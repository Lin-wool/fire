<template>
<div class="login-wrap">
  <el-form :model="loginForm" :rules="loginRule" ref="loginForm" label-position="left" label-width="0px" class="demo-ruleForm login-container">
    <h3 class="title">系统登录</h3>
     <el-form-item  prop="loginType">
        <el-select v-model="loginForm.loginType" placeholder="登录类型" style="width:100%;">
            <el-option
            v-for="item in loginTypeOptions"
            :key="item.value"
            :label="item.name"
            :value="item.value">
            </el-option>
        </el-select>
    </el-form-item>
    <el-form-item prop="userName">
      <el-input type="text" v-model="loginForm.userName" auto-complete="off" placeholder="账号"></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item style="width:100%;">
      <el-button type="primary" style="width:100%;" @click.native.prevent="doLogin" :loading="$store.state.isLoading">登录</el-button>
    </el-form-item>
  </el-form>
  </div>
</template>

<script>
  import {post} from '@/request.js'
  import util from '@/common/js/util'
  //import NProgress from 'nprogress'
  export default {
    mounted(){
      this.loginForm.loginType=util.getFromSession("loginType")==null?"0":util.getFromSession("loginType");
    },
    data() {
      return {
        
        loginTypeOptions:[
          {value:'0',name:'管理员'},
          {value:'1',name:'客户'}
        ],
        loginForm: {
          loginType:'0',
          userName: '',
          password: ''
        },
        loginRule: {
          loginType: [
            { required: true, message: '请选择登录类型', trigger: 'blur' },
          ],
          userName: [
            { required: true, message: '请输入账号', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {
     
      doLogin(ev) {
        var _this = this;
        this.$refs.loginForm.validate((valid) => {
          if (valid) {
              let url=this.loginForm.loginType==0?'sysUser/login':'account/login';

              post(url,{
                      "loginType":this.loginForm.loginType,
                      "userName":this.loginForm.userName,
                      "password":this.loginForm.password
              }).then((result) => {
                  if (result.success) { 
                      util.saveToSession("loginType",this.loginForm.loginType+"");
                      if(this.loginForm.loginType==0){
                          util.saveToSession("sysUser", JSON.stringify(result.data)); 
                          this.$router.push('/admin/index');
                      }else{
                          util.saveToSession("account", JSON.stringify(result.data));
                          this.$router.push('/account/index');
                      }
                      
                      
                  } else {

                    this.$message.error('登录失败：' + result.error);
                  }
              })
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      }
    }
  }

</script>

<style lang="scss" scoped>
 .login-wrap{
        position: absolute;
        width:100%;
        height:100%;
        background-color:#57aefe; 
        background:url(../assets/bg.jpg) no-repeat;
        background-size:100% 100%; 
        background-attachment: fixed;
   
  .login-container {
    /*box-shadow: 0 0px 8px 0 rgba(0, 0, 0, 0.06), 0 1px 0px 0 rgba(0, 0, 0, 0.02);*/
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    margin: 130px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
    .title {
      margin: 0px auto 40px auto;
      text-align: center;
      color: #505458;
    }
    .remember {
      margin: 0px 0px 35px 0px;
    }
  }
 }
</style>