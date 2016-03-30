package com.fujitsu.manager.networkManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.manager.networkManager.service.TransSystemService;
@Service
@Transactional(rollbackFor = Exception.class)
public class TransSystemServiceImpl extends TransSystemService{
	
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	private IPerformanceManagerService performanceManagerService;
	
	


	public Map<String, Object> queryTransmissionSystem(Map<String, Object> conds, 
					Integer sysUserId, int start, int limit) throws CommonException {
		
		Map<String,Object> data = new HashMap<String,Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
//			Map<String, Object> conds = arrangeCondsForQueryTransmissionSystem(paramMap);
			//获取用户可见的所有网元
			List<Map<String, Object>> allNe = transSystemMapper.getAllVisibleNe(sysUserId, CommonDefine.TREE.TREE_DEFINE);
			List<Integer> allNeId = new ArrayList<Integer>();
			allNeId.add(0);
			for(Map<String, Object> ne : allNe){
				allNeId.add(Integer.parseInt(ne.get("BASE_NE_ID").toString()));
			}
			conds.put("neIds", allNeId);
			
			int total = transSystemMapper.queryTransmissionSystemCount(conds);
			resultList = transSystemMapper.queryTransmissionSystem(conds, start, limit);
			//增加区域全路径
			addAreaFullPathForTransmissionSystem(resultList);
			
			data.put("rows", resultList);
			data.put("total", total);
			
		} catch (Exception e) {
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	/**
	 * 给传输系统记录加上区域全路径
	 * @param list
	 */
	private void addAreaFullPathForTransmissionSystem(List<Map<String, Object>> list) {
		
		int areaId = 0;
		for(Map<String, Object> map : list){
			if(map.get("RESOURCE_AREA_ID") != null 
					&& !"".equals(map.get("RESOURCE_AREA_ID").toString())){
				areaId = Integer.parseInt(map.get("RESOURCE_AREA_ID").toString());
				map.put("AREA_FULL_PATH", commonManagerService.getMulitLevelFullName(areaId, 
					"T_RESOURCE_AREA","RESOURCE_AREA_ID", "AREA_PARENT_ID","AREA_NAME"));
			}
		}
	}
	
	/**
	 * 整理查询传输系统的条件
	 * @param paramMap
	 * @return
	 */
	public static Map<String, Object> arrangeCondsForQueryTransmissionSystem(Map<String, String> paramMap) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.putAll(paramMap);
		
		List<Integer> areaIdList = null;
		if(paramMap.get("areaIds") != null && !"".equals(paramMap.get("areaIds").toString())){
			String areaIds = paramMap.get("areaIds").toString();
			areaIdList = new ArrayList<Integer>();
			String[] array = areaIds.split(",");
			for(String s : array){
				areaIdList.add(Integer.parseInt(s));
			}
		}
		result.put("areaIds", areaIdList);
		
		return result;
	}
	
	public CommonResult deleteTransmissionSystem(Map<String, String> paramMap) throws CommonException {
		
		CommonResult result = new CommonResult();
		if(paramMap.get("transSysId") != null && !"".equals(paramMap.get("transSysId").toString())){
			int transSysId = Integer.parseInt(paramMap.get("transSysId").toString());
			//修改t_base_link表中RESOURCE_TRANS_SYS_ID字段
			transSystemMapper.updateTransSysIdInTBaseLink(transSysId);
			//删除t_resource_trans_sys_ne表中记录
			transSystemMapper.dltTransSysNeByTransSysId(transSysId);
			//删除t_resource_trans_sys表中的记录
			transSystemMapper.dltTransSysByTransSysId(transSysId);
			
			result.setReturnResult(CommonDefine.SUCCESS);
		}else{
			result.setReturnResult(CommonDefine.FAILED);
		}
		
		return result;
	}
	
