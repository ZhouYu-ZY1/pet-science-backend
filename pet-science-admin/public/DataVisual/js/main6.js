// 加载平台概览数据
function loadPlatformOverview() {
    $.ajax({
        url: '/api/dataVisual/intro',
        type: 'GET',
        success: function (res) {
            if (res.code === 200) {
                const data = res.data;

                $('#introduction').text(data.introduction);
                $('#registeredUsers').text(data.registeredUsers);
                $('#activeDailyUsers').text(data.activeDailyUsers);
                $('#annualTransactionValue').text(data.annualTransactionValue);
                $('#totalOrders').text(data.totalOrders);
                $('#activeDailyUsers').text(data.activeDailyUsers);
                $('#activeWeeklyUsers').text(data.activeWeeklyUsers);
                // $('#main1_introduction8').text(data.activeMonthlyUsers);

                // 渲染访问量趋势图
                renderVisitsChart(data.visitsTrend || []);
            }
        }
    });
}


// 渲染访问量趋势图
function renderVisitsChart(data) {
    const chart = echarts.init(document.getElementById('visitsChart'));

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
        grid: {
            top: 50,
            right: 0,
            bottom: 0,
            left: 0,
            containLabel: true
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        xAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
            axisLine: {
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                }
            },
            axisLabel: {
                color: 'rgba(255, 255, 255, 0.7)'
            }
        },
        yAxis: {
            type: 'value',
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
                color: 'rgba(255, 255, 255, 0.7)'
            }
        },
        series: [{
            data: [40, 45, 51, 65, 72, 90, 110],
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            itemStyle: {
                color: '#0e94eb'
            },
            lineStyle: {
                width: 3,
                color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [{
                        offset: 0,
                        color: '#0e94eb'
                    }, {
                        offset: 1,
                        color: 'rgba(14, 148, 235, 0.1)'
                    }]
                },
                shadowColor: 'rgba(14, 148, 235, 0.5)',
                shadowBlur: 10
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
                        color: 'rgba(14, 148, 235, 0.1)'
                    }]
                }
            }
        }]
    };

    chart.setOption(option);
}
