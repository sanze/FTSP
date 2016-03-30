/**
 * 创建网管分组数据源
 */

var emsGroupStore = new Ext.data.Store({
			// 获取数据源地址
			proxy : new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching:
				// false，
				url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
				disableCaching : false
					// 是否禁用缓存，设置false禁用默认的参数_dc
			}),
			baseParams : {
				"displayAll" : true,
				"displayNone" : true
			},
			// record格式
			reader : new Ext.data.JsonReader({
						root : 'rows',// json数据的key值
						fields : ['BASE_EMS_GROUP_ID', 'GROUP_NAME']
					})
		});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
emsGroupStore.load({
			// 回调函数
			callback : function(records, options, success) {// records：加载的数据数组
				// ，options:调用load方法的配置对象
				// ，success:
				// 布尔值，是否加载成功
				// 获取下拉框的第一条记录
				var firstValue = records[0].get('BASE_EMS_GROUP_ID');
				// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
				Ext.getCmp('emsGroup').setValue(firstValue);
			}
		});
// 创建网管分组下拉框
var emsGroupCombo = new Ext.form.ComboBox({
			id : 'emsGroup',
			fieldLabel : '网管分组',
			store : emsGroupStore,// 数据源
			valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
			displayField : 'GROUP_NAME',// 下拉框显示值
			editable : false,
			triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
			width : 150,
			resizable: true,
			listeners : {// 监听事件
				select : function(combo, record, index) {
					var emsGroupId = combo.getValue();
					// 还原网管下拉框
					Ext.getCmp('ems').reset();
					Ext.getCmp('trunkLine').reset();
					// 动态改变网管数据源的参数
					emsStore.baseParams = {
						'emsGroupId' : emsGroupId,
						"displayAll" : false
					};
					// 加载网管数据源
					emsStore.load({
								callback : function(records, options, success) {// records：加载的数据数组
									// ，options:调用load方法的配置对象
									// ，success:
									// 布尔值，是否加载成功
									if (!success) {
										Ext.Msg.alert('错误', '查询失败！请重新查询');
									} else {
										// // 获取下拉框的第一条记录
										// var firstValue =
										// records[0].get('BASE_EMS_CONNECTION_ID');
										// //
										// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
										// Ext.getCmp('emsCombo').setValue(firstValue);
									}
								}
							});
				}
			}
		});

/**
 * 创建网管数据源
 */
var emsStore = new Ext.data.Store({
			url : 'common!getAllEmsByEmsGroupId.action',
			baseParams : {
				'emsGroupId' : -99,
				"displayAll" : false
			},
			reader : new Ext.data.JsonReader({
						root : 'rows',
						fields : ['BASE_EMS_CONNECTION_ID', 'DISPLAY_NAME']
					})
		});

/**
 * 加载网管数据源
 */
emsStore.load({
			// 回调函数
			callback : function(records, options, success) {// records：加载的数据数组
				// ，options:调用load方法的配置对象
				// ，success:
				// 布尔值，是否加载成功
				// var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
				// Ext.getCmp('emsCombo').setValue(firstValue);
			}
		});

/**
 * 创建网管下拉框
 */
var emsCombo = new Ext.form.ComboBox({
			id : 'ems',
			fieldLabel : '网管',
			store : emsStore,
			valueField : 'BASE_EMS_CONNECTION_ID',
			displayField : 'DISPLAY_NAME',
			editable : false,
			triggerAction : 'all',
			width : 150,
			resizable: true,
			listeners : {// 监听事件
				select : function(combo, record, index) {
					var emsId = combo.getValue();
					// 还原网管下拉框
					Ext.getCmp('trunkLine').reset();
					// 动态改变网管数据源的参数
					var jsonString = new Array();
					var map = {
						"BASE_EMS_CONNECTION_ID" : emsId
					};
					jsonString.push(map);
					var jsonData = {
						"jsonString" : Ext.encode(jsonString)
					};
					trunkLineStore.proxy = new Ext.data.HttpProxy({
								url : 'multiple-section!selectTrunkLine.action'
							});
					trunkLineStore.baseParams = jsonData;
					trunkLineStore.load({
								callback : function(r, options, success) {
									if (success) {
										// 将网管选择的值设为空
									} else {
										Ext.Msg.alert('错误', '查询失败！请重新查询');
									}
								}
							});

				}
			}
		});

var trunkLineStore = new Ext.data.Store({
			url : 'multiple-section!selectTrunkLine.action',
			reader : new Ext.data.JsonReader({
						totalProperty : 'total',
						root : "rows"
					}, ["DISPLAY_NAME", "PM_TRUNK_LINE_ID"])
		});
// trunkLineStore.load();

// 干线下拉
var trunkCombo = new Ext.form.ComboBox({
			id : 'trunkLine',
			name : 'trunkLine',
			fieldLabel : '干线',
			store : trunkLineStore,
			displayField : "DISPLAY_NAME",
			valueField : 'PM_TRUNK_LINE_ID',
			triggerAction : 'all',
			editable : false,
			resizable: true,
			width : 150
		});
