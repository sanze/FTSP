package com.fujitsu.manager.viewManager.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.EvaluateManagerMapper;
import com.fujitsu.dao.mysql.LinkEvalTopoMapper;
import com.fujitsu.manager.viewManager.service.LinkEvalTopoManagerService;
import com.fujitsu.model.LinkEvaluateLineModel;
import com.fujitsu.model.LinkEvaluateNodeModel;

public class LinkEvalTopoManagerServiceImpl extends LinkEvalTopoManagerService {

	@Resource
	private LinkEvalTopoMapper linkEvalTopoMapper;
	@Resource
	private AlarmManagementMapper alarmManagementMapper;
	@Resource
	private EvaluateManagerMapper evaluateManagerMapper;
	@Resource
	private CommonManagerMapper commonManagerMapper;
	
	@Override
	public Map<String, Object> getSystemList(int netLevel, int userId) throws CommonException {
		// 当前用户的网元设备域列表
		List<Integer> neIdListByCurUser = new ArrayList<Integer>();
		Map<String,Object> transSysMap = new HashMap <String,Object>();
		List<Map<String, Object>> sysMapList = new ArrayList<Map<String, Object>>();
		List<Integer> sysIdListByCurUser = new ArrayList<Integer>();
		try {
			List<Map<String, Object>> mapList = linkEvalTopoMapper.getTransmissionSystemByNetlevel(netLevel);
			
			if (mapList != null && mapList.size() > 0) {
				int[] sysIdList = new int[mapList.size()];
				for (int i=0; i<sysIdList.length; i++) {
					sysIdList[i] =  (Integer) mapList.get(i).get("RESOURCE_TRANS_SYS_ID");
				}			
				// 获取系统内网元信息
				List<Map<String, Object>> neListInSys = linkEvalTopoMapper.getTansSysNeById(sysIdList);			
				
				// 如果非管理员用户则获取当前用户可管理的网元信息及创建当前用户的传输系统ID列表
				if (userId != CommonDefine.USER_ADMIN_ID) {
					//获取当前用户的可管理网元信息
					List<Map<String, Object>> neList = alarmManagementMapper
							.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
					if (neList != null && neList.size() > 0) {
						for (int i = 0; i < neList.size(); i++) {
							neIdListByCurUser.add(Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString()));
						}
					}
					// 构建当前用户的传输系统ID列表
					for (Map<String, Object> sysNe : neListInSys) {
						int neId = (Integer) sysNe.get("NE_ID");
						if (neIdListByCurUser.contains(neId)) {
							int sysId = (Integer) sysNe.get("RESOURCE_TRANS_SYS_ID");
							if (!sysIdListByCurUser.contains(sysId)) {
								sysIdListByCurUser.add(sysId);
							}
						}
					}				
				}
			}
			
			for (Map<String, Object> item : mapList) {
				int sysId = (Integer) item.get("RESOURCE_TRANS_SYS_ID");
				// 排除不在当前用户传输系统列表的系统
				if (userId != CommonDefine.USER_ADMIN_ID && !sysIdListByCurUser.contains(sysId)) {
					continue;
				}
				String sysName = item.get("SYS_NAME") != null ? item.get("SYS_NAME").toString() : "";
				String sysCode = item.get("SYS_CODE") != null ? item.get("SYS_CODE").toString() : "";
				// 将系统名称和系统代号连接成一个字符串
				String sysStr = "".equals(sysCode) ? sysName : sysName +"(" + sysCode + ")";
				// 创建SysMap，包含SYS_ID和SYS_NAME两个元素
				Map<String, Object> sys = new HashMap<String, Object>();
				sys.put("SYS_ID", item.get("RESOURCE_TRANS_SYS_ID"));
				sys.put("SYS_NAME", sysStr);
				// 加入SysMap列表
				sysMapList.add(sys);
			}
			// 封装返回数据Map
			transSysMap.put("total", sysMapList.size());
			transSysMap.put("rows", sysMapList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		} 
		return transSysMap;
	}

	@Override
	public Map<String, Object> getLinkEvalTopoData(int sysId, String evalTime, int userId) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		// 返回数据行集合
		List<Object> rows = new ArrayList<Object>();
		// 当前用户的网元设备域列表
		List<Integer> neIdListByCurUser = new ArrayList<Integer>();
		String privilege = "all";
		
