/**
 * 数据格式化工具函数
 */

// 格式化数字为中文单位
function formatNumber(num) {
    if (typeof num !== 'number') {
        num = parseInt(num) || 0;
    }
    
    if (num >= 100000000) {
        return (num / 100000000).toFixed(1).replace(/\.0$/, '') + '亿';
    } else if (num >= 10000) {
        return (num / 10000).toFixed(1).replace(/\.0$/, '') + '万';
    }  else {
        return num.toString();
    }
}

// 格式化金额
function formatCurrency(num) {
    console.log("0:"+num)
    if (typeof num !== 'number') {
        num = parseFloat(num) || 0;
    }
    
    console.log("1:"+num)
    if (num >= 100000000) {
        return (num / 100000000).toFixed(1).replace(/\.0$/, '') + '亿元';
    } else if (num >= 10000) {
        return (num / 10000).toFixed(1).replace(/\.0$/, '') + '万元';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1).replace(/\.0$/, '') + '千元';
    } else {
        return num.toFixed(2) + '元';
    }
}

// 格式化百分比
function formatPercentage(num) {
    if (typeof num !== 'number') {
        num = parseFloat(num) || 0;
    }
    return num.toFixed(1) + '%';
}