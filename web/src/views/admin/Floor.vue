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