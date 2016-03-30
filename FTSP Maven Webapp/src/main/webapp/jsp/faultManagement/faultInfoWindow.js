

//设备故障的故障定位
function eqptFaultLocation(){
	
	var unitContent = {
		layout : 'column',
		anchor : '95%',
		border : false,
		items : [{
			layout : 'form',
			columnWidth : .90,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'unitEqpt',
				fieldLabel : '板卡',
				readOnly : true,
				anchor : '95%'
			}]
		},{
			xtype : 'button',
			columnWidth : .10,
			text : '...',
			id : 'unitSelectBtn',
			handler :function(){
				var treeParams = {
						rootId : 0,
						rootType : 0,
						rootText : "FTSP",
						checkModel : "single",
						rootVisible : false,
						// 规定数显示到的层数。8表示树可以展开的到第八个层级
						leafType : 6
					};
				var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
				var modifyTreePanel = new Ext.Panel({
					id : "modifyTreePanel",
					region : "west",
					width : 210,
					height : 400,
					forceFit : true,
					collapsed : false, // initially collapse the group
					collapsible : false,
					collapseMode : 'mini',
					split : true,
					html : '<iframe id="modifytreePanel" name = "modifytreePanel" src ="'
							+ treeurl + '" height="100%" width="100%" frameBorder=0 border=0/>'
				});
				var addTreeWindow_Z = new Ext.Window({
					id : 'addTreeWindow_Z',
					title : '板卡选择',
					width : 300,
					autoHeight : true,
					minWidth : 350,
					minHeight : 400,
					layout : 'fit',
					plain : false,
					modal : true,
					constrain : true,
					resizable : false,
					bodyStyle : 'padding:1px;',
					items : [ modifyTreePanel ],
					buttons : [ {
						text : '确定',
						handler : function() {
							var iframe = window.frames["tree_panel"] || window.frames[0];
							var zNode;
							// 兼容不同浏览器的取值方式
							if (iframe.getCheckedNodes) {
								zNode = iframe.getCheckedNodes(null, "all");
							} else {
								zNode = iframe.contentWindow.getCheckedNodes(null,
										"all");
							}

							if (zNode.length < 1)
								Ext.Msg.alert("重新选择", "选择不能为空，请重新选择");
							else if (zNode.length > 1)
								Ext.Msg.alert("重新选择", "不能多选，请重新选择");
							else if (zNode[0]['attributes']['nodeLevel'] != 6)
								Ext.Msg.alert("重新选择", "请选择板卡");
							else {
								zNodeId = zNode[0]['attributes'].nodeId;
								
								zNodeLevel = zNode[0]['attributes'].nodeLevel;
								var unitName=zNode[0]['attributes']['additionalInfo']['UNIT_DESC'];
								var emsName=zNode[0]['attributes']['emsName'];
								var parentNode = zNode[0];
								var subnetName,neName,neId;
								while(parentNode){
									if(parentNode['attributes']['nodeLevel']==4){
										neName=parentNode['attributes']['text'];
										neId = parentNode['attributes'].nodeId;
									}else if(parentNode['attributes']['nodeLevel']==3){
										subnetName=parentNode['attributes']['text'];
									}else if(parentNode['attributes']['nodeLevel']<3){
										break;
									}
									parentNode=parentNode.parentNode;
								}
								
								Ext.getCmp('neEqpt').setValue(neName);
								Ext.getCmp('emsEqpt').setValue(emsName);
								Ext.getCmp('unitEqpt').setValue(unitName);
								
								var jsonData = {
									"paramMap.unitId" : zNodeId,
									"paramMap.neId" : neId
								};
								Ext.Ajax.request({
									url : 'fault-management!getEquipFaultLocationInfo.action',
									method : 'POST',
									params : jsonData,
									success : function(response) {
										var obj = Ext.decode(response.responseText);
										Ext.getCmp("transSystemEqpt").setValue(obj.sysName);
										Ext.getCmp("stationEqpt").setValue(obj.stationName);
										Ext.getCmp("factoryEqpt").setValue(factoryMap[obj.factory]);
										addTreeWindow_Z.close();
									},
									failure : function(response) {
										Ext.Msg.alert("异常", "无法连接到服务器");
									}
								});
							}
							
							enableSaveFaultInfoBtn();
						}
					}, {
						text : '取消',
						handler : function() {
							addTreeWindow_Z.close();
						}
					} ]
				});
				addTreeWindow_Z.show();
			} 
		}]
	};
	
	var neContent = {
		layout : 'column',
		anchor : '95%',
		border : false,
		items : [{
			layout : 'form',
			columnWidth : .90,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'neEqpt',
				fieldLabel : '网元',
				readOnly : true,
				anchor : '95%'
			}]
		},{
			xtype : 'button',
			columnWidth : .10,
			text : '...',
			id : 'neSelectBtn',
			handler :function(){
				var treeParams = {
						rootId : 0,
						rootType : 0,
						rootText : "FTSP",
						checkModel : "single",
						rootVisible : false,
						// 规定数显示到的层数。8表示树可以展开的到第八个层级
						leafType : 4
					};
				var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
				var modifyTreePanel_ne = new Ext.Panel({
					id : "modifyTreePanel_ne",
					region : "west",
					width : 210,
					height : 400,
					forceFit : true,
					collapsed : false, // initially collapse the group
					collapsible : false,
					collapseMode : 'mini',
					split : true,
					html : '<iframe id="modifytreePanel_ne" name = "modifytreePanel_ne" src ="'
							+ treeurl + '" height="100%" width="100%" frameBorder=0 border=0/>'
				});
				var addTreeWindow_ne = new Ext.Window({
					id : 'addTreeWindow_ne',
					title : '网元选择',
					width : 300,
					autoHeight : true,
					minWidth : 350,
					minHeight : 400,
					layout : 'fit',
					plain : false,
					modal : true,
					constrain : true,
					resizable : false,
					bodyStyle : 'padding:1px;',
					items : [ modifyTreePanel_ne ],
					buttons : [ {
						text : '确定',
						handler : function() {
							var iframe = window.frames["tree_panel"] || window.frames[0];
							var zNode;
							// 兼容不同浏览器的取值方式
							if (iframe.getCheckedNodes) {
								zNode = iframe.getCheckedNodes(null, "all");
							} else {
								zNode = iframe.contentWindow.getCheckedNodes(null,
										"all");
							}

							if (zNode.length < 1)
								Ext.Msg.alert("重新选择", "选择不能为空，请重新选择");
							else if (zNode.length > 1)
								Ext.Msg.alert("重新选择", "不能多选，请重新选择");
							else if (zNode[0]['attributes']['nodeLevel'] != 4)
								Ext.Msg.alert("重新选择", "请选择网元");
							else {
								zNodeId = zNode[0]['attributes'].nodeId;
								
								zNodeLevel = zNode[0]['attributes'].nodeLevel;
//								var unitName=zNode[0]['attributes']['additionalInfo']['UNIT_DESC'];
								var emsName=zNode[0]['attributes']['emsName'];
								var parentNode = zNode[0];
								var subnetName,neName,neId;
								while(parentNode){
									if(parentNode['attributes']['nodeLevel']==4){
										neName=parentNode['attributes']['text'];
										neId = parentNode['attributes'].nodeId;
									}else if(parentNode['attributes']['nodeLevel']==3){
										subnetName=parentNode['attributes']['text'];
									}else if(parentNode['attributes']['nodeLevel']<3){
										break;
									}
									parentNode=parentNode.parentNode;
								}
								
								Ext.getCmp('neEqpt').setValue(neName);
								Ext.getCmp('emsEqpt').setValue(emsName);
//								Ext.getCmp('unitEqpt').setValue(unitName);
								
								var jsonData = {
									"paramMap.unitId" : 0,
									"paramMap.neId" : neId
								};
								Ext.Ajax.request({
									url : 'fault-management!getEquipFaultLocationInfo.action',
									method : 'POST',
									params : jsonData,
									success : function(response) {
										var obj = Ext.decode(response.responseText);
										Ext.getCmp("transSystemEqpt").setValue(obj.sysName);
										Ext.getCmp("stationEqpt").setValue(obj.stationName);
										Ext.getCmp("factoryEqpt").setValue(factoryMap[obj.factory]);
										
										Ext.getCmp('unitEqpt').setValue("");
										addTreeWindow_ne.close();
									},
									failure : function(response) {
										Ext.Msg.alert("异常", "无法连接到服务器");
									}
								});
							}
							
							enableSaveFaultInfoBtn();
						}
					}, {
						text : '取消',
						handler : function() {
							addTreeWindow_ne.close();
						}
					} ]
				});
				addTreeWindow_ne.show();
			} 
		}]
	};
	
	var faultLocationContent = {
		layout : 'column',
		flex : 1,
		border : false,
		height:55,
		items : [{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'transSystemEqpt',
				fieldLabel : '传输系统',
				readOnly : true,
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			},neContent]
		},{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'emsEqpt',
				fieldLabel : '网管',
				readOnly : true,
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			},unitContent
//			{
//				xtype : 'textfield',
//				id : 'unitEqpt',
//				fieldLabel : '板卡',
//				anchor : '95%'
//			}
			]
		},{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'stationEqpt',
				fieldLabel : top.FieldNameDefine.STATION_NAME,
				readOnly : true,
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			},{
				xtype : 'textfield',
				id : 'factoryEqpt',
				fieldLabel : '设备厂家',
				readOnly : true,
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			}]
		}]
	};
	
	return faultLocationContent;
}

