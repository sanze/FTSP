
/*var summaryFieldSet = new Ext.form.FieldSet({
//	columnWidth:.45,
	width:450,
	height:160,
	title:'摘要',
	layout:'form',
	baseCls:'x-fieldset',
	maskDisabled:true,
//	bodyStyle:"padding:10px 0px 10px 10px",
	items:[{
		layout:'column',
		border:false,
		style:{
			'background-color':'#d9e4f4'
		},
		items:[{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'当前用户:'
		},{
			xtype:'label',
			id:'currentTime',
			cls:'my-label-style',
			id:'currentTime'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:110,
			cls:'my-label-style',
			text:'上次登录时间:'
		},{
			xtype:'label',
			id:'loginTimeLabel',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:110,
			cls:'my-label-style',
			text:'上次退出时间:'
		},{
			xtype:'label',
			id:'logoutTimeLabel',
			cls:'my-label-style'
		}]
	},new Ext.Spacer({
		width:10,
		height:20
	}),{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'南向连接'
		},{
			xtype:'label',
			width:60,
			id:'southConnectCount',
			cls:'my-label-style',
			text:'　个'
		},{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'连接正常'
		},{
			xtype:'label',
			id:'connectNormalCount',
			cls:'my-label-style',
			text:'　个'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'连接中断'
		},{
			xtype:'label',
			width:60,
			id:'connectDisconnectCount',
			cls:'my-label-style',
			text:'　个'
		},{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'连接异常'
		},{
			xtype:'label',
			width:60,
			id:'connectExceptionCount',
			cls:'my-label-style',
			text:'　个'
		},{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'网络中断'
		},{
			xtype:'label',
			width:50,
			id:'connectInterruptCount',
			cls:'my-label-style',
			text:'　个'
		}]
	}]
});

var alarmFieldSet = new Ext.form.FieldSet({
	title:'告警',
	width:450,
	height:160,
	bodyStyle:"padding:10px 10px 10px 10px",
	layout:'form',
	items:[{
		layout:'column',
		border:false,
		items:[{
			xtype:'spacer',
			width:90,
			height:5
		},{
			xtype:'label',
			width:120,
			cls:'my-label-style',
			style:{
				'text-align':'center'
			},
			text:'当前告警总数'
		},{
			xtype:'label',
			width:120,
			cls:'my-label-style',
			style:{
				'text-align':'center'
			},
			text:'今日新增告警数'
		}]
	},{
		xtype:'spacer',
		width:10,
		height:10
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'紧急告警：'
		},{
			xtype:'label',
			width:120,
			id:'totalCRCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		},{
			xtype:'label',
			width:120,
			id:'newAddCRCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'重要告警：'
		},{
			xtype:'label',
			width:120,
			id:'totalMJCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		},{
			xtype:'label',
			width:120,
			id:'newAddMJCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'次要告警：'
		},{
			xtype:'label',
			width:120,
			id:'totalMNCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		},{
			xtype:'label',
			width:120,
			id:'newAddMNCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'提示告警：'
		},{
			xtype:'label',
			width:120,
			id:'totalWRCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		},{
			xtype:'label',
			width:120,
			id:'newAddWRCount',
			cls:'my-label-style',
			style:{
				'text-align':'center'
			}
		}]
	}]
});

var taskFieldSet = new Ext.form.FieldSet({
	title:'任务',
	width:450,
	height:160,
	layout:'form',
	bodyStyle:"padding:10px 10px 10px 10px",
	items:[{
		layout:'column',
		border:false,
		items:[{
			xtype:'spacer',
			width:140,
			height:5
		},{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'启用状态'
		},{
			xtype:'label',
			width:80,
			cls:'my-label-style',
			text:'昨天执行'
		},{
			xtype:'label',
//			width:100,
			cls:'my-label-style',
			text:'成功'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'网管自动同步任务：'
		},{
			xtype:'label',
			id:'EMSAutoSYNCStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'EMSAutoSYNCYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			cls:'my-label-style',
			id:'EMSAutoSYNCSuccess'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'告警自动同步任务：'
		},{
			xtype:'label',
			id:'alarmAutoSYNCStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'alarmAutoSYNCYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'alarmAutoSYNCSuccess',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'性能采集任务：'
		},{
			xtype:'label',
			id:'pmCollStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'pmCollYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'pmCollSuccess',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'性能报表生成任务：'
		},{
			xtype:'label',
			id:'pmReportStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'pmReportYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'pmReportSuccess',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'割接任务：'
		},{
			xtype:'label',
			id:'cutOverStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'cutOverYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'cutOverSuccess',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'电路自动生成：'
		},{
			xtype:'label',
			id:'circuitAutoNewStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'circuitAutoNewYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'circuitAutoNewSuccess',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:140,
			cls:'my-label-style',
			text:'数据库备份：'
		},{
			xtype:'label',
			id:'dbBackupStartStatus',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'dbBackupYesExcute',
			cls:'my-label-style',
			width:80
		},{
			xtype:'label',
			id:'dbBackupSuccess',
			cls:'my-label-style'
		}]
	}]
});

var performanceFieldSet = new Ext.form.FieldSet({
	title:'性能',
	width:450,
	height:160,
	layout:'form',
	bodyStyle:"padding:10px 10px 10px 10px",
	items:[{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:130,
			cls:'my-label-style',
			text:'昨天采集网元数：'
		},{
			xtype:'label',
			id:'yesCollNeCount',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:160,
			cls:'my-label-style',
			text:'昨天成功采集网元数：'
		},{
			xtype:'label',
			id:'yesSucCollNeCount',
			cls:'my-label-style'
		}]
	},{
		xtype:'spacer',
		width:10,
		height:10
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'重要预警'
		},{
			xtype:'label',
			id:'importantWarning',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'次要预警'
		},{
			xtype:'label',
			id:'secondaryWarning',
			cls:'my-label-style'
		}]
	},{
		layout:'column',
		border:false,
		items:[{
			xtype:'label',
			width:90,
			cls:'my-label-style',
			text:'一般预警'
		},{
			xtype:'label',
			id:'commonlyWarning',
			cls:'my-label-style'
		}]
	}]
});

//主容器
var formPanel = new Ext.FormPanel({
	region : 'center',
	id : 'formPanel',
//	layout:{
//		type:'vbox',
//		align: 'center',
//	    pack: 'center'
//	},
	bodyStyle:"padding:50px 0px 0px 160px",
	items:[{
		layout:'column',
		border:false,
		items:[summaryFieldSet,new Ext.Spacer({
			width:30,
			height:10
		}),alarmFieldSet]
	},new Ext.Spacer({
		width:10,
		height:30
	}),{
		layout:'column',
		border:false,
		items:[taskFieldSet,new Ext.Spacer({
			width:30,
			height:10
		}),performanceFieldSet]
	}],
	split : false
});

var netTopoWin = new Ext.Window({
	id:'netTopoWin',
	x:0,
	y:50,
	width:120,
	height:35,
	baseCls:'my-window-no-border',
	closable:false,
	resizable:false,
	shadow:false,
	items:[{
		xtype:'button',
    	text:'网络拓扑图',
    	width:'100%',
    	height:35,
    	style:"background-color:#ebf1fa",
    	overCls:"btn-blue",
    	onClick:function(e){
//    		Ext.getCmp("currentTime").getEl().dom.style.color = 0x00ff00;
//    		alert(Ext.getCmp("currentTime").getEl().dom.innerHTML);
//    		Ext.getCmp("currentTime").getEl().dom.innerHTML = '<a href="javascript:void(0)" onclick="javascript:test();"' +
//    			'style="color: ' + '#00ff00' + ';text-decoration:none;font-size:24px">' + '王剑' + '</a>';
    		parent.addTabPage("../viewManager/Topo.jsp","网络拓扑图");
    	}
	}]
});

var currAlarmWin = new Ext.Window({
	id:'currAlarmWin',
	x:0,
	y:90,
	width:120,
	height:35,
	baseCls:'my-window-no-border',
	closable:false,
	resizable:false,
	shadow:false,
	items:[{
		xtype:'button',
    	text:'当前告警',
    	width:'100%',
    	height:35,
    	style:"background-color:#ebf1fa",
    	overCls:"btn-blue",
    	onClick:function(e){
    		parent.addTabPage("../faultManager/currentAlarm.jsp","当前告警");
    	}
	}]
});

var SDHCurrPMSearchWin = new Ext.Window({
	id:'SDHCurrPMSearchWin',
	x:0,
	y:130,
	width:120,
	height:35,
	baseCls:'my-window-no-border',
	closable:false,
	resizable:false,
	shadow:false,
	items:[{
		xtype:'button',
    	text:'SDH当前性能查询',
    	width:'100%',
    	height:35,
    	style:"background-color:#ebf1fa",
    	overCls:"btn-blue",
    	onClick:function(e){
    		parent.addTabPage("../performanceManager/PMsearch/SDHCurrent.jsp","SDH当前性能查询");
    	}
	}]
});

var WDMCurrPMSearchWin = new Ext.Window({
	id:'WDMCurrPMSearchWin',
	x:0,
	y:170,
	width:120,
	height:35,
	baseCls:'my-window-no-border',
	closable:false,
	resizable:false,
	shadow:false,
	items:[{
		xtype:'button',
    	text:'WDM当前性能查询',
    	width:'100%',
    	height:35,
    	style:"background-color:#ebf1fa",
    	overCls:"btn-blue",
    	onClick:function(e){
    		parent.addTabPage("../performanceManager/PMsearch/WDMCurrent.jsp","WDM当前性能查询");
    	}
	}]
});

var PMReportTaskWin = new Ext.Window({
	id:'PMReportTaskWin',
	x:0,
	y:210,
	width:120,
	height:35,
	baseCls:'my-window-no-border',
	closable:false,
	resizable:false,
	shadow:false,
	items:[{
		xtype:'button',
    	text:'性能报表定制',
    	width:'100%',
    	height:35,
    	style:"background-color:#ebf1fa",
    	overCls:"btn-blue",
    	onClick:function(e){
    		parent.addTabPage("../performanceManager/performanceReport/PMReportTask.jsp",
    				"性能报表定制");
    	}
	}]
});

var WDMOMSManagerWin = new Ext.Window({
	id:'WDMOMSManagerWin',
	x:0,
	y:250,
	width:120,
	height:35,
	baseCls:'my-window-no-border',
	closable:false,
	resizable:false,
	shadow:false,
	items:[{
		xtype:'button',
    	text:'WDM光复用段管理',
    	width:'100%',
    	height:35,
    	style:"background-color:#ebf1fa",
    	overCls:"btn-blue",
    	onClick:function(e){
    		parent.addTabPage("../performanceManager/opticalPathMonitorManager/multiplexSectionMonitor/multiSectionList.jsp",
    				"WDM光复用段管理");
    	}
	}]
});

function showAllWin(){
	netTopoWin.show();
	currAlarmWin.show();
	SDHCurrPMSearchWin.show();
	WDMCurrPMSearchWin.show();
	PMReportTaskWin.show();
	WDMOMSManagerWin.show();
}*/

