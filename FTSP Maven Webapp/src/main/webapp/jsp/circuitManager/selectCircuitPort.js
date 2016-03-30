/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

//Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
//===========================全局变量============================
//共通树显示格式参数
var flag = 1;
var aLocationLevel = 0;
var aLocationId = -1;
var zLocationLevel = 0;
var zLocationId = -1;
var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	rootVisible : false,
	checkModel : "single",
	leafType : 8
};

// 共通树url
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);

// ===========================定义组件============================
// 定义一个westpanel用来存放共通树
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
	html : '<iframe id="tree_panel" name = "tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0/>'
});

var aWindow;
// 定义locationPanelA用来存放按地点查询的选择框
var locationPanelA = new Ext.Panel(
		{
			region : "center",
			frame : false,
			bodyStyle : 'padding:20px 30px 20px',
			layout : 'column',
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
//									collapsible : true,
//									title : '区域选择',
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
											// id : 'aWindow',
											title : '地点选择',
											width : 250,
											height:0.7*Ext.getCmp('formPanel').getHeight(),
											minWidth : 200,
											constrain:true,
											layout : 'fit',
											plain : false,
											modal : true,
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
						}]
					},{
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
					} ]
		});

var zWindow;
// 定义locationPanelZ用来存放按地点查询的选择框
var locationPanelZ = new Ext.Panel(
		{
			region : "center",
			frame : false,
			bodyStyle : 'padding:20px 30px 20px',
			layout : 'column',
			autoScroll : true,
			items : [
					{
						columnWidth : .2,
						border : false,
						items : [ {
							xtype : 'button',
							id : 'z_locationBtn',
							text : '地点选择',
							handler : function() {
								var areaTree = {
									region : 'center',
									collapsible : false,
//									title : '区域选择',
									xtype : 'area',
									maxLevel : 12,
									width : 200,
									autoScroll : true,
									split : true,
									singleSelection : false,
									id : "vt"
								};
								 zWindow = new Ext.Window(
										{
											// id : 'aWindow',
											title : '地点选择',
											width : 250,
											minWidth : 200,
											height:0.7*Ext.getCmp('formPanel').getHeight(),
											layout : 'fit',
											plain : false,
											modal : true,
											resizable : false,
											closeAction : 'hide',// 关闭窗口
											bodyStyle : 'padding:1px;',
											items : [ areaTree ],
											buttons : [ {
												text : '确定',
												handler : function() {
													var z_location = Ext
															.getCmp('vt')
															.getSelectedNodes();
													if (z_location.total == 0) {
														Ext.Msg.alert("提示",
																"选择地点");
													} else if (z_location.nodes[0].level < 5) {
														Ext.Msg
																.alert("提示",
																		"地点选择时必须选择到“"+top.FieldNameDefine.STATION_NAME+"”或“机房”");
													} else {
														var re = /(\([^\(\)]*\))/g;
														var locationName = z_location.nodes[0].fullPath
																.replace(re,
																		"/");
														Ext
																.getCmp(
																		'location_Z')
																.setValue(
																		locationName
																				.substring(
																						0,
																						locationName.length));
														zLocationId = z_location.nodes[0].id;
														zLocationLevel = z_location.nodes[0].level;
														zWindow.hide();
													}
												}
											} ]
										});
								zWindow.show();
							}
						} ]

					}, {
						columnWidth : .6,
						border : false,
						items : [ {
							xtype : 'textfield',
							id : 'location_Z',
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
								Ext.getCmp('location_Z').reset();
								zLocationLevel = 0;
								zLocationId = -1;
							}
						} ]
					}]
		});

// 定义equipPanelA用来存放按设备查询的选择框
var equipPanelA = new Ext.Panel({
	region : "center",
	frame : false,
	height : 220,
	bodyStyle : 'padding:20px 30px 0',
	labelAlign : 'right',
	autoScroll : true,
	items : [ {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .2,
			layout : 'form',
			region : "center",
			border : false,
			labelSeparator : "：",
			items : [ {
				width : 20,
				height : 35,
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
				handler : removeA,
				icon : '../../resource/images/btnImages/left2.gif'
			} ]
		}, {
			columnWidth : .8,
			xtype :'form',
			layout : 'form',
			border : false,
			labelWidth :40,
			labelPad :2,
			labelSeparator : "：",
			items : [ {
				width : 20,
				height : 25,
				border : false
			}, {
				xtype : 'textfield',
				id : 'aNeId',
				name : 'aNeId',
				fieldLabel : '网元',
				width : 200,
				readOnly : true
			// anchor: '95%'
			}, {
				xtype : 'textfield',
				id : 'aUnit',
				name : 'aUnit',
				readOnly : true,
				fieldLabel : '板卡',
				width : 200
			// anchor: '95%'
			}, {
				xtype : 'textfield',
				id : 'aPort',
				readOnly : true,
				name : 'aPort',
				fieldLabel : '端口',
				width : 200
			// anchor: '95%'
			} ]
		} ]
	} ]
});

