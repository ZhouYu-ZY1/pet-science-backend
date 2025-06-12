/**
 * 中间顶部 中国地图各地区用户分布情况
 */
function loadOrderMap() {
    $.ajax({
        url: '/api/dataVisual/userRegionNumber',
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data;
                let myChart = echarts.init(document.getElementById('main4_china_map'));

                let option = {
                    title: {
                        text: '各地区用户分布情况',
                        top: 80,
                        left: 0,
                        textStyle: {
                            color: '#0e94eb'
                        }
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: function(params) {
                            return params.name + '<br/>用户数: ' + formatNumber(params.value || 0);
                        },
                        backgroundColor: 'rgba(255,255,255,0.8)',
                        textStyle: {
                            color: '#333'
                        }
                    },
                    visualMap: {
                        min: 10000,
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
                        },
                        formatter: function(value) {
                            return formatNumber(value);
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
                                show: true,
                                color: '#fff',
                                formatter: function(params) {
                                    if (params.value && params.value > 20000) {
                                        return params.name + '\n' + formatNumber(params.value);
                                    }
                                    return params.name;
                                },
                                fontSize: 10
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
