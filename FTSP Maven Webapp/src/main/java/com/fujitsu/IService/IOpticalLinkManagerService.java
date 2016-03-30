package com.fujitsu.IService;

import java.util.Map;
import com.fujitsu.common.CommonException;

/**
 * @author ZJL
 *
 */
public interface IOpticalLinkManagerService {
	/**
	 * 查找光纤链路衰耗信息
	 * 
	 * @param opticalLinkEvaluateModel
	 *            查询条件
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> searchOpticalLink(
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
}
