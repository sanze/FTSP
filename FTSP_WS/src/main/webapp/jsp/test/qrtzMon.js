Ext.QuickTips.init();
function mnuProc(cmd, rec){
	switch(cmd){
	case "activate":
		//console.log("立刻执行 -> " + rec.get("JOB_NAME"));
		ctrl(rec.get("JOB_NAME"), rec.get("TRIGGER_NAME"), 1);
		break;
	case "delayedActivate":
		//console.log("延时执行 -> " + rec.get("JOB_NAME"));
		delayedActivate(rec.get("JOB_NAME"), rec.get("TRIGGER_NAME"));
		break;
	case "pause":
		//console.log("暂停执行 -> " + rec.get("JOB_NAME"));
		ctrl(rec.get("JOB_NAME"), rec.get("TRIGGER_NAME"), 2);
		break;
	case "resume":
		//console.log("回复执行 -> " + rec.get("JOB_NAME"));
		ctrl(rec.get("JOB_NAME"), rec.get("TRIGGER_NAME"), 4);
		break;
	case "delete":
		//console.log("删除 -> " + rec.get("JOB_NAME"));
		Ext.Msg.confirm("wtf!",String.format("你丫的确定要删掉<{0}>这么可爱的任务？",rec.get("JOB_NAME")),function(btn){
				if(btn == "yes"){
					ctrl(rec.get("JOB_NAME"), rec.get("TRIGGER_NAME"), 3);
				}
			});
		break;
	case "modify":
		//console.log("修改时间 -> " + rec.get("JOB_NAME"));
		Ext.Msg.prompt("修改任务时间","清输入Cron表达式：",function(btn,v){
			if(btn == "ok")
				modTime(rec.get("JOB_NAME"), rec.get("TRIGGER_NAME"), v);
		});
		break;
	default:
	}
}
var grid = (function initGrid() {
	var dataStore = new Ext.data.Store({
		url : "job!getAllJobInfo.action",
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "CRON_EXPRESSION", "END_TIME", "IS_VOLATILE", "JOB_DATA",
				"JOB_GROUP", "JOB_NAME", "MISFIRE_INSTR", "NEXT_FIRE_TIME",
				"PREV_FIRE_TIME", "PRIORITY", "START_TIME", "TRIGGER_GROUP",
				"TRIGGER_NAME", "TRIGGER_STATE", "TRIGGER_TYPE" ])
	});
	dataStore.load();
	var selModel = new Ext.grid.RowSelectionModel ({
		singleSelect : true
	});
	var cm = new Ext.grid.ColumnModel({
		// specify any defaults for each column
		defaults : {
			sortable : true
		},
		columns : [new Ext.grid.RowNumberer(),  {
			id : 'JOB_NAME',
			header : '任务名称',
			width:120,
			dataIndex : 'JOB_NAME'
		},{
			id : 'TRIGGER_NAME',
			header : '触发器名称',
			hidden : true,
			dataIndex : 'TRIGGER_NAME'
		}, {
			id : 'TRIGGER_GROUP',
			header : '触发器组',
			hidden : true,
			dataIndex : 'TRIGGER_GROUP'
		},  {
			id : 'TRIGGER_TYPE',
			header : '触发器类型',
            width: 80,
			dataIndex : 'TRIGGER_TYPE'
		},  {
			id : 'TRIGGER_STATE',
			header : '触发器状态',
            width: 80,
			dataIndex : 'TRIGGER_STATE'
		}, {
			id : 'MISFIRE_INSTR',
			header : '错过触发对策',
            width: 80,
			dataIndex : 'MISFIRE_INSTR'
		},{
			id : 'CTRL_COL',
			header : '<b>★金手指！★</b>',
            xtype: 'actioncolumn',
            width: 100,
            items: [{
                icon : '../../resource/images/btnImages/control_play_blue.png',
                tooltip: '立即执行',
                handler: function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
        			mnuProc("activate", rec);
                }
            },{
                icon : '../../resource/images/btnImages/control_play.png',
                tooltip: '2分钟后执行',
                handler: function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
        			mnuProc("delayedActivate", rec);
                }
            },{
                icon : '../../resource/images/btnImages/control_pause_blue.png', 
                tooltip: '暂停任务',
                handler: function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
        			mnuProc("pause", rec);
                }
            },{
                icon : '../../resource/images/btnImages/arrow_undo.png', 
                tooltip: '恢复任务',
                handler: function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
        			mnuProc("resume", rec);
                }
            },{
                icon : '../../resource/images/btnImages/delete.png', 
                tooltip: '删除任务',
                handler: function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
        			mnuProc("delete", rec);
                }
            },{
                icon : '../../resource/images/btnImages/modify.png', 
                tooltip: '修改时间',
                handler: function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
        			mnuProc("modify", rec);
                }
            }]
        }, {
			id : 'CRON_EXPRESSION',
			header : 'Cron表达式',
			width : 160,
			dataIndex : 'CRON_EXPRESSION'
		}, {
			id : 'JOB_GROUP',
			header : '任务组',
			hidden : true,
			dataIndex : 'JOB_GROUP'
		}, {
			id : 'JOB_DATA',
			header : 'JobData',
			hidden : true,
			dataIndex : 'JOB_DATA'
		}, {
			id : 'START_TIME',
			header : '创建时间',
			width : 130,
			dataIndex : 'START_TIME'
		}, {
			id : 'PREV_FIRE_TIME',
			header : '上一次执行时间',
			width : 130,
			dataIndex : 'PREV_FIRE_TIME'
		}, {
			id : 'NEXT_FIRE_TIME',
			header : '下一次执行时间',
			width : 130,
			dataIndex : 'NEXT_FIRE_TIME'
		} ]
	});
	var neGrid = new Ext.grid.GridPanel({
		id : "neGrid",
		// autoScroll : true,
		// title:'用户管理',
		flex : 1,
		cm : cm,
		sm:selModel,
		border : true,
		store : dataStore,
		stripeRows : true, // 交替行效果
		loadMask : true,
		forceFit : true,
		frame : false
	});

	neGrid.on("rowclick",function(grid, rowIndex, e){
		if (rowIndex < 0) {
			return;
		}
		//tb.setPosition(e.getPoint());
		//tb.setTitle(dataStore.getAt(rowIndex).get("JOB_NAME"));
		Ext.getCmp("pauseBtn").setText(
				dataStore.getAt(rowIndex).get("TRIGGER_STATE")=="PAUSED"?"恢　　复":"暂　　停");
	});
	return neGrid;
})();

