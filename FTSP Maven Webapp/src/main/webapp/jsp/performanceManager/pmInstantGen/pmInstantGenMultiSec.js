Ext.override(Ext.form.CheckboxGroup, {
	getValue : function(mode) {
		var v = [];
		if (mode == 1) {
			this.items.each(function(item) {
				if (item.getValue())
					v.push(item.getRawValue());
			});
			return v;
		} else {
			this.items.each(function(item) {
				v.push(item.getValue());
			});
			return v;
		}
	}
});
Ext.override(Ext.form.BasicForm, {
	isValid : function(){
        var valid = true;
        this.items.each(function(f){
        	//console.log(f);
           if(!f.hidden && !f.validate()){
               valid = false;
           }
        });
        return valid;
    }
});
// ----------------------------------------------------

var privilegeStore = new Ext.data.Store({
	url : 'pm-report!getPrivilegeGroupList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ {
		name : "groupId",
		mapping : "SYS_USER_GROUP_ID"
	}, {
		name : "groupName",
		mapping : "GROUP_NAME"
	} ])
});
privilegeStore.load({
	callback : function(r, options, success) {
		if (success) {
			var initValue = privilegeStore.reader.jsonData.initValue;
			privilegeCombo.setValue(initValue);
		} else {
			var obj = Ext.decode(r.responseText);
			Ext.Msg.alert("提示", obj.returnMessage);
		}
	}
});
var privilegeCombo = new Ext.ux.form.LovCombo({
	id : 'privilege',
	fieldLabel : '报表共享范围',
	hideOnSelect : false,
	width : 200,
	anchor : '95%',
	editable : false,
	triggerAction : 'all',
	store : privilegeStore,
	displayField : 'groupName',
	valueField : 'groupId'
});
// ---------------------------------------------------------
var storeMulti = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "trunkLineName", "MSName", "direction", "MSId", "MSNameList",
			"MSNameTag", 'TLId' ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cmMulti = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'trunkLineName',
		header : '干线名称',
		dataIndex : 'trunkLineName',
		width : 100
	}, {
		id : 'MSNameList',
		header : '包含复用段',
		dataIndex : 'MSNameList',
		hidden : true,
		width : 150,
		renderer : MSNameListRenderer
	}, {
		id : 'MSName',
		header : '复用段名称',
		dataIndex : 'MSName',
		width : 100
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'direction',
		width : 100,
		renderer : directionRenderer
	} ]
});
// ---------------------------------------------------
var periodCb;
(function() {
	var data = [ [ 0, '每日' ], [ 1, '每月' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'period'
		} ]
	});
	store.loadData(data);
	periodCb = new Ext.form.ComboBox({
		id : 'periodCb',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '类型',
		editable:false,
		anchor : '95%',
		store : store,
		value : 0,
		valueField : 'id',
		displayField : 'period'
	});
})();
periodCb.on('select', function(combo, record, index) {
	if (combo.getValue() == 0) {
		Ext.getCmp('startTime').show();
		Ext.getCmp('endTime').show();
		Ext.getCmp('startMonth').hide();
		Ext.getCmp('endMonth').hide();
		Ext.getCmp('pmDate').hide();
	}
	if (combo.getValue() == 1) {
		Ext.getCmp('startTime').hide();
		Ext.getCmp('endTime').hide();
		Ext.getCmp('startMonth').show();
		Ext.getCmp('endMonth').show();
		Ext.getCmp('pmDate').show();
	}
});
// -----------------------------------------------------------------

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	store : storeMulti,
	cm : cmMulti,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-',{
		text : '添加复用段',
		privilege:addAuth,
		id : 'addMS',
		icon : '../../../resource/images/btnImages/add.png',
		handler : addMS
	}, '-',{
		text : '添加干线',
		privilege:addAuth,
		id : 'addTL',
		disabled:true,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addTL
	}, '-',{
		text : '删除对象',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : del
	}, '-',{
		text : '清空对象',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {
			Ext.Msg.confirm("提示","是否清空所有对象数据？",function(btn){
				if(btn=='yes'){
				storeMulti.removeAll();
				triggerForButton();
				}
			});
		}
	} ],
	buttons : [ '->', {
		text : '立即生成报表',
		privilege:addAuth,
//		icon : '../../../resource/images/buttonImages/submit.png',
		handler : beforeGen
	} ]
});
// -----------------------------------------------------------------------
var rowI = {
	layout : 'column',
	border : false,
	width:1120,
	items : [ {
		columnWidth : 1,
		border : false,
		layout : 'form',
		items : [ {
			xtype : 'fieldset',
			title : '基本设置 ',
			layout : 'column',
			items : [ {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ {
					xtype : 'textfield',
					id : 'reportTaskName',
					anchor : '95%',
					allowBlank : false,
					sideText : '<font color=red>*</font>',
					fieldLabel : '任务名称'
				} ]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 80,
				border : false,
				items : [ privilegeCombo ]

			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 40,
				border : false,
				items : [ periodCb ]

			} ]
		} ]
	} ]

};
// --------------------------------------------------------
// ====开始日期====@
var startTime = {
	xtype : 'textfield',
	id : 'startTime',
	name : 'startTime',
	fieldLabel : '起始日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	// width : 160,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
//				minDate:  '#F{$dp.$D(\'endTime\',{d:-40})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startTime = Ext.getCmp('startTime').getValue();
//			var endTime = Ext.getCmp('endTime').getValue();
//			if(startTime&&endTime)
//				dayLimitCheck(startTime,endTime);
		}
	}
};

