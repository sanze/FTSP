var westPanel;
var equipList;
var warningType = 0;
(function() {
	var treeParams = {
		rootId : 0,
		rootType : 0,
		rootText : "FTSP",
		rootVisible : false,
		leafType : 4
	};
	var treeurl = "../../commonManager/tree.jsp?" + Ext.urlEncode(treeParams);
	westPanel = new Ext.Panel({
		id : "westPanel",
		region : "west",
		width : 270,
		autoScroll : true,
		boxMinWidth : 230,
		boxMinHeight : 260,
		forceFit : true,
		collapsed : false, // initially collapse the group
		collapsible : false,
		collapseMode : 'mini',
		split : true,
		html : '<iframe name="tree_panel" id="tree_panel" src ="' + treeurl
				+ '" height="100%" width="100%" frameBorder=0 border=0 />'
	});
})();

function onGetChecked(getFunc){
	equipList = new Array();
	var result=getFunc(["nodeLevel","nodeId"],"top");
    var reader = new Ext.data.ArrayReader({
      fields : [
        {name:'equipType',mapping:"nodeLevel"},
        {name:'equipId',mapping:"nodeId"}]
    });
    obj=reader.readRecords(result);
    var Records=[];    
    for(i=0;i<obj.records.length;i++){
    	var equipValue = {
				'equipType' : obj.records[i].get('equipType'),
				'equipId' : obj.records[i].get('equipId')
		};
    	//alert(obj.records[i].get('equipId'));
		equipList.push(Ext.encode(equipValue));
    }
    reconfigureCoumnModel(type);
	generalDiagram(type);
}

var earlyWarningCombox;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'warningLevelValue'
		}, {
			name : 'warningLevelDisplay'
		} ],
		data : [ [ 0, '全部' ], [ 1, '重要预警' ], [ 2, '次要预警' ], [ 3, '一般预警' ] ]
	});
	earlyWarningCombox = new Ext.form.ComboBox({
		id : 'earlyWarningCombox',
		fieldLabel : '预警等级',
		labelWidth : 60,
		labelSeparator : "：",
		store : store,
		valueField : 'warningLevelValue',
		displayField : 'warningLevelDisplay',
		mode : 'local',
		triggerAction : 'all',
		anchor : '95%',
		value: 0,
		allowBlank : false,
		resizable: true,
		listeners : {
			//过滤数据
			select : function(combo, record, index) {
				/*gridPanel.store.filterBy(function(record, id) {
					if (combo.getValue() != 0) {
						if (record.get('warningLevel') == combo.getValue()) {
							//获取统计数据
							return true;
						} else
							return false;
					}else{
						return true;
					}
				});*/
				warningType = combo.getValue();
				getData(type);
			}
		}
	});
})();

var myPageSize = 200;

var store = new Ext.data.Store({});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : false
});

var warningPanel = new Ext.form.FormPanel({
	id : 'warningPanel',
	bodyStyle:'padding:5px 5px 5px 5px',
	height : 90,
	autoScroll : true,
	collapsible : false,
	collapsed : false,
	labelWidth : 60,
	items : [{
        xtype: 'compositefield',
        fieldLabel: '重要预警',
        items: [{
	        	xtype:'numberfield',
	            id: 'important',
	            name: 'important',
	            width:100,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2,
	    		value:1,
	            anchor:'95%'
        },{
               xtype: 'displayfield',
               value: '%'
        }]
    },{
        xtype: 'compositefield',
        fieldLabel: '次要预警',
        items: [{
	            xtype:'numberfield',
	            id: 'secondary',
	            name: 'secondary',
	            width:100,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2,
	    		value:10,
	            anchor:'95%'
        },{
               xtype: 'displayfield',
               value: '%'
        }]
    },{
        xtype: 'compositefield',
        fieldLabel: '一般预警',
        items: [{
	            xtype:'numberfield',
	            id: 'normal',
	            name: 'normal',
	            width:100,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2,
	    		value:20,
	            anchor:'95%'
        },{
               xtype: 'displayfield',
               value: '%'
        }]
    }]
});

var warningWindow = new Ext.Window({
	id : 'warningWindow',
	title : '预警设置',
	width : 250,
	height : 160,
	isTopContainer : true,
	modal : true,
	autoScroll : true,
	closeAction: 'hide',
	items:[warningPanel],
    buttonAlign:"center",
    buttons: [{
    	text : '确定', 
		handler : modifyWarningValue
    },{
    	text : '取消', 
    	handler: function(){
    		warningWindow.hide();
    	}
    }]
	
//	html : '<iframe src = "addMultiSection.jsp" height="100%" width="100%" frameBorder=0/>'
});

