package com.fujitsu.manager.planManager.serviceImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;
import jxl.write.Label;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.struts2.ServletActionContext;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CommonMapper;
import com.fujitsu.dao.mysql.PlanMapper;
import com.fujitsu.manager.planManager.service.TestResultManagementService;
import com.fujitsu.util.ZipUtil;

/**
 * @Description: 测试结果管理业务逻辑实现类
 * @author liuXin
 * @date 2014-05-08
 * @version V1.0
 */
public class TestResultManagementServiceImpl extends TestResultManagementService {
	
	@Resource
	private PlanMapper planMapper;
	@Resource
	private CommonMapper commonMapper;
	
	private static String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
	private static String TEMP_FILE_NAME = "resultTemp.xml";
	
	private File file = null;
	private WritableWorkbook book = null;
	private WritableSheet ws = null;
	private FontName fnt = WritableFont.createFont("宋体");
	private WritableFont wfTitle = null;
	private WritableFont wfSection = null;
	private WritableFont wfCell = null;
	private WritableFont wfResult = null;
	private WritableCellFormat wcfTitle = null;
	private WritableCellFormat wcfSection = null;
	private WritableCellFormat wcfCell = null;
	private WritableCellFormat wcfResult = null;
	private int curRow = 0;
	private String path="";
	
	@SuppressWarnings( {"rawtypes", "unchecked" })
	public Map queryTestResults(Map map)
	{
		Map returnMap = new HashMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 查询测试结果
		List<Map> testResultList = planMapper.getResultList(map);
		for(Map obj : testResultList){
			Date exeTime =(Date) obj.get("EXE_TIME");
			if(exeTime != null){
				obj.put("EXE_TIME", df.format(exeTime));
			}
			if(obj.get("TEST_TYPE") != null){
				int testType = (Integer)obj.get("TEST_TYPE");
				String testPeriod = "-";
				if(testType == 3){
					if(obj.get("TEST_PERIOD") != null){
						testPeriod = obj.get("TEST_PERIOD").toString();
					}
				}
				obj.put("TEST_PERIOD", testPeriod);
			}
		}
		// 查询测试结果总数
		int total = commonMapper.selectTableCount("t_ftts_test_result");
		
		returnMap.put("rows", testResultList);
		returnMap.put("total", total);
		
		return returnMap;
	}
	/* (non-Javadoc)
	 * @see com.fujitsu.manager.planManager.serviceImpl.ITestResultManagementService#queryTemplates(int, int)
	 */
	@SuppressWarnings( { "unchecked" })
	public List<Map> queryTestEvents(Map map)
	{
		List<Map> testEventList = planMapper.getEventListByResult(map);
		return testEventList;
	}
	
	public Map getResultById(Map map)
	{
		Map resultMap = planMapper.getResultById(map);
		String pointStr = ZipUtil.getInstance().unCompressStr(resultMap.get("RESULT_POINT").toString());
		resultMap.put("RESULT_POINT",pointStr);
		return resultMap;
	}
	
	public List<Map> getRouteList()
	{
		List<Map> routeMapList = planMapper.getRouteList();
		return routeMapList;
	}
	
