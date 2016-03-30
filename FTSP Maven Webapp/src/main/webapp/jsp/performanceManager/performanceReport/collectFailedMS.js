function showCollectFailedMS() {
	var record = dataGrid.getSelectionModel().getSelected();
	if(!record){
		Ext.Msg.alert("提示","请先选择报表记录！");
		return;
	}
	var failedId = record.get('MSFailedId');

	var store = new Ext.data.Store({
		url : 'pm-report!searchCollectFailedMSInfo.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "emsGroup", "ems","emsType","TL","direction","stdWave","actualWave","MS"])
	});
	var cm = new Ext.grid.ColumnModel({
		defaults : {
			sortable : true
		},
		columns : [ new Ext.grid.RowNumberer({
			width : 26
		}), {
			id : 'emsGroup',
			header : '网管分组',
			dataIndex : 'emsGroup',
			width : 100
		}, {
			id : 'ems',
			header : '网管',
			dataIndex : 'ems',
			width : 100
		}, {
			id : 'emsType',
			header : '网络类型',
			dataIndex : 'emsType',
			width : 100,
			renderer: emsTypeRender
		}, {
			id : 'TL',
			header : '干线名称',
			dataIndex : 'TL',
			width : 95
		}, {
			id : 'MS',
			header : '光复用段名称',
			dataIndex : 'MS',
			width : 100
		}, {
			id : 'direction',
			header : '方向',
			dataIndex : 'direction',
			width : 80,
			renderer:directionRender
		}, {
			id : 'stdWave',
			header : '标称波道数',
			dataIndex : 'stdWave',
			width : 100
		}, {
			id : 'actualWave',
			header : '实际波道数',
			dataIndex : 'actualWave',
			width : 80
		}]
	});
	if(failedId!=null&&failedId!="")
	store.load({
		params : {
			"searchCond.MSId" : failedId
		},
		callback : function(r, scope, success) {
			if (!success)
				Ext.Msg.alert('提示', '获取网元信息失败！');
		}
	});

	var gridPanel = new Ext.grid.GridPanel({
		id : 'gridPanel',
		cm : cm,
		store : store,
		region : 'center',
		stripeRows : true
	});

	// ----------------------------------------------------------------------------
	var win = new Ext.Window({
		id : 'showCollectFailedPtpWin',
		title : '失败复用段清单',
		layout : 'border',
		height : 500,
		width : 800,
		autoScroll : true,
		items : [ gridPanel ],
		buttons : [ {
			text : '导出',
			disabled:true,
//			icon : '../../../resource/images/btnImages/export.png',
			handler : exportCollectFailedMS
		}, {
			text : '确定',
//			icon : '../../../resource/images/btnImages/submit.png',
			handler : function() {
				win.close();
			}
		} ]
	});
	win.show();
	if (win.getHeight() > Ext.getCmp('win').getHeight()) {
		win.setHeight(Ext.getCmp('win').getHeight() * 0.7);
	} else {
		gridPanel.setHeight(win.getInnerHeight());
	}
	win.center();
	win.doLayout();
	
	
//=================@
	function exportCollectFailedMS(){

		var list = new Array();
		if(!gridPanel.getStore().getCount()>0)
			return;
			gridPanel.getStore().each(function(rec) {
				var direction = directionRender(rec.get('direction'));
				var emsType = emsTypeRender(rec.get('emsType'));
				var record = {
						"emsGroup":rec.get('emsGroup'),
						"ems":rec.get('ems'),
						"emsType":emsType,
						"TL":rec.get('TL'),
						"direction":direction,
						"stdWave":rec.get('stdWave'),
						"actualWave":rec.get('actualWave'),
						"MS":rec.get('MS')
				};
				list.push(Ext.encode(record));
			});
			var params = {
				'modifyList' :  Ext.encode(list),
				'searchCond.filename':exportFilenameAnalysisCFMS,
				'searchCond.exportType' : 3
			};
		top.Ext.getBody().mask('正在导出，请稍候...');
		post('pm-report!exportAndDownloadPmAnalysisInfo.action', params);
//		window.location.href="pm-report!exportAndDownloadPmAnalysisInfo.action?"+Ext.urlEncode(params);
		top.Ext.getBody().unmask();
	}
//=================@	
	function emsTypeRender(v){
		for(var type in NMS_TYPE){
			if(v==NMS_TYPE[type]['key']){
				return NMS_TYPE[type]['value'];
			}
		}
		return v;
	}
	
	function directionRender(v){
		if(v==1)
			return '单向';
		if(v==2)
			return '双向';
	}
}