//线路故障的故障定位
function lineFaultLocation(){
	
	//传输系统
	var transformSystemStore_Line = new Ext.data.Store({
		reader : new Ext.data.JsonReader({
			root : 'rows',
			fields : [ 'RESOURCE_TRANS_SYS_ID', 'SYS_NAME']
		}),
		proxy : new Ext.data.HttpProxy({
			url : 'fault-management!getTransformSystemList.action',
			disableCaching : false
		})
	});
	transformSystemStore_Line.load();
	
	//系统段
//	var transSystemSectionStore_Line = new Ext.data.Store({
//		url : 'fault-management!getTransSystemSectionList.action',
//		reader : new Ext.data.JsonReader({
//			root : 'rows',
//			fields : [ 'RESOURCE_TRANS_SYS_ID', 'SYS_NAME']
//		})
//	});
	
	//光缆
	var cableStore_Line = new Ext.data.Store({
		url : 'fault-management!getCableList.action',
		reader : new Ext.data.JsonReader({
			root : 'rows',
			fields : [ 'RESOURCE_CABLES_ID', 'DISPLAY_NAME']
		})
	});
	cableStore_Line.load();
	
	//光缆段
	var cableSectionStore_Line = new Ext.data.Store({
		url : 'fault-management!getCableSectionList.action',
		reader : new Ext.data.JsonReader({
			root : 'rows',
			fields : [ 'RESOURCE_CABLE_ID', 'CABLE_NAME', 'A_END_NAME', 'Z_END_NAME']
		})
	});
//	cableSectionStore_Line.load();
	
	//距离
	var nearStationStore_Line = new Ext.data.ArrayStore({
		fields: ['STATION_NAME']
	});
	
	var nearStationData_Line = new Array();
	nearStationStore_Line.loadData(nearStationData_Line);
	
	var faultLocationContent = {
		layout : 'column',
		flex : 1,
		border : false,
		height:110,
		items : [{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'combo',
				id : 'transSystemLine',
				fieldLabel : '传输系统',
				store : transformSystemStore_Line,
				triggerAction: 'all',
				editable : false,
				displayField : "SYS_NAME",
				valueField : 'RESOURCE_TRANS_SYS_ID',
				anchor : '95%',
				listeners : {
					'select' : enableSaveFaultInfoBtn
				}
			},{
				xtype : 'combo',
				id : 'cableLine',
				fieldLabel : '光缆',
				editable : false,
				store : cableStore_Line,
				mode : 'local',
				triggerAction: 'all',
				displayField : 'DISPLAY_NAME',
				valueField : 'RESOURCE_CABLES_ID',
				anchor : '95%',
				listeners : {
					'select' : function(combo, record, index){
						cableSectionStore_Line.load({
							params : {
								"paramMap.cablesId" : record.get("RESOURCE_CABLES_ID")
							}
						});
						
						enableSaveFaultInfoBtn();
					}
				}
			},{
				xtype : 'textfield',
				id : 'startStationLine',
				fieldLabel : '起始'+top.FieldNameDefine.STATION_NAME,
				readOnly : true,
				anchor : '95%'
			},{
				xtype : 'combo',
				id : 'nearStationLine',
				store : nearStationStore_Line,
				triggerAction: 'all',
				displayField : 'STATION_NAME',
				valueField : 'STATION_NAME',
				fieldLabel : '距离',
				editable : false,
				mode : 'local',
				anchor : '95%',
				listeners : {
					'select' : enableSaveFaultInfoBtn
				}
			}]
		},{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'displayfield',//用于占位
				labelSeparator : ' ',
				fieldLabel : '　'
			},
//			         {
//				xtype : 'combo',
//				id : 'sysSectionLine',
//				fieldLabel : '系统段名称',
//				anchor : '95%'
//			},
			{
				xtype : 'combo',
				id : 'cableSectionLine',
				fieldLabel : '光缆段',
				store : cableSectionStore_Line,
				triggerAction: 'all',
				displayField : 'CABLE_NAME',
				valueField : 'RESOURCE_CABLE_ID',
				editable : false,
				mode : 'local',
				anchor : '95%',
				listeners : {
					'select' : function(combo, record, index){
						Ext.getCmp("startStationLine").setValue(record.get("A_END_NAME"));
						Ext.getCmp("endStationLine").setValue(record.get("Z_END_NAME"));
						
						nearStationData_Line = new Array();
						nearStationData_Line.push([record.get("A_END_NAME")]);
						nearStationData_Line.push([record.get("Z_END_NAME")]);
						nearStationStore_Line.loadData(nearStationData_Line);
					}
				}
			},{
				xtype : 'textfield',
				id : 'endStationLine',
				fieldLabel : '终点'+top.FieldNameDefine.STATION_NAME,
				readOnly : true,
				anchor : '95%'
			},{
				xtype : 'textfield',
				id : 'distanceLine',
				fieldLabel : '公里',
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			}]
		},{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'displayfield',//用于占位
				labelSeparator : ' ',
				fieldLabel : '　'
			},{
				xtype : 'textfield',
				id : 'maintenancerLine',
				fieldLabel : '维护单位',
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			},{
				xtype : 'textfield',
				id : 'longitudeLine',
				fieldLabel : '经度',
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			},{
				xtype : 'textfield',
				id : 'latitudeLine',
				fieldLabel : '纬度',
				anchor : '95%',
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			}]
		}]
	};
	
	return faultLocationContent;
}

