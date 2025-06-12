// 加载平台概览数据
function loadPlatformOverview() {
    $.ajax({
        url: '/api/dataVisual/intro',
        type: 'GET',
        success: function (res) {
            if (res.code === 200) {
                const data = res.data;

                $('#introduction').text(data.introduction);
                $('#registeredUsers').text(formatNumber(data.registeredUsers));
                $('#activeDailyUsers').text(formatNumber(data.activeDailyUsers));
                $('#annualTransactionValue').text(formatCurrency(data.annualTransactionValue));
                $('#totalOrders').text(formatNumber(data.totalOrders));
                $('#activeDailyUsers').text(formatNumber(data.activeDailyUsers));
                $('#activeWeeklyUsers').text(formatNumber(data.activeWeeklyUsers));
                // $('#main1_introduction8').text(data.activeMonthlyUsers);

                // 加载用户增长数据
                loadUserGrowthData();
            }
        }
    });
}

// 加载用户增长数据
function loadUserGrowthData() {
    $.getJSON('./data/userGrowth.json', function(data) {
        if (data && data.userGrowthTrend) {
            renderVisitsChart(data.userGrowthTrend.daily);
        }
    }).fail(function() {
        console.error('Failed to load user growth data, using default data');
        // 如果加载失败，使用默认数据
        renderVisitsChart([]);
    });
}


// 渲染用户增长趋势图
function renderVisitsChart(data) {
    const chart = echarts.init(document.getElementById('visitsChart'));

    // 如果没有数据，使用默认数据
    if (!data || data.length === 0) {
        data = [
            { date: '周一', newUsers: 5, totalUsers: 40, activeUsers: 35 },
            { date: '周二', newUsers: 3, totalUsers: 45, activeUsers: 38 },
            { date: '周三', newUsers: 4, totalUsers: 51, activeUsers: 42 },
            { date: '周四', newUsers: 6, totalUsers: 65, activeUsers: 48 },
            { date: '周五', newUsers: 4, totalUsers: 72, activeUsers: 55 },
            { date: '周六', newUsers: 5, totalUsers: 90, activeUsers: 68 },
            { date: '周日', newUsers: 6, totalUsers: 110, activeUsers: 83 }
        ];
    }

    // 处理数据，提取日期和各项指标
    // 生成从当前日期开始往前推的日期序列
    const generateRecentDates = (count) => {
        const dates = [];
        const today = new Date();

        for (let i = count - 1; i >= 0; i--) {
            const date = new Date(today);
            date.setDate(today.getDate() - i);

            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            dates.push(`${month}-${day}`);
        }

        return dates;
    };

    // 使用动态生成的日期替换原始日期
    const recentDates = generateRecentDates(data.length);
    const dates = recentDates;

    const newUsersData = data.map(item => item.newUsers || 0);
    const totalUsersData = data.map(item => item.totalUsers || 0);
    const activeUsersData = data.map(item => item.activeUsers || 0);

    const option = {
        title: {
            text: '用户增长趋势',
            top: 10,
            left: 'center',
            textStyle: {
                fontSize: 14,
                color: '#0e94eb'
            }
        },
        legend: {
            data: ['新增用户', '累计用户', '活跃用户'],
            top: 30,
            textStyle: {
                color: 'rgba(255, 255, 255, 0.8)',
                fontSize: 12
            }
        },
        grid: {
            top: '40%',
            right: 20,
            bottom: 30,
            left: 20,
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
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            borderColor: '#0e94eb',
            borderWidth: 1,
            textStyle: {
                color: '#fff'
            },
            formatter: function(params) {
                let result = params[0].name + '<br/>';
                params.forEach(function(item) {
                    result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:' + item.color + ';"></span>';
                    result += item.seriesName + ': ' + item.value + '<br/>';
                });
                return result;
            }
        },
        dataZoom: [
            {
                type: 'slider',
                show: data.length > 10,
                xAxisIndex: [0],
                start: data.length > 10 ? 70 : 0,
                end: 100,
                height: 20,
                bottom: 5,
                textStyle: {
                    color: 'rgba(255, 255, 255, 0.6)'
                },
                borderColor: 'rgba(255, 255, 255, 0.2)'
            }
        ],
        xAxis: {
            type: 'category',
            data: dates,
            axisLine: {
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                }
            },
            axisLabel: {
                color: 'rgba(255, 255, 255, 0.7)',
                fontSize: 10,
                rotate: data.length > 15 ? 45 : 0
            },
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: [
            {
                type: 'value',
                name: '新增/活跃用户',
                position: 'left',
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                splitLine: {
                    lineStyle: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                },
                axisLabel: {
                    color: 'rgba(255, 255, 255, 0.7)',
                    fontSize: 10,
                    formatter: function(value) {
                        if (value >= 1000) {
                            return (value / 1000).toFixed(1) + 'k';
                        }
                        return value;
                    }
                },
                nameTextStyle: {
                    color: 'rgba(255, 255, 255, 0.7)',
                    fontSize: 10
                }
            },
            {
                type: 'value',
                name: '累计用户',
                position: 'right',
                min: function(value) {
                    // 设置最小值为数据最小值的90%，让趋势更明显
                    return Math.floor(value.min * 0.9);
                },
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                splitLine: {
                    show: false
                },
                axisLabel: {
                    color: 'rgba(255, 255, 255, 0.7)',
                    fontSize: 10,
                    formatter: function(value) {
                        if (value >= 1000000) {
                            return (value / 1000000).toFixed(1) + 'M';
                        } else if (value >= 1000) {
                            return (value / 1000).toFixed(0) + 'k';
                        }
                        return value;
                    }
                },
                nameTextStyle: {
                    color: 'rgba(255, 255, 255, 0.7)',
                    fontSize: 10
                }
            }
        ],
        series: [
            {
                name: '新增用户',
                type: 'bar',
                data: newUsersData,
                itemStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: '#00d4ff'
                        }, {
                            offset: 1,
                            color: 'rgba(0, 212, 255, 0.3)'
                        }]
                    },
                    shadowColor: 'rgba(0, 212, 255, 0.5)',
                    shadowBlur: 5
                },
                barWidth: '30%'
            },
            {
                name: '累计用户',
                type: 'line',
                yAxisIndex: 1,
                data: totalUsersData,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                itemStyle: {
                    color: '#0e94eb'
                },
                lineStyle: {
                    width: 3,
                    color: '#0e94eb',
                    shadowColor: 'rgba(14, 148, 235, 0.5)',
                    shadowBlur: 8
                },
                areaStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0,
                            color: 'rgba(14, 148, 235, 0.3)'
                        }, {
                            offset: 1,
                            color: 'rgba(14, 148, 235, 0.05)'
                        }]
                    }
                }
            },
            {
                name: '活跃用户',
                type: 'line',
                data: activeUsersData,
                smooth: true,
                symbol: 'diamond',
                symbolSize: 6,
                itemStyle: {
                    color: '#ff6b6b'
                },
                lineStyle: {
                    width: 2,
                    color: '#ff6b6b',
                    type: 'dashed'
                }
            }
        ],
        animation: true,
        animationDuration: 2000,
        animationEasing: 'cubicOut'
    };

    chart.setOption(option);

    // 窗口大小变化时重新调整图表大小
    window.addEventListener('resize', () => {
        chart.resize();
    });
}
