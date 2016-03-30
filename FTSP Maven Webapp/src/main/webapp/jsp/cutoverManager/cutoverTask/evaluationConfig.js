/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//全局变量定义
//全局变量定义
var confirmOrApply;//0代表应用，1代表确认
/*------------------------------------------冲突电路------------------------------------------*/

var configStore = new Ext.data.Store({
	url : 'cutover-task!getEvaluationConfig.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "key","value" ])
});

// ==========================page=============================
//var circuitCheckboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
//	singleSelect : true,
//	header : ""
//});
//circuitCheckboxSelectionModel.sortLock();
var configCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26
	}), {
		id : 'key',
		header : '割接评估项',
		dataIndex : 'key',
		width : 300
	}, {
		id : 'value',
		header : '量化值',
		dataIndex : 'value',
		width : 100
	} ]
});



var configGridPanel = new Ext.grid.EditorGridPanel({
	id : "configGridPanel",
	region : "center",
	cm : configCm,
	store : configStore,
	stripeRows : true, // 交替行效果
	viewConfig : {
		forceFit : true
	},
//	loadMask : {
//		msg : '数据加载中...'
//	},
fbar : [ {
		text : '确定',
		id : 'ok',
		name : 'ok',
		privilege:modAuth,
		handler : function() {
			confirmOrApply = 1;
			applyConfig();
		}
	}, {
		text : '取消',
		id : 'cancel',
		name : 'cancel',
		handler : function() {
			var window = parent.Ext.getCmp('configWindow');
			if (window) {
				window.close();
			}
		}
	}, {
		text : '应用',
		id : 'apply',
		name : 'apply',
		privilege:modAuth,
		handler : function() {
			confirmOrApply = 0;
			applyConfig();
		}
	}, {
		xtype : 'label',
		width : '15px'
	} ]
});



/*-----------------------------------------------割接任务详细信息-----------------------------------------*/
var westPanel = new Ext.form.FormPanel({
	title : "",
	id : "westPanel",
	region : "west",
	bodyStyle : 'padding:10px 15px 10px 15px;',
	width : 350,
	autoScroll : true,
	collapsible : false,
	labelWidth : 60,
	// labelAlign: 'right',
	
	// defaults: {
	// anchor: '100%',
	// labelStyle:"margin-bottom:10px;",
	// style:"margin-bottom:10px;"
	// },
	items : [
			 {xtype:'fieldset',
				labelWidth:1,
				title:'割接结果等级',
				layout:"form",
				items:[{
					xtype : 'compositefield',
//					width:0,
					flex : 4,
					items : [{
						xtype : 'displayfield',
						value : "完美:",
						fieldLabel : '',
						flex : 1
					}, {

						xtype : 'textfield',
						id : 'perfect',
						flex : 1

					}, {
						xtype : 'displayfield',
						value : "≥评分",
						flex : 1
					},{
						xtype : 'displayfield',
						value : " ",
						flex : 1
					}]
				}, {
					xtype : 'compositefield',
//					width:0,
					flex : 4,
					items : [{
						xtype : 'displayfield',
						value : "优秀:",
						fieldLabel : '',
						flex : 1
					}, {

						xtype : 'textfield',
						id : 'excellent',
						flex : 1

					}, {
						xtype : 'displayfield',
						value : "≤评分≤",
						flex : 1
					},{

						xtype : 'textfield',
						id : 'excellent2',
						flex : 1

					} ]
				}, {
					xtype : 'compositefield',
//					width:0,
					flex : 4,
					items : [{
						xtype : 'displayfield',
						value : "良好:",
						fieldLabel : '',
						flex : 1
					}, {

						xtype : 'textfield',
						id : 'good',
						flex : 1

					}, {
						xtype : 'displayfield',
						value : "≤评分≤",
						fieldLabel : '',
						flex : 1
					},{

						xtype : 'textfield',
						id : 'good2',
						flex : 1

					} ]
				}, {
					xtype : 'compositefield',
//					width:0,
					flex : 4,
					items : [{
						xtype : 'displayfield',
						value : "一般:",
						flex : 1
					}, {

						xtype : 'textfield',
						id : 'average',
						flex : 1

					}, {
						xtype : 'displayfield',
						value : "≤评分≤",
						flex : 1
					},{

						xtype : 'textfield',
						id : 'average2',
						flex : 1

					} ]
				},{
					xtype : 'compositefield',
//					width:0,
					flex : 4,
					items : [{
						xtype : 'displayfield',
						value : "差劲:",
						fieldLabel : '',
						flex : 1
					}, {

						xtype : 'textfield',
						id : 'bad',
						flex : 1

					}, {
						xtype : 'displayfield',
						value : "≤评分",
						flex : 1
					},{
						xtype : 'displayfield',
						value : " ",
						flex : 1
					} ]
				}]}, {
				xtype : 'fieldset',
				title : '光功率差值判定门限:',
				height:100,
				layout:"form",
				items : [ {

						xtype : 'textfield',
						id : 'sdhConfig',
						fieldLabel: 'SDH'

					}, {

						xtype : 'textfield',
						id : 'wdmConfig',
						fieldLabel: 'WDM'

					} ]
			} ]
});



