<template>
	<el-row class="container">
		<el-col :span="24" class="header">
			<el-col :span="10" class="logo" :class="collapsed?'logo-collapse-width':'logo-width'">
				{{collapsed?'':sysName}}
			</el-col>
			<el-col :span="10">
				<div class="tools" @click.prevent="collapse">
					<i class="fa fa-align-justify"><strong class="title" style="padding-left:20px;">{{$route.name}}</strong></i>	
				</div>
			</el-col>
			<el-col :span="4" class="userinfo">
				<el-dropdown trigger="hover">
					<span class="el-dropdown-link userinfo-inner">
						{{fullName}}<i class="el-icon-arrow-down el-icon--right"></i>
					</span>
					<el-dropdown-menu slot="dropdown">
						<el-dropdown-item  @click.native="logout">退出登录</el-dropdown-item>
					</el-dropdown-menu>
				</el-dropdown>
			</el-col>
		</el-col>
		<el-col :span="24" class="main">
			<aside :class="collapsed?'menu-collapsed':'menu-expanded'">
				<!--导航菜单-->
				<el-menu :default-active="$route.path" class="el-menu-vertical-demo" 				    
					 router v-show="!collapsed" id="mainMenu">
					<template v-for="item in menuItems"  >
						<template v-if="item.children">
							<el-submenu :index="item.path" :key="item.path">
								<template slot="title">
									<i :class="item.iconCls"></i><span slot="title">{{ item.name }}</span>
								</template>
								<template v-for="subItem in item.children">
									<el-menu-item  :index="subItem.path" :key="subItem.path">
										{{ subItem.name }}
									</el-menu-item>
								</template>
							</el-submenu>
						</template>
						<template v-else>
							<el-menu-item :index="item.path" :key="item.path">
								<i :class="item.iconCls"></i><span slot="title">{{ item.name }}</span>
							</el-menu-item>
						</template>

					</template>
				</el-menu>
				<!--导航菜单-折叠后-->
				<ul class="el-menu el-menu-vertical-demo collapsed" v-show="collapsed" ref="menuCollapsed">
					<li v-for="(item,index) in menuItems"  class="el-submenu item" v-bind:key="index">
						<template v-if="item.children">
							<div class="el-submenu__title" style="padding-left: 20px;" @mouseover="showMenu(index,true)" @mouseout="showMenu(index,false)"><i :class="item.iconCls"></i></div>
							<ul class="el-menu submenu" :class="'submenu-hook-'+index" @mouseover="showMenu(index,true)" @mouseout="showMenu(index,false)"> 
								<li v-for="child in item.children" :key="child.path" class="el-menu-item" style="padding-left: 40px;" :class="$route.path==child.path?'is-active':''" @click="$router.push(child.path)">{{child.name}}</li>
							</ul>
						</template>
						<template v-else>
							<li class="el-submenu">
								 <el-tooltip class="item" effect="dark" :content="item.name" placement="right">
									<div class="el-submenu__title " style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;" :class="$route.path==item.path?'is-active':''" @click="$router.push(item.path)"><i :class="item.iconCls"></i></div>
								</el-tooltip>
							</li>
						</template>
					</li>
				</ul>
			</aside>
			<section class="content-container">
				<div class="grid-content bg-purple-light">
                  
					<el-col :span="24" class="content-wrapper">
						<transition name="fade" mode="out-in">
							<router-view></router-view>
						</transition>
					</el-col>
				</div>
			</section>
		</el-col>

		<!--新增界面-->
		<el-dialog title="修改密码" :visible.sync="changePassFormVisible" :close-on-click-modal="false">
			<el-form :model="changePassForm" label-width="100px" :rules="changePassRules" ref="changePassForm">
				<el-form-item label="新密码" prop="newPass">
					<el-input v-model="changePassForm.newPass" auto-complete="off" type="password"></el-input>
				</el-form-item>
                <el-form-item label="确认新密码" prop="confirmNewPass">
					<el-input v-model="changePassForm.confirmNewPass" auto-complete="off" type="password"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="changePassFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="changePass" :loading="$store.state.isLoading" >提交</el-button>
			</div>
		</el-dialog>
	</el-row>
</template>

