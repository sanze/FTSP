/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
//获取数据列表
var checkboxSelectionModel = new Ext.grid.CheckboxSelectionModel({singleSelect :false});

var routeId;

var routeStore = new Ext.data.Store({
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, 
	[ "TEST_ROUTE_ID","NAME","ROUTE_NAME","TEST_PERIOD","ROUTE_LENGTH",
	  "OTDR_WAVE_LENGTH","OTDR_PLUSE_WIDTH","OTDR_RANGE","OTDR_TEST_DURATION","OTDR_REFRACT_COEFFICIENT",
	  "ATT_BASE","ATT_OFFSET","EVENT_COUNT_BASE"])
});

var data = [ 
['-1','不测试'],
['1','1'], 
['2','2'], 
['3','3'],
['4','4'],
['5','5'],
['6','6'],
['7','7'], 
['8','8'], 
['9','9'],
['10','10'],
['11','11'],
['12','12'],
['12','12'], 
['14','14'], 
['15','15'],
['16','16'],
['17','17'],
['18','18'],
['19','19'], 
['20','20'], 
['21','21'],
['22','22'],
['23','23'],
['24','24'],
['25','25'],
['26','26'],
['27','27'],
['28','28'],
['29','29'],
['30','30']
]; 
var dateStore = new Ext.data.SimpleStore({
	data:data,
	fields:[ "id","value"]
});

var routeColumnModel = new Ext.grid.ColumnModel({
	defaults : {
		sortable : true,
		// forceFit : true,
// align:'left'
	},
	columns:[ new Ext.grid.RowNumberer({
		width : 26
	}),{
			id:'TEST_ROUTE_ID',
			header:'ID',
			dataIndex:'TEST_ROUTE_ID',
			width:40,
			hidden:true
		},{
			id:'NAME',
			header:'测试项目名称',
			dataIndex:'NAME',
			width:120
		},{
			id:'TEST_PERIOD',
			header:'<span style="font-weight:bold">测试周期(天)</span>',
			tooltip:'可编辑列',
			dataIndex:'TEST_PERIOD',
			editor:new Ext.form.ComboBox({
			     id:'testPriod',
			     name: 'testPriod',
			     mode:"local",
			     store:dateStore,
			     displayField:"value",
			     valueField:'id',
			     triggerAction : 'all'  
			}), 
			renderer: function(value,metadata,record){
	              var index = dateStore.find('id',value);  
	              if(index!=-1){  
	                   return dateStore.getAt(index).data.value;  
	               }  
	               return value;  
			},
			width:90
		},{
			id:'ROUTE_NAME',
			header:'测试路由',
			dataIndex:'ROUTE_NAME',
			width:120
		},{
			id:'ROUTE_LENGTH',
			header:'路由长度(KM)',
			dataIndex:'ROUTE_LENGTH',
			width:80
		},{
			id:'para',
			header:'测试参数',
			width:60,
			renderer: function toURL(){
				return '<a href="#">设置</a>';
			},
			listeners: { 'click': function (grid, rowIndex, columnIndex, e) {  
    		  	var selections = routeGridPanel.getSelectionModel().getSelections();
  		  		var record = selections[0];
  		  		var id =  record.get("TEST_ROUTE_ID");
  		  		routeId = id;
  		  		waveLengthStore.load({
	  		  		params: {
	  		  		"jsonString":Ext.encode({
	  					'routeId':id
	  				})
	  		  		},
	  			    callback: function(response) {
	  			    }
  		  		});
  		  		
  		  		rangeStore.load({
	  		  		params: {
	  		  		"jsonString":Ext.encode({
	  					'routeId':id
	  				})
	  		  		},
	  			    callback: function(records, operation, success) {
	  			    }
		  		});
  		  		
  		  		pluseWidthStore.load({
	  		  		params: {
	  		  		"jsonString":Ext.encode({
	  					'routeId':id,
	  					'TEST_RANGE':record.get("OTDR_RANGE")
	  				})
	  		  		},
	  			    callback: function(records, operation, success) {
	  			    }
		  		});
  		  		Ext.getCmp("otdrWaveLenght").setValue(record.get("OTDR_WAVE_LENGTH"));
	  		  	Ext.getCmp("otdrRange").setValue(record.get("OTDR_RANGE"));
	  		  	Ext.getCmp("otdrPluseWidth").setValue(record.get("OTDR_PLUSE_WIDTH"));
	  		  	Ext.getCmp("otdrAveCount").setValue(record.get("OTDR_TEST_DURATION"));
	  		  	Ext.getCmp("otdrRefractCoefficient").setValue(record.get("OTDR_REFRACT_COEFFICIENT"));
  		  		
	  		  	confirmParaWin.setPosition(500,50);
	  			confirmParaWin.show();
    		  }
    	  }
		},{
			id:'ATT_BASE',
			header:'衰耗基准值(dB)',
			dataIndex:'ATT_BASE',
			width:90
		},{
			id:'ATT_OFFSET',
			header:'<span style="font-weight:bold">衰耗基准值偏差</span>',
			tooltip:'可编辑列',
			dataIndex:'ATT_OFFSET',
			width:90,
			editor:new Ext.form.NumberField({
				id:'attOffsetEditor',
				allowBlank:false
			})
		},{
			id:'EVENT_COUNT_BASE',
			header:'事件计数基准值',
			dataIndex:'EVENT_COUNT_BASE',
			width:90
		}
	]}
);

