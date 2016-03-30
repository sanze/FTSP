var limit = 200;
function debug(type, title, msg) {
    console[type](title + " - " + msg);
}
//---------------------------------DDF页面初始化时Store---开始---------------------------------------
var store = new Ext.data.Store({
        url : 'resource-dframe!getDdfList.action',
        baseParams : {
            "limit" : limit
        },
        reader : new Ext.data.JsonReader({
            totalProperty : 'total',
            root : "rows"
        }, [
                "areaId", "areaName", "stationId", "stationName",
                "roomId", "roomName", "ddfId", "ddfNo","source",
                "ptpId", "neId", "neDisplayName", "ptpDisplayName",
                "roomTargetName", "destination","useable", "note",
                "boolUserDeviceDomain"
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
//---------------------------------DDF页面初始化时Store------结束------------------------------------

//function 
//---------------------------------查询条件“用途”combo的Store------开始-------------------------------------
var useStore = new Ext.data.Store({
        url : 'resource-dframe!getDdfUseableList.action',
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

//---------------------------------查询DDF子架--------------------------------------------
var ddfNo,useable, roomId;
function searchDDF() {
    var jsonData = {
        "conMap.ddfNo" : Ext.getCmp('ddfNoField').getValue(),
        "conMap.useable" : Ext.getCmp('useable').getValue(),
        "conMap.roomId" : Ext.getCmp('roomName').getRawValue().id,
        "limit" : limit
    };
    store.proxy = new Ext.data.HttpProxy({
            url : 'resource-dframe!getDdfList.action'
        });
    store.baseParams = jsonData;
    store.load({ 
        callback : function (r, options, success) { 
            if (success) {
            	ddfNo=Ext.getCmp('ddfNoField').getValue();
            	useable=Ext.getCmp('useable').getValue();
            	roomId=Ext.getCmp('roomName').getRawValue().id;
            }else {
                var obj = Ext.decode(r.responseText);
                Ext.Msg.alert("提示", obj.returnMessage);
            }
        }
    });
}

//--------------------------------- 增加DDF架--------------------------------------------
function addDDF() {
	var roomId;
    var locator = new Ext.ux.AreaTree({
        xtype:"area",
        id:"locaotr_ddf"
    });
    var ddfTreeWindow = new Ext.Window({
        title : '选择机房',
        id : 'addWindow',
        layout : 'fit',
        modal : true,
        closable : true,
        autoScroll:true,   
        plain : true,
        width : 280,
        closeAction:'close',
        height : 500,
        items : [locator],
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
                    ddfTreeWindow.close();
                    if (!roomId) {
                        Ext.Msg.alert('提示', '请选择机房！');
                    } else {
                        var url = "../DDF/addDDF.jsp?roomId=" + roomId;
                        addDdfWindow = new Ext.Window({
                                id : 'addDdfWindow',
                                title : '新增DDF',
                                width : 780,
                                height : 340,
                            	labelAlign : 'left',
                                isTopContainer : true,
                                resizable:false,
                                modal : true,
                                autoScroll:true,   
                                plain : true, //是否为透明背景
                                html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
                            });
                        addDdfWindow.show();  
                    } 
                }
            }
        }, {
            text : '取消',
            handler : function () {
                ddfTreeWindow.close();
            }
        }]
    });  
    ddfTreeWindow.show(); 
}

//--------------------------------- 删除DDF架-------------------------------------------
function deleteDDF() {
    var selectedRows = gridPanel.getSelectionModel().getSelections();
    if (selectedRows.length < 1) {
        Ext.Msg.alert('提示', '请选择需要删除的DDF信息！');
        return;
    }
    var sourceIds = new Array();
    for (var i = 0; i < selectedRows.length; i++) {
    	if(selectedRows[i].get("boolUserDeviceDomain") == false){
      		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
      	     return; 
       	} 
        var ddf = {
            "ddfId" : selectedRows[i].get("ddfId")
        }
        sourceIds.push(Ext.encode(ddf));
    }
    if (sourceIds.length > 0) {
        Ext.Msg.confirm('提示', '确认删除？',
            function (btn) {
            if (btn == 'yes') { 
                var jsonData = {
                    "dataList" : sourceIds
                };
                Ext.Ajax.request({
                    url : 'resource-dframe!deleteDdf.action',
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
                            Ext.Msg.alert("错误", obj.returnMessage);
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
    }  
}

//--------------------------------- 修改DDF架-------------------------------------------
function modifyDDF() {
    var cell = gridPanel.getSelectionModel().getSelections(); 
    if (cell.length == 0) { 
        Ext.Msg.alert("提示", "请选择需要修改的DDF信息！");
    } else if (cell.length > 1) {
        Ext.Msg.alert("提示", "只能选择一条DDF记录！");
    } else {
    	if(cell[0].get("boolUserDeviceDomain") == false){
     		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
     	     return; 
      	} else{
	        var mDdfParams = {
	            mDdfNo : cell[0].get('ddfNo'),
	            mUseable : cell[0].get('useable'),
	            mNote : cell[0].get('note'),
	            ddfId : cell[0].get('ddfId')
	        };
	        var url = "../DDF/modifyDDF.jsp?" + Ext.urlEncode(mDdfParams);
	        var modifyDdfWindow = new Ext.Window({
	                id : 'modifyDdfWindow',
	                title : '修改DDF',
	            	labelAlign : 'left',
	                width : 400,
	                height : 220,
	                isTopContainer : true,
	                modal : true,
	                autoScroll:true,   
	                plain : true, //是否为透明背景
	                html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
	            });
	        modifyDdfWindow.show();  
      	}
    }
}

//--------------------------------- DDF架-关联------------------------------------------
var sourceIds = new Array();
var targetIds = new Array();
var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : true,
		leafType : 8
	};
var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
//------------------------------------------------------------------------------------
//点击关联按钮，调用
function relate() {
    var cell = gridPanel.getSelectionModel().getSelections();
    if (cell.length == 0) {
        Ext.Msg.alert("提示", "请选择需要进行关联的DDF端子！");
    } else { 
        var str = "";
        sourceIds = new Array();
        for (var i = 0; i < cell.length; i++) { 
           //记录已被占用端子号
           if(cell[i].get("ptpId") != ''){
        	   str += cell[i].get("ddfNo") +','; 
           } 
	       var ddf = {
	           "ddfId" : cell[i].get("ddfId")
	       }
            sourceIds.push(Ext.encode(ddf));
        }
 
        if(str!=""){ 
        	 Ext.Msg.alert("提示", "端子"+str.substring(0, str.length-1)+"已被占用！");
        }else{  
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
                         associate(sourceIds, targetIds,0, neTreeWindow);
                    }
                }, {
                    text : '取消',
                    handler : function () { 
                    	neTreeWindow.close();
                    }
            	}]
    		});
	        neTreeWindow.show(); 
        }
	}
}


//--------------------------------- DDF架-跳线管理------------------------------------------
var ddfPanelWindow ;
//关联的DDF存储store
var ddfstore = new Ext.data.Store({
      url : 'resource-dframe!getRelateDdfList.action',
      reader : new Ext.data.JsonReader({
          root : "rows"
      }, [
              "RESOURCE_DDF_ID", "STATION_NAME", "ROOM_NAME", "DDF_NO"
          ])
  });
//关联的DDF的grid
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
            id : 'ddfId',
            header : 'DDF配线架ID',
            dataIndex : 'RESOURCE_DDF_ID',
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
            id : 'DDF_NO',
            header : 'DDF端子号',
            dataIndex : 'DDF_NO',
            width : 80
      }]
  });

