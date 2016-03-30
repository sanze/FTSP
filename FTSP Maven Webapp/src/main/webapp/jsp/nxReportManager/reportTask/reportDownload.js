/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var today = new Date();
var todayStr = today.format("yyyy-MM-dd");
today.setDate(today.getDate()-7);
var lastWeekStr = today.format("yyyy-MM-dd");
var taskNameStore = new Ext.data.Store({
	url : 'nx-report!getTaskNameComboValuePrivilege.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "taskName", "taskId" ])
});
// 页面打开初始化创建人下拉框
taskNameStore.baseParams = {'paramMap.needAll':1};
taskNameStore.load();

var creatorStore = new Ext.data.Store({
	url : 'nx-report!getCreatorComboValuePrivilege.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "userName", "userId" ])
});
//页面打开初始化创建人下拉框
creatorStore.load();

var dataSrcData = [
    ['','全部'],
    ['0','原始数据'],
    ['1','异常数据']
];

var dataSrcStore = new Ext.data.ArrayStore({
	fields:[
			{name:'value'},
			{name:'displayField'}
		 ]
});
dataSrcStore.loadData(dataSrcData);

var reportTypeData = [
    ['','全部'],
    ['0','日报'],
    ['1','月报']
];
var reportTypeData1 = [
                      ['','全部'],
                      ['0','每日'],
                      ['1','每月']
                      ];

var reportTypeStore = new Ext.data.ArrayStore({
	fields:[
			{name:'value'},
			{name:'displayField'}
		 ]
});
reportTypeStore.loadData(reportTypeData);

var instantOrTaskData = [ [ '0', '即时报表' ], [ '1', '定时报表' ] ];

var instantOrTaskStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayField'
	} ]
});
instantOrTaskStore.loadData(instantOrTaskData);


