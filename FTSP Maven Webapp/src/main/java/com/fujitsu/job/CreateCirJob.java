package com.fujitsu.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.circuitManager.serviceImpl.CircuitManagerServiceImpl;
import com.fujitsu.util.SpringContextUtil;

public class CreateCirJob implements Job {

	private CircuitManagerMapper circuitManagerMapper;
	private ICircuitManagerService circuitManagerService;

	public CreateCirJob() {
		circuitManagerMapper = (CircuitManagerMapper) SpringContextUtil
				.getBean("circuitManagerMapper");
		circuitManagerService = (CircuitManagerServiceImpl) SpringContextUtil
				.getBean("circuitManagerServiceImpl");
	}

	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		Map map = new HashMap();
		Map update = null;
		// 网管ID
		int emsConnectionId = Integer.parseInt(context.getJobDetail()
				.getJobDataMap().get("BASE_EMS_CONNECTION_ID").toString());
		// 获取任务id
		String[] jobName = context.getJobDetail().getName().split("_");
		int taskId = Integer.parseInt(jobName[1]);
		System.out.println("进入quartz，进行电路自动生成。。。。" + taskId);
		// 获取下次执行时间
		Date next_time = context.getNextFireTime();
		System.out.println(next_time);

		List<Integer> lis = new ArrayList<Integer>();
		lis.add(emsConnectionId);

		map.put("NAME", "BASE_EMS_CONNECTION_ID");
		map.put("ID", lis);

		// ICircuitManagerService cc = new CircuitManagerServiceImpl();

		// circuitManagerMapper = SpringContextUtil.;
		// 先更新任务状态 正在执行中。。。
		update = new HashMap();
		update.put("NAME", "t_sys_task");
		update.put("ID_NAME", "SYS_TASK_ID");
		update.put("ID_VALUE", taskId);
		update.put("ID_NAME_2", "RESULT");
		update.put("ID_VALUE_2", CommonDefine.TASK_ON);
		update.put("ID_NAME_3", "START_TIME");
		update.put("ID_VALUE_3", new Date());

		circuitManagerMapper.updateByParameter(update);
		boolean stopSdh = false;
		boolean stopZte = false;
		boolean stopOtn = false;
		boolean stopPtn = false;
		boolean stopAluPtn = false;
		boolean stopZteOtn = false;
		// cc.updateTask(update);
		try {
			String key = taskId + "_newCir";
			if (CommonDefine.PROCESS_MAP.containsKey(key)) {
				CommonDefine.PROCESS_MAP.remove(key);
			}
			
			// 电路回退逻辑
			//circuitManagerService.deleteAllCirAbout("deleteCir", taskId+ "",-99);
			
			int countSdh = 0;
			int countOtn = 0;
			Map total = circuitManagerMapper.getTotal(map,0,CommonDefine.TREE.TREE_DEFINE);
			Map map_count = circuitManagerMapper.getOtnCrsTotal(map,0,CommonDefine.TREE.TREE_DEFINE);
			if (total.get("total") != null) {
				countSdh = Integer.parseInt(total.get("total").toString());
			}
			if (map_count.get("total") != null) {
				countOtn = Integer.parseInt(map_count.get("total").toString());

			}
			System.out.println("countSdh=="+countSdh);
			System.out.println("countOtn=="+countOtn);
			stopSdh = circuitManagerService.newCircuit(map, "newCir", taskId
					+ "", countSdh, countOtn,-99);
			stopZte=circuitManagerService.createZTECir(map,"newCir", taskId+ "",-99);
			stopOtn = circuitManagerService.createHWOtnCrs(map, "newCir",
					taskId + "", countSdh, countOtn,-99);
			stopZteOtn = circuitManagerService.createZteOtn(map, "newCir", taskId+ "", -99);
			stopAluPtn = circuitManagerService.createAluPtnCir(map, "newCir", taskId+ "", -99);
			stopPtn = circuitManagerService.createZtePtnCircuit(map, "newCir",
					taskId + "", 0, 0,-99);
		
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		// 先更新任务状态 执行成功
		update = new HashMap();
		update.put("NAME", "t_sys_task");
		update.put("ID_NAME", "SYS_TASK_ID");
		update.put("ID_VALUE", taskId);
		if (!stopSdh && !stopOtn && !stopPtn && !stopZte && !stopZteOtn && !stopAluPtn) {
			update.put("ID_NAME_2", "RESULT");
			update.put("ID_VALUE_2", CommonDefine.TASK_SUCCESS);
		}
		update.put("ID_NAME_3", "END_TIME");
		update.put("ID_VALUE_3", new Date());
		update.put("ID_NAME_4", "NEXT_TIME");
		update.put("ID_VALUE_4", next_time);
		circuitManagerMapper.updateByParameter(update);
		// cc.updateTask(update);
		// System.out.println("上次1。。。");
	}

}
