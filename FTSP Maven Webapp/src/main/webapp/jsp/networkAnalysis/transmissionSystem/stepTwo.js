var neStore = new Ext.data.Store({
	url : 'trans-system!getNeInfoWithArea.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "neName", "areaName", "neModel","neId"])
});

var neCm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : false
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), {
		id : 'neName',
		header : '网元名称',
		dataIndex : 'neName',
		width : 300
	}, {
		id : 'areaName',
		header : top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName',
		width : 100
	}, {
		id : 'neModel',
		header : '型号',
		dataIndex : 'neModel',
		width : 70
	}, {
		id : 'oper',
		header : '操作',
		dataIndex : 'oper',
		hideable:false ,
		width : 80,
		renderer : function(v,m,r,ri){
			return '<a href="javascript:;" onclick="removeRecord('+ri+');"><span style="color:red;">删除</span></a>';
		}
	}]
});
var stepTwoGrid = new Ext.grid.GridPanel({
	id : 'stepTwoGrid',
	title:'已关联网元',
//	region : 'center',
	store : neStore,
	cm : neCm,
	frame : false,
	loadMask:true,
	viewConfig: {forceFit: true},
	stripeRows : true // 交替行效果
});
var stepTwo = new Ext.Panel({
	id : 'stepTwo',
	border : false,
	title : '系统内网元',
	layout : 'fit',
	bodyStyle : 'padding:50px',
	align:"center",
	items : [ stepTwoGrid ]
});



function removeRecord(index) {
	neStore.removeAt(index);
	stepTwoGrid.getView().refresh();
}


function getLinkBetweenNe(idList,newPanel,transSysId){
	if(!!transSysId){
		var param = {'intList' : idList,
						'paramMap.transSysId':transSysId};
	}else{
		var param = {'intList' : idList};
	}
	if (idList.length > 0) {
		Ext.Ajax.request({
			url : 'trans-system!getLinkBetweenNe.action',
			params : param,
			method : 'POST',
			success : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					var data = linkJsonReader.readRecords(result);
					var records = data.records;
					removeLinkOutOfNe(records);
					// 其实后台加了限制之后，这里不需要再做检查，暂不修改
					var recordsForAdd = removeSelectedFromUns(records);
					unselectedLinkStore.removeAll();
					unselectedLinkStore.add(recordsForAdd);
				}
				if(!!newPanel)
					saveLink(newPanel);
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}else{
		selectedLinkStore.removeAll();
		unselectedLinkStore.removeAll();
	}
}


