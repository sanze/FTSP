//指定provider
Ext.state.Manager.setProvider(   
    new Ext.state.SessionStorageStateProvider({   
      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
    })   
);
/******************************树结构加载 开始**********************************/
var treeParams={
		leafType:leafType,
	    checkModel:"multiple"
	    //onlyLeafCheckable:false
	};
var treeurl="../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
	
/**
 * 左侧的树
 */
var westPanel = new Ext.Panel(
{
	id : "westPanel",
	region : "west",
	width : 280,
	height : 800,
	minSize : 230,
//	maxSize : 320,
	autoScroll : true,
	forceFit : true,
	collapsed : false, // initially collapse the group
	collapsible : false,
	collapseMode : 'mini',
	split : true,
	html : '<iframe id="tree_panel" name ="tree_panel" src ="'+treeurl+'"  height="100%" width="100%" frameBorder=0 border=0/>'
});
/*****************************树结构加载 结束**********************************/

/*****************************centerPanel start**********************************/
var limit=200;
var resourceType = 0;
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
//store for grid NE
var storeNe = new Ext.data.Store(
{
	/*url: '',
	baseParams: {"limit":limit},*/
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"resourceId",mapping:"neId"},
	   {name:"emsGroupName",mapping:"emsGroupDisplayName"},
	   {name:"emsName",mapping:"emsDisplayName"},
	   {name:"neOriginalName",mapping:"nativeEmsName"},
	   {name:"neStandardName",mapping:"userLabel"},
	   {name:"displayMode",mapping:"neDisplayMode"},
	   {name:"region",mapping:"areaName"},
	   {name:"station",mapping:"station"},
	   {name:"factory",mapping:"factory"},
	   {name:"neModel",mapping:"productName"},
	   {name:"neVersion",mapping:"neVersion"},
	   {name:"manageCategory",mapping:"manageCategory"},
	   {name:"note",mapping:"note"}
    ])
});

//grid部分数据定义
var cmNe = new Ext.grid.ColumnModel({
    // specify any defaults for each column
	stateId:'neResourceId',
	stateful : true, 
	defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer(),
        checkboxSelectionModel ,
        {
            id: 'resourceId',
            name:'resourceId',
            header: 'id',
            dataIndex: 'resourceId',
            hidden:true
        },
        {
	        id:'emsGroupName',
	        header   : '网管分组', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsGroupName'
	     },
	     {
	        id:'emsName',
	        header   : '网管', 
	        width    : (10*15), 
	        sortable : true, 
	        dataIndex: 'emsName'
	     },
	     {
	        id:'neOriginalName',
	        header   : '网元原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neOriginalName'
	     },
	     {
	        id:'neStandardName',
	        header   : '网元自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neStandardName'
	     },
	     {
	        id:'displayMode',
	        header   : '显示方式', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'displayMode'
	        /*editor: new Ext.form.TextField({  
                allowBlank: false  
            })*/  
	     },
	     {
	        id:'region',
	        header   : top.FieldNameDefine.AREA_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'region'
	     },
	     {
	        id:'station',
	        header   : top.FieldNameDefine.STATION_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'station'
	     },
	     {
	        id:'factory',
	        header   : '厂家', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'factory'
	     },
	     {
	        id:'neModel',
	        header   : '网元型号', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neModel'
	     },
	     {
	        id:'neVersion',
	        header   : '网元版本', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neVersion'
	     },
	     {
	        id:'manageCategory',
	        header   : '管理类别', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'manageCategory'
	     },
	     {
	        id:'note',
	        header   : '备注', 
	        width    : (15*12+10), 
	        sortable : true, 
	        dataIndex: 'note'
	     }	
    	]
});

