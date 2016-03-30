/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

/*
 * store.proxy = new Ext.data.HttpProxy({ url : 'getTaskInfoList.action' });
 * store.baseParams = { "taskInfoModel.taskId" : taskId }; store.load({ callback :
 * function(r, options, success) { if (success) { } else { Ext.Msg.alert('错误',
 * '查询失败！请重新查询'); } } });
 */
var level = 1; 
  Ext.QuickTips.init();
  
  Ext.state.Manager.setProvider(   
    new Ext.state.SessionStorageStateProvider({   
      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
    })   
  );
  
  
var factoryStore = new Ext.data.ArrayStore({
	fields : [ {name:'id',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:0,value:"全部"}]
});
factoryStore.loadData(FACTORY,true);
//用于保存上一次查询条件
var selectedTargets4Del;
var factory4Del;
var templateId4Del;

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
			templateStore.baseParams = {
				'searchCond.factory' : record.get('id'),
				'searchCond.needAll' : 1,
				'searchCond.needNull' : 1
			};
			Ext.getCmp('templateCombo').reset();
			templateStore.removeAll();
			templateStore.load({
				callback : function(records, options, success) {
					if (!success)
						Ext.Msg.alert("提示", "模板加载失败");
				}
			});
		}
	}
});

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
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 1
	};
	templateStore.load({
		callback : function(records, options, success) {
			if (success){
				templateCombo.setValue(0);
//				alert(templateStore.reader.jsonData.msg);
			}
			else
				Ext.Msg.alert("提示", "模板加载失败");
		}
	});
})();

var templateCombo = new Ext.form.ComboBox({
	id : 'templateCombo',
	store : templateStore,
	displayField : "TEMPLATE_NAME",
	valueField : 'PM_TEMPLATE_ID',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	resizable: true,
	// value : '全部',
	width : 100
});
var templateEditStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getTemplates.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "PM_TEMPLATE_ID", "TEMPLATE_NAME", "FACTORY" ])
});

var templateEditCombo = new Ext.form.ComboBox({
	id : 'templateEditCombo',
	store : templateEditCombo,
	displayField : "TEMPLATE_NAME",
	valueField : 'PM_TEMPLATE_ID',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	// value : '全部',
	width : 100
});

// ************************* 模板列表 ****************************
var store = new Ext.data.Store({
	url : 'regular-pm-analysis!searchPtpTemplate.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "EMSGroup", "EMSDispalyName", "NeDisplayName", "subNetWork",
			"templateName", "port", "stationName", "area", "shelf", "unit",
			"ptpId", "unitId", "factory", "templateId", "NeType", 
			"portDescription", "unitDescription", "unitDescription" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : false
});
checkboxSelectionModel.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : false
	// columns are not sortable by default
	},
	stateId:'templateGridStoreId',  
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked:true
		}), checkboxSelectionModel, {
		id : 'ptpId',
		header : 'ptpId',
		dataIndex : 'ptpId',
		hidden:true,
		hideable:false,
		width : 100
	}, {
		id : 'unitId',
		header : 'unitId',
		dataIndex : 'unitId',
		hidden:true,
		hideable:false,
		width : 100
	},{
		id : 'EMSGroup',
		header : '网管分组',
		locked:true,
		dataIndex : 'EMSGroup',
		width : 100
	}, {
		id : 'EMSDispalyName',
		header : '网管',
		locked:true,
		dataIndex : 'EMSDispalyName',
		width : 100
	}, {
		id : 'subNetwork',
		header : '子网',
		locked:true,
		dataIndex : 'subNetwork',
		width : 100
	}, {
		id : 'NeDisplayName',
		header : '网元',
		dataIndex : 'NeDisplayName',
		locked:true,
		width : 100
	}, {
		id : 'area',
		header : top.FieldNameDefine.AREA_NAME,
		hidden:true,
		dataIndex : 'area',
		width : 100
	}, {
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME,
		hidden:true,
		dataIndex : 'stationName',
		width : 100
	}, {
		id : 'NeType',
		header : '网元型号',
		dataIndex : 'NeType',
		width : 100
	}, {
		id : 'description',
		header : '端口信息',
		dataIndex : 'portDescription',
		width : 220
	}, {
		id : 'templateId',
		header : '<span style="font-weight:bold">性能分析模板</span>',
		dataIndex : 'templateId',
		width : 100,
		renderer : transTemplateName,
		tooltip:'可编辑列',
		editor : new Ext.form.ComboBox({
			id : 'templateEdit',
			triggerAction : 'all',
			editable : false,
			store : templateEditStore,
			displayField : "TEMPLATE_NAME",
			valueField : 'PM_TEMPLATE_ID',	
			resizable: true,
			listeners : {
				select : function(combo, record, index) {
					// searchPrice();

				}

			}
		})
	} ]
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	stateId:'templateGridStoreId',  
	stateful:true,
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	frame : false,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-', "厂家:", factoryCombo, '-',"性能分析模板:", templateCombo, '-',{
		text : '查询',
		privilege:viewAuth, 
		icon : '../../../resource/images/btnImages/search.png',
		menu : {
			items : [ {
				text : '查询板卡',
				handler : searchPtpTemplate(2)
			}, {
				text : '查询端口',
				handler : searchPtpTemplate(1)
			} ]
		}
		// handler : searchPtpTemplate
	}, "-", {
		text : '保存',
		privilege:modAuth, 
		icon : '../../../resource/images/btnImages/disk.png',
		handler : savePtpTemplate
	},'-', {
		text : '模板解除',
		privilege:delAuth, 
		icon : '../../../resource/images/btnImages/disassociate.png',
//		handler : cancelPtpTemplate
		menu : {
			items : [ {
				text : '解除选中',
				handler : cancelPtpTemplate
			}, {
				text : '解除全部',
				handler : cancelTemplateBatch
			} ]
		}
	} ],
	bbar : pageTool,
	listeners : {
		rowdblclick : function(t, rowIndex, e) {
			e.preventDefault();// 阻止默认事件的传递
			var factory = t.store.getAt(rowIndex).get('factory');
			templateEditStore.baseParams = {
				'searchCond.factory' : factory,
				'searchCond.needAll' : 0,
				'searchCond.needNull' : 1
			};
			templateEditStore.load({
				callback : function(records, options, success) {
					if (!success)
						Ext.Msg.alert("提示", "模板加载失败");
				}
			});
		}
	}
});

