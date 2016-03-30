var coverData = [ [ '1', '架空' ], [ '2', '地埋' ], [ '3', '混合' ] ];
var coverStore = new Ext.data.ArrayStore({
	fields : [ {name : 'value'}, {name : 'displayField'}]
});
coverStore.loadData(coverData);

function coverRenderer(v, m, r) {
	return (typeof v == 'number' && coverData[v - 1] != null) ? coverData[v - 1][1] : null;
}

var fiberModelData = [ [ '1', 'G.652' ], [ '2', 'G.655' ], [ '3', 'G.653' ],
                       [ '4', 'G.654' ],[ '5', 'G.656' ], [ '6', 'G.657' ] ];
var fiberModelStore = new Ext.data.ArrayStore({
	fields : [{name : 'value'}, {name : 'displayField'}]
});
fiberModelStore.loadData(fiberModelData);

function fiberRenderer(v, m, r) { 
	return (fiberModelData[v - 1] != null) ? fiberModelData[v - 1][1]: null;
}

var formPanel = new Ext.FormPanel({
	id : "formPanel",
	region : "north",
	frame : false,
	border : false,
	bodyStyle : 'padding:10px 10px 10px 10px',
	height : 100,
	labelWidth : 80,
	labelAlign : 'right',
	collapsed : false,  
	items : [ {
		layout : 'column',
		border : false,
		items : [{
			layout : 'form',
			labelSeparator : "：", 
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'CablesName', 
				fieldLabel : '光缆名称',  
				width : 120
			}, {
				xtype : 'combo',
				id : 'CableFiberType', 
				fieldLabel : '光纤型号',
				store : fiberModelStore,
				triggerAction : 'all',
				valueField : 'value',
				displayField : 'displayField',
				mode : 'local', 
				width : 120
			}]
		},{
			layout : 'form',
			labelSeparator : "：", 
			border : false,
			items : [{
				xtype : 'textfield',
				id : 'CablesNo', 
				fieldLabel : '光缆代号',  
				width : 120
			}, {
				xtype : 'combo',
				id : 'CableCover', 
				fieldLabel : '敷设方式',
				triggerAction : 'all',
				store : coverStore,
				valueField : 'value',
				displayField : 'displayField',
				mode : 'local', 
				editable : false,
				width : 120
			} ]
		}, {
			layout : 'form',
			labelSeparator : "：", 
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'CableName', 
				fieldLabel : '光缆段名称', 
				width : 120
			},{
				xtype : 'areaselector',
				id : 'aStationSelector',
				privilege : viewAuth,
				width : 120,
				readOnly : true,
				fieldLabel : '起始'+top.FieldNameDefine.STATION_NAME,
				targetLevel : 11
			} ]
		}, {
			layout : 'form', 
			labelSeparator : "：", 
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'CableNo', 
				fieldLabel : '光缆段代号', 
				width : 120
			}, {
				xtype : 'areaselector',
				id : 'zStationSelector',
				privilege : viewAuth,
				width : 120,
				readOnly : true,
				fieldLabel : '终点'+top.FieldNameDefine.STATION_NAME,
				targetLevel : 11
			} ]
		}, {
			layout : 'form',
			labelSeparator : "：", 
			border : false,
			items : [ {
				xtype : 'textfield',
				id : 'CableType', 
				fieldLabel : '光缆段型号', 
				width : 120
			}, { 
				layout : 'column',
				border : false,
				forceFit : false,
				items : [  {
					xtype : 'label',
					columnWidth : .05,
					text : '　　'
				},{
					xtype : 'button',
					text : '重置',
					columnWidth : .35,
					width : 60,
					privilege : viewAuth,
					handler : function() {
						formPanel.getForm().reset();
					}
				}, {
					xtype : 'label',
					columnWidth : .05,
					text : '　　'
				}, {
					xtype : 'button',
					text : '查询',
					columnWidth : .35,
					width : 60,
					privilege : viewAuth,
					handler : searchCable
				} ] 
			} ]
		}]
	} ],
	bbar : [ '-', {
		text : '新增',
		icon : '../../../resource/images/btnImages/add.png', 
		privilege : addAuth,
		handler : function (){
			modifyCable(addAuth);
		}
	}, {
		text : '删除',
		icon : '../../../resource/images/btnImages/delete.png', 
		privilege : delAuth,
		handler : deleteCable
	}, {
		text : '修改',
		icon : '../../../resource/images/btnImages/modify.png', 
		privilege : modAuth,
		handler : function (){
			modifyCable(modAuth);
		}
	}, '-',{
		text : '查询光纤',
		icon : '../../../resource/images/btnImages/search.png', 
		privilege : viewAuth,
		handler : searchCableFiber
	}, '-',{
		text : '相关链路',
		icon : '../../../resource/images/btnImages/search.png', 
		privilege : viewAuth,
		handler : relateLinks
	}]
}); 