//store for grid Rack
var storeRack = new Ext.data.Store(
{
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"resourceId",mapping:"shelfId"},
	   {name:"emsGroupName",mapping:"emsGroupDisplayName"},
	   {name:"emsName",mapping:"emsDisplayName"},
	   {name:"neOriginalName",mapping:"neNativeEmsName"},
	   {name:"neStandardName",mapping:"neUserLabel"},
	   {name:"region",mapping:"areaName"},
	   {name:"station",mapping:"station"},
	   {name:"factory",mapping:"factory"},
	   {name:"neModel",mapping:"productName"},
	   {name:"rackType",mapping:"shelfType"},
	   {name:"shelfOriginalName",mapping:"shelfNativeEmsName"},
	   {name:"shelfStandardName",mapping:"shelfUserLabel"},
	   {name:"displayMode",mapping:"shelfDisplayMode"},
	   {name:"note",mapping:"note"}
    ])
});
var cmRack = new Ext.grid.ColumnModel({
    // specify any defaults for each column
	stateId:'rackResourceId',
	stateful : true, 
	defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer(),
        checkboxSelectionModel ,
        {
            id: 'resourceId',
            name:'resourceId',
            header: 'id',
            dataIndex: 'resourceId',
            hidden:true
        },
        {
	        id:'emsGroupName',
	        header   : '网管分组', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsGroupName'
	     },
	     {
	        id:'emsName',
	        header   : '网管', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsName'
	     },
	     {
	        id:'neOriginalName',
	        header   : '网元原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neOriginalName'
	     },
	     {
	        id:'neStandardName',
	        header   : '网元自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neStandardName'
	     },
	     {
	        id:'region',
	        header   : top.FieldNameDefine.AREA_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'region'
	     },
	     {
	        id:'station',
	        header   : top.FieldNameDefine.STATION_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'station'
	     },
	     {
	        id:'factory',
	        header   : '厂家', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'factory'
	     },
	     {
	        id:'neModel',
	        header   : '网元型号', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neModel'
	     },
	     {
	        id:'rackType',
	        header   : '子架类型', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'rackType'
	     },
	     {
	        id:'shelfOriginalName',
	        header   : '子架原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'shelfOriginalName'
	     },
	     {
	        id:'shelfStandardName',
	        header   : '子架自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'shelfStandardName'
	     },
	     {
	        id:'displayMode',
	        header   : '显示方式', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'displayMode'
	        /*editor: new Ext.form.TextField({  
                allowBlank: false  
            })*/  
	     },
	     {
	        id:'note',
	        header   : '备注', 
	        width    : (15*12+10), 
	        sortable : true, 
	        dataIndex: 'note'
	     }		
    	]
});

