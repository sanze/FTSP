package com.fujitsu.manager.southConnectionManager.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.dao.mysql.CommonManagerMapper;
import com.fujitsu.dao.mysql.ConnectionManagerMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.job.EmsSyncJob;
import com.fujitsu.manager.commonManager.service.CommonManagerService;
import com.fujitsu.manager.performanceManager.service.PerformanceManagerService;
import com.fujitsu.manager.southConnectionManager.service.SouthConnectionService;
import com.fujitsu.manager.southConnectionManager.thread.syncSingleNeThread;
import com.fujitsu.model.EmsConnectionModel;
import com.fujitsu.model.EmsGroupModel;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.LinkAlterResultModel;
import com.fujitsu.model.NeAlterModel;
import com.fujitsu.model.NeModel;
import com.fujitsu.model.SdhCrsModel;
import com.fujitsu.model.SubnetModel;
import com.fujitsu.model.SysServiceModel;
import com.fujitsu.util.ExportExcelUtil;
import com.fujitsu.util.SpringContextUtil;

@Scope("prototype")
@Service
//@Transactional(rollbackFor = Exception.class)
public class SouthConnectionServiceImpl extends SouthConnectionService {

	@Resource
	private ConnectionManagerMapper connectionManagerMapper;
	public IDataCollectServiceProxy dataCollectService;
	private Map define = CommonDefine.TREE.TREE_DEFINE;
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;

	@Resource
	private CircuitManagerMapper circuitManagerMapper;

	@Resource
	private CommonManagerMapper commonManagerMapper;

	@Resource
	private ICircuitManagerService circuitManagerService;

	@Resource
	private IAlarmManagementService faultManagerService;

	@Resource
	private IPerformanceManagerService performanceManagerService;

	@Resource
	private ICommonManagerService commonManagerService;
	
	@Resource
	private IQuartzManagerService quartzManagerService;

	private static final int OPERATE_TYPE_ADD = 1;
	private static final int OPERATE_TYPE_MOD = 2;
	private static final int OPERATE_TYPE_DEL = 3;

	/**
	 * 根据网管分组编号查询该网管分组下的所有连接
	 * 
	 * @param emsGroupId
	 *            --网管分组编号
	 * @return List<Map>
	 * @throws CommonException
	 */
	@Override
	public Map<String, Object> getConnectionListByGroupId(int userId,int flag,
			int start, int limit, Integer emsGroupId) throws CommonException {

		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> connectionList = new ArrayList<Map>();
		connectionList = commonManagerService.getAllEmsByEmsGroupId(userId,
				emsGroupId, false, true);
		int total = 0;
		if (connectionList.size() > 0) {

			List<Integer> ids = new ArrayList<Integer>();

			for (Map ma : connectionList) {
				ids.add(Integer.parseInt(ma.get("BASE_EMS_CONNECTION_ID")
						.toString()));
			}

			// map.put("emsGroupId", emsGroupId == 0 ? null : emsGroupId);

			map.put("ids", ids);
			map.put("start", start);
			map.put("limit", limit);
			map.put("flag", flag);

			total = connectionManagerMapper.getConnectionListCount(map);
			connectionList = connectionManagerMapper
					.getConnectionListByGroupId(map);
			returnMap.put("rows", connectionList);
			returnMap.put("total", total);
		} else {
			List<Map> nullList = new ArrayList<Map>();
			returnMap.put("rows", nullList);
			returnMap.put("total", total);
		}

		return returnMap;
	}

	/**
	 * 根据网管连接编号查询该网管连接信息
	 * 
	 * @param emsGroupId
	 *            --网管分组编号
	 * @return List<Map>
	 * @throws CommonException
	 */
	@Override
	public Map getConnectionByEmsConnectionId(
			EmsConnectionModel emsConnectionModel) throws CommonException {
		Map map = new HashMap();
		map = connectionManagerMapper
				.getConnectionByEmsConnectionId(emsConnectionModel
						.getEmsConnectionId());
		// 判断查询的是corba还是telnet ，如果是telnet 执行下面的 否则不执行
		if (Integer.parseInt(map.get("CONNETION_TYPE").toString()) == CommonDefine.CONNETION_TYPE_TELNET) {

			Map neMap = getNeInfoByNeId(Integer.parseInt(map.get(
					"GATEWAY_NE_ID").toString()));

			map.put("NATIVE_EMS_NAME", neMap.get("DISPLAY_NAME").toString());
			map.put("USER_NAME", neMap.get("USER_NAME").toString());
			map.put("PASSWORD", neMap.get("PASSWORD").toString());
		}
		return map;
	}

	
	/**
	 * 根据网管分组编号查询该网管分组下的所有连接
	 * 
	 * @param emsGroupId --网管分组编号
	 * @return List<Map>
	 * @throws CommonException
	 */
	@Override
	public Map getConnectGroup(int emsGroupId) throws CommonException {
		List<Map> returnData = new ArrayList<Map>();
		Map re = new HashMap();
		Map reMap = new HashMap();
		Map result = new HashMap();
		returnData = connectionManagerMapper.getConnectGroup(emsGroupId);
		re.put("BASE_EMS_GROUP_ID", "-1");
		re.put("GROUP_NAME", "全部");
		returnData.add(re);
		reMap.put("BASE_EMS_GROUP_ID", "0");
		reMap.put("GROUP_NAME", "无");
		returnData.add(reMap);
		result.put("rows", returnData);
		result.put("total", returnData.size());
		return result;
	}
	
	@Override
	/**
	 * 根据网管分组编号查询该网管分组下的所有连接
	 * 
	 * @param emsGroupId --网管分组编号
	 * @return List<Map>
	 * @throws CommonException
	 */
	public Map getEmsConnectionGroup(int emsGroupId) throws CommonException {
		List<Map> returnData = new ArrayList<Map>();
		Map re = new HashMap();
		Map reMap = new HashMap();
		Map result = new HashMap();
		returnData = connectionManagerMapper.getConnectGroup(emsGroupId);
		reMap.put("BASE_EMS_GROUP_ID", "0");
		reMap.put("GROUP_NAME", "无");
		returnData.add(reMap);
		result.put("rows", returnData);
		result.put("total", returnData.size());
		return result;
	}

	@Override
	public void updateCollectStatus(EmsConnectionModel emsConnectionModel,
			Integer minutes) throws CommonException {
		Map map = new HashMap();
		map.put("emsConnectionId", emsConnectionModel.getEmsConnectionId());
		map.put("collectStatus", emsConnectionModel.getCollectStatus());
		
		Map reMap = new HashMap();

		reMap = connectionManagerMapper.getEmsCollectInfoByEmsConnectionId(map);
		if (minutes == null) {
			connectionManagerMapper.updateCollectStatus(map);
		} else {
			map.put("minutes", minutes);
			if(CommonDefine.PAUSE_COLLECT_STATUS == Integer.parseInt(reMap.get("COLLECT_STATUS").toString())){
				connectionManagerMapper.updateCollectTime(map);
			} else {
				connectionManagerMapper.updateCollectStatusAndTime(map);
			}
		}
	}
	
	@Override
	public void timeUpdateCollectStatus(EmsConnectionModel emsConnectionModel)
			throws CommonException {
		Map map = new HashMap();
		map.put("emsConnectionId", emsConnectionModel.getEmsConnectionId());
		try {
			Integer dmm = connectionManagerMapper.pauseCollectTime(map);
			if (dmm > 0) {
				System.out.print(dmm);
				Thread.sleep(dmm * 1000 * 1);
				map.clear();
				map.put("emsConnectionId",
						emsConnectionModel.getEmsConnectionId());
				map.put("collectStatus", CommonDefine.NORMAL_COLLECT_STATUS);
				connectionManagerMapper.updateCollectStatus(map);
			}
		} catch (InterruptedException e) {
			throw new CommonException(e, MessageCodeDefine.INTERRUPTED_EXCPT);
		}

	}

	@Override
	public void updateEmsConnectionSync(Integer emsConnectionId,
			Integer taskId, Integer minutes) throws CommonException {
		Map map = new HashMap();
		Map reMap = new HashMap();
		map.put("emsConnectionId", emsConnectionId);
		map.put("taskId", taskId);
		map.put("minutes", minutes);

		reMap = connectionManagerMapper.getEmsSyncTaskParamByTaskId(map);
//		try {
//		SimpleDateFormat format = new SimpleDateFormat(CommonDefine.COMMON_FORMAT);
//		Date date = null;
		connectionManagerMapper.updateEmsConnectionSync(taskId);
		if (reMap == null) {
//			date = new Date();
			connectionManagerMapper.insertEmsSyncTaskParam(map);
		} else {
//			String str = reMap.get("PARAM_VALUE").toString();
//			date = format.parse(str);
//			if (date.after(new Date())) {
//				map.put("date", date);
//				connectionManagerMapper.updateEmsSyncTaskParamAfter(map);
//			} else {
				connectionManagerMapper.updateEmsSyncTaskParamBefore(map);
//			}
		}
//		} catch (ParseException e) {
//			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_PARSE);
//		}
	}

	@Override
	public Map getConnectService() throws CommonException {
		List<Map> connectServiceList = connectionManagerMapper
				.getConnectService();

		Map result = new HashMap();
		result.put("rows", connectServiceList);
		result.put("total", connectServiceList.size());
		return result;
	}

	
	/**
	 * 新增Corba 连接
	 * 
	 * @param emsGroupId --网管分组编号
	 * @return List<Map>
	 * @throws CommonException
	 */
	public String addCorbaConnection(EmsConnectionModel emsConnectionModel)

			throws CommonException {
		
		// 检查是否需要检测license
		if (checkNeedToCheckLicense()) {
			// 检测license是否支持
			if (!checkAllowToAddNms(null)) {
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.LICENSE_OUT_OF_CONN_NUM);
			}
		}

		// 定义一个map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 给map赋值
		map.put("BASE_EMS_CONNECTION_ID", null);
		
		map.put("connectionType", emsConnectionModel.getConnectionType());

		map.put("emsGroupId", emsConnectionModel.getEmsGroupId() == CommonDefine.VALUE_NONE ? null
				: emsConnectionModel.getEmsGroupId());
		map.put("displayName", emsConnectionModel.getEmsDisplayName());
		map.put("type", emsConnectionModel.getEmsType());
		map.put("factory", emsConnectionModel.getFactory());
		map.put("svcRecordId", emsConnectionModel.getConnectServer());
		map.put("connectionMode", emsConnectionModel.getConnectMode());
		if(emsConnectionModel.getConnectMode() == CommonDefine.CONNECT_MODE_MANUAL){
			map.put("connectStatus", CommonDefine.CONNECT_STATUS_DISCONNECT_FLAG);
		}else {
			map.put("connectStatus", null);
		}		
		map.put("ip", emsConnectionModel.getIp());
		map.put("encode", emsConnectionModel.getEncode());
		map.put("port", emsConnectionModel.getPort());
		map.put("emsName", emsConnectionModel.getEmsName());
		map.put("internalEmsName", emsConnectionModel.getInternalEmsName());
		map.put("userName", emsConnectionModel.getUserName());
		map.put("password", emsConnectionModel.getPassword());
		map.put("collecStartTime", CommonDefine.COLLEC_START_TIME);
		map.put("collecEndTime", CommonDefine.COLLEC_END_TIME);
		map.put("collectSource", CommonDefine.HISTORY_PROPERTY);
		map.put("collectStatus", CommonDefine.NORMAL_COLLECT_STATUS);
		map.put("intervalTime", emsConnectionModel.getIntervalTime());
		map.put("timeOut", emsConnectionModel.getTimeOut());
		map.put("threadNum", emsConnectionModel.getThreadNum());
		map.put("iteratorNum", emsConnectionModel.getIteratorNum());

		connectionManagerMapper.addCorbaConnection(map);

		//添加同步网管任务job
		int emsConnectionId = Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID")
				.toString());
		//任务相关
		emsJobRelateOperation(emsConnectionId, OPERATE_TYPE_ADD,
				emsConnectionModel.getEmsDisplayName());
		
		return   map.get("BASE_EMS_CONNECTION_ID").toString();
	}

	//网管级别任务相关
	private void emsJobRelateOperation(int emsConnectionId,int operateType,String displayName) throws CommonException{
		
		switch(operateType){
		case OPERATE_TYPE_ADD :
			//添加网管同步
			addEmsSyncTask(emsConnectionId,displayName);
			//添加电路生成
			circuitManagerService.addCirTask(emsConnectionId,displayName);
			//添加性能采集
			performanceManagerService.insertPmTask(emsConnectionId,
					displayName);
			break;
		case OPERATE_TYPE_MOD:
			//*********************************  网管同步 *******************************
			//获取网管同步任务
			Map taskInfo = connectionManagerMapper.getEmsSyncTask(emsConnectionId);
			//没有任务。。。
			if (taskInfo == null) {
				//添加网管同步任务
				addEmsSyncTask(emsConnectionId,displayName);
			}
			//*********************************  性能采集 *******************************
			//性能采集任务
			Integer taskId = performanceManagerMapper.getTaskIdFromEmsId(emsConnectionId,
					PerformanceManagerService.RegularPmAnalysisDefine);
			//没有任务。。。
			if (taskId == null) {
				//添加性能采集
				performanceManagerService.insertPmTask(emsConnectionId,
						displayName);
			}
			//*********************************  电路生成 *******************************
			//获取电路生成任务
			//电路生成任务Id
			Integer sysTaskId = circuitManagerMapper.getTaskIdFromEmsId(emsConnectionId,
					CommonDefine.TREE.NODE.EMS,CommonDefine.QUARTZ.JOB_CIRCUIT);
			//没有任务。。。
			if (sysTaskId == null) {
					//添加电路生成
					circuitManagerService.addCirTask(emsConnectionId,displayName);
				}
			break;
		case OPERATE_TYPE_DEL:
			List<Integer> emsIds = new ArrayList<Integer>();
			emsIds.add(emsConnectionId);
			//删除网管同步
			deleteEmsSyncTask(emsConnectionId);
			//删除电路生成
			circuitManagerService.deleteCircuitTask(emsConnectionId);
			//删除性能采集
			performanceManagerService.deletePmTask(emsConnectionId);
			//删除告警自动同步任务
			faultManagerService.deleteAlarmAutoSynchSetting(emsConnectionId);
			//删除此网管告警
			faultManagerService.deleteAlarmByEmsIds(emsIds);
			
			break;
		}
	}

	/**
	 * 新增网管时，给网管同步生成新增一条任务记录
	 * 
	 * @param emsId
	 * @throws ParseException 
	 */
	public void addEmsSyncTask(int emsId,String emsName) throws CommonException {
			// 向任务表中插入一条记录
			Map insert = null;
			insert = new HashMap();
			insert.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_BASE);
			// 默认每月执行
			insert.put("TASK_NAME", emsName + "网管同步任务");
			// 默认每月执行
			insert.put("PERIOD_TYPE", CommonDefine.CIR_TASK_CYCLE_MONTH);
			// 周期值 数据格式 年， 季，月，周，日，时间 例：2015，2，5，，4，9:00
			// 表示2015年第2季度 5月份 4号 9:00
			// insert.put("PERIOD", "0,0,2,0,15,10:15");
			// insert.put("START_TIME", value);

			// 每月1号12：00：00执行
			String cycle = "0,0,2,0,15,10:15";
			insert.put("PERIOD", cycle);
			// 计算下次开始时间

