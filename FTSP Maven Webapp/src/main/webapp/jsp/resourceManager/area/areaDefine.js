//全局参数：区域树节点
var nodes;  
//是否含下属区域
var ifSubArea={
	id : 'ifSubArea',
    xtype : 'checkbox', 
    listeners:{
    	'afterrender':function(checkbox){
    		Ext.QuickTips.register({
    			target : Ext.getCmp('ifSubArea').getEl(),  
    			text : '含下属区域'
    		}); 
    	}
    }
};
//区域树     
function getTree(field,level){  
	var win = new Ext.Window({ 
		layout : 'fit',
		modal : true,
		height : 400,
		width : 260,
		pageX :field.getPosition()[0],
		pageY :field.getPosition()[1]+20,
		items : [{
			id:'area',
			xtype : "area",
			maxLevel:level
		}],
		buttons: [{ 
			text: '确定',
			handler: function(){
				nodes = Ext.getCmp("area").getSelectedNodes();
				if(nodes.total == 0){ 
					Ext.Msg.alert('提示', '请选择！');
				}else{ 
					var id = nodes.nodes[0].id;
					field.setValue(nodes.nodes[0].text);
					field.areaId = id;
					win.close();
				}
			}
		},{
			text: '取消',
			handler: function(){ win.close(); }
		}]
	});
	win.show();
	field.blur();
}   
/**
 * 查询功能,只有点击查询按钮时store才会加载
 * 字符串showAll代表是否包含下属区域
 **/
function showAll(store,name){   
	if(!!nodes && (nodes.total> 0)){   
	    store.baseParams.node = nodes.nodes[0].id+'-'+nodes.nodes[0].level; 
	}else{
		store.baseParams.node = "0-0";
	}
	if(Ext.getCmp('ifSubArea').checked){
		store.baseParams.node += "-showAll";
	}else{
		store.baseParams.node += "-0";
	} 
	store.baseParams.name=name ;
	store.baseParams.limit = 200; 
	store.load();  
} 

/**
 * 修改资源的入口
 * 内部根据选择节点的不同跳转到不同的修改函数
 */
function modResource(id,lvl,auth) {
	Ext.Ajax.request({
		url : "area!getAreaInfo.action",
		method : "POST",
		params:{
			node : id+"-"+lvl+"-"
		},
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (!!obj) { 
				if(lvl == 11){
					modStation(auth,obj);
				}else{
					modRoom(auth,obj);
				}
			} else {
				Ext.Msg.alert('提示', '获取资源信息失败！');
			}
		}
	}); 
}

