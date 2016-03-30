Ext.QuickTips.init(); 

var dataKindData = [ [ '计数值', '0' ], [ '物理量', '1' ] ];
var dataKindStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'displayName'
	}, {
		name : 'id'
	} ]
});
dataKindStore.loadData(dataKindData);
var dataKindCombo = new Ext.form.ComboBox({
	id : 'dataKindCombo',
	store : dataKindStore,
	displayField : "displayName",
	valueField : 'id',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : '0',
	width : 100,
	listeners : {
//		'select' : function(combo, record, index) {
//			if(record.get('id')==1){
//				gridPanel.reconfigure(physicalStore,physicalCm);
//				getPhysical();
//				Ext.getCmp('savePhysical').setVisible(true);
//				Ext.getCmp('saveNumberic').setVisible(false);
//			}else{
//				gridPanel.reconfigure(numbericStore,numbericCm);
//				getNumberic();
//				Ext.getCmp('savePhysical').setVisible(false);
//				Ext.getCmp('saveNumberic').setVisible(true);
//			}
//		},
		'beforeselect' : function(combo, record, index){
//			return false;
			if(record.get('id')==1){
				var records = numbericStore.getModifiedRecords();
				if(records.length>0){
					Ext.Msg.confirm("提示","还有修改未保存，确定切换吗？",function(btn){
						if(btn=="no"){
//							return false;
							dataKindCombo.setValue(0);
							return;
						}
						gridPanel.reconfigure(physicalStore,physicalCm);
						getPhysical();
						Ext.getCmp('savePhysical').setVisible(true);
						Ext.getCmp('saveNumberic').setVisible(false);
					});
				}else{
					gridPanel.reconfigure(physicalStore,physicalCm);
					getPhysical();
					Ext.getCmp('savePhysical').setVisible(true);
					Ext.getCmp('saveNumberic').setVisible(false);
				}
				
			}else{
				var records = physicalStore.getModifiedRecords();
				if(records.length>0){
					Ext.Msg.confirm("提示","还有修改未保存，确定切换吗？",function(btn){
						if(btn=="no"){
//							return false;
							dataKindCombo.setValue(1);
							return;
						}
						gridPanel.reconfigure(numbericStore,numbericCm);
						getNumberic();
						Ext.getCmp('savePhysical').setVisible(false);
						Ext.getCmp('saveNumberic').setVisible(true);
					});
				}else{
					gridPanel.reconfigure(numbericStore,numbericCm);
					getNumberic();
					Ext.getCmp('savePhysical').setVisible(false);
					Ext.getCmp('saveNumberic').setVisible(true);
				}
				
			}
		}
	}
});

var numbericStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getNumberic.action',
	pruneModifiedRecords:true,
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmTemplateInfoId","pmStdIndex", "pmDesc", "factory", "unit", "threshold1", "threshold2",
			"threshold3", "filterValue", "domain", "granularity" ])
});
var numbericCm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}),  {
		id : 'pmStdIndex',
		header : '性能事件',
		dataIndex : 'pmStdIndex',
		width :120
	},{
		id : 'granularity',
		header : '周期',
		dataIndex : 'granularity',
		width : 120,
		renderer : function(v) {
			if (v == 1) {
				return "15min";
			} else if (v == 2) {
				return "24hour";
			}
		}
	}, {
		id : 'domain',
		header : '性能类型',
		dataIndex : 'domain',
		width :120,
		renderer:domainRenderer
	}, {
		id : 'pmDesc',
		header : '描述',
		dataIndex : 'pmDesc',
		width : 120
	}, {
		id : 'factory',
		header : '厂家',
		dataIndex : 'factory',
		width : 120,
		renderer:transFactoryName
	}, {
		id : 'unit',
		header : '单位',
		dataIndex : 'unit',
		width : 120
	}, {
		id : 'threshold1',
		header: '<span style="font-weight:bold">阈值1</span>',
	    tooltip:'可编辑列',
		dataIndex : 'threshold1',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'threshold1Editor',
			allowBlank:false
		})
	}, {
		id : 'threshold2',
		header: '<span style="font-weight:bold">阈值2</span>',
	    tooltip:'可编辑列',
		dataIndex : 'threshold2',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'threshold2Editor',
			allowBlank:false
		})
	}, {
		id : 'threshold3',
		header: '<span style="font-weight:bold">阈值3</span>',
	    tooltip:'可编辑列',
		dataIndex : 'threshold3',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'threshold3Editor',
			allowBlank:false
		})
	}, {
		id : 'filterValue',
		header: '<span style="font-weight:bold">过滤值</span>',
	    tooltip:'可编辑列',
		dataIndex : 'filterValue',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'filterValueEditor',
			allowBlank:false
		})
	}]
});
var physicalStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getPhysical.action',
	pruneModifiedRecords:true,
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "pmTemplateInfoId", "pmStdIndex", "pmDesc", "factory", "unit", "offset", "upperValue",
			"lowerValue", "upperOffset", "lowerOffset", "domain", "upper", "lower" ])
});
var physicalCm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked : true
	}),  {
		id : 'pmStdIndex',
		header : '性能事件',
		dataIndex : 'pmStdIndex',
		width :120
	},{
		id : 'domain',
		header : '性能类型',
		dataIndex : 'domain',
		width :120,
		renderer:domainRenderer
	}, {
		id : 'pmDesc',
		header : '描述',
		dataIndex : 'pmDesc',
		width : 120
	}, {
		id : 'factory',
		header : '厂家',
		dataIndex : 'factory',
		width : 120,
		renderer:transFactoryName
	}, {
		id : 'unit',
		header : '单位',
		dataIndex : 'unit',
		width : 120
	},{
		id : 'offset',
		header: '<span style="font-weight:bold">基准值偏差</span>',
	    tooltip:'可编辑列',
		dataIndex : 'offset',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'offsetEditor',
			allowBlank:false
		})
	}, 
