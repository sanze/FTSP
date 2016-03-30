package com.fujitsu.manager.equipmentTestManager.service;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.CommonMapper;
import com.fujitsu.dao.mysql.ResourceMapper;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

/**
 * 自动监测rtu设备连接状态
 * @author xuxiaojun
 * 
 */
public class AutoCheckConnection extends TimerTask {

	private static CommonMapper commonMapper;
	private static ResourceMapper resourceMapper;

	public AutoCheckConnection() {

	}

	public void run() {

		try {
			System.out.println("我在检查测试设备连接状态...开始");

			if (commonMapper == null) {
				commonMapper = (CommonMapper) SpringContextUtil
						.getBean("commonMapper");
			}
			if (resourceMapper == null) {
				resourceMapper = (ResourceMapper) SpringContextUtil
						.getBean("resourceMapper");
			}

			// 获取设备列表
			List<Map> rtuList = commonMapper.selectTable("t_ftts_rc", null,
					null);

			if (rtuList != null) {
				for (Map rtu : rtuList) {
					// 获取设备ip地址
					String ip = rtu.get("IP") == null ? null : rtu.get("IP")
							.toString();
					// 获取当前连接状态
					Integer connectStatus = rtu.get("STATUS") == null ? null
							: Integer.valueOf(rtu.get("STATUS").toString());

					boolean isReachable = CommonUtil.isReachable(ip);
					
					if(connectStatus == null){
						// 更新为CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG
						if(isReachable){
							rtu.put("status",
									CommonDefine.CONNECT_STATUS_NORMAL_FLAG);
						}else{
							rtu.put("status",
									CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG);
						}
						rtu.put("rcId",rtu.get("RC_ID"));
						rtu.put("comboFactory", -99);
						rtu.put("comboType", -99);
						resourceMapper.modRC(rtu);
					}else{
						// 当前可ping通且连接状态非正常->更新连接状态为正常
						if (isReachable
								&& connectStatus != CommonDefine.CONNECT_STATUS_NORMAL_FLAG) {
							// 更新为CommonDefine.CONNECT_STATUS_NORMAL_FLAG
							rtu.put("status",
									CommonDefine.CONNECT_STATUS_NORMAL_FLAG);
							rtu.put("rcId",rtu.get("RC_ID"));
							rtu.put("comboFactory", -99);
							rtu.put("comboType", -99);
							resourceMapper.modRC(rtu);
						}
						// ping不通且连接状态非断开->更新连接状态为断开
						if (!isReachable
								&& connectStatus != CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG) {
							// 更新为CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG
							rtu.put("status",
									CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG);
							rtu.put("rcId",rtu.get("RC_ID"));
							rtu.put("comboFactory", -99);
							rtu.put("comboType", -99);
							resourceMapper.modRC(rtu);
						}
					}	
				}
			}
			System.out.println("我在检查测试设备连接状态...结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