Ext.Ajax.timeout = 600000;
function setSummaryValue(obj){
//    document.getElementById('currentTime').innerHTML=obj.userName;
    document.getElementById('loginTimeLabel').innerHTML=obj.loginTime;
    document.getElementById('logoutTimeLabel').innerHTML=obj.logoutTime;
    document.getElementById('southConnectCount').innerHTML=obj.southConnectCount + "个";
    document.getElementById('connectNormalCount').innerHTML=obj.connectNormal + '个';
    document.getElementById('connectDisconnectCount').innerHTML=obj.connectDisconnect + '个';
    document.getElementById('connectExceptionCount').innerHTML=obj.connectException + '个';
    document.getElementById('connectInterruptCount').innerHTML=obj.connectInterrupt + '个';
	/*Ext.getCmp('currentTime').setText(obj.userName);
	Ext.getCmp('loginTimeLabel').setText(obj.loginTime);
	Ext.getCmp('logoutTimeLabel').setText(obj.logoutTime);
	Ext.getCmp('southConnectCount').setText(obj.southConnectCount + '个');
	Ext.getCmp('connectNormalCount').setText(obj.connectNormal + '个');
	Ext.getCmp('connectDisconnectCount').setText(obj.connectDisconnect + '个');
	Ext.getCmp('connectExceptionCount').setText(obj.connectException + '个');
	Ext.getCmp('connectInterruptCount').setText(obj.connectInterrupt + '个');*/
}

