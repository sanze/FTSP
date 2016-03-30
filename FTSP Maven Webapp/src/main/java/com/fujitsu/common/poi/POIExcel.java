package com.fujitsu.common.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.FieldNameDefine;

public class POIExcel {
	private int count=1;
	private POIExcel _instance;
	private Workbook book;
	private Sheet sheet;
	private FileOutputStream out = null;
	@SuppressWarnings({ "rawtypes" })
	private List<Map> dat = null;
	private int row;
	private int col;
	private final int PAGE_LIMIT = 1000000;
	private final short[] colors = {
			IndexedColors.LIGHT_GREEN.getIndex(),
			IndexedColors.LIGHT_YELLOW.getIndex()
	};
	private CellStyle styles[], columnStyle;
	private ColumnMap[] header;
	private int headerCode;
	public POIExcel(String fileName) {
    	
        try {
        	book = new SXSSFWorkbook(8); //缓存128M内存。
        	intiColumn();
			out = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
//			 T1ODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
//			 T1ODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void intiColumn(){
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
    	columnStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
    	Font font =  book.createFont();
		font.setColor(HSSFColor.WHITE.index);
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    	columnStyle.setFont(font);
    	columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    	columnStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
    	columnStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
    	columnStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
	}
	 
   
    /**
     * 导出数据到一页(一页最大 100万行),然后关闭文件
     * @param sheetName 表名
     * @param dat 数据源
     * @return
     */
    @SuppressWarnings("rawtypes")
	public boolean writeSheet(String sheetName,List<Map> dat){
    	return writeSheet(sheetName,dat, PAGE_LIMIT, true);
    }
    /**
     * 导出数据到一页(一页最大 100万行)
     * @param sheetName 表名
     * @param dat 数据源
     * @param pageSize 每页数据量
     * @return
     */
    
	public boolean writeSheet(String sheetName,List<Map> dat, int pageSize){
    	return writeSheet(sheetName,dat, pageSize, true);
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
    private boolean writeSheet(String sheetName,List<Map> dat, int pageSize, boolean append){
    	this.setData(dat);
    	int total = dat.size();
    	if(pageSize>this.PAGE_LIMIT){
    		pageSize=this.PAGE_LIMIT;
    	}
    	//计算页数,数据量为0时，加个判断
    	int pages = ((total == 0 ? 1 : total) - 1) / pageSize + 1;
//    	System.out.println("Total Page ： " + pages);
    	int start;
    	for (int i = 0; i < pages; i++) {
    		start = i * pageSize;
    		String sheetNameN = sheetName;
    		if(this.count > 1){
    			sheetNameN = sheetName + (this.count-1);
    		}
    		//对Sheet名称处理，使其合法（类似文件名）
    		sheetNameN = WorkbookUtil.createSafeSheetName(sheetNameN);
//    		System.out.println("Writing page - " + sheetNameN);
    		
    		sheet = book.createSheet(sheetNameN);
    		writeData(start, pageSize);
    		this.count=this.count+1;
		}
    	if(!append){
    		close();
    	}
    	return true;
    }
    
    public boolean writeSheet(String sheetName,List<Map> dat,short columnWidth ,boolean append){
    	this.setData(dat);
    	sheetName=WorkbookUtil.createSafeSheetName(sheetName);
    	sheet=book.createSheet(sheetName);
    	writeData(0,PAGE_LIMIT,columnWidth);
    	if(!append){
    		close();
    	}
    	return true;
    }
    /**
     * 写入Excel实现函数
     * @param start 起始数据index
     * @param pageSize 数据量
     * @return
     */
    private boolean writeData(int start,int pageSize,short columnWidth){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
//    		System.out.println("        writeData(" + start + ", " + pageSize + ")");
        	int styleIndex = 0;
        	Row sheetRow = sheet.createRow(0);
        	int rangeStart = 0, rangeLen = 0, columnIndex = -1;
        	boolean comboFlag = false;
        	String prev = null, cur = null;
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
//                if(header[i].getComboType() == CommonDefine.REPORT.COMBO_KEY){
//                	columnIndex = i;
//                	prev = header[i].getColumnName();
//                }
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i,columnWidth);    
        	}
        	//数据写入
        	for (int i = 0; i < pageSize; i++){
                sheetRow = sheet.createRow(i+1);
                Map datMap = dat.get(i + start);
                //写入每一行的数据
                for (int j = 0; j < col; j++) {
                    Cell cell = sheetRow.createCell(j);
                    Object v = datMap.get(header[j].getKey());
                    if(v!=null){
                    	cur = v.toString();
                    }else{
                    	cur = "-";
                    }
                    cell.setCellValue(cur); 
                    cell.setCellStyle(styles[styleIndex]);
                }
            }
//        	for (int k = 0; k < col; k++) {
//        		if(header[k].getComboType() == CommonDefine.REPORT.COMBO_AUTO){
//        			sheet.addMergedRegion(new CellRangeAddress(rangeStart,rangeStart + rangeLen,k,k));
//            	}
//    		}
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    private boolean writeData_multiHeader(int start,int pageSize,short columnWidth){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	int styleIndex = 0;
        	Row sheetRow = sheet.createRow(0);
        	String prev = null, cur = null;
        	//输出标题
        	String[] head1 = new String[20]; 
        	if(this.headerCode == CommonDefine.EXCEL.NE_EARLY_WARN){
        		String[] headTmp={"网元","槽道可用率","端口占用率","","","","","","复用段时隙平均可用率（所在传输系统）"};
        		head1=headTmp;
        	}else if(this.headerCode == CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN){
        		String[] headTmp={"所属区域","系统名称","系统代号","复用段VC4时隙","","","复用段VC12时隙","","",
        				"技术体制","拓扑结构","传输介质","节点数","保护类型","波道数","速率",FieldNameDefine.NET_LEVEL_NAME,"生成方式","状态","所在网管","备注"};
        		head1=headTmp;
        	}
        	columnStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
         	columnStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
        		cell.setCellValue(head1[i]);
                cell.setCellStyle(columnStyle); 
                if(i==col-1 && this.headerCode == CommonDefine.EXCEL.NE_EARLY_WARN)
                	sheet.setColumnWidth((short)i,columnWidth*3);  
                else
                	sheet.setColumnWidth((short)i,columnWidth);  
        	} 
        	sheetRow = sheet.createRow(1);
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                if(i==col-1  && this.headerCode == CommonDefine.EXCEL.NE_EARLY_WARN)
                	sheet.setColumnWidth((short)i,columnWidth*3);  
                else
                	sheet.setColumnWidth((short)i,columnWidth);  
        	} 
           	if(this.headerCode == CommonDefine.EXCEL.NE_EARLY_WARN){
           		sheet.addMergedRegion(new CellRangeAddress(0,0,2,7));
             	sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            	sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
             	sheet.addMergedRegion(new CellRangeAddress(0,1,8,8)); 
        	}else if(this.headerCode == CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN){
        		sheet.addMergedRegion(new CellRangeAddress(0,0,3,5));
             	sheet.addMergedRegion(new CellRangeAddress(0,0,6,8));
            	sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
             	sheet.addMergedRegion(new CellRangeAddress(0,1,1,1)); 
            	sheet.addMergedRegion(new CellRangeAddress(0,1,2,2)); 
                for(int z=9;z<21;z++){
                	sheet.addMergedRegion(new CellRangeAddress(0,1,z,z)); 
                }
        	}  
        	//数据写入
        	int k=0;
        	for (int i = 0; i < pageSize; i++){
        		k=i+2;
                sheetRow = sheet.createRow(k);
                Map datMap = dat.get(i + start);
                //写入每一行的数据
                for (int j = 0; j < col; j++) {
                    Cell cell = sheetRow.createCell(j);
                    Object v = datMap.get(header[j].getKey());
                    if(v!=null){
                    	cur = v.toString();
                    }else{
                    	cur = "-";
                    }
                    cell.setCellValue(cur); 
                    cell.setCellStyle(styles[styleIndex]);
                }
            }
       
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    
    private boolean writeData(int start, int pageSize){
    	short width=3000;
    	if(this.headerCode == CommonDefine.EXCEL.NE_EARLY_WARN ||
    			this.headerCode == CommonDefine.EXCEL.MULTI_SEC_EARLY_WARN){
    		return writeData_multiHeader(start, pageSize,width);
		}else{
		 	return writeData(start, pageSize,width);
		} 
    }
    public Workbook getBook() {
		return book;
	}
	public void setBook(Workbook book) {
		this.book = book;
	}
	public FileOutputStream getOut() {
		return out;
	}
	public void setOut(FileOutputStream out) {
		this.out = out;
	}
	public boolean close(){
    	System.out.println("-----------close----------");
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
    public POIExcel getInstance() {
		if(_instance == null){
			_instance = new POIExcel("D:\\aaaa.xlsx");
		}
		return _instance;
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
		this.header = header;
		this.col = header.length;
	}
	public int getHeaderCode() {
		return headerCode;
	}
	public void setHeaderCode(int headerCode) {
		this.headerCode = headerCode;
	} 
}