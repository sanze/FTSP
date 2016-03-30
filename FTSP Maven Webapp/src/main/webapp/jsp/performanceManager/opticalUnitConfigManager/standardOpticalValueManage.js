/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

// --------------------domainCombo---------------------
Ext.QuickTips.init(); 

var domainData = [ [ '0', '全部' ], [ '1', 'SDH' ], [ '2', 'WDM' ],
		[ '3', 'ETH' ] ];
var domainStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
domainStore.loadData(domainData);
var domainCombo = new Ext.form.ComboBox({
	id : 'domainCombo',
	name : 'domainCombo',
	fieldLabel : '业务类型',
	store : domainStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	value:0,
	triggerAction : 'all',
	width : 100,
	listeners : {
		select : function(combo, record, index) {
			// 重置combo
			Ext.getCmp('portCombo').reset();
			Ext.getCmp('opticalStandardCombo').reset();
			// 设置port类型数据源
			if (record.get('value') == '1') {
				portStore.loadData(sdhData);
			} else if (record.get('value') == '2') {
				portStore.loadData(wdmData);
			} else if (record.get('value') == '3') {
				portStore.loadData(ethData);
			} else if (record.get('value') == '0') {
				portStore.loadData(allData);
			}
			// 加载光口标准
			getopticalStandardComboValue();
		}
	}
});
// --------------------portCombo---------------------
var ethData = [ [ '全部', '全部' ], [ 'FE', 'FE' ],[ 'GE', 'GE' ], [ '10GE', '10GE' ]];
var sdhData = [ [ '全部', '全部' ], [ 'STM-1', 'STM-1' ], [ 'STM-4', 'STM-4' ],
		[ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ] ];
var wdmData = [ [ '全部', '全部' ], [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ],
		[ 'OTU3', 'OTU3' ] ];
var allData = [ [ '全部', '全部' ], [ 'MAC', 'ETH' ], [ 'STM-1', 'STM-1' ],
		[ 'STM-4', 'STM-4' ], [ 'STM-16', 'STM-16' ], [ 'STM-64', 'STM-64' ],
		[ 'STM-256', 'STM-256' ], [ 'OTU1', 'OTU1' ], [ 'OTU2', 'OTU2' ],
		[ 'OTU3', 'OTU3' ] ];
var portStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
portStore.loadData(allData);
var portCombo = new Ext.form.ComboBox({
	id : 'portCombo',
	name : 'portCombo',
	fieldLabel : '端口类型',
	store : portStore,
	valueField : 'value',
	displayField : 'displayName',
	mode : 'local',
	triggerAction : 'all',
	value:'全部',
	width : 100,
	listeners : {
		select : function(combo, record, index) {
			// 重置光口标准combo和store
			Ext.getCmp('opticalStandardCombo').reset();
			getopticalStandardComboValue();
		}
	}
});

// --------------------opticalStandardCombo---------------------
var opticalStandardStore = new Ext.data.Store({
	url : 'optical-unit-config!getOptStdComboValue.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmStdOptPortId", "model" ])
});

var opticalStandardCombo = new Ext.form.ComboBox({
	id : 'opticalStandardCombo',
	name : 'opticalStandardCombo',
	fieldLabel : '光口标准',
	store : opticalStandardStore,
	mode : 'local',
	displayField : "model",
	valueField : 'pmStdOptPortId',
	width : 100,
	resizable: true,
	triggerAction : 'all'
});
// 页面打开初始化加载光口标准
(function() {
	var searchParam = {
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 0
	};
	opticalStandardStore.baseParams = searchParam;
	opticalStandardStore.load({
		callback:function(){
			opticalStandardCombo.setValue(0);
		}
	});
})();

function getopticalStandardComboValue() {
	var domain = Ext.getCmp('domainCombo').getValue();
	var ptpType = Ext.getCmp('portCombo').getValue();
	var searchParam = {
		'searchCond.domain' : domain,
		'searchCond.ptpType' : ptpType,
		'searchCond.needAll' : 1,
		'searchCond.needNull' : 0
	};
	opticalStandardStore.baseParams = searchParam;
	opticalStandardStore.load();
}

