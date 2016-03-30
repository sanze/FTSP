package com.fujitsu.common.poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.fujitsu.common.ExportResult;
/**
 * 测试的人坑我脑细胞思密达
 * @author TianHongjun
 * 
 */
@SuppressWarnings({"rawtypes"})
public class PushExcelUtil {
	private POIExcelUtil poi = null;
	
	private List<Map> cache = null;
	private int startCnt, endCnt;
	private boolean isEmpty = true;
	
	private String sheetNameTpl = "";
	public PushExcelUtil(String fileName, ColumnMap[] header, String sheetTpl) {
		poi = new POIExcelUtil(fileName, header);
		cache = new ArrayList<Map>();
		sheetNameTpl = sheetTpl;
		startCnt = 1;
		endCnt = 0;
	}
	/**
	 * 向缓存中推送数据
	 * @param dat 推送的数据
	 * @param cnt
	 */
	public void push(List<Map> dat, int cnt){
//		showMemory();
//		System.out.println("PushExcelUtil<push> @ 网元数 = " + cnt + " 性能数 = " + dat.size());
		if(cache.size() != 0 && (cache.size() + dat.size()) > 10000){
//			System.out.println("PushExcelUtil<push> @ 数据满一页 size = " + cache.size());
			//生成新Sheet的名称
			String sheetName = sheetNameTpl  + "(网元" + startCnt + " - 网元" + endCnt +")";
			poi.writeSheet(sheetName, cache, true);
			startCnt = endCnt + 1;
			cache = new ArrayList<Map>();
			isEmpty = false;
		}
		endCnt += cnt;
		cache.addAll(dat);
	}
	/**
	 * 关闭函数~
	 */
	public void close(){
//		System.out.println("PushExcelUtil<close> @ total ne = " + endCnt);
		//先看缓存还有没有数据，有的话直接保存
		if(cache.size() != 0 || isEmpty){
			String sheetName = sheetNameTpl  + "(网元" + startCnt + " - 网元" + endCnt +")";
			poi.writeSheet(sheetName, cache);
		}
		poi.close();
	}
	/**
	 * 获取POI导出信息
	 * @return
	 */
	public ExportResult getResult() {
		return poi.getResult();
	}
	private void showMemory(){
		Runtime run = Runtime.getRuntime();
		long max = run.maxMemory();
		long total = run.totalMemory();
		long free = run.freeMemory();
		long usable = max - total + free;
		System.out.print("" + max);
		System.out.print("\t" + total);
		System.out.print("\t" + free);
		System.out.print("\t" + usable + "\n");
	}
	public SXSSFWorkbook getXls() {
		return poi.getXls();
	}
}
