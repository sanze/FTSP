var linkJsonReader = new Ext.data.JsonReader({
	totalProperty : 'total',
	root : "rows"
}, [ "linkId", "linkName", "aPtpId","zPtpId","aNeName", "zNeName", "aPtpName","zPtpName",
     "aNeId", "zNeId", "direction","isManual"])
var unselectedLinkStore = new Ext.data.Store({
	id:'unselectedLinkStore',
	url : 'trans-system!getLinkBetweenNe.action',
	reader : linkJsonReader
});
var uslcm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var unselectedLinkCm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}),uslcm, {
		id : 'linkName',
		header : '链路名称',
		dataIndex : 'linkName',
		width : 100
	}, {
		id : 'aNeName',
		header : 'A端网元',
		dataIndex : 'aNeName',
		width : 100
	}, {
		id : 'aPtpName',
		header : 'A端端口',
		dataIndex : 'aPtpName',
		width : 100
	}, {
		id : 'zNeName',
		header : 'Z端网元',
		dataIndex : 'zNeName',
		width : 100
	}, {
		id : 'zPtpName',
		header : 'Z端端口',
		dataIndex : 'zPtpName',
		width : 100
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'direction',
		width : 80,
		renderer : linkDirectionRenderer
	}, {
		id : 'isManual',
		header : '生成方式',
		dataIndex : 'isManual',
		width : 80,
		renderer : isManualRenderer
	}]
});

var selectedLinkStore = new Ext.data.Store({
	url : 'trans-system!getTransSysLink.action',
	reader : linkJsonReader
});
var slcm = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});
var selectedLinkCm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}),slcm, {
		id : 'linkName',
		header : '链路名称',
		dataIndex : 'linkName',
		width : 100
	}, {
		id : 'aNeName',
		header : 'A端网元',
		dataIndex : 'aNeName',
		width : 100
	}, {
		id : 'aPtpName',
		header : 'A端端口',
		dataIndex : 'aPtpName',
		width : 100
	}, {
		id : 'zNeName',
		header : 'Z端网元',
		dataIndex : 'zNeName',
		width : 100
	}, {
		id : 'zPtpName',
		header : 'Z端端口',
		dataIndex : 'zPtpName',
		width : 100
	}, {
		id : 'direction',
		header : '方向',
		dataIndex : 'direction',
		width : 80,
		renderer : linkDirectionRenderer
	}, {
		id : 'isManual',
		header : '生成方式',
		dataIndex : 'isManual',
		width : 80,
		renderer : isManualRenderer
	}]
});
var uslGrid = new Ext.grid.GridPanel({
	id : 'uslGrid',
	title:'未关联链路',
	height:190,
	store : unselectedLinkStore,
	autoScroll:true,
	margins:{top:0, right:0, bottom:20, left:0},
	cm : unselectedLinkCm,
	selModel : uslcm,
	frame : false,
	loadMask:true,
	stripeRows : true, // 交替行效果
	tbar:['-',{
		text:'关联',
//		icon:'../../../resource/images/down2.gif',
		handler:function(){
			carrier(uslGrid,slGrid);
		}
	}]
});

var slGrid = new Ext.grid.GridPanel({
	id : 'slGrid',
	title:'已关联链路',
	height:190,
	store : selectedLinkStore,
	autoScroll:true,
	cm : selectedLinkCm,
	selModel : slcm,
	frame : false,
	loadMask:true,
	stripeRows : true, // 交替行效果
	tbar:['-',{
		text:'取消关联',
		id:'usbutton',
//		icon:'../../../resource/images/up2.gif',
		handler:function(){
			carrier(slGrid,uslGrid);
		}
	}]
});

var stepThree = new Ext.Panel({
	id : 'stepThree',
	border : false,
	title : '系统内链路',
	autoScroll:true,
	layout : 'form',
	labelWidth:1,
	align:"center",
	bodyStyle : 'padding:10px 50px',
	items : [ uslGrid,{xtype:'spacer',height:15}, slGrid ]
});



