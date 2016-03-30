
var SDHTPLevel = {
	id : 'SDHTPLevel',
	xtype : 'checkboxgroup',
	columns : 1,
	// anchor:'90%',
	items : [ {
		checked : true,
		boxLabel : 'STM1',
		inputValue : 'STM-1'
	}, {
		checked : true,
		boxLabel : 'STM4',
		inputValue : 'STM-4'
	}, {
		checked : true,
		boxLabel : 'STM16',
		inputValue : 'STM-16'
	}, {
		checked : true,
		boxLabel : 'STM64',
		inputValue : 'STM-64'
	}, {
		checked : true,
		boxLabel : 'STM256',
		inputValue : 'STM-256'
	}, {
		checked : true,
		boxLabel : '其他',
		id : 'SDHTPLevelOther',
		inputValue : 'other'
	} ]
};
var SDHTP = {
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	title : 'TP等级',
	height : 200,
	items : SDHTPLevel
};


var SDHPhysical = {
	id : 'SDHPhysical',
	xtype : 'checkboxgroup',
	columns : 1,
	// anchor:'90%',
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
		id : 'sdhPhyOther',
		checked : true,
		boxLabel : '其他',
		inputValue : 16
	} ]
};

var SDHPhysicalField = {
	id : 'SDHPhysicalField',
	xtype : 'fieldset',
	anchor : '95%',
	height : 173,
	labelWidth : 10,
	title : 'SDH物理量',
	items : SDHPhysical
};

var SDHNumberic = {
		id : 'SDHNumberic',
		xtype : 'checkboxgroup',
		columns : 1,
		// anchor:'90%',
		items : [ {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '再生段误码(B1)',
			inputValue : 5
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '复用段误码(B2)',
			inputValue : 6
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'VC4通道误码(B3)',
			inputValue : 7
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'VC3/VC12通道误码(B3/V5)',
			inputValue : 8
		}, {
			xtype : 'checkbox',
			checked : true,
			id : 'sdhNumOther',
			boxLabel : '其他',
			inputValue : 17
		} ]
	};

var SDHNumbericField = {
	id : 'SDHNumbericField',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	height : 200,
	title : 'SDH计数值',
	items : SDHNumberic
};

var SDH = {
	id : 'SDH',
	xtype : 'fieldset',
	title : 'SDH性能设置',
	layout : 'column',
	anchor : '95%',
	labelWidth : 10,
	items : [ {
		columnWidth : 0.25,
		border : false,
		layout : 'form',
		items : [ SDHTP ]
	}, {
		columnWidth : 0.25,
		border : false,
		layout : 'form',
		labelWidth : 20,
		items : [ SDHPhysicalField, {
			xtype : 'checkbox',
			boxLabel : '最大、最小值',
			id : 'SDHMaxMin',
			inputValue : 1
		} ]
	}, {
		columnWidth : 0.5,
		border : false,
		layout : 'form',
		items : [ SDHNumbericField ]
	} ]
};

