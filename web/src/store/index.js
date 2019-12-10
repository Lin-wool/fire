// store文件夹下index.js
import Vue from 'vue'
import Vuex from 'vuex'
import {post} from '@/request.js'

Vue.use(Vuex);
 
export default new Vuex.Store({
  state: {
    isLoading:false
  },
  mutations: {
    showLoading(state){
        state.isLoading = true;
    },
    hideLoading(state){
        state.isLoading=false;
    }
  }
})
