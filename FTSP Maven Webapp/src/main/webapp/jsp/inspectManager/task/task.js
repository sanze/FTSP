/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

//指定provider
	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );

var limit = 200;
var store = new Ext.data.Store(
{
	url: 'inspect-task!getInspectTaskList.action',
	baseParams: {"limit":limit},
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
	    {name:"taskId",mapping:"SYS_TASK_ID"},
	    {name:"taskName",mapping:"TASK_NAME"},
	    {name:"description",mapping:"TASK_DESCRIPTION"},
		{name:"lastTime",mapping:"END_TIME"},
	//	{name:"completionTime",mapping:"COMPLETION_TIME"},
		{name:"nextTime",mapping:"NEXT_TIME"},
		{name:"taskState",mapping:"TASK_STATUS"},
		{name:"actionStatus",mapping:"RESULT"},
		{name:"user",mapping:"USER_NAME"}
    ])
});
store.load({
	
	callback: function(r, options, success){
		if(!success){
			var obj = Ext.decode(r.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});
 var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: limit,//每页显示的记录值
	store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
 });

 var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});
 var columnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	}),checkboxSelectionModel,{
            id: 'taskId',
            header: 'id',
            dataIndex: 'taskId',
            hidden:true
        },{
            id: 'taskName',
            header: '巡检任务名称',
            width:(10+12*15),
            dataIndex: 'taskName',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'description',
            header: '描述',
			width:(10+12*15),
            dataIndex: 'description',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'lastTime',
            header: '上次执行时间',
			width:(10+12*8),
            dataIndex: 'lastTime',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        //    renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s')
        },{
            id: 'nextTime',
            header: '下次执行时间',
			width:(10+12*8),
            dataIndex: 'nextTime',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
           // renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s')
        },{
            id: 'taskState',
            header: '任务状态',
			width:(10+12*8),
            dataIndex: 'taskState',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'actionStatus',
            header: '执行状态',
			width:(10+12*8),
            dataIndex: 'actionStatus',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'user',
            header: '创建人',
			width:(10+12*8),
            dataIndex: 'user',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }]
    });

