<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>
				<el-form-item>
					<el-input v-model="filters.searchName" placeholder="姓名"></el-input>
				</el-form-item>
                <el-form-item>
					<el-select v-model="filters.searchStatus" clearable placeholder="状态" style="width:100px;" >
                        <el-option
                        v-for="item in statusOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>

				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="search" :loading="$store.state.isLoading">查询</el-button>
				</el-form-item>
				
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="users" highlight-current-row  @row-dblclick="handleEdit" border style="width: 100%;"  :loading="$store.state.isLoading">
			
			<el-table-column prop="name" label="账号" width="100" >
			</el-table-column>
			<el-table-column prop="fullName" label="姓名"   >
			</el-table-column>
			<el-table-column prop="mobile" label="手机号"   >
			</el-table-column>
			<el-table-column  label="权限"    >
				<template slot-scope="scope">
                        {{ scope.row.role | roleName }}
                </template>
			</el-table-column>
			<el-table-column  label="状态"   >
				<template slot-scope="scope">
                        {{ scope.row.locked | statusName }}
                </template>
			</el-table-column>
			<el-table-column label="操作" min-width="180">
				<template slot-scope="scope" >
                    <el-button-group >
						<el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button type="primary" size="small" @click="handleChangeStatus(scope.$index, scope.row)">{{scope.row.locked==1?'解锁':'锁定'}}</el-button>
                        <el-button type="primary" size="small" @click="handleResetPass(scope.$index, scope.row)">重置密码</el-button>
                     </el-button-group>
				</template>
			</el-table-column>
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar">
			<el-pagination layout="total,prev, pager, next" @current-change="handleCurrentChange" :page-size="pageSize" :total="total" style="float:right;">
			</el-pagination>
		</el-col>



		<!--新增界面-->
		<el-dialog title="编辑" :visible.sync="addFormVisible" :close-on-click-modal="false">
			<el-form :model="addForm" label-width="80px" :rules="addFormRules" ref="addForm">
				<el-form-item label="账号" prop="name">
					<el-input v-model="addForm.name" auto-complete="off" :disabled="addForm.id!=null"></el-input>
				</el-form-item>
                <el-form-item label="姓名" prop="fullName">
					<el-input v-model="addForm.fullName" auto-complete="off"></el-input>
				</el-form-item>
                <el-form-item label="手机号" prop="mobile">
					<el-input v-model="addForm.mobile" auto-complete="off"></el-input>
				</el-form-item>

                <el-form-item label="权限" >
					<el-transfer v-model="grantedRoles" :data="allRoles"
					            :titles="['可选', '已选']"
					></el-transfer>
				</el-form-item>

				
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="addFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="addSubmit" :loading="$store.state.isLoading">提交</el-button>
			</div>
		</el-dialog>
	</section>
</template>