//故障信息
function faultInfo() {
	
	var cell = Ext.getCmp('gridPanel').getSelectionModel().getSelections();
	if(cell.length == 0){
		Ext.Msg.alert("提示","请选择故障！");
	}else{
		var faultNo = cell[0].get('FAULT_NO');
		var faultId = cell[0].get('FAULT_ID');
		var type = cell[0].get('TYPE');
		var source = cell[0].get("SOURCE");
		
		createFaultInfoWindow(false, type, faultId, faultNo);
		
		if(source == 2){
			Ext.getCmp("analyzeAccuracyCombo").setVisible(false);
			Ext.getCmp("analyzeAccuracyDis").setVisible(false);
		}
		
		Ext.Ajax.request({
			url : 'fault-management!getFaultInfoByFaultIdAndType.action',
			method : 'POST',
			params : {
				"paramMap.faultId" : faultId,
				"paramMap.type" : type
			},
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				if(type == 1){
					initEquipWindow(obj);
				}else if(type == 2) {
					initLineWindow(obj);
				}
			},
			failue : function(response) {
				Ext.Msg.alert('错误', '访问服务器失败！');
			}
		});
	}
}

function initEquipWindow(info) {
	
	Ext.getCmp("faultGenerateDis").setValue(faultGenerateRenderer(info.SOURCE));
	Ext.getCmp("faultTypeDis").setValue(faultTypeRenderer(info.TYPE));
	Ext.getCmp("statusDis").setValue(statusRenderer(info.STATUS));
	//故障原因
	Ext.getCmp("faultReasonCombo").setValue(info.REASON1_ID);
	Ext.getCmp("faultReasonCombo").setRawValue(info.REASON_NAME);
	
	if(info.IS_BROKEN){
		Ext.getCmp('systemBrokenCheckboxEquip').setValue(true);
	}else{
		Ext.getCmp('systemBrokenCheckboxEquip').setValue(false);
	}
	
	Ext.getCmp("transSystemEqpt").setValue(info.SYSTEM_NAME);
	Ext.getCmp("emsEqpt").setValue(info.EMS_NAME);
	Ext.getCmp("stationEqpt").setValue(info.STATION_NAME);
	Ext.getCmp("neEqpt").setValue(info.NE_NAME);
	Ext.getCmp("unitEqpt").setValue(info.UNIT_NAME);
	Ext.getCmp("factoryEqpt").setValue(factoryMap[info.FACTORY]);
//	Ext.getCmp("factoryEqpt").setRawValue(info.FACTORY);
	
	if(info.START_TIME != null){
		Ext.getCmp("startTimeFaultInfo").setValue(info.START_TIME.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
				info.START_TIME.time)) : "");
	}
	if(info.ALARM_END_TIME != null){
		Ext.getCmp("alarmEndFaultInfo").setValue(info.ALARM_END_TIME.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
				info.ALARM_END_TIME.time)) : "");
	}
	if(info.END_TIME != null){
		Ext.getCmp("confirmTimeFaultInfo").setValue(info.END_TIME.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
				info.END_TIME.time)) : "");
	}
	Ext.getCmp("faultAnalyzeTextArea").setValue(info.MEMO);
	Ext.getCmp("analyzeAccuracyCombo").setRawValue(accuracyRenderer(info.ACCURACY));
	Ext.getCmp("analyzeAccuracyCombo").setValue(info.ACCURACY);
	Ext.getCmp("faultDescriptionTextArea").setValue(info.DESCRIPTION);
	
	//设置故障状态的按钮
	setStatusBtn(info.STATUS);
	
	//设置板卡选择按钮
	setUintSelectBtn(info.SOURCE);
};

//设置自动生成故障的故障定位状态为不可编辑
function setAutoFaultLocation_line(source){
	if(source != 2){
		Ext.getCmp("transSystemLine").setDisabled(true);
		Ext.getCmp("cableLine").setDisabled(true);
		Ext.getCmp("cableSectionLine").setDisabled(true);
		Ext.getCmp("nearStationLine").setDisabled(true);
		
		Ext.getCmp("distanceLine").setReadOnly(true);
		Ext.getCmp("maintenancerLine").setReadOnly(true);
		Ext.getCmp("longitudeLine").setReadOnly(true);
		Ext.getCmp("latitudeLine").setReadOnly(true);
	}
}

//设置板卡选择按钮
function setUintSelectBtn(source){
	if(source != 2){
		Ext.getCmp("unitSelectBtn").disable();
		Ext.getCmp("neSelectBtn").disable();
	}
}

