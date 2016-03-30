/* 
 * Copyright 2005 - 2009 Terracotta, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package com.fujitsu.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

/**
 * @author xuxiaojun
 *
 */
public class SyncLinkPmJob implements Job {
	
	private PerformanceManagerMapper performanceManagerMapper;
	
	public SyncLinkPmJob(){
		performanceManagerMapper = (PerformanceManagerMapper) SpringContextUtil
				.getBean("performanceManagerMapper");
	}

    public void execute(JobExecutionContext context)
        throws JobExecutionException {

    	//获取昨天日期
    	Date yestoday = CommonUtil.getSpecifiedDay(new Date(), -2, 0);
    	//格式化采集日期
    	String collectDate = new SimpleDateFormat(CommonDefine.COMMON_SIMPLE_FORMAT).format(yestoday);
    	//外部link
    	Integer[] linkType = new Integer[] { CommonDefine.LINK_OUT };
    	//pmIndex集合
    	String[] pmStdIndexList = new String[]{
    			CommonDefine.PM.STD_INDEX_RPL_MAX,
    			CommonDefine.PM.STD_INDEX_TPL_MAX,
    			CommonDefine.PM.STD_INDEX_RPL_CUR,
    			CommonDefine.PM.STD_INDEX_TPL_CUR,
    			CommonDefine.PM.STD_INDEX_RPL_AVG,
    			CommonDefine.PM.STD_INDEX_TPL_AVG
    			};
    	Map<Integer,List<Integer>> ptpGroupByEmsId = new HashMap<Integer,List<Integer>>();
    	//获取link上a端z端ptp集合--IS_MAIN = 1  0：不是 1：是
    	List<Map> ptpIdMapList = performanceManagerMapper.selectPtpIdListFromExternalLink(linkType, CommonDefine.TRUE,CommonDefine.FALSE);
    	//ptpId按网管分组
    	if(ptpIdMapList!=null){
    		for(Map ptpIdMap:ptpIdMapList){
    			Integer emsConnectionId = Integer.valueOf(ptpIdMap.get("emsConnectionId").toString());
    			Integer ptpId = Integer.valueOf(ptpIdMap.get("ptpId").toString());
    			if(ptpGroupByEmsId.containsKey(emsConnectionId)){
    				ptpGroupByEmsId.get(emsConnectionId).add(ptpId);
    			}else{
    				List<Integer> ptpList = new ArrayList<Integer>();
    				ptpList.add(ptpId);
    				ptpGroupByEmsId.put(emsConnectionId, ptpList);
    			}
    		}
    	}
    	//分网管获取性能数据
    	List<Map> originalDataAll = new ArrayList<Map>();
    	for(Integer emsConnectionId:ptpGroupByEmsId.keySet()){
    		
    		String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA+ "_"+emsConnectionId+"_"+collectDate.substring(0, 7).replace("-", "_");
    		//获取数据库表,判断是否存在此分表
    		int tableNumber = performanceManagerMapper.selectTableCount(tableName,SpringContextUtil.getDataBaseParam(CommonDefine.DB_SID));
    		if(tableNumber>0){
    			List<Map> originalData = performanceManagerMapper.selectLinkHistoryPm(tableName, ptpGroupByEmsId.get(emsConnectionId), pmStdIndexList, collectDate);
        		if(originalData!=null && originalData.size()>0){
        			originalDataAll.addAll(originalData);
        		}
    		}
    	}
    	
    	List<Map> linkPmList = new ArrayList<Map>();
    	if(originalDataAll.size()>0){
    		//更新t_base_link_pm表数据
        	List<Map> linkList = performanceManagerMapper.selectLinkList(linkType, CommonDefine.TRUE,CommonDefine.FALSE);
        	for(Map link:linkList){
        		Map linkPm = null;
        		//发送光功率
        		String tplValue = null;
        		//接收光功率
        		String rplValue = null;
        		Integer linkId =  Integer.valueOf(link.get("BASE_LINK_ID").toString());
        		Integer aPtpId = Integer.valueOf(link.get("A_END_PTP").toString());
        		Integer zPtpId = Integer.valueOf(link.get("Z_END_PTP").toString());
        		for(Map origiData:originalDataAll){
        			Integer ptpId = Integer.valueOf(origiData.get("ptpId").toString());
        			String stdIndex = origiData.get("pmStdIndex").toString();
        			String pmValue = origiData.get("pmValue").toString();
        			if(aPtpId.intValue() == ptpId.intValue()&&
        					(stdIndex.equals(CommonDefine.PM.STD_INDEX_TPL_MAX)||
        					stdIndex.equals(CommonDefine.PM.STD_INDEX_TPL_CUR)||
        					stdIndex.equals(CommonDefine.PM.STD_INDEX_TPL_AVG))){
        				tplValue = pmValue;
        			}
        			if(zPtpId.intValue() == ptpId.intValue()&&
        					(stdIndex.equals(CommonDefine.PM.STD_INDEX_RPL_MAX)||
        					stdIndex.equals(CommonDefine.PM.STD_INDEX_RPL_CUR)||
        					stdIndex.equals(CommonDefine.PM.STD_INDEX_RPL_AVG))){
        				rplValue = pmValue;
        			}
        			if(tplValue!=null && rplValue!=null){
        				linkPm = new HashMap();
        				linkPm.put("BASE_LINK_PM_ID", null);
        				linkPm.put("BASE_LINK_ID", linkId);
        				linkPm.put("SEND_OP", tplValue);
        				linkPm.put("REC_OP", rplValue);
        				linkPm.put("COLLECT_DATE", collectDate);
        				linkPmList.add(linkPm);
        				break;
        			}
        		}
        	}
    	}
    	if(linkPmList.size()>0){
    		//批量更新表数据
        	performanceManagerMapper.insertLinkPmBatch(linkPmList);
    	}
    	
    }

}
