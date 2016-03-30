package com.fujitsu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {

	private ZipUtil() {
	}

	/**
	 * 压缩指定路径的文件或文件夹
	 * 
	 * @param filePath
	 *            String 源文件路径
	 * @param zipFilePath
	 *            String 目标路径
	 * @return 是否成功 boolean
	 */
	public boolean CreateZipFile(String filePath, String zipFilePath) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		boolean bool = true;
		try {
			fos = new FileOutputStream(zipFilePath);
			zos = new ZipOutputStream(fos);
			bool = writeZipFile(new File(filePath), zos, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (zos != null)
					zos.close();
			} catch (IOException e) {
				e.printStackTrace();
				bool = false;
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				bool = false;
			}
		}
		return bool;

	}
	public boolean CreateZipFile(List<String> filePaths, String zipFilePath, boolean replaceOnExist) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		boolean bool = true;
		try {
			File zipFile=new File(zipFilePath);
			
			if(zipFile.exists()&&zipFile.isFile()){
				if(replaceOnExist){
					zipFile.delete();
				}else{
					return true;
				}
			}
			zipFile.createNewFile();
			fos = new FileOutputStream(zipFilePath);
			zos = new ZipOutputStream(fos);
			for(String filePath:filePaths){
				bool = bool&&writeZipFile(new File(filePath), zos, "");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (zos != null)
					zos.close();
			} catch (IOException e) {
				e.printStackTrace();
				bool = false;
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				bool = false;
			}
		}
		return bool;

	}
	private boolean writeZipFile(File f, ZipOutputStream zos, String hiberarchy) {
		boolean bool = true;
		if (f.exists()) {
			if (f.isDirectory()) {
				hiberarchy += f.getName() + "/";
				File[] fif = f.listFiles();
				for (int i = 0; (i < fif.length) && bool; i++) {
					bool = writeZipFile(fif[i], zos, hiberarchy);
				}
			} else {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(f);
					ZipEntry ze = new ZipEntry(hiberarchy + f.getName());
					zos.putNextEntry(ze);
					byte[] b = new byte[1024];
					while (fis.read(b) != -1) {
						zos.write(b);
						b = new byte[1024];
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					bool = false;
				} catch (IOException e) {
					e.printStackTrace();
					bool = false;
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (IOException e) {
						e.printStackTrace();
						bool = false;
					}
				}

			}
		} else {
			bool = false;
		}
		return bool;

	}
	
	/**
	 * @param lst
	 * @return
	 */
	public String zipFiles(List<String> lst, String fileName,String zipPath) {
		SimpleDateFormat fmt = new SimpleDateFormat();
		fmt.applyPattern("yyyy_MM_dd_HH-mm-ss");

//		String zipPath = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.TEMP_DIR + "\\";

		String zipFile = zipPath + fmt.format(new Date()) + fileName + ".zip";

		try {
			File pathDir = new File(zipPath);
			if(!pathDir.exists()){
				pathDir.mkdir();
			}
			FileOutputStream target = new FileOutputStream(zipFile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					target));
			int BUFFER_SIZE = 1024;
			byte buff[] = new byte[BUFFER_SIZE];
			File tmp;

			List<File> files = new ArrayList<File>();
			for (int i = 0; i < lst.size(); i++) {
				lst.set(i, lst.get(i));
				tmp = new File(lst.get(i));
				files.add(tmp);
			}

			for (int i = 0; i < files.size(); i++) {
				FileInputStream fi = new FileInputStream(files.get(i));
				BufferedInputStream origin = new BufferedInputStream(fi);
				ZipEntry entry = new ZipEntry(files.get(i).getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(buff)) != -1) {
					out.write(buff, 0, count);
				}
				origin.close();
			}
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return zipFile;
	}

	private static ZipUtil zu = null;

	public static ZipUtil getInstance() {
		if (zu == null)
			zu = new ZipUtil();
		return zu;

	}
	
	/**
	 * 使用gzip压缩字符串
	 * @param str
	 * @return compressed string
	 */
	public String compressStr(String str) {
		String result = "";
		if (null==str || str.length() <=0){
			return result;
		}
		try {
			// 创建一个新的 byte 数组输出流 
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// 使用默认缓冲区大小创建新的输出流 
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			// 将 b.length 个字节写入此输出流
			gzip.write(str.getBytes());
			gzip.close();
			// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串 
			result = out.toString("ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 使用gzip解压字符串
	 * @param str
	 * @return uncompressed string
	 */
	public String unCompressStr(String str) {
		String result = "";
		if (null==str || str.length()<=0){
			return result;
		}
		try {
			// 创建一个新的 byte 数组输出流
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
			ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			// 使用默认缓冲区大小创建新的输入流
			GZIPInputStream gzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n = 0;
			while ((n = gzip.read(buffer)) >= 0) {
				// 将未压缩数据读入字节数组
				// 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
				out.write(buffer, 0, n);
			}
			// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
			result = out.toString("GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {

		ZipUtil.getInstance().CreateZipFile("D:/My Documents/jquery",
				"d:/test.zip");
	}

}