var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	region:"center",
	stripeRows:true,
	autoScroll:true,
	frame:false,
	cm: columnModel,
	store:store,
	loadMask: true,
	clicksToEdit: 2,//设置点击几次才可编辑  
	selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox 
	viewConfig: {
        forceFit:false
    },
	bbar: pageTool, 
	tbar: [{
			xtype: 'label',
			text: '',
			width: 20
		},'-',{
            text: '新增',
            icon:'../../../resource/images/btnImages/add.png',
            privilege:addAuth,
            handler : function(){
				editTask(0,0);
        	}
        },'-',{
            text: '删除',
            icon:'../../../resource/images/btnImages/delete.png',
            privilege:delAuth,
            handler : function(){
			   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一或多个任务。");
			   }else{
			      var taskIdList = new Array();
			      var status = 0;
			      for(var i = 0; i< selectRecord.length;i++){
			         if(selectRecord[i].get("taskState") == "启用"){
			           status = 1;
			           break;
			         }
			         taskIdList.push(selectRecord[i].get("taskId"));
			      }
			      if(status == 1){
			         Ext.Msg.alert("错误","只能删除处于挂起状态的任务。");
			      }else{
			        // deleteTask(taskIdList);
			        var msg = '将会删除所选择的任务，但不会删除已经完成的巡检结果。</br></br>确认删除?';
			        changeInspectTaskStatus(taskIdList,msg,3);	
			      }
			      
			   }
			}
        },'-',{
            text: '修改',
            icon:'../../../resource/images/btnImages/modify.png',
            privilege:modAuth,
            handler : function(){
                var selections = gridPanel.getSelectionModel().getSelections();
                if(selections.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一个任务。");
			   }else if(selections.length > 1){
			      Ext.Msg.alert("提示","只可以选择一个任务。");
			   }else{
				editTask(selections[0].get("taskId"),1);
			   }
        	}
        },'-',{
            text: '查看',
            icon:'../../../resource/images/btnImages/application.png',
            privilege:viewAuth,
            handler : function(){
                var selections = gridPanel.getSelectionModel().getSelections();
                if(selections.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一个任务。");
			   }else if(selections.length > 1){
			      Ext.Msg.alert("提示","只能选择一个任务查看。");
			   }else{
				editTask(selections[0].get("taskId"),2);
			   }
        	}
        },'-',{
            text: '立即执行',
            icon:'../../../resource/images/btnImages/control_play_blue.png',
            privilege:actionAuth,
            handler : function(){
			   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一个任务。");
			   }else if(selectRecord.length > 1){
			      Ext.Msg.alert("提示","只可以选择一个任务进行立即执行。");
			      
			   }else{
			      startTask(selectRecord[0].get("taskId"));
			   }
			   }
        },'-',{
            text: '启用',
            icon:'../../../resource/images/btnImages/control_play.png',
            privilege:actionAuth,
            handler : function(){
              var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			  if(selectRecord.length == 0)
			  {
			     Ext.Msg.alert("提示","必须选择一或多个任务。");
			  }else{
			    var taskIdList = new Array();
			    for(var i = 0; i< selectRecord.length;i++){
			      taskIdList.push(selectRecord[i].get("taskId"));
			    }
               changeInspectTaskStatus(taskIdList,'任务启用。</br></br>确认？',1);
              }	
        	}
        },'-',{
            text: '挂起',
            icon:'../../../resource/images/btnImages/control_stop.png',
            privilege:actionAuth,
            handler : function(){
              var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			  if(selectRecord.length == 0)
			  {
			     Ext.Msg.alert("提示","必须选择一或多个任务。");
			  }else{
                var taskIdList = new Array();
			    for(var i = 0; i< selectRecord.length;i++){
			      taskIdList.push(selectRecord[i].get("taskId"));
			    }
                changeInspectTaskStatus(taskIdList,'任务挂起。将不会自动执行。</br></br>确认？',2);
              }
        	}
        },'-',{
            text: '执行情况',
            icon:'../../../resource/images/btnImages/setTask.png',
            privilege:viewAuth,
            handler : function(){
               var selections =gridPanel.getSelectionModel().getSelections(); 
			   if(selections.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一个任务。");
			   }else if(selections.length > 1){
			      Ext.Msg.alert("提示","只可以选择一个任务进行立即执行。");
			      
			   }else{
			      getTaskRunDetial(selections[0].get("taskId"));
			   }
        	}
        }
	]/*,
	plugins: [rightMenu]*/
}); 

