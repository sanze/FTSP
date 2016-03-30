var limit = 200;
function debug(type, title, msg) {
    console[type](title + " - " + msg);
}
//---------------------------------ODF页面初始化时Store---开始---------------------------------------
var store = new Ext.data.Store({
        url : 'resource-dframe!getOdfList.action',
        baseParams : {
            "limit" : limit
        },
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, [
                "areaId", "areaName", "stationId", "stationName",
                "roomId", "roomName", "cableId", "cableNo", "cableName",
                "fiberResourceId", "fiberNo", "fiberName", "odfId", "odfNo",
                "outType", "outTarget", "neDisplayName", "outTargetName",
                "roomTargetName", "useable", "note","boolUserDeviceDomain"
            ])
    });
store.load({
    callback : function (r, options, success) { 
        if (success) {}
        else {
            var obj = Ext.decode(r.responseText);
            Ext.Msg.alert("提示", obj.returnMessage);
        }
    }
});
//---------------------------------ODF页面初始化时Store------结束------------------------------------

//---------------------------------查询条件“用途”combo的Store------开始-------------------------------------
var useStore = new Ext.data.Store({
        url : 'resource-dframe!getUseableList.action',
        baseParams : {},
        reader : new Ext.data.JsonReader({
            root : "rows"
        }, [
                "USEABLE"
            ])
    });
useStore.load({
    callback : function (r, options, success) {
        if (success) {}
        else {
            Ext.Msg.alert('错误', '查询失败！请重新查询');
        }
    }
});

//---------------------------------查询ODF子架--------------------------------------------
var odfNo,useable,roomId,cableName;
function searchODF() {
    var jsonData = {
        "conMap.odfNo" : Ext.getCmp('odfNoField').getValue(),
        "conMap.useable" : Ext.getCmp('useable').getValue(),
        "conMap.roomId" : Ext.getCmp('roomName').getRawValue().id,
        "conMap.cableName" : Ext.getCmp('CABLE_NAME').getValue(),
        "limit" : limit
    };
    store.proxy = new Ext.data.HttpProxy({
            url : 'resource-dframe!getOdfList.action'
        });
    store.baseParams = jsonData;
    store.load({ 
        callback : function (r, options, success) { 
            if (success) {
            	odfNo=Ext.getCmp('odfNoField').getValue();
            	useable=Ext.getCmp('useable').getValue();
            	roomId=Ext.getCmp('roomName').getRawValue().id;
            	cableName=Ext.getCmp('CABLE_NAME').getValue(); 
            }
            else {
                var obj = Ext.decode(r.responseText);
                Ext.Msg.alert("提示", obj.returnMessage);
            }
        }
    });
}

//--------------------------------- 增加ODF架--------------------------------------------
function addODF() {
	var roomId;
    var locator = new Ext.ux.AreaTree({
        xtype:"area",
        id:"locaotr_add"
    });
    var odfTreeWindow = new Ext.Window({
        title : '选择机房',
        id : 'addWindow',
        layout : 'fit',
        modal : true,
        closable : true,
        plain : true,
        width : 280,
        closeAction:'close',
        height : 500,
        items : [locator],
        autoScroll:true,   
        buttonAlign : "right",
        buttons : [{
            text : '确定',
            handler : function () {
                var nodes = locator.getSelectedNodes();
                var names = Ext.ux.AreaTree.LevelName;
                if (nodes.total == 0 || nodes.nodes[0].level != locator.maxLevel){
                	 Ext.Msg.alert('提示', '请选择' + names[locator.maxLevel] + '！');
                } else {
                	roomId = nodes.nodes[0].id; 
                    odfTreeWindow.close();
                    if (!roomId) {
                        Ext.Msg.alert('提示', '请选择机房！');
                    } else {
                        var url = "../ODF/addODF.jsp?roomId=" + roomId;
                        addOdfWindow = new Ext.Window({
                                id : 'addOdfWindow',
                                title : '新增ODF',
                                autoScroll:true,   
                                width : 1200,
                                height : 360,
                             	labelAlign : 'left',
                        		resizable:false,
                                isTopContainer : true,
                                modal : true,
                                plain : true, //是否为透明背景
                                html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
                            });
                        addOdfWindow.show();  
                    } 
                }
            }
        }, {
            text : '取消',
            handler : function () {
                odfTreeWindow.close();
            }
        }]
    });  
    odfTreeWindow.show();   
}