var store = new Ext.data.Store({
	url : 'resource-cable!getCableList.action', 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "RESOURCE_CABLE_ID","CABLE_NAME", "CABLE_NO", "CABLE_LENGTH", "CABLE_TYPE", "CABLE_FIBER_TYPE",
		"CABLE_FIBER_COUNT", "CABLE_COVER", "A_END", "A_END_STATION_NAME","ATT_COEFFICIENT_EXPERIENCE",
		"ATT_COEFFICIENT_THEORY","Z_END", "Z_END_STATION_NAME", "buildTime","NOTE","CABLES" ])
}); 

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	header : ''
});
var cm = new Ext.grid.ColumnModel({ 
	defaults : {
		sortable : true 
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'cableName_',
		header : '光缆段名称',
		dataIndex : 'CABLE_NAME' 
	}, {
		id : 'cableNo_',
		header : '光缆段代号',
		dataIndex : 'CABLE_NO' 
	}, {
		id : 'cableLength_',
		header : '长度(KM)',
		width:80,
		dataIndex : 'CABLE_LENGTH' 
	}, {
		id : 'cables_',
		header : '所属光缆',
		dataIndex : 'CABLES' 
	},{
		id : 'cableType_',
		header : '光缆段型号',
		width:80,
		dataIndex : 'CABLE_TYPE' 
	}, {
		id : 'cableFiberType_',
		header : '光纤型号',
		width:80,
		dataIndex : 'CABLE_FIBER_TYPE' ,
		renderer :  function(v,m,r){ 
			return fiberRenderer(parseInt(v),m,r);
		} 
	}, {
		id : 'cableFiberCount_',
		header : '芯数',
		width:80,
		dataIndex : 'CABLE_FIBER_COUNT' 
	}, {
		id : 'cableCover_',
		header : '敷设方式',
		dataIndex : 'CABLE_COVER', 
		width:80,
		renderer :  function(v,m,r){ 
			return coverRenderer(parseInt(v),m,r);
		}
	}, {
		id : 'AEndStationName_',
		header : '起始'+top.FieldNameDefine.STATION_NAME,
		dataIndex : 'A_END_STATION_NAME' 
	}, {
		id : 'ZEndStationName_',
		header : '终点'+top.FieldNameDefine.STATION_NAME,
		dataIndex : 'Z_END_STATION_NAME' 
	}, {
		id : 'buildTime_',
		header : '开通时间',
		dataIndex : 'buildTime' 
	}, {
		id : 'attExperience_',
		header : '衰耗系数经验值',
		dataIndex : 'ATT_COEFFICIENT_EXPERIENCE' 
	}, {
		id : 'attTheory_',
		header : '衰耗系数理论值',
		dataIndex : 'ATT_COEFFICIENT_THEORY' 
	}, {
		id : 'note_',
		header : '备注',
		dataIndex : 'NOTE' 
	}]
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
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	frame : false,
	stripeRows : true, // 交替行效果
	collapsible : false,
	forceFit : false, 
	loadMask : {
		msg : '数据加载中...'
	},
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	bbar : pageTool
});

function searchCable() {  
	store.baseParams = {
		"limit" : 200, 
		"name" : Ext.getCmp('CablesName').getValue(),
		"no" : Ext.getCmp('CablesNo').getValue(), 
		"cName" : Ext.getCmp('CableName').getValue(),
		"cNo" : Ext.getCmp('CableNo').getValue(),
		"comboCover" : Ext.getCmp('CableCover').getValue(),
		"comboType" : Ext.getCmp('CableFiberType').getValue(),
		"cableType" : Ext.getCmp('CableType').getValue(),
		"aStationId" : Ext.getCmp('aStationSelector').getRawValue().id,
		"zStationId" : Ext.getCmp('zStationSelector').getRawValue().id
	};
	store.load({
		callback : function(r, o, s) {
			if (s) {
			} else {
				Ext.Msg.alert("提示", "查询光缆段失败！");
			}
		}
	});
}

