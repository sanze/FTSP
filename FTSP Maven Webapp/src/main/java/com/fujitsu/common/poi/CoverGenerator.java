package com.fujitsu.common.poi;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
/**
 * 生成4中报表的封面
 * 1. 干线
 * 2. 复用段
 * 3. 原始数据
 * 4. 异常数据
 * @author TianHongjun
 *
 */
@SuppressWarnings("rawtypes")
public class CoverGenerator {
	private SXSSFWorkbook book;
	private Sheet sheet;
	@SuppressWarnings("rawtypes")
	private Map info;
	private int curRow;
	private CellStyle titleStyle, secStyle, boldStyle;
	private List<Map<String, String>> nodes;
	/**
	 * 封面生成器构造函数
	 * @param book excel文件
	 * @param taskInfo 任务信息
	 * @param nodes 
	 */
	public CoverGenerator(SXSSFWorkbook book, Map taskInfo, List<Map<String,String>> nodes) {
		this.curRow = -1;
		this.book = book;
		this.sheet = book.createSheet("报表设置");
		this.info = taskInfo;
		this.nodes = nodes;
		//将sheet设置为第一页
		book.setSheetOrder("报表设置", 0);
		//设置每一列的宽度
		//序号列窄一点
		sheet.setColumnWidth((short)0, (short)1024);
		//其他列宽一点
		for (int i = 1; i < 32; i++) {
			sheet.setColumnWidth((short)i, (short)2048);
		}
		//建立粗体字体
		Font font =  book.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		//设置标题格式
		titleStyle = book.createCellStyle();
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		titleStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		titleStyle.setFont(font);
		//段落格式
		secStyle = book.createCellStyle();
		secStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		secStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		secStyle.setFont(font);
		//粗体文字格式
		boldStyle = book.createCellStyle();
		boldStyle.setFont(font);
	}

	/**
	 * 生成干线封面
	 */
	public void genTrunkCover(){
		addTitle("报表设置");
		addSection("一、报表基本设置");
		addProperty("报表任务名称：", info.get("taskName").toString());

		addSection("二、报表周期");
		if("0".equals(info.get("period").toString())){
			//日报
			addProperty("类型：","日报");
			addProperty("时间范围：",info.get("start").toString() + " 至 " + info.get("end").toString());
		}else{
			//月报
			addProperty("类型：", "月报");
			addProperty("时间范围：", info.get("start").toString() + " 至 " + info.get("end").toString());
			int day = Integer.parseInt(info.get("pmDate").toString());
			addProperty("性能日期：", "每月" + day + "日");
		}
		addSection("三、干线选择");
		curRow++;
		setValue("干线名称", 1, 6, curRow, boldStyle);
		setValue("包含复用段", 7, 14, curRow, boldStyle);
		//填充复用段数据
		curRow++;
		for (int i = 0; i < nodes.size(); i++) {
			Map<String, String> node = nodes.get(i);
			setValue(i + 1, 0, 0, curRow, null);
			setValue(node.get("emsGroup"), 1, 6, curRow, boldStyle);
			String[] msNames = node.get("emsGroup").split(",");
			for (int j = 0; j < msNames.length; j++) {
				setValue(msNames[i], 7, 14, curRow++, boldStyle);
			}
		}
	}
	/**
	 * 生成复用段封面
	 * @param isTrunkLine 是否干线
	 */
	public void genMsCover(boolean isTrunkLine){
		addTitle("报表设置");
		addSection("一、报表基本设置");
		addProperty("报表任务名称：", info.get("taskName").toString());

		addSection("二、报表周期");
		if("0".equals(info.get("period").toString())){
			//日报
			addProperty("类型：","日报");
			addProperty("时间范围：",info.get("start").toString() + " 至 " + info.get("end").toString());
		}else{
			//月报
			addProperty("类型：", "月报");
			addProperty("时间范围：", info.get("start").toString() + " 至 " + info.get("end").toString());
			int day = Integer.parseInt(info.get("pmDate").toString());
			addProperty("性能日期：", "每月" + day + "日");
		}
		if(isTrunkLine){
			addSection("三、干线选择");
			curRow++;
			setValue("干线名称", 1, 6, curRow, boldStyle);
			setValue("包含复用段", 7, 14, curRow, boldStyle);
			//填充复用段数据
			curRow++;
			for (int i = 0; i < nodes.size(); i++) {
				Map<String, String> node = nodes.get(i);
				setValue(i + 1, 0, 0, curRow, null);
				setValue(node.get("trunkLineName"), 1, 6, curRow, null);
				String[] msNames = node.get("MSNameList").split(",");
				for (int j = 0; j < msNames.length; j++) {
					setValue(msNames[j], 7, 14, curRow++, null);
				}
			}
		}else{
			addSection("三、复用段选择");
			curRow++;
			setValue("干线名称", 1, 6, curRow, boldStyle);
			setValue("复用段名称", 7, 12, curRow, boldStyle);
			setValue("方向", 13, 14, curRow, boldStyle);
			//填充复用段数据
			for (int i = 0; i < nodes.size(); i++) {
				curRow++;
				Map<String, String> node = nodes.get(i);
				setValue(i + 1, 0, 0, curRow, null);
				setValue(node.get("trunkLineName"), 1, 6, curRow, boldStyle);
				setValue(node.get("MSName"), 7, 12, curRow, boldStyle);
				setValue(node.get("direction"), 13, 14, curRow, boldStyle);
			}
		}
	}
	/**
	 * 生成网元数据报表封面
	 */
	public void genNeCover(){
		SettingParser sp = new SettingParser(info);
		addTitle("报表设置");
		addSection("一、报表基本设置");
		addProperty("报表任务名称：", info.get("taskName").toString());
		if("0".equals(info.get("dataSrc").toString())){
			addProperty("数据源：", "原始数据");
		}else{
			addProperty("数据源：", "异常数据");
			addProperty("连续异常：", "≥" + info.get("continueAbnormal").toString() + "次");
		}
		
		addSection("二、报表周期");
		if("0".equals(info.get("period").toString())){
			//日报
			addProperty("类型：","日报");
			addProperty("时间范围：",info.get("start").toString() + " 至 " + info.get("end").toString());
		}else{
			//月报
			addProperty("类型：", "月报");
			addProperty("时间范围：", info.get("start").toString() + " 至 " + info.get("end").toString());
			int day = Integer.parseInt(info.get("pmDate").toString());
			addProperty("性能日期：", "每月" + day + "日");
		}
//		addProperty("生成延迟：","");
//		addProperty("生成时间：","");
		
		addSection("三、SDH性能设置");
		addProperty("TP等级：", sp.getSdhTp());
		addProperty("SDH物理量：", sp.getSdhPhysics());
		addProperty("最大、最小值：", sp.getSdhMaxMin());
		addProperty("SDH计数值：", sp.getSdhCounter());

		addSection("四、WDM性能设置");
		addProperty("TP等级：", sp.getWdmTp());
		addProperty("WDM物理量：", sp.getWdmPhysics());
		addProperty("最大、最小值：", sp.getWdmMaxMin());
		addProperty("WDM计数值：", sp.getWdmCounter());

		addSection("五、网元选择");
		curRow++;
		setValue("网管分组", 1, 2, curRow, boldStyle);
		setValue("网管", 3, 4, curRow, boldStyle);
		setValue("子网", 5, 7, curRow, boldStyle);
		setValue("网元", 8, 11, curRow, boldStyle);
		setValue("网元型号", 12, 14, curRow, boldStyle);
		//填充网元数据
		for (int i = 0; i < nodes.size(); i++) {
			curRow++;
			Map<String, String> node = nodes.get(i);
			setValue(i + 1, 0, 0, curRow, null);
			setValue(node.get("emsGroup"), 1, 2, curRow, null);
			setValue(node.get("ems"), 3, 4, curRow, null);
			setValue(node.get("subNet"), 5, 7, curRow, null);
			setValue(node.get("ne"), 8, 11, curRow, null);
			setValue(node.get("neType"), 12, 14, curRow, null);
		}
	}
	//-----------------私有公共方法---------------------
	/**
	 * 合并单元格
	 * @param colStart
	 * @param colEnd
	 * @param rowStart
	 * @param rowEnd
	 */
	private void combine(int colStart, int colEnd, int row){
		if(colStart == colEnd){
			return;
		}
		sheet.addMergedRegion(new CellRangeAddress(row, row, colStart, colEnd));
	}
	
