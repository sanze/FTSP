startTime.value=startDate;
endTime.value=endDate;
//必填项*
startTime.sideText='<span style="color:red">*</span>';
endTime.sideText='<span style="color:red">*</span>';
categoryCombo.sideText='<span style="color:red">*</span>';
startTime.allowBlank=false;
endTime.allowBlank=false;
categoryCombo.allowBlank=false;

//全局变量定义
//==========================center=============================
var centerPanel = new Ext.Panel({
    id:'centerPanel',
    border:false,
    region:'center',
    autoScroll:true,
    layout:'border',
    items:[]
});
//==========================center=============================
var store;
var queryPanel;
var dataPanel;

/*-----------------------------------设 备---------------------------------------------*/
if(taskType=="设备"){
	//==================For the Tree====================
	var treeParams={
	  leafType:CommonDefine.TREE.NODE.NE//,
	    //checkModel:"single",
	    //onlyLeafCheckable:false
	};
	var treeurl="../../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
	
	queryPanel = new Ext.ux.EquipTreePanel({
		title:"",
		id:"queryPanel",
		region:"west",
		width: 250,
		autoScroll:true,
		boxMinWidth: 250,
	    boxMinHeight: 260,
		forceFit:true,
		collapsed: false,   // initially collapse the group
	    collapsible: false,
	    collapseMode: 'mini',
	    split:true,
	    leafType:CommonDefine.TREE.NODE.NE,
	    onGetChecked:onGetChecked
	    //html:'<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	
	function onGetChecked(getFunc){
		var pathParam="path"+CommonDefine.NameSeparator+"text";
		var result=getFunc(["nodeLevel","nodeId",pathParam],"top");
	    var reader = new Ext.data.ArrayReader({
	      fields : [
	        {name:'equipType',mapping:"nodeLevel"},
	        {name:'equipId',mapping:"nodeId"},
	        {name: 'equipFullName',mapping:pathParam}]
	    });
	    obj=reader.readRecords(result);
	    var Records=[];
	    for(i=0;i<obj.records.length;i++){
	      var recordIndex=store.findBy(function(record,id){
	        if( record.get('equipType')==obj.records[i].get('equipType')&&
	            record.get('equipId')==obj.records[i].get('equipId')){
	            return true;
	        }
	      });
	      if(recordIndex==-1){
	        Records.push(obj.records[i])
	      }
	    }
	    
	    store.add(Records);
	}
	
	//设备初始化加载
	store = new Ext.data.Store({
	  url: 'impt-protect-task!getTaskInfo.action',
	  reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
			root : "rows",
	        fields : [
	          {name:'equipType',mapping:"TARGET_TYPE"},
	          {name:'equipId',mapping:"TARGET_ID"},
	          {name: 'equipFullName',mapping:"DISPLAY_NAME"}]}),
	  listeners:{
	  	"exception": function(proxy,type,action,options,response,arg){
	  		Ext.Msg.alert(NOTICE_TEXT,"设备加载出错"+
				"<BR>Status:"+response.statusText||"unknow");
	  	}
	  }
	});
	
	var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
	var cm = new Ext.grid.ColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true
		},
		columns : [new Ext.grid.RowNumberer({
			width : 26
		}), checkboxSelectionModel, {
	            id: 'equipType',
	            header: 'type',
	            dataIndex: 'equipType',
	            hidden:true
	        },{
	            id: 'equipId',
	            header: 'id',
	            dataIndex: 'equipId',
	            hidden:true
	        },{
				id : 'equipFullName',
				header : '设备',
				dataIndex : 'equipFullName'
		}]
	});
	
	var dataPanel = new Ext.grid.GridPanel({
		id : "dataPanel",
		region : "center",
		title:'',
		cm : cm,
		store : store,
		stripeRows : true, // 交替行效果
		loadMask : true,
		selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
		viewConfig : {
			forceFit : true
		},
		tbar : 	["设备","->",{
			text : '删除',
			id:'deleteEquip',
			name:'deleteEquip',
			icon : '../../../resource/images/btnImages/delete.png',
			handler : function() {
			    var records = dataPanel.getSelectionModel().getSelections();
			    var len = records.length;
			    if(len <= 0){
			       Ext.Msg.alert("提示","请选择需要删除的设备！");
			    }else{
			       //for(var i = 0;i<len;i++ ){
			    	dataPanel.store.remove(records);
				   //}
			    }			
			}
		}]
	});
	centerPanel.add(dataPanel);
}else if(taskType=="电路"){
	var tbItemWidth=170;
	var tbFieldWidth=100;
	var circuitNoField={
		xtype : 'compositefield',
		width : tbItemWidth,
		items:[{
			xtype : 'label',
			text  : '电路编号：'
		},{
			xtype : 'numberfield',
			id : 'circuitNo',
			name : 'circuitNo',
			//fieldLabel : '电路编号',
			maxLength : 64,
			allowBlank : true,
			width : tbFieldWidth
		}]
	};
	var systemSourceNoField={
		xtype : 'compositefield',
		width : tbItemWidth,
		items:[{
			xtype : 'label',
			text  : '资源编号：'
		},{
			xtype : 'textfield',
			id : 'systemSourceNo',
			name : 'systemSourceNo',
			fieldLabel : '资源编号',
			maxLength : 64,
			allowBlank : true,
			width : tbFieldWidth
		}]
	};
	var circuitNameField={
		xtype : 'compositefield',
		width : tbItemWidth,
		items:[{
			xtype : 'label',
			text  : '路由名称：'
		},{
			xtype : 'textfield',
			id : 'circuitName',
			name : 'circuitName',
			//fieldLabel : '电路名称',
			maxLength : 64,
			allowBlank : true,
			width : tbFieldWidth
		}]
	};
	function queryCircuit(){
		var nodeIds=treeField.getCheckedNodes(["nodeId","nodeLevel"]);
		var nodes=null;
		if(nodeIds){
			for(var idx=0;idx<nodeIds.length;idx++){
				if(nodeIds[idx]["nodeLevel"]!=CommonDefine.TREE.NODE.NE){
					Ext.Msg.alert("提示","相关网元，只能勾选网元");
					return;
				}
				nodes=nodes+nodeIds[idx]["nodeId"]+"/";
			}
		}
		var params={
			authSequence:authSequence,
			connectRate : '',
			circuitState : '',
			serviceType : Ext.getCmp('serviceType').getValue(),
			linkId : null,
			circuitNo : Ext.getCmp('circuitNo').getValue(),
			systemSourceNo : Ext.getCmp('systemSourceNo').getValue(),
			sourceNo : null,
			aLocationId : -1,
			aLocationLevel : 0,
			flag : 2,
			nodes : nodes,
			nodeLevel : CommonDefine.TREE.NODE.NE,
			clientName : Ext.getCmp('clientName').getValue(),
			circuitName : Ext.getCmp('circuitName').getValue(),
			useFor : null,
			advancedCon : null,
			readOnly: true,
			singleSelect: true
		};
		var url="../../circuitManager/selectCircuitPortReasult.jsp?"+Ext.urlEncode(params);
		var html='<iframe id="circuit_panel" name="circuit_panel" src ="'+url+'" height="100%" width="100%" frameBorder=0 border=0/>';
		var tarPanel=Ext.getCmp('circuitPanel');
		if(tarPanel)
			tarPanel.update(Ext.DomHelper.markup(html));
	}
	function svcType2targetType(v){
		if (v == 1)
			return CommonDefine.TASK_TARGET_TYPE.SDH_CIRCUIT;
		if (v == 2)
			return CommonDefine.TASK_TARGET_TYPE.ETH_CIRCUIT;
		if (v == 3)
			return CommonDefine.TASK_TARGET_TYPE.WDM_CIRCUIT;
	}
	function addCircuit(){
		var queryCircuitGridPanel=null;
		var iframe = window.frames["circuit_panel"] || window.frames[0];
		// 兼容不同浏览器的取值方式
		if (iframe.gridPanel) {
			queryCircuitGridPanel=iframe.gridPanel;
		} else {
			queryCircuitGridPanel=iframe.contentWindow.gridPanel;
		}
		var records = queryCircuitGridPanel.getSelectionModel().getSelections();
	    var len = records.length;
	    if(len <= 0){
	    	Ext.Msg.alert("提示","请选择需要添加的电路！");
	    }else{
	    	var Records=[];
		    for(i=0;i<records.length;i++){
		      var recordIndex=store.findBy(function(record,id){
		        if( record.get('equipType')==svcType2targetType(records[i].get('svc_type'))&&
		            record.get('equipId')==records[i].get('CIR_CIRCUIT_ID')){
		            return true;
		        }
		      });
		      if(recordIndex==-1){
		    	  var record=records[i].copy();
		    	  record.set('equipType',svcType2targetType(record.get('svc_type')));
		    	  record.set('equipId',record.get('CIR_CIRCUIT_ID'));
		    	  Records.push(record)
		      }
		    }
		    store.add(Records);
	    }
	}
	function delCircuit(){
		var records = dataPanel.getSelectionModel().getSelections();
	    var len = records.length;
	    if(len <= 0){
	    	Ext.Msg.alert("提示","请选择需要删除的电路！");
	    }else{
	    	dataPanel.store.remove(records);
	    }
	}
	function emptyCircuit(){
		store.removeAll();
	}
	var queryButton={
		xtype : 'button',
		id : 'query',
		name : 'query',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler: queryCircuit
	};
	var clientNameField={
		xtype : 'compositefield',
		width : tbItemWidth,
		items:[{
			xtype : 'label',
			text  : '客户名称：'
		},{
			xtype : 'textfield',
			id : 'clientName',
			name : 'clientName',
			fieldLabel : '客户名称',
			maxLength : 64,
			allowBlank : true,
			width : tbFieldWidth
		}]
	};
	var serviceTypeStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'value'
		}, {
			name : 'displayName'
		} ],
		data : [ [ '1', 'SDH' ], [ '2', '以太网' ], [ '3', 'OTN' ] ]
	});
	var serviceTypeField={
		xtype : 'compositefield',
		width : tbItemWidth,
		items:[{
			xtype : 'label',
			text  : '业务类型：'
		},{
			xtype: 'combo',
			id : 'serviceType',
			name : 'serviceType',
			//fieldLabel : '业务类型',
			store : serviceTypeStore,
			displayField : "displayName",
			valueField : 'value',
			triggerAction : 'all',
			value : '1',
			mode : 'local',
			editable : false,
			allowBlank : false,
			width : tbFieldWidth
		}]
	};
	var treeField=new Ext.ux.EquipTreeCombo({
		rootVisible: false,
		width: tbFieldWidth,
		leafType: CommonDefine.TREE.NODE.NE,
		checkModel:"multiple"
	});
	var relateNeField={
		xtype : 'compositefield',
		width : tbItemWidth,
		items:[{
			xtype : 'label',
			text  : '相关网元：'
		},treeField]
	};
	var cleanQueryButton={
		xtype : 'button',
		id : 'cleanQuery',
		name : 'cleanQuery',
		text : '清空',
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler: function(){
			Ext.getCmp('circuitNo').reset();
			Ext.getCmp('systemSourceNo').reset();
			Ext.getCmp('clientName').reset();
			Ext.getCmp('circuitName').reset();
			Ext.getCmp('serviceType').reset();
			treeField.reset();
		}
	};
	var tbar = {
		layout : 'form',
		items : [
	         {xtype:'toolbar',items:[circuitNoField,systemSourceNoField,circuitNameField,'->',queryButton]},
	         {xtype:'toolbar',items:[clientNameField,serviceTypeField,relateNeField,'->',cleanQueryButton]}
        ]
	};
	queryPanel = new Ext.Panel({
		title:"查询条件",
		id:"queryPanel",
		region:"north",
		//width: 250,
		height: 300,
		autoScroll:true,
		//boxMinWidth: 250,
	    //boxMinHeight: 250,
		forceFit:true,
		collapsed: false,   // initially collapse the group
	    collapsible: false,
	    collapseMode: 'mini',
	    split:true,
	    tbar:tbar,
	    layout:'fit',
	    items:[{
	    	//title: "待选电路",
	    	xtype:'panel',
	    	id:"circuitPanel",
	    	collapsible: false,
	    	border: false,
	    	tbar:[
	    	      '<span style="font-weight:bold">待选电路</span>',
	    	      {
	    	    	  text:'新增',
	    	    	  icon : '../../../resource/images/btnImages/add.png',
	    	    	  handler : addCircuit
	    	      }
	    	],
		    listeners:{
//		    	'afterrender':queryCircuit
		    }
	    }]
	});
	store = new Ext.data.Store({
		url: 'impt-protect-task!getTaskInfo.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [  {name:'equipType',mapping:"TARGET_TYPE"},
	          {name:'equipId',mapping:"TARGET_ID"},
	            "CIR_CIRCUIT_ID", "cir_no", "source_no", "svc_type", "client_name",
				"a_end_ctp", "z_end_ctp", "a_end_port", "z_end_port", "rate",
				"cir_name", "a_end_user_name", "z_end_user_name",
				"IS_COMPLETE_CIR", "a_end_ne", "a_end_ems", "a_end_ems_group",
				"z_end_ne", "z_end_ems", "Z_end_ems_group", "A_CTP_ID", "Z_CTP_ID",
				"USED_FOR", "CIR_CIRCUIT_INFO_ID" ]),
		listeners:{
		  	"exception": function(proxy,type,action,options,response,arg){
		  		Ext.Msg.alert(NOTICE_TEXT,"设备加载出错"+
					"<BR>Status:"+response.statusText||"unknow");
		  	}
	    }
	});
	var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
	var cm = new Ext.ux.grid.LockingColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true
		// columns are not sortable by default
		},
		stateId : 'imptCirListGrid',//保持列锁定状态
		columns : [ new Ext.grid.RowNumberer({
			width:26,
			locked : true
		}), checkboxSelectionModel, {
			id : 'circuitNo',
			header : '电路编号',
			dataIndex : 'cir_no',
			width : 100
		}, {
			id : 'systemSourceNo',
			header :  '资源编号',
			dataIndex : 'source_no',
			width : 100
		}, {
			id : 'circuitType',
			header : "业务类型",
			dataIndex : 'svc_type',
			width : 100,
			renderer : function(v) {
				if (v == 1) {
					return "SDH电路";
				}
				if (v == 2)
					return "以太网电路";
				if (v == 3)
					return "WDM电路";
			}
		}, {
			id : 'ane',
			header : 'A端网元',
			dataIndex : 'a_end_ne',
			width : 100

		}, {
			id : 'aport',
			header : 'A端端口',
			dataIndex : 'a_end_port',
			width : 100

		}, {
			id : 'actp',
			header : 'A端时隙',
			dataIndex : 'a_end_ctp',
			width : 100

		}, {
			id : 'zne',
			header : 'Z端网元',
			dataIndex : 'z_end_ne',
			width : 100

		}, {
			id : 'zport',
			header : 'Z端端口',
			dataIndex : 'z_end_port',
			width : 100

		}, {
			id : 'zctp',
			header : 'Z端时隙',
			dataIndex : 'z_end_ctp',
			width : 100

		}, {
			id : 'rate',
			header : '电路速率',
			dataIndex : 'rate',
			width : 100,
			listeners : {
				beforerender : function() {
					if (serviceType == 3) {
						Ext.getCmp('rate').hide();
					}
				}
			}
		}, {
			id : 'type',
			header : '电路类别',
			dataIndex : 'IS_COMPLETE_CIR',
			width : 100,
			renderer : function(v) {
				if (v == 0)
					return "不完整";
				if (v == 1)
					return "完整";
			}

		}, {
			id : 'circuitName',
			header :'路由名称',
			dataIndex : 'cir_name',
			width : 100
		}, {
			id : 'clientName',
			header : '客户名称',
			dataIndex : 'client_name',
			width : 100
		}, {
			id : 'usedFor',
			header : '用途',
			dataIndex : 'USED_FOR',
			width : 100
		}, {
			id : 'AEndUserName',
			header :'A端用户',
			dataIndex : 'a_end_user_name',
			width : 100
		}, {
			id : 'ZEndUserName',
			header :'Z端用户',
			dataIndex : 'z_end_user_name',
			width : 100
		}, {
			id : 'AEMS',
			header : 'A端所属网管',
			dataIndex : 'a_end_ems',
			hidden:true,
			width : 100
		}, {
			id : 'AEMSGroup',
			header : 'A端所属网管分组',
			dataIndex : 'a_end_ems_group',
			hidden:true,
			width : 100
		}, {
			id : 'ZEMS',
			header : 'Z端所属网管',
			dataIndex : 'z_end_ems',
			hidden:true,
			width : 100
		}, {
			id : 'ZEMSGroup',
			header : 'Z端所属网管分组',
			dataIndex : 'Z_end_ems_group',
			hidden:true,
			width : 100
		} ]
	});
	
	var dataPanel = new Ext.grid.GridPanel({
		id : "dataPanel",
		region : "center",
		stateId : 'imptCirListGrid', //注意！！！这个ID不能与其他页面的重复
		stateful : true, 
		//title:'',
		cm : cm,
		store : store,
		stripeRows : true, // 交替行效果
		loadMask : true,
		selModel : checkboxSelectionModel,
		view : new Ext.ux.grid.LockingGridView(),
	    tbar:[
	          '<span style="font-weight:bold">已选电路</span>',
	          {
	        	  text:'删除',
        		  icon : '../../../resource/images/btnImages/delete.png',
        		  handler: delCircuit
	          },{
	        	  text:'清空',
        		  icon : '../../../resource/images/btnImages/bin_empty.png',
        		  handler: emptyCircuit
	          }
	    ]
	});
	centerPanel.add(dataPanel);
}else if(taskType=="场馆"){
	
}

