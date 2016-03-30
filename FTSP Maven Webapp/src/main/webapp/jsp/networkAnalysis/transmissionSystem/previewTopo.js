var previewTopo = {
	preTopo : function() {
		return new Ext.Flex({
			id : "previewTopo",
			type : "tp",
			isLocalDebug : false
		});
	},
	rows : new Array(),
	generateNeData : function(displayName, nodeId) {
		var neNode = {
			"displayName" : displayName,
			"neType" : "3",
			"nodeId" : nodeId,
			"nodeOrLine" : "node",
			"nodeType" : "1",
			"parentId" : "",
			"position_X" : "",
			"position_Y" : ""
		};
		rows.push(neNode.toString());
	},

	generateLinkData : function(from, to) {
		var linkNode = {
			"fromNode" : from,
			"fromNodeType" : "3",
			"lineType" : "neLine",
			"nodeOrLine" : "line",
			"tipString" : "",
			"toNode" : to,
			"toNodeType" : "3"
		};
		rows.push(linkNode.toString());
	},
	init : function() {
		Ext.getCmp("previewTopo").on("initialize", function() {
			var dat = {
				"total" : rows.length,
				"currentTopoType" : "EMS",
				"colorWR" : "#800000",
				"colorCR" : "#ff0000",
				"isFirstTopo" : "yes",
				"colorMN" : "#ffff00",
				"parentType" : -1,
				"colorMJ" : "#ff8000",
				"rows" : rows,
				"parentId" : -1,
				"title" : "拓扑预览",
				"layout" : "round",
				"privilege" : "all",
				"colorCL" : "#00ff00"
			};
			Ext.getCmp("previewTopo").loadData(dat);

		})
	}
}