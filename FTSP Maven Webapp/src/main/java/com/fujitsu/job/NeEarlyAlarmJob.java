package com.fujitsu.job;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.NetworkManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.networkManager.serviceImpl.NetworkManagerImpl;
import com.fujitsu.util.SpringContextUtil;

public class NeEarlyAlarmJob implements Job { 
	private NetworkManagerMapper networkManagerMapper; 
	private NetworkManagerImpl networkManagerImpl; 
	
	public NeEarlyAlarmJob() {
		networkManagerMapper =(NetworkManagerMapper) SpringContextUtil.getBean("networkManagerMapper");
		networkManagerImpl =(NetworkManagerImpl) SpringContextUtil.getBean("networkManagerImpl");
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try { 
			int start=0,limit=200;
			List<Map> neList =new ArrayList<Map>();
			for(int a=0;;a++){
				start=a*limit+1;
				neList = networkManagerMapper.getAllNeList(start,limit);
				if(neList==null || neList.isEmpty()){
					break;
				}
		        List <Map> ptptType=new ArrayList<Map>(); 
		        for(Map ne:neList){  
		        	if(isTriggeredQz(ne)){
		        		continue;
		        	} 
		            Map<String,Object> data=new HashMap<String,Object>(); 
		            List <Map> rlData=new ArrayList<Map>();
		            data.put("BASE_NE_ID",ne.get("BASE_NE_ID"));  
		            data.put("DISPLAY_NAME",ne.get("DISPLAY_NAME"));   
		            data.put("SLOT_OCCUPANCY",0);  
		            data.put("PTP_OCCUPANCY_2M",'-');
		            data.put("PTP_OCCUPANCY_STM1",'-');
		            data.put("PTP_OCCUPANCY_STM4",'-');
		            data.put("PTP_OCCUPANCY_STM16",'-');
		            data.put("PTP_OCCUPANCY_STM64",'-');
		            data.put("PTP_OCCUPANCY_STM256",'-'); 
		            //槽道占用率
		            getSlotOccupy(data,ne); 
		            ptptType = networkManagerMapper.judgePtpTypeIsNull(Integer.valueOf(ne.get("BASE_NE_ID").toString())); 
		            if(ptptType!= null && !ptptType.isEmpty()){
			            for(Map tmp:ptptType){
			            	if(tmp.get("RATE")!=null){
				            	if(tmp.get("RATE").equals("2M")){
				            	    data.put("PTP_OCCUPANCY_2M",100);
				            	}else if(tmp.get("RATE").equals("155M")){
				            	    data.put("PTP_OCCUPANCY_STM1",100);
				            	}else if(tmp.get("RATE").equals("622M")){
				            	    data.put("PTP_OCCUPANCY_STM4",100);
				            	}else if(tmp.get("RATE").equals("2.5G")){
				            	    data.put("PTP_OCCUPANCY_STM16",100);
				            	}else if(tmp.get("RATE").equals("10G")){
				            	    data.put("PTP_OCCUPANCY_STM64",100);
				            	}else if(tmp.get("RATE").equals("40G")){
				            	    data.put("PTP_OCCUPANCY_STM256",100);
				            	}  
			            	}
			            }
			            getPortOccupy(data,ne);
		            }  
		            
		            String expMulti=""; 
		        	if ("1".equals(ne.get("TYPE").toString())){ 
		        		//只需计算SDH设备
			            rlData = networkManagerMapper.getRLInfoByneId(Integer.valueOf(ne.get("BASE_NE_ID").toString())); 
			            Map <String,Integer> rtn = new HashMap<String,Integer>();
			            if(rlData!=null && rlData.size()>0){
				            for(Map tmp:rlData){  
				            	rtn =getRlMultiSecOccupy(tmp);
				            	if(rtn.get("isCaled")==0){
				            		continue;
				            	}
				            	//存储的数据格式为ID:NAME:VC4:VC12;ID:NAME:VC4:VC12
				            	expMulti+=tmp.get("RESOURCE_TRANS_SYS_ID").toString()+":"+tmp.get("SYS_NAME").toString()
				            			+":"+rtn.get("VC4")+":"+rtn.get("VC12")+";";
				                //存储在t_resource_ms_wr表中
					            int isExist = networkManagerMapper.searchMsOccupyEarlyWarn(
					            		Integer.valueOf(tmp.get("RESOURCE_TRANS_SYS_ID").toString())); 
					        	rtn.put("RESOURCE_TRANS_SYS_ID",Integer.valueOf(tmp.get("RESOURCE_TRANS_SYS_ID").toString()));
					        	if(isExist>0){ 
					        		networkManagerMapper.updateMsOccupyEarlyWarn(rtn); 
					        	}else{
					        		networkManagerMapper.insertMsOccupyEarlyWarn(rtn); 
					        	} 
			            	} 
		                    expMulti=expMulti.substring(0,expMulti.length()-1);
			            }
		        	}
		        	data.put("MS_OCCUPANCY", expMulti);  
		            Map<String,Object> searchCond=new HashMap<String,Object>(); 
		            searchCond.put("NEID",Integer.valueOf(ne.get("BASE_NE_ID").toString()));
		            //存储在t_resource_network_wr表中
		            List<Map> isExisted = networkManagerMapper.searchNeEarlyWarn(searchCond,0,0); 
		        	data.put("BASE_NE_ID",Integer.valueOf(ne.get("BASE_NE_ID").toString()));
		        	if(isExisted!=null && !isExisted.isEmpty()){ 
		        		networkManagerMapper.updateNeEarlyWarn(data); 
		        	}else{
		        		networkManagerMapper.insertNeEarlyWarn(data); 
		        	}
		        }  
			}
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
		} 
	}
	 
