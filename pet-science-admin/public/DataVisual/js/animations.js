/**
 * 数据大屏动画效果管理器
 * 包含数字滚动动画、图表加载动画等效果
 */

class AnimationManager {
    constructor() {
        this.isAnimating = false;
        this.numberAnimations = new Map();
    }

    /**
     * 数字滚动动画
     * @param {string} elementId - 元素ID
     * @param {number} targetValue - 目标数值
     * @param {number} duration - 动画持续时间(毫秒)
     * @param {string} suffix - 数字后缀(如: 万, %, 等)
     */
    animateNumber(elementId, targetValue, duration = 2000, suffix = '') {
        const element = document.getElementById(elementId);
        if (!element) return;

        // 如果该元素正在动画中，先停止之前的动画
        if (this.numberAnimations.has(elementId)) {
            cancelAnimationFrame(this.numberAnimations.get(elementId));
        }

        element.classList.add('animating');
        
        const startValue = 0;
        const startTime = Date.now();
        const valueRange = targetValue - startValue;

        const updateNumber = () => {
            const elapsed = Date.now() - startTime;
            const progress = Math.min(elapsed / duration, 1);
            
            // 使用缓动函数
            const easeOutQuart = 1 - Math.pow(1 - progress, 4);
            const currentValue = Math.floor(startValue + valueRange * easeOutQuart);
            
            // 格式化数字显示
            element.textContent = this.formatNumber(currentValue) + suffix;
            
            if (progress < 1) {
                const animationId = requestAnimationFrame(updateNumber);
                this.numberAnimations.set(elementId, animationId);
            } else {
                element.classList.remove('animating');
                this.numberAnimations.delete(elementId);
                element.textContent = this.formatNumber(targetValue) + suffix;
            }
        };

        updateNumber();
    }

    /**
     * 批量启动数字动画
     * @param {Array} animations - 动画配置数组
     */
    startNumberAnimations(animations) {
        animations.forEach((config, index) => {
            setTimeout(() => {
                this.animateNumber(config.elementId, config.targetValue, config.duration, config.suffix);
            }, index * 200); // 错开启动时间
        });
    }

    /**
     * 格式化数字显示
     * @param {number} num - 数字
     * @returns {string} 格式化后的字符串
     */
    formatNumber(num) {
        if (num >= 10000) {
            return (num / 10000).toFixed(1) + '万';
        } else if (num >= 1000) {
            return (num / 1000).toFixed(1) + 'k';
        }
        return num.toString();
    }

    /**
     * 3D模型加载动画
     * @param {string} containerId - 模型容器ID
     * @param {Function} onProgress - 进度回调
     * @param {Function} onComplete - 完成回调
     */
    show3DModelLoading(containerId, onProgress, onComplete) {
        const container = document.getElementById(containerId);
        if (!container) return;

        // 创建加载指示器
        const loadingDiv = document.createElement('div');
        loadingDiv.className = 'model-loading';
        loadingDiv.innerHTML = `
            <div class="model-loading-spinner"></div>
            <div class="model-loading-text">正在加载3D模型...</div>
            <div class="model-loading-progress">
                <div class="model-loading-progress-bar"></div>
            </div>
        `;
        
        container.appendChild(loadingDiv);
        
        // 模拟加载进度
        const progressBar = loadingDiv.querySelector('.model-loading-progress-bar');
        const progressText = loadingDiv.querySelector('.model-loading-text');
        let progress = 0;
        
        const updateProgress = () => {
            progress += Math.random() * 8 + 2;
            if (progress > 100) progress = 100;
            
            progressBar.style.width = progress + '%';
            progressText.textContent = `正在加载3D模型... ${Math.floor(progress)}%`;
            
            if (onProgress) onProgress(progress);
            
            if (progress < 100) {
                setTimeout(updateProgress, 50 + Math.random() * 100);
            } else {
                setTimeout(() => {
                    loadingDiv.remove();
                    if (onComplete) onComplete();
                }, 500);
            }
        };
        
        updateProgress();
        
        return loadingDiv;
    }
}

// 创建全局动画管理器实例
const animationManager = new AnimationManager();

// 页面加载完成后启动数据动画
document.addEventListener('DOMContentLoaded', function() {
    // 延迟启动数字动画，让页面先渲染完成
    setTimeout(() => {
        // 启动数据概览动画
        animationManager.startNumberAnimations([
            { elementId: 'totalPets', targetValue: 1250, duration: 2000, suffix: '' },
            { elementId: 'catCount', targetValue: 680, duration: 2200, suffix: '' },
            { elementId: 'dogCount', targetValue: 520, duration: 2400, suffix: '' },
            { elementId: 'otherCount', targetValue: 50, duration: 2600, suffix: '' },
            { elementId: 'registeredUsers', targetValue: 8520, duration: 2000, suffix: '' },
            { elementId: 'annualTransactionValue', targetValue: 256, duration: 2200, suffix: '万' },
            { elementId: 'totalOrders', targetValue: 15680, duration: 2400, suffix: '' },
            { elementId: 'activeDailyUsers', targetValue: 1280, duration: 2600, suffix: '' },
            { elementId: 'activeWeeklyUsers', targetValue: 3560, duration: 2800, suffix: '' }
        ]);
    }, 1000);
});