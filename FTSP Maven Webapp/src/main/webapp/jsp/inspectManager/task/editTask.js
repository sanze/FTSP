/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//全局变量定义
var periodType;
var periodTime;
var createMonth;
var createDay;
var createTime;
var startTime1;
var nextTime1 = "";
var privilegeParamId;
var inspectItemParamId;
var nextTimeValue;
/*-----------------------------------巡 检 设 备---------------------------------------------*/
//==================For the Tree====================
var treeParams={
  leafType:leafType//,
    //checkModel:"single",
    //onlyLeafCheckable:false
};
var treeurl="../../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);

var westPanel = new Ext.Panel({
	title:"",
	id:"westPanel",
	region:"west",
	width: "25%",
	autoScroll:true,
	forceFit:true,
	collapsed: false,   // initially collapse the group
    collapsible: false,
    collapseMode: 'mini',
    split:true,
    html:'<iframe id="tree_panel" name="tree_panel" src ="'+treeurl+'" height="100%" width="100%" frameBorder=0 border=0/>'
});

/*var store = new Ext.data.ArrayStore({
    fields: [
           {name:'equipType'},{name:'equipId'},{ name: 'equipFullName'}
        ]
});*/

//巡检设备初始化加载
var store = new Ext.data.Store({
  url: 'inspect-task!initInspectEquip.action',
  reader: new Ext.data.JsonReader({
        totalProperty: 'total',
		root : "rows",
        fields : [
          {name:'equipType',mapping:"TARGET_TYPE"},
          {name:'equipId',mapping:"TARGET_ID"},
          {name: 'equipFullName',mapping:"DISPLAY_NAME"}]})
    
});

//添加巡检设备
/*function onGetChecked(getFunc){

	var result=getFunc(null,"top");
	var inspectEquipList = new Array();

	for(var i=0;i<result.length;i++){
	 var inspectEquipName;
	 var currentNode;
	 var currentNameString;
	 var father;
	 var fatherLevel;

	 var equipArray = new Array();
	 //当前节点
	 currentNode = result[i];
	 inspectEquipLevel = currentNode.attributes["nodeLevel"];
	 currentNameString = currentNode.attributes["text"];
     
     father = currentNode.parentNode;
     fatherLevel = father.attributes["nodeLevel"];
     //判断当前节点是否为根节点
     if(fatherLevel == 0){
        //一级节点
        inspectEquipName = result[i].attributes["text"];
     }else{
        currentNode = currentNode.parentNode;
        currentNameString = currentNode.attributes["text"]+ "：" + currentNameString;
        father = currentNode.parentNode;
        fatherLevel = father.attributes["nodeLevel"];
        if(fatherLevel == 0){
          //二级节点
          inspectEquipName = currentNameString;
        }else{
            currentNode = currentNode.parentNode;
            currentNameString = currentNode.attributes["text"]+ "：" + currentNameString;
	        father = currentNode.parentNode;
	        fatherLevel = father.attributes["nodeLevel"];
	        if(fatherLevel == 0){
	          //三级节点
	          inspectEquipName = currentNameString;
	        }else{
	            currentNode = currentNode.parentNode;
	            currentNameString = currentNode.attributes["text"]+ "：" + currentNameString;
		        father = currentNode.parentNode;
		        fatherLevel = father.attributes["nodeLevel"];
		        if(fatherLevel == 0){
		          //四级节点
		          inspectEquipName = currentNameString;
	            }else{
	              Ext.Msg.alert("提示","巡检设备只能选到网元！");
	            }
	        }
        }
     }
     equipArray.push(result[i].attributes["nodeLevel"]);
     equipArray.push(result[i].attributes["nodeLevel"]+"_"+result[i].attributes["nodeId"]); 
     equipArray.push(inspectEquipName);     
     inspectEquipList.push(equipArray);
     
	}
    store.loadData(inspectEquipList);
}*/

function onGetChecked(getFunc){
	var pathParam="path"+CommonDefine.NameSeparator+"text";
	var result=getFunc(["nodeLevel","nodeId",pathParam],"top");
    var reader = new Ext.data.ArrayReader({
      fields : [
        {name:'equipType',mapping:"nodeLevel"},
        {name:'equipId',mapping:"nodeId"},
        {name: 'equipFullName',mapping:pathParam}]
    })
    obj=reader.readRecords(result);
    var Records=[];
    for(i=0;i<obj.records.length;i++){
      var recordIndex=store.findBy(function(record,id){
        if( record.get('equipType')==obj.records[i].get('equipType')&&
            record.get('equipId')==obj.records[i].get('equipId')){
            return true;
        }
      });
      if(recordIndex==-1){
        Records.push(obj.records[i])
      }
    }
    
    store.add(Records);
}

