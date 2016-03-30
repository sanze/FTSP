var aboutGrid = new Ext.grid.PropertyGrid({
	id : 'aboutGrid',
    autoHeight:true,
    autoSort:false,
//    source: {
//        "员工名称": '张三',  
//        "出生日期": new Date(1978, 01, 02),  
//        "性别": '男',  
//        "是否已婚": true,  
//        "年龄": 31  
//    },
    viewConfig : {
        forceFit: true,
        scrollOffset: 2 // the grid will never have scrollbars
    }
});
aboutGrid.on('beforeedit', function(e){  
    e.cancel = true;  
    return false;  
});

function getAboutInfo(){
	Ext.Ajax.request({
		url : 'common!getAboutInfo.action',
		method : "POST",
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.getCmp("aboutGrid").setSource(obj);
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}

var aboutWindow = new Ext.Window({
	id : 'aboutWindow',
	title : '关于FTSP',
	width : 400,
	height : 82,
	isTopContainer : true,
	modal : true,
	autoScroll : false,
	closeAction: 'hide',
	items:[aboutGrid]
});


Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.parent.Ext.menu.MenuMgr.hideAll();
	};
//	var win = new Ext.Viewport({
//		id : 'win',
//		title : "许可",
//		layout : 'border',
//		items : [permissionWindow],
//		renderTo : Ext.getBody()
//	});
	aboutWindow.show();
	getAboutInfo();
});