//局站或机房关联至网元
function relate(isRoom,record){ 
	var attrId=isRoom?"roomId":"stationId"; 
	var attrName=isRoom?"roomName":"stationName"; 
	var treePanel = new Ext.ux.EquipTreePanel({   
		rootVisible: false,
		region:"west",
		width: 250,
		split: true, 
		collapseMode: 'mini', 
	    boxMinWidth: 250,
	    boxMinHeight: 260, 
	    checkableLevel: [CommonDefine.TREE.NODE.NE],
	    leafType:[CommonDefine.TREE.NODE.NE],
	    filterBy: stationFilter
	});
	//关联过的网元被过滤
    function stationFilter(tree, parent, node) { 
        if (!!node.attributes["additionalInfo"]["RESOURCE_STATION_ID"]) {
            return false;
        }
    }    
	// Record对象，用于实时向右侧Grid的Store添加记录
	var RelatedNe = Ext.data.Record.create([
	                                        {name: 'emsGroupName'},
	                                        {name: 'emsName'},
	                                        {name: 'text'},
	                                        {name: 'neModel'},
	                                        {name: 'id'}
	                                        ]);
	//右侧Grid的Store
	var dataStore = new Ext.data.Store({
		url:"area!getRelatedNE.action",
		baseParams:{
			node:isRoom?(record.get('stationId')+'-'+record.get(attrId)):(record.get(attrId)+'-0') 
		},
		sortInfo: {
		    field: 'id',
		    direction: 'ASC' // or 'DESC' (case sensitive for local sorting)
		},
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, ["emsGroupName", "emsName", "text", "neModel", "id"])
    });
	dataStore.load();
	var selModel = new Ext.grid.CheckboxSelectionModel();

	var cm = new Ext.grid.ColumnModel({
	        // specify any defaults for each column
	        defaults : {
	            sortable : true,
	            width : 100
	            // columns are not sortable by default
	        },
	        columns : [selModel, {
	                id : 'emsGroupName',
	                header : '网管分组',
	                dataIndex : 'emsGroupName'
	            }, {
	                id : 'emsName',
	                header : '网管',
	                dataIndex : 'emsName'
	            }, {
	                id : 'text',
	                header : '网元名称',
	                dataIndex : 'text'
	            }, {
	                id : 'neModel',
	                header : '网元型号',
	                dataIndex : 'neModel'
	            }, {
	                id : 'id',
	                hidden:true,
	                header : 'NodeID',
	                dataIndex : 'id'
	            }
	        ]
	    });

	var neGrid = new Ext.grid.EditorGridPanel({
	        id : "neGrid",
	        autoScroll : true,
	        // title:'用户管理',
	        flex:1,
	        cm : cm,
	        border : true,
	        store : dataStore,
	        stripeRows : true, // 交替行效果
	        loadMask : true,
	        selModel : selModel, // 必须加不然不能选checkbox
	        forceFit : true,
	        frame : false
	    });

		//==========================center=============================
		// 关联窗口右侧Panel的定义
		// 包含 2个按钮（添加删除），以及一个Grid
		var relateNeWin = new Ext.Panel({
		    id:'relateNeWin', 
		    border:true,
		    layout: {
                type:'hbox',
                padding:'5',
                align:'stretch'
            },
		    region:'center',
		    autoScroll:true,
		    items:[new Ext.Panel({
				    id:'selBtnPanel',
				    autoScroll:false,
				    border:false,
                    width:30,
                    flex:1,
				    layout: {
                        type:'vbox',
                        padding:'5',
                        pack:'center',
                        align:'center'
                    },
	                defaults:{margins:'0 0 5 0'},
	                items:[{ 
	                    xtype:'button',
	                    text: '＞',
	                    //从树添加到网元列表的函数
	                    handler:function(){   
	    					//获取勾选的网元
	    					var rlt = treePanel.getCheckedNodes(["emsGroupName", "emsName", "text", "neModel", "id"], 
	    							"leaf", CommonDefine.TREE.NODE.NE, "all"); 
    						var rlts=[]; 
    						for(var i=0,len = rlt.length;i<len;i++){
    							var obj = rlt[i]; 
    							try {
									obj.id = parseInt(obj.id.split("-")[1]);
	    							// 创建Record
	    							var rec = new RelatedNe(obj);
	    							// 判断是否重复，重复则不添加 
	    							if(dataStore.findExact("id", obj.id) < 0){ 
    									rlts.push(rec); 
	    							}
								} catch (e) {
								}
    						}
    						//加载数据
    						dataStore.add(rlts);
	                    }
	                },{
	                    xtype:'button',
	                    text: '＜',
	                    handler:function(){
	                    	var records = selModel.getSelections();
	                    	dataStore.remove(records);
	                    }
	                }]
			    }),neGrid]
        });
		/**
		 * 关联网元的弹出窗口
		 */
		var relateWin = new Ext.Window({
			//设置标题
			title : '关联网元(' + record.get(attrName) + ")",
			id : 'relateWin',
			layout : 'border',
			modal : true,
			closable : true,
			plain : true,
			closeAction : 'close',
			width : 800,
			height : 480,
			items : [treePanel, relateNeWin],
			buttons : [{
				scope : this,
				text : '重置',
				handler : function() {
					dataStore.reload();
				}
			},  {
				scope : this,
				text : '确定',
				handler : function() {  
					var ids = [];
					var records = dataStore.getRange();
					for(var i=0,len = records.length;i<len;i++){
						var rec = records[i];
						ids.push(rec.get("id"));
					} 
					Ext.Ajax.request({
						url : "area!updateRelatedNE.action",
						method : "POST",
						params:{
							node:isRoom?(record.get('stationId')+'-'+record.get(attrId)):(record.get(attrId)+'-0'),
							ids:ids.join(",") 
						},
						success : function(response) {
							try {
								var obj = Ext.decode(response.responseText);
								if (obj.returnResult == 1) { 
									//显示提示信息
									//Ext.Msg.alert('提示', obj.returnMessage);
									//关闭窗口
									relateWin.close();
								} else {
									//显示提示信息
									Ext.Msg.alert('提示', obj.returnMessage);
								}
							} catch (e) { 
							}
						}
					});

				}
			}, {
				text : '取消',
				handler : function() {
					relateWin.close();
				}
			}],
			buttonAlign : "right"
		});
		relateWin.show(this); 
}

//导出
var exportParams = {};
var store = null;
function exportInfo(store,exportParams){ 
	if(store.getCount() == 0){
		Ext.Msg.alert("信息","信息列表为空！");
		return;
	}
	Ext.getBody().mask('正在导出到Excel，请稍候...');
   	Ext.Ajax.request({
	    url: 'area!exportInfo.action',
	    type: 'post',
	    params: exportParams,
	    success: function(response) {
            Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText); 
	    	if(obj.returnResult == 1){
		    	window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
            }
        	if(obj.returnResult == 0){
        		Ext.Msg.alert("信息","导出数据失败！");
        	}
	    },
	    error:function(response) {
	    	Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 
}