netLevelComboField.emptyText="全部";
projectComboGridField.allowBlank=false;
//projectComboGridField.emptyText="全部";
var win;
// ================stores===================
var store = new Ext.data.Store({
	url : 'fiber-link-evaluate!searchFiberLink.action',
	baseParams : {'limit':200},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ {name:"linkId",mapping:"RESOURCE_LINK_ID"}, 
	     {name:"collectDate",mapping:"COLLECT_DATE"},
	     {name:"length",mapping:"LENGTH"},
	     {name:"attCoefficientTheory",mapping:"ATT_COEFFICIENT_THEORY"},
	     {name:"attCoefficientBuild",mapping:"ATT_COEFFICIENT_BUILD"},
	     {name:"attCoefficientExperience",mapping:"ATT_COEFFICIENT_EXPERIENCE"},
	     {name:"workRange",mapping:"WORK_RANGE"},
	     {name:"standardAttenuation",mapping:"ATT_STD"},
	     
	     {name:"projectName",mapping:"PROJECT_NAME"},
	     {name:"linkName",mapping:"LINK_NAME"},
	     {name:"offsetLevel",mapping:"OFFSET_LEVEL"},
	     {name:"attenuationValue",mapping:"ATT_VALUE"},
	     {name:"offsetAttenuation",mapping:"ATT_OFFSET"},
	     {name:"balanceDegree",mapping:"BALANCE_DEGREE"},
	     {name:"attCoefficient",mapping:"ATT_COEFFICIENT"},
	     {name:"att",mapping:"ATT"},
	     
	     /*{name:"zGroupName",mapping:"zGroupName"},
	     {name:"zEmsName",mapping:"zEmsName"},
	     {name:"zSubnetName",mapping:"zSubnetName"},*/
	     {name:"zNe",mapping:"Z_NE_NAME"},
	     /*{name:"zAreaName",mapping:"zAreaName"},*/
	     {name:"zStationName",mapping:"Z_END_STATION"},
	     /*{name:"zProductName",mapping:"zProductName"},
	     {name:"zPtpId",mapping:"Z_END_PTP_ID"},*/
	     {name:"zPtp",mapping:"Z_PTP_NAME"},
	     /*{name:"zPtpType",mapping:"zPtpType"},
	     {name:"zRate",mapping:"zRate"},
	     {name:"aGroupName",mapping:"aGroupName"},
	     {name:"aEmsName",mapping:"aEmsName"},
	     {name:"aSubnetName",mapping:"aSubnetName"},*/
	     {name:"aNe",mapping:"A_NE_NAME"},
	     /*{name:"aAreaName",mapping:"aAreaName"},*/
	     {name:"aStationName",mapping:"A_END_STATION"},
	     /*{name:"aProductName",mapping:"aProductName"},
	     {name:"aPtpId",mapping:"A_END_PTP_ID"},*/
	     {name:"aPtp",mapping:"A_PTP_NAME"},
	     /*{name:"aPtpType",mapping:"aPtpType"},
	     {name:"aRate",mapping:"aRate"},*/
	     {name:"sendOP",mapping:"SEND_OP"},
	     {name:"recOP",mapping:"REC_OP"},
	     
	     {name:"aPtpOsc",mapping:"A_OSC_PTP_NAME"},
	     {name:"zPtpOsc",mapping:"Z_OSC_PTP_NAME"},
	     {name:"attOsc",mapping:"ATT_OSC"},
	     {name:"attenuationValueOsc",mapping:"ATT_VALUE_OSC"},
	     {name:"offsetAttenuationOsc",mapping:"ATT_OFFSET_OSC"},
	     {name:"attCoefficientOsc",mapping:"ATT_COEFFICIENT_OSC"},
	     {name:"sendOPOsc",mapping:"SEND_OP_OSC"},
	     {name:"recOPOsc",mapping:"REC_OP_OSC"},
	     {name:"offsetLevelOsc",mapping:"OFFSET_LEVEL_OSC"},
	     {name:"balanceDegreeOsc",mapping:"BALANCE_DEGREE_OSC"},
	     
	     {name:"RESOURCE_CABLES_ID",mapping:"RESOURCE_CABLES_ID"}, 
	     {name:"RESOURCE_CABLE_ID",mapping:"RESOURCE_CABLE_ID"}, 
	     {name:"RESOURCE_FIBER_ID",mapping:"RESOURCE_FIBER_ID"}, 
	     {name:"cables",convert:function(v,record){if(record["CABLES_NO"]) return record["CABLES_NAME"]+"("+record["CABLES_NO"]+")";}},
	     {name:"cable",convert:function(v,record){if(record["CABLE_NO"]) return record["CABLE_NAME"]+"("+record["CABLE_NO"]+")";}},
	     {name:"fiberNo",mapping:"FIBER_NO"}]),
	listeners:{
		"exception": function(proxy,type,action,options,response,arg){
			Ext.Msg.alert("提示","搜索出错"+
					"<BR>Status:"+response.statusText||"unknow");
		}
	}
});

