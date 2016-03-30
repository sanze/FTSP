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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.ExportResult;

public class MSExcelUtil {
	private File file;
	private Workbook book;
	private Sheet sheet;
	private FileOutputStream out = null;
	private List<Map> dat = null;
	private Map<String, ArrayList> tlMsList = null;
	private List<String> tlNameList = null;
	private int success;
	private int col;
	private Map<String,Map> infoMap;
	private final int PAGE_LIMIT = 1000000;
	private final short[] colors = {
			IndexedColors.LIGHT_GREEN.getIndex(),
			IndexedColors.LIGHT_YELLOW.getIndex()
	};
	private CellStyle styles[], columnStyle, exceptionStyles[];
	private ColumnMap[] header;
	//以下用于一半的分组情况（主分组+子分组）
	private int autoGroup[];
	//----------以下用于统计报表导出-------------
	private Map<String, Integer> colMap = null, rowMap = null;
	//
	public MSExcelUtil(String fileName, ColumnMap[] header) {
		this.setHeader(header);
    	book = new SXSSFWorkbook(16); //缓存16M内存。
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
	
    /**
     * 导出数据到一页(一页最大 100万行),然后关闭文件
     * @param sheetName 表名
     * @param dat 数据源
     * @return
     */
	public boolean writeSheet(String sheetName, List<Map> dat, List filter){
    	return writeSheet(sheetName, dat, filter, PAGE_LIMIT, false);
    }
    /**
     * 导出数据到一页(一页最大 100万行)
     * @param sheetName 表名
     * @param dat 数据源
     * @param pageSize 每页数据量
     * @return
     */
    
	public boolean writeSheet(String sheetName, List<Map> dat, List filter, int pageSize){
    	return writeSheet(sheetName, dat, filter, pageSize, false);
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
    public boolean writeSheet(String sheetName, List<Map> dat, List filter, boolean append){
    	return writeSheet(sheetName, dat, filter, PAGE_LIMIT, append);
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
    public boolean writeSheet(String sheetName, List<Map> dat, List filter, int pageSize, boolean append){
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
				writeData(start, pageSize, filter);
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
     * @param filter  复用段ID列表，在列表之外的必须过滤 
     * @return
     */
    private boolean writeData(int start, int pageSize, List filter){
    	boolean result = true;
    	try {
			//设置数据量，不满pageSize则修改为实际值
			if((start + pageSize) > dat.size()){
				pageSize = dat.size() - start;
			}
		System.out.println("    writeData(" + start + ", " + pageSize + ")");
			int styleIndex = 0;
			int rangeStart = 0, rangeLen = 0;
			boolean comboFlag = false;
			String mainPrev = "%^(*&$%*&(&*^$*%^($%*&(^$#$%^(*$%@#$*%&^(", 
					mainCur = null,
					msCur = null,
					msPrev = "&*(&*(&*^&*^&^&&*^*%^&^*^&*";
//					tlCur = null,
//					tlPrev = "&*(^&%&%^&(*^%^^*";
//    	System.out.println("--输出标题--");
			//输出主用路由、保护路由
			Row sheetRow = sheet.createRow(0);
			Cell cell = sheetRow.createCell(0);
			cell.setCellValue("主用路由");
			cell.setCellStyle(columnStyle);
			cell = sheetRow.createCell((col-3)/2);
			cell.setCellValue("");
			cell.setCellStyle(columnStyle);
			sheet.addMergedRegion(
    				new CellRangeAddress(0,0,0,(col-3)/2));
			cell = sheetRow.createCell((col+1)/2);
			cell.setCellValue("保护路由");
			cell.setCellStyle(columnStyle);
			cell = sheetRow.createCell(col-1);
			cell.setCellValue("");
			cell.setCellStyle(columnStyle);
			sheet.addMergedRegion(
    				new CellRangeAddress(0,0,(col+1)/2,col-1));
			// --------------------输出标题--------------------
			sheetRow = sheet.createRow(1);
			for (int i = 0; i < col; i++){
				cell = sheetRow.createCell(i);
			    cell.setCellValue(header[i].getColumnName());

			    if(!"splitter".equals(header[i].getKey()))
			    	cell.setCellStyle(columnStyle);
			    // ---设置列宽---
			    sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
			}
//    	System.out.println("--数据写入--");
			// --------------------数据写入--------------------
			int curRow = 2;
			for (int i = 0; i < pageSize; i++){
//				System.out.println("----Line - <" + curRow + ">---- ");
				Map datMap = dat.get(i + start);
				if(filter != null){
					int msId = (Integer) datMap.get("MULTI_SEC_IDMain");
					if(!filter.contains(msId)){
						continue;
					}
				}
		    	//每个复用段需要区分
		    	Object o2 = datMap.get("MULTI_SEC_IDMain");
		    	msCur = str(o2);

		    	if(msCur.equals(msPrev)){
		    		
		    	}else{
		    		Row r = sheet.createRow(curRow);
		    		//主
		    		Cell c1 = r.createCell(0);
		    		c1.setCellStyle(styles[0]);
		    		c1.setCellValue("干线名称："+infoMap.get(String.valueOf(msCur)).get("tlName").toString()+"    复用段名称："+infoMap.get(String.valueOf(msCur)).get("secName").toString());
		    		c1 = r.createCell((header.length - 1)/2-1);
		    		c1.setCellStyle(styles[0]);
					sheet.addMergedRegion(new CellRangeAddress(curRow, curRow,
							0, (header.length - 1)/2-1));
					setRegionBorder(1, new CellRangeAddress(curRow, curRow,
							0, (header.length - 1)/2-1), sheet, book);
					//备
					Cell c2 = r.createCell((header.length - 1)/2+1);
					c2.setCellStyle(styles[0]);
					c2.setCellValue("干线名称："+infoMap.get(String.valueOf(msCur)).get("tlName").toString()+"    复用段名称："+infoMap.get(String.valueOf(msCur)).get("secName").toString());
					c2 = r.createCell(header.length - 1);
					c2.setCellStyle(styles[0]);
					sheet.addMergedRegion(new CellRangeAddress(curRow, curRow,
							(header.length - 1)/2+1, header.length - 1));
					setRegionBorder(1, new CellRangeAddress(curRow, curRow,
							(header.length - 1)/2+1, header.length - 1), sheet, book);
		    		curRow++;
		    		int row = sheet.getLastRowNum();
		    		msPrev = msCur;
		    	}
		    	//=====
			    sheetRow = sheet.createRow(curRow);
			    //先 遍历 一遍，看是否是新一组数据
		    	Object o1 = datMap.get("NE_DISPLAY_NAMEMain");
		    	
		    	mainCur = str(o1);
		    	if(mainCur.equals(mainPrev)){
		    		rangeLen++;
		    	}else{
		    		comboFlag = true;
		    		mainPrev = mainCur;
		    	}

		    	
			    if(comboFlag){
//			    	System.out.println("---------------Combo!" + mainCur + "-----------------");
				    //如果是，则 变更样式，
					//并合并之前的单元格
			        if(curRow > 0){
			        	styleIndex = styleIndex == 0 ? 1 : 0;
			        	//合并自动分组
			        	for (int k = 0; k < this.autoGroup.length; k++) {
			        		sheet.addMergedRegion(
			        				new CellRangeAddress(rangeStart+1,rangeStart + rangeLen+1,
			        						this.autoGroup[k],this.autoGroup[k])
			        				);
			        	}
			        	comboFlag = false;
			        	//重新标记自身范围标记
			        	rangeLen = 0;
			    		rangeStart = curRow-1;
			        }else{
			        	//起始数据，必然这样，取消合并
			        	comboFlag = false;
			        	rangeLen = 0;
			    		rangeStart = curRow-1;
			        }
			    }
			    //写入每一行的数据
			    for (int j = 0; j < col; j++) {
			        cell = sheetRow.createCell(j);
			        Object v = datMap.get(header[j].getKey());
		        	mainCur = str(v);
//		        	System.out.print(mainCur + "\t");
			        if(!"splitter".equals(header[j].getKey())){
				        cell.setCellValue(mainCur); 
				        cell.setCellStyle(styles[1]);
			        }else{
			        	//System.out.println("Col< " + header[i].getKey() +"> - " + mainCur);
				        cell.setCellValue("");
			        }
			    }
		    	
			    curRow++;
//			    System.out.println();
			}
			//结束再合并一次
//			System.out.println("结束再合并一次 @ " + pageSize + "_" + this.autoGroup.length);
			if(pageSize > 0){
				//合并自动分组
	        	for (int i = 0; i < this.autoGroup.length; i++) {
	        		sheet.addMergedRegion(
        				new CellRangeAddress(rangeStart+1,rangeStart + rangeLen+1,
        						this.autoGroup[i],this.autoGroup[i])
        				);
	        	}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			result = false;
		}
    	return result;
    }

    public boolean export(List<Map> dat, List<Map> datRev){
    	return export(dat, datRev, PAGE_LIMIT);
    }
    
    public boolean export(List<Map> dat, List<Map> datRev, int pageSize){
    	try {
			for (String string : tlNameList) {
				System.out.println("#-> " + string);
				List filter = tlMsList.get(string);
				System.out.println("filter = " + filter.toString());
				writeSheet(string + "正向", dat, filter, true);
				writeSheet(string + "反向", datRev, filter, true);
			}
			close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;
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
//		System.out.println("--------setHeader--------");
		this.header = header;
		this.col = header.length;
		//把自动合并分组
		int autoGroupTmp[] = new int[this.col], autoGroupCnt = 0;
		//以下对应统计报表
		colMap = new HashMap<String, Integer>();
		for (int i = 0; i < header.length; i++) {
			switch(header[i].getComboType()){
			case CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO:
				autoGroupTmp[autoGroupCnt++] = i;
				break;
			case CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE:
				colMap.put(header[i].getKey(), i);
				break;
			default:
				break;
			}
		}
		//临时数据赋值给全局量
		this.autoGroup = new int[autoGroupCnt];
		for (int i = 0; i < autoGroupCnt; i++) {
			this.autoGroup[i] = autoGroupTmp[i];
		}
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
	
	@SuppressWarnings("unused")
	private void printHeader(ColumnMap[] head){
		System.out.print("\t[");
		for (int j = 0; j < head.length; j++) {
			String prop = head[j].getColumnName();
			System.out.print(prop + "\t");
		}
		System.out.println("]");
	}
	
	private static void setRegionBorder(int border, CellRangeAddress region,
			Sheet sheet, Workbook wb) {
		RegionUtil.setBorderBottom(border, region, sheet, wb);
		RegionUtil.setBorderLeft(border, region, sheet, wb);
		RegionUtil.setBorderRight(border, region, sheet, wb);
		RegionUtil.setBorderTop(border, region, sheet, wb);
		//test
//		writeFile(region);
	}
	@SuppressWarnings("unused")
	private int getColIndex(String colName){
		Integer index = -1;
		index = colMap.get(colName);
//		System.out.print("index = " + index + "_" + colMap.values().size());
		return index;
	}
	private String str(Object o){
		return (o==null)?"":o.toString();
	}
	public SXSSFWorkbook getXls() {
		return (SXSSFWorkbook)book;
	}
	public Map<String, Map> getInfoMap() {
		return infoMap;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setInfoMap(Map<String, Map> infoMap) {
		this.infoMap = infoMap;
		tlMsList = new HashMap<String, ArrayList>();
		tlNameList = new ArrayList<String>();
		//System.out.println("-------------------------------");
		for (Map.Entry<String, Map> entry : infoMap.entrySet()) {
			String tlName = (String) entry.getValue().get("tlName");
			int msId = (Integer) entry.getValue().get("MSId");
			//System.out.println("<" + tlName + "> - <" + msId + ">");
			ArrayList lst = tlMsList.get(tlName);
			if(lst == null){
				lst = new ArrayList();
				tlNameList.add(tlName);
			}
			lst.add(msId);
			tlMsList.put(tlName, lst);
		}
		//System.out.println("-------------------------------");
	}
}