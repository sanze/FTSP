//全局变量
var faultIdGlobal;
var typeGlobal;

//故障类别
var faultTypeMap = [[0, '全部'], [1, '设备故障'], [2, '线路故障']];

//故障状态
var statusData = [[0, '全部'], [1, '未确认'], [2, '已确认'], [3, '故障恢复'], [4, '已归档']];
var statusStore = new Ext.data.ArrayStore({
	fields : [ {
		name : 'value'
	}, {
		name : 'displayName'
	} ]
});
statusStore.loadData(statusData);

var statusCombo = new Ext.form.ComboBox({
	id : 'statusCombo',
	store : statusStore,
	displayField : 'displayName',
	valueField : 'value',
	triggerAction : 'all',
	mode : 'local',
	editable : false,
	allowBlank : false,
	value : 0,
	width : 100
});

//厂家
var factoryMap = {
	'1' : '华为',
	'2' : '中兴',
	'3' : '朗讯',
	'4' : '烽火',
	'9' : '富士通'
};

//告警级别
var severityData = [[1, '紧急'], [2, '重要'], [3, '次要'], [4, '提示']];

//准确性
var accuracyData = [[1, '未知'],[2, '准确'], [3, '部分准确'], [4, '不准确']];

//告警收敛
var convergeData = [[1, '未收敛告警'], [2, '主告警'], [3, '衍生告警']];

function faultTypeRenderer(v, m , r) {
	return (typeof v == 'number' && faultTypeMap[v] != null) ? faultTypeMap[v][1] : v;
}
function statusRenderer(v, m, r) {
	return (typeof v == 'number' && statusData[v] != null) ? statusData[v][1] : v;
}
function accuracyRenderer(v, m, r) {
	return (typeof v == 'number' && accuracyData[v-1] != null) ? accuracyData[v-1][1] : v;
}
function convergeRenderer(v, m, r) {
	return (typeof v == 'number' && convergeData[v-1] != null) ? convergeData[v-1][1] : convergeData[0][1];
}
function severityRenderer(v, m, r) {
	return (typeof v == 'number' && severityData[v-1] != null) ? severityData[v-1][1] : v;
}




