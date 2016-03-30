var testResultId = 0;
var pageSizeCount = 200 ;
var jsonString = "";
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});

var routeStore = new Ext.data.Store({
	url : 'test-result!getRouteList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, [ "TEST_ROUTE_ID","ROUTE_NAME"])
});
routeStore.load();

testTypedata = [ 
  		['1','触发测试'],
  		['2','手工测试'],
  		['3','周期测试']
]; 
testTypeStore = new Ext.data.SimpleStore({ 
	fields:['id','type'],	 
	data:testTypedata
}); 

evalData = [[-1,'所有数据'],
            [1,'一般预警'],
            [2,'重要预警'],
            [9,'异常数据']];
evalDataStore = new Ext.data.SimpleStore({
	fields:['id','eval'],
	data:evalData
});

var store = new Ext.data.Store({
	 url : 'test-result!queryTestResults.action',
//	 baseParams : {
//		 "jsonString":jsonString
//	 },
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	['TEST_RESULT_ID',
	 'ROUTE_NAME',
	 'TEST_TYPE',
	 'OTDR_WAVE_LENGTH',
	 'OTDR_PLUSE_WIDTH',
	 'OTDR_RANGE',
	 'OTDR_AVE_COUNT',
	 'OTDR_REFRACT_COEFFICIENT',
	 'TRANS_ATTENUATION',
	 'TRANS_OPTICAL_DISTANCE',
	 'REVERSE_ATTENUATION',
	 'EXE_TIME',
	 'EXE_RESULT',
	 'RC_NAME',
	 'TEST_PERIOD',
	 'TEST_ROUTE_ID',
	 'EVALUATION',
	 'EVAL_DESCRIPTION'
	])
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : pageSizeCount,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});


var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel,{
	  dataIndex: 'TEST_RESULT_ID',
	  hidden:true,
	  header: '测试结果ID'
  },
  {
	  dataIndex: 'string',
	  header: '测试结果',
	  renderer : function(value, metadata, record) {
	    var resultId = record.get('TEST_RESULT_ID');
		return ("<a href='#' onclick = getResultDetal();> <img src='../../resource/images/btnImages/qst.PNG'align='absmiddle' width='82' height='18'  /> </a>");
	  }
  },
  {
	  dataIndex: 'ROUTE_NAME',
	  header: '测试路由',
	  width:120
  },
  {
	  dataIndex: 'TEST_TYPE',
	  header: '测试类型',
	  width:70,
	  renderer:transTestType
  },
  {
	  dataIndex: 'TEST_PERIOD',
	  header: '测试周期',
	  width:70,
	  renderer:transTestPeriod
  },
  {
	  dataIndex: 'EXE_TIME',
	  header: '测试时间',
	  width:140
  },
  {
	  dataIndex: 'EXE_RESULT',
	  header: '执行情况',
	  width:60,
	  renderer:transExeResult
  },
  {
	  dataIndex: 'EVALUATION',
	  header: '测试结果评估',
	  width:80,
	  renderer:evaluation
  },
  {
	  dataIndex: 'EVAL_DESCRIPTION',
	  header: '测试评估描述',
	  width:350
  }]
});

var gridPanel = new Ext.grid.GridPanel({
	region : 'center',
	stripeRows : true,
	autoScroll : true,
	frame : false,
	store : store,
	loadMask : true,
	border:true,
	cm : columnModel,
	selModel:checkboxSelectionModel,
	bbar: pageTool,
	tbar : [{
		xtype:"label",
		text:"测试路由：",
		width:100
	},{
	   	 xtype: 'combo',
	     id:'routecombo',
	     name: 'routecombo',
	     mode:"local",
	     store:routeStore,
	     displayField:"ROUTE_NAME",
	     valueField:'TEST_ROUTE_ID',
	     triggerAction: 'all',
	     anchor: '30%'
	},'-',{
		xtype:"label",
		text:"测试类型：",
		width:60
	},{
	   	 xtype: 'combo',
	     id:'testTypecombo',
	     name: 'testTypecombo',
	     mode:"local",
	     store:testTypeStore,
	     displayField:"type",
	     valueField:'id',
	     triggerAction: 'all',
	     anchor: '30%',
	     width: 80
	},'-',{
		xtype:"label",
		text:"评估筛选：",
		width:60
	},{
		xtype: 'combo',
	     id:'evalDatacombo',
	     name: 'evalDatacombo',
	     mode:"local",
	     store:evalDataStore,
	     displayField:"eval",
	     valueField:'id',
	     triggerAction: 'all',
	     anchor: '30%',
	     width: 80
	},'-',{
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		handler : function(){searchResult();}
	},'-',{
		text : '重置',
		icon : '../../resource/images/btnImages/arrow_undo.png',
		handler : function(){
			Ext.getCmp("routecombo").reset();
			Ext.getCmp("testTypecombo").reset();
			Ext.getCmp('evalDatacombo').setValue(-1);
			jsonString = "";
			}
	},'-',{
		text : '删除',
		icon : '../../resource/images/btnImages/delete.png',
		handler : function() {deleteResult();}
	},'-',{
		text : '结果详情',
		icon : '../../resource/images/btnImages/setTask.png',
		handler : function(){getResultDetal();}
	},'-',{
		text : '趋势图',
		icon : '../../resource/images/btnImages/chart.png',
//		privilege : viewAuth,
		handler : function() {
			showResultDiagram();
		}
	}]
});


