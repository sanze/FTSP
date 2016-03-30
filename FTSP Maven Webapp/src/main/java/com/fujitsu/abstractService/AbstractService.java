package com.fujitsu.abstractService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.SpringContextUtil;

/**
 * @author xuxiaojun
 *
 */
public abstract class AbstractService {
	
	private static Map<Integer,ExecutorService> poolMap= new HashMap<Integer,ExecutorService>();
	
//	//创建一个线程池 
//    public static ExecutorService pool = Executors.newFixedThreadPool(50); 
	
	/**
	 * 初始化万能查询参数
	 * 
	 * @param name
	 *            表名
	 * @param id_name
	 *            字段名1
	 * @param id_value
	 *            字段值1
	 * @param id_name_2
	 *            字段名2
	 * @param id_value_2
	 *            字段值2
	 * @param value
	 *            查询所需要的字段
	 * @return
	 */
	protected Map hashMapSon(String name, String id_name, Object id_value,
			String id_name_2, Object id_value_2,String value) {
		Map map = new HashMap();
		map.put("NAME", name);
		map.put("ID_NAME", id_name);
		map.put("ID_VALUE", id_value);
		if(value!=null&&!value.isEmpty()){
			map.put("VALUE", value);
		}else{
			map.put("VALUE", "*");
		}
		if (id_name_2 != null && !id_name_2.isEmpty()) {
			map.put("ID_NAME_2", id_name_2);
			map.put("ID_VALUE_2", id_value_2);
		}
		return map;
	}

	/**
	 * 
	 * @param name 表名
	 * @param id_name 字段名
	 * @param id_value 字段值
	 * @param id 主键
	 * @return
	 */
	protected Map hashMapLatest(String name, String id_name, Object id_value,
			String id) {
		Map map = new HashMap();
		map.put("NAME", name);
		map.put("ID_NAME", id_name);
		map.put("ID_VALUE", id_value);
		map.put("ID", id);

		return map;
	}
	
	protected String getSessionId(){
		HttpServletRequest request = ServletActionContext.getRequest();
		String sessionId = request.getSession().getId();
		return sessionId;
	}
	
	//检查是否需要license
	protected boolean checkNeedToCheckLicense() {
		boolean result = true;
		try {
			String config = CommonUtil.getSystemConfigProperty("licenseCheck");
			if(config == null){
				config = "true";
			}
			result = Boolean.parseBoolean(config);
		} catch (Exception e) {
			return true;
		}
		return result;
	}
	//获取网管任务池
	public synchronized static ExecutorService getPool(Integer emsConnectionId){
		
		if(poolMap.get(emsConnectionId) == null){
			
			CommonManagerMapper commonManagerMapper = (CommonManagerMapper) SpringContextUtil
					.getBean("commonManagerMapper");
			Map connect = commonManagerMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", emsConnectionId);
			
			int maxThreads = (connect.get("THREAD_NUM")+"").matches("\\d+")?Integer.valueOf(connect.get("THREAD_NUM")+""):1;
			
			ExecutorService pool = Executors.newFixedThreadPool(maxThreads); 
			
			poolMap.put(emsConnectionId, pool);
		}
		return poolMap.get(emsConnectionId);
	}
	
	//修改网管任务池
	public synchronized static void setPool(Integer emsConnectionId,Integer maxThreads){
		//移除旧数据
		if(poolMap.get(emsConnectionId) != null){
			poolMap.remove(emsConnectionId);
		}
		//添加新数据
		ExecutorService pool = Executors.newFixedThreadPool(maxThreads); 
		
		poolMap.put(emsConnectionId, pool);
	}
}