// ==========================page=============================
var rows=[[{header:''},
	       {header:''},
	       {header:''},
	       {header:''},
	       {header:''},
	       {header:''},
	       {header: '质量等级',align: 'center',colspan: 2},
	       {header:''},
	       {header:''},
	       {header: '衰耗(dB)', align: 'center', colspan: 3},
	       {header: '衰耗偏差ΔA(dB)', align: 'center', colspan: 2},
	       {header: '平衡度(dB)', align: 'center', colspan: 2},
	       {header: '衰耗系数α(dB/km)', align: 'center', colspan: 5},
	       {header: '起点', align: 'center', colspan: 3},
	       {header: '发送光功率(dBm)', align: 'center', colspan: 2},
	       {header: '终点', align: 'center', colspan: 3},
	       {header: '接收光功率(dBm)', align: 'center', colspan: 2},
	       {header:''},
	       {header: '衰耗器', align: 'center', colspan: 2},
	       {header:''},
	       {header:''},
	       {header:''}]]
;
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
    header:'',singleSelect:true});

var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : "fiberLinkEvaluateGridId",
	rows: rows,
	columns : [new Ext.grid.RowNumberer({
		width:26,
		locked:true
	}),checkboxSelectionModel,{
		id : 'projectName',
		header : '系统名称',
		dataIndex : 'projectName',
		width : 80,
		locked:true
	}, {
		id : 'aStationName',
		header : '起点站',
		dataIndex : 'aStationName',
		width : 60,
		locked:true
	}, {
		id : 'zStationName',
		header : '终点站',
		dataIndex : 'zStationName',
		width : 60,
		locked:true
	}, {
		id : 'linkName',
		header : '链路名称',
		dataIndex : 'linkName',
		width : 80,
		locked:true
	}, {
		id : 'offsetLevel',
		header : '主信号',
		dataIndex : 'offsetLevel',
		width : 60,
		renderer : colorGrid,
		locked:true
	}, {
		id : 'offsetLevelOsc',
		header : 'OSC信号',
		dataIndex : 'offsetLevelOsc',
		width : 60,
		renderer : colorGrid,
		locked:true
	}, {
		id : 'collectDate',
		header : '日期',
		dataIndex : 'collectDate',
		width : 80,
		locked:true
	}, {
		id : 'length',
		header : '距离(km)',
		dataIndex : 'length',
		width : 60
	}, {
		id : 'attenuationValue',
		header : '主信号',
		dataIndex : 'attenuationValue',
		width : 60
	}, {
		id : 'attenuationValueOsc',
		header : 'OSC信号',
		dataIndex : 'attenuationValueOsc',
		width : 60
	}, {
		id : 'standardAttenuation',
		header : '基准值',
		dataIndex : 'standardAttenuation',
		width : 60
	}, {
		id : 'offsetAttenuation',
		header : '主信号',
		dataIndex : 'offsetAttenuation',
		width : 60
	}, {
		id : 'offsetAttenuationOsc',
		header : 'OSC信号',
		dataIndex : 'offsetAttenuationOsc',
		width : 60
	}, {
		id : 'balanceDegree',
		header : '主信号',
		dataIndex : 'balanceDegree',
		width : 60
	}, {
		id : 'balanceDegreeOsc',
		header : 'OSC信号',
		dataIndex : 'balanceDegreeOsc',
		width : 60
	}, {
		id : 'attCoefficient',
		header : '主信号',
		dataIndex : 'attCoefficient',
		width : 60
	}, {
		id : 'attCoefficientOsc',
		header : 'OSC信号',
		dataIndex : 'attCoefficientOsc',
		width : 60
	}, {
		id : 'attCoefficientTheory',
		header : '理论值',
		dataIndex : 'attCoefficientTheory',
		width : 60
	}, {
		id : 'attCoefficientBuild',
		header : '竣工值',
		dataIndex : 'attCoefficientBuild',
		width : 60
	}, {
		id : 'attCoefficientExperience',
		header : '经验值',
		dataIndex : 'attCoefficientExperience',
		width : 60
	}, {
		id : 'aNe',
		header : '网元',
		dataIndex : 'aNe',
		width : 60
	}, {
		id : 'aPtp',
		header : '主信号端口',
		dataIndex : 'aPtp',
		width : 150
	}, {
		id : 'aPtpOsc',
		header : 'OSC信号端口',
		dataIndex : 'aPtpOsc',
		width : 150
	}, {
		id : 'sendOP',
		header : '主信号',
		dataIndex : 'sendOP',
		width : 60
	}, {
		id : 'sendOPOsc',
		header : 'OSC信号',
		dataIndex : 'sendOPOsc',
		width : 60
	}, {
		id : 'zNe',
		header : '网元',
		dataIndex : 'zNe',
		width : 60
	}, {
		id : 'zPtp',
		header : '主信号端口',
		dataIndex : 'zPtp',
		width : 150
	}, {
		id : 'zPtpOsc',
		header : 'OSC信号端口',
		dataIndex : 'zPtpOsc',
		width : 150
	}, {
		id : 'recOP',
		header : '主信号',
		dataIndex : 'recOP',
		width : 60
	}, {
		id : 'recOPOsc',
		header : 'OSC信号',
		dataIndex : 'recOPOsc',
		width : 60
	}, {
		id : 'workRange',
		header : '工作窗口(nm)',
		dataIndex : 'workRange',
		width : 80
	}, {
		id : 'att',
		header : '主信号',
		dataIndex : 'att',
		width : 60
	}, {
		id : 'attOsc',
		header : 'OSC信号',
		dataIndex : 'attOsc',
		width : 60
	}, {
		id : 'cables',
		header : '光缆',
		dataIndex : 'cables',
		width : 4*15
	}, {
		id : 'cable',
		header : '光缆段',
		dataIndex : 'cable',
		width : 6*15
	}, {
		id : 'fiberNo',
		header : '光纤芯号',
		dataIndex : 'fiberNo',
		width : 4*15
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

var offsetLevelCombox;

(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'offsetLevelValue'
		}, {
			name : 'offsetLevelDisplay'
		} ],
		data : [ [ 0, '全部' ], [ 1, 'A级' ], [ 2, 'B级' ], [ 3, 'C级' ],[ 4, 'D级' ] ]
	});
	offsetLevelCombox = new Ext.form.ComboBox({
		id : 'offsetLevelCombox',
//		fieldLabel : '质量等级',
//		labelWidth : 60,
//		labelSeparator : "：",
		width: 100,
		store : store,
		valueField : 'offsetLevelValue',
		displayField : 'offsetLevelDisplay',
		mode : 'local',
		triggerAction : 'all',
//		anchor : '95%',
		value: 0,
		allowBlank : false,
		resizable: true,
		listeners : {
			//过滤数据
			select : function(combo, record, index) {
				gridPanel.store.filterBy(function(record, id) {
					if (combo.getValue() != 0) {
						if (record.get('offsetLevel') == combo.getValue()||
							record.get('offsetLevelOsc') == combo.getValue()) {
							return true;
						} else
							return false;
					}else{
						return true;
					}
				});
			}
		}
	});
})();

