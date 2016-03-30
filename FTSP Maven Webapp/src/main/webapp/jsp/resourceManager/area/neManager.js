//网元详细信息的Store
var store = new Ext.data.Store({
	url : 'area!getNeGrid.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "neId","areaName", "stationName", "roomName", "emsGroup", "emsName", "neName", "neModel" ])
});

//局站详细信息grid列
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});

var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true ,
		width:120
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel, {
		id : 'neName',
		header : '网元名称',
		dataIndex : 'neName'
	} , {
		id : 'neModel',
		header : '网元型号',
		dataIndex : 'neModel'
	} , {
		id : 'emsName',
		header : '网管名称',
		dataIndex : 'emsName',
		width:140
	}, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup'
	},{
		id : 'areaName',
		header : '所属'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName'
	}, {
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME+'名称',
		dataIndex : 'stationName'
	}, {
		id : 'roomName',
		header : '所属机房',
		dataIndex : 'roomName'
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

//网元关联选择
var comboRel = new Ext.form.ComboBox({ 
	id:'ifRelate',
    triggerAction: 'all', 
    mode: 'local',  
	editable:false,
    store: new Ext.data.ArrayStore({ 
        fields: [
            'value',
            'displayName'
        ],
        data: [[ 1, '已关联' ], [ 2, '未关联' ], [ 3, '全部' ]]
    }),
    valueField: 'value',
    displayField: 'displayName',
    value: 3,
    width : 100,
    listeners:{
    	'select':function(combo){
 			Ext.getCmp('areaField').disable();
			Ext.getCmp('ifSubArea').disable();
    		if(combo.isDirty ()){ 
	    		if(combo.getValue()==1){
	    			Ext.getCmp('areaField').enable();
	    			Ext.getCmp('ifSubArea').enable();
	    		}
    		}
    		combo.blur();
    	} 
    }
}); 

//区域树
var areaTree = new Ext.form.TextField({ 
	id : 'areaField',  
	disabled:true,
	width: 110,  
	emptyText:'选择区域/局站/机房',
	listeners : {
		'focus' : function(field){  
			getTree(this,12); 
			field.blur();
		}
	}
});    
 

//网元名称
var neName={
	xtype: 'textfield',
	id:'neName',   
	emptyText:'模糊搜索',
	width: 110
}; 

var emsText = { 
    xtype: 'equiptreecombo', 
    allowBlank: true,
    id: 'emsText', 
    checkModel: 'single',
	leafType : [CommonDefine.TREE.NODE.EMS],
    checkableLevel: [CommonDefine.TREE.NODE.EMS],
    listeners:{
    	'collapse':  function (combo,value){ 
    		combo.hiddenValue=combo.treeField.getCheckedNodes('nodeId');
    	}
    }
}; 

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false, 
	loadMask:true, 
	stripeRows : true, // 交替行效果
	selModel : checkboxSelectionModel,   
	bbar : pageTool,
	tbar : ['位置：',comboRel,'-',areaTree,'-',ifSubArea,
		'-','网管：',emsText,'网元名：',neName,'-',
		  {
			xtype : 'button',
			text : '重置', 
			icon : '../../../resource/images/btnImages/refresh.png',
			handler :function(){ 
				comboRel.setValue(3);
				areaTree.setDisabled(true);
				areaTree.setValue(null);
				nodes=null;
				Ext.getCmp('ifSubArea').reset();
				Ext.getCmp('emsText').setValue(null);
				Ext.getCmp('emsText').hiddenValue=null;
				Ext.getCmp('neName').setValue(null);
			}
		  },{
		xtype : 'button',
		text : '查询', 
		icon : '../../../resource/images/btnImages/search.png',
		handler :function(){ 
			store.baseParams.ifRelated = Ext.getCmp('ifRelate').getValue();
			store.baseParams.ids=Ext.getCmp('emsText').hiddenValue==null?"0":Ext.getCmp('emsText').hiddenValue;
			showAll(store,Ext.getCmp('neName').getValue());
		}
	},"-", {
		xtype : 'button', 
		text : '关联', 
		privilege:delAuth, 
        icon:'../../../resource/images/btnImages/associate.png',
		handler : neRelateTo
	}, {
		xtype : 'button',  
		text :'取消关联', 
		privilege:viewAuth,
    	icon:'../../../resource/images/btnImages/disassociate.png',
		handler:cancelRelateTo
	},'-', {
		xtype : 'button', 
		text :'导出',
		privilege:viewAuth, 
		icon : '../../../resource/images/btnImages/export.png',
		handler:function (){
			exportParams = store.baseParams;
		    exportParams.exportType = 13;
			exportParams.limit=0;
			exportInfo(store,exportParams);
		}
	}]
});