//	{
//		id : 'upperValue',
//		header : '上限值',
//		dataIndex : 'upperValue',
//		width : 120,
//		editor:new Ext.form.NumberField({
//			id:'upperValueEditor',
//			allowBlank:false
//		})
//	}, {
//		id : 'lowerValue',
//		header : '下限值',
//		dataIndex : 'lowerValue',
//		width : 120,
//		editor:new Ext.form.NumberField({
//			id:'lowerValueEditor',
//			allowBlank:false
//		})
//	}, 
	{
		id : 'upper',
		header : '<span style="font-weight:bold">标称上限</span>',
		tooltip : '可编辑列',
		dataIndex : 'upper',
		width : 120,
		editor : new Ext.form.NumberField({
			allowBlank : false
		})
	}, {
		id : 'upperOffset',
		header: '<span style="font-weight:bold">上限值偏差</span>',
	    tooltip:'可编辑列',
		dataIndex : 'upperOffset',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'upperOffsetEditor',
			allowBlank:false
		})
	}, {
		id : 'lower',
		header : '<span style="font-weight:bold">标称下限</span>',
		tooltip : '可编辑列',
		dataIndex : 'lower',
		width : 120,
		editor : new Ext.form.NumberField({
			allowBlank : false
		})
	}, {
		id : 'lowerOffset',
		header: '<span style="font-weight:bold">下限值偏差</span>',
	    tooltip:'可编辑列',
		dataIndex : 'lowerOffset',
		width : 120,
		editor:new Ext.form.NumberField({
			id:'lowerOffsetEditor',
			allowBlank:false
		})
	}]
});

var gridPanel = new Ext.grid.EditorGridPanel({
	region : "center",
	frame : false,
	store : numbericStore,
	loadMask:true,
	cm : numbericCm,
	tbar : [ '-',"数据类型:", dataKindCombo, '-',{
		text : '保存',
		id:'saveNumberic',
		icon : '../../../resource/images/btnImages/disk.png',
		handler : saveNumberic
	},{
		text : '保存',
		id:'savePhysical',
		icon : '../../../resource/images/btnImages/disk.png',
		handler : savePhysical
	} ],
	listeners : {
		'beforeedit' : function(e) {
			var pmStdInd = e.record.get("pmStdIndex");
			if (pmStdInd == "FEC_BEF_COR_ER" || pmStdInd == "FEC_AFT_COR_ER") {
				this.getColumnModel().setEditor(e.column, new Ext.form.TextField({
					editable : true,
					allowBlank : false,
					regex : /^1E-(\d)+$/
				}));
			} else {
				this.getColumnModel().setEditor(e.column, new Ext.form.NumberField({
					allowBlank : false
				}));
			}
		}
	}
});

function getNumberic(){
	numbericStore.baseParams = {"templateId":newId};
	numbericStore.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "计数值详情加载失败");
		}
	});
}

function getPhysical(){
	physicalStore.baseParams = {"templateId":newId};
	physicalStore.load({
		callback : function(records, options, success) {
			if (!success)
				Ext.Msg.alert("提示", "物理量详情加载失败");
		}
	});
}

function saveNumberic(){
	var records = numbericStore.getModifiedRecords();
	var list = new Array();
	if (records.length > 0) {
		for ( var i = 0; i < records.length; i++) {
			var numberic = {
				"pmTemplateInfoId" : records[i].get("pmTemplateInfoId"),
				"threshold1" : records[i].get("threshold1"),
				"threshold2" : records[i].get("threshold2"),
				"threshold3" : records[i].get("threshold3"),
				"filterValue" : records[i].get("filterValue")
			};
			list.push(Ext.encode(numberic));
		}
		var saveParams = {
			"modifyList" : list
		};
		Ext.Ajax.request({
			url : 'regular-pm-analysis!saveNumberic.action',
			params : saveParams,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					// 提交修改，不然store.getModifiedRecords();数据会累加
					numbericStore.commitChanges();
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
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}

function savePhysical(){
	var records = physicalStore.getModifiedRecords();
	var list = new Array();
	if (records.length > 0) {
		for ( var i = 0; i < records.length; i++) {
			var numberic = {
					"pmTemplateInfoId" : records[i].get("pmTemplateInfoId"),
					"offset" : records[i].get("offset"),
				"upper" : records[i].get("upper"),
				"lower" : records[i].get("lower"),
					"upperOffset" : records[i].get("upperOffset"),
					"lowerOffset" : records[i].get("lowerOffset")
			};
			list.push(Ext.encode(numberic));
		}
		var saveParams = {
				"modifyList" : list
		};
		Ext.Ajax.request({
			url : 'regular-pm-analysis!savePhysical.action',
			params : saveParams,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					Ext.Msg.alert("提示", result.returnMessage);
					// 提交修改，不然store.getModifiedRecords();数据会累加
					physicalStore.commitChanges();
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
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}

function transFactoryName(v){
	for(var fac in FACTORY){
		if(v==FACTORY[fac]['key']){
    		return FACTORY[fac]['value'];
    	}
	}
	return v;
}

function domainRenderer(v){
	switch(v){
	case 1: return 'SDH';break;
	case 2: return 'WDM';break;
	case 3: return 'ETH';break;
	case 4: return 'ATM';break;
	default:return '';
}
}

Ext.onReady(function() {
			Ext.Msg = top.Ext.Msg;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			getNumberic();
			Ext.getCmp('savePhysical').setVisible(false)
			var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [ gridPanel ],
				renderTo : Ext.getBody()
			});
		});