var routeGridPanel = new Ext.grid.EditorGridPanel({
	region : 'center',
	stripeRows : true,
	autoScroll : true,
	frame : false,
	height: 400,
	store : routeStore,
	loadMask : true,
	border:true,
	cm : routeColumnModel,
	selModel:checkboxSelectionModel,
	clicksToEdit : 2
});

var planConfigForm = new Ext.FormPanel({
	id : "planConfigForm",
	frame : true,
	border : false,
	bodyStyle : 'padding:20px 10px 20px',
	labelAlign : 'right',
	items : [routeGridPanel]
});



//****************************************************************
var testTimeStore = new Ext.data.SimpleStore({
    fields: ['id', 'name'],
    data : [[15,15],[30,30],[60,60],[120,120],[180,180]]
});

var waveLengthStore = new Ext.data.Store({
	url : 'plan!getWaveLengthList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	['WAVE_LENGTH'])
});

var rangeStore = new Ext.data.Store({
	url : 'plan!getRangeList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	['TEST_RANGE'])
});

var pluseWidthStore = new Ext.data.Store({
	url : 'plan!getPluseWidthList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	['PLUSE_WIDTH'])
});

var paraPanel = new Ext.FormPanel({
	id:"paraPanel",
    border:false,
    bodyStyle:'padding:10px 10px 20px 10px',
    autoHeight:true,
    labelAlign: 'right',
    items: [{
    	layout:'form',
    	border:false,
        items: [{
        	layout:'column',
        	border:false,
        	bodyStyle:'padding:10px 10px 2px 10px',
        	items: [{
        		xtype:"label",
        		text:"测试波长(nm)：",
        		columnWidth:.3
        	},{
        		xtype: 'combo',
                id:'otdrWaveLenght',
                name: 'otdrWaveLenght',
                mode:"local",
	       	    store:waveLengthStore,
	       	    displayField:"WAVE_LENGTH",
	       	    valueField:'WAVE_LENGTH',
	       	    triggerAction: 'all',
                columnWidth:.65,
                anchor: '95%'
        	}]
        },{
        	layout:'column',
        	border:false,
        	bodyStyle:'padding:10px 10px 2px 10px',
        	items: [{
        		xtype:"label",
        		text:"量    程(Km)：",
        		columnWidth:.3
        	},{
        		xtype: 'combo',
                id:'otdrRange',
                name: 'otdrRange',
                mode:"local",
	       	    store:rangeStore,
	       	    displayField:"TEST_RANGE",
	       	    valueField:'TEST_RANGE',
	       	    triggerAction: 'all',
                columnWidth:.65,
                anchor: '95%',
                listeners:{
         	         'select': function () {
         	        	pluseWidthStore.load({
         	        		params: {
         		  		  		"jsonString":Ext.encode({
         		  					'routeId':routeId,
         		  					'TEST_RANGE':this.value
         		  				})
         		  		  		},
		       			    callback: function(records, operation, success) {
		       			    }
		       			});
         	         }
          		}
        	}]
        },{
        	layout:'column',
        	border:false,
        	bodyStyle:'padding:10px 10px 2px 10px',
        	items: [{
        		xtype:"label",
        		text:"脉冲宽度(ns)：",
        		columnWidth:.3
        	},{
        		xtype: 'combo',
                id:'otdrPluseWidth',
                name: 'otdrPluseWidth',
                mode:"local",
	       	    store:pluseWidthStore,
	       	    displayField:"PLUSE_WIDTH",
	       	    valueField:'PLUSE_WIDTH',
	       	    triggerAction: 'all',
                columnWidth:.65,
                anchor: '95%'
        	}]
        },{
        	layout:'column',
        	border:false,
        	bodyStyle:'padding:10px 10px 2px 10px',
        	items: [{
        		xtype:"label",
        		text:"测试时长(s)：",
        		columnWidth:.3
        	},{
        		xtype: 'combo',
                id:'otdrAveCount',
                name: 'otdrAveCount',
                mode:"local",
	       	    store:testTimeStore,
	       	    displayField:"name",
	       	    valueField:'id',
	       	    triggerAction: 'all',
                columnWidth:.65,
                anchor: '95%'
        	}]
        },{
        	layout:'column',
        	border:false,
        	bodyStyle:'padding:10px 10px 2px 10px',
        	items: [{
        		xtype:"label",
        		text:"折射系数：",
        		columnWidth:.3
        	},{
        		xtype: 'textfield',
                id:'otdrRefractCoefficient',
                name: 'otdrRefractCoefficient',
                allowBlank:true,
                columnWidth:.65,
                anchor: '95%'
        	}]
        }]
    }]
});

