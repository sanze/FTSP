function compareValue(v,important,less,normal){   
	if(v!=null && v!='-' && v!=""){ 
		if(important!=null && important!='' && important>0 && v<important){
			return '<font color="#FF0000">' + v + "%"+'</font>';
		}else if(less!=null && less!='' && less>0 && v<less){
			return '<font color="#FF8000">' + v + "%"+'</font>';
		}else if(normal!=null && normal!='' && normal>0 && v<normal){
			return '<font color="#DBDB70">' + v + "%"+'</font>';
		}
		return v +"%";
	}
	return "-" ;
}  

function compareValueRL(v,important,less,normal,showVal){   
	if(v!=null && v!='-' && v!=""){ 
		if(important!=null && important!='' && important>0 && v>=important){
			return '<font color="#FF0000">' + showVal +'</font>';
		}else if(less!=null && less!='' && less>0 && v>=less){ 
			return '<font color="#FF8000">' + showVal +'</font>';
		}else if(normal!=null && normal!='' && normal>0 && v>=normal){
			return '<font color="#DBDB70">' + showVal + '</font>';
		}
		return showVal;
	}
	return showVal ;
}  
var SLOT_MJ,SLOT_MN,SLOT_WR,PTP_MJ,PTP_MN,PTP_WR,MS_VC4_MJ,MS_VC4_MN,MS_VC4_WR,
	MS_VC12_MJ,MS_VC12_MN,MS_VC12_WR,LARGE_RING_MJ,LARGE_RING_MN,LARGE_RING_WR,
	LONG_CHAIN_MJ,LONG_CHAIN_WR,LONG_CHAIN_MN;
function getEarlyAlarmSetting() {
	Ext.Ajax.request({
		url : 'network!getEarlyAlarmSetting.action',
		params : {"jsonString":"*"},
		method : 'POST',
		success : function(response) {
			var obj = Ext.decode(response.responseText); 
			if (obj.returnResult && obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}else{
				SLOT_MJ = obj.SLOT_MJ;
				SLOT_MN = obj.SLOT_MN;
				SLOT_WR = obj.SLOT_WR; 
				PTP_MJ = obj.PTP_MJ;
				PTP_MN = obj.PTP_MN;
				PTP_WR = obj.PTP_WR; 
				MS_VC4_MJ = obj.MS_VC4_MJ;
				MS_VC4_MN = obj.MS_VC4_MN;
				MS_VC4_WR = obj.MS_VC4_WR; 
				MS_VC12_MJ = obj.MS_VC12_MJ;
				MS_VC12_MN = obj.MS_VC12_MN;
				MS_VC12_WR = obj.MS_VC12_WR;
				LARGE_RING_MJ = obj.LARGE_RING_MJ; 
				LARGE_RING_MN = obj.LARGE_RING_MN; 
				LARGE_RING_WR = obj.LARGE_RING_WR;   
				LONG_CHAIN_MJ = obj.LONG_CHAIN_MJ; 
				LONG_CHAIN_WR = obj.LONG_CHAIN_WR; 
				LONG_CHAIN_MN = obj.LONG_CHAIN_MN; 
	    	}
		},
		error : function(response) {
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			Ext.Msg.alert("错误", response.responseText);
		} 
	});
}   

