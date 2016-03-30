var storeCond,storeAct,applyEmsIds,applyEmsIdsStr="",eqStr="";
var statusMapping = [ [ 1, '启用' ], [ 2, '挂起' ]];  
var store = new Ext.data.Store({ 
	url:'alarm-converge!searchAlarmConverge.action',
	baseParams : { 
		'limit':200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["CONVERGE_ID","RULE_NAME","STATUS","DESCRIPTION","MODIFIER",
	    "UPDATE_TIME","mainAlarmName","applyRegion","applyEquip"
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
				id : 'mainAlarmName',
				header : '主告警名称',
				dataIndex : 'mainAlarmName',
				width : 150 
			},{
				id : 'applyRegion',
				header : '适用范围',
				dataIndex : 'applyRegion',
				width : 190,
				renderer :function (data, metadata, record, rowIndex, columnIndex, store) {         
	        	    metadata.attr = 'ext:qtip="' + record.get('applyRegion')+'"'; 
	        	    return data ;     
	        	}
			},{
				id : 'applyEquip',
				header : '适用设备',
				dataIndex : 'applyEquip',
				width : 200,
				renderer : function (data, metadata, record, rowIndex, columnIndex, store) {   
	        	    metadata.attr = 'ext:qtip="' + record.get('applyEquip')+'"';
	        	    return data ;     
	        	}
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
				id : 'UPDATE_TIME',
				header : '最近修改时间',
				dataIndex : 'UPDATE_TIME',
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
		id:'add',
		disabled:true,
		icon : '../../../resource/images/btnImages/add.png',
		privilege : addAuth,
		handler : function(){ 
			editConvergeRules(0);
		}
	},{
		text : '删除',
		disabled:true,
		id:'del',
		icon : '../../../resource/images/btnImages/delete.png',
		privilege : delAuth,
		handler : deleteConvergeRules
	},{
		text : '修改',
		disabled:true,
		id:'mod',
		icon : '../../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler :function (){ 
			editConvergeRules(1);
		}
	} ,'-',{
		text : '详情',
		icon : '../../../resource/images/btnImages/information.png',
		privilege : viewAuth,
		handler : function (){ 
			editConvergeRules(2);
		}
	},'-',{
		text : '启用',
		disabled:true,
		id:'qiy',
		icon:'../../../resource/images/btnImages/control_play.png',
		privilege : modAuth,
		handler : function (){ 
			changeConvergeRuleStatus(1);
		}
	},{
		text : '挂起',
		disabled:true,
		id:'guaq',
		icon :'../../../resource/images/btnImages/control_stop.png',
		privilege : modAuth,
		handler : function (){ 
			changeConvergeRuleStatus(2);
		}
	},'-',{
		text : '手动执行',
		disabled:true,
		id:'manual',
		icon : '../../../resource/images/btnImages/information.png',
		privilege : actionAuth,
		handler : manualActionRules 
	},'-',{
		text : '收敛参数',
		icon : '../../../resource/images/btnImages/information.png',
		privilege : modAuth,
		handler : setConvergeParam
	}],
	bbar : pageTool
});  

//告警收敛参数设置
function setConvergeParam(){ 
	var url= "../../../jsp/faultManager/alarmConverge/convergeParam.jsp"; 
	var timeParamWin=new Ext.Window({
        id:'timeParamWin',
        title:"告警收敛参数", 
        width : Ext.getBody().getWidth()*0.4,      
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

//新增或修改收敛规则
function editConvergeRules(type){
	var url= "../../../jsp/faultManager/alarmConverge/editConvergeRules.jsp?editType="+type;  
	if(type==1 || type==2){ 
		var cell = gridPanel.getSelectionModel().getSelections();
		if(cell.length!=1){
			Ext.Msg.alert("提示","请选择一条收敛规则！");
			return;
		}  
		//修改时判定状态
		if(type==1){  
			if(cell[0].get("STATUS")==1){
				Ext.Msg.alert('提示', '规则启用中，请先挂起！');
				return;
			}
		}   
		url += "&CONVERGE_ID="+cell[0].get('CONVERGE_ID'); 
	}

	var editWindow=new Ext.Window({
        id:'editWindow',
        title:"收敛规则",
        width : Ext.getBody().getWidth()*0.6,      
        height : Ext.getBody().getHeight()-90, 
        isTopContainer : true,
        modal : true,
        autoScroll:false, 
		maximized:false,
		html : "<iframe id='editConvergeRule_panel' name = 'editConvergeRule_panel'  src="
			+ url
			+ ' height="100%" width="100%" frameborder=0 border=0/>'
     });
    editWindow.show();  
}

//弹窗选择适用范围
function addAlarmResourceWin(){ 
	var url='../../../jsp/faultManager/alarmConverge/alarmResource.jsp';
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
	var url='../../../jsp/faultManager/alarmConverge/applyEquip.jsp'; 
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
	var url='../../../jsp/faultManager/alarmConverge/editCondtion.jsp?editType='+type;  
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
function changeConvergeRuleStatus(type){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
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
					ids.push(records[i].get("CONVERGE_ID"));
				}
				Ext.Ajax.request({
				    url: 'alarm-converge!changeConvergeRuleStatus.action',
				    method: 'POST',
				    params: {'jsonString':type,
				    	'ids':ids},
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
			ids.push(records[i].get("CONVERGE_ID"));
			if(records[i].get("STATUS")==1){
				cnt++;
			}
		}   
		if(cnt>0){
			Ext.Msg.alert('提示', '规则启用中，请先挂起！');
			return; 
		}  
		var manual=new Ext.Window({
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
		    url: 'alarm-converge!manualActionRules.action',
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
function isServerStarted(){   
	Ext.Ajax.request({
	    url: 'alarm-converge!isServerStarted.action',
	    method: 'POST', 
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if (obj.returnResult == 0) {  
	    		//服务未启动
	    		Ext.Msg.alert("提示","告警收敛服务未启动,相关按钮禁用！"); 
			}else{  
				if(authSequence == "all"){
					Ext.getCmp('add').setDisabled(false);
					Ext.getCmp('del').setDisabled(false);		
					Ext.getCmp('mod').setDisabled(false);
					Ext.getCmp('qiy').setDisabled(false);
					Ext.getCmp('guaq').setDisabled(false);
					Ext.getCmp('manual').setDisabled(false);
					return;
				}
				
				if((parseInt(Ext.getCmp('add').privilege,2)&parseInt(authSequence,2)) != 0){
					Ext.getCmp('add').setDisabled(false);
				} 
				if((parseInt(Ext.getCmp('del').privilege,2)&parseInt(authSequence,2)) != 0){
					Ext.getCmp('del').setDisabled(false);
				} 
				if((parseInt(Ext.getCmp('mod').privilege,2)&parseInt(authSequence,2)) != 0){
					Ext.getCmp('mod').setDisabled(false);
				} 
				if((parseInt(Ext.getCmp('qiy').privilege,2)&parseInt(authSequence,2)) != 0){
					Ext.getCmp('qiy').setDisabled(false);
				} 
				if((parseInt(Ext.getCmp('guaq').privilege,2)&parseInt(authSequence,2)) != 0){
					Ext.getCmp('guaq').setDisabled(false);
				} 
				if((parseInt(Ext.getCmp('manual').privilege,2)&parseInt(authSequence,2)) != 0){
					Ext.getCmp('manual').setDisabled(false);
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
	isServerStarted();
	var win = new Ext.Viewport({
		id:'win',
		layout:'border',
		items:[gridPanel]
	});
	win.show(); 
	gridPanel.getEl().mask("正在查询");
});