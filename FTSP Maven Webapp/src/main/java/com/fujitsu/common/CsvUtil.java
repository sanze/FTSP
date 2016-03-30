package com.fujitsu.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.poi.ColumnMap;
import com.fujitsu.common.poi.DataGenerator;

public class CsvUtil {
	private String fn;
	private ColumnMap[] header;
	private File file;
	private int success;
	private boolean firstWrite = true;
	private boolean showHeader = false;
	//,,,,,,,,,,
	private static ColumnMap[] testheader ={
		new ColumnMap("emsGroup","ID",0),
		new ColumnMap("ems","属性1",0),
		new ColumnMap("ne","属性11",0),
		new ColumnMap("neType","属性111",0),
		new ColumnMap("portDesc","属性1111",0),
		new ColumnMap("ctp","属性11111",0),
		new ColumnMap("pmDesc","属性11",0),
		new ColumnMap("retrievalTime","属性111",0),
		new ColumnMap("ptpId","属性1111",0),
		new ColumnMap("location","属性11111",0),
		new ColumnMap("pmValue","属性11",0),
		new ColumnMap("exceptionLv","属性111",0)
	};
	/**
	 * CsvUtil构造函数
	 * @param fn 文件路径
	 * @param header Header
	 */
	public CsvUtil(String fn, ColumnMap[] header) {
		super();
		this.fn = fn;
		String[] paths = fn.split("/");
		String dir = fn.substring(0,fn.length()-paths[paths.length-1].length());
		File dirFile = new File(dir);
		if (!(dirFile.exists()) || !(dirFile.isDirectory())) {
			dirFile.mkdirs();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//创建一个空文件，占个坑再说
		File f = new File(fn);
        if(!f.exists()){
            try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		this.header = header;
		this.success = 1;
	}
	/**
	 * CsvUtil构造函数
	 * @param fn 文件路径
	 * @param header Header
	 * @param showHeader 是否输出标题
	 */
	public CsvUtil(String fn, ColumnMap[] header, boolean showHeader) {
		this(fn, header);
		this.showHeader = showHeader;
	}

	/**
	 * 
	 * @param dat
	 * @param exceptionOnly
	 * @throws IOException
	 */
	private void write(List<Map>dat, boolean exceptionOnly) throws IOException {
		FileWriter fs = new FileWriter(fn);
		BufferedWriter fw = new BufferedWriter(fs);
		Map e = null;
		//写header
		for (int i = 0; showHeader && i < header.length; i++) {
			if(i > 0){
				fw.write(',');
			}
			fw.write(header[i].getKey());
		}
		if(showHeader){
			fw.write("\r\n");
		}
		for (int i = 0; i < dat.size(); i++) {
			e = dat.get(i);
			//当仅输出异常数据 && 数据为正数据时，忽略此条数据
			if(exceptionOnly && ("0".equals(e.get("exceptionLv").toString()))){
				continue;
			}
			// 根据列信息写入每一条数据
			for (int j = 0; j < header.length; j++) {
				if(j > 0){
					fw.write(',');
				}
				Object o = e.get(header[j].getKey());
				String v = null == o ? "" : o.toString();
				fw.write(v);
			}
			fw.write("\r\n");
		}
		fw.close();
		fs.close();
	}
	/**
	 * 
	 * @param dat
	 * @param exceptionOnly
	 * @throws IOException
	 */
	private void writeMsData(List<Map>dat, boolean exceptionOnly) throws IOException {
		String[] paths = fn.split("/");
		String dir = fn.substring(0,fn.length()-paths[paths.length-1].length());
		File dirFile = new File(dir);
		if (!(dirFile.exists()) || !(dirFile.isDirectory())) {
			dirFile.mkdirs();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		FileWriter fs = new FileWriter(fn);
		BufferedWriter fw = new BufferedWriter(fs);
		Map e = null;
		//写header
		for (int i = 0; showHeader && i < header.length; i++) {
			if(i > 0){
				fw.write(',');
			}
			fw.write(header[i].getKey());
		}
		if(showHeader){
			fw.write("\r\n");
		}
		for (int i = 0; i < dat.size(); i++) {
			e = dat.get(i);
			//当仅输出异常数据 && 数据为正数据时，忽略此条数据
			if(exceptionOnly && ("0".equals(getData(e.get("MSStatus"))))){
				continue;
			}
			// 根据列信息写入每一列数据
			for (int j = 0; j < header.length; j++) {
				if(j > 0){
					fw.write(',');
				}
				String v = getData(e.get(header[j].getKey()));
				fw.write(v);
			}
			fw.write("\r\n");
		}
		fw.close();
		fs.close();
	}
	private String getData(Object o){
		return o == null ? "" : o.toString();
	}
	/**
	 * 追加写入csv文件
	 * @param dat 数据
	 * @throws IOException
	 */
	public void append(List<Map>dat) throws IOException {
		append(dat, false);
	}
	/**
	 * 追加写入csv文件
	 * @param dat 数据
	 * @param exceptionOnly 是否只写入异常数据
	 * @throws IOException
	 */
	public void append(List<Map>dat, boolean exceptionOnly) throws IOException {
		try {
			if(firstWrite){
				write(dat, exceptionOnly);
				firstWrite = false;
				return;
			}
			FileWriter fs = new FileWriter(fn, true);
			BufferedWriter fw = new BufferedWriter(fs);
			Map e = null;
			//写header
//		for (int i = 0; i < header.length; i++) {
//			if(i > 0){
//				fw.write(',');
//			}
//			fw.write(header[i].getKey());
//		}
//		fw.write("\r\n");
			for (int i = 0; i < dat.size(); i++) {
				e = dat.get(i);
				//当仅输出异常数据 && 数据为正数据时，忽略此条数据
//				System.out.println("#"+e.get("exceptionLv").toString()+"#");
				if(exceptionOnly && ("0".equals(e.get("exceptionLv").toString()))){
					continue;
				}
				// 根据列信息写入每一条数据
				for (int j = 0; j < header.length; j++) {
					if(j > 0){
						fw.append(',');
//						System.out.print("\t");
					}
					Object o = e.get(header[j].getKey());
					String v = (null == o ? "" : o.toString());
//					System.out.print(v);
					fw.append(v);
				}
//				System.out.println();
				fw.append("\r\n");
			}
			fw.close();
			fs.close();
			this.success *= 1;
		} catch (Exception e) {
			e.printStackTrace();
			this.success = 0;
		}
	}
	/**
	 * 大文件快速读取方法
	 * @param fileName 文件名
	 * @param start 起始字节
	 * @param length 数据长度
	 * @return 读取的字符串
	 */
	@Deprecated
	public static String qRead(String fileName, long start, int length){
		String rv  = null;
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(fileName, "r");
	        long fileLength = randomFile.length();
	        System.out.println("文件大小:" + fileLength);
	        randomFile.seek(start);
	        byte[] bytes = new byte[length];
	        int byteread = 0;
	        // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
	        // 将一次读取的字节数赋给byteread
	        while ((byteread = randomFile.read(bytes)) != -1) {
	           // System.out.write(bytes, 0, byteread);
	        }
	        System.out.println(bytes.length);
	        rv = new String(bytes,"UTF-8");
	        if (randomFile != null) {
	        	randomFile.close();
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rv;
	}
	/**
	 * 测试大文件读取
	 * @throws IOException
	 */
	@Deprecated
	public static void readBigFile() throws IOException{
		String fileName = "D:\\TestData\\megaData.csv";
		String val = qRead(fileName, 10L, 40);
		System.out.println("【" + val + "】");
	}
	/**
	 * 读取数据，越到后面越慢，百万级别大约需要2秒
	 * @param start
	 * @param length
	 * @return
	 */
	public List<Map> read(long start, int length) {
		List<Map> rv = new ArrayList<Map>();
		Date t1 = new Date(), t2;
		try {
			FileReader fs = new FileReader(fn);
			BufferedReader fr = new BufferedReader(fs);
			for (int i = 0; i < start; i++) {
				fr.readLine();
			}
			String sRead = null;
			//当能够读到东西，且没有读够数量时
			while((sRead = fr.readLine())!= null && (length--)>0){
				//处理读取到的数据
				Map e = new HashMap();
				String vals[] = sRead.split(",");
				for (int i = 0; i < header.length; i++) {
					e.put(header[i].getKey(), vals[i]);
				}
				rv.add(e);
			}
			fr.close();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		t2 = new Date();
		System.out
				.println("TimeUsed : " + (t2.getTime() - t1.getTime()) + "ms");
		return rv;
	}

	public static void main(String[] args) {
		Date t1 = new Date(), t2;
		try {
			DataGenerator g = new DataGenerator(100, 31000);
			List<Map> dat = g.getData(testheader);
			CsvUtil csv = new CsvUtil("D:\\testBigData.csv", testheader);
			csv.append(dat);
			csv.append(dat);
			System.out.println(dat.size());
//			List<Map> dat = null;
//			CsvUtil csv = new CsvUtil("D:\\TestData\\megaData.csv", testheader);
//			dat = csv.read(0L, 1000);
//			System.out.println("-----");
//			readBigFile();
//			Calendar calendar = Calendar.getInstance();
//			System.out.println((new Date()).toString());
//			calendar.setTime(new Date());
//			calendar.add(Calendar.MONTH, -1);
//			Date time = calendar.getTime();
//			System.out.println(time.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		t2 = new Date();
		System.out.println("TimeUsed : " + (t2.getTime() - t1.getTime()) + "ms");
		
	}
	/**
	 * 导出CSV的方法（原始数据）
	 * @param dat 数据源
	 * @return ExportResult对象
	 */
	public ExportResult export(List<Map>dat){
		return export(dat, false);
	}
	/**
	 * 导出CSV的方法（原始数据/异常数据）
	 * @param dat 原始数据
	 * @param exceptionOnly 是否只导出异常数据
	 * @return ExportResult对象
	 */
	public ExportResult export(List<Map>dat, boolean exceptionOnly){
		ExportResult rv = new ExportResult();
		try {
			write(dat, exceptionOnly);
			File f = new File(fn);
			rv.setExportTime(new Date());
			rv.setReturnResult(CommonDefine.SUCCESS);
			rv.setFileName(f.getName()); 
			rv.setFilePath(f.getPath());
			rv.setReturnMessage("success");
			rv.setSize((int)(f.length()/1024));
		} catch (Exception e) {
			e.printStackTrace();
			rv.setReturnResult(CommonDefine.FAILED);
		}
		return rv;
	}
	/**
	 * 导出CSV的方法（原始数据/异常数据）
	 * @param dat 原始数据
	 * @param exceptionOnly 是否只导出异常数据
	 * @return ExportResult对象
	 */
	public ExportResult exportMSData(List<Map>dat, boolean exceptionOnly){
		ExportResult rv = new ExportResult();
		try {
			writeMsData(dat, exceptionOnly);
			File f = new File(fn);
			rv.setExportTime(new Date());
			rv.setReturnResult(CommonDefine.SUCCESS);
			rv.setFileName(f.getName()); 
			rv.setFilePath(f.getPath());
			rv.setReturnMessage("success");
			rv.setSize((int)(f.length()/1024));
		} catch (Exception e) {
			e.printStackTrace();
			rv.setReturnResult(CommonDefine.FAILED);
		}
		return rv;
	}

	public ExportResult getResult() {
		ExportResult rv = new ExportResult();
		File f = new File(fn);
		rv.setExportTime(new Date());
		rv.setFileName(f.getName()); 
		rv.setFilePath(f.getPath());
		rv.setReturnMessage("success");
		rv.setSize((int)(f.length()/1024));
		rv.setReturnResult(success);
		return rv;
	}

}
