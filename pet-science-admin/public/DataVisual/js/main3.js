/**
 * 热销商品排行
 */
function loadTopSellingProducts() {
    $.ajax({
        url: '/api/dataVisual/topSellingProducts',
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data;
                
                // 基于准备好的dom，初始化echarts实例
                let myChart = echarts.init(document.getElementById('topProductsChart'));
                
                // 设置图表配置
                let option = {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'shadow'
                        },
                        formatter: function(params) {
                            return params.name + ': ' + formatNumber(params.value) + '件';
                        }
                    },
                    grid: {
                        left: '-90',
                        right: '10%',
                        bottom: '0%',
                        top: '5%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'value',
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: '#0e94eb'
                            }
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            color: '#fff',
                            fontSize: 10,
                            formatter: function(value) {
                                return formatNumber(value);
                            }
                        },
                        splitLine: {
                            show: false
                        }
                    },
                    yAxis: {
                        type: 'category',
                        data: [],
                        axisLine: {
                            show: true,
                            lineStyle: {
                                color: '#0e94eb'
                            }
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            color: '#fff',
                            fontSize: 11,
                            align: 'left',
                            margin: 110,
                            formatter: function(value) {
                                // 如果名称太长，截断并添加省略号
                                if (value.length > 10) {
                                    return value.substring(0, 10) + '...';
                                }
                                return value;
                            }
                        }
                    },
                    series: [
                        {
                            name: '销量',
                            type: 'bar',
                            data: [],
                            barWidth: '60%',
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                                    { offset: 0, color: '#83bff6' },
                                    { offset: 0.5, color: '#188df0' },
                                    { offset: 1, color: '#0e94eb' }
                                ])
                            },
                            label: {
                                show: true,
                                position: 'right',
                                color: '#fff',
                                formatter: function(params) {
                                    return formatNumber(params.value) + '件';
                                }
                            }
                        }
                    ]
                };
                
                // 使用配置项和数据显示图表
                myChart.setOption(option);
                
                // 设置窗口大小变化时图表自适应
                window.addEventListener('resize', function() {
                    myChart.resize();
                });
                
                // 添加自动滚动效果
                if (data.length > 5) {
                    let displayCount = 5; // 每次显示的数据条数
                    let totalGroups = Math.ceil(data.length / displayCount); // 总共有几组数据
                    let currentGroup = 0; // 当前显示的是第几组数据
                    
                    // 初始显示前5条数据（销量最高的5条）
                    let initialData = data.slice(0, displayCount);

                    // 添加序号到名称前，并反转数组使销量最高的显示在最上面
                    let initialNames = initialData.map((item, index) => {
                        return (index + 1) + ". " + item.name;
                    }).reverse();
                    let initialValues = initialData.map(item => item.value).reverse();
                    
                    myChart.setOption({
                        yAxis: {
                            data: initialNames
                        },
                        series: [{
                            data: initialValues
                        }]
                    });
                    
                    // 设置定时器，每3秒切换一次数据
                    setInterval(function() {
                        currentGroup = (currentGroup + 1) % totalGroups;
                        
                        let startIndex = currentGroup * displayCount;
                        let endIndex = Math.min(startIndex + displayCount, data.length);
                        
                        let visibleData = data.slice(startIndex, endIndex);
                        
                        // 添加序号到名称前，并反转数组使销量最高的显示在最上面
                        let visibleNames = visibleData.map((item, index) => {
                            let rankNumber = startIndex + index + 1;
                            return rankNumber + ". " + item.name;
                        }).reverse();
                        let visibleValues = visibleData.map(item => item.value).reverse();
                        
                        // 如果最后一组数据不足5条，需要补充空数据
                        if (visibleNames.length < displayCount) {
                            let emptyCount = displayCount - visibleNames.length;
                            for (let i = 0; i < emptyCount; i++) {
                                visibleNames.push("");
                                visibleValues.push(0);
                            }
                        }
                        
                        // 更新图表数据
                        myChart.setOption({
                            yAxis: {
                                data: visibleNames
                            },
                            series: [{
                                data: visibleValues
                            }]
                        });
                    }, 3000); // 每3秒滚动一次
                }
                
            } else {
                console.error('获取热销商品数据失败:', result.msg);
            }
        },
        error: function(xhr, status, error) {
            console.error('请求热销商品数据失败:', error);
        }
    });
}