var store = new Ext.data.Store({ 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "DISPLAY_NAME", "VC4" ,"VC12" ,"aNeDisplayName" ,"aPortDesc",
	     "zNeDisplayName","zPortDesc", "DIRECTION",,"IS_MANUAL"])
});
function getSelection() {
    // 选择tree中选中的节点
    var sels = westPanel.getCheckedNodes(["nodeId", "nodeLevel", "text"], "top",
            [1], "all");
    if(sels.length>0){
    	Ext.Msg.alert("提示", "请勿选择网管分组节点！");
    	return {length:0};
    }
    sels = westPanel.getCheckedNodes(["nodeId", "nodeLevel", "text"], "top",
            [2, 3, 4], "all");
    if(sels.length == 0){
    	Ext.Msg.alert("提示", "请先选择要查询的节点！");
    	return {length:0};
    }
    var rv = {
    	nodes : [],
        count : 0
    };
	for(var i=0;i<sels.length;i++){
		rv.nodes.push(Ext.encode({nodeId:sels[i]["nodeId"],nodeLevel:sels[i]["nodeLevel"]}));
	}
    rv.length = sels.length;
    return rv;
}
var westPanel = new Ext.ux.EquipTreePanel({
	xtype:'equiptree',
	rootVisible: false,
	title:"",
	region:"west",
	width: 250,
	//autoScroll:true,
    boxMinWidth: 250,
	leafType:4,
    boxMinHeight: 260,
    split : true,
	collapsible : false,
	collapseMode : 'mini',
//    filterBy: CommonDefine.filterNE_WDM,
    //checkNodes: checkNodes,
//    onGetChecked:onGetChecked,
    //onCheckChange:onCheckChange,
    listeners:{
//    	afterrender : null
    }
});
/**
 * 告警高级查询
 */
