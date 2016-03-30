// ************************* 查询结果列表 ****************************
var sm = new Ext.grid.CheckboxSelectionModel({
	singleSelect:true,
	header:""
});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, {
		id : 'templateName',
		header : '模板名称',
		dataIndex : 'templateName',
		width : 250
	}, {
		id : 'factory',
		header : '厂家',
		dataIndex : 'factory',
		width : 250,
		renderer:transFactoryName
	}, {
		id : 'updateTime',
		header : '修改日期',
		dataIndex : 'updateTime',
		width : 150
	}, {
		id : 'createTime',
		header : '创建日期',
		dataIndex : 'createTime',
		width : 150
	}]
});

var store = new Ext.data.Store({
	url:'regular-pm-analysis!getTemplatesInfo.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "templateId", "templateName", "factory", "isDefault", "updateTime", "createTime" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var factoryStore = new Ext.data.ArrayStore({
	fields : [ {name:'id',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:0,value:"全部"}]
});
factoryStore.loadData(FACTORY,true);

var factoryCombo = new Ext.form.ComboBox({
	id : 'factoryCombo',
	store : factoryStore,
	displayField : "displayName",
	valueField : 'id',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '0',
	width : 100,
	listeners : {
		'select' : function(combo, record, index) {
			getTemplatesInfo();
		}
	}
});

// 模板查询列表
var grid = new Ext.grid.EditorGridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	selModel : sm, // 必须加不然不能选checkbox
	animCollapse : false,
	frame : false,
	loadMask:true,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
// view : new Ext.ux.grid.LockingGridView(),
	tbar : [ '-',"厂家:", factoryCombo, '-',{
		text : '模板详情',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/application.png',
		handler : toDetailTemplate
	}, "-", {
		text : '新增模板',
		privilege:addAuth, 
		icon : '../../../resource/images/btnImages/add.png',
		handler : toAddTemplate
	}, '-',{
		text : '修改模板',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/modify.png',
		handler : toEditTemplate
		
	}, '-',{
		text : '模板应用',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/associate.png',
		handler : function(){
			var record = grid.getSelectionModel().getSelections();
			if(record.length>0){
				if(record.length!=1){
					Ext.Msg.alert("提示","只能选择一个模板！");
					return;
				}
				var treeParams = {
						rootId : 0,
						rootType : 0,
						rootText : "FTSP",
						rootVisible : false,
						leafType : 8
					};
				var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
				var newPanel = new Ext.Panel({
					id : "newPanel",
					region : "center",
					width : 300,
					autoScroll : true,
					boxMinWidth: 230,
				    boxMinHeight: 260,
					forceFit : true,
					collapsed : false, // initially collapse the group
					collapsible : false,
					collapseMode : 'mini',
					split : true,
					html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
							+ '" height="100%" width="100%" frameBorder=0 border=0 />'
				});
				var applyWin = new Ext.Window({
					id:"applyWin",
					title : '选择节点',
					width : 320,
					height : 350,
					layout : 'border',
					modal : true,
// closeAction:'hide',
					autoScroll : true,
					plain : true, // 是否为透明背景
					items : [ newPanel ],
					buttons:[{
							text:"确定", 
							handler:applyTemplate
						},{
							text:"取消", 
							handler:function(){
								Ext.getCmp('applyWin').close();
							}
						}]
				});
				applyWin.show();
			}else{
				Ext.Msg.alert("提示","请选择模板");
				return;
			}
		}
	},'-', {
		text : '模板解除',
		privilege:delAuth, 
		icon : '../../../resource/images/btnImages/disassociate.png',
		handler : cancelTemplate
	}, '-',{
		text : '删除模板',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : toDeleteTemplate
	} ],
	bbar : pageTool
});

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	border : false,
	layout : 'border',
	autoScroll : true,
	items : [ grid ]
});

// ************************* 函数 ****************************

