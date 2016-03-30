package com.fujitsu.manager.resourceManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonMethod;
import com.fujitsu.dao.mysql.ResourceMapper;
import com.fujitsu.manager.alarmManager.service.AlarmManagementService;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.EquipmentTestManagerServiceImpl;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.resourceManager.service.ResourceManagerService;

@Service 
public class ResourceManagerServiceImpl extends ResourceManagerService {

	@Resource
	private ResourceMapper resourceMapper;  
	@Resource 
	public EquipmentTestManagerServiceImpl equipmentTestManagerServiceImpl;
	@Resource
	private IAreaManagerService areaManagerImpl;
	@Resource
	private AlarmManagementService alarmManagementService;
	@Override 
	public Map<String, Object> queryRC(Map<String, Object> map,int nodeId,int level,int start,int limit)
	throws CommonException{ 
		//区域条件暂时还未修改
		Map<String, Object> rtnMap = new HashMap<String,Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int total=0; 
		String ids ="("+nodeId;
		try { 
			List<String>  areaIds = new ArrayList();
			if(level>0 && level<11){
				areaIds=areaManagerImpl.getSubAreaIds(nodeId);  
				for (String areaId : areaIds) {
					ids+=","+areaId;
				} 
			} 
			ids+=")"; 
			total = resourceMapper.queryRCCount(map,ids,level);  
		    result = resourceMapper.queryRC(map,ids,level,start, limit); 
			rtnMap.put("total", total);
			rtnMap.put("rows", result);
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取设备列表失败！");
		}
		return rtnMap;
	} 
	@Override 
	public void addRC(Map<String,Object> map) throws CommonException{  
		try{ 
			resourceMapper.addRC(map);
			resourceMapper.addTestPlan(map);
		}catch (Exception e) {
			throw new CommonException(e, -1, "新增设备失败！");
		}	 
	}

