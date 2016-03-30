/**
 * 定义全局变量
 */
var resourceSelectIds = parent.resourceSelectIds;
var resourceSelectLvs = parent.resourceSelectLvs;
var modifyResourceSelectIds = parent.modifyResourceSelectIds;
var modifyResourceSelectLvs = parent.modifyResourceSelectLvs;

/**
 * 创建网管分组数据源
 */
var EmsGroupStore = new Ext.data.Store({
	proxy: new Ext.data.HttpProxy({
		url : 'common!getAllEmsGroups.action', 
		disableCaching: false
	}),
	baseParams : {"displayAll" : true,"displayNone" : true},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});

/**
 * 加载网管分组数据源
 */
EmsGroupStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}else{
			var firstValue = records[0].get('BASE_EMS_GROUP_ID');
			Ext.getCmp('emsGroupCombo').setValue(firstValue);
		}
	}
});

/**
 * 创建网管分组下拉框
 */
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : EmsGroupStore,
	valueField : 'BASE_EMS_GROUP_ID',
	displayField : 'GROUP_NAME',
	editable : false,
	triggerAction : 'all',
	width :150,
	resizable: true,
	listeners : {
		select : function(combo, record, index) {

			var emsGroupId = combo.getValue();
			if(emsGroupId){
				emsCombo.enable();
			// 还原网管下拉框
				emsCombo.reset();
			// 动态改变网管数据源的参数
				emsStore.baseParams.emsGroupId = emsGroupId;
//				emsStore.baseParams = {'emsGroupId':emsGroupId};
//				// 加载网管数据源
			emsStore.load({
				callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
					if (!success) {
							Ext.Msg.alert('错误', '查询失败！');
					}else{
//						// 获取下拉框的第一条记录
//						var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//						// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
//						Ext.getCmp('emsCombo').setValue(firstValue);
						if(type=='modify'){
							getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
						}else{
							getDetailByNodeLevel(resourceSelectIds,resourceSelectLvs);
						}

					}
				}
			});
			}else{
				emsCombo.disable();
				neCombo.disable();
			}
//			var emsGroupId = record.get('BASE_EMS_GROUP_ID');
//			// 还原网管下拉框
//			Ext.getCmp('emsCombo').reset();
//			// 动态改变网管数据源的参数
//			emsStore.baseParams = {'jsonString':Ext.encode({'emsGroupId' : emsGroupId})};
//			// 加载网管数据源
//			emsStore.load({
//				callback : function(records,options,success){
//					if (!success) {
//						Ext.Msg.alert('错误', '查询失败！请重新查询');
//					}else{
//						var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//						Ext.getCmp('emsCombo').setValue(firstValue);
//						if(type=='modify'){
//							getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
//						}else{
//							getDetailByNodeLevel(resourceSelectIds,resourceSelectLvs);
//						}
//					}
//				}
//			});
		}
	}
});

/**
 * 创建网管数据源
 */
var emsStore = new Ext.data.Store({
	url : 'common!getAllEmsByEmsGroupId.action',
	baseParams : {},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_EMS_CONNECTION_ID','DISPLAY_NAME']
	})
});

/**
 * 加载网管数据源
 */
emsStore.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}else{
//			var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//			Ext.getCmp('emsCombo').setValue(firstValue);
		}
	}
});

/**
 * 创建网管下拉框
 */
var emsCombo = new Ext.form.ComboBox({
	id : 'emsCombo',
	fieldLabel : '网管',
	store : emsStore,
	valueField : 'BASE_EMS_CONNECTION_ID',
	displayField : 'DISPLAY_NAME',
	editable : false,
	triggerAction : 'all',
	width :150,
	resizable: true,
	listeners : {
		select : function(combo, record, index){
			if(type=='modify'){
				getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
			}else{
				getDetailByNodeLevel(resourceSelectIds,resourceSelectLvs);
			}
		}
	}
});
/**
* 创建表格选择模型
 */
var sm = new Ext.ux.grid.LockingCheckboxSelectionModel();
sm.sortLock();

/**
 * 创建表格列模型
 */
var cm = new Ext.ux.grid.LockingColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}), sm, {
		header : 'ID',
		dataIndex : 'ID',
		width : 87,
		hidden : true
	},{
		header : 'LV',
		dataIndex : 'LV',
		width : 87,
		hidden : true
	},{
		header : '网管分组',
		dataIndex : 'GROUP_NAME',
		width : 87
	}, {
		header : '网管',
		dataIndex : 'EMS_NAME',
		width : 87
	}, {
		header : '网元',
		dataIndex : 'NE_NAME',
		width : 87
	}, {
		header : '槽道',
		dataIndex : 'SLOT_NAME',
		width : 87
	}, {
		header : '板卡',
		dataIndex : 'UNIT_NAME',
		width : 87
	}, {
		header : '端口',
		dataIndex : 'PTP_NAME',
		width : 87
	}]
});

