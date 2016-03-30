package com.fujitsu.manager.viewManager.serviceImpl;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.TopoManagerMapper;
import com.fujitsu.manager.commonManager.service.CommonManagerService;
import com.fujitsu.manager.viewManager.service.TopoManagerService;
import com.fujitsu.model.CurrentAlarmModel;
import com.fujitsu.model.EMSInfoModel;
import com.fujitsu.model.LinkAlarmModel;
import com.fujitsu.model.TopoLineModel;
import com.fujitsu.model.TopoNodeModel;
import com.fujitsu.model.TopoTreeNeLikeModel;
import com.fujitsu.model.TopoTreeNodeModel;


public class TopoManagerServiceImpl extends TopoManagerService{
	
	@Resource
	private CommonManagerMapper commonManagerMapper;
	
	@Resource
	private TopoManagerMapper topoManagerMapper;
	
	@Resource
	private ICommonManagerService commonManagerService;
	
	@Resource
	private IAlarmManagementService alarmManagementService;

	/**
	 * 获取节点信息
	 * @@@分权分域到网元@@@
	 */
	public Map<String, Object> getNode(int nodeId,int nodeType,String direction,int userId,String privilege,boolean needAlarmInfo) throws CommonException
	{
		Map<String,Object> return_map = null;
		//先判断数据库的数据是否正常
//		boolean flag = checkData(nodeId,nodeType,userId);
//		
//		if(!flag){//数据库数据有变化
//			return_map = new HashMap<String,Object>();
//			return_map.put("total", -1);//-1代表数据库数据有变化
//			return return_map;
//		}
//		
		if(direction.equals("current")){//current是指显示的拓扑图中包含从前台 传过来的节点
			switch(nodeType){
			case CommonDefine.VIEW_TYPE_FTSP:
				break;
			case CommonDefine.VIEW_TYPE_EMSGROUP:
				nodeType = CommonDefine.VIEW_TYPE_FTSP;
				nodeId = CommonDefine.FTSP_NODE;//-1代表FTSP的节点ID
				break;
			case CommonDefine.VIEW_TYPE_EMS:
				Map currEMS = topoManagerMapper.getEMSByEMSId(nodeId,userId,CommonManagerService.TREE_DEFINE);
				Object emsGroup = currEMS.get("BASE_EMS_GROUP_ID");
				if(emsGroup != null && !emsGroup.toString().equals("")){
					nodeType = CommonDefine.VIEW_TYPE_EMSGROUP;
					nodeId = Integer.parseInt(currEMS.get("BASE_EMS_GROUP_ID").toString());
				}else{
					nodeType = CommonDefine.VIEW_TYPE_FTSP;
					nodeId = CommonDefine.FTSP_NODE;
				}
				break;
			case CommonDefine.VIEW_TYPE_SUBNET:
				Map currSubnet = topoManagerMapper.getSubnetBySubnetId(nodeId,userId,CommonManagerService.TREE_DEFINE);
				Object parentSubnet = currSubnet.get("PARENT_SUBNET");
				if(parentSubnet != null && !parentSubnet.toString().equals("")){
					nodeType = CommonDefine.VIEW_TYPE_SUBNET;
					nodeId = Integer.parseInt(currSubnet.get("PARENT_SUBNET").toString());
				}else{
					nodeType = CommonDefine.VIEW_TYPE_EMS;
					nodeId = Integer.parseInt(currSubnet.get("BASE_EMS_CONNECTION_ID").toString());
				}
				break;
			case CommonDefine.VIEW_TYPE_NE:
				Map currNe = topoManagerMapper.getNeByNeId(nodeId);
				Object neSubnet = currNe.get("BASE_SUBNET_ID");
				if(neSubnet != null && !neSubnet.toString().equals("")){
					nodeType = CommonDefine.VIEW_TYPE_SUBNET;
					nodeId = Integer.parseInt(currNe.get("BASE_SUBNET_ID").toString());
				}else{
					nodeType = CommonDefine.VIEW_TYPE_EMS;
					nodeId = Integer.parseInt(currNe.get("BASE_EMS_CONNECTION_ID").toString());
				}
				break;
			default:
				break;
			}
		}
		
		switch(nodeType){
		case CommonDefine.VIEW_TYPE_FTSP:     //FTSP
			return_map = getFTSP(userId,privilege);
			break;
		case CommonDefine.VIEW_TYPE_EMSGROUP:      //网管分组
			return_map = getEMSInGroup(nodeId,userId);
			break;
		case CommonDefine.VIEW_TYPE_EMS:      //网管
			return_map = getNodeInEMS(nodeId,userId,needAlarmInfo);
			break;
		case CommonDefine.VIEW_TYPE_SUBNET:      //子网
			return_map = getNodeInSubnet(nodeId,userId,needAlarmInfo);
			break;
		default:
			break;
		}
		//获取时隙预警值
		Map warnConfig = commonManagerMapper.selectTableById("t_nwa_warn_conf", "TYPE", 3);
		if(warnConfig!=null){
			return_map.put("bestThreshold",warnConfig.get("AVAILABILITY_WR"));
			return_map.put("midThreshold",warnConfig.get("AVAILABILITY_MN"));
			return_map.put("worstThreshold",warnConfig.get("AVAILABILITY_MJ"));
		}else{
			return_map.put("bestThreshold",50);
			return_map.put("midThreshold",30);
			return_map.put("worstThreshold",10);
		}
		if(return_map.get("colorBlockade") == null){
			return_map.put("colorBlockade","#FF0000");
		}
		if(return_map.get("colorMJ") == null){
			return_map.put("colorMJ","#FFC000");
		}
		if(return_map.get("colorMN") == null){
			return_map.put("colorMN","#FFFF00");
		}
		if(return_map.get("colorWR") == null){
			return_map.put("colorWR","#0070C0");
		}
		if(return_map.get("colorIdle") == null){
		return_map.put("colorIdle","#00FF00");
		}
		
		return return_map;
	}
	
	/** @@@分权分域到网元@@@
	 * 检查数据库的数据是否有变化
	 * @param nodeId
	 * @param nodeType
	 * @param userId
	 * @return
	 */
//	public boolean checkData(int nodeId, int nodeType, int userId) throws CommonException {
//		
//		boolean flag = false;
//		
//		switch(nodeType){
//		case CommonDefine.VIEW_TYPE_FTSP:
//			flag = true;
//			break;
//		case CommonDefine.VIEW_TYPE_EMSGROUP:
//			List<Map> allEMSGroup = commonManagerService.getAllEmsGroups(userId, false, false);
//			for(Map map : allEMSGroup){
//				int emsGroupId = Integer.valueOf(map.get("BASE_EMS_GROUP_ID").toString()).intValue();
//				if(emsGroupId == nodeId){
//					flag = true;
//				}
//			}
//			break;
//		case CommonDefine.VIEW_TYPE_EMS:
//			List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false);
//			for(Map map : allEMS){
//				int emsId = Integer.valueOf(map.get("BASE_EMS_CONNECTION_ID").toString()).intValue();
//				if(emsId == nodeId){
//					flag = true;
//				}
//			}
//			break;
//		case CommonDefine.VIEW_TYPE_SUBNET:
//			Map currSubnet = topoManagerMapper.getSubnetBySubnetId(nodeId);
//			if(currSubnet == null){
//				break;
//			}
//			int currSubentEMSId = Integer.valueOf(currSubnet.get("BASE_EMS_CONNECTION_ID").toString()).intValue();
//			List<Map> emsListSubnet = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false);
//			for(Map map : emsListSubnet){
//				int emsId = Integer.valueOf(map.get("BASE_EMS_CONNECTION_ID").toString()).intValue();
//				if(emsId == currSubentEMSId){
//					flag = true;
//				}
//			}
//			break;
//		case CommonDefine.VIEW_TYPE_NE:
//			Map currNe = topoManagerMapper.getNeByNeId(nodeId);
//			if(currNe == null){
//				break;
//			}
//			
//			int currNeEMSId = Integer.valueOf(currNe.get("BASE_EMS_CONNECTION_ID").toString()).intValue();
//			
//			List<Map> emsListNe = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false);
//			for(Map map : emsListNe){
//				int emsId = Integer.valueOf(map.get("BASE_EMS_CONNECTION_ID").toString()).intValue();
//				if(emsId == currNeEMSId){
//					flag = true;
//				}
//			}
//			break;
//		default:
//			break;
//		}
//		
//		return flag;
//	}
	
	public String createSubnetTitle(int subnetId,List<Map> allSubnet,Integer userId) {
		
		String title = "";
		//取出当前子网的记录
		Map currentSubnet = topoManagerMapper.getSubnetBySubnetId(subnetId,userId,CommonManagerService.TREE_DEFINE);
		int currentEMSId = Integer.parseInt(currentSubnet.get("BASE_EMS_CONNECTION_ID").toString());
		//获取当前子网所属网管的记录
		Map currentEMS = topoManagerMapper.getEMSByEMSId(currentEMSId,userId,CommonManagerService.TREE_DEFINE);
		if(currentEMS.get("DISPLAY_NAME") != null){
			title = currentEMS.get("DISPLAY_NAME").toString();
		}
		
		if(currentSubnet.get("PARENT_SUBNET") != null && !currentSubnet.get("PARENT_SUBNET").toString().equals("")){
			List<Integer> parentSubnetId = this.findAllSubnetParent(subnetId, allSubnet);
			for(int i=parentSubnetId.size();i>0;i--){
				Map parentSubnet = topoManagerMapper.getSubnetBySubnetId(parentSubnetId.get(i-1),userId,CommonManagerService.TREE_DEFINE);
				if(parentSubnet.get("DISPLAY_NAME") != null){
					title = title + ":" + parentSubnet.get("DISPLAY_NAME").toString();
				}
			}
			
			if(currentSubnet.get("DISPLAY_NAME") != null){
				title = title + ":" + currentSubnet.get("DISPLAY_NAME").toString();
			}
		}else{//直属网管
			if(currentSubnet.get("DISPLAY_NAME") != null){
				title = title + ":" + currentSubnet.get("DISPLAY_NAME").toString();
			}
		}
		
		return title;
	}
	
