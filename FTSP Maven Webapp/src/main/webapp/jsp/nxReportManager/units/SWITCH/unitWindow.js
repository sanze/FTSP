
var neId='';
var unitIdRemoved = new Array();

if(type == 2)
	neId = parent.RECORD_FOR_EDIT.get('BASE_NE_ID');
var neInfo = new Ext.ux.EquipTreeCombo({
//		xtype:'equiptreecombo',
		fieldLabel:"网元",
		id: "neInfo",
//		anchor:'85%',
		width:850,
		listWidth: null,
//		listHeight: 200,
		checkableLevel: [4],
		leafType:CommonDefine.TREE.NODE.NE,
		allowBlank:false,
		checkModel : "single",
		rootVisible: false,
		listeners:{
			afterrender : checkNode,
			checkchange : function(node,checked){
			if(checked!='all'){
				return true;
			}else{
				var Node = node.id.split("-"); 
				neId = Node[1];unsStoreLoad(neId);
				selectedUnitGrid.removeAll();
				selectedUnitStore.removeAll();
				selectedUnitGrid.getView().refresh();
			}
			return true;
		}
		}
	});

var unitInfoField = {
		xtype:'textfield',
		fieldLabel:"单位",
		id:"unitInfoField",
		anchor:'90%',
		sideText:'<font color=red>*</font>',
		allowBlank:false
};

var stationInfo = {
		xtype:'textfield',
		fieldLabel:"站名",
		id:"stationInfo",
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
		id:"waveCountActInfo",
		sideText:'<font color=red>*</font>',
		allowBlank:false
};

var infoPanel = new Ext.form.FormPanel({
	id:'infoPanel',
	region:'north',
	border:false,
	height:140,
	bodyStyle:'padding:10px 30px 10px 30px',
	title:'网元信息',
	labelWidth:70,
	items:[neInfo,{
		layout:'column',
		width:1080,
		border:false,
		items:[{
			columnWidth : 0.45,
			border:false,
			layout:'form',
			items:[unitInfoField,networkInfo]
		},{
			columnWidth : 0.45,
			layout:'form',
			border:false,
			items:[stationInfo]
		}]
	}]
});

// -----------------------波分方向信息----------------------------

//%%%%%%%%%%%%%%%%%%%%%%%%%%%板卡信息%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

// ************************* 可选板卡 *****************************

var unselectedUnitStore = new Ext.data.Store({
	url : 'nx-report!getUnitByNe.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "BASE_UNIT_ID","unitId", "rack", "unitDesc", "shelf", "slot","unit", "proMode", "waveLength","UNIT_INFO"])
});
var unselectedUnitSM = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var unselectedUnitCM = new Ext.grid.ColumnModel({
	defaults : {sortable : false},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked:true
		}), unselectedUnitSM, {
		id : 'rack',
		header : '机架',
		dataIndex : 'rack',
		width : 100
	}, {
		id : 'shelf',
		header : '子架',
		dataIndex : 'shelf',
		width : 100
	}, {
		id : 'slot',
		header : '槽道',
		dataIndex : 'slot',
		width : 100
	}, {
		id : 'unit',
		header : '板卡',
		dataIndex : 'unit',
		width : 100
	}]
});

var unselectedUnitGrid = new Ext.grid.GridPanel({
	id : 'unselectedUnitGrid',
	title:'可选板卡',
	store : unselectedUnitStore,
	cm : unselectedUnitCM,
//	height: 230,
	boxMinWidth:440,
//	flex:1,
	loadMask : true,
	selModel : unselectedUnitSM, 
	frame : false,
	stripeRows : true ,
	viewConfig:{forceFit:true}
});
// ------------------------- 可选板卡 -----------------------------

// ************************* 已选板卡 *****************************
var proMode;
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
	}, [ "unitId","rack","shelf", "slot", "BASE_UNIT_ID","DIRECTION", "unitDesc", "DIRECTION_LINK","unit",'UNIT_INFO'
	     ,'UNIT_INFO','modelName'])
});

var selectedUnitSM = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});

