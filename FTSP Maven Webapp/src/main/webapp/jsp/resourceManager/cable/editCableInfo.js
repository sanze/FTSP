var coverData=[['1','架空'],['2','地埋'],['3','混合']];
var coverStore=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'displayField'}
	 ]
});
coverStore.loadData(coverData);

var fiberModelData=[['1','G.652'],['2','G.655'],['3','G.653'],
	['4','G.654'],['5','G.656'],['6','G.657']];

var fiberModelStore=new Ext.data.ArrayStore({
	fields:[
		{name:'value'},
		{name:'displayField'}
	 ]
});
fiberModelStore.loadData(fiberModelData);

var cableName = new Ext.form.TextField({
	id:'cName',
	name: 'cName',
	fieldLabel: '光缆段名称', 
	allowBlank:false,
	sideText : '<font color=red>*</font>',
	anchor:'90%' 
});

var cableNo = new Ext.form.TextField({
	id:'cNo',
	name: 'cNo',
	fieldLabel: '光缆段代号', 
	anchor:'90%'
});

var cableLength = new Ext.form.NumberField({
	id:'length_',
	name: 'length_',
	fieldLabel: '长度(km)',
	allowBlank:false,
	allowDecimals:true,
	allowNegative : false, 
	sideText : '<font color=red>*</font>',
	anchor:'90%'
});

var cableFiberCount = new Ext.form.NumberField({
	id:'fiberCount',
	name: 'fiberCount',
	allowDecimals: false,
	fieldLabel: '光纤芯数', 
	sideText : '<font color=red>*</font>',
	allowBlank:false, 
	anchor:'90%'
});

var cableType = new Ext.form.TextField({
	id:'cableType',
	name: 'cableType',
	fieldLabel: '光缆段型号',  
	anchor:'90%'
});

var cableFiberType = new Ext.form.ComboBox({
	id:'fiberType',
	name: 'fiberType',
	fieldLabel: '光纤型号',
	triggerAction: 'all',
	store:fiberModelStore,
	valueField: 'value',
	displayField: 'displayField',
	mode: 'local', 
	editable:false,
	anchor:'90%'
});

var cableCover = new Ext.form.ComboBox({
	id:'cableCover',
	name: 'cableCover',
	fieldLabel: '敷设方式',
	triggerAction: 'all',
	store:coverStore,
	valueField: 'value',
	displayField: 'displayField',
	mode: 'local', 
	editable:false,
	anchor:'90%'
}); 

var aEndStationId = new Ext.Panel({
	border:false,
	layout:'column',
	fieldLabel: '起始'+top.FieldNameDefine.STATION_NAME,
	items:[{
		xtype:'areaselector',
		id:'aStaId',
		readOnly:true,
		winWidth:300,
	    winHeight:350,
	    width:'130',
		allowBlank:false,
		targetLevel:11
	},{
		xtype:'label',
		html:'<font color=red>*</font>'
	}]
});

var zEndStationId = new Ext.Panel({
	border:false,
	layout:'column', 
	fieldLabel: '终点'+top.FieldNameDefine.STATION_NAME,
	items:[{
		xtype:'areaselector',
		id:'zStaId',
		readOnly:true,
		winWidth:300,
	    winHeight:350, 
	    width:'130',
		allowBlank:false,
		targetLevel:11
	},{
		xtype:'label',
		html:'<font color=red>*</font>'
	}]
});


var note = new Ext.form.TextField({
	id:'note',
	name: 'note',
	fieldLabel: '备注',
	anchor:'95%'
});

var buildTime = new Ext.form.TextField({
	id:'time',
	name: 'time',
	fieldLabel: '开通时间', 
    anchor:'90%',
    cls : 'Wdate',
    value:this.nowTime, 
    listeners: {
        'focus': function(){
            WdatePicker({
                el : "time",
                isShowClear : false,
                readOnly : true,
                dateFmt : 'yyyy-MM-dd',
                autoPickDate : true 
            });
			this.blur();
        }
    }
});

