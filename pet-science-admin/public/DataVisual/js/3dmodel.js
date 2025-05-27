// 3D模型实现
document.addEventListener('DOMContentLoaded', function() {
    // loadModel('3d-model1-container', // 容器ID
    //     [0,1,5], // 相机位置
    //     1.5, // 光照强度
    //     'dog.glb', // 模型名称
    //     [2,2,2], // 模型大小
    //     -1.5, // 模型位置
    //     true, // 是否自动旋转
    // );
    // 定义模型配置数组
    const modelConfigs = [
        {
            containerId: '3d-model1-container',  
            cameraPosition: [0,1,5],
            light: 1.5,
            modelName: 'dog.glb',
            modelSize: [2,2,2],
            modelPosition: -1.5,
            autoRotate: true
        },
        {
            containerId: '3d-model2-container',
            cameraPosition: [3,2,5],
            light: 1,
            modelName: 'cat.glb',
            modelSize: [9,9,9],
            modelPosition: -1.5,
            autoRotate: false
        },
        {
            containerId: '3d-model3-container',
            cameraPosition: [-4,2,5],
            light: 1.5,
            modelName: 'parrot.glb',
            modelSize: [13,13,13],
            modelPosition: -1.6,
            autoRotate: false
        },
        {
            containerId: '3d-model4-container',
            cameraPosition: [0,1,5],
            light: 1.5,
            modelName: 'dog2.glb',
            modelSize: [0.6,0.6,0.6],
            modelPosition: 0.5,
            autoRotate: true
        }
    ];
    
    // 设置总模型数量
    totalModels = modelConfigs.length;
    
    // 顺序加载模型
    loadModelsSequentially(modelConfigs, 0);
});

let totalModels = 0;
let loadedModels = 0;

// 顺序加载模型的函数
function loadModelsSequentially(modelConfigs, index) {
    if (index >= modelConfigs.length) {
        console.log('所有模型加载完成');
        return;
    }
    
    const config = modelConfigs[index];
    loadModel(
        config.containerId,
        config.cameraPosition,
        config.light,
        config.modelName,
        config.modelSize,
        config.modelPosition,
        config.autoRotate,
        function() {
            // 当前模型加载完成后，加载下一个模型
            loadModelsSequentially(modelConfigs, index + 1);
        }
    );
}

function loadModel(containerId, cameraPosition, light, modelName, modelSize, modelPosition, autoRotate, onComplete) {
    const loadingElem = document.getElementById("model-load-text");
    loadingElem.textContent = `正在加载3D模型：${loadedModels}/${totalModels}`;

    // 创建场景
    const scene = new THREE.Scene();
    // 移除背景色，使场景背景透明
    scene.background = null;

    // 获取容器元素
    const container = document.getElementById(containerId);
    if (!container) {
        if (onComplete) onComplete();
        return;
    }

    // 获取容器尺寸
    const containerWidth = container.clientWidth;
    const containerHeight = container.clientHeight;

    // 创建相机
    const camera = new THREE.PerspectiveCamera(75, containerWidth / containerHeight, 0.1, 1000);
    // 相机位置
    camera.position.set(cameraPosition[0],cameraPosition[1],cameraPosition[2]);
    // 设置相机朝向场景中心
    camera.lookAt(0, 0, 0);

    // 创建渲染器，设置为透明
    const renderer = new THREE.WebGLRenderer({ 
        antialias: true, 
        alpha: true 
    });
    renderer.setSize(containerWidth, containerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    // 设置渲染器的透明度
    renderer.setClearColor(0x000000, 0);

    // 将渲染器添加到DOM
    container.appendChild(renderer.domElement);

    // 添加轨道控制器
    const controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controls.minDistance = 3;
    controls.maxDistance = 10;

    // 自动旋转设置
    controls.autoRotate = autoRotate;
    controls.autoRotateSpeed = -4.0;

    // 添加光源
    const ambientLight = new THREE.AmbientLight(0xffffff, light);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(5, 10, 7);
    directionalLight.castShadow = true;
    directionalLight.shadow.mapSize.width = 1024;
    directionalLight.shadow.mapSize.height = 1024;
    scene.add(directionalLight);

    // 全局变量
    let dogModel = null;
    let mixer = null;
    let animations = [];
    let currentAnimation = null;
    let clock = new THREE.Clock();

    // 旋转状态控制
    let isAutoRotating = true;

    // 加载3D狗模型
    loadDogModel();

    // 添加动画效果
    animate();

    // 加载3D狗模型
    function loadDogModel() {
        const loader = new THREE.GLTFLoader();
        
        // 使用GLTFLoader加载模型
        loader.setPath('./models/');
        loader.load(modelName, function(gltf) {
            dogModel = gltf.scene;
            
            // 调整模型大小和位置 
            dogModel.scale.set(modelSize[0], modelSize[1], modelSize[2]);
            dogModel.position.y = modelPosition; 
            
            // 确保模型可以投射和接收阴影
            dogModel.traverse(function(node) {
                if (node.isMesh) {
                    node.castShadow = true;
                    node.receiveShadow = true;
                }
            });
            
            scene.add(dogModel);
            
            // 处理动画
            if (gltf.animations && gltf.animations.length) {
                mixer = new THREE.AnimationMixer(dogModel);
                animations = gltf.animations;
                
                // 播放第一个动画
                playAnimation(0);
            }
            
            // 模型加载完成，更新计数并调用完成回调
            loadedModels += 1;
            loadingElem.textContent = `正在加载3D模型：${loadedModels}/${totalModels}`;
            if(loadedModels >= totalModels){
                loadingElem.style.display = 'none';
            }
            
            // 调用完成回调，触发下一个模型的加载
            if (onComplete) onComplete();
            
        }, function(xhr) {
            // 加载进度
            // const percent = Math.round(xhr.loaded / xhr.total * 100);
        }, function(error) {
            console.error('加载模型失败:', error);
            
            // 即使加载失败也调用完成回调，确保下一个模型能够加载
            loadedModels += 1;
            loadingElem.textContent = `正在加载3D模型：${loadedModels}/${totalModels}`;
            if(loadedModels >= totalModels){
                loadingElem.style.display = 'none';
            }
            
            if (onComplete) onComplete();
        });
    }

    // 播放指定索引的动画
    function playAnimation(index) {
        if (!mixer || !animations || index >= animations.length) return;
        
        // 停止当前动画
        if (currentAnimation) {
            currentAnimation.stop();
        }
        
        // 播放新动画
        currentAnimation = mixer.clipAction(animations[index]);
        currentAnimation.play();
    }

    // 切换动画
    function switchAnimation() {
        if (!animations || animations.length <= 1) return;
        
        const currentIndex = animations.indexOf(currentAnimation._clip);
        const nextIndex = (currentIndex + 1) % animations.length;
        
        playAnimation(nextIndex);
    }

    // 动画循环
    function animate() {
        requestAnimationFrame(animate);

        // 更新控制器
        controls.update();

        // 更新动画混合器
        if (mixer) {
            mixer.update(clock.getDelta());
        }

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

    // 添加双击事件切换动画
    container.addEventListener('dblclick', function() {
        // 双击时切换动画
        switchAnimation();

        // 双击时切换自动旋转状态
        isAutoRotating = !isAutoRotating;
        if(autoRotate){
           controls.autoRotate = isAutoRotating;
        }
    });
}

// 导出函数供外部调用
function init3D() {
    console.log('3D模型初始化完成');
}