var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel, {
            id: 'equipType',
            header: 'type',
            dataIndex: 'equipType',
            hidden:true
        },{
            id: 'equipId',
            header: 'id',
            dataIndex: 'equipId',
            hidden:true
        },{
			id : 'equipFullName',
			header : '设备',
			dataIndex : 'equipFullName'
	}]
});
/*var rightMenu = new Ext.ux.grid.RightMenu({ 
	 items : [{ 
		 text : '删除', 
		 recHandler : function(record, rowIndex, grid) { 
		 }
	 }]
 }); */
var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	title:'',
	cm : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	},
	tbar : 	["巡检设备","->",{
		text : '删除',
		id:'deleteEquip',
		name:'deleteEquip',
		icon : '../../../resource/images/btnImages/delete.png',
		handler : function() {
		    var records = gridPanel.getSelectionModel().getSelections();
		    var len = records.length;
		    if(len <= 0){
		       parent.Ext.Msg.alert("提示","请选择需要删除的设备！");
		    }else{
		       for(var i = 0;i<len;i++ ){
			        gridPanel.store.remove(records[i]);
			   }
		    }			
		}
	}]/*,
	plugins: [rightMenu]*/
});

/*-----------------------------------------------操作权限组-----------------------------------------*/
var privilegeStore = new Ext.data.Store({
	url : 'inspect-task!getPrivilegeGroupList.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [
         {name:"groupId",mapping:"SYS_USER_GROUP_ID"},
         {name:"groupName",mapping:"GROUP_NAME"}
      ])
});
privilegeStore.load({
    callback: function(r, options, success){
		if(!success){
			var obj = Ext.decode(r.responseText);
    		Ext.Msg.alert("提示",obj.returnMessage);
		}
	}
});

