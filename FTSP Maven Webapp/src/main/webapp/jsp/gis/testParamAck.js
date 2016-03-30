/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

var testTimeStore = new Ext.data.SimpleStore({
    fields: ['id', 'name'],
    data : [[15,15],[30,30],[60,60],[120,120],[180,180]]
});

var testRouteStore = new Ext.data.Store();

var waveLengthStore = new Ext.data.Store({
	url : 'gis!getWaveLengthList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	['WAVE_LENGTH'])
});

var rangeStore = new Ext.data.Store({
	url : 'gis!getRangeList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	['TEST_RANGE'])
});

var pluseWidthStore = new Ext.data.Store({
	url : 'gis!getPluseWidthList.action',
	reader : new Ext.data.JsonReader({
		root : "rows"
	}, 
	['PLUSE_WIDTH'])
});

var testRouteCombo = {
	xtype: 'combo',
    id:'testRoute',
    name: 'testRoute',
    mode:"local",
    store:testRouteStore,
    displayField:"routeName",
    valueField:'testRouteId',
    triggerAction: 'all',
    columnWidth:.65,
    anchor: '95%',
	listeners:{
		'select': function () {
			if(this.value < 0){
				Ext.getCmp("otdrWaveLength").disable();
	  		  	Ext.getCmp("otdrRange").disable();
	  		  	Ext.getCmp("otdrPluseWidth").disable();
	  		  	Ext.getCmp("testDuration").disable();
	  		  	Ext.getCmp("otdrRefractCoefficient").disable();
				return;
			}else{
				Ext.getCmp("otdrWaveLength").enable();
	  		  	Ext.getCmp("otdrRange").enable();
	  		  	Ext.getCmp("otdrPluseWidth").enable();
	  		  	Ext.getCmp("testDuration").enable();
	  		  	Ext.getCmp("otdrRefractCoefficient").enable();
			}
			var jsonString = {"jsonString":Ext.encode({"ROUTE_ID":this.value})};
			Ext.Ajax.timeout = 10000;
			Ext.Ajax.request({
				url: 'gis!getTestParamById.action',
				type: 'post',
				params: jsonString,
				success: function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.returnResult==1) {
						// 波长下拉框列表数据构建
				  		waveLengthStore.load({
			  		  		params: {
			  		  			"jsonString": Ext.encode({
			  		  				'routeId': routeId
			  		  			})
			  		  		},
			  			    callback: function(response) {
			  			    }
				  		});
				  		// 量程下拉框列表数据构建
					  	rangeStore.load({
			  		  		params: {
			  		  			"jsonString": Ext.encode({
			  		  				'routeId': routeId
			  		  			})
			  		  		},
			  			    callback: function(records, operation, success) {
			  			    }
				  		});
				  		// 脉宽下拉框列表数据构建
				  		pluseWidthStore.load({
			  		  		params: {
			  		  			"jsonString": Ext.encode({
				  					'routeId': routeId,
				  					'TEST_RANGE': obj.OTDR_RANGE
			  		  			})
			  		  		},
			  			    callback: function(records, operation, success) {
			  			    }
				  		});
				  		// 设置各项参数默认值
						Ext.getCmp("otdrWaveLength").setValue(obj.OTDR_WAVE_LENGTH);
			  		  	Ext.getCmp("otdrRange").setValue(obj.OTDR_RANGE);
			  		  	Ext.getCmp("otdrPluseWidth").setValue(obj.OTDR_PLUSE_WIDTH);
			  		  	Ext.getCmp("testDuration").setValue(obj.OTDR_TEST_DURATION);
			  		  	Ext.getCmp("otdrRefractCoefficient").setValue(obj.OTDR_REFRACT_COEFFICIENT);

					} else {
						Ext.Msg.alert("错误",obj.returnMessage);
					}
				},
				error: function(response) {
					alert('error');
				},
				failure: function(response) {
					alert('failure');
				}
			});	
		}
	}
};

