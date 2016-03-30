package com.fujitsu.IService;

import java.util.Map;
import com.fujitsu.common.CommonException;

/**
 * @author ZJL
 *
 */
public interface IEvaluateManagerService {
	/**
	 * 查找光纤链路衰耗信息
	 * 
	 * @param param
	 *            查询条件
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> searchFiberLink(
			Map<String, Object> param, int startNumber,
			int pageSize) throws CommonException;

	/**
	 * 获取偏差值信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> getOffsetValue() throws CommonException;

	/**
	 * 修改偏差值信息
	 * 
	 * @throws CommonException
	 */
	public void modifyOffsetValue(String upperOffset, String middleOffset, String downOffset)
			throws CommonException;

	/**
	 * 生成趋势图
	 * @param param
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> generateDiagram(Map<String, Object> param) throws CommonException;
	
	/**
	 * 查找光纤链路信息
	 * 
	 * @param param
	 *            查询条件
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> getAllResourceLink(
			Map<String, Object> param, int startNumber,
			int pageSize) throws CommonException;
	
	/**
	 * 删除光纤链路信息
	 * 
	 * @param param
	 *            查询条件
	 * @return
	 * @throws CommonException
	 */
	public void deleteResourceLink(
			Map<String, Object> param) throws CommonException;
	
	/**
	 * 设置光纤链路信息
	 * 
	 * @param param
	 *            查询条件
	 * @return
	 * @throws CommonException
	 */
	public void setResourceLink(
			Map<String, Object> param) throws CommonException;
	
	/**
	 * 生成趋势图
	 * @param param
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> generateDiagramLine(Map<String, Object> param) throws CommonException; 
	
	
	/**
	 * 生成趋势图
	 * @param param
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> generateDiagramTable(Map<String, Object> param) throws CommonException; 
}
