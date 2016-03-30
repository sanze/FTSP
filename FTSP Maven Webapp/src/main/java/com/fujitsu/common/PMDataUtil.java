package com.fujitsu.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fujitsu.common.poi.ColumnMap;

public class PMDataUtil {
	//----------以下用于统计报表导出-------------
	private static Map<String, Integer> rowMap = null;

	/**
	 * 合并Ne统计数据，把同一端口的不同时间段的数据合并到同一行
	 * @param lstSrc 原始数据
	 * @param srcHeader 原始数据对应的列信息
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map> combineNeData(List<Map> lstSrc, ColumnMap[] srcHeader){
//		System.out.println();
		rowMap = new HashMap<String, Integer>();
		List<Map> rv = new ArrayList<Map>();
		int groupIndex = -1;
		int dateIndex = -1;
		int eventIndex = -1;
		int valueIndex = -1;
		int curIndex = 0;
		//初始化，获取各个标识列的index
		for (int i = 0; i < srcHeader.length; i++) {
//			System.out.print(srcHeader[i].getColumnName() + "\t");
			if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_KEY){
				//标记子分组的Key
				groupIndex = i;
			}else if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE){
				//标记日期分组的Key
				dateIndex = i;
			}else if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_SUBKEY){
				//标记性能事件的Key
				eventIndex = i;
			}else if(srcHeader[i].getComboType() == CommonDefine.PM.CUSTOM_REPORT.COMBO_VALUE){
				//标记性能值的Key
				valueIndex = i;
			}
		}
//		System.out.println();
		//随机生成一个前一列信息，保证和数据中的列信息不一样
		String preGroupName = "" + Math.random();
		String curGroupName = "";
		String date = "";
		String propertyName = "";
		String[] exceptionLvlColorStringPrefix = {"X","B","O","R","X","X"};
		Map<String, String> neTypeMap = new HashMap<String, String>();
		neTypeMap.put("1", "SDH");
		neTypeMap.put("2", "WDM");
		neTypeMap.put("3", "OTN");
		neTypeMap.put("4", "PTN");
		neTypeMap.put("5", "微波");
		neTypeMap.put("6", "FTTX");
		neTypeMap.put("9", "VirtualNE");
		neTypeMap.put("99", "未知");
		neTypeMap.put("-", "未知");
		//开始数据归并过程
//		System.out.println("原始数据一共 " + lstSrc.size() + "行");
		for (int i = 0; i < lstSrc.size(); i++) {
			Map dat = lstSrc.get(i);
//			System.out.println("\t当前：【" + (i+1) + "】行");
			curGroupName = dat.get(srcHeader[groupIndex].getKey()).toString() + dat.get("ne");
			date = dat.get(srcHeader[dateIndex].getKey()).toString();
			propertyName = o2s(dat.get(srcHeader[eventIndex].getKey())) + o2s(dat.get("ctpId"));
			//判断是不是新的一组数据
			if(curGroupName.equals(preGroupName) || curGroupName == preGroupName){
				
			}else{
//				System.out.println("Diff Group");
				//如果是新的分组，则清除原先的分组信息
				//主要是不同性能事件对应的行索引
				rowMap.clear();
				preGroupName = curGroupName;
			}
			//获取当前行的位置
			int rowIndex = getRowIndex(propertyName);
			//如果是新的性能事件，则新建行
			if(rowIndex < 0){
				//添加记录
				rowMap.put(propertyName, curIndex);
				rv.add(new HashMap());
				rowIndex = curIndex++;			
//				System.out.println("->" + propertyName + " 不存在！添加新行 @ " + curIndex);
			}
			//为了防止出现越界报错，加了个判断。但是逻辑上来说不会出现越界
			Map e = null;
			if (rowIndex >= rv.size())
				e = rv.get(rv.size() - 1);
			else
				e = rv.get(rowIndex);
			//获取Map内容，填充结果集
//			System.out.print("#" +(i+1)+ "\t");
//			String newInfo = "";
			for (int j = 0; j < srcHeader.length; j++) {
				String prop = srcHeader[j].getKey();
				String val = dat.get(prop) == null ? "-" : dat.get(prop).toString();
//				System.out.print(val+ "\t");
				if(j == valueIndex){
					//在性能值前面加上异常等级颜色参数
					int exceptionLvl = (Integer) dat.get("exceptionLv");
//					System.out.print(exceptionLvlColorStringPrefix[exceptionLvl]);
					val = exceptionLvl + val;
					//如果是性能事件对应的值，则将其添加到对应的时间里
					e.put(date, val);
//					newInfo += "[" + date + " -> " + val + "] @ #" + curIndex;
				}else if(j == dateIndex){
					//如果是时间列，则忽略
				}else{
					//其他则复制
					if(prop == "neType"){
						val = neTypeMap.get(val);
					}
					e.put(prop, val);
//					System.out.print(prop + "\t");
				}
			}
//			System.out.println();
//			System.out.println(newInfo);		
		}
		return rv;
	}
	private static String o2s(Object o){
		return o==null?"-":o.toString();
	}
	/**
	 * 根据性能事件获取行号
	 * @param pmEvent
	 * @return
	 */
	private static int getRowIndex(String pmEvent){
		Integer index = -1;
		if(null == rowMap){
			rowMap = new HashMap<String, Integer>();
		}
		index = rowMap.get(pmEvent);
		if(null == index){
			index = -1;
		}
		return index;
	}
	
//	//---------------------------------以下对应 复用段 信息导出-----------------------------
//	/**
//	 * 生成Pm信息
//	 * @param  val PM值
//	 * @return
//	 */
//	private static Map<String, Object> genPmValue(String val){
//		Map<String, Object> o = new HashMap<String, Object>();
//		o.put("2012-12-21", val);
//		o.put("2012-12-11", val);
//		if(Math.random()>0.5){
//			o.put("2012-12-01", val);
//		}
//		if(Math.random()>0.75){
//			o.put("2012-12-02", val);
//		}
//		if(Math.random()>0.825){
//			o.put("2012-12-03", val);
//		}
////		o.put("THEORETICAL_VALUE", val);
//		return o;
//	}
//	/**
//	 * 生成Pm列表信息
//	 * @return
//	 */
//	private static Map<String,List<Map<String, Object>>> genPmList(){
//		Map<String,List<Map<String, Object>>> rv = new HashMap<String,List<Map<String, Object>>>();
//		List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
//		Map<String, Object> o = new HashMap<String, Object>();//1
//		o.put("THEORETICAL_VALUE", "1");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		o = new HashMap<String, Object>();//2
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "1-1-3-SD1");
//		lst.add(o);
//		o = new HashMap<String, Object>();//3
//		o.put("THEORETICAL_VALUE", "3");
//		o.put("PM_VALUE", genPmValue("-2.00"));
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		o = new HashMap<String, Object>();//4
//		o.put("THEORETICAL_VALUE", "10");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "显示左(虚拟)");
//		lst.add(o);
//		o = new HashMap<String, Object>();//5
//		o.put("THEORETICAL_VALUE", "1");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		o = new HashMap<String, Object>();//6
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "虚拟入口(虚拟)");
//		lst.add(o);
//		o = new HashMap<String, Object>();//7
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "虚拟出口(虚拟)");
//		lst.add(o);
//		o = new HashMap<String, Object>();//8
//		o.put("THEORETICAL_VALUE", "3");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		rv.put("multiSecsDataListMain", lst );
//		//正向备用
//		lst = new ArrayList<Map<String, Object>>();
//		o = new HashMap<String, Object>();//1
//		o.put("THEORETICAL_VALUE", "1");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		o = new HashMap<String, Object>();//2
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "1-1-6-PL1S");
//		lst.add(o);
//		o = new HashMap<String, Object>();//3
//		o.put("THEORETICAL_VALUE", "3");
//		o.put("PM_VALUE", genPmValue("-2.00"));
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		o = new HashMap<String, Object>();//4
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		lst.add(o);
//		o = new HashMap<String, Object>();//5
//		o.put("THEORETICAL_VALUE", "1");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		o = new HashMap<String, Object>();//6
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "备用入(虚拟)");
//		lst.add(o);
//		o = new HashMap<String, Object>();//7
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "备用出(虚拟)");
//		lst.add(o);
//		o = new HashMap<String, Object>();//8
//		o.put("THEORETICAL_VALUE", "3");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↓");
//		lst.add(o);
//		rv.put("multiSecsDataListBackup", lst );
//		//反向
//		lst = new ArrayList<Map<String, Object>>();
//		o = new HashMap<String, Object>();//1
//		o.put("THEORETICAL_VALUE", "1");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↑");
//		lst.add(o);
//		o = new HashMap<String, Object>();//2
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "1-1-3-SD1");
//		lst.add(o);
//		o = new HashMap<String, Object>();//3
//		o.put("THEORETICAL_VALUE", "3");
//		o.put("PM_VALUE", genPmValue("2.00"));
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↑");
//		lst.add(o);
//		rv.put("multiSecsDataListReverseMain", lst );
//		//反向备用
//		lst = new ArrayList<Map<String, Object>>();
//		o = new HashMap<String, Object>();//1
//		o.put("THEORETICAL_VALUE", "1");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↑");
//		lst.add(o);
//		o = new HashMap<String, Object>();//2
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "1-1-6-PL1S");
//		lst.add(o);
//		o = new HashMap<String, Object>();//3
//		o.put("THEORETICAL_VALUE", "3");
//		o.put("PM_VALUE", null);
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "↑");
//		lst.add(o);
//		o = new HashMap<String, Object>();//4
//		o.put("NE_DISPLAY_NAME", "NE663");
//		o.put("NE_ID", "1925");
//		o.put("UNIT_INFO", "1-1-6-PL1S");
//		lst.add(o);
//		rv.put("multiSecsDataListReverseBackup", lst );
//		return rv;
//	}
	/**
	 * 生成 <b>复用段</b> 的Excel头
	 * @param lstSrc
	 * @return
	 */
	public static Map<String, Object> getMsDates(Map<String,List<Map<String, Object>>> lstSrc){
		Map<String, Object> rv = new HashMap<String, Object>();
		boolean hasBackup = false;
		//主字段
		final String[] msKeys = {
				"multiSecsDataListMain",//正向路由
				"multiSecsDataListBackup",//正向路由保护路由
				"multiSecsDataListReverseMain",//反向路由
				"multiSecsDataListReverseBackup"//反向路由保护路由
		};
		//取出ListMap
		List<String> dates = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
//			System.out.println("@" + msKeys[i]);
			List<Map<String, Object>> curListMap = lstSrc.get(msKeys[i]);
			if(curListMap == null){
				//如果当前的ListMap没有值，则处理下一个ListMap
				continue;
			}
			if(i%2 == 1){	//没错！hasBackup这个玩意儿已经没用了-.-
				hasBackup = true;
			}
//			System.out.println("    Len = " + curListMap.size());
			//并遍历ListMap
			for (int j = 0; j < curListMap.size(); j++) {
//				System.out.println("        Proc @ " + (j+1));
				Map<String, Object> curMap = curListMap.get(j);
				//取出其中的PM信息，提取日期，用于生成Header
				Map<String, Object> pm = (Map<String, Object>)curMap.get("PM_VALUE");
				if(pm != null){
//					System.out.println("\t\tPM found!");
					//遍历KV
					//取出所有的非 "THEORETICAL_VALUE"字段
			       Iterator<Entry<String, Object>> it = pm.entrySet().iterator();
			       while(it.hasNext()){
			           Map.Entry<String, Object>  entry=(Entry<String, Object>) it.next();
			           String key = entry.getKey();
//			    	   System.out.print("\t\t@ " + key);
//			           System.out.println(" : "+entry.getValue().toString());
			           //保证一个日期只添加一次
			           if(!dates.contains(key) && "THEORETICAL_VALUE" != key){
			        	   dates.add(key);
			           }
			       }
				}
			}
		}
