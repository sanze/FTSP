var unitInfoField = {
		xtype:'textfield',
		fieldLabel:"单位",
		id:"unitInfoField",
		anchor:'90%',
		sideText:'<font color=red>*</font>',
		allowBlank:false
};

var networkInfo = {
		xtype:'textfield',
		fieldLabel:"网络名称",
		id:"networkInfo",
		anchor:'90%',
		sideText:'<font color=red>*</font>',
		allowBlank:false
};

var waveCountInfo = {
		id:"waveCountInfo",
		xtype:'numberfield', 
		fieldLabel:"容量(波数)",
		minValue:1,
		decimalPrecision:0,
		id:"waveCountInfo",
		anchor:'90%',
		sideText:'<font color=red>*</font>',
		allowBlank:false
};

var waveCountActInfo = {
		id:"waveCountActInfo",
		xtype:'numberfield',
		fieldLabel:"实开(波数)",
		minValue:1,
		anchor:'90%',
		decimalPrecision:0,
		sideText:'<font color=red>*</font>',
		allowBlank:false
};

var factory = {};
var product = {};
var factoryStore = new Ext.data.ArrayStore({
	fields :['factoryId','factoryName'],
	data : [[1,'华为'],[2,'中兴'],[5,'贝尔']	]
});
var productStore = new Ext.data.Store({
	url :'nx-report!getProductNameByFactoryIdNoSDH.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : 'rows'
	},['productName']),
	baseParams :{factoryId : 1}
});
var PTP_ComBox;
var ptpStore;
(function(){
	ptpStore = new Ext.data.Store({
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		},['BASE_PTP_ID','DISPLAY_NAME']),
		url : 'nx-report!getPortByUnitId.action'
	});
	PTP_ComBox = new Ext.form.ComboBox({
		id : 'PTP_ComBox',
		triggerAction : 'all',
		editable : false,
		selectOnFocus:false,
		typeAheadDelay:500,
		store : ptpStore,
		displayField : "DISPLAY_NAME",
		valueField : 'BASE_PTP_ID',	
		typeAhead:true,
		resizable: true,
		listeners: {
	        beforequery: function(qe){
	        	var selections = selectedUnitGrid.getSelectionModel().getSelections();
            	if(selections.length>0){
            		var unitId  = selections[0].get('BASE_UNIT_ID');
            			delete qe.combo.lastQuery;
            			ptpStore.baseParams = {unitId :  unitId};
            	}
	        }
	    }
	});
})();

(function(){
	factory = new Ext.form.ComboBox({
			id:'factory',
			fieldLabel:'设备厂家',
			sideText:'<font color=red>*</font>',
			allowBlank:false,
			value : 1,
			triggerAction : 'all',
			editable : false,
			mode:'local',
			width :100,
			store : factoryStore,
			displayField : "factoryName",
			valueField : 'factoryId',
			listeners :{
				select : function(combo,record,index){
					Ext.getCmp('product').setValue("");
					productStore.baseParams = {
							factoryId : record.get('factoryId')
					};
					productStore.load();
					// 删除勾选的板卡记录，并刷新树
					selectedUnitStore.removeAll();
					selectedUnitGrid.getView().refresh();
					tree.update('<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
							+ '" height="100%" width="100%" frameBorder=0 border=0 />', true);
				}
			}
	});
	product = new Ext.form.ComboBox({
			triggerAction : 'all',
			editable : false,
			store : productStore,
			displayField : "productName",
			valueField : 'productName',
			id:'product',
			width :100,
			fieldLabel:'设备型号',
			listWidth:160,
			sideText:'<font color=red>*</font>',
			allowBlank:false,
			listeners : {
				select : function(combo,record,index){
					// 删除勾选的板卡记录，并刷新树
					selectedUnitStore.removeAll();
					selectedUnitGrid.getView().refresh();
					tree.update('<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
							+ '" height="100%" width="100%" frameBorder=0 border=0 />', true);
				}
			}
	});
})();
var infoPanel = new Ext.form.FormPanel({
	id:'infoPanel',
	region:'north',
	border:false,
	height:100,
	bodyStyle:'padding:10px 30px 10px 30px',
	title:'基本信息',
	labelWidth:70,
	items:[{
		layout:'column',
		width:1080,
		border:false,
		items:[{
			columnWidth : 0.45,
			border:false,
			layout:'form',
			items:[unitInfoField,
			       {border :false,
					layout : 'column',
					items:[{
						columnWidth : 0.45,
						border:false,
						layout:'form',
						items:[factory]
					},{columnWidth : 0.45,
						border:false,
						layout:'form',
						items:[product]
					}]}]
		},{
			columnWidth : 0.45,
			layout:'form',
			border:false,
			items:[networkInfo, {border :false,
				layout : 'column',
				items:[{
					columnWidth : 0.4,
					border:false,
					layout:'form',
					items:[waveCountInfo]
				},{columnWidth : 0.4,
					border:false,
					layout:'form',
					items:[waveCountActInfo]
				}]}]
		}]
	}]
});