<script>
import {post} from '@/request.js'
import util from '@/common/js/util'
	export default {

		data() {
			const validateConfirmNewPass = (rule, value, callback) => {
                if (!value) {
                    callback(new Error('请再次输新入密码'));
                } else if (value !== this.changePassForm.newPass) {
                    callback(new Error('两次输入密码不一致!'));
                } else {
                    callback();
                }
            };
			return {
				sysName:'消防云',
				collapsed:false,
				fullName:'',
				account:null,
				changePassFormVisible:false,
				changePassForm: {
					newPass: '',
					confirmNewPass: ''
				},
				changePassRules:{
					newPass: [
						{ required: true, message: '请输入新密码', trigger: 'blur' }
                    ],
					confirmNewPass: [
						{ required: true,  trigger: 'blur' ,validator:validateConfirmNewPass}
                    ]
				},
				menuItems:[
					{ path:'/account/index',name:'首页',iconCls: 'menu-icon fa fa-home fa-lg' },
					{ path:'/account/info',name:'客户信息',iconCls: 'menu-icon fa fa-user-circle-o fa-lg' },
					{ path:'/account/task',name:'任务列表',iconCls: 'menu-icon fa fa-tasks fa-lg' }
				]
			}
		},

		methods: {
			
			changePass(){
				this.$confirm('确认修改密码吗?', '提示', {
					type: 'warning'
				}).then(() => {
					
                    let param = { 
						newPass:this.changePassForm.newPass 
					};
                     post('/account/changePass',param).then(result=>{
                        this.$message({
							message: '密码修改成功',
							type: 'success'
						});
						this.changePassFormVisible=false;
                    });
				})
			},

			//退出登录
			logout() {
				var _this = this;
				this.$confirm('确认退出吗?', '提示', {
					//type: 'warning'
				}).then(() => {
					util.removeSession('account');
					_this.$router.push('/login');
				}).catch(() => {

				});


			},
			//折叠导航栏
			collapse(){
				this.collapsed=!this.collapsed;
				var mainMenu = document.getElementById('mainMenu');
                if(mainMenu.offsetWidth===0){
                    mainMenu.style.width = "230px"
                }
			},
			showMenu(i,status){
				this.$refs.menuCollapsed.getElementsByClassName('submenu-hook-'+i)[0].style.display=status?'block':'none';
			}
		},
		mounted() {
			var account = util.getFromSession('account');
			if (account) {
				this.account = JSON.parse(account);
				this.fullName=this.account.fullName;
			}

		}
	}

</script>

<style scoped lang="scss">
	@import '~scss_vars';
	
	.container {
		position: absolute;
		top: 0px;
		bottom: 0px;
		width: 100%;
		.header {
			height: 50px;
			line-height: 50px;
			background: $color-primary;
			color:#fff;
			.userinfo {
				text-align: right;
				padding-right: 35px;
				float: right;
				.userinfo-inner {
					cursor: pointer;
					color:#fff;
					img {
						width: 40px;
						height: 40px;
						border-radius: 20px;
						margin: 10px 0px 10px 10px;
						float: right;
					}
				}
			}
			.logo {
				//width:230px;
				height:50px;
				font-size: 22px;
				padding-left:20px;
				padding-right:20px;
				border-color: rgba(238,241,146,0.3);
				border-right-width: 1px;
				border-right-style: solid;
				img {
					width: 40px;
					float: left;
					margin: 10px 10px 10px 18px;
				}
				.txt {
					color:#fff;
				}
			}
			.logo-width{
				width:230px;
			}
			.logo-collapse-width{
				width:60px
			}
			.tools{
				padding: 0px 23px;
				//width:14px;
				height: 50px;
				line-height: 50px;
				cursor: pointer;
			}
		}
		.main {
			display: flex;
			// background: #324057;
			position: absolute;
			top: 50px;
			bottom: 0px;
			overflow: hidden;
			aside {
				flex:0 0 230px;
				width: 230px;
				// position: absolute;
				// top: 0px;
				// bottom: 0px;
				.el-menu{
					height: 100%;
				}
				.collapsed{
					width:60px;
					.item{
						position: relative;
					}
					.submenu{
						position:absolute;
						top:0px;
						left:60px;
						z-index:99999;
						height:auto;
						display:none;
					}

				}
			}
			.menu-collapsed{
				flex:0 0 60px;
				width: 60px;
			}
			.menu-expanded{
				flex:0 0 230px;
				width: 230px;
			}
			.content-container {
				// background: #f1f2f7;
				flex:1;
				// position: absolute;
				// right: 0px;
				// top: 0px;
				// bottom: 0px;
				// left: 230px;
				overflow-y: scroll;
				padding: 10px;
				.breadcrumb-container {
					//margin-bottom: 15px;
					.title {
						width: 200px;
						float: left;
						color: #475669;
					}
					.breadcrumb-inner {
						float: left;
					}
				}
				.content-wrapper {
					background-color: #fff;
					box-sizing: border-box;
				}
			}
			.menu-icon {
				display: inline-block;
				min-width: 30px;
				margin-right: 2px;
				vertical-align: sub;
				text-align: center;
				font-size: 18px;
				font-weight: 400;
			}
		}
	}
</style>