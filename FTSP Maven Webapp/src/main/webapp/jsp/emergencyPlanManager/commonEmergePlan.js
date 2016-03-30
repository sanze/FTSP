//预案类型
var typeMapping = [ [ 1, '设备预案' ], [ 2, '网管预案' ], [ 3, '电路预案' ], [ 4, '传输系统预案' ],
		[ 5, '机房环境预案' ], [ 6, '后勤保障预案' ], [ 99, '其他预案' ] ]; 
var emergencyType;
(function() {
	var typeStore = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'emergencyType'
		} ]
	});
	typeStore.loadData(typeMapping);
	emergencyType = new Ext.form.ComboBox({
		id : 'emergencyType',
		fieldLabel : '预案类型',
		mode : 'local',
		store : typeStore,
		displayField : 'emergencyType',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})(); 

function typeRenderer(v, m, r) {
	if(typeof v == 'number'){
		for(var i=0;i<typeMapping.length;i++){
			if(typeMapping[i][0] == v){
				return typeMapping[i][1];
			}
		}
	}
}

//预案内容
var storeFile;
/*(function() {
	var data = [ [ 1,'1', '省网三四期_华为','2010/01/22', '2010/01/22' ],
			[ 2,'2', '省网三四期_华为', 3, 'admin','2010/01/22', '2010/01/22' ],
			[ 2,'2', '省网三四期_华为', 3, 'admin','2010/01/22', '2010/01/22' ]];
	
	storeFile = new Ext.data.ArrayStore({
		fields : [ "id", "fileName","DISPALY_NAME","EP_TYPE","KEY_WORD"]
	});
	storeFile.loadData(data);
})();*/
(function(){
	storeFile = new Ext.data.Store({
		url : 'emergency-plan!getEmergencyPlanContentList.action',
	    reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
					root : "rows"
			},[
				"FAULT_EP_CONTENT_ID","FAULT_EP_ID","DISPALY_NAME",
				"FILE_PATH","CREATE_TIME","UPDATE_TIME"
		])
	});	
})();

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect:false
    }); 

var cm = new Ext.grid.ColumnModel({ 
	defaults : {
		sortable : false
	},
	columns : [
			new Ext.grid.RowNumberer({
				width : 26
			}),checkboxSelectionModel,
			{
				id : 'DISPALY_NAME', 
				header: '文档名',
				dataIndex : 'DISPALY_NAME',
				width: 300
			}]
	});  
 
var listGrid = new Ext.grid.GridPanel({
	id : 'listGrid',  
	store : storeFile,
	cm : cm,
	frame : false,
	stripeRows : true, // 交替行效果 
//	autoHeight:true,
	selModel : checkboxSelectionModel,
	listeners:{
		cellclick :function(grid, rowIndex, columnIndex, e){
			var filePath = grid.getStore().getAt(rowIndex).get("FILE_PATH");
			//只预览pdf文档
			if(/^.*?\.(pdf)$/.test(filePath)){
				document.getElementById("preview").src="download!preview.action?"+Ext.urlEncode({filePath:filePath});
			}else{
				document.getElementById("preview").src="";
			}
		 }
	}
});

var emergeSet = new Ext.form.FieldSet({ 
    title: '预案列表',  
    items :[listGrid] 
});  