// ==========================page=============================
// --------------------srore for grid---------------------
var store = new Ext.data.Store({
	url : 'optical-unit-config!searchOptStdDetail.action',
	baseParams : {
		"pageSize" : 50
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "optStdId", "domain", "rate", "ptpType", "optStd", "maxOut", "minOut",
			"maxIn", "minIn", "distance", "centerWaveLength" ])
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	header:""
});
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
		id : 'domain',
		header : '业务类型',
		dataIndex : 'domain',
		renderer : transDomainName,
		width : 100
	}, {
		id : 'ptpType',
		header : '端口类型',
		dataIndex : 'ptpType',
		width : 150
	}, {
		id : 'optStd',
		header : '光口标准',
		dataIndex : 'optStd',
		width : 150
	}, {
		id : 'maxOut',
		header : '<span style="font-weight:bold">最大输出功率(dBm)</span>',
		dataIndex : 'maxOut',
		tooltip:'可编辑列',
		width : 150,
		editor : new Ext.form.NumberField({
			allowBlank : true
		}),
		renderer:function(v){
			return Ext.util.Format.number(v, '0.00');
		}
	}, {
		id : 'minOut',
		header : '<span style="font-weight:bold">最小输出功率(dBm)</span>',
		tooltip:'可编辑列',
		dataIndex : 'minOut',
		width : 150,
		editor : new Ext.form.NumberField({
			allowBlank : true
// allowNegative : true,
// maxValue : 100000,
		}),
		renderer:function(v){
			return Ext.util.Format.number(v, '0.00');
		}
	}, {
		id : 'maxIn',
		header : '<span style="font-weight:bold">过载点(dBm)</span>',
		tooltip:'可编辑列',
		dataIndex : 'maxIn',
		width : 100,
		editor : new Ext.form.NumberField({
			allowBlank : true,
			allowNegative : true
// maxValue : 100000
		}),
		renderer:function(v){
			return Ext.util.Format.number(v, '0.00');
		}
	}, {
		id : 'minIn',
		header : '<span style="font-weight:bold">灵敏度(dBm)</span>',
		tooltip:'可编辑列',
		dataIndex : 'minIn',
		width : 100,
		editor : new Ext.form.NumberField({
			allowBlank : true,
			allowNegative : true
// maxValue : 100000
		}),
		renderer:function(v){
			return Ext.util.Format.number(v, '0.00');
		}
	}, {
		id : 'distance',
		header : '<span style="font-weight:bold">传送距离(km)</span>',
		tooltip:'可编辑列',
		dataIndex : 'distance',
		width : 100,
		editor : new Ext.form.NumberField({
			allowBlank : true,
			decimalPrecision:0,
			allowNegative : false
// maxValue : 100000
		})
	}, {
		id : 'centerWaveLength',
		header : '<span style="font-weight:bold">中心波长(nm)</span>',
		tooltip:'可编辑列',
		dataIndex : 'centerWaveLength',
		width : 100,
		editor : new Ext.form.NumberField({
			allowBlank : true,
			allowNegative : true
// maxValue : 100000
		}),
		renderer:function(v){
			return Ext.util.Format.number(v, '0.00');
		}
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	// title:'用户管理',
	loadMask : true,
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	clicksToEdit : 2,// 设置点击几次才可编辑
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	bbar : pageTool,
	tbar : [ '-','业务类型:', domainCombo, '-','端口类型:', portCombo, '-','光口标准:',
			opticalStandardCombo, '-',{
				text : '查询',
				privilege:viewAuth,
				icon : '../../../resource/images/btnImages/search.png',
				handler : searchOptStdDetail
			}, '-', {
				text : '新增',
				privilege:addAuth,
				icon : '../../../resource/images/btnImages/add.png',
				handler : addData
			}, '-',{
				text : '保存',
				privilege:modAuth,
				icon : '../../../resource/images/btnImages/disk.png',
				handler : saveOptStdDetail
			}, '-',{
				text : '删除',
				privilege:delAuth,
				icon : '../../../resource/images/btnImages/delete.png',
				handler : checkIfStdAppliedThenDelete
			} ]
});

gridPanel.on('afteredit', afterEdit, this);

// =======================Functions=========================

// editPanel的afteredit事件的处理方法
function afterEdit(e) {
	if (e.field == 'maxIn' || e.field == 'minIn') {
		var maxIn = e.record.get('maxIn');
		var minIn = e.record.get('minIn');
		if (maxIn != '' && minIn != '') {
			if (maxIn <= minIn) {
				Ext.MessageBox.show({
					title : '错误',
					msg : '过载点必须大于灵敏度！',
					buttons : Ext.MessageBox.OK,
					icon : Ext.MessageBox.ERROR
				});
				e.record.set(e.field, e.originalValue);
			}
		}
	}
	if (e.field == 'maxOut' || e.field == 'minOut') {
		var maxOut = e.record.get('maxOut');
		var minOut = e.record.get('minOut');
		if (maxOut != '' && minOut != '') {
			if (maxOut <= minOut) {
				Ext.MessageBox.show({
					title : '错误',
					msg : '最大输出光功率必须大于最小输出光功率！',
					buttons : Ext.MessageBox.OK,
					icon : Ext.MessageBox.ERROR
				});
				e.record.set(e.field, e.originalValue);
			}
		}
	}
}
// -------------------------新增--------------------------
function addData() {
	var url = "addStandardOpticalValue.jsp";
	addStandardOpticalValueWindow = new Ext.Window({
		id : 'addStandardOpticalValueWindow',
		title : '新增光口标准',
		width : 400,
		height : 350,
		isTopContainer : true,
		modal : true,
		plain : true, // 是否为透明背景
		html : '<iframe src=' + url
				+ ' height="100%" width="100%" frameborder=0 border=0/>'
	});
	addStandardOpticalValueWindow.show();
}