//store for grid Unit
var storeUnit = new Ext.data.Store(
{
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"resourceId",mapping:"unitId"},
	   {name:"emsGroupName",mapping:"emsGroupDisplayName"},
	   {name:"emsName",mapping:"emsDisplayName"},
	   {name:"neOriginalName",mapping:"neNativeEmsName"},
	   {name:"neStandardName",mapping:"neUserLabel"},
	   {name:"region",mapping:"areaName"},
	   {name:"station",mapping:"station"},
	   {name:"factory",mapping:"factory"},
	   {name:"neModel",mapping:"productName"},
	   {name:"slot",mapping:"slotNo"},
	   {name:"unitVersion",mapping:"unitVersion"},
	   {name:"unitProductionDate",mapping:"manufacture"},
	   {name:"unitNum",mapping:"partNum"},
	   {name:"unitSequence",mapping:"serialNum"},
	   {name:"serviceState",mapping:"serviceState"},
	   {name:"unitOriginalName",mapping:"unitNativeEmsName"},
	   {name:"unitStandardName",mapping:"unitUserLabel"},
	   {name:"displayMode",mapping:"unitDisplayMode"},
	   {name:"note",mapping:"note"}
    ])
});
var cmUnit = new Ext.grid.ColumnModel({
    // specify any defaults for each column
	stateId:'unitResourceId',
	stateful : true, 
	
	defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer(),
        checkboxSelectionModel ,
        {
            id: 'resourceId',
            name:'resourceId',
            header: 'id',
            dataIndex: 'resourceId',
            hidden:true
        },
        {
	        id:'emsGroupName',
	        header   : '网管分组', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsGroupName'
	     },
	     {
	        id:'emsName',
	        header   : '网管', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsName'
	     },
	     {
	        id:'neOriginalName',
	        header   : '网元原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neOriginalName'
	     },
	     {
	        id:'neStandardName',
	        header   : '网元自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neStandardName'
	     },
	     {
	        id:'region',
	        header   : top.FieldNameDefine.AREA_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'region'
	     },
	     {
	        id:'station',
	        header   : top.FieldNameDefine.STATION_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'station'
	     },
	     {
	        id:'factory',
	        header   : '厂家', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'factory'
	     },
	     {
	        id:'neModel',
	        header   : '网元型号', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neModel'
	     },
	     {
	        id:'slot',
	        header   : '槽道', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'slot'
	     },
	     {
	        id:'unitVersion',
	        header   : '板卡版本', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'unitVersion'
	     },
	     {
	        id:'unitProductionDate',
	        header   : '板卡生产日期', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'unitProductionDate'
	     },
	     {
	        id:'unitNum',
	        header   : '板卡图号', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'unitNum'
	     },
	     {
	        id:'unitSequence',
	        header   : '板卡序列号', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'unitSequence'
	     },
	     {
	        id:'serviceState',
	        header   : '服务状态', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'serviceState'
	     },
	     {
	        id:'unitOriginalName',
	        header   : '板卡原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'unitOriginalName'
	     },
	     {
	        id:'unitStandardName',
	        header   : '板卡自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'unitStandardName'
	     },
	     {
	        id:'displayMode',
	        header   : '显示方式', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'displayMode'
	        /*editor: new Ext.form.TextField({  
                allowBlank: false  
            })*/  
	     },
	     {
	        id:'note',
	        header   : '备注', 
	        width    : (15*12+10), 
	        sortable : true, 
	        dataIndex: 'note'
	     }		
    	]
});

//store for grid Port
var storePort = new Ext.data.Store(
{
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"resourceId",mapping:"ptpId"},
	   {name:"emsGroupName",mapping:"emsGroupDisplayName"},
	   {name:"emsName",mapping:"emsDisplayName"},
	   {name:"neOriginalName",mapping:"neNativeEmsName"},
	   {name:"neStandardName",mapping:"neUserLabel"},
	   {name:"region",mapping:"areaName"},
	   {name:"station",mapping:"station"},
	   {name:"factory",mapping:"factory"},
	   {name:"neModel",mapping:"productName"},
	   {name:"port",mapping:"portNo"},
	   {name:"businessType",mapping:"domain"},
	   {name:"portType",mapping:"ptpType"},
	   {name:"attenuator",mapping:"att"},
	   {name:"opticalModule",mapping:"optModel"},
	   {name:"distributionFrame",mapping:"ddfPdf"},
	   {name:"ptpOriginalName",mapping:"ptpNativeEmsName"},
	   {name:"ptpStandardName",mapping:"ptpUserLabel"},
	   {name:"displayMode",mapping:"ptpDisplayMode"},
	   {name:"note",mapping:"note"}
    ])
});
var cmPort = new Ext.grid.ColumnModel({
    // specify any defaults for each column
	stateId:'portResourceId',
	stateful : true, 
	defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer(),
        checkboxSelectionModel ,
        {
            id: 'resourceId',
            name:'resourceId',
            header: 'id',
            dataIndex: 'resourceId',
            hidden:true
        },
        {
	        id:'emsGroupName',
	        header   : '网管分组', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsGroupName'
	     },
	     {
	        id:'emsName',
	        header   : '网管', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'emsName'
	     },
	     {
	        id:'neOriginalName',
	        header   : '网元原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neOriginalName'
	     },
	     {
	        id:'neStandardName',
	        header   : '网元自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neStandardName'
	     },
	     {
	        id:'region',
	        header   : top.FieldNameDefine.AREA_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'region'
	     },
	     {
	        id:'station',
	        header   : top.FieldNameDefine.STATION_NAME, 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'station'
	     },
	     {
	        id:'factory',
	        header   : '厂家', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'factory'
	     },
	     {
	        id:'neModel',
	        header   : '网元型号', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'neModel'
	     },
	     {
	        id:'port',
	        header   : '端口', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'port'
	     },
	     {
	        id:'businessType',
	        header   : '业务类型', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'businessType'
	     },
	     {
	        id:'portType',
	        header   : '端口类型', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'portType'
	     },
	     {
	        id:'attenuator',
	        header   : '衰耗器', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'attenuator'
	     },
	     {
	        id:'opticalModule',
	        header   : '光模块', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'opticalModule'
	     },
	     {
	        id:'distributionFrame',
	        header   : 'ODF/DDF', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'distributionFrame'
	     },
	     {
	        id:'ptpOriginalName',
	        header   : '端口原始名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'ptpOriginalName'
	     },
	     {
	        id:'ptpStandardName',
	        header   : '端口自定义名称', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'ptpStandardName'
	     },
	     {
	        id:'displayMode',
	        header   : '显示方式', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'displayMode'
	        /*editor: new Ext.form.TextField({  
                allowBlank: false  
            })*/  
	     },
	     {
	        id:'note',
	        header   : '备注', 
	        width    : (15*12+10), 
	        sortable : true, 
	        dataIndex: 'note'
	     }		
    	]
});

