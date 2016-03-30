function addPtp() {
	var treeParams = {
		leafType : 8
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
		title : "端口选择",
		layout : 'border',
		id : 'addPtpWin',
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
				"text", "emsId", "path%text" ], "leaf");
		var list = new Array();
		for ( var i = 0; i < selectedTargets.length; i++) {
			// alert('id：'+selectedTargets[i].nodeId+'-level：'+selectedTargets[i].nodeLevel);
			if(selectedTargets[i].nodeLevel!=8){
				Ext.Msg.alert('信息','只能选择端口！');
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
		top.Ext.getBody().mask('请稍候...');
		Ext.Ajax.request({
			url : 'optical-unit-config!searchPtpOptModelInfo.action',
			params : params,
			method : 'POST',
			success : function(response) {
				top.Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					var myReader = new Ext.data.JsonReader({
						root : 'rows'
					},
					["emsGroup","ems", "subnet", "NeDisplayName", "ptpId","NeType",
						'portDescription','factory','maxIn','minIn']);
					var data = myReader.readRecords(result);
//					console.dir(data)
					var nodelist = data.records;
					var recordsForAdd = new Array();
					var length ;
					length = nodelist.length;
					for ( var i = 0; i < length; i++) {
						var recordIndex = store.find('ptpId',nodelist[i].data.ptpId);
						if (recordIndex == -1) {
							recordsForAdd.push(nodelist[i]);
						}
					}
					
					var data = myReader.readRecords(result);
					store.add(recordsForAdd);
					win.close();
				}
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});

		return;

	}
}