// 智慧物流控制中心主要功能 - 主入口文件
let logisticsMap;

// 初始化函数
function init() {
    // 注册ECharts主题
    echarts.registerTheme('logisticsTheme', {
        color: ['#00d4ff', '#00ff88', '#ffaa00', '#ff4444', '#8888ff', '#ff8888'],
        backgroundColor: 'transparent',
        tooltip: {
            textStyle: {
                color: '#fff',
                fontSize: 14
            },
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            borderColor: '#00d4ff',
            borderWidth: 1,
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);'
        },
        legend: {
            textStyle: {
                color: '#88ccff'
            }
        },
        grid: {
            borderColor: 'rgba(0, 150, 255, 0.1)'
        }
    });

    // 加载所有模块数据
    loadOverviewData();
    loadVehicleStatus();
    loadWarehouseData();
    initLogisticsMap();
    loadCityAnalysis();
    loadEfficiencyData();
    loadRegionAnalysis();
    loadTransportModes();
    loadTrendsData();

    // 初始化时间显示
    updateTime();
    setInterval(updateTime, 1000);

    // 设置自动刷新
    setInterval(function () {
        loadOverviewData();
        updateMapData();
    }, 30000); // 每30秒刷新一次
}

// 页面加载完成后初始化
$(document).ready(function() {
    init();

    // 添加刷新按钮功能
    $('.refresh').click(function() {
        location.reload();
    });
});
