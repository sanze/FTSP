package com.fujitsu.common.poi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGenerator {
	private int pages;
	private int pageSize;
	private int curPage;
	public DataGenerator(int pageSize, int total) {
		super();
		this.pageSize = pageSize;
		this.pages = (total - 1) / pageSize + 1;
		this.curPage = 0;
	}
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<Map> genDataFromFile(ColumnMap[] header, String fn){
		//fn = "D:\\srcDataLarge.txt";
		List<Map> rv = new ArrayList<Map>();
		File file = new File(fn);
        BufferedReader reader = null;
        try {
            System.out.println("读取：" + fn);
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int len = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	String[] vals = tempString.split(",");
            	Map dat = new HashMap();
            	for (int i = 0; i < vals.length; i++) {
            		dat.put(header[i].getKey(), vals[i]);
				}
//                System.out.println("#" + len + ": " + tempString);
                len++;
                rv.add(dat);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		return rv;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getData(ColumnMap[] header){
    	List<Map> rv = new ArrayList<Map>();
    	int period = 0;
    	boolean midData = false;
    	for (int i = 0; i < pageSize; i++) {
    		if(period == 0){
    			System.out.println("新周期开始 ----------------");
    		}
    		Map dat = new HashMap();
			for (int j = 0; j < header.length; j++) {
				String value = "";
				if(header[j].getComboType() > 0 && period > 0){
					Map prev = rv.get(i-1);
					value = prev.get(header[j].getKey()).toString();
					dat.put(header[j].getKey(), value);
				}else{
					//新数据
					value = (int)(Math.random()*1000) + "!";
					dat.put(header[j].getKey(), value);
				}
				System.out.print(value + "\t");
        	}
			System.out.println();
			if(period == 0){
				period = (int) (Math.random()*5+5);
				
			}
    		rv.add(dat);
    		period--;
		}
    	return rv;
    }
	public static void main(String[] args) {
		ColumnMap[] header = {
				new ColumnMap("id","ID", 0),
				new ColumnMap("prop1","其┐", 1),
				new ColumnMap("prop2","实的", 1),
				new ColumnMap("prop3","我萌", 2),
				new ColumnMap("prop4","是卖", 3),
				new ColumnMap("prop5","来来",0),
				new ColumnMap("prop6","卖是",0),
				new ColumnMap("prop7","萌我",0),
				new ColumnMap("prop8","的实",0),
				new ColumnMap("prop9","└其",0)
		};
		DataGenerator g = new DataGenerator(20, 3);
//		g.genData(header);
		g.genDataFromFile(header, "D:\\TestData\\srcDataLarge.txt");
		SimpleDateFormat cnSdf = new SimpleDateFormat("yyyy-MM-dd");

		Long startTime;
		Long endTime;
		try {
			Date startDate = cnSdf.parse("2012-1-1");
			startTime = startDate.getTime();
			Date endDate = cnSdf.parse("2012-1-9");
			endTime = endDate.getTime();
			Long timeStep = 24*3600L*1000;
			while(startTime <= endTime){
				Date someDay = new Date(startTime);
				System.out.println(cnSdf.format(someDay));
				startTime += timeStep;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
