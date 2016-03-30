package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


/**
 * @author xuxiaojun
 *
 */
public interface CommonManagerMapper {
	
	/**
	 * @param param
	 */
	public void setSysParam(Map param);
	
	/**
	 * @param paramKey
	 * @return
	 */
	public Map selectSysParam(
			@Param(value = "paramKey") String paramKey);
	
	/**
	 * 	获取整表数据
	 * @param tableName 表名
	 * @return
	 */
	public List selectTable(
			@Param(value = "tableName") String tableName,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * 	获取整表数据count值
	 * @param tableName 表名
	 * @return
	 */
	public int selectTableCount(
			@Param(value = "tableName") String tableName);
	
	/**
	 * 	按id获取一条数据
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public Map selectTableById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	/**
	 * 	按id获取数据列表
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public List selectTableListById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize);
	
	/**
	 * 	按id获取数据列表count值
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param id id值
	 * @return
	 */
	public int selectTableListCountById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	
//	/**
//	 * 按userId,目标类型获取用户设备域信息
//	 * @param userId
//	 * @param targetType 为null时查询全部
//	 * @return
//	 * key:userId 用户Id
//	 * key:targetId 目标Id
//	 * 	key:targetType 目标类型，详见DB表说明
//	 * key:emsDisplayName ems 显示名称
//	 */
//	public List selectUserDeviceDomain(
//			@Param(value = "userId") int userId,
//			@Param(value = "targetType") Integer targetType);
	
	/**
	 * 查询网管分组信息
	 * @param userId 用户Id
	 * @param startNumber
	 * @param pageSize
	 * @param Define 固定使用CommonDefine.TREE.TREE_DEFINE
	 * @return t_base_ems_group字段一致
	 */
	public List getAllEmsGroups(
			@Param(value = "userId") Integer userId,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "Define") Map Define);
	
	/**
	 * 查询网管信息
	 * @param userId 用户Id
	 * @param emsGroupId 网管分组Id
	 * @param startNumber
	 * @param pageSize
	 * @param Define 固定使用CommonDefine.TREE.TREE_DEFINE
	 * @return t_base_ems_connection字段一致
	 */
	public List getAllEmsByEmsGroupId(
			@Param(value = "userId") Integer userId,
			@Param(value = "emsGroupId") Integer emsGroupId,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "Define") Map Define);
	
	/**
	 * 模糊查询某个网管下的所有网元
	 * @param userId 用户Id
	 * @param emsId 网管Id
	 * @param startNumber
	 * @param pageSize
	 * @param isDel 
	 * 							默认值为null查询isDel = 0
	 * 							CommonDefine.FALSE = 0 
	 * 							CommonDefine.TRUE = 1 
	 * 							CommonDefine.DELETE_FLAG = 2  标记删除
	 * @param Define 固定使用CommonDefine.TREE.TREE_DEFINE
	 * @return t_base_ne字段一致
	 */
	public List getAllNeByEmsId(
			@Param(value = "userId") Integer userId,
			@Param(value = "emsId") Integer emsId,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "isDel") Integer[] isDel,
			@Param(value = "Define") Map Define);
	
	/**
	 * 模糊查询某个网管下的所有网元总数
	 * @param userId 用户Id
	 * @param emsId 网管Id
	 * @param isDel 
	 * 							默认值为null查询isDel = 0
	 * 							CommonDefine.FALSE = 0 
	 * 							CommonDefine.TRUE = 1 
	 * 							CommonDefine.DELETE_FLAG = 2  标记删除
	 * @param Define 固定使用CommonDefine.TREE.TREE_DEFINE
	 * @return t_base_ne字段一致
	 */
	public int getAllNeByEmsIdCount(
			@Param(value = "userId") Integer userId,
			@Param(value = "emsId") Integer emsId,
			@Param(value = "isDel") Integer[] isDel,
			@Param(value = "Define") Map Define);
	
	
	/**
	 * 模糊查询某个网管下的所有网元
	 * @param userId 用户Id
	 * @param emsId 网管Id
	 * @param startNumber
	 * @param pageSize
	 * @param isDel 
	 * 							默认值为null查询isDel = 0
	 * 							CommonDefine.FALSE = 0 
	 * 							CommonDefine.TRUE = 1 
	 * 							CommonDefine.DELETE_FLAG = 2  标记删除
	 * @param Define 固定使用CommonDefine.TREE.TREE_DEFINE
	 * @return t_base_ne字段一致
	 */
	public List getAllNeByEmsIdWithAdditionInfo(
			@Param(value = "userId") Integer userId,
			@Param(value = "emsId") Integer emsId,
			@Param(value = "startNumber") Integer startNumber,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "isDel") Integer[] isDel,
			@Param(value = "Define") Map Define);
	
	/**
	 * 获取菜单集合--首页显示用
	 * @param userId
	 * @param menuIds
	 * @return
	 */
	public List<Map> getMenuList(
			@Param(value = "userId") Integer userId, 
			@Param(value = "menuIds") List<Integer> menuIds);
	
	/**
	 * 获取子菜单集合--无权限
	 * @param menuParentId
	 * @return
	 */
	public List<Map> getAllSubMenuList(
			@Param(value = "menuParentId") int menuParentId);
	
	/**
	 * 获取子菜单集合--含权限
	 * @param menuParentId
	 * @param userId
	 * @return
	 */
	public List<Map> getAuthSubMenuList(
			@Param(value = "menuParentId") int menuParentId,
			@Param(value = "userId") int userId);
	
/**^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^**/
	public List<Map> treeGetNodesByParent(
			@Param(value = "parentId") int parentId, 
			@Param(value = "parentLevel") int parentLevel, 
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	public Map treeGetNodeById(
			@Param(value = "nodeId") int nodeId, 
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "userId") Integer userId, 
			@Param(value = "Define") Map Define);
	public List<Map> treeGetNodesByText(
			@Param(value = "text") String text, 
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "rootId") int rootId, 
			@Param(value = "rootLevel") int rootLevel,
			@Param(value = "startNumber") int startNumber, 
			@Param(value = "pageSize") int pageSize, 
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map Define);
	public int treeCountNodesByText(
			@Param(value = "text") String text, 
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "rootId") int rootId, 
			@Param(value = "rootLevel") int rootLevel, 
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map Define);
	public Map getNodeInfo(
			@Param(value = "nodeId") int nodeId, 
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "Define") Map Define);
	public List<Map> getNodesInfoByRoot(
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "rootId") int rootId, 
			@Param(value = "rootLevel") int rootLevel,
			@Param(value = "startNumber") int startNumber, 
			@Param(value = "pageSize") int pageSize,
			@Param(value = "Define") Map Define);
	public int CountNodesInfoByRoot(
			@Param(value = "nodeLevel") int nodeLevel, 
			@Param(value = "rootId") int rootId, 
			@Param(value = "rootLevel") int rootLevel,
			@Param(value = "Define") Map Define);
/**------------------------------- 共通树部分 -------------------------------**/
}