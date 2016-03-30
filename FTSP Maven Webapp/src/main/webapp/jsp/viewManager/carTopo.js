
Ext.onReady(function(){
	var view = new Ext.Viewport({ 
		layout:'fit',
		items:[{
			region:'center',
			id:'car_topo',
			isLocalDebug:false,
			xtype:'flex',
			type : 'car_topo'
		}]
	});
	view.show();
	Ext.getCmp("car_topo").on("initialize", function () {

	    Ext.Ajax.request({
	        url: "car-topo!getNode.action",
	        method : 'POST',
	        params:{"nodeId":-1,
					"nodeType":-1,
					"direction":"forward",
					"privilege":authSequence
					},
	        scope:this,
	        success:function(resp) {
	            dat = Ext.decode(resp.responseText);
	            if (dat&&dat.returnResult==0){
	            	Ext.Msg.alert("提示", dat.returnMessage);
	            }else{
	            	Ext.getCmp("car_topo").loadData(dat);
	            }
	        }
	    });
	});
});

//查看网管属性
function openEMSAttributes(emsId){
	parent.addTabPage("../southConnectionManager/emsConnectionProperty.jsp?emsConnectionId="+emsId, 
			"网管属性(" + emsId + ")", authSequence);
}