<script>
	import util from '@/common/js/util'
	import {post} from '@/request.js'

	const ALL_ROLES=[
		{key:'ROLE_ACCOUNT',label:'客户管理'},
		{key:'ROLE_DEVICE',label:'设备管理'},
		{key:'ROLE_SIM',label:'SIM卡管理'},
		{key:'ROLE_BUILDING',label:'建筑管理'},
		{key:'ROLE_ALARM',label:'告警管理'},
		{key:'ROLE_SYSPARAM',label:'配置管理'},
		{key:'ROLE_SYSLOG',label:'日志查看'},
		{key:'ROLE_SYSUSER',label:'管理员账号管理'},
	];
	export default {
		data() {
			return {
				filters: {
                    searchName: '',
                    searchStatus:''
                },
                statusOptions:[
                    {value:'',label:'全部'},
                    {value:'0',label:'正常'},
                    {value:'1',label:'锁定'}                   
                ],
				users: [],
				total: 0,
				pageIndex: 1,
                pageSize:15,

				addFormVisible: false,
				
				addFormRules: {
                    name: [
						{ required: true, message: '请输入账号', trigger: 'blur' },
						{validator:(rule,value,callback)=>{
							let reg=/[0-9a-zA-Z]{4,9}/
							if(!reg.test(value)){
								callback(new Error('账号必须是由4-9位数字和字母组合'))
							}else{
								callback()
							}
						},trigger:['blur','change']}
                    ],
					fullName: [
						{ required: true, message: '请输入姓名', trigger: 'blur' },
						{validator:(rule,value,callback)=>{
							if(value==null){
								callback();
								return;
							}
							if(value.length<2 || value.length>30){
								callback(new Error('长度必须在2到30之间'))
							}else{
								callback()
							}
						},trigger:['blur','change']}
                    ],
                    mobile: [
						{ required: false, message: '请输入手机号', trigger: 'blur' },
						{validator:(rule,value,callback)=>{
							if(value==null || value==""){
								callback();
								return;
							}
							if(!(/^1[3-9]\d{9}$/.test(value))){
								callback(new Error('手机号格式不正确'))
							}else{
								callback()
							}
						},trigger:['blur','change']}
					]
				},
				grantedRoles:[],
				addForm: {
                    id:null,
					name: '',
                    fullName:'',
					mobile:''
					
				},
				allRoles:ALL_ROLES

			}
		},
		filters:{
			roleName(role){
				if(!role){
					return '';
				}
				let roleNames = role.split(',').map(r=>{
					let roleObj=ALL_ROLES.find(n=>{
						return n.key==r;
					});
					if(roleObj){
						return roleObj.label
					}else{
						return '';
					}
				}).join(',');
				return roleNames;
			},
			statusName(status){
				return status==1 ? '锁定' : '正常';
			}
		},
		methods: {
			
			handleCurrentChange(val) {
				this.pageIndex = val;
				this.search();
			},
			//获取用户列表
			search() {
				let param = {
                    pageIndex: this.pageIndex,
                    pageSize:this.pageSize,
                    searchName: this.filters.searchName,
                    searchStatus:this.filters.searchStatus
				};
                
                post('sysUser/list',param).then(result=>{
                    this.total=result.data.totalElements;
                    this.users=result.data.content;
                });

			},

			handleResetPass(index, row) {
				this.$confirm('确认重置密码吗?', '提示', {
					type: 'warning'
				}).then(() => {
					
                    let param = { id: row.id };
                     post('sysUser/resetPass',param).then(result=>{
                        this.$message({
							message: '重置密码成功',
							type: 'success'
						});
                    });
				}).catch(() => {

				});
			},

			handleChangeStatus(index, row)  {
				this.$confirm(row.locked==1?'确认解锁吗？':'确定锁定吗？', '提示', {}).then(() => {

                    let param={
                        id:row.id,
                        status:row.locked==1?'0':'1'
                    };

                    post('sysUser/changeStatus',param).then(result=>{
                        this.$message('提交成功');
                        this.search();
                    });

                });
			},
			//显示新增界面
			handleAdd: function () {
				this.addFormVisible = true;
				this.addForm = {
					name: '',
                    fullName:'',
					mobile:''
				};
				this.grantedRoles=[];
            },
            //显示编辑界面
			handleEdit(row, column) {
				this.addFormVisible = true;
				this.addForm = Object.assign({}, row);
				this.grantedRoles=this.addForm.role?this.addForm.role.split(','):[];
			},
			
			//新增
			addSubmit: function () {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						let param = Object.assign({}, this.addForm);
						param['role']=this.grantedRoles && this.grantedRoles.length ? this.grantedRoles.join(','):'';
						post('sysUser/save',param).then(result=>{
							this.$message({
								message: '提交成功',
								type: 'success'
							});
							this.$refs['addForm'].resetFields();
							this.addFormVisible = false;
							this.search();
						});
					}
				});
			}
		},
		mounted() {
			this.search();
		}
	}

</script>

<style  lang="scss">
.el-transfer{
	.el-transfer-panel{
		width:180px;
	}
}

</style>