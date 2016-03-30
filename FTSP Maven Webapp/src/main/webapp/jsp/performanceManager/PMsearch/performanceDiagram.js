
var PM_STD_INDEX_TYPE = null;
var UNKNOWN_PM = false;
Ext.override(Ext.form.CheckboxGroup, {
	getValue : function(mode) {
		var v = [];
		if (mode == 1) {
			this.items.each(function(item) {
				if (item.getValue())
					v.push(item.getRawValue());
			});
			return v;
		} else {
			this.items.each(function(item) {
				v.push(item.getValue());
			});
			return v;
		}
	}
});
/*
 * data : [ [ 0, "再生段" ], [ 1, "复用段" ], [ 2, "光监控信道" ], [ 3, "FEC误码率" ], [ 4,
 * "VC4通道" ], [ 5, "VC3通道" ], [ 6, "VC12通道" ], [ 7, "输出光功率" ], [ 8, "输入光功率" ], [
 * 9, "每信道光功率" ], [ 10, "每信道中心波长" ], [ 11, "信噪比" ], [ 12, "OTU误码" ], [13,
 * "ODU误码"]]
 */
var paramStore = new Ext.data.ArrayStore({
	idIndex : 0,
	fields : [ 'paramId', 'paramName', 'pmType' ]
});
var SDHData = [ [ 0, "再生段", 1 ], [ 1, "复用段", 1 ], [ 4, "VC4通道", 1 ], [ 5, "VC3通道", 1 ],
		[ 6, "VC12通道", 1 ], [ 7, "输出光功率", 2 ], [ 8, "输入光功率", 2 ], [ 14, "环境温度", 2 ],
		[ 15, "激光器温度", 2 ], [ 17, "激光器电流", 2 ], [18, "以太网性能"], [19, "其他"] ];
var WDMData = [ [ 2, "光监控信道", 1 ], [ 3, "FEC误码率", 1 ], [ 7, "输出光功率", 2 ], [ 8, "输入光功率", 2 ],
		[ 9, "每信道光功率", 2 ], [ 10, "每信道中心波长", 2 ], [ 11, "信噪比", 2 ], [ 12, "OTU误码", 1 ],
		[ 13, "ODU误码", 1 ], [ 14, "环境温度", 2 ], [ 15, "激光器温度", 2 ], [ 16, "激光器电流", 2 ], [19, "其他"] ];
(function() {
	if (type == 1)
		paramStore.loadData(SDHData);
	if (type == 2)
		paramStore.loadData(WDMData);
})();

var timeRangeStore = new Ext.data.ArrayStore({
	idIndex : 0,
	fields : [ 'timeRangeId', 'timeRangeName' ],
	data : [ [ 1, "前10天" ], [ 2, "前20天" ], [ 3, "前30天" ], [ 4, "后10天" ],
			[ 5, "后20天" ], [ 6, "后30天" ] ]
});
// **************************checkboxes-bottom***********************
// 再生段
var regeneratorSectionBottom = {
	id : 'regeneratorSectionBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	border : false,
	// width : 280,
	// height : 150,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'RS_BBE_Bottom',
		name : 'RS_BBE_Bottom',
		boxLabel : '再生段背景块误码',
		inputValue : 'RS_BBE'
	}, {
		xtype : 'checkbox',
		id : 'RS_ES_Bottom',
		name : 'RS_ES_Bottom',
		boxLabel : '再生段误码秒',
		inputValue : 'RS_ES'
	}, {
		xtype : 'checkbox',
		id : 'RS_SES_Bottom',
		name : 'RS_SES_Bottom',
		boxLabel : '再生段严重误码秒',
		inputValue : 'RS_SES'
	}, {
		xtype : 'checkbox',
		id : 'RS_UAS_Bottom',
		name : 'RS_UAS_Bottom',
		boxLabel : '再生段不可用秒',
		inputValue : 'RS_UAS'
	}, {
		xtype : 'checkbox',
		id : 'RS_CSES_Bottom',
		name : 'RS_CSES_Bottom',
		boxLabel : '再生段连续严重误码秒',
		inputValue : 'RS_CSES'
	}, {
		xtype : 'checkbox',
		id : 'RS_OFS_Bottom',
		name : 'RS_OFS_Bottom',
		boxLabel : '再生段帧失步秒',
		inputValue : 'RS_OFS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// 复用段
var multiplexSectionBottom = {
	id : 'multiplexSectionBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'MS_BBE_Bottom',
		name : 'MS_BBE_Bottom',
		boxLabel : '复用段背景块误码',
		inputValue : 'MS_BBE'
	}, {
		xtype : 'checkbox',
		id : 'MS_ES_Bottom',
		name : 'MS_ES_Bottom',
		boxLabel : '复用段误码秒',
		inputValue : 'MS_ES'
	}, {
		xtype : 'checkbox',
		id : 'MS_SES_Bottom',
		name : 'MS_SES_Bottom',
		boxLabel : '复用段严重误码秒',
		inputValue : 'MS_SES'
	}, {
		xtype : 'checkbox',
		id : 'MS_UAS_Bottom',
		name : 'MS_UAS_Bottom',
		boxLabel : '复用段不可用秒',
		inputValue : 'MS_UAS'
	}, {
		xtype : 'checkbox',
		id : 'MS_CSES_Bottom',
		name : 'MS_CSES_Bottom',
		boxLabel : '复用段连续严重误码秒',
		inputValue : 'MS_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// 光监控信道
var opticalSupervisoryChannelBottom = {
	id : 'opticalSupervisoryChannelBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OSC_BBE_Bottom',
		name : 'OSC_BBE_Bottom',
		boxLabel : '光监控信道背景误码',
		inputValue : 'OSC_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OSC_ES_Bottom',
		name : 'OSC_ES_Bottom',
		boxLabel : '光监控信道误码秒',
		inputValue : 'OSC_ES'
	}, {
		xtype : 'checkbox',
		id : 'OSC_SES_Bottom',
		name : 'OSC_SES_Bottom',
		boxLabel : '光监控信道严重误码秒',
		inputValue : 'OSC_SES'
	}, {
		xtype : 'checkbox',
		id : 'OSC_UAS_Bottom',
		name : 'OSC_UAS_Bottom',
		boxLabel : '光监控信道不可用秒',
		inputValue : 'OSC_UAS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// FEC误码率
var FEC_Bottom = {
	id : 'FEC_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'FEC_BEF_COR_ER_Bottom',
		name : 'FEC_BEF_COR_ER_Bottom',
		boxLabel : 'FEC纠错前的误码率',
		inputValue : 'FEC_BEF_COR_ER'
	}, {
		xtype : 'checkbox',
		id : 'FEC_AFT_COR_ER_Bottom',
		name : 'FEC_AFT_COR_ER_Bottom',
		boxLabel : 'FEC纠错后的误码率',
		inputValue : 'FEC_AFT_COR_ER'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// VC4通道
var VC4_Bottom = {
	id : 'VC4_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'VC4_BBE_Bottom',
		name : 'VC4_BBE_Bottom',
		boxLabel : 'VC4通道背景块误码',
		inputValue : 'VC4_BBE'
	}, {
		xtype : 'checkbox',
		id : 'VC4_ES_Bottom',
		name : 'VC4_ES_Bottom',
		boxLabel : 'VC4通道误码秒',
		inputValue : 'VC4_ES'
	}, {
		xtype : 'checkbox',
		id : 'VC4_SES_Bottom',
		name : 'VC4_SES_Bottom',
		boxLabel : 'VC4通道严重误码秒',
		inputValue : 'VC4_SES'
	}, {
		xtype : 'checkbox',
		id : 'VC4_UAS_Bottom',
		name : 'VC4_UAS_Bottom',
		boxLabel : 'VC4通道不可用秒',
		inputValue : 'VC4_UAS'
	}, {
		xtype : 'checkbox',
		id : 'VC4_CSES_Bottom',
		name : 'VC4_CSES_Bottom',
		boxLabel : 'VC4通道连续严重误码秒',
		inputValue : 'VC4_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// VC3通道
var VC3_Bottom = {
	id : 'VC3_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'VC3_BBE_Bottom',
		name : 'VC3_BBE_Bottom',
		boxLabel : 'VC3通道背景块误码',
		inputValue : 'VC3_BBE'
	}, {
		xtype : 'checkbox',
		id : 'VC3_ES_Bottom',
		name : 'VC3_ES_Bottom',
		boxLabel : 'VC3通道误码秒',
		inputValue : 'VC3_ES'
	}, {
		xtype : 'checkbox',
		id : 'VC3_SES_Bottom',
		name : 'VC3_SES_Bottom',
		boxLabel : 'VC3通道严重误码秒',
		inputValue : 'VC3_SES'
	}, {
		xtype : 'checkbox',
		id : 'VC3_UAS_Bottom',
		name : 'VC3_UAS_Bottom',
		boxLabel : 'VC3通道不可用秒',
		inputValue : 'VC3_UAS'
	}, {
		xtype : 'checkbox',
		id : 'VC3_CSES_Bottom',
		name : 'VC3_CSES_Bottom',
		boxLabel : 'VC3通道连续严重误码秒',
		inputValue : 'VC3_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// VC12通道
var VC12_Bottom = {
	id : 'VC12_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'VC12_BBE_Bottom',
		name : 'VC12_BBE_Bottom',
		boxLabel : 'VC12通道背景块误码',
		inputValue : 'VC12_BBE'
	}, {
		xtype : 'checkbox',
		id : 'VC12_ES_Bottom',
		name : 'VC12_ES_Bottom',
		boxLabel : 'VC12通道误码秒',
		inputValue : 'VC12_ES'
	}, {
		xtype : 'checkbox',
		id : 'VC12_SES_Bottom',
		name : 'VC12_SES_Bottom',
		boxLabel : 'VC12通道严重误码秒',
		inputValue : 'VC12_SES'
	}, {
		xtype : 'checkbox',
		id : 'VC12_UAS_Bottom',
		name : 'VC12_UAS_Bottom',
		boxLabel : 'VC12通道不可用秒',
		inputValue : 'VC12_UAS'
	}, {
		xtype : 'checkbox',
		id : 'VC12_CSES_Bottom',
		name : 'VC12_CSES_Bottom',
		boxLabel : 'VC12通道连续严重误码秒',
		inputValue : 'VC12_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// 输出光功率
var outputLightPowerBottom = {
	id : 'outputLightPowerBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'TPL_AVG_Bottom',
		name : 'TPL_AVG_Bottom',
		boxLabel : '输出光功率平均值',
		inputValue : 'TPL_AVG'
	},{
		xtype : 'checkbox',
		id : 'TPL_CUR_Bottom',
		name : 'TPL_CUR_Bottom',
		boxLabel : '输出光功率当前值',
		inputValue : 'TPL_CUR'
	}, {
		xtype : 'checkbox',
		id : 'TPL_MAX_Bottom',
		name : 'TPL_MAX_Bottom',
		boxLabel : '输出光功率最大值',
		inputValue : 'TPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'TPL_MIN_Bottom',
		name : 'TPL_MIN_Bottom',
		boxLabel : '输出光功率最小值',
		inputValue : 'TPL_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			if (array.length > 1) {
				Ext.getCmp('max_min_bottom').setValue(false);
				Ext.getCmp('max_min_bottom').setDisabled(true);

			} else {
				Ext.getCmp('max_min_bottom').setDisabled(false);
			}
		}
	}
};

// 输入光功率
var inputLightPowerBottom = {
	id : 'inputLightPowerBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'RPL_AVG_Bottom',
		name : 'RPL_AVG_Bottom',
		boxLabel : '输入光功率平均值',
		inputValue : 'RPL_AVG'
	},{
		xtype : 'checkbox',
		id : 'RPL_CUR_Bottom',
		name : 'RPL_CUR_Bottom',
		boxLabel : '输入光功率当前值',
		inputValue : 'RPL_CUR'
	}, {
		xtype : 'checkbox',
		id : 'RPL_MAX_Bottom',
		name : 'RPL_MAX_Bottom',
		boxLabel : '输入光功率最大值',
		inputValue : 'RPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'RPL_MIN_Bottom',
		name : 'RPL_MIN_Bottom',
		boxLabel : '输入光功率最小值',
		inputValue : 'RPL_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			if (array.length > 1) {
				Ext.getCmp('max_min_bottom').setValue(false);
				Ext.getCmp('max_min_bottom').setDisabled(true);

			} else {
				Ext.getCmp('max_min_bottom').setDisabled(false);
			}
		}
	}
};

// 每信道光功率
var opticalPowerPerChanelBottom = {
	id : 'opticalPowerPerChanelBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'PCLSOP_CUR_Bottom',
		name : 'PCLSOP_CUR_Bottom',
		boxLabel : '每信道光功率当前值',
		inputValue : 'PCLSOP_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSOP_MAX_Bottom',
		name : 'PCLSOP_MAX_Bottom',
		boxLabel : '每信道光功率最大值',
		inputValue : 'PCLSOP_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSOP_MIN_Bottom',
		name : 'PCLSOP_MIN_Bottom',
		boxLabel : '每信道光功率最小值',
		inputValue : 'PCLSOP_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			if (array.length > 1) {
				Ext.getCmp('max_min_bottom').setValue(false);
				Ext.getCmp('max_min_bottom').setDisabled(true);

			} else {
				Ext.getCmp('max_min_bottom').setDisabled(false);
			}
		}
	}
};

