package com.fujitsu.job;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.performanceManager.service.PerformanceManagerService;
import com.fujitsu.util.FileWriterUtil;
import com.fujitsu.util.SpringContextUtil;

public class ManualCollectJob implements Job {

	private PerformanceManagerMapper performanceManagerMapper;
//	private IDataCollectServiceProxy dataCollectService;
//	private IQuartzManagerService quartzManagerService;
	private Timestamp date;

	public ManualCollectJob() {
		performanceManagerMapper = (PerformanceManagerMapper) SpringContextUtil
				.getBean("performanceManagerMapper");
//		quartzManagerService = (IQuartzManagerService) SpringContextUtil
//				.getBean("quartzManagerService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// 网管ID
		int emsConnectionId = Integer.parseInt(context.getJobDetail()
				.getJobDataMap().get("BASE_EMS_CONNECTION_ID").toString());
		int taskId = performanceManagerMapper.getTaskIdFromEmsId(
				emsConnectionId,
				PerformanceManagerService.RegularPmAnalysisDefine);
		
		Map jobMap=context.getJobDetail().getJobDataMap();
		if(jobMap.containsKey("COLLECT_DATE")){
			date = new Timestamp(((Date)jobMap.get("COLLECT_DATE")).getTime());
		}else{
			date = new Timestamp(System.currentTimeMillis());
		}

		/*Timestamp lastCollectTime = performanceManagerMapper
				.getTaskLastStartTime(taskId);
		try {
			if (lastCollectTime != null
					&& getDateWithoutTime(today).getTime() == getDateWithoutTime(
							lastCollectTime).getTime()) {
				// 一天内多次执行
				//return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}*/

		// 查询采集任务所需的网管信息
		List<Map> ems = performanceManagerMapper.getEmsCollectInfo(
				emsConnectionId, CommonDefine.TRUE);
		String startTime = (String) ems.get(0).get("COLLEC_START_TIME");
		String endTime = (String) ems.get(0).get("COLLEC_END_TIME");
		int collectSource = (Integer) ems.get(0).get("COLLECT_SOURCE");

//		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");// 判断超时用时间转换
		// 检查任务是否在非允许时间开始执行，对付miss fire
		/*if (endTime.compareTo(startTime) < 0) {
			// 任务开始时间晚于结束时间，应为第二天
			if (timeFormat.format(today).compareTo(startTime) < 0
				&& timeFormat.format(today).compareTo(endTime) >= 0) {
				//endTime<当前<startTime,禁止区间startTime~endTime
				return;
			}
		}else{
			if (timeFormat.format(today).compareTo(startTime) < 0
				|| timeFormat.format(today).compareTo(endTime) >= 0) {
				//endTime<当前 或 当前<startTime,禁止区间00:00~startTime,endTime~24:00
				return;
			}
		}*/

		try {
			// 任务开始时间和结束时间哪个大？
			Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(date);
			Date endDate;
			String dayString = "";
			if (endTime.compareTo(startTime) < 0) {
				// 任务开始时间晚于结束时间，应为第二天
				endDateCal.add(Calendar.DAY_OF_YEAR, 1);
				dayString = new SimpleDateFormat(
						CommonDefine.COMMON_SIMPLE_FORMAT).format(endDateCal
						.getTime());
				dayString = dayString + " " + endTime + ":00";

			} else {
				dayString = new SimpleDateFormat(
						CommonDefine.COMMON_SIMPLE_FORMAT).format(endDateCal
						.getTime());
				dayString = dayString + " " + endTime + ":00";
			}

			try {
				endDate = new SimpleDateFormat(CommonDefine.COMMON_FORMAT)
						.parse(dayString);
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}

			// 任务下次执行时间和开始时间更新
//			Timestamp nextTime = new Timestamp(((Date) quartzManagerService
//					.getJobInfo(CommonDefine.PM.PM_TASK_TYPE, taskId).get(
//							"nextFireTime")).getTime());
//			performanceManagerMapper.updateTaskTime(
//					new Timestamp(System.currentTimeMillis()), null, nextTime,
//					taskId);
			IDataCollectServiceProxy dataCollectService = SpringContextUtil
					.getDataCollectServiceProxy(emsConnectionId);

			// 标记网管为正在执行
			performanceManagerMapper.updateEmsCollectStatus(taskId,
					CommonDefine.PM.COLLECT_STATUS.EXECUTING);

			// 先采集必须采集的网元
			getPMDate(emsConnectionId, taskId, endDate, collectSource,
					CommonDefine.PM.NE_LEVEL.KEY_COLLECT,dataCollectService);
			// 再采集尽量采
			getPMDate(emsConnectionId, taskId, endDate, collectSource,
					CommonDefine.PM.NE_LEVEL.CYCLE_COLLECT,dataCollectService);

		} catch (CommonException e) {
			// 无法连接采集服务器采集失败,所有必须采网元设为失败,网管采集状态为失败╮(╯-╰)╭
			List<Map> neList = performanceManagerMapper.getNeCollectList(
					emsConnectionId, CommonDefine.PM.NE_LEVEL.KEY_COLLECT,
					CommonDefine.TRUE);
			storeFailedNes(0, neList, taskId, emsConnectionId, new Timestamp(
					System.currentTimeMillis()),
					MessageHandler.getErrorMessage(e.getErrorCode()));
			e.printStackTrace();
		} finally {
			// 比较采集失败的网元数是否为鸭蛋,鸭蛋则为成功,否则部分成功(→_←)
			// 任务结束时间
			try {
				performanceManagerMapper.updateTaskTime(null, new Timestamp(
						System.currentTimeMillis()), null, taskId);
				Timestamp todayWithoutTime = new Timestamp(getDateWithoutTime(
						date).getTime());
				Integer succeedCollected = performanceManagerMapper
						.getNeResultCount(todayWithoutTime,
								CommonDefine.SUCCESS, taskId);
				Integer failedCollected = performanceManagerMapper
						.getNeResultCount(todayWithoutTime,
								CommonDefine.FAILED, taskId);
				if (failedCollected == 0) {
					performanceManagerMapper.updateEmsCollectStatus(taskId,
							CommonDefine.PM.COLLECT_STATUS.SUCCESS);
				} else if (succeedCollected == 0 && failedCollected > 0) {
					performanceManagerMapper.updateEmsCollectStatus(taskId,
							CommonDefine.PM.COLLECT_STATUS.FAILED);
				} else {
					performanceManagerMapper.updateEmsCollectStatus(taskId,
							CommonDefine.PM.COLLECT_STATUS.PARTLY);
				}
			} catch (Exception e) {
				ExceptionHandler.handleException(e);
			}
		}
	}

