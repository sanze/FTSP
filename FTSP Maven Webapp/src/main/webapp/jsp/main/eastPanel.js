	Ext.Msg = top.Ext.Msg;
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
    
    var tools = [{
        id:'close',
        handler: function(e, target, panel){
            panel.ownerCt.remove(panel, true);
        }
    }];
    
	Ext.onReady(function(){
        var win = new Ext.Viewport({
        	id:"viewport",
        	loadMask : true,
            layout: 'border',
            items: [{
	            xtype:'portal',
	            region:'center',
	            items:[{
	                columnWidth:1,
	                style:'padding:2px 2px 0 2px',
	                items:[{
	                    title: '我的任务',
	                    tools: tools,
	                    html:'今天我没有任务，哈哈哈'
	                },{
	                    title: '我的信息',
	                    tools: tools,
	                    html:'今天我没有信息，哈哈哈'
	                },{
	                    title: '我的XXX',
	                    tools: tools,
	                    html:'今天我没有XXX，哈哈哈'
	                }]
	            }]
	        }],
	        renderTo : Ext.getBody()
        });
    });
