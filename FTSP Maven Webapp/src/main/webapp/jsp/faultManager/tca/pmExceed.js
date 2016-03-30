var westPanel;
(function () {
    var treeParams = {
        rootId : 0,
        rootType : 0,
        rootText : "FTSP",
        rootVisible : false,
        containerId : "westPanel",
		checkModel : "multiple",
        leafType : 4
    };
    var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
    function treeFilter(tree, parent, node) {
        if (node.attributes["nodeLevel"] == NodeDefine.NE
             && node.attributes["additionalInfo"]
             && node.attributes["additionalInfo"]["TYPE"] == NodeDefine.TYPE) {
            return false;
        }
    }
    westPanel = new Ext.Panel({
            id : "westPanel",
            region : "west",
            width : 280,
            //		border:false,
            autoScroll : true,
            boxMinWidth : 230,
            boxMinHeight : 260,
            forceFit : true,
            collapsed : false, // initially collapse the group
            collapsible : false,
            collapseMode : 'mini',
            split : true,
            //		filterBy : treeFilter,
            html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
             + '" height="100%" width="100%" frameBorder=0 border=0 />'
        });
})();
var centerPanel;
var dataStore;
(function () {
    // ************************* SDH设置 ****************************
    var TPLevel = {
        xtype : 'fieldset',
        labelWidth : 10,
        anchor : '95%',
        title : 'TP等级',
        height : 240,
        items : [{
                id : 'TPLevel',
                xtype : 'checkboxgroup',
                columns : 1,
                items : [{
                        checked : true,
                        boxLabel : 'STM1',
                        inputValue : 'STM-1'
                    }, {
                        checked : true,
                        boxLabel : 'STM4',
                        inputValue : 'STM-4'
                    }, {
                        checked : true,
                        boxLabel : 'STM16',
                        inputValue : 'STM-16'
                    }, {
                        checked : true,
                        boxLabel : 'STM64',
                        inputValue : 'STM-64'
                    }, {
                        checked : true,
                        boxLabel : 'STM256',
                        inputValue : 'STM-256'
                    }, {
                        checked : true,
                        boxLabel : '光传送和复用单元',
                        inputValue : 'OTU'
                    }, {
                        checked : true,
                        boxLabel : '光通道',
                        inputValue : 'OCH'
                    }, {
                        checked : true,
                        boxLabel : '光监控通道',
                        inputValue : 'OSC'
                    }, {
                        checked : true,
                        boxLabel : '其他',
                        id : 'SDHTPLevelOther',
                        inputValue : 'other'
                    }
                ]
            }
        ]
    };
    var physicFieldset = {
        xtype : 'fieldset',
        anchor : '95%',
        height : 210,
        labelWidth : 10,
        title : '物理量',
        items : [{
                id : 'physicFieldset',
                xtype : 'checkboxgroup',
                columns : 1,
                items : [{
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '发光功率',
                        inputValue : 1
                    }, {
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '收光功率',
                        inputValue : 2
                    }, {
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '信道中心波长/偏移',
                        inputValue : 10
                    }, {
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '信道信噪比',
                        inputValue : 11
                    }, {
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '信道光功率',
                        inputValue : 9
                    }, {
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '激光器电流',
                        inputValue : 4
                    }, {
                        xtype : 'checkbox',
                        id : 'phyOther',
                        checked : false,
                        boxLabel : '其他',
                        inputValue : 16
                    }
                ]
            }
        ]
    };
    var numbericFieldset = {
        xtype : 'fieldset',
        labelWidth : 10,
        anchor : '95%',
        height : 240,
        title : '计数值',
        items : [{
                id : 'numbericFieldset',
                xtype : 'checkboxgroup',
                columns : 1,
                items : [{
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '再生段误码(B1)',
                        inputValue : 5
                    }, {
                        xtype : 'checkbox',
                        checked : true,
                        boxLabel : '复用段误码(B2)',
                        inputValue : 6
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        boxLabel : 'VC4通道误码(B3)',
                        inputValue : 7
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        boxLabel : 'VC3/VC12通道误码(B3/V5)',
                        inputValue : 8
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        boxLabel : '光监控通道误码',
                        inputValue : 12
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        boxLabel : 'FEC误码率',
                        inputValue : 13
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        boxLabel : 'OTU误码率',
                        inputValue : 14
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        boxLabel : 'ODU误码率',
                        inputValue : 15
                    }, {
                        xtype : 'checkbox',
                        checked : false,
                        id : 'sdhNumOther',
                        boxLabel : '其他',
                        inputValue : 17
                    }
                ]
            }
        ]
    };
    var boxArea = {
        id : 'boxArea',
        layout : 'column',
        anchor : '100%',
        border : false,
        items : [{
                columnWidth : 0.3,
                border : false,
                layout : 'form',
                items : [TPLevel]
            }, {
                columnWidth : 0.3,
                border : false,
                layout : 'form',
                labelWidth : 20,
                items : [physicFieldset, {
                        xtype : 'checkbox',
                        boxLabel : '最大、最小值、平均值',
                        id : 'maxMinFlag',
                        inputValue : 1
                    }
                ]
            }, {
                columnWidth : 0.3,
                border : false,
                layout : 'form',
                items : [numbericFieldset]
            }
        ]
    };

    var today = new Date();
    var todayStr = today.format("yyyy-MM-dd 23:59:59");
    today.setDate(today.getDate() - 7);
    var lastWeekStr = today.format("yyyy-MM-dd 00:00:00");
    centerPanel = new Ext.Panel({
            id : 'centerPanel',
            title : "查询条件",
            region : 'center',
            //border : false,
            layout : 'anchor',
            autoScroll : true,
            tbar : [{
                    xtype : 'button',
                    icon : '../../../resource/images/btnImages/search.png',
                    text : '查询',
                    handler : function () {
                        searchTCA();
                    }
                }
            ],
            items : [{
                    xtype : "compositefield",
                    height : 25,
                    border : false,
                    anchor : '100%',
                    items : [{
                            xtype : 'panel',
                            labelWidth : 95,
                            layout : "form",
                            border : false,
                            items : [{
                                    xtype : 'radiogroup',
                                    fieldLabel : "&nbsp;&nbsp;越限事件类别",
                                    width : 200,
                                    id : 'eventType',
                                    name : 'state',
                                    items : [{
                                        	name : 'clearStatus',
                                            inputValue : 1,
                                            boxLabel : '未结束',
                                            checked : true
                                        }, {
                                            name : 'clearStatus',
                                            inputValue : 0,
                                            boxLabel : '已结束'
                                        }
                                    ]
                                }
                            ]
                        }, {
                            xtype : 'panel',
                            labelWidth : 75,
                            layout : "form",
                            border : false,
                            items : [{
                                    xtype : 'checkboxgroup',
                                    width : 200,
                                    fieldLabel : "&nbsp;&nbsp;监测周期",
                                    id : 'period',
                                    name : 'period',
                                    items : [{
                                            name : 'Granuality',
                                            inputValue : 1,
                                            boxLabel : '15 min',
                                            checked : true
                                        }, {
                                            name : 'Granuality',
                                            inputValue : 2,
                                            boxLabel : '24h',
                                            checked : true
                                        }
                                    ]
                                }
                            ]

                        }
                    ]
                },{
                    xtype : "spacer",
                    height : 10,
                    border : false,
                    anchor : '100%'
                }, {
                    xtype : "panel",
                    height : 30,
                    border : false,
                    anchor : '100%',
                    items : [{
                            xtype : 'compositefield',
                            labelWidth : 120,
                            items : [{
                                    xtype : 'label',
                                    text : "",
                                    width : 5,
                                    align : "middle"
                                }, {
                                    xtype : 'label',
                                    text : "发生时间： 从",
                                    align : "middle",
                                    width : 90
                                }, {
                                    xtype : 'textfield',
                                    id : 'startTime',
                                    name : 'startTime',
                                    fieldLabel : '开始时间',
                                    allowBlank : false,
                                    width : 150,
                                    value : lastWeekStr,
                                    sideText : '<font color=red>*</font>',
                                    cls : 'Wdate',
                                    listeners : {
                                        'focus' : function () {
                                            WdatePicker({
                                                el : "startTime",
                                                isShowClear : false,
                                                readOnly : true,
                                                dateFmt : 'yyyy-MM-dd HH:mm:ss',
                                                maxDate : Ext.getCmp('endTime').getValue(),
                                                autoPickDate : true
                                            });
                                            this.blur();
                                        }
                                    }
                                }, {
                                    xtype : 'label',
                                    html : "<font color='red'>*</font>",
                                    width : 12
                                }, {
                                    xtype : 'button',
                                    text : "清空",
                                    handler : function () {
                                        Ext.getCmp("startTime").setValue("");
                                    }
                                }, {
                                    xtype : 'label',
                                    html : "到",
                                    width : 12
                                }, {
                                    xtype : 'textfield',
                                    id : 'endTime',
                                    name : 'endTime',
                                    fieldLabel : '开始时间',
                                    allowBlank : false,
                                    width : 150,
                                    value : todayStr,
                                    cls : 'Wdate',
                                    listeners : {
                                        'focus' : function () {
                                            WdatePicker({
                                                el : "endTime",
                                                isShowClear : false,
                                                readOnly : true,
                                                dateFmt : 'yyyy-MM-dd HH:mm:ss',
                                                minDate:Ext.getCmp('startTime').getValue(),
                                                autoPickDate : true,
                                                maxDate : '%y-%M-%d'
                                            });
                                            this.blur();
                                        }
                                    }
                                }, {
                                    xtype : 'label',
                                    html : "<font color='red'>*</font>",
                                    width : 12,
                                }, {
                                    xtype : 'button',
                                    text : "清空",
                                    handler : function () {
                                        Ext.getCmp("endTime").setValue("");
                                    }
                                }
                            ]

                        }
                    ]
                }, boxArea]

        });
})();
function getSelection() {
    // 选择tree中选中的节点
    var iframe = window.frames["tree_panel"];
    var sels = iframe.getCheckedNodes(["nodeId", "nodeLevel", "text"], "top",
            [1], "all");
    if(sels.length>0){
    	Ext.Msg.alert("提示", "请勿选择网管分组节点！");
    	return {length:0};
    }
    sels = iframe.getCheckedNodes(["nodeId", "nodeLevel", "text"], "top",
            [2, 3, 4], "all");
    if(sels.length == 0){
    	Ext.Msg.alert("提示", "请先选择要查询的节点！");
    	return {length:0};
    }
    var rv = {
    	"1" : [],
        "2" : [],
        "3" : [],
        "4" : [],
        count : 0,
        names : []
    };
    for (var i = 0; i < sels.length; i++) {
        rv[("_" + sels[i].nodeLevel).substr(1, 1)].push(sels[i].nodeId);
//        console.log(sels[i]);
        rv.names.push(sels[i].text);
    }
    rv.length = sels.length;
    return rv;
}
function getParam() {
    var param = {};
    var tmpIds = [];
    var tmpNames = [];
    param.eventType = Ext.getCmp("eventType").getValue().inputValue;
    param.period = [];
    Ext.getCmp('period').items.each(function (item) {
        if (item.getValue()) {
            param.period.push(item.inputValue);
            tmpNames.push(item.boxLabel);
        }
    });
    if(param.period.length == 0){
    	Ext.Msg.alert("提示", "请选择监测周期！");
    	return null;
    }
    param.periodString = "(" + param.period.join(",") + ")";
    param.periodNames = tmpNames.join("、");
    tmpNames = [];
    param.startTime = Ext.getCmp('startTime').getValue();
    param.endTime = Ext.getCmp('endTime').getValue();
    Ext.getCmp('TPLevel').items.each(function (item) {
        if (item.getValue()) {
            tmpIds.push(item.inputValue);
            tmpNames.push(item.boxLabel);
        }
    });
    if(tmpIds.length == 0){
    	Ext.Msg.alert("提示", "请选择TP等级！");
    	return null;
    }
    param.tpIds = tmpIds;
    param.tpNames = tmpNames.join("、");
    tmpIds = [];
    tmpNames = [];
    Ext.getCmp('physicFieldset').items.each(function (item) {
        if (item.getValue()) {
            tmpIds.push(item.inputValue);
            tmpNames.push(item.boxLabel);
        }
    });
    param.physicNames = tmpNames.length>0 ? tmpNames.join("、") : "无";
    tmpNames = [];
    Ext.getCmp('numbericFieldset').items.each(function (item) {
        if (item.getValue()) {
            tmpIds.push(item.inputValue);
            tmpNames.push(item.boxLabel);
        }
    });
    if(tmpIds.length == 0){
    	Ext.Msg.alert("提示", "物理量、计数值至少选择一个！");
    	return null;
    }
    param.pmStdIndexs = tmpIds;
    param.numbericNames = tmpNames.length>0 ? tmpNames.join("、") : "无";
    param.maxMinFlag = Ext.getCmp("maxMinFlag").getValue();
    var targets = getSelection();
    if (targets.length == 0) {
        return null;
    }
//    console.log("asdasdasd");
    var tmp = {};
    if (targets["1"].length > 0)
        tmp.emsGroup = "(" + targets["1"].join(",") + ")";
    if (targets["2"].length > 0)
        tmp.ems = "(" + targets["2"].join(",") + ")";
    if (targets["3"].length > 0)
        tmp.subnet = "(" + targets["3"].join(",") + ")";
    if (targets["4"].length > 0)
        tmp.ne = "(" + targets["4"].join(",") + ")";
    param.targets = tmp;
    param.emsGroup = tmp.emsGroup;
    param.ems = tmp.ems;
    param.subnet = tmp.subnet;
    param.ne = tmp.ne;
    param.targetNames = targets.names.join("、");
    return param;
}
/**
 * 搜索性能越限数据
 */