	/**
	 *  @@@分权分域到网元@@@
	 * @param nodeId
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> getNodeInSubnet(int nodeId,Integer userId,boolean needAlarmInfo) throws CommonException {
		
		//取出当前子网的记录
		Map currentSubnet = topoManagerMapper.getSubnetBySubnetId(nodeId,userId,CommonManagerService.TREE_DEFINE);
		int currentEMSId = Integer.parseInt(currentSubnet.get("BASE_EMS_CONNECTION_ID").toString());
		//获取当前子网所属网管的记录
//		Map currentEMS = topoManagerMapper.getEMSByEMSId(currentEMSId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前子网的直属子网
		List<Map> allDirectSubnet = topoManagerMapper.getDirectSubnetInSubnet(nodeId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前子网的直属网元
		List<Map> allDirectNe = topoManagerMapper.getDirectNeInSubnet(nodeId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前网管下的所有子网
		List<Map> allSubnet = topoManagerMapper.getAllSubnet(currentEMSId); 
		//获取当前网管下的所有link
		List<Map> allLink = topoManagerMapper.getLinkInEMS(currentEMSId,userId,CommonManagerService.TREE_DEFINE);
		
		List<TopoLineModel> lineList = new ArrayList<TopoLineModel>();
		
		//获取所有link上的ptpId
		List<Integer> allPtpIds = null;
		List<Map<String, Object>> allPtpRecords = null;
		List<Integer> allLinkNeIds = null;
		List<Map<String, Object>> allLinkNeRecords = null;
		
		if(allLink.size() > 0){
			allPtpIds = new ArrayList<Integer>();
			allLinkNeIds = new ArrayList<Integer>();
			for(Map<String, Object> link : allLink) {
				allPtpIds.add(Integer.parseInt(link.get("A_END_PTP").toString()));
				allPtpIds.add(Integer.parseInt(link.get("Z_END_PTP").toString()));
			}
			allPtpRecords = topoManagerMapper.
					getPtpListByPtpIds(removeRepeatInteger(allPtpIds));
			for(Map<String, Object> ptp : allPtpRecords){
				allLinkNeIds.add(Integer.parseInt(ptp.get("BASE_NE_ID").toString()));
			}
			allLinkNeRecords = topoManagerMapper.
					getNeListByNeIds(removeRepeatInteger(allLinkNeIds));
		}
		
		//加入link线
		for(Map link:allLink){
			//获取link两端的记录
			Map aPortRecord = getPtpRecordInList(Integer.parseInt(link.get("A_END_PTP").toString()), allPtpRecords);
			Map zPortRecord = getPtpRecordInList(Integer.parseInt(link.get("Z_END_PTP").toString()), allPtpRecords);
			
			if (aPortRecord == null || zPortRecord == null
					|| aPortRecord.get("BASE_NE_ID") == null
					|| zPortRecord.get("BASE_NE_ID") == null) {
				System.out.println("Link中出现的PTP已经被标记删除："
						+ Integer.parseInt(link.get("A_END_PTP").toString())
						+ "&&"
						+ Integer.parseInt(link.get("Z_END_PTP").toString()));
				continue;
			}
			
			int fromNeId = Integer.parseInt(aPortRecord.get("BASE_NE_ID").toString());
			int fromNode = CommonDefine.INVALID_VALUE;
			int toNode = CommonDefine.INVALID_VALUE;
			
			TopoLineModel line = new TopoLineModel();
			line.setNodeOrLine("line");
			line.setLinkId(Integer.valueOf(link.get("BASE_LINK_ID").toString()));
			//获取A端的网元
			Map fromNeRecord = getNeRecordInList(fromNeId, allLinkNeRecords);
			if (fromNeRecord == null) {
				System.out.println("Link中出现的A端网元已经被标记删除："+fromNeId);
				continue;				
			}
			
			if(fromNeRecord.get("BASE_SUBNET_ID") == null){//网元直属网管
				continue;
			}else if(Integer.parseInt(fromNeRecord.get("BASE_SUBNET_ID").toString()) == nodeId){   //a端网元直属当前子网
				fromNode = fromNeId;
				line.setFromNode(String.valueOf(fromNode));
				line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_NE));
			}else{    //A端网元不是直属于当前子网
				int parentId = Integer.parseInt(fromNeRecord.get("BASE_SUBNET_ID").toString());
				fromNode = findParentSubnet(parentId,allSubnet,allDirectSubnet);
				line.setFromNode(String.valueOf(fromNode));
				line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
			}
			
			if(fromNode == CommonDefine.INVALID_VALUE) continue;
			
			int toNeId = Integer.parseInt(zPortRecord.get("BASE_NE_ID").toString());
			//获取Z端网元记录
			Map toNeRecord = getNeRecordInList(toNeId, allLinkNeRecords);
			if (toNeRecord == null) {
				System.out.println("Link中出现的Z端网元已经被标记删除："+toNeId);
				continue;				
			}
			
			if(toNeRecord.get("BASE_SUBNET_ID") == null){
				continue;
			}else if(Integer.parseInt(toNeRecord.get("BASE_SUBNET_ID").toString()) == nodeId){   
				toNode = toNeId;
				line.setToNode(String.valueOf(toNode));
				line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_NE));
			}else{    //Z端网元不是直属于子网
				int parentId = Integer.parseInt(toNeRecord.get("BASE_SUBNET_ID").toString());
				toNode = findParentSubnet(parentId,allSubnet,allDirectSubnet);
				line.setToNode(String.valueOf(toNode));
				line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
			}
			
			if(toNode == CommonDefine.INVALID_VALUE) continue;
			
			if(!line.getFromNode().equals(line.getToNode()) || !line.getFromNodeType().equals(line.getToNodeType())){
				line.setLineType("neLine");
				//加入a端、z端信息
				if(line.getLinkAlarm() == null){
					line.setLinkAlarm(new ArrayList<LinkAlarmModel>());
				}
				LinkAlarmModel linkAlarmModel = setLinkInfo(link,aPortRecord,zPortRecord);
				if(fromNeRecord.get("TYPE") != null){
					linkAlarmModel.setaNeType(fromNeRecord.get("TYPE").toString());
				}
				if(toNeRecord.get("TYPE") != null){
					linkAlarmModel.setzNeType(toNeRecord.get("TYPE").toString());
				}
				line.getLinkAlarm().add(linkAlarmModel);
				
				lineList.add(line);
			}
		}
		
		//整合节点
		List<Object> rows = new ArrayList<Object>();
		
		//向告警模块发送的数据
		List<Integer> subnetIdList = new ArrayList<Integer>();
		//加入当前被双击的子网Id
		subnetIdList.add(nodeId);

		List<TopoNodeModel> directSubnetList = new ArrayList<TopoNodeModel>();
		
		//处理子网节点
		for(Map map:allDirectSubnet){
			
			TopoNodeModel subnet = transformSubnet(map);
			//加入当前子网下属的所有子网（直属和非直属）
			addAllSubnetIdClickSubnet(subnet,allSubnet);

			//判断该子网下有哪些类型的节点，例：SDH,OTN,WDM
			addNeType(subnet,userId);
			
			//加入当前直属子网自身的Id
			subnetIdList.add(Integer.parseInt(subnet.getNodeId()));
			
			//加入当前直属子网的所有子网
			if(subnet.getSubnetIdList() != null){
				for(String subnetIdStr : subnet.getSubnetIdList()){
					subnetIdList.add(Integer.parseInt(subnetIdStr));
				}
			}
			
			directSubnetList.add(subnet);
//			rows.add(subnet);
		}
		
		//加入告警信息，此处调用告警模块的接口
		List<CurrentAlarmModel> currentAlarmList = new ArrayList<CurrentAlarmModel>();
		//判断是否需要告警数据
		if(needAlarmInfo){
			currentAlarmList = alarmManagementService.getCurrentAlarmBySubnetForView(subnetIdList);
		}
		
		//向返回结果中加入子网节点
		List<Map<String, Object>> allNeInEMS = topoManagerMapper.
				getAllNeInEMS(currentEMSId, userId, CommonManagerService.TREE_DEFINE);
		
		for(TopoNodeModel directSubnetModel : directSubnetList){
			//向当前直属子网中加入当前告警
			addSubnetAlarm_Subnet(directSubnetModel, currentAlarmList, userId, allNeInEMS);
			
			rows.add(directSubnetModel);
		}
		
		List<Integer> emsIds = new ArrayList<Integer>();
		for(Map map:allDirectNe){
			if(map.get("BASE_EMS_CONNECTION_ID") != null) { 
				emsIds.add(Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID").toString()));
			}
		}
		
		List<Map<String, Object>> emsRecords = null;
		if(emsIds.size() > 0){
			emsRecords = topoManagerMapper.getEMSListByIds(removeRepeatInteger(emsIds),
					userId, CommonManagerService.TREE_DEFINE);
		}
		
		//处理网元节点
		for(Map map:allDirectNe){
			TopoNodeModel directNe = transformNe(map);
			//加入该网元所属的网管分组
			Map ems = getEMSRecordInList(Integer.valueOf(directNe.getEmsId()).intValue(),
					emsRecords);
			if(ems.get("BASE_EMS_GROUP_ID") != null && !ems.get("BASE_EMS_GROUP_ID").toString().equals("")){
				directNe.setEmsGroupId(ems.get("BASE_EMS_GROUP_ID").toString());
			}
			//向当前直属网元加入当前告警
			addNeAlarm(directNe,currentAlarmList);
			
			rows.add(directNe);
		}
		
		for(TopoLineModel line:lineList){
			//加入link线的当前告警
			addLinkAlarm(line,currentAlarmList);
		}
		
		//link添加端口可用统计数据
		if(!needAlarmInfo){	
			lineList = addLinkCTPAvailabilityData(lineList);
		}else{
			//将重复的link进行整合
			lineList = deleteRepeatLink(lineList);
		}
		
		//处理link线
		for(TopoLineModel line:lineList){
			rows.add(line);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		result.put("layout", "free");
		result.put("isFirstTopo", "no");
		result.put("title", createSubnetTitle(nodeId, allSubnet,userId));
		result.put("currentTopoType", "NetWork");
		result.put("parentType",CommonDefine.VIEW_TYPE_SUBNET);
		result.put("parentId",nodeId);
		result.putAll(getAlarmColorSet());
		
		return result;
	}
	
	/**双击子网时，获取直属子网的下层子网
	 * @param subnet
	 * @param allSubnet
	 */
	public void addAllSubnetIdClickSubnet(TopoNodeModel model,List<Map> allSubnet) {
		
		int currentSubnetId = Integer.parseInt(model.getNodeId());
		
		for(Map map : allSubnet){
			int subnetId = Integer.parseInt(map.get("BASE_SUBNET_ID").toString());
			List<Integer> parentList = findAllSubnetParent(subnetId,allSubnet);
			for(Integer parent : parentList){
				if(parent == currentSubnetId){
					if(model.getSubnetIdList() == null){
						model.setSubnetIdList(new ArrayList<String>());
					}
					
					model.getSubnetIdList().add(String.valueOf(subnetId));
				}
			}
		}
	}
	
	/**获取当前子网的所有父节点
	 * @param currentSubnetId
	 * @param allSubnet
	 * @return
	 */
	public List<Integer> findAllSubnetParent(int currentSubnetId,List<Map> allSubnet) {
		
		List<Integer> parentList = new ArrayList<Integer>();
		
		while(true){
			int parentId = findDirectSubnetParent(currentSubnetId,allSubnet);
			if(parentId != CommonDefine.INVALID_VALUE){
				parentList.add(parentId);
				currentSubnetId = parentId;
			}else{
				break;
			}
		}
		
		return parentList;
	}
	
	/**获取当前子网的直系父子网节点，返回-1则说明该子网直属网管
	 * @param currentSubnetId
	 * @param allSubnet
	 * @return
	 */
	public int findDirectSubnetParent(int currentSubnetId,List<Map> allSubnet) {
		
		int parentSubnetId = CommonDefine.INVALID_VALUE;
		boolean dataException = true;
		
		for(Map subnet:allSubnet){
			if(Integer.parseInt(subnet.get("BASE_SUBNET_ID").toString()) == currentSubnetId){
				dataException = false;
				if(subnet.get("PARENT_SUBNET") == null){
					break;
				}else{
					parentSubnetId = Integer.parseInt(subnet.get("PARENT_SUBNET").toString());
				}
			}
		}
		
		if(dataException){
			System.out.println("当前网管下的子网数据异常！");
			System.out.println("异常的父子网ID：" + currentSubnetId);
		}
		
		return parentSubnetId;
	}
	