var privilegeCKSM = new Ext.grid.CheckboxSelectionModel();
var privilegeCM = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	},
	columns : [new Ext.grid.RowNumberer({
		width : 26
	}), privilegeCKSM, {
		id : 'groupId',
		header : 'ID',
		dataIndex : 'groupId',
		width : 100,
		hidden : true
	},{
		id : 'groupName',
		header : '组名',
		dataIndex : 'groupName'
	}]
});
var privilegeGrid = new Ext.grid.GridPanel({
	id : "privilegeGrid",
	title:'',
	height:150,
	cm : privilegeCM,
	sm : privilegeCKSM,
	store : privilegeStore,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : privilegeCKSM, // 必须加不然不能选checkbox
	viewConfig : {
		forceFit : true
	}
});
/*-----------------------------------------------巡检任务详细信息-----------------------------------------*/
var eastPanel = new Ext.form.FormPanel({
	title:"",
	id:"eastPanel",
	region:"east",
	bodyStyle:'padding:10px 15px 10px 15px;',
	width: "40%",
	autoScroll:true,
    collapsible: false,
	labelWidth: 60,
//	labelAlign: 'right',
	fbar: [{
        text: '确定',
        id:'ok',
        name:'ok',
        handler: function(){
          var taskName = Ext.getCmp('taskName').getValue();
          if(taskName == ""){
              Ext.Msg.alert("提示","任务名称必填。");
          }else if(nextTime1 == ""){
              Ext.Msg.alert("提示","必须设定周期。");
          }else{
	          Ext.Ajax.request({
				 url:'inspect-task!checkTaskNameExist.action',
				 method:'post',
				 params:{'taskName':taskName,
				         'inspectTaskId':inspectTaskId},
				 success:function(response,opts){
					 var obj=Ext.decode(response.responseText);
					 if(obj.exit){
					   Ext.Msg.alert("提示","有相同名称的巡检任务。名称不可重复。");
					 }else{
					   addTask(1);
					 }
					 },
				 failure: function(response,opts){
				     var obj=Ext.decode(response.responseText);
				     Ext.Msg.alert("提示",obj.returnMessage);   
				 }
			  });
          }
          
		}
     },{
        text: '取消',
        id:'cancel',
        name:'cancel',
        handler: function(){
            // 关闭修改任务信息窗口
			var window = parent.Ext.getCmp('addWindow');
			if (window) {
				window.close();
			}
        }
    },{
        text: '应用',
        id:'apply',
        name:'apply',
        handler: function(){
          var taskName = Ext.getCmp('taskName').getValue();
          if(taskName == ""){
              Ext.Msg.alert("提示","任务名称必填。");
          }else if(nextTime1 == ""){
              Ext.Msg.alert("提示","必须设定周期。");
          }else{
	          Ext.Ajax.request({
				 url:'inspect-task!checkTaskNameExist.action',
				 method:'post',
				 params:{'taskName':taskName,
				         'inspectTaskId':inspectTaskId},
				 success:function(response,opts){
					 var obj=Ext.decode(response.responseText);
					 if(obj.exit){
					   Ext.Msg.alert("提示","有相同名称的巡检任务。名称不可重复。");
					 }else{
					   addTask(0);
					 }
					 },
				 failure: function(response,opts){
				     var obj=Ext.decode(response.responseText);
				     Ext.Msg.alert("提示",obj.returnMessage);   
				 }
			  });
		  }
        }
    },{xtype:'label',width:'15px'}],
	defaults: {
		anchor: '95%',
		labelStyle:"margin-bottom:10px;",
		style:"margin-bottom:10px;"
	},
	items:[{
		xtype: 'hidden',
		fieldLabel: '任务ID',
		disabled: true
	},{
		xtype: 'textfield',
		id:"taskName",
		name:"taskName",
		fieldLabel: '任务名称',
		allowBlank: false,
        blankText: "必填",
        validationEvent: 'blur',
        invalidText: '任务名重复',
        sideText: '<span style="color:red">*</span>',
      //  validateOnBlur: true,
      //  validationDelay:2000,
      //  validateOnChange: false,
        validator:function(val){
			var vastatic = false;
			var validator = this;
            var error = true;
			Ext.Ajax.request({
			 url:'inspect-task!checkTaskNameExist.action',
			 method:'post',
			 scope: validator,
			 params:{'taskName':val,
			         'inspectTaskId':inspectTaskId},
			 success:function(response,opts){
				 var obj=Ext.decode(response.responseText);
				 if(obj.exit){
				  vastatic = true;
				  Ext.Msg.alert("提示","有相同名称的巡检任务。名称不可重复。");
				 }else{
				  vastatic = false;
				 }
				 },
			 failure: function(response,opts){
			     var obj=Ext.decode(response.responseText);
			     Ext.Msg.alert("提示",obj.returnMessage);   
			 }
			 });
			//  return vastatic;
			},
		listeners: {
             focus: {
                 fn: function () { this.clearInvalid(); }
             }
         }
	},{
		xtype: 'textarea',
		id:"taskDescription",
		name:"taskDescription",
		fieldLabel: '任务描述',
		value: ''
	},{
		xtype: 'checkboxgroup',
		id:"taskItem",
		name:"taskItem",
		fieldLabel: '巡检项目',
		// Distribute controls across 3 even columns, filling each row
		// from left to right before starting the next row
		columns: 2,
		items: [
			{boxLabel: '设备基础数据', id:'1', name: '1', checked: true},
			{boxLabel: '性能参数', id:'2', name: '2', checked: true},
			//{boxLabel: '物理量', id:'3', name: '3', checked: true},
			{boxLabel: '设备告警', id:'4', name: '4', checked: true},
			{boxLabel: '网元时间检查', id:'5', name: '5', checked: false, disabled: true},
			{boxLabel: '保护设置和状态检查', id:'6', name: '6', checked: false},
			{boxLabel: '时钟设置和状态检查', id:'7', name: '7', checked: false}/*,
			{boxLabel: '网管状态',id:'8', name: '8', checked: false}*/
		]
	},{
		xtype: 'fieldset',
		title: '巡检周期',
		labelWidth: 80,
		bodyStyle:'padding: 0px 10px 0px 10px',
		items:[{
			xtype: 'compositefield',
			fieldLabel: '巡检周期',
			items: [
				{
					xtype     : 'textfield',
					id        : 'cycleField',
					name      : 'cycleField',
					disabled  : true,
					width     : '70%'
				},
				{
					xtype     : 'button',
					id        : 'setButton',
					name      : 'setButton',
					text      : '设定',
					handler   : function(){
					var handUp = Ext.getCmp('handUp').checked;
					setWindow.show();
					}
						}
					]
		},{
			xtype: 'compositefield',
			fieldLabel: '下次执行时间',
			items: [
				{
					xtype     : 'textfield',
					id        : 'nextTime',
					name      : 'nextTime',
					disabled  : true,
					width     : '70%'
				},{
					xtype: 'checkbox',
					id:"handUp",
					name:"handUp",
					boxLabel: '挂起',
					checked:false
				}
			]
		}]
	},{
		xtype: 'fieldset',
		title: '操作权限组',
		items:[privilegeGrid]
	}]
});

