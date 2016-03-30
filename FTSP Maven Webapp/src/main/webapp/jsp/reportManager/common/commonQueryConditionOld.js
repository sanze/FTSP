/**
 * 创建网管分组数据源
 */
var emsGroupStore = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'report!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	baseParams : {"displayAll" : false,"displayNone" : true},
	// record格式
	reader : new Ext.data.JsonReader({
		root : 'rows',//json数据的key值
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
emsGroupStore.load({
	// 回调函数
	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
		// 获取下拉框的第一条记录
		//var firstValue = records[0].get('BASE_EMS_GROUP_ID');
		// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
		//Ext.getCmp('emsGroupCombo').setValue(firstValue);
	}
});
// 创建网管分组下拉框
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : emsGroupStore,// 数据源
	valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
	displayField : 'GROUP_NAME',// 下拉框显示值
	editable : false,
	triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
	width :120,
	resizable: true,
	listeners : {// 监听事件
		select : function(combo, record, index) {
			
			
		}
	}
});


var emsTextField = new Ext.form.TextField({
	id : 'emsTextField',
	fieldLabel : '网管',
	width :120,
	listeners: {
        focus:function(field){
        	var groupId=Ext.getCmp('emsGroupCombo').getValue();
        	if(groupId==undefined || groupId==null || groupId==''){
        		Ext.Msg.alert("提示",'请先选择网管分组');
        		return;
        	}
        	if(groupId==-99){
        		treeParams={};
        	}else if(groupId==-1){
        		treeParams={};
        		treeParams.rootId=groupId;
        		treeParams.rootType=0;
        	}else{
        		treeParams={};
        		treeParams.rootId=groupId;
        		treeParams.rootType=1;
        	}
        	getTree(5,field.fieldLabel,field.getPosition()[0],field.getPosition()[1]);
        	field.blur();// 赋值后，主动失去焦点，否则不能立即出发下了个获取焦点事件
        }
    }
});




var emsIds;
var emsNames;
//填入相应的信息
function fillChooseInfo(){
	emsTextField.setValue(emsNames);
}
/**
 * 弹出树状结构
 * @param title 标题
 * @param pageX	横坐标
 * @param pageY 从坐标
 */
//获取树状数据
function getTree(type,title,pageX,pageY){
	var treeurl="../common/reportTreeCommon1.jsp?"+Ext.urlEncode(treeParams);
	var url ="<iframe  id="+title+"'_panel' name = "+title+"'_panel' src =" + treeurl+ " height='100%' width='100%' frameBorder=0 border=0/>";
	var treeWindow=new Ext.Window({
        id:'treeWindow',
        title : title,
		width : 350,
		height : (Ext.getBody().getHeight()-60)*0.7,
		pageX :pageX,
		pageY :pageY+20,
        isTopContainer : true,
        modal : true,
        autoScroll:true,
		maximized:false,
        html:url
     });
	treeWindow.show();
}





//时间粒度控件
var timeFinenessCombo = new Ext.form.ComboBox({
	id : 'timeFinenessCombo',
	fieldLabel : '查询粒度',
	mode : 'local',
	store : new Ext.data.ArrayStore({
				fields : ['value','displayName'],
				data : [['year','年'],['month','月'],['day','日']] 
			}),
	valueField : 'value',
	displayField : 'displayName',
	triggerAction : 'all',// 表示下拉框每次加载所有的值，否则选择某个值后，下拉款里只有一个值
	width : 60,
	listeners : {
		// 设置下拉框的第一条数据为默认值
		beforerender : function(combo){
			// 获取下拉框的第一条记录
			var firstValue = combo.getStore().getRange()[1].get('value');
			// 设置下拉框默认值(这里直接设记录的value值，自动会显示和value对应的displayName)
			combo.setValue(firstValue);
		},
		select : function (combo,record,index){
			Ext.getCmp('queryYearCombo').clearValue();
			Ext.getCmp('queryMonthCombo').clearValue();
			Ext.getCmp('queryDayCombo').clearValue();
			if(record.data.value=='year'){
				Ext.getCmp('queryYear').setVisible(true);
				Ext.getCmp('queryYearCombo').setVisible(true);
				Ext.getCmp('queryMonth').setVisible(false);
				Ext.getCmp('queryMonthCombo').setVisible(false);
				Ext.getCmp('queryDay').setVisible(false);
				Ext.getCmp('queryDayCombo').setVisible(false);
			}else if(record.data.value=='month'){
				Ext.getCmp('queryYear').setVisible(true);
				Ext.getCmp('queryYearCombo').setVisible(true);
				Ext.getCmp('queryMonth').setVisible(true);
				Ext.getCmp('queryMonthCombo').setVisible(true);
				Ext.getCmp('queryDay').setVisible(false);
				Ext.getCmp('queryDayCombo').setVisible(false);
			}else if(record.data.value=='day'){
				Ext.getCmp('queryYear').setVisible(true);
				Ext.getCmp('queryYearCombo').setVisible(true);
				Ext.getCmp('queryMonth').setVisible(true);
				Ext.getCmp('queryMonthCombo').setVisible(true);
				Ext.getCmp('queryDay').setVisible(true);
				Ext.getCmp('queryDayCombo').setVisible(true);
			}
		}
	}
});


//加载年数据源
var queryYearStore=new Ext.data.ArrayStore({
	fields:[
		   {name:"value",mapping:"value"},
		   {name:"displayName",mapping:"displayName"}
   ]
});