	@Override
	public void saveToBase(Map<String, Object> map) throws CommonException {
		// TODO Auto-generated method stub
		try {
			planMapper.updateBaseValueById(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void CreateXLSFile(String dirName, String prefix) throws IOException{
		File dirFile = null;
		//本机测试时把下面的注释掉，换成 String path = "D:\\";
		HttpServletRequest request = ServletActionContext.getRequest();
		path = request.getSession().getServletContext().getRealPath(
		"/").replace('\\', '/')
		+ dirName + "/";
//		String path = "D:\\";
		dirFile = new File(path);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy_MM_dd_HH-mm-ss");
		String curDate = formatter.format(new Date(System
				.currentTimeMillis()));
		if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
			dirFile.mkdirs();
		}
		if(prefix == null|| prefix.isEmpty()){
			file = new File(path + curDate + ".xls");
		}else{
			file = new File(path + prefix + "_"+curDate + ".xls");
		}

		// start to create sheet
		book = Workbook.createWorkbook(file);
		//Define.printCmd("JxlExcelWriter","Create file : " + file.getAbsolutePath());
	}
	
	private boolean CreateXMLFile(String filePath,String fileName, String Content)
	{
		try {
			File path=new File(filePath);
			if(!path.exists()) 
				if(!path.mkdirs())
					new Exception("this file can not be created!.."+path);
			File file = new File(filePath+"\\"+fileName);
			//			if(file.exists())file.delete();

			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(file, false);

			writer.write(Content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map exportInfo(Map map) {
		
		Map resultMap = planMapper.getResultInfoById(map);
		Map rc = new HashMap();
		if(resultMap.get("RESULT_POINT") != null){
			try {
				String point = ZipUtil.getInstance().unCompressStr(resultMap.get("RESULT_POINT").toString());
				String[] pointArr = point.split("\n");
				
				StringBuilder stringBuilderXml = new StringBuilder("<?xml version=\"1.0\"?>\n");
				stringBuilderXml.append("<JSChart>\n").append("	<dataset type=\"line\">\n");
				
				for(int i=0;i<pointArr.length;i++){
					stringBuilderXml.append("<data unit=\"").append(pointArr[i].split(",")[0]).append("\" value=\"");
					stringBuilderXml.append(pointArr[i].split(",")[1]).append("\"/>\n");
				}
				stringBuilderXml.append("</dataset>\n").append("</JSChart>");
				String xmlContentString = stringBuilderXml.toString();
				CreateXMLFile(TEMP_FILE_PATH,TEMP_FILE_NAME,xmlContentString);
				
				try {
					// 启动结果监测线程
					ExecutorService execute = Executors.newSingleThreadExecutor();
					CreatePNGThread thread = new CreatePNGThread();
					Future<String> result = execute.submit(thread);
					result.get();
					execute.shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				CreateXLSFile("resultFile", "OTDR");
				
				String pngName = TEMP_FILE_NAME.replaceAll("xml", "png");
				String pngPath = TEMP_FILE_PATH +"\\" + pngName;
				ws = book.createSheet("OTDR", 0);
				// 设置标题格式
				wfTitle = new WritableFont(fnt, 18, WritableFont.BOLD, false);
				// wfTitle.setItalic(false);
				wcfTitle = new WritableCellFormat(wfTitle);
				wcfTitle.setAlignment(Alignment.CENTRE);
				wcfTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
				wcfTitle.setWrap(false);
				wcfTitle.setBorder(Border.ALL, BorderLineStyle.THIN);
				// 设置小节标题格式
				wfSection = new WritableFont(fnt, 12, WritableFont.BOLD, false);
				wcfSection = new WritableCellFormat(wfSection);
				wcfSection.setWrap(false);
				// 设置一格标题格式
				wfCell = new WritableFont(fnt, 12, WritableFont.NO_BOLD, false);
				wcfCell = new WritableCellFormat(wfCell);
				wcfCell.setWrap(false);
				wcfCell.setBorder(Border.ALL, BorderLineStyle.THIN);
				// 无格式文本
				wcfResult = new WritableCellFormat(wfCell);
				wcfResult.setWrap(false);
				// wcfResult.setBorder(Border.ALL, BorderLineStyle.THIN);

				for (int i = 0; i < 6; i++) {
					ws.setColumnView(i, 22);
				}
				// 标题栏
				ws.mergeCells(0, 0, 4, 1);
				Label titleCell = new Label(0, 0, "测试报表", wcfTitle);
				ws.addCell(titleCell);
				curRow += 2;
				
				// ----------------------测试基本信息----------------------
				Label section = new Label(0, 3, "测试基本信息", wcfSection);
				ws.addCell(section);
				String[] basicInfo = { "RTU名", "测试路由", "本次计划执行时间" };
				// 填充 测试基本信息-标题部分
				for (int i = 0; i < 3; i++) {
					Label cell = new Label(i, 4, basicInfo[i], wcfCell);
					ws.addCell(cell);
				}
				// RTU名称
				Label cell = new Label(0, 5, (String) resultMap.get("RC_NAME"),
						wcfCell);
				ws.addCell(cell);
				// 路由
				cell = new Label(1, 5, (String) resultMap.get("ROUTE_NAME"), wcfCell);
				ws.addCell(cell);
				// 时间
				cell = new Label(2, 5, resultMap.get("EXE_TIME").toString(), wcfCell);
				ws.addCell(cell);
				
				curRow = 7;
				// ---------------------测试参数----------------------
				section = new Label(0, curRow, "测试参数", wcfSection);
				ws.addCell(section);
				curRow += 1;
				// 边框设置
				WritableCellFormat wcfTmpL = new WritableCellFormat(wfCell);
				wcfTmpL.setBorder(Border.LEFT, BorderLineStyle.MEDIUM);
				WritableCellFormat wcfTmpB = new WritableCellFormat(wfCell);
				wcfTmpB.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
				WritableCellFormat wcfTmpT = new WritableCellFormat(wfCell);
				wcfTmpT.setBorder(Border.TOP, BorderLineStyle.MEDIUM);
				String[] paramsInfo = { "量程", "测试波长", "测试脉冲宽度", "测试时长", "群折射率" };
				// 填充 测试基本信息-标题部分
				for (int i = 0; i < 5; i++) {
					Label paramCell = new Label(i, curRow, paramsInfo[i], wcfCell);
					ws.addCell(paramCell);
				}
				curRow++;
				// 量程
				Label paramsValueCell = new Label(0, curRow, resultMap
						.get("OTDR_RANGE").toString(), wcfCell);
				ws.addCell(paramsValueCell);
				// 测试波长
				paramsValueCell = new Label(1, curRow, resultMap.get("OTDR_WAVE_LENGTH")
						.toString(), wcfCell);
				ws.addCell(paramsValueCell);
				// 测试脉冲宽度
				paramsValueCell = new Label(2, curRow, resultMap.get("OTDR_PLUSE_WIDTH")
						.toString(), wcfCell);
				ws.addCell(paramsValueCell);
				// 平均化次数
				paramsValueCell = new Label(3, curRow, resultMap.get("OTDR_AVE_COUNT")
						.toString(), wcfCell);
				ws.addCell(paramsValueCell);
				// 群折射率
				paramsValueCell = new Label(4, curRow, resultMap.get("OTDR_REFRACT_COEFFICIENT")
						.toString(), wcfCell);
				ws.addCell(paramsValueCell);
				
				// ---------------------------测试结果记录(插入图片)----------------------------
				curRow += 2;
				File imgFile = new File(pngPath);
				// WritableImage(col, row, width, height, imgFile);
				if (imgFile.exists()) {
					WritableImage image = new WritableImage(0, curRow, 7, 34,
							imgFile);
					ws.addImage(image);
				}
				curRow--;
				for (int i = 0; i < 7; i++) {
					cell = new Label(i, curRow, "", wcfTmpB);
					ws.addCell(cell);
					cell = new Label(i, curRow + 35, "", wcfTmpT);
					ws.addCell(cell);
				}
				for (int i = 0; i < 34; i++) {
					cell = new Label(7, curRow + i + 1, "", wcfTmpL);
					ws.addCell(cell);
				}
				wcfSection = new WritableCellFormat(wfSection);
				wcfSection.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
				section = new Label(0, curRow, "测试结果图", wcfSection);
				ws.addCell(section);
				curRow += 37;
				
				// ---------------------------测试事件列表----------------------------
				List<Map> events = planMapper.getEventListByResult(map);

				String[] testResult = { "事件点类型", "位置", "衰耗", "反射值" };
				// 填充 测试结果记录-标题部分
				for (int i = 0; i < 4; i++) {
					cell = new Label(i, curRow, testResult[i], wcfCell);
					ws.addCell(cell);
				}

				curRow++;
				for (int i = 0; i < events.size(); i++) {
					int j = 0;
					cell = new Label(j++, curRow + i, (String) events.get(i).get(
							"EVENT_TYPE"), wcfCell);
					ws.addCell(cell);
					cell = new Label(j++, curRow + i, events.get(i).get("LOCATION")
							.toString(), wcfCell);
					ws.addCell(cell);
					cell = new Label(j++, curRow + i, events.get(i).get(
							"ATTENUATION").toString(), wcfCell);
					ws.addCell(cell);
					cell = new Label(j++, curRow + i, events.get(i).get(
							"REFLECT_VALUE").toString(), wcfCell);
					ws.addCell(cell);

				}
				curRow -= 2;
				for (int i = 0; i < 4; i++) {
					cell = new Label(i, curRow, "", wcfTmpB);
					ws.addCell(cell);
					cell = new Label(i, curRow + events.size() + 2, "", wcfTmpT);
					ws.addCell(cell);
				}

				wcfSection = new WritableCellFormat(wfSection);
				wcfSection.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
				section = new Label(0, curRow, "测试事件列表", wcfSection);
				ws.addCell(section);
				
				
				book.write();
				book.close();
				rc.put("result", 1);
				rc.put("filePath", file.getAbsolutePath());
				rc.put("fileName", file.getName());
				System.out.println("------------------OTDR!!!------------------");
			} catch (IOException e) {
				rc.put("result", 0);
				rc.put("message", e.getMessage());
				System.out.println("------------------OTDR!!! IOException------------------");
			} catch (WriteException e) {
				rc.put("result", 0);
				rc.put("message", e.getMessage());
				System.out.println("------------------OTDR!!! WriteException------------------");
			}
			
		}
		return rc;
	}
	
	@Override
	public Boolean deleteResult(Map map) throws CommonException {
		Boolean result = false;
		try {
			String ids = map.get("TEST_RESULT_ID").toString();
			String[] idStrArray = ids.split(",");
			List<Integer> idList = new ArrayList<Integer>();
			for (String id : idStrArray) {
				idList.add(Integer.valueOf(id));
			}
			planMapper.deleteEvent(idList);
			planMapper.deleteResult(idList);
		} catch (Exception e) {
			throw new CommonException(e, MessageCodeDefine.CMD_EXECUTE_FAIL);
		}
		return result;
	}
}
