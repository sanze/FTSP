var addAuth = "1000000000";
var delAuth = "0100000000";
var modAuth = "0010000000";
var viewAuth = "0001000000";
var actionAuth = "0000100000";

var authSequence;
function initPage() {
	var url = location.search;
	//获取权限参数
	if (url.indexOf("?") != -1) {
		var str = url.substr(1);
		strs = str.split("&");
		for ( var i = 0; i < strs.length; i++) {
			if (strs[i].split("=")[0] == "authSequence") {
				authSequence = strs[i].split("=")[1];
				break;
			}
		}
	}
	//管理员用户
	if(authSequence == "all"){
		return;
	}
	//循环页面组件
	var itms = Ext.ComponentMgr.all.items;
	for ( var p in itms) {
		if (itms[p].getXType) {
			if (itms[p].getXType()== "button"
				||itms[p].getXType()== "splitbutton"
				||itms[p].getXType()== "menuitem") {
				if(itms[p].privilege){
					var privilege = itms[p].privilege;
				//按位与操作
					var result = parseInt(privilege,2)&parseInt(authSequence,2);
					//无权限设置为不可用
					if(result == 0){
						itms[p].disable();
					}
				}
			}
		}
	}
};
Ext.onReady(function() {
	initPage.defer(50);
});