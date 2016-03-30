
Ext.onReady(function () {
	Ext.BLANK_IMAGE_URL = "data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
    Ext.Ajax.timeout = 900000;
    document.onmousedown = function () {
        top.Ext.menu.MenuMgr.hideAll();
    };
    
    var combo;
    (function(){
    	var store = new Ext.data.ArrayStore({
    		fields : [ {name:'id',mapping:'key'}, {name:'displayText',mapping:'value'} ],
    		data:[]
    	});
    	store.loadData(REPORT_TYPE,true);
    	combo = new Ext.form.ComboBox({
    		id : 'taskType',
    		triggerAction : 'all',
    		typeAhead: true,
    		editable : false,
    		flex:1,
            lazyRender:true,
    		mode:'local',
    		store : store,
    		value:typeDefine.WAVELENGTH,
    		displayField : "displayText",
    		valueField : 'id'
    	});
    })();
    var win = new Ext.Window({
		title: '报表设置',
		id : 'repSetting',
		layout : {
			type : 'hbox',
			padding : '0',
			align : 'middle'
		},
		modal : true,
		closable:true,
		plain:true,
		width: 300,
		height: 120,
		items : [new Ext.Spacer({
			width:40,
			height:10
		}),
		combo,
		new Ext.Spacer({
 			width:40,
			height:10
		})],
	    buttons: [{
	        text: '确定',
	        id:"btnOK",
	        handler: function(){
	        	//console.log("Click->确定");
	        	var url;
//	        	console.log("authSequence = " + authSequence);
	        	//console.log("Open -> " + "../performanceManager/pmInstantGen/" + url[combo.getValue()]);
	        	switch(combo.getValue()){
	        	case typeDefine.WAVELENGTH:
	        		url = "../../jsp/nxReportManager/instantReport/WDMWaveLengthInstant.jsp";break;
	        	case typeDefine.AMP:
	        		url = "../../jsp/nxReportManager/instantReport/WDMAmplifierInstant.jsp";break;
	        	case typeDefine.SWITCH:
	        		url = "../../jsp/nxReportManager/instantReport/WDMSwitchInstant.jsp";break;
	        	case typeDefine.WAVE_DIV:
	        		url = "../../jsp/nxReportManager/instantReport/WDMJoinAndDivInstant.jsp";break;
	        	case typeDefine.WAVE_JOIN:
	        		url = "../../jsp/nxReportManager/instantReport/WDMJoinAndDivInstant.jsp";break;
	        	case typeDefine.SDH_PM:
	        		url = "../../jsp/performanceManager/pmInstantGen/pmInstantGenNe.jsp";break;
	        	}
	        	url+="?repType="+combo.getValue();
	        	top.addTabPage(url, "定制即时报表生成", authSequence);
	        }
	    },{
	        text: '取消',
	        handler: function(){
	        	top.closeTab("定制即时报表生成");
	        }
	    }],
//	    buttonAlign:"center"
	});
	win.show();
});