function initLineWindow(info) {
	
	Ext.getCmp("faultGenerateDis").setValue(faultGenerateRenderer(info.SOURCE));
	Ext.getCmp("faultTypeDis").setValue(faultTypeRenderer(info.TYPE));
	Ext.getCmp("statusDis").setValue(statusRenderer(info.STATUS));
	
	Ext.getCmp("faultReasonCombo1").setValue(info.REASON1_ID);
	Ext.getCmp("faultReasonCombo1").setRawValue(info.REASON_NAME1);
	
	if(typeof info.REASON1_ID == 'number'){
		Ext.getCmp("faultReasonCombo2").getStore().load({
			params : {
				"paramMap.reasonType" : 2,
				"paramMap.level" : 2,
				"paramMap.parentId" : info.REASON1_ID
			}
		});
	}
	
	Ext.getCmp("faultReasonCombo2").setValue(info.REASON2_ID);
	Ext.getCmp("faultReasonCombo2").setRawValue(info.REASON_NAME2);
	
	if(info.IS_BROKEN){
		Ext.getCmp('systemBrokenCheckboxLine').setValue(true);
	}else{
		Ext.getCmp('systemBrokenCheckboxLine').setValue(false);
	}
	
	Ext.getCmp("transSystemLine").setRawValue(info.SYSTEM_NAME);
	Ext.getCmp("cableLine").setRawValue(info.CABLE_NAME);
	Ext.getCmp("startStationLine").setValue(info.A_STATION);
	Ext.getCmp("nearStationLine").setRawValue(info.NEAR_STATION);
	Ext.getCmp("cableSectionLine").setRawValue(info.CABLE_SECTION_NAME);
	Ext.getCmp("endStationLine").setValue(info.Z_STATION);
	
	var temp = new Array();
	if(info.A_STATION != null && info.A_STATION != ''){
		temp.push([info.A_STATION]);
	}
	if(info.Z_STATION != null && info.Z_STATION != ''){
		temp.push([info.Z_STATION]);
	}
	Ext.getCmp("nearStationLine").getStore().loadData(temp);
	
	Ext.getCmp("distanceLine").setValue(info.DISTANCE);
	Ext.getCmp("maintenancerLine").setValue(info.MAINTENANCER);
	Ext.getCmp("longitudeLine").setValue(info.LONGITUDE);
	Ext.getCmp("latitudeLine").setValue(info.LATITUDE);
	
	if(info.START_TIME != null){
		Ext.getCmp("startTimeFaultInfo").setValue(info.START_TIME.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
				info.START_TIME.time)) : "");
	}
	if(info.ALARM_END_TIME != null){
		Ext.getCmp("alarmEndFaultInfo").setValue(info.ALARM_END_TIME.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
				info.ALARM_END_TIME.time)) : "");
	}
	if(info.END_TIME != null){
		Ext.getCmp("confirmTimeFaultInfo").setValue(info.END_TIME.time ? (Ext.util.Format.dateRenderer('Y-m-d H:i:s'))(new Date(
				info.END_TIME.time)) : "");
	}
	Ext.getCmp("faultAnalyzeTextArea").setValue(info.MEMO);
	Ext.getCmp("analyzeAccuracyCombo").setValue(accuracyRenderer(info.ACCURACY));
	Ext.getCmp("faultDescriptionTextArea").setValue(info.DESCRIPTION);
	
	setStatusBtn(info.STATUS);
	
	//设置自动生成故障的故障定位状态为不可编辑
	setAutoFaultLocation_line(info.SOURCE);
}

var reasonParentId = 0;

/**
 * 创建故障信息弹窗
 * @param createFlag 是否新建故障
 * @param type       故障类别
 * @param faultId    故障ID
 * @param faultNo    故障编号
 */
