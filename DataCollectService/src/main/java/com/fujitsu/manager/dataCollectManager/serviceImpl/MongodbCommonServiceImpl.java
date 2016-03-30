package com.fujitsu.manager.dataCollectManager.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.dataCollectManager.service.MongodbCommonService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Service
@Transactional(rollbackFor = Exception.class)
public class MongodbCommonServiceImpl extends MongodbCommonService {
	@Resource
	private Mongo mongo;
	
	private static DBCollection conn = null;
	
	@Override
	public int getSequenceId(String tableName) throws CommonException {
		// 获取数据库连接
		try {
			if(conn == null){
				conn = mongo.getDB(DataCollectDefine.MONGODB_NAME).getCollection(DataCollectDefine.SEQUENCE);
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_CONNECT);
		}
		// sequence查询条件
		DBObject queryObj = new BasicDBObject("tableName",tableName);
		// sequence更新内容
		DBObject update = new BasicDBObject("$inc", new BasicDBObject("id", 1));
		// 该表的sequence
		DBObject dbo = conn.findAndModify(queryObj,update);
		if(dbo==null){// 如果不存在，则创建该表的sequence,并返回id的默认值+1
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tableName", tableName);
			map.put("id", DataCollectDefine._ID + 1);
			conn.insert(new BasicDBObject(map));
			return DataCollectDefine._ID;
		}else{// 如果存在，直接返回id
			return Integer.parseInt(dbo.get("id").toString());
		}
	}
	

	
}