/*-----------------------------------------------方法部分-----------------------------------------*/


// -----------------------------页面初始化数据----------------------------------
function initData() {

configStore.load({
					callback : function(records, options, success) {
						if (success) {
						    var config = configStore.reader.jsonData;
						    Ext.getCmp("perfect").setValue(config.perfectUpperBound);
						    Ext.getCmp("excellent").setValue(config.excellentLowerBound);
						    Ext.getCmp("excellent2").setValue(config.excellentUpperBound);
						    Ext.getCmp("good").setValue(config.goodLowerBound);
						    Ext.getCmp("good2").setValue(config.goodUpperBound);
						    Ext.getCmp("average").setValue(config.averageLowerBound);
						    Ext.getCmp("average2").setValue(config.averageUpperBound);
						    Ext.getCmp("bad").setValue(config.badLowerBounder);
						    Ext.getCmp("sdhConfig").setValue(config.SDHDifferent);
						    Ext.getCmp("wdmConfig").setValue(config.WDMDifferent);
						}
					}
				});
}
function applyConfig(confirmType)
{
		var jsonData = {
		"searchCondition.perfectUpperBound" : Ext.getCmp("perfect").getValue(),
		"searchCondition.excellentLowerBound" : Ext.getCmp("excellent").getValue(),
		"searchCondition.excellentUpperBound" : Ext.getCmp("excellent2").getValue(),
		"searchCondition.goodLowerBound" : Ext.getCmp("good").getValue(),
		"searchCondition.goodUpperBound" : Ext.getCmp("good2").getValue(),
		"searchCondition.averageLowerBound" : Ext.getCmp("average").getValue(),
		"searchCondition.averageUpperBound" : Ext.getCmp("average2").getValue(),
		"searchCondition.badLowerBounder" : Ext.getCmp("bad").getValue(),
		"searchCondition.SDHDifferent" : Ext.getCmp("sdhConfig").getValue(),
		"searchCondition.WDMDifferent" : Ext.getCmp("wdmConfig").getValue()
	};
	Ext.Ajax.request({
			url : 'cutover-task!modifyEvaluationConfig.action',
			method : 'Post',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 0) {
					Ext.Msg.alert("提示", obj.returnMessage);
				}
				if (obj.returnResult == 1) {
					Ext.Msg.alert("提示", obj.returnMessage);
					if (confirmOrApply == 1) {
						var window = parent.Ext.getCmp('configWindow');
						if (window) {
							window.close();
						}
					}
				}
	
			},
			error : function(response) {
				Ext.Msg.alert("提示", response.responseText);
			},
			failure : function(response) {
				Ext.Msg.alert("提示", response.responseText);
			}
	
		})
}
Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}

	if (saveType == 2) {
		var win = new Ext.Viewport({
			id : 'win',
			loadMask : true,
			layout : 'border',
			items : [ centerPanel, eastPanel ],
			renderTo : Ext.getBody()
		});
		// Ext.getCmp('ok').setDisabled(true);
		Ext.getCmp('apply').setVisible(false);
		Ext.getCmp('cancel').setVisible(false);
		Ext.getCmp('handUp').setVisible(false);
		Ext.getCmp('setButton').setVisible(false);
	} else {
		var win = new Ext.Viewport({
			id : 'win',
			// width:900,
			loadMask : true,
			layout : 'border',
			autoScroll : true,
			items : [ configGridPanel, westPanel ],
			renderTo : Ext.getBody()
		});
	}
	initData();
	
});