function toAddTemplate() {
	var templateStore = new Ext.data.Store({
		url : 'regular-pm-analysis!getTemplates.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "PM_TEMPLATE_ID", "TEMPLATE_NAME", "FACTORY" ])
	});
	(function() {
		templateStore.baseParams = {
			'searchCond.factory' : 0,
			'searchCond.needAll' : 0,
			'searchCond.needNull' : 0
		};
		templateStore.load({
			callback : function(records, options, success) {
				if (!success)
					Ext.Msg.alert("提示", "模板加载失败");
			}
		});
	})();
	var text = new Ext.form.TextField({
		id : 'newTemplateName',
		fieldLabel : '新模板名称',
		sideText : '<font color=red>*</font>',
		readOnly:false,
		allowBlank : false,
		width : 160
	});

	var panel = new Ext.FormPanel({
		region : "center",
		frame : false,
		border : false,
		// height : 100,
		bodyStyle : 'padding:5px 5px 0 5px;',
		labelWidth : 80,
		labelAlign : 'left',
		items : [ {
			layout : 'form',
			labelSeparator : "：",
			border : false,
			items : [ {
				id : 'factory',
				xtype : 'combo',
				store : factoryStore,
				displayField : "displayName",
				fieldLabel : '厂家',
				valueField : 'id',
				displayField : 'displayName',
				triggerAction : 'all',
				mode : 'local',
				editable : false,
				allowBlank : false,
				value : '0',
				width : 160,
				listeners : {
					'select' : function(combo, record, index) {
						templateStore.baseParams = {
							'searchCond.factory' : record.get('id'),
							'searchCond.needAll' : 0,
							'searchCond.needNull' : 0
						};
						Ext.getCmp('refTemplateCombo').reset();
						templateStore.removeAll();
						templateStore.load({
							callback : function(records, options, success) {
								if (!success)
									Ext.Msg.alert("提示", "模板加载失败");
							}
						});
					}
				}
			}, {
				id : 'refTemplateCombo',
				xtype : 'combo',
				store : templateStore,
				displayField : "TEMPLATE_NAME",
				fieldLabel : '参考模板',
				valueField : 'PM_TEMPLATE_ID',
				sideText : '<font color=red>*</font>',
				triggerAction : 'all',
				mode : 'local',
				editable : false,
				allowBlank : false,
				width : 160,
				listeners : {
					'select' : function(combo, record, index) {
						Ext.getCmp('factory').setValue(record.get('FACTORY'));
					}
				}
			},text ]
		} ]
	});

	var toAddTemplateWindow = new Ext.Window({
		id : 'toAddTemplateWindow',
		title : '新增性能分析模板',
		width : 320,
		height : 160,
		layout : 'border',
		modal : true,
		plain : true, // 是否为透明背景
		items : [ panel ],
		buttons : [ {
			text : '确定', 
			handler : function() {
				var factory = Ext.getCmp('factory').getValue();
				var refTemplateId = Ext.getCmp('refTemplateCombo').getValue();
				var newTemplateName = Ext.getCmp('newTemplateName').getValue();
				if(refTemplateId==""){
						Ext.Msg.alert("提示","请选择参考模板！");
						return;
				}
				if(newTemplateName==""){
					Ext.Msg.alert("提示","请输入新模板名称！");
					return;
				}
				// 以下将参考模板复制一份保存
				var saveParams = {
						"searchCond.templateId" : refTemplateId,
						"searchCond.templateName" : newTemplateName
					};
					Ext.Ajax.request({
						url : 'regular-pm-analysis!newTemplate.action',
						params : saveParams,
						method : 'POST',
						success : function(response) {
							var result = Ext.util.JSON.decode(response.responseText);
							if (result) {
								if(result.returnResult==0){
									Ext.Msg.alert('提示',result.returnMessage);
									return;
								}
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								
								toAddTemplateWindow.close();
								url = "../../jsp/performanceManager/regularPMAnalysisManager/addTemplate.jsp?factory="
									+factory+"&newId="+result.newId+"&newTemplateName"+newTemplateName;
								top.addTabPage(url,"性能分析模板设置");
								}
						},
						failure : function(response) {
							var result = Ext.util.JSON.decode(response.responseText);
							grid.getEl().unmask();
							Ext.Msg.alert("提示", result.returnMessage);
						},
						error : function(response) {
							var result = Ext.util.JSON.decode(response.responseText);
							grid.getEl().unmask();
							Ext.Msg.alert("提示", result.returnMessage);
						}
					});
				
			
			}
		},{
			text : '取消', 
			handler : function() {
				toAddTemplateWindow.close();
			}
		} ]
	});
	toAddTemplateWindow.show();
}

function toEditTemplate() {
	var cell = grid.getSelectionModel().getSelections();
	if (cell.length == 1) {
		if(cell[0].get('isDefault')==1){
			Ext.Msg.alert('信息', '不能修改系统缺省性能分析模板，请增加新的模板后进行编辑！');
			return;
		}
		var templateId = cell[0].get('templateId');
		var factory = cell[0].get("factory");
		var url = "../../jsp/performanceManager/regularPMAnalysisManager/editTemplate.jsp?" +
				"templateId="+templateId+"&factory="+factory;
		parent.addTabPage(url,"性能分析模板设置");
	} else {
		Ext.Msg.alert('信息', '请选择模板，每次只能选择一个！');
	}
	
}

function toDetailTemplate() {
	var cell = grid.getSelectionModel().getSelections();
	if (cell.length != 1) {
		Ext.Msg.alert('信息', '请选择模板，每次只能选择一个！');
		return;
	}
	var templateId = cell[0].get("templateId");
	var factory = cell[0].get("factory");
	var url = "../../jsp/performanceManager/regularPMAnalysisManager/templateDetail.jsp?" +
			"templateId="+templateId+"&factory="+factory;
	parent.addTabPage(url, "性能分析模板详情");
}