// %%%%%%%%%%%%%%%%%%%%%%%%%%%板卡信息%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

var tree = {};
var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		checkModel : "multiple",
		containerId : "westPanel",
		leafType : 6
	};
var treeurl = "../../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
(function(){
	
// function treeFilter(tree, parent, node) {
// if (node.attributes["nodeLevel"] == NodeDefine.NE &&
// node.attributes["additionalInfo"]
// && node.attributes["additionalInfo"]["TYPE"] == NodeDefine.TYPE) {
// return false;
// }
// }
		tree = new Ext.Panel({
			id : "westPanel",
			region : "west",
			width : 280,
			autoScroll : true,
			boxMinWidth : 230,
			boxMinHeight : 260,
			forceFit : true,
			collapsed : false, // initially collapse the group
			collapsible : false,
			collapseMode : 'mini',
			split : true,
			filterBy : function(tree, parent, node){
					if(node.attributes["nodeLevel"]==CommonDefine.TREE.NODE.NE&&
					node.attributes["additionalInfo"]&&
				    node.attributes["additionalInfo"]["PRODUCT_NAME"]!=product.getValue()){
					return false;
					}
			},
			html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
					+ '" height="100%" width="100%" frameBorder=0 border=0 />'
		});
})();


(function(){
	var store = new Ext.data.ArrayStore({
		fields : [ {name:'id',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:[]
	});
	store.loadData(PROMODE,true);
	proMode = new Ext.form.ComboBox({
		id : 'proMode',
		triggerAction : 'all',
		editable : false,
		mode:'local',
		store : store,
		displayField : "displayName",
		valueField : 'id',	
		resizable: true
	});
})();
var selectedUnitStore = new Ext.data.Store({
	url : 'nx-report!getUnitInfoByManageId.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ['STATION','EMSGROUP_DISPLAY_NAME','EMS_DISPLAY_NAME','SUBNET_DISPLAY_NAME','NE_DISPLAY_NAME',
	    'BASE_UNIT_ID','unit','unitDesc','RESOURCE_UNIT_MANAGE_ID','ACTUAL_WAVE_NUM','INSERTION_LOSS',
	    'r_PTP_ID','r_PTP_NAME'])
});

var selectedUnitSM = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});

var selectedUnitCM = new Ext.grid.ColumnModel({
	defaults : {sortable : false},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked:true
	}), selectedUnitSM,{
		id : 'STATION',
		header : '站点',
		dataIndex : 'STATION',
		width : 100
	}, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'EMSGROUP_DISPLAY_NAME',
		width : 100
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'EMS_DISPLAY_NAME',
		width : 100
	}, {
		id : 'subNet',
		header : '子网',
		dataIndex : 'SUBNET_DISPLAY_NAME',
		width : 100
	}, {
		id : 'ne',
		header : '网元名称',
		dataIndex : 'NE_DISPLAY_NAME',
		width : 100
	}, {
		id : 'unitDesc',
		header : '槽道号',
		dataIndex : 'unitDesc',
		width : 100
	},  {
		id : 'unit',
		header : '板卡名称',
		dataIndex : 'unit',
		width : 100
	}, {
		id : 'sPort',
		header : '<span style="font-weight:bold">收光端口</span>',
		dataIndex : 'r_PTP_ID',
		width : 100,
		editor : PTP_ComBox,
		renderer : function(value,cellmeta,record){
			var name ;
			ptpStore.each(function(r){
				if(r.get('BASE_PTP_ID') == value){
					name = r.get('DISPLAY_NAME');
				}
			});
			if(typeof name !="undefined"){
				record.set('r_PTP_NAME',name);
			}
 			return record.data['r_PTP_NAME'];
		}
	}, {
		id : 'loss',
		header : '<span style="font-weight:bold">插损</span>',
		dataIndex : 'INSERTION_LOSS',
		width : 100,
		editor :new Ext.form.NumberField()
	}, {
		id : 'actualNum',
		header : '<span style="font-weight:bold">在用波数</span>',
		dataIndex : 'ACTUAL_WAVE_NUM',
		width : 100,
		editor :new Ext.form.NumberField({allowNegative:false})
	}]
});
var dupMark = false;
var selectedUnitGrid = new Ext.grid.EditorGridPanel({
	id : 'selectedUnitGrid',
	store : selectedUnitStore,
	cm : selectedUnitCM,
	boxMinWidth:440,
	height:900,
	loadMask : true,
	selModel : selectedUnitSM, 
	stripeRows : true ,
	tbar:['-',{
		text:"上移",
		icon:"../../../../resource/images/btnImages/up.png",
		handler:function(){
			upForward(selectedUnitGrid, selectedUnitStore);
		}
	},'-',{
		text:"下移",
		icon:"../../../../resource/images/btnImages/down.png",
		handler:function(){
			downForward(selectedUnitGrid, selectedUnitStore);
		}
	}],
	listeners:{
		afteredit: function(o){
			o.grid.getView().refresh();
			dupMark = false;
			if(o.field=='waveLength'){
				selectedUnitStore.each(function(r){
					if(selectedUnitStore.indexOf(r)!=o.row&&r.get('waveLength')==o.value){
						o.record.set("waveLength",o.originalValue );
						Ext.Msg.alert("提示","波道/波长配置数据重复！");
						dupMark = true;
						return false;
					}
				});
			}
			return true;
		}
	}
});
// ------------------------- 已选板卡 -----------------------------