var offsetPanel = new Ext.form.FormPanel({
	id : 'offsetPanel',
	bodyStyle:'padding:5px 5px 5px 5px',
//	height : 80,
	autoScroll : true,
	forceLayout : true,
	collapsible : false,
	labelWidth : 40,
	items : [{
        xtype: 'compositefield',
        fieldLabel: '阈值1',
        items: [{
	        	xtype:'numberfield',
	            id: 'downOffset',
	            name: 'downOffset',
	            width:75,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2
        },{
               xtype: 'displayfield',
               value: 'dB'
        }]
    },{
        xtype: 'compositefield',
        fieldLabel: '阈值2',
        items: [{
	            xtype:'numberfield',
	            id: 'middleOffset',
	            name: 'middleOffset',
//	            fieldLabel: '二级',
	            width:75,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2
        },{
               xtype: 'displayfield',
               value: 'dB'
        }]
    },{
        xtype: 'compositefield',
        fieldLabel: '阈值3',
        items: [{
	            xtype:'numberfield',
	            id: 'upperOffset',
	            name: 'upperOffset',
//	            fieldLabel: '二级',
	            width:75,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2
        },{
               xtype: 'displayfield',
               value: 'dB'
        }]
    }]
});

var offsetWindow = new Ext.Window({
	id : 'offsetWindow',
	title : '等级设置',
	width : 200,
	height : 160,
	layout: 'fit',
	//isTopContainer : true,
	//modal : true,
	autoScroll : true,
	closeAction: 'hide',
	items:[offsetPanel],
    buttonAlign:"center",
    buttons: [{
    	text : '确定', 
		handler : modifyOffsetValue
    },{
    	text : '取消', 
    	handler: function(){
    		offsetWindow.hide();
    	}
    }]
	
//	html : '<iframe src = "addMultiSection.jsp" height="100%" width="100%" frameBorder=0/>'
});