//统一高度
var h = 105;
var WDMTPLevel = {
	id : 'WDMTPLevel',
	xtype : 'checkboxgroup',
	columns : 2,
	items : [ {
		checked : true,
		id:"OTS&OMS",
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
};
var WDMTP = {
	xtype : 'fieldset',
	title : 'TP等级',
	height : h,
	anchor : '95%',
	labelWidth : 10,
	items : WDMTPLevel
};

var WDMPhysical = {
		id : 'WDMPhysical',
		xtype : 'checkboxgroup',
		columns : 2,
		// anchor:'90%',
		items :  [ {
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
			id : 'wdmPhyOther',
			boxLabel : '其他',
			inputValue : 16
		} ]
	};

var WDMPhysicalField = {
	id : 'WDMPhysicalField',
	xtype : 'fieldset',
	labelWidth : 10,
	height : h,
	anchor : '95%',
	title : 'WDM物理量',
	items :WDMPhysical
};

var WDMNumberic = {
		id : 'WDMNumberic',
		xtype : 'checkboxgroup',
		columns : 2,
		// anchor:'90%',
		items :  [ {
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
			checked : true,
			boxLabel : 'ODU误码',
			inputValue : 15
		}, {
			xtype : 'checkbox',
			boxLabel : '其他',
			checked : true,
			id : 'wdmNumOther',
			inputValue : 17
		} ]
	};

var WDMNumbericField = {
	id : 'WDMNumbericField',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	height : h,
	title : 'WDM计数值',
	items : WDMNumberic
};
var WDM = {
	id : 'WDM',
	xtype : 'fieldset',
	title : 'WDM性能设置',
	layout : 'column',
	items : [ {
		columnWidth : 0.33,
		border : false,
		layout : 'form',
		items : [ WDMTP ]
	}, {
		columnWidth : 0.33,
		border : false,
		layout : 'form',
		labelWidth : 20,
		items : [ WDMPhysicalField ]
	}, {
		columnWidth : 0.33,
		border : false,
		layout : 'form',
		items : [ WDMNumbericField ]
	} ]
};

// 配套方法
var getPmChecked = {
//		getSdhPhysical : function() {
//			var pmStdIndex = {
//				21 : [ 'TPL_CUR', 'TPL_MAX', 'TPL_MIN' ],
//				22 : [ 'RPL_CUR', 'RPL_MAX', 'RPL_MIN' ],
//				23 : []
//			};
//			var pmStdIndexWithoutUpperAndLower = {
//				21 : [ 'TPL_CUR' ],
//				22 : [ 'RPL_CUR' ],
//				23 : []
//			};
//			return function() {
//				var SdhParamenter = new Array();
//				var SdhPhyMaxMin = Ext.getCmp('SDHMaxMin').getValue();
//				if (SdhPhyMaxMin) {
//					Ext.getCmp('SDHPhysical').items.each(function(item) {
//							if (item.getValue()) {
//								SdhParamenter.push.apply(SdhParamenter,
//										pmStdIndex[item.inputValue]);
//							}
//					});
//				} else {
//					Ext.getCmp('SDHPhysical').items
//							.each(function(item) {
//									if (item.getValue()) {
//										SdhParamenter.push
//												.apply(
//														SdhParamenter,
//														pmStdIndexWithoutUpperAndLower[item.inputValue]);
//									}
//							});
//				}
//				return SdhParamenter;
//			};
//		}(),
//		getSdhNumeric : function() {
//			var pmStdIndex = {
//				31 : [ 'RS_BBE', 'RS_ES', 'RS_SES', 'RS_CSES', 'RS_UAS', 'RS_OFS' ],
//				32 : [ 'MS_BBE', 'MS_ES', 'MS_SES', 'MS_CSES', 'MS_UAS' ],
//				33 : [ 'VC4_BBE', 'VC4_ES', 'VC4_SES', 'VC4_CSES', 'VC4_UAS' ],
//				34 : [ 'VC3_BBE', 'VC3_ES', 'VC3_SES', 'VC3_CSES', 'VC3_UAS',
//						'VC12_BBE', 'VC12_ES', 'VC12_SES', 'VC12_CSES', 'VC12_UAS' ],
//				35 : []
//			};
//			return function() {
//				var SdhParamenter = new Array();
//				Ext.getCmp('SDHNumberic').items.each(function(item) {
//						if (item.getValue()) {
//							SdhParamenter.push.apply(SdhParamenter,
//									pmStdIndex[item.inputValue]);
//						}
//				});
//				return SdhParamenter;
//			};
//		}(),
		getSdhPmStdIndex : function() {
			return function() {
				var paramenter = new Array();
				if(!!Ext.getCmp('SDHPhysical'))
					Ext.getCmp('SDHPhysical').items.each(function(item) {
						if (item.getValue()) {
							paramenter.push(item.inputValue);
						}
					});
				if(!!Ext.getCmp('SDHNumberic'))
					Ext.getCmp('SDHNumberic').items.each(function(item) {
						if (item.getValue()) {
							paramenter.push(item.inputValue);
						}
					});
				return paramenter;
			};
		}(),
		getWdmPmStdIndex : function() {
			return function() {
				var paramenter = new Array();
				if(!!Ext.getCmp('WDMPhysical'))
					Ext.getCmp('WDMPhysical').items.each(function(item) {
						if (item.getValue()) {
							paramenter.push(item.inputValue);
						}
					});
				if(!!Ext.getCmp('WDMNumberic'))
					Ext.getCmp('WDMNumberic').items.each(function(item) {
						if (item.getValue()) {
							paramenter.push(item.inputValue);
						}
					});
				return paramenter;
			};
		}(),
		getWdmTp : function() {
			var tpLvl = new Array();
			var other = Ext.getCmp('WDMTPLevelOther').getValue();
			Ext.getCmp('WDMTPLevel').items.each(function(item) {
				if (!other) {
					if (item.getValue() && item.inputValue == 'OTS&OMS') {
						tpLvl.push('OTS&OMS');
						tpLvl.push('OTS');
						tpLvl.push('OMS');
					}
					if (item.getValue() && item.inputValue != 'other' && item.inputValue != 'OTS&OMS') {
						tpLvl.push(item.inputValue);
					}
//					if (item.getValue()) {
//						if (item.getRawValue() != 'other')
//							tpLvl.push(item.getRawValue());
//					}
				} else {
					if (!item.getValue() && item.inputValue == 'OTS&OMS') {
						tpLvl.push('OTS&OMS');
						tpLvl.push('OTS');
						tpLvl.push('OMS');
					}
					if (!item.getValue() && item.inputValue != 'other' && item.inputValue != 'OTS&OMS') {
						// 添加所有未选中的项
						tpLvl.push(item.inputValue);
					}
//					if (!item.getValue()) {
//						if (item.getRawValue() != 'other')
//							tpLvl.push(item.getRawValue());
//					}
				}
			});
			return tpLvl;
		},
		getSdhTp : function() {
			var tpLvl = new Array();
			var other = Ext.getCmp('SDHTPLevelOther').getValue();
			Ext.getCmp('SDHTPLevel').items.each(function(item) {
				if (!other) {
					if (item.getValue()) {
						if (item.getRawValue() != 'other')
							tpLvl.push(item.getRawValue());
					}
				} else {
					if (!item.getValue()) {
						if (item.getRawValue() != 'other')
							tpLvl.push(item.getRawValue());
					}
				}
			});
			return tpLvl;
		}
	};