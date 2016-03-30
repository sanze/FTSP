
/**
 * 检查数据合法
 * @param isPreview 是否是预览
 * @param key targetId的字段名
 * @param sdh 是否获取sdh的参数
 * @param wdm 是否获取wdm的参数
 */
function beforeSave(isPreview,key,sdh,wdm) {
	if (Ext.getCmp('northPanel').getForm().isValid()) {
		if (store.getCount() < 1) {
			Ext.Msg.alert('信息', '请添加'+TARGET_NAME+'!');
			return;
		}
		beforeSaveCallBack(isPreview,key,sdh,wdm);
	}
}
/**
 * 检查任务重名
 */
function beforeSaveCallBack(isPreview,key,sdh,wdm) {
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var param = {
		'paramMap.taskName' : taskName,
		'paramMap.taskType' : TASK_TYPE
	};
	if (!!taskId)
		param['paramMap.taskId'] = taskId;
	Ext.Ajax.request({
		url : 'nx-report!checkTaskNameDuplicate.action',
		method : 'POST',
		params : param,
		success : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			if (!isPreview&&result.returnResult == 0) {
				Ext.Msg.alert("提示", result.returnMessage);
			} else {
				saveTask(isPreview,key,sdh,wdm);
			}
		},
		failure : function(response) {
			var result = Ext.util.JSON.decode(response.responseText);
			Ext.Msg.alert("提示", result.returnMessage);
		}
	});
}


/**
 * 保存
 */
function saveTask(isPreview,key,sdh,wdm) {
	var params = getTaskParams(sdh,wdm);
	params['modifyList'] = getTargetIds(key);
	
	if (isPreview) {// 预览
		params['reportType'] = TASK_TYPE;
		preview(params);
	} else {
		if (!!taskId)
			params['paramMap.taskId'] = taskId;
		var url;
		if (!!taskId)
			url = 'nx-report!updateReportTask.action';
		else
			url = 'nx-report!saveReportTask.action';
		top.Ext.getBody().mask('正在保存，请稍候...');
		Ext.Ajax.request({
			url : url,
			method : 'POST',
			params : params,
			success : function(response) {
				top.Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage, function() {
					if (result.returnResult == 1) {
						var f = top.window.frames[frameId];
						if (f) {
							if (f.loadTaskName) {
								f.loadTaskName();
								var pageTool = f.pageTool;
							} else {
								f.contentWindow.loadTaskName();
								var pageTool = f.contentWindow.pageTool;
							}
							pageTool.doLoad(pageTool.cursor);
						}
						// parent.closeTab('WDM波长转换盘作业计划');
					}
				});
			},
			failure : function(response) {
				top.Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			},
			error : function(response) {
				top.Ext.getBody().unmask();
				var result = Ext.util.JSON.decode(response.responseText);
				Ext.Msg.alert("提示", result.returnMessage);
			}
		});
	}
}