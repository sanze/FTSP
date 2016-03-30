  Ext.state.Manager.setProvider(   
    new Ext.state.SessionStorageStateProvider({   
      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
    })   
  );

//-------------------光缆名称的联想输入框------开始------------------------------
var cableStore = new Ext.data.Store(
{
    proxy : new Ext.data.HttpProxy({
		url : 'resource-dframe!getCableNameList.action',
		async: false
    }), 
    pageSize:10,
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows"
    },[
       "RESOURCE_CABLE_ID","CABLE_NAME"
    ])
});

var Cable = new Ext.form.ComboBox({
    id: 'CABLE_NAME',
    width: 150,
    minListWidth: 220,
    store: cableStore,
    fieldLabel: '光缆名称',
    valueField: 'CABLE_NAME',
    displayField: 'CABLE_NAME',
    emptyText : '输入对象名',
    listEmptyText: '未找到匹配的结果',
    loadingText: '搜索中...',
    mode:'remote',  
    pageSize:cableStore.pageSize,
    queryDelay: 400,
    typeAhead: false,
    autoSelect:false,
    enableKeyEvents : true,
    resizable: true,
    autoScroll:true,
    listeners : {
      keypress: function(field, event) {
        field.setValue(field.getRawValue());
        if(event.getKey()==event.ENTER){//输入回车后开始过滤节点树
          gKey = field.getValue();
          if(gKey == null || gKey==""){
            return;
          }
        }
      },
      beforequery:function(event){
        if(event.combo.lastQuery!=event.combo.getRawValue()){
        	event.combo.lastQuery=event.combo.getRawValue();
        	queryCable(event.combo,event.combo.getRawValue());
          return false;
        }
      },
      scope : this
    }
  }); 
		
function queryCable(combo,gKey){
    cableStore.baseParams={
		"CABLE_NAME":gKey,
		"limit": cableStore.pageSize
	};
	cableStore.load({
		callback : function(records,options,success){
			if(!success)
				Ext.Msg.alert("提示","模糊搜索出错");
		}
	});
    combo.expand();
}

//------------------------------查询form--------------------------------- 
var formPanel = new Ext.FormPanel({
	region:"north",
    frame:false,
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
            columnWidth : .22, 
	        //第一列
			items : [{
				xtype: 'areaselector',
				id:'roomName',
			    privilege:viewAuth, 
				name:'roomName',
				targetLevel:6,
				fieldLabel: '机房',
				width: 150
			}]
        },{
            labelSeparator:"：",
            border:false,
    		layout : 'form',
            columnWidth : .22, 
	        //第二列
			items : [{
				xtype: 'textfield',
				id:'odfNoField',
				name:'odfNoField',
				fieldLabel: 'ODF端子号',
				width: 150
	            }]
        },{
            labelSeparator:"：",
            border:false,
    		layout : 'form',
            columnWidth : .22, 
		        //第三列
			items : [Cable]
        },{
            labelSeparator:"：",
            border:false,
    		layout : 'form',
            columnWidth : .22, 
	            //第四列
			items : [{
				xtype: 'combo',
				id:'useable', 		
				fieldLabel: '用途',
				store:useStore,
				displayField:"USEABLE",
				valueField : 'USEABLE',
				triggerAction : 'all',
				editable : false, 
				width: 150,
				listeners : { 
				} 
			}]
        },{
            //第五列
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

//------------------------------ODF显示页面的grid--------------------------------- 

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
    	id : 'odfId',
        header: 'ODF配线架ID',
        dataIndex: 'odfId',
        hidden:true,
        width: 80
    },{
    	id : 'fiberResourceId',
        header: '光纤ID',
        dataIndex: 'fiberResourceId',
        hidden:true,
        width: 80
    },{
    	id : 'cableId',
        header: '光缆ID',
        dataIndex: 'cableId',
        hidden:true,
        width: 80
    },{
    	id : 'roomId',
        header: '机房ID',
        dataIndex: 'roomId',
        hidden:true,
        width: 80
    },{
    	id : 'stationId',
        header: top.FieldNameDefine.STATION_NAME+'ID',
        dataIndex: 'stationId',
        hidden:true,
        width: 80
    },{
    	id : 'areaId',
        header: top.FieldNameDefine.AREA_NAME+'ID',
        dataIndex: 'areaId',
        hidden:true,
        width: 80
    },{
    	id : 'outTarget',
        header: '配出线ID',
        dataIndex: 'outTarget',
        hidden:true,
        width: 80
    }
    ,{
        id:'areaName',
        header: top.FieldNameDefine.AREA_NAME,
        dataIndex: 'areaName',
        width: 200
    },{
        id:'stationName',
        header: top.FieldNameDefine.STATION_NAME,
        dataIndex: 'stationName',
        width: 80
    },{
        id:'roomName',
        header: '机房',
        dataIndex: 'roomName',
        width: 100
    },{
        id:'odfNo',
        header: 'ODF端子号',
        dataIndex: 'odfNo',
        width: 80
    },{
        id:'cableName',
        header: '光缆名称',
        dataIndex: 'cableName',
        width: 100
    },{
        id:'cableNo',
        header: '光缆编号',
        dataIndex: 'cableNo',
        width: 80
    },{
        id:'fiberName',
        header: '光纤名称',
        dataIndex: 'fiberName',
        width: 100
    },{
        id:'fiberNo',
        header: '光纤编号',
        dataIndex: 'fiberNo',
        width: 80
    },{
        id:'outType',
        header: '配出类型',
        dataIndex: 'outType',
        width: 80
//        ,
//        renderer :transOutType
    },{
        id:'roomTargetName',
        header: '配出机房',
        dataIndex: 'roomTargetName',
        width: 100
    },{
        id:'neDisplayName',
        header: '网元',
        dataIndex: 'neDisplayName',
        width: 80,
        renderer:function(value, cellmeta, record){
        	if(record.get("boolUserDeviceDomain") == false){
        		return "<span style='color:red'>"+value+"</span>";
        	}else{
        		return value;
        	}
        } 
    },{
        id:'outTargetName',
        header: '配出端子',
        dataIndex: 'outTargetName',
        width: 150
    },{
        id:'useable',
        header: '用途',
        dataIndex: 'useable',
        width: 80
    },{
        id:'note',
        header: '备注',
        dataIndex: 'note',
        width: 80
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


var gridPanel = new Ext.grid.EditorGridPanel({
	id:"gridPanel",
	region:"center",
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    loadMask: {msg: '数据加载中...'},
    selModel:checkboxSelectionModel ,  
	view: new Ext.ux.grid.LockingGridView(),
	stateId:'odfgridPanelId',  
	stateful:true, 
	bbar: pageTool,
    tbar: ['-',{
            text: '查询', 
            privilege:viewAuth, 
            icon:'../../../resource/images/btnImages/search.png',
            handler: searchODF
        },'-',{
        	text: '新增',
        	privilege:addAuth, 
        	icon:'../../../resource/images/btnImages/add.png',
        	handler: addODF
	    },{
        	text: '删除',
        	privilege:delAuth, 
        	icon:'../../../resource/images/btnImages/delete.png',
        	handler: deleteODF
	    },{
        	text: '修改',
        	privilege:modAuth, 
        	icon:'../../../resource/images/btnImages/modify.png',
        	handler: modifyODF
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
//
////转换配出类型
//function transOutType(v) {
//	if (v == 1){
//		return '端口';
//	}
//	if (v == 2){
//		return 'ODF';
//	} 
//}


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