	/**双击子网时，判断子网与被双击子网的关系
	 * @param subnetId
	 * @param allSubnet
	 * @param allDirectSubnet
	 * @return
	 */
	public int findParentSubnet(int subnetId,List<Map> allSubnet,List<Map> allDirectSubnet)	{
		
		List<Integer> allParent = findAllSubnetParent(subnetId,allSubnet);
		if(allParent.size() > 0){
			allParent.add(subnetId);
		}
		int result = CommonDefine.INVALID_VALUE;
//		allParent.add(subnetId);
//		
//		int parentId = subnetId;
//		//找到当前子网的所有父节点
//		for(Map subnet:allSubnet){
//			if(Integer.parseInt(subnet.get("BASE_SUBNET_ID").toString()) == parentId){
//				if(subnet.get("PARENT_SUBNET") == null){
//					break;
//				}else{
//					allParent.add(Integer.parseInt(subnet.get("PARENT_SUBNET").toString()));
//					parentId = Integer.parseInt(subnet.get("PARENT_SUBNET").toString());
//				}
//			}
//		}
		
		for(Integer parent:allParent){
			for(Map map:allDirectSubnet){
				if(Integer.parseInt(map.get("BASE_SUBNET_ID").toString()) == parent){
					result = parent;
				}
			}
		}

		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param nodeId
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> getNodeInEMS(int nodeId,Integer userId,boolean needAlarmInfo) throws CommonException {
		
		//获取当前网管记录
		Map currentEMS = topoManagerMapper.getEMSByEMSId(nodeId,userId,CommonManagerService.TREE_DEFINE);
		String displayName = "";
		//获取当前网管下所有的直属子网
		List<Map> allDirectSubnet = topoManagerMapper.getDirectSubnetInEMS(nodeId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前网管下所有的直属网元
		List<Map> allDirectNe = topoManagerMapper.getDirectNeInEMS(nodeId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前网管下所有的子网（直属和非直属）
		List<Map> allSubnet = topoManagerMapper.getAllSubnet(nodeId); 
		//获取当前网管下所有的link
//		List<Map> tmp = commonManagerService.getAllNeByEmsId(userId, 4, false, null);
		List<Map> allLink = topoManagerMapper.getLinkInEMS(nodeId,userId,CommonManagerService.TREE_DEFINE);
		
		List<TopoLineModel> lineList = new ArrayList<TopoLineModel>();
		
		//获取所有link上的ptpId
		List<Integer> allPtpIds = null;
		List<Map<String, Object>> allPtpRecords = null;
		List<Integer> allLinkNeIds = null;
		List<Map<String, Object>> allLinkNeRecords = null;
		
		if(allLink.size() > 0){
			allPtpIds = new ArrayList<Integer>();
			allLinkNeIds = new ArrayList<Integer>();
			for(Map<String, Object> link : allLink) {
				allPtpIds.add(Integer.parseInt(link.get("A_END_PTP").toString()));
				allPtpIds.add(Integer.parseInt(link.get("Z_END_PTP").toString()));
			}
			allPtpRecords = topoManagerMapper.
					getPtpListByPtpIds(removeRepeatInteger(allPtpIds));

			for(Map<String, Object> ptp : allPtpRecords){
				allLinkNeIds.add(Integer.parseInt(ptp.get("BASE_NE_ID").toString()));
			}
			allLinkNeRecords = topoManagerMapper.
					getNeListByNeIds(removeRepeatInteger(allLinkNeIds));
		}
		
		//组装link线
		for(Map link:allLink){//遍历所有link
			Map aPortRecord = getPtpRecordInList(Integer.parseInt(link.get("A_END_PTP").toString()), allPtpRecords);
			Map zPortRecord = getPtpRecordInList(Integer.parseInt(link.get("Z_END_PTP").toString()), allPtpRecords);
			
			if (aPortRecord == null || zPortRecord == null
					|| aPortRecord.get("BASE_NE_ID") == null
					|| zPortRecord.get("BASE_NE_ID") == null) {
				System.out.println("Link中出现的PTP已经被标记删除："
						+ Integer.parseInt(link.get("A_END_PTP").toString())
						+ "&&"
						+ Integer.parseInt(link.get("Z_END_PTP").toString()));
				continue;
			}
			//找到当前link的起始网元
			int fromNeId = Integer.parseInt(aPortRecord.get("BASE_NE_ID").toString());
			int fromNode;
			int toNode;
			
			TopoLineModel line = new TopoLineModel();
			line.setNodeOrLine("line");
			line.setLinkId(Integer.valueOf(link.get("BASE_LINK_ID").toString()));
			//获取起始网元的记录
			Map fromNeRecord = getNeRecordInList(fromNeId, allLinkNeRecords);
			if (fromNeRecord == null) {
				System.out.println("Link中出现的A端网元已经被标记删除："+fromNeId);
				continue;				
			}
			
			if(fromNeRecord.get("BASE_SUBNET_ID") == null){   //起始网元直属网管
				fromNode = fromNeId;
				line.setFromNode(String.valueOf(fromNode));
				line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_NE));
			}else{    //起始网元属于子网
				int parentId = Integer.parseInt(fromNeRecord.get("BASE_SUBNET_ID").toString());
				fromNode = findTopSubnet(parentId,allSubnet);
				line.setFromNode(String.valueOf(fromNode));
				line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
			}
			
			int toNeId = Integer.parseInt(zPortRecord.get("BASE_NE_ID").toString());

			//获取终点网元的记录
			Map toNeRecord = getNeRecordInList(toNeId, allLinkNeRecords);
			if (toNeRecord == null) {
				System.out.println("Link中出现的Z端网元已经被标记删除："+toNeId);
				continue;				
			}
			
			if(toNeRecord.get("BASE_SUBNET_ID") == null){   //终点网元直属网管
				toNode = toNeId;
				line.setToNode(String.valueOf(toNode));
				line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_NE));
			}else{    //终点网元属于子网
				int parentId = Integer.parseInt(toNeRecord.get("BASE_SUBNET_ID").toString());
				toNode = findTopSubnet(parentId,allSubnet);
				line.setToNode(String.valueOf(toNode));
				line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
			}
			
			if(!line.getFromNode().equals(line.getToNode()) || !line.getFromNodeType().equals(line.getToNodeType())){
				line.setLineType("neLine");
				//加入 link线的a端和z端信息
				if(line.getLinkAlarm() == null){
					line.setLinkAlarm(new ArrayList<LinkAlarmModel>());
				}
				LinkAlarmModel linkAlarmModel = setLinkInfo(link,aPortRecord,zPortRecord);
				if(fromNeRecord.get("TYPE") != null){
					linkAlarmModel.setaNeType(fromNeRecord.get("TYPE").toString());
				}
				if(toNeRecord.get("TYPE") != null){
					linkAlarmModel.setzNeType(toNeRecord.get("TYPE").toString());
				}
				line.getLinkAlarm().add(linkAlarmModel);
				
				lineList.add(line);
			}
		}
		
		List<Object> rows = new ArrayList<Object>();
		
		//加入告警信息，此处调用告警模块的接口
		List<CurrentAlarmModel> currentAlarmList = new ArrayList<CurrentAlarmModel>();
		//判断是否需要告警数据
		if(needAlarmInfo){
			currentAlarmList = alarmManagementService.getCurrentAlarmByEmsIdForView(nodeId);
		}
		
		//加入子网节点
		List<Map> pureSubnetList = getPureSubnetList(allDirectSubnet, allSubnet);
		List<Map<String, Object>> allNeInEMS = topoManagerMapper.
				getAllNeInEMS(nodeId,userId,CommonManagerService.TREE_DEFINE);
		
		for(Map map:allDirectSubnet){
			TopoNodeModel subnet = transformSubnet(map);
			
			//加入当前子网下属的所有子网（直属和非直属）
			if(pureSubnetList != null && pureSubnetList.size() > 0){
				addAllSubnetId(subnet, pureSubnetList);
			}
			
			//判断该子网下有哪些类型的节点，例：SDH,OTN,WDM
			addNeType(subnet,userId);
			
			//子网节点的当前告警
			addSubnetAlarm_EMS(subnet, currentAlarmList, userId, allNeInEMS);
			
			rows.add(subnet);
		}
		
		//加入网元节点
		for(Map map:allDirectNe){
			TopoNodeModel directNe = transformNe(map);
			//加入该网元所属的网管分组
			Map ems = topoManagerMapper.getEMSByEMSId(Integer.valueOf(directNe.getEmsId()).intValue(),
					userId,CommonManagerService.TREE_DEFINE);
			if(ems.get("BASE_EMS_GROUP_ID") != null && !ems.get("BASE_EMS_GROUP_ID").toString().equals("")){
				directNe.setEmsGroupId(ems.get("BASE_EMS_GROUP_ID").toString());
			}
			addNeAlarm(directNe,currentAlarmList);
			rows.add(directNe);
		}
		
		for(TopoLineModel line:lineList){
			//加入link线的当前告警
			addLinkAlarm(line,currentAlarmList);
//			rows.add(line);
		}
		
