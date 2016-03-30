//var parentNodeType  ;
//var parentNodeId  ; 
//var nodeType;
//var nodeId ;
//var nodePath ;

//var parentSubnetId;
//var saveType;
var subnetId;
var saveValue;
/**
 * 子网下拉框所用Store
 */
var subnetStore = new Ext.data.Store({
	pruneModifiedRecords : true,
	// load using HTTP
	url : 'getSubnetList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "subnetId","subnetName" ])
});
	

var subnetName = new Ext.form.TextField({
	id : 'subnetName',
	name : 'subnetName',
	fieldLabel : '子网名称',
	sideText : '<font color=red>*</font>',
	emptyText : '请输入子网名称........',
	minLength : 1,
	maxLength : 40,
	allowBlank : false,
	anchor : '95%'
//	,listeners : {
//		blur : function(t) {
//
//			var param = {
//				'subnetModel.subnetName' : t.getValue(),
//				'subnetModel.emsConnectionId' : emsId
//			};
//			Ext.Ajax.request({
//				url : 'connection!checkSubnetNameExist.action',
//				method : 'POST',
//				params : param,
//				success : function(response) {
//					var result = Ext.util.JSON.decode(response.responseText);
//					if (result) {
//						if (0 == result.returnResult) {
//							Ext.MessageBox.show({
//								title : '信息',
//								width : 350,
//								height : 45,
//								msg : '同一网管下有相同的子网名称。名称不可重复。请修改！',
//								buttons : Ext.MessageBox.OK,
//								icon : Ext.MessageBox.alert
//							});
//						}
//					}
//				}
//			});
//		}
//	}
});

var subnetNote = new Ext.form.TextField({
	id : 'subnetNote',
	name : 'subnetNote',
	fieldLabel : '子网备注',
	emptyText : '请输入子网备注........',
	minLength : 0,
	maxLength : 128,
	allowBlank : true,
	anchor : '95%'
});

var subnetPath = new Ext.form.TextField({
	id : 'subnetPath',
	name : 'subnetPath',
	fieldLabel : '子网路径',
	//	emptyText : '请输入子网备注........',
	minLength : 0,
	maxLength : 128,
	allowBlank : true,
	anchor : '95%'
});

/**
 * 检查子网名称是否重复
 * 
 * @param {}
 *            txt 要检验是否重复的名称
 */
function checkDuplicate(txt) {
	return subnetStore.findExact(STR_VALUE, txt) >= 0 ? true : false;
}

