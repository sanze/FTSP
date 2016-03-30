/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

//Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";
/*var Data = [ [ '123' ], [ '1234' ], [ '1235' ], [ '1236' ], [ '1237' ],
 [ '1234' ], [ '1235' ], [ '1236' ], [ '1237' ], [ '1238' ] ];
 var dataStore = new Ext.data.ArrayStore({
 fields : [ {
 name : 'linkName'
 }

 ]
 });
 dataStore.loadData(Data);*/
var result = [];
var nodes = null;
var nodeLevel = null;
var flag = 2;
var aLocationLevel = 0;
var aLocationId = -1;
var rt = Ext.data.Record.create([ {
	name : 'nodeId'
}, {
	name : 'nodeLevel'
}, {
	name : 'text'
}, {
	name : 'path:text'
} ]);

var dataStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		id : 'nodeId'
	}, rt)
});

// ==================页面====================
var panel = new Ext.Panel({
	region : "center",
	width : 50,
	border : false,
	flex : 1,
	frame : false,
	bodyStyle : 'padding:20px 15px 0',
	items : [ {
		layout : 'column',
		border : false,
		items : [ {
			layout : 'form',
			border : false,
			items : [ {
				xtype : "label",
				width : 20,
				height : 5,
				border : false
			}, {
				xtype : 'button',
				width : 20,
				height : 15,
				border : false,
				icon : '../../resource/images/btnImages/right2.gif',
				handler : selectTreeA
			}, {
				xtype : 'button',
				width : 20,
				height : 15,
				border : false,
				handler : removeTarget,
				icon : '../../resource/images/btnImages/left2.gif'
			}, {
				xtype : 'button',
				width : 20,
				height : 15,
				border : false,
				handler : removeAllTarget,
				icon : '../../resource/images/btnImages/all.gif'
			} ]
		} ]
	} ]
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'equipmentName',
		header : '设备信息',
		width : 500,
		dataIndex : 'path:text'
	} ]
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	flex : 1,
	hideHeaders : true,
	height : 130,
	cm : cm,
	border : false,
	width : 600,
	autoScroll : true,
	store : dataStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	forceFit : true,
	frame : false
});

var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	checkModel : "multiple",
	rootVisible : false,
	containerId : 'westPanel',
	leafType : 8
};

// 共通树url
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);

var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "west",
	width : 280,
	height : 800,
	minSize : 230,
//	maxSize : 320,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	filterBy: filterFTP,
	html : '<iframe id="tree_panel" name = "tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0/>'
});
function filterFTP(tree, parent, node){
	if(node.attributes["nodeLevel"]==8&&
		node.attributes["additionalInfo"]&&
		(node.attributes["additionalInfo"]["PTP_FTP"]==1)){
		return false;// 不显示FTP
	}
}

var rateStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ],
	data : [ [ '64C', '64C' ], [ '16C', '16C' ], [ '8C', '8C' ],
			[ '4C', '4C' ], [ 'VC4', 'VC4' ], [ 'VC3', 'VC3' ],
			[ 'VC12', 'VC12' ],['','全部']]
});

var rateCombo = new Ext.form.ComboBox({
	id : 'rateCombo',
	name : 'rateCombo',
	fieldLabel : '电路速率',
	store : rateStore,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	value : '',
	editable : false,
	allowBlank : false,
	anchor : '95%'
});

var stateStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ],
	data : [ [ '1', '完整' ], [ '0', '不完整' ] ,['','全部']]
});

var stateCombo = new Ext.form.ComboBox({
	id : 'stateCombo',
	name : 'stateCombo',
	fieldLabel : '电路类别',
	store : stateStore,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	value : '',
	mode : 'local',
	editable : false,
	allowBlank : false,
	anchor : '95%'
});

var serviceTypeStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ],
	data : [ [ '1', 'SDH' ], [ '2', '以太网' ], [ '3', 'OTN' ], [ '4', 'PTN' ] ]
});

