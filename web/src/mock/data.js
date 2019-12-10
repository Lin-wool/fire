import Mock from 'mockjs';


Date.prototype.format = function(fmt){ 
  var o = { 
    "M+" : this.getMonth()+1,                 //月份 
    "d+" : this.getDate(),                    //日 
    "H+" : this.getHours(),                   //小时 
    "m+" : this.getMinutes(),                 //分 
    "s+" : this.getSeconds(),                 //秒 
    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
    "S"  : this.getMilliseconds()             //毫秒 
  }; 
  if(/(y+)/.test(fmt)){ 
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
  }
  for(var k in o){ 
    if(new RegExp("("+ k +")").test(fmt)){ 
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
    }
  } 
  return fmt; 
};

const SysUsers = [
  {
    id: 1,
    name: 'admin',
    password:'Pass@1234',
    fullName:'管理员',
    mobile:'13800138000',
    role:'',
    locked:0,
    createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
  }
];

const Clients = [
  {
    id: 1,
    name: 'client1',
    password:'Pass@1234',
    fullName:'张三',
    locked:0,
    createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
  },
  {
    id: 2,
    name: 'client2',
    password:'Pass@1234',
    fullName:'李四',
    locked:0,
    createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
  }
];


const Domains=[{
  id:1,
  domain:'localhost:8002',
  ip:'localhost',
  maxNum:100,
  createdNum:100,
  invalid:0,
  createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
}];


const Templates=[{
   id:1,
   clientId:1,
   content:'请点击【SHORT_URL】了解更多详情【SIGN】',
   auditStatus:0,
   createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
}];


const Signatures=[{
  id:1,
  clientId:1,
  content:'步云',
  auditStatus:0,
  createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
}];


const Tasks=[{
   id:1,
   clientId:1,
   name:'测试任务1',
   template:'请点击【SHORT_URL】了解更多详情【SIGN】',
   signature:'步云',
   targetUrl:'http://www.baidu.com',
   mobileCount:100,
   sendCount:50,
   successCount:40,
   unknownCount:5,
   failCount:5,
   visitCount:10,
   visitMobileCount:8,
   createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
}];

const VisitRecords=[{
  id:1,
  clientId:1,
  clientFullName:'张三',
  taskId:1,
  taskName:'测试任务1',
  mobile:'13800138000',
  userAgent:'XXXX',
  remoteAddr:'127.0.0.1',
  targetUrl:"http://xxxxx",
  sendTime:'2019-03-08 12:30:12',
  visitTime:'2019-03-08 12:35:07'
}];

export  { 
  SysUsers, 
  Clients,
  Domains,
  Templates,
  Signatures,
  Tasks,
  VisitRecords 
};