/*-----------------------------------------------周期设定窗口-----------------------------------------*/
var Data_createType=[
	['5','每年'],
	['4','每季'],
	['3','每月']
];
var Date_QuarterMonth=[
	['1','第一个月'],
	['2','第二个月'],
	['3','第三个月']
];
var Date_Month=[
	['1','1月'],
	['2','2月'],
	['3','3月'],
	['4','4月'],
	['5','5月'],
	['6','6月'],
	['7','7月'],
	['8','8月'],
	['9','9月'],
	['10','10月'],
	['11','11月'],
	['12','12月']
];
var Date_Day=[
	['1','1号'],
	['2','2号'],
	['3','3号'],
	['4','4号'],
	['5','5号'],
	['6','6号'],
	['7','7号'],
	['8','8号'],
	['9','9号'],
	['10','10号'],
	['11','11号'],
	['12','12号'],
	['13','13号'],
	['14','14号'],
	['15','15号'],
	['16','16号'],
	['17','17号'],
	['18','18号'],
	['19','19号'],
	['20','20号'],
	['21','21号'],
	['22','22号'],
	['23','23号'],
	['24','24号'],
	['25','25号'],
	['26','26号'],
	['27','27号'],
	['28','28号'],
	['29','29号'],
	['30','30号'],
	['31','31号']
];
var store_createType=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'name'}
	 ]
});
store_createType.loadData(Data_createType);

var store_createMonth=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'name'}
	 ]
});
store_createMonth.loadData(Date_Month);

var store_createDay=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'name'}
	 ]
});
store_createDay.loadData(Date_Day);

var setWindow=new Ext.Window({
        id:'setWindow',
        title:'周期设定',
        isTopContainer : true,
        modal : true,
        autoScroll:true,
        closeAction:'hide',
		width: 250,
        items: [{
			xtype: 'form',
			border: false,
			bodyStyle: 'padding:12px 0px 0px 12px',
			labelWidth: 60,
			defaults: {
				anchor: '95%',
				labelStyle:"margin-bottom:12px;",
				style:"margin-bottom:12px;"
			},
			items:[{
				xtype:'combo',
				id:"period",
				name:"period",
				//hiddenName:'period', 
				fieldLabel: '重复',
				valueField: 'value',
				displayField: 'name',
				mode: 'local',
				triggerAction: 'all',				
				store:store_createType,
				value:"5",
				editable:false,
				listeners:{
				   select:function(combo,record,index){
				      //alert(index);
				      if(index == 0){
				       store_createMonth.loadData(Date_Month);
				       Ext.getCmp('createMonth').setValue("1");
				       Ext.getCmp('createMonth').setDisabled(false);
				      }else if(index == 1){
				       store_createMonth.loadData(Date_QuarterMonth);
				       Ext.getCmp('createMonth').setValue("1");
				       Ext.getCmp('createMonth').setDisabled(false);
				      }else if(index == 2){
				       //alert(Ext.getCmp('createMonth').disabled);
				       Ext.getCmp('createMonth').setDisabled(true);
				      }
				      var index = Ext.getCmp('period').value;
				      setCycle();
				      setSummary(index);
				   }
				}
			},{
				xtype:'combo',
				id: 'createMonth',
				name:'createMonth',
				fieldLabel: '月份',
				valueField: 'value',
				displayField: 'name',
				mode: 'local',
				triggerAction: 'all',
				store:store_createMonth,
				value:"1",
				editable:false,
				disabled:false,
				listeners:{
				   select:function(combo,record,index){
				     var index = Ext.getCmp('period').value;
				     setCycle(); 
				     setSummary(index);
				   }
				}
			},{
				xtype:'combo',
				id: 'createDay',
				fieldLabel: '日期',
				valueField: 'value',
				displayField: 'name',
				mode: 'local',
				triggerAction: 'all',
				store:store_createDay,
				value:"1",
				editable:false,
				listeners:{
				   select:function(combo,record,index){
				     var index = Ext.getCmp('period').value;
				     setCycle(); 
				     setSummary();
				   }
				}
			},{
				xtype : 'displayfield',
				id : 'explain',
				name : 'explain',
				//fieldLabel : '',
				//width : 200,
				value : "<font color=red>注：如选择29-31号，则部分月份无法执行</font>"
				//height : 25
			},{
				xtype:'timefield',
				id:'createTime',
				name:'createTime',
				fieldLabel: '时间',
				selectOnFoucs:true,
				allowBlank:false,
				editable: false,
				increment:30,
				format : 'G:i',
				value:'1:00',
				editable:false,
				listeners:{
				   select:function(combo,record,index){
				     setCycle();
				   }
				}
			},{
				xtype:'textfield',
				id:"startTime",
				id:"startTime",
				fieldLabel: '开始日期',
				disabled: true,
				value: '2014/1/1 01:00AM'
			},{
				xtype:'textfield',
				id:'summary',
				name:'summary',
				fieldLabel: '摘要',
				disabled: true,
				value: '每年1月1号'
			}]
		}],
		buttons: [{
			text: '确定',
			handler: function(){
			   // Ext.getCmp('nextTime').setValue(startTimeValue); 
			    var summary = Ext.getCmp('summary').value;
			    var time = Ext.getCmp('createTime').value;
			    setCycle(); 

		        Ext.getCmp('cycleField').setValue(summary + " " + time);
		         periodType = Ext.getCmp('period').value;
				 createMonth = Ext.getCmp('createMonth').value;
				 createDay = Ext.getCmp('createDay').value;
				 createTime = Ext.get('createTime').dom.value;
				 startTime1 = Ext.getCmp('startTime').value;
				 nextTime1 = Ext.getCmp('nextTime').value;
			    //是否挂起
			     var isHangUp = Ext.getCmp('handUp').checked;
			     if(isHangUp == true){
			       Ext.getCmp('nextTime').setValue("");
			     }else{
			       Ext.getCmp('nextTime').setValue(nextTimeValue);
			     }
			    
			    setWindow.hide();

			}
		 },{
			text: '取消',
			handler: function(){
			    setWindow.hide();
			}
		}]
     });

