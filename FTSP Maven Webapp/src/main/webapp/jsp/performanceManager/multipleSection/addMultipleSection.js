/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */

// 光复用段
var section = {
	xtype : 'textfield',
	id : 'section',
	fieldLabel : "光复用段名称",
	name : 'section',
	sideText : '<font color=red>*</font>',
	width : 150,
	maxLength : 256,
	allowBlank : true,
	listeners : {
		'blur' : function() {
			Ext.getCmp("section").setValue(Ext.getCmp("section").getValue().replace(/\s/ig,''));
		}
	}
				
}
// 方向
var direction = new Ext.form.RadioGroup({
			fieldLabel : '方向',
			id : 'direction',
			name : 'direction',
			items : [{
						name : 'direction',
						inputValue : '1',
						boxLabel : '单向',
						checked : true

					}, {
						name : 'direction',
						inputValue : '2',
						boxLabel : '双向'

					}]

		})

// 标称波道数
var std_wave = new Ext.form.NumberField({
			id : 'std_wave',
			fieldLabel : "标称波道数",
			emptyText : "1～320",
			sideText : '<font color=red>*</font>',
			name : 'std_wave',
			width : 150,
			allowBlank : true
		})
// 实际波道数
var act_wave = new Ext.form.NumberField({
			id : 'act_wave',
			fieldLabel : "实际波道数",
			emptyText : "0～320",
			name : 'act_wave',
			sideText : '<font color=red>*</font>',
			width : 150,
			allowBlank : true
		})

// 路由建立
var routeType = new Ext.form.RadioGroup({
			fieldLabel : '路由建立',
			id : 'routeType',
			name : 'routeType',
			items : [{
						name : 'routeType',
						inputValue : '1',
						boxLabel : '立即'

					}, {
						name : 'routeType',
						inputValue : '2',
						boxLabel : '稍后',
						checked : true

					}, {
						name : 'routeType',
						inputValue : '3',
						boxLabel : '向导'

					}]

		})
// ==================页面====================
var formPanel = new Ext.FormPanel({
			region : "center",
			// labelAlign: 'top',
			frame : false,
			// title: '新增用户',
			bodyStyle : 'padding:20px 10px 0',
			// labelWidth: 100,
			labelAlign : 'left',
			autoScroll : true,
			items : [{
				layout : 'column',
				border : false,
				items : [{
					layout : 'form',
					border : false,
					labelSeparator : "：",
					items : [emsGroupCombo, emsCombo, trunkCombo, section,
							direction, std_wave, act_wave, routeType]
				}]
			}],
			buttons : [{
						text : '确定',
						handler : save
					}, {
						text : '取消',
						handler : close
					}]
		});

// =================函数===================
function close() {
	var win = parent.Ext.getCmp('addMultipleSectionWindow');
	if (win) {
		win.close();
	}
}

function save() {

	// 判断参数是否为空
	var groupId = Ext.getCmp("emsGroup").getValue();
	if (groupId.length < 1) {
		Ext.Msg.alert('提示', '网管分组不能为空！');
		return;
	}
	var emsId = Ext.getCmp("ems").getValue();
	if (emsId.length < 1) {
		Ext.Msg.alert("提示", "网管不能为空！");
		return;
	}
	var trunkLine = Ext.getCmp("trunkLine").getValue();
	if (trunkLine.length < 1) {
		Ext.Msg.alert("提示", "干线名称不能为空！");
		return;
	}
	var section = Ext.getCmp("section").getValue();
	if (section.length < 1) {
		Ext.Msg.alert("提示", "光复用端名称不能为空！");
		return;
	}

	var std_wave = Ext.getCmp("std_wave").getValue();
	if (std_wave.length < 1) {
		Ext.Msg.alert("提示", "标称波道数不能为空！");
		return;
	}

	var act_wave = Ext.getCmp("act_wave").getValue();
	if (act_wave.length < 1) {
		Ext.Msg.alert("提示", "实际波道数不能为空！");
		return;
	}

	if (act_wave > std_wave) {
		Ext.Msg.alert("提示", "实际波道数不能大于标称波道数！");
		return;
	}
	var direction = Ext.getCmp("direction").getValue().inputValue;
	var jsonString = new Array();
	var map = {
		"SEC_NAME" : section.replace(/\s/ig,''),
		"STD_WAVE" : std_wave,
		"DIRECTION" : direction,
		"PM_TRUNK_LINE_ID" : trunkLine,
		"ACTULLY_WAVE" : act_wave
	};
	jsonString.push(map);

	var jsonData = {
		"jsonString" : Ext.encode(jsonString)
	};
	Ext.Ajax.request({
		url : 'multiple-section!addMultipleSection.action',
		method : 'POST',
		params : jsonData,
		success : function(response) {// 回调函数
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 1) {
				//Ext.Msg.alert("提示", obj.returnMessage, function(r) {

				// 刷新列表
				var pageTool = parent.Ext.getCmp('pageTool');
				if (pageTool) {
					pageTool.doLoad(pageTool.cursor);
				}
				var routeType = Ext.getCmp("routeType").getValue().inputValue;

				// 如果是立即生成，则切换到选择路由方式界面
				if (routeType == "1") {
					parent.addRouteType(obj.returnId, obj.returnName, Ext
									.getCmp("ems").getValue());
					// 关闭修改任务信息窗口
					var win = parent.Ext.getCmp('addMultipleSectionWindow');
					if (win) {
						win.close();
					}
				} else {
					Ext.Msg.confirm('信息', '继续添加？', function(btn) {
								if (btn == 'yes') {

								} else {
									// 关闭修改任务信息窗口
									var win = parent.Ext
											.getCmp('addMultipleSectionWindow');
									if (win) {
										win.close();
									}
								}
							});
				}

				//});
			}
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}

		},
		error : function(response) {
			Ext.Msg.alert('错误', '保存失败！');
		},
		failure : function(response) {
			Ext.Msg.alert('错误', '保存失败！');
		}

	});

}

Ext.onReady(function() {
	Ext.Msg = parent.Ext.Msg;
	Ext.BLANK_IMAGE_URL = "../../../resource/ext/resources/images/default/s.gif";
	document.onmousedown = function() {
		top.Ext.menu.MenuMgr.hideAll();
	}
	Ext.QuickTips.init(); // 开启悬停提示
	Ext.form.Field.prototype.msgTarget = 'side'; // 提示显示风格
	var win = new Ext.Viewport({
				id : 'win',
				loadMask : true,
				layout : 'border',
				items : [formPanel],
				renderTo : Ext.getBody()
			});
});