//		for (int i = 0; i < dates.size(); i++) {
//			System.out.println("#" + i + " - " + dates.get(i));
//		}
//		System.out.println();
		//对 日期君 进行排序
		Collections.sort(dates);
//		for (int i = 0; i < dates.size(); i++) {
//			System.out.println("#" + i + " - " + dates.get(i));
//		}
		rv.put("dates", dates);
		//下面生成ColumnMap[]信息
		int colLength = 7 + dates.size()*2;
		ColumnMap[] header = new ColumnMap[colLength];
		List<String> columnNames = new ArrayList<String>();
		columnNames.add("网元名");
		columnNames.add("机盘号");
		columnNames.add("光放理论值");
		columnNames.addAll(dates);
		List<String> columnKeys = new ArrayList<String>();
		columnKeys.add("NE_DISPLAY_NAME");
		columnKeys.add("UNIT_INFO");
		columnKeys.add("THEORETICAL_VALUE");
		columnKeys.addAll(dates);
		//定义字段后缀
		final String[] destSuffix = {
			"Main",//主路由字段后缀
			"Backup"//备用路由字段后缀
		};
		int i, j;
//		System.out.println("Total header = " + colLength);
		header[0] = new ColumnMap(columnKeys.get(0) + "Main", columnNames.get(0)+"(主用)", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 20);
		header[1] = new ColumnMap(columnKeys.get(1) + "Main", columnNames.get(1), CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12);
		header[2] = new ColumnMap(columnKeys.get(2) + "Main", columnNames.get(2), CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE);
		for (i = 3; i < columnNames.size(); i++) {
//			System.out.println("#" + i + " - " + columnKeys.get(i) + "Main");
			header[i] = new ColumnMap(columnKeys.get(i) + "Main", columnNames.get(i), CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE);
		}
