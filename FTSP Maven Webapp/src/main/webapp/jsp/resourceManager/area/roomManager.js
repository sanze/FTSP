//机房详细信息的Store
var store = new Ext.data.Store({
	url : 'area!getRoomGrid.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ "roomName","areaName",  "stationName","roomType", 
	     "management", "phone", "note","roomId","stationId"
     ])
});

//局站详细信息grid列
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
	singleSelect : true
});

var cm = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true  
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}),checkboxSelectionModel,{
		id : 'roomName',
		header : '机房名称',
		dataIndex : 'roomName'
	}, {
		id : 'areaName',
		header : '所属'+top.FieldNameDefine.AREA_NAME,
		dataIndex : 'areaName'
	}, {
		id : 'stationName',
		header : '所属'+top.FieldNameDefine.STATION_NAME,
		dataIndex : 'stationName'
	}, {
		id : 'roomType',
		header : '机房类型',
		dataIndex : 'roomType'
	}, {
		id : 'management',
		header : '联系人',
		dataIndex : 'management'
	}, {
		id : 'phone',
		header : '电话',
		dataIndex : 'phone'
	}, {
		id : 'note',
		header : '备注',
		dataIndex : 'note'
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
	width: 110,
	emptyText:'选择区域/局站',
	listeners : {
		'focus' : function(field){ 
			getTree(this,11);
		}
	}
});   

//局站名称
var roomName={
	xtype: 'textfield',
	id:'roomName',   
	emptyText:'模糊搜索',
	width: 110
};

var gridPanel = new Ext.grid.GridPanel({
	id : 'grid',
	region : 'center',
	store : store,
	cm : cm,
	frame : false, 
	loadMask:true, 
	stripeRows : true, // 交替行效果
	selModel : checkboxSelectionModel,   
	bbar : pageTool,
	tbar : ['&nbsp&nbsp',areaTree,'-',ifSubArea,'-','机房名：',roomName,'-',
	  {
		xtype : 'button',
		text : '查询',
		icon : '../../../resource/images/btnImages/search.png',
		handler :function(){
			showAll(store,Ext.getCmp('roomName').getValue());
		}
	},"-",{
		xtype : 'button',
		icon : '../../../resource/images/btnImages/add.png',
		text : '新增',
		privilege:addAuth,
		handler :function (){
			modRoom(addAuth);
		}
	}, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/modify.png',
		text : '修改',
		privilege:modAuth,
		handler:function (){
			modResourceRoom(modAuth);
		}
	}, {
		xtype : 'button',
		icon : '../../../resource/images/btnImages/delete.png',
		text : '删除',
		privilege:delAuth, 
		handler : delRoom
	}, "-", {
		xtype : 'button', 
		text :'关联网元',
		privilege:viewAuth,
		handler:function(){
			var count = gridPanel.getSelectionModel().getCount();
			if(count==0){
				Ext.Msg.alert("提示","请选择一条数据！");
				return;
			} else {
				relate(true,gridPanel.getSelectionModel().getSelected());
			}
		}
	},'-', {
		xtype : 'button', 
		text :'导出',
		icon : '../../../resource/images/btnImages/export.png',
		privilege:viewAuth,
		handler:function (){
			exportParams = store.baseParams;
		    exportParams.exportType = 12;
			exportParams.limit=0;
			exportInfo(store,exportParams);
		}
	}]
});




/**
 * 修改机房信息
 * @param prop 机房信息的Object对象
 */ 
function modRoom(auth,prop) {
	var isAdd=(auth==addAuth);
	prop=prop?prop:{};    
	//机房类型ComboBox
	var combo = new Ext.form.ComboBox({
	    typeAhead: true,
	    triggerAction: 'all',
	    id:"roomType",
        fieldLabel : '机房类型',
	    lazyRender:true,
	    mode: 'local', 
	    store: new Ext.data.ArrayStore({
	        id: 0,
	        fields: [
	            'roomType',
	            'roomName'
	        ],
	        data: [['综合机房', '综合机房'], ['传输机房', '传输机房']]
	    }),
	    valueField: 'roomType',
	    displayField: 'roomName',
	    value: prop.ROOM_TYPE,
	    width : 170
	}); 
	
	var roomForm = new Ext.FormPanel({
		id : "roomForm",
		frame : false,
		border : false,
		bodyStyle : 'padding:30px 20px 20px 40px', 
		labelWidth : 70,
		labelAlign : 'left',
		split : true,
		items : [{
			xtype : 'textfield',
			id : 'name',
			name : 'name',
			fieldLabel : '机房名称',
			allowBlank : false,
			value : prop.ROOM_NAME,
			sideText : '<font color=red>*</font>',
			width : 170
		},combo,{
			xtype:'compositefield',  
			width:180,
			border : false,
			items : [{
				xtype : 'areaselector',
				id : 'parentArea',
				name : 'parentArea',
				fieldLabel : '所属'+top.FieldNameDefine.STATION_NAME,
				allowBlank : false,
				targetLevel:11, 
				value : prop.STATION_NAME,
				width : 170,
				allowBlank:false
	 		},{ 
	 			xtype:'label',
	 			html:'<font color=red>*</font>'
			}]   
		}, {
			xtype : 'textfield',
			id : 'management',
			name : 'management',
			fieldLabel : '联系人',
			value : prop.MANAGEMENT,
			width : 170
		}, {
			xtype : 'textfield',
			id : 'phone',
			name : 'phone',
			fieldLabel : '电话',
			value : prop.PHONE,
			width : 170
		}, {
			xtype : 'textfield',
			id : 'note',
			name : 'note',
			fieldLabel : '备注',
			value : prop.NOTE,
			width : 170
		}]
	});
  	Ext.getCmp('parentArea').rawValue.id = prop.stationId;
	var win = new Ext.Window({
		title : isAdd?"新增机房":"修改机房",
		id : 'modRoomWin',
		layout : 'fit',
		modal : true,
		closable : true,
		plain : true,
		closeAction : 'close',
		width : 360,
		height : 285,
		items : [ roomForm ],
		buttons : [ {
			scope : this,
			text : '确定',
			handler : function() {
				if(roomForm.form.isDirty() && roomForm.form.isValid()){
					var b = Ext.getCmp('parentArea').getValue();
					if(Ext.getCmp('parentArea').getValue()==""){
						Ext.Msg.alert("提示","所属区域不能为空！");
						return;
					}
					roomForm.form.doAction("submit", {
						url : isAdd?"area!addRoom.action":"area!modRoom.action",
						params:{ 
							newParentId:Ext.getCmp('parentArea').getRawValue().id,
							cellId:	isAdd?0:prop.RESOURCE_ROOM_ID	 
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
				}else if(!roomForm.form.isDirty()){
					/*Ext.Msg.alert('提示', "修改成功！");*/
					//关闭窗口
					win.close();
				}
			}
		}, {
			text : '取消',
			handler : function() {
				win.close();
			}
		}],
		buttonAlign : "right"
	});
	win.show(this);
}


/**
 * 修改资源的入口
 * 内部根据选择节点的不同跳转到不同的修改函数
 */
function modResourceRoom(auth) {
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else {
		modResource(gridPanel.getSelectionModel().getSelected().get("roomId"),12,auth);
	}
}

/**
 * 删除 机房
 */
function delRoom() {
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else {
		Ext.Msg.confirm("提示", "确认删除:"+gridPanel.getSelectionModel().getSelected().get("roomName"), function(btn) {
			if (btn == "yes") {
				// 确认删除
				Ext.Ajax.request({
					url :  "area!delRoom.action",
					params : {
						cellId : gridPanel.getSelectionModel().getSelected().get("roomId")
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
						if(obj.returnResult == 0){
							Ext.Msg.alert('提示',obj.returnMessage);
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
	showAll(store,Ext.getCmp('roomName').getValue());
});
