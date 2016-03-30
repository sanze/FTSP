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
			var emsConnectionId = combo.getValue();

			var jsonData = {
				"userId" : userId,
				"emsGroupId" : emsConnectionId,
				"limit" : 200
			};

			Ext.Ajax.request({
						url : 'circuit!getAllEMSTask.action',
						method : 'POST',
						params : jsonData,

						success : function(response) {// 回调函数

							var obj = Ext.decode(response.responseText);
							if (obj.returnResult == 0) {
								Ext.Msg.alert("提示", obj.returnMessage);
								// store.rejectChanges() ;
							} else {
								store.proxy = new Ext.data.HttpProxy({
											url : 'circuit!getAllEMSTask.action'
										});
								store.baseParams = jsonData;
								store.load();

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
	}
});
