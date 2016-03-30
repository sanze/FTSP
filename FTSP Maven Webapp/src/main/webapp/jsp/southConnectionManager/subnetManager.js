Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
var Tree = Ext.tree;
// ================全局变量===================
var subnetId;

var emsConnectionId = -1;

// ==========================================

var treeParams={
		leafType:leafType,
	    checkModel:"single",
	    leafType:3
	};
var treeurl="../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
checkedSubnetInfo = {
	checkedSubnetId : null,
	neIds : null,
	emsId : null,
	moveNe : {},
	clear : function(){
		this.checkedSubnetId = null;
		this.neIds = null;
		this.emsId = null;
		this.moveNe = {};
	}
};
/**
 * 左侧的树
 */
var westPanel = new Ext.Panel(
		{
			id : "westPanel",
			region : "west",
			title : '子网结构预览',
			width : 280,
			height : 800,
			minSize : 230,
//			maxSize : 320,
			autoScroll : true,
			forceFit : true,
			collapsed : false, // initially collapse the group
			collapsible : false,
			collapseMode : 'mini',
			split : true,
			html : '<iframe id="tree_panel" name ="tree_panel" src ="'+treeurl+'"  height="100%" width="100%" frameBorder=0 border=0/>'
		});

// =============================网元归属选择============================

var unclassifiedNe = new Ext.data.Store({
	url : 'connection!getNeListByEmsConnnectionId.action',
    reader: new Ext.data.JsonReader({
        totalProperty: 'total',
				root : "rows"
		},[
			"neId","neName"
	])
});

var classifiedNe = new Ext.data.Store({
	url : 'connection!getNeListByEmsConnnectionIdAndSubnetId.action',
    reader: new Ext.data.JsonReader({
        totalProperty: 'total',
				root : "rows"
		},[
			"neId","neName","PRODUCT_NAME","STATION_NAME","AREA_NAME"
	])
});

var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
// 添加一个listener用来监听勾选状态变化
checkboxSelectionModel.addListener("selectionchange",function(model){
	// 控制"移动到"按钮
	// 注意直接通过gridPanel.getCheckBoxSelectionModel()方法获取的model和该model有所不同，
	//该model点击全选框时不显示过滤掉的数据！
	if(model.getCount() > 0){
		var jString = new Array();
		var neInfo = model.getSelections();
		for (var i=0 ;i<neInfo.length;i++) {
			jString.push(neInfo[i].get('neId'));
		}
		checkedSubnetInfo.moveNe = jString;
		Ext.getCmp('moveToBtn').enable();
	}else{
		Ext.getCmp('moveToBtn').disable();
	}
});
checkboxSelectionModel.addListener("rowdeselect",function(model ,rowIndex ,record){
	//加快非过滤时前台相应速度
	if(Ext.getCmp('neQueryCon').getValue()){
		// 控制store过滤
		filterFun();
	}
});
//{解决checkbox列无法锁定问题
checkboxSelectionModel.sortLock();
//}解决checkbox列无法锁定问题