function setAlarmValue(obj){
	document.getElementById('totalCRCount').innerHTML="<font color=\""+obj.colorCR+"\">"+obj.totalCR+"</font>";
    document.getElementById('newAddCRCount').innerHTML="<font color=\""+obj.colorCR+"\">"+obj.newAddCR+"</font>";
    document.getElementById('totalMJCount').innerHTML="<font color=\""+obj.colorMJ+"\">"+obj.totalMJ+"</font>";
    document.getElementById('newAddMJCount').innerHTML="<font color=\""+obj.colorMJ+"\">"+obj.newAddMJ+"</font>";
    document.getElementById('totalMNCount').innerHTML="<font color=\""+obj.colorMN+"\">"+obj.totalMN+"</font>";
    document.getElementById('newAddMNCount').innerHTML="<font color=\""+obj.colorMN+"\">"+obj.newAddMN+"</font>";
    document.getElementById('totalWRCount').innerHTML="<font color=\""+obj.colorWR+"\">"+obj.totalWR+"</font>";
    document.getElementById('newAddWRCount').innerHTML="<font color=\""+obj.colorWR+"\">"+obj.newAddWR+"</font>";
    // 设置鼠标指针属性
    document.getElementById('totalCRCount').setAttribute("style","cursor:pointer");
    document.getElementById('newAddCRCount').setAttribute("style","cursor:pointer");
    document.getElementById('totalMJCount').setAttribute("style","cursor:pointer");
    document.getElementById('newAddMJCount').setAttribute("style","cursor:pointer");
    document.getElementById('totalMNCount').setAttribute("style","cursor:pointer");
    document.getElementById('newAddMNCount').setAttribute("style","cursor:pointer");
    document.getElementById('totalWRCount').setAttribute("style","cursor:pointer");
    document.getElementById('newAddWRCount').setAttribute("style","cursor:pointer");
    // 设置鼠标单击属性
    document.getElementById('totalCRCount').setAttribute("onclick","openAlarm(1,false);");
    document.getElementById('newAddCRCount').setAttribute("onclick","openAlarm(1,true);");
    document.getElementById('totalMJCount').setAttribute("onclick","openAlarm(2,false);");
    document.getElementById('newAddMJCount').setAttribute("onclick","openAlarm(2,true);");
    document.getElementById('totalMNCount').setAttribute("onclick","openAlarm(3,false);");
    document.getElementById('newAddMNCount').setAttribute("onclick","openAlarm(3,true);");
    document.getElementById('totalWRCount').setAttribute("onclick","openAlarm(4,false);");
    document.getElementById('newAddWRCount').setAttribute("onclick","openAlarm(4,true);");
	/*Ext.getCmp('totalCRCount').getEl().dom.style.color = obj.colorCR;
	Ext.getCmp('totalCRCount').setText(obj.totalCR);
	Ext.getCmp('newAddCRCount').getEl().dom.style.color = obj.colorCR;
	Ext.getCmp('newAddCRCount').setText(obj.newAddCR);
	
	Ext.getCmp('totalMJCount').getEl().dom.style.color = obj.colorMJ;
	Ext.getCmp('totalMJCount').setText(obj.totalMJ);
	Ext.getCmp('newAddMJCount').getEl().dom.style.color = obj.colorMJ;
	Ext.getCmp('newAddMJCount').setText(obj.newAddMJ);
	
	Ext.getCmp('totalMNCount').getEl().dom.style.color = obj.colorMN;
	Ext.getCmp('totalMNCount').setText(obj.totalMN);
	Ext.getCmp('newAddMNCount').getEl().dom.style.color = obj.colorMN;
	Ext.getCmp('newAddMNCount').setText(obj.newAddMN);
	
	Ext.getCmp('totalWRCount').getEl().dom.style.color = obj.colorWR;
	Ext.getCmp('totalWRCount').setText(obj.totalWR);
	Ext.getCmp('newAddWRCount').getEl().dom.style.color = obj.colorWR;
	Ext.getCmp('newAddWRCount').setText(obj.newAddWR);*/
}

