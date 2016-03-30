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
	view : new Ext.ux.grid.LockingGridView()
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
 * 创建border布局的south部分
 */
var southPanel = new Ext.Panel({
	region : 'south',
	buttons:[{
		text:'取消',
		handler : function(){
			// 关闭告警源选择窗口
			var win = parent.Ext.getCmp('viewAlarmShieldResourceWindow');
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
	Ext.Ajax.request({
	    url: 'fault!getAlarmShieldDetailById.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'shieldId' : shieldId,'flag': 'second'})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText).resource;
	    	for ( var i = 0; i < obj.length; i++) {
	    		rightMove(obj[i].DEVICE_ID,obj[i].DEVICE_TYPE);
			}
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
