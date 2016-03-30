netLevelComboField.emptyText="全部";
projectComboGridField.allowBlank=false;
//projectComboGridField.emptyText="全部";
cableComboGridField.initialConfig.textField=['CABLES_NAME','CABLES_NO','CABLE_NAME','CABLE_NO'];
cableComboGridField.on('afterrender',
	function(me){
		me.comboGrid.grid.getColumnModel().setHidden(0, false);
		me.comboGrid.grid.getColumnModel().setHidden(1, false);
	}
);
fiberComboGridField.columnWidth=1;
var win;
// ================stores===================
var store = new Ext.data.Store({
	url : 'fiber-link-evaluate!getAllResourceLink.action',
	baseParams : {'limit':200},
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, [ 
	     {name:"sysName",mapping:"SYS_NAME"},
	     
	     {name:"linkName",mapping:"LINK_NAME"},
	     
	     {name:"aNe",mapping:"A_NE_NAME"},
	     {name:"aStationName",mapping:"A_STATION_NAME"},
	     {name:"aPtp",mapping:"A_PTP_NAME"},
	     {name:"zNe",mapping:"Z_NE_NAME"},
	     {name:"zStationName",mapping:"Z_STATION_NAME"},
	     {name:"zPtp",mapping:"Z_PTP_NAME"},
	     
	     {name:"isManual",mapping:"IS_MANUAL"},
	     {name:"cables",convert:function(v,record){if(record["RESOURCE_FIBER_ID"]) return record["CABLES_NAME"]+"("+record["CABLES_NO"]+")";}},
	     {name:"cable",convert:function(v,record){if(record["RESOURCE_FIBER_ID"]) return record["CABLE_NAME"]+"("+record["CABLE_NO"]+")";}},
	     {name:"fiberNo",mapping:"FIBER_NO"},
	     {name:"attMain",mapping:"ATT"},
	     {name:"attOsc",mapping:"ATT_OSC"},
	     {name:"aPtpMain",mapping:"A_PTP_NAME_MAIN"},
	     {name:"zPtpMain",mapping:"Z_PTP_NAME_MAIN"},
	     {name:"aPtpOsc",mapping:"A_PTP_NAME_OSC"},
	     {name:"zPtpOsc",mapping:"Z_PTP_NAME_OSC"},
	     
	     {name:"linkId",mapping:"BASE_LINK_ID"},
	     {name:"FEND_LINK_ID",mapping:"FEND_LINK_ID"},
	     {name:"A_STATION_ID",mapping:"A_STATION_ID"}, 
	     {name:"Z_STATION_ID",mapping:"Z_STATION_ID"}, 
	     {name:"RESOURCE_CABLES_ID",mapping:"RESOURCE_CABLES_ID"}, 
	     {name:"RESOURCE_CABLE_ID",mapping:"RESOURCE_CABLE_ID"}, 
	     {name:"RESOURCE_FIBER_ID",mapping:"RESOURCE_FIBER_ID"}, 
	     {name:"A_NE_ID",mapping:"A_NE_ID"},
	     {name:"Z_NE_ID",mapping:"Z_NE_ID"},
	     {name:"A_PTP_ID",mapping:"A_PTP_ID"},
	     {name:"Z_PTP_ID",mapping:"Z_PTP_ID"},
	     {name:"A_PTP_ID_MAIN",mapping:"A_PTP_ID_MAIN"},
	     {name:"Z_PTP_ID_MAIN",mapping:"Z_PTP_ID_MAIN"},
	     {name:"A_PTP_ID_OSC",mapping:"A_PTP_ID_OSC"},
	     {name:"Z_PTP_ID_OSC",mapping:"Z_PTP_ID_OSC"}]),
	listeners:{
		"exception": function(proxy,type,action,options,response,arg){
			Ext.Msg.alert("提示","搜索出错"+
					"<BR>Status:"+response.statusText||"unknow");
		}
	}
});

// ==========================page=============================
var rows=[[{header:''},//RowNumberer
           {header:''},//checkboxSelectionModel
           {header:''},
           {header:''},
	       {header:'链路A端',align: 'center',colspan: 3},
	       {header:'链路Z端',align: 'center',colspan: 3},
	       {header:''},
	       {header:''},
	       {header:''},
	       {header:''},
	       {header:'主信号',align: 'center',colspan: 3},
	       {header:'OSC信号',align: 'center',colspan: 3}]]
