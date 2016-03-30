package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SysInterfaceManageMapper {
	/**
	 * 
	 * Method name: selectInterfaceDataList <BR>
	 * Description: 查询所有系统接口带分页 <BR>
	 * Remark: <BR>
	 * @param map
	 * @return  List<Map><BR>
	 */
	public List<Map<String, Object>> selectInterfaceDataList(@Param(value = "map")Map<String, Object> map);
	/**
	 * 
	 * Method name: countInterfaceDataList <BR>
	 * Description: 查询所有系统接口的数量 <BR>
	 * Remark: <BR>
	 * @param map
	 * @return  int<BR>
	 */
	public int countInterfaceDataList(@Param(value = "map")Map map);
	/**
	 * Method name: addInterface <BR>
	 * Description:新增接口 <BR>
	 * Remark: <BR>
	 * @param paramMap  void<BR>
	 */
	public void addInterface(@Param(value = "map")Map<String, Object> paramMap);
	/**
	 * 
	 * Method name: modifyInterface <BR>
	 * Description: 修改接口 <BR>
	 * Remark: <BR>
	 * @param paramMap  void<BR>
	 */
	public void modifyInterface(@Param(value = "map")Map<String, Object> paramMap);
	/**
	 * 
	 * Method name: deleteInterface <BR>
	 * Description: 删除接口 <BR>
	 * Remark: <BR>
	 * @param id  void<BR>
	 */
	public void deleteInterface(@Param(value="id")int id);
	/**
	 * 
	 * Method name: getDetailById <BR>
	 * Description: 获取详情 <BR>
	 * Remark: <BR>
	 * @param id
	 * @return  Map<String,Object><BR>
	 */
	public Map<String, Object> getDetailById(@Param(value="id")int id);
	
	/**
	 * 
	 * Method name: checkConnection <BR>
	 * Description: 认证源IP <BR>
	 * Remark: <BR>
	 * @param map
	 * @return  int<BR>
	 */
	public int checkConnection(@Param(value = "map")Map<String, Object> map);
	public int checkInterfaceName(@Param(value = "map")Map<String, Object> paramMap);
	public int checkInterfaceIPPort(@Param(value = "map")Map<String, Object> paramMap);
	public int checkInterfacePeer(@Param(value = "map")Map<String, Object> paramMap);
}
