package com.fujitsu.manager.planManager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fujitsu.IService.IEquipmentTestManagerService;
import com.fujitsu.IService.IWSManagerService;
import com.fujitsu.activeMq.JMSSender;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonMapper;
import com.fujitsu.dao.mysql.GisManagerMapper;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.EqptInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.RoutePointInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.SysInfoModel;
import com.fujitsu.manager.equipmentTestManager.serviceImpl.model.TestParaInfoModel;
import com.fujitsu.model.TestResultModel;
import com.fujitsu.util.ZipUtil;

/**
 * @Description：
 * @author cao senrong
 * @date 2015-2-6
 * @version V1.0
 */
@Service
public class TestManagement implements IWSManagerService{
	
	@Resource
	private PlanMapper planMapper;
	@Resource
	private IEquipmentTestManagerService edmsi;
	@Resource
	private GisManagerMapper gisManagerMapper;
	@Resource
	private CommonMapper commonMapper;
	
	public CommonResult runTest(String routeId, String testParam, int level) throws CommonException{
		boolean resultflag = false;
		CommonResult testRlt = new CommonResult() ;
		testRlt.setReturnResult(CommonDefine.SUCCESS);
		testRlt.setReturnMessage("指定的光缆段测试成功，请到测试结果页面查看测试结果。");
		if(routeId != null && !"".equals(routeId)){
			Map routeMap = planMapper.getRouteById(String.valueOf(routeId));
			//判断路由状态 是否在测试  测试中的路由直接放弃
			String status = routeMap.get("STATUS").toString();
			if("0".equals(status)){
				String planId = routeMap.get("TEST_PLAN_ID").toString();
				
				Map planMap = planMapper.getPlanById(planId);
				
				//获得系统信息
				SysInfoModel sysInfo = getSysInfo();
				
				//获取设备信息
				EqptInfoModel eqptInfo = getEqptInfo(planMap);
				
				//获取路由点列表
				List<RoutePointInfoModel> routePointList = getRoutePointList(routeMap);
				
				//获取测试参数
				TestParaInfoModel testParaInfo = getTestParaInfo(routeMap);
				
				//人工指定测量参数时，将覆盖原先测试路由中的参数
				if (testParam != null) {
			        try {        
			        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						Document doc = builder.parse(new InputSource(new StringReader(testParam)));
						
						Element root = doc.getDocumentElement();
						NodeList nodes = root.getChildNodes();
						for (int i=0; i<nodes.getLength(); i++) {
							Node node = nodes.item(i);
							if ("OTDR_WAVE_LENGTH".equals(node.getNodeName())) {
								testParaInfo.setOtdrWaveLength(node.getFirstChild().getNodeValue());
							} else if ("OTDR_RANGE".equals(node.getNodeName())) {
								testParaInfo.setOtdrTestRange(node.getFirstChild().getNodeValue());
							} else if ("OTDR_PLUSE_WIDTH".equals(node.getNodeName())) {
								testParaInfo.setOtdrPluseWidth(node.getFirstChild().getNodeValue());
							} else if ("OTDR_TEST_DURATION".equals(node.getNodeName())) {
								testParaInfo.setOtdrTestTime(node.getFirstChild().getNodeValue());
							} else if ("OTDR_REFRACT_COEFFICIENT".equals(node.getNodeName())) {
								testParaInfo.setOtdrRefractCoefficient(node.getFirstChild().getNodeValue());
							}
						}
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						testRlt.setReturnResult(CommonDefine.FAILED);
						testRlt.setReturnMessage("测试参数解析错误。");
					}
				}
				
				//执行测试。调接口
				String result = "";
				try {
					System.out.println("====TestInfo================================================================================");
					System.out.println("IP:"+eqptInfo.getRtuIp()+", Port:"+eqptInfo.getRtuPort()+", Fac:"+eqptInfo.getFactory());
					System.out.println("SysIP:"+sysInfo.getNip());
					for (RoutePointInfoModel r : routePointList) {
						System.out.println("Slot:"+r.getSlot()+", Port:"+r.getPort());
					}
					System.out.println("波长：" + testParaInfo.getOtdrWaveLength() + ", 量程：" + testParaInfo.getOtdrTestRange() +
							", 脉宽：" + testParaInfo.getOtdrPluseWidth() + "，测试时长：" + testParaInfo.getOtdrTestTime() +
							", 折射系数：" + testParaInfo.getOtdrRefractCoefficient());
					System.out.println("====TestInfo================================================================================");
					result = edmsi.otdrTestCentralizeEntrance(eqptInfo, sysInfo, routePointList, testParaInfo, level);
				} catch (CommonException e) {
					// 测试发生异常时，将测试路由状态置为空闲
					edmsi.modifyRouteStatus(Integer.valueOf(routeId), CommonDefine.ROUTE_STATUS_FREE);
					e.printStackTrace();
					testRlt.setReturnResult(CommonDefine.FAILED);
					switch (e.getErrorCode()) {
					case MessageCodeDefine.CMD_CONNECT_ERROR:
						testRlt.setReturnMessage("连接设备异常。");
						break;
					case MessageCodeDefine.CMD_RETURN_UNKNOW_FACTORY:
						testRlt.setReturnMessage("不支持的测试设备。");
						break;
					case MessageCodeDefine.MESSAGE_CODE_999999:
						testRlt.setReturnMessage("运行错误。");
						break;
					default:
						testRlt.setReturnMessage("未知错误。");
						break;
					}
					return testRlt;
				}
				if (result!=null && !"".equals(result)) {
					resultflag = saveTestResult(result, routeMap.get("TEST_ROUTE_ID").toString(), level-1);
					if (!resultflag) {
						testRlt.setReturnResult(CommonDefine.FAILED);
						testRlt.setReturnMessage("保存测试结果发生错误。");
					}
				} else {
					testRlt.setReturnResult(CommonDefine.FAILED);
					testRlt.setReturnMessage("返回的测试结果信息为空白。");
				}
				
				Map<String, Object> msgMap = new HashMap<String, Object>();
				msgMap.put("CABLE_IDS", routeMap.get("CABLE_IDS").toString());
				// 发送断点更新的JMS通知消息
				JMSSender.sendMessage(CommonDefine.MESSAGE_TYPE_BREAK_POINT, msgMap);
			}
		}
		
		return testRlt;
	}
	
		
	public SysInfoModel getSysInfo() throws CommonException{
		SysInfoModel sysInfo = new SysInfoModel();
		
		try {
			
			Map sys_map = planMapper.getSysParam("SYS_IP");
			sysInfo.setNip(sys_map.get("PARAM_VALUE").toString());
			sys_map = planMapper.getSysParam("SYS_CODE");
			sysInfo.setNcode(sys_map.get("PARAM_VALUE").toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PLAN_DB_INFO_ERROR);
		}
		return sysInfo;
	}
	
