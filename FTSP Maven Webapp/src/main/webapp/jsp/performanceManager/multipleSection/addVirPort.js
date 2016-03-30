/*
 * ! Ext JS Library 3.4.0 Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
// 端口名字
var nameValue = "";
// 端口id
var ids = "";
// ==================页面====================
var formPanel = new Ext.FormPanel({
			id : "formPanel",
			name : "formPanel",
			region : "center",
			// labelAlign: 'top',
			frame : false,
			// title: '新增用户',
			bodyStyle : 'padding:20px 10px 0',
			// labelWidth: 100,
			labelAlign : 'left',
			autoScroll : true,
			items : [{
				xtype : 'combo',
				id : 'virTypeCombo',
				name : 'virTypeCombo',
				fieldLabel : '虚拟端口类型',
				store : new Ext.data.ArrayStore({
							fields : ['value', 'displayName'],
							data : [['1', '自定义'], ['2', '衰耗值'], ['3', '空白行'],
									['4', '合并光功率']]
						}),
				displayField : "displayName",
				valueField : 'value',
				mode : 'local',
				triggerAction : 'all',
				editable : false,
				width : 250,
				listeners : {
					select : function(combo, record, index) {
						// 根据选择的值，重新组装界面
						var value = Ext.getCmp("virTypeCombo").getValue();
						// remove();
						if (value == "1") {
							// 自定义界面
							Ext.getCmp("virName").setVisible(true);
							Ext.getCmp("caculateDown").setVisible(false);
							Ext.getCmp("rows").setVisible(false);
							Ext.getCmp("direction").setVisible(false);
							Ext.getCmp("portSelect").setVisible(false);
							Ext.getCmp("formPanel").doLayout();
						} else if (value == "2") {
							// 衰耗值
							Ext.getCmp("virName").setVisible(true);
							Ext.getCmp("caculateDown").setVisible(true);
							Ext.getCmp("rows").setVisible(false);
							Ext.getCmp("direction").setVisible(false);
							Ext.getCmp("portSelect").setVisible(false);
							Ext.getCmp("formPanel").doLayout();
						} else if (value == "3") {
							Ext.getCmp("virName").setVisible(false);
							Ext.getCmp("caculateDown").setVisible(false);
							Ext.getCmp("rows").setVisible(true);
							Ext.getCmp("direction").setVisible(false);
							Ext.getCmp("portSelect").setVisible(false);
							Ext.getCmp("formPanel").doLayout();
						} else if (value == "4") {
							Ext.getCmp("virName").setVisible(true);
							Ext.getCmp("caculateDown").setVisible(false);
							Ext.getCmp("rows").setVisible(false);
							Ext.getCmp("direction").setVisible(true);
							Ext.getCmp("portSelect").setVisible(true);
							Ext.getCmp("formPanel").doLayout();
						} else {

						}
					}
				}
			}, {
				xtype : 'textfield',
				id : "virName",
				name : "virName",
				width : 250,
				fieldLabel : '虚拟端口显示名',
				hidden : true

			}, {
				xtype : 'numberfield',
				id : "caculateDown",
				name : "caculateDown",
				width : 250,
				fieldLabel : '衰耗理论值',
				sideText : 'dB',
				hidden : true
			}, {
				xtype : 'combo',
				id : "rows",
				name : "rows",
				fieldLabel : '行数',
				width : 250,
				store : new Ext.data.ArrayStore({
							fields : ['value', 'displayName'],
							data : [['1', '1'], ['2', '2'], ['3', '3'],
									['4', '4'], ['5', '5'], ['6', '6'],
									['7', '7'], ['8', '8'], ['9', '9'],
									['10', '10']]
						}),
				displayField : "displayName",
				valueField : 'value',
				mode : 'local',
				hidden : true,
				triggerAction : 'all',
				editable : false

			}, new Ext.form.RadioGroup({
						fieldLabel : '合并光功率源',
						id : 'direction',
						width : 250,
						hidden : true,
						name : 'direction',
						items : [{
									name : 'direction',
									inputValue : '1',
									boxLabel : '输入',
									checked : true

								}, {
									name : 'direction',
									inputValue : '2',
									boxLabel : '输出'

								}]

					}), new Ext.form.TextArea({
						id : "portSelect",
						name : "portSelect",
						hidden : true,
						width : 250,
						disabled:true,
						fieldLabel : '端口选择',
						sideText : '<div id = "damnButton" ></div>'

					})],
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
	var win = parent.Ext.getCmp('addVirtualPortLeftForwardWindow');
	if (win) {
		win.close();
	}
}

function save() {
	var virType = Ext.getCmp("virTypeCombo").getValue();
	if (virType == 1) {
		// 自定义
		var virName = Ext.getCmp("virName").getValue();
		if (virName == "") {
			Ext.Msg.alert('提示', '虚拟端口显示名不能为空！');
			return;
		}

	} else if (virType == 2) {
		// 衰耗值
		var virName = Ext.getCmp("virName").getValue();
		if (virName == "") {
			Ext.Msg.alert('提示', '虚拟端口显示名不能为空！');
			return;
		}
		var caculateDown = Ext.getCmp("caculateDown").getValue();

	} else if (virType == 3) {
		// 空白行
		var rows = Ext.getCmp("rows").getValue();
	} else if (virType == 4) {
		// 合并光功率
		var virName = Ext.getCmp("virName").getValue();
		if (virName == "") {
			Ext.Msg.alert('提示', '虚拟端口显示名不能为空！');
			return;
		}
		var direction = Ext.getCmp("direction").getValue().inputValue;
		if (ids == "") {
			Ext.Msg.alert('提示', '端口不能为空！');
			return;
		}

	}
	parent.saveVirtualPortLeftForward(virName+"(虚拟)", caculateDown, rows, direction,
			ids, virType,portType,type);
	parent.Ext.getCmp('addVirtualPortLeftForwardWindow').close();

}
// 定义刷新方法，虚拟端口赋值
var refrshForward = function(result) {
	nameValue = "";
	ids = "";
	for (var i = 0; i < result.length; i++) {
		if (result[i].nodeLevel == 8) {
			var portId = result[i].nodeId
			// 判断选择是否跨网元
			var jsonString = new Array();
			var map = {
				"BASE_PTP_ID" : portId
			};
			jsonString.push(map);

			var jsonData = {
				"jsonString" : Ext.encode(jsonString)
			};
			Ext.Ajax.request({
						url : 'multiple-section!selecrPtpName.action',
						method : 'POST',
						params : jsonData,
						success : function(response) {
							var obj = Ext.decode(response.responseText);
							// 查询成功以后，record赋值
							nameValue += obj.ptpName + "\n";
							ids += portId + ","
							Ext.getCmp('portSelect').setValue(nameValue);
						},
						error : function(response) {
							Ext.Msg.alert('错误', '保存失败！');
						},
						failure : function(response) {
							Ext.Msg.alert('错误', '保存失败！');
						}

					});

		}
	}
	parent.Ext.getCmp('addNeForwardWindow').close();

}
parent.refrshForward = refrshForward;
function selectPort() {
	var url = "addTree.jsp?level=8&type=5&checkModel=multiple&rootType=4&rootId="
			+ neId + "&rootVisible=" + true;
	addNeForwardWindow = new parent.Ext.Window({
				id : 'addNeForwardWindow',
				title : '端口选择',
				// width : 210,
				autoWidth : true,
				height : 400,
				// layout : 'fit',
				plain : false,
				modal : true,
				resizable : false,
				// closeAction : 'hide',// 关闭窗口
				bodyStyle : 'padding:1px;',
				buttonAlign : 'center',
				html : '<iframe src='
						+ url
						+ ' height="100%" width="100%" frameborder=0 border=0/>'

			});
	addNeForwardWindow.show();
	// 调节高度
	if (addNeForwardWindow.getHeight() > parent.Ext.getCmp('win').getHeight()) {
		addNeForwardWindow
				.setHeight(parent.Ext.getCmp('win').getHeight() * 0.7);
	} else {
		addNeForwardWindow.setHeight(addNeForwardWindow.getInnerHeight());
	}
	// 调节宽度
	if (addNeForwardWindow.getWidth() > parent.Ext.getCmp('win').getWidth()) {
		addNeForwardWindow.setWidth(parent.Ext.getCmp('win').getWidth() * 0.7);
	} else {
		addNeForwardWindow.setWidth(addNeForwardWindow.getInnerWidth());
	}
	addNeForwardWindow.center();
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
(function() {
		// renderTo: "damnButton",
		var btn = new Ext.Button({
					id : "dam",
					name : "dam",
					width : 5,
					text : "...",
					renderTo : "damnButton",
					clickEvent : 'click',
					handler : function() {
						selectPort();
					}
				});
	}).defer(25);
});