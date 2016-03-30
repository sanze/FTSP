package com.fujitsu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.mybatis.spring.SqlSessionTemplate;

import com.fujitsu.common.DataCollectDefine;

/**
 * @author xuxj
 * @version 2013/01/30,
 */

public class ExcelUtil {
	
	public static SqlSessionTemplate sqlSession = (SqlSessionTemplate) BeanUtil
			.getBean("sqlSession");
	
	private int columnHead_factory = 0;
	private int columnHead_type = 1;
	private int columnHead_productName = 2;
	
	private int columnHead_ALARM_CODE = 0;
	private int columnHead_PROBABLE_CAUSE = 1;
	private int columnHead_NATIVE_PROBABLE_CAUSE = 2;
	
	public static void main(String args[]){
//		ExcelUtil util = new ExcelUtil();
//		File file = new File("D:/FTSP3.0项目/02.BD/12.数据采集模块/网元类型.xls");
//		
//		try {
//			util.readExcelFileForProductMapping(file, 0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		ExcelUtil util = new ExcelUtil();
		File file = new File("C:/Users/xuxiaojun/Desktop/NBI-xTN 基础数据/厂商告警表--整理.xls");
		
		try {
			util.readExcelFileForAlarmTranfer(file, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readExcelFileForProductMapping(File file, int sheetNumber)
			throws Exception {
		Workbook workbook = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			workbook = Workbook.getWorkbook(in);
			Sheet sheet = workbook.getSheet(sheetNumber);

			int rowCount = sheet.getRows();
			
			for (int i = 1; i < rowCount; i++) {
				if (sheet.getRow(i) == null || sheet.getRow(i).length == 0) {
					continue;
				}

				Integer factoryFlag = null;
				String factory = sheet.getRow(i)[columnHead_factory]
						.getContents();

				if (factory.equals("HW")) {
					factoryFlag = 1;
				} else if (factory.equals("ZTE")) {
					factoryFlag = 2;
				} else if (factory.equals("Lucent")) {
					factoryFlag = 3;
				} else if (factory.equals("FIM")) {
					factoryFlag = 4;
				} else if (factory.equals("富士通")) {
					factoryFlag = 9;
				}else{
					continue;
				}

				int typeFlag = 0;

				String type = sheet.getRow(i)[columnHead_type].getContents();

				if (type.equals("SDH")) {
					typeFlag = 1;
				} else if (type.equals("WDM")) {
					typeFlag = 2;
				} else if (type.equals("OTN")) {
					typeFlag = 3;
				} else if (type.equals("PTN")) {
					typeFlag = 4;
				} else if (type.equals("微波")) {
					typeFlag = 5;
				} else if (type.equals("FTTX")) {
					typeFlag = 6;
				}

				String productName = sheet.getRow(i)[columnHead_productName]
						.getContents();

				Map parameter = new HashMap();

				parameter.put("BASE_PRODUCT_MAPPING_ID", null);
				parameter.put("PRODUCT_NAME", productName);
				parameter.put("TYPE", typeFlag);
				parameter.put("FACTORY", factoryFlag);

				String statement = "com.fujitsu.dao.mysql.DataCollectMapper.insertProductMapping";

				sqlSession.insert(statement, parameter);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
			workbook.close();
		}
	}
	
	public void readExcelFileForAlarmTranfer(File file, int sheetNumber)
			throws Exception {
		Workbook workbook = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			workbook = Workbook.getWorkbook(in);
			Sheet sheet = workbook.getSheet(sheetNumber);

			int rowCount = sheet.getRows();
			
			for (int i = 1; i < rowCount; i++) {
				if (sheet.getRow(i) == null || sheet.getRow(i).length == 0) {
					continue;
				}
				Integer factoryFlag = DataCollectDefine.FACTORY_ZTE_FLAG;

				String alarmCode = sheet.getRow(i)[columnHead_ALARM_CODE].getContents();
				String probableCause = sheet.getRow(i)[columnHead_PROBABLE_CAUSE]
						.getContents();
				String nativeProbableCause = sheet.getRow(i)[columnHead_NATIVE_PROBABLE_CAUSE]
						.getContents();
				if(nativeProbableCause.split(" ").length > 1){
					nativeProbableCause = nativeProbableCause.split(" ")[nativeProbableCause.split(" ").length-1];
				}

				Map parameter = new HashMap();

				parameter.put("ALARM_CODE", alarmCode);
				parameter.put("PROBABLE_CAUSE", probableCause);
				parameter.put("NATIVE_PROBABLE_CAUSE", nativeProbableCause);
				parameter.put("FACTORY", factoryFlag);

				String statement = "com.fujitsu.dao.mysql.DataCollectMapper.insertAlarmTransfer";

				sqlSession.insert(statement, parameter);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
			workbook.close();
		}
	}
	
	
}
