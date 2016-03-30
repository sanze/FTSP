function onItemClick(itm, evt) {
//	console.log("------onSelectData-------");
//	console.log(itm.url);
	Ext.getCmp("action").setValue(itm.url);
	Ext.getCmp("param").setValue(itm.param);
	Ext.getCmp("curData").setValue(itm.text);
	
}
function saveData() {
//	console.log("-----saveData-----");
}
 
var runner = new Ext.FormPanel({
	labelWidth : 75,
	labelAlign : "right",
	frame : true,
	title : 'Action临时测试器',
	bodyStyle : 'padding:5px 5px 0',
	defaultType : 'textfield',
	items : [ {
		xtype : 'compositefield',
		items : [ {
			xtype : "textarea",
			height:24,
			width : 200,
			fieldLabel : 'ID',
			id : 'curData'
		}, new Ext.SplitButton({
			text : '加载',
			height:24,
			width:80,
			enableToggle : undefined,
			menu : {
				id : "mnu",
				items : []
			}
		}) , {
			xtype : "button",
			height : 24,
			width : 100,
			text : "新增",
			id : 'add',
			handler : function() {
				var param = Ext.getCmp("param").getValue();
				param = jsl.format.formatJson(param);
				Ext.getCmp("param").setValue(param);
			}
		} , {
			xtype : "button",
			height : 24,
			width : 100,
			text : "修改",
			id : 'mod',
			handler : function() {
				var param = Ext.getCmp("param").getValue();
				param = jsl.format.formatJson(param);
				Ext.getCmp("param").setValue(param);
			}
		} , {
			xtype : "button",
			height : 24,
			width : 100,
			text : "删除",
			id : 'del',
			handler : function() {
				var param = Ext.getCmp("param").getValue();
				param = jsl.format.formatJson(param);
				Ext.getCmp("param").setValue(param);
			}
		}, {
			xtype : "button",
			height : 24,
			width : 100,
			text : "修改QRTZ时间",
			id : 'modQrtz',
			handler : function() {
				var cron = getNextCron();
				var param = {
					jobType:8,
					jobID:14,
					jobTime:cron
				};
				Ext.getCmp("action").setValue("job!modTime.action");
				Ext.getCmp("param").setValue(Ext.encode(param));
			}
		} ]
	}, {
		fieldLabel : 'Action名称',
		xtype : 'textarea',
		width : 600,
		height:24,
		id : "action"
	}, {
		xtype : 'compositefield',
		items : [ {
			xtype : "textarea",
			height : 80,
			width : 600,
			fieldLabel : '参数',
			value : "{'searchCond.taskId':176}",
			id : 'param'
		}, {
			xtype : "button",
			height : 32,
			width : 120,
			text : "格式化!",
			id : 'fmt',
			handler : function() {
				var param = Ext.getCmp("param").getValue();
				param = jsl.format.formatJson(param);
				Ext.getCmp("param").setValue(param);
			}
		} ]
	}, {
		xtype : 'compositefield',
		items : [ {
			xtype : "textarea",
			fieldLabel : '结果',
			height : 480,
			width : 600,
			id : 'rlt'
		}, {
			xtype : "button",
			height : 80,
			width : 120,
			text : "<b>Ajax!</b>",
			id : 'go',
			handler : function() {
				var action = Ext.getCmp("action").getValue();
				var param = Ext.getCmp("param").getValue();
				param = eval("(" + param + ")");
				console.log(param);
				Ext.Ajax.request({
					url : action,
					method : "POST",
					params : param,
					success : function(response) {
						var rv = response.responseText;
						rv = jsl.format.formatJson(rv);
						Ext.getCmp("rlt").setValue(rv);
					},
					failure : function(response) {
						console.log("failure");
						console.log(response.responseText);
						var msgs = response.responseText.split(/\r?\n/);
						var rv = "";
						for ( var i = 0; i < msgs.length; i++) {
							if (msgs[i].indexOf("com.fujitsu") > 0) {
								rv += msgs[i] + "\r\n";
							}
						}
						Ext.getCmp("rlt").setValue(rv);
					},
					error : function(response) {
						console.log("error");
					}
				});
			}
		} ]
	} ],
	listeners : {
		"resize" : function() {
			Ext.getCmp("action").setWidth(Ext.getCmp("param").getWidth());
			Ext.getCmp("rlt").setWidth(Ext.getCmp("param").getWidth());
		}
	}
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
		items : [ runner ],
		renderTo : Ext.getBody()
	});
	win.show();
	initData();
	loadData();
});
var db = {};
function initData() {
	db = {
		"PM-网元-每日" : {
			text : "PM-网元-每日",
			url : "pm-report!searchPMForReportNeDaily.action",
			param : "{'searchCond.taskId':226}"
		},
		"PM-网元-每月" : {
			text : "PM-网元-每月",
			url : "pm-report!searchPMForReportNeMonthly.action",
			param : "{'searchCond.taskId':231}"
		},
		"PM-复用段-每日" : {
			text : "PM-复用段-每日",
			url : "pm-report!getPmFromTaskId.action",
			param : "{'searchCond.taskId':329}"
		},
		"PM-复用段-每月" : {
			text : "PM-复用段-每月",
			url : "pm-report!getPmFromTaskId.action",
			param : "{'searchCond.taskId':???}"
		}
	};
	saveCookie();
}
function saveCookie() {
	var cookie = Ext.util.Cookies;
	var ids=[];
	for(var s in db){
//		console.log(String.format("[{0}]-[{1}]",s,Ext.encode(db[s])));
		ids.push(s);
//		cookie.set(s, Ext.encode(db[s]), new Date((new Date()).getTime()+365*24*60*3600*1000));
	}
	cookie.set("ids",ids.join(","), new Date((new Date()).getTime()+365*24*60*3600*1000));
}
function loadData() {
	console.log("-----loadData-----");
	var cookie = Ext.util.Cookies;
	var ids = cookie.get("ids");
	Ext.getCmp("mnu").removeAll();
	console.log("ids = " + ids);
	if (!!ids) {
		var idArr = ids.split(",");
		for ( var i = 0, len = idArr.length; i < len; i++) {
			console.log("Revovering -> " + idArr[i]);
			var objStr = cookie.get(idArr[i]);
			console.log("      data -> " + objStr);
			var obj = Ext.decode(objStr);
			console.log(obj);
			db[idArr[i]] = {};
			db[idArr[i]].text = obj.text;
			db[idArr[i]].url = obj.url;
			db[idArr[i]].param = obj.param;
			db[idArr[i]].handler = onItemClick;
			Ext.getCmp("mnu").add(db[idArr[i]]);
		}
	}
}

function getNextCron(){
	var now = new Date();
	now.setMinutes(now.getMinutes()+2);
	var cronStr = now.format("0 i h * * ? *");
	console.log("下一次执行时间：" + now.format("h:i:00"));
	console.log("CRON表达式：" + cronStr );
	return cronStr;
}