var taskType;
(function(){
	var store = new Ext.data.ArrayStore({
		fields : [ {name:'id',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:[{key:'0',value:"全部"}]
	});
	store.loadData(REPORT_TYPE,true);
	taskType = new Ext.form.ComboBox({
		id : 'taskType',
		fieldLabel:'作业计划对象',
		triggerAction : 'all',
		editable : false,
		mode:'local',
		store : store,
//		value:0,
		width : 120,
		displayField : "displayName",
		valueField : 'id',	
		resizable: true
	});
})();
var formPanel = new Ext.FormPanel({
	id:'formPanel',
	region:'north',
	bodyStyle:'padding:20px 20px 20px 20px',
	border:false,
	frame:false,
	height:130,
	tbar:['-',{
		id : 'search',
		name : 'search',
		text:'查询',
		icon : '../../../resource/images/btnImages/search.png',
		privilege:viewAuth,
		handler:searchReport
	}],
	bbar:['-',{
		id : 'download',
		name : 'download',
		text:'下载',
		icon : '../../../resource/images/btnImages/down.png',
		privilege:actionAuth,
		handler:downloadReport
	},'-',{
		id : 'preview',
		name : 'preview',
		text:'预览',
		icon : '../../../resource/images/btnImages/search.png',
		privilege:viewAuth,
		handler:function(){
			preview();
		}
	},'-',{
		id : 'delete',
		name : 'delete',
		text:'删除',
		privilege:delAuth,
		icon : '../../../resource/images/btnImages/delete.png',
		handler:deleteReport
	}],
	items:[{
		layout:'column',
//		bodyStyle:'padding:5px 5px 5px 5px',
		border:false,
		items:[{
			layout:'form',
			width:220,
			labelWidth:60,
//			bodyStyle:'padding:5px 0px 0px 0px',
			border:false,
			items:[{
				xtype:'combo',
				id : 'instantOrTaskCombo',
				name : 'instantOrTaskCombo',
				triggerAction: 'all',
				store : instantOrTaskStore,
				editable:false,
				value:1,
				valueField : 'value',
				displayField : 'displayField',
				fieldLabel:'报表类型',
				forceSelection:true,
				width:120,
				mode : 'local',
				listeners:{
					select:function(combo,rec,index){
						if(combo.getValue()=="1"){
							Ext.getCmp('taskNameCombo').show();
							reportTypeStore.loadData(reportTypeData);
						}
						if(combo.getValue()=="0"){
							Ext.getCmp('taskNameCombo').hide();
							Ext.getCmp('taskNameCombo').setValue('');
							reportTypeStore.loadData(reportTypeData1);
						}
					}
				}
			},{
				xtype:'combo',
				id : 'dataSourceCombo',
				name : 'dataSourceCombo',
				triggerAction: 'all',
				store : dataSrcStore,
				valueField : 'value',
				width:120,
				editable:false,
				displayField : 'displayField',
				fieldLabel:'数据源',
				forceSelection:true,
				mode : 'local'
			}]
		},{
			layout:'form',
			labelWidth:60,
			width:220,
//			bodyStyle:'padding:5px 0px 0px 0px',
			border:false,
			items:[{
				xtype:'combo',
				id : 'reportTypeCombo',
				name : 'reportTypeCombo',
				triggerAction: 'all',
				width:120,
				editable:false,
				store : reportTypeStore,
				valueField : 'value',
				displayField : 'displayField',
				fieldLabel:'报表周期',
				forceSelection:true,
				mode : 'local'
			},{
				xtype:'combo',
				id : 'creatorCombo',
				name : 'creatorCombo',
				triggerAction: 'all',
				store : creatorStore,
				width:120,
				valueField : 'userId',
				displayField : 'userName',
				editable : false,
				fieldLabel:'创建人',
				mode : 'local'
			}]
		},{
			layout:'form',
			width:280,
			labelWidth:60,
//			bodyStyle:'padding:5px 0px 0px 0px',
			border:false,
			items:[{
				xtype : 'compositefield',
				fieldLabel : '起始日期',
				defaults : {flex : 2},
				items : [ {
					xtype : 'textfield',
					id : 'startDate',
					name : 'startDate',
					width : 130,
					value : lastWeekStr,
					cls : 'Wdate',
					listeners : {
						'focus' : function() {
							WdatePicker({
								el : "startDate",
								isShowClear : false,
								readOnly : true,
								dateFmt : 'yyyy-MM-dd',
								autoPickDate : true,
								maxDate : '%y-%M-%d',
								errDealMode : 1
							});
							this.blur();
						}
					}
				}, {
					xtype : 'button',
					id : 'clearStartDate',
					name : 'clearStartDate',
					width:50,
					text : '清空',
					handler : function() {
						Ext.getCmp("startDate").setValue("");
					}
				} ]
			},{
				xtype : 'compositefield',
				fieldLabel : '结束日期',
				defaults : {
					flex : 2
				},
				items : [ {
					xtype : 'textfield',
					id : 'endDate',
					name : 'endDate',
					width : 130,
					value : todayStr,
					cls : 'Wdate',
					listeners : {
						'focus' : function() {
							WdatePicker({
								el : "endDate",
								isShowClear : false,
								readOnly : true,
								dateFmt : 'yyyy-MM-dd',
								autoPickDate : true,
								maxDate : '%y-%M-%d',
								errDealMode : 1
							});
							this.blur();
						}
					}
				}, {
					xtype:'button',
					id : 'clearEndDate',
					name : 'clearEndDate',
					width:50,
					text:'清空',
					handler:function(){
						Ext.getCmp("endDate").setValue("");
					}
				} ]
			}]
		},{
			layout:'form',
			width:220,
			labelWidth:80,
			border:false,
			items:[{
				xtype:'combo',
				id : 'taskNameCombo',
				name : 'taskNameCombo',
				triggerAction: 'all',
				fieldLabel : '任务名称',
				store : taskNameStore,
				width:120,
				valueField : 'taskId',
				displayField : 'taskName',
				mode : 'local'
			},taskType]
		}]
	}]
});

var store = new Ext.data.Store({
	url : 'pm-report!getReportInfoList.action',
//	baseParams: {
//		"pageSize":200
//		},
    reader: new Ext.data.JsonReader({
        totalProperty: 'total',
				root : "rows"
		},[
		   "PM_REPORT_ID","REPORT_NAME","DATA_SRC","PERIOD","EXPORT_TIME","USER_NAME","SIZE","EXCEL_URL","TASK_TYPE"
	])
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect:false});
var cm = new Ext.grid.ColumnModel({
    // specify any defaults for each column
    defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel,
    {
    	id : 'pmReportIdCM',
        header: '报表ID',
        dataIndex: 'PM_REPORT_ID',
        hidden:true,//hidden colunm
        width: 100
    },{
    	id : 'reportNameCM',
        header: '报表名称',
        dataIndex: 'REPORT_NAME',
        width: 150
    },{
    	id : 'TASK_TYPE',
        header: '作业计划对象',
        dataIndex: 'TASK_TYPE',
        width: 150,
        renderer:taskTypeRenderer
    },{
    	id : 'dataSourceCM',
        header: '数据源',
        dataIndex: 'DATA_SRC',
        width: 150,
        renderer: function(v){
        	if(v=='0')
        		return '原始数据';
        	else if(v=='1')
        		return '异常数据';
        }
    },{
        id:'periodCM',
        header: '类型',
        dataIndex: 'PERIOD',
        width: 150,
        renderer: function(v){
        	if(v=='0')
        		return '日报';
        	else if(v=='1')
        		return '月报';
        }
    },{
        id:'exportTimeCM',
        header: '生成时间',
        dataIndex: 'EXPORT_TIME',
        width: 150
    },{
        id:'creatorCM',
        header: '创建人',
        dataIndex: 'USER_NAME',
        width: 150
    },{
        id:'sizeCM',
        header: '文件大小',
        dataIndex: 'SIZE',
        width: 150
    },{
        id:'filePath',
        hidden:true,
        header: '文件路径',
        dataIndex: 'EXCEL_URL',
        width: 150
    }]
});
function taskTypeRenderer(v){
	for(var i=0;i<REPORT_TYPE.length;i++){
		if(v==REPORT_TYPE[i]['key'])
			return REPORT_TYPE[i]['value'];
	}
	return v;
}
var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: 200,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
});

