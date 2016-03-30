package com.fujitsu.manager.commonManager.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.SystemManagerMapper;
import com.fujitsu.manager.commonManager.service.CommonManagerService;
import com.fujitsu.util.CommonUtil;

@Service
@Transactional(rollbackFor = Exception.class)
public class CommonManagerServiceImpl extends CommonManagerService {
	@Resource
	private SystemManagerMapper systemManagerMapper;
	
	private Properties props = System.getProperties(); // 获得系统属性集
	private String osName = props.getProperty("os.name"); // 操作系统名称

//	@Override
//	public List<UserDeviceDomainModel> getUserDeviceDomain(int userId,
//			Integer targetType, boolean needDisplayName) throws CommonException {
//
//		List<UserDeviceDomainModel> result = new ArrayList<UserDeviceDomainModel>();
//
//		UserDeviceDomainModel userDeviceDomainModel = null;
//		// 获取权限集合
//		List<Map> userDeviceAuthList = commonManagerMapper
//				.selectUserDeviceDomain(userId, targetType);
//
//		if (userDeviceAuthList != null) {
//			for (Map userDeviceAuth : userDeviceAuthList) {
//				userDeviceDomainModel = new UserDeviceDomainModel();
//				Integer targetId = Integer.valueOf(userDeviceAuth.get(
//						"targetId").toString());
//				Integer type = Integer.valueOf(userDeviceAuth
//						.get("targetType").toString());
//				userDeviceDomainModel.setUserId(userId);
//				userDeviceDomainModel.setTargetId(targetId);
//				userDeviceDomainModel.setTargetType(type);
//				// 设置显示名称
//				if (needDisplayName) {
//					// 暂时只支持到ems级别
//					if (CommonDefine.TREE.NODE.EMS == type.intValue()) {
//						Map target = commonManagerMapper.selectTableById(
//								"T_BASE_EMS_CONNECTION",
//								"BASE_EMS_CONNECTION_ID", targetId);
//						userDeviceDomainModel.setDisplayName(target.get(
//								"DISPLAY_NAME").toString());
//					}
//				}
//				result.add(userDeviceDomainModel);
//			}
//		}
//		return result;
//	}
	