var unitInfo = new Ext.Panel({
	id:"unitInfo",
	title:'板卡选择',
	region:"center",
	layout: 'column',
	border:false,
	bodyStyle:'padding:15px 0px 10px 30px',
	defaults: {
		height:260,
		width:450
    },
	items:[tree,{
    	width:50,
    	border:false,
    	layout: {
            type: 'vbox',
            pack: 'start',  // 纵向对齐方式 start：从顶部；center：从中部；end：从底部
            align: 'center'  // 对齐方式
								// center、left、right：居中、左对齐、右对齐；stretch：延伸；stretchmax：以最大的元素为标准延伸
        },
        defaults: {
            xtype: 'button'
        },
        items: [{
            xtype: 'tbspacer',          // 插入的空填充
            flex: 1
        },{
            text: ">>",
            height:10,
            width: 40,
            flex: 1,                      // 表示当前子元素尺寸所占的均分的份数。
            handler:function(){
            	getCheckNode();
            }
        },{
            xtype: 'tbspacer',          // 插入的空填充
            flex: 2
        },{
            text: "<<<",
            height:10,
            width: 40,
            flex: 1,                      // 表示当前子元素尺寸所占的均分的份数。
            handler:function(){
            	var selections = selectedUnitGrid.getSelectionModel().getSelections();
            	if(selections.length>0){
	            	selectedUnitStore.remove(selections);
	            	selectedUnitGrid.getView().refresh();
            	}
            }
        },{
            xtype: 'tbspacer',          // 插入的空填充
            flex: 1
        }]
    },selectedUnitGrid]
});

/**
 * 上移
 * 
 * @param {}
 *            forwardUpPanel panel
 * @param {}
 *            forwardUpStore store
 */
function upForward(forwardUpPanel, forwardUpStore) {
	var records = forwardUpPanel.getSelectionModel().getSelections();
	if (!!records&&records.length>1) {
		Ext.Msg.alert("提示", "只能选择一条记录！");
		return;
	}
	var record = records[0];
	var index = forwardUpStore.indexOf(record);
	if (index == 0) {
		return;
	}
	forwardUpStore.remove(record);
	forwardUpStore.insert(index - 1, record);
	forwardUpPanel.getView().refresh();
	forwardUpPanel.getSelectionModel().selectRow(index - 1);
}

/**
 * 下移
 * 
 * @param {}
 *            forwardUpPanel
 * @param {}
 *            forwardUpStore
 */
function downForward(forwardUpPanel, forwardUpStore) {
	var records = forwardUpPanel.getSelectionModel().getSelections();
	if (!!records&&records.length>1) {
		Ext.Msg.alert("提示", "只能选择一条记录！");
		return;
	}
	var record = records[0];
	var index = forwardUpStore.indexOf(record);
	if (index == forwardUpStore.getCount() - 1) {
		return;
	}
	forwardUpStore.remove(record);
	forwardUpStore.insert(index + 1, record);
	forwardUpPanel.getView().refresh();
	forwardUpPanel.getSelectionModel().selectRow(index + 1);
}
// ==========================板卡信息===================================

