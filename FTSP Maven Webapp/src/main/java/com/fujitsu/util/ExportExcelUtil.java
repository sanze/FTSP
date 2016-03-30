package com.fujitsu.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fujitsu.IService.IExportExcel;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.poi.ColumnMap;
import com.fujitsu.common.poi.POIExcel;
import com.fujitsu.handler.ExcelHeaderHandler;
import com.fujitsu.handler.FieldNameHandler;

import java.util.Arrays;

//使用poi实现数据导出成.xlsx格式
public class ExportExcelUtil implements IExportExcel {
	public POIExcel poi;
	protected final String fileName;
	protected final static int PAGE_SIZE = 200;
	protected final String path;
	protected String sheet = "sheet";
	protected int pageSize = PAGE_SIZE;
	protected static final int MAX_PAGE_SIZE=65535;
	
	public ExportExcelUtil(String path, String fileName) {
		this.fileName = fileName;
		this.path = path;
	}

	public ExportExcelUtil(String path, String fileName, String sheet) {
		this(path, fileName);
		this.sheet = sheet;
	}

	public ExportExcelUtil(String path, String fileName, int pageSize) {
		this(path, fileName);
		if(pageSize>MAX_PAGE_SIZE){
			this.pageSize = MAX_PAGE_SIZE;
		}else{
			this.pageSize=pageSize;
		}
	}
	//指定文件路径、名称、sheet名称，和每一页的数据行数（pageSize），
	//其中sheet默认名称为"sheet"，pageSize默认为200条数据,当pageSize大于65535时，每页只显示65535条
	public ExportExcelUtil(String path, String fileName, String sheet,
			int pageSize) {
		this(path, fileName,pageSize);
		this.sheet=sheet;
		
	}

	public String writeExcel(List<Map> data, int headerCode, boolean append) {
		CommonResult rs = ExcelHeaderHandler.getExcelHeader(headerCode);
		if (rs.getReturnResult() == 0) {
			return "";
		}
		if (getHeader(rs.getReturnMessage()) == null) {
			return "";
		}
		ColumnMap[] col = getHeader(rs.getReturnMessage());
		File dir = new File(this.path);
		if (!dir.exists()) {
			dir.mkdirs();// 创建文件夹
		}
		if (poi == null) {
			this.poi = new POIExcel(this.path + "/" + this.fileName + ".xlsx");
		}
		try {
			poi.setHeader(col); 
			//网络分析多表头导出
			poi.setHeaderCode(headerCode);
			poi.writeSheet(this.sheet, data, this.pageSize); 
			if (append == false) {
				poi.close();
			}

			return this.path + "/" + this.fileName + ".xlsx";
		} catch (Exception e) {
			e.printStackTrace();
			poi.close();
			return "";
		}
	}
	public String writeExcel(List<Map> data, String[] columns, int headerCode, boolean append) {
		CommonResult rs = ExcelHeaderHandler.getExcelHeader(headerCode);
		if (rs.getReturnResult() == 0) {
			return "";
		}
		if (getHeader(rs.getReturnMessage()) == null) {
			return "";
		}
		ColumnMap[] col = getHeader(rs.getReturnMessage());
		
		/* 过滤表头 */
		List<ColumnMap> coltmp=new ArrayList<ColumnMap>();
		List<String> columnList=Arrays.asList(columns);
		for(ColumnMap item:col){
			if(columnList.contains(item.getKey())){
				coltmp.add(item);
			}
		}
		col=new ColumnMap[coltmp.size()];
		col=coltmp.toArray(col);
		/* 过滤表头 */
		
		File dir = new File(this.path);
		if (!dir.exists()) {
			dir.mkdirs();// 创建文件夹
		}
		if (poi == null) {
			this.poi = new POIExcel(this.path + "/" + this.fileName + ".xlsx");
		}
		try {
			poi.setHeader(col); 
			//网络分析多表头导出
			poi.setHeaderCode(headerCode);
			poi.writeSheet(this.sheet, data, this.pageSize); 
			if (append == false) {
				poi.close();
			}

			return this.path + "/" + this.fileName + ".xlsx";
		} catch (Exception e) {
			e.printStackTrace();
			poi.close();
			return "";
		}
	}
	//为每个sheet页命名。
	public String writeExcel(List<Map> data, int headerCode,String sheetName,short columnWidth,boolean append){
		CommonResult rs = ExcelHeaderHandler.getExcelHeader(headerCode);
		if (rs.getReturnResult() == 0) {
			return "";
		}
		if (getHeader(rs.getReturnMessage()) == null) {
			return "";
		}
		ColumnMap[] col = getHeader(rs.getReturnMessage());
		File dir = new File(this.path);
		if (!dir.exists()) {
			dir.mkdirs();// 创建文件夹
		}
		if (poi == null) {
			this.poi = new POIExcel(this.path + "/" + this.fileName + ".xlsx");
		}
		try {
			poi.setHeader(col);
			poi.writeSheet(sheetName, data,columnWidth,append);
			if (append == false) {
				poi.close();
			}
			return this.path + "/" + this.fileName + ".xlsx";
		} catch (Exception e) {
			e.printStackTrace();
			poi.close();
			return "";
		}
	}
	public static List<String> match(String strLine,
			String regExp) {
		List<String> columns=new ArrayList<String>();
		Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(strLine);
		while (matcher.find()) {
			// 分解行信息为单元信息
			columns.add(matcher.group());
		}
		return columns;
	}
	private ColumnMap[] getHeader(String header) {
		if (header.startsWith("{") && header.endsWith("}")) {
			String[] temp = header.substring(1, header.length() - 1).split(",");
			ColumnMap[] col = new ColumnMap[temp.length];
			int i = 0;
			for (String temp2 : temp) {
				String[] temp3 = temp2.split("=");
				if (temp3.length != 2) {
					return null;
				}
				/* 表头名称转换 */
				String regExp="\\$[A-Za-z_]+";
				List<String> fieldList=match(temp3[1],regExp);
				if(fieldList!=null){
					for(String field:fieldList){
						temp3[1]=temp3[1].replaceFirst(regExp, FieldNameHandler.getFieldName(field.replaceFirst("\\$", "")));
					}
				}
				/* 表头名称转换 */
				col[i] = new ColumnMap(temp3[0], temp3[1]);
//				System.out.println(col[i].getKey());
				i++;
			}
			return col;
		} else
			return null;
	}