	@Override
	public List<Map> getAllEmsGroups(Integer userId,boolean displayAll,
			boolean displayNone,boolean authDomain) throws CommonException{
		List<Map> list = new ArrayList<Map>();
		//加入全部选项
		if(displayAll){
			Map map = new HashMap();
			map.put("BASE_EMS_GROUP_ID", CommonDefine.VALUE_ALL);
			map.put("GROUP_NAME", "全部");
			list.add(map);
		}
		//加入查询选项
		List<Map> emsGroupList;
		if(authDomain){
			//只显示全部权限数据
			emsGroupList = commonManagerMapper
					.getAllEmsGroups(userId,null,null,CommonDefine.TREE.TREE_DEFINE);
		}else{
			//显示所有数据
			emsGroupList = commonManagerMapper
					.getAllEmsGroups(userId,null,null,TREE_DEFINE);
		}
		list.addAll(emsGroupList);
		//加入无选项
		if(displayNone){
			Map map = new HashMap();
			map.put("BASE_EMS_GROUP_ID", CommonDefine.VALUE_NONE);
			map.put("GROUP_NAME", "无");
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map> getAllEmsGroups(Integer userId,Integer startNumber,
			Integer pageSize,boolean authDomain) throws CommonException{
		List<Map> list = new ArrayList<Map>();
		//加入查询选项
		List<Map> emsGroupList;
		if(authDomain){
			//只显示全部权限数据
			emsGroupList = commonManagerMapper
					.getAllEmsGroups(userId,startNumber,pageSize,CommonDefine.TREE.TREE_DEFINE);
		}else{
			//显示所有数据
			emsGroupList = commonManagerMapper
					.getAllEmsGroups(userId,startNumber,pageSize,TREE_DEFINE);
		}
		list.addAll(emsGroupList);
		return list;
	}
	
	@Override
	public List<Map> getAllEmsByEmsGroupId(Integer userId,Integer emsGroupId,
			boolean displayAll,boolean authDomain) throws CommonException {
		List<Map> list = new ArrayList<Map>();
		//加入全部选项
		if(displayAll){
			Map map = new HashMap();
			map.put("BASE_EMS_CONNECTION_ID", CommonDefine.VALUE_ALL);
			map.put("DISPLAY_NAME", "全部");
			list.add(map);
		}
		
		List<Map> emsList;
		if(authDomain){
			//只显示全部权限数据
			emsList = commonManagerMapper.getAllEmsByEmsGroupId(
					userId, emsGroupId,null,null,CommonDefine.TREE.TREE_DEFINE);
		}else{
			//显示所有数据
			emsList = commonManagerMapper.getAllEmsByEmsGroupId(
					userId, emsGroupId,null,null,TREE_DEFINE);
		}
		list.addAll(emsList);
		
		return list;
	}
	
	@Override
	public List<Map> getAllEmsByEmsGroupId(Integer userId,
			Integer emsGroupId,
			Integer startNumber,
			Integer pageSize,boolean authDomain) throws CommonException{
		
		List<Map> list = new ArrayList<Map>();
		List<Map> emsList;
		if(authDomain){
			//只显示全部权限数据
			emsList = commonManagerMapper.getAllEmsByEmsGroupId(
					userId, emsGroupId,startNumber,pageSize,CommonDefine.TREE.TREE_DEFINE);
		}else{
			//显示所有数据
			emsList = commonManagerMapper.getAllEmsByEmsGroupId(
					userId, emsGroupId,startNumber,pageSize,TREE_DEFINE);
		}
		list.addAll(emsList);
		
		return list;
	}
	
	@Override
	public List<Map> getAllNeByEmsId(Integer userId,Integer emsId,
			boolean displayAll, Integer[] isDel) throws CommonException {
		List<Map> list = new ArrayList<Map>();
		//加入全部选项
		if(displayAll){
			Map map = new HashMap();
			map.put("BASE_NE_ID", CommonDefine.VALUE_ALL);
			map.put("DISPLAY_NAME", "全部");
			list.add(map);
		}
		
		//显示所有数据
		List<Map> neList = commonManagerMapper.getAllNeByEmsId(userId,emsId, null,null,isDel,TREE_DEFINE);
		
		list.addAll(neList);
		
		return list;
	}
	
	@Override
	public List<Map> getAllNeByEmsId(Integer userId,Integer emsId,
			Integer startNumber,
			Integer pageSize, Integer[] isDel) throws CommonException {
		List<Map> list = new ArrayList<Map>();
		// 查询所有网管
		List<Map> neList = commonManagerMapper.getAllNeByEmsId(userId,emsId, startNumber,pageSize,isDel,TREE_DEFINE);
		
		list.addAll(neList);
		
		return list;
	}


	@Override
	public List<Map> getSubMenuList(Integer userId, int menuParentId, boolean needAuthCheck)
			throws CommonException {

		List<Map> menuList = getSubMenu(userId, menuParentId,needAuthCheck);

		return menuList;

	}

	/**
	 * @param userId
	 * @param menuParentId
	 * @return
	 */
	private List<Map> getSubMenu(Integer userId, int menuParentId,boolean needAuthCheck) {

		List<Map> nodes = new ArrayList<Map>();
		//查询全部菜单项
		List<Map> allMenuList = commonManagerMapper.getAllSubMenuList(menuParentId);
		//查询权限菜单项
		List<Map> authMenuList = null;
		//判断是否需要权限检测
		if(needAuthCheck){
			authMenuList=commonManagerMapper.getAuthSubMenuList(menuParentId, userId);
		}
		if (allMenuList != null) {
			//标示符，防止重复
			for (Map obj : allMenuList) {
				//权限过滤
				if(needAuthCheck){
					obj.put("DISABLED", true);
					for(Map auth:authMenuList){
						if(obj.get("SYS_MENU_ID").equals(auth.get("SYS_MENU_ID"))){
							obj.put("DISABLED", false);
							
							if(obj.get("AUTH_SEQUENCE")==null){
							obj.put("AUTH_SEQUENCE", auth.get("AUTH_SEQUENCE"));
							}else{
								//合并权限
								String sequence = mergeAuthSequence(obj.get("AUTH_SEQUENCE").toString(),
										auth.get("AUTH_SEQUENCE").toString());
								obj.put("AUTH_SEQUENCE", sequence);
							}
						}
					}
				}else{
					//所有权限
					obj.put("AUTH_SEQUENCE", "all");
					obj.put("DISABLED", false);
				}
				//指定菜单项变灰
				if(obj.get("MENU_HREF")!=null&&"DISABLED".equals(obj.get("MENU_HREF").toString())){
					obj.put("DISABLED", true);
				}
				nodes.add(obj);
				if (menuParentId != 0) {
					nodes.addAll(getSubMenu(userId, Integer.parseInt(obj.get(
							"SYS_MENU_ID").toString()),needAuthCheck));
				}
			}
		}
		return nodes;
	}
	
	/**获取菜单集合--首页显示用
	 * @param userId
	 * @param menuId
	 * @return
	 */
	public List<Map> getMenuList(Integer userId, List<Integer> menuIds) throws CommonException{
		if(userId==null){
			throw new CommonException(new Exception(), MessageCodeDefine.USER_LOGIN_AGAIN);
		}
		//查询菜单项
		List<Map> menuList = new ArrayList<Map>();
		try{
			//查询菜单项
			menuList = commonManagerMapper.getMenuList(userId, menuIds);
			if (menuList != null) {
				int pos=0;
				while(pos<menuList.size()){
					Map posMenu=menuList.get(pos);
					if(userId!=CommonDefine.USER_ADMIN_ID){
						posMenu.put("DISABLED", posMenu.get("AUTH_SEQUENCE")==null);
					}else{
						//所有权限
						posMenu.put("AUTH_SEQUENCE", "all");
						posMenu.put("DISABLED", false);
					}
					for(int i=0;i<pos;i++){
						Map cosMenu=menuList.get(i);
						if(posMenu.get("SYS_MENU_ID").equals(
								cosMenu.get("SYS_MENU_ID"))){
							cosMenu.put("AUTH_SEQUENCE",
									mergeAuthSequence(
										""+posMenu.get("AUTH_SEQUENCE"),
										""+cosMenu.get("AUTH_SEQUENCE")));
							menuList.remove(pos);
							--pos;
							break;
						}
					}
					++pos;
				}
			}
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return menuList;
	}
	
	//合并权限字符串 补足10位
	private String mergeAuthSequence(String oldSequence, String newSequence) {
		if(oldSequence=="all"||newSequence=="all"){
			return "all";
		}else if(oldSequence==null||oldSequence.isEmpty()||
				newSequence==null||newSequence.isEmpty()){
			return oldSequence+newSequence;
		}
		//按位或运算，叠加权限
		BigInteger src = new BigInteger(String.valueOf(
				Integer.valueOf(oldSequence, 2)
						| Integer.valueOf(newSequence, 2)).toString());
		//补足10位
		String result = src.toString(2);
		int xx = result.length();
		for (int i = 0; i < 10 - xx; i++) {
			result = "0" + result;
		}
		return result;
	}

	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ * */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map> getUserDeviceDomainDetail(Integer userId) throws CommonException {
		if(userId==null){
			throw new CommonException(new Exception(), MessageCodeDefine.USER_LOGIN_AGAIN);
		}
		List<Map> returnData = new ArrayList<Map>();
		try {
			if(userId==CommonDefine.USER_ADMIN_ID){
				Map data = new HashMap();
				//data.put("SYS_DEVICE_DOMAIN_REF_ID", null);
				//data.put("SYS_DEVICE_DOMAIN_ID", null);
				data.put("TARGET_ID", CommonDefine.TREE.ROOT_ID);
				data.put("TARGET_TYPE", CommonDefine.TREE.NODE.ROOT);
				returnData.add(data);
			} else {
				returnData = systemManagerMapper.getCurrrentDeviceDomainDetail(userId);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	/** _______________________________ 共通部分 _______________________________ * */
	
	/** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 共通树部分 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ * */

	@SuppressWarnings("unchecked")
	@Override
	public List<Map> treeGetChildNodes(int parentId, int parentLevel,
			int leafLevel, Integer userId) throws CommonException {
		if(userId==null){
			throw new CommonException(new Exception(), MessageCodeDefine.USER_LOGIN_AGAIN);
		}
		List<Map> returnData = new ArrayList<Map>();
		try {
			List<Map> rowsList = null;
			Integer[] childLevels = CommonDefine.TREE.Childs.get(parentLevel);
			if (null != childLevels) {
				for (int nodeLevel : childLevels) {
					if (leafLevel < nodeLevel) {
						continue;
					}
					rowsList = commonManagerMapper.treeGetNodesByParent(
							parentId, parentLevel, nodeLevel, userId, TREE_DEFINE);
					//SDH网元仅有一个shelf时取消此层级
					boolean singleSdhShelf=false;
					if(CommonDefine.TREE.NODE.NE==parentLevel&&
						CommonDefine.TREE.NODE.SHELF==nodeLevel&&
						(rowsList.size()==1)){//网元下单shelf
						Map neMap=treeGetNode(parentId,parentLevel,userId);
						if(neMap!=null&&neMap.get("additionalInfo")!=null&&
							Integer.valueOf(CommonDefine.NE_TYPE_SDH_FLAG).equals(//SDH网元
									((Map)neMap.get("additionalInfo")).get("TYPE"))){
							singleSdhShelf=true;
							Map shelfMap=(Map)rowsList.get(0);
							rowsList=treeGetChildNodes(
									(Integer)shelfMap.get("nodeId"), 
									nodeLevel, leafLevel, userId);
							for(Map row:rowsList){
								row.put("parent", parentLevel+"-"+parentId);
								row.put("parentLevel", parentLevel);
								row.put("parentId", parentId);
							}
						}
					}
					if(!singleSdhShelf){
						rowsList = constructNodes(rowsList, nodeLevel, leafLevel,
							null,true);
					}
					returnData.addAll(rowsList);
				}
			}
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map treeGetNode(int nodeId, int nodeLevel, Integer userId) throws CommonException {
		if(userId==null){
			throw new CommonException(new Exception(), MessageCodeDefine.USER_LOGIN_AGAIN);
		}
		Map returnData = null;
		try {
			if (CommonDefine.TREE.NODE.ROOT == nodeLevel) {
				returnData = new HashMap();
				returnData.put("nodeId", CommonDefine.TREE.ROOT_ID);
				returnData.put("text", CommonDefine.TREE.ROOT_TEXT);
				returnData.put("domainAuth", Long.valueOf(CommonDefine.FALSE));
			} else {
				returnData = commonManagerMapper.treeGetNodeById(nodeId,
						nodeLevel, userId, TREE_DEFINE);
				if(returnData==null||returnData.isEmpty()){
					returnData = commonManagerMapper.treeGetNodeById(nodeId,
							nodeLevel, CommonDefine.USER_ADMIN_ID, TREE_DEFINE);
					if(returnData==null||returnData.isEmpty()){
						throw new CommonException(new Exception(), MessageCodeDefine.COM_EXCPT_ENTITY_NOT_FOUND);
					} else {
						throw new CommonException(new Exception(), MessageCodeDefine.COM_EXCPT_ACCESS_DENIED);
					}
				}
			}
			returnData = constructNode(returnData, nodeLevel, false, null);
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map treeGetNodesByKey(String key, int nodeId, int nodeLevel,
			int rootId, int rootLevel, boolean hasPath, int startNumber,
			int pageSize, Integer userId) throws CommonException {
		if(userId==null){
			throw new CommonException(new Exception(), MessageCodeDefine.USER_LOGIN_AGAIN);
		}
		Map returnData = new HashMap();
		try {
			List returnRows = new ArrayList();
			int total = 0;
			if (nodeId <= 0) {
				key=key.trim().replace("%", "\\%").replaceAll("  ", " ").replace(" ", "%");
				total = commonManagerMapper.treeCountNodesByText("%" + key
						+ "%", nodeLevel, rootId, rootLevel, userId, TREE_DEFINE);
				returnRows = commonManagerMapper.treeGetNodesByText("%" + key
						+ "%", nodeLevel, rootId, rootLevel, startNumber,
						pageSize, userId, TREE_DEFINE);
			} else {
				Map nodeMap = treeGetNode(nodeId, nodeLevel, userId);
				if (nodeMap != null) {
					total = 1;
					returnRows.add(nodeMap);
				}
			}
			returnRows = constructNodes(returnRows, nodeLevel,
					CommonDefine.TREE.NODE.LEAFMAX, null,!hasPath);
			if (hasPath && returnRows != null && total > 0) {
				List pathNodes = new ArrayList();
				for (Object row : returnRows) {
					Map nodeMap = (Map) row;
					Object parentIdObject = nodeMap.get("parentId");
					Object parentLevelObject = nodeMap.get("parentLevel");
					while (parentIdObject != null && parentLevelObject != null) {
						int parentId = (Integer) parentIdObject;
						int parentLevel = (Integer) parentLevelObject;
						Map parentNode = treeGetNode(parentId, parentLevel, CommonDefine.USER_ADMIN_ID);
						if ((parentNode != null)
								&& parentLevel >= rootLevel
								&& !(pathNodes.contains(parentNode) || returnRows
										.contains(parentNode))) {
							pathNodes.add(parentNode);
							if (parentId == rootId && parentLevel == rootLevel) {
								break;
							}
							parentIdObject = parentNode.get("parentId");
							parentLevelObject = parentNode.get("parentLevel");
						} else {
							break;
						}
					}
				}
				returnRows = constructNodes(returnRows, nodeLevel,
						CommonDefine.TREE.NODE.LEAFMAX,
						CommonDefine.TREE.CHECKED_ALL,!hasPath);// 带有"checked":"all"的为目标节点,没有的为路径节点
				returnRows.addAll(pathNodes);
				returnData.put("total", returnRows.size());
				returnData.put("rows", returnRows);
			} else {
				returnData.put("total", total);
				returnData.put("rows", returnRows);
			}
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}

	@SuppressWarnings("unchecked")
	public List<Map> constructNodes(List<Map> nodes, int nodeLevel,
			int leafLevel, String checked, boolean sort) throws CommonException {
		if (nodes != null) {
			for (Map node : nodes) {
				boolean leaf = false;
				if (nodeLevel > leafLevel) {
					leaf = true;
				} else if (nodeLevel == leafLevel) {
					Integer[] childsArray = CommonDefine.TREE.Childs
							.get(nodeLevel);
					if (null == childsArray) {
						leaf = true;
					} else {
						List childsList = Arrays.asList(childsArray);
						if (childsList.contains(nodeLevel)) {
							List<Map> eqLevelChilds = commonManagerMapper
									.treeGetNodesByParent((Integer) node
											.get("nodeId"), nodeLevel,
											nodeLevel, CommonDefine.USER_ADMIN_ID, TREE_DEFINE);
							if (null == eqLevelChilds
									|| eqLevelChilds.isEmpty()) {
								leaf = true;
							} else {
								leaf = false;
							}
						} else {
							leaf = true;
						}
					}
				}
				nodes.set(nodes.indexOf(node),constructNode(node, nodeLevel, leaf, checked));
			}
			if (sort&&nodes.size() > 1) {
				Collections.sort(nodes, new NodeComparator());
			}
		}
		return nodes;
	}

	@Override
	public Map getNodeInfo(int nodeId, int nodeLevel) throws CommonException {
		Map returnData = null;
		try {
			if (CommonDefine.TREE.NODE.ROOT == nodeLevel) {
				returnData = new HashMap();
				returnData.put("nodeId", CommonDefine.TREE.ROOT_ID);
				returnData.put("nodeLevel", CommonDefine.TREE.NODE.ROOT);
				returnData.put("text", CommonDefine.TREE.ROOT_TEXT);
			} else {
				returnData = commonManagerMapper.getNodeInfo(nodeId,
						nodeLevel, TREE_DEFINE);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}

	@Override
	public Map getNodesInfoByRoot(int rootId, int rootLevel, int nodeLevel,
			int startNumber, int pageSize) throws CommonException {
		Map returnData = new HashMap();
		try {
			List returnRows = new ArrayList();
			int total = 0;
			total = commonManagerMapper.CountNodesInfoByRoot(nodeLevel, rootId, rootLevel, TREE_DEFINE);
			if(total>0){
				returnRows = commonManagerMapper.getNodesInfoByRoot(nodeLevel, rootId, rootLevel, startNumber,
					pageSize, TREE_DEFINE);
			}
			returnData.put("total", total);
			returnData.put("rows", returnRows);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		}
		return returnData;
	}
	
	/** _______________________________ 共通树部分 _______________________________ * */
	//去除多余字符
	public String getMulitLevelFullName(int idValue, String tableName,
			String idName, String parentIdName, String displayName) {
		String result = getMulitLevelFullNameImpl(idValue,tableName,idName,parentIdName,displayName);
		if(result.endsWith("/")){
			result = result.substring(0,result.length()-1);
		}
		return result;
	}
	
	//迭代拼接名称
	private String getMulitLevelFullNameImpl(int idValue, String tableName,
			String idName, String parentIdName, String displayName) {

		String name = "";
		Map target = commonManagerMapper.selectTableById(tableName, idName,
				idValue);
		
		if(target == null){
			return name;
		}
		if(target.get(displayName)!=null){
			name = target.get(displayName).toString() + "/" + name;
		}

		Integer parentId = target.get(parentIdName) != null ? Integer
				.valueOf(target.get(parentIdName).toString()) : null;
		if (parentId != null) {
			name = getMulitLevelFullNameImpl(parentId, tableName, idName,
					parentIdName, displayName) + name;
		}
		return name.toString();
	}

	/**
	 * 单个文件上传方法
	 * 
	 * @param file
	 *            文件
	 * @param fileName
	 *            文件名
	 * @param uploadPath
	 *            上传路径
	 * @return
	 * @throws FileNotFoundException
	 */
	public boolean uploadFile(File file, String fileName, String uploadPath)
			throws CommonException, IOException, FileNotFoundException {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			File dir = new File(uploadPath);
			if (!dir.exists()) {
				dir.mkdirs();// 创建文件夹
			}
			if (osName.contains("Windows")) {
				fos = new FileOutputStream(uploadPath + "\\" + fileName);
			} else {
				fos = new FileOutputStream(uploadPath + fileName);
			}
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			fos.close();
			fis.close();
		}
		return true;
	}
	
	public Map getPermissionInfo(){
		
		SimpleDateFormat sf = CommonUtil.getDateFormatter(CommonDefine.COMMON_SIMPLE_FORMAT);
		
		Map licenseInfo = new LinkedHashMap();
		if(checkNeedToCheckLicense()){
			//激活时间
			Date startTime = null;
			try {
				startTime = sf.parse(CommonDefine.LICENSE
						.get(CommonDefine.LICENSE_KEY_START_TIME).trim().substring(0, 10));
			} catch (ParseException e) {
				startTime = new Date();
			}
			//可用时长
			int dayGap = Integer.valueOf(CommonDefine.LICENSE
					.get(CommonDefine.LICENSE_KEY_VALIDATE_TIME));
			//到期日期
			Date endTime = CommonUtil.getSpecifiedDay(startTime, dayGap, 0);
			//最近更新时间
			Date lastModify = new Date(Long.valueOf(CommonDefine.LICENSE
					.get(CommonDefine.LICENSE_KEY_LAST_MODIFIED)));
			
			licenseInfo.put("license限制", "有限制");
			licenseInfo.put("license激活时间", sf.format(startTime));
			licenseInfo.put("license到期时间", sf.format(endTime));
			licenseInfo.put("license最近更新时间", sf.format(lastModify));
			licenseInfo.put("支持网管数量", CommonDefine.LICENSE
					.get(CommonDefine.LICENSE_KEY_SUPPORT_NMS_NUMBER));
		}else{
			licenseInfo.put("license限制", "无限制");
			licenseInfo.put("license激活日期", "无");
			licenseInfo.put("license到期时间", "无");
			licenseInfo.put("license最近更新日期", "无");
			licenseInfo.put("支持网管数量", "无限制");
		}
		return licenseInfo;
	}
	
	public Map getAboutInfo() {

		Map aboutInfo = new LinkedHashMap();

		ResourceBundle bundle = ResourceBundle
				.getBundle("resourceConfig/systemConfig/" + "about");

		String version = bundle.getString("version");
//		String copyright = bundle.getString("copyright");

		aboutInfo.put("version", version);
//		try {
//			copyright = new String(
//					copyright
//							.getBytes("UTF-8"),"GBK");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		aboutInfo.put("Copyright", copyright);

		return aboutInfo;
	}
}
