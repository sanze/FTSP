/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
var myPageSize = 200;
var storedata = [ [ '1', '公共板卡' ], [ '2', '业务板卡' ] ];
var ctpCategoryStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
ctpCategoryStore.loadData(storedata);

var store = new Ext.data.Store({
	//1代表查
	url : 'network!ctpNameCustomList.action',
	baseParams : {
		"limit":200
	},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["NWA_UNIT_TYPE_ID","SORT_B","SORT_A"])
});
store.load();

var pageTool = new Ext.PagingToolbar({
	id : 'pageTool',
	pageSize : myPageSize,// 每页显示的记录值
	store : store,
	displayInfo : true,
	displayMsg : '当前 {0} - {1} ，总数 {2}',
	emptyMsg : "没有记录"
});


var tbar = new Ext.Toolbar({
	id : 'tbar',
	items : [  {
					icon : '../../../resource/images/btnImages/add.png',
					xtype : 'button',
					text:'新增',
					handler:function(){
						addCtpCategory();
					}
				},{
					xtype : 'tbspacer',
					width : 10
				},{
					icon : '../../../resource/images/btnImages/delete.png',
					xtype : 'button',
					text:'删除',
					handler:function(){
						deleteCtpCategory();
					}
				},{
					xtype : 'tbspacer',
					width : 10
				},{
					icon : '../../../resource/images/btnImages/modify.png',
					xtype : 'button',
					text:'保存',
					handler:function(){
						saveCtpCategory();
					}
				}]
});
// modify 2015/09/15 fanguangming start
//var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({
//	singleSelect : true
//});
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel();
//modify 2015/09/15 fanguangming end
var columnModel = new Ext.grid.ColumnModel({
	defaults : {
		 sortable : true,
		 forceFit : true,
		 align:'left'/*,
		 renderer: function (data, metadata, record, rowIndex, columnIndex, store) {       
	       	    metadata.attr = 'ext:qtip="' +data+'"';   //关键  
	       	    return data ;     
       	 } */
	},
	columns:[ new Ext.grid.RowNumberer({
					width : 26
				}),
	          checkboxSelectionModel,
				{
					id : 'NWA_UNIT_TYPE_ID',
					header : 'id',
					dataIndex : 'NWA_UNIT_TYPE_ID',
					width : 80,
					hidden : true
				},
				{
					id:'SORT_B',
					header:'板卡小类名称',
					dataIndex:'SORT_B',
					width:150,
					editor: {
	                    allowBlank: false
	                }
				},
				{
					id:'SORT_A',
					header:'板卡大类',
					dataIndex:'SORT_A',
					width:150,
					renderer:function(value){
						if(value == '1'){
							return '公共板卡';
						}else if(value == '2'){
							return '业务板卡';
						}
					},
					editor:new Ext.form.ComboBox({
						id:'unitType',
						name:'termtype1',
						mode :'local',
						lazyRender:true,  
						store : ctpCategoryStore,
						displayField : "displayName",
						valueField : 'value',
						forceSelection:true,  
						typeAhead: true,
						triggerAction: 'all'})
				}
]});

// ==========================page=============================
var gridPanel = new Ext.grid.EditorGridPanel({
	id : "gridPanel",
	region : "center",
	cm : columnModel,
	selType: 'cellmodel',
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	forceFit : true,
	stateful : true,
	bbar : pageTool,
	tbar : tbar
});
//==========================add ctp category=============================
function addCtpCategory(){
	var addCtpCategoryWindow=new Ext.Window({
		id:'addCtpCategoryWindow',
		title:'新增板卡类别',
		width:300,  
		height:125,
		isTopContainer : true,
		modal : true,
	items : [{
		border:false, 
			bodyStyle : 'padding:5px 5px 0 5px',
		items:[{
			layout : 'form', 
			border : false,
			labelWidth:90,
			items:[{
				id:'sortB',
				xtype:'textfield',
				fieldLabel:'板卡小类名称',
				sideText:'<font color=red>*</font>',
				allowBlank:false,
				anchor : '95%' 
			},{
				id:'sortA',
				xtype:'combo', 
				labelSeparator : "：",
				fieldLabel:'板卡大类',
				store : ctpCategoryStore,
				displayField : "displayName",
				valueField : 'value',
				triggerAction : 'all',
				mode : 'local',
				anchor : '95%'
			}] 
		}]
	}],
	buttons : [ {
		text : '确定',
		handler : function() { 
			insertCtpCategory();
		}
	}, {
		text : '取消 ',
		handler : function() {
			var win = Ext.getCmp('addCtpCategoryWindow');
			if (win) {
				win.close();
			}
		}
	} ]
});   
	addCtpCategoryWindow.show();
}
function insertCtpCategory(){
	var jsonData = {
			"sortB" : Ext.getCmp("sortB").getValue(),
			"sortA" : Ext.getCmp("sortA").getValue()};
// add 2015/09/16 fanguangming start
	if (Ext.getCmp("sortB").getValue() == "" || Ext.getCmp("sortA").getValue() == "") {
		Ext.Msg.alert('提示', '板卡小类名称或板卡大类为空请重新填写！');
		return;
	} else {
// add 2015/09/16 fanguangming end
		Ext.Ajax.request({
			url : 'network!addCtpCategory.action',
			type : 'post',
			params : jsonData,
			success : function(response) {
				var obj = Ext.decode(response.responseText);
				Ext.getBody().unmask();
				
				if (obj.returnResult == 1) {
					Ext.Msg.alert("信息", obj.returnMessage, function(r) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
						Ext.Msg.confirm('信息', '继续添加？', function(btn) {
							if (btn == 'yes') {
								
							} else {
								// 关闭修改任务信息窗口
								var win = Ext.getCmp('addCtpCategoryWindow');
								if (win) {
									win.close();
								}
							}
						});
					});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", obj.returnMessage);
				}
			},
			error : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("错误", response.responseText);
			}
		});