var gridPanel = new Ext.grid.GridPanel({
	id:'gridPanel',
	region:'center',
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    loadMask: {msg: '数据加载中...'},
    selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox  
	viewConfig: {
        forceFit:false
    },
	bbar: pageTool
});

function searchReport(){
	
	var taskId = Ext.getCmp('taskNameCombo').getValue();
	var dataSrc = Ext.getCmp('dataSourceCombo').getValue();
	var reportType = Ext.getCmp('reportTypeCombo').getValue();
	var creator = Ext.getCmp('creatorCombo').getValue();
	var startDate = Ext.getCmp('startDate').getValue();
	var endDate = Ext.getCmp('endDate').getValue();
	var instantOrTask = Ext.getCmp('instantOrTaskCombo').getValue();
	var taskType = Ext.getCmp('taskType').getValue();
	var jsonData = {
		"taskId":taskId,
		"dataSrc":dataSrc,
		"reportType":reportType,
		"creator":creator,
		"startDate":startDate,
		"endDate":endDate,
		"instantOrTask":instantOrTask,
		"taskType":(!!taskType&&taskType!=0)?taskType:ALL_NX_TASK_TYPE
	};

	store.baseParams = {
		"limit":200,
		"reportSearchJsonString":Ext.encode(jsonData)
	};
	store.load();
}

function downloadReport(){
	
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length > 0){
		Ext.Msg.confirm('提示','是否下载选中的报表？',function(r){
			if(r == 'yes'){
				if(cell.length == 1){
					var filePath = cell[0].get("EXCEL_URL");
					window.location.href="download.action?"+Ext.urlEncode({filePath:filePath});
				}else{
					var filePathList = new Array();
					for(var i=0;i<cell.length;i++){
						filePathList.push(cell[i].get("EXCEL_URL"));
					}
					Ext.Ajax.request({ 
					    url: 'pm-report!zipReport.action',
					    method : 'POST',
					    params: {
					    	"filePathList":filePathList
					    },
					    success: function(response) {
					    	top.Ext.getBody().unmask();
					    	var obj = Ext.decode(response.responseText);
					    	if(obj.returnResult == 1){
					    		window.location.href="download.action?"+Ext.urlEncode({filePath:obj.returnMessage});
					    	}
					    	if(obj.returnResult == 0){
				        		Ext.Msg.alert("提示",obj.returnMessage);
				        	}
					    },
					    error:function(response) {
					    	top.Ext.getBody().unmask();
				        	Ext.Msg.alert("异常",response.responseText);
					    },
					    failure:function(response) {
					    	top.Ext.getBody().unmask();
				        	Ext.Msg.alert("异常",response.responseText);
					    }
					});
				}
			}
		});
	}else{
		Ext.Msg.alert('提示','请先选取报表！');
	}
}

