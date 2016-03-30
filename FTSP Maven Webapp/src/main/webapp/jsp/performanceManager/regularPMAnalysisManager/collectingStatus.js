/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
var keySet;
var str = "default";
var MAX_DAY = 20;
var fields = ["EMS_DISPLAY_NAME", "SUBNET_DISPLAY_NAME", "BASE_NE_ID", "DISPLAY_NAME", "TYPE",
		"PRODUCT_NAME", "ACTION_RESULT", "COLLECT_RESULT"];
var dyColIds = ['status0', 'status1', 'status2', 'status3', 'status4', 'status5',
        		'status6', 'status7', 'status8', 'status9', 'status10', 'status11', 'status12',
        		'status13', 'status14', 'status15', 'status16', 'status17', 'status18', 'status19']
/*
 * var d = {rows:[{ "EMS_DISPLAY_NAME" : "一干_兰成波分", "DISPLAY_NAME" : "兰州雁滩",
 * "TYPE" : 2, "PRODUCT_NAME" : "ZXWM M900(100M)", "status0":1,
 * "status0data":"采集方式:循环采<br/>采集时间:2014-12-01 23:34:45<br/>项数:23",
 * "status1":0, "status1data":"采集方式:循环采<br/>采集时间:2014-12-01 23:36:15<br/>失败原因:接口错误：网管获取数据异常！",
 * "status2":0, "status2data":"采集方式:循环采<br/>采集时间:2014-12-01 23:36:15<br/>失败原因:接口错误：网管获取数据异常！" }, {
 * "EMS_DISPLAY_NAME" : "一干_兰成波分", "DISPLAY_NAME" : "榆中", "TYPE" : 2,
 * "PRODUCT_NAME" : "ZXWM M900(100M)", "status0":1, "status0data":"",
 * "status1":1, "status1data":"", "status2":1, "status2data":"" }, {
 * "EMS_DISPLAY_NAME" : "一干_兰成波分", "DISPLAY_NAME" : "定西", "TYPE" : 2,
 * "PRODUCT_NAME" : "ZXWM M900(100M)", "status0":1, "status0data":"",
 * "status1":1, "status1data":"", "status2":1, "status2data":"" }, {
 * "EMS_DISPLAY_NAME" : "一干_兰成波分", "DISPLAY_NAME" : "通渭", "TYPE" : 2,
 * "PRODUCT_NAME" : "ZXWM M900(100M)", "status0":1, "status0data":"",
 * "status1":0, "status1data":"", "status2":0, "status2data":"" }, {
 * "EMS_DISPLAY_NAME" : "一干_兰成波分", "DISPLAY_NAME" : "泰安", "TYPE" : 2,
 * "PRODUCT_NAME" : "ZXWM M900(100M)", "status0":3, "status0data":"采集方式:不采集",
 * "status1":1, "status1data":"", "status2":0, "status2data":"" }],total:5} ;
 */
var d = {"total":3,"rows":[{"DISPLAY_NAME":"3-新华","PRODUCT_NAME":"OptiX OSN 8800 T32","2014-12-13hover":"无计划","EMS_DISPLAY_NAME":"二干_西信波分","2014-12-15hover":"无计划","BASE_NE_ID":1437,"2014-12-16":2,"2014-12-15":2,"2014-12-14hover":"无计划","TYPE":3,"2014-12-14":2,"2014-12-13":2,"2014-12-16hover":"无计划"},{"DISPLAY_NAME":"1-西信","PRODUCT_NAME":"OptiX OSN 8800 T32","2014-12-13hover":"采集方式:循环采<br\/>采集时间:2014-12-13 11:50:59<br\/>失败原因:接口错误：未知错误！","EMS_DISPLAY_NAME":"二干_西信波分","2014-12-15hover":"采集方式:循环采<br\/>采集时间:2014-12-15 11:50:59<br\/>项数:11项","BASE_NE_ID":1436,"2014-12-16":0,"2014-12-15":1,"2014-12-14hover":"采集方式:循环采<br\/>采集时间:2014-12-14 11:54:00<br\/>失败原因:接口错误：未知错误！","TYPE":3,"2014-12-14":0,"2014-12-13":0,"2014-12-16hover":"采集方式:循环采<br\/>采集时间:2014-12-16 11:50:59<br\/>失败原因:接口错误：未知错误！"},{"DISPLAY_NAME":"2-贝森","PRODUCT_NAME":"OptiX OSN 8800 T32","2014-12-13hover":"采集方式:循环采<br\/>采集时间:2014-12-13 11:50:59<br\/>失败原因:接口错误：未知错误！","EMS_DISPLAY_NAME":"二干_西信波分","2014-12-15hover":"采集方式:循环采<br\/>采集时间:2014-12-15 11:50:59<br\/>失败原因:接口错误：未知错误！","BASE_NE_ID":1435,"2014-12-16":0,"2014-12-15":0,"2014-12-14hover":"采集方式:循环采<br\/>采集时间:2014-12-14 11:51:59<br\/>失败原因:接口错误：未知错误！","TYPE":3,"2014-12-14":0,"2014-12-13":0,"2014-12-16hover":"采集方式:循环采<br\/>采集时间:2014-12-16 11:50:59<br\/>失败原因:接口错误：未知错误！"}]};
var store = new Ext.data.JsonStore({
	url : 'regular-pm-analysis!getNeStateListMulti.action',
	fields:fields,
	totalProperty : 'total',
	root : "rows",
});
//store.loadData(d);
var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});
var renderer = function(v,m,record,rowIndex,colIndex){
	var r = record.data;
	var str;
	if(v===0){
		m.attr = 'style="background:#FF6666;"'; //COLLECT_RESULT
		var str = record.get(cm.getDataIndex(colIndex)+"hover").split("失败原因:");
		return '失败('+str[str.length-1]+")";
	}else if(v==1){
		m.attr = 'style="background:#99CC66;"';
		var str = record.get(cm.getDataIndex(colIndex)+"hover").split(":");
		return '成功('+str[str.length-1]+")";
	}else if(v==2){
		m.attr = 'style="background:white;"';
		return '无计划';
	}
}; 

