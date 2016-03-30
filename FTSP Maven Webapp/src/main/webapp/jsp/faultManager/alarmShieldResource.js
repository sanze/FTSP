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
		width : 150
	}, {
		header : '网管',
		dataIndex : 'EMS_NAME',
		width : 150
	}, {
		header : '网元',
		dataIndex : 'NE_NAME',
		width : 150
	}]
});

/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	store : new Ext.data.ArrayStore({
		fields : [{name:'ID'},{name:'LV'},{name:'GROUP_NAME'},{name:'EMS_NAME'},{name:'NE_NAME'}],
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
	tbar : [{
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
	leafType:4
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
		checkedNodeIds = iframe.getCheckedNodes(["nodeId", "nodeLevel", "path*text"], "top",[2,4]);
	} else {
		checkedNodeIds = iframe.contentWindow.getCheckedNodes(["nodeId","nodeLevel", "path*text"], "top",[2,4]);
	}
	if (checkedNodeIds.length == 0) {
		Ext.Msg.alert('提示', '请先勾选网管或网元节点后再添加。');
		return false;
	}
	// 右侧已有的设备
	var rightRecords = gridPanel.getStore().getRange();
	// 判断需要移动的告警源，是否已经在表格中存在了，如果不存在，就添加，否则不处理
	var records = [];
	for ( var i = 0; i < checkedNodeIds.length; i++) {
		var count = 0;
		for ( var j = 0; j < rightRecords.length; j++) {
			if (checkedNodeIds[i].nodeLevel != rightRecords[j].get('LV')) {
				Ext.Msg.alert('提示', '一个告警屏蔽中只能选择一种对象类型，网管或网元。');
				return false;
			}
			if(checkedNodeIds[i].nodeId==rightRecords[j].get('ID')&&checkedNodeIds[i].nodeLevel==rightRecords[j].get('LV')){
				count++;
			}
		}
		if(count==0){
			//rightMove(checkedNodeIds[i].nodeId,checkedNodeIds[i].nodeLevel);
			// 定义表格的一条记录
			var record = new Ext.data.Record(); 
			record.set('ID', checkedNodeIds[i].nodeId);
			record.set('LV', checkedNodeIds[i].nodeLevel);
			var names = checkedNodeIds[i]['path*text'].split("*");
			if (checkedNodeIds[i].nodeLevel == 2) {
				if (names.length > 1) { // 有网管分组
					record.set('GROUP_NAME', names[0]);
					record.set('EMS_NAME', names[1]);
				} else { // 没有网管分组
					record.set('GROUP_NAME', '');
					record.set('EMS_NAME', names[0]);
				}
			}
			record.set('NE_NAME', null);
			if (checkedNodeIds[i].nodeLevel == 4) {
				if (names.length > 2) { // 有网管分组
					record.set('GROUP_NAME', names[0]);
					record.set('EMS_NAME', names[1]);
					record.set('NE_NAME', names[names.length-1]);
				} else { // 没有网管分组
					record.set('GROUP_NAME', '');
					record.set('EMS_NAME', names[0]);
					record.set('NE_NAME', names[names.length-1]);
				}
			}
			
			records.push(record);
			if (rightRecords.length == 0) {
				rightRecords.push(record);
			}
		}
	}
	// 向表格里添加值
	gridPanel.getStore().add(records);
};

function onCheckChange(node,checked){
	if (checked == "all") {
		if (node.attributes.nodeLevel == 1 && !node.expanded) {
			Ext.Msg.alert('提示', '请先展开网管分组到网管级节点。');
		}
		if (node.attributes.nodeLevel == 1 && node.expanded && node.childNodes.length==0) {
			Ext.Msg.alert('提示', '请选择包含网管节点的网管分组。');
		}
		if (node.attributes.nodeLevel == 3 && !node.expanded) {
			Ext.Msg.alert('提示', '请先展开子网到网元级节点。');
		}
		if (node.attributes.nodeLevel == 3 && node.expanded && node.childNodes.length==0) {
			Ext.Msg.alert('提示', '请选择包含网元节点子网。');
		}
	}
	
}

/**
 * 右移
 * @param nodeId 设备节点ID
 * @param nodeId 设备节点等级
 */
function rightMove(nodeId,nodeLv){
	Ext.Ajax.request({
	    url: 'fault!getSimpleByNodeLevel.action',
	    method: 'POST',
	    params: {
	    	'jsonString' : Ext.encode({'nodeId':nodeId,'nodeLevel':nodeLv})
	    },
	    success : function(response) {
			var obj = Ext.decode(response.responseText).rows;
			// 定义表格的一条记录
			var record = new Ext.data.Record(['ID','LV','GROUP_NAME','EMS_NAME','NE_NAME']); 
			record.set('ID',obj.ID);
			record.set('LV',obj.LV);
			record.set('GROUP_NAME',obj.GROUP_NAME);
			record.set('EMS_NAME',obj.EMS_NAME);
			record.set('NE_NAME',obj.NE_NAME);
			// 向表格里添加值
			gridPanel.getStore().add(record);
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

/**
 * 移除
 */
function deleteResource(){
	// 获取选择的记录
	var records = gridPanel.getSelectionModel().getSelections();
	var len = records.length;
	if(len <= 0) {
	       parent.Ext.Msg.alert("提示", "请选择需要删除的网管或网元！");
    } else {
       for(var i = 0;i<len;i++ ){
	        gridPanel.store.remove(records[i]);
	   }
    }
//	for ( var i = 0; i < records.length; i++) {
//		gridPanel.getStore().remove(records[i]);
//	}
	// 删除后刷新型号
//	gridPanel.getView().refresh();
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
				// 右侧已有的设备
				var rightRecords = gridPanel.getStore().getRange();
				// 先清空原来的，否则会累加
				parent.modifyResourceSelectIds = '';
				parent.modifyResourceSelectLvs = '';
				parent.modifyNeName = '';
				for ( var i = 0; i < rightRecords.length; i++) {
					parent.modifyResourceSelectIds += rightRecords[i].get('ID')+',';
					parent.modifyResourceSelectLvs += rightRecords[i].get('LV')+',';
					if (rightRecords[i].get('LV') == 4) {
						// 网元名称
						parent.modifyNeName += rightRecords[i].get('NE_NAME')+',';
					} else {
						// 网管名称
						parent.modifyNeName += rightRecords[i].get('EMS_NAME')+',';
					}
				}
				// 去掉最后一个,号
				parent.modifyNeName = parent.modifyNeName.substring(0,parent.modifyNeName.lastIndexOf(','));
				parent.modifyResourceSelectIds = parent.modifyResourceSelectIds.substring(0,parent.modifyResourceSelectIds.lastIndexOf(','));
				parent.modifyResourceSelectLvs = parent.modifyResourceSelectLvs.substring(0,parent.modifyResourceSelectLvs.lastIndexOf(','));
			}else{
				// 右侧已有的设备
				var rightRecords = gridPanel.getStore().getRange();
				// 先清空原来的，否则会累加
				parent.resourceSelectIds = '';
				parent.resourceSelectLvs = '';
				parent.neName = '';
				for ( var i = 0; i < rightRecords.length; i++) {
					parent.resourceSelectIds += rightRecords[i].get('ID')+',';
					parent.resourceSelectLvs += rightRecords[i].get('LV')+',';
					if (rightRecords[i].get('LV') == 4) {
						// 网元名称
						parent.neName += rightRecords[i].get('NE_NAME')+',';
					} else {
						// 网管名称
						parent.neName += rightRecords[i].get('EMS_NAME')+',';
					}
				}
				// 去掉最后一个,号
				parent.neName = parent.neName.substring(0,parent.neName.lastIndexOf(','));
				parent.resourceSelectIds = parent.resourceSelectIds.substring(0,parent.resourceSelectIds.lastIndexOf(','));
				parent.resourceSelectLvs = parent.resourceSelectLvs.substring(0,parent.resourceSelectLvs.lastIndexOf(','));
				parent.frames["addAlarmShield_panel"].Ext.getCmp('alarmResource').setValue(parent.neName);
			}
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('alarmShieldResourceWindow');
			if(win){
				win.close();
			}
		}
	},{
		text:'重置',
		handler : function(){
			// 清除右侧已选的设备
			var rightRecords = gridPanel.getStore().removeAll();
			if(type=="modify"){
				if(parent.modifyResourceSelectIds!=''){
					var nodeIdArr = parent.modifyResourceSelectIds.split(',');
					var nodeLvArr = parent.modifyResourceSelectLvs.split(',');
					for ( var i = 0; i < nodeIdArr.length; i++) {
						rightMove(nodeIdArr[i],nodeLvArr[i]);
					}
				}
			}else{
				if(parent.resourceSelectIds!=''){
					var nodeIdArr = parent.resourceSelectIds.split(',');
					var nodeLvArr = parent.resourceSelectLvs.split(',');
					for ( var i = 0; i < nodeIdArr.length; i++) {
						rightMove(nodeIdArr[i],nodeLvArr[i]);
					}
				}
			}
		}
	},{
		text:'取消',
		handler : function(){
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('alarmShieldResourceWindow');
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
	if(type=="modify"){
		if(parent.modifyShieldFlag!=shieldId){
			parent.modifyShieldFlag = shieldId;
			Ext.Ajax.request({
			    url: 'fault!getAlarmShieldDetailById.action',
			    method: 'POST',
			    params: {'jsonString':Ext.encode({'shieldId' : shieldId,'flag': 'second'})},
			    success : function(response) {
			    	var obj = Ext.decode(response.responseText).resource;
			    	for ( var i = 0; i < obj.length; i++) {
			    		parent.modifyResourceSelectIds += obj[i].DEVICE_ID + ',';
			    		parent.modifyResourceSelectLvs += obj[i].DEVICE_TYPE + ',';
			    		rightMove(obj[i].DEVICE_ID,obj[i].DEVICE_TYPE);
					}
			    	// 去掉最后一个,号
					parent.modifyResourceSelectIds = parent.modifyResourceSelectIds.substring(0,parent.modifyResourceSelectIds.lastIndexOf(','));
					parent.modifyResourceSelectLvs = parent.modifyResourceSelectLvs.substring(0,parent.modifyResourceSelectLvs.lastIndexOf(','));
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
			if(parent.modifyResourceSelectIds!=''){
				var nodeIdArr = parent.modifyResourceSelectIds.split(',');
				var nodeLvArr = parent.modifyResourceSelectLvs.split(',');
				for ( var i = 0; i < nodeIdArr.length; i++) {
					rightMove(nodeIdArr[i],nodeLvArr[i]);
				}
			}
		}
	}else{
		if(parent.resourceSelectIds!=''){
			var nodeIdArr = parent.resourceSelectIds.split(',');
			var nodeLvArr = parent.resourceSelectLvs.split(',');
			for ( var i = 0; i < nodeIdArr.length; i++) {
				rightMove(nodeIdArr[i],nodeLvArr[i]);
			}
		}
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
