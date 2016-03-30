var cable1ComboGridField;
var fiber1ComboGridField;

(function(){
	var cableStore =new Ext.data.Store({
		url : 'resource-cable!getCableList.action',
		baseParams : { 
		},
	   reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		},["RESOURCE_CABLES_ID","CABLES_NAME","CABLES_NO","RESOURCE_CABLE_ID","CABLE_NAME","CABLE_NO"])
	});
	var fiberStore =new Ext.data.Store({
		url : 'resource-cable!getFiberResourceList.action', 
		baseParams : { 
		},
	   reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		},["RESOURCE_FIBER_ID","FIBER_NAME","FIBER_NO"])
	});
	cable1ComboGridField=new Ext.form.TriggerField({
		id:'cable1ComboGridField',
        name: 'cable1ComboGridField',
        fieldLabel: '光缆段',
        childField:'fiber1ComboGridField',
        textField: ['CABLE_NAME','CABLE_NO'],
        valueField: 'RESOURCE_CABLE_ID',
        listeners: {
        	render: onAfterRenderCable,
        	reset: function(me,newval,oldval){
        		OnSelectCable.createDelegate(me,[newval])();
        	}
        }
    });
	fiber1ComboGridField=new Ext.form.TriggerField({
		id:'fiber1ComboGridField',
        name: 'fiber1ComboGridField',
        fieldLabel: '光纤',
        listeners: {
        	afterrender: onAfterRenderCable,
        	reset: function(me,newval,oldval){
        		OnSelectCable.createDelegate(me,[newval])();
        	}
        }
    }); 
	function onAfterRenderCable(self){
		var childField=Ext.getCmp(self.initialConfig.childField);
		if(childField) childField.disable();
		var config={
				applyId: self.getId(),
				childField: self.initialConfig.childField,
				autoLoad : true,
				onSelect : OnSelectCable
		};
		if("cable1ComboGridField"==self.getId()){
			config.textField=self.initialConfig.textField,
			config.valueField=self.initialConfig.valueField,
			config.store = cableStore,
			config.columns = [{
		        header : '光缆名称',
		        dataIndex : 'CABLES_NAME',
		        hidden: true
		    }, {
		        header : '光缆代号',
		        dataIndex : 'CABLES_NO',
		        hidden: true
		    }, {
		        header : '光缆段名称',
		        dataIndex : 'CABLE_NAME'
		    }, {
		        header : '光缆段代号',
		        dataIndex : 'CABLE_NO'
		    }];
		}else if("fiber1ComboGridField"==self.getId()){
			config.textField=['FIBER_NO','FIBER_NAME'],
			config.valueField='RESOURCE_FIBER_ID',
			config.store = fiberStore,
			config.columns = [{
		        header : '芯号',
		        dataIndex : 'FIBER_NO'
		    },{
		        header : '光纤名称',
		        dataIndex : 'FIBER_NAME'
		    }];
		}
		self.comboGrid=new ComboGrid(config);
	}
	function OnSelectCable(val){
		if(this.childField&&Ext.getCmp(this.childField)){
			var childField=Ext.getCmp(this.childField);
			var step=1;
			while(childField && childField.comboGrid){
				if(step==1){
					if(val) childField.enable();
					else childField.disable();
				}else childField.disable();
				var childStore=childField.comboGrid.grid.getStore();
				childStore.baseParams={};
				childStore.baseParams[this.valueField]=val;
				childStore.baseParams["cellId"]=val?val:0;
				childStore.load();
				childField.reset();
				childField.comboGrid.inputVal.reset();
				childField=childField.childField?Ext.getCmp(childField.childField):null;
				step++;
			}
		}
	}
})();