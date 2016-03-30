
/**
 * 删除记录
 * 1 删除
 * 2 清空
 */
function deleteTarget(type){
//	if(!!window.taskId){
	if(typeof taskId != 'undefined'){
		deleteLater(type);
	}else{
		deleteNow(type);
	}
}
var ID_LIST_FOR_DELETE = new Array();
function deleteLater(type){
	if(type == 2){
		if (store.getCount() > 0) {
			Ext.Msg.confirm("提示", "是否清空所有对象数据？", function(btn) {
				if (btn == "yes") {
						store.each(function(r){
							ID_LIST_FOR_DELETE.push(r.get('RESOURCE_UNIT_MANAGE_ID'));
						});
						store.removeAll();
						store.commitChanges();
				}
			});
		} else {
			Ext.Msg.alert("提示", "请先选取对象！");
		}
	}else{
		var items = grid.getSelectionModel().getSelections();
		if (items.length > 0) {
			Ext.Msg.confirm("提示", "是否删除选中的对象？", function(btn) {
				if (btn == "yes") {
						for ( var i = 0; i < items.length; i++) {
							ID_LIST_FOR_DELETE.push(items[i].get('RESOURCE_UNIT_MANAGE_ID'));
						}
						for ( var i = 0; i < items.length; i++) {
							store.remove(items[i]);
						}
						store.commitChanges();
				}
			});
		} else {
			Ext.Msg.alert("提示", "请先选取对象！");
		}
	}
}

/**
 * 删除记录
 * 1 删除
 * 2 清空
 */
function deleteNow(type){
	if(type == 2){
		if (store.getCount() > 0) {
			Ext.Msg.confirm("提示", "是否清空所有对象数据？", function(btn) {
				if (btn == "yes") {
						var idList = new Array();
						store.each(function(r){
							idList.push(r.get('RESOURCE_UNIT_MANAGE_ID'));
						});
						grid.getEl().mask('请稍后...');
						Ext.Ajax.request({
							url : 'nx-report!deleteUnitManageByManageIdList.action',
							method : 'POST',
							params : {intList:idList},
							success : function(response) {
								grid.getEl().unmask();
							},
							failure : function(response) {
								grid.getEl().unmask();
								var result = Ext.util.JSON.decode(response.responseText);
								Ext.Msg.alert("提示", result.returnMessage);
							}
						});
						store.removeAll();
						store.commitChanges();
				}
			});
		} else {
			Ext.Msg.alert("提示", "请先选取对象！");
		}
	}else{
		var items = grid.getSelectionModel().getSelections();
		if (items.length > 0) {
			Ext.Msg.confirm("提示", "是否删除选中的对象？", function(btn) {
				if (btn == "yes") {
						var idList = new Array();
						for ( var i = 0; i < items.length; i++) {
							idList.push(items[i].get('RESOURCE_UNIT_MANAGE_ID'));
						}
						grid.getEl().mask('请稍后...');
						Ext.Ajax.request({
							url : 'nx-report!deleteUnitManageByManageIdList.action',
							method : 'POST',
							params : {intList:idList},
							success : function(response) {
								grid.getEl().unmask();
							},
							failure : function(response) {
								grid.getEl().unmask();
								var result = Ext.util.JSON.decode(response.responseText);
								Ext.Msg.alert("提示", result.returnMessage);
							}
						});
						for ( var i = 0; i < items.length; i++) {
							store.remove(items[i]);
						}
						store.commitChanges();
				}
			});
		} else {
			Ext.Msg.alert("提示", "请先选取对象！");
		}
	}
}