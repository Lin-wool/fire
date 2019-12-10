<template>
<section >
	   <el-row>
	   <el-col :span="24" >
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-date-picker
						v-model="filters.startDate"
						type="date"
						value-format="yyyy-MM-dd"
						format="yyyy-MM-dd"
						placeholder="开始日期">
					</el-date-picker>
				</el-form-item>
                <el-form-item>
					<el-date-picker
						v-model="filters.endDate"
						type="date"
						value-format="yyyy-MM-dd"
						format="yyyy-MM-dd"
						placeholder="截止日期">
					</el-date-picker>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="search" :loading="$store.state.isLoading">查询</el-button>
				</el-form-item>
				
			</el-form>
		</el-col>
		</el-row>
        <el-row>
            <el-col :span="12">
                <div id="chartPie1" style="width:100%; height:400px;"></div>
            </el-col>
            <el-col :span="12">
				<div id="chartPie2" style="width:100%; height:400px;"></div>
            </el-col>
        </el-row>
</section>
	
</template>

<script>
import echarts from 'echarts'
import {post} from '@/request.js'
import util from '@/common/js/util'
	export default {

		data(){
			return {
				filters:{
					startDate:'2019-01-01',
					endDate:new Date().format('yyyy-MM-dd')
				},
				account:null,
				data:null,
				chartPie1:null,
				chartPie2:null
				
			};
		},
		methods:{
			search() {

				let startDate=this.filters.startDate || '2019-01-01';
				let endDate=this.filters.endDate||new Date().format('yyyy-MM-dd');
				let param = {
					accountId: this.account.id,
                    startDate:startDate+' 00:00:00',
                    endDate: endDate+' 23:59:59'
				};


                
                post('task/stataccount',param).then(result=>{
					this.data=result.data;
					this.drawPieChart();
                });

			},

			
			drawPieChart() {
				if(!this.data){
					return;
				}
				

                this.chartPie1.setOption({
                    title: {
                        text: '任务统计',
                        bottom:20,
						x:'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    color:['PaleGreen', 'OrangeRed','LightSkyBlue','blueviolet'],
                    series: [
                        {
                            name: '发送统计',
                            type: 'pie',
                            radius: '55%',
                            center: ['50%', '50%'],
                            data: [
                                { value: this.data.successCount, name: '成功数' },
                                { value: this.data.failCount, name: '失败数' },
                                { value: this.data.unknownCount, name: '未知数' }
                            ],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
				});
				

                
                this.chartPie2.setOption({
                    title: {
                        text: '访问统计',
						bottom:20,
						x:'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    color:['PaleGreen', 'OrangeRed','LightSkyBlue','blueviolet'],
                    series: [
                        {
                            name: '访问统计',
                            type: 'pie',
                            radius: '55%',
                            center: ['50%', '50%'],
                            data: [
                                { value: this.data.visitMobileCount, name: '有访问' },
                                { value: this.data.successCount+this.data.unknownCount-this.data.visitMobileCount, name: '无访问' }
                            ],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                });
            }
		},
		mounted() {
			var account = util.getFromSession('account');
			if (account) {
				this.account = JSON.parse(account);
			}
			this.chartPie1 = echarts.init(document.getElementById('chartPie1'));
			this.chartPie2 = echarts.init(document.getElementById('chartPie2'));
			this.search();
		},
		 updated() {
            this.drawPieChart()
        }
	}
</script>

<style scoped>
    .chart-container {
        width: 100%;
        float: left;
    }
    /*.chart div {
        height: 400px;
        float: left;
    }*/
    
</style>