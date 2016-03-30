var statusMapping = [ [ 1, '启用' ], [ 2, '挂起' ]];  
/*---------------------------规则名称----------------------------------------*/
var ruleName = new Ext.form.TextField({
	id:'ruleName',      
	maxLength: 32,
	sideText:'<font color=red>*</font>',
	allowBlank:false,
	anchor : '85%'  
});
/*---------------------------描述---------------------------------------*/
var description = new Ext.form.TextField({
	id:'description', 
	maxLength: 128,
	height:130, 
	anchor : '85%'  
});
/*---------------------------适用范围---------------------------------------*/
var applyRegion = {
	id:'applyRegion',
	layout : 'column',
	border : false,
	items : [{
		columnWidth:0.85,
		layout : 'form',
		border : false,
		items : [{
			id : 'regionText',
			xtype : 'textfield',  
			readOnly : true,
			sideText:'<font color=red>*</font>',
			allowBlank:false,
			anchor : '95%'  
		}]
	},{ 
		border : false,
		id:'btnRegion',
		html : '<img src="../../../resource/images/btnImages/setTask.png"'
			+'onclick="applyRegionClick()" style="cursor:hand"></img>'
	}]
}; 
 
function applyRegionClick(){
	parent.addAlarmResourceWin(); 
} 

/*---------------------------适用设备---------------------------------------*/  
var applyEquip = {
	id:'equip',
	layout : 'column',
	border : false,
	items : [{
		columnWidth:0.85,
		layout : 'form',
		border : false,
		items : [{
			id:'applyEquip',
			xtype : 'textfield',  
			readOnly : true,
			anchor : '95%' 
		}]
	},{ 
		border : false,
		id:'btnEquip',
		html : '<img src="../../../resource/images/btnImages/setTask.png"'
			+'onclick="applyEquipClick()" style="cursor:hand"></img>'
	}]
}; 

function applyEquipClick(){
	if(Ext.getCmp('regionText').getValue()==null || Ext.getCmp('regionText').getValue()==""){
		Ext.Msg.alert("提示","请先选取适用范围！");
	}else{
		parent.selectApplyEquips();
	}
} 
/*-----------------------------条件Condition gridPanel----------------------------------------*/
parent.storeCond = new Ext.data.Store({ 
	reader : new Ext.data.JsonReader({ 
		root : "rows"
	}, ["CONVERGE_ID","ALARM_NAME","OBJECT_TYPE","LEVEL"])
});  

var checkCond = new Ext.grid.CheckboxSelectionModel({
	singleSelect:true,
	header :""
    }); 
var cmCond = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}),checkCond,{
		header : '主告警名称',
		dataIndex : 'ALARM_NAME',
		width : 120 
	},{
		header : '告警对象类别',
		dataIndex : 'OBJECT_TYPE',
		width : 80,
		renderer : function(v) {
			switch (v) {
			case 1:
				return "网元";
			case 5:
				return "端口";
			case 6:
				return "通道";
			case 9:
				return "板卡";
			default:
				return null;
			}
		}
	}, {
		header : '对象等级',
		dataIndex : 'LEVEL',
		width : 120
	}]
}); 

