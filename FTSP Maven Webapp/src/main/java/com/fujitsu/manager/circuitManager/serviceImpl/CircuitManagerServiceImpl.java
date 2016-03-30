package com.fujitsu.manager.circuitManager.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IQuartzManagerService;
import com.fujitsu.IService.ITopoManagerService;
import com.fujitsu.common.CircuitDefine;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.job.CreateCirJob;
import com.fujitsu.job.NotificationJob;
import com.fujitsu.manager.circuitManager.service.CircuitManagerService;
import com.fujitsu.manager.resourceManager.serviceImpl.AreaManagerImpl;
import com.fujitsu.model.LinkAlarmModel;
import com.fujitsu.model.ProcessModel;
import com.fujitsu.model.TopoLineModel;
import com.fujitsu.model.TopoNodeModel;
import com.fujitsu.util.ExportExcelUtil;

@Scope("prototype")
@Service
public class CircuitManagerServiceImpl extends CircuitManagerService {
	/**
	 * @author wangjian (前半) daihuijun (后半)
	 */
	@Resource
	private CircuitManagerMapper circuitManagerMapper;

	@Resource
	private ITopoManagerService topoManagerService;
	@Resource
	ICommonManagerService commonManagerService;

	@Resource
	IQuartzManagerService quartzManagerService;

	@Resource
	IAlarmManagementService faultManagerService;
	
	
	public IDataCollectService dataCollectService;

	/** ********************************wangjian**begin************************ */
	
	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public Map<String, Object> getAllEMSTask(Integer userId, int emsGroupId,
			int start, int limit) throws CommonException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> listMap = new ArrayList<Map>();

		List listEmsId = new ArrayList();

		// 按网管查询
		List<Map> listems = commonManagerService.getAllEmsByEmsGroupId(userId, emsGroupId, 0, 0,false);


		for (Map ma : listems) {

			listEmsId.add(ma.get("BASE_EMS_CONNECTION_ID"));
		}
		listEmsId.add(0);
		List<Map> listTask = circuitManagerMapper.getAllEMSTask(emsGroupId,
				start, limit, listEmsId,userId,CommonDefine.TREE.TREE_DEFINE);

		if (listTask != null) {
			for (Map ma : listTask) {
				if (ma.get("RESULT") != null
						&& ma.get("RESULT").toString()
								.equals(CommonDefine.TASK_ON + "")) {
					ProcessModel model = CommonDefine.getProcessParameter(
							ma.get("SYS_TASK_ID").toString(), "newCir");
					ma.put("RESULT", "执行中(" + model.getText() + ")");
				}
				listMap.add(ma);

			}
		}
		map.put("rows", listMap);
		map.put("total", listMap.size());

		return map;
	}

	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public void createAllCircuit(String processKey) throws CommonException {

		// deleteCirByCrsDelete();
		// deleteCirByCrsAdd();
		// deleteCirByLinkDelete();
		// deleteCirByLinkAdd();
		// createHwVirtualOtn();
		
		//createZtePtnCircuit();
		
		HttpServletRequest request = ServletActionContext.getRequest();
		int userId=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		// 新建一个变量，存储全网生成查询条件
		String id = processKey;
//		try {
//			deleteAllCirAbout(id,getSessionId(),userId);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try {
			Map map = new HashMap();

			int countSdh = 0;
			int countOtn = 0;
			Map total = circuitManagerMapper.getTotal(map,userId,CommonDefine.TREE.TREE_DEFINE);
			Map map_count = circuitManagerMapper.getOtnCrsTotal(map,userId,CommonDefine.TREE.TREE_DEFINE);

			countSdh = Integer.parseInt(total.get("total").toString());
			countOtn = Integer.parseInt(map_count.get("total").toString());
			
			setCirState();
			boolean stop1 = newCircuit(map, id, getSessionId(), countSdh, 0,userId);
			if(stop1){
				return ;
			}
			
			// 中兴U31以太网
			boolean stop2 =createU31Eth(map,id, getSessionId(),userId);
			if(stop2){
				return ;
			}
			// 中兴E300以太网电路生成
			boolean stop3 =createZTECir(map,id, getSessionId(),userId);
			if(stop3){
				return ;
			}
			
			// otn电路生成
			boolean stop4 =createHWOtnCrs(map, id, getSessionId(), 0, countOtn,userId);
			if(stop4){
				return ;
			}
			// 中兴otn电路生成
			boolean stop5 =createZteOtn(map, id, getSessionId(),userId);
			if(stop5){
				return ;
			}
			// 贝尔 ptn
			boolean stop6 =createAluPtnCir(map, id, getSessionId(),userId);
			if(stop6){
				return ;
			}			
			// ptn 电路生成
			boolean stop7 =createZtePtnCircuit(map,id,getSessionId(), 0, 0,userId);
			if(stop7){
				return ;
			}
//			// sdh电路生成
//			newCircuit(map, id, getSessionId(), countSdh, 0,userId);
//			
//			// 中兴U31以太网
//			createU31Eth(map,id, getSessionId(),userId);
//			
//			// 中兴E300以太网电路生成
//			createZTECir(id, getSessionId(),userId);
//			
//			
//						
//			// otn电路生成
//			createHWOtnCrs(map, id, getSessionId(), 0, countOtn,userId);
//			
//			// 中兴otn电路生成
//			createZteOtn(map, id, getSessionId(),userId);
//			
//			//createTypeCir();
//			
//			// 贝尔 ptn
//			createAluPtnCir(map, id, getSessionId(),userId);
//			
//			// 中兴 ptn 电路生成
//			createZtePtnCircuit(map,id,getSessionId(), 0, 0,userId);
			
			
			
		} catch (Exception e) {

			CommonDefine.respCancel(getSessionId(), id);
			System.out.println(e.getMessage());
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NEW);
			
		}

	}

	/**
	 * 电路生成前，将电路设为前一次生成电路，以及交叉连接为查找状态
	 */
	public void setCirState(){
		 Map update = null;
			// 开始前先将数据库中的数据更新为未查找状态，isinCircuit为1L
			circuitManagerMapper.updateCrsState(CommonDefine.FALSE);
			// 将电路表中flag为2的统统改成0,2表示最近一次生成的电路
			circuitManagerMapper.updateCirState(CommonDefine.FALSE);
			// 更新交叉连接查询状态 1 是查找过 0 是没有查找过
			update = new HashMap();
			update.put("NAME", "t_base_otn_crs");
			update.put("ID_NAME", "IS_IN_CIRCUIT");
			update.put("ID_VALUE", CommonDefine.FALSE);
			update.put("ID_NAME_2", "IS_IN_CIRCUIT");
			update.put("ID_VALUE_2", CommonDefine.FALSE);
			circuitManagerMapper.updateByParameter(update);
			
			update = new HashMap();
			update.put("NAME", "t_cir_otn_circuit_info");
			update.put("ID_NAME", "IS_LATEST_CREATE");
			update.put("ID_VALUE", CommonDefine.TRUE);
			update.put("ID_NAME_2", "IS_LATEST_CREATE");
			update.put("ID_VALUE_2", CommonDefine.FALSE);			
			circuitManagerMapper.updateByParameter(update);
			
			// ptn 电路更新
			update = new HashMap();
			update.put("NAME", "t_cir_ptn_circuit_info");
			update.put("ID_NAME", "IS_LATEST_CREATE");
			update.put("ID_VALUE", CommonDefine.TRUE);
			update.put("ID_NAME_2", "IS_LATEST_CREATE");
			update.put("ID_VALUE_2", CommonDefine.FALSE);			
			circuitManagerMapper.updateByParameter(update);
	}
	/**
	 * 新增网管时，给电路生成新增一条任务记录
	 * 
	 * @param emsId
	 */
	public void addCirTask(int emsId,String emsName) throws CommonException {
		// 向任务表中插入一条记录
		Map insert = null;
		try {
			insert = new HashMap();
			insert.put("TASK_TYPE", CommonDefine.QUARTZ.JOB_CIRCUIT);
			// 默认每月执行
			insert.put("PERIOD_TYPE", CommonDefine.CIR_TASK_CYCLE_MONTH);
			// 周期值 数据格式 年， 季，月，周，日，时间 例：2015，2，5，，4，9:00
			// 每月1号12：00：00执行
			String cycle = "2014,0,1,0,1,12:00:00";
			insert.put("PERIOD", cycle);
			// 计算下次开始时间

			String next = calculateDate(cycle,
					CommonDefine.CIR_TASK_CYCLE_MONTH + "");
			insert.put("NEXT_TIME", next);
			// 任务状态 1.启用 2.挂起 3.删除
			insert.put("TASK_STATUS", CommonDefine.CIR_TASK_HOLD);

			insert.put("TASK_NAME", emsName + "电路自动生成");

			circuitManagerMapper.insertTask(insert);

			// 获取任务id
			Integer sysTaskId = Integer.parseInt(insert.get("SYS_TASK_ID")
					.toString());

			insert = new HashMap();
			insert.put("TARGET_ID", emsId);
			insert.put("SYS_TASK_ID", sysTaskId);
			// 任务对象类型
			insert.put("TARGET_TYPE", CommonDefine.TREE.NODE.EMS);
			circuitManagerMapper.insertTaskInfo(insert);

			// "0 15 10 * * ?" Fire at 10:15am every day
			String cronExpression = "0 0 12 1 * ?";
			Map map = new HashMap();
			map.put("BASE_EMS_CONNECTION_ID", emsId);
			// 添加一个quartz任务
			quartzManagerService.addJob(CommonDefine.QUARTZ.JOB_CIRCUIT,
					sysTaskId, CreateCirJob.class, cronExpression, map);
			
			quartzManagerService.addJob(CommonDefine.QUARTZ.SYSTEM_TASK_ID,
					null, NotificationJob.class, "0 0/5 * * * ?", map);
			
			// 将任务挂起
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_CIRCUIT,
					sysTaskId, CommonDefine.QUARTZ.JOB_PAUSE);
		} catch (ParseException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_PARSE);
		}
	}

	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public Map<String, Object> createCircuit(List<Map> list, String processKey)
			throws CommonException {

		Map return_map = new HashMap();
		HttpServletRequest request = ServletActionContext.getRequest();
		int userId=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());		
		String id = processKey;
		try {
			Map<String, Object> map = getCondition(list, 500);
			if (map.get("ReturnResult") != null) {
				return_map.put("returnResult",
						Integer.parseInt(map.get("ReturnResult").toString()));
				return_map.put("returnMessage", map.get("ReturnMessage")
						.toString());
				return return_map;
			}
			int countSdh = 0;
			int countOtn = 0;
			Map total = circuitManagerMapper.getTotal(map,userId,CommonDefine.TREE.TREE_DEFINE);
			Map map_count = circuitManagerMapper.getOtnCrsTotal(map,userId,CommonDefine.TREE.TREE_DEFINE);

			countSdh = Integer.parseInt(total.get("total").toString());
			countOtn = Integer.parseInt(map_count.get("total").toString());
			setCirState();
			// sdh电路生成
			boolean stop1 = newCircuit(map, id, getSessionId(), countSdh, 0,userId);
			if(stop1){
				return return_map;
			}
			
			// 中兴U31以太网
			boolean stop2 =createU31Eth(map,id, getSessionId(),userId);
			if(stop2){
				return return_map;
			}
			// 中兴E300以太网电路生成
			boolean stop3 =createZTECir(map,id, getSessionId(),userId);
			if(stop3){
				return return_map;
			}
			
			// otn电路生成
			boolean stop4 =createHWOtnCrs(map, id, getSessionId(), 0, countOtn,userId);
			if(stop4){
				return return_map;
			}
			// 中兴otn电路生成
			boolean stop5 =createZteOtn(map, id, getSessionId(),userId);
			if(stop5){
				return return_map;
			}
			// 贝尔 ptn
			boolean stop6 =createAluPtnCir(map, id, getSessionId(),userId);
			if(stop6){
				return return_map;
			}			
			// ptn 电路生成
			boolean stop7 =createZtePtnCircuit(map,id,getSessionId(), 0, 0,userId);
			if(stop7){
				return return_map;
			}
			return_map.put("returnResult", CommonDefine.SUCCESS);
			return_map.put("returnMessage", MessageHandler
					.getErrorMessage(MessageCodeDefine.CIRCUIT_CREATE_SUCCESS));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			CommonDefine.respCancel(getSessionId(), id);
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NEW);

		}

		return return_map;
	}

	/**
	 * 将tree中传入的参数做处理
	 * 
	 * @param list
	 * @return
	 */
	public Map<String, Object> getCondition(List<Map> list, int num) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map select = null;
		if (list != null && list.size() > 0) {
			// 所选个数不能超过五个
			if (list.size() > num) {
				map.put("ReturnResult", CommonDefine.FAILED);
				map.put("ReturnMessage",
						MessageHandler
								.getErrorMessage(MessageCodeDefine.CIRCUIT__NUMBER500_LIMIT));
				return map;
			} else {
				// 判断选择的范围是否一直
				String first = "";
				first = list.get(0).get("nodeLevel").toString();
				// 当单选时不需要判断，只有选择多个时才需要判断
				if (list.size() > 1) {
					for (int i = 1; i < list.size(); i++) {
						if (!first.equals(list.get(i).get("nodeLevel")
								.toString())) {
							map.put("ReturnResult", CommonDefine.FAILED);
							map.put("ReturnMessage",
									MessageHandler
											.getErrorMessage(MessageCodeDefine.CIRCUIT__LEVEL_LIMIT));
							return map;
						}
					}
				}

				// 判断树的级别，获取对应的id
				if (Integer.parseInt(first) == CommonDefine.TREE.NODE.EMSGROUP) {
					// 查询出所有网管组所包含的网管
					List<Integer> ids = new ArrayList<Integer>();

					for (Map ma : list) {
						ids.add(Integer.parseInt(ma.get("nodeId").toString()));
					}
					// 新建一个查询条件
					select = new HashMap();
					select.put("tableName", "t_base_ems_connection");
					select.put("select_id", "BASE_EMS_GROUP_ID");
					select.put("ids", ids);

					List<Map> lt_map = circuitManagerMapper.selectTable(select);
					List<Integer> lis = new ArrayList<Integer>();

					for (Map ma : lt_map) {
						lis.add(Integer.parseInt(ma.get(
								"BASE_EMS_CONNECTION_ID").toString()));
					}
					map.put("NAME", "BASE_EMS_CONNECTION_ID");
					map.put("ID", lis);

				} else if (Integer.parseInt(first) == CommonDefine.TREE.NODE.EMS) {
					List<Integer> lis = new ArrayList<Integer>();
					for (Map ma : list) {
						lis.add(Integer.parseInt(ma.get("nodeId").toString()));
					}
					map.put("NAME", "BASE_EMS_CONNECTION_ID");
					map.put("ID", lis);
				} else if (Integer.parseInt(first) == CommonDefine.TREE.NODE.SUBNET) {
					// 查询出所有子网包含的网元
					List<Integer> ids = new ArrayList<Integer>();

					for (Map ma : list) {
						ids.add(Integer.parseInt(ma.get("nodeId").toString()));
						// 判断子网是否还有下级，最多三级
						// 根据子网id查询相关网元，如果不存在则说明子网下面还有子网或者子网下面没有网元
						// 新建一个查询条件
						select = new HashMap();
						select.put("tableName", "t_base_ne");
						select.put("select_id", "BASE_SUBNET_ID");
						select.put("ids", new ArrayList<Integer>(Arrays.asList(Integer.parseInt(ma.get("nodeId").toString()))));
						List<Map> sub_1 = circuitManagerMapper.selectTable(select);
						if(sub_1!=null&&sub_1.size()>0){
							//ids.add(e);
						}else{
							// 查询出子id，二级子网
							select = new HashMap();
							select.put("tableName", "t_base_subnet");
							select.put("select_id", "PARENT_SUBNET");
							select.put("ids", new ArrayList<Integer>(Arrays.asList(Integer.parseInt(ma.get("nodeId").toString()))));
							List<Map> sub_2 = circuitManagerMapper.selectTable(select);
							if(sub_2!=null &&sub_2.size()>0){
								// 二级子网查询
								for(Map sb2:sub_2){
									//
									select = new HashMap();
									select.put("tableName", "t_base_ne");
									select.put("select_id", "BASE_SUBNET_ID");
									select.put("ids", new ArrayList<Integer>(Arrays.asList(Integer.parseInt(sb2.get("BASE_SUBNET_ID").toString()))));
									List<Map> sub_3 = circuitManagerMapper.selectTable(select);
									if(sub_3!=null&&sub_3.size()>0){
										ids.add(Integer.parseInt(sb2.get("BASE_SUBNET_ID").toString()));
									}else{
										//三级子网
										select = new HashMap();
										select.put("tableName", "t_base_subnet");
										select.put("select_id", "PARENT_SUBNET");
										select.put("ids", new ArrayList<Integer>(Arrays.asList(Integer.parseInt(sb2.get("BASE_SUBNET_ID").toString()))));
										List<Map> sub_4 = circuitManagerMapper.selectTable(select);
										if(sub_4!=null&&sub_4.size()>0){
											for(Map sb4:sub_4){
												select = new HashMap();
												select.put("tableName", "t_base_ne");
												select.put("select_id", "BASE_SUBNET_ID");
												select.put("ids", new ArrayList<Integer>(Arrays.asList(Integer.parseInt(sb4.get("BASE_SUBNET_ID").toString()))));
												List<Map> sub_5 = circuitManagerMapper.selectTable(select);
												if(sub_5!=null&&sub_5.size()>0){
													ids.add(Integer.parseInt(sb4.get("BASE_SUBNET_ID").toString()));
												}else{
													// 4级子网，暂时不做处理
												}
											}
										}
									}
									
								}
								
							}
						}
					}
					
					
					// 新建一个查询条件
					select = new HashMap();
					select.put("tableName", "t_base_ne");
					select.put("select_id", "BASE_SUBNET_ID");
					select.put("ids", ids);

					List<Map> lt_map = circuitManagerMapper.selectTable(select);
					List<Integer> lis = new ArrayList<Integer>();
					lis.add(0);
					for (Map ma : lt_map) {
						lis.add(Integer.parseInt(ma.get("BASE_NE_ID").toString()));
					}
					map.put("NAME", "BASE_NE_ID");
					map.put("ID", lis);
				} else if (Integer.parseInt(first) == CommonDefine.TREE.NODE.NE) {
					List<Integer> lis = new ArrayList<Integer>();
					for (Map ma : list) {
						lis.add(Integer.parseInt(ma.get("nodeId").toString()));
					}
					map.put("NAME", "BASE_NE_ID");
					map.put("ID", lis);
				}
				return map;
			}

		} else {
			map.put("ReturnResult", CommonDefine.FAILED);
			map.put("ReturnMessage", MessageHandler.getErrorMessage(MessageCodeDefine.CIRCUIT__NULL_LIMIT));
			return map;
		}

	}

	/**
	 * @@@分权分域到网元@@@
	 * SDH 电路生成方法 id 进度条生成key
	 */
	public boolean newCircuit(Map mapCondition, String id, String sessionId,
			int countSdh, int countOtn,int userId) throws CommonException {
		boolean stop = false;
		Map select = null;
		Map insert = null;
		Map update = null;
		//HttpServletRequest request = ServletActionContext.getRequest();
		//int userId=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		setCirState();
		// 统计需要更新的交叉连接的总数
		// Map total = circuitManagerMapper.getTotal(mapCondition);
		// if (total.get("total") != null
		// && Integer.parseInt(total.get("total").toString()) > 0) {
		if (countSdh > 0) {
			// 每一千条记录处理一次
			for (int i = 0; i <= (countSdh - 1) / 1000; i++) {
				//计算实际生成的电路数
				
				if (stop) {
					break;
				}
				
				List<Map> list_part = circuitManagerMapper.getPartCrossId(mapCondition, 0, 1000,userId,CommonDefine.TREE.TREE_DEFINE);
				if(list_part!=null&&list_part.size()>0){
					// 此时进行电路生成
					for (int j = 0; j < list_part.size(); j++) {

						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						
						Map map = (HashMap) list_part.get(j);
						// 查询条件
						select = new HashMap();
						select.put("BASE_SDH_CRS_ID", Integer.parseInt(map.get("BASE_SDH_CRS_ID").toString()));
						select.put("IS_IN_CIRCUIT", CommonDefine.FALSE);
						select.put("CIRCUIT_COUNT", 0);
						// 先查询一条满足条件的交叉连接，然后进行电路生成
						List<Map> list_crs = circuitManagerMapper.getCrossConnect(select);
						System.out.println(select.get("BASE_SDH_CRS_ID").toString()+" "+new Date()+"###################");
						int isagain = 0;

						if (list_crs != null && list_crs.size() > 0) {
							Map crsCon = list_crs.get(0);
							// 电路查找开始，先往电路表中插入数据
							Map map_cir = new HashMap();
							map_cir.put("IS_DEL", CommonDefine.FALSE);
							map_cir.put("CREATE_TIME", new Date());
							map_cir.put("A_END_CTP", crsCon.get("A_END_CTP"));
							map_cir.put("A_END_PTP", crsCon.get("A_END_PTP"));

							// 插入数据
							circuitManagerMapper.insertCircuit(map_cir);
							// // 新建查询对象
							// select = new HashMap();
							// select.put("NAME", "t_cir_circuit");
							// select.put("ID", "CIR_CIRCUIT_ID");
							// select.put("ID_NAME", "A_END_CTP");
							// select.put("ID_VALUE", Integer.parseInt(crsCon.get(
							// "A_END_CTP").toString()));
							// // 将保存后的值查出来，存放在map中
							// map_cir =
							// circuitManagerMapper.getLatestRecord(select);
							// .getCircuitByCtp(Integer.parseInt(crsCon.get(
							// "A_END_CTP").toString()));
							// 电路生成
							createSingleCircuit(crsCon, map_cir, isagain);
							// 定义一个hashMap 存放查询条件
							HashMap map_con = new HashMap();
							map_con.put("IS_COMPLETE", CommonDefine.FALSE);
							// 将剩余的没有在路由表flag为1的继续生成完成
							List<Map> list_route = circuitManagerMapper.getRoute(map_con);

							// 定义一个全局boolean值，用来控制dowhile循环是否结束
							boolean again = false;

							do {

								list_route = circuitManagerMapper.getRoute(map_con);

								again = false;
								if (list_route != null && list_route.size() > 0) {

									for (int k = 0; k < list_route.size(); k++) {

										Map map_route = list_route.get(k);

										// 查询电路的ctp和ptp 存入
										select = hashMapSon("t_cir_circuit","CIR_CIRCUIT_ID",map_route.get("CIR_CIRCUIT_ID"),null, null, null);
										List<Map> list_cir = circuitManagerMapper.getByParameter(select);
										// 新建一个hash 存储即将新建的电路
										Map hash_cir = new HashMap();
										hash_cir.put("A_END_CTP",Integer.parseInt(list_cir.get(0).get("A_END_CTP").toString()));
										hash_cir.put("A_END_PTP",Integer.parseInt(list_cir.get(0).get("A_END_PTP").toString()));
										hash_cir.put("CREATE_TIME", new Date());
										// 存入数据库
										circuitManagerMapper.insertCircuit(hash_cir);

										// // 将存储的记录查询出来
										// select = new HashMap();
										// select.put("NAME", "t_cir_circuit");
										// select.put("IN_NAME", "A_END_CTP");
										// select.put("IN_VALUE", Integer
										// .parseInt(list_cir.get(0).get(
										// "A_END_CTP").toString()));
										// select.put("ID", "CIR_CIRCUIT_ID");
										// hash_cir = circuitManagerMapper
										// .getLatestRecord(select);

										// 由于不能共享之前的路由信息，所以将之前的路由信息查出并复制一份
										// 查询条件是电路号以及路由id小于flag为1的
										List<Map> route_before = circuitManagerMapper.getRouteBefore(
														Integer.parseInt(map_route.get("CIR_CIRCUIT_ID").toString()),
														Integer.parseInt(map_route.get("CIR_CIRCUIT_ROUTE_ID").toString()));

										if (route_before != null&& route_before.size() > 0) {
											for (int r = 0; r < route_before.size(); r++) {
												Map map_route_before = route_before.get(r);
												// 准备将数据以新的电路号插入
												HashMap route_insert = new HashMap();
												route_insert.put("IS_COMPLETE",CommonDefine.TRUE);
												route_insert.put("CIR_CIRCUIT_ID",Integer.parseInt(hash_cir.get("CIR_CIRCUIT_ID").toString()));
												route_insert.put("CHAIN_ID",Integer.parseInt(map_route_before.get("CHAIN_ID").toString()));
												route_insert.put("CHAIN_TYPE",Integer.parseInt(map_route_before.get("CHAIN_TYPE").toString()));
												route_insert.put("AHEAD_CRS_ID",Integer.parseInt(map_route_before.get("AHEAD_CRS_ID").toString()));
												route_insert.put("NEXT_CHAIN_ID",map_route_before.get("NEXT_CHAIN_ID"));

												circuitManagerMapper.insertRoute(route_insert);

											}

											// 继续电路的查找
											// newCircuit(tcagain, tCircuit,
											// isagain);
											// newCircuitByfanDirection(tcagain,
											// tCircuit);

										}

										// 定义查询条件
										Map map_condition = new HashMap();
										map_condition.put("CIR_CIRCUIT_ROUTE_ID",Integer.parseInt(map_route.get("CIR_CIRCUIT_ROUTE_ID").toString()));

										// 查处flag为1的那一条
										List<Map> list_flag = circuitManagerMapper.getRoute(map_condition);

										Map route_fg = null;
										if (list_flag != null&& list_flag.size() > 0) {

											route_fg = list_flag.get(0);

											// 定义新的hashMap 准备往数据库插入
											HashMap hash_route_fg = new HashMap();

											hash_route_fg.put("AHEAD_CRS_ID",Integer.parseInt(route_fg.get("AHEAD_CRS_ID").toString()));
											hash_route_fg.put("IS_COMPLETE",CommonDefine.TRUE);
											hash_route_fg.put("CIR_CIRCUIT_ID",Integer.parseInt(hash_cir.get("CIR_CIRCUIT_ID").toString()));
											hash_route_fg.put("CHAIN_ID", Integer.parseInt(route_fg.get("CHAIN_ID").toString()));
											hash_route_fg.put("CHAIN_TYPE", Integer.parseInt(route_fg.get("CHAIN_TYPE").toString()));
											hash_route_fg.put("NEXT_CHAIN_ID",route_fg.get("NEXT_CHAIN_ID"));

											// 查询条件赋值
											select = new HashMap();
											select.put("BASE_SDH_CRS_ID",route_fg.get("NEXT_CHAIN_ID"));
											// 将路由的最后一条进行赋值
											// FIXME
											/*济南联通版本临时修改，正确性未知，出错原因是存在已经被标记删除的交叉连接，
											但是在t_cir_circuit_route中的NEXT_CHAIN_ID仍为此交叉连接id,
											导致circuitManagerMapper.getCrossConnect(select)查询结果为空，
											形成越界数据获取，前台报错生成电路异常*/
											List<Map> xxx = circuitManagerMapper.getCrossConnect(select);
											if(xxx.size() == 0){
												System.out.println(route_fg);
											}else{
												Map ma_crs = xxx.get(0);
											isagain = Integer.parseInt(route_fg.get("AHEAD_CRS_ID").toString());

											// 保存为新的路由记录
											circuitManagerMapper.insertRoute(hash_route_fg);
											// 继续电路的查找
											createSingleCircuit(ma_crs, hash_cir,isagain);
											}

										}
										if (route_fg != null)
										// 删除flag为1的记录
										circuitManagerMapper.deleteRoute(Integer.parseInt(route_fg.get("CIR_CIRCUIT_ROUTE_ID").toString()));

									}
								}

								// 检查是否还有flag为1L的没有处理
								List<Map> list = circuitManagerMapper.getRoute(map_con);

								if (list != null && list.size() > 0) {
									again = true;
								}

							} while (again);

							// // 删除路由信息
							// circuitDAOService.deleteTCircuitRoute(
							// TCircuitRoute.class, "flag", 1L);
							// // 更新完成以后将电路里flag为1的记录删除即垃圾电路删除
							// circuitDAOService.deleteObject(TCircuit.class,
							// "flag",
							// 1L);
							//
							// // // 将交叉连接标记为已经查找
							// Object obt = circuitDAOService.getObject(
							// TCrossConnection.class, "crossConnectionId", tc
							// .getCrossConnectionId());
							// TCrossConnection taga = new TCrossConnection();
							// taga = (TCrossConnection) obt;
							// taga.setIsInCircuit(Define.FLAG_TRUE);
							//
							// circuitDAOService.storeObject(taga);

						}
					
						
						// 进度描述信息更改--此处修改
						/*String text = "当前进度" + (i * 1000 + j + 1) + "/"+ (countSdh + countOtn);
						if ("newCir".equals(id)) {
							text = (i * 1000 + j + 1) + "/" + (countSdh + countOtn);
						}
						// 加入进度值
						CommonDefine.setProcessParameter(sessionId,id,text,Double.valueOf((i * 1000 + j + 1)/ ((double) (countSdh + countOtn))));*/
						CommonDefine.setProcessParameter(sessionId,id,(i * 1000 + j + 1),(int) ((countSdh + countOtn)*1.006+1),"sdh电路生成 ");
					}
					// 每1000次，生成一次以太网电路
					
					// 查询infoA表，判断电路是否是以太网电路
					List<Map> listEth = circuitManagerMapper.getEthFromInfoA();
					for (Map mapEth : listEth) {
						select = new HashMap();
						select.put("BASE_SDH_CTP_ID",Integer.parseInt(mapEth.get("Z_END_CTP").toString()));
						// 查询当前值的ctp
						Map ctp = circuitManagerMapper.getCtp(select).get(0);
						int isEth = 0;
						if (ctp.get("IS_ETH") != null) {
							isEth = Integer.parseInt(ctp.get("IS_ETH").toString());
						}

						createETHCircuit(mapEth, CommonDefine.TRUE, isEth);
					}

					// 查询info表z，判断电路是否是以太网电路
					List<Map> listEthZ = circuitManagerMapper.getEthFromInfoZ();
					for (Map mapEth : listEthZ) {
						select = new HashMap();
						select.put("BASE_SDH_CTP_ID",Integer.parseInt(mapEth.get("A_END_CTP").toString()));
						// 查询当前值的ctp
						Map ctp = circuitManagerMapper.getCtp(select).get(0);
						int isEth = 0;
						if (ctp.get("IS_ETH") != null) {
							isEth = Integer.parseInt(ctp.get("IS_ETH").toString());
						}

						createETHCircuit(mapEth, isEth, CommonDefine.TRUE);
					}
					
					// 判断是否是中兴的端口
					//createZTEEth();
					
				}else{
					// 已经结束
					System.out.println("循环已经结束。。。");
					CommonDefine.setProcessParameter(sessionId,id,countSdh,(int) ((countSdh + countOtn)*1.006+1),"sdh电路生成 ");
				}
				
			}
		} else {
			// 进度描述信息更改--此处修改
			/*String text = "当前进度0/0";
			if ("newCir".equals(id)) {
				text = "0/0";
			}
			// 加入进度值
			CommonDefine.setProcessParameter(sessionId, id, text, Double.valueOf(0));*/
			CommonDefine.setProcessParameter(sessionId,id,0,1,"sdh电路生成 ");
		}


		return stop;
	}


	public void createSingleCircuit(Map crsCon, Map map_cir, int isagain) {

		Map select = null;
		Map update = null;
		Map insert = null;
		String rate = crsCon.get("RATE") == null ? "" : crsCon.get("RATE")
				.toString();
		// 将第一个交叉连接id存入全局变量
		int tcfirst = Integer
				.parseInt(crsCon.get("BASE_SDH_CRS_ID").toString());
		boolean isture = true;
		// 取得电路的初始jklm

		int zk = 0;
		int zl = 0;
		int zm = 0;
		int zj = 0;
		int zvc4 = 0;
		int zvc8 = 0;
		int zvc16 = 0;
		int zvc64 = 0;

		int is_a_eth = 0;

		// 如果是分叉的电路生成，则需要先将jklm初始化，用起点的值初始化
		if (isagain != 0) {
			tcfirst = isagain;

			// 设置查询条件
			select = new HashMap();
			select.put("BASE_SDH_CRS_ID", isagain);
			Map ma = circuitManagerMapper.getCrossConnect(select).get(0);
			rate = ma.get("RATE") == null ? "" : ma.get("RATE").toString();
			if (ma != null) {
				// 查询条件
				select = new HashMap();
				select.put("BASE_SDH_CTP_ID",Integer.parseInt(ma.get("Z_END_CTP").toString()));
				Map map_ctp = circuitManagerMapper.getCtp(select).get(0);
				if (map_ctp != null) {
					zk = Integer.parseInt(map_ctp.get("CTP_K").toString());
					zl = Integer.parseInt(map_ctp.get("CTP_L").toString());
					zm = Integer.parseInt(map_ctp.get("CTP_M").toString());
					zj = Integer.parseInt(map_ctp.get("CTP_J").toString());
					zvc4 = Integer.parseInt(map_ctp.get("CTP_4C").toString());
					zvc8 = Integer.parseInt(map_ctp.get("CTP_8C").toString());
					zvc16 = Integer.parseInt(map_ctp.get("CTP_16C").toString());
					zvc64 = Integer.parseInt(map_ctp.get("CTP_64C").toString());

					// 起始端点是否属于以太网
					is_a_eth = Integer.parseInt(map_ctp.get("IS_ETH").toString());
				}
				// 查询a端是否是以太网口
				select = new HashMap();
				select.put("BASE_SDH_CTP_ID",Integer.parseInt(ma.get("A_END_CTP").toString()));
				Map ctp_a = circuitManagerMapper.getCtp(select).get(0);
				is_a_eth = Integer.parseInt(ctp_a.get("IS_ETH").toString());
			}
		} else {
			// 起始端点是否属于以太网
			select = new HashMap();
			select.put("BASE_SDH_CTP_ID",Integer.parseInt(crsCon.get("A_END_CTP").toString()));
			// 查询当前值的ctp
			Map ctp_new_a = circuitManagerMapper.getCtp(select).get(0);
			if (ctp_new_a.get("IS_ETH") != null) {
				is_a_eth = Integer.parseInt(ctp_new_a.get("IS_ETH").toString());
			}

		}

		// 查询条件
		select = new HashMap();
		select.put("BASE_SDH_CTP_ID",Integer.parseInt(crsCon.get("Z_END_CTP").toString()));

		// 查询当前值的ctp
		Map map_ctp_new = circuitManagerMapper.getCtp(select).get(0);
		// 将当前的值付过去
		if (map_ctp_new.get("CTP_K") != null&& !"0".equals(map_ctp_new.get("CTP_K").toString())) {
			zk = Integer.parseInt(map_ctp_new.get("CTP_K").toString());
		}
		if (map_ctp_new.get("CTP_L") != null&& !"0".equals(map_ctp_new.get("CTP_L").toString())) {
			zl = Integer.parseInt(map_ctp_new.get("CTP_L").toString());
		}

		if (map_ctp_new.get("CTP_M") != null&& !"0".equals(map_ctp_new.get("CTP_M").toString())) {
			zm = Integer.parseInt(map_ctp_new.get("CTP_M").toString());
		}
		if (map_ctp_new.get("CTP_J") != null&& !"0".equals(map_ctp_new.get("CTP_J").toString())) {
			zj = Integer.parseInt(map_ctp_new.get("CTP_J").toString());
		}
		if (map_ctp_new.get("CTP_4C") != null&& !"0".equals(map_ctp_new.get("CTP_4C").toString())) {
			zvc4 = Integer.parseInt(map_ctp_new.get("CTP_4C").toString());
		}
		if (map_ctp_new.get("CTP_8C") != null&& !"0".equals(map_ctp_new.get("CTP_8C").toString())) {
			zvc8 = Integer.parseInt(map_ctp_new.get("CTP_8C").toString());
		}
		if (map_ctp_new.get("CTP_16C") != null&& !"0".equals(map_ctp_new.get("CTP_16C").toString())) {
			zvc16 = Integer.parseInt(map_ctp_new.get("CTP_16C").toString());
		}
		if (map_ctp_new.get("CTP_64C") != null&& !"0".equals(map_ctp_new.get("CTP_64C").toString())) {
			zvc64 = Integer.parseInt(map_ctp_new.get("CTP_64C").toString());
		}

		// Map map_info = insertInfo(map_cir, CommonDefine.CIR_TYPE_SDH, rate);

		// 记录循环的次数，如果大于50次，则认为进入死循环，结束此次循环
		int numb = 0;
		int route_squence;
		do {
			numb++;
			// 查询条件
			select = new HashMap();
			select.put("BASE_SDH_CTP_ID",Integer.parseInt(crsCon.get("Z_END_CTP").toString()));
			// 每次进入循环都查询出z端的ctp
			map_ctp_new = circuitManagerMapper.getCtp(select).get(0);

			// 定义一个map，存放电路路由表的信息
			HashMap map_route = new HashMap();

			// 定义查询条件
			select = new HashMap();
			select.put("BASE_PTP_ID",Integer.parseInt(map_ctp_new.get("BASE_PTP_ID").toString()));

			// 根据ctp 查询出ctp的信息
			Map map_ptp = circuitManagerMapper.getPtp(select).get(0);
			// z端是边界点

			if (Integer.parseInt(map_ptp.get("PORT_TYPE").toString()) == CommonDefine.PORT_TYPE_EDGE_POINT) {

				isture = false;
				// 电路查找完成，先将信息存入路由表中

				HashMap map_route_insert = new HashMap();
				map_route_insert.put("CIR_CIRCUIT_ID", Integer.parseInt(map_cir.get("CIR_CIRCUIT_ID").toString()));
				map_route_insert.put("IS_COMPLETE", CommonDefine.TRUE);
				map_route_insert.put("CHAIN_ID", Integer.parseInt(crsCon.get("BASE_SDH_CRS_ID").toString()));
				map_route_insert.put("AHEAD_CRS_ID", tcfirst);
				map_route_insert.put("CHAIN_TYPE",CommonDefine.CHAIN_TYPE_SDH_CRS);
				// 将数据存入路由信息表
				circuitManagerMapper.insertRoute(map_route_insert);

				map_cir.put("Z_END_PTP",Integer.parseInt(crsCon.get("Z_END_PTP").toString()));

				map_cir.put("Z_END_CTP",Integer.parseInt(crsCon.get("Z_END_CTP").toString()));

				// 3表示当前生成的电路表示是当前新增的数据
				map_cir.put("CIR_TYPE", CommonDefine.FLAG_TEMP);
				// 更新完整电路信息补全
				map_cir.put("DIRECTION", CommonDefine.DIRECTION_ONE);

				// 获取电路起点的连接速率
				/* 暂时先注销掉tCircuit.setZConnectRate(tc.getConnectRate());* */
				map_cir.put("IS_MAIN_CIR", CommonDefine.TRUE);
				circuitManagerMapper.updateCircuit(map_cir);
				Map map_info = null;
				// // 判断逆向单电路是否已经存在，存在则删除
				map_info = insertInfo(map_cir, CommonDefine.CIR_TYPE_SDH, rate);
				// 更新电路表
				map_cir.put("CIR_CIRCUIT_INFO_ID", Integer.parseInt(map_info.get("CIR_CIRCUIT_INFO_ID").toString()));
				circuitManagerMapper.updateCircuit(map_cir);
				// 逆向查找看电路是否是双向的
				boolean isTwo = checkIsTwoDirection(map_cir);
				if (isTwo) {
					map_cir.put("DIRECTION", CommonDefine.DIRECTION_TWO);
					//map_info.put("CIR_COUNT",(Integer) map_info.get("CIR_COUNT") + 1);

				}
	
				circuitManagerMapper.updateCircuitInfo(map_info);

				// 将flag变为2
				map_cir.put("CIR_TYPE", CommonDefine.FLAG_LATEST);

				circuitManagerMapper.updateCircuit(map_cir);

				// 查询完整的cir_info
				select = hashMapSon("t_cir_circuit_info","CIR_CIRCUIT_INFO_ID",map_info.get("CIR_CIRCUIT_INFO_ID"), null, null, null);
				Map info = circuitManagerMapper.getByParameter(select).get(0);
				// 此处只做华为的电路生成，判断端口是否是华为
				
				List<Map> list = circuitManagerMapper.selectHwPort(info);
				if(list!=null && list.size()==2){
					// 判断是否是以太网电路 , 只要有一个时隙被以太网占用，就短视以太网电路
					if (is_a_eth == CommonDefine.TRUE
							|| (map_ctp_new.get("IS_ETH") != null && Integer
									.parseInt(map_ctp_new.get("IS_ETH").toString()) == CommonDefine.TRUE)) {
						createETHCircuit(info, is_a_eth,Integer.parseInt(map_ctp_new.get("IS_ETH").toString()));
					}
				}
				

			} else {
				// z端不是边界点的情况

				// 根据交叉连接的z端的ptp查处链路id（等于link的a端）
				// 查询条件
				select = new HashMap();
				select.put("A_END_PTP", Integer.parseInt(map_ctp_new.get("BASE_PTP_ID").toString()));
				// 获取链路信息,链路是没有方向性的，所以需要正反两次查询
				List<Map> link_list = circuitManagerMapper.getLink(select);
				if (link_list != null && link_list.size() > 0) {

					Map map_link = link_list.get(0);
					// 存在link，将上一跳额交叉练级数据，存入，下一跳信息为linkId
					HashMap route_crs = new HashMap();
					route_crs.put("CHAIN_ID", Integer.parseInt(crsCon.get("BASE_SDH_CRS_ID").toString()));
					route_crs.put("CIR_CIRCUIT_ID", Integer.parseInt(map_cir.get("CIR_CIRCUIT_ID").toString()));
					route_crs.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_SDH_CRS);
					route_crs.put("IS_COMPLETE", CommonDefine.TRUE);
					route_crs.put("NEXT_CHAIN_ID", Integer.parseInt(map_link.get("BASE_LINK_ID").toString()));
					route_crs.put("AHEAD_CRS_ID", tcfirst);
					// 插入交叉连接信息
					circuitManagerMapper.insertRoute(route_crs);

					// 新建一个map，存储查询条件
					Map ma_con = new HashMap();
					ma_con.put("BASE_PTP_ID", Integer.parseInt(map_link.get("Z_END_PTP").toString()));
					ma_con.put("CTP_64C", Integer.parseInt(map_ctp_new.get("CTP_64C").toString()));
					ma_con.put("CTP_16C", Integer.parseInt(map_ctp_new.get("CTP_16C").toString()));
					ma_con.put("CTP_8C", Integer.parseInt(map_ctp_new.get("CTP_8C").toString()));
					ma_con.put("CTP_4C", Integer.parseInt(map_ctp_new.get("CTP_4C").toString()));
					ma_con.put("CTP_J", Integer.parseInt(map_ctp_new.get("CTP_J").toString()));
					ma_con.put("CTP_K", Integer.parseInt(map_ctp_new.get("CTP_K").toString()));
					ma_con.put("CTP_L", Integer.parseInt(map_ctp_new.get("CTP_L").toString()));
					ma_con.put("CTP_M", Integer.parseInt(map_ctp_new.get("CTP_M").toString()));
					// 查询条件

					List<Map> list_ctp = circuitManagerMapper.getCtp(ma_con);

					int ctpid = 0;
					if (list_ctp != null && list_ctp.size() > 0) {
						// 取第一条记录
						ctpid = Integer.parseInt(list_ctp.get(0).get("BASE_SDH_CTP_ID").toString());
					}

					// 查询条件
					Map map_a = new HashMap();
					map_a.put("A_END_CTP", ctpid);
					List<Map> list_next = circuitManagerMapper.getCrossConnect(map_a);

					// 先处理没有找到的情况
					if (list_next == null || list_next.size() < 1) {

						int ratenum = 0;

						boolean isup = false; // 判断越级寻找是否找到
						// 将曾速率转换成数字，便于使用for循环
						if ("VC12".equals(rate)) {
							ratenum = 0;

						}
						if ("VC3".equals(rate)) {
							ratenum = 1;

						}
						if ("VC4".equals(rate)) {
							ratenum = 2;

						}
						if ("4C".equals(rate)) {
							ratenum = 3;

						}
						if ("8C".equals(rate)) {
							ratenum = 4;

						}
						if ("16C".equals(rate)) {
							ratenum = 5;

						}
						if ("64C".equals(rate)) {
							ratenum = 6;

						}
						int jump = 0;// 是否结束循环，1是0否
						// int jumpdown = 0;// 否结束向下循环 0否 1是
						// 对于当前曾速率查找不到的情况，层速率向上进一级，再查找，直到查到位置。
						// 最高的曾速率为16c，64c的默认是电路不存在的情况
						for (int h = ratenum; h < 7; h++) {

							Map map_condition = new HashMap();

							if (h == 0) {

								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", zvc16);
								map_condition.put("CTP_8C", zvc8);
								map_condition.put("CTP_4C", zvc4);
								map_condition.put("CTP_J", zj);
								map_condition.put("CTP_K", zk);
								map_condition.put("CTP_L", zl);
								map_condition.put("CTP_M", zm);

							} else if (h == 1) {

								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", zvc16);
								map_condition.put("CTP_8C", zvc8);
								map_condition.put("CTP_4C", zvc4);
								map_condition.put("CTP_J", zj);
								map_condition.put("CTP_K", zk);
								map_condition.put("CTP_L", 0);
								map_condition.put("CTP_M", 0);

							} else if (h == 2) {
								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", zvc16);
								map_condition.put("CTP_8C", zvc8);
								map_condition.put("CTP_4C", zvc4);
								map_condition.put("CTP_J", zj);
								map_condition.put("CTP_K", 0);
								map_condition.put("CTP_L", 0);
								map_condition.put("CTP_M", 0);

							} else if (h == 3) {
								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", zvc16);
								map_condition.put("CTP_8C", zvc8);
								map_condition.put("CTP_4C", zvc4);
								map_condition.put("CTP_J", 0);
								map_condition.put("CTP_K", 0);
								map_condition.put("CTP_L", 0);
								map_condition.put("CTP_M", 0);

							} else if (h == 4) {

								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", zvc16);
								map_condition.put("CTP_8C", zvc8);
								map_condition.put("CTP_4C", 0);
								map_condition.put("CTP_J", 0);
								map_condition.put("CTP_K", 0);
								map_condition.put("CTP_L", 0);
								map_condition.put("CTP_M", 0);
							} else if (h == 5) {
								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", zvc16);
								map_condition.put("CTP_8C", 0);
								map_condition.put("CTP_4C", 0);
								map_condition.put("CTP_J", 0);
								map_condition.put("CTP_K", 0);
								map_condition.put("CTP_L", 0);
								map_condition.put("CTP_M", 0);

							} else if (h == 6) {
								map_condition.put("BASE_PTP_ID",Integer.parseInt(map_link.get("Z_END_PTP").toString()));
								map_condition.put("CTP_64C", zvc64);
								map_condition.put("CTP_16C", 0);
								map_condition.put("CTP_8C", 0);
								map_condition.put("CTP_4C", 0);
								map_condition.put("CTP_J", 0);
								map_condition.put("CTP_K", 0);
								map_condition.put("CTP_L", 0);
								map_condition.put("CTP_M", 0);
							}

							// ctp 进一位进行查询
							List<Map> list_ctp_back = circuitManagerMapper.getCtp(map_condition);
							int ctpidnext = 0;
							if (list_ctp_back != null&& list_ctp_back.size() > 0) {
								// 取第一条记录
								ctpidnext = Integer.parseInt(list_ctp_back.get(0).get("BASE_SDH_CTP_ID").toString());
							}

							// 查询条件
							Map map_b = new HashMap();
							map_b.put("A_END_CTP", ctpidnext);
							List<Map> list_back = circuitManagerMapper.getCrossConnect(map_b);

							if (list_back != null && list_back.size() > 0) {

								for (int l = list_back.size(); l > 0; l--) {
									Map tnextup = list_back.get(l - 1);

									// 先将tc赋值
									map_route.put("CHAIN_ID", Integer.parseInt(map_link.get("BASE_LINK_ID").toString()));
									// 标明是lianlu
									map_route.put("CHAIN_TYPE",CommonDefine.CHAIN_TYPE_OUT_LINK);

									// 为下一跳作准备，将next的值付给tc
									if (l == 1) {
										crsCon = tnextup;
									}
									if (l == 1) {
										map_route.put("IS_COMPLETE",CommonDefine.TRUE);
									} else {
										map_route.put("IS_COMPLETE",CommonDefine.FALSE);
									}

									// 往路由表钟插值
									map_route.put("CIR_CIRCUIT_ID", Integer.parseInt(map_cir.get("CIR_CIRCUIT_ID").toString()));
									map_route.put("AHEAD_CRS_ID", tcfirst);
									map_route.put("NEXT_CHAIN_ID", Integer.parseInt(tnextup.get("BASE_SDH_CRS_ID").toString()));
									circuitManagerMapper.insertRoute(map_route);

									// tCircuitRoute.setFlag(0L);
									if (l == 1) {
										select = new HashMap();
										select.put("BASE_SDH_CTP_ID",Integer.parseInt(crsCon.get("Z_END_CTP").toString()));
										// 每次进入循环都查询出z端的ctp
										map_ctp_new = circuitManagerMapper.getCtp(select).get(0);

										if (Integer.parseInt(map_ctp_new.get("CTP_K").toString()) != 0) {
											zk = Integer.parseInt(map_ctp_new.get("CTP_K").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_L").toString()) != 0) {
											zl = Integer.parseInt(map_ctp_new.get("CTP_L").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_M").toString()) != 0) {
											zm = Integer.parseInt(map_ctp_new.get("CTP_M").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_J").toString()) != 0) {
											zj = Integer.parseInt(map_ctp_new.get("CTP_J").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_4C").toString()) != 0) {
											zvc4 = Integer.parseInt(map_ctp_new.get("CTP_4C").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_8C").toString()) != 0) {
											zvc8 = Integer.parseInt(map_ctp_new.get("CTP_8C").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_16C").toString()) != 0) {
											zvc16 = Integer.parseInt(map_ctp_new.get("CTP_16C").toString());
										}
										if (Integer.parseInt(map_ctp_new.get("CTP_64C").toString()) != 0) {
											zvc64 = Integer.parseInt(map_ctp_new.get("CTP_64C").toString());
										}
									}
									if (l == 1) {
										isup = true;
										isture = true;
										jump = 1;
									}

								}
							}
							if (jump == 1) {
								break;
							}

						}

						if (isup == false) {

							// 先插入链路信息

							insert = new HashMap();
							insert.put("CHAIN_ID",Integer.parseInt(map_link.get("BASE_LINK_ID").toString()));
							insert.put("CIR_CIRCUIT_ID",Integer.parseInt(map_cir.get("CIR_CIRCUIT_ID").toString()));
							insert.put("CHAIN_TYPE",CommonDefine.CHAIN_TYPE_OUT_LINK);
							insert.put("IS_COMPLETE", CommonDefine.TRUE);
							insert.put("AHEAD_CRS_ID", tcfirst);
							// 插入交叉连接信息
							circuitManagerMapper.insertRoute(insert);
							isture = false;
							// 插入垃圾电路的电路信息
							Map info = insertPartInfo(map_cir,CommonDefine.CIR_TYPE_SDH, rate);
							map_cir.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
							map_cir.put("IS_MAIN_CIR", CommonDefine.TRUE);
							map_cir.put("CIR_CIRCUIT_INFO_ID",info.get("CIR_CIRCUIT_INFO_ID"));
							circuitManagerMapper.updateCircuit(map_cir);
							// 更新交叉连接数
							select = hashMapSon("t_cir_circuit_route","CIR_CIRCUIT_ID",map_cir.get("CIR_CIRCUIT_ID"),"CHAIN_TYPE",CommonDefine.CHAIN_TYPE_SDH_CRS, null);
							List<Map> listRoute = circuitManagerMapper.getByParameter(select);
							// 如果存在逆向，则更新逆向的交叉连接
							for (Map mapRoute : listRoute) {
								select = hashMapSon("t_base_sdh_crs","BASE_SDH_CRS_ID",mapRoute.get("CHAIN_ID"), null, null,null);
								List<Map> listCrs = circuitManagerMapper.getByParameter(select);
								Map mapCrs = listCrs.get(0);
								// 将交叉连接数加1
								int countfan = Integer.parseInt(mapCrs.get("CIRCUIT_COUNT").toString()) + 1;
								mapCrs.put("CIRCUIT_COUNT", countfan);
								mapCrs.put("IS_IN_CIRCUIT", CommonDefine.TRUE);
								circuitManagerMapper.updateCrs(mapCrs);

							}

							// map_cir.put("IS_COMPLETE_CIR",
							// CommonDefine.FALSE);
							// circuitManagerMapper.updateCircuit(map_cir);
							// 查询完整的cir_info

							if (is_a_eth == CommonDefine.TRUE|| Integer.parseInt(map_ctp_new.get(
											"IS_ETH").toString()) == CommonDefine.TRUE) {
								createETHCircuit(info,is_a_eth,Integer.parseInt(map_ctp_new.get("IS_ETH").toString()));
							}

							// 删除关于垃圾电路在路由表中的信息

							// circuitDAOService.deleteTCircuitRoute(
							// TCircuitRoute.class, "circuitId",
							// tCircuit.getCircuitId());

						}

					} else {

						// 根据link信息查到的ptp符合的交叉连接
						for (int l = list_next.size(); l > 0; l--) {

							Map map_cr = list_next.get(l - 1);

							// 为下一跳作准备，将next的值付给tc
							map_route.put("CHAIN_ID",Integer.parseInt(map_link.get("BASE_LINK_ID").toString()));
							// 标明是lianlu
							map_route.put("CHAIN_TYPE",CommonDefine.CHAIN_TYPE_OUT_LINK);

							if (l == 1) {
								crsCon = map_cr;
							}
							// 往路由表钟插值
							map_route.put("CIR_CIRCUIT_ID",Integer.parseInt(map_cir.get("CIR_CIRCUIT_ID").toString()));
							map_route.put("AHEAD_CRS_ID", tcfirst);
							map_route.put("NEXT_CHAIN_ID",Integer.parseInt(map_cr.get("BASE_SDH_CRS_ID").toString()));

							// 是否已经完成所经过交叉连接的电路查找 0是1否
							if (l == 1) {
								map_route.put("IS_COMPLETE", CommonDefine.TRUE);
							} else {
								map_route.put("IS_COMPLETE", CommonDefine.FALSE);
							}

							circuitManagerMapper.insertRoute(map_route);

							if (l == 1) {
								select = new HashMap();
								select.put("BASE_SDH_CTP_ID", Integer.parseInt(crsCon.get("Z_END_CTP").toString()));
								// 每次进入循环都查询出z端的ctp
								map_ctp_new = circuitManagerMapper.getCtp(select).get(0);
								if (Integer.parseInt(map_ctp_new.get("CTP_K").toString()) != 0) {
									zk = Integer.parseInt(map_ctp_new.get("CTP_K").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_L").toString()) != 0) {
									zl = Integer.parseInt(map_ctp_new.get("CTP_L").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_M").toString()) != 0) {
									zm = Integer.parseInt(map_ctp_new.get("CTP_M").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_J").toString()) != 0) {
									zj = Integer.parseInt(map_ctp_new.get("CTP_J").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_4C").toString()) != 0) {
									zvc4 = Integer.parseInt(map_ctp_new.get("CTP_4C").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_8C").toString()) != 0) {
									zvc8 = Integer.parseInt(map_ctp_new.get("CTP_8C").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_16C").toString()) != 0) {
									zvc16 = Integer.parseInt(map_ctp_new.get("CTP_16C").toString());
								}
								if (Integer.parseInt(map_ctp_new.get("CTP_64C").toString()) != 0) {
									zvc64 = Integer.parseInt(map_ctp_new.get("CTP_64C").toString());
								}
							}

						}

					}

				} else {

					isture = false;
					// 找不到link信息，Z端不为边界点
					// 存在link，将上一跳额交叉练级数据，存入，下一跳信息为linkId
					HashMap route_crs = new HashMap();
					route_crs.put("CHAIN_ID", Integer.parseInt(crsCon.get("BASE_SDH_CRS_ID").toString()));
					route_crs.put("CIR_CIRCUIT_ID", Integer.parseInt(map_cir.get("CIR_CIRCUIT_ID").toString()));
					route_crs.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_SDH_CRS);
					route_crs.put("IS_COMPLETE", CommonDefine.TRUE);
					route_crs.put("AHEAD_CRS_ID", tcfirst);
					// 插入交叉连接信息
					circuitManagerMapper.insertRoute(route_crs);

					// 插入垃圾电路的电路信息
					Map info = insertPartInfo(map_cir,CommonDefine.CIR_TYPE_SDH, rate);
					map_cir.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
					map_cir.put("IS_MAIN_CIR", CommonDefine.TRUE);
					map_cir.put("CIR_CIRCUIT_INFO_ID",info.get("CIR_CIRCUIT_INFO_ID"));
					circuitManagerMapper.updateCircuit(map_cir);

					// 更新交叉连接数
					select = hashMapSon("t_cir_circuit_route","CIR_CIRCUIT_ID", map_cir.get("CIR_CIRCUIT_ID"),"CHAIN_TYPE", CommonDefine.CHAIN_TYPE_SDH_CRS, null);
					List<Map> listRoute = circuitManagerMapper.getByParameter(select);
					// 如果存在逆向，则更新逆向的交叉连接
					for (Map mapRoute : listRoute) {
						select = hashMapSon("t_base_sdh_crs","BASE_SDH_CRS_ID", mapRoute.get("CHAIN_ID"),null, null, null);
						List<Map> listCrs = circuitManagerMapper.getByParameter(select);
						Map mapCrs = listCrs.get(0);
						// 将交叉连接数加1
						int countfan = Integer.parseInt(mapCrs.get("CIRCUIT_COUNT").toString()) + 1;
						mapCrs.put("CIRCUIT_COUNT", countfan);
						mapCrs.put("IS_IN_CIRCUIT", CommonDefine.TRUE);
						circuitManagerMapper.updateCrs(mapCrs);

					}
					//
					if (is_a_eth == CommonDefine.TRUE
							|| Integer.parseInt(map_ctp_new.get("IS_ETH").toString()) == CommonDefine.TRUE) {
						createETHCircuit(info, is_a_eth,Integer.parseInt(map_ctp_new.get("IS_ETH").toString()));
					}
					// 删除关于垃圾电路在路由表中的信息
					// circuitDAOService.deleteTCircuitRoute(TCircuitRoute.class,
					// "circuitId", tCircuit.getCircuitId());
				}

			}
			if (numb > 100) {
				isture = false;
			}
		} while (isture);
	}

	/**
	 * 插入完整的电路信息
	 * 
	 * @param map_cir
	 *            电路map
	 * @param cir_type
	 *            电路类型 SDh 或者 以太网
	 * @param rate
	 *            交叉连接速率
	 * @return
	 */
	public Map insertInfo(Map map_cir, int cir_type, String rate) {
		Map select = null;
		// 在存电路时，也同时往电路信息表插入记录
		Map map_info = new HashMap();
		// 先查处当前电路编号的最大值
		Map map_maxNo = circuitManagerMapper.getMaxCircuitNo();
		if (map_maxNo != null) {
			if (map_maxNo.get("CIR_NO") != null&& !map_maxNo.get("CIR_NO").toString().isEmpty()) {
				map_info.put("CIR_NO",((Integer.parseInt(map_maxNo.get("CIR_NO").toString()) + 1) + ""));
			} else {
				map_info.put("CIR_NO", "100000");
			}
		} else {
			map_info.put("CIR_NO", "100000");
		}

		// 查询条件
		select = new HashMap();
		select.put("A_END_CTP", map_cir.get("A_END_CTP"));
		select.put("Z_END_CTP", map_cir.get("Z_END_CTP"));

		// 正向查找，电路信息表中否以存在相同电路
		List<Map> list_zh = circuitManagerMapper.getCircuitInfo(select);
		// 如果纯在记录，则直接更新电路信息表中的电路数
		if (list_zh != null && list_zh.size() > 0) {
			Map map_info_zh = list_zh.get(0);
			int no = Integer.parseInt(map_info_zh.get("CIR_COUNT").toString()) + 1;
			// if (Integer.parseInt(map_cir.get("DIRECTION").toString()) ==
			// CommonDefine.DIRECTION_TWO) {
			// no = Integer.parseInt(map_cir_info_z.get("CIR_COUNT")
			// .toString()) + 2;
			// }

			// map_info_zh.put("CIR_NO", map_info.get("CIR_NO"));
			map_info_zh.put("CIR_COUNT", no);
			map_info_zh.put("IS_LATEST_CREATE", CommonDefine.TRUE);
			circuitManagerMapper.updateCircuitInfo(map_info_zh);

			// 查询最近插入的一条记录
			select = hashMapSon("t_cir_circuit_info", "CIR_CIRCUIT_INFO_ID",map_info_zh.get("CIR_CIRCUIT_INFO_ID"), null, null, null);

			map_info = circuitManagerMapper.getByParameter(select).get(0);

		} else {

			// 查询条件
			select = new HashMap();
			select.put("A_END_CTP", map_cir.get("Z_END_CTP"));
			select.put("Z_END_CTP", map_cir.get("A_END_CTP"));
			// 如果不存在，则将az端反过来查找一边
			List<Map> list_fa = circuitManagerMapper.getCircuitInfo(select);

			if (list_fa != null && list_fa.size() > 0) {
				Map map_info_fa = list_fa.get(0);
				int no = Integer.parseInt(map_info_fa.get("CIR_COUNT").toString()) + 1;

				// 如果是双向的则电路数加2
				// if (Integer.parseInt(map_cir.get("DIRECTION").toString()) ==
				// CommonDefine.DIRECTION_TWO) {
				// no = Integer.parseInt(map_cir_info_f.get("CIR_COUNT")
				// .toString()) + 2;
				// }
				Map update = new HashMap();
				update.put("NAME", "t_cir_circuit_info");
				update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
				update.put("ID_VALUE", map_info_fa.get("CIR_CIRCUIT_INFO_ID"));
				update.put("ID_NAME_2", "CIR_COUNT");
				update.put("ID_VALUE_2", no);
				// update.put("ID_NAME_3", "CIR_NO");
				// update.put("ID_VALUE_3", map_info.get("CIR_NO"));
				update.put("ID_NAME_4", "IS_LATEST_CREATE");
				update.put("ID_VALUE_4", CommonDefine.TRUE);
				circuitManagerMapper.updateByParameter(update);
				// map_info_fa.put("CIR_COUNT", no);
				// map_info_fa.put("CIR_NO", map_info.get("CIR_NO"));
				// map_info_fa.put("IS_LATEST_CREATE", CommonDefine.TRUE);
				// circuitManagerMapper.updateCircuitInfo(map_info_fa);

				// 查询最近插入的一条记录
				select = hashMapSon("t_cir_circuit_info","CIR_CIRCUIT_INFO_ID",map_info_fa.get("CIR_CIRCUIT_INFO_ID"), null, null,null);

				map_info = circuitManagerMapper.getByParameter(select).get(0);

			} else {

				// 如果反过来还是没有，则插入新的记录
				// tci.setAEndJOrigin(tCircuit.getAEndJOrigin());
				// tci.setZEndJOrigin(tCircuit.getZEndJOrigin());

				map_info.put("A_END_CTP", map_cir.get("A_END_CTP"));
				map_info.put("A_END_PTP", map_cir.get("A_END_PTP"));
				map_info.put("Z_END_CTP", map_cir.get("Z_END_CTP"));
				map_info.put("Z_END_PTP", map_cir.get("Z_END_PTP"));
				map_info.put("IS_LATEST_CREATE", CommonDefine.TRUE);
				map_info.put("CIR_NO", map_info.get("CIR_NO"));
				map_info.put("A_END_RATE", rate);
				map_info.put("SVC_TYPE", cir_type);
				if (cir_type == CommonDefine.CIR_TYPE_ETH) {
					map_info.put("IS_COMPLETE_CIR",map_cir.get("IS_COMPLETE_CIR"));
				} else {
					map_info.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
				}
				map_info.put("SELECT_TYPE", CommonDefine.CIR_SELECT_YES);
				if (map_cir.get("DIRECTION") != null) {
					if (Integer.parseInt(map_cir.get("DIRECTION").toString()) == CommonDefine.DIRECTION_TWO) {
						map_info.put("CIR_COUNT", 2);
					} else {
						map_info.put("CIR_COUNT", 1);
					}
				} else {
					map_info.put("CIR_COUNT", 1);
				}
				circuitManagerMapper.insertCircuitInfo(map_info);

				// // 查询最近插入的一条记录
				// select = new HashMap();
				// select.put("NAME", "t_cir_circuit_info");
				// select.put("IN_NAME", "A_END_CTP");
				// select.put("IN_VALUE", map_cir.get("A_END_CTP"));
				// select.put("ID", "CIR_CIRCUIT_INFO_ID");
				//
				// map_info = circuitManagerMapper.getLatestRecord(select);
			}
		}

		return map_info;
	}

	/**
	 * 插入垃圾电路的电路信息
	 * 
	 * @param map_cir
	 *            电路map
	 * @param cir_type
	 *            电路类型 SDh 或者 以太网
	 * @param rate
	 *            交叉连接速率
	 * @return
	 */
	public Map insertPartInfo(Map map_cir, int cir_type, String rate) {
		Map select = null;
		Map update = null;
		// 在存电路时，也同时往电路信息表插入记录
		Map map_info = new HashMap();
		// 先查处当前电路编号的最大值
		Map map_maxNo = circuitManagerMapper.getMaxCircuitNo();
		if (map_maxNo != null) {
			if (map_maxNo.get("CIR_NO") != null&& !map_maxNo.get("CIR_NO").toString().isEmpty()) {
				map_info.put("CIR_NO",((Integer.parseInt(map_maxNo.get("CIR_NO").toString()) + 1) + ""));
			} else {
				map_info.put("CIR_NO", "100000");
			}
		} else {
			map_info.put("CIR_NO", "100000");
		}

		// 查询条件
		if(map_cir.get("A_END_CTP")!=null){
			select = hashMapSon("t_cir_circuit_info", "A_END_CTP",map_cir.get("A_END_CTP"), "IS_COMPLETE_CIR",CommonDefine.FALSE, null);

		}else{
			select = hashMapSon("t_cir_circuit_info", "Z_END_CTP",map_cir.get("Z_END_CTP"), "IS_COMPLETE_CIR",CommonDefine.FALSE, null);

		}
		List<Map> list = circuitManagerMapper.getByParameter(select);
		if (list != null && list.size() > 0) {
			Map map_info_zh = list.get(0);
			int no = Integer.parseInt(map_info_zh.get("CIR_COUNT").toString()) + 1;
			update = new HashMap();
			update.put("NAME", "t_cir_circuit_info");
			update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
			update.put("ID_VALUE", map_info_zh.get("CIR_CIRCUIT_INFO_ID"));
			update.put("ID_NAME_2", "CIR_NO");
			update.put("ID_VALUE_2", map_info.get("CIR_NO"));
			update.put("ID_NAME_3", "CIR_COUNT");
			update.put("ID_VALUE_3", no);
			update.put("ID_NAME_4", "IS_LATEST_CREATE");
			update.put("ID_VALUE_4", CommonDefine.TRUE);

			// 通用更新法
			circuitManagerMapper.updateByParameter(update);

			// 查询最近插入的一条记录
			select = hashMapSon("t_cir_circuit_info", "CIR_CIRCUIT_INFO_ID",
					map_info_zh.get("CIR_CIRCUIT_INFO_ID"), null, null, null);

			map_info = circuitManagerMapper.getByParameter(select).get(0);
		} else {
			// 为了修复bug#2909 去除了Interger.parseInt()方法
			/**
			 * map_info.put("A_END_CTP",Integer.parseInt(map_cir.get("A_END_CTP").toString()));
			 * map_info.put("A_END_PTP",Integer.parseInt(map_cir.get("A_END_PTP").toString()));
			 */
			if(map_cir.get("A_END_CTP")!=null){
				map_info.put("A_END_CTP",map_cir.get("A_END_CTP"));
			}else{
				map_info.put("Z_END_CTP",map_cir.get("Z_END_CTP"));
			}
			
			if(map_cir.get("A_END_PTP")!=null){
				map_info.put("A_END_PTP",map_cir.get("A_END_PTP"));
			}else{
				map_info.put("Z_END_PTP",map_cir.get("Z_END_PTP"));
			}
			
			map_info.put("IS_LATEST_CREATE", CommonDefine.TRUE);
			map_info.put("CIR_NO", map_info.get("CIR_NO"));
			map_info.put("SVC_TYPE", cir_type);
			map_info.put("A_END_RATE", rate);
			map_info.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
			map_info.put("SELECT_TYPE", CommonDefine.CIR_SELECT_YES);
			if (map_cir.get("DIRECTION") != null) {
				if (Integer.parseInt(map_cir.get("DIRECTION").toString()) == CommonDefine.DIRECTION_TWO) {
					map_info.put("CIR_COUNT", 2);
				} else {
					map_info.put("CIR_COUNT", 1);
				}
			} else {
				map_info.put("CIR_COUNT", 1);
			}
			circuitManagerMapper.insertCircuitInfo(map_info);
		}

		// // 查询最近插入的一条记录
		// select = new HashMap();
		// select.put("NAME", "t_cir_circuit_info");
		// select.put("IN_NAME", "A_END_CTP");
		// select.put("IN_VALUE", Integer.parseInt(map_cir.get("A_END_CTP")
		// .toString()));
		// select.put("ID", "CIR_CIRCUIT_INFO_ID");
		//
		// map_info = circuitManagerMapper.getLatestRecord(select);

		return map_info;
	}

	/**
	 * 判断电路是否是双向的，逆向电路生成
	 * 
	 * @return
	 */
	public boolean checkIsTwoDirection(Map mapCir) {

		boolean istwo = true;
		Map select = null;
		String cirId = "";// 电路id
		boolean isExist = true;// 用来存储比较逆向单电路是否已经存在
		// 创建查询条件，将最近一次生成的电路拿出
		select = hashMapSon("t_cir_circuit", "CIR_CIRCUIT_ID",
				mapCir.get("CIR_CIRCUIT_ID"), null, null, null);
		// select = hashMapSon("t_cir_circuit", "CIR_TYPE",
		// CommonDefine.FLAG_TEMP, null, null, null);
		// has_condition.put("CIR_TYPE", CommonDefine.FLAG_TEMP);

		// 查询出刚刚更新的一条电路的数据
		List<Map> list_cir = circuitManagerMapper.getByParameter(select);

		// .getCircuit(select);
		// 查处的数据正常只会有一条，如果有多条则用for循环处理
		if (list_cir != null && list_cir.size() > 0) {

			for (int i = 0; i < list_cir.size(); i++) {

				Map cir = list_cir.get(i);
				// 定义一个list 用来存 逆向的交叉连接

				List<Map> ni_crs_list = new ArrayList<Map>();
				List<Map> link_list = new ArrayList<Map>();

				// 根据电路查询出涉及的所有交叉
				List<Map> crs_list = circuitManagerMapper.getCrsFromRoute(
						Integer.parseInt(cir.get("CIR_CIRCUIT_ID").toString()),
						CommonDefine.CHAIN_TYPE_SDH_CRS);

				if (crs_list != null && crs_list.size() > 0) {
					// 更新交叉连接所穿过的电路数
					for (int j = 0; j < crs_list.size(); j++) {

						Map map_crs = crs_list.get(j);

						if (map_crs != null) {

							// 更新交叉连接的电路数
							int count = Integer.parseInt(map_crs.get("CIRCUIT_COUNT").toString()) + 1;
							map_crs.put("CIRCUIT_COUNT", count);
							map_crs.put("IS_IN_CIRCUIT", CommonDefine.TRUE);
							circuitManagerMapper.updateCrs(map_crs);

						}
					}
					// 查处的记录进行逆向的验证
					for (int j = 0; j < crs_list.size(); j++) {

						Map map_crs = crs_list.get(j);
						if (map_crs != null) {

							// 将交叉连接逆向赋值
							select = new HashMap();

							// 依次赋值，az反过来
							// a
							select.put("A_END_CTP", Integer.parseInt(map_crs.get("Z_END_CTP").toString()));

							// z
							select.put("Z_END_CTP", Integer.parseInt(map_crs.get("A_END_CTP").toString()));

							// 看是否存在反向的交叉连接
							List<Map> list_f = circuitManagerMapper.getCrossConnect(select);
							// 如果为空，结束本次循环
							if (list_f == null || list_f.size() < 1) {
								// 赋值给返回参数，电路是单向
								istwo = false;
								break;
							} else {

								// 获取反向的id
								Map map_f = list_f.get(0);
								boolean isYes = true;
								// 查看路由表中是否已存在此交叉
								select = hashMapSon("t_cir_circuit_route","CHAIN_ID",map_f.get("BASE_SDH_CRS_ID"),"CHAIN_TYPE",CommonDefine.CHAIN_TYPE_SDH_CRS, null);
								List<Map> listCrs = circuitManagerMapper.getByParameter(select);
								if (listCrs != null && listCrs.size() > 0) {
									if (listCrs.size() == 1) {
										cirId = listCrs.get(0).get("CIR_CIRCUIT_ID").toString();
									}
									isYes = true;
								} else {
									isYes = false;
								}
								if (!isYes) {
									isExist = false;
								}
								// 将id存入list
								ni_crs_list.add((HashMap) map_f);
							}

						}
						if (!istwo) {
							break;
						}

					}
				}

				// 查询出所有的link
				List<Map> link = circuitManagerMapper.getLinkFromRoute(
						Integer.parseInt(cir.get("CIR_CIRCUIT_ID").toString()),
						CommonDefine.CHAIN_TYPE_OUT_LINK);

				// 判断link是否为空，如果不为空，则进行link验证

				if (link != null && link.size() > 0) {
					for (int k = 0; k < link.size(); k++) {

						Map map_link = link.get(k);

						//
						select = new HashMap();

						// 依次赋值，az反过来
						// a
						select.put("A_END_PTP", Integer.parseInt(map_link.get(
								"Z_END_PTP").toString()));

						select.put("Z_END_PTP", Integer.parseInt(map_link.get(
								"A_END_PTP").toString()));

						// 逆向赋值以后进行查寻
						List<Map> link_f = circuitManagerMapper.getLink(select);

						if (link_f == null || link_f.size() < 1) {
							// 赋值给返回参数，电路是单向
							istwo = false;
							break;
						} else {
							boolean isYes = true;
							// 查看路由表中是否已存在此交叉
							select = hashMapSon("t_cir_circuit_route",
									"CHAIN_ID",
									link_f.get(0).get("BASE_LINK_ID"),
									"CHAIN_TYPE",
									CommonDefine.CHAIN_TYPE_OUT_LINK, null);
							List<Map> listCrs = circuitManagerMapper
									.getByParameter(select);
							if (listCrs != null && listCrs.size() > 0) {
								isYes = true;
							} else {
								isYes = false;
							}
							if (!isYes) {
								isExist = false;
							}
							link_list.add((HashMap) link_f.get(0));
						}
						if (!istwo) {
							break;
						}

					}
				}

				if (istwo) {

					if (isExist) {
						// 不用新增，已经存在逆向电路，将电路设为副电路
						if (cirId != "") {
							Map update = new HashMap();
							update.put("NAME", "t_cir_circuit");
							update.put("ID_NAME", "CIR_CIRCUIT_ID");
							update.put("ID_VALUE", cirId);
							update.put("ID_NAME_2", "IS_MAIN_CIR");
							update.put("ID_VALUE_2", CommonDefine.FALSE);
							update.put("ID_NAME_3", "DIRECTION");
							update.put("ID_VALUE_3", CommonDefine.DIRECTION_TWO);
							circuitManagerMapper.updateByParameter(update);
						} else {
							cir.put("IS_MAIN_CIR", CommonDefine.FALSE);
							circuitManagerMapper.updateCircuit(cir);
						}

						return true;
					}
					// 先新建电路
					Map cir_fan = new HashMap();
					cir_fan.put("A_END_CTP",
							Integer.parseInt(cir.get("Z_END_CTP").toString()));
					cir_fan.put("A_END_PTP",
							Integer.parseInt(cir.get("Z_END_PTP").toString()));
					cir_fan.put("Z_END_CTP",
							Integer.parseInt(cir.get("A_END_CTP").toString()));
					cir_fan.put("Z_END_PTP",
							Integer.parseInt(cir.get("A_END_PTP").toString()));
					cir_fan.put("CIR_CIRCUIT_INFO_ID", Integer.parseInt(cir
							.get("CIR_CIRCUIT_INFO_ID").toString()));
					/* 暂时注销掉tcfan.setAConnectRate(tcircuit.getZConnectRate());* */
					cir_fan.put("DIRECTION", CommonDefine.DIRECTION_TWO);
					cir_fan.put("CREATE_TIME", new Date());
					cir_fan.put("CIR_TYPE", CommonDefine.FLAG_LATEST);
					cir_fan.put("IS_MAIN_CIR", CommonDefine.FALSE);
					cir_fan.put("IS_DEL", CommonDefine.FALSE);
					circuitManagerMapper.insertCircuit(cir_fan);
					// // 查处电路的id,查询刚插入的记录
					// select = new HashMap();
					// select.put("NAME", "t_cir_circuit");
					// select.put("IN_NAME", "A_END_CTP");
					// select.put("IN_VALUE", cir.get("Z_END_CTP"));
					// select.put("ID", "CIR_CIRCUIT_ID");
					// cir_fan = circuitManagerMapper.getLatestRecord(select);
					// cir_fan = (HashMap) circuitManagerMapper
					// .getCircuitByCtp(Integer.parseInt(cir.get(
					// "Z_END_CTP").toString()));

					// 向路由表中插入数据
					if (ni_crs_list.size() == link_list.size() + 1) {
						int ahead = 0;
						for (int k = 0; k <= ni_crs_list.size() - 1; k++) {

							Map map_crs = ni_crs_list.get(k);
							if (k == 0) {
								ahead = Integer.parseInt(map_crs.get(
										"BASE_SDH_CRS_ID").toString());
							}

							HashMap map_route = new HashMap();
							map_route.put("AHEAD_CRS_ID", ahead);
							map_route.put(
									"CIR_CIRCUIT_ID",
									Integer.parseInt(cir_fan.get(
											"CIR_CIRCUIT_ID").toString()));
							map_route.put("IS_COMPLETE", CommonDefine.TRUE);
							map_route.put(
									"CHAIN_ID",
									Integer.parseInt(map_crs.get(
											"BASE_SDH_CRS_ID").toString()));
							map_route.put("CHAIN_TYPE",
									CommonDefine.CHAIN_TYPE_SDH_CRS);
							// 电路的最后一条记录 没有link和下一跳的值
							if (k < (ni_crs_list.size() - 1)) {
								map_route
										.put("NEXT_CHAIN_ID",
												Integer.parseInt(link_list
														.get(k)
														.get("BASE_LINK_ID")
														.toString()));
							}
							circuitManagerMapper.insertRoute(map_route);
							// 将逆向查找的交叉连接的起点设为以查找
							if (k == 0) {
								Map update = new HashMap();
								update.put("NAME", "t_base_sdh_crs");
								update.put("ID_NAME", "BASE_SDH_CRS_ID");
								update.put("ID_VALUE",
										map_crs.get("BASE_SDH_CRS_ID"));
								update.put("ID_NAME_2", "IS_IN_CIRCUIT");
								update.put("ID_VALUE_2", CommonDefine.TRUE);
								circuitManagerMapper.updateByParameter(update);
							}
							if (k < (ni_crs_list.size() - 1)) {
								Map map_link = link_list.get(k);
								HashMap route_link = new HashMap();
								route_link.put("AHEAD_CRS_ID", ahead);
								route_link.put(
										"CIR_CIRCUIT_ID",
										Integer.parseInt(cir_fan.get(
												"CIR_CIRCUIT_ID").toString()));
								route_link
										.put("IS_COMPLETE", CommonDefine.TRUE);
								route_link.put(
										"CHAIN_ID",
										Integer.parseInt(map_link.get(
												"BASE_LINK_ID").toString()));
								route_link.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OUT_LINK);

								route_link.put("NEXT_CHAIN_ID", ni_crs_list
										.get(k + 1).get("BASE_SDH_CRS_ID"));
								circuitManagerMapper.insertRoute(route_link);
							}
						}
					}

					// 如果存在逆向，则更新逆向的交叉连接
					for (int num = 0; num < ni_crs_list.size(); num++) {
						Map ma_crs = ni_crs_list.get(num);

						// 将交叉连接数加1
						int countfan = Integer.parseInt(ma_crs.get(
								"CIRCUIT_COUNT").toString()) + 1;
						ma_crs.put("CIRCUIT_COUNT", countfan);
						ma_crs.put("IS_IN_CIRCUIT", CommonDefine.TRUE);
						circuitManagerMapper.updateCrs((HashMap) ma_crs);

					}
				}
			}

		}
		return istwo;

	}

	/**
	 * 以太网电路生成
	 * 
	 * @param map
	 *            电路表信息
	 * @param porta
	 *            a端是否是以太网端口
	 * @param portz
	 */
	public void createETHCircuit(Map map, int porta, int portz) {

		// 定义az端的端口id
		Object port_a = null;
		Object port_z = null;
		Object macctp_a = null;
		Object macctp_z = null;
		Map select = null;
		if (porta == CommonDefine.TRUE) {
			// 如果是mac口
			int ctp_a = Integer.parseInt(map.get("A_END_CTP").toString());
			// 创建查询表名和查询条件
			select = hashMapSon("t_base_binding_path", "BASE_SDH_CTP_ID",
					ctp_a, "TYPE", CommonDefine.CIR_BINDING_ALL, null);

			List<Map> list = circuitManagerMapper.getByParameter(select);

			// 默认查询结果只有一条
			if (list != null && list.size() > 0) {
				Map ma = list.get(0);

				// 查找以太网server表
				select = hashMapSon("t_base_eth_svc", "Z_END_POINT",
						ma.get("BASE_PTP_ID"), "Z_END_POINT_TYPE", "MP", null);

				List<Map> list_yes = circuitManagerMapper
						.getByParameter(select);

				// 判断那个端口是mac
				if (list_yes != null && list_yes.size() > 0) {
					Map m_yes = list_yes.get(0);

					port_a = m_yes.get("A_END_POINT");
				}

			}

			// 查找ctp表
			select = hashMapSon("t_base_sdh_ctp", "BASE_PTP_ID", port_a, null,
					null, null);

			List<Map> list_ctp = circuitManagerMapper.getByParameter(select);

			if (list_ctp == null || list_ctp.size() < 1) {
				// 如果不存在虚拟时隙，则虚拟一条
				Map map_ctp = new HashMap();
				map_ctp.put("BASE_PTP_ID", port_a);
				map_ctp.put("IS_ETH", CommonDefine.TRUE);
				circuitManagerMapper.insertCtp(map_ctp);

				// // 新建查询条件
				// select = hashMapLatest("t_base_sdh_ctp", "BASE_PTP_ID",
				// port_a,
				// "BASE_SDH_CTP_ID");
				// Map new_id = circuitManagerMapper.getLatestRecord(select);

				if (map_ctp.get("BASE_SDH_CTP_ID") != null) {
					macctp_a = map_ctp.get("BASE_SDH_CTP_ID");
				}
			} else {
				macctp_a = list_ctp.get(0).get("BASE_SDH_CTP_ID");
			}

		} else {
			// 否则是ptp口
			port_a = map.get("A_END_PTP");
			macctp_a = map.get("A_END_CTP");
		}

		if (portz == CommonDefine.TRUE) {
			// 如果是mac口
			int ctp_z = Integer.parseInt(map.get("Z_END_CTP").toString());
			// 创建查询表名和查询条件
			select = hashMapSon("t_base_binding_path", "BASE_SDH_CTP_ID",
					ctp_z, "TYPE", CommonDefine.CIR_BINDING_ALL, null);

			List<Map> list = circuitManagerMapper.getByParameter(select);

			// 默认查询结果只有一条
			if (list != null && list.size() > 0) {
				Map ma = list.get(0);

				// 查找以太网server表
				select = hashMapSon("t_base_eth_svc", "A_END_POINT",
						ma.get("BASE_PTP_ID"), "A_END_POINT_TYPE", "MP", null);

				List<Map> list_yes = circuitManagerMapper
						.getByParameter(select);

				// 判断那个端口是mac
				if (list_yes != null && list_yes.size() > 0) {
					Map m_yes = list_yes.get(0);
					// 判断哪一段是mac口
					port_z = m_yes.get("A_END_POINT");
				}

				// 查找ctp表
				select = hashMapSon("t_base_sdh_ctp", "BASE_PTP_ID", port_z,
						null, null, null);

				List<Map> list_ctp = circuitManagerMapper
						.getByParameter(select);

				if (list_ctp == null || list_ctp.size() < 1) {
					// 如果不存在虚拟时隙，则虚拟一条

					Map map_ctp = new HashMap();
					map_ctp.put("BASE_PTP_ID", port_z);
					map_ctp.put("IS_ETH", CommonDefine.TRUE);
					circuitManagerMapper.insertCtp(map_ctp);


					if (map_ctp.get("BASE_SDH_CTP_ID") != null) {
						macctp_z = map_ctp.get("BASE_SDH_CTP_ID");
					}
				} else {
					macctp_z = list_ctp.get(0).get("BASE_SDH_CTP_ID");
				}

			}
		} else {
			// 否则是ptp口,添加判空语句，为不完整电路做准备
			port_z = map.get("Z_END_PTP");

			macctp_z = map.get("Z_END_CTP");

		}

		// 确定完端口以后去验证电路是否已经存在
		select = new HashMap();
		select.put("A_END_PTP", port_a);
		if (macctp_z == null) {
			// 如果为null，则是垃圾电路
			select.put("IS_COMPLETE_CIR", CommonDefine.FALSE);

		} else {
			select.put("Z_END_PTP", port_z);
		}
		List<Map> list_m = circuitManagerMapper.getCircuitInfo(select);

		Map map_info = null;
		// 如果存在，则更新电路数
		if (list_m != null && list_m.size() > 0) {
			map_info = list_m.get(0);

			// 新建一个map，更新电路数
			Map map_if = new HashMap();
	
			map_if.put("NAME", "t_cir_circuit_info");
			map_if.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
			map_if.put("ID_VALUE", map_info.get("CIR_CIRCUIT_INFO_ID"));
			map_if.put("ID_NAME_2", "CIR_COUNT");
			map_if.put("ID_VALUE_2",
					Integer.parseInt(map_info.get("CIR_COUNT").toString()) + 1);
			// map_if.put("A_END_PTP",
			// Integer.parseInt(map_info.get("A_END_PTP")
			// .toString()));
			// map_if.put("Z_END_PTP",
			// Integer.parseInt(map_info.get("Z_END_PTP")
			// .toString()));
			circuitManagerMapper.updateByParameter(map_if);

		} else {
			// 不存在则新增一条
			Map ma_new = new HashMap();
			ma_new.put("A_END_CTP", macctp_a);
			ma_new.put("Z_END_CTP", macctp_z);
			ma_new.put("A_END_PTP", port_a);
			ma_new.put("Z_END_PTP", port_z);
			
			if (macctp_z == null) {
				// 为空，则是不完整电路插入
				ma_new.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
				map_info = insertPartInfo(ma_new, CommonDefine.CIR_TYPE_ETH,
						map.get("A_END_RATE").toString());
			} else {
				// 有一个是mac口就认为电路是以太网电路
				if (porta == CommonDefine.TRUE || portz == CommonDefine.TRUE) {
					ma_new.put("IS_COMPLETE_CIR", CommonDefine.TRUE);

				} else {
					// 则是不完整电路
					ma_new.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
				}
				map_info = insertInfo(ma_new, CommonDefine.CIR_TYPE_ETH, map
						.get("A_END_RATE").toString());

			}
			// map_info = circuitManagerMapper.getLatestRecord(select);
		}
		// 将原有电路改为子电路
		Map map_sub = new HashMap();
		map_sub.put("NAME", "t_cir_circuit_info");
		map_sub.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
		map_sub.put("ID_VALUE", map.get("CIR_CIRCUIT_INFO_ID"));
		map_sub.put("ID_NAME_2", "PARENT_CIR");
		map_sub.put("ID_VALUE_2", map_info.get("CIR_CIRCUIT_INFO_ID"));
		map_sub.put("ID_NAME_3", "SVC_TYPE");
		map_sub.put("ID_VALUE_3", CommonDefine.CIR_TYPE_ETH);
		map_sub.put("ID_NAME_4", "SELECT_TYPE");
		map_sub.put("ID_VALUE_4", CommonDefine.CIR_SELECT_NO);

		// map_sub.put("PARENT_CIR", Integer.parseInt(map_info.get(
		// "CIR_CIRCUIT_INFO_ID").toString()));
		// map_sub.put("A_END_CTP", Integer.parseInt(map.get("A_END_CTP")
		// .toString()));
		// map_sub.put("Z_END_CTP", Integer.parseInt(map.get("Z_END_CTP")
		// .toString()));
		//
		// map_sub.put("SVC_TYPE", CommonDefine.CIR_TYPE_ETH);
		// map_sub.put("SELECT_TYPE", CommonDefine.CIR_SELECT_NO);

		circuitManagerMapper.updateByParameter(map_sub);
	}
	
	// 创建中兴以太网
	public boolean createZTEEth(Map mapSelect,String id, String sessionId,int userId){
		boolean stop= false;
		CommonDefine.setProcessParameter(sessionId,id,0,1,"中兴E300以太网端口解析 ");
		Map select = null;
		 // 获取符合条件的中兴ptp端口  在t_base_ptp表中查询PTP_FTP字段为0，DOMAIN为3的PTP记录A 即mac口
		//select = hashMapSon("t_base_ptp", "PTP_FTP", CommonDefine.CIR_PTP, "DOMAIN", CommonDefine.PM.DOMAIN.DOMAIN_ETH_FLAG, null);
		//select.put("ID_NAME_3", "PORT_TYPE");
		//select.put("ID_VALUE_3", CommonDefine.PORT_TYPE_EDGE_POINT);
		Map mapCount = circuitManagerMapper.selectETHPortCount(mapSelect,userId,CommonDefine.TREE.TREE_DEFINE);
		int count = Integer.parseInt(mapCount.get("total").toString());
		if(count>0){
			for(int i = 0; i <= (count - 1) / 1000; i++){
				if(stop){
					break;
				}
				List<Map> list_port = circuitManagerMapper.selectETHPort(mapSelect,userId,CommonDefine.TREE.TREE_DEFINE,i*1000,1000);
				if(list_port!=null&&list_port.size()>0){
					for(int j = 0;j<list_port.size();j++){
						
						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						
						// 判断板卡类型
						select = hashMapSon("t_base_unit", "BASE_UNIT_ID", list_port.get(j).get("BASE_UNIT_ID"), null, null, null);
						List<Map> list_unit = circuitManagerMapper.getByParameter(select);
						if(list_unit!=null&&list_unit.size()>0){
							// TGEB,TFE,TSGA8 目前已知的透传板卡为上述三种类型
							if(list_unit.get(0).get("DISPLAY_NAME").toString().contains("TGE")||
									list_unit.get(0).get("DISPLAY_NAME").toString().contains("TFE")||
									list_unit.get(0).get("DISPLAY_NAME").toString().contains("TSG")){
								// 透传板卡处理逻辑
								createTouChuan(list_port.get(j));
								
							}else if(list_unit.get(0).get("DISPLAY_NAME").toString().contains("TSG")&&
									"1".equals(list_unit.get(0).get("IS_TRANSPARENCY").toString())){
								// 透传板卡处理逻辑
								createTouChuan(list_port.get(j));
							}else{
								
								// 2层板卡处理逻辑 ,如果在bingdingpath表能查到此ptp端口 则是无uui口
								select.clear();
								select = hashMapSon("t_base_binding_path", "BASE_PTP_ID", list_port.get(0).get("BASE_PTP_ID"), null, null, null);
								List<Map> list_bangding = circuitManagerMapper.getByParameter(select);
								if(list_bangding!=null&&list_bangding.size()>0){
									// 无 UUi
									createNoUUI(list_port.get(j));
								}else{
									
									// 有uui
									createUUI(list_port.get(j));
								}
								
							}
							
						}
						CommonDefine.setProcessParameter(sessionId,id,(i * 1000 + j + 1),(int) (count*1.006+1),"中兴E300以太网端口解析 ");
					}
					
				}
			}
		}
		
		return stop;
		
	}
	
	/**
	 * 中兴u31以太网电路生成
	 * @param id
	 * @param sessionId
	 * @param userId
	 * @return
	 */
	public boolean createU31Eth(Map map,String id, String sessionId,int userId){
		boolean stop= false;
		CommonDefine.setProcessParameter(sessionId,id,0,1,"中兴U31以太网端口解析 ");
		Map select = null;
		Map insert = null;
		 // 获取符合条件的中兴ptp端口  在t_base_ptp表中查询PTP_FTP字段为0，DOMAIN为3的PTP记录A 即mac口
		//select = hashMapSon("t_base_ptp", "PTP_FTP", CommonDefine.CIR_PTP, "DOMAIN", CommonDefine.PM.DOMAIN.DOMAIN_ETH_FLAG, null);
		//select.put("ID_NAME_3", "PORT_TYPE");
		//select.put("ID_VALUE_3", CommonDefine.PORT_TYPE_EDGE_POINT);
		Map mapCount = circuitManagerMapper.selectU31EthPortCount(map,userId,CommonDefine.TREE.TREE_DEFINE);
		int count = Integer.parseInt(mapCount.get("total").toString());
		if(count>0){
			for(int i = 0; i <= (count - 1) / 1000; i++){
				if(stop){
					break;
				}
				List<Map> list_port = circuitManagerMapper.selectU31EthPort(map,userId,CommonDefine.TREE.TREE_DEFINE,i*1000,1000);
				if(list_port!=null&&list_port.size()>0){
					for(int j = 0;j<list_port.size();j++){
						
						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						
						
			
						select = new HashMap();
						select.put("BINDING_PTP_ID", list_port.get(j).get("BASE_PTP_ID"));
						// 在bangdingPath表中根据bangding_ptp_id查询绑定的ftp（base_ptp_id）
						List<Map> ftpList = circuitManagerMapper.selectFromBangByBangID(select);
						// 判断有无14，15端口类型
						if(ftpList!=null && ftpList.size()>0){
							// 有14，15
							for(int k = 0 ;k<ftpList.size();k++){
								select = new HashMap();
								select.put("BASE_PTP_ID", ftpList.get(k).get("BASE_PTP_ID"));
								// 根据查询到的Vbid去vblist表中查询出Ftp，
								List<Map> vbPtp = circuitManagerMapper.selectFromVbListByFtpId(select);
								if(vbPtp!=null&&vbPtp.size()>0){
									List<Integer> listPtp = new ArrayList<Integer>();
									for(Map mp :vbPtp){
										listPtp.add(Integer.parseInt(mp.get("BASE_PTP_ID").toString()));
									}
									select = new HashMap();
									select.put("list", listPtp);
									// 根据ftp去bangdingPatn表中查询对应Ftp的bangdingID
									List<Map> bangFtpList = circuitManagerMapper.selectFromBangByPtpId(select);
									if(bangFtpList!=null&&bangFtpList.size()>0){
										List<Integer> listBing = new ArrayList<Integer>();
										for(Map mp :bangFtpList){
											listBing.add(Integer.parseInt(mp.get("BINDING_PTP_ID").toString()));
										}
										select = new HashMap();
										select.put("list", listBing);
										// 将查询到的ftp作为baseptpId再次查询bangding表，获取ctp信息
										List<Map> ctpList = circuitManagerMapper.selectFromBangByPtpId(select);
										if(ctpList!=null&&ctpList.size()>0){
											for(Map mapBang:ctpList){
												// 讲绑定关系插入零时表
												insert = new HashMap();
												insert.put("BASE_MAC_ID", list_port.get(j).get("BASE_PTP_ID"));
												insert.put("BASE_CTP_ID", mapBang.get("BASE_SDH_CTP_ID"));
												circuitManagerMapper.insertTemp(insert);
											}
											
										}
									}
								}
							
							}
//								select = new HashMap();
//								select.put("list", ftpList);
//								//根据端口的网元id，去Vb表中查询出vbid
//								List<Map> vbList = circuitManagerMapper.selectFromVbByNeId(select);
//								if(vbList!=null&&vbList.size()>0){}
							}else{
							// 如果不含14，15
//							List<Integer> listNe = new ArrayList<Integer>();
//							listNe.add(Integer.parseInt(list_port.get(j).get("BASE_PTP_ID").toString()));
//							select  = new HashMap();
//							select.put("list", listNe);
//							//根据端口的网元id，去Vb表中查询出vbid
//							List<Map> vbList = circuitManagerMapper.selectFromVbByNeId(select);
//							// 默认取第一条记录
							
							select  = new HashMap();
							select.put("BASE_PTP_ID", list_port.get(j).get("BASE_PTP_ID"));
							// 根据查询到的Vbid去vblist表中查询出ftp，
							List<Map> vbPtp = circuitManagerMapper.selectFromVbListByFtpId(select);
							if(vbPtp!=null && vbPtp.size()>0){
								List<Integer> listBing = new ArrayList<Integer>();
								for(Map mp :vbPtp){
									listBing.add(Integer.parseInt(mp.get("BASE_PTP_ID").toString()));
								}
								select = new HashMap();
								select.put("list", listBing);
								// 根据查询出的Ftp，去bangdingpath表中查询出对应的ctp
								List<Map> ctpList = circuitManagerMapper.selectFromBangdingByPTp(select);
								if(ctpList!=null&&ctpList.size()>0){
									for(Map mapBang:ctpList){
										// 讲绑定关系插入零时表
										insert = new HashMap();
										insert.put("BASE_MAC_ID", list_port.get(j).get("BASE_PTP_ID"));
										insert.put("BASE_CTP_ID", mapBang.get("BASE_SDH_CTP_ID"));
										circuitManagerMapper.insertTemp(insert);
									}
									
								}
							}
							
						}
						
						CommonDefine.setProcessParameter(sessionId,id,(i * 1000 + j + 1),(int) (count*1.006+1),"中兴U31以太网端口解析 ");
					}
					
				}
			}
		}
		
		return stop;
	}

	/**
	 * 具体电路生成
	 */
	public boolean createZTECir(Map mapSelect, String id, String sessionId,int userId){
		boolean stop = false;

		// 先将数据移动到临时表中
		stop = createZTEEth(mapSelect, id,  sessionId, userId);
		if(stop){
			return stop;
		}
		
		Map select = null;
		Map insert = null;
		Map update = null;
		Map delete = null;
		Object port_a = null;
		Object ctp_a = null;
		boolean isMaca = true;
		Object port_z = null;
		Object ctp_z = null;
		boolean isMacz = true;
		//Map mapCir = null;
		Object port_temp = null;
		Object ctp_temp = null;
		boolean isGo = false;
		
		update = new HashMap();
		update.put("NAME", "t_cir_temp");
		update.put("ID_NAME_2", "IS_SELECT");
		update.put("ID_VALUE_2", 0);
		circuitManagerMapper.updateByParameter(update);
		
		// 遍历从临时表中数据，生成以太网电路
		CommonDefine.setProcessParameter(sessionId,id,0,1,"中兴以太网电路生成 ");
		Map mapTempCount = circuitManagerMapper.selectZTETempCount(mapSelect);
		int tempCount = Integer.parseInt(mapTempCount.get("total").toString());
		if(tempCount>0){
			
			for(int i = 0 ; i <tempCount ;i++){
				 port_a = null;
				 ctp_a = null;
				 port_z = null;
				 ctp_z = null;
				
				if (CommonDefine.getIsCanceled(sessionId, id)) {
					CommonDefine.respCancel(sessionId, id);
					stop = true;
					break;
				}
				
				List<Map> list_cir = circuitManagerMapper.selectZTETemp(mapSelect,0,1);
				if(list_cir!=null&&list_cir.size()>0){
					for(int j = 0;j<list_cir.size();j++){
						Map mapTemp = list_cir.get(j);
						System.out.println("BASE_TEMP_ID=="+mapTemp.get("BASE_TEMP_ID").toString());
						// 更新查询标记
						update = new HashMap();
						update.put("NAME", "t_cir_temp");
						update.put("ID_NAME", "BASE_TEMP_ID");
						update.put("ID_VALUE", list_cir.get(0).get("BASE_TEMP_ID"));
						update.put("ID_NAME_2", "IS_SELECT");
						update.put("ID_VALUE_2", 1);
						circuitManagerMapper.updateByParameter(update);
						
						List<Map> list_delete = new ArrayList<Map>();
						port_a = mapTemp.get("BASE_PTP_ID");
						// 查找ctp表
						select = hashMapSon("t_base_sdh_ctp", "BASE_PTP_ID", port_a, null,
								null, null);
			
						List<Map> list_ctp = circuitManagerMapper.getByParameter(select);
			
						if (list_ctp == null || list_ctp.size() < 1) {
							// 如果不存在虚拟时隙，则虚拟一条
							Map map_ctp = new HashMap();
							map_ctp.put("BASE_PTP_ID", port_a);
							map_ctp.put("IS_ETH", CommonDefine.TRUE);
							circuitManagerMapper.insertCtp(map_ctp);
			
							if (map_ctp.get("BASE_SDH_CTP_ID") != null) {
								ctp_a = map_ctp.get("BASE_SDH_CTP_ID");
							}
						} else {
							ctp_a = list_ctp.get(0).get("BASE_SDH_CTP_ID");
						}
			
						// 以ctp作为起点，查询电路
						select.clear();
						select = hashMapSon("t_cir_circuit_info", "A_END_CTP", mapTemp.get("BASE_CTP_ID"), null, null, null);
						List<Map> list_cir_a = circuitManagerMapper.getByParameter(select);
						if(list_cir_a!=null&&list_cir_a.size()>0){					
							Map mapcir = list_cir_a.get(0);
							// 如果存在父id，则跳过 无需再次绑定
							if(mapcir.get("PARENT_CIR")==null||mapcir.get("PARENT_CIR").toString().isEmpty()
									||"0".equals(mapcir.get("PARENT_CIR").toString())){
								
							}else{
								continue;
							}
							// 查找到电路后，获取另一端的mac数据
							select.clear();
							select = hashMapSon("t_cir_temp", "BASE_CTP_ID", mapcir.get("Z_END_CTP"), null, null, null);
							List<Map> list_temp = circuitManagerMapper.getByParameter(select);
							if(list_temp!=null&&list_temp.size()>0){
								port_z = list_temp.get(0).get("BASE_PTP_ID");
								// 查找ctp表
								select = hashMapSon("t_base_sdh_ctp", "BASE_PTP_ID", port_z,
										null, null, null);
			
								List<Map> list_ctp_z = circuitManagerMapper
										.getByParameter(select);
			
								if (list_ctp_z == null || list_ctp_z.size() < 1) {
									// 如果不存在虚拟时隙，则虚拟一条
			
									Map map_ctp = new HashMap();
									map_ctp.put("BASE_PTP_ID", port_z);
									map_ctp.put("IS_ETH", CommonDefine.TRUE);
									circuitManagerMapper.insertCtp(map_ctp);
			
			
									if (map_ctp.get("BASE_SDH_CTP_ID") != null) {
										ctp_z = map_ctp.get("BASE_SDH_CTP_ID");
									}
								} else {
									ctp_z = list_ctp_z.get(0).get("BASE_SDH_CTP_ID");
								}
			
							}				
							// 确定完端口以后去验证电路是否已经存在
							select = new HashMap();
							select.put("A_END_CTP", ctp_a);
							if (ctp_z == null) {
								// 如果为null，则是垃圾电路
								select.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
			
							} else {
								select.put("Z_END_CTP", ctp_z);
							}
							List<Map> list_m = circuitManagerMapper.getCircuitInfo(select);
			
							Map map_info = null;
							// 如果存在，则更新电路数
							if (list_m != null && list_m.size() > 0) {
								map_info = list_m.get(0);
			
								// 新建一个map，更新电路数
								Map map_if = new HashMap();
						
								map_if.put("NAME", "t_cir_circuit_info");
								map_if.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
								map_if.put("ID_VALUE", map_info.get("CIR_CIRCUIT_INFO_ID"));
								map_if.put("ID_NAME_2", "CIR_COUNT");
								map_if.put("ID_VALUE_2",
										Integer.parseInt(map_info.get("CIR_COUNT").toString()) + 1);
								circuitManagerMapper.updateByParameter(map_if);
			
							} else {
								// 不存在则新增一条
								Map ma_new = new HashMap();
								ma_new.put("A_END_CTP", ctp_a);
								ma_new.put("Z_END_CTP", ctp_z);
								ma_new.put("A_END_PTP", port_a);
								ma_new.put("Z_END_PTP", port_z);
								
								if (ctp_z == null) {
									// 为空，则是不完整电路插入
									ma_new.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
									map_info = insertPartInfo(ma_new, CommonDefine.CIR_TYPE_ETH,
											mapcir.get("A_END_RATE").toString());
								} else {
									ma_new.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
			
									
									map_info = insertInfo(ma_new, CommonDefine.CIR_TYPE_ETH, mapcir
											.get("A_END_RATE").toString());
			
								}
								// map_info = circuitManagerMapper.getLatestRecord(select);
							}
							// 将原有电路改为子电路
							Map map_sub = new HashMap();
							map_sub.put("NAME", "t_cir_circuit_info");
							map_sub.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
							map_sub.put("ID_VALUE", mapcir.get("CIR_CIRCUIT_INFO_ID"));
							map_sub.put("ID_NAME_2", "PARENT_CIR");
							map_sub.put("ID_VALUE_2", map_info.get("CIR_CIRCUIT_INFO_ID"));
							map_sub.put("ID_NAME_3", "SVC_TYPE");
							map_sub.put("ID_VALUE_3", CommonDefine.CIR_TYPE_ETH);
							map_sub.put("ID_NAME_4", "SELECT_TYPE");
							map_sub.put("ID_VALUE_4", CommonDefine.CIR_SELECT_NO);
			
							circuitManagerMapper.updateByParameter(map_sub);
							// 删除两条查找过的数据
							update = new HashMap();
							update.put("NAME", "t_cir_temp");
							update.put("ID_NAME", "BASE_TEMP_ID");
							update.put("ID_VALUE", mapTemp.get("BASE_TEMP_ID"));
							update.put("ID_NAME_2", "IS_USE");
							update.put("ID_VALUE_2", 1);
							circuitManagerMapper.updateByParameter(update);
							
							if(list_temp!=null&&list_temp.size()>0){
								update = new HashMap();
								update.put("NAME", "t_cir_temp");
								update.put("ID_NAME", "BASE_TEMP_ID");
								update.put("ID_VALUE", list_temp.get(0).get("BASE_TEMP_ID"));
								update.put("ID_NAME_2", "IS_USE");
								update.put("ID_VALUE_2", 1);
								
								circuitManagerMapper.updateByParameter(update);
							}
							
							
						}else{
							// 换z端查询
							select.clear();
							select = hashMapSon("t_cir_circuit_info", "Z_END_CTP", mapTemp.get("BASE_CTP_ID"),null,null, null);
							List<Map> list_cir_z = circuitManagerMapper.getByParameter(select);
							if(list_cir_z!=null&&list_cir_z.size()>0){
								Map mapcir = list_cir_z.get(0);
								// 如果存在父id，则跳过 无需再次绑定
								if(mapcir.get("PARENT_CIR")==null||mapcir.get("PARENT_CIR").toString().isEmpty()){
								}else{
									continue;

								}
								// 需要将azmac口重新赋值
								// 查找到电路后，获取另一端的mac数据
								port_z = port_a;
								ctp_z = ctp_a;
								select.clear();
								select = hashMapSon("t_cir_temp", "BASE_CTP_ID", mapcir.get("A_END_CTP"), null, null, null);
								List<Map> list_temp = circuitManagerMapper.getByParameter(select);
								if(list_temp!=null&&list_temp.size()>0){
									port_a = list_temp.get(0).get("BASE_PTP_ID");
									// 查找ctp表
									select = hashMapSon("t_base_sdh_ctp", "BASE_PTP_ID", port_a,
											null, null, null);
			
									List<Map> list_ctp_a = circuitManagerMapper
											.getByParameter(select);
			
									if (list_ctp_a == null || list_ctp_a.size() < 1) {
										// 如果不存在虚拟时隙，则虚拟一条
			
										Map map_ctp = new HashMap();
										map_ctp.put("BASE_PTP_ID", port_a);
										map_ctp.put("IS_ETH", CommonDefine.TRUE);
										circuitManagerMapper.insertCtp(map_ctp);
			
			
										if (map_ctp.get("BASE_SDH_CTP_ID") != null) {
											ctp_a = map_ctp.get("BASE_SDH_CTP_ID");
										}
									} else {
										ctp_a = list_ctp_a.get(0).get("BASE_SDH_CTP_ID");
									}
			
								}else{
									ctp_a = null;
									port_a = null;
								}
								
								// 确定完端口以后去验证电路是否已经存在
								select = new HashMap();
								select.put("Z_END_CTP", ctp_z);
								if (ctp_a == null) {
									// 如果为null，则是垃圾电路
									select.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
			
								} else {
									select.put("A_END_CTP", ctp_a);
								}
								List<Map> list_m = circuitManagerMapper.getCircuitInfo(select);
			
								Map map_info = null;
								// 如果存在，则更新电路数
								if (list_m != null && list_m.size() > 0) {
									map_info = list_m.get(0);
			
									// 新建一个map，更新电路数
									Map map_if = new HashMap();
							
									map_if.put("NAME", "t_cir_circuit_info");
									map_if.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
									map_if.put("ID_VALUE", map_info.get("CIR_CIRCUIT_INFO_ID"));
									map_if.put("ID_NAME_2", "CIR_COUNT");
									map_if.put("ID_VALUE_2",
											Integer.parseInt(map_info.get("CIR_COUNT").toString()) + 1);
									circuitManagerMapper.updateByParameter(map_if);
			
								} else {
									// 不存在则新增一条
									Map ma_new = new HashMap();
									ma_new.put("A_END_CTP", ctp_a);
									ma_new.put("Z_END_CTP", ctp_z);
									ma_new.put("A_END_PTP", port_a);
									ma_new.put("Z_END_PTP", port_z);
									
									if (ctp_a == null) {
										// 为空，则是不完整电路插入
										ma_new.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
										map_info = insertPartInfo(ma_new, CommonDefine.CIR_TYPE_ETH,
												mapcir.get("A_END_RATE").toString());
									} else {
										ma_new.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
			
										
										map_info = insertInfo(ma_new, CommonDefine.CIR_TYPE_ETH, mapcir
												.get("A_END_RATE").toString());
			
									}
									// map_info = circuitManagerMapper.getLatestRecord(select);
								}
								// 将原有电路改为子电路
								Map map_sub = new HashMap();
								map_sub.put("NAME", "t_cir_circuit_info");
								map_sub.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
								map_sub.put("ID_VALUE", mapcir.get("CIR_CIRCUIT_INFO_ID"));
								map_sub.put("ID_NAME_2", "PARENT_CIR");
								map_sub.put("ID_VALUE_2", map_info.get("CIR_CIRCUIT_INFO_ID"));
								map_sub.put("ID_NAME_3", "SVC_TYPE");
								map_sub.put("ID_VALUE_3", CommonDefine.CIR_TYPE_ETH);
								map_sub.put("ID_NAME_4", "SELECT_TYPE");
								map_sub.put("ID_VALUE_4", CommonDefine.CIR_SELECT_NO);
			
								circuitManagerMapper.updateByParameter(map_sub);
			
								// 删除两条查找过的数据
								update = new HashMap();
								update.put("NAME", "t_cir_temp");
								update.put("ID_NAME", "BASE_TEMP_ID");
								update.put("ID_VALUE", mapTemp.get("BASE_TEMP_ID"));
								update.put("ID_NAME_2", "IS_USE");
								update.put("ID_VALUE_2", 1);
								circuitManagerMapper.updateByParameter(update);
								
								if(list_temp!=null&&list_temp.size()>0){
									update = new HashMap();
									update.put("NAME", "t_cir_temp");
									update.put("ID_NAME", "BASE_TEMP_ID");
									update.put("ID_VALUE", list_temp.get(0).get("BASE_TEMP_ID"));
									update.put("ID_NAME_2", "IS_USE");
									update.put("ID_VALUE_2", 1);
									circuitManagerMapper.updateByParameter(update);
								}
								
							}else{
								// 没有符合的电路
							}
						}
						
						CommonDefine.setProcessParameter(sessionId,id,(i+ 1),(int) (tempCount*1.006+1),"中兴以太网电路生成 ");	
					
					}
				}
			}
		}
//		do {
//			isGo = false;
//			List<Map> list_cir = circuitManagerMapper.selectZTETemp();
//			for(Map mapTemp:list_cir){}
//			// 查询看是否还有数据存在
//			List<Map> list_have = circuitManagerMapper.selectZTETemp();
//			if(list_have!=null&&list_have.size()>0){
//				isGo = true;
//			}
//		}while(isGo);	
		
		return stop;
	}
	/**
	 * 透传板卡电路生成逻辑
	 */
	public void createTouChuan(Map ptp){
		Map select = null;
		Map insert = null;
		Object port_a = null;
		Object ctp_a = null;
		boolean isMaca = true;
		Object port_z = null;
		Object ctp_z = null;
		boolean isMacz = true;
		Map mapCir = null;
		// 在t_base_ptp表中查询PTP_FTP字段为1，BASE_NE_ID和PTP记录A相同，
		// NAME字段为：FTPXXXXX的FTP数据B，其中XXXXX和PTP记录A的NAME字段去除”PTP”字符后相同
		select = hashMapSon("t_base_ptp", "PTP_FTP", CommonDefine.CIR_FTP, "BASE_NE_ID", ptp.get("BASE_NE_ID"), null);
		select.put("ID_NAME_3", "NAME");
		select.put("ID_VALUE_3", ptp.get("BASE_NE_ID").toString().replace("PTP", "FTP"));
		List<Map> list_ptp = circuitManagerMapper.getByParameter(select);
		if(list_ptp!=null&&list_ptp.size()>0){
			// 查询bingdingpath表
			select.clear();
			select = hashMapSon("t_base_binding_path", "VCG_PTP_ID", list_ptp.get(0).get("BASE_PTP_ID"), null, null, null);
			List<Map> list_bangding = circuitManagerMapper.getByParameter(select);
			if(list_bangding!=null&&list_bangding.size()>0){ // 可能有多个数据
				for(Map mapBang : list_bangding){
					// createZTECir(ptp,mapBang);
					// 讲绑定关系插入零时表
					insert = new HashMap();
					insert.put("BASE_MAC_ID", ptp.get("BASE_PTP_ID"));
					insert.put("BASE_CTP_ID", mapBang.get("BASE_SDH_CTP_ID"));
					circuitManagerMapper.insertTemp(insert);
				}
			}
		}
	}
	
	/**
	 * 无uui口
	 */
	public void createNoUUI(Map ptp){
		Map select = null;
		Map insert = null;
			
			//无uui 根据查询到的PTP记录A，获取SLOT ID，在t_base_vb查询此SLOT记录
			//select.clear();
			select = hashMapSon("t_base_vb", "BASE_SLOT_ID", ptp.get("BASE_SLOT_ID"), null, null, null);
			List<Map> list_vb = circuitManagerMapper.getByParameter(select);
			if(list_vb!=null&&list_vb.size()>0){ // ? 会有多个？
				// 是否含有记录a
				boolean isHaveA = false;
				//根据查询到的BASE_VB_ID在t_base_vblist表中查询PTP记录A
				// 查询vb_list 
				select.clear();
				select = hashMapSon("t_base_vb_list", "BASE_VB_ID", list_vb.get(0).get("BASE_VB_ID"), null, null, null);
				List<Map> list_vbList = circuitManagerMapper.getByParameter(select);
				if(list_vbList!=null&&list_vbList.size()>0){
					//在t_base_vblist表中获取和PTP记录A绑定的FTP记录B
					//根据查询到的BASE_VB_ID在t_base_vblist表中查询PTP记录A
					for(Map mapVb : list_vbList){
						// 判断记录a是否在内
						// 判断其他记录是否是ftp
						if(mapVb.get("BASE_PTP_ID").toString().equals(ptp.get("BASE_PTP_ID").toString())){
							isHaveA = true;
						}else{
							// 判断端口是否是FTP口，如果不是,则跳过去
							select.clear();
							// 默认ftp是边界点
							select = hashMapSon("t_base_ptp", "BASE_PTP_ID", mapVb.get("BASE_PTP_ID"), "PTP_FTP", CommonDefine.CIR_FTP, null);
							List<Map> list_ftp = circuitManagerMapper.getByParameter(select);
							if(list_ftp!=null&&list_ftp.size()>0){
								// 获取店口所属的以太网信息进行绑定
								select.clear();
								select = hashMapSon("t_base_binding_path", "VCG_PTP_ID", mapVb.get("BASE_PTP_ID"), null,null, null);
								List<Map> list_path = circuitManagerMapper.getByParameter(select);
								if(list_path!=null&&list_path.size()>0){
									// 走先前的公共流程
									for(Map mapBang : list_path){
										// 讲绑定关系插入零时表
										insert = new HashMap();
										insert.put("BASE_MAC_ID", ptp.get("BASE_PTP_ID"));
										insert.put("BASE_CTP_ID", mapBang.get("BASE_SDH_CTP_ID"));
										circuitManagerMapper.insertTemp(insert);
									}
								}
							}
							
						}
					}
				}
			}
			
	}
	
	/**
	 * 有uui口
	 * @param ptp
	 */
	public void createUUI(Map ptp){
		Map select  = null;
		Map insert = null;
		boolean isHave = false;
		// 获取和PTP记录A绑定的FTP记录B
		//select.clear();
		select = hashMapSon("t_base_ptp", "PTP_FTP", CommonDefine.CIR_FTP, "BASE_NE_ID", ptp.get("BASE_NE_ID"), null);
		select.put("ID_NAME_3", "NAME");
		select.put("ID_VALUE_3", ptp.get("BASE_NE_ID").toString().replace("PTP", "FTP"));
		List<Map> list_ptp = circuitManagerMapper.getByParameter(select);
		if(list_ptp!=null&&list_ptp.size()>0){
			// 根据FTP记录B，获取SLOT ID，在t_base_vb查询此SLOT记录
			select.clear();
			select = hashMapSon("t_base_vb", "BASE_SLOT_ID", list_ptp.get(0).get("BASE_SLOT_ID"), null, null, null);
			List<Map> list_vb = circuitManagerMapper.getByParameter(select);
			if(list_vb!=null&&list_vb.size()>0){
				//根据查询到的BASE_VB_ID在t_base_vblist表中查询FTP记录B
				// 在t_base_vblist表中获取和FTP记录B绑定的FTP记录C
				select.clear();
				select = hashMapSon("t_base_vblist", "BASE_VB_ID", list_vb.get(0).get("BASE_VB_ID"), null, null, null);
				List<Map> list_vblist = circuitManagerMapper.getByParameter(select);
				if(list_vblist!=null&&list_vblist.size()>0){
					for(Map mapVbList:list_vblist){
						if(mapVbList.get("BASE_PTP_ID").toString().equals(list_ptp.get(0).get("BASE_PTP_ID").toString())){
							isHave = true;
						}else{
							// 在t_base_vblist表中获取和FTP记录B绑定的FTP记录C
							select.clear();
							select = hashMapSon("t_base_ptp", "BASE_PTP_ID", mapVbList.get("BASE_PTP_ID"), "PTP_FTP", CommonDefine.CIR_FTP, null);
							List<Map> list_ftp = circuitManagerMapper.getByParameter(select);
							if(list_ftp!=null&&list_ftp.size()>0){
								// 根据获取的FTP记录C在t_base_binding_path表中查询和FTP记录C绑定的PTP记录D
								select.clear();
								 //字段尚未确定,默认只有一个绑定;
								select = hashMapSon("t_base_binding_path", "VCG_PTP_ID", mapVbList.get("BINDING_PTP_ID"), null, null, null);
								List<Map> list_bangd = circuitManagerMapper.getByParameter(select);
								if(list_bangd!=null&&list_bangd.size()>0){
									for(Map mapD : list_bangd){
										if(mapD.get("BINDING_PTP_ID")!=null){
											//根据查到的PTP记录D在t_base_binding_path表中查询值和PTP记录D相同，但PTP_FTP字段为1的PTP记录E绑定的CTP记录
											select.clear();
											select = hashMapSon("t_base_PTP", "BASE_PTP_ID", mapVbList.get("BINDING_PTP_ID"), "PTP_FTP", CommonDefine.CIR_FTP, null);
											List<Map> list_E = circuitManagerMapper.getByParameter(select);
											if(list_E!=null&&list_E.size()>0){
												// 但PTP_FTP字段为1的PTP记录E绑定的CTP记录
												select.clear();
												select = hashMapSon("t_base_binding_path", "VCG_PTP_ID", mapVbList.get("BINDING_PTP_ID"), "PTP_FTP", CommonDefine.CIR_FTP, null);
												List<Map> list_ctp= circuitManagerMapper.getByParameter(select);
												if(list_ctp!=null&&list_ctp.size()>0){
													for(Map mapBang : list_ctp){
														insert = new HashMap();
														insert.put("BASE_MAC_ID", ptp.get("BASE_PTP_ID"));
														insert.put("BASE_CTP_ID", mapBang.get("BASE_SDH_CTP_ID"));
														circuitManagerMapper.insertTemp(insert);
													}
												}
											}
										}
									}
									
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ICircuitManagerService#selectCircuitLast(int,
	 *      int)
	 */
	@Override
	public Map<String, Object> selectCircuitLast(int type, int start, int limit)
			throws CommonException {
		Map select = new HashMap();
		select.put("start", start);
		select.put("limit", limit);

		Map<String, Object> result_map = new HashMap<String, Object>();

		List<Map> list = null;
		Map total = null;
		// 判断是何种电路，默认是SDh
		if (type == CommonDefine.CIR_TYPE_ETH) {
			// ETH
			select.put("SELECT_TYPE", CommonDefine.CIR_SELECT_YES);
			select.put("SVC_TYPE", CommonDefine.CIR_TYPE_ETH);
			list = circuitManagerMapper.selectCircuit(select);
			total = circuitManagerMapper.selectCircuitTotal(select);
		} else if (type == CommonDefine.CIR_TYPE_OTN) {
			// OTN
			select.put("SELECT_TYPE", CommonDefine.CIR_SELECT_YES);
			select.put("SVC_TYPE", CommonDefine.CIR_TYPE_OTN);
			// FIXME:change func
			list = circuitManagerMapper.selectOtnCircuit(select);
			total = circuitManagerMapper.selectOtnCircuitTotal(select);
		} else if (type == CommonDefine.CIR_TYPE_PTN) {
			// PTN
			select.put("SELECT_TYPE", CommonDefine.CIR_SELECT_YES);
			select.put("SVC_TYPE", CommonDefine.CIR_TYPE_PTN);
			// FIXME:change func
			list = circuitManagerMapper.selectPtnCircuit(select);
			total = circuitManagerMapper.selectPtnCircuitTotal(select);
		} else {
			// SDh
			select.put("SELECT_TYPE", CommonDefine.CIR_SELECT_YES);
			select.put("SVC_TYPE", CommonDefine.CIR_TYPE_SDH);
			list = circuitManagerMapper.selectCircuit(select);
			total = circuitManagerMapper.selectCircuitTotal(select);
		}

		result_map
				.put("total", Integer.parseInt(total.get("total").toString()));
		result_map.put("rows", list);
		// 查询最近生成的电路

		return result_map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ICircuitManagerService#getAllGroup()
	 */
	@Override
	public List<Map> getAllGroup(Map map) throws CommonException {
		if (map.get("userId") == null
				|| "null".equals(map.get("userId").toString())
				|| map.get("userId").toString().isEmpty()) {
			throw new CommonException(new Exception(),
					MessageCodeDefine.USER_LOGIN_AGAIN);
		}
		List<Map> listGroup = commonManagerService.getAllEmsGroups(
				Integer.parseInt(map.get("userId").toString()), true, true,false);
		// List<Map> listGroup = circuitManagerMapper.getAllGroup();
		// // 将全部放入list
		// Map map = new HashMap();
		// map.put("BASE_EMS_GROUP_ID", 0);
		// map.put("GROUP_NAME", "全部");
		// listGroup.add(0, map);
		// map = new HashMap();
		// map.put("BASE_EMS_GROUP_ID", -1);
		// map.put("GROUP_NAME", "无");
		// listGroup.add(map);
		return listGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.ICircuitManagerService#setCircuitTaskHold(java.util
	 * .List)
	 */
	@Override
	public void setCircuitTaskHold(List<Map> list_map) throws CommonException,
			ParseException {
		for (Map map : list_map) {
			// 结束当前任务
			String key = map.get("SYS_TASK_ID").toString() + "_newCir";
			if (CommonDefine.PROCESS_MAP.containsKey(key)) {
				CommonDefine.PROCESS_MAP.get(key).setCanceled(true);
			}
			Map select = hashMapSon("t_sys_task", "SYS_TASK_ID",
					map.get("SYS_TASK_ID"), null, null, null);

			// 查询出当前任务
			List<Map> list = circuitManagerMapper.getByParameter(select);

			Map map_t = list.get(0);

			// 判断当前任务是否正在执行
			if (map_t.get("RESULT") != null
					&& Integer.parseInt(map_t.get("RESULT").toString()) == CommonDefine.TASK_ON) {
				// 先暂停进程，再更新
				map.put("RESULT", CommonDefine.TASK_HOLD);
			}

			// String next = calculateDate(map.get("PERIOD").toString(),
			// map.get(
			// "PERIOD_TYPE").toString());
			// map.put("NEXT_TIME", next);
			circuitManagerMapper.updateTask(map);

			// 添加一个quartz任务
			quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_CIRCUIT,
					Integer.parseInt(map.get("SYS_TASK_ID").toString()),
					CommonDefine.QUARTZ.JOB_PAUSE);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.ICircuitManagerService#setCircuitTaskOn(java.util
	 * .List)
	 */
	@Override
	public void setCircuitTaskOn(List<Map> list_map) throws CommonException {
		Map select = null;
		try {
			for (Map map : list_map) {
				// 增加任务是否正在执行的判断

				// String next = calculateDate(map.get("PERIOD").toString(), map
				// .get("PERIOD_TYPE").toString());
				// map.put("NEXT_TIME", next);
				circuitManagerMapper.updateTask(map);
				// 添加一个quartz任务
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_CIRCUIT,
						Integer.parseInt(map.get("SYS_TASK_ID").toString()),
						CommonDefine.QUARTZ.JOB_RESUME);

			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_PARSE);
		}

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
						+ Integer.parseInt(ff[1]) * 60
						+ Integer.parseInt(ff[2]);
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
						+ Integer.parseInt(ff[1]) * 60
						+ Integer.parseInt(ff[2]);
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
				calendar.set(Calendar.DATE, calendar.get(Calendar.DAY_OF_MONTH));
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
								+ Integer.parseInt(ff[1]) * 60
								+ Integer.parseInt(ff[2]);
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
					calendar.set(Calendar.MONTH, current_month + plan_month - 1);
					calendar.set(Calendar.MONTH, current_month + plan_month - 1);
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
								+ Integer.parseInt(ff[1]) * 60
								+ Integer.parseInt(ff[2]);
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
								+ Integer.parseInt(ff[1]) * 60
								+ Integer.parseInt(ff[2]);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ICircuitManagerService#setCycle(java.util.Map)
	 */
	@Override
	public void setCycle(Map map) throws CommonException {
		// 判断时间
		String cronExpression = "";
		String[] time = map.get("PERIOD").toString().split(",");
		if (time != null && time.length >= 6) {
			String[] hms = time[5].split(":");
			if (hms != null && hms.length == 3) {
				cronExpression = hms[2] + " " + hms[1] + " " + hms[0] + " ";
			} else {
				cronExpression = "0 0 3 ";
			}
		} else {
			cronExpression = "0 0 3 ";
		}

		// 判断执行周期
		if ("1".equals(map.get("PERIOD_TYPE").toString())) {
			// 每日执行
			cronExpression += "* * ?";
		} else if ("2".equals(map.get("PERIOD_TYPE").toString())) {
			// 每周执行
			cronExpression += "? * " + time[3];
		} else {
			cronExpression += time[4] + " * ?";
			// 每月执行
		}
		// 修改quartz的时间
		// "0 15 10 * * ?" Fire at 10:15am every day
		// 添加一个quartz任务
		quartzManagerService.modifyJobTime(CommonDefine.QUARTZ.JOB_CIRCUIT,
				Integer.parseInt(map.get("SYS_TASK_ID").toString()),
				cronExpression);

		circuitManagerMapper.updateTask(map);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.ICircuitManagerService#setBeginTime(java.util.Map)
	 */
	@Override
	public Map setBeginTime(Map map) throws CommonException {
		Map mapp = new HashMap();
		try {

			String next = calculateDate(map.get("PERIOD").toString(),
					map.get("PERIOD_TYPE").toString());
			// 从quartz获取下次执行时间
			mapp.put("NEXT_TIME", next);
			mapp.put("returnResult", CommonDefine.SUCCESS);
		} catch (ParseException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_PARSE);
		}
		return mapp;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fujitsu.IService.ICircuitManagerService#checkTask(java.util.List)
	 */
	@Override
	public Map checkTask(List<Map> list_map) throws CommonException {

		Map<String, Object> map_return = new HashMap<String, Object>();
		Map select = null;
		boolean is_have = false;
		for (Map map : list_map) {
			// 增加任务是否正在执行的判断
			// 新增查询条件
			select = hashMapSon("t_sys_task", "SYS_TASK_ID",
					map.get("SYS_TASK_ID"), null, null, null);

			// 查询出当前任务
			List<Map> list = circuitManagerMapper.getByParameter(select);

			Map map_t = list.get(0);

			// 判断当前任务是否正在执行
			if (map_t.get("RESULT") != null
					&& Integer.parseInt(map_t.get("RESULT").toString()) == CommonDefine.TASK_ON) {
				is_have = true;
				map_return.put("DISPLAY_NAME", map.get("DISPLAY_NAME"));
				map_return.put("returnResult", CommonDefine.FAILED);
				map_return.put("returnMessage", "");
				return map_return;
			}

		}
		map_return.put("returnResult", CommonDefine.SUCCESS);
		map_return.put("returnMessage", "");
		return map_return;
	}
	

	// 链路删除电路删除
	public void deleteCirByLinkDelete() {
		Map select = null;
		// 查找出被删除的link
		select = hashMapSon("t_base_link", "IS_DEL", CommonDefine.TRUE,
				"CHANGE_STATE", CommonDefine.STATE_DELETE_LATEST, null);

		List<Map> list = circuitManagerMapper.getByParameter(select);
		// 遍历删除的link
		if (list != null && list.size() > 0) {
			for (Map map : list) {
				// 判断是内部link还是外部link
				if (Integer.parseInt(map.get("LINK_TYPE").toString()) == CommonDefine.LINK_OUT) {

					// 获取经过此条链路的理由信息
					select = new HashMap();
					select.put("CHAIN_ID", Integer.parseInt(map.get(
							"BASE_LINK_ID").toString()));
					select.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_OUT_LINK);

					// 查询出相关电路
					List<Map> list_cir = circuitManagerMapper
							.getDeleteCir(select);
					// 删除电路
					deleteByCirId(list_cir);

					// 如果是otn ，先查询
					select = new HashMap();
					select.put("CHAIN_ID", Integer.parseInt(map.get(
							"BASE_LINK_ID").toString()));
					select.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_OUT_LINK);

					// 查询出相关电路
					List<Map> listOtncir = circuitManagerMapper
							.getDeleteOtnCir(select);
					// 删除电路
					deleteByOtnCirId(listOtncir);

				} else if (Integer.parseInt(map.get("LINK_TYPE").toString()) == CommonDefine.LINK_IN) {
					// 内部link 肯定是otn电路，查找otn交叉
					select = hashMapSon("t_base_otn_crs_relation", "CHAIN_ID",
							map.get("BASE_LINK_ID"), "CHAIN_TYPE",
							CommonDefine.CHAIN_TYPE_IN_LINK, null);
					List<Map> listOtnVir = circuitManagerMapper
							.getByParameter(select);
					for (Map mapOtn : listOtnVir) {
						select = hashMapSon("t_cir_otn_circuit_route",
								"CHAIN_ID", mapOtn.get("VIR_CRS_ID"),
								"CHAIN_TYPE", CommonDefine.CHAIN_TYPE_OTN_CRS,
								null);
						List<Map> listOtnCir = circuitManagerMapper
								.getOtnCirByCrs(select);
						deleteByOtnCirId(listOtnCir);
						// 清楚虚拟交叉和虚拟交叉关系表
						select = hashMapSon("t_base_otn_crs_relation",
								"VIR_CRS_ID", map.get("VIR_CRS_ID"), null,
								null, null);
						List<Map> listOtncrs = circuitManagerMapper
								.getByParameter(select);
					}

				}

			}
		}

	}

	// 链路新增电路删除，
	public void deleteCirByLinkAdd() {
		Map select = null;
		// 查找出被删除的link
		select = hashMapSon("t_base_link", "CHANGE_STATE",
				CommonDefine.STATE_ADD_LATEST, null,null, null);
		List<Map> list = circuitManagerMapper.getByParameter(select);

		if (list != null && list.size() > 0) {
			// 遍历link，
			for (Map map : list) {
				// 外部链路
				if (Integer.parseInt(map.get("LINK_TYPE").toString()) == CommonDefine.LINK_OUT) {
					// 挨个查找端口所经过的电路
					select = new HashMap();
					select.put("A_END_PTP", map.get("A_END_PTP"));

					List<Map> list_aa = circuitManagerMapper
							.getCirByPort(select);
					System.out.println("list_aa=="+list_aa.size());
					deleteByCirId(list_aa);

					select = new HashMap();
					select.put("A_END_PTP", map.get("Z_END_PTP"));
					List<Map> list_az = circuitManagerMapper
							.getCirByPort(select);
					System.out.println("list_az=="+list_az.size());
					deleteByCirId(list_az);
					
					select = new HashMap();
					select.put("Z_END_PTP", map.get("A_END_PTP"));
					List<Map> list_za = circuitManagerMapper
							.getCirByPort(select);
					System.out.println("list_za=="+list_za.size());
					deleteByCirId(list_za);
					
					select = new HashMap();
					select.put("Z_END_PTP", map.get("Z_END_PTP"));
					List<Map> list_zz = circuitManagerMapper
							.getCirByPort(select);
					System.out.println("list_zz=="+list_zz.size());
					deleteByCirId(list_zz);

					// 删除otn交叉
					select = new HashMap();
					select.put("Z_END_PTP", map.get("A_END_PTP"));
					List<Map> listOtn = circuitManagerMapper
							.getOtnCirByPort(select);
					deleteByOtnCirId(listOtn);

					select = new HashMap();
					select.put("A_END_PTP", map.get("Z_END_PTP"));
					List<Map> listOtnZ = circuitManagerMapper
							.getOtnCirByPort(select);
					deleteByOtnCirId(listOtnZ);

				} else if (Integer.parseInt(map.get("LINK_TYPE").toString()) == CommonDefine.LINK_IN) {
					// 内部link ,查找两个端口所经过的虚拟交叉

					select = hashMapSon("t_base_otn_crs_relation", "CHAIN_ID",
							map.get("BASE_LINK_ID"), "CHAIN_TYPE",
							CommonDefine.CHAIN_TYPE_IN_LINK, null);
					List<Map> listOtnVir = circuitManagerMapper
							.getByParameter(select);
					for (Map mapOtn : listOtnVir) {
						// 挨个查找端口所经过的电路
						select = new HashMap();
						select.put("A_END_PTP", map.get("A_END_PTP"));

						List<Map> list_a = circuitManagerMapper
								.getOtnVirByPort(select);
						// 根据电路id 删除电路
						for (Map mapVir : list_a) {
							select = hashMapSon("t_cir_otn_circuit_route",
									"CHAIN_ID", mapVir.get("VIR_CRS_ID"),
									"CHAIN_TYPE",
									CommonDefine.CHAIN_TYPE_OTN_CRS, null);
							List<Map> listOtnCir = circuitManagerMapper
									.getOtnCirByCrs(select);
							deleteByOtnCirId(listOtnCir);
						}

						select = new HashMap();
						select.put("Z_END_PTP", map.get("Z_END_PTP"));
						List<Map> list_z = circuitManagerMapper
								.getOtnVirByPort(select);
						// 根据电路id 删除电路
						for (Map mapVir : list_z) {
							select = hashMapSon("t_cir_otn_circuit_route",
									"CHAIN_ID", mapVir.get("VIR_CRS_ID"),
									"CHAIN_TYPE",
									CommonDefine.CHAIN_TYPE_OTN_CRS, null);
							List<Map> listOtnCir = circuitManagerMapper
									.getOtnCirByCrs(select);
							deleteByOtnCirId(listOtnCir);
						}
					}

				}

			}
		}
	}

	// 交叉连接删除电路删除
	public void deleteCirByCrsDelete() {
		Map select = null;
		// 查找出被删除的交叉
		select = hashMapSon("t_base_sdh_crs", "IS_DEL", CommonDefine.TRUE,
				"CHANGE_STATE", CommonDefine.STATE_DELETE_LATEST, null);
		List<Map> list = circuitManagerMapper.getByParameter(select);
		if (list != null && list.size() > 0) {
			for (Map map : list) {

				select = new HashMap();
				select.put("CHAIN_ID",
						Integer.parseInt(map.get("BASE_SDH_CRS_ID").toString()));
				select.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_SDH_CRS);

				// 查询经过次交叉连接的电路
				List<Map> list_crs = circuitManagerMapper.getCirByCrs(select);
				// 根据电路id 删除电路
				deleteByCirId(list_crs);
			}
		}

		// 删除otn的电路
		select = hashMapSon("t_base_otn_crs", "IS_DEL", CommonDefine.TRUE,
				"CHANGE_STATE", CommonDefine.STATE_DELETE_LATEST, null);
		List<Map> listOtn = circuitManagerMapper.getByParameter(select);
		for (Map map : listOtn) {
			// 验证是否被虚拟交叉占用
			select = hashMapSon("t_base_otn_crs_relation", "CHAIN_ID",
					map.get("BASE_OTN_CRS_ID"), "CHAIN_TYPE",
					CommonDefine.CHAIN_TYPE_OTN_CRS, null);
			List<Map> listOtnVir = circuitManagerMapper.getByParameter(select);
			for (Map mapVir : listOtnVir) {
				select = hashMapSon("t_cir_otn_circuit_route", "CHAIN_ID",
						mapVir.get("VIR_CRS_ID"), "CHAIN_TYPE",
						CommonDefine.CHAIN_TYPE_OTN_CRS, null);
				List<Map> listOtnCir = circuitManagerMapper
						.getOtnCirByCrs(select);
				deleteByOtnCirId(listOtnCir);
			}
		}

	}

	// 交叉连接新增电路删除
	public void deleteCirByCrsAdd() {
		Map select = null;
		// 查找出新增的交叉
		select = hashMapSon("t_base_sdh_crs", "CHANGE_STATE",
				CommonDefine.STATE_ADD_LATEST, null, null, null);
		List<Map> list = circuitManagerMapper.getByParameter(select);
		if (list != null && list.size() > 0) {
			for (Map map : list) {

				select = new HashMap();
				select.put("Z_END_PTP", map.get("A_END_PTP"));

				List<Map> list_a = circuitManagerMapper.getCirByPortCrs(select);
				// 根据电路id 删除电路
				deleteByCirId(list_a);

				select = new HashMap();
				select.put("A_END_PTP", map.get("Z_END_PTP"));
				List<Map> list_z = circuitManagerMapper.getCirByPortCrs(select);
				// 根据电路id 删除电路
				deleteByCirId(list_z);
			}
		}

		// 查找出新增的otn交叉
		select = hashMapSon("t_base_otn_crs", "IS_VIRTUAL", CommonDefine.FALSE,
				"CHANGE_STATE", CommonDefine.STATE_ADD_LATEST, null);
		List<Map> listOtn = circuitManagerMapper.getByParameter(select);
		// 查处所经过的otn虚拟交叉
		for (Map map : listOtn) {
			select = hashMapSon("t_base_otn_crs_relation", "CHAIN_ID",
					map.get("BASE_OTN_CRS_ID"), "CHAIN_TYPE",
					CommonDefine.CHAIN_TYPE_OTN_CRS, null);
			List<Map> listOtnVir = circuitManagerMapper.getByParameter(select);
			for (Map mapOtn : listOtnVir) {
				select = hashMapSon("t_base_otn_crs", "BASE_OTN_CRS_ID",
						mapOtn.get("VIR_CRS_ID"), null, null, null);
				Map mapVir = circuitManagerMapper.getByParameter(select).get(0);

				select = new HashMap();
				select.put("A_END_PTP", mapVir.get("A_END_PTP"));

				List<Map> list_a = circuitManagerMapper.getOtnCirByPort(select);
				// 根据电路id 删除电路
				deleteByOtnCirId(list_a);

				select = new HashMap();
				select.put("Z_END_PTP", mapVir.get("Z_END_PTP"));
				List<Map> list_z = circuitManagerMapper.getOtnCirByPort(select);
				// 根据电路id 删除电路
				deleteByOtnCirId(list_z);
			}

		}
	}

	/**
	 * 根据电路id删除电路相关信息
	 * 
	 * @param list_b
	 */
	public void deleteByCirId(List<Map> list) {
		Map select = null;
		// 查看电路是否是双向的
		if (list != null && list.size() > 0) {
			for (Map mapCir : list) {
				select = hashMapSon("t_cir_circuit", "CIR_CIRCUIT_INFO_ID",
						mapCir.get("CIR_CIRCUIT_INFO_ID"), null, null, null);
				List<Map> listCir = circuitManagerMapper.getByParameter(select);
				for (Map ma : listCir) {
					// 更新交叉连接数据
					select = hashMapSon("t_cir_circuit_route",
							"CIR_CIRCUIT_ID", ma.get("CIR_CIRCUIT_ID"),
							"CHAIN_TYPE", CommonDefine.CHAIN_TYPE_SDH_CRS, null);
					List<Map> listCrs = circuitManagerMapper
							.getByParameter(select);
					for (Map mapCrs : listCrs) {
						select = hashMapSon("t_base_sdh_crs",
								"BASE_SDH_CRS_ID", mapCrs.get("CHAIN_ID"),
								null, null, null);
						Map mapcr = circuitManagerMapper.getByParameter(select)
								.get(0);
						Map update = new HashMap();
						update.put("NAME", "t_base_sdh_crs");
						update.put("ID_NAME", "BASE_SDH_CRS_ID");
						update.put("ID_VALUE", mapCrs.get("CHAIN_ID"));
						update.put("ID_NAME_2", "CIRCUIT_COUNT");
						update.put("ID_VALUE_2", Integer.parseInt(mapcr.get(
								"CIRCUIT_COUNT").toString()) - 1);
						circuitManagerMapper.updateByParameter(update);
					}
					// 删除路由信息
					select = hashMapSon("t_cir_circuit_route",
							"CIR_CIRCUIT_ID", ma.get("CIR_CIRCUIT_ID"), null,
							null, null);
					circuitManagerMapper.deleteByParameter(select);

					// 更新电路信息表信息，查询信息表
					select = hashMapSon("t_cir_circuit_info",
							"CIR_CIRCUIT_INFO_ID",
							ma.get("CIR_CIRCUIT_INFO_ID"), null, null, null);
					List<Map> list_info = circuitManagerMapper
							.getByParameter(select);
					if (list_info != null && list_info.size() > 0) {
						Map map_info = list_info.get(0);
						// 判断含有的电路数是否为0
						if (map_info.get("CIR_COUNT") != null
								&& Integer.parseInt(map_info.get("CIR_COUNT")
										.toString()) > 0) {
							Map update = new HashMap();
							int count = Integer.parseInt(map_info.get(
									"CIR_COUNT").toString()) - 1;
							update.put("NAME", "t_cir_circuit_info");
							update.put("ID_NAME", "CIR_CIRCUIT_INFO_ID");
							update.put("ID_VALUE",
									map_info.get("CIR_CIRCUIT_INFO_ID"));
							update.put("ID_NAME_2", "CIR_COUNT");
							if (count == 0) {
								update.put("ID_VALUE_2", "0");
							} else {
								update.put("ID_VALUE_2", count);
							}

							// 如果子电路下，没有电路存在，则将父电路清空，同时将父电路的通过电路数减1
							if (count == 0) {
								update.put("ID_NAME_2", "PARENT_CIR");
								update.put("ID_VALUE_2", "0");
								// 如果是以太网的子电路，则还需要更新父电路
								if (Integer.parseInt(map_info
										.get("SELECT_TYPE").toString()) == CommonDefine.CIR_SUB) {
									Map map_f = hashMapSon(
											"t_cir_circuit_info",
											"CIR_CIRCUIT_INFO_ID",
											Integer.parseInt(map_info.get(
													"PARENT_CIR").toString()),
											null, null, null);
									List<Map> list_parent = circuitManagerMapper
											.getByParameter(map_f);

									if (list_parent != null
											&& list_parent.size() > 0) {
										Map map_parent = list_parent.get(0);
										if (map_parent.get("CIR_COUNT") != null
												&& Integer.parseInt(map_parent
														.get("CIR_COUNT")
														.toString()) > 0) {
											Map updateParent = new HashMap();
											updateParent.put("NAME",
													"t_cir_circuit_info");
											updateParent.put("ID_NAME",
													"CIR_CIRCUIT_INFO_ID");
											updateParent
													.put("ID_VALUE",
															map_parent
																	.get("CIR_CIRCUIT_INFO_ID"));
											updateParent.put("ID_NAME_2",
													"CIR_COUNT");
											if ((Integer.parseInt(map_parent
													.get("CIR_COUNT")
													.toString()) - 1) == 0) {
												updateParent.put("ID_VALUE_2",
														"0");
											} else {
												updateParent
														.put("ID_VALUE_2",
																Integer.parseInt(map_parent
																		.get("CIR_COUNT")
																		.toString()) - 1);
											}

											circuitManagerMapper
													.updateByParameter(updateParent);
										}
									}
								}
							}
							circuitManagerMapper.updateByParameter(update);

						}
					}

					// 删除电路表信息
					select = hashMapSon("t_cir_circuit", "CIR_CIRCUIT_ID",
							ma.get("CIR_CIRCUIT_ID"), null, null, null);
					circuitManagerMapper.deleteByParameter(select);
				}

			}
		}
	}

	/**
	 * 根据电路id删除Otn电路相关信息
	 * 
	 * @param list_b
	 */
	public void deleteByOtnCirId(List<Map> list) {
		Map select = null;
		if (list != null && list.size() > 0) {
			for (Map ma : list) {

				// 删除路由信息
				select = hashMapSon("t_cir_otn_circuit_route",
						"CIR_OTN_CIRCUIT_ID", ma.get("CIR_OTN_CIRCUIT_ID"),
						null, null, null);
				circuitManagerMapper.deleteByParameter(select);

				// 更新电路信息表信息，查询信息表
				select = hashMapSon("t_cir_otn_circuit_info",
						"CIR_OTN_CIRCUIT_INFO_ID",
						ma.get("CIR_OTN_CIRCUIT_INFO_ID"), null, null, null);
				List<Map> list_info = circuitManagerMapper
						.getByParameter(select);
				if (list_info != null && list_info.size() > 0) {
					Map map_info = list_info.get(0);
					// 判断含有的电路数是否为0
					if (map_info.get("CIR_COUNT") != null
							&& Integer.parseInt(map_info.get("CIR_COUNT")
									.toString()) > 0) {
						Map update = new HashMap();
						int count = Integer.parseInt(map_info.get("CIR_COUNT")
								.toString()) - 1;
						update.put("NAME", "t_cir_otn_circuit_info");
						update.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
						update.put("ID_VALUE",
								map_info.get("CIR_OTN_CIRCUIT_INFO_ID"));
						update.put("ID_NAME_2", "CIR_COUNT");
						update.put("ID_VALUE_2", count);
						circuitManagerMapper.updateByParameter(update);

					}
				}

				// 删除电路表信息
				select = hashMapSon("t_cir_otn_circuit", "CIR_OTN_CIRCUIT_ID",
						ma.get("CIR_OTN_CIRCUIT_ID"), null, null, null);
				circuitManagerMapper.deleteByParameter(select);

			}
		}
	}

	/**
	 * 将华为otn交叉生成虚拟交叉
	 */
	public boolean createHWOtnCrs1(Map mapSelect, String id, String sessionId,
			int countSdh, int countOtn) {
		boolean stop = false;
		Map select = null;
		Map insert = null;
		Map update = null;
		// 更新交叉连接查询状态 1 是查找过 0 是没有查找过
		update = new HashMap();
		update.put("NAME", "t_base_otn_crs");
		update.put("ID_NAME", "IS_DEL");
		update.put("ID_VALUE", CommonDefine.FALSE);
		update.put("ID_NAME_2", "IS_IN_CIRCUIT");
		update.put("ID_VALUE_2", CommonDefine.FALSE);

		circuitManagerMapper.updateByParameter(update);

		// 查询a端ptp且为边界点的交叉连接
		// 统计总数
		// Map map_count = circuitManagerMapper.getOtnCrsCirTotal(mapSelect);

		// int count = Integer.parseInt(map_count.get("total").toString());
		if (countOtn > 0) {
			for (int i = 0; i <= (countOtn - 1) / 1000; i++) {
				// 每次取1000条,如果后期查询速度慢，再换其他机制
				List<Map> list = circuitManagerMapper.getOtnCrsCir(mapSelect,
						0, 1000);
				if (list != null && list.size() > 0) {
					for (int j = 0; j < list.size(); j++) {
						Map map = list.get(j);
						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						// 进度描述信息更改--此处修改
						/*String text = "当前进度" + (i * 1000 + j + 1 + countSdh)
								+ "/" + (countSdh + countOtn);
						if ("newCir".equals(id)) {
							text = (i * 1000 + j + 1 + countSdh) + "/"
									+ (countSdh + countOtn);
						}
						// 加入进度值
						CommonDefine.setProcessParameter(
								sessionId,
								id,
								text,
								Double.valueOf((i * 1000 + j + 1 + countSdh)
										/ ((double) (countSdh + countOtn))));*/
						CommonDefine.setProcessParameter(
								sessionId,
								id,
								(i * 1000 + j + 1 + countSdh),
								(int) ((countSdh + countOtn)*1.006+1),
								"otn电路生成 ");

						// 先更新是否被查找状态
						update = new HashMap();
						update.put("NAME", "t_base_otn_crs");
						update.put("ID_NAME", "BASE_OTN_CRS_ID");
						update.put("ID_VALUE", map.get("BASE_OTN_CRS_ID"));
						update.put("ID_NAME_2", "IS_IN_CIRCUIT");
						update.put("ID_VALUE_2", CommonDefine.TRUE);
						circuitManagerMapper.updateByParameter(update);

						Map temp = null;
						Map map_otn = map;

						// 插入一条otn电路信息
						insert = new HashMap();
						insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
						insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
						insert.put("CIR_COUNT", 1);

						insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
						// 新增cirNo
						Map map_maxNo = circuitManagerMapper
								.getMaxOtnCircuitNo();
						if (map_maxNo != null) {
							if (map_maxNo.get("CIR_NO") != null
									&& !map_maxNo.get("CIR_NO").toString()
											.isEmpty()) {
								insert.put(
										"CIR_NO",
										((Integer.parseInt(map_maxNo.get(
												"CIR_NO").toString()) + 1) + ""));
							} else {
								insert.put("CIR_NO", "100000");
							}
						} else {
							insert.put("CIR_NO", "100000");
						}
						circuitManagerMapper.insertOtnInfo(insert);

						// select = new HashMap();
						// select.put("NAME", "t_cir_otn_circuit_info");
						// select.put("IN_NAME", "A_END_CTP");
						// select.put("IN_VALUE", map_otn.get("A_END_CTP"));
						// select.put("ID", "CIR_OTN_CIRCUIT_INFO_ID");
						//
						// Map info =
						// circuitManagerMapper.getLatestRecord(select);
						// 获取插入数据Id
						Integer circuitInfoId = Integer.valueOf(insert.get(
								"CIR_OTN_CIRCUIT_INFO_ID").toString());

						// 新建一条otn电路
						insert = new HashMap();
						insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
						insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
						insert.put("IS_MAIN_CIR", CommonDefine.TRUE);
						insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
						insert.put("CIR_OTN_CIRCUIT_INFO_ID", circuitInfoId);
						Map map_otn_cir = insertOtnCir(insert);

						// 插入首条路由信息
						// 将链路存入路由
						insert = new HashMap();
						insert.put("CIR_OTN_CIRCUIT_ID",
								map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
						insert.put("CHAIN_ID", map_otn.get("BASE_OTN_CRS_ID"));
						insert.put("CHAIN_TYPE",
								CommonDefine.CHAIN_TYPE_OTN_CRS);

						insert.put("AHEAD_CRS_ID",
								map_otn.get("BASE_OTN_CRS_ID"));
						insert.put("IS_COMPLETE", CommonDefine.TRUE);
						circuitManagerMapper.insertOtnRoute(insert);
						// 单条电路生成
						CreateSingleOtnCrs(map_otn, map_otn_cir, temp);

						// 查找支路未完成的虚拟交叉
						select = hashMapSon("t_cir_otn_circuit_route",
								"IS_COMPLETE", CommonDefine.FALSE, null, null,
								null);

						List<Map> list_false = new ArrayList<Map>();
						boolean isGoOn = true;
						do {
							list_false = circuitManagerMapper
									.getByParameter(select);

							if (list_false != null && list_false.size() > 0) {
								temp = new HashMap();
								// 取出最小的一条处理
								Map map_flase = list_false.get(0);
								Map map_s = new HashMap();
								map_s.put("VIR_CRS_ID",
										map_flase.get("VIR_CRS_ID"));
								map_s.put("RELATION_ID",
										map_flase.get("RELATION_ID"));
								map_s.put("VIR_CRS_ID",
										map_flase.get("VIR_CRS_ID"));
								map_s.put("BASE_OTN_CRS_RELATION_ID", map_flase
										.get("BASE_OTN_CRS_RELATION_ID"));
								select = new HashMap();
								select.put("CIR_OTN_CIRCUIT_ID",
										map_flase.get("CIR_OTN_CIRCUIT_ID"));
								select.put(
										"CIR_OTN_CIRCUIT_ROUTE_ID",
										map_flase
												.get("CIR_OTN_CIRCUIT_ROUTE_ID"));
								// 查询出id小于当前id且
								List<Map> list_before = circuitManagerMapper
										.getOtnBefore(select);
								if (list_before != null
										&& list_before.size() > 0) {
									// 先查询出电路的信息
									select = hashMapSon(
											"t_cir_otn_circuit",
											"CIR_OTN_CIRCUIT_ID",
											list_before.get(0).get(
													"CIR_OTN_CIRCUIT_ID"),
											null, null, null);

									Map cir = circuitManagerMapper
											.getByParameter(select).get(0);

									// 插入一条新的电路去数据库
									insert = new HashMap();
									insert.put("CIR_OTN_CIRCUIT_ID",
											cir.get("CIR_OTN_CIRCUIT_ID"));
									insert.put("A_END_CTP",
											cir.get("A_END_CTP"));
									insert.put("A_END_PTP",
											cir.get("A_END_PTP"));

									Map map_cir = insertOtnCir(insert);

									// 遍历插入数据
									for (Map map_before : list_before) {
										insert = new HashMap();
										insert.put(
												"CIR_OTN_CIRCUIT_ROUTE_ID",
												map_before
														.get("CIR_OTN_CIRCUIT_ROUTE_ID"));
										insert.put(
												"CIR_OTN_CIRCUIT_ID",
												map_cir.get("CIR_OTN_CIRCUIT_ID"));
										insert.put("IS_COMPLETE",
												CommonDefine.TRUE);
										insert.put("CHAIN_ID",
												map_before.get("CHAIN_ID"));
										insert.put("CHAIN_TYPE",
												map_before.get("CHAIN_TYPE"));
										insert.put("AHEAD_CRS_ID",
												map_before.get("AHEAD_CRS_ID"));
										circuitManagerMapper
												.insertOtnRoute(insert);

										// 判断是否是交叉连接，如果是交叉连接，则将az端的值赋给全局变量
										if (Integer.parseInt(map_before.get(
												"CHAIN_TYPE").toString()) == CommonDefine.CHAIN_TYPE_OTN_CRS) {
											select = hashMapSon(
													"t_base_otn_crs",
													"BASE_OTN_CRS_ID",
													map_before.get("CHAIN_ID"),
													null, null, null);
											Map crs = circuitManagerMapper
													.getByParameter(select)
													.get(0);
											// a端
											temp.put("OS", crs.get("A_OS"));
											temp.put("OTS", crs.get("A_OTS"));
											temp.put("OMS", crs.get("A_OMS"));
											temp.put("OCH", crs.get("A_OCH"));
											temp.put("ODU0", crs.get("A_ODU0"));
											temp.put("ODU1", crs.get("A_ODU1"));
											temp.put("ODU2", crs.get("A_ODU2"));
											temp.put("ODU3", crs.get("A_ODU3"));
											temp.put("DSR", crs.get("A_DSR"));
											// 如果oms 有2出现，则记录在OMS2
											if (crs.get("A_OMS") != null
													&& "2".equals(crs.get(
															"A_OMS").toString())) {
												temp.put("OMS2", 2);
											}
											// z端
											temp.put("OS", crs.get("Z_OS"));
											temp.put("OTS", crs.get("Z_OTS"));
											temp.put("OMS", crs.get("Z_OMS"));
											temp.put("OCH", crs.get("Z_OCH"));
											temp.put("ODU0", crs.get("Z_ODU0"));
											temp.put("ODU1", crs.get("Z_ODU1"));
											temp.put("ODU2", crs.get("Z_ODU2"));
											temp.put("ODU3", crs.get("Z_ODU3"));
											temp.put("DSR", crs.get("Z_DSR"));
											if (crs.get("Z_OMS") != null
													&& "2".equals(crs.get(
															"Z_OMS").toString())) {
												temp.put("OMS2", 2);
											}
										}
										// 将最后一条记录赋值
										if (Integer.parseInt(map_before.get(
												"IS_COMPLETE").toString()) == CommonDefine.TRUE) {
											// 删除为完成的激励
											select = hashMapSon(
													"t_cir_otn_circuit_route",
													"CIR_OTN_CIRCUIT_ROUTE_ID",
													map_before
															.get("CIR_OTN_CIRCUIT_ROUTE_ID"),
													null, null, null);
											circuitManagerMapper
													.deleteByParameter(select);

											// 获取新增的记录
											select = new HashMap();
											select.put("NAME",
													"t_cir_otn_circuit_route");
											select.put("ID",
													"CIR_OTN_CIRCUIT_ROUTE_ID");
											select.put("ID_NAME",
													"CIR_OTN_CIRCUIT_ID");
											select.put("ID_VALUE", map_before
													.get("CIR_OTN_CIRCUIT_ID"));

											Map map_latest = circuitManagerMapper
													.getLatestRecord(select);

											// 单条电路生成
											CreateSingleOtnCrs(map_otn,
													map_latest, temp);
										}
									}
								}

							} else {
								isGoOn = false;
							}

							select = hashMapSon("t_cir_otn_circuit_route",
									"IS_COMPLETE", CommonDefine.FALSE, null,
									null, null);

							// 判断是否还存在未完成的虚拟交叉
							list_false = circuitManagerMapper
									.getByParameter(select);
							if (list_false != null && list_false.size() > 0) {
								isGoOn = true;
							} else {
								isGoOn = false;
							}
						} while (isGoOn);

					}
				}
			}
		} else {

			// 进度描述信息更改--此处修改
			/*String text = "当前进度0/0";
			if ("newCir".equals(id)) {
				text = "0/0";
			}
			// 加入进度值
			CommonDefine.setProcessParameter(sessionId, id, text, Double.valueOf(1));*/
			CommonDefine.setProcessParameter(sessionId, id, 0,1,"otn电路生成 ");
		}

		return stop;
	}

	/**
	 * 
	 * 创建虚拟交叉
	 * @@@分权分域到网元@@@
	 * @param map_otn
	 *            交叉连接
	 * @param map_otn_cir
	 *            电路
	 * @param temp
	 *            临时交叉
	 */
	public boolean createHWOtnCrs(Map mapSelect, String id, String sessionId,
			int countSdh, int countOtn,int userId) {
		boolean stop = false;
		Map select = null;
		Map insert = null;
		Map update = null;
		//HttpServletRequest request = ServletActionContext.getRequest();
		//int userId=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		
		// 更新交叉连接查询状态 1 是查找过 0 是没有查找过
		update = new HashMap();
		update.put("NAME", "t_base_otn_crs");
		update.put("ID_NAME", "IS_DEL");
		update.put("ID_VALUE", CommonDefine.FALSE);
		update.put("ID_NAME_2", "IS_IN_CIRCUIT");
		update.put("ID_VALUE_2", CommonDefine.FALSE);

		circuitManagerMapper.updateByParameter(update);

		// 查询a端ptp且为边界点的交叉连接

		int n = 0;
		if (countOtn > 0) {
			for (int i = 0; i <= (countOtn - 1) / 1000; i++) {
				if(stop){
					break;
				}
				// 每次取1000条,如果后期查询速度慢，再换其他机制
				List<Map> list = circuitManagerMapper.getOtnCrs(mapSelect, 0,
						1000,userId,CommonDefine.TREE.TREE_DEFINE);
				if (list != null && list.size() > 0) {
					System.out.println("list=="+list.size());
					for (int j = 0; j < list.size(); j++) {
						Map map = list.get(j);
						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						
						//一条一条遍历
						System.out.println("交叉连接id=="+j+"--"+map.get("BASE_OTN_CRS_ID").toString());
						// 先更新是否被查找状态
						update = new HashMap();
						update.put("NAME", "t_base_otn_crs");
						update.put("ID_NAME", "BASE_OTN_CRS_ID");
						update.put("ID_VALUE", map.get("BASE_OTN_CRS_ID"));
						update.put("ID_NAME_2", "IS_IN_CIRCUIT");
						update.put("ID_VALUE_2", CommonDefine.TRUE);
						circuitManagerMapper.updateByParameter(update);
						// 判断交叉连接是否符规定
						if (map.get("A_TYPE") == null
								|| map.get("A_TYPE").toString().isEmpty()) {
							CommonDefine.setProcessParameter(
									sessionId,
									id,
									(i * 1000 + j + 1 + countSdh),
									(int) ((countSdh + countOtn)*1.006+1),
									"otn电路生成 ");
							System.out.println("countSdh="+countSdh+" ---"+countOtn);
							// 跳过本次循环
							continue;
						}
						if (map.get("Z_TYPE") == null
								|| map.get("Z_TYPE").toString().isEmpty()) {
							CommonDefine.setProcessParameter(
									sessionId,
									id,
									(i * 1000 + j + 1 + countSdh),
									(int) ((countSdh + countOtn)*1.006+1),
									"otn电路生成 ");
							System.out.println("countOtn="+countSdh+" ---"+countOtn);
							// 跳过本次循环
							continue;
						}

						Map temp = null;
						Map map_otn = map;

						// 插入一条otn电路信息
						insert = new HashMap();
						insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
						insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
						insert.put("CIR_COUNT", 1);

						insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
						// 新增cirNo
						Map map_maxNo = circuitManagerMapper
								.getMaxOtnCircuitNo();
						if (map_maxNo != null) {
							if (map_maxNo.get("CIR_NO") != null
									&& !map_maxNo.get("CIR_NO").toString()
											.isEmpty()) {
								insert.put(
										"CIR_NO",
										((Integer.parseInt(map_maxNo.get(
												"CIR_NO").toString()) + 1) + ""));
							} else {
								insert.put("CIR_NO", "100000");
							}
						} else {
							insert.put("CIR_NO", "100000");
						}
						circuitManagerMapper.insertOtnInfo(insert);

						// 获取插入数据Id
						Integer circuitInfoId = Integer.valueOf(insert.get(
								"CIR_OTN_CIRCUIT_INFO_ID").toString());

						// 新建一条otn电路
						insert = new HashMap();
						insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
						insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
						insert.put("IS_MAIN_CIR", CommonDefine.TRUE);
						insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
						insert.put("CIR_OTN_CIRCUIT_INFO_ID", circuitInfoId);
						Map map_otn_cir = insertOtnCir(insert);

						// 插入首条路由信息
						// 将链路存入路由
						insert = new HashMap();
						insert.put("CIR_OTN_CIRCUIT_ID",
								map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
						insert.put("CHAIN_ID", map_otn.get("BASE_OTN_CRS_ID"));
						insert.put("CHAIN_TYPE",
								CommonDefine.CHAIN_TYPE_OTN_CRS);

						insert.put("AHEAD_CRS_ID",
								map_otn.get("BASE_OTN_CRS_ID"));
						insert.put("IS_COMPLETE", CommonDefine.TRUE);
						circuitManagerMapper.insertOtnRoute(insert);

						// 经过数量加1
						update = new HashMap();
						update.put("NAME", "t_base_otn_crs");
						update.put("ID_NAME", "BASE_OTN_CRS_ID");
						update.put("ID_VALUE", map_otn.get("BASE_OTN_CRS_ID"));
						update.put("ID_NAME_2", "CIRCUIT_COUNT");
						update.put("ID_VALUE_2", Integer.parseInt(map_otn.get(
								"CIRCUIT_COUNT").toString()) + 1);
						circuitManagerMapper.updateByParameter(update);

						// 单条虚拟交叉生成
						CreateOtnVirCrsByNe(map_otn, temp, map_otn_cir);

						// 查找支路未完成的虚拟交叉
						select = hashMapSon("t_cir_otn_circuit_route",
								"IS_COMPLETE", CommonDefine.FALSE, null, null,
								null);

						List<Map> list_false = new ArrayList<Map>();
						boolean isGoOn = true;
						do {
							list_false = circuitManagerMapper
									.getByParameter(select);

							if (list_false != null && list_false.size() > 0) {
								temp = new HashMap();
								// 取出最小的一条处理
								Map map_flase = list_false.get(0);
								select = new HashMap();
								select.put(
										"CIR_OTN_CIRCUIT_ROUTE_ID",
										map_flase
												.get("CIR_OTN_CIRCUIT_ROUTE_ID"));
								select.put("CIR_OTN_CIRCUIT_ID",
										map_flase.get("CIR_OTN_CIRCUIT_ID"));
								// 查询出id小于当前id且
								List<Map> list_before = circuitManagerMapper
										.getOtnBefore(select);
								if (list_before != null
										&& list_before.size() > 0) {
									// 先查询出电路的信息
									select = hashMapSon(
											"t_cir_otn_circuit",
											"CIR_OTN_CIRCUIT_ID",
											list_before.get(0).get(
													"CIR_OTN_CIRCUIT_ID"),
											null, null, null);

									Map cir = circuitManagerMapper
											.getByParameter(select).get(0);
									// 插入一条新的电路
									Map insertCir = insertOtnCir(cir);

									// 遍历插入数据
									for (Map map_before : list_before) {
										insert = new HashMap();
										insert.put(
												"CIR_OTN_CIRCUIT_ID",
												insertCir
														.get("CIR_OTN_CIRCUIT_ID"));
										insert.put("IS_COMPLETE",
												CommonDefine.TRUE);
										insert.put("CHAIN_ID",
												map_before.get("CHAIN_ID"));
										insert.put("CHAIN_TYPE",
												map_before.get("CHAIN_TYPE"));
										insert.put("AHEAD_CRS_ID",
												map_before.get("AHEAD_CRS_ID"));
										circuitManagerMapper
												.insertOtnRoute(insert);
										// 经过数量加1
										update = new HashMap();
										update.put("NAME", "t_base_otn_crs");
										update.put("ID_NAME", "BASE_OTN_CRS_ID");
										update.put("ID_VALUE",
												map_otn.get("BASE_OTN_CRS_ID"));
										update.put("ID_NAME_2", "CIRCUIT_COUNT");
										update.put("ID_VALUE_2", Integer
												.parseInt(map_otn.get(
														"CIRCUIT_COUNT")
														.toString()) + 1);
										circuitManagerMapper
												.updateByParameter(update);

										// 判断是否是交叉连接，如果是交叉连接，则将az端的值赋给全局变量
										if (Integer.parseInt(map_before.get(
												"CHAIN_TYPE").toString()) == CommonDefine.CHAIN_TYPE_OTN_CRS) {
											select = hashMapSon(
													"t_base_otn_crs",
													"BASE_OTN_CRS_ID",
													map_before.get("CHAIN_ID"),
													null, null, null);
											Map crs = circuitManagerMapper
													.getByParameter(select)
													.get(0);
											// a端
											if(crs.get("A_OS")!=null&&!crs.get("A_OS").toString().isEmpty()){
												temp.put("OS", crs.get("A_OS"));
											}
											if(crs.get("A_OTS")!=null&&!crs.get("A_OTS").toString().isEmpty()){
												temp.put("OTS", crs.get("A_OTS"));
											}
											if(crs.get("A_OMS")!=null&&!crs.get("A_OMS").toString().isEmpty()){
												temp.put("OMS", crs.get("A_OMS"));
											}
											if(crs.get("A_OCH")!=null&&!crs.get("A_OCH").toString().isEmpty()){
												temp.put("OCH", crs.get("A_OCH"));
											}
											if(crs.get("A_ODU0")!=null&&!crs.get("A_ODU0").toString().isEmpty()){
												temp.put("ODU0", crs.get("A_ODU0"));
											}
											if(crs.get("A_ODU1")!=null&&!crs.get("A_ODU1").toString().isEmpty()){
												temp.put("ODU1", crs.get("A_ODU1"));
											}
											if(crs.get("A_ODU2")!=null&&!crs.get("A_ODU2").toString().isEmpty()){
												temp.put("ODU2", crs.get("A_ODU2"));
											}
											if(crs.get("A_ODU3")!=null&&!crs.get("A_ODU3").toString().isEmpty()){
												temp.put("ODU3", crs.get("A_ODU3"));
											}
											if(crs.get("A_DSR")!=null&&!crs.get("A_DSR").toString().isEmpty()){
												temp.put("DSR", crs.get("A_DSR"));
											}
											
																												
											// 如果oms 有2出现，则记录在OMS2
											if (crs.get("A_OMS") != null
													&& "2".equals(crs.get(
															"A_OMS").toString())) {
												temp.put("OMS2", 2);
											}
											// z端
											if(crs.get("Z_OS")!=null&&!crs.get("Z_OS").toString().isEmpty()){
												temp.put("OS", crs.get("Z_OS"));
											}
											if(crs.get("Z_OTS")!=null&&!crs.get("Z_OTS").toString().isEmpty()){
												temp.put("OTS", crs.get("Z_OTS"));
											}
											if(crs.get("Z_OMS")!=null&&!crs.get("Z_OMS").toString().isEmpty()){
												temp.put("OMS", crs.get("Z_OMS"));
											}
											if(crs.get("Z_OCH")!=null&&!crs.get("Z_OCH").toString().isEmpty()){
												temp.put("OCH", crs.get("Z_OCH"));
											}
											if(crs.get("Z_ODU0")!=null&&!crs.get("Z_ODU0").toString().isEmpty()){
												temp.put("ODU0", crs.get("Z_ODU0"));
											}
											if(crs.get("Z_ODU1")!=null&&!crs.get("Z_ODU1").toString().isEmpty()){
												temp.put("ODU1", crs.get("Z_ODU1"));
											}
											if(crs.get("Z_ODU2")!=null&&!crs.get("Z_ODU2").toString().isEmpty()){
												temp.put("ODU2", crs.get("Z_ODU2"));
											}
											if(crs.get("Z_ODU3")!=null&&!crs.get("Z_ODU3").toString().isEmpty()){
												temp.put("ODU3", crs.get("Z_ODU3"));
											}
											if(crs.get("Z_DSR")!=null&&!crs.get("Z_DSR").toString().isEmpty()){
												temp.put("DSR", crs.get("Z_DSR"));
											}
											if (crs.get("Z_OMS") != null
													&& "2".equals(crs.get(
															"Z_OMS").toString())) {
												temp.put("OMS2", 2);
											}
										}
										// 将最后一条记录赋值
										if (Integer.parseInt(map_before.get(
												"IS_COMPLETE").toString()) == CommonDefine.FALSE) {
											// 删除为完成的激励
											select = hashMapSon(
													"t_cir_otn_circuit_route",
													"CIR_OTN_CIRCUIT_ROUTE_ID",
													map_before
															.get("CIR_OTN_CIRCUIT_ROUTE_ID"),
													null, null, null);
											circuitManagerMapper
													.deleteByParameter(select);

											// 获取新增的记录
											select = new HashMap();
											select.put("NAME",
													"t_cir_otn_circuit_route");
											select.put("ID",
													"CIR_OTN_CIRCUIT_ROUTE_ID");
											select.put("ID_NAME",
													"CIR_OTN_CIRCUIT_ID");
											select.put("ID_VALUE", insertCir
													.get("CIR_OTN_CIRCUIT_ID"));

											Map map_latest = circuitManagerMapper
													.getLatestRecord(select);

											// 获取最新交叉连接
											select = hashMapSon(
													"t_base_otn_crs",
													"BASE_OTN_CRS_ID",
													map_latest.get("CHAIN_ID"),
													null, null, null);
											Map crs = circuitManagerMapper
													.getByParameter(select)
													.get(0);
											// 单条电路生成
											CreateOtnVirCrsByNe(crs, temp,
													insertCir);
										}
									}
								}

							} else {
								isGoOn = false;
							}

							select = hashMapSon("t_cir_otn_circuit_route",
									"IS_COMPLETE", CommonDefine.FALSE, null,
									null, null);

							// 判断是否还存在未完成的虚拟交叉
							list_false = circuitManagerMapper
									.getByParameter(select);
							if (list_false != null && list_false.size() > 0) {
								isGoOn = true;
							} else {
								isGoOn = false;
							}
						} while (isGoOn);
						
						/*// 进度描述信息更改--此处修改
						String text = "当前进度" + (i * 1000 + j + 1 + countSdh)
								+ "/" + (countSdh + countOtn);
						if ("newCir".equals(id)) {
							text = (i * 1000 + j + 1 + countSdh) + "/"
									+ (countSdh + countOtn);
						}
						// 加入进度值
						CommonDefine.setProcessParameter(
								sessionId,
								id,
								text,
								Double.valueOf((i * 1000 + j + 1 + countSdh)
										/ ((double) (countSdh + countOtn))));*/
						CommonDefine.setProcessParameter(
								sessionId,
								id,
								(i * 1000 + j + 1 + countSdh),
								(int) ((countSdh + countOtn)*1.006+1),
								"otn电路生成 ");

					}
				}else{
					CommonDefine.setProcessParameter(
							sessionId,
							id,
							(countSdh + countSdh),
							(int) ((countSdh + countOtn)*1.006+1),
							"otn电路生成 ");
				}
			}
		} else {

			// 进度描述信息更改--此处修改
			/*String text = "当前进度0/0";
			if ("newCir".equals(id)) {
				text = "0/0";
			}
			// 加入进度值
			CommonDefine.setProcessParameter(sessionId, id, text, Double.valueOf(1));*/
			CommonDefine.setProcessParameter(sessionId, id, 0,1,"otn电路生成 ");

		}
		return stop;
	}

	/**
	 * 创建以网元为单位的虚拟交叉
	 */
	public void CreateOtnVirCrsByNe(Map map_otn, Map temp_, Map map_otn_cir) {
		Map temp = new HashMap();
		// 定义变量
		if (temp_ == null) {
			temp.put("OS", 0);
			temp.put("OTS", 0);
			temp.put("OMS", 0);
			temp.put("OCH", 0);
			temp.put("ODU0", 0);
			temp.put("ODU1", 0);
			temp.put("ODU2", 0);
			temp.put("ODU3", 0);
			temp.put("DSR", 0);
			temp.put("OMS2", 0);
		} else {
			temp = temp_;
		}
		// 定义查询map变量
		Map select = null;
		Map insert = null;
		Map update = null;
		Map delete = null;
		// 将A端的值赋给全局变量
		if (map_otn.get("A_TYPE") != null
				&& !map_otn.get("A_TYPE").toString().isEmpty()) {
			String[] otn_type = map_otn.get("A_TYPE").toString().split(",");
			for (String otn_typeName : otn_type) {
				temp.put(otn_typeName.substring(2), map_otn.get(otn_typeName));
				// 如果oms 有2出现，则记录在OMS2
				if (otn_typeName.contains("OMS")
						&& map_otn.get(otn_typeName) != null
						&& "2".equals(map_otn.get(otn_typeName).toString())) {
					temp.put("OMS2", 2);
				}
			}

		}

		// 将z端的值赋给全局变量
		if (map_otn.get("Z_TYPE") != null
				&& !map_otn.get("Z_TYPE").toString().isEmpty()) {
			String[] otn_type = map_otn.get("Z_TYPE").toString().split(",");
			for (String otn_typeName : otn_type) {
				temp.put(otn_typeName.substring(2), map_otn.get(otn_typeName));
				// 如果oms 有2出现，则记录在OMS2
				if (otn_typeName.contains("OMS")
						&& map_otn.get(otn_typeName) != null
						&& "2".equals(map_otn.get(otn_typeName).toString())) {
					temp.put("OMS2", 2);
				}
			}

		}

		boolean istrue = true;
		int numb = 0;
		do {
			numb++;
			select = hashMapSon("t_base_ptp", "BASE_PTP_ID",
					map_otn.get("Z_END_PTP"), null, null, null);

			List<Map> list_ptp = circuitManagerMapper.getByParameter(select);

			if (list_ptp != null && list_ptp.size() > 0) {
				Map map_ptp = list_ptp.get(0);

				// 如果z是边界点或者外部link点，则结束虚拟交叉生成
				if (Integer.parseInt(map_ptp.get("PTP_FTP").toString()) == CommonDefine.CIR_PTP) {
					// 判断是否为边界点
					if (Integer.parseInt(map_ptp.get("PORT_TYPE").toString()) == CommonDefine.PORT_TYPE_EDGE_POINT) {

						Map info = null;
						// 先判断电路信息表中是否已经存在电路
						select = hashMapSon("t_cir_otn_circuit_info",
								"A_END_CTP", map_otn_cir.get("A_END_CTP"),
								"Z_END_CTP", map_otn.get("Z_END_CTP"), null);

						List<Map> list_info = circuitManagerMapper
								.getByParameter(select);
						// 如果存在，则变成多路经电路，增加电路数量
						if (list_info != null && list_info.size() > 0) {
							info = list_info.get(0);
							// 更新电路信息表的电路数
							int num = Integer.parseInt(info.get("CIR_COUNT")
									.toString()) + 1;
							update = new HashMap();
							update.put("NAME", "t_cir_otn_circuit_info");
							update.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
							update.put("ID_VALUE",
									info.get("CIR_OTN_CIRCUIT_INFO_ID"));
							update.put("ID_NAME_2", "CIR_COUNT");
							update.put("ID_VALUE_2", num);
							update.put("ID_NAME_3", "IS_LATEST_CREATE");
							update.put("ID_VALUE_3", CommonDefine.TRUE);

							circuitManagerMapper.updateByParameter(update);

							// // 将新的电路信息id绑向电路表
							//
							// update = hashMapSon("t_cir_otn_circuit",
							// "CIR_OTN_CIRCUIT_ID", map_otn_cir
							// .get("CIR_OTN_CIRCUIT_ID"),
							// "CIR_OTN_CIRCUIT_INFO_ID", info
							// .get("CIR_OTN_CIRCUIT_INFO_ID"), null);
							// circuitManagerMapper.updateByParameter(update);

							// 删除刚开始插入的电路信息记录
							delete = new HashMap();
							delete.put("NAME", "t_cir_otn_circuit_info");
							delete.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
							delete.put("ID_VALUE",
									map_otn_cir.get("CIR_OTN_CIRCUIT_INFO_ID"));
							circuitManagerMapper.deleteByParameter(delete);

						} else {
							// 如果不存在，将更新之前插入的记录
							update = new HashMap();
							update.put("CIR_OTN_CIRCUIT_INFO_ID",
									map_otn_cir.get("CIR_OTN_CIRCUIT_INFO_ID"));
							update.put("Z_END_CTP", map_otn.get("Z_END_CTP"));
							update.put("Z_END_PTP", map_otn.get("Z_END_PTP"));
							update.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
							update.put("IS_LATEST_CREATE", CommonDefine.TRUE);
							circuitManagerMapper.updateOtnInfo(update);

						}

						// 更新otn电路表
						map_otn_cir.put("Z_END_CTP", map_otn.get("Z_END_CTP"));
						map_otn_cir.put("Z_END_PTP", map_otn.get("Z_END_PTP"));
						map_otn_cir
								.put("DIRECTION", CommonDefine.DIRECTION_ONE);
						map_otn_cir.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
						map_otn_cir.put("IS_MAIN_CIR", CommonDefine.TRUE);
						// 如果info为空，则不需要更改电路信息id
						if (info != null) {
							map_otn_cir.put("CIR_OTN_CIRCUIT_INFO_ID",
									info.get("CIR_OTN_CIRCUIT_INFO_ID"));
						}

						circuitManagerMapper.updateOtnCir(map_otn_cir);
						istrue = false;

					} else {
						// 查找链路
						select = hashMapSon("t_base_link", "A_END_PTP",
								map_otn.get("Z_END_PTP"), null, null, null);

						List<Map> list_link = circuitManagerMapper
								.getByParameter(select);

						if (list_link != null && list_link.size() > 0) {

							// 将链路存入路由
							insert = new HashMap();
							insert.put("CIR_OTN_CIRCUIT_ID",
									map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
							insert.put("CHAIN_ID",
									list_link.get(0).get("BASE_LINK_ID"));
							// 判断是内部还是外部link
							if (Integer.parseInt(list_link.get(0)
									.get("LINK_TYPE").toString()) == CommonDefine.LINK_IN) {
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_IN_LINK);
							} else {
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OUT_LINK);
							}
							insert.put("AHEAD_CRS_ID", 1);
							insert.put("IS_COMPLETE", CommonDefine.TRUE);
							circuitManagerMapper.insertOtnRoute(insert);

							// 经过数量加1
							update = new HashMap();
							update.put("NAME", "t_base_otn_crs");
							update.put("ID_NAME", "BASE_OTN_CRS_ID");
							update.put("ID_VALUE",
									map_otn.get("BASE_OTN_CRS_ID"));
							update.put("ID_NAME_2", "CIRCUIT_COUNT");
							update.put(
									"ID_VALUE_2",
									Integer.parseInt(map_otn.get(
											"CIRCUIT_COUNT").toString()) + 1);
							circuitManagerMapper.updateByParameter(update);

							// 查找下一条的交叉
							select = new HashMap();
							select.put("A_END_PTP",
									list_link.get(0).get("Z_END_PTP"));

							// 判断交叉连接z端的值,先获取z端类型
							String[] otn_type = map_otn.get("Z_TYPE")
									.toString().split(",");
							for (String name : otn_type) {
								select.put(name.replace("Z", "A"),
										map_otn.get(name));
							}

							List<Map> list_crs = circuitManagerMapper
									.getOtnCrsByCtp(select);
							// 找到则继续往下
							if (list_crs != null && list_crs.size() > 0) {
								//
								Map returnMap = insertToVir(list_crs, insert,
										temp, map_otn_cir, map_otn);
								temp = (Map) returnMap.get("temp");
								map_otn = (Map) returnMap.get("map_otn");
							} else {
								// 找不到交叉连接时，需要匹配当前ctp中的高阶位或者再向前进一位
								String type_name = sortType(
										map_otn.get("Z_TYPE").toString(), "Z_");

								// 逐步缩减字段匹配
								String[] type_z = type_name.split(",");

								// 如果type字段是多个，从小到大依次缩减
								List<Map> list_crs_s = new ArrayList<Map>();

								for (int j = type_z.length - 1; j > 0; j--) {
									// 查找下一条的交叉
									select = new HashMap();
									select.put("A_END_PTP", list_link.get(0)
											.get("Z_END_PTP"));

									// 判断交叉连接z端的值,先获取z端类型
									for (int k = 0; k < j; k++) {
										select.put(
												"A" + type_z[k].substring(1),
												map_otn.get(type_z[k]));
									}

									list_crs_s = circuitManagerMapper
											.getOtnCrsByCtp(select);

									// 如果存在值，则结束本次循环
									if (list_crs_s != null
											&& list_crs_s.size() > 0) {
										break;
									}
								}

								if (list_crs_s != null && list_crs_s.size() > 0) {
									// 找到交叉连接的处理方法
									Map returnMap = insertToVir(list_crs_s,
											insert, temp, map_otn_cir, map_otn);
									temp = (Map) returnMap.get("temp");
									map_otn = (Map) returnMap.get("map_otn");
									istrue = true;
								} else {
									Map returnMap = upToFindOtnCrsVir(type_z,
											insert, temp, map_otn_cir, map_otn,
											list_link);
									temp = (Map) returnMap.get("temp");
									map_otn = (Map) returnMap.get("map_otn");
									istrue = (Boolean) returnMap.get("is_find");
								}

							}
						} else {
							// 找不到链路
							istrue = false;
						}

					}

					// 外部link点
				} else {

					// 如果是ftp，则继续查找下一跳,则表示电路未完成，且为交叉连交叉模式
					// 先判断交叉是否为空

					if (map_otn.get("Z_TYPE") != null
							&& !map_otn.get("Z_TYPE").toString().isEmpty()) {

						// 如果不为空，则查询为空的交叉,此处如果速度过慢，可以直接通过交叉表进行判断，z_TYPE
						// 防止正反数据，此处忽略，需要添加
						select = new HashMap();
						select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
						select.put("IS_CTP", CommonDefine.FALSE);
						select.put("PTP_FTP", CommonDefine.CIR_FTP);

						String[] type_z = map_otn.get("Z_TYPE").toString()
								.split(",");
						for (String name : type_z) {
							select.put(name, map_otn.get(name));
						}

						List<Map> list_null = circuitManagerMapper
								.getNextOtnCrs(select);
						List<Map> listNull = new ArrayList<Map>();
						for (Map crsNull : list_null) {
							// 向上越级可以无视此规则
							// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
							int k = -1;
							// 判断上一交叉的z端最大级别
							if (crsNull.get("Z_TYPE").toString().contains("OS")) {
								// 已经到最顶端
								// istrue = false;
								k = 8;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("OTS")) {
								k = 7;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("OMS")) {
								k = 6;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("OCH")) {
								k = 5;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("ODU3")) {
								k = 4;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("ODU2")) {
								k = 3;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("ODU1")) {
								k = 2;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("ODU0")) {
								k = 1;
							} else if (crsNull.get("Z_TYPE").toString()
									.contains("DSR")) {
								k = 0;
							}

							int j = 0;
							// 判断上一交叉的z端最大级别
							if (map_otn.get("Z_TYPE").toString().contains("OS")) {
								// 已经到最顶端
								// istrue = false;
								j = 8;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("OTS")) {
								j = 7;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("OMS")) {
								j = 6;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("OCH")) {
								j = 5;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("ODU3")) {
								j = 4;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("ODU2")) {
								j = 3;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("ODU1")) {
								j = 2;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("ODU0")) {
								j = 1;
							} else if (map_otn.get("Z_TYPE").toString()
									.contains("DSR")) {
								j = 0;
							}
							if (k >= j) {
								listNull.add(crsNull);
								continue;
							}
							boolean isRight = true; // 交叉是否符合标准
							if (crsNull.get("Z_TYPE") != null
									&& !crsNull.get("Z_TYPE").toString()
											.isEmpty()) {
								String[] typeZ = crsNull.get("Z_TYPE")
										.toString().split(",");
								for (String name : typeZ) {
									if (temp.get(name.substring(2)) == null
											|| !temp.get(name.substring(2))
													.toString()
													.equals(crsNull.get(name)
															.toString())) {
										// 结束本次循环
										isRight = false;
										break;
									}
								}
							}else{
								isRight = false;
							}
							if (!isRight) {
								continue;
							} else {
								listNull.add(crsNull);
							}
						}
						if (listNull != null && listNull.size() > 0) {
							// 开始遍历
							for (int j = listNull.size() - 1; j >= 0; j--) {

								Map map_crs = listNull.get(j);
								// 将数据存入数据库，标注未完成,处理第一条

								insert = new HashMap();

								insert.put("CIR_OTN_CIRCUIT_ID",
										map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
								insert.put("CHAIN_ID",
										map_crs.get("BASE_OTN_CRS_ID"));
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OTN_CRS);
								insert.put("AHEAD_CRS_ID", 1);
								if (j > 0) {
									insert.put("IS_COMPLETE",
											CommonDefine.FALSE);
								} else {
									// 如果是最后一条，则 将交叉连接赋值
									insert.put("IS_COMPLETE", CommonDefine.TRUE);
									map_otn = listNull.get(0);

									// 给全局变量赋值
									String[] typeName = map_otn.get("Z_TYPE")
											.toString().split(",");
									for (String name : typeName) {
										temp.put(name.substring(2),
												map_otn.get(name));
										// 如果oms 有2出现，则记录在OMS2
										if (name.contains("OMS")
												&& map_otn.get(name) != null
												&& "2".equals(map_otn.get(name)
														.toString())) {
											temp.put("OMS2", 2);
										}
									}

								}
								circuitManagerMapper.insertOtnRoute(insert);
								/** 待完善 */
							}
						} else {
							// 同级没有查找到，向上越级查找

							Map returnMap = upToFindOtnCrsANullVir(type_z,
									insert, temp, map_otn_cir, map_otn);
							temp = (Map) returnMap.get("temp");
							map_otn = (Map) returnMap.get("map_otn");
							istrue = (Boolean) returnMap.get("is_find");
						}

					} else {

						// 如果为空，则查询不为空的交叉
						select = new HashMap();
						select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
						select.put("IS_CTP", CommonDefine.TRUE);
						select.put("PTP_FTP", CommonDefine.CIR_FTP);

						// 将上一跳的z端时隙值，作为参数放入
						String[] type_a = map_otn.get("A_TYPE").toString()
								.split(",");
						for (String name : type_a) {
							select.put(name, map_otn
									.get(name));

						}

						List<Map> list_notnull = circuitManagerMapper
								.getNextOtnCrs(select);
						List<Map> listNotNull = new ArrayList<Map>();
						for (Map crsNotNull : list_notnull) {
							// 向上越级可以无视此规则
							// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
							int k = -1;
							// 判断上一交叉的z端最大级别
							if (crsNotNull.get("A_TYPE").toString()
									.contains("OS")) {
								// 已经到最顶端
								// istrue = false;
								k = 8;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("OTS")) {
								k = 7;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("OMS")) {
								k = 6;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("OCH")) {
								k = 5;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("ODU3")) {
								k = 4;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("ODU2")) {
								k = 3;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("ODU1")) {
								k = 2;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("ODU0")) {
								k = 1;
							} else if (crsNotNull.get("A_TYPE").toString()
									.contains("DSR")) {
								k = 0;
							}

							int j = 0;
							// 判断上一交叉的z端最大级别
							if (map_otn.get("A_TYPE").toString().contains("OS")) {
								// 已经到最顶端
								// istrue = false;
								j = 8;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("OTS")) {
								j = 7;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("OMS")) {
								j = 6;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("OCH")) {
								j = 5;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("ODU3")) {
								j = 4;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("ODU2")) {
								j = 3;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("ODU1")) {
								j = 2;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("ODU0")) {
								j = 1;
							} else if (map_otn.get("A_TYPE").toString()
									.contains("DSR")) {
								j = 0;
							}
							if (k >= j) {
								listNotNull.add(crsNotNull);
								continue;
							}
							boolean isRight = true; // 交叉是否符合标准
							if (crsNotNull.get("A_TYPE") != null
									&& !crsNotNull.get("A_TYPE").toString()
											.isEmpty()) {
								String[] typeA = crsNotNull.get("A_TYPE")
										.toString().split(",");
								for (String name : typeA) {
									System.out.println("name==" + name);
									System.out.println("crsNotNull.get(name)=="
											+ crsNotNull.get(name).toString());
									if (temp.get(name.substring(2)) == null
											|| !temp.get(name.substring(2))
													.toString()
													.equals(crsNotNull
															.get(name)
															.toString())) {
										// 结束本次循环
										isRight = false;
										break;
									}
								}
							}
							else{
								isRight = false;
							}
							if (!isRight) {
								continue;
							} else {
								listNotNull.add(crsNotNull);
							}
						}

						if (listNotNull != null && listNotNull.size() > 0) {
							// 开始遍历
							for (int j = listNotNull.size() - 1; j >= 0; j--) {

								insert = new HashMap();

								insert.put("CIR_OTN_CIRCUIT_ID",
										map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
								insert.put(
										"CHAIN_ID",
										listNotNull.get(j).get(
												"BASE_OTN_CRS_ID"));
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OTN_CRS);
								insert.put("AHEAD_CRS_ID", 1);
								if (j > 0) {
									insert.put("IS_COMPLETE",
											CommonDefine.FALSE);
								} else {
									// 如果是最后一条，则 将交叉连接赋值
									insert.put("IS_COMPLETE", CommonDefine.TRUE);
									map_otn = listNotNull.get(0);

									// 给全局变量赋值
									if (map_otn.get("A_TYPE") != null
											&& !map_otn.get("A_TYPE")
													.toString().isEmpty()) {
										String[] typeName_a = map_otn
												.get("A_TYPE").toString()
												.split(",");
										for (String name : typeName_a) {
											temp.put(name.substring(2), map_otn
													.get(name));
											// 如果oms 有2出现，则记录在OMS2
											if (name.contains("OMS")
													&& map_otn.get(name) != null
													&& "2".equals(map_otn.get(
															name).toString())) {
												temp.put("OMS2", 2);
											}
										}
									}

									if (map_otn.get("Z_TYPE") != null
											&& !map_otn.get("Z_TYPE")
													.toString().isEmpty()) {
										// 给全局变量赋值
										String[] typeName_z = map_otn
												.get("Z_TYPE").toString()
												.split(",");
										for (String name : typeName_z) {
											temp.put(name.substring(2), map_otn
													.get(name));
											// 如果oms 有2出现，则记录在OMS2
											if (name.contains("OMS")
													&& map_otn.get(name) != null
													&& "2".equals(map_otn.get(
															name).toString())) {
												temp.put("OMS2", 2);
											}
										}
									}

								}
								circuitManagerMapper.insertOtnRoute(insert);
							}
							/** 待完善 */
						} else {
							// 分部查找 1.向上 2。向下
							// 向上查询
							Map returnMap = upToFindOtnCrsZNullVir(type_a,
									insert, temp, map_otn_cir, map_otn);
							temp = (Map) returnMap.get("temp");
							map_otn = (Map) returnMap.get("map_otn");
							istrue = (Boolean) returnMap.get("is_find");

						}

					}

				}
			}
			if (numb > 50) {
				istrue = false;
			}
		} while (istrue);

	}

	/**
	 * 中兴otn 单条电路生成
	 * @param map_otn
	 * @param temp_
	 * @param map_otn_cir
	 */
	public void CreateSingleZteOtn(Map map_otn, Map temp_, Map map_otn_cir) {
		Map temp = new HashMap();
		// 定义变量
		if (temp_ == null) {
			temp.put("OS", 0);
			temp.put("OTS", 0);
			temp.put("OMS", 0);
			temp.put("OCH", 0);
			temp.put("ODU0", 0);
			temp.put("ODU1", 0);
			temp.put("ODU2", 0);
			temp.put("ODU3", 0);
			temp.put("DSR", 0);
			temp.put("OMS2", 0);
		} else {
			temp = temp_;
		}
		// 定义查询map变量
		Map select = null;
		Map insert = null;
		Map update = null;
		Map delete = null;
		// 将A端的值赋给全局变量
		if (map_otn.get("A_TYPE") != null
				&& !map_otn.get("A_TYPE").toString().isEmpty()) {
			String[] otn_type = map_otn.get("A_TYPE").toString().split(",");
			for (String otn_typeName : otn_type) {
				temp.put(otn_typeName.substring(2), map_otn.get(otn_typeName));
				// 如果oms 有2出现，则记录在OMS2
				if (otn_typeName.contains("OMS")
						&& map_otn.get(otn_typeName) != null
						&& "2".equals(map_otn.get(otn_typeName).toString())) {
					temp.put("OMS2", 2);
				}
			}

		}

		// 将z端的值赋给全局变量
		if (map_otn.get("Z_TYPE") != null
				&& !map_otn.get("Z_TYPE").toString().isEmpty()) {
			String[] otn_type = map_otn.get("Z_TYPE").toString().split(",");
			for (String otn_typeName : otn_type) {
				temp.put(otn_typeName.substring(2), map_otn.get(otn_typeName));
				// 如果oms 有2出现，则记录在OMS2
				if (otn_typeName.contains("OMS")
						&& map_otn.get(otn_typeName) != null
						&& "2".equals(map_otn.get(otn_typeName).toString())) {
					temp.put("OMS2", 2);
				}
			}

		}

		boolean istrue = true;
		int numb = 0;
		do {
			numb++;
			select = hashMapSon("t_base_ptp", "BASE_PTP_ID",
					map_otn.get("Z_END_PTP"), null, null, null);

			List<Map> list_ptp = circuitManagerMapper.getByParameter(select);

			if (list_ptp != null && list_ptp.size() > 0) {
				Map map_ptp = list_ptp.get(0);

					// 判断是否为边界点
					if (Integer.parseInt(map_ptp.get("PORT_TYPE").toString()) == CommonDefine.PORT_TYPE_EDGE_POINT
							||Integer.parseInt(map_ptp.get("PORT_TYPE").toString())==CommonDefine.PORT_TYPE_INTERNAL_LINK_POINT) {
						istrue = false;
						// 判断能否找到下一跳，如果没有，则结束电路,并验证是否是完整电路
						select = new HashMap();
						select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
						select.put("Z_END_PTP", map_otn.get("A_END_PTP"));
						select.put("A_TYPE", map_otn.get("Z_TYPE"));
						// 判断交叉连接z端的值,先获取z端类型
						String[] otn_type = map_otn.get("Z_TYPE")
								.toString().split(",");
						for (String name : otn_type) {
							select.put(name.replace("Z", "A"),
									map_otn.get(name));
						}
						
						List<Map> listCrs = circuitManagerMapper.selectZteOtnNextCrs(select);
						if(listCrs!=null&&listCrs.size()>0){

							Map returnMap = insertToZteVir(listCrs, insert,
									temp, map_otn_cir, map_otn);
 							map_otn = (Map) returnMap.get("map_otn");
							istrue = true;
						}else{
							// 如果没找到，则向上查找，如果再没有则向下查找

							// 找不到交叉连接时，需要匹配当前ctp中的高阶位或者再向前进一位
							String type_name = sortType(map_otn.get("Z_TYPE").toString(), "Z_");

							// 逐步缩减字段匹配
							String[] type_z = type_name.split(",");

							// 如果type字段是多个，从小到大依次缩减
							List<Map> list_crs_s = new ArrayList<Map>();

							for (int j = type_z.length - 1; j > 0; j--) {
								// 查找下一条的交叉
								select = new HashMap();
								select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
								select.put("Z_END_PTP", map_otn.get("A_END_PTP"));
								// 判断交叉连接z端的值,先获取z端类型
								for (int k = 0; k < j; k++) {
									select.put(
											"A" + type_z[k].substring(1),
											map_otn.get(type_z[k]));
								}

								list_crs_s = circuitManagerMapper.selectZteOtnNextCrs(select);

								// 如果存在值，则结束本次循环
								if (list_crs_s != null && list_crs_s.size() > 0) {
									break;
								}
							}

							if (list_crs_s != null && list_crs_s.size() > 0) {
								// 
								// 找到交叉连接的处理方法
								Map returnMap = insertToZteVir(list_crs_s,
										insert, temp, map_otn_cir, map_otn);
								temp = (Map) returnMap.get("temp");
								map_otn = (Map) returnMap.get("map_otn");
								istrue = true;
							} else {
								// 向上或者向下遍历
//								Map returnMap = upToFindOtnCrsVir(type_z,
//										insert, temp, map_otn_cir, map_otn,
//										list_link);
								Map returnMap = findCrsByUpAndDown(type_z,
										insert, temp, map_otn_cir, map_otn,
										map_otn);
								temp = (Map) returnMap.get("temp");
								map_otn = (Map) returnMap.get("map_otn");
								istrue = (Boolean) returnMap.get("is_find");
							}

						
						}
						// 如果找不到下一跳，则判断电路是否是完整电路
						if(!istrue){
							boolean isComplete = false;
							// 取出电路开端的交叉连接
							select = hashMapSon("t_base_otn_crs",
									"A_END_CTP", map_otn_cir.get("A_END_CTP"),
									null, null, null);
							List<Map> firstCrs = circuitManagerMapper.getByParameter(select);
							
							if(firstCrs!=null&&firstCrs.size()>0){
								// 判断电路的起点和结束点的级别是否相同，如果相同，则认为是完整电路
								if(!firstCrs.get(0).get("A_TYPE").toString().trim().isEmpty()){
									if(firstCrs.get(0).get("BASE_OTN_CRS_ID").toString().equals(map_otn.get("BASE_OTN_CRS_ID").toString().trim())){
										isComplete = true;
									}
									if(firstCrs.get(0).get("A_TYPE").toString().trim().equals(map_otn.get("Z_TYPE").toString().trim().replaceAll("Z", "A"))){
										isComplete = true;
									}
								}
							}
							// 如果是完整电路，则更新电路z端节点
							if(isComplete){
								Map info = null;
								// 先判断电路信息表中是否已经存在电路
								select = hashMapSon("t_cir_otn_circuit_info",
										"A_END_CTP", map_otn_cir.get("A_END_CTP"),
										"Z_END_CTP", map_otn.get("Z_END_CTP"), null);

								List<Map> list_info = circuitManagerMapper
										.getByParameter(select);
								// 如果存在，则变成多路经电路，增加电路数量
								if (list_info != null && list_info.size() > 0) {
									info = list_info.get(0);
									// 更新电路信息表的电路数
									int num = Integer.parseInt(info.get("CIR_COUNT")
											.toString()) + 1;
									update = new HashMap();
									update.put("NAME", "t_cir_otn_circuit_info");
									update.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
									update.put("ID_VALUE",
											info.get("CIR_OTN_CIRCUIT_INFO_ID"));
									update.put("ID_NAME_2", "CIR_COUNT");
									update.put("ID_VALUE_2", num);
									update.put("ID_NAME_3", "IS_LATEST_CREATE");
									update.put("ID_VALUE_3", CommonDefine.TRUE);

									circuitManagerMapper.updateByParameter(update);

									// 删除刚开始插入的电路信息记录
									delete = new HashMap();
									delete.put("NAME", "t_cir_otn_circuit_info");
									delete.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
									delete.put("ID_VALUE",
											map_otn_cir.get("CIR_OTN_CIRCUIT_INFO_ID"));
									circuitManagerMapper.deleteByParameter(delete);

								} else {
									// 如果不存在，将更新之前插入的记录
									update = new HashMap();
									update.put("CIR_OTN_CIRCUIT_INFO_ID",
											map_otn_cir.get("CIR_OTN_CIRCUIT_INFO_ID"));
									update.put("Z_END_CTP", map_otn.get("Z_END_CTP"));
									update.put("Z_END_PTP", map_otn.get("Z_END_PTP"));
									update.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
									update.put("IS_LATEST_CREATE", CommonDefine.TRUE);
									circuitManagerMapper.updateOtnInfo(update);

								}

								// 更新otn电路表
								map_otn_cir.put("Z_END_CTP", map_otn.get("Z_END_CTP"));
								map_otn_cir.put("Z_END_PTP", map_otn.get("Z_END_PTP"));
								map_otn_cir
										.put("DIRECTION", CommonDefine.DIRECTION_ONE);
								map_otn_cir.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
								map_otn_cir.put("IS_MAIN_CIR", CommonDefine.TRUE);
								// 如果info为空，则不需要更改电路信息id
								if (info != null) {
									map_otn_cir.put("CIR_OTN_CIRCUIT_INFO_ID",
											info.get("CIR_OTN_CIRCUIT_INFO_ID"));
								}

								circuitManagerMapper.updateOtnCir(map_otn_cir);
							}
							
							}
							
						
						//istrue = false;

					} else {
						// 查找链路
						select = hashMapSon("t_base_link", "A_END_PTP",
								map_otn.get("Z_END_PTP"), null, null, null);

						List<Map> list_link = circuitManagerMapper
								.getByParameter(select);

						if (list_link != null && list_link.size() > 0) {

							// 将链路存入路由
							insert = new HashMap();
							insert.put("CIR_OTN_CIRCUIT_ID",
									map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
							insert.put("CHAIN_ID",
									list_link.get(0).get("BASE_LINK_ID"));
							// 判断是内部还是外部link
							if (Integer.parseInt(list_link.get(0)
									.get("LINK_TYPE").toString()) == CommonDefine.LINK_IN) {
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_IN_LINK);
							} else {
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OUT_LINK);
							}
							insert.put("AHEAD_CRS_ID", 1);
							insert.put("IS_COMPLETE", CommonDefine.TRUE);
							circuitManagerMapper.insertOtnRoute(insert);

							// 经过数量加1
							update = new HashMap();
							update.put("NAME", "t_base_otn_crs");
							update.put("ID_NAME", "BASE_OTN_CRS_ID");
							update.put("ID_VALUE",
									map_otn.get("BASE_OTN_CRS_ID"));
							update.put("ID_NAME_2", "CIRCUIT_COUNT");
							update.put(
									"ID_VALUE_2",
									Integer.parseInt(map_otn.get(
											"CIRCUIT_COUNT").toString()) + 1);
							circuitManagerMapper.updateByParameter(update);

							// 查找下一条的交叉
							select = new HashMap();
							select.put("A_END_PTP",
									list_link.get(0).get("Z_END_PTP"));

							// 判断交叉连接z端的值,先获取z端类型
							String[] otn_type = map_otn.get("Z_TYPE")
									.toString().split(",");
							for (String name : otn_type) {
								select.put(name.replace("Z", "A"),
										map_otn.get(name));
							}

							List<Map> list_crs = circuitManagerMapper
									.getOtnCrsByCtp(select);
							// 找到则继续往下
							if (list_crs != null && list_crs.size() > 0) {
								//
								Map returnMap = insertToZteVir(list_crs, insert,
										temp, map_otn_cir, map_otn);
								temp = (Map) returnMap.get("temp");
								map_otn = (Map) returnMap.get("map_otn");
							} else {
								// 找不到交叉连接时，需要匹配当前ctp中的高阶位或者再向前进一位
								String type_name = sortType(
										map_otn.get("Z_TYPE").toString(), "Z_");

								// 逐步缩减字段匹配
								String[] type_z = type_name.split(",");

								// 如果type字段是多个，从小到大依次缩减
								List<Map> list_crs_s = new ArrayList<Map>();

								for (int j = type_z.length - 1; j > 0; j--) {
									// 查找下一条的交叉
									select = new HashMap();
									select.put("A_END_PTP", list_link.get(0)
											.get("Z_END_PTP"));

									// 判断交叉连接z端的值,先获取z端类型
									for (int k = 0; k < j; k++) {
										select.put(
												"A" + type_z[k].substring(1),
												map_otn.get(type_z[k]));
									}

									list_crs_s = circuitManagerMapper
											.getOtnCrsByCtp(select);

									// 如果存在值，则结束本次循环
									if (list_crs_s != null
											&& list_crs_s.size() > 0) {
										break;
									}
								}

								if (list_crs_s != null && list_crs_s.size() > 0) {
									// 找到交叉连接的处理方法
									Map returnMap = insertToZteVir(list_crs_s,
											insert, temp, map_otn_cir, map_otn);
									temp = (Map) returnMap.get("temp");
									map_otn = (Map) returnMap.get("map_otn");
									istrue = true;
								} else {
									
									Map returnMap = findCrsByUpAndDown(type_z,
											insert, temp, map_otn_cir, map_otn,
											list_link.get(0));
									temp = (Map) returnMap.get("temp");
									map_otn = (Map) returnMap.get("map_otn");
									istrue = (Boolean) returnMap.get("is_find");
								}

							}
						} else {
							// 找不到链路
							istrue = false;
						}

					}

			}
			if (numb > 50) {
				istrue = false;
			}
		} while (istrue);

	}
	
	
	/**
	 * 查询到交叉连接以后的入库处理逻辑
	 * 
	 * @param list_crs
	 *            查询到的交叉
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 */
	public Map insertToVir(List<Map> list_crs, Map insert, Map temp,
			Map crsVir, Map map_otn) {

		Map returnMap = new HashMap();
		// 找到交叉连接的处理方法

		for (int j = list_crs.size() - 1; j >= 0; j--) {
			// 如果找到多条连接。则先存入路由表，将最后一条取出来处理


		
			insert = new HashMap();
			insert.put("CIR_OTN_CIRCUIT_ID", crsVir.get("CIR_OTN_CIRCUIT_ID"));
			insert.put("CHAIN_ID", list_crs.get(j).get("BASE_OTN_CRS_ID"));
			insert.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_OTN_CRS);
			insert.put("AHEAD_CRS_ID", 1);
			if (j > 0) {
				insert.put("IS_COMPLETE", CommonDefine.FALSE);
			} else {
				// 如果是最后一条，则 将交叉连接赋值
				insert.put("IS_COMPLETE", CommonDefine.TRUE);
				map_otn = list_crs.get(0);

				if (map_otn.get("A_TYPE") != null
						&& !map_otn.get("A_TYPE").toString().isEmpty()) {
					// 给全局变量赋值
					String[] typeName_a = map_otn.get("A_TYPE").toString()
							.split(",");
					for (String name : typeName_a) {
						temp.put(name.substring(2), map_otn.get(name));
						// 如果oms 有2出现，则记录在OMS2
						if (name.contains("OMS") && map_otn.get(name) != null
								&& "2".equals(map_otn.get(name).toString())) {
							temp.put("OMS2", 2);
						}
					}
				}

				if (map_otn.get("Z_TYPE") != null
						&& !map_otn.get("Z_TYPE").toString().isEmpty()) {
					// 给全局变量赋值
					String[] typeName_z = map_otn.get("Z_TYPE").toString()
							.split(",");
					for (String name : typeName_z) {
						temp.put(name.substring(2), map_otn.get(name));
						// 如果oms 有2出现，则记录在OMS2
						if (name.contains("OMS") && map_otn.get(name) != null
								&& "2".equals(map_otn.get(name).toString())) {
							temp.put("OMS2", 2);
						}
					}
				}

				// circuitManagerMapper.insertOtnCrsVir(insert);
				// 经过数量加1
				Map update = new HashMap();
				update.put("NAME", "t_base_otn_crs");
				update.put("ID_NAME", "BASE_OTN_CRS_ID");
				update.put("ID_VALUE", list_crs.get(j).get("BASE_OTN_CRS_ID"));
				update.put("ID_NAME_2", "CIRCUIT_COUNT");
				update.put(
						"ID_VALUE_2",
						Integer.parseInt(list_crs.get(j).get("CIRCUIT_COUNT")
								.toString()) + 1);
				circuitManagerMapper.updateByParameter(update);
				returnMap.put("temp", temp);
				returnMap.put("map_otn", map_otn);

			}
			circuitManagerMapper.insertOtnRoute(insert);
		}
		return returnMap;

	}

	/**
	 *  中兴otn数据插入表中
	 * @param list_crs
	 * @param insert
	 * @param temp
	 * @param crsVir
	 * @param map_otn
	 * @return
	 */
	public Map insertToZteVir(List<Map> list_crs, Map insert, Map temp,
			Map crsVir, Map map_otn) {

		Map returnMap = new HashMap();
		// 找到交叉连接的处理方法
		
		List<Map> list = new ArrayList<Map>();

		for (int j = 0; j<list_crs.size(); j++) {
			// 如果找到多条连接。则先存入路由表，将最后一条取出来处理
			// 判断数据A端是否合格
			String [] aType = list_crs.get(j).get("A_TYPE").toString().split(",");
			boolean isPass = false;
			if(aType.length>0){
				for(int k = 0 ; k<aType.length;k++){
					if(!list_crs.get(j).get(aType[k]).toString().equals(temp.get(aType[k].substring(2)).toString())){
						isPass = true;
						break;
					}
					
				}
			}
			if(isPass){
				continue;
			}else{
				list.add(list_crs.get(j));
			}
		}
		if(list!=null && list.size()>0){
			for(int j = list.size() - 1; j >= 0; j--){
				insert = new HashMap();
				insert.put("CIR_OTN_CIRCUIT_ID", crsVir.get("CIR_OTN_CIRCUIT_ID"));
				insert.put("CHAIN_ID", list.get(j).get("BASE_OTN_CRS_ID"));
				insert.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_OTN_CRS);
				insert.put("AHEAD_CRS_ID", 1);
				if (j > 0) {
					insert.put("IS_COMPLETE", CommonDefine.FALSE);
				} else {
					// 如果是最后一条，则 将交叉连接赋值
					insert.put("IS_COMPLETE", CommonDefine.TRUE);
					map_otn = list.get(0);

					if (map_otn.get("A_TYPE") != null
							&& !map_otn.get("A_TYPE").toString().isEmpty()) {
						// 给全局变量赋值
						String[] typeName_a = map_otn.get("A_TYPE").toString()
								.split(",");
						for (String name : typeName_a) {
							temp.put(name.substring(2), map_otn.get(name));
							// 如果oms 有2出现，则记录在OMS2
							if (name.contains("OMS") && map_otn.get(name) != null
									&& "2".equals(map_otn.get(name).toString())) {
								temp.put("OMS2", 2);
							}
						}
					}

					if (map_otn.get("Z_TYPE") != null
							&& !map_otn.get("Z_TYPE").toString().isEmpty()) {
						// 给全局变量赋值
						String[] typeName_z = map_otn.get("Z_TYPE").toString()
								.split(",");
						for (String name : typeName_z) {
							temp.put(name.substring(2), map_otn.get(name));
							// 如果oms 有2出现，则记录在OMS2
							if (name.contains("OMS") && map_otn.get(name) != null
									&& "2".equals(map_otn.get(name).toString())) {
								temp.put("OMS2", 2);
							}
						}
					}

					// circuitManagerMapper.insertOtnCrsVir(insert);
					// 经过数量加1
					Map update = new HashMap();
					update.put("NAME", "t_base_otn_crs");
					update.put("ID_NAME", "BASE_OTN_CRS_ID");
					update.put("ID_VALUE", list.get(j).get("BASE_OTN_CRS_ID"));
					update.put("ID_NAME_2", "CIRCUIT_COUNT");
					update.put(
							"ID_VALUE_2",
							Integer.parseInt(list.get(j).get("CIRCUIT_COUNT")
									.toString()) + 1);
					circuitManagerMapper.updateByParameter(update);
					returnMap.put("temp", temp);
					returnMap.put("map_otn", map_otn);

				}
				circuitManagerMapper.insertOtnRoute(insert);
			}
		}

		return returnMap;

	}
	/**
	 * 创建单条电路
	 * 
	 * @param map
	 */
	public void CreateSingleOtnCrs(Map map_otn, Map map_otn_cir, Map temp_) {
		Map temp = new HashMap();
		// 定义变量
		if (temp_ == null) {
			temp.put("OS", 0);
			temp.put("OTS", 0);
			temp.put("OMS", 0);
			temp.put("OCH", 0);
			temp.put("ODU0", 0);
			temp.put("ODU1", 0);
			temp.put("ODU2", 0);
			temp.put("ODU3", 0);
			temp.put("DSR", 0);
			temp.put("OMS2", 0);
		} else {
			temp = temp_;
		}
		// 定义查询map变量
		Map select = null;
		Map insert = null;
		Map update = null;
		Map delete = null;
		// 将A端的值赋给全局变量
		// 根据虚拟交叉取出真实交叉的az端进行复制
		temp = setTypeValue(temp, map_otn);
		// if (map_otn.get("A_TYPE") !=
		// null&&!map_otn.get("A_TYPE").toString().isEmpty()) {
		// String[] otn_type = map_otn.get("A_TYPE").toString().split(",");
		// for (String otn_typeName : otn_type) {
		// temp.put(otn_typeName.substring(2), map_otn.get(otn_typeName));
		// // 如果oms 有2出现，则记录在OMS2
		// if (map_otn.get(otn_typeName) != null
		// && "2".equals(map_otn.get(otn_typeName).toString())) {
		// temp.put("OMS2", 2);
		// }
		// }
		//
		// }
		//
		// // 将z端的值赋给全局变量
		// if (map_otn.get("Z_TYPE") !=
		// null&&!map_otn.get("Z_TYPE").toString().isEmpty()) {
		// String[] otn_type = map_otn.get("Z_TYPE").toString().split(",");
		// for (String otn_typeName : otn_type) {
		// temp.put(otn_typeName.substring(2), map_otn.get(otn_typeName));
		// // 如果oms 有2出现，则记录在OMS2
		// if (map_otn.get(otn_typeName) != null
		// && "2".equals(map_otn.get(otn_typeName).toString())) {
		// temp.put("OMS2", 2);
		// }
		// }
		//
		// }

		boolean istrue = true;
		int numb = 0;
		do {
			numb++;
			select = hashMapSon("t_base_ptp", "BASE_PTP_ID",
					map_otn.get("Z_END_PTP"), null, null, null);

			List<Map> list_ptp = circuitManagerMapper.getByParameter(select);

			if (list_ptp != null && list_ptp.size() > 0) {
				Map map_ptp = list_ptp.get(0);
				// 如果z端是ptp，则先哦安端z端是否是边界点，如果是，则一条电路生成完成，如果不是，则查找交叉连接
				if (Integer.parseInt(map_ptp.get("PTP_FTP").toString()) == CommonDefine.CIR_PTP) {
					// 判断是否为边界点
					if (Integer.parseInt(map_ptp.get("PORT_TYPE").toString()) == CommonDefine.PORT_TYPE_EDGE_POINT) {

						Map info = null;
						// 先判断电路信息表中是否已经存在电路
						select = hashMapSon("t_cir_otn_circuit_info",
								"A_END_CTP", map_otn_cir.get("A_END_CTP"),
								"Z_END_CTP", map_otn.get("Z_END_CTP"), null);

						List<Map> list_info = circuitManagerMapper
								.getByParameter(select);
						// 如果存在，则变成多路经电路，增加电路数量
						if (list_info != null && list_info.size() > 0) {
							info = list_info.get(0);
							// 更新电路信息表的电路数
							int num = Integer.parseInt(info.get("CIR_COUNT")
									.toString()) + 1;
							update = new HashMap();
							update.put("NAME", "t_cir_otn_circuit_info");
							update.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
							update.put("ID_VALUE",
									info.get("CIR_OTN_CIRCUIT_INFO_ID"));
							update.put("ID_NAME_2", "CIR_COUNT");
							update.put("ID_VALUE_2", num);
							update.put("ID_NAME_3", "IS_LATEST_CREATE");
							update.put("ID_VALUE_3", CommonDefine.TRUE);

							circuitManagerMapper.updateByParameter(update);

							// // 将新的电路信息id绑向电路表
							//
							// update = hashMapSon("t_cir_otn_circuit",
							// "CIR_OTN_CIRCUIT_ID", map_otn_cir
							// .get("CIR_OTN_CIRCUIT_ID"),
							// "CIR_OTN_CIRCUIT_INFO_ID", info
							// .get("CIR_OTN_CIRCUIT_INFO_ID"), null);
							// circuitManagerMapper.updateByParameter(update);

							// 删除刚开始插入的电路信息记录
							delete = new HashMap();
							delete.put("NAME", "t_cir_otn_circuit_info");
							delete.put("ID_NAME", "CIR_OTN_CIRCUIT_INFO_ID");
							delete.put("ID_VALUE",
									map_otn_cir.get("CIR_OTN_CIRCUIT_INFO_ID"));
							circuitManagerMapper.deleteByParameter(delete);

						} else {
							// 如果不存在，将更新之前插入的记录
							update = new HashMap();
							update.put("CIR_OTN_CIRCUIT_INFO_ID",
									map_otn_cir.get("CIR_OTN_CIRCUIT_INFO_ID"));
							update.put("Z_END_CTP", map_otn.get("Z_END_CTP"));
							update.put("Z_END_PTP", map_otn.get("Z_END_PTP"));
							update.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
							update.put("IS_LATEST_CREATE", CommonDefine.TRUE);
							circuitManagerMapper.updateOtnInfo(update);

						}

						// 更新otn电路表
						map_otn_cir.put("Z_END_CTP", map_otn.get("Z_END_CTP"));
						map_otn_cir.put("Z_END_PTP", map_otn.get("Z_END_PTP"));
						map_otn_cir
								.put("DIRECTION", CommonDefine.DIRECTION_ONE);
						map_otn_cir.put("IS_COMPLETE_CIR", CommonDefine.TRUE);
						map_otn_cir.put("IS_MAIN_CIR", CommonDefine.TRUE);
						// 如果info为空，则不需要更改电路信息id
						if (info != null) {
							map_otn_cir.put("CIR_OTN_CIRCUIT_INFO_ID",
									info.get("CIR_OTN_CIRCUIT_INFO_ID"));
						}

						circuitManagerMapper.updateOtnCir(map_otn_cir);
						istrue = false;

					} else {
						// 查找链路
						select = hashMapSon("t_base_link", "A_END_PTP",
								map_otn.get("Z_END_PTP"), null, null, null);

						List<Map> list_link = circuitManagerMapper
								.getByParameter(select);

						if (list_link != null && list_link.size() > 0) {
							// 将链路存入路由
							insert = new HashMap();
							insert.put("CIR_OTN_CIRCUIT_ID",
									map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
							insert.put("CHAIN_ID",
									list_link.get(0).get("BASE_LINK_ID"));
							// 判断是内部还是外部link
							if (Integer.parseInt(list_link.get(0)
									.get("LINK_TYPE").toString()) == CommonDefine.LINK_IN) {
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_IN_LINK);
							} else {
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OUT_LINK);
							}
							insert.put("AHEAD_CRS_ID", 1);
							insert.put("IS_COMPLETE", CommonDefine.TRUE);
							circuitManagerMapper.insertOtnRoute(insert);

							// 查找下一条的交叉
							select = new HashMap();
							select.put("A_END_PTP",
									list_link.get(0).get("Z_END_PTP"));
							select.put("IS_VIRTUAL", CommonDefine.TRUE);

							// 判断交叉连接z端的值,先获取z端类型
							String[] otn_type = map_otn.get("Z_TYPE")
									.toString().split(",");
							for (String name : otn_type) {
								select.put(name.replace("Z", "A"),
										map_otn.get(name));
							}

							List<Map> list_crs = circuitManagerMapper
									.getOtnCrsByCtp(select);
							// 找到则继续往下
							if (list_crs != null && list_crs.size() > 0) {
								// 找到交叉连接的处理方法
								Map returnMap = crsToDB(list_crs, insert, temp,
										map_otn_cir, map_otn);
								temp = (Map) returnMap.get("temp");
								map_otn = (Map) returnMap.get("map_otn");
							} else {
								// 找不到交叉连接时，需要匹配当前ctp中的高阶位或者再向前进一位
								String type_name = sortType(
										map_otn.get("Z_TYPE").toString(), "Z_");

								// 逐步缩减字段匹配
								String[] type_z = type_name.split(",");

								// 如果type字段是多个，从小到大依次缩减
								List<Map> list_crs_s = new ArrayList<Map>();

								for (int j = type_z.length - 1; j > 0; j--) {
									// 查找下一条的交叉
									select = new HashMap();
									select.put("A_END_PTP", list_link.get(0)
											.get("Z_END_PTP"));
									select.put("IS_VIRTUAL", CommonDefine.TRUE);
									// 判断交叉连接z端的值,先获取z端类型
									for (int k = 0; k < j; k++) {
										select.put(
												"A" + type_z[k].substring(1),
												map_otn.get(type_z[k]));
									}

									list_crs_s = circuitManagerMapper
											.getOtnCrsByCtp(select);

									// 如果存在值，则结束本次循环
									if (list_crs_s != null
											&& list_crs_s.size() > 0) {
										break;
									}
								}

								if (list_crs_s != null && list_crs_s.size() > 0) {
									// 找到交叉连接的处理方法
									Map returnMap = crsToDB(list_crs_s, insert,
											temp, map_otn_cir, map_otn);
									temp = (Map) returnMap.get("temp");
									map_otn = (Map) returnMap.get("map_otn");
									istrue = true;
								} else {
									Map returnMap = upToFindOtnCrs(type_z,
											insert, temp, map_otn_cir, map_otn,
											list_link);
									temp = (Map) returnMap.get("temp");
									map_otn = (Map) returnMap.get("map_otn");
									istrue = (Boolean) returnMap.get("is_find");
								}

							}
						} else {
							// 找不到链路
							istrue = false;
						}

					}

				} else {
					// 如果是ftp，则继续查找下一跳,则表示电路未完成，且为交叉连交叉模式
					// 先判断交叉是否为空

					// select = hashMapSon("t_base_otn_ctp", "A_END_PTP",
					// map_otn
					// .get("Z_END_PTP"), null, null, null);
					//
					// List<Map> list_next = circuitManagerMapper
					// .getByParameter(select);

					// 判断ctp是否为空,也可以通过z_type直接判断

					// select = hashMapSon("t_base_otn_ctp", "Z_END_CTP",
					// map_otn.get("Z_END_CTP"), null, null, null);
					// Map map_ctp = circuitManagerMapper.getByParameter(select)
					// .get(0);

					// 判断ctp是否与前面相同,暂时用
					// if (Integer.parseInt(map_ctp.get("IS_CTP").toString()) ==
					// CommonDefine.FALSE) {

					if (map_otn.get("Z_TYPE") != null
							&& !map_otn.get("Z_TYPE").toString().isEmpty()) {

						// 如果不为空，则查询为空的交叉,此处如果速度过慢，可以直接通过交叉表进行判断，z_TYPE
						select = new HashMap();
						select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
						select.put("IS_CTP", CommonDefine.FALSE);
						select.put("PTP_FTP", CommonDefine.CIR_FTP);

						String[] type_z = map_otn.get("Z_TYPE").toString()
								.split(",");
						for (String name : type_z) {
							select.put(name.replace("Z", "A"),
									map_otn.get(name));
						}

						List<Map> list_null = circuitManagerMapper
								.getNextOtnCrs(select);

						if (list_null != null && list_null.size() > 0) {
							// 开始遍历
							for (int j = list_null.size() - 1; j >= 0; j--) {

								Map map_crs = list_null.get(j);
								// 将数据存入数据库，标注未完成,处理第一条

								insert = new HashMap();

								insert.put("CIR_OTN_CIRCUIT_ID",
										map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
								insert.put("CHAIN_ID",
										list_null.get(j).get("BASE_OTN_CRS_ID"));
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OTN_CRS);
								insert.put("AHEAD_CRS_ID", 1);
								if (j > 0) {
									insert.put("IS_COMPLETE",
											CommonDefine.FALSE);
								} else {
									// 如果是最后一条，则 将交叉连接赋值
									insert.put("IS_COMPLETE", CommonDefine.TRUE);
									map_otn = list_null.get(0);

									// 给全局变量赋值
									String[] typeName = map_otn.get("Z_TYPE")
											.toString().split(",");
									for (String name : typeName) {
										temp.put(name.substring(2),
												map_otn.get(name));
										// 如果oms 有2出现，则记录在OMS2
										if (name.contains("OMS")
												&& map_otn.get(name) != null
												&& "2".equals(map_otn.get(name)
														.toString())) {
											temp.put("OMS2", 2);
										}
									}

								}
								circuitManagerMapper.insertOtnRoute(insert);
								/** 待完善 */
							}
						} else {
							// 同级没有查找到，向上越级查找

							Map returnMap = upToFindOtnCrsANull(type_z, insert,
									temp, map_otn_cir, map_otn);
							temp = (Map) returnMap.get("temp");
							map_otn = (Map) returnMap.get("map_otn");
							istrue = (Boolean) returnMap.get("is_find");
						}

					} else {

						// 如果为空，则查询不为空的交叉
						select = new HashMap();
						select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
						select.put("IS_CTP", CommonDefine.TRUE);
						select.put("PTP_FTP", CommonDefine.CIR_FTP);

						// 将上一跳的z端时隙值，作为参数放入
						String[] type_a = map_otn.get("A_TYPE").toString()
								.split(",");
						for (String name : type_a) {
							select.put("Z" + name.substring(1),
									map_otn.get(name));

						}

						List<Map> list_notnull = circuitManagerMapper
								.getNextOtnCrs(select);

						if (list_notnull != null && list_notnull.size() > 0) {
							// 开始遍历
							for (int j = list_notnull.size() - 1; j >= 0; j--) {

								insert = new HashMap();

								insert.put("CIR_OTN_CIRCUIT_ID",
										map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
								insert.put(
										"CHAIN_ID",
										list_notnull.get(j).get(
												"BASE_OTN_CRS_ID"));
								insert.put("CHAIN_TYPE",
										CommonDefine.CHAIN_TYPE_OTN_CRS);
								insert.put("AHEAD_CRS_ID", 1);
								if (j > 0) {
									insert.put("IS_COMPLETE",
											CommonDefine.FALSE);
								} else {
									// 如果是最后一条，则 将交叉连接赋值
									insert.put("IS_COMPLETE", CommonDefine.TRUE);
									map_otn = list_notnull.get(0);

									// 给全局变量赋值
									String[] typeName_a = map_otn.get("A_TYPE")
											.toString().split(",");
									for (String name : typeName_a) {
										temp.put(name.substring(2),
												map_otn.get(name.substring(2)));
										// 如果oms 有2出现，则记录在OMS2
										if (name.contains("OMS")
												&& map_otn.get(name) != null
												&& "2".equals(map_otn.get(name)
														.toString())) {
											temp.put("OMS2", 2);
										}
									}
									// 给全局变量赋值
									String[] typeName_z = map_otn.get("Z_TYPE")
											.toString().split(",");
									for (String name : typeName_z) {
										temp.put(name.substring(2),
												map_otn.get(name.substring(2)));
										// 如果oms 有2出现，则记录在OMS2
										if (name.contains("OMS")
												&& map_otn.get(name) != null
												&& "2".equals(map_otn.get(name)
														.toString())) {
											temp.put("OMS2", 2);
										}
									}

								}
								circuitManagerMapper.insertOtnRoute(insert);
							}
							/** 待完善 */
						} else {
							// 分部查找 1.向上 2。向下
							// 向上查询
							Map returnMap = upToFindOtnCrsZNull(type_a, insert,
									temp, map_otn_cir, map_otn);

							temp = (Map) returnMap.get("temp");
							map_otn = (Map) returnMap.get("map_otn");
							istrue = (Boolean) returnMap.get("is_find");
						}

					}

				}
			}
			if (numb > 50) {
				istrue = false;
			}
		} while (istrue);

	}

	/**
	 * 根据虚拟交叉id遍历给temp变量赋值
	 * 
	 * @param temp
	 *            全局变量
	 * @param otn
	 *            虚拟交叉
	 * @return
	 */
	public Map setTypeValue(Map temp, Map otn) {
		Map select = null;
		select = hashMapSon("t_base_otn_crs_relation", "VIR_CRS_ID",
				otn.get("BASE_OTN_CRS_ID"), "CHAIN_TYPE",
				CommonDefine.CHAIN_TYPE_OTN_CRS, null);
		List<Map> listCrs = circuitManagerMapper.getByParameter(select);
		for (Map crs : listCrs) {
			select = hashMapSon("t_base_otn_crs", "BASE_OTN_CRS_ID",
					crs.get("CHAIN_ID"), null, null, null);
			Map otnCrs = circuitManagerMapper.getByParameter(select).get(0);
			if (otnCrs.get("A_TYPE") != null
					&& !otnCrs.get("A_TYPE").toString().isEmpty()) {
				String[] otn_type = otnCrs.get("A_TYPE").toString().split(",");
				for (String otn_typeName : otn_type) {
					temp.put(otn_typeName.substring(2),
							otnCrs.get(otn_typeName));
					// 如果oms 有2出现，则记录在OMS2
					if (otn_typeName.contains("OMS")
							&& otnCrs.get(otn_typeName) != null
							&& "2".equals(otnCrs.get(otn_typeName).toString())) {
						temp.put("OMS2", 2);
					}
				}
			}
			// 将z端的值赋给全局变量
			if (otnCrs.get("Z_TYPE") != null
					&& !otnCrs.get("Z_TYPE").toString().isEmpty()) {
				String[] otn_type = otnCrs.get("Z_TYPE").toString().split(",");
				for (String otn_typeName : otn_type) {
					temp.put(otn_typeName.substring(2),
							otnCrs.get(otn_typeName));
					// 如果oms 有2出现，则记录在OMS2
					if (otn_typeName.contains("OMS")
							&& otnCrs.get(otn_typeName) != null
							&& "2".equals(otnCrs.get(otn_typeName).toString())) {
						temp.put("OMS2", 2);
					}
				}
			}
		}
		return temp;

	}

	/**
	 * 进阶查询，通过链路跳转
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map upToFindOtnCrs(String[] type_z, Map insert, Map temp,
			Map map_otn_cir, Map map_otn, List<Map> list_link) {

		Map returnMap = new HashMap();
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", list_link.get(0).get("Z_END_PTP"));
			select.put("IS_VIRTUAL", CommonDefine.TRUE);
			// 依次向上查找
			if (j == 0) {
				select.put("A_DSR", "1");
			} else if (j == 1) {
				select.put("A_ODU0", "1");
			} else if (j == 2) {
				select.put("A_ODU1", "1");
			} else if (j == 3) {
				select.put("A_ODU2", "1");
			} else if (j == 4) {
				select.put("A_ODU3", "1");
			} else if (j == 5) {
				select.put("A_OCH", "1");
			} else if (j == 6) {
				select.put("A_OMS", "1");
			} else if (j == 7) {
				select.put("A_OTS", "1");
			} else if (j == 8) {
				select.put("A_OS", "1");
			}

			List<Map> list_up = circuitManagerMapper.getOtnCrsUp(select);
			if (list_up != null && list_up.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = crsToDB(list_up, insert, temp, map_otn_cir, map_otn);
				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
			List<Map> list_crs_down = new ArrayList<Map>();
			for (int i = 0; i < type_z.length; i++) {
				// 查找下一条的交叉
				select = new HashMap();
				select.put("A_END_PTP", list_link.get(0).get("Z_END_PTP"));
				select.put("IS_VIRTUAL", CommonDefine.TRUE);
				// 判断交叉连接z端的值,先获取z端类型
				for (int k = i; k < type_z.length; k++) {
					select.put(type_z[k], map_otn.get(type_z[k]));
				}

				list_crs_down = circuitManagerMapper.getOtnCrsByCtp(select);

				if (list_crs_down != null && list_crs_down.size() > 0) {
					break;
				}
			}
			if (list_crs_down != null && list_crs_down.size() > 0) {
				// 找到交叉连接的处理方法，可以有多重
				returnMap = crsToDB(list_crs_down, insert, temp, map_otn_cir,
						map_otn);
				is_find = true;
				returnMap.put("is_find", is_find);
			} else {
				returnMap = downToFindOtnCrs(type_z, insert, temp, map_otn_cir,
						map_otn, list_link);

			}
		}
		return returnMap;
	}

	/**
	 * 进阶查询，通过链路跳转
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map upToFindOtnCrsVir(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn, List<Map> list_link) {

		Map returnMap = new HashMap();
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", list_link.get(0).get("Z_END_PTP"));

			// 依次向上查找
			if (j == 0) {
				if (temp.get("DSR") != null
						&& !"0".equals(temp.get("DSR").toString())
						&& !temp.get("DSR").toString().isEmpty()) {
					select.put("A_DSR", temp.get("DSR"));
				}
			} else if (j == 1) {
				if (temp.get("ODU0") != null
						&& !"0".equals(temp.get("ODU0").toString())
						&& !temp.get("ODU0").toString().isEmpty()) {
					select.put("A_ODU0", temp.get("ODU0"));
				}

			} else if (j == 2) {
				if (temp.get("ODU1") != null
						&& !"0".equals(temp.get("ODU1").toString())
						&& !temp.get("ODU1").toString().isEmpty()) {
					select.put("A_ODU1", temp.get("ODU1"));
				}

			} else if (j == 3) {
				if (temp.get("ODU2") != null
						&& !"0".equals(temp.get("ODU2").toString())
						&& !temp.get("ODU2").toString().isEmpty()) {
					select.put("A_ODU2", temp.get("ODU2"));
				}

			} else if (j == 4) {
				if (temp.get("ODU3") != null
						&& !"0".equals(temp.get("ODU3").toString())
						&& !temp.get("ODU3").toString().isEmpty()) {
					select.put("A_ODU3", temp.get("ODU3"));
				}

			} else if (j == 5) {
				if (temp.get("OCH") != null
						&& !"0".equals(temp.get("OCH").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					select.put("A_OCH", temp.get("OCH"));
				}

			} else if (j == 6) {
				if (temp.get("OMS") != null
						&& !"0".equals(temp.get("OMS").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					select.put("A_OMS", temp.get("OMS"));
				}

			} else if (j == 7) {
				if (temp.get("OTS") != null
						&& !"0".equals(temp.get("OTS").toString())
						&& !temp.get("OTS").toString().isEmpty()) {
					select.put("A_OTS", temp.get("OTS"));
				}

			} else if (j == 8) {
				if (temp.get("OS") != null
						&& !"0".equals(temp.get("OS").toString())
						&& !temp.get("OS").toString().isEmpty()) {
					select.put("A_OS", temp.get("OS"));
				}

			}

			List<Map> list_up = circuitManagerMapper.getOtnCrsUp(select);
			List<Map> listUp = new ArrayList<Map>();
			for (Map up : list_up) {
				// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
				int k = 0;
				// 判断上一交叉的z端最大级别
				if (up.get("A_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					k = 8;
				} else if (up.get("A_TYPE").toString().contains("OTS")) {
					k = 7;
				} else if (up.get("A_TYPE").toString().contains("OMS")) {
					k = 6;
				} else if (up.get("A_TYPE").toString().contains("OCH")) {
					k = 5;
				} else if (up.get("A_TYPE").toString().contains("ODU3")) {
					k = 4;
				} else if (up.get("A_TYPE").toString().contains("ODU2")) {
					k = 3;
				} else if (up.get("A_TYPE").toString().contains("ODU1")) {
					k = 2;
				} else if (up.get("A_TYPE").toString().contains("ODU0")) {
					k = 1;
				} else if (up.get("A_TYPE").toString().contains("DSR")) {
					k = 0;
				}
				if (k >= j) {
					listUp.add(up);
				}
			}
			if (listUp != null && listUp.size() > 0) {

				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToVir(listUp, insert, temp, crsVir, map_otn);
				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
			List<Map> list_crs_down = new ArrayList<Map>();
			for (int i = 0; i < type_z.length; i++) {
				// 查找下一条的交叉
				select = new HashMap();
				select.put("A_END_PTP", list_link.get(0).get("Z_END_PTP"));

				// 判断交叉连接z端的值,先获取z端类型
				for (int k = i; k < type_z.length; k++) {
					select.put(type_z[k].replace("Z", "A"),
							map_otn.get(type_z[k]));
				}

				list_crs_down = circuitManagerMapper.getOtnCrsByCtp(select);

				if (list_crs_down != null && list_crs_down.size() > 0) {
					break;
				}
			}
			if (list_crs_down != null && list_crs_down.size() > 0) {
				// 找到交叉连接的处理方法，可以有多重
				returnMap = insertToVir(list_crs_down, insert, temp, crsVir,
						map_otn);
				is_find = true;
				returnMap.put("is_find", is_find);
			} else {

				returnMap = downToFindOtnCrsVir(type_z, insert, temp, crsVir,
						map_otn, list_link);
			}
		}
		return returnMap;
	}
	
	
	public Map findCrsByUpAndDown(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn, Map link) {

		Map returnMap = new HashMap();
		returnMap.put("map_otn", map_otn);
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", link.get("Z_END_PTP"));
			select.put("Z_END_PTP", link.get("A_END_PTP"));
			boolean isCheck = false;
			// 依次向上查找
			if (j == 0) {
				if (temp.get("DSR") != null
						&& !"0".equals(temp.get("DSR").toString())
						&& !temp.get("DSR").toString().isEmpty()) {
					select.put("A_DSR", temp.get("DSR"));
					isCheck = true;
				}
			} else if (j == 1) {
				if (temp.get("ODU0") != null
						&& !"0".equals(temp.get("ODU0").toString())
						&& !temp.get("ODU0").toString().isEmpty()) {
					select.put("A_ODU0", temp.get("ODU0"));
					isCheck = true;
				}

			} else if (j == 2) {
				if (temp.get("ODU1") != null
						&& !"0".equals(temp.get("ODU1").toString())
						&& !temp.get("ODU1").toString().isEmpty()) {
					select.put("A_ODU1", temp.get("ODU1"));
					isCheck = true;
				}

			} else if (j == 3) {
				if (temp.get("ODU2") != null
						&& !"0".equals(temp.get("ODU2").toString())
						&& !temp.get("ODU2").toString().isEmpty()) {
					select.put("A_ODU2", temp.get("ODU2"));
					isCheck = true;
				}

			} else if (j == 4) {
				if (temp.get("ODU3") != null
						&& !"0".equals(temp.get("ODU3").toString())
						&& !temp.get("ODU3").toString().isEmpty()) {
					select.put("A_ODU3", temp.get("ODU3"));
					isCheck = true;
				}

			} else if (j == 5) {
				if (temp.get("OCH") != null
						&& !"0".equals(temp.get("OCH").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					select.put("A_OCH", temp.get("OCH"));
					isCheck = true;
				}

			} else if (j == 6) {
				if (temp.get("OMS") != null
						&& !"0".equals(temp.get("OMS").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					select.put("A_OMS", temp.get("OMS"));
					isCheck = true;
				}

			} else if (j == 7) {
				if (temp.get("OTS") != null
						&& !"0".equals(temp.get("OTS").toString())
						&& !temp.get("OTS").toString().isEmpty()) {
					select.put("A_OTS", temp.get("OTS"));
					isCheck = true;
				}

			} else if (j == 8) {
				if (temp.get("OS") != null
						&& !"0".equals(temp.get("OS").toString())
						&& !temp.get("OS").toString().isEmpty()) {
					select.put("A_OS", temp.get("OS"));
					isCheck = true;
				}

			}
			// 如果上述条件无法匹配，则结束本次循环
			if(!isCheck){
				continue;
			}
			List<Map> list_up = circuitManagerMapper.selectZteOtnNextCrs(select);
			List<Map> listUp = new ArrayList<Map>();
			for (Map up : list_up) {
				// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
				int k = 0;
				// 判断上一交叉的z端最大级别
				if (up.get("A_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					k = 8;
				} else if (up.get("A_TYPE").toString().contains("OTS")) {
					k = 7;
				} else if (up.get("A_TYPE").toString().contains("OMS")) {
					k = 6;
				} else if (up.get("A_TYPE").toString().contains("OCH")) {
					k = 5;
				} else if (up.get("A_TYPE").toString().contains("ODU3")) {
					k = 4;
				} else if (up.get("A_TYPE").toString().contains("ODU2")) {
					k = 3;
				} else if (up.get("A_TYPE").toString().contains("ODU1")) {
					k = 2;
				} else if (up.get("A_TYPE").toString().contains("ODU0")) {
					k = 1;
				} else if (up.get("A_TYPE").toString().contains("DSR")) {
					k = 0;
				}
				if (k >= j) {
					listUp.add(up);
				}
			}
			if (listUp != null && listUp.size() > 0) {

				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToZteVir(listUp, insert, temp, crsVir, map_otn);
				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
//			List<Map> list_crs_down = new ArrayList<Map>();
//			for (int i = 1; i < type_z.length; i++) {
//				if(type_z.length<=1){
//					break;
//				}
//				// 查找下一条的交叉
//				select = new HashMap();
//				select.put("A_END_PTP", link.get("Z_END_PTP"));
//				select.put("Z_END_PTP", link.get("A_END_PTP"));
//				// 判断交叉连接z端的值,先获取z端类型
//				for (int k = i; k < type_z.length; k++) {
//					select.put(type_z[k].replace("Z", "A"),
//							map_otn.get(type_z[k]));
//				}
//
//				list_crs_down = circuitManagerMapper.selectZteOtnNextCrs(select);
//
//				if (list_crs_down != null && list_crs_down.size() > 0) {
//					break;
//				}
//			}
//			if (list_crs_down != null && list_crs_down.size() > 0) {
//				// 找到交叉连接的处理方法，可以有多重
//				returnMap = insertToVir(list_crs_down, insert, temp, crsVir,
//						map_otn);
//				is_find = true;
//				returnMap.put("is_find", is_find);
//			} else {

//				returnMap = downToFindOtnCrsVir(type_z, insert, temp, crsVir,
//						map_otn, list_link);
				

				

				// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
				boolean is_oms_two = true;
				n = 0;
				// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
				if (type_z[type_z.length - 1].contains("OS")) {
					n = 0;
				} else if (type_z[type_z.length - 1].contains("OTS")) {
					n = 1;
				} else if (type_z[type_z.length - 1].contains("OMS")) {
					n = 2;
				} else if (type_z[type_z.length - 1].contains("OCH")) {
					n = 3;
				} else if (type_z[type_z.length - 1].contains("ODU3")) {
					n = 4;
				} else if (type_z[type_z.length - 1].contains("ODU2")) {
					n = 5;
				} else if (type_z[type_z.length - 1].contains("ODU1")) {
					n = 6;
				} else if (type_z[type_z.length - 1].contains("ODU0")) {
					n = 7;
				} else if (type_z[type_z.length - 1].contains("DSR")) {
					n = 8;
				}

				// 根据当前所在的层级，逐级向下层遍历
				for (int j = n + 1; j < 9; j++) {
					select = new HashMap();
					select.put("A_END_PTP", link.get("Z_END_PTP"));
					select.put("Z_END_PTP", link.get("A_END_PTP"));
					// 想下遍历时，需要匹配全局变量，如果为空，则跳过
					if (j == 0) {
						if (temp.get("OS") != null
								&& !"0".equals(temp.get("OS").toString())
								&& !temp.get("OS").toString().isEmpty()) {
							select.put("A_OS", temp.get("OS"));
						} else {
							continue;
						}
					} else if (j == 1) {
						if (temp.get("OTS") != null
								&& !"0".equals(temp.get("OTS").toString())
								&& !temp.get("OTS").toString().isEmpty()) {
							select.put("A_OTS", temp.get("OTS"));
						} else {
							continue;
						}
					} else if (j == 2) {
						// ots 有1和2 两个值，默认先去找2 再去找1
						if (temp.get("OMS") != null
								&& !"0".equals(temp.get("OMS").toString())
								&& !temp.get("OMS").toString().isEmpty()) {
							if (temp.get("OMS2") != null
									&& "2".equals(temp.get("OMS2").toString())
									&& is_oms_two) {
								select.put("A_OMS", 2);
							} else {
								select.put("A_OMS", 1);
							}
						} else {
							continue;
						}

						// select.put("A_OMS", temp.get("OMS"));
					} else if (j == 3) {
						if (temp.get("OCH") != null
								&& !"0".equals(temp.get("OCH").toString())
								&& !temp.get("OCH").toString().isEmpty()) {
							select.put("A_OCH", temp.get("OCH"));
						} else {
							continue;
						}
					} else if (j == 4) {
						if (temp.get("ODU3") != null
								&& !"0".equals(temp.get("ODU3").toString())
								&& !temp.get("ODU3").toString().isEmpty()) {
							select.put("A_ODU3", temp.get("ODU3"));
						} else {
							continue;
						}
					} else if (j == 5) {
						if (temp.get("ODU2") != null
								&& !"0".equals(temp.get("ODU2").toString())
								&& !temp.get("ODU2").toString().isEmpty()) {
							select.put("A_ODU2", temp.get("ODU2"));
						} else {
							continue;
						}
					} else if (j == 6) {
						if (temp.get("ODU1") != null
								&& !"0".equals(temp.get("ODU1").toString())
								&& !temp.get("ODU1").toString().isEmpty()) {
							select.put("A_ODU1", temp.get("ODU1"));
						} else {
							continue;
						}
					} else if (j == 7) {
						if (temp.get("ODU0") != null
								&& !"0".equals(temp.get("ODU0").toString())
								&& !temp.get("ODU0").toString().isEmpty()) {
							select.put("A_ODU0", temp.get("ODU0"));
						} else {
							continue;
						}
					} else if (j == 8) {
						if (temp.get("DSR") != null
								&& !"0".equals(temp.get("DSR").toString())
								&& !temp.get("DSR").toString().isEmpty()) {
							select.put("A_DSR", temp.get("DSR"));
						} else {
							continue;
						}
					}

					// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
					List<Map> list_down = circuitManagerMapper.selectZteOtnNextCrs(select);
					if (list_down != null && list_down.size() > 0) {
						// 向上如果找到多条交叉时，只去第一条处理
						returnMap = insertToZteVir(list_down, insert, temp, crsVir,
								map_otn);
						is_find = true;
						// 结束整个循环
						break;
					}
					// 专门处理 ots的特殊情况
					if (is_oms_two) {
						j--;
						is_oms_two = false;
					}
				}
				returnMap.put("is_find", is_find);
				//return returnMap;
			
			}
		//}
		return returnMap;
	}

	/**
	 * 进阶查询，下一跳交叉A端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map upToFindOtnCrsANull(String[] type_z, Map insert, Map temp,
			Map map_otn_cir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
			select.put("IS_VIRTUAL", CommonDefine.TRUE);
			// 依次向上查找
			if (j == 0) {
				select.put("Z_DSR", "1");
			} else if (j == 1) {
				select.put("Z_ODU0", "1");
			} else if (j == 2) {
				select.put("Z_ODU1", "1");
			} else if (j == 3) {
				select.put("Z_ODU2", "1");
			} else if (j == 4) {
				select.put("Z_ODU3", "1");
			} else if (j == 5) {
				select.put("Z_OCH", "1");
			} else if (j == 6) {
				select.put("Z_OMS", "1");
			} else if (j == 7) {
				select.put("Z_OTS", "1");
			} else if (j == 8) {
				select.put("Z_OS", "1");
			}

			List<Map> list_up = circuitManagerMapper.getOtnCrsUp(select);
			if (list_up != null && list_up.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = crsToDB(list_up, insert, temp, map_otn_cir, map_otn);
				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
			List<Map> list_crs_down = new ArrayList<Map>();
			for (int i = 0; i < type_z.length; i++) {
				// 查找下一条的交叉
				select = new HashMap();
				select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
				select.put("IS_VIRTUAL", CommonDefine.TRUE);
				// 判断交叉连接z端的值,先获取z端类型
				for (int k = i; k < type_z.length; k++) {
					select.put(type_z[k], map_otn.get(type_z[k]));
				}

				list_crs_down = circuitManagerMapper.getOtnCrsByCtp(select);

				if (list_crs_down != null && list_crs_down.size() > 0) {
					break;
				}
			}
			if (list_crs_down != null && list_crs_down.size() > 0) {
				// 找到交叉连接的处理方法，可以有多重
				returnMap = crsToDB(list_crs_down, insert, temp, map_otn_cir,
						map_otn);
				is_find = true;
				returnMap.put("is_find", is_find);
			} else {
				returnMap = downToFindOtnCrsANull(type_z, insert, temp,
						map_otn_cir, map_otn);
			}
		}
		return returnMap;
	}

	/**
	 * 进阶查询，下一跳交叉A端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map upToFindOtnCrsANullVir(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));

			// // 依次向上查找
			// if (j == 0) {
			// select.put("Z_DSR", "1");
			// } else if (j == 1) {
			// select.put("Z_ODU0", "1");
			// } else if (j == 2) {
			// select.put("Z_ODU1", "1");
			// } else if (j == 3) {
			// select.put("Z_ODU2", "1");
			// } else if (j == 4) {
			// select.put("Z_ODU3", "1");
			// } else if (j == 5) {
			// select.put("Z_OCH", "1");
			// } else if (j == 6) {
			// select.put("Z_OMS", "1");
			// } else if (j == 7) {
			// select.put("Z_OTS", "1");
			// } else if (j == 8) {
			// select.put("Z_OS", "1");
			// }

			// 依次向上查找
			if (j == 0) {
				if (temp.get("DSR") != null
						&& !"0".equals(temp.get("DSR").toString())
						&& !temp.get("DSR").toString().isEmpty()) {
					select.put("Z_DSR", temp.get("DSR"));
				}
			} else if (j == 1) {
				if (temp.get("ODU0") != null
						&& !"0".equals(temp.get("ODU0").toString())
						&& !temp.get("ODU0").toString().isEmpty()) {
					select.put("Z_ODU0", temp.get("ODU0"));
				}

			} else if (j == 2) {
				if (temp.get("ODU1") != null
						&& !"0".equals(temp.get("ODU1").toString())
						&& !temp.get("ODU1").toString().isEmpty()) {
					select.put("Z_ODU1", temp.get("ODU1"));
				}

			} else if (j == 3) {
				if (temp.get("ODU2") != null
						&& !"0".equals(temp.get("ODU2").toString())
						&& !temp.get("ODU2").toString().isEmpty()) {
					select.put("Z_ODU2", temp.get("ODU2"));
				}

			} else if (j == 4) {
				if (temp.get("ODU3") != null
						&& !"0".equals(temp.get("ODU3").toString())
						&& !temp.get("ODU3").toString().isEmpty()) {
					select.put("Z_ODU3", temp.get("ODU3"));
				}

			} else if (j == 5) {
				if (temp.get("OCH") != null
						&& !"0".equals(temp.get("OCH").toString())
						&& !temp.get("OCH").toString().isEmpty()) {
					select.put("Z_OCH", temp.get("OCH"));
				}

			} else if (j == 6) {
				if (temp.get("OMS") != null
						&& !"0".equals(temp.get("OMS").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					select.put("Z_OMS", temp.get("OMS"));
				}

			} else if (j == 7) {
				if (temp.get("OTS") != null
						&& !"0".equals(temp.get("OTS").toString())
						&& !temp.get("OTS").toString().isEmpty()) {
					select.put("Z_OTS", temp.get("OTS"));
				}

			} else if (j == 8) {
				if (temp.get("OS") != null
						&& !"0".equals(temp.get("OS").toString())
						&& !temp.get("OS").toString().isEmpty()) {
					select.put("Z_OS", temp.get("OS"));
				}

			}

			List<Map> list_up = circuitManagerMapper.getOtnCrsUp(select);
			// 判断是否有值是向下级别的
			List<Map> listUp = new ArrayList<Map>();
			for(Map crsUp :list_up){
				// 向上越级可以无视此规则
				
				// 判断查询的结果是否a端为空，防止正反交叉现象
				if(crsUp.get("A_TYPE")!=null ||!crsUp.get("A_TYPE").toString().isEmpty() ){
					continue;
				}
				
				// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
				int k = -1;
				// 判断上一交叉的z端最大级别
				if (crsUp.get("Z_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					k = 8;
				} else if (crsUp.get("Z_TYPE").toString().contains("OTS")) {
					k = 7;
				} else if (crsUp.get("Z_TYPE").toString().contains("OMS")) {
					k = 6;
				} else if (crsUp.get("Z_TYPE").toString().contains("OCH")) {
					k = 5;
				} else if (crsUp.get("Z_TYPE").toString().contains("ODU3")) {
					k = 4;
				} else if (crsUp.get("Z_TYPE").toString().contains("ODU2")) {
					k = 3;
				} else if (crsUp.get("Z_TYPE").toString().contains("ODU1")) {
					k = 2;
				} else if (crsUp.get("Z_TYPE").toString().contains("ODU0")) {
					k = 1;
				} else if (crsUp.get("Z_TYPE").toString().contains("DSR")) {
					k = 0;
				}
				
				int m = 0;
				// 判断上一交叉的z端最大级别
				if (map_otn.get("Z_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					m = 8;
				} else if (map_otn.get("Z_TYPE").toString().contains("OTS")) {
					m = 7;
				} else if (map_otn.get("Z_TYPE").toString().contains("OMS")) {
					m = 6;
				} else if (map_otn.get("Z_TYPE").toString().contains("OCH")) {
					m = 5;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU3")) {
					m = 4;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU2")) {
					m = 3;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU1")) {
					m = 2;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU0")) {
					m = 1;
				} else if (map_otn.get("Z_TYPE").toString().contains("DSR")) {
					m = 0;
				}
				if(k>=m){
					
					listUp.add(crsUp);
					continue;
				}
//				boolean isRight = true; // 交叉是否符合标准
//				if(crsUp.get("Z_TYPE")!=null&& !crsUp.get("Z_TYPE").toString().isEmpty()){
//					String [] typeZ = crsUp.get("Z_TYPE").toString().split(",");
//					for(String name :typeZ){
//						if(temp.get(name.substring(2))==null ||!temp.get(name.substring(2)).toString()
//								.equals(crsUp.get(name).toString())){
//							// 结束本次循环
//							isRight = false;
//							break;
//						}
//					}
//				}
//				if(!isRight){
//					continue;
//				}else{
//					listNull.add(crsNull);
//				}
			}
			if (listUp != null && listUp.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToVir(listUp, insert, temp, crsVir, map_otn);
				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
			List<Map> list_crs_down = new ArrayList<Map>();
			List<Map> list_cr_d = new ArrayList<Map>();
			for (int i = 0; i < type_z.length; i++) {
				// 查找下一条的交叉
				select = new HashMap();
				select.put("A_END_PTP", map_otn.get("Z_END_PTP"));

				// 判断交叉连接z端的值,先获取z端类型
				for (int k = i; k < type_z.length; k++) {
					select.put(type_z[k], map_otn.get(type_z[k]));
				}

				list_crs_down = circuitManagerMapper.getOtnCrsByCtp(select);

				
				if (list_crs_down != null && list_crs_down.size() > 0) {
					
					// 遍历结果，看a端是否为空
					for(int ii = 0;ii<list_crs_down.size();ii++){
						if(list_crs_down.get(ii).get("A_TYPE")!=null || !list_crs_down.get(ii).get("A_TYPE").toString().isEmpty()){
							continue;
						}else{
							list_cr_d.add(list_crs_down.get(ii));
						}
					}
					if(list_cr_d!=null &&list_cr_d.size()>0){
						break;
					}
					
				}
			}
			if (list_cr_d != null && list_cr_d.size() > 0) {
				// 找到交叉连接的处理方法，可以有多重
				returnMap = insertToVir(list_cr_d, insert, temp, crsVir,
						map_otn);

				is_find = true;
				returnMap.put("is_find", is_find);
			} else {
				returnMap = downToFindOtnCrsANullVir(type_z, insert, temp,
						crsVir, map_otn);
			}
		}
		return returnMap;
	}

	/**
	 * 进阶查询，下一跳交叉Z端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map upToFindOtnCrsZNull(String[] type_z, Map insert, Map temp,
			Map map_otn_cir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
			select.put("IS_VIRTUAL", CommonDefine.TRUE);
			// 依次向上查找
			if (j == 0) {
				select.put("A_DSR", "1");
			} else if (j == 1) {
				select.put("A_ODU0", "1");
			} else if (j == 2) {
				select.put("A_ODU1", "1");
			} else if (j == 3) {
				select.put("A_ODU2", "1");
			} else if (j == 4) {
				select.put("A_ODU3", "1");
			} else if (j == 5) {
				select.put("A_OCH", "1");
			} else if (j == 6) {
				select.put("A_OMS", "1");
			} else if (j == 7) {
				select.put("A_OTS", "1");
			} else if (j == 8) {
				select.put("A_OS", "1");
			}

			// 此处查询为加非空判断
			List<Map> list_up = circuitManagerMapper.getOtnCrsUp(select);
			if (list_up != null && list_up.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = crsToDB(list_up, insert, temp, map_otn_cir, map_otn);
				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
			List<Map> list_crs_down = new ArrayList<Map>();
			for (int i = 0; i < type_z.length; i++) {
				// 查找下一条的交叉
				select = new HashMap();
				select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
				select.put("IS_VIRTUAL", CommonDefine.TRUE);
				// 判断交叉连接z端的值,先获取z端类型
				for (int k = i; k < type_z.length; k++) {
					select.put(type_z[k], map_otn.get(type_z[k]));
				}

				list_crs_down = circuitManagerMapper.getOtnCrsByCtp(select);

				if (list_crs_down != null && list_crs_down.size() > 0) {
					break;
				}
			}
			if (list_crs_down != null && list_crs_down.size() > 0) {
				// 找到交叉连接的处理方法，可以有多重
				returnMap = crsToDB(list_crs_down, insert, temp, map_otn_cir,
						map_otn);
				is_find = true;
				returnMap.put("is_find", is_find);
			} else {
				returnMap = downToFindOtnCrsZNull(type_z, insert, temp,
						map_otn_cir, map_otn);
			}
		}
		return returnMap;
	}

	/**
	 * 进阶查询，下一跳交叉Z端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map upToFindOtnCrsZNullVir(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 定义一个变量，用来记录向上查找是否已经找到
		boolean is_find = false;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		int n = 0;
		// 判断上一交叉的z端最大级别
		if (type_z[0].contains("OS")) {
			// 已经到最顶端
			// istrue = false;
			n = 8;
		} else if (type_z[0].contains("OTS")) {
			n = 7;
		} else if (type_z[0].contains("OMS")) {
			n = 6;
		} else if (type_z[0].contains("OCH")) {
			n = 5;
		} else if (type_z[0].contains("ODU3")) {
			n = 4;
		} else if (type_z[0].contains("ODU2")) {
			n = 3;
		} else if (type_z[0].contains("ODU1")) {
			n = 2;
		} else if (type_z[0].contains("ODU0")) {
			n = 1;
		} else if (type_z[0].contains("DSR")) {
			n = 0;
		}

		// 根据当前所在的层级，逐级向上层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));

			// // 依次向上查找
			// if (j == 0) {
			// select.put("A_DSR", "1");
			// } else if (j == 1) {
			// select.put("A_ODU0", "1");
			// } else if (j == 2) {
			// select.put("A_ODU1", "1");
			// } else if (j == 3) {
			// select.put("A_ODU2", "1");
			// } else if (j == 4) {
			// select.put("A_ODU3", "1");
			// } else if (j == 5) {
			// select.put("A_OCH", "1");
			// } else if (j == 6) {
			// select.put("A_OMS", "1");
			// } else if (j == 7) {
			// select.put("A_OTS", "1");
			// } else if (j == 8) {
			// select.put("A_OS", "1");
			// }

			// 依次向上查找
			if (j == 0) {
				if (temp.get("DSR") != null
						&& !"0".equals(temp.get("DSR").toString())
						&& !temp.get("DSR").toString().isEmpty()) {
					select.put("A_DSR", temp.get("DSR"));
				}
			} else if (j == 1) {
				if (temp.get("ODU0") != null
						&& !"0".equals(temp.get("ODU0").toString())
						&& !temp.get("ODU0").toString().isEmpty()) {
					select.put("A_ODU0", temp.get("ODU0"));
				}

			} else if (j == 2) {
				if (temp.get("ODU1") != null
						&& !"0".equals(temp.get("ODU1").toString())
						&& !temp.get("ODU1").toString().isEmpty()) {
					select.put("A_ODU1", temp.get("ODU1"));
				}

			} else if (j == 3) {
				if (temp.get("ODU2") != null
						&& !"0".equals(temp.get("ODU2").toString())
						&& !temp.get("ODU2").toString().isEmpty()) {
					select.put("A_ODU2", temp.get("ODU2"));
				}

			} else if (j == 4) {
				if (temp.get("ODU3") != null
						&& !"0".equals(temp.get("ODU3").toString())
						&& !temp.get("ODU3").toString().isEmpty()) {
					select.put("A_ODU3", temp.get("ODU3"));
				}

			} else if (j == 5) {
				if (temp.get("OCH") != null
						&& !"0".equals(temp.get("OCH").toString())
						&& !temp.get("OCH").toString().isEmpty()) {
					select.put("A_OCH", temp.get("OCH"));
				}

			} else if (j == 6) {
				if (temp.get("OMS") != null
						&& !"0".equals(temp.get("OMS").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					select.put("A_OMS", temp.get("OMS"));
				}

			} else if (j == 7) {
				if (temp.get("OTS") != null
						&& !"0".equals(temp.get("OTS").toString())
						&& !temp.get("OTS").toString().isEmpty()) {
					select.put("A_OTS", temp.get("OTS"));
				}

			} else if (j == 8) {
				if (temp.get("OS") != null
						&& !"0".equals(temp.get("OS").toString())
						&& !temp.get("OS").toString().isEmpty()) {
					select.put("A_OS", temp.get("OS"));
				}

			}

			// 此处查询为加非空判断
			List<Map> list_up = circuitManagerMapper.getOtnCrsUp(select);
			// 判断是否有值是向下级别的
			List<Map> listUp = new ArrayList<Map>();
			for(Map crsUp :list_up){
				// 向上越级可以无视此规则
				// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
				int k = -1;
				// 判断上一交叉的z端最大级别
				if (crsUp.get("A_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					k = 8;
				} else if (crsUp.get("A_TYPE").toString().contains("OTS")) {
					k = 7;
				} else if (crsUp.get("A_TYPE").toString().contains("OMS")) {
					k = 6;
				} else if (crsUp.get("A_TYPE").toString().contains("OCH")) {
					k = 5;
				} else if (crsUp.get("A_TYPE").toString().contains("ODU3")) {
					k = 4;
				} else if (crsUp.get("A_TYPE").toString().contains("ODU2")) {
					k = 3;
				} else if (crsUp.get("A_TYPE").toString().contains("ODU1")) {
					k = 2;
				} else if (crsUp.get("A_TYPE").toString().contains("ODU0")) {
					k = 1;
				} else if (crsUp.get("A_TYPE").toString().contains("DSR")) {
					k = 0;
				}
				
				int m = 0;
				// 判断上一交叉的z端最大级别
				if (map_otn.get("A_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					m = 8;
				} else if (map_otn.get("A_TYPE").toString().contains("OTS")) {
					m = 7;
				} else if (map_otn.get("A_TYPE").toString().contains("OMS")) {
					m = 6;
				} else if (map_otn.get("A_TYPE").toString().contains("OCH")) {
					m = 5;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU3")) {
					m = 4;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU2")) {
					m = 3;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU1")) {
					m = 2;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU0")) {
					m = 1;
				} else if (map_otn.get("A_TYPE").toString().contains("DSR")) {
					m = 0;
				}
				if(k>=m){
					listUp.add(crsUp);
					continue;
				}
//				boolean isRight = true; // 交叉是否符合标准
//				if(crsUp.get("Z_TYPE")!=null&& !crsUp.get("Z_TYPE").toString().isEmpty()){
//					String [] typeZ = crsUp.get("Z_TYPE").toString().split(",");
//					for(String name :typeZ){
//						if(temp.get(name.substring(2))==null ||!temp.get(name.substring(2)).toString()
//								.equals(crsUp.get(name).toString())){
//							// 结束本次循环
//							isRight = false;
//							break;
//						}
//					}
//				}
//				if(!isRight){
//					continue;
//				}else{
//					listNull.add(crsNull);
//				}
			}
			

			if (listUp != null && listUp.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToVir(listUp, insert,
						temp, crsVir, map_otn);

				// 标记为已找到
				is_find = true;
				returnMap.put("is_find", is_find);
				// 结束整个循环
				break;
			}
		}
		// 向下查找
		if (!is_find) {
			// 从大往小，从前往后，依次减少字段
			List<Map> list_crs_down = new ArrayList<Map>();
			for (int i = 0; i < type_z.length; i++) {
				// 查找下一条的交叉
				select = new HashMap();
				select.put("A_END_PTP", map_otn.get("Z_END_PTP"));

				// 判断交叉连接z端的值,先获取z端类型
				for (int k = i; k < type_z.length; k++) {
					select.put(type_z[k], map_otn.get(type_z[k]));
				}

				list_crs_down = circuitManagerMapper.getOtnCrsByCtp(select);

				if (list_crs_down != null && list_crs_down.size() > 0) {
					break;
				}
			}
			if (list_crs_down != null && list_crs_down.size() > 0) {
				// 找到交叉连接的处理方法，可以有多重
				returnMap = insertToVir(list_crs_down, insert, temp, crsVir,
						map_otn);
				is_find = true;
				returnMap.put("is_find", is_find);
			} else {
				returnMap = downToFindOtnCrsZNullVir(type_z, insert, temp,
						crsVir, map_otn);
			}
		}
		return returnMap;
	}

	/**
	 * 降阶查询
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map downToFindOtnCrs(String[] type_z, Map insert, Map temp,
			Map map_otn_cir, Map map_otn, List<Map> list_link) {
		Map returnMap = new HashMap();
		Map select = null;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		boolean is_find = false;

		// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
		boolean is_oms_two = true;
		int n = 0;
		// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
		if (type_z[type_z.length - 1].contains("OS")) {
			n = 0;
		} else if (type_z[type_z.length - 1].contains("OTS")) {
			n = 1;
		} else if (type_z[type_z.length - 1].contains("OMS")) {
			n = 2;
		} else if (type_z[type_z.length - 1].contains("OCH")) {
			n = 3;
		} else if (type_z[type_z.length - 1].contains("ODU3")) {
			n = 4;
		} else if (type_z[type_z.length - 1].contains("ODU2")) {
			n = 5;
		} else if (type_z[type_z.length - 1].contains("ODU1")) {
			n = 6;
		} else if (type_z[type_z.length - 1].contains("ODU0")) {
			n = 7;
		} else if (type_z[type_z.length - 1].contains("DSR")) {
			n = 8;
		}

		// 根据当前所在的层级，逐级向下层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", list_link.get(0).get("Z_END_PTP"));
			select.put("IS_VIRTUAL", CommonDefine.TRUE);
			// 想下遍历时，需要匹配全局变量，如果为空，则跳过
			if (j == 0) {
				if (temp.get("OS") != null) {
					select.put("A_OS", temp.get("OS"));
				} else {
					continue;
				}
			} else if (j == 1) {
				if (temp.get("OTS") != null) {
					select.put("A_OTS", temp.get("OTS"));
				} else {
					continue;
				}
			} else if (j == 2) {
				// ots 有1和2 两个值，默认先去找2 再去找1
				if (temp.get("OMS") != null) {
					if (temp.get("OMS2") != null
							&& "2".equals(temp.get("OMS2").toString())
							&& is_oms_two) {
						select.put("A_OMS", 2);
					} else {
						select.put("A_OMS", 1);
					}
				} else {
					continue;
				}

				// select.put("A_OMS", temp.get("OMS"));
			} else if (j == 3) {
				if (temp.get("OCH") != null) {
					select.put("A_OCH", temp.get("OCH"));
				} else {
					continue;
				}
			} else if (j == 4) {
				if (temp.get("ODU3") != null) {
					select.put("A_ODU3", temp.get("ODU3"));
				} else {
					continue;
				}
			} else if (j == 5) {
				if (temp.get("ODU2") != null) {
					select.put("A_ODU2", temp.get("ODU2"));
				} else {
					continue;
				}
			} else if (j == 6) {
				if (temp.get("ODU1") != null) {
					select.put("A_ODU1", temp.get("ODU1"));
				} else {
					continue;
				}
			} else if (j == 7) {
				if (temp.get("ODU0") != null) {
					select.put("A_ODU0", temp.get("ODU0"));
				} else {
					continue;
				}
			} else if (j == 8) {
				if (temp.get("DSR") != null) {
					select.put("A_DSR", temp.get("DSR"));
				} else {
					continue;
				}
			}

			// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
			List<Map> list_down = circuitManagerMapper.getOtnCrsByCtp(select);
			if (list_down != null && list_down.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = crsToDB(list_down, insert, temp, map_otn_cir,
						map_otn);
				is_find = true;
				// 结束整个循环
				break;
			}
			// 专门处理 ots的特殊情况
			if (is_oms_two) {
				j--;
				is_oms_two = false;
			}
		}
		returnMap.put("is_find", is_find);
		return returnMap;
	}

	/**
	 * 降阶查询
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map downToFindOtnCrsVir(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn, List<Map> list_link) {
		Map returnMap = new HashMap();
		Map select = null;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		boolean is_find = false;

		// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
		boolean is_oms_two = true;
		int n = 0;
		// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
		if (type_z[type_z.length - 1].contains("OS")) {
			n = 0;
		} else if (type_z[type_z.length - 1].contains("OTS")) {
			n = 1;
		} else if (type_z[type_z.length - 1].contains("OMS")) {
			n = 2;
		} else if (type_z[type_z.length - 1].contains("OCH")) {
			n = 3;
		} else if (type_z[type_z.length - 1].contains("ODU3")) {
			n = 4;
		} else if (type_z[type_z.length - 1].contains("ODU2")) {
			n = 5;
		} else if (type_z[type_z.length - 1].contains("ODU1")) {
			n = 6;
		} else if (type_z[type_z.length - 1].contains("ODU0")) {
			n = 7;
		} else if (type_z[type_z.length - 1].contains("DSR")) {
			n = 8;
		}

		// 根据当前所在的层级，逐级向下层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", list_link.get(0).get("Z_END_PTP"));

			// 想下遍历时，需要匹配全局变量，如果为空，则跳过
			if (j == 0) {
				if (temp.get("OS") != null
						&& !"0".equals(temp.get("OS").toString())
						&& !temp.get("OS").toString().isEmpty()) {
					select.put("A_OS", temp.get("OS"));
				} else {
					continue;
				}
			} else if (j == 1) {
				if (temp.get("OTS") != null
						&& !"0".equals(temp.get("OTS").toString())
						&& !temp.get("OTS").toString().isEmpty()) {
					select.put("A_OTS", temp.get("OTS"));
				} else {
					continue;
				}
			} else if (j == 2) {
				// ots 有1和2 两个值，默认先去找2 再去找1
				if (temp.get("OMS") != null
						&& !"0".equals(temp.get("OMS").toString())
						&& !temp.get("OMS").toString().isEmpty()) {
					if (temp.get("OMS2") != null
							&& "2".equals(temp.get("OMS2").toString())
							&& is_oms_two) {
						select.put("A_OMS", 2);
					} else {
						select.put("A_OMS", 1);
					}
				} else {
					continue;
				}

				// select.put("A_OMS", temp.get("OMS"));
			} else if (j == 3) {
				if (temp.get("OCH") != null
						&& !"0".equals(temp.get("OCH").toString())
						&& !temp.get("OCH").toString().isEmpty()) {
					select.put("A_OCH", temp.get("OCH"));
				} else {
					continue;
				}
			} else if (j == 4) {
				if (temp.get("ODU3") != null
						&& !"0".equals(temp.get("ODU3").toString())
						&& !temp.get("ODU3").toString().isEmpty()) {
					select.put("A_ODU3", temp.get("ODU3"));
				} else {
					continue;
				}
			} else if (j == 5) {
				if (temp.get("ODU2") != null
						&& !"0".equals(temp.get("ODU2").toString())
						&& !temp.get("ODU2").toString().isEmpty()) {
					select.put("A_ODU2", temp.get("ODU2"));
				} else {
					continue;
				}
			} else if (j == 6) {
				if (temp.get("ODU1") != null
						&& !"0".equals(temp.get("ODU1").toString())
						&& !temp.get("ODU1").toString().isEmpty()) {
					select.put("A_ODU1", temp.get("ODU1"));
				} else {
					continue;
				}
			} else if (j == 7) {
				if (temp.get("ODU0") != null
						&& !"0".equals(temp.get("ODU0").toString())
						&& !temp.get("ODU0").toString().isEmpty()) {
					select.put("A_ODU0", temp.get("ODU0"));
				} else {
					continue;
				}
			} else if (j == 8) {
				if (temp.get("DSR") != null
						&& !"0".equals(temp.get("DSR").toString())
						&& !temp.get("DSR").toString().isEmpty()) {
					select.put("A_DSR", temp.get("DSR"));
				} else {
					continue;
				}
			}

			// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
			List<Map> list_down = circuitManagerMapper.getOtnCrsByCtp(select);
			if (list_down != null && list_down.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToVir(list_down, insert, temp, crsVir,
						map_otn);
				is_find = true;
				// 结束整个循环
				break;
			}
			// 专门处理 ots的特殊情况
			if (is_oms_two) {
				j--;
				is_oms_two = false;
			}
		}
		returnMap.put("is_find", is_find);
		return returnMap;
	}

	/**
	 * 降阶查询,查询下一交叉A端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map downToFindOtnCrsANull(String[] type_z, Map insert, Map temp,
			Map map_otn_cir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		boolean is_find = false;

		// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
		boolean is_oms_two = true;
		int n = 0;
		// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
		if (type_z[type_z.length - 1].contains("OS")) {
			n = 0;
		} else if (type_z[type_z.length - 1].contains("OTS")) {
			n = 1;
		} else if (type_z[type_z.length - 1].contains("OMS")) {
			n = 2;
		} else if (type_z[type_z.length - 1].contains("OCH")) {
			n = 3;
		} else if (type_z[type_z.length - 1].contains("ODU3")) {
			n = 4;
		} else if (type_z[type_z.length - 1].contains("ODU2")) {
			n = 5;
		} else if (type_z[type_z.length - 1].contains("ODU1")) {
			n = 6;
		} else if (type_z[type_z.length - 1].contains("ODU0")) {
			n = 7;
		} else if (type_z[type_z.length - 1].contains("DSR")) {
			n = 8;
		}

		// 根据当前所在的层级，逐级向下层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
			select.put("IS_VIRTUAL", CommonDefine.TRUE);
			// 想下遍历时，需要匹配全局变量，如果为空，则跳过
			if (j == 0) {
				if (temp.get("OS") != null) {
					select.put("Z_OS", temp.get("OS"));
				} else {
					continue;
				}
			} else if (j == 1) {
				if (temp.get("OTS") != null) {
					select.put("Z_OTS", temp.get("OTS"));
				} else {
					continue;
				}
			} else if (j == 2) {
				// ots 有1和2 两个值，默认先去找2 再去找1
				if (temp.get("OMS") != null) {
					if (temp.get("OMS2") != null
							&& "2".equals(temp.get("OMS2").toString())
							&& is_oms_two) {
						select.put("Z_OMS", 2);
					} else {
						select.put("Z_OMS", 1);
					}
				} else {
					continue;
				}

				// select.put("A_OMS", temp.get("OMS"));
			} else if (j == 3) {
				if (temp.get("OCH") != null) {
					select.put("Z_OCH", temp.get("OCH"));
				} else {
					continue;
				}
			} else if (j == 4) {
				if (temp.get("ODU3") != null) {
					select.put("Z_ODU3", temp.get("ODU3"));
				} else {
					continue;
				}
			} else if (j == 5) {
				if (temp.get("ODU2") != null) {
					select.put("Z_ODU2", temp.get("ODU2"));
				} else {
					continue;
				}
			} else if (j == 6) {
				if (temp.get("ODU1") != null) {
					select.put("Z_ODU1", temp.get("ODU1"));
				} else {
					continue;
				}
			} else if (j == 7) {
				if (temp.get("ODU0") != null) {
					select.put("Z_ODU0", temp.get("ODU0"));
				} else {
					continue;
				}
			} else if (j == 8) {
				if (temp.get("DSR") != null) {
					select.put("Z_DSR", temp.get("DSR"));
				} else {
					continue;
				}
			}

			// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
			List<Map> list_down = circuitManagerMapper.getOtnCrsByCtp(select);
			if (list_down != null && list_down.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = crsToDB(list_down, insert, temp, map_otn_cir,
						map_otn);
				is_find = true;
				// 结束整个循环
				break;
			}
			// 专门处理 ots的特殊情况
			if (is_oms_two) {
				j--;
				is_oms_two = false;
			}
		}
		returnMap.put("is_find", is_find);
		return returnMap;
	}

	/**
	 * 降阶查询,查询下一交叉A端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map downToFindOtnCrsANullVir(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		boolean is_find = false;

		// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
		boolean is_oms_two = true;
		int n = 0;
		// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
		if (type_z[type_z.length - 1].contains("OS")) {
			n = 0;
		} else if (type_z[type_z.length - 1].contains("OTS")) {
			n = 1;
		} else if (type_z[type_z.length - 1].contains("OMS")) {
			n = 2;
		} else if (type_z[type_z.length - 1].contains("OCH")) {
			n = 3;
		} else if (type_z[type_z.length - 1].contains("ODU3")) {
			n = 4;
		} else if (type_z[type_z.length - 1].contains("ODU2")) {
			n = 5;
		} else if (type_z[type_z.length - 1].contains("ODU1")) {
			n = 6;
		} else if (type_z[type_z.length - 1].contains("ODU0")) {
			n = 7;
		} else if (type_z[type_z.length - 1].contains("DSR")) {
			n = 8;
		}

		// 根据当前所在的层级，逐级向下层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));

			// 想下遍历时，需要匹配全局变量，如果为空，则跳过
			if (j == 0) {
				if (temp.get("OS") != null && !temp.get("OS").toString().isEmpty()) {
					select.put("Z_OS", temp.get("OS"));
				} else {
					continue;
				}
			} else if (j == 1) {
				if (temp.get("OTS") != null && !temp.get("OTS").toString().isEmpty() ) {
					select.put("Z_OTS", temp.get("OTS"));
				} else {
					continue;
				}
			} else if (j == 2) {
				// ots 有1和2 两个值，默认先去找2 再去找1
				if (temp.get("OMS") != null && !temp.get("OMS").toString().isEmpty()) {
					if (temp.get("OMS2") != null
							&& "2".equals(temp.get("OMS2").toString())
							&& is_oms_two) {
						select.put("Z_OMS", 2);
					} else {
						select.put("Z_OMS", 1);
					}
				} else {
					continue;
				}

				// select.put("A_OMS", temp.get("OMS"));
			} else if (j == 3) {
				if (temp.get("OCH") != null && !temp.get("OCH").toString().isEmpty()) {
					select.put("Z_OCH", temp.get("OCH"));
				} else {
					continue;
				}
			} else if (j == 4) {
				if (temp.get("ODU3") != null && !temp.get("ODU3").toString().isEmpty()) {
					select.put("Z_ODU3", temp.get("ODU3"));
				} else {
					continue;
				}
			} else if (j == 5) {
				if (temp.get("ODU2") != null && !temp.get("ODU2").toString().isEmpty()) {
					select.put("Z_ODU2", temp.get("ODU2"));
				} else {
					continue;
				}
			} else if (j == 6) {
				if (temp.get("ODU1") != null && !temp.get("ODU1").toString().isEmpty()) {
					select.put("Z_ODU1", temp.get("ODU1"));
				} else {
					continue;
				}
			} else if (j == 7) {
				if (temp.get("ODU0") != null && !temp.get("ODU0").toString().isEmpty()) {
					select.put("Z_ODU0", temp.get("ODU0"));
				} else {
					continue;
				}
			} else if (j == 8) {
				if (temp.get("DSR") != null && !temp.get("DSR").toString().isEmpty()) {
					select.put("Z_DSR", temp.get("DSR"));
				} else {
					continue;
				}
			}

			// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
			List<Map> list_down = circuitManagerMapper.getOtnCrsByCtp(select);
			// 判断是否有值是向上级别的
			List<Map> listDown = new ArrayList<Map>();
			for(Map crsDown :list_down){
				// 向上越级可以无视此规则
				// 判断a端是否为空
				if(crsDown.get("A_TYPE")!=null || !crsDown.get("A_TYPE").toString().isEmpty() ){
					continue;
				}
				// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
				int k = 0;
				// 判断上一交叉的z端最大级别
				if (crsDown.get("Z_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					k = 8;
				} else if (crsDown.get("Z_TYPE").toString().contains("OTS")) {
					k = 7;
				} else if (crsDown.get("Z_TYPE").toString().contains("OMS")) {
					k = 6;
				} else if (crsDown.get("Z_TYPE").toString().contains("OCH")) {
					k = 5;
				} else if (crsDown.get("Z_TYPE").toString().contains("ODU3")) {
					k = 4;
				} else if (crsDown.get("Z_TYPE").toString().contains("ODU2")) {
					k = 3;
				} else if (crsDown.get("Z_TYPE").toString().contains("ODU1")) {
					k = 2;
				} else if (crsDown.get("Z_TYPE").toString().contains("ODU0")) {
					k = 1;
				} else if (crsDown.get("Z_TYPE").toString().contains("DSR")) {
					k = 0;
				}
				
				int m = -1;
				// 判断上一交叉的z端最大级别
				if (map_otn.get("Z_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					m = 8;
				} else if (map_otn.get("Z_TYPE").toString().contains("OTS")) {
					m = 7;
				} else if (map_otn.get("Z_TYPE").toString().contains("OMS")) {
					m = 6;
				} else if (map_otn.get("Z_TYPE").toString().contains("OCH")) {
					m = 5;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU3")) {
					m = 4;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU2")) {
					m = 3;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU1")) {
					m = 2;
				} else if (map_otn.get("Z_TYPE").toString().contains("ODU0")) {
					m = 1;
				} else if (map_otn.get("Z_TYPE").toString().contains("DSR")) {
					m = 0;
				}
				if(k>m){
					//listDown.add(crsDown);
					continue;
				}
				boolean isRight = true; // 交叉是否符合标准
				if(crsDown.get("Z_TYPE")!=null&& !crsDown.get("Z_TYPE").toString().isEmpty()){
					String [] typeA = crsDown.get("Z_TYPE").toString().split(",");
					for(String name :typeA){
						if(temp.get(name.substring(2))==null ||!temp.get(name.substring(2)).toString()
								.equals(crsDown.get(name).toString())){
							// 结束本次循环
							isRight = false;
							break;
						}
					}
				}else{
					isRight = false;
				}
				if(!isRight){
					continue;
				}else{
					listDown.add(crsDown);
				}
			}
			if (listDown != null && listDown.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToVir((List<Map>) listDown.get(0), insert,
						temp, crsVir, map_otn);
				is_find = true;
				// 结束整个循环
				break;
			}
			// 专门处理 ots的特殊情况
			if (is_oms_two) {
				j--;
				is_oms_two = false;
			}
		}
		returnMap.put("is_find", is_find);
		return returnMap;
	}

	/**
	 * 降阶查询,查询下一交叉Z端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map downToFindOtnCrsZNull(String[] type_z, Map insert, Map temp,
			Map map_otn_cir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		boolean is_find = false;

		// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
		boolean is_oms_two = true;
		int n = 0;
		// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
		if (type_z[type_z.length - 1].contains("OS")) {
			n = 0;
		} else if (type_z[type_z.length - 1].contains("OTS")) {
			n = 1;
		} else if (type_z[type_z.length - 1].contains("OMS")) {
			n = 2;
		} else if (type_z[type_z.length - 1].contains("OCH")) {
			n = 3;
		} else if (type_z[type_z.length - 1].contains("ODU3")) {
			n = 4;
		} else if (type_z[type_z.length - 1].contains("ODU2")) {
			n = 5;
		} else if (type_z[type_z.length - 1].contains("ODU1")) {
			n = 6;
		} else if (type_z[type_z.length - 1].contains("ODU0")) {
			n = 7;
		} else if (type_z[type_z.length - 1].contains("DSR")) {
			n = 8;
		}

		// 根据当前所在的层级，逐级向下层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));
			select.put("IS_VIRTUAL", CommonDefine.TRUE);
			// 想下遍历时，需要匹配全局变量，如果为空，则跳过
			if (j == 0) {
				if (temp.get("OS") != null) {
					select.put("A_OS", temp.get("OS"));
				} else {
					continue;
				}
			} else if (j == 1) {
				if (temp.get("OTS") != null) {
					select.put("A_OTS", temp.get("OTS"));
				} else {
					continue;
				}
			} else if (j == 2) {
				// ots 有1和2 两个值，默认先去找2 再去找1
				if (temp.get("OMS") != null) {
					if (temp.get("OMS2") != null
							&& "2".equals(temp.get("OMS2").toString())
							&& is_oms_two) {
						select.put("A_OMS", 2);
					} else {
						select.put("A_OMS", 1);
					}
				} else {
					continue;
				}

				// select.put("A_OMS", temp.get("OMS"));
			} else if (j == 3) {
				if (temp.get("OCH") != null) {
					select.put("A_OCH", temp.get("OCH"));
				} else {
					continue;
				}
			} else if (j == 4) {
				if (temp.get("ODU3") != null) {
					select.put("A_ODU3", temp.get("ODU3"));
				} else {
					continue;
				}
			} else if (j == 5) {
				if (temp.get("ODU2") != null) {
					select.put("A_ODU2", temp.get("ODU2"));
				} else {
					continue;
				}
			} else if (j == 6) {
				if (temp.get("ODU1") != null) {
					select.put("A_ODU1", temp.get("ODU1"));
				} else {
					continue;
				}
			} else if (j == 7) {
				if (temp.get("ODU0") != null) {
					select.put("A_ODU0", temp.get("ODU0"));
				} else {
					continue;
				}
			} else if (j == 8) {
				if (temp.get("DSR") != null) {
					select.put("A_DSR", temp.get("DSR"));
				} else {
					continue;
				}
			}

			// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
			List<Map> list_down = circuitManagerMapper.getOtnCrsByCtp(select);
			if (list_down != null && list_down.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = crsToDB(list_down, insert, temp, map_otn_cir,
						map_otn);
				is_find = true;
				// 结束整个循环
				break;
			}
			// 专门处理 ots的特殊情况
			if (is_oms_two) {
				j--;
				is_oms_two = false;
			}
		}
		returnMap.put("is_find", is_find);
		return returnMap;
	}

	/**
	 * 降阶查询,查询下一交叉Z端为空
	 * 
	 * @param type_z
	 *            排序过ctp占用的字段
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 * @param list_link
	 *            查找到的link
	 */
	public Map downToFindOtnCrsZNullVir(String[] type_z, Map insert, Map temp,
			Map crsVir, Map map_otn) {
		Map returnMap = new HashMap();
		Map select = null;
		// 如果不存在，则，向高位进阶，从dsr开始一次向前进位
		boolean is_find = false;

		// oms 默认先匹配2 再匹配1 所有要查找2次，变量用来标记
		boolean is_oms_two = true;
		int n = 0;
		// 判断上一交叉的z的最小级别，type_z不为空，最前面已判断
		if (type_z[type_z.length - 1].contains("OS")) {
			n = 0;
		} else if (type_z[type_z.length - 1].contains("OTS")) {
			n = 1;
		} else if (type_z[type_z.length - 1].contains("OMS")) {
			n = 2;
		} else if (type_z[type_z.length - 1].contains("OCH")) {
			n = 3;
		} else if (type_z[type_z.length - 1].contains("ODU3")) {
			n = 4;
		} else if (type_z[type_z.length - 1].contains("ODU2")) {
			n = 5;
		} else if (type_z[type_z.length - 1].contains("ODU1")) {
			n = 6;
		} else if (type_z[type_z.length - 1].contains("ODU0")) {
			n = 7;
		} else if (type_z[type_z.length - 1].contains("DSR")) {
			n = 8;
		}

		// 根据当前所在的层级，逐级向下层遍历
		for (int j = n + 1; j < 9; j++) {
			select = new HashMap();
			select.put("A_END_PTP", map_otn.get("Z_END_PTP"));

			// 想下遍历时，需要匹配全局变量，如果为空，则跳过
			if (j == 0) {
				if (temp.get("OS") != null && !temp.get("OS").toString().isEmpty()) {
					select.put("A_OS", temp.get("OS"));
				} else {
					continue;
				}
			} else if (j == 1) {
				if (temp.get("OTS") != null && !temp.get("OTS").toString().isEmpty()) {
					select.put("A_OTS", temp.get("OTS"));
				} else {
					continue;
				}
			} else if (j == 2) {
				// ots 有1和2 两个值，默认先去找2 再去找1
				if (temp.get("OMS") != null && !temp.get("OMS").toString().isEmpty()) {
					if (temp.get("OMS2") != null
							&& "2".equals(temp.get("OMS2").toString())
							&& is_oms_two) {
						select.put("A_OMS", 2);
					} else {
						select.put("A_OMS", 1);
					}
				} else {
					continue;
				}

				// select.put("A_OMS", temp.get("OMS"));
			} else if (j == 3) {
				if (temp.get("OCH") != null && !temp.get("OCH").toString().isEmpty()) {
					select.put("A_OCH", temp.get("OCH"));
				} else {
					continue;
				}
			} else if (j == 4) {
				if (temp.get("ODU3") != null && !temp.get("ODU3").toString().isEmpty()) {
					select.put("A_ODU3", temp.get("ODU3"));
				} else {
					continue;
				}
			} else if (j == 5) {
				if (temp.get("ODU2") != null && !temp.get("ODU2").toString().isEmpty()) {
					select.put("A_ODU2", temp.get("ODU2"));
				} else {
					continue;
				}
			} else if (j == 6) {
				if (temp.get("ODU1") != null && !temp.get("ODU1").toString().isEmpty()) {
					select.put("A_ODU1", temp.get("ODU1"));
				} else {
					continue;
				}
			} else if (j == 7) {
				if (temp.get("ODU0") != null && !temp.get("ODU0").toString().isEmpty() ) {
					select.put("A_ODU0", temp.get("ODU0"));
				} else {
					continue;
				}
			} else if (j == 8) {
				if (temp.get("DSR") != null && !temp.get("DSR").toString().isEmpty() ) {
					select.put("A_DSR", temp.get("DSR"));
				} else {
					continue;
				}
			}

			// List<Map> list_down = circuitManagerMapper.getOtnCrsDown(select);
			List<Map> list_down = circuitManagerMapper.getOtnCrsByCtp(select);
			// 判断是否有值是向上级别的
			List<Map> listDown = new ArrayList<Map>();
			for(Map crsDown :list_down){
				// 向上越级可以无视此规则
				// 判断查询到的结果是否是低阶，如果是低阶则过滤掉
				int k = 0;
				// 判断上一交叉的z端最大级别
				if (crsDown.get("A_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					k = 8;
				} else if (crsDown.get("A_TYPE").toString().contains("OTS")) {
					k = 7;
				} else if (crsDown.get("A_TYPE").toString().contains("OMS")) {
					k = 6;
				} else if (crsDown.get("A_TYPE").toString().contains("OCH")) {
					k = 5;
				} else if (crsDown.get("A_TYPE").toString().contains("ODU3")) {
					k = 4;
				} else if (crsDown.get("A_TYPE").toString().contains("ODU2")) {
					k = 3;
				} else if (crsDown.get("A_TYPE").toString().contains("ODU1")) {
					k = 2;
				} else if (crsDown.get("A_TYPE").toString().contains("ODU0")) {
					k = 1;
				} else if (crsDown.get("A_TYPE").toString().contains("DSR")) {
					k = 0;
				}
				
				int m = -1;
				// 判断上一交叉的z端最大级别
				if (map_otn.get("A_TYPE").toString().contains("OS")) {
					// 已经到最顶端
					// istrue = false;
					m = 8;
				} else if (map_otn.get("A_TYPE").toString().contains("OTS")) {
					m = 7;
				} else if (map_otn.get("A_TYPE").toString().contains("OMS")) {
					m = 6;
				} else if (map_otn.get("A_TYPE").toString().contains("OCH")) {
					m = 5;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU3")) {
					m = 4;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU2")) {
					m = 3;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU1")) {
					m = 2;
				} else if (map_otn.get("A_TYPE").toString().contains("ODU0")) {
					m = 1;
				} else if (map_otn.get("A_TYPE").toString().contains("DSR")) {
					m = 0;
				}
				if(k>m){
					//listDown.add(crsDown);
					continue;
				}
				boolean isRight = true; // 交叉是否符合标准
				if(crsDown.get("A_TYPE")!=null&& !crsDown.get("A_TYPE").toString().isEmpty()){
					String [] typeA = crsDown.get("A_TYPE").toString().split(",");
					for(String name :typeA){
						if(temp.get(name.substring(2))==null ||!temp.get(name.substring(2)).toString()
								.equals(crsDown.get(name).toString())){
							// 结束本次循环
							isRight = false;
							break;
						}
					}
				}else{
					isRight = false;
				}
				if(!isRight){
					continue;
				}else{
					listDown.add(crsDown);
				}
			}
			if (listDown != null && listDown.size() > 0) {
				// 向上如果找到多条交叉时，只去第一条处理
				returnMap = insertToVir(listDown, insert, temp, crsVir,
						map_otn);
				is_find = true;
				// 结束整个循环
				break;
			}
			// 专门处理 ots的特殊情况
			if (is_oms_two) {
				j--;
				is_oms_two = false;
			}
		}
		returnMap.put("is_find", is_find);
		return returnMap;
	}

	/**
	 * 查询到交叉连接以后的入库处理逻辑
	 * 
	 * @param list_crs
	 *            查询到的交叉
	 * @param insert
	 *            插入交叉的条件变量（Map）
	 * @param temp
	 *            全局变量
	 * @param map_otn_cir
	 *            电路
	 * @param map_otn
	 *            上一条交叉连接
	 */
	public Map crsToDB(List<Map> list_crs, Map insert, Map temp,
			Map map_otn_cir, Map map_otn) {
		Map returnMap = new HashMap();
		for (int j = list_crs.size() - 1; j >= 0; j--) {
			// 如果找到多条连接。则先存入路由表，将最后一条取出来处理
			insert = new HashMap();
			insert.put("CIR_OTN_CIRCUIT_ID",
					map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
			insert.put("CHAIN_ID", list_crs.get(j).get("BASE_OTN_CRS_ID"));
			insert.put("CHAIN_TYPE", CommonDefine.CHAIN_TYPE_OTN_CRS);
			insert.put("AHEAD_CRS_ID", 1);
			if (j > 0) {
				insert.put("IS_COMPLETE", CommonDefine.FALSE);
			} else {
				// 如果是最后一条，则 将交叉连接赋值
				insert.put("IS_COMPLETE", CommonDefine.TRUE);
				map_otn = list_crs.get(0);

				temp = setTypeValue(temp, map_otn);
				// // 给全局变量赋值
				// if(map_otn.get("A_TYPE")!=null&&!map_otn.get("A_TYPE").toString().isEmpty()){
				// String[] typeName_a =
				// map_otn.get("A_TYPE").toString().split(",");
				// for (String name : typeName_a) {
				// temp.put(name.substring(2), map_otn.get(name.substring(2)));
				// // 如果oms 有2出现，则记录在OMS2
				// if (map_otn.get(name) != null&&
				// "2".equals(map_otn.get(name).toString())) {
				// temp.put("OMS2", 2);
				// }
				// }
				// }
				//
				// if(map_otn.get("Z_TYPE")!=null&&!map_otn.get("Z_TYPE").toString().isEmpty()){
				// // 给全局变量赋值
				// String[] typeName_z =
				// map_otn.get("Z_TYPE").toString().split(",");
				// for (String name : typeName_z) {
				// temp.put(name.substring(2), map_otn.get(name.substring(2)));
				// // 如果oms 有2出现，则记录在OMS2
				// if (map_otn.get(name) != null&&
				// "2".equals(map_otn.get(name).toString())) {
				// temp.put("OMS2", 2);
				// }
				// }
				// }

				returnMap.put("temp", temp);
				returnMap.put("map_otn", map_otn);

			}
			circuitManagerMapper.insertOtnRoute(insert);

		}
		return returnMap;
	}

	/**
	 * 将ctp字段按一定顺序排列OS，OTS，OMS，OCH，ODU3，ODU2，ODU1，ODU0，DSR
	 * 
	 * @param type
	 * @param before
	 *            拼接字段的前缀 如 A_
	 * @return
	 */
	public String sortType(String type, String before) {
		String sortType = "";
		if (type.contains("OS")) {
			sortType += before + "OS,";
		}
		if (type.contains("OTS")) {
			sortType += before + "OTS,";
		}
		if (type.contains("OMS")) {
			sortType += before + "OMS,";
		}
		if (type.contains("OCH")) {
			sortType += before + "OCH,";
		}
		if (type.contains("ODU3")) {
			sortType += before + "ODU3,";
		}
		if (type.contains("ODU2")) {
			sortType += before + "ODU2,";
		}
		if (type.contains("ODU1")) {
			sortType += before + "ODU1,";
		}
		if (type.contains("ODU0")) {
			sortType += before + "ODU0,";
		}
		if (type.contains("DSR")) {
			sortType += before + "DSR,";
		}
		// 去掉最后一个逗号
		if (type.length() > 0) {
			type = type.substring(0, type.length());
		}
		return type;

	}

	public Map insertOtnCrs(Map map_otn) {
		// 新建一条虚拟交叉
		Map insert = new HashMap();
		insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
		insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
		insert.put("BASE_EMS_CONNECTION_ID",
				map_otn.get("BASE_EMS_CONNECTION_ID"));
		insert.put("BASE_NE_ID", map_otn.get("BASE_NE_ID"));
		insert.put("A_OS", map_otn.get("A_OS"));
		insert.put("A_OTS", map_otn.get("A_OTS"));
		insert.put("A_OMS", map_otn.get("A_OMS"));
		insert.put("A_OCH", map_otn.get("A_OCH"));
		insert.put("A_ODU0", map_otn.get("A_ODU0"));
		insert.put("A_ODU1", map_otn.get("A_ODU1"));
		insert.put("A_ODU2", map_otn.get("A_ODU2"));
		insert.put("A_ODU3", map_otn.get("A_ODU3"));
		insert.put("A_OTU0", map_otn.get("A_OTU0"));
		insert.put("A_OTU1", map_otn.get("A_OTU1"));
		insert.put("A_OTU2", map_otn.get("A_OTU2"));
		insert.put("A_OTU3", map_otn.get("A_OTU3"));
		insert.put("A_DSR", map_otn.get("A_DSR"));
		insert.put("A_OAC_TYPE", map_otn.get("A_OAC_TYPE"));
		insert.put("A_OAC_VALUE", map_otn.get("A_OAC_VALUE"));
		insert.put("A_TYPE", map_otn.get("A_TYPE"));
		insert.put("CHANGE_STATE", CommonDefine.STATE_ADD_LATEST);
		insert.put("CIRCUIT_COUNT", 0);
		insert.put("IS_VIRTUAL", CommonDefine.TRUE);

		circuitManagerMapper.insertOtnCrs(insert);

		// Map select = new HashMap();
		// select.put("NAME", "t_base_otn_crs");
		// select.put("ID", "BASE_OTN_CRS_ID");
		// select.put("IN_NAME", "A_END_CTP");
		// select.put("IN_VALUE", map_otn.get("A_END_CTP"));
		//
		// insert = circuitManagerMapper.getLatestRecord(select);

		return insert;
	}

	/**
	 * 新增一条电路
	 * 
	 * @param map_otn
	 * @return
	 */
	public Map insertOtnCir(Map map_otn) {

		Map map_otn_crs = new HashMap();
		map_otn_crs.put("A_END_CTP", map_otn.get("A_END_CTP"));
		map_otn_crs.put("A_END_PTP", map_otn.get("A_END_PTP"));
		map_otn_crs.put("IS_MAIN_CIR", CommonDefine.TRUE);
		map_otn_crs.put("CIR_OTN_CIRCUIT_INFO_ID",
				map_otn.get("CIR_OTN_CIRCUIT_INFO_ID"));

		circuitManagerMapper.insertOtnCir(map_otn_crs);

		// Map select = new HashMap();
		// select.put("NAME", "t_cir_otn_circuit");
		// select.put("ID", "CIR_OTN_CIRCUIT_ID");
		// select.put("IN_NAME", "A_END_CTP");
		// select.put("IN_VALUE", map_otn.get("A_END_CTP"));
		//
		// map_otn_crs = circuitManagerMapper.getLatestRecord(select);

		return map_otn_crs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ICircuitManagerService#deleteTask(int)
	 */
	@Override
	public void deleteCircuitTask(int emsId) throws CommonException {
		Map delete = null;
		// 获取任务Id
		Integer sysTaskId = circuitManagerMapper.getTaskIdFromEmsId(emsId,
				CommonDefine.TREE.NODE.EMS, CommonDefine.QUARTZ.JOB_CIRCUIT);

		if (sysTaskId != null) {
			// 先删除quartz任务
			if (quartzManagerService.IsJobExist(
					CommonDefine.QUARTZ.JOB_CIRCUIT, sysTaskId)) {
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_CIRCUIT,
						sysTaskId, CommonDefine.QUARTZ.JOB_PAUSE);
				quartzManagerService.ctrlJob(CommonDefine.QUARTZ.JOB_CIRCUIT,
						sysTaskId, CommonDefine.QUARTZ.JOB_DELETE);
			}
			// quartzManagerService.
			delete = new HashMap();
			delete.put("NAME", "t_sys_task_info");
			delete.put("ID_NAME", "SYS_TASK_ID");
			delete.put("ID_VALUE", sysTaskId);
			circuitManagerMapper.deleteByParameter(delete);
			delete = new HashMap();
			delete.put("NAME", "t_sys_task_param");
			delete.put("ID_NAME", "SYS_TASK_ID");
			delete.put("ID_VALUE", sysTaskId);
			circuitManagerMapper.deleteByParameter(delete);
			delete = new HashMap();
			delete.put("NAME", "t_sys_task");
			delete.put("ID_NAME", "SYS_TASK_ID");
			delete.put("ID_VALUE", sysTaskId);
			circuitManagerMapper.deleteByParameter(delete);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ICircuitManagerService#updateTask(int)
	 */
	@Override
	public void updateTask(Map map) throws CommonException {
		// TODO Auto-generated method stub
		Map select = hashMapSon("t_sys_task", "SYS_TASK_ID",
				map.get("SYS_TASK_ID"), null, null, null);
		circuitManagerMapper.getByParameter(select);
		circuitManagerMapper.updateByParameter(map);
	}

	/** ********************************wangjian**end************************* */
	/** ################楚##河#############汉##界############################### */
	/** ********************************daihuijun**begin********************** */

	/**
	 * sdh电路交叉连接查询
	 * 
	 * @author DaiHuijun
	 * @param connectRate
	 *            交叉连接速率
	 * @param circuitState
	 *            交叉连接状态
	 * @param neId
	 *            选择的网元编号
	 * @param crossChange
	 *            交叉连接变化
	 * @return T_BASE_SDH_CRS表中记录
	 */
	@Override
	public Map selectCrossConnect(Map map, Map to_Map) throws CommonException {
		// Map return_map = new HashMap();
		Map<String, Object> result = new HashMap<String, Object>();
		Map condition = new HashMap();
		condition.put("VALUE", "TYPE");
		condition.put("NAME", "t_base_ne");
		condition.put("ID_NAME", "BASE_NE_ID");
		condition.put("ID_VALUE", to_Map.get("nodeId"));
		List<Map> neType = circuitManagerMapper.getByParameter(condition);
		// 判断网元类型
		if (neType != null) {
			if (Integer.parseInt((neType.get(0).get("TYPE").toString())) == 1) {
				map.put("ctp_table", "T_base_sdh_ctp");
				map.put("crs_table", "t_base_sdh_crs");
				String actp_value = "concat(c1.CTP_J_ORIGINAL,'-',c1.CTP_K,'-',c1.CTP_L,'-',c1.CTP_M)";
				String zctp_value = "concat(c2.CTP_J_ORIGINAL,'-',c2.CTP_K,'-',c2.CTP_L,'-',c2.CTP_M)";
				map.put("CTP_ID", "base_sdh_CTP_ID");
				map.put("A_END_CTP_VALUE", actp_value);
				map.put("Z_END_CTP_VALUE", zctp_value);
				map.put("needSelectIsFix", false);
			} else {
				map.put("CTP_ID", "BASE_OTN_CTP_ID");
				map.put("ctp_table", "T_base_otn_ctp");
				map.put("crs_table", "t_base_otn_crs");
				map.put("A_END_CTP_VALUE", "c1.CTP_VALUE");
				map.put("Z_END_CTP_VALUE", "c2.CTP_VALUE");
				map.put("needSelectIsFix", true);
			}
		}
		map.put("ID", to_Map.get("nodeId"));
		List<Map> mapList = circuitManagerMapper.selectCrossConnect(map);
		Map totel = circuitManagerMapper.crossConnectTotel(map);
		result.put("rows", mapList);
		result.put("total", totel.get("count(*)"));
		return result;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ICircuitManagerService#modifyCircuit(java.util.List)
	 */
	@Override
	public void modifyCircuit(List<Map> list) throws CommonException {

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get("SVC_TYPE").toString().equals("3")) {
				list.get(i).put("tableName", "t_cir_otn_circuit_info");
			} else if(list.get(i).get("SVC_TYPE").toString().equals("4")){
				list.get(i).put("tableName", "t_cir_ptn_circuit_info");
			}else{
				list.get(i).put("tableName", "t_cir_circuit_info");
			}
			circuitManagerMapper.updateCircuitInfo(list.get(i));
		}

	}

	/**
	 * 获取电路详细信息
	 */
	public Map<String, Object> getCirInfoById(Map circuitId)
			throws CommonException {
		HashMap map = new HashMap();
		map.put("vCircuit", circuitId.get("vCircuit"));
		map.put("ctp_table", "T_base_sdh_ctp");
		String actp_value = "concat(ctp1.CTP_J_ORIGINAL,'-',ctp1.CTP_K,'-',ctp1.CTP_L,'-',ctp1.CTP_M)";
		String zctp_value = "concat(ctp2.CTP_J_ORIGINAL,'-',ctp2.CTP_K,'-',ctp2.CTP_L,'-',ctp2.CTP_M)";
		map.put("CTP_ID", "base_sdh_CTP_ID");
		map.put("A_END_CTP_VALUE", actp_value);
		map.put("Z_END_CTP_VALUE", zctp_value);
		map.put("cir_info_table", "t_cir_circuit_info");
		map.put("cir_table", "t_cir_circuit");
		map.put("cir_info_id", "CIR_CIRCUIT_INFO_ID");
		map.put("cir_id", "CIR_CIRCUIT_ID");
		List<Map> mapList = circuitManagerMapper.getCirInfoById(map);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rows", mapList);
		result.put("total", mapList.size());

		return result;
	}

	/**
	 * 获取ptn电路详细信息
	 */
	public Map<String, Object> getPtnCirInfoById(Map circuitId)
			throws CommonException {
		HashMap map = new HashMap();
		map.put("vCircuit", circuitId.get("vCircuit"));
		List<Map> mapList = circuitManagerMapper.getPtnCirInfoById(map);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rows", mapList);
		result.put("total", mapList.size());

		return result;
	}
	
	public Map<String, Object> getOtnCirInfoById(Map circuitId)
			throws CommonException {
		HashMap map = new HashMap();
		map.put("service", circuitId.get("serviceType"));
		map.put("vCircuit", circuitId.get("vCircuit"));
		map.put("ctp_table", "T_base_otn_ctp");
		String actp_value = "ctp1.DISPLAY_NAME";
		String zctp_value = "ctp2.DISPLAY_NAME";
		map.put("CTP_ID", "base_otn_CTP_ID");
		map.put("A_END_CTP_VALUE", actp_value);
		map.put("Z_END_CTP_VALUE", zctp_value);
		map.put("cir_info_table", "t_cir_otn_circuit_info");
		map.put("cir_table", "t_cir_otn_circuit");
		map.put("cir_info_id", "CIR_OTN_CIRCUIT_INFO_ID");
		map.put("cir_id", "CIR_OTN_CIRCUIT_ID");
		List<Map> mapList = circuitManagerMapper.getCirInfoById(map);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rows", mapList);
		result.put("total", mapList.size());

		return result;
	}

	/**
	 * 电路端到端查询 如果前台没有传端口信息则NodeId默认为-1，NodeLevel默认为0 serviceType表示电路类型 1：sdh
	 * 2：以太网 3：wdm Map conditions = {aNodeId = aNodeId, aNodeLevel = aNodeLevel,
	 * zNodeId = zNodeId, zNodeLevel = zNodeLevel, aLocationLevel =
	 * aLocationLevel, aLocationId = aLocationId, zLocationLevel =
	 * zLocationLevel, zLocationId = zLocationId, serviceType = serviceType,
	 * limit = 200 };
	 * 
	 */
	public Map<String, Object> getCircuitByPtp(Map conditions)
			throws CommonException {
		int i = 1;
		// 当aNodeId、zNodeId、aLocationId、zLocationId为-1时表示该段为空
		// start：数据库从第几条记录开始查找
		int start = Integer.parseInt(conditions.get("start").toString());
		// A端设备ID
		int aNodeId = Integer.parseInt(conditions.get("aNodeId").toString());
		// A端设备类型：网元、板卡或端口
		int aNodeLevel = Integer.parseInt(conditions.get("aNodeLevel")
				.toString());
		// Z端设备ID
		int zNodeId = Integer.parseInt(conditions.get("zNodeId").toString());
		// Z端设备类型：网元、板卡或端口
		int zNodeLevel = Integer.parseInt(conditions.get("zNodeLevel")
				.toString());
		// 一次查询记录个数
		int limit = Integer.parseInt(conditions.get("limit").toString());
		// A端地点ID
		int aLocationLevel = Integer.parseInt(conditions.get("aLocationLevel")
				.toString());
		// A端地点级别：局站或机房
		int aLocationId = Integer.parseInt(conditions.get("aLocationId")
				.toString());
		// Z端地点ID
		int zLocationLevel = Integer.parseInt(conditions.get("zLocationLevel")
				.toString());
		// z端地点级别：局站或机房
		int zLocationId = Integer.parseInt(conditions.get("zLocationId")
				.toString());
		int serviceType = Integer.parseInt(conditions.get("serviceType")
				.toString());
		HashMap map = new HashMap();
		map.put("start", start);
		map.put("limit", limit);
		map.put("serviceType", serviceType);
		// 定义cons变量用来存放电路类型变量
		CircuitDefine cons;
		// 由CircuitDefineFactory类负责生产CircuitDefine对象
		CircuitDefineFactory fc = new CircuitDefineFactory();
		// 当serviceType值为1或2，即sdh或以太网电路，将SDH对象赋值给cons变量
		// 当serviceType为otn/wdm电路时，将OTN对象赋值给cons变量
		cons = fc.getCircuitDefineFactory(serviceType);
		map.put("ctp_table", cons.ctpTable);
		map.put("CTP_ID", cons.ctpId);
		map.put("A_END_CTP_VALUE", cons.actp);
		map.put("Z_END_CTP_VALUE", cons.zctp);
		map.put("cir_info_table", cons.cirInfoTable);
		map.put("cir_table", cons.cirTable);
		map.put("cir_info_id", cons.cirInfoId);
		map.put("cir_id", cons.cirId);
		// A端设备选择不为空
		if (aNodeId != -1) {
			if (aNodeLevel == 4) {// A端为网元
				map.put("aNodeName", "BASE_NE_ID");
				map.put("aNodeId", aNodeId);
			}
			if (aNodeLevel == 5) {// A端为机架
				map.put("aNodeName", "BASE_SHELF_ID");
				map.put("aNodeId", aNodeId);
			}
			if (aNodeLevel == 6) {// A端为槽道
				map.put("aNodeName", "BASE_UNIT_ID");
				map.put("aNodeId", aNodeId);
			}
			if (aNodeLevel == 7) {// A端为子槽道
				map.put("aNodeName", "BASE_SUB_UNIT_ID");
				map.put("aNodeId", aNodeId);
			}
			if (aNodeLevel == 8) {// A端为端口
				map.put("aNodeName", "BASE_PTP_ID");
				map.put("aNodeId", aNodeId);
			}
		}
		if (zNodeId != -1) {
			if (zNodeLevel == 4) {// Z端为网元
				map.put("zNodeName", "BASE_NE_ID");
				map.put("zNodeId", zNodeId);
			}
			if (zNodeLevel == 5) {// Z端为机架
				map.put("zNodeName", "BASE_SHELF_ID");
				map.put("zNodeId", zNodeId);
			}
			if (zNodeLevel == 6) {// Z端为槽道
				map.put("zNodeName", "BASE_UNIT_ID");
				map.put("zNodeId", zNodeId);
			}
			if (zNodeLevel == 7) {// Z端为子槽道
				map.put("zNodeName", "BASE_SUB_UNIT_ID");
				map.put("zNodeId", zNodeId);
			}
			if (zNodeLevel == 8) {// Z端为端口
				map.put("zNodeName", "BASE_PTP_ID");
				map.put("zNodeId", zNodeId);
			}
		}
		if (aLocationId != -1) {// A端地点选择不为空,
			// ptn 电路 网元表中不存放局站id，需要将局站id转为机房id
			List rooma = new ArrayList();
			rooma.add(0);
			if (aLocationLevel == AreaManagerImpl.AreaDef.LEVEL.STATION) {// 选择局站
				map.put("aLocationName", "RESOURCE_STATION_ID");
				map.put("aLocationId", aLocationId);
				if(serviceType == 4){
					// 将局站转化成机房
					Map select = new HashMap();
					select.put("NAME", "t_resource_room");
					select.put("VALUE", "*");
					select.put("ID_NAME", "RESOURCE_STATION_ID");
					select.put("ID_VALUE", aLocationId);
					
					List<Map> listRoom = circuitManagerMapper.getByParameter(select);
					if(listRoom!=null && listRoom.size()>0){
						for(int a = 0;a<listRoom.size();a++){
							rooma.add(listRoom.get(a).get("RESOURCE_ROOM_ID"));
						}
					}
				}
			}
			if (aLocationLevel == AreaManagerImpl.AreaDef.LEVEL.ROOM) {// 选择机房
				map.put("aLocationName", "RESOURCE_ROOM_ID");
				map.put("aLocationId", aLocationId);
				if(serviceType == 4){
					rooma.add(aLocationId);
				}
			}
			if(serviceType == 4){
				map.put("aLocationLevel", AreaManagerImpl.AreaDef.LEVEL.ROOM);
				map.put("roomaList", rooma);
			}else{
				map.put("aLocationLevel", aLocationLevel);

			}
			
			
		}
		if (zLocationId != -1) {// Z端地点不为空，网元表中不存放局站id，需要将局站id转为机房id
			// ptn 电路 网元表中不存放局站id，需要将局站id转为机房id
			List roomz = new ArrayList();
			roomz.add(0);
			if (zLocationLevel == AreaManagerImpl.AreaDef.LEVEL.STATION) {// 选择局站
				map.put("zLocationName", "RESOURCE_STATION_ID");
				map.put("zLocationId", zLocationId);
				if(serviceType == 4){
					// 将局站转化成机房
					Map select = new HashMap();
					select.put("NAME", "t_resource_room");
					select.put("VALUE", "*");
					select.put("ID_NAME", "RESOURCE_STATION_ID");
					select.put("ID_VALUE", zLocationId);
					
					List<Map> listRoom = circuitManagerMapper.getByParameter(select);
					if(listRoom!=null && listRoom.size()>0){
						for(int z = 0;z<listRoom.size();z++){
							roomz.add(listRoom.get(z).get("RESOURCE_ROOM_ID"));
						}
					}
				}
			}
			if (zLocationLevel == AreaManagerImpl.AreaDef.LEVEL.ROOM) {// 选择机房
				map.put("zLocationName", "RESOURCE_ROOM_ID");
				map.put("zLocationId", zLocationId);
				if(serviceType == 4){
					roomz.add(zLocationId);
				}
			}
			if(serviceType == 4){
				map.put("zLocationLevel", AreaManagerImpl.AreaDef.LEVEL.ROOM);
				map.put("roomzList", roomz);
			}else{
				map.put("zLocationLevel", zLocationLevel);

			}
			//map.put("zLocationLevel", zLocationLevel);
		}
		Map<String, Object> result = new HashMap<String, Object>();

		if(serviceType == 4){
			// 如果数据是ptn 调用新的查询方法
			List<Map> mapListPtn = circuitManagerMapper.getPtnInfoByPtp(map); 
			Map totalPtn = circuitManagerMapper.getPtnInfoByPtpTotal(map); 
			result.put("rows", mapListPtn);
			result.put("total", totalPtn.get("total"));
		}else{
			// 定义dao.mysql.CircuitManagerMapper.getCircuitByPtp方法的参数
			List<Map> mapList = circuitManagerMapper.getCircuitInfoByCtp(map);
			Map total = circuitManagerMapper.getptpCircuitTotal(map);
			result.put("rows", mapList);
			result.put("total", total.get("total"));
		}
		
		
		return result;
	}

	/**
	 * sdh电路详细路由信息查询
	 */
	public Map<String, Object> getCircuitRoute(int circuitId)
			throws CommonException {
		/*
		 * 查询数据库获取原始数据，返回的数据字段有：
		 * NE_NAME(网元名称)、EMS_NAME(网管名称)、A_END_CTP(A端时隙)、Z_END_CTP（Z端时隙）
		 * A_END_PORT(A端端口名)、Z_END_PORT(Z端端口名)、LINK_NAME(链路名称)
		 */
		List<Map> origianlData = circuitManagerMapper
				.getCircuitRoute(circuitId);
		// 定义routeInfo变量存放返回标准格式数据
		List<Map> routeInfo = new ArrayList();
		// 将origianlData中交叉连接信息格式化，返回字段：NE_NAME,EMS_NAME,CTP,PORT,LINK_NAME
		int j = 1;// list的计数，
		for (int i = 0; i < origianlData.size(); i++) {
			// 如果返回的是链路信息，则将链路名称填入网元名称中
			if (origianlData.get(i).get("LINK_NAME") != null) {
				Map temp_link = new HashMap();
				if(origianlData.get(i).get("LINK_NO")!=null){
					temp_link.put("NE_NAME","<a href='javascript:void(0)' onclick='openOtnTab(\" "+origianlData.get(i).get("LINK_NO")+" \" )'>链路：" + origianlData.get(i).get("LINK_NAME")+"</a>");
				}else{
					temp_link.put("NE_NAME","链路：" + origianlData.get(i).get("LINK_NAME"));
				}
				
				routeInfo.add(temp_link);
			} else {// 将一组数据中的A.Z端信息拆成两组数据
				Map temp_a = new HashMap();
				temp_a.put("NE_NAME", origianlData.get(i).get("NE_NAME"));
				temp_a.put("EMS_NAME", origianlData.get(i).get("EMS_NAME"));
				temp_a.put("CTP", origianlData.get(i).get("A_END_CTP"));
				temp_a.put("PORT", origianlData.get(i).get("A_END_PORT"));
				routeInfo.add(temp_a);
				Map temp_z = new HashMap();
				temp_z.put("NE_NAME", origianlData.get(i).get("NE_NAME"));
				temp_z.put("EMS_NAME", origianlData.get(i).get("EMS_NAME"));
				temp_z.put("CTP", origianlData.get(i).get("Z_END_CTP"));
				temp_z.put("PORT", origianlData.get(i).get("Z_END_PORT"));
				routeInfo.add(temp_z);
			}
		}
		// 声明一个返回的map变量，其字段包括rows（路由详细信息）、total（记录总数）
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rows", routeInfo);
		result.put("total", routeInfo.size());
		return result;
	}

	/**
	 * 获取sdh电路路由图
	 */
	public Map<String, Object> getRouteTopo(int circuitNo)
			throws CommonException {
		/*
		 * 查询数据库获取原始数据，返回的数据字段有：
		 * NE_NAME(网元名称)BASE_NE_ID(网元id)、EMS_NAME(网管名称)、A_END_CTP
		 * (A端时隙)、Z_END_CTP（Z端时隙）
		 * A_END_PORT(A端端口名)、Z_END_PORT(Z端端口名)、LINK_NAME(链路名称)
		 */
		Map query = new HashMap();
		query.put("vCircuit", circuitNo);
		List<Map> resultList = circuitManagerMapper
				.getCircuitBycircuitNo(query);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Map map1 : resultList) {
			ids.add(Integer.parseInt(map1.get("CIR_CIRCUIT_ID").toString()));
		}
		if (ids.size() <= 0) {
			ids.add(-1);
		}

		// 存放节点和链路信息
		List rows = new ArrayList();
		List rawLineRow = new ArrayList();
		List<Map> allNodeRecord = new ArrayList();
		List list = new ArrayList();
		for (int j = 0; j < ids.size(); j++) {
			List<Map> origianlData = circuitManagerMapper
					.getCircuitRouteTopo(ids.get(j));
			List<Map> formatData = getFormatData(origianlData);
			// 创建topo图模块的节点对象和链路对象

			for (int i = 0; i < formatData.size(); i++) {
				if (formatData.get(i).get("base_ne_id") != null) {
					allNodeRecord.add(formatData.get(i));
				} else {
					TopoLineModel linkModel = new TopoLineModel();
					List<Map> portInfo = new ArrayList<Map>();
					portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
							.parseInt(formatData.get(i).get("A_END_PTP")
									.toString())));
					portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
							.parseInt(formatData.get(i).get("Z_END_PTP")
									.toString())));
					Map intallMap = new HashMap();
					// 为链路的a/z端赋值
					for (int k = 0; k < portInfo.size(); k++) {
						String name = "a";
						if (k == 1) {
							name = "z";
						}
						Iterator it = portInfo.get(k).keySet().iterator();
						while (it.hasNext()) {
							Object o = it.next();
							intallMap.put(name + o, portInfo.get(k).get(o));
						}
					}
					intallMap.put("aNeType", 1);
					intallMap.put("zNeType", 1);
					// 为LinkAlarmModel赋值
					LinkAlarmModel linkAlarm = topoManagerService
							.transMap2LinkAlarmModel(intallMap);
					linkAlarm.setLinkId(Integer.parseInt(formatData.get(i)
							.get("BASE_LINK_ID").toString()));
					ArrayList<Integer> neList = new ArrayList<Integer>();
					ArrayList<Integer> aPtpList = new ArrayList<Integer>();
					ArrayList<Integer> zPtpList = new ArrayList<Integer>();
					aPtpList.add(Integer.parseInt(formatData.get(i)
							.get("A_END_PTP").toString()));
					Map countA = faultManagerService
							.getAllCurrentAlarmCountForCircuit(neList, aPtpList);
					linkAlarm.setaCRCount(Integer.parseInt(countA.get(
							"PS_CRITICAL").toString()));
					linkAlarm.setaMJCount(Integer.parseInt(countA.get(
							"PS_MAJOR").toString()));
					linkAlarm.setaMNCount(Integer.parseInt(countA.get(
							"PS_MINOR").toString()));
					linkAlarm.setaWRCount(Integer.parseInt(countA.get(
							"PS_WARNING").toString()));
					zPtpList.add(Integer.parseInt(formatData.get(i)
							.get("Z_END_PTP").toString()));
					Map countZ = faultManagerService
							.getAllCurrentAlarmCountForCircuit(neList, zPtpList);
					linkAlarm.setzCRCount(Integer.parseInt(countZ.get(
							"PS_CRITICAL").toString()));
					linkAlarm.setzMJCount(Integer.parseInt(countZ.get(
							"PS_MAJOR").toString()));
					linkAlarm.setzMNCount(Integer.parseInt(countZ.get(
							"PS_MINOR").toString()));
					linkAlarm.setzWRCount(Integer.parseInt(countZ.get(
							"PS_WARNING").toString()));
					List<LinkAlarmModel> rawLinkAlarm = new ArrayList<LinkAlarmModel>();
					rawLinkAlarm.add(linkAlarm);
					linkModel.setLinkAlarm(rawLinkAlarm);
					linkModel.setNodeOrLine("line");
					linkModel.setLineType("neLine");
					linkModel.setFromNode(formatData.get(i - 1)
							.get("base_ne_id").toString());
					linkModel.setFromNodeType("3");
					linkModel.setToNode(formatData.get(i + 1).get("base_ne_id")
							.toString());
					linkModel.setToNodeType("3");
					rawLineRow.add(linkModel);
				}
			}
		}
		rows.addAll(deleteRepeatNode(allNodeRecord, "1"));
		rows.addAll(topoManagerService.deleteRepeatLink(rawLineRow));
		// 用来返回结果
		Map<String, Object> result = new HashMap();
		result.putAll(topoManagerService.getAlarmColorSet());
		result.put("rows", rows);
		result.put("currentTopoType", "NetWork");
		result.put("total", rows.size());
		result.put("layout", "free");
		System.out.println("----------------------->"+result.toString());
		return result;
	}
	/**
	 * 获取ptn电路路由图
	 */
	public Map<String, Object> getPtnRouteTopo(int circuitNo)
			throws CommonException {
		/*
		 * 查询数据库获取原始数据，返回的数据字段有：
		 * NE_NAME(网元名称)、base_ne_id(网元id)、PRODUCT_NAME(产品名称)、LINK_NAME(链路名称)
		 * A_END_PTP(链路A端ID),Z_END_PTP(链路Z端ID) ,BASE_LINK_ID(链路ID)
		 * BASE_EMS_CONNECTION_ID(网管ID)、PTP1(路由起点端口ID)、PTP2(路由终点端口ID)
		 */
		
		Map query = new HashMap();
		query.put("vCircuit", circuitNo);
		List<Map> resultList = circuitManagerMapper
				.getPtnCircuitBycircuitNo(query);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Map map1 : resultList) {
			ids.add(Integer.parseInt(map1.get("CIR_CIRCUIT_ID").toString()));
		}
		if (ids.size() <= 0) {
			ids.add(-1);
		}

		// 存放节点和链路信息
		List rows = new ArrayList();
		List rawLineRow = new ArrayList();
		List<Map> allNodeRecord = new ArrayList();
		List list = new ArrayList();
		for (int j = 0; j < ids.size(); j++) {
			//List<Map> origianlData = new ArrayList<Map>();
			
			List<Map> origianlData = circuitManagerMapper
					.getPtnCircuitRouteTopo(ids.get(j));
			// 查询电路az端
			List<Map> cirMap = circuitManagerMapper.getPtnCirById(ids.get(j));
			if(cirMap!=null && cirMap.size()>0){
				// 起点赋值
				if(origianlData.size()>0){
					Object aPtp = origianlData.get(0).get("PTP1");
					origianlData.get(0).put("PTP1",cirMap.get(0).get("A_END_PTP"));
					origianlData.get(0).put("PTP2", aPtp);
				}
				
				if(origianlData.size()>1){
					// 结尾赋值
					origianlData.get(origianlData.size()-1).put("PTP2", cirMap.get(0).get("Z_END_PTP"));
				}
			}
			
			List<Map> formatData = getFormatData(origianlData);
			// 创建topo图模块的节点对象和链路对象

			for (int i = 0; i < formatData.size(); i++) {
				if (formatData.get(i).get("base_ne_id") != null) {
					allNodeRecord.add(formatData.get(i));
				} else {
					TopoLineModel linkModel = new TopoLineModel();
					List<Map> portInfo = new ArrayList<Map>();
					portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
							.parseInt(formatData.get(i).get("A_END_PTP")
									.toString())));
					portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
							.parseInt(formatData.get(i).get("Z_END_PTP")
									.toString())));
					Map intallMap = new HashMap();
					// 为链路的a/z端赋值
					for (int k = 0; k < portInfo.size(); k++) {
						String name = "a";
						if (k == 1) {
							name = "z";
						}
						Iterator it = portInfo.get(k).keySet().iterator();
						while (it.hasNext()) {
							Object o = it.next();
							intallMap.put(name + o, portInfo.get(k).get(o));
						}
					}
					intallMap.put("aNeType", 1);
					intallMap.put("zNeType", 1);
					// 为LinkAlarmModel赋值
					LinkAlarmModel linkAlarm = topoManagerService
							.transMap2LinkAlarmModel(intallMap);
					linkAlarm.setLinkId(Integer.parseInt(formatData.get(i)
							.get("BASE_LINK_ID").toString()));
					ArrayList<Integer> neList = new ArrayList<Integer>();
					ArrayList<Integer> aPtpList = new ArrayList<Integer>();
					ArrayList<Integer> zPtpList = new ArrayList<Integer>();
					aPtpList.add(Integer.parseInt(formatData.get(i)
							.get("A_END_PTP").toString()));
					Map countA = faultManagerService
							.getAllCurrentAlarmCountForCircuit(neList, aPtpList);
					linkAlarm.setaCRCount(Integer.parseInt(countA.get(
							"PS_CRITICAL").toString()));
					linkAlarm.setaMJCount(Integer.parseInt(countA.get(
							"PS_MAJOR").toString()));
					linkAlarm.setaMNCount(Integer.parseInt(countA.get(
							"PS_MINOR").toString()));
					linkAlarm.setaWRCount(Integer.parseInt(countA.get(
							"PS_WARNING").toString()));
					zPtpList.add(Integer.parseInt(formatData.get(i)
							.get("Z_END_PTP").toString()));
					Map countZ = faultManagerService
							.getAllCurrentAlarmCountForCircuit(neList, zPtpList);
					linkAlarm.setzCRCount(Integer.parseInt(countZ.get(
							"PS_CRITICAL").toString()));
					linkAlarm.setzMJCount(Integer.parseInt(countZ.get(
							"PS_MAJOR").toString()));
					linkAlarm.setzMNCount(Integer.parseInt(countZ.get(
							"PS_MINOR").toString()));
					linkAlarm.setzWRCount(Integer.parseInt(countZ.get(
							"PS_WARNING").toString()));
					List<LinkAlarmModel> rawLinkAlarm = new ArrayList<LinkAlarmModel>();
					rawLinkAlarm.add(linkAlarm);
					linkModel.setLinkAlarm(rawLinkAlarm);
					linkModel.setNodeOrLine("line");
					linkModel.setLineType("neLine");
					linkModel.setFromNode(formatData.get(i - 1)
							.get("base_ne_id").toString());
					linkModel.setFromNodeType("3");
					linkModel.setToNode(formatData.get(i + 1).get("base_ne_id")
							.toString());
					linkModel.setToNodeType("3");
					rawLineRow.add(linkModel);
				}
			}
		}
		rows.addAll(deleteRepeatNode(allNodeRecord, "1"));
		rows.addAll(topoManagerService.deleteRepeatLink(rawLineRow));
		// 用来返回结果
		Map<String, Object> result = new HashMap();
		result.putAll(topoManagerService.getAlarmColorSet());
		result.put("rows", rows);
		result.put("currentTopoType", "NetWork");
		result.put("total", rows.size());
		result.put("layout", "free");
		return result;
	}
	/**
	 * 获取otn电路路由图
	 */
	public Map<String, Object> getOtnRouteTopo(int circuitNo)
			throws CommonException {
		/*
		 * 查询数据库获取原始数据，返回的数据字段有：
		 * NE_NAME(网元名称)BASE_NE_ID(网元id)、EMS_NAME(网管名称)、A_END_CTP
		 * (A端时隙)、Z_END_CTP（Z端时隙）
		 * A_END_PORT(A端端口名)、Z_END_PORT(Z端端口名)、LINK_NAME(链路名称)
		 */
		Map query = new HashMap();
		query.put("vCircuit", circuitNo);
		List<Map> resultList = circuitManagerMapper
				.getOtnCircuitBycircuitNo(query);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Map map1 : resultList) {
			ids.add(Integer.parseInt(map1.get("CIR_OTN_CIRCUIT_ID").toString()));
		}
		if (ids.size() <= 0) {
			ids.add(-1);
		}

		// 存放节点和链路信息
		List rows = new ArrayList();
		List rawLineRow = new ArrayList();
		List<Map> allNodeRecord = new ArrayList();
		List list = new ArrayList();
		for (int j = 0; j < ids.size(); j++) {
			List<Map> origianlData = circuitManagerMapper
					.getOtnCircuitRouteTopo(ids.get(j));
			List<Map> formatData = getFormatData(origianlData);
			// 创建topo图模块的节点对象和链路对象

			for (int i = 0; i < formatData.size(); i++) {
				if (formatData.get(i).get("base_ne_id") != null) {
					allNodeRecord.add(formatData.get(i));
				} else {
					TopoLineModel linkModel = new TopoLineModel();
					List<Map> portInfo = new ArrayList<Map>();
					portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
							.parseInt(formatData.get(i).get("A_END_PTP")
									.toString())));
					portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
							.parseInt(formatData.get(i).get("Z_END_PTP")
									.toString())));
					Map intallMap = new HashMap();
					// 为链路的a/z端赋值
					for (int k = 0; k < portInfo.size(); k++) {
						String name = "a";
						if (k == 1) {
							name = "z";
						}
						Iterator it = portInfo.get(k).keySet().iterator();
						while (it.hasNext()) {
							Object o = it.next();
							intallMap.put(name + o, portInfo.get(k).get(o));
						}
					}
					intallMap.put("aNeType", 3);
					intallMap.put("zNeType", 3);
					// 为LinkAlarmModel赋值
					LinkAlarmModel linkAlarm = topoManagerService
							.transMap2LinkAlarmModel(intallMap);
					linkAlarm.setLinkId(Integer.parseInt(formatData.get(i)
							.get("BASE_LINK_ID").toString()));
					ArrayList<Integer> neList = new ArrayList<Integer>();
					ArrayList<Integer> aPtpList = new ArrayList<Integer>();
					ArrayList<Integer> zPtpList = new ArrayList<Integer>();
					aPtpList.add(Integer.parseInt(formatData.get(i)
							.get("A_END_PTP").toString()));
					Map countA = faultManagerService
							.getAllCurrentAlarmCountForCircuit(neList, aPtpList);
					linkAlarm.setaCRCount(Integer.parseInt(countA.get(
							"PS_CRITICAL").toString()));
					linkAlarm.setaMJCount(Integer.parseInt(countA.get(
							"PS_MAJOR").toString()));
					linkAlarm.setaMNCount(Integer.parseInt(countA.get(
							"PS_MINOR").toString()));
					linkAlarm.setaWRCount(Integer.parseInt(countA.get(
							"PS_WARNING").toString()));
					zPtpList.add(Integer.parseInt(formatData.get(i)
							.get("Z_END_PTP").toString()));
					Map countZ = faultManagerService
							.getAllCurrentAlarmCountForCircuit(neList, zPtpList);
					linkAlarm.setzCRCount(Integer.parseInt(countZ.get(
							"PS_CRITICAL").toString()));
					linkAlarm.setzMJCount(Integer.parseInt(countZ.get(
							"PS_MAJOR").toString()));
					linkAlarm.setzMNCount(Integer.parseInt(countZ.get(
							"PS_MINOR").toString()));
					linkAlarm.setzWRCount(Integer.parseInt(countZ.get(
							"PS_WARNING").toString()));
					List<LinkAlarmModel> rawLinkAlarm = new ArrayList<LinkAlarmModel>();
					rawLinkAlarm.add(linkAlarm);
					linkModel.setLinkAlarm(rawLinkAlarm);
					linkModel.setNodeOrLine("line");
					linkModel.setLineType("neLine");
					linkModel.setFromNode(formatData.get(i - 1)
							.get("base_ne_id").toString());
					linkModel.setFromNodeType("3");
					linkModel.setToNode(formatData.get(i + 1).get("base_ne_id")
							.toString());
					linkModel.setToNodeType("3");
					rawLineRow.add(linkModel);
				}
			}
		}
		//这里"1"应该变为"3",由于前台内部路由暂无法实现
		rows.addAll(deleteRepeatNode(allNodeRecord, "1"));
		rows.addAll(topoManagerService.deleteRepeatLink(rawLineRow));
		// 用来返回结果
		Map<String, Object> result = new HashMap();
		result.putAll(topoManagerService.getAlarmColorSet());
		result.put("rows", rows);
		result.put("currentTopoType", "NetWork");
		result.put("total", rows.size());
		result.put("layout", "free");
		return result;
	}

	// 删除所有重复的网元节点
	public List<TopoNodeModel> deleteRepeatNode(List<Map> allRecord,
			String neType) throws CommonException {
		List<TopoNodeModel> nodes = new ArrayList<TopoNodeModel>();
		try {
			for (int i = 0; i < allRecord.size(); i++) {
				String tempP1 = CommonDefine.UNKNOW_ELEMENT;
				String tempP2 = CommonDefine.UNKNOW_ELEMENT;
				if (allRecord.get(i).get("PTP1") != null) {
					tempP1 = allRecord.get(i).get("PTP1").toString();
				}
				if (allRecord.get(i).get("PTP2") != null) {
					tempP2 = allRecord.get(i).get("PTP2").toString();
				}
				String ptps = tempP1 + "," + tempP2;
				allRecord.get(i).put("ptps", ptps);
				for (int j = i; j < allRecord.size(); j++) {
					if (i != j
							&& (allRecord.get(i).get("base_ne_id").toString())
									.equals(allRecord.get(j).get("base_ne_id")
											.toString())) {
						if (allRecord.get(j).get("PTP1") != null) {
							tempP1 = allRecord.get(j).get("PTP1").toString();
						}
						if (allRecord.get(j).get("PTP2") != null) {
							tempP2 = allRecord.get(j).get("PTP2").toString();
						}
						ptps = ptps + "," + tempP1 + "," + tempP2;
						allRecord.get(i).put("ptps", ptps);
						allRecord.remove(j);
						// 重新检查下一个是否重复，防止连续重复网元漏算的情况
						j--;
					}
				}
			}
			for (int i = 0; i < allRecord.size(); i++) {
				TopoNodeModel neModel = new TopoNodeModel();
				List<Integer> neList = new ArrayList<Integer>();
				List<Integer> ptpList = new ArrayList<Integer>();
				neList.add(Integer.parseInt(allRecord.get(i).get("base_ne_id")
						.toString()));
				// 将以逗号连接的ptp转化成List<Integer>
				String[] temp = allRecord.get(i).get("ptps").toString().trim()
						.split(",");
				for (String a : temp) {
					ptpList.add(Integer.parseInt(a));
				}
				// 获取PTP(含CTP)以外的所有告警计数
				Map count = faultManagerService
						.getAllCurrentAlarmCountForCircuit(neList, new ArrayList<Integer>());
				neModel.setCrCount(Integer.parseInt(count.get("PS_CRITICAL")
						.toString()));
				neModel.setMjCount(Integer.parseInt(count.get("PS_MAJOR")
						.toString()));
				neModel.setMnCount(Integer.parseInt(count.get("PS_MINOR")
						.toString()));
				neModel.setWrCount(Integer.parseInt(count.get("PS_WARNING")
						.toString()));
				
				// 获取PTP的告警计数（不含CTP告警）
				count = faultManagerService
						.getAllCurrentAlarmCountForCircuit(new ArrayList<Integer>(), ptpList);
				neModel.setCrPtpCount(Integer.parseInt(count.get("PS_CRITICAL")
						.toString()));
				neModel.setMjPtpCount(Integer.parseInt(count.get("PS_MAJOR")
						.toString()));
				neModel.setMnPtpCount(Integer.parseInt(count.get("PS_MINOR")
						.toString()));
				neModel.setWrPtpCount(Integer.parseInt(count.get("PS_WARNING")
						.toString()));
				
				neModel.setPtpIdList(ptpList);

				neModel.setDisplayName(allRecord.get(i).get("NE_NAME")
						.toString());

				if(allRecord.get(i).get("PRODUCT_NAME")!=null){
					neModel.setProductName(allRecord.get(i).get("PRODUCT_NAME").toString());
				}
				neModel.setNodeOrLine("node");
				neModel.setEmsId(allRecord.get(i).get("BASE_EMS_CONNECTION_ID")
						.toString());
				neModel.setNodeId(allRecord.get(i).get("base_ne_id").toString());
				neModel.setNeType(neType);
				neModel.setNodeType("3");
				nodes.add(neModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, 111);
		}
		return nodes;
	}

	protected List getFormatData(List<Map> origianlData) {
		List<Map> data = new ArrayList<Map>();
		List<Map> formatData = new ArrayList<Map>();
		// 遇到断头电路时再次查询两端的网元，添加到origianlData中
		if (origianlData.size() > 0) {
			if (origianlData.get(0).get("BASE_LINK_ID") != null) {
				Map mapA = new HashMap();
				mapA.put("end", "A");
				mapA.put("value", origianlData.get(0).get("A_END_PTP"));
				Map firstNe = circuitManagerMapper.getNeInfoByLink(mapA).get(0);
				origianlData.get(origianlData.size() - 1).put("A_END_PTP",
						CommonDefine.UNKNOW_ELEMENT);
				firstNe.put("PTP1", CommonDefine.UNKNOW_ELEMENT);
				firstNe.put("base_ne_id", CommonDefine.UNKNOW_ELEMENT);
				firstNe.put("NE_NAME", "未知网元");
				firstNe.put("PRODUCT_NAME", "未知网元");
				origianlData.add(0, firstNe);
			}
			if (origianlData.get(origianlData.size() - 1).get("BASE_LINK_ID") != null) {
				Map mapB = new HashMap();
				mapB.put("end", "Z");
				mapB.put("value", origianlData.get(origianlData.size() - 1)
						.get("Z_END_PTP"));
				// List l=circuitManagerMapper.getNeInfoByLink(mapB);
				Map lastNe = circuitManagerMapper.getNeInfoByLink(mapB).get(0);
				origianlData.get(origianlData.size() - 1).put("Z_END_PTP",
						CommonDefine.UNKNOW_ELEMENT);
				lastNe.put("PTP2", CommonDefine.UNKNOW_ELEMENT);
				lastNe.put("base_ne_id", CommonDefine.UNKNOW_ELEMENT);
				lastNe.put("NE_NAME", "未知网元");
				lastNe.put("PRODUCT_NAME", "未知网元");
				origianlData.add(lastNe);
			}
		}
		for (int i = 0; i < origianlData.size(); i++) {
			if (origianlData.get(i).get("base_ne_id") != null) {
				formatData.add(origianlData.get(i));
			} else {
				// 当电路完整的时候赋值
				if (i > 0 && (i + 1) < origianlData.size()) {
					formatData.add(origianlData.get(i));
				}
			}
		}
		

		if (formatData.size() > 2) {
			// A端网元Id大于Z端
			if (Integer
					.parseInt(formatData.get(0).get("base_ne_id").toString()) > Integer
					.parseInt(formatData.get(formatData.size() - 1)
							.get("base_ne_id").toString())) {
				for (int j = formatData.size() - 1; j >= 0; j--) {
					if (formatData.get(j).get("base_ne_id") == null) {
						if (formatData.get(j).get("A_END_PTP") != null
								&& formatData.get(j).get("Z_END_PTP") != null) {
							Object o = formatData.get(j).get("A_END_PTP");
							formatData.get(j).put("A_END_PTP",
									formatData.get(j).get("Z_END_PTP"));
							formatData.get(j).put("Z_END_PTP", o);
						}
					}
					data.add(formatData.get(j));
				}
			} else {
				data = formatData;
			}
		} else {
			data = formatData;
		}
		return data;
	}

	/**
	 * 获取以太网子电路清单
	 */
	public Map<String, Object> getSubCircuitInfo(int parentCir)
			throws CommonException {
		// 根据父电路的id号查询出该电路下所有子电路信息
		// 定义map变量subCir用来存储查询的子电路a、z端的时隙信息
		Map map = new HashMap();
		map.put("ctp_table", "T_base_sdh_ctp");
		String actp_value = "concat(ctp1.CTP_J_ORIGINAL,'-',ctp1.CTP_K,'-',ctp1.CTP_L,'-',ctp1.CTP_M)";
		String zctp_value = "concat(ctp2.CTP_J_ORIGINAL,'-',ctp2.CTP_K,'-',ctp2.CTP_L,'-',ctp2.CTP_M)";
		map.put("CTP_ID", "base_sdh_CTP_ID");
		map.put("A_END_CTP_VALUE", actp_value);
		map.put("Z_END_CTP_VALUE", zctp_value);
		map.put("cir_info_table", "t_cir_circuit_info");
		map.put("cir_table", "t_cir_circuit");
		map.put("cir_info_id", "CIR_CIRCUIT_INFO_ID");
		map.put("cir_id", "CIR_CIRCUIT_ID");
		map.put("parentCir", parentCir);
		List<Map> subCir = circuitManagerMapper.getSubCircuit(map);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rows", subCir);
		result.put("total", subCir.size());
		result.put("isFirstTopo", "no");
		return result;
	}

	/**
	 * 获取设备名称
	 */
	public Map<String, Object> getEquipmentName(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		if ((Integer) map.get("nodeLevel") == 4
				|| (Integer) map.get("nodeLevel") == 5) {
			if ((Integer) map.get("nodeLevel") == 4) {
				map.put("tableName", "t_base_ne");
				map.put("field", "base_ne_id");
			}
			if ((Integer) map.get("nodeLevel") == 5) {
				map.put("tableName", "t_base_shelf");
				map.put("field", "base_shelf_id");
			}
			result = circuitManagerMapper.getNeName(map);
		}
		if ((Integer) map.get("nodeLevel") == 8) {
			result = circuitManagerMapper.getPortName(map);
		}
		if ((Integer) map.get("nodeLevel") == 6
				|| (Integer) map.get("nodeLevel") == 7) {
			if ((Integer) map.get("nodeLevel") == 6) {
				map.put("tableName", "t_base_unit");
				map.put("field", "base_unit_id");
			}
			if ((Integer) map.get("nodeLevel") == 5) {
				map.put("tableName", "t_base_sub_unit");
				map.put("field", "base_sub_unit_id");
			}
			result = circuitManagerMapper.getUnitName(map);
		}

		return result;
	}

	/**
	 * 电路相关性查询
	 */
	public Map<String, Object> selectCircuitAbout(Map map)
			throws CommonException {

		// result用来返回查询结果和总数
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (map.get("clientName") != null) {
				String clientName;
				clientName = URLDecoder.decode(
						map.get("clientName").toString(), "UTF-8");

				map.remove("clientName");
				map.put("clientName", clientName);
			}
			if (map.get("circuitName") != null) {
				String circuitName = URLDecoder.decode(map.get("circuitName")
						.toString(), "UTF-8");
				map.remove("circuitName");
				map.put("circuitName", circuitName);
			}
			if (map.get("advancedCon") != null) {
				String advancedCon = URLDecoder.decode(map.get("advancedCon")
						.toString(), "UTF-8");
				map.remove("advancedCon");
				map.put("advancedCon", advancedCon);
			}
			if (map.get("systemSourceNo") != null) {
				String systemSourceNo = URLDecoder.decode(map.get("systemSourceNo")
						.toString(), "UTF-8");
				map.remove("systemSourceNo");
				map.put("systemSourceNo", systemSourceNo);
			}
			if (map.get("useFor") != null) {
				String useFor = URLDecoder.decode(map.get("useFor").toString(),
						"UTF-8");
				map.remove("useFor");
				map.put("useFor", useFor);
			}
			if (map.get("connectRate") != null) {
				String connectRate = URLDecoder.decode(map.get("connectRate").toString(),
						"UTF-8");
				map.remove("connectRate");
				map.put("connectRate", connectRate);
			}
			
			int serviceType = Integer.parseInt(map.get("serviceType")
					.toString());
			// 资源管理条件转化
			// A端地点ID
			int aLocationLevel = Integer.parseInt(map.get("aLocationLevel")
					.toString());
			// A端地点级别：局站或机房
			int aLocationId = Integer.parseInt(map.get("aLocationId")
					.toString());
			if (aLocationId != -1) {// A端地点选择不为空
				List room = new ArrayList();
				room.add(0);
				if (aLocationLevel == AreaManagerImpl.AreaDef.LEVEL.STATION) {// 选择局站
					// 将局站转化成机房
					Map select = new HashMap();
					select.put("NAME", "t_resource_room");
					select.put("VALUE", "*");
					select.put("ID_NAME", "RESOURCE_STATION_ID");
					select.put("ID_VALUE", aLocationId);
					
					List<Map> listRoom = circuitManagerMapper.getByParameter(select);
					if(listRoom!=null && listRoom.size()>0){
						for(int a = 0;a<listRoom.size();a++){
							room.add(listRoom.get(a).get("RESOURCE_ROOM_ID"));
						}
					}
//					map.put("aLocationName", "RESOURCE_STATION_ID");
//					map.put("aLocationId", aLocationId);
				}
				if (aLocationLevel == AreaManagerImpl.AreaDef.LEVEL.ROOM) {// 选择机房
					//map.put("aLocationName", "RESOURCE_ROOM_ID");
					//map.put("aLocationId", aLocationId);
					room.add(aLocationId);
				}
				map.put("aLocationLevel", AreaManagerImpl.AreaDef.LEVEL.ROOM);
				map.put("roomList", room);
			}
			
			// 将传来的string转化成string数组；
			if (map.get("nodes") != null) {
				String[] nodes = map.get("nodes").toString().split("/");
				// nodeList用来放查询设备的id，将string转化成int
				
				if (nodes.length > 0 && !nodes[0].isEmpty()) {
					List nodeList = new ArrayList();
					for (int i = 0; i < nodes.length; i++) {
						nodeList.add(Integer.parseInt(nodes[i]));
					}
					String select_id = null;
					int tag = -1;// 用来区分，不同等级设备的查询方式1：网元分组到网元2：机架到端口
					// 如果勾选的是网管分组一级,在网管表中以网管分组为条件查询出所有网管id
					if (map.get("nodeLevel").toString().equals("1")) {
						tag = 1;
						select_id = "ems.BASE_EMS_GROUP_ID";
					}
					if (map.get("nodeLevel").toString().equals("2")) {
						tag = 1;
						select_id = "crs.BASE_EMS_CONNECTION_ID";
					}
					if (map.get("nodeLevel").toString().equals("3")) {
						tag = 1;
						select_id = "ne.BASE_SUBNET_ID";
						if(serviceType == 4){
							select_id = "crs.BASE_SUBNET_ID";
						}
					}
					if (map.get("nodeLevel").toString().equals("4")) {
						tag = 1;
						select_id = "crs.BASE_NE_ID";
					}
					if (map.get("nodeLevel").toString().equals("5")) {
						tag = 2;
						select_id = "BASE_SHELF_ID";
					}
					if (map.get("nodeLevel").toString().equals("6")) {
						tag = 2;
						select_id = "BASE_UNIT_ID";
					}
					if (map.get("nodeLevel").toString().equals("7")) {
						tag = 2;
						select_id = "BASE_SUB_UNIT_ID";
					}
					if (map.get("nodeLevel").toString().equals("8")) {
						tag = 2;
						select_id = "BASE_PTP_ID";
					}
					map.put("select_id", select_id);
					map.put("nodeList", nodeList);
					map.put("tag", tag);
				}
			}
			
			CircuitDefineFactory fa = new CircuitDefineFactory();
			CircuitDefine cons = fa.getCircuitDefineFactory(serviceType);
			map.put("CTP_ID", cons.ctpId);
			map.put("ctp_table", cons.ctpTable);
			map.put("A_END_CTP_VALUE", cons.actp);
			map.put("Z_END_CTP_VALUE", cons.zctp);
			map.put("cir_info_table", cons.cirInfoTable);
			map.put("cir_table", cons.cirTable);
			map.put("cir_info_id", cons.cirInfoId);
			map.put("cir_id", cons.cirId);
			map.put("route", cons.routeTable);
			map.put("crs", cons.crsTable);
			map.put("crs_id", cons.crsId);
			Map<String, Object> total = new HashMap<String, Object>();
			if(serviceType == 4){
				// ptn 相关性查询
				// 
				List<Map> ptnList = circuitManagerMapper.selectPtnCircuitAbout(map);
				total = circuitManagerMapper.selectPtnCircuitAboutCount(map);
				result.put("total", total.get("total"));
				result.put("rows", ptnList);
			}else{
				List<Map> ciucuits = circuitManagerMapper.selectCircuitAbout(map);
				total = circuitManagerMapper.circuitAboutTotal(map);
				result.put("total", total.get("total"));
				result.put("rows", ciucuits);
			}
			
		} catch (UnsupportedEncodingException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_UNENCODING);
		}
		return result;
	}

	/**
	 * 割接时，查询所有类型电路
	 */
	public Map<String, Object> selectAllCircuitAbout(Map map)
			throws CommonException {
		int limit = Integer.parseInt(map.get("limit").toString());
		Map<String, Object> result = new HashMap<String, Object>();
		int start = Integer.parseInt(map.get("start").toString());
		// 查询sdh电路总数
		map.put("serviceType", 1);
		setQueryCon(map);
		// t1表示sdh电路总个数
		int t1 = Integer.parseInt(circuitManagerMapper.circuitAboutTotal(map)
				.get("total").toString());
		// 查询以太网电路总数
		map.put("serviceType", 2);
		setQueryCon(map);
		// t2表示以太网电路总个数
		int t2 = Integer.parseInt(circuitManagerMapper.circuitAboutTotal(map)
				.get("total").toString());
		// 查询wdm电路总数
		map.put("serviceType", 3);
		setQueryCon(map);
		// t3表示wdm电路总个数
		int t3 = Integer.parseInt(circuitManagerMapper.circuitAboutTotal(map)
				.get("total").toString());
		// 计算查询电路总数，用于返回
		int total = t1 + t2 + t3;
		// 将电路总数写入返回对象
		result.put("total", total);
		// 根据start范围查询不同类型的电路
		int a = t1 / limit;
		int b = t2 / limit;
		int c = t3 / limit;
		// 查询sdh电路
		if (start >= 0 && start < a * limit) {
			map.put("serviceType", 1);
			setQueryCon(map);
			List<Map> ciucuits = circuitManagerMapper.selectCircuitAbout(map);
			result.put("rows", ciucuits);
		}
		// 查询以太网电路
		if (start >= a * limit && start < (a + b) * limit) {
			map.put("serviceType", 2);
			setQueryCon(map);
			map.put("start", start - a * limit);
			List<Map> ciucuits = circuitManagerMapper.selectCircuitAbout(map);
			result.put("rows", ciucuits);
		}
		// 查询wdm电路
		if (start >= (a + b) * limit && start < (a + b + c) * limit) {
			map.put("serviceType", 3);
			setQueryCon(map);
			map.put("start", start - a * limit - b * limit);
			List<Map> ciucuits = circuitManagerMapper.selectCircuitAbout(map);
			result.put("rows", ciucuits);
		}
		// 将sdh，wdm，以太网电路剩余查询结果全部放入一个list中
		if (start >= (a + b + c) * limit) {
			ArrayList<Map> al = new ArrayList<Map>();
			// 查询sdh电路
			map.put("serviceType", 1);
			setQueryCon(map);
			map.put("start", a * limit);
			al.addAll(circuitManagerMapper.selectCircuitAbout(map));
			// 查询以太网电路
			map.put("serviceType", 2);
			setQueryCon(map);
			map.put("start", b * limit);
			al.addAll(circuitManagerMapper.selectCircuitAbout(map));
			// 查询wdm电路
			map.put("serviceType", 3);
			setQueryCon(map);
			map.put("start", c * limit);
			al.addAll(circuitManagerMapper.selectCircuitAbout(map));
			// 根据start，和limit取list，若
			int tail = (start - limit * (a + b + c) + limit) > al.size() ? al
					.size() : (start - limit * (a + b + c) + limit);
			result.put("rows", al.subList(start - limit * (a + b + c), tail));
		}
		return result;
	}

	/**
	 * 设置相关性查询时的查询条件：包括要查询的表，要查询的字段名称，不同等级设备该如何查询。
	 * 
	 * @param map
	 */
	public void setQueryCon(Map map) {
		if (map.get("nodeList") != null) {
			String select_id = null;
			int tag = -1;// 用来区分，不同等级设备的查询方式1：网元分组到网元2：机架到端口
			// 如果勾选的是网管分组一级,在网管表中以网管分组为条件查询出所有网管id
			if (map.get("nodeLevel").toString().equals("1")) {
				tag = 1;
				select_id = "ems.BASE_EMS_GROUP_ID";
			}
			if (map.get("nodeLevel").toString().equals("2")) {
				tag = 1;
				select_id = "ne.BASE_EMS_CONNECTION_ID";
			}
			if (map.get("nodeLevel").toString().equals("3")) {
				tag = 1;
				select_id = "ne.BASE_SUBNET_ID";
			}
			if (map.get("nodeLevel").toString().equals("4")) {
				tag = 1;
				select_id = "ne.BASE_NE_ID";
			}
			if (map.get("nodeLevel").toString().equals("5")) {
				tag = 2;
				select_id = "BASE_SHELF_ID";
			}
			if (map.get("nodeLevel").toString().equals("6")) {
				tag = 2;
				select_id = "BASE_UNIT_ID";
			}
			if (map.get("nodeLevel").toString().equals("7")) {
				tag = 2;
				select_id = "BASE_SUB_UNIT_ID";
			}
			if (map.get("nodeLevel").toString().equals("8")) {
				tag = 2;
				select_id = "BASE_PTP_ID";
			}
			map.put("select_id", select_id);
			map.put("nodeList", map.get("nodeList"));
			map.put("tag", tag);
			// }
		}
		int serviceType = Integer.parseInt(map.get("serviceType").toString());
		CircuitDefineFactory fa = new CircuitDefineFactory();
		CircuitDefine cons = fa.getCircuitDefineFactory(serviceType);
		map.put("CTP_ID", cons.ctpId);
		map.put("ctp_table", cons.ctpTable);
		map.put("A_END_CTP_VALUE", cons.actp);
		map.put("Z_END_CTP_VALUE", cons.zctp);
		map.put("cir_info_table", cons.cirInfoTable);
		map.put("cir_table", cons.cirTable);
		map.put("cir_info_id", cons.cirInfoId);
		map.put("cir_id", cons.cirId);
		map.put("route", cons.routeTable);
		map.put("crs", cons.crsTable);
		map.put("crs_id", cons.crsId);
	}

	public Map<String, Object> exportLinks(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		if (map.get("tag").toString().equals("1")) {
			result = getExportLinks(map);
		} else
			result = getExportLinksByIds(map);
		return result;
	}
	public Map<String, Object> exportShowLinks(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		if (map.get("tag").toString().equals("1")) {
			result = selectLinks(map);
		} else
			result = getLinksByIds(map);
		return result;
	}

	/**
	 * 链路查询，用于导出
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getExportLinks(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> total = new HashMap<String, Object>();
		List emsIdList = new ArrayList();
		// 当节点是网元管的时候，根据用户id查询该用户有权查看的所有网管
		if (map.get("aNodeLevel") != null
				&& map.get("aNodeLevel").toString().equals("1")) {
			List<Map> emsInfoList = commonManagerService.getAllEmsByEmsGroupId(
					Integer.parseInt(map.get("userId").toString()),
					Integer.parseInt(map.get("aNodeId").toString()), false,false);
			// 如果该用户没有改网管分组下所有网管的查询权限，则结果为空
			if (emsInfoList.size() == 0) {
				map.put("aNodeLevel", -99);
			} else {// 如果有结果则将结果即带入查询条件查询
				for (Map ems : emsInfoList) {
					emsIdList.add(Integer.parseInt(ems.get(
							"BASE_EMS_CONNECTION_ID").toString()));
				}
				map.put("emsList", emsIdList);
			}
		}
		List<Map> linksInfo = circuitManagerMapper.getExportLinks(map);
		for (int i = 0; i < linksInfo.size(); i++) {
			linksInfo.get(i).put("No", i + 1);
		}
		// total = circuitManagerMapper.linksTotal(map);
		result.put("total", linksInfo.size());
		result.put("rows", linksInfo);
		return result;
	}

	/**
	 * 链路查询
	 */
	public Map<String, Object> selectLinks(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> total = new HashMap<String, Object>();
		List emsIdList = new ArrayList();
		// 当节点是网元管的时候，根据用户id查询该用户有权查看的所有网管
		if (map.get("aNodeLevel") != null
				&& map.get("aNodeLevel").toString().equals("1")) {
			List<Map> emsInfoList = commonManagerService.getAllEmsByEmsGroupId(
					Integer.parseInt(map.get("userId").toString()),
					Integer.parseInt(map.get("aNodeId").toString()), false, false);
			// 如果该用户没有改网管分组下所有网管的查询权限，则结果为空
			if (emsInfoList.size() == 0) {
				map.put("aNodeLevel", -99);
			} else {// 如果有结果则将结果即带入查询条件查询
				for (Map ems : emsInfoList) {
					emsIdList.add(Integer.parseInt(ems.get(
							"BASE_EMS_CONNECTION_ID").toString()));
				}
				map.put("emsList", emsIdList);
			}
		}
		List<Map> linksInfo = circuitManagerMapper.selectLinks(map);
		total = circuitManagerMapper.linksTotal(map);
		result.put("total", total.get("count(*)"));
		result.put("rows", linksInfo);
		return result;
	}

	/**
	 * 按链路Id查询，用于导出
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getExportLinksByIds(Map map)
			throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> con = new HashMap<String, Object>();
		if (map.get("jsonString") != null) {
			String[] link = map.get("jsonString").toString().split(",");
			ArrayList<Integer> linkList = new ArrayList<Integer>();
			try {
				for (String a : link) {
					linkList.add(Integer.parseInt(a));
				}
				con.put("links", linkList);
				List<Map> linksInfo = circuitManagerMapper
						.queryExportLinksById(con);
				for (int i = 0; i < linksInfo.size(); i++) {
					linksInfo.get(i).put("No", i + 1);
				}
				int total = linksInfo.size();
				result.put("total", total);
				result.put("rows", linksInfo);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new CommonException(e, 111);
			} catch (Exception e) {
				throw new CommonException(e, 111);
			}
		}
		return result;
	}

	/**
	 * 链路查询（按linkIds）
	 * 
	 */
	public Map<String, Object> getLinksByIds(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> con = new HashMap<String, Object>();
		if (map.get("jsonString") != null) {
			String[] link = map.get("jsonString").toString().split(",");
			ArrayList<Integer> linkList = new ArrayList<Integer>();
			try {
				for (String a : link) {
					linkList.add(Integer.parseInt(a));
				}
				con.put("links", linkList);
				con.put("linkType", map.get("linkType"));
				List<Map> linksInfo = circuitManagerMapper.queryLinksById(con);
				int total = linksInfo.size();
				result.put("total", total);
				result.put("rows", linksInfo);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new CommonException(e, 111);
			} catch (Exception e) {
				throw new CommonException(e, 111);
			}
		}
		return result;
	}

	/**
	 * toMap初始值:{"linkId":123,"direction":0} 删除链路
	 */
	public void deleteLinks(Map toMap) {
		Map update = new HashMap();
		
		update  = new HashMap();
		update.put("NAME", "t_base_link");
		update.put("ID_NAME", "CHANGE_STATE");
		update.put("ID_VALUE", CommonDefine.STATE_DELETE_LATEST);
		update.put("ID_NAME_2", "CHANGE_STATE");
		update.put("ID_VALUE_2", CommonDefine.STATE_DELETE_BEFORE);
		circuitManagerMapper.updateByParameter(update);
		
		Map map = new HashMap();
		List linkIds = new ArrayList();
		linkIds.add(toMap.get("linkId"));
		if (!toMap.get("direction").toString().isEmpty()
				&& Integer.parseInt(toMap.get("direction").toString()) == 1) {
			toMap.put("VALUE", "*");
			toMap.put("NAME", "t_base_link");
			toMap.put("ID_NAME", "BASE_LINK_ID");
			toMap.put("ID_VALUE", toMap.get("linkId"));
			List<Map> list = circuitManagerMapper.getByParameter(toMap);
			list.get(0).get("A_END_PTP");
			list.get(0).get("Z_END_PTP");
			update  = new HashMap();
			update.put("NAME", "t_base_ptp");
			update.put("ID_NAME", "BASE_PTP_ID");
			update.put("ID_VALUE", list.get(0).get("A_END_PTP"));
			update.put("ID_NAME_2", "PORT_TYPE");
			update.put("ID_VALUE_2", CommonDefine.PORT_TYPE_EDGE_POINT);
			circuitManagerMapper.updateByParameter(update);
			toMap.clear();
			toMap.put("NAME", "t_base_link");
			toMap.put("VALUE", "BASE_LINK_ID");
			toMap.put("ID_NAME", "A_END_PTP");
			toMap.put("ID_VALUE", list.get(0).get("Z_END_PTP"));
			toMap.put("ID_NAME_2", "Z_END_PTP");
			toMap.put("ID_VALUE_2", list.get(0).get("A_END_PTP"));
			toMap.put("ID_NAME_3", "IS_DEL");
			toMap.put("ID_VALUE_3", CommonDefine.FALSE);
			if (circuitManagerMapper.getByParameter(toMap).size() > 0) {
				linkIds.add(circuitManagerMapper.getByParameter(toMap).get(0)
						.get("BASE_LINK_ID"));
				update  = new HashMap();
				update.put("NAME", "t_base_ptp");
				update.put("ID_NAME", "BASE_PTP_ID");
				update.put("ID_VALUE", list.get(0).get("Z_END_PTP"));
				update.put("ID_NAME_2", "PORT_TYPE");
				update.put("ID_VALUE_2", CommonDefine.PORT_TYPE_EDGE_POINT);
				circuitManagerMapper.updateByParameter(update);
			}
		}
		map.put("linkIds", linkIds);
		circuitManagerMapper.deleteLinks(map);
		
		deleteCirByLinkDelete();
	}

	/**
	 * 新增链路 map中包括、A/Z端的端口、网元、网管信息、链路名称、链路方向信息（A(Z)_END_PTP/A(Z)_END_EMS/A(Z)
	 * _END_NE/ linkName/direction） 添加或修改电路
	 */
	public Map<String, Object> manageLink(Map map) throws CommonException {
		// 定义result用来返回执行结果
		Map<String, Object> result = new HashMap();
		Map update = null;
		// 判断端口是否有链路；
		Map hasLink = new HashMap();
		Map hasLink_Z = new HashMap();
		try {
			// 更新链路信息
			if (map.get("linkId") != null) {
				// 如果AZ端口没有改变，那么直修改电路显示名称
				if ((Integer) map.get("A_END_PTP") == -1
						&& (Integer) map.get("Z_END_PTP") == -1) {
					// 调用更新单条链路的方法
					circuitManagerMapper.updateSingleLink(map);
					result.put("returnResult", CommonDefine.SUCCESS);
				} else {
					result.put("returnResult", CommonDefine.FAILED);
				}
				// 新增链路
			} else if ((Integer) map.get("A_END_PTP") == -1
					|| (Integer) map.get("Z_END_PTP") == -1) {// 两端信息不全
				result.put("returnResult", CommonDefine.FAILED);
				result.put("returnMessage", "请选择端口！");
			} else if (((Integer) map.get("A_END_PTP"))// 判断两端是否相等
					.equals((Integer) map.get("Z_END_PTP"))) {
				result.put("returnResult", CommonDefine.FAILED);
				result.put("returnMessage", "A/Z端端口重复!");
			} else {
				// 判断需要新增链路的端口上是否已经存在链路
				map.put("end_PTP", map.get("A_END_PTP"));
				hasLink = circuitManagerMapper.hasLinkOnPtp(map);
				map.put("end_PTP", map.get("Z_END_PTP"));
				hasLink_Z = circuitManagerMapper.hasLinkOnPtp(map);
				if ((Long) hasLink.get("count(*)") > 0
						|| (Long) hasLink_Z.get("count(*)") > 0) {
					if ((Long) hasLink.get("count(*)") > 0
							&& (Long) hasLink_Z.get("count(*)") > 0) {
						result.put("returnResult", CommonDefine.FAILED);
						result.put("returnMessage", "无法新增，A、Z端端口均已被占用");
					} else if ((Long) hasLink.get("count(*)") > 0) {
						result.put("returnResult", CommonDefine.FAILED);
						result.put("returnMessage", "无法新增，A端端口已被占用！");
					} else {
						result.put("returnResult", CommonDefine.FAILED);
						result.put("returnMessage", "无法新增，Z端端口已被占用！");
					}
				} else {
					update  = new HashMap();
					update.put("NAME", "t_base_link");
					update.put("ID_NAME", "CHANGE_STATE");
					update.put("ID_VALUE", CommonDefine.STATE_ADD_LATEST);
					update.put("ID_NAME_2", "CHANGE_STATE");
					update.put("ID_VALUE_2", CommonDefine.STATE_ADD_BEFORE);
					circuitManagerMapper.updateByParameter(update);
					
					if (map.get("A_END_NE").toString()
							.equals(map.get("Z_END_NE").toString())) {
						// 如果A、Z端网元相等，将链路类型标为“内部” 
						if("1".equals(map.get("linkType").toString())){
							result.put("returnResult", CommonDefine.FAILED);
							result.put("returnMessage", "A端和Z端不能是同一网元！");
							return result;
						}else{
							map.put("LINK_TYPE", 2); 	
						}
					} else{
						if("2".equals(map.get("linkType").toString())){ 
							result.put("returnResult", CommonDefine.FAILED);
							result.put("returnMessage", "A端和Z端不能是同一网元！");
							return result;
						}else{
							map.put("LINK_TYPE", 1);// 如果不等，则标为“外部”
						}
					}

					// 将电路标为手动生成
					map.put("IS_MANUAL", 1);
					map.put("DISPLAY_NAME", map.get("nativeLinkName"));
					map.put("IS_MAIN", 1);
					circuitManagerMapper.insertSingleLink(map);
					update  = new HashMap();
					update.put("NAME", "t_base_ptp");
					update.put("ID_NAME", "BASE_PTP_ID");
					update.put("ID_VALUE", map.get("A_END_PTP"));
					update.put("ID_NAME_2", "PORT_TYPE");
					update.put("ID_VALUE_2", CommonDefine.PORT_TYPE_LINK_POINT);
					circuitManagerMapper.updateByParameter(update);
					
					// 如果方向为双向
					if (Integer.parseInt(map.get("direction").toString()) == 1) {
						// 将A/Z端端口、网元、网管信息调换再次入库，IS_MAIN修改为0（副电路），其他信息不变
						Map directionMap = new HashMap();
						directionMap.put("IS_MANUAL", 1);
						directionMap.put("IS_MAIN", 0);
						directionMap.put("direction", 1);
						directionMap.put("A_END_PTP", map.get("Z_END_PTP"));
						directionMap.put("Z_END_PTP", map.get("A_END_PTP"));
						directionMap.put("A_END_NE", map.get("Z_END_NE"));
						directionMap.put("Z_END_NE", map.get("A_END_NE"));
						directionMap.put("A_END_EMS", map.get("Z_END_EMS"));
						directionMap.put("Z_END_EMS", map.get("A_END_EMS"));
						directionMap.put("LINK_TYPE", map.get("LINK_TYPE"));
						directionMap.put("nativeLinkName", map.get("nativeLinkName"));
						directionMap.put("userLabel", map.get("userLabel"));
						directionMap.put("DISPLAY_NAME", map.get("nativeLinkName"));
						directionMap.put("islocked", map.get("islocked"));
						circuitManagerMapper.insertSingleLink(directionMap);
						
						update  = new HashMap();
						update.put("NAME", "t_base_ptp");
						update.put("ID_NAME", "BASE_PTP_ID");
						update.put("ID_VALUE", map.get("Z_END_PTP"));
						update.put("ID_NAME_2", "PORT_TYPE");
						update.put("ID_VALUE_2", CommonDefine.PORT_TYPE_LINK_POINT);
						circuitManagerMapper.updateByParameter(update);
					}
					// 新增link，回退部分电路
					deleteCirByLinkAdd();
					result.put("returnResult", CommonDefine.SUCCESS);
				}
			}
		} catch (Exception e) {
			result.put("returnResult", CommonDefine.FAILED);
			result.put("returnMessage", "数据库异常");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据电路号查询该电路号下的所有电路id
	 */
	public Map<String, Object> getCircuitBycircuitNo(Map map)
			throws CommonException {
		Map<String, Object> result = new HashMap();
		List<Map> list = circuitManagerMapper.getCircuitBycircuitNo(map);
		List rows = new ArrayList();
		int i = 0;
		for (Map cirId : list) {
			i++;
			Map name = new HashMap();
			name.put("circuitId", cirId.get("CIR_CIRCUIT_ID"));
			name.put("displayname", "路径-" + i);
			rows.add(name);
		}
		result.put("total", list.size());
		result.put("rows", rows);
		return result;
	}

	/**
	 * 根据电路号查询该电路号下的所有电路id
	 */
	public Map<String, Object> getPtnCircuitBycircuitNo(Map map)
			throws CommonException {
		Map<String, Object> result = new HashMap();
		List<Map> list = circuitManagerMapper.getPtnCircuitBycircuitNo(map);
		List rows = new ArrayList();
		int i = 0;
		for (Map cirId : list) {
			i++;
			Map name = new HashMap();
			name.put("circuitId", cirId.get("CIR_CIRCUIT_ID"));
			name.put("displayname", "路径-" + i);
			rows.add(name);
		}
		result.put("total", list.size());
		result.put("rows", rows);
		return result;
	}
	
	/**
	 * 根据电路号查询该otn电路号下的所有电路id
	 */
	public Map<String, Object> getOtnCircuitBycircuitNo(Map map)
			throws CommonException {
		Map<String, Object> result = new HashMap();
		List<Map> list = circuitManagerMapper.getOtnCircuitBycircuitNo(map);
		List rows = new ArrayList();
		int i = 0;
		for (Map cirId : list) {
			i++;
			Map name = new HashMap();
			name.put("circuitId", cirId.get("CIR_OTN_CIRCUIT_ID"));
			name.put("displayname", "路径-" + i);
			rows.add(name);
		}
		result.put("total", list.size());
		result.put("rows", rows);
		return result;
	}

	/**
	 * 将链路信息的.xls文件上传至服务器并导入
	 */
	public Map<String, Object> importLinksExcel(File file, String fileName,
			String uploadPath) throws CommonException {
		String result = null;
		Map<String, Object> map = new HashMap();
		Boolean isUpload = new Boolean(false);
		// 将本地的xls文件上传至服务器指定位置
		try {
			isUpload = commonManagerService.uploadFile(file, fileName,
					uploadPath);

		} catch (FileNotFoundException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (CommonException e) {
			throw e;
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		}
		if (isUpload) {
			// ReadEXCEL reader = new ReadEXCEL();
			File excelFile = new File(uploadPath + "/" + fileName);
			if (fileName.endsWith(".xls")) {
				result = readExcelFileForLink(file, 0, false);
			} else {
				result = readExcelFileForLink(file, 0, true);
			}
		}
		map.put("result", result);
		return map;
	}

	// 获取Excelcell中值
	public String getCell(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case 1:
			return cell.getStringCellValue();
		case 0:
			return String.valueOf((int) cell.getNumericCellValue());
		default:
			return "";

		}
	}

	public String readExcelFileForLink(File file, int sheetNumber,
			boolean isXlsx) throws CommonException {
		// 定义excel表每一行的长度
		int rowLength = 15;
		String result = "";
		Workbook workbook = null;
		InputStream in = null;
		// ip验证正则表达式
		// Pattern pattern = Pattern
		// .compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		try {
			in = new FileInputStream(file);
			if (isXlsx) {
				workbook = new XSSFWorkbook(in);
			} else {
				workbook = new HSSFWorkbook(in);
			}

			Sheet sheet = workbook.getSheetAt(sheetNumber);
			int rowCount = sheet.getPhysicalNumberOfRows();
			// 存放“网管-网元-机架号-子架号-槽道号-端口号”的拼接；

			List infoList = new ArrayList();

			// 统计一共有多少条有效的link记录（防止列excel中出现空行）
			int recordCount = 0;
			if (rowCount < 2) {
				result = "文件记录为空";
				return result;
			}
			// 先用一个for循环判断excel中数据在格式上是否有问题
			for (int i = 1; i < rowCount; i++) {
				// 如果有空行，直接进入进入下一次循环

				if (sheet.getRow(i) == null
						|| sheet.getRow(i).getLastCellNum() == 0) {
					recordCount++;
					continue;
				}
				// 除了空行以外，如果第n行的记录长度不等于规定的长度，则提示“第n行长度不正确”
				if (sheet.getRow(i).getLastCellNum() != rowLength) {
					result = "导入文件第" + i + "条错误";
					return result;
				}

				// 将第i行的第k列记录提取出来，判断是否为空（序号和链路名称这两列无须判断，链路名称如果为空则用两端端口名称代替）
				// 从第3列开始判断所以k=2开始循环；
				for (int k = 2; k < rowLength; k++) {
					// 从第3列开始，每一列不为空时直接进入下一次循环
					if (!getCell(sheet.getRow(i).getCell(k)).equals("")) {
						continue;
					} else {
						result = "导入数据第" + (i + 1) + "行第" + (k + 1) + "列为空";
						return result;
					}
				}
				recordCount++;
			}

			// 当excel中数据在格式上符合要求后，开始判断数据内容是否正确(共三步)；
			// 1、判断excel数据中端口是否有重复（一个端口上只能有一个链路，所以批量导入的链路的端口不能重复）
			// 使用set对象添加端口，由于set能自动去除重复的元素所以当添加一个端口set的size（）不变化则判断端口出现重复；
			Set ptpName_Set = new HashSet();
			List ptpName_List = new ArrayList();
			// 记录链路名称和方向信息；
			List<Map> otherInfo_List = new ArrayList();
			// 定义变量size检测添加端口后set对象大小是否变化即判断端口是否重复
			int size = 1;
			// 记录从第二行开始
			for (int i = 1; i < rowCount; i++) {
				// 如果有空行，直接进入进入下一次循环
				if (sheet.getRow(i) == null
						|| sheet.getRow(i).getLastCellNum() == 0) {
					recordCount++;
					continue;
				}
				String nameA = getCell(sheet.getRow(i).getCell(2)) + "-"
						+ getCell(sheet.getRow(i).getCell(3)) + "-"
						+ getCell(sheet.getRow(i).getCell(4)) + "-"
						+ getCell(sheet.getRow(i).getCell(5)) + "-"
						+ getCell(sheet.getRow(i).getCell(6)) + "-"
						+ getCell(sheet.getRow(i).getCell(7));

				ptpName_Set.add(nameA);
				ptpName_List.add(nameA);
				if (ptpName_Set.size() != size) {
					result = "第" + (i + 1) + "行A端端口出现重复";
					return result;
				} else
					size++;
				String nameZ = getCell(sheet.getRow(i).getCell(8)) + "-"
						+ getCell(sheet.getRow(i).getCell(9)) + "-"
						+ getCell(sheet.getRow(i).getCell(10)) + "-"
						+ getCell(sheet.getRow(i).getCell(11)) + "-"
						+ getCell(sheet.getRow(i).getCell(12)) + "-"
						+ getCell(sheet.getRow(i).getCell(13));
				ptpName_Set.add(nameZ);
				ptpName_List.add(nameZ);
				Map otherInfo_Map = new HashMap();
				otherInfo_Map.put("linkName", getCell(sheet.getRow(i)
						.getCell(1)));
				otherInfo_Map.put("direction",
						getCell(sheet.getRow(i).getCell(14)));
				otherInfo_List.add(otherInfo_Map);
				if (ptpName_Set.size() != size) {
					result = "第" + (i + 1) + "行Z端端口出现重复";
					return result;
				} else
					size++;
			}

			// 2、判断端口是否存在于数据库中
			// 声明Map对象存放端口名称,Map对象的键包括（“name”）；
			Map ptpNameMap = new HashMap();
			ptpNameMap.put("name", ptpName_List);
			/*
			 * queryResult获取数据库返回结果,返回值包括（Name（ptpName值）,BASE_PTP_ID（数据库中端口的id）,
			 * BASE_NE_ID（数据库中端口的网元id）,BASE_EMS_CONNECTION_ID（数据库中端口的网管id））
			 */
			List<Map> queryResult = circuitManagerMapper
					.getLinksInfo(ptpNameMap);
			if (queryResult.size() == 0) {
				result = "所有端口均不存在！";
				return result;
			}
			// 判断是否所有的端口都存在，如果有端口不存在于数据库中，则返回端口所在行数有错误的提示，
			if (queryResult.size() != ptpName_List.size()) {
				for (int i = 0; i < queryResult.size(); i++) {
					if (!ptpName_List.get(i).toString()
							.equals(queryResult.get(i).get("Name").toString())) {
						if (i % 2 == 0)
							result = "第" + (i / 2 + 1) + "条数据A端口不存在";
						else
							result = "第" + (i / 2 + 1) + "条数据Z端口不存在";
						return result;
					}
				}
			}
			// 3、判断t_base_link表中的端口是否和需要插入的端口冲突
			List ptpIdList = new ArrayList();
			for (Map map : queryResult) {
				ptpIdList.add(map.get("BASE_PTP_ID"));
			}
			Map ptpIdMap = new HashMap();
			ptpIdMap.put("end_PTP", ptpIdList);
			Map hasLink = circuitManagerMapper.hasLinkOnPtps(ptpIdMap);
			if ((Long) hasLink.get("count(*)") != 0) {
				for (int i = 0; i < ptpIdList.size(); i++) {
					HashMap mapHasLink = new HashMap();
					mapHasLink.put("end_PTP", ptpIdList.get(i));
					hasLink = circuitManagerMapper.hasLinkOnPtp(mapHasLink);
					if (!hasLink.get("count(*)").toString().equals("0")) {
						if (i % 2 == 0)
							result = "第" + (i / 2 + 1) + "行A端端口被占用";
						else {
							result = "第" + (i / 2 + 1) + "行Z端端口被占用";
						}
						return result;
					}
				}

			}

			// 检查结束，组织插入信息
			// Map对象键包括（DISPLAY_NAME,DIRECTION,A_EMS_CONNECTION_ID,A_NE_ID,A_END_PTP,
			// Z_EMS_CONNECTION_ID,Z_NE_ID,Z_END_PTP,LINK_TYPE,CHANGE_STATE,IS_MANUAL,IS_MAIN,IS_DEL,CREATE_TIME）
			Map linkInfo = new HashMap();
			int j = 0;
			for (int i = 0; i < otherInfo_List.size(); i++) {
				if (!otherInfo_List.get(i).get("linkName").toString()
						.equals("")) {
					linkInfo.put("linkName",
							otherInfo_List.get(i).get("linkName").toString());
				} else
					linkInfo.put("linkName", queryResult.get(j)
							.get("PORT_DESC")
							+ "="
							+ queryResult.get(j + 1).get("PORT_DESC"));
				if (otherInfo_List.get(i).get("direction").toString()
						.equals("双向"))
					linkInfo.put("direction", 1);
				else if (otherInfo_List.get(i).get("direction").toString()
						.equals("单向"))
					linkInfo.put("direction", 0);
				else {
					result = "第" + (i + 1) + "行方向设置有误（必须为”单向“或者“双向”）";
					return result;
				}

				linkInfo.put("A_END_EMS",
						queryResult.get(j).get("BASE_EMS_CONNECTION_ID"));
				linkInfo.put("A_END_NE", queryResult.get(j).get("BASE_NE_ID"));
				linkInfo.put("A_END_PTP", queryResult.get(j).get("BASE_PTP_ID"));
				linkInfo.put("Z_END_EMS",
						queryResult.get(j + 1).get("BASE_EMS_CONNECTION_ID"));
				linkInfo.put("Z_END_NE",
						queryResult.get(j + 1).get("BASE_NE_ID"));
				linkInfo.put("Z_END_PTP",
						queryResult.get(j + 1).get("BASE_PTP_ID"));
				if (queryResult
						.get(j + 1)
						.get("BASE_NE_ID")
						.toString()
						.equals(queryResult.get(j).get("BASE_NE_ID").toString()))
					// 内部链路
					linkInfo.put("LINK_TYPE", 2);
				// 外部链路
				else
					linkInfo.put("LINK_TYPE", 1);
				// 是最近一次新增
				linkInfo.put("CHANGE_STATE", 1);
				// 手动生成
				linkInfo.put("IS_MANUAL", 1);
				// 是否为主电路
				linkInfo.put("IS_MAIN", 1);
				// 是否删除
				linkInfo.put("IS_DEL", 1);
				// 创建时间
				linkInfo.put("CREATE_TIME", 1);
				circuitManagerMapper.insertSingleLink(linkInfo);
				j = j + 2;
			}
			in.close();
		} catch (FileNotFoundException e) {
			try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_NOTFOUND);
		} catch (IOException e) {
			try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);

		} catch (Exception e2) {
			e2.printStackTrace();
			return "未知错误";
		}

		return result;
	}

	/**
	 * 生成朗讯sdh设备的虚拟交叉,返回1表示新建成功，返回0表示失败
	 * 
	 * @return
	 */
	public int createVirtualCrs() {
		int result = 1;
		// 定义端口类型：“1”表示FTP类型端口
		final int portType = 1;
		// 定义数据库一次查询个数
		final int limit = 1000;
		// 定义初始查询开始的行数
		int size = 0;
		// 获取A/Z端均为FTP类型且FTP的值为cc的交叉连接
		Map map = new HashMap();
		// 设置开始查询的行数
		map.put("size", size);
		// 端口的类型
		map.put("portType", portType);
		// 一次查询的个数
		map.put("limit", limit);
		try {
			List<Map> crsList = circuitManagerMapper.getInnerCrs(map);
			while (crsList.size() > 0) {
				for (int i = 0; i < crsList.size(); i++) {
					List<Map> crsInfo = getCrsByInnerCrs(crsList.get(i));
					// 成功地查询到该交叉连接两端的交叉连接
					if (crsInfo.size() == 2) {
						// 对获取A端ctp
						Map virtualCrsA = getVirtualCrsCtp(crsInfo.get(0));
						// 获取Z端ctp
						Map virtualCrsZ = getVirtualCrsCtp(crsInfo.get(1));
						// 成功获取两端ctp后新建一条虚拟交叉连接
						if (virtualCrsA.size() == 1 && virtualCrsZ.size() == 1) {
							// circuitManagerMapper.insertVirtualCrs();
						}
					} else
						continue;
				}
				// 继续查找下1000行记录
				size = size + limit;
				map.put("size", size);
				crsList = circuitManagerMapper.getInnerCrs(map);
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}

	// 获取otn电路路由
	public Map<String, Object> getOtnCircuitRoute(int circuitId)
			throws CommonException {
		List<Map> origianlData = circuitManagerMapper
				.getOtnCircuitRoute(circuitId);
		// 定义routeInfo变量存放返回标准格式数据
		List<Map> routeInfo = new ArrayList();
		// 将origianlData中交叉连接信息格式化，返回字段：NE_NAME,EMS_NAME,CTP,CTP_TWO,PORT,PORT_TWO,LINK_NAME,LINK_NANE_TWO
		int j = 1;// list的计数，
		for (int i = 0; i < origianlData.size(); i++) {
			// 如果返回的是链路信息，则将链路名称填入网元名称中
			if (origianlData.get(i).get("LINK_NAME") != null) {
				Map temp_link = new HashMap();
				temp_link.put("PORT",
						"链路：" + origianlData.get(i).get("LINK_NAME"));
				temp_link.put(
						"PORT_TWO",
						origianlData.get(i).get("LINK_NAME_TWO") == null ? ""
								: "链路："+ origianlData.get(i).get("LINK_NAME_TWO"));
				routeInfo.add(temp_link);
			} else {// 将一组数据中的A.Z端信息拆成两组数据
				Map temp_a = new HashMap();
				temp_a.put("NE_NAME", origianlData.get(i).get("NE_NAME"));
				temp_a.put("EMS_NAME", origianlData.get(i).get("EMS_NAME"));
				temp_a.put("CTP", origianlData.get(i).get("A_END_CTP"));
				temp_a.put("CTP_TWO", origianlData.get(i).get("A_END_CTP_TWO"));
				temp_a.put("PORT", origianlData.get(i).get("A_END_PORT"));
				temp_a.put("PORT_TWO", origianlData.get(i)
						.get("A_END_PORT_TWO"));
				routeInfo.add(temp_a);
				Map temp_z = new HashMap();
				temp_z.put("NE_NAME", origianlData.get(i).get("NE_NAME"));
				temp_z.put("EMS_NAME", origianlData.get(i).get("EMS_NAME"));
				temp_z.put("CTP", origianlData.get(i).get("Z_END_CTP"));
				temp_z.put("CTP_TWO", origianlData.get(i).get("Z_END_CTP_TWO"));
				temp_z.put("PORT", origianlData.get(i).get("Z_END_PORT"));
				temp_z.put("PORT_TWO", origianlData.get(i)
						.get("Z_END_PORT_TWO"));
				routeInfo.add(temp_z);
			}
		}
		// 声明一个返回的map变量，其字段包括rows（路由详细信息）、total（记录总数）
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rows", routeInfo);
		result.put("total", routeInfo.size());
		return result;
	}

	// 获取otn电路路由
		public Map<String, Object> getPtnCircuitRoute(int circuitId)
				throws CommonException {
			List<Map> origianlData = circuitManagerMapper
					.getPtnCircuitRoute(circuitId);
			// 定义routeInfo变量存放返回标准格式数据
//			List<Map> routeInfo = new ArrayList();
//			// 将origianlData中交叉连接信息格式化，返回字段：NE_NAME,EMS_NAME,CTP,CTP_TWO,PORT,PORT_TWO,LINK_NAME,LINK_NANE_TWO
//			int j = 1;// list的计数，
//			for (int i = 0; i < origianlData.size(); i++) {
//				// 如果返回的是链路信息，则将链路名称填入网元名称中
//				if (origianlData.get(i).get("LINK_NAME") != null) {
//					Map temp_link = new HashMap();
//					temp_link.put("PORT",
//							"链路：" + origianlData.get(i).get("LINK_NAME"));
//					temp_link.put(
//							"PORT_TWO",
//							origianlData.get(i).get("LINK_NAME_TWO") == null ? ""
//									: "链路："+ origianlData.get(i).get("LINK_NAME_TWO"));
//					routeInfo.add(temp_link);
//				} else {// 将一组数据中的A.Z端信息拆成两组数据
//					Map temp_a = new HashMap();
//					temp_a.put("NE_NAME", origianlData.get(i).get("NE_NAME"));
//					temp_a.put("EMS_NAME", origianlData.get(i).get("EMS_NAME"));
//					temp_a.put("CTP", origianlData.get(i).get("A_END_CTP"));
//					temp_a.put("CTP_TWO", origianlData.get(i).get("A_END_CTP_TWO"));
//					temp_a.put("PORT", origianlData.get(i).get("A_END_PORT"));
//					temp_a.put("PORT_TWO", origianlData.get(i)
//							.get("A_END_PORT_TWO"));
//					routeInfo.add(temp_a);
//					Map temp_z = new HashMap();
//					temp_z.put("NE_NAME", origianlData.get(i).get("NE_NAME"));
//					temp_z.put("EMS_NAME", origianlData.get(i).get("EMS_NAME"));
//					temp_z.put("CTP", origianlData.get(i).get("Z_END_CTP"));
//					temp_z.put("CTP_TWO", origianlData.get(i).get("Z_END_CTP_TWO"));
//					temp_z.put("PORT", origianlData.get(i).get("Z_END_PORT"));
//					temp_z.put("PORT_TWO", origianlData.get(i)
//							.get("Z_END_PORT_TWO"));
//					routeInfo.add(temp_z);
//				}
//			}
			// 声明一个返回的map变量，其字段包括rows（路由详细信息）、total（记录总数）
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("rows", origianlData);
			result.put("total", origianlData.size());
			return result;
		}
	// 获取otn网元内部路由
	public Map getInnerRoute(Map map) throws CommonException {
		// 返回给action层的值其中result包括关键字：rows（记录）=formatData,total(记录总个数)=formatData.size()
		Map result = new HashMap();
		// Map中的游泳关键字为：NE_NAME(网元名称)、A_END_CTP(A端时隙),Z_END_CTP(Z端时隙),A_END_PORT（A端口）,Z_END_PORT(Z端口),
		// LINK_NAME（外部链路名称）,INNER_LINK_NAME（内部链路名称）,IS_COMPLETE(判断一条路由是否结束，即区分两条路由的标识)
		// CHAIN_TYPE(记录类型：2:交叉连接，3：内部链路，4：外部链路)
		List<Map> origianlData = new ArrayList();
		// 最后返回的标准格式的List，其中Map的关键字有：inner_NE_NAME（网元名称）,
		// inner_PORT（工作侧端口）,inner_CTP（工作侧时隙）,
		// inner_PORT_p（保护侧端口）,inner_CTP_p（保护侧时隙）为了显示方便将工作侧链路和保护侧链路分别写在工作侧端口和保护侧端口
		List<Map> formatData = new ArrayList();
		try {
			// 先从数据库中取回电路中指定网元内部的路由信息:
			origianlData = circuitManagerMapper.getInnerRoute(map);
			// 成功获取到内部路由
			if (origianlData.size() > 0) {
				// 工作侧的路由记录
				List<Map> workList = new ArrayList();
				// 保护侧的路由记录
				List<Map> protectList = new ArrayList();
				// 遍历List形成规范格式的数据
				int j = 0;
				// 将记录分为工作路由和保护路由
				for (int i = 0; i < origianlData.size(); i++) {
					if (origianlData.get(i).get("IS_COMPLETE") != null
							&& (Integer.parseInt(origianlData.get(i)
									.get("IS_COMPLETE").toString()) == 1)) {
						if (j == 0) {
							workList = origianlData.subList(j, i + 1);
							j = i;
						} else {
							protectList = origianlData.subList(j + 1, i + 1);
							break;
						}
					}
				}
				// 如果插叙结果不正确，则直接返回
				if (workList.size() + protectList.size() != origianlData.size()) {
					return result;
				}
				// 取workList.size()和protectList.size()较大的值作为formatData的size
				formatData = getformedInnerRoute(workList, protectList);
				result.put("total", formatData.size());
				result.put("rows", formatData);
				// int size = 0;
				// if (workList.size() > protectList.size()) {
				// size = workList.size();
				// } else
				// size = protectList.size();
				// // 将保护侧和工作侧的路由信息填入formatData中
				// for (int i = 0; i < size; i++) {
				// Map record=new HashMap();
				// record.put("inner_NE_NAME", workList.get(i).get("NE_NAME"));
				// record.put("inner_PORT", workList.get(i).get("A_END_PORT"));
				// record.put("inner_CTP", workList.get(i).get("A_END_PORT"));
				// record.put("inner_PORT_p",
				// protectList.get(i).get("A_END_CTP"));
				// record.put("inner_CTP_p",
				// protectList.get(i).get("A_END_CTP"));
				// formatData.add(record);
				// record.put("inner_NE_NAME", workList.get(i).get("NE_NAME"));
				// record.put("inner_PORT", workList.get(i).get("Z_END_PORT"));
				// record.put("inner_CTP", workList.get(i).get("Z_END_PORT"));
				// record.put("inner_PORT_p",
				// protectList.get(i).get("Z_END_CTP"));
				// record.put("inner_CTP_p",
				// protectList.get(i).get("Z_END_CTP"));
				// formatData.add(record);
				// }
			}
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		}
		return result;
	}

	/**
	 * 将工作侧路由和保护侧路由整合成一个List
	 * 
	 * @param workList
	 * @param protectList
	 * @return
	 */
	private List<Map> getformedInnerRoute(List<Map> workList,
			List<Map> protectList) {
		List<Map> formatData = new ArrayList();
		List<Map> formatWorkList = new ArrayList();
		List<Map> formatProtectList = new ArrayList();
		// formatWorkList
		formatWorkList = getFormatInfo(workList, 1);
		// formatProtectList
		formatProtectList = getFormatInfo(protectList, 2);
		int minsize = 0;
		int maxsize = 0;
		if (formatWorkList.size() > formatProtectList.size()) {
			minsize = formatProtectList.size();
			maxsize = formatWorkList.size();
		} else {
			minsize = formatWorkList.size();
			maxsize = formatProtectList.size();
		}
		if (formatWorkList.size() != 0 && formatProtectList.size() != 0) {
			int count = 0;
			for (int i = 0; i < minsize; i++) {
				Map map = new HashMap();
				map.put("inner_NE_NAME",
						formatWorkList.get(i).get("inner_NE_NAME") == null ? ""
								: formatWorkList.get(i).get("inner_NE_NAME"));
				map.put("inner_PORT",
						formatWorkList.get(i).get("inner_PORT") == null ? ""
								: formatWorkList.get(i).get("inner_PORT"));
				map.put("inner_CTP",
						formatWorkList.get(i).get("inner_CTP") == null ? ""
								: formatWorkList.get(i).get("inner_CTP"));
				map.put("inner_PORT_p",
						formatProtectList.get(i).get("inner_PORT_p") == null ? ""
								: formatProtectList.get(i).get("inner_PORT_p"));
				map.put("inner_CTP_p",
						formatProtectList.get(i).get("inner_CTP_p") == null ? ""
								: formatProtectList.get(i).get("inner_CTP_p"));
				formatData.add(map);
				count = i;
			}
			if (formatWorkList.size() == maxsize) {
				for (int j = count + 1; j < maxsize; j++) {
					formatData.add(formatWorkList.get(j));
				}
			} else {
				for (int j = count; count < maxsize; j++) {
					formatData.add(formatProtectList.get(j));
				}
			}
		} else if (formatWorkList.size() == 0) {
			formatData = formatProtectList;
		} else
			formatData = formatWorkList;
		return formatData;
	}

	// 整合两端信息
	private List<Map> getFormatInfo(List<Map> origianlList, int tag) {
		List<Map> formatList = new ArrayList();
		String port;
		String ctp;
		if (tag == 1) {
			port = "inner_PORT";
			ctp = "inner_CTP";
		} else {
			port = "inner_PORT_p";
			ctp = "inner_CTP_p";
		}
		if (origianlList.size() > 0
				&& (Integer.parseInt(origianlList.get(0).get("CHAIN_TYPE")
						.toString())) == 2) {
			for (int i = 0; i < origianlList.size(); i++) {
				if (Integer.parseInt(origianlList.get(i).get("CHAIN_TYPE")
						.toString()) == 2) {
					Map record = new HashMap();
					record.put("inner_NE_NAME",
							origianlList.get(i).get("NE_NAME"));
					record.put(port,
							origianlList.get(i).get("A_END_PORT") == null ? ""
									: origianlList.get(i).get("A_END_PORT"));
					record.put(ctp,
							origianlList.get(i).get("A_END_CTP") == null ? ""
									: origianlList.get(i).get("A_END_CTP"));
					formatList.add(record);
					Map recordZ = new HashMap();
					recordZ.put("inner_NE_NAME",
							origianlList.get(i).get("NE_NAME") == null ? ""
									: origianlList.get(i).get("NE_NAME"));
					recordZ.put(port,
							origianlList.get(i).get("Z_END_PORT") == null ? ""
									: origianlList.get(i).get("Z_END_PORT"));
					recordZ.put(ctp,
							origianlList.get(i).get("Z_END_CTP") == null ? ""
									: origianlList.get(i).get("Z_END_CTP"));
					formatList.add(record);
					if (Integer.parseInt(origianlList.get(i + 1)
							.get("CHAIN_TYPE").toString()) == 2) {
						Map recordS = new HashMap();
						recordS.put("inner_NE_NAME", "");
						recordS.put(port, "");
						recordS.put(ctp, "");
						formatList.add(recordS);
					} else {
						Map recordL = new HashMap();
						if (Integer.parseInt(origianlList.get(i + 1)
								.get("CHAIN_TYPE").toString()) == 3)
							recordL.put(
									port,
									"内部链路："
											+ origianlList.get(++i)
													.get("INNER_LINK_NAME")
													.toString());
						else if (Integer.parseInt(origianlList.get(i + 1)
								.get("CHAIN_TYPE").toString()) == 4)
							recordL.put(port, "外部链路："
									+ origianlList.get(++i).get("LINK_NAME")
											.toString());
						formatList.add(recordL);
					}
				}
			}
		}
		return formatList;
	}

	// 朗讯虚拟交叉：通过给定的内部交叉连接查询两端的交叉连接，如果查询成果查询成功，则返回
	private List<Map> getCrsByInnerCrs(Map innerCrs) {
		List<Map> crsInfo = new ArrayList();
		try {
			crsInfo = getCrsByInnerCrs(innerCrs);
			if (crsInfo.size() == 2) {
				return crsInfo;
			} else
				crsInfo.clear();
			return crsInfo;
		} catch (Exception e) {
			return crsInfo;
		}
	}

	// 获取虚拟交叉两端的ctp信息
	private Map getVirtualCrsCtp(Map crsInfo) {
		Map result = new HashMap();
		Map ma_con = new HashMap();
		// 判断两端的端口类型：有ctp信息?getPtpCtp():以太网?getEthCtp():getPdhCtp()
		if (crsInfo.get("Ctp") != null) {
			// 有时隙
			ma_con.put("BASE_PTP_ID",
					Integer.parseInt(crsInfo.get("Z_END_PTP").toString()));
			ma_con.put("CTP_J_ORIGINAL",
					Integer.parseInt(crsInfo.get("CTP_J_ORIGINAL").toString()));
			ma_con.put("CTP_K",
					Integer.parseInt(crsInfo.get("CTP_K").toString()));
			ma_con.put("CTP_L",
					Integer.parseInt(crsInfo.get("CTP_L").toString()));
			ma_con.put("CTP_M",
					Integer.parseInt(crsInfo.get("CTP_M").toString()));
			// 查询条件
			List<Map> list_ctp = circuitManagerMapper.getCtp(ma_con);
			if (list_ctp.size() > 0) {
				result = list_ctp.get(0);
			} else {
				insertNewCtp();
				list_ctp = circuitManagerMapper.getCtp(ma_con);
				result = list_ctp.get(0);
			}

		} else if (crsInfo.get("Type").equals("Eth")) {
			// 以太网
			ma_con.put("BASE_PTP_ID",
					Integer.parseInt(crsInfo.get("Z_END_PTP").toString()));
			ma_con.put("CTP_J_ORIGINAL",
					Integer.parseInt(crsInfo.get("CTP_J_ORIGINAL").toString()));
			ma_con.put("CTP_K",
					Integer.parseInt(crsInfo.get("CTP_K").toString()));
			ma_con.put("CTP_L",
					Integer.parseInt(crsInfo.get("CTP_L").toString()));
			ma_con.put("CTP_M",
					Integer.parseInt(crsInfo.get("CTP_M").toString()));
			List<Map> list_ctp = circuitManagerMapper.getCtp(ma_con);
			if (list_ctp.size() > 0) {
				result = list_ctp.get(0);
			} else {
				insertNewCtp();
				list_ctp = circuitManagerMapper.getCtp(ma_con);
				result = list_ctp.get(0);
			}
		} else {// pdh
			int k = Integer.parseInt(crsInfo.get("ctp_k").toString());
			int l = Integer.parseInt(crsInfo.get("ctp_l").toString());
			int m = Integer.parseInt(crsInfo.get("ctp_m").toString());
			// 计算端口号
			int PORT_NO = (k - 1) * 21 + (l - 1) * 3 + (m - 1);
			Map condition = new HashMap();
			condition.put("PORT_NO", PORT_NO);
			List<Map> list_ctp = circuitManagerMapper
					.getCtpIdByPortNo(condition);
			if (list_ctp.size() > 0) {
				result = list_ctp.get(0);
			}
		}
		return result;
	}

	public void insertNewCtp() {

	}

	// 定义CircuitDefineFactory类生成CircuitDefine对象
	protected class CircuitDefineFactory {
		CircuitDefine getCircuitDefineFactory(int type) {
			if (type == 1 || type == 2) {
				return CircuitDefine.SDH;
			}
			return CircuitDefine.OTN;
		}
	}
	@Override
	public String linksOnPageExport(Map map, Map<String, Object> toMap) throws CommonException{
		String resultMessage = "";
		String name = "";
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		int flag = CommonDefine.EXCEL.LINK_SHOW_EXPORT;
		int tag = CommonDefine.EXCEL.LINK_SHOW_EXPORT;
		String fileName = map.get("displayName").toString() + "_" + name + "_"
				+ formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		try {
			// 限制最多导出记录的个数
			map.put("limit", CommonDefine.EXCEL.MAX_EXPORT_SIZE);
			// 根据flag选择sql查询方法
			Map result = getExportResult(tag, map, toMap);
			if (result == null) {
				return resultMessage;
			}
			List<Map> rows = (List<Map>) result.get("rows");
			int total = Integer.parseInt(result.get("total").toString());
			// 将数据库中取到的数据加工成显示的数据，例如：IS_COMPLETE为1，则显示“完整”
			formData(flag, rows);
			// 导出数据
			resultMessage = ex2.writeExcel(rows, flag, false);
		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			return resultMessage;
		}
		return resultMessage;
	}
	// 导出文件
	public String exportExcel(Map map, Map toMap) throws CommonException {
		String resultMessage = "";
		String name = "";
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		int flag = -1;
		int tag = -1;
		if (map.get("flag") != null) {
			switch (Integer.parseInt(map.get("flag").toString())) {
			case 1: {
				flag = CommonDefine.EXCEL.PTP_EXPORT;
				tag = CommonDefine.EXCEL.PTP_EXPORT;
				name = "电路清单";
			}
				break;
			case 2: {
				flag = CommonDefine.EXCEL.ABOUTQUERY_EXPORT;
				tag = CommonDefine.EXCEL.ABOUTQUERY_EXPORT;
				name = "电路清单";
			}
				break;
			case 3: {
				flag = CommonDefine.EXCEL.CROSSCONNECT_EXPORT;
				tag = CommonDefine.EXCEL.CROSSCONNECT_EXPORT;
				name = "交叉连接";
			}
				break;
			case 4: {
				flag = CommonDefine.EXCEL.SUB_CIRCUIT_EXPORT;
				tag = CommonDefine.EXCEL.SUB_CIRCUIT_EXPORT;
				name = "子电路清单_" + map.get("cirNo");
			}
				break;
			case 5: {
				flag = CommonDefine.EXCEL.LAST_CIRCUIT;
				tag = CommonDefine.EXCEL.LAST_CIRCUIT;
				name = "电路清单";
			}
				break;
			case 7: {
				flag = CommonDefine.EXCEL.ABOUTQUERY_EXPORT;
				tag = 7;
				name = "电路清单";
			}
				break;
			}
		} else {
			flag = CommonDefine.EXCEL.LINK_EXPORT;
			tag = CommonDefine.EXCEL.LINK_EXPORT;
		}
		String fileName = map.get("displayName").toString() + "_" + name + "_"
				+ formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		try {
			// 限制最多导出记录的个数
			map.put("limit", CommonDefine.EXCEL.MAX_EXPORT_SIZE);
			// 根据flag选择sql查询方法
			Map result = getExportResult(tag, map, toMap);
			if (result == null) {
				return resultMessage;
			}
			List<Map> rows = (List<Map>) result.get("rows");
			int total = Integer.parseInt(result.get("total").toString());
			// 将数据库中取到的数据加工成显示的数据，例如：IS_COMPLETE为1，则显示“完整”
			formData(flag, rows);
			// 导出数据
			resultMessage = ex2.writeExcel(rows, flag, false);
		} catch (Exception e) {
			e.printStackTrace();
			ex2.close();
			return resultMessage;
		}
		return resultMessage;
	}

	// 导出路由表——>excel
	public String exportRoute(Map map) {
		String resultMessage = "";
		map.put("vCircuit", map.get("cirNo"));
		// 定义导出存放路径
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
		try {
			List<Map> circuitList;
			// 查询该电路编号下所有的电路id
			if(map.get("serviceType").toString().equals("3")){
				circuitList = (List<Map>) getOtnCircuitBycircuitNo(map).get(
						"rows");
			}
			else{
				circuitList = (List<Map>) getCircuitBycircuitNo(map).get("rows");
			}
			// circuitIds用来存放电路Id
			List<Integer> circuitIds = new ArrayList();
			// 循环赋值；
			for (Map ids : circuitList) {
				circuitIds.add(Integer
						.parseInt(ids.get("circuitId").toString()));
			}
			// 如果没有电路id则不导出
			if (circuitIds.size() == 0) {
				return resultMessage;
			}
			Map infoMap = map;
			infoMap.put("vCircuit", circuitIds.get(0));
			List<Map> info;
			if(map.get("serviceType").toString().equals("3")){
				info= (List<Map>) getOtnCirInfoById(infoMap).get("rows");
			}else{
				info=(List<Map>) getCirInfoById(infoMap).get("rows");
			}
			formData(CommonDefine.EXCEL.SDH_INFO, info);
			String routeName = "电路路由";
			if (info.size() > 0 && info.get(0).get("cir_name") != null) {
				routeName = info.get(0).get("cir_name").toString();
			}
			String fileName = map.get("cirNo").toString() + "_" + routeName
					+ "_电路详情_"

					+ formatter.format(new Date(System.currentTimeMillis()));
			ExportExcelUtil ex2 = new ExportExcelUtil(path, fileName, 1000);
			// 输出路由信息
			ex2.writeExcel(info, CommonDefine.EXCEL.SDH_INFO, "路由信息",
					(short) 4000, true);
			// 如果存在一条路由信息
			for (int i = 0; i < circuitIds.size(); i++) {
				// 获取路由详细信息表；
				List<Map> rows;
				if(map.get("serviceType").toString().equals("3")){
					rows= (List<Map>) getOtnCircuitRoute(circuitIds.get(i)).get("rows");
				}else{
					rows=(List<Map>) getCircuitRoute(circuitIds.get(i)).get("rows");
				}
				if (i != circuitIds.size() - 1) {
					ex2.writeExcel(rows, CommonDefine.EXCEL.SDH_ROUTE, "路径"
							+ (i + 1), (short) 6000, true);
				} else {
					resultMessage = ex2.writeExcel(rows,
							CommonDefine.EXCEL.SDH_ROUTE, "路径" + (i + 1),
							(short) 6000, false);
				}
			}
			
		} catch (CommonException e) {
			e.printStackTrace();
			return resultMessage;
		}
		return resultMessage;
	}

	// 根据flag判断选择使用哪个service方法
	private Map getExportResult(int tag, Map map, Map toMap)
			throws CommonException {
		Map result = null;
		switch (tag) {
		case CommonDefine.EXCEL.PTP_EXPORT:
			result = getCircuitByPtp(map);
			break;
		case CommonDefine.EXCEL.ABOUTQUERY_EXPORT:
			result = selectCircuitAbout(map);
			break;
		case 7:
			result = getAboutCircuit(map);
			break;
		case CommonDefine.EXCEL.LINK_EXPORT:
			result = exportLinks(map);
			break;
		case CommonDefine.EXCEL.LINK_SHOW_EXPORT:
			result = exportShowLinks(map);
			break;
		case CommonDefine.EXCEL.CROSSCONNECT_EXPORT:
			result = selectCrossConnect(map, toMap);
			break;
		case CommonDefine.EXCEL.SUB_CIRCUIT_EXPORT:
			result = getSubCircuitInfo(Integer.parseInt(map.get("parentCir")
					.toString()));
			break;
		case CommonDefine.EXCEL.LAST_CIRCUIT: {
			int type = Integer.parseInt(map.get("type").toString());
			result = selectCircuitLast(type, 0,
					CommonDefine.EXCEL.MAX_EXPORT_SIZE);
		}
		}
		return result;
	}

	/** ********************************daihuijun**end************************ */
	// 定义数据库中数据k-v替换的规则
	private void formData(int flag, List<Map> rows) {
		if (flag == CommonDefine.EXCEL.LINK_EXPORT || flag== CommonDefine.EXCEL.LINK_SHOW_EXPORT) {
			transformData(rows, "DIRECTION", new String[] { "单向", "双向" });
			transformData(rows, "LINK_TYPE",
					new String[] { "", "内部链路", "外部链路" });
			transformData(rows, "IS_MANUAL", new String[] { "手动生成", "自动生成" });
			transformData(rows, "IS_CONFLICT", new String[] { "","冲突项", "冲突源" });
		}
		if (flag == CommonDefine.EXCEL.ABOUTQUERY_EXPORT
				|| flag == CommonDefine.EXCEL.PTP_EXPORT
				|| flag == CommonDefine.EXCEL.SUB_CIRCUIT_EXPORT
				|| flag == CommonDefine.EXCEL.SDH_INFO) {
			transformData(rows, "svc_type", new String[] { "", "SDH电路",
					"以太网电路", "WDM电路" });
			transformData(rows, "IS_COMPLETE_CIR", new String[] { "不完整", "完整" });
		}
		if (flag == CommonDefine.EXCEL.LAST_CIRCUIT) {
			transformData(rows, "SVC_TYPE", new String[] { "", "SDH电路",
					"以太网电路", "WDM电路" });
			transformData(rows, "IS_COMPLETE_CIR", new String[] { "不完整", "完整" });
		}
		if (flag == CommonDefine.EXCEL.CROSSCONNECT_EXPORT) {
			transformData(rows, "CHANGE_STATE", new String[] { "", "新增", "删除",
					"不变" });
			transformData(rows, "CIRCUIT_COUNT", new String[] { "离散", "正常",
					"正常", "正常" });
			transformData(rows, "IS_FIX", new String[] { "否", "是"});
		}
	}

	/**
	 * 
	 * @param data
	 *            从数据库中查询出来的结果
	 * @param cell
	 *            记录中字段名
	 * @param disName
	 *            需要替换成的值
	 * @return
	 */

	private boolean transformData(List<Map> data, String cell,
			String[] displayName) {
		if (displayName.length == 0 || data.size() == 0 || cell.equals("")) {
			return false;
		}
		for (Map data1 : data) {
			if (data1.get(cell) != null) {
				if (!("".equals(data1.get(cell).toString()))) {
					if (Integer.parseInt(data1.get(cell).toString()) > 3) {
						data1.put(cell, "正常");
					} else
						data1.put(cell, displayName[Integer.parseInt(data1.get(
								cell).toString())]);
				}
			} else
				return false;
		}
		return true;
	}

	/**
	 * 根据电路编号，和电路类型获取电路的网元和端口
	 * 
	 * @param cirNo
	 * @param type
	 * @return
	 */
	public Map<String, Object> getNeAndPortByCirNo(int cirNo, int type)
			throws CommonException {
		Map result = new HashMap();
		try {
			Map query = new HashMap();
			query.put("vCircuit", cirNo);
			List<Map> resultList = new ArrayList<Map>();
			Map map = new HashMap();
			// 用于查询otn/sdh电路的两端ptp信息
			// 两端ptp查询结果
			// 根据type选择不同的查询方式
			if (type == 1) {
				map.put("route_table", "t_cir_circuit_route");
				map.put("crs_table", "t_base_sdh_crs");
				map.put("crs_id_name", "BASE_SDH_CRS_ID");
				map.put("CHAIN_TYPE", 1);
				map.put("cir_id_name", "CIR_CIRCUIT_ID");
				resultList = circuitManagerMapper.getCircuitBycircuitNo(query);
			} else if(type == 4) {
				map.put("cir_id_name", "CIR_CIRCUIT_ID");
				resultList = circuitManagerMapper.getPtnCircuitBycircuitNo(query);
			}else {
				map.put("route_table", "t_cir_otn_circuit_route");
				map.put("crs_table", "t_base_otn_crs");
				map.put("crs_id_name", "BASE_OTN_CRS_ID");
				map.put("CHAIN_TYPE", 2);
				map.put("cir_id_name", "CIR_OTN_CIRCUIT_ID");
				resultList = circuitManagerMapper
						.getOtnCircuitBycircuitNo(query);
			}
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (Map map1 : resultList) {
				ids.add(Integer.parseInt(map1.get(map.get("cir_id_name"))
						.toString()));
			}
			if (ids.size() <= 0) {
				ids.add(-1);
			}
			// 电路中所有的端口
			Set<Integer> ptpList = new HashSet<Integer>();
			// 电路中所有的网元
			Set<Integer> neList = new HashSet<Integer>();
			// a/z端的ptpId（从ptpList中删除）
			Set<Integer> endPtps = new HashSet<Integer>();
			for (int i = 0; i < ids.size(); i++) {
				map.put("cirId", ids.get(i));
				if(type == 4){
					
					List<Map<String, Integer>> getResult = circuitManagerMapper.getPtnNeAndPortByCirNo(map);
					for (int j = 0; j < getResult.size(); j++) {
						ptpList.add(getResult.get(j).get("ptp_id"));
						neList.add(getResult.get(j).get("ne_id"));
		
					}
				}else{
					List<Map<String, Integer>> getResult = circuitManagerMapper
							.getNeAndPortByCirNo(map);
					for (int j = 0; j < getResult.size(); j++) {
						ptpList.add(getResult.get(j).get("A_END_PTP"));
						ptpList.add(getResult.get(j).get("Z_END_PTP"));
						neList.add(getResult.get(j).get("BASE_NE_ID"));

					}
				}
				
			}
			result.put("neList", neList);
			result.put("ptpList", ptpList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}
	/**
	 * 根据电路编号，和电路类型获取电路的网元和端口,时隙
	 * 
	 * @param cirNo
	 * @param type
	 * @return
	 */
	public Map<String, Object> getNeAndPortAndCtpByCirNo(int cirNo, int type)
			throws CommonException {
		Map result = new HashMap();
		try {
			Map query = new HashMap();
			query.put("vCircuit", cirNo);
			List<Map> resultList = new ArrayList<Map>();
			Map map = new HashMap();
			// 用于查询otn/sdh电路的两端ptp信息
			// 两端ptp查询结果
			// 根据type选择不同的查询方式
			if (type == 1) {
				map.put("route_table", "t_cir_circuit_route");
				map.put("crs_table", "t_base_sdh_crs");
				map.put("crs_id_name", "BASE_SDH_CRS_ID");
				map.put("CHAIN_TYPE", 1);
				map.put("cir_id_name", "CIR_CIRCUIT_ID");
				resultList = circuitManagerMapper.getCircuitBycircuitNo(query);
			} else {
				map.put("route_table", "t_cir_otn_circuit_route");
				map.put("crs_table", "t_base_otn_crs");
				map.put("crs_id_name", "BASE_OTN_CRS_ID");
				map.put("CHAIN_TYPE", 2);
				map.put("cir_id_name", "CIR_OTN_CIRCUIT_ID");
				resultList = circuitManagerMapper
						.getOtnCircuitBycircuitNo(query);
			}
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (Map map1 : resultList) {
				ids.add(Integer.parseInt(map1.get(map.get("cir_id_name"))
						.toString()));
			}
			if (ids.size() <= 0) {
				ids.add(-1);
			}
			// 电路中所有的端口
			Set<Integer> ptpList = new HashSet<Integer>();
			// 电路中所有的网元
			Set<Integer> neList = new HashSet<Integer>();
			// a/z端的ptpId（从ptpList中删除）
			Set<Integer> endPtps = new HashSet<Integer>();
			// 电路中所有的端口
			Set<Integer> ctpList = new HashSet<Integer>();
			// 时隙信息
			List<Map> ctpInfoList = new ArrayList<Map>();
			for (int i = 0; i < ids.size(); i++) {
				map.put("cirId", ids.get(i));
				List<Map<String, Integer>> getResult = circuitManagerMapper
						.getNeAndPortByCirNo(map);
				for (int j = 0; j < getResult.size(); j++) {
					ptpList.add(getResult.get(j).get("A_END_PTP"));
					ptpList.add(getResult.get(j).get("Z_END_PTP"));
					ctpList.add(getResult.get(j).get("A_END_CTP"));
					ctpList.add(getResult.get(j).get("Z_END_CTP"));
					neList.add(getResult.get(j).get("BASE_NE_ID"));
					Map ctpA = new HashMap();
					ctpA.put("BASE_NE_ID", getResult.get(j).get("BASE_NE_ID"));
					ctpA.put("BASE_PTP_ID", getResult.get(j).get("A_END_PTP"));
					ctpA.put("BASE_CTP_ID", getResult.get(j).get("A_END_CTP"));
					Map ctpZ = new HashMap();
					ctpZ.put("BASE_NE_ID", getResult.get(j).get("BASE_NE_ID"));
					ctpZ.put("BASE_PTP_ID", getResult.get(j).get("Z_END_PTP"));
					ctpZ.put("BASE_CTP_ID", getResult.get(j).get("Z_END_CTP"));
					ctpInfoList.add(ctpA);
					ctpInfoList.add(ctpZ);
				}
			}
			result.put("neList", neList);
			result.put("ptpList", ptpList);
			result.put("ctpList", ctpList);
			result.put("ctpInfoList", ctpInfoList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_SELECT);
		}
		return result;
	}

	/**
	 * 告警模块的电路相关性查询
	 * 
	 * @param Map
	 *            map:keySet={"nodeLevel","nodes","limit","start","serviceType"}
	 * @return Map result:keySet={"total","rows"}
	 */
	public Map<String, Object> getAboutCircuit(Map map) throws CommonException {
		Map result = new HashMap();
		// 添加了nodeList，当nodelevel=3时，查询链路
		try {
			getAboutCircuitCon(map);
			// 将key:nodeList的值转化成ArrayList<Integer>数组;
			Map<String, Object> total = new HashMap<String, Object>();
			List<Map> ciucuits = null;
			if(map.get("serviceType")!=null&&"4".equals(map.get("serviceType").toString())){
				ciucuits = circuitManagerMapper.selectPtnCircuitAbout(map);
				total = circuitManagerMapper.selectPtnCircuitAboutCount(map);
			}else{
				ciucuits = circuitManagerMapper.selectCircuitAbout(map);
				total = circuitManagerMapper.circuitAboutTotal(map);
			}
			
			result.put("total", total.get("total"));
			result.put("rows", ciucuits);
		} catch (RuntimeException e) {
			// 输入参数的格式不对，无法转化
			e.printStackTrace();
			throw new CommonException(e,
					MessageCodeDefine.COM_EXCPT_INVALID_INPUT);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_UNKNOW);
		}
		return result;
	}

	public void getAboutCircuitCon(Map map) {
		List nodeList = new ArrayList();
		String[] nodes = map.get("nodes").toString().split(",");
		for (int i = 0; i < nodes.length; i++) {
			nodeList.add(Integer.parseInt(nodes[i]));
		}
		String select_id = null;
		int tag = -1;// 用来区分，不同等级设备的查询方式1：网元分组到网元和ctp2：机架到端口//
		if (map.get("nodeLevel").toString().equals("3")) {
			map.put("nodeLevel", -1);
			map.put("linkId", nodeList.get(0));
		}
		if (map.get("nodeLevel").toString().equals("4")) {
			tag = 1;
			select_id = "ne.BASE_NE_ID";
		}
		if (map.get("nodeLevel").toString().equals("7")) {
			tag = 2;
			select_id = "BASE_UNIT_ID";
		}
		if (map.get("nodeLevel").toString().equals("8")) {
			tag = 2;
			select_id = "BASE_PTP_ID";
		}
		if(map.get("nodeLevel").toString().equals("9")){
			select_id = "route.CHAIN_ID";
			tag = 1;
			//根据时隙查询出交叉连接Id
			nodeList = circuitManagerMapper.getCrsByCtpId(nodeList.get(0),map.get("serviceType"));
			if(nodeList.size() == 0){
				nodeList.clear();
				nodeList.add(-1);
			}
		}
		if (map.get("nodeLevel").toString().equals("10")) {
			tag = 2;
			select_id = "BASE_PTP_ID";
			map.put("nodeLevel", 8);
			int tempId = Integer.parseInt(nodeList.get(0).toString());
			nodeList.clear();
			for (Map<String, Integer> temp : circuitManagerMapper
					.getPtpByProId(tempId)) {
				nodeList.add(temp.get("BASE_PTP_ID"));
			}
			
		}
		map.put("select_id", select_id);
		map.put("nodeList", nodeList);
		map.put("tag", tag);
		int serviceType = Integer.parseInt(map.get("serviceType").toString());
		CircuitDefineFactory fa = new CircuitDefineFactory();
		CircuitDefine cons = fa.getCircuitDefineFactory(serviceType);
		map.put("CTP_ID", cons.ctpId);
		map.put("ctp_table", cons.ctpTable);
		map.put("A_END_CTP_VALUE", cons.actp);
		map.put("Z_END_CTP_VALUE", cons.zctp);
		map.put("cir_info_table", cons.cirInfoTable);
		map.put("cir_table", cons.cirTable);
		map.put("cir_info_id", cons.cirInfoId);
		map.put("cir_id", cons.cirId);
		map.put("route", cons.routeTable);
		map.put("crs", cons.crsTable);
		map.put("crs_id", cons.crsId);
	}

	/**
	 * 通过指定的ctp获取相关电路信息
	 * 
	 * @param Map
	 *            map:keySet={"nodeLevel","nodeList","limit","start","DOMAIN"}
	 * @return Map result:keySet={"total","rows"}
	 * @throws CommonException
	 */
	public Map getAboutCircuitByCtp(Map map) throws CommonException {
		Map<String, Object> result = new HashMap<String, Object>();

		return result;
	}

	public Map<String, Object> getCurrentAlarmForCircuit(String jsonString,
			int start) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = (Map) jsonObject;
		List<Integer> neList = new ArrayList<Integer>();
		List<Map<String, Integer>> endResult = new ArrayList<Map<String, Integer>>();
		// 查询指定网元或链路上的告警
		if (map.get("cirNo") != null && map.get("type") != null) {
			Map endPtpsMap = new HashMap();
			endPtpsMap.put("cirNo", map.get("cirNo"));
			if ("1".equals(map.get("type").toString())) {
				endPtpsMap.put("tableName", "t_cir_circuit_info");
			} else {
				endPtpsMap.put("tableName", "t_cir_otn_circuit_info");
			}
			endResult = circuitManagerMapper.getEndPtpsByCirNo(endPtpsMap);
		}
		if (map.get("neList") != null) {
			JSONArray neArray = JSONArray.fromObject(map.get("neList"));
			for (Object o : neArray) {
				if (o.toString().equals("")) {
					continue;
				}
				neList.add(Integer.parseInt(o.toString()));
			}
		} else
			neList.add(0);
		List<Integer> ptpList = new ArrayList<Integer>();
		ptpList.add(0);
		if (map.get("ptpList") != null) {
			JSONArray ptpArray = JSONArray.fromObject(map.get("ptpList"));
			for (Object o : ptpArray) {
				if (o.toString().equals("")) {
					continue;
				}
				ptpList.add(Integer.parseInt(o.toString()));
			}
		} else
			ptpList.add(0);
		int pageSize = 500;
		if (map.get("limit") != null) {
			pageSize = Integer.parseInt(map.get("limit").toString());
		}
		// 如果能查询到电路的A、Z端信息
		if (endResult.size() > 0) {
			if (endResult.get(0).get("A_END_NE") != null
					&& endResult.get(0).get("A_END_PTP") != null) {
				// 如果框选的网元中有A端网元，则手动向ptpList中添加a端落地端口
				if (neList.contains(endResult.get(0).get("A_END_NE"))) {
					ptpList.add(endResult.get(0).get("A_END_PTP"));
				}
			}
			if (endResult.get(0).get("Z_END_NE") != null
					&& endResult.get(0).get("Z_END_PTP") != null) {
				// 如果框选的网元中有Z端网元，则手动向ptpList中添加Z端落地端口
				if (neList.contains(endResult.get(0).get("Z_END_NE"))) {
					ptpList.add(endResult.get(0).get("Z_END_PTP"));
				}
			}
		}
		List<Map> unitList = circuitManagerMapper.selectUnitId(ptpList);
		List<Integer>listUnit = new ArrayList<Integer>();
		listUnit.add(0);
		if(unitList!=null&&unitList.size()>0){
			for(Map ma :unitList){
				listUnit.add(Integer.parseInt(ma.get("BASE_UNIT_ID").toString()));
			}
			
		}
		return faultManagerService.getCurrentAlarmForCircuit(neList, ptpList,
				start, pageSize);
	}

	public static void main(String[] args) {
//		 CircuitManagerServiceImpl tt = new CircuitManagerServiceImpl();
//		 try {
//		 tt.calculateDate("0,0,0,5,27,16:10:00", "2");
//		 } catch (ParseException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
//		 for (int i = 0; i < 5; i++) {
//			 char c = (char) (97 + i);
//			 System.out.println(c);
//		 }
		
		String testName = "hello map!";
		Map map = new HashMap();
		map.put("test", testName);
		System.out.println(testName.substring(0,4));
		if(testName!=null && !testName.isEmpty()){
			System.out.println("12\n34");
			System.out.println("if test > 0 \n then  go head ! ");
		}
		System.out.println(map.get("test").toString());
		
	}
	/**~~~~~~~~~~~~~~~~~~~~~~~~~333~~~~~~~~~~~~~~~~~~~~~~~~~~~**/
	
	public List<Map> sdhToFormat(List<Map> resultList)
			throws CommonException {
	
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Map map1 : resultList) {
			ids.add(Integer.parseInt(map1.get("CIR_CIRCUIT_ID").toString()));
		}
		if (ids.size() <= 0) {
			ids.add(-1);
		}
		// 存放节点和链路信息
		List<Map> list = new ArrayList<Map>();
		for (int j = 0; j < ids.size(); j++) {
			List<Map> origianlData = circuitManagerMapper
					.getCircuitRouteTopo(ids.get(j));
			List<Map> formatData = getFormatData(origianlData);
			// 创建topo图模块的节点对象和链路对象
			list.addAll(formatData);
		}
		return list;
	}

	
	/**
	 * otn转为formatData
	 * @param resultList
	 * @return
	 * @throws CommonException
	 */
	public List<Map> ontToFormat(List<Map> resultList)
			throws CommonException{
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Map map1 : resultList) {
			ids.add(Integer.parseInt(map1.get("CIR_OTN_CIRCUIT_ID").toString()));
		}
		if (ids.size() <= 0) {
			ids.add(-1);
		}
		// 存放节点和链路信息
		List<Map> list = new ArrayList<Map>();
		for (int j = 0; j < ids.size(); j++) {
			List<Map> origianlData = circuitManagerMapper
					.getOtnCircuitRouteTopo(ids.get(j));
			List<Map> formatData = getFormatData(origianlData);
			list.addAll(formatData);
		}
		return list;
	}
	
	
	/**
	 * 加载一个既有OTN又有SDH的拓扑图
	 */
	public Map<String, Object> getRouteTopoOtnAndSdh(List<Map<String,Object>> cirList)
			throws CommonException {
		List<Map> rows = new ArrayList<Map>();
		Map query = new HashMap(); 
		List<Map> formatData = new ArrayList<Map>();
		for(Map<String,Object> cir : cirList){
			query.put("vCircuit", cir.get("cir_no"));
			if("3".equals(cir.get("svc_type").toString())){
				List<Map> resultList = circuitManagerMapper
						.getOtnCircuitBycircuitNo(query);
				formatData.addAll(ontToFormat(resultList));
			}else{
				List<Map> resultList = circuitManagerMapper
						.getCircuitBycircuitNo(query);
				formatData.addAll(sdhToFormat(resultList));
			}
		}
		List<Integer> ptpIdList = extractPtpId(formatData);
		getTopoData(formatData,rows);
		// 用来返回结果
		Map<String, Object> result = new HashMap();
		result.putAll(topoManagerService.getAlarmColorSet());
		result.put("rows", rows);
		result.put("currentTopoType", "EMS");
		result.put("total", rows.size());
		result.put("layout", "free");
		//为了有条件啊啊啊啊
		result.put("ptpIdList", ptpIdList);
		return result;
	}
	

	private List<Integer> extractPtpId(List<Map> formatData) {
		Set<Integer> ptpIdSet = new HashSet<Integer>();
		for(Map m :formatData){
			//TODO
			if(m.get("PTP1")!=null)
				ptpIdSet.add(Integer.valueOf(m.get("PTP1").toString()));
			if(m.get("PTP2")!=null)
				ptpIdSet.add(Integer.valueOf(m.get("PTP2").toString()));
			if(m.get("A_END_PTP")!=null)
				ptpIdSet.add(Integer.valueOf(m.get("A_END_PTP").toString()));
			if(m.get("Z_END_PTP")!=null)
				ptpIdSet.add(Integer.valueOf(m.get("Z_END_PTP").toString()));
		}
		return new ArrayList<Integer>(ptpIdSet);
	}

	/**
	 * 获取拓扑图初始化数据
	 * @param resultList
	 * @return
	 * @throws CommonException
	 */
	public void getTopoData(List<Map> formatData, List rows)
			throws CommonException {
		List rawLineRow = new ArrayList();
		List<Map> allNodeRecord = new ArrayList();
		List list = new ArrayList();

		for (int i = 0; i < formatData.size(); i++) {
			if (formatData.get(i).get("base_ne_id") != null) {
				allNodeRecord.add(formatData.get(i));
			} else {
				TopoLineModel linkModel = new TopoLineModel();
				List<Map> portInfo = new ArrayList<Map>();
				portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
						.parseInt(formatData.get(i).get("A_END_PTP").toString())));
				portInfo.addAll(circuitManagerMapper.getPortInfo(Integer
						.parseInt(formatData.get(i).get("Z_END_PTP").toString())));
				Map intallMap = new HashMap();
				// 为链路的a/z端赋值
				for (int k = 0; k < portInfo.size(); k++) {
					String name = "a";
					if (k == 1) {
						name = "z";
					}
					Iterator it = portInfo.get(k).keySet().iterator();
					while (it.hasNext()) {
						Object o = it.next();
						intallMap.put(name + o, portInfo.get(k).get(o));
					}
				}
				intallMap.put("aNeType", 3);
				intallMap.put("zNeType", 3);
				// 为LinkAlarmModel赋值
				LinkAlarmModel linkAlarm = topoManagerService
						.transMap2LinkAlarmModel(intallMap);
				linkAlarm.setLinkId(Integer.parseInt(formatData.get(i)
						.get("BASE_LINK_ID").toString()));
				ArrayList<Integer> neList = new ArrayList<Integer>();
				ArrayList<Integer> aPtpList = new ArrayList<Integer>();
				ArrayList<Integer> zPtpList = new ArrayList<Integer>();
				aPtpList.add(Integer.parseInt(formatData.get(i)
						.get("A_END_PTP").toString()));
				Map countA = faultManagerService
						.getAllCurrentAlarmCountForCircuit(neList, aPtpList);
				linkAlarm.setaCRCount(Integer.parseInt(countA
						.get("PS_CRITICAL").toString()));
				linkAlarm.setaMJCount(Integer.parseInt(countA.get("PS_MAJOR")
						.toString()));
				linkAlarm.setaMNCount(Integer.parseInt(countA.get("PS_MINOR")
						.toString()));
				linkAlarm.setaWRCount(Integer.parseInt(countA.get("PS_WARNING")
						.toString()));
				zPtpList.add(Integer.parseInt(formatData.get(i)
						.get("Z_END_PTP").toString()));
				Map countZ = faultManagerService
						.getAllCurrentAlarmCountForCircuit(neList, zPtpList);
				linkAlarm.setzCRCount(Integer.parseInt(countZ
						.get("PS_CRITICAL").toString()));
				linkAlarm.setzMJCount(Integer.parseInt(countZ.get("PS_MAJOR")
						.toString()));
				linkAlarm.setzMNCount(Integer.parseInt(countZ.get("PS_MINOR")
						.toString()));
				linkAlarm.setzWRCount(Integer.parseInt(countZ.get("PS_WARNING")
						.toString()));
				List<LinkAlarmModel> rawLinkAlarm = new ArrayList<LinkAlarmModel>();
				rawLinkAlarm.add(linkAlarm);
				linkModel.setLinkAlarm(rawLinkAlarm);
				linkModel.setNodeOrLine("line");
				linkModel.setLineType("neLine");
				linkModel.setFromNode(formatData.get(i - 1).get("base_ne_id")
						.toString());
				linkModel.setFromNodeType("3");
				linkModel.setToNode(formatData.get(i + 1).get("base_ne_id")
						.toString());
				linkModel.setToNodeType("3");
				rawLineRow.add(linkModel);
			}
		}
		// 这里"1"应该变为"3",由于前台内部路由暂无法实现
		rows.addAll(deleteRepeatNode(allNodeRecord, "1"));
		rows.addAll(deleteRepeatLink(rawLineRow));
	}
	
	
	/**将重复的link进行整合
	 * @param lineList
	 * @return
	 */
	public List<TopoLineModel> deleteRepeatLink(List<TopoLineModel> lineList) {
		
		List<TopoLineModel> resultLineList = new ArrayList<TopoLineModel>();
		
		for(TopoLineModel line : lineList){
			boolean isRepeat = false;
			//判断当前的link是否和result中的重复
			for(TopoLineModel resultLine : resultLineList){
				if(line.getFromNode().equals(resultLine.getFromNode()) &&
				   line.getFromNodeType().equals(resultLine.getFromNodeType()) &&
				   line.getToNode().equals(resultLine.getToNode()) &&
				   line.getToNodeType().equals(resultLine.getToNodeType())){
					
					isRepeat = true;
					boolean repeatAlarm = false;
					for(LinkAlarmModel la : resultLine.getLinkAlarm()){
						if(la.getaEndPTP()==line.getLinkAlarm().get(0).getaEndPTP()&&la.getzEndPTP()==line.getLinkAlarm().get(0).getzEndPTP())
							repeatAlarm = true;
					}
					if(!repeatAlarm)
						resultLine.getLinkAlarm().add(line.getLinkAlarm().get(0));
				}
				
				if(line.getFromNode().equals(resultLine.getToNode()) &&
				   line.getFromNodeType().equals(resultLine.getToNodeType()) &&
				   line.getToNode().equals(resultLine.getFromNode()) &&
				   line.getToNodeType().equals(resultLine.getFromNodeType())){
					
					isRepeat = true;
					
					boolean repeatAlarm = false;
					for(LinkAlarmModel la : resultLine.getLinkAlarm()){
						if(la.getaEndPTP()==line.getLinkAlarm().get(0).getaEndPTP()&&la.getzEndPTP()==line.getLinkAlarm().get(0).getzEndPTP())
							repeatAlarm = true;
					}
					if(!repeatAlarm)
						resultLine.getLinkAlarm().add(line.getLinkAlarm().get(0));
				}
			}
			
			if(!isRepeat){
				resultLineList.add(line);
			}
		}
		
		return resultLineList;
	} 
	
	public void cancelRelateFiber(List<Map<String, Object>> list)throws CommonException{ 
		 try{
			 for(Map o: list){
				 int linkId_=0;
				 if(o.get("DIRECTION")!=null && "1".equals(o.get("DIRECTION").toString())){
					 //双向
					 Map<String,Object> data = new HashMap<String,Object>();
					 data = circuitManagerMapper.getRelateInfo(Integer.parseInt(o.get("aNodeId").toString()),
							 Integer.parseInt(o.get("zNodeId").toString()));  
					 linkId_= Integer.parseInt(data.get("linkId_").toString());
				 }
				 circuitManagerMapper.cancelRelateFiber(Integer.parseInt(o.get("BASE_LINK_ID").toString()),linkId_); 
			 }  
		 }catch(Exception e){
			 throw  new CommonException(e, -1, "删除关联关系失败！");
		 } 
	}
	
	public void relateFiber(Map<String,Object>map)throws CommonException{
		try{
			  circuitManagerMapper.relateFiber(map); 
			  if(map.get("linkId_")!=null && !"".equals(map.get("linkId_").toString())){
				  map.put("linkId",map.get("linkId_").toString());
				  map.put("fiberId",map.get("fiberId_").toString());
				  circuitManagerMapper.relateFiber(map);  
			  }
		 }catch(Exception e){
			 throw  new CommonException(e, -1, "关联关系失败！");
		 } 
	}
	 
	public Map<String, Object> getRelateInfo(long linkId,int aNodeId,int zNodeId)
			throws CommonException{
	    Map<String,Object> data = new HashMap<String,Object>();
		try{  
			  data = circuitManagerMapper.getRelateInfo(aNodeId,zNodeId); 
		 }catch(Exception e){
			 throw  new CommonException(e, -1, "关联关系失败！");
		 } 
		return data;
	}
	
	/**
	 * 中兴ptn电路生成
	 */
	public boolean createZtePtnCircuit(Map mapSelect, String id, String sessionId,
			int countSdh, int countOtn,int userId){
		// 获取中兴ptn端口为边界点的ptp，并判断网元类型，其中ZXCTN 6100.6200.6300 为一个处理方式，ZXCTN 9004 为另一种处理方式
		boolean stop = false;
		Map select = null;
		Map insert = null;
		Map update = null;
		
		// 清除临时表数据
		//circuitManagerMapper.deleteAllTemp(select);
		
		// 暂时不做分页
		int countPtp = circuitManagerMapper.getPtnPtpCount(mapSelect,userId,CommonDefine.TREE.TREE_DEFINE);
		if(countPtp>0){
			for(int m = 0;m<countPtp/1000+1;m++){
				if(stop){
					break;
				}
				List<Map> listPtp = circuitManagerMapper.getPtnPtp(mapSelect, 1000*m, 1000,userId,CommonDefine.TREE.TREE_DEFINE);
				
				
				// 先找出所有对应的ptp和ctp 然后插入临时表
				if(listPtp != null && listPtp.size() > 0 ){
					//for(Map mapPtp : listPtp){
					for(int n = 0 ; n<listPtp.size();n++){
						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						Map mapPtp = listPtp.get(n);
						List<Map> listc = new ArrayList<Map>();
						// 判断ptp口是ZXCTN 6x00还是ZXCTN 9004
						if(!"ZXCTN 9004".equals(mapPtp.get("PRODUCT_NAME").toString())){
							// 是ZXCTN 6x00
							select = new HashMap();
							select.put("BASE_PTP_ID", mapPtp.get("BASE_PTP_ID"));
							List<Map> listFtp = circuitManagerMapper.getFtpFromBingding(select);
							
							if(listFtp!=null && listFtp.size()>0){
								for(Map mapFtp : listFtp){
									select = new HashMap();
									select.put("BASE_NE_ID", mapFtp.get("BASE_NE_ID"));
									select.put("PTP_NAME", mapFtp.get("BASE_PTP_NAME"));
									List<Map> listVb = circuitManagerMapper.getFtpfromVb(select);
									if(listVb!=null && listVb.size()>0 ){
										for(Map vb :listVb){
											// 根据ftp的pwId获取纬线id，并进行绑定
											select = new HashMap();
											select.put("BASE_NE_ID", mapFtp.get("BASE_NE_ID"));
											select.put("PW_ID", vb.get("PTN_PWID"));
											List<Map> listPw = circuitManagerMapper.getPwByPw(select);
												if(listPw!=null && listPw.size()>0){
													for(Map pw:listPw){
														insert = new HashMap();
														insert.put("BASE_PTP_ID", mapPtp.get("BASE_PTP_ID"));
														insert.put("BASE_PTN_CTP_ID", pw.get("BASE_PTN_CTP_ID"));
														insert.put("SRC_IN_LABEL", pw.get("SRC_IN_LABEL"));
														insert.put("SRC_OUT_LABEL", pw.get("SRC_OUT_LABEL"));
														circuitManagerMapper.insertPtnTemp(insert);
													}
													
												}																							
										}
									}
								}
							}
							
						}else {
							// 是ZXCTN 9004
							select = new HashMap();
							select.put("PTP_NAME", mapPtp.get("NAME"));
							select.put("BASE_NE_ID", mapPtp.get("BASE_NE_ID"));
							List<Map> listFtp = circuitManagerMapper.getPtpFromVb(select);
							if(listFtp != null && listFtp.size()>0){
								for(Map ftp:listFtp){
									select = new HashMap();
									select.put("PW_ID", ftp.get("PTN_PWID"));
									select.put("BASE_NE_ID", ftp.get("BASE_NE_ID"));
									List<Map> listCtp = circuitManagerMapper.getPwByPtp(select);
									if(listCtp != null && listCtp.size() > 0 ){
										
										for(Map ctp : listCtp){
											insert = new HashMap();
											insert.put("BASE_PTP_ID", mapPtp.get("BASE_PTP_ID"));
											insert.put("BASE_PTN_CTP_ID", ctp.get("BASE_PTN_CTP_ID"));
											insert.put("SRC_IN_LABEL", ctp.get("SRC_IN_LABEL"));
											insert.put("SRC_OUT_LABEL", ctp.get("SRC_OUT_LABEL"));
											circuitManagerMapper.insertPtnTemp(insert);
										}
									}
								}
							}
						}
						
						CommonDefine.setProcessParameter(sessionId,id,(n+1+1000*m),(int) (countPtp*1.006+1),"ptn端口解析 ");
					}
					
				}else{
					CommonDefine.setProcessParameter(sessionId,id,countPtp,(int) (countPtp*1.006+1),"ptn端口解析 ");
				}
			}
		}else{
			CommonDefine.setProcessParameter(sessionId,id,countPtp,(int) (countPtp*1.006+1),"ptn端口解析 ");
		}
		
		
		
		// 正式生成ptn电路
		// 考虑电路重复的问题
		
		// 先统计需要生成电路的数量
		
		// 从ptn临时表中获取数据，逐步查找电路
		
		//List<Map> list = circuitManagerMapper.getptnTemp(select);
		int count = circuitManagerMapper.getptnTempCount(select);
		if(count>0){
			for(int n = 0 ; n<=count;n++){
				
				if (CommonDefine.getIsCanceled(sessionId, id)) {
					CommonDefine.respCancel(sessionId, id);
					stop = true;
					break;
				}
				// 每次只取一条
				List<Map> list = circuitManagerMapper.getptnTemp(select);
				//根据遍历ctp生成电路	
				if(list!=null && list.size()>0){				
					for(Map ct : list){
						
						List<Map> listRoute = new ArrayList<Map>();
						
						// 创建电路的起始端,
						Map insertPtnInfo = new HashMap();
						// 设置起始口
						insertPtnInfo.put("A_END_PTP", ct.get("BASE_PTP_ID"));
						insertPtnInfo.put("IS_COMPLETE_CIR", 0);
						
						// 具体的每条电路
						Map insertPtnCir = new HashMap();
						insertPtnCir.put("A_END_PTP", ct.get("BASE_PTP_ID"));
						insertPtnCir.put("A_END_CTP", ct.get("BASE_PTN_CTP_ID"));
						
						
						// 根据起点，生成电路,将电路起始端口座位一条路由
						Map insertPtnRoutePtp = new HashMap();
						// 端口
						//insertPtnRoutePtp.put("CIR_CIRCUIT_ID", "");
						insertPtnRoutePtp.put("CHAIN_ID", ct.get("BASE_PTP_ID"));
						insertPtnRoutePtp.put("CHAIN_TYPE", 1);
						listRoute.add(insertPtnRoutePtp);
						
						// 第二跳是纬线
						Map insertPtnRoutePw = new HashMap();
						//insertPtnRoutePw.put("CIR_CIRCUIT_ID", "");
						insertPtnRoutePw.put("CHAIN_ID", ct.get("BASE_PTN_CTP_ID"));
						insertPtnRoutePw.put("CHAIN_TYPE", 2);
						listRoute.add(insertPtnRoutePw);
						select = new HashMap();
						select.put("BASE_PTN_CTP_ID", ct.get("BASE_PTN_CTP_ID"));
						
						
						// 根据纬线的值 去路由表 查隧道,且端口号相同
						List<Map> listName = circuitManagerMapper.getTunelNameFromCtp(select);
						
						if(listName!=null && listName.size()>0){
							Object ob = listName.get(0).get("NAME");
							if(ob != null){
							String  name = 	ob.toString().split("/pw")[0].replace("tmpctpID", "tmp");
							select = new HashMap();
							select.put("NAME", name);
							select.put("BASE_PTP_ID", listName.get(0).get("BASE_PTP_ID"));
							List<Map> listTunel = circuitManagerMapper.getTunelFromRoute(select);
							if(listTunel!=null && listTunel.size()>0){
								// 可能会有多条隧道，只去一条,获取隧道的name值
								Map mapTunel = listTunel.get(0);
								
								// 用name 查出该条路由所有的相关隧道
								select = new HashMap();
								select.put("NAME", mapTunel.get("BELONGED_TRAIL").toString());
								List<Map> listTun = circuitManagerMapper.getTunelbyName(select);
								Object ptp = null;
								Object lastPtpId = null;
								List<Map> listTT = getTun(listTun,mapTunel);
								if(listTT!=null && listTT.size()>0){
								// 判断第一条记录是否是与mapTunel吻合，如果不吻合反向排序
									
								boolean	isBeginPw = false;
								boolean isLinkOppsite = false;
									for(int i = 0 ; i<listTT.size();i++){
										isLinkOppsite = false;
										// 遍历路由，插入ptn路由表
										// 先判断获取的路由是否是pw，如果是跳过
//										if(listTT.get(i).get("A_END_CTP").toString().contains("pw")){
//											isBeginPw = true; 
//											continue;
//										}
										// 如果存在纬线，则 向后再
										//判断是否存在link联系
										if(i>=1){
											select = new HashMap();
											select.put("A_END_PTP", ptp);
											select.put("Z_END_PTP", listTT.get(i).get("A_END_PTP"));
											List<Map> linklist = circuitManagerMapper.getlinkByAZ(select);
											if(linklist!=null&&linklist.size()>0){
												// 将电路插入
												Map insertPtnlink = new HashMap();
												//insertPtnlink.put("CIR_CIRCUIT_ID", "");
												insertPtnlink.put("CHAIN_ID", linklist.get(0).get("BASE_LINK_ID"));
												insertPtnlink.put("CHAIN_TYPE", 3);
												listRoute.add(insertPtnlink);
											}else{
												// az互换
												select = new HashMap();
												select.put("A_END_PTP", ptp);
												select.put("Z_END_PTP", listTT.get(i).get("Z_END_PTP"));
												List<Map> linklist_ = circuitManagerMapper.getlinkByAZ(select);
												if(linklist_!=null&&linklist_.size()>0){
													isLinkOppsite = true;
													// 将电路插入
													Map insertPtnlink = new HashMap();
													//insertPtnlink.put("CIR_CIRCUIT_ID", "");
													insertPtnlink.put("CHAIN_ID", linklist_.get(0).get("BASE_LINK_ID"));
													insertPtnlink.put("CHAIN_TYPE", 3);
													listRoute.add(insertPtnlink);
												}else{
													// 结束电路查找
													break;
												}
												
											}
										}
										
										
										// 判断是否是隧道,z端为空，就插入一条，否则插入两条
										if(listTT.get(i).get("Z_END_CTP") == null){
											// 根据ptp 和ctp 去ptnctp表中查出对应id
											select = new HashMap();
											select.put("BASE_PTP_ID", listTT.get(i).get("A_END_PTP"));
											select.put("NAME", listTT.get(i).get("A_END_CTP"));
											List<Map> lista = circuitManagerMapper.getPtnCtp(select);
											if(lista!=null && lista.size()>0){
												Map insertPtnRoute = new HashMap();
												//insertPtnRoute.put("CIR_CIRCUIT_ID", "");
												insertPtnRoute.put("CHAIN_ID", lista.get(0).get("BASE_PTN_CTP_ID"));
												insertPtnRoute.put("CHAIN_TYPE", 4);
												insertPtnRoute.put("ROUTE_ID", listTT.get(i).get("BASE_PTN_ROUTE_ID"));
												listRoute.add(insertPtnRoute);
											}
											
											ptp =listTT.get(i).get("A_END_PTP")  ;// a 端口
											lastPtpId = listTT.get(i).get("A_END_PTP");
											
										}else{
											if(!isLinkOppsite){
												ptp = listTT.get(i).get("Z_END_PTP"); // z端口
												lastPtpId = listTT.get(i).get("Z_END_PTP");
												// 先放a端
												select = new HashMap();
												select.put("BASE_PTP_ID", listTT.get(i).get("A_END_PTP"));
												select.put("NAME", listTT.get(i).get("A_END_CTP"));
												List<Map> lista = circuitManagerMapper.getPtnCtp(select);
												if(lista!=null && lista.size()>0){
													Map insertPtnRouteA = new HashMap();
													//insertPtnRouteA.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteA.put("CHAIN_ID", lista.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteA.put("CHAIN_TYPE", 4);
													insertPtnRouteA.put("ROUTE_ID", listTT.get(i).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteA);
												}
												
												// 再放z端
												select = new HashMap();
												select.put("BASE_PTP_ID", listTT.get(i).get("Z_END_PTP"));
												select.put("NAME", listTT.get(i).get("Z_END_CTP"));
												List<Map> listz = circuitManagerMapper.getPtnCtp(select);
												if(listz!=null && listz.size()>0){
													Map insertPtnRouteZ = new HashMap();
													//insertPtnRouteZ.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteZ.put("CHAIN_ID", listz.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteZ.put("CHAIN_TYPE", 4);
													insertPtnRouteZ.put("ROUTE_ID", listTT.get(i).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteZ);
												}
											}else{
												ptp = listTT.get(i).get("A_END_PTP"); // a端口
												lastPtpId = listTT.get(i).get("A_END_PTP");
												// 先放z端
												select = new HashMap();
												select.put("BASE_PTP_ID", listTT.get(i).get("Z_END_PTP"));
												select.put("NAME", listTT.get(i).get("Z_END_CTP"));
												List<Map> listz = circuitManagerMapper.getPtnCtp(select);
												if(listz!=null && listz.size()>0){
													Map insertPtnRouteZ = new HashMap();
													//insertPtnRouteZ.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteZ.put("CHAIN_ID", listz.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteZ.put("CHAIN_TYPE", 4);
													insertPtnRouteZ.put("ROUTE_ID", listTT.get(i).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteZ);
												}
												
												// 再放a端
												select = new HashMap();
												select.put("BASE_PTP_ID", listTT.get(i).get("A_END_PTP"));
												select.put("NAME", listTT.get(i).get("A_END_CTP"));
												List<Map> lista = circuitManagerMapper.getPtnCtp(select);
												if(lista!=null && lista.size()>0){
													Map insertPtnRouteA = new HashMap();
													//insertPtnRouteA.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteA.put("CHAIN_ID", lista.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteA.put("CHAIN_TYPE", 4);
													insertPtnRouteA.put("ROUTE_ID", listTT.get(i).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteA);
												}
											}
											
											
										}
										
									}
									
									// 查找结束纬线
									
									
								}
								// 查找结束纬线,验证纬线是否合格
								select = new HashMap();
								select.put("SRC_IN_LABEL", ct.get("SRC_OUT_LABEL"));
								select.put("SRC_OUT_LABEL", ct.get("SRC_IN_LABEL"));
								select.put("BASE_PTP_ID", lastPtpId);
								List<Map> lastPw = circuitManagerMapper.getLastPtnPw(select);
								if(lastPw!=null && lastPw.size()>0){
									Map insertLastPw = new HashMap();
									//insertLastPw.put("CIR_CIRCUIT_ID", "");
									insertLastPw.put("CHAIN_ID", lastPw.get(0).get("BASE_PTN_CTP_ID"));
									insertLastPw.put("CHAIN_TYPE", 2);
									listRoute.add(insertLastPw);
									
									// ptn 电路纬线id
									insertPtnCir.put("Z_END_CTP", lastPw.get(0).get("BASE_PTN_CTP_ID"));
									
									
									// 查找结束ptp
									// 根据pw号反推ptp号，需要确认会不会有夸不同网元类型的电路
									select = new HashMap();
									select.put("BASE_PTN_CTP_ID", lastPw.get(0).get("BASE_PTN_CTP_ID"));
									List<Map> lastPtp = circuitManagerMapper.getLastPtnPtp(select);
									if(lastPtp!=null && lastPtp.size()>0){
										Map insertLastPtp = new HashMap();
										//insertLastPtp.put("CIR_CIRCUIT_ID", "");
										insertLastPtp.put("CHAIN_ID", lastPtp.get(0).get("BASE_PTP_ID"));
										insertLastPtp.put("CHAIN_TYPE", 1);
										listRoute.add(insertLastPtp);
										
										// 电路z端节点
										insertPtnCir.put("Z_END_PTP", lastPtp.get(0).get("BASE_PTP_ID"));
										insertPtnInfo.put("Z_END_PTP", lastPtp.get(0).get("BASE_PTP_ID"));
										insertPtnInfo.put("IS_COMPLETE_CIR", 1);
										
									}
									
									
									
									
								}
								
								
							}
							}
						}
						
							
						// 统一插入数据
						//先判断电路是否完整
						//if("1".equals(insertPtnInfo.get("IS_COMPLETE_CIR").toString())){
							// 先查看是否已经存在电路，有就更新，没有插入
							select = new HashMap();
							select.put("A_END_PTP", insertPtnInfo.get("A_END_PTP"));
							select.put("Z_END_PTP", insertPtnInfo.get("Z_END_PTP"));
							select.put("IS_COMPLETE_CIR", insertPtnInfo.get("IS_COMPLETE_CIR"));
							List<Map> listPtnIn = circuitManagerMapper.getPtnInfo(select);
							Object infoId = null;
							Object cirId = null;
							if(listPtnIn!=null && listPtnIn.size()>0){
								insertPtnInfo.put("CIR_CIRCUIT_INFO_ID", listPtnIn.get(0).get("CIR_CIRCUIT_INFO_ID"));
								update = new HashMap();
								update.put("CIR_CIRCUIT_INFO_ID", infoId);
								circuitManagerMapper.updatePtnInfo(update);
							}else{
								Map map_maxNo = circuitManagerMapper.getMaxPtnCircuitNo();
								if (map_maxNo != null) {
									if (map_maxNo.get("CIR_NO") != null&& !map_maxNo.get("CIR_NO").toString().isEmpty()) {
										insertPtnInfo.put("CIR_NO",((Integer.parseInt(map_maxNo.get("CIR_NO").toString()) + 1) + ""));
									} else {
										insertPtnInfo.put("CIR_NO", "100000");
									}
								} else {
									insertPtnInfo.put("CIR_NO", "100000");
								}
								 circuitManagerMapper.insertPtnInfo(insertPtnInfo);
							}
							
							// 插入ptncir 表
							insertPtnCir.put("CIR_CIRCUIT_INFO_ID", insertPtnInfo.get("CIR_CIRCUIT_INFO_ID"));
							circuitManagerMapper.insertPtnCir(insertPtnCir);
							
							// 遍历  插入路由表
							if(listRoute!=null && listRoute.size()>0){
								for(int j = 0 ; j<listRoute.size();j++){
									Map ma = listRoute.get(j);
									ma.put("CIR_CIRCUIT_ID", insertPtnCir.get("CIR_CIRCUIT_ID"));
									ma.put("SEQUENCE", (j+1));
									circuitManagerMapper.insertPtnRoute(ma);
								}
							}
							
							// 更新已经用于电路生成的两条数据
							select = new HashMap();
							select.put("BASE_PTN_CTP_ID", insertPtnCir.get("A_END_CTP"));
							circuitManagerMapper.deletePtnTemp(select);
							
							if(insertPtnCir.get("Z_END_CTP")!=null){
								select = new HashMap();
								select.put("BASE_PTN_CTP_ID", insertPtnCir.get("Z_END_CTP"));
								circuitManagerMapper.deletePtnTemp(select);
							}
							
							
						//}
						
						
					}
				}
				
				CommonDefine.setProcessParameter(sessionId,id,(n+2),(count+1),"ptn电路生成 ");

			}
		}else{
			CommonDefine.setProcessParameter(sessionId,id,(count+1),(count+1),"ptn电路生成 ");
		}
		
		
	return stop;
		
		
	}
	
	
	public boolean createZteOtn(Map mapSelect, String id, String sessionId,int userId) throws CommonException{
		boolean stop = false;
		
		// 只做网元类型是M800和8300的，其他未知
		Map select = null;
		Map insert = null;
		Map update = null;
		//HttpServletRequest request = ServletActionContext.getRequest();
		//int userId=Integer.parseInt(request.getSession().getAttribute("SYS_USER_ID").toString());
		
		// 更新交叉连接查询状态 1 是查找过 0 是没有查找过
		update = new HashMap();
		update.put("NAME", "t_base_otn_crs");
		update.put("ID_NAME", "IS_DEL");
		update.put("ID_VALUE", CommonDefine.FALSE);
		update.put("ID_NAME_2", "IS_IN_CIRCUIT");
		update.put("ID_VALUE_2", CommonDefine.FALSE);

		circuitManagerMapper.updateByParameter(update);

		// 查询a端ptp且为边界点的交叉连接
		// 查询符合条件的交叉连接
		Map mapCount = circuitManagerMapper.selectZteOtnCount(mapSelect,userId,CommonDefine.TREE.TREE_DEFINE);
		int zteOtn = Integer.parseInt(mapCount.get("total").toString());
		int n = 0;
		if (zteOtn > 0) {
			// 如果大于0，则更新otn route，
			// 宁夏定制版本
//			select= new HashMap();
//			
//			List<Map> listEms = circuitManagerMapper.getByParameter(select);
//			if(listEms!=null &&listEms.size()>0){
//				for(Map ems:listEms){
//					dataCollectService.syncCRSFromRoute(ems, CommonDefine.COLLECT_LEVEL_1);
//				}
//			}
			for (int i = 0; i <= (zteOtn - 1) / 1000; i++) {
				if(stop){
					break;
				}
				// 每次取1000条,如果后期查询速度慢，再换其他机制
				List<Map> list = circuitManagerMapper.selectZteOtn(mapSelect, 0,
						1000,userId,CommonDefine.TREE.TREE_DEFINE);
				if (list != null && list.size() > 0) {
					System.out.println("list=="+list.size());
					for (int j = 0; j < list.size(); j++) {
						Map map = list.get(j);
						if (CommonDefine.getIsCanceled(sessionId, id)) {
							CommonDefine.respCancel(sessionId, id);
							stop = true;
							break;
						}
						
//						if(!"656".equals(map.get("BASE_OTN_CRS_ID").toString())){
//							continue;
//						}
						//一条一条遍历
						System.out.println("交叉连接id=="+j+"--"+map.get("BASE_OTN_CRS_ID").toString());
						// 先更新是否被查找状态
						update = new HashMap();
						update.put("NAME", "t_base_otn_crs");
						update.put("ID_NAME", "BASE_OTN_CRS_ID");
						update.put("ID_VALUE", map.get("BASE_OTN_CRS_ID"));
						update.put("ID_NAME_2", "IS_IN_CIRCUIT");
						update.put("ID_VALUE_2", CommonDefine.TRUE);
						circuitManagerMapper.updateByParameter(update);

						// 判断交叉连接是否可以作为起点
						//  ptp起点是否作为另一条交叉的z端（非反向交叉）
						select = new HashMap();
						select.put("Z_END_PTP", map.get("A_END_PTP"));
						select.put("A_END_PTP", map.get("Z_END_PTP"));
						List<Map> ListIsEndPort = circuitManagerMapper.selectIsEndPort(select);
						if(ListIsEndPort!=null&&ListIsEndPort.size()>0){
							CommonDefine.setProcessParameter(
									sessionId,
									id,
									(i * 1000 + j + 1),
									(int) (zteOtn*1.006+1),
									"中兴otn电路生成 ");
							// 跳过本次循环
							continue;
						}
						Map temp = null;
						Map map_otn = map;

						// 插入一条otn电路信息
						insert = new HashMap();
						insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
						insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
						insert.put("CIR_COUNT", 1);

						insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
						// 新增cirNo
						Map map_maxNo = circuitManagerMapper
								.getMaxOtnCircuitNo();
						if (map_maxNo != null) {
							if (map_maxNo.get("CIR_NO") != null
									&& !map_maxNo.get("CIR_NO").toString()
											.isEmpty()) {
								insert.put(
										"CIR_NO",
										((Integer.parseInt(map_maxNo.get(
												"CIR_NO").toString()) + 1) + ""));
							} else {
								insert.put("CIR_NO", "100000");
							}
						} else {
							insert.put("CIR_NO", "100000");
						}
						circuitManagerMapper.insertOtnInfo(insert);

						// 获取插入数据Id
						Integer circuitInfoId = Integer.valueOf(insert.get(
								"CIR_OTN_CIRCUIT_INFO_ID").toString());

						// 新建一条otn电路
						insert = new HashMap();
						insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
						insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
						insert.put("IS_MAIN_CIR", CommonDefine.TRUE);
						insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
						insert.put("CIR_OTN_CIRCUIT_INFO_ID", circuitInfoId);
						Map map_otn_cir = insertOtnCir(insert);

						// 插入首条路由信息
						// 将链路存入路由
						insert = new HashMap();
						insert.put("CIR_OTN_CIRCUIT_ID",
								map_otn_cir.get("CIR_OTN_CIRCUIT_ID"));
						insert.put("CHAIN_ID", map_otn.get("BASE_OTN_CRS_ID"));
						insert.put("CHAIN_TYPE",
								CommonDefine.CHAIN_TYPE_OTN_CRS);

						insert.put("AHEAD_CRS_ID",
								map_otn.get("BASE_OTN_CRS_ID"));
						insert.put("IS_COMPLETE", CommonDefine.TRUE);
						circuitManagerMapper.insertOtnRoute(insert);

						// 经过数量加1
						update = new HashMap();
						update.put("NAME", "t_base_otn_crs");
						update.put("ID_NAME", "BASE_OTN_CRS_ID");
						update.put("ID_VALUE", map_otn.get("BASE_OTN_CRS_ID"));
						update.put("ID_NAME_2", "CIRCUIT_COUNT");
						update.put("ID_VALUE_2", Integer.parseInt(map_otn.get(
								"CIRCUIT_COUNT").toString()) + 1);
						circuitManagerMapper.updateByParameter(update);

						// 单条虚拟交叉生成
						//CreateOtnVirCrsByNe(map_otn, temp, map_otn_cir);
						CreateSingleZteOtn(map_otn, temp, map_otn_cir);
						// 查找支路未完成的虚拟交叉
						select = hashMapSon("t_cir_otn_circuit_route",
								"IS_COMPLETE", CommonDefine.FALSE, null, null,
								null);

						List<Map> list_false = new ArrayList<Map>();
						boolean isGoOn = true;
						do {
							list_false = circuitManagerMapper
									.getByParameter(select);

							if (list_false != null && list_false.size() > 0) {
								temp = new HashMap();
								// 取出最小的一条处理
								Map map_flase = list_false.get(0);
								select = new HashMap();
								select.put(
										"CIR_OTN_CIRCUIT_ROUTE_ID",
										map_flase
												.get("CIR_OTN_CIRCUIT_ROUTE_ID"));
								select.put("CIR_OTN_CIRCUIT_ID",
										map_flase.get("CIR_OTN_CIRCUIT_ID"));
								// 查询出id小于当前id且
								List<Map> list_before = circuitManagerMapper
										.getOtnBefore(select);
								if (list_before != null
										&& list_before.size() > 0) {
									// 先查询出电路的信息
									select = hashMapSon(
											"t_cir_otn_circuit",
											"CIR_OTN_CIRCUIT_ID",
											list_before.get(0).get(
													"CIR_OTN_CIRCUIT_ID"),
											null, null, null);

									Map cir = circuitManagerMapper.getByParameter(select).get(0);
									
									// 插入一条otn电路信息
									insert = new HashMap();
									insert.put("A_END_CTP", cir.get("A_END_CTP"));
									insert.put("A_END_PTP", cir.get("A_END_PTP"));
									insert.put("CIR_COUNT", 1);

									insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
									// 新增cirNo
									Map map_maxNo_ = circuitManagerMapper
											.getMaxOtnCircuitNo();
									if (map_maxNo_ != null) {
										if (map_maxNo_.get("CIR_NO") != null
												&& !map_maxNo_.get("CIR_NO").toString()
														.isEmpty()) {
											insert.put(
													"CIR_NO",
													((Integer.parseInt(map_maxNo_.get(
															"CIR_NO").toString()) + 1) + ""));
										} else {
											insert.put("CIR_NO", "100000");
										}
									} else {
										insert.put("CIR_NO", "100000");
									}
									circuitManagerMapper.insertOtnInfo(insert);

									// 获取插入数据Id
									//Integer circuitInfoId_ = Integer.valueOf(insert.get(
									//		"CIR_OTN_CIRCUIT_INFO_ID").toString());

//									// 新建一条otn电路
//									insert = new HashMap();
//									insert.put("A_END_CTP", map_otn.get("A_END_CTP"));
//									insert.put("A_END_PTP", map_otn.get("A_END_PTP"));
//									insert.put("IS_MAIN_CIR", CommonDefine.TRUE);
//									insert.put("IS_COMPLETE_CIR", CommonDefine.FALSE);
//									insert.put("CIR_OTN_CIRCUIT_INFO_ID", circuitInfoId);
//									Map insertCir = insertOtnCir(insert);
									cir.put("CIR_OTN_CIRCUIT_INFO_ID", insert.get("CIR_OTN_CIRCUIT_INFO_ID"));
									// 插入一条新的电路
									Map insertCir = insertOtnCir(cir);
									
									

									// 遍历插入数据
									for (Map map_before : list_before) {
										insert = new HashMap();
										insert.put(
												"CIR_OTN_CIRCUIT_ID",
												insertCir
														.get("CIR_OTN_CIRCUIT_ID"));
										insert.put("IS_COMPLETE",
												CommonDefine.TRUE);
										insert.put("CHAIN_ID",
												map_before.get("CHAIN_ID"));
										insert.put("CHAIN_TYPE",
												map_before.get("CHAIN_TYPE"));
										insert.put("AHEAD_CRS_ID",
												map_before.get("AHEAD_CRS_ID"));
										circuitManagerMapper
												.insertOtnRoute(insert);
										// 经过数量加1
										update = new HashMap();
										update.put("NAME", "t_base_otn_crs");
										update.put("ID_NAME", "BASE_OTN_CRS_ID");
										update.put("ID_VALUE",
												map_otn.get("BASE_OTN_CRS_ID"));
										update.put("ID_NAME_2", "CIRCUIT_COUNT");
										update.put("ID_VALUE_2", Integer
												.parseInt(map_otn.get(
														"CIRCUIT_COUNT")
														.toString()) + 1);
										circuitManagerMapper
												.updateByParameter(update);

										// 判断是否是交叉连接，如果是交叉连接，则将az端的值赋给全局变量
										if (Integer.parseInt(map_before.get(
												"CHAIN_TYPE").toString()) == CommonDefine.CHAIN_TYPE_OTN_CRS) {
											select = hashMapSon(
													"t_base_otn_crs",
													"BASE_OTN_CRS_ID",
													map_before.get("CHAIN_ID"),
													null, null, null);
											Map crs = circuitManagerMapper
													.getByParameter(select)
													.get(0);
											// a端
											if(crs.get("A_OS")!=null&&!crs.get("A_OS").toString().isEmpty()){
												temp.put("OS", crs.get("A_OS"));
											}
											if(crs.get("A_OTS")!=null&&!crs.get("A_OTS").toString().isEmpty()){
												temp.put("OTS", crs.get("A_OTS"));
											}
											if(crs.get("A_OMS")!=null&&!crs.get("A_OMS").toString().isEmpty()){
												temp.put("OMS", crs.get("A_OMS"));
											}
											if(crs.get("A_OCH")!=null&&!crs.get("A_OCH").toString().isEmpty()){
												temp.put("OCH", crs.get("A_OCH"));
											}
											if(crs.get("A_ODU0")!=null&&!crs.get("A_ODU0").toString().isEmpty()){
												temp.put("ODU0", crs.get("A_ODU0"));
											}
											if(crs.get("A_ODU1")!=null&&!crs.get("A_ODU1").toString().isEmpty()){
												temp.put("ODU1", crs.get("A_ODU1"));
											}
											if(crs.get("A_ODU2")!=null&&!crs.get("A_ODU2").toString().isEmpty()){
												temp.put("ODU2", crs.get("A_ODU2"));
											}
											if(crs.get("A_ODU3")!=null&&!crs.get("A_ODU3").toString().isEmpty()){
												temp.put("ODU3", crs.get("A_ODU3"));
											}
											if(crs.get("A_DSR")!=null&&!crs.get("A_DSR").toString().isEmpty()){
												temp.put("DSR", crs.get("A_DSR"));
											}
											
																												
											// 如果oms 有2出现，则记录在OMS2
											if (crs.get("A_OMS") != null
													&& "2".equals(crs.get(
															"A_OMS").toString())) {
												temp.put("OMS2", 2);
											}
											// z端
											if(crs.get("Z_OS")!=null&&!crs.get("Z_OS").toString().isEmpty()){
												temp.put("OS", crs.get("Z_OS"));
											}
											if(crs.get("Z_OTS")!=null&&!crs.get("Z_OTS").toString().isEmpty()){
												temp.put("OTS", crs.get("Z_OTS"));
											}
											if(crs.get("Z_OMS")!=null&&!crs.get("Z_OMS").toString().isEmpty()){
												temp.put("OMS", crs.get("Z_OMS"));
											}
											if(crs.get("Z_OCH")!=null&&!crs.get("Z_OCH").toString().isEmpty()){
												temp.put("OCH", crs.get("Z_OCH"));
											}
											if(crs.get("Z_ODU0")!=null&&!crs.get("Z_ODU0").toString().isEmpty()){
												temp.put("ODU0", crs.get("Z_ODU0"));
											}
											if(crs.get("Z_ODU1")!=null&&!crs.get("Z_ODU1").toString().isEmpty()){
												temp.put("ODU1", crs.get("Z_ODU1"));
											}
											if(crs.get("Z_ODU2")!=null&&!crs.get("Z_ODU2").toString().isEmpty()){
												temp.put("ODU2", crs.get("Z_ODU2"));
											}
											if(crs.get("Z_ODU3")!=null&&!crs.get("Z_ODU3").toString().isEmpty()){
												temp.put("ODU3", crs.get("Z_ODU3"));
											}
											if(crs.get("Z_DSR")!=null&&!crs.get("Z_DSR").toString().isEmpty()){
												temp.put("DSR", crs.get("Z_DSR"));
											}
											if (crs.get("Z_OMS") != null
													&& "2".equals(crs.get(
															"Z_OMS").toString())) {
												temp.put("OMS2", 2);
											}
										}
										// 将最后一条记录赋值
										if (Integer.parseInt(map_before.get(
												"IS_COMPLETE").toString()) == CommonDefine.FALSE) {
											// 删除为完成的激励
											select = hashMapSon(
													"t_cir_otn_circuit_route",
													"CIR_OTN_CIRCUIT_ROUTE_ID",
													map_before
															.get("CIR_OTN_CIRCUIT_ROUTE_ID"),
													null, null, null);
											circuitManagerMapper
													.deleteByParameter(select);

											// 获取新增的记录
											select = new HashMap();
											select.put("NAME",
													"t_cir_otn_circuit_route");
											select.put("ID",
													"CIR_OTN_CIRCUIT_ROUTE_ID");
											select.put("ID_NAME",
													"CIR_OTN_CIRCUIT_ID");
											select.put("ID_VALUE", insertCir
													.get("CIR_OTN_CIRCUIT_ID"));

											Map map_latest = circuitManagerMapper
													.getLatestRecord(select);

											// 获取最新交叉连接
											select = hashMapSon(
													"t_base_otn_crs",
													"BASE_OTN_CRS_ID",
													map_latest.get("CHAIN_ID"),
													null, null, null);
											Map crs = circuitManagerMapper
													.getByParameter(select)
													.get(0);
											// 单条电路生成
											CreateSingleZteOtn(crs, temp,
													insertCir);
										}
									}
								}

							} else {
								isGoOn = false;
							}

							select = hashMapSon("t_cir_otn_circuit_route",
									"IS_COMPLETE", CommonDefine.FALSE, null,
									null, null);

							// 判断是否还存在未完成的虚拟交叉
							list_false = circuitManagerMapper
									.getByParameter(select);
							if (list_false != null && list_false.size() > 0) {
								isGoOn = true;
							} else {
								isGoOn = false;
							}
						} while (isGoOn);
						
						/*// 进度描述信息更改--此处修改
						String text = "当前进度" + (i * 1000 + j + 1 + countSdh)
								+ "/" + (countSdh + countOtn);
						if ("newCir".equals(id)) {
							text = (i * 1000 + j + 1 + countSdh) + "/"
									+ (countSdh + countOtn);
						}
						// 加入进度值
						CommonDefine.setProcessParameter(
								sessionId,
								id,
								text,
								Double.valueOf((i * 1000 + j + 1 + countSdh)
										/ ((double) (countSdh + countOtn))));*/
						CommonDefine.setProcessParameter(
								sessionId,
								id,
								(i * 1000 + j + 1 ),
								(int) (zteOtn*1.006+1),
								"中兴otn电路生成 ");

					}
				}else{
					CommonDefine.setProcessParameter(
							sessionId,
							id,
							(zteOtn),
							(int) (zteOtn*1.006+1),
							"中兴otn电路生成 ");
				}
			}
		} else {

			// 进度描述信息更改--此处修改
			/*String text = "当前进度0/0";
			if ("newCir".equals(id)) {
				text = "0/0";
			}
			// 加入进度值
			CommonDefine.setProcessParameter(sessionId, id, text, Double.valueOf(1));*/
			CommonDefine.setProcessParameter(sessionId, id, 0,1,"中兴otn电路生成 ");

		}
		return stop;
	}
	
	List<Map> getTun(List<Map> list,Map mapT){
		List<Map> listT = new ArrayList<Map>();
		List<Map> listT_ = new ArrayList<Map>();
		// 先取出隧道及以上信息
		for(Map map : list){
			// 判断是否是纬线
			if(map.get("A_END_CTP")!=null && map.get("A_END_CTP").toString().contains("pw=")){
				continue;
			}else{
				listT.add(map);
			}
		}
		if(listT!=null && listT.size()>0){
			if(mapT.get("BASE_PTP_ID").toString().equals(listT.get(0).get("A_END_PTP").toString())){
				return listT;
			}else{
				// 进行逆向排序
				for(int i =listT.size()-1;i>=0;i-- ){
					listT_.add(listT.get(i));
				}
				return listT_;
			}
		}else{
			return null;
		}
		
		
	}
	List<Map> getTunALu(List<Map> list,Map mapT){
		List<Map> listT = new ArrayList<Map>();
		List<Map> listT_ = new ArrayList<Map>();
		// 先取出隧道及以上信息
		for(Map map : list){
			// 判断是否是纬线
			if(map.get("A_END_CTP")!=null && (map.get("A_END_CTP").toString().contains("pw=")||map.get("A_END_CTP").toString().contains("PW"))){
				continue;
			}else{
				listT.add(map);
			}
		}
		if(listT!=null && listT.size()>0){
			if(mapT.get("BASE_PTP_ID").toString().equals(listT.get(0).get("A_END_PTP").toString())
					&& mapT.get("BASE_PTP_ID_Z").toString().equals(listT.get(listT.size()-1).get("A_END_PTP").toString())){
				return listT;
			}else if(mapT.get("BASE_PTP_ID").toString().equals(listT.get(listT.size()-1).get("A_END_PTP").toString())
					&& mapT.get("BASE_PTP_ID_Z").toString().equals(listT.get(0).get("A_END_PTP").toString())){
				// 进行逆向排序
				for(int i =listT.size()-1;i>=0;i-- ){
					listT_.add(listT.get(i));
				}
				return listT_;
			}else{
				return null;
			}
		}else{
			return null;
		}
		
		
	}
	
	
	/**
	 * 跨类型电路生成
	 * @return
	 */
	public boolean createTypeCir(){
		Map select = null;
		Map update = null;
		// 遍历link
		List<Map> listLink = circuitManagerMapper.selectLink();
		if(listLink!=null && listLink.size()>0){
			int  i = 0;
			for(Map link:listLink){
				System.out.println("================"+link.get("BASE_LINK_ID")+"------->"+i++);
				// 将az端分开遍历
				// 先确定是否存在a口
				select = new HashMap();
				select.put("A_END_PTP", link.get("A_END_PTP"));
				select.put("Z_END_PTP", link.get("Z_END_PTP"));
				List<Map> aPortList = circuitManagerMapper.selectPortA(select);
				// 如果存在a，则继续查找b，否则，跳过本次循环
				if(aPortList!=null && aPortList.size()>0){
					//查找b口
					select = new HashMap();
					select.put("A_END_PTP", link.get("A_END_PTP"));
					select.put("Z_END_PTP", link.get("Z_END_PTP"));
					List<Map> zPortList = circuitManagerMapper.selectPortZ(select);
					if(zPortList!=null && zPortList.size()>0){
						// 遍历a口，区分方向
						// 根据az口去查找电路
						
						List<Map> cirList = circuitManagerMapper.selectOtnCirByaz(aPortList,zPortList);
						if(cirList!=null && cirList.size()>0){
							// 如果是多条 拼接字符串
							String no ="";
							for(int k = 0 ; k <(cirList.size()-1);k++ ){
								no += cirList.get(k).get("CIR_NO").toString()+";";
							}
							no += cirList.get(cirList.size()-1).get("CIR_NO").toString();
							update = new HashMap();
							update.put("A_END_PTP", link.get("A_END_PTP"));
							update.put("Z_END_PTP", link.get("Z_END_PTP"));
							update.put("CIR_NO", no);
							circuitManagerMapper.updateLinkCir(update);
						}
//						for(Map mapPort:aPortList){
//							// 如果又多条电路怎么破？默认是一条，查询是完整电路
//							List<Map> cirList = circuitManagerMapper.selectOtnCirBya(Map);
//							if(cirList!=null && cirList.size()>0){
//								
//							}
//
//						}
					}
				}else{
					continue;
				}
			}
		}
		
//		

		
		return true;
		
	}
	
	
	// 贝尔ptn电路生成
	public boolean createAluPtnCir(Map mapSelect, String id, String sessionId,int userId){
		boolean stop = false;
		Map select = null;
		Map update = null;
		CommonDefine.setProcessParameter(sessionId,id,0,1,"贝尔ptn电路生成 ");
		// 全部置为未查询
		circuitManagerMapper.updateTableByColumn("t_base_ptn_fdfr_list", "IS_SELECT", 0, null, null);
		
		Map count = circuitManagerMapper.selectCountfromFdfrList(mapSelect,userId,CommonDefine.TREE.TREE_DEFINE);
		int total = Integer.parseInt(count.get("total").toString());
		for(int i = 0 ; i <total;i++){
			//
			if (CommonDefine.getIsCanceled(sessionId, id)) {
				CommonDefine.respCancel(sessionId, id);
				stop = true;
				break;
			}
			
			// 遍历t_base_ptn_fdfr_list 查找ptp_a
			List<Map> list_fdfr_one = circuitManagerMapper.selectOnefromFdfrList(mapSelect,userId,CommonDefine.TREE.TREE_DEFINE);
			
			if(list_fdfr_one!= null && list_fdfr_one.size()>0){
				// 将此记录更为已查询 
				//circuitManagerMapper.updateTableByColumn("t_base_ptn_fdfr_list", "IS_SELECT", 1, "BASE_PTN_FDFR_LIST_ID", list_fdfr_one.get(0).get("BASE_PTN_FDFR_LIST_ID"));
			
				// 在 t_base_ptn_fdfr_list 查找ptp_a,通过关联查询，将z端也查出来
				List<Map> list_fdfr_two = circuitManagerMapper.selectFdfrList(list_fdfr_one.get(0).get("BASE_PTN_FD_ID"));
				
				// 将查询出的a,z端数据也更新为已查询
				circuitManagerMapper.updateTableByColumn("t_base_ptn_fdfr_list", "IS_SELECT", 1, "BASE_PTN_FD_ID", list_fdfr_one.get(0).get("BASE_PTN_FD_ID"));

				// 判断是否为az两条数据，如果不是，则更新查询状态，并且跳过被刺循环
				if(list_fdfr_two!=null && list_fdfr_two.size() == 2){
					
					// 选取第一条作为a端，第二条作为z端
					Object ptpa = list_fdfr_two.get(0).get("BASE_PTP_ID");
					Object nea = list_fdfr_two.get(0).get("BASE_NE_ID");
					Object ptpz = list_fdfr_two.get(1).get("BASE_PTP_ID");
					Object nez = list_fdfr_two.get(1).get("BASE_NE_ID");
					// 去 link_fdr 表中查询 字段ptp_a 相同记录的ctp
					List<Map> list_link_fdfr = circuitManagerMapper.selectPtpfromlinkFdfr(list_fdfr_two.get(0).get("BASE_PTN_FD_ID"),nea);
					if(list_link_fdfr!=null && list_link_fdfr.size()>0){
						// 记录ctpa 的信息,先判断是哪一端符合
						Object ctpa = null;
						Object selectPtp = null;
						if(nea.toString().equals(list_link_fdfr.get(0).get("A_END_NE").toString())){
							 ctpa = list_link_fdfr.get(0).get("A_END_CTP");
							 selectPtp = list_link_fdfr.get(0).get("A_END_PTP");
						}else{
							 ctpa = list_link_fdfr.get(0).get("Z_END_CTP");
							 selectPtp = list_link_fdfr.get(0).get("Z_END_PTP");
						}
						
						Object ctpaId = null;
						select = new HashMap();
						select.put("NAME", ctpa);
						select.put("BASE_PTP_ID", selectPtp);
						List<Map> list_ptpa = circuitManagerMapper.getPtnCtp(select);
						if(list_ptpa!=null && list_ptpa.size()>0){
							ctpaId = list_ptpa.get(0).get("BASE_PTN_CTP_ID");
						}
						// 查找 snc 表，匹配指端  serviceObj
						List<Map> list_snc = circuitManagerMapper.selectTableByColumn("t_base_ptn_snc","NAME",list_link_fdfr.get(0).get("SERVER_OBJ"));
						if(list_snc!=null && list_snc.size()>0){
							// 计算出对端ctpz
							Object ctpz = null;
							Object ctpzId = null;
							select = new HashMap();
							if(ctpa.toString().equals(list_snc.get(0).get("A_END_CTP").toString())){
								ctpz = list_snc.get(0).get("Z_END_CTP");
								select.put("BASE_PTP_ID", list_snc.get(0).get("Z_END_PTP"));
							}else{
								ctpz = list_snc.get(0).get("A_END_CTP");
								select.put("BASE_PTP_ID", list_snc.get(0).get("A_END_PTP"));
							}
							// 查询ctpz的id
							select.put("NAME", ctpz);							
							List<Map> list_ptpz = circuitManagerMapper.getPtnCtp(select);
							if(list_ptpz!=null && list_ptpz.size()>0){
								ctpzId = list_ptpz.get(0).get("BASE_PTN_CTP_ID");
							}
							//获取对端ctp——b以及snc_tuid,根据tuid 查询隧道信息
							List<Map>  list_tunl = circuitManagerMapper.selectTableByColumn("t_base_ptn_snc","NAME","TU_"+list_snc.get(0).get("BELONG_SNC").toString());
							if(list_tunl!=null && list_tunl.size()>0){
								// 获取base_ptn_snc_id,去 route表查询，并且按顺序排列
								List<Map> listTT = circuitManagerMapper.selectTableByColumn("t_base_ptn_route","NAME",list_tunl.get(0).get("NAME"));
								if(listTT!=null && listTT.size()>0){
									// 要注意route是否按序排列
									
									// 进入正常ptn电路生成流程

									//-----------
									List<Map> listRoute = new ArrayList<Map>();
									
									// 创建电路的起始端,
									Map insertPtnInfo = new HashMap();
									// 设置起始口
									insertPtnInfo.put("A_END_PTP", ptpa);
									insertPtnInfo.put("IS_COMPLETE_CIR", 0);
									
									// 具体的每条电路
									Map insertPtnCir = new HashMap();
									insertPtnCir.put("A_END_PTP", ptpa);
									insertPtnCir.put("A_END_CTP", ctpaId);
									
									
									// 根据起点，生成电路,将电路起始端口座位一条路由
									Map insertPtnRoutePtp = new HashMap();
									// 端口
									//insertPtnRoutePtp.put("CIR_CIRCUIT_ID", "");
									insertPtnRoutePtp.put("CHAIN_ID", ptpa);
									insertPtnRoutePtp.put("CHAIN_TYPE", 1);
									listRoute.add(insertPtnRoutePtp);
									
									// 第二跳是纬线
									Map insertPtnRoutePw = new HashMap();
									//insertPtnRoutePw.put("CIR_CIRCUIT_ID", "");
									insertPtnRoutePw.put("CHAIN_ID", ctpaId);
									insertPtnRoutePw.put("CHAIN_TYPE", 2);
									listRoute.add(insertPtnRoutePw);
									// pw 和隧道的关系未给出
									Map mapTunel = new HashMap();
									// a 端所属隧道口应与ptpa属于相同的网元
									List<Map>  list_ptp = circuitManagerMapper.selectTableByColumn("t_base_ptp","BASE_PTP_ID",list_tunl.get(0).get("A_END_PTP"));
									if(list_ptp!=null &&list_ptp.size()>0){
										if(nea.toString().equals(list_ptp.get(0).get("BASE_NE_ID").toString())){
											mapTunel.put("BASE_PTP_ID", list_tunl.get(0).get("A_END_PTP"));
											mapTunel.put("BASE_PTP_ID_Z", list_tunl.get(0).get("Z_END_PTP"));
										}else{
											mapTunel.put("BASE_PTP_ID", list_tunl.get(0).get("Z_END_PTP"));
											mapTunel.put("BASE_PTP_ID_Z", list_tunl.get(0).get("A_END_PTP"));
										}
									}else{
										// 进入下一次循环
										continue;
									}
									
									List<Map> list_route = getTunALu(listTT,mapTunel);
									// 确认route信息中是否含有pw信息
									
									Object ptp = null;
									Object lastPtpId = null;	
									boolean	isBeginPw = false;
									boolean isLinkOppsite = false;
									boolean isnormal = true;
									if(list_route!=null && list_route.size()>0){
										
									}else{
										// 如果az 隧道口和路由中的隧道口不相符合，结束本次循环
										continue;
									}
									for(int j = 0 ; j<list_route.size();j++){
										isLinkOppsite = false;
										// 遍历路由，插入ptn路由表
										// 先判断获取的路由是否是pw，如果是跳过										
										// 如果存在纬线，则 向后再
										//判断是否存在link联系
										if(j>=1){
											select = new HashMap();
											select.put("A_END_PTP", ptp);
											select.put("Z_END_PTP", list_route.get(j).get("A_END_PTP"));
											List<Map> linklist = circuitManagerMapper.getlinkByAZ(select);
											if(linklist!=null&&linklist.size()>0){
												// 将电路插入
												Map insertPtnlink = new HashMap();
												//insertPtnlink.put("CIR_CIRCUIT_ID", "");
												insertPtnlink.put("CHAIN_ID", linklist.get(0).get("BASE_LINK_ID"));
												insertPtnlink.put("CHAIN_TYPE", 3);
												listRoute.add(insertPtnlink);
											}else{
												// az互换
												select = new HashMap();
												select.put("A_END_PTP", ptp);
												select.put("Z_END_PTP", list_route.get(j).get("Z_END_PTP"));
												List<Map> linklist_ = circuitManagerMapper.getlinkByAZ(select);
												if(linklist_!=null&&linklist_.size()>0){
													isLinkOppsite = true;
													// 将电路插入
													Map insertPtnlink = new HashMap();
													//insertPtnlink.put("CIR_CIRCUIT_ID", "");
													insertPtnlink.put("CHAIN_ID", linklist_.get(0).get("BASE_LINK_ID"));
													insertPtnlink.put("CHAIN_TYPE", 3);
													listRoute.add(insertPtnlink);
												}else{
													// 结束电路查找
													isnormal = false;
													break;
												}					
											}
										}
										
										
										// 判断是否是隧道,z端为空，就插入一条，否则插入两条
										if(list_route.get(j).get("Z_END_CTP") == null){
											// 根据ptp 和ctp 去ptnctp表中查出对应id
											select = new HashMap();
											select.put("BASE_PTP_ID", list_route.get(j).get("A_END_PTP"));
											select.put("NAME", list_route.get(j).get("A_END_CTP"));
											List<Map> lista = circuitManagerMapper.getPtnCtp(select);
											if(lista!=null && lista.size()>0){
												Map insertPtnRoute = new HashMap();
												//insertPtnRoute.put("CIR_CIRCUIT_ID", "");
												insertPtnRoute.put("CHAIN_ID", lista.get(0).get("BASE_PTN_CTP_ID"));
												insertPtnRoute.put("CHAIN_TYPE", 4);
												insertPtnRoute.put("ROUTE_ID", list_route.get(j).get("BASE_PTN_ROUTE_ID"));
												listRoute.add(insertPtnRoute);
											}
											
											ptp =list_route.get(j).get("A_END_PTP")  ;// a 端口
											lastPtpId = list_route.get(j).get("A_END_PTP");
											
										}else{
											if(!isLinkOppsite){
												ptp = list_route.get(j).get("Z_END_PTP"); // z端口
												lastPtpId = list_route.get(j).get("Z_END_PTP");
												// 先放a端
												select = new HashMap();
												select.put("BASE_PTP_ID", list_route.get(j).get("A_END_PTP"));
												select.put("NAME", list_route.get(j).get("A_END_CTP"));
												List<Map> lista = circuitManagerMapper.getPtnCtp(select);
												if(lista!=null && lista.size()>0){
													Map insertPtnRouteA = new HashMap();
													//insertPtnRouteA.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteA.put("CHAIN_ID", lista.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteA.put("CHAIN_TYPE", 4);
													insertPtnRouteA.put("ROUTE_ID", list_route.get(j).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteA);
												}else{
													System.out.println("insertPtnRouteA...");
													isnormal = false;
													break;
												}
												
												// 再放z端
												select = new HashMap();
												select.put("BASE_PTP_ID", list_route.get(j).get("Z_END_PTP"));
												select.put("NAME", list_route.get(j).get("Z_END_CTP"));
												List<Map> listz = circuitManagerMapper.getPtnCtp(select);
												if(listz!=null && listz.size()>0){
													Map insertPtnRouteZ = new HashMap();
													//insertPtnRouteZ.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteZ.put("CHAIN_ID", listz.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteZ.put("CHAIN_TYPE", 4);
													insertPtnRouteZ.put("ROUTE_ID", list_route.get(j).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteZ);
												}else{
													System.out.println("insertPtnRouteZ...");
													isnormal = false;
													break;
												}
											}else{
												ptp = list_route.get(j).get("A_END_PTP"); // a端口
												lastPtpId = list_route.get(j).get("A_END_PTP");
												// 先放z端
												select = new HashMap();
												select.put("BASE_PTP_ID", list_route.get(j).get("Z_END_PTP"));
												select.put("NAME", list_route.get(j).get("Z_END_CTP"));
												List<Map> listz = circuitManagerMapper.getPtnCtp(select);
												if(listz!=null && listz.size()>0){
													Map insertPtnRouteZ = new HashMap();
													//insertPtnRouteZ.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteZ.put("CHAIN_ID", listz.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteZ.put("CHAIN_TYPE", 4);
													insertPtnRouteZ.put("ROUTE_ID", list_route.get(j).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteZ);
												}else{
													System.out.println("insertPtnRouteZa...");
													isnormal = false;
													break;
												}
												
												// 再放a端
												select = new HashMap();
												select.put("BASE_PTP_ID", list_route.get(j).get("A_END_PTP"));
												select.put("NAME", list_route.get(j).get("A_END_CTP"));
												List<Map> lista = circuitManagerMapper.getPtnCtp(select);
												if(lista!=null && lista.size()>0){
													Map insertPtnRouteA = new HashMap();
													//insertPtnRouteA.put("CIR_CIRCUIT_ID", "");
													insertPtnRouteA.put("CHAIN_ID", lista.get(0).get("BASE_PTN_CTP_ID"));
													insertPtnRouteA.put("CHAIN_TYPE", 4);
													insertPtnRouteA.put("ROUTE_ID", list_route.get(j).get("BASE_PTN_ROUTE_ID"));
													listRoute.add(insertPtnRouteA);
												}else{
													System.out.println("insertPtnRouteAz...");
													isnormal = false;
													break;
												}
											}	
										}	
									}
												
									// 如果电路非正常结束，则跳过本次循环
									if(!isnormal){
										continue;
									}
									// 查找结束纬线													
									// 查找结束纬线,验证纬线是否合格
								
									Map insertLastPw = new HashMap();
									//insertLastPw.put("CIR_CIRCUIT_ID", "");
									insertLastPw.put("CHAIN_ID", ctpzId);
									insertLastPw.put("CHAIN_TYPE", 2);
									listRoute.add(insertLastPw);
									
									// ptn 电路纬线id
									insertPtnCir.put("Z_END_CTP", ctpzId);
									
									
									// 查找结束ptp
									// 根据pw号反推ptp号，需要确认会不会有夸不同网元类型的电路
									
									Map insertLastPtp = new HashMap();
									//insertLastPtp.put("CIR_CIRCUIT_ID", "");
									insertLastPtp.put("CHAIN_ID", ptpz);
									insertLastPtp.put("CHAIN_TYPE", 1);
									listRoute.add(insertLastPtp);
									
									// 电路z端节点
									insertPtnCir.put("Z_END_PTP", ptpz);
									insertPtnInfo.put("Z_END_PTP", ptpz);
									insertPtnInfo.put("IS_COMPLETE_CIR", 1);
																
								
									// 统一插入数据
									//先判断电路是否完整
									//if("1".equals(insertPtnInfo.get("IS_COMPLETE_CIR").toString())){
										// 先查看是否已经存在电路，有就更新，没有插入
									select = new HashMap();
									select.put("A_END_PTP", insertPtnInfo.get("A_END_PTP"));
									select.put("Z_END_PTP", insertPtnInfo.get("Z_END_PTP"));
									select.put("IS_COMPLETE_CIR", insertPtnInfo.get("IS_COMPLETE_CIR"));
									List<Map> listPtnIn = circuitManagerMapper.getPtnInfo(select);
									Object infoId = null;
									Object cirId = null;
									if(listPtnIn!=null && listPtnIn.size()>0){
										insertPtnInfo.put("CIR_CIRCUIT_INFO_ID", listPtnIn.get(0).get("CIR_CIRCUIT_INFO_ID"));
										update = new HashMap();
										update.put("CIR_CIRCUIT_INFO_ID", infoId);
										circuitManagerMapper.updatePtnInfo(update);
									}else{
										Map map_maxNo = circuitManagerMapper.getMaxPtnCircuitNo();
										if (map_maxNo != null) {
											if (map_maxNo.get("CIR_NO") != null&& !map_maxNo.get("CIR_NO").toString().isEmpty()) {
												insertPtnInfo.put("CIR_NO",((Integer.parseInt(map_maxNo.get("CIR_NO").toString()) + 1) + ""));
											} else {
												insertPtnInfo.put("CIR_NO", "100000");
											}
										} else {
											insertPtnInfo.put("CIR_NO", "100000");
										}
										 circuitManagerMapper.insertPtnInfo(insertPtnInfo);
									}
									
									// 插入ptncir 表
									insertPtnCir.put("CIR_CIRCUIT_INFO_ID", insertPtnInfo.get("CIR_CIRCUIT_INFO_ID"));
									circuitManagerMapper.insertPtnCir(insertPtnCir);
									
									// 遍历  插入路由表
									if(listRoute!=null && listRoute.size()>0){
										for(int j = 0 ; j<listRoute.size();j++){
											Map ma = listRoute.get(j);
											ma.put("CIR_CIRCUIT_ID", insertPtnCir.get("CIR_CIRCUIT_ID"));
											ma.put("SEQUENCE", (j+1));
											circuitManagerMapper.insertPtnRoute(ma);
										}
									}
									
									// 更新已经用于电路生成的两条数据,设为已用作电路生成
									circuitManagerMapper.updateTableByColumn("t_base_ptn_fdfr_list", "IS_USE", 1, "BASE_PTN_FD_ID", list_fdfr_two.get(0).get("BASE_PTN_FD_ID"));
								
										
									//}
									
									
								//-----------
									
								}
							}
						}
					}
				}else{
					// 进入下一次循环
					continue;
				}
				
			}
			CommonDefine.setProcessParameter(sessionId,id,(i + 1 ),(int) (total*1.006+1),"贝尔ptn电路生成 ");
		}
		
		
		return stop;
		
	}
	
	// 根据基础数据的变化，将变化过的电路删除电路
	public boolean deleteAllCirAbout(String id,String sessionId,int userId) throws Exception{
		// 未完成各种逻辑
		
		// 1.sdh电路相关删除 涉及表  t_base_sdh_crs， t_base_link，
		// t_cir_circuit，t_cir_circuit_info，t_cir_circuit_route
		
		// 设立保护机制，如果10 分钟内，符合条件的数据没有发生变化，则进行电路删除，否则暂停删除
		
		/***********sdh交叉开始****************************************************/
		
		// 先查看sdh交叉连接
		Map sdh_crs_before = circuitManagerMapper.selectCountTableByColumn("t_base_sdh_crs", "IS_DEL", 1);
		// 暂停十分钟 
		//Thread.sleep(600000);
		// 10 10 分钟后再次查询
		Map sdh_crs_after = circuitManagerMapper.selectCountTableByColumn("t_base_sdh_crs", "IS_DEL", 1);
		
		if(sdh_crs_before.get("total").toString().equals(sdh_crs_after.get("total").toString())){
			// 根据交叉查询
			int sdh_crs_total = Integer.parseInt(sdh_crs_before.get("total").toString());
			// 遍历每条交叉，进行电路回退
			for(int i = 0 ;i < sdh_crs_total; i++){
				Map map_one = circuitManagerMapper.selectTableByColumnForOne("t_base_sdh_crs", "IS_DEL", 1);
				// 根据交叉id查询出相关电路
				List<Map> list_delete_cir  = circuitManagerMapper.selectSdhDeleteCirByCrs(map_one.get("BASE_SDH_CRS_ID"));
				if(list_delete_cir!=null && list_delete_cir.size()>0){
					for(Map de_cir : list_delete_cir ){
						// 删除路由
						circuitManagerMapper.deleteTableByColumn("t_cir_circuit_route", "CIR_CIRCUIT_ID", de_cir.get("CIR_CIRCUIT_ID"));
						
						// 编写路由触发器删除电路
						
						// 编写电路触发器，更新info表
					}
					
				}
				// 删除当前交叉连接
				circuitManagerMapper.deleteTableByColumn("t_base_sdh_crs", "BASE_SDH_CRS_ID", map_one.get("BASE_SDH_CRS_ID"));
				
			}	
			
		}
		
		/********sdh交叉结束**************************************************************/
		
		/********otn交叉开始**************************************************************/
		
		Map otn_crs_before = circuitManagerMapper.selectCountTableByColumn("t_base_otn_crs", "IS_DEL", 1);
		// 暂停十分钟 
		//Thread.sleep(600000);
		// 10 10 分钟后再次查询
		Map otn_crs_after = circuitManagerMapper.selectCountTableByColumn("t_base_otn_crs", "IS_DEL", 1);
		
		if(otn_crs_before.get("total").toString().equals(otn_crs_after.get("total").toString())){
			// 根据交叉查询
			int otn_crs_total = Integer.parseInt(otn_crs_before.get("total").toString());
			// 遍历每条交叉，进行电路回退
			for(int i = 0 ;i < otn_crs_total; i++){
				Map map_one = circuitManagerMapper.selectTableByColumnForOne("t_base_otn_crs", "IS_DEL", 1);
				// 根据交叉id查询出相关电路
				List<Map> list_delete_cir  = circuitManagerMapper.selectOtnDeleteCirByCrs(map_one.get("BASE_OTN_CRS_ID"));
				if(list_delete_cir!=null && list_delete_cir.size()>0){
					for(Map de_cir : list_delete_cir ){
						// 删除otn路由
						circuitManagerMapper.deleteTableByColumn("t_cir_otn_circuit_route", "CIR_OTN_CIRCUIT_ID", de_cir.get("CIR_OTN_CIRCUIT_ID"));
						
						// 编写路由触发器删除电路 otn
						
						// 编写电路触发器，更新info表 otn
					}
					
				}
				// 删除当前交叉连接
				circuitManagerMapper.deleteTableByColumn("t_base_otn_crs", "BASE_OTN_CRS_ID", map_one.get("BASE_OTN_CRS_ID"));
				
			}	
			
		}		
		
		/********otn交叉结束**************************************************************/
		/********link开始**************************************************************/
		
		Map link_before = circuitManagerMapper.selectCountTableByColumn("t_base_link", "IS_DEL", 1);
		// 暂停十分钟 
		//Thread.sleep(600000);
		// 10 10 分钟后再次查询
		Map link_after = circuitManagerMapper.selectCountTableByColumn("t_base_link", "IS_DEL", 1);
		if(link_before.get("total").toString().equals(link_after.get("total").toString())){
			// 根据link
			int link_total = Integer.parseInt(link_before.get("total").toString());
			// 遍历每条交叉，进行电路回退
			for(int i = 0 ;i < link_total; i++){
				Map map_one = circuitManagerMapper.selectTableByColumnForOne("t_base_link", "IS_DEL", 1);
				System.out.println("1");
				// 根据link查询出相关sdh电路
				List<Map> sdh_delete_cir  = circuitManagerMapper.selectSdhDeleteCirByLink(map_one.get("BASE_LINK_ID"));
				if(sdh_delete_cir!=null && sdh_delete_cir.size()>0){
					for(Map de_cir : sdh_delete_cir ){
						// 删除sdh路由
						circuitManagerMapper.deleteTableByColumn("t_cir_circuit_route", "CIR_CIRCUIT_ID", de_cir.get("CIR_CIRCUIT_ID"));
						
						// 编写路由触发器删除电路 otn
						
						// 编写电路触发器，更新info表 otn
					}
					
				}
				// 根据link查询出相关otn电路
				List<Map> otn_delete_cir  = circuitManagerMapper.selectOtnDeleteCirByLink(map_one.get("BASE_LINK_ID"));
				if(otn_delete_cir!=null && otn_delete_cir.size()>0){
					for(Map de_cir : otn_delete_cir ){
						// 删除otn路由
						circuitManagerMapper.deleteTableByColumn("t_cir_otn_circuit_route", "CIR_OTN_CIRCUIT_ID", de_cir.get("CIR_OTN_CIRCUIT_ID"));
						
						// 编写路由触发器删除电路 otn
						
						// 编写电路触发器，更新info表 otn
					}
					
				}
				
				// 根据link 查询 其他各种电路
				// 未完成
				// 删除当前link连接
				circuitManagerMapper.deleteTableByColumn("t_base_link", "BASE_LINK_ID", map_one.get("BASE_LINK_ID"));
				
			}
		}
		
		/********link结束**************************************************************/
		
		/********ptn开始**************************************************************/
		// ptn 全部删除重新生成
		/**删除代码           DELETE FROM t_cir_ptn_circuit_route;
					DELETE FROM t_cir_ptn_circuit;
					DELETE FROM t_cir_ptn_circuit_info;
					DELETE FROM t_base_ptn_temp;
					UPDATE t_base_ptn_fdfr_list SET is_use = 0;*/
		circuitManagerMapper.deleteTableByColumn("t_cir_ptn_circuit_route", null, null);
		circuitManagerMapper.deleteTableByColumn("t_cir_ptn_circuit", null, null);
		//circuitManagerMapper.deleteTableByColumn("t_cir_ptn_circuit_info", null, null);
		//circuitManagerMapper.updateTableByColumn("t_cir_ptn_circuit_info", "cir_count", 0, null, null);
		circuitManagerMapper.deleteTableByColumn("t_base_ptn_temp", null, null);
		circuitManagerMapper.updateTableByColumn("t_base_ptn_fdfr_list", "is_use", 0, null, null);
		Map mapSelect = new HashMap();
		mapSelect.put("NAME", "");
		// 中兴ptn 删除再生成
		createAluPtnCir(mapSelect, id, sessionId,userId);

		// 贝尔Ptn 删除再生成
		createZtePtnCircuit(mapSelect,id,sessionId, 0, 0,userId);
		
		/********ptn结束**************************************************************/
		
		/********eth开始**************************************************************/
		
		// 删除以太网电路,更新电路时需要注意,要同时更新以太网电路和以太网子电路的相关数据
		circuitManagerMapper.updateEthTableByColumn("t_cir_circuit_info", "PARENT_CIR", 0, "PARENT_CIR", 1);
		circuitManagerMapper.updateEthCir("t_cir_circuit_info", "cir_count", 0, "PARENT_CIR", 0);
		// 测试添加代码，真是环境，数据会删掉重新生成
		// circuitManagerMapper.updateTableByColumn("t_cir_temp", "is_use", 0, null, null);// 生成中兴以太网电路
		
		// 中兴U31以太网
		createU31Eth(mapSelect,id, sessionId,userId);
		
		// 中兴E300以太网电路生成
		createZTECir(mapSelect,id, sessionId,userId);
		
		// 生成华为eth电路
		Map select = null;
		// 查询infoA表，判断电路是否是以太网电路
		
		int count = circuitManagerMapper.getHwEthFromInfoCount(select);
		for(int i = 0;i <(count/2000+1);i++){
			List<Map> listEth = circuitManagerMapper.getHwEthFromInfo(select,i*2000,2000);
			if(listEth!=null && listEth.size()>0){
				for (Map mapEth : listEth) {
					// 查询当前值的ctp
					Map ctpa = circuitManagerMapper.selectTableByColumn("t_base_sdh_ctp", "BASE_SDH_CTP_ID", mapEth.get("A_END_CTP")).get(0);
					int isEtha = 0;
					if (ctpa.get("IS_ETH") != null) {
						isEtha = Integer.parseInt(ctpa.get("IS_ETH").toString());
					}
					int isEthz = 0;
					if(mapEth.get("Z_END_CTP")!=null){
						Map ctpz = circuitManagerMapper.selectTableByColumn("t_base_sdh_ctp", "BASE_SDH_CTP_ID", mapEth.get("Z_END_CTP")).get(0);						
						if (ctpz.get("IS_ETH") != null) {
							isEthz = Integer.parseInt(ctpz.get("IS_ETH").toString());
						}
					}
					createETHCircuit(mapEth, isEtha, isEthz);
				}	
			}
			
		}
		
		/********eth结束**************************************************************/
		
		return false;
		
	}
}