function startTask(taskId){
Ext.Msg.confirm('提示','任务立即执行，巡检项目执行时间较长，期间可以打开巡检任务界面查询巡检情况。</br></br>确认立即执行?',
function(btn){
	if(btn=='yes'){
	  var jsonData = {
       "inspectTaskId":taskId
      }
	  Ext.Ajax.request({
	      url:'inspect-task!startTaskImmediately.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
			    	/*var obj = Ext.decode(response.responseText);
		         	Ext.Msg.alert("信息",obj.returnMessage);*/
		         	store.reload();
			    },
	      error:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	      },
	      failure:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	      }
     }) 
	
	}
});
}
//删除巡检任务
function deleteTask(taskIdList){
Ext.Msg.confirm('提示','删除巡检任务。将会删除此任务下所有设置，但不会删除已经完成的巡检结果。</br></br>确认删除?',
function(btn){
	if(btn=='yes'){
	  var jsonData = {
       "taskIdList":taskIdList
      }
	  Ext.Ajax.request({
	      url:'inspect-task!deleteInspectTask.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
			    	var obj = Ext.decode(response.responseText);
		         	Ext.Msg.alert("信息",obj.returnMessage);
		         	store.reload();
			    },
	      error:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	      },
	      failure:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	      }
     }) 
	}
});
}
//增加、查看、修改巡检任务
function editTask(inspectTaskId,type){
	var addWindow=new Ext.Window({
        id:'addWindow',
        title:'巡检任务设置',
        width:1000,
        height:500,
        isTopContainer : true,
        modal : true,
        autoScroll:true,
		//maximized:true,
        html:'<iframe src = "editTask.jsp?saveType='+type+'&inspectTaskId='+inspectTaskId+'" height="100%" width="100%" frameBorder=0 border=0/>' 
     });
    addWindow.show();
}
//任务执行状态：启用/挂起/删除
function changeInspectTaskStatus(taskIdList,msg,statusFlag){
Ext.Msg.confirm('提示',msg,
	function(btn){
		if(btn=='yes'){
		
		var jsonData = {
	       "taskIdList":taskIdList,
	       "statusFlag" :statusFlag
	    }
		
		Ext.Ajax.request({
			url : 'inspect-task!changeInspectTaskStatus.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
		        Ext.Msg.alert("信息",obj.returnMessage);
		        store.reload();
			},
			error : function(response) {
				Ext.Msg.alert("错误",response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("错误",response.responseText);
			}
		});
		}
});
}
//巡检任务执行情况
function getTaskRunDetial(inspectTaskId){

    var jsonData = {
      "inspectTaskId":inspectTaskId
    }
	var detailStore = new Ext.data.Store(
	{
	    url : 'inspect-task!getTaskRunDetial.action',
		baseParams:jsonData,
		reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
			root : "rows"
	    },[
		    {name:"inspectItem",mapping:"inspectItem"},
		    {name:"finishTime",mapping:"finishTime"},
		    {name:"status",mapping:"runStatus"},
		    {name:"itemsNum",mapping:"itemNum"},
		    {name:"description",mapping:"detialInfo"}
	    ])
	});
    detailStore.load({
		callback: function(r, options, success){
			if(!success){
				var obj = Ext.decode(r.responseText);
	    		Ext.Msg.alert("提示",obj.returnMessage);
			}
		}
	});
	 var detailCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :true});
	 var detailColumnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	//}),detailCheckboxSelectionModel,{
        }),{
            id: 'inspectItem',
            header: '巡检项目',
            width:(10+12*15),
            dataIndex: 'inspectItem',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'finishTime',
            header: '完成时间',
			width:(10+12*9),
            dataIndex: 'finishTime',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'status',
            header: '状态',
			width:(10+12*8),
            dataIndex: 'status',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'itemsNum',
            header: '项目数',
			width:(10+12*8),
            dataIndex: 'itemsNum',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        },{
            id: 'description',
            header: '描述',
			width:(10+12*15),
            dataIndex: 'description',
            editor: new Ext.form.TextField({
                allowBlank: false
            })
        }]
    });

	var detailGridPanel = new Ext.grid.EditorGridPanel({
		id:"detailGridPanel",
		region:"center",
		stripeRows:true,
		autoScroll:true,
		frame:false,
		cm: detailColumnModel,
		store:detailStore,
		loadMask: true,
		clicksToEdit: 2,//设置点击几次才可编辑  
		//selModel:detailCheckboxSelectionModel ,  //必须加不然不能选checkbox 
		viewConfig: {
	        forceFit:false
	    }
	})
  
var implementWindow=new Ext.Window({
      id:'implementWindow',
      title:'执行情况',
      width:800,
      height:445,
      isTopContainer : true,
      modal : true,
      autoScroll:true,
   //   html:'<iframe src = '+url+' height="100%" width="100%" frameBorder=0 border=0/>' ,
      layout:'border',
      items : [detailGridPanel],
      buttons: [{
		text: '确定',
		handler: function(b,e){
			b.findParentByType('window').close();
		}
	    }]
   });
implementWindow.show();
}

Ext.onReady(function(){

	Ext.Ajax.timeout=900000; 
	//collapse menu
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	Ext.Msg = top.Ext.Msg;
	
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [gridPanel]
	});
 });
 
 