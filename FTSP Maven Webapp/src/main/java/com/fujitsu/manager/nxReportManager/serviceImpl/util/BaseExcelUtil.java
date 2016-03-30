package com.fujitsu.manager.nxReportManager.serviceImpl.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.util.TimeUtil;
@SuppressWarnings("unused")
public class BaseExcelUtil {
	protected final int ERROR_VALUE = -999999;
	protected static BaseExcelUtil _instance;
	protected Workbook book;
	protected Sheet sheet;
	protected FileOutputStream out = null;
	protected CellStyle tableStyle, columnStyle, headerStyle, 
		fieldStyle, borderStyle, wrapStyle, float2Style;
	protected CellStyle centerStyle;
	protected File file;
	protected List<Integer> dates;
	public BaseExcelUtil(String fileName) {
        try {
        	dates = new ArrayList<Integer>();
        	book = new SXSSFWorkbook(256);
        	file = new File(fileName);
    		String parentPath=file.getParent();
    		if(parentPath == null){
    			String absPath = file.getAbsolutePath();
    			System.out.println("absPath = " + absPath);
    			parentPath = absPath.substring(0, absPath.lastIndexOf("\\"));
    		}
			System.out.println("parentPath = " + parentPath);
    		if(file.exists()){
    			file.delete();
    		}
    		File dirFile = new File(parentPath);
    		if (!(dirFile.exists()) || !(dirFile.isDirectory())) {
    			dirFile.mkdirs();
    			try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    		file = new File(fileName);
			out = new FileOutputStream(fileName);
			intiColumn();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
//			 T1ODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 初始化表格参数
	 */
	protected void intiColumn(){
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
    	font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerStyle.setFont(font);
		headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		fieldStyle = book.createCellStyle();
		wrapStyle = book.createCellStyle();
		wrapStyle.setWrapText(true);
		wrapStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		wrapStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		wrapStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		wrapStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		wrapStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		wrapStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		centerStyle= book.createCellStyle();
		centerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		centerStyle.setWrapText(true);
		centerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		centerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		centerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		centerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		centerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		
		borderStyle= book.createCellStyle();
		borderStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		borderStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		borderStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		borderStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		borderStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		borderStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

		float2Style= book.createCellStyle();
		float2Style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		float2Style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		float2Style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		float2Style.setBorderRight(XSSFCellStyle.BORDER_THIN);
		float2Style.setBorderTop(XSSFCellStyle.BORDER_THIN);
		float2Style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		XSSFDataFormat format = (XSSFDataFormat) book.createDataFormat();
		float2Style.setDataFormat(format.getFormat("0.00"));
	}
	/**
	 * 新建单元格格式
	 * @param horizonAlign 是否横向居中
	 * @param verticalAlign
	 * @param setBorder
	 * @param wrap
	 * @return
	 */
	protected CellStyle createStyle(short horizonAlign,short verticalAlign,
			boolean setBorder,
			boolean wrap){
		CellStyle style = book.createCellStyle();
		style.setAlignment(horizonAlign);
		style.setVerticalAlignment(verticalAlign);
		style.setWrapText(wrap);
		if(setBorder){
			style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			style.setBorderRight(XSSFCellStyle.BORDER_THIN);
			style.setBorderTop(XSSFCellStyle.BORDER_THIN);
			style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		}
		return style;
	}
	/**
	 * 设置字体
	 * @param style 要设置字体的单元格样式
	 * @param fontSize 字体大小，0表示不设置字体大小
	 * @param isBold 是否粗体
	 * @param isItalic 是否斜体
	 * @param color 字体颜色，0表示不设置
	 */
	protected void setFont(CellStyle style, short fontSize,
			boolean isBold,boolean isItalic, short color){
		if(fontSize>-1){
			Font font = book.createFont();
			if(fontSize>0){
		    	font.setFontHeightInPoints(fontSize);
			}
			if(isBold){
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			}
			font.setItalic(isItalic);
			if(color>0){
				font.setColor(color);
			}
			style.setFont(font);
		}
	}
	/**
	 * 根据字符串坐标合并单元格
	 * @param startCell
	 * @param endCell
	 * @param setBorber
	 */
	protected void merge(String startCell, String endCell, boolean setBorber){
		merge(getRow(startCell), 
				getCol(startCell), 
				getRow(endCell), 
				getCol(endCell), setBorber);
		
	}
	/**
	 * 根据字符串坐标合并单元格
	 * @param startCell
	 * @param endCell
	 */
	protected void merge(String startCell, String endCell){
		merge(startCell, endCell, false);
	}
	/**
	 * 用于合并单元格,不设置边框
	 * @param startRow
	 * @param startCol
	 * @param endRow
	 * @param endCol
	 */
	protected void merge(int startRow, int startCol,int endRow, int endCol){
		merge(startRow, startCol, endRow, endCol, false);
	}
	/**
	 * 用于合并单元格,同时根据需要设置边框
	 * @param startRow
	 * @param startCol
	 * @param endRow
	 * @param endCol
	 * @param setBorber 是否需要设置边框
	 */
	protected void merge(int startRow, int startCol,int endRow, int endCol, boolean setBorber){
//		System.out.println("合并单元格<" + getCellPos(startRow, startCol) + " - " + getCellPos(endRow, endCol) + ">");
		CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);
		sheet.addMergedRegion(region);
		if(setBorber){
			setBorder(region);
		}
	}
	
	/**
	 * 设置边框
	 * @param startRow
	 * @param startCol
	 * @param endRow
	 * @param endCol
	 */
	protected void setBorder(int startRow, int startCol,int endRow, int endCol){
		CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);
		setBorder(region);
	}
	/**
	 * 设置边框
	 * @param region
	 */
	protected void setBorder(CellRangeAddress region){
		RegionUtil.setBorderBottom(1, region, sheet, book);
		RegionUtil.setBorderLeft(1, region, sheet, book);
		RegionUtil.setBorderRight(1, region, sheet, book);
		RegionUtil.setBorderTop(1, region, sheet, book);
	}
	/**
	 * 根据字符串坐标获取Column
	 * @param celPos
	 * @return
	 */
	protected int getCol(String celPos){
    	int col = celPos.toUpperCase().charAt(0) - 'A';
    	return col;
    }
    /**
     * 根据字符串坐标获取Row
     * @param celPos
     * @return
     */
    protected int getRow(String celPos){
    	int row = Integer.valueOf(celPos.substring(1)) - 1;
    	return row;
    }
    protected String getCellPos(int row, int col){
    	char c;
    	row++;
    	String pos="";
		c=(char) ('A'+(col%26));
		pos = c +  pos;
    	return pos + row;
    }
    /**
     * 根据字符串坐标写入计算公式
     * @param celPos  例如A1
     * @param formular
     * @param style
     */
    protected void writeFormular(String celPos, String formular, CellStyle style){
    	writeFormular(getRow(celPos), getCol(celPos), formular, style);
    }
	/**
	 * 写入计算公式到指定格子
	 * @param row
	 * @param col
	 * @param formular
	 * @param style
	 */
    protected void writeFormular(int row, int col, String formular, CellStyle style){
    	Row r = null;
    	r = sheet.getRow(row);
    	if(r == null){
    		r = sheet.createRow(row);
    	}
    	Cell cell = r.getCell(col);
    	if(cell == null){
    		cell = r.createCell(col);
    	}
    	cell.setCellFormula(formular);
    	if(style != null){
    		cell.setCellStyle(style);
    	}
    }
    /**
     * 根据字符串坐标写入数据
     * @param celPos  例如A1
     * @param value
     * @param style
     */
    protected void writeCell(String celPos, String value, CellStyle style){
    	writeCell(getRow(celPos), getCol(celPos), value, style);
    }
	/**
	 * 写入指定格子
	 * @param row
	 * @param col
	 * @param value
	 * @param style
	 */
    protected void writeCell(int row, int col, String value, CellStyle style){
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
    protected void writeCell(int row, int col, double value, CellStyle style){
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
    	CellStyle arg0 = null;
		cell.setCellStyle(arg0);
    	if(style != null){
    		cell.setCellStyle(style);
    	}
    }
    /**
     * 根据字符串坐标写入数据
     * @param celPos  例如A1
     * @param value
     * @param style
     */
    protected void writeCell(String celPos, int value, CellStyle style){
    	writeCell(getRow(celPos), getCol(celPos), value, style);
    }

	/**
	 * 写入指定格子
	 * @param row
	 * @param col
	 * @param value
	 * @param style
	 */
    protected void writeCell(int row, int col, int value, CellStyle style){
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
//    	System.out.println(getCellPos(row,col) + " = " + value);
    }

    
    protected String getVal(Map data, String field){
    	Object v = data.get(field);
    	return v==null? "-" : v.toString();
    }
    protected int getInt(Map data, String field){
    	Object v = data.get(field);
    	return v==null ? 0 : Integer.parseInt((v.toString().length()>0?v.toString():"0"));
    }
    protected float getFloat(Map data, String field){
    	Object val = data.get(field);
    	if(val == null)
    		return ERROR_VALUE;
    	float v = Float.parseFloat(getVal(data, field));
    	return v;
    }
    protected String getFloat2(Map data, String field){
    	Object val = data.get(field);
    	if(val == null)
    		return "-";
    	//System.out.println("getFloat2 = " + val.toString());
    	float v = Float.parseFloat(getVal(data, field));
    	DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
    	String p= decimalFormat.format(v);//format 返回的是字符串
    	return p;
    }
    protected void flush(){
    	try {
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public boolean close(){
    	if(out!=null){
    		try {
				book.write(out);
				out.close();
			} catch (IOException e) {
//				e.printStackTrace();
				return false;
			}
    	}
    	return true;
    }
    public static BaseExcelUtil getInstance(String fileName) {
    	_instance = new BaseExcelUtil(fileName);
		return _instance;
	}
    /**
     * 对于同一文件分批导出的，使用此方法获取返回结果
     * @return ExportResult
     */
    public Map<String, Object> getResult(){
//    	close();
    	Map<String, Object> rv = new HashMap<String, Object>();
		rv.put("EXPORT_TIME", new Date());
		rv.put("REPORT_NAME", file.getName());
		rv.put("EXCEL_URL", file.getPath());
		rv.put("SIZE", (int)(file.length()/1024));
		return rv;
    }
	/**
	 * 生成列名称
	 * @param searchCond 搜索条件
	 * @return
	 */
	protected List<Integer> getColumnNames(Map<String, String> searchCond){
		List<Integer> rv = new ArrayList<Integer>();
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int step[] = {Calendar.DATE, Calendar.MONTH};
		try {
			String startTime = searchCond.get("start");
			String endTime = searchCond.get("end");
			int period = Integer.parseInt(searchCond.get("period"));
			int genType = Integer.parseInt(searchCond.get("genType"));
			int pmDate = getInt(searchCond, "pmDate");
			boolean isMonthReport = (period == 1);
			boolean isManual = (genType == 1);
			start.setTime(TimeUtil.parseString2Date(startTime, CommonDefine.COMMON_FORMAT));
			end.setTime(TimeUtil.parseString2Date(endTime, CommonDefine.COMMON_FORMAT));
//			System.out.println("end = " + end.toString());
			if(isManual && isMonthReport){
				start.set(Calendar.DATE, pmDate);
			}
			while(start.before(end)){
//				System.out.println("\tcur = " + start.get(Calendar.YEAR)
//						+ "-" + (start.get(Calendar.MONTH)+1)
//						+ "-" + start.get(Calendar.DATE));
				int key = start.get(Calendar.YEAR)*10000
						+ (start.get(Calendar.MONTH)+1)*100;
				key += start.get(Calendar.DATE);
				rv.add(key);
//				System.out.println("\t\tadd -> " + key);
				start.add(step[(isManual && isMonthReport)?1:0], 1);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rv;
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		Map map = new HashMap();
//		map.put("方向名称字段", "dir Name");
//		map.put("sheetName", "sheet Name");
//		map.put("单位字段", "Test Value!@123");
//		map.put("网络名称字段", "Test Value!@123");
//		map.put("设备类型字段", "Test Value!@123");
//		map.put("站名字段", "Test Value!@123");
//		map.put("设备名称字段", "Test Value!@123");
//		map.put("方向字段", "Test Value!@123");
////		List<Map> list = new ArrayList<Map>();
////		for(int i = 0; i < 7; i++){
////			Map detail = new HashMap();
////			list.add(detail);
////		}
////		data.setExerciseDetail(list);
//
//		String fn = (new Date()).toLocaleString();//2014-9-22 12:41:57
//		fn = fn.replace("-", "_");
//		fn = fn.replace(":", "_");
//		NxReportExcelUtil.getInstance("D:\\" + fn + ".xlsx").writeSheet(map);
	}
}
