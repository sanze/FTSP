var fitPanel = new Ext.Panel({
	id : 'fitPanel',
	bodyStyle : 'padding:20px 20px 0 20px',
	autoScroll : true,
	collapsible : false,
	collapsed : false,
	border : false,
	items : [ manualBackupPanel, new Ext.Spacer({ // 占位
		id : 'chart1',
		height : 20
	}), autoBackupPanel ]
});

var backupPath;
var autoValuePri;
var autoValueSec;

function setDatabaseSettings(autoValue){
	if(autoValue=='null'){
		Ext.getCmp('isCopyBackup').setValue(0);
		Ext.getCmp('backupPathCF2').hide();
		Ext.getCmp('autoCopyPath').setValue('');
		Ext.getCmp('retainAmount').setValue(3);
		Ext.getCmp('timeFinenessCombo').setValue('day');
		Ext.getCmp('dateChoose').setValue('');
		Ext.getCmp('beginDate').setValue('');
		Ext.getCmp('summary').setValue('');
		return;
	}
	var aValues=autoValue.split(',');
	if(aValues[0]=='0'){
		Ext.getCmp('autoDataChooseName1').setValue(true);
	}else{
		Ext.getCmp('autoDataChooseName2').setValue(true);
	}
	if(aValues[1]=='0'){
		Ext.getCmp('isAutoBackupName1').setValue(true);
	}else{
		Ext.getCmp('isAutoBackupName2').setValue(true);
	}
	if(aValues[2]=='0'){
		Ext.getCmp('isCopyBackup').setValue(1);
		Ext.getCmp('backupPathCF2').show();
	}else{
		Ext.getCmp('isCopyBackup').setValue(0);
		Ext.getCmp('backupPathCF2').hide();
	}
	Ext.getCmp('autoCopyPath').setValue(aValues[3]);
	Ext.getCmp('retainAmount').setValue(aValues[4]);
	Ext.getCmp('timeFinenessCombo').setValue(aValues[5]);
	var startTime=aValues[6];
	Ext.getCmp('dateChoose').setValue(startTime.split(' ')[1]);
	setBeginDate(startTime.split(' ')[1]);
}


// 获取手动和自动备份路径
function getBackupPath() {
	Ext.Ajax.request({
		url : 'data-backup!getBackupPath.action',
		method : 'POST',
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			backupPath = obj;
			Ext.getCmp('manuBackupPathHTML').setText(obj.manualMysqlPath);
			Ext.getCmp('autoBPHTML').setText(obj.autoMysqlPath);
			
			var manuValue=obj.manuValue;
			autoValuePri=obj.autoValuePri;
			autoValueSec=obj.autoValueSec;
			if(manuValue!='null'){
				var mValues=manuValue.split(',');
				if(mValues[0]=='0'){
					Ext.getCmp('manualPrimaryDatabase').setValue(true);
				}else{
					Ext.getCmp('manualPrimaryDatabase').setValue(false);
				}
				if(mValues[1]=='0'){
					Ext.getCmp('manualDialogDatabase').setValue(true);
				}else{
					Ext.getCmp('manualDialogDatabase').setValue(false);
				}
				if(mValues[2]=='0'){
					Ext.getCmp('isBackUpCb').setValue(1);
					Ext.getCmp('backupPathCF').show();
				}else{
					Ext.getCmp('isBackUpCb').setValue(0);
					Ext.getCmp('backupPathCF').hide();
				}
				Ext.getCmp('backupPath').setValue(mValues[3]);
			}
			if(autoValuePri!='null'){
				setDatabaseSettings(autoValuePri);
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}
	Ext.Msg = top.Ext.Msg;
	// 获取手动和自动备份路径
	getBackupPath();

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'fit',
		items : [ fitPanel ]
	});
	win.show();
});
