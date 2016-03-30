//局站类型
var typeMapping = [ [ 1, '调度大楼' ], [ 2, '微波站' ], [3, '光缆分支'], [4, '生产基地'], [5, '供电营业所'],
                    [ 6, '牵引站'], [7, '火电厂'], [8, '水电厂'], [ 9, '抽水蓄能站'], [ 10, '核电厂'],
                    [ 11, '500KV变电站' ], [ 12, '220KV变电站' ], [ 13, '110KV变电站' ],
                    [14, '35KV变电站及供电所' ] ];
function typeRenderer(v, m, r) {
	return (typeof v == 'number' && typeMapping[v - 1] != null) ? typeMapping[v - 1][1] : v;
}
var typeStore = new Ext.data.ArrayStore({
	fields : [{
		name : 'value' 
	}, {
		name : 'displayName'
	}]
});
typeStore.loadData(typeMapping);

//局站详细信息的Store
var store = new Ext.data.Store({
	url : 'area!getStationGrid.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["stationId","stationName", "stationNo","areaName","stationType","longitude",
	    "latitude","address", "management", "phone", "note" ])
});

//局站详细信息grid列
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});

var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		width:90
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), checkboxSelectionModel,{
		id : 'stationName',
		header : top.FieldNameDefine.STATION_NAME+'名称',
		dataIndex : 'stationName'
	},  {
		id : 'stationNo',
		header : top.FieldNameDefine.STATION_NAME+'代号',
		dataIndex : 'stationNo'
	}, {
		id : 'area',
		header : '所属'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName',
		width:130
	}, {
		id : 'stationType',
		header : top.FieldNameDefine.STATION_NAME+'类型',
		dataIndex : 'stationType',
		renderer:typeRenderer
	}, {
		id : 'longitude',
		header : '经度',
		dataIndex : 'longitude'
	},{
		id : 'latitude',
		header : '纬度',
		dataIndex : 'latitude'
	}, {
		id : 'address',
		header : '地址',
		dataIndex : 'address',
		width:130
	}, {
		id : 'management',
		header : '联系人',
		dataIndex : 'management'
	}, {
		id : 'phone',
		header : '电话',
		dataIndex : 'phone',
		width:130
	}, {
		id : 'note',
		header : '备注',
		dataIndex : 'note',
		width:150
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

//区域树    
var areaTree = new Ext.form.TextField({
	id : 'areaField', 
	readOnly : true,
	emptyText:'选择区域',
	width: 110,
	listeners : {
		'focus' : function(field){ 
			getTree(this,10);
		}
	}
});  

//局站名称
var stationName={
	xtype: 'textfield',
	id:'statName',   
	emptyText:'模糊搜索',
	width: 110
};

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm, 
	loadMask:true, 
	stripeRows : true, // 交替行效果 
	forceFit : true,
	selModel : checkboxSelectionModel,   
	bbar : pageTool,
	tbar : ['-',top.FieldNameDefine.AREA_NAME+'：',areaTree,'-',ifSubArea,'-',
	        top.FieldNameDefine.STATION_NAME+'名：',stationName,'-',{
		xtype : 'button', 
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler :function(){
			showAll(store,Ext.getCmp('statName').getValue());
		}
	},"-",{
		xtype : 'button',
		icon : '../../../resource/images/btnImages/add.png',
		text : '新增',
		privilege:addAuth,
		handler :function (){
			modStation(addAuth);
		}
	}, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/modify.png',
		text : '修改',
		privilege:modAuth,
		handler:function (){
			modResourceSat(modAuth);
		}
	}, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/delete.png',
		text : '删除',
		privilege:delAuth, 
		handler : delResource
	}, "-",{
		xtype : 'button', 
		text :'关联网元',
		privilege:viewAuth,
		handler:function (){
			var count = gridPanel.getSelectionModel().getCount();
			if(count==0){
				Ext.Msg.alert("提示","请选择一条数据！");
				return;
			} else {
				relate(false,gridPanel.getSelectionModel().getSelected());
			} 
		}
	},'-', {
		xtype : 'button', 
		text :top.FieldNameDefine.STATION_NAME+'详情',
		privilege:viewAuth,
		handler:function (){
			modResourceSat(viewAuth);
		}
	},'-', {
		xtype : 'button', 
		text :'导出',
		privilege:viewAuth,
		icon : '../../../resource/images/btnImages/export.png',
		handler:function (){
			exportParams = store.baseParams;
			exportParams.limit=0;
		    exportParams.exportType = 11;
			exportInfo(store,exportParams);
		}
	}]
});

