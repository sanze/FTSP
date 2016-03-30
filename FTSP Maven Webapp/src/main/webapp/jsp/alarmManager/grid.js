// 时间粒度控件
var timeTypeCombo = new Ext.form.ComboBox({
	id : 'timeTypeCombo',
	fieldLabel : '时间粒度',
	mode : 'local',
	store : new Ext.data.ArrayStore({
				fields : ['value','displayName'],
				data : [['year','年'],['month','月'],['day','天']] 
			}),
	valueField : 'value',
	displayField : 'displayName',
	triggerAction : 'all',// 表示下拉框每次加载所有的值，否则选择某个值后，下拉款里只有一个值
	width : 100,
	listeners : {
		// 设置下拉框的第一条数据为默认值
		beforerender : function(combo){
			// 获取下拉框的第一条记录
			var firstValue = combo.getStore().getRange()[0].get('value');
			// 设置下拉框默认值(这里直接设记录的value值，自动会显示和value对应的displayName)
			combo.setValue(firstValue);
		},
		select : function (combo,record,index){
//			Ext.getCmp('queryTime').getValue().format('Y-m-d');
		}
	}
});

// 时间控件
var queryTime = new Ext.form.DateField({
	id : 'queryTime',
	fieldLabel : '查询时间',
	format : 'Y-m-d'
});

// 封装查询参数
var paramMap = {'queryYear':'2013','timeType':'year'};
// 创建数据源
var store = new Ext.data.Store({
	url : 'report!getAlarmByTime.action',// 数据请求地址
	baseParams : {// 请求参数
		"jsonString" : Ext.encode(paramMap),
		'limit':200
	},
	reader : new Ext.data.JsonReader({// 
				totalProperty : 'total',
				root : "rows"
			}, ["RESOURCE_NE_ID", "RESOURCE_NE_NAME", "FTSP_NE_NAME",
					"NE_NAME"])
});
// 创建表格选择模型(多选、单选.....)
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
//	singleSelect ：true,// 表示只能单选，默认false,
//	sortable : true,//表示选择框列可以排序，默认fasle
});

// 创建表格列模型
var cm = new Ext.grid.ColumnModel({
	defaults : {// 所有列默认的属性
		sortable : true
	// 表示所有列可以排序
	},
	columns : [ new Ext.grid.RowNumberer({
//		header : '序号',// 行号列的列名,默认为空
		width : 26,// 行号列宽，一般不用设置，否则可能会和行的颜色有冲突
		locked : false
	}), checkboxSelectionModel, {
		id : 'RESOURCE_NE_ID',
		header : '比对表Id',
		dataIndex : 'RESOURCE_NE_ID',
		hidden : true,// hidden colunm
		width : 100
	}, {
		id : 'RESOURCE_NE_NAME',
		header : '资源设备网元标识1',
		dataIndex : 'RESOURCE_NE_NAME',
		hidden : false,// hidden colunm
		width : 350,
		editor : new Ext.form.TextField({
			allowNegative : true,
			maxLenth : 256
		})
	}, {
		id : 'FTSP_NE_NAME',
		header : '网管网元名称',
		dataIndex : 'FTSP_NE_NAME',
		hidden : false,// hidden colunm
		width : 200,
		editor : new Ext.form.TextField({
			allowNegative : true,
			maxLenth : 256
		})
	}, {
		id : 'NE_NAME',
		header : '资源系统内部网元名',
		dataIndex : 'NE_NAME',
		hidden : true,// hidden colunm
		width : 200
	} ]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});

var gridPanel = new Ext.grid.EditorGridPanel({
	region : "center",
	// title:'用户管理',
	cm : cm,
	store : store,
	// autoExpandColumn: 'roleName', // column with this id will be
	// expanded
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	// view: new Ext.ux.grid.LockingGridView(),
	forceFit : true,
	tbar : ['时间粒度:',timeTypeCombo,'-','查询时间:',queryTime,'-',{
			text : '查询',
			icon : '../../resource/images/btnImages/search.png',
			handler : queryAlarm
			}],
	bbar : pageTool
	
});

//加载数据源
store.load({
	 callback : function(records, options, success){//回调函数
	      //  top.Ext.getBody().unmask();
	  		  if(success){
	          	if(records.length == 0){
	          		  Ext.Msg.alert("信息","查询结果为空！"); 
	          		          //刷新列表
	          		    }                		    	
	            }else{
	//			              	    top.Ext.getBody().unmask();
	          		Ext.Msg.alert("错误",'查询失败，请重新查询！');
	           }                   			
	      }
	}
);

// 查询按钮调用的方法
function queryAlarm(){
	var timeType = Ext.getCmp('timeTypeCombo').getValue();
	alert(timeType);
}

//创建border布局的头部(north)
var titlePanel = new Ext.Panel({
	title : '完整的测试实例',
	region : 'north'
});

// 创建TabPanel(可以切换的tab)
var tabPanel = new Ext.TabPanel({
	region : 'center',// boder布局的主题部分
	activeTab : 0,// 设置默认打开第几个tab，0表示第一个
	items : [{
		// 注意:如果tabPanel里面放的是grid表格，那么这里布局必须为fit，否则表格显示有问题(列名和值不显示)
		layout : 'fit',
		title : '表格',
		items :gridPanel
	},{
		layout : 'fit',
		title : 'tab2',
		html :'fdf'
	}]
});

// Ext加载
Ext.onReady(function(){
	var v = new Ext.Viewport({
		layout : 'border',// 将整个页面分为上(north)、下(south)、左(west)、右(east)、中(center)5个模块
		items : [titlePanel,tabPanel]
	});
	v.show();
});

