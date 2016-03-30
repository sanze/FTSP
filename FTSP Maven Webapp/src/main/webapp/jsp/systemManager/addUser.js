

var departmentStore = new Ext.data.Store({
        url : 'user-management!getDepartment.action',
        reader : new Ext.data.JsonReader({
            root : "rows"
        }, ["DEPARTMENT"])
    });

departmentStore.load({
    callback : function (r, options, success) {
        if (success) {}
        else {
            Ext.Msg.alert('错误', '查询失败！请重新查询');
        }
    }
});

var positionStore = new Ext.data.Store({
        url : 'user-management!getPosition.action',
        baseParams : {},
        reader : new Ext.data.JsonReader({
            root : "rows"
        }, ["POSITION"])
    });

positionStore.load({
    callback : function (r, options, success) {
        if (success) {}
        else {
            Ext.Msg.alert('错误', '查询失败！请重新查询');
        }
    }
});

function isExistAuthAtStore(dId, userList) {
    for (var i = 0; i < userList.length; i++) {
        if (userList[i].SYS_AUTH_DOMAIN_ID == dId) {
            return true;
        }
    }
    return false;
}

function isExistDeviceAtStore(dId, userList) {
    for (var i = 0; i < userList.length; i++) {
        if (userList[i].SYS_DEVICE_DOMAIN_ID == dId) {
            return true;
        }
    }
    return false;
}

var authDomainStore = new Ext.data.Store({
        url : 'user-management!getAuthDomain.action',
        baseParams : {
            "userGroupId" : "0"
        },
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, [
                "SYS_AUTH_DOMAIN_ID", "AUTH_DOMAIN_NAME"
            ])
    });

authDomainStore.load({
    callback : function (r, options, success) {
        if (success) {
            if (saveType == 1) {
                Ext.Ajax.request({
                    url : 'user-management!getDetailByUserId.action',
                    method : 'POST',
                    //async: false,
                    params : {
                        "sysUserId" : sysUserId
                    },
                    success : function (response) {
                        var obj = Ext.decode(response.responseText);
                        var authDetail = obj.currentAuthDomain;
                        var authRowCount = authDomainStore.getCount();
                        if (saveType == 1) {
                            if (authDetail != null) {
                                for (var j = 0; j < authRowCount; j++) {
                                    for (var i = 0; i < authDetail.length; i++) {
                                        if (authDetail[i].SYS_AUTH_DOMAIN_ID == authDomainStore.getAt(j).get("SYS_AUTH_DOMAIN_ID")) {
                                            authDomainPanel.getSelectionModel().selectRow(j, true);
                                        }
                                    }
                                }
                            }
                        }

                    },
                    error : function (response) {
                        Ext.Msg.alert("错误", response.responseText);
                    },
                    failure : function (response) {
                        Ext.Msg.alert("错误", response.responseText);
                    }
                })
            }

        } else {
            Ext.Msg.alert('错误', '查询失败！请重新查询');
        }
    }
});

var deviceDomainStore = new Ext.data.Store({
        url : 'user-management!getDeviceDomain.action',
        baseParams : {
            "userGroupId" : "0"
        },
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, ["SYS_DEVICE_DOMAIN_ID", "DEVICE_DOMAIN_NAME"])
    });

