//添加复用段
function addWaveDir() {
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓复用段信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	var storeWaveDir = new Ext.data.Store({
		url : 'nx-report!searchWaveDir.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "emsGroup", "ems", "ne", "subnet",
				"factory", "neType", 'networkName',"unit","waveDirId",
				"waveDir","stdWaveNum","actWaveNum","station","neId" ])
	});
	
	var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	var cm = new Ext.grid.ColumnModel({
		defaults : {
			sortable : true
		},
		columns : [ new Ext.grid.RowNumberer({
			width:26,
			}), checkboxSelectionModel, {
			id : 'emsGroup',
			header : '网管分组',
			dataIndex : 'emsGroup',
			width : 100
		}, {
			id : 'ems',
			header : '网管',
			dataIndex : 'ems',
			width : 100
		}, {
			id : 'subnet',
			header : '子网',
			dataIndex : 'subnet',
			width : 100
		}, {
			id : 'ne',
			header : '网元名称',
			dataIndex : 'ne',
			width : 150
		}, {
			id : 'factory',
			header : '设备厂家',
			dataIndex : 'factory',
			width : 100,
			renderer : factoryRenderer
		}, {
			id : 'neType',
			header : '网元型号',
			dataIndex : 'neType',
			width : 150
		}, {
			id : 'unit',
			header : '单位',
			dataIndex : 'unit',
			width : 100
		}, {
			id : 'station',
			header : '站名',
			dataIndex : 'station',
			width : 100
		}, {
			id : 'networkName',
			header : '网络名称',
			dataIndex : 'networkName',
			width : 100
		}, {
			id : 'waveDir',
			header : '方向',
			dataIndex : 'waveDir',
			width : 100
		} , {
			id : 'stdWaveNum',
			header : '容量（波数）',
			dataIndex : 'stdWaveNum',
			width : 100
		} , {
			id : 'actWaveNum',
			header : '实开（波数）',
			dataIndex : 'actWaveNum',
			width : 100
		}  ]
	});
	// ----------------------------------------------------------------
	var gridPanel = new Ext.grid.GridPanel({
		id : 'gridPanel',
		cm : cm,
//		height : 500,
		title : '波分方向信息',
		store : storeWaveDir,
		border:false,
		region : 'center',
		selModel : checkboxSelectionModel,
//		viewConfig:{forceFit:true},
		stripeRows : true,
		tbar : [  '-',{
			text : '查询',
			icon : '../../../resource/images/btnImages/search.png',
			handler : searchWaveDir
		} ]
	});
	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑方向信息结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	var westPanel = new Ext.ux.EquipTreePanel({
		xtype : 'equiptree',
		rootVisible : false,
		title : "",
		region : "west",
		width : 250,
		split : true,
		collapseMode : 'mini',
		autoScroll:true,
		boxMinWidth : 250,
		boxMinHeight : 260,
		leafType:CommonDefine.TREE.NODE.NE,
		checkableLevel : [ 2, 3, 4 ]
	});
	//***********************************************************************
	var all = new Ext.Panel({
		id:'all',
		border:false,
		layout:"border",
		boxMinHeight:420,
		boxMinWidth:1000,
		items:[westPanel,gridPanel]
	});
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓functions↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	/**
	 * 查询
	 */
	function searchWaveDir(){
		var result=westPanel.getCheckedNodes(["nodeId","nodeLevel","text","path:text","emsId"]);
		if(!result || result.length==0){
			Ext.Msg.alert("提示","请先选取查询范围！");
			return;
		}
		var nodeList = new Array();
		for(var i=0;i<result.length;i++){
			nodeList.push(Ext.encode({nodeId:result[i]["nodeId"],nodeLevel:result[i]["nodeLevel"]}));
		}
		storeWaveDir.baseParams = {modifyList:nodeList};
		storeWaveDir.load({
			callback : function(records,options,success){
				// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
				if (!success) {
					Ext.Msg.alert('错误', '查询失败！请重新查询');
				}else{
				}
			}
		});
	}
	// 添加到已选
	function add(close) {
		var selected = gridPanel.getSelectionModel().getSelections();
		var records = new Array();
		if (selected && selected.length > 0) {
			for ( var i = 0; i < selected.length; i++) {
				var recordIndex = store.findBy(function(rec, id) {
					if (rec.get('waveDirId') == selected[i].get('waveDirId')) {
						return true;
					}
				});
				if (recordIndex == -1) {
					records.push(selected[i]);
				}
			}
		}
		store.add(records);
		if (close == 1)
			win.close();
		
	}
	//----------------------------------------------------------------------------
	var win = new Ext.Window({
		id : 'addTaskWin',
		title : '添加波分方向',
		layout : 'fit',
		width : Ext.getBody().getWidth()*0.8,      
		height : Ext.getBody().getHeight()-80, 
		autoScroll : true,
		items : [ all ],
		buttons : [ {
			text : '应用', 
			handler : function() {
				add(0);
			}
		}, {
			text : '确定', 
			handler : function() {
				add(1);
			}
		}, {
			text : '取消', 
			handler : function() {
				win.close();
			}
		} ]
	});
	win.show();
	if (win.getHeight() > Ext.getCmp('win').getHeight()) {
		win.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		gridPanel.setHeight(win.getInnerHeight());
	}
	if (win.getWidth() > Ext.getCmp('win').getWidth()) {
		win.setWidth(Ext.getCmp('win').getWidth() * 0.7);
	} else {
		gridPanel.setWidth(win.getInnerWidth());
	}
	win.center();
	win.doLayout();
}