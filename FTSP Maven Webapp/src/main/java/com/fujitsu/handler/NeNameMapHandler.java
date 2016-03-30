package com.fujitsu.handler;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.fujitsu.common.CommonResult;

public class NeNameMapHandler {
	// 获取错误信息
		public static final CommonResult getNeName(String headerCode) {
			String headerInfo = "";
			CommonResult rs=new CommonResult();
			ResourceBundle bundle = ResourceBundle.getBundle("resourceConfig.ncResource."
					+ "neNameMap");
			try {
				headerInfo = bundle.getString(String.valueOf(headerCode));
				rs.setReturnResult(1);
				rs.setReturnMessage(headerInfo);
			} catch (MissingResourceException e) {
				rs.setReturnResult(0);
				rs.setReturnMessage("为查询到指定的Info");
			}
			return rs;
		}
}