// 每信道中心波长
var frequencyBottom = {
	id : 'frequencyBottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'PCLSWL_CUR_Bottom',
		name : 'PCLSWL_CUR_Bottom',
		boxLabel : '每信道中心波长当前值',
		inputValue : 'PCLSWL_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWL_MAX_Bottom',
		name : 'PCLSWL_MAX_Bottom',
		boxLabel : '每信道中心波长最大值',
		inputValue : 'PCLSWL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWL_MIN_Bottom',
		name : 'PCLSWL_MIN_Bottom',
		boxLabel : '每信道中心波长最小值',
		inputValue : 'PCLSWL_MIN'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWLO_CUR_Bottom',
		name : 'PCLSWLO_CUR_Bottom',
		boxLabel : '每信道中心波长偏移当前值',
		inputValue : 'PCLSWLO_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWLO_MAX_Bottom',
		name : 'PCLSWLO_MAX_Bottom',
		boxLabel : '每信道中心波长偏移最大值',
		inputValue : 'PCLSWLO_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWLO_MIN_Bottom',
		name : 'PCLSWLO_MIN_Bottom',
		boxLabel : '每信道中心波长偏移最小值',
		inputValue : 'PCLSWLO_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			var i, cnt = 0;
			for (i = 0; i < array.length; i++) {
				var box = array[i];
				var index = box.getRawValue();
				if (index == 'PCLSWLO_CUR' || index == 'PCLSWLO_MAX'
						|| index == 'PCLSWLO_MIN') {
					cnt++;
				} else {
					Ext.getCmp('max_min').setValue(false);
					Ext.getCmp('max_min').setDisabled(true);
					return;
				}
			}
			if (cnt > 1) {
				Ext.getCmp('max_min').setValue(false);
				Ext.getCmp('max_min').setDisabled(true);

			} else {
				Ext.getCmp('max_min').setDisabled(false);
			}
		}
	}
};

