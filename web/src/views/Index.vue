<template>
	<section>
		main
	</section>
</template>

<script>
import util from '@/common/js/util'
	export default {
		mounted(){
			let loginType=util.getFromSession("loginType");
			if(loginType=='0'){
				this.$router.push('/admin/index');
			}else if(loginType=='1'){
				this.$router.push('/account/index');
			}else{
				this.$router.push('/login');
			}
		}
	}
</script>

<style scoped>
</style>