function setSummary(index){
	var periodValue = Ext.get('period').dom.value;
	var createMonth = Ext.get('createMonth').dom.value;
	var createDay = Ext.get('createDay').dom.value;
	var createTime = Ext.get('createTime').dom.value;
	var summaryValue;
	if(index == 3){
		summaryValue = periodValue + createDay;
	}else{
		summaryValue = periodValue + createMonth + createDay;
	}
	Ext.getCmp('summary').setValue(summaryValue);
}

function setCycle(){	
    var period = Ext.getCmp('period').value;
    var createMonth = Ext.getCmp('createMonth').value;
    var createDay = Ext.getCmp('createDay').value;
    var createTime = Ext.get('createTime').dom.value;
    //获取当前时间
    var date = new Date();
    var year = date.getYear();
    var month = date.getMonth()+1;
    var day = date.getDate();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var currentTime = year+"-"+month+"-"+day+" "+hours+":"+minutes;
    //获取下个月日期
    var nextYear = date.getYear()+1;
    var nextMonth = date.getMonth()+2;
    var nextDay;

    var standardTime = "1990-0-0 00:00";
    //计算任务开始时间和下次执行时间
    if(period == 5){  //每年
       //计算时间差
       var yearTime = year + "-" + createMonth + "-" + createDay + " " + createTime;
       var currentTimeDiff = TimeDiff(currentTime, standardTime);
       var yearDiff = TimeDiff(yearTime, standardTime);
       if(yearDiff > currentTimeDiff){
         startTimeValue = yearTime;
         nextTimeValue = yearTime;
       }else{
         startTimeValue = nextYear + "-" + createMonth + "-" + createDay + " " + createTime;
         nextTimeValue = nextYear + "-" + createMonth + "-" + createDay + " " + createTime;
       }
    }else if(period == 4){ //每季
       var quarterFir=100;
       var quarterSec;
       var quarterThi;
       var quarterFor;
       //判断是每个季度的第几个月
       switch (parseInt(createMonth)) {
		  case 1:
		    quarterFir = 1;
		    quarterSec = 4;
		    quarterThi = 7;
		    quarterFor = 10;
		    break;
		  case 2: 
		    quarterFir = 2;
		    quarterSec = 5;
		    quarterThi = 8;
		    quarterFor = 11;
		    break;
		  case 3: 
		    quarterFir = 3;
		    quarterSec = 6;
		    quarterThi = 9;
		    quarterFor = 12;
		    break;
		  default: break;
       }
       //计算时间差
       quarterFirTime = year + "-" + quarterFir + "-" + createDay + " " + createTime;
       quarterSecTime = year + "-" + quarterSec + "-" + createDay + " " + createTime;
       quarterThiTime = year + "-" + quarterThi + "-" + createDay + " " + createTime;
       quarterForTime = year + "-" + quarterFor + "-" + createDay + " " + createTime;

       var currentTimeDiff = TimeDiff(currentTime, standardTime);
       var quarterFirDiff = TimeDiff(quarterFirTime, standardTime);
       var quarterSecDiff = TimeDiff(quarterSecTime, standardTime);
       var quarterThiDiff = TimeDiff(quarterThiTime, standardTime);
       var quarterForDiff = TimeDiff(quarterForTime, standardTime);

       if(currentTimeDiff < quarterFirDiff){
         startTimeValue = quarterFirTime;
         nextTimeValue = quarterFirTime;
       }else if(currentTimeDiff <quarterSecDiff){
         startTimeValue = quarterSecTime;
         nextTimeValue = quarterSecTime;
       }else if(currentTimeDiff <quarterThiDiff){
         startTimeValue = quarterThiTime;
         nextTimeValue = quarterThiTime;
       }else if(currentTimeDiff <quarterForDiff){
         startTimeValue = quarterForTime;
         nextTimeValue = quarterForTime;
       }else{
         quarterFirTime = nextYear + "-" + quarterFir + "-" + createDay + " " + createTime;
         startTimeValue = quarterFirTime;
         nextTimeValue = quarterFirTime;
       }
       
    }else{  //每月
       //当前月份巡检日期
       var monthTime = year + "-" + month + "-" + createDay + " " + createTime;
       //计算时间差
       var currentTimeDiff = TimeDiff(currentTime, standardTime);
       var monthDiff = TimeDiff(monthTime, standardTime);
       if(currentTimeDiff < monthDiff){
          startTimeValue = monthTime;
          nextTimeValue = monthTime;
       }else{
          startTimeValue = year + "-" + nextMonth + "-" + createDay + " " + createTime;
          nextTimeValue = startTimeValue;
       }
    }
    Ext.getCmp('startTime').setValue(startTimeValue);
    Ext.getCmp('nextTime').setValue(nextTimeValue);
       
}