// 信噪比
var SNR_Bottom = {
	id : 'SNR_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'PCLSSNR_CUR_Bottom',
		name : 'PCLSSNR_CUR_Bottom',
		boxLabel : '信噪比当前值',
		inputValue : 'PCLSSNR_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSSNR_MAX_Bottom',
		name : 'PCLSSNR_MAX_Bottom',
		boxLabel : '信噪比最大值',
		inputValue : 'PCLSSNR_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSSNR_MIN_Bottom',
		name : 'PCLSSNR_MIN_Bottom',
		boxLabel : '信噪比最小值',
		inputValue : 'PCLSSNR_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// OTU
var OTU_Bottom = {
	id : 'OTU_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OTU_BBE_Bottom',
		name : 'OTU_BBE_Bottom',
		boxLabel : 'OTU的SM段背景误码块',
		inputValue : 'OTU_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU_ES_Bottom',
		name : 'OTU_ES_Bottom',
		boxLabel : 'OTU的SM段误码秒',
		inputValue : 'OTU_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU_SES_Bottom',
		name : 'OTU_SES_Bottom',
		boxLabel : 'OTU的SM段严重误码秒',
		inputValue : 'OTU_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU_UAS_Bottom',
		name : 'OTU_UAS_Bottom',
		boxLabel : 'OTU的SM段不可用秒',
		inputValue : 'OTU_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_BBE_Bottom',
		name : 'OTU1_BBE_Bottom',
		boxLabel : 'OTU1的SM段背景误码块',
		inputValue : 'OTU1_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_ES_Bottom',
		name : 'OTU1_ES_Bottom',
		boxLabel : 'OTU1的SM段误码秒',
		inputValue : 'OTU1_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_SES_Bottom',
		name : 'OTU1_SES_Bottom',
		boxLabel : 'OTU1的SM段严重误码秒',
		inputValue : 'OTU1_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_UAS_Bottom',
		name : 'OTU1_UAS_Bottom',
		boxLabel : 'OTU1的SM段不可用秒',
		inputValue : 'OTU1_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_BBE_Bottom',
		name : 'OTU2_BBE_Bottom',
		boxLabel : 'OTU2的SM段背景误码块',
		inputValue : 'OTU2_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_ES_Bottom',
		name : 'OTU2_ES_Bottom',
		boxLabel : 'OTU2的SM段误码秒',
		inputValue : 'OTU2_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_SES_Bottom',
		name : 'OTU2_SES_Bottom',
		boxLabel : 'OTU2的SM段严重误码秒',
		inputValue : 'OTU2_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_UAS_Bottom',
		name : 'OTU2_UAS_Bottom',
		boxLabel : 'OTU2的SM段不可用秒',
		inputValue : 'OTU2_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_BBE_Bottom',
		name : 'OTU3_BBE_Bottom',
		boxLabel : 'OTU3的SM段背景误码块',
		inputValue : 'OTU3_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_ES_Bottom',
		name : 'OTU3_ES_Bottom',
		boxLabel : 'OTU3的SM段误码秒',
		inputValue : 'OTU3_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_SES_Bottom',
		name : 'OTU3_SES_Bottom',
		boxLabel : 'OTU3的SM段严重误码秒',
		inputValue : 'OTU3_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_UAS_Bottom',
		name : 'OTU3_UAS_Bottom',
		boxLabel : 'OTU3的SM段不可用秒',
		inputValue : 'OTU3_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_BBE_Bottom',
		name : 'OTU5G_BBE_Bottom',
		boxLabel : 'OTU5G的SM段背景误码块',
		inputValue : 'OTU5G_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_ES_Bottom',
		name : 'OTU5G_ES_Bottom',
		boxLabel : 'OTU5G的SM段误码秒',
		inputValue : 'OTU5G_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_SES_Bottom',
		name : 'OTU5G_SES_Bottom',
		boxLabel : 'OTU5G的SM段严重误码秒',
		inputValue : 'OTU5G_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_UAS_Bottom',
		name : 'OTU5G_UAS_Bottom',
		boxLabel : 'OTU5G的SM段不可用秒',
		inputValue : 'OTU5G_UAS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// ODU
var ODU_Bottom = {
	id : 'ODU_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ODU_BBE_Bottom',
		name : 'ODU_BBE_Bottom',
		boxLabel : 'ODU的PM段背景误码块',
		inputValue : 'ODU_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU_ES_Bottom',
		name : 'ODU_ES_Bottom',
		boxLabel : 'ODU的PM段误码秒',
		inputValue : 'ODU_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU_SES_Bottom',
		name : 'ODU_SES_Bottom',
		boxLabel : 'ODU的PM段严重误码秒',
		inputValue : 'ODU_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU_UAS_Bottom',
		name : 'ODU_UAS_Bottom',
		boxLabel : 'ODU的PM段不可用秒',
		inputValue : 'ODU_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_BBE_Bottom',
		name : 'ODU1_BBE_Bottom',
		boxLabel : 'ODU1的PM段背景误码块',
		inputValue : 'ODU1_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_ES_Bottom',
		name : 'ODU1_ES_Bottom',
		boxLabel : 'ODU1的PM段误码秒',
		inputValue : 'ODU1_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_SES_Bottom',
		name : 'ODU1_SES_Bottom',
		boxLabel : 'ODU1的PM段严重误码秒',
		inputValue : 'ODU1_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_UAS_Bottom',
		name : 'ODU1_UAS_Bottom',
		boxLabel : 'ODU1的PM段不可用秒',
		inputValue : 'ODU1_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_BBE_Bottom',
		name : 'ODU2_BBE_Bottom',
		boxLabel : 'ODU2的PM段背景误码块',
		inputValue : 'ODU2_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_ES_Bottom',
		name : 'ODU2_ES_Bottom',
		boxLabel : 'ODU2的PM段误码秒',
		inputValue : 'ODU2_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_SES_Bottom',
		name : 'ODU2_SES_Bottom',
		boxLabel : 'ODU2的PM段严重误码秒',
		inputValue : 'ODU2_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_UAS_Bottom',
		name : 'ODU2_UAS_Bottom',
		boxLabel : 'ODU2的PM段不可用秒',
		inputValue : 'ODU2_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_BBE_Bottom',
		name : 'ODU3_BBE_Bottom',
		boxLabel : 'ODU3的PM段背景误码块',
		inputValue : 'ODU3_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_ES_Bottom',
		name : 'ODU3_ES_Bottom',
		boxLabel : 'ODU3的PM段误码秒',
		inputValue : 'ODU3_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_SES_Bottom',
		name : 'ODU3_SES_Bottom',
		boxLabel : 'ODU3的PM段严重误码秒',
		inputValue : 'ODU3_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_UAS_Bottom',
		name : 'ODU3_UAS_Bottom',
		boxLabel : 'ODU3的PM段不可用秒',
		inputValue : 'ODU3_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_BBE_Bottom',
		name : 'ODU5G_BBE_Bottom',
		boxLabel : 'ODU5G的PM段背景误码块',
		inputValue : 'ODU5G_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_ES_Bottom',
		name : 'ODU5G_ES_Bottom',
		boxLabel : 'ODU5G的PM段误码秒',
		inputValue : 'ODU5G_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_SES_Bottom',
		name : 'ODU5G_SES_Bottom',
		boxLabel : 'ODU5G的PM段严重误码秒',
		inputValue : 'ODU5G_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_UAS_Bottom',
		name : 'ODU5G_UAS_Bottom',
		boxLabel : 'ODU5G的PM段不可用秒',
		inputValue : 'ODU5G_UAS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

//EnvTmp
var EnvTmp_Bottom = {
	id : 'EnvTmp_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ENV_TMP_CUR_Bottom',
		name : 'ENV_TMP_CUR_Bottom',
		boxLabel : '环境温度当前值',
		inputValue : 'ENV_TMP_CUR'
	}, {
		xtype : 'checkbox',
		id : 'ENV_TMP_MAX_Bottom',
		name : 'ENV_TMP_MAX_Bottom',
		boxLabel : '环境温度最大值',
		inputValue : 'ENV_TMP_MAX'
	}, {
		xtype : 'checkbox',
		id : 'ENV_TMP_MIN_Bottom',
		name : 'ENV_TMP_MIN_Bottom',
		boxLabel : '环境温度最小值',
		inputValue : 'ENV_TMP_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

//OPT_LTEMP_Bottom
var OPT_LTEMP_Bottom = {
	id : 'OPT_LTEMP_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OPT_LTEMP_CUR_Bottom',
		name : 'OPT_LTEMP_CUR_Bottom',
		boxLabel : '激光器工作温度当前值',
		inputValue : 'OPT_LTEMP_CUR'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LTEMP_MAX_Bottom',
		name : 'OPT_LTEMP_MAX_Bottom',
		boxLabel : '激光器工作温度最大值',
		inputValue : 'OPT_LTEMP_MAX'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LTEMP_MIN_Bottom',
		name : 'OPT_LTEMP_MIN_Bottom',
		boxLabel : '激光器工作温度最小值',
		inputValue : 'OPT_LTEMP_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

//LASER
var LASER_Bottom = {
	id : 'LASER_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_CUR_Bottom',
		name : 'OPT_LBIAS_CUR_Bottom',
		boxLabel : '激光器偏置电流当前值',
		inputValue : 'OPT_LBIAS_CUR'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MAX_Bottom',
		name : 'OPT_LBIAS_MAX_Bottom',
		boxLabel : '激光器偏置电流最大值',
		inputValue : 'OPT_LBIAS_MAX'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MIN_Bottom',
		name : 'OPT_LBIAS_MIN_Bottom',
		boxLabel : '激光器偏置电流最小值',
		inputValue : 'OPT_LBIAS_MIN'
	}, {
		xtype : 'checkbox',
		id : 'LWC_CUR_Bottom',
		name : 'LWC_CUR_Bottom',
		boxLabel : '激光器工作电流当前值',
		inputValue : 'LWC_CUR'
	}, {
		xtype : 'checkbox',
		id : 'LWC_MAX_Bottom',
		name : 'LWC_MAX_Bottom',
		boxLabel : '激光器工作电流最大值',
		inputValue : 'LWC_MAX'
	}, {
		xtype : 'checkbox',
		id : 'LWC_MIN_Bottom',
		name : 'LWC_MIN_Bottom',
		boxLabel : '激光器工作电流最小值',
		inputValue : 'LWC_MIN'
	}, {
		xtype : 'checkbox',
		id : 'LCC_CUR_Bottom',
		name : 'LCC_CUR_Bottom',
		boxLabel : '激光器制冷电流当前值',
		inputValue : 'LCC_CUR'
	}, {
		xtype : 'checkbox',
		id : 'LCC_MAX_Bottom',
		name : 'LCC_MAX_Bottom',
		boxLabel : '激光器制冷电流最大值',
		inputValue : 'LCC_MAX'
	}, {
		xtype : 'checkbox',
		id : 'LCC_MIN_Bottom',
		name : 'LCC_MIN_Bottom',
		boxLabel : '激光器制冷电流最小值',
		inputValue : 'LCC_MIN'
	}, {
		xtype : 'checkbox',
		id : 'LBC_CUR_Bottom',
		name : 'LBC_CUR_Bottom',
		boxLabel : '激光器背光检测电流当前值',
		inputValue : 'LBC_CUR'
	}, {
		xtype : 'checkbox',
		id : 'LBC_MAX_Bottom',
		name : 'LBC_MAX_Bottom',
		boxLabel : '激光器背光检测电流最大值',
		inputValue : 'LBC_MAX'
	}, {
		xtype : 'checkbox',
		id : 'LBC_MIN_Bottom',
		name : 'LBC_MIN_Bottom',
		boxLabel : '激光器背光检测电流最小值',
		inputValue : 'LBC_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

//LASER_SDH_Bottom
var LASER_SDH_Bottom = {
	id : 'LASER_SDH_Bottom',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_CUR_Bottom',
		name : 'OPT_LBIAS_CUR_Bottom',
		boxLabel : '激光器偏置电流当前值',
		inputValue : 'OPT_LBIAS_CUR'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MAX_Bottom',
		name : 'OPT_LBIAS_MAX_Bottom',
		boxLabel : '激光器偏置电流最大值',
		inputValue : 'OPT_LBIAS_MAX'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MIN_Bottom',
		name : 'OPT_LBIAS_MIN_Bottom',
		boxLabel : '激光器偏置电流最小值',
		inputValue : 'OPT_LBIAS_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min_bottom').setDisabled(false);
			// }
		}
	}
};

// **************************checkboxes-top***********************
// 再生段
var regeneratorSection = {
	id : 'regeneratorSection',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout:'form',
	border : false,
	// width : 280,
	// height : 150,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'RS_BBE',
		name : 'RS_BBE',
		boxLabel : '再生段背景块误码',
		inputValue : 'RS_BBE'
	}, {
		xtype : 'checkbox',
		id : 'RS_ES',
		name : 'RS_ES',
		boxLabel : '再生段误码秒',
		inputValue : 'RS_ES'
	}, {
		xtype : 'checkbox',
		id : 'RS_SES',
		name : 'RS_SES',
		boxLabel : '再生段严重误码秒',
		inputValue : 'RS_SES'
	}, {
		xtype : 'checkbox',
		id : 'RS_UAS',
		name : 'RS_UAS',
		boxLabel : '再生段不可用秒',
		inputValue : 'RS_UAS'
	}, {
		xtype : 'checkbox',
		id : 'RS_CSES',
		name : 'RS_CSES',
		boxLabel : '再生段连续严重误码秒',
		inputValue : 'RS_CSES'
	}, {
		xtype : 'checkbox',
		id : 'RS_OFS',
		name : 'RS_OFS',
		boxLabel : '再生段帧失步秒',
		inputValue : 'RS_OFS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// 复用段
var multiplexSection = {
	id : 'multiplexSection',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'MS_BBE',
		name : 'MS_BBE',
		boxLabel : '复用段背景块误码',
		inputValue : 'MS_BBE'
	}, {
		xtype : 'checkbox',
		id : 'MS_ES',
		name : 'MS_ES',
		boxLabel : '复用段误码秒',
		inputValue : 'MS_ES'
	}, {
		xtype : 'checkbox',
		id : 'MS_SES',
		name : 'MS_SES',
		boxLabel : '复用段严重误码秒',
		inputValue : 'MS_SES'
	}, {
		xtype : 'checkbox',
		id : 'MS_UAS',
		name : 'MS_UAS',
		boxLabel : '复用段不可用秒',
		inputValue : 'MS_UAS'
	}, {
		xtype : 'checkbox',
		id : 'MS_CSES',
		name : 'MS_CSES',
		boxLabel : '复用段连续严重误码秒',
		inputValue : 'MS_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// 光监控信道
var opticalSupervisoryChannel = {
	id : 'opticalSupervisoryChannel',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OSC_BBE',
		name : 'OSC_BBE',
		boxLabel : '光监控信道背景误码',
		inputValue : 'OSC_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OSC_ES',
		name : 'OSC_ES',
		boxLabel : '光监控信道误码秒',
		inputValue : 'OSC_ES'
	}, {
		xtype : 'checkbox',
		id : 'OSC_SES',
		name : 'OSC_SES',
		boxLabel : '光监控信道严重误码秒',
		inputValue : 'OSC_SES'
	}, {
		xtype : 'checkbox',
		id : 'OSC_UAS',
		name : 'OSC_UAS',
		boxLabel : '光监控信道不可用秒',
		inputValue : 'OSC_UAS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// FEC误码率
var FEC = {
	id : 'FEC',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'FEC_BEF_COR_ER',
		name : 'FEC_BEF_COR_ER',
		boxLabel : 'FEC纠错前的误码率',
		inputValue : 'FEC_BEF_COR_ER'
	}, {
		xtype : 'checkbox',
		id : 'FEC_AFT_COR_ER',
		name : 'FEC_AFT_COR_ER',
		boxLabel : 'FEC纠错后的误码率',
		inputValue : 'FEC_AFT_COR_ER'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// VC4通道
var VC4 = {
	id : 'VC4',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// autoScroll:true,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'VC4_BBE',
		name : 'VC4_BBE',
		boxLabel : 'VC4通道背景块误码',
		inputValue : 'VC4_BBE'
	}, {
		xtype : 'checkbox',
		id : 'VC4_ES',
		name : 'VC4_ES',
		boxLabel : 'VC4通道误码秒',
		inputValue : 'VC4_ES'
	}, {
		xtype : 'checkbox',
		id : 'VC4_SES',
		name : 'VC4_SES',
		boxLabel : 'VC4通道严重误码秒',
		inputValue : 'VC4_SES'
	}, {
		xtype : 'checkbox',
		id : 'VC4_UAS',
		name : 'VC4_UAS',
		boxLabel : 'VC4通道不可用秒',
		inputValue : 'VC4_UAS'
	}, {
		xtype : 'checkbox',
		id : 'VC4_CSES',
		name : 'VC4_CSES',
		boxLabel : 'VC4通道连续严重误码秒',
		inputValue : 'VC4_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// VC3通道
var VC3 = {
	id : 'VC3',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'VC3_BBE',
		name : 'VC3_BBE',
		boxLabel : 'VC3通道背景块误码',
		inputValue : 'VC3_BBE'
	}, {
		xtype : 'checkbox',
		id : 'VC3_ES',
		name : 'VC3_ES',
		boxLabel : 'VC3通道误码秒',
		inputValue : 'VC3_ES'
	}, {
		xtype : 'checkbox',
		id : 'VC3_SES',
		name : 'VC3_SES',
		boxLabel : 'VC3通道严重误码秒',
		inputValue : 'VC3_SES'
	}, {
		xtype : 'checkbox',
		id : 'VC3_UAS',
		name : 'VC3_UAS',
		boxLabel : 'VC3通道不可用秒',
		inputValue : 'VC3_UAS'
	}, {
		xtype : 'checkbox',
		id : 'VC3_CSES',
		name : 'VC3_CSES',
		boxLabel : 'VC3通道连续严重误码秒',
		inputValue : 'VC3_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// VC12通道
var VC12 = {
	id : 'VC12',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'VC12_BBE',
		name : 'VC12_BBE',
		boxLabel : 'VC12通道背景块误码',
		inputValue : 'VC12_BBE'
	}, {
		xtype : 'checkbox',
		id : 'VC12_ES',
		name : 'VC12_ES',
		boxLabel : 'VC12通道误码秒',
		inputValue : 'VC12_ES'
	}, {
		xtype : 'checkbox',
		id : 'VC12_SES',
		name : 'VC12_SES',
		boxLabel : 'VC12通道严重误码秒',
		inputValue : 'VC12_SES'
	}, {
		xtype : 'checkbox',
		id : 'VC12_UAS',
		name : 'VC12_UAS',
		boxLabel : 'VC12通道不可用秒',
		inputValue : 'VC12_UAS'
	}, {
		xtype : 'checkbox',
		id : 'VC12_CSES',
		name : 'VC12_CSES',
		boxLabel : 'VC12通道连续严重误码秒',
		inputValue : 'VC12_CSES'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// 输出光功率
var outputLightPower = {
	id : 'outputLightPower',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'TPL_AVG',
		name : 'TPL_AVG',
		boxLabel : '输出光功率平均值',
		inputValue : 'TPL_AVG'
	},{
		xtype : 'checkbox',
		id : 'TPL_CUR',
		name : 'TPL_CUR',
		boxLabel : '输出光功率当前值',
		inputValue : 'TPL_CUR'
	}, {
		xtype : 'checkbox',
		id : 'TPL_MAX',
		name : 'TPL_MAX',
		boxLabel : '输出光功率最大值',
		inputValue : 'TPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'TPL_MIN',
		name : 'TPL_MIN',
		boxLabel : '输出光功率最小值',
		inputValue : 'TPL_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			if (array.length > 1) {
				Ext.getCmp('max_min').setValue(false);
				Ext.getCmp('max_min').setDisabled(true);

			} else {
				Ext.getCmp('max_min').setDisabled(false);
			}
		}
	}
};

// 输入光功率
var inputLightPower = {
	id : 'inputLightPower',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'RPL_AVG',
		name : 'RPL_AVG',
		boxLabel : '输入光功率平均值',
		inputValue : 'RPL_AVG'
	},{
		xtype : 'checkbox',
		id : 'RPL_CUR',
		name : 'RPL_CUR',
		boxLabel : '输入光功率当前值',
		inputValue : 'RPL_CUR'
	}, {
		xtype : 'checkbox',
		id : 'RPL_MAX',
		name : 'RPL_MAX',
		boxLabel : '输入光功率最大值',
		inputValue : 'RPL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'RPL_MIN',
		name : 'RPL_MIN',
		boxLabel : '输入光功率最小值',
		inputValue : 'RPL_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			if (array.length > 1) {
				Ext.getCmp('max_min').setValue(false);
				Ext.getCmp('max_min').setDisabled(true);

			} else {
				Ext.getCmp('max_min').setDisabled(false);
			}
		}
	}
};

// 每信道光功率
var opticalPowerPerChanel = {
	id : 'opticalPowerPerChanel',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'PCLSOP_CUR',
		name : 'PCLSOP_CUR',
		boxLabel : '每信道光功率当前值',
		inputValue : 'PCLSOP_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSOP_MAX',
		name : 'PCLSOP_MAX',
		boxLabel : '每信道光功率最大值',
		inputValue : 'PCLSOP_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSOP_MIN',
		name : 'PCLSOP_MIN',
		boxLabel : '每信道光功率最小值',
		inputValue : 'PCLSOP_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			if (array.length > 1) {
				Ext.getCmp('max_min').setValue(false);
				Ext.getCmp('max_min').setDisabled(true);

			} else {
				Ext.getCmp('max_min').setDisabled(false);
			}
		}
	}
};

// 每信道中心波长
var frequency = {
	id : 'frequency',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'PCLSWL_CUR',
		name : 'PCLSWL_CUR',
		boxLabel : '每信道中心波长当前值',
		inputValue : 'PCLSWL_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWL_MAX',
		name : 'PCLSWL_MAX',
		boxLabel : '每信道中心波长最大值',
		inputValue : 'PCLSWL_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWL_MIN',
		name : 'PCLSWL_MIN',
		boxLabel : '每信道中心波长最小值',
		inputValue : 'PCLSWL_MIN'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWLO_CUR',
		name : 'PCLSWLO_CUR',
		boxLabel : '每信道中心波长偏移当前值',
		inputValue : 'PCLSWLO_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWLO_MAX',
		name : 'PCLSWLO_MAX',
		boxLabel : '每信道中心波长偏移最大值',
		inputValue : 'PCLSWLO_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSWLO_MIN',
		name : 'PCLSWLO_MIN',
		boxLabel : '每信道中心波长偏移最小值',
		inputValue : 'PCLSWLO_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			var i, cnt = 0;
			for (i = 0; i < array.length; i++) {
				var box = array[i];
				var index = box.getRawValue();
				if (index == 'PCLSWLO_CUR' || index == 'PCLSWLO_MAX'
						|| index == 'PCLSWLO_MIN') {
					cnt++;
				} else {
					Ext.getCmp('max_min').setValue(false);
					Ext.getCmp('max_min').setDisabled(true);
					return;
				}
			}
			if (cnt > 1) {
				Ext.getCmp('max_min').setValue(false);
				Ext.getCmp('max_min').setDisabled(true);

			} else {
				Ext.getCmp('max_min').setDisabled(false);
			}
		}
	}
};

