/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


//定义全局查询条件
//var queryCondition={'userGroupId':'','userId':'','startTime':'','endTime':''};
var qWidth=Ext.getBody().getWidth();
//获取用户组下拉框的数据
var userGroupStore = new Ext.data.Store({
	url : 'user-management!getUserGroup.action',
	baseParams : {
		"userGroupId" : "0"
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "GROUP_NAME", "SYS_USER_GROUP_ID" ])
});

userGroupStore.load({
	callback : function(r, options, success) {
		if (success) {
			var groupCount = userGroupStore.getCount();
			if(groupCount!=null){
				for(var j=0;j<groupCount;j++){
					    if(userGroupStore.getAt(j).get("SYS_USER_GROUP_ID")=='0'){
					    	userGroupStore.remove(userGroupStore.getAt(j));
					    	return;
					    }
		   		}
			}
		} else {
			Ext.Msg.alert('错误', '查询失败！请重新查询');
		}
	}
});

//根据用户组id获取用户下拉框的数据
var userStore = new Ext.data.Store({
	//1代表查
	url : '',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["SYS_USER_ID","USER_NAME"])
});


//获取日志列表数据
var store = new Ext.data.Store({
	url: 'log-management!searchLogList.action',
	reader: new Ext.data.JsonReader({
        totalProperty: 'total',
    	root : "rows"
    },["USER_GROUP_NAME","USER_NAME","CREATE_TIME","OPERATION"
    ])
});

store.addListener({
	beforeload:function(store,records,options){
		var json={start : 0,limit :200};
		var groupId=Ext.getCmp('userGroupId').getValue();
		var userId=Ext.getCmp('userId').getValue();
		var startDate=Ext.getCmp('startDate').formatDate(Ext.getCmp('startDate').getValue());
		var endDate=Ext.getCmp('endDate').formatDate(Ext.getCmp('endDate').getValue());
		var logKeyword=Ext.getCmp('logKeyword').getValue();
		if(groupId!=null && groupId!=''){
			if(Ext.getCmp('userGroupId').findRecord('SYS_USER_GROUP_ID',groupId)==undefined){//用户手输的
				json.userGroupName=groupId;
			}else{//用户从下拉框选的
				json.userGroupId=groupId;
			}
		}
		if(userId!=null && userId!=''){
			if(Ext.getCmp('userId').findRecord('SYS_USER_ID',userId)==undefined ){//用户手输的
				json.userName=userId;
			}else{//用户从下拉框选的
				json.userId=userId;
			}
		}	
			
		if(startDate!=null && startDate!=""){
			json.startDate=startDate;
			if(endDate==null && endDate==""){
				//json.endDate=new Date();
			}
		}
		if(endDate!=null && endDate!=""){
			json.endDate=endDate;
		}
		if(logKeyword!=null && logKeyword!=""){
			json.logKeyword=logKeyword;
		}
		store.baseParams = json;
	}
});


var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,//每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});



var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		//forceFit : true,
		align:'left'
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),
				{
					id:'USER_GROUP_NAME',
					header:'组名',
					dataIndex:'USER_GROUP_NAME',
					width:120					
				},
				{
					id:'USER_NAME',
					header:'用户名',
					dataIndex:'USER_NAME',
					width:80					
				},         
				{
					id:'CREATE_TIME',
					header:'事件发生时间',
					dataIndex:'CREATE_TIME',
					width:140					
				}, 
				{
					id:'OPERATION',
					header:'描述',
					dataIndex:'OPERATION',
					width:180					
				}
				]}		
);



var logCheck = new Ext.grid.GridPanel({
	id : "logCheck",
	//renderTo:'logCheckDiv',
	region : "center",
	stripeRows : true,
	autoScroll : false,
	frame : false,
	cm : columnModel,
	store : store,
	height : Ext.getBody().getHeight(),
	width:Ext.getBody().getWidth()*0.8,
	loadMask : true,
	columnLines:true,
	multiSelect:true,
	border:true,
	clicksToEdit : 2,//设置点击几次才可编辑  
	viewConfig : {
		forceFit : false
	},
	
	bbar : pageTool,
	tbar :{items:['-',
	        	'用户组 ：',
	           {
				xtype : 'combo',
				id : 'userGroupId',
				name : 'userGroupId',
				fieldLabel : '用户组  ',
				mode : "local",
				width : qWidth*0.096,
				value:'',
				store : userGroupStore,
				displayField : "GROUP_NAME",
				valueField : 'SYS_USER_GROUP_ID',
				triggerAction : 'all',
				anchor : '95%',
				listeners : {
					select : function(combo, record, index) {
						var userGroupId = Ext.getCmp('userGroupId').getValue();
						Ext.getCmp('userId').setValue('');
						//加载网元同步列表
						var jsonData = {
							"userGroupId" : userGroupId,
							"limit" : 200
						};
						userStore.proxy = new Ext.data.HttpProxy({
							url : 'user-management!getUserListByGroupId.action'
						});
						userStore.baseParams = jsonData;
						userStore.load();
					}
				}
          	}, 
          	'-',
              '用户名：',
	          {
				xtype : 'combo',
				id : 'userId',
				name : 'userId',
				fieldLabel : '用户名  ',
				mode : "local",
				width : qWidth*0.096,
				//value:'全部',
				store : userStore,
				displayField : "USER_NAME",
				valueField : 'SYS_USER_ID',
				triggerAction : 'all',
				anchor : '95%',
				listeners : {
					select : function(combo, record, index) {
						
						
					}
				}
        	}, 
        	'-',
        	'开始时间：',
        	{
        		xtype : 'datefield',
        		width : qWidth*0.096,
                name: 'startDate',
                id: 'startDate',
                format:'Y-m-d',
                vtype: 'daterange',
                endDateField: 'endDate' // id of the end date field
            },
            '~',
            '结束时间：',
            {
            	xtype : 'datefield',
            	width : qWidth*0.096,
                name: 'endDate',
                id: 'endDate',
                format:'Y-m-d',
                vtype: 'daterange',
                startDateField: 'startDate' // id of the start date field
            },
           '-',
            '日志关键字：',
            {
            	xtype: 'textfield',
			    id:'logKeyword',
			    name: 'logKeyword',
			    width: qWidth*0.08,
			    allowBlank:true,
			    maxLength: 50
            },
           '-',
            {
            	xtype: 'button',
                text: "查询",
                icon : '../../../resource/images/btnImages/search.png',
                width: qWidth*0.024,
                listeners: 
                { 
                	"click": function () {
                		(function(){
                			queryLogList();
        				}).defer(500);
                	}
                }
            },
            '-',
            {
            	xtype: 'button',
                text: "导出",
                icon : '../../../resource/images/btnImages/export.png',
                width: qWidth*0.024,
                listeners: 
                { 
                	"click": function () {
                		exportLog();
                	}
                }
            }
            ]
		}
});

