package com.fujitsu.manager.systemManager.serviceImpl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ILogManagerService;
import com.fujitsu.common.BaseDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.CommonModuleMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Service
@Transactional(rollbackFor=Exception.class)
public class LogManagerServiceImpl implements ILogManagerService{
	@Resource
	private Mongo mongo;
	@Resource
	private CommonModuleMapper commonModuleMapper;
	@Override
	public void JournalToMongodb(Map<String, Object> paramMap)throws CommonException, ParseException {
		// 获取自增id
		int id = getSequenceId(BaseDefine.T_JOURNAL);
		// 获取数据库连接
		DBCollection conn = mongo.getDB(BaseDefine.MONGODB_NAME).getCollection(BaseDefine.T_JOURNAL);
		paramMap.put("_id", id);
		conn.insert(new BasicDBObject(paramMap));
	}
	@Override
	public List<Map<String, Object>> getUserGroupByUserId(int userId) throws CommonException, ParseException {
		return commonModuleMapper.getUserGroupByUserId(userId);
	}
	
	public int getSequenceId(String tableName) throws CommonException {
		// 获取数据库连接
		DBCollection conn = null;
		try {
			conn = mongo.getDB(BaseDefine.MONGODB_NAME).getCollection(BaseDefine.SEQUENCE);
		} catch (Exception e) {
			e.printStackTrace();
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
			map.put("id", BaseDefine._ID + 1);
			conn.insert(new BasicDBObject(map));
			return BaseDefine._ID;
		}else{// 如果存在，直接返回id
			return Integer.parseInt(dbo.get("id").toString());
		}
	}
	
}