var store = storeNe;
var cm = cmNe;

//store for combo
var myData1 = [
   ['1','网元'],
   ['2','子架'],
   ['3','板卡'],
   ['4','端口']
];
           
var resourceTypeStore=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'name'}
   	]
});
resourceTypeStore.loadData(myData1);

//toolBar
var toolPanel=[
    {
		xtype:'combo',
		id:'resourceTP',	
		name:'resourceTP',
		emptyText: '网元...',
		width:150,
		style: 'margin:0px 0px 0px 20px;',
		store:resourceTypeStore,
		valueField:'value',
		displayField:'name',
		triggerAction: 'all',
		mode:'local',
		listeners:{
			select:function(combo,record,index){
				if(index == 0){
					gridPanel.reconfigure(storeNe, new Ext.grid.ColumnModel(cmNe));
					pageTool.bind(storeNe);
					resourceType = 0;
					storeNe.removeAll();
					store = storeNe;
				}else if(index == 1){
					gridPanel.reconfigure(storeRack, new Ext.grid.ColumnModel(cmRack));
					pageTool.bind(storeRack);
					resourceType = 1;
					storeRack.removeAll();
					store = storeRack;
				}else if(index == 2){
					gridPanel.reconfigure(storeUnit, new Ext.grid.ColumnModel(cmUnit));
					pageTool.bind(storeUnit);
					resourceType = 2;
					storeUnit.removeAll();
					store = storeUnit;
				}else if(index == 3){
					gridPanel.reconfigure(storePort, new Ext.grid.ColumnModel(cmPort));
					pageTool.bind(storePort);
					resourceType = 3;
					storePort.removeAll();
					store = storePort;
				}
			}
		}
    },'-',
    {
    	id:'search',
    	text:'查询',
        icon:'../../resource/images/btnImages/search.png',
        privilege:viewAuth,
		style: 'margin:0px 10px 0px 30px;',
		handler:function(){
			search(resourceType);
		}
    },'-',
    {
    	xtype: 'splitbutton',
		text:'显示方式',
        icon:'../../resource/images/btnImages/report.png',
        privilege:modAuth,
		style: 'margin:0px 10px 0px 10px;',
		handler:function(b,e){
			modifyDisplayMode(1,resourceType);
		},
		menu:{
			style: {
				overflow: 'visible'     // For the Combo popup
			},
			items: [
			{
				text: '原始名称',
				privilege:modAuth,
				handler: function(b,e){
					modifyDisplayMode(1,resourceType);
				}
			}, {
				text: '自定义名称',
				privilege:modAuth,
				handler: function(b,e){
					modifyDisplayMode(2,resourceType);
				}
			}
			]
		}
    },'-',
    {
    	id:'modify',
    	text:'修改',
        icon:'../../resource/images/btnImages/modify.png',
        privilege:modAuth,
		style: 'margin:0px 10px 0px 10px;',
		handler:function(){
			modify(resourceType);
		}
    },'-',
    {
    	id:'export',
    	text:'导出',
        icon:'../../resource/images/btnImages/export.png',
        privilege:viewAuth,
		style: 'margin:0px 10px 0px 10px;',
		handler:function(){
			exportResourceStock(resourceType);
		}
    }
];



