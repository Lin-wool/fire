import babelpolyfill from 'babel-polyfill'
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
//import 'element-ui/lib/theme-default/index.css'
import 'element-ui/lib/theme-chalk/index.css';
//import './assets/theme/theme-green/index.css'
//import './assets/theme/theme-darkblue/index.css'

import store from './store/index.js'
import router from './routes'

import 'font-awesome/css/font-awesome.min.css'

import {service} from './request'

import util from '@/common/js/util'


import Mock from './mock'
//Mock.bootstrap(service);



Vue.use(ElementUI)

Vue.prototype.$axios = service;



Date.prototype.format = function(fmt){ 
  var o = { 
    "M+" : this.getMonth()+1,                 //月份 
    "d+" : this.getDate(),                    //日 
    "H+" : this.getHours(),                   //小时 
    "m+" : this.getMinutes(),                 //分 
    "s+" : this.getSeconds(),                 //秒 
    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
    "S"  : this.getMilliseconds()             //毫秒 
  }; 
  if(/(y+)/.test(fmt)){ 
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
  }
  for(var k in o){ 
    if(new RegExp("("+ k +")").test(fmt)){ 
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
    }
  } 
  return fmt; 
};



router.beforeEach((to, from, next) => {
  //NProgress.start();
  //console.log(from);
  if (to.path == '/login') {
    util.removeSession('sysUser');
    util.removeSession('account');
    util.removeSession('loginType');
    next();
  }else{
      let loginType = util.getFromSession('loginType');
      if (loginType==null) {
        next({ path: '/login' })
      } else {
        next()
      }
  }
  
})

//router.afterEach(transition => {
//NProgress.done();
//});

new Vue({
  //el: '#app',
  //template: '<App/>',
  router,
  store,
  //components: { App }
  render: h => h(App)
}).$mount('#app')