function addUnitInterface(){
	var url = 'addUnitInterface.jsp?authSequence=' + authSequence + "&unitInterfaceId=0&unitId=0";
	var addUnitInterfaceWindow = new Ext.Window({
		id : 'addUnitInterfaceWindow',
		title : '新增板卡接口配置',
		width: Ext.getBody().getWidth()*0.8,
		height: Ext.getBody().getHeight() * 0.9,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='addUnitInterface_Panel' name = '添加板卡接口'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addUnitInterfaceWindow.show();
}

/**
 * 新增WDM光开关盘界面
 */
function addSwitchInterface(){
	var url = 'addSwitchInterface.jsp?authSequence=' + authSequence + "&unitInterfaceId=0&unitId=0&neId=0";
	var addSwitchInterfaceWindow = new Ext.Window({
		id : 'addSwitchInterfaceWindow',
		title : '新增板卡接口配置',
		width: Ext.getBody().getWidth()*0.8,
		height: Ext.getBody().getHeight() * 0.9,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='addSwitchInterface_Panel' name = '添加光开关接口'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addSwitchInterfaceWindow.show();
}
/**
 * 修改板卡接口
 */
function modUnitInterface(rec){
	var unitInterfaceId = rec.get("UNIT_INTERFACE_ID");
	var unitId = rec.get("BASE_UNIT_ID");
	var url = 'addUnitInterface.jsp?authSequence=' + authSequence + "&unitInterfaceId=" + unitInterfaceId + "&unitId=" + unitId;
	var addUnitInterfaceWindow = new Ext.Window({
		id : 'addUnitInterfaceWindow',
		title : '修改板卡接口配置',
		width: Ext.getBody().getWidth()*0.8,
		height: Ext.getBody().getHeight() * 0.9,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='modUnitInterface_Panel' name = '修改板卡接口'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addUnitInterfaceWindow.show();
}
/**
 * 修改光开关接口
 */
function modSwitchInterface(rec){
	var unitInterfaceId = rec.get("UNIT_INTERFACE_ID");
	var unitId = rec.get("BASE_UNIT_ID");
	var url = 'addSwitchInterface.jsp?authSequence=' + authSequence + "&unitInterfaceId=" + unitInterfaceId + "&unitId=" + unitId;
	var addUnitInterfaceWindow = new Ext.Window({
		id : 'addSwitchInterfaceWindow',
		title : '修改板卡接口配置',
		width: Ext.getBody().getWidth()*0.8,
		height: Ext.getBody().getHeight() * 0.9,
		isTopContainer : true,
		modal : true,
		autoScroll : true,
		html : "<iframe  id='modSwitchInterface_Panel' name = '修改光开关接口'  src = " + url
				+ " height='100%' width='100%' frameBorder=0 border=0/>"
	});
	addUnitInterfaceWindow.show();
}
/**
 * 删除
 */
function delUnitInterface(){
	var selections = centerPanel.sm.getSelections();
	if(!selections || selections.length==0){
		Ext.Msg.alert("提示","请选取要删除的板卡接口！");
		return;
	}
	var idList = new Array();
	for(var i=0;i<selections.length;i++){
		idList.push(selections[i].get("BASE_UNIT_ID"));
	}
	var params = {"paramMap.unitIdToDel":idList.toString()};
	Ext.getBody().mask('正在执行，请稍候...');
	Ext.Ajax.request({
		url : 'nx-report!delUnitInterface.action',
		params : params,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
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
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}
var centerPanel;
var pageTool = null;
(function(){
	var dataStore = new Ext.data.Store({
	    url : 'nx-report!getUnitList.action',
	    baseParams : {
	    },
	    reader : new Ext.data.JsonReader({
	        totalProperty : 'total',
	        root : "rows"
	    }, ["SLOT_NO","EMS_GROUP_NAME","SUBNET_NAME",'UNIT_NAME','UNIT_INFO_SWITCH','UNIT_INFO_WAVE','UNIT_TYPE',"UNIT_MODEL","NE_NAME","UNIT_INFO","EMS_NAME","BASE_UNIT_ID","UNIT_INTERFACE_ID"])
	});
	pageTool = new Ext.PagingToolbar({
		id : 'dataStore',
		pageSize : 200,// 每页显示的记录值
		store : dataStore,
		displayInfo : true,
		displayMsg : '当前 {0} - {1} ，总数 {2}',
		emptyMsg : "没有记录"
	});
	var selModel = new Ext.grid.CheckboxSelectionModel({
//		singleSelect : true
	});
	var cm = new Ext.grid.ColumnModel({
	    // specify any defaults for each column
	    defaults : {
	        sortable : true,
	        width : 100
	        // columns are not sortable by default
	    },
	    columns : [new Ext.grid.RowNumberer({
			width : 26
		}),
		selModel,{
			id : 'EMS_GROUP_NAME',
			header : '网管分组',
			dataIndex : 'EMS_GROUP_NAME',
			renderer:function(v){
				return !!v ? v : "-";
			}
		},{
			id : 'EMS_NAME',
			header : '网管',
			dataIndex : 'EMS_NAME'
		},{
			id : 'SUBNET_NAME',
			header : '子网',
			dataIndex : 'EMS_NAME',
			renderer:function(v){
				return !!v ? v : "-";
			}
		},{
			id : 'NE_NAME',
			header : '网元名称',
			dataIndex : 'NE_NAME'
		},{
			id : 'SLOT_NO',
			header : '槽道',
			dataIndex : 'SLOT_NO'
		},{
			id : 'UNIT_NAME',
			header : '板卡',
			dataIndex : 'UNIT_NAME'
		},{
			id : 'UNIT_TYPE',
			header : '板卡类型',
			dataIndex : 'UNIT_TYPE',
			renderer:function(v){
				switch(v){
				case 1: return 'WDM波长转换盘';
				case 2 : return 'WDM光开关盘';
				default : return v;
				}
			}
		},{
			id : 'UNIT_INFO',
			header : '板卡规格',
			dataIndex : 'UNIT_INFO',
			renderer:function(v,m,r){
				switch(r.get('UNIT_TYPE')){
				case 1: return r.get('UNIT_INFO_WAVE');
				case 2 : return r.get('UNIT_INFO_SWITCH');
				default : return v;
				}
			}
		}]
	});
	
	var gridPanel = new Ext.grid.GridPanel({
	    id : "gridPanel",
	    autoScroll : true,
	    // title:'用户管理',
	    cm : cm,
	    border : false,
	    store : dataStore,
	    stripeRows : true, // 交替行效果
	    loadMask : true,
	    selModel : selModel, // 必须加不然不能选checkbox
	    forceFit : true,
	    frame : false,
		bbar : pageTool
	});
	var cmbType = new Ext.form.ComboBox({
	    typeAhead: true,
	    triggerAction: 'all',
	//    lazyRender:true,
	    mode: 'local',
	    width:120,
	    store: new Ext.data.ArrayStore({
	        id: 0,
	        fields: [
	            'id',
	            'displayText'
	        ],
	        //全部、WDM波长转换盘、WDM光开关盘
	        data: [[0, '全部'], [1, 'WDM波长转换盘'], [2, 'WDM光开关盘']]
	    }),
	    value:0,
	    valueField: 'id',
	    displayField: 'displayText',
		listeners : {
			'select' : function(cmb) {
	//			if(cmb.getValue() != 9)
	//				cmb.getStore().filter("TRAFFIC_FAULT", cmb.getValue());
	//			else
	//				cmb.getStore().clearFilter();
			}
		}
	});
	
	centerPanel = new Ext.Panel({
		id : 'centerPanel',
	    region : 'center',
	    //border : false,
	    layout : 'fit',
	    autoScroll : true,
	    tbar : ["板卡类型：", cmbType, {
	            xtype : 'button', 
	            icon : '../../../resource/images/btnImages/search.png',
	            text : '查询',
	            privilege:modAuth,
	            handler:function(){
	            	var rv = getSelection();
	            	if(rv.length == 0){
	            		return;
	            	}
	            	//当有选择节点的时候
	            	dataStore.baseParams = {
	            			modifyList:rv.nodes,
	            			"paramMap.UNIT_TYPE":cmbType.getValue(),
	            			start:0, 
	            			limit:200
            			};
	            	dataStore.load({
	            		callback : function(records,options,success){
	            			// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
	            			if (!success) {
	            				Ext.Msg.alert('错误', '查询失败！请重新查询');
	            			}else{
	            			}
	            		}
	            	});
	            }
	        },	{
	        	xtype: 'splitbutton', 
				text:'新增',
				privilege:addAuth, 
				icon : '../../../resource/images/btnImages/add.png',
				handler: function(b,e){this.showMenu();},
				menu:{
					items: [{
						text: 'WDM波长转换盘界面',
						handler: addUnitInterface
					}, {
						text: 'WDM光开关盘界面',
						handler: addSwitchInterface
					}]
				} 
	        }, {
	            xtype : 'button',
	            icon : '../../../resource/images/btnImages/delete.png',
	            text : '删除',
	            privilege:delAuth,
	            handler:delUnitInterface
	        }, {
	            xtype : 'button',
	            icon : '../../../resource/images/btnImages/modify.png',
	            text : '修改',
	            privilege:modAuth,
	            handler:function(){
	            	var selections = centerPanel.sm.getSelections();
	            	if(!selections || selections.length==0){
	            		Ext.Msg.alert("提示","请选取要修改的板卡接口配置！");
	            		return;
	            	}
	            	if(selections.length>1){
	            		Ext.Msg.alert("提示","只能选取一个板卡接口配置进行修改！");
	            		return;
	            	}
	            	var UNIT_TYPE = selections[0].get("UNIT_TYPE")>>0;
	            	if(UNIT_TYPE == 1){
	            		modUnitInterface(selections[0]);
	            	}else if(UNIT_TYPE == 2){
	            		modSwitchInterface(selections[0]);
	            	}
	            }
	        }
	    ],
	    items : [gridPanel]
	});
	var gridPanel = new Ext.grid.GridPanel({
		id : "gridPanel",
		region : "center",
		cm : cm,
		store : store,
		stripeRows : true, // 交替行效果
		loadMask : true 
	});
	centerPanel.sm = selModel;
})();

var mainPanel = new Ext.Panel({
	id : 'mainPanel',
	title : "板卡接口管理",
	layout:"border",
	items : [westPanel, centerPanel]
});
function initData(){
	
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000; 
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'fit',
		items : [mainPanel]
	});
	win.show();
//	initData();
});