// add 2015/09/16 fanguangming start
	}
// add 2015/09/16 fanguangming end	
}
//==========================delete ctp category=============================
function deleteCtpCategory(){
	var cell = gridPanel.getSelectionModel().getSelections();
	if (cell.length <= 0) {
		Ext.Msg.alert("提示", "请选择需要删除的板卡类别！");
		return;
	}
	Ext.Msg.confirm('提示','删除板卡类别。</br></br>确认删除?',
		function(btn){
			if(btn=='yes'){
// mod 2015/09/15 fanguangming Start
//			 var jsonData = {
//					"unitTypeId" : cell[0].get("NWA_UNIT_TYPE_ID")
//			  };			
//			  Ext.Ajax.request({
//		      url:'network!deleteCtpCategory.action',
//		      method:'Post',
//		      params:jsonData,
//		      success: function(response) {
//		    	  var obj = Ext.decode(response.responseText);
//		    	  	if(obj.success == false){
//		    	  		Ext.Msg.alert("提示",obj.msg);
//		    	  	}else{
//		    	  		store.reload();
//		    	  	}
//				    },	
//		    error:function(response) {
//		    	Ext.Msg.alert("错误",response.responseText);
//		    },
//		    failure:function(response) {
//		    	Ext.Msg.alert("错误",response.responseText);
//		    }		   
//			   });
				var unitTypeList = new Array();
				for (var i = 0; i < cell.length; i++) {
					unitTypeList.push(cell[i].get("NWA_UNIT_TYPE_ID"));
				}
				var jsonData = {
						"unitTypeList" : unitTypeList
				};	
				Ext.Ajax.request({
					url:'network!deleteCtpCategory.action',
					method:'Post',
					params:jsonData,
					success: function(response) {
						var obj = Ext.decode(response.responseText);
						if(obj.success == false){
							Ext.Msg.alert("提示",obj.msg);
						}else{
							store.reload();
						}
				    },	
				    error:function(response) {
				    	Ext.Msg.alert("错误",response.responseText);
				    },
				    failure:function(response) {
				    	Ext.Msg.alert("错误",response.responseText);
				    }		   
				});
// mod 2015/09/15 fanguangming Start
			}
		});
	}
//==========================save ctp category=============================
function saveCtpCategory(){
	var cell = store.getModifiedRecords();
//2015/09/15 fanguangming add start
	if (cell.length <= 0) {
		Ext.Msg.alert("提示", "没有需要保存的内容！");
		return;
	}
//2015/05/15 fanguangming add end	
	if (cell.length > 0) {
		gridPanel.getEl().mask("正在执行,请稍候...");
		var unitTypeList = new Array();
		
		for ( var i = 0; i < cell.length; i++) {
			var unitTypeValue = {
					'NWA_UNIT_TYPE_ID' : cell[i].get('NWA_UNIT_TYPE_ID'),
					'SORT_B' : cell[i].get('SORT_B'),
					'SORT_A' : cell[i].get('SORT_A')
			};
			unitTypeList.push(Ext.encode(unitTypeValue));
		}
		var jsonData = {
			"modifyList" : unitTypeList
		};
		// 提交修改，不然store.getModifiedRecords();数据会累加
		store.commitChanges();
		Ext.Ajax.request({
			url : 'network!updateCtpCategory.action',
			params : jsonData,
			method : 'POST',
			success : function(response) {
				gridPanel.getEl().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				
				if (result.returnResult == 1) {
					Ext.Msg.alert("信息", result.returnMessage, function(r) {
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					});
				}
			},
			failure : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			},
			error : function(response) {
				gridPanel.getEl().unmask();
				Ext.Msg.alert("提示", "保存修改出错");
			}
		});
	}
}
Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		parent.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	var win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [gridPanel],
		renderTo : Ext.getBody()
	});
	win.show();
	
});