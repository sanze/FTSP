/**
 * 
 */
package com.fujitsu.manager.nxReportManager.serviceImpl.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.util.TimeUtil;

/**
 * @author TianHongjun
 *
 */
public class OptAmpExcel extends BaseExcelUtil {

	private CellStyle exceptionStyle;
	public OptAmpExcel(String fileName) {
		super(fileName);
		exceptionStyle = createStyle(XSSFCellStyle.ALIGN_CENTER,
				XSSFCellStyle.VERTICAL_CENTER, true, true);
		setFont(exceptionStyle, (short)0, false, false, Font.COLOR_RED);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeData(Map aptAmpData) {
		String sheetName = "光放大器";
		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
		sheet = book.createSheet(sheetName);
		Map cond = (Map) aptAmpData.get("searchCond");
		String header =  cond.get("taskName").toString();
		int genType = (Integer) aptAmpData.get("genType");
		int period = Integer.parseInt(cond.get("period").toString());
		try {
			Date start = TimeUtil.parseString2Date(cond.get("start").toString(), CommonDefine.COMMON_FORMAT);
			Date end = TimeUtil.parseString2Date(cond.get("end").toString(), CommonDefine.COMMON_FORMAT);
			//如果是计划任务
			String startStr = "",endStr = "";
			if(genType == 0){
				if(period == 0){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT);
				}else if(period == 1){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT_MONTH);
				}
				header += "        " + startStr;
			//如果是手动生成
			}else if(genType == 1){
				if(period == 0){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT);
					endStr = TimeUtil.parseDate2String(end, CommonDefine.REPORT_CN_FORMAT);
					header += "        " + startStr + " -> " + endStr;
				}else if(period == 1){
					startStr = TimeUtil.parseDate2String(start, CommonDefine.REPORT_CN_FORMAT_MONTH);
					endStr = TimeUtil.parseDate2String(end, CommonDefine.REPORT_CN_FORMAT_MONTH);
					header += "        " + startStr + " -> " + endStr + " 每月" + cond.get("pmDate").toString() + "日";
				}					
			}
			cond.put("genType", genType + "");
			dates = this.getColumnNames(cond);
			System.out.println(dates);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int curRow = 0;
		writeCell(curRow++, 0, header, headerStyle);
		//合并A1-M1
		merge(0, 0, 0, 12);
		List<Map> detail = (List<Map>) aptAmpData.get("neRows");
		for(Map m:detail){
			//填写每一组信息
			curRow = writeInfo(m, curRow);
			//填写表头
			curRow = writeColumn(curRow);
			//填写具体数据
			List<Map>ampDetail = (List<Map>) m.get("unitRows");
			for(Map rec:ampDetail){
				writeRecord(rec, curRow++);
			}
			curRow++;
		}
		close();
	}
	/**
	 * 填写波分方向信息
	 * @param data 波分方向信息
	 * @param startRow 起始行（0为第一行
	 * @return
	 */
    @SuppressWarnings("rawtypes")
	private int writeInfo(Map data, int startRow){
    	int curRow = startRow;
		//单位:
		writeCell(curRow, 0, "单位：" + getVal(data, "DEPARTMENT"), null);
		//网络名称：
		writeCell(curRow++, 7, "网络名称：" + getVal(data, "NET_WORK_NAME"), null);
		//设备类型：
		writeCell(curRow, 0, "设备类型：" + getVal(data, "EQPT_TYPE"), null);
		//站名：
		writeCell(curRow++, 7, "站名：" + getVal(data, "STATION"), null);
		//设备名称:
		writeCell(curRow, 0, "设备名称:" + getVal(data, "EQPT_NAME"), null);
		//方向：
		writeCell(curRow++, 7, "方向：" + getVal(data, "DIRECTION"), null);
		return curRow;
    }
    
	/**
	 * 填写表头信息
	 * @param startRow 起始行（0为第一行）
	 * @param month 月份
	 * @return
	 */
    private int writeColumn(int startRow){
    	int curRow = startRow;
		//填写表头 
		writeCell(curRow, 0, "环网链路方向", centerStyle);
		merge(startRow, 0, startRow+1, 0, true);
		writeCell(curRow, 1, "光放大器名称", centerStyle);
		merge(startRow, 1, startRow+1, 1, true);
		writeCell(curRow, 2, "光放大器级数", centerStyle);
		merge(startRow, 2, startRow+1, 2, true);
		writeCell(curRow, 3, "型号（增益饱和输出）", centerStyle);
		merge(startRow, 3, startRow+1, 3, true);
		writeCell(curRow, 4, "槽位编号", centerStyle);
		merge(startRow, 4, startRow+1, 4, true);
		writeCell(curRow, 5, "标称增益", centerStyle);
		merge(startRow, 5, startRow+1, 5, true);
		writeCell(curRow, 6, "饱和输出", centerStyle);
		merge(startRow, 6, startRow+1, 6, true);
		writeCell(curRow, 7, "容量", centerStyle);
		merge(startRow, 7, startRow+1, 7, true);
		writeCell(curRow, 8, "开通业务波数", centerStyle);
		merge(startRow, 8, startRow+1, 8, true);
		writeCell(curRow, 9, "方向", centerStyle);
		merge(startRow, 9, startRow+1, 9, true);
		writeCell(curRow, 10, "理想收光功率", centerStyle);
		merge(startRow, 10, startRow+1, 10, true);
//    	Row r = null;
//    	r = sheet.getRow(curRow);
//    	r.setHeight((short) 200);
		for (int i = 0; i < dates.size(); i++) {
			int date = dates.get(i);
			String day = (date/10000) + "年" + (date%10000/100) + "月" + (date%100) + "日";
			writeCell(curRow, 11 + 2 * i, day, centerStyle);
			merge(startRow, 11 + 2 * i, startRow, 12 + 2 * i, true);
			writeCell(curRow+1, 11 + 2 * i, "收光功率", centerStyle);
			writeCell(curRow+1, 12 + 2 * i, "发光功率", centerStyle);
		}
		int col = 6;
		for (int i = 0; i < 11+2*dates.size(); i++) {
			sheet.setColumnWidth(i, 9*256);
		}
		curRow+=2;
		return curRow;
    }
    /**
     * 写入一条记录
     * @param data
     * @param startRow
     */
    /*
     * "DISPLAY_NAME" : "JSTI",
                    "rpms_u" : [],
                    "STD_WAVE_NUM" : 0,
                    "BASE_UNIT_ID" : 6191,
                    "tpms_u" : [],
                    "ACTUAL_WAVE_NUM" : 0*/
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeRecord(Map data, int startRow){
		//环网链路方向
		writeCell(startRow, 0, getVal(data, "DIRECTION_LINK"), borderStyle);
		//光放大器名称
		writeCell(startRow, 1, getVal(data, "AMPLIFIER_NAME"), borderStyle);
		//光放大器级数
		writeCell(startRow, 2, getVal(data, "OPTICAL_LEVEL"), borderStyle);
		//型号（增益饱和输出）
		writeCell(startRow, 3, getVal(data, "MODEL"), borderStyle);
		//槽位编号
		writeCell(startRow, 4, getVal(data, "SLOT_NO"), borderStyle);
		//标称增益
		double bczy = getFloat(data, "TYPICAL_GAIN");
		writeCell(startRow, 5, getVal(data, "TYPICAL_GAIN"), borderStyle);
		//饱和输出
		double bhsc = getFloat(data, "MAX_OUT");
		writeCell(startRow, 6, getVal(data, "MAX_OUT"), borderStyle);
		//容量
		int rl = getInt(data, "STD_WAVE_NUM");
		writeCell(startRow, 7, getVal(data, "STD_WAVE_NUM"), borderStyle);
		//开通业务波数
		int ywbs = getInt(data, "ACTUAL_WAVE_NUM");
		writeCell(startRow, 8, getVal(data, "ACTUAL_WAVE_NUM"), borderStyle);
		//方向
		writeCell(startRow, 9, getVal(data, "DIRECTION"), borderStyle);
		/*
		 * “理想收光功率”计算方法：
		 * 饱和输出-标称增益-10*LOG10(容量）+10*LOG10（开通业务）
		 */
		//理想收光功率,此处直接计算出来，不使用公式
		double lxsggl = bhsc - bczy - 10 * Math.log10(rl) + 10 * Math.log10(ywbs);
		if(rl>0 && ywbs >0 && bhsc > ERROR_VALUE && bczy > ERROR_VALUE){
			writeCell(startRow, 10, lxsggl, float2Style);
		}else{
			writeCell(startRow, 10, "-", borderStyle);
		}
		//----------------------------------------------------
		/*
		 * 异常判断方法：
		 * ｜本月收光功率-理想收光功率｜≥2，分析结论：异常，光功率数据字体为红色
		 * ｜本月收光功率-理想收光功率｜<2，分析结论：正常
		 */
		List<Map> rpls = (List<Map>) data.get("rpms_u");
		List<Map> tpls = (List<Map>) data.get("tpms_u");
		for (int i = 0; i < dates.size(); i++) {
			int date = dates.get(i);
			//收光功率
			double sggl = getValue(rpls, date);
			boolean isNormal = false;
			if(Math.abs(sggl-lxsggl)<2){
				isNormal = true;
			}
			if(sggl > ERROR_VALUE){
				writeCell(startRow, 11 + 2*i, getString(rpls, date), isNormal ? borderStyle:exceptionStyle);
			}else{
				writeCell(startRow, 11 + 2*i, "-", borderStyle);
			}
			//发光功率
			writeCell(startRow, 12 + 2*i, getString(tpls, date), borderStyle);
		}
    	
    }
	/**
	 * 从PM列表中寻找需要的string数据
	 * @param pmList
	 * @param date
	 * @return
	 */
	private String getString(List<Map> pmList,int date){
		String rv = "-";
		if(pmList!=null && pmList.size()>0){
			for(Map pm:pmList){
				int curDate = Integer.parseInt(pm.get("RTRV_TIME").toString());
				if(curDate == date){
					rv = (String)pm.get("PM_VALUE");
					break;
				}
			}
		}
		return rv;
	}
	/**
	 * 从PM列表中寻找需要的double数据
	 * @param pmList
	 * @param date
	 * @return
	 */
	private double getValue(List<Map> pmList,int date){
		String rv = getString(pmList, date);
		if("-".equals(rv)){
			return ERROR_VALUE;
		}else{
			return Double.parseDouble(rv);
		}
	}
	/**
	 * 测试入口
	 * @param args
	 */
	public static void main(String[] args) {
		String datStr = "{\"searchCond\":{\"TABLE_NAME\":\"t_pm_origi_data_8_2014_07\",\"start\":\"2014-07-01 00:00:00\",\"dataSrc\":\"0\",\"userId\":\"-1\",\"privilege\":\"3\",\"retrivalTime\":\"1\",\"continueAbnormal\":\"1\",\"period\":\"0\",\"taskName\":\"AMP_REPORT\",\"pmDate\":\"1\",\"end\":\"2014-07-31 23:59:59\"},\"genType\":1,\"neRows\":[{\"DIRECTION\":\"TEST\",\"unitRows\":[{\"DISPLAY_NAME\":\"EONAD\",\"MAX_OUT\":\"14\",\"T_PTP_ID\":97491,\"MODEL\":\"PA1414\",\"rpms_u\":[{\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-16.16\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-15.77\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0}],\"R_PTP_ID\":97492,\"STD_WAVE_NUM\":1,\"BASE_UNIT_ID\":10370,\"tpms_u\":[{\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-16.16\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"BASE_PTP_ID\":97491,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10370,\"PM_VALUE\":\"-15.97\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0}],\"TYPICAL_GAIN\":\"14\",\"ACTUAL_WAVE_NUM\":1},{\"DISPLAY_NAME\":\"OCI\",\"MAX_OUT\":\"14\",\"T_PTP_ID\":97498,\"MODEL\":\"PA1414\",\"rpms_u\":[{\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.54\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.52\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0}],\"R_PTP_ID\":97499,\"STD_WAVE_NUM\":1,\"BASE_UNIT_ID\":10372,\"tpms_u\":[{\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140701\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.54\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":1,\"day\":2,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404144000000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0},{\"BASE_PTP_ID\":97498,\"RTRV_TIME\":\"20140702\",\"BASE_UNIT_ID\":10372,\"PM_VALUE\":\"11.52\",\"EXCEPTION_LV\":0,\"RETRIEVAL_TIME\":{\"date\":2,\"day\":3,\"hours\":0,\"minutes\":0,\"month\":6,\"nanos\":0,\"seconds\":0,\"time\":1404230400000,\"timezoneOffset\":-480,\"year\":114},\"EXCEPTION_COUNT\":0}],\"TYPICAL_GAIN\":\"14\",\"ACTUAL_WAVE_NUM\":1}],\"STATION\":\"ccc\",\"EQPT_NAME\":\"11-绵阳236局\",\"NET_WORK_NAME\":\"fdadf\",\"DEPARTMENT\":\"adfa\",\"EMS_ID\":8},{\"DIRECTION\":\"大幅\",\"unitRows\":[{\"DISPLAY_NAME\":\"OCI\",\"MAX_OUT\":\"14\",\"T_PTP_ID\":103039,\"MODEL\":\"PA1414\",\"rpms_u\":[],\"R_PTP_ID\":103038,\"BASE_UNIT_ID\":10827,\"tpms_u\":[],\"TYPICAL_GAIN\":\"14\"}],\"STATION\":\"dddfs\",\"EQPT_NAME\":\"21-绵阳236局\",\"NET_WORK_NAME\":\"adfa\",\"DEPARTMENT\":\"adfa\",\"EMS_ID\":8}]}";
		Map dat = JSONObject.fromObject(datStr);
		OptAmpExcel xls = new OptAmpExcel("test "
				+TimeUtil.parseDate2String(new Date(), CommonDefine.REPORT_CN_FORMAT_24H)
				+ ".xlsx");
		xls.writeData(dat);
	}
	
}