deviceDomainStore.load({
    callback : function (r, options, success) {
        if (success) {
            if (saveType == 1 || saveType == 2) {
                Ext.Ajax.request({
                    url : 'user-management!getDetailByUserId.action',
                    method : 'POST',
                    params : {
                        "sysUserId" : sysUserId
                    },
                    success : function (response) {
                        var obj = Ext.decode(response.responseText);
                        var deviceDetail = obj.currentDeviceDomain;
                        var deviceRowCount = deviceDomainStore.getCount();
                        if (saveType == 2) {
                            var arr = [];
                            for (var i = 0; i < deviceRowCount; i++) {
                                var isE = isExistDeviceAtStore(deviceDomainStore.getAt(i).get("SYS_DEVICE_DOMAIN_ID"), deviceDetail); //判断数据源中数据在用户列表中是否存在
                                if (!isE) {
                                    arr.push(deviceDomainStore.getAt(i));
                                }
                            }
                            for (var j = 0; j < arr.length; j++) {
                                deviceDomainStore.remove(arr[j]);
                            }
                        } else if (saveType == 1) {
                            if (deviceDetail != null) {
                                for (var j = 0; j < deviceRowCount; j++) {
                                    for (var i = 0; i < deviceDetail.length; i++) {
                                        if (deviceDetail[i].SYS_DEVICE_DOMAIN_ID == deviceDomainStore.getAt(j).get("SYS_DEVICE_DOMAIN_ID")) {
                                            deviceDomainPanel.getSelectionModel().selectRow(j, true);
                                        }
                                    }
                                }
                            }
                        }
                    },
                    error : function (response) {
                        Ext.Msg.alert("错误", response.responseText);
                    },
                    failure : function (response) {
                        Ext.Msg.alert("错误", response.responseText);
                    }
                })
            }

        } else {
            Ext.Msg.alert('错误', '查询失败！请重新查询');
        }
    }
});





var userName = new Ext.form.TextField({
        id : 'userName',
        name : 'userName',
        fieldLabel : '用户名',
        sideText : '<font color=red>*</font>',
//        width : 120,
//        height : 20,
        emptyText : '请输入用户名',
        allowBlank : false,
        maxLength : 10,
        maxLengthText : '姓名最大长度不能超过10个字符!'
    });

var loginName = new Ext.form.TextField({
        id : 'loginName',
        name : 'loginName',
//        bodyStyle : 'padding:10px 50px 100px',
        fieldLabel : '登录名',
        sideText : '<font color=red>*</font>',
        emptyText : '请输入登录名',
//        width : 120,
//        height : 20,
        allowBlank : false,
        maxLength : 10,
        maxLengthText : '登陆名最大长度不能超过10个字符!'
    });

var jobNumber = new Ext.form.TextField({
        id : 'jobNumber',
        name : 'jobNumber',
        fieldLabel : '工号',
        sideText : '<font color=red>*</font>',
        emptyText : '请输入工号',
        allowBlank : false,
//        width : 120,
//        height : 20,
        maxLength : 20,
        maxLengthText : '工号最大长度不能超过20个字符!'
    });

var telephone = new Ext.form.TextField({
        id : 'telephone',
        name : 'telephone',
        fieldLabel : '电话号码',
        sideText : '<font color=red>*</font>',
        emptyText : '请输入电话号码 ',
        allowBlank : false,
        minLength : 4,
        maxLength : 24,
//        width : 120,
//        height : 20,
        regex : /^[\d]{4,24}$/,
        regexText : '请输入正确的手机号码!'
    });
var timeOut = new Ext.form.TextField({
        id : 'timeout',
        name : 'timeout',
        fieldLabel : '超时时间',
        sideText : '<font color=red>*</font>',
        emptyText : '请输入超时时间（单位：分钟）!',
        allowBlank : false,
//        width : 120,
//        height : 20,
        regex : /^\d+(\.\d+)?$/,
        regexText : '请输入超时时间（单位：分钟）!'
    });

var email = new Ext.form.TextField({
        id : 'email',
        name : 'email',
        fieldLabel : '邮箱',
        //emptyText:'请输入邮箱',
        allowBlank : true,
//        width : 120,
//        height : 20,
        maxLength : 50,
        vtype : "email", //email格式验证
        vtypeText : "不是有效的邮箱地址"//错误提示信息,默认值我就不说了
    });

var note = new Ext.form.TextField({
        id : 'note',
        name : 'note',
//        width:855,
        anchor:'95%',
//        height : 20,
        fieldLabel : '备注',
        emptyText : '请输入备注',
        allowBlank : true,
        maxLength : 200
    });

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
        singleSelect : false,
        handleMouseDown : Ext.emptyFn
    });

