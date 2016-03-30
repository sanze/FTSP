var permissionGrid = new Ext.grid.PropertyGrid({
	id : 'permissionGrid',
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
permissionGrid.on('beforeedit', function(e){  
    e.cancel = true;  
    return false;  
});

function getPermissionInfo(){
	Ext.Ajax.request({
		url : 'common!getPermissionInfo.action',
		method : "POST",
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			Ext.getCmp("permissionGrid").setSource(obj);
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

var permissionWindow = new Ext.Window({
	id : 'permissionWindow',
	title : '许可',
	width : 400,
	height : 176,
	isTopContainer : true,
	modal : true,
	autoScroll : false,
	closeAction: 'hide',
	items:[permissionGrid]
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
	permissionWindow.show();
	getPermissionInfo();
});