	@SuppressWarnings("rawtypes")
    public void getSlotOccupy(Map<String,Object> data,Map ne){
    	int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
        int unitCnt=0;
        int slotCnt=0; 
        slotCnt = networkManagerMapper.getslotCntByneId(neId); 
        if(slotCnt>0){
            unitCnt = networkManagerMapper.getunitCntByneId(neId);
            double tmpV=1-(unitCnt*1.0/slotCnt); 
            data.put("SLOT_OCCUPANCY",new BigDecimal(tmpV*100).setScale(0,BigDecimal.ROUND_HALF_UP));      
        } 
	}
    
	@SuppressWarnings("rawtypes")
    public void getPortOccupy(Map<String,Object> data,Map ne){
     	int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString()); 
        Map<String,Object> allData=new HashMap<String,Object>();
        Map<String,Object> crsData=new HashMap<String,Object>();  
        allData = networkManagerMapper.getRateCntALL(neId); 
        double tmpV; 
        if(allData!=null && ne.get("TYPE") != null){ 
            Map<String,Object> map=new HashMap<String,Object>(); 
            List <Map> ptpCrsType=new ArrayList<Map>(); 
			if ("1".equals(ne.get("TYPE").toString())) {
				map.put("crs_table", "t_base_sdh_crs");
			} else {
				map.put("crs_table", "t_base_otn_crs");
			}
        	ptpCrsType = networkManagerMapper.judgePtpTypeCrsIsNull(neId,map); 
        	if(ptpCrsType!=null && !ptpCrsType.isEmpty()){
	            crsData = networkManagerMapper.getRateCntCrossconnect(neId,map);
	            if(crsData!=null){ 
		            if(!"0".equals(allData.get("cnt_2M").toString())){
		            	tmpV=1-(Integer.valueOf(crsData.get("cnt_2M").toString())*1.0/Integer.valueOf(allData.get("cnt_2M").toString()));
		                	data.put("PTP_OCCUPANCY_2M",new BigDecimal(tmpV*100).setScale(0,BigDecimal.ROUND_HALF_UP)); 
		            }
		            if(!"0".equals(allData.get("cnt_STM1").toString())){
		            	tmpV=1-(Integer.valueOf(crsData.get("cnt_STM1").toString())*1.0/Integer.valueOf(allData.get("cnt_STM1").toString()));
		            		data.put("PTP_OCCUPANCY_STM1",new BigDecimal(tmpV*100).setScale(0,BigDecimal.ROUND_HALF_UP));
		            }
		            if(!"0".equals(allData.get("cnt_STM4").toString())){
		            	tmpV=1-(Integer.valueOf(crsData.get("cnt_STM4").toString())*1.0/Integer.valueOf(allData.get("cnt_STM4").toString()));
		            	data.put("PTP_OCCUPANCY_STM4",new BigDecimal(tmpV*100).setScale(0,BigDecimal.ROUND_HALF_UP)); 
		            } 
		            if(!"0".equals(allData.get("cnt_STM16").toString())){
		            	tmpV=1-(Integer.valueOf(crsData.get("cnt_STM16").toString())*1.0/Integer.valueOf(allData.get("cnt_STM16").toString()));
		            		data.put("PTP_OCCUPANCY_STM16",new BigDecimal(tmpV*1.0).setScale(0,BigDecimal.ROUND_HALF_UP));
		            } 
		            if(!"0".equals(allData.get("cnt_STM64").toString())){
		            	tmpV=1-(Integer.valueOf(crsData.get("cnt_STM64").toString())*1.0/Integer.valueOf(allData.get("cnt_STM64").toString()));
		            		data.put("PTP_OCCUPANCY_STM64",new BigDecimal(tmpV*100).setScale(0,BigDecimal.ROUND_HALF_UP));  
		            } 
		            if(!"0".equals(allData.get("cnt_STM256").toString())){
		            	tmpV=1-(Integer.valueOf(crsData.get("cnt_STM256").toString())*1.0/Integer.valueOf(allData.get("cnt_STM256").toString()));
		                	data.put("PTP_OCCUPANCY_STM256",new BigDecimal(tmpV*100).setScale(0,BigDecimal.ROUND_HALF_UP)); 
		            }
	            }
        	}
    	}
	} 
	@SuppressWarnings("rawtypes")
	public Map<String,Integer> getRlMultiSecOccupy(Map rl){
		 List<Map> ptpData=new ArrayList<Map>();///环、链上端口 
         List<Map> ctpData=new ArrayList<Map>(); 
         Map<String,Integer> multi = new HashMap<String,Integer>();
		 multi.put("isCaled", 1);
         try{
		 	ptpData = networkManagerMapper.getRLptpIds(Integer.valueOf(rl.get("RESOURCE_TRANS_SYS_ID").toString()));
		 	for(Map ptpl:ptpData){
					if(ptpl.get("rate")!=null && !"".equals(ptpl.get("rate").toString()) &&
					   !("2M".equals(ptpl.get("rate").toString())||"155M".equals(ptpl.get("rate").toString())||
						"622M".equals(ptpl.get("rate").toString())||"2.5G".equals(ptpl.get("rate").toString())||
						"10G".equals(ptpl.get("rate").toString())||"40G".equals(ptpl.get("rate").toString())||
						"100G".equals(ptpl.get("rate").toString()))){ 
					 multi.put("isCaled", 0);
					 return multi;
				}
			} 
			if(ptpData!=null && ptpData.size()>0){
			 	 String ptpIds="(";
				 for(Map ptp:ptpData){
					 if(ptp.get("A_END_PTP")!=null)ptpIds+=ptp.get("A_END_PTP").toString()+",";
					 if(ptp.get("Z_END_PTP")!=null)ptpIds+=ptp.get("Z_END_PTP").toString()+",";
				 }
				 ptpIds=ptpIds.substring(0,ptpIds.length()-1)+")";	 
				 	 int VC12=0,VC4=0;
					//VC12
			   		 ctpData=networkManagerMapper.getVC12Data(ptpIds);
		   		 
		   		 ptpData=networkManagerImpl.getPortMultiSecOccupy("VC12",ptpData,ctpData); 
			   		 for(Map ptp:ptpData){  
			   			VC12+=Integer.valueOf(ptp.get("VC12").toString()); 
			    	 } 
					//VC4
			   		 ctpData=networkManagerMapper.getVC4Data(ptpIds);
		   		 ptpData=networkManagerImpl.getPortMultiSecOccupy("VC4",ptpData,ctpData);
			   
			   		 for(Map ptp:ptpData){
			   			VC4+=Integer.valueOf(ptp.get("VC4").toString());
			    	 }
			   		 if(rl.get("PRO_GROUP_TYPE")!=null && ("2".equals(rl.get("PRO_GROUP_TYPE").toString())||
			   				"3".equals(rl.get("PRO_GROUP_TYPE").toString()))){//2F BLSR、4F BLSR可用率除以2
				   		 multi.put("VC12",(new BigDecimal(VC12*0.5/ptpData.size()).setScale(0,BigDecimal.ROUND_HALF_UP)).intValue()); 
			   			 multi.put("VC4",(new BigDecimal(VC4*0.5/ptpData.size()).setScale(0,BigDecimal.ROUND_HALF_UP)).intValue());  
						 multi.put("VC12MAX", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC12MAX").toString())/2);
						 multi.put("VC12MIN", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC12MIN").toString())/2);
						 multi.put("VC4MAX", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC4MAX").toString())/2);
						 multi.put("VC4MIN", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC4MIN").toString())/2);
			   		  }else{
			 	   		 multi.put("VC12",(new BigDecimal(VC12*1.0/ptpData.size()).setScale(0,BigDecimal.ROUND_HALF_UP)).intValue()); 
			   			 multi.put("VC4",(new BigDecimal(VC4*1.0/ptpData.size()).setScale(0,BigDecimal.ROUND_HALF_UP)).intValue());  
						 multi.put("VC12MAX", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC12MAX").toString()));
						 multi.put("VC12MIN", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC12MIN").toString()));
						 multi.put("VC4MAX", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC4MAX").toString()));
						 multi.put("VC4MIN", Integer.valueOf(ptpData.get(ptpData.size()-1).get("VC4MIN").toString()));
			   		  }
			}else{
				 multi.put("VC12",0);
			     multi.put("VC4",0);
				 multi.put("VC12MAX", 0);
				 multi.put("VC12MIN", 0);
				 multi.put("VC4MAX", 0);
				 multi.put("VC4MIN", 0);  
			 } 
         }catch(Exception e){
        	 
         }
         return multi;
	} 
	
	public boolean isTriggeredQz(Map ne){ 
		try{
			long time = System.currentTimeMillis();  
			SimpleDateFormat formatter = new SimpleDateFormat(CommonDefine.COMMON_FORMAT); 
			Map <String,Object> data=new HashMap<String,Object>();
			data=networkManagerMapper.isTriggeredQz(Integer.valueOf(ne.get("BASE_NE_ID").toString()));
			if(data!=null && data.get("BASIC_SYNC_TIME")!=null){ 
				long t1= formatter.parse(data.get("BASIC_SYNC_TIME").toString()).getTime();   
				if((time-t1)*1.0/(24*60*60*1000)<=1){
					return false;
				}
				if(data.get("UPDATE_TIME")!=null){
					long t2= formatter.parse(data.get("UPDATE_TIME").toString()).getTime();  
					if((time-t2)*1.0/(24*60*60*1000)<=1){
						return false;
					}
				} 
			} 
		}catch(Exception e){
			
		}
		return true;
	} 
}