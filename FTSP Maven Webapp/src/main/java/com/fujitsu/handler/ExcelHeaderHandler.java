package com.fujitsu.handler;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonResult;

/**
 * @author DaiHuijun
 * 
 */
public class ExcelHeaderHandler {
	
	// 获取错误信息
	public static final CommonResult getExcelHeader(int headerCode) {
		String headerInfo = "";
		CommonResult rs=new CommonResult();
		ResourceBundle bundle = ResourceBundle.getBundle("resourceConfig.excelHeader."
				+ CommonDefine.EXCELHEADER_CONFIG_FILE);
		try {
			headerInfo = bundle.getString(String.valueOf(headerCode));
			rs.setReturnResult(1);
			rs.setReturnMessage(headerInfo);
		} catch (MissingResourceException e) {
			rs.setReturnResult(0);
			rs.setReturnMessage("为查询到指定的ExcelHeader");
		}
		return rs;
	}

	public static void main(String args[]) {
		System.out.println(getExcelHeader(1));
	}
}