function createFaultInfoWindow(createFlag, type, faultId, faultNo) {
	
	//保存全局变量
	if(createFlag){
		faultIdGlobal = 0;
		typeGlobal = type;
	}else{
		faultIdGlobal = faultId;
		typeGlobal = type;
	}
	
	var faultGenerateDis = {
		layout : 'form',
		columnWidth : .33,
		border : false,
		bodyStyle: 'background:#dfe8f6;',
		items : [{
			xtype : 'displayfield',
			id : 'faultGenerateDis',
			fieldLabel : '故障生成'
		}]
	};
	
	var faultTypeDis = {
		layout : 'form',
		columnWidth : .33,
		border : false,
		bodyStyle: 'background:#dfe8f6;',
		items : [{
			xtype : 'displayfield',
			id : 'faultTypeDis',
			fieldLabel : '故障类别'
		}]
	};
	
	var statusDis = {
		layout : 'form',
		columnWidth : .34,
		border : false,
		bodyStyle: 'background:#dfe8f6;',
		items : [{
			xtype : 'displayfield',
			id : 'statusDis',
			fieldLabel : '状态'
		}]
	};
	
	var northDis = {
		layout : 'column',
		border : false,
		bodyStyle : 'padding:3px 20px 3px 20px;background:#dfe8f6;',
		items : [
		    faultGenerateDis,faultTypeDis,statusDis
		]
	};
	
	var faultReasonDis = {
		layout : 'form',
	//		columnWidth : .33,
		width : 80,
		border : false,
	//		bodyStyle: 'background:#dfe8f6;',
		items : [{
			xtype : 'displayfield',
			fieldLabel : '故障原因'
		}]
	};
	
	var faultReasonContent;
	if(type == 1){
		
		//故障原因
		var faultReasonStore = new Ext.data.Store({
			url : 'fault-management!getFaultReasonList.action',
			baseParams : {
				"paramMap.reasonType" : 1,
				"paramMap.level" : 1
			},
			reader : new Ext.data.JsonReader({
				root : 'rows',
				fields : [ 'REASON_ID', 'REASON_NAME']
			})
		});
		faultReasonStore.load();
		
		faultReasonContent = {
			layout : 'column',
			border : false,
			flex : 1,
			items : [{
				xtype : 'combo',
				id : 'faultReasonCombo',
				store : faultReasonStore,
				displayField : 'REASON_NAME',
				valueField : 'REASON_ID',
				triggerAction : 'all',
				editable : false,
//				allowBlank : false,
				mode : 'local',
				columnWidth : .20,
				listeners : {
					'select' : enableSaveFaultInfoBtn
				}
			},{
				xtype : 'spacer',
				width : 25,
				height : 5
			},
			{
				xtype : 'checkbox',
				id : 'systemBrokenCheckboxEquip',
				boxLabel : '系统阻断',
				columnWidth : .33,
				listeners : {
					'change' : enableSaveFaultInfoBtn
				}
			}]
		};
	}else if(type == 2){
		
		//故障原因
		var faultReasonStore1 = new Ext.data.Store({
			url : 'fault-management!getFaultReasonList.action',
			baseParams : {
				"paramMap.reasonType" : 2,
				"paramMap.level" : 1
			},
			reader : new Ext.data.JsonReader({
				root : 'rows',
				fields : [ 'REASON_ID', 'REASON_NAME']
			})
		});
		faultReasonStore1.load();
		
		//故障原因
		var faultReasonStore2 = new Ext.data.Store({
			url : 'fault-management!getFaultReasonList.action',
			baseParams : {
				"paramMap.reasonType" : 2,
				"paramMap.level" : 2
			},
			reader : new Ext.data.JsonReader({
				root : 'rows',
				fields : [ 'REASON_ID', 'REASON_NAME']
			})
		});
		
		faultReasonContent = {
				layout : 'column',
				border : false,
				flex : 1,
				items : [{
					xtype : 'combo',
					id : 'faultReasonCombo1',
					displayField : 'REASON_NAME',
					valueField : 'REASON_ID',
					triggerAction : 'all',
					editable : false,
//					allowBlank : false,
					mode : 'local',
					store : faultReasonStore1,
					columnWidth : .20,
					listeners : {
						'select' : function(){
							reasonParentId = Ext.getCmp("faultReasonCombo1").getValue();
							faultReasonStore2.load({
								params : {
									"paramMap.reasonType" : 2,
									"paramMap.level" : 2,
									"paramMap.parentId" : reasonParentId
								},
								callback : function(r, options, success){
									Ext.getCmp("faultReasonCombo2").reset();
								}
							});
							enableSaveFaultInfoBtn();
						}
					}
				},{
					xtype : 'spacer',
					width : 10,
					height : 5
				},{
					xtype : 'combo',
					id : 'faultReasonCombo2',
					displayField : 'REASON_NAME',
					valueField : 'REASON_ID',
					triggerAction : 'all',
					mode : 'local',
					editable : false,
					store : faultReasonStore2,
					columnWidth : .20,
					listeners : {
						'select' : enableSaveFaultInfoBtn
					}
				},{
					xtype : 'spacer',
					width : 25,
					height : 5
				},{
					xtype : 'checkbox',
					id : 'systemBrokenCheckboxLine',
					boxLabel : '系统阻断',
					columnWidth : .33
				}]
			};
	}
	
	var faultReasonLine = {
		layout : 'hbox',
		border : false,
		items : [faultReasonDis, faultReasonContent]
	};
	
	var faultLocationDis = {
		layout : 'form',
	//	columnWidth : .33,
		width : 80,
		border : false,
	//	bodyStyle: 'background:#dfe8f6;',
		items : [{
			xtype : 'displayfield',
			fieldLabel : '故障定位'
		}]
	};
	
	var faultLocationContent;
	if(type == 1){
		faultLocationContent = eqptFaultLocation();
	}else if(type == 2){
		faultLocationContent = lineFaultLocation();
	}
	
	var faultLocation = {
		layout : 'hbox',
	//	layoutConfig : {
	//		pack : 'start',
	//		align : 'top'
	//	},
		border : false,
		items : [faultLocationDis, faultLocationContent]
	};
	
	var faultTimeDis = {
		layout : 'form',
		width : 80,
		border : false,
		items : [{
			xtype : 'displayfield',
			fieldLabel : '故障时间'
		}]
	};
	
	var faultTimeContent = {
		layout : 'column',
		border : false,
		flex : 1,
	//		border : false,
			height:30,
		items : [{
			layout : 'form',
			columnWidth : .33,
			labelWidth : 70,
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'startTimeFaultInfo',
				fieldLabel : '开始时间',
				readOnly : true,
				anchor : '95%'
			}]
			},{
				layout : 'form',
				columnWidth : .33,
				labelWidth : 70,
				border : false,
				items : [{
					xtype : 'textfield',
					id : 'alarmEndFaultInfo',
					fieldLabel : '告警结束',
					readOnly : true,
					anchor : '95%'
			}]
			},{
				layout : 'form',
				columnWidth : .33,
				labelWidth : 70,
				border : false,
				items : [{
					xtype : 'textfield',
					id : 'confirmTimeFaultInfo',
					fieldLabel : '确认恢复',
					readOnly : true,
					anchor : '95%'
				}]
			}
		]
	};
	
	var faultTime = {
		layout : 'hbox',
		border : false,
		items : [faultTimeDis, faultTimeContent]
	};
	
	var relatedAlarmDis = {
		layout : 'form',
		width : 80,
		border : false,
		items : [{
			xtype : 'displayfield',
			fieldLabel : '相关告警'
		}]
	};
	
	var relatedAlarmStore = new Ext.data.Store({
		id : 'relatedAlarmStore',
		baseParams : {
			"paramMap.faultId" : faultIdGlobal,
			"limit" : 200
		},
		url : 'fault-management!getFaultAlarmList.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, ["ALARM_ID", "CONVERGE_FLAG", "ALARM_NAME", "SEVERITY", 
		    "NE_NAME", "START_TIME", "CLEAN_TIME","START_TIME_STR","CLEAN_TIME_STR"]
		)
	});
	
	//显示
	if(!createFlag) {
//		relatedAlarmStore.baseParams = {
//			"paramMap.faultId" : faultId,
//			"limit" : 200
//		};
			
		relatedAlarmStore.load({
			callback : function(r, o, s){
				if(!s){
					Ext.Msg.alert("提示：","查询故障相关告警失败！");
				}
			}
		});
	}
	// ==========================page=============================
	var relatedAlarmSelModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
		singleSelect : true,
		header : ""
	});
	relatedAlarmSelModel.sortLock();
	var relatedAlarmCM = new Ext.ux.grid.LockingColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true
		// columns are not sortable by default
		},
		columns : [ new Ext.grid.RowNumberer({
			width:26,
			locked : true
		}), relatedAlarmSelModel,{
			header : '相关告警ID',
			dataIndex : 'ALARM_ID',
			width : 100,
			hidden : true
		},{
			header : '主次告警',
			dataIndex : 'CONVERGE_FLAG',
			width : 100,
			renderer : convergeRenderer
		},{
			header : '告警名称',
			dataIndex : 'ALARM_NAME',
			width : 100
		}, {
			header : '告警等级',
			dataIndex : 'SEVERITY',
			width : 100,
			renderer : severityRenderer
		},{
			header : '网元名',
			dataIndex : 'NE_NAME',
			width : 100
		}, {
			header : '开始时间',
			dataIndex : 'START_TIME_STR',
			width : 100
		}, {
			header : '清除时间',
			dataIndex : 'CLEAN_TIME_STR',
			width : 100
		}]
	});
	
	var relatedAlarmPageTool = new Ext.PagingToolbar({
		id : 'relatedAlarmPageTool',
		pageSize : 200,// 每页显示的记录值
		store : relatedAlarmStore,
		displayInfo : true,
		displayMsg : '当前 {0} - {1} ，总数 {2}',
		emptyMsg : "没有记录"
	});
	
	var relatedAlarmGridPanel = new Ext.grid.GridPanel({
	//	region : "center",
		id : 'gridPanel_Alarm',
		flex : 1,
		height : 200,
		cm : relatedAlarmCM,
		store : relatedAlarmStore,
		stripeRows : true, // 交替行效果
		loadMask : {
			msg : '数据加载中...'
		},
		selModel : relatedAlarmSelModel, // 必须加不然不能选checkbox
		view : new Ext.ux.grid.LockingGridView(),
		forceFit : true,
		stateful:false,
		tbar : ['->', {
			xtype : 'button',
			text : '刷新',
			id : 'refreshAlarmBtn',
			handler : function(){
//				relatedAlarmStore.load();
				Ext.Ajax.request({
						url : 'fault-management!refreshFaultAlarm.action',
						method : 'POST',
						params : {
							"paramMap.faultId" : faultIdGlobal
						},
						success : function(response) {
							relatedAlarmStore.load({
								callback : function(r, o, s){
									if(!s){
										Ext.Msg.alert("提示：","查询故障相关告警失败！");
									}
								}
							});
							
							Ext.getCmp("startTimeFaultInfo").setValue("");
							Ext.getCmp("alarmEndFaultInfo").setValue("");
							
							var obj = Ext.decode(response.responseText);
							if(obj.time.START_TIME != null){
								var v = Ext.util.Format.dateRenderer('Y-m-d H:i:s')(new Date(
										obj.time.START_TIME.time));
								Ext.getCmp("startTimeFaultInfo").setValue(v);
							}
							if(obj.time.ALM_CLEAR_TIME != null){
								var v = Ext.util.Format.dateRenderer('Y-m-d H:i:s')(new Date(
										obj.time.ALM_CLEAR_TIME.time));
								Ext.getCmp("alarmEndFaultInfo").setValue(v);
							}
						},
						failue : function(response) {
							Ext.getBody().unmask();
							Ext.Msg.alert('错误', '访问服务器失败！');
						}
					}
				);
				
				enableSaveFaultInfoBtn();
			}
		}, {
			xtype : 'button',
			text : '增加',
			handler : addAlarm
		}, {
			xtype : 'button',
			text : '删除',
			handler : function(){
				var cell = Ext.getCmp('gridPanel_Alarm').getSelectionModel().getSelections();
				if (cell.length == 0) {
					Ext.Msg.alert("提示", "请选择需要删除的告警记录！");
				} else {
					relatedAlarmStore.remove(cell);
					relatedAlarmGridPanel.getView().refresh();
					
					setFaultTime();
					enableSaveFaultInfoBtn();
				}
			}
		}],
		bbar : relatedAlarmPageTool
	});
	
	var relatedAlarm = {
		layout : 'hbox',
		border : false,
		items : [relatedAlarmDis, relatedAlarmGridPanel]
	};
	
	var faultAnalyzeDis = {
		layout : 'form',
		width : 80,
		border : false,
		items : [{
			xtype : 'displayfield',
			fieldLabel : '故障分析'
		}]
	};
	
	var faultAnalyzePanel = new Ext.Panel({
		id : 'faultAnalyzePanel',
		flex : 1,
	//	height : 100,
//		width : '100%',
		collapsible : true,
//		forceFit : true,
		bodyStyle : 'padding:0px 5px 0px 0px',
		items : [{
			xtype : 'textarea',
			id : 'faultAnalyzeTextArea',
			readOnly : true,
			width : '100%'
		}],
		listeners : {
			'expand' : function(p){
				Ext.getCmp("faultAnalyzePanel").setHeight(180);
				Ext.getCmp("faultAnalyzeTextArea").setHeight(155);
				Ext.getCmp("centerPanel").doLayout();
			},
			'collapse' : function(p) {
				Ext.getCmp("centerPanel").doLayout();
				//第二次调用doLayout方法是为了消除页面出现的瑕疵
				Ext.getCmp("centerPanel").doLayout();
			}
		}
	});
	
	var faultAnalyze = {
		layout : 'hbox',
		border : false,
		bodyStyle : 'padding:10px 0px 0px 0px;',
		items : [faultAnalyzeDis, faultAnalyzePanel]
	};
	
	var analyzeAccuracyDis = {
		layout : 'form',
		width : 80,
		border : false,
		items : [{
			xtype : 'displayfield',
			id : 'analyzeAccuracyDis',
			fieldLabel : '分析准确性'
		}]
	};
	
	var accuracyStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'value'
		}, {
			name : 'displayName'
		} ]
	});
	accuracyStore.loadData(accuracyData);
	
	var analyzeAccuracy = {
		layout : 'hbox',
		border : false,
		height : 35,
		bodyStyle : 'padding:11px 0px 0px 0px',
		items : [analyzeAccuracyDis, {
			xtype : 'combo',
			id : 'analyzeAccuracyCombo',
			store : accuracyStore,
			displayField : 'displayName',
			valueField : 'value',
			triggerAction : 'all',
			mode : 'local',
			listeners : {
				'select' : enableSaveFaultInfoBtn
			}
		}]
	};
	
	var faultDescriptionDis = {
		layout : 'form',
		width : 80,
		border : false,
		items : [{
			xtype : 'displayfield',
			fieldLabel : '故障描述'
		}]
	};
	
	var faultDescriptionPanel = new Ext.Panel({
		id : 'faultDescriptionPanel',
		flex : 1,
		collapsible : true,
		bodyStyle : 'padding:0px 5px 0px 0px',
		forceFit : true,
		items : [{
			xtype : 'textarea',
			id : 'faultDescriptionTextArea',
			width : '100%',
			listeners : {
				'change' : enableSaveFaultInfoBtn
			}
		}],
		listeners : {
			'expand' : function(p){
				Ext.getCmp("faultDescriptionPanel").setHeight(180);
				Ext.getCmp("faultDescriptionTextArea").setHeight(155);
				Ext.getCmp("centerPanel").doLayout();
			},
			'collapse' : function(p){
				Ext.getCmp("centerPanel").doLayout();
				//第二次调用doLayout方法是为了消除页面出现的瑕疵
				Ext.getCmp("centerPanel").doLayout();
			}
		}
	});
	
	var faultDescription = {
		layout : 'hbox',
		id : 'faultDescription',
		bodyStyle : 'padding:10px 0px 20px 0px;',
		border : false,
	//	height : 200,
		items : [faultDescriptionDis, faultDescriptionPanel]
	};
	
	var centerPanel = new Ext.FormPanel({
		id : 'centerPanel',
		flex:1,
		bodyStyle : 'padding:10px 20px 0px 20px;',
		border : false,
		autoScroll : true,
		items : [faultReasonLine, faultLocation, faultTime, 
		         relatedAlarm, faultAnalyze, analyzeAccuracy, 
		         faultDescription]
	});
	
	var fault_window = new Ext.Window({
		id : 'faultWin',
		layout : 'vbox',
		layoutConfig : {
			pack : 'start',
			align : 'stretch'
		},
		modal : true,
		width : 0.63 * Ext.getCmp('win').getWidth(),
		height : 0.9 * Ext.getCmp('win').getHeight() + 10,
		minWidth : 0.5 * Ext.getCmp('win').getWidth(),
		autoScroll : true,
		items : [ northDis, centerPanel],
		buttonAlign: 'left',
		buttons : [{
			text : '保存',
			id : 'saveFaultInfoBtn',
			disabled : true,
			handler : saveFaultInfo
		},'->',{
			text : '故障确认',
			id : 'faultConfirmBtn',
			listeners : {
				'click' : faultConfirm
			}
//			handler : faultConfirm
		},{
			text : '故障恢复',
			id : 'faultRecovery',
			handler : faultRecovery
		},{
			text : '归档',
			id : 'faultArchive',
			handler : faultArchive
		},{
			text : '取消',
			handler : function(){
				fault_window.close();
			}
		}]
	});
	
	if(createFlag){
		fault_window.setTitle('故障信息');
		Ext.getCmp("saveFaultInfoBtn").disable();
		Ext.getCmp("faultRecovery").disable();
		Ext.getCmp("faultArchive").disable();
		Ext.getCmp("refreshAlarmBtn").disable();
	}else{
		fault_window.setTitle('故障信息' + '(' + faultNo + ')');
	}
	fault_window.show();
	
	collapsePanel();
}