		try {
			// 如果非管理员用户则获取当前用户可管理的网元信息
			if (userId != CommonDefine.USER_ADMIN_ID) {
				List<Map<String, Object>> neList = alarmManagementMapper
						.getNeIdListByUserId(userId, CommonDefine.TREE.TREE_DEFINE);
				if (neList != null && neList.size() > 0) {
					for (int i = 0; i < neList.size(); i++) {
						neIdListByCurUser.add(Integer.parseInt(neList.get(i).get("BASE_NE_ID").toString()));
					}
				}
				privilege = "none";
			}
			
			// 获取系统名称
			Map sysMap = commonManagerMapper.selectTableById("t_resource_trans_sys", "RESOURCE_TRANS_SYS_ID", sysId);
			String sysName = sysMap.get("SYS_NAME") != null ? sysMap.get("SYS_NAME").toString() : "";
			String sysCode = sysMap.get("SYS_CODE") != null ? sysMap.get("SYS_CODE").toString() : "";
			// 将系统名称和系统代号连接成一个字符串
			String sysStr = "".equals(sysCode) ? sysName : sysName +"(" + sysCode + ")";
			
			// 获取传输系统内网元节点
			List<Map<String, Object>> neMapList = linkEvalTopoMapper.getTransSysNeBySysId(sysId);
			
			for (Map<String, Object> neMap : neMapList) {
				// 去除不在当前用户管理域的网元
				if (userId != CommonDefine.USER_ADMIN_ID &&
						neIdListByCurUser.size() > 0 &&
						!neIdListByCurUser.contains(neMap.get("NE_ID"))) {
					continue;
				}
				LinkEvaluateNodeModel neModel = new LinkEvaluateNodeModel();
				neModel.setNodeOrLine("node");
				neModel.setNeId(neMap.get("NE_ID").toString());
				neModel.setNeName(neMap.get("NE_NAME").toString());
				neModel.setNeType(neMap.get("NE_TYPE").toString());
				neModel.setStationName(neMap.get("STATION_NAME")!=null ? neMap.get("STATION_NAME").toString() : "");
				neModel.setPosition_X(neMap.get("POSITION_X")!=null ? neMap.get("POSITION_X").toString() : "");
				neModel.setPosition_Y(neMap.get("POSITION_Y")!=null ? neMap.get("POSITION_Y").toString() : "");
				rows.add(neModel);
			}
			
			// 组织链路评估查询的参数
			Map<String, Object> param =  new HashMap<String, Object>();
			param.put("RESOURCE_TRANS_SYS_ID", sysId);
			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
					CommonDefine.COMMON_SIMPLE_FORMAT);
			Date date = null;

			if ("".equals(evalTime)) {
				date = new Date();
			} else {
				date = df.parse(evalTime);
			}
			param.put("START_DATE", date);

			Integer total = 0;
			param.put("total", total);
			// 获取链路评估数据
			List<Map<String,Object>> linkEvals = evaluateManagerMapper.callFiberLinkPmSP(param, 0, 0);
	//		total = (param.get("total")+"").matches("\\d+")?Integer.valueOf((param.get("total"))+""):0;
			total = linkEvals.size();
			// 获取传输系统内的Link线信息
			List<Map<String, Object>> linkMapList = linkEvalTopoMapper.getTransSysLinkBySysId(sysId);
			for (Map<String, Object> linkMap : linkMapList) {
				// 去除不在当前用户管理域的链路
				if (userId != CommonDefine.USER_ADMIN_ID &&
						neIdListByCurUser.size() > 0 &&
						!(neIdListByCurUser.contains(linkMap.get("FROM_NODE")) &&
						  neIdListByCurUser.contains(linkMap.get("TO_NODE")))) {
					continue;
				}
				LinkEvaluateLineModel lineModel = new LinkEvaluateLineModel();
				lineModel.setNodeOrLine("line");
				lineModel.setLinkId(linkMap.get("LINK_ID").toString());
				lineModel.setFromNode(linkMap.get("FROM_NODE").toString());
				lineModel.setFromNodeType(linkMap.get("FROM_NODE_TYPE").toString());
				lineModel.setToNode(linkMap.get("TO_NODE").toString());
				lineModel.setToNodeType(linkMap.get("TO_NODE_TYPE").toString());
				lineModel.setLineType(linkMap.get("LINE_TYPE").toString());
				// 设置链路评估数据
				if (total > 0) {
					for (Map<String, Object> map : linkEvals) {
						String linkId = map.get("RESOURCE_LINK_ID").toString();
						if (linkId.equals(lineModel.getLinkId())) {
							lineModel.setMainQuality(map.get("OFFSET_LEVEL") != null ? 
									map.get("OFFSET_LEVEL").toString():""); 
							lineModel.setOscQuality(map.get("OFFSET_LEVEL_OSC") != null ? 
									map.get("OFFSET_LEVEL_OSC").toString():"");
							break;
						}
					}
				}
				rows.add(lineModel);
			}
	
			// 组织返回结果
			result.put("total", rows.size());
			result.put("rows", rows);
			result.put("title", sysStr);
			result.put("privilege", privilege);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		} 
		return result;
	}

	public void savePosition(List<String> positionArray) throws CommonException {
		try {
			for(String node:positionArray){
				String[] nodeInfo = node.split(",");
				linkEvalTopoMapper.saveNePosition(Integer.parseInt(nodeInfo[0]),
								Integer.parseInt(nodeInfo[1]),Integer.parseInt(nodeInfo[2]));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		} 
	}
}