var attBuild = new Ext.form.NumberField({
	id:'attBuild',
	name: 'attBuild',
	fieldLabel: '光纤衰耗竣工值（dB）', 
	allowDecimals:true,
	allowNegative : false, 
	anchor:'95%'
});

var attExper = new Ext.form.NumberField({
	id:'attExper',
	name: 'attExper',
	fieldLabel: '衰耗系数经验值（dB/km）', 
	allowDecimals:true,
	allowNegative : false, 
	emptyText:'0.28',
	anchor:'90%'
});

var attTheory = new Ext.form.NumberField({
	id:'attTheory',
	name: 'attTheory',
	fieldLabel: '衰耗系数理论值（dB/km）', 
	allowDecimals:true,
	allowNegative : false, 
	emptyText:'0.24',
	anchor:'90%'
});
 
var attValue = new Ext.form.NumberField({
	id:'attValue',
	name: 'attValue',
	fieldLabel: '光纤衰耗基准值（dB）', 
	allowDecimals:true,
	allowNegative : false, 
	anchor:'95%'
});
 
function GenerateCodeNameComboGrid(params){
	var dsParam={
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "CODE_NAME_ID","CODE","NAME"]),
		listeners:{
		  	"exception": function(proxy,type,action,options,response,arg){
		  		Ext.Msg.alert("提示","加载出错"+
					"<BR>Status:"+response.statusText||"unknow");
		  	}
		}
	};
	Ext.apply(dsParam,{ 
		url:'resource-cable!getAllCodeNames.action'
	});
	var ds=new Ext.data.Store(dsParam);
	return Ext.apply({
		tbar: [
	       new Ext.ux.form.SearchField({
	    	   store: ds,
	    	   paramName: [{property:'NAME',anyMatch:true},{property:'CODE',anyMatch:true}],
	    	   logical: "or"
	       })
        ],
    	xtype: 'combogrid',
    	columns : [ {
            header : '光缆名称',
            dataIndex : 'NAME'
        }, {
            header : '光缆代号',
            dataIndex : 'CODE'
        } ],
        autoLoad: true,
        valueField: "CODE_NAME_ID",
        textField: ["NAME","CODE"], 
        store : ds,
        editable : false,
        allowBlank : true
    },params);
}

