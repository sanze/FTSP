// ************************* SDH设置 ****************************
var TPLevel = {
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	title : 'TP等级',
	height : 170,
	items : {
		id : 'TPLevel',
		xtype : 'checkboxgroup',
		columns : 1,
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
	}
};

var sdhPhysicFieldset = {
	id : 'sdhPhysicFieldset',
	xtype : 'fieldset',
	anchor : '95%',
	height : 143,
	labelWidth : 10,
	title : 'SDH物理量',
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
		boxLabel : '工作温度',
		inputValue : 3
	}, {
		xtype : 'checkbox',
		checked : true,
		boxLabel : '偏置电流',
		inputValue : 4
	}, {
		xtype : 'checkbox',
		id : 'sdhPhyOther',
		checked : false,
		boxLabel : '其他',
		inputValue : 16
	} ]
};

var sdhNumbericFieldset = {
	id : 'sdhNumbericFieldset',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	height : 165,
	title : 'SDH计数值',
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
		checked : false,
		boxLabel : 'VC4通道误码(B3)',
		inputValue : 7
	}, {
		xtype : 'checkbox',
		checked : false,
		boxLabel : 'VC3/VC12通道误码(B3/V5)',
		inputValue : 8
	}, {
		xtype : 'checkbox',
		checked : false,
		boxLabel : '以太网性能',
		inputValue : 18
	}, {
		xtype : 'checkbox',
		checked : false,
		id : 'sdhNumOther',
		boxLabel : '其他',
		inputValue : 17
	} ]
};

var SDH = {
	id : 'SDH',
	layout : 'column',
	border : false,
	items : [ {
		columnWidth : 0.25,
		border : false,
		layout : 'form',
		items : [ TPLevel ]
	}, {
		columnWidth : 0.3,
		border : false,
		layout : 'form',
		labelWidth : 20,
		items : [ sdhPhysicFieldset, {
			xtype : 'checkbox',
			boxLabel : '最大、最小值',
			id : 'SDHMaxMin',
			inputValue : 1
		} ]
	}, {
		columnWidth : 0.45,
		border : false,
		layout : 'form',
		items : [ sdhNumbericFieldset ]
	} ]
}

// =====================函数=====================
var getPmStdIndex = {
	getSdhPmStdIndex : function() {
//		var pmStdIndex = {
//			21 : [ 'TPL_CUR', 'TPL_MAX', 'TPL_MIN' ],
//			22 : [ 'RPL_CUR', 'RPL_MAX', 'RPL_MIN' ],
//			23 : [],
//			31 : [ 'RS_BBE', 'RS_ES', 'RS_SES', 'RS_CSES', 'RS_UAS', 'RS_OFS' ],
//			32 : [ 'MS_BBE', 'MS_ES', 'MS_SES', 'MS_CSES', 'MS_UAS' ],
//			33 : [ 'VC4_BBE', 'VC4_ES', 'VC4_SES', 'VC4_CSES', 'VC4_UAS' ],
//			34 : [ 'VC3_BBE', 'VC3_ES', 'VC3_SES', 'VC3_CSES', 'VC3_UAS', 'VC12_BBE', 'VC12_ES',
//					'VC12_SES', 'VC12_CSES', 'VC12_UAS' ],
//			35 : [ 'E4_BBE', 'E4_ES', 'E4_SES', 'E4_UAS', 'E3_BBE', 'E3_ES', 'E3_SES', 'E3_UAS',
//			       'E1_BBE', 'E1_ES', 'E1_SES', 'E1_UAS' ]
//		};
//		var pmStdIndexWithoutUpperAndLower = {
//			21 : [ 'TPL_CUR' ],
//			22 : [ 'RPL_CUR' ],
//			23 : [],
//			31 : [ 'RS_BBE', 'RS_ES', 'RS_SES', 'RS_CSES', 'RS_UAS', 'RS_OFS' ],
//			32 : [ 'MS_BBE', 'MS_ES', 'MS_SES', 'MS_CSES', 'MS_UAS' ],
//			33 : [ 'VC4_BBE', 'VC4_ES', 'VC4_SES', 'VC4_CSES', 'VC4_UAS' ],
//			34 : [ 'VC3_BBE', 'VC3_ES', 'VC3_SES', 'VC3_CSES', 'VC3_UAS', 'VC12_BBE', 'VC12_ES',
//					'VC12_SES', 'VC12_CSES', 'VC12_UAS' ],
//			35 : [ 'E4_BBE', 'E4_ES', 'E4_SES', 'E4_UAS', 'E3_BBE', 'E3_ES', 'E3_SES', 'E3_UAS',
//			       'E1_BBE', 'E1_ES', 'E1_SES', 'E1_UAS' ]
//		};
		return function() {
			var paramenter = new Array();
//			var physical = Ext.getCmp('SDHMaxMin').getValue();
//			if (physical) {
				Ext.getCmp('sdhPhysicFieldset').items.each(function(item) {
					if (item.getValue()) {
						paramenter.push(item.inputValue);
					}
				});
				Ext.getCmp('sdhNumbericFieldset').items.each(function(item) {
					if (item.getValue()) {
						paramenter.push(item.inputValue);
					}
				});
//			} else {
//				Ext.getCmp('sdhPhysicFieldset').items.each(function(item) {
//					if (item.getValue()) {
//						paramenter.push(item.inputValue);
//					}
//				});
//				Ext.getCmp('sdhNumbericFieldset').items.each(function(item) {
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
		if (Ext.getCmp('SDHTPLevelOther').getValue()) {
			paramenter.push("not_in");
			Ext.getCmp('TPLevel').items.each(function(item) {
				if (!item.getValue() && item.inputValue != 'other') {
					// 添加所有未选中的项
					paramenter.push(item.inputValue);
				}
			});
		} else {
			paramenter.push("in");
			Ext.getCmp('TPLevel').items.each(function(item) {
				if (item.getValue() && item.inputValue != 'other') {
					paramenter.push(item.inputValue);
				}
			});
		}
		return paramenter;
	}
};
