/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var westPanel = new Ext.Panel({
	id:"westPanel",
	region:"west",
	width:300,
	autoScroll:true,
	forceFit:true,
	collapsed: false,   // initially collapse the group
    collapsible: false,
    collapseMode: 'mini',
    split:true,
    html:'<iframe id="tree_panel" src ="../../../jsp/common/tree.jsp?nodeLimite=5" height="100%" width="100%" frameBorder=0/>'
});


var aSendPort = new Ext.form.TextField({
    id:'aSendPort',
    name: 'aSendPort',
    fieldLabel: 'A站发光端口',
    emptyText:'请选择端口........',
// margins:{top:150, right:0, bottom:50, left:0},
	style:"margin-bottom:5px;",
    allowBlank:false,
    readOnly:true,
    anchor: '95%',
    button:[ {text:'设置A站发光端口', 
	        handler:function(){
        		setPort(aSendPort,aSendPortHidden);
	        }}]
});

var bSendPort = new Ext.form.TextField({  
    id:'bSendPort',
    name: 'bSendPort',
    fieldLabel: 'B站发光端口',
    emptyText:'请选择端口........',
    style:"margin-bottom:5px;",
    allowBlank:false,
    readOnly:true,
    anchor: '95%'
});

var bReceivePort = new Ext.form.TextField({
    id:'bReceivePort',
    name: 'bReceivePort',
    fieldLabel: 'B站收光端口',
    emptyText:'请选择端口........',
    style:"margin-bottom:5px;",
    allowBlank:false,
    readOnly:true,
    anchor: '95%'
});

var aSendPortHidden =  new Ext.form.Hidden({  
	 name:'aSendPortHidden'
});

var bSendPortHidden =  new Ext.form.Hidden({  
	 name:'bSendPortHidden'
});

var bReceivePortHidden =  new Ext.form.Hidden({  
	 name:'bReceivePortHidden' 
});

var waveNumber = new Ext.form.NumberField({
    id:'waveNumber',
    name: 'waveNumber',
    fieldLabel: '波道数',
    allowNegative:false,
    allowDecimals:false,
    style:"margin-bottom:5px;",
    maxValue:100,
    minValue:0,
    emptyText:'0-100',
    allowBlank:false,
    anchor: '95%'
});

var standardWaveNumber = new Ext.form.NumberField({
    id:'standardWaveNumber',
    name: 'standardWaveNumber',
    fieldLabel: '标称波道数',
    allowNegative:false,
    allowDecimals:false,
    style:"margin-bottom:5px;",
    maxValue:100,
    minValue:0,
    emptyText:'0-100',
    allowBlank:false,
    anchor: '95%'
});

var aSendStandardPower = new Ext.form.NumberField({
    xtype: 'textfield',
    id:'aSendStandardPower',
    name: 'aSendStandardPower',
    fieldLabel: 'A站OA(发)单波标称功率',
    allowNegative:true,
    allowDecimals:true,
    style:"margin-bottom:5px;",
    decimalPrecision:2,
    maxValue:100,
    minValue:-30,
    emptyText:'-30-100',
    allowBlank:false,
    anchor: '95%'
});

var aMaxPower = new Ext.form.NumberField({
    xtype: 'textfield',
    id:'aMaxPower',
    name: 'aMaxPower',
    fieldLabel: 'A站OA最大发光功率',
    allowNegative:true,
    allowDecimals:true,
    style:"margin-bottom:5px;",
    decimalPrecision:2,
    maxValue:100,
    minValue:-30,
    emptyText:'-30-100',
    allowBlank:false,
    anchor: '95%'
});

 var bReceiveStandardPower = new Ext.form.NumberField({  
    xtype: 'textfield',
    id:'bReceiveStandardPower',
    name: 'bReceiveStandardPower',
    fieldLabel: 'B站OA(收)单波标称功率',
    allowNegative:true,
    allowDecimals:true,
    style:"margin-bottom:5px;",
    decimalPrecision:2,
    maxValue:100,
    minValue:-30,
    emptyText:'-30-100',
    allowBlank:false,
    anchor: '95%'
});