//删除报表
function deleteReport(){
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length > 0){
		var pmReportIdList = new Array();
		for(var i=0;i<cell.length;i++){
			pmReportIdList.push(cell[i].get("PM_REPORT_ID"));
		}
		//先判断当前用户是否有删除权限
		Ext.Ajax.request({
			url:'pm-report!preDeleteReport.action',
			type:'post',
			params: {
		    	"pmReportIdList":pmReportIdList
		    },
		    success: function(response) {
		    	top.Ext.getBody().unmask();
		    	var obj = Ext.decode(response.responseText);
		    	if(obj.returnResult == 0){
	        		Ext.Msg.alert("提示",obj.returnMessage);
	        	}
		    	if(obj.returnResult == 1){
		    		if(obj.returnMessage == "noPrivilege"){
		    			Ext.Msg.alert("提示","所选报表没有删除权限！");
		    		}else if(obj.returnMessage == "partPrivilege"){
		    			Ext.Msg.confirm('提示','部分报表没有删除权限，是否删除可删除报表？',function(r){
		    				if(r == 'yes'){
		    					Ext.Ajax.request({ 
		    					    url: 'pm-report!deleteReport.action',
		    					    type: 'post',
		    					    params: {
		    					    	"pmReportIdList":pmReportIdList
		    					    },
		    					    success: function(response) {
		    					    	top.Ext.getBody().unmask();
		    					    	var obj = Ext.decode(response.responseText);
		    					    	if(obj.returnResult == 1){
		    					    		Ext.Msg.alert("提示",obj.returnMessage,function(r){
		    					    			var pageTool = Ext.getCmp('pageTool');
		    					    			if(pageTool){
		    				    					pageTool.doLoad(pageTool.cursor);
		    				    				}
		    					    		});
		    					    	}
		    					    	if(obj.returnResult == 0){
		    				        		Ext.Msg.alert("提示",obj.returnMessage);
		    				        	}
		    					    },
		    					    error:function(response) {
		    					    	top.Ext.getBody().unmask();
		    				        	Ext.Msg.alert("异常",response.responseText);
		    					    },
		    					    failure:function(response) {
		    					    	top.Ext.getBody().unmask();
		    				        	Ext.Msg.alert("异常",response.responseText);
		    					    }
		    					});
		    				}
		    			});
		    		}else if(obj.returnMessage == "allPrivilege"){
		    			Ext.Msg.confirm('提示','是否删除选中的报表？',function(r){
		    				if(r == 'yes'){
		    					Ext.Ajax.request({ 
		    					    url: 'pm-report!deleteReport.action',
		    					    type: 'post',
		    					    params: {
		    					    	"pmReportIdList":pmReportIdList
		    					    },
		    					    success: function(response) {
		    					    	top.Ext.getBody().unmask();
		    					    	var obj = Ext.decode(response.responseText);
		    					    	if(obj.returnResult == 1){
		    					    		Ext.Msg.alert("提示",obj.returnMessage,function(r){
		    					    			var pageTool = Ext.getCmp('pageTool');
		    					    			if(pageTool){
		    				    					pageTool.doLoad(pageTool.cursor);
		    				    				}
		    					    		});
		    					    	}
		    					    	if(obj.returnResult == 0){
		    				        		Ext.Msg.alert("提示",obj.returnMessage);
		    				        	}
		    					    },
		    					    error:function(response) {
		    					    	top.Ext.getBody().unmask();
		    				        	Ext.Msg.alert("异常",response.responseText);
		    					    },
		    					    failure:function(response) {
		    					    	top.Ext.getBody().unmask();
		    				        	Ext.Msg.alert("异常",response.responseText);
		    					    }
		    					});
		    				}
		    			});
		    		}
		    	}
		    },
		    error:function(response) {
		    	top.Ext.getBody().unmask();
	        	Ext.Msg.alert("异常",response.responseText);
		    },
		    failure:function(response) {
		    	top.Ext.getBody().unmask();
	        	Ext.Msg.alert("异常",response.responseText);
		    }
		});
	}else{
		Ext.Msg.alert('提示','请先选取报表！');
	}
}
function preview(){
	var cell = gridPanel.getSelectionModel().getSelections();
	if(cell.length == 0){
		Ext.Msg.alert("提示", "请选择要预览的报表！");
		return;
	}else if(cell.length == 1){
		var url = cell[0].get("EXCEL_URL");
	}else{
		Ext.Msg.alert("提示", "一次只能预览一个报表！");
		return;
	}
//	url = "E:\\LaunchField\\Tomcat\\webapps\\FTSP\\NxReport\\WaveTrans 2014_10_10 04_02_36.xlsx";
	Ext.getBody().mask('请稍等...');
	Ext.Ajax.request({
		url : 'nx-report!getExcelPreview.action',
		params : {
			"paramMap.EXCEL_URL": url
		},
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.decode(response.responseText);
			if (result.returnResult==0) {
				Ext.Msg.alert("提示", result.returnMessage);
			}
			if (result.returnResult==1) {
				var url = result.returnMessage;
				url = "../../../../" + url;
				//console.log(url);
				var previewWindow = new top.Ext.Window({
					id : 'previewWindow',
					title : '报表预览',
					width: Ext.getBody().getWidth()*0.8,
					height: Ext.getBody().getHeight() * 0.9,
					isTopContainer : true,
					modal : true,
					autoScroll : true,
					html : "<iframe  id='报表预览' name = '报表预览'  src = '" + url
							+ "' height='100%' width='100%' frameBorder=0 border=0/>"
				});
				previewWindow.show();
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
Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){parent.Ext.menu.MenuMgr.hideAll();};
	searchReport();
	var win = new Ext.Viewport({
		id:'win',
		layout:'border',
		items:[formPanel,gridPanel],
		loadMask : true,
		renderTo : Ext.getBody()
	});
  });