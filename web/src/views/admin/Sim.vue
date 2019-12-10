<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">

				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>

				<el-form-item>
					<el-select v-model="filters.searchGateway" clearable placeholder="运营商" style="width:100px;" >
                        <el-option
                        v-for="item in gatewayOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
				</el-form-item>

                <el-form-item>
					<el-select v-model="filters.searchType" clearable placeholder="网络类型" style="width:120px;" >
                        <el-option
                        v-for="item in typeOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
				</el-form-item>

				<el-form-item>
					<el-input v-model="filters.searchMsisdn" placeholder="请输入MSISDN号"></el-input>
				</el-form-item>

				<el-form-item>
					<el-button type="primary" @click="search" :loading="$store.state.isLoading">查询</el-button>
				</el-form-item>

				<el-form-item>
					<el-button type="primary" @click="batchDelete" :loading="$store.state.isLoading">批量删除</el-button>
				</el-form-item>
				
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="sims" highlight-current-row @selection-change="handleSelectionChange"  border style="width: 100%;"  :loading="$store.state.isLoading" >

			<el-table-column  type="selection" width="55" prop='id'></el-table-column>

			<el-table-column prop="msisdn" label="MSISDN"  ></el-table-column>

			<el-table-column prop="imsi" label="IMSI"  ></el-table-column>

			<el-table-column prop="iccid" label="ICCID"  ></el-table-column>

			<el-table-column prop="gateway" label="网关"  >
				<template slot-scope="scope">
                        {{ scope.row.gateway | gateways }}
                </template>
			</el-table-column>

			<el-table-column prop="type" label="网络类型"  >
				<template slot-scope="scope">
                        {{ scope.row.type | types }}
                </template>
			</el-table-column>

			<el-table-column prop="status" label="卡状态"  >
				<template slot-scope="scope">
                        {{ scope.row.status | cardStatus }}
                </template>
			</el-table-column>

			<el-table-column prop="openTime" label="开卡时间"  ></el-table-column>

			<el-table-column prop="activeTime" label="激活时间"  ></el-table-column>

			<el-table-column label="操作" min-width="180">
				<template slot-scope="scope" >
                    <el-button-group >
						<el-button type="primary" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
				<el-form-item label="MSISDN" prop="msisdn">
					<el-input v-model="addForm.msisdn" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label='运营商' prop='gateway'>
					<el-select v-model="addForm.gateway" clearable placeholder="请选择运营商" style="width:150px;" >
                        <el-option
                        v-for="item in gatewayOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
				</el-form-item>
                <el-form-item label='网络类型' prop='type'>
					<el-select v-model="addForm.type" clearable placeholder="请选择网络类型" style="width:150px;" >
                        <el-option
                        v-for="item in typeOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                        </el-option>
                    </el-select>
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


	export default {
		data() {
			return {
				filters: {
                    searchGateway: '',
                    searchType:'',
					searchMsisdn:''
                },
				// 网关
                gatewayOptions:[
                    {value:'1',label:'移动'},
                    {value:'2',label:'联通'},
                    {value:'3',label:'电信'}                   
                ],
				typeOptions:[
					{value:'1',label:'NB'},
                    {value:'2',label:'4G'}
				],
				sims: [],
				total: 0,
				pageIndex: 1,
                pageSize:15,
				multipleSelection:[],

				addFormVisible: false,
				
				addFormRules: {
                    msisdn: [
						{ required: true, message: '请输入MSISDN号', trigger: 'blur' },
						{validator:(rule,value,callback)=>{
							if(value==null || value==""){
								callback();
								return;
							}
							if(!(/\d{13}$/.test(value))){
								callback(new Error('MSISDN格式不正确'))
							}else{
								callback()
							}
						},trigger:['blur','change']}
					],
					gateway :[
						{ required : true, message: '请选择网关', trigger:'change'}
					],
					type:[
						{required : true , message:'请选择网络类型', trigger:'change'}
					]
				},
				addForm: {
                    id:null,
					msisdn: '',
                    gateway:'',
					type:''
				},

			}
		},
		filters:{
			types(status){
				return status==1 ? 'NB' : '4G';
			},
			gateways(status){
				if(status==1){
					return '移动';
				}else if(status == 2){
					return '联通';
				}else{
					return '电信';
				}
			},
			cardStatus(status){
				return status==0 ? '正常' : '不可用';
			}
		},
		methods: {
			handleSelectionChange(val) {
				this.multipleSelection = val;
			},

			// 多选框变化
			handleCurrentChange(val) {
				this.pageIndex = val;
				this.search();
			},

			// 获取SIM卡列表
			search() {
				let param = {
                    pageIndex: this.pageIndex,
                    pageSize:this.pageSize,
                    searchGateway: this.filters.searchGateway,
                    searchType:this.filters.searchType,
					searchMsisdn:this.filters.searchMsisdn
				};
                
                post('sim/list',param).then(result=>{
                    this.total=result.data.totalElements;
                    this.sims=result.data.content;
                });

			},
			// 显示新增界面
			handleAdd: function () {
				this.addFormVisible = true;
				this.addForm = {
					gateway: '',
                    type:'',
					msisdn:''
				};
            },
			
			// 删除SIM卡
			handleDelete(row) {
				this.$confirm('确认删除SIM卡吗?', '提示', {
					type: 'warning'
				}).then(() => {
                    let param = { ids: row.id };
                     post('sim/delete',param).then(result=>{
                        this.$message({
							message: '删除成功',
							type: 'success'
						});
						this.search();
                    });

				}).catch(() => {
				});
			},

			// 批量删除
			batchDelete(){
				let ids = [];
				this.multipleSelection.forEach(s => ids.push(s.id));

				if(ids.length==0){
					this.$message({
						message: '请选择要删除的SIM卡',
						type:'warning'
					})
				}else{
					var idsStr = ids.join('@');
					this.$confirm('确认批量删除SIM卡吗?', '提示', {
						type: 'warning'
					}).then(() => {
						let param = { ids: idsStr };
						post('sim/delete',param).then(result=>{
							this.$message({
								message: '删除成功',
								type: 'success'
							});
							this.search();
						});

					}).catch(() => {});
				}
			},
			
			// 新增
			addSubmit: function () {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						let param = Object.assign({}, this.addForm);
						post('sim/save',param).then(result=>{
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