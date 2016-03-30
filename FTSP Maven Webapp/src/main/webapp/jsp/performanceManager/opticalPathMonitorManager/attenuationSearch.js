/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

//Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";

//记录需要使用当前性能的linkId
var userCurrentPmLinkIds = new Array();
var westPanel;
(function() {
	var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		leafType : 4
	};
	var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	westPanel = new Ext.Panel({
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
		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
	});
})();

// ************************* 查询条件设置 ****************************
var time = new Ext.Panel({
    border : false,
    layout:'form',
    labelWidth : 60,
//    bodyStyle:'padding:5px 5px 5px 5px',
    items: [{
		xtype : 'compositefield',
		fieldLabel : '采集日期',
		anchor : '-20',
		labelSeparator : ":",
		defaults : {
			flex : 2
		},
		items : [ {
			xtype : 'textfield',
			id : 'collectDate',
			name : 'collectDate',
			fieldLabel : '开始时间',
			width : 150,
			anchor : '95%',
			cls : 'Wdate',
//			emptyText : '默认最近日期',
			// value:this.nowTime,
			listeners : {
				'focus' : function() {
					WdatePicker({
						el : "collectDate",
						isShowClear : false,
						readOnly : true,
						dateFmt : 'yyyy-MM-dd',
						autoPickDate : true,
						maxDate : '%y-%M-%d',
						highLineWeekDay:true
					});
					this.blur();
				}
			}
		}, {
			xtype : 'button',
			id : 'resetCollectDate',
			name : 'resetCollectDate',
			text : '重置',
			width : 45,
			handler : function() {
				Ext.getCmp('collectDate').setValue("");
			}
		} ]
	},{
        layout:'column',
        border : false,
        items:[{
            columnWidth:.25,
            layout: 'form',
            border : false,
            items: [{
                xtype:'textfield',
                id: 'linkId',
                name: 'linkId',
                fieldLabel: '链路编号',
                labelWidth : 60,
    			labelSeparator : ":",
                
                anchor:'95%'
            }]
        },{
            columnWidth:.25,
            layout: 'form',
			labelWidth : 60,
			labelSeparator : ":",
            border : false,
            items: [{
                xtype:'textfield',
                id: 'linkName',
                name: 'linkName',
                fieldLabel: '链路名称',
                anchor:'95%'
            }]
        },{
            columnWidth:.25,
            layout: 'form',
			labelWidth : 60,
			labelSeparator : ":",
            border : false,
            items: [{
                xtype:'button',
                id: 'search',
                text: '查询',
                privilege:viewAuth,
                width:60,
                icon : '../../../resource/images/btnImages/search.png',
                handler : linkSearch
            }]
        }]
    }]
});


// ************************* 查询条件 ****************************
var searchPanel = new Ext.form.FormPanel({
	id : 'searchPanel',
	region : "north",
	title : '查询条件',
	bodyStyle:'padding:5px 5px 5px 5px',
	height :85,
	autoScroll : true,
	collapsible : true,
	collapsed : false,
	items : [time],
	plugins : [ Ext.ux.PanelCollapsedTitle ]
});


// ================stores===================
var store = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "linkId", "linkName",
	     "zGroupName","zEmsName","zSubnetName","zNe","zAreaName","zStationName",
		  "zProductName","zEmsConnectionId","zPtpId","zPtp","zPtpType","zRate",
		  "aGroupName","aEmsName","aSubnetName","aNe","aAreaName","aStationName",
		  "aProductName","aEmsConnectionId","aPtpId","aPtp","aPtpType","aRate",
		  "sendOP","attenuationValue","standardAttenuation",
		  "offsetAttenuation","att","recOP","collectDate","offsetLevel"])
});

