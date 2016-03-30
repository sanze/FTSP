/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


//定义全局查询条件
//var queryCondition={'userGroupId':'','userId':'','startTime':'','endTime':''};

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
    },[
	   {name:"userGroupName",mapping:"SYS_USER_GROUP_NAME"},
	   {name:"userName",mapping:"SYS_USER_NAME"},
	   {name:"startTime",mapping:"START_TIME"},
	   {name:"logKeyword",mapping:"LOG_LEYWORD"},
	   {name:"logDescription",mapping:"LOG_DESCRIPTION"}
    ])
});

store.load({
	 params : {//这两个参数是分页的关键，当你点击 下一页 时，这里的值会传到后台,也就是会重新运行：store.load
		    start : 0,   
		    limit : 20
		   },
	callback: function(r, options, success){
		if(success){

		}else{
			var obj = Ext.decode(response.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 20,//每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});



var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		//forceFit : true,
		align:'center'
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),
				{
					id:'SYS_USER_GROUP_NAME',
					header:'组名',
					dataIndex:'SYS_USER_GROUP_NAME',
					width:120					
				},
				{
					id:'SYS_USER_NAME',
					header:'用户名',
					dataIndex:'SYS_USER_NAME',
					width:80					
				},         
				{
					id:'START_TIME',
					header:'事件发生时间',
					dataIndex:'START_TIME',
					width:140					
				}, 
				{
					id:'LOG_LEYWORD',
					header:'日志关键字',
					dataIndex:'LOG_LEYWORD',
					width:80					
				}, 
				{
					id:'LOG_DESCRIPTION',
					header:'日志描述',
					dataIndex:'LOG_DESCRIPTION',
					width:160					
				}
]}		
);

var logCheck = new Ext.grid.GridPanel({
	id : "logCheck",
	region : "center",
	stripeRows : true,
	autoScroll : true,
	frame : false,
	cm : columnModel,
	store : store,
	loadMask : true,
	columnLines:true,
	multiSelect:true,
	clicksToEdit : 2,//设置点击几次才可编辑  
	viewConfig : {
		forceFit : false
	},
	bbar : pageTool,
	tbar :{  height:60,
				padding:'20 20 20 20',
				items:[ {xtype: 'tbspacer', width:20,shadow:false},
	        	'用户组 ',
	           {xtype: 'tbspacer', width:20,shadow:false},
	           {
				xtype : 'combo',
				id : 'userGroupId',
				name : 'userGroupId',
				fieldLabel : '用户组  ',
				mode : "local",
				width : 120,
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
					},
					
					
				}
          	}, 
          	{ xtype: 'tbspacer', width: 60,shadow:false },
              '用户名',
              { xtype: 'tbspacer', width: 20,shadow:false },
	          {
				xtype : 'combo',
				id : 'userId',
				name : 'userId',
				fieldLabel : '用户名  ',
				mode : "local",
				width : 120,
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
        	{ xtype: 'tbspacer', width: 60,shadow:false },
        	'开始时间',
        	{ xtype: 'tbspacer', width: 20,shadow:false },
        	{
        		xtype : 'datefield',
        		width : 120,
                name: 'startDate',
                id: 'startDate',
                format:'Y-m-d',
                vtype: 'daterange',
                endDateField: 'endDate' // id of the end date field
            },
            { xtype: 'tbspacer', width: 10,shadow:false },
            '~',
            { xtype: 'tbspacer', width: 10,shadow:false },
            '结束时间',
            { xtype: 'tbspacer', width: 20,shadow:false },
            {
            	xtype : 'datefield',
            	width : 120,
                name: 'endDate',
                id: 'endDate',
                format:'Y-m-d',
                vtype: 'daterange',
                startDateField: 'startDate' // id of the start date field
            },
            { xtype: 'tbspacer', width: 30,shadow:false },
            '日志关键字',
            { xtype: 'tbspacer', width: 20,shadow:false },
            {
            	xtype: 'textfield',
			    id:'logKeyword',
			    name: 'logKeyword',
			    width: 120,
			    allowBlank:true,
			    maxLength: 50
            },
            { xtype: 'tbspacer', width: 40,shadow:false },
            {
            	xtype: 'button',
                text: "查询",
                width: 35,
                listeners: 
                { 
                	"click": function () {
                		queryLogList();
                	}
                }
            },
            { xtype: 'tbspacer', width: 40,shadow:false },
            {
            	xtype: 'button',
                text: "导出",
                width: 35,
                listeners: 
                { 
                	"click": function () {
                		addChooseNodes();
                	}
                }
            }
            ]
		}
});



//根据下拉框数据判断是否在

//根据查询条件查询日志列表
function queryLogList(){
	var json={start : 0,limit :20};
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

//	console.info(json);
	store.baseParams = json;
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



Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	Ext.Ajax.timeout = 900000;
	//Ext.Msg = top.Ext.Msg;
	
	dealDate();
	
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : logCheck
	});


});
