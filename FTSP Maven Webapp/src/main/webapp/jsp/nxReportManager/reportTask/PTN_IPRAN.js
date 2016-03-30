var TARGET_NAME = "端口";
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


/* 【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/
var store = new Ext.data.Store({
	url : 'optical-unit-config!searchPtpOptModelInfo.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsGroup","ems", "subnet", "NeDisplayName", "NeType",
			"ptpId", 'portDescription','factory','maxIn','minIn' ])
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 100
	},{
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 100
	}, {
		id : 'subnet',
		header : '子网',
		dataIndex : 'subnet',
		width : 100
	}, {
		id : 'NeDisplayName',
		header : '网元',
		dataIndex : 'NeDisplayName',
		width : 150
	}, {
		id : 'factory',
		header : '设备厂家',
		dataIndex : 'factory',
		width : 100,
		renderer:transFactoryName
	}, {
		id : 'NeType',
		header : '网元型号',
		dataIndex : 'NeType',
		width : 100
	}, {
		id : 'portDescription',
		header : '端口名称',
		dataIndex : 'portDescription',
		width : 200
	}, {
		id : 'maxIn',
		header : '过载点(dBm)',
		dataIndex : 'maxIn',
		width : 100
	}, {
		id : 'minIn',
		header : '灵敏度(dBm)',
		dataIndex : 'minIn',
		width : 100
	} ]
});
var curPanel = null;
var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	title : TARGET_NAME+'选择',
	store : store,
	loadMask:true,
	cm : cm,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-', {
		text : '新增',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : addPtp
	}, '-', {
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function(){deleteTarget(1);}
	}, '-', {
		text : '清空',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function(){deleteTarget(2);}
	},'-', {
        text : "关联光口标准",
        privilege:modAuth,
        icon : '../../../resource/images/btnImages/associate.png',
        handler : function () {
            var recs = grid.getSelectionModel().getSelections();
            curPanel = grid;
            if (!!!recs||recs.length == 0) {
                Ext.Msg.alert("提示", "请至少选择一个端口！");
                return;
            }
            showRelateOpticalStandardValue();
        }
    } ],
	buttons : [ '->', {
		text : '保存',
		id : 'saveButton',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function() {
			beforeSave(false,'ptpId');
		}
	}, {
		text : '预览',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/disk.png',
		handler : function() {
			beforeSave(true,'ptpId');
		}
	} ]
});

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '作业计划设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	autoScroll : true,
	labelAlign : 'left',
	height : 190,
	collapsible : true,
	items : [ rowI, rowII ]
});

/**【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

/* 【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/


/**
 * 修改时初始化页面
 */