	// 采集
	private void getPMDate(int emsConnectionId, int taskId, Date endDate,
			int collectSource, int neLevel,IDataCollectServiceProxy dataCollectService) {

		// 获取待采集的网元列表
		List<Map> neList = performanceManagerMapper.getNeCollectList(
				emsConnectionId, neLevel, CommonDefine.TRUE);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");// 判断超时用时间转换

		for (int i = 0; i < neList.size(); i++) {
			Map ne = neList.get(i);
			checkPause(emsConnectionId, taskId);

			Timestamp now = new Timestamp(System.currentTimeMillis());
			// 是否超时?超时break
//			if (now.getTime() >= endDate.getTime()) {
//				if (neLevel == CommonDefine.PM.NE_LEVEL.KEY_COLLECT) {
//					// 未采的必须采网元都是采集失败
//					storeFailedNes(i, neList, taskId, emsConnectionId, now,
//							"超时");
//				}
//				break;
//			}

			// 执行中被挂起则中止
			Integer taskStatus = performanceManagerMapper
					.getTaskCollectStatus(taskId);
			if (taskStatus == CommonDefine.PM.TASK_STATUS.SUSPEND) {
				if (neLevel == CommonDefine.PM.NE_LEVEL.KEY_COLLECT) {
					// 未采的必须采网元都是采集失败
					storeFailedNes(i, neList, taskId, emsConnectionId, now,
							"任务挂起,采集中止");
				}
				break;
			}

			// 获取两次采集间隔时间
			Integer collectInterval = getCollectInterval(ne, now);
			try {
				// 更新网元状态为进行中
				performanceManagerMapper.updateNeCollectInfo(
						(Integer) ne.get("BASE_NE_ID"), "进行中", collectInterval,
						now);

				List<PmDataModel> pmList = new ArrayList<PmDataModel>();
				// 此操作可能因等待网管侧正常而耗费极长时间
				String success = getPmList(pmList, collectSource, ne, now,
						timeFormat, endDate,dataCollectService);

				now = new Timestamp(System.currentTimeMillis());

				// 是否超时?超时break
//				if (now.getTime() >= endDate.getTime()) {
//					if (neLevel == CommonDefine.PM.NE_LEVEL.KEY_COLLECT) {
//						// 未采的必须采网元都是采集失败
//						storeFailedNes(i, neList, taskId, emsConnectionId, now,
//								"超时");
//					} else {
//						// 当前网元为循环采且超时
//						storeNeInfo(Integer.valueOf(ne.get("BASE_NE_ID")
//								.toString()), now, taskId, emsConnectionId,
//								"超时", CommonDefine.FAILED, collectInterval);
//					}
//					break;
//				}

				if ("SUCCESS".equals(success)) {
					if (pmList.size() > 0) {
						// 采集成功
						// 拼表名
						Date rtrvTime = pmList.get(0).getRetrievalTimeDisplay();
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyy_MM");
						String tableName = CommonDefine.PM.PM_TABLE_NAMES.ORIGINAL_DATA
								+ "_"
								+ emsConnectionId
								+ "_" + formatter.format(rtrvTime);
						// 建表
						performanceManagerMapper
								.createPmTableIfNotExist(tableName);
						// 存为txt文件
						Calendar rtrvTimeCal = Calendar.getInstance();
						rtrvTimeCal.setTime(new Date());
						String filePath = FileWriterUtil.BASE_FILE_PATH
								+ "PM_DATA_" + taskId+"_"+rtrvTimeCal.get(Calendar.YEAR)
								+ "_" + rtrvTimeCal.get(Calendar.MONTH) + "_"
								+ rtrvTimeCal.get(Calendar.DAY_OF_MONTH) + "_"
								+ rtrvTimeCal.get(Calendar.HOUR_OF_DAY) + "_"
								+ rtrvTimeCal.get(Calendar.MINUTE) + "_"
								+ rtrvTimeCal.get(Calendar.SECOND) + ".txt";
						FileWriterUtil.writeToTxtPm(filePath, pmList);
						// load到数据库
						performanceManagerMapper
								.loadPmData(filePath, tableName);

					}
					// 更新网元 LAST_COLLECT_TIME COLLECT_INTERVAL COLLECT_RESULT
					storeNeInfo((Integer) ne.get("BASE_NE_ID"), now, taskId,
							emsConnectionId, "", CommonDefine.SUCCESS, -1, ne);
				} else {
					// 采集失败
					storeNeInfo((Integer) ne.get("BASE_NE_ID"), now, taskId,
							emsConnectionId, success, CommonDefine.FAILED, -1, ne);
				}

			} catch (CommonException e) {
				// 更新网元采集状态
				storeNeInfo((Integer) ne.get("BASE_NE_ID"), now, taskId,
						emsConnectionId,
						MessageHandler.getErrorMessage(e.getErrorCode()),
						CommonDefine.FAILED, -1, ne);
			} catch (Exception e) {
				e.printStackTrace();
				// 更新网元采集状态
				storeNeInfo((Integer) ne.get("BASE_NE_ID"), now, taskId,
						emsConnectionId, e.getMessage(), CommonDefine.FAILED,
						-1, ne);
			}
		}
	}

