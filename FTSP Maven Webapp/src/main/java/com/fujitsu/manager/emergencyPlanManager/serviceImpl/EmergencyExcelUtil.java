package com.fujitsu.manager.emergencyPlanManager.serviceImpl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class EmergencyExcelUtil {
	private static EmergencyExcelUtil _instance;
	private Workbook book;
	private Sheet sheet;
	private FileOutputStream out = null;
	@SuppressWarnings({ "rawtypes" })
	private CellStyle tableStyle, columnStyle, headerStyle, fieldStyle, wrapStyle;
	
	private EmergencyExcelUtil(String fileName) {
        try {
        	book = new SXSSFWorkbook(8);
			out = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
//			 T1ODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void intiColumn(){
		columnStyle = book.createCellStyle();
		columnStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    	Font font = book.createFont();
//		font.setColor(HSSFColor.WHITE.index);
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		columnStyle.setFont(font);
		columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		columnStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		columnStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		columnStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		columnStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		columnStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		columnStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);

		tableStyle = book.createCellStyle();
		tableStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		tableStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		tableStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		tableStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		
    	headerStyle = book.createCellStyle();
    	font = book.createFont();
    	font.setFontHeightInPoints((short) 22);
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerStyle.setFont(font);
		headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		fieldStyle = book.createCellStyle();
		wrapStyle = book.createCellStyle();
		wrapStyle.setWrapText(true);
		wrapStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		wrapStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		int colWidths[] = {14, 2, 8, 8, 14, 8, 8, 8, 8, 8, 8};
		for (int i = 0; i < colWidths.length; i++) {
			sheet.setColumnWidth(i, colWidths[i]*256);
		}
	}
	/**
	 * 用于合并单元格
	 * @param startRow
	 * @param startCol
	 * @param endRow
	 * @param endCol
	 */
	private void merge(int startRow, int startCol,int endRow, int endCol){
		CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);
		sheet.addMergedRegion(region);
	}
	private void setBorder(int startRow, int startCol,int endRow, int endCol){
		CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);

		RegionUtil.setBorderBottom(1, region, sheet, book);
		RegionUtil.setBorderLeft(1, region, sheet, book);
		RegionUtil.setBorderRight(1, region, sheet, book);
		RegionUtil.setBorderTop(1, region, sheet, book);
	}
	/**
	 * 写入指定格子
	 * @param row
	 * @param col
	 * @param value
	 * @param style
	 */
    private void writeCell(int row, int col, String value, CellStyle style){
    	Row r = null;
    	r = sheet.getRow(row);
    	if(r == null){
    		r = sheet.createRow(row);
    	}
    	Cell cell = r.getCell(col);
    	if(cell == null){
    		cell = r.createCell(col);
    	}
    	cell.setCellValue(value);
    	if(style != null){
    		cell.setCellStyle(style);
    	}
    }
	/**
	 * 写入指定格子
	 * @param row
	 * @param col
	 * @param value
	 * @param style
	 */
    private void writeCell(int row, int col, int value, CellStyle style){
    	Row r = null;
    	r = sheet.getRow(row);
    	if(r == null){
    		r = sheet.createRow(row);
    	}
    	Cell cell = r.getCell(col);
    	if(cell == null){
    		cell = r.createCell(col);
    	}
    	cell.setCellValue(value);
    	if(style != null){
    		cell.setCellStyle(style);
    	}
    }

    public boolean writeData(Map data){
    	try{
    		String sheetName = data.get("sheetName").toString();
    		//对Sheet名称处理，使其合法（类似文件名）
    		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
    		sheet = book.createSheet(sheetName);
    		intiColumn();
    		writeCell(0, 0, data.get("sheetName").toString(), headerStyle);
    		merge(0,0,0,10);
    		//演习名称：
    		writeCell(1, 0, "演习名称：", fieldStyle);
    		writeCell(1, 1, data.get("EXERCISE_DISPALY_NAME").toString(), null);
    		//预案类型：
    		writeCell(1, 4, "预案类型：", fieldStyle);
    		writeCell(1, 5, data.get("EP_TYPE").toString(), null);
    		//演习开始时间：
    		writeCell(2, 0, "演习开始时间：", fieldStyle);
    		writeCell(2, 1, data.get("START_TIME").toString(), null);
    		//演习结束时间：
    		writeCell(2, 4, "演习结束时间：", fieldStyle);
    		writeCell(2, 5, data.get("END_TIME").toString(), null);
    		//演习参与人员：
    		writeCell(3, 0, "演习参与人员：", fieldStyle);
    		writeCell(3, 1, data.get("PARTICIPANTS").toString(), null);
    		merge(3, 1, 3, 10);
    		//演习结果：
    		writeCell(4, 0, "演习结果：", fieldStyle);
    		writeCell(4, 1, data.get("RESULT").toString(), null);
    		//演习步骤：
    		writeCell(5, 0, "演习步骤：", fieldStyle);
    		writeCell(5, 1, "", columnStyle);
    		writeCell(5, 2, "操作描述", columnStyle);
    		merge(5, 2, 5, 4);
    		writeCell(5, 5, "开始时间", columnStyle);
    		writeCell(5, 6, "结束时间", columnStyle);
    		writeCell(5, 7, "参与人员", columnStyle);
    		merge(5, 7, 5, 9);
    		writeCell(5, 10, "实施结果", columnStyle);
    		setBorder(5, 1, 5, 10);
    		List<Map> listDetail = (List<Map>) data.get("exerciseDetailList");
    		for (int i = 0; i < listDetail.size(); i++) {
    			Map detail = listDetail.get(i);
    			writeCell(6 + i, 1, i+1, tableStyle);
    			writeCell(6 + i, 2, detail.get("ACTION_DESC").toString(), tableStyle);
        		merge(6 + i, 2, 6 + i, 4);
    			writeCell(6 + i, 5, detail.get("START_TIME").toString(), tableStyle);
    			writeCell(6 + i, 6, detail.get("END_TIME").toString(), tableStyle);
    			writeCell(6 + i, 7, detail.get("PARTICIPANTS").toString(), tableStyle);
        		merge(6 + i, 7, 6 + i, 9);
    			writeCell(6 + i, 10, detail.get("RESULT").toString(), tableStyle);
        		setBorder(6 + i, 1, 6 + i, 10);
			}
    		//演习评估：
    		writeCell(7 + listDetail.size(), 0, 
    				"演习评估：", fieldStyle);
    		writeCell(7 + listDetail.size(), 1, data.get("ASSESSMENT") !=null?
    				data.get("ASSESSMENT").toString():"", wrapStyle);
    		merge(7 + listDetail.size(), 1, 10 + listDetail.size(), 10);
    		close();
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }

	public boolean close(){
    	if(out!=null){
    		try {
				book.write(out);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
    	}
    	return true;
    }
    public static EmergencyExcelUtil getInstance(String fileName) {
    	_instance = new EmergencyExcelUtil(fileName);
		return _instance;
	}
	public static void main(String[] args) {
//		EmergencyPlanModel data = new EmergencyPlanModel();
//		data.setEpName("XX预案YY演习详情");
//		data.setExerciseName("个发送的");
//		data.setErType("啥子类型");
//		data.setStartTime("2014 11-22");
//		data.setEndTime("2014 13-14");
//		data.setParticipants("路人甲1号、路人甲2号、路人甲3号、路人甲4号、路人甲5号、路人甲6号、路人甲7号");
//		data.setAssessment("首先,我想感谢CCTV、感谢MTV、感谢Channel[V]、感谢SMG  blablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla");
//		data.setResult("灰常成功");
//		List<EmergencyPlanDetailModel> list = new ArrayList<EmergencyPlanDetailModel>();
//		for(int i = 0; i < 7; i++){
//			EmergencyPlanDetailModel detail = new EmergencyPlanDetailModel();
//			detail.setActionDesc("Step " + (i+1));
//			detail.setStartTime("Time " + i);
//			detail.setEndTime("Time " + (i+1));
//			detail.setParticipants("路人甲" + (i+1) + "号");
//			detail.setResult("结果" + i);
//			list.add(detail);
//		}
//		data.setExerciseDetail(list);
//		
//		String fn = (new Date()).toLocaleString();//2014-9-22 12:41:57
//		fn = fn.replace("-", "_");
//		fn = fn.replace(":", "_");
//		EmergencyExcelUtil.getInstance("D:\\" + fn + ".xlsx").writeData(data);
	}
}