var ddfgridPanel = new Ext.grid.GridPanel({
        id : "ddfgridPanel",
        region : "center",
        cm : cm,
        store : ddfstore,
        stripeRows : true, // 交替行效果
        autoScroll : true,
        loadMask : {
          msg : '数据加载中...'
        },
        selModel : checkboxSelectionModel
    });

//点击设置跳线按钮，调用
function jumpLine() {
    var cell = gridPanel.getSelectionModel().getSelections();
    if (cell.length == 0) {
        Ext.Msg.alert("提示", "请选择需要设置跳线的DDF！");
    } else { 
        var str = "";
        sourceIds = new Array();
        for (var i = 0; i < cell.length; i++) { 
        if(cell[i].get("boolUserDeviceDomain") == false){
            Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
            return; 
        } else{
          //记录已被占用端子号
        if(cell[i].get("destination") != ''&& cell[i].get("destination") != null){
            str += cell[i].get("ddfNo") +','; 
        } 
        var ddf = {
            "ddfId" : cell[i].get("ddfId")
        }
        sourceIds.push(Ext.encode(ddf));
        }
    }
      
    if(str!=""){ 
        Ext.Msg.alert("提示", "端子"+str.substring(0, str.length-1)+"已存在跳线！");
    }else{  
        //DDF关联
        var locID =Ext.id();
        var locator = new Ext.ux.AreaTree({
                xtype:"area",
                id:locID
                });
        var ddfTreeWindow = new Ext.Window({
            title : '选择机房',
            id : 'ddfTreeWindow',
            layout : 'fit',
            modal : true,
            closable : true,
            plain : true,
            width : 280,
            closeAction:'close',
            height : 500,
            autoScroll:true,   
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
                        ddfstore.baseParams = {
                            "RESOURCE_ROOM_ID" : nodes.nodes[0].id
                        };
	                    ddfstore.load({
	                        callback : function (r, options, success) {
	                            if (success) {}
	                            else {
	                                var obj = Ext.decode(r.responseText);
	                                Ext.Msg.alert("提示", obj.returnMessage);
	                            }
	                        }
	                    });  
	                    if(!ddfPanelWindow){ 
	                        ddfPanelWindow = new Ext.Window({
	                            title : '选择DDF端子',
	                            id : 'ddfPanelWindow',
	                            layout : 'fit',
	                            autoScroll:true,   
	                            modal : true,
	                            closable : true,
	                            plain : true,
	                            width : 340,
	                            height : 400,
	                         	labelAlign : 'left',
	                            closeAction:'hide',
	                            buttonAlign : "right",
	                            items : [ddfgridPanel],
	                            buttons : [{
	                                text : '确定',
	                                handler : function () {
	                                    var cell = ddfgridPanel.getSelectionModel().getSelections();
	                                    if (cell.length == 0) {
	                                        Ext.Msg.alert("提示", "请选择跳线的目标DDF端子 ！");
	                                    } else {
	                                        if (sourceIds.length != cell.length) {
	                                            Ext.Msg.alert('提示', '所选端子数量不一致！');
	                                            return;
	                                        } else {
	                                        	targetIds = new Array();
	                                            for (var i = 0; i < cell.length; i++) {
	                                                var ddf = {
	                                                    "ddfId" : cell[i].get("RESOURCE_DDF_ID")
	                                                }
	                                                targetIds.push(Ext.encode(ddf));
	                                            }
	                                            ddfPanelWindow.hide();
	                                            associate(sourceIds, targetIds, 1,ddfPanelWindow);
	                                        }
	                                    }
	                                }
	                            }, {
	                                text : '取消',
	                                handler : function () { 
	                                    ddfPanelWindow.hide();
	                                }
	                            }]
	                        }); 
	                    }
	                        ddfTreeWindow.close();
	                        ddfPanelWindow.show();
	                    }
	                }
	            }, {
	                text : '取消',
	                    handler : function () {
	                        ddfTreeWindow.close();
	                    }
	            }]
	        });  
	        ddfTreeWindow.show();
	  }
  }
}
     
