/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
var store = new Ext.data.Store({
			url : 'resource-circuit!resultCount.action',
			baseParams : {
				"limit" : 200
			},
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["re_name", "num", "perc"])
		});
store.load();
// ==========================page=============================
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
			// specify any defaults for each column
			defaults : {
				sortable : true
				// columns are not sortable by default
			},
			columns : [new Ext.grid.RowNumberer({
				width : 26
			}), checkboxSelectionModel, {
						id : 're_name',
						header : '名称',
						dataIndex : 're_name',
						hidden : false,// hidden colunm
						width : 100
					}, {
						id : 'num',
						header : '数量',
						dataIndex : 'num',
						hidden : false,// hidden colunm
						width : 100
					}, {
						id : 'perc',
						header : '百分比',
						dataIndex : 'perc',
						hidden : false,// hidden colunm
						width : 100
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

var gridPanel = new Ext.grid.EditorGridPanel({
			id : "gridPanel",
			region : "center",
			// title:'用户管理',
			cm : cm,
			store : store,
			// autoExpandColumn: 'roleName', // column with this id will be
			// expanded
			stripeRows : true, // 交替行效果
			loadMask : true,
			selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
			// view: new Ext.ux.grid.LockingGridView(),
			forceFit : true,
			bbar : pageTool
		});

function initData() {

}

Ext.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			}
			Ext.Msg = top.Ext.Msg;

			var win = new Ext.Viewport({
						id : 'win',
						layout : 'border',
						items : [gridPanel],
						renderTo : Ext.getBody()
					});
		});