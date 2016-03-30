function showCollectFailedPtp() {
	var record = dataGrid.getSelectionModel().getSelected();
	if(!record){
		Ext.Msg.alert("提示","请先选择报表记录！");
		return;
	}
	var failedId = record.get('ptpFailedId');

	var store = new Ext.data.Store({
		url : 'pm-report!searchCollectFailedPtpInfo.action',
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : "rows"
		}, [ "emsGroup", "ems", "subnet", "ne", "neType", "area", "station",
		     "ptp","ptpType","MS"])
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
			id : 'subnet',
			header : '子网',
			dataIndex : 'subnet',
			width : 95
		}, {
			id : 'ne',
			header : '网元',
			dataIndex : 'ne',
			width : 100
		}, {
			id : 'neType',
			header : '型号',
			dataIndex : 'neType',
			width : 80
		}, {
			id : 'ptp',
			header : '端口',
			dataIndex : 'ptp',
			width : 100
		}, {
			id : 'ptpType',
			header : '端口类型',
			dataIndex : 'ptpType',
			width : 80
		}, {
			id : 'MS',
			header : '所属复用段',
			dataIndex : 'MS',
			width : 100
		}, {
			id : 'area',
			header : top.FieldNameDefine.AREA_NAME,
			dataIndex : 'area',
			hidden : true,
			width : 100
		}, {
			id : 'station',
			header : top.FieldNameDefine.STATION_NAME,
			dataIndex : 'station',
			hidden : true,
			width : 100
		} ]
	});
	if(failedId!=null&&failedId!="")
	store.load({
		params : {
			"searchCond.ptpId" : failedId
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
		title : '失败端口清单',
		layout : 'border',
		height : 500,
		width : 800,
		autoScroll : true,
		items : [ gridPanel ],
		buttons : [ {
			text : '导出',
//			icon : '../../../resource/images/btnImages/export.png',
			disabled:true,
			handler : exportCollectFailedPTP
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
	function exportCollectFailedPTP(){

		var list = new Array();
		if(!gridPanel.getStore().getCount()>0)
			return;
			gridPanel.getStore().each(function(rec) {
				var record = {
						 "emsGroup":rec.get('emsGroup'), 
						 "ems":rec.get('ems'), 
						 "subnet":rec.get('subnet'), 
						 "ne":rec.get('ne'), 
						 "neType":rec.get('neType'), 
						 "area":rec.get('area'), 
						 "station":rec.get('station'),
						 "ptp":rec.get('ptp'),
						 "ptpType":rec.get('ptpType'),
						 "MS":rec.get('MS')
				};
				list.push(Ext.encode(record));
			});
			var params = {
				'modifyList' : Ext.encode(list),
				'searchCond.filename':exportFilenameAnalysisCFPTP,
				'searchCond.exportType' : 4
			};
		top.Ext.getBody().mask('正在导出，请稍候...');
		post('pm-report!exportAndDownloadPmAnalysisInfo.action', params);
//		window.location.href="pm-report!exportAndDownloadPmAnalysisInfo.action?"+Ext.urlEncode(params);
		top.Ext.getBody().unmask();
	}
//=================@
}