function setTaskValue(obj){
	document.getElementById('EMSAutoSYNCTaskNum').innerHTML=obj.EMSSYNCTaskNum + '个';
	document.getElementById('EMSAutoSYNCStartStatus').innerHTML=obj.EMSSYNCStartStatus + '个';
    document.getElementById('EMSAutoSYNCSuccess').innerHTML=obj.EMSSYNCSuccess + '个';
    
    document.getElementById('alarmAutoSYNCTaskNum').innerHTML=obj.alarmAutoSYNCTaskNum + '个';
    document.getElementById('alarmAutoSYNCStartStatus').innerHTML=obj.alarmAutoSYNCStartStatus + '个';
    document.getElementById('alarmAutoSYNCSuccess').innerHTML=obj.alarmAutoSYNCSuccess + '个';
    
    document.getElementById('pmCollTaskNum').innerHTML=obj.PMCollTaskNum + '个';
    document.getElementById('pmCollStartStatus').innerHTML=obj.PMCollStartStatus + '个';
    document.getElementById('pmCollSuccess').innerHTML=obj.PMCollSuccess + '个';
    
    document.getElementById('pmReportTaskNum').innerHTML=obj.pmReportTaskNum + '个';
    document.getElementById('pmReportStartStatus').innerHTML=obj.pmReportStartStatus + '个';
    document.getElementById('pmReportSuccess').innerHTML=obj.pmReportSuccess + '个';
    
    document.getElementById('cutOverTaskNum').innerHTML=obj.cutOverTaskNum + '个';
    document.getElementById('cutOverStartStatus').innerHTML=obj.cutOverStartStatus + '个';
    document.getElementById('cutOverSuccess').innerHTML=obj.cutOverSuccess + '个';
    
    document.getElementById('circuitAutoNewTaskNum').innerHTML=obj.circuitAutoNewTaskNum + '个';
    document.getElementById('circuitAutoNewStartStatus').innerHTML=obj.circuitAutoNewStartStatus + '个';
    document.getElementById('circuitAutoNewSuccess').innerHTML=obj.circuitAutoNewSuccess + '个';
    
    document.getElementById('dbBackupTaskNum').innerHTML=obj.dataBackupTaskNum + '次';
    document.getElementById('dbBackupStartStatus').innerHTML=obj.dataBackupStartStatus + '个';
    document.getElementById('dbBackupSuccess').innerHTML=obj.dataBackupSuccess + '次';
	
	/*Ext.getCmp('EMSAutoSYNCStartStatus').setText(obj.EMSSYNCStartStatus + '个');
	Ext.getCmp('EMSAutoSYNCYesExcute').setText(obj.EMSSYNCYesterdayExcute + '个');
	Ext.getCmp('EMSAutoSYNCSuccess').setText(obj.EMSSYNCSuccess + '个');
	
	Ext.getCmp('alarmAutoSYNCStartStatus').setText(obj.alarmAutoSYNCStartStatus + '个');
	Ext.getCmp('alarmAutoSYNCYesExcute').setText(obj.alarmAutoSYNCYesterdayExcute + '次');
	Ext.getCmp('alarmAutoSYNCSuccess').setText(obj.alarmAutoSYNCSuccess + '次');
	
	Ext.getCmp('pmCollStartStatus').setText(obj.PMCollStartStatus + '个');
	Ext.getCmp('pmCollYesExcute').setText(obj.PMCollYesterdayExcute + '个');
	Ext.getCmp('pmCollSuccess').setText(obj.PMCollSuccess + '个');
	
	Ext.getCmp('pmReportStartStatus').setText(obj.pmReportStartStatus + '个');
	Ext.getCmp('pmReportYesExcute').setText(obj.pmReportYesterdayExcute + '个');
	Ext.getCmp('pmReportSuccess').setText(obj.pmReportSuccess + '个');
	
	Ext.getCmp('cutOverStartStatus').setText(obj.cutOverStartStatus + '个');
	Ext.getCmp('cutOverYesExcute').setText(obj.cutOverYesterdayExcute + '个');
	Ext.getCmp('cutOverSuccess').setText(obj.cutOverSuccess + '个');
	
	Ext.getCmp('circuitAutoNewStartStatus').setText(obj.circuitAutoNewStartStatus + '个');
	Ext.getCmp('circuitAutoNewYesExcute').setText(obj.circuitAutoNewYesterdayExcute + '个');
	Ext.getCmp('circuitAutoNewSuccess').setText(obj.circuitAutoNewSuccess + '个');
	
	Ext.getCmp('dbBackupStartStatus').setText(obj.dataBackupStartStatus + '个');
	Ext.getCmp('dbBackupYesExcute').setText(obj.dataBackupYesterdayExcute + '次');
	Ext.getCmp('dbBackupSuccess').setText(obj.dataBackupSuccess + '次');*/
}

