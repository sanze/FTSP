function topoInit(topo,nodeList,linkList){
	var rows = new Array();
	for(var i=0;i<nodeList.length;i++){
		generateNeData(nodeList[i]);
	}
	for(var i=0;i<linkList.length;i++){
		generateLinkData(linkList[i]);
	}
	topo.on("initialize", function() {
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
			"title" : "网络拓扑",
			"layout" : "round",
			"privilege" : "all",
			"colorCL" : "#00ff00"
		};
		topo.loadData(dat);

	});
	function generateNeData(displayName, nodeId) {
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
		rows.push(neNode);
	}
	;

	function generateLinkData(from, to) {
		var linkNode = {
			"fromNode" : from,
			"fromNodeType" : "3",
			"lineType" : "neLine",
			"nodeOrLine" : "line",
			"tipString" : "",
			"toNode" : to,
			"toNodeType" : "3"
		};
		rows.push(linkNode);
	}
}