var checkboxSelectionModel1 = new Ext.grid.CheckboxSelectionModel({
        singleSelect : false,
        handleMouseDown : Ext.emptyFn
    });

var department = new Ext.form.ComboBox({
        id : 'department',
        name : 'department',
        fieldLabel : '部门',
        //displayField:"SERVICE_NAME",
        //valueField:'SYS_SVC_RECORD_ID',
        selectOnFoucs : true,
        mode : "local",
        emptyText : "请选择用户所属部门",
        displayField : 'DEPARTMENT',
        valueField : 'DEPARTMENT',
        editable : true,
//        width : 120,
//        height : 20,
        anchor:'88%',
        maxLength : 20,
        maxLengthText : '最大长度不能超过20个字符!',
        store : departmentStore,
        allowBlank : true,
        triggerAction : 'all',
    	resizable: true,
        listeners : {
            select : function (combo, record, index) {}
        }
    });

var position = new Ext.form.ComboBox({
        id : 'position',
        name : 'position',
        fieldLabel : '职务',
        //displayField:"SERVICE_NAME",
        //valueField:'SYS_SVC_RECORD_ID',
        selectOnFoucs : true,
        mode : "local",
        emptyText : "请选择用户所属职务",
        displayField : 'POSITION',
        valueField : 'POSITION',
        editable : true,
//        width : 120,
//        height : 20,
        anchor:'88%',
        maxLength : 20,
        maxLengthText : '最大长度不能超过20个字符!',
        store : positionStore,
        allowBlank : true,
        triggerAction : 'all',
    	resizable: true,
        listeners : {
            select : function (combo, record, index) {}
        }
    });

var deviceDomainCM = new Ext.grid.ColumnModel({
        defaults : {
            sortable : true,
            forceFit : false
        },
        columns : [new Ext.grid.RowNumberer({
    		width : 26
    	}), checkboxSelectionModel1, {
                id : 'SYS_DEVICE_DOMAIN_ID',
                header : 'id',
                dataIndex : 'SYS_DEVICE_DOMAIN_ID',
                hidden : true
            }, {
                id : 'DEVICE_DOMAIN_NAME',
                header : '设备域名称',
                width : 140,
                dataIndex : 'DEVICE_DOMAIN_NAME',
                editor : new Ext.form.TextField({
                    allowBlank : false,
                    maxValue : 40,
                    minValue : 1
                })
            }
        ]
    });

var deviceDomainPanel = new Ext.grid.GridPanel({
        id : "deviceDomainPanel",
        height :  (Ext.getBody().getHeight()-160)*0.7, 
        width : 400,
        height:280,
        border : true,
        autoScroll : true,
        frame : false,
        cm : deviceDomainCM,
        stripeRows:true,
        store : deviceDomainStore,
        multiSelect : true,
        loadMask : true,
        sm : checkboxSelectionModel1,
        selModel : checkboxSelectionModel1, // 必须加不然不能选checkbox
        viewConfig : {
            forceFit : true
        }
    });

var authDomainCM = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults : {
            sortable : true,
            forceFit : false
        },
        columns : [new Ext.grid.RowNumberer({
    		width : 26
    	}), checkboxSelectionModel, {
                id : 'SYS_AUTH_DOMAIN_ID',
                header : 'id',
                dataIndex : 'SYS_AUTH_DOMAIN_ID',
                hidden : true
            }, {
                id : 'AUTH_DOMAIN_NAME',
                header : '权限域名称',
                width : 140,
                dataIndex : 'AUTH_DOMAIN_NAME',
                editor : new Ext.form.TextField({
                    allowBlank : false,
                    maxValue : 40,
                    minValue : 1
                })
            }
        ]
    });

var authDomainPanel = new Ext.grid.GridPanel({
        id : "authDomainPanel",
        // region : "center",
        height :  (Ext.getBody().getHeight()-160)*0.7, 
        width : 400,
        height:280,
        border : true,
        autoScroll : true,
        stripeRows:true,
        frame : false,
        cm : authDomainCM,
        store : authDomainStore,
        loadMask : true,
        clicksToEdit : 2, // 设置点击几次才可编辑
        selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
        viewConfig : {
            forceFit : true
        }
    });



