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
    camera.position.set(0, 5, 10);

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
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(5, 10, 7);
    scene.add(directionalLight);

    // 创建中国地图的3D模型
    createChinaMap();

    // 添加动画效果
    animate();

    // 创建中国地图的3D模型
    function createChinaMap() {
        // 加载中国地图GeoJSON数据
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
        const group = new THREE.Group();
    
        // 地图中心和缩放比例
        const center = [104.0, 37.5];
        const scale = 20;
    
        // 处理每个省份
        geoData.features.forEach(feature => {
            const province = feature.properties.name;
            const coordinates = feature.geometry.coordinates;
            
            // 检查几何类型
            if (feature.geometry.type === 'MultiPolygon') {
                // 处理多边形
                coordinates.forEach(polygon => {
                    try {
                        createPolygon(polygon, province, group, center, scale);
                    } catch (e) {
                        console.error(`处理省份 ${province} 时出错:`, e);
                    }
                });
            } else if (feature.geometry.type === 'Polygon') {
                try {
                    createPolygon(coordinates, province, group, center, scale);
                } catch (e) {
                    console.error(`处理省份 ${province} 时出错:`, e);
                }
            }
        });
    
        // 旋转地图使其水平放置
        group.rotation.x = -Math.PI / 2;
        scene.add(group);
    }

    // 创建备用地图（当GeoJSON加载失败时）
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
    // 更新标签位置
    function updateLabels() {
        // 获取所有点
        const points = scene.children.filter(child =>
            child.type === 'Mesh' &&
            child.geometry.type === 'SphereGeometry' &&
            child.userData &&
            child.userData.label
        );

        points.forEach(point => {
            const label = point.userData.label;
            const vector = new THREE.Vector3();
            vector.setFromMatrixPosition(point.matrixWorld);
            vector.project(camera);

            const x = (vector.x * 0.5 + 0.5) * containerWidth;
            const y = (-vector.y * 0.5 + 0.5) * containerHeight;

            label.style.left = x + 'px';
            label.style.top = y + 'px';

            // 如果点在相机后面或超出视野，隐藏标签
            if (vector.z > 1 || x < 0 || x > containerWidth || y < 0 || y > containerHeight) {
                label.style.display = 'none';
            } else {
                label.style.display = 'block';
            }
        });
    }

    // 动画循环
    function animate() {
        requestAnimationFrame(animate);

        // 更新控制器
        controls.update();

        // 更新标签位置
        updateLabels();

        // 渲染场景
        renderer.render(scene, camera);
    }

    // 响应窗口大小变化
    window.addEventListener('resize', function() {
        // 获取最新的容器尺寸
        const newWidth = container.clientWidth;
        const newHeight = container.clientHeight;

        // 更新相机
        camera.aspect = newWidth / newHeight;
        camera.updateProjectionMatrix();

        // 更新渲染器
        renderer.setSize(newWidth, newHeight);
    });
});


// 从GeoJSON创建地图
function createMapFromGeoJSON(geoData) {
    const group = new THREE.Group();
    
    // 地图中心和缩放比例
    const center = [104.0, 37.5];
    const scale = 20;
    
    // 处理每个省份
    geoData.features.forEach(feature => {
        const province = feature.properties.name;
        const coordinates = feature.geometry.coordinates;
        
        // 检查几何类型
        if (feature.geometry.type === 'MultiPolygon') {
            // 处理多边形
            coordinates.forEach(polygon => {
                try {
                    createPolygon(polygon, province, group, center, scale);
                } catch (e) {
                    console.error(`处理省份 ${province} 时出错:`, e);
                }
            });
        } else if (feature.geometry.type === 'Polygon') {
            try {
                createPolygon(coordinates, province, group, center, scale);
            } catch (e) {
                console.error(`处理省份 ${province} 时出错:`, e);
            }
        }
    });
    
    // 旋转地图使其水平放置
    group.rotation.x = -Math.PI / 2;
    scene.add(group);
    
    // 添加地图上的点
    addMapPoints(geoData);
}

// 创建多边形
function createPolygon(polygon, province, group, center, scale) {
    // 确保多边形有足够的点
    if (!polygon || !polygon[0] || polygon[0].length < 3) {
        console.warn(`省份 ${province} 的多边形点数不足`);
        return;
    }
    
    const shape = new THREE.Shape();
    
    // 第一个多边形作为外轮廓
    const points = [];
    let hasValidPoints = false;
    
    // 过滤无效坐标
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
    
    // 如果没有有效点，则跳过
    if (!hasValidPoints || points.length < 3) {
        console.warn(`省份 ${province} 没有足够的有效点`);
        return;
    }
    
    // 创建形状
    shape.setFromPoints(points);
    
    // 处理内部孔洞
    for (let i = 1; i < polygon.length; i++) {
        if (polygon[i] && polygon[i].length >= 3) {
            const holePoints = [];
            let hasValidHolePoints = false;
            
            // 过滤无效坐标
            polygon[i].forEach(coord => {
                if (Array.isArray(coord) && coord.length >= 2 && 
                    !isNaN(coord[0]) && !isNaN(coord[1])) {
                    const x = (coord[0] - center[0]) / scale;
                    const y = (coord[1] - center[1]) / scale;
                    if (!isNaN(x) && !isNaN(y) && isFinite(x) && isFinite(y)) {
                        holePoints.push(new THREE.Vector2(x, y));
                        hasValidHolePoints = true;
                    }
                }
            });
            
            // 如果孔洞有足够的有效点，则添加
            if (hasValidHolePoints && holePoints.length >= 3) {
                const hole = new THREE.Path();
                hole.setFromPoints(holePoints);
                shape.holes.push(hole);
            }
        }
    }
    
    try {
        // 创建几何体
        const geometry = new THREE.ExtrudeGeometry(shape, {
            depth: 0.1,
            bevelEnabled: true,
            bevelThickness: 0.01,
            bevelSize: 0.01,
            bevelSegments: 1
        });
        
        // 验证几何体是否有效
        if (geometry.attributes.position.count === 0) {
            console.warn(`省份 ${province} 生成的几何体没有顶点`);
            return;
        }
        
        // 检查几何体中是否有NaN值
        const positions = geometry.attributes.position.array;
        for (let i = 0; i < positions.length; i++) {
            if (isNaN(positions[i])) {
                positions[i] = 0; // 将NaN值替换为0
            }
        }
        
        // 更新几何体
        geometry.attributes.position.needsUpdate = true;
        geometry.computeBoundingSphere();
        
        // 创建材质
        const material = new THREE.MeshPhongMaterial({
            color: 0x0099ff,
            transparent: true,
            opacity: 0.8,
            side: THREE.DoubleSide
        });
        
        // 创建网格
        const mesh = new THREE.Mesh(geometry, material);
        mesh.userData = { province: province };
        
        // 添加边框
        const edges = new THREE.EdgesGeometry(geometry);
        const line = new THREE.LineSegments(
            edges,
            new THREE.LineBasicMaterial({ color: 0x00ffff, linewidth: 1 })
        );
        
        group.add(mesh);
        group.add(line);
    } catch (e) {
        console.error(`创建省份 ${province} 的几何体时出错:`, e);
    }
}