var setOffsetButton = new Ext.Button({
	text : '等级设置',
	privilege:modAuth,
	icon : '../../../resource/images/btnImages/config_add.png',
	handler : function(){
		offsetWindow.show();
	}
});

var levelADisplay = new Ext.form.DisplayField({
	id: 'levelADisplay',
    name: 'levelADisplay'
});

var levelBDisplay = new Ext.form.DisplayField({
	id: 'levelBDisplay',
    name: 'levelBDisplay'
});

var levelCDisplay = new Ext.form.DisplayField({
	id: 'levelCDisplay',
    name: 'levelCDisplay'
});

var levelDDisplay = new Ext.form.DisplayField({
	id: 'levelDDisplay',
    name: 'levelDDisplay'
});

var oneTbar = new Ext.Toolbar({
	id : 'oneTbar',
	items : [ '-', "质量等级：", offsetLevelCombox,"-",setOffsetButton,'-',
	  levelADisplay,{
		xtype : 'tbspacer',
		width : 10
	},levelBDisplay,{
		xtype : 'tbspacer',
		width : 10
	},levelCDisplay,{
		xtype : 'tbspacer',
		width : 10
	},levelDDisplay,'-']
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	colModel : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	plugins:[new Ext.ux.plugins.LockedGroupHeaderGrid()],
	//enableColumnMove : false,
	//enableColLock : false,
	//enableHdMenu : false,
	forceFit : false,
	stateId : "fiberLinkEvaluateGridId",
	stateful : true,
	bbar : pageTool,
	tbar : ['-',netLevelComboBar,
	        '-',projectComboGridBar, 
		'-', "日期：", {
		xtype : 'textfield',
		id : 'collectDate',
		name : 'collectDate',
//		fieldLabel : '日期',
		width : 100,
		anchor : '95%',
		cls : 'Wdate',
//		emptyText : '默认最近日期',
		value: endDate?endDate:getDayBefore(),
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "collectDate",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd',
					autoPickDate : true,
//					maxDate : '%y-%M-%d',
					highLineWeekDay:true
				});
				this.blur();
			}
		}
	}, '-', {
		id : 'reset',
		text : '重置',
		handler : function() {
			netLevelComboBar.getForm().reset();
			projectComboGridBar.getForm().reset();
			Ext.getCmp('collectDate').reset();
		}
	}, {
        id: 'search',
        text: '查询',
        privilege:viewAuth,
        icon : '../../../resource/images/btnImages/search.png',
        handler : linkSearch
    }, '-', {
		text : '筛选条件',
		id : 'searchCond',
		enableToggle: true,
		icon : '../../../resource/images/btnImages/getChecked.gif',
		handler : function() {
			if (!oneTbar.hidden) {
				Ext.getCmp('oneTbar').setVisible(false);
			} else {
				Ext.getCmp('oneTbar').setVisible(true);
			}
			gridPanel.syncSize();
			win.doLayout();
		}
	}, '-', {
		text : '趋势图',
		icon : '../../../resource/images/btnImages/chart.png',
		handler : performanceDiagram
	}],
	listeners : {
		render : function() {
			oneTbar.render(this.tbar); // add one tbar
			oneTbar.hide();
		},
		destroy : function() {
			Ext.destroy(oneTbar);// 这一句不加可能会有麻烦滴
		}
	}
});