// ====结束日期====@
var endTime = {
	xtype : 'textfield',
	id : 'endTime',
	name : 'endTime',
	fieldLabel : '结束日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	anchor : '95%',
	// width : 160,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
				minDate:'#F{$dp.$D(\'startTime\',{d:0})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startTime = Ext.getCmp('startTime').getValue();
//			var endTime = Ext.getCmp('endTime').getValue();
//			if(startTime&&endTime)
//				dayLimitCheck(startTime,endTime);
		}
	}
};

// ====开始月====@
var startMonth = {
	xtype : 'textfield',
	id : 'startMonth',
	name : 'startMonth',
	fieldLabel : '起始月份',
	sideText : '<font color=red>*</font>',
	hidden : true,
	allowBlank : false,
	readOnly : true,
	// width : 160,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startMonth",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M',
				dateFmt : 'yyyy-MM',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startMonth = Ext.getCmp('startMonth').getValue();
//			var endMonth = Ext.getCmp('endMonth').getValue();
//			if(startMonth&&endMonth)
//				monthLimitCheck(startMonth,endMonth);
		}
	}
};

// ====结束月====@
var endMonth = {
	xtype : 'textfield',
	id : 'endMonth',
	name : 'endMonth',
	fieldLabel : '结束月份',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	hidden : true,
	readOnly : true,
	anchor : '95%',
	// width : 160,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endMonth",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M',
				minDate:'#F{$dp.$D(\'startMonth\',{d:0})}',
				dateFmt : 'yyyy-MM',
				isShowToday : false,
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startMonth = Ext.getCmp('startMonth').getValue();
//			var endMonth = Ext.getCmp('endMonth').getValue();
//			if(startMonth&&endMonth)
//				monthLimitCheck(startMonth,endMonth);
		}
	}
};


// ----------------------------------------------------
// 每月性能日期
var pmDate;
(function() {
	var data = [ [ 1, '1日' ], [ 2, '2日' ], [ 3, '3日' ], [ 4, '4日' ],
			[ 5, '5日' ], [ 6, '6日' ], [ 7, '7日' ], [ 8, '8日' ], [ 9, '9日' ],
			[ 10, '10日' ], [ 11, '11日' ], [ 12, '12日' ], [ 13, '13日' ],
			[ 14, '14日' ], [ 15, '15日' ], [ 16, '16日' ], [ 17, '17日' ],
			[ 18, '18日' ], [ 19, '19日' ], [ 20, '20日' ], [ 21, '21日' ],
			[ 22, '22日' ], [ 23, '23日' ], [ 24, '24日' ], [ 25, '25日' ],
			[ 26, '26日' ], [ 27, '27日' ], [ 28, '28日' ], [ 29, '29日' ],
			[ 30, '30日' ], [ 31, '31日' ] ];
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'pmDate'
		} ]
	});
	store.loadData(data);
	pmDate = new Ext.form.ComboBox({
		id : 'pmDate',
		triggerAction : 'all',
		mode : 'local',
		fieldLabel : '性能日期',
		anchor : '95%',
		store : store,
		hidden : true,
		value : 1,
		valueField : 'id',
		displayField : 'pmDate'
	});
})();
// -------------------------------------------------------------------

// --------------------------------------------------------------
var rowII = {
	layout : 'column',
	width:1120,
	border : false,
	items : [ {
		columnWidth : 1,
		border : false,
		layout : 'form',
		items : [ {
			xtype : 'fieldset',
			title : '报表时间 ',
			layout : 'column',
			items : [ {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ startTime, startMonth ]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ endTime, endMonth ]
			}, {
				columnWidth : 0.2,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ pmDate ]
			} ]
		} ]
	} ]

};