	public boolean close() {
		if (poi.getOut() != null) {
			try {
				poi.getBook().write(poi.getOut());
				poi.getOut().close();
			} catch (IOException e) {
				// e.printStackTrace();
				// poi.getOut().close();
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		// --------------模拟创建数据源list，使用时直接传入List<Map>类型参数即可；
		List<Map> list = new ArrayList();
		Map map = new HashMap();
		for (int i = 0; i < 1000; i++) {
			map.put("BASE_LINK_ID", "BASE_LINK_ID" + i);
			map.put("LINK_NAME", "LINK_NAME" + i);
			map.put("A_EMS_NAME", "A_EMS_NAME" + i);
			map.put("A_NE_NAME", "A_NE_NAME" + i);
			map.put("A_END_PORT", "A_END_PORT" + i);
			map.put("Z_EMS_NAME", "Z_EMS_NAME" + i);
			map.put("Z_NE_NAME", "Z_NE_NAME" + i);
			map.put("Z_END_PORT", "Z_END_PORT" + i);
			map.put("DIRECTION", "DIRECTION" + i);
			map.put("LINK_TYPE", "LINK_TYPE" + i);
			map.put("IS_MANUAL", "IS_MANUAL" + i);
			list.add(map);
		}
		// --------------------------------
		//创建ExportExcelUtil对象，指定文件路径path和文件名fileName
		String path="D:/Tomcat/webapps/FTSP3.0/uploadedFiles";
		//防止创建文件重名，给文件名加上一个时间戳。
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss");
		String fileName = "test1"
				+ formatter.format(new Date(System.currentTimeMillis()));
		//创建一个对象，指定路径和文件名
		IExportExcel ex = new ExportExcelUtil(path, fileName);
		//当参数为false是没有必要显示的调用close()方法关闭io,一旦调用该方法就无法向该文件中追加数据
		ex.writeExcel(list, CommonDefine.EXCEL.LINK_EXPORT, false);
		//无须调用close();
		//ex.close();
		
		//向同一个文件中追加数据的操作；
		path="D:/Tomcat/webapps/FTSP3.0/uploadedFiles";
		fileName="test2"+formatter.format(new Date(System.currentTimeMillis()));
		//指定sheet名称为”链路“,每页大小为500条数据
		IExportExcel ex2=new ExportExcelUtil(path, fileName,"链路",500);
		ex2.writeExcel(list, CommonDefine.EXCEL.LINK_EXPORT, true);
		//写入操作成功则返回文件位置，否则返回空
		System.out.println(ex2.writeExcel(list, CommonDefine.EXCEL.LINK_EXPORT, true));
		//当最后一个函数的append参数为true时，必须显示的调用close()方法关闭文件！
		ex2.close();
		//
	}
	
	public String writeExcel(List<Map> data,String headerStr, int headerCode, boolean append) {
		CommonResult rs = ExcelHeaderHandler.getExcelHeader(headerCode);
		if (rs.getReturnResult() == 0) {
			return "";
		}
		if (getHeader(rs.getReturnMessage()) == null) {
			return "";
		}
		ColumnMap[] col = getHeader(headerStr);
		File dir = new File(this.path);
		if (!dir.exists()) {
			dir.mkdirs();// 创建文件夹
		}
		if (poi == null) {
			this.poi = new POIExcel(this.path + "/" + this.fileName + ".xlsx");
		}
		try {
			poi.setHeader(col); 
			//网络分析多表头导出
			poi.setHeaderCode(headerCode);
			poi.writeSheet(this.sheet, data, this.pageSize); 
			if (append == false) {
				poi.close();
			}

			return this.path + "/" + this.fileName + ".xlsx";
		} catch (Exception e) {
			e.printStackTrace();
			poi.close();
			return "";
		}
	}
}
