package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IInstantReportService {

	/**
	 * 性能报表：光路误码监测记录表
	 * 
	 * @param condMap
	 *            各种条件
	 * @param nodeList
	 *            节点条件
	 * @return
	 */
	String generateOptPathBitErrReport(Map<String, String> condMap,
			List<Map> nodeList,Integer sysUserId) throws CommonException;

	/**
	 * 性能报表：网元数量检查
	 * 
	 * @param nodeList
	 * @return
	 * @throws CommonException
	 */
	int neCountCheck(List<Map> nodeList) throws CommonException;

	/**
	 * SDH发送、接收光功率记录表
	 * @param condMap
	 * @param nodeList
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	String generateSDHLightPowerReport(Map<String, String> condMap,
			List<Map> nodeList, Integer sysUserId)throws CommonException;

}
