  Ext.state.Manager.setProvider(   
    new Ext.state.SessionStorageStateProvider({   
      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
    })   
  );
//------------------------------查询form--------------------------------- 
var formPanel = new Ext.FormPanel({
	region:"north",
    frame:false,
    id:"formPanel",
    border:false,
	bodyStyle : 'padding:10px 10px 0 10px', 
	autoHeight : true,
    labelAlign: 'right',
    collapsed: false,   // initially collapse the group
    collapseMode: 'mini',
    split:true,
    items: [{
        layout:'column',
        border:false,
        items:[{ 
            labelSeparator:"：",
            border:false,
    		layout : 'form',
            columnWidth : .25, 
	        //第一列
			items : [{
				xtype: 'areaselector',
		        privilege:viewAuth, 
				id:'roomName',
				name:'roomName',
				targetLevel:6,
				fieldLabel: '机房',
				width: 150
			}]
        },{
            labelSeparator:"：",
            border:false,
    		layout : 'form',
            columnWidth : .25, 
	        //第二列
			items : [{
				xtype: 'textfield',
				id:'ddfNoField',
				name:'ddfNoField',
				fieldLabel: 'DDF端子号'
	            }]
        },{
            labelSeparator:"：",
            border:false,
    		layout : 'form',
            columnWidth : .25, 
	            //第三列
			items : [{
				xtype: 'combo',
				id:'useable', 		
				fieldLabel: '用途',
				store:useStore,
				displayField:"USEABLE",
				valueField : 'USEABLE',
				triggerAction : 'all',
				editable : false  
			}]
        },{
            //第四列
            border:false,
    		layout : 'form', 
        	items : [{
        		xtype:'button',  
	        	text:'重置',
	            privilege:viewAuth, 
	        	width:80,
	        	handler: function(){
	        	    formPanel.getForm().reset(); 
	        	}	
        	}]
        }]
    }]
});
//------------------------------DDF显示页面的grid--------------------------------- 

var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel();
checkboxSelectionModel.sortLock();
var cm = new Ext.ux.grid.LockingColumnModel({
    // specify any defaults for each column
    defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer({
    	width:26,
    	locked: true
    	}),checkboxSelectionModel,
    {
    	id : 'ddfId',
        header: 'DDF配线架ID',
        dataIndex: 'ddfId',
        hidden:true,
        width: 100
    },{
    	id : 'roomId',
        header: '机房ID',
        dataIndex: 'roomId',
        hidden:true,
        width: 100
    },{
    	id : 'stationId',
        header: top.FieldNameDefine.STATION_NAME+'ID',
        dataIndex: 'stationId',
        hidden:true,
        width: 100
    },{
    	id : 'areaId',
        header: top.FieldNameDefine.AREA_NAME+'ID',
        dataIndex: 'areaId',
        hidden:true,
        width: 100
    },{
        id:'areaName',
        header: top.FieldNameDefine.AREA_NAME,
        dataIndex: 'areaName',
        width: 220
    },{
        id:'stationName',
        header: top.FieldNameDefine.STATION_NAME,
        dataIndex: 'stationName',
        width: 110
    },{
        id:'roomName',
        header: '机房',
        dataIndex: 'roomName',
        width: 110
    },{
        id:'ddfNo',
        header: 'DDF端子号',
        dataIndex: 'ddfNo',
        width: 110
    },{
        id:'neDisplayName',
        header: '网元',
        dataIndex: 'neDisplayName',
        width: 110,
        renderer:function(value, cellmeta, record){
        	if(record.get("boolUserDeviceDomain") == false){
        		return "<span style='color:red'>"+value+"</span>";
        	}else{
        		return value;
        	}
        } 
    },{
        id:'ptpDisplayName',
        header: '端口',
        dataIndex: 'ptpDisplayName',
        width: 200
    },{
        id:'roomTargetName',
        header: '目标端子机房',
        dataIndex: 'roomTargetName',
        width: 110
    },{
        id:'destination',
        header: '目标端子',
        dataIndex: 'destination',
        width: 110
    },{
        id:'useable',
        header: '用途',
        dataIndex: 'useable',
        width: 100
    },{
        id:'note',
        header: '备注',
        dataIndex: 'note',
        width: 120
    }]
});

//-----------------------------------分页--------------------------------------------------
var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: 200,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
});
 
//-----------------------------------grid-------------------------------------------------
var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	region:"center",
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    loadMask: {msg: '数据加载中...'},
    selModel:checkboxSelectionModel ,  
	view: new Ext.ux.grid.LockingGridView(),
	stateId:'ddfgridPanelId',  
	stateful:true, 
	bbar: pageTool,
    tbar: ['-',{
            text: '查询',
            privilege:viewAuth, 
            icon:'../../../resource/images/btnImages/search.png',
            handler: searchDDF
        },'-',{
        	text: '新增',
        	privilege:addAuth,
            icon:'../../../resource/images/btnImages/add.png',
        	handler: addDDF
	    },{
        	text: '删除',
        	privilege:delAuth,  
            icon:'../../../resource/images/btnImages/delete.png',
        	handler: deleteDDF
	    },{
        	text: '修改',
        	privilege:modAuth,
            icon:'../../../resource/images/btnImages/modify.png',
        	handler: modifyDDF
	    },'-',{
        	text: '关联',
        	privilege:modAuth,
            icon:'../../../resource/images/btnImages/associate.png',
        	handler: relate
	    },{
        	text: '删除关联',
        	privilege:modAuth,
        	icon:'../../../resource/images/btnImages/disassociate.png',
        	handler: deleteRelate
	    },'-',{
        	text: '设置跳线', 
        	privilege:actionAuth,
        	handler: jumpLine
	    },{
        	text: '删除跳线', 
        	privilege:actionAuth,
        	handler: delJumpLine
	    },'-',{
        	text: '导出',
        	privilege:viewAuth, 
          	icon:'../../../resource/images/btnImages/export.png',
        	handler: exportExcel
	    }]
});
//==========================center=============================
var centerPanel = new Ext.Panel({
    id:'centerPanel',
    border:false,
    region:'center',
    autoScroll:true,
    layout:'border',  
    items:[formPanel,gridPanel]
});
//-----------------------------------------init the page--------------------------------------------
Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
	Ext.Ajax.timeout=900000; 
    var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [centerPanel],
        renderTo : Ext.getBody()
    }); 
});