var all = new Ext.Panel({
	id:'all',
	border:false,
	layout:"border",
	boxMinHeight:420,
	boxMinWidth:1000,
	defaults: {
		width:900
    },
	items:[infoPanel,unitInfo]
});

// **************************FUNCTIONS**************************
/**
 * 验证需要提交的数据是否合法
 */
function saveValid(){
	var info = infoPanel.getForm().getValues();
	 if(!infoPanel.getForm().isValid()) {
			Ext.Msg.alert("提示","有必填项没有填写！");
			return false;
	}else if(Ext.getCmp('waveCountInfo').getValue()<Ext.getCmp('waveCountActInfo').getValue()){
			Ext.Msg.alert("提示","实开（波数）不能大于容量（波数）！");
			return false;
	}return true;
}
/**
 * 保存对unit的添加或更新
 */
function okBtnFunction(){
	 if(!saveValid()) {
			return;
	}
	 // type从jsp中获取
	if(type ==1){
		saveCreate();// 新增
	}else if(type == 2){
		saveModify();// 修改
	}else{
		Ext.Msg.alert('提示','缺少参数');
	}
}
/**
 * 应用
 */
function updateParentInfo(){
	parent.RECORD_FOR_EDIT.set('DEPARTMENT',Ext.getCmp('unitInfoField').getValue());	
	parent.RECORD_FOR_EDIT.set('NET_WORK_NAME',Ext.getCmp('networkInfo').getValue());
	parent.RECORD_FOR_EDIT.set('FACTORY',Ext.getCmp('factory').getValue());
	parent.RECORD_FOR_EDIT.set('PRODUCT_NAME',Ext.getCmp('product').getValue());
	parent.RECORD_FOR_EDIT.set('ACTUAL_WAVE_NUM',Ext.getCmp('waveCountActInfo').getValue());
	parent.RECORD_FOR_EDIT.set('STD_WAVE_NUM',Ext.getCmp('waveCountInfo').getValue());
}
function applyFunction(){
	if(!saveValid()) {
		return;
	}
	saveModify(true);
	updateParentInfo();
}
/**
 * 重置
 */
function resetFunction(){
	init();
	tree.update('<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
			+ '" height="100%" width="100%" frameBorder=0 border=0 />', true);
}
/**
 * 修改时初始化
 */
function init(){
	var manageInfo = {
			unitInfoField :parent.RECORD_FOR_EDIT.get('DEPARTMENT'),	
			networkInfo :parent.RECORD_FOR_EDIT.get('NET_WORK_NAME'),
			factory : parent.RECORD_FOR_EDIT.get('FACTORY'),
			product : parent.RECORD_FOR_EDIT.get('PRODUCT_NAME'),
			waveCountActInfo : parent.RECORD_FOR_EDIT.get('ACTUAL_WAVE_NUM'),
			waveCountInfo : parent.RECORD_FOR_EDIT.get('STD_WAVE_NUM')
	};
	infoPanel.getForm().setValues(manageInfo);
	productStore.baseParams = {factoryId : parent.RECORD_FOR_EDIT.get('FACTORY')};
	sStoreLoad(parent.RECORD_FOR_EDIT.get('RESOURCE_UNIT_MANAGE_ID'));
}
/**
 * 保存修改的manage和units信息,并向前台返回manage信息
 * 
 * @param manageId
 *            查询数据库中manage的原有信息
 */
function saveModify(_isOpen){
	var manageInfo = getManageAndUnitsInfo();
	manageInfo.RESOURCE_UNIT_MANAGE_ID =parent.RECORD_FOR_EDIT.get('RESOURCE_UNIT_MANAGE_ID');
	var manageArray = [manageInfo];
	var jsonData = getResourceUnitManagerListModel(manageArray);
	if(!jsonData){
		return;
	}
	// 发送插入新纪录的请求
	CreateAndModifyAjax("nx-report!updateManageInfo.action",jsonData,manageInfo,_isOpen);
}
/**
 * 保存新创建的manage和units信息,并向前台返回manage信息
 */
function saveCreate(){
	var manageInfo = getManageAndUnitsInfo();
	var manageArray = [manageInfo];
	var jsonData = getResourceUnitManagerListModel(manageArray);
	if(!jsonData){
		return;
	}
	// 发送插入新纪录的请求
	CreateAndModifyAjax("nx-report!insertManageInfo.action",jsonData,manageInfo);
}