	public EqptInfoModel getEqptInfo(Map planMap) throws CommonException{
		EqptInfoModel eqptInfo = new EqptInfoModel();
		
		try {
			
			eqptInfo.setRtuIp(planMap.get("IP").toString());
			eqptInfo.setRtuPort((Integer)planMap.get("PORT"));
			eqptInfo.setRcode(planMap.get("NUMBER").toString());
			eqptInfo.setFactory(planMap.get("FACTORY").toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PLAN_DB_INFO_ERROR);
		}
		return eqptInfo;
	}
	
	public TestParaInfoModel getTestParaInfo(Map routeMap) throws CommonException{
		TestParaInfoModel testParaInfo = new TestParaInfoModel();
		
		try {
			testParaInfo.setRouteId((Integer)routeMap.get("TEST_ROUTE_ID"));
			testParaInfo.setOtdrWaveLength(routeMap.get("OTDR_WAVE_LENGTH").toString());
			testParaInfo.setOtdrPluseWidth(routeMap.get("OTDR_PLUSE_WIDTH").toString());
			testParaInfo.setOtdrTestRange(routeMap.get("OTDR_RANGE").toString());
			testParaInfo.setOtdrTestTime(routeMap.get("OTDR_TEST_DURATION").toString());
			testParaInfo.setOtdrRefractCoefficient(routeMap.get("OTDR_REFRACT_COEFFICIENT").toString());
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PLAN_DB_INFO_ERROR);
		}
		return testParaInfo;
	}
	
	public List<RoutePointInfoModel> getRoutePointList(Map routeMap) throws CommonException{
		List<RoutePointInfoModel> routePointList = new ArrayList();
		
		try {
			String testEqptInfo = routeMap.get("TEST_EQPT_INFO").toString();
			String[] testEqptInfos = testEqptInfo.split(",");
			
			for(int j=0;j<testEqptInfos.length;j++){
				
				RoutePointInfoModel routePoint = new RoutePointInfoModel();
				
				String[] eqptInfos = testEqptInfos[j].split("-");
				Map rcMap = planMapper.getRcById(eqptInfos[0]);
				
				routePoint.setCtuIp(rcMap.get("IP").toString());
				routePoint.setCtuPort((Integer)rcMap.get("PORT"));
				routePoint.setSlot(eqptInfos[1]);
				routePoint.setPort(eqptInfos[2]);
				
				routePointList.add(routePoint);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.PLAN_DB_INFO_ERROR);
		}
		return routePointList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean saveTestResult(String resultString,String routeId,int testType){
		boolean result = false;
		TestResultModel testResult = new TestResultModel();		
		try {
			if(resultString!=null && !"".equals(resultString)){	
				String[] resultArr = resultString.split("\n");
				//测试参数
				String paraString = resultArr[0];
				String[] paraArr = paraString.split("@");
				testResult.setTestRouteId(Integer.valueOf(routeId));
				testResult.setTestTime(new Date());
				testResult.setTestType(testType);
				testResult.setWaveLength(Double.valueOf(paraArr[2]));
				testResult.setPlusWidth(Double.valueOf(paraArr[3]));
				testResult.setRange(Double.valueOf(paraArr[4]));
				testResult.setTestDuration(Double.valueOf(paraArr[5]));
				testResult.setRefractCoefficient(Double.valueOf(paraArr[6]));
				testResult.setTransAttenuation(paraArr[7]);
				testResult.setOpticalDistance(paraArr[8]);
				testResult.setReverseAttenuation(paraArr[9]);
				testResult.setOperateResult(0);
				
				// 增加关联的静态字段(RC_NAME,ROUTE_NAME,TEST_PERIOD)
				Map<String,Object> relatedInfo = planMapper.getRelatedInfoByRouteId(routeId);
				System.out.println(relatedInfo.get("RC_NAME")+";"+relatedInfo.get("ROUTE_NAME")+";"+relatedInfo.get("TEST_PERIOD"));
				testResult.setRcName(relatedInfo.get("RC_NAME").toString());
				testResult.setTestRouteName(relatedInfo.get("ROUTE_NAME").toString());
				testResult.setTestPeriod((Integer) relatedInfo.get("TEST_PERIOD"));

				// 测量结果质量评估
				resultEval(testResult,resultArr[resultArr.length -1].split("###").length - 1);
				
				//点
				StringBuffer sb = new StringBuffer();
				for(int i=1;i<resultArr.length-1;i++){
					String[] row = resultArr[i].split(",");
					if ((CommonDefine.DISTANCE_NUMBER_AFTER_POINT+row[0].indexOf('.')) < (row[0].length()-1)) {
						sb.append(row[0].substring(0, row[0].indexOf('.') + CommonDefine.DISTANCE_NUMBER_AFTER_POINT + 1));
					} else {
						sb.append(row[0]);
					}
					sb.append(",");
					if ((CommonDefine.DISTANCE_NUMBER_AFTER_POINT+row[1].indexOf('.')) < (row[1].length())) {
						sb.append(row[1].substring(0, row[1].indexOf('.') + CommonDefine.DISTANCE_NUMBER_AFTER_POINT));
					} else {
						sb.append(row[1]);
					}
					sb.append("\n");
//					sb.append(resultArr[i]).append("\n");
				}
				testResult.setResultPoint(ZipUtil.getInstance().compressStr(sb.toString()));
				
				planMapper.addTestResult(testResult);
				
				String testResultId = String.valueOf(testResult.getId());
				//事件
				String eventString = resultArr[resultArr.length -1];
				String[] eventsArr = eventString.split("###");
				Map eventMap = new HashMap();
				for(int i=1;i<eventsArr.length;i++){
					String[] eventArr = eventsArr[i].split("#");
					eventMap.put("TEST_RESULT_ID", testResultId);
					eventMap.put("SEQUENCE", eventArr[1]);
					eventMap.put("EVENT_TYPE", eventArr[5]);
					eventMap.put("LOCATION", eventArr[2]);
					eventMap.put("ATTENUATION", eventArr[3]);
					eventMap.put("REFLECT_VALUE", eventArr[4]);
					
					planMapper.addTestEvent(eventMap);
				}
				// 断点分析
				testResultAnalysisOnBreakpoint(testResultId);
				result = true;
			}else{
//				Map map = new HashMap();
//				map.put("TEST_ROUTE_ID", routeId);
//				map.put("EXE_TIME", new Date());
//				map.put("TEST_TYPE", testType);
//				map.put("EXE_RESULT", 1);
//				testResult.setTestRouteId(Integer.valueOf(routeId));
//				testResult.setTestTime(new Date());
//				testResult.setTestType(testType);
//				testResult.setOperateResult(1);
//				planMapper.addTestResult(testResult);
//				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//测试结果分析之断点分析
	public void testResultAnalysisOnBreakpoint(String testResultId){
		Map<String,Object> info = planMapper.getBreakpointInfo(testResultId);
		if (info == null || info.get("LOCATION") == null || info.get("CABLE_SECTION_IDS") == null)
			return;
		double location = Double.valueOf(info.get("LOCATION").toString());
		String route = info.get("CABLE_SECTION_IDS").toString(); 
		String[] cableSectionIds = route.split(",");
		double _length = 0; //测试路由由起点到所在光缆段的长度
		double cs_length = 0; //断点所在光缆段的长度
		String breakCSid = "";
		for(String csId : cableSectionIds){
			Map<String,Object> cs = commonMapper.selectTableById("t_resource_cable", "RESOURCE_CABLE_ID", Integer.valueOf(csId));
			cs_length = Double.valueOf(cs.get("CABLE_LENGTH").toString());
			_length += cs_length;
			if(_length >= location){
				breakCSid = csId;
				break;
			}			
		}
		// 判断光缆断点是否存在：1.最大事件点长度大于测试光缆段长度  2.最大事件点长度接近于测试光缆长度（误差20m）
		if ("".equals(breakCSid) || Math.abs(_length-location) <= 0.02) {
			// 清除光缆段上的断点信息
			for(String csId : cableSectionIds){
				planMapper.updateBreakpointToCableSection("0", csId);
			}
			return;
		}
		
		// 光缆断点信息处理
		double ratio = (cs_length-(_length-location))/cs_length ;
		List<Map<String,Object>> startAndEnd = gisManagerMapper.getStartAndEndLngLat(breakCSid);
		double startLng = Double.valueOf(startAndEnd.get(0).get("LNG").toString()),
			   startLat = Double.valueOf(startAndEnd.get(0).get("LAT").toString()),
			   endLng = Double.valueOf(startAndEnd.get(1).get("LNG").toString()),
			   endLat = Double.valueOf(startAndEnd.get(1).get("LAT").toString());
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.0000");
		String lng = df.format((1-ratio)*startLng + ratio*endLng);
		String lat = df.format((1-ratio)*startLat + ratio*endLat);
		
		//保存断点到该光缆段
		String breakpointStr = lng+","+lat;
		planMapper.updateBreakpointToCableSection(breakpointStr, breakCSid);
	}
	
	// 光缆测试结果质量评估
	private void resultEval(TestResultModel testResult, int eventCount) {
		double attBase;
		double attOffset;
		int eventCountBase;
		StringBuilder sb = new StringBuilder();
		
		// 仅对计划测试进行质量评估
		if (testResult.getTestType() != CommonDefine.TEST_TYPE_REGULAR) {
			testResult.setEvalDescription("");
			return;
		}
		
		// 测试结果有效性检查
		if ("-".equals(testResult.getTransAttenuation())) {
			testResult.setEvaluation(CommonDefine.EVALUATION_INVALID);
			testResult.setEvalDescription("测量结果数据异常");
			return;
		}
		
		// 获取基准值
		int tetsRouteId = testResult.getTestRouteId();
		Map<String,Object> route = commonMapper.selectTableById("t_ftts_test_route", "TEST_ROUTE_ID", tetsRouteId);
		attOffset = Double.valueOf(route.get("ATT_OFFSET").toString());
		
		// 如果没有设置基准值，则将首次测量结果设置为基准值
		if (route.get("ATT_BASE") == null ||
				route.get("EVENT_COUNT_BASE") == null ||				
				"".equals(route.get("ATT_BASE")) ||
				"".equals(route.get("EVENT_COUNT_BASE"))) {
			Map map = new HashMap();
			map.put("TEST_ROUTE_ID", tetsRouteId);
			map.put("ATT_BASE", testResult.getTransAttenuation());
			map.put("EVENT_COUNT_BASE", eventCount);
			planMapper.updateBaseValueById(map);
			attBase = Double.valueOf(testResult.getTransAttenuation());
			eventCountBase = eventCount;
		} else {
			attBase = Double.valueOf(route.get("ATT_BASE").toString());
			eventCountBase = Integer.valueOf(route.get("EVENT_COUNT_BASE").toString());
		}
		
		double att = Double.valueOf(testResult.getTransAttenuation());
		// 质量评估
		// 正常
		if ((att < (attBase+attOffset)) &&  (eventCount == eventCountBase)) {
			testResult.setEvaluation(CommonDefine.EVALUATION_NORMAL);
			testResult.setEvalDescription("正常");
			
		// 一般预警
		} else if ((att < (attBase+attOffset)) &&  (eventCount != eventCountBase)) {
			testResult.setEvaluation(CommonDefine.EVALUATION_MINOR);
			sb.setLength(0);
			sb.append("测量结果事件点计数异常(当前：").append(eventCount).append("/基准：").append(eventCountBase).append(")");
			testResult.setEvalDescription(sb.toString());
			
		// 重要预警
		} else if (att >= (attBase+attOffset)) {
			testResult.setEvaluation(CommonDefine.EVALUATION_MAJOR);
			sb.setLength(0);
			sb.append("全程传输损耗值异常(当前：").append(att).append("/基准：").append(attBase).append(",偏差：").append(attOffset).append(")");
			testResult.setEvalDescription(sb.toString());
		}
	}
}