function searchResult(){
	var routecombo = Ext.getCmp('routecombo').getValue();
	var testTypecombo = Ext.getCmp('testTypecombo').getValue();
	var evalDatacombo = Ext.getCmp('evalDatacombo').getValue();
	jsonString = Ext.encode({
		'TEST_ROUTE_ID':routecombo,
		'TEST_TYPE':testTypecombo,
		'EVALUATION':evalDatacombo,
		'limit':pageSizeCount
	});
	store.baseParams = {"jsonString": jsonString};
	store.load({
		callback: function(r, options, success){   
			if(success){ 
			}else{
			}   
		} 
	});
}

function deleteResult(){
	var cells = gridPanel.getSelectionModel().getSelections();
	if(cells.length > 0){
		var resultIds = '';
		for (var i=0;i<cells.length;i++){
			resultIds += cells[i].get("TEST_RESULT_ID") + ',';
		}
		resultIds=resultIds.substring(0, resultIds.lastIndexOf(','));

		Ext.Msg.confirm('提示', '请确认是否删除选定的测试结果！', function(btn) {
			if (btn == 'yes') {
				Ext.getBody().mask('正在执行，请稍候...');
				var parameters = {
					"jsonString":Ext.encode({'TEST_RESULT_ID':resultIds})
				};
				Ext.Ajax.request({
					url : 'test-result!deleteResult.action',
					method : 'POST',
					params : parameters,
					success : function(response) {
						Ext.getBody().unmask();
						// 刷新结果列表
						searchResult();
					},
					error : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("错误", response.responseText);
					},
					failure : function(response) {
						Ext.getBody().unmask();
						var obj = Ext.decode(response.responseText);
						Ext.Msg.alert("错误", response.responseText);
					}
				});
			}
		});
	}else{
		Ext.Msg.show({
			   title:'错误',
			   msg: '请选择需要删除的测试结果！',
			   buttons: Ext.Msg.CANCEL,
			   icon: Ext.MessageBox.ERROR
			});
	}
}

function transTestType(v){
	if(v == 1){
		return '触发测试';
	}else if(v == 2){
		return '手工测试';
	}else if(v == 3){
		return '周期测试';
	}else {
		return '错误测试类型';
	}
}

function transTestPeriod(v){
	if(v == -1){
		return '不测试';
	}else if(v == '-'){
		return v;
	}else {
		return v+'天';
	}
}

function transExeResult(v){
	if(v == 0){
		return '<span style="color:green;">成功</span>';
	}else {
		return '<span style="color:red;">失败</span>';
	}
}

function evaluation(v){
	switch (v){
	case 0:
	default:
		return '<span style="color:green;">正常</span>';
	case 1:
		return '<span style="color:orange;">一般预警</span>';
	case 2:
		return '<span style="color:red;">重要预警</span>';
	case 9:
		return '<span style="color:brown;">异常数据</span>';
	}
}