//==========================center=============================
var centerPanel = new Ext.Panel({
    id:'centerPanel',
    border:false,
    region:'center',
    autoScroll:true,
    layout:'border',
    items:[gridPanel]
});
//==========================center=============================



/*-----------------------------------------------方法部分-----------------------------------------*/
//==========================计算时间差=============================

function TimeDiff(day1, day2){
    var reg = /-|-|- |\/|:| /;
    //dayinfo -  用正则分割
    var DI1 = day1.split(reg);
    var DI2 = day2.split(reg);
    var date1 = new Date(DI1[0], parseInt(DI1[1])-1, DI1[2], DI1[3], DI1[4]);
    var date2 = new Date(DI2[0], parseInt(DI2[1])-1, DI2[2], DI2[3], DI2[4]);
    //用距标准时间差来获取相距时间
    var minsec = Date.parse(date1) - Date.parse(date2);
    var days = minsec / 1000 / 60 / 60 / 24; //factor: second / minute / hour / day
     var re = "";
     if(minsec < 0){ 
      re="0秒";
     }
    if(minsec>=1000*60*60*24)
       {
        re=parseInt(minsec/(1000*60*60*24))+"d";
        minsec = minsec-(parseInt(minsec/(1000*60*60*24))*(1000*60*60*24));
       }
    if(minsec>=1000*60*60)
       {
        re=re+parseInt(minsec/(1000*60*60))+"h";
        minsec = minsec-(parseInt(minsec/(1000*60*60))*(1000*60*60));
       }
    if(minsec>=1000*60)
       {
        re=re+parseInt(minsec/(1000*60))+"m";
       }
    return days;
}