var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: limit,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
});

var gridPanel = new Ext.grid.GridPanel({
	id:"gridPanel",
	region:"center",
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    //loadMask: {msg: '数据加载中...'},
    selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox  
	view: new Ext.grid.GridView({
        forceFit:false
    }),
    stateId:'netResourceId', 
    stateful:true,
	bbar: pageTool,
	tbar: toolPanel/*,
	plugins: [rightMenu]*/
});

var neWin;

/*****************************centerPanel end**********************************/
/*****************************方法函数 开始**********************************/
function search(resourceType){	
	var iframe = window.frames["tree_panel"];
	
	// 兼容不同浏览器的取值方式
	if (iframe.getCheckedNodes) {
		result = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "text",
				"path:text" ]);
	} else {
		result = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel",
				"text", "path:text" ]);
	}
	/*for (var i = 0; i < result.length; i++) {
		alert(result[i].nodeId+"::"+result[i].nodeLevel+"::"+result[i].text);
	}*/
	
	if(result.length == 0){
		Ext.Msg.alert("提示","请选择网管、子网或网元！");
		return;
	}
	
	var nodes = new Array();
	for ( var i = 0; i < result.length; i++) {
		nodes.push(Ext.encode(result[i]));
	}
	
	gridPanel.getEl().mask("正在查询...");
	store.proxy = new Ext.data.HttpProxy({url:"resource-stock!search.action"});
	store.baseParams = {
			"resourceType":resourceType,
			"treeNodes":nodes,
			"limit":limit
	}

	store.load({
		callback: function(response, success){
			gridPanel.getEl().unmask();
			if(!success){
				var obj = Ext.decode(response.responseText);
	    		Ext.Msg.alert("提示",obj.returnMessage);
			}
		}
	});
}

//修改显示方式
function modifyDisplayMode(displayModeType,resourceType){
	var selectRecord =gridPanel.getSelectionModel().getSelections();
	
	if(selectRecord.length == 0){
		Ext.Msg.alert("提示","必须选择一或多个资源。");
		return;
	}else{
		//gridPanel.getEl().mask("正在修改显示方式...");
		var neIdList = new Array();
		for(var i = 0; i< selectRecord.length;i++){
			neIdList.push(selectRecord[i].get("resourceId"));
		}
	}
	Ext.Ajax.request({
		url:'resource-stock!changeDisplayMode.action',
		method:'Post',
		params:{"displayModeType" : displayModeType,
				"neIdList" : neIdList,
				"resourceType":resourceType},
		success:function(response){
			//gridPanel.getEl().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("提示",obj.returnMessage);
			store.reload();
		},
		error:function(response){
			//gridPanel.getEl().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("错误",obj.returnMessage);
		},
		failure:function(response){
			//gridPanel.getEl().unmask();
			var obj = Ext.decode(response.responseText);
			Ext.Msg.alert("失败",obj.returnMessage);
		}
	});
}