var cablesForm = new Ext.form.FormPanel({
	id:"cablesForm",
	region:"center",
    frame:false,
    bodyStyle:'padding:30px 20px 0px 30px', 
    labelAlign: 'left',
    labelWidth : 65,
    autoScroll:true,
    items:[GenerateCodeNameComboGrid({
	   	id:'cable',
    	fieldLabel:'光缆',
        allowBlank : false, 
        anchor:'95%'
	}),{  
    	 layout : "column",
         border : false,
         items : [{
             columnWidth : .5,
             layout : "form", 
             border : false,
             items : [cableName,cableLength,cableType]
         }, {
             columnWidth : .5,
             layout : "form", 
             border : false,
             items : [cableNo,cableFiberCount,cableFiberType]
         }]
     },{ 
		layout : "column",
		  labelWidth : 160,
		border : false,
		items : [{
			columnWidth : .5,
			layout : "form", 
			border : false,
			items : [attTheory]
		}, {
			columnWidth : .5,
			layout : "form", 
			border : false,
			items : [attExper] 
		}]
	},{
		 layout : "column",
	     border : false,
	     items : [{
             columnWidth : .5,
             layout : "form", 
             border : false,
         	 items : [cableCover,aEndStationId]
         }, {
             columnWidth : .5,
             layout : "form", 
             border : false,
             items : [buildTime,zEndStationId] 
         }] 
	},note,{ 
    	   layout : "form",
    	   labelWidth : 130,
           border : false,
    	   items:[attBuild,attValue]
		}],
		buttons : [ {
			scope : this,
			text : '确定',
			handler : function() {
				var win = parent.Ext.getCmp('cableWin'); 
				if(cablesForm.getForm().isDirty() && cablesForm.getForm().isValid()){
				if(Ext.getCmp('zStaId').getValue()=='' || Ext.getCmp('zStaId').getValue()==''){
					Ext.Msg.alert('提示', '请选择局站！');
					return;
				} 
				
					/*alert(cablesForm.getForm().isDirty());
					alert(cablesForm.getForm().isValid());*/
					cablesForm.getForm().submit({
						url : (cableId==0)?"resource-cable!addCable.action":"resource-cable!modCable.action",
						method:"POST",
						params:{
							comboType:Ext.getCmp('fiberType').getValue(),
							comboCover:Ext.getCmp('cableCover').getValue(),
							aStationId:Ext.getCmp('aStaId').rawValue.id,
							zStationId:Ext.getCmp('zStaId').rawValue.id, 
							ids:Ext.getCmp('cable').getValue(),
							cellId:	cableId,
							originalValue:Ext.getCmp('fiberCount').originalValue
						},
						/*waitTitle : "请稍候",
						waitMsg : "正在提交表单数据，请稍候",*/
						success : function(form, action) {
							/*Ext.Msg.alert("提示",action.result.returnMessage, function(r) {
								
								var pageTool = parent.Ext.getCmp('pageTool');
								if (pageTool) {
									pageTool.doLoad(pageTool.cursor);
								}
								if (win) {
									win.close();
								} 
							}); */  
							//关闭窗口 
							var pageTool = parent.Ext.getCmp('pageTool');
							if (pageTool) {
								pageTool.doLoad(pageTool.cursor);
							}
							if (win) {
								win.close();
							} 
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
				var win = parent.Ext.getCmp('cableWin');
				if (win) {
					win.close();
				}
			}
		}]
});

function initData(){
    Ext.getCmp('attValue').hide();
    Ext.getCmp('attBuild').hide();
	Ext.Ajax.request({
		url: 'resource-cable!getCableInfo.action', 
		method : 'POST',
		params:{'cellId':cableId},
		success: function(response) {
		    var obj = Ext.decode(response.responseText);
		    if(obj.returnResult == 1){
		    	Ext.getCmp('cName').setValue(obj.CABLE_NAME);
		    	Ext.getCmp('cNo').setValue(obj.CABLE_NO);
		    	Ext.getCmp('length_').setValue(obj.CABLE_LENGTH);
		    	Ext.getCmp('fiberCount').setValue(obj.CABLE_FIBER_COUNT);
		    	Ext.getCmp('fiberCount').originalValue = obj.CABLE_FIBER_COUNT;
		    	Ext.getCmp('cableType').setValue(obj.CABLE_TYPE);
		    	Ext.getCmp('fiberType').setValue(obj.CABLE_FIBER_TYPE);
		    	Ext.getCmp('cableCover').setValue(obj.CABLE_COVER);
		    	Ext.getCmp('aStaId').setValue(obj.A_END_STATION_NAME);
		    	Ext.getCmp('aStaId').rawValue.id = obj.A_END;
		    	Ext.getCmp('zStaId').setValue(obj.Z_END_STATION_NAME);
		    	Ext.getCmp('zStaId').rawValue.id = obj.Z_END;
		    	Ext.getCmp('note').setValue(obj.NOTE);
                Ext.getCmp('time').setValue(obj.buildTime);
                Ext.getCmp('attExper').setValue(obj.ATT_COEFFICIENT_EXPERIENCE);
                Ext.getCmp('attTheory').setValue(obj.ATT_COEFFICIENT_THEORY); 
                Ext.getCmp('cable').setValue(obj.RESOURCE_CABLES_ID); 
	        }else{
	        	Ext.Msg.alert("错误",obj.returnMessage);
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

Ext.onReady(function(){
	Ext.Msg = top.Ext.Msg;
	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};

    var win = new Ext.Viewport({
    	id:'win', 
        layout: 'border',
        items : [cablesForm],
        renderTo : Ext.getBody()
    });
    if(!!cableId && cableId>0){ 
    	initData();
    }
});