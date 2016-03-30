
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
			items:[unitInfoField,waveDirInfo]
		},{
			columnWidth : 0.45,
			layout:'form',
			border:false,
			items:[stationInfo,networkInfo]
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
	}, [ "BASE_UNIT_ID","unitId", "rack", "unitDesc", "shelf", "slot","unit", "proMode", "waveLength"])
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
	url : 'nx-report!getUnitInfoByManageId.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "unitId","rack","shelf", "slot", "BASE_UNIT_ID","DIRECTION", "unitDesc", "PM_STD_OPT_AMP_ID", "DIRECTION_LINK","unit",
	     "OPTICAL_LEVEL", "IN_OUT","STD_WAVE_NUM","ACTUAL_WAVE_NUM","r_PTP_ID",'t_PTP_ID',"r_PTP_NAME",'t_PTP_NAME','modelName'])
});

var selectedUnitSM = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var inOut;
var inOutStore;
var PTP_ComBox;
var ptpStore;
(function(){
	inOutStore = new Ext.data.ArrayStore({
		fields : ['id','displayName'],
		data:[['1','收'],['2','发']]
	});
	ptpStore = new Ext.data.Store({
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		},['BASE_PTP_ID','DISPLAY_NAME']),
		url : 'nx-report!getPortByUnitId.action'
	});
	inOut= new Ext.form.ComboBox({
		id : 'inOut',
		triggerAction : 'all',
		editable :false,
		mode:'local',
		selectOnFocus:false,
		typeAheadDelay:500,
		store : inOutStore,
		displayField : "displayName",
		valueField : 'id',	
		typeAhead:true,
		resizable: true
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
            		var unitId  = selections[0].get('BASE_UNIT_ID')==""?selections[0].get('unitId'):selections[0].get('BASE_UNIT_ID');
            			delete qe.combo.lastQuery;
            			ptpStore.baseParams = {unitId :  unitId};
            			currentUnitId = unitId;
            	}
	        }
	    }

	});
})();

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
		id : 'PM_STD_OPT_AMP_ID',
		header :'<span style="font-weight:bold">板卡规格</span>',
		dataIndex : 'PM_STD_OPT_AMP_ID',
		width : 100,
		renderer : function(value,cellmeta,record){
			return record.get('modelName');
		},
		editor : new Ext.form.TextField({
			readOnly : true,
			allowNegative : true,
			maxLenth : 100,
			listeners : {
				focus :	function(scope){
					var fieldText = scope;
					var pmSelectWindow=new Ext.Window({
						id:'pmSelectWindow',
						title:'光放板卡规格选择',
						width : Ext.getBody().getWidth()*0.8,      
					    height : Ext.getBody().getHeight()-80, 
						closeAction :'close',
						border:'fit',
						stateful:false,
						isTopContainer : true,
						modal : true,
						plain:true,  //是否为透明背景 
						html : '<iframe id="pmSelect" name = "pmSelect" src = "'+'../../../performanceManager/multipleSection/standardOpticalValueListForReport.jsp'+'" height="100%" width="100%"  frameBorder=0 border=0/>',
						buttons:[{
							text:"确定",
							handler:function(){
								 var pmSelect = window.frames["pmSelect"];
								 var o = pmSelect.getSelectedId();
								if(typeof o == 'object'){
									var selections = selectedUnitGrid.getSelectionModel().getSelections();
									if(selections.length>0){
										selections[0].set('PM_STD_OPT_AMP_ID',o.id);
										selections[0].set('modelName',o.model);
										selectedUnitGrid.getView().refresh();
									}
									pmSelectWindow.close();
								}else{
									Ext.Msg.alert('提示','请勾选一个');
								}
							}
						},{
							text:"取消",
							handler:function(){
								pmSelectWindow.close();
							}
						}]
					});
					pmSelectWindow.show();
				}
			}
		})
	},{
		id :'R_PTP_ID',
		header :'<span style="font-weight:bold">板卡收光端口</span>',
		dataIndex :'r_PTP_ID',
		width : 150,
		editor :PTP_ComBox,
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
	},{
		id :'T_PTP_ID',
		header :'<span style="font-weight:bold">板卡发光端口</span>',
		dataIndex :'t_PTP_ID',
		width : 150,
		editor : PTP_ComBox,
		renderer : function(value,cellmeta,record){
			var name ;
			ptpStore.each(function(r){
				if(r.get('BASE_PTP_ID') == value){
					name = r.get('DISPLAY_NAME');
				}
			});
			if(typeof name !="undefined"){
				record.set('t_PTP_NAME',name);
			}
 			return record.data['t_PTP_NAME'];
		}
	},{
		id :'DIRECTION_LINK',
		header :'<span style="font-weight:bold">环链路方向</span>',
		dataIndex :'DIRECTION_LINK',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	},{
		id :'OPTICAL_LEVEL',
		header :'<span style="font-weight:bold">放大器级数</span>',
		dataIndex :'OPTICAL_LEVEL',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	},{
		id :'IN_OUT',
		header :'<span style="font-weight:bold">收/发</span>',
		dataIndex :'IN_OUT',
		width : 100,
		editor: inOut,
		renderer : function(v){
			if(v == 1){
				return "收"	;
			}
			if(v == 2){
				return '发';
			}
		}
	},{
		id :'DIRECTION',
		header :'<span style="font-weight:bold">方向</span>',
		dataIndex :'DIRECTION',
		width : 100,
		editor : new Ext.form.TextField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100
		})
	},{
		id :'STD_WAVE_NUM',
		header :'<span style="font-weight:bold">容量</span>',
		dataIndex :'STD_WAVE_NUM',
		width : 100,
		editor : new Ext.form.NumberField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100,
			allowNegative :false
		})
	},{
		id :'ACTUAL_WAVE_NUM',
		header :'<span style="font-weight:bold">开通波数</span>',
		dataIndex :'ACTUAL_WAVE_NUM',
		width : 100,
		editor : new Ext.form.NumberField({
			// allowBlank: false,
			allowNegative : true,
			maxLenth : 100,
			allowNegative :false
		})
	}
	]
});
var dupMark = false;
var selectedUnitGrid = new Ext.grid.EditorGridPanel({
	id : 'selectedUnitGrid',
	title:'波分方向所含板卡',
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
	parent.RECORD_FOR_EDIT.set('DIRECTION',Ext.getCmp('waveDirInfo').getValue());
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
			waveDirInfo :parent.RECORD_FOR_EDIT.get('DIRECTION'),	
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
			DIRECTION_LINK : r.get('DIRECTION_LINK'),
			OPTICAL_LEVEL : r.get('OPTICAL_LEVEL'),
			IN_OUT : r.get('IN_OUT'),
			DIRECTION : r.get('DIRECTION'),
			STD_WAVE_NUM : r.get('STD_WAVE_NUM'),
			ACTUAL_WAVE_NUM : r.get('ACTUAL_WAVE_NUM'),
			T_PTP_ID:r.get('t_PTP_ID'),
			R_PTP_ID:r.get('r_PTP_ID'),
			TYPE :1
		});
});
	var info = infoPanel.getForm().getValues();
	var  manageInfo = {
			BASE_NE_ID : neId,
			DEPARTMENT : info.unitInfoField,
			STATION :info.stationInfo,
			NET_WORK_NAME:info.networkInfo,
			DIRECTION :info.waveDirInfo,
			TYPE :1
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
//				Ext.Msg.confirm("提示", "数据保存成功，是否继续？",function(btn){
//					if(btn!='yes')
				if(!_isOpen){
					parent.Ext.getCmp('addWindow').close();
				}
//					else
//						selectedUnitStore.removeAll();
//				});
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
			reportType:1
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
