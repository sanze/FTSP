package com.fujitsu;


import java.util.Map;

import org.junit.Test;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.util.SpringContextUtil;

/**
 * Maven
 * @author xuxiaojun
 *
 */
public class TestDataCollectService {

	@Test
	public void testSyncNeCtpImpl(){
		
		SpringContextUtil util = new SpringContextUtil(true);
		
		IDataCollectService service = (IDataCollectService) util.getBean("dataCollectService");
		
		DataCollectMapper mapper = (DataCollectMapper) util.getBean("dataCollectMapper");
		
		
		Map paramter = mapper.selectTableById("t_base_ems_connection", "BASE_EMS_CONNECTION_ID", 6);
        
		int neId = 1052;
		
        try {
//        	service.syncEmsInfo(paramter, DataCollectDefine.COLLECT_LEVEL_1);
        	
//        	service.syncSNC(paramter, DataCollectDefine.COLLECT_LEVEL_1);
        	
//        	service.syncRoute(paramter, DataCollectDefine.COLLECT_LEVEL_1);
        	
//        	service.syncNeList(paramter, DataCollectDefine.COLLECT_LEVEL_1);
        	
//        	service.syncNeEquipmentOrHolder(paramter, neId, DataCollectDefine.COLLECT_LEVEL_1);
//        	
//        	service.syncNePtp(paramter, neId, DataCollectDefine.COLLECT_LEVEL_1);
//
//        	service.syncNeCtp(paramter, neId, DataCollectDefine.COLLECT_LEVEL_1);
        	
//        	service.syncNeCRS(paramter, neId, new short[]{}, DataCollectDefine.COLLECT_LEVEL_1);

//        	service.syncNeVBs(paramter, neId, DataCollectDefine.COLLECT_LEVEL_1);
        	
        	service.syncNeBindingPath(paramter, neId, DataCollectDefine.COLLECT_LEVEL_1);

		} catch (CommonException e) {
			e.printStackTrace();
		}
	}
	
	
}