;
var checkboxSelectionModel = new Ext.ux.grid.LockingCheckboxSelectionModel({
    header:'',singleSelect:true});

var cm = new Ext.ux.grid.LockingColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true
	// columns are not sortable by default
	},
	stateId : "fiberLinkEvaluateSettingGridId",
	rows: rows,
	columns : [new Ext.grid.RowNumberer({
		width:26,
		locked:true
	}),checkboxSelectionModel,{
		id : 'sysName',
		header : '系统名称',
		dataIndex : 'sysName',
		width : 6*15,
		locked:true,
		hidden:true
	}, {
		id : 'linkName',
		header : '链路名称',
		dataIndex : 'linkName',
		width : 6*15,
		locked:true
	}, {
		id : 'aStationName',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'aStationName',
		width : 4*15,
		locked:true
	}, {
		id : 'aNe',
		header : '网元',
		dataIndex : 'aNe',
		width : 5*15,
		locked:true
	}, {
		id : 'aPtp',
		header : '端口',
		dataIndex : 'aPtp',
		width : 6*15,
		locked:true
	}, {
		id : 'zStationName',
		header : top.FieldNameDefine.STATION_NAME,
		dataIndex : 'zStationName',
		width : 4*15,
		locked:true
	}, {
		id : 'zNe',
		header : '网元',
		dataIndex : 'zNe',
		width : 5*15,
		locked:true
	}, {
		id : 'zPtp',
		header : '端口',
		dataIndex : 'zPtp',
		width : 6*15,
		locked:true
	}, {
		id : 'isManual',
		header : '生成方式',
		dataIndex : 'isManual',
		width : 4*15,
		renderer : function(v) {
			if (v == 0) {
				return ("自动生成");
			}
			if (v == 1) {
				return ("手动生成");
			}
		}
	}, {
		id : 'cables',
		header : '光缆',
		dataIndex : 'cables',
		width : 4*15
	}, {
		id : 'cable',
		header : '光缆段',
		dataIndex : 'cable',
		width : 6*15
	}, {
		id : 'fiberNo',
		header : '光纤芯号',
		dataIndex : 'fiberNo',
		width : 4*15
	}, {
		id : 'aPtpMain',
		header : '起始端',
		dataIndex : 'aPtpMain',
		width : 6*15
	}, {
		id : 'attMain',
		header : '衰耗器(dB)',
		dataIndex : 'attMain',
		width : 5*15
	}, {
		id : 'zPtpMain',
		header : '终点端',
		dataIndex : 'zPtpMain',
		width : 6*15
	}, {
		id : 'aPtpOsc',
		header : '起始端',
		dataIndex : 'aPtpOsc',
		width : 6*15
	}, {
		id : 'attOsc',
		header : '衰耗器(dB)',
		dataIndex : 'attOsc',
		width : 5*15
	}, {
		id : 'zPtpOsc',
		header : '终点端',
		dataIndex : 'zPtpOsc',
		width : 6*15
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

var offsetPanel = new Ext.form.FormPanel({
	id : 'offsetPanel',
	bodyStyle:'padding:10px 10px 10px 10px',
//	height : 80,
	autoScroll : true,
	forceLayout : true,
	collapsible : false,
	labelWidth : 40,
	items : [{
        xtype: 'compositefield',
        hideLabel: true,
        style: {
        	marginTop: '10px',
            marginBottom: '10px'
        },
        items: [{
	            xtype: 'displayfield',
	            value: '阈值1：'
		    },{
//        		fieldLabel:'阈值1',
	        	xtype:'numberfield',
	            id: 'downOffset',
	            name: 'downOffset',
	            width:75,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2
	        },{
                xtype: 'displayfield',
                value: 'dB'
		    },{
	            xtype: 'displayfield',
	            value: '阈值2：'
		    },{
//        		fieldLabel:'阈值2',
	            xtype:'numberfield',
	            id: 'middleOffset',
	            name: 'middleOffset',
	            width:75,
	            //flex:1,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2
	        },{
               xtype: 'displayfield',
               value: 'dB'
	        },{
	            xtype: 'displayfield',
	            value: '阈值3：'
		    },{
//        		fieldLabel:'阈值3',
	            xtype:'numberfield',
	            id: 'upperOffset',
	            name: 'upperOffset',
	            width:75,
	            //flex:1,
	            allowDecimals : true,
	    		allowNegative : true,
	    		decimalPrecision : 2
	        },{
               xtype: 'displayfield',
               value: 'dB'
	        }
	    ]
	},{
        xtype: 'displayfield',
        hideLabel: true,
        value:  '说明：<br>'+
        		' 1 链路衰耗=发送光功率-接收光功率-衰耗器 <br>'+
        		' 2 衰耗偏差ΔA=衰耗基准值-链路衰耗 <br>'+
        		' 3 A级：|ΔA|≤阈值1；B级：阈值1<|ΔA|≤阈值2；C级：阈值2<|ΔA|≤阈值3；D级：|ΔA|>阈值3 <br>'+
        		' 4 衰耗系数=链路衰耗/距离 <br>'+
        		' 5 绑定的链路：平衡度=|链路衰耗-链路衰耗| （备注：取绝对值）'
    }]
});

var offsetWindow = new Ext.Window({
	id : 'offsetWindow',
	title : '评估参数设置',
	width : 550,
	//height : 300,
	//layout: 'fit',
	//isTopContainer : true,
	//modal : true,
	autoScroll : true,
	closeAction: 'hide',
	items:[offsetPanel],
    //buttonAlign:"center",
    buttons: [{
    	text : '恢复默认值', 
		handler : function(){
			Ext.getCmp('upperOffset').setValue(3);
			Ext.getCmp('middleOffset').setValue(2);
			Ext.getCmp('downOffset').setValue(1.5);
		}
    },{
    	text : '确定', 
		handler : modifyOffsetValue
    },{
    	text : '取消', 
    	handler: function(){
    		offsetWindow.hide();
    	}
    }]
});

var setOffsetButton = new Ext.Button({
	text : '评估参数设置',
	privilege:modAuth,
	icon : '../../../resource/images/btnImages/config_add.png',
	handler : function(){
		offsetWindow.show();
	}
});

var gridPanel = new Ext.grid.GridPanel({
	id : "gridPanel",
	region : "center",
	colModel : cm,
	store : store,
	stripeRows : true, // 交替行效果
	loadMask : true,
	selModel : checkboxSelectionModel, // 必须加不然不能选checkbox
	view : new Ext.ux.grid.LockingGridView(),
	plugins:[new Ext.ux.plugins.LockedGroupHeaderGrid()],
	//enableColumnMove : false,
	//enableColLock : false,
	//enableHdMenu : false,
	forceFit : false,
	stateId : "fiberLinkEvaluateSettingGridId",
	stateful : true,
	bbar : pageTool,
	tbar : ['-',netLevelComboBar,
	        '-',projectComboGridBar, 
		'-', {
		id : 'reset',
		text : '重置',
//		icon : '../../../resource/images/btnImages/bin_empty.png',
		handler : function() {
			netLevelComboBar.getForm().reset();
			projectComboGridBar.getForm().reset();
		}
	}, {
        id: 'search',
        text: '查询',
        privilege:viewAuth,
        icon : '../../../resource/images/btnImages/search.png',
        handler : linkSearch
    }, {
        id: 'modify',
        text: '设置',
        privilege:modAuth,
        icon : '../../../resource/images/btnImages/modify.png',
        handler : setResourceLink
    }, {
        id: 'delete',
        text: '删除',
        privilege:delAuth,
        icon : '../../../resource/images/btnImages/delete.png',
        handler : deleteResourceLink
    }, setOffsetButton]
});

// -----------------------------------------init the
// page--------------------------------------------

// ************************************************************************************
function linkSearch() {
	if(!projectComboGridField.isValid()){
		return;
	}
	var netLevel = netLevelComboField.getValue();
	var project = projectComboGridField.comboGrid.getValue();
	var jsonData = {
		"netLevel" : netLevel,
		"transSysId" : project,
		"limit" : 200
	};

	store.baseParams = jsonData;

	store.load();
}
function checkSingleSelect(){
	var record=gridPanel.getSelectionModel().getSelected();
	if(record&&record.get("linkId")){
		return record;
	}else{
		Ext.Msg.alert("提示", '请选择一条记录');
		return false;
	}
}
linkComboGridField.fieldLabel="反向链路";
//cableComboGridField.allowBlank=false;
//fiberComboGridField.allowBlank=false;
//cableComboGridField.sideText='<font color="red">*</font>';
//fiberComboGridField.sideText='<font color="red">*</font>';
var linkInfoPanel = new Ext.form.FormPanel({
	id : 'linkInfoPanel',
	bodyStyle:'padding:10px 10px 10px 10px',
//	height : 80,
	monitorValid: true,
	autoScroll : true,
	forceLayout : true,
	collapsible : false,
	labelWidth : 110,
	defaults: {
		anchor: '100%'
	},
	items : [{
        xtype: 'displayfield',
        fieldLabel:'链路名称',
        id: 'LINK_DISPLAY_NAME'
    },{
        xtype: 'displayfield',
        fieldLabel:'链路A端',
        id: 'A_PTP_NAME'
    },{
        xtype: 'displayfield',
        fieldLabel:'链路Z端',
        id: 'Z_PTP_NAME'
    },linkComboGridField,cableComboGridField,
    {
    	layout:'column',
    	fieldLabel:'光纤',
    	items:[fiberComboGridField,
    	       /*{xtype:'displayfield',value: '<font color="red">*</font>'},*/{
    		xtype:'button',
    		text:'︾光纤参数',
    		handler: function(){
    			var cableId=cableComboGridField.comboGrid.getValue();
    			var cableName=cableComboGridField.getValue();
    			var idx = cableComboGridField.comboGrid.store.find(cableComboGridField.valueField, cableId);
    	        // console.log(idx);
    			var cablesName="";
    	        if (idx >= 0) {
    	        	var record=cableComboGridField.comboGrid.store.getAt(idx);
    	        	cableName=record.get('CABLE_NAME');
    	        	cablesName=record.get('CABLES_NAME');
    	        }
    			if(cableId){
    				var param = {
						"cableId" : cableId,
						"cable" : cableName,
						"cables" : cablesName
					};
    				var url = "../../resourceManager/cable/fiberList.jsp?" + Ext.urlEncode(param);
    				var fiberListWindow = new Ext.Window({
    					id : 'fiberListWindow',
    					title : '光纤信息',
    					width : 1200,
    					height : 500,
    					isTopContainer : true,
    					modal : true,
    					plain : true, // 是否为透明背景
    					html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0/>'
    				});
    				fiberListWindow.show();

    				// 调节高度
    				if (fiberListWindow.getHeight() > Ext.getCmp('win').getHeight()) {
    					fiberListWindow.setHeight(Ext.getCmp('win').getHeight() * 0.7);
    				} else {
    					fiberListWindow.setHeight(fiberListWindow.getInnerHeight());
    				}
    				// 调节宽度
    				if (fiberListWindow.getWidth() > Ext.getCmp('win').getWidth()) {
    					fiberListWindow.setWidth(Ext.getCmp('win').getWidth() * 0.7);
    				} else {
    					fiberListWindow.setWidth(fiberListWindow.getInnerWidth());
    				}
    				fiberListWindow.center();
    			}
    		}
    	}]
    }, {
    	layout:'column',
    	fieldLabel:'主信号起始端',
    	items:[{
    		columnWidth: 1,
	        xtype: 'equiptreecombo',
	        //fieldLabel:'主信号起始端',
	        //sideText: '<font color="red">*</font>',
	        allowBlank: false,
	        id: 'A_PTP_MAIN',
	        //rootVisible: true,
	        checkModel: 'single',
	        checkableLevel: [CommonDefine.TREE.NODE.PTP],
	        listeners:{
	        	'collapse': setTreeComboHiddenValue
	        }
    	},{xtype:'displayfield',value: '<font color="red">*</font>'},{
    		xtype:'button',
    		text:'同链路端口',
    		handler: function(){
    			var p=CommonDefine.TREE.NODE.PTP;
    			port=linkInfoWindow.findById('A_PTP_MAIN');
    			port.hiddenValue=linkInfoWindow.record.get('A_PTP_ID');
    			port.treeField.checkNodes(p+'-'+port.hiddenValue);
    		}
    	}]
    },{
    	fieldLabel:'主信号衰耗器(dB)',
    	xtype:'numberfield',
    	id: 'ATT_MAIN',
        allowDecimals : true,
		allowNegative : true,
		decimalPrecision : 2,
		allowNegative : false,
		maxValue : 30
    },{
    	layout:'column',
    	fieldLabel:'主信号终点端',
    	items:[{
    		columnWidth: 1,
	        xtype: 'equiptreecombo',
	        //fieldLabel:'主信号终点端',
	        //sideText: '<font color="red">*</font>',
	        allowBlank: false,
	        id: 'Z_PTP_MAIN',
	        //rootVisible: true,
	        checkModel: 'single',
	        checkableLevel: [CommonDefine.TREE.NODE.PTP],
	        listeners:{
	        	'collapse': setTreeComboHiddenValue
	        }
    	},{xtype:'displayfield',value: '<font color="red">*</font>'},{
    		xtype:'button',
    		text:'同链路端口',
    		handler: function(){
    			var p=CommonDefine.TREE.NODE.PTP;
    			var port=linkInfoWindow.findById('Z_PTP_MAIN');
    			port.hiddenValue=linkInfoWindow.record.get('Z_PTP_ID');
    			port.treeField.checkNodes(p+'-'+port.hiddenValue);
    		}
    	}]
    },{
        xtype: 'equiptreecombo',
        fieldLabel:'OSC起始端',
        id: 'A_PTP_OSC',
        //rootVisible: true,
        checkModel: 'single',
        checkableLevel: [CommonDefine.TREE.NODE.PTP],
        listeners:{
        	'collapse': setTreeComboHiddenValue
        }
    },{
    	fieldLabel:'OSC衰耗器(dB)',
    	xtype:'numberfield',
    	id: 'ATT_OSC',
        allowDecimals : true,
		allowNegative : true,
		decimalPrecision : 2,
		allowNegative : false,
		maxValue : 30
    },{
        xtype: 'equiptreecombo',
        fieldLabel:'OSC终点端',
        id: 'Z_PTP_OSC',
        //rootVisible: true,
        checkModel: 'single',
        checkableLevel: [CommonDefine.TREE.NODE.PTP],
        listeners:{
        	'collapse': setTreeComboHiddenValue
        }
    }],
    buttons: [{
    	formBind: true,
    	text : '确定', 
		handler : modifyLink
    },{
    	text : '取消', 
    	handler: function(){
    		linkInfoWindow.hide();
    	}
    }]
});

var linkInfoWindow = null;
function setTreeComboHiddenValue(combo,value){
	combo.hiddenValue=combo.treeField.getCheckedNodes('nodeId');
}
function modifyLink(){
	if(linkInfoPanel.getForm().isValid()){
		/*console.log('RESOURCE_LINK_ID:'+linkInfoWindow.record.get('linkId'));
		console.log('FEND_LINK_ID:'+linkComboGridField.comboGrid.getValue());
		console.log('RESOURCE_FIBER_ID:'+fiberComboGridField.comboGrid.getValue());
		
		console.log('A_PTP_MAIN:'+linkInfoWindow.findById('A_PTP_MAIN').hiddenValue);
		console.log('ATT_MAIN:'+linkInfoWindow.findById('ATT_MAIN').getValue());
		console.log('Z_PTP_MAIN:'+linkInfoWindow.findById('Z_PTP_MAIN').hiddenValue);
		console.log('A_PTP_OSC:'+linkInfoWindow.findById('A_PTP_OSC').hiddenValue);
		console.log('ATT_OSC:'+linkInfoWindow.findById('ATT_OSC').getValue());
		console.log('Z_PTP_OSC:'+linkInfoWindow.findById('Z_PTP_OSC').hiddenValue);*/
		var param={
			linkId : linkInfoWindow.record.get('linkId'),
			FEND_LINK_ID : linkComboGridField.comboGrid.getValue(),
			RESOURCE_FIBER_ID : fiberComboGridField.comboGrid.getValue(),
			A_PTP_MAIN : linkInfoWindow.findById('A_PTP_MAIN').hiddenValue,
			ATT_MAIN : linkInfoWindow.findById('ATT_MAIN').getValue(),
			Z_PTP_MAIN : linkInfoWindow.findById('Z_PTP_MAIN').hiddenValue,
			A_PTP_OSC : linkInfoWindow.findById('A_PTP_OSC').hiddenValue,
			ATT_OSC : linkInfoWindow.findById('ATT_OSC').getValue(),
			Z_PTP_OSC : linkInfoWindow.findById('Z_PTP_OSC').hiddenValue
		};
		var maskEl=linkInfoWindow.getEl();
		maskEl.mask("执行中...");
		Ext.Ajax.request({
			url : 'fiber-link-evaluate!setResourceLink.action',
			method : "POST",
			params : param,
			success : function(response) {
				maskEl.unmask();
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					//Ext.Msg.alert('信息', '删除成功！',function(){
						linkInfoWindow.hide();
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					//});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", obj.returnMessage);
				}
			},
			error : function(response) {
				maskEl.unmask();
				Ext.Msg.alert("异常", response.responseText);
			},
			failure : function(response) {
				maskEl.unmask();
				Ext.Msg.alert("异常", response.responseText);
			}
		});
	}
}
function setResourceLink() {
	var record=checkSingleSelect();
	if(record){
		linkInfoWindow.record=record;
		linkInfoWindow.findById('LINK_DISPLAY_NAME').setValue(record.get('linkName'));
		linkInfoWindow.findById('A_PTP_NAME').setValue(record.get('aPtp'));
		linkInfoWindow.findById('Z_PTP_NAME').setValue(record.get('zPtp'));
		linkInfoWindow.findById('ATT_MAIN').setValue(record.get('attMain'));
		linkInfoWindow.findById('ATT_OSC').setValue(record.get('attOsc'));
		
		linkComboGridField.comboGrid.store.load({
			scope:linkComboGridField,
			params:{
				'A_NE_ID':record.get('Z_NE_ID'),
				'Z_NE_ID':record.get('A_NE_ID')
			},
			callback:function(r,options,success){
				this.comboGrid.setValue(record.get('FEND_LINK_ID'));
			}
		});
		cableComboGridField.comboGrid.store.load({
			scope:cableComboGridField,
			params:{
				'aStationId':record.get('A_STATION_ID'),
				'zStationId':record.get('Z_STATION_ID'),
				'DIRECTION':1
			},
			callback:function(r,options,success){
				this.comboGrid.setValue(record.get('RESOURCE_CABLE_ID'));
				fiberComboGridField.comboGrid.setValue(record.get('RESOURCE_FIBER_ID'));
			}
		});

		var p=CommonDefine.TREE.NODE.PTP;
		A_PTP_MAIN=linkInfoWindow.findById('A_PTP_MAIN');
		Z_PTP_MAIN=linkInfoWindow.findById('Z_PTP_MAIN');
		A_PTP_OSC=linkInfoWindow.findById('A_PTP_OSC');
		Z_PTP_OSC=linkInfoWindow.findById('Z_PTP_OSC');
		
		A_PTP_MAIN.treeField.setRoot(
				record.get('A_NE_ID'),
		        CommonDefine.TREE.NODE.NE);
		Z_PTP_MAIN.treeField.setRoot(
				record.get('Z_NE_ID'),
		        CommonDefine.TREE.NODE.NE);
		A_PTP_OSC.treeField.setRoot(
				record.get('A_NE_ID'),
		        CommonDefine.TREE.NODE.NE);
		Z_PTP_OSC.treeField.setRoot(
				record.get('Z_NE_ID'),
		        CommonDefine.TREE.NODE.NE);
		
		A_PTP_MAIN.hiddenValue=record.get('A_PTP_ID_MAIN');
		Z_PTP_MAIN.hiddenValue=record.get('Z_PTP_ID_MAIN');
		A_PTP_OSC.hiddenValue=record.get('A_PTP_ID_OSC');
		Z_PTP_OSC.hiddenValue=record.get('Z_PTP_ID_OSC');
		
		A_PTP_MAIN.treeField.checkNodes(p+'-'+A_PTP_MAIN.hiddenValue);
		Z_PTP_MAIN.treeField.checkNodes(p+'-'+Z_PTP_MAIN.hiddenValue);
		A_PTP_OSC.treeField.checkNodes(p+'-'+A_PTP_OSC.hiddenValue);
		Z_PTP_OSC.treeField.checkNodes(p+'-'+Z_PTP_OSC.hiddenValue);
		
		linkInfoWindow.show();
	}
}
function deleteResourceLink() {
	var record=checkSingleSelect();
	if(record){
		var param={
			linkId:record.get("linkId")
		};
		Ext.getBody().mask("执行中...");
		Ext.Ajax.request({
			url : 'fiber-link-evaluate!deleteResourceLink.action',
			method : "POST",
			params : param,
			success : function(response) {
				Ext.getBody().unmask();
				var obj = Ext.decode(response.responseText);
				if (obj.returnResult == 1) {
					//Ext.Msg.alert('信息', '删除成功！',function(){
						// 刷新列表
						var pageTool = Ext.getCmp('pageTool');
						if (pageTool) {
							pageTool.doLoad(pageTool.cursor);
						}
					//});
				}
				if (obj.returnResult == 0) {
					Ext.Msg.alert("信息", obj.returnMessage);
				}
			},
			error : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("异常", response.responseText);
			},
			failure : function(response) {
				Ext.getBody().unmask();
				Ext.Msg.alert("异常", response.responseText);
			}
		});
	}
}