// ************************* 任务信息列模型 ****************************
// var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
	singleSelect : true
});
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : "collectingStatusGridId",
	columns : [
			new Ext.grid.RowNumberer({
				width : 26,
				locked:true
			}),checkboxSelectionModel,
			{
				id : 'EMS_DISPLAY_NAME',
				header : '网管',
				dataIndex : 'EMS_DISPLAY_NAME',
				locked:true,
				width : 150
			},{
				id : 'SUBNET_DISPLAY_NAME',
				header : '子网',
				dataIndex : 'SUBNET_DISPLAY_NAME',
				locked:true,
				width : 120
			},
			{
				id : 'DISPLAY_NAME',
				header : '网元',
				dataIndex : 'DISPLAY_NAME',
				locked:true,
				width : 180
			},
			{
				id : 'TYPE',
				header : '类型',
				dataIndex : 'TYPE',
				locked:true,
				width : 50,
				renderer : function(v) {
					if (v == 1) {
						return "SDH";
					} else if (v == 2) {
						return "WDM";
					} else if (v == 3) {
						return "OTN";
					} else if (v == 4) {
						return "PTN";
					} else {
						return "";
					}
				}
			},
			{
				id : 'PRODUCT_NAME',
				header : '型号',
				locked:true,
				dataIndex : 'PRODUCT_NAME',
				width : 150
			}, {
				id : 'status0',
				header : 'default',
				dataIndex : 'status0',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			
			}, {
				id : 'status1',
				header : 'default',
				dataIndex : 'status1',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status2',
				header : 'default',
				dataIndex : 'status2',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status3',
				header : 'default',
				dataIndex : 'status3',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status4',
				header : 'default',
				dataIndex : 'status4',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status5',
				header : 'default',
				dataIndex : 'status5',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status6',
				header : 'default',
				dataIndex : 'status6',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status7',
				header : 'default',
				dataIndex : 'status7',
				hidden : true,
				hideable:false,
				width : 120,
				renderer:renderer
			}, {
				id : 'status8',
				header : 'default',
				dataIndex : 'status8',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status9',
				header : 'default',
				dataIndex : 'status9',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status10',
				header : 'default',
				dataIndex : 'status10',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status11',
				header : 'default',
				dataIndex : 'status11',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status12',
				header : 'default',
				dataIndex : 'status12',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status13',
				header : 'default',
				dataIndex : 'status13',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status14',
				header : 'default',
				dataIndex : 'status14',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status15',
				header : 'default',
				dataIndex : 'status15',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status16',
				header : 'default',
				dataIndex : 'status16',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status17',
				header : 'default',
				dataIndex : 'status17',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status18',
				header : 'default',
				dataIndex : 'status18',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}, {
				id : 'status19',
				header : 'default',
				dataIndex : 'status19',
				hidden : true,
				hideable:false,
				width : 120,renderer:renderer
			}]
});


