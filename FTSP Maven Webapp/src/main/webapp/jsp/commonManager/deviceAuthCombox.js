/**
 * 创建网管分组数据源
 */
var emsGroupStore = new Ext.data.Store({
	// 获取数据源地址
	proxy: new Ext.data.HttpProxy({// 如果请求没有参数，则把url定义在proxy属性里，并接设置disableCaching: false，
		url : 'common!getAllEmsGroups.action', // 否则会默认传一个_dc的参数，后台报错，除非在后台给_dc加上set方法
		disableCaching: false// 是否禁用缓存，设置false禁用默认的参数_dc
	}),
	//设置默认值
	baseParams : {"displayAll" : true,"displayNone" : true,"authDomain":false},
	// record格式
	reader : new Ext.data.JsonReader({
		root : 'rows',//json数据的key值
		fields :['BASE_EMS_GROUP_ID','GROUP_NAME']
	})
});
// 访问地址，加载数据(如果没有这一句，则不会去后台查询)
//emsGroupStore.load({
//	// 回调函数
//	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
//		// 获取下拉框的第一条记录
//		var firstValue = records[0].get('BASE_EMS_GROUP_ID');
//		if(firstValue<0){
//			// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
//			Ext.getCmp('emsGroupCombo').setValue(records[0]);
//		}
//	}
//});
// 创建网管分组下拉框
var emsGroupCombo = new Ext.form.ComboBox({
	id : 'emsGroupCombo',
	fieldLabel : '网管分组',
	store : emsGroupStore,// 数据源
	valueField : 'BASE_EMS_GROUP_ID',// 下拉框实际值
	displayField : 'GROUP_NAME',// 下拉框显示值
	editable : false,
	triggerAction : 'all',// 每次加载所有值，否则下拉框选择一个值后，再点击就只有一个值
	width :100,
	listWidth :150,
	resizable: true,
	listeners : {// 监听事件
		select : function(combo, record, index) {
			var emsGroupId = combo.getValue();
			if(emsGroupId){
				emsCombo.enable();
			// 还原网管下拉框
				emsCombo.reset();
				// 还原网元下拉框
				neCombo.reset();
				neStore.removeAll();
			// 动态改变网管数据源的参数
				emsStore.baseParams.emsGroupId = emsGroupId;
//				emsStore.baseParams = {'emsGroupId':emsGroupId};
//				// 加载网管数据源
			emsStore.load({
				callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
					if (!success) {
							Ext.Msg.alert('错误', '查询失败！');
					}else{
//						// 获取下拉框的第一条记录
//						var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
//						// 设置下拉框默认值(这里直接设记录的BASE_EMS_GROUP_ID值，自动会显示和BASE_EMS_GROUP_ID对应的GROUP_NAME)
//						Ext.getCmp('emsCombo').setValue(firstValue);
					}
				}
			});
			}else{
				emsCombo.disable();
				neCombo.disable();
			}

		}
	}
});


/**
 * 创建网管数据源
 */
var emsStore = new Ext.data.Store({
	url : 'common!getAllEmsByEmsGroupId.action',
	baseParams : {"authDomain":false},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_EMS_CONNECTION_ID','DISPLAY_NAME','CONNETION_TYPE']
	})
});

/**
 * 加载网管数据源
 */
//emsStore.load({
//	// 回调函数
//	callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
////		var firstValue = records[0].get('BASE_EMS_CONNECTION_ID');
////		Ext.getCmp('emsCombo').setValue(firstValue);
//	}
//});

/**
 * 创建网管下拉框
 */
var emsCombo = new Ext.form.ComboBox({
	id : 'emsCombo',
	fieldLabel : '网管',
	store : emsStore,
	valueField : 'BASE_EMS_CONNECTION_ID',
	displayField : 'DISPLAY_NAME',
	editable : false,
	triggerAction : 'all',
	disabled:true,
	width :100,
	listWidth :150,
	resizable: true,
	listeners : {// 监听事件
		select : function(combo, record, index) {
			var emsId = combo.getValue();
			if(emsId){
				neCombo.enable();
			// 还原网管下拉框
				neCombo.reset();
			// 动态改变网管数据源的参数
				neStore.baseParams.emsId = emsId;
//				neStore.baseParams = {'emsId':emsId,"displayAll" : true};
			// 加载网管数据源
			neStore.load({
				callback : function(records,options,success){// records：加载的数据数组 ，options:调用load方法的配置对象 ，success: 布尔值，是否加载成功
					if (!success) {
							Ext.Msg.alert('错误', '查询失败！');
					}else{
//						// 获取下拉框的第一条记录
//						var firstValue = records[0].get('BASE_NE_ID');
//						Ext.getCmp('neCombo').setValue(firstValue);
					}
				}
			});
			}else{
				neCombo.disable();
			}
		}
	}
});


/**
 * 创建网元数据源
 */
var neStore = new Ext.data.Store({
	url : 'common!getAllNeByEmsId.action',
	//默认值
	baseParams : {"displayAll":false},
	reader : new Ext.data.JsonReader({
		root : 'rows',
		fields : ['BASE_NE_ID','DISPLAY_NAME']
	})
});

///**
// * 加载网元数据源
// */
//neStore.load({
//	callback : function(records,options,success){
////		var firstValue = records[0].get('BASE_NE_ID');
////		Ext.getCmp('neCombo').setValue(firstValue);
//	}
//});

/**
 * 创建网元下拉框
 */ 
var neCombo = new Ext.form.ComboBox({
	id : 'neCombo',
	fieldLabel : '网元',
	store : neStore,
	valueField : 'BASE_NE_ID',
	displayField : 'DISPLAY_NAME',
	disabled:true,
//	emptyText : '全部',
//	listEmptyText : '未找到匹配的结果',
	loadingText : '搜索中...',
//	minChars:1,  //输入几个字符开始搜索
	width :100,
	listWidth :150,
	resizable: true,
	triggerAction : 'all'
});

//authDomain = false 显示全部包含部分权限数据 authDomain = true 只显示全部权限数据
function initDisplayOption(object,displayAll,authDomain){
	object.baseParams.displayAll = displayAll;
	object.baseParams.authDomain = authDomain;
}
//使用此配置
//initDisplayOption(emsGroupStore,false);
