package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface ILinkEvalTopoManagerService {
	
	/**
	 * 按给定的网络级别返回系统名称和系统Id
	 * @param netLevel 【取值】1：骨干层， 2：汇聚层，3：接入层，4：一干，5：二干，-1：所有
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getSystemList(int netLevel, int userId) throws CommonException;

	/**
	 * 按给定的系统ID获取相应系统的拓扑数据
	 * @param sysId
	 * @param evalTime	【取值】yyyy-mm-dd
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getLinkEvalTopoData(int sysId, String evalTime, int userId) throws CommonException;
	
	/**
	 * 保存网元座标
	 * @param positionArray
	 * @throws CommonException
	 */
	public void savePosition(List<String> positionArray) throws CommonException;
}
