
//时间粒度控件
var timeFinenessComboPanel= {
		id : 'timeFinenessComboPanel',
		style : 'margin-left:20px;margin-top:20px;',
		border : false,
		layout : 'column',
		items : [
			{
				border : false,
				width:80,
				html : '<span>重复：</span>'
			},  {
				xtype : 'combo',
				style : 'margin-left:0',
				id : 'timeFinenessCombo',
				name : 'timeFinenessCombo',
				fieldLabel : '',
				editable : false,
				mode : "local",
				width : 140,
				value:'day',
				store : new Ext.data.ArrayStore({
					fields : ['value','displayName'],
					data : [['day','每日'],['week','每周'],['month','每月']] 
				}),
				valueField : 'value',
				displayField : 'displayName',
				triggerAction : 'all',
				listeners : {
					select : function(combo, record, index) {
						qhour=0;
						qmin=0;
						Ext.getCmp('dateChoose').setValue('');
						Ext.getCmp('beginDate').setValue();
						Ext.getCmp('summary').setValue();
						if(record.data.value=='day'){
							Ext.getCmp('queryWeekComboPanel').setVisible(false);
							Ext.getCmp('queryMonthComboPanel').setVisible(false);
						}else if(record.data.value=='week'){
							Ext.getCmp('queryMonthComboPanel').setVisible(false);
							Ext.getCmp('queryWeekComboPanel').setVisible(true);
						}else if(record.data.value=='month'){
							Ext.getCmp('queryWeekComboPanel').setVisible(false);
							Ext.getCmp('queryMonthComboPanel').setVisible(true);
						}
					}
				}
          	}
	  ]
};


//加载周数据
var queryWeekComboPanel= {
		id : 'queryWeekComboPanel',
		style : 'margin-left:20px;margin-top:20px;',
		border : false,
		layout : 'column',
		items : [
			{
				border : false,
				width:80,
				html : '<span>星期：</span>'
			},  {
				xtype : 'combo',
				style : 'margin-left:0',
				id : 'queryWeekCombo',
				editable : false,
				name : 'queryWeekCombo',
				fieldLabel : '',
				mode : "local",
				width : 140,
				value:'1',
				store : new Ext.data.ArrayStore({
					fields:[
							   {name:"value",mapping:"value"},
							   {name:"displayName",mapping:"displayName"}
					   ]
					}),
				valueField : 'value',
				displayField : 'displayName',
				triggerAction : 'all',
				//anchor : '95%',
				listeners : {
					beforequery:function(queryEvent){
						var arr=[];
						for(var i=1;i<=7;i++){
							var json={};
							json.value=i;
							json.displayName=i;
							arr.push(json);
						}
						Ext.getCmp('queryWeekCombo').getStore().loadData(arr);  
					},
					select : function(combo, record, index) {
						setBeginDate();
					}
				}
          	}
	  ]
};


//加载月数据
var queryMonthComboPanel= {
		id : 'queryMonthComboPanel',
		style : 'margin-left:20px;margin-top:20px;',
		border : false,
		layout : 'form',
		items : [
		    {
		    	layout : 'column',
		    	border : false,
		    	items:[
			{
				border : false,
				width:80,
				html : '<span>日期：</span>'
			},  {
				xtype : 'combo',
				style : 'margin-left:0',
				id : 'queryMonthCombo',
				name : 'queryMonthCombo',
				editable : false,
				fieldLabel : '',
				mode : "local",
				width : 140,
				value:'1',
				store : new Ext.data.ArrayStore({
					fields:[
							   {name:"value",mapping:"value"},
							   {name:"displayName",mapping:"displayName"}
					   ]
					}),
				valueField : 'value',
				displayField : 'displayName',
				triggerAction : 'all',
				//anchor : '95%',
				listeners : {
					beforequery:function(queryEvent){
						var arr=[];
						for(var i=1;i<=31;i++){
							var json={};
							json.value=i;
							json.displayName=i;
							arr.push(json);
						}
						Ext.getCmp('queryMonthCombo').getStore().loadData(arr);  
					},
					select : function(combo, record, index) {
						setBeginDate();
					}
				}
          	}]},
          	{
    			id:'',
    			border : false,
    			html : '<span style="color:red">注：如选择29~31号，则在部分月份无法执行</span>'
    		}
	  ]
};