var setWarningButton = new Ext.Button({
	text : '预警设置',
//	privilege:modAuth,
	icon : '../../../resource/images/btnImages/config_add.png',
	handler : function(){
		warningWindow.show();
	}
});

var testButton = new Ext.Button({
	text : 'xxxxx',
//	privilege:modAuth,
	icon : '../../../resource/images/btnImages/config_add.png',
	handler : function(){
		reconfigureCoumnModel(type);
		
		generalDiagram(type);
		parent.parent.addTabPage('../networkAnalysis/availability/ctpNameCustom.jsp','板卡类别名称自定义');
		parent.parent.addTabPage('../networkAnalysis/availability/ctpCategoryCustom.jsp','板卡分类自定义');
	}
});

var importantDisplay = new Ext.form.DisplayField({
	id: 'importantDisplay',
    name: 'importantDisplay'
});

var secondaryDisplay = new Ext.form.DisplayField({
	id: 'secondaryDisplay',
    name: 'secondaryDisplay'
});

var normalDisplay = new Ext.form.DisplayField({
	id: 'normalDisplay',
    name: 'normalDisplay'
});

var oneTbar = new Ext.Toolbar({
	id : 'oneTbar',
	items : [ "预警等级 ：", earlyWarningCombox,"-",setWarningButton,'-',/*testButton,*/
	  importantDisplay,{
		xtype : 'tbspacer',
		width : 10
	},secondaryDisplay,{
		xtype : 'tbspacer',
		width : 10
	},normalDisplay,'-',{
		icon : '../../../resource/images/btnImages/export.png',
		xtype : 'button',
		text:'导出',
		handler:function(){
			exportAvailabilityData(type);
		}
	},'-',{
		icon : '../../../resource/images/btnImages/export.png',
		xtype : 'button',
		text:'link时隙可用率导出',
		hidden:true,
		handler:function(){
			exportLinkAvailabilityData();
		}
	},'-',{
		icon : '../../../resource/images/btnImages/chart.png',
		xtype : 'button',
		text:'数据抽取',
		handler:function(){
			runTransfer(type);
		}
	}]
});

//************************* 查询条件 ****************************
var searchPanel = new Ext.Panel({
	id : 'searchPanel',
	region : "north",
	title : '可用率',
	bodyStyle:'padding:5px 5px 5px 5px',
	height :290,
	autoScroll : true,
	collapsible : true,
	collapsed : false,
	split : true,
	layout:'column',
	items: [new Ext.Spacer({ // 占位
		id : 'chart2',
		height : 250,
		columnWidth: .30
	}),new Ext.Spacer({ // 占位
		id : 'chart1',
		height : 250,
		columnWidth: .70
	})],
	plugins : [ Ext.ux.PanelCollapsedTitle ]
});

