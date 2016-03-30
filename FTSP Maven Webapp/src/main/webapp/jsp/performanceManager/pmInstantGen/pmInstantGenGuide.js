
Ext.onReady(function () {
	Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
    Ext.Ajax.timeout = 900000;
    document.onmousedown = function () {
        top.Ext.menu.MenuMgr.hideAll();
    };
    var combo = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        flex:1,
        lazyRender:true,
        mode: 'local',
        value:0,
        store: new Ext.data.ArrayStore({
            id: 0,
            fields: [
                'id',
                'displayText'
            ],
            data: [[0, '按网元生成报表'], [1, '按WDM光复用段生成报表']]
        }),
        valueField: 'id',
        displayField: 'displayText'
    });

    var win = new Ext.Window({
		title: '报表设置',
		id : 'repSetting',
		layout : {
			type : 'hbox',
			padding : '0',
			align : 'middle'
		},
		modal : true,
		closable:true,
		plain:true,
		width: 300,
		height: 120,
		items : [new Ext.Spacer({
			width:40,
			height:10
		}),
		combo,
		new Ext.Spacer({
 			width:40,
			height:10
		})],
	    buttons: [{
	        text: '确定',
	        id:"btnOK",
	        handler: function(){
	        	//console.log("Click->确定");
	        	var url = ["pmInstantGenNe.jsp","pmInstantGenMultiSec.jsp"];
//	        	console.log("authSequence = " + authSequence);
	        	//console.log("Open -> " + "../performanceManager/pmInstantGen/" + url[combo.getValue()]);
	        	top.addTabPage("../performanceManager/pmInstantGen/" + url[combo.getValue()], "自动作业报表即时生成", authSequence);
	        }
	    },{
	        text: '取消',
	        handler: function(){
	        	top.closeTab("自动作业报表即时生成");
	        }
	    }],
//	    buttonAlign:"center"
	});
	win.show();
});