Ext.override(Ext.form.CheckboxGroup, {
	getValue : function(mode) {
		var v = [];
		if (mode == 1) {
			this.items.each(function(item) {
				if (item.getValue())
					v.push(item.getRawValue());
			});
			return v;
		} else {
			this.items.each(function(item) {
				v.push(item.getValue());
			});
			return v;
		}
	}
});
Ext.override(Ext.form.BasicForm, {
	isValid : function(){
        var valid = true;
        this.items.each(function(f){
        	//console.log(f);
           if(!f.hidden && !f.validate()){
               valid = false;
           }
        });
        return valid;
	}
});


var store = new Ext.data.Store({
	url:'nx-report!searchWaveDirById.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [  "emsGroup", "ems", "ne", "subnet",
			"factory", "neType", 'networkName',"unit","waveDirId",
			"waveDir","stdWaveNum","actWaveNum","station","neId" ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 130
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 130
	}, {
		id : 'subnet',
		header : '子网',
		dataIndex : 'subnet',
		width : 120
	}, {
		id : 'ne',
		header : '网元名称',
		dataIndex : 'ne',
		width : 180
	}, {
		id : 'factory',
		header : '设备厂家',
		dataIndex : 'factory',
		width : 100,
		renderer : factoryRenderer
	}, {
		id : 'neType',
		header : '网元型号',
		dataIndex : 'neType',
		width : 140
	}, {
		id : 'unit',
		header : '单位',
		dataIndex : 'unit',
		width : 100
	}, {
		id : 'networkName',
		header : '网络名称',
		dataIndex : 'networkName',
		width : 120
	}, {
		id : 'waveDir',
		header : '方向',
		dataIndex : 'waveDir',
		width : 120
	} , {
		id : 'stdWaveNum',
		header : '容量（波数）',
		dataIndex : 'stdWaveNum',
		width : 100
	} , {
		id : 'actWaveNum',
		header : '实开（波数）',
		dataIndex : 'actWaveNum',
		width : 100
	} ]
});

function factoryRenderer(v){
	for(var i=0;i<FACTORY.length;i++){
		if(v==FACTORY[i]['key'])
			return FACTORY[i]['value'];
	}
	return v;
}

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	 title:'波分方向选择',
	store : store,
	cm : cm,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-',{
		text : '新增',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addWaveDir
	}, '-',{
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
			var items = grid.getSelectionModel().getSelections();
			if(items.length>0){
				Ext.Msg.confirm("提示","是否删除选中的对象？",function(btn){
					if(btn=="yes"){
						for ( var i = 0; i < items.length; i++) {
							store.remove(items[i]);
						}
						store.commitChanges();
					}
				});
			}else{
				Ext.Msg.alert("提示","请先选取对象！");
			}
		}
	}, '-',{
		text : '清空',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {
			Ext.Msg.confirm("提示","是否清空所有对象数据？",function(btn){
				if(btn=="yes"){
					store.removeAll();
					store.commitChanges();
				}
			});
		}
	} ],
	buttons : [ '->', {
		text : '立即生成报表',
		privilege:addAuth,
		//icon : '../../../resource/images/buttonImages/submit.png',
		handler : beforeGen
	} ]
});
//---------------------------------------------------


var rowIII = {
	id : 'rowIII',
	width:1120,
	border : false,
	layout : 'column',
	items : [ {
		columnWidth : 1,
		layout : 'form',
		border : false,
		items : [ WDM ]
	} ]
};

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	labelAlign : 'left',
	height :350,
	autoScroll:true,
	collapsible : true,
	items : [ rowI, rowII, rowIII ]
});

// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
function beforeGen() {
	if (Ext.getCmp('northPanel').getForm().isValid()) {
		if (store.getCount() < 1) {
			Ext.Msg.alert('信息', '请添加波分方向！');
			return;
		}
		generateImmediately();
	}
}

function generateImmediately() {
	var otherWDMTP = Ext.getCmp('WDMTPLevelOther').getValue() ? 1 : 0;
	// WDM性能参数
	var WdmPm = getPmChecked.getWdmPmStdIndex();
	var WdmTp = getPmChecked.getWdmTp();
	//@
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var dataSrc = Ext.getCmp('dataSrcCombo').getValue();// 0:正常；1:异常
	var continueAbnormal = Ext.getCmp('continueAbnormal').getValue();
	var privilege = Ext.getCmp('privilege').getValue();
	var period = Ext.getCmp('periodCb').getValue();// 0:每天；1：每月
	//@
	var start;
	var end;
	if(period==0){
		start = Ext.getCmp('startTime').getValue();
		end = Ext.getCmp('endTime').getValue();
		if(!dayLimitCheck(start,end))
			return;
		
	}else if(period==1){
		start = Ext.getCmp('startMonth').getValue();
		end = Ext.getCmp('endMonth').getValue();
		if(!monthLimitCheck(start,end))
			return;
	}
	var list = new Array();
	store.each(function(record) {
		var nodes = {
				'targetId' : record.get('waveDirId')
			};
			list.push(Ext.encode(nodes));
	});
	var params = {
		'modifyList' : list,
		'paramMap.wdmTpOther' : otherWDMTP,
		'paramMap.wdmPm' : WdmPm.toString(),
		'paramMap.wdmTp' : WdmTp.toString(),
		'paramMap.taskName' : taskName,
		'paramMap.dataSrc' : dataSrc,
		'paramMap.continueAbnormal' : continueAbnormal != '' ? continueAbnormal
				: 1,
		'paramMap.privilege' : privilege,
		'paramMap.period' : period,
		'paramMap.start' : start,
		'paramMap.end' : end,
		'paramMap.pmDate' : pmDate.getValue(),
		'paramMap.userId' : userId,
		'reportType':repType
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'nx-report!getReportInstantly.action',
		method : 'POST',
		params : params,
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
//	    	console.log(result);
	    	if(result.returnResult == 1){
	    		Ext.Msg.alert("信息", result.returnMessage);
            } else {
        		Ext.Msg.alert("信息", result.returnMessage);
        	}
		},
		failure : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

//--------------------------------------------
Ext
		.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var win = new Ext.Viewport({
				id : 'win',
				title : "定制即时报表生成",
				layout : 'border',
				// autoScroll:true,
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			Ext.getCmp('WDMTPLevel').items.get(0).disable();
			Ext.getCmp('WDMPhysical').items.get(0).disable();
			Ext.getCmp('WDMPhysical').items.get(1).disable();
			win.show();
		});