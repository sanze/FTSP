var getPmChecked = {
	getSdhPhysical : function() {
		var pmStdIndex = {
			21 : [ 'TPL_CUR', 'TPL_MAX', 'TPL_MIN' ],
			22 : [ 'RPL_CUR', 'RPL_MAX', 'RPL_MIN' ],
			23 : []
		};
		var pmStdIndexWithoutUpperAndLower = {
			21 : [ 'TPL_CUR' ],
			22 : [ 'RPL_CUR' ],
			23 : []
		};
		return function() {
			var SdhParamenter = new Array();
			var SdhPhyMaxMin = Ext.getCmp('SDHMaxMin').getValue();
			if (SdhPhyMaxMin) {
				Ext.getCmp('SDHPhysical').items.each(function(item) {
						if (item.getValue()) {
							SdhParamenter.push.apply(SdhParamenter,
									pmStdIndex[item.inputValue]);
						}
				});
			} else {
				Ext.getCmp('SDHPhysical').items
						.each(function(item) {
								if (item.getValue()) {
									SdhParamenter.push
											.apply(
													SdhParamenter,
													pmStdIndexWithoutUpperAndLower[item.inputValue]);
								}
						});
			}
			return SdhParamenter;
		};
	}(),
	getSdhNumeric : function() {
		var pmStdIndex = {
			31 : [ 'RS_BBE', 'RS_ES', 'RS_SES', 'RS_CSES', 'RS_UAS', 'RS_OFS' ],
			32 : [ 'MS_BBE', 'MS_ES', 'MS_SES', 'MS_CSES', 'MS_UAS' ],
			33 : [ 'VC4_BBE', 'VC4_ES', 'VC4_SES', 'VC4_CSES', 'VC4_UAS' ],
			34 : [ 'VC3_BBE', 'VC3_ES', 'VC3_SES', 'VC3_CSES', 'VC3_UAS',
					'VC12_BBE', 'VC12_ES', 'VC12_SES', 'VC12_CSES', 'VC12_UAS' ],
			35 : []
		};
		return function() {
			var SdhParamenter = new Array();
			Ext.getCmp('SDHNumberic').items.each(function(item) {
					if (item.getValue()) {
						SdhParamenter.push.apply(SdhParamenter,
								pmStdIndex[item.inputValue]);
					}
			});
			return SdhParamenter;
		};
	}(),
	getWdmPhysical : function() {
		var pmStdIndex = {
			51 : [ 'TPL_CUR', 'TPL_MAX', 'TPL_MIN', 'TPL_AVG' ],
			52 : [ 'RPL_CUR', 'RPL_MAX', 'RPL_MIN', 'RPL_AVG' ],
			54 : [ 'PCLSSNR_CUR', 'PCLSSNR_MAX', 'PCLSSNR_MIN' ],
			55 : [ 'PCLSWL_CUR', 'PCLSWL_MAX', 'PCLSWL_MIN', 'PCLSWLO_CUR',
					'PCLSWLO_MAX', 'PCLSWLO_MIN' ],
			56 : [ 'PCLSOP_CUR', 'PCLSOP_MAX', 'PCLSOP_MIN' ],
			57 : []
		};
		var pmStdIndexWithoutUpperAndLower = {
			51 : [ 'TPL_CUR', 'TPL_AVG' ],
			52 : [ 'RPL_CUR', 'RPL_AVG' ],
			54 : [ 'PCLSSNR_CUR' ],
			55 : [ 'PCLSWL_CUR', 'PCLSWLO_CUR' ],
			56 : [ 'PCLSOP_CUR' ],
			57 : []
		};
		return function() {
			var WDMParamenter = new Array();
			var WDMPhyMaxMin = Ext.getCmp('WDMMaxMin').getValue();
			if (WDMPhyMaxMin) {
				Ext.getCmp('WDMPhysical').items.each(function(item) {
						if (item.getValue()) {
							WDMParamenter.push.apply(WDMParamenter,
									pmStdIndex[item.inputValue]);
						}
				});

			} else {
				Ext.getCmp('WDMPhysical').items
						.each(function(item) {
								if (item.getValue()) {
									WDMParamenter.push
											.apply(
													WDMParamenter,
													pmStdIndexWithoutUpperAndLower[item.inputValue]);
								}
						});
			}
			return WDMParamenter;
		};
	}(),
	getWdmNumberic : function() {
		var pmStdIndex = {
			61 : [ 'OTU_BBE', 'OTU_ES', 'OTU_SES', 'OTU_UAS', 'OTU1_BBE',
					'OTU1_ES', 'OTU1_SES', 'OTU1_UAS', 'OTU2_BBE', 'OTU2_ES',
					'OTU2_SES', 'OTU2_UAS', 'OTU3_BBE', 'OTU3_ES', 'OTU3_SES',
					'OTU3_UAS', 'OTU5G_BBE', 'OTU5G_ES', 'OTU5G_SES',
					'OTU5G_UAS' ],
			62 : [ 'ODU_BBE', 'ODU_ES', 'ODU_SES', 'ODU_UAS', 'ODU1_BBE',
					'ODU1_ES', 'ODU1_SES', 'ODU1_UAS', 'ODU2_BBE', 'ODU2_ES',
					'ODU2_SES', 'ODU2_UAS', 'ODU3_BBE', 'ODU3_ES', 'ODU3_SES',
					'ODU3_UAS', 'ODU5G_BBE', 'ODU5G_ES', 'ODU5G_SES',
					'ODU5G_UAS' ],
			63 : [ 'OSC_BBE', 'OSC_ES', 'OSC_SES', 'OSC_UAS' ],
			64 : [ 'FEC_BEF_COR_ER', 'FEC_AFT_COR_ER' ],
			65 : []
		};
		return function() {
			var WDMParamenter = new Array();
			Ext.getCmp('WDMNumberic').items.each(function(item) {
					if (item.getValue()) {
						WDMParamenter.push.apply(WDMParamenter,
								pmStdIndex[item.inputValue]);
					}
			});
			return WDMParamenter;
		};
	}(),
	getWdmTp : function() {
		var tpLvl = new Array();
		var other = Ext.getCmp('WDMTPLevelOther').getValue();
		Ext.getCmp('WDMTPLevel').items.each(function(item) {
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
