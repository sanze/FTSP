// ************************* WDM设置 ****************************
var TPLevel = {
	xtype : 'fieldset',
	title : 'TP等级',
	height : 165,
	anchor : '95%',
	labelWidth : 10,
	items : {
		id : 'TPLevel',
		xtype : 'checkboxgroup',
		columns : 1,
		items : [ {
			checked : true,
			boxLabel : '光传送和复用单元',
			inputValue : 'OTS&OMS'
		}, {
			checked : true,
			boxLabel : '光通道',
			inputValue : 'OCH'
		}, {
			checked : true,
			boxLabel : '光监控通道',
			inputValue : 'OSC'
		}, {
			checked : true,
			boxLabel : '其他',
			id : 'WDMTPLevelOther',
			inputValue : 'other'
		} ]
	}
};

var wdmPhysicFieldset = {
	id : 'wdmPhysicFieldset',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	height : 138,
	title : 'WDM物理量',
	items : [ {
		layout : 'column',
		border : false,
		items : [ {
			columnWidth : 0.5,
			border : false,
			layout : 'form',
			id : 'wdmPhysicFieldsetA',
			items : [ {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '发光功率',
				inputValue : 1
			}, {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '收光功率',
				inputValue : 2
			}, {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '信道信噪比',
				inputValue : 11
			}, {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '激光器电流',
				inputValue : 4
			}  ]
		}, {
			columnWidth : 0.5,
			border : false,
			layout : 'form',
			id : 'wdmPhysicFieldsetB',
			labelWidth : 20,
			items : [ {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '信道中心波长/偏移',
				inputValue : 10
			}, {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '信道光功率',
				inputValue : 9
			}, {
				xtype : 'checkbox',
				checked : true,
				boxLabel : '工作温度',
				inputValue : 3
			}, {
				xtype : 'checkbox',
				checked : false,
				id : 'wdmPhyOther',
				boxLabel : '其他',
				inputValue : 16
			} ]
		} ]
	} ]
};

var wdmNumbericFieldset = {
	id : 'wdmNumbericFieldset',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	height : 165,
	title : 'WDM计数值',
	items : [ {
		xtype : 'checkbox',
		checked : true,
		boxLabel : '光监控信道误码',
		inputValue : 12
	}, {
		xtype : 'checkbox',
		checked : true,
		boxLabel : 'FEC误码率',
		inputValue : 13
	}, {
		xtype : 'checkbox',
		checked : true,
		boxLabel : 'OTU误码',
		inputValue : 14
	}, {
		xtype : 'checkbox',
		checked : false,
		boxLabel : 'ODU误码',
		inputValue : 15
	}, {
		xtype : 'checkbox',
		boxLabel : '其他',
		checked : false,
		id : 'wdmNumOther',
		inputValue : 17
	} ]
};

var WDM = {
	id : 'WDM',
	border : false,
	layout : 'column',
	items : [ {
		columnWidth : 0.25,
		border : false,
		layout : 'form',
		items : [ TPLevel ]
	}, {
		columnWidth : 0.45,
		border : false,
		layout : 'form',
		labelWidth : 20,
		items : [ wdmPhysicFieldset, {
			xtype : 'checkbox',
			boxLabel : '最大、最小值',
			id : 'WDMMaxMin',
			inputValue : 1
		} ]
	}, {
		columnWidth : 0.3,
		border : false,
		layout : 'form',
		items : [ wdmNumbericFieldset ]
	} ]
};