function neRelateTo(){
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else {  
		var win = new Ext.Window({ 
			title : '选择局站/机房',
			region : 'center',
			height:0.7*Ext.getCmp('win').getHeight(),
			width : 250,
			layout : 'fit',
			plain : false,
			modal : true,
			constrain:true,
			resizable : true, 
			items : [{
				id:'areaSel',
				xtype : "area",
				maxLevel:12,
				singleSelection:true
			}],
			buttons: [{ 
				text: '确定',
				handler: function(){
					nodes = Ext.getCmp("areaSel").getSelectedNodes();
					if(nodes.total == 0 || nodes.nodes[0].level<11 ){ 
						Ext.Msg.alert('提示', '请选择'+top.FieldNameDefine.STATION_NAME+'/机房！');
					}else{ 
						var targetStr='';
						if(nodes.nodes[0].level==11) targetStr+=top.FieldNameDefine.STATION_NAME+nodes.nodes[0].text;
						if(nodes.nodes[0].level==12) targetStr+="机房："+nodes.nodes[0].text;
						Ext.Msg.confirm('提示','确定关联到'+targetStr,function(r){
							if(r == 'yes'){
								//存储网元id
								var ids = [];
								var records = gridPanel.getSelectionModel().getSelections();
								for(var i=0,len = records.length;i<len;i++){
									var rec = records[i];
									ids.push(rec.get("neId"));
								}   	 
								Ext.Ajax.request({ 
								    url: 'area!neRelateTo.action',
								    method : 'POST',
								    params: {
								    	node:nodes.nodes[0].node.id,
								    	ids:ids.join(",") 
								    },
								    success: function(response) {
								    	var obj = Ext.decode(response.responseText); 
								    	if(obj.returnResult == 1){ 
								    		Ext.Msg.alert("提示",'关联到'+targetStr+'成功！', function(r) {
												var pageTool = Ext.getCmp('pageTool');
												if (pageTool) {
													pageTool.doLoad(pageTool.cursor);
												}
											}); 
								    	}
								    	if(obj.returnResult == 0){
							        		Ext.Msg.alert("错误",obj.returnMessage);
							        	}
								    	win.close();
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
			},{
				text: '取消',
				handler: function(){ win.close(); }
			}]
		});
		win.show(); 
	}
}

function cancelRelateTo(){
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else {  		 
		Ext.Msg.confirm('提示','确定取消网元关联',function(r){
			if(r == 'yes'){
				//存储网元id
				var ids = [];
				var records = gridPanel.getSelectionModel().getSelections();
				for(var i=0,len = records.length;i<len;i++){
					var rec = records[i];
					ids.push(rec.get("neId"));
				}   	 
				Ext.Ajax.request({ 
				    url: 'area!cancelRelateTo.action',
				    method : 'POST',
				    params: {
				    	ids:ids.join(",") 
				    },
				    success: function(response) {
				    	var obj = Ext.decode(response.responseText); 
				    	if(obj.returnResult == 1){ 
							Ext.Msg.alert("提示",obj.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							}); 
				    	}
				    	if(obj.returnResult == 0){
			        		Ext.Msg.alert("错误",obj.returnMessage);
			        	}
				    	win.close();
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

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif"; 
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}; 
	Ext.QuickTips.init(); 
	Ext.Ajax.timeout = 90000000; 
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel],
		renderTo : Ext.getBody()
	}); 
	Ext.getCmp('ifSubArea').disable (); 
	store.baseParams.ifRelated = Ext.getCmp('ifRelate').getValue();
	store.baseParams.ids=Ext.getCmp('emsText').hiddenValue==null?"0":Ext.getCmp('emsText').hiddenValue;
	showAll(store,Ext.getCmp('neName').getValue()); 
});