// -------------------------查询--------------------------
function searchOptStdDetail() {
	var domain = Ext.getCmp('domainCombo').getValue();
	var ptpType = Ext.getCmp('portCombo').getValue();
	var optStdId = Ext.getCmp('opticalStandardCombo').getValue();
	store.baseParams = {
		'searchCond.domain' : domain,
		'searchCond.ptpType' : ptpType,
		'start' : 0,
		'limit' : 200,
		'searchCond.optStdId' : optStdId
	};
	store.load();
}

// -------------------------保存修改--------------------------
function saveOptStdDetail() {
	var records = store.getModifiedRecords();
	var list = new Array();
	if (records.length > 0) {
		for ( var i = 0; i < records.length; i++) {
			var rec = {
				"optStdId" : records[i].get("optStdId"),
				"maxOut" :  Ext.util.Format.number(records[i].get("maxOut"), '0.00'),
				"maxIn" :  Ext.util.Format.number(records[i].get("maxIn"), '0.00'),
				"minOut" : Ext.util.Format.number(records[i].get("minOut"), '0.00'),
				"minIn" : Ext.util.Format.number(records[i].get("minIn"), '0.00'),
				"distance" : records[i].get("distance"),
				"centerWaveLength" : Ext.util.Format.number(records[i].get("centerWaveLength"), '0.00'),
			};
			list.push(Ext.encode(rec));
		}
		var saveParams = {
			"modifyList" : list
		};
		Ext.Ajax.request({
			url : 'optical-unit-config!saveOptStdDetail.action',
			params : saveParams,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
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
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}
// ------------------------确认是否已应用---------------------
function checkIfStdAppliedThenDelete(){
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length==0){
		Ext.MessageBox.show({
			title : '信息',
			msg : '请先选取光口标准!',
			buttons : Ext.MessageBox.OK,
			icon : Ext.MessageBox.INFO
		});
		return;
	}
	var params = {"searchCond.optStdId":cell[0].get("optStdId")};
	var r;
	var isApplied = 0;
	Ext.Ajax.request({
		url : 'optical-unit-config!checkIfStdApplied.action',
		method : 'POST',
		params : params,
		success : function(response) {
			var result = Ext.util.JSON
					.decode(response.responseText);
			if (result) {
				if (result.returnResult!=1) {
					r = result.returnMessage;
					if(result.returnResult==2){
						Ext.Msg.alert("提示",r);
						return;
					}
					isApplied = 1;
				}else{
					r = '是否删除选中的光口标准？';
				}
				deleteOptStd(r,cell,isApplied);
			}
		}
	});
}
// -------------------------删除--------------------------
function deleteOptStd(r,cell,isApplied) {
	var selectedId = new Array();
	Ext.Msg.confirm('提示', r, function(btn) {
		if (btn == 'yes') {
			// 获取选中的id
			for ( var i = 0; i < cell.length; i++) {
				selectedId.push(cell[i].get("optStdId"));
			}
			var jsonData = {
				"condList" : selectedId,
				"searchCond.isApplied":isApplied
			};
			// top.Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'optical-unit-config!deleteOptStd.action',
				method : 'POST',
				params : jsonData,
				success : function(response) {
					gridPanel.getEl().unmask();
					var result = Ext.util.JSON
							.decode(response.responseText);
					if (result) {
						Ext.Msg.alert("提示", result.returnMessage);
						if (1 == result.returnResult) {
							var pageTool = Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
						}
					}
				}
			});
		}
	});
}
//------------------------domain renderer-------------------------
function transDomainName(v) {
	if (v == 1)
		return "SDH";
	if (v == 2)
		return "WDM";
	if (v == 3)
		return "ETH";
	if (v == 4)
		return "ATM";
}
// ------------------------init the page-------------------------
Ext
		.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.Ext.menu.MenuMgr.hideAll();
			};

			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [ gridPanel ],
				renderTo : Ext.getBody()
			});
		});