package com.fujitsu.filter;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class MyRequestWrapper extends HttpServletRequestWrapper {

	private Locale locale = null;

	public MyRequestWrapper(HttpServletRequest request) {
		super(request);
		HttpSession session = request.getSession();
		locale = (Locale) session.getAttribute("WW_TRANS_I18N_LOCALE");
	}

	/**
	 * struts2的BUG，如果重定向的话，国际化默认取HTTP请求头中的参数 替换HTTP请求参数
	 **/
	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if ("Accept-Language".equals(name) && locale != null) {
			value = locale.getLanguage() + "_" + locale.getCountry()
					+ value.substring(6, value.length());
		}
		return value;
	}

	@Override
	public Locale getLocale() {
		if (locale != null) {
			return locale;
		}
		return super.getLocale();
	}

}
