var treeParams = {
    leafType : 4,
    //checkModel:"single",
    onlyLeafCheckable : true
};
var treeurl = "../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);

var treePanel = new Ext.Panel({
        title : "",
        id : "treePanel",
        region : "west",
        width : '20%',
        //autoS croll:true,
        boxMinWidth : 230,
        boxMinHeight : 260,
        forceFit : true,
        collapsed : false, // initially collapse the group
        collapsible : false,
        collapseMode : 'mini',
        split : true,
        html : '<iframe id="tree_panel" name="tree_panel" src ="' + treeurl + '" height="100%" width="100%" frameBorder=0 border=0/>'
    });
var RelatedNe = Ext.data.Record.create([{
                name : 'emsGroupName'
            }, {
                name : 'emsName'
            }, {
                name : 'text'
            }, {
                name : 'neModel'
            }, {
                name : 'id'
            }
        ]);

var dataStore = new Ext.data.Store({
        url : "area!getRelatedNE.action",
        baseParams : {
            node : ""
        },
        sortInfo : {
            field : 'id',
            direction : 'ASC' // or 'DESC' (case sensitive for local sorting)
        },
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, ["emsGroupName", "emsName", "text", "neModel", "id"])
    });
var fakeData = {"total":16,"rows":[{"id":1925,"text":"NE663","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX Metro 100"},{"id":1926,"text":"NE644","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX Metro 1000V3"},{"id":1956,"text":"三台","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":2013,"text":"三台1","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":19261,"text":"NE644","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX Metro 1000V3"},{"id":19561,"text":"三台","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":20131,"text":"三台1","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":19262,"text":"NE644","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX Metro 1000V3"},{"id":19562,"text":"三台","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":20132,"text":"三台1","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":19263,"text":"NE644","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX Metro 1000V3"},{"id":19563,"text":"三台","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":20133,"text":"三台1","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":19264,"text":"NE644","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX Metro 1000V3"},{"id":19564,"text":"三台","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"},{"id":20134,"text":"三台1","emsGroupName":"网管分组1","emsName":"xxxxxx","neModel":"OptiX DWDM OLA"}]};
dataStore.loadData(fakeData);
var selModel = new Ext.grid.SM();

var cm = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults : {
            sortable : true,
            width : 100
            // columns are not sortable by default
        },
        columns : [selModel, {
                id : 'emsGroupName',
                header : '网管分组',
                dataIndex : 'emsGroupName'
            }, {
                id : 'emsName',
                header : '网管',
                dataIndex : 'emsName'
            }, {
                id : 'text',
                header : '网元名称',
                dataIndex : 'text'
            }, {
                id : 'neModel',
                header : '网元型号',
                dataIndex : 'neModel'
            }, {
                id : 'id',
                //	                hidden:true,
                header : 'NodeID',
                dataIndex : 'id'
            }
        ]
    });

var neGrid = new Ext.grid.EditorGridPanel({
        id : "neGrid",
        autoScroll : true,
        // title:'用户管理',
        flex : 1,
        cm : cm,
        border : true,
        store : dataStore,
        stripeRows : true, // 交替行效果
        loadMask : true,
        selModel : selModel, // 必须加不然不能选checkbox
        forceFit : true,
        frame : false
    });

//==========================center=============================
var relateNeWin = new Ext.Panel({
        id : 'relateNeWin',
        border : false,
        layout : {
            type : 'hbox',
            padding : '5',
            align : 'stretch'
        },
        region : 'center',
        autoScroll : true,
        items : [new Ext.Panel({
                id : 'selBtnPanel',
                autoScroll : false,
                border : false,
                width : 30,
                flex : 1,
                layout : {
                    type : 'vbox',
                    padding : '5',
                    pack : 'center',
                    align : 'center'
                },
                defaults : {
                    margins : '0 0 5 0'
                },
                items : [{
                        xtype : 'button',
                        text : '>>',
                        handler : function () {
                            //	                    	console.log("addToRoom");
                            var neDoc = window.frames["tree_panel"];
                            var neTree = neDoc.treePanel;
                            var getNodes = neDoc.getCheckedNodes;
                            var rlt = getNodes(["emsGroupName", "emsName", "text", "neModel", "id"], "all", 4, "all");
                            // console.dir(rlt);
                            var rlts = [];
                            for (var i = 0, len = rlt.length; i < len; i++) {
                                var obj = rlt[i];
                                obj.id = obj.id.split("-")[1];
                                //创建Record
                                var rec = new RelatedNe(obj);
                                //判断是否重复
                                if (dataStore.find("id", obj.id) < 0) {
                                    rlts.push(rec);
                                }
                            }
                            //加载数据
                            dataStore.add(rlts);
                        }
                    }, {
                        xtype : 'button',
                        text : '<<',
                        handler : function () {
                            //	                    	console.log("delFromRoom");
                            var records = selModel.getSelections();
                            //	                    	console.dir(records);
                            dataStore.remove(records);
                        }
                    }
                ]
            }), neGrid]
    });

Ext.onReady(function () {
    Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
    Ext.Ajax.timeout = 900000;
    document.onmousedown = function () {
        top.Ext.menu.MenuMgr.hideAll();
    };
    // Ext.Msg = top.Ext.Msg;

    var win = new Ext.Viewport({
            id : 'win',
            layout : 'border',
            items : [treePanel, relateNeWin],
            renderTo : Ext.getBody()
        });
    win.show();
});
