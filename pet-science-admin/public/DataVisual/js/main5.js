/**
 * 分组折线柱状图 - 视频标签互动数据
 */
function loadInteractionChart() {
    $.ajax({
        url: '/api/dataVisual/videoKeywords',
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data;
                
                // 提取数据
                let categories = [];
                let diggData = [];
                let shareData = [];
                let commentData = [];
                let videoCountData = [];
                
                data.forEach(function(item) {
                    categories.push(item.name);
                    diggData.push(item.diggCount);
                    shareData.push(item.shareCount);
                    commentData.push(item.commentCount);
                    videoCountData.push(item.videoCount);
                });
                
                // 基于准备好的dom，初始化echarts实例
                let myChart = echarts.init(document.getElementById('wordCloudChart'));
                
                // 指定图表的配置项和数据
                let option = {
                    backgroundColor: 'transparent',
                    title: {
                        text: '近期视频最热门标签互动数据统计',
                        left: 'center',
                        top: '1%',
                        textStyle: {
                            color: '#0e94eb',
                            fontSize: 16,
                            fontWeight: 'bold'
                        }
                    },
                    grid: {
                        left: '2%',
                        right: '2%',
                        top: '30%',
                        bottom: '1%',
                        containLabel: true
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#0e94eb'
                            }
                        },
                        backgroundColor: 'rgba(6, 30, 93, 0.9)',
                        borderColor: '#0e94eb',
                        borderWidth: 1,
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    legend: {
                        data: ['点赞数', '分享数', '评论数', '视频数量'],
                        top: '12%',
                        textStyle: {
                            color: '#fff',
                            fontSize: 12
                        },
                        itemWidth: 14,
                        itemHeight: 10
                    },
                    xAxis: {
                        type: 'category',
                        data: categories,
                        axisPointer: {
                            type: 'shadow'
                        },
                        axisLabel: {
                            rotate: 30,
                            interval: 0,
                            color: '#fff',
                            fontSize: 10,
                            margin: 8
                        },
                        axisLine: {
                            lineStyle: {
                                color: '#0e94eb'
                            }
                        },
                        axisTick: {
                            lineStyle: {
                                color: '#0e94eb'
                            }
                        }
                    },
                    yAxis: [
                        {
                            type: 'log',
                            name: '互动量',
                            position: 'left',
                            min: 1,
                            nameTextStyle: {
                                color: '#64b5f6',
                                fontSize: 12,
                                fontWeight: 'bold'
                            },
                            axisLabel: {
                                color: '#64b5f6',
                                fontSize: 10,
                                formatter: function(value) {
                                    if (value >= 10000) {
                                        return (value / 10000).toFixed(1) + '万';
                                    } else if (value >= 1000) {
                                        return (value / 1000).toFixed(1) + '千';
                                    }
                                    return value;
                                }
                            },
                            axisLine: {
                                lineStyle: {
                                    color: '#64b5f6'
                                }
                            },
                            axisTick: {
                                lineStyle: {
                                    color: '#64b5f6'
                                }
                            },
                            splitLine: {
                                lineStyle: {
                                    color: 'rgba(100, 181, 246, 0.15)'
                                }
                            }
                        },
                        {
                            type: 'value',
                            name: '视频数量',
                            position: 'right',
                            nameTextStyle: {
                                color: '#ffd54f',
                                fontSize: 12,
                                fontWeight: 'bold'
                            },
                            axisLabel: {
                                color: '#ffd54f',
                                fontSize: 10
                            },
                            axisLine: {
                                lineStyle: {
                                    color: '#ffd54f'
                                }
                            },
                            axisTick: {
                                lineStyle: {
                                    color: '#ffd54f'
                                }
                            }
                        }
                    ],
                    series: [
                        {
                            name: '点赞数',
                            type: 'bar',
                            data: diggData,
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    {offset: 0, color: '#ff7b9c'},
                                    {offset: 1, color: '#e83a5f'}
                                ])
                            },
                            barWidth: '20%'
                        },
                        {
                            name: '分享数',
                            type: 'bar',
                            data: shareData,
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    {offset: 0, color: '#7be0ad'},
                                    {offset: 1, color: '#36b37e'}
                                ])
                            },
                            barWidth: '20%'
                        },
                        {
                            name: '评论数',
                            type: 'bar',
                            data: commentData,
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    {offset: 0, color: '#7cc9ff'},
                                    {offset: 1, color: '#3498db'}
                                ])
                            },
                            barWidth: '20%'
                        },
                        {
                            name: '视频数量',
                            type: 'line',
                            yAxisIndex: 1,
                            symbol: 'circle',
                            symbolSize: 8,
                            smooth: true,
                            lineStyle: {
                                color: '#ffcc29',
                                width: 4,
                                shadowColor: 'rgba(255, 204, 41, 0.3)',
                                shadowBlur: 10
                            },
                            itemStyle: {
                                color: '#ffcc29',
                                borderColor: '#fff',
                                borderWidth: 2
                            },
                            data: videoCountData
                        }
                    ]
                };
                
                // 使用配置项和数据显示图表
                myChart.setOption(option);
                
                // 设置窗口大小变化时图表自适应
                window.addEventListener('resize', function() {
                    myChart.resize();
                });
                
            } else {
                console.error('获取视频关键词数据失败:', result.msg);
            }
        },
        error: function(xhr, status, error) {
            console.error('请求视频关键词数据失败:', error);
        }
    });
}

// 页面加载完成后执行
$(document).ready(function() {
    loadInteractionChart();
});