/**
 * 新增、修改局站
 * @param prop 局站信息的Object对象
 */
function modStation(auth,prop) {
	var isAdd=(auth==addAuth);
	var isView=(auth==viewAuth);
	prop=prop?prop:{};

	var stationForm = new Ext.FormPanel({
		id : "stationForm",
		frame : false,
		border : false,
		bodyStyle : 'padding:20px 10px 20px 40px', 
		labelWidth : 55,
		labelAlign : 'left',
		forcefit:true,
		split : true,
		items : [{
			xtype : 'textfield',
			id : 'name',
			name : 'name',
			fieldLabel :top.FieldNameDefine.STATION_NAME+'名称',
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			readOnly :isView?true:false,
			value : prop.STATION_NAME,  
			width : 200
		},{
			xtype : 'textfield',
			id : 'no',
			name : 'no',
			fieldLabel :top.FieldNameDefine.STATION_NAME+'代号',
			readOnly :isView?true:false,
			value : prop.STATION_NO, 
			width : 200
		},{
			xtype : 'combo',
			id : 'type',
			name : 'type',
			fieldLabel :top.FieldNameDefine.STATION_NAME+'类型',  
			store : typeStore,
			displayField : "displayName",
			valueField : 'value',
			triggerAction : 'all',  
			editable:false,
			mode : "local", 
			disabled:isView?true:false,
			value : prop.TYPE, 		
			width : 200
		}, {
			xtype:'compositefield', 
			id:'composCheck',
			width:220,
			border : false,
			items : [{
	  			xtype : 'areaselector',
	  			id : 'parentArea',
	  			name : 'parentArea',
	  			fieldLabel : '所属'+top.FieldNameDefine.AREA_NAME, 
	 			allowBlank : false,
	  			disabled:isView?true:false,
	  			targetControl:false,
	  			targetLevel:10,
				value : prop.AREA_NAME,
	  			width : 200
	 		},{ 
	 			xtype:'label',
	 			html:'<font color=red>*</font>'
			}]   
		} , {  
    	 layout : "column",
         border : false,
         items : [{ 
                 layout : "form", 
                 border : false,
                 items : [{ 
         			xtype : 'numberfield',
        			id : 'longitude',
        			name : 'longitude',
        			fieldLabel :'经度',
        			value : prop.LONGITUDE, 
        			allowDecimals:true,
        			decimalPrecision : 6,
        			readOnly :isView?true:false,
        			width : 200
        		},{
        			xtype : 'numberfield',
    				id : 'latitude',
    				name : 'latitude',
    				fieldLabel : '纬度',
    				allowDecimals:true,
    				decimalPrecision : 6,
    				value : prop.LATITUDE,
    				readOnly :isView?true:false,
    				width:200
    			}]
             },{ 
                 layout : "form", 
         		 bodyStyle : 'padding:0px 0px 0px 10px', 
                 border : false,
                 items : [{
     				xtype : 'button',
    				id : 'map', 
    				text:'地图',
    				hidden:isView?true:false,
    				handler:function(){
    					var lng = Ext.getCmp("longitude").getValue(),
    						lat = Ext.getCmp("latitude").getValue();
    					openBaiduMap(lng,lat);
    				},
    				height:48 
    			}]
             }]
        },{
			xtype : 'textfield',
			id : 'address',
			name : 'address',
			fieldLabel : '地址',
			value : prop.ADDRESS,
			readOnly :isView?true:false,
			width : 200
		}, {
			xtype : 'textfield',
			id : 'management',
			name : 'management',
			fieldLabel : '联系人',
			value : prop.MANAGEMENT,
			readOnly :isView?true:false,
			width : 200
		}, {
			xtype : 'textfield',
			id : 'phone',
			name : 'phone',
			fieldLabel : '电话',
			value : prop.PHONE,
			readOnly :isView?true:false,
			width : 200
		}, {
			xtype : 'textfield',
			id : 'note',
			name : 'note',
			fieldLabel : '备注',
			value : prop.NOTE,
			readOnly :isView?true:false,
			width : 200
		}]
	});
  	Ext.getCmp('parentArea').rawValue.id = prop.areaId;
	var buttons=[];
	if(isView){
		buttons.push({
			text : '确定',
			handler : function() {
				win.close();
			}
		});
	}else{
		buttons.push({
			scope : this,
			text : '确定',
			handler : function() {
				if(stationForm.form.isDirty() && stationForm.form.isValid()){  
					if(Ext.getCmp('parentArea').getValue()==''){
						Ext.Msg.alert('提示', '请选择区域！');
						return;
					} 
					stationForm.form.doAction("submit", {
						url : isAdd?"area!addStation.action":"area!modStation.action",
						method:"POST",
						params:{
							comboType:Ext.getCmp('type').getValue(),
							newParentId:Ext.getCmp('parentArea').getRawValue().id,
							cellId:	isAdd?0:prop.RESOURCE_STATION_ID	
						},
						waitTitle : "请稍候",
						waitMsg : "正在提交表单数据，请稍候",
						success : function(form, action) {
							Ext.Msg.alert("提示",action.result.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});  
							win.close(); 
						},
						failure : function(form, action) {
							Ext.Msg.alert('提示', action.result.returnMessage);
						}
					});
				}else if(!stationForm.form.isDirty()){
					win.close();
				}
			}
		}, {
			text : '取消',
			handler : function() {
				win.close();
			}
		});
	}
	var title="";
	if(isAdd) title="新增"+top.FieldNameDefine.STATION_NAME;
	else if(isView) title=top.FieldNameDefine.STATION_NAME+"详情";
	else title="修改"+top.FieldNameDefine.STATION_NAME;
	
	var win = new Ext.Window({
		title : title,
		id : 'selAreaWin', 
		modal : true,
		closable : true,
		plain : true,
		closeAction : 'close',
		width : 400, 
		forcefit:true,
		items : [ stationForm ],
		buttons : buttons,
		buttonAlign : "right"
	});
	win.show(this);
}

/**
 * 修改资源的入口
 * 内部根据选择节点的不同跳转到不同的修改函数
 */
function modResourceSat(auth) {
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else {
		modResource(gridPanel.getSelectionModel().getSelected().get("stationId"),11,auth);
	}
}

/**
 * 删除 局站
 */
function delResource() {
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else { 
		Ext.Msg.confirm("提示", "确认删除:"+gridPanel.getSelectionModel().getSelected().get("stationName"), function(btn) {
			if (btn == "yes") {
				// 确认删除
				Ext.Ajax.request({
					url :  "area!delStation.action",
					params : {
						cellId : gridPanel.getSelectionModel().getSelected().get("stationId")
					},
					success : function(response) { 
						var obj = Ext.decode(response.responseText); 
						if (obj.returnResult == 1) {  
							Ext.Msg.alert("提示",obj.returnMessage, function(r) {
								var pageTool = Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
							});  
						} 
						if (obj.returnResult == 0) {  
							Ext.Msg.alert('提示', obj.returnMessage);
						}
					},
					error:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    },
				    failure:function(response) { 
			        	Ext.Msg.alert("异常",response.responseText);
				    }
				});
			}
		});
	}
}

function openBaiduMap(lng,lat){
	var href = "../../gis/gisForStation.jsp?lng="+lng+"&lat="+lat;
	var win = new Ext.Window({
		id : 'mapWin', 
		closable : true,
		plain:true,
		width : 400,
		height: 300,
		html:'<iframe src='+href+' frameborder="0" width="100%" height="100%"/>'
	});
	win.show();
}

function setLngLat(point){
	Ext.getCmp("longitude").setValue(point.lng);
	Ext.getCmp("latitude").setValue(point.lat);
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif"; 
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}; 
	Ext.QuickTips.init(); 
	Ext.Ajax.timeout = 90000000; 
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel],
		renderTo : Ext.getBody()
	});  
	showAll(store,Ext.getCmp('statName').getValue());
});
