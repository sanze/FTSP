/**
 * 网络层次数据
 */
var netLevelFieldLabel=top.FieldNameDefine.NET_LEVEL_NAME;

var netLevelComboBar;
var projectComboGridBar;
//var linkComboGridBar;

/**
 * 网络层次下拉框
 */
var netLevelComboField;
var projectComboGridField;
var linkComboGridField;

(function(){
	var netLevelStore = new Ext.data.ArrayStore({
		fields : [ {name:'netLevelId',mapping:'key'}, {name:'netLevel',mapping:'value'} ],
		data:NET_LEVEL
	});
	netLevelComboField = new Ext.form.ComboBox({
		id:'netLevelComboField',
        name: 'netLevelComboField',
        fieldLabel: netLevelFieldLabel,
        childField:'projectComboGridField',
        listeners: {
        	select: function(combo,record,index){
        		OnSelectProject.createDelegate(combo,[record.get(combo.initialConfig.valueField)])();
        	}/*,
			afterrender: function (self){
				var childField=Ext.getCmp(self.initialConfig.childField);
				if(childField) childField.disable();
			}*/,
        	reset: function(me,newval,oldval){
        		var childField=Ext.getCmp(me.initialConfig.childField);
				if(childField){
					var childStore=childField.comboGrid.grid.getStore();
					childStore.baseParams={};
					childStore.baseParams[this.valueField]=newval;
					childStore.load();
					childField.reset();
				}
//        		OnSelectProject.createDelegate(me,[newval])();
        	}
        },
        reset: function(){
        	this.fireEvent('reset',this,this.originalValue,this.getValue());
        	this.setValue(this.originalValue);
        	this.clearInvalid();
        },
		mode : 'local',
		store : netLevelStore,
		displayField : 'netLevel',
		valueField : 'netLevelId',
		triggerAction : 'all',
		editable : false,
		resizable : true
	});
	
	var projectStore =new Ext.data.Store({
		url : 'project!getAllProject.action',
		baseParams : {
			//"stationIds":[],
			//"RESOURCE_PROJECTS_ID":
		},
	   reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		},["RESOURCE_TRANS_SYS_ID","SYS_NAME","SYS_CODE","NET_LEVEL"])
	});
	var linkStore =new Ext.data.Store({
		url : 'project!getAllLink.action', 
		baseParams : {
			//"stationIds":[],
			//"RESOURCE_PROJECT_ID":
		},
		reader: new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		},["BASE_LINK_ID","SYS_NAME","LINK_NAME","A_NE_NAME","A_PTP_NAME","Z_NE_NAME","Z_PTP_NAME"])
	});

	projectComboGridField=new Ext.form.TriggerField({
		id:'projectComboGridField',
        name: 'projectComboGridField',
        fieldLabel: '传输系统',
        /*childField:'linkComboGridField',*/
        listeners: {
        	afterrender: onAfterRenderProject,
        	reset: function(me,newval,oldval){
        		OnSelectProject.createDelegate(me,[newval])();
        	}
        }
    });
	linkComboGridField=new Ext.form.TriggerField({
		id:'linkComboGridField',
        name: 'linkComboGridField',
        fieldLabel: '链路',
        listeners: {
        	afterrender: onAfterRenderProject
        }
    });
	netLevelComboBar=new Ext.form.FormPanel({labelSeparator:'：',labelWidth:50,
		border:false,autoHeight:true,unstyled:true,padding:"5px 0 0 0",
		items:[netLevelComboField]});
	projectComboGridBar=new Ext.form.FormPanel({labelSeparator:'：',labelWidth:50,
		border:false,autoHeight:true,unstyled:true,padding:"5px 0 0 0",
		items:[projectComboGridField]});
	linkComboGridBar=new Ext.form.FormPanel({labelSeparator:'：',labelWidth:40,
		border:false,autoHeight:true,unstyled:true,padding:"5px 0 0 0",
		items:[linkComboGridField]});
	function onAfterRenderProject(self){
		var childField=Ext.getCmp(self.initialConfig.childField);
		if(childField) childField.disable();
		var config={
				applyId: self.getId(),
				childField: self.initialConfig.childField,
				autoLoad : true,
				onSelect : OnSelectProject
		};
		if("projectComboGridField"==self.getId()){
			config.textField=['SYS_NAME','SYS_CODE'],
			config.valueField='RESOURCE_TRANS_SYS_ID',
			config.store = projectStore,
			config.columns = [{
		        header : '系统名称',
		        dataIndex : 'SYS_NAME'
		    }, {
		        header : '系统代号',
		        dataIndex : 'SYS_CODE'
		    }];
		}else if("linkComboGridField"==self.getId()){
			config.textField='LINK_NAME',
			config.valueField='BASE_LINK_ID',
			config.store = linkStore,
			config.columns = [{
		        header : '链路ID',
		        dataIndex : 'BASE_LINK_ID',
		        hidden: true
		    },{
		        header : '系统名称',
		        dataIndex : 'SYS_NAME',
		        hidden: true
		    },{
		        header : '链路名称',
		        dataIndex : 'LINK_NAME'
		    },{
		        header : 'A端网元',
		        dataIndex : 'A_NE_NAME'
		    },{
		        header : 'A端端口',
		        dataIndex : 'A_PTP_NAME'
		    },{
		        header : 'Z端网元',
		        dataIndex : 'Z_NE_NAME'
		    },{
		        header : 'Z端端口',
		        dataIndex : 'Z_PTP_NAME'
		    }];
		}
		self.comboGrid=new ComboGrid(config);
	}
	function OnSelectProject(val){
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
				childStore.load();
				childField.reset();
				childField.comboGrid.inputVal.reset();
				childField=childField.childField?Ext.getCmp(childField.childField):null;
				step++;
			}
		}
	}
})();