//		System.out.println("#" + i + " - " + "splitter");
		header[i++] = new ColumnMap("splitter", "  ");
		header[i] = new ColumnMap(columnKeys.get(0) + "Backup", columnNames.get(0)+"(备用)", CommonDefine.PM.CUSTOM_REPORT.COMBO_AUTO, 20);
		header[i+1] = new ColumnMap(columnKeys.get(1) + "Backup", columnNames.get(1), CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE, 12);
		header[i+2] = new ColumnMap(columnKeys.get(2) + "Backup", columnNames.get(2), CommonDefine.PM.CUSTOM_REPORT.COMBO_NONE);
		for (j = 3; j < columnNames.size(); j++) {
//			System.out.println("#" + (i + j) + " - " + columnKeys.get(j) + "Backup");
			header[i+j] = new ColumnMap(columnKeys.get(j) + "Backup", columnNames.get(j), CommonDefine.PM.CUSTOM_REPORT.COMBO_DATE);
		}
		rv.put("header", header);
		return rv;
	}
	/**
	 * 合并复用段数据
	 * @param lstSrc 原始数据
	 * @param isReverse 正向/反向
	 * @return
	 */
	public static List<Map> combineMsData(Map<String,List<Map<String, Object>>> lstSrc, boolean isReverse){
		//主字段
		final String[] msKeys = {
				"multiSecsDataListMain",//正向路由
				"multiSecsDataListBackup",//正向路由保护路由
				"multiSecsDataListReverseMain",//反向路由
				"multiSecsDataListReverseBackup"//反向路由保护路由
		};
		//正向电路，从0开始，反向电路，从2开始
		int startColumn = isReverse ? 2 : 0;
		final String[] detailKeys = {
				"NE_DISPLAY_NAME",//网元名
				"UNIT_INFO",//机盘号
				"THEORETICAL_VALUE"//光放理论值
		};
		//定义字段后缀
		final String[] destSuffix = {
				"Main",//主路由字段后缀
				"Backup"//备用路由字段后缀
		};
		int dataLen = 0, curIndex = 0;
		//处理主路由
		List<Map<String, Object>> routeMap = null;
		List<Map> rv = new ArrayList<Map>();
		//获取数据中的日期，以备后用
		List<String> dates = (List<String>) getMsDates(lstSrc).get("dates");
		Map<String, Object> origMap = null;
		Map dstMap = null;
		for(int col = 0; col<2; col++){
			int realCol = col + startColumn;
			String routeName = msKeys[realCol];
			routeMap = lstSrc.get(routeName);
			if(routeMap != null){
				curIndex = 0;
				//处理每一条数据
				for (int i = 0; i < routeMap.size(); i++) {
					origMap = routeMap.get(i);
					if(curIndex < dataLen){
						//说明已经到备用，可以直接取Map然后追加信息
						dstMap = rv.get(curIndex);
					}else{
						//主路由 / 备用路由长度超过主路由,新建Map到结果中
						dstMap = new HashMap();
						dataLen++;
						rv.add(dstMap);
					}
					//开始数据迁移
					dstMap.put("splitter","");
					//复用段ID
					dstMap.put("MULTI_SEC_ID" + destSuffix[col], origMap.get("MULTI_SEC_ID"));
					//网元名
					dstMap.put("NE_DISPLAY_NAME" + destSuffix[col], origMap.get("NE_DISPLAY_NAME"));
					//机盘号
					dstMap.put("UNIT_INFO" + destSuffix[col], origMap.get("UNIT_INFO"));
					//光放理论值
					dstMap.put("THEORETICAL_VALUE" + destSuffix[col], origMap.get("THEORETICAL_VALUE"));
					//trunk line
					dstMap.put("trunkLineId" + destSuffix[col], origMap.get("trunkLineId"));
					//各个日期字段
					Map<String, Object> pm = (Map<String, Object>)origMap.get("PM_VALUE");
					if(pm != null){
						for (int j = 0; j < dates.size(); j++) {
							String date = dates.get(j);
							dstMap.put(date + destSuffix[col], pm.get(date));
						}
					}
					//计数++
					curIndex++;
				}
			}
		}
		return rv;
	}
