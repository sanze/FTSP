/**
 * 创建网管分组数据源
 */
if(noAllFlag==undefined){
	var noAllFlag = false;
}
var emsGroupStore = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'report!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	baseParams : {"displayAll" : noAllFlag?false:true,"displayNone" : true},
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
		//var firstValue = records[0].get('BASE_EMS_GROUP_ID');
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
			emsIds='';
			emsNames='';
			emsTextField.setValue(emsNames);
		}
	}
});


var treeParams;
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
			var firstValue = combo.getStore().getRange()[0].get('value');
			// 设置下拉框默认值(这里直接设记录的value值，自动会显示和value对应的displayName)
			combo.setValue(firstValue);
		},
		select : function (combo,record,index){
			if(record.data.value=='year'){
				Ext.getCmp('queryYear').setVisible(true);
				Ext.getCmp('yearText').setVisible(true);
				Ext.getCmp('queryMonth').setVisible(false);
				Ext.getCmp('monthText').setVisible(false);
				Ext.getCmp('queryDay').setVisible(false);
				Ext.getCmp('dayText').setVisible(false);
			}else if(record.data.value=='month'){
				Ext.getCmp('queryYear').setVisible(false);
				Ext.getCmp('yearText').setVisible(false);
				Ext.getCmp('queryMonth').setVisible(true);
				Ext.getCmp('monthText').setVisible(true);
				Ext.getCmp('queryDay').setVisible(false);
				Ext.getCmp('dayText').setVisible(false);
			}else if(record.data.value=='day'){
				Ext.getCmp('queryYear').setVisible(false);
				Ext.getCmp('yearText').setVisible(false);
				Ext.getCmp('queryMonth').setVisible(false);
				Ext.getCmp('monthText').setVisible(false);
				Ext.getCmp('queryDay').setVisible(true);
				Ext.getCmp('dayText').setVisible(true);
			}
		}
	}
});



//====年====@
var queryYear = {
	xtype : 'textfield',
	id : 'queryYear',
	name : 'queryYear',
	fieldLabel : '年度',
	//sideText : '<font color=red>*</font>',
	hidden : false,
	//allowBlank : false,
	readOnly : true,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "queryYear",
				isShowClear : false,
				readOnly : true,
				maxDate :'%y',
				dateFmt : 'yyyy',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){
		}
	}
};



//====月====@
var queryMonth = {
	xtype : 'textfield',
	id : 'queryMonth',
	name : 'queryMonth',
	fieldLabel : '月份',
	//sideText : '<font color=red>*</font>',
	hidden : true,
	//allowBlank : false,
	readOnly : true,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "queryMonth",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M',
				dateFmt : 'yyyy-MM',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){

		}
	}
};




//====天===@
var queryDay = {
	xtype : 'textfield',
	id : 'queryDay',
	name : 'queryDay',
	fieldLabel : '日期',
	//sideText : '<font color=red>*</font>',
	hidden : true,
	//allowBlank : false,
	readOnly : true,
	anchor : '95%',
	cls : 'Wdate',
	listeners : {
		'focus' : function() {
			WdatePicker({
				el : "queryDay",
				isShowClear : false,
				readOnly : true,
				maxDate : '%y-%M-{%d-1}',
				dateFmt : 'yyyy-MM-dd',
				autoPickDate : true
			});
			this.blur();
		},
		'blur':function(t){

			
		}
	}
};



function setQueryTime(){
	var fineness=Ext.getCmp('timeFinenessCombo').getValue();
	if(fineness=='year'){
		time=Ext.getCmp('queryYear').getValue();
	}else if(fineness=='month'){
		time=Ext.getCmp('queryMonth').getValue();
	}else if(fineness=='day'){
		time=Ext.getCmp('queryDay').getValue();
	}
}

//查询按钮调用的方法
function queryByCondition(){
	setQueryTime();
	if(time==null || time==''){
		Ext.Msg.alert("提示",'请选择查询时间');
		return;
	}
	query();
}