function toDeleteTemplate(){
	var cell = grid.getSelectionModel().getSelections();
	var list = new Array();
	var isDefault = false;
	if(cell.length>0){
		for(var i=0;i<cell.length;i++){
			list.push(cell[i].get('templateId'));
			if(cell[i].get('isDefault')==1)
				isDefault = true;
		}
		if(isDefault){
			Ext.Msg.alert('信息', '不能删除系统缺省性能分析模板！');
			return;
		}
	Ext.Msg.confirm('提示', '是否删除选中的性能分析模板？',function(btn){
		if(btn == 'yes'){
				var param = {'condList':list};
				top.Ext.getBody().mask('正在删除');
				Ext.Ajax.request({
					url:'regular-pm-analysis!deleteTemplate.action',
					params : param,
					method : 'POST',
					success : function(response) {
							top.Ext.getBody().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert("提示", result.returnMessage);
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
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
		});
	}else{
		Ext.Msg.alert('信息', '请选择模板！');
		return;
	}
}

function getTemplatesInfo(){
	var factory = Ext.getCmp('factoryCombo').getValue();
	store.baseParams = {
			'factory' : factory,
			'start' : 0,
			'limit' : 200
		};
	store.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "模板加载失败");
		}
	});
} 

// 检查树的选择情况是否符合要求
function checkNodesIfLegal(selectedTargets){
	if (selectedTargets == '') {
		return '请选择节点！';
	}
	var emsId = selectedTargets[0].emsId;
	var countEms = 0;
	for ( var i = 0; i < selectedTargets.length; i++){
		if (selectedTargets[i].nodeLevel == 1)
			return '请勿选择网管分组！';
		if(selectedTargets[i].emsId!=emsId)
			return '只能选择同一网管的节点！';
	}
	return '';
}

function applyTemplate(){
	var iframe = window.frames["tree_panel"];
	var record = grid.getSelectionModel().getSelections();
	var list = new Array();
	var selectedTargets ;
		if (iframe.getCheckedNodes) {
			selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel","emsId" ],"top");
			// 检查树的选择情况是否符合要求
			if(checkNodesIfLegal(selectedTargets)!=''){
				 Ext.MessageBox.show({
			            title: '错误',
			            msg: checkNodesIfLegal(selectedTargets),
			            buttons: Ext.MessageBox.OK,
			            icon: Ext.MessageBox.ERROR
			        });
				 return;
			}
			for ( var i = 0; i < selectedTargets.length; i++) {
				var nodes = {
					'nodeId' : selectedTargets[i].nodeId,
					'nodeLevel' : selectedTargets[i].nodeLevel
				};
				list.push(Ext.encode(nodes));
			}
			var params = {
				'modifyList' : list,
				'searchCond.templateId' : record[0].get('templateId'),
				'searchCond.factory' : record[0].get('factory')
			};
			Ext.getBody().mask('正在应用...');
			Ext.Ajax.request({
				url : 'regular-pm-analysis!applyTemplate.action',
				params : params,
				method : 'POST',
				success : function(response) {
					Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					if (result) {
						Ext.Msg.alert("提示", result.returnMessage);
						if(result.returnResult==1){
							Ext.getCmp('applyWin').close();
						}
						// 提交修改，不然store.getModifiedRecords();数据会累加
						/*
						 * store.commitChanges(); if (1 == result.returnResult) {
						 * var pageTool = Ext.getCmp('pageTool'); if (pageTool) {
						 * pageTool.doLoad(pageTool.cursor); } }
						 */
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
}

function cancelTemplate(){
	var list= new Array();
	var record = grid.getSelectionModel().getSelections();
	if(record.length>0){
		Ext.Msg.confirm('提示','将解除所有应用此模板的端口与板卡，是否继续？',function(btn){
			if(btn=='yes'){
				for(var i=0;i<record.length;i++){
					list.push(record[i].get('templateId'));
				}
				var params = {
						'condList' : list
				};
				Ext.Ajax.request({
					url : 'regular-pm-analysis!detachTemplate.action',
					params : params,
					method : 'POST',
					success : function(response) {
						grid.getEl().unmask();
						var result = Ext.util.JSON.decode(response.responseText);
						if (result) {
							Ext.Msg.alert("提示", result.returnMessage);
							// 提交修改，不然store.getModifiedRecords();数据会累加
							/*
							 * store.commitChanges(); if (1 ==
							 * result.returnResult) { var pageTool =
							 * Ext.getCmp('pageTool'); if (pageTool) {
							 * pageTool.doLoad(pageTool.cursor); } }
							 */
						}
					},
					failure : function(response) {
						var result = Ext.util.JSON.decode(response.responseText);
						grid.getEl().unmask();
						Ext.Msg.alert("提示", result.returnMessage);
					},
					error : function(response) {
						var result = Ext.util.JSON.decode(response.responseText);
						grid.getEl().unmask();
						Ext.Msg.alert("提示", result.returnMessage);
					}
				});
			}
			
		});
		
	}else{
		Ext.Msg.alert("提示", "请选择需要解除的模板！");
		return;
	}
}

function transFactoryName(v){
	if(factoryStore.find("id",v)!=-1){
		return factoryStore.getAt(factoryStore.find("id",v)).get("displayName");
	}
}
Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	// Ext.Msg = top.Ext.Msg;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ centerPanel ],
		renderTo : Ext.getBody()
	});
	getTemplatesInfo();
	win.show();

});