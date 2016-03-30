
function preview(params) {
	top.Ext.getBody().mask("请稍等...");
	Ext.Ajax
			.request({
				url : 'nx-report!getReportPreview.action',
				method : 'POST',
				params : params,
				success : function(response) {
					top.Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					var url = result.returnMessage;
					url = "../../../../" + url;
					var previewWindow = new top.Ext.Window(
							{
								id : 'previewWindow',
								title : '报表预览',
								width : Ext.getBody().getWidth() * 0.8,
								height : Ext.getBody().getHeight() * 0.9,
								isTopContainer : true,
								modal : true,
								autoScroll : true,
								html : "<iframe  id='报表预览' name = '报表预览'  src = '"
										+ url
										+ "' height='100%' width='100%' frameBorder=0 border=0/>"
							});
					previewWindow.show();
				},
				failure : function(response) {
					top.Ext.getBody().unmask();
//					var result = Ext.util.JSON.decode(response.responseText);
					Ext.Msg.alert("提示", response.responseText);
				},
				error : function(response) {
					top.Ext.getBody().unmask();
//					var result = Ext.util.JSON.decode(response.responseText);
					Ext.Msg.alert("提示", response.responseText);
				}
			});
}