//时间
var dateChoosePanel= {
	id : 'dateChoosePanel',
	style : 'margin-left:20px;margin-top:20px;',
	border : false,
	layout : 'column',
	items : [
		{
			border : false,
			width:80,
			html : '<span>时间：</span>'
		},
		{
	        xtype : 'textfield',
	        id : 'dateChoose',
	        name : 'dateChoose',
	        style : 'margin-left:0',
	        readOnly:true,
	        width : 140,
	        height : 20,
	        allowBlank : true,
	        anchor : '95%'
	    },{
			xtype: 'button',
		    text: "...",
		    height:20,
		    width: 20,
		    listeners: { "click": function () {
		    	chooseDate();
		     }
		    }
		}
  ]
};

//弹出时间选择框
function chooseDate(){
	var dateChooseWindow=new Ext.Window({
		id:'dateChooseWindow',
		title:'选择时间',
		width:260,
		height:340,
		resizable:true, 
		isTopContainer : true,
		modal : true,
		autoScroll : false,
		html : '<iframe src = "dateChoose.jsp" height="100%" width="100%" frameBorder=0 border=0/>'
	});
	dateChooseWindow.show();
}


var qhour=0;
var qmin=0;
//选完时间后调用
function setChooseDate(hour,min){
	qhour=hour;
	qmin=min;
	hour=hour<10?'0'+''+hour:hour+'';
	min=min<10?'0'+''+min:min+'';
	Ext.getCmp('dateChoose').setValue(hour+':'+min);
	setBeginDate();
}


//开始时间
var beginDatePanel= {
	id : 'beginDatePanel',
	style : 'margin-left:20px;margin-top:20px;',
	border : false,
	layout : 'column',
	items : [
		{
			border : false,
			width:80,
			html : '<span>开始时间：</span>'
		},
		{
	        xtype : 'textfield',
	        id : 'beginDate',
	        style : 'margin-left:0;',
	        name : 'beginDate',
	        width : 140,
	        height : 20,
	        disabled:true,
	        allowBlank : true,
	        anchor : '95%'
	    }
  ]
};


//摘要
var summaryPanel= {
	id : 'summaryPanel',
	style : 'margin-left:20px;margin-top:20px;',
	border : false,
	layout : 'column',
	items : [
		{
			border : false,
			width:80,
			html : '<span>摘要：</span>'
		},
		{
	        xtype : 'textfield',
	        id : 'summary',
	        name : 'summary',
	        disabled:true,
	        style : 'margin-left:0',
	        width : 140,
	        height : 20,
	        allowBlank : true,
	        anchor : '95%'
	    }
  ]
};


var autoBackupPeriodPanel = new Ext.FormPanel({
	region : 'center',
	border :false,
	width : 300,
	items : [{
		layout : 'form',
		border : false,
		items:[timeFinenessComboPanel,queryMonthComboPanel,queryWeekComboPanel,dateChoosePanel,beginDatePanel,summaryPanel]
	}],
	buttons: [{
		id:'ok',
	    text: '确定',
	    handler: function(){
	    	setAutoDate();
		}
	 },
	 { xtype: 'tbspacer', width: 60,shadow:false },
	 {
	    text: '取消',
	    handler: function(){
	        //关闭修改任务信息窗口
			var win = parent.Ext.getCmp('backupPeriodWindow');
			if(win){
				win.close();
			}
	    }
	}]
});


function setAutoDate(){
	var beginDate=Ext.getCmp('beginDate').getValue();
	if(beginDate==undefined || beginDate==null || beginDate==''){
		Ext.Msg.alert("提示", "请选择自动备份的周期时间!");
		return;
	}
	parent.Ext.getCmp('backupPeriod').setValue(Ext.getCmp('summary').getValue());
	parent.timeType=Ext.getCmp('timeFinenessCombo').getValue();
	parent.day=Ext.getCmp('queryMonthCombo').getValue();
	parent.week=Ext.getCmp('queryWeekCombo').getValue();
	parent.hour=qhour;
	parent.min=qmin;
	parent.nextExecuteDate=Ext.getCmp('beginDate').getValue();
	var win = parent.Ext.getCmp('backupPeriodWindow');
	if(win){
		win.close();
	}
}



//根据星期获取下一个时间
function getNextDate(weekDay){
	//0是星期日,1是星期一,...
	var nowDate=new Date();
	weekDay%=7;
	var day = nowDate.getDay();
	var time = nowDate.getTime();
	var sub=0;
	if(weekDay>day){
		sub = weekDay-day;
	}else{
		sub=7-day+weekDay;
	}
	time+=sub*24*3600000;
	nowDate.setTime(time);
	return nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
}

