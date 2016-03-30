package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

/**
 * @Description：
 * @author cao senrong
 * @date 2015-1-23
 * @version V1.0
 */
public interface ITestResultManagementService {

	/**
	 * 查询测试结果
	 * @param testResultModel
	 * @param start
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map queryTestResults(Map map);

	/**
	 * 查询测试事件
	 * @param testResultModel
	 * @param start
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> queryTestEvents(Map map);
	
	public Map getResultById(Map map);

	public List<Map> getRouteList();
	
	// 保存基准值
	public void saveToBase(Map<String,Object> map) throws CommonException;
	
	public Map exportInfo(Map map);
	
	public Boolean deleteResult(Map map) throws CommonException;
}