var NX_SYS_TYPE_DATA = [];
var SYS_MENU = [];
NX_SYS_TYPE_DATA.push([ -99, "全部" ]);
for ( var i = 0; i < NX_SYS_TYPE.length; i++) {
	NX_SYS_TYPE_DATA.push([ NX_SYS_TYPE[i].key, NX_SYS_TYPE[i].value ]);
	SYS_MENU.push({
		text : NX_SYS_TYPE[i].value,
		id : NX_SYS_TYPE[i].key,
		handler : function() {
			addSys(this.id);
		}
	});
};

sysTypeStore = new Ext.data.ArrayStore({
	id : 0,
	fields : [ 'key', 'value' ]
});

sysTypeStore.loadData(NX_SYS_TYPE_DATA);
// 系统类型的下拉框
var sysTypeCombo = new Ext.form.ComboBox({
	id : 'sysTypeCombo',
	typeAhead : true,
	triggerAction : 'all',
	mode : 'local',
	width : 120,
	store : sysTypeStore,
	valueField : 'key',
	displayField : 'value'
});

// renderer
function sysTypeRenderer(v) {
	for ( var i = 0; i < NX_SYS_TYPE.length; i++) {
		if (v == NX_SYS_TYPE[i]['key'])
			return NX_SYS_TYPE[i]['value'];
	}
	return v;
};

// grid

var sm = new Ext.grid.CheckboxSelectionModel({
// singleSelect : true
});
var cm = new Ext.grid.ColumnModel({
	// specify any defaults for each column
	defaults : {
		sortable : true,
		width : 100
	// columns are not sortable by default
	},
	columns : [ new Ext.grid.RowNumberer({
		width : 26
	}), sm, {
		id : 'GROUP_NAME',
		header : '网管分组',
		dataIndex : 'GROUP_NAME'
	}, {
		id : 'EMS_DISPLAY_NAME',
		header : '网管',
		dataIndex : 'EMS_DISPLAY_NAME'
	}, {
		id : 'SYS_NAME',
		header : '系统名称',
		dataIndex : 'SYS_NAME'
	}, {
		id : 'SYS_CAPACITY',
		header : '系统容量',
		dataIndex : 'SYS_CAPACITY'
	}, {
		id : 'SYS_TYPE',
		header : '系统类型',
		dataIndex : 'SYS_TYPE',
		width : 200,
		renderer : sysTypeRenderer
	} ]
});

//function