var typeCombo;
(function() {
	var searchStore = new Ext.data.ArrayStore({
		fields : [ 'value', 'display' ],
		data : [ [ 0, '全部' ], [ 1, 'SDH' ], [ 2, 'WDM' ], [ 3, 'OTN' ], [ 4, 'PTN' ] ]
	});
	typeCombo = new Ext.form.ComboBox({
		store : searchStore,
		valueField : 'value',
		displayField : 'display',
		mode : 'local',
		triggerAction : 'all',
		editable : false,
		width : 100,
		value : 0,
		listeners : {
			'select' : function(combo, record, index) {
				modelCombo.reset();
				var jsonData = {
					"searchCond.emsId" : emsId,
					"searchCond.type" : combo.getValue()
				};
				modelStore.baseParams = jsonData;
				modelStore.load({
					callback : function(records, options, success) {
						if (!success)
							Ext.Msg.alert("提示", "查询出错");
					}
				});
			}
		}
	});
})();

var modelStore = new Ext.data.Store({
	url : 'regular-pm-analysis!getProductNames.action',
	baseParams : {
		"searchCond.emsId" : emsId,
		"searchCond.type" : 0
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "PRODUCT_NAME" ])
});

var modelCombo = new Ext.form.ComboBox({
	store : modelStore,
	triggerAction : "all",
	editable : false,
	valueField : 'PRODUCT_NAME',
	displayField : 'PRODUCT_NAME',
	width : 100,
	value : '全部',
	resizable : true
});


var tree = new Ext.ux.EquipTreeCombo({
        xtype : "equiptreecombo",
        width : 100,
        listWidth : 200,
        rootVisible : true,
        emptyText:"全部",
        rootId: emsId,
    	rootType:CommonDefine.TREE.NODE.EMS,
        leafType : CommonDefine.TREE.NODE.SUBNET,
        checkableLevel : [3],
        fieldLabel : "子网",
        attrFormat : "text"
    });


var today = new Date();
var todayStr = today.format("yyyy-MM-dd");
today.setDate(today.getDate()-6);//这里写6，不然包括首尾日期实际上是8天
var lastWeekStr = today.format("yyyy-MM-dd");

//====开始日期====@
var startTime = {
	xtype : 'textfield',
	id : 'startTime',
	name : 'startTime',
	fieldLabel : '起始日期',
	value: lastWeekStr,
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	 width : 100,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "startTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
				minDate:  '%y-%M-#{%d-364}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startTime = Ext.getCmp('startTime').getValue();
//			var endTime = Ext.getCmp('endTime').getValue();
//			if(startTime&&endTime)
//				dayLimitCheck(startTime,endTime);
		}
	}
};

//====结束日期====@
var endTime = {
	xtype : 'textfield',
	id : 'endTime',
	name : 'endTime',
	fieldLabel : '结束日期',
	sideText : '<font color=red>*</font>',
	allowBlank : false,
	readOnly : true,
	value: todayStr,
	anchor : '95%',
	 width : 100,
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "endTime",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-%d',
				minDate:'#F{$dp.$D(\'startTime\',{d:0})}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
//			var startTime = Ext.getCmp('startTime').getValue();
//			var endTime = Ext.getCmp('endTime').getValue();
//			if(startTime&&endTime)
//				dayLimitCheck(startTime,endTime);
		}
	}
};

//检查日期跨度
function dayLimitCheck(start,end){
	var startTime = new Date(start.replace(/-/g, "/"));
	var endTime = new Date(end.replace(/-/g, "/"));
	if(endTime-startTime <0)
	{
	    Ext.Msg.alert("提示","结束时间不能早于开始时间！");
	    return false;
	}
	if((endTime-startTime)/1000/60/60/24 +1 >MAX_DAY)
	{
	    Ext.Msg.alert("提示","间隔时间不超过20天！");
	    return false;
	}
	return (endTime-startTime)/1000/60/60/24 + 1;
}

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	bbar : pageTool,
	enableColumnMove:false,
	loadMask : true,
	view : new Ext.ux.grid.LockingGridView(),
	stateId : "collectingStatusGridId",
//	stateful : true,
	 selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	// viewConfig : {
	// forceFit : true
	// },
	tbar : [ '-', "类型：", typeCombo, '-', "型号：", modelCombo, '-', "子网",
	         tree, '-', "计划日期：从：", startTime, '-', "到：", endTime,'-',
			{
				text:'查询',
				icon:'../../../resource/images/btnImages/search.png',
				handler: search
			},'-',
			{
				text:'查看长期记录',
				icon:'../../../resource/images/btnImages/search.png',
				handler: getLongTimeRecord
			} ]
});

