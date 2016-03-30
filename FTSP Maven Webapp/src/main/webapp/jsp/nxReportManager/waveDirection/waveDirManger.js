/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// *************************grid****************************
var store = new Ext.data.Store({
	url : 'nx-report!searchWaveDir.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "emsGroup", "ems", "ne", "subnet",
			"factory", "neType", 'networkName',"unit","waveDirId",
			"waveDir","stdWaveNum","actWaveNum","station","neId" ])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width:26,
		}), checkboxSelectionModel, {
		id : 'emsGroup',
		header : '网管分组',
		dataIndex : 'emsGroup',
		width : 130
	}, {
		id : 'ems',
		header : '网管',
		dataIndex : 'ems',
		width : 130
	}, {
		id : 'subnet',
		header : '子网',
		dataIndex : 'subnet',
		width : 120
	}, {
		id : 'ne',
		header : '网元名称',
		dataIndex : 'ne',
		width : 180
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
		width : 140
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
		width : 120
	}, {
		id : 'waveDir',
		header : '方向',
		dataIndex : 'waveDir',
		width : 120
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

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	loadMask : true,
	autoScroll:true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	frame : false,
	stripeRows : true,
//	viewConfig:{forceFit:true},
	tbar : ['-',{
		text : '查询',
		privilege:viewAuth, 
		icon : '../../../resource/images/btnImages/search.png',
		 handler : searchWaveDir
	}, "-", {
		text : '新增',
		privilege:addAuth, 
		icon : '../../../resource/images/btnImages/add.png',
		handler : addWaveDir
	},'-', {
		text : '删除',
		privilege:delAuth, 
		icon : '../../../resource/images/btnImages/delete.png',
		handler : deleteWaveDir
	},'-', {
		text : '修改',
		privilege:modAuth, 
		icon : '../../../resource/images/btnImages/modify.png',
		handler : editWaveDir
	} ],
	bbar : pageTool
});

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

/**
 * 新增
 */
function addWaveDir() {
	var addWindow=new Ext.Window({
		id:'addWindow',
		title:'新增波分方向',
		width : Ext.getBody().getWidth()*0.8,      
	    height : Ext.getBody().getHeight()-80, 
		closeAction :'close',
		border:'fit',
		stateful:false,
		isTopContainer : true,
		modal : true,
		plain:true,  //是否为透明背景 
		html : '<iframe id="addAndEdit" name = "addAndEdit" src = "addAndEdit.jsp" height="100%" width="100%"  frameBorder=0 border=0/>',
		buttons:[{
			text:"确定",
			handler:function(){
				 var addAndEdit = window.frames["addAndEdit"];
				 addAndEdit.selectedUnitGrid.stopEditing();
				 addAndEdit.save();
			}
		},{
			text:"取消",
			handler:function(){
				addWindow.close();
			}
		}]
	});
	addWindow.show();
}
/**
 * 修改
 */
var editInfoFormValues = {};
function editWaveDir() {
	var selections = gridPanel.getSelectionModel().getSelections();
	if(!selections || selections.length==0 || selections.length>1){
		Ext.Msg.alert("提示","请选取一条波分方向！");
		return;
	}
	var waveDirId = selections[0].get("waveDirId");
	var neId = selections[0].get("neId");
	var neStr = "";
	if(!!selections[0].get("emsGroup"))
		neStr += selections[0].get("emsGroup") + ":";
	if(!!selections[0].get("ems"))
		neStr += selections[0].get("ems") + ":";
	if(!!selections[0].get("subnet"))
		neStr += selections[0].get("subnet") + ":";
	editInfoFormValues = {
			"neInfo":neStr+selections[0].get("ne"),
			"unitInfoField":selections[0].get("unit"),
			"stationInfo":selections[0].get("station"),
			"waveDirInfo":selections[0].get("waveDir"),
			"networkInfo":selections[0].get("networkName"),
			"waveCountInfo":selections[0].get("stdWaveNum"),
			"waveCountActInfo":selections[0].get("actWaveNum")
	};
	var editWindow=new Ext.Window({
		id:'editWindow',
		title:'修改波分方向',
		width : Ext.getBody().getWidth()*0.8,      
		height : Ext.getBody().getHeight()-80, 
		closeAction :'close',
		border:'fit',
		stateful:false,
		isTopContainer : true,
		modal : true,
		plain:true,  //是否为透明背景 
		html : '<iframe id="addAndEdit" name = "addAndEdit" src = "addAndEdit.jsp?waveDirId='+
				waveDirId+'&neId='+neId
				+'" height="100%" width="100%"  frameBorder=0 border=0/>',
		buttons:[{
			text:"确定",
			handler:function(){
				var addAndEdit = window.frames["addAndEdit"];
				 addAndEdit.selectedUnitGrid.stopEditing();
				addAndEdit.saveEdit(true);
			}
		},{
			text:"取消",
			handler:function(){
				editWindow.close();
			}
		},{
			text:"应用",
			handler:function(){
				addAndEdit.selectedUnitGrid.stopEditing();
				addAndEdit.saveEdit(false);
			}
		},{
			text:"重置",
			handler:function(){
				var addAndEdit = window.frames["addAndEdit"];
				addAndEdit.init();
			}
		}]
	});
	editWindow.show();
}


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
	store.baseParams = {modifyList:nodeList,start:0,limit:200};
	store.load({
		callback : function(records,options,success){
			// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
			if (!success) {
				Ext.Msg.alert('错误', '查询失败！请重新查询');
			}else{
			}
		}
	});
}


/**
 * 删除
 */
function deleteWaveDir(){
	Ext.Msg.confirm('提示','是否删除选中的波分方向？',function(btn){
		if(btn=="yes"){
			var selections = gridPanel.getSelectionModel().getSelections();
			if(!selections || selections.length==0){
				Ext.Msg.alert("提示","请选取波分方向！");
				return;
			}
			var idList = new Array();
			for(var i=0;i<selections.length;i++){
				idList.push(selections[i].get("waveDirId"));
			}
			var params = {"paramMap.waveDirIdDel":idList.toString()};
			top.Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
				url : 'nx-report!deleteWaveDir.action',
				params : params,
				method : 'POST',
				success : function(response) {
					top.Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					if (result.returnResult==0) {
						Ext.Msg.alert("提示", result.returnMessage);
					}
					if (result.returnResult==1) {
						Ext.Msg.alert("提示", result.returnMessage);
						pageTool.doLoad(pageTool.cursor);
					}
				},
				failure : function(response) {
					top.Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					Ext.Msg.alert("提示", result.returnMessage);
				},
				error : function(response) {
					top.Ext.getBody().unmask();
					var result = Ext.util.JSON.decode(response.responseText);
					Ext.Msg.alert("提示", result.returnMessage);
				}
			});
		}
	});
}

function factoryRenderer(v){
	for(var i=0;i<FACTORY.length;i++){
		if(v==FACTORY[i]['key'])
			return FACTORY[i]['value'];
	}
	return v;
}

Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			Ext.Msg = top.Ext.Msg;
			Ext.Ajax.timeout = 900000;

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ gridPanel, westPanel ]
			});
			win.show();
		});
