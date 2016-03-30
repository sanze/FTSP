package com.fujitsu.manager.equipmentTestManager.serviceImpl.ZBTest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.Result;
import com.fujitsu.manager.equipmentTestManager.service.DeviceTest;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.LightPathModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;

public class ZBTestImpl  extends DeviceTest{

	@Override
	public String startTest(EqptInfoModel eqpt, SysInfoModel sys,
			List<RoutePointInfoModel> routeList, TestParaInfoModel testPara) {
		// TODO Auto-generated method stub
		
		return "";
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDeviceTest#socketServerMsgHandle(java.io.InputStream)
	 */
	@Override
	public void socketServerMsgHandle(InputStream inputStream) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDeviceTest#loadRTUConfiguration(com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel, com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel)
	 */
	@Override
	public List<RTUConfiguration> loadRTUConfiguration(EqptInfoModel eqpt, SysInfoModel sys) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDeviceTest#loadRTUAlarm(com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel, com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel)
	 */
	@Override
	public List<RTUAlarm> loadRTUAlarm(EqptInfoModel eqpt, SysInfoModel sys) throws CommonException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