var northPanel = new Ext.Panel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll:true,
	labelAlign : 'left',
	height : 190,
	layout : 'form',
	collapsible : true,
	items : [ rowI, rowII ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
function directionRenderer(v) {
	if (v == 1)
		return '单向';
	if (v == 2)
		return '双向';
}
function MSNameListRenderer(v, m, r) {
	var tag = "<div title=\"" + r.get('MSNameTag') + "\">" + v + "</div>";
	return tag;

}
//从已选删除
function del() {
	var selected = grid.getSelectionModel().getSelections();
	if (selected && selected.length > 0) {
		Ext.Msg.confirm("提示","是否删除选中的对象？",function(btn){
			if(btn=="yes"){
				for ( var i = 0; i < selected.length; i++) {
					storeMulti.remove(selected[i]);
				}
				triggerForButton();
			}
		});
	}else{
		Ext.Msg.alert("提示","请先选取对象！");
	}
}

// 控制添加复用段和干线的按钮
function triggerForButton() {
	if (storeMulti.getCount() > 0) {
		if (storeMulti.getAt(0).get('TLId')) {
			Ext.getCmp('addMS').setDisabled(true);
			Ext.getCmp('addTL').setDisabled(false);
		}
		if (storeMulti.getAt(0).get('MSId')) {
			Ext.getCmp('addMS').setDisabled(false);
			Ext.getCmp('addTL').setDisabled(true);
		}
	} else {
		Ext.getCmp('addMS').setDisabled(false);
		Ext.getCmp('addTL').setDisabled(false);
	}
}

function beforeGen() {
//	if (Ext.getCmp('northPanel').getForm().isValid()) {
		if (storeMulti.getCount() < 1) {
			Ext.Msg.alert('信息', '请添加复用段或干线！');
			return;
		}
		var taskName = Ext.getCmp('reportTaskName').getValue();
		var param = {
			'searchCond.taskName' : taskName
		};
		Ext.Ajax.request({
			url : 'pm-report!checkTaskNameDuplicate.action',
			method : 'POST',
			params : param,
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if (result.returnResult == 0) {
					Ext.Msg.alert("提示", result.returnMessage);
				} else {
					generateImmediately();
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
//	}
}

function generateImmediately() {
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var privilege = Ext.getCmp('privilege').getValue();
	var period = Ext.getCmp('periodCb').getValue();// 0:每日；1：每月
	var pmDate = Ext.getCmp('pmDate').getValue();
	var start;
	var end;
	if(period==0){
		start = Ext.getCmp('startTime').getValue();
		end = Ext.getCmp('endTime').getValue();
		if(!dayLimitCheck(start,end))
			return;
		
	}else if(period==1){
		start = Ext.getCmp('startMonth').getValue();
		end = Ext.getCmp('endMonth').getValue();
		if(!monthLimitCheck(start,end))
			return;
	}
	var list = new Array();
	storeMulti.each(function(record) {
		var nodes;
		if (storeMulti.getAt(0).get('MSId'))
			nodes = {
				'targetId' : record.get('MSId'),
				'targetType' : targetIdMultiSec
			};
		if (storeMulti.getAt(0).get('TLId'))
			nodes = {
				'targetId' : record.get('TLId'),
				'targetType' : targetIdTrunkLine
			};
		list.push(Ext.encode(nodes));
	});
	var params = {
		'modifyList' : list,
		'searchCond.taskName' : taskName,
		'searchCond.taskType' : 9,
		'searchCond.privilege' : privilege,
		'searchCond.period' : period,
		'searchCond.start' : start,
		'searchCond.end' : end,
		'searchCond.pmDate' : pmDate,
		'searchCond.hour':1,
		'searchCond.delay':1
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'pm-report!generateMsReportImmediately.action',
		method : 'POST',
		params : params,
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
	    	
	    	if(obj.returnResult == 1){
	    		Ext.Msg.alert("信息","报表生成成功！");
		    	//window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
            }
        	if(obj.returnResult == 0){
        		Ext.Msg.alert("信息","报表生成失败！");
        	}
		},
		failure : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

//-------------------------------------------------------
//检查日期跨度
function dayLimitCheck(start,end){
	var startTime = new Date(start.replace(/-/g, "/"));
	var endTime = new Date(end.replace(/-/g, "/"));
	if(endTime-startTime <0)
	{
	    Ext.Msg.alert("提示","结束时间不能早于开始时间！");
	    return false;
	}
	if((endTime-startTime)/1000/60/60/24 >40)
	{
	    Ext.Msg.alert("提示","间隔时间不超过40天！");
	    return false;
	}
	return true;
}
//检查月份跨度
function monthLimitCheck(start,end){
	var startTime = new Date(start.replace(/-/g, "/"));
	var endTime = new Date(end.replace(/-/g, "/"));
	if(endTime.getFullYear()==startTime.getFullYear()){
		if(endTime.getMonth()-startTime.getMonth()>12){
			 Ext.Msg.alert("提示","不能超过12个月！");
			 return false;
		}
	}else{
		if(endTime.getFullYear()-startTime.getFullYear()>1){
			 Ext.Msg.alert("提示","不能超过12个月！");
			 return false;
		}else if(startTime.getMonth()-endTime.getMonth()<0){
			 Ext.Msg.alert("提示","不能超过12个月！");
			 return false;
		}
	}
	return true;
}
//-------------------------------------------------------
Ext
		.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var win = new Ext.Viewport({
				id : 'win',
				title : "自动作业报表即时生成",
				layout : 'border',
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			win.show();
		});