// =====================函数=====================
var getPmStdIndex = {
	getWdmPmStdIndex : function() {
//		var pmStdIndex = {
//			51 : [ 'TPL_CUR', 'TPL_MAX', 'TPL_MIN', 'TPL_AVG' ],
//			52 : [ 'RPL_CUR', 'RPL_MAX', 'RPL_MIN', 'RPL_AVG' ],
//			54 : [ 'PCLSSNR_CUR', 'PCLSSNR_MAX', 'PCLSSNR_MIN' ],
//			55 : [ 'PCLSWL_CUR', 'PCLSWL_MAX', 'PCLSWL_MIN', 'PCLSWLO_CUR', 'PCLSWLO_MAX',
//					'PCLSWLO_MIN' ],
//			56 : [ 'PCLSOP_CUR', 'PCLSOP_MAX', 'PCLSOP_MIN' ],
//			57 : [],
//			61 : [ 'OTU_BBE', 'OTU_ES', 'OTU_SES', 'OTU_UAS', 'OTU1_BBE', 'OTU1_ES', 'OTU1_SES',
//					'OTU1_UAS', 'OTU2_BBE', 'OTU2_ES', 'OTU2_SES', 'OTU2_UAS', 'OTU3_BBE',
//					'OTU3_ES', 'OTU3_SES', 'OTU3_UAS', 'OTU5G_BBE', 'OTU5G_ES', 'OTU5G_SES',
//					'OTU5G_UAS' ],
//			62 : [ 'ODU_BBE', 'ODU_ES', 'ODU_SES', 'ODU_UAS', 'ODU1_BBE', 'ODU1_ES', 'ODU1_SES',
//					'ODU1_UAS', 'ODU2_BBE', 'ODU2_ES', 'ODU2_SES', 'ODU2_UAS', 'ODU3_BBE',
//					'ODU3_ES', 'ODU3_SES', 'ODU3_UAS', 'ODU5G_BBE', 'ODU5G_ES', 'ODU5G_SES',
//					'ODU5G_UAS' ],
//			63 : [ 'OSC_BBE', 'OSC_ES', 'OSC_SES', 'OSC_UAS' ],
//			64 : [ 'FEC_BEF_COR_ER', 'FEC_AFT_COR_ER' ],
//			65 : [ 'DSR_CV', 'DSR_ES', 'DSR_SES', 'DSR_CSES', 'DSR_UAS', 'DSR_OFS' ]
//		};
//		var pmStdIndexWithoutUpperAndLower = {
//			51 : [ 'TPL_CUR', 'TPL_AVG' ],
//			52 : [ 'RPL_CUR', 'RPL_AVG' ],
//			54 : [ 'PCLSSNR_CUR' ],
//			55 : [ 'PCLSWL_CUR', 'PCLSWLO_CUR' ],
//			56 : [ 'PCLSOP_CUR' ],
//			57 : [],
//			61 : [ 'OTU_BBE', 'OTU_ES', 'OTU_SES', 'OTU_UAS', 'OTU1_BBE', 'OTU1_ES', 'OTU1_SES',
//					'OTU1_UAS', 'OTU2_BBE', 'OTU2_ES', 'OTU2_SES', 'OTU2_UAS', 'OTU3_BBE',
//					'OTU3_ES', 'OTU3_SES', 'OTU3_UAS', 'OTU5G_BBE', 'OTU5G_ES', 'OTU5G_SES',
//					'OTU5G_UAS' ],
//			62 : [ 'ODU_BBE', 'ODU_ES', 'ODU_SES', 'ODU_UAS', 'ODU1_BBE', 'ODU1_ES', 'ODU1_SES',
//					'ODU1_UAS', 'ODU2_BBE', 'ODU2_ES', 'ODU2_SES', 'ODU2_UAS', 'ODU3_BBE',
//					'ODU3_ES', 'ODU3_SES', 'ODU3_UAS', 'ODU5G_BBE', 'ODU5G_ES', 'ODU5G_SES',
//					'ODU5G_UAS' ],
//			63 : [ 'OSC_BBE', 'OSC_ES', 'OSC_SES', 'OSC_UAS' ],
//			64 : [ 'FEC_BEF_COR_ER', 'FEC_AFT_COR_ER' ],
//			65 : [ 'DSR_CV', 'DSR_ES', 'DSR_SES', 'DSR_CSES', 'DSR_UAS', 'DSR_OFS' ]
//		};
		return function() {
			var paramenter = new Array();
//			var physical = Ext.getCmp('WDMMaxMin').getValue();
//			if (physical) {
				Ext.getCmp('wdmPhysicFieldsetA').items.each(function(item) {
					if (item.getValue()) {
						paramenter.push(item.inputValue);
					}
				});
				Ext.getCmp('wdmPhysicFieldsetB').items.each(function(item) {
					if (item.getValue()) {
						paramenter.push(item.inputValue);
					}
				});
				Ext.getCmp('wdmNumbericFieldset').items.each(function(item) {
					if (item.getValue()) {
						paramenter.push(item.inputValue);
					}
				});
//			} else {
//				Ext.getCmp('wdmPhysicFieldsetA').items.each(function(item) {
//					if (item.getValue()) {
//						paramenter.push(item.inputValue);
//					}
//				});
//				Ext.getCmp('wdmPhysicFieldsetB').items.each(function(item) {
//					if (item.getValue()) {
//						paramenter.push(item.inputValue);
//					}
//				});
//				Ext.getCmp('wdmNumbericFieldset').items.each(function(item) {
//					if (item.getValue()) {
//						paramenter.push(item.inputValue);
//					}
//				});
//			}

			return paramenter;
		};
	}(),
	getRate : function() {
		var paramenter = new Array();
		if (Ext.getCmp('WDMTPLevelOther').getValue()) {
			paramenter.push("not_in");
			Ext.getCmp('TPLevel').items.each(function(item) {
				if (!item.getValue() && item.inputValue == 'OTS&OMS') {
					paramenter.push('OTS&OMS');
					paramenter.push('OTS');
					paramenter.push('OMS');
				}
				if (!item.getValue() && item.inputValue != 'other' && item.inputValue != 'OTS&OMS') {
					// 添加所有未选中的项
					paramenter.push(item.inputValue);
				}
			});
		} else {
			paramenter.push("in");
			Ext.getCmp('TPLevel').items.each(function(item) {
				if (item.getValue() && item.inputValue == 'OTS&OMS') {
					paramenter.push('OTS&OMS');
					paramenter.push('OTS');
					paramenter.push('OMS');
				}
				if (item.getValue() && item.inputValue != 'other' && item.inputValue != 'OTS&OMS') {
					paramenter.push(item.inputValue);
				}
			});
		}
		return paramenter;
	}
};