var otdrRangeCombo = {
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
   			    	if (success) {
   			    		var maxpw = records[records.length-1].get("PLUSE_WIDTH");
   			    		var curpw = Ext.getCmp("otdrPluseWidth").getValue();
   			    		if (curpw > maxpw) { //如果当前脉冲宽度大于所选量程的最大脉宽，则设为当前量程的最大脉宽
   			    			Ext.getCmp("otdrPluseWidth").setValue(maxpw);
   			    		} else {
   			    			var find = false;
   			    			for (var i=0; i<records.length; i++) {
   			    				if (curpw == records[i].get("PLUSE_WIDTH")) {
   			    					find = true;
   			    					break;
   			    				}
   			    			}
   			    			//如果当前脉宽不在所选量程的脉宽列表中，则设为当前量程的最大脉宽
   			    			if (!find)
   			    				Ext.getCmp("otdrPluseWidth").setValue(maxpw);
   			    		}
   			    	}
   			    }
   			});
    	}
	}
};

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
        		text:"测试路由：",
        		columnWidth:.3
        	},testRouteCombo]
        },{
        	layout:'column',
        	border:false,
        	bodyStyle:'padding:10px 10px 2px 10px',
        	items: [{
        		xtype:"label",
        		text:"测试波长(nm)：",
        		columnWidth:.3
        	},{
        		xtype: 'combo',
                id:'otdrWaveLength',
                name: 'otdrWaveLength',
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
        	},otdrRangeCombo]
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
                id:'testDuration',
                name: 'testDuration',
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
	height: 300,
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
		handler : function(){
			testAction(Ext.getCmp("testRoute").getValue());
			confirmParaWin.hide();
		}
	},{
		xtype : 'button',
		text : '取消',
		handler : function(){
			confirmParaWin.hide();
		}
	}]
});

function testAction(testRouteId) {
	var prompt=new Ext.Window({
	    id:'prompt', 
        width : 250,      
        height : 160, 
	    isTopContainer : true,
	    modal : true,
	    autoScroll:false,
		maximized:false, 
		resizable:false,
		shadow:false,
		title: "状态",
		items : [{ 
			xtype:'panel',
		    height : 93, 
			border : false,
			items:[{
				border : false,
				style : 'margin-top:45px;margin-left:50px;',
				html : '<span><font size = "2.5">正在执行光缆测试，请稍候！</font></span>'
			}] 
		}], 
		buttonAlign:'center',
		buttons:[{
			text : '确定', 
			handler : function() {
				prompt.close();
			}
		} ]
	 }).show();
	prompt.center();
	var jsonString ="";
	// 所有测试路由
	if(testRouteId < 0) {
		var datas = testRouteStore.data.items;
		var ids = '';
		for(var i=0; i<datas.length-1;i++){
			ids += datas[i].data.testRouteId;
			ids +=",";
		}
		ids = ids.substring(0, ids.length-1);
		jsonString = {"jsonString":Ext.encode({
			"ROUTE_ID":ids
		})};
	// 单个测试路由
	}else{
		jsonString = {"jsonString":Ext.encode({
			"ROUTE_ID":testRouteId,
			"OTDR_WAVE_LENGTH":Ext.getCmp("otdrWaveLength").getValue(),
			"OTDR_RANGE":Ext.getCmp("otdrRange").getValue(),
			"OTDR_PLUSE_WIDTH":Ext.getCmp("otdrPluseWidth").getValue(),
			"OTDR_TEST_DURATION":Ext.getCmp("testDuration").getValue(),
			"OTDR_REFRACT_COEFFICIENT":Ext.getCmp("otdrRefractCoefficient").getValue()
		})};		
	}

	Ext.Ajax.timeout = 600000;
	Ext.Ajax.request({
		url:'gis!doTest.action',
		type: 'post',
		params: jsonString,
		success: function(response) {
			prompt.close();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult==1) {
				Ext.Msg.alert("提示",obj.returnMessage);
			} else {
				Ext.Msg.alert("错误",obj.returnMessage);
			}
		},
		error:function(response) {
			prompt.close();
			alert('error');
		},
		failure:function(response) {
			prompt.close();
			alert('failure');
		}
	});	
}
