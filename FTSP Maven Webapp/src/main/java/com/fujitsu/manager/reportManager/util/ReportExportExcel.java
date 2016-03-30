package com.fujitsu.manager.reportManager.util;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fujitsu.IService.IExportExcel;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.poi.ColumnMap;
import com.fujitsu.handler.ExcelHeaderHandler;

public class ReportExportExcel implements IExportExcel{
	public ReportPOIExcel poi;
	private final String fileName;
	private final static int PAGE_SIZE = 5000;
	private final String path;
	private String sheet = "sheet";
	private int pageSize = PAGE_SIZE;
	private static final int MAX_PAGE_SIZE=65535;
	private String type="";
	private List<String> hColumns;
	public ReportExportExcel(String path, String fileName,String type) {
		this.fileName = fileName;
		this.path = path;
		this.type=type;
	}

	public ReportExportExcel(String path, String fileName, String sheet,String type) {
		this(path, fileName,type);
		this.sheet = sheet;
	}

	public ReportExportExcel(String path, String fileName, int pageSize,String type) {
		this(path, fileName,type);
		if(pageSize>MAX_PAGE_SIZE){
			this.pageSize = MAX_PAGE_SIZE;
		}else{
			this.pageSize=pageSize;
		}
	}
	//指定文件路径、名称、sheet名称，和每一页的数据行数（pageSize），
	//其中sheet默认名称为"sheet"，pageSize默认为200条数据,当pageSize大于65535时，每页只显示65535条
	public ReportExportExcel(String path, String fileName, String sheet,int pageSize,String type) {
		this(path, fileName,pageSize,type);
		this.sheet=sheet;
		
	}

	public ReportExportExcel(String path, String fileName, String type,List<String> hColumns) {
		this(path, fileName,type);
		this.hColumns=hColumns;
	}

	public String writeExcel(List data, int headerCode, boolean append) {
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
			this.poi = new ReportPOIExcel(this.path + "/" + this.fileName + ".xlsx");
		}
		try {
			poi.setHeader(col);
			poi.writeSheet(this.sheet, data, this.pageSize,this.type);
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
	
	
	
	
	public String writeExcel(List data, Map colors,int headerCode, boolean append) {
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
			this.poi = new ReportPOIExcel(this.path + "/" + this.fileName + ".xlsx");
		}
		try {
			poi.setHeader(col);
			poi.sethColumns(hColumns);
			poi.writeAlarmSheet(this.sheet,data,colors,this.pageSize,true,this.type);
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

	private ColumnMap[] getHeader(String header) {
		if (header.startsWith("{") && header.endsWith("}")) {
			String[] temp = header.substring(1, header.length() - 1).split(",");
//			List<String> ls=(List<String>)Arrays.asList(temp);
//			ArrayList<String> lists=new ArrayList<String>(ls);
//			for(int m=lists.size()-1;m>=0;m--){
//				String ss=lists.get(m);
//				String[] s = ss.split("=");
//				if (s.length != 2) {
//					return null;
//				}
//				if(hColumns.contains(s[0])){
//					lists.remove(m);
//				}
//			}
//			temp=new String[lists.size()];
//			lists.toArray(temp);
			ColumnMap[] col = new ColumnMap[temp.length];
			int i = 0;
			for (String temp2 : temp) {
				String[] temp3 = temp2.split("=");
				if (temp3.length != 2) {
					return null;
				}
				col[i] = new ColumnMap(temp3[0], temp3[1]);
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

	@Override
	public String writeExcel(List<Map> data, String headerStr, int headerCode,
			boolean append) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