function initTaskInfo() {
	var param = {
		'paramMap.taskId' : taskId
	};
	top.Ext.getBody().mask("正在加载");
	Ext.Ajax.request({
		url : 'nx-report!initReportTaskInfo.action',
		method : 'POST',
		params : param,
		success : function(response) {
			top.Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result.returnResult != 0) {
				var taskInfo = result.taskInfo[0];
				var taskNodes = result.taskNodes;
				// 上半
				if (!!taskInfo) {
					Ext.getCmp('reportTaskName').setValue(taskInfo.taskName);
					Ext.getCmp('privilege').setValue(taskInfo.privilege);
					Ext.getCmp('periodCb').setValue(taskInfo.period);
					Ext.getCmp('hourCb').setValue(taskInfo.hour);
					Ext.getCmp('dataSrcCombo').setValue(taskInfo.dataSrc);
					if (taskInfo.dataSrc == 1) {
						Ext.getCmp('continueAbnormal').setVisible(true);
						Ext.getCmp('continueAbnormal').setValue(
								taskInfo.continueAbnormal);
					} else {
						Ext.getCmp('continueAbnormal').setVisible(false);
					}
					if (taskInfo.period == 0) {
						delay4MonthlyCb.hide();
						delay4DailyCb.show();
						delay4DailyCb.setValue(taskInfo.delay);
					}
					if (taskInfo.period == 1) {
						delay4DailyCb.hide();
						delay4MonthlyCb.show();
						delay4MonthlyCb.setValue(taskInfo.delay);
					}
				}
				var list = new Array();
				// 下半
				for ( var i = 0; i < taskNodes.length; i++) {
					var nodes = {
						'nodeId' : taskNodes[i].nodeId,
						'nodeLevel' : NodeDefine.PTP
					};
					list.push(Ext.encode(nodes));
				}
				var params = {
					'modifyList' : list
				};
				store.baseParams= params;
				store.load();
			} else {
				Ext.Msg.alert("提示", result.returnMessage);
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



function showRelateOpticalStandardValue() {
    var url = '../unitInterface/standardOpticalValueManage.jsp?authSequence=' + authSequence;
    var addUnitInterfaceWindow = new Ext.Window({
            id : 'relateOpticalStandardValueWindow',
            title : '关联光口标准',
            width : Ext.getBody().getWidth() * 0.8,
            height : Ext.getBody().getHeight() * 0.9,
            isTopContainer : true,
            modal : true,
            autoScroll : true,
            html : "<iframe  id='关联光口标准' name = '关联光口标准'  src = " + url
             + " height='100%' width='100%' frameBorder=0 border=0/>"
        });
    addUnitInterfaceWindow.show();
}

function relateOpticalStandardValue(rec) {
    var optStdId = rec.get("optStdId");
    var maxIn = rec.get("maxIn");
    var minIn = rec.get("minIn");
    var recs = curPanel.getSelectionModel().getSelections();
    var modIds = [];
    for (var i = 0; i < recs.length; i++) {
        modIds.push(recs[i].get("ptpId"));
        //		recs[i].set("MAX_IN", maxIn);
        //		recs[i].set("MIN_IN", minIn);
    }
    //	curPanel.getStore().commitChanges();

    var params = {
        "paramMap.ptpIds" : modIds.toString(),
        "paramMap.optStdId" : optStdId
    };
    Ext.getBody().mask('正在执行，请稍候...');
    Ext.Ajax.request({
        url : 'nx-report!relateOpticalStandardValue.action',
        params : params,
        method : 'POST',
        success : function (response) {
            Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            if (result.returnResult == 0) {
                Ext.Msg.alert("提示", result.returnMessage);
            }
            if (result.returnResult == 1) {
                Ext.Msg.alert("提示", result.returnMessage);
                //				pageTool.doLoad(pageTool.cursor);
                for (var i = 0; i < recs.length; i++) {
                    recs[i].set("maxIn", maxIn);
                    recs[i].set("minIn", minIn);
                    var ptpId = recs[i].get("ptpId");
                    var idx = grid.getStore().find("ptpId", ptpId);
                    //					console.log("ptp @ " + idx)
                    if (idx > -1) {
                        var r = grid.getStore().getAt(idx);
                        r.set("maxIn", maxIn);
                        r.set("minIn", minIn);
                    }
                }
                curPanel.getStore().commitChanges();

            }

        },
        failure : function (response) {
            Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        },
        error : function (response) {
            Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        }
    });
}
/**【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/
function transFactoryName(v){
	for(var fac in FACTORY){
		if(v==FACTORY[fac]['key']){
    		return FACTORY[fac]['value'];
    	}
	}
	return v;
}
Ext.onReady(function() {
			Ext.Ajax.timeout = 900000;
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				parent.parent.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			var win = new Ext.Viewport({
				id : 'win',
				title : "新增报表",
				layout : 'border',
				// autoScroll:true,
				items : [ northPanel, grid ],
				renderTo : Ext.getBody()
			});
			win.show();
			Ext.getCmp("periodCb").disable();
			Ext.getCmp("dataSrcCombo").disable();
			if (!!taskId) {
				initTaskInfo();
				if (userId != creatorId && userId != -1)
					Ext.getCmp("saveButton").disable();
			}
		});