		//link添加端口可用统计数据
		if(!needAlarmInfo){	
			lineList = addLinkCTPAvailabilityData(lineList);
		}else{
			//将重复的link进行整合
			lineList = deleteRepeatLink(lineList);
		}
		for(TopoLineModel resultLine : lineList){
			rows.add(resultLine);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		result.put("layout", "free");
		result.put("isFirstTopo", "no");
		if(currentEMS.get("DISPLAY_NAME") != null){
			displayName = currentEMS.get("DISPLAY_NAME").toString();
		}
		result.put("title", displayName);
		result.put("currentTopoType", "NetWork");
		result.put("parentType",CommonDefine.VIEW_TYPE_EMS);
		result.put("parentId",nodeId);
		result.putAll(getAlarmColorSet());
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private List<Map> getPureSubnetList(List<Map> allDirectSubnet, List<Map> allSubnet) {
		
		List<Map> rv = new ArrayList<Map>();
		
		if(allSubnet != null && allDirectSubnet != null){
			for(Map am : allSubnet){
				boolean add = true;
				for(Map dm : allDirectSubnet){
					if(Integer.parseInt(am.get("BASE_SUBNET_ID").toString()) 
							== Integer.parseInt(dm.get("BASE_SUBNET_ID").toString())){
						add = false;
						break;
					}
				}
				if(add) rv.add(am);
			}
		}
		
		return rv;
	}
	
	private Map<String, Object> getNeRecordInList(int neId, List<Map<String, Object>> list) {
		
		Map<String, Object> rv = null;
		
		if(list != null && list.size() > 0){
			for(Map<String, Object> map : list){
				if(neId == Integer.parseInt(map.get("BASE_NE_ID").toString())){
					rv = map;
					break;
				}
			}
		}
		
		return rv;
	}
	
	private Map<String, Object> getEMSRecordInList(int emsId, List<Map<String, Object>> list) {
		
		Map<String, Object> rv = null;
		
		if(list != null && list.size() > 0){
			for(Map<String, Object> map : list){
				if(emsId == Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID").toString())){
					rv = map;
					break;
				}
			}
		}
		
		return rv;
	}
	
	private Map<String, Object> getPtpRecordInList(int ptpId, List<Map<String, Object>> list) {
		
		Map<String, Object> rv = null;
		
		if(list != null && list.size() > 0){
			for(Map<String, Object> map : list){
				if(ptpId == Integer.parseInt(map.get("BASE_PTP_ID").toString())){
					rv = map;
					break;
				}
			}
		}
		
		return rv;
	}
	
	/**
	 * 整型list去重
	 * @param data
	 */
	public List<Integer> removeRepeatInteger(List<Integer> data) {
		
		List<Integer> rv = new ArrayList<Integer>();
		
		if(data != null && data.size() > 0){
			for(Integer v:data){
				if(rv.indexOf(v)<0){
					rv.add(v);
				}
			}
		}
		
		return rv;
	}
	
	/**
	 * 获取系统使用的告警颜色
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getAlarmColorSet() throws CommonException {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map<String, Object> colorMap = alarmManagementService.getAlarmColorSet();
		result.put("colorCR", colorMap.get("PS_CRITICAL_IMAGE"));
		result.put("colorMJ", colorMap.get("PS_MAJOR_IMAGE"));
		result.put("colorMN", colorMap.get("PS_MINOR_IMAGE"));
		result.put("colorWR", colorMap.get("PS_WARNING_IMAGE"));
		result.put("colorCL", colorMap.get("PS_CLEARED_IMAGE"));
		
		return result;
	}
	
	/**将重复的link进行整合
	 * @param lineList
	 * @return
	 */
	public List<TopoLineModel> deleteRepeatLink(List<TopoLineModel> lineList) {
		
		List<TopoLineModel> resultLineList = new ArrayList<TopoLineModel>();
		
		for(TopoLineModel line : lineList){
//			String fromNodeId = line.getFromNode();
//			String fromNodeType = line.getFromNodeType();
//			String toNodeId = line.getToNode();
//			String toNodeType = line.getToNodeType();
			boolean isRepeat = false;
			//判断当前的link是否和result中的重复
			for(TopoLineModel resultLine : resultLineList){
				if(line.getFromNode().equals(resultLine.getFromNode()) &&
				   line.getFromNodeType().equals(resultLine.getFromNodeType()) &&
				   line.getToNode().equals(resultLine.getToNode()) &&
				   line.getToNodeType().equals(resultLine.getToNodeType())){
					
					isRepeat = true;
					resultLine.getLinkAlarm().add(line.getLinkAlarm().get(0));
				}
				
				if(line.getFromNode().equals(resultLine.getToNode()) &&
				   line.getFromNodeType().equals(resultLine.getToNodeType()) &&
				   line.getToNode().equals(resultLine.getFromNode()) &&
				   line.getToNodeType().equals(resultLine.getFromNodeType())){
					
					isRepeat = true;
					resultLine.getLinkAlarm().add(line.getLinkAlarm().get(0));
				}
			}
			
			if(!isRepeat){
				resultLineList.add(line);
			}
		}
		
		return resultLineList;
	}
	
	/**
	 * 从map转换为model（路由图用）
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public LinkAlarmModel transMap2LinkAlarmModel(Map<String,Object> map) throws CommonException {
		
		LinkAlarmModel model = new LinkAlarmModel();
		Set<String> keys = map.keySet();
		Method method = null;
		Class c = model.getClass();
		
		for(String key : keys){
			try {
				method = c.getDeclaredMethod("set" + key, String.class);
				method.invoke(model, map.get(key).toString());
			} catch (Exception e) {
				throw new CommonException(e,MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
			}
		}
		
		return model;
	}

	/**加入link线的当前告警
	 * @param line
	 * @param currentAlarmList
	 */
	public void addLinkAlarm(TopoLineModel line,List<CurrentAlarmModel> currentAlarmList) {
		
		LinkAlarmModel linkAlarmModel = line.getLinkAlarm().get(0);//在调用addLinkAlarm方法时，每条连接线中的link只有一条
		int aNeId = Integer.parseInt(linkAlarmModel.getaNeId());
		String aRackNo = linkAlarmModel.getaRackNo();
		String aShelfNo = linkAlarmModel.getaShelfNo();
		String aSlotNo = linkAlarmModel.getaSlotNo();
		String aPortNo = linkAlarmModel.getaPortNo();
		String aDomain = linkAlarmModel.getaDomain();
		
		int zNeId = Integer.parseInt(linkAlarmModel.getzNeId());
		String zRackNo = linkAlarmModel.getzRackNo();
		String zShelfNo = linkAlarmModel.getzShelfNo();
		String zSlotNo = linkAlarmModel.getzSlotNo();
		String zPortNo = linkAlarmModel.getzPortNo();
		String zDomain = linkAlarmModel.getzDomain();
		
		if(currentAlarmList != null){
			for(CurrentAlarmModel currentAlarm : currentAlarmList){
				if(currentAlarm.getObjectType() != 
						CommonDefine.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT){
					continue;
				}
				
				if(currentAlarm.getRackNo() == null ||
				   currentAlarm.getShelfNo() == null ||
				   currentAlarm.getSlotNo() == null ||
				   currentAlarm.getPortNo() == null ||
				   currentAlarm.getDomain() == null){  //如果当前告警信息不属于端口
					
					continue;
				}
				
				if(currentAlarm.getNeId() == aNeId && 
				   aRackNo.equals(currentAlarm.getRackNo()) &&
				   aShelfNo.equals(currentAlarm.getShelfNo()) &&
				   aSlotNo.equals(currentAlarm.getSlotNo()) &&
				   aPortNo.equals(currentAlarm.getPortNo()) &&
				   aDomain.equals(currentAlarm.getDomain())){  //如果当前告警信息发生在a端口
					
					addAPortAlarm(line,currentAlarm.getPerceivedSeverity());
				}
				
				if(currentAlarm.getNeId() == zNeId && 
				   zRackNo.equals(currentAlarm.getRackNo()) &&
				   zShelfNo.equals(currentAlarm.getShelfNo()) &&
				   zSlotNo.equals(currentAlarm.getSlotNo()) &&
				   zPortNo.equals(currentAlarm.getPortNo()) &&
				   zDomain.equals(currentAlarm.getDomain())){  //如果当前告警信息发生在z端口
					
					addZPortAlarm(line,currentAlarm.getPerceivedSeverity());
				}
			}
		}
	}
	
	
	/**加入link线的当前告警
	 * @param line
	 * @param currentAlarmList
	 */
	public List<TopoLineModel> addLinkCTPAvailabilityData(List<TopoLineModel> lineList ) {

		List<TopoLineModel> dataList = new ArrayList<TopoLineModel>();
		List<Map> linkCTPAvailabilityDataList = commonManagerMapper.selectTable("T_NWA_LINK_ANALYSIS", null, null);
		if(linkCTPAvailabilityDataList!=null){
			for(TopoLineModel line:lineList){
				Integer linkId =  line.getLinkId();
				for(Map linkCTPAvailabilityData:linkCTPAvailabilityDataList){
					Integer linkIdFromSource = Integer.valueOf(linkCTPAvailabilityData.get("BASE_LINK_ID").toString());
					if(linkId.intValue() == linkIdFromSource.intValue()){
						line.setUnoccupiedVc12Count(Integer.valueOf(linkCTPAvailabilityData.get("VC12_UNUSE").toString()));
						line.setUnoccupiedVc4Count(Integer.valueOf(linkCTPAvailabilityData.get("VC4_UNUSE").toString()));
						line.setVc12Total(Integer.valueOf(linkCTPAvailabilityData.get("VC12_TOTAL").toString()));
						line.setVc4Total(Integer.valueOf(linkCTPAvailabilityData.get("VC4_TOTAL").toString()));
						dataList.add(line);
						break;
					}
				}
			}
		}
		return dataList;
	}
	
	/**给a端加入告警
	 * @param line
	 * @param alarm
	 */
	public void addAPortAlarm(TopoLineModel line,String alarm) {
		
		int crCount = line.getLinkAlarm().get(0).getaCRCount();
		int mjCount = line.getLinkAlarm().get(0).getaMJCount();
		int mnCount = line.getLinkAlarm().get(0).getaMNCount();
		int wrCount = line.getLinkAlarm().get(0).getaWRCount();
		
		if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_CRITICAL))){
			line.getLinkAlarm().get(0).setaCRCount(crCount+1);
		}else if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_MAJOR))){
			line.getLinkAlarm().get(0).setaMJCount(mjCount+1);
		}else if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_MINOR))){
			line.getLinkAlarm().get(0).setaMNCount(mnCount+1);
		}else if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_WARNING))){
			line.getLinkAlarm().get(0).setaWRCount(wrCount+1);
		}
	}
	
	/**给z端加入告警
	 * @param line
	 * @param alarm
	 */
	public void addZPortAlarm(TopoLineModel line,String alarm) {
		
		int crCount = line.getLinkAlarm().get(0).getzCRCount();
		int mjCount = line.getLinkAlarm().get(0).getzMJCount();
		int mnCount = line.getLinkAlarm().get(0).getzMNCount();
		int wrCount = line.getLinkAlarm().get(0).getzWRCount();
		
		if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_CRITICAL))){
			line.getLinkAlarm().get(0).setzCRCount(crCount+1);
		}else if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_MAJOR))){
			line.getLinkAlarm().get(0).setzMJCount(mjCount+1);
		}else if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_MINOR))){
			line.getLinkAlarm().get(0).setzMNCount(mnCount+1);
		}else if(alarm.equals(Integer.toString(CommonDefine.ALARM_PS_WARNING))){
			line.getLinkAlarm().get(0).setzWRCount(wrCount+1);
		}
	}
	
	public void addSubnetAlarm_EMS(TopoNodeModel model,
			List<CurrentAlarmModel> currentAlarmList,Integer userId, 
			List<Map<String, Object>> allNe) {
		
		//加入自身当前告警
		addSingleSubnetAlarm_EMS(Integer.parseInt(model.getNodeId()), 
				model,currentAlarmList,userId,allNe);
		
		//加入下属子网节点的当前告警
		if(model.getSubnetIdList() != null){
			for(String subnetId : model.getSubnetIdList()){
				addSingleSubnetAlarm_EMS(Integer.parseInt(subnetId),
						model,currentAlarmList,userId,allNe);
			}
		}
	}
	
	public void addSingleSubnetAlarm_EMS(int currentSubnetId,TopoNodeModel directSubnet,
			List<CurrentAlarmModel> currentAlarmList,Integer userId, 
				List<Map<String, Object>> allNe) {
		
		for(CurrentAlarmModel alarm : currentAlarmList){
			if(alarm.getSubnetId() == currentSubnetId){  
				//获取当前网管下所有的直属网元
				List<Map<String, Object>> allDirectNe = getDirectNeInSubnet(currentSubnetId, allNe); 
				for(Map<String, Object> ne:allDirectNe){
					if(alarm.getNeId() == Integer.valueOf(ne.get("BASE_NE_ID").toString()).intValue()){ 
						String perceivedSeverity = alarm.getPerceivedSeverity();
						int neType = alarm.getNeType();
						
						switch(neType){
						case CommonDefine.NE_TYPE_SDH_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrSDHCount(directSubnet.getCrSDHCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjSDHCount(directSubnet.getMjSDHCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnSDHCount(directSubnet.getMnSDHCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrSDHCount(directSubnet.getWrSDHCount() + 1);
							}
							break;
						case CommonDefine.NE_TYPE_WDM_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrWDMCount(directSubnet.getCrWDMCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjWDMCount(directSubnet.getCrWDMCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnWDMCount(directSubnet.getMnWDMCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrWDMCount(directSubnet.getWrWDMCount() + 1);
							}
							break;
						case CommonDefine.NE_TYPE_OTN_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrOTNCount(directSubnet.getCrOTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjOTNCount(directSubnet.getMjOTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnOTNCount(directSubnet.getMnOTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrOTNCount(directSubnet.getWrOTNCount() + 1);
							}
							break;
						case CommonDefine.NE_TYPE_PTN_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrPTNCount(directSubnet.getCrPTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjPTNCount(directSubnet.getMjPTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnPTNCount(directSubnet.getMnPTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrPTNCount(directSubnet.getWrPTNCount() + 1);
							}
							break;
						default:
							break;
						}
						
						if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
							directSubnet.setCrCount(directSubnet.getCrCount() + 1);
						}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
							directSubnet.setMjCount(directSubnet.getMjCount() + 1);
						}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
							directSubnet.setMnCount(directSubnet.getMnCount() + 1);
						}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
							directSubnet.setWrCount(directSubnet.getWrCount() + 1);
						}
					}
				}
			}
		}
	}
	

	private List<Map<String, Object>> getDirectNeInSubnet(int subnetId, List<Map<String, Object>> allNe) {
		
		List<Map<String, Object>> rv = new ArrayList<Map<String, Object>>();
		
		if(allNe != null && allNe.size() > 0){
			for(Map<String, Object> ne : allNe){
				if(ne.get("BASE_SUBNET_ID") != null && 
					Integer.parseInt(ne.get("BASE_SUBNET_ID").toString()) == subnetId){
					rv.add(ne);
				}
			}
		}
		
		return rv;
	} 
	
	/**
	 * @@@分权分域到网元@@@
	 * 给子网节点加入自身及下属子网的当前告警
	 * @param model
	 * @param currentAlarmList
	 */
	public void addSubnetAlarm_Subnet(TopoNodeModel model,List<CurrentAlarmModel> currentAlarmList,
			Integer userId, List<Map<String, Object>> allNe) {
		
		//加入自身当前告警
		addSingleSubnetAlarm_Subnet(Integer.parseInt(model.getNodeId()), model,
				currentAlarmList, userId, allNe);
		
		//加入下属子网节点的当前告警
		if(model.getSubnetIdList() != null){
			for(String subnetId : model.getSubnetIdList()){
				addSingleSubnetAlarm_Subnet(Integer.parseInt(subnetId), model, 
						currentAlarmList, userId, allNe);
			}
		}
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * 给子网节点加入当前告警
	 * @param directNe
	 * @param currentAlarmList
	 */
	public void addSingleSubnetAlarm_Subnet(int currentSubnetId,TopoNodeModel directSubnet,
			List<CurrentAlarmModel> currentAlarmList,Integer userId, List<Map<String, Object>> allNe) { 
		
		for(CurrentAlarmModel alarm : currentAlarmList){
			if(alarm.getSubnetId() == currentSubnetId){  
				//获取当前网管下所有的直属网元
				List<Map<String, Object>> allDirectNe = getDirectNeInSubnet(currentSubnetId, allNe); 
				for(Map<String, Object> ne:allDirectNe){
					if(alarm.getNeId() == Integer.valueOf(ne.get("BASE_NE_ID").toString()).intValue()){ 
						String perceivedSeverity = alarm.getPerceivedSeverity();
						int neType = alarm.getNeType();
						
						switch(neType){
						case CommonDefine.NE_TYPE_SDH_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrSDHCount(directSubnet.getCrSDHCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjSDHCount(directSubnet.getMjSDHCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnSDHCount(directSubnet.getMnSDHCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrSDHCount(directSubnet.getWrSDHCount() + 1);
							}
							break;
						case CommonDefine.NE_TYPE_WDM_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrWDMCount(directSubnet.getCrWDMCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjWDMCount(directSubnet.getCrWDMCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnWDMCount(directSubnet.getMnWDMCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrWDMCount(directSubnet.getWrWDMCount() + 1);
							}
							break;
						case CommonDefine.NE_TYPE_OTN_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrOTNCount(directSubnet.getCrOTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjOTNCount(directSubnet.getMjOTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnOTNCount(directSubnet.getMnOTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrOTNCount(directSubnet.getWrOTNCount() + 1);
							}
							break;
						case CommonDefine.NE_TYPE_PTN_FLAG:
							if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
								directSubnet.setCrPTNCount(directSubnet.getCrPTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
								directSubnet.setMjPTNCount(directSubnet.getMjPTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
								directSubnet.setMnPTNCount(directSubnet.getMnPTNCount() + 1);
							}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
								directSubnet.setWrPTNCount(directSubnet.getWrPTNCount() + 1);
							}
							break;
						default:
							break;
						}
						
						if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
							directSubnet.setCrCount(directSubnet.getCrCount() + 1);
						}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
							directSubnet.setMjCount(directSubnet.getMjCount() + 1);
						}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
							directSubnet.setMnCount(directSubnet.getMnCount() + 1);
						}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
							directSubnet.setWrCount(directSubnet.getWrCount() + 1);
						}
					}
				}
			}
		}
	}
	
	
	/**给网元加入当前告警
	 * @param directNe
	 * @param currentAlarmList
	 */
	public void addNeAlarm(TopoNodeModel directNe,List<CurrentAlarmModel> currentAlarmList) {
		
		int crCount = 0;
		int mjCount = 0;
		int mnCount = 0;
		int wrCount = 0;
		int needAckAlmCount = 0;
		
		//加入告警信息
		for(CurrentAlarmModel neAlarm : currentAlarmList){
			
			if(neAlarm.getNeId() == Integer.parseInt(directNe.getNodeId())){
				String perceivedSeverity = neAlarm.getPerceivedSeverity();
				if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_CRITICAL){
					crCount++;
				}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MAJOR){
					mjCount++;
				}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_MINOR){
					mnCount++;
				}else if(Integer.valueOf(perceivedSeverity).intValue() == CommonDefine.ALARM_PS_WARNING){
					wrCount++;
				}
				if (neAlarm.getAckState() == CommonDefine.IS_ACK_NO) {
					needAckAlmCount++;
				}
			}
		}
		
		directNe.setCrCount(crCount);
		directNe.setMjCount(mjCount);
		directNe.setMnCount(mnCount);
		directNe.setWrCount(wrCount);
		directNe.setNeedAckAlmCount(needAckAlmCount);
	}
	
	/**
	 * 设置link信息
	 * @param link
	 * @param aEnd
	 * @param zEnd
	 * @return
	 */
	public LinkAlarmModel setLinkInfo(Map link, Map aEnd, Map zEnd) {
		
		LinkAlarmModel linkAlarmModel = new LinkAlarmModel();
		//加入linkId
		if(link.get("BASE_LINK_ID") != null){
			linkAlarmModel.setLinkId(Integer.valueOf(link.get("BASE_LINK_ID").toString()).intValue());
		}
		
		//a端
		if(aEnd.get("BASE_PTP_ID") != null){
			linkAlarmModel.setaEndPTP(aEnd.get("BASE_PTP_ID").toString());
		}
		if(aEnd.get("BASE_NE_ID") != null){
			linkAlarmModel.setaNeId(aEnd.get("BASE_NE_ID").toString());
		}
//		linkAlarmModel.setaNeType();
		if(aEnd.get("RACK_NO") != null){
			linkAlarmModel.setaRackNo(aEnd.get("RACK_NO").toString());
		}
		if(aEnd.get("SHELF_NO") != null){
			linkAlarmModel.setaShelfNo(aEnd.get("SHELF_NO").toString());
		}
		if(aEnd.get("SLOT_NO") != null){
			linkAlarmModel.setaSlotNo(aEnd.get("SLOT_NO").toString());
		}
		if(aEnd.get("PORT_NO") != null){
			linkAlarmModel.setaPortNo(aEnd.get("PORT_NO").toString());
		}
		if(aEnd.get("DOMAIN") != null){
			linkAlarmModel.setaDomain(aEnd.get("DOMAIN").toString());
		}
		
		//z端
		if(zEnd.get("BASE_PTP_ID") != null){
			linkAlarmModel.setzEndPTP(zEnd.get("BASE_PTP_ID").toString());
		}
		if(zEnd.get("BASE_NE_ID") != null){
			linkAlarmModel.setzNeId(zEnd.get("BASE_NE_ID").toString());
		}
//		linkAlarmModel.setzNeType();
		if(zEnd.get("RACK_NO") != null){
			linkAlarmModel.setzRackNo(zEnd.get("RACK_NO").toString());
		}
		if(zEnd.get("SHELF_NO") != null){
			linkAlarmModel.setzShelfNo(zEnd.get("SHELF_NO").toString());
		}
		if(zEnd.get("SLOT_NO") != null){
			linkAlarmModel.setzSlotNo(zEnd.get("SLOT_NO").toString());
		}
		if(zEnd.get("PORT_NO") != null){
			linkAlarmModel.setzPortNo(zEnd.get("PORT_NO").toString());
		}
		if(zEnd.get("DOMAIN") != null){
			linkAlarmModel.setzDomain(zEnd.get("DOMAIN").toString());
		}
		
		return linkAlarmModel;
	}
	
	
	/**向返回的子网节点中加入所有下属网元的类型，例SDH,OTN,WDM
	 * @param subnet
	 */
	public void addNeType(TopoNodeModel subnet,Integer userId) {
		
		boolean result = false;
		result = setNeType(Integer.parseInt(subnet.getNodeId()),subnet,userId);
		
		if(result) return;

		if(subnet.getSubnetIdList() != null){
			for(String id : subnet.getSubnetIdList()){
				result = setNeType(Integer.parseInt(id),subnet,userId);
				if(result) return;
			}
		}
	}
	
	/**设置网元类型
	 * @param subnetId
	 * @param model
	 * @return
	 */
	public boolean setNeType(int subnetId,TopoNodeModel model,Integer userId) {
		
		boolean result = false;
		
		boolean hasSDH = false;
		boolean hasOTN = false;
		boolean hasWDM = false;
		boolean hasPTN = false;
		if(model.getHasSDH() != null && model.getHasSDH().equals("1")){
			hasSDH = true;
		}
		
		if(model.getHasWDM() != null && model.getHasWDM().equals("1")){
			hasWDM = true;
		}
		
		if(model.getHasOTN() != null && model.getHasOTN().equals("1")){
			hasOTN = true;
		}
		if(model.getHasPTN() != null && model.getHasPTN().equals("1")){
			hasPTN = true;
		}
		
		//先判断当前子网的直系网元
		List<Map> allDirectNe = topoManagerMapper.getDirectNeInSubnet(subnetId,userId,CommonManagerService.TREE_DEFINE);
		
		for(Map directNe : allDirectNe){
			if(directNe.get("TYPE") != null){
				int type = Integer.parseInt(directNe.get("TYPE").toString());
				switch(type){
				case 1:
					hasSDH = true;
					break;
				case 2:
					hasWDM = true;
					break;
				case 3:
					hasOTN = true;
					break;
				case 4:
					hasPTN = true;
				default:
					break;
				}
			}
			
			if(hasSDH && hasWDM && hasOTN && hasPTN){
				result = true;
				break;
			}
		}
		
		if(hasSDH){
			model.setHasSDH("1");
		}
		if(hasWDM){
			model.setHasWDM("1");
		}
		if(hasOTN){
			model.setHasOTN("1");
		}
		if(hasPTN){
			model.setHasPTN("1");
		}
		
		return result;
	}
	
	/**向子网中加入下属所有子网的Id(用于双击网管时)
	 * @param subnet
	 * @param allSubnet
	 */
	public void addAllSubnetId(TopoNodeModel subnet,List<Map> allSubnet) {
		
		int parentId = 0;
		int fatherSubnetId = Integer.parseInt(subnet.getNodeId());
		for(Map map : allSubnet){
			if(map.get("PARENT_SUBNET") == null){//如果是最高层子网，则跳过此次循环
				continue;
			}
			
			int currentSubnetId = Integer.parseInt(map.get("BASE_SUBNET_ID").toString());
			parentId = findTopSubnet(currentSubnetId,allSubnet);
			if(parentId == fatherSubnetId){
				if(subnet.getSubnetIdList() == null){
					subnet.setSubnetIdList(new ArrayList<String>());
				}
				subnet.getSubnetIdList().add(String.valueOf(currentSubnetId));
			}
		}
	}
	
	//获取下层子网所属的最高层父子网
//	public int findParent(int subnetId,List<Map> allSubnet) {
//		
//		int parentId = 0;
//		for(Map subnet:allSubnet){
//			if(Integer.parseInt(subnet.get("BASE_SUBNET_ID").toString()) == subnetId){
//				if(subnet.get("PARENT_SUBNET") == null){
//					return subnetId;
//				}else{
//					parentId = Integer.parseInt(subnet.get("PARENT_SUBNET").toString());
//				}
//			}
//		}
//		
//		return findParent(parentId,allSubnet);
//	}
	
	/**
	 * 获取网元属性
	 * @param nodeId
	 * @return
	 */
	public Map<String, Object> getNeAttributes(int nodeId) throws CommonException {
		
		Map ne = topoManagerMapper.getNeByNeId(nodeId);
		
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
		//网元名称
		String displayName = "";
		//网元型号
		String productName = "";
		//网元状态
		String communicationState = "";
		//网元类型
		String type = "";
		//厂家
		String factory = "";
		//基本同步状态
		String basicSyncStatus = "";
		//基本同步时间
		String basicSyncTime = "";
		//MSTP同步状态
		String MSTPSyncStatus = "";
		//MSTP同步时间
		String MSTPSyncTime = "";
		//交叉同步状态
		String CRSSyncStatus = "";
		//交叉同步时间
		String CRSSyncTime = "";
		//性能采集等级
		String NELevel = "";
		//最近采集时间
		String lastCollectTime = "";
		
		Map<String,Object> result = new HashMap<String,Object>();
		if(ne.get("DISPLAY_NAME") != null){
			displayName = ne.get("DISPLAY_NAME").toString();
		}
		if(ne.get("PRODUCT_NAME") != null){
			productName = ne.get("PRODUCT_NAME").toString();
		}
		if(ne.get("COMMUNICATION_STATE") != null){
			communicationState = ne.get("COMMUNICATION_STATE").toString();
		}
		if(ne.get("TYPE") != null){
			type = ne.get("TYPE").toString();
		}
		if(ne.get("FACTORY") != null){
			factory = ne.get("FACTORY").toString();
		}
		if(ne.get("BASIC_SYNC_STATUS") != null){
			basicSyncStatus = ne.get("BASIC_SYNC_STATUS").toString();
		}
		if(ne.get("BASIC_SYNC_TIME") != null){
			basicSyncTime = sf.format(ne.get("BASIC_SYNC_TIME"));
		}
		if(ne.get("MSTP_SYNC_STATUS") != null){
			MSTPSyncStatus = ne.get("MSTP_SYNC_STATUS").toString();
		}
		if(ne.get("MSTP_SYNC_TIME") != null){
			MSTPSyncTime = sf.format(ne.get("MSTP_SYNC_TIME"));
		}
		if(ne.get("CRS_SYNC_STATUS") != null){
			CRSSyncStatus = ne.get("CRS_SYNC_STATUS").toString();
		}
		if(ne.get("CRS_SYNC_TIME") != null){
			CRSSyncTime = sf.format(ne.get("CRS_SYNC_TIME"));
		}
		if(ne.get("NE_LEVEL") != null){
			NELevel = ne.get("NE_LEVEL").toString();
		}
		if(ne.get("LAST_COLLECT_TIME") != null){
			lastCollectTime = sf.format(ne.get("LAST_COLLECT_TIME"));
		}
		
		result.put("displayName",displayName);
		result.put("productName",productName);
		result.put("communicationState",communicationState);
		result.put("type",type);
		result.put("factory",factory);
		result.put("basicSyncStatus",basicSyncStatus);
		result.put("basicSyncTime",basicSyncTime);
		result.put("MSTPSyncStatus",MSTPSyncStatus);
		result.put("MSTPSyncTime",MSTPSyncTime);
		result.put("CRSSyncStatus",CRSSyncStatus);
		result.put("CRSSyncTime",CRSSyncTime);
		result.put("NELevel",NELevel);
		result.put("lastCollectTime",lastCollectTime);
		
		return result;
	}
	
	/**获取非顶层子网的最高父子网
	 * @param subnetId
	 * @param allSubnet
	 * @return
	 */
	public int findTopSubnet(int subnetId,List<Map> allSubnet) {
		
		//获取当前子网的所有父节点
		List<Integer> parentList = findAllSubnetParent(subnetId,allSubnet);
		
		int parentId = subnetId;
		if(parentList.size() > 0){
			parentId = parentList.get(parentList.size()-1);
		}
		
		return parentId;
	}
	
	public Map<String,Object> getEMSInGroup(int nodeId,int userId) throws CommonException
	{
		Map emsGroup = topoManagerMapper.getEMSGroupByEMSGroupId(nodeId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前用户可以看到的所有网管ID
		List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, nodeId, false, false);

		List<Object> nodeList = new ArrayList<Object>();
		String displayName = "";
		if(emsGroup.get("GROUP_NAME") != null){
			displayName = emsGroup.get("GROUP_NAME").toString();
		}
		
		for(Map map:allEMS){
			nodeList.add(transformEMS(map));
		}

		List<TopoLineModel> lineList = new ArrayList<TopoLineModel>();
		for(Object node:nodeList){
			TopoLineModel line = new TopoLineModel();
			line.setFromNode("ftsp");
			line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
			line.setToNode(((TopoNodeModel)node).getNodeId());
			line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
			line.setNodeOrLine("line");
			line.setConnectStatus(((TopoNodeModel)node).getConnectStatus());
			line.setTipString(((TopoNodeModel)node).getDisplayName());
			line.setLineType("emsLine");
			lineList.add(line);
		}

		TopoNodeModel ftsp = new TopoNodeModel();
		ftsp.setNodeId("ftsp");
		ftsp.setDomainAuth("1");
		ftsp.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
		ftsp.setNodeOrLine("node");
		nodeList.add(ftsp);
		
		for(TopoLineModel line:lineList){
			nodeList.add(line);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", nodeList.size());
		result.put("rows", nodeList);
		result.put("layout", "round");
		result.put("title", "EMS拓扑:" + displayName);
		result.put("isFirstTopo", "no");
		result.put("currentTopoType", "EMS");
		result.put("parentType",CommonDefine.VIEW_TYPE_EMSGROUP);
		result.put("parentId",nodeId);
		result.putAll(getAlarmColorSet());
		
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param userId
	 * @param privilege
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getFTSP(int userId,String privilege) throws CommonException
	{
		//获取所有的网管分组
//		List<Map> allEMSGroup = topoManagerMapper.getAllEMSGroup();
		List<Map> allEMSGroup = commonManagerService.getAllEmsGroups(userId, false, false, false);
		//获取该用户可以看到的所有网管ID
		List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false, false);

		//将所有的网管分组、网管用ArrayList进行保存
		List<TopoNodeModel> emsGroupList = new ArrayList<TopoNodeModel>();
		List<TopoNodeModel> emsList = new ArrayList<TopoNodeModel>();
		
		//将网管分组和网管转换成model形式
		for(Map map:allEMSGroup){
			emsGroupList.add(transformEMSGroup(map));
		}
		
		for(Map map:allEMS){
			emsList.add(transformEMS(map));
		}

		List<Object> rows = new ArrayList<Object>();
		
		//遍历所有的网管
		for(TopoNodeModel ems:emsList){
			Boolean isAdded = false;
			//遍历所有的网管分组
			for(TopoNodeModel group:emsGroupList){
				//如果网管不是FTSP的直属网管，并且找到了它所属的网管分组
				if(ems.getParentId() != null && ems.getParentId().equals(group.getNodeId())){
					//将当前网管名加入它所属的网管分组
					if(group.getChildrenName() == null){
						group.setChildrenName("");
						group.setChildrenName(group.getChildrenName() + ems.getDisplayName() + ";");
					}else{
						group.setChildrenName(group.getChildrenName() + ems.getDisplayName() + ";");
					}
					
					//将网管分组下属网管的信息带至前台
					EMSInfoModel emsInfoModel = new EMSInfoModel();
					if(group.getChildrenEMS() == null){
						group.setChildrenEMS(new ArrayList<EMSInfoModel>());
					}
					emsInfoModel.setEmsId(ems.getNodeId());
					emsInfoModel.setConnectStatus(ems.getConnectStatus());
					group.getChildrenEMS().add(emsInfoModel);
					
					isAdded = true;
					break;
				}
			}
			
			if(isAdded){
				isAdded = false;
			}else{   //不属于网管分组
				rows.add(ems);
			}
		}
		
		//加入所有的网管分组
		for(TopoNodeModel group:emsGroupList){
			if(group.getChildrenEMS() != null){
				rows.add(group);
			}
		}
		
		//加入连接线
		List<TopoLineModel> lineList = new ArrayList<TopoLineModel>();
		for(Object node:rows){
			TopoLineModel line = null;
			if(((TopoNodeModel)node).getNodeType().equals(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP))){   //FTSP与网管分组
				line = new TopoLineModel();
				line.setFromNode("ftsp");
				line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
				line.setToNode(((TopoNodeModel)node).getNodeId());
				line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
				line.setNodeOrLine("line");
				line.setLineType("emsGroupLine");
				line.setTipString(((TopoNodeModel)node).getChildrenName());
				//加入所有下属网管的连接状态
				if(((TopoNodeModel)node).getChildrenEMS() != null){
					line.setEmsConnectStatus(((TopoNodeModel)node).getChildrenEMS());
				}
				
			}else{          //FTSP与网管
				line = new TopoLineModel();
				line.setFromNode("ftsp");
				line.setFromNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
				line.setToNode(((TopoNodeModel)node).getNodeId());
				line.setToNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
				line.setNodeOrLine("line");
				line.setLineType("emsLine");
				line.setConnectStatus(((TopoNodeModel)node).getConnectStatus());
				line.setTipString(((TopoNodeModel)node).getDisplayName());
			}
			
			lineList.add(line);
		}
		
		//加入FTSP节点
		TopoNodeModel ftsp = new TopoNodeModel();
		ftsp.setNodeId("ftsp");
		ftsp.setDomainAuth("1");
		ftsp.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
		ftsp.setNodeOrLine("node");
		rows.add(ftsp);
		
		//向返回结果中加入连接线
		for(TopoLineModel line:lineList){
			rows.add(line);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("layout", "round");//圆形布局
		result.put("total", rows.size());
		result.put("rows", rows);
		result.put("title", "EMS拓扑");
		result.put("isFirstTopo", "yes");
		if(privilege != null){
			result.put("privilege", privilege);
		}else{
			result.put("privilege", "");
		}
		result.put("currentTopoType", "EMS");
		result.put("parentType", CommonDefine.VIEW_TYPE_FTSP);
		result.put("parentId", CommonDefine.INVALID_VALUE);
		result.putAll(getAlarmColorSet());
		
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoNodeModel transformEMSGroup(Map map)
	{
		TopoNodeModel model = new TopoNodeModel();
		model.setNodeId(map.get("BASE_EMS_GROUP_ID").toString());
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
		model.setNodeOrLine("node");
		
		if(map.get("GROUP_NAME") != null){
			model.setDisplayName(map.get("GROUP_NAME").toString());
		}
		
		if(map.get("POSITION_X") != null){
			model.setPosition_X(map.get("POSITION_X").toString());
		}
		
		if(map.get("POSITION_Y") != null){
			model.setPosition_Y(map.get("POSITION_Y").toString());
		}
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoNodeModel transformEMS(Map map)
	{
		TopoNodeModel model = new TopoNodeModel();
		model.setNodeId(map.get("BASE_EMS_CONNECTION_ID").toString());
		
		if(map.get("BASE_EMS_GROUP_ID") != null){
			model.setParentId(map.get("BASE_EMS_GROUP_ID").toString());
		}
		
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
		model.setNodeOrLine("node");
		
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
		
		if(map.get("CONNECT_STATUS") != null){
			model.setConnectStatus(map.get("CONNECT_STATUS").toString());
		}else{
			model.setConnectStatus("1");
		}
		
		if(map.get("POSITION_X") != null){
			model.setPosition_X(map.get("POSITION_X").toString());
		}
		
		if(map.get("POSITION_Y") != null){
			model.setPosition_Y(map.get("POSITION_Y").toString());
		}

		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoNodeModel transformSubnet(Map map){
		TopoNodeModel model = new TopoNodeModel();
		model.setNodeOrLine("node");
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
		model.setEmsId(map.get("BASE_EMS_CONNECTION_ID").toString());
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
		
		model.setNodeId(map.get("BASE_SUBNET_ID").toString());
		
		if(map.get("POSITION_X") != null){
			model.setPosition_X(map.get("POSITION_X").toString());
		}
		
		if(map.get("POSITION_Y") != null){
			model.setPosition_Y(map.get("POSITION_Y").toString());
		}
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoNodeModel transformNe(Map map){
		TopoNodeModel model = new TopoNodeModel();
		
		model.setNodeOrLine("node");
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_NE));
		model.setEmsId(map.get("BASE_EMS_CONNECTION_ID").toString());
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
		
		model.setNodeId(map.get("BASE_NE_ID").toString());
		
		if(map.get("POSITION_X") != null){
			model.setPosition_X(map.get("POSITION_X").toString());
		}
		
		if(map.get("POSITION_Y") != null){
			model.setPosition_Y(map.get("POSITION_Y").toString());
		}
		
		if(map.get("TYPE") != null){
			model.setNeType(map.get("TYPE").toString());
		}
		
		if (map.get("PRODUCT_NAME") != null) {
			model.setProductName(map.get("PRODUCT_NAME").toString());
		}
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		} 
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public void savePosition(List<String> positionArray) throws CommonException {
		
		for(String node:positionArray){
			String[] nodeInfo = node.split(",");
			//domainAuth为1权限,可保存按钮布局
			if(nodeInfo[4].equals("1")){
				switch(Integer.parseInt(nodeInfo[1])){
				case CommonDefine.VIEW_TYPE_EMSGROUP:  //网管分组
					topoManagerMapper.saveEMSGroupPosition(Integer.parseInt(nodeInfo[0]),
							Integer.parseInt(nodeInfo[2]),Integer.parseInt(nodeInfo[3]));
					break;
				case CommonDefine.VIEW_TYPE_EMS:   //网管
					topoManagerMapper.saveEMSPosition(Integer.parseInt(nodeInfo[0]),
							Integer.parseInt(nodeInfo[2]),Integer.parseInt(nodeInfo[3]));
					break;
				case CommonDefine.VIEW_TYPE_SUBNET:   //子网
					topoManagerMapper.saveSubnetPosition(Integer.parseInt(nodeInfo[0]),
							Integer.parseInt(nodeInfo[2]),Integer.parseInt(nodeInfo[3]));
					break;
				case CommonDefine.VIEW_TYPE_NE:   //网元
					topoManagerMapper.saveNePosition(Integer.parseInt(nodeInfo[0]),
							Integer.parseInt(nodeInfo[2]),Integer.parseInt(nodeInfo[3]));
					break;
				default:
					break;
				}
			}
		}
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * 将网管分组转换为对象树model
	 * @param map
	 * @return
	 */
	private TopoTreeNodeModel transEMSGroup2TopoTreeNodeModel(Map map) {
		
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		model.setNodeId(map.get("BASE_EMS_GROUP_ID").toString());
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
		if(map.get("GROUP_NAME") != null){
			model.setDisplayName(map.get("GROUP_NAME").toString());
		}
		model.setParentNodeId(String.valueOf(CommonDefine.INVALID_VALUE));
		model.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * 将网管转换为对象树model
	 * @param map
	 * @return
	 */
	private TopoTreeNodeModel transEMS2TopoTreeNodeModel(Map map) {
		
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		model.setNodeId(map.get("BASE_EMS_CONNECTION_ID").toString());
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * 刷新对象树
	 * @param expandedNodeArray
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> refreshTree(List<String> expandedNodeArray, int userId) throws CommonException {
		
		Map<String,Object> result = new HashMap<String,Object>();
		List<TopoTreeNodeModel> list = new ArrayList<TopoTreeNodeModel>();
		
		for(String node:expandedNodeArray){
			String[] nodeInfo = node.split(",");
			switch(Integer.parseInt(nodeInfo[1])){
			case CommonDefine.VIEW_TYPE_FTSP://FTSP节点
				//获取所有的网管分组
//				List<Map> allEMSGroup = topoManagerMapper.getAllEMSGroup();
				List<Map> allEMSGroup = commonManagerService.getAllEmsGroups(userId, false, false, false);
				
				//获取当前用户可见的所有网管ID
				List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false, false);
				
				//将网管分组转换成model形式
				for(Map map:allEMSGroup){
					//过滤掉空网管分组
					for(Map ems:allEMS){
						if(ems.get("BASE_EMS_GROUP_ID") != null && !map.get("BASE_EMS_GROUP_ID").toString().equals("")){
							if(Integer.valueOf(map.get("BASE_EMS_GROUP_ID").toString()).intValue() == 
									Integer.valueOf(ems.get("BASE_EMS_GROUP_ID").toString()).intValue()){
								list.add(transEMSGroup2TopoTreeNodeModel(map));
								break;
							}
						}
					}
				}
				
				//将网管转换成model形式
				for(Map map:allEMS){
					if(map.get("BASE_EMS_GROUP_ID") != null && !map.get("BASE_EMS_GROUP_ID").toString().equals("")){
						
					}else{
						TopoTreeNodeModel emsInFTSPModel = transEMS2TopoTreeNodeModel(map);
						emsInFTSPModel.setParentNodeId(String.valueOf(CommonDefine.INVALID_VALUE));
						emsInFTSPModel.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_FTSP));
						list.add(emsInFTSPModel);
					}
				}
				break;
			case CommonDefine.VIEW_TYPE_EMSGROUP://网管分组节点				
				//获取当前用户可见的所有网管ID
				List<Map> allEMSInEMSGroup = commonManagerService.getAllEmsByEmsGroupId(userId, Integer.parseInt(nodeInfo[0]), false, false);

				for(Map emsInEMSGroup:allEMSInEMSGroup){
					TopoTreeNodeModel emsInEMSGroupModel = transEMS2TopoTreeNodeModel(emsInEMSGroup);
					emsInEMSGroupModel.setParentNodeId(nodeInfo[0]);
					emsInEMSGroupModel.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
					list.add(emsInEMSGroupModel);
				}
				break;
			case CommonDefine.VIEW_TYPE_EMS://网管节点
				List<Map> allSubnetInEMS = topoManagerMapper.getDirectSubnetInEMS(Integer.parseInt(nodeInfo[0]),userId,CommonManagerService.TREE_DEFINE);
				for(Map subnetInEMS:allSubnetInEMS){
					list.add(transSubnetTree(subnetInEMS));
				}
				List<Map> allNeInEMS = topoManagerMapper.getDirectNeInEMS(Integer.parseInt(nodeInfo[0]),userId,CommonManagerService.TREE_DEFINE);
				for(Map neInEMS:allNeInEMS){
					list.add(transNeTree(neInEMS));
				}
				break;
			case CommonDefine.VIEW_TYPE_SUBNET://子网节点
				List<Map> allSubnetInSubnet = topoManagerMapper.getDirectSubnetInSubnet(Integer.parseInt(nodeInfo[0]),userId,CommonManagerService.TREE_DEFINE);
				for(Map subnetInSubnet:allSubnetInSubnet){
					list.add(transSubnetTree(subnetInSubnet));
				}
				List<Map> allNeInSubnet = topoManagerMapper.getDirectNeInSubnet(Integer.parseInt(nodeInfo[0]),userId,CommonManagerService.TREE_DEFINE);
				for(Map neInSubnet:allNeInSubnet){
					list.add(transNeTree(neInSubnet));
				}
				break;
			default:
				break;
			}
		}
		result.put("total", list.size());
		result.put("rows",list);
		
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 */
	public Map<String, Object> addNodeTree(int nodeType,int nodeId,String displayName,Integer userId) throws CommonException {
		
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		//判断新加入节点的类型
		switch(nodeType){
		case CommonDefine.VIEW_TYPE_FTSP://加入EMS分组
			//判断该EMS分组名是否已存在
			Map emsGroup = topoManagerMapper.getEMSGroupByDisplayName(displayName);
			if(emsGroup != null){
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("您输入的EMS分组名称已经存在!");
			}else{
				topoManagerMapper.addEMSGroup(displayName);
				Map insertedEMSGroup = topoManagerMapper.getEMSGroupByDisplayName(displayName);
				model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				if(insertedEMSGroup.get("GROUP_NAME") != null){
					model.setDisplayName(insertedEMSGroup.get("GROUP_NAME").toString());
				}
				if(insertedEMSGroup.get("BASE_EMS_GROUP_ID") != null){
					model.setNodeId(insertedEMSGroup.get("BASE_EMS_GROUP_ID").toString());
				}
				model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
			}
			break;
		case CommonDefine.VIEW_TYPE_EMSGROUP:
			break;
		case CommonDefine.VIEW_TYPE_EMS://加入子网
			//判断当前网管下是否有该子网名
			Map subnetInEMS = topoManagerMapper.getSubnetInEMSByName(nodeId,displayName);
			if(subnetInEMS != null){
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("您输入的子网名称已经存在!");
			}else{
				topoManagerMapper.addSubnetInEMS(nodeId,displayName);
				Map insertedSubnetInEMS = topoManagerMapper.getSubnetInEMSByName(nodeId,displayName);
				model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				if(insertedSubnetInEMS.get("BASE_SUBNET_ID") != null){
					model.setNodeId(insertedSubnetInEMS.get("BASE_SUBNET_ID").toString());
				}
				model.setDisplayName(displayName);
				model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
			}
			break;
		case CommonDefine.VIEW_TYPE_SUBNET://加入子网
			//判断当前子网下是否有该子网名
			Map subnetInSubnet = topoManagerMapper.getSubnetInSubnetByName(nodeId,displayName);
			if(subnetInSubnet != null){
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("您输入的子网名称已经存在!");
			}else{
				Map currentSubnet = topoManagerMapper.getSubnetBySubnetId(nodeId,userId,CommonManagerService.TREE_DEFINE);
				int emsId = Integer.valueOf(currentSubnet.get("BASE_EMS_CONNECTION_ID").toString());
				topoManagerMapper.addSubnetInSubnet(nodeId,displayName,emsId);
				Map indertedSubnetInSubnet = topoManagerMapper.getSubnetInSubnetByName(nodeId,displayName);
				model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				if(indertedSubnetInSubnet.get("BASE_SUBNET_ID") != null){
					model.setNodeId(indertedSubnetInSubnet.get("BASE_SUBNET_ID").toString());
				}
				model.setDisplayName(displayName);
				model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
			}
			break;
		case CommonDefine.VIEW_TYPE_NE:
			break;
		default:
			break;
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("result", model);
		
		return result;
	}
	
	
	/**
	 *  拓扑树修改节点的名称
	 *  @@@分权分域到网元@@@
	 */
	public Map<String, Object> modifyNodeNameTree(int nodeType,int nodeId,String displayName,Integer userId) throws CommonException {
		
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		//判断修改的节点类型
		switch(nodeType){
		case CommonDefine.VIEW_TYPE_EMSGROUP:
			//判断新输入的EMS分组名是否已存在
			Map emsGroup = topoManagerMapper.getEMSGroupByDisplayName(displayName);
			if(emsGroup != null){
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("您输入的EMS分组名称已经存在!");
			}else{
				topoManagerMapper.modifyEMSGroupName(nodeId,displayName);
				model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				model.setDisplayName(displayName);
			}
			break;
		case CommonDefine.VIEW_TYPE_SUBNET:
			//获取当前网管记录
			Map currentSubnet = topoManagerMapper.getSubnetBySubnetId(nodeId,userId,CommonManagerService.TREE_DEFINE);
			int parentNodeId = 0;
			int parentNodeType = 0;
			boolean hasSameName = false;
			if(currentSubnet.get("PARENT_SUBNET") != null){
				parentNodeId = Integer.parseInt(currentSubnet.get("PARENT_SUBNET").toString());
				parentNodeType = CommonDefine.VIEW_TYPE_SUBNET;
				Map sameNameInSubnet = topoManagerMapper.getSubnetInSubnetByName(parentNodeId,displayName);
				if(sameNameInSubnet != null){
					hasSameName = true;
				}
			}else{
				parentNodeId = Integer.parseInt(currentSubnet.get("BASE_EMS_CONNECTION_ID").toString());
				parentNodeType = CommonDefine.VIEW_TYPE_EMS;
				Map sameNameInEMS = topoManagerMapper.getSubnetInEMSByName(parentNodeId,displayName);
				if(sameNameInEMS != null){
					hasSameName = true;
				}
			}
			
			if(hasSameName){//有重名记录
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("您输入的子网名称已经存在!");
			}else{
				topoManagerMapper.modifySubnetName(nodeId,displayName);
				model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				model.setDisplayName(displayName);
			}
			break;
		default:
			break;
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("result", model);
		
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@ 
	 * 删除拓扑树节点
	 */
	public Map<String, Object> dltNodeTree(int nodeType,int nodeId,Integer userId) throws CommonException {
		
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		//判断修改的节点类型
		switch(nodeType){
		case CommonDefine.VIEW_TYPE_EMSGROUP:
			//判断该EMS分组下是否有网管
			List<Map> allEMS = topoManagerMapper.getEMSInGroup(nodeId);
			if(allEMS != null && allEMS.size() > 0){
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("该EMS分组下存在网管，不允许删除！");
			}else{
				//先判断该网管分组是否存在
				Map emsGroup = topoManagerMapper.getEMSGroupByEMSGroupId(nodeId,userId,CommonManagerService.TREE_DEFINE);
				if(emsGroup == null){//如果该网管分组已被删除
					model.setReturnResult(String.valueOf(CommonDefine.FAILED));
					model.setReturnMessage("指定的EMS分组名称现已不存在！");
				}else{
					topoManagerMapper.dltEMSGroupById(nodeId);
					model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				}
			}
			break;
		case CommonDefine.VIEW_TYPE_SUBNET:
			//判断该子网下是否有子网
			List<Map> allDirectSubnet = topoManagerMapper.getDirectSubnetInSubnet(nodeId,userId,CommonManagerService.TREE_DEFINE);
			//判断该子网下是否有网元
			List<Map> allDirectNe = topoManagerMapper.getDirectNeInSubnet(nodeId,userId,CommonManagerService.TREE_DEFINE);
			if((allDirectSubnet != null && allDirectSubnet.size() > 0) ||
				(allDirectNe != null && allDirectNe.size() > 0)){
				model.setReturnResult(String.valueOf(CommonDefine.FAILED));
				model.setReturnMessage("该子网下存在子网或网元，不允许删除！");
			}else{
				//先判断该子网是否存在
				Map subnet = topoManagerMapper.getSubnetBySubnetId(nodeId,userId,CommonManagerService.TREE_DEFINE);
				if(subnet == null){
					model.setReturnResult(String.valueOf(CommonDefine.FAILED));
					model.setReturnMessage("指定的子网名称现已不存在！");
				}else{
					topoManagerMapper.dltSubnetById(nodeId);
					model.setReturnResult(String.valueOf(CommonDefine.SUCCESS));
				}
			}
			break;
		default:
			break;
		}
			
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("result", model);
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@ 
	 * 通过网元名模糊查询树节点
	 */
	public Map<String, Object> getTreeNeLike(String displayName,int userId) throws CommonException {
		
		//通过模糊查询查出所有相关的网元记录
		List<Map> allNeLike = topoManagerMapper.getTreeNeLike(displayName,userId,CommonManagerService.TREE_DEFINE);
		
		//获取当前用户可见的所有网管ID
		List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false, false);
		
		List<TopoTreeNodeModel> allNeList = new ArrayList<TopoTreeNodeModel>();
		
		for(Map map:allNeLike){
			for(Map ems:allEMS){
				if(map.get("BASE_EMS_CONNECTION_ID") != null &&
				   Integer.valueOf(map.get("BASE_EMS_CONNECTION_ID").toString()).intValue() == 
				   Integer.valueOf(ems.get("BASE_EMS_CONNECTION_ID").toString()).intValue()){
					allNeList.add(transNeTree(map));
				}
			}
		}
		
		List<Object> rows = new ArrayList<Object>();
		
		//根据该网元记录找到它的所有父节点
		for(TopoTreeNodeModel ne:allNeList){
			TopoTreeNeLikeModel model = new TopoTreeNeLikeModel();
			model.setParentList(new ArrayList<TopoTreeNodeModel>());
			//加入当前网元节点
			model.getParentList().add(ne);
			//当前网元的Id号
			int currentNodeId = Integer.parseInt(ne.getNodeId());
			String nodeType = String.valueOf(CommonDefine.VIEW_TYPE_NE);
			int parentNodeId = Integer.parseInt(ne.getParentNodeId());
			String parentNodeType = ne.getParentNodeType();
			
			while(true){
				boolean flag = false;
//				TopoTreeNodeModel node = new TopoTreeNodeModel();
				switch(Integer.parseInt(parentNodeType)){
				case CommonDefine.VIEW_TYPE_EMSGROUP://网管分组
					//查出该网管分组的记录
					Map emsGroupRecord = topoManagerMapper.getEMSGroupByEMSGroupId(parentNodeId,userId,CommonManagerService.TREE_DEFINE);
					TopoTreeNodeModel emsGroupModel = transEMSGroupTree(emsGroupRecord);
					model.getParentList().add(emsGroupModel);
					flag = true;
					break;
				case CommonDefine.VIEW_TYPE_EMS://网管
					//查出该网管的记录
					Map emsRecord = topoManagerMapper.getEMSByEMSId(parentNodeId,userId,CommonManagerService.TREE_DEFINE);
					TopoTreeNodeModel emsModel = transEMSTree(emsRecord);
					model.getParentList().add(emsModel);
					if(emsModel.getParentNodeId() != null){//该网管有父节点
						parentNodeId = Integer.parseInt(emsModel.getParentNodeId());
						parentNodeType = emsModel.getParentNodeType();
					}else{//该网管没有父节点
						flag = true;
					}
					break;
				case CommonDefine.VIEW_TYPE_SUBNET://子网
					//查出该子网的记录
					Map subnetRecord = topoManagerMapper.getSubnetBySubnetId(parentNodeId,userId,CommonManagerService.TREE_DEFINE);
					TopoTreeNodeModel subnetModel = transSubnetTree(subnetRecord);
					model.getParentList().add(subnetModel);
					parentNodeId = Integer.parseInt(subnetModel.getParentNodeId());
					parentNodeType = subnetModel.getParentNodeType();
					break;
				case CommonDefine.VIEW_TYPE_NE://网元
					break;
				default:
					break;
				}
				
				if(flag) break;//若到了最高的父节点，则进入下一个相关网元的循环
			}
			
			//将model的parentList中的节点倒序
			List<TopoTreeNodeModel> inverse = new ArrayList<TopoTreeNodeModel>();
			int len = model.getParentList().size();
			for(int i=len;i>0;i--){
				inverse.add(model.getParentList().get(i-1));
			}
			model.setParentList(inverse);
			
			rows.add(model);
		}

		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		
//		System.out.println(result.toString());
		
		return result;
	}
	
	
	public Map<String, Object> getTreeNode(int nodeId,int nodeType,int userId) throws CommonException 
	{  
		Map<String,Object> return_map = null;
		switch(nodeType){
		case CommonDefine.VIEW_TYPE_FTSP:     //FTSP
			return_map = getTreeFTSP(userId);
			break;
		case CommonDefine.VIEW_TYPE_EMSGROUP:      //网管分组
			return_map = getTreeEMSInGroup(nodeId,userId);
			break;
		case CommonDefine.VIEW_TYPE_EMS:      //网管
			return_map = getTreeNodeInEMS(nodeId,userId);
			break;
		case CommonDefine.VIEW_TYPE_SUBNET:      //子网
			return_map = getTreeNodeInSubnet(nodeId,userId);
			break;
		default:
			break;
		}
		
		return return_map;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param subnetId
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getTreeNodeInSubnet(int subnetId,Integer userId) {
		
		//获取当前子网的直属子网
		List<Map> allDirectSubnet = topoManagerMapper.getDirectSubnetInSubnet(subnetId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前子网的直属网元
		List<Map> allDirectNe = topoManagerMapper.getDirectNeInSubnet(subnetId,userId,CommonManagerService.TREE_DEFINE);
		
		List<TopoTreeNodeModel> rows = new ArrayList<TopoTreeNodeModel>();
		
		for(Map map:allDirectSubnet){
			rows.add(transSubnetTree(map));
		}
		
		for(Map map:allDirectNe){
			rows.add(transNeTree(map));
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		
		return result;
	} 
	
	/**
	 * @@@分权分域到网元@@@
	 * @param emsId
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getTreeNodeInEMS(int emsId,Integer userId) {
		
		//获取当前网管下所有的直属子网
		List<Map> allDirectSubnet = topoManagerMapper.getDirectSubnetInEMS(emsId,userId,CommonManagerService.TREE_DEFINE);
		//获取当前网管下所有的直属网元
		List<Map> allDirectNe = topoManagerMapper.getDirectNeInEMS(emsId,userId,CommonManagerService.TREE_DEFINE);
		
		List<TopoTreeNodeModel> rows = new ArrayList<TopoTreeNodeModel>();
		
		for(Map map:allDirectSubnet){
			rows.add(transSubnetTree(map));
		}
		
		for(Map map:allDirectNe){
			rows.add(transNeTree(map));
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param emsGroupId
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTreeEMSInGroup(int emsGroupId,int userId) throws CommonException {
		
		//取出该网管分组下所有的网管
//		List<Map> allEMS = topoManagerMapper.getEMSInGroup(emsGroupId);
		//获取当前用户可见的所有网管ID
		List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, emsGroupId, false, false);
		
		List<Object> rows = new ArrayList<Object>();
		
		for(Map map:allEMS){
			rows.add(transEMSTree(map));
		}
//		for(UserDeviceDomainModel emsDeviceDomainModel:allEMSModel){
//			Map ems = topoManagerMapper.getEMSByEMSId(emsDeviceDomainModel.getTargetId());
//			if(ems.get("BASE_EMS_GROUP_ID") != null && 
//					Integer.valueOf(ems.get("BASE_EMS_GROUP_ID").toString()) == emsGroupId){
//				rows.add(transformEMS(ems));
//			}
//		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		
		return result;
	}  
	
	/**
	 * @@@分权分域到网元@@@
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTreeFTSP(int userId) throws CommonException {

		
		//获取所有的网管分组
//		List<Map> allEMSGroup = topoManagerMapper.getAllEMSGroup();
		List<Map> allEMSGroup = commonManagerService.getAllEmsGroups(userId, false, false, false);
		//获取所有FTSP的直属网管
//		List<Map> allEMS = topoManagerMapper.getAllEMSInFTSP();
		//获取当前用户可见的所有网管ID
		List<Map> allEMS = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false, false);
		
		//将所有的网管分组、网管用ArrayList进行保存
		List<TopoTreeNodeModel> emsGroupList = new ArrayList<TopoTreeNodeModel>();
		List<TopoTreeNodeModel> emsList = new ArrayList<TopoTreeNodeModel>();
		
		//将网管分组转换成model形式
		for(Map map:allEMSGroup){
			//过滤掉空网管分组
			for(Map ems:allEMS){
				if(ems.get("BASE_EMS_GROUP_ID") != null && !map.get("BASE_EMS_GROUP_ID").toString().equals("")){
					if(Integer.valueOf(map.get("BASE_EMS_GROUP_ID").toString()).intValue() == 
							Integer.valueOf(ems.get("BASE_EMS_GROUP_ID").toString()).intValue()){
						emsGroupList.add(transEMSGroupTree(map));
						break;
					}
				}
			}
		}
		
		//将网管转换成model形式
		for(Map map:allEMS){
			if(map.get("BASE_EMS_GROUP_ID") != null && !map.get("BASE_EMS_GROUP_ID").toString().equals("")){
				
			}else{
				emsList.add(transEMSTree(map));
			}
		}
//		for(UserDeviceDomainModel emsDeviceDomainModel:allEMSModel){
//			Map ems = topoManagerMapper.getEMSByEMSId(Integer.valueOf(emsDeviceDomainModel.getTargetId()));
//			emsList.add(transEMSTree(ems));
//		}
		
		List<Object> rows = new ArrayList<Object>();
		
		for(TopoTreeNodeModel model : emsGroupList){
			rows.add(model);
		}
		
		for(TopoTreeNodeModel model : emsList){
			rows.add(model);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", rows.size());
		result.put("rows", rows);
		
		return result;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoTreeNodeModel transEMSTree(Map map) {
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		
		if(map.get("BASE_EMS_CONNECTION_ID") != null){
			model.setNodeId(map.get("BASE_EMS_CONNECTION_ID").toString());
		}
		
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
		
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
		
		if(map.get("BASE_EMS_GROUP_ID") != null){
			model.setParentNodeId(map.get("BASE_EMS_GROUP_ID").toString());
			model.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
		}

		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		} 
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoTreeNodeModel transEMSGroupTree(Map map) {
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		
		if(map.get("BASE_EMS_GROUP_ID") != null){
			model.setNodeId(map.get("BASE_EMS_GROUP_ID").toString());
		}
		
		if(map.get("GROUP_NAME") != null){
			model.setDisplayName(map.get("GROUP_NAME").toString());
		}
		
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMSGROUP));
		
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoTreeNodeModel transSubnetTree(Map map) {
		TopoTreeNodeModel model = new TopoTreeNodeModel();
		
		if(map.get("BASE_SUBNET_ID") != null){
			model.setNodeId(map.get("BASE_SUBNET_ID").toString());
		}
		
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
		
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
		
		if(map.get("PARENT_SUBNET") != null){
			model.setParentNodeId(map.get("PARENT_SUBNET").toString());
			model.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
		}else{
			model.setParentNodeId(map.get("BASE_EMS_CONNECTION_ID").toString());
			model.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
		}
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	}
	
	/**
	 * @@@分权分域到网元@@@
	 * @param map
	 * @return
	 */
	public TopoTreeNodeModel transNeTree(Map map) {
		TopoTreeNodeModel model = new TopoTreeNodeModel();
	
		if(map.get("BASE_NE_ID") != null){
			model.setNodeId(map.get("BASE_NE_ID").toString());
		}
		
		if(map.get("DISPLAY_NAME") != null){
			model.setDisplayName(map.get("DISPLAY_NAME").toString());
		}
	
		model.setNodeType(String.valueOf(CommonDefine.VIEW_TYPE_NE));
		
		if(map.get("BASE_SUBNET_ID") != null){
			model.setParentNodeId(map.get("BASE_SUBNET_ID").toString());
			model.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_SUBNET));
		}else{
			if(map.get("BASE_EMS_CONNECTION_ID") != null){
				model.setParentNodeId(map.get("BASE_EMS_CONNECTION_ID").toString());
				model.setParentNodeType(String.valueOf(CommonDefine.VIEW_TYPE_EMS));
			}
		}
		if(map.get("domainAuth") != null){
			model.setDomainAuth(map.get("domainAuth").toString());
		}  
		return model;
	} 
}
