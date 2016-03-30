Ext.state.Manager.setProvider(new Ext.state.SessionStorageStateProvider({
	expires : new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365))
}));

var params={};
var descLevel1 = "省", descLevel2 = "市", descLevel3 = "区", descLevel4 = "街道";
var tree = null;
var westPanel = {
	region : "west",
	title : top.FieldNameDefine.AREA_NAME+'选择',
	width : 280,
	minSize : 230,
	maxSize : 320,
	autoScroll : true,
	forceFit : true,
	collapsed : false,
	collapsible : true,
	split : true,
	id : "tree",
	xtype : "area",
	maxLevel : 4,
	checkModel : "path"
};

// ************************* 查询条件 ****************************
proType.getStore().loadData([{key:null,value:'全部'}],true);
netLevel.getStore().loadData([{key:null,value:'全部'}],true);
sysRate.getStore().loadData([[null,'全部']],true);
domain.getStore().loadData([[null,'全部']],true);
structure.getStore().loadData([[null,'全部']],true);
genMethod.getStore().loadData([[null,'全部']],true);
transMedium.getStore().loadData([[null,'全部']],true);
var searchPanel = new Ext.FormPanel({
	id : 'searchPanel',
	region : 'north',
	height : 160,
	bodyStyle : 'padding:20px 10px 0',
	autoScroll : true,
	collapsible : true,
	items : [ {
		border : false,
		layout : 'column',
		items : [ {
			border : false,
			columnWidth : 0.3,
			items : [ {
				layout : 'form',
				labelWidth : 80,
				border : false,
				items : [ sysCode, domain, proType ]
			} ]
		}, {
			border : false,
			columnWidth : 0.3,
			items : [ {
				layout : 'form',
				labelWidth : 80,
				border : false,
				items : [ sysName, structure, genMethod ]
			} ]
		}, {
			border : false,
			columnWidth : 0.3,
			items : [ {
				layout : 'form',
				labelWidth : 80,
				border : false,
				items : [ sysRate, netLevel, emsCombo ]
			} ]
		} ]
	} ],
	tbar : [ '-', {
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : queryTransmissionSystem
	} ]
});

//查询传输系统
function queryTransmissionSystem(){
	
	//区域
	var selectedNodes = Ext.getCmp("tree").getSelectedNodes();
	var areaIds = "";
	if(selectedNodes != null && selectedNodes.total > 0){
		for(var i=0;i<selectedNodes.total;i++){
			var areaId = selectedNodes.nodes[i].id;
			if(areaId != 0){
				areaIds = areaIds + areaId + ",";
			}
		}
	}
	//系统代号
	var sysCode = Ext.getCmp("sysCode").getValue();
	//系统名称
	var sysName = Ext.getCmp("sysName").getValue();
	//系统速率
	var sysRate = Ext.getCmp("sysRate").getRawValue();
	//技术体制
	var domain = Ext.getCmp("domain").getValue();
	//拓扑结构
	var type = Ext.getCmp("structure").getValue();
	//网络层级
	var netLevel = Ext.getCmp("netLevel").getValue();
	//保护类型
	var proType = Ext.getCmp("proType").getValue();
	//生成方式
	var genMethod = Ext.getCmp("genMethod").getValue();
	//网管
	var emsCombo = Ext.getCmp("emsCombo").getValue();
	
	params= {
		"paramMap.sysCode" : sysCode,
		"paramMap.sysName" : sysName,
		"paramMap.sysRate" : sysRate=='全部'?null:sysRate,
		"paramMap.domain" : domain,
		"paramMap.type" : type,
		"paramMap.netLevel" : netLevel,
		"paramMap.proType" : proType,
		"paramMap.genMethod" : genMethod,
		"paramMap.emsCombo" : emsCombo==-99?null:emsCombo,
		"paramMap.areaIds" : areaIds,
		"limit" : 200
	};
	
	store.load({
		params : params,
		callback : function(r, o, s){
			if(!s){
				Ext.Msg.alert("提示：","查询传输系统失败！");
			}
		}
	});
}