function modify(resourceType){
	
	var selectRecord =gridPanel.getSelectionModel().getSelections();
	if(selectRecord.length == 0){
		Ext.Msg.alert("提示","请选择资源！");
		return;
	}else if(selectRecord.length > 1){
		Ext.Msg.alert("提示","只能选择一个资源修改。");
		return;
	}else{
		//实时创建修改网元、子架、板卡和端口的窗口资源，并赋值
		var originalNeName = new Ext.form.TextField({
			id : 'originalNeName',
		    name: 'originalNeName',
		    fieldLabel: '网元原始名称',
		    anchor:'100%',
		    layout:'fit',
		    disabled:true
		});
		
		//显示方式
		var displayModestore = new Ext.data.ArrayStore({
			id:'displayModestore',
			fields : ['value','displayName'],
			data : [['1','原始名称'],['2','自定义名称']] 
		})
		var displayModeCombo = new Ext.form.ComboBox({
			id : 'displayModeCombo',
		    name: 'displayModeCombo',
		    hiddenName:'modeCombo',
		    fieldLabel: '显示方式',
		    valueField : 'value',
			displayField : 'displayName',
			anchor:'100%',
			layout:'fit',
		    store: displayModestore,
		    mode: 'local',
		    triggerAction : 'all'
		})

		//管理类别
		var manageCatogorystore = new Ext.data.ArrayStore({
			id:'manageCatogorystore',
			fields : ['value','displayName'],
			data : [['1','维护'],['2','工程'],['3','退网']] 
		})
		var manageCatogoryCombo = new Ext.form.ComboBox({
			id : 'manageCatogoryCombo',
		    name: 'manageCatogoryCombo',    
		    fieldLabel: '管理类别',
		    valueField : 'value',
			displayField : 'displayName',
			anchor:'100%',
			layout:'fit',
		    store: manageCatogorystore,
		    mode: 'local',
		    triggerAction : 'all'
		})

		//备注
		var noteText = new Ext.form.TextArea({
			id : 'noteText',
		    name: 'noteText',
		    fieldLabel: '备注',
		    anchor:'100%'
		});
				
		record = selectRecord[0];
		var ItemsList;
		var titleName;
		Ext.getCmp('originalNeName').value = record.get('neOriginalName');
		//Ext.getCmp('displayModeCombo').value = record.get('displayMode');
		Ext.getCmp('noteText').value = record.get('note');
		if(record.get('displayMode') == "原始名称"){
			Ext.getCmp('displayModeCombo').setValue(1);
		}else{
			Ext.getCmp('displayModeCombo').setValue(2);
		}

		if(resourceType == 0){	
			var standardNeName = new Ext.form.TextField({
				id : 'standardNeName',
			    name: 'standardNeName',
			    fieldLabel: '网元自定义名称',
			    anchor:'100%'
			});
			Ext.getCmp('standardNeName').value = record.get('neStandardName');
			Ext.getCmp('manageCatogoryCombo').value = record.get('manageCategory');
			if(record.get('manageCategory') == '维护'){
				Ext.getCmp('manageCatogoryCombo').setValue(1);
			}else if(record.get('manageCategory') == '工程'){
				Ext.getCmp('manageCatogoryCombo').setValue(2);
			}else if(record.get('manageCategory') == '退网'){
				Ext.getCmp('manageCatogoryCombo').setValue(3);
			}

			ItemsList = [originalNeName,standardNeName,displayModeCombo,manageCatogoryCombo,noteText];
			//alert(Ext.getCmp('standardNeName').value);
			titleName = '网元修改';
		}else if(resourceType == 1){
			var originalShelfName = new Ext.form.TextField({
				id : 'originalShelfName',
			    name: 'originalShelfName',
			    fieldLabel: '子架原始名称',
			    anchor:'100%',
			    value: "1",
			    disabled:true
			});
			
			var standardShelfName = new Ext.form.TextField({
				id : 'standardShelfName',
			    name: 'standardShelfName',
			    fieldLabel: '子架自定义名称',
			    anchor:'100%'
			});
			Ext.getCmp('originalShelfName').value = record.get('shelfOriginalName');
			Ext.getCmp('standardShelfName').value = record.get('shelfStandardName');
			ItemsList = [originalNeName,originalShelfName,standardShelfName,displayModeCombo,noteText];
			titleName = '子架修改';
		}else if(resourceType == 2){
			var slot = new Ext.form.TextField({
				id : 'slot',
			    name: 'slot',
			    fieldLabel: '所在槽道',
			    anchor:'100%',
			    value: "1",
			    disabled:true
			});

			var originalUnitName = new Ext.form.TextField({
				id : 'originalUnitName',
			    name: 'originalUnitName',
			    fieldLabel: '板卡原始名称',
			    anchor:'100%',
			    value: "1",
			    disabled:true
			});
			var standardUnitName = new Ext.form.TextField({
				id : 'standardUnitName',
			    name: 'standardUnitName',
			    fieldLabel: '板卡自定义名称',
			    anchor:'100%',
			    value: "1"
			});
			Ext.getCmp('originalUnitName').value = record.get('unitOriginalName');
			Ext.getCmp('standardUnitName').value = record.get('unitStandardName');
			Ext.getCmp('slot').value = record.get('slot');
			ItemsList = [originalNeName,slot,originalUnitName,standardUnitName,displayModeCombo,noteText];
			titleName = '板卡修改';
		}else if(resourceType == 3){
			var port = new Ext.form.TextField({
				id : 'port',
			    name: 'port',
			    fieldLabel: '端口',
			    anchor:'100%',
			    value: "1",
			    disabled:true
			});

			var originalPtpName = new Ext.form.TextField({
				id : 'originalPtpName',
			    name: 'originalPtpName',
			    fieldLabel: '端口原始名称',
			    anchor:'100%',
			    value: "1",
			    disabled:true
			});
			var standardPtpName = new Ext.form.TextField({
				id : 'standardPtpName',
			    name: 'standardPtpName',
			    fieldLabel: '端口自定义名称',
			    anchor:'100%',
			    value: "1"
			});
			Ext.getCmp('originalPtpName').value = record.get('ptpOriginalName');
			Ext.getCmp('standardPtpName').value = record.get('ptpStandardName');
			Ext.getCmp('port').value = record.get('port');
			ItemsList = [originalNeName,port,originalPtpName,standardPtpName,displayModeCombo,noteText];
			titleName = '端口修改';
		}
		// 如果窗口存在，显示  
	    /*win = Ext.getCmp("neWin");  
	    if (win) { 
	    	win.removeAll();
	    	win.add(originalUnitName);
	    	win.add(standardShelfName);
	    	win.doLayout();
	    	win.show();
	    }else{*/
    	//修改资源窗口
		var modifyPanel = new Ext.FormPanel({
			id:'modifyPanel',
			name:'modifyPanel',
			layout:'form',
			border : false,
			frame : false,
			autoScroll : true,
			bodyStyle : 'padding:10px 10px 10px 10px;',
			items: ItemsList
			
		})
		
		neWin = new Ext.Window({
		    id:'neWin',
			title: titleName,
		   // height: auto,
		    width: 300,
		    layout: 'fit',
		    closeAction:'close',
		    items:[modifyPanel],
		    buttons: [{
				id:'ok',
			    text: '确定',
			    handler:function(){
			    	var selectRecord =gridPanel.getSelectionModel().getSelections();
			    	var resourceId = selectRecord[0].get('resourceId');
			    	saveChangedInfo(resourceType,resourceId);
			    }
			 },
			 {
			    text: '取消',
			    handler:function(){
			    	neWin.close();
			    }
			}]
		})
		//neWin.render();
		neWin.show();
    }
	//}		
}