var condPanel = new Ext.grid.GridPanel({
	id : 'condPanel',	 
	height : 200, 
	store : parent.storeCond, 
	cm : cmCond, 
	frame : false,
	stripeRows : true, 
	selModel : checkCond,
	tbar : [ '条件','->','-', { 
		text : '增加', 
		id:'condAdd',
		handler : function(){
			parent.editCondtion(0); 
		}
	},{
		text : '删除', 
		id:'condDel',
		disabled:true,
		handler : delCondtion
	},{
		text : '修改', 
		id:'condMod',
		disabled:true,
		handler : function(){
			var linenum=condPanel.getSelectionModel().lastActive;
			var cell = condPanel.getSelectionModel().getSelected();
			if(cell==null){
				Ext.Msg.alert("提示","请选择需要修改的条件！");
				return;
			}  
			parent.editCondtion(1,linenum,cell);  
		}
	}],
	listeners:{
		'rowclick':function(){ 
			if(condPanel.getSelectionModel().hasSelection()){
				Ext.getCmp('condDel').enable();
				Ext.getCmp('condMod').enable();
			}else{
				Ext.getCmp('condDel').disable();
				Ext.getCmp('condMod').disable();
			}
		}
	}
});
/*----------------------------执行动作 Act gridPanel----------------------------------------*/ 
var actTypeMapping = [ [ 1, '主告警监测对象本身' ], [ 2, '主告警监测对象的下属对象' ], 
                       [3, '主告警监测对象相邻端口' ], [ 4, '主告警监测对象所属传输系统网元' ],  [ 5, '主告警监测对象相关电路通道 ' ]];    
var data = [ [null,1,1, '所有'],[null,1,2, '所有'],[null,2,3, '所有'],
 			[null,2, 4 , '所有'],[null,2,5, '所有']]; 
(function() { 
	parent.storeAct = new Ext.data.ArrayStore({
		fields : [ "CONVERGE_ID","STATUS","ACTION_TYPE","ALARMS"]
	}); 
})();  

var checkAct = new Ext.grid.CheckboxSelectionModel({
	singleSelect:true,
	header :""
    }); 

var cmAct = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}),checkAct,{
		header : '状态',
		dataIndex : 'STATUS',
		width : 50 ,
		renderer :  function(v){
			return (statusMapping[v-1] != null) ? statusMapping[v-1][1] : v;
		}
	},{
		header : '动作', 
		width : 50,
		renderer : function(){
			return "收敛";
		}
	}, {
		header : '对象',
		dataIndex : 'ACTION_TYPE',
		width : 180,
		renderer :function(v){
			return (actTypeMapping[v-1] != null) ? actTypeMapping[v-1][1] : v;
		}
	}, {
		header : '告警',
		dataIndex : 'ALARMS',
		width : 120
	}]
}); 
 
var actPanel = new Ext.grid.GridPanel({
	id : 'actPanel',	 
	height : 200, 
	store : parent.storeAct, 
	cm : cmAct, 
	frame : false,
	stripeRows : true, 
	selModel : checkAct,
	tbar : [ '执行动作','->','-', {
		text : '启用', 
		disabled:true,
		id:'qiyong',
		handler : function(){
			var cell = actPanel.getSelectionModel().getSelected();
			if(cell==null){
				Ext.Msg.alert("提示","请选择需要启用的动作！");
				return;
			}  
			var linenum=actPanel.getSelectionModel().lastActive;
			parent.storeAct.getAt(linenum).set('STATUS',1); 
		}
	},{
		text : '挂起', 
		disabled:true,
		id:'guaqi',
		handler : function(){
			var cell = actPanel.getSelectionModel().getSelected();
			if(cell==null){
				Ext.Msg.alert("提示","请选择需要挂起的动作！");
				return;
			}  
			var linenum=actPanel.getSelectionModel().lastActive;
			parent.storeAct.getAt(linenum).set('STATUS',2); 
		 
		}
	},{
		text : '修改', 
		id:'actMod',
		disabled:true,
		handler :function (){
			var linenum=actPanel.getSelectionModel().lastActive;
			var cell = actPanel.getSelectionModel().getSelected();
			if(cell==null){
				Ext.Msg.alert("提示","请选择需要修改的动作！");
				return;
			}   
			parent.modifyAction(linenum,cell);
		}
	}] ,
	listeners:{
		'rowclick':function(){
			if(actPanel.getSelectionModel().hasSelection()){
				Ext.getCmp('qiyong').enable();
				Ext.getCmp('guaqi').enable();
				Ext.getCmp('actMod').enable();
			}else{
				Ext.getCmp('qiyong').disable();
				Ext.getCmp('guaqi').disable();
				Ext.getCmp('actMod').disable();
			} 
		}
	}
}); 
/*-------------------------------------------------------------------------------------------*/
  
