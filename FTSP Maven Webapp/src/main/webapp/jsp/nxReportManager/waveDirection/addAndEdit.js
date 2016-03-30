
// ***********************波分方向信息****************************

var neId='';
var unitIdRemoved = new Array();

if(!!neIdPassed)
	neId = neIdPassed;
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

var waveDirInfo = {
		xtype:'textfield',
		fieldLabel:"波分方向",
		id:"waveDirInfo",
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
	title:'波分方向信息',
	labelWidth:70,
	items:[neInfo,{
		layout:'column',
		width:1080,
		border:false,
		items:[{
			columnWidth : 0.45,
			border:false,
			layout:'form',
			items:[unitInfoField,waveDirInfo,waveCountInfo]
		},{
			columnWidth : 0.45,
			layout:'form',
			border:false,
			items:[stationInfo,networkInfo,waveCountActInfo]
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
	}, [ "unitId", "rack", "unitDesc", "shelf", "slot",	"unit","unitDesc", "proMode", "waveLength"])
});
var unselectedUnitSM = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var unselectedUnitCM = new Ext.grid.ColumnModel({
	defaults : {sortable : true},
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
var waveLen;
(function(){
	var store = new Ext.data.ArrayStore({
		fields : ['id','displayName'],
		data:[['192.1','192.1'], ['192.55','192.55'],['193','193'],['193.45','193.45'],
		      ['193.9','193.9'], ['194.35','194.35'],['194.8','194.8'],['195.25','195.25'],
		      ['195.7','195.7'], ['192.15','192.15'],['192.6','192.6'],['193.05','193.05'],
		      ['193.5','193.5'], ['193.95','193.95'],['194.4','194.4'],['194.85','194.85'],
		      ['195.3','195.3'], ['195.75','195.75'],['192.2','192.2'],['192.65','192.65'],
		      ['193.1','193.1'], ['193.55','193.55'],['194','194'],['194.45','194.45'],
		      ['194.9','194.9'], ['195.35','195.35'],['195.8','195.8'],['192.25','192.25'],
		      ['192.7','192.7'], ['193.15','193.15'],['193.6','193.6'],['194.05','194.05'],
		      ['194.5','194.5'], ['194.95','194.95'],['195.4','195.4'],['195.85','195.85'],
		      ['192.3','192.3'], ['192.75','192.75'],['193.2','193.2'],['193.65','193.65'],
		      ['194.1','194.1'], ['194.55','194.55'],['195','195'], ['195.45','195.45'],
		      ['195.9','195.9'], ['192.35','192.35'],['192.8','192.8'],['193.25','193.25'],
		      ['193.7','193.7'], ['194.15','194.15'],['194.6','194.6'],['195.05','195.05'],		      
		      ['195.5','195.5'], ['195.95','195.95'],['192.4','192.4'],['192.85','192.85'],
		      ['193.3','193.3'], ['193.75','193.75'],['194.2','194.2'],['194.65','194.65'],
		      ['195.1','195.1'], ['195.55','195.55'],['196','196'],['192.45','192.45'],
		      ['192.9','192.9'], ['193.35','193.35'],['193.8','193.8'],['194.25','194.25'],
		      ['194.7','194.7'], ['195.15','195.15'],['195.6','195.6'],['196.05','196.05'],
		      ['192.5','192.5'], ['192.95','192.95'],['193.4','193.4'],['193.85','193.85'],
		      ['194.3','194.3'], ['194.75','194.75'],['195.2','195.2'],['195.65','195.65']
]
	});
	store.sort("id");
	waveLen = new Ext.form.ComboBox({
		id : 'waveLen',
		triggerAction : 'all',
		editable : true,
		mode:'local',
		selectOnFocus:false,
		typeAheadDelay:500,
		store : store,
		displayField : "displayName",
		valueField : 'id',	
		typeAhead:true,
		resizable: true
	});
})();

var selectedUnitStore = new Ext.data.Store({
	url : 'nx-report!getUnitByNe.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "unitId", "rack", "unitDesc", "shelf", "slot",	"unit","unitDesc", "proMode", "waveLength"])
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
	}, {
		id : 'proMode',
		header : '<span style="font-weight:bold">保护方式</span>',
		dataIndex : 'proMode',
		width : 100,
		editor : proMode,
		renderer:proModeRenderer
	}, {
		id : 'waveLength',
		header : '<span style="font-weight:bold">波道/波长</span>',
		dataIndex : 'waveLength',
		width : 100,
		editor:waveLen
	}]
});
var dupMark = false;
var selectedUnitGrid = new Ext.grid.EditorGridPanel({
	id : 'selectedUnitGrid',
	title:'波分方向所含板卡',
	store : selectedUnitStore,
	cm : selectedUnitCM,
	boxMinWidth:440,
	height:900,
//	flex:1,
	loadMask : true,
	selModel : selectedUnitSM, 
	viewConfig:{forceFit:true},
//	frame : false,
	stripeRows : true ,
	tbar:['-',{
		text:"上移",
		icon:"../../../resource/images/btnImages/up.png",
		handler:function(){
			upForward(selectedUnitGrid, selectedUnitStore);
		}
	},'-',{
		text:"下移",
		icon:"../../../resource/images/btnImages/down.png",
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
            		selectedUnitStore.add(selections);
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
	            	unselectedUnitStore.add(selections);
	            	
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
//	height:500,
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
 * 保存方向
 */
function save(){
	if(!infoPanel.getForm().isValid()) {
		Ext.Msg.alert("提示","有必填项没有填写！");
		return;
	}
	if(Ext.getCmp('waveCountInfo').getValue()<Ext.getCmp('waveCountActInfo').getValue()){
		Ext.Msg.alert("提示","实开（波数）不能大于容量（波数）！");
		return;
	}
	if(dupMark)
		return;
	var unitList = new Array();
	var i=0;
	selectedUnitStore.each(function(r){
		
//		if(selectedUnitStore.indexOf(r)!=o.row&&r.get('waveLength')==o.value){
//			o.record.set("waveLength",o.originalValue );
//			return false;
//		}
			unitList.push(Ext.encode({
				index : i,
				unitId : r.get('unitId'),
				proMode: r.get('proMode'),
				waveLength : r.get('waveLength')
			}));
			i++;
	});
	var info = infoPanel.getForm().getValues();
	info.neInfo = neId;
	var params = {
			"paramMap.neInfo": info.neInfo,
			"paramMap.unitInfoField": info.unitInfoField,
			"paramMap.stationInfo": info.stationInfo,
			"paramMap.waveDirInfo": info.waveDirInfo,
			"paramMap.networkInfo": info.networkInfo,
			"paramMap.waveCountInfo": info.waveCountInfo,
			"paramMap.waveCountActInfo": info.waveCountActInfo
	};
	if(unitList.length>0)
		params.modifyList = unitList;
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'nx-report!saveWaveDir.action',
		params : params,
		method : 'POST',
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult==0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
			if (result.returnResult==1) {
				parent.pageTool.doLoad(parent.pageTool.cursor);
				Ext.Msg.confirm("提示", "数据保存成功，是否继续？",function(btn){
					if(btn!='yes')
						parent.Ext.getCmp('addWindow').close();
					else
						selectedUnitStore.removeAll();
				});
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
	
//	console.dir(info);
}

function checkNode(){
//	if(!!waveDirId){
//		this.expand();
//		this.collapse();
////		this.initTreeWin();
//		this.treeField.checkNodes("4-"+neId);
//	}
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
			"paramMap.waveDirId":waveDirId
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
/**
 * 修改时初始化
 */
function init(){
	infoPanel.getForm().setValues(parent.editInfoFormValues);
	unsStoreLoad(neId);
	sStoreLoad(waveDirId);
}

/**
 * 保存修改
 * @param close 是否关闭窗口
 */
function saveEdit(close){
	if(!infoPanel.getForm().isValid()) {
		Ext.Msg.alert("提示","有必填项没有填写！");
		return;
	}
	if(Ext.getCmp('waveCountInfo').getValue()<Ext.getCmp('waveCountActInfo').getValue()){
		Ext.Msg.alert("提示","实开（波数）不能大于容量（波数）！");
		return;
	}
	if(dupMark)
		return;
//	console.log(unitIdRemoved.toString());
	var i=0;
	var unitList = new Array();
	selectedUnitStore.each(function(r){
		
			unitList.push(Ext.encode({
				index : i,
				unitId : r.get('unitId'),
				proMode: r.get('proMode'),
				waveLength : r.get('waveLength')
			}));
			i++;
	});
	var info = infoPanel.getForm().getValues();
	info.neInfo = neId;
	var params = {
			"paramMap.waveDirId": waveDirId,
			"paramMap.neInfo": neId,
			"paramMap.unitInfoField": info.unitInfoField,
			"paramMap.stationInfo": info.stationInfo,
			"paramMap.waveDirInfo": info.waveDirInfo,
			"paramMap.networkInfo": info.networkInfo,
			"paramMap.waveCountInfo": info.waveCountInfo,
			"paramMap.waveCountActInfo": info.waveCountActInfo,
			"paramMap.unitIdRemoved": unitIdRemoved.toString()
	};
	if(unitList.length>0)
		params.modifyList = unitList;
	
	top.Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'nx-report!editWaveDir.action',
		params : params,
		method : 'POST',
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult==0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
			if (result.returnResult==1) {
				Ext.Msg.alert("提示", result.returnMessage);
				parent.pageTool.doLoad(parent.pageTool.cursor);
				parent.editInfoFormValues = info;
				if(close)
					parent.Ext.getCmp('editWindow').close();
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
Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
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
			if(!!waveDirId){
				init();
				neInfo.disable();
			}
		});