function saveChangedInfo(resourceType,resourceId){
	var noteText = Ext.getCmp('noteText').getValue();
	
	
	if(noteText.length > 32){
		Ext.Msg.alert("提示","最长32个中文字符！");
		return;
	}
	
	if(resourceType == 0){
		var standardNeName = Ext.getCmp('standardNeName').getValue();
		var param = {
				"resourceType" : resourceType,
				"resourceId" : resourceId,
				"standardName":standardNeName,
				"displayMode" :Ext.getCmp('displayModeCombo').getValue(),
				"manageCategory":Ext.getCmp('manageCatogoryCombo').getValue(),
				"note":noteText
		}
		
		Ext.Ajax.request({
			url:'resource-stock!checkNeNameExit.action',
			method:'Post',
			params:{"resourceId" : resourceId,
					"standardName":standardNeName},
			success:function(response){
				var obj = Ext.decode(response.responseText);
				if(obj.returnResult == 0){
					Ext.Ajax.request({
						url:'resource-stock!saveChangedInfo.action',
						method:'Post',
						params:param,
						success:function(response){
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("提示",obj.returnMessage,function(){neWin.close();});
							store.reload();
						},
						error:function(response){
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("错误",obj.returnMessage);
						},
						failure:function(response){
							var obj = Ext.decode(response.responseText);
							Ext.Msg.alert("失败",obj.returnMessage);
						}
					})
				}else{
					Ext.Msg.alert("提示","网元自定义名称已经存在，请修改！");
					return;
				}				
			},
			error:function(response){
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误",obj.returnMessage);
				return;
			},
			failure:function(response){
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("失败",obj.returnMessage);
				return;
			}
		})
	
	}else{
		if(resourceType == 1){
			var param = {
					"resourceType" : resourceType,
					"resourceId" : resourceId,
					"standardName":Ext.getCmp('standardShelfName').getValue(),
					"displayMode" :Ext.getCmp('displayModeCombo').getValue(),
					"note":noteText
			}
		}else if(resourceType == 2){
			var param = {
					"resourceType" : resourceType,
					"resourceId" : resourceId,
					"standardName":Ext.getCmp('standardUnitName').getValue(),
					"displayMode" :Ext.getCmp('displayModeCombo').getValue(),
					"note":noteText
			}
		}else if(resourceType == 3){
			var param = {
					"resourceType" : resourceType,
					"resourceId" : resourceId,
					"standardName":Ext.getCmp('standardPtpName').getValue(),
					"displayMode" :Ext.getCmp('displayModeCombo').getValue(),
					"note":noteText
			}
		}
		
		Ext.Ajax.request({
			url:'resource-stock!saveChangedInfo.action',
			method:'Post',
			params:param,
			success:function(response){
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("提示",obj.returnMessage,function(){neWin.close();});
				store.reload();
			},
			error:function(response){
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误",obj.returnMessage);
			},
			failure:function(response){
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("失败",obj.returnMessage);
			}
		})
	} 
}

