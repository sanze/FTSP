package com.fujitsu.job;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ConnectionManagerMapper;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

public class CheckServerStatus extends TimerTask{
	
	private ConnectionManagerMapper connectionManagerMapper;
	
	
	public void run() {
		checkDataCollectService();
	} 
	
	
	/**
	 * 获取数据采集服务
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException 
	 */
	public boolean checkDataCollectService() {

		connectionManagerMapper = (ConnectionManagerMapper) SpringContextUtil
				.getBean("connectionManagerMapper");
		List<Map> svcRecordMap = connectionManagerMapper.selectAllSvcRecord();
		Map svcRecord = null;
		String ip = null;
		String address = null;
		int recordId = -1;
		int svcStatus = CommonDefine.CONNECT_STATUS_NORMAL_FLAG; // 1
																	// 连接正常,2连接异常,3
																	// 网络中断
		if (svcRecordMap == null) {
			return true;
			// throw new CommonException(new
			// NullPointerException(),MessageCodeDefine.DATA_COLLECT_SERVICE_UNCONFIG);
		}
		for (int i = 0; i < svcRecordMap.size(); i++) {
			svcRecord = svcRecordMap.get(i);
			ip = svcRecord.get("IP").toString();
			svcStatus = CommonDefine.CONNECT_STATUS_NORMAL_FLAG;// 循环开始重新设置初始状态
			// 检查是否能ping通
			if (!CommonUtil.isReachable(ip)) {
				svcStatus = CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG;// 3网络中断
				// throw new CommonException(new
				// NullPointerException(),MessageCodeDefine.DATA_COLLECT_SERVICE_FAILED);
			} else {// 如果可以ping通，再检查服务器状态
				address = svcRecord.get("ADDRESS").toString();
				// 获取rmi对象
				RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
				rmiProxyFactoryBean
						.setServiceInterface(IDataCollectService.class);
				// 例"rmi://localhost:1021/dataCollectService"
				rmiProxyFactoryBean.setServiceUrl(address);
				try {
					rmiProxyFactoryBean.afterPropertiesSet(); //
					// 更改ServiceInterface或ServiceUrl之后必须调用该方法，来获取远程调用桩
				} catch (Exception ex) {
					svcStatus = CommonDefine.CONNECT_STATUS_EXCEPTION_FLAG;// 2连接异常
					// throw new CommonException(new
					// NullPointerException(),MessageCodeDefine.DATA_COLLECT_SERVICE_FAILED);
				}
				// service = (IDataCollectService)
				// rmiProxyFactoryBean.getObject();
			}
			recordId = Integer.parseInt(svcRecord.get("SYS_SVC_RECORD_ID")
					.toString());
			// 更新接入服务器状态
			connectionManagerMapper.updateServerStatus(svcStatus, recordId);
			// 更新网管连接状态
			if (svcStatus != CommonDefine.CONNECT_STATUS_NORMAL_FLAG) {
				connectionManagerMapper.updateEmsConnectStatusByServerId(
						CommonDefine.CONNECT_STATUS_EXCEPTION_FLAG, recordId,"接入服务器异常！");
				//发送告警信息
				
			}

		}
		return true;
	}
}