var bSendStandardPower = new Ext.form.NumberField({
    xtype: 'textfield',
    id:'bSendStandardPower',
    name: 'bSendStandardPower',
    fieldLabel: 'B站OA(发)单波标称功率',
    allowNegative:true,
    allowDecimals:true,
    style:"margin-bottom:5px;",
    decimalPrecision:2,
    maxValue:100,
    minValue:-30,
    emptyText:'-30-100',
    allowBlank:false,
    anchor: '95%'
});

var bMaxPower = new Ext.form.NumberField({
    xtype: 'textfield',
    id:'bMaxPower',
    name: 'bMaxPower',
    fieldLabel: 'B站OA最大发光功率',
    style:"margin-bottom:5px;",
    allowNegative:true,
    allowDecimals:true,
    decimalPrecision:2,
    maxValue:100,
    minValue:-30,
    emptyText:'-30-100',
    allowBlank:false,
    anchor: '95%'
});

var sectionName = new Ext.form.TextField({
    id:'sectionName',
    name: 'sectionName',
    fieldLabel: '光放段名称',
    style:"margin-bottom:5px;",
    allowBlank:false,
    anchor: '95%'
});

var sectionGroup = new Ext.form.TextField({
    id:'sectionGroup',
    name: 'sectionGroup',
    fieldLabel: '光放段组',
    style:"margin-bottom:5px;",
    allowBlank:false,
    anchor: '95%'
});

var aSendPortButton = new Ext.Button({
							id:'aSendPortButton',
							style:"margin-bottom:5px;",
							text:'设置',
	        				handler:function(){
        						setPort(aSendPort,aSendPortHidden);
	       					 }
						})
var bReceivePortButton = new Ext.Button({
							id:'bReceivePortButton',
							style:"margin-bottom:5px;",
							text:'设置',
					        handler:function(){
					        	setPort(bReceivePort,bReceivePortHidden);
					        }
						})
var bSendPortButton = new Ext.Button({
							id:'bSendPortButton',
							style:"margin-bottom:5px;",
							text:'设置',
					        handler:function(){
					        	setPort(bSendPort,bSendPortHidden);
					        }
						})


var formPanel = new Ext.FormPanel({
	region:"center",
// title:'光放段信息录入',
    frame:false,
    border:false,
    bodyStyle:'padding:30px 10px 10px 10px',
// autoHeight:true,
    labelWidth: 100,
    labelAlign: 'right',
    collapsed: true,   // initially collapse the group
    collapsible: false,
    collapseMode: 'mini',
    split:true,
	items: [{// 行1
				layout:"column",
				bodyStyle:'padding:1px 50px',
				border:false,
				items:[	{
							columnWidth:.60,
							layout : "form", // A站发光端口
							labelWidth:150,							   
							border:false,
							items : [ 
									aSendPort,
									aSendPortHidden,// A站发光端口
									bReceivePort,bReceivePortHidden,// B站收光端口
									bSendPort,bSendPortHidden,// B站发光端口
									sectionGroup,// 光放段组
									sectionName,// 光放段名称
									waveNumber,// 波道数
									standardWaveNumber,// 标称波道数
									aSendStandardPower,// A站OA（发）单波标称功率
									aMaxPower,// A站OA最大发光功率
									bReceiveStandardPower,// //B站OA（收）单波标称功率
									bSendStandardPower,// B站OA（发）单波标称功率
									bMaxPower// B站OA最大发光功率
									 ]
						},{
							columnWidth:.15,
							layout : "form", // A站发光端口
							labelWidth:150,
							border:false,
							items : [ 
									aSendPortButton,
									bReceivePortButton,
									bSendPortButton
									 ]
						}]	
		    }],
    buttons: ['->',

	    {text:'保存',
	        icon:'../../../resource/images/btnImages/disk.png',
	        handler:function(){
	        	saveSection();
	        }
	    },
	    {text:'重置',
	        icon:'../../../resource/images/btnImages/arrow_undo.png',
	        handler:function(){
	        	formPanel.getForm().reset();
	        }
	    }
    ]
});




