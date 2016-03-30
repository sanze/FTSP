var store = new Ext.data.Store({
	url : 'resource-cable!getCables.action', 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "DISPLAY_NAME", "CABLES_NO", "RESOURCE_CABLES_ID", "NOTE","cableLength"])
});

var selModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect:true
});

var cm = new Ext.grid.ColumnModel({ 
    defaults: {
        sortable: true  ,
    	width:140
    },
    columns: [new Ext.grid.RowNumberer({
    	width:26
    	}),selModel, {
		id : 'cablesName_',
		header : '光缆名称',
		dataIndex : 'DISPLAY_NAME' 
	}, {
		id : 'cablesNo_',
		header : '光缆代号',
		dataIndex : 'CABLES_NO'
	},{
		id : 'cableLength_',
		header : '长度(KM)',
		dataIndex : 'cableLength' 
	}, {
		id : 'note_',
		header : '备注',
		dataIndex : 'NOTE' 
	}] 
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
}); 

var cablesName = new Ext.form.TextField({
	id:'CablesName',
	width:100
});

var cablesNo = new Ext.form.TextField({
	id:'CablesNo',  
	width:100
});


var gridPanel = new Ext.grid.GridPanel({
	id:"gridPanel",
	region:"center",
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    loadMask: {msg: '数据加载中...'},
    selModel:selModel ,  
	bbar: pageTool,
	tbar : [ '-','光缆名称：',cablesName,'光缆代号：',cablesNo,'-',{
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchCables
	},{
		text : '新增',
		icon : '../../../resource/images/btnImages/add.png',
		privilege : addAuth,
		handler : function(){
            editCables(addAuth);
        }
	}, {
		text : '删除',
		icon : '../../../resource/images/btnImages/delete.png',
		privilege : delAuth,
		handler : delCables
	}, {
		text : '修改',
		icon : '../../../resource/images/btnImages/modify.png',
		privilege : modAuth,
		handler : function(){
			modResource(modAuth);
        }
	}, '-',  
	{
		text : '查询光缆段',
		icon : '../../../resource/images/btnImages/search.png',
		privilege : viewAuth,
		handler : searchCableSection
	}]
});

//查询光缆
function searchCables() {  
	store.baseParams = { 
		"name" : Ext.getCmp('CablesName').getValue(),
		"no" : Ext.getCmp('CablesNo').getValue(),
		'limit':200
	};
	store.load({
		callback : function(r, o, s) {
			if (s) { 
			} else {
				Ext.Msg.alert("提示", "查询光缆失败！");
			}
		}
	});
}

/**
 * 添加修改 光缆
 */
function editCables(auth,prop) { 
	var isAdd=(auth==addAuth);
	prop=prop?prop:{};    
	
	var cablesPanel = new Ext.FormPanel({
	    id : "cablesPanel",
	    frame : false,
	    bodyStyle : 'padding:20px 30px 30px 30px',
	    labelAlign : 'left',
	    autoScroll : true,
	    items : [{ 
                xtype : 'textfield',
                id : 'name',
                name : 'name',
                fieldLabel : '光缆名称', 
                sideText : '<font color=red>*</font>',
                allowBlank:false,
                value:prop.DISPLAY_NAME,
                anchor : '90%' 
            },{ 
                xtype : 'textfield',
                id : 'no',
                name : 'no',
                fieldLabel : '光缆代号',  
                value:prop.CABLES_NO,
                anchor : '90%' 
            },{ 
                xtype : 'textfield',
                id : 'note',
                name : 'note',
                fieldLabel : '备注',  
                value:prop.NOTE,
                anchor : '90%' 
            }]
		});
	
	var win = new Ext.Window({
		title : isAdd?"新增光缆":"修改光缆",
		id : 'cablesWin', 
		modal : true,
		closable : true,
		plain : true,
		closeAction : 'close',
	    width : 360,
        height : 200,  
		items : [ cablesPanel ],
		buttons : [ {
			scope : this,
			text : '确定',
			handler : function() {
				if(cablesPanel.form.isDirty() && cablesPanel.form.isValid()){
					cablesPanel.form.doAction("submit", {
						url : isAdd?"resource-cable!addCables.action":"resource-cable!modCables.action",
						method:"POST",
						params:{
							cellId:	isAdd?0:prop.RESOURCE_CABLES_ID	 
						},
						waitTitle : "请稍候",
						waitMsg : "正在提交表单数据，请稍候",
						success : function(form, action) { 
							Ext.Msg.alert("提示",action.result.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}); 
							win.close();
						},
						failure : function(form, action) {
							Ext.Msg.alert('提示', action.result.returnMessage);
						}
					});
				}else{
					win.close();
				} 
			}
		}, {
			text : '取消',
			handler : function() {
				win.close();
			}
		}]
	});
	win.show(this);
} 

function modResource(modAuth){ 
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length > 0) { 
		Ext.Ajax.request({
			url: 'resource-cable!getCablesInfo.action', 
			method : 'POST',
			params:{cellId :cell[0].get("RESOURCE_CABLES_ID")},
			success: function(response) {
				var obj = Ext.decode(response.responseText);
				if (!!obj) { 
					editCables(modAuth,obj);
				} else {
					Ext.Msg.alert('提示', '获取光缆信息失败！');
				}
			},
			error:function(response) {
		        Ext.Msg.alert("异常",response.responseText);
			},
			failure:function(response) {
		        Ext.Msg.alert("异常",response.responseText);
			}
		}); 
	}else{
		  Ext.Msg.alert("提示",'请选择一条数据！');
	}
}

 

function delCables() {
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else { 
		Ext.Msg.confirm("提示", "确认删除:"+gridPanel.getSelectionModel().getSelected().get("DISPLAY_NAME"), function(btn) {
			if (btn == "yes") {
				// 确认删除
				Ext.Ajax.request({
					url : 'resource-cable!delCables.action',
					params : {
						cellId : gridPanel.getSelectionModel().getSelected().get("RESOURCE_CABLES_ID")
					},
					success : function(response) { 
						var obj = Ext.decode(response.responseText);
						if (obj.returnResult == 1) {
							Ext.Msg.alert("提示",obj.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}); 
						}
						if (obj.returnResult == 0) { 
							Ext.Msg.alert("提示", obj.returnMessage);
						}
					},
					error:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    }
				});
			}
		});
	}	
}

//查询光缆段
function searchCableSection() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 1) { 
        var url = "../resourceManager/cable/cableSection.jsp?cablesId=" 
                    + cell[0].get('RESOURCE_CABLES_ID')+"&cableName="+cell[0].get("DISPLAY_NAME")
                    +"&cableNo="+cell[0].get("CABLES_NO");
        
        parent.addTabPage(url, "光缆段管理", authSequence);
    }else{
        Ext.Msg.alert('提示', '请先选择一条记录！');
    } 
}
    
Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
	Ext.Ajax.timeout=900000; 
    var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [gridPanel],
        renderTo : Ext.getBody()
    }); 
    searchCables() ;
});