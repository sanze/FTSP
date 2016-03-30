var FAILED = 0;
var NOTICE_TEXT = "提示";
var wdateFmt="yyyy-MM-dd HH:mm:ss";
var dateFmt="yyyy-MM-dd hh:mm:ss";

  //任务类别
  var categoryData=[
  	[1,'重大活动'],
  	[2,'重要会议'],
  	[3,'自然灾害']
  ];
  var categoryStore=new Ext.data.ArrayStore({
  	fields:[
  		{name:'value'},
  		{name:'displayName'}
  	 ]
  });
  categoryStore.loadData(categoryData);
  function categoryRenderer(value){
  	var index=categoryStore.find('value',value,0,true);
  	if(index>=0){
  		return categoryStore.getAt(index).get('displayName');
  	} else {
  		return value;
  	}
  }
  var categoryCombo = {
	xtype:'combo',
	id : 'categoryCombo',
	store : categoryStore,
	mode : 'local',
	fieldLabel : '任务类别',
	emptyText : '任务类别',
	anchor:'95%',
	triggerAction : 'all',
	valueField : 'value',
	displayField : 'displayName',
	editable : false,
	value:1
  };
  var TASK_STATUS =	{
	ALL		:(1<<8)-1,
	WAITTING:1<<0,
	RUNNING	:1<<1,
	STOPED	:1<<2,
	COMPLETED:1<<3
  }
  //任务状态
  var statusData=[
	[TASK_STATUS.ALL,'全部'],//0xff
  	[TASK_STATUS.WAITTING,'等待'],
  	[TASK_STATUS.RUNNING,'进行中'],
  	[TASK_STATUS.STOPED,'人工停止'],
  	[TASK_STATUS.COMPLETED,'完成'],
  	[TASK_STATUS.WAITTING|TASK_STATUS.RUNNING,'进行中&等待']
  ];
  var statusStore=new Ext.data.ArrayStore({
  	fields:[
  		{name:'value'},
  		{name:'displayName'}
  	 ]
  });
  statusStore.loadData(statusData);
  function statusRenderer(value){
  	var index=statusStore.findBy(function fn(record,id){
  		return record.get('value')==value;
  	});
  	if(index>=0){
  		return statusStore.getAt(index).get('displayName');
  	} else {
  		return value;
  	}
  }
  var statusCombo = {
	xtype:'combo',
	id : 'statusCombo',
	store : statusStore,
	mode : 'local',
	fieldLabel : '任务状态',
	emptyText : '任务状态',
	anchor:'95%',
	triggerAction : 'all',
	valueField : 'value',
	displayField : 'displayName',
	editable : false,
	value : TASK_STATUS.ALL
  };
  //对象类型
  var targetTypeData=[
  	[1,'设备'],
  	[2,'电路'],
  	[3,'场馆']
  ];
  var targetTypeStore=new Ext.data.ArrayStore({
  	fields:[
  		{name:'value'},
  		{name:'displayName'}
  	 ]
  });
  targetTypeStore.loadData(targetTypeData);
  function targetTypeRenderer(value){
  	var index=targetTypeStore.find('value',value,0,true);
  	if(index>=0){
  		return targetTypeStore.getAt(index).get('displayName');
  	} else {
  		return value;
  	}
  }
  //开始、结束时间
  var startTime={
      xtype:'textfield',
      id:'startTime',
      name:'startTime',
      fieldLabel: '开始时间',
      width:130,
      anchor:'95%',
      cls : 'Wdate',
      //value:startDate,
      listeners: {
      	'focus': function(){
      		WdatePicker({
  				el : "startTime",
				isShowClear : true,
				readOnly : true,
				dateFmt : wdateFmt?wdateFmt:"yyyy-MM-dd",
				autoPickDate : true,
				onpicked: function(){
					var endTime=Ext.getCmp('endTime').getValue();
					var startTime=Ext.getCmp('startTime').getValue();
					if(endTime&&endTime.length>0&&
						(Date.parseDate(endTime,"Y-m-d H:i:s").getTime()<
						 Date.parseDate(startTime,"Y-m-d H:i:s").getTime()))//选择的起始时间晚于结束时间
						Ext.getCmp('endTime').setValue('');
				}
			});
  			this.blur();
		}
      }
  };
  var endTime={
      xtype:'textfield',
      id:'endTime',
      name:'endTime',
      fieldLabel: '结束时间',
      anchor:'95%',
      width:130,
      cls : 'Wdate',
      //value:endDate,
      listeners: {
      	'focus': function(){
      		WdatePicker({
  			    el : "endTime",
				isShowClear : true,
				readOnly : true,
				dateFmt : wdateFmt?wdateFmt:"yyyy-MM-dd",
				autoPickDate : true,
				minDate:Ext.getCmp('startTime').getValue()
			});
			this.blur();
		}
      }
  }
  function dateRenderer(val){
	  if(val)
		  return new Date(val.time).format(dateFmt?dateFmt:"yyyy-MM-dd");
	  return val;
  }