// -----------------------------------------init the
// page--------------------------------------------

// ************************************************************************************
function linkSearch() {
	if(!projectComboGridField.isValid()){
		return;
	}
	var netLevel = netLevelComboField.getValue();
	var project = projectComboGridField.comboGrid.getValue();
	var collectDate = Ext.getCmp('collectDate').getValue();
	var jsonData = {
		"netLevel" : netLevel,
		"transSysId" : project,
		"collectDate" : collectDate,
		"limit" : 200
	};

	store.baseParams = jsonData;

	store.load();
}

function modifyOffsetValue(){
	
	var upper = Ext.getCmp('upperOffset').getValue();
	var middle = Ext.getCmp('middleOffset').getValue();
	var down = Ext.getCmp('downOffset').getValue();
	
	if(middle<=down){
		Ext.Msg.alert('信息', '二级值必须大于一级值！');
		return;
	}
	if(upper<=middle){
		Ext.Msg.alert('信息', '三级值必须大于二级值！');
		return;
	}
	offsetWindow.hide();
	var jsonData = {
		"downOffset" : down,
		"middleOffset" : middle,
		"upperOffset" : upper
	};
	Ext.Ajax.request({
		url : 'fiber-link-evaluate!modifyOffsetValue.action',
		method : "POST",
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert('信息', '修改成功！',function(){
					initOffsetValue();
					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				});
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}

function performanceDiagram(){
	var record=gridPanel.getSelectionModel().getSelected();
	if(record&&record.get("linkId")){
		var param={
			linkId:record.get("linkId"),
			endDate:record.get("collectDate")
		};
		top.addTabPage("../evaluateManager/fiberLinkEvaluate/performanceDiagram.jsp?"+Ext.urlEncode(param), "光纤链路趋势图",authSequence,false);
	}else{
		Ext.Msg.alert("提示", '请选择一条记录');
	}
}

function colorGrid(v, m, r) {
	if(v == 1){
		m.css = 'x-grid-background-green';
		return 'A级';
	}
	else if(v == 2){
		m.css = 'x-grid-background-blue';
		return 'B级';
	}
	else if(v == 3){
		m.css = 'x-grid-background-yellow';
		return 'C级';
	}
	else if(v == 4){
		m.css = 'x-grid-background-red';
		return 'D级';
	}
	return v;
}

function initOffsetValue(){
	Ext.Ajax.request({
		url : 'fiber-link-evaluate!getOffsetValue.action',
		method : 'POST',
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			var upper = obj.upperOffset;
			var middle = obj.middleOffset;
			var down = obj.downOffset;
			
			Ext.getCmp('upperOffset').setValue(upper);
			Ext.getCmp('middleOffset').setValue(middle);
			Ext.getCmp('downOffset').setValue(down);
			
			Ext.getCmp('levelADisplay').setValue("A级：|ΔA|≤"+down);
			Ext.getCmp('levelBDisplay').setValue("B级："+down+"<|ΔA|≤"+middle);
			Ext.getCmp('levelCDisplay').setValue("C级："+middle+"<|ΔA|≤"+upper);
			Ext.getCmp('levelDDisplay').setValue("D级："+"|ΔA|>"+upper);
			
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}
function init(){
	initOffsetValue();
	if(linkId){
		var jsonData = {
			"linkId" : linkId,
			"collectDate" : endDate,
			"limit" : 200
		};
		store.baseParams = jsonData;
		store.load();
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";

	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel],
		renderTo : Ext.getBody()
	});
	win.show();
	init();
});