// 设置端口信息
function setPort(port,portHidden){
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var selectedTargets = new Array();
	var checkedNodes;
	if(iframe.getAllCheckedNodes){
		checkedNodes = iframe.getAllCheckedNodes();
	}else{
		checkedNodes = iframe.contentWindow.getAllCheckedNodes();
	}
	var portDisplayname = iframe.getPtpCompleteDisplayName();
	if(checkedNodes.length == 1){
		var nodeId = checkedNodes[0].split("-")[0];
		var nodeType = checkedNodes[0].split("-")[1];
		if(nodeType == 5){
			port.setValue(portDisplayname);
			portHidden.setValue(nodeId);
			iframe.triggerRootNode();
		}else{
			Ext.Msg.alert("信息","请选择一个端口！");
		}
		
	}else{
		Ext.Msg.alert("信息","请选择一个端口！");
	}
}

function saveSection(){
	if(formPanel.getForm().isValid()){
		jsonData = {
	    	// three port source data
			"sectionModel.ASendPortId":aSendPortHidden.getValue(),
			"sectionModel.ASendPortDisplayname":aSendPort.getValue(),
			"sectionModel.BSendPortId":bSendPortHidden.getValue(),
			"sectionModel.BSendPortDisplayname":bSendPort.getValue(),
			"sectionModel.BReceivePortId":bReceivePortHidden.getValue(),
			"sectionModel.BReceivePortDisplayname":bReceivePort.getValue(),
			// input paramaters
			"sectionModel.sectionGroupName":sectionGroup.getValue(),
			"sectionModel.sectionName":sectionName.getValue(),
			"sectionModel.waveNumber":waveNumber.getValue(),
			"sectionModel.standardWaveNumber":standardWaveNumber.getValue(),
			"sectionModel.ASendStandardPower":aSendStandardPower.getValue(),
			"sectionModel.AMaxPower":aMaxPower.getValue(),
			"sectionModel.BSendStandardPower":bSendStandardPower.getValue(),
			"sectionModel.BReceiveStandardPower":bReceiveStandardPower.getValue(),
			"sectionModel.BMaxPower":bMaxPower.getValue()
        };
        Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
            url: 'saveSectionWave.action',
            method: 'POST',
            params: jsonData,
            success : function(response){// 回调函数
            	Ext.getBody().unmask();
            	var obj = Ext.decode(response.responseText);
				if(obj.returnResult == 0){
		   		    // 刷新列表
	        		var pageTool = Ext.getCmp('pageTool');
	        		if(pageTool){
						pageTool.doLoad(pageTool.cursor);
					}
				}
				if(obj.returnResult == 1){
	        		Ext.Msg.alert("信息",obj.returnMessage);
	        	}
            	
			},
			error:function(response) {
				Ext.getBody().unmask();
            	Ext.Msg.alert('错误','录入波分复用段错误，请检查corba连接！');
			},
			failure:function(response) {
				Ext.getBody().unmask();
            	Ext.Msg.alert('错误','录入波分复用段错误，请检查corba连接！');
			}
		 })
	}
}
/*
 * function fulshSectionWave(){ var cell =
 * checkboxSelectionModel.getSelections(); if(cell.length == 1){ var jsonData = {
 * "sectionModel.sectionId":cell[0].get("sectionId") };
 * Ext.getBody().mask('正在执行，请稍候...'); Ext.Ajax.request({ url:
 * 'fulshSectionWave.action', method: 'POST', params: jsonData, success :
 * function(response){//回调函数 Ext.getBody().unmask(); var obj =
 * Ext.decode(response.responseText); if(obj.returnResult == 0){ //刷新列表 var
 * pageTool = Ext.getCmp('pageTool'); if(pageTool){
 * pageTool.doLoad(pageTool.cursor); } } if(obj.returnResult == 1){
 * Ext.Msg.alert("信息",obj.returnMessage); } }, error:function(response) {
 * Ext.getBody().unmask(); Ext.Msg.alert('错误','数据加载失败，请检查corba连接！'); },
 * failure:function(response) { Ext.getBody().unmask();
 * Ext.Msg.alert('错误','数据加载失败，请检查corba连接！'); } }); }else{
 * Ext.Msg.alert("信息","请选择需要刷新的数据，只能选择一条！"); } }
 * 
 * function deleteSectionWave(){ var jsonString=new Array(); var cell =
 * checkboxSelectionModel.getSelections(); if(cell.length>0){ for(var i=0;i<cell.length;i++){
 * var sectionModel = { "sectionId":cell[i].get("sectionId") };
 * jsonString.push(sectionModel); } var jsonData = {
 * "jsonString":Ext.encode(jsonString) }; Ext.Msg.confirm('提示','确认删除？',
 * function(btn){ if(btn=='yes'){ Ext.getBody().mask('正在执行，请稍候...');
 * Ext.Ajax.request({ url: 'deleteSectionWave.action', method: 'POST', params:
 * jsonData, success : function(response){//回调函数 Ext.getBody().unmask(); var obj =
 * Ext.decode(response.responseText); if(obj.returnResult == 0){ //刷新列表 var
 * pageTool = Ext.getCmp('pageTool'); if(pageTool){
 * pageTool.doLoad(pageTool.cursor); } } if(obj.returnResult == 1){
 * Ext.Msg.alert("信息",obj.returnMessage); } }, error:function(response) {
 * Ext.getBody().unmask(); Ext.Msg.alert('错误','数据加载失败，请检查连接！'); },
 * failure:function(response) { Ext.getBody().unmask();
 * Ext.Msg.alert('错误','数据加载失败，请检查连接！'); } }); }else{
 *  } }); }else{ Ext.Msg.alert("信息","请选择需要删除的数据！"); } }
 * 
 * function modifySectionWave(){ // var cell =
 * checkboxSelectionModel.getSelections(); var jsonString=new Array(); var cell =
 * store.getModifiedRecords(); if(cell.length>0){ for(var i=0;i<cell.length;i++){
 * var sectionModel = { "sectionId":cell[i].get("sectionId"),
 * 
 * "sectionGroupName":cell[i].get("sectionGroupName"),
 * "sectionName":cell[i].get("sectionName"),
 * "waveNumber":cell[i].get("waveNumber"),
 * "standardWaveNumber":cell[i].get("standardWaveNumber"),
 * "attenuationStandardValue":cell[i].get("attenuationStandardValue"),
 * "ASendStandardPower":cell[i].get("ASendStandardPower"),
 * "AMaxPower":cell[i].get("AMaxPower"),
 * "BSendStandardPower":cell[i].get("BSendStandardPower"),
 * "BReceiveStandardPower":cell[i].get("BReceiveStandardPower"),
 * "BMaxPower":cell[i].get("BMaxPower"),
 * "ASendPortValue":cell[i].get("ASendPortValue"),
 * "BReceivePortValue":cell[i].get("BReceivePortValue"),
 * "BSendPortValue":cell[i].get("BSendPortValue") };
 * jsonString.push(sectionModel); } var jsonData = {
 * "jsonString":Ext.encode(jsonString) };
 * //提交修改，不然store.getModifiedRecords();数据会累加 store.commitChanges();
 * Ext.getBody().mask('正在执行，请稍候...'); Ext.Ajax.request({ url:
 * 'modifySectionWave.action', method: 'POST', params: jsonData, success :
 * function(response){//回调函数 Ext.getBody().unmask(); var obj =
 * Ext.decode(response.responseText); if(obj.returnResult == 0){ //刷新列表 var
 * pageTool = Ext.getCmp('pageTool'); if(pageTool){
 * pageTool.doLoad(pageTool.cursor); } } if(obj.returnResult == 1){
 * Ext.Msg.alert("信息",obj.returnMessage); } }, error:function(response) {
 * Ext.getBody().unmask(); Ext.Msg.alert('错误','数据加载失败，请检查连接！'); },
 * failure:function(response) { Ext.getBody().unmask();
 * Ext.Msg.alert('错误','数据加载失败，请检查连接！'); } }); }else{
 * Ext.Msg.alert("信息","没有修改的数据！"); } }
 */
function trans(v) {
	if (v == 'null' || v.length == 0){
		return '-';
	}else {
		return v;
	}
}

function colorGrid(v,m) {
	if(v=='一般告警'){
		m.css='x-grid-font-pink';
	}else if(v=='严重告警'){
		m.css='x-grid-font-red';
	}else{
		m.css='x-grid-font-blue';
	}
	return v;
}

Ext.onReady(function(){
	
 	Ext.BLANK_IMAGE_URL="../../../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
// Ext.Msg = top.Ext.Msg;
 	Ext.Ajax.timeout=900000; 

    var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [westPanel,formPanel]
	});
	win.show();	
	formPanel.toggleCollapse(true);
  });
  
  