//			String next = calculateDate(cycle,
//					CommonDefine.CIR_TASK_CYCLE_MONTH + "");
//			insert.put("NEXT_TIME", next);

			// 挂起
			insert.put("TASK_STATUS", CommonDefine.CIR_TASK_HOLD);
			// 任务执行结果 1.执行成功 2.执行失败 3.执行中 4.执行中止5.暂停6.部分成功
			insert.put("RESULT", null);
		
			connectionManagerMapper.insertTask(insert);
		//获取任务id
		Integer sysTaskId = Integer.parseInt(insert.get("SYS_TASK_ID").toString());

			insert = new HashMap();
			insert.put("TARGET_ID", emsId);
		insert.put("SYS_TASK_ID", sysTaskId);
			// 任务对象类型
			insert.put("TARGET_TYPE", CommonDefine.TREE.NODE.EMS);
			connectionManagerMapper.insertTaskInfo(insert);

			// "0 15 10 15 * ?" 每个月的第15天的上午10:15执行
			String cronExpression = "0 15 10 15 * ?";
			Map map = new HashMap();
			map.put("BASE_EMS_CONNECTION_ID", emsId);
			// 添加一个quartz任务
		quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_BASE,
				sysTaskId,
					EmsSyncJob.class, cronExpression, map);
			// 将任务挂起
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_BASE,
				sysTaskId,
					CommonDefine.QUARTZ.JOB_PAUSE);
	}

	@Override
	/**
	 * 新增  Telnet 网关网元 
	 * @param hashMap 
	 * @throws CommonException
	 */
	public void addNeInfo(HashMap<String, Object> hashMap)
			throws CommonException {
		connectionManagerMapper.addNeInfo(hashMap);

	}

	@Override
	/**
	 * 根据网元本地名称 查询网元信息 
	 * @param nativeEmsName --网元本地名称
	 * @return String
	 * @throws CommonException
	 */
	public String getNeInfoByNativeName(String nativeEmsName)
			throws CommonException {
		String neId = null;
		neId = connectionManagerMapper.getNeInfoByNativeName(nativeEmsName);
		return neId;
	}

	@Override
	/**
	 * 新增 telnet 连接 
	 * @param map 
	 * @throws CommonException
	 */
	public String addTelnetConnection(EmsConnectionModel emsConnectionModel,
			NeModel neModel) throws CommonException {
		String neId = "";

		// 定义一个map类型变量用来存储查询条件
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		// 给map赋值
		hashMap.put("nativeEmsName", neModel.getNativeEmsName());
		hashMap.put("userName", neModel.getUserName());
		hashMap.put("password", neModel.getPassword());

		addNeInfo(hashMap);
		neId = getNeInfoByNativeName(neModel.getNativeEmsName());
		// 定义一个map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 给map赋值
		map.put("BASE_EMS_CONNECTION_ID", null);
		map.put("connectionType", emsConnectionModel.getConnectionType());
		map.put("emsGroupId",emsConnectionModel.getEmsGroupId() == CommonDefine.VALUE_NONE ? null
				: emsConnectionModel.getEmsGroupId());
		map.put("displayName", emsConnectionModel.getEmsDisplayName());
		map.put("type", emsConnectionModel.getTelnetType());
		map.put("factory", emsConnectionModel.getFactory());
		map.put("svcRecordId", emsConnectionModel.getConnectServer());
		map.put("connectionMode", emsConnectionModel.getConnectMode());
		if(emsConnectionModel.getConnectMode() == CommonDefine.CONNECT_MODE_MANUAL){
			map.put("connectStatus", CommonDefine.CONNECT_STATUS_DISCONNECT_FLAG);
		}else {
			map.put("connectStatus", null);
		}
		map.put("ip", emsConnectionModel.getIp());
		map.put("port", emsConnectionModel.getPort());

		map.put("gateWayNeId", neId);
		map.put("userName", neModel.getUserName());
		map.put("password", neModel.getPassword());
		
		map.put("collecStartTime", CommonDefine.COLLEC_START_TIME);
		map.put("collecEndTime", CommonDefine.COLLEC_END_TIME);
		map.put("collectSource", CommonDefine.HISTORY_PROPERTY);
		map.put("collectStatus", CommonDefine.NORMAL_COLLECT_STATUS);
		map.put("intervalTime", emsConnectionModel.getIntervalTime());
		map.put("timeOut", emsConnectionModel.getTimeOut());
		connectionManagerMapper.addTelnetConnection(map);

		addEmsSyncTask(Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID")
				.toString()), emsConnectionModel.getEmsDisplayName());
		circuitManagerService.addCirTask(Integer.parseInt(map.get(
				"BASE_EMS_CONNECTION_ID").toString()), emsConnectionModel.getEmsDisplayName());
		performanceManagerService.insertPmTask(
				Integer.parseInt(map.get("BASE_EMS_CONNECTION_ID").toString()),
				emsConnectionModel.getEmsDisplayName());
		return   map.get("BASE_EMS_CONNECTION_ID").toString();
	}

	/**
	 * 根据网元Id查询网元信息
	 * 
	 * @param neId
	 * @throws CommonException
	 */
	@Override
	public Map getNeInfoByNeId(int neId) throws CommonException {
		Map map = new HashMap();
		map = connectionManagerMapper.getNeInfoByNeId(neId);
		return map;
	}

	/**
	 * 根据网元Id更新网元信息
	 * 
	 * @param hashMap
	 * @throws CommonException
	 */
	@Override
	public void modifyNeInfoByNeId(HashMap<String, Object> hashMap) {
		connectionManagerMapper.modifyNeInfoByNeId(hashMap);
	}

	/**
	 * 修改连接信息
	 * 
	 * @param map
	 * @throws CommonException
	 */
	@Override
	public void modifyConnection(EmsConnectionModel emsConnectionModel,
			NeModel neModel) throws CommonException {
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();

		HashMap<String, Object> hashMap = new HashMap<String, Object>();

		// 给map赋值
		map.put("emsConnectionId", emsConnectionModel.getEmsConnectionId());
		map.put("connectionType", emsConnectionModel.getConnectionType());
		map.put("emsGroupId",
				emsConnectionModel.getEmsGroupId() == CommonDefine.VALUE_NONE ? null
						: emsConnectionModel.getEmsGroupId());
		map.put("displayName", emsConnectionModel.getEmsDisplayName());
		map.put("type", emsConnectionModel.getEmsType());
		map.put("svcRecordId", emsConnectionModel.getConnectServer());
		map.put("connectionMode", emsConnectionModel.getConnectMode());
		map.put("ip", emsConnectionModel.getIp());
		map.put("port", emsConnectionModel.getPort());
		map.put("intervalTime", emsConnectionModel.getIntervalTime());
		map.put("timeOut", emsConnectionModel.getTimeOut());
		map.put("threadNum", emsConnectionModel.getThreadNum());
		map.put("iteratorNum", emsConnectionModel.getIteratorNum());

		if (emsConnectionModel.getConnectionType() == CommonDefine.CONNETION_TYPE_TELNET) {

			// 给map赋值
			hashMap.put("neId", emsConnectionModel.getGateWayNeId());
			hashMap.put("nativeEmsName", neModel.getNativeEmsName());
			hashMap.put("userName", neModel.getUserName());
			hashMap.put("password", neModel.getPassword());

			map.put("userName", neModel.getUserName());
			map.put("password", neModel.getPassword());

			modifyNeInfoByNeId(hashMap);

		} else if (emsConnectionModel.getConnectionType() == CommonDefine.CONNETION_TYPE_CORBA) {

			map.put("encode", emsConnectionModel.getEncode());
			map.put("emsName", emsConnectionModel.getEmsName());
			map.put("internalEmsName", emsConnectionModel.getInternalEmsName());
			map.put("userName", emsConnectionModel.getUserName());
			map.put("password", emsConnectionModel.getPassword());
		}
		connectionManagerMapper.modifyConnection(map);
		//更新任务池
		setPool(emsConnectionModel.getEmsConnectionId(), emsConnectionModel.getThreadNum());
		//任务相关
		emsJobRelateOperation(emsConnectionModel.getEmsConnectionId(),
				OPERATE_TYPE_MOD, emsConnectionModel.getEmsDisplayName());
	}

	/**
	 * 删除网管同步任务
	 * 
	 * @param emsId
	 * @throws CommonException
	 */
	private void deleteEmsSyncTask(int emsId) throws CommonException {
		Map delete = null;
		Map taskInfo = connectionManagerMapper.getEmsSyncTask(emsId);
		if (taskInfo != null) {
			// 先删除quartz任务
			// quartzManagerService
			if(quartzManagerService.IsJobExist(CommonDefine.QUARTZ.JOB_BASE,
					Integer.valueOf(taskInfo.get("SYS_TASK_ID").toString()))){
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_BASE,
					Integer.valueOf(taskInfo.get("SYS_TASK_ID").toString()),
					CommonDefine.QUARTZ.JOB_PAUSE);
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_BASE,
					Integer.valueOf(taskInfo.get("SYS_TASK_ID").toString()),
					CommonDefine.QUARTZ.JOB_DELETE);
			}