function modifyCable(auth) {
	var isAdd = (auth==addAuth);
	var cell = gridPanel.getSelectionModel().getSelections();
	if(!isAdd && cell.length<1){
		Ext.Msg.alert('提示', '请选择需要修改的光缆段！');
		return;
	} 
	var url = "editCableInfo.jsp?cablesId="+cablesId+"&cableId="+(isAdd?0:cell[0].get("RESOURCE_CABLE_ID"));
	cableWin = new Ext.Window({
		id : 'cableWin',
		title : isAdd?'新增光缆段':'修改光缆段',
		width : 520,
		height : 450,
		isTopContainer : true,
		modal : true,
		plain : true, // 是否为透明背景
		html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
	});
	cableWin.show();

	// 调节高度
	if (cableWin.getHeight() > Ext.getCmp('win').getHeight()) {
		cableWin.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		cableWin.setHeight(cableWin.getInnerHeight());
	}
	// 调节宽度
	if (cableWin.getWidth() > Ext.getCmp('win').getWidth()) {
		cableWin.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		cableWin.setWidth(cableWin.getInnerWidth());
	}
	cableWin.center(); 
}

function deleteCable() {
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else { 
		Ext.Msg.confirm("提示", "确认删除:"+gridPanel.getSelectionModel().getSelected().get("CABLE_NAME"), function(btn) {
			if (btn == "yes") {
				// 确认删除
				Ext.Ajax.request({
					url : 'resource-cable!deleteCable.action',
					params : {
						cellId : gridPanel.getSelectionModel().getSelected().get("RESOURCE_CABLE_ID")
					},
					success : function(response) { 
						var obj = Ext.decode(response.responseText); 
						if (obj.returnResult == 1) {  
							Ext.Msg.alert("提示",obj.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});  
						} 
						if (obj.returnResult == 0) {  
							Ext.Msg.alert('提示', obj.returnMessage);
						}
					},
					error:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    }
				});
			}
		});
	}
} 

function searchCableFiber() {
	var cell = gridPanel.getSelectionModel().getSelected();
	if (!!cell) {
		var cableId = cell.get("RESOURCE_CABLE_ID");
		var cable= cell.get("CABLE_NAME")+(cell.get("CABLE_NO")==null?"":('('+cell.get("CABLE_NO")+')'));
		var cables = cell.get("CABLES");  
		var param = {
			"cableId" : cableId,
			"cable" : cable,
			"cables" : cables,
			"authSequence":authSequence
		}; 
		var url = "fiberList.jsp?" + Ext.urlEncode(param);
		var fiberListWindow = new Ext.Window({
			id : 'fiberListWindow',
			title : '光纤信息',
			width : 1200,
			height : 500,
			isTopContainer : true,
			modal : true,
			plain : true, // 是否为透明背景
			html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
		});
		fiberListWindow.show();

		// 调节高度
		if (fiberListWindow.getHeight() > Ext.getCmp('win').getHeight()) {
			fiberListWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
		} else {
			fiberListWindow.setHeight(fiberListWindow.getInnerHeight());
		}
		// 调节宽度
		if (fiberListWindow.getWidth() > Ext.getCmp('win').getWidth()) {
			fiberListWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
		} else {
			fiberListWindow.setWidth(fiberListWindow.getInnerWidth());
		}
		fiberListWindow.center();

	} else {
		Ext.Msg.alert('信息', '请选择一条数据！');
	}
}

function relateLinks() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 1) { 
		var url = "../resourceManager/cable/relateLinks.jsp?cableId="+cell[0].get("RESOURCE_CABLE_ID"); 
		parent.addTabPage(url, "相关链路", authSequence); 
	} else {
		Ext.Msg.alert('信息', '请选择光缆段！每次只能选择一个！');
	}
}

Ext.onReady(function() {
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};

	var win = new Ext.Viewport({
		id : 'win',
		loadMask : true,
		layout : 'border',
		items : [ formPanel, gridPanel ],
		renderTo : Ext.getBody()
	});   
	
	if(!!cablesId){
		Ext.getCmp('CablesName').setValue(cableName);
		Ext.getCmp('CablesNo').setValue(cableNo); 
		store.baseParams = {
			"limit" : 200, 
			"cellId" : cablesId
		};
	}
	store.load();
});