var centerPanel = new Ext.Panel({
	region : 'center',
	autoScroll : true,
	bodyStyle:'overflow-x:hidden;',
	layout : 'form',
	border:true,
	items : [logCheck]
});

//根据下拉框数据判断是否在

//根据查询条件查询日志列表
function queryLogList(){
	
	store.load();
	
	
}


//日期处理
function dealDate(){
	Ext.apply(Ext.form.VTypes, {
		daterange : function(val, field) {
			var date = field.parseDate(val);
			if (!date) {
				return;
			}
			if (field.startDateField && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax.getTime()))) {
				var start = Ext.getCmp(field.startDateField);
				start.setMaxValue(date);
				start.validate();
				this.dateRangeMax = date;
			} else if (field.endDateField && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
				var end = Ext.getCmp(field.endDateField);
				end.setMinValue(date);
				end.validate();
				this.dateRangeMin = date;
			}
			return true;
		}
	});
}


//获取隐藏的所有列
function getGridHiddenColomn(){
	var count=columnModel.getColumnCount();
	var columns='';
	for(var i=0;i<count;i++){
		var t=columnModel.isHidden(i);
		if(t){
			columns+=columnModel.getDataIndex(i);
				columns+=',';
		}
	}
	columns=columns.substring(0,columns.length-1);
	return columns;
}

//导出日志
function exportLog(){
	var hiddenColoumms=getGridHiddenColomn();
	var json={start:pageTool.cursor,limit :200,'hiddenColoumms':hiddenColoumms};
	var groupId=Ext.getCmp('userGroupId').getValue();
	var userId=Ext.getCmp('userId').getValue();
	var startDate=Ext.getCmp('startDate').formatDate(Ext.getCmp('startDate').getValue());
	var endDate=Ext.getCmp('endDate').formatDate(Ext.getCmp('endDate').getValue());
	var logKeyword=Ext.getCmp('logKeyword').getValue();
	if(groupId!=null && groupId!=''){
		if(Ext.getCmp('userGroupId').findRecord('SYS_USER_GROUP_ID',groupId)==undefined){//用户手输的
			json.userGroupName=groupId;
		}else{//用户从下拉框选的
			json.userGroupId=groupId;
		}
	}
	if(userId!=null && userId!=''){
		if(Ext.getCmp('userId').findRecord('SYS_USER_ID',userId)==undefined ){//用户手输的
			json.userName=userId;
		}else{//用户从下拉框选的
			json.userId=userId;
		}
	}	
		
	if(startDate!=null && startDate!=""){
		json.startDate=startDate;
	}
	if(endDate!=null && endDate!=""){
		json.endDate=endDate;
	}
	if(logKeyword!=null && logKeyword!=""){
		json.logKeyword=logKeyword;
	}
	window.location.href=  "log-management!downloadLogManage.action?" + Ext.urlEncode(json);
}


Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	//Ext.Msg = top.Ext.Msg;
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
	dealDate();
	
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : logCheck
	});
	var today = new Date();
	Ext.getCmp("endDate").setValue(today);
	today.setMonth(today.getMonth()-1);
	Ext.getCmp("startDate").setValue(today);
	
	
	store.load({
		 params : {//这两个参数是分页的关键，当你点击 下一页 时，这里的值会传到后台,也就是会重新运行：store.load
			    start : 0,   
			    limit : 200,
			    startDate:Ext.getCmp('startDate').formatDate(Ext.getCmp('startDate').getValue()),
				endDate:Ext.getCmp('endDate').formatDate(Ext.getCmp('endDate').getValue())
			   },
		callback: function(r, options, success){
			if(success){
				
			}else{
	    		Ext.Msg.alert("提示",'导入失败,请重试!');
			}
		}
	});
	
	
});
