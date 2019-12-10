<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>
				
				<el-form-item>
					<el-input v-model="filters.searchName" placeholder="请输入楼宇名称或编号"></el-input>
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
		<el-table :data="parks" highlight-current-row @selection-change="handleSelectionChange"  border style="width: 100%;"  :loading="$store.state.isLoading" >

			<el-table-column  type="selection" width="55" prop='id'></el-table-column>

			<el-table-column prop="name" label="楼宇名称或编号"  ></el-table-column>

			<el-table-column prop="floorCount" label="楼层数"  ></el-table-column>

			<el-table-column prop="maintain" label="所属维保"  ></el-table-column>
			
			<el-table-column prop="account" label="所属客户"  ></el-table-column>
			
			<el-table-column prop="description" label="备注"  ></el-table-column>

			<el-table-column label="操作" min-width="180">
				<template slot-scope="scope" >
                    <el-button-group >
						<el-button type="primary" size="small" @click="handleDelete(scope.row)">删除</el-button>
						<el-button type="primary" size="small" @click="handeEdit(scope.row)">修改</el-button>
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
			<el-form :model="addForm" label-width="120px" :rules="addFormRules" ref="addForm">

				<el-form-item label="楼宇名称或编号" prop="name">
					<el-input v-model="addForm.name" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="楼层数" prop="floorCount">
					<el-input v-model="addForm.floorCount" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="所属维保管理员" prop="maintain">
					<el-input v-model="addForm.maintain" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="所属客户" prop="account">
					<el-input v-model="addForm.account" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="楼宇描述" prop="description">
					<el-input v-model="addForm.description" auto-complete="off"></el-input>
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
					searchName:'',
					searchMaintain:'',
					searchAccount:'',
					searchPosition:'',
                },
				parks: [],
				total: 0,
				pageIndex: 1,
                pageSize:15,
				multipleSelection:[],
				addFormVisible: false,
				// 维保管理人员列表
				maintains:[],
				// 维保人员列表
				accounts:[],
				
				addFormRules: {
                    name: [
						{ required:true, message: '请输入园区名称', trigger: 'blur' },
					],
					floorCount: [
						{ required:true, message: '请输入楼层数', trigger: 'blur' },
					],
					maintain: [
						{ required:true, message: '请选择维保管理员', trigger: 'blur' },
					],
					account: [
						{ required:true, message: '请选择维保人员', trigger: 'blur' },
					],
					description: [
						{ required:false, message: '请输入园区描述', trigger: 'blur' },
					],
				},
				addForm: {
					name:'',
					floorCount:'',
					maintain:'',
					account:'',
                    description:''
				},

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

			// 获取楼宇列表
			search() {
				let param = {
                    pageIndex: this.pageIndex,
                    pageSize:this.pageSize,
                    searchName: this.filters.searchName,
					searchMaintain:this.filters.searchMaintain,
					searchAccount:this.filters.searchAccount,
					searchPosition:this.filters.searchPosition
				};
                
                post('park/list',param).then(result=>{
                    this.total=result.data.totalElements;
                    this.parks=result.data.content;
                });

			},
			// 显示新增界面
			handleAdd: function () {
				this.addFormVisible = true;
				this.addForm = {
					name:'',
					floorCount:'',
					maintain:'',
					account:'',
                    description:''
				};
            },
			// 显示编辑页面
			handeEdit(row){
				this.addFormVisible = true;
				this.addForm = Object.assign({}, row);
			},
			
			// 删除
			handleDelete(row) {
				this.$confirm('确认删除SIM卡吗?', '提示', {
					type: 'warning'
				}).then(() => {
                    let param = { ids: row.id };
                     post('park/delete',param).then(result=>{
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
						message: '请选择要删除的园区',
						type:'warning'
					})
				}else{
					var idsStr = ids.join('@');
					this.$confirm('确认批量删除园区吗?', '提示', {
						type: 'warning'
					}).then(() => {
						let param = { ids: idsStr };
						post('park/delete',param).then(result=>{
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
						post('park/save',param).then(result=>{
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