function saveNe(id){
	var idList = new Array();
	neStore.each(function(r){
		idList.push(r.get('neId'));
	});
	if(idList.length>0){
		var saveParams = {
				intList : idList,
				'paramMap.transSysId':id,
				'paramMap.operType':'new'
		};
		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
		    url: 'trans-system!saveTransSystemNe.action', 
		    method : 'POST',
		    params: saveParams,
		    success: function(response) {
		    	top.Ext.getBody().unmask();
		    	var obj = Ext.decode(response.responseText);
				if(obj.returnResult){//新增成功
					alert(obj.returnMessage);
				}else{
					Ext.Msg.alert("提示", obj.returnMessage);
				}
		    },
		    error:function(response) {
		    	top.Ext.getBody().unmask();
		    	Ext.Msg.alert("提示", obj.returnMessage);
		    },
		    failure:function(response) {
		    	top.Ext.getBody().unmask();
		    	Ext.Msg.alert("提示", obj.returnMessage);
		    }
		}); 	
	}
}
function saveLink(id){
	var idList = new Array();
	selectedLinkStore.each(function(r){
		idList.push(r.get('linkId'));
	});
	if(idList.length>0){
		var saveParams = {
				intList : idList,
				'paramMap.transSysId':id,
				'paramMap.operType':'new',
				'paramMap.linkList':idList.toString()
		};
		top.Ext.getBody().mask('正在执行，请稍候...');
		Ext.Ajax.request({
			url: 'trans-system!saveTransSystemLink.action', 
			method : 'POST',
			params: saveParams,
			success: function(response) {
				top.Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				if(obj.returnResult){//新增成功
				}else{
					Ext.Msg.alert("提示", obj.returnMessage);
				}
			},
			error:function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("提示", obj.returnMessage);
			},
			failure:function(response) {
				top.Ext.getBody().unmask();
				Ext.Msg.alert("提示", obj.returnMessage);
			}
		}); 	
	}
}

function saveLink(id){
	var idList = new Array();
	neStore.each(function(r){
		idList.push(r.get('neId'));
	});
}
/**
 * 找出记录中已经被加载的部分并去掉
 * @param records 一组需要检查的RECORD
 * @returns {Array} 已经去除多余数据的记录集合
 */
function removeSelectedFromUns(records){
	var recordsForAdd = new Array();
	var length ;
	length = records.length;
	for ( var i = 0; i < length; i++) {
		var recordIndex = selectedLinkStore.findBy(function(rec, id) {
			if (rec.get('linkId') == records[i].get('linkId')) {
				return true;
			}
		});
		if (recordIndex == -1) {
			recordsForAdd.push(records[i]);
		}
	}
	return recordsForAdd;
}
/**
 * 已关联的链路中如果有超过网元范围的需要移除
 * @param records
 */
function removeLinkOutOfNe(records){
	var linkRec = selectedLinkStore.getRange(0,selectedLinkStore.getCount()-1);
	var recToRemove = [];
	var recToAdd = [];
	var i=0;
	for(i;i<linkRec.length;i++){
		var rl = linkRec[i];
		var j=0;
		var isIn = false;
		for(j;j<records.length;j++){
			var r = records[j];
			var rldata = rl.data?rl.data:rl;
			if(r.get('linkId')==rldata.linkId){
				isIn = true;
				recToAdd.push(rl);
				break;
			}
		}
		if(isIn)
			continue;
		else
			recToRemove.push(rl);
	}
	if(recToRemove.length==0)
		return;
	selectedLinkStore.removeAll();
	selectedLinkStore.insert(0,recToAdd);
}
/**
 * 把记录从一个表格搬运到另一个表格的搬运工
 * @param from
 * @param to
 */
function carrier(from,to){
	var selections = from.getSelectionModel().getSelections(); 
	if(selections.length==0){
		return;
	}
	var i=0;
	for(i;i<selections.length;i++){
		from.getStore().remove(selections[i]);
		to.getStore().add(selections[i]);
	}
	from.getView().refresh();
	to.getView().refresh();
}