var serviceTypeCombo = new Ext.form.ComboBox({
	id : 'serviceTypeCombo',
	name : 'serviceTypeCombo',
	fieldLabel : '业务类型',
	store : serviceTypeStore,
	displayField : "displayName",
	valueField : 'value',
	triggerAction : 'all',
	value : '1',
	mode : 'local',
	editable : false,
	allowBlank : false,
	anchor : '95%'
});
// ================stores===================
var aWindow;
var locationPanelA = new Ext.Panel(
		{
			region : "center",
			frame : false,
			bodyStyle : 'padding:10px 0px 20px',
			layout : 'column',
			border : false,
			autoScroll : true,
			items : [
					{
						columnWidth : .2,
						border : false,
						items : [ {
							xtype : 'button',
							id : 'a_locationBtn',
							text : '地点选择',
							handler : function() {
								var areaTree_A = {
									region : 'center',
									xtype : 'area',
									maxLevel : 12,
									width : 200,
									autoScroll : true,
									split : true,
									singleSelection : false,
									id : "vt_A"
								};
								 aWindow = new Ext.Window(
										{
											title : '地点选择',
											height:0.7*Ext.getCmp('formPanel').getHeight(),
											width : 250,
											minWidth : 200,
											layout : 'fit',
											plain : false,
											modal : true,
											constrain:true,
											resizable : true,
											closeAction : 'hide',// 关闭窗口
											bodyStyle : 'padding:1px;',
											items : [ areaTree_A ],
											buttons : [ {
												text : '确定',
												handler : function() {
													var a_location = Ext
															.getCmp('vt_A')
															.getSelectedNodes();
													if (a_location.total == 0) {
														Ext.Msg.alert("提示",
																"选择地点");
													} else if (a_location.nodes[0].level < 5) {
														Ext.Msg
																.alert("提示",
																		"地点选择时必须选择到“"+top.FieldNameDefine.STATION_NAME+"”或“机房”");
													} else {
														var re = /(\([^\(\)]*\))/g;
														var locationName = a_location.nodes[0].fullPath
																.replace(re,
																		"/");
														Ext
																.getCmp(
																		'location_A')
																.setValue(
																		locationName
																				.substring(
																						0,
																						locationName.length));
														aLocationId = a_location.nodes[0].id;
														aLocationLevel = a_location.nodes[0].level;
														aWindow.hide();
													}
												}
											} ]
										});
								aWindow.show();
							}
						} ]

					}, {
						columnWidth : .6,
						border : false,
						items : [ {
							xtype : 'textfield',
							id : 'location_A',
							width : 280,
							readOnly : true
						} ]

					} ,{
						columnWidth : .2,
						border : false,
						items : [ {
							xtype : "button",
							text : '重置',
							handler : function() {
								Ext.getCmp('location_A').reset();
								aLocationLevel = 0;
								aLocationId = -1;
							}
						} ]
					}]
		});
var infoPanel = new Ext.FormPanel({
	id : 'infoPanel',
	name : 'infoPanel',
	labelSeparator : "：",
	labelWidth :60,
	labelPad : 0,
	bodyStyle : 'padding:30px 10px 10px',
	border : false,
	items : [ locationPanelA, {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .5, // 第一列
			layout : 'form',
			border : false,
			items : [ rateCombo ]
		}, {
			columnWidth : .5, // 第二列
			layout : 'form',
			border : false,
			items : [ stateCombo ]
		} ]
	}, {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .5,
			layout : 'form',
			border : false,
			items : [ serviceTypeCombo ]
		}, {
			columnWidth : .5,
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'useFor',
				name : 'useFor',
				fieldLabel : '用途',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		} ]
	}, {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .5, // 第一列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'numberfield',
				id : 'sourceNo',
				name : 'sourceNo',
				fieldLabel : '光缆编号',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth : .5, // 第二列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'systemSourceNo',
				name : 'systemSourceNo',
				fieldLabel : '资源编号',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		} ]
	}, {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .5, // 第一列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'numberfield',
				id : 'linkId',
				name : 'linkId',
				fieldLabel : '链路编号',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth : .5, // 第二列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'circuitName',
				name : 'circuitName',
				fieldLabel : '路由名称',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		} ]
	}, {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .5, // 第一列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'numberfield',
				id : 'circuitNo',
				name : 'circuitNo',
				fieldLabel : '电路编号',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		}, {
			columnWidth : .5, // 第二列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'clientName',
				name : 'clientName',
				fieldLabel : '客户名称',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		} ]
	} ,{
		layout : 'column',
		border : false,
		items :[{
			columnWidth : .5, // 第一列
			layout : 'form',
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'AdvancedCon',
				name : 'AdvancedCon',
				fieldLabel : '模糊查询',
				maxLength : 64,
				allowBlank : true,
				anchor : '95%'
			} ]
		}]
	}]
});