// 定义equipPanelZ用来存放按设备查询的选择框
var equipPanelZ = new Ext.Panel({
	region : "center",
	frame : false,
	height : 220,
	bodyStyle : 'padding:20px 30px 0',
	labelAlign : 'right',
	autoScroll : true,
	items : [ {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : .2,
			layout : 'form',
			region : "center",
			border : false,
			labelSeparator : "：",
			items : [ {
				width : 20,
				height : 35,
				border : false
			}, {
				xtype : 'button',
				width : 20,
				height : 15,
				border : false,
				icon : '../../resource/images/btnImages/right2.gif',
				handler : selectTreeZ
			}, {
				xtype : 'button',
				width : 20,
				height : 15,
				border : false,
				handler : removeZ,
				icon : '../../resource/images/btnImages/left2.gif'
			} ]
		}, {
			columnWidth : .8,
			xtype :'form',
			layout : 'form',
			border : false,
			labelWidth :40,
			labelPad :2,
			labelSeparator : "：",
			items : [ {
				width : 20,
				height : 25,
				border : false
			}, {
				xtype : 'textfield',
				id : 'zNeId',
				readOnly : true,
				name : 'zNeId',
				fieldLabel : '网元',
				width : 200
			// anchor: '95%'
			}, {
				xtype : 'textfield',
				id : 'zUnit',
				readOnly : true,
				name : 'zUnit',
				fieldLabel : '板卡',
				width : 200
			// anchor: '95%'
			}, {
				xtype : 'textfield',
				id : 'zPort',
				readOnly : true,
				name : 'zPort',
				fieldLabel : '端口',
				width : 200
			// anchor: '95%'
			} ]
		} ]
	} ]
});

// 定义名为aEndInfo的一个区域用来存放A端的locationPanel和equipPanel组件
var aEndInfo = {
	xtype : 'fieldset',
	title : 'A端参数选择',
	region : "center",
	items : [ new Ext.TabPanel({
		id : 'tabs1',
		border : false,
		region : "center",
		height : 180,
		activeTab : 0,
		// plugins: new Ext.ux.TabCloseMenu(),
		items : [ {
			title : '设备选择',
			items : [ equipPanelA ]
		} /*
			 * , { title : '地点选择', items : [ locationPanelA ] }
			 */]
	}), locationPanelA ]
};

// 定义名为zEndInfo的一个区域用来存放Z端的locationPanel和equipPanel组件
var zEndInfo = {
	xtype : 'fieldset',
	title : 'Z端参数选择',
	region : "center",
	items : [ new Ext.TabPanel({
		id : 'tabs2',
		region : "center",
		border : false,
		height : 180,
		activeTab : 0,
		// plugins: new Ext.ux.TabCloseMenu(),
		items : [ {
			title : '设备选择',
			items : [ equipPanelZ ]
		} /*
			 * , { title : '地点选择', items : [ locationPanelZ ] }
			 */]
	}), locationPanelZ ]
};

// 定义一个eastPanel用来存放aEndInfo和zEndInfo组件
var eastPanel = new Ext.Panel({
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
			if(zWindow){
				zWindow.setHeight(0.7*Ext.getCmp('formPanel').getHeight());
			}
		}
	},
	bodyStyle : 'padding:10px 10px 0;',
	items : [ {
		width : 600,
		layout : 'form',
		border : false,
		labelSeparator : "：",
		labelWidth : 85,
		labelPad : 0,
		items : [ {
			xtype : 'combo',
			id : 'serviceTypeCombo',
			mode : 'local',
			fieldLabel : '电路业务类型',
			store : new Ext.data.ArrayStore({
				fields : [ 'value', 'displayName' ],
				data : [ [ 1, 'SDH' ], [ 2, '以太网' ], [ 3, 'OTN/WDM' ],[ 4, 'PTN' ] ]
			}),
			valueField : 'value',
			displayField : 'displayName',
			triggerAction : 'all',
			value : 1
		}, aEndInfo, zEndInfo, {
			labelWidth : 60,
			border : false,
			items : [ {
				layout : 'hbox',
				border : false,
				items : [ {
					border : false,
					flex : 12
				}, {
					xtype : 'button',
					width : 70,
					border : false,
					flex : 2,
					icon : '../../resource/images/btnImages/search.png',
					text : '查询',
					privilege:viewAuth,
					handler : selectData

				}, {
					border : false,
					flex : 1
				}, {
					xtype : 'button',
					border : false,
					width : 70,
					flex : 2,
					icon : '../../resource/images/btnImages/arrow_undo.png',
					text : '重置',
					handler : resetLink

				} ]
			} ]
		} ]
	} ]

});

