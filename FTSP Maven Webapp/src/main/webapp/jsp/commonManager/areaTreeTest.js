
var tree = {
    region : 'west',
    collapsible : true,
    title : '区域选择',
    xtype : 'area',
    maxLevel:6,
    width : 200,
    autoScroll : true,
    split : true,
    singleSelection:false,
    id:"vt"
};

Ext.onReady(function () {
    var swfPanel = new Ext.Panel({
            width : 800,
            height : 600,
            title : '面板图演示',
            renderTo : 'container',
            layout : "border",
            items : [tree, {
                    region : 'center',
                    xtype : 'tabpanel'
                }
            ],
            tbar : [{
                    text : '测试LoadData',
                    //icon : '../../../icons/fam/information.png',
                    handler : function () {
                        console.log("Test Button1 Click");
                        console.log(Ext.getCmp("vt").getSelectedNodes());
                    }
                }, {
                    text : '测试2',
                    //icon : '../../../icons/fam/information.png',
                    handler : function () {
                        console.log("Test Button2 Click");
                    }
                }, {
                    xtype : 'areaselector',
                    //指定选择器的宽度
                	width:150,
                	//指定弹出窗口的宽与高
                	winWidth:320,
                	winHeight:320,
                	//要选择的目标层级
                	targetLevel:4
                }
            ]
        });
});