// ==========================page=============================
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
// {解决checkbox列无法锁定问题
checkboxSelectionModel.sortLock();
// }解决checkbox列无法锁定问题
var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : "attenuationSearchGridId",
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		locked:true
	}),checkboxSelectionModel,{
		id : 'linkId',
		header : '链路编号',
		dataIndex : 'linkId',
		width : 80
	}, {
		id : 'linkName',
		header : '链路名称',
		dataIndex : 'linkName',
		width : 100
	}, {
		id : 'attenuationValue',
		header : '光路衰耗(dB)',
		dataIndex : 'attenuationValue',
		width : 100,
		renderer : colorGrid
	}, {
		id : 'standardAttenuation',
		header : '衰耗基准值（dB）',
		dataIndex : 'standardAttenuation',
		// editor:new Ext.form.TextField({allowBalank:false}),
		width : 110
	}, {
		id : 'offsetAttenuation',
		header : '偏差值（dB）',
		dataIndex : 'offsetAttenuation',
		// editor:new Ext.form.TextField({allowBalank:false}),
		width : 90
	}, {
		id : 'att',
		header : '<span style="font-weight:bold">ATT(dB)</span>',
		dataIndex : 'att',
		editor : new Ext.form.NumberField({
			allowDecimals : true,
			allowNegative : true,
			decimalPrecision : 2
		// 精确到小数点后两位
		}),
		width : 80
	}, {
		id : 'collectDate',
		header : '采集日期',
		dataIndex : 'collectDate',
		width : 80
	},{
		id : 'aGroupName',
		header : 'A端网管分组',
		dataIndex : 'aGroupName',
		width : 100
	}, {
		id : 'aEmsName',
		header : 'A端网管',
		dataIndex : 'aEmsName',
		width : 100
	}, {
		id : 'aSubnetName',
		header : 'A端子网',
		dataIndex : 'aSubnetName',
		width : 100,
		hidden: true
	}, {
		id : 'aNe',
		header : 'A端网元',
		dataIndex : 'aNe',
		width : 100
	}, {
		id : 'aAreaName',
		header : 'A端'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'aAreaName',
		width : 100,
		hidden: true
	}, {
		id : 'aStationName',
		header : 'A端'+top.FieldNameDefine.STATION_NAME,
		dataIndex : 'aStationName',
		width : 100,
		hidden: true
	}, {
		id : 'aProductName',
		header : 'A端型号',
		dataIndex : 'aProductName',
		width : 100
	},{
		id : 'aPtp',
		header : 'A端端口',
		dataIndex : 'aPtp',
		width : 120
	}, {
		id : 'aPtpType',
		header : 'A端端口类型',
		dataIndex : 'aPtpType',
		width : 100
	}, {
		id : 'aRate',
		header : 'A端速率',
		dataIndex : 'aRate',
		width : 80,
		hidden: true
	}, {
		id : 'sendOP',
		header : '发光功率(dBm)',
		dataIndex : 'sendOP',
		width : 100
	}, {
		id : 'zGroupName',
		header : 'Z端网管分组',
		dataIndex : 'zGroupName',
		width : 100
	}, {
		id : 'zEmsName',
		header : 'Z端网管',
		dataIndex : 'zEmsName',
		width : 100
	}, {
		id : 'zSubnetName',
		header : 'Z端子网',
		dataIndex : 'zSubnetName',
		width : 100,
		hidden: true
	}, {
		id : 'zNe',
		header : 'Z端网元',
		dataIndex : 'zNe',
		width : 100
	}, {
		id : 'zAreaName',
		header : 'Z端'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'zAreaName',
		width : 100,
		hidden: true
	}, {
		id : 'zStationName',
		header : 'Z端'+top.FieldNameDefine.STATION_NAME,
		dataIndex : 'zStationName',
		width : 100,
		hidden: true
	}, {
		id : 'zProductName',
		header : 'Z端型号',
		dataIndex : 'zProductName',
		width : 100
	},{
		id : 'zPtp',
		header : 'Z端端口',
		dataIndex : 'zPtp',
		width : 120
	}, {
		id : 'zPtpType',
		header : 'Z端端口类型',
		dataIndex : 'zPtpType',
		width : 100
	}, {
		id : 'zRate',
		header : 'Z端速率',
		dataIndex : 'zRate',
		width : 80,
		hidden: true
	}, {
		id : 'recOP',
		header : '接收光功率(dBm)',
		dataIndex : 'recOP',
		width : 100
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
//默认采集历史性能
var mode = 1;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'offsetLevelValue'
		}, {
			name : 'offsetLevelDisplay'
		} ],
		data : [ [ 0, '全部' ], [ 1, '等级1' ], [ 2, '等级2' ], [ 3, '等级3' ] ]
	});
	offsetLevelCombox = new Ext.form.ComboBox({
		id : 'offsetLevelCombox',
		fieldLabel : '偏差范围',
		labelWidth : 60,
		labelSeparator : "：",
		store : store,
		valueField : 'offsetLevelValue',
		displayField : 'offsetLevelDisplay',
		mode : 'local',
		triggerAction : 'all',
		anchor : '95%',
		value: 0,
		allowBlank : false,
		resizable: true,
		listeners : {
			//过滤数据
			select : function(combo, record, index) {
				gridPanel.store.filterBy(function(record, id) {
					if (combo.getValue() != 0) {
						if (record.get('offsetLevel') == combo.getValue()) {
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
	height : 68,
	autoScroll : true,
	collapsible : false,
	collapsed : false,
	labelWidth : 40,
	items : [{
        xtype: 'compositefield',
        fieldLabel: '一级',
        items: [{
	        	xtype:'numberfield',
	            id: 'downOffset',
	            name: 'downOffset',
	            width:100,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2,
	            anchor:'95%'
        },{
               xtype: 'displayfield',
               value: 'dB'
        }]
    },{
        xtype: 'compositefield',
        fieldLabel: '二级',
        items: [{
	            xtype:'numberfield',
	            id: 'upperOffset',
	            name: 'upperOffset',
	            fieldLabel: '二级',
	            width:100,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2,
	            anchor:'95%'
        },{
               xtype: 'displayfield',
               value: 'dB'
        }]
    }]
});

var offsetWindow = new Ext.Window({
	id : 'offsetWindow',
	title : '偏差设置',
	width : 200,
	height : 133,
	isTopContainer : true,
	modal : true,
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
	text : '偏差设置',
	privilege:modAuth,
	icon : '../../../resource/images/btnImages/config_add.png',
	handler : function(){
		offsetWindow.show();
	}
});

var upperOffsetDisplay = new Ext.form.DisplayField({
	id: 'upperOffsetDisplay',
    name: 'upperOffsetDisplay'
});

var downOffsetDisplay = new Ext.form.DisplayField({
	id: 'downOffsetDisplay',
    name: 'downOffsetDisplay'
});

var middleOffsetDisplay = new Ext.form.DisplayField({
	id: 'middleOffsetDisplay',
    name: 'middleOffsetDisplay'
});

var oneTbar = new Ext.Toolbar({
	id : 'oneTbar',
	items : [ "偏差范围 ：", offsetLevelCombox,"-",setOffsetButton,'-',
	 downOffsetDisplay,{
		xtype : 'tbspacer',
		width : 10
	},middleOffsetDisplay,{
		xtype : 'tbspacer',
		width : 10
	},upperOffsetDisplay]
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	colModel : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	stateId : "attenuationSearchGridId",
	stateful : true,
	bbar : pageTool,
	tbar : [ '-', {
		text : '当前值刷新',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/refresh.png',
		handler : fulshCurrentPm
	// disabled:true
	}, '-', {
		text : '性能趋势图',
		icon : '../../../resource/images/btnImages/chart.png',
		handler : function() {
		},
		disabled:true
	}, '-', {
		text : '保存',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : modifyAttForLink
	}, {
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
			centerPanel.doLayout();
		}
	} ],
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
	//清空
	userCurrentPmLinkIds = new Array();
	// 选择tree中选中的节点
	var iframe = window.frames["tree_panel"] || window.frames[0];
	var selectTargets = new Array();
	var targets;
	if (iframe.getCheckedNodes) {
		targets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ], "top");
	} else {
		targets = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel" ], "top");
	}
	var linkId = Ext.getCmp('linkId').getValue();
	var linkName = Ext.getCmp('linkName').getValue();
	var collectDate = Ext.getCmp('collectDate').getValue();
	var upperOffset = Ext.getCmp('upperOffset').getValue();
	var downOffset = Ext.getCmp('downOffset').getValue();
	
	if(collectDate == ''){
		Ext.Msg.alert("提示", "查询日期为空！");
		return;
	}
	for ( var i = 0; i < targets.length; i++) {
		selectTargets.push(Ext.encode(targets[i]));
	}

	if (targets.length <= 0 && linkId == '' && linkName == '') {
		Ext.Msg.alert("提示", "查询目标为空！");
		return;
	}
	var jsonData = {
		"opticalPathMonitorModel.linkId" : linkId,
		"opticalPathMonitorModel.linkName" : linkName,
		"opticalPathMonitorModel.downOffset" : downOffset,
		"opticalPathMonitorModel.upperOffset" : upperOffset,
		"opticalPathMonitorModel.collectDate" : collectDate,
		"opticalPathMonitorModel.selectTargets" : selectTargets,
		//恢复baseParams值
		"opticalPathMonitorModel.userCurrentPmLinkIds" : new Array(),
		"limit" : 200
	};

	// Ext.getBody().mask('正在执行，请稍候...');
	store.proxy = new Ext.data.HttpProxy({
		url : 'optical-path-monitor!searchOpticalPath.action'
	});
	store.baseParams = jsonData;

	store.load({
		callback : function(r, options, success) {// 回调函数
			// Ext.getBody().unmask();
			if (success) {
				if (r.length == 0) {
//					Ext.Msg.alert("信息", "查询结果为空！");
					// 刷新列表
				};
			} else {
				// Ext.getBody().unmask();
				Ext.Msg.alert("错误", '查询失败，请重新查询！');
			};
		}
	});
}

//修改att值
function modifyAttForLink(){
	var jsonString = new Array();
	var vRecords = gridPanel.getStore().getModifiedRecords(); // 得到修改的数据（记录对象）
	if (vRecords.length == 0) {
		Ext.Msg.alert("提示", "您没有做任何修改!");
		return;
	}
	for ( var i = 0; i < vRecords.length; i++) {
		var opticalPathMonitorModel = {
			"linkId" : vRecords[i].get('linkId'),
			"att" : vRecords[i].get('att')
		};
		jsonString.push(opticalPathMonitorModel);
	}
	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	// 提交修改，不然store.getModifiedRecords();数据会累加
	store.commitChanges();
	Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'optical-path-monitor!modifyAttForLink.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				// 刷新列表
				var pageTool = Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				};
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			};
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

function fulshCurrentPm(){
	var jsonString = new Array();
	var vRecords = gridPanel.getSelectionModel().getSelections();
	if (vRecords.length == 0) {
		Ext.Msg.alert("提示", "请先选取光链路信息！");
		return;
	}
	if (vRecords.length > 20) {
		Ext.Msg.alert("提示", "最多选择20条光链路！");
		return;
	}
	for ( var i = 0; i < vRecords.length; i++) {
		var opticalPathMonitorModel = {
			"linkId":vRecords[i].get('linkId'),
			"aEmsConnectionId":vRecords[i].get('aEmsConnectionId'),
			"zEmsConnectionId":vRecords[i].get('zEmsConnectionId'),
			"aPtpId" : vRecords[i].get('aPtpId'),
			"zPtpId" : vRecords[i].get('zPtpId')
		};
		//记录linkId
		userCurrentPmLinkIds.push(vRecords[i].get('linkId'));
		
		jsonString.push(opticalPathMonitorModel);
	}
	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'optical-path-monitor!fulshCurrentPm.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				//修改baseParams值
				if(store.baseParams){
					store.setBaseParam("opticalPathMonitorModel.userCurrentPmLinkIds",userCurrentPmLinkIds);
				}
				// 刷新列表
				var pageTool = Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
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

function modifyOffsetValue(){
	
	var upper = Ext.getCmp('upperOffset').getValue();
	var down = Ext.getCmp('downOffset').getValue();
	
	if(upper<down || upper == down){
		Ext.Msg.alert('信息', '二级值必须大于一级值！');
		return;
	}
	
	var jsonData = {
		"opticalPathMonitorModel.downOffset" : down,
		"opticalPathMonitorModel.upperOffset" : upper
		
	};
	Ext.Ajax.request({
		url : 'optical-path-monitor!modifyOffsetValue.action',
		method : "POST",
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert('信息', '修改成功！', function(){
					Ext.getCmp('downOffsetDisplay').setValue("一级：<"+down);
					Ext.getCmp('middleOffsetDisplay').setValue("二级："+down+"~"+upper);
					Ext.getCmp('upperOffsetDisplay').setValue("三级：≧"+upper);
					offsetWindow.hide();
				});
				// 刷新列表
				var pageTool = Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
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

function colorGrid(v, m, r) {
	if(r.data.offsetLevel == 1){
		m.css = 'x-grid-font-blue';
	}
	if(r.data.offsetLevel == 2){
		m.css = 'x-grid-font-orange';
	}
	if(r.data.offsetLevel == 3){
		m.css = 'x-grid-font-red';
	}
	return v;
}

function init(){
	Ext.Ajax.request({
		url : 'optical-path-monitor!getOffsetValue.action',
		method : 'POST',
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			var upper = obj.UPPER_OFFSET;
			var down = obj.DOWN_OFFSET;
			
			Ext.getCmp('upperOffset').setValue(upper);
			Ext.getCmp('downOffset').setValue(down);
			
			Ext.getCmp('downOffsetDisplay').setValue("一级：<"+down);
			Ext.getCmp('middleOffsetDisplay').setValue("二级："+down+"~"+upper);
			Ext.getCmp('upperOffsetDisplay').setValue("三级：≧"+upper);
			
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

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	autoScroll : true,
	layout : 'border',
	items : [searchPanel,gridPanel]
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		title : "光路衰耗监测",
		layout : 'border',
		items : [ centerPanel, westPanel ],
		renderTo : Ext.getBody()
	});
	win.show();
	init();
});