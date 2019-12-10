import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);



let routes = [
    {
        path: '/login',
        component:resolve => require(['./views/Login.vue'], resolve)
    },
    {
        path: '/404',
        component: resolve => require(['./views/404.vue'], resolve)
    },
    {
        path: '/',
        component: resolve => require(['./views/Index.vue'], resolve)
    },
    {
        path: '/admin',
        component: resolve => require(['./views/admin/Home.vue'], resolve),
        children: [
            { path:'index', name:'首页',component:resolve => require(['./views/admin/Index.vue'], resolve)},
            { path:'param', name:'配置管理',component:resolve => require(['./views/admin/SysParam.vue'], resolve)},
            { path:'sysUser', name:'管理员账号管理',component:resolve => require(['./views/admin/SysUser.vue'], resolve)},
            { path:'account',name:'客户账号查看',component:resolve => require(['./views/admin/Account.vue'], resolve)},
            { path:'maintain',name:'维保管理员管理',component:resolve => require(['./views/admin/Account.vue'], resolve)},
            { path:'maintainSub',name:'维保人员查看',component:resolve => require(['./views/admin/Account.vue'], resolve)},
            { path:'sim',name:'卡管理',component:resolve => require(['./views/admin/Sim.vue'], resolve)},
            { path:'park',name:'园区管理',component:resolve => require(['./views/admin/Park.vue'], resolve)},
            { path:'building',name:'楼宇管理',component:resolve => require(['./views/admin/Building.vue'], resolve)},
            { path:'floor',name:'楼层管理',component:resolve => require(['./views/admin/Floor.vue'], resolve)},
            { path:'sysParam',name:'配置管理',component:resolve => require(['./views/admin/SysParam.vue'], resolve)},
            { path:'sysLog',name:'日志查看',component:resolve => require(['./views/admin/SysLog.vue'], resolve)}
        ]
    },
    {
        path: '/account',
        component: resolve => require(['./views/account/Home.vue'], resolve),
        children: [
            { path:'index', name:'客户首页',component:resolve => require(['./views/account/Index.vue'], resolve)},
        ]
    },
    {
        path: '*',
        hidden: true,
        redirect: { path: '/404' }
    }
];

export default new Router({
    routes
});