//			delete = new HashMap();
//			delete.put("NAME", "t_sys_task_param");
//			delete.put("ID_NAME", "SYS_TASK_ID");
//			delete.put("ID_VALUE", taskInfo.get("SYS_TASK_ID"));
//			connectionManagerMapper.deleteByParameter(delete);
//			delete = new HashMap();
//			delete.put("NAME", "t_sys_task_info");
//			delete.put("ID_NAME", "SYS_TASK_INFO_ID");
//			delete.put("ID_VALUE", taskInfo.get("SYS_TASK_INFO_ID"));
//			connectionManagerMapper.deleteByParameter(delete);
			//数据库做级联删除
			delete = new HashMap();
			delete.put("NAME", "t_sys_task");
			delete.put("ID_NAME", "SYS_TASK_ID");
			delete.put("ID_VALUE", taskInfo.get("SYS_TASK_ID"));
			connectionManagerMapper.deleteByParameter(delete);
		}
	}

	/**
	 * 删除连接信息
	 * 
	 * @param emsConnectionId
	 * @throws CommonException
	 */
	@Override
	public void deleteConnection(EmsConnectionModel emsConnectionModel)
			throws CommonException {
		if (emsConnectionModel.getGateWayNeId() != null) {
			// 删除网元表中该链接的网关网元信息
			deleteNeInfoByNeId(emsConnectionModel.getGateWayNeId());
		}
		//删除网管链接
		connectionManagerMapper.deleteConnection(emsConnectionModel
				.getEmsConnectionId());
		//删除相关任务
		emsJobRelateOperation(emsConnectionModel
				.getEmsConnectionId(),OPERATE_TYPE_DEL,null);
	}

	/**
	 * 删除网元信息
	 * 
	 * @param neId
	 * @throws CommonException
	 */
	@Override
	public void deleteNeInfoByNeId(Integer neId) throws CommonException {
		connectionManagerMapper.deleteNeInfoByNeId(neId);
	}

	/**
	 * 检索网管分组信息
	 * 
	 * @param start
	 * @param limit
	 * @param emsGroupId
	 * @return
	 */
	@Override
	public Map<String, Object> getEmsGroupListByGroupId(int start, int limit,
			Integer emsGroupId) throws CommonException {

		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> emsGroupList = new ArrayList<Map>();
		List<Map> emsGroupCountList = new ArrayList<Map>();
//		emsGroupList = commonManagerService.getAllEmsGroups(userId, start, limit);
//		emsGroupCountList = commonManagerService.getAllEmsGroups(userId, start, null);
		map.put("emsGroupId", emsGroupId);
		int total = connectionManagerMapper.selectEmsGroupListCount(map);
		map.put("start", start);
		map.put("limit", limit);	
		emsGroupList = connectionManagerMapper.selectEmsGroupList(map);
		returnMap.put("rows", emsGroupList);
		returnMap.put("total", total);

		return returnMap;
	}

	/**
	 * 新增网管分组
	 * 
	 * @param emsGroupName
	 * @param emsGroupNote
	 * @return
	 * @throws CommonException
	 */
	@Override
	public void addEmsGroup(EmsGroupModel emsGroupModel) throws CommonException {

		Map map = new HashMap();
		map.put("emsGroupName", emsGroupModel.getEmsGroupName());
		map.put("emsGroupNote", emsGroupModel.getEmsGroupNote());

		connectionManagerMapper.addEmsGroup(map);
	}

	/**
	 * 删除网管分组
	 * 
	 * @param emsGroupId
	 */
	@Override
	public void deleteEmsGroup(Integer emsGroupId) throws CommonException {
		Map map = new HashMap();
		map.put("emsGroupId", emsGroupId);

		connectionManagerMapper.updateEmsConnnectionByEmsGroupId(map);
		connectionManagerMapper.deleteEmsGroup(map);
	}

	/**
	 * 修改网管分组
	 * 
	 * @param emsGroupModels
	 */
	@Override
	public void modifyEmsGroup(EmsGroupModel emsGroupModel)
			throws CommonException {
		Map map = new HashMap();
		map.put("emsGroupId", emsGroupModel.getEmsGroupId());
		map.put("emsGroupName", emsGroupModel.getEmsGroupName());
		map.put("emsGroupNote", emsGroupModel.getEmsGroupNote());
		connectionManagerMapper.modifyEmsGroup(map);
	}

	/**
	 * 新增子网
	 */
	@Override
	public void addSubnet(SubnetModel subnetModel) throws CommonException {

		Map map = new HashMap();
		map.put("subnetName", subnetModel.getSubnetName());
		map.put("subnetNote", subnetModel.getSubnetNote());
		map.put("parentSubnetId", subnetModel.getParentSubnetId());
		map.put("emsConnectionId", subnetModel.getEmsConnectionId());

		connectionManagerMapper.addSubnet(map);

	}

	/**
	 * 修改子网
	 */
	@Override
	public void modifySubnet(SubnetModel subnetModel) throws CommonException {

		Map map = new HashMap();
		map.put("subnetId", subnetModel.getSubnetId());
		map.put("subnetName", subnetModel.getSubnetName());
		map.put("subnetNote", subnetModel.getSubnetNote());

		connectionManagerMapper.modifySubnet(map);

	}

	/**
	 * 根据子网编号取得子网信息
	 */
	@Override
	public Map getSubnetBySubnetId(Integer subnetId) throws CommonException {
		Map returnData = new HashMap();
		returnData = connectionManagerMapper.getSubnetBySubnetId(subnetId);
		return returnData;
	}

	/**
	 * 删除子网
	 */
	@Override
	public void deleteSubnet(Integer subnetId) throws CommonException {
		Map map = new HashMap();
		map.put("emsGroupId", subnetId);
		//
		connectionManagerMapper.getSubnetBySubnetId(subnetId);
		// 更新子网下的网元信息中的子网值
		connectionManagerMapper.updateNeInfoBySubnetId(map);
		connectionManagerMapper.deleteSubnet(map);
	}

	/**
	 * 获取网元同步信息列表
	 */
	@Override
	public Map<String, Object> getSyncNeListByEmsInfo(int userId,int start, int limit,
			Integer emsGroupId, Integer emsConnectionId)
			throws CommonException {

		Map returnMap = new HashMap();
		//获取网元列表
		List<Map> neList = commonManagerMapper.getAllNeByEmsIdWithAdditionInfo(userId,
				emsConnectionId, start,limit,new Integer[] { CommonDefine.FALSE,
						CommonDefine.DELETE_FLAG },CommonManagerService.TREE_DEFINE);
		//获取总数
		int total = commonManagerMapper.getAllNeByEmsIdCount(userId,
				emsConnectionId, new Integer[] { CommonDefine.FALSE,
						CommonDefine.DELETE_FLAG },CommonManagerService.TREE_DEFINE);
		
		returnMap.put("rows", neList);
		returnMap.put("total", total);

		return returnMap;
	}

	/**
	 * 根据网管分组编号获取该分组下的网管列表信息
	 */
	@Override
	public Map getEmsConnection(Integer emsGroupId) throws CommonException {
		List<Map> returnData = new ArrayList<Map>();

		returnData = connectionManagerMapper
				.getEmsConnection(emsGroupId == 0 ? null : emsGroupId);

		Map re = new HashMap();
		re.put("emsConnectionId", "-1");
		re.put("emsGroupId", "-1");
		re.put("emsConnectionName", "全部");
		returnData.add(re);
		Map result = new HashMap();

		result.put("rows", returnData);
		result.put("total", returnData.size());
		return result;
	}

	/**
	 * 根据网管编号检索本地网元列表信息
	 */
	@Override
	public List<Map> getNeListSyncByEmsConnectionId(Integer emsConnectionId)
			throws CommonException {
		List<Map> returnData = new ArrayList<Map>();

		returnData = connectionManagerMapper
				.getNeListSyncByEmsConnectionId(emsConnectionId);

		return returnData;
	}

	/**
	 * 删除网管下的第一级子网的操作
	 */
	@Override
	public void deleteEmsSubnet(Integer emsConnectionId, Integer subnetId)
			throws CommonException {

		// 更新要删除子网的下级子网的父节点为null
		connectionManagerMapper.updateSubnetByParentSubnetId(subnetId);
		// 更新要删除子网下的网元的子网信息为null
		connectionManagerMapper.updateNeBySubnetId(subnetId);
		// 从子网表中删除该子网信息
		connectionManagerMapper.deleteSubnetBySubnetId(subnetId);

	}

	/**
	 * 子网管理页面中 删除子网下的子网操作
	 */
	@Override
	public void deleteSubnetSubnet(Integer parentSubnetId, Integer subnetId)
			throws CommonException {

		// 更新要删除子网的下级子网的父节点为自己的父子网编号
		connectionManagerMapper
				.updateSubnetBySubnetId(parentSubnetId, subnetId);
		// 更新要删除子网下的网元的子网信息为自己的父子网编号
		connectionManagerMapper.updateNeByParentSubnetId(parentSubnetId,
				subnetId);
		// 从子网表中删除该子网信息
		connectionManagerMapper.deleteSubnetBySubnetId(subnetId);

	}

	/**
	 * @@@分权分域到网元@@@
	 * 子网管理页面中 根据网管编号获取网元列表信息
	 */
	@Override
	public Map getNeListByEmsConnnectionId(Integer emsConnectionId, Integer sysUserId)
			throws CommonException {
		List<Map> map = new ArrayList();
		Map result = new HashMap();
		map = connectionManagerMapper
				.getNeListByEmsConnnectionId(emsConnectionId, sysUserId, this.define);
		result.put("rows", map);
		result.put("total", map.size());
		return result;
	}

	/**
	 * @@@分权分域到网元@@@
	 * 子网管理页面中 根据网管编号和子网编号获取网元列表信息
	 */
	@Override
	public Map getNeListByEmsConnnectionIdAndSubnetId(NeModel neModel, Integer sysUserId)
			throws CommonException {
		List<Map> map = new ArrayList();
		Map reMap = new HashMap();
		map = connectionManagerMapper.getNeListByEmsConnnectionIdAndSubnetId(
				neModel.getEmsConnectionId(), neModel.getSubnetId(), sysUserId, this.define);

		reMap.put("rows", map);
		reMap.put("total", map.size());
		return reMap;
	}

	/**
	 * 网元管理页面中新增网元
	 */
	@Override
	public void addTelnetNe(List<Map> neList) throws CommonException {
		Map map = new HashMap();
		for (Map neMap : neList) {
			connectionManagerMapper.addTelnetNe(neMap);
		}
	}

	/**
	 * 根据网元编号获取Telnet网元
	 */
	@Override
	public Map getTelnetNeByNeId(NeModel neModel) throws CommonException {
		Map map = new HashMap();
		map.put("neId", neModel.getNeId());
		map.put("emsConnectionId",neModel.getEmsConnectionId());
		map = connectionManagerMapper.getTelnetNeByNeId(map);
		return map;
	}

	/**
	 * 修改Telnet网元
	 */
	@Override
	public void modifyTelnetNe(NeModel neModel)
			throws CommonException {

		Map map = new HashMap();
		map.put("neId", neModel.getNeId());
		map.put("displayName", neModel.getDisplayName());
		map.put("userName", neModel.getUserName());
		map.put("password", neModel.getPassword());
		map.put("connectionMode", neModel.getConnectionMode());
		connectionManagerMapper.modifyTelnetNe(map);

	}

	/**
	 * 修改Corba网元
	 */
	@Override
	public void modifyCorbaNe(NeModel neModel)
			throws CommonException {

		Map map = null;
		//批量更新
		if(neModel.getNeIdList()!=null){
			for(Integer neId:neModel.getNeIdList()){
				map = new HashMap();
				map.put("neId", neId.intValue());
				map.put("syncMode", neModel.getSyncMode());
				connectionManagerMapper.modifyCorbaNe(map);
			}
		}else{
			//单网元更新
			map = new HashMap();
			map.put("neId", neModel.getNeId());
			map.put("userLabel", neModel.getUserLabel());
			map.put("syncMode", neModel.getSyncMode());
			connectionManagerMapper.modifyCorbaNe(map);
		}
	}

	/**
	 * 删除Telnet网元
	 */
	@Override
	public void deleteTelnetNe(Integer emsConnectionId, Integer neId)
			throws CommonException {
		Map map = new HashMap();
		map.put("neId", neId);
		map.put("emsConnectionId", emsConnectionId);
		connectionManagerMapper.deleteTelnetNe(map);
	}

	/**
	 * 子网分组中保存按钮功能
	 */
	@Override
	public void saveClassifiedNe(Integer subnetId, String jString)
			throws CommonException {
		Map map = new HashMap();
		JSONArray jsonArray = JSONArray.fromObject(jString);

		// 解析选中的节点信息
		for (Object o : jsonArray) {
			JSONObject jsonObject = (JSONObject) o;
			NeModel ne = (NeModel) JSONObject.toBean(jsonObject, NeModel.class);

			// 保存新分组的网元
			connectionManagerMapper.saveClassifiedNe(ne.getSubnetId(),
					ne.getNeId());
		}

	}

	/**
	 * 网元管理页面中 列表同步 操作弹窗 获取新增网元的信息列表
	 */
	@Override
	public Map getAlterNeByEmsConnectionId(Integer emsConnectionId)
			throws CommonException {
		Map map = new HashMap();
		List neList = new ArrayList();
		dataCollectService = SpringContextUtil
				.getDataCollectServiceProxy(emsConnectionId);

		List<NeAlterModel> NeAlterModelList = dataCollectService
				.getNeAlertList(CommonDefine.COLLECT_LEVEL_1);

		for (NeAlterModel neTemp : NeAlterModelList) {
			Map<String, String> reMap = new HashMap<String, String>();
			reMap.put("changeType", String.valueOf(neTemp.getChangeType()));
			reMap.put("neName", neTemp.getNeName());
			neList.add(reMap);
		}

		map.put("rows", neList);
		map.put("total", neList.size());
		return map;
	}

	/**
	 * 网元管理页面中 列表同步 操作弹窗中 新增 按钮操作
	 */
	@Override
	public void neListSyncAdd(Integer emsConnectionId) throws CommonException {
		dataCollectService = SpringContextUtil
				.getDataCollectServiceProxy(emsConnectionId);
		dataCollectService.syncNeList(CommonDefine.COLLECT_LEVEL_1);
	}

	
	/**
	 * 网元管理页面中 网元同步 操作
	 */
	@Override
	public void syncSelectedNe(String jsonString) throws CommonException {
		Map map = new HashMap();
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		//采集优先级
		Integer collectLevel = CommonDefine.COLLECT_LEVEL_1;
        
		NeModel neModel = null;

		Callable  syncThread = null;
		
		List<Future> futureList = new ArrayList<Future>();
		
		//提交同步任务
		for (Object o : jsonArray) {
			JSONObject jsonObject = (JSONObject) o;
			neModel = (NeModel) JSONObject.toBean(jsonObject, NeModel.class);
			
			Map param = new HashMap();
			param.put("sessionId", getSessionId());
			param.put("syncName", neModel.getSyncName());
			param.put("DISPLAY_NAME", neModel.getDisplayName());
			param.put("SUPORT_RATES", neModel.getSuportRates());

			// 开启线程
			syncThread = new syncSingleNeThread(
					neModel.getEmsConnectionId(), neModel.getNeId(),
					param,collectLevel,true);
			//执行同步
			Future future = getPool(neModel.getEmsConnectionId()).submit(syncThread);
			//保存
			futureList.add(future);
		}
		//同步完成计数
		Integer syncCount = 0;
		
		//初始化进度
		CommonDefine.setProcessParameter(
				getSessionId(),
				neModel.getSyncName(),
				syncCount,
				jsonArray.size(),
				null);
		
		//监测同步进度
		while(syncCount!=jsonArray.size()){
			//判断是否取消操作
			if (CommonDefine.getIsCanceled(getSessionId(), neModel.getSyncName())) {
				//取消所有同步进程
				for(Future future : futureList){
					future.cancel(false);
				}
				CommonDefine.respCancel(getSessionId(), neModel.getSyncName());
				break;
			}
			//未取消正常读取进度
			for(Future future : futureList){
				if(future.isDone()){
					syncCount++;
					
					futureList.remove(future);
					// 更新进度值--不更新内容
					CommonDefine.setProcessParameter(
							getSessionId(),
							neModel.getSyncName(),
							syncCount,
							jsonArray.size(),
							null);
					break;
				}
			}
		}
	}
	
	/**
	 * 网元管理页面 断开连接
	 */
	@Override
	public void disConnect(EmsConnectionModel emsConnectionModel)
			throws CommonException {
		boolean bool = false;
		dataCollectService = SpringContextUtil
				.getDataCollectServiceProxy(emsConnectionModel.getEmsConnectionId());
		if (emsConnectionModel.getConnectionType() == CommonDefine.CONNETION_TYPE_CORBA) {
			bool = dataCollectService.disCorbaConnect();
		} else if (emsConnectionModel.getConnectionType() == CommonDefine.CONNETION_TYPE_TELNET) {
			bool = dataCollectService.disTelnetConnect();
		}
		if (bool == true) {
			emsConnectionModel.setConnectMode(CommonDefine.CONNECT_MODE_MANUAL);

			Map map = new HashMap();
			map.put("emsConnectionId", emsConnectionModel.getEmsConnectionId());
			map.put("connectStatus", CommonDefine.CONNECT_STATUS_DISCONNECT_FLAG);
			map.put("connectMode", CommonDefine.CONNECT_MODE_MANUAL);
			connectionManagerMapper.updateEmsConnection(map);
		}
	}

	/**
	 * 网元管理页面 启动连接
	 */
	@Override
	public void startConnect(EmsConnectionModel emsConnectionModel)
			throws CommonException {
		int connectStatus = 0;
		String message = null;
		dataCollectService = SpringContextUtil
				.getDataCollectServiceProxy(emsConnectionModel.getEmsConnectionId());
		try {
			if (emsConnectionModel.getConnectionType() == CommonDefine.CONNETION_TYPE_CORBA) {
				connectStatus = dataCollectService.startCorbaConnect();
			} else if (emsConnectionModel.getConnectionType() == CommonDefine.CONNETION_TYPE_TELNET) {
				connectStatus = dataCollectService.startTelnetConnect();
			}
		} catch (CommonException e) {
			message = e.getErrorMessage();
			connectStatus = CommonDefine.CONNECT_STATUS_EXCEPTION_FLAG;
		}

		Map map = new HashMap();
		map.put("emsConnectionId", emsConnectionModel.getEmsConnectionId());
		map.put("connectStatus", connectStatus);
		map.put("exceptionReason", message);
		map.put("connectMode", CommonDefine.CONNECT_MODE_AUTO);
		connectionManagerMapper.updateEmsConnection(map);
	}

	/**
	 * 根据网管分组、网管获取交叉连接信息
	 */
	@Override
	public Map<String, Object> getCrossConnectListByEmsInfo(int userId,
			int start, int limit, Integer emsGroupId, Integer emsConnectionId)
			throws CommonException {
		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> connectionList = new ArrayList<Map>();

		connectionList = commonManagerService.getAllNeByEmsId(userId,
				emsConnectionId, false, null);
		int total = 0;
		// map.put("emsGroupId", emsGroupId == 0 ? null : emsGroupId);
		// map.put("emsConnectionId", emsConnectionId);
		if (connectionList.size() > CommonDefine.FALSE) {
			List<Integer> neIdList = new ArrayList<Integer>();
			
			for (Map neId : connectionList) {
				neIdList.add(Integer
						.parseInt(neId.get("BASE_NE_ID").toString()));
			}

			map.put("neIdList", neIdList);
			map.put("start", start);
			map.put("limit", limit);
			total = connectionManagerMapper.getCrossConnectListCount(map);
			connectionList = connectionManagerMapper
					.getCrossConnectListByEmsInfo(map);
			returnMap.put("rows", connectionList);
			returnMap.put("total", total);
		} else {
			List<Map> nullList = new ArrayList<Map>();
			returnMap.put("rows", nullList);
			returnMap.put("total", total);
		}
		return returnMap;
	}

	/**
	 * Telnet网元登录操作
	 */
	@Override
	public void logonTelnetNe(String jsonString) throws CommonException {
		Map map = new HashMap();
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		// 解析选中的节点信息
		for (Object o : jsonArray) {
			JSONObject jsonObject = (JSONObject) o;
			NeModel ne = (NeModel) JSONObject.toBean(jsonObject, NeModel.class);
			dataCollectService = SpringContextUtil.getDataCollectServiceProxy(ne
					.getEmsConnectionId());
			dataCollectService.logonTelnetNe(ne.getNeId());
		}
	}

	/**
	 * Telnet网元退出登录操作
	 */
	@Override
	public void logoutTelnetNe(String jsonString) throws CommonException {
		Map map = new HashMap();
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		// 解析选中的节点信息
		for (Object o : jsonArray) {
			JSONObject jsonObject = (JSONObject) o;
			NeModel ne = (NeModel) JSONObject.toBean(jsonObject, NeModel.class);
			dataCollectService = SpringContextUtil.getDataCollectServiceProxy(ne
					.getEmsConnectionId());
			dataCollectService.logoutTelnetNe(ne.getNeId());
		}
	}

	/**
	 * 交叉连接管理页面同步操作
	 */
	@Override
	public void syncNeCrossConnnection(String jsonString)
			throws CommonException {
		Map map = new HashMap();
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer collectLevel = CommonDefine.COLLECT_LEVEL_1;
		//初始化异常消息
		String message="";
		// 解析选中的节点信息
		Integer count = 0;
		for (Object o : jsonArray) {	
			JSONObject jsonObject = (JSONObject) o;
			NeModel ne = (NeModel) JSONObject.toBean(jsonObject,
					NeModel.class);
			//初始化同步结果
			int syncResult = CommonDefine.NE_SYNC_HAD;
			if (CommonDefine.getIsCanceled(getSessionId(), ne.getSyncName())) {
				CommonDefine.respCancel(getSessionId(), ne.getSyncName());
				break;
			} else {
				String str = ne.getSuportRates();
				short[] layerRateList = null;
				if (str.isEmpty()){
					layerRateList =null;
				} else {
					layerRateList = constructLayRates(str);
				}
				int neId = ne.getNeId();
				String text;
				dataCollectService = SpringContextUtil.getDataCollectServiceProxy(ne
						.getEmsConnectionId());
				count++;
				try {
					// 进度描述信息更改--此处修改
					text = "正在进行：    "+ne.getDisplayName() + "    正在同步交叉连接信息";
					// 加入进度值
					CommonDefine.setProcessParameter(getSessionId(), ne.getSyncName(), 
							count,jsonArray.size(),text);
					dataCollectService.syncNeCRS(neId, layerRateList,
							collectLevel);
				} catch (CommonException e) {
					syncResult = CommonDefine.NE_SYNC_FAILED;
					message = e.getErrorMessage();
				} finally {
					connectionManagerMapper.updateNeCRSInfo(neId,
							syncResult, message);
				}
			}
		}
	}
	