//==========================编辑巡检任务=============================
function addTask(closeOrNot){
if(saveType == 2){
   // 关闭修改任务信息窗口
	var window = parent.Ext.getCmp('addWindow');
	if (window) {
		window.close();
	}
}else{  
   //巡检设备
   var inspectEquipList = null;
   var inspectEquipNameList = null;
   if(store.getCount() != 0){
     inspectEquipList = new Array();
     inspectEquipNameList = new Array();
     for(var i=0; i<store.getCount();i++){
       inspectEquipList.push(store.getAt(i).get("equipType")+"_"+store.getAt(i).get("equipId"));
       inspectEquipNameList.push(store.getAt(i).get("equipFullName")); 
     }
   }
   

  // var engineerId = Ext.getCmp('engineerID').getValue();
   var taskName = Ext.getCmp('taskName').getValue();
   var taskDescription = Ext.getCmp('taskDescription').getValue();
   
   //巡检项目取值 
	var myCheckBoxGroup = Ext.getCmp('taskItem');
	var inspectItemList = new Array();
    for (var i = 0; i < myCheckBoxGroup.items.length; i++)    
    {    
        if (myCheckBoxGroup.items.itemAt(i).checked)    
        {    
          //  alert(myCheckBoxGroup.items.itemAt(i).name); 
            inspectItemList.push(myCheckBoxGroup.items.itemAt(i).name);                   
        }    
    } 

   //是否挂起
   var handUp = Ext.getCmp('handUp').checked;
   if(handUp == true){
     handUp = 2;
   }else{
     handUp = 1;
   }
   //周期类型及周期
 //  var periodType = Ext.getCmp('period').value;
 //  var periodTime;
 //  var createMonth = Ext.getCmp('createMonth').value;
 //  var createDay = Ext.getCmp('createDay').value;
 //  var createTime = Ext.get('createTime').dom.value;

   if(periodType == 3){ //每月
      periodTime = "0,0,0,0,"+createDay + "," + createTime;  
    //  alert(periodTime);
   }else{//每年(每季)
      periodTime = "0,0,"+createMonth + ",0," + createDay + "," + createTime;
   //   alert(periodTime);
   }
   
   //开始时间、下次执行时间
  // var startTime = Ext.getCmp('startTime').value;
  // var nextTime = Ext.getCmp('nextTime').value;
   
   
   //操作权限组
   var selections = privilegeGrid.getSelectionModel().getSelections();
   var privilegeList = new Array();
   /*if(selections.length == 0){
       Ext.Msg.alert('信息','请选择操作权限组！');
       return;
   }*/
   for(var i = 0; i < selections.length; i++){
      privilegeList.push(selections[i].get("groupId"));
   }
      
   var jsonData = {
       "inspectTaskId":inspectTaskId,
       "taskName":taskName,
       "taskDescription":taskDescription,
       "inspectItemList":inspectItemList,
       "periodType":periodType,
       "periodTime":periodTime,
       "startTime":startTime1,
       "nextTime":nextTime1,
       "handUp":handUp,
       "privilegeList":privilegeList,
       "inspectEquipList":inspectEquipList,
       "inspectEquipNameList":inspectEquipNameList,
       "privilegeParamId":privilegeParamId,
       "inspectItemParamId":inspectItemParamId
         
   }
   
   var url;
   if(saveType == 0){
	   if(inspectTaskId == 0){//第一次打开新增巡检任务窗口，添加任务，"应用"按下，此操作向数据库中插入新纪录
		   url = 'inspect-task!addInspectTask.action';
	   }else{//新增窗口未关闭，继续编辑巡检任务，"应用"或"确定"按钮按下，此操作为更新巡检任务
		   url = 'inspect-task!updateInspectTask.action';
	   }
     
   }else{
     url = 'inspect-task!updateInspectTask.action';
   }
   
   Ext.Ajax.request({
      url:url,
      method:'Post',
      params:jsonData,
      success: function(response) {
		    	var obj = Ext.decode(response.responseText);
		    	if(obj.returnResult == 0){
		    		Ext.Msg.alert("信息",obj.returnMessage);
	             }
	         	if(obj.returnResult == 1){
	         		if(inspectTaskId == 0){
		         		privilegeParamId = obj.privilegeParamId;
		         		inspectItemParamId = obj.inspectItemParamId;
	         		}
	         		inspectTaskId = obj.inspectTaskId;
	         		if(closeOrNot == 1){
		         	  Ext.Msg.alert("信息",obj.returnMessage,function(btn){
		         	    // 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
		         	    // 关闭修改任务信息窗口
						var window = parent.Ext.getCmp('addWindow');
						if (window) {
							window.close();
						}
		         	  });
		         	}else if(closeOrNot == 0){
		         	  Ext.Msg.alert("信息",obj.returnMessage);
		         	  // 刷新列表
						var pageTool = parent.Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
		         	}
		         	
	         	}
		    },
    error:function(response) {
    	Ext.Msg.alert("错误",response.responseText);
    },
    failure:function(response) {
    	Ext.Msg.alert("错误",response.responseText);
    }
   
   })
}
}


//----------------------------save handler for use--------------------------------

