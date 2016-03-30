startTime.value=startDate;
endTime.value=endDate;

var store = new Ext.data.Store({
	url : 'impt-protect-task!getTaskList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "SYS_TASK_ID", "TASK_NAME", "TASK_DESCRIPTION", 
	     {name:"START_TIME",mapping:"START_TIME"}, 
	     {name:"END_TIME",mapping:"END_TIME"},
	     "CATEGORY","TARGET_TYPE",
	     "TASK_STATUS", "USER_NAME", 
	     {name:"CREATE_TIME",mapping:"CREATE_TIME"}]),
	listeners:{
	  	"exception": function(proxy,type,action,options,response,arg){
	  		Ext.Msg.alert(NOTICE_TEXT,"任务加载出错"+
				"<BR>Status:"+response.statusText||"unknow");
	  	}
	},
	sortInfo: {
	    field: 'START_TIME',
	    direction: 'ASC' // or 'DESC' (case sensitive for local sorting)
	}
});

// ************************* 任务信息列模型 ****************************
var searchStore = new Ext.data.Store({
	url : 'impt-protect-task!getTaskList.action',
	pageSize:10,
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "SYS_TASK_ID", "TASK_NAME", "TASK_DESCRIPTION", 
	     {name:"START_TIME",mapping:"START_TIME"}, 
	     {name:"END_TIME",mapping:"END_TIME"},
	     "CATEGORY","TARGET_TYPE",
	     "TASK_STATUS", "USER_NAME", 
	     {name:"CREATE_TIME",mapping:"CREATE_TIME"}]),
	listeners:{
	  	"exception": function(proxy,type,action,options,response,arg){
	  		Ext.Msg.alert(NOTICE_TEXT,"任务名加载出错"+
				"<BR>Status:"+response.statusText||"unknow");
	  	}
	}
});
var taskNameCombo=new Ext.form.ComboBox({
    id: 'taskName',
    width : 120,
    minListWidth: 220,
    store: searchStore,
    valueField: 'TASK_NAME',
    displayField: 'TASK_NAME',
    emptyText : '任务名模糊匹配',
    listEmptyText: '未找到匹配的结果',
    loadingText: '搜索中...',
    mode:'remote', 
    //minChars:1,  //输入几个字符开始搜索
    pageSize:searchStore.pageSize,
    queryDelay: 500,
    selectOnFocus: true,// 获得焦点时选中所有已输入文本
    //hideTrigger : true,
    typeAhead: false,
    autoSelect:false,
    enableKeyEvents : true,
    autoScroll:false,
	resizable: true,
    listeners : {
      beforeselect: function(combo,record,index){
        //search(record.get('nodeId'));
        //combo.collapse();
        //return false;
      },
      keypress: function(field, event) {
        field.setValue(field.getRawValue());
        /*if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
          search(field.getValue());
        }*/
      },
      beforequery:function(queryEvent){
        if(queryEvent.combo.lastQuery!=queryEvent.combo.getRawValue().trim()){
          queryEvent.combo.lastQuery=queryEvent.combo.getRawValue().trim();
          queryNodes(queryEvent.combo,queryEvent.combo.getRawValue());
        }
        queryEvent.combo.expand();
        return false;
      },
      scope : this
    }
});
function queryNodes(combo,gKey){
    searchStore.baseParams={
    	taskName: "%"+gKey+"%",
		limit: searchStore.pageSize
	};
	searchStore.load();
}
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect:true,header:""});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'SYS_TASK_ID',
		header : 'ID',
		dataIndex : 'SYS_TASK_ID',
		hidden : true,
		hideable : false,
		width : 60
	}, {
		id : 'TASK_NAME',
		header : '任务名称',
		dataIndex : 'TASK_NAME',
		width : 150
	}, {
		id : 'START_TIME',
		header : '开始时间',
		dataIndex : 'START_TIME',
		renderer : dateRenderer,
		width : 120
	}, {
		id : 'END_TIME',
		header : '结束时间',
		dataIndex : 'END_TIME',
		renderer : dateRenderer,
		width : 120
	}, {
		id : 'CATEGORY',
		header : '任务类别',
		dataIndex : 'CATEGORY',
		renderer : categoryRenderer,
		width : 60
	}, {
		id : 'TARGET_TYPE',
		header : '保障对象',
		dataIndex : 'TARGET_TYPE',
		width : 60
	}, {
		id : 'TASK_STATUS',
		header : '状态',
		dataIndex : 'TASK_STATUS',
		renderer : statusRenderer,
		width : 60
	}, {
		id : 'USER_NAME',
		header : '创建人',
		dataIndex : 'USER_NAME',
		width : 60
	}, {
		id : 'CREATE_TIME',
		header : '创建时间',
		dataIndex : 'CREATE_TIME',
		renderer : dateRenderer,
		width : 120
	}, {
		id : 'TASK_DESCRIPTION',
		header : '描述',
		dataIndex : 'TASK_DESCRIPTION',
		width : 250
	}]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	tbar:[
	    	"开始时间：从",startTime," 至",
	    	endTime,'-',/*'任务名：',*/taskNameCombo,'-',
	    	/*'任务状态:',*/statusCombo,'-',
	        {
		        text: '查询',
		        privilege:viewAuth,
		        icon:'../../../resource/images/btnImages/search.png',
		        handler: searchTask
    		},'-',
	       {
				xtype: 'splitbutton',
				text:'新增',
				privilege:addAuth, 
				icon:'../../../resource/images/btnImages/add.png',
				handler: function(b,e){b.showMenu()},
				menu:{
					items: [
					{
						text: '按设备',
						handler: function(b,e){
							editTask('新增','设备');
						}
					}, {
						text: '按电路',
						handler: function(b,e){
							editTask('新增','电路');
						}
					},{
						text: '按场馆',
						disabled:true,
						handler: function(b,e){
							editTask('新增','场馆');
						}
					}
					]
				}
			},
	        {
		        text: '删除',
		        privilege:delAuth,
		        icon:'../../../resource/images/btnImages/delete.png',
		        handler: function(){
		        	changeTaskStatus(3);
		        }
    		},
	        {
		        text: '修改',
		        privilege:modAuth,
		        icon:'../../../resource/images/btnImages/modify.png',
		        handler: function(b,e){
		        	preEditTask("修改");
				}
    		},{
		        text: '复制',
		        privilege:addAuth,
		        //icon:'../../../resource/images/btnImages/modify.png',
		        handler: function(b,e){
		        	preEditTask("复制")
				}
    		},'-',
	        {
		        text: '监测',
		        privilege:viewAuth,
		        //icon:'../../../resource/images/btnImages/wand.png',
		        handler: taskOperation
    		},{
		        text: '启动',
		        privilege:modAuth,
		        handler : function(){
		        	changeTaskStatus(4);
	        	}
    		},{
		        text: '停止',
		        privilege:modAuth,
		        handler : function(){
		        	changeTaskStatus(2);
	        	}
    		}
	    ]
});
function singleSelect(){
	var selections = gridPanel.getSelectionModel().getSelections();
	if(selections.length == 0){
	  Ext.Msg.alert("提示","请选择任务。");
	  return false;
	}else if(selections.length > 1){
	  Ext.Msg.alert("提示","只可以选择一个任务。");
	  return false;
	}else{
	  return selections[0];
	}
}
//查询任务
function searchTask() {
	var startTime =  Ext.getCmp("startTime").getValue();
	var endTime = Ext.getCmp("endTime").getValue();
	var status = Ext.getCmp("statusCombo").getValue();
	var taskName = "%"+Ext.getCmp("taskName").getValue()+"%";
	// 查询条件
	var jsonData = {
		"startTime" : startTime,
		"endTime" : endTime,
		"taskStatus" : status,
		"taskName" : taskName,
		"start" : 0,
		"limit" : 200
	};
	store.baseParams = jsonData;
	store.load();
}
function preEditTask(editType){
	var row=singleSelect();
	if(row){
		var taskId=row.get("SYS_TASK_ID");
		var taskType=row.get("TARGET_TYPE");
		var taskStatus=row.get("TASK_STATUS");
		if((taskStatus&(TASK_STATUS.WAITTING|TASK_STATUS.RUNNING))>0){
			Ext.Msg.alert("提示","任务在等待或进行中，请先停止");
			return;
		}
		editTask(editType,taskType,taskId)
	}
}
//按设备新增任务
function editTask(editType,taskType,taskId){
	var param={
		authSequence:authSequence,
		editType:editType,
		taskType:taskType,
		taskId:taskId
	}
	var addWindow=new Ext.Window({
        id:'addWindow',
        title:(editType=="复制"?"新增":editType)+'重保任务('+taskType+')',
        width:1900,
        height:1500,
        isTopContainer : true,
        shadow:false,
        autoScroll:true,
		html:'<iframe src = "editTask.jsp?'+Ext.urlEncode(param)+'" height="100%" width="100%" frameBorder=0 border=0/>'	     	
	});
 	addWindow.show();
	if (addWindow.getHeight() > Ext.getCmp('win').getHeight()) {
		addWindow.setHeight(Ext.getCmp('win').getHeight() * 0.8);
	} else {
		gridPanel.setHeight(addWindow.getInnerHeight());
	}
	if (addWindow.getWidth() > Ext.getCmp('win').getWidth()) {
		addWindow.setWidth(Ext.getCmp('win').getWidth() * 0.8);
	} else {
		gridPanel.setWidth(addWindow.getInnerWidth());
	}
	addWindow.center();
	addWindow.doLayout();
}