	//--------------------------333333333333333333333-------------------------
	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getNeInfoWithArea(List<Map> nodeList,Integer sysUserId,List<Integer> idList)
			throws CommonException {
		 Map<String,Object> data=new HashMap<String,Object>();
		 List<Map> resultList =new ArrayList<Map>();
		 try {
			 if(nodeList.size()==0)
				 nodeList = null;
			 if(idList==null||idList.size()==0)
				 idList = null;
			 resultList = transSystemMapper.getNeInfoWithArea(nodeList,sysUserId,CommonDefine.TREE.TREE_DEFINE,idList);
			 data.put("rows", resultList);
			 data.put("total", resultList.size());
			 
		} catch (Exception e) {
			throw new CommonException(e,1);
		}
		 return data;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getLinkBetweenNe(List<Integer> intList,Map<String, String> paramMap)
			throws CommonException {
		Map<String,Object> data=new HashMap<String,Object>();
		 List<Map> resultList =new ArrayList<Map>();
		 try {
			 
			 resultList = transSystemMapper.getLinkBetweenNe(intList,paramMap);
			 data.put("rows", resultList);
			 data.put("total", resultList.size());
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,1);
		}
		 return data;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public CommonResult newTransSystem(Map<String, String> paramMap,List<Integer> intList)
			throws CommonException {
		CommonResult result = new CommonResult();
		try {
			int count = transSystemMapper.checkIfSameSysName(paramMap);
			if(count>0){
				result.setReturnResult(0);
				result.setReturnMessage("系统名称已经存在，不允许重复，请修改！");
				return result;
			}
			count = transSystemMapper.checkIfSameSysCode(paramMap);
			if(count>0){
				result.setReturnResult(0);
				result.setReturnMessage("系统代号已经存在，不允许重复，请修改！");
				return result;
			}
			Map<String,Long> idMap = new HashMap<String,Long>();
			transSystemMapper.newTransSystem(paramMap,idMap);
			paramMap.put("transSysId", idMap.get("newId").toString());
			if(intList.size()>0&&intList.get(0)!=null)
				transSystemMapper.saveTransSystemNe(intList, paramMap);
			if(paramMap.get("linkList").length()>0){
				List<Map> otherLinkList = transSystemMapper.getTheOtherLink(paramMap);
				if(otherLinkList.size()>0){
					StringBuffer sb = new StringBuffer(paramMap.get("linkList"));
					for(Map m:otherLinkList){
						sb.append(",");
						sb.append(m.get("linkId"));
					}
					paramMap.put("linkList", sb.toString());
				}
				transSystemMapper.saveTransSystemLink(paramMap);
			}
			result.setReturnResult(1);
			result.setReturnMessage("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,1);
		}
		return result;
	}

	@Override
	public CommonResult saveTransSystemNe(List<Integer> intList,
			Map<String, String> paramMap) throws CommonException {
		String operType = paramMap.get("operType");
		CommonResult result = new CommonResult();
		try {
			if (operType.equals("edit")) {
				transSystemMapper.dltTransSysNeByTransSysId(Integer.valueOf(paramMap.get("transSysId")));
				transSystemMapper.updateNodeCount(paramMap);
			}
			if(intList.size()>0&&intList.get(0)!=null){
			transSystemMapper.saveTransSystemNe(intList, paramMap);
			}
			result.setReturnResult(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return result;
	}

	@Override
	public CommonResult saveTransSystemLink(Map<String, String> paramMap) throws CommonException {
		String operType = paramMap.get("operType");
		CommonResult result = new CommonResult();
		try {
			if (operType.equals("edit")) {
				transSystemMapper.updateTransSysIdInTBaseLink(Integer.valueOf(paramMap.get("transSysId")));
				transSystemMapper.updateNodeCount(paramMap);
			}
			if(paramMap.get("linkList").length()>0){
				List<Map> otherLinkList = transSystemMapper.getTheOtherLink(paramMap);
				if(otherLinkList.size()>0){
					StringBuffer sb = new StringBuffer(paramMap.get("linkList"));
					for(Map m:otherLinkList){
						sb.append(",");
						sb.append(m.get("linkId"));
					}
					paramMap.put("linkList", sb.toString());
				}
				transSystemMapper.saveTransSystemLink(paramMap);
			}
			result.setReturnResult(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return result;
	}

	@Override
	public Map<String, Object> getTransSystem(Map<String, String> paramMap)
			throws CommonException {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		
		try {
			List<Map> transSys = transSystemMapper.getTransSystem(paramMap);
			List<Long> neList = transSystemMapper.getTransSystemNe(paramMap);
			returnResult.put("transSys",transSys);
			returnResult.put("neList",neList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return returnResult;
	}

	@Override
	public Map<String, Object> getTransSysLink(Map<String, String> paramMap)
			throws CommonException {
		
		Map<String, Object> returnResult = new HashMap<String, Object>();
		try {
			List<Map> rows = transSystemMapper.getTransSysLink(paramMap);
			returnResult.put("rows",rows);
			returnResult.put("total",rows.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 1);
		}
		return returnResult;
	}

	@Override
	public CommonResult updateTransSystem(Map<String, String> paramMap)
			throws CommonException {
		CommonResult result = new CommonResult();
		try {
			paramMap.put("exceptSelf", "1");
			int count = transSystemMapper.checkIfSameSysName(paramMap);
			if(count>0){
				result.setReturnResult(0);
				result.setReturnMessage("系统名称已经存在，不允许重复，请修改！");
				return result;
			}
			count = transSystemMapper.checkIfSameSysCode(paramMap);
			if(count>0){
				result.setReturnResult(0);
				result.setReturnMessage("系统代号已经存在，不允许重复，请修改！");
				return result;
			}
			transSystemMapper.updateTransSystem(paramMap);
			result.setReturnResult(1);
			result.setReturnMessage("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,1);
		}
		return result;
	}

	@Override
	public CommonResult checkIfNeDeletable(List<Integer> intList,
			Map<String, String> paramMap) throws CommonException {
		CommonResult result = new CommonResult();
		try {
			int count = transSystemMapper.checkIfNeDeletable(intList,paramMap);
			if(count>0){
				result.setReturnResult(0);
//				result.setReturnMessage("系统名称已经存在，不允许重复，请修改！");
				return result;
			}
			
			result.setReturnResult(1);
//			result.setReturnMessage("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,1);
		}
		return result;
	}

	@Override
	public CommonResult autoFindSystem(List<Map> nodeList)
			throws CommonException {
		CommonResult result = new CommonResult();
		try {
			List<Integer> neList = performanceManagerService.getNeIdsFromNodes(nodeList);
			List<Map> proGroupList = transSystemMapper.getProListByNe(neList);
			// 以保护组为单位，一个保护组可以找出一个环
			for(Map proGrp : proGroupList){
				List<String> sysNeList = new ArrayList<String>(); 
				List<String> sysLinkList = new ArrayList<String>(); 
				String ptpId = proGrp.get("ptp").toString().split(",")[0];
				sysNeList.add(proGrp.get("neId").toString());
				boolean iFind = goForNextNe(ptpId,proGrp, sysNeList, sysLinkList, 0);
				if(iFind){
					String sysNameprefix = PROTECTION_GROUP_TYPE[Integer.valueOf(proGrp.get("proType").toString())];
					String lastName = transSystemMapper.getSystemLastName(sysNameprefix+"-%");
					String sysName;
					if(lastName==null){
						sysName = sysNameprefix+"-1";
					}else{
						String[] ln = lastName.split("-");
						Integer index = Integer.valueOf(ln[1]);
						index++;
						sysName = sysNameprefix+"-"+index.toString();
					}
					Map<String,String> sysInfo = new HashMap<String, String>();
					Map<String,Long> idMap = new HashMap<String,Long>();
					sysInfo.put("sysName", sysName);
					sysInfo.put("nodeCount", String.valueOf(sysNeList.size()));
					sysInfo.put("structure", "1");
					sysInfo.put("proType", proGrp.get("proType").toString());
					sysInfo.put("genMethod","1");
					/* 这段的作用是为了防止同时操作导致的重名，目前先不用，只是一个想法
					int count = transSystemMapper.checkIfSameSysName(sysInfo);
					if(count>0){
						String[] ln = lastName.split("-");
						Integer index = Integer.valueOf(ln[1]);
						index++;
						sysName = sysNameprefix+"-"+index.toString();
					}
					*/
					transSystemMapper.newTransSystem(sysInfo,idMap);
					sysInfo.put("transSysId", idMap.get("newId").toString());
					if(sysNeList.size()>0)
						transSystemMapper.saveTransSystemNe(sysNeList, sysInfo);
					if(sysLinkList.size()>0){
						sysInfo.put("linkList", sysLinkList.toString().substring(1, sysLinkList.toString().length()-1));
						transSystemMapper.saveTransSystemLink(sysInfo);
					}
				}
			}
			result.setReturnResult(1);
			result.setReturnMessage("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,1);
		}
		return result;
	}
	private boolean goForNextNe(String ptpId,Map oriProGrp,List<String> sysNeList,List<String> sysLinkList,int count){
		Map zEnd = transSystemMapper.getZEnd(ptpId);
		if(zEnd==null)
			return false;
		else{
			sysLinkList.add(zEnd.get("BASE_LINK_ID").toString());
			if(zEnd.get("Z_NE_ID").toString().equals(oriProGrp.get("neId").toString())){
				return true;
			}else{
				// 如果需要人工判断死循环，此处可以加判断，看看网元list里除了起始网元有无该网元存在
				sysNeList.add(zEnd.get("Z_NE_ID").toString());
				String nextAPtp = transSystemMapper.getProListByPtp(zEnd.get("Z_END_PTP").toString());
				//递归计数
				count++;
				if(count>30)
					return false;
				
				return goForNextNe(nextAPtp,oriProGrp,sysNeList,sysLinkList,count);
			}
		}
	}

	private String transSystemMapper(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	private Map<String,String> generateAutoSysInfoMap(){
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