//-----------------------------initial----------------------------------
function initData(){
 
    if(saveType == 0){
      Ext.Ajax.request({
	      url:'inspect-task!getCurrentUserGroup.action',
	      method:'Post',
	      //params:jsonData,
	      success: function(response) {
	              var obj = Ext.decode(response.responseText);
	              var checkedGroupList = obj.checkedGroupList;
	              var rowIdList = new Array();
	              for(var i=0; i<privilegeStore.getCount();i++){
			    	  var groupId = privilegeStore.getAt(i).get("groupId");
				      for(var j=0; j<checkedGroupList.length;j++){
				         if(checkedGroupList[j].SYS_USER_GROUP_ID == groupId){
				            rowIdList.push(i);
				         }
				      }
				  }
				  privilegeGrid.getSelectionModel().selectRows(rowIdList);
	      },
	      error:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	      },
	      failure:function(response) {
	    	Ext.Msg.alert("失败",response.responseText);
	      } 
	      
	  })
    
    }
    if(saveType != 0){
	  // alert(saveType);
	   var jsonData = {
	       "inspectTaskId":inspectTaskId
	   }
	  var inspectEquipList = new Array();
	  store.baseParams=jsonData;
      store.load({
        callback : function(records,options,success){
          if(!success)
            Ext.Msg.alert("提示","加载包机设备失败");
        }
      });
	 /* Ext.Ajax.request({
	      url:'inspect-engineer!initInspectEquip.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
			    	var obj = Ext.decode(response.responseText);
			    	var rows = obj.rows;
                    for(var i=0; i<rows.length;i++){
                        var equipArray = new Array();
	                    var id =  rows[i].TARGET_TYPE + "_" + rows[i].TARGET_ID; 
	                    equipArray.push(rows[i].TARGET_TYPE);
	                    equipArray.push(id); 
	                    equipArray.push(rows[i].DISPLAY_NAME); 
	                    inspectEquipList.push(equipArray);
                    
                    }
			    	store.loadData(inspectEquipList);
			    },
	    error:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	   
	   })
	   */
	    Ext.Ajax.request({
	      url:'inspect-task!initTaskInfo.action',
	      method:'Post',
	      params:jsonData,
	      success: function(response) {
			    	var obj = Ext.decode(response.responseText);
			    	var task = obj.task;
			    	Ext.getCmp('taskName').setValue(task.TASK_NAME);
			    	Ext.getCmp('taskDescription').setValue(task.TASK_DESCRIPTION);
			    	Ext.getCmp('startTime').setValue(task.START_TIME);
			    	Ext.getCmp('nextTime').setValue(task.NEXT_TIME);
			    	startTime1 = task.START_TIME;
			    	nextTime1 = task.NEXT_TIME;
			    	nextTimeValue = task.NEXT_TIME;
			    	
			    	var hangUp = task.TASK_STATUS;
			    	if(task.TASK_STATUS == 2){
			    	  Ext.getCmp('handUp').setValue(true);
			    	}else{
			    	  Ext.getCmp('handUp').setValue(false);
			    	}
			    	//巡检项目初始化
			    	Ext.getCmp('1').setValue(false);
			    	Ext.getCmp('2').setValue(false);
//			    	Ext.getCmp('3').setValue(false);
			    	Ext.getCmp('4').setValue(false);
			    	Ext.getCmp('5').setValue(false);
			    	Ext.getCmp('6').setValue(false);
			    	Ext.getCmp('7').setValue(false);
			    	var taskItem = obj.inspectItemList;
			    	//alert(taskItem.length);
			    	inspectItemParamId = obj.inspectItemParamId;
			    	//alert(inspectItemParamId);
			    	for(var i=0;i<taskItem.length;i++){
			    	  Ext.getCmp(taskItem[i]).setValue(true);
			    	}
			    	
			    	//周期设定
			    	periodType = obj.periodType;
			    	var period = obj.period;
			    	Ext.getCmp('period').setValue(periodType);
			    	Ext.getCmp('createMonth').setValue(period.month);
			    	Ext.getCmp('createDay').setValue(period.day);
			    	Ext.getCmp('createTime').setValue(period.time);
			    	Ext.getCmp('summary').setValue(period.summary);
				    Ext.getCmp('cycleField').setValue(period.cycleField);
				     if(periodType == 3){
				         Ext.getCmp('createMonth').setDisabled(true);
				     }else{
				         Ext.getCmp('createMonth').setDisabled(false);
				     }
			    	createMonth = period.month;
			    	createDay = period.day;
			    	createTime = period.time;
			    	
			    	//权限组加载
			    	var privilegeList = obj.privilegeList;
			    	var rowIdList = new Array();
			    	
			    	//alert(privilegeList.length);
			    	privilegeParamId = obj.privilegeParamId;
			    	//alert(privilegeParamId);
			    	for(var i=0; i<privilegeStore.getCount();i++){
			    	  var groupId = privilegeStore.getAt(i).get("groupId");
			    //	  alert("groupId:"+groupId);
				      for(var j=0; j<privilegeList.length; j++){
				     //   alert("privilege:"+privilegeList[j]);
				       if(privilegeList[j] == groupId){
				         // privilegeGrid.getSelectionModel().selectRow(i);
				         rowIdList.push(i);
				        }
				      }
				    }
				    privilegeGrid.getSelectionModel().selectRows(rowIdList);
			    },
	    error:function(response) {
	        alert("error");
	    	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	alert("failure");
	    	Ext.Msg.alert("错误",response.responseText);
	    }
	   
	   })
    }
}

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;

	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();}
    Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
    if(saveType == 2){
      var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [centerPanel,eastPanel],
        renderTo : Ext.getBody()
    });    
    //  Ext.getCmp('ok').setDisabled(true);
      Ext.getCmp('apply').setVisible(false);
      Ext.getCmp('cancel').setVisible(false);
      Ext.getCmp('handUp').setVisible(false);
      Ext.getCmp('setButton').setVisible(false);
      Ext.getCmp('deleteEquip').setDisabled(true);
      
      Ext.getCmp('taskName').setDisabled(true);
      Ext.getCmp('taskDescription').setDisabled(true);
      Ext.getCmp('taskItem').setDisabled(true);
      //Ext.getCmp("taskName").getEl().dom.readOnly = true;
      //Ext.getCmp("taskDescription").getEl().dom.readOnly = true;
      Ext.getCmp('privilegeGrid').setDisabled(true);
    }else{
      var win = new Ext.Viewport({
    	id:'win',
        loadMask : true,
        layout: 'border',
        items : [centerPanel,westPanel,eastPanel],
        renderTo : Ext.getBody()
    });
    }
 	initData();
  });