//	/**
//	 * 网元 交叉连接同步 操作
//	 * @param emsConnectionId
//	 * @param neId
//	 * @param layerRateList
//	 * @param collectLevel
//	 * @throws CommonException
//	 */
//	public void syncCrossConnnection(Integer neId, short[] layerRateList,
//			Integer collectLevel) throws CommonException {
//		try {
//			dataCollectService.syncNeCRS(neId, layerRateList, collectLevel);
//		} catch (CommonException e) {
//			System.out.print(e.getErrorMessage());
//			connectionManagerMapper.updateNeCRSInfo(neId,
//					CommonDefine.NE_SYNC_FAILED, e.getErrorMessage());
//		}
//	}

	/**
	 * 交叉连接页面 详情按钮功能
	 */
	@Override
	public Map getCrsNeDetailInfoByNeId(int start, int limit, NeModel neModel,
			SdhCrsModel sdhCrsModel) throws CommonException {
		Map map = new HashMap();
		Map returnMap = new HashMap();
		int total;

		List<Map> crsDetailInfoList = new ArrayList<Map>();
		map.put("neId", neModel.getNeId());
		map.put("type", neModel.getType());
		if (null != sdhCrsModel.getRate()) {
			map.put("circuitCount", sdhCrsModel.getCircuitCount());
			map.put("rate", sdhCrsModel.getRate().equals("C") ? null
					: sdhCrsModel.getRate());
			map.put("changeState", sdhCrsModel.getChangeState());
		}
		if (neModel.getType() == CommonDefine.NE_TYPE_SDH_FLAG) {
			map.put("start", start);
			map.put("limit", limit);

			total = connectionManagerMapper.getCrsSdhNeDetailInfoCount(map);

			crsDetailInfoList = connectionManagerMapper
					.getCrsSdhNeDetailInfoByNeId(map);

		} else {
			map.put("start", start);
			map.put("limit", limit);
			total = connectionManagerMapper.getCrsOtherNeDetailInfoCount(map);
			crsDetailInfoList = connectionManagerMapper
					.getCrsOtherNeDetailInfoByNeId(map);
		}

		returnMap.put("rows", crsDetailInfoList);
		returnMap.put("total", total);

		return returnMap;
	}

	/**
	 * 以太网管理页面根据网管分组和网管查询功能
	 */
	@Override
	public Map<String, Object> getMstpListByEmsInfo(int userId,int start, int limit,
			Integer emsGroupId, Integer emsConnectionId) throws CommonException {
		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> connectionList = new ArrayList<Map>();
		
		connectionList = commonManagerService.getAllNeByEmsId(userId,
				emsConnectionId, false, null);
		int total = 0;
		// map.put("emsGroupId", emsGroupId == 0 ? null : emsGroupId);
		// map.put("emsConnectionId", emsConnectionId);
		if (connectionList.size() > CommonDefine.FALSE) {
			List<Integer> neIdList = new ArrayList<Integer>();
			
			for (Map neId : connectionList) {
				neIdList.add(Integer
						.parseInt(neId.get("BASE_NE_ID").toString()));
			}

			map.put("neIdList", neIdList);
			map.put("start", start);
			map.put("limit", limit);
			total = connectionManagerMapper.getMstpListCount(map);
			connectionList = connectionManagerMapper
					.getMstpListByEmsInfo(map);
			returnMap.put("rows", connectionList);
			returnMap.put("total", total);
		} else {
			List<Map> nullList = new ArrayList<Map>();
			returnMap.put("rows", nullList);
			returnMap.put("total", total);
		}

		return returnMap;
	}

	/**
	 * 以太网管理页面中 同步以太网操作
	 */
	@Override
	public void syncMstpNe(String jsonString) throws CommonException {
		Map map = new HashMap();
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer collectLevel = CommonDefine.COLLECT_LEVEL_1;
		// 解析选中的节点信息
		Integer count = 0;
		for (Object o : jsonArray) {
			
			JSONObject jsonObject = (JSONObject) o;
			NeModel ne = (NeModel) JSONObject.toBean(jsonObject,
					NeModel.class);
			
			if (CommonDefine.getIsCanceled(getSessionId(), ne.getSyncName())) {
				CommonDefine.respCancel(getSessionId(), ne.getSyncName());
				break;
			} else {
				//进度描述信息更改--此处修改
				String text = "正在进行：    "+ne.getDisplayName() + "    同步开始";
				CommonDefine.setProcessParameter(getSessionId(), ne.getSyncName(), 
						count, jsonArray.size(),text);
				count++;
				dataCollectService = SpringContextUtil.getDataCollectServiceProxy(ne
						.getEmsConnectionId());
				try{
					syncMstp(ne, collectLevel);
				}catch (CommonException e) {
					System.out.println(e.getErrorMessage());
				} finally{
					//进度描述信息更改--此处修改
					text = "正在进行：    "+ne.getDisplayName() + "    同步完成";
					CommonDefine.setProcessParameter(getSessionId(), ne.getSyncName(), 
							count, jsonArray.size(),text);
				}
			}
		}
	}
	
	/**
	 * 网元以太网同步操作
	 * @param neId
	 * @param collectLevel
	 * @throws CommonException
	 */
	private void syncMstp(NeModel ne, Integer collectLevel) throws CommonException {
		// 初始化同步结果
		int syncResult = CommonDefine.NE_SYNC_HAD;
		// 初始化异常消息
		String message = "";
		try {
			String text;
			try {
				// 进度描述信息更改--此处修改
				text = "正在进行：    "+ne.getDisplayName()+"    正在同步以太网信息";
				// 加入进度值
				CommonDefine.setProcessParameter(getSessionId(), ne.getSyncName(),
						null,null,text);
				dataCollectService.syncNeEthService(ne.getNeId(), collectLevel);
			} catch (CommonException e) {
				syncResult = CommonDefine.NE_SYNC_FAILED;
				message = e.getErrorMessage();
				System.out.print(e.getErrorMessage());
			}

			try {
				// 进度描述信息更改--此处修改
				text = "正在进行：    "+ne.getDisplayName() + "    正在同步bindingPath信息";
				// 加入进度值
				CommonDefine.setProcessParameter(getSessionId(), ne.getSyncName(), 
						null,null,text);
				dataCollectService
						.syncNeBindingPath(ne.getNeId(), collectLevel);
			} catch (CommonException e) {
				syncResult = CommonDefine.NE_SYNC_FAILED;
				message = e.getErrorMessage();
				System.out.print(e.getErrorMessage());

			}
		} catch (Exception e) {
			syncResult = CommonDefine.NE_SYNC_FAILED;
			message = "未知错误！";
		} finally {
			connectionManagerMapper.updateNeMstpSyncInfo(ne.getNeId(),
					syncResult, message);
		}
	}

	/**
	 * 根据网管分组编号获取该网管分组下的所有拓扑链路信息
	 */
	@Override
	public Map getTopoLinkSyncListByEmsGroupId(int userId,int start, int limit,
			Integer emsGroupId) throws CommonException {
		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> connectionList = new ArrayList<Map>();
		
		connectionList = commonManagerService.getAllEmsByEmsGroupId(userId, emsGroupId, false, true);
		int total = 0;
		// map.put("emsGroupId", emsGroupId == 0 ? null : emsGroupId);
		// map.put("emsConnectionId", emsConnectionId);
		if (connectionList.size() > CommonDefine.FALSE) {
			List<Integer> emsIdList = new ArrayList<Integer>();
			
			for (Map emsConnection : connectionList) {
				emsIdList.add(Integer
						.parseInt(emsConnection.get("BASE_EMS_CONNECTION_ID").toString()));
			}

			map.put("emsIdList", emsIdList);
			map.put("start", start);
			map.put("limit", limit);
			total = connectionManagerMapper.getTopoLinkSyncListCount(map);
			connectionList = connectionManagerMapper
					.getTopoLinkSyncListByEmsGroupId(map);
			returnMap.put("rows", connectionList);
			returnMap.put("total", total);
		} else {
			List<Map> nullList = new ArrayList<Map>();
			returnMap.put("rows", nullList);
			returnMap.put("total", total);
		}

		return returnMap;
	}

	/**
	 * 拓扑链路同步 页面 同步操作
	 */
	@Override
	public Map topoLinkSync(EmsConnectionModel emsConnectionModel)
			throws CommonException {
		Map map = new HashMap();

		dataCollectService = SpringContextUtil
				.getDataCollectServiceProxy(emsConnectionModel
						.getEmsConnectionId());
		try {
			//先置为正在同步
			int syncResult=CommonDefine.LINK_SYNC_DOING;
			//更新同步值为正在同步
			connectionManagerMapper.updateEmsLinkSyncInfo(
					emsConnectionModel.getEmsConnectionId(),
					syncResult, "");
			
			LinkAlterResultModel returnModel = dataCollectService
					.getLinkAlterList(CommonDefine.COLLECT_LEVEL_1);
//			if (returnModel.isNeedSyncEms() == true) {
//				map.put("isNeedSyncEms", returnModel.isNeedSyncEms());
//			} else {
				map.putAll(topoLinkSyncReturnInfo(returnModel));
				syncResult=CommonDefine.LINK_SYNC_HAD;
				String errorMsg="";
				if(returnModel.isNeedSyncEms()||returnModel.isNeedSyncNe()){
					syncResult=CommonDefine.LINK_SYNC_FAILED;
					errorMsg=returnModel.isNeedSyncEms()?"网元匹配失败":"端口匹配失败";
				}
//				if(!returnModel.isNeedSyncNe()){
					connectionManagerMapper.updateEmsLinkSyncInfo(
							emsConnectionModel.getEmsConnectionId(),
							syncResult, errorMsg);
//				}
//			}
			return map;
		} catch (CommonException e) {

			// 更新同步结果
			connectionManagerMapper.updateEmsLinkSyncInfo(
					emsConnectionModel.getEmsConnectionId(),
					CommonDefine.LINK_SYNC_FAILED, e.getErrorMessage());
			throw e;
		}
	}
	
	/**
	 * 拓扑链路同步 页面 同步操作返回信息
	 */
	private Map topoLinkSyncReturnInfo(LinkAlterResultModel returnModel)
			throws CommonException {
		Map map = new HashMap();

		map.put("returnResult", CommonDefine.SUCCESS);
		map.put("isChanged", returnModel.isChanged());
		map.put("isNeedSyncEms", returnModel.isNeedSyncEms());
		map.put("isNeedSyncNe", returnModel.isNeedSyncNe());
		
		// 是否有变化 false:无变化 true:有变化
		if (returnModel.isChanged()) {
			Map neMap = new HashMap();
			Map neAMap = new HashMap();
			Map neZMap = new HashMap();
			List<Map> linkList = new ArrayList<Map>();

			// 变化列表
			List<LinkAlterModel> changeList = returnModel.getChangeList();
			if (changeList != null) {
				for (LinkAlterModel model : changeList) {
					if(model.getLink()==null) continue;
					//不列出无冲突变更
					if(model.getConflictList()==null||model.getConflictList().isEmpty()){
						continue;
					}
					// neMap.put("name", neSerialNo);
					Map addMap = new HashMap();
					addMap.put("linkAlterModel", model);
					neAMap = connectionManagerMapper.getLinkByChangeInfo(
							model.getaEndPtp());
					neZMap = connectionManagerMapper.getLinkByChangeInfo(
							model.getzEndPtp());
					 //1.新增 2.删除
					 addMap.put("changeType", model.getChangeType());
					 addMap.put("aNeDisplayName", neAMap.get("neDisplayName"));
					 addMap.put("aNeId",neAMap.get("neId"));
					 addMap.put("aPtp", neAMap.get("ptpDesc"));
					 addMap.put("zNeDisplayName", neZMap.get("neDisplayName"));
					 addMap.put("zNeId",neZMap.get("neId"));
					 addMap.put("zPtp", neZMap.get("ptpDesc"));
					 addMap.put("linkDisplayName", model.getLinkName()+"");
					 addMap.put("isManual", model.getLink().get("IS_MANUAL")==null?false:model.getLink().get("IS_MANUAL"));
					 if(model.getConflictList()!=null&&!model.getConflictList().isEmpty()){
						 for(Map conflict:model.getConflictList()){
							 neAMap = connectionManagerMapper.getLinkByChangeInfo(
									 (Integer)conflict.get("A_END_PTP"));
							 neZMap = connectionManagerMapper.getLinkByChangeInfo(
									 (Integer)conflict.get("Z_END_PTP"));
							 conflict.put("aNeDisplayName", neAMap.get("neDisplayName").toString());
							 conflict.put("aNeId",neAMap.get("neId").toString());
							 conflict.put("aPtp", neAMap.get("ptpDesc").toString());
							 conflict.put("zNeDisplayName", neZMap.get("neDisplayName").toString());
							 conflict.put("zNeId",neZMap.get("neId").toString());
							 conflict.put("zPtp", neZMap.get("ptpDesc").toString());
							 conflict.put("linkDisplayName", conflict.get("DISPLAY_NAME")+"");
							 conflict.put("isManual", conflict.get("IS_MANUAL")==null?false:conflict.get("IS_MANUAL"));
						 }
						 addMap.put("conflictList", model.getConflictList());
					 }
					linkList.add(addMap);
				}
				map.put("changeList", linkList);
			}
		}

		// 是否需要同步网元 false:不需要 true:需要
		if (returnModel.isNeedSyncNe()) {
			Map neMap = new HashMap();
			// 需要同步网元列表Id
			List<Integer> syncNeIdList = returnModel.getSyncNeList();
			List<Map> neList = new ArrayList<Map>();

			for (Integer neId : syncNeIdList) {
				// neMap.put("name", neSerialNo);
				neMap = connectionManagerMapper.getNeInfoByNeSerialNo(neId);
				Map addMap = new HashMap();
				addMap.put("emsConnectionId", neMap.get("emsConnectionId"));
				addMap.put("emsDisplayName", neMap.get("emsDisplayName"));
				addMap.put("neDisplayName", neMap.get("neDisplayName"));
				addMap.put("suportRates", neMap.get("suportRates"));
				addMap.put("neSerialNo", neMap.get("neSerialNo"));
				addMap.put("neId", neMap.get("neId"));
				neList.add(addMap);

			}
			map.put("syncNeList", neList);
		}
		/*// 是否有变化 false:无变化 true:有变化
		if (returnModel.isChanged() == true) {
			Map neMap = new HashMap();
			Map neAMap = new HashMap();
			Map neZMap = new HashMap();

			// 是否需要同步网元 false:不需要 true:需要
			if (returnModel.isNeedSyncNe() == true) {
				// 需要同步网元列表Id
				List<Integer> syncNeIdList = returnModel.getSyncNeList();
				List<Map> neList = new ArrayList<Map>();

				for (Integer neId : syncNeIdList) {
					// neMap.put("name", neSerialNo);
					neMap = connectionManagerMapper.getNeInfoByNeSerialNo(neId);
					Map addMap = new HashMap();
					addMap.put("emsConnectionId", neMap.get("emsConnectionId"));
					addMap.put("emsDisplayName", neMap.get("emsDisplayName"));
					addMap.put("neDisplayName", neMap.get("neDisplayName"));
					addMap.put("suportRates", neMap.get("suportRates"));
					addMap.put("neSerialNo", neMap.get("neSerialNo"));
					addMap.put("neId", neMap.get("neId"));
					neList.add(addMap);

				}
				map.put("isChanged", returnModel.isChanged());
				map.put("isNeedSyncNe", returnModel.isNeedSyncNe());
				map.put("rows", neList);
				map.put("total", neList.size());

			} else {
				List<Map> linkList = new ArrayList<Map>();

				// 变化列表
				List<LinkAlterModel> changeList = returnModel.getChangeList();
				if (changeList != null) {
					for (LinkAlterModel model : changeList) {
						if(model.getLink()==null) continue;
						//不列出无冲突变更
						if(model.getConflictList()==null||model.getConflictList().isEmpty()){
							continue;
						}
						// neMap.put("name", neSerialNo);
						Map addMap = new HashMap();
						addMap.put("linkAlterModel", model);
						neAMap = connectionManagerMapper.getLinkByChangeInfo(
								model.getaEndPtp());
						neZMap = connectionManagerMapper.getLinkByChangeInfo(
								model.getzEndPtp());
						 //1.新增 2.删除
						 addMap.put("changeType", model.getChangeType());
						 addMap.put("aNeDisplayName", neAMap.get("neDisplayName"));
						 addMap.put("aNeId",neAMap.get("neId"));
						 addMap.put("aPtp", neAMap.get("ptpDesc"));
						 addMap.put("zNeDisplayName", neZMap.get("neDisplayName"));
						 addMap.put("zNeId",neZMap.get("neId"));
						 addMap.put("zPtp", neZMap.get("ptpDesc"));
						 addMap.put("linkDisplayName", model.getLinkName()+"");
						 addMap.put("isManual", model.getLink().get("IS_MANUAL")==null?false:model.getLink().get("IS_MANUAL"));
						 if(model.getConflictList()!=null&&!model.getConflictList().isEmpty()){
							 for(Map conflict:model.getConflictList()){
								 neAMap = connectionManagerMapper.getLinkByChangeInfo(
										 (Integer)conflict.get("A_END_PTP"));
								 neZMap = connectionManagerMapper.getLinkByChangeInfo(
										 (Integer)conflict.get("Z_END_PTP"));
								 conflict.put("aNeDisplayName", neAMap.get("neDisplayName").toString());
								 conflict.put("aNeId",neAMap.get("neId").toString());
								 conflict.put("aPtp", neAMap.get("ptpDesc").toString());
								 conflict.put("zNeDisplayName", neZMap.get("neDisplayName").toString());
								 conflict.put("zNeId",neZMap.get("neId").toString());
								 conflict.put("zPtp", neZMap.get("ptpDesc").toString());
								 conflict.put("linkDisplayName", conflict.get("DISPLAY_NAME")+"");
								 conflict.put("isManual", conflict.get("IS_MANUAL")==null?false:conflict.get("IS_MANUAL"));
							 }
							 addMap.put("conflictList", model.getConflictList());
						 }
						linkList.add(addMap);
					}
					map.put("isChanged", returnModel.isChanged());
					map.put("isNeedSyncNe", returnModel.isNeedSyncNe());
					map.put("rows", linkList);
					map.put("total", linkList.size());
				}
			}
		} else {
			map.put("isChanged", returnModel.isChanged());
			map.put("isNeedSyncNe", returnModel.isNeedSyncNe());
			map.put("returnResult", CommonDefine.SUCCESS);
			map.put("returnMessage", MessageHandler
					.getErrorMessage(MessageCodeDefine.LINK_SYNC_NO_CHANGE));
		}*/
		return map;
	}

	@Override
	public void topoLinkSyncChangeList(Integer emsConnectionId,
			Integer collectLevel, List<LinkAlterModel> changeList, Integer taskId) throws CommonException {
//		Map map = new HashMap();

		//初始化同步结果
//		int syncResult = CommonDefine.LINK_SYNC_HAD;
		//初始化异常消息
//		String message="";

		try {
			dataCollectService = SpringContextUtil
					.getDataCollectServiceProxy(emsConnectionId);
			/*if(changeList==null){
				changeList=new ArrayList<LinkAlterModel>();
				List<LinkAlterModel> tmpList=dataCollectService.getLinkAlterList(collectLevel).getChangeList();
				//去除有冲突的
				if(tmpList!=null){
				for(LinkAlterModel tmp:tmpList){
					if(tmp.getChangeType()==CommonDefine.CHANGE_TYPE_DELETE||
						tmp.getConflictList()==null||
						tmp.getConflictList().isEmpty()){
						changeList.add(tmp);
					}
				}
			}
			}*/
			if(changeList!=null&&!changeList.isEmpty())
			dataCollectService.syncLink(changeList,collectLevel);
			// if (collectLevel == CommonDefine.COLLECT_LEVEL_1) {
			// } else if (collectLevel == CommonDefine.COLLECT_LEVEL_4) {
//			connectionManagerMapper.updateTaskDetailInfo(taskId,
//					emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS, null,
//					CommonDefine.SUCCESS, "完成");
			// }
		} catch (CommonException e) {
			// if (collectLevel == CommonDefine.COLLECT_LEVEL_1) {
//			syncResult = CommonDefine.LINK_SYNC_FAILED;
//			message = e.getErrorMessage();
			// } else if (collectLevel == CommonDefine.COLLECT_LEVEL_4) {
//			connectionManagerMapper.updateTaskDetailInfo(taskId,
//					emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS, null,
//					null, "采集失败（" + e.getErrorMessage() + ")");
			// }
			throw e;
		}
//		//更新同步结果
//		connectionManagerMapper.updateEmsLinkSyncInfo(emsConnectionId,
//				syncResult, message);
	}

	@Override
	public Map getEmsConnectionSyncInfo(int userId,int start, int limit, Integer emsGroupId)
			throws CommonException {

		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> connectionList = new ArrayList<Map>();
		
		connectionList = commonManagerService.getAllEmsByEmsGroupId(userId, emsGroupId, false, true);
		int total = 0;
		// map.put("emsGroupId", emsGroupId == 0 ? null : emsGroupId);
		// map.put("emsConnectionId", emsConnectionId);
		if (connectionList.size() > CommonDefine.FALSE) {
			List<Integer> emsIdList = new ArrayList<Integer>();
			
			for (Map emsConnection : connectionList) {
				emsIdList.add(Integer
						.parseInt(emsConnection.get("BASE_EMS_CONNECTION_ID").toString()));
			}

			map.put("emsIdList", emsIdList);
			map.put("start", start);
			map.put("limit", limit);
			total = connectionManagerMapper.getEmsConnectionSyncCount(map);
			connectionList = connectionManagerMapper
					.getEmsConnectionSyncInfo(map);
			returnMap.put("rows", connectionList);
			returnMap.put("total", total);
		} else {
			List<Map> nullList = new ArrayList<Map>();
			returnMap.put("rows", nullList);
			returnMap.put("total", total);
		}

		return returnMap;
	}

	/**
	 * 网管同步管理页面 启动功能
	 */
	@Override
	public void startTask(Map map) throws CommonException {

		Map mapParam = new HashMap();
		mapParam.put("taskId", map.get("taskId").toString());
		try {
			String nextTime = calculateDate(map.get("period").toString(), map
					.get("periodType").toString());
			mapParam.put("nextTime", nextTime);

			connectionManagerMapper.startTask(mapParam);
			// 添加一个quartz任务
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_BASE,
					Integer.parseInt(map.get("taskId").toString()),
					CommonDefine.QUARTZ.JOB_RESUME);
		} catch (ParseException e) {
			throw new CommonException(e, MessageCodeDefine.PARSE_EXCPT);
		}
	}

	/**
	 * 网管同步管理页面 挂起功能
	 */
	@Override
	public void disTask(Map map) throws CommonException {
		Map mapParam = new HashMap();
		mapParam.put("emsConnectionId", map.get("emsConnectionId").toString());
		mapParam.put("taskId", map.get("taskId").toString());
		mapParam.put("taskStatus", map.get("taskStatus").toString());
		mapParam.put("executeStatus", map.get("executeStatus").toString());
		connectionManagerMapper.disTask(mapParam);
		// 添加一个quartz任务
		quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_BASE,
				Integer.parseInt(map.get("taskId").toString()),
				CommonDefine.QUARTZ.JOB_PAUSE);
	}

	/**
	 * 网管同步管理页面中的 手动同步操作
	 */
	@Override
	public void manualSyncEms(Integer taskId, Integer emsConnectionId)
			throws CommonException, Exception {
		
//		List<Map> neList = new ArrayList<Map>();
//		Map reMap = new HashMap();
//		// 获取该网管下所有网元列表
//		neList = connectionManagerMapper
//				.getAllNeListByEmsConnnectionId(emsConnectionId);
////		for (Map m : neList) {
//		for (int i = neList.size() - 1 ; i >= 0; i--) {
//			Map ne = neList.get(i);
//			String str = ne.get("suportRates").toString();
//			short[] layerRateList = constructLayRates(str);
//			Integer neId = Integer.valueOf(ne.get("neId").toString());
//			Integer collectLevel = CommonDefine.COLLECT_LEVEL_1;
//
//			syncEmsconnectionNe(emsConnectionId, neId,neList,taskId, layerRateList,
//					collectLevel);
//			neList.remove(ne);
//		}
		quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_BASE,
				taskId,CommonDefine.QUARTZ.JOB_ACTIVATE);
	}
	
	@Override
	public Map setBeginTime(Map map) throws CommonException {
		Map mapp = new HashMap();
		try {

			String next = calculateDate(map.get("PERIOD").toString(), map.get(
					"PERIOD_TYPE").toString());
			mapp.put("NEXT_TIME", next);
			mapp.put("returnResult", CommonDefine.SUCCESS);
		} catch (ParseException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_PARSE);
		}
		return mapp;

	}

	/**
	 * 计算下次开始时间
	 * 
	 * @throws ParseException
	 */
	public String calculateDate(String time, String cycle)
			throws ParseException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// time 格式 年(1)，季(1)，月(2)，周(3)，日(4)，时间(5)
		String[] date_time = time.split(",");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 每周执行
		if ("2".equals(cycle)) {

			// 周日 1 ，周一 2， 周三 3 。。。 与现实规则不一样，需要转换
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			if (dayOfWeek == 1) {
				// 周日+7 变成8
				dayOfWeek += 7;
			}

			// 取出计划中是周几执行
			int plan = Integer.parseInt(date_time[3]);
			if (plan == 1) {
				plan += 7;
			}

			// 表示本周还可以执行，不需要到下一周
			if (plan > dayOfWeek) {
				calendar.add(calendar.DATE, plan - dayOfWeek);

			} else if (plan == dayOfWeek) {
				// 需要判断时间
				String[] ff = date_time[5].split(":");
				int plan_hour = Integer.parseInt(ff[0]) * 60 * 60
						+ Integer.parseInt(ff[1]) * 60;
//						+ Integer.parseInt(ff[2]);
				int current_hour = calendar.get(calendar.HOUR_OF_DAY) * 3600
						+ calendar.get(calendar.MINUTE) * 60
						+ calendar.get(calendar.SECOND);
				if (plan_hour > current_hour) {

				} else {
					// 下周执行
					calendar.add(calendar.DATE, plan + 7 - dayOfWeek);

				}
			} else {
				calendar.add(calendar.DATE, plan + 7 - dayOfWeek);

			}

		} else if ("3".equals(cycle)) {// 每月执行
			int plan_day = Integer.parseInt(date_time[4]);
			int current_day = calendar.get(calendar.DAY_OF_MONTH);

			if (plan_day > current_day) {
				calendar.add(calendar.DATE, plan_day - current_day);
			} else if (plan_day == current_day) {
				// 需要判断时间
				String[] ff = date_time[5].split(":");
				int plan_hour = Integer.parseInt(ff[0]) * 60 * 60
						+ Integer.parseInt(ff[1]) * 60;
//						+ Integer.parseInt(ff[2]);
				int current_hour = calendar.get(calendar.HOUR_OF_DAY) * 3600
						+ calendar.get(calendar.MINUTE) * 60
						+ calendar.get(calendar.SECOND);
				if (plan_hour > current_hour) {

				} else {
					// 下月执行
					calendar.add(Calendar.MONTH, 1);

				}
			} else {
				calendar.add(Calendar.MONTH, 1);
				calendar
						.set(Calendar.DATE, calendar.get(Calendar.DAY_OF_MONTH));
				calendar.set(Calendar.DATE, plan_day);
			}
		} else if ("4".equals(cycle)) {// 每季执行
			int plan_month = Integer.parseInt(date_time[2]);
			int plan_day = Integer.parseInt(date_time[4]);
			int current_month = calendar.get(calendar.MONTH) + 1;
			int current_day = calendar.get(calendar.DAY_OF_MONTH);
			int times = current_month / 3;
			int remainder = current_month % 3;
			if (remainder == 0) {
				if (plan_month == 3) {
					if (plan_day > current_day) {
						calendar.set(Calendar.MONTH, current_month - 1);
						calendar.add(calendar.DATE, plan_day - current_day);
					} else if (plan_day == current_day) {
						// 需要判断时间
						String[] ff = date_time[5].split(":");
						int plan_hour = Integer.parseInt(ff[0]) * 60 * 60
								+ Integer.parseInt(ff[1]) * 60;
//								+ Integer.parseInt(ff[2]);
						int current_hour = calendar.get(calendar.HOUR_OF_DAY)
								* 3600 + calendar.get(calendar.MINUTE) * 60
								+ calendar.get(calendar.SECOND);
						if (plan_hour > current_hour) {

						} else {
							// 下季度执行
							calendar.add(Calendar.MONTH, 3);
						}
					} else {
						calendar.add(Calendar.MONTH, 3);
						calendar.set(Calendar.DATE, plan_day);
					}

				} else if (plan_month == 2) {
					calendar
							.set(Calendar.MONTH, current_month + plan_month - 1);
					calendar
							.set(Calendar.MONTH, current_month + plan_month - 1);
					calendar.set(Calendar.DATE, plan_day);
				} else if (plan_month == 1) {
					calendar.set(Calendar.MONTH, current_month + 1 - 1);
					calendar.set(Calendar.DATE, plan_day);
				}
			} else if (remainder == 1) {
				if (plan_month == 1) {
					if (plan_day > current_day) {
						calendar.set(Calendar.MONTH, current_month - 1);
						calendar.add(calendar.DATE, plan_day - current_day);
					} else if (plan_day == current_day) {
						// 需要判断时间
						String[] ff = date_time[5].split(":");
						int plan_hour = Integer.parseInt(ff[0]) * 60 * 60
								+ Integer.parseInt(ff[1]) * 60;
//								+ Integer.parseInt(ff[2]);
						int current_hour = calendar.get(calendar.HOUR_OF_DAY)
								* 3600 + calendar.get(calendar.MINUTE) * 60
								+ calendar.get(calendar.SECOND);
						if (plan_hour > current_hour) {

						} else {
							// 下季度执行
							calendar.add(Calendar.MONTH, 3);
						}
					} else {
						calendar.add(Calendar.MONTH, 3);
						calendar.set(Calendar.DATE, plan_day);
					}
				} else if (plan_month == 2) {
					calendar.set(Calendar.MONTH, current_month + 1 - 1);
					calendar.set(Calendar.DATE, plan_day);
				} else if (plan_month == 3) {
					calendar.set(Calendar.MONTH, current_month + 2 - 1);
					calendar.set(Calendar.DATE, plan_day);
				}
			} else if (remainder == 2) {
				if (plan_month == 2) {
					if (plan_day > current_day) {
						calendar.set(Calendar.MONTH, current_month - 1);
						calendar.add(calendar.DATE, plan_day - current_day);
					} else if (plan_day == current_day) {
						// 需要判断时间
						String[] ff = date_time[5].split(":");
						int plan_hour = Integer.parseInt(ff[0]) * 60 * 60
								+ Integer.parseInt(ff[1]) * 60;
//								+ Integer.parseInt(ff[2]);
						int current_hour = calendar.get(calendar.HOUR_OF_DAY)
								* 3600 + calendar.get(calendar.MINUTE) * 60
								+ calendar.get(calendar.SECOND);
						if (plan_hour > current_hour) {

						} else {
							// 下季度执行
							calendar.add(Calendar.MONTH, 3);
						}
					} else {
						calendar.add(Calendar.MONTH, 3);
						calendar.set(Calendar.DATE, plan_day);
					}
				} else if (plan_month == 1) {
					calendar.set(Calendar.MONTH, current_month + 2 - 1);
					calendar.set(Calendar.DATE, plan_day);
				} else if (plan_month == 3) {
					calendar.set(Calendar.MONTH, current_month + 1 - 1);
					calendar.set(Calendar.DATE, plan_day);
				}
			}
		}
		time = sdf.format(calendar.getTime());
		// 转成日期格式
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss");
		// Date ti = formatter.parse(time + " " + date_time[5]);
		String re_t = time + " " + date_time[5];

		return re_t;
	}
	
