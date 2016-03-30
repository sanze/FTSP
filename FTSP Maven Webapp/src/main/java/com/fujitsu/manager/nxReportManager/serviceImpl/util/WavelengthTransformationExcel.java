package com.fujitsu.manager.nxReportManager.serviceImpl.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.WorkbookUtil;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.util.TimeUtil;

@SuppressWarnings({ "rawtypes" })
public class WavelengthTransformationExcel extends BaseExcelUtil{
	private List<Integer> days;
	public WavelengthTransformationExcel(String fileName) {
		super(fileName);
	}
	/**
	 * 填写波分方向信息
	 * @param data 波分方向信息
	 * @param startRow 起始行（0为第一行）
	 * @param notPM 是否是PM表
	 * @return
	 */
    private int writeWaveInfo(Map data, int startRow, boolean notPM){
    	int curRow = startRow;
		//单位:
		writeCell(curRow, 0, "单位：" + getVal(data, "UNIT"), null);
		//网络名称：
		writeCell(curRow++, 9, "网络名称：" + getVal(data, "NET_WORK_NAME"), null);
		if(notPM){
			//设备类型：
			writeCell(curRow, 0, "设备类型：" + getVal(data, "PRODUCT_NAME"), null);
			//站名：
			writeCell(curRow++, 9, "站名：" + getVal(data, "STATION"), null);
		}
		//设备名称：
		writeCell(curRow, 0, "设备名称：" + getVal(data, "DISPLAY_NAME"), null);
		//方向：
		writeCell(curRow, 4, "方向：" + getVal(data, "DIRECTION"), null);
		//容量（波数）/实开（波数）：
		writeCell(curRow++, 9, "容量（波数）/实开（波数）：" + 
				getVal(data, "STD_WAVE_NUM")+ 
				"/" + 
				getVal(data, "ACTUAL_WAVE_NUM"), null);
		return curRow;
    }
	/**
	 * 填写表头信息
	 * @param startRow 起始行（0为第一行）
	 * @return
	 */
    private int writePMColumn(int startRow){
    	int curRow = startRow;
		//填写表头 
		writeCell(curRow, 0, "机盘型号名称", centerStyle);
		merge(curRow, 0, curRow+1, 0, true);
		writeCell(curRow, 1, "保护方式", centerStyle);
		merge(curRow, 1,curRow+1, 1, true);
		writeCell(curRow, 2, "槽位编号", centerStyle);
		merge(curRow, 2,curRow+1, 2, true);
		writeCell(curRow, 3, "波道/波长", centerStyle);
		merge(curRow, 3,curRow+1, 3, true);
		writeCell(curRow, 4, "端口", centerStyle);
		merge(curRow, 4,curRow+1, 4, true);
		writeCell(curRow, 5, "性能事件", centerStyle);
		merge(curRow, 5,curRow+1, 5, true);
		writeCell(curRow, 6, "性能值", centerStyle);
		curRow++;
		int col = 6;
		int colWidths[] = {14, 18, 36, 11, 24, 24};
		for (int i = 0; i < colWidths.length; i++) {
			sheet.setColumnWidth(i, colWidths[i]*256);
		}
		for (int i = 0; i < days.size(); i++) {
			int day = days.get(i);
			sheet.setColumnWidth(col, 9*256);
			int month = (day % 10000) / 100;
			int date = day % 100;
			writeCell(curRow, col++, month + "月" + date + "日", centerStyle);
		}
		merge(curRow-1, 6,curRow-1, col-1, true);
		curRow++;
		return curRow;
    }
	/**
	 * 填写表头信息
	 * @param startRow 起始行（0为第一行）
	 * @return
	 */
    private int writeDataColumn(int startRow){
    	int curRow = startRow;
		//填写表头
		writeCell(curRow, 0, "机盘型号名称", centerStyle);
		merge(curRow, 0, curRow+1, 0, true);
		writeCell(curRow, 1, "保护方式", centerStyle);
		merge(curRow, 1,curRow+1, 1, true);
		writeCell(curRow, 2, "槽位编号", centerStyle);
		merge(curRow, 2,curRow+1, 2, true);
		writeCell(curRow, 3, "波道/波长", centerStyle);
		merge(curRow, 3,curRow+1, 3, true);
		writeCell(curRow, 4, "业务侧", wrapStyle);
		merge(curRow, 4,curRow, 8, true);
		writeCell(curRow, 9, "波分侧", wrapStyle);
		merge(curRow, 9,curRow, 14, true);
		//给波分侧单元格右侧的单元格加上边框
		setBorder(curRow, 15,curRow, 15);
		curRow++;
		writeCell(curRow, 4, "接口编号", wrapStyle);
		writeCell(curRow, 5, "发光功率", wrapStyle);
		writeCell(curRow, 6, "收光功率", wrapStyle);
		writeCell(curRow, 7, "过载点", wrapStyle);
		writeCell(curRow, 8, "灵敏度", wrapStyle);
		writeCell(curRow, 9, "发光功率", wrapStyle);
		writeCell(curRow, 10, "收光功率", wrapStyle);
		writeCell(curRow, 11, "过载点", wrapStyle);
		writeCell(curRow, 12, "灵敏度", wrapStyle);
		writeCell(curRow, 13, "收光功率最大差异", wrapStyle);
		writeCell(curRow, 14, "收光富余度", wrapStyle);
		writeCell(curRow, 15, "分析", wrapStyle);
		curRow++;
		int colWidths[] = {9, 9, 9, 11, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9};
		for (int i = 0; i < colWidths.length; i++) {
			sheet.setColumnWidth(i, colWidths[i]*256);
		}
		return curRow;
    }
    