//--------------------------------- 删除ODF架-------------------------------------------
function deleteODF() { 
    var selectedRows = gridPanel.getSelectionModel().getSelections();
    if (selectedRows.length < 1) {
        Ext.Msg.alert('提示', '请选择需要删除的ODF信息！');
        return;
    }
    var sourceIds = new Array(); 
    for (var i = 0; i < selectedRows.length; i++) {  
    	if(selectedRows[i].get("boolUserDeviceDomain") == false){
   		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
   	     return; 
    	} 
        var odf = {
            "odfId" : selectedRows[i].get("odfId")
        }
        sourceIds.push(Ext.encode(odf));
    }
    if (sourceIds.length > 0) {
        Ext.Msg.confirm('提示', '确认删除？',
            function (btn) {
            if (btn == 'yes') { 
                var jsonData = {
                    "dataList" : sourceIds
                };
                Ext.Ajax.request({
                    url : 'resource-dframe!deleteOdf.action',
                    method : 'POST',
                    params : jsonData,
                    success : function (response) {
                        var obj = Ext.decode(response.responseText);
                        if (obj.returnResult == 1) {
                            Ext.Msg.alert("提示", obj.returnMessage, function (r) {
                                //刷新列表
                                var pageTool = Ext.getCmp('pageTool');
                                if (pageTool) {
                                    pageTool.doLoad(pageTool.cursor);
                                } 
                                useStore.reload();
                            });
                        }
                        if (obj.returnResult == 0) {
                            Ext.getBody().unmask();
                            Ext.Msg.alert("提示", obj.returnMessage);
                        }
                    },
                    error : function (response) {
                        top.Ext.getBody().unmask();
                        Ext.Msg.alert("异常", response.responseText);
                    },
                    failure : function (response) {
                        top.Ext.getBody().unmask();
                        Ext.Msg.alert("异常", response.responseText);
                    }
                });
            }
        });
    } else {
        Ext.Msg.alert('提示', '请选择记录！');
    }
}

//--------------------------------- 修改ODF架-------------------------------------------
function modifyODF() {
    var cell = gridPanel.getSelectionModel().getSelections(); 
    if (cell.length == 0) {
        Ext.Msg.alert("提示", "请选择需要修改的ODF信息！");
    } else if (cell.length > 1) {
        Ext.Msg.alert("提示", "只能选择一条ODF记录！");
    } else {
      	if(cell[0].get("boolUserDeviceDomain") == false){
      		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
      	     return; 
       	} else{
	        var mOdfParams = {
	            mOdfNo : cell[0].get('odfNo'),
	            mCableName : cell[0].get('cableName'),
	            mCableId : cell[0].get('cableId'),
	            mFiberNo : cell[0].get('fiberNo'),
	            mUseable : cell[0].get('useable'),
	            mNote : cell[0].get('note'),
	            odfId : cell[0].get('odfId'),
	            roomId : cell[0].get('roomId')
	        };
	        var url = "../ODF/modifyODF.jsp?" + Ext.urlEncode(mOdfParams);
	        var modifyOdfWindow = new Ext.Window({
	                id : 'modifyOdfWindow',
	                title : '修改ODF',
	                width : 400,
	                height : 250,
	                autoScroll:true,  
	             	labelAlign : 'left',
	                isTopContainer : true,
	                modal : true,
	                plain : true, //是否为透明背景
	                html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
	            });
	        modifyOdfWindow.show();    
       	}
    }
}

//--------------------------------- ODF架-关联------------------------------------------
var sourceIds = new Array();
var targetIds = new Array();
var odfPanelWindow ;
//关联的ODF存储store
var odfstore = new Ext.data.Store({
        url : 'resource-dframe!getRelateOdfList.action',
        reader : new Ext.data.JsonReader({
            root : "rows"
        }, [
                "RESOURCE_ODF_ID", "STATION_NAME", "ROOM_NAME", "ODF_NO"
            ])
    });
//关联的ODF的grid
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
        singleSelect : false
    });
var cm = new Ext.grid.ColumnModel({
        defaults : {
            sortable : true
        },
        columns : [new Ext.grid.RowNumberer({
    		width : 26
    	}), checkboxSelectionModel, {
                id : 'odfId',
                header : 'ODF配线架ID',
                dataIndex : 'RESOURCE_ODF_ID',
                hidden : true,
                width : 100
            }, {
                id : 'STATION_NAME',
                header : top.FieldNameDefine.STATION_NAME,
                dataIndex : 'STATION_NAME',
                width : 80
            }, {
                id : 'ROOM_NAME',
                header : '机房',
                dataIndex : 'ROOM_NAME',
                width : 80
            }, {
                id : 'ODF_NO',
                header : 'ODF端子号',
                dataIndex : 'ODF_NO',
                width : 80
            }
        ]
    });