var height = window.screen.height*0.6;
var baseDetail = new Ext.FormPanel({
        id : "base",
        labelAlign: 'left',
        frame : false,
//        autoScroll : true,
        border : false,
        width:990,
        height:420,
        bodyStyle : 'padding:20px 20px 0 20px;',
        items :[{
                        layout : "column",
                        border : false,
                        items : [{
                                columnWidth : .25,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                items : [userName,jobNumber]
                            }, {
                                columnWidth : .25,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                items : [loginName,email,position]
                            }, {
                                columnWidth : .25,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                items : [department,telephone]
                            }, {
                                columnWidth : .25,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                items : [position,timeOut]
                            }
                        ]
                    }, {
                        layout : "column",
                        bodyStyle : 'padding:3px 0px 0 0px;',
                        border : false,
                        items : [{
                            layout : "form",
                            id:'noteCon',
                            labelWidth : 60,
                            width : 970 ,  
                            border : false,
                            items : note
                        }, {
                            layout : "form",
                            id:'modifyPassCon',
                            width : 200,
                            bodyStyle : 'padding-left:55px;',
                            border : false,
                            items : [{
                                id : 'modifyPass',
                                name : 'modifyPass',  
                                text:'<span style="color:red;">修改密码</span>',
                                xtype:'button',
                                width : 145,
                                height : 20,
                                border : false,
                                handler:modifyPassword,
								// html : '<a href="javascript:;" onclick="modifyPassword();"><span
								// style="color:red;font-size:12px">修改密码</span></a>',
                                hidden : true
                            }]
                        }
                        ]
                    },{
                        layout : {
                            type : 'column',
                            columns : 2
                        },
                        border : false,
                        bodyStyle : 'padding:20px 0 0 65px',
                        items : [{
                                baseCls : 'x-plain',
                                border : true,
                                bodyStyle : 'padding:0 55px 0 0',
                                items : deviceDomainPanel
                            }, {
                                baseCls : 'x-plain',
                                border : true,
                                items : authDomainPanel
                            }
                        ]
                    }
                ]
    });


var panel = new Ext.Panel({
    region:"center",
    frame : false,
    layout:'form',
    autoScroll : true,
    border : false,
    items:baseDetail,
    buttons : [{
        id : 'ok',
        text : '确定',
        handler : function () {
            saveConfig();
        }
    }, {
        id : 'reset',
        text : '重置',
        handler : function () {
            retConfig();
        }
    }, {
        text : '取消',
        handler : function () {
            // 关闭修改任务信息窗口
            var win = parent.Ext.getCmp('createUserWindow');
            if (win) {
                win.close();
            }
        }
    }
]
});
//修改密码
function modifyPassword() {
    var url = 'modifyPass.jsp';
    var passWindow = new Ext.Window({
            id : 'passWindow',
            title : '修改密码',
            width : 420,
            height : 280,
            isTopContainer : true,
            modal : true,
            autoScroll : true,
            html : '<iframe  id="modifyUserPass_panel" name = "modifyUserPass_panel"  src = ' + url
             + ' height="100%" width="100%" frameBorder=0 border=0/>'
        });
    passWindow.show();

}

function close() {
    var win = parent.Ext.getCmp('createUserWindow');
    if (win) {
        win.close();
    }
}

