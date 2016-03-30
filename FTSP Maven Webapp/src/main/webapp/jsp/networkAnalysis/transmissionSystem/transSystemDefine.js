/**
 * 系统速率数据
 */
var sysRateMapping = [ [ 1, '2M' ], [ 2, '34M' ], [ 3, '140M' ], [ 4, '155M' ],
			[ 5, '622M' ], [ 6, '2.5G' ], [ 7, '10G' ], [ 8, '40G' ],
			[ 9, '100M' ], [ 10, '1000M' ], [ 11, '10000M' ] ];
/**
 * 技术体制数据
 */
var domainMapping = [ [ 1, 'SDH' ], [ 2, 'WDM' ], [ 3, 'MSTP' ], [ 4, 'MSAP' ],
		[ 5, 'ASON' ], [ 6, 'PDH' ] ];
/**
 * 拓扑结构数据
 */
var structureMapping = [ [ 1, '环' ], [ 2, '链' ], [ 3, '其他' ] ];
/**
 * 网络层次数据
 */
var netLevelMapping = [ [ 1, '骨干层' ], [ 2, '汇聚层' ], [ 3, '接入层' ], [ 4, '一干' ],
		[ 5, '二干' ] ];
/**
 * 保护模式数据
 */
var proTypeMapping = [ [ 1, '1+1 MSP' ], [ 2, '1:N MSP' ], [ 3, '2F BLSR' ],
		[ 4, '4F BLSR' ], [ 5, '1+1 ATM' ], [ 6, '1:N ATM' ], [ 7, 'SNCP' ], [ 100, '无' ] ];
/**
 * 生成方式数据
 */
var genMethodMapping = [ [ 1, '自动' ], [ 2, '手动' ] ];
/**
 * 传输介质数据
 */
var transMediumMapping = [ [ 1, '光' ], [ 2, '电' ], [ 3, '微波' ] ];
/**
 * 状态数据
 */
var statusMapping = [ [ 1, '存在' ], [ 2, '不存在' ] ];
/**
 * LINK方向数据
 */
var linkDirectionMapping = [ [ 0, '单向' ], [ 1, '双向' ] ];
/**
 * LINK是否手工生成数据
 */
var isManualMapping = [ [ 0, '非手工' ], [ 1, '手工' ] ];

/**
 * 系统名称输入框
 */
var sysName = new Ext.form.TextField({
	id : 'sysName',
	fieldLabel : '系统名称',
	anchor : '90%',
	maxLength : 128
});

/**
 * 系统代号输入框
 */
var sysCode = new Ext.form.TextField({
	id : 'sysCode',
	fieldLabel : '系统代号',
	anchor : '90%',
	maxLength : 128
});

/**
 * 备注输入框
 */
var note = new Ext.form.TextField({
	id : 'note',
	fieldLabel : '备注',
	anchor : '90%',
	maxLength : 128
});

/**
 * 波道数输入框
 */
var waveCount = new Ext.form.NumberField({
	id : 'waveCount',
	fieldLabel : '波道数',
	anchor : '90%',
	maxLength : 128,
	minValue:0,
	decimalPrecision:0
});


/**
 * 系统速率下拉框
 */