function setPMValue(obj){
	document.getElementById('yesCollNeCount').innerHTML=obj.collectedNe + "网元";
    document.getElementById('yesSucCollNeCount').innerHTML=obj.collectSucceedNe + "网元";
    document.getElementById('importantWarning').innerHTML=obj.pmException1;
    document.getElementById('secondaryWarning').innerHTML=obj.pmException2;
    document.getElementById('commonlyWarning').innerHTML=obj.pmException3;
	
	/*Ext.getCmp('yesCollNeCount').setText(obj.collectedNe + "网元");
	Ext.getCmp('yesSucCollNeCount').setText(obj.collectSucceedNe + "网元");
	
	Ext.getCmp('importantWarning').setText(obj.pmException1);
	Ext.getCmp('secondaryWarning').setText(obj.pmException2);
	Ext.getCmp('commonlyWarning').setText(obj.pmException3);*/
}

function init(){
	Ext.get("userPanel").mask("刷新中...");
	Ext.get("taskPanel").mask("刷新中...");
	Ext.get("almPanel").mask("刷新中...");
	// 获取时间
	var serverNow = getServerTime();
	
	Ext.Ajax.request({
        url: "login!getParamFP.action",
        method : 'POST',
//        params:{},
        scope:this,
        success: function(response) {
//            top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
	    	if(obj&&(0==obj.returnResult)){
	    		Ext.Msg.alert("错误",obj.returnMessage);
	    	}else{
		    	setSummaryValue(obj);
		    	setAlarmValue(obj);
		    	setTaskValue(obj);
	    	}
	    	Ext.get("userPanel").unmask();
	    	Ext.get("almPanel").unmask();
	    	Ext.get("taskPanel").unmask();
	    },
	    error:function(response) {
//	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    	Ext.get("userPanel").unmask();
	    	Ext.get("almPanel").unmask();
	    	Ext.get("taskPanel").unmask();
	    },
	    failure:function(response) {
//	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    	Ext.get("userPanel").unmask();
	    	Ext.get("almPanel").unmask();
	    	Ext.get("taskPanel").unmask();
	    }
    });
}
function initPmInfo(){
	Ext.get("pmPanel").mask("刷新中...");
	Ext.Ajax.request({
        url: "login!getIndexPmInfo.action",
        method : 'POST',
//        params:{},
        scope:this,
        success: function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if(obj&&(0==obj.returnResult)){
	    		Ext.Msg.alert("错误",obj.returnMessage);
	    	}else{
	    		setPMValue(obj);
	    	}
	    	Ext.get("pmPanel").unmask();
	    },
	    error:function(response) {
//	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    	Ext.get("pmPanel").unmask();
	    },
	    failure:function(response) {
//	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    	Ext.get("pmPanel").unmask();
	    }
    });
}

