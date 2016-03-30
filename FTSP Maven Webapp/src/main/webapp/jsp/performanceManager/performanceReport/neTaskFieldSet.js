
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
		inputValue : 21
	}, {
		xtype : 'checkbox',
		checked : true,
		boxLabel : '收光功率',
		inputValue : 22
	}, {
		xtype : 'checkbox',
		id : 'sdhPhyOther',
		checked : true,
		boxLabel : '其他',
		inputValue : 23
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
			inputValue : 31
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '复用段误码(B2)',
			inputValue : 32
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'VC4通道误码(B3)',
			inputValue : 33
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'VC3/VC12通道误码(B3/V5)',
			inputValue : 34
		}, {
			xtype : 'checkbox',
			checked : true,
			id : 'sdhNumOther',
			boxLabel : '其他',
			inputValue : 35
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

var WDMTPLevel = {
	id : 'WDMTPLevel',
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
};
var WDMTP = {
	xtype : 'fieldset',
	title : 'TP等级',
	height : 200,
	anchor : '95%',
	labelWidth : 10,
	items : WDMTPLevel
};

var WDMPhysical = {
		id : 'WDMPhysical',
		xtype : 'checkboxgroup',
		columns : 1,
		// anchor:'90%',
		items :  [ {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '发光功率',
			inputValue : 51
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '收光功率',
			inputValue : 52
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '信道信噪比',
			inputValue : 54
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '信道中心波长/偏移',
			inputValue : 55
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '信道光功率',
			inputValue : 56
		}, {
			xtype : 'checkbox',
			checked : true,
			id : 'wdmPhyOther',
			boxLabel : '其他',
			inputValue : 57
		} ]
	};

var WDMPhysicalField = {
	id : 'WDMPhysicalField',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	title : 'WDM物理量',
	items :WDMPhysical
};

var WDMNumberic = {
		id : 'WDMNumberic',
		xtype : 'checkboxgroup',
		columns : 1,
		// anchor:'90%',
		items :  [ {
			xtype : 'checkbox',
			checked : true,
			boxLabel : '光监控信道误码',
			inputValue : 63
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'FEC误码率',
			inputValue : 64
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'OTU误码',
			inputValue : 61
		}, {
			xtype : 'checkbox',
			checked : true,
			boxLabel : 'ODU误码',
			inputValue : 62
		}, {
			xtype : 'checkbox',
			boxLabel : '其他',
			checked : true,
			id : 'wdmNumOther',
			inputValue : 65
		} ]
	};

var WDMNumbericField = {
	id : 'WDMNumbericField',
	xtype : 'fieldset',
	labelWidth : 10,
	anchor : '95%',
	height : 200,
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
		items : [ WDMPhysicalField, {
			xtype : 'checkbox',
			boxLabel : '最大、最小值',
			id : 'WDMMaxMin',
			inputValue : 1
		} ]
	}, {
		columnWidth : 0.33,
		border : false,
		layout : 'form',
		items : [ WDMNumbericField ]
	} ]
};