/**
 * 从当前页面的选择状态，获取manageInfo对象
 * e.g.{BASE_NE_ID:1,STATION:"TES",unitList:[{BASE_UNIT_ID:1},{BASE_UNIT_ID:2}]}
 */ 
function getManageAndUnitsInfo(){
	var unitList = [];
	selectedUnitStore.each(function(r){
		unitList.push({
			BASE_UNIT_ID :r.get("BASE_UNIT_ID"),
			R_PTP_ID:r.get('r_PTP_ID'),
			STATION:r.get('STATION'),
			ACTUAL_WAVE_NUM : r.get('ACTUAL_WAVE_NUM'),
			INSERTION_LOSS : r.get('INSERTION_LOSS'),
			TYPE :4
		});
});
	var info = infoPanel.getForm().getValues();
	var  manageInfo = {
			DEPARTMENT : info.unitInfoField,
			NET_WORK_NAME:info.networkInfo,
			PRODUCT_NAME : info.product,
			FACTORY : factoryStore.getAt(factoryStore.find('factoryName',info.factory)).get('factoryId'),
			ACTUAL_WAVE_NUM : info.waveCountActInfo,
			STD_WAVE_NUM : info.waveCountInfo,
			TYPE :4
	};
	manageInfo.unitList = unitList;
	return manageInfo;
}
/**
 * 发送请求
 * 
 * @param url
 * @param jsonData
 *            参数
 * @param manageInfo
 *            需要传个上个页面的信息
 */
function CreateAndModifyAjax(url,jsonData,manageInfo,_isOpen){
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : url,
		params : jsonData,
		method : 'POST',
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult==0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
			if (result.returnResult==1) {
				if(type == 1){
					manageInfo.RESOURCE_UNIT_MANAGE_ID = result.returnMessage;
				}
				parent.dataTransport(manageInfo);
				if(!_isOpen){
					parent.Ext.getCmp('addWindow').close();
				}
			}
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

/**
 * 加载方向内板卡
 * 
 * @param waveDirId
 */
function sStoreLoad(waveDirId){
	selectedUnitStore.baseParams = {
			"manageId":waveDirId,
			reportType:4
	};
	selectedUnitStore.load({
		callback : function(records,options,success){// records：加载的数据数组
														// ，options:调用load方法的配置对象
														// ，success: 布尔值，是否加载成功
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}else{
			}
		}
	});
}

var unitInfoStore = new Ext.data.Store({
	id : 'BASE_UNIT_ID',
	url : 'nx-report!getNodeInfoByUnitId.action',
	reader : new Ext.data.JsonReader({
		id : 'BASE_UNIT_ID',
		totalProperty : 'total',
		root : 'rows'
	}, ['STATION','EMSGROUP_DISPLAY_NAME','EMS_DISPLAY_NAME','SUBNET_DISPLAY_NAME','NE_DISPLAY_NAME',
	    'BASE_UNIT_ID','unit','unitDesc','RESOURCE_UNIT_MANAGE_ID','ACTUAL_WAVE_NUM','INSERTION_LOSS',
	    'r_PTP_ID','r_PTP_NAME'])
});
function getCheckNode(){
	var iframe = window.frames['tree_panel'];
	var result = iframe.getCheckedNodes(['nodeId','nodeLevel']);
	var unitIds = [];
	if(result.length == 0){
		Ext.Msg.alert('提示','选择不能为空');
		return;
	}
	for(var i = 0; i< result.length;i++){
		if(result[i].nodeLevel<6){
			Ext.Msg.alert('提示','只能选择板卡');
			return ;
		}else{
				unitIds.push(result[i].nodeId);
		}
	}
	unitInfoStore.baseParams = {'unitIds':unitIds};
	unitInfoStore.load({
		callback : function(records,options,success){// records：加载的数据数组
														// ，options:调用load方法的配置对象
														// ，success: 布尔值，是否加载成功
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}else{
				unitInfoStore.each(function(r){
					if(!selectedUnitStore.getById(r.get('BASE_UNIT_ID'))){
						selectedUnitStore.add(r);
					}
				});
				selectedUnitGrid.getView().refresh();
			}
		}
	});
}
Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 900000;

			var subwin = new Ext.Viewport({
				id : 'subwin',
				layout : 'fit',
				autoScroll:true, 
				items : [ all]
			});
			subwin.show();
			if(type == 2){
				init();
			}
		});