var odfgridPanel = new Ext.grid.GridPanel({
        id : "odfgridPanel",
        region : "center",
        cm : cm,
        store : odfstore,
        stripeRows : true, // 交替行效果
        autoScroll : true,
        loadMask : {
            msg : '数据加载中...'
        },
        selModel : checkboxSelectionModel
    });

var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : true,
		leafType : 8
	};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);

//点击关联按钮，调用
function relate() {
    var cell = gridPanel.getSelectionModel().getSelections();
    if (cell.length == 0) {
        Ext.Msg.alert("提示", "请选择需要进行关联的ODF端子！");
    } else {
 
        var str = "";
        sourceIds = new Array();
        for (var i = 0; i < cell.length; i++) { 
           //记录已被占用端子号
           if(cell[i].get("outType") == "端口" || cell[i].get("outType") == "ODF"){
        	   str += cell[i].get("odfNo") +','; 
           } 
           if(cell[i].get("boolUserDeviceDomain") == false){
      		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
      	     return; 
           }
	       var odf = {
	           "odfId" : cell[i].get("odfId")
	       }
            sourceIds.push(Ext.encode(odf));
        }
        if(str!=""){ 
        	 Ext.Msg.alert("提示", "端子"+str.substring(0, str.length-1)+"已被占用！");
        }else{
	        var relatePanel = new Ext.FormPanel({
                region : "center",
                frame : false,
                border : false,
                bodyStyle : 'padding:20px 10px 30px',
                labelAlign : 'right',
                autoScroll : true,
                buttonAlign : 'center',
                items : [{
                        xtype : 'radiogroup',
                        id : 'relateRadio',
                        name : 'relateRadio',
                        columns : 1,
                        items : [{
                                boxLabel : "网元端口关联",
                                name : 'relateRadio',
                                inputValue : 0,
                                checked : true
                            }, {
                                boxLabel : "ODF端子关联",
                                name : 'relateRadio',
                                inputValue : 1
                            }
                        ]
                    }]
            });  
	        
	        	var  relateWindow = new Ext.Window({
	                id : 'relateWindow',
	                title : '关联类型选择',
	                width : 320,
	                height : 170,
	                autoScroll:true,   
	                resizable : false,
	                isTopContainer : true,
	                modal : true,
	                closeAction:'hide',
	                plain : true, //是否为透明背景
	                items : [relatePanel],
	                buttonAlign : "right",
	             	labelAlign : 'left',
	                buttons : [{
	                    text : '确定',
	                    handler : function () {
	                        if (relatePanel.form.isValid()) {  
	                            if (Ext.getCmp('relateRadio').getValue().inputValue == 0) {
	                                //网元关联		
	                            	var neRelatePanel = new Ext.Panel({
		                            		id : "neRelatePanel",
		                            		autoScroll : true,
		                            		width : 270,
		                            		height : 440,
		                            		forceFit : true,
		                            		collapsed : false, // initially collapse the group
		                            		collapsible : false,
		                            		collapseMode : 'mini',
		                            		split : true,
		                            		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
		                            				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
		                            	});
	                            	
	                            	var neTreeWindow = new Ext.Window({
		                            		id : "neTreeWindow", 
		                            		title : '选择端口',
		                            		width : 280,
		                            		height : 500,
	                            		    autoScroll:true,   
		                                    isTopContainer : true,
		                                    modal : true,
		                                    items:[neRelatePanel],
		                                    buttonAlign : "right",
		                                    buttons : [{
		                                        text : '确定',
		                                        handler : function () {
		                                        	// 选择tree中选中的节点
		                                        	var iframe = window.frames["tree_panel"];
		                                        	var selectedTargets = iframe.getCheckedNodes([ "nodeId", "nodeLevel" ],"all",8);
//		                                        	console.log(selectedTargets.length);
		                                        	if (selectedTargets.length == 0) {
		                                        		Ext.Msg.alert('提示', '请选择端口！');
		                                        		return;
		                                        	}
	                                        	    if (sourceIds.length != selectedTargets.length ) {
	                                                      Ext.Msg.alert('提示', '所选端口数量不一致！');
	                                                      return;
	                                                }
		                                        	for ( var i = 0; i < selectedTargets.length; i++) {
		                                        		if (selectedTargets[i].nodeLevel != 8) {
		                                        			Ext.Msg.alert('提示', '请选择端口！');
		                                        			return;
		                                        		}
		                                        	}
		                                        	
		                                        	targetIds = new Array();
		                                        	for ( var i = 0; i < selectedTargets.length; i++) {
		                                        		targetIds.push(Ext.encode(selectedTargets[i]));
		                                        	}
		                                        	 neTreeWindow.hide();
	                                                 associate(sourceIds, targetIds, 0,neTreeWindow);
							                    }
							                }, {
							                    text : '取消',
							                    handler : function () { 
							                    	neTreeWindow.close();
							                    }
						                	}]
	                        		});
	                            	relateWindow.close();
	                            	neTreeWindow.show();
	                            	
	                            } else if (Ext.getCmp('relateRadio').getValue().inputValue == 1) {
		                                //ODF关联
		                            var locator = new Ext.ux.AreaTree({
		                                    xtype:"area",
		                                    id:"locaotr"
		                                });
		                            var odfTreeWindow = new Ext.Window({
		                                    title : '选择机房',
		                                    id : 'odfTreeWindow',
		                                    layout : 'fit',
		                                    modal : true,
		                                    closable : true,
		                                    plain : true,
		                                    width : 280,
		                                    closeAction:'close',
		                                    autoScroll:true,   
		                                    height : 500,
		                                    items : [locator],
		                                    buttonAlign : "right",
		                                    buttons : [{
		                                        text : '确定',
		                                        handler : function () {
		                                            var nodes = locator.getSelectedNodes();
		                                            var names = Ext.ux.AreaTree.LevelName;
		                                            if (nodes.total == 0) {
		                                                //没选中
		                                                Ext.Msg.alert('提示', '请选择' + names[locator.maxLevel] + '！');
		                                            } else if (nodes.nodes[0].level != locator.maxLevel) {
		                                                Ext.Msg.alert('提示', '级别错误，请选择' + names[locator.maxLevel] + '！');
		                                            } else {
		                                                odfstore.baseParams = {
		                                                    "RESOURCE_ROOM_ID" : nodes.nodes[0].id
		                                                };
		                                                odfstore.load({
		                                                    callback : function (r, options, success) {
		                                                        if (success) {}
		                                                        else {
		                                                            var obj = Ext.decode(r.responseText);
		                                                            Ext.Msg.alert("提示", obj.returnMessage);
		                                                        }
		                                                    }
		                                                }); 
			                                                        
		                                                if(!odfPanelWindow){
		                                                    odfPanelWindow = new Ext.Window({
		                                                        title : '选择ODF端子',
		                                                        id : 'odfPanelWindow',
		                                                        layout : 'fit',
		                                                        modal : true,
		                                                        closable : true,
		                                                        plain : true,
		                                                     	labelAlign : 'left',
		                                                        width : 340,
		                                                        height : 400,
		                                                        autoScroll:true,   
		                                                        closeAction:'hide',
		                                                        buttonAlign : "right",
		                                                        items : [odfgridPanel],
		                                                        buttons : [{
		                                                            text : '确定',
		                                                            handler : function () {
		                                                                var cell = odfgridPanel.getSelectionModel().getSelections();
		                                                                if (cell.length == 0) {
		                                                                    Ext.Msg.alert("提示", "请选择需要进行关联的ODF端子 ！");
		                                                                } else {
		                                                                    if (sourceIds.length != cell.length) {
		                                                                        Ext.Msg.alert('提示', '所选端子数量不一致！');
		                                                                        return;
		                                                                    } else {
		                                                                    	targetIds = new Array();
		                                                                        for (var i = 0; i < cell.length; i++) {
		                                                                            var odf = {
		                                                                                "odfId" : cell[i].get("RESOURCE_ODF_ID")
		                                                                            }
		                                                                            targetIds.push(Ext.encode(odf));
		                                                                        }
		                                                                        odfPanelWindow.hide();
		                                                                        associate(sourceIds, targetIds, 1,odfPanelWindow);
		                                                                    }
		                                                                }
		                                                            }
		                                                        }, {
		                                                                text : '取消',
		                                                                handler : function () { 
		                                                                    odfPanelWindow.hide();
		                                                                }
		                                                        }]
		                                                    }); 
		                                                }
		                                                odfTreeWindow.close();
		                                                odfPanelWindow.show();
		                                            }
		                                        }
		                                    }, {
		                                        text : '取消',
		                                        handler : function () {
		                                            odfTreeWindow.close();
		                                        }
		                                    }
		                                ]
		                            }); 
			                                
				                        relateWindow.close();
				                        odfTreeWindow.show();
	                            }
	                        }
	                    }
	                }, {
		                    text : '取消',
		                    handler : function () { 
		                        relateWindow.close();
		                    }
	                }]
	        	});
	        
	    	relateWindow.show();
        }
	}
}

