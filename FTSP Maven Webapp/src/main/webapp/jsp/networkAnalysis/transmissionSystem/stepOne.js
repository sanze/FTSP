var area = new Ext.ux.AreaSelector({
	fieldLabel : '所属'+top.FieldNameDefine.AREA_NAME,
	id:'area',
	buttonOffset : 5,
	anchor : '90%',
	targetControl:false,
	style:'margin-bottom: 20px'
});
// 设置不能为空的项目
notAllowBlank(sysName);
notAllowBlank(sysCode);
notAllowBlank(structure);
notAllowBlank(domain);
notAllowBlank(transMedium);
notAllowBlank(proType);
notAllowBlank(sysRate);
notAllowBlank(genMethod);
notAllowBlank(netLevel);
notAllowBlank(note);
genMethod.value=2;
genMethod.disabled=true;

var stepOne = new Ext.form.FormPanel({
	id : 'stepOne',
	title:'系统属性',
	autoScroll:true,
	trackResetOnLoad:true,
	border : false,
	bodyStyle:'padding:50px 100px',
	align:"center",
	defaults:{
		style:{marginBottom: '5px'},
		anchor:'95%'
	},
	items : [ sysName, sysCode, area,{xtype:'spacer',height:1}, domain, structure, transMedium,
				proType, sysRate, genMethod, waveCount, netLevel, note ]
});