package com.fujitsu.IService;

import java.util.List;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;

/**
 * @author xuxiaojun 定义向外部开放的组合式命令，如开启测试，同步设备数据，同步告警等操作
 */
public interface IEquipmentTestManagerService {

	/**
	 * OTDR测试服务统一入口
	 * 
	 * @param eqpt
	 * @param sys
	 * @param routeList
	 * @param testPara
	 * @return
	 * @throws CommonException
	 */
	public String otdrTestCentralizeEntrance(EqptInfoModel eqpt,
			SysInfoModel sys, List<RoutePointInfoModel> routeList,
			TestParaInfoModel testPara, int collectLevel) throws CommonException;
	
	/**
	 * @function:查询设备配置
	 * @data:2015-1-8
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return
	 * @throws CommonException
	 * List<RTUConfiguration> 
	 *
	 */
	public List<RTUConfiguration> loadRTUConfiguration(EqptInfoModel eqpt, 
			SysInfoModel sys, int collectLevel) throws CommonException;
		
	/**
	 * @function:查询设备告警
	 * @data:2015-1-8
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return
	 * @throws CommonException
	 * List<RTUAlarm> 
	 *
	 */
	public List<RTUAlarm> loadRTUAlarm(EqptInfoModel eqpt, 
			SysInfoModel sys, int collectLevel) throws CommonException;
	
	/**
	 * 修改测试路由的状态及发送测试的起停消息
	 * @param routeId
	 * @param status（0:空闲  1:测试中）
	 * @return
	 * @throws CommonException
	 */
	public void modifyRouteStatus(int routeId , int status) throws CommonException;
}
