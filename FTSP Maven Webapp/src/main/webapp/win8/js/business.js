var topoDisplayUrl='circuit!getRouteTopo.action';
var href = location.href;
var index = href.indexOf("win8");
var preUrl = href.substr(0, index);
Ext.Flex.APA_URL = preUrl + "jsp/viewManager/APA.swf";

Ext.onReady(function() {	
	var canvasPanel = new Ext.Panel({
		id : 'canvasPanel',
		height : 300,
		width:'80%',
		renderTo:'topo',
		
		items : [ {
			xtype : "flex",
			id : "flex",
			type : "apa"
		} ]
	});
	function FlexInitialize(){
		Ext.getCmp("flex").on("initialize", function() {
			var jsonDataTopo1 = {
					"vCircuit" : 100050
				};
				Ext.Ajax.request({
					url : topoDisplayUrl,
					type : 'post',
					params : jsonDataTopo1,
					success : function(response) {
						var obj = Ext.decode(response.responseText);
						/*alert(obj);*/
						Ext.getCmp('flex').loadData(obj);
					},
					error : function(response) {
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						Ext.Msg.alert("错误", response.responseText);
					}
				});
		});
		
	}
	FlexInitialize();
})