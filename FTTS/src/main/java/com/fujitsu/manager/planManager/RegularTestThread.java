package com.fujitsu.manager.planManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.util.SpringContextUtil;

/**
 * @Description：周期测试线程
 * @author cao senrong
 * @date 2015-1-19
 * @version V1.0
 */
public class RegularTestThread implements Runnable {

	private Timer timer;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");	
	private PlanMapper planMapper ;
	private TestManagement service;
	
	public RegularTestThread() {
		planMapper = (PlanMapper) SpringContextUtil.getBean("planMapper");
		service = (TestManagement) SpringContextUtil.getBean("testManagement");
	}
	@Override
	public void run() {
		
		timer = new Timer();
		StringBuilder sb = new StringBuilder();
		
		while(true){
			
			Map planMap = planMapper.getNextPlan();
			if(planMap == null){
				planMap = planMapper.getFirstPlan();
			}
			// 防止测设计划列表为空时出现异常
			if (planMap != null) {
				Date startTime =(Date) planMap.get("START_TIME");
				Date now = new Date();
				sb.setLength(0);
				sb.append("RC[").append(planMap.get("RC_ID")).append("], startTime:").append(startTime);
				System.out.println(sb.toString());
				
				Calendar calendar_now = Calendar.getInstance();
				int year = calendar_now.get(Calendar.YEAR);
				int month = calendar_now.get(Calendar.MONTH);
				int day = calendar_now.get(Calendar.DAY_OF_MONTH);
				
				Calendar calendar_test = Calendar.getInstance();  
				calendar_test.setTime(startTime);
				calendar_test.set(year, month, day);
				if (calendar_test.before(calendar_now)) {
					calendar_test.add(Calendar.DAY_OF_MONTH, 1);
				}

				Date nextDate = calendar_test.getTime();
				//设置定时器
				sb.setLength(0);
				sb.append("RC[").append(planMap.get("RC_ID")).append("]下次开始时间：").append(df.format(nextDate));
				System.out.println(sb.toString());
				timer.schedule(new RegularTestTask(planMapper,service, planMap), nextDate);
				
				try {
					Thread.sleep(nextDate.getTime() - now.getTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}


class RegularTestTask extends java.util.TimerTask {
	
	private PlanMapper planMapper;
	private Map planMap;
	private TestManagement testManagement;
	
	public RegularTestTask(PlanMapper planMapper, TestManagement service, Map planMap) {
		this.planMapper = planMapper;
		this.planMap = planMap;
		this.testManagement = service;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void run() {
		System.out.println("RegularTestTask:" +  new Date());
		
		try {
			int planStatus =(Integer) planMap.get("STATUS");
			if(CommonDefine.PLAN_STARTUP == planStatus){

				Map map = new HashMap();
				map.put("planId", planMap.get("TEST_PLAN_ID").toString());
				List<Map> routeList = planMapper.getRouteListByPlanId(map);

				//遍历路由进行测试
				for(int i=0;i<routeList.size();i++){
					
					try {
						Map routeMap = routeList.get(i);
						int testPeriod = (Integer) routeMap.get("TEST_PERIOD");
						
						System.out.println("testing route :" + routeMap.get("TEST_ROUTE_ID").toString());
						
						if(testPeriod < 0){
							continue;
						}else {
							int scanTimes = (Integer) routeMap.get("SCAN_TIMES");
							System.out.println("scanTimes:" +  scanTimes);
							
							if(testPeriod - scanTimes == 1){
								
								//执行测试。调接口
								try {
									testManagement.runTest(routeMap.get("TEST_ROUTE_ID").toString(), null, CommonDefine.COLLECT_LEVEL_4);
									Thread.sleep(10000);
								} catch (CommonException e) {
									e.printStackTrace();
								}
								//测试完，计数器清0
								routeMap.put("SCAN_TIMES", 0);
								planMapper.updateRouteScanTimes(routeMap);
							}else{
								//不测试 ，只修改计数器
								routeMap.put("SCAN_TIMES", scanTimes+1);
								planMapper.updateRouteScanTimes(routeMap);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}else{
				//计划挂起，不测试
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}

