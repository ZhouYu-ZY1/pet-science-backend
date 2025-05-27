// 3D地图实现
document.addEventListener('DOMContentLoaded', function() {
    // 创建场景
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0x0a2a5e);

    // 获取容器元素
    const container = document.getElementById('3d-map-container');
    if (!container) return;

    // 获取容器尺寸
    const containerWidth = container.clientWidth;
    const containerHeight = container.clientHeight;

    // 创建相机
    const camera = new THREE.PerspectiveCamera(75, containerWidth / containerHeight, 0.1, 1000);
    camera.position.set(0, 8, 15);

    // 创建渲染器
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setSize(containerWidth, containerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);

    // 将渲染器添加到DOM
    container.appendChild(renderer.domElement);

    // 添加轨道控制器
    const controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;

    // 添加光源
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(5, 10, 7);
    scene.add(directionalLight);

    // 全局变量
    let mapGroup = new THREE.Group();
    let flyLineGroup = new THREE.Group();
    let scatterGroup = new THREE.Group();
    let animationMixers = [];
    
    // 地理坐标数据
    const chinaGeoCoordMap = {
        '黑龙江': [127.9688, 45.368],
        '内蒙古': [110.3467, 41.4899],
        '吉林': [125.8154, 44.2584],
        '北京市': [116.4551, 40.2539],
        '辽宁': [123.1238, 42.1216],
        '河北': [114.4995, 38.1006],
        '天津': [117.4219, 39.4189],
        '山西': [112.3352, 37.9413],
        '陕西': [109.1162, 34.2004],
        '甘肃': [103.5901, 36.3043],
        '宁夏': [106.3586, 38.1775],
        '青海': [101.4038, 36.8207],
        '新疆': [87.9236, 43.5883],
        '西藏': [91.11, 29.97],
        '四川': [103.9526, 30.7617],
        '重庆': [108.384366, 30.439702],
        '山东': [117.1582, 36.8701],
        '河南': [113.4668, 34.6234],
        '江苏': [118.8062, 31.9208],
        '安徽': [117.29, 32.0581],
        '湖北': [114.3896, 30.6628],
        '浙江': [119.5313, 29.8773],
        '福建': [119.4543, 25.9222],
        '江西': [116.0046, 28.6633],
        '湖南': [113.0823, 28.2568],
        '贵州': [106.6992, 26.7682],
        '云南': [102.9199, 25.4663],
        '广东': [113.12244, 23.009505],
        '广西': [108.479, 23.1152],
        '海南': [110.3893, 19.8516],
        '上海': [121.4648, 31.2891]
    };

    const local = "四川";
    const center = [104.0, 37.5];
    const scale = 0.05;

    // 坐标转换函数
    function geoToVector3(coord) {
        const x = (coord[0] - center[0]) * scale;
        const z = -(coord[1] - center[1]) * scale;
        return new THREE.Vector3(x, 0, z);
    }

    // 创建中国地图的3D模型
    createChinaMap();

    // 加载数据并创建飞线
    loadOrderData();

    // 添加动画效果
    animate();

    // 创建中国地图的3D模型
    function createChinaMap() {
        fetch('./data/china.json')
            .then(response => response.json())
            .then(geoData => {
                createMapFromGeoJSON(geoData);
            })
            .catch(error => {
                console.error('加载地图数据失败:', error);
                createFallbackMap();
            });
    }

    // 从GeoJSON创建地图
    function createMapFromGeoJSON(geoData) {
        // 地图中心和缩放比例
        const mapCenter = [104.0, 37.5];
        const mapScale = 20;
        
        // 处理每个省份
        geoData.features.forEach(feature => {
            const province = feature.properties.name;
            const coordinates = feature.geometry.coordinates;
            
            // 检查几何类型
            if (feature.geometry.type === 'MultiPolygon') {
                coordinates.forEach(polygon => {
                    try {
                        createPolygon(polygon, province, mapGroup, mapCenter, mapScale);
                    } catch (e) {
                        console.error(`处理省份 ${province} 时出错:`, e);
                    }
                });
            } else if (feature.geometry.type === 'Polygon') {
                try {
                    createPolygon(coordinates, province, mapGroup, mapCenter, mapScale);
                } catch (e) {
                    console.error(`处理省份 ${province} 时出错:`, e);
                }
            }
        });
        
        // 旋转地图使其水平放置
        mapGroup.rotation.x = -Math.PI / 2;
        scene.add(mapGroup);
    }

    // 创建多边形
    function createPolygon(polygon, province, group, center, scale) {
        if (!polygon || !polygon[0] || polygon[0].length < 3) {
            console.warn(`省份 ${province} 的多边形点数不足`);
            return;
        }
        
        const shape = new THREE.Shape();
        const points = [];
        let hasValidPoints = false;
        
        polygon[0].forEach(coord => {
            if (Array.isArray(coord) && coord.length >= 2 && 
                !isNaN(coord[0]) && !isNaN(coord[1])) {
                const x = (coord[0] - center[0]) / scale;
                const y = (coord[1] - center[1]) / scale;
                if (!isNaN(x) && !isNaN(y) && isFinite(x) && isFinite(y)) {
                    points.push(new THREE.Vector2(x, y));
                    hasValidPoints = true;
                }
            }
        });
        
        if (!hasValidPoints || points.length < 3) {
            console.warn(`省份 ${province} 没有足够的有效点`);
            return;
        }
        
        shape.setFromPoints(points);
        
        try {
            const geometry = new THREE.ExtrudeGeometry(shape, {
                depth: 0.1,
                bevelEnabled: true,
                bevelThickness: 0.01,
                bevelSize: 0.01,
                bevelSegments: 1
            });
            
            if (geometry.attributes.position.count === 0) {
                console.warn(`省份 ${province} 的几何体无效`);
                return;
            }
            
            const material = new THREE.MeshPhongMaterial({
                color: 0x00d7fa,
                transparent: true,
                opacity: 0.8,
                side: THREE.DoubleSide
            });
            
            const mesh = new THREE.Mesh(geometry, material);
            mesh.userData = { province: province };
            group.add(mesh);
            
        } catch (error) {
            console.error(`创建省份 ${province} 的几何体时出错:`, error);
        }
    }

    // 创建备用地图
    function createFallbackMap() {
        const geometry = new THREE.PlaneGeometry(10, 8);
        const material = new THREE.MeshPhongMaterial({
            color: 0x0099ff,
            transparent: true,
            opacity: 0.8,
            side: THREE.DoubleSide
        });
        const plane = new THREE.Mesh(geometry, material);
        plane.rotation.x = -Math.PI / 2;
        scene.add(plane);
    }

    // 加载订单数据
    function loadOrderData() {
        $.ajax({
            url: '/api/dataVisual/orderRegionDistribution',
            type: 'GET',
            dataType: 'json',
            success: function(result) {
                if (result.code === 200) {
                    createFlyLines(result.data);
                    createScatterPoints(result.data);
                } else {
                    console.error('获取订单地区分布数据失败:', result.msg);
                    // 使用模拟数据
                    const mockData = [
                        {name: '四川', value: 2076},
                        {name: '北京', value: 1800},
                        {name: '上海', value: 1700},
                        {name: '广东', value: 1500},
                        {name: '江苏', value: 1400}
                    ];
                    createFlyLines(mockData);
                    createScatterPoints(mockData);
                }
            },
            error: function(xhr, status, error) {
                console.error('请求订单地区分布数据失败:', error);
                // 使用模拟数据
                const mockData = [
                    {name: '四川', value: 2076},
                    {name: '北京', value: 1800},
                    {name: '上海', value: 1700},
                    {name: '广东', value: 1500},
                    {name: '江苏', value: 1400}
                ];
                createFlyLines(mockData);
                createScatterPoints(mockData);
            }
        });
    }

    // 创建飞线
    function createFlyLines(data) {
        const localCoord = chinaGeoCoordMap[local];
        if (!localCoord) return;

        const fromPos = geoToVector3(localCoord);
        fromPos.y = 0.2;

        data.forEach(item => {
            if (item.name !== local && chinaGeoCoordMap[item.name]) {
                const toCoord = chinaGeoCoordMap[item.name];
                const toPos = geoToVector3(toCoord);
                toPos.y = 0.2;

                createFlyLine(fromPos, toPos, item.value);
            }
        });

        flyLineGroup.rotation.x = -Math.PI / 2;
        scene.add(flyLineGroup);
    }

    // 创建单条飞线
    function createFlyLine(fromPos, toPos, value) {
        const distance = fromPos.distanceTo(toPos);
        const midPoint = new THREE.Vector3().addVectors(fromPos, toPos).multiplyScalar(0.5);
        midPoint.y += distance * 0.3;

        const curve = new THREE.QuadraticBezierCurve3(fromPos, midPoint, toPos);
        const points = curve.getPoints(50);
        const geometry = new THREE.BufferGeometry().setFromPoints(points);

        // 创建飞线材质
        const material = new THREE.LineBasicMaterial({
            color: 0xffde00,
            transparent: true,
            opacity: 0.6
        });

        const line = new THREE.Line(geometry, material);
        flyLineGroup.add(line);

        // 创建移动的光点
        createMovingLight(curve, value);
    }

    // 创建移动的光点
    function createMovingLight(curve, value) {
        const geometry = new THREE.SphereGeometry(0.02, 8, 8);
        const material = new THREE.MeshBasicMaterial({
            color: 0xffde00,
            transparent: true,
            opacity: 0.8
        });

        const light = new THREE.Mesh(geometry, material);
        flyLineGroup.add(light);

        // 创建动画
        const duration = 2000 + Math.random() * 1000;
        let startTime = Date.now();

        function animateLight() {
            const elapsed = Date.now() - startTime;
            const progress = (elapsed % duration) / duration;
            
            const position = curve.getPoint(progress);
            light.position.copy(position);

            // 添加脉冲效果
            const pulse = 1 + 0.3 * Math.sin(elapsed * 0.01);
            light.scale.setScalar(pulse);

            requestAnimationFrame(animateLight);
        }

        animateLight();
    }

    // 创建散点
    function createScatterPoints(data) {
        data.forEach(item => {
            if (chinaGeoCoordMap[item.name]) {
                const coord = chinaGeoCoordMap[item.name];
                const pos = geoToVector3(coord);
                pos.y = 0.1;

                createScatterPoint(pos, item.value, item.name);
            }
        });

        scatterGroup.rotation.x = -Math.PI / 2;
        scene.add(scatterGroup);
    }

    // 创建单个散点
    function createScatterPoint(position, value, name) {
        const size = Math.max(0.05, value / 1000 * 0.2);
        const geometry = new THREE.SphereGeometry(size, 16, 16);
        
        const material = new THREE.MeshBasicMaterial({
            color: name === local ? 0xff4444 : 0x00ffff,
            transparent: true,
            opacity: 0.8
        });

        const sphere = new THREE.Mesh(geometry, material);
        sphere.position.copy(position);
        sphere.userData = { name: name, value: value };

        // 添加脉冲动画
        let time = 0;
        function animatePulse() {
            time += 0.02;
            const scale = 1 + 0.2 * Math.sin(time);
            sphere.scale.setScalar(scale);
            requestAnimationFrame(animatePulse);
        }
        animatePulse();

        scatterGroup.add(sphere);

        // 如果是中心点，添加特殊标记
        if (name === local) {
            createCenterMarker(position);
        }
    }

    // 创建中心标记
    function createCenterMarker(position) {
        const geometry = new THREE.ConeGeometry(0.05, 0.2, 8);
        const material = new THREE.MeshBasicMaterial({
            color: 0xff4444,
            transparent: true,
            opacity: 0.9
        });

        const cone = new THREE.Mesh(geometry, material);
        cone.position.copy(position);
        cone.position.y += 0.15;
        
        // 添加旋转动画
        function animateRotation() {
            cone.rotation.y += 0.02;
            requestAnimationFrame(animateRotation);
        }
        animateRotation();

        scatterGroup.add(cone);
    }

    // 动画循环
    function animate() {
        requestAnimationFrame(animate);

        // 更新控制器
        controls.update();

        // 更新动画混合器
        animationMixers.forEach(mixer => {
            mixer.update(0.016);
        });

        // 渲染场景
        renderer.render(scene, camera);
    }

    // 响应窗口大小变化
    window.addEventListener('resize', function() {
        const newWidth = container.clientWidth;
        const newHeight = container.clientHeight;

        camera.aspect = newWidth / newHeight;
        camera.updateProjectionMatrix();

        renderer.setSize(newWidth, newHeight);
    });
});

// 导出函数供外部调用
function init3DMap() {
    // 初始化3D地图的入口函数
    console.log('3D地图初始化完成');
}