var store = new Ext.data.Store({
	url : "trans-system!queryTransmissionSystem.action",
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "RESOURCE_TRANS_SYS_ID", "AREA_FULL_PATH", "SYS_NAME", "SYS_CODE", "DOMAIN",
			"TYPE", "NODE_COUNT", "PRO_GROUP_TYPE", "RATE", "NET_LEVEL",
			"GENERATE_METHOD", "STATUS", "EMS_NAME", "NOTE",
			"TRANS_MEDIUM", "WAVE_COUNT" ])
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : false
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'RESOURCE_TRANS_SYS_ID',
		header : 'ID',
		dataIndex : 'RESOURCE_TRANS_SYS_ID',
		hidden : true
	},{
		id : 'AREA_FULL_PATH',
		header : '所属'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'AREA_FULL_PATH',
		width : 100
	}, {
		id : 'SYS_NAME',
		header : '系统名称',
		dataIndex : 'SYS_NAME',
		width : 100
	}, {
		id : 'SYS_CODE',
		header : '系统代号',
		dataIndex : 'SYS_CODE',
		width : 70
	}, {
		id : 'DOMAIN',
		header : '技术体制',
		dataIndex : 'DOMAIN',
		width : 80,
		renderer : domainRenderer
	}, {
		id : 'TYPE',
		header : '拓扑结构',
		dataIndex : 'TYPE',
		width : 80,
		renderer : structureRenderer
	}, {
		id : 'TRANS_MEDIUM',
		header : '传输介质',
		dataIndex : 'TRANS_MEDIUM',
		width : 80,
		renderer : transMediumRenderer
	}, {
		id : 'NODE_COUNT',
		header : '节点数',
		dataIndex : 'NODE_COUNT',
		width : 70
	}, {
		id : 'PRO_GROUP_TYPE',
		header : '保护类型',
		dataIndex : 'PRO_GROUP_TYPE',
		width : 100,
		renderer : proTypeRenderer
	}, {
		id : 'WAVE_COUNT',
		header : '波道数',
		dataIndex : 'WAVE_COUNT',
		width : 70
	}, {
		id : 'RATE',
		header : '速率',
		dataIndex : 'RATE',
		width : 70
	}, {
		id : 'NET_LEVEL',
		header : top.FieldNameDefine.NET_LEVEL_NAME,
		dataIndex : 'NET_LEVEL',
		width : 80,
		renderer : netLevelRenderer
	}, {
		id : 'GENERATE_METHOD',
		header : '生成方式',
		dataIndex : 'GENERATE_METHOD',
		width : 80,
		renderer : genMethodRenderer
	}, {
		id : 'STATUS',
		header : '状态',
		dataIndex : 'STATUS',
		width : 70,
		renderer : statusRenderer
	}, {
		id : 'EMS_NAME',
		header : '所在网管',
		dataIndex : 'EMS_NAME',
		width : 120,
		renderer : function(v, m, r){
			if(v != null){
				var array = v.split(",");
				var obj = {}, a = [];
				for (var i = 0, iLen = array.length; i < iLen; i++) {
					if (!obj[array[i]]) {
						a.push(array[i]);
						obj[array[i]] = true;
					}
				}
				
				return a;
			}
		}
	}, {
		id : 'NOTE',
		header : '备注',
		dataIndex : 'NOTE',
		width : 70
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false,
	stateful:true,
	loadMask:true,
	stateId:"transmissonStateId",
	stripeRows : true, // 交替行效果
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool,
	tbar : [ '-', {
		text : '自动发现',
		icon : '../../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : function() {
			var treeParams = {
					leafType : 4
				};
				var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
				var treePanel = new Ext.Panel({
					id : "treePanel",
					region : "center",
					boxMinWidth : 230,
					boxMinHeight : 260,
					forceFit : true,
					// split : true,
					html : '<iframe id="tree_panel" name="tree_panel" src ="' + treeurl
							+ '" height="100%" width="100%" frameBorder=0 border=0/>'
				});
				var win = new Ext.Window({
					title : "发现范围",
					layout : 'border',
					id : 'chooseNeWin',
					height : 400,
					width : 300,
					modal : true,
					plain : true,
					items : treePanel,
					buttons : [ {
						text : '确定', 
						handler : goFind
					}, {
						text : '取消', 
						handler : function() {
							win.close();
						}
					} ]
				});
				win.show();
				
				function goFind(){
					var iframe = window.frames["tree_panel"];
					var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel",
							"text", "emsId", "path%text" ], "top");
					var list = new Array();
					for ( var i = 0; i < selectedTargets.length; i++) {
						// alert('id：'+selectedTargets[i].nodeId+'-level：'+selectedTargets[i].nodeLevel);
						if(selectedTargets[i].nodeLevel<2){
							Ext.Msg.alert('信息','请勿选择网管分组！');
							return;
						}
						var nodes = {
							'nodeId' : selectedTargets[i].nodeId,
							'nodeLevel' : selectedTargets[i].nodeLevel
						};
						list.push(Ext.encode(nodes));
					}
					var params = {
							'modifyList' : list
						};
					top.Ext.getBody().mask('正在执行，请稍候...');
					Ext.Ajax.request({
						url : 'trans-system!autoFindSystem.action',
						params : params,
						method : 'POST',
						success : function(response) {
							top.Ext.getBody().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							if (result) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								win.close();
							}
						},
						failure : function(response) {
							top.Ext.getBody().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert("提示", result.returnMessage);
						},
						error : function(response) {
							top.Ext.getBody().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert("提示", result.returnMessage);
						}
					});
					
				}
		}
	}, '-', {
		text : '手动新增',
		icon : '../../../resource/images/btnImages/add.png',
		privilege : addAuth,
		handler : manualAdd
	}, '-', {
		text : '删除',
		icon : '../../../resource/images/btnImages/delete.png',
		privilege : delAuth,
		handler : deleteTransmissionSystem
	}, '-', {
		text : '修改',
		icon : '../../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler : edit
	}, '-', {
		text : '详情',
		icon : '../../../resource/images/btnImages/information.png',
		privilege : viewAuth,
		handler : detail
	}, '-', {
		text : '导出',
		icon : '../../../resource/images/btnImages/export.png',
		privilege : actionAuth,
		handler : exportData
	} ]
});