	@Override
	public boolean RCExists(Map<String, Object> map) throws CommonException{
		boolean flag = true;
		try {
			List<Map<String, Object>> lst = resourceMapper.RCExists(map);
			if(lst == null || lst.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询设备是否存在失败！");
		}
		return flag;
	}
	
	@Override
	public Map<String,Object> getRCInfo(int rcId) throws CommonException{ 
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			data = resourceMapper.getRCInfo(rcId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取设备属性失败！");
		}	
		return data;
	}
	
	@Override
	public boolean modRCCheck(Map<String,Object> map) throws CommonException{
		List<Map<String, Object>> lst = resourceMapper.modRCCheck(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}
	
	@Override
	public void modRC(Map<String,Object> map) throws CommonException { ;
		try{
			resourceMapper.modRC(map); 
		}catch (Exception e) {
			throw new CommonException(e, -1, "修改设备失败！");
		}	
	} 

	@Override
	public Map<String,Object> queryRCCard(int rcId) throws CommonException{ 
		Map<String, Object> data = new HashMap<String,Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try{
			data = resourceMapper.getRcById(rcId);  
			list = resourceMapper.getUnitById(rcId);
			//此处添加告警数据
			List<Map<String, Object>> almData = new ArrayList<Map<String,Object>>();
			almData=alarmManagementService.getEquipAlarm(rcId); 
			int cnt;  
			for(int k = 0;k<list.size();k++){ 
				//组装告警等级
				Map<String,Object> currentAlarm = new HashMap <String,Object>();
				currentAlarm.put("CR", 0);
				currentAlarm.put("MJ", 0);
				currentAlarm.put("MN", 0);
				currentAlarm.put("WR", 0);
				for (Map<String, Object> objAlm : almData) { 
					if(objAlm.get("SLOT_NO").toString().equals(list.get(k).get("SLOT_NO").toString())){   
						if(Integer.valueOf(objAlm.get("SEVERITY").toString())== CommonDefine.ALARM_LEVEL_CR){
							cnt = Integer.valueOf(currentAlarm.get("CR").toString())+1;
							currentAlarm.put("CR",cnt);
						}else if(Integer.valueOf(objAlm.get("SEVERITY").toString()) == CommonDefine.ALARM_LEVEL_MJ){
							cnt = Integer.valueOf(currentAlarm.get("MJ").toString())+1;
							currentAlarm.put("MJ",cnt);
						}else if(Integer.valueOf(objAlm.get("SEVERITY").toString()) == CommonDefine.ALARM_LEVEL_MN){
							cnt = Integer.valueOf(currentAlarm.get("MN").toString())+1;
							currentAlarm.put("MN",cnt);
						}else if(Integer.valueOf(objAlm.get("SEVERITY").toString()) == CommonDefine.ALARM_LEVEL_WR){
							cnt = Integer.valueOf(currentAlarm.get("WR").toString())+1;
							currentAlarm.put("WR",cnt);
						}
					}
				}
				list.get(k).put("currentAlarm",currentAlarm);
			}
			data.put("rows",list);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取测试设备面板失败！");
		}	
		return data;
	}
	
	@Override
	public Map<String,Object> getTestEquipAttr(int unitId) throws CommonException{ 
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			data = resourceMapper.getTestEquipAttr(unitId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取设备板卡属性失败！");
		}	
		return data;
	} 
	
	@Override
	public boolean syncRtuAllCardInfo(Map<String, Object> map)throws CommonException{ 
		try {
			Map<String,Object> rcCardMap =  new HashMap<String, Object>();
			//获取DB中的此RTU的板卡信息
			List<Map<String,Object>> rcCardList = resourceMapper.getCardById(Integer.valueOf(map.get("rcId").toString())); 
			//组装查询配置的条件
			EqptInfoModel eqpt = new EqptInfoModel();
			eqpt.setRtuIp(map.get("ip").toString());
			eqpt.setRtuPort(Integer.valueOf(map.get("port").toString()));
			eqpt.setRcode(map.get("number").toString()); 
			eqpt.setFactory(map.get("comboFactory").toString());
			SysInfoModel sys = new SysInfoModel();    
			sys.setNip(CommonMethod.ipToDecString(resourceMapper.selectSysParam("SYS_IP")));
			sys.setNcode(resourceMapper.selectSysParam("SYS_CODE"));
			List<RTUConfiguration> config = new ArrayList<RTUConfiguration>();
			config = equipmentTestManagerServiceImpl.loadRTUConfiguration(eqpt,sys,CommonDefine.COLLECT_LEVEL_1);  
			for(int i = 0;i<config.size();i++){
				RTUConfiguration data = config.get(i);
				if("13".equals(data.getSlot())){ 
					 config.set(i, config.get(0));  
					 config.set(0, data);
					 break;
				}
			} 
			
			//查询存在t_ftts_test_route的槽道
			List<String> routes = resourceMapper.getRouteById(Integer.valueOf(map.get("rcId").toString())); 
		    Set<String> set = new LinkedHashSet<String>();       
			for(int p=0; p <routes.size(); p++){   
				String [] tmp = routes.get(p).split(",");
				for(int k=0;k<tmp.length;k++){
					 set.add(tmp[k].split("-")[1]);   
				} 
			}
			routes.clear();
			routes.addAll(set); 
			
			//删除DB中多余板卡
			for(int m=0; m <rcCardList.size(); m++){ 
				boolean isDel=true;
				String slot = rcCardList.get(m).get("slotNo").toString();
				for(int i=0; i <config.size(); i++){ 
					if(slot.equals(config.get(i).getSlot().trim())){
						isDel=false;
						break;
					}
				} 
				if(isDel){
					if(!routes.contains(slot)){
						resourceMapper.deleteUnit(Integer.valueOf(map.get("rcId").toString()),Integer.valueOf(slot));
					}else{ 
						//status为8 
						resourceMapper.updateUnitStatus(Integer.valueOf(map.get("rcId").toString()),Integer.valueOf(slot),8);
					} 
				} 
			}  
			
			if(config.size()>0){
				for(int i=0; i <config.size(); i++){ 
					//-------解析RTU设备当前槽道信息 Start------- 
					String slot =config.get(i).getSlot().trim();
					rcCardMap.put("slot", slot);
					rcCardMap.put("hardWareVersion", config.get(i).getHardwareVersion().trim());
					rcCardMap.put("softWareVersion", config.get(i).getSoftwareVersion().trim());   
					rcCardMap.put("tu",config.get(i).getTu().trim());
					rcCardMap.put("status", config.get(i).getStatus().trim());
					String serialNo = config.get(i).getSn().trim();
					String key = CommonMethod.getEqptSerialNo(serialNo).trim(); 
					//解析序列号信息
					String cardType = CommonMethod.getEquipType(key); 
					String portCount = CommonMethod.getEquipPortCount(key); 
					if(cardType.equals("OTDR")){  
						rcCardMap.put("otdrType",  CommonMethod.getEquipFactory(key));
						rcCardMap.put("waveLen",  CommonMethod.getEquipWaveLen(key)); 
					} else {
						rcCardMap.remove("otdrType");
						rcCardMap.remove("waveLen");
					}
					if(slot.equals("13")){
						cardType = "SHELF"; 
						portCount = "0";
					}  
					rcCardMap.put("rcId", Integer.parseInt(map.get("rcId").toString()));
					rcCardMap.put("serialNo", serialNo); 
					rcCardMap.put("cardType", cardType);
					rcCardMap.put("portCount", portCount); 
					if(rcCardMap.get("shelfId")==null){
						rcCardMap.put("shelfId", map.get("shelfId")==null?0:Integer.parseInt(map.get("shelfId").toString())); 
					}
					
					//-------解析RTU设备当前槽道信息 End------- 
					syncRtuInfoToDB(rcCardList,rcCardMap,routes);
				} 
			} 
			
		} catch (Exception e) {
			throw new CommonException(e, -1, "同步测试设备数据失败！");
		} 
		return true;
	}
	// 匹配同步DB中数据
	public void syncRtuInfoToDB(List<Map<String,Object>> list,Map<String,Object> map,List<String> routes)
			throws CommonException{  
		try{
			boolean isExist = false; 
			// 匹配同步DB中数据
			for(int i=0; i <list.size(); i++){
				Map<String, Object> indexMap = list.get(i); 
				if(indexMap.get("slotNo").toString().equals(map.get("slot").toString())){
					isExist = true;	 
					//对比名称和槽道号，端口数均相同的情况下才可更新
					if(!routes.contains(map.get("slot").toString())){ 
						// ★★★同步板卡状态
						map.put("cardId", indexMap.get("cardId").toString());
						if("13".equals(map.get("slot").toString())){
							resourceMapper.updateShelf(map);
						}else{
							//更新：先删除再增加
							/*2015/5/7注：此处不能按先删除再增加的方法进行处理，因为osw单元的端口是动态生成的，这样会造成该单元的端口ID前后不一致
							   外部连接表中是以端口ID组织数据的，会造成外部连接的显示出现问题。
							resourceMapper.deleteUnit(Integer.valueOf(map.get("rcId").toString()),
									Integer.valueOf(map.get("slot").toString()));
							addRCCard(map);
							 非OSW的的单元可以按老流程处理，OSW单元特殊处理*/
							if (!"OSW".equals(map.get("cardType"))) {
								// 非OSW单元，老流程处理（先删后增，板卡ID保持一致）
								resourceMapper.deleteUnit(Integer.valueOf(map.get("rcId").toString()),
										Integer.valueOf(map.get("slot").toString()));
								addRCCard(map);								
							} else {
								// OSW单元盘，如端口数一致则进行更新，否则不进行更新
								if (indexMap.get("portCount").equals(map.get("portCount"))) {
									// 更新板卡信息
									resourceMapper.updateUnit(map);
								} else {
									// 标记板卡状态为8：数据不同步状态
									resourceMapper.updateUnitStatus(Integer.valueOf(map.get("rcId").toString()),
											Integer.valueOf(map.get("slot").toString()),8);
								}
							}
						} 
					}else{ 
						resourceMapper.updateUnitStatus(Integer.valueOf(map.get("rcId").toString()),
								Integer.valueOf(map.get("slot").toString()),8);
					}  
				}
			}  
			if(!isExist ){
				// ★★★新增板卡
				addRCCard(map);
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "匹配同步DB中数据失败！");
		} 
	} 
	
	public void  addRCCard(Map<String, Object> map)throws CommonException{ 
		try {    
			if(map.get("cardType").toString().equals("SHELF")){   
				resourceMapper.addShelf(map); 
				List<Map<String,Object>> slotList =new ArrayList<Map<String,Object>>(); 
				for(int i=1; i<= 12; i++){ 
					Map<String,Object> slotInfo = new HashMap<String,Object>();
					slotInfo.put("RC_ID",Integer.parseInt(map.get("rcId").toString()));
					slotInfo.put("SHELF_ID",Integer.parseInt(map.get("shelfId").toString()));
					slotInfo.put("SHELF_NO",map.get("tu").toString());  
					slotInfo.put("SLOT_NO",i);  
					slotList.add(slotInfo);
				} 
				resourceMapper.addSlot(slotList);
			}else{ 
				int slotId = resourceMapper.getSlotId(map);
				map.put("slotId",slotId); 
				resourceMapper.addUnit(map);
			} 
		    if(map.get("cardType").toString().equals("OSW")){ 
				List<Map<String,Object>> portList =new ArrayList<Map<String,Object>>();
				int portCount = Integer.parseInt(map.get("portCount").toString()); 
				for(int i=1; i<= portCount; i++){ 
					Map<String,Object> temp = new HashMap<String,Object>();
			    	temp.put("RC_ID",Integer.parseInt(map.get("rcId").toString()));
			    	temp.put("SHELF_ID",Integer.parseInt(map.get("shelfId").toString()));
			    	temp.put("SLOT_ID",Integer.parseInt(map.get("slotId").toString()));    	
			    	temp.put("UNIT_ID",Integer.parseInt(map.get("unitId").toString()));  
					temp.put("PORT_NO",i);  
					portList.add(temp);
				}
				if(portCount>0){
					//生成OSW端口初始数据信息 
					resourceMapper.addOSWPort(portList);  
					//OSW的板卡名称须带端口名
					resourceMapper.updateOSWUnit(Integer.parseInt(map.get("unitId").toString()),"OSW-"+portCount); 
				} 
			} 
		} catch (Exception e) {
			throw new CommonException(e, -1, "新增CARD数据失败！");
		} 
	} 
	
	@Override
	public Map<String, Object> getEqptAndServerTime(Map<String, Object> map){ 
		//暂时无接口对应
		return map;
	}
	
	@Override
	public boolean isPortUsed(int cellId)throws CommonException{
		boolean flag = true;
		try {
			List<Map<String, Object>> lst = resourceMapper.isPortUsed(cellId);
			if(lst == null || lst.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询设备端口是否被使用失败！");
		}
		return flag;
	}
	
	@Override
	public void deleteRC(int cellId)throws CommonException{
		try {

			 resourceMapper.deleteTestPlan(cellId); 
			 resourceMapper.deleteRC(cellId); 
		} catch (Exception e) {
			throw new CommonException(e, -1, "删除失败！");
		}
	}
	
	@Override
	public boolean testRouteExist(int cellId)throws CommonException{
		boolean flag = true;
		try {
			List<Map<String, Object>> lst = resourceMapper.testRouteExist(cellId);
			if(lst == null || lst.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询设备端口是否存在测试路由失败！");
		}
		return flag;
	}
	
}
	 
