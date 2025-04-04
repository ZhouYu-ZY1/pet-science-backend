module.exports = {
  devServer: {
    proxy: {
      '/statics': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        pathRewrite: { '^/statics': '/statics' }
      }
    }
  }
}