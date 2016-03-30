package com.fujitsu.IService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface ICommonManagerService {
	
	
	/**获取参数
	 * @param key
	 * @return
	 * @throws CommonException
	 */
	public Map getSysParam(String key) throws CommonException;
	/**设置参数
	 * @param param
	 * @return
	 * @throws CommonException
	 */
	public void setSysParam(Map param) throws CommonException;
//	/**
//	 * 获取用户设备域权限集
//	 * @param userId
//	 * @param targetType 为null获取全部
//	 * @param needDisplayName 是否需要显示名称
//	 * @return 
//	 * @throws CommonException
//	 */
//	public List<UserDeviceDomainModel> getUserDeviceDomain(int userId,
//			Integer targetType, boolean needDisplayName) throws CommonException;
	
	/**
	 * 查询网管分组信息
	 * @param userId 用户Id
	 * @param displayAll 是否需要显示全部
	 * @param displayNone 是否需要显示无
	 * @param authDomain false 显示全部包含部分权限数据 true 只显示全部权限数据
	 * @return t_base_ems_group字段一致
	 * @throws CommonException
	 */
	public List<Map> getAllEmsGroups(Integer userId,
			boolean displayAll,
			boolean displayNone,
			boolean authDomain) throws CommonException;

	/**
	 * 查询网管分组信息
	 * @param userId 用户Id
	 * @param startNumber
	 * @param pageSize
	 * @param authDomain false 显示全部包含部分权限数据 true 只显示全部权限数据
	 * @return t_base_ems_group字段一致
	 * @throws CommonException
	 */
	public List<Map> getAllEmsGroups(Integer userId,
			Integer startNumber,
			Integer pageSize,
			boolean authDomain) throws CommonException;

	/**
	 * 查询网管信息
	 * @param userId 用户Id
	 * @param emsGroupId 网管分组Id  CommonDefine.VALUE_ALL查询全部ems
	 * @param displayAll 是否需要显示全部
	 * @param authDomain false 显示全部包含部分权限数据 true 只显示全部权限数据
	 * @return t_base_ems_connection字段一致
	 * @throws CommonException
	 */
	public List<Map> getAllEmsByEmsGroupId(Integer userId,
			Integer emsGroupId,
			boolean displayAll,
			boolean authDomain) 
			throws CommonException;
	
	/**
	 * 查询网管信息
	 * @param userId 用户Id
	 * @param emsGroupId 网管分组Id  CommonDefine.VALUE_ALL查询全部ems
	 * @param startNumber
	 * @param pageSize
	 * @param authDomain false 显示全部包含部分权限数据 true 只显示全部权限数据
	 * @return t_base_ems_connection字段一致
	 * @throws CommonException
	 */
	public List<Map> getAllEmsByEmsGroupId(Integer userId,
			Integer emsGroupId,
			Integer startNumber,
			Integer pageSize,
			boolean authDomain) throws CommonException;
	
	/**
	 * 查询某个网管下的所有网元
	 * @param userId 用户Id
	 * @param emsId 网管Id CommonDefine.VALUE_ALL查询全部ne
	 * @param displayAll 是否需要显示全部
	 * @param isDel 
	 * 							默认值为null查询isDel = 0
	 * 							CommonDefine.FALSE = 0 
	 * 							CommonDefine.TRUE = 1 
	 * 							CommonDefine.DELETE_FLAG = 2  标记删除
	 * @return t_base_ne字段一致
	 * @throws CommonException
	 */
	public List<Map> getAllNeByEmsId(Integer userId,Integer emsId,
			boolean displayAll,
			Integer[] isDel) throws CommonException;
	
	/**
	 * 查询某个网管下的所有网元
	 * @param userId 用户Id
	 * @param emsId 网管Id CommonDefine.VALUE_ALL查询全部ne
	 * @param startNumber
	 * @param pageSize
	 * @param isDel 
	 * 							默认值为null查询isDel = 0
	 * 							CommonDefine.FALSE = 0 
	 * 							CommonDefine.TRUE = 1 
	 * 							CommonDefine.DELETE_FLAG = 2  标记删除
	 * @return t_base_ne字段一致
	 * @throws CommonException
	 */
	public List<Map> getAllNeByEmsId(Integer userId,Integer emsId,
			Integer startNumber,
			Integer pageSize, Integer[] isDel) throws CommonException;

	
	/**
	 * 获取多层级完整路径名称
	 * @param idValue id值
	 * @param tableName 表名
	 * @param idName id字段名
	 * @param parentIdName 父节点Id字段名
	 * @param displayName 显示名
	 * @return
	 */
	public String getMulitLevelFullName(int idValue, String tableName,
			String idName, String parentIdName, String displayName);
	
	/**
	 * 获取菜单集合
	 * @param userId
	 * @param menuIds
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getMenuList(Integer userId, List<Integer> menuIds) throws CommonException;
	
	/**
	 * 获取子菜单集合
	 * @param userId
	 * @param menuId
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getSubMenuList (Integer userId,int menuId,boolean needAuthCheck) throws CommonException;

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ * */
	/**
	 * @param userId
	 * @return List<Map>
	 * @key	SYS_DEVICE_DOMAIN_REF_ID
	 * @key	SYS_DEVICE_DOMAIN_ID
	 * @key	TARGET_ID
	 * @key	TARGET_TYPE
	 * @throws CommonException
	 */
	public List<Map> getUserDeviceDomainDetail(Integer userId) throws CommonException;
	
	/** _______________________________ 共通部分 _______________________________ * */
	
	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ * */
	/**
	 * 取得树节点模型列表
	 * 
	 * @param parentId
	 *            父节点id
	 * @param parentType
	 *            父节点类型
	 * @param leafType
	 *            叶节点类型
	 * @return
	 */
	public List<Map> treeGetChildNodes(int parentId, int parentLevel,
			int leafLevel, Integer userId) throws CommonException;
	
	/**获取指定节点信息(树节点基本信息)
	 * @param nodeId
	 * @param nodeLevel
	 * @return Map("id":String,"nodeId":int,"nodeLevel":int,"parent":String,"parentId":int,"parentLevel":int,"leaf":boolean,"checked":String)
	 * @throws CommonException
	 */
	public Map treeGetNode(int nodeId, int nodeLevel, Integer userId) throws CommonException;

	/**搜索节点路径或节点列表
	 * @param key
	 * @param nodeId
	 * @param nodeLevel
	 * @param rootId
	 * @param rootLevel
	 * @param hasPath
	 * @param startNumber
	 * @param pageSize
	 * @return Map ("total":int, "rows":List<Map>)
	 * @throws CommonException
	 */
	public Map treeGetNodesByKey(String key, int nodeId, int nodeLevel,
			int rootId, int rootLevel, boolean hasPath, 
			int startNumber, int pageSize, Integer userId) throws CommonException;
	
	/**由root节点获取指定层级节点详细信息(数据库表所有信息)
	 * @param rootId
	 * @param rootLevel
	 * @param nodeLevel
	 * @param startNumber
	 * @param pageSize
	 * @return Map ("total":int, "rows":List<Map>)
	 * @throws CommonException
	 */
	public Map getNodesInfoByRoot(int rootId, int rootLevel,
			int nodeLevel, int startNumber, int pageSize) throws CommonException;

	/**获取指定节点详细信息(数据库表所有信息)
	 * @param nodeId
	 * @param nodeLevel
	 * @return Map(key:表字段名,value:值)
	 * @throws CommonException
	 */
	public Map getNodeInfo(int nodeId, int nodeLevel) throws CommonException;
	/** _______________________________ 共通树部分 _______________________________ * */
	
	/**
	 * 多个文件上传方法
	 * 
	 * @param file
	 *            文件数组
	 * @param fileName
	 *            文件名数组
	 * @param uploadPath
	 *            上传路径
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException
	 */
	public boolean uploadFile(File file, String fileName, String uploadPath)
			throws CommonException, IOException, FileNotFoundException;
	
	/**
	 * 获取license信息
	 * @return
	 */
	public Map getPermissionInfo();
	
	/**
	 * 获取关于信息
	 * @return
	 */
	public Map getAboutInfo();
}