	// 采集性能
	private String getPmList(List<PmDataModel> pmList, int collectSource,
			Map ne, Timestamp now, SimpleDateFormat timeFormat, Date endDate,IDataCollectServiceProxy dataCollectService) {
		String success = "SUCCESS";// 标记网元采集是否成功
		List<PmDataModel> toaddPmList = new ArrayList<PmDataModel>();
		SimpleDateFormat corbaFormat = new SimpleDateFormat("yyyyMMddHHmmss");// corba接受的时间参数格式
		try {
			// 历史性能?当前性能?
			if (collectSource == CommonDefine.PM.COLLECT_SOURCE.HISTORY_PM) {
				// 历史
				toaddPmList = dataCollectService
						.getHistoryPmData_Ne(
								(Integer) ne.get("BASE_NE_ID"),
								corbaFormat.format(new Date(date.getTime())),
								new short[] {},
								new int[] { CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG,
										CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG},
										new int[] {CommonDefine.PM.GRANULARITY_24HOUR_FLAG},
								(Integer) ne.get("COLLECT_NUMBIC") == CommonDefine.TRUE,
								(Integer) ne.get("COLLECT_PHYSICAL") == CommonDefine.TRUE,
								(Integer) ne.get("COLLECT_CTP") == CommonDefine.TRUE,
								CommonDefine.COLLECT_LEVEL_2);
			} else {
				// 当前
				toaddPmList = dataCollectService
						.getCurrentPmData_Ne(
								(Integer) ne.get("BASE_NE_ID"),
								new short[] {},
								new int[] { CommonDefine.PM.PM_LOCATION_NEAR_END_RX_FLAG,
										CommonDefine.PM.PM_LOCATION_NEAR_END_TX_FLAG},
								new int[] {CommonDefine.PM.GRANULARITY_24HOUR_FLAG},
								(Integer) ne.get("COLLECT_NUMBIC") == CommonDefine.TRUE,
								(Integer) ne.get("COLLECT_PHYSICAL") == CommonDefine.TRUE,
								(Integer) ne.get("COLLECT_CTP") == CommonDefine.TRUE,
								CommonDefine.COLLECT_LEVEL_2);
			}
			if (toaddPmList != null) {
				pmList.addAll(toaddPmList);
			}
		} catch (CommonException e) {
			if (CommonDefine.PM.CORBA_ERROR_CODE_Set.contains(e.getErrorCode())) {
				// 需要推迟采集的任务,每次推个十分钟直到推无可推就失败啦~\(≧▽≦)/~
				try {
					// 线程休眠10分
					Thread.sleep(60 * 10 * 1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				now = new Timestamp(System.currentTimeMillis());
				// 是否超时?未超时递归调用
				if (now.getTime() < endDate.getTime()) {
					// 递归调用该方法,好像要调用很多层的样子Σ(⊙▽⊙"a... 不要问我内存会不会挂
					success = getPmList(pmList, collectSource, ne, now,
							timeFormat, endDate,dataCollectService);
				}
			} else {
				// 网元采集失败
				// success = MessageHandler.getErrorMessage(e.getErrorCode());
				success = e.getErrorMessage();
			}
		}
		return success;
	}

	// 超时,将所有未采集的必须采网元设为采集失败o(′益`)o
	private void storeFailedNes(int j, List<Map> neList, int taskId,
			int emsConnectionId, Timestamp now, String errorReason) {
		for (; j < neList.size(); j++) {
			storeNeInfo(
					Integer.valueOf(neList.get(j).get("BASE_NE_ID").toString()),
					now, taskId, emsConnectionId, errorReason,
					CommonDefine.FAILED, getCollectInterval(neList.get(j), now), neList.get(j));
		}
	}

	// 保存网元信息
	//TODO   因为这个方法改过了！！！！！！！updateNeCollectInfo，insertTaskCountInfo
	private void storeNeInfo(Integer neId, Timestamp now, int taskId,
			int emsConnectionId, String failedReason, int isSuccess,
			int collectInterval, Map ne) {
		Map<String, Object> failedNe = new HashMap<String, Object>();
		failedNe.put("NE_ID", neId);
		failedNe.put("ACTION_RESULT", isSuccess);
		failedNe.put("BELONG_TO_DATE", date);// 记录采集 结果的表记录开始采集时的日期
		failedNe.put("FAILED_REASON", failedReason);
		failedNe.put("COLLECT_TYPE", ne.get("NE_LEVEL"));
		failedNe.put("COLLECT_TIME", now);
		
		if (isSuccess == CommonDefine.SUCCESS) {
			performanceManagerMapper.updateNeCollectInfo(neId, "完成",
					collectInterval, now);
			failedNe.put("COLLECT_RESULT", "成功("
					+ failedReason + ")");
		} else {
			performanceManagerMapper.updateNeCollectInfo(neId, "采集失败("
					+ failedReason + ")", collectInterval, now);
			failedNe.put("COLLECT_RESULT", "采集失败("
					+ failedReason + ")");
		}
		performanceManagerMapper.insertTaskCountInfo(taskId, emsConnectionId,
				failedNe);
	}

	// 上次采集间隔天数
	private Integer getCollectInterval(Map ne, Timestamp now) {
		// 消灭时分秒
		Date lastCollectTime;
		try {
			if (ne.get("LAST_COLLECT_TIME") != null) {
				lastCollectTime = getDateWithoutTime((Timestamp) ne
						.get("LAST_COLLECT_TIME"));
				Date nowDate = getDateWithoutTime(now);
				return ((Long) ((nowDate.getTime() - lastCollectTime.getTime()) / 1000 / 60 / 60 / 24))
						.intValue();
			}
		} catch (ParseException e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}

	// 消灭时分秒
	private Date getDateWithoutTime(Timestamp d) throws ParseException {
		SimpleDateFormat dayFormat = new SimpleDateFormat(
				CommonDefine.COMMON_FORMAT);// 计算上次采集间隔时间用
		SimpleDateFormat toStringFormat = new SimpleDateFormat(
				CommonDefine.COMMON_SIMPLE_FORMAT);// 计算上次采集间隔时间用
		return dayFormat.parse(toStringFormat.format(new Date(d.getTime())) + " 00:00:00");
	}

	// 检查任务是否被暂停
	private void checkPause(int emsConnectionId, int taskId) {
		Date forbiddenTime;
		SimpleDateFormat format = new SimpleDateFormat(
				CommonDefine.COMMON_FORMAT);

		Timestamp now = new Timestamp(System.currentTimeMillis());
		// 检测是否需要暂停?是否超时
		String forbiddenTimeString = performanceManagerMapper
				.getTaskForbiddenTime(taskId);
		if (forbiddenTimeString != null) {
			// 采集暂停
			try {
				forbiddenTime = format.parse(forbiddenTimeString);
				// 标记任务为暂停
				performanceManagerMapper.updateEmsCollectStatus(taskId,
						CommonDefine.PM.COLLECT_STATUS.PAUSE);
				while (true) {
					if (forbiddenTimeString == null
							|| now.getTime() >= forbiddenTime.getTime()) {
						// 当前时间已经超过需要暂停的时间,暂停时间置为null,跳出死循环
						performanceManagerMapper
								.deleteTaskForbiddenTime(taskId);
						break;
					} else {
						try {
							// 线程休眠120秒
							Thread.sleep(60 * 2 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						forbiddenTimeString = performanceManagerMapper
								.getTaskForbiddenTime(taskId);
						if (forbiddenTimeString != null) {
							forbiddenTime = format.parse(forbiddenTimeString);
							now = new Timestamp(System.currentTimeMillis());
						}
					}
				}
				// 标记网管为正在执行
				performanceManagerMapper.updateEmsCollectStatus(taskId,
						CommonDefine.PM.COLLECT_STATUS.EXECUTING);
			} catch (ParseException e) {
				ExceptionHandler.handleException(e);
			}

		}
	}
	
}