gridPanel.on('render', function(grid) {
//    var store = grid.getStore();  // 获取 Store.
    var view = grid.getView();    // 获取 GridView.
    grid.tip = new Ext.ToolTip({
        target: view.mainBody,    // 全部的元素。
        delegate: '.x-grid3-cell', // 每一行都会触发其单独的显示和隐藏。
        trackMouse: true,         // 在行内移动时不会隐藏tip。
        renderTo: document.body,  // 立即渲染，使得tip.body可以
                                  //  在第一次显示时预先被引用。
        listeners: {              // 对于不同的触发元素
                                  //  显示不同的值。
            beforeshow: function updateTipBody(tip) {
                var rowIndex = view.findRowIndex(tip.triggerElement);
                var cellIndex = view.findCellIndex(tip.triggerElement);
                var colHeader = cm.getColumnHeader(cellIndex);
                var colDataIndex = cm.getDataIndex(cellIndex);
//                if(fields.indexOf(colDataIndex)!=-1)
//                	return false;
                tip.body.dom.innerHTML = store.getAt(rowIndex).get(colHeader+'hover');
            }
        }
    });
});

function search(){
	var start = Ext.getCmp('startTime').getValue();
	var end = Ext.getCmp('endTime').getValue();
	var type = typeCombo.getValue();
	var productName = modelCombo.getValue();
	var subnetId = tree.getCheckedNodes();
	var subnetIdStr='';
	if(!!subnetId)
		for(var i=0;i<subnetId.length;i++){
			if(i!=0){
				subnetIdStr+=",";
			}
				subnetIdStr+=subnetId[i].id.split('-')[1];
		}
//	alert(subnetIdStr);
	var dayCount = dayLimitCheck(start,end);
	if(!dayCount)
		return;
	
	columnControl(start,dayCount);
	keySet = keySet.concat(fields);
	store = new Ext.data.JsonStore({
		url : 'regular-pm-analysis!getNeStateListMulti.action',
		fields:keySet,
		totalProperty : 'total',
		root : "rows",
	});
	gridPanel.reconfigure(store,cm);
	pageTool.bindStore(store);
	var param = {
			"searchCond.startTime":start,
			"searchCond.endTime":end,
			"searchCond.emsId" : emsId,
			"searchCond.type" : type,
			"searchCond.productName" :productName,
			"searchCond.subnetIdStr" :subnetIdStr,
			"start":0,
			"limit":200
	};
	store.baseParams = param;
	store.load({
		callback : function(records, options, success) {
//			console.dir(records);
		}
	});
}

function columnControl(start,count){
	//清除一下store字段
	keySet = new Array();
	var i = 0;
	var time = new Date(start.replace(/-/g, "/"));
	var ms = time.getTime();
	for(i;i<count;i++){
		var y = time.getFullYear();
		var m = time.getMonth()+1;
		if(m<10){
			m = "0"+m;
		}
		var d = time.getDate();
		if(d<10){
			d = "0"+d;
		}
		header = y+"-"+m+"-"+d;
		ms+=  24 * 60 * 60 * 1000 ; 
		time = new Date(ms);
		cm.setColumnHeader(cm.getIndexById(dyColIds[i]),header);
		cm.setDataIndex(cm.getIndexById(dyColIds[i]),header);
		cm.setHidden(cm.getIndexById(dyColIds[i]),false);
		cm.getColumnAt(cm.getIndexById(dyColIds[i])).hideable = true;
		keySet.push(header);
		keySet.push(header+'hover');
	}
	for(i;i<MAX_DAY;i++){
		cm.setHidden(cm.getIndexById(dyColIds[i]),true);
		cm.getColumnAt(cm.getIndexById(dyColIds[i])).hideable = false;
	}
}
function getLongTimeRecord(){
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0 && cell.length < 2) {
		var url = '../../performanceManager/regularPMAnalysisManager/longTermRecord.jsp?neId=' + cell[0].get("BASE_NE_ID")
				+ '&taskId=' + cell[0].get("SYS_TASK_ID");
		var title = '网元长期记录('+cell[0].get('EMS_DISPLAY_NAME')+'-'+cell[0].get('DISPLAY_NAME')+')';
		
		var setTaskWin = new Ext.Window({
			id : 'longTermWindow',
			title : title,
			width : 850,
			height : 400,
			isTopContainer : true,
			modal : true,
			plain : true, // 是否为透明背景
			html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />',
			buttons : [ /*pauseBtn, */{
				text : '取消',
				handler : function() {
					setTaskWin.close();
				}
			} ]
		});
		setTaskWin.show();
		// if (setTaskWin.getHeight() > Ext.getCmp('win').getHeight()) {
		setTaskWin.setHeight(Ext.getCmp('win').getHeight() * 0.9);
		setTaskWin.setWidth(Ext.getCmp('win').getWidth() * 0.9);
		// }
		setTaskWin.center();
		setTaskWin.doLayout();
	} else {
		Ext.Msg.alert("提示", "请先选取网元！");
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : gridPanel
	});
	win.show();
	search();

//	store.load({
//		callback : function(records, options, success) {
//			if (!success)
//				Ext.Msg.alert("提示", "加载失败");
//		}
//	});
});