//设置状态按钮
function setStatusBtn(status){
	
	Ext.getCmp("faultConfirmBtn").disable();
	Ext.getCmp("faultRecovery").disable();
	Ext.getCmp("faultArchive").disable();
	
	if(status == 1){//未确认
		Ext.getCmp("faultConfirmBtn").enable();
	}else if(status == 2){//已确认
		Ext.getCmp("faultRecovery").enable();
	}else if(status == 3){//故障恢复
		Ext.getCmp("faultArchive").enable();
	}else if(status == 4){//已归档
	}
}

//故障确认
function faultConfirm() {
	
	Ext.Ajax.request({
		url : 'fault-management!saveFaultInfo.action',
		method : 'POST',
		params : getParamForSave(),
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				
				Ext.Ajax.request({
					url : 'fault-management!faultConfirm.action',
					method : 'POST',
					params : {
						"paramMap.faultId" : faultIdGlobal
					},
					success : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
						if (obj.returnResult == 1) {
							
							Ext.Msg.alert("提示", "故障确认成功！", function(r) {
								
								Ext.getCmp("statusDis").setValue("已确认");
								var pageTool = Ext.getCmp('faultManagementPageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.getCmp("faultWin").close();
								updateFaultInfo_Main();
							});
						};
					},
					failue : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '访问服务器失败！');
					}
				});
			};
		},
		failue : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert('错误', '访问服务器失败！');
		}
	});
}

