//局站类型
var typeMapping = [ [ 1, '火电站' ], [ 2, '市县局' ], [ 3, '500KV变电站' ], [ 4, '220KV变电站' ],
		[ 5, '110KV变电站' ], [ 6, '35KV变电站及供电所' ], [ 7, '110KV集控站' ] ];
function typeRenderer(v, m, r) {
	return (typeof v == 'number' && typeMapping[v - 1] != null) ? typeMapping[v - 1][1] : v;
}
var typeStore = new Ext.data.ArrayStore({
	fields : [{
		name : 'value' 
	}, {
		name : 'displayName'
	}]
});
typeStore.loadData(typeMapping);

//局站详细信息的Store
var store = new Ext.data.Store({
	url : 'external-connect!getStationList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["stationId","stationName", "stationNo","areaName","stationType","longitude",
	    "latitude","address", "management", "phone", "note" ])
});

//局站详细信息grid列
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true,
	header : ""
});

var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		width:90
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel,{
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME+'名称',
		dataIndex : 'stationName'
	},  {
		id : 'stationNo',
		header : top.FieldNameDefine.STATION_NAME+'代号',
		dataIndex : 'stationNo'
	}, {
		id : 'area',
		header : '所属'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName',
		width:130
	}, {
		id : 'stationType',
		header : top.FieldNameDefine.STATION_NAME+'类型',
		dataIndex : 'stationType',
		renderer:typeRenderer
	}, {
		id : 'longitude',
		header : '经度',
		dataIndex : 'longitude'
	},{
		id : 'latitude',
		header : '纬度',
		dataIndex : 'latitude'
	}, {
		id : 'address',
		header : '地址',
		dataIndex : 'address',
		width:130
	}, {
		id : 'management',
		header : '联系人',
		dataIndex : 'management'
	}, {
		id : 'phone',
		header : '电话',
		dataIndex : 'phone',
		width:130
	}, {
		id : 'note',
		header : '备注',
		dataIndex : 'note',
		width:150
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : page_size,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
}); 

//区域树    
var areaTree = new Ext.form.TextField({
	id : 'areaField', 
	readOnly : true,
	emptyText:'选择区域',
	width: 110,
	listeners : {
		'focus' : function(field){ 
			getTree(this,10);
		}
	}
});  

//局站名称
var stationName={
	xtype: 'textfield',
	id:'statName',   
	emptyText:'模糊搜索',
	width: 110
};

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm, 
	loadMask:true, 
	stripeRows : true, // 交替行效果 
	forceFit : true,
	selModel : checkboxSelectionModel,   
	bbar : pageTool,
	tbar : ['-',top.FieldNameDefine.AREA_NAME+'：',areaTree,'-',ifSubArea,'-',
	        top.FieldNameDefine.STATION_NAME+'名：',stationName,'-',{
		xtype : 'button', 
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		handler :function(){
			showAll(store,Ext.getCmp('statName').getValue());
		}
	},'-', {
		xtype : 'button', 
		text : '外部光纤连接详情',
//		privilege:viewAuth,
		handler:function (){
			externalConnectDetail();
		}
	}]
});

gridPanel.on('rowdblclick', function(grid, rowIndex, e){
	grid.getSelectionModel().selectRow(rowIndex);
	externalConnectDetail();
});

function externalConnectDetail() {
	var count = gridPanel.getSelectionModel().getCount();
	if (count==0) {
		Ext.Msg.alert("提示","请选择一行局站数据！");
		return;
	} else {
		var stationId = gridPanel.getSelectionModel().getSelected().get("stationId");	
		var href = window.location.protocol+"//"+window.location.host+"/FTTS/jsp/tl/tl.jsp?stationId="+stationId;
		parent.parent.window.addTabPage(href, "外部光纤连接详情","",false);
	}

}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif"; 
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
	showAll(store,Ext.getCmp('statName').getValue());
});
