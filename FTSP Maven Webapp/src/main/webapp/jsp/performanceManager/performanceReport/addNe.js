function addNe() {
	var treeParams = {
		leafType : 4
	};
	var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	var treePanel = new Ext.Panel({
		id : "treePanel",
		region : "center",
		boxMinWidth : 230,
		boxMinHeight : 260,
		forceFit : true,
		// split : true,
		html : '<iframe id="tree_panel" name="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	var win = new Ext.Window({
		title : "网元选择",
		layout : 'border',
		id : 'addNeWin',
		height : 400,
		width : 300,
		modal : true,
		plain : true,
		items : treePanel,
		buttons : [ {
			text : '确定', 
			handler : confirmAdd
		}, {
			text : '取消', 
			handler : function() {
				win.close();
			}
		} ]
	});
	win.show();

	function confirmAdd() {
		var iframe = window.frames["tree_panel"];
		var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel",
				"text", "emsId", "path%text" ], "top");
		var list = new Array();
		for ( var i = 0; i < selectedTargets.length; i++) {
			// alert('id：'+selectedTargets[i].nodeId+'-level：'+selectedTargets[i].nodeLevel);
			if(selectedTargets[i].nodeLevel==1){
				Ext.Msg.alert('信息','请勿选择网管分组！');
				return;
			}
			var nodes = {
				'nodeId' : selectedTargets[i].nodeId,
				'nodeLevel' : selectedTargets[i].nodeLevel
			};
			list.push(Ext.encode(nodes));
		}
		var params = {
			'modifyList' : list
		};
		Ext.Ajax.request({
			url : 'pm-report!getNodeInfo.action',
			params : params,
			method : 'POST',
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					var nodelist = result.info;
					var myReader = new Ext.data.JsonReader({
						root : 'info',
						fields : [ {
							name : 'nodeId'
						}, {
							name : 'nodeLevel'
						}, {
							name : "ems"
						}, {
							name : "emsGroup"
						}, {
							name : "subNet"
						}, {
							name : "ne"
						}, {
							name : "neType"
						}, {
							name : "emsId"
						}, {
							name : "neId"
						}, {
							name : "subNetId"
						} ]
					});
					var data = myReader.readRecords(result);
					var records = data.records;
					var indexForRemove = new Array();
					var recordsForAdd = new Array();
					var length ;
					length = records.length;
					for ( var i = 0; i < length; i++) {
						var recordIndex = storeNe.findBy(function(rec, id) {
							if (rec.get('nodeId') == records[i].get('nodeId')
									&& rec.get('nodeLevel') == records[i]
											.get('nodeLevel')) {
								return true;
							}
						});
						if (recordIndex == -1) {
							recordsForAdd.push(records[i]);
						}
					}
					storeNe.add(recordsForAdd);
					win.close();
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});

		return;

	}
}