/**
 * 中间顶部 中国地图订单分布
 */
function echart_map() {
    $.ajax({
        url: '/api/dataVisual/orderRegionDistribution',
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data;
                let myChart = echarts.init(document.getElementById('ceshi8'));

                // 地理坐标数据
                let chinaGeoCoordMap = {
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
                let local = "四川";
                let localCoord = chinaGeoCoordMap[local]; // 当前城市坐标
                let localSales = 0;
                // 转换数据格式，生成从北京到各省的连线数据
                let convertData = function(data) {
                    let res = [];
                    for (let i = 0; i < data.length; i++) {
                        let dataItem = data[i];
                        let toCoord = chinaGeoCoordMap[dataItem.name];
                        if (localCoord && toCoord && dataItem.name !== local) {
                            res.push([{
                                coord: localCoord,
                                name: dataItem.name,
                                value: dataItem.value
                            }, {
                                coord: toCoord
                            }]);
                        }else if (dataItem.name === local) {
                            localSales = dataItem.value;
                        }
                    }
                    return res;
                };
            
                // 生成箭头系列
                let linesData = convertData(data);
                localCoord.push(localSales)

                let effectScatterData = data.map(function(dataItem) {
                    return {
                        name: dataItem.name,
                        value: chinaGeoCoordMap[dataItem.name] ?
                            chinaGeoCoordMap[dataItem.name].concat([dataItem.value]) :
                            [0, 0, 0]
                    };
                });

                var planePath = 'path://M1705.06,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705.06,1318.313z';
                let option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: function(params) {
                            if(params.value[2] != undefined){
                                return params.name + '<br/>销量: ' + params.value[2];
                            }
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
                        // inRange: {
                        //     color: ['#03e9ff', '#1280d1', '#0c67b3', '#064e94']
                        // },
                        color: ['#f44336', '#fc9700', '#ffde00', '#ffde00', '#00eaff'],
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    // 添加地理坐标系
                    geo: {
                        map: 'china',
                        roam: true,
                        zoom: 2,
                        center: [104, 31],
                        aspectScale: 0.75,
                        label: {
                            formatter: function(params) {
                                return params.name;
                            },
                            emphasis: {
                                show: false
                            }
                        },
                        itemStyle: {
                            normal: {
                                areaColor: '#0252bb',
                                borderColor: '#000',
                                borderWidth: 1
                            },
                            emphasis: {
                                areaColor: '#013a70'
                            }
                        }
                    },
                    series: [
                        // 添加箭头线系列
                        {
                            name: '物流路线',
                            type: 'lines',
                            coordinateSystem: 'geo',
                            zlevel: 2,
                            effect: {
                                show: true,
                                period: 6, // 动画周期
                                trailLength: 0.02, // 拖尾长度
                                // symbol: 'arrow', // 箭头形状
                                symbol: planePath, // 飞机样式
                                symbolSize: 15, // 箭头大小
                            },
                            lineStyle: {
                                normal: {
                                    color: '#ffde00', // 修改为黄色，与蓝色背景形成鲜明对比
                                    width: 1,       // 增加线宽，使线条更明显
                                    opacity: 1,     // 增加不透明度
                                    curveness: 0.4 // 增加曲率，使线条更流畅
                                }
                            },
                            data: linesData
                        },
                        // 添加散点系列
                        {
                            name: '城市销量',
                            type: 'effectScatter',
                            coordinateSystem: 'geo',
                            zlevel: 2,
                            rippleEffect: {
                                period: 4,
                                brushType: 'stroke',
                                scale: 4
                            },
                            label: {
                                normal: {
                                    show: false,
                                    position: 'right',
                                    formatter: '{b}'
                                }
                            },
                            symbol: 'circle',
                            symbolSize: function(val) {
                                return val[2] > 0 ? 4 + val[2] / 50 : 0;
                            },
                            itemStyle: {
                                normal: {
                                    color: '#ffde00' // 修改为黄色，与箭头保持一致
                                }
                            },
                            data: effectScatterData
                        },
                        // 添加北京中心点
                        {
                            name: '中心点',
                            type: 'scatter',
                            coordinateSystem: 'geo',
                            zlevel: 3,
                            symbol: 'pin',
                            symbolSize: 30,
                            itemStyle: {
                                normal: {
                                    color: '#f44336' // 使用红色，更加醒目
                                }
                            },
                            data: [{
                                name: local,
                                value: localCoord
                            }]
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