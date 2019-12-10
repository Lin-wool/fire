import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import qs from 'qs'

import {SysUsers,Clients,Domains,Templates,Signatures,Tasks,VisitRecords} from './data.js';

let session={};

export default {
  /**
   * mock bootstrap
   */
  bootstrap(axiosInstance) {
    let mock = new MockAdapter(axiosInstance || axios, { delayResponse: 500 });

  

    //管理员登录
    mock.onPost('/api/sysUser/login').reply(config => {
      let param=qs.parse(config.data);
      let user=SysUsers.find(u=>{
        return u.name==param.userName && u.password == param.password && u.locked==0;
      });
      if(!user){
        return [200, { success: false, error: '账号或密码错误' }];
      }
      session.user=user;
      
      session.loginType=param.loginType;

      const ALL_ROLES=[
        {key:'ROLE_USER',label:'用户管理'},
        {key:'ROLE_DOMAIN',label:'域名管理'},
        {key:'ROLE_CLIENT',label:'客户管理'},
        {key:'ROLE_TASK',label:'任务管理'},
        {key:'ROLE_AUDIT',label:'审核管理'},
        {key:'ROLE_DATA',label:'报表统计'},
        {key:'ROLE_LOG',label:'日志查看'}
      ];

      let role=user.role;
      if( user.name=='admin'){
         role=ALL_ROLES.map(u=>u.key).join(',');
      }

      return [200, { success: true, error: '',data:{
                        name:user.name,
                        fullName:user.fullName,
                        loginType:param.loginType,
                        role:role,
                        id:user.id
                      }
                   }
             ];
    });

    //客户登录
    mock.onPost('/api/client/login').reply(config => {
      let param=qs.parse(config.data);
      let user=Clients.find(u=>{
        return u.name==param.userName && u.password == param.password && u.locked==0;
      });
      if(!user){
        return [200, { success: false, error: '账号或密码错误' }];
      }
      session.client=user;
      
      session.loginType=param.loginType;

      return [200, { success: true, error: '',data:{
                        name:user.name,
                        fullName:user.fullName,
                        loginType:param.loginType,
                        id:user.id
                      }
                   }
             ];
    });

    mock.onPost('/api/logout').reply(config => {
      session.user=null;
      session.client=null;
      session.loginType=null;
      return [200, { success: true, error: '' }];
    });


    mock.onPost('/api/sysUser/list').reply(config => {
      let param=qs.parse(config.data);
      let result=SysUsers.map(u=>{
         let model=JSON.parse(JSON.stringify(u));
         model.password=null;
         return model;
      });
      result=result.filter(u=>{
          let nameOk = param.searchName? u.fullName.indexOf(param.searchName)>-1 :true;
          let lockedOk= param.searchStatus ==''?true:u.locked==param.searchStatus;
          return nameOk && lockedOk;
      });
     
      param.pageIndex=parseInt(param.pageIndex||1)-1;
      param.pageSize=parseInt(param.pageSize||15);
      let start=param.pageIndex*param.pageSize;
      let end=start+param.pageSize;
      let pageResult=[];
      for(let i=start;i<result.length && i<end;i++){
         pageResult.push(result[i]);
      }


      return [200, { success: true, error: '',
          data:{
            totalElements:result.length,
            content:pageResult
          } 
      }];
    });

    
    mock.onPost('/api/sysUser/save').reply(config => {
      let param=qs.parse(config.data);
      let dbUser=SysUsers.find(u=>{
        return u.name==param.name;
      });
      
      let model={};
      if(param.id){
        model=SysUsers.find(u=>{
          return u.id==param.id;
        });
        if(!model){
          return [200, { success: false, error: '找不到该记录' }];
        }
      }else{
        model.id=SysUsers.length+1;
        model.name=param.name;
        SysUsers.push(model);
      }
      model.fullName=param.fullName;
      model.mobile=param.mobile;
      model.createTime=model.createTime||new Date().format('yyyy-MM-dd HH:mm:ss');
      model.modifyTime=new Date().format('yyyy-MM-dd HH:mm:ss');
      model.password=model.password || '123456';
      model.locked=0;
      model.role=param.role;
      return [200, { success: true, error: '' }];
    });

    
    
    mock.onPost('/api/sysUser/changeStatus').reply(config => {
      let param=qs.parse(config.data);
      let model=SysUsers.find(u=>{
        return u.id==param.id;
      });
      if(model){
        model.locked=param.status;
      }
      return [200, { success: true, error: '' }];
    });

    mock.onPost('/api/sysUser/resetPass').reply(config => {
      let param=qs.parse(config.data);
      let model=SysUsers.find(u=>{
        return u.id==param.id;
      });
      if(model){
        model.password='123456';
      }
      return [200, { success: true, error: '' }];
    });



    mock.onPost('/api/sysUser/changePass').reply(config => {
      let param=qs.parse(config.data);

      session.user.password=param.newPass;    
      return [200, { success: true, error: '' }];
    });
   

    mock.onPost('shorturl/domain/list').reply(config=>{
      let param=qs.parse(config.data);
      let result=Domains.filter(u=>{
          let nameOk = param.searchDomain? u.domain.indexOf(param.searchDomain)>-1 :true;
          let invalidOk= param.searchStatus ==''?true:u.invalid==param.searchStatus;
          return nameOk && invalidOk;
      });
     
      param.pageIndex=parseInt(param.pageIndex||1)-1;
      param.pageSize=parseInt(param.pageSize||15);
      let start=param.pageIndex*param.pageSize;
      let end=start+param.pageSize;
      let pageResult=[];
      for(let i=start;i<result.length && i<end;i++){
         pageResult.push(result[i]);
      }


      return [200, { success: true, error: '',
          data:{
            totalElements:result.length,
            content:pageResult
          } 
      }];
    });

    mock.onPost('shorturl/domain/add').reply(config => {
      let param=qs.parse(config.data);
      param.maxNum=parseInt(param.maxNum||'0');
      let model=Domains.find(u=>{
        return u.domain==param.domain;
      });

      if(model){
         if(model.invalid==1){
            return [200, { success: false, error: '域名已失效' }];
         }
         if(model.maxNum<param.maxNum){
           model.maxNum=param.maxNum;
         }
      }else{
        Domains.push({
          id:Domains.length+1,
          domain:param.domain,
          maxNum:param.maxNum,
          createdNum:0,
          invalid:0,
          ip:param.ip,
          createTime:new Date().format('yyyy-MM-dd HH:mm:ss')
        })
      }
      
      return [200, { success: true, error: '' }];
    });

    mock.onPost('shorturl/domain/invalid').reply(config => {
      let param=qs.parse(config.data);
      param.maxNum=parseInt(param.maxNum||'0');
      let model=Domains.find(u=>{
        return u.domain==param.domain;
      });

      if(model){
         model.invalid=1;
      }
      return [200, { success: true, error: '' }];
    });



    mock.onPost('/api/client/list').reply(config => {
      let param=qs.parse(config.data);
      let result=Clients.map(u=>{
         let model=JSON.parse(JSON.stringify(u));
         model.password=null;
         return model;
      });
      result=result.filter(u=>{
          let nameOk = param.searchName? u.fullName.indexOf(param.searchName)>-1 :true;
          let lockedOk= param.searchStatus ==''?true:u.locked==param.searchStatus;
          return nameOk && lockedOk;
      });
     
      param.pageIndex=parseInt(param.pageIndex||1)-1;
      param.pageSize=parseInt(param.pageSize||15);
      let start=param.pageIndex*param.pageSize;
      let end=start+param.pageSize;
      let pageResult=[];
      for(let i=start;i<result.length && i<end;i++){
         pageResult.push(result[i]);
      }


      return [200, { success: true, error: '',
          data:{
            totalElements:result.length,
            content:pageResult
          } 
      }];
    });

    
    mock.onPost('/api/client/save').reply(config => {
      let param=qs.parse(config.data);
      
      let model={};
      if(param.id){
        model=Clients.find(u=>{
          return u.id==param.id;
        });
        if(!model){
          return [200, { success: false, error: '找不到该记录' }];
        }
      }else{
        model.id=Clients.length+1;
        model.name=param.name;
        Clients.push(model);
      }
      model.fullName=param.fullName;
      model.mobile=param.mobile;
      model.createTime=model.createTime||new Date().format('yyyy-MM-dd HH:mm:ss');
      model.modifyTime=new Date().format('yyyy-MM-dd HH:mm:ss');
      model.password=model.password || '123456';
      model.locked=0;
      model.limitNum=param.limitNum;
      return [200, { success: true, error: '' }];
    });

    
    
    mock.onPost('/api/client/changeStatus').reply(config => {
      let param=qs.parse(config.data);
      let model=Clients.find(u=>{
        return u.id==param.id;
      });
      if(model){
        model.locked=param.status;
      }
      return [200, { success: true, error: '' }];
    });

    mock.onPost('/api/client/resetPass').reply(config => {
      let param=qs.parse(config.data);
      let model=Clients.find(u=>{
        return u.id==param.id;
      });
      if(model){
        model.password='123456';
      }
      return [200, { success: true, error: '' }];
    });

    mock.onPost('/api/client/changePass').reply(config => {
      let param=qs.parse(config.data);
      session.client.password=param.newPass;       
      return [200, { success: true, error: '' }];
    });


    mock.onPost('/api/template/list').reply(config=>{
       let param=qs.parse(config.data);
       if(session.loginType==1){
          param.searchClientId=session.client.id
       }
       
        let result=Templates.filter(u=>{
            let idOk = param.searchClientId == null || param.searchClientId =='' ? true : u.clientId==param.searchClientId;
            let statusOk = param.searchAuditStatus!=null ? param.searchAuditStatus==u.auditStatus :true;
            return idOk && statusOk;
        });
        
        param.pageIndex=parseInt(param.pageIndex||1)-1;
        param.pageSize=parseInt(param.pageSize||15);
        let start=param.pageIndex*param.pageSize;
        let end=start+param.pageSize;
        let pageResult=[];
        for(let i=start;i<result.length && i<end;i++){

            result[i].clientName=Clients.find(u=>u.id==result[i].clientId).fullName;
            pageResult.push(result[i]);
        }


        return [200, { success: true, error: '',
            data:{
              totalElements:result.length,
              content:pageResult
            } 
        }];       
    });

    mock.onPost('/api/template/save').reply(config => {
      let param=qs.parse(config.data);
      
      let model={};
      if(param.id){
        model=Templates.find(u=>{
          return u.id==param.id;
        });
        if(!model){
          return [200, { success: false, error: '找不到该记录' }];
        }
       
      }else{
        model.id=Templates.length+1;
        
        Templates.push(model);
      }
      model.clientId=session.client.id;
      model.content=param.content;
      model.auditStatus=0;
      model.createTime=new Date().format('yyyy-MM-dd HH:mm:ss');
      return [200, { success: true, error: '' }];
    });

    mock.onPost('/api/template/delete').reply(config => {
      let param=qs.parse(config.data);
      
      let ids=param.ids.split('@');


      ids.forEach(id=>{
          Templates.splice(Templates.findIndex(u=>u.id==id),1);
      })

      
      return [200, { success: true, error: '' }];
    });


    mock.onPost('/api/template/audit').reply(config => {
      let param=qs.parse(config.data);
      
      let ids=param.ids.split('@');
     
      ids.forEach(id=>{
          let model=Templates.find(u=>u.id==id);
          model.auditStatus=param.auditStatus;
      })

      
      return [200, { success: true, error: '' }];
    });


    mock.onPost('/api/signature/list').reply(config=>{
      let param=qs.parse(config.data);
      if(session.loginType==1){
        param.searchClientId=session.client.id
     }
       let result=Signatures.filter(u=>{
           let idOk = param.searchClientId == null || param.searchClientId =='' ? true : u.clientId==param.searchClientId;
           let statusOk = param.searchAuditStatus!=null ? param.searchAuditStatus==u.auditStatus :true;
           return idOk && statusOk;
       });
       
       param.pageIndex=parseInt(param.pageIndex||1)-1;
       param.pageSize=parseInt(param.pageSize||15);
       let start=param.pageIndex*param.pageSize;
       let end=start+param.pageSize;
       let pageResult=[];
       for(let i=start;i<result.length && i<end;i++){
           result[i].clientName=Clients.find(u=>u.id==result[i].clientId).fullName;
           pageResult.push(result[i]);
       }


       return [200, { success: true, error: '',
           data:{
             totalElements:result.length,
             content:pageResult
           } 
       }];       
   });

   mock.onPost('/api/signature/save').reply(config => {
     let param=qs.parse(config.data);
     
     let model={};
     if(param.id){
       model=Signatures.find(u=>{
         return u.id==param.id;
       });
       if(!model){
         return [200, { success: false, error: '找不到该记录' }];
       }
      
     }else{
       model.id=Signatures.length+1;
       
       Signatures.push(model);
     }
     model.clientId=session.client.id;
     model.content=param.content;
     model.auditStatus=0;
     model.createTime=new Date().format('yyyy-MM-dd HH:mm:ss');
     return [200, { success: true, error: '' }];
   });

   mock.onPost('/api/signature/delete').reply(config => {
     let param=qs.parse(config.data);
     
     let ids=param.ids.split('@');


     ids.forEach(id=>{
        Signatures.splice(Templates.findIndex(u=>u.id==id),1);
     })

     
     return [200, { success: true, error: '' }];
   });


   mock.onPost('/api/signature/audit').reply(config => {
     let param=qs.parse(config.data);
     
     let ids=param.ids.split('@');
    
     ids.forEach(id=>{
         let model=Signatures.find(u=>u.id==id);
         model.auditStatus=param.auditStatus;
     })

     
     return [200, { success: true, error: '' }];
   });


   mock.onPost('/api/task/list').reply(config=>{
      let param=qs.parse(config.data);
      if(session.loginType==1){
        param.searchClientId=session.client.id
     }
      let result=Tasks.filter(u=>{
          let idOk = param.searchClientId == null || param.searchClientId =='' ? true : u.clientId==param.searchClientId;
          let nameOk=param.searchName ?u.name.indexOf(param.searchName)>-1:true
          return idOk && nameOk;
      });
      
      param.pageIndex=parseInt(param.pageIndex||1)-1;
      param.pageSize=parseInt(param.pageSize||15);
      let start=param.pageIndex*param.pageSize;
      let end=start+param.pageSize;
      let pageResult=[];
      for(let i=start;i<result.length && i<end;i++){
          result[i].clientName=Clients.find(u=>u.id==result[i].clientId).fullName;
          pageResult.push(result[i]);
      }


      return [200, { success: true, error: '',
          data:{
            totalElements:result.length,
            content:pageResult
          } 
      }];       
  });

  mock.onPost('/api/task/save').reply(config => {
    let param=qs.parse(config.data);
    
    let model={};
    model.id=Tasks.length+1;    
    Tasks.push(model);
    model.clientId=session.client.id;
    model.name=param.name;
    model.template=param.template;
    model.signature=param.signature;
    model.targetUrl=param.targetUrl;
    model.createTime=new Date().format('yyyy-MM-dd HH:mm:ss');
    return [200, { success: true, error: '' }];
  });

    mock.onPost('/api/data/visit/list').reply(config=>{
        let param=qs.parse(config.data);
        if(session.loginType==1){
          param.searchClientId=session.client.id
      }
        let result=VisitRecords.filter(u=>{
            let idOk = param.searchClientId == null || param.searchClientId =='' ? true : u.clientId==param.searchClientId;
            let nameOk=param.searchTaskName ?u.taskName.indexOf(param.searchTaskName)>-1:true
            return idOk && nameOk;
        });
        
        param.pageIndex=parseInt(param.pageIndex||1)-1;
        param.pageSize=parseInt(param.pageSize||15);
        let start=param.pageIndex*param.pageSize;
        let end=start+param.pageSize;
        let pageResult=[];
        for(let i=start;i<result.length && i<end;i++){
            pageResult.push(result[i]);
        }


        return [200, { success: true, error: '',
            data:{
              totalElements:result.length,
              content:pageResult
            } 
        }];       
    });

  }
};