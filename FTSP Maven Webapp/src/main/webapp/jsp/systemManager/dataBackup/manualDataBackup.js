var manualBackupPanel;
(function() {
	// 数据库选择
	var dataBaseGroup = new Ext.form.CheckboxGroup({
		fieldLabel : '数据库 ',
		width : 400,
		id : 'dataBaseGroup',
		name : 'dataBaseGroup',
		items : [ {
			name : 'dataBase',
			id : 'manualPrimaryDatabase',
			inputValue : 1,
			boxLabel : '主数据库',
			checked : true
		}, {
			name : 'dataBase',
			id : 'manualDialogDatabase',
			inputValue : 2,
			boxLabel : '告警及日志数据库',
			checked : true
		} ]
	});
	// 备份路径
	var manuBackupPathHTML = new Ext.form.Label({
		id : 'manuBackupPathHTML',
		fieldLabel : '备份路径'
	});
	// 备份路径选择
	var isBackUpCb;
	(function() {
		var data = [ [ 0, '不备份文件' ], [ 1, '备份文件' ] ];
		var store = new Ext.data.ArrayStore({
			fields : [ {
				name : 'id'
			}, {
				name : 'isBackUp'
			} ]
		});
		store.loadData(data);
		isBackUpCb = new Ext.form.ComboBox({
			id : 'isBackUpCb',
			triggerAction : 'all',
			mode : 'local',
			fieldLabel : '是否备份文件',
			width : 300,
			editable : false,
			store : store,
			value : 0,
			valueField : 'id',
			displayField : 'isBackUp',
			listeners : {
				'select' : function(combo, record, index) {
					if (combo.getValue() == 1) {
						Ext.getCmp('backupPathCF').show();
					}
					if (combo.getValue() == 0) {
						Ext.getCmp('backupPathCF').hide();
					}
				}
			}
		});
	})();
	var backupPathCF = new Ext.form.CompositeField({
		id : 'backupPathCF',
		fieldLabel : '备份后文件拷贝到',
		hidden : true,
		items : [ {
			xtype : 'textfield',
			id : 'backupPath',
			name : 'backupPath',
			emptyText : '请输入正确格式,例如  c:\\databackup\\autocopy',
			width : 300,
			allowBlank : true,
		}, {
			xtype : 'button',
			text : "路径检查",
			width : 60,
			listeners : {
				"click" : function() {
					var bPath = Ext.getCmp('backupPath').getValue();
					if (bPath == undefined || bPath == null || bPath == '') {
						Ext.Msg.alert("提示", "请输入备份的拷贝路径!");
						return;
					}
					checkPathAndCreatePath();
				}
			}
		} ]
	});
	manualBackupPanel = new Ext.FormPanel({
		id : 'manualBackupPanel',
		title : '手动备份',
		height : 200,
		labelWidth : 150,
		bodyStyle : 'padding:20px 20px 0 20px',
		items : [ dataBaseGroup, manuBackupPathHTML, isBackUpCb, backupPathCF ],
		tbar : [ {
			text : '立即备份',
			icon : '../../../resource/images/btnImages/control_play_blue.png',
			handler : beginBackup
		} ]
	});
})();


//判断路径是否合法
function judgePathIsLegal(path){
	if(path.substr(-1)!='/') path+='/';
    // 盘符为a-z ,路径不能包含特殊字符 \/:*?"<>|
    if(/^[a-z]\:\\([^\/:*?"<>|]+\/)+$/i.test(path)){
        return true;
    }
    return false;
}

function checkPathAndCreatePath() {
	if(!judgePathIsLegal(Ext.getCmp('backupPath').getValue())){
		Ext.Msg.alert("错误","请输入合法的文件路径");
		return;
	}
	
	
	
	
	Ext.Ajax.request({
		timeout : 9000000,
		url : 'data-backup!checkPathIsExists.action',
		method : 'POST',
		async : false,
		params : {
			'copyPath' : Ext.getCmp('backupPath').getValue()
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnMessage == 'true') {
				Ext.Msg.alert("提示", '路径已存在');
				return;
			} else {
				Ext.MessageBox.confirm("选择框", "拷贝路径不存在,是否创建路径", function(add) {
					if (add == 'yes') {
						beginCreatePath(Ext.getCmp('backupPath').getValue());
					} else {
						return;
					}
				});
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

function beginCreatePath(path) {
	Ext.Ajax.request({
		timeout : 9000000,
		url : 'data-backup!beginCreatePath.action',
		method : 'POST',
		async : false,
		params : {
			'copyPath' : path
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnMessage == 'true') {
				Ext.Msg.alert("提示", '路径创建成功');
				return;
			} else {
				Ext.Msg.alert("提示", '路径创建失败');
				return;
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

// 检查copy路径是否存在
function checkCopyPathIsExists() {
	if(!judgePathIsLegal(Ext.getCmp('backupPath').getValue())){
		Ext.Msg.alert("错误","请输入合法的文件路径");
		return;
	}
	
	
	
	Ext.Ajax.request({
		timeout : 9000000,
		url : 'data-backup!checkPathIsExists.action',
		method : 'POST',
		async : false,
		params : {
			'copyPath' : Ext.getCmp('backupPath').getValue()
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnMessage == 'true') {
				executeBeginBackup();
				return;
			} else {
				Ext.MessageBox.confirm("选择框", "拷贝路径不存在,是否创建路径", function(add) {
					if (add == 'yes') {
						executeBeginBackup();
					} else {
						return;
					}
				});
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		}
	});

}

function beginBackup() {
	var isCopy = 1;
	if (Ext.getCmp('isBackUpCb').getValue() == 1) {
		var bPath = Ext.getCmp('backupPath').getValue();
		if (bPath == undefined || bPath == null || bPath == '') {
			Ext.Msg.alert("提示", "请输入备份的拷贝路径!");
			return;
		}
		isCopy = 0;
	} else {
		isCopy = 1;
	}
	if (isCopy == 0) {
		checkCopyPathIsExists();
	} else {
		executeBeginBackup();
		return;
	}

}

function executeBeginBackup() {
	var isCopy = 1;
	if (Ext.getCmp('isBackUpCb').getValue() == 1) {
		var bPath = Ext.getCmp('backupPath').getValue();
		if (bPath == undefined || bPath == null || bPath == '') {
			Ext.Msg.alert("提示", "请输入备份的拷贝路径!");
			return;
		}
		isCopy = 0;
	} else {
		isCopy = 1;
	}
	var pri = 0;
	var sec = 0;
	if (Ext.getCmp('manualPrimaryDatabase').checked) {
		pri = 0;
	} else {
		pri = 1;
	}
	if (Ext.getCmp('manualDialogDatabase').checked) {
		sec = 0;
	} else {
		sec = 1;
	}
	
	if(pri==1 && sec==1){
		Ext.Msg.alert("提示", "请选择需要备份的数据库!");
		return;
	}
	
	var database = pri + ',' + sec;
	Ext.MessageBox.wait('正在执行,请稍等...');
	Ext.Ajax.request({
		timeout : 9000000,
		url : 'data-backup!beginBackup.action',
		method : 'POST',
		params : {
			'database' : database,
			'isCopy' : isCopy,
			'copyPath' : Ext.getCmp('backupPath').getValue()
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.MessageBox.hide();
			Ext.Msg.alert("信息", obj.returnMessage);
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.MessageBox.hide();
			Ext.Msg.alert("错误", response.responseText);
		}
	});

}