	/**
	 * 添加标题，淡蓝底色，第二行，A~O，共15列
	 * @param rptName
	 */
	private void addTitle(String rptName){
		curRow += 2;
		setValue("【" + rptName + "】", 0, 14, curRow, titleStyle);
	}
	
	/**
	 * 添加段落标题，灰色25% ，第N行，A~O，共15列
	 * @param rptName
	 */
	private void addSection(String secName){
		curRow += 2;
		setValue(secName, 0, 14, curRow, secStyle);
	}
	
	/**
	 * 添加具体内容
	 * 
	 * @param propName 属性名称，占地2格（7个中文宽度）
	 * @param propValue 属性值
	 */
	private void addProperty(String propName, String propValue){
		curRow++;
		setValue(propName, 1, 2, curRow, boldStyle);
		setValue(propValue, 3, 14, curRow, null);
	}
	
	/**
	 * 在指定区域设置值
	 * @param value 值
	 * @param colStart 列起始位置
	 * @param colEnd 列结束位置
	 * @param row 行
	 * @param style 文字格式，默认设置为空
	 */
	private void setValue(String value, int colStart, int colEnd, int row, CellStyle style){
		Row r = sheet.getRow(row);
		if(null == r){
			r = sheet.createRow(row);
		}
		Cell cell = r.getCell(colStart);
		if(null == cell){
			cell = r.createCell(colStart);
		}
		//写入内容
		cell.setCellValue(value);
		if(null != style){
			cell.setCellStyle(style);
		}
		//最后合并
		combine(colStart, colEnd, row);
	}
	/**
	 * 在指定区域设置值
	 * @param value
	 * @param colStart
	 * @param colEnd
	 * @param row
	 * @param style
	 */
	private void setValue(int value, int colStart, int colEnd, int row, CellStyle style){
		Row r = sheet.getRow(row);
		if(null == r){
			r = sheet.createRow(row);
		}
		Cell cell = r.getCell(colStart);
		if(null == cell){
			cell = r.createCell(colStart);
		}
		//写入内容
		cell.setCellValue(value);
		if(null != style){
			System.out.println("setCellStyle");
			cell.setCellStyle(style);
		}
		//最后合并
		combine(colStart, colEnd, row);
	}
	public static void main(String[] args) {
		
	}
}
