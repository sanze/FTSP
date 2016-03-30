Ext.apply(Ext.form.VTypes, {
    positive : function (v) {
        return /^[0-9]+$/.test(v) && parseInt(v) > 0;
    },
    positiveText : '请输入正整数！',
    number : function (v) {
        return !isNaN(v);
    },
    numberText : '请输入自然数！',
    integer : function (val, field) {
        try {
            if (/^[-+]?[\d]+$/.test(val))
                return true;
            return false;
        } catch (e) {
            return false;
        }
    },
    integerText : '请输入正确的整数',
    range1_2 : function (v) {
        return /^[0-9]+$/.test(v) && parseInt(v) > 0 && parseInt(v) < 3;
    },
    range1_2Text : '数据范围为1~2'
});
var params = {
    "paramMap.RESOURCE_UNIT_INTERFACE_ID" : unitInterfaceId
};
var Interface = new Ext.data.Record.create([{
                name : 'BASE_PTP_ID'
            }, {
                name : 'INDEX'
            }, {
                name : 'DISPLAY_NAME'
            }, {
                name : 'MAX_IN'
            }, {
                name : 'MIN_IN'
            }, {
                name : 'FACTORY'
            }, {
                name : 'PTP_DIRECTION'
            }, {
                name : 'PORT_NO'
            }
        ]);
var cmbType = new Ext.form.ComboBox({
        typeAhead : true,
        triggerAction : 'all',
        //    lazyRender:true,
        fieldLabel : "板卡类型",
        sideText : '<font color=red>*</font>',
        mode : 'local',
        width : 120,
        store : new Ext.data.ArrayStore({
            id : 0,
            fields : [
                'id',
                'displayText'
            ],
            //全部、WDM波长转换盘、WDM光开关盘
            data : [[0, '全部'], [1, 'WDM波长转换盘'], [2, 'WDM光开关盘']]
        }),
        value : 1,
        disabled : true,
        valueField : 'id',
        displayField : 'displayText'
    });
var lastUnit = -1;
var tree = new Ext.ux.EquipTreeCombo({
        xtype : "equiptreecombo",
        width : 480,
        listWidth : null,
        sideText : '<font color=red>*</font>',
        rootVisible : false,
        leafType : 6,
        allowBlank:false,
        checkableLevel : [6],
        checkModel : "single",
        fieldLabel : "板卡",
        listeners : {
            "change" : function () {
                //			console.log("equiptreecombo - change");
            },
            "blur" : function () {
                //			console.log("equiptreecombo - blur");
            },
            afterrender : function () {
                if (isMod) {
                    //				this.checkNodes("6-" + unitId);
                    params["paramMap.BASE_UNIT_ID"] = unitId;
                    params["paramMap.UNIT_INTERFACE_ID"] = unitInterfaceId;
                    lastUnit = unitId;
                    this.disable();
                }
            },
            "valid" : function (me) {
                var nodes = me.getCheckedNodes(["nodeId", "nodeLevel", "text"]);
                //			console.log(nodes);
                if (!!nodes && nodes.length > 0) {
                    if (nodes[0].nodeLevel < 5) {
                        Ext.Msg.alert("提示", "请选择板卡！");
                        return;
                    }
                    var unitId = nodes[0].nodeId;
                    params["paramMap.BASE_UNIT_ID"] = unitId;
                    unitInterfaceStore.baseParams.jsonString = unitId;
                    unitInterfaceStore.load();
                    if (lastUnit != nodes[0].nodeId) {
                        lastUnit = nodes[0].nodeId;
                        businessPanel.getStore().removeAll();
                        wdmPanel.getStore().removeAll();
                    }
                }
            }
        }
    });
var northPanel = new Ext.form.FormPanel({
        id : 'northPanel',
        title : "板卡类型",
        //    border : false,
        //	layout : 'form',
        height : 80,
        items : [tree, {
                layout : 'hbox',
                border : false,
                margins : "5",
                height : 24,
                layoutConfig : {
                    align : "stretch",
                    pack : "start"
                },
                items : [{
                        layout : "form",
                        border : false,
                        //	    	height:34,
                        width : 260,
                        items : [cmbType]
                    }, {
                        layout : "form",
                        border : false,
                        //	    	height:34,
                        width : 180,
                        items : [{
                                id : "businessPortCnt",
                                sideText : '<font color=red>*</font>',
                                xtype : "textfield",
                                value : "12",
                                width : 40,
                                vtype : "positive",
                                fieldLabel : "业务侧端口数"
                            }
                        ]
                    }, {
                        layout : "form",
                        border : false,
                        //	    	height:34,
                        width : 200,
                        items : [{
                                id : "wdmPortCnt",
                                xtype : "textfield",
                                sideText : '<font color=red>*</font>',
                                value : "1",
                                vtype : "range1_2",
                                width : 40,
                                fieldLabel : "波分侧端口数"
                            }
                        ]
                    }
                ]
            }
        ]
    });