/*-----------------------------------------------操作权限组-----------------------------------------*/
var privilegeStore = new Ext.data.Store({
	url : 'inspect-task!getPrivilegeGroupList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [
         {name:"groupId",mapping:"SYS_USER_GROUP_ID"},
         {name:"groupName",mapping:"GROUP_NAME"}
      ]),
    listeners:{
    	"exception": function(proxy,type,action,options,response,arg){
    		Ext.Msg.alert(NOTICE_TEXT,"用户组加载出错"+
				"<BR>Status:"+response.statusText||"unknow");
    	}
    }
});

var privilegeCKSM = new Ext.grid.CheckboxSelectionModel();
var privilegeCM = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}), privilegeCKSM, {
		id : 'groupId',
		header : 'ID',
		dataIndex : 'groupId',
		width : 100,
		hidden : true
	},{
		id : 'groupName',
		header : '组名',
		dataIndex : 'groupName'
	}]
});
var privilegeGrid = new Ext.grid.GridPanel({
	id : "privilegeGrid",
	title:'',
	height:150,
	cm : privilegeCM,
	sm : privilegeCKSM,
	store : privilegeStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : privilegeCKSM, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	}
});
/*-----------------------------------------------巡检任务详细信息-----------------------------------------*/
var eastPanel = new Ext.form.FormPanel({
	title:"",
	id:"eastPanel",
	region:"east",
	bodyStyle:'padding:10px 15px 10px 15px;',
	width: 280,
	autoScroll:true,
    collapsible: false,
	labelWidth: 60,
//	labelAlign: 'right',
	buttons: [{
        text: '确定',
        id:'ok',
        name:'ok',
        handler: function(){
        	saveTask(true);
		}
     },{
        text: '取消',
        id:'cancel',
        name:'cancel',
        handler: function(){
            // 关闭修改任务信息窗口
			var window = parent.Ext.getCmp('addWindow');
			if (window) {
				window.close();
			}
        }
    },{
        text: '应用',
        id:'apply',
        name:'apply',
        hidden:true,
        handler: function(){
        	saveTask(false);
        }
    },{xtype:'label',width:'15px'}],
	defaults: {
		anchor: '95%',
		labelStyle:"margin-bottom:10px;",
		style:"margin-bottom:10px;"
	},
	items:[{
		xtype: 'hidden',
		fieldLabel: '任务ID',
		disabled: true
	},{
		xtype: 'textfield',
		id:"taskName",
		name:"taskName",
		fieldLabel: '任务名称',
		allowBlank: false,
		maxLength: 64,
        blankText: "必填",
        //validationEvent: 'blur',
        invalidText: '任务名重复',
        sideText: '<span style="color:red">*</span>',
      //  validateOnBlur: true,
      //  validationDelay:2000,
      //  validateOnChange: false,
        validator:function(val){
        	if(val==undefined||val==null||val==''){
        		return false;
        	}
			var validator = this;
			Ext.Ajax.request({
			 url:'impt-protect-task!checkTaskNameExist.action',
			 method:'post',
			 scope: validator,
			 params:{'taskName':val,
			         'taskId':taskId},
			 success:function(response,opts){
				 var obj=Ext.decode(response.responseText);
				 if(obj.exit){
					 this.markInvalid("有相同名称的重保任务。名称不可重复。");
					 Ext.Msg.alert("提示","有相同名称的重保任务。名称不可重复。");
				 }else{
					 this.clearInvalid();
				 }
			 },
			 failure : function(response) {
				 this.markInvalid();
				 Ext.Msg.alert(NOTICE_TEXT, "任务名校验出错"+
				 	"<BR>Status:"+response.statusText||"unknow");
			 },
			 error : function(response) {
				 this.markInvalid();
				 Ext.Msg.alert(NOTICE_TEXT, "任务名校验出错"+
					"<BR>Status:"+response.statusText||"unknow");
			 }
			});
		}
	},{
		xtype: 'textarea',
		id:"taskDescription",
		name:"taskDescription",
		fieldLabel: '任务描述',
		maxLength: 128
	},startTime,endTime,categoryCombo,
	{
		xtype: 'combo',
		fieldLabel: "异常信息<br>短信提醒",
		disabled: true
	},{
		xtype: 'checkboxgroup',
		id:"taskItem",
		name:"taskItem",
		fieldLabel: '异常信息<br>内容定制',
		disabled: true,
		// Distribute controls across 3 even columns, filling each row
		// from left to right before starting the next row
		columns: 2,
		items: [
			{boxLabel: '紧急告警', id:'1', name: '1', checked: false},
			{boxLabel: '保护倒换事件', id:'2', name: '2', checked: false},
			{boxLabel: '重要告警', id:'3', name: '3', checked: false},
			{boxLabel: '设备性能越限事件', id:'4', name: '4', checked: false},
			{boxLabel: '次要告警', id:'5', name: '5', checked: false},
			{boxLabel: '采集性能越限事件', id:'6', name: '6', checked: false},
			{boxLabel: '提示告警', id:'7', name: '7', checked: false}
		]
	},{
		xtype: 'fieldset',
		title: '操作权限组',
		items:[privilegeGrid]
	}]
});