function getResultDetal(){
	
	var cells = gridPanel.getSelectionModel().getSelections();
	if(cells.length == 1){
		var resultId = cells[0].get("TEST_RESULT_ID");
		var testRouteId = cells[0].get("TEST_ROUTE_ID");
		
		// store所用数据模型
		var eventStore = new Ext.data.Store({
			url: 'test-result!queryTestEvents.action',
			reader : new Ext.data.JsonReader({
				root : "rows"
			}, 
			['SEQUENCE','EVENT_TYPE','LOCATION','ATTENUATION','REFLECT_VALUE','TEST_RESULT_ID'])
		});
		var parameters =
		{
			"jsonString":Ext.encode({
				'TEST_RESULT_ID':resultId
			})
		}
		eventStore.load({
		    params: parameters,
		    callback: function(records, operation, success) {
		    }
		});
	 
		var OTDRResultPanel = new Ext.Panel({
			id:"OTDRResultPanel", 
		    title: '测试结果图',
		    layout:'form',
		    height:550,
		    width:800,
		    animScroll : true,
		    tbar:[
		          {text:"RTU："},
		          {
		        	xtype: 'textfield',
		            id:'rtuName',
		            name: 'rtuName',
		            allowBlank:true,
		            width:100,
		            anchor: '95%'
		          },
		          {text:"测试时间："},
		          {
		        	xtype: 'textfield',
		            id:'exeTime',
		            name: 'exeTime',
		            allowBlank:true,
		            width:120,
		            anchor: '95%'
		          },
		          {text:"测试路由："},
		          {
		        	xtype: 'textfield',
		            id:'testRoute',
		            name: 'testRoute',
		            allowBlank:true,
		            width:300,
		            anchor: '95%'
		          }
		          ],
		    html : '<iframe  src="resultDetal.jsp?resultId='+resultId +'"  frameborder="0" width="100%" height="100%"/>' 
		});
		var OTDRPropertyPanel = new Ext.TabPanel({
			id:"OTDRPropertyPanel", 
		    activeTab : 0,
		    animScroll : true,
		    height:600,
		    width:350,
		    bodyStyle:'padding:10px 10px 60px 10px',
		    labelAlign: 'center',
		    items:[
		    		{title: '测试参数',layout:'form',id:'OTDRtab',
		    		items:[{
		    	    	layout:'form',
		    	    	border:false,
		    	        items: [{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"测试波长(nm)：",
		    	        		columnWidth:.3
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'otdrWaveLenght',
		    	                name: 'otdrWaveLenght',
		    	                columnWidth:.65
		    	        	}]
		    	        },{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"量    程(Km)：",
		    	        		columnWidth:.3
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'otdrRange',
		    	                name: 'otdrRange',
		    	                columnWidth:.65
		    	        	}]
		    	        },{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"脉冲宽度(ns)：",
		    	        		columnWidth:.3
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'otdrPluseWidth',
		    	                name: 'otdrPluseWidth',
		    	                columnWidth:.65
		    	        	}]
		    	        },{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"测试时长(s)：",
		    	        		columnWidth:.3
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'otdrAveCount',
		    	                name: 'otdrAveCount',
		    	                columnWidth:.65
		    	        	}]
		    	        },{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"折射系数：",
		    	        		columnWidth:.3
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'otdrRefractCoefficient',
		    	                name: 'otdrRefractCoefficient',
		    	                columnWidth:.65
		    	        	}]
		    	        }]
		    	    }]
		            },
		            {title: '测试事件',layout:'form',id:'OSAtab',
		            items:[{
		    	    	layout:'form',
		    	    	border:false,
		    	        items: [{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"全程传输损耗值(dB)：",
		    	        		columnWidth:.4
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'transAttenuation',
		    	                name: 'transAttenuation',
		    	                columnWidth:.25
		    	        	}]
		    	        },{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"全程光学长度值(Km)：",
		    	        		columnWidth:.4
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'transOptDistance',
		    	                name: 'transOptDistance',
		    	                columnWidth:.25
		    	        	}]
		    	        },{
		    	        	layout:'column',
		    	        	border:false,
		    	        	bodyStyle:'padding:10px 10px 2px 10px',
		    	        	items: [{
		    	        		xtype:"label",
		    	        		text:"全程反向损耗值：",
		    	        		columnWidth:.4
		    	        	},{
		    	        		xtype: 'label',
		    	                id:'reverseAttenuation',
		    	                name: 'reverseAttenuation',
		    	                columnWidth:.25
		    	        	}]
		    	        }]
		    	    },{
		    	    	xtype: 'fieldset',
		    	        title: '测试事件表',
		    	        layout:'form',
		    	        width:300,
		    	        items:{               // Results grid specified as a config object
			    	        xtype: 'grid',
			    	        margins:'0 0 0 2', 
			    	        height: 300,
			    	        store: eventStore,
			    	        columns: [ {
				    				header:'编号',
				    				dataIndex:'SEQUENCE',
				    				width:35
		    	       	           },
		    	       	          {
		    	       	        	header:'位置',
				    				dataIndex:'LOCATION',
				    				width:40
		    	       	          },
		    	       	          {
		    	       	        	header:'衰耗',
				    				dataIndex:'ATTENUATION',
				    				width:50
		    	       	          },
		    	       	          {
		    	       	        	header:'反射值',
				    				dataIndex:'REFLECT_VALUE',
				    				width:50
		    	       	          },
		    	       	          {
		    	       	        	header:'事件类型',
				    				dataIndex:'EVENT_TYPE',
				    				width:80
		    	       	          }]      
		    	    			}
		    	    },{
		    	    	xtype : 'button',
		    	    	text : '设为基准值',
		    	    	handler : function saveToBase(){
									var attBase = Ext.getCmp("transAttenuation").text;
									var eventCountBase = eventStore.data.length;
									var parameters =
									{
										"jsonString":Ext.encode({
											'TEST_ROUTE_ID':testRouteId,
											'ATT_BASE':attBase,
											'EVENT_COUNT_BASE' : eventCountBase
										})
									}
									top.Ext.getBody().mask('正在执行，请稍候...');
									Ext.Ajax.request({
									    url: 'test-result!saveToBase.action',
									    params: parameters,
									    success: function(response){
									    	top.Ext.getBody().unmask();
											var obj = Ext.decode(response.responseText);  
											Ext.Msg.alert("提示","基准值保存完成！");
									    }
									});
				    	    	  }
		    	    }]
		        }
		    ]
		});
		var OTDRPanel = new Ext.Panel({
			id:"OTDRPanel", 
		   	layout:'column',
		    height:600,
		    region:"center",
		    items: [{
		    	columnWidth: .7,
		    	items:[OTDRResultPanel]
		    },{
		    	columnWidth: .3,
		    	items:[OTDRPropertyPanel]
		    } ]
		    
		});
		var resultWin = new Ext.Window({
			title: '测试结果信息',
			id:'resultWin',
			width: 1100,
			height: 600,
			closable: false,
			modal: true,
			closeAction: 'hide',
			layout : 'fit',
			bodyPadding : 10,
			border: false,
			buttonAlign:'center',
			items: [OTDRPanel],
			buttons: [{
				xtype : 'button',
				text : '确认',
//				icon : '../../resource/images/btnImages/add.png',
				handler : function(){
					resultWin.close();
					}
			},{
				xtype : 'button',
				text : '导出',
//				icon : '../../resource/images/btnImages/add.png',
				handler : function(){
					var cells = gridPanel.getSelectionModel().getSelections();
					var resultId = cells[0].get("TEST_RESULT_ID");
					var parameters =
					{
						"jsonString":Ext.encode({
							'TEST_RESULT_ID':resultId
						})
					};
					top.Ext.getBody().mask('正在导出报表，请稍候...');
					Ext.Ajax.request({
					    url: 'test-result!exportInfo.action',
					    params: parameters,
					    success: function(response) {
					    	top.Ext.getBody().unmask();
					        var text = Ext.decode(response.responseText);
							var destination={
								"filePath":text.filePath
							};
							window.location.href="download-result!execute.action?"+Ext.urlEncode(destination);
					    },
						failure: function () {
							top.Ext.getBody().unmask();
			    		},
			    		error: function() {
			    			top.Ext.getBody().unmask();
			    		}
					});
				}
			}]
		});
		
		resultWin.show();
		resultWin.center();
		
		Ext.getCmp("rtuName").setValue(cells[0].get("RC_NAME"));
		Ext.getCmp("exeTime").setValue(cells[0].get("EXE_TIME"));
		Ext.getCmp("testRoute").setValue(cells[0].get("ROUTE_NAME"));
		
		Ext.getCmp("otdrWaveLenght").setText(cells[0].get("OTDR_WAVE_LENGTH"));
		Ext.getCmp("otdrRange").setText(cells[0].get("OTDR_RANGE"));
		Ext.getCmp("otdrPluseWidth").setText(cells[0].get("OTDR_PLUSE_WIDTH"));
		Ext.getCmp("otdrAveCount").setText(cells[0].get("OTDR_AVE_COUNT"));
		Ext.getCmp("otdrRefractCoefficient").setText(cells[0].get("OTDR_REFRACT_COEFFICIENT"));

		Ext.getCmp("transAttenuation").setText(cells[0].get("TRANS_ATTENUATION"));
		Ext.getCmp("transOptDistance").setText(cells[0].get("TRANS_OPTICAL_DISTANCE"));
		Ext.getCmp("reverseAttenuation").setText(cells[0].get("REVERSE_ATTENUATION"));
		
	}else{
		Ext.Msg.show({
		   title:'错误',
		   msg: '每次仅可查看一个结果详情！',
		   buttons: Ext.Msg.CANCEL,
		   icon: Ext.MessageBox.ERROR
		});

	}
}

//显示趋势图
function showResultDiagram() {
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length == 1) {
//		var url = getDiagramURL(1, cell[0]);
		var testRouteId = cell[0].data.TEST_ROUTE_ID;
		var href = location.href;
		var index = href.indexOf("jsp") + 4;
		var preUrl = href.substr(0, index);
		var url = preUrl + 'plan/resultDiagram.jsp?testRouteId='+testRouteId;
		parent.parent.addTabPage(url, "光缆测试结果趋势图(周期)");
	} else {
		Ext.Msg.alert('信息', '请选择记录，每次只能选择一条！');
	}
}

Ext.onReady(function(){

	Ext.QuickTips.init();

	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel],
		renderTo : Ext.getBody()
	});
	Ext.getCmp('evalDatacombo').setValue(-1);
	searchResult();
});