//根据选择设置开始时间及摘要
function setBeginDate(){
	var fin=Ext.getCmp('timeFinenessCombo').getValue();
	if(fin=='day'){
		if(qhour==0){
			return;
		}
		if(new Date().getHours()<qhour || (new Date().getHours()==qhour && new Date().getMinutes()<qmin)){
			var nowDate=new Date();
			date=nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
		}else{
			date=getDayByDays(1);
		}
		Ext.getCmp('beginDate').setValue(date+" "+Ext.getCmp('dateChoose').getValue());
		Ext.getCmp('summary').setValue("每日"+" "+Ext.getCmp('dateChoose').getValue());
		
	}else if(fin=='week'){
		if(qhour==0){
			return;
		}
		var week=Ext.getCmp('queryWeekCombo').getValue();
		var date="";
		if(week==new Date().getDay() && new Date().getHours()<qhour){
			var nowDate=new Date();
			date=nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
		}else if(week==new Date().getDay() && new Date().getHours()==qhour && new Date().getMinutes()<qmin){
			var nowDate=new Date();
			date=nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
		}else{
			date=getNextDate(week);
		}
		Ext.getCmp('beginDate').setValue(date+" "+Ext.getCmp('dateChoose').getValue());
		Ext.getCmp('summary').setValue("每周"+getWeekFormat(week)+" "+Ext.getCmp('dateChoose').getValue());
	}else if(fin=='month'){
		if(qhour==0){
			return;
		}
		var day=Ext.getCmp('queryMonthCombo').getValue();
		if(new Date().getDate()<day){
//			var nowDate = new Date(new Date().getFullYear(),new Date().getMonth()+1,0);    
//			days = nowDate.getDate();//获取当前日期中的月的天数
//			if(day>days){
//				day=days;
//			}
			var nowDate=new Date();
			date=nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+day;
		}else if(new Date().getDate()==day && new Date().getHours()<qhour){
			var nowDate=new Date();
			date=nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
		}else if(new Date().getDate()==day && new Date().getHours()==qhour && new Date().getMinutes()<qmin){
			var nowDate=new Date();
			date=nowDate.getFullYear()+'/'+(nowDate.getMonth()+1)+'/'+nowDate.getDate();
		}else{
			var nowDate=new Date();
			date=getNextMonth(nowDate.getFullYear()+"-"+(nowDate.getMonth()+1)+"-"+day);
		}
		Ext.getCmp('beginDate').setValue(date+" "+Ext.getCmp('dateChoose').getValue());
		Ext.getCmp('summary').setValue("每月"+day+" 号"+Ext.getCmp('dateChoose').getValue());
	}
}

//获取给定日期的下月时间
function getNextMonth(t){
    var tarr = t.split('-');
    var year = tarr[0];                //获取当前日期的年
    var month = tarr[1];            //获取当前日期的月
    var day = tarr[2];                //获取当前日期的日
    var days = new Date(year,month,0);    
    days = days.getDate();//获取当前日期中的月的天数
    var year2 = year;
    var month2 = parseInt(month)+1;
    if(month2==13) {
        year2 = parseInt(year2)+1;
        month2 = 1;
    }
    var day2 = day;
    var days2 = new Date(year2,month2,0);
    days2 = days2.getDate();
    if(day2>days2) {
        day2 = days2;
    }
    var t2 = year2+'/'+month2+'/'+day2;
    return t2;
}
    
    
    
    
function getWeekFormat(week){
	if(week==1){
		return "一";
	}else if(week==2){
		return "二";
	}else if(week==3){
		return "三";
	}else if(week==4){
		return "四";
	}else if(week==5){
		return "五";
	}else if(week==6){
		return "六";
	}else if(week==7){
		return "日";
	}
}

//获取给定时间的指定日期
function getDayByDays(day){  
	
    var today = new Date();  
    var targetday_milliseconds=today.getTime() + 1000*60*60*24*day;          
    today.setTime(targetday_milliseconds); //注意，这行是关键代码    
    var tYear = today.getFullYear();  
    var tMonth = today.getMonth()+1;  
    var tDate = today.getDate();  
    return tYear+"/"+tMonth+"/"+tDate;  
}  
function doHandleMonth(month){  
    var m = month;  
    if(month.toString().length == 1){  
       m = "0" + month;  
    }  
    return m;  
}  



Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
 	Ext.Msg = top.Ext.Msg; 
  	new Ext.Viewport({
        layout : 'border',
        items : autoBackupPeriodPanel,
        listeners:{
           	afterrender: function( win,obj){
           		Ext.getCmp('queryWeekComboPanel').setVisible(false);
				Ext.getCmp('queryMonthComboPanel').setVisible(false);
           	}
		}
	});
});