function searchTCA() {
    var param = getParam();
    if(!!param)
    	top.pmExceed.addTab(param);
}
Ext.onReady(function () {
    Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
    Ext.Ajax.timeout = 900000;
    document.onmousedown = function () {
        top.Ext.menu.MenuMgr.hideAll();
    };
    var win = new Ext.Viewport({
            id : 'win',
            layout : 'border',
            items : [westPanel, centerPanel],
            renderTo : Ext.getBody()
        });
    win.show();
});
top.pmExceed = {
    cnt : 0,
    args : [],
    addTab : function (params) {
        if (top.pmExceed.cnt >= 5) {
            Ext.Msg.alert("提示", "只能同时打开5个查询页面!");
        } else {
            top.pmExceed.cnt++;
            var found = false;
            for (var i = 0; i < 5 && !found; i++) {
                var arg = top.pmExceed.args[i];
                if (!arg) {
                    top.pmExceed.args[i] = arg = {
                        available : true,
                        param : null
                    };
                }
                //console.log(String.format("arg[{0}].available = {1}",i,arg.available));
                if (arg.available) {
                	found = true;
                    params.id = i;
                    //						top.pmExceed.args[i] = arg = {available:true,param:null};
                    arg.available = false;
                    arg.param = params;
//                    console.log(arg);
                    top.addTabPage("../faultManager/tca/pmExceedResult.jsp?tabId=" + i, "性能越限事件查询结果" + (i + 1),
                        authSequence, false);
//                    console.log(String.format("arg[{0}].available = {1}",i,arg.available));
                    break;
                }
            }
        }
    }
};
top.centerPanel.on("beforeremove",function(tp,tab){
	if(tab.id.indexOf("性能越限事件查询结果") == 0){
		var idx = tab.id.substr(tab.id.length-1, tab.id.length);
//		console.log("idx = " + idx);
		idx = (idx>>0) - 1;
//		console.log("idx == " + idx);
		top.pmExceed.args[idx].available = true;
		top.pmExceed.cnt--;
//		console.log(String.format("性能越限事件查询结果[{0}]关闭，剩余{1}",idx,top.pmExceed.cnt));
	}
});
