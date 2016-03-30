package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.manager.systemManager.model.AuthRegion;

public interface AuthRegionManageMapper {
	/**
	 * Method name: selectAuthRegionDataList <BR>
	 * Description: 查询所有权限域分页数据<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public List<Map> selectAuthRegionDataList(@Param(value = "map")Map map);
	/**
	 * Method name: countAuthRegionDataList <BR>
	 * Description: 查询所有权限域总数<BR>
	 * Remark: 2013-12-02<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public int countAuthRegionDataList(@Param(value = "map")Map map);
	/**
	 * Method name: insert <BR>
	 * Description: 插入数据<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public void insert(@Param(value = "authRegion")AuthRegion authRegion);
	/**
	 * Method name: update <BR>
	 * Description: 更新数据<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return Map<String, Object><BR>
	 */
	public void update(@Param(value = "authRegion")AuthRegion authRegion);
	/**
	 * Method name: delete <BR>
	 * Description: 删除记录<BR>
	 * Remark: 2013-12-07<BR>
	 * @author wuchao
	 * @return String<BR>
	 */
	public void delete(@Param(value = "authRegion")AuthRegion authRegion);
	
	//插入权限域关联的菜单
	public void insertAuthRegionRefMenu(@Param(value = "authRegion")AuthRegion authRegion);
	
	//删除权限域关联的菜单
	public void deleteAuthRegionRefMenu(@Param(value = "authRegion")AuthRegion authRegion);
	
	//删除用户关联的权限域
	public void deleUserRefAuthRegion(@Param(value = "authRegion")AuthRegion authRegion);
	
	public List<Map> getAuthTreeNodes(@Param(value = "authRegion")AuthRegion authRegion);
	//返回指定权限域，指定菜单的权限
	public String getMenuAuthByAuth(@Param(value = "authRegion")AuthRegion authRegion);
	
	//返回权限域对应的权限
	public List<Map> getMenuAuthsByAuthDomainId(@Param(value = "authRegion")AuthRegion authRegion);
	//获取父菜单
	public Map getParentMenuByMenuId(String parentMenuId);
	public String getIsLeafByMenuId(String menuId);
	public int validateUserAuthDomainName(@Param(value = "name")String name);
}