//故障恢复
function faultRecovery() {
	
	Ext.Ajax.request({
		url : 'fault-management!saveFaultInfo.action',
		method : 'POST',
		params : getParamForSave(),
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				
				Ext.Ajax.request({
					url : 'fault-management!faultRecovery.action',
					method : 'POST',
					params : {
						"paramMap.faultId" : faultIdGlobal
					},
					success : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
						if (obj.returnResult == 1) {
							
							Ext.Msg.alert("提示", "故障恢复成功！", function(r) {
								
								Ext.getCmp("statusDis").setValue("故障恢复");
								var pageTool = Ext.getCmp('faultManagementPageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.getCmp("faultWin").close();
							});
						};
					},
					failue : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '访问服务器失败！');
					}
				});
			};
		},
		failue : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert('错误', '访问服务器失败！');
		}
	});
}

//故障归档
function faultArchive() {
	
	Ext.Ajax.request({
		url : 'fault-management!saveFaultInfo.action',
		method : 'POST',
		params : getParamForSave(),
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				
				Ext.Ajax.request({
					url : 'fault-management!faultArchive.action',
					method : 'POST',
					params : {
						"paramMap.faultId" : faultIdGlobal
					},
					success : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
						if (obj.returnResult == 1) {
							
							Ext.Msg.alert("提示", "故障归档成功！", function(r) {
								
								Ext.getCmp("statusDis").setValue("已归档");
								var pageTool = Ext.getCmp('faultManagementPageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								Ext.getCmp("faultWin").close();
							});
						};
					},
					failue : function(response) {
						Ext.getBody().unmask();
						Ext.Msg.alert('错误', '访问服务器失败！');
					}
				});
			};
		},
		failue : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert('错误', '访问服务器失败！');
		}
	});
}

//保存按钮使能
function enableSaveFaultInfoBtn(){
	if(faultIdGlobal != 0){
		Ext.getCmp("saveFaultInfoBtn").enable();
	}
}

//故障生成
function faultGenerate() {
	saveFaultInfo();
//	Ext.getCmp("faultWin").close();
}