var unitListPanel;
var unitInterfaceStore = null;
(function () {
	    var dataStore = new Ext.data.Store({
		url : 'nx-report!getPtpByUnit.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "DISPLAY_NAME", "FACTORY", "PTP_DIRECTION", "BASE_PTP_ID",
				"PORT_NO", "MAX_IN", "MIN_IN" ])
	});
    unitInterfaceStore = dataStore;
    var selModel = new Ext.grid.CheckboxSelectionModel();
    var cm = new Ext.grid.ColumnModel({
            columns : [new Ext.grid.RowNumberer(),
                selModel, {
                    id : 'DISPLAY_NAME',
                    header : '接口',
                    dataIndex : 'DISPLAY_NAME',
                    width : 200
                }
            ]
        });

    unitListPanel = new Ext.grid.GridPanel({
            id : "unitListPanel",
            autoScroll : true,
            // title:'用户管理',
            cm : cm,
            border : true,
            store : dataStore,
            stripeRows : true, // 交替行效果
            loadMask : true,
            selModel : selModel, // 必须加不然不能选checkbox
            forceFit : true,
            frame : false
        });
})();
var businessPanel;
var businessStore;
(function () {
    var dataStore = new Ext.data.Store({
            url : 'nx-report!getUsedPtp.action',
            reader : new Ext.data.JsonReader({
                totalProperty : 'total',
                root : "rows",
                fields : Interface
            })
        });
    businessStore = dataStore;
    var selModel = new Ext.grid.CheckboxSelectionModel();
    var cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true,
                width : 100
                //columns are not sortable by default
            },
            columns : [new Ext.grid.RowNumberer({
                    width : 26
                }),
                selModel, {
                    id : 'INDEX',
                    header : '接口号',
                    width : 60,
                    isCellEditable : false,
                    dataIndex : 'INDEX'
                }, {
                    id : 'DISPLAY_NAME',
                    header : '接口',
                    width : 160,
                    isCellEditable : false,
                    dataIndex : 'DISPLAY_NAME'
                }, {
                    id : 'MAX_IN',
                    header : '过载点(dBm)',
                    isCellEditable : true,
                    dataIndex : 'MAX_IN',
                    // use shorthand alias defined above
                    editor : new Ext.form.TextField({
                        allowBlank : false,
                        vtype : "number"
                    })
                }, {
                    id : 'MIN_IN',
                    header : '灵敏度(dBm)',
                    isCellEditable : true,
                    dataIndex : 'MIN_IN',
                    // use shorthand alias defined above
                    editor : new Ext.form.TextField({
                        allowBlank : false,
                        vtype : "number"
                    })
                }
            ]
        });

    businessPanel = new Ext.grid.GridPanel({
            id : "businessPanel",
            autoScroll : true,
            // title:'用户管理',
            cm : cm,
            flex : 1,
            border : true,
            store : dataStore,
            stripeRows : true, // 交替行效果
            loadMask : true,
            selModel : selModel, // 必须加不然不能选checkbox
            forceFit : true,
            tbar : [{
                    xtype : "button",
                    text : "上移",
                    icon : '../../../resource/images/btnImages/up.png',
                    handler : function () {
                        var record = null;
                        var selIndexs = [];
                        var recs = businessPanel.getSelectionModel().getSelections();
                        var newSelIndexs = [];
                        //	            console.log("count = " + recs.length);
                        if (recs.length > 0) {
                            for (var i = 0; i < recs.length; i++) {
                                record = recs[i];
                                selIndexs[i] = (record.get("INDEX") >> 0) - 1;
                                //	    	            console.log(String.format("#{0} - Index = {1}  Text = {2}",
                                //	    	            		recs.length,
                                //	    	            		record.get("INDEX"),
                                //	    	            		record.get("DISPLAY_NAME")));
                                if ((selIndexs[i] - 1) >= 0) {
                                    businessPanel.getStore().remove(record);
                                    businessPanel.getStore().insert(selIndexs[i] - 1, record);
                                    newSelIndexs.push(selIndexs[i] - 1);
                                } else if (selIndexs[i] == 0) {
                                    newSelIndexs.push(0);
                                }
                            }
                            businessPanel.view.refresh();
                            businessPanel.getSelectionModel().selectRows(newSelIndexs);
                            reIndex(businessPanel.getStore());
                        }
                    }
                }, {
                    xtype : "button",
                    text : "下移",
                    icon : '../../../resource/images/btnImages/down.png',
                    handler : function () {
                        var record = null;
                        var selIndexs = [];
                        var recs = businessPanel.getSelectionModel().getSelections();
                        var newSelIndexs = [];
                        //	            console.log("count = " + recs.length);
                        if (recs.length > 0) {
                            for (var len = recs.length, i = len - 1; i >= 0; i--) {
                                record = recs[i];
                                selIndexs[i] = (record.get("INDEX") >> 0) - 1;
                                //	    	            console.log(String.format("#{0} - Index = {1}  Text = {2}",
                                //			            		recs.length,
                                //			            		record.get("INDEX"),
                                //			            		record.get("DISPLAY_NAME")));
                                if ((selIndexs[i] + 1) < businessPanel.getStore().getCount()) {
                                    businessPanel.getStore().remove(record);
                                    businessPanel.getStore().insert(selIndexs[i] + 1, record);
                                    newSelIndexs.push(selIndexs[i] + 1);
                                } else if (selIndexs[i] + 1 == businessPanel.getStore().getCount()) {
                                    newSelIndexs.push(selIndexs[i]);
                                }
                            }
                            businessPanel.view.refresh();
                            businessPanel.getSelectionModel().selectRows(newSelIndexs);
                            reIndex(businessPanel.getStore());
                        }
                    }
                }, {
                    xtype : "button",
                    text : "关联光口标准",
                    icon : '../../../resource/images/btnImages/associate.png',
                    handler : function () {
                        var recs = businessPanel.getSelectionModel().getSelections();
                        curPanel = businessPanel;
                        if (recs.length == 0) {
                            Ext.Msg.alert("提示", "请至少选择一个接口！");
                            return;
                        }
                        showRelateOpticalStandardValue();
                    }
                }
            ],
            frame : false
        });
})();
var wdmPanel;
(function () {
    var dataStore = new Ext.data.Store({
            url : 'nx-report!getUsedPtp.action',
            reader : new Ext.data.JsonReader({
                totalProperty : 'total',
                root : "rows",
                fields : Interface
            })
        });
    var selModel = new Ext.grid.CheckboxSelectionModel();
    var cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true,
                width : 100,
                isCellEditable : true
                // columns are not sortable by default
            },
            columns : [new Ext.grid.RowNumberer({
                    width : 26
                }),
                selModel, {
                    id : 'INDEX',
                    header : '接口号',
                    width : 60,
                    isCellEditable : false,
                    dataIndex : 'INDEX'
                }, {
                    id : 'DISPLAY_NAME',
                    header : '接口',
                    width : 160,
                    isCellEditable : false,
                    dataIndex : 'DISPLAY_NAME'
                }, {
                    id : 'MAX_IN',
                    header : '过载点(dBm)',
                    isCellEditable : true,
                    dataIndex : 'MAX_IN'
                }, {
                    id : 'MIN_IN',
                    header : '灵敏度(dBm)',
                    isCellEditable : true,
                    dataIndex : 'MIN_IN'
                }
            ]
        });

    wdmPanel = new Ext.grid.GridPanel({
            id : "wdmPanel",
            autoScroll : true,
            // title:'用户管理',
            cm : cm,
            flex : 1,
            border : true,
            store : dataStore,
            stripeRows : true, // 交替行效果
            loadMask : true,
            selModel : selModel, // 必须加不然不能选checkbox
            forceFit : true,
            tbar : [{
                    xtype : "button",
                    text : "上移",
                    icon : '../../../resource/images/btnImages/up.png',
                    handler : function () {
                        var record = null;
                        var selIndexs = [];
                        var recs = wdmPanel.getSelectionModel().getSelections();
                        var newSelIndexs = [];
                        //	            console.log("count = " + recs.length);
                        if (recs.length > 0) {
                            for (var i = 0; i < recs.length; i++) {
                                record = recs[i];
                                selIndexs[i] = (record.get("INDEX") >> 0) - 1;
                                //	    	            console.log(String.format("#{0} - Index = {1}  Text = {2}",
                                //	    	            		recs.length,
                                //	    	            		record.get("INDEX"),
                                //	    	            		record.get("DISPLAY_NAME")));
                                if ((selIndexs[i] - 1) >= 0) {
                                    wdmPanel.getStore().remove(record);
                                    wdmPanel.getStore().insert(selIndexs[i] - 1, record);
                                    newSelIndexs.push(selIndexs[i] - 1);
                                } else if (selIndexs[i] == 0) {
                                    newSelIndexs.push(0);
                                }
                            }
                            wdmPanel.view.refresh();
                            wdmPanel.getSelectionModel().selectRows(newSelIndexs);
                            reIndex(wdmPanel.getStore());
                        }
                    }
                }, {
                    xtype : "button",
                    text : "下移",
                    icon : '../../../resource/images/btnImages/down.png',
                    handler : function () {
                        var record = null;
                        var selIndexs = [];
                        var recs = wdmPanel.getSelectionModel().getSelections();
                        var newSelIndexs = [];
                        //	            console.log("count = " + recs.length);
                        if (recs.length > 0) {
                            for (var len = recs.length, i = len - 1; i >= 0; i--) {
                                record = recs[i];
                                selIndexs[i] = (record.get("INDEX") >> 0) - 1;
                                //	    	            console.log(String.format("#{0} - Index = {1}  Text = {2}",
                                //					            		recs.length,
                                //					            		record.get("INDEX"),
                                //					            		record.get("DISPLAY_NAME")));
                                if ((selIndexs[i] + 1) < wdmPanel.getStore().getCount()) {
                                    wdmPanel.getStore().remove(record);
                                    wdmPanel.getStore().insert(selIndexs[i] + 1, record);
                                    newSelIndexs.push(selIndexs[i] + 1);
                                } else if (selIndexs[i] + 1 == businessPanel.getStore().getCount()) {
                                    newSelIndexs.push(selIndexs[i]);
                                }
                            }
                            wdmPanel.view.refresh();
                            wdmPanel.getSelectionModel().selectRows(newSelIndexs);
                            reIndex(wdmPanel.getStore());
                        }
                    }
                }, {
                    xtype : "button",
                    text : "关联光口标准",
                    icon : '../../../resource/images/btnImages/associate.png',
                    handler : function () {
                        var recs = wdmPanel.getSelectionModel().getSelections();
                        curPanel = wdmPanel;
                        if (recs.length == 0) {
                            Ext.Msg.alert("提示", "请至少选择一个接口！");
                            return;
                        }
                        showRelateOpticalStandardValue();
                    }
                }
            ],
            frame : false
        });
})();
var mainPanel = new Ext.Panel({
        id : 'mainPanel',
        title : "端口设置",
        layout : "fit",
        flex : 1,
        items : [{
                xtype : "panel",
                border : false,
                layout : 'hbox',
                layoutConfig : {
                    align : "stretch",
                    pack : "start"
                },
                items : [{
                        xtype : 'fieldset',
                        //    		border:false,
                        title : '可选接口', // title, header, or checkboxToggle creates fieldset
                        // header
                        //	        autoHeight:true,
                        width : 180,
                        collapsed : false, // fieldset initially collapsed
                        layout : 'fit',
                        items : [unitListPanel]
                    }, {
                        layout : "vbox",
                        border : false,
                        flex : 1,
                        layoutConfig : {
                            align : "stretch",
                            pack : "start"
                        },
                        items : [{
                                xtype : 'fieldset',
                                title : '业务侧', // title, header, or checkboxToggle creates
                                // fieldset header
                                //		        autoHeight:true,
                                //        		border:false,
                                flex : 1,
                                collapsed : false, // fieldset initially collapsed
                                layout : 'fit',
                                items : [{
                                        xtype : "panel",
                                        layout : 'hbox',
                                        border : false,
                                        layoutConfig : {
                                            align : "stretch",
                                            pack : "start"
                                        },
                                        items : [{
                                                xtype : "panel",
                                                width : 24,
                                                layout : "vbox",
                                                layoutConfig : {
                                                    align : "center",
                                                    pack : "center"
                                                },
                                                items : [{
                                                        xtype : "button",
                                                        text : "＞",
                                                        handler : function () {
                                                            var recs = unitListPanel.getSelectionModel().getSelections();
             /*                                               var total = businessPanel.getStore().getCount();
                                                            if ((total + recs.length) > parseInt(Ext.getCmp("businessPortCnt").getValue())) {
                                                                Ext.Msg.alert("提示", "添加的接口数大于波分侧最大接口数！");
                                                                return;
                                                            }*/
                                                            for (var i = 0; i < recs.length; i++) {
                                                                var rec = recs[i];
                                                                var ptpId = rec.get("BASE_PTP_ID");
                                                                var name = rec.get("DISPLAY_NAME");
                                                                addRecord(businessPanel.getStore(), rec, wdmPanel.getStore(), "波分侧");
                                                            }
                                                        }
                                                    }, {
                                                        xtype : "button",
                                                        text : "＜",
                                                        handler : function () {
                                                            var recs = businessPanel.getSelectionModel().getSelections();
                                                            businessPanel.getStore().remove(recs);
                                                            reIndex(businessPanel.getStore());
                                                        }
                                                    }
                                                ]
                                            }, businessPanel]
                                    }
                                ]
                            }, {
                                xtype : 'fieldset',
                                title : '波分侧',
                                //			    autoHeight:true,
                                flex : 1,
                                //        		border:false,
                                collapsed : false, // fieldset initially collapsed
                                layout : 'fit',
                                items : [{
                                        xtype : "panel",
                                        layout : 'hbox',
                                        border : false,
                                        layoutConfig : {
                                            align : "stretch",
                                            pack : "start"
                                        },
                                        items : [{
                                                xtype : "panel",
                                                width : 24,
                                                layout : "vbox",
                                                layoutConfig : {
                                                    align : "center",
                                                    pack : "center"
                                                },
                                                items : [{
                                                        xtype : "button",
                                                        text : "＞",
                                                        handler : function () {
                                                            var recs = unitListPanel.getSelectionModel().getSelections();
                                                            /*var total = wdmPanel.getStore().getCount();
                                                            if ((recs.length + total) > parseInt(Ext.getCmp('wdmPortCnt').getValue())) {
                                                                Ext.Msg.alert("提示", "添加的接口数大于波分侧最大接口数！");
                                                                return;
                                                            }*/
                                                            for (var i = 0; i < recs.length; i++) {
                                                                var rec = recs[i];
                                                                var ptpId = rec.get("BASE_PTP_ID");
                                                                var name = rec.get("DISPLAY_NAME");
                                                                addRecord(wdmPanel.getStore(), rec, businessPanel.getStore(), "业务侧");
                                                            }
                                                        }
                                                    }, {
                                                        xtype : "button",
                                                        text : "＜",
                                                        handler : function () {
                                                            var recs = wdmPanel.getSelectionModel().getSelections();
                                                            wdmPanel.getStore().remove(recs);
                                                            reIndex(wdmPanel.getStore());
                                                        }
                                                    }
                                                ]
                                            }, wdmPanel]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
    });
function reIndex(store) {
    var recs = store.getRange();
    for (var i = 0; i < recs.length; i++) {
        recs[i].set("INDEX", i + 1);
    }
    store.commitChanges();
}
var curPanel = null;
function relateOpticalStandardValue(rec) {
    var optStdId = rec.get("optStdId");
    var maxIn = rec.get("maxIn");
    var minIn = rec.get("minIn");
    var recs = curPanel.getSelectionModel().getSelections();
    var modIds = [];
    for (var i = 0; i < recs.length; i++) {
        modIds.push(recs[i].get("BASE_PTP_ID"));
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
                    recs[i].set("MAX_IN", maxIn);
                    recs[i].set("MIN_IN", minIn);
                    var ptpId = recs[i].get("BASE_PTP_ID");
                    var idx = unitListPanel.getStore().find("BASE_PTP_ID", ptpId);
                    //					console.log("ptp @ " + idx)
                    if (idx > -1) {
                        var r = unitListPanel.getStore().getAt(idx);
                        r.set("MAX_IN", maxIn);
                        r.set("MIN_IN", minIn);
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
function showRelateOpticalStandardValue() {
    var url = 'standardOpticalValueManage.jsp?authSequence=' + authSequence;
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
function addRecord(store, rec, storeCheck, storeName) {
    var total = store.getCount();
    var newPtpId = rec.get("BASE_PTP_ID");
    var name = rec.get("DISPLAY_NAME");
    var max_in = rec.get("MAX_IN") || "";
    var min_in = rec.get("MIN_IN") || "";
    if (store.find("BASE_PTP_ID", newPtpId) < 0) {
        if (storeCheck.find("BASE_PTP_ID", newPtpId) < 0) {
            var nbi = new Interface({
                    BASE_PTP_ID : newPtpId,
                    INDEX : total + 1,
                    DISPLAY_NAME : name,
                    MAX_IN : max_in,
                    MIN_IN : min_in,
                    FACTORY : rec.get("FACTORY"),
                    PTP_DIRECTION : rec.get("PTP_DIRECTION"),
                    PORT_NO : rec.get("PORT_NO")
                });
            store.add(nbi);
        } else {
            Ext.Msg.alert("提示", "部分接口已在<" + storeName + ">添加,这部分接口将被忽略！");
        }
    }
}


Ext.onReady(function () {
    Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
    document.onmousedown = function () {
        top.Ext.menu.MenuMgr.hideAll();
    };
    Ext.Msg = top.Ext.Msg;
    Ext.Ajax.timeout = 900000;
    var win = new Ext.Viewport({
            id : 'win',
            layout : 'fit',
            items : [{
                    id : 'winMain',
                    layout : 'vbox',
                    layoutConfig : {
                        align : "stretch",
                        pack : "start"
                    },
                    items : [northPanel, mainPanel],
                    buttons : [{
                            text : "确定",
                            handler : function () {
                                if (!validate()) {
                                    return;
                                }
                                saveOrUpdate(true);
                            }
                        }, {
                            text : "取消",
                            handler : function () {
                                parent.Ext.getCmp('addUnitInterfaceWindow').close();
                            }
                        }, {
                            text : "应用",
                            hidden : !isMod,
                            handler : function () {
                                if (!validate()) {
                                    return;
                                }
                                saveOrUpdate(false);
                            }
                        }, {
                            text : "重置",
                            hidden : !isMod,
                            handler : function () {
                                reset();
                            }
                        }
                    ],
                    buttonAlign : "right"
                }
            ]
        });
    win.show();
    if (isMod) {
        reset();
    }
    //	initEvents();
});
function validate() {
	//检测板卡是否选择
	if(!tree.getValue()){
        Ext.Msg.alert("提示", "请先选择板卡！");
        return false;
	}
    if (!northPanel.getForm().isValid()) {
        Ext.Msg.alert("提示", "有必填项没有正确填写！");
        return false;
    }
    // TODO
    if (countByDirection(Ext.getCmp('businessPanel').getStore()) > Ext.getCmp('businessPortCnt').getValue()) {
        Ext.Msg.alert("提示", "业务侧端口数不能大于设定值！");
        return false;
    }
    if (countByDirection(Ext.getCmp('wdmPanel').getStore()) > Ext.getCmp('wdmPortCnt').getValue()) {
        Ext.Msg.alert("提示", "波分侧端口数不能大于设定值！");
        return false;
    }
    var rv = true;
    Ext.getCmp('businessPanel').getStore().each(function (v) {
        rv = (v.get("MAX_IN") - v.get("MIN_IN")) >= 0;
        return rv;
    });
    Ext.getCmp('wdmPanel').getStore().each(function (v) {
        rv = (v.get("MAX_IN") - v.get("MIN_IN")) >= 0;
        return rv;
    });
    if (!rv) {
        Ext.Msg.alert("提示", "过载点应不小于灵敏度！");
        return false;
    }
    return true;
}
function countByDirection(store){
	if(!store.getCount()>0)
		return 0;
	if(store.getAt(0).get("FACTORY")!=FACTORY.ZTE){
		return store.getCount();
	}
	var count=0;
	store.each(function (v) {
        var dir = v.get("PTP_DIRECTION");
        if(dir==3){ // 收
        	var port_no_1 = v.get("PORT_NO").match(/\d+_(\d+)\(*.*\)*/)[1];
        	store.each(function (r){
        		var port_no_2 = r.get("PORT_NO").match(/\d+_(\d+)\(*.*\)*/)[1];
        		if(port_no_1==port_no_2&&r.get("PTP_DIRECTION")==2){
        			count++;
        		}
        	});
        }
        return true;
    });
	return store.getCount()-count;
}
function saveOrUpdate(prompt) {
    var businessPtps = businessPanel.getStore().getRange();
    var wdmPtps = wdmPanel.getStore().getRange();
    var businessPtpList = [];
    var wdmPtpList = [];
    params["paramMap.DISPLAY_NAME"] = tree.getValue();
    params["paramMap.UNIT_TYPE"] = cmbType.getValue();
    params["paramMap.BUSINESS_PORT_NUM"] = Ext.getCmp("businessPortCnt").getValue();
    params["paramMap.WAVE_PORT_NUM"] = Ext.getCmp("wdmPortCnt").getValue();
    if(unitInterfaceId!=null){
        params["paramMap.unitInterfaceId"] = unitInterfaceId;

    }
    //填充ID
    for (var i = 0; i < businessPtps.length; i++) {
        businessPtpList.push(businessPtps[i].get("BASE_PTP_ID"));
    }
    for (var i = 0; i < wdmPtps.length; i++) {
        wdmPtpList.push(wdmPtps[i].get("BASE_PTP_ID"));
    }
    params["paramMap.BUSINESS_LIST"] = businessPtpList.join(",");
    params["paramMap.WDM_LIST"] = wdmPtpList.join(",");
    //					console.log(params);
    Ext.getBody().mask("保存中...");
    Ext.Ajax.request({
        url : 'nx-report!saveUnitInterface.action',
        params : params,
        method : 'POST',
        success : function (response) {
            Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            if (result.returnResult == 0) {
                Ext.Msg.alert("提示", result.returnMessage);
            }
            if (result.returnResult > 0) {
                params["paramMap.RESOURCE_UNIT_INTERFACE_ID"] = result.returnResult;
                parent.pageTool.doLoad(parent.pageTool.cursor);
                if (!!prompt) {
                    Ext.Msg.confirm("提示", "数据保存成功，是否继续？", function (btn) {
                        if (btn != 'yes')
                            parent.Ext.getCmp('addUnitInterfaceWindow').close();
                    });
                } else {
                    Ext.Msg.alert("提示", "数据保存成功!");
                }
            }
        },
        failure : function (response) {
            top.Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        },
        error : function (response) {
            top.Ext.getBody().unmask();
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        }
    });

}
function reset() {
    //开始加载初始数据
    Ext.Ajax.request({
        url : 'nx-report!getUnitInterface.action',
        params : {
            "paramMap.unitInterfaceId" : unitInterfaceId
        },
        method : 'POST',
        success : function (response) {
            var result = Ext.util.JSON.decode(response.responseText);
            //				console.log(result);
            tree.setValue(result.DISPLAY_NAME);
            cmbType.setValue(result.UNIT_TYPE);
            cmbType.disable();
            Ext.getCmp("businessPortCnt").setValue(result.BUSINESS_PORT_NUM);
            Ext.getCmp("wdmPortCnt").setValue(result.WAVE_PORT_NUM);
        },
        failure : function (response) {
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        },
        error : function (response) {
            var result = Ext.util.JSON.decode(response.responseText);
            Ext.Msg.alert("提示", result.returnMessage);
        }
    });
    //开始加载可选板卡数据
    unitInterfaceStore.baseParams.jsonString = unitId;
    unitInterfaceStore.load();
    //
    //		businessPanel.getStore().url = "nx-report!getUsedPtpInfo.action";
    businessPanel.getStore().baseParams = {
        "paramMap.unitInterfaceId" : unitInterfaceId,
        "paramMap.ptpType" : 1
    };
    businessPanel.getStore().load({
        callback : function (r, options, success) {
            reIndex(businessPanel.getStore());

        }
    });
   
    //		wdmPanel.getStore().url = "nx-report!getUsedPtpInfo.action";
    wdmPanel.getStore().baseParams = {
        "paramMap.unitInterfaceId" : unitInterfaceId,
        "paramMap.ptpType" : 2
    };
    wdmPanel.getStore().load({
        callback : function (r, options, success) {
            reIndex(wdmPanel.getStore());
        }
    });
}