var sysRate;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'rate'
		} ]
	});
	store.loadData(sysRateMapping);
	sysRate = new Ext.form.ComboBox({
		id : 'sysRate',
		fieldLabel : '系统速率',
		mode : 'local',
		store : store,
		displayField : 'rate',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();


/**
 * 技术体制下拉框
 */
var domain;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'domain'
		} ]
	});
	store.loadData(domainMapping);
	domain = new Ext.form.ComboBox({
		id : 'domain',
		fieldLabel : '技术体制',
		mode : 'local',
		store : store,
		displayField : 'domain',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();


/**
 * 拓扑结构下拉框
 */
var structure;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'structure'
		} ]
	});
	store.loadData(structureMapping);
	structure = new Ext.form.ComboBox({
		id : 'structure',
		fieldLabel : '拓扑结构',
		mode : 'local',
		store : store,
		displayField : 'structure',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();


/**
 * 网络层次下拉框
 */
var netLevel;
(function(){
	var store = new Ext.data.ArrayStore({
		fields : [ {name:'id',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:[]
	});
	store.loadData(NET_LEVEL,true);
	netLevel = new Ext.form.ComboBox({
		id : 'netLevel',
		fieldLabel : top.FieldNameDefine.NET_LEVEL_NAME,
		mode : 'local',
		store : store,
		displayField : 'displayName',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();


/**
 * 保护类型下拉框
 */
var proType;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {name:'id',mapping:'key'}, {name:'displayName',mapping:'value'} ],
		data:[]
	});
	store.loadData(PRO_GROUP_TYPE,true);
	store.sort('id');
	proType = new Ext.form.ComboBox({
		id : 'proType',
		fieldLabel : '保护类型',
		mode : 'local',
		store : store,
		displayField : 'displayName',
		valueField : 'id',
		value:0,
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();



/**
 * 生成方式下拉框
 */
var genMethod;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'genMethod'
		} ]
	});
	store.loadData(genMethodMapping);
	genMethod = new Ext.form.ComboBox({
		id : 'genMethod',
		fieldLabel : '生成方式',
		mode : 'local',
		store : store,
		displayField : 'genMethod',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();

/**
 * 传输介质下拉框
 */
var transMedium;
(function() {
	var store = new Ext.data.ArrayStore({
		fields : [ {
			name : 'id'
		}, {
			name : 'transMedium'
		} ]
	});
	store.loadData(transMediumMapping);
	transMedium = new Ext.form.ComboBox({
		id : 'transMedium',
		fieldLabel : '传输介质',
		mode : 'local',
		store : store,
		displayField : 'transMedium',
		valueField : 'id',
		triggerAction : 'all',
		editable : false,
		anchor : '90%',
		resizable : true
	});
})();


/*---------------------------------------*/


/* -------------各种渲染器---------- */

function domainRenderer(v, m, r) {
	return (typeof v == 'number' && domainMapping[v - 1] != null) ? domainMapping[v - 1][1] : v;
}
function structureRenderer(v, m, r) {
	return (typeof v == 'number' && structureMapping[v - 1] != null) ? structureMapping[v - 1][1] : v;
}
var proTypeRenderer;
if(!!PRO_GROUP_TYPE){
	proTypeRenderer = function (v) {
		for(var i=0;i<PRO_GROUP_TYPE.length;i++){
			if(v==PRO_GROUP_TYPE[i]['key'])
				return PRO_GROUP_TYPE[i]['value'];
		}
		if (typeof v == 'number' && v == 99)
			return '无';
		return v;
	};
}else{
	proTypeRenderer = function (v, m, r) {
		if (typeof v == 'number' && v == 99)
			return '无';
		else
			return (typeof v == 'number' && proTypeMapping[v][1] != null) ? proTypeMapping[v][1] : v;
	}
}

function genMethodRenderer(v, m, r) {
	return (typeof v == 'number' && genMethodMapping[v - 1] != null) ? genMethodMapping[v - 1][1] : v;
}
function transMediumRenderer(v, m, r) {
	return (typeof v == 'number' && transMediumMapping[v - 1] != null) ? transMediumMapping[v - 1][1] : v;
}
var netLevelRenderer;
if(!!NET_LEVEL){
	netLevelRenderer = function (v) {
		for(var i=0;i<NET_LEVEL.length;i++){
			if(v==NET_LEVEL[i]['key'])
				return NET_LEVEL[i]['value'];
		}
		return v;
	};
}else{
	netLevelRenderer = function (v, m, r) {
		return (typeof v == 'number' && netLevelMapping[v - 1] != null) ? netLevelMapping[v - 1][1] : v;
	};
}
//function netLevelRenderer(v, m, r) {
//	return (typeof v == 'number') ? netLevelMapping[v+1] : v;
//}
function statusRenderer(v, m, r) {
	return (typeof v == 'number' && statusMapping[v - 1] != null) ? statusMapping[v - 1][1] : v;
}
function linkDirectionRenderer(v, m, r) {
	return (typeof v == 'number') ? linkDirectionMapping[v][1] : v;
}
function isManualRenderer(v, m, r) {
	return (typeof v == 'number') ? isManualMapping[v][1] : v;
}

/* ----------------------- */
function notAllowBlank(o){
	o.sideText='<font color=red>*</font>';
	o.allowBlank=false;
}


/* IE 呵呵*/
if(!Array.prototype.every){
	Array.prototype.every = function(callback){
		 for(var i = 0;i<this.length;i++){  
			 if(!callback(this[i])){
				 return false;
			 }
		 }
		 return true;
	}
}