// 信噪比
var SNR = {
	id : 'SNR',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'PCLSSNR_CUR',
		name : 'PCLSSNR_CUR',
		boxLabel : '信噪比当前值',
		inputValue : 'PCLSSNR_CUR'
	}, {
		xtype : 'checkbox',
		id : 'PCLSSNR_MAX',
		name : 'PCLSSNR_MAX',
		boxLabel : '信噪比最大值',
		inputValue : 'PCLSSNR_MAX'
	}, {
		xtype : 'checkbox',
		id : 'PCLSSNR_MIN',
		name : 'PCLSSNR_MIN',
		boxLabel : '信噪比最小值',
		inputValue : 'PCLSSNR_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// OTU
var OTU = {
	id : 'OTU',
	xtype : 'checkboxgroup',
	columns : 1,
	layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	// labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OTU_BBE',
		name : 'OTU_BBE',
		boxLabel : 'OTU的SM段背景误码块',
		inputValue : 'OTU_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU_ES',
		name : 'OTU_ES',
		boxLabel : 'OTU的SM段误码秒',
		inputValue : 'OTU_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU_SES',
		name : 'OTU_SES',
		boxLabel : 'OTU的SM段严重误码秒',
		inputValue : 'OTU_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU_UAS',
		name : 'OTU_UAS',
		boxLabel : 'OTU的SM段不可用秒',
		inputValue : 'OTU_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_BBE',
		name : 'OTU1_BBE',
		boxLabel : 'OTU1的SM段背景误码块',
		inputValue : 'OTU1_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_ES',
		name : 'OTU1_ES',
		boxLabel : 'OTU1的SM段误码秒',
		inputValue : 'OTU1_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_SES',
		name : 'OTU1_SES',
		boxLabel : 'OTU1的SM段严重误码秒',
		inputValue : 'OTU1_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU1_UAS',
		name : 'OTU1_UAS',
		boxLabel : 'OTU1的SM段不可用秒',
		inputValue : 'OTU1_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_BBE',
		name : 'OTU2_BBE',
		boxLabel : 'OTU2的SM段背景误码块',
		inputValue : 'OTU2_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_ES',
		name : 'OTU2_ES',
		boxLabel : 'OTU2的SM段误码秒',
		inputValue : 'OTU2_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_SES',
		name : 'OTU2_SES',
		boxLabel : 'OTU2的SM段严重误码秒',
		inputValue : 'OTU2_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU2_UAS',
		name : 'OTU2_UAS',
		boxLabel : 'OTU2的SM段不可用秒',
		inputValue : 'OTU2_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_BBE',
		name : 'OTU3_BBE',
		boxLabel : 'OTU3的SM段背景误码块',
		inputValue : 'OTU3_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_ES',
		name : 'OTU3_ES',
		boxLabel : 'OTU3的SM段误码秒',
		inputValue : 'OTU3_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_SES',
		name : 'OTU3_SES',
		boxLabel : 'OTU3的SM段严重误码秒',
		inputValue : 'OTU3_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU3_UAS',
		name : 'OTU3_UAS',
		boxLabel : 'OTU3的SM段不可用秒',
		inputValue : 'OTU3_UAS'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_BBE',
		name : 'OTU5G_BBE',
		boxLabel : 'OTU5G的SM段背景误码块',
		inputValue : 'OTU5G_BBE'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_ES',
		name : 'OTU5G_ES',
		boxLabel : 'OTU5G的SM段误码秒',
		inputValue : 'OTU5G_ES'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_SES',
		name : 'OTU5G_SES',
		boxLabel : 'OTU5G的SM段严重误码秒',
		inputValue : 'OTU5G_SES'
	}, {
		xtype : 'checkbox',
		id : 'OTU5G_UAS',
		name : 'OTU5G_UAS',
		boxLabel : 'OTU5G的SM段不可用秒',
		inputValue : 'OTU5G_UAS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

// ODU
var ODU = {
	id : 'ODU',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ODU_BBE',
		name : 'ODU_BBE',
		boxLabel : 'ODU的PM段背景误码块',
		inputValue : 'ODU_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU_ES',
		name : 'ODU_ES',
		boxLabel : 'ODU的PM段误码秒',
		inputValue : 'ODU_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU_SES',
		name : 'ODU_SES',
		boxLabel : 'ODU的PM段严重误码秒',
		inputValue : 'ODU_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU_UAS',
		name : 'ODU_UAS',
		boxLabel : 'ODU的PM段不可用秒',
		inputValue : 'ODU_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_BBE',
		name : 'ODU1_BBE',
		boxLabel : 'ODU1的PM段背景误码块',
		inputValue : 'ODU1_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_ES',
		name : 'ODU1_ES',
		boxLabel : 'ODU1的PM段误码秒',
		inputValue : 'ODU1_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_SES',
		name : 'ODU1_SES',
		boxLabel : 'ODU1的PM段严重误码秒',
		inputValue : 'ODU1_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU1_UAS',
		name : 'ODU1_UAS',
		boxLabel : 'ODU1的PM段不可用秒',
		inputValue : 'ODU1_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_BBE',
		name : 'ODU2_BBE',
		boxLabel : 'ODU2的PM段背景误码块',
		inputValue : 'ODU2_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_ES',
		name : 'ODU2_ES',
		boxLabel : 'ODU2的PM段误码秒',
		inputValue : 'ODU2_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_SES',
		name : 'ODU2_SES',
		boxLabel : 'ODU2的PM段严重误码秒',
		inputValue : 'ODU2_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU2_UAS',
		name : 'ODU2_UAS',
		boxLabel : 'ODU2的PM段不可用秒',
		inputValue : 'ODU2_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_BBE',
		name : 'ODU3_BBE',
		boxLabel : 'ODU3的PM段背景误码块',
		inputValue : 'ODU3_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_ES',
		name : 'ODU3_ES',
		boxLabel : 'ODU3的PM段误码秒',
		inputValue : 'ODU3_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_SES',
		name : 'ODU3_SES',
		boxLabel : 'ODU3的PM段严重误码秒',
		inputValue : 'ODU3_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU3_UAS',
		name : 'ODU3_UAS',
		boxLabel : 'ODU3的PM段不可用秒',
		inputValue : 'ODU3_UAS'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_BBE',
		name : 'ODU5G_BBE',
		boxLabel : 'ODU5G的PM段背景误码块',
		inputValue : 'ODU5G_BBE'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_ES',
		name : 'ODU5G_ES',
		boxLabel : 'ODU5G的PM段误码秒',
		inputValue : 'ODU5G_ES'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_SES',
		name : 'ODU5G_SES',
		boxLabel : 'ODU5G的PM段严重误码秒',
		inputValue : 'ODU5G_SES'
	}, {
		xtype : 'checkbox',
		id : 'ODU5G_UAS',
		name : 'ODU5G_UAS',
		boxLabel : 'ODU5G的PM段不可用秒',
		inputValue : 'ODU5G_UAS'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

//EnvTmp
var EnvTmp = {
	id : 'EnvTmp',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'ENV_TMP_CUR',
		name : 'ENV_TMP_CUR',
		boxLabel : '环境温度当前值',
		inputValue : 'ENV_TMP_CUR'
	}, {
		xtype : 'checkbox',
		id : 'ENV_TMP_MAX',
		name : 'ENV_TMP_MAX',
		boxLabel : '环境温度最大值',
		inputValue : 'ENV_TMP_MAX'
	}, {
		xtype : 'checkbox',
		id : 'ENV_TMP_MIN',
		name : 'ENV_TMP_MIN',
		boxLabel : '环境温度最小值',
		inputValue : 'ENV_TMP_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

//OPT_LTEMP
var OPT_LTEMP = {
	id : 'OPT_LTEMP',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OPT_LTEMP_CUR',
		name : 'OPT_LTEMP_CUR',
		boxLabel : '激光器工作温度当前值',
		inputValue : 'OPT_LTEMP_CUR'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LTEMP_MAX',
		name : 'OPT_LTEMP_MAX',
		boxLabel : '激光器工作温度最大值',
		inputValue : 'OPT_LTEMP_MAX'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LTEMP_MIN',
		name : 'OPT_LTEMP_MIN',
		boxLabel : '激光器工作温度最小值',
		inputValue : 'OPT_LTEMP_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

//LASER
var LASER = {
	id : 'LASER',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_CUR',
		name : 'OPT_LBIAS_CUR',
		boxLabel : '激光器偏置电流当前值',
		inputValue : 'OPT_LBIAS_CUR'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MAX',
		name : 'OPT_LBIAS_MAX',
		boxLabel : '激光器偏置电流最大值',
		inputValue : 'OPT_LBIAS_MAX'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MIN',
		name : 'OPT_LBIAS_MIN',
		boxLabel : '激光器偏置电流最小值',
		inputValue : 'OPT_LBIAS_MIN'
	}, {
		xtype : 'checkbox',
		id : 'LWC_CUR',
		name : 'LWC_CUR',
		boxLabel : '激光器工作电流当前值',
		inputValue : 'LWC_CUR'
	}, {
		xtype : 'checkbox',
		id : 'LWC_MAX',
		name : 'LWC_MAX',
		boxLabel : '激光器工作电流最大值',
		inputValue : 'LWC_MAX'
	}, {
		xtype : 'checkbox',
		id : 'LWC_MIN',
		name : 'LWC_MIN',
		boxLabel : '激光器工作电流最小值',
		inputValue : 'LWC_MIN'
	}, {
		xtype : 'checkbox',
		id : 'LCC_CUR',
		name : 'LCC_CUR',
		boxLabel : '激光器制冷电流当前值',
		inputValue : 'LCC_CUR'
	}, {
		xtype : 'checkbox',
		id : 'LCC_MAX',
		name : 'LCC_MAX',
		boxLabel : '激光器制冷电流最大值',
		inputValue : 'LCC_MAX'
	}, {
		xtype : 'checkbox',
		id : 'LCC_MIN',
		name : 'LCC_MIN',
		boxLabel : '激光器制冷电流最小值',
		inputValue : 'LCC_MIN'
	}, {
		xtype : 'checkbox',
		id : 'LBC_CUR',
		name : 'LBC_CUR',
		boxLabel : '激光器背光检测电流当前值',
		inputValue : 'LBC_CUR'
	}, {
		xtype : 'checkbox',
		id : 'LBC_MAX',
		name : 'LBC_MAX',
		boxLabel : '激光器背光检测电流最大值',
		inputValue : 'LBC_MAX'
	}, {
		xtype : 'checkbox',
		id : 'LBC_MIN',
		name : 'LBC_MIN',
		boxLabel : '激光器背光检测电流最小值',
		inputValue : 'LBC_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

//LASER_SDH
var LASER_SDH = {
	id : 'LASER_SDH',
	xtype : 'checkboxgroup',
	columns : 1,
	// layout : 'form',
	// width : 280,
	// height : 150,
	// autoScroll:true,
	border : false,
	labelWidth : 70,
	items : [ {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_CUR',
		name : 'OPT_LBIAS_CUR',
		boxLabel : '激光器偏置电流当前值',
		inputValue : 'OPT_LBIAS_CUR'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MAX',
		name : 'OPT_LBIAS_MAX',
		boxLabel : '激光器偏置电流最大值',
		inputValue : 'OPT_LBIAS_MAX'
	}, {
		xtype : 'checkbox',
		id : 'OPT_LBIAS_MIN',
		name : 'OPT_LBIAS_MIN',
		boxLabel : '激光器偏置电流最小值',
		inputValue : 'OPT_LBIAS_MIN'
	} ],
	listeners : {
		'change' : function(t, array) {
			// if (array.length > 1) {
			// Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(true);
			//
			// } else {
			// Ext.getCmp('max_min').setDisabled(false);
			// }
		}
	}
};

var panel1 = new Ext.form.FormPanel({
	id : 'panel1',
	autoScroll : true,
	border : false,
	height : 175,
	width : 280,
	// margins:{top:20, right:0, bottom:20, left:0},
	style : " margin-top: 20px; margin-bottom: 15px;",
	labelWidth : 59,
	items : []
});
var panel2 = new Ext.form.FormPanel({
	id : 'panel2',
	autoScroll : true,
	border : false,
	height : 175,
	width : 280,
	// margins:{top:20, right:0, bottom:20, left:0},
	style : " margin-top: 20px; margin-bottom: 15px;",
	labelWidth : 59,
	items : []
});
// **********************************combo********************
// TODO
var paramCombo = new Ext.form.ComboBox({
	id : 'paramCombo',
	fieldLabel : '参数类型',
	mode : 'local',
	labelSeparator : ':',
	triggerAction : 'all',
	store : paramStore,
	valueField : 'paramId',
	displayField : 'paramName',
	listeners : {
		'select' : function(combo, record, index) {
			// 如果有checkbox组，先remove掉
			if (panel1.get(0)) {
				panel1.remove(panel1.get(0));
			}
			// 重置上限值下限值选框
			Ext.getCmp('max_min').setValue(false);
			// Ext.getCmp('max_min').setDisabled(false);
			switch (record.get('paramId')) {
			case 0: // 再生段
				panel1.add(regeneratorSection);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 1: // 复用段
				panel1.add(multiplexSection);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 2: // 光监控信道
				panel1.add(opticalSupervisoryChannel);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 3: // FEC误码率
				panel1.add(FEC);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 4: // VC4
				panel1.add(VC4);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 5: // VC3
				panel1.add(VC3);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 6: // VC12
				panel1.add(VC12);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 7: // 输出光功率
				panel1.add(outputLightPower);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(false);
				break;
			case 8: // 输入光功率
				panel1.add(inputLightPower);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(false);
				break;
			case 9: // 每信道光功率
				panel1.add(opticalPowerPerChanel);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(false);
				break;
			case 10: // 每信道中心波长
				panel1.add(frequency);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(false);
				break;
			case 11: // 信噪比
				panel1.add(SNR);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 12: // OTU误码
			// Ext.getCmp('panel1').add(OTU);
			// Ext.getCmp('panel1').doLayout();
				panel1.add(OTU);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 13: // ODU误码
				panel1.add(ODU);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 14: // 环境温度
				panel1.add(EnvTmp);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 15: // 激光器温度
				panel1.add(OPT_LTEMP);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 16: // 激光器电流
				panel1.add(LASER);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 17: // SDH激光器电流
				panel1.add(LASER_SDH);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			case 18: // ETH性能相关
//				panel1.add(LASER_SDH);
				panel1.doLayout();
				Ext.getCmp('max_min').setDisabled(true);
				break;
			}
		}
	}
});

var paramComboBottom = new Ext.form.ComboBox({
	id : 'paramComboBottom',
	fieldLabel : '参数类型',
	mode : 'local',
	labelSeparator : ':',
	triggerAction : 'all',
	store : paramStore,
	valueField : 'paramId',
	displayField : 'paramName',
	listeners : {
		'select' : function(combo, record, index) {
			// 如果有checkbox组，先remove掉
			if (panel2.get(0)) {
				panel2.remove(panel2.get(0));
			}
			// 重置上限值下限值选框
			Ext.getCmp('max_min_bottom').setValue(false);
			// Ext.getCmp('max_min_bottom').setDisabled(false);		
			switch (record.get('paramId')) {
			case 0: // 再生段
				panel2.add(regeneratorSectionBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 1: // 复用段
				panel2.add(multiplexSectionBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 2: // 光监控信道
				panel2.add(opticalSupervisoryChannelBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 3: // FEC误码率
				panel2.add(FEC_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 4: // VC4
				panel2.add(VC4_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 5: // VC3
				panel2.add(VC3_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 6: // VC12
				panel2.add(VC12_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 7: // 输出光功率
				panel2.add(outputLightPowerBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(false);
				break;
			case 8: // 输入光功率
				panel2.add(inputLightPowerBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(false);
				break;
			case 9: // 每信道光功率
				panel2.add(opticalPowerPerChanelBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(false);
				break;
			case 10: // 每信道中心波长
				panel2.add(frequencyBottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(false);
				break;
			case 11: // 信噪比
				panel2.add(SNR_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 12: // OTU
				panel2.add(OTU_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 13: // ODU
				panel2.add(ODU_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 14: // 环境温度
				panel2.add(EnvTmp_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 15: // 激光器温度
				panel2.add(OPT_LTEMP_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 16: // 激光器电流
				panel2.add(LASER_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			case 17: // SDH激光器电流
				panel2.add(LASER_SDH_Bottom);
				panel2.doLayout();
				Ext.getCmp('max_min_bottom').setDisabled(true);
				break;
			}
		}
	}
});

var timeRangeCombo = new Ext.form.ComboBox({
	id : 'timeRangeCombo',
	mode : 'local',
	fieldLabel : '时间段',
	store : timeRangeStore,
	editable:false,
	value:1,
	labelSeparator : ':',
	width : 160,
	triggerAction : 'all',
	valueField : 'timeRangeId',
	displayField : 'timeRangeName',
	listeners : {
		'select' : function() {
			Ext.getCmp('timeRangeComboBottom').setValue(
					Ext.getCmp('timeRangeCombo').getValue());
		}
	}
});

var timeRangeComboBottom = new Ext.form.ComboBox({
	id : 'timeRangeComboBottom',
	mode : 'local',
	disabled : true,
	fieldLabel : '时间段',
	editable:false,
	store : timeRangeStore,
	value:1,
	width : 160,
	labelSeparator : ':',
	triggerAction : 'all',
	valueField : 'timeRangeId',
	displayField : 'timeRangeName'
});

// ******************************date picker*****************************
var startTime = {
	xtype : 'compositefield',
	fieldLabel : '开始时间',
	defaults : {
		flex : 2
	},
	items : [
			{
				xtype : 'textfield',
				id : 'startTime',
				name : 'startTime',
				fieldLabel : '开始时间',
				allowBlank : false,
				readOnly : true,
				// value:this.nowTime,
				width : 160,
				cls : 'Wdate',
				listeners : {
					'focus' : function() {
						WdatePicker({
							el : "startTime",
							isShowClear : false,
							readOnly : true,
							dateFmt : 'yyyy-MM-dd',
							autoPickDate : true,
							maxDate : '%y-%M-%d',
							onpicked : function() {
								Ext.getCmp('startTimeBottom').setValue(
										Ext.getCmp('startTime').getValue());
							}

						});
						this.blur();
					}
				}
			} ]
};

var startTimeBottom = {
	xtype : 'compositefield',
	fieldLabel : '开始时间',
	defaults : {
		flex : 2
	},
	items : [ {
		xtype : 'textfield',
		id : 'startTimeBottom',
		name : 'startTimeBottom',
		disabled : true,
		fieldLabel : '开始时间',
		width : 160,
		cls : 'Wdate',
		listeners : {
			'focus' : function() {
				WdatePicker({
					el : "startTimeBottom",
					isShowClear : false,
					readOnly : true,
					dateFmt : 'yyyy-MM-dd',
					autoPickDate : true,
					maxDate : '%y-%M-%d'
				});
				this.blur();
			}
		}
	}
	// , {
	// xtype : 'button',
	// id : 'resetStartTimeBottom',
	// name : 'resetStartTimeBottom',
	// text : '清空',
	// width : 45,
	// handler : function() {
	// Ext.getCmp('startTimeBottom').setValue("");
	// }
	// }
	]
};

// *************************上限值和下限值*****************************
var limit = {
	xtype : 'checkbox',
	id : 'max_min',
	name : 'max_min',
	boxLabel : '上限值和下限值'
};

var limitBottom = {
	xtype : 'checkbox',
	id : 'max_min_bottom',
	name : 'max_min_bottom',
	boxLabel : '上限值和下限值'
};

// *************************field set ******************************
var otherParam1 = {
	id : 'otherParam1',
	xtype : 'fieldset',
	layout : 'form',
	padding : '10px',
	// height : 100,
	labelWidth : 60,
	items : [ startTime, timeRangeCombo ]
};
var otherParam2 = {
	id : 'otherParam2',
	xtype : 'fieldset',
	layout : 'form',
	padding : '10px',
	// height : 100,
	labelWidth : 60,
	items : [ startTimeBottom, timeRangeComboBottom ]
};

// TODO
var fieldSet1 = {
	id : 'fieldSet1',
	xtype : 'fieldset',
	padding : '15px',
	layout : 'form',
	// height : 250,
	labelWidth : 60,
	items : [ paramCombo, panel1, limit ]
};

var fieldSet2 = {
	id : 'fieldSet2',
	xtype : 'fieldset',
	padding : '15px',
	layout : 'form',
	// width : 320,
	// height : 250,
	labelWidth : 60,
	items : [ paramComboBottom, panel2, limitBottom ]
};

var topField = {
	xtype : 'fieldset',
	id : 'topField',
	title : '性能趋势图1',
	layout : 'fit',
	 width : Ext.getBody().getWidth()-50,
	items : [ {
		xtype : 'compositefield',
		// defaults : {
		// flex : 2
		// },
		items : [ new Ext.Spacer({ // 占位
			id : 'chart1',
			height : 320,
			width : Ext.getBody().getWidth()-420
		}), {
			xtype : 'fieldset',
			id : 'rightPartTop',
			layout : 'form',
			border : false,
			width : 350,
			items : [ fieldSet1, otherParam1, {
				layout : 'column',
				height : 40,
				border : false,
				items : [ {
					columnWidth : .5,
					border : false,
					items : {
						xtype : 'button',
						id : 'generateDiagram1',
						text : '生成趋势图',
						ctCls : 'margin-for-button',
						width : 80,
						handler : generateDiagramNend
					}
				}, {
					columnWidth : .5,
					border : false,
					items : new Ext.Spacer({
						id : 'exportDiagram1',
						width : 120
					})
				} ]
			} ]
		} ]
	} ]

};

var nendOrFend = 'nend';

var switchButton = new Ext.Button({
	id : 'switchButton',
	text : '切换到远端(当前：本端)',
	width : 80,
	handler : function() {
	},
	listeners : {
		'click' : function(t, e) {
			if (ctpId != 'null') {
				Ext.Msg.alert("提示", "ctp性能无对端数据！");
			} else {
				if (nendOrFend == 'nend') {
					nendOrFend = 'fend';
					switchButton.setText('切换到本端(当前：远端)');
					var index = paramStore.find("paramId", Ext.getCmp(
							'paramCombo').getValue());
					Ext.getCmp('paramComboBottom').setValue(
							Ext.getCmp('paramCombo').getValue());
					Ext.getCmp('paramComboBottom').fireEvent('select',
							Ext.getCmp('paramComboBottom'),
							paramStore.getAt(index),
							Ext.getCmp('paramCombo').getValue());

					// 不可编辑状态
					// Ext.getCmp('paramComboBottom').disable();
					// Ext.getCmp('max_min_bottom').disable();
					// Ext.getCmp('fieldSet2').get(1).disable();
					// Ext.getCmp('startTimeBottom').disable();
					// Ext.getCmp('resetStartTimeBottom').disable();
					// Ext.getCmp('timeRangeComboBottom').disable();
					// 参数复制
					panel2.get(0).setValue(panel1.get(0).getValue());
					Ext.getCmp('max_min_bottom').setValue(
							Ext.getCmp('max_min').getValue());
					Ext.getCmp('startTimeBottom').setValue(
							Ext.getCmp('startTime').getValue());
					Ext.getCmp('timeRangeComboBottom').setValue(
							Ext.getCmp('timeRangeCombo').getValue());

					if (panel2.get(0).getValue(1).length < 2)
						Ext.getCmp('max_min_bottom').enable();
					else
						Ext.getCmp('max_min_bottom').disable();

				} else {
					nendOrFend = 'nend';
					switchButton.setText('切换到远端(当前：本端)');

					// Ext.getCmp('paramComboBottom').enable();
					// if (Ext.getCmp('fieldSet2').get(1).getValue(1).length <
					// 2)
					// Ext.getCmp('max_min_bottom').enable();
					// else
					// Ext.getCmp('max_min_bottom').disable();
					// Ext.getCmp('fieldSet2').get(1).enable();
					// Ext.getCmp('startTimeBottom').enable();
					// Ext.getCmp('resetStartTimeBottom').enable();
					// Ext.getCmp('timeRangeComboBottom').enable();
				}
			}
		}
	}
});

var bottomField = {
	xtype : 'fieldset',
	id : 'bottomField',
	title : '性能趋势图2',
	layout : 'fit',
//	width:1327,
	// padding:'10px 0 0 0',
	 width : Ext.getBody().getWidth()-50,
	// height : 450,
	region : 'center',
	items : [ {
		xtype : 'compositefield',
		// defaults : {
		// flex : 2
		// },
		items : [ new Ext.Spacer({ // 占位
			height : 320,
			width : Ext.getBody().getWidth()-420,
			id : 'chart2'
		// ,width : 700
		}), {
			xtype : 'fieldset',
			layout : 'form',
			width : 350,
			border : false,
			items : [ switchButton, new Ext.Spacer({
				height : 10
			}), fieldSet2, otherParam2, {
				layout : 'column',
				border : false,
				items : [ {
					columnWidth : .5,
					border : false,
					items : {
						xtype : 'button',
						id : 'generateDiagram2',
						text : '生成趋势图',
						ctCls : 'margin-for-button',
						width : 80,
						handler : generateDiagramFend
					}
				}, {
					columnWidth : .5,
					border : false,
					items : {
						columnWidth : .5,
						border : false,
						items : new Ext.Spacer({
							id : 'exportDiagram2',
							width : 120
						})
					}
				} ]
			} ]
		} ]
	} ]
};

// *********************************panel****************************
// var paramTopPanel = new Ext.Panel({
// id : 'paramTopPanel',
// xtype : 'fieldset',
// region : 'center',
// padding : '30px',
// autoScroll : true,
// items :
// })

var centerPanel = new Ext.Panel({
	id : 'centerPanel',
	region : 'center',
	frame : false,
	layout : 'form',
	bodyStyle : 'padding:20px 50px 0 20px',
	autoScroll : true,
	items : [ topField, bottomField ]
});

// --------------------------------------------functions---------------------------------------

function showFusionCharts(xmlStr, chartdiv, chartId) {
	var myChart = new FusionCharts(
				"../../../resource/FusionCharts/Charts/MSLine.swf", chartId, Ext.getBody().getWidth()-430,
				"420");
	// myChart.setDataURL("dataLine.xml");
	myChart.setDataXML(xmlStr);
	myChart.render(chartdiv);
}

function exportPicture() {
	var myExportComponent = new FusionChartsExportObject("nendExporter",
			"../../../resource/FusionCharts/Charts/FCExporter.swf"); // 参数1：为处理程序标识，参数二为：上文中提到的导出需要用到的swf文件

	myExportComponent.componentAttributes.btnColor = 'F5F5F5';

	myExportComponent.componentAttributes.btnBorderColor = '666666';

	myExportComponent.componentAttributes.btnFontFace = 'Verdana';

	myExportComponent.componentAttributes.btnFontColor = '333333';

	myExportComponent.componentAttributes.btnFontSize = '12';
	// Title of button
	myExportComponent.componentAttributes.btnsavetitle = '另存为';
	myExportComponent.componentAttributes.btndisabledtitle = '右键生成图片';
	myExportComponent.render("exportDiagram1");

	var myExportComponent = new FusionChartsExportObject("fendExporter",
			"../../../resource/FusionCharts/Charts/FCExporter.swf"); // 参数1：为处理程序标识，参数二为：上文中提到的导出需要用到的swf文件

	myExportComponent.componentAttributes.btnColor = 'F5F5F5';

	myExportComponent.componentAttributes.btnBorderColor = '666666';

	myExportComponent.componentAttributes.btnFontFace = 'Verdana';

	myExportComponent.componentAttributes.btnFontColor = '333333';

	myExportComponent.componentAttributes.btnFontSize = '12';
	// Title of button
	myExportComponent.componentAttributes.btnsavetitle = '另存为';
	myExportComponent.componentAttributes.btndisabledtitle = '右键生成图片';
	myExportComponent.render("exportDiagram2");
}
// 本端
function generateDiagramNend() {
	var pmStdIndexSelect = [];
	if(Ext.getCmp('paramCombo').getValue()==18){
		if(PM_STD_INDEX_TYPE==18)
			pmStdIndexSelect = [pmStdIndex];
	}else if(Ext.getCmp('paramCombo').getValue()==19){
		if(UNKNOWN_PM)
			pmStdIndexSelect = [pmStdIndex];
	}else{
		var fieldId = getId(Ext.getCmp('paramCombo').getValue(), 1);
		pmStdIndexSelect = Ext.getCmp(fieldId).getValue(1);
	}
	var startTime = Ext.getCmp('startTime').getValue();
	var timeRange = Ext.getCmp('timeRangeCombo').getValue();
	var needLimit = Ext.getCmp('max_min').getValue() ? 1 : 0;
	var pmType = paramStore.getAt(paramStore.find('paramId',Ext.getCmp('paramCombo').getValue())).get('pmType');
//	if (startTime == '') {
//		Ext.Msg.alert('提示', '请选择开始时间！');
//		return;
//	}
//	if (timeRange == '') {
//		Ext.Msg.alert('提示', '请选择时间段！');
//		return;
//	}
	if (pmStdIndexSelect.length > 6) {
		Ext.Msg.alert('提示', '请勿选择超过6个性能！');
		return;
	}
	var searchParam = {
		'searchCond.pmStdIndex' : pmStdIndexSelect.toString(),
		'searchCond.startTime' : startTime,
		'searchCond.timeRange' : timeRange,
		'searchCond.unitId' : unitId,
		'searchCond.ptpId' : ptpId,
		'searchCond.ctpId' : ctpId,
		'searchCond.emsConnectionId' : emsConnectionId,
		'searchCond.needLimit' : needLimit,
		'searchCond.type' : type,
		'searchCond.pmType' : pmType,
		'searchCond.targetType' : targetType
	};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'pm-search!generateDiagramNend.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if (result) {
				showFusionCharts(result.returnMessage, "chart1", "nendChart");
				return;
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});

}
// 远端
function generateDiagramFend() {
	var pmStdIndexSelect = [];
	if(Ext.getCmp('paramComboBottom').getValue()==18){
		if(PM_STD_INDEX_TYPE==18)
			pmStdIndexSelect = [pmStdIndex];
	}else if(Ext.getCmp('paramComboBottom').getValue()==19){
		if(UNKNOWN_PM)
			pmStdIndexSelect = [pmStdIndex];
	}else{
		var fieldId = getId(Ext.getCmp('paramComboBottom').getValue(), 2);
		pmStdIndexSelect = Ext.getCmp(fieldId).getValue(1);
	}
	var startTime = Ext.getCmp('startTimeBottom').getValue();
	var timeRange = Ext.getCmp('timeRangeComboBottom').getValue();
	var needLimit = Ext.getCmp('max_min_bottom').getValue() ? 1 : 0;
	var pmType = paramStore.getAt(paramStore.find('paramId',Ext.getCmp('paramCombo').getValue())).get('pmType');

//	if (startTime == '') {
//		Ext.Msg.alert('提示', '请选择开始时间！');
//		return;
//	}
//	if (timeRange == '') {
//		Ext.Msg.alert('提示', '请选择时间段！');
//		return;
//	}
	if (pmStdIndexSelect.length > 6) {
		Ext.Msg.alert('提示', '请勿选择超过6个性能！');
		return;
	}
	var searchParam = {
		'searchCond.pmStdIndex' : pmStdIndexSelect.toString(),
		'searchCond.startTime' : startTime,
		'searchCond.timeRange' : timeRange,
		'searchCond.unitId' : unitId,
		'searchCond.ptpId' : ptpId,
		'searchCond.ctpId' : ctpId,
		'searchCond.fend' : 'fend',
		'searchCond.emsConnectionId' : emsConnectionId,
		'searchCond.type' : type,
		'searchCond.needLimit' : needLimit,
		'searchCond.pmType' : pmType,
		'searchCond.targetType' : targetType
	};
	if (nendOrFend == 'nend') {
		Ext.getBody().mask('执行中...');
		Ext.Ajax.request({
			url : 'pm-search!generateDiagramNend.action',
			params : searchParam,
			method : 'POST',
			success : function(response) {
				Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					showFusionCharts(result.returnMessage, "chart2",
							"fendChart");
					return;
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.getBody().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.getBody().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	} else {
		Ext.getBody().mask('执行中...');
		Ext.Ajax.request({
			url : 'pm-search!generateDiagramFend.action',
			params : searchParam,
			method : 'POST',
			success : function(response) {
				Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				if (result) {
					showFusionCharts(result.returnMessage, "chart2",
							"fendChart");
					return;
				}
			},
			failure : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.getBody().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.getBody().unmask();
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}
// 通过所选择的combo获取对应的checkboxgroup的id
function getId(paramId, location) {
	if (location == 1) {
		switch (paramId) {
		case 0:
			return 'regeneratorSection';
		case 1:
			return 'multiplexSection';
		case 2:
			return 'opticalSupervisoryChannel';
		case 3:
			return 'FEC';
		case 4:
			return 'VC4';
		case 5:
			return 'VC3';
		case 6:
			return 'VC12';
		case 7:
			return 'outputLightPower';
		case 8:
			return 'inputLightPower';
		case 9:
			return 'opticalPowerPerChanel';
		case 10:
			return 'frequency';
		case 11:
			return 'SNR';
		case 12:
			return 'OTU';
		case 13:
			return 'ODU';
		case 14:
			return 'EnvTmp';
		case 15:
			return 'OPT_LTEMP';
		case 16:
			return 'LASER';
		case 17:
			return 'LASER_SDH';
		}
	} else {
		switch (paramId) {
		case 0:
			return 'regeneratorSectionBottom';
		case 1:
			return 'multiplexSectionBottom';
		case 2:
			return 'opticalSupervisoryChannelBottom';
		case 3:
			return 'FEC_Bottom';
		case 4:
			return 'VC4_Bottom';
		case 5:
			return 'VC3_Bottom';
		case 6:
			return 'VC12_Bottom';
		case 7:
			return 'outputLightPowerBottom';
		case 8:
			return 'inputLightPowerBottom';
		case 9:
			return 'opticalPowerPerChanelBottom';
		case 10:
			return 'frequencyBottom';
		case 11:
			return 'SNR_Bottom';
		case 12:
			return 'OTU_Bottom';
		case 13:
			return 'ODU_Bottom';
		case 14:
			return 'EnvTmp_Bottom';
		case 15:
			return 'OPT_LTEMP_Bottom';
		case 16:
			return 'LASER_Bottom';
		case 17:
			return 'LASER_SDH_Bottom';
		}
	}
}
// TODO 通过pm std index获取对应的check box group的id
function getParentId(index) {
	var record = new Ext.data.Record.create([ {
		name : 'paramId',
		type : 'long'
	}, {
		name : 'paramName',
		type : 'string'
	} ]);
	switch (index) {
	// 再生段
	case 'RS_BBE':
	case 'RS_ES':
	case 'RS_SES':
	case 'RS_CSES':
	case 'RS_UAS':
	case 'RS_OFS': {
		return new record({
			paramId : 0,
			paramName : "再生段"
		});
	}
		// 复用段
	case 'MS_BBE':
	case 'MS_ES':
	case 'MS_SES':
	case 'MS_CSES':
	case 'MS_UAS': {
		return new record({
			paramId : 1,
			paramName : "复用段"
		});
	}
		// 光监控信道
	case 'OSC_BBE':
	case 'OSC_ES':
	case 'OSC_SES':
	case 'OSC_UAS': {
		return new record({
			paramId : 2,
			paramName : "光监控信道"
		});
	}
		// FEC误码率
	case 'FEC_BEF_COR_ER':
	case 'FEC_AFT_COR_ER':
	case 'MS_BBE': {
		return new record({
			paramId : 3,
			paramName : "FEC误码率"
		});
	}
		// VC4通道
	case 'VC4_BBE':
	case 'VC4_ES':
	case 'VC4_SES':
	case 'VC4_CSES':
	case 'VC4_UAS': {
		return new record({
			paramId : 4,
			paramName : "VC4通道"
		});
	}
		// VC3通道
	case 'VC3_BBE':
	case 'VC3_ES':
	case 'VC3_SES':
	case 'VC3_CSES':
	case 'VC3_UAS': {
		return new record({
			paramId : 5,
			paramName : "VC3通道"
		});
	}
		// VC12通道
	case 'VC12_BBE':
	case 'VC12_ES':
	case 'VC12_SES':
	case 'VC12_CSES':
	case 'VC12_UAS': {
		return new record({
			paramId : 6,
			paramName : "VC12通道"
		});
	}
		// 输出光功率
	case 'TPL_AVG':
	case 'TPL_CUR':
	case 'TPL_MAX':
	case 'TPL_MIN': {
		return new record({
			paramId : 7,
			paramName : "输出光功率"
		});
	}
		// 输入光功率
	case 'RPL_AVG':
	case 'RPL_CUR':
	case 'RPL_MAX':
	case 'RPL_MIN': {
		return new record({
			paramId : 8,
			paramName : "输入光功率"
		});
	}
		// 每信道光功率
	case 'PCLSOP_CUR':
	case 'PCLSOP_MAX':
	case 'PCLSOP_MIN': {
		return new record({
			paramId : 9,
			paramName : "每信道光功率"
		});
	}
		// 每信道中心波长
	case 'PCLSWL_CUR':
	case 'PCLSWL_MAX':
	case 'PCLSWL_MIN':
	case 'PCLSWLO_CUR':
	case 'PCLSWLO_MAX':
	case 'PCLSWLO_MIN': {
		return new record({
			paramId : 10,
			paramName : "每信道中心波长"
		});
	}
		// 信噪比
	case 'PCLSSNR_CUR':
	case 'PCLSSNR_MAX':
	case 'PCLSSNR_MIN': {
		return new record({
			paramId : 11,
			paramName : "信噪比"
		});
	}
		// OTU
	case 'OTU_BBE':
	case 'OTU_ES':
	case 'OTU_SES':
	case 'OTU_UAS':
	case 'OTU1_BBE':
	case 'OTU1_ES':
	case 'OTU1_SES':
	case 'OTU1_UAS':
	case 'OTU2_BBE':
	case 'OTU2_ES':
	case 'OTU2_SES':
	case 'OTU2_UAS':
	case 'OTU3_BBE':
	case 'OTU3_ES':
	case 'OTU3_SES':
	case 'OTU3_UAS':
	case 'OTU5G_BBE':
	case 'OTU5G_ES':
	case 'OTU5G_SES':
	case 'OTU5G_UAS': {
		return new record({
			paramId : 12,
			paramName : "OTU误码"
		});
	}
		// ODU
	case 'ODU_BBE':
	case 'ODU_ES':
	case 'ODU_SES':
	case 'ODU_UAS':
	case 'ODU1_BBE':
	case 'ODU1_ES':
	case 'ODU1_SES':
	case 'ODU1_UAS':
	case 'ODU2_BBE':
	case 'ODU2_ES':
	case 'ODU2_SES':
	case 'ODU2_UAS':
	case 'ODU3_BBE':
	case 'ODU3_ES':
	case 'ODU3_SES':
	case 'ODU3_UAS':
	case 'ODU5G_BBE':
	case 'ODU5G_ES':
	case 'ODU5G_SES':
	case 'ODU5G_UAS': {
		return new record({
			paramId : 13,
			paramName : "ODU误码"
		});
	}
	case 'ENV_TMP_CUR':
	case 'ENV_TMP_MAX':
	case 'ENV_TMP_MIN':
		return new record({
			paramId : 14,
			paramName : "环境温度"
		});
	case 'OPT_LTEMP_CUR':
	case 'OPT_LTEMP_MAX':
	case 'OPT_LTEMP_MIN':
		return new record({
			paramId : 15,
			paramName : "激光器温度"
		});
	case 'OPT_LBIAS_CUR':
	case 'OPT_LBIAS_MAX':
	case 'OPT_LBIAS_MIN':
	case 'LWC_CUR':
	case 'LWC_MAX':
	case 'LWC_MIN':
	case 'LCC_CUR':
	case 'LCC_MAX':
	case 'LCC_MIN':
	case 'LBC_CUR':
	case 'LBC_MAX':
	case 'LBC_MIN':
		if(type == 1){
			return new record({
				paramId : 17,
				paramName : "激光器电流"
			});
		}else if (type == 2){
			return new record({
				paramId : 16,
				paramName : "激光器电流"
			});
		}
	default: 
		if(PM_STD_INDEX_TYPE==18)
			return new record({
				paramId : 18,
				paramName : "以太网性能"
			});
		else{
				UNKNOWN_PM = true;
				return new record({
					paramId : 19,
					paramName : "其他"
				});
			}
	}

}
// --------------------------------------------------------------------------------------------


function initialize(){	
	var searchParam = {'searchCond.pmStdIndex' : pmStdIndex};
	Ext.getBody().mask('执行中...');
	Ext.Ajax.request({
		url : 'pm-search!getPmStdIndexType.action',
		params : searchParam,
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var result = Ext.util.JSON.decode(response.responseText);
			if(result.returnResult != 1){
				Ext.Msg.alert("提示", result.returnMessage);
				return;
			}
				
			PM_STD_INDEX_TYPE = result.returnMessage;
			Ext.getCmp('paramCombo').setValue(
					getParentId(pmStdIndex).get('paramId'));
			var index = Ext.getCmp('paramCombo').getStore().find('paramId',
					getParentId(pmStdIndex).get('paramId'));
			Ext.getCmp('paramCombo').fireEvent('select',
					Ext.getCmp('paramCombo'),
					Ext.getCmp('paramCombo').getStore().getAt(index), index);
			if(Ext.getCmp(pmStdIndex)!=undefined)
				Ext.getCmp(pmStdIndex).setValue(true);
			if(!Ext.getCmp('max_min').disabled){
				Ext.getCmp('max_min').setValue(true);
			}
			
			if (starttime != null) {
				Ext.getCmp('startTimeBottom').setValue(starttime);
				Ext.getCmp('startTime').setValue(starttime);
			}
			exportPicture();
			generateDiagramNend();
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		},
		error : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.getBody().unmask();
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
	
}
Ext
		.onReady(function() {
			Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
			Ext.Ajax.timeout = 900000;
			document.onmousedown = function() {
				top.Ext.menu.MenuMgr.hideAll();
			};
			// Ext.Msg = top.Ext.Msg;

			var win = new Ext.Viewport({
				id : 'win',
				layout : 'border',
				items : [ centerPanel ],
				renderTo : Ext.getBody()
			});
			// var d = new Date();
			// function addzero(v) {
			// if (v < 10) return '0' + v;
			// return v.toString();
			// }
			// var s = d.getFullYear().toString() +'-'+ addzero(d.getMonth() +
			// 1) +'-'+ addzero(d.getDate());
			// Ext.getCmp('startTime').setValue(s);
			// Ext.getCmp('startTimeBottom').setValue(s);
			initialize();
			
			// showFusionCharts();
			
			win.show();
			
		});