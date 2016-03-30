package com.fujitsu.manager.viewManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fujitsu.IService.IBayfaceManagerService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.BayfaceManagerMapper;
import com.fujitsu.manager.faultManager.serviceImpl.AlarmManagementServiceImpl;
import com.fujitsu.model.CurrentAlarmModel;
  
public class BayfaceManagerServiceImpl  extends AbstractService implements  IBayfaceManagerService{
	@Resource
	private BayfaceManagerMapper bayfaceManagerMapper;
	@Resource
	private AlarmManagementServiceImpl fltManageService;
	/** 
	 * 面板图右键菜单:属性信息  
	 * @param String 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@Override
	public Map<String,Object> getUnitAttribute(String unitId) throws CommonException{
		Map<String,Object> returnMap = new HashMap <String,Object>();
		try{ 
			returnMap = bayfaceManagerMapper.getUnitAttribute(unitId);  
		}catch (Exception e){ 
		}
		return returnMap;
	}
	
	/** 
	 * 面板图：初始化页面 
	 * 
	 * @param Map 查询数据时的参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	@Override
	public Map<String,Object> getBayfaceDataFromNE(String neId, String speShelfNo) throws CommonException{
		//获取网元neMap
		Map<String,Object> neMap = new HashMap <String,Object>();
		try{
			neMap = bayfaceManagerMapper.getBayfaceDataFromNE(neId);  
			//指定的子架号
			neMap.put("shelfNo", speShelfNo);
			
			//子架shlfList
			List<Map<String,Object>> shlfList = new ArrayList<Map<String,Object>>();
			
			shlfList = bayfaceManagerMapper.getBayfaceDataFromShelf(neId);    
			neMap.put("rows", shlfList);
			neMap.put("total", shlfList.size());
			
			//板卡unitMap
			List<Map<String,Object>> unitList = new ArrayList<Map<String,Object>>();
			
			//取告警数据		
			List<CurrentAlarmModel> neAlmData  = new ArrayList<CurrentAlarmModel>();
			//根据网元Id取告警数据 ，与苏州部分的接口数据
			neAlmData = fltManageService.getCurrentAlarmByNeIdForView(Integer.valueOf(neId));  
			
			for(int i = 0;i<shlfList.size();i++){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("BASE_NE_ID", neId);
				map.put("BASE_SHELF_ID", shlfList.get(i).get("BASE_SHELF_ID")); 
				unitList = bayfaceManagerMapper.getBayfaceDataFromUnit(map);   
				int cnt;  

				for(int k = 0;k<unitList.size();k++){ 
					//组装告警等级
					Map<String,Object> currentAlarm = new HashMap <String,Object>();
					currentAlarm.put("CR", 0);
					currentAlarm.put("MJ", 0);
					currentAlarm.put("MN", 0);
					currentAlarm.put("WR", 0);
					for (CurrentAlarmModel objAlm : neAlmData) {
					
						if(objAlm.getRackNo().equals(shlfList.get(i).get("RACK_NO"))
								&& objAlm.getShelfNo().equals(shlfList.get(i).get("SHELF_NO"))
								&& objAlm.getSlotNo().equals(unitList.get(k).get("SLOT_NO"))){  
							if(Integer.valueOf(objAlm.getPerceivedSeverity())== CommonDefine.ALARM_PS_CRITICAL){
								cnt = Integer.valueOf(currentAlarm.get("CR").toString())+1;
								currentAlarm.put("CR",cnt);
							}else if(Integer.valueOf(objAlm.getPerceivedSeverity()) == CommonDefine.ALARM_PS_MAJOR){
								cnt = Integer.valueOf(currentAlarm.get("MJ").toString())+1;
								currentAlarm.put("MJ",cnt);
							}else if(Integer.valueOf(objAlm.getPerceivedSeverity()) == CommonDefine.ALARM_PS_MINOR){
								cnt = Integer.valueOf(currentAlarm.get("MN").toString())+1;
								currentAlarm.put("MN",cnt);
							}else if(Integer.valueOf(objAlm.getPerceivedSeverity()) == CommonDefine.ALARM_PS_WARNING){
								cnt = Integer.valueOf(currentAlarm.get("WR").toString())+1;
								currentAlarm.put("WR",cnt);
							}
						}
					}
					unitList.get(k).put("currentAlarm",currentAlarm);
				}
				
				shlfList.get(i).put("rows", unitList);
				shlfList.get(i).put("total", unitList.size()); 
			} 
		}catch(Exception e){
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INTERNAL_ERROR);
		} 
		return neMap;
	} 
	
	/** 
	 * 面板图：取端口详细信息
	 * 
	 * @param Map 查询数据时的参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	@Override
	public Map<String,Object> getPortDomain (Map <String,String> map) throws CommonException{
		Map<String,Object> returnMap = new HashMap <String,Object>();
		try{
			returnMap = bayfaceManagerMapper.getPortDomain(map);  

		}catch (Exception e){
			
		}
		return returnMap;
	}
	
	/** 
	 * 面板图：取板卡详细信息
	 * 
	 * @param Map 查询数据时的参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	@Override
	public Map<String,Object> getBayfaceUintId (Map <String,String> map) throws CommonException{
		Map<String,Object> returnMap = null;//new HashMap <String,Object>();
		List<Map<String,Object>> tmp = null; 
		Map<String,Object> neMap = null;
		try { 
			returnMap = bayfaceManagerMapper.getBayfaceUintId(map);
			if(!returnMap.isEmpty()) {
				tmp = bayfaceManagerMapper.getDetailPTP(returnMap.get("BASE_UNIT_ID").toString());
				if (tmp.isEmpty() || tmp == null ) {
					// 无端口的单元盘按网元类型设置domain
					neMap = bayfaceManagerMapper.getBayfaceDataFromNE(map.get("neId"));
					Integer neType = (Integer)neMap.get("TYPE");
					switch (neType) {
					case CommonDefine.NE_TYPE_SDH_FLAG:
						returnMap.put("DOMAIN", CommonDefine.NE_TYPE_SDH_FLAG);
						break;
					case CommonDefine.NE_TYPE_WDM_FLAG:
						returnMap.put("DOMAIN", CommonDefine.NE_TYPE_WDM_FLAG);
						break;
					case CommonDefine.NE_TYPE_OTN_FLAG:
						returnMap.put("DOMAIN", CommonDefine.NE_TYPE_WDM_FLAG);
						break;
					case CommonDefine.NE_TYPE_PTN_FLAG:
						returnMap.put("DOMAIN", CommonDefine.NE_TYPE_SDH_FLAG);
						break;
					default:
						returnMap.put("DOMAIN", CommonDefine.NE_TYPE_SDH_FLAG);
					}
				} else {
					// 带有端口的单元盘，按端口类型设置domain
					returnMap.put("DOMAIN", tmp.get(0).get("DOMAIN"));
				}
			}
		} catch (Exception e) {
			
		}
		return returnMap;
	}
	
	
	/** 
	 * 面板图：取网元详细信息
	 * 
	 * @param Map 查询数据时的参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	@Override
	public Map<String,Object> getNeRelate (String neId) throws CommonException{
		Map<String,Object> returnMap = new HashMap <String,Object>();
		String emsId = "";
		String emsGroupId = "";
		try{ 
			emsId = bayfaceManagerMapper.getEmsId(neId);  
			emsId=((emsId==null)?"":emsId);
			if(emsId!=""){
				emsGroupId = bayfaceManagerMapper.getEmsGroupId(emsId);
				emsGroupId=((emsGroupId==null)?"":emsGroupId);
			}
			returnMap.put("BASE_NE_ID", neId);
			returnMap.put("BASE_EMS_CONNECTION_ID", emsId);
			returnMap.put("BASE_EMS_GROUP_ID", emsGroupId);
			
		}catch (Exception e){
			
		}
		return returnMap;
	} 
}
