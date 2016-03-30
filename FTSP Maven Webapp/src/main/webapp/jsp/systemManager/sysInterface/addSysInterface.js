
var interfaceName = new Ext.form.TextField({
        xtype : 'textfield',
        id : 'interfaceName',
        name : 'interfaceName',
        //bodyStyle : 'padding:10px 50px 100px 10px',
        fieldLabel : '接口名称  <span style="color:red">*</span>',
        labelSeparator : ' ', //表单label与其他元素朋分符
        width : 120,
        height : 20,
        emptyText : '',
        allowBlank : false,
        maxLength : 30,
        maxLengthText : '接口名称最大长度不能超过30个字符!',
        anchor : '95%'
    });

var ownIP = new Ext.form.TextField({
        xtype : 'textfield',
        id : 'ownIP',
        name : 'ownIP',
        bodyStyle : 'padding:10px 50px 100px',
        fieldLabel : 'IP地址 <span style="color:red">*</span>',
        labelSeparator : ' ', //表单label与其他元素朋分符
        emptyText : '',
        width : 120,
        height : 20,
        allowBlank : false,
        regex : /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/,
        regexText : '请输入正确的IP地址!',
        anchor : '95%'
    });

var port = new Ext.form.TextField({
    xtype : 'textfield',
    id : 'port',
    name : 'port',
    bodyStyle : 'padding:10px 50px 100px',
    fieldLabel : '端口 <span style="color:red">*</span>',
    labelSeparator : ' ', //表单label与其他元素朋分符
    emptyText : '',
    width : 120,
    height : 20,
    allowBlank : false,
    regex : /^[\d]{1,5}$/,
    regexText : '请输入正确的端口!',
    anchor : '95%'
});

var peerIP = new Ext.form.TextField({
    xtype : 'textfield',
    id : 'peerIP',
    name : 'peerIP',
    bodyStyle : 'padding:10px 50px 100px',
    fieldLabel : '对端IP &nbsp&nbsp&nbsp<span style="color:red">*</span>',
    labelSeparator : ' ', //表单label与其他元素朋分符
    emptyText : '',
    width : 120,
    height : 20,
    allowBlank : false,
    regex : /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/,
    regexText : '请输入正确的IP地址!',
    anchor : '95%'
});

