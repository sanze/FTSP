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

//--------------------------------------------------------
//====开始日期====@
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

//====结束日期====@
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

//====开始月====@
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

//====结束月====@
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


//----------------------------------------------------



var storeNe = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsGroup","ems", "subNet", "ne", "neType", "emsId", "subNetId", "neId",
			"nodeLevel", 'nodeId' ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cmNe = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	},{
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 100
	}, {
		id : 'subNet',
		header : '子网',
		dataIndex : 'subNet',
		width : 100
	}, {
		id : 'ne',
		header : '网元',
		dataIndex : 'ne',
		width : 150
	}, {
		id : 'neType',
		header : '网元型号',
		dataIndex : 'neType',
		width : 100
	}, {
		id : 'type',
		header : '类型',
		dataIndex : 'nodeLevel',
		hidden : true,
		width : 100
	} ]
});

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	 title:'网元选择',
	store : storeNe,
	cm : cmNe,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ {
		text : '新增',
		icon : '../../../resource/images/btnImages/add.png',
		handler : addNe
	}, {
		text : '删除',
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
			var items = grid.getSelectionModel().getSelections();
			if(items.length>0){
				Ext.Msg.confirm("提示","是否删除选中的对象？",function(btn){
					if(btn=="yes"){
						for ( var i = 0; i < items.length; i++) {
							storeNe.remove(items[i]);
						}
						storeNe.commitChanges();
					}
				});
			}else{
				Ext.Msg.alert("提示","请先选取对象！");
			}
		}
	}, {
		text : '清空',
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {
			Ext.Msg.confirm("提示","是否清空所有对象数据？",function(btn){
				if(btn=="yes"){
					storeNe.removeAll();
					storeNe.commitChanges();
				}
			});
		}
	} ],
	buttons : [ '->', {
		text : '立即生成报表',
		//icon : '../../../resource/images/buttonImages/submit.png',
		handler : generateImmediately
	} ]
});
//---------------------------------------------------
var periodCb;
(function() {
	var data = [ [ 0, '光路误码监测记录表' ], [ 1, '光功率记录表' ] ];
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
		anchor : '95%',
		editable:false,
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
		Ext.getCmp('interval').show();
	}
	if (combo.getValue() == 1) {
		Ext.getCmp('startTime').hide();
		Ext.getCmp('endTime').hide();
		Ext.getCmp('startMonth').show();
		Ext.getCmp('endMonth').show();
		Ext.getCmp('pmDate').show();
		Ext.getCmp('interval').hide();
	}
});
// -----------------------------------------------------------------
// -----------------------------------------------------------------------
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

var rowI = {
	layout : 'column',
	width:1120,
	border : false,
	items : [ {
		columnWidth : 1,
		border : false,
		layout : 'form',
		items : [ {
			xtype : 'fieldset',
			title : '基本设置 ',
			layout : 'column',
			items : [
					{
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
							fieldLabel : '报表名称'
						} ]
					},/* {
						columnWidth : 0.24,
						layout : 'form',
						labelWidth : 80,
						border : false,
						items : [ privilegeCombo ]

					}, */{
						columnWidth : 0.24,
						layout : 'form',
						labelWidth : 60,
						border : false,
						items : [ periodCb ]

					} ]
		} ]
	} ]

};

//--------------------------------------------------------
//每月性能日期
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
		allowBlank:false,
		valueField : 'id',
		displayField : 'pmDate'
	});
})();
//-------------------------------------------------------------------

 var interval = {
		xtype : 'numberfield',
		id : 'interval',
//		hidden : true,
		// disabledClass:'x-item-disabled-modified',
		anchor : '90%',
		maxValue : 10,
		minValue : 0,
//		value:0,
		allowDecimals : false,
		allowNegative : false,
		sideText : '<font color=red>*</font>',
		emptyText : '(0~10天，0为每天)',
		allowBlank : false,
		fieldLabel : '日期间隔'
	}; 

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
				items : [  startTime, startMonth  ]
			}, {
				columnWidth : 0.24,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [endTime, endMonth]
			}, {
				columnWidth : 0.24,
				layout : 'form',
				labelWidth : 60,
				border : false,
				items : [ pmDate, interval ]
			} ]
		} ]
	} ]

};

var SDHTPLevel = {
	id : 'SDHTPLevel',
	xtype : 'checkboxgroup',
	columns : 6,
	width:500,
//	 anchor:'90%',
	items : [ {
		checked : true,
		boxLabel : 'STM1',
		inputValue : 'STM-1'
	}, {
		checked : true,
		boxLabel : 'STM4',
		inputValue : 'STM-4'
	}, {
		checked : true,
		boxLabel : 'STM16',
		inputValue : 'STM-16'
	}, {
		checked : true,
		boxLabel : 'STM64',
		inputValue : 'STM-64'
	}, {
		checked : true,
		boxLabel : 'STM256',
		inputValue : 'STM-256'
	}]
};


var SDHTP = {
		xtype : 'fieldset',
		labelWidth : 10,
//		anchor : '95%', 	
		width:1120,
		title : 'TP等级',
		height : 60,
		items : SDHTPLevel
	};