    @SuppressWarnings("unchecked")
	private boolean writeDataSheet(List<Map> waveTransOUTData, Map<String, String> searchCond, int year, int month, int day){
    	try{
    		int dateKey = year*10000+month*100+day;
    		//对Sheet名称处理，使其合法（类似文件名）
    		String sheetName = "波长转换OUT_" + month + "月" + day + "日";
    		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
    		sheet = book.createSheet(sheetName);
    		//填写表头
    		String header =  "波长转换盘测试表格    " + year + "年" + month + "月" + day + "日";
    		writeCell(0, 0, header, headerStyle);
    		//合并A1-P1
    		merge(0, 0, 0, 15);
    		
    		String[] protectModes = {"-","光通道1+1保护组", "光复用段1+1保护组"};
    		int curRowLeft = 1,curRowRight =1, curGroup;
    		for(Map waveInfo:waveTransOUTData){
//    			JSONObject o = JSONObject.fromObject(waveInfo);
//    			System.out.println(o.toString());
    			//写入波分信息
    			curRowLeft = writeWaveInfo(waveInfo, curRowLeft, true);
    			//写入表头
    			curRowLeft = writeDataColumn(curRowLeft);
    			//写入数据
    			//首先写入ptp基本信息
    			List<Map>ptpList = (List<Map>) waveInfo.get("ptp");
    			List<Map>pm = (List<Map>) waveInfo.get("pm");
//    			System.out.println(JSONArray.fromObject(pm).toString());
    			int preUnitId = -247986;
    			int businessGroupStart = curRowLeft;
    			int waveGroupStart = curRowRight = curRowLeft;
    			curGroup = waveGroupStart;
				int businessPortNum,wavePortNum,maxNum = 0;
				double rplMax = -999999, rplMin = 999999;
				List<String> anylysedCells = new ArrayList<String>();
    			for(int i=0;i<ptpList.size();i++){
    				Map ptpInfo = ptpList.get(i);
    				if(ptpInfo.get("SKIP_FLAG")!=null) //For combine in and out ptp
    					continue;
    				int curUnitId = Integer.parseInt(ptpInfo.get("BASE_UNIT_ID").toString());
    				//ptp未必关联，所以特殊处理
    				Object v = ptpInfo.get("BASE_PTP_ID");
    				int curPtpId = 0xffffffff;
    				if(v!=null){
        				curPtpId = Integer.parseInt(v.toString());
    				}
    				boolean  isDirGroupAbnormal = false;
    				if(curUnitId != preUnitId){
    					curRowRight = curRowLeft = businessGroupStart + maxNum;
    					isDirGroupAbnormal = false;
    					preUnitId = curUnitId;
    					businessGroupStart = curRowLeft;
    					waveGroupStart = curRowRight;
    					//机盘型号名称	保护方式	槽位编号	波道/波长
	    				writeCell(curRowLeft, 0, getVal(ptpInfo, "MODEL"), wrapStyle);
	    				writeCell(curRowLeft, 1, protectModes[getInt(ptpInfo, "PROTECT_MODE")], wrapStyle);
	    				writeCell(curRowLeft, 2, getVal(ptpInfo, "SLOT"), wrapStyle);
	    				writeCell(curRowLeft, 3, getVal(ptpInfo, "WAVE_LENGTH"), wrapStyle);
	    				if(v==null){
	    					//当板卡没有关联ptp的时候，填上一堆空格
	    					maxNum = 1;
	    					for (int k = 4; k < 15; k++) {
    							writeCell(businessGroupStart, k, "", borderStyle);
							}
	    					continue;
	    				}
    					//新的板卡
    					businessPortNum = Integer.parseInt(ptpInfo.get("BUSINESS_PORT_NUM").toString());
    					wavePortNum = Integer.parseInt(ptpInfo.get("WAVE_PORT_NUM").toString());
    					maxNum = Math.max(businessPortNum, wavePortNum);
//    					System.out.println("板卡占据" + maxNum + "行");
	    				//合并单元格
    					merge(curRowLeft, 0, curRowLeft + maxNum - 1, 0, true);
    					merge(curRowLeft, 1, curRowLeft + maxNum - 1, 1, true);
    					merge(curRowLeft, 2, curRowLeft + maxNum - 1, 2, true);
    					merge(curRowLeft, 3, curRowLeft + maxNum - 1, 3, true);
    					setBorder(curRowLeft, 14, curRowLeft + maxNum - 1, 14);
    					setBorder(curRowLeft, 15, curRowLeft + maxNum - 1, 15);
    					//填写业务侧编号 TODO 不应该是max吗？（理论上businessPortNum肯定是maxNum，但是以防万一，还是换成maxNum）
    					for (int j = 0; j < maxNum; j++) {
    						writeCell(businessGroupStart + j, 4, j+1, borderStyle);
    						for (int k = 5; k < 16; k++) {
    							writeCell(businessGroupStart + j, k, "", borderStyle);
							}
						}
    				}
    				int ptpType = Integer.valueOf(ptpInfo.get("PTP_TYPE").toString());
    				double TPL_CUR = getFloat(pm, curPtpId, "TPL_CUR", dateKey);
    				double RPL_CUR = getFloat(pm, curPtpId, "RPL_CUR", dateKey);
    				double MAX_IN = getFloat(ptpInfo, "MAX_IN");
    				double MIN_IN = getFloat(ptpInfo, "MIN_IN");
    				if(ptpType == 1){
//    					System.out.println();
	    				//如果是业务
	    				//发光功率、收光功率、过载点、灵敏度
    					writeFloat(curRowLeft, 5, TPL_CUR, float2Style);
    					writeFloat(curRowLeft, 6, RPL_CUR, float2Style);
    					writeFloat(curRowLeft, 7, MAX_IN, borderStyle);
    					writeFloat(curRowLeft, 8, MIN_IN, borderStyle);
	    				curRowLeft++;
	    			}else if(ptpType == 2){
	    				//如果是波分
    					writeFloat(curRowRight , 9, TPL_CUR, float2Style);
    					writeFloat(curRowRight , 10, RPL_CUR, float2Style);
    					writeFloat(curRowRight , 11, MAX_IN, borderStyle);
    					writeFloat(curRowRight , 12, MIN_IN, borderStyle);
	    				//写入公式
	    				String formular = "MIN(K" + (curRowRight+1) + "-M" + (curRowRight+1) + 
	    						",L" + (curRowRight+1) + "-K" + (curRowRight+1) + ")";
	    				String clearErrorFomular = "=IF(ISERROR(" + formular + "),\"-\",(" + formular + "))";
	    				writeFormular(curRowRight, 14, clearErrorFomular,
	    						borderStyle);
	    				//分析 列！
	    				/*｜波分侧收光功率最大值-波分侧收光功率最小值｜>4，
	    				 * 本方向所有板卡判断为异常。
	    				 *（波分侧过载点-波分侧收光功率）和（波分侧收光功率-波分侧灵敏度）的最小值<3，所在板卡分析结论：异常
	    				 *｜波分侧收光功率最大值-波分侧收光功率最小值｜≤4，&&
						 *（波分侧过载点-波分侧收光功率）和（波分侧收光功率-波分侧灵敏度）的最小值≥3，分析结论：正常
	    				 */
//	    				System.out.println("当前单元格：" + getCellPos(curRowRight, 15));
//	    				System.out.println("波分侧过载点 = " + (isDataValid(MAX_IN)?MAX_IN:"-"));
//	    				System.out.println("波分侧收光功率 = " + (isDataValid(RPL_CUR)?RPL_CUR:"-"));
//	    				System.out.println("波分侧灵敏度= " + (isDataValid(MIN_IN)?MIN_IN:"-"));
//	    				System.out.println("（波分侧过载点-波分侧收光功率）和（波分侧收光功率-波分侧灵敏度）的最小值= " +
//	    						((isDataValid(MAX_IN)
//		    							&& isDataValid(MIN_IN)
//		    							&& isDataValid(RPL_CUR))?Math.min(MAX_IN - RPL_CUR, RPL_CUR-MIN_IN):"-"));
	    				//-----
	    				if(isDataValid(RPL_CUR)){
	    					rplMax = Math.max(rplMax, RPL_CUR);
		    				rplMin = Math.min(rplMin, RPL_CUR);
//		    				System.out.println("\trplMax = " + rplMax);
//		    				System.out.println("\trplMin = " + rplMin);
//		    				System.out.println("\trplMax - rplMin = " + (rplMax - rplMin));
	    				}else{
//	    					System.out.println("\t\t发光功率无效！");
	    				}
	    				// 写入 发光功率	收光功率	过载点	灵敏度
    					if(isDataValid(MAX_IN)
    							&& isDataValid(MIN_IN)
    							&& isDataValid(RPL_CUR)){
	    					boolean isAbnormal = Math.min(MAX_IN - RPL_CUR, RPL_CUR-MIN_IN)<3;
	    					if(isAbnormal){
		    					writeCell(curRowRight, 15, "异常", borderStyle);
//								System.out.println("异常单元格：" + getCellPos(curRowRight, 15));
	    					}else{
		    					writeCell(curRowRight, 15, "正常", borderStyle);
	    					}
    					}else{
    						writeCell(curRowRight, 15, "-", borderStyle);
    					}
    					anylysedCells.add(getCellPos(curRowRight, 15));
	    				
	    				curRowRight++;
	    			}
    			}
//				System.out.println(anylysedCells.toString());
				//写入-=[收光功率最大差异]=-公式
    			if(curGroup != curRowLeft){
					String range = "K" + (curGroup + 1) + ":K" + (maxNum + waveGroupStart);
					writeFormular(curGroup, 13,
							"=MAX(" + range + ")-MIN(" + range + ")",
							null);
					merge(curGroup, 13, (maxNum + waveGroupStart-1), 13, true);
    			}
				setBorder(curGroup, 10, (maxNum + waveGroupStart-1), 10);
				setBorder(curGroup, 11, (maxNum + waveGroupStart-1), 11);
				setBorder(curGroup, 15, (maxNum + waveGroupStart-1), 15);
				//判断是否｜波分侧收光功率最大值-波分侧收光功率最小值｜>4
				if(Math.abs(rplMax - rplMin) > 4){
//					System.out.println(anylysedCells.toString());
					//重置之前的波分ptp为异常
					for(int k = 0; k < anylysedCells.size(); k++){
//						System.out.println("异常单元格：" + anylysedCells.get(k));
						writeCell(anylysedCells.get(k), "异常", borderStyle);
					}
				}
				curRowLeft = maxNum + waveGroupStart;
				//添加波分方向结尾处的说明文字
				String psInfo = "说明：本表每方向一张，波分侧接收功率最大差异是用波分侧接收最大功率减去最小功率（同一方向设备内比较）差异不能超过4dB；接收光功率必须有3dB富裕度，否则分析原因进行整治；数据单位为dBm、dB在表格中不填写单位";
				writeCell(curRowLeft, 0, psInfo, wrapStyle);
				merge(curRowLeft, 0,curRowLeft+1, 15, true);
				//跳转到下一张表的起始行
				curRowLeft += 3;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    /**
     * 判断数据是否有效（以ERROR_VALUE为判断标准）
     * @param v
     * @return
     */
    private boolean isDataValid(double v){
    	if(v > ERROR_VALUE){
    		return true;
    	}
    	return false;
    }
    /**
     * 写入float数据，当数据为ERROR_VALUE时，写入“-”
     * @param row
     * @param col
     * @param v float数据
     * @param style
     */
    private void writeFloat(int row, int col, double v, CellStyle style){
		if(v > ERROR_VALUE){
			writeCell(row , col, v, float2Style);
		}else{
			writeCell(row , col, "-", borderStyle);
		}
    }
    @SuppressWarnings("unchecked")
	private boolean writePMSheet(List<Map> waveTransOUTData, Map<String, String> searchCond){
    	try{
			//按日期写入波长转换OUT
			//日期转换String->Date 格式"2014-06-01 00:00:00" 
			Date start = TimeUtil.parseString2Date(searchCond.get("start"), CommonDefine.COMMON_FORMAT);
//			System.out.println("Start = " + TimeUtil.parseDate2String(start));
			Date end = TimeUtil.parseString2Date(searchCond.get("end"), CommonDefine.COMMON_FORMAT);
//			System.out.println("End = " + TimeUtil.parseDate2String(end));
			int year = start.getYear() + 1900;
			int month = start.getMonth() + 1;
			int startDay = start.getDate();
			int endDay = end.getDate();
    		//对Sheet名称处理，使其合法（类似文件名）
    		String sheetName = "波长转换OUT (性能值)";
    		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
    		sheet = book.createSheet(sheetName);
    		//填写表头
    		String header =  "波长转换盘测试表格  (性能值)    "  + year + "年";
    		if(startDay == endDay){
    			header += month + "月";
    		}else{
    			header += month + "月" + startDay + "日  -> "  + year + "年" + month + "月" + endDay + "日";
    		}
    		writeCell(0, 0, header, headerStyle);
    		//合并A1-P1
    		merge(0, 0, 0, 15);
    		
    		String[] protectModes = {"-", "光通道1+1保护组", "光复用段1+1保护组"};
    		int curRow = 1, ptpPMCount = 0;
//	    	System.out.println("------------------------------------");
    		for(Map waveInfo:waveTransOUTData){
    			//写入表头波分信息
    			curRow = writeWaveInfo(waveInfo, curRow, false);
    			//写入表头
    			curRow = writePMColumn(curRow);
    			//写入具体信息
    		    //首先写入ptp基本信息
    		    List<Map>ptpList = (List<Map>) waveInfo.get("ptp");
    		    List<Map>pmList = (List<Map>) waveInfo.get("pm");
    		    for(int i=0;i<ptpList.size();i++){
    		    	Map ptpInfo = ptpList.get(i);
    		    	writeCell(curRow, 0, getVal(ptpInfo, "MODEL"), wrapStyle);
    		    	writeCell(curRow, 1, protectModes[getInt(ptpInfo, "PROTECT_MODE")], wrapStyle);
    		    	writeCell(curRow, 2, getVal(ptpInfo, "SLOT"), wrapStyle);
    		    	writeCell(curRow, 3, getVal(ptpInfo, "WAVE_LENGTH"), wrapStyle);
    		    	writeCell(curRow, 4, getVal(ptpInfo, "PORT_NO"), wrapStyle);
    		    	ptpPMCount = 0;
		    		int ptpId = getInt(ptpInfo, "BASE_PTP_ID");
    		    	Map nvp = new HashMap();
    		    	for(Map pmInfo:pmList){
    		    		//查出当前ptp下所有性能名称、索引
    		    		String key = getVal(pmInfo, "PM_STD_INDEX");
    		    		String value = getVal(pmInfo, "PM_DESCRIPTION");
    		    		int curPtpId = getInt(pmInfo, "BASE_PTP_ID");
    		    		if(curPtpId == ptpId && nvp.get(key)==null){
    		    			ptpPMCount++;
    		    			nvp.put(key, value);
    		    			//填写事件名
    		    			writeCell(curRow, 5, value, wrapStyle);
    		    			//查找相应日期的性能值
    		    			for (int j = 0; j < days.size(); j++) {
								int day = days.get(j);
								writeCell(curRow, j + 6, getString(pmList, ptpId, key, day), wrapStyle);
							}
    		    			curRow++;
    		    		}
    		    	}
    		    	for (int j = 0; ptpPMCount==0 && j < days.size(); j++) {
    		    		setBorder(curRow, j + 6, curRow, j + 6);
    		    	}
    		    	if(ptpPMCount==0){
    		    		writeCell(curRow, 5, "-", wrapStyle);
    		    		for (int j = 0; j < days.size(); j++) {
							writeCell(curRow, j + 6, "-", wrapStyle);
						}
		    			curRow++;
		    			ptpPMCount = 1;
    		    	}
    		    	//一组PTP的PM填充完毕之后进行单元格合并
    		    	merge(curRow - ptpPMCount, 0, curRow - 1, 0, true);
    		    	merge(curRow - ptpPMCount, 1, curRow - 1, 1, true);
    		    	merge(curRow - ptpPMCount, 2, curRow - 1, 2, true);
    		    	merge(curRow - ptpPMCount, 3, curRow - 1, 3, true);
    		    	merge(curRow - ptpPMCount, 4, curRow - 1, 4, true);
    		    }
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }

	public boolean close(){
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
    public static WavelengthTransformationExcel getInstance(String fileName) {
    	_instance = new WavelengthTransformationExcel(fileName);
		return (WavelengthTransformationExcel)_instance;
	}
    /**
     * 对于同一文件分批导出的，使用此方法获取返回结果
     * @return ExportResult
     */
    public Map<String, Object> getResult(){
//    	close();
    	Map<String, Object> rv = new HashMap<String, Object>();
		rv.put("EXPORT_TIME", new Date());
		rv.put("REPORT_NAME", file.getName());
		rv.put("EXCEL_URL", file.getPath());
		rv.put("SIZE", (int)(file.length()/1024));
		return rv;
    }

	public void writeData(List<Map> waveTransOUTData, Map<String, String> searchCond) {
		try {
			days = getColumnNames(searchCond);
			//按日期写入波长转换OUT
			//日期转换String->Date 格式"2014-06-01 00:00:00"
			Date start = TimeUtil.parseString2Date(searchCond.get("start"), CommonDefine.COMMON_FORMAT);
//			System.out.println("Start = " + TimeUtil.parseDate2String(start));
			Date end = TimeUtil.parseString2Date(searchCond.get("end"), CommonDefine.COMMON_FORMAT);
//			System.out.println("End = " + TimeUtil.parseDate2String(end));
			int year = start.getYear() + 1900;
			int month = start.getMonth() + 1;
			int startDay = start.getDate();
			int endDay = end.getDate();
//			System.out.println("===> " + month + "月{" + startDay + "->" + endDay + "}");
			for (int i = 0; i < days.size(); i++) {
				int day = days.get(i);
				int y = day / 10000;
				int m = (day % 10000) / 100;
				int d = day % 100;
				writeDataSheet(waveTransOUTData,searchCond, y, m, d);
			}
			//写入PM表
			writePMSheet(waveTransOUTData, searchCond);
			//关闭
			close();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 从PM列表中寻找需要的string数据
	 * @param pmList
	 * @param ptpId
	 * @param key
	 * @return
	 */
	private String getString(List<Map> pmList, int ptpId, String key, int date){
		String rv = "-";
		if(pmList!=null && pmList.size()>0){
			for(Map pm:pmList){
				int curPtpId = Integer.parseInt(pm.get("BASE_PTP_ID").toString());
				int curDate = Integer.parseInt(pm.get("RTRV_TIME").toString());
				int pairPtpId = -999;
				if(pm.get("PAIR_PTP_ID")!=null){
					pairPtpId = Integer.parseInt(String.valueOf(pm.get("PAIR_PTP_ID")));
				}
				if((curPtpId == ptpId || pairPtpId == ptpId) && curDate == date){
					//如果ptpID符合
					if(key.equals(pm.get("PM_STD_INDEX").toString())){
						//如果字段符合
						Object v = pm.get("PM_VALUE");
						if(v!=null){
							rv = v.toString();
						}
						break;
					}
				}
			}
		}
		if("-".equals(rv) && "TPL_CUR".equals(key)){
			rv = getString(pmList, ptpId, "TPL_AVG", date);
		}
		if("-".equals(rv) && "RPL_CUR".equals(key)){
			rv = getString(pmList, ptpId, "RPL_AVG", date);
		}
		return rv;
	}
	/**
	 * 从PM列表中寻找需要的string数据
	 * @param pmList
	 * @param ptpId
	 * @param key
	 * @return
	 */
	private double getFloat(List<Map> pmList, int ptpId, String key, int date){
		String rv = getString(pmList, ptpId, key, date);
		if("-".equals(rv)){
			return ERROR_VALUE;
		}
		return Double.parseDouble(rv);
	}

	/**
	 * 从PM列表中寻找需要的int数据
	 * @param pmList
	 * @param ptpId
	 * @param key
	 * @return
	 */
	private int getInt(List<Map> pmList, int ptpId, String key){
		int rv = 0xffffff;
		if(pmList!=null && pmList.size()>0){
			for(Map pm:pmList){
				int curPtpId = Integer.parseInt(pm.get("BASE_PTP_ID").toString());
				if(curPtpId == ptpId){
					//如果ptpID符合
					if(key.equals(pm.get("PM_STD_INDEX").toString())){
						//如果字段符合
						rv = Integer.parseInt(pm.get("PM_VALUE").toString());
						break;
					}
				}
			}
		}
		return rv;
	}
}