function selectData() {
	if (aNodeLevel == 0 && zNodeLevel == 0 && aLocationLevel == 0
			&& zLocationLevel == 0) {
		Ext.Msg.alert("提示", "请设置A端和Z端两端参数！");
	} else {
		serviceType = Ext.getCmp('serviceTypeCombo').getValue();
		var url = "../circuitManager/selectCircuitPortReasult.jsp?aNodeId="
				+ aNodeId + "&zNodeId=" + zNodeId + "&aNodeLevel=" + aNodeLevel
				+ "&zNodeLevel=" + zNodeLevel + "&flag=" + flag
				+ "&aLocationLevel=" + aLocationLevel + "&aLocationId="
				+ aLocationId + "&zLocationLevel=" + zLocationLevel
				+ "&zLocationId=" + zLocationId + "&serviceType=" + serviceType;
		parent.addTabPage(url, "电路清单",authSequence);
	}
}

// ------------重置---------------------
function resetLink() {
	Ext.getCmp('aNeId').reset();
	Ext.getCmp('aUnit').reset();
	Ext.getCmp('aPort').reset();
	Ext.getCmp('zNeId').reset();
	Ext.getCmp('zUnit').reset();
	Ext.getCmp('zPort').reset();
	Ext.getCmp('location_A').reset();
	Ext.getCmp('location_Z').reset();
	aNodeId = -1;
	aNodeLevel = 0;
	zNodeId = -1;
	zNodeLevel = 0;
	aLocationLevel = 0;
	aLocationId = -1;
	zLocationLevel = 0;
	zLocationId = -1;
}

function removeA() {
	Ext.getCmp('aNeId').reset();
	Ext.getCmp('aUnit').reset();
	Ext.getCmp('aPort').reset();
	aNodeId = -1;
	aNodeLevel = 0;
}

function removeZ() {
	Ext.getCmp('zNeId').reset();
	Ext.getCmp('zUnit').reset();
	Ext.getCmp('zPort').reset();
	zNodeId = -1;
	zNodeLevel = 0;
}

// -------------定义全局变量------------------
var treetypeA = 9;
var treetypeZ = 9;
var aNodeId = -1;
var aNodeLevel = 0;
var zNodeId = -1;
var zNodeLevel = 0;
var checkedNodeIdsA = new Array();
var checkedNodeIdsZ = new Array();
// -----------------------------------------
function selectTreeA() {
	checkedNodeIdsA = new Array();
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var aNode;
	// 兼容不同浏览器的取值方式
	if (iframe.getCheckedNodes) {
		aNode = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "level");
	} else {
		aNode = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel" ],
				"level");
	}
	if (aNode.length < 1)
		Ext.Msg.alert("重新选择", "选择不能为空，请重新选择");
	else if (aNode.length > 1)
		Ext.Msg.alert("重新选择", "不能多选，请重新选择");
	else if (aNode[0].nodeLevel < 4)
		Ext.Msg.alert("重新选择", "查询最大单位是网元，请重新选择");
	else {
		aNodeId = aNode[0].nodeId;
		aNodeLevel = aNode[0].nodeLevel;
		var jsonData = {
			"jsonString" : Ext.encode(aNode[0])
		};
		Ext.Ajax.request({
			url : 'circuit!getEquipmentName.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var object = Ext.decode(response.responseText);
				Ext.getCmp('aNeId').setValue(object.neName);
				Ext.getCmp('aUnit').setValue(object.unitName);
				Ext.getCmp('aPort').setValue(object.portName);
			},
			failure : function(response) {
				Ext.Msg.alert("异常", response.responseText);
			}
		});
	}
}

function selectTreeZ() {
	checkedNodeIdsZ = new Array();
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var zNode;
	// 兼容不同浏览器的取值方式
	if (iframe.getCheckedNodes) {
		zNode = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "all");
	} else {
		zNode = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel" ],
				"all");
	}

	if (zNode.length < 1)
		Ext.Msg.alert("重新选择", "选择不能为空，请重新选择");
	else if (zNode.length > 1)
		Ext.Msg.alert("重新选择", "不能多选，请重新选择");
	else if (zNode[0].nodeLevel < 4)
		Ext.Msg.alert("重新选择", "查询最大单位是网元，请重新选择");
	else {
		zNodeId = zNode[0].nodeId;
		zNodeLevel = zNode[0].nodeLevel;
		var jsonData = {
			"jsonString" : Ext.encode(zNode[0])
		};
		Ext.Ajax.request({
			url : 'circuit!getEquipmentName.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var object = Ext.decode(response.responseText);
				Ext.getCmp('zNeId').setValue(object.neName);
				Ext.getCmp('zUnit').setValue(object.unitName);
				Ext.getCmp('zPort').setValue(object.portName);
			},
			failure : function(response) {
				Ext.Msg.alert("异常", response.responseText);
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
		items : [ eastPanel, westPanel ],
		renderTo : Ext.getBody()
	});
	win.show();

});