var LeftMenuList;
function leftMenuInit(){
	var menuIds = new Array();
	
	menuIds.push(4010000);
	menuIds.push(5010000);
	menuIds.push(6010000);
	menuIds.push(6040000);
	menuIds.push(6131000);
	menuIds.push(10070000);
	
	var menuCss = new Array();
	
	menuCss.push("network");
	menuCss.push("alarm");
	menuCss.push("SDHPM");
	menuCss.push("WDMPM");
	menuCss.push("pmReport");
	menuCss.push("WDMOptical");
	
	Ext.Ajax.request({
        url: "menu!getMenuList.action",
        method : 'POST',
        params:{"menuIds":menuIds},
        scope:this,
        success: function(response) {
	    	var responseArray = Ext.util.JSON.decode(response.responseText);
	    	if(responseArray&&(0==responseArray.returnResult)){
    			Ext.Msg.alert("错误",responseArray.returnMessage);
	    	}else{
		    	LeftMenuList=responseArray;
		    	var innerHtml = "";
		    	for (var i = 0; i < responseArray.length; i++) {
		    	  var disabled = responseArray[i].DISABLED;
				  //var authSequence = responseArray[i].AUTH_SEQUENCE;
				  //var text = responseArray[i].MENU_DISPLAY_NAME;
				  var menuId = responseArray[i].SYS_MENU_ID;
				  if(disabled == false){
					  for(var j=0;j<menuIds.length;j++){
						  if(menuId == menuIds[j]){
							  innerHtml += '<img id="'+menuCss[j]+'" style="margin-top:5px;" onclick="javascript:showNewTab(\''+i+'\')" onmouseover="move(\''+menuCss[j]+'\',\''+menuCss[j]+'On\');" onmouseout="move(\''+menuCss[j]+'\',\''+menuCss[j]+'Title\');" src="../../resource/images/otherImages/'+menuCss[j]+'Title.png"/>';
						  }
					  }
	//			  	if(menuId == menuIds[0]){
	//			  		innerHtml = '<img id="netWork"  onclick="javascript:showNewTab(1,\''+authSequence+'\')" onmouseover="move(\'netWork\',\'networkOn\');" onmouseout="move(\'netWork\',\'networkTitle\');" src="../../resource/images/otherImages/networkTitle.png"/>';
	//			  	}else if(menuId == menuIds[1]){
	//			  		innerHtml += '<img id="alarm" style="margin-top:5px;" onclick="javascript:showNewTab(2,\''+authSequence+'\')" onmouseover="move(\'alarm\',\'alarmOn\');" onmouseout="move(\'alarm\',\'alarmTitle\');" src="../../resource/images/otherImages/alarmTitle.png"/>';
	//			  	}else if(menuId == menuIds[2]){
	//			  		innerHtml += '<img id="SDHPm" style="margin-top:5px;" onclick="javascript:showNewTab(3,\''+authSequence+'\')" onmouseover="move(\'SDHPm\',\'SDHPMOn\');" onmouseout="move(\'SDHPm\',\'SDHPMTitle\');" src="../../resource/images/otherImages/SDHPMTitle.png"/>';
	//			  	}else if(menuId == menuIds[3]){
	//			  		innerHtml += '<img id="WDMPm" style="margin-top:5px;" onclick="javascript:showNewTab(4,\''+authSequence+'\')" onmouseover="move(\'WDMPm\',\'WDMPMOn\');" onmouseout="move(\'WDMPm\',\'WDMPMTitle\');" src="../../resource/images/otherImages/WDMPMTitle.png"/>';
	//			  	}else if(menuId == menuIds[4]){
	//			  		innerHtml += '<img id="pmReport" style="margin-top:5px;" onclick="javascript:showNewTab(5,\''+authSequence+'\')" onmouseover="move(\'pmReport\',\'pmReportOn\');" onmouseout="move(\'pmReport\',\'pmReportTitle\');" src="../../resource/images/otherImages/pmReportTitle.png"/>';
	//			  	}else if(menuId == menuIds[5]){
	//			  		innerHtml += '<img id="WDMOptical" style="margin-top:5px;" onclick="javascript:showNewTab(6,\''+authSequence+'\')" onmouseover="move(\'WDMOptical\',\'WDMOpticalOn\');" onmouseout="move(\'WDMOptical\',\'WDMOpticalTitle\');" src="../../resource/images/otherImages/WDMOpticalTitle.png"/>';
	//			  	}
				  }
		    	}
		    	document.getElementById('left').innerHTML= innerHtml;
    		}
	    	
	    },
	    error:function(response) {
//	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    },
	    failure:function(response) {
//	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    }
    });
}


function showNewTab(index){
   top.addTabPage(LeftMenuList[index]['MENU_HREF'],LeftMenuList[index]['MENU_DISPLAY_NAME'],LeftMenuList[index]['AUTH_SEQUENCE']);
}

Ext.onReady(function(){
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};

	//Ext.getBody().mask('数据加载中...');
	//alert(parent.leftMenuArray);	
	/*var win = new Ext.Viewport({
		id:"viewport",
		loadMask : true,
		layout: 'border',
		items: [formPanel],
		renderTo : Ext.getBody()
	});
	
	showAllWin();*/
	leftMenuInit();
	initPmInfo();
	init();
	
	//Ext.getBody().unmask();
});

//取得服务器时间
function getServerTime() {
	Ext.Ajax.request({
				url : 'common!getCurrentTime.action',
				method : 'POST',
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					serverNow = parseInt(obj.returnMessage);
					var myDate = new Date();
					delta = serverNow - myDate.getTime();
					setInterval(getCurrentTime, 1000);
				},
				failure : function(response) {
					Ext.Msg.alert("超时", response.responseText);
				}
			});
}