var treeParams = {
	rootId : 0,
	rootType : 0,
	rootText : "FTSP",
	rootVisible : false,
	leafType : 8
};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
var westPanel = new Ext.Panel({
	id : "westPanel",
	region : "west",
	width : 300,
	autoScroll : true,
	boxMinWidth : 230,
	boxMinHeight : 260,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0 />'
});

//检查树的选择情况是否符合要求
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

function searchPtpTemplate(searchLevel){
	function doSearch(){
		level = searchLevel;
	var iframe = window.frames["tree_panel"];
	var selectedTargets;
	var list = new Array();
	if (iframe.getCheckedNodes) {
			selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" , "emsId"],"top");
		//检查树的选择情况是否符合要求
		if(checkNodesIfLegal(selectedTargets)!=''){
			 Ext.MessageBox.show({
		            title: '错误',
		            msg: checkNodesIfLegal(selectedTargets),
		            buttons: Ext.MessageBox.OK,
		            icon: Ext.MessageBox.ERROR
		        });
			 return;
		}
		
			// 修改columnModel
			if(searchLevel==1){
				var column = cm.getColumnById("description");
				column.header = '端口信息';
				column.dataIndex = 'portDescription';
			}else if(searchLevel==2){
				var column = cm.getColumnById("description");
				column.header = '板卡信息';
				column.dataIndex = 'unitDescription';
			}
			
		//保存查询条件
		selectedTargets4Del = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "emsId" ],"leaf");
		factory4Del=Ext.getCmp('factoryCombo').getValue();
		templateId4Del=Ext.getCmp('templateCombo').getValue();
		
		for ( var i = 0; i < selectedTargets.length; i++) {
			var nodes = {
				'nodeId' : selectedTargets[i].nodeId,
				'nodeLevel' : selectedTargets[i].nodeLevel
			};
			list.push(Ext.encode(nodes));
		}
		var searchParams = {
			'modifyList' : list,
			'searchCond.factory' : Ext.getCmp('factoryCombo').getValue(),
			'searchCond.templateId' : Ext.getCmp('templateCombo').getValue(),
				'searchCond.searchLevel' : searchLevel,
			'limit' : 200,
			'start' : 0
		};
		store.baseParams = searchParams;
		store.load({
			callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "查询失败！");
		}});
	}
}

	return doSearch;
}
function savePtpTemplate() {
	var records = store.getModifiedRecords();
	var list = new Array();
	if (records.length > 0) {
		if (level == 1){
		for ( var i = 0; i < records.length; i++) {
			var ptpAndTemplate = {
				"templateId" : records[i].get("templateId"),
				"ptpId" : records[i].get("ptpId")
			};
			list.push(Ext.encode(ptpAndTemplate));
		}
		}else if (level==2){
			for ( var i = 0; i < records.length; i++) {
				var ptpAndTemplate = {
					"templateId" : records[i].get("templateId"),
					"unitId" : records[i].get("unitId")
				};
				list.push(Ext.encode(ptpAndTemplate));
			}
		}
		
		var saveParams = {
			"modifyList" : list,
			"searchCond.searchLevel" : level
		};
		Ext.Ajax.request({
			url : 'regular-pm-analysis!savePtpTemplate.action',
			params : saveParams,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					// 提交修改，不然store.getModifiedRecords();数据会累加
					
					if (1 == result.returnResult) {
						store.commitChanges();
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					}
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}

function cancelPtpTemplate() {
	var records = gridPanel.getSelectionModel().getSelections();
	var list = new Array();
	if (records.length > 0) {
		Ext.Msg.confirm("提示", "是否解除选中对象的性能分析模板？", function(btn) {
			if (btn == "yes") {
				if(level==1){
				for ( var i = 0; i < records.length; i++) {
					list.push(records[i].get("ptpId"));
				}
				}else if(level==2){
					for ( var i = 0; i < records.length; i++) {
						list.push(records[i].get("unitId"));
					}
				}
				
				var params = {
					"condList" : list,
					"searchCond.searchLevel" : level
				};
				Ext.Ajax.request({
					url : 'regular-pm-analysis!cancelPtpTemplate.action',
					params : params,
					method : 'POST',
					success : function(response) {
						gridPanel.getEl().unmask();
						var result = Ext.util.JSON
								.decode(response.responseText);
						if (result) {
							Ext.Msg.alert("提示", result.returnMessage);
							// 提交修改，不然store.getModifiedRecords();数据会累加
							store.commitChanges();
							if (1 == result.returnResult) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}
						}
					},
					failure : function(response) {
						var result = Ext.util.JSON
								.decode(response.responseText);
						gridPanel.getEl().unmask();
						Ext.Msg.alert("提示", result.returnMessage);
					},
					error : function(response) {
						var result = Ext.util.JSON
								.decode(response.responseText);
						gridPanel.getEl().unmask();
						Ext.Msg.alert("提示", result.returnMessage);
					}
				});
			}
		});
	} else {
		Ext.Msg.alert("提示", "请选择需要解除的端口性能分析模板！");
		return;
	}
}

function cancelTemplateBatch(){
//	var iframe = window.frames["tree_panel"] || window.frames[0];
//	var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ]);
//	if (selectedTargets == '') {
//		Ext.Msg.alert('提示', '请选择节点！');
//		return;
//	}
	if(store.getCount()==0){
		Ext.Msg.alert("提示", "无记录！");
		return;
	}
	Ext.Msg.confirm("提示", "是否解除选中对象的性能分析模板？", function(btn) {
		if(btn=='yes'){
			var list= new Array();
			for ( var i = 0; i < selectedTargets4Del.length; i++) {
				// alert('id：'+selectedTargets[i].nodeId+'-level：'+selectedTargets[i].nodeLevel);
				var nodes = {
					'nodeId' : selectedTargets4Del[i].nodeId,
					'nodeLevel' : selectedTargets4Del[i].nodeLevel
				};
				list.push(Ext.encode(nodes));
			}
			var params = {
				'modifyList' : list,
				'searchCond.factory' : factory4Del,
				'searchCond.templateId' : templateId4Del,
				"searchCond.searchLevel" : level
			};
			Ext.Ajax.request({
				url : 'regular-pm-analysis!cancelTemplateBatch.action',
				params : params,
				method : 'POST',
				success : function(response) {
					gridPanel.getEl().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					if (result) {
						Ext.Msg.alert("提示", result.returnMessage);
						// 提交修改，不然store.getModifiedRecords();数据会累加
//						store.commitChanges();
						if (1 == result.returnResult) {
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						}
					}
				},
				failure : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					gridPanel.getEl().unmask();
					Ext.Msg.alert("提示", result.returnMessage);
				},
				error : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					gridPanel.getEl().unmask();
					Ext.Msg.alert("提示", result.returnMessage);
				}
			});
		}
	});
	
}

function transTemplateName(v) {
	var index = templateStore.find("PM_TEMPLATE_ID", v);
	if (templateStore.find("PM_TEMPLATE_ID", v) != -99 && index!=-1){
		return templateStore.getAt(index).get("TEMPLATE_NAME");
	}

}
Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 900000;

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ gridPanel, westPanel ]
			});
			win.show();

		});