//	public static void main(String[] args){
//		System.out.println("---------PMDataUtil main----------");
////		getMsDates(genPmList());
//		Map<String, List<Map<String, Object>>> origData = genPmList();
//		List<Map> newData = combineMsData(origData, false);
//		List<Map> newDataRev = combineMsData(origData, true);
//		Map<String, Object> headerInfo = getMsDates(origData);
//		ColumnMap[] header = (ColumnMap[]) headerInfo.get("header");
//		
//		POIExcelUtil xls = new POIExcelUtil("D:\\a.xlsx", header);
//		xls.writeSheet("正向", newData, true);
//		xls.writeSheet("反向", newDataRev);
////		xls.close();
//	}
	
//	private static String[] BITERR_FIELDS= {"pmStdIndex"};
	
	
//--------------------误码报表--------------------------	
	
	/**
	 * 误码表的数据处理
	 * @param listSrc
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  List<Map> combineBitErrData(List<Map> listSrc){
		//反正先往里面塞吧
		Map<String,Map> locker = new HashMap<String, Map>();
		List<Map> doneList = new ArrayList<Map>();
		//以ptpId为key
		for(Map row:listSrc){
			String key = row.get("neId").toString()+"-"+row.get("ptpId").toString();
			if(locker.containsKey(key)){
				Map ptpRow = locker.get(key);
				Float value = Float.valueOf(row.get("pmValue").toString());
//				2014-9-11 不用斜杠表示0值了
//				if(value.compareTo(0F)==0)
//				ptpRow.put(row.get("tag"), "/");
//				else
				ptpRow.put(row.get("tag"), row.get("pmValue"));
			}else{
				Float value = Float.valueOf(row.get("pmValue").toString());
//				if(value.compareTo(0F)==0)
//					row.put(row.get("tag"), "/");
//				else
//					row.put(row.get("tag"), row.get("pmValue"));
				locker.put(key, row);
			}
		}
		//变成list吧
		Iterator it = locker.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString();
			locker.get(key).put("key", key);
			doneList.add(locker.get(key));
		}
		sort(doneList,"key");
		return doneList;
	}
	
	/**
	 * 光功率表的数据处理
	 * @param listSrc
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  List<Map> combineSDHLightPowerData(List<Map> listSrc){
		//反正先往里面塞吧
		Map<String,Map> locker = new HashMap<String, Map>();
		List<Map> doneList = new ArrayList<Map>();
		//以ptpId为key
		for(Map row:listSrc){
			String key = row.get("neId").toString()+"-"+row.get("ptpId").toString();
			if(locker.containsKey(key)){
				Map ptpRow = locker.get(key);
				ptpRow.put(row.get("tag"), row.get("pmValue"));
				ptpRow.put("COMP_"+row.get("tag"), row.get("compValue"));
			}else{
				row.put(row.get("tag"), row.get("pmValue"));
				row.put("COMP_"+row.get("tag"), row.get("compValue"));
				locker.put(key, row);
			}
		}
		//变成list吧
		Iterator it = locker.keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString();
			locker.get(key).put("key", key);
			doneList.add(locker.get(key));
		}
		sort(doneList,"key");
		return doneList;
	}
	
	private static void sort(List<Map> list,String key) {
		Map temp = null;
		for (int i = list.size() - 1; i > 0; --i) {
			for (int j = 0; j < i; ++j) {
				if (list.get(j+1).get(key).toString().compareTo(list.get(j).get(key).toString())<0) {
					temp = list.get(j);
					list.set(j, list.get(j+1));
					list.set(j+1, temp);
				}
			}
		}
	}
	
}