var centerPanel = new Ext.FormPanel({
	region : 'center',
	autoScroll:true, 
	border:false,
	labelWidth:35,
	items : [{
		layout:'column',
		border:false,
		items : [{
			columnWidth:0.4,
			border:false,
			layout:'form',
			items:[{
				style : 'margin-top:20px;margin-left:20px;',
				border : false,
				html : '<font size="2">规则名称</font>'
			},ruleName,{
				style : 'margin-left:20px;',
				border : false,
				html : '<font size="2">描述</font>'
			},description,{
				xtype: 'spacer', 
	        	border:false ,
	        	height:100
			},{
				style : 'margin-left:20px;',
				border : false,
				html : '<font size="2">适用范围</font>'
			},applyRegion,{
				style : 'margin-left:20px;',
				border : false,
				html : '<font size="2">适用设备</font>'
			},applyEquip,{
				style : 'margin-left:40px;',
				border : false,
				html : '<font size="2">注：只对主告警进行约束</font>'
			}]
		},{
			columnWidth:0.55, 
			border:false,
			layout:'form',
			items : [{
				xtype: 'spacer', 
	        	border:false ,
	        	height:20
			},condPanel,{
				xtype: 'spacer', 
	        	border:false ,
	        	height:20
			},actPanel]
		}] 
	}],
	buttons : [{
		text : '确定',
		id:'sure',
		handler : function(){
			if(editType==2){
				var win = parent.Ext.getCmp('editWindow');
				if (win) {
					win.close();
				}
			}else{
				saveConvergeRules("confirm");
			}
		}   
	}]
});
 
//删除收敛条件限制
function delCondtion() {
	var cell = condPanel.getSelectionModel().getSelections();
	if (cell.length == 0) {
		Ext.Msg.alert("提示", "请选择需要删除的条件！");
	} else { 
		parent.storeCond.remove(cell);
		condPanel.getView().refresh(); 
	}
}