/*var setWindow=new Ext.Window({
        id:'setWindow',
        title:'用户选择',
        isTopContainer : true,
        modal : true,
        autoScroll:true,
        closeAction:'hide',
		width: 250,
        items: [],
		buttons: [{
			text: '确定',
			handler: function(){
			   
			}
		 },{
			text: '取消',
			handler: function(){
			    setWindow.hide();
			}
		}]
     });*/

//==========================编辑巡检任务=============================
function saveTask(closeOrNot){
    if(!eastPanel.getForm().isValid()){
        Ext.Msg.alert("提示","参数不合法。");
    }else{
    	var taskName = Ext.getCmp('taskName').getValue();
        Ext.Ajax.request({
			 url:'impt-protect-task!checkTaskNameExist.action',
			 method:'post',
			 params:{'taskName':taskName,
			         'taskId':taskId},
			 success:function(response,opts){
				 var obj=Ext.decode(response.responseText);
				 if(obj.exit){
				   Ext.Msg.alert("提示","有相同名称的重保任务。名称不可重复。");
				 }else{
				   addTask(closeOrNot);
				 }
			 },
			 failure : function(response) {
				 Ext.Msg.alert(NOTICE_TEXT, "任务名校验出错"+
				 	"<BR>Status:"+response.statusText||"unknow");
			 },
			 error : function(response) {
				 Ext.Msg.alert(NOTICE_TEXT, "任务名校验出错"+
					"<BR>Status:"+response.statusText||"unknow");
			 }
        });
    }
}
function addTask(closeOrNot){
	if(editType == "查看"){
	   // 关闭修改任务信息窗口
		var window = parent.Ext.getCmp('addWindow');
		if (window) {
			window.close();
		}
	}else{
	   //设备
	   var equipList = null;
	   //var equipNameList = null;
	   if(store.getCount() != 0){
		   equipList = new Array();
		   equipList = new Array();
	     for(var i=0; i<store.getCount();i++){
	    	 equipList.push(store.getAt(i).get("equipType")+"_"+store.getAt(i).get("equipId"));
	    	 //equipNameList.push(store.getAt(i).get("equipFullName")); 
	     }
	   }else{
		   Ext.Msg.alert("提示","重保设备必选。");
		   return;
	   }
	   
	
	  // var engineerId = Ext.getCmp('engineerID').getValue();
	   var taskName = Ext.getCmp('taskName').getValue();
	   var taskDescription = Ext.getCmp('taskDescription').getValue();
	   
	   //巡检项目取值 
		/*var myCheckBoxGroup = Ext.getCmp('taskItem');
		var inspectItemList = new Array();
	    for (var i = 0; i < myCheckBoxGroup.items.length; i++)    
	    {    
	        if (myCheckBoxGroup.items.itemAt(i).checked)    
	        {    
	          //  alert(myCheckBoxGroup.items.itemAt(i).name); 
	            inspectItemList.push(myCheckBoxGroup.items.itemAt(i).name);                   
	        }    
	    } */
	
	   //是否挂起
	   /*var handUp = Ext.getCmp('handUp').checked;
	   if(handUp == true){
	     handUp = 2;
	   }else{
	     handUp = 1;
	   }*/
	   
	   //开始时间、下次执行时间
		var startTime = Ext.getCmp('startTime').getValue();
		var endTime = Ext.getCmp('endTime').getValue();
	   
	   var category = Ext.getCmp('categoryCombo').getValue();

	   //操作权限组
	   var selections = privilegeGrid.getSelectionModel().getSelections();
	   var privilegeList = new Array();
	   /*if(selections.length == 0){
	       Ext.Msg.alert('信息','请选择操作权限组！');
	       return;
	   }*/
	   for(var i = 0; i < selections.length; i++){
	      privilegeList.push(selections[i].get("groupId"));
	   }
	   
	   var jsonData = {
	       "taskId":taskId,
	       "taskName":taskName,
	       "taskDescription":taskDescription,
	       //"inspectItemList":inspectItemList,
	       "startTime":startTime,
	       "endTime":endTime,
	       "category":category,
	       "taskType":taskType,
	       //"handUp":handUp,
	       "privilegeList":privilegeList,
	       "equipList":equipList//,
	       //"inspectEquipNameList":inspectEquipNameList,
	       //"privilegeParamId":privilegeParamId//,
	       //"inspectItemParamId":inspectItemParamId
	   }
	   
	   var url = 'impt-protect-task!editTask.action';
	   /*if(editType == "新增"){
		   url = 'impt-protect-task!editTask.action';
	   }else{
		   url = 'impt-protect-task!updateTask.action';
	   }*/
	   
	   Ext.Ajax.request({
	      url:url,
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if(obj.returnResult == FAILED){
	    		Ext.Msg.alert("信息",obj.returnMessage);
	        }else{
	        	if(obj['SYS_TASK_ID'])
	        		taskId=obj['SYS_TASK_ID'];
	         	if(closeOrNot){
	         		//Ext.Msg.alert("信息","新增重保任务成功",function(btn){
		         	    // 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
		         	    // 关闭修改任务信息窗口
						var window = parent.Ext.getCmp('addWindow');
						if (window) {
							window.close();
						}
	         	    //});
	         	}else{
	         		//Ext.Msg.alert("信息","新增重保任务成功",function(btn){
		         	    // 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
	         	    //});
	         	}
	     	}
	    },
	    failure : function(response) {
			 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
			 	"<BR>Status:"+response.statusText||"unknow");
		 },
		 error : function(response) {
			 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
				"<BR>Status:"+response.statusText||"unknow");
		 }
	   });
	}
}


