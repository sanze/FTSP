package com.fujitsu.manager.reportManager.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import com.fujitsu.common.poi.ColumnMap;

public class ReportPOIExcel {
	private int count=1;
	private ReportPOIExcel _instance;
	private Workbook book;
	private Sheet sheet;
	private FileOutputStream out = null;
	@SuppressWarnings({ "rawtypes" })
	private List<Map> dat = null;
	private int row;
	private int col;
	private final int PAGE_LIMIT = 1000000;
	private List<String> hColumns;
	private final short[] colors = {
			IndexedColors.LIGHT_GREEN.getIndex(),
			IndexedColors.LIGHT_YELLOW.getIndex()
	};
	private CellStyle styles[], columnStyle;
	private ColumnMap[] header;
	
	public ReportPOIExcel(String fileName) {
    	
        try {
        	book = new SXSSFWorkbook(8); //缓存128M内存。
        	intiColumn();
			out = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
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
	public boolean writeSheet(String sheetName,List<Map> dat,String type){
    	return writeSheet(sheetName,dat, PAGE_LIMIT, true,type);
    }
    /**
     * 导出数据到一页(一页最大 100万行)
     * @param sheetName 表名
     * @param dat 数据源
     * @param pageSize 每页数据量
     * @return
     */
    
	public boolean writeSheet(String sheetName,List<Map> dat, int pageSize,String type){
    	return writeSheet(sheetName,dat, pageSize, true,type);
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
    private boolean writeSheet(String sheetName,List<Map> dat, int pageSize, boolean append,String type){
    	this.setData(dat);
    	int total = dat.size();
    	if(pageSize>this.PAGE_LIMIT){
    		pageSize=this.PAGE_LIMIT;
    	}
    	int pages = ((total == 0 ? 1 : total) - 1) / pageSize + 1;
    	int start;
    	for (int i = 0; i < pages; i++) {
    		start = i * pageSize;
    		String sheetNameN = sheetName + this.count;
    		sheetNameN = WorkbookUtil.createSafeSheetName(sheetNameN);
    		sheet = book.createSheet(sheetNameN);
    		if(type!=null && "性能".equals(type)){
    			writePerformanceData(start, pageSize);
    		}else if(type!=null && "资源".equals(type)){
    			writeResourceData(start, pageSize);
    		}else if(type!=null && "告警".equals(type)){
    			writeAlarmData(start, pageSize);
    		}
    		this.count=this.count+1;
		}
    	if(!append){
    		close();
    	}
    	return true;
    }
    
    
    public boolean writeAlarmSheet(String sheetName,List dat, Map colors,int pageSize, boolean append,String type){
    	this.setData(dat);
    	int total = dat.size();
    	if(pageSize>this.PAGE_LIMIT){
    		pageSize=this.PAGE_LIMIT;
    	}
    	int pages = ((total == 0 ? 1 : total) - 1) / pageSize + 1;
    	int start;
    	for (int i = 0; i < pages; i++) {
    		start = i * pageSize;
    		String sheetNameN = sheetName + this.count;
    		sheetNameN = WorkbookUtil.createSafeSheetName(sheetNameN);
    		sheet = book.createSheet(sheetNameN);
    		if("当前告警".equals(type)){
    			writeCurrentAlarmData(start,pageSize,colors);
    		}else if("日志管理".equals(type)){
    			writeLogManagementData(start,pageSize,colors);
    		}else{
    			writeHistoryAlarmData(start,pageSize,colors);
    		}
    		this.count=this.count+1;
		}
    	if(!append){
    		close();
    	}
    	return true;
    }
    
    
    private boolean writeLogManagementData(int start, int pageSize, Map colors) {
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	Row sheetRow = sheet.createRow(0);
        	String cur = null;
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
                if(hColumns.contains(header[i].getColumnName())){
	        		sheet.setColumnHidden(i,true);
	        	}
        	}
        	//生成数据
        	//数据写入
        	for (int i = 0; i < pageSize; i++){
                sheetRow = sheet.createRow(i+1);
                Map datMap = dat.get(i + start);
	        	CellStyle cs=book.createCellStyle();
	        	cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
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
                	cell.setCellStyle(styles[0]);
                	if(hColumns.contains((String)header[j].getKey())){
    	        		sheet.setColumnHidden(j,true);
    	        	}
                }
            }
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
	}
	/**
     * 当前告警导出
     */
    private boolean writeCurrentAlarmData(int start, int pageSize,Map colors){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	Row sheetRow = sheet.createRow(0);
        	String cur = null;
        	//styles[1].setFillForegroundColor(IndexedColors.WHITE.getIndex());
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
                if(hColumns.contains(header[i].getColumnName())){
	        		sheet.setColumnHidden(i,true);
	        	}
        	}
        	//生成数据
        	//数据写入
        	for (int i = 0; i < pageSize; i++){
                sheetRow = sheet.createRow(i+1);
                Map datMap = dat.get(i + start);
                int value=datMap.get("PERCEIVED_SEVERITY")==null?0:Integer.valueOf(datMap.get("PERCEIVED_SEVERITY").toString());
	        	String gjjb="";//告警级别
	        	CellStyle cs=book.createCellStyle();
	        	cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        	if(value==1){
	        		 gjjb="紧急";
	 			}else if(value==2){
	 				 gjjb="重要";
	 			}else if(value==3){
	 				 gjjb="次要";
	 			}else if(value==4){
	 				 gjjb="提示";
	 			}
	        	 
	        	int isAck=datMap.get("IS_ACK")==null?0:Integer.valueOf(datMap.get("IS_ACK").toString());
        		String isConsu="";
	        	if(isAck==1){
	        		isConsu="确认";
    			}else if(isAck==2){
    				isConsu="";
    			}
	        	
	        	Cell c0=sheetRow.createCell(0);
	        	c0.setCellValue(isConsu); 
	        	c0.setCellStyle(styles[0]);
	        	if(hColumns.contains("IS_ACK")){
	        		sheet.setColumnHidden(0,true);
	        	}
	        	
        		Cell c1=sheetRow.createCell(1);
	        	c1.setCellValue(gjjb); 
	        	c1.setCellStyle(styles[0]);
	        	if(hColumns.contains("PERCEIVED_SEVERITY")){
	        		sheet.setColumnHidden(1,true);
	        	}
                //写入每一行的数据
                for (int j = 2; j < col; j++) {
                    Cell cell = sheetRow.createCell(j);
                    Object v = datMap.get(header[j].getKey());
			        if(header[j].getKey()!=null && "DOMAIN".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
			        		v="SDH";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
			 				v="WDM";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("3")){
			 				v="ETH";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("4")){
			 				v="ATM";
			 			}else{
			 				v="";
			 			}
			        }else if(header[j].getKey()!=null && "SERVICE_AFFECTING".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
			 				v="影响";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
			 				v="不影响";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("3")){
			 				v="未知";
			 			}else{
			 				v="";
			 			}
			        }else if(header[j].getKey()!=null && "ALARM_TYPE".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("0")){
				        	v="通信";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
				 			v="服务";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
				 			v="设备";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("3")){
				 			v="处理";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("4")){
				 			v="环境";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("5")){
				 			v="安全";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("6")){
				 			v="连接";
				 		}else{
				 			v="";
				 		}
			        }else if(header[j].getKey()!=null && "IS_CLEAR".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
			        		v = "已清除";
			        	}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
			        		v = "未清除";
			        	}else {
			        		v = "";
			        	}
			        }
                    if(v!=null){
                    	cur = v.toString();
                    }else{
                    	cur = "-";
                    }
                    cell.setCellValue(cur); 
                	cell.setCellStyle(styles[0]);
                	
                	if(hColumns.contains((String)header[j].getKey())){
    	        		sheet.setColumnHidden(j,true);
    	        	}
                }
            }
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    
    /**
     * 历史告警导出
     */
    private boolean writeHistoryAlarmData(int start, int pageSize,Map colors){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	Row sheetRow = sheet.createRow(0);
        	String cur = null;
        	//styles[1].setFillForegroundColor(IndexedColors.WHITE.getIndex());
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
                if(hColumns.contains(header[i].getColumnName())){
	        		sheet.setColumnHidden(i,true);
	        	}
        	}
        	//生成数据
        	//数据写入
        	for (int i = 0; i < pageSize; i++){
                sheetRow = sheet.createRow(i+1);
                Map datMap = dat.get(i + start);
                int value=datMap.get("PERCEIVED_SEVERITY")==null?0:Integer.valueOf(datMap.get("PERCEIVED_SEVERITY").toString());
	        	String gjjb="";//告警级别
	        	CellStyle cs=book.createCellStyle();
	        	cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

	        	if(value==1){
	        		 gjjb="紧急";
	 			}else if(value==2){
	 				 gjjb="重要";
	 			}else if(value==3){
	 				 gjjb="次要";
	 			}else if(value==4){
	 				 gjjb="提示";
	 			}
        		Cell c0=sheetRow.createCell(0);
	        	c0.setCellValue(gjjb); 
	        	c0.setCellStyle(styles[0]);
	        	if(hColumns.contains("PERCEIVED_SEVERITY")){
	        		sheet.setColumnHidden(0,true);
	        	}
                //写入每一行的数据
                for (int j = 1; j < col; j++) {
                    Cell cell = sheetRow.createCell(j);
                    Object v = datMap.get(header[j].getKey());
			        if(header[j].getKey()!=null && "DOMAIN".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
			 				v="SDH";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
			 				v="WDM";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("3")){
			 				v="ETH";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("4")){
			 				v="ATM";
			 			}else{
			 				v="";
			 			}
			        }else if(header[j].getKey()!=null && "SERVICE_AFFECTING".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
			 				v="影响";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
			 				v="不影响";
			 			}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("3")){
			 				v="未知";
			 			}else{
			 				v="";
			 			}
			        }else if(header[j].getKey()!=null && "ALARM_TYPE".equals(header[j].getKey())){
			        	if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("0")){
				        	v="通信";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("1")){
				 			v="服务";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("2")){
				 			v="设备";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("3")){
				 			v="处理";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("4")){
				 			v="环境";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("5")){
				 			v="安全";
				 		}else if(datMap.get(header[j].getKey())!=null && datMap.get(header[j].getKey()).toString().equals("6")){
				 			v="连接";
				 		}else{
				 			v="";
				 		}
			        }
                    if(v!=null){
                    	cur = v.toString();
                    }else{
                    	cur = "-";
                    }
                    cell.setCellValue(cur); 
                	cell.setCellStyle(styles[0]);
                	
                	if(hColumns.contains((String)header[j].getKey())){
    	        		sheet.setColumnHidden(j,true);
    	        	}
                }
            }
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    	

    public short getColorByString(String color){
    	 if(color==null){
    		return IndexedColors.WHITE.getIndex();
    	 }
    	 if("#ff0000".equals(color)){
    		 return IndexedColors.RED.getIndex();
    	 }else if("#ff8000".equals(color)){
    		 return IndexedColors.ORANGE.getIndex();
    	 }else if("#ffff00".equals(color)){
    		 return IndexedColors.YELLOW.getIndex();
    	 }else if("#800000".equals(color)){
    		 return IndexedColors.BROWN.getIndex();
    	 }else if("#00ff00".equals(color)){
    		 return IndexedColors.GREEN.getIndex();
    	 }else if("#000000".equals(color)){
    		 return IndexedColors.BLACK.getIndex();
    	 }
    	 return IndexedColors.WHITE.getIndex();
    }
    
    
    /**
     * 写入Excel实现函数
     * @param start 起始数据index
     * @param pageSize 数据量
     * @return
     */
    private boolean writePerformanceData(int start, int pageSize){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	Row sheetRow = sheet.createRow(0);
        	int rangeStart = 0, rangeLen = 0, columnIndex = -1;
        	boolean comboFlag = false;
        	String prev = null, cur = null;
        	styles[1].setFillForegroundColor(IndexedColors.WHITE.getIndex());
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
        	}
        	//生成数据
        	//数据写入
        	for (int i = 0; i < pageSize; i++){
                sheetRow = sheet.createRow(i+1);
                Map datMap = dat.get(i + start);
                int value=(datMap.get("EXCEPTION_LV")==null?0:Integer.valueOf(datMap.get("EXCEPTION_LV").toString()));
        		if(value==0){
        			 styles[0].setFillForegroundColor(IndexedColors.GREEN.getIndex());
	 			}else if(value==1){
	 				 styles[0].setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	 			}else if(value==2){
	 				 styles[0].setFillForegroundColor(IndexedColors.ORANGE.getIndex());
	        	}else if(value==3){
	        		 styles[0].setFillForegroundColor(IndexedColors.RED.getIndex());
	        	}
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
                    if(j==0){
                    	cell.setCellStyle(styles[1]);
                    }else{
                    	cell.setCellStyle(styles[1]);
                    }
                   
                }
            }
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    
    
    
    /**
     * 资源导出
     */
    private boolean writeResourceData(int start, int pageSize){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	Row sheetRow = sheet.createRow(0);
        	int rangeStart = 0, rangeLen = 0, columnIndex = -1;
        	boolean comboFlag = false;
        	String prev = null, cur = null;
        	styles[1].setFillForegroundColor(IndexedColors.WHITE.getIndex());
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
        	}
        	//生成数据
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
                	cell.setCellStyle(styles[1]);
                   
                }
            }
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    
    
    /**
     * 告警导出
     */
    private boolean writeAlarmData(int start, int pageSize){
    	try{
    		if((start + pageSize) > dat.size()){
        		pageSize = dat.size() - start;
        	}
        	Row sheetRow = sheet.createRow(0);
        	int rangeStart = 0, rangeLen = 0, columnIndex = -1;
        	boolean comboFlag = false;
        	String prev = null, cur = null;
        	styles[1].setFillForegroundColor(IndexedColors.WHITE.getIndex());
        	//输出标题
        	for (int i = 0; i < col; i++){
        		Cell cell = sheetRow.createCell(i);
                cell.setCellValue(header[i].getColumnName());
                cell.setCellStyle(columnStyle);
                sheet.setColumnWidth((short)i, (short)header[i].getWidth()*256);    
        	}
        	//生成数据
        	//数据写入
        	for (int i = 0; i < pageSize; i++){
                sheetRow = sheet.createRow(i+1);
                Map datMap = dat.get(i + start);
                int value=datMap.get("PERCEIVED_SEVERITY")==null?0:Integer.valueOf(datMap.get("PERCEIVED_SEVERITY").toString());
	        	int isClear=datMap.get("IS_CLEAR")==null?0:Integer.valueOf(datMap.get("IS_CLEAR").toString());
	        	String gjjb="";//告警级别
	        	String isclearName="";//是否清除
	        	 if(isClear==2){
	        		 isclearName="未清除";
	        	 }else{
	        		 isclearName="已清除";
	        	 }
	        	 if(value==1){
	        		 gjjb="紧急";
	 			}else if(value==2){
	 				 gjjb="重要";
	 			}else if(value==3){
	 				 gjjb="次要";
	 			}else if(value==4){
	 				 gjjb="提示";
	 			}
	        	Cell c0=sheetRow.createCell(0);
	        	c0.setCellValue(gjjb); 
	        	c0.setCellStyle(styles[1]);
                //写入每一行的数据
                for (int j = 0; j < col; j++) {
                    Cell cell = sheetRow.createCell(j+1);
                    Object v = datMap.get(header[j].getKey());
                    if(v!=null){
                    	cur = v.toString();
                    }else{
                    	cur = "-";
                    }
                    cell.setCellValue(cur); 
                	cell.setCellStyle(styles[1]);
                }
                Cell end=sheetRow.createCell(col);
                end.setCellValue(isclearName); 
                end.setCellStyle(styles[1]);
            }
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
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
    public ReportPOIExcel getInstance() {
		if(_instance == null){
			_instance = new ReportPOIExcel("D:\\aaaa.xlsx");
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
	public List<String> gethColumns() {
		return hColumns;
	}
	public void sethColumns(List<String> hColumns) {
		this.hColumns = hColumns;
	}
	
	 
}