function modifyWarningValue(){
	
	var important = Ext.getCmp('important').getValue();
	var secondary = Ext.getCmp('secondary').getValue();
	var normal = Ext.getCmp('normal').getValue();
	
	if((important==null || important=="")||(secondary==null || secondary=="")
			||(normal==null || normal=="")){
		Ext.Msg.alert("提示", "设置参数不符合要求，参数值不可为空。");
		return;
	}
	if((important<1 || important>100)||(secondary<1 || secondary>100)
			||(normal<1 || normal>100)){
		Ext.Msg.alert("提示", "设置参数不符合要求，值在1～100之间，并且重要预警<次要预警<一般预警，请确认。");
		return;
	}
	if(!(important<secondary && secondary<normal)){
		Ext.Msg.alert("提示", "设置参数不符合要求，值在1～100之间，并且重要预警<次要预警<一般预警，请确认。");
		return;
	}
	
	var jsonString = Ext.encode({'TYPE':type,'AVAILABILITY_MJ':important,'AVAILABILITY_MN':secondary,'AVAILABILITY_WR':normal});
	
	Ext.Ajax.request({
		url : 'network!modifyWarningValue.action',
		params : {'jsonString':jsonString},
		method : 'POST',
		success : function(response) {
			Ext.getCmp('importantDisplay').setValue("重要预警"+important+"%");
			Ext.getCmp('secondaryDisplay').setValue("< 次要预警"+secondary+"%");
			Ext.getCmp('normalDisplay').setValue("< 一般预警"+normal+"%");
			warningWindow.hide();
			getData(type);
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}

function init(type){
	
	//判断是否为数字，如果不是不处理
	if(isNaN(type)){
		return;
	}
	var jsonString = Ext.encode({'type':type});

	Ext.Ajax.request({
		url : 'network!searchWarningValue.action',
		params : {'jsonString':jsonString},
		method : 'POST',
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			
			Ext.getCmp('important').setValue(obj.AVAILABILITY_MJ);
			Ext.getCmp('secondary').setValue(obj.AVAILABILITY_MN);
			Ext.getCmp('normal').setValue(obj.AVAILABILITY_WR);
			
			Ext.getCmp('importantDisplay').setValue("重要预警"+obj.AVAILABILITY_MJ+"%");
			Ext.getCmp('secondaryDisplay').setValue("< 次要预警"+obj.AVAILABILITY_MN+"%");
			Ext.getCmp('normalDisplay').setValue("< 一般预警"+obj.AVAILABILITY_WR+"%");
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}

var columns;

//初始化coumnModel type:1 slot type 2:port type:3 ctp type 4:port
function initCoumnModel(){
	columns = [ new Ext.grid.RowNumberer({
		width : 26,
		locked : true
	}), checkboxSelectionModel];
	
	//固定表头创建
	if(type == 1){
		for(var i=0;i<headDisplay.length;i++) {
			if(headDisplay[i]){
				if(headIndex[i] == 'NE_DISPLAY_NAME'){
					columns.push({
						header : headDisplay[i],
						dataIndex : headIndex[i],
						width : 100,
						renderer:function(value,cellmeta,record,rowIndex,columnIndex,store){
							//alert(record.get("BASE_NE_ID"));
							var neId = record.get("BASE_NE_ID");
							var neName = "'"+value+"'";
							return '<a href="javascript:void(0)" onclick="openBayface('+neId+','+neName+')">'+value+'</a>';
						}
					});
				}else{
					columns.push({
						header : headDisplay[i],
						dataIndex : headIndex[i],
						width : 100,
						renderer:colorGrid
					});
				}
				
			}
		};
	}else if(type == 2 || type == 4){//端口
		for(var i=0;i<headDisplay.length;i++) {
			if(headDisplay[i]){
				if(headIndex[i] == 'NE_DISPLAY_NAME'){
					columns.push({
						header : headDisplay[i],
						dataIndex : headIndex[i],
						width : 100,
						renderer:function(value,cellmeta,record,rowIndex,columnIndex,store){
							//alert(record.get("BASE_NE_ID"));
							var neId = record.get("BASE_NE_ID");
							var neName = "'"+value+"'";
							return '<a href="javascript:void(0)" onclick="portDetial('+neId+','+neName+','+type+')">'+value+'</a>';
						}
					});
				}else{
					columns.push({
						header : headDisplay[i],
						dataIndex : headIndex[i],
						width : 100,
						renderer:colorGrid
					});
				}
				
			}
		};
	}else{
		for(var i=0;i<headDisplay.length;i++) {
			if(headDisplay[i]){
				columns.push({
					header : headDisplay[i],
					dataIndex : headIndex[i],
					width : 100,
					renderer:colorGrid
				});
			}
		};
	}
}

function openBayface(neId, neName){
	parent.addTabPage("../viewManager/bayface.jsp?neId="+neId, "网元:"+neName, authSequence);
}


//端口使用详情
function portDetial(neId,neName,type){
  getPortDetial(neId,neName,type);
}


function reconfigureCoumnModel(type){

	initCoumnModel();

	if(type == 3){
		store = new Ext.data.Store({       
		    reader : new Ext.data.JsonReader({
				totalProperty : 'total',
				root : "rows"
			}, headIndex)
		});
		pageTool.bind(store);
		var cm = new Ext.grid.ColumnModel({
			defaults : {
				sortable : true
			},
			columns : columns
		});
	
	  gridPanel.reconfigure(store,cm);
		//获取统计数据
		getData(type);
	}else{
		var paramMap = {};
		paramMap["paramMap.type"] = parseInt(type);
		var params={
				"paramMap":paramMap,
				"modifyList":equipList
		};
		
		Ext.Ajax.request({
			url : 'network!searchAvailabilityHeader.action',
			method : 'POST',
			params : paramMap,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				
				for(var key in obj) {
					columns.push({
						header : obj[key],
						dataIndex : key,
						width : 100,
						renderer:colorGrid
					});
					headIndex.push(key);
				};
				columns.push({
					header : "告警等级",
					dataIndex : "warningLevel",
					width : 100,
					hidden:true
				});
				headIndex.push("warningLevel");
				columns.push({
					header : "网元ID",
					dataIndex : "BASE_NE_ID",
					width : 100,
					hidden:true
				});
				headIndex.push("BASE_NE_ID");
				store = new Ext.data.Store({       
				    reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, headIndex)
				});
				pageTool.bind(store);
				
				var cm = new Ext.grid.ColumnModel({
						defaults : {
							sortable : true
						},
						columns : columns
					});
				
				gridPanel.reconfigure(store,cm);
				//获取统计数据
				getData(type);
			},
			error : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("异常", response.responseText);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("异常", response.responseText);
			}
		});
	}
	
}

function colorGrid(v, m, r) {
	if(v.split('%').length<2){
		return v;
	}
	var value = v.split('%')[0];
	
	var important = Ext.getCmp('important').getValue();
	var secondary = Ext.getCmp('secondary').getValue();
	var normal = Ext.getCmp('normal').getValue();
	
	if(important && secondary && normal){
		if(secondary<=parseInt(value)&&parseInt(value)<normal){
			m.css = 'x-grid-font-blue';
		}
		if(important<=parseInt(value)&&parseInt(value)<secondary){
			m.css = 'x-grid-font-orange';
		}
		if(parseInt(value)<important){
			m.css = 'x-grid-font-red';
		}
	}
	return v;
}

function getData(type){
	if(equipList != null && equipList.length > 0){
		//修改查询条件，查询出相应的数据
	    store.proxy = new Ext.data.HttpProxy({
			url : 'network!searchAvailabilityData.action'
	    });    
		store.baseParams ={"paramMap.type" : type,
	    				   "modifyList":equipList,
	    				   "warningType":warningType,
	    				   "limit":myPageSize};	
		store.load({
			callback : function(r, options, success) {
				if (success) {
					Ext.getBody().unmask();
				} else {
					Ext.Msg.alert('错误', '加载失败！');
				}
			}
		});
	}
}


//1:槽道可用率 2:端口可用率 3:时隙可用率 4:端口可用率
function generalDiagram(type){
	//判断是否为数字，如果不是不处理
	if(isNaN(type)){
		return;
	}
	var chartUrl;
	
	if(type == 3){
		chartUrl = '../../../resource/FusionCharts/Charts/MSColumn2D.swf'; 
	}else{
		chartUrl = '../../../resource/FusionCharts/Charts/Column2D.swf'; 
	}
	//生成综合图表
	generalDiagram4zonghe(type);
	//生成各子数据图表
	generalDiagram4subType(chartUrl,type);
}

//综合图表生成
function generalDiagram4zonghe(type){
	var xmlStr;
	//重新设置type值
	var paramMap = {};
	paramMap["paramMap.type"] = type*10+1;
	var params={
			"paramMap.type":type*10+1,
			"modifyList":equipList
	};
	Ext.Ajax.request({
		url : 'network!generateDiagramXml.action',
		params : params,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result["chart_one"]) {
				xmlStr = result["chart_one"];

				var myChart2 = new FusionCharts(
						"../../../resource/FusionCharts/Charts/Pie2D.swf", "myChartId2", Ext.getCmp('chart2').getWidth(),
						Ext.getCmp('chart2').getHeight());
				myChart2.setDataXML(xmlStr);
				myChart2.render("chart2");
			}
			
			if (result["chart_two"]) {
				xmlStr = result["chart_two"];

				var myChart3 = new FusionCharts(
						"../../../resource/FusionCharts/Charts/Pie2D.swf", "myChartId3", Ext.getCmp('chart3').getWidth(),
						Ext.getCmp('chart3').getHeight());
				myChart3.setDataXML(xmlStr);
				myChart3.render("chart3");
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

//各类型槽道板卡端口图表生成
function generalDiagram4subType(chartUrl,type){

	var xmlStr;
	//重新设置type值
	var paramMap = {};
	paramMap["paramMap.type"] = type*10+2;
	var params={
			"paramMap.type":type*10+2,
			"modifyList":equipList
	};
	
	Ext.Ajax.request({
		url : 'network!generateDiagramXml.action',
		params : params,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result["chart_one"]) {
				xmlStr = result["chart_one"];
				var myChart1 = new FusionCharts(chartUrl, "myChartId1", Ext.getCmp('chart1').getWidth(),
						Ext.getCmp('chart1').getHeight());
				myChart1.setDataXML(xmlStr);
				myChart1.render("chart1");
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

function exportAvailabilityData(type){
	if(store.getCount() == 0){
		Ext.Msg.alert("提示","导出结果为空！");
		return;
	}else{
		gridPanel.getEl().mask("正在导出...");
		
		Ext.Ajax.request({
			url:'network!exportAvailabilityData.action',
			type:"Post",
			params:{"paramMap.type" : type,
				   "modifyList":equipList},
			success:function(response){
				gridPanel.getEl().unmask();
				var obj = Ext.decode(response.responseText);
				if(obj.returnResult == 1 && obj.returnMessage != ""){
					window.location.href = "download!execute.action?"
						+ Ext.urlEncode({"filePath" : obj.returnMessage});
				}else{
					Ext.Msg.alert("提示", "导出失败！");
				}
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("错误",obj.returnMessage);
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				var obj = Ext.decode(response.responseText);
				Ext.Msg.alert("失败",obj.returnMessage);
			}
		});
	}
}


function exportLinkAvailabilityData(){
//	var map={};
	var jsonString = Ext.encode({'flag':'11','linkIds':'1,5','subnetName':'二干SDH'});
//	map["paramMap.flag"] = 11;
//	map["paramMap.linkIds"] = "1,5";
//	map["paramMap.subnetName"] = "二干SDH";
	Ext.Ajax.request({
		url : 'network!exportLinkToExcel.action',
		type : 'POST',
		params : {'jsonString':jsonString},
		success : function(response) {
			gridPanel.getEl().unmask();
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else { 
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}

//各类型槽道板卡端口图表生成
function runTransfer(type){
	Ext.getBody().mask("正在抽取........");
	//重新设置type值
	var paramMap = {};
	var params={
			"paramMap.type":type
	};
	
	Ext.Ajax.request({
		url : 'network!runTransfer.action',
		params : params,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", "抽取成功！");
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}

//网元端口使用详情
function getPortDetial(neId,neName,type){
	var paramMap = {};
	var params={
			"paramMap.neId":neId,
			"paramMap.type":type
	};
	var detailStore = new Ext.data.Store(
	{
	    url : 'network!getPortDetial.action',
		baseParams:params,
		reader: new Ext.data.JsonReader({
	        totalProperty: 'total',
			root : "rows"
	    },['BASE_NE_ID','DISPLAY_NAME','DOMAIN','PTP_TYPE','IS_AVILABLE','NOTE','NE_DISPLAY_NAME'])
	});
    detailStore.load({
		callback: function(r, options, success){
			if(!success){
				var obj = Ext.decode(r.responseText);
	    		Ext.Msg.alert("提示",obj.returnMessage);
			}
		}
	});
	 var detailCheckboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :true});
	 var detailColumnModel = new Ext.grid.ColumnModel({
        // specify any defaults for each column
        defaults: {
            sortable: true,
            forceFit:false
        },
        columns: [new Ext.grid.RowNumberer({
    		width : 26
    	//}),detailCheckboxSelectionModel,{
        }),{
            id: 'BASE_NE_ID',
            header: '网元ID',
            width:(10+12*15),
            dataIndex: 'BASE_NE_ID',
            hidden:true
        },{
            id: 'NE_DISPLAY_NAME',
            header: '网元名称',
			width:(10+12*9),
            dataIndex: 'NE_DISPLAY_NAME'
        },{
            id: 'DISPLAY_NAME',
            header: '端口',
			width:(10+12*8),
            dataIndex: 'DISPLAY_NAME'
        },{
            id: 'DOMAIN',
            header: '业务类型',
			width:(10+12*8),
            dataIndex: 'DOMAIN'
        },{
            id: 'PTP_TYPE',
            header: '端口类型',
			width:(10+12*15),
            dataIndex: 'PTP_TYPE'
        },{
            id: 'IS_AVILABLE',
            header: '可用',
			width:(10+12*15),
            dataIndex: 'IS_AVILABLE'
        },{
            id: 'NOTE',
            header: '摘要',
			width:(10+12*15),
            dataIndex: 'NOTE'
        }]
    });

	var detailGridPanel = new Ext.grid.EditorGridPanel({
		id:"detailGridPanel",
		region:"center",
		stripeRows:true,
		autoScroll:true,
		frame:false,
		cm: detailColumnModel,
		store:detailStore,
		loadMask: true,
		clicksToEdit: 2,//设置点击几次才可编辑  
		//selModel:detailCheckboxSelectionModel ,  //必须加不然不能选checkbox 
		viewConfig: {
	        forceFit:false
	    }
	})
  
var implementWindow=new Ext.Window({
      id:'implementWindow',
      title:'网元端口使用详情',
      width:800,
      height:445,
      isTopContainer : true,
      modal : true,
      autoScroll:true,
   //   html:'<iframe src = '+url+' height="100%" width="100%" frameBorder=0 border=0/>' ,
      layout:'border',
      items : [detailGridPanel],
      buttons: [{
		text: '确定',
		handler: function(b,e){
			b.findParentByType('window').close();
		}
	    }]
   });
implementWindow.show();
}
