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

//--------------------------------------------------------



/* 【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

var grid = new Ext.grid.GridPanel({
	id : "centerPanel",
	region : "center",
	 title:TARGET_NAME+'选择',
	store : store,
	cm : cm,
	selModel : sm,
	autoScroll : true,
	stripeRows : true,
	tbar : [ '-',{
		text : '新增',
		privilege:addAuth,
		icon : '../../../resource/images/btnImages/add.png',
		handler : function(){addOrEditTarget(1);}
	},'-', {
		text : '修改',
		privilege:modAuth,
		icon : '../../../resource/images/btnImages/modify.png',
		handler : function(){addOrEditTarget(2);}
	}, '-',{
		text : '删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function(){deleteTarget(1);}
	}, '-',{
		text : '清空',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function(){deleteTarget(2);}
	} ],
	buttons : [ '->', {
		text : '立即生成报表',
		privilege:addAuth,
		//icon : '../../../resource/images/buttonImages/submit.png',
		handler : beforeGen
	} ]
});

var northPanel = new Ext.form.FormPanel({
	id : 'northPanel',
	title : '报表设置',
	plugins : [ Ext.ux.PanelCollapsedTitle ], // 折叠后显示title
	region : 'north',
	padding : '10',
	labelAlign : 'left',
	height :190,
	autoScroll:true,
	collapsible : true,
	items : [ rowI, rowII ]
});
/**【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

/* 【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

/**
 * 生成之前的检查
 */
function beforeGen() {
	if (Ext.getCmp('northPanel').getForm().isValid()) {
		if (store.getCount() < 1) {
			Ext.Msg.alert('信息', '请添加'+TARGET_NAME+"!");
			return;
		}
		generateImmediately();
	}
}

/**
 * 生成报表
 */
function generateImmediately() {
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
				'targetId' : record.get('RESOURCE_UNIT_MANAGE_ID')
			};
			list.push(Ext.encode(nodes));
	});
	var params = {
		'modifyList' : list,
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
/**【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】【_】*/

Ext.onReady(function() {
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
//			Ext.getCmp('WDMTPLevel').items.get(0).disable();
//			Ext.getCmp('WDMPhysical').items.get(0).disable();
//			Ext.getCmp('WDMPhysical').items.get(1).disable();
			win.show();
		});