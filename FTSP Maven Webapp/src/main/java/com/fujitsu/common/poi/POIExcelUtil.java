package com.fujitsu.common.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.ExportResult;

public class POIExcelUtil {
	private File file;
	private SXSSFWorkbook book;
	private Sheet sheet;
	private FileOutputStream out = null;
	private List<Map> dat = null;
	private int success;
	private int col;
	private final int PAGE_LIMIT = 1000000;
	private final short[] colors = {
			IndexedColors.LIGHT_GREEN.getIndex(),
			IndexedColors.LIGHT_YELLOW.getIndex()
	};
	private CellStyle styles[], columnStyle, exceptionStyles[];
	private ColumnMap[] header;
	//以下用于一半的分组情况（主分组+子分组）
	private int keyColumn, autoGroup[], subGroup[];
	//----------以下用于统计报表导出-------------
	private Map<String, Integer> colMap = null, rowMap = null;
	//
	public SXSSFWorkbook getXls(){
		return this.book;
	}
	public POIExcelUtil(String fileName, ColumnMap[] header) {
		this.setHeader(header);
    	book = new SXSSFWorkbook(128); //缓存128行
    	intiColumnStyle();
    	this.success = 1;
        try {
        	file = new File(fileName);
    		String parentPath=file.getParent();
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
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		}
	}
	private void intiColumnStyle(){
    	//定义单元格格式
    	styles = new CellStyle[2];
    	for (int i = 0; i < 2; i++) {
    		styles[i] = book.createCellStyle();
    		styles[i].setFillForegroundColor(colors[i]);
    		styles[i].setFillPattern(CellStyle.SOLID_FOREGROUND);
    		styles[i].setBorderBottom(XSSFCellStyle.BORDER_THIN);
    		styles[i].setBorderLeft(XSSFCellStyle.BORDER_THIN);
    		styles[i].setBorderRight(XSSFCellStyle.BORDER_THIN);
    		styles[i].setBorderTop(XSSFCellStyle.BORDER_THIN);
    		styles[i].setAlignment(XSSFCellStyle.ALIGN_LEFT);
    		styles[i].setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		}
    	
    	columnStyle = book.createCellStyle();
    	columnStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
    	columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    	Font font =  book.createFont();
		font.setColor(HSSFColor.WHITE.index);
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    	columnStyle.setFont(font);
    	columnStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
    	columnStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
    	//性能值颜色
    	short[] colorIndex = {
    			HSSFColor.BLACK.index,
    			HSSFColor.BLUE.index,
    			HSSFColor.ORANGE.index,
    			HSSFColor.RED.index,
    			HSSFColor.GREY_80_PERCENT.index,
    			HSSFColor.GREY_50_PERCENT.index,
    			HSSFColor.GREY_25_PERCENT.index
		};
    	exceptionStyles = new CellStyle[6*2];
    	for (int i = 0; i < 6; i++) {
    		exceptionStyles[i] = book.createCellStyle();
    		exceptionStyles[i].setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
    		exceptionStyles[i].setFillPattern(CellStyle.SOLID_FOREGROUND);
    		exceptionStyles[i].setBorderBottom(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i].setBorderLeft(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i].setBorderRight(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i].setBorderTop(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i].setAlignment(XSSFCellStyle.ALIGN_LEFT);
    		exceptionStyles[i].setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
        	font =  book.createFont();
    		font.setColor(colorIndex[i]);
//    		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    		exceptionStyles[i].setFont(font);
    		
    		exceptionStyles[i+6] = book.createCellStyle();
    		exceptionStyles[i+6].setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
    		exceptionStyles[i+6].setFillPattern(CellStyle.SOLID_FOREGROUND);
    		exceptionStyles[i+6].setBorderBottom(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i+6].setBorderLeft(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i+6].setBorderRight(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i+6].setBorderTop(XSSFCellStyle.BORDER_THIN);
    		exceptionStyles[i+6].setAlignment(XSSFCellStyle.ALIGN_LEFT);
    		exceptionStyles[i+6].setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
        	font =  book.createFont();
    		font.setColor(colorIndex[i]);
//    		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    		exceptionStyles[i+6].setFont(font);
		}
    }
	//测试用header
	static ColumnMap[] testheader = {
		new ColumnMap("id","ID", 0),
		new ColumnMap("prop1","其┐", 1),
		new ColumnMap("prop2","实的", 1),
		new ColumnMap("prop3","我萌", 2),
		new ColumnMap("prop4","是卖", 3),
		new ColumnMap("prop5","来来",0),
		new ColumnMap("prop6","卖是",0),
		new ColumnMap("prop7","萌我",0),
		new ColumnMap("prop8","的实",0),
		new ColumnMap("prop9","└其",0)
	};
	//测试用header
	static ColumnMap[] testCmbHeader = {
		new ColumnMap("id","ID", 1),
		new ColumnMap("port","端口", 2),
		new ColumnMap("event","性能事件", 0),
		new ColumnMap("13-1-4","13-1-4", 0),
		new ColumnMap("13-1-5","13-1-5", 0),
		new ColumnMap("13-1-6","13-1-6", 0),
		new ColumnMap("13-1-7","13-1-7",0),
		new ColumnMap("13-1-8","13-1-8",0),
		new ColumnMap("13-1-9","13-1-9",0)
	};
	static ColumnMap[] testDataHeader = {
		new ColumnMap("id","ID", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO),
		new ColumnMap("port","key", CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY),
		new ColumnMap("date","a", CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE),
		new ColumnMap("event","p",CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY),
		new ColumnMap("val","v", CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE)
	};
	/**
	 * 本地测试入口
	 * @param args
	 */
    @SuppressWarnings("deprecation")
	public static void main(String[] args) {
    	//TODO Test
        String newFileName = "d:\\test_performance" + (new Date().toLocaleString()).replace(":", "_") + ".xlsx";
        //newFileName = WorkbookUtil.createSafeSheetName(newFileName);
//        newFileName = newFileName.replace(":", "_");
        int rows = 1033;
 
        Date time = new Date();
        
        try {
        	DataGenerator g = new DataGenerator(rows, rows * 3);
//            List<Map> data = g.genDataFromFile(testheader, "D:\\TestData\\srcDataLarge.txt");
//            System.out.println("创建 Excel用时");
//            POIExcelUtil poi = new POIExcelUtil(newFileName, testheader);
//            poi.writeSheet("test", data, 5000, true);
            List<Map> data = g.genDataFromFile(testDataHeader, "D:\\TestData\\multiDateData.txt");
            System.out.println("创建 Excel用时");
            POIExcelUtil poi = new POIExcelUtil(newFileName, testCmbHeader);
            List<Map> dataC = poi.combineData(data, testDataHeader);
            poi.writeSheet("test", dataC, 5000, true);
            System.out.println(new Date().getTime() - time.getTime());
            time = new Date();
            poi.close();
            System.out.println("写文件用时");
            System.out.println(new Date().getTime() - time.getTime());
        } catch (Exception e) {
            // TODqO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new Date().getTime() - time.getTime());
    }
    /**
     * 导出数据到一页(一页最大 100万行),然后关闭文件
     * @param sheetName 表名
     * @param dat 数据源
     * @return
     */
	public boolean writeSheet(String sheetName, List<Map> dat){
    	return writeSheet(sheetName, dat, PAGE_LIMIT, false);
    }
    /**
     * 导出数据到一页(一页最大 100万行)
     * @param sheetName 表名
     * @param dat 数据源
     * @param pageSize 每页数据量
     * @return
     */
    
	public boolean writeSheet(String sheetName, List<Map> dat, int pageSize){
    	return writeSheet(sheetName, dat, pageSize, false);
    }
    /**
     * 导出数据到一页(一页最大 100万行)
     * @param sheetName 表名
     * @param dat 数据源
     * @param append 是否追加数据
     * --true 后面还有数据，需要继续输入
     * --false 后面没有数据，直接结束
     * @return
     */
    public boolean writeSheet(String sheetName, List<Map> dat, boolean append){
    	return writeSheet(sheetName, dat, PAGE_LIMIT, append);
    }
    
    /**
     * 导出数据到一页
     * @param sheetName 表名
     * @param dat 数据源
     * @param pageSize 每页最大数据量
     * @param append 是否追加数据
     * --true 后面还有数据，需要继续输入
     * --false 后面没有数据，直接结束
     * @return
     */
    public boolean writeSheet(String sheetName, List<Map> dat, int pageSize, boolean append){
    	boolean result = true;
    	try {
			this.setData(dat);
			int total = dat.size();
			//计算页数,数据量为0时，加个判断
			int pages = ((total == 0 ? 1 : total) - 1) / pageSize + 1;
//    	System.out.println("Total Page ： " + pages);
			int start;
			for (int i = 0; i < pages; i++) {
				start = i * pageSize;
				String sheetNameN = sheetName + (pages>1 ? "-【" + (i+1) + "】":"");
				//对Sheet名称处理，使其合法（类似文件名）
				sheetNameN = WorkbookUtil.createSafeSheetName(sheetNameN);
				sheet = book.createSheet(sheetNameN);
				writeData(start, pageSize);
			}
			if(!append){
				close();
			}
			this.success *= 1;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			this.success = 0;
		}
    	return result;
    }
    /**
     * 写入Excel实现函数
     * @param start 起始数据index
     * @param pageSize 数据量
     * @return
     */
    private boolean writeData(int start, int pageSize){
    	boolean result = true;
    	try {
			//设置数据量，不满pageSize则修改为实际值
			if((start + pageSize) > dat.size()){
				pageSize = dat.size() - start;
			}
//		System.out.println("    writeData(" + start + ", " + pageSize + ")");
			int styleIndex = 0;
			Row sheetRow = sheet.createRow(0);
			int rangeStart = 0, rangeLen = 0;
			int subRangeStart[] = new int[this.subGroup.length],
					subRangeLen[] = new int[this.subGroup.length];
			boolean comboFlag = false, subComboFlag[] = new boolean[this.subGroup.length];
			String mainPrev = null, 
					subPrev[] = new String[this.subGroup.length], 
					mainCur = null, 
					subCur[] = new String[this.subGroup.length];
			//清空子分组的长度/位置等信息
			for (int i = 0; i < subRangeStart.length; i++) {
				subRangeStart[i] = -1;
				subRangeLen[i] = 0;
				subPrev[i] = "" + Math.random();
				subComboFlag[i] = false;
			}
//    	System.out.println("--输出标题--");
			// --------------------输出标题--------------------
			for (int i = 0; i < col; i++){
				Cell cell = sheetRow.createCell(i);
			    cell.setCellValue(header[i].getColumnName());
			    if(header[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY){
			    	mainPrev = header[i].getColumnName();
			    }
			    cell.setCellStyle(columnStyle);
			    // ---设置列宽---
			    sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
			}
//    	System.out.println("--数据写入--");
			// --------------------数据写入--------------------
			for (int i = 0; i < pageSize; i++){
//    		System.out.println("----Line - <" + (i+1) + ">----");
			    sheetRow = sheet.createRow(i+1);
			    Map datMap = dat.get(i + start);
			    //先遍历一遍，看是否是新一组数据
			    mainCur = "null";
			    if(keyColumn >= 0){
			    	Object o1 = datMap.get(header[keyColumn].getKey());
			    	Object o2 = datMap.get("ne");
			    	mainCur = (o1==null? "null":o1.toString()) + (o2==null? "null":o2.toString());
			    }
//			    mainCur = keyColumn >= 0? datMap.get(header[keyColumn].getKey()).toString() : "null";
			    if(keyColumn >= 0 && header[keyColumn].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY){
			    	if(mainCur.equals(mainPrev) || mainCur == mainPrev){
			    		rangeLen++;
			    	}else{
			    		comboFlag = true;
			    		mainPrev = mainCur;
			    	}
			    }
			    if(comboFlag){
			    //如果是，则 变更样式，
				//并合并之前的单元格
			        if(i > 0){
//	            	System.out.println("Combo!");
			        	styleIndex = styleIndex == 0 ? 1 : 0;
			        	//合并自动分组
			        	for (int k = 0; k < this.autoGroup.length; k++) {
			        		sheet.addMergedRegion(
			        				new CellRangeAddress(rangeStart,rangeStart + rangeLen,
			        						this.autoGroup[k],this.autoGroup[k])
			        				);
			        	}
			        	//合并关键列
			        	if(keyColumn >= 0){
				        	sheet.addMergedRegion(
				    				new CellRangeAddress(rangeStart,rangeStart + rangeLen,
				    						keyColumn,keyColumn)
				    				);
			        	}
			        	comboFlag = false;
			        	//重新标记自身范围标记
			        	rangeLen = 0;
			    		rangeStart = i+1;
			    		for (int j = 0; j < subPrev.length; j++) {
			    			subPrev[j] = "ThjMarker" + Math.random();
						}
			        }else{
			        	comboFlag = false;
			        	rangeLen = 0;
			    		rangeStart = i+1;
			        }
			    }
			    //检测SubGroup,进行合并等操作
			    for (int j = 0; j < this.subGroup.length; j++) {
			    	subCur[j] = datMap.get(header[this.subGroup[j]].getKey()).toString();
			    	if(subCur[j].equals(subPrev[j]) || subCur[j] == subPrev[j]){
			    		subRangeLen[j]++;
			    	}else{
			    		subComboFlag[j] = true;
			    		subPrev[j] = subCur[j];
			    		break;
			    	}
			    	if(subComboFlag[j]){
			    		if(subRangeStart[j] > 0){
			        		//合并子分组
			        		sheet.addMergedRegion(
			        				new CellRangeAddress(subRangeStart[j],subRangeStart[j] + subRangeLen[j],
			        						this.subGroup[j],this.subGroup[j])
			        				);
			            	subComboFlag[j] = false;
			            	//重新标记自身范围标记
			            	subRangeLen[j] = 0;
			            	subRangeStart[j] = i;
			    		}else{
			    			subComboFlag[j] = false;
			            	//重新标记自身范围标记
			            	subRangeLen[j] = 0;
			            	subRangeStart[j] = i;
			    		}
			    	}
				}
			    //写入每一行的数据
			    for (int j = 0; j < col; j++) {
			        Cell cell = sheetRow.createCell(j);
			        Object v = datMap.get(header[j].getKey());
			        if(v!=null){
			        	mainCur = v.toString();
			        }else{
			        	mainCur = "";
			        }
			        //如果是性能值，则根据前缀性能等级设置颜色
			        if(header[j].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE){
//			        	System.out.println("styleIndex = " + styleIndex);
			        	try{
				        	int exceptionLvl = Integer.parseInt(mainCur.substring(0, 1));
				        	cell.setCellValue(mainCur.substring(1)); 
					        cell.setCellStyle(exceptionStyles[exceptionLvl + styleIndex * 6]);
			        	}catch(Exception e){
			        		cell.setCellValue(mainCur);
			        		cell.setCellStyle(exceptionStyles[0 + styleIndex * 6]);
			        	}
			        }else{
				        cell.setCellValue(mainCur); 
				        cell.setCellStyle(styles[styleIndex]);
			        }
			    }
			}
			if(pageSize > 0){
				//合并自动分组
	        	for (int i = 0; i < this.autoGroup.length; i++) {
	        		sheet.addMergedRegion(
        				new CellRangeAddress(rangeStart,rangeStart + rangeLen,
        						this.autoGroup[i],this.autoGroup[i])
        				);
	        	}
	        	//合并关键列
	        	if(keyColumn >= 0){
		        	sheet.addMergedRegion(
	    				new CellRangeAddress(rangeStart,rangeStart + rangeLen,
	    						keyColumn,keyColumn)
	    				);
	        	}
	        	//合并子分组
	        	for (int i = 0; i < this.subGroup.length; i++) {
		        	sheet.addMergedRegion(
	        			//由于不是在新的一组进行判别的，所以范围要+1
	    				new CellRangeAddress(subRangeStart[i],subRangeStart[i] + subRangeLen[i] + 1,
	    						this.subGroup[i],this.subGroup[i])
	    				);
	        	}
	
			}
		} catch (Exception e) {
			//e.printStackTrace();
			result = false;
		}
    	return result;
    }
    public ExportResult export(String sheetName, List<Map> dat){
    	return export(sheetName, dat, PAGE_LIMIT);
    }
    public ExportResult export(String sheetName, List<Map> dat, int pageSize){
    	ExportResult rv = new ExportResult();
    	rv.setReturnResult(CommonDefine.FAILED);
    	try {
        	boolean result = writeSheet(sheetName, dat, pageSize);
        	rv.setExportTime(new Date());
        	rv.setFileName(this.getFileName());
        	rv.setFilePath(this.getFilePath());
	    	rv.setReturnResult(result ? CommonDefine.SUCCESS : CommonDefine.FAILED);
	    	rv.setSize(this.getSize());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return rv;
    }
    /**
     * 对于同一文件分批导出的，使用此方法获取返回结果
     * @return ExportResult
     */
    public ExportResult getResult(){
    	close();
    	ExportResult rv = new ExportResult();
		rv.setExportTime(new Date());
		rv.setFileName(file.getName()); 
		rv.setFilePath(file.getPath());
		rv.setReturnMessage("success");
		rv.setSize((int)(file.length()/1024));
		rv.setReturnResult(success);
		return rv;
    }
    public boolean close(){
//    	System.out.println("-----------close----------");
    	if(out!=null){
    		try {
				book.write(out);
				out.close();
				book.dispose();
			} catch (IOException e) {
				//e.printStackTrace();
				return false;
			}
    	}
    	return true;
    }

	public List<Map> getData() {
		return dat;
	}
	
	public void setData(List<Map> dat) {
		this.dat = dat;
	}
	public ColumnMap[] getHeader() {
		return header;
	}
	public void setHeader(ColumnMap[] header) {
		if(null == header){
			return;
		}
//		System.out.println("--------setHeader--------");
		this.header = header;
		this.col = header.length;
		this.keyColumn = -1;
		//把自动合并分组
		int autoGroupTmp[] = new int[this.col], autoGroupCnt = 0;
		int subGroupTmp[] = new int[this.col], subGroupCnt = 0;
		//以下对应统计报表
		colMap = new HashMap<String, Integer>();
		for (int i = 0; i < header.length; i++) {
//			System.out.println(header[i].getColumnName() + " - " + header[i].getKey() + " - " + header[i].getComboType());
			switch(header[i].getComboType()){
			case CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY:
				this.keyColumn = i;
				break;
			case CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO:
				autoGroupTmp[autoGroupCnt++] = i;
				break;
			case CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY:
				subGroupTmp[subGroupCnt++] = i;
				break;
			case CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE:
				colMap.put(header[i].getKey(), i);
//				System.out.println("NewCol -> " + header[i].getKey());
				break;
			default:
				break;
			}
		}
		//临时数据赋值给全局量
//		System.out.println("this.keyColumn = " + this.keyColumn);
//		System.out.print("        autoGroup = [");
		this.autoGroup = new int[autoGroupCnt];
		for (int i = 0; i < autoGroupCnt; i++) {
			this.autoGroup[i] = autoGroupTmp[i];
//			System.out.print((i==0?"":", ") + this.autoGroup[i]);
		}
//		System.out.print("]\n        subGroup = [");
		this.subGroup = new int[subGroupCnt];
		for (int i = 0; i < subGroupCnt; i++) {
			this.subGroup[i] = subGroupTmp[i];
//			System.out.print((i==0?"":", ") + this.subGroup[i]);
		}
//		System.out.println("]");
	}
	public int getSize() {
		return file == null ? 0 : (int)(file.length() / 1024);
	}
	public String getFileName() {
		return file == null ? "" : file.getName();
	}
	public String getFilePath() {
		return file == null ? "" : file.getAbsolutePath();
	}
	/**
	 * 合并统计数据，把同一端口的不同时间段的数据合并到同一行
	 * @param lstSrc 原始数据
	 * @param srcHeader 原始数据对应的列信息
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<Map> combineData(List<Map> lstSrc, ColumnMap[] srcHeader){
//		System.out.println();
		rowMap = new HashMap<String, Integer>();
		List<Map> rv = new ArrayList<Map>();
		int groupIndex = -1;		//
		int dateIndex = -1;
		int eventIndex = -1;
		int valueIndex = -1;
		int curIndex = 0;
		//初始化，获取各个标识列的index
		for (int i = 0; i < srcHeader.length; i++) {
			if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY){
				//标记子分组的Key
				groupIndex = i;
			}else if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE){
				//标记日期分组的Key
				dateIndex = i;
			}else if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY){
				//标记性能事件的Key
				eventIndex = i;
			}else if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE){
				//标记性能值的Key
				valueIndex = i;
			}
		}
		//随机生成一个前一列信息，保证和数据中的列信息不一样
		String preGroupName = "" + Math.random();
		String curGroupName = "";
		String date = "";
		String propertyName = "";
		//开始数据归并过程
		System.out.println("原始数据一共 " + lstSrc.size() + "行");
		for (int i = 0; i < lstSrc.size(); i++) {
			Map dat = lstSrc.get(i);
			curGroupName = dat.get(srcHeader[groupIndex].getKey()).toString();
			date = dat.get(srcHeader[dateIndex].getKey()).toString();
			propertyName = dat.get(srcHeader[eventIndex].getKey()).toString();
			//判断是不是新的一组数据
			if(curGroupName.equals(preGroupName) || curGroupName == preGroupName){
			}else{
				//如果是新的分组，则清除原先的分组信息
				//主要是不同性能事件对应的行索引
				rowMap.clear();
				preGroupName = curGroupName;
			}
			//获取当前行的位置
			int rowIndex = getRowIndex(propertyName);
			//如果是新的性能事件，则新建行
			if(rowIndex < 0){
				//添加记录
				rowMap.put(propertyName, curIndex);
				rv.add(new HashMap());
				rowIndex = curIndex++;			
			}
			Map e = rv.get(rowIndex);
			//获取Map内容，填充结果集
			System.out.print("#" +(i+1)+ "\t");
			String newInfo = "";
			for (int j = 0; j < srcHeader.length; j++) {
				String prop = srcHeader[j].getKey();
				String val = dat.get(prop) == null ? "-" : dat.get(prop).toString();
				System.out.print(val+ "\t");
				if(j == valueIndex){
					//如果是性能事件对应的值，则将其添加到对应的时间里
					e.put(date, val);
					newInfo += "[" + date + " -> " + val + "] @ #" + curIndex;
				}else if(j == dateIndex){
					//如果是时间列，则忽略
				}else{
					//其他则复制
					e.put(prop, val);
//					System.out.print(prop + "\t");
				}
			}
			System.out.println();
			System.out.println(newInfo);		
		}
		return rv;
	}
	
	@SuppressWarnings("unused")
	private void printHeader(ColumnMap[] head){
		System.out.print("\t[");
		for (int j = 0; j < head.length; j++) {
			String prop = head[j].getColumnName();
			System.out.print(prop + "\t");
		}
		System.out.println("]");
	}
	
	@SuppressWarnings("unused")
	private void printMap(Map<String,String> e){
		System.out.print("[");
		for (int j = 0; j < testCmbHeader.length; j++) {
			String prop = testCmbHeader[j].getKey();
			Object val = e.get(prop); 
			if(val == null){
				val = "-";
			}
			System.out.print(val + "\t");
		}
		System.out.println("]");
	}
	@SuppressWarnings("unused")
	private int getColIndex(String colName){
		Integer index = -1;
		index = colMap.get(colName);
//		System.out.print("index = " + index + "_" + colMap.values().size());
		return index;
	}
	/**
	 * 根据性能事件获取行号
	 * @param pmEvent
	 * @return
	 */
	private int getRowIndex(String pmEvent){
		Integer index = -1;
		if(null == rowMap){
			rowMap = new HashMap<String, Integer>();
		}
		index = rowMap.get(pmEvent);
		if(null == index){
			index = -1;
		}
		return index;
	}
	private void setVal(int rowNo, int colNo, String val){
		Row row = sheet.getRow(rowNo);
		if(null == row){
			row = sheet.createRow(rowNo);
		}
		Cell cell = row.getCell(colNo);
		if(null == cell){
			cell = row.createCell(colNo);
		}
		cell.setCellValue(val);
//		cell.setCellStyle(columnStyle);
	}
}