var userName = new Ext.form.TextField({
    xtype : 'textfield',
    id : 'userName',
    name : 'userName',
    //bodyStyle : 'padding:10px 50px 100px 10px',
    fieldLabel : '用户名  <span style="color:red">*</span>',
    labelSeparator : ' ', //表单label与其他元素朋分符
    width : 120,
    height : 20,
    emptyText : '',
    allowBlank : false,
    maxLength : 20,
    maxLengthText : '用户名最大长度不能超过20个字符!',
    anchor : '95%'
});
var password = new Ext.form.TextField({
    xtype : 'textfield',
    id : 'password',
    name : 'password',
    //bodyStyle : 'padding:10px 50px 100px 10px',
    fieldLabel : '密码 <span style="color:red">*</span>',
    labelSeparator : ' ', //表单label与其他元素朋分符
    width : 120,
    height : 20,
    emptyText : '',
    allowBlank : false,
    regex : /^[\@A-Za-z0-9\!\#\$\%\^\&\*\.\~]{6,20}$/,
    regexText : '请输入正确的密码格式,6-20位数字和字母组成!',
    anchor : '95%'
});

var remark = new Ext.form.TextField({
    xtype : 'textarea',
    id : 'remark',
    name : 'remark',
    fieldLabel : '备注',
    labelSeparator : ' ', //表单label与其他元素朋分符
    emptyText : '请输入备注',
    allowBlank : true,
    maxLength : 200,
    height : 40,
    anchor : '95%'
});


var height = window.screen.height*0.6;
var baseDetail = new Ext.FormPanel({
        id : "base",
        // title:'区域',
        //region : "center",
        frame : false,
        border : false,
        layout : 'form',
        frame : false,
        width : '100%',
        //height : height,
        border : false,
        autoScroll:true,
        bodyStyle : 'padding:20px 20px 20px 20px;',
        items : [{
                layout : "form",
                border : false,
                items : [{
                        layout : "column",
                        //bodyStyle : 'padding:1px 50px',
                        border : false,
                        items : [{
                                columnWidth : .33,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                bodyStyle : 'padding:0 20px 0 0',
                                items : [interfaceName, peerIP]
                            }, {
                                columnWidth : .33,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                bodyStyle : 'padding:0 20px 0 0',
                                items : [ownIP, userName]
                            }, {
                                columnWidth : .33,
                                layout : "form",
                                labelWidth : 60,
                                border : false,
                                bodyStyle : 'padding:0 20px 0 0',
                                items : [
                                    port, password
                                ]
                            }
                        ]
                    }, {
                        layout : "column",
                        columnWidth : 1,
                        //bodyStyle : 'padding:1px 50px',
                        border : false,
                        items : [{
                                layout : "form",
                                //bodyStyle : 'padding:1px 50px',
                                border : false,
                                items : [{
                                        columnWidth : .75,
                                        layout : "form",
                                        labelWidth : 60,
                                        width : 700,
                                        border : false,
                                        style : "margin-top:20px;",
                                        labelStyle : "margin-top:20px;",
                                        maxLength : 200,
                                        maxLengthText : '备注最大长度不能超过200个字符!',
                                        items : remark
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ],
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
                    //关闭修改任务信息窗口
                    var win = parent.Ext.getCmp('createInterfaceWindow');
                    if (win) {
                        win.close();
                    }
                }
            }
        ]
    });



function close() {
    var win = parent.Ext.getCmp('createInterfaceWindow');
    if (win) {
        win.close();
    }
}

//重置
function retConfig() {
    if (saveType == 0) {
        Ext.getCmp('base').form.reset();
    } else if (saveType == 1) {
        if (qjsonData != null && qjsonData != undefined) {
            var currentBaseDetail = qjsonData;
            Ext.getCmp('interfaceName').setValue(currentBaseDetail.INTERFACE_NAME);
            Ext.getCmp('ownIP').setValue(currentBaseDetail.OWN_IP);
            Ext.getCmp('port').setValue(currentBaseDetail.PORT);
            Ext.getCmp('peerIP').setValue(currentBaseDetail.PEER_IP);
            Ext.getCmp('userName').setValue(currentBaseDetail.USERNAME);
            Ext.getCmp('password').setValue(currentBaseDetail.PASSWORD);
            Ext.getCmp('remark').setValue(currentBaseDetail.REMARK);
        }
    }

}

function saveConfig() {
    if (!baseDetail.getForm().isValid()) {
        return;
    }
    var interfaceName = Ext.getCmp("interfaceName").getValue();
    var ownIP = Ext.getCmp("ownIP").getValue();
    var port = Ext.getCmp("port").getValue();
    var peerIP = Ext.getCmp("peerIP").getValue();
    var userName = Ext.getCmp("userName").getValue();
    var password = Ext.getCmp("password").getValue();
    var remark = Ext.getCmp("remark").getValue();
    top.Ext.getBody().mask('正在执行，请稍候...');
    if (saveType == 0) {
    	var paramAd = { // 请求参数
            	'jsonString':Ext.encode({
                    'INTERFACE_NAME' : interfaceName,
                    'OWN_IP' : ownIP,
                    'PORT' : port,
                    'PEER_IP' : peerIP,
                    'USERNAME' : userName,
                    'PASSWORD' : password,
                    'REMARK' : remark})
                };
    	 Ext.Ajax.request({
             url : 'interface-manage!checkInterface.action',
             type : 'post',
             params : paramAd,
             success : function (response) {
                 top.Ext.getBody().unmask();
                 var ce = Ext.decode(response.responseText);
                 if (ce.returnResult==1) { //验证成功
                	 Ext.Ajax.request({
                         url : 'interface-manage!addInterface.action',
                         type : 'post',
                         params : paramAd,
                         success : function (response) {
                             top.Ext.getBody().unmask();
                             var obj = Ext.decode(response.responseText);
                             if (obj.returnResult==1) { //新增成功
                                 parent.store.reload();
                                 close();
                             } else {
                                 Ext.Msg.alert("错误", obj.returnMessage);
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
                 } else {
                     Ext.Msg.alert("错误", ce.returnMessage);
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
    	var paramMo = { // 请求参数
            	'jsonString':Ext.encode({
                    'ID' : sysInterfaceId,
                    'INTERFACE_NAME' : interfaceName,
                    'OWN_IP' : ownIP,
                    'PORT' : port,
                    'PEER_IP' : peerIP,
                    'USERNAME' : userName,
                    'PASSWORD' : password,
                    'REMARK' : remark})
                };
    	 Ext.Ajax.request({
             url : 'interface-manage!checkInterface.action',
             type : 'post',
             params : paramMo,
             success : function (response) {
                 top.Ext.getBody().unmask();
                 var ce = Ext.decode(response.responseText);
                 if (ce.returnResult==1) { //修改成功
                     Ext.Ajax.request({
                         url : 'interface-manage!modifyInterface.action',
                         type : 'post',
                         params : paramMo,
                         success : function (response) {
                             top.Ext.getBody().unmask();
                             var obj = Ext.decode(response.responseText);
                             if (obj.returnResult==1) { //新增成功
                                 parent.store.reload();
                                 close();
                             } else {
                                 Ext.Msg.alert("错误", obj.returnMessage);
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
                 } else {
                     Ext.Msg.alert("错误", ce.returnMessage);
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
    var jsonData = {'jsonString':Ext.encode({
        "ID" : sysInterfaceId})
    }
    Ext.Ajax.request({
        url : 'interface-manage!getDetailById.action',
        method : 'POST',
        //async: false,
        params : jsonData,
        success : function (response) {
            var obj = Ext.decode(response.responseText);
            var baseDetail = obj;
            qjsonData = obj;
            Ext.getCmp('interfaceName').setValue(baseDetail.INTERFACE_NAME);
            Ext.getCmp('ownIP').setValue(baseDetail.OWN_IP);
            Ext.getCmp('port').setValue(baseDetail.PORT);
            Ext.getCmp('peerIP').setValue(baseDetail.PEER_IP);
            Ext.getCmp('userName').setValue(baseDetail.USERNAME);
            Ext.getCmp('password').setValue(baseDetail.PASSWORD);
            Ext.getCmp('remark').setValue(baseDetail.REMARK);
        },
        error : function (response) {
            Ext.Msg.alert("错误", response.responseText);
        },
        failure : function (response) {
            Ext.Msg.alert("错误", response.responseText);
        }
    })
}

Ext.onReady(function () {
    Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
    Ext.Msg = top.Ext.Msg;
    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget = 'title';
    if (saveType == 1) {
        initData();
    }

    var win = new Ext.Viewport({
            id : 'win',
            layout : 'form',
            items : baseDetail,
            renderTo : Ext.getBody()
        });
});