//启动1、停止2、删除3任务
function changeTaskStatus(taskStatus)
{
	var row=singleSelect();
	if(row){
      var taskId=row.get("SYS_TASK_ID");
      var status = row.get("TASK_STATUS");
      var END_TIME = row.get("END_TIME");
      
      //for(var i = 0; i< selectRecord.length;i++){
    	 /*if(selectRecord[i].get("STATUS") == "1" ||selectRecord[i].get("STATUS") == "2"){
           status = 1;
           break;
         }*/
         //taskIdList.push(selectRecord[i].get("SYS_TASK_ID"));
      //}
      if(taskStatus==3&&status == TASK_STATUS.RUNNING){
         Ext.Msg.alert("提示","任务在进行中，请先停止");
      }else if(taskStatus==4&&new Date()>new Date(END_TIME.time)){
         Ext.Msg.alert("提示","任务结束时间已到，如需启动，请修改结束时间");
      }else{
    	  function execute(taskId,taskStatus){
    		var jsonData = {
   		       "taskId":taskId,
   		       "taskStatus":taskStatus
   		    }
           	Ext.Ajax.request({
	   			url : 'impt-protect-task!changeTaskStatus.action',
	   			method : 'POST',
	   			params : jsonData,
	   			success : function(response) {
	   				var obj = Ext.decode(response.responseText);
	   				if(obj.returnResult==FAILED){
	   					Ext.Msg.alert("提示",obj.returnMessage);
	   				}
	   				store.reload();
	   			},
	   			error : function(response) {
	   				Ext.Msg.alert("提示",response.responseText);
	   			},
	   			failure : function(response) {
	   				Ext.Msg.alert("提示",response.responseText);
	   			}
   			});
    	  }
    	  var confirmtext;
    	  if(taskStatus==2)
    		  confirmtext="任务停止，系统将无法主动监控重保任务，推送相关信息。是否确认？";
    	  else if(taskStatus==3)
    		  confirmtext='任务删除后无法恢复，是否确认？';
    	  if(confirmtext){
    		  Ext.Msg.confirm('提示',confirmtext,
			  function(btn){
			  	if(btn=='yes'){
			  		execute(taskId,taskStatus);
			  	}
    		  });
    	  }else{
    		  execute(taskId,taskStatus);
    	  }
      }
   }
}

//任务操作
function taskOperation()
{
   var row=singleSelect();
   if(row){
   		 var taskId = row.get("SYS_TASK_ID");
   		 var taskType=row.get("TARGET_TYPE");
   		 var url = "../imptProtectManager/monitor/imptTaskMonitor.jsp?taskId="+taskId+"&taskType="+taskType;
   		 if(top.addTabPage)
   			 top.addTabPage(url, "重保任务监测"+"（"+row.get("TASK_NAME")+"）", authSequence);
   }
}

Ext.onReady(function() {
	document.onmousedown=function(){if(top!=this)top.Ext.menu.MenuMgr.hideAll();}
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();
	searchTask();
});
