Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
 
 //指定provider
	Ext.state.Manager.setProvider(   
	    new Ext.state.SessionStorageStateProvider({   
	      expires: new Date(new Date().getTime()+(1000*60*60*24*365))   
	    })   
    );
 
//==========================page=============================
var FAILED=0;
var EVENT_VIEW=1;
var EVENT_EXPORT=2;

var limit=200;
var store = new Ext.data.Store(
{
	url: 'inspect-report!getInspectReportList.action',
	baseParams: {"limit":limit,
	             "inspectTime":"一个月内",
	             "userId":userId
	          //  "taskName":taskName,
	          //  "inspector":inspector
	             },
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },[
	   {name:"reportId",mapping:"INSPECT_REPORT_ID"},
	   {name:"reportName",mapping:"REPORT_NAME"},
	   {name:"time",mapping:"CREATE_TIME"},
	   {name:"status",mapping:"RESULT"},
	   {name:"creator",mapping:"CREATE_USER"},
	   {name:"detial",mapping:"NOTE"}
    ])
});
store.load({
	callback: function(response, success){
		if(!success){
			var obj = Ext.decode(response.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});

/*var date = new Date(); 
var myData1 = [
        ['1','一个月内'],
        ['2','六个月内'],
        ['3','一年内'],
        ['4',date.getYear()],
        ['0','全部']
    ];
    
var searchPeriodStore=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'name'}
	 ]
});
searchPeriodStore.loadData(myData1);*/

var searchPeriodStore = new Ext.data.Store(
{
	url: 'inspect-report!getDateLimitList.action',
	reader: new Ext.data.JsonReader({
	},[
	   {name:'value',mapping:"value"},
	   {name:'name',mapping:"name"}
    ])
});
searchPeriodStore.load({
	callback: function(r, options, success){
		if(success){
		if(r.length>0){
		    Ext.getCmp('inspectTime').setValue(r[0].get('value'));
		}
		}else{
			var obj = Ext.decode(r.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});


/*
//------------------------------查询条件---------------------------------
var formPanel = new Ext.FormPanel({
    frame:false,
    border:false,
  	bodyStyle:'padding:5px 0px 0px 5px;',
    labelWidth: 10,
    labelAlign: 'right',
    items: [{
        layout : "column",
        border:false,
		defaults:{
			layout : "form",
			border:false,
			columnWidth: .05
		},
        items:[{
            columnWidth:.1,
			items:[{
		        //第一列
				xtype: 'combo',
				emptyText: '巡检时间...',
				valueField: 'value',
				displayField: 'name',
				mode: 'local',
				editable: false,
				allowBlank: false,
				store:searchPeriodStore,
				anchor: '100%'
			}]
			},{
			columnWidth:.1,
			items:[{
			//第二列
				xtype: 'combo',
				emptyText: '任务名称...',
				displayField:"name",
				valueField:'value',
				mode: 'local',
				editable:false,
				allowBlank: true,
				store:taskNameStore,
				anchor: '100%'
			}]
			},{
			//第三列
				xtype: 'button',
				id:'search',
				text:'查询',
				style: 'margin:0px 0px 0px 10px;'
			},{
			//第四列
				xtype: 'button',
				id:'delete',
				text:'删除',
				handler: function(){
					var grid=gridPanel;
					deleteReport("","",grid);
				},
				style: 'margin:0px 0px 0px 10px;'
			},{
				//第五列
				xtype: 'button',
				text:'查看',
				style: 'margin:0px 0px 0px 10px;',
				menu:{
					style: {
						overflow: 'visible'     // For the Combo popup
					},
					items: [
					{
						text: '查看巡检报告',
						handler: function(b,e){}
					}, {
						text: '选择查看巡检报告',
						handler: function(b,e){}
					}
					]
				}
			},{
				//第六列
				xtype: 'button',
				text:'导出',
				style: 'margin:0px 0px 0px 10px;',
				menu: {
					style: {
						overflow: 'visible'     // For the Combo popup
					},
					iconCls: 'bmenu',
					items: [
					{
						text: '导出巡检报告',
						handler: function(b,e){}
					}, {
						text: '选择导出巡检报告',
						handler: function(b,e){}
					}
					]
				}
			}
	        ]
    	}]
});*/
var formPanel=[
	{
		xtype: 'label',
		width: 20
	},'-',{
		xtype: 'combo',
		id:'inspectTime',
		name:'inspectTime',
		emptyText: '巡检时间...',
		valueField: 'value',
		displayField: 'name',
		mode: 'local',
		editable: false,
		allowBlank: false,
		store:searchPeriodStore,
		triggerAction: 'all',
		autoWidth:true,
		width:100
	},{
		xtype: 'label',
		width: 10
	},'-',{
		xtype: 'textfield',
		id:'taskName',
		name:'taskName',
		emptyText: '巡检任务名称',
		//displayField:"name",
		//valueField:'value',
		//mode: 'local',
		//editable:false,
		allowBlank: true,
		//store:taskNameStore,
		width:100//,
		//listWidth:(10+12*25)
	},{
		xtype: 'label',
		width: 10
	},'-',{
		xtype: 'textfield',
		id:'inspector',
		name:'inspector',
		emptyText: '创建人',
		//displayField:"name",
		//valueField:'value',
		//mode: 'local',
		//editable:false,
		allowBlank: true,
		//store:taskNameStore,
		width:100//,
		//listWidth:(10+12*25)
	},'-',{
		text:'查询',
        icon:'../../../resource/images/btnImages/search.png',
        privilege:viewAuth,
		style: 'margin:0px 0px 0px 10px;',
		id:'search',
		handler: function(){
		   searchReport();    
		}
	},'-',{
		id:'delete',
		text:'删除',
        icon:'../../../resource/images/btnImages/delete.png',
        privilege:delAuth,
		handler: function(){
			   var selectRecord =gridPanel.getSelectionModel().getSelections(); 
			   if(selectRecord.length == 0)
			   {
			      Ext.Msg.alert("提示","必须选择一或多个巡检报告。");
			   }else{
			      var reportIdList = new Array();
			      for(var i = 0; i< selectRecord.length;i++){
			         reportIdList.push(selectRecord[i].get("reportId"));
			      }
			      deleteReport(reportIdList);
			   }
		},
		style: 'margin:0px 0px 0px 10px;'
	},'-',{
		xtype: 'splitbutton',
		text:'查看',
        icon:'../../../resource/images/btnImages/report.png',
        privilege:viewAuth,
		style: 'margin:0px 0px 0px 10px;',
		handler:function(b,e){
			if(checkSingleSelect()){
				viewOexport(EVENT_VIEW,null);
			}
		},
		menu:{
			style: {
				overflow: 'visible'     // For the Combo popup
			},
			items: [
			{
				text: '查看巡检报告',
				handler: function(b,e){
					if(checkSingleSelect()){
						viewOexport(EVENT_VIEW,null);
					}
				}
			}, {
				text: '选择查看巡检报告',
				handler: function(b,e){
					if(checkSingleSelect()){
						selectItem(b.initialConfig.text,EVENT_VIEW,viewOexport);
					}
				}
			}
			]
		}
	},'-',{
		xtype: 'splitbutton',
		text:'导出',
        icon:'../../../resource/images/btnImages/export.png',
        privilege:actionAuth,
		style: 'margin:0px 0px 0px 10px;',
		handler: function(b,e){
			if(checkSingleSelect()){
				viewOexport(EVENT_EXPORT,null);
			}
		},
		menu: {
			style: {
				overflow: 'visible'     // For the Combo popup
			},
			iconCls: 'bmenu',
			items: [
			{
				text: '导出巡检报告',
				handler: function(b,e){
					if(checkSingleSelect()){
						viewOexport(EVENT_EXPORT,null);
					}
				}
			}, {
				text: '选择导出巡检报告',
				handler: function(b,e){
					if(checkSingleSelect()){
						selectItem(b.initialConfig.text,EVENT_EXPORT,viewOexport);
					}
				}
			}
			]
		}
	}]
//------------------------------数据呈现---------------------------------
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();

var cm = new Ext.grid.ColumnModel({
    // specify any defaults for each column
    defaults: {
        sortable: true // columns are not sortable by default           
    },
    columns: [new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel ,
        {
            id: 'reportId',
            name:'reportId',
            header: 'id',
            dataIndex: 'reportId',
            hidden:true
        },
        {
	        id:'reportName',
	        header   : '巡检报告名称', 
	        width    : (15*12+10), 
	        sortable : true, 
	        dataIndex: 'reportName'
	     },
	     {
	        id:'time',
	        header   : '执行完毕时间', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'time'
	     },
	     {
	        id:'status',
	        header   : '状态', 
	        width    : (10*12+10), 
	        sortable : true, 
	        dataIndex: 'status'
	     },
	     {
	        id:'creator',
	        header   : '创建人', 
	        width    : (5*12+10), 
	        sortable : true, 
	        dataIndex: 'creator'
	     },
	     {
	        id:'detial',
	        header   : '描述', 
	        width    : (15*12+10), 
	        sortable : true, 
	        dataIndex: 'detial'
	     }	
    	]
});



var pageTool = new Ext.PagingToolbar({
	id:'pageTool',
    pageSize: limit,//每页显示的记录值
    store: store,
    displayInfo: true,
    displayMsg : '当前 {0} - {1} ，总数 {2}',
    emptyMsg: "没有记录"
});
/*
var rightMenu = new Ext.ux.grid.RightMenu({ 
	 items : [{ 
			 text : '查看巡检报告', 
			 recHandler : function(record, rowIndex, grid) { 
			 }
		 }, { 
			 text : '导出巡检报告', 
			 recHandler : function(record, rowIndex, grid) { 
			 } 
		 }, { 
			 text : '选择查看巡检报告', 
			 recHandler : function(record, rowIndex, grid) { 
			 } 
		 }, { 
			 text : '选择导出巡检报告', 
			 recHandler : function(record, rowIndex, grid) { 
			 } 
		 }, { 
			 text : '删除', 
			 recHandler : deleteReport
		 }] 
 });*/
var gridPanel = new Ext.grid.GridPanel({
	id:"gridPanel",
	region:"center",
	cm:cm,
    store:store,
    stripeRows : true, // 交替行效果
    loadMask: {msg: '数据加载中...'},
    selModel:checkboxSelectionModel ,  //必须加不然不能选checkbox  
	view: new Ext.grid.GridView({
        forceFit:false
    }),
	bbar: pageTool,
	tbar: formPanel/*,
	plugins: [rightMenu]*/
});

function selectItem(title,flag,callback){
	var checkboxGroup = new Ext.form.CheckboxGroup({
		id:'checkboxGroup',
		xtype: 'checkboxgroup',
		// Put all controls in a single column with width 100%
		style: 'margin-left:30px',
		columns: 1,
		items: [
			{id:'summary',boxLabel: '巡检报告纲要', name: 'report2', checked: true},
			{id:'errorDetail',boxLabel: '异常项明细报告', name: 'report3'},
			{id:'netDetail',boxLabel: '网络层次明细报告', name: 'report4'},
			{id:'engineerDetail',boxLabel: '包机人明细报告', name: 'report5'}//,
			//{id:'...',boxLabel: '。。。', name: 'report6'}
		],
		listeners:{
			'change':
				function(o,checkeds){
					var checkboxAll=o.findParentByType('form').get('checkboxAll');
					if(checkeds.length>=o.initialConfig.items.length){
						if(!checkboxAll.getValue()){
							checkboxAll.suspendEvents(false);
							checkboxAll.setValue(true);
							checkboxAll.resumeEvents();
						}
					}else{
						if(checkboxAll.getValue()){
							checkboxAll.suspendEvents(false);
							checkboxAll.setValue(false);
							checkboxAll.resumeEvents();
						}
					}
				}
		}
	});
	var form={
		xtype: 'form',
		bodyStyle: "padding:10",
		border: false,
		hideLabels: true,
		items:[
			{id:'checkboxAll',xtype: 'checkbox',boxLabel: '全部报告', name: 'report1',
				listeners:{
					'check':
						function(o,checked){
							var checkboxs=checkboxGroup.initialConfig.items;
							var newValues=[];
							for(i=0;i<checkboxs.length;i++)
								newValues.push(checked);
							checkboxGroup.suspendEvents(false);
							checkboxGroup.setValue(newValues);
							checkboxGroup.resumeEvents();
						}
				}
			},
			checkboxGroup,
			{xtype: 'checkbox',boxLabel: '查看低阶告警和低阶性能参数', name: 'report',disabled:true,hidden:true}]
	}
	var selectWindow=new Ext.Window({
        id:'selectItem',
        title:title,
        width:250,
        //height:300,
        isTopContainer : true,
        modal : true,
		resizable: false,
        //autoScroll:true,
//		maximized:true,
		items:[form],
        buttons: [{
			text: '确定',
			handler: function(b,e){
				if(callback){
					var boxs=checkboxGroup.getValue();
					var items=[];
					for(var i=0;i<boxs.length;i++){
						items.push(boxs[i].boxLabel);
					}
					callback(flag,items);
				}
				b.findParentByType('window').close();
			}
		 },{
			text: '取消',
			handler: function(b,e){
				b.findParentByType('window').close();
			}
		}]
		//html:'<iframe src = "editTask.jsp?saveType=0" height="100%" width="100%" frameBorder=0 border=0/>' 
     });
    selectWindow.show();
}

function deleteReport(reportIdList){
	Ext.Msg.confirm('提示','删除巡检报告。</br>将删除所选巡检任务的检查结果，</br></br>确认删除？',
		function(btn){
			if(btn=='yes'){
			
			  var jsonData = {
		       "reportIdList":reportIdList
		      }
			
			  Ext.Ajax.request({
			      url:'inspect-report!deleteInspectReport.action',
			      method:'Post',
			      params:jsonData,
			      success: function(response) {
					  store.reload();
			      },
			      error:function(response) {
			    	  Ext.Msg.alert("错误",response.responseText);
			      },
			      failure:function(response) {
			    	  Ext.Msg.alert("错误",response.responseText);
			      }
			  });
			}
		}
	);
}

function searchReport(){
   
   var inspectTime = Ext.get('inspectTime').dom.value;
   var taskName = Ext.getCmp('taskName').getValue();
   var inspector = Ext.getCmp('inspector').getValue();
   
   var jsonData = {
      "inspectTime":inspectTime,
      "taskName":taskName,
      "inspector":inspector,
      "userId":userId,
      "limit":limit
     }

   store.proxy = new Ext.data.HttpProxy({
	   url : 'inspect-report!getInspectReportList.action'
   });
   store.baseParams = jsonData;  
   store.load({
       callback : function(r, options, success){//回调函数
           		    if(!success){
						var obj = Ext.decode(r.responseText);
			    		Ext.Msg.alert("提示",obj.returnMessage);
					}
	              }
	 });
}

function checkSingleSelect(){
  var selectRecord =gridPanel.getSelectionModel().getSelections(); 
  if(selectRecord.length == 0){
    Ext.Msg.alert("提示","必须选择一个巡检报告。");
    return false;
  }else if(selectRecord.length > 1){
    Ext.Msg.alert("提示","只可以选择一个巡检报告。");
    return false;
  }else {
    return true;
  }
}
function viewOexport(flag,items){
	var path='inspect-report!getReportUrl.action';
//	var preText;
//	if(EVENT_VIEW==flag){
//		preText="查看";
//	}else{
//		preText="导出";
//	}
	var selectRecord =gridPanel.getSelectionModel().getSelections();
	var reportIdList = new Array();
	for(var i = 0; i< selectRecord.length;i++){
		reportIdList.push(selectRecord[i].get("reportId"));
	}
	if(items === null || items === undefined ){
		items=["巡检报告纲要","异常项明细报告","网络层次明细报告","包机人明细报告"];
	}
	var params={"operation":flag,"reportIdList":reportIdList,selectList:items};
	Ext.Ajax.request({
		url : path,
		params: params,
		method : 'POST',
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
	    	if(result&&(FAILED==result.returnResult)){
	    		Ext.Msg.alert("提示",result.returnMessage);
	    	}else{
	    		if(EVENT_VIEW==flag){
	    			var param={basePath:basePath,files:result.returnMessage};//
	    			var win=window.open("main.htm?"+Ext.urlEncode(param),"",'top=0,left=0,status=no,scrollbars=yes,menubar=no,toolbar=no,location=no');
	    			win.resizeTo(win.screen.availWidth,win.screen.availHeight);
	    			window.open('','_self','');
	    		}else{
	    			var destination={
						"filePath":result.returnMessage
					};
					window.location.href="download.action?"+Ext.urlEncode(destination);
	    		}
			}
		},
		failure : function(response) {
			Ext.Msg.alert("提示", "未知错误"+
					"<BR>Status:"+response.statusText||"unknow");
		},
		error : function(response) {
			Ext.Msg.alert("提示", "未知错误"+
					"<BR>Status:"+response.statusText||"unknow");
		}
	});
}

//-----------------------------------------init the page--------------------------------------------
Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	Ext.Ajax.timeout=900000; 
    var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [gridPanel],
        renderTo : Ext.getBody()
    });
  });