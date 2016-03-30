
var napcause = new Ext.form.TextField({
	id : 'napcause',
	fieldLabel : '厂家告警名称',
//	allowBlank : false,
	height:20,
	maxLength:50,                
	maxLengthText:'最多可输入50个字符',
	blankText:'不能为空',
//	width : 110
});
var nopcause = new Ext.form.TextField({
	id : 'nopcause',
	fieldLabel : '标准告警名称',
	allowBlank : true,
	height:20,
	maxLength:50,                
	maxLengthText:'最多可输入50个字符',
//	blankText:'不能为空',
//	width : 110
});
var repcause = new Ext.form.TextField({
	id : 'repcause',
	fieldLabel : '归一化告警名称',
//	allowBlank : false,
	height:20,
	maxLength:50,                
	maxLengthText:'最多可输入50个字符',
	blankText:'不能为空',
//	width : 110
});

/**
 * 创建告警级别下拉框
 */
var factroyCombo = new Ext.form.ComboBox({
	id : 'factroyCombo',
	fieldLabel : '厂家',
	store :  new Ext.data.ArrayStore({
		fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:FACTORY
	}),
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	editable : false,
	triggerAction : 'all',
	width :130
});



/**
 * 创建主体部分
 */
var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'form',
	width:800,
	items : [{
		width:800,
		layout : 'column',
		border : false,
		style : 'margin-left:20px;margin-top:30px',
		items : [{
			border : false,
			columnWidth : .25,
			tag:'span',
 		    html:'厂家'
		},{
			border : false,
			columnWidth : .25,
			tag:'span',
 		    html:'厂家告警名称 '
		},{
			border : false,
			columnWidth : .25,
			tag:'span',
 		    html:'标准告警名称'
		},{
			border : false,
			columnWidth : .25,
			tag:'span',
 		    html:'归一化告警名称'
		}]
	},{
		layout : 'column',
		width:800,
		border : false,
		style : 'margin-left:20px;margin-top:20px',
		items : [{
			border : false,
			columnWidth : .25,
			items : factroyCombo
		},{
			border : false,
			layout : 'column',
			columnWidth : .25,
			items : [napcause,{
				border : false,
				tag:'span',
	 		    html:'&nbsp;<span style="color:red">*</span>'
			}]
		},{
			border : false,
			columnWidth : .25,
			items : nopcause
		},{
			border : false,
			layout : 'column',
			columnWidth : .25,
			items : [repcause,{
				border : false,
				tag:'span',
	 		    html:'&nbsp;<span style="color:red">*</span>'
			}]
		}]
	}],
	buttons: [{
        text: '确定',
        style : 'margin-left:10px;',
        handler : function(){
        	addAlarmNormlized();
        }
    },{
        text: '取消',
        style : 'margin-left:10px;',
        handler : function(){
        	var win = parent.Ext.getCmp('addAlarmNormlizedWindow');
			if(win){
				win.close();
			}
        }
    }]
});

/**
 * 新增/修改告警重定义
 */
function addAlarmNormlized(){
	
	
	// 厂家ID
	var FACTORY_ID = Ext.getCmp('factroyCombo').getValue();
	if(FACTORY_ID==''){
		Ext.Msg.alert('错误', '请选择厂家');
		return false;
	}
	// 原告警名称
	var NATIVE_PROBABLE_CAUSE = Ext.getCmp('napcause').getValue();
	if(NATIVE_PROBABLE_CAUSE==''){
		Ext.Msg.alert('错误', '请输入厂家告警名称');
		return false;
	}
	// 标准告警名称
	var NORM_PROBABLE_CAUSE = Ext.getCmp('nopcause').getValue();
//	if(NORM_PROBABLE_CAUSE==''){
//		Ext.Msg.alert('错误', '请输入标准告警名称');
//		return false;
//	}
	// 归一化告警名称
	var REDEFINE_PROBABLE_CAUSE = Ext.getCmp('repcause').getValue();
	if(REDEFINE_PROBABLE_CAUSE==''){
		Ext.Msg.alert('错误', '请输入归一化告警名称');
		return false;
	}
	if(!Ext.getCmp('napcause').isValid()){
		Ext.Msg.alert('错误', '请输入正确格式');
		return false;
	}
	if(!Ext.getCmp('nopcause').isValid()){
		Ext.Msg.alert('错误', '请输入正确格式');
		return false;
	}
	if(!Ext.getCmp('repcause').isValid()){
		Ext.Msg.alert('错误', '请输入正确格式');
		return false;
	}
	// 请求地址
	var url = '';
	// 请求参数
	var params = '';
	if(type=='add'){
		url = 'fault!addAlarmNormlized.action';
		params = {'jsonString':Ext.encode({'FACTORY_ID':FACTORY_ID,'NATIVE_PROBABLE_CAUSE':NATIVE_PROBABLE_CAUSE,'NORM_PROBABLE_CAUSE':NORM_PROBABLE_CAUSE,'REDEFINE_PROBABLE_CAUSE':REDEFINE_PROBABLE_CAUSE})};
	}else{
		url = 'fault!modifyAlarmNormlized.action';
		params = {'jsonString':Ext.encode({'FACTORY_ID':FACTORY_ID,'NATIVE_PROBABLE_CAUSE':NATIVE_PROBABLE_CAUSE,'NORM_PROBABLE_CAUSE':NORM_PROBABLE_CAUSE,'REDEFINE_PROBABLE_CAUSE':REDEFINE_PROBABLE_CAUSE,'redefineId':redefineId})};
	}
	Ext.Ajax.request({
	    url: url,
	    method: 'POST',
	    params: params,
	    success : function(response) {
	    	parent.store.load({
	    		callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
	    	});
	    	var win = parent.Ext.getCmp('addAlarmNormlizedWindow');
			if(win){
				win.close();
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
	})
}

/**
 * 初始化参数
 */
function initData(){
	if(type=='modify'){
		Ext.Ajax.request({
		    url: 'fault!getAlarmNormlizedById.action',
		    method: 'POST',
		    params: {'jsonString':Ext.encode({'redefineId':redefineId})},
		    success : function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	// 厂家
		    	Ext.getCmp('factroyCombo').setValue(obj.FACTORY_ID);
		    	// 厂家告警名称
		    	Ext.getCmp('napcause').setValue(obj.NATIVE_PROBABLE_CAUSE);
		    	// 标准告警名称
		    	Ext.getCmp('nopcause').setValue(obj.NORM_PROBABLE_CAUSE);
		    	//归一化告警名称
		    	Ext.getCmp('repcause').setValue(obj.REDEFINE_PROBABLE_CAUSE);
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		})
	}
}

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initData();
  	new Ext.Viewport({
        layout : 'border',
        items : centerPanel
	});
 });
