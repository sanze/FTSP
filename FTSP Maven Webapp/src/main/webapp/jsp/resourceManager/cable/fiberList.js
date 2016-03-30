Ext.QuickTips.init();

var formPanel = new Ext.FormPanel({
	id:"formPanel",
	region:"north",
    frame:false,
    border:false,
    labelAlign:'left',
    labelWidth:50,
    bodyStyle:'padding:10px 10px 10px 10px',
	height: 40, 
    items : [{
    	layout : 'column',
		border : false,
		items : [{ 
			layout : 'form',
			labelSeparator : "：", 
			border : false,
		    columnWidth: .2,
			items : [{ 
		    	xtype:'label',
		    	id:'cables',
				name: 'cables', 
				fieldLabel:'光缆' 
		    }]
		},{
			layout : 'form',
			labelSeparator : "：", 
			border : false, 
	        columnWidth: .25,
			items : [{ 
				xtype:'label',
		    	id:'cable',
				name: 'cable', 
				fieldLabel:'光缆段'
			}]
	    }]
    }]
});

var store = new Ext.data.Store({
	url : 'resource-cable!getFiberResourceList.action',
    reader: new Ext.data.JsonReader({
    totalProperty: 'total',
			root : "rows"
	},[
		"RESOURCE_FIBER_ID","FIBER_NO","FIBER_NAME",
		"ATT_VALUE","NOTE","ATT","ATT_BUILD","linkName",
		"attExper","attTheory" 
	])
});
store.on('update',function(){
	Ext.getCmp('saveFiber').enable();
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect:true});
var cm = new Ext.grid.ColumnModel({ 
    defaults: {
        sortable: false         
    },
    columns: [new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel,{
        id:'FIBER_NO',
        header:'芯号',
        dataIndex: 'FIBER_NO',
        width: 70
    },{
        id:'FIBER_NAME',
        header: '光纤名称',
        dataIndex: 'FIBER_NAME',
        width: 120,
        editor: new Ext.form.TextField({
        	allowBlank: true
        }),
        tooltip:'可编辑列'
    },{
        id:'linkName',
        header:'承载链路',
        dataIndex: 'linkName',
        width: 120,
    	renderer : function(v, metadata, record) { 
			return ((v == null) ? "" : "<a href='#'>" + v + "</a>");
		}
    },{
    	id : 'ATT_BUILD',
    	header: '<span style="font-weight:bold">衰耗竣工值(dB)</span>',
    	dataIndex: 'ATT_BUILD',
        width: 230,
        editor: new Ext.form.NumberField({
        	allowDecimals:true,
			allowNegative : false, 
        	allowBlank: true
        }),
        tooltip:'可编辑列'
    },{
        id:'ATT_VALUE',
        header: '<span style="font-weight:bold">衰耗基准值(dB)</span>',
        dataIndex: 'ATT_VALUE',
        width: 190,
        editor: new Ext.form.NumberField({
        	allowDecimals:true,
			allowNegative : false, 
        	allowBlank: true
        }),
        tooltip:'可编辑列'
    },{
    	id : 'attTheory',
    	header: '衰耗系统理论值(dB/km)',
    	dataIndex: 'attTheory',
        width: 230 
    },{
		id : 'attExper',
		header: '衰耗系统经验值(dB/km)',
		dataIndex: 'attExper',
	    width: 230
	},{
        id:'ATT',
        header: '<span style="font-weight:bold">衰耗器(dB)</span>',
        dataIndex: 'ATT',
        width: 160,
        editor: new Ext.form.NumberField({
        	allowDecimals:true,
			allowNegative : false, 
        	allowBlank: true
        }),
        tooltip:'可编辑列'
    },{
        id:'NOTE',
        header: '<span style="font-weight:bold">备注</span>',
        dataIndex: 'NOTE',
        width: 180,
        editor: new Ext.form.TextField({
        	allowBlank: true
        }),
        tooltip:'可编辑列'
    }]
});

var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: 200,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
});

var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	region:"center",
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    loadMask: {msg: '数据加载中...'},
    selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox  
	viewConfig: {
        forceFit:true
    },
	bbar: pageTool,
    buttons:[{
    	text:'保存',
    	disabled:true,
    	id:'saveFiber',
    	handler:modifyFiber
    }, {
    	text:'关闭',
    	handler:function(){
    		var win = parent.Ext.getCmp("fiberListWindow");
    		if(win){
    			win.close();
    		}
    	}
    }],
    listeners: {
        'cellclick':function(grid, rowIndex, columnIndex, e) {
            var record = grid.getStore().getAt(rowIndex); 
            var fieldName = grid.getColumnModel().getDataIndex(columnIndex);
            if('linkName'==fieldName){
            	var url = "../resourceManager/cable/relateLinks.jsp?fiberId="+record.get("RESOURCE_FIBER_ID"); 
        		parent.parent.addTabPage(url, "相关链路",authSequence); 
            }
        }
    }
}); 

function modifyFiber(){
	var records=store.getModifiedRecords();
	var jsonString=new Array();
	if(records.length == 0){
		Ext.Msg.alert('信息','没有修改记录！');
		return;
	} 
	for(var i=0;i<records.length;i++){
		var VFiberResource = {
			"RESOURCE_FIBER_ID":records[i].get("RESOURCE_FIBER_ID"),
			"FIBER_NAME":records[i].get("FIBER_NAME"),
			"ATT_VALUE":records[i].get("ATT_VALUE"), 
			"ATT":records[i].get("ATT"),
			"ATT_BUILD":records[i].get("ATT_BUILD"),
			"NOTE":records[i].get("NOTE")
    	};
		jsonString.push(VFiberResource);
	}
	var jsonData = {
		"jsonString":Ext.encode(jsonString)
	};
	
    Ext.Msg.confirm('提示','确认修改？',
      function(btn){
        if(btn=='yes'){
        	//提交修改，不然store.getModifiedRecords();数据会累加
    		store.commitChanges();
        	
            top.Ext.getBody().mask('正在执行，请稍候...');
	    	Ext.Ajax.request({ 
			    url: 'resource-cable!modifyFiberResource.action',
			    method : 'POST',
			    params: jsonData,
			    success: function(response) {
			    	top.Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);
	            	Ext.Msg.alert("信息",obj.returnMessage, function(r){
	            		//刷新列表
	            		var pageTool = Ext.getCmp('pageTool');
	            		if(pageTool){
	    					pageTool.doLoad(pageTool.cursor);
	    				}
	            		Ext.getCmp('saveFiber').disable();
	    			});
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
        }else{
	       
        }
      });
}

function initData(){
	store.baseParams = {
		"cellId":cableId,
		"limit":200
	};
	store.load(); 
}


Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};

	var win = new Ext.Viewport({
		id:'win',
		loadMask : true,
		layout: 'border',
		items : [formPanel,gridPanel] 
	}); 
	Ext.getCmp("cable").setText(cable);
	Ext.getCmp("cables").setText(cables);
	initData();
  });