//点击确定，保存规则
function saveConvergeRules(flagStr){
	if(centerPanel.getForm().isValid()){
		//禁用确定按钮，避免重复添加
		Ext.getCmp('sure').disable(); 
		//判断收敛条件是否为空
		if(parent.storeCond.getCount()==0){
			Ext.Msg.alert("提示","收敛条件不能为空!"); 
			Ext.getCmp('sure').enable();
			return;
		} 
		//判断收敛条件的告警类别是否一致
		var obj=parent.storeCond.getAt(0).get("OBJECT_TYPE");
		for(var i = 1;i<parent.storeCond.getCount();i++){
			if(parent.storeCond.getAt(i).get("OBJECT_TYPE")!=obj){
				Ext.Msg.alert("提示","条件中的告警对象类别应保持一致!"); 
				Ext.getCmp('sure').enable();
				return;
			}
		} 
		//规则名称是否有重复
		var RULE_NAME = Ext.getCmp('ruleName').getValue(); 
		if(RULE_NAME==null ||RULE_NAME==""){
			Ext.Msg.alert("提示","规则名称不能为空!"); 
			Ext.getCmp('sure').enable();
			return;
		}
		for(var i = 0;i<parent.store.getCount();i++){
			if(editType==0){
				if(parent.store.getAt(i).get("RULE_NAME")==RULE_NAME){
					Ext.Msg.alert("提示","规则名称存在重复!"); 
					Ext.getCmp('sure').enable();
					return;
				} 
			}
			if(editType==1){
				if(parent.store.getAt(i).get("RULE_NAME")==RULE_NAME 
						&& parent.store.getAt(i).get("CONVERGE_ID")!=CONVERGE_ID){
					Ext.Msg.alert("提示","规则名存在重复!"); 
					Ext.getCmp('sure').enable();
					return;
				}
			} 
		} 
		//是否规则冲突
		//适用范围,除去“网管：”
		var applyRegion = Ext.getCmp('regionText').getValue(); 
		var applyEquip = Ext.getCmp('applyEquip').getValue(); 
		
		if(parent.store.getCount()>0){
			if(judgeDuplicate(applyRegion.substring(3,applyRegion.length),applyEquip)){ 
				Ext.Msg.alert("提示","规则适用和条件有重合，请检查适用范围和条件!"); 
				Ext.getCmp('sure').enable();
				return;
			} 
		}
		
		//描述
		var DESCRIPTION = Ext.getCmp('description').getValue();  
		//条件grid
		var condCheck = '';
		var records = condPanel.getStore().getRange();
		for ( var i = 0; i < records.length; i++) {
			condCheck += records[i].get('ALARM_NAME') + "."+records[i].get('OBJECT_TYPE') 
						 +"."+records[i].get('LEVEL') + ";";
		}
		condCheck = condCheck.substring(0, condCheck.lastIndexOf(";"));
		//执行动作grid
		var actCheck = '';
		var records = actPanel.getStore().getRange();
		for ( var i = 0; i < records.length; i++) {
			actCheck += records[i].get('STATUS') + "."+records[i].get('ACTION_TYPE') 
						 +"."+records[i].get('ALARMS') + ";";
		}
		actCheck = actCheck.substring(0, actCheck.lastIndexOf(";")); 
		
		var map = {'RULE_NAME':RULE_NAME,'DESCRIPTION':DESCRIPTION,
		    		'applyEquip':parent.eqStr,'condCheck':condCheck,'actCheck':actCheck
		    		};
		map['flagStr']=flagStr;
		var url='alarm-converge!addAlarmConvergeRules.action'; 
		if(editType==1){
			map['CONVERGE_ID']=CONVERGE_ID;
			url='alarm-converge!modifyAlarmConvergeRules.action';
		}
		
		Ext.Ajax.request({
		    url: url,
		    method: 'POST',
		    params:  {
		    	'jsonString' : Ext.encode(map),
	    		'ids':parent.applyEmsIds
		    },
		    success : function(response) { 
				var obj = Ext.decode(response.responseText); 
				if (obj.returnResult == 1) {  
					Ext.Msg.alert("提示", obj.returnMessage,function(){
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}    
						if(obj.returnMessage.indexOf("保存")<0){
							// 关闭新增窗口
							var win = parent.Ext.getCmp('editWindow');
							if(win){
								win.close();
							}
						}
					}); 
				} 
				Ext.getCmp('sure').enable();
			},
			error : function(response) { 
				Ext.Msg.alert("错误", response.responseText);
				Ext.getCmp('sure').enable();
			},
			failure : function(response) { 
				Ext.Msg.alert("错误", response.responseText);
				Ext.getCmp('sure').enable();
			}
		});    
	}
}

//检测规则是否存在冲突
function judgeDuplicate(applyRegion,applyEquip){ 
	var flag = false;
	parent.storeCond.each(function(cond){   
		if(flag==true) return false;
		parent.store.each(function (converge){
			if(flag==true) return false;
			var checkAlarmName = converge.get("mainAlarmName");  
			var checkApplyRegion = converge.get("applyRegion").substring(3,converge.get("applyRegion").length);
			var checkApplyEquip = converge.get("applyEquip");
			//主告警名称相同的情况下 
			if(isContained(converge.get("CONVERGE_ID"),cond.get("ALARM_NAME"),checkAlarmName)){
			//适用范围
				if(isContained(applyRegion,checkApplyRegion)){
					//适用设备
					if(applyEquip,checkApplyEquip){
						flag=true;
					}
				}
			} 
		});
	});   
	return flag;
} 

function isContained(convergeId,str1,str2){
	var data1 = str1.split(",");
	var data2 = str2.split(",");
	for(var i=0;i<data1.length;i++){
		for(var j=0;j<data2.length;j++){
			if(editType==0){
				if(data1[i]==data2[j]){
					return true;
				}
			}
			if(editType==1){
				if(convergeId!= CONVERGE_ID && data1[i]==data2[j]){
					return true;
				}
			}
		}
	} 
	return false;
} 