var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	labelAlign : 'left',
	height : 250,
	autoScroll:true,
	collapsible : true,
	items : [ rowI, rowII, SDHTP ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

function beforeGen() {
	var list = new Array();
	storeNe.each(function(record) {
		var nodes = {
			'nodeId' : record.get('nodeId'),
			'nodeLevel' : record.get('nodeLevel')
		};
		list.push(Ext.encode(nodes));
	});
	var params = {
		'sList' : list
	};
		Ext.Ajax.request({
			url : 'instant-report!neCountCheck.action',
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
}

function generateImmediately() {
	
	var tpLevel = Ext.getCmp('SDHTPLevel').getValue(1);
	//@
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var privilege = Ext.getCmp('privilege').getValue();
	var reportType = Ext.getCmp('periodCb').getValue();// [ 0, '光路误码监测记录表' ], [ 1, '光功率记录表' ]
	//@
	var start;
	var end;
	var interval = Ext.getCmp('interval').getValue();
	if(reportType==0){
		start = Ext.getCmp('startTime').getValue();
		end = Ext.getCmp('endTime').getValue();
		if(!dayLimitCheck(start,end))
			return;
	}else if(reportType==1){
		start = Ext.getCmp('startMonth').getValue();
		end = Ext.getCmp('endMonth').getValue();
		if(!monthLimitCheck(start,end))
			return;
	}
	
	if(!Ext.getCmp('reportTaskName').isValid()){
		Ext.Msg.alert("提示", "请输入报表名称！");
		return;
	}
	if(start=='' || start==null){
		Ext.Msg.alert("提示", "请输入起始时间！");
		return;
	}
	if(end=='' || end==null){
		Ext.Msg.alert("提示", "请输入结束时间！");
		return;
	}
	if(Ext.getCmp('interval').hidden==false&&(!Ext.getCmp('interval').isValid())){
		Ext.Msg.alert("提示", "请输入日期间隔！");
		return;
	}
	if(pmDate.hidden==false&&(!pmDate.isValid())){
		Ext.Msg.alert("提示", "请输入性能日期！");
		return;
	}
	
	var list = new Array();
	storeNe.each(function(record) {
		var nodes = {
			'nodeId' : record.get('nodeId'),
			'nodeLevel' : record.get('nodeLevel')
		};
		list.push(Ext.encode(nodes));
	});
	if(list.length==0){
		Ext.Msg.alert("提示", "请选择网元！");
		return;
	}
	var params = {
		'sList' : list,
		'condMap.taskName' : taskName,
		// 'condMap.privilege' : privilege,
		'condMap.reportType' : reportType,
		'condMap.start' : start,
		'condMap.end' : end,
		'condMap.interval' : interval == '' ? 0 : interval,
		'condMap.pmDate' : pmDate.getValue(),
		'condMap.tpLevel' : tpLevel
	};
	Ext.Ajax.request({
		url : 'instant-report!neCountCheck.action',
		method : 'POST',
		params : params,
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult == 0) {
//				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", result.returnMessage);
			} else {
				Ext.getBody().mask('执行中...');
//				var url = reportType == 0 ? 'instant-report!generateOptPathBitErrReport.action?'
//						: 'instant-report!generateSDHLightPowerReport.action?';
//				top.Ext.getBody().unmask();
//				window.location.href = url + Ext.urlEncode(params);
				Ext.Ajax.request({
					url :reportType == 0 ? 'instant-report!generateOptPathBitErrReport.action'
							: 'instant-report!generateSDHLightPowerReport.action',
					method : 'POST',
					params : params,
					success : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
				    	if(obj.returnResult == 1){
					    	window.location.href="instant-report!download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
			            }
			        	if(obj.returnResult == 0){
			        		Ext.Msg.alert("提示","报表生成失败！");
			        	}
					},
					failure : function(response) {
						Ext.getBody().unmask();
						var result = Ext.util.JSON.decode(response.responseText);
						Ext.Msg.alert("错误", result.returnMessage);
					}
				})
			}
		},
		failure : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("错误", result.returnMessage);
		}
	});
	
}
//-------------------------------------------------------
//检查日期跨度
function dayLimitCheck(start,end){
	var startTime = NewDate(start);
	var endTime = NewDate(end);
	if(endTime-startTime <0)
	{
	    Ext.Msg.alert("提示","结束时间不能早于开始时间！");
	    return false;
	}
	if((endTime-startTime)/1000/60/60/24 >30)
	{
	    Ext.Msg.alert("提示","间隔时间不超过30天！");
	    return false;
	}
	return true;
}
//检查月份跨度
function monthLimitCheck(start,end){
	start+='-01';
	end+='-01';
	var startTime = NewDate(start);
	var endTime = NewDate(end);
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

function NewDate(str) {
	str = str.split('-');
	var date = new Date();
	date.setUTCFullYear(str[0], str[1] - 1, str[2]);
	date.setUTCHours(0, 0, 0, 0);
	return date;
	} 
//--------------------------------------------
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
				title : "新增报表",
				layout : 'border',
				// autoScroll:true,
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			win.show();
		});