//ODF与端口或ODF关联,后台操作
function associate(sourceIds, targetIds, type,win) {

    var jsonData = {
        "sourceIds" : sourceIds,
        "targetIds" : targetIds
    }; 
    if (type == 0) {
        url = 'resource-dframe!associateOdfWithPtp.action';
    } else {
        url = 'resource-dframe!associateOdfWithOdf.action';
    } 
    Ext.Ajax.request({
        url : url,
        method : 'POST',
        params : jsonData,
        success : function (response) {
            var obj = Ext.decode(response.responseText);
            if (obj.returnResult == 1) {
           
                Ext.Msg.alert("提示", obj.returnMessage, function (r) { 
//                	console.log(obj.returnMessage.substring(0,1));
                	if(obj.returnMessage.substring(0,1)=="端" ||
                		obj.returnMessage == "请选择非自身端子！"){
                		win.show();
                	} else{
                 		if(type == 0){
                  			win.close();
                  		}else {
                  			 win=="";
                  		}
	                    //刷新列表
	                    var pageTool = Ext.getCmp('pageTool');
	                    if (pageTool) {
	                        pageTool.doLoad(pageTool.cursor);
	                    }	 
                	}
        		}); 
            } 
            if (obj.returnResult == 0) {
         		if(type == 0){
          			win.close();
          		}else {
          			 win=="";
          		}
                top.Ext.getBody().unmask();
                Ext.Msg.alert("提示", obj.returnMessage);
            }
        },
        error : function (response) {
            top.Ext.getBody().unmask();
            Ext.Msg.alert("异常", response.responseText);
        },
        failure : function (response) {
            top.Ext.getBody().unmask();
            Ext.Msg.alert("异常", response.responseText);
        }
    });
}

