package fusioncharts;

import javax.servlet.http.HttpServletResponse;

public class FusionCharts
{
	/**
     * Encodes the dataURL before it's served to FusionCharts.
     * If you have parameters in your dataURL, you necessarily need to encode it.
     * @param strDataURL - dataURL to be fed to chart
     * @param addNoCacheStr - Whether to add aditional string to URL to disable caching of data
     * @return
     */

    public static String encodeDataURL(String strDataURL, String addNoCacheStr,
	    HttpServletResponse response) {
		String encodedURL = strDataURL;
		//Add the no-cache string if required
		if (addNoCacheStr.equals("true")) {
		    /*We add ?FCCurrTime=xxyyzz
		    If the dataURL already contains a ?, we add &FCCurrTime=xxyyzz
		    We send the date separated with '_', instead of the usual ':' as FusionCharts cannot handle : in URLs
		    */
		    java.util.Calendar nowCal = java.util.Calendar.getInstance();
		    java.util.Date now = nowCal.getTime();
		    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
			    "MM/dd/yyyy HH_mm_ss a");
		    String strNow = sdf.format(now);
		    if (strDataURL.indexOf("?") > 0) {
			encodedURL = strDataURL + "&FCCurrTime=" + strNow;
		    } else {
			strDataURL = strDataURL + "?FCCurrTime=" + strNow;
		    }
		    encodedURL = response.encodeURL(strDataURL);
	
		}
		return encodedURL;
    }	
	/**
	 * Method name: createChartHTMLByXmlUrl <BR>
	 * Description: 根据XML地址生产的Chart报表 <BR>
	 * Remark: <BR>
	 * 
	 * @return String<BR>
	 * @param chartSWF            flash文件
	 *        strXmlUrl           xml文件地址
	 *        chartId             报表
	 *        chartWidth          报表宽度
	 *        chartHeight         报表高度
	 *        debugMode           是否为调试模式
	 */
	public static String createChartHTMLByXmlUrl(String chartSWF, String strXmlUrl, String chartId, String chartWidth, int chartHeight,
			boolean debugMode)
	{
		/*
		 * Generate the FlashVars string based on whether dataURL has been
		 * provided or dataXML.
		 */
		String strFlashVars = "";
		Boolean debugModeBool = new Boolean(debugMode);
		// DataXML Mode
		strFlashVars = "chartWidth="
				+ chartWidth
				+ "&chartHeight="
				+ chartHeight
				+ "&debugMode="
				+ boolToNum(debugModeBool)
				+ "&dataURL="+ strXmlUrl + "";
		StringBuffer strBuf = new StringBuffer();

		// START Code Block for Chart
		strBuf.append("\t\t<!--START Code Block for Chart-->\n");
		strBuf
				.append("\t\t\t\t<div style='width:100%;'><object classid='clsid:d27cdb6e-ae6d-11cf-96b8-444553540000' codebase='cab/swflash.cab#version=8,0,0,0' width='"
						+ chartWidth
						+ "' height='"
						+ chartHeight
						+ "' id='"
						+ chartId + "'>\n");
		strBuf
				.append("\t\t\t\t	<param name='allowScriptAccess' value='always' /><param name='wmode' value='transparent'>\n");
		strBuf.append("\t\t\t\t	<param name='movie' value='" + chartSWF
				+ "'/>\n");
		strBuf.append("\t\t\t\t<param name='FlashVars' value=\"" + strFlashVars
				+ "\" />\n");
		strBuf.append("\t\t\t\t	<param name='quality' value='high' />\n");
		strBuf
				.append("\t\t\t\t<embed src='"
						+ chartSWF
						+ "' FlashVars=\""
						+ strFlashVars
						+ "\" quality='high' width='"
						+ chartWidth
						+ "' height='"
						+ chartHeight
						+ "' name='"
						+ chartId
						+ "' wmode='transparent' allowScriptAccess='always' type='application/x-shockwave-flash' pluginspage='cab/install_flash_player.exe' />\n");
		strBuf.append("\t\t</object></div>\n");
		// END Code Block for Chart
		strBuf.append("\t\t<!--END Code Block for Chart-->\n");
		return strBuf.substring(0);
	}
	
	
	/**
	 * Converts boolean to corresponding integer
	 * 
	 * @param bool
	 *            - The boolean that is to be converted to number
	 * @return int - 0 or 1 representing the given boolean value
	 */
	public static int boolToNum(Boolean bool)
	{
		int num = 0;
		if (bool.booleanValue())
		{
			num = 1;
		}
		return num;
	}
}