function modifyOffsetValue(){
	
	var upper = Ext.getCmp('upperOffset').getValue();
	var middle = Ext.getCmp('middleOffset').getValue();
	var down = Ext.getCmp('downOffset').getValue();
	
	if(middle<=down){
		Ext.Msg.alert('信息', '二级值必须大于一级值！');
		return;
	}
	if(upper<=middle){
		Ext.Msg.alert('信息', '三级值必须大于二级值！');
		return;
	}
	offsetWindow.hide();
	var jsonData = {
		"downOffset" : down,
		"middleOffset" : middle,
		"upperOffset" : upper
	};
	Ext.Ajax.request({
		url : 'fiber-link-evaluate!modifyOffsetValue.action',
		method : "POST",
		params : jsonData,
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				Ext.Msg.alert('信息', '修改成功！',function(){
					initOffsetValue();
					// 刷新列表
					var pageTool = Ext.getCmp('pageTool');
					if (pageTool) {
						pageTool.doLoad(pageTool.cursor);
					}
				});
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}

function initOffsetValue(){
	Ext.Ajax.request({
		url : 'fiber-link-evaluate!getOffsetValue.action',
		method : 'POST',
		success : function(response) {
			var obj = Ext.decode(response.responseText);
			var upper = obj.upperOffset;
			var middle = obj.middleOffset;
			var down = obj.downOffset;
			
			Ext.getCmp('upperOffset').setValue(upper);
			Ext.getCmp('middleOffset').setValue(middle);
			Ext.getCmp('downOffset').setValue(down);
		},
		error : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		},
		failure : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert("异常", response.responseText);
		}
	});
}
function init(){
	initOffsetValue();
	linkInfoWindow=new Ext.Window({
		id : 'linkInfoWindow',
		title : '光路评估设置',
		width : 450,
		height : 415,
		boxMinWidth: 300,
		layout: 'fit',
		isTopContainer : true,
		renderTo: Ext.getBody(),
		modal : true,
		autoScroll : true,
		closeAction: 'hide',
		items:[linkInfoPanel]
	});
	if(linkId){
		var jsonData = {
			"linkId" : linkId,
			"limit" : 200
		};
		store.baseParams = jsonData;
		store.load();
	}
}

Ext.onReady(function() {
	Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";

	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	};
//	Ext.Msg = top.Ext.Msg;
	Ext.Ajax.timeout = 900000;
	win = new Ext.Viewport({
		id : 'win',
		layout : 'border',
		items : [ gridPanel],
		renderTo : Ext.getBody()
	});
	win.show();
	init();
});