function exportRequest(params) { 

	Ext.Ajax.request({
		url : 'network!exportExcel.action',
		type : 'POST',
		params : params,
		success : function(response) {
			gridPanel.getEl().unmask();
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else { 
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
}; 

function exportExcelByParams(params) {  
	Ext.Ajax.request({
		url : 'network!exportExcelByParams.action',
		type : 'POST',
		params : params,
		success : function(response) {
			gridPanel.getEl().unmask();
			var rs = Ext.decode(response.responseText);
			if (rs.returnResult == 1 && rs.returnMessage != "") {
				var destination = {
					"filePath" : rs.returnMessage
				};
				window.location.href = "download!execute.action?"
						+ Ext.urlEncode(destination);
			} else { 
				Ext.Msg.alert("提示", "导出失败！");
			}
		},
		error : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			gridPanel.getEl().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	});
};

var descLevel1 = "省", descLevel2 = "市", descLevel3 = "区", descLevel4 = "街道"; 
var tree = null; 
var westPanel = {
        region : "west",
        title : top.FieldNameDefine.AREA_NAME+'选择',
        width : 280,
        minSize : 230,
        maxSize : 320,
        autoScroll : true,
        forceFit : true,
        collapsed : false,
        collapsible : true,
        split : true,
        id : "tree",
        xtype : "area",
        maxLevel : 4, 
        checkModel : "path"  
    }; 
var proTypeRenderer;
if(typeof PRO_GROUP_TYPE != undefined && !!PRO_GROUP_TYPE){   
	proTypeRenderer = function (v) {
		for(var i=0;i<PRO_GROUP_TYPE.length;i++){
			if(v==PRO_GROUP_TYPE[i]['key'])
				return PRO_GROUP_TYPE[i]['value'];
		}
		return v;
	};
} 
var protectTypeCombo; 
(function() { 	 
	var dataKineStore = new Ext.data.ArrayStore({
		fields : [ {name:'dataKind',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:[]
	});
	var defaultData = {
		dataKind : 7,
		displayName :"全部"
	};
	dataKineStore.loadData(PRO_GROUP_TYPE);
	dataKineStore.add(new dataKineStore.recordType(defaultData));  
	protectTypeCombo = new Ext.form.ComboBox({
		id : 'protectTypeCombo',
		fieldLabel : '保护类型',
		privilege : viewAuth,
		store : dataKineStore,
		displayField : "displayName",
		valueField : 'dataKind',
		triggerAction : 'all',
		mode : 'local',
		editable : false,
		allowBlank : false,
		value : '7',
		width : 100
	});
})();

var netLevelRenderer;
if(!!NET_LEVEL){ 
	netLevelRenderer = function (v) {
		for(var i=0;i<NET_LEVEL.length;i++){
			if(v==NET_LEVEL[i]['key'])
				return NET_LEVEL[i]['value'];
		}
		return v;
	};
}
var levelCombo;
(function() {  
	var dataKineStore = new Ext.data.ArrayStore({
		fields : [ {name:'dataKind',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:[]
	});
	dataKineStore.loadData(NET_LEVEL); 
	var defaultData = {
		dataKind : 6,
		displayName :"全部"
	}; 
	dataKineStore.add(new dataKineStore.recordType(defaultData));  
	levelCombo = new Ext.form.ComboBox({
		id : 'levelCombo',
		fieldLabel : top.FieldNameDefine.NET_LEVEL_NAME,
		privilege : viewAuth,
		store : dataKineStore,
		displayField : "displayName",
		valueField : 'dataKind',
		triggerAction : 'all',
		mode : 'local',
		editable : false,
		allowBlank : false,
		value : '6',
		width : 100
	});
})(); 
var searchPanel = new Ext.FormPanel({
	id : 'FormPanel',
	region : 'north',
	height : 60,
	bodyStyle : 'padding:20px 10px 0', 
	autoScroll : true,
	items : [{
		border : false, 
		items : [{ 
			border : false, 
			layout : 'column',
			items : [ { 
				width : 120,
				layout : 'form',
				labelWidth : 80,
				border : false,
				labelWidth : 5,
				items : [ {
					xtype : 'checkbox',
					id : 'alarm3',
					checked : true,
					boxLabel : '重要预警',
					inputValue : '3'
					} ]
				}, { 
					width : 120,
					layout : 'form',
					labelWidth : 80,
					border : false,
					labelWidth : 5,
					items : [ {
						xtype : 'checkbox',
						id : 'alarm2',
						checked : true,
						boxLabel : '次要预警',
						inputValue : '2'
					} ]
				},{ 
					width : 120,
					layout : 'form',
					labelWidth : 80,
					border : false,
					labelWidth : 5,
					items : [ {
						xtype : 'checkbox',
						id : 'alarm1',
						checked : true,
						boxLabel : '一般预警',
						inputValue : '1'
					} ]
				}, { 
					width : 250,
					layout : 'form',
					labelWidth : 80,
					border : false,
					items : [ protectTypeCombo ]
	
				},{ 
					width : 250,
					layout : 'form',
					labelWidth : 80,
					border : false,
					items : [ levelCombo ]
			}]
		}]
	}]
});

var store = new Ext.data.Store({ 
	reader : new Ext.data.JsonReader({
		totalProperty : 'total',
		root : "rows"
	}, ["RESOURCE_TRANS_SYS_ID","areaName","SYS_NAME","SYS_CODE","DOMAIN",
	    "TYPE","NODE_COUNT","PRO_GROUP_TYPE","RATE","NET_LEVEL","TRANS_MEDIUM",
	   "WAVE_COUNT","GENERATE_METHOD","STATUS","emsDisplayName","NOTE"
	   ])
}); 


function toCountDetail(id) {  
	var rec = store.getAt(store.findExact("RESOURCE_TRANS_SYS_ID",id));
	var name = rec.get("SYS_NAME");
	var areaName = rec.get("areaName");
	var url = '../networkAnalysis/areaNodeList.jsp?rlId='+id+'&areaName='+areaName;  
	var nodeListWin = new Ext.Window({
		id : 'nodeListWin',
		title : '节点列表（'+name+"）",
		width : 850,
		height : 400,
		isTopContainer : true,
		modal : true,
		plain : true, // 是否为透明背景
		html : '<iframe src=' + url + ' height="100%" width="100%" frameborder=0 border=0 />'
	});
	nodeListWin.show(); 
}