var selectedUnitCM = new Ext.grid.ColumnModel({
	defaults : {sortable : false},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked:true
	}), selectedUnitSM, {
		id : 'unit',
		header : '板卡名称',
		dataIndex : 'unit',
		width : 100
	}, {
		id : 'unitDesc',
		header : '槽道号',
		dataIndex : 'unitDesc',
		width : 100
	},{
		id : 'UNIT_INFO',
		header :'板卡规格',
		dataIndex : 'UNIT_INFO',
		width : 100
	}]
});
var dupMark = false;
var selectedUnitGrid = new Ext.grid.EditorGridPanel({
	id : 'selectedUnitGrid',
	title:'作业计划所含板卡',
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
//------------------------- 已选板卡 -----------------------------

var unitInfo = new Ext.Panel({
	id:"unitInfo",
	title:'板卡选择',
	region:"center",
	layout: 'column',
	border:false,
	bodyStyle:'padding:15px 0px 10px 30px',
	defaults: {
		height:230,
		width:450
    },
	items:[unselectedUnitGrid,{
//    	height:230,
    	width:50,
    	border:false,
    	layout: {
            type: 'vbox',
            pack: 'start',  //纵向对齐方式 start：从顶部；center：从中部；end：从底部
            align: 'center'  //对齐方式 center、left、right：居中、左对齐、右对齐；stretch：延伸；stretchmax：以最大的元素为标准延伸
        },
        defaults: {
            xtype: 'button'
        },
        items: [{
            xtype: 'tbspacer',          //插入的空填充
            flex: 1
        },{
            text: ">>",
            height:10,
            width: 40,
            flex: 1,                      //表示当前子元素尺寸所占的均分的份数。
            handler:function(){
            	var selections = unselectedUnitGrid.getSelectionModel().getSelections();
            	if(selections.length>0){
            		for(var i=0;i<selections.length;i++){
            			selections[i].set('proMode',1);
            		}
            		if(type == 2){
	            		for(var i = 0;i<selections.length;i++){
		            		var index = selectedUnitStore.find('BASE_UNIT_ID',selections[i].get('unitId'));
		            		if(index < 0){
		            			selectedUnitStore.add(selections[i]);
		            		}
		            	}
	            	}else{
	            		selectedUnitStore.add(selections);
	            	}
            		unselectedUnitStore.remove(selections);
            		selectedUnitGrid.getView().refresh();
            		unselectedUnitGrid.getView().refresh();
            	}
            	if(!!waveDirId){
            		for(var i=0;i<selections.length;i++){
            			var index = unitIdRemoved.indexOf(selections[i].get("unitId"));
            			if(index!=-1){
            				unitIdRemoved.splice(index,1);
            			}
            		}
            	}
            }
            
        },{
            xtype: 'tbspacer',          //插入的空填充
            flex: 2
        },{
            text: "<<<",
            height:10,
            width: 40,
            flex: 1,                      //表示当前子元素尺寸所占的均分的份数。
            handler:function(){
            	var selections = selectedUnitGrid.getSelectionModel().getSelections();
            	if(selections.length>0){
	            	selectedUnitStore.remove(selections);
	            	if(type == 2){
	            		for(var i = 0;i<selections.length;i++){
		            		var index = unselectedUnitStore.find('unitId',selections[i].get('BASE_UNIT_ID'));
		            		if(index < 0){
		            			unselectedUnitStore.add(selections[i]);
		            		}
		            	}
	            	}else{
	            		unselectedUnitStore.add(selections);
	            	}
	            	selectedUnitGrid.getView().refresh();
	            	unselectedUnitGrid.getView().refresh();
	            	
	            	if(!!waveDirId){
	            		for(var i=0;i<selections.length;i++){
	            			unitIdRemoved.push(selections[i].get("unitId"));
	            		}
	            	}
            	}
            }
        },{
            xtype: 'tbspacer',          //插入的空填充
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
//==========================板卡信息===================================

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

//**************************RENDERERS*************************
function proModeRenderer(v){
	for(var i=0;i<PROMODE.length;i++){
		if(v==PROMODE[i]['key'])
			return PROMODE[i]['value'];
	}
	return v;
}

//**************************FUNCTIONS**************************

/**
 * 保存对unit的添加或更新
 */
function okBtnFunction(){
	 if(!infoPanel.getForm().isValid()) {
			Ext.Msg.alert("提示","有必填项没有填写！");
			return;
		}
	 // type从jsp中获取
	if(type ==1){
		saveCreate();//新增
	}else if(type == 2){
		saveModify();//修改
	}else{
		Ext.Msg.alert('提示','缺少参数');
	}
}
/**
 * 应用
 */
function updateParentInfo(){
	parent.RECORD_FOR_EDIT.set('DEPARTMENT',Ext.getCmp('unitInfoField').getValue());	
	parent.RECORD_FOR_EDIT.set('STATION',Ext.getCmp('stationInfo').getValue());
	parent.RECORD_FOR_EDIT.set('NET_WORK_NAME',Ext.getCmp('networkInfo').getValue());
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
}
/**
 * 修改时初始化
 */
function init(){
	var manageInfo = {
			neInfo : parent.RECORD_FOR_EDIT.get('NE_DISPLAY_NAME'),
			unitInfoField :parent.RECORD_FOR_EDIT.get('DEPARTMENT'),	
			stationInfo :parent.RECORD_FOR_EDIT.get('STATION'),	
			networkInfo :parent.RECORD_FOR_EDIT.get('NET_WORK_NAME'),	
	};
	infoPanel.getForm().setValues(manageInfo);
	unsStoreLoad(neId);
	sStoreLoad(parent.RECORD_FOR_EDIT.get('RESOURCE_UNIT_MANAGE_ID'));
	unselectedUnitStore.remove(selectedUnitStore.fields);
	unselectedUnitGrid.getView().refresh();
}
/**
 * 保存修改的manage和units信息,并向前台返回manage信息
 * @param manageId 查询数据库中manage的原有信息
 */
function saveModify(_isOpen){
	var manageInfo = getManageAndUnitsInfo();
	manageInfo.RESOURCE_UNIT_MANAGE_ID =parent.RECORD_FOR_EDIT.get('RESOURCE_UNIT_MANAGE_ID');
	var manageArray = [manageInfo];
	var jsonData = getResourceUnitManagerListModel(manageArray);
	if(!jsonData){
		return;
	}
	//发送插入新纪录的请求
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
	//发送插入新纪录的请求
	CreateAndModifyAjax("nx-report!insertManageInfo.action",jsonData,manageInfo);
}
/**
 * 验证需要提交的数据是否合法
 */
function saveValid(){
	 if(!infoPanel.getForm().isValid()) {
			Ext.Msg.alert("提示","有必填项没有填写！");
			return false;
	}else {
		return true;
	}
}
/**
 * 从当前页面的选择状态，获取manageInfo对象
 * e.g.{BASE_NE_ID:1,STATION:"TES",unitList:[{BASE_UNIT_ID:1},{BASE_UNIT_ID:2}]}
 */ 
function getManageAndUnitsInfo(){
	var unitList = [];
	selectedUnitStore.each(function(r){
		unitList.push({
			BASE_UNIT_ID : r.get('unitId')==""?r.get("BASE_UNIT_ID"):r.get('unitId') == 0?r.get("BASE_UNIT_ID"):r.get('unitId'),
			PM_STD_OPT_AMP_ID: r.get('PM_STD_OPT_AMP_ID'),
			TYPE :2
		});
});
	var info = infoPanel.getForm().getValues();
	var  manageInfo = {
			BASE_NE_ID : neId,
			DEPARTMENT : info.unitInfoField,
			STATION :info.stationInfo,
			NET_WORK_NAME:info.networkInfo,
			TYPE :2
	};
	manageInfo.unitList = unitList;
	return manageInfo;
}
/**
 * 发送请求
 * @param url 
 * @param jsonData 参数
 * @param manageInfo 需要传个上个页面的信息
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
 * 加载可选板卡
 * @param neId
 */
function unsStoreLoad(neId){
	unselectedUnitStore.baseParams = {
			"paramMap.neId":neId
	};
	unselectedUnitStore.load({
		callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}else{
			}
		}
	});
}

/**
 * 加载方向内板卡
 * @param waveDirId
 */
function sStoreLoad(waveDirId){
	selectedUnitStore.baseParams = {
			"manageId":waveDirId,
			reportType:2
	};
	selectedUnitStore.load({
		callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}else{
			}
		}
	});
}

function checkNode(){
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
				neInfo.disable();
			}
		});
