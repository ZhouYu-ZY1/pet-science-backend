/**
 * 中间顶部 中国地图订单分布
 */
function loadOrderMap() {
    $.ajax({
        url: '/api/dataVisual/orderRegionDistribution',
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data;
                let myChart = echarts.init(document.getElementById('main4_china_map'));

                let option = {
                    title: {
                        text: '用户地区分布情况',
                        top: 80,
                        left: 0,
                        textStyle: {
                            color: '#0e94eb'
                        }
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: function(params) {
                            return params.name + '<br/>销量: ' + (params.value || 0);
                        },
                        backgroundColor: 'rgba(255,255,255,0.8)',
                        textStyle: {
                            color: '#333'
                        }
                    },
                    visualMap: {
                        min: 0,
                        max: Math.max(...data.map(item => item.value), 100),
                        left: 'right',
                        top: 'center',
                        text: ['高', '低'],
                        calculable: true,
                        inRange: {
                            color: ['#199af0', '#1280d1', '#0c67b3', '#064e94']
                        },
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    series: [
                        {
                            type: 'map',
                            mapType: 'china',
                            roam: false,
                            zoom: 1,
                            center: [104, 36],
                            aspectScale: 0.75,
                            label: {
                                top: 0,
                                show: true,
                                color: '#fff',
                                formatter: function(params) {
                                    return params.name;
                                },
                                fontSize: 11
                            },
                            itemStyle: {
                                areaColor: '#199af0',
                                borderColor: '#fff',
                                borderWidth: 0.5
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    color: '#fff',
                                    fontSize: 12,
                                    fontWeight: 'bold'
                                },
                                itemStyle: {
                                    areaColor: '#013a70'
                                }
                            },
                            data: data,
                            selectedMode: false
                        }
                    ]
                };
                
                myChart.setOption(option);
                
                // 添加地图区域自动高亮动画
                mapAnimation(myChart, data);
                
                window.addEventListener('resize', function() {
                    myChart.resize();
                });
            } else {
                console.error('获取订单地区分布数据失败:', result.msg);
            }
        },
        error: function(xhr, status, error) {
            console.error('请求订单地区分布数据失败:', error);
            // // 如果API请求失败，使用模拟数据进行展示
            // let mockData = [
            //     {name: '四川', value: 2076},
            //     {name: '北京', value: 1800},
            //     {name: '上海', value: 1700},
            //     {name: '广东', value: 1500},
            //     {name: '江苏', value: 1400},
            //     {name: '浙江', value: 1300},
            //     {name: '山东', value: 1200},
            //     {name: '河南', value: 1100},
            //     {name: '湖北', value: 1000}
            // ];
        }
    });
}

/**
 * 地图区域自动高亮动画
 * @param {echarts Dom 对象} myChart
 * @param {地图数据} data
 */
function mapAnimation(myChart, data) {
    // 过滤掉没有值的数据
    let validData = data.filter(item => item.value && item.value > 0);
    
    // 如果没有有效数据，则不执行动画
    if (validData.length === 0) return;
    
    // 按销量排序，优先显示销量高的地区
    validData.sort((a, b) => b.value - a.value);
    
    let currentIndex = -1;
    let animationInterval = setInterval(function() {
        // 取消之前高亮的区域
        myChart.dispatchAction({
            type: 'downplay',
            seriesIndex: 0,
            dataIndex: currentIndex
        });
        
        // 更新索引，循环显示所有区域
        currentIndex = (currentIndex + 1) % validData.length;
        
        // 找到当前区域在原始数据中的索引
        let dataIndex = data.findIndex(item => item.name === validData[currentIndex].name);
        
        // 高亮当前区域
        myChart.dispatchAction({
            type: 'highlight',
            seriesIndex: 0,
            dataIndex: dataIndex
        });
        
        // 显示tooltip
        myChart.dispatchAction({
            type: 'showTip',
            seriesIndex: 0,
            dataIndex: dataIndex
        });
    }, 2000); // 每2秒切换一次，可以根据需要调整
    
    // 为了防止内存泄漏，在页面切换时清除定时器
    window.addEventListener('beforeunload', function() {
        clearInterval(animationInterval);
    });
}