function ctrl(jobName,triggerName, flag){
	Ext.Ajax.request({
		url: "job!ctrl.action",
		method : 'POST',
		params:{
			jobName:jobName,
			triggerName:triggerName,
			jobFlag:flag
		},
		scope:this,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			if(flag === 2 || flag === 3 || flag === 4)
				grid.getStore().load();
		},
		error:function(response) {
			Ext.Msg.alert("错误",response.responseText);
		},
		failure:function(response) {
			Ext.Msg.alert("错误",response.responseText);
		}
	});
}
function modTime(jobName,triggerName,newTime){
	Ext.Ajax.request({
		url: "job!modTime.action",
		method : 'POST',
		params:{
			jobName:jobName,
			triggerName:triggerName,
			jobTime:newTime
		},
		scope:this,
		success: function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert(obj.returnResult == 1?"提示":"警告", obj.returnMessage);
			grid.getStore().load();
		},
		error:function(response) {
			Ext.Msg.alert("错误",response.responseText);
		},
		failure:function(response) {
			Ext.Msg.alert("错误",response.responseText);
		}
	});
}//0 0/2 * * * ? *
var main = new Ext.Panel({
	frame : true,
	title : 'Quartz Monitor',
	layout : "fit",
	items : [grid]
});

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'fit',
		items : [ main ],
		renderTo : Ext.getBody()
	});
	win.show();
	grid.getStore().load();
});

function getNextCron(delay) {
	delay = delay || 2;
	var now = new Date();
	now.setMinutes(now.getMinutes() + delay);
	var cronStr = now.format("0 i h * * ? *");
	//console.log("下一次执行时间：" + now.format("h:i:00"));
	//console.log("CRON表达式：" + cronStr);
	return cronStr;
}
