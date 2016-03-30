var storeCond,storeAct,applyEmsIds,applyEmsIdsStr="",eqStr="";
var diagnoseIdGlobal = 0;
var statusMapping = [ [ 1, '启用' ], [ 2, '挂起' ]];  
var store = new Ext.data.Store({ 
	url:'fault-diagnose!getFaultDiagnoseRules.action',
	baseParams : { 
		'limit':200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["DIAGNOSE_ID","RULE_NAME","MAIN_ALARM","EMS_NAME","EQUIPMENT_NAME",
	    "STATUS","DESCRIPTION","MODIFIER",
	    "UPDATE_TIME_STR"
	   ])
});  
store.load({
	callback : function(records, options, success){  
		if(success){
			gridPanel.getEl().unmask();
//			if(records.length == 0){
//				Ext.Msg.alert("信息","查询结果为空！");
//			}
		}else{ 
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误",'查询失败，请重新查询！');
		}
	}
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect:false
    }); 

var cm = new Ext.grid.ColumnModel({ 
	defaults : {
		sortable : false 
	},
	columns : [
		new Ext.grid.RowNumberer({
			width : 26
		}),checkboxSelectionModel,
		{
			id : 'RULE_NAME',
			header : '规则名称',
			dataIndex : 'RULE_NAME',
			width : 160
		},{
			id : 'MAIN_ALARM',
			header : '主告警名称',
			dataIndex : 'MAIN_ALARM',
			width : 150 
		},{
			id : 'EMS_NAME',
			header : '适用范围',
			dataIndex : 'EMS_NAME',
			width : 190
		},{
			id : 'EQUIPMENT_NAME',
			header : '适用设备',
			dataIndex : 'EQUIPMENT_NAME',
			width : 200
		}, {
			id : 'STATUS',
			header : '启用状态',
			dataIndex : 'STATUS',
			width : 80,
			renderer : function(v){
				return (statusMapping[v-1] != null) ? statusMapping[v-1][1] : v;
			}
		} , {
			id : 'DESCRIPTION',
			header : '描述',
			dataIndex : 'DESCRIPTION',
			width : 200 
		} , {
			id : 'MODIFIER',
			header : '最近修改者',
			dataIndex : 'MODIFIER',
			width : 120
		}, {
			id : 'UPDATE_TIME_STR',
			header : '最近修改时间',
			dataIndex : 'UPDATE_TIME_STR',
			width : 120
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
 
var gridPanel = new Ext.grid.GridPanel({
	id : 'gridPanel',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stripeRows : true,
	selModel : checkboxSelectionModel, 
	tbar : [ '-',{
		text : '新增',
		icon : '../../resource/images/btnImages/add.png',
		privilege : addAuth,
		disabled : true,
		handler : function(){ 
//			editConvergeRules(0);
		}
	},{
		text : '删除',
		icon : '../../resource/images/btnImages/delete.png',
		disabled : true,
		privilege : delAuth
//		handler : deleteConvergeRules
	},{
		text : '修改',
		icon : '../../resource/images/btnImages/modify.png',
		id : 'modifyRuleBtn',
		disabled : true,
		privilege : modAuth,
		handler :function (){ 
			editFaultDiagnoseRules(1);
		}
	} ,'-',{
		text : '详情',
		icon : '../../resource/images/btnImages/information.png',
		privilege : viewAuth,
		handler : function (){ 
			editFaultDiagnoseRules(2);
		}
	},'-',{
		text : '启用',
		icon:'../../resource/images/btnImages/control_play.png',
		id : 'startRuleBtn',
		disabled : true,
		privilege : modAuth,
		handler : function (){ 
			changeFaultDiagnoseRuleStatus(1);
		}
	},{
		text : '挂起',
		icon :'../../resource/images/btnImages/control_stop.png',
		id : 'holdRuleBtn',
		disabled : true,
		privilege : modAuth,
		handler : function (){ 
			changeFaultDiagnoseRuleStatus(2);
		}
	},'-',{
		text : '手动执行',
		icon : '../../resource/images/btnImages/information.png',
		id : 'manualRuleBtn',
		disabled : true,
		privilege : actionAuth,
		handler : manualActionRules
	},'-',{
		text : '故障诊断参数',
		icon : '../../resource/images/btnImages/information.png',
		privilege : modAuth,
		handler : setFaultDiagnoseParam
	}
	],
	bbar : pageTool
});  

//故障诊断参数设置
function setFaultDiagnoseParam(){ 
	var url= "faultDiagnoseParam.jsp";
	var timeParamWin=new Ext.Window({
        id:'timeParamWin',
        title:"故障诊断参数",
        width : Ext.getBody().getWidth()*0.45,
        height : 230, 
        isTopContainer : true,
        modal : true,
        autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
		
    });
	timeParamWin.show();
}

//修改或显示故障诊断规则详情
function editFaultDiagnoseRules(type){
	
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length == 0){
		Ext.Msg.alert("提示","请选择规则！");
	}else if(cell.length == 1){
		var url= "editFaultDiagnoseRules.jsp?editType="+type;
		url += "&DIAGNOSE_ID="+cell[0].get('DIAGNOSE_ID');
		
		//修改全局变量
		diagnoseIdGlobal = cell[0].get('DIAGNOSE_ID');
		
		var editWindow=new Ext.Window({
	        id:'editWindow',
	        title:"故障诊断规则",
	        width : Ext.getBody().getWidth()*0.6,      
	        height : Ext.getBody().getHeight()-90, 
	        isTopContainer : true,
	        modal : true,
	        autoScroll:false, 
			maximized:false,
			html : "<iframe id='editFaultDiagnose_panel' name = 'editFaultDiagnose_panel'  src="
				+ url
				+ ' height="100%" width="100%" frameborder=0 border=0/>'
	     });
	    editWindow.show();
	}else if(cell.length > 1){
		Ext.Msg.alert("提示","请勿多选！");
	}
}

//弹窗选择适用范围
function addAlarmResourceWin(){ 
	var url='alarmResource.jsp?diagnoseId=' + diagnoseIdGlobal;
	var applyAreaWin=new Ext.Window({
	    id:'applyAreaWin',
	    title:"适用范围",
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()-90, 
	    isTopContainer : true,
	    modal : true,
	    autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
	 });
	applyAreaWin.show();  
}


//弹窗选择适用设备
function selectApplyEquips(){  
	var url='applyEquip.jsp';
	var applyEquipWin=new Ext.Window({
	    id:'applyEquipWin',
	    title:"适用设备",
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()-90, 
	    isTopContainer : true,
	    modal : true,
	    autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
	 });
	applyEquipWin.show();
}

//弹窗选择适用设备
function editCondtion(type,linenum,cell){  
	var url='editCondtion.jsp?editType='+type;
	if(cell!=null){
		url+="&alarmName="+cell.get("ALARM_NAME")+"&objType="+cell.get("OBJECT_TYPE")
		+"&alarmLevelCheck="+cell.get("LEVEL")+"&linenum="+linenum;
	}
	
	var editCondtion=new Ext.Window({
	    id:'editCondtion',
	    title:"对象等级",
        width : Ext.getBody().getWidth()*0.3,      
        height : Ext.getBody().getHeight()-320, 
	    isTopContainer : true,
	    modal : true,
	    autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
	 });
	editCondtion.show();  
} 

//弹窗选择适用设备
function modifyAction(linenum,cell){  
	var url='../../../jsp/faultManager/alarmConverge/modifyAct.jsp?ALARMS='
		+cell.get("ALARMS")+"&linenum="+linenum;    
	var modifyAction=new Ext.Window({
	    id:'modifyAction',
	    title:"执行动作",
        width : Ext.getBody().getWidth()*0.5,      
        height : Ext.getBody().getHeight()-200, 
	    isTopContainer : true,
	    modal : true,
	    autoScroll:false,
		maximized:false,
		html : '<iframe src='
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
	 });
	modifyAction.show();  
} 

//删除告警规则
function deleteConvergeRules(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择规则！');
	}else{
		Ext.Msg.confirm('提示','规则删除后无法恢复，是否确认？',function(btn){       
			if(btn=='yes'){
				gridPanel.getEl().mask("删除中。。。");
				var convergeIds = ''; 
				var count = 0;
				for ( var i = 0; i < records.length; i++) {
					convergeIds += records[i].get('CONVERGE_ID')+','; 
				}
				convergeIds = convergeIds.substring(0,convergeIds.lastIndexOf(','));  
				Ext.Ajax.request({
				    url: 'alarm-converge!deleteConvergeRules.action',
				    method: 'POST',
				    params: {'jsonString':convergeIds},
				    success : function(response) { 
				    	gridPanel.getEl().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("提示", obj.returnMessage);
						if (obj.returnResult == 1) {
							// 刷新列表
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						};
					},
					error : function(response) { 
				    	gridPanel.getEl().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) { 
				    	gridPanel.getEl().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				}); 
			}              
		},this);
	}
}

//启用和挂起
function changeFaultDiagnoseRuleStatus(type){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length < 1){
		Ext.Msg.alert('提示', '请选择规则！');
	}else{
		if(type==1){
			var msg ='确认启用选择的规则？';
		} else{
			var msg ='确认挂起选择的规则？';
		}
		Ext.Msg.confirm('提示', msg, function(button) {
			if (button == 'yes') {
				var ids=new Array();
				for(var i=0;i<records.length;i++){
					ids.push(records[i].get("DIAGNOSE_ID"));
				}
				Ext.Ajax.request({
				    url: 'fault-diagnose!changeFaultDiagnoseRuleStatus.action',
				    method: 'POST',
				    params: {
				    	'paramMap.type':type,
				    	'ids':ids
				    },
				    success : function(response) { 
						var obj = Ext.decode(response.responseText); 
						if (obj.returnResult == 1) {  
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						}
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});  
			}
		});
	}
}

//手动执行
function manualActionRules(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择规则！');
	}else{
		var ids=new Array();
		var cnt=0;
		for(var i=0;i<records.length;i++){
			ids.push(records[i].get("DIAGNOSE_ID"));
			if(records[i].get("STATUS")==1){
				cnt++;
			}
		}   
		if(cnt>0){
			Ext.Msg.alert('提示', '规则启用中，请先挂起！');
			return; 
		}  
		var manual=new Ext.Window({
		    id:'manual', 
	        width : 250,      
	        height : 160, 
		    isTopContainer : true,
		    modal : true,
		    autoScroll:false,
			maximized:false, 
			resizable:false,
			shadow:false,
			items : [{ 
				xtype:'panel',
			    height : 93, 
				border : false,
				items:[{
					border : false,
					style : 'margin-top:45px;margin-left:50px;',
					html : '<span><font size = "2.5">正在执行，请稍候！</font></span>'
				}] 
			}], 
			buttonAlign:'center',
			buttons:[{
				text : '后台运行', 
				handler : function() {
					manual.close();
				}
			} ]
		 }).show();  
		 Ext.Ajax.request({
		    url: 'fault-diagnose!manualActionRules.action',
		    method: 'POST',
		    params: {'ids':ids},
		    success : function(response) {  
		    	manual.close();   
				var obj = Ext.decode(response.responseText);  
				if (obj.returnResult == 1) {  
					Ext.Msg.alert("提示","执行完成！");
				}
				if(obj.returnResult == 0) {
					Ext.Msg.alert("提示","执行失败！");
				}
			},
			error : function(response) {
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("错误", response.responseText);
			}
		});  
	}	
}

//判定收敛服务是否开启
function isServerStarted() {
	Ext.Ajax.request({
	    url: 'fault-diagnose!isServerStarted.action',
	    method: 'POST', 
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if (obj.returnResult == 0) { 
	    		//服务未启动
	    		Ext.Msg.alert("提示","故障诊断服务未启动,相关按钮禁用！");
			}else{
				if(authSequence == "all"){
		    		Ext.getCmp("modifyRuleBtn").setDisabled(false);
		    		Ext.getCmp("startRuleBtn").setDisabled(false);
		    		Ext.getCmp("holdRuleBtn").setDisabled(false);
		    		Ext.getCmp("manualRuleBtn").setDisabled(false);
				}else{
					if((parseInt(Ext.getCmp('modifyRuleBtn').privilege,2)&parseInt(authSequence,2)) != 0){
						Ext.getCmp('modifyRuleBtn').setDisabled(false);
					} 
					if((parseInt(Ext.getCmp('startRuleBtn').privilege,2)&parseInt(authSequence,2)) != 0){
						Ext.getCmp('startRuleBtn').setDisabled(false);
					} 
					if((parseInt(Ext.getCmp('holdRuleBtn').privilege,2)&parseInt(authSequence,2)) != 0){
						Ext.getCmp('holdRuleBtn').setDisabled(false);
					} 
					if((parseInt(Ext.getCmp('manualRuleBtn').privilege,2)&parseInt(authSequence,2)) != 0){
						Ext.getCmp('manualRuleBtn').setDisabled(false);
					}
				}
			}
		},
		error : function(response) { 
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) { 
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

Ext.onReady(function (){
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	}; 
	Ext.Ajax.timeout = 90000000;
	Ext.Msg = top.Ext.Msg;
	var win = new Ext.Viewport({
		id:'win',
		layout:'border',
		items:[gridPanel]
	});
	win.show();
	isServerStarted();
	gridPanel.getEl().mask("正在查询");
});