//--------------------------------- ODF架-删除关联------------------------------------------
function deleteRelate(){ 
	var selectedRows = gridPanel.getSelectionModel().getSelections();
	if(selectedRows.length < 1){
		Ext.Msg.alert('提示','请选择需要删除关联的ODF！');
		return;
	}
	var sourceIds = new Array(); 
	for(var i=0;i<selectedRows.length;i++){ 
    	if(selectedRows[i].get("boolUserDeviceDomain") == false){
    		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
    	     return; 
    	} 
        var odf = {
            "odfId" : selectedRows[i].get("odfId")
        }
       sourceIds.push(Ext.encode(odf));
		if(selectedRows[i].get('outType')==null ||selectedRows[i].get('outType')==""){
			Ext.Msg.alert('提示','此ODF不存在关联信息！');
			return;
		}
	}
	if(sourceIds.length > 0){
		Ext.Msg.confirm('提示','确认删除？',
	      function(btn){
	        if(btn=='yes'){ 
	        	
		        var jsonData = {
		    		"sourceIds":sourceIds
		    	};
		    	Ext.Ajax.request({ 
				    url: 'resource-dframe!deleteOdfRelate.action',
				    method : 'POST',
				    params: jsonData,
				    success: function(response) {
				    	var obj = Ext.decode(response.responseText);
				    	if(obj.returnResult == 1){
				    		top.Ext.getBody().unmask();
				    		Ext.Msg.alert("提示",obj.returnMessage, function(r){
				    			var pageTool = Ext.getCmp('pageTool');
				    			if(pageTool){
			    					pageTool.doLoad(pageTool.cursor);
			    				}
							});
				    	}
				    	if(obj.returnResult == 0){
		            		top.Ext.getBody().unmask();
		            		Ext.Msg.alert("提示",obj.returnMessage);
		            	}
				    },
				    error:function(response) {
				    	top.Ext.getBody().unmask();
		            	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) {
				    	top.Ext.getBody().unmask();
		            	Ext.Msg.alert("异常",response.responseText);
				    }
				});
	        }
	      });
	}
}

//--------------------------------- ODF架-导出------------------------------------------ 
function exportExcel(){ 
	var params = {
		"conMap.odfNo":odfNo,
		"conMap.useable":useable,
		"conMap.roomId":roomId,
		"conMap.cableName":cableName
	};
	if(store.getCount() == 0){
		Ext.Msg.alert("提示","信息列表为空！");
		return;
	}
//	top.Ext.getBody().mask('正在导出到Excel，请稍候...');
	Ext.Ajax.request({
		url:"resource-dframe!odfExport.action",
		type:"post",
		params:params,
		success:function (response){
			top.Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if(obj.returnResult == 1){
				window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
			}else if(obj.returnResult == 0){
				Ext.Msg.alert("提示","导出数据失败！");
			} 
		},
	    error:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 
}
