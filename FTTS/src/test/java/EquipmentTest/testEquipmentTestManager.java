package EquipmentTest;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fujitsu.IService.IEquipmentTestManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUAlarm;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RTUConfiguration;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;
import com.fujitsu.util.SpringContextUtil;


public class testEquipmentTestManager {

	@Test
	public void testOtdrTestCentralizeEntrance(){
		
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IEquipmentTestManagerService service = (IEquipmentTestManagerService) SpringContextUtil.getBean("equipmentTestManagerServiceImpl");
		
		System.out.println("开始构造测试数据。。。。");
		
		
		EqptInfoModel eqpt = new EqptInfoModel();
		
		eqpt.setFactory(String.valueOf(CommonDefine.EQUIP_FACTORY_XTW));
		eqpt.setRcode("RTU0000001");
		eqpt.setRtuIp("192.168.101.151");
		eqpt.setRtuPort(5000);
		
		SysInfoModel sys = new SysInfoModel();
		
		sys.setNcode("sdfsdf");

		sys.setNip("192.168.101.185");
		
		List<RoutePointInfoModel> routeList = new ArrayList<RoutePointInfoModel>();
		RoutePointInfoModel a = new RoutePointInfoModel();
		a.setCtuIp("192.168.101.151");
		a.setCtuPort(5000);
		a.setSlot("5");
		a.setPort("8");

		routeList.add(a);
		
		TestParaInfoModel testPara = new TestParaInfoModel();
		
		testPara.setRouteId(1);
		testPara.setOtdrPluseWidth("10");
		testPara.setOtdrRefractCoefficient("1.4685");
		testPara.setOtdrTestRange("20");
		testPara.setOtdrTestTime("10");
		testPara.setOtdrWaveLength("1310");
		
		System.out.println("测试数据构造完成。。。。");
		
		try {
			String result = service.otdrTestCentralizeEntrance(eqpt, sys, routeList, testPara, CommonDefine.COLLECT_LEVEL_1);
			System.out.println(result.substring(0, 100));
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testLoadRTUAlarm(){
		
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IEquipmentTestManagerService service = (IEquipmentTestManagerService) SpringContextUtil.getBean("equipmentTestManagerServiceImpl");
		
		System.out.println("开始构造测试数据。。。。");
		
		EqptInfoModel eqpt = new EqptInfoModel();
		
		eqpt.setFactory(String.valueOf(CommonDefine.EQUIP_FACTORY_XTW));
		eqpt.setRcode("RTU0000001");
		eqpt.setRtuIp("192.168.101.151");
		eqpt.setRtuPort(5000);
		
		SysInfoModel sys = new SysInfoModel();
		
		sys.setNcode("sdfsdf");
		sys.setNip("192.168.101.192");
		
		System.out.println("测试数据构造完成。。。。");
		
		try {
			List<RTUAlarm> loadRTUAlarm = service.loadRTUAlarm(eqpt, sys, CommonDefine.COLLECT_LEVEL_1);
			for(RTUAlarm data: loadRTUAlarm){
				System.out.println(data.getAlarmContent());
			}
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoadRTUConfiguration(){
		
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IEquipmentTestManagerService service = (IEquipmentTestManagerService) SpringContextUtil.getBean("equipmentTestManagerServiceImpl");
		
		System.out.println("开始构造测试数据。。。。");
		
		EqptInfoModel eqpt = new EqptInfoModel();
		
		eqpt.setFactory(String.valueOf(CommonDefine.EQUIP_FACTORY_XTW));
		eqpt.setRcode("RTU0000001");
		eqpt.setRtuIp("192.168.101.151");
		eqpt.setRtuPort(5000);
		
		SysInfoModel sys = new SysInfoModel();
		
		sys.setNcode("sdfsdf");
		sys.setNip("192.168.101.192");
		
		System.out.println("测试数据构造完成。。。。");
		
		try {
			List<RTUConfiguration> loadRTUConfiguration = service.loadRTUConfiguration(eqpt, sys, CommonDefine.COLLECT_LEVEL_1);
			for(RTUConfiguration data: loadRTUConfiguration){
				System.out.println(data.getHardwareVersion());
			}
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void testSaveOTDRFile(){
//		
//		String testResult = "";
//		
//		String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
//		String TEMP_FILE_NAME = "resultTemp.tsv";
//		
//		try {
//			// 启动结果监测线程
//			ExecutorService execute = Executors.newSingleThreadExecutor();
//			ResultFileListenThread thread = new ResultFileListenThread(
//					(new Date()), TEMP_FILE_PATH , TEMP_FILE_NAME);
//			Future<String> result = execute.submit(thread);
//			testResult = result.get();
//			execute.shutdown();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println(testResult);
//	}
}