//点击删除跳线按钮，调用
function delJumpLine() {
	var selectedRows = gridPanel.getSelectionModel().getSelections();
	if (selectedRows.length == 0) {
		Ext.Msg.alert("提示", "请选择需要删除跳线的DDF！");
	} 
	var sourceIds = new Array();
	for(var i=0;i<selectedRows.length;i++){
		if(selectedRows[i].get("boolUserDeviceDomain") == false){
     		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
     	     return; 
      	} else{
	}
		var ddf = {
			"ddfId" : selectedRows[i].get("ddfId")
		}
		sourceIds.push(Ext.encode(ddf)); 
		if(selectedRows[i].get('destination')==null ||selectedRows[i].get('destination')==""){
			Ext.Msg.alert('提示','此DDF不存在跳线信息！');
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
				    url: 'resource-dframe!deleteDdfJumpLine.action',
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
		            		Ext.Msg.alert("错误",obj.returnMessage);
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
     


//DDF与端口或条线管理,后台操作
function associate(sourceIds, targetIds,type ,win) {
    var jsonData = {
        "sourceIds" : sourceIds,
        "targetIds" : targetIds
    };  
    if (type == 0) {
        url = 'resource-dframe!associateDdfWithPtp.action';
    } else {
        url = 'resource-dframe!associateDdfWithDdf.action';
    } 
    Ext.Ajax.request({
        url : url,
        method : 'POST',
        params : jsonData,
        success : function (response) {
            var obj = Ext.decode(response.responseText); 
            if (obj.returnResult == 1) { 
                Ext.Msg.alert("提示", obj.returnMessage, function (r){  
                    if(obj.returnMessage.substring(0,1)=="端"||
                    		obj.returnMessage == "请选择非自身端子！"){ 
                        win.show();
                    } else{
                        if(type == 0){
                            win.close();
                        }else {
                            win=="";
                        }
                        var pageTool = Ext.getCmp('pageTool');
                        if (pageTool) {
                            pageTool.doLoad(pageTool.cursor);
                        }	 
                    }
                }); 
            } 
            if (obj.returnResult == 0){
                if(type == 0){
                	win.close();
                }else {
                    win=="";
                } 
                top.Ext.getBody().unmask();
                Ext.Msg.alert("错误", obj.returnMessage);
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


//--------------------------------- DDF架-删除关联------------------------------------------
function deleteRelate(){ 
	var selectedRows = gridPanel.getSelectionModel().getSelections();
	if(selectedRows.length < 1){
		Ext.Msg.alert('提示','请选择需要删除关联的DDF！');
		return;
	}
	var sourceIds = new Array();

	for(var i=0;i<selectedRows.length;i++){
		if(selectedRows[i].get("boolUserDeviceDomain") == false){
    		 Ext.Msg.alert('提示', '红色字体网元不在您的设备域，不能操作！');
    	     return; 
     	}  
        var ddf = {
            "ddfId" : selectedRows[i].get("ddfId")
        }
       sourceIds.push(Ext.encode(ddf));
		if(selectedRows[i].get('ptpId')==null ||selectedRows[i].get('ptpId')==""){
			Ext.Msg.alert('提示','此DDF不存在关联信息！');
			return;
		}
	}
	if(sourceIds.length > 0){
		Ext.Msg.confirm('提示','确认删除？',function(btn){
            if(btn=='yes'){ 
            
                var jsonData = {
                    "sourceIds":sourceIds
                };
                Ext.Ajax.request({ 
                    url: 'resource-dframe!deleteDdfRelate.action',
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
                            Ext.Msg.alert("错误",obj.returnMessage);
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

//--------------------------------- DDF架-导出------------------------------------------ 
function exportExcel(){  
	var params = {
        "conMap.ddfNo" : ddfNo,
        "conMap.useable" : useable,
        "conMap.roomId" : roomId 
	};
	if(store.getCount() == 0){
		Ext.Msg.alert("提示","信息列表为空！");
		return;
	}
//	top.Ext.getBody().mask('正在导出到Excel，请稍候...');
	Ext.Ajax.request({
		url:"resource-dframe!ddfExport.action",
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