//----------------------------save handler for use--------------------------------

//-----------------------------initial----------------------------------
function initData(){
	function loadData(){
	    if(editType == "新增"){
	      Ext.Ajax.request({
		      url:'inspect-task!getCurrentUserGroup.action',
		      method:'Post',
		      //params:jsonData,
		      success: function(response) {
	              var obj = Ext.decode(response.responseText);
	              var checkedGroupList = obj.checkedGroupList;
	              var rowIdList = new Array();
	              for(var i=0; i<privilegeStore.getCount();i++){
			    	  var groupId = privilegeStore.getAt(i).get("groupId");
				      for(var j=0; j<checkedGroupList.length;j++){
				         if(checkedGroupList[j].SYS_USER_GROUP_ID == groupId){
				            rowIdList.push(i);
				         }
				      }
				  }
				  privilegeGrid.getSelectionModel().selectRows(rowIdList);
		      },
		      failure : function(response) {
				 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
				 	"<BR>Status:"+response.statusText||"unknow");
			 },
			 error : function(response) {
				 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
					"<BR>Status:"+response.statusText||"unknow");
			 }
		  });
	    }else{
	    	var jsonData = {
	    		"taskId":taskId
	    	};
	    	if(editType=="复制"){
	  		  taskId=null;
	  	  	}
	    	store.load({params:jsonData});
		    Ext.Ajax.request({
		      url:'impt-protect-task!getTask.action',
		      method:'Post',
		      params:jsonData,
		      success: function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	if(obj.returnResult==FAILED){
		    		Ext.Msg.alert(NOTICE_TEXT, obj.returnMessage);
		    	}else{
			    	var task = obj;
			    	Ext.getCmp('taskName').setValue(editType=="复制"?("复制 "+task.TASK_NAME):task.TASK_NAME);
			    	Ext.getCmp('taskDescription').setValue(task.TASK_DESCRIPTION);
			    	Ext.getCmp('startTime').setValue(dateRenderer(task.START_TIME));
			    	Ext.getCmp('endTime').setValue(dateRenderer(task.END_TIME));
			    	
			    	/*var hangUp = task.TASK_STATUS;
			    	if(task.TASK_STATUS == 2){
			    	  Ext.getCmp('handUp').setValue(true);
			    	}else{
			    	  Ext.getCmp('handUp').setValue(false);
			    	}*/
			    	
			    	Ext.getCmp('categoryCombo').setValue(task.CATEGORY);
			    	//权限组加载
			    	var privilegeList = Ext.util.JSON.decode(task.privilegeList);
			    	var rowIdList = new Array();
			    	
			    	//privilegeParamId = obj.privilegeParamId;
			    	for(var i=0; i<privilegeStore.getCount();i++){
			    	  var groupId = privilegeStore.getAt(i).get("groupId");
				      for(var j=0; j<privilegeList.length; j++){
				       if(privilegeList[j] == groupId){
				         rowIdList.push(i);
				        }
				      }
				    }
				    privilegeGrid.getSelectionModel().selectRows(rowIdList);
		    	}
		    },
		    failure : function(response) {
				 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
				 	"<BR>Status:"+response.statusText||"unknow");
			 },
			 error : function(response) {
				 Ext.Msg.alert(NOTICE_TEXT, "出错啦~~~"+
					"<BR>Status:"+response.statusText||"unknow");
			 }
		  });
		}
	}
    privilegeStore.load({callback:loadData});
}

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	if(editType == "修改"){
		//westPanel=null;
		//Ext.getCmp('ok').setDisabled(true);
		Ext.getCmp('apply').setVisible(true);
		/*Ext.getCmp('cancel').setVisible(false);
		//Ext.getCmp('handUp').setVisible(false);
		//Ext.getCmp('setButton').setVisible(false);
		Ext.getCmp('deleteEquip').setDisabled(true);
      
		Ext.getCmp('taskName').setDisabled(true);
		Ext.getCmp('taskDescription').setDisabled(true);
		//Ext.getCmp('taskItem').setDisabled(true);
		Ext.getCmp('privilegeGrid').setDisabled(true);*/
    }
	centerPanel.add(queryPanel);
	var win = new Ext.Viewport({
		id:'win',
		loadMask : true,
		layout: 'border',
		items : [centerPanel,eastPanel],
		renderTo : Ext.getBody()
	});
 	initData();
});