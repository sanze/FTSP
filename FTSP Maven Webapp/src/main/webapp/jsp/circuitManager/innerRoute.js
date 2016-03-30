var innerRouteCm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [{
		id : 'inner_Route_neName',
		header : '网元',
		sortable : false,
		dataIndex : 'inner_NE_NAME'
	}, {
		id : 'inner_route_port',
		header : '端口w',
		sortable : false,
		dataIndex : 'inner_PORT'
	}, {
		id : 'inner_route_ctp',
		header : '时隙',
		sortable : false,
		dataIndex : 'inner_CTP'
	}, {
		id : 'inner_route_port_p',
		header : '端口p',
		sortable : false,
		dataIndex : 'inner_PORT_p',
		resizable : true
	},{
		id:'inner_route_ctp_p',
		header:'时隙',
		sortable:false,
		dataIndex:'inner_CTP_p'
	} ]
});
var innerRouteStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "inner_NE_NAME", "inner_PORT", "inner_CTP", "inner_PORT_p","inner_CTP_p"])
});
var innerRouteGrid = new Ext.grid.GridPanel({
	id : 'innerRouteGrid',
	region : "center",
	store : innerRouteStore,
	cm : innerRouteCm,
	animCollapse : true,
	autoScroll : true,
	frame : false,
	border : false
});
var innerRouteWindow = new Ext.Window({
	id : 'innerRouteWindow',
	title : '网元内部电路路由表',
	width:500,
	height:600,
	closeAction : 'hide',
	modal:true,
	constrain:true,
	layout:'fit',
	items:[innerRouteGrid]
});

innerRouteStore.proxy = new Ext.data.HttpProxy({
	url : 'circuit!getInnerRoute.action'
});