var confirmParaWin = new Ext.Window({
	title: '测试参数确认',
	id:'confirmParaWin',
	width: 350,
	height: 260,
	closable: true,
	modal: true,
	closeAction: 'hide',
	layout : 'fit',
	bodyPadding : 10,
	border: false,
	buttonAlign:'center',
	items: [paraPanel],
	buttons: [{
		xtype : 'button',
		text : '确认',
//		icon : '../../resource/images/btnImages/add.png',
		handler : function(){
			modifyTestRoutePara();
			}
	},{
		xtype : 'button',
		text : '取消',
//		icon : '../../resource/images/btnImages/arrow_undo.png',
		handler : function(){
			confirmParaWin.hide();
			}
	}]
});

function modifyTestRoutePara(){
	Ext.Msg.confirm('警告',"即将更新路由测试参数，是否继续？",
      function(btn){
        if(btn=='yes'){
        	var selections = routeGridPanel.getSelectionModel().getSelections();
	  		var record = selections[0];
			var parameters =
			{
				"jsonString":Ext.encode({
					'routeId':record.get("TEST_ROUTE_ID"),
					'otdrWaveLenght':Ext.getCmp("otdrWaveLenght").getValue(),
					'otdrRange':Ext.getCmp("otdrRange").getValue(),
					'otdrPluseWidth':Ext.getCmp("otdrPluseWidth").getValue(),
					'otdrAveCount':Ext.getCmp("otdrAveCount").getValue(),
					'otdrRefractCoefficient':Ext.getCmp("otdrRefractCoefficient").getValue()
				})
			}
			Ext.Ajax.timeout=120000;
			top.Ext.getBody().mask('正在执行，请稍候...');
			Ext.Ajax.request({
			    url: 'plan!modifyTestRoutePara.action',
			    params: parameters,
			    success: function(response){
			    	top.Ext.getBody().unmask();
					var obj = Ext.decode(response.responseText);    	
					routeStore.reload();
					Ext.Msg.show({
					   title:'提示',
					   msg: '更新成功！',
					   buttons: Ext.Msg.CANCEL,
					   icon: Ext.MessageBox.INFO
					});
			    }
			});
			confirmParaWin.hide();
        }
    })
};