//重置
function retConfig() {
    if (saveType == 0) {
        Ext.getCmp('base').form.reset();
        deviceDomainPanel.getSelectionModel().clearSelections();
        authDomainPanel.getSelectionModel().clearSelections();
    } else if (saveType == 1) {
        if (qjsonData != null && qjsonData != undefined) {
            var currentBaseDetail = qjsonData.currentBaseDetail;
            Ext.getCmp('userName').setValue(currentBaseDetail[0].USER_NAME);
            Ext.getCmp('loginName').setValue(currentBaseDetail[0].LOGIN_NAME);
            Ext.getCmp('jobNumber').setValue(currentBaseDetail[0].JOB_NUMBER);
            Ext.getCmp('telephone').setValue(currentBaseDetail[0].TELEPHONE);
            Ext.getCmp('timeout').setValue(currentBaseDetail[0].TIME_OUT);
            Ext.getCmp('email').setValue(currentBaseDetail[0].EMAIL);
            Ext.getCmp('note').setValue(currentBaseDetail[0].NOTE);
            Ext.getCmp('department').setValue(currentBaseDetail[0].DEPARTMENT);
            Ext.getCmp('position').setValue(currentBaseDetail[0].POSITION);

            deviceDomainPanel.getSelectionModel().clearSelections();
            authDomainPanel.getSelectionModel().clearSelections();

            //重置设备域
            var deviceDetail = qjsonData.currentDeviceDomain;
            var deviceRowCount = deviceDomainStore.getCount();
            for (var j = 0; j < deviceRowCount; j++) {
                for (var i = 0; i < deviceDetail.length; i++) {
                    if (deviceDetail[i].SYS_DEVICE_DOMAIN_ID == deviceDomainStore.getAt(j).get("SYS_DEVICE_DOMAIN_ID")) {
                        deviceDomainPanel.getSelectionModel().selectRow(j, true);
                    }
                }
            }
            //重置权限域
            var authDetail = qjsonData.currentAuthDomain;
            var authRowCount = authDomainStore.getCount();
            for (var j = 0; j < authRowCount; j++) {
                for (var i = 0; i < authDetail.length; i++) {
                    if (authDetail[i].SYS_AUTH_DOMAIN_ID == authDomainStore.getAt(j).get("SYS_AUTH_DOMAIN_ID")) {
                        authDomainPanel.getSelectionModel().selectRow(j, true);
                    }
                }
            }

        }
    }

}

function saveConfig() {
    if (!baseDetail.getForm().isValid()) {
        return;
    }

    var userName = Ext.getCmp("userName").getValue();
    var loginName = Ext.getCmp("loginName").getValue();
    var jobNumber = Ext.getCmp("jobNumber").getValue();
    var telephone = Ext.getCmp("telephone").getValue();
    var timeout = Ext.getCmp("timeout").getValue();
    var email = Ext.getCmp("email").getValue();
    var note = Ext.getCmp("note").getValue();
    var department = Ext.getCmp("department").getValue();
    var position = Ext.getCmp("position").getValue();
    var deviceSelectRecord = deviceDomainPanel.getSelectionModel().getSelections();
    var deviceDomain = new Array();
    for (var i = 0; i < deviceSelectRecord.length; i++) {
        deviceDomain.push(deviceSelectRecord[i].get("SYS_DEVICE_DOMAIN_ID"));
    }
    var authSelectRecord = authDomainPanel.getSelectionModel().getSelections();
    var authDomain = new Array();
    for (var i = 0; i < authSelectRecord.length; i++) {
        authDomain.push(authSelectRecord[i].get("SYS_AUTH_DOMAIN_ID"));
    }

    top.Ext.getBody().mask('正在执行，请稍候...');
    if (saveType == 0) {
        Ext.Ajax.request({
            url : 'user-management!addUser.action',
            type : 'post',
            params : { // 请求参数
                'userName' : userName,
                'loginName' : loginName,
                'jobNumber' : jobNumber,
                'telephone' : telephone,
                'timeout' : timeout,
                'department' : department,
                'email' : email,
                'position' : position,
                'note' : note,
                "authDomainList" : authDomain,
                "deviceDomainList" : deviceDomain
            },
            success : function (response) {
                top.Ext.getBody().unmask();
                var obj = Ext.decode(response.responseText);
                if (obj.success) { //新增成功
                    parent.store.reload();
                    close();
                } else {
                    Ext.Msg.alert("错误", obj.msg);
                }
            },
            error : function (response) {
                top.Ext.getBody().unmask();
                Ext.Msg.alert("错误", "添加失败,请联系管理员!");
            },
            failure : function (response) {
                top.Ext.getBody().unmask();
                Ext.Msg.alert("错误", "添加失败,请联系管理员!");
            }
        });
    }
    if (saveType == 1) {
        Ext.Ajax.request({
            url : 'user-management!modifyUser.action',
            type : 'post',
            params : { // 请求参数
                'sysUserId' : sysUserId,
                'userName' : userName,
                'loginName' : loginName,
                'jobNumber' : jobNumber,
                'telephone' : telephone,
                'timeout' : timeout,
                'department' : department,
                'email' : email,
                'position' : position,
                'note' : note,
                "authDomainList" : authDomain,
                "deviceDomainList" : deviceDomain
            },
            success : function (response) {
                top.Ext.getBody().unmask();
                var obj = Ext.decode(response.responseText);
                if (obj.success) { //新增成功
                    parent.store.reload();
                    close();
                } else {
                    Ext.Msg.alert("错误", obj.msg);
                }
            },
            error : function (response) {
                top.Ext.getBody().unmask();
                Ext.Msg.alert("错误", response.responseText);
            },
            failure : function (response) {
                top.Ext.getBody().unmask();
                Ext.Msg.alert("错误", response.responseText);
            }
        });
    }
}

