// 初始化函数
function init() {

    echarts.registerTheme('myTheme', {
        tooltip: {
            // 文字样式（白色）
            textStyle: {
                color: '#fff',
                fontSize: 14
            },
            // 背景样式（透明黑）
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);' // 阴影效果
        }
    });


    // 加载宠物类型
    loadPetType()
    // 加载产品类别销售数据
    loadProductCategorySales()
    // 加载热销商品排行
    loadTopSellingProducts(); 
    // 加载订单地区分布
    loadOrderMap()
    // 加载平台概览数据
    loadPlatformOverview();

    echart_map();


    // // 设置自动刷新
    // setInterval(function () {
    //     loadPlatformOverview();
    //     loadOrderMap();
    //     loadPetType();
    //     loadProductCategorySales();
    // }, 60000); // 每分钟刷新一次
}

//
// // 加载产品类别销售数据
// function loadProductCategorySales() {
//     $.ajax({
//         url: '/api/dataVisual/productCategorySales',
//         type: 'GET',
//         success: function (res) {
//             if (res.code === 200) {
//                 const data = res.data;
//
//                 // 初始化销售数据
//                 let catFoodSales = 0;
//                 let dogFoodSales = 0;
//                 let petToySales = 0;
//                 let petCareSales = 0;
//                 let highestSales = 0;
//                 let totalProducts = 0;
//
//                 // 处理数据
//                 data.forEach(item => {
//                     totalProducts += item.productCount || 0;
//
//                     if (item.category === '猫粮') {
//                         catFoodSales = item.sales;
//                     } else if (item.category === '狗粮') {
//                         dogFoodSales = item.sales;
//                     } else if (item.category === '宠物玩具') {
//                         petToySales = item.sales;
//                     } else if (item.category === '宠物护理') {
//                         petCareSales = item.sales;
//                     }
//
//                     if (item.sales > highestSales) {
//                         highestSales = item.sales;
//                     }
//                 });
//
//                 // 更新数据展示
//                 $('#catFoodSales').text(catFoodSales);
//                 $('#dogFoodSales').text(dogFoodSales);
//                 $('#petToySales').text(petToySales);
//                 $('#petCareSales').text(petCareSales);
//                 $('#highestSales').text(highestSales);
//                 $('#totalProducts').text(totalProducts);
//                 $('#salesGrowth').text('12.5%'); // 假设数据
//                 $('#stockWarning').text('5'); // 假设数据
//             }
//         }
//     });
// }




// 渲染用户活跃度图表
function renderUserActivityChart(data) {
    const chart = echarts.init(document.getElementById('userActivityChart'));

    const option = {
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            right: 10,
            top: 'center',
            textStyle: {
                color: 'rgba(255, 255, 255, 0.7)'
            }
        },
        series: [
            {
                type: 'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#0e1c47',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: '14',
                        fontWeight: 'bold',
                        color: '#fff'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    { value: 735, name: '活跃用户', itemStyle: { color: '#00ffff' } },
                    { value: 580, name: '一般用户', itemStyle: { color: '#00cfff' } },
                    { value: 300, name: '沉睡用户', itemStyle: { color: '#006ced' } }
                ]
            }
        ]
    };

    chart.setOption(option);
}

// 页面加载完成后初始化
$(document).ready(function () {
    init();

    // 设置时间显示
    setInterval(function () {
        const now = new Date();
        const timeStr = now.getFullYear() + '-' +
            String(now.getMonth() + 1).padStart(2, '0') + '-' +
            String(now.getDate()).padStart(2, '0') + ' ' +
            String(now.getHours()).padStart(2, '0') + ':' +
            String(now.getMinutes()).padStart(2, '0') + ':' +
            String(now.getSeconds()).padStart(2, '0');
        $('#currentTime').text(timeStr);
    }, 1000);
});