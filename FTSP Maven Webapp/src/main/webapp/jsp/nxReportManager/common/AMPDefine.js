/* 【_】【_】【_】【_】【_】【_】【G】【R】【I】【D】【_】【_】【_】【_】【_】【_】【_】*/
var reader = new Ext.data.JsonReader({
	totalProperty : 'total',
	root : "rows"
}, [ "EMSGROUP_DISPLAY_NAME", "EMS_DISPLAY_NAME", "NE_DISPLAY_NAME", "BASE_NE_ID", 
     "SUBNET_DISPLAY_NAME", "FACTORY", "PRODUCT_NAME",	"NET_WORK_NAME", "DEPARTMENT", 
     "RESOURCE_UNIT_MANAGE_ID", "DIRECTION", "STATION", "detailInfo" ]);

var store = new Ext.data.Store({
	url:'nx-report!getManageInfoByTaskId.action',
	reader : reader
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'EMSGROUP_DISPLAY_NAME',
		header : '网管分组',
		dataIndex : 'EMSGROUP_DISPLAY_NAME',
		width : 130
	}, {
		id : 'EMS_DISPLAY_NAME',
		header : '网管',
		dataIndex : 'EMS_DISPLAY_NAME',
		width : 130
	}, {
		id : 'SUBNET_DISPLAY_NAME',
		header : '子网',
		dataIndex : 'SUBNET_DISPLAY_NAME',
		width : 120
	}, {
		id : 'NE_DISPLAY_NAME',
		header : '网元名称',
		dataIndex : 'NE_DISPLAY_NAME',
		width : 180
	}, {
		id : 'FACTORY',
		header : '设备厂家',
		dataIndex : 'FACTORY',
		width : 100,
		renderer : factoryRenderer
	}, {
		id : 'PRODUCT_NAME',
		header : '网元型号',
		dataIndex : 'PRODUCT_NAME',
		width : 140
	}, {
		id : 'DEPARTMENT',
		header : '单位',
		dataIndex : 'DEPARTMENT',
		width : 100
	}, {
		id : 'STATION',
		header : '站名',
		dataIndex : 'STATION',
		width : 100
	}, {
		id : 'NET_WORK_NAME',
		header : '网络名称',
		dataIndex : 'NET_WORK_NAME',
		width : 120
	}, {
		id : 'DIRECTION',
		header : '方向',
		dataIndex : 'DIRECTION',
		width : 120
	} ]
});

function factoryRenderer(v){
	for(var i=0;i<FACTORY.length;i++){
		if(v==FACTORY[i]['key'])
			return FACTORY[i]['value'];
	}
	return v;
}

var TARGET_NAME = "光放大器";
/**【_】【_】【_】【_】【_】【_】【G】【R】【I】【D】【_】【_】【_】【_】【_】【_】【_】*/


/* 【_】【_】【_】【_】【_】【_】【_】【光】【放】【盘】【_】【_】【_】【_】【_】【_】【_】*/


//用于修改盘信息时初始化
var RECORD_FOR_EDIT = null;

/**
 * 新增
 * type:
 *       1 新增
 *       2 修改 
 */
function addOrEditTarget(type) {
	var url =  "../units/AMP/Add.jsp?type="+type;
	var title;
	var hidden = true;
	if(type == 2){
		hidden = false;
		title = "修改"+TARGET_NAME;
		var items = grid.getSelectionModel().getSelections();
		if(!(items.length>0)){
			Ext.Msg.alert('提示','请选择一条记录！');
			return;
		}else if(items.length>1){
			Ext.Msg.alert('提示','只能选择一条记录！');
			return;
		}
		RECORD_FOR_EDIT = items[0];
	}else{
		title = "新增"+TARGET_NAME;
	}
	var addWindow=new Ext.Window({
		id:'addWindow',
		title:title,
		width : Ext.getBody().getWidth()*0.8,      
	    height : Ext.getBody().getHeight()-80, 
		closeAction :'close',
		border:'fit',
		stateful:false,
		isTopContainer : true,
		modal : true,
		plain:true,  //是否为透明背景 
		html : '<iframe id="addAndEdit" name = "addAndEdit" src = "'+url+'" height="100%" width="100%"  frameBorder=0 border=0/>',
		buttons:[{
			text:"确定",
			handler:function(){
				 var addAndEdit = window.frames["addAndEdit"];
				 addAndEdit.okBtnFunction();
			}
		},{
			text:"应用",
			hidden:hidden,
			handler:function(){
				 var addAndEdit = window.frames["addAndEdit"];
				 addAndEdit.applyFunction();
			}
		},{
			text:"重置",
			hidden:hidden,
			handler:function(){
				 var addAndEdit = window.frames["addAndEdit"];
				 addAndEdit.resetFunction();
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
 * @param obj
 *            数据对象 { BASE_NE_ID:, DEPARTMENT:, STATION:, NET_WORK_NAME:,
 *            DIRECTION, RESOURCE_UNIT_MANAGE_ID: }
 */
function dataTransport(obj) {
	if (!!!obj.BASE_NE_ID)
		return;
	var list = new Array();
	var nodes = {
		'nodeId' : obj.BASE_NE_ID,
		'nodeLevel' : NodeDefine.NE
	};
	list.push(Ext.encode(nodes));
	var params = {
		'modifyList' : list
	};
	grid.getEl().mask('加载中...');
	Ext.Ajax.request({
		url : 'nx-report!getNodeInfo.action',
		params : params,
		method : 'POST',
		success : function(response) {
			grid.getEl().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				var record = result.info[0];
				record.NET_WORK_NAME = obj.NET_WORK_NAME;
				record.STATION = obj.STATION;
				record.DIRECTION = obj.DIRECTION;
				record.DEPARTMENT = obj.DEPARTMENT;
				record.RESOURCE_UNIT_MANAGE_ID = obj.RESOURCE_UNIT_MANAGE_ID;
				record.detailInfo = obj;
				var records = new Array();
				records.push(record);
				var dataObj = {
					'rows' : records,
					'total' : records.length
				};
				//判重
				store.each(function(r){
					if(r.get('RESOURCE_UNIT_MANAGE_ID')==obj.RESOURCE_UNIT_MANAGE_ID){
						store.remove(r);
						return;
					}
				});
				store.loadData(dataObj,true);
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			grid.getEl().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			grid.getEl().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

/**【_】【_】【_】【_】【_】【_】【_】【光】【放】【盘】【_】【_】【_】【_】【_】【_】【_】*/

var extend=function(o,n,override){
	   for(var p in n)if(n.hasOwnProperty(p) && (!o.hasOwnProperty(p) || override))o[p]=n[p];
	};