var qjsonData;

function initData() {
    if (saveType == 1) {
        Ext.getCmp('modifyPass').show();
        Ext.getCmp('noteCon').setWidth(720);
    }
    var jsonData = {
        "sysUserId" : sysUserId
    }
    var authDomainList = new Array();
    var deviceDomainList = new Array();
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"加载中..."});
	   myMask.show();
    Ext.Ajax.request({
        url : 'user-management!getDetailByUserId.action',
        method : 'POST',
        //async: false,
        params : jsonData,
        success : function (response) {
            var obj = Ext.decode(response.responseText);
            var baseDetail = obj.currentBaseDetail;
            qjsonData = obj;
            Ext.getCmp('userName').setValue(baseDetail[0].USER_NAME);
            Ext.getCmp('loginName').setValue(baseDetail[0].LOGIN_NAME);
            Ext.getCmp('jobNumber').setValue(baseDetail[0].JOB_NUMBER);
            Ext.getCmp('telephone').setValue(baseDetail[0].TELEPHONE);
            Ext.getCmp('timeout').setValue(baseDetail[0].TIME_OUT);
            Ext.getCmp('email').setValue(baseDetail[0].EMAIL);
            Ext.getCmp('note').setValue(baseDetail[0].NOTE);
            Ext.getCmp('department').setValue(baseDetail[0].DEPARTMENT);
            Ext.getCmp('position').setValue(baseDetail[0].POSITION);
            if (saveType == 2) {
                Ext.getCmp('ok').hide();
                Ext.getCmp('userName').disable(true);
                Ext.getCmp('loginName').disable(true);
                Ext.getCmp('jobNumber').disable(true);
                Ext.getCmp('telephone').disable(true);
                Ext.getCmp('timeout').disable(true);
                Ext.getCmp('email').disable(true);
                Ext.getCmp('note').disable(true);
                Ext.getCmp('department').disable(true);
                Ext.getCmp('position').disable(true);
            } else {
                Ext.getCmp('userName').disable(true);
                Ext.getCmp('jobNumber').disable(true);
            }
            myMask.hide();
        },
        error : function (response) {
            Ext.Msg.alert("错误", response.responseText);
            myMask.hide();
        },
        failure : function (response) {
            Ext.Msg.alert("错误", response.responseText);
            myMask.hide();
        }
    })
}

Ext.onReady(function () {
    Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
    Ext.Msg = top.Ext.Msg;
    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget = 'title';
    if (saveType == 1 || saveType == 2) {
        initData();
    }

    var win = new Ext.Viewport({
            id : 'win',
            loadMask : true,
            layout: 'border',
            items : [panel],
            renderTo : Ext.getBody()
        });
});