var formPanel = new Ext.Panel({
	id : 'formPanel',
	region : "center",
	border : false,
	frame : false,
	autoScroll : true,
	labelWidth : 120,
	// layout:'border',
	width : 200,
	listeners:{
		bodyresize:function(){
			if(aWindow){
				aWindow.setHeight(0.7*Ext.getCmp('formPanel').getHeight());
			}
		}
	},
	bodyStyle : 'padding:10px 10px 0;',
	items : [ {
		width : 600,
		layout : 'form',
		border : false,
		autoScroll : true,
		labelSeparator : "：",
		items : [ {
			layout : 'column',
			title : '设备选择',
			items : [ {
				columnWidth : .1,
				height : 130,
				items : [ panel ]
			}, {
				columnWidth : .9,
				height : 130,
				border : false,
				items : [ gridPanel ]
			} ]
		}, {
			layout : 'form',
			width : 600,
			border : false,
			items : [ infoPanel ]
		}, {
			xtype : '',
			text : '',
			border : false,
			height : 10
		}, {
			labelWidth : 60,
			border : false,
			items : [ {
				layout : 'hbox',
				border : false,
				items : [ {
					xtype : '',
					border : false,
					flex : 12,
					text : ''
				}, {
					xtype : 'button',
					width : 70,
					flex : 2,
					icon : '../../resource/images/btnImages/search.png',
					text : '查询',
					privilege:viewAuth,
					handler : selectData
				}, {
					xtype : '',
					border : false,
					flex : 1,
					text : ''
				}, {
					xtype : 'button',
					width : 70,
					icon : '../../resource/images/btnImages/arrow_undo.png',
					flex : 2,
					text : '重置',
					handler : resetLink
				} ]
			} ]
		} ]
	} ]
});
// -----------手动更新链路----------------
function selectData() {
	if(infoPanel.getForm().isValid()){
		// var getNode=Ext.encode(result);
		var array = new Array();

		if (dataStore.getCount () > 0) {
			for ( var i = 0; i <dataStore.getCount (); i++) {
				array.push(dataStore.data.items[i].json.nodeId);
				nodes = nodes + array[i] + "/";
			}
			nodeLevel = dataStore.data.items[0].json.nodeLevel;
		} else {
			nodes = null;
			nodeLevel = null;
		}if(dataStore.getCount()>5){
			Ext.Msg.alert("提示","选择对象最大不能超过5个");
		}else{
			var conditions = {
					connectRate : Ext.getCmp('rateCombo').getValue(),
					circuitState : Ext.getCmp('stateCombo').getValue(),
					serviceType : Ext.getCmp('serviceTypeCombo').getValue(),
					linkId : Ext.getCmp('linkId').getValue(),
					circuitNo : Ext.getCmp('circuitNo').getValue(),
					systemSourceNo : Ext.getCmp('systemSourceNo').getValue(),
					sourceNo : Ext.getCmp('sourceNo').getValue(),
					aLocationId : aLocationId,
					aLocationLevel : aLocationLevel,
					flag : 2,
					nodes : nodes,
					nodeLevel : nodeLevel
				};

				var url = "../circuitManager/selectCircuitPortReasult.jsp?"
						+ Ext.urlEncode(conditions) + "&clientName="
						+ encodeURI(encodeURI(Ext.getCmp('clientName').getValue()))
						+ "&circuitName="
						+ encodeURI(encodeURI(Ext.getCmp('circuitName').getValue()))
						+ "&useFor="
						+ encodeURI(encodeURI(Ext.getCmp('useFor').getValue()))
						+ "&advancedCon="
						+ encodeURI(encodeURI(Ext.getCmp('AdvancedCon').getValue()));

				parent.addTabPage(url, "电路相关性查询",authSequence);
		}
		nodes = null;
		nodeLevel = null;
	}
}

// ------------重置---------------------
function resetLink() {
	Ext.Msg.confirm('提示', '确认清空？', function(btn) {
		if (btn == 'yes') {
			dataStore.removeAll();
			result = [];
			Ext.getCmp('location_A').reset();
			aLocationLevel = 0;
			aLocationId = -1;
			infoPanel.getForm().reset();
			var westPanel = Ext.getCmp('westPanel');
			if (westPanel) {
				westPanel.update(westPanel.initialConfig.html, true);
			}
		}
	});
}

// -------------定义全局变量------------------
var treetypeA = 9;
var treetypeZ = 9;

var checkedNodeIdsA = new Array();
var checkedNodeIdsZ = new Array();
// -----------------------------------------
function selectTreeA() {

	var message;
	var iframe = window.frames["tree_panel"] || window.frames[0];
	// 兼容不同浏览器的取值方式
	if (iframe.getCheckedNodes) {
		result = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "text",
				"path:text" ]);
	} else {
		result = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel",
				"text", "path:text" ]);
	}
	// 判断store中是否有元素
	
	var i = 1;
	if (result.length == 0)
		Ext.Msg.alert('提示', '请选择');
	else if (result.length > 0) {
		if(result.length>5){
			Ext.Msg.alert("提示","选择对象最大不能超过5个！");
		}else{
			for (i; i < result.length; i++) {
				if (result[0].nodeLevel != result[i].nodeLevel) {
					Ext.Msg.alert('提示', '不能查询不同等级设备');
					break;
				}
			}
			if (i == result.length) {
				if (dataStore.getCount () > 0) {
					if(result[0].nodeLevel==dataStore.data.items[0].json.nodeLevel){
						dataStore.loadData(result, true);
					}else  Ext.Msg.alert('提示', '不能查询不同等级设备');
				}else dataStore.loadData(result, true);
			}
		}
	}
}
function removeTarget() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 0) {
		Ext.Msg.alert("提示", "请选择需要删除的条件");
	} else {
		for ( var i = 0; i < cell.length; i++) {
			result.remove(cell[i].json);
		}
		dataStore.remove(cell);
		gridPanel.getView().refresh();
	}
}

function removeAllTarget() {
	if (dataStore.getTotalCount() > 0) {
		Ext.Msg.confirm('提示', '确认清空？', function(btn) {
			if (btn == 'yes') {
				result = [];
				dataStore.removeAll();
			}
		});
	}
}

// -----------------------------------------init the
// page--------------------------------------------

// ************************************************************************************

Ext.onReady(function() {
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 9000000;
	var win = new Ext.Viewport({
		id : 'win',
		title : "B类比较值设定",
		layout : 'border',
		items : [ formPanel, westPanel ],
		renderTo : Ext.getBody()
	});
	win.show();

});