var formPanel = new Ext.FormPanel({
	id : 'formPanel',
	name : 'formPanel',
	border : true,
	//	title : "新增网管分组",
	region : 'center',
	labelWidth : 70,
	//autoScroll : true,
	bodyStyle : 'padding:10px 12px 0;',
	items : [ subnetName, subnetNote, subnetPath ],
	buttons : [ {
		text : '确定',
		handler : function() {
			saveConfig();
		}
	}, {
		text : '取消',
		handler : function() {
			//关闭修改任务信息窗口
			var win = parent.Ext.getCmp('addSubnetWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});

function saveConfig() {
	if (formPanel.getForm().isValid()) {
	var subnetName = Ext.getCmp("subnetName").getValue();
	var subnetNote = Ext.getCmp("subnetNote").getValue();
	var subnetPath = Ext.getCmp("subnetPath").getValue();
	var emsConnectionId;

	
	Ext.getBody().mask('正在执行，请稍候...');

	//新增
	if (saveType == 1) {
		if( parentNodeType == 0) {
			parentSubnetId =null;
			var jsonAddData = {
					"subnetModel.emsConnectionId" : nodeId,
					"subnetModel.parentSubnetId" : parentSubnetId,
					"subnetModel.subnetName" : subnetName,
					"subnetModel.subnetNote" : subnetNote
				};
				Ext.Ajax.request({
					url : 'connection!addSubnet.action',
					method : 'POST',
					params : jsonAddData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.getBody().unmask();
						
						if (obj.returnResult == 1) {
							Ext.Msg.alert("信息", obj.returnMessage, function(r) {
								//刷新列表
								var westPanel = parent.Ext.getCmp('westPanel');
								if (westPanel) {
									westPanel.update(westPanel.initialConfig.html,true);
								}
								Ext.Msg.confirm('信息', '继续添加？', function(btn) {
									if (btn == 'yes') {
										Ext.getCmp('subnetName').reset();
										Ext.getCmp('subnetNote').reset();
									} else {
										//关闭修改任务信息窗口
										var win = parent.Ext.getCmp('addSubnetWindow');
										if (win) {
											win.close();
										}
									}
								});
							});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("信息", obj.returnMessage);
						}
					},
					error : function(response) {
					
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
				
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});
		}
		if( parentNodeType == 1) {
			parentSubnetId =null;
			var jsonAddData = {
					"subnetModel.emsConnectionId" : emsId,
					"subnetModel.parentSubnetId" : parentSubnetId,
					"subnetModel.subnetName" : subnetName,
					"subnetModel.subnetNote" : subnetNote
				};
				Ext.Ajax.request({
					url : 'connection!addSubnet.action',
					method : 'POST',
					params : jsonAddData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.getBody().unmask();
						
						if (obj.returnResult == 1) {
							Ext.Msg.alert("信息", obj.returnMessage, function(r) {
								//刷新列表
								var westPanel = parent.Ext.getCmp('westPanel');
								if (westPanel) {
									westPanel.update(westPanel.initialConfig.html,true);
								}
								Ext.Msg.confirm('信息', '继续添加？', function(btn) {
									if (btn == 'yes') {
										Ext.getCmp('subnetName').reset();
										Ext.getCmp('subnetNote').reset();
									} else {
										//关闭修改任务信息窗口
										var win = parent.Ext.getCmp('addSubnetWindow');
										if (win) {
											win.close();
										}
									}
								});
							});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("信息", obj.returnMessage);
						}
						

					},
					error : function(response) {
					
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
				
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});
		}
		// 父节点是网管的情况 parentNodeType 值为2
		if( parentNodeType == 2) {
			emsConnectionId = parentNodeId ;
			parentSubnetId = nodeId;
			var jsonAddData = {
					"subnetModel.emsConnectionId" : emsConnectionId,
					"subnetModel.parentSubnetId" : parentSubnetId,
					"subnetModel.subnetName" : subnetName,
					"subnetModel.subnetNote" : subnetNote
				};
				Ext.Ajax.request({
					url : 'connection!addSubnet.action',
					method : 'POST',
					params : jsonAddData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.getBody().unmask();
						if (obj.returnResult == 1) {
							Ext.Msg.alert("信息", obj.returnMessage, function(r) {
								//刷新列表
								var westPanel = parent.Ext.getCmp('westPanel');
								if (westPanel) {
									westPanel.update(westPanel.initialConfig.html,true);
								}
								Ext.Msg.confirm('信息', '继续添加？', function(btn) {
									if (btn == 'yes') {
										Ext.getCmp('subnetName').reset();
										Ext.getCmp('subnetNote').reset();
									} else {
										//关闭修改任务信息窗口
										var win = parent.Ext.getCmp('addSubnetWindow');
										if (win) {
											win.close();
										}
									}
								});
							});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("信息", obj.returnMessage);
						}
					},
					error : function(response) {
						
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						
						Ext.getBody().unmask();
						Ext.Msg.alert("错误", response.responseText);
					}
				});
		} else if (parentNodeType == 3) {
			parentSubnetId = nodeId ;
	
			var jsonAddData = {
							"subnetModel.emsConnectionId" : emsId,
							"subnetModel.parentSubnetId" : parentSubnetId,
							"subnetModel.subnetName" : subnetName,
							"subnetModel.subnetNote" : subnetNote
						};
						Ext.Ajax.request({
							url : 'connection!addSubnet.action',
							method : 'POST',
							params : jsonAddData,
							success : function(response) {
								var obj = Ext.decode(response.responseText);
								Ext.getBody().unmask();
								if (obj.returnResult == 1) {
									Ext.Msg.alert("信息", obj.returnMessage, function(r) {
										//刷新列表
										var westPanel = parent.Ext.getCmp('westPanel');
										if (westPanel) {
											westPanel.update(westPanel.initialConfig.html,true);
										}
										Ext.Msg.confirm('信息', '继续添加？', function(btn) {
											if (btn == 'yes') {
												Ext.getCmp('subnetName').reset();
												Ext.getCmp('subnetNote').reset();
											} else {
												//关闭修改任务信息窗口
												var win = parent.Ext.getCmp('addSubnetWindow');
												if (win) {
													win.close();
												}
											}
										});
									});
								}
								if (obj.returnResult == 0) {
									Ext.Msg.alert("信息", obj.returnMessage);
								}
							},
							error : function(response) {
							
								Ext.getBody().unmask();
								Ext.Msg.alert("错误", response.responseText);
							},
							failure : function(response) {
							
								Ext.getBody().unmask();
								Ext.Msg.alert("错误", response.responseText);
							}
						});
		}

		// 修改子网
	} else if (saveType  == 3) {
		var jsonData = {
			"subnetModel.emsConnectionId" : emsId,
			"subnetModel.subnetId" : subnetId,
			"subnetModel.subnetName" : subnetName,
			"subnetModel.subnetNote" : subnetNote
		};
		Ext.Ajax.request({
			url : 'connection!modifySubnet.action',
			method : 'POST',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.getBody().unmask();
				if (obj.returnResult == 1) {
					Ext.Msg.alert("信息", obj.returnMessage, function(r) {
						//刷新列表
						var westPanel = parent.Ext.getCmp('westPanel');
						if (westPanel) {
							westPanel.update(westPanel.initialConfig.html,true);
						}
						//关闭修改任务信息窗口
						var win = parent.Ext.getCmp('addSubnetWindow');
						if (win) {
							win.close();
						}
					});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", obj.returnMessage);
				}
			},
			error : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		});
	}
	}
}

function initData(nodeId,nodePath) {
	saveValue = saveType %2;
	var jsonData = {
			"subnetModel.subnetId":nodeId
		};
	Ext.Ajax.request({
	    url: 'connection!getSubnetBySubnetId.action',
	    method : 'POST',
	    params: jsonData,
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	subnetId = obj.BASE_SUBNET_ID;
	    	emsConnectionId = obj.BASE_EMS_CONNECTION_ID;
	    	
	    	Ext.getCmp("subnetName").setValue(obj.DISPLAY_NAME);
	    	Ext.getCmp("subnetNote").setValue(obj.NOTE);
	    	Ext.getCmp("subnetPath").setValue(nodePath);
	    	Ext.getCmp("subnetPath").setDisabled(true);
	    },
	    error:function(response) {
	    	Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 
}


Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	
	if (saveType == 1) {
		var str =nodePath;
        var tt = str.split(':');
        var path="";

    	for(var i = 0; i <tt.length; i++){
    		if(i == tt.length-1){
    			path = path + tt[i] ;
    		} else {
    			path = path + tt[i] +"\\";
    		}
    	}
		
		Ext.getCmp("subnetPath").setValue(nodePath);
		Ext.getCmp("subnetPath").setDisabled(true);
		saveValue = saveType %2;
	}

	if (saveType == 3) {

		var str =nodePath;
        var tt = str.split(':');
        var path="";

    	for(var i = 0; i <tt.length; i++){
    		if(i == tt.length-1){
    			path = path + tt[i] ;
    		} else {
    			path = path + tt[i] +"\\";
    		}
    	}
		initData(nodeId,path);
		
	}
	
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ formPanel ],
		renderTo : Ext.getBody()
	});
});