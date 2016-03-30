/**
 * @returns 组织好的上半部分报表参数
 */
function getTaskParams(sdh,wdm){
	var taskName = Ext.getCmp('reportTaskName').getValue();
	var dataSrc = Ext.getCmp('dataSrcCombo').getValue();// 0:正常；1:异常
	var needExport = Ext.getCmp('needExport').getValue() ? 1 : 0;
	var continueAbnormal = Ext.getCmp('continueAbnormal').getValue();
	var privilege = Ext.getCmp('privilege').getValue();
	var period = Ext.getCmp('periodCb').getValue();// 0:日报；1：月报
	var hour = Ext.getCmp('hourCb').getValue();// 采集结束后几小时
	var delay = period == 0 ? Ext.getCmp('delay4DailyCb').getValue() : Ext
			.getCmp('delay4MonthlyCb').getValue();// 延迟几天
	var params = {
			'paramMap.taskName' : taskName,
			'paramMap.dataSrc' : dataSrc,
			'paramMap.continueAbnormal' : continueAbnormal != '' ? continueAbnormal
					: 1,
			'paramMap.privilege' : privilege,
			'paramMap.period' : period,
			'paramMap.hour' : hour,
			'paramMap.delay' : delay,
			'paramMap.taskType' : TASK_TYPE
		};
	if(sdh||wdm)
		getPmParams(params,sdh,wdm);
	
	return params;
}

function getPmParams(params,sdh,wdm){
	if(sdh)
		getSdhParams(params);
	if(wdm)
		getWdmParams(params);
    return params;
}

function getSdhParams(params){
	// SDH性能参数
	if(!!Ext.getCmp('SDH'))
		params['paramMap.SdhPm']= getPmChecked.getSdhPmStdIndex();
	// 可以再确定一下other里面是什么
	if(!!Ext.getCmp('SDHTPLevel'))
		params['paramMap.SdhTp'] = getPmChecked.getSdhTp();
	if(!!Ext.getCmp('SDHTPLevelOther'))
		params['paramMap.otherSDHTP'] = Ext.getCmp('SDHTPLevelOther').getValue() ? 1 : 0;
	
	//SDH的选择情况
	if(!!Ext.getCmp('SDHPhysical'))
		params['paramMap.SDHPhyCheckedStatus']=Ext.getCmp('SDHPhysical').getValue(0);
	if(!!Ext.getCmp('SDHNumberic'))
		params['paramMap.SDHNumCheckedStatus'] = Ext.getCmp('SDHNumberic').getValue(0);
	if(!!Ext.getCmp('SDHTPLevel'))
		params['paramMap.SDHTpCheckedStatus'] = Ext.getCmp('SDHTPLevel').getValue(0);
	if(!!Ext.getCmp('SDHMaxMin'))
		params['paramMap.SDHMaxMin'] = Ext.getCmp('SDHMaxMin').getValue();
}
function getWdmParams(params){
	// WDM性能参数
	if(!!Ext.getCmp('WDM'))
		params['paramMap.WdmPm']=getPmChecked.getWdmPmStdIndex();
	// wdm的指定了其他里面包含的东西，所以不需要other
	if(!!Ext.getCmp('WDMTPLevel'))
		params['paramMap.WdmTp'] = getPmChecked.getWdmTp();
//	if(!!Ext.getCmp('WDMTPLevelOther'))
//		params['paramMap.otherWDMTP'] = Ext.getCmp('WDMTPLevelOther').getValue() ? 1 : 0;
	
	//WDM的选择情况
	if(!!Ext.getCmp('WDMPhysical'))
		params['paramMap.WDMPhyCheckedStatus']=Ext.getCmp('WDMPhysical').getValue(0);
	if(!!Ext.getCmp('WDMNumberic'))
		params['paramMap.WDMNumCheckedStatus'] = Ext.getCmp('WDMNumberic').getValue(0);
	if(!!Ext.getCmp('WDMTPLevel'))
		params['paramMap.WDMTpCheckedStatus'] = Ext.getCmp('WDMTPLevel').getValue(0);
	if(!!Ext.getCmp('WDMMaxMin'))
		params['paramMap.WDMMaxMin'] = Ext.getCmp('WDMMaxMin').getValue();
}

function getTargetIds(key){
	var list = new Array();
	store.each(function(record) {
		var nodes = {
			'targetId' : record.get(key)
		};
		list.push(Ext.encode(nodes));
	});
	return list;
}
