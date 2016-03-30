/* 【_】【_】【_】【_】【_】【_】【G】【R】【I】【D】【_】【_】【_】【_】【_】【_】【_】*/
var reader = new Ext.data.JsonReader({
	totalProperty : 'total',
	root : "rows"
}, [ "RESOURCE_UNIT_MANAGE_ID","FACTORY", "PRODUCT_NAME","NET_WORK_NAME", "DEPARTMENT", 
     "STD_WAVE_NUM","ACTUAL_WAVE_NUM" , "detailInfo"]);

var store = new Ext.data.Store({
	url:'nx-report!getManageInfoByTaskId.action',
	reader : reader
});

var sm = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'DEPARTMENT',
		header : '单位',
		dataIndex : 'DEPARTMENT',
		width : 100
	},{
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
		id : 'NET_WORK_NAME',
		header : '网络名称',
		dataIndex : 'NET_WORK_NAME',
		width : 120
	},  {
		id : 'STD_WAVE_NUM',
		header : '容量（波数）',
		dataIndex : 'STD_WAVE_NUM',
		width : 100
	},  {
		id : 'ACTUAL_WAVE_NUM',
		header : '实开（波数）',
		dataIndex : 'ACTUAL_WAVE_NUM',
		width : 100
	} ]
});

function factoryRenderer(v){
	for(var i=0;i<FACTORY.length;i++){
		if(v==FACTORY[i]['key'])
			return FACTORY[i]['value'];
	}
	return v;
}

var TARGET_NAME = TASK_TYPE==WAVE_JOIN?"合波盘":"分波盘";
var PATH =  TASK_TYPE==WAVE_JOIN?"../units/WAVEJOIN/":"../units/WAVEDIV/";
/**【_】【_】【_】【_】【_】【_】【G】【R】【I】【D】【_】【_】【_】【_】【_】【_】【_】*/


/* 【_】【_】【_】【_】【_】【_】【合】【波】【分】【波】【_】【_】【_】【_】【_】【_】【_】*/
/**
 * 新增
 * type:
 *       1 新增
 *       2 修改 
 */
function addOrEditTarget(type) {
	var url =  PATH+"Add.jsp?type="+type;
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
	var record = {};
	record.NET_WORK_NAME = obj.NET_WORK_NAME;
	record.PRODUCT_NAME = obj.PRODUCT_NAME;
	record.DEPARTMENT = obj.DEPARTMENT;
	record.RESOURCE_UNIT_MANAGE_ID = obj.RESOURCE_UNIT_MANAGE_ID;
	record.STD_WAVE_NUM = obj.STD_WAVE_NUM;
	record.ACTUAL_WAVE_NUM = obj.ACTUAL_WAVE_NUM;
	record.FACTORY = obj.FACTORY;
	// record.detailInfo = obj;
	var records = new Array();
	records.push(record);
	var dataObj = {
		'rows' : records,
		'total' : records.length
	};
	// 判重
	store.each(function(r) {
		if (r.get('RESOURCE_UNIT_MANAGE_ID') == obj.RESOURCE_UNIT_MANAGE_ID) {
			store.remove(r);
			return;
		}
	});
	store.loadData(dataObj, true);
}

/**【_】【_】【_】【_】【_】【_】【合】【波】【分】【波】【_】【_】【_】【_】【_】【_】【_】*/

var extend=function(o,n,override){
	   for(var p in n)if(n.hasOwnProperty(p) && (!o.hasOwnProperty(p) || override))o[p]=n[p];
	};