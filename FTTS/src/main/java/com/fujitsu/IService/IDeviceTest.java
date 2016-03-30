package com.fujitsu.IService;

import java.io.InputStream;
import java.util.List;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;

/**
 * @author xuxiaojun
 * 定义和设备交互的原子命令，如获取告警，获取设备信息，端口切换等，获取数据后返回统一的数据模型
 */
public interface IDeviceTest {
	
	/**
	 * 设备测试方法
	 * @param eqpt
	 * @param sys
	 * @param routeList
	 * @param testPara
	 * @return
	 */
	public String startTest(EqptInfoModel eqpt, SysInfoModel sys,
			List<RoutePointInfoModel> routeList, TestParaInfoModel testPara)
			throws CommonException;

	/**
	 * @function:
	 * @data:2015-1-21
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return
	 * @throws CommonException
	 * List<RTUConfiguration> 
	 *
	 */
	public List<RTUConfiguration> loadRTUConfiguration(EqptInfoModel eqpt, SysInfoModel sys) throws CommonException ;
	
	/**
	 * @function:
	 * @data:2015-1-21
	 * @author cao senrong
	 * @param eqpt
	 * @param sys
	 * @return
	 * @throws CommonException
	 * List<RTUAlarm> 
	 *
	 */
	public List<RTUAlarm> loadRTUAlarm(EqptInfoModel eqpt, SysInfoModel sys) throws CommonException;
	
	/**
	 * @function:处理socketserver上报的消息
	 * @data:2015-1-8
	 * @author cao senrong
	 * @param inputStream
	 * void 
	 *
	 */
	public void socketServerMsgHandle(InputStream inputStream);
}