var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	})
	// 如果需要添加选择框则取消注释，并将gridPanel中selModel注释取消
	, checkboxSelectionModel, {
		id : 'neId',
		header : '网元Id',
		dataIndex : 'neId',
		hidden : true,
		width : 200
	}, {
		id : 'neName',
		header : '网元名称',
		dataIndex : 'neName',
		width : 200
	},{
		id : 'PRODUCT_NAME',
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 200
	},{
		id : 'AREA_NAME',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'AREA_NAME',
		width : 200
	},{
		id : 'STATION_NAME',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'STATION_NAME',
		width : 200
	}]
});
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	cm : cm,
	store:classifiedNe,
	height : 400,
	stripeRows : true, // 交替行效果
	loadMask : {
		msg : '正在执行，请稍后...'
	},
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ["-",{enableKeyEvents:true,id : 'neQueryCon',xtype : 'textfield',width : 200,emptyText: '输入搜索网元名称',},
	        "-",{text:'添加网元',handler:addNe},"-",
	        {text : '移动到',id : 'moveToBtn',disabled:true,handler:moveTo}]

});
function filterFun(){
	classifiedNe.filter([{fn:function(record){
		var name = record.get('neName');
		// 如果存在相同选项
		if(name.indexOf(Ext.getCmp('neQueryCon').getValue())>-1){
			return true;
		}
	    var tt = checkboxSelectionModel.getSelections();
	    // 如果已选中的话也显示
	    for(var i =0 ;i<tt.length;i++){
	    if(record.get('neId') == tt[i].get('neId')){
	         return true;
	      }
	    }
	    return false;
	 }}]);
}
// 注册两个监听器用来过滤Store:classifiedNe
Ext.getCmp('neQueryCon').addListener('keyup',filterFun);
classifiedNe.addListener('load',filterFun);
var subnetInfoPanel = new Ext.FormPanel({
	id : 'subnetInfoPanel',
	name : 'subnetInfoPanel',
	autoScroll : false,
	fieldLabel : '',
	border : false,
	items: [{
		layout: 'column',
		border : false,
		items :[{bodyStyle: 'padding: 10px;',border:false,layout:'form',items:[{
	        xtype: 'textfield',
	        id :'subnetName',
	        fieldLabel: '子网名称',
	        width:250,
	        readOnly : true
	    }]},{bodyStyle: 'padding: 10px;',border:false,layout:'form',items:[{
	        xtype: 'textfield',
	        id : 'subnetPath',
	        fieldLabel: '子网路径',
	        width : 250,
	        readOnly : true
	    }]}]
	},{
		bodyStyle: 'padding: 10px;',border:false,layout:'form',items:[{
			xtype: 'textfield',
	        id :'subnetNote',
	        width :700,
	        fieldLabel: '备注',
	        readOnly : true
		}]
	    },gridPanel]
});
function infoReset(){
	Ext.getCmp('subnetInfoPanel').getForm().reset();
	classifiedNe.removeAll();
	checkedSubnetInfo.clear();
	subnetInfoPanel.getEl().mask();
}

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	border : true,
	layout:'form',
	columnWidth :.9,
	region : 'center',
	layout : 'fit',
	autoScroll : true,
	tbar : [ '-', {
		text : '新建子网',
		privilege:addAuth,
		id : 'btnNewSubnet',
		icon : '../../resource/images/btnImages/add.png',
		handler : addSubnet
	}, {
		text : '修改子网',
		id : 'btnModSubnet',
		privilege:modAuth,
		icon : '../../resource/images/btnImages/modify.png',
		handler : modifySubnet
	}, {
		text : '删除子网',
		id : 'btnDelSubnet',
		privilege:delAuth,
		icon : '../../resource/images/btnImages/delete.png',
		handler : deleteSubnet
	}, '-', {
		text : '划分子网',
		privilege:actionAuth,
		id : 'save',
		icon : '../../resource/images/btnImages/disk.png',
		handler : managerSubnet
	} ],
	items : [subnetInfoPanel ]

});
function managerSubnet(){
	subnetInfoPanel.getEl().unmask();
	var iframe = window.frames["tree_panel"];
	var result = iframe.getCheckedNodes(["nodeId", "nodeLevel","emsId",
			"text", "path:text" ]);
	if(result==null||result.length==0){
		Ext.Msg.alert("信息", "请在左侧节点树上选择子网!");
		return;
	}
	var nodeName = result[0]["text"];
	var nodeType = result[0]["nodeLevel"];
	var nodeId = result[0]["nodeId"];
	var nodePath = result[0]["path:text"];
	if(nodeType !=3){
		Ext.Msg.alert("信息", "请在左侧节点树上选择子网!");
		return;
	}
	// 为全局变量赋值
	checkedSubnetInfo.checkedSubnetId = nodeId;
	checkedSubnetInfo.emsId = result[0]["emsId"];
	var jsonData = {
			"subnetModel.subnetId":nodeId
		};
	Ext.Ajax.request({
	    url: 'connection!getSubnetBySubnetId.action',
	    method : 'POST',
	    params: jsonData,
	    success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	Ext.getCmp("subnetName").setValue(obj.DISPLAY_NAME);
	    	Ext.getCmp("subnetNote").setValue(obj.NOTE);
	    	Ext.getCmp("subnetPath").setValue(nodePath);
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
	saveClassifiedNe();
	subnetInfoPanel.getEl().unmask();
}
function saveClassifiedNe() {
	var iframe = window.frames["tree_panel"];
	var result = iframe.getCheckedNodes([ "parentId", "parentLevel", "nodeId", "nodeLevel",
			"text", "path:text" ]);
	if(result==null||result.length==0){
		Ext.Msg.alert("信息", "请在左侧节点树上选择子网!");
		return;
	}
	var parentNodeType = result[0]["parentLevel"];
	var parentNodeId = result[0]["parentId"];
	var nodeType = result[0]["nodeLevel"];
	var nodeId = result[0]["nodeId"];
	var nodePath = result[0]["path:text"];

	// 查询该网管下的未分组的网元
	if (nodeType == 2) {		
		emsConnectionId = nodeId;	
		unclassifiedNe.baseParams = {
				"neModel.emsConnectionId" : emsConnectionId
			};
		unclassifiedNe.load();
		
		classifiedNe.baseParams = {
				"neModel.emsConnectionId" : emsConnectionId,
				"neModel.subnetId" : 0
			};
		classifiedNe.load();
	}
	// 查询该网管下的未分组的网元 以及 子网下已分组的网元
	else if (nodeType == 3 && parentNodeType == 2) {
		emsConnectionId = parentNodeId;
		subnetId = nodeId;

		unclassifiedNe.baseParams = {
				"neModel.emsConnectionId" : emsConnectionId
			};
		unclassifiedNe.load();
		
		classifiedNe.baseParams = {
				"neModel.emsConnectionId" : emsConnectionId,
				"neModel.subnetId" : subnetId
			};
		classifiedNe.load();
		
		
	}
	// 先查询子网所在的网管，再查询网管下的未分组的网元 以及子网下已分组的网元
	else if (nodeType == 3 && parentNodeType == 3) {
		subnetId = nodeId;
		parentSubnetId = parentNodeId;
		
		var jsonAddData = {
				"subnetModel.subnetId" : subnetId
			};
			Ext.Ajax.request({
				url : 'connection!getSubnetBySubnetId.action',
				method : 'POST',
				params : jsonAddData,
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					emsConnectionId = obj.BASE_EMS_CONNECTION_ID;
					
					unclassifiedNe.baseParams = {
							"neModel.emsConnectionId" : emsConnectionId
						};
					unclassifiedNe.load();
					
					classifiedNe.baseParams = {
							"neModel.emsConnectionId" : emsConnectionId,
							"neModel.subnetId" : subnetId
						};
					classifiedNe.load();
				},
				error : function(response) {
				
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", obj.returnMessage);
				},
				failure : function(response) {
			
					Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
					Ext.Msg.alert("错误", obj.returnMessage);
				}
			});
			

			
	} else  {
		Ext.Msg.alert("信息", "请在左侧节点树上选择网管或者子网!");
	} 
}

/**
 * 检查子网名称是否重复
 * 
 * @param {}
 *            txt 要检验是否重复的名称
 */
function checkDuplicate(txt) {
	return subnetStore.findExact(STR_VALUE, txt) >= 0 ? true : false;
}

/**
 * 新增子网
 */
function addSubnet() {
	infoReset();
	var iframe = window.frames["tree_panel"];
	//修改返回参数
	var checkedNodeIds = iframe.getCheckedNodes([ "parentId", "parentLevel","nodeId", "nodeLevel","emsId","path\\text","path\\nodeLevel" ],"top");
	if (checkedNodeIds.length == 1) {
		var parentNodeType = checkedNodeIds[0]["parentLevel"];
		var parentNodeId = checkedNodeIds[0]["parentId"];
		var nodeType = checkedNodeIds[0]["nodeLevel"];
		var nodeId = checkedNodeIds[0]["nodeId"];
		var nodePath = checkedNodeIds[0]["path\\text"];
		//判断是否是3级子网，是则提示不能新建直接返回
		var nodeLevel = checkedNodeIds[0]["path\\nodeLevel"];
		var levels = nodeLevel.split("\\");
		var i = 0;
		for(a in levels){
			if(levels[a] == 3){
				i++;
			}
		}
		if(i == 3){
			Ext.Msg.alert("信息", "最大支持3级子网，请合理分配子网。");
			return;
		}
		var emsId = checkedNodeIds[0]["emsId"];
		
		var saveType = 1;
		
		if (nodeType == 2 || nodeType == 3 ) {

			var urlParams={
					parentNodeType:parentNodeType,
					parentNodeId:parentNodeId,
					nodeId:nodeId,
					nodePath:encodeURI(nodePath),
					emsId:emsId,
					nodeType:nodeType,
					saveType:1
				};
			var url ="addSubnet.jsp?"+Ext.urlEncode(urlParams);
			
			var addSubnetWindow = new Ext.Window(
					{
						id : 'addSubnetWindow',
						title : '新增子网',
						width : 400,
						height : 170,
						isTopContainer : true,
						modal : true,
						autoScroll : true,
						html : '<iframe id="addSubnet_panel" name = "addSubnet_panel"  src = ' + url
						+ '  height="100%" width="100%" frameBorder=0 border=0/>'
					});
			addSubnetWindow.show();
		} else {
			Ext.Msg.alert("信息", "请在左侧节点树上选择网管或者子网。");
			return; //没选网管则直接退出
		}
	} else if (checkedNodeIds.length < 1) {
		Ext.Msg.alert("重新选择", "请在左侧节点树上选择网管或者子网。");
	} else {
		Ext.Msg.alert("重新选择", "同一级别上，请不要多选！");
	}

}

/**
 * 修改子网
 */
function modifySubnet() {
	infoReset();
	var iframe = window.frames["tree_panel"];
	var checkedNodeIds = iframe.getCheckedNodes([ "parentId", "parentLevel","nodeId", "nodeLevel","emsId","path:text" ],"top");
	if (checkedNodeIds.length == 1) {
		var parentNodeType = checkedNodeIds[0]["parentLevel"];
		var parentNodeId = checkedNodeIds[0]["parentId"];
		var nodeType = checkedNodeIds[0]["nodeLevel"];
		var nodeId = checkedNodeIds[0]["nodeId"];
		var emsId = checkedNodeIds[0]["emsId"];
		var nodePath = checkedNodeIds[0]["path:text"];
		var saveType = 3;

		if (nodeType == 3 ) {
			var url = 'addSubnet.jsp?parentNodeType=' + parentNodeType + '&parentNodeId='+parentNodeId + '&nodeId=' +nodeId+'&nodePath='+nodePath
			+ '&nodeType=' +nodeType + '&emsId=' +emsId +'&saveType=' + saveType;
			url=encodeURI(encodeURI(url)); 
			var addSubnetWindow = new Ext.Window(
					{
						id : 'addSubnetWindow',
						title : '修改子网',
						width : 400,
						height : 170,
						isTopContainer : true,
						modal : true,
						autoScroll : true,
						html : '<iframe id="modifySubnet_panel" name = "modifySubnet_panel"  src = ' + url
						+ '  height="100%" width="100%" frameBorder=0 border=0/>'
					});
			addSubnetWindow.show();
		} else {
			Ext.Msg.alert("信息", "请在左侧节点树上选择子网。");
			return; //没选网管则直接退出
		}
	} else if (checkedNodeIds.length < 1) {
		Ext.Msg.alert("重新选择", "请在左侧节点树上选择子网。");
	} else {
		Ext.Msg.alert("重新选择", "同一级别上，请不要多选！");
	}
}

/**
 * 删除子网
 */
function deleteSubnet() {
	infoReset();
	var iframe = window.frames["tree_panel"];
	var checkedNodeIds = iframe.getCheckedNodes([ "parentId", "parentLevel","nodeId", "nodeLevel","path:text" ],"top");
	if (checkedNodeIds.length == 1) {
		var parentNodeType = checkedNodeIds[0]["parentLevel"];
		var parentNodeId = checkedNodeIds[0]["parentId"];
		var nodeType = checkedNodeIds[0]["nodeLevel"];
		var nodeId = checkedNodeIds[0]["nodeId"];
		var nodePath = checkedNodeIds[0]["path:text"];
		
		if (nodeType == 3 ) {
			if(parentNodeType == 2){
				emsConnectionId = parentNodeId;
				//去检索所选节点的子节点类型
				var jsonData = {
						"emsConnectionId":emsConnectionId,
						"subnetModel.subnetId" : nodeId
					};
				Ext.Msg.confirm('提示', '删除子网，不会删除网元数据。该子网下所有网元将划分到上一层，确认删除吗？', function(btn) {
					if (btn == 'yes') {
						
						Ext.getBody().mask('正在执行，请稍候...');
				Ext.Ajax.request({
					url : 'connection!deleteEmsSubnet.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.getBody().unmask();
						Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							//刷新列表
							var westPanel = Ext.getCmp('westPanel');
							if (westPanel) {
								westPanel.update(westPanel.initialConfig.html,true);
							}
						});
					},
					error : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("错误", obj.returnMessage);
					},
					failure : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("错误", obj.returnMessage);
					}
				});
					}
				});
			} if (parentNodeType == 3) {
				//在网管上新增子网
				var jsonData = {
					"subnetModel.parentSubnetId" : parentNodeId,
					"subnetModel.subnetId" : nodeId
				};
				Ext.Msg.confirm('提示', '删除子网，不会删除网元数据。该子网下所有网元将划分到上一层，确认删除吗？？', function(btn) {
					if (btn == 'yes') {
				Ext.Ajax.request({
					url : 'connection!deleteSubnetSubnet.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						Ext.getBody().unmask();
						Ext.Msg.alert("信息", obj.returnMessage, function(r) {
							//刷新列表
							var westPanel = Ext.getCmp('westPanel');
							if (westPanel) {
								westPanel.update(westPanel.initialConfig.html,true);
							}
						});
					},
					error : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("错误", obj.returnMessage);
					},
					failure : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("错误", obj.returnMessage);
					}
				});
					}
				});
			}
		} else {
			Ext.Msg.alert("信息", "请在左侧节点树上选择需要删除的子网。");
			return; //没选网管则直接退出
		}
	} else if (checkedNodeIds.length < 1) {
		Ext.Msg.alert("重新选择", "请在左侧节点树上选择需要删除的子网。");
	} else {
		Ext.Msg.alert("重新选择", "同一级别上，请不要多选！");
	}

}
// 向子网中添加网元
function addNe(){
	// 检查checkedSubnetId
		if(checkedSubnetInfo.checkedSubnetId == null){
			Ext.Msg.alert("提示","请选择子网");
			return ;
		}
	var treeParams={
			leafType:leafType,
		    checkModel:"multiple",
		    leafType:4,
		    rootVisible : true,
		    rootType : 2,
		    rootId : checkedSubnetInfo.emsId
		};
	var treeurl="../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
	var panel = new Ext.Panel({
		id : 'addNePanel',
		name : 'addNePanel',
		border : true,
		labelWidth : 70,
		html : '<iframe id="tree_panel_addNe" name ="tree_panel_addNe" src ="'+treeurl+'"  height="100%" width="100%" frameBorder=0 border=0/>',
		buttons : [ {
			text : '确定',
			handler : function() {
				var iframe = window.frames["tree_panel_addNe"];
				var tt = iframe.getCheckedNodes([ "parentId", "parentLevel","nodeId", "nodeLevel","path:text" ],"top");
				if (tt.length > 0) {
					var jString = new Array();
					var id = checkedSubnetInfo.checkedSubnetId;
					for (var i=0 ;i<tt.length;i++) {
						var neModel = {
							"neId" : tt[i]['nodeId'],
							// 如果是网管的话直接=0
							"subnetId" : id
						};
						if(tt[i]['nodeLevel']!=4){
							Ext.Msg.alert("提示","只能勾选网元");
							return;
						}
						jString.push(neModel);
					}
					var jsonData = {
							"jString" : Ext.encode(jString)
						};

						Ext.Ajax.request({
							url : 'connection!saveClassifiedNe.action',
							method : 'POST',
							params : jsonData,
							success : function(response) {// 回调函数
								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("信息", obj.returnMessage, function(r) {
									// 刷新列表
									var westPanel = Ext.getCmp('westPanel');
									if (westPanel) {
										westPanel.update(westPanel.initialConfig.html, true);
									}
									Ext.getCmp('addNeWindow').close();
									classifiedNe.load();
								});
							},
							error : function(response) {
								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("错误", obj.returnMessage);
							},
							failure : function(response) {
								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("错误", obj.returnMessage);
							}
						});
				}else {Ext.Msg.alert("提示","请勾选网元");}
			
			}
		}, {
			text : '取消',
			handler : function() {
				//关闭修改任务信息窗口
				var win = Ext.getCmp('addNeWindow');
				if (win) {
					win.close();
				}
			}
		} ]
	});
	var addNeWindow = new Ext.Window(
			{
				id : 'addNeWindow',
				title : '新增网元',
				width : 300,
				height : 450,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				layout : 'fit',
				items : [panel]
			});
	addNeWindow.show();
}

