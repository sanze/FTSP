/*	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );*/

/**
 * 创建网管分组下拉框
 */
var factoryStore=new Ext.data.ArrayStore({
	fields : [ {name:'value',mapping:'key'}, {name:'displayName',mapping:'value'} ],
	data:[{key:-99,value:'全部'}]
});
factoryStore.loadData(FACTORY,true);
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '厂家',
	store : factoryStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	editable : false,
	triggerAction : 'all',
	width :150,
	listeners : {
		select : function(combo, record, index) {
			store.baseParams = {'jsonString':Ext.encode({'factoryId':record.get('value')}),
				'limit':500
			},
			store.load({
				callback : function(records,options,success){
					if (!success) {
						Ext.Msg.alert('错误', '查询失败！请重新查询');
					}
				}
			});
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
		header : 'id',
		dataIndex : 'ID',
		width : 200,	
		hidden : true
	},{
		header : '厂家',
		dataIndex : 'FACTORY_ID',
		width : 100,
		renderer : function(v){
			for(var fac in FACTORY){
	    		if(v==FACTORY[fac]['key']){
		    		return FACTORY[fac]['value'];
		    	}
	    	}
			return v;
		}
		
	},{
		header : '厂家告警名称',
		dataIndex : 'NATIVE_PROBABLE_CAUSE',
		width : 100
	}, {
		header : '标准告警名称',
		dataIndex : 'NORM_PROBABLE_CAUSE',
		width : 100
	}, {
		header : '归一化告警名称',
		dataIndex : 'REDEFINE_PROBABLE_CAUSE',
		width : 100
	}, {
		header : '修改时间',
		dataIndex : 'UPDATE_TIME',
		width : 200
	}]
});


/**
 * 创建表格数据源
 */
var store = new Ext.data.Store({
	url : 'fault!getAlarmNormlizedByEmsGroup.action',
	baseParams : {'jsonString':Ext.encode({'factoryId':'-99'}),'limit':500},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	["ID","FACTORY_ID","NATIVE_PROBABLE_CAUSE","NORM_PROBABLE_CAUSE","REDEFINE_PROBABLE_CAUSE","UPDATE_TIME"])
});

/**
 * 创建表格工具栏
 */
var pageTool = new Ext.PagingToolbar({
	pageSize : 500,
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

/**
 * 加载表格数据源
 */
store.load({
	callback : function(records,options,success){
		if (!success) {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

/**
 * 创建表格实例
 */
var gridPanel = new Ext.grid.EditorGridPanel({
//	title :'告警归一化设置',
	region : 'center',
	store : store,
	loadMask : true,
	cm : cm,
	selModel : sm, 
	animCollapse : false,
	frame : false,
	stripeRows : true, 
	clicksToEdit : 1,
	/*stateId:'alarmNormlizedId',  
	stateful:true,*/
	view : new Ext.ux.grid.LockingGridView(),
	tbar : ['-','厂家：',emsGroupCombo,'-',{
		privilege:addAuth,
        text: '新增',
        icon : '../../resource/images/btnImages/add.png',
        handler : function(){
        	addAlarmNormlized();
        }
    },{
    	privilege:delAuth,
        text: '删除',
        icon : '../../resource/images/btnImages/delete.png',
        handler : function(){
        	deleteAlarmNormlized();
        }
    },{
    	privilege:modAuth,
        text: '修改',
        icon : '../../resource/images/btnImages/modify.png',
        handler : function(){
        	modifyAlarmNormlized();
        }
    }],
    bbar : pageTool
});

/**
 * 新增告警重定义
 */
function addAlarmNormlized(){
	var url = 'addAlarmNormlized.jsp?type=add';
	var addAlarmNormlizedWindow = new Ext.Window({
		id : 'addAlarmNormlizedWindow',
		title : '新增告警归一化规则',
		width : 840,
		height : 200,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='addAlarmNormlized_panel' name = 'addAlarmNormlized_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addAlarmNormlizedWindow.show();
}

/**
 * 删除告警重定义
 */
function deleteAlarmNormlized(){
	// 需要删除的记录
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '选择模板');
	}else{
		Ext.Msg.confirm('系统提示','删除模板后，无法恢复。是否确认？',function(btn){       
			if(btn=='yes'){
				var redefineIds = '';
				var count = 0;
				for ( var i = 0; i < records.length; i++) {
					redefineIds += records[i].get('ID') + ',';
						count++;
				}
				redefineIds = redefineIds.substring(0, redefineIds.lastIndexOf(','));
				if(count>0){
					Ext.Ajax.request({
					    url: 'fault!deleteAlarmNormlized.action',
					    method: 'POST',
					    params: {'jsonString':Ext.encode({'redefineIds' : redefineIds})},
					    success : function(response) {
					    	store.load({
					    		callback : function(records,options,success){
									if (!success) {
										Ext.Msg.alert('错误', '查询失败！请重新查询');
									}
								}
					    	});
						},
						error : function(response) {
							top.Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						},
						failure : function(response) {
							top.Ext.getBody().unmask();
							Ext.Msg.alert("错误", response.responseText);
						}
					})	
				}else{
					Ext.Msg.alert('提示', '模板在启用状态，请先禁用');
				}
			}              
		},this);
	}
}

/**
 * 修改告警重定义
 */
function modifyAlarmNormlized(){
	var records = gridPanel.getSelectionModel().getSelections();
	if(records.length<1){
		Ext.Msg.alert('提示', '请选择模板');
	}else if(records.length==1){
		// 重定义模板ID
		var redefineId = records[0].get('ID');
		var url = 'addAlarmNormlized.jsp?type=modify&redefineId='+redefineId;
		var addAlarmNormlizedWindow = new Ext.Window({
			id : 'addAlarmNormlizedWindow',
			title : '修改告警归一化规则',
			width : 820,
			height : 200,
			isTopContainer : true,
			modal : true,
			autoScroll : true,
			html : "<iframe  id='addAlarmNormlized_panel' name = 'addAlarmNormlized_panel'  src = " + url + " height='100%' width='100%' frameBorder=0 border=0/>"
		});
		addAlarmNormlizedWindow.show();
	}else{
		Ext.Msg.alert('提示', '请勿多选');
	}
}


/**
 * 创建主体部分
 */
//var centerPanel = new Ext.Panel({
//	id : 'centerPanel',
//	region : 'center',
//	autoScroll : true,
//	layout : 'border',
//	items : gridPanel
//});

/**
 * 初始化EXT
 */
Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	new Ext.Viewport({
		layout : 'border',
		items : gridPanel
	});
});