//初始化新增修改和详情的按钮
function initData(){ 
	if(editType==1 || editType==2){
		Ext.Ajax.request({
		    url: 'alarm-converge!getAlarmConvergeDetailById.action',
		    method: 'POST',
		    params: {'jsonString':CONVERGE_ID},
		    success : function(response) {
		    	var obj = Ext.decode(response.responseText);  
		    	Ext.getCmp('ruleName').setValue(obj.textMap.RULE_NAME);
		    	Ext.getCmp('description').setValue(obj.textMap.DESCRIPTION);
		    	Ext.getCmp('applyEquip').setValue(obj.textMap.equipStr); 
		    	parent.eqStr = obj.textMap.eqStr;   
		    	Ext.getCmp('regionText').setValue(obj.textMap.applyStr); 
		    	parent.applyStr = obj.textMap.applyStr;
		    	parent.applyEmsIdsStr = obj.textMap.applyEmsIdsStr;
		    	parent.applyEmsIds = obj.applyEmsIds; 
		    	//条件
		    	var condArr = obj.condList; 
		    	for ( var i = 0; i < condArr.length; i++) { 
			    	var record = new Ext.data.Record( ["CONVERGE_ID","ALARM_NAME","OBJECT_TYPE","LEVEL"]);
			    	record.set('CONVERGE_ID',CONVERGE_ID);
			    	record.set('ALARM_NAME',condArr[i].ALARM_NAME);
			    	record.set('OBJECT_TYPE',condArr[i].OBJECT_TYPE);
			       	record.set('LEVEL',condArr[i].LEVEL); 
			    	parent.storeCond.add(record);
				} 
		    	//执行动作
		    	var actArr = obj.actList; 
		    	for ( var i = 0; i < actArr.length; i++) { 
			    	var record = new Ext.data.Record([ "CONVERGE_ID","STATUS","ACTION_TYPE","ALARMS"]);
			    	record.set('CONVERGE_ID',CONVERGE_ID);
			    	record.set('STATUS',actArr[i].STATUS);
			    	record.set('ACTION_TYPE',actArr[i].ACTION_TYPE);
			       	record.set('ALARMS',actArr[i].ALARMS); 
			    	parent.storeAct.add(record);
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
	if(editType==0){
		parent.applyEmsIds="";
		parent.applyEmsIdsStr="";
		parent.eqStr=""; 
		//加载ActionStore
		parent.storeAct.loadData(data); 
	}
	
	if(editType==0 || editType==1){
		//新增、修改
		centerPanel.addButton({text:'取消'},
			function(){
				var win = parent.Ext.getCmp('editWindow');
				if (win) {
					win.close();
				}
		});
	}
	if(editType==1){
		//修改
		centerPanel.addButton({text:'应用'},function(){ 
			saveConvergeRules("apply");
		});
	}
	//详情，页面禁止修改
	if(editType==2){   
		ruleName.disabled=true;
		description.disabled=true;
		Ext.getCmp('regionText').disabled=true;
		Ext.getCmp('btnRegion').hidden=true;
		Ext.getCmp('applyEquip').disabled=true;
		Ext.getCmp('btnEquip').hidden=true;
		Ext.getCmp('condAdd').setVisible (false);
		Ext.getCmp('condDel').setVisible (false);
		Ext.getCmp('condMod').setVisible (false);
		Ext.getCmp('qiyong').setVisible (false);
		Ext.getCmp('guaqi').setVisible (false);
		Ext.getCmp('actMod').setVisible (false);
	} 
	centerPanel.doLayout();
}


Ext.onReady(function(){
	Ext.QuickTips.init(); 
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){
 		top.Ext.menu.MenuMgr.hideAll();
	};  
	initData();
  	var win =new Ext.Viewport({
  		id:'win',
        layout : 'border', 
        items : centerPanel
	});
 });