//删除传输系统
function deleteTransmissionSystem(){
	
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length > 0){
		Ext.Msg.confirm('提示', '确认删除？', function(btn) {
			if (btn == 'yes') {
				top.Ext.getBody().mask('正在执行，请稍候...');
				
				var jsonData = {
					"paramMap.transSysId" : cell[0].get('RESOURCE_TRANS_SYS_ID')
				};
				Ext.Ajax.request({
					url : 'trans-system!deleteTransmissionSystem.action',
					method : 'POST',
					params : jsonData,
					success : function(response) {
						top.Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示", "删除传输系统成功！", function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});
						}
						if (obj.returnResult == 0) {
							Ext.Msg.alert("提示", obj.returnMessage);
						}
					},
					error : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("异常", response.responseText);
					},
					failure : function(response) {
						top.Ext.getBody().unmask();
						Ext.Msg.alert("异常", response.responseText);
					}
				});
			}
		});
	}else{
		Ext.Msg.alert("提示", "请选择需要删除的传输系统！");
	}
}

function manualAdd(){
	var url = '../networkAnalysis/transmissionSystem/manualAddTransSys.jsp';
	parent.addTabPage(url, "手动新增");
}

function edit(){
	var record = gridPanel.getSelectionModel().getSelected();
	if(!record){
		Ext.Msg.alert("提示", "请选择需要修改的传输系统！");
		return;
	}
	var url = '../networkAnalysis/transmissionSystem/editTransSys.jsp?id='+record.get('RESOURCE_TRANS_SYS_ID')+'&type=2';
	parent.addTabPage(url, "修改");
}
function detail(){
	var record = gridPanel.getSelectionModel().getSelected();
	if(!record){
		Ext.Msg.alert("提示", "请选择需要查看的传输系统！");
		return;
	}
	var url = '../networkAnalysis/transmissionSystem/editTransSys.jsp?id='+record.get('RESOURCE_TRANS_SYS_ID')+'&type=3';
	parent.addTabPage(url, "详情");
}
//导出
function exportData(){ 
	params["paramMap.flag"] = 9;
	if (store.getCount() == 0) {
		Ext.Msg.alert("提示", "结果为空，不导出！");
	} else if (store.getTotalCount() > 2000) {
		Ext.Msg.confirm("提示", "最多导出2000条数据，是否导出？", function(btn) {
			if (btn == 'yes') {
				{ 
					gridPanel.getEl().mask("正在导出...");
					exportRequest(params);
				}
			}
		});
	} else{
		gridPanel.getEl().mask("正在导出...");
		exportRequest(params); 
	} 
}  

// 调整组件大小格式
(function() {
	Ext.apply(emsCombo, {
		anchor : '90%',
		width : 'auto',
		listWidth : '',
		disabled : false
	});
	emsCombo.getStore().baseParams.emsGroupId = -99;
	emsCombo.getStore().baseParams.displayAll = true;
	emsCombo.getStore().load();
})();

Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Ajax.timeout = 90000000;
			proType.getStore().loadData({id:'',displayName:'全部'},true);
			netLevel.getStore().loadData({id:'',displayName:'全部'},true);
			var centerPanel = new Ext.Panel({
				id : 'centerPanel',
				region : 'center',
				border : false,
				layout : 'border',
				autoScroll : true,
				items : [ searchPanel, gridPanel ]
			});

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ centerPanel, westPanel ]
			});
			win.show();
			tree = Ext.getCmp("tree");
		});