// 取得当前时间
function getCurrentTime() {
	var now = new Date();
	var date = new Date(delta + now.getTime());
	var month = date.getMonth() + 1;
//	month.toString().length >= 2 ? month = month.toString() : month = '0'
//			+ month.toString();
	var day = date.getDate();
//	day.toString().length >= 2 ? day = day.toString() : day = '0'
//			+ day.toString();
	var hours = date.getHours();
	hours.toString().length >= 2 ? hours = hours.toString() : hours = '0'
			+ hours.toString();
	var minutes = date.getMinutes();
	minutes.toString().length >= 2
			? minutes = minutes.toString()
			: minutes = '0' + minutes.toString();
	var seconds = date.getSeconds();
	seconds.toString().length >= 2
			? seconds = seconds.toString()
			: seconds = '0' + seconds.toString();
	var dateString = date.getFullYear() + '年' + month + '月' + day + '日 ' + hours
			+ ':' + minutes + ':' + seconds;
	
	document.getElementById('currentTime').innerHTML=dateString;
}

function openAlarm(severity,isToday) {
	var menuIds = new Array();
	menuIds.push(5010000);
	Ext.Ajax.request({
        url: "menu!getMenuList.action",
        method : 'POST',
        params:{"menuIds":menuIds},
        scope:this,
        success: function(response) {
	    	var responseArray = Ext.util.JSON.decode(response.responseText);
	    	if(responseArray&&(0==responseArray.returnResult)){
    			Ext.Msg.alert("错误",responseArray.returnMessage);
	    	}else{
	    		var url = responseArray[0].MENU_HREF + '?fp_severity=' + severity +'&fp_isToday='+isToday;
    		    parent.addTabPage(url,"当前告警",responseArray[0].AUTH_SEQUENCE);
    		}
	    },
	    error:function(response) {
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    },
	    failure:function(response) {
	    	Ext.Msg.alert("错误","初始化加载失败"+"<BR>Status:"+response.statusText||"unknow");
	    }
    });
}
