package com.fujitsu.IService;

import java.text.ParseException;
import java.util.List;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;

public interface IFaultManagerService {
	
	/**
	 * Method name: alarmDataToMongodb <BR>
	 * Description: 告警数据入库<BR>
	 * Remark: 2013-11-29<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 * @throws ParseException 
	 */
	public void alarmDataToMongodb(List<AlarmDataModel> modelList,
			Integer emsConnectionId, Integer neId, Integer operateType)throws CommonException;
}
