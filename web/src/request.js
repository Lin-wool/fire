import axios from 'axios'
import { Message,Loading } from 'element-ui'
import qs from 'qs'
import store from "./store/index.js"
import router from './routes'

// create an axios instance
export const service = axios.create({
  baseURL: '/api/', // api的base_url
  timeout: 30000 // request timeout
})

// request interceptor
service.interceptors.request.use(config => {

    // 让每个请求携带token-- ['X-Token']为自定义key 请根据实际情况自行修改
    //config.headers['clientType'] = 'E'
    store.commit('showLoading');
    return config
}, error => {
  // Do something with request error
  console.log(error) // for debug
  Promise.reject(error)
})

// respone interceptor
service.interceptors.response.use(
  response => {
    store.commit('hideLoading');
    var result=response.data
    return result;
  },
  error => {
    store.commit('hideLoading');
    if(error.message.includes('timeout')){  
      error.message='请求超时';
    }
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  })


export function post(url,params,config){
  
  let  p = service.post(url,qs.stringify(params),config);
  
  return new Promise((resolve, reject)=>{
     p.then(data=>{
         
        if(data.error=='nologin'){
          Message({
            message: '登录超时，请重新登录',
            type: 'error',
            duration: 5 * 1000
          });
          setTimeout(()=>{
            router.push('/login');
          },2000);
         
          reject('登录超时');
        }else{

          if(data.success){
            resolve(data);
          }else{
            let msg=data.error||'未知错误';
            Message({
              message:msg,
              type: 'error'
            });
            reject(msg);
          }

          
        }
        
     }).catch(error=>{
       
        reject(error);
     });
  });
}