//移动到
function moveTo(){
	var treeParams={
			leafType:leafType,
		    checkModel:"single",
		    leafType:3,
		    rootVisible : true,
		    rootType : 2,
		    rootId : checkedSubnetInfo.emsId
		};
	var treeurl="../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
	var panel = new Ext.Panel({
		id : 'moveToPanel',
		name : 'moveToPanel',
		border : true,
		labelWidth : 70,
		html : '<iframe id="tree_panel_moveTo" name ="tree_panel_moveTo" src ="'+treeurl+'"  height="100%" width="100%" frameBorder=0 border=0/>',
		buttons : [ {
			text : '确定',
			handler : function() {
				var iframe = window.frames["tree_panel_moveTo"];
				var checkedNodeIds = iframe.getCheckedNodes([ "parentId", "parentLevel","nodeId", "nodeLevel","path:text" ],"top");
				if (checkedNodeIds.length == 1) {
					var parentNodeType = checkedNodeIds[0]["parentLevel"];
					var parentNodeId = checkedNodeIds[0]["parentId"];
					var nodeType = checkedNodeIds[0]["nodeLevel"];
					var nodeId = checkedNodeIds[0]["nodeId"];
					var nodePath = checkedNodeIds[0]["path:text"];
					if(nodeType !=2 && nodeType!=3){
						Ext.Msg.alert("提示","请勾选网管或子网");
						return;
					}
					var jString = new Array();
					var tt = checkedSubnetInfo.moveNe;
					var id = nodeType == 3?nodeId:0;
					for (var i=0 ;i<tt.length;i++) {
						var neModel = {
							"neId" : tt[i],
							// 如果是网管的话直接=0
							"subnetId" : id
						};
						jString.push(neModel);
					}
					var jsonData = {
							"jString" : Ext.encode(jString)
						};

						Ext.Ajax.request({
							url : 'connection!saveClassifiedNe.action',
							method : 'POST',
							params : jsonData,
							success : function(response) {// 回调函数
								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("信息", obj.returnMessage, function(r) {
									// 刷新列表
									var westPanel = Ext.getCmp('westPanel');
									if (westPanel) {
										westPanel.update(westPanel.initialConfig.html, true);
									}
									Ext.getCmp('moveToWindow').close();
									classifiedNe.load();
								});
							},
							error : function(response) {
								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("错误", obj.returnMessage);
							},
							failure : function(response) {
								Ext.getBody().unmask();
								var obj = Ext.decode(response.responseText);
								Ext.Msg.alert("错误", obj.returnMessage);
							}
						});
				}else {Ext.Msg.alert("提示","请勾选网管或子网");}
			}
		}, {
			text : '取消',
			handler : function() {
				//关闭修改任务信息窗口
				var win = Ext.getCmp('moveToWindow');
				if (win) {
					win.close();
				}
			}
		} ]
	});
	var moveToWindow = new Ext.Window(
			{
				id : 'moveToWindow',
				title : '移动到',
				width : 300,
				height : 450,
				isTopContainer : true,
				modal : true,
				autoScroll : true,
				layout : 'fit',
				items : [panel]
			});
	moveToWindow.show();
}


Ext.onReady(function() {
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.parent.Ext.menu.MenuMgr.hideAll();
	};
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ westPanel, centerPanel ],
		//items : [centerPanel],
		renderTo : Ext.getBody()
	});
	subnetInfoPanel.getEl().mask();
	win.show();
});