//年控件
var queryYearCombo = new Ext.form.ComboBox({
	id : 'queryYearCombo',
	fieldLabel : '年',
	mode : 'local',
	store : queryYearStore,
	valueField : 'value',
	displayField : 'displayName',
	triggerAction : 'all',// 表示下拉框每次加载所有的值，否则选择某个值后，下拉款里只有一个值
	width : 60,
	resizable: true,
	listeners : {
		beforequery:function(queryEvent){
			var arr=[];
            var myDate=new Date();
            var nowYear=myDate.getFullYear();
			for(var i=nowYear;i>=2012;i--){
				var json={};
				json.value=i;
				json.displayName=i;
				arr.push(json);
			}
			queryYearStore.loadData(arr);  
		},
		select : function (combo,record,index){
			Ext.getCmp('queryMonthCombo').clearValue();
			Ext.getCmp('queryDayCombo').clearValue();
		}
	}
});


//加载月数据源
var queryMonthStore=new Ext.data.ArrayStore({
	fields:[
		   {name:"value",mapping:"value"},
		   {name:"displayName",mapping:"displayName"}
   ]
});

//月控件
var queryMonthCombo = new Ext.form.ComboBox({
	id : 'queryMonthCombo',
	fieldLabel : '月',
	mode : 'local',
	store : queryMonthStore,
	valueField : 'value',
	displayField : 'displayName',
	triggerAction : 'all',// 表示下拉框每次加载所有的值，否则选择某个值后，下拉款里只有一个值
	width : 60,
	resizable: true,
	listeners : {
		beforequery:function(queryEvent){
			var year = Ext.getCmp('queryYearCombo').getValue();
			if(year==undefined || year==null || year==''){
				queryMonthStore.loadData([]);  
				return;
			}
			var arr=[];
			for(var i=1;i<=12;i++){
				var json={};
				json.value=i;
				json.displayName=i;
				arr.push(json);
			}
			queryMonthStore.loadData(arr);  
		},
		select : function (combo,record,index){
			Ext.getCmp('queryDayCombo').clearValue();
		}
	}
});


//加载天数据源
var queryDayStore=new Ext.data.ArrayStore({
	fields:[
		   {name:"value",mapping:"value"},
		   {name:"displayName",mapping:"displayName"}
   ]
});

//日控件
var queryDayCombo = new Ext.form.ComboBox({
	id : 'queryDayCombo',
	fieldLabel : '日',
	mode : "local",
	store : queryDayStore,
	valueField : 'value',
	displayField : 'displayName',
	triggerAction : 'all',// 表示下拉框每次加载所有的值，否则选择某个值后，下拉款里只有一个值
	width : 60,
	resizable: true,
	listeners : {
		beforequery:function(queryEvent){
			var year = Ext.getCmp('queryYearCombo').getValue();
			var month = Ext.getCmp('queryMonthCombo').getValue();
			if(year==undefined || year==null || year=='' || month==undefined || month==null || month==''){
				queryDayStore.loadData([]);  
				return;
			}
			var days=getLastDay(year,month);
			var arr=[];
			for(var i=1;i<=days;i++){
				var json={};
				json.value=i;
				json.displayName=i;
				arr.push(json);
			}
			queryDayStore.loadData(arr);  
		},
		select : function (combo,record,index){
		}
	}
});


function getLastDay(year,month){        
	 var new_year = year;    //取当前的年份         
	 var new_month = month++;//取下一个月的第一天，方便计算（最后一天不固定）         
	 if(month>12){           //如果当前大于12月，则年份转到下一年               
	  new_month -=12;        //月份减         
	  new_year++;            //年份增         
	 }        
	 var new_date = new Date(new_year,new_month,1);                //取当年当月中的第一天         
	 return (new Date(new_date.getTime()-1000*60*60*24)).getDate();//获取当月最后一天日期         
}  


//定义查询条件panel高度
var queryHeight = 20;
/**
 * 创建查询条件panel
 */
var queryPanel = new Ext.Panel({
	id : 'queryPanel',
	height : queryHeight,
	border : false,
	tbar : ['&nbsp;&nbsp;网管：',emsTextField,'&nbsp;&nbsp;时间粒度：',timeFinenessCombo,'-',{text:'&nbsp;&nbsp;年：',id:'queryYear'},queryYearCombo,'-',
	        {text:'&nbsp;&nbsp;月：',id:'queryMonth'},queryMonthCombo,'-',{text:'&nbsp;&nbsp;日：',id:'queryDay'},queryDayCombo,'-',
    {
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler : queryByCondition
	},{
		xtype : 'button',
		style : 'margin-left : 30px',
		text : '导出',
		icon : '../../../resource/images/btnImages/export.png',
		handler : exportByCondition
	}]
	
});


//查询按钮调用的方法
function queryByCondition(){
	setQueryTime();
	if(time==null || time==''){
		Ext.Msg.alert("提示",'请选择查询时间');
		return;
	}
	query();
}

function exportByCondition(){
	setQueryTime();
	exportInfo();
}

function setQueryTime(){
	var fineness=Ext.getCmp('timeFinenessCombo').getValue();
	var year = Ext.getCmp('queryYearCombo').getValue();
	var month = Ext.getCmp('queryMonthCombo').getValue();
	var day = Ext.getCmp('queryDayCombo').getValue();
	if(fineness=='year'){
		if(year==null || year==''){
			
		}else{
			time=year+"";
		}
	}else if(fineness=='month'){
		if(year==null || year==''){
			
		}else if(month==null || month==''){
			
		}else{
			month=month<10?'0'+month:month;
			time=year+"-"+month;
		}
	}else if(fineness=='day'){
		if(year==null || year==''){
			
		}else if(month==null || month==''){
			
		}else if(day==null || day==''){
			
		}else{
			month=month<10?'0'+month:month;
			day=day<10?'0'+day:day;
			time=year+"-"+month+"-"+day;
		}
	}
}
Ext.onReady(function() {
	Ext.getCmp('queryDay').setVisible(false);
	Ext.getCmp('queryDayCombo').setVisible(false);
});
