<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
                <el-form-item>
					<el-select v-model="filters.searchLoginType"                            
                                placeholder="请选择客户"
                                clearable>
						<el-option value="0" label="管理账号"></el-option>
						<el-option value="1" label="客户账号"></el-option>
					</el-select>
				</el-form-item>
            	<el-form-item>
					<el-input v-model="filters.searchContent" placeholder="请输入日志内容" auto-complete="off" >
					</el-input>
				</el-form-item>
            	<el-form-item>
					<el-input v-model="filters.searchName" placeholder="请输入操作账号" auto-complete="off" >
					</el-input>
				</el-form-item>
            	<el-form-item>
					<el-input v-model="filters.searchIp" placeholder="请输入操作IP" auto-complete="off" >
					</el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary"  @click="search" :loading="$store.state.isLoading" >查询</el-button>
				</el-form-item>
				
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="logs" highlight-current-row border style="width: 100%;"   :loading="$store.state.isLoading">
			
			<el-table-column prop="name" label="账号"></el-table-column>
			<el-table-column prop="content" label="内容"></el-table-column>
			<el-table-column prop="ip" label="IP"></el-table-column>
			<el-table-column prop="createTime" label="操作时间"></el-table-column>
			
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar">
			<el-pagination layout="total,prev, pager, next" @current-change="handleCurrentChange" :page-size="pageSize" :total="total" style="float:right;">
			</el-pagination>
		</el-col>

		
	</section>
</template>

<script>
	import util from '@/common/js/util'
	import {post} from '@/request.js'


	export default {
		data() {
			return {
				filters: {
					searchLoginType:'0',
					searchContent:'',
					searchName:'',
					searchIp:""
                },
				logs: [],
				total: 0,
				pageIndex: 1,
                pageSize:15,

			}
		},
		methods: {
			

			handleCurrentChange(val) {
				this.pageIndex = val;
				this.search();
			},
			
			search() {
				let param = {
                    pageIndex: this.pageIndex,
					pageSize:this.pageSize,
					searchLoginType:this.filters.searchLoginType,
					searchContent:this.filters.searchContent,
                    searchName:this.filters.searchName,
                    searchIp:this.filters.searchIp
				};
                
                post('sysLog/list',param).then(result=>{
                    this.total=result.data.totalElements;
                    this.logs=result.data.content;
                });

			},

		},
		mounted() {
			this.search();
		}
	}

</script>

<style  lang="scss">


</style>