//	/**
//	 * 网元同步操作
//	 * 
//	 * @param emsConnectionId
//	 * @param neSerialNo
//	 * @throws CommonException
//	 */
//	private boolean syncNeBasicInfo(NeModel neModel,Integer collectLevel)
//			throws CommonException {
//		//初始化异常消息
//		String message="";
//		String text ;
//		try {
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步设备信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			// 网元基础信息同步 最基础的部分，如果异常更新网元同步结果信息，结束本次同步操作
//			dataCollectService.syncNeEquipmentOrHolder(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			connectionManagerMapper.updateNeBasicSyncInfo(neModel.getNeId(),
//					CommonDefine.NE_SYNC_FAILED, e.getErrorMessage());
//			return false;
//		}
//		try{
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步端口信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			dataCollectService.syncNePtp(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			connectionManagerMapper.updateNeBasicSyncInfo(neModel.getNeId(),
//					CommonDefine.NE_SYNC_FAILED, e.getErrorMessage());
//			return false;
//		}
//		try{
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步时隙信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			dataCollectService.syncNeCtp(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			connectionManagerMapper.updateNeBasicSyncInfo(neModel.getNeId(),
//					CommonDefine.NE_SYNC_FAILED, e.getErrorMessage());
//			return false;
//		} 
//		return true;
//	}
	
	/**
	 * 网管同步任务操作失败时，更新网元表以及任务执行详细表
	 * @param neList
	 * @param taskId
	 * @param message
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void updateEmsSyncTaskStatus(List<Map> neList, Integer taskId,
			String neMessage, String taskMessage, Integer runResult,
			Integer neFlag, Integer flag) {
		Integer emsConnectionId = null;
		for (Map ne : neList) {
			int neId = Integer.parseInt(ne.get("BASE_NE_ID").toString());
			emsConnectionId = Integer.parseInt(ne.get("BASE_EMS_CONNECTION_ID")
					.toString());
			if (flag == CommonDefine.FALSE) {
				connectionManagerMapper.updateNeBasicSyncInfo(neId,
						CommonDefine.NE_SYNC_FAILED, neMessage);
//				connectionManagerMapper.updateNeMstpSyncInfo(neId,
//						CommonDefine.NE_SYNC_FAILED, neMessage);
//				connectionManagerMapper.updateNeCRSInfo(neId,
//						CommonDefine.NE_SYNC_FAILED, neMessage);
			}
			// if (collectLevel == CommonDefine.COLLECT_LEVEL_4) {

			connectionManagerMapper.updateTaskDetailInfo(taskId, neId,
					CommonDefine.TASK_TARGET_TYPE.NE,
					null, runResult, taskMessage);
//			connectionManagerMapper.updateTaskDetailInfo(taskId, neId,
//					CommonDefine.TASK_TARGET_TYPE.NE,
//					CommonDefine.NE_MSTP_SYNC, runResult, taskMessage);
//			connectionManagerMapper.updateTaskDetailInfo(taskId, neId,
//					CommonDefine.TASK_TARGET_TYPE.NE,
//					CommonDefine.NE_CROSS_SYNC, runResult, taskMessage);
			// }
		}
		if (neFlag == CommonDefine.FALSE) {
			connectionManagerMapper.updateTaskDetailInfo(taskId,
					emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS, null,
					runResult, taskMessage);
		}
	}

//	/**
//	 * 网管同步操作
//	 * 
//	 * @param emsConnectionId
//	 * @param neSerialNo
//	 * @param layerRateList
//	 * @throws CommonException
//	 */
//	public void syncEmsconnectionNe(Integer emsConnectionId, Integer neId,
//			List<Map> neList, Integer taskId, short[] layerRateList,
//			Integer collectLevel) throws CommonException {
//		String neMessage = "";
//		String taskMessage = "";
//		int syncResult = CommonDefine.NE_SYNC_HAD;
//		Integer runResult = null;
//		
//		
//		
//		//------------------------------- 同步网元基础数据 equip,ptp,ctp --------------------------------
//		try {
//			dataCollectService = SpringContextUtil
//					.getDataCollectServiceProxy(emsConnectionId);
//
//			// 网元基础信息同步 最基础的部分，如果异常更新网元同步结果信息，结束本次同步操作
//			dataCollectService.syncNeEquipmentOrHolder(neId, collectLevel);
//			dataCollectService.syncNePtp(neId, collectLevel);
////			dataCollectService.syncNeCtp(neId, collectLevel);
//		} catch (CommonException e) {
//			List<Map> neIdList = new ArrayList<Map>();
//			Map map = new HashMap();
//			map.put("neId", neId);
//			map.put("emsConnectionId", emsConnectionId);
//			neIdList.add(map);
//			
//			if (collectLevel == CommonDefine.COLLECT_LEVEL_1) {
//				neMessage = e.getErrorMessage();
//			}
//			if (collectLevel == CommonDefine.COLLECT_LEVEL_4) {
//				taskMessage = "采集失败（" + e.getErrorMessage() + ")";
//				runResult = CommonDefine.FALSE;
//			}
//			updateEmsSyncTaskStatus(neIdList, taskId, neMessage, taskMessage,
//					runResult, CommonDefine.TRUE,CommonDefine.FALSE);
//			return;
//		}
//		//-------------------- 同步网元基础数据 盘保护，端口保护，wdm端口保护，时钟， 内部link--------------
//		try{
//			//初始化状态
//			runResult = CommonDefine.SUCCESS;
//			syncResult = CommonDefine.NE_SYNC_HAD;
//			neMessage = "";
//			try {
//				//同步盘保护信息
//				dataCollectService.syncNeEProtectionGroup(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//			}
//			try {
//				//同步端口保护
//				dataCollectService.syncNeProtectionGroup(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//			}
//			try {
//				//同步wdm端口保护
//				dataCollectService.syncNeWDMProtectionGroup(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//			}
//			try {
//				//同步时钟信息
//				dataCollectService.syncNeClock(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//			}
//			try {
//				//同步内部link
//				dataCollectService.syncNeInternalLink(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//			}
//		}catch(Exception e){
//			runResult = CommonDefine.FAILED;
//			syncResult = CommonDefine.NE_SYNC_FAILED;
//			neMessage = "未知错误！";
//		}finally{
//			//更新同步状态
//			connectionManagerMapper.updateNeBasicSyncInfo(neId,
//					syncResult, neMessage);
//			if(runResult == CommonDefine.FAILED){
//				neMessage = "采集失败（" + neMessage + ")";
//			}else{
//				neMessage = "完成";
//			}
//			connectionManagerMapper.updateTaskDetailInfo(taskId, neId,
//					CommonDefine.TASK_TARGET_TYPE.NE, CommonDefine.NE_BASIC_SYNC,
//					runResult, neMessage);
//		}
//		//-------------------- 网元以太网同步操作 --------------
//		try{
//			//初始化状态
//			runResult = CommonDefine.SUCCESS;
//			syncResult = CommonDefine.NE_SYNC_HAD;
//			neMessage = "";
//			try {
//				dataCollectService.syncNeEthService(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//				syncResult = CommonDefine.NE_SYNC_FAILED;
//			}
//			try {
//				dataCollectService.syncNeBindingPath(neId, collectLevel);
//			} catch (CommonException e) {
//				neMessage = e.getErrorMessage();
//				syncResult = CommonDefine.NE_SYNC_FAILED;
//			}
//		}catch(Exception e){
//			runResult = CommonDefine.FAILED;
//			syncResult = CommonDefine.NE_SYNC_FAILED;
//			neMessage = "未知错误！";
//		}finally{
//			connectionManagerMapper.updateNeMstpSyncInfo(neId,
//					syncResult, neMessage);
//			if(runResult == CommonDefine.FAILED){
//				neMessage = "采集失败（" + neMessage + ")";
//			}else{
//				neMessage = "完成";
//			}
//			connectionManagerMapper.updateTaskDetailInfo(taskId, neId,
//					CommonDefine.TASK_TARGET_TYPE.NE, CommonDefine.NE_MSTP_SYNC,
//					runResult, neMessage);
//		}
//		
//
//		//--------------------网元交叉连接同步操作 --------------
//		try {
//			//初始化状态
//			runResult = CommonDefine.SUCCESS;
//			syncResult = CommonDefine.NE_SYNC_HAD;
//			neMessage = "";
//			dataCollectService.syncNeCRS(neId, layerRateList, collectLevel);
//		} catch (CommonException e) {
//			runResult = CommonDefine.FAILED;
//			syncResult = CommonDefine.NE_SYNC_FAILED;
//			neMessage = e.getErrorMessage();
//		}catch(Exception e){
//			runResult = CommonDefine.FAILED;
//			syncResult = CommonDefine.NE_SYNC_FAILED;
//			neMessage = "未知错误！";
//		}finally{
//			connectionManagerMapper.updateNeCRSInfo(neId, syncResult,
//					neMessage);
//			if(runResult == CommonDefine.FAILED){
//				neMessage = "采集失败（" + neMessage + ")";
//			}else{
//				neMessage = "完成";
//			}
//			connectionManagerMapper.updateTaskDetailInfo(taskId, neId,
//					CommonDefine.TASK_TARGET_TYPE.NE,
//					CommonDefine.NE_CROSS_SYNC, runResult,
//					neMessage);
//		}
//	}
	
//	/**
//	 * 网元基础信息同步
//	 * @param emsConnectionId
//	 * @param neId
//	 * @param taskId
//	 * @param layerRateList
//	 * @param collectLevel
//	 * @throws CommonException
//	 */
//	private void syncNeExtendInfo(NeModel neModel,
//			Integer collectLevel) throws CommonException {
//		String message = "";
//		String text;
//		try {
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步板卡保护信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			// 网元最基础信息同步
//			dataCollectService.syncNeEProtectionGroup(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			message = e.getErrorMessage();
//			System.out.print(e.getErrorMessage());
//		}
//		try {
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步环保护信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			dataCollectService.syncNeProtectionGroup(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			message = e.getErrorMessage();
//			System.out.print(e.getErrorMessage());
//		}
//		try {
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步WDM环保护信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			dataCollectService.syncNeWDMProtectionGroup(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			message = e.getErrorMessage();
//			System.out.print(e.getErrorMessage());
//		}
//		try {
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步时钟信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			dataCollectService.syncNeClock(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			message = e.getErrorMessage();
//			System.out.print(e.getErrorMessage());
//		}
//		try {
//			//进度描述信息更改--此处修改
//			text = "正在进行：    "+neModel.getDisplayName()+"    正在同步内部link信息";
//			//加入进度值
//			CommonDefine.setProcessParameter(getSessionId(), neModel.getSyncName(), null,null,text);
//			dataCollectService.syncNeInternalLink(neModel.getNeId(), collectLevel);
//		} catch (CommonException e) {
//			message = e.getErrorMessage();
//			System.out.print(e.getErrorMessage());
//		}
//		connectionManagerMapper.updateNeBasicSyncInfo(neModel.getNeId(),
//				CommonDefine.NE_SYNC_HAD, message);
//		
//	}

	@Override
	public void setCycle(Map map) throws CommonException {
		// 判断时间
		String cronExpression = "";
		int taskType = 12;
		String[] time = map.get("PERIOD").toString().split(",");
		if (time != null && time.length >= 6) {
			String[] hms = time[5].split(":");
			if (hms != null && hms.length == 2) {
//				cronExpression = hms[2] + " " + hms[1] + " " + hms[0] + " ";
//			} else {
				cronExpression = "0 " + hms[1] + " " + hms[0] + " ";
			}
		} else {
			cronExpression = "0 15 10 ";
		}
		// int month = 0;
		// Calendar c=Calendar.getInstance();
		// //获得系统当前日期
		// month = c.get(Calendar.MONTH)+1;
		// for (int i = 1; i<=4 ;i++){
		// if(month < (i*3)){
		//
		// }
		// }
		// 判断执行周期
		if ("1".equals(map.get("PERIOD_TYPE").toString() )) {
			// 每日执行
			cronExpression += "* * ?";
		} else if ("2".equals(map.get("PERIOD_TYPE").toString())) {
			// 每周执行
			cronExpression += "? * " + time[3];
		} else if ("4".equals(map.get("PERIOD_TYPE").toString() )) {
			// 每季执行
			cronExpression += time[4] + " " + time[2] + "/3" + " ?";
		} else {
			cronExpression += time[4] + " * ?";
			// 每月执行
		}
		// 修改quartz的时间
		// "0 15 10 * * ?" Fire at 10:15am every day
		// 添加一个quartz任务
		System.out.println("cronExpression==" + cronExpression);
		quartzManagerService.modifyJobTime(taskType,
				Integer.parseInt(map.get("SYS_TASK_ID").toString()),
				cronExpression);

		connectionManagerMapper.updateTask(map);

	}

	@Override
	public Map getTaskDetailInfo(Integer emsConnectionId)
			throws CommonException {
		List<Map> recordList = new ArrayList<Map>();
		Map returnMap = new HashMap();
		recordList = connectionManagerMapper.getTaskDetailInfo(emsConnectionId);
		returnMap.put("rows", recordList);
		returnMap.put("total", recordList.size());

		return returnMap;
	}

	@Override
	public Map getSysServiceRecord(int start, int limit, Integer sysSvcRecordId)
			throws CommonException {
		Map map = new HashMap();
		Map returnMap = new HashMap();
		List<Map> serviceRecordList = new ArrayList<Map>();
		map.put("sysSvcRecordId", sysSvcRecordId);
		int total = connectionManagerMapper.getSysServiceRecordCount(map);
		map.put("start", start);
		map.put("limit", limit);

		serviceRecordList = connectionManagerMapper
				.getSysServiceRecordInfo(map);
		returnMap.put("rows", serviceRecordList);
		returnMap.put("total", total);

		return returnMap;
	}

	@Override
	public void addSysService(SysServiceModel sysServiceModel)
			throws CommonException {
		StringBuffer address = new StringBuffer();
		address.append("rmi://");
		address.append(sysServiceModel.getIp());
		address.append(":");
		address.append(sysServiceModel.getPort());
		address.append("/dataCollectService");

		// 定义一个map类型变量用来存储查询条件
		Map map = new HashMap();
		// 给map赋值
		map.put("serviceName", sysServiceModel.getServiceName());
		map.put("note", sysServiceModel.getNote());
		map.put("port", sysServiceModel.getPort());
		map.put("address", address.toString());

		// map.put("address", "'"+address+"'");
		map.put("ip", sysServiceModel.getIp());

		connectionManagerMapper.addSysService(map);
	}

	@Override
	public void deleteSysService(Integer sysSvcRecordId) throws CommonException {
		Map map = new HashMap();
		map.put("sysSvcRecordId", sysSvcRecordId);

		connectionManagerMapper.deleteSysService(map);

	}

	/**
	 * 修改接入服务器
	 */
	@Override
	public void modifySysService(SysServiceModel sysServiceModel)
			throws CommonException {
		StringBuffer address = new StringBuffer();
		address.append("rmi://");
		address.append(sysServiceModel.getIp());
		address.append(":");
		address.append(sysServiceModel.getPort());
		address.append("/dataCollectService");

		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("sysSvcRecordId", sysServiceModel.getSysSvcRecordId());
		map.put("serviceName", sysServiceModel.getServiceName());
		map.put("ip", sysServiceModel.getIp());
		map.put("port", sysServiceModel.getPort());
		map.put("address", address.toString());
		map.put("status", sysServiceModel.getStatus());
		map.put("note", sysServiceModel.getNote());

		connectionManagerMapper.modifySysService(map);
	}

	/**
	 * 
	 */
	public boolean getSysServiceBySvcInfo(SysServiceModel sysServiceModel)
			throws CommonException {
		Boolean exit = true;
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new HashMap<String, Object>();
		map.put("sysSvcRecordId", sysServiceModel.getSysSvcRecordId());
		map.put("ip", sysServiceModel.getIp());
		map.put("port", sysServiceModel.getPort());
		map.put("serviceName", sysServiceModel.getServiceName());

		returnMap = connectionManagerMapper.getSysServiceBySvcInfo(map);

		if (returnMap == null || returnMap.size() == 0) {
			exit = false;
		} else {	
			if (returnMap.get("SYS_SVC_RECORD_ID").toString().equals(sysServiceModel.getSysSvcRecordId().toString())) {
				exit = false;
			}
		}
		return exit;
	}
	
	@Override
	public boolean getSysServiceBySvcIpAddress(SysServiceModel sysServiceModel)
			throws CommonException {
		Boolean exit = true;
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new HashMap<String, Object>();
		map.put("sysSvcRecordId", sysServiceModel.getSysSvcRecordId());
		map.put("ip", sysServiceModel.getIp());
		map.put("port", sysServiceModel.getPort());
		map.put("serviceName", sysServiceModel.getServiceName());

		returnMap = connectionManagerMapper.getSysServiceBySvcIpAddress(map);

		if (returnMap == null || returnMap.size() == 0) {
			exit = false;
		} else {	
			if (returnMap.get("SYS_SVC_RECORD_ID").toString().equals(sysServiceModel.getSysSvcRecordId().toString())) {
				exit = false;
			}
		}
		return exit;
	}

	@Override
	public Map getSysServiceBySysSvcId(Integer sysSvcRecordId)
			throws CommonException {
		Map returnData = new HashMap();
		returnData = connectionManagerMapper
				.getSysServiceBySysSvcId(sysSvcRecordId);
		return returnData;
	}

	@Override
	public Boolean checkConnectionNameExist(
			EmsConnectionModel emsConnectionModel) throws CommonException {
		Boolean exit = true;
		// 需要保存的网管名称信息整合在map中
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("emsGroupId", emsConnectionModel.getEmsGroupId());
		map.put("emsDisplayName", emsConnectionModel.getEmsDisplayName());

		List<Map> returnList = connectionManagerMapper
				.checkConnectionNameExist(map);
		if (returnList.size() == 0) {
			exit = false;
		}

		return exit;
	}

	/**
	 * 添加、修改网管连接时，检查网管IP地址是否已经存在
	 * 
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	@Override
	public Boolean checkIpAddressExist(EmsConnectionModel emsConnectionModel)
			throws CommonException {
		Boolean exit = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", emsConnectionModel.getIp());
		List<Map> returnList = connectionManagerMapper.checkIpAddressExist(map);
		if (returnList.size() == 0) {
			exit = false;
		}

		return exit;
	}

	@Override
	public Boolean checkSubnetNameExist(SubnetModel subnetModel)
			throws CommonException {
		Boolean exit = true;
		// 需要保存的网管名称信息整合在map中
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("emsConnectionId", subnetModel.getEmsConnectionId());
		map.put("subnetName", subnetModel.getSubnetName());

		List<Map> returnList = connectionManagerMapper
				.checkSubnetNameExist(map);

		if (returnList.size() == 0) {
			exit = false;
		}

		return exit;
	}

	@Override
	public Boolean checkNameExist(EmsConnectionModel emsConnectionModel,
			SysServiceModel sysServiceModel, SubnetModel subnetModel,
			EmsGroupModel emsGroupModel) throws CommonException {
		Boolean exit = true;
		Map<String, Object> map = null;
		// 判断 南向连接 的网管名称 和 接入服务器名称 是否重复
		if (null == sysServiceModel.getServiceName()
				&& null == subnetModel.getSubnetName()
				&& null == emsGroupModel.getEmsGroupName()) {
			map = new HashMap<String, Object>();
			map.put("NAME", "t_base_ems_connection");
			map.put("IN_NAME", "base_ems_group_id");
			map.put("IN_VALUE", emsConnectionModel.getEmsGroupId());
			map.put("ID_NAME", "display_name");
			map.put("ID_VALUE", emsConnectionModel.getEmsDisplayName());
		} else if (null == emsConnectionModel.getEmsDisplayName()
				&& null == subnetModel.getSubnetName()
				&& null == emsGroupModel.getEmsGroupName()) {
			map = new HashMap<String, Object>();
			map.put("NAME", "t_sys_svc_record");
			map.put("IN_NAME", "SERVICE_NAME");
			map.put("IN_VALUE", sysServiceModel.getServiceName());
		} else if (null == sysServiceModel.getServiceName()
				&& null == emsConnectionModel.getEmsDisplayName()
				&& null == emsGroupModel.getEmsGroupName()) {
			map = new HashMap<String, Object>();
			map.put("NAME", "t_base_subnet");
			map.put("IN_NAME", "DISPLAY_NAME");
			map.put("IN_VALUE", subnetModel.getSubnetName());
			map.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
			map.put("ID_VALUE", subnetModel.getEmsConnectionId());
		} else if (null == sysServiceModel.getServiceName()
				&& null == emsConnectionModel.getEmsDisplayName()
				&& null == subnetModel.getSubnetName()) {
			map = new HashMap<String, Object>();
			map.put("NAME", "t_base_ems_group");
			map.put("IN_NAME", "GROUP_NAME");
			map.put("IN_VALUE", emsGroupModel.getEmsGroupName());
		}
		List<Map> returnList = connectionManagerMapper.getRecord(map);
		if (returnList.size() == 0) {
			exit = false;
		}
		return exit;
	}

	/**
	 * 根据网管分组名称查询网管分组
	 */
	@Override
	public boolean getEmsGroupByName(EmsGroupModel emsGroupModel)
			throws CommonException {
		Boolean exit = true;
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new  HashMap<String, Object>();
		map.put("emsGroupName", emsGroupModel.getEmsGroupName());
		map.put("emsGroupId", emsGroupModel.getEmsGroupId());

		returnMap = connectionManagerMapper.getEmsGroupByName(map);
		if (returnMap == null || returnMap.size() == 0) {
			exit = false;
		} else {	
			if (returnMap.get("emsGroupId").toString().equals(emsGroupModel.getEmsGroupId().toString())) {
				exit = false;
			}
		}
		return exit;
	}

	@Override
	public Map getConnectionListBySysServiceId(SysServiceModel sysServiceModel)
			throws CommonException {
		Map<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new HashMap();
		List<Map> connectionList = new ArrayList<Map>();
		map.put("NAME", "t_base_ems_connection");
		map.put("IN_NAME", "SVC_RECORD_ID");
		map.put("IN_VALUE", sysServiceModel.getSysSvcRecordId());

		List<Map> returnList = connectionManagerMapper.getRecord(map);
		
		List<Map> result = new ArrayList<Map> ();
		
		for(Map data:returnList){
			if(data.get("IS_DEL")!=null && CommonDefine.FALSE == Integer.valueOf(data.get("IS_DEL").toString())){
				result.add(data);
			}
		}
		returnMap.put("rows", result);
		returnMap.put("total", result.size());
		return returnMap;
	}

	@Override
	public boolean getConnectionByInfo(EmsConnectionModel emsConnectionModel)
			throws CommonException {		
		Boolean exit = true;
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new HashMap<String, Object>();
		map.put("emsConnectionId", emsConnectionModel.getEmsConnectionId());
		map.put("displayName", emsConnectionModel.getEmsDisplayName());
		map.put("emsGroupId", emsConnectionModel.getEmsGroupId()==-1?null:emsConnectionModel.getEmsGroupId());
		map.put("ip", emsConnectionModel.getIp());

		returnMap = connectionManagerMapper.getConnectionByInfo(map);
		if (returnMap == null || returnMap.size() == 0) {
			exit = false;
		} else {	
			if (returnMap.get("BASE_EMS_CONNECTION_ID").toString().equals(emsConnectionModel.getEmsConnectionId().toString())) {
				exit = false;
			}
		}
		return exit;
	}

	@Override
	public boolean getSubnetInfo(SubnetModel subnetModel)
			throws CommonException {
		Boolean exit = true;
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new  HashMap<String, Object>();
		map.put("subnetId", subnetModel.getSubnetId());
		map.put("emsConnectionId", subnetModel.getEmsConnectionId());
		map.put("subnetName", subnetModel.getSubnetName());

		returnMap = connectionManagerMapper.getSubnetInfo(map);
		if (returnMap == null || returnMap.size() == 0) {
			exit = false;
		} else {	
			if (returnMap != null&&subnetModel.getSubnetId()!=null){
				if(returnMap.get("BASE_SUBNET_ID").toString().equals(subnetModel.getSubnetId().toString())){
					exit = false;
				}
			}
		}
		return exit;
	}

	@Override
	public void proceedTaskSetting(Integer emsConnectionId, Integer taskId)
			throws CommonException {
		Map map = new HashMap();
		map.put("NAME", "t_sys_task_param");
		map.put("ID_NAME", "SYS_TASK_ID");
		map.put("ID_VALUE", taskId);
		connectionManagerMapper.deleteByParameter(map);

		connectionManagerMapper.updateTaskSetting(CommonDefine.EMS_SYNC_ING,taskId);	
	}

	@Override
	public void stopTaskSetting(Integer emsConnectionId, Integer taskId)
			throws CommonException {
		Map map = new HashMap();	
		map.put("NAME", "t_sys_task_param");
		map.put("ID_NAME", "SYS_TASK_ID");
		map.put("ID_VALUE", taskId);
		connectionManagerMapper.deleteByParameter(map);
		connectionManagerMapper.updateTaskSetting(CommonDefine.EMS_SYNC_INGSTOP,taskId);
	}
	
	// 导出文件
	public String exportExcel(Map map,int excelFlag) throws CommonException {
		String resultMessage = "";
		String name;
		int flag;
		
		if (excelFlag == CommonDefine.FALSE){
			name = "南向连接";
			flag = CommonDefine.EXCEL.SOUTH_CONNECTION_EXPORT;
		} else {
			name = "网元变更列表";
			flag = CommonDefine.EXCEL.NE_ALTER_LIST_EXPORT;
		}
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		String fileName = name + "_" + formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel export = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		try {
			Map result = map;
			if (result == null) {
				return resultMessage;
			}
			// 限制最多导出记录的个数
			if (map.get("limit") != null) {
				map.put("limit", CommonDefine.EXCEL.MAX_EXPORT_SIZE);
			}
			
			List<Map> rows = (List<Map>) result.get("rows");
			if(excelFlag == CommonDefine.TRUE) {
				for (Map data : rows) {
					if (data.get("changeType") != null) {
						if (!("".equals(data.get("changeType").toString()))) {
							if(Integer.parseInt(data
									.get("changeType").toString())== CommonDefine.CHANGE_TYPE_ADD){
								data.put("changeType", "新增");
							} else if (Integer.parseInt(data
									.get("changeType").toString())== CommonDefine.CHANGE_TYPE_DELETE){
								data.put("changeType", "删除");
							}
						}
					} 
				}
			}
			// 导出数据
			resultMessage = export.writeExcel(rows, flag, false);
		} catch (Exception e) {
			e.printStackTrace();
			export.close();
			return resultMessage;
		}
		return resultMessage;
	}

	
	public short[] constructLayRates(String layRatesString) {
		String[] composite = layRatesString.split(":");
		short[] layRate;
		if(composite.length>0){
			layRate = new short[composite.length];
			for (int i = 0; i < composite.length; i++) {
				layRate[i] = Short.valueOf(composite[i]);
			}
		}else{
			layRate = new short[]{};
		}
		return layRate;
	}

	@Override
	public Map getParamFromDataCollectionFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		Integer southConnectCount = 0;//南向连接
		Integer connectNormal = 0;//连接正常
		Integer connectDisconnect = 0;//连接中断
		Integer connectException = 0;//连接异常
		Integer connectInterrupt = 0;//网络中断
		List<Map> connectionList = new ArrayList<Map>();
		connectionList = commonManagerService.getAllEmsByEmsGroupId(userId,
				CommonDefine.VALUE_ALL, false, true);
		if (connectionList.size() > 0) {

			List<Integer> ids = new ArrayList<Integer>();

			for (Map ma : connectionList) {
				ids.add(Integer.parseInt(ma.get("BASE_EMS_CONNECTION_ID")
						.toString()));
			}
			Map map = new HashMap<String, Object>();
			map.put("ids", ids);
			connectionList = connectionManagerMapper
					.getConnectionInfoByConnectionStatus(map);
			Integer count = 0;

			for(Map m : connectionList){
				if(m.get("CONNECT_STATUS")==null){
					continue;
				}
				Integer connectStatus = Integer.parseInt(m.get("CONNECT_STATUS").toString());
				Integer connectCount = Integer.parseInt(m.get("connectCount").toString() ==""?"0":m.get("connectCount").toString());
				if( connectStatus == CommonDefine.CONNECT_STATUS_NORMAL_FLAG){
					connectNormal = connectCount;
				} else 	if( connectStatus == CommonDefine.CONNECT_STATUS_EXCEPTION_FLAG){
					 connectException = connectCount;
				} else if( connectStatus == CommonDefine.CONNECT_STATUS_INTERRUPT_FLAG){
					 connectInterrupt = connectCount;
				} else if( connectStatus == CommonDefine.CONNECT_STATUS_DISCONNECT_FLAG){
					 connectDisconnect = connectCount;
				}
			}
		}
		
		southConnectCount = connectNormal + connectDisconnect + connectException + connectInterrupt;
		return_result.put("southConnectCount", southConnectCount);
		return_result.put("connectNormal", connectNormal);
		return_result.put("connectDisconnect", connectDisconnect);
		return_result.put("connectException", connectException);
		return_result.put("connectInterrupt", connectInterrupt);
		
		return return_result;
	}
	
	@Override
	public boolean checkNeNameExist(NeModel neModel) throws CommonException {
		Boolean exit = true;
		// 定义map类型变量用来存储查询条件
		HashMap<String, Object> map = new HashMap<String, Object>();
		Map returnMap = new HashMap<String, Object>();
		map.put("emsConnectionId", neModel.getEmsConnectionId());
		map.put("neName", neModel.getDisplayName());

		returnMap = connectionManagerMapper.getTelnetNeByNeInfo(map);

		if (returnMap == null || returnMap.size() == 0) {
			exit = false;
		} else {	
			if (returnMap.get("neId").toString().equals(neModel.getNeId().toString())) {
				exit = false;
			}
		}
		return exit;
	}
	
	// 结合license检测是否可以添加网管
	private boolean checkAllowToAddNms(Long nmsType) {
		// 数量支持
		boolean numberAllow = false;
		// 类型支持--不检测类型
		boolean typeAllow = true;
		try {
			List<Map> connectionList = commonManagerMapper.selectTableListById(
					"T_BASE_EMS_CONNECTION", "IS_DEL", CommonDefine.FALSE,null,null);
			// 取得支持nms数量
			long supportNmsNumber = Long.valueOf(CommonDefine.LICENSE
					.get(CommonDefine.LICENSE_KEY_SUPPORT_NMS_NUMBER));
			// // 取得支持nms类型
			// String[] supportNmsTypeList = Define.LICENSE.get(
			// Define.LICENSE_KEY_SUPPORT_NMS_TYPE).split(";");
			// 已经添加的网管数量>=license支持数量，返回false
			if (connectionList.size() > supportNmsNumber
					|| connectionList.size() == supportNmsNumber) {
				numberAllow = false;
			} else {
				numberAllow = true;
			}
			// 已经添加的网管数量>=license支持数量，返回false
			// for (String type : supportNmsTypeList) {
			// if (Long.valueOf(type).longValue() == nmsType) {
			// typeAllow = true;
			// break;
			// } else {
			// typeAllow = false;
			// }
			// }
			if (numberAllow && typeAllow) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void editSyncMode(int emsConnectionId,int syncMode)throws CommonException{
		try{ 
			connectionManagerMapper.updateSyncMode(emsConnectionId,syncMode); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e,1);
		}  
	}
}