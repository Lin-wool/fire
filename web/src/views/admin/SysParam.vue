<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="addForm">
				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>
			
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="params" highlight-current-row  @row-dblclick="handleEdit" border style="width: 100%;"  :loading="$store.state.isLoading">
			
			<el-table-column prop="paramName" label="参数名称">
			</el-table-column>
			<el-table-column prop="paramValue" label="参数值">
			</el-table-column>
			<el-table-column prop="description" label="描述">
			</el-table-column>
			
			<el-table-column label="操作" width="180">
				<template slot-scope="scope" >
                    <el-button-group >
						<el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
						<el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                     </el-button-group>
				</template>
			</el-table-column>
		</el-table>

		


		<!--新增界面-->
		<el-dialog title="编辑" :visible.sync="addFormVisible" :close-on-click-modal="false">
			<el-form :model="addForm" label-width="80px" :rules="addFormRules" ref="addForm">
				<el-form-item label="参数名" prop="paramName">
					<el-input v-model="addForm.paramName" auto-complete="off" :disabled="addForm.id!=null"></el-input>
				</el-form-item>
                <el-form-item label="参数值" prop="paramValue">
					<el-input v-model="addForm.paramValue" auto-complete="off"></el-input>
				</el-form-item>
                <el-form-item label="描述" prop="description">
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
			
				params: [],
			
				addFormVisible: false,
				
				addFormRules: {
                    paramName: [
						{ required: true, message: '参数名称不能为空', trigger: 'blur' }
                    ],
					
                    paramValue: [
						{ required: true, message: '描述不能为空', trigger: 'blur' }
					]
				},
				addForm: {
                    id:null,
					paramName: '',
                    paramValue:'',
					description:''
					
				}

			}
		},
		
		methods: {
			
			
			search() {
				
                
                post('param/list',{}).then(result=>{
                    this.params=result.data;
                });

			},

			
			//显示新增界面
			handleAdd: function () {
				this.addFormVisible = true;
				this.addForm = {
					paramName: '',
                    paramValue:'',
					description:''
				};
            },
            //显示编辑界面
			handleEdit(row, column) {
				this.addFormVisible = true;
				this.addForm = Object.assign({}, row);
			},
			
			//新增
			addSubmit: function () {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						let param = Object.assign({}, this.addForm);
						post('param/save',param).then(result=>{
							this.$message({
								message: '提交成功',
								type: 'success'
							});
							this.addFormVisible = false;
							this.search();
						});
					}
				});
			},

			handleDelete( row) {
				this.$confirm('删除参数可能导致系统不可用，是否确认删除？', '警告', {
					type: 'warning'
				}).then(() => {
					
                    let param = { id: row.id };
                     post('param/delete',param).then(result=>{
                        this.$message({
							message: '删除成功',
							type: 'success'
						});
						this.search();
                    });
				});
			}
		},
		mounted() {
			this.search();
		}
	}

</script>

<style  lang="scss">


</style>