/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : new Ext.data.ArrayStore({
		fields : [{name:'ID'},{name:'LV'},{name:'GROUP_NAME'},{name:'EMS_NAME'},{name:'NE_NAME'},{name:'SLOT_NAME'},{name:'UNIT_NAME'},{name:'PTP_NAME'}],
		data : []
	}),
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 2,
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['网管分组：',emsGroupCombo,'-','网管：',emsCombo,'-',{
	        text: '移除',
	        style : 'margin-left:10px;',
	        icon : '../../resource/images/btnImages/delete.png',
	        handler : function(){
	        	deleteResource();
	        }
		}]
});

/**
 * 创建border布局的center部分
 */
var centerPanel = new Ext.FormPanel({
	border : false,
	region : 'center',
	layout : 'fit',
	items :gridPanel
});

/**
 * 树状结构参数
 */
var treeParams={
	rootId:0,
	rootType:0,
	rootText:"FTSP",
	rootVisible:false,
	leafType:8
};

/**
 * 创建border布局的west部分
 */
var westPanel = new Ext.Panel({
	region : 'west',
	width : 250,
	html : '<iframe id="tree_panel" name = "tree_panel" src ="../commonManager/tree.jsp?'+Ext.urlEncode(treeParams)+'" height="100%" width="100%" frameBorder=0 border=0/>'
});

/**
 * gridPanel的全选反选复选框的修复
 */
var fixGridSelectAll = function(grid) {var sm = grid.getSelectionModel();var store = grid.getStore();sm.addListener("selectionchange", function(thiz) {var hd = grid.getEl().select('div.x-grid3-hd-checker').parent().first();if(!hd) {return;} if (store.getCount()>0 && store.getCount() == thiz.getCount()) {hd.addClass('x-grid3-hd-checker-on');} else {hd.removeClass('x-grid3-hd-checker-on');}});};
Ext.grid.GridPanel.override({onRender: Ext.grid.GridPanel.prototype.onRender.createInterceptor(function(){fixGridSelectAll(this);})});

/**
 * 点击右移按钮触发
 * @param getFunc
 */
function onGetChecked(getFunc){
	// 告警源选择
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var checkedNodeIds;
	if (iframe.getCheckedNodes) {
		checkedNodeIds = iframe.getCheckedNodes(["nodeId", "nodeLevel"], "top");
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes(["nodeId","nodeLevel"], "top");
	}
	// 判断需要移动的告警源，是否已经在表格中存在了，如果不存在，就添加，否则不处理
	if(type=='modify'){
		for ( var i = 0; i < checkedNodeIds.length; i++) {
			var resourceSelectIdsArr = modifyResourceSelectIds.split(",");
			var resourceSelectLvsArr = modifyResourceSelectLvs.split(",");
			var count = 0;
			for ( var j = 0; j < resourceSelectIdsArr.length; j++) {
				if(checkedNodeIds[i].nodeId==resourceSelectIdsArr[j]&&checkedNodeIds[i].nodeLevel==resourceSelectLvsArr[j]){
					count++;
				}
			}
			if(count==0){
				modifyResourceSelectIds = modifyResourceSelectIds+checkedNodeIds[i].nodeId+",";
				modifyResourceSelectLvs = modifyResourceSelectLvs+checkedNodeIds[i].nodeLevel+",";
			}
		}
		getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
	}else{
		for ( var i = 0; i < checkedNodeIds.length; i++) {
			var resourceSelectIdsArr = resourceSelectIds.split(",");
			var resourceSelectLvsArr = resourceSelectLvs.split(",");
			var count = 0;
			for ( var j = 0; j < resourceSelectIdsArr.length; j++) {
				if(checkedNodeIds[i].nodeId==resourceSelectIdsArr[j]&&checkedNodeIds[i].nodeLevel==resourceSelectLvsArr[j]){
					count++;
				}
			}
			if(count==0){
				resourceSelectIds = resourceSelectIds+checkedNodeIds[i].nodeId+",";
				resourceSelectLvs = resourceSelectLvs+checkedNodeIds[i].nodeLevel+",";
			}
		}
		getDetailByNodeLevel(resourceSelectIds,resourceSelectLvs);
	}
};

/**
 * 移除
 */
function deleteResource(){
	// 获取选择的记录
	var record = gridPanel.getSelectionModel().getSelections();
	if(type=='modify'){
		// 更新已选的nodeId和nodeLv
		for ( var i = 0; i < record.length; i++) {
			var id = record[i].get('ID')+',';
			var lv = record[i].get('LV')+',';
			modifyResourceSelectIds = modifyResourceSelectIds.replace(id,'');
			modifyResourceSelectLvs = modifyResourceSelectLvs.replace(lv,'');
		}
		getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
	}else{
		// 更新已选的nodeId和nodeLv
		for ( var i = 0; i < record.length; i++) {
			var id = record[i].get('ID')+',';
			var lv = record[i].get('LV')+',';
			resourceSelectIds = resourceSelectIds.replace(id,'');
			resourceSelectLvs = resourceSelectLvs.replace(lv,'');
		}
		getDetailByNodeLevel(resourceSelectIds,resourceSelectLvs);
	}
}

/**
 * 刷新已选表格的数据
 * @param nodeIds
 * @param nodeLevels
 */
function getDetailByNodeLevel(nodeIds,nodeLevels){
	// 清除之前所有记录
	gridPanel.getStore().removeAll();
	// 只有当有已选择的告警源才会去更新表格数据
	if(nodeIds!=""){
		Ext.Ajax.request({
		    url: 'fault!getDetailByNodeLevel.action',
		    method: 'POST',
		    params: {
		    	'jsonString' : Ext.encode({'nodeIds':nodeIds.substring(0, nodeIds.lastIndexOf(",")),'nodeLevels':nodeLevels.substring(0, nodeLevels.lastIndexOf(",")),'emsGroupId':Ext.getCmp('emsGroupCombo').getValue(),'emsId':Ext.getCmp('emsCombo').getValue()})
		    },
		    success : function(response) {
				var obj = Ext.decode(response.responseText).rows;
				var array = new Array();
				for ( var i = 0; i < obj.length; i++) {
					// 定义表格的一条记录
					var record = new Ext.data.Record(['ID','LV','GROUP_NAME','EMS_NAME','NE_NAME','SLOT_NAME','UNIT_NAME','PTP_NAME']); 
					record.set('ID',obj[i].ID);
					record.set('LV',obj[i].LV);
					record.set('GROUP_NAME',obj[i].GROUP_NAME);
					record.set('EMS_NAME',obj[i].EMS_NAME);
					record.set('NE_NAME',obj[i].NE_NAME);
					record.set('SLOT_NAME',obj[i].SLOT_NAME);
					record.set('UNIT_NAME',obj[i].UNIT_NAME);
					record.set('PTP_NAME',obj[i].PTP_NAME);
					// 向表格里添加值
					array.push(record);
				}
				gridPanel.getStore().add(array);
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		}); 
	}
}

/**
 * 创建border布局的south部分
 */
var southPanel = new Ext.Panel({
	region : 'south',
	buttons:[{
		text:'确定',
		handler : function(){
			if(type=="modify"){
				parent.modifyResourceSelectIds = modifyResourceSelectIds;
				parent.modifyResourceSelectLvs = modifyResourceSelectLvs;
			}else{
				parent.resourceSelectIds = resourceSelectIds;
				parent.resourceSelectLvs = resourceSelectLvs;
			}
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('alarmFilterComReportResourceWindow');
			if(win){
				win.close();
			}
		}
	},{
		text:'重置',
		handler : function(){
			Ext.getCmp('emsGroupCombo').reset();
			Ext.getCmp('emsCombo').reset();
			if(type=="modify"){
				modifyResourceSelectIds = parent.modifyResourceSelectIds;
				modifyResourceSelectLvs = parent.modifyResourceSelectLvs;
				getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
			}else{
				resourceSelectIds = parent.resourceSelectIds;
				resourceSelectLvs = parent.resourceSelectLvs;
				getDetailByNodeLevel(resourceSelectIds,resourceSelectLvs);
			}
		}
	},{
		text:'取消',
		handler : function(){
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('alarmFilterComReportResourceWindow');
			if(win){
				win.close();
			}
		}
	}]
});

/**
 * 初始化窗口
 */
function initData(){
//	Ext.getCmp('emsGroupCombo').setValue('all');
//	Ext.getCmp('emsCombo').setValue('all');
	if(type=="modify"){
		if(parent.filterFlag!=filterId){
			parent.filterFlag = filterId;
			Ext.Ajax.request({
			    url: 'fault!getAlarmFilterDetailById.action',
			    method: 'POST',
			    params: {'jsonString':Ext.encode({'filterId' : filterId,'flag': 'second'})},
			    success : function(response) {
			    	var obj = Ext.decode(response.responseText).resource;
			    	var nodeIdSelect = '';
			    	var nodeLevelSelect = '';
			    	for ( var i = 0; i < obj.length; i++) {
			    		nodeIdSelect += obj[i].DEVICE_ID + ',';
			    		nodeLevelSelect += obj[i].DEVICE_TYPE + ',';
					}
			    	parent.modifyResourceSelectIds = nodeIdSelect;
			    	parent.modifyResourceSelectLvs = nodeLevelSelect;
			    	modifyResourceSelectIds = nodeIdSelect;
			    	modifyResourceSelectLvs = nodeLevelSelect;
			    	getDetailByNodeLevel(nodeIdSelect,nodeLevelSelect);
				},
				error : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				},
				failure : function(response) {
					top.Ext.getBody().unmask();
					Ext.Msg.alert("错误", response.responseText);
				}
			});
		}else{
			getDetailByNodeLevel(modifyResourceSelectIds,modifyResourceSelectLvs);
		}
	}else{
		getDetailByNodeLevel(parent.resourceSelectIds,parent.resourceSelectLvs);
	}
}

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
	
 	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initData();
  	new Ext.Viewport({
        layout : 'border',
        items : [westPanel,centerPanel,southPanel]
	});
 });