function exportResourceStock(resourceType){
	if(store.getCount() == 0){
		Ext.Msg.alert("提示","导出结果为空！");
		return;
	}else{
		gridPanel.getEl().mask("正在导出...");
		var iframe = window.frames["tree_panel"];
		// 兼容不同浏览器的取值方式
		if (iframe.getCheckedNodes) {
			result = iframe.getCheckedNodes([ "nodeId", "nodeLevel", "text",
					"path:text" ]);
		} else {
			result = iframe.contentWindow.getCheckedNodes([ "nodeId", "nodeLevel",
					"text", "path:text" ]);
		}
		var nodes = new Array();
		for ( var i = 0; i < result.length; i++) {
			nodes.push(Ext.encode(result[i]));
		}
		Ext.Ajax.request({
			url:'resource-stock!exportResourceStock.action',
			type:"Post",
			params:{"resourceType":resourceType,
					"treeNodes":nodes},
			success:function(response){
				gridPanel.getEl().unmask();
				var obj = Ext.decode(response.responseText);
				if(obj.returnResult == 1 && obj.returnMessage != ""){
					window.location.href = "download!execute.action?"
						+ Ext.urlEncode({"filePath" : obj.returnMessage});
				}else{
					Ext.Msg.alert("提示", "导出失败！");
				}
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误",obj.returnMessage);
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("失败",obj.returnMessage);
			}
		});
	}
}

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	Ext.Ajax.timeout=900000; 
	var win = new Ext.Viewport({
        id:'win',
		layout : 'border',
		items : [westPanel,gridPanel],
		renderTo : Ext.getBody()
	});
})