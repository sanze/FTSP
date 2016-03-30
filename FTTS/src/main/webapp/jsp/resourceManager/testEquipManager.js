var nodes=null;
//区域树     
function getTree(field,level){  
	var win = new Ext.Window({ 
		layout : 'fit',
		modal : true,
		height : 400,
		width : 260,
		pageX :field.getPosition()[0],
		pageY :field.getPosition()[1]+20,
		items : [{
			id:'area',
			xtype : "area",
			maxLevel:level
		}],
		buttons: [{ 
			text: '确定',
			handler: function(){
				nodes = Ext.getCmp("area").getSelectedNodes();
				if(nodes.total >0){ 
					field.setValue(nodes.nodes[0].text);
					win.close();
				}
			}
		},{
			text: '取消',
			handler: function(){ win.close(); }
		}]
	});
	win.show();
	field.blur();
}  

//区域树
var areaTree = {
	xtype: 'textfield',
	id : 'areaField', 
	readOnly : true,
	emptyText:'选择区域',
	width: 110,
	listeners : {
		'focus' : function(field){ 
			getTree(this,12); 
			field.blur();
		}
	}
}; 
 
//设备名
var name_={
	xtype: 'textfield',
	id:'name_',   
	emptyText:'输入设备名...',
	width: 110
};
//厂家
var factoryMapping = [ [ 11, '昕天卫 ' ], [ 12, '中博' ]]; 
var factoryStore = new Ext.data.ArrayStore({
	fields : [{
		name : 'value' 
	}, {
		name : 'displayName'
	}]
});
factoryStore.loadData(factoryMapping);
function factoryRenderer(v, m, r) {
	return (typeof v == 'number' && factoryMapping[v-11] != null) ? factoryMapping[v-11][1] : v;
}
//状态
var statusMapping = [ [ 0, '未知' ], [ 1, '连接正常' ], [ 2, '连接异常' ], [3, '网络中断'], [ 4, '连接中断'] ]; 
var statusStore = new Ext.data.ArrayStore({
	fields : [{
		name : 'value' 
	}, {
		name : 'displayName'
	}]
});
statusStore.loadData(statusMapping);
function statusRenderer(v, m, r) {
	return (typeof v == 'number' && statusMapping[v] != null) ? statusMapping[v][1] : v;
} 
//设备类型
var typeMapping = [ [ 111, 'RTU ' ], [ 112, 'CTU' ]]; 
var typeStore = new Ext.data.ArrayStore({
	fields : [{
		name : 'value' 
	}, {
		name : 'displayName'
	}]
});
typeStore.loadData(typeMapping);
function typeRenderer(v, m, r) {
	return (typeof v == 'number' && typeMapping[v-111] != null) ? typeMapping[v-111][1] : v;
} 

var store = new Ext.data.Store({
	url: 'resource!queryRC.action',
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["RC_ID","NAME","NUMBER", "TYPE","FACTORY","STATUS","IP","PORT",
	    "roomName", "stationName", "areaName", "TIMEOUT","NOTE" ])
});
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
		id : 'NAME',
    	header: '设备名称',
    	dataIndex: 'NAME' 
    },{
    	id:'NUMBER',
    	header: '设备编号',
    	dataIndex: 'NUMBER' 
    },{
    	id:'TYPE',
    	header: '设备类型',
    	dataIndex: 'TYPE',
    	renderer:typeRenderer
    },{
    	id:'FACTORY',
    	header: '厂家',
    	dataIndex: 'FACTORY',
    	renderer:factoryRenderer
    },{
    	id:'STATUS',
    	header: '状态',
    	dataIndex: 'STATUS',
    	renderer:statusRenderer
    },{
    	id:'IP',
    	header: 'IP地址',
    	dataIndex: 'IP',
		width:100
    },{
    	id:'PORT',
    	header: '端口',
    	dataIndex: 'PORT' 
    },{
    	id:'roomName',
    	header: '机房',
    	dataIndex: 'roomName' 
    },{
    	id:'stationName',
    	header: '局站',
    	dataIndex: 'stationName' 
    },{  
    	id:'areaName',
    	header:'区域',  
    	dataIndex: 'areaName' 
    },{	
    	id:'TIMEOUT',
    	header: '命令超时',
    	dataIndex: 'TIMEOUT' 
    		
    },{	
      	id:'NOTE',
    	header: '备注',
    	dataIndex: 'NOTE' 
    }]
});

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : 200,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
}); 

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
	tbar : ['-','区域：',areaTree,'-','设备名：',name_,'-',{
		xtype : 'button', 
		text : '重置',
		icon : '../../resource/images/btnImages/refresh.png',
		handler :function(){   
			Ext.getCmp("areaField").setValue(null);
			nodes=null;
			Ext.getCmp('name_').setValue(null); 
		}
	},{
		xtype : 'button', 
		text : '查询',
		icon : '../../resource/images/btnImages/search.png',
		handler :queryRC
	},"-",{
		xtype : 'button',
		icon : '../../resource/images/btnImages/add.png',
		text : '新增', 
		handler :function (){
			modRC(0);
		}
	},{
		xtype : 'button',
		icon : '../../resource/images/btnImages/delete.png',
		text : '删除', 
		handler : deleteRC
	}, {
		xtype : 'button',
		icon : '../../resource/images/btnImages/modify.png',
		text : '修改', 
		handler:function (){
			modResourceSat(1);
		}
	},  "-",{
		xtype : 'button', 
		icon : '../../resource/images/btnImages/config_add.png',
		text :'配置', 
		handler:configRC
	}]
}); 

//查询按钮调用的方法
function queryRC(){
	store.baseParams.name=Ext.getCmp('name_').getValue();
	store.baseParams.node=nodes?nodes.nodes[0].node.id:null;
	store.baseParams.limit = 200; 
	store.load({ 
		callback : function(records, options, success){
			if(success){ 		    	
			}else{
				Ext.Msg.alert("错误",'查询失败!');
			}
		}
	});
}

function deleteRC(){
	var count = gridPanel.getSelectionModel().getCount();
	if(count==0){
		Ext.Msg.alert("提示","请选择一条数据！");
		return;
	} else {
		Ext.Msg.confirm("提示", "确认删除:"+gridPanel.getSelectionModel().getSelected().get("NAME"), function(btn) {
			if (btn == "yes") {
				Ext.Ajax.request({
					url : "resource!deleteRC.action",
					method : "POST",
					params:{
						cellId:gridPanel.getSelectionModel().getSelected().get("RC_ID")
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

function modRC(auth,prop) {
	var isAdd=(auth==0); 
	prop=prop?prop:{}; 
	
	var RCForm = new Ext.FormPanel({
	    id : "RCForm",
		frame : false,
		border : false,
		bodyStyle : 'padding:20px 10px 20px 40px', 
		labelWidth : 55,
		labelAlign : 'left',
		forcefit:true,
		split : true,
		items : [{
			xtype : 'combo', 
			id : 'factory',
			name : 'factory',
			fieldLabel :'设备厂家',  
			store : factoryStore,
			displayField : "displayName",
			valueField : 'value',
			triggerAction : 'all',  
			editable:false,
			mode : "local",
			value : prop.FACTORY, 		
			sideText : isAdd?'<font color=red>*</font>':'',
			allowBlank : false,
			readOnly:isAdd?false:true,
			width : 200	
		},{
			xtype : 'combo', 
			id : 'type',
			name : 'type',
			fieldLabel :'设备类型',  
			store : typeStore,
			displayField : "displayName",
			valueField : 'value',
			triggerAction : 'all',  
			editable:false,
			mode : "local",
			value : prop.TYPE, 		
			sideText :  isAdd?'<font color=red>*</font>':'',
			allowBlank : false,
			readOnly:isAdd?false:true,
			width : 200	
		},{
			xtype : 'textfield',
			id : 'number',
			name : 'number',
			fieldLabel : '设备编号',
			maxLength: 10,
			value : prop.NUMBER, 
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			width:200
		},{
			xtype : 'textfield',
			id : 'name',
			name : 'name',
			fieldLabel : '设备名称',
			value : prop.NAME, 
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			width:200
		},{
			xtype : 'textfield',
			id : 'ip',
			name : 'ip',
			regex:/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
			fieldLabel : 'IP地址',
			value : prop.IP,  
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			width:200
		},{
			xtype : 'numberfield',
			id : 'port',
			name : 'port',
			fieldLabel : '端口',
			value : prop.PORT, 
			emptyText:5000,
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			width:200
		},{
			xtype : 'numberfield',
			id : 'timeOut',
			name : 'timeOut',
			fieldLabel : '命令超时',
			emptyText:180,
			value : prop.TIMEOUT, 
			sideText : '<font color=red>*</font>',
			allowBlank : false,
			width:200
		},{
			xtype : 'panel',
			border:false,
			hidden:isAdd?false:true,
			layout:'column',
			fieldLabel : '机房', 	
			items:[{
				xtype:'areaselector',
				id : 'room',
	 			name : 'room',
				readOnly:true, 
			    width:'200',
				allowBlank:false, 
			    targetLevel:12
			},{
				xtype:'label', 
				html:'<font color=red>*</font>'
			}]
		},{
			xtype : 'textfield',
			id : 'note',
			name : 'note',
			fieldLabel : '备注',
			value : prop.NOTE, 
			width : 200
		}]
	});
  	Ext.getCmp('room').rawValue.id = prop.RESOURCE_ROOM_ID;
	var buttons=[]; 
	buttons.push({
		text : '重置',
		handler : function() {
			RCForm.getForm().reset();
		}
	},{
		scope : this,
		text : '确定',
		handler : function() {
			if(RCForm.form.isDirty() && RCForm.form.isValid()){
				var roomId=Ext.getCmp('room').getRawValue().id;
				if (isAdd && roomId==undefined) {
					Ext.Msg.alert("提示",'请选择所属机房！');
					return;
				}				
				RCForm.form.doAction("submit", {
					url : isAdd?"resource!addRC.action":"resource!modRC.action",
					method:"POST",
					params:{ 
						comboFactory:Ext.getCmp('factory').getValue(),
						comboType:Ext.getCmp('type').getValue(),
						ids:Ext.getCmp('room').getRawValue().id,
						cellId:	isAdd?0:prop.RC_ID	
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
			}else{
				win.close(); 
			}
		}
	}, {
		text : '取消',
		handler : function() {
			win.close();
		}
	}); 
	
	var win = new Ext.Window({
		title : isAdd?"新增设备":"修改设备",
		id : 'win', 
		modal : true,
		closable : true,
		plain : true,
		closeAction : 'close',
		width : 400, 
		forcefit:true,
		items : [ RCForm ],
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
		Ext.Ajax.request({
			url : "resource!geRCInfo.action",
			method : "POST",
			params:{
				cellId:gridPanel.getSelectionModel().getSelected().get("RC_ID")
			},
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				if (!!obj) { 
					modRC(auth,obj); 
				} else {
					Ext.Msg.alert('提示', '获取测试设备信息失败！');
				}
			}
		}); 
	}
}
 

function configRC(){
	var cell = Ext.getCmp('grid').getSelectionModel().getSelections();   
	if(cell.length==1){
		var href = location.href;
		var index = href.indexOf("jsp") + 4;
		var preUrl = href.substr(0, index);
		var url = preUrl + "resourceManager/testEquipBayface.jsp?rcId="+cell[0].get("RC_ID");
		parent.addTabPage(url,"设备管理"); 
	}else{
		Ext.Msg.alert('提示', '请选择一条数据！');
		return;
	} 
}


Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif"; 
	document.onmousedown = function() {
//		top.Ext.menu.MenuMgr.hideAll();
	}; 
	Ext.QuickTips.init(); 
	Ext.Ajax.timeout = 90000000; 
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel],
		renderTo : Ext.getBody()
	});   
	pageTool.doLoad(pageTool.cursor);
});