function getParamForSave(){
	
	//分析准确性
	var accuracy = Ext.getCmp("analyzeAccuracyCombo").getValue();
	
	//故障描述
	var description = Ext.getCmp("faultDescriptionTextArea").getValue();
	
	//开始时间
	var startTime = Ext.getCmp("startTimeFaultInfo").getValue();
	
	//结束时间
	var alarmClearTime = Ext.getCmp("alarmEndFaultInfo").getValue();
	
	var param = {
		"paramMap.accuracy" : accuracy,
		"paramMap.description" : description,
		"paramMap.startTime" : startTime,
		"paramMap.alarmClearTime" : alarmClearTime
	};
	
	//修改故障信息
	if(typeof faultIdGlobal == 'number' && faultIdGlobal > 0){
		param["paramMap.faultId"] = faultIdGlobal;
	}
	//新建故障信息
	else if(typeof faultIdGlobal == 'number' && faultIdGlobal == 0){
		param["paramMap.source"] = 2;
		param["paramMap.type"] = typeGlobal;
	}
	
	if(typeGlobal == 1){//设备
		//系统阻断
		var isBroken = Ext.getCmp("systemBrokenCheckboxEquip").getValue();
		//故障原因
		var faultReason = Ext.getCmp("faultReasonCombo").getValue();
		//传输系统
		var transSystem = Ext.getCmp("transSystemEqpt").getValue();
		//网管
		var ems = Ext.getCmp("emsEqpt").getValue();
		//局站
		var station = Ext.getCmp("stationEqpt").getValue();
		//网元
		var ne = Ext.getCmp("neEqpt").getValue();
		//板卡
		var unit = Ext.getCmp("unitEqpt").getValue();
		//设备厂家
		var factory = Ext.getCmp("factoryEqpt").getValue();
		
		param["paramMap.isBroken"] = isBroken;
		param["paramMap.faultReason1"] = faultReason;
		param["paramMap.transSystem"] = transSystem;
		param["paramMap.ems"] = ems;
		param["paramMap.station"] = station;
		param["paramMap.ne"] = ne;
		param["paramMap.unit"] = unit;
		param["paramMap.factory"] = factory;
	}else if(typeGlobal == 2){//线路
		//系统阻断
		var isBroken = Ext.getCmp("systemBrokenCheckboxLine").getValue();
		//故障原因
		var faultReason1 = Ext.getCmp("faultReasonCombo1").getValue();
		var faultReason2 = Ext.getCmp("faultReasonCombo2").getValue();
		//传输系统
		var transSystem = Ext.getCmp("transSystemLine").getRawValue();
		//光缆
		var cable = Ext.getCmp("cableLine").getRawValue();
		//光缆段
		var cableSection = Ext.getCmp("cableSectionLine").getRawValue();
		//维护单位
		var maintenancer = Ext.getCmp("maintenancerLine").getValue();
		//起始局站
		var aStation = Ext.getCmp("startStationLine").getValue();
		//终点局站
		var zStation = Ext.getCmp("endStationLine").getValue();
		//距离
		var nearStation = Ext.getCmp("nearStationLine").getValue();
		//公里
		var distance = Ext.getCmp("distanceLine").getValue();
		//经度
		var longitude = Ext.getCmp("longitudeLine").getValue();
		//纬度
		var latitude = Ext.getCmp("latitudeLine").getValue();
		
		param["paramMap.isBroken"] = isBroken;
		param["paramMap.faultReason1"] = faultReason1;
		param["paramMap.faultReason2"] = faultReason2;
		param["paramMap.transSystem"] = transSystem;
		param["paramMap.cable"] = cable;
		param["paramMap.cableSection"] = cableSection;
		param["paramMap.maintenancer"] = maintenancer;
		param["paramMap.aStation"] = aStation;
		param["paramMap.zStation"] = zStation;
		param["paramMap.nearStation"] = nearStation;
		param["paramMap.distance"] = distance;
		param["paramMap.longitude"] = longitude;
		param["paramMap.latitude"] = latitude;
	}
	
	var array = new Array();
	var records = Ext.getCmp("gridPanel_Alarm").getStore().getRange();
	
	for(var i=0; i<records.length; i++){
		var map = {
			"alarmId" : records[i].get("ALARM_ID"),
			"convergeFlag" : records[i].get("CONVERGE_FLAG"),
			"alarmName" : records[i].get("ALARM_NAME"),
			"severity" : records[i].get("SEVERITY"),
			"neName" : records[i].get("NE_NAME"),
			"startTime" : records[i].get("START_TIME_STR"),
			"cleanTime" : records[i].get("CLEAN_TIME_STR")
		};
		
		array.push(Ext.encode(map));
	}
	
	if(array.length > 0){
		param["paramMapList"] = array;
	}
	
	return param;
}

//保存故障信息
function saveFaultInfo() {
	
	Ext.Ajax.request({
		url : 'fault-management!saveFaultInfo.action',
		method : 'POST',
		params : getParamForSave(),
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				
				Ext.Msg.alert("提示", obj.returnMessage, function(r) {
					var pageTool = Ext.getCmp('faultManagementPageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
					Ext.getCmp("faultWin").close();
					
					updateFaultInfo_Main();
				});
			};
		},
		failue : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert('错误', '访问服务器失败！');
		}
	});
}

//更新首页故障信息
function updateFaultInfo_Main() {
	
	Ext.Ajax.request({
		url : 'fault-management!updateFaultInfo_Main.action',
		method : 'POST',
		success : function(response) {
			
		},
		failue : function(response) {
			Ext.Msg.alert('错误', '访问服务器失败！');
		}
	});
}

//折叠故障分析、故障描述panel
function collapsePanel(){
	
	Ext.getCmp("faultAnalyzePanel").setHeight(0);
	Ext.getCmp("faultAnalyzeTextArea").setHeight(0);
	Ext.getCmp("faultAnalyzePanel").collapse(true);
	Ext.getCmp("centerPanel").doLayout();
	
	Ext.getCmp("faultDescriptionPanel").setHeight(0);
	Ext.getCmp("faultDescriptionTextArea").setHeight(0);
	Ext.getCmp("faultDescriptionPanel").collapse(true);
	Ext.getCmp("centerPanel").doLayout();
}

//function afterCollapse() {
//	
//	
//	
//
//	
//	
//	Ext.getCmp("centerPanel").doLayout();
//	//第二次调用doLayout方法是为了消除页面出现的瑕疵
//	Ext.getCmp("centerPanel").doLayout();
//	Ext.getCmp("faultAnalyzePanel").setWidth(
//			Ext.getCmp("gridPanel_Alarm").getWidth());
//	Ext.getCmp("faultDescriptionPanel").setWidth(
//			Ext.getCmp("gridPanel_Alarm").getWidth());
////	alert(Ext.getCmp("gridPanel_Alarm").getWidth());
//	
//	
//}





