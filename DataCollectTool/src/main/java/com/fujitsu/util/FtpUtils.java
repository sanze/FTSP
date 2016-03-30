package com.fujitsu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;


/**
 * 从FTP读取文件
 * @author tony
 *
 */
public class FtpUtils {

	public FTPClient ftpClient;

	/**
	 * init ftp servere
	 * @throws CommonException 
	 */
	public FtpUtils(String ip, int port,
			String userName,String password) throws CommonException {
		
		this.connectServer(ip, port, userName, password);
	}

	public void reSet() {
		
	}

	/**
	 * @param ip
	 * @param port
	 * @param userName
	 * @param userPwd
	 * @param path
	 * @throws CommonException 
	 * @throws SocketException
	 * @throws IOException
	 * function:连接到服务器
	 */
	public void connectServer(String ip, int port, String userName,
			String userPwd) throws CommonException {
		if(ftpClient == null){
			ftpClient = new FTPClient();
		}
		try {
			if(!ftpClient.isConnected()){
				//连接
				ftpClient.connect(ip, port);
				//登录
				ftpClient.login(userName, userPwd);
				
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			}
		} catch (SocketException e) {
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_SOCKET_EXCEPTION);
		} catch (IOException e) {
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
	}

	/**
	 * @throws CommonException 
	 * @throws IOException
	 * function:关闭连接
	 */
	public void closeServer() throws CommonException {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				System.out.println("CORBA_IO_EXCEPTION：closeServer");
				throw new CommonException(
						e,
						MessageCodeDefine.CORBA_IO_EXCEPTION);
			}
		}
	}

	/**
	 * @param path
	 * @return
	 * function:读取指定目录下的文件名,不包含文件夹
	 * @throws CommonException 
	 * @throws IOException 
	 */
	public List<String> getFileList(String path) throws CommonException {
		List<String> fileLists = new ArrayList<String>();
		// 获得指定目录下所有文件名
		FTPFile[] ftpFiles = null;
		try {
			ftpFiles = ftpClient.listFiles(path);
		
		for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
			FTPFile file = ftpFiles[i];
			if (file.isFile()) {
				String fileName = file.getName();
				fileName = new String(fileName.getBytes("iso-8859-1"),"GBK");	
				fileLists.add(fileName);
			}
		}
		} catch (IOException e) {
			System.out.println("CORBA_IO_EXCEPTION：getFileList");
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
		return fileLists;
	}
	
	/**
	 * @param fileName 指定文件名
	 * @return 文件大小，-1表示文件不存在
	 * function:获取主目录指定文件大小
	 * @throws IOException 
	 */
	public long getFileSize(String path, String fileName) throws CommonException {
		long fileSize = -1;
		try {
			// 获得指定目录下所有文件
			FTPFile[]  ftpFiles = ftpClient.listFiles(path);
			for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
				FTPFile ftpFile = ftpFiles[i];
				if (ftpFile.isFile()) {
					String ftpFileName = ftpFile.getName();
					if (ftpFileName.equals(fileName)){
						fileSize=ftpFile.getSize();
						break;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("CORBA_IO_EXCEPTION：getFileSize");
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
		return fileSize;
	}
	
	/**
	 * @param fileName 指定文件名
	 * @return
	 * function:检测主目录先是否包含指定文件
	 * @throws IOException 
	 */
	public boolean checkFileExist(String fileName) throws CommonException {
		//获得指定目录下所有文件名
		String[] fileNameList = null;
		try {
			fileNameList = ftpClient.listNames();
		} catch (IOException e) {
			System.out.println("CORBA_IO_EXCEPTION：checkFileExist");
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
		for (int i = 0; fileNameList != null && i < fileNameList.length; i++) {
			String name = fileNameList[i];
			if (name.equals(fileName)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @param sourceFilePath
	 *            上传文件地址如C:/test.csv
	 * @param destFilePath
	 *            目标文件路径如test/test
	 * @param destFileName
	 *            目标文件名如test.csv
	 * @return
	 * @throws CommonException 
	 * @throws IOException
	 *             function:下载文件
	 */
	public boolean uploadFile(String sourceFilePath, String destFilePath,
			String destFileName) throws CommonException {
		boolean flag = false;
		try {
			if (destFilePath==null||createDirectory(destFilePath)) {
				// 创建目标文件
				File file_in = new File(sourceFilePath);
				// 创建输出流
				FileInputStream fis = new FileInputStream(file_in);

//				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 上传文件
//				ftpClient.storeFile(new String(
//						(destFilePath + "/" + destFileName).getBytes("GBK"),
//						"iso-8859-1"), fis);
				flag = ftpClient.storeFile(destFilePath==null||destFilePath.isEmpty()?destFileName:destFilePath + "/" + destFileName, fis);
				fis.close();
			}
		} catch(IOException e){
			System.out.println("CORBA_IO_EXCEPTION：uploadFile");
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
		return flag;
	}
	

	/**
	 * @param sourceFilePath需要下载的文件路径test.csv
	 * @param destFileName 文件存储名如test.csv
	 * @return
	 * @throws CommonException 
	 * @throws IOException
	 * function:下载文件
	 */
	public boolean downloadFile(String sourceFilePath,String destFilePath,String destFileName) throws CommonException {
		boolean flag = false;
		try {
			File file_out = new File(destFilePath+destFileName);
			//创建输出流
			FileOutputStream fos = new FileOutputStream(file_out);
			
//			sourceFilePath = new String(sourceFilePath.getBytes("GBK"), "iso-8859-1"); 
			//下载文件
			flag = ftpClient.retrieveFile(sourceFilePath, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			System.out.println("CORBA_IO_EXCEPTION：downloadFile");
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
		return flag;
	}
	
	
	/**
	 * @param sourcefilePath 要转移的文件目录
	 * @param sourcefileName 要转移的文件名称
	 * @param destDirectory 目标文件夹如test/test
	 * @param fileName
	 * function:移动文件
	 * @throws CommonException 
	 */
	public boolean moveFile(String sourcefilePath,String sourcefileName,String destFilePath,String destFileName) throws CommonException {
		boolean flag = false;
		try {
			//缓存文件地址
			String tempFolder = System.getProperty("java.io.tmpdir");
			String tempPath = tempFolder + destFileName;
			//下载文件至缓存文件地址
			boolean downResult = downloadFile(sourcefilePath+"/"+sourcefileName,tempFolder,destFileName);
			//上传文件至目标文件
			boolean uploadResult = uploadFile(tempPath,destFilePath,destFileName);
			//文件转移成功的情况下删除缓存文件，删除原始文件
			if(downResult&&uploadResult){
				File file = new File(tempPath);
				if(file.isFile()&&file.exists()){
					file.delete();
				}
				ftpClient.deleteFile(sourcefilePath+"/"+sourcefileName);
				flag = true;
			}
			
		} catch (IOException e) {
			System.out.println("CORBA_IO_EXCEPTION：moveFile");
			throw new CommonException(
					e,
					MessageCodeDefine.CORBA_IO_EXCEPTION);
		}
		return flag;
	}
	
	
	/**
	 * @param fileName 目标文件夹字符串如：test/test
	 * function:创建文件
	 * @throws CommonException 
	 */
	public boolean createFile(String fileName) throws CommonException {
		boolean flag = true;
		//新建文件
		File file = new File(fileName);
		try {
			flag = file.createNewFile();
		} catch (IOException e) {
			flag = false;
		}
		//上传文件
		if(flag)flag=uploadFile(file.getPath(), "", fileName);
		//删除原始文件
		file.delete();
		return flag;
	}
	
	
	/**
	 * @param destDirectory 目标文件夹字符串如：test/test
	 * function:创建文件夹
	 */
	public boolean createDirectory(String destDirectory) {
		String tempPath = "";
		String[] folderList = destDirectory.split("/");
		boolean flag = true;
		try {
			for (int i = 0; i < folderList.length; i++) {
				if (i == 0) {
					tempPath = folderList[i];
				} else {
					tempPath = tempPath + "/" + folderList[i];
				}
				//如果目录存在会返回false,导致后续处理不正确
				ftpClient.makeDirectory(tempPath);
//				flag = flag&&ftpClient.makeDirectory(tempPath);
			}
		} catch (IOException e) {
			flag = false;
		}
		return flag;
	}

//	/**
//	 * 返回一个文件流
//	 * @param fileName
//	 * @return
//	 */
//	public String readFile(String fileName) {
//		String result = "";
//		InputStream ins = null;
//		try {
//			ins = ftpClient.retrieveFileStream(fileName);
//
//			//byte []b = new byte[ins.available()];
//			//ins.read(b);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					ins));
//			String inLine = reader.readLine();
//			while (inLine != null) {
//				result += (inLine + System.getProperty("line.separator"));
//				inLine = reader.readLine();
//			}
//			reader.close();
//			if (ins != null) {
//				ins.close();
//			}
//			// 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
//			ftpClient.getReply();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}

	/**
	 * @param fileName
	 * @return
	 * function:从服务器上读取指定的文件
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public List readFile() throws ParseException {
		return null;
	}

	/**
	 * @param fileName
	 * function:删除文件
	 */
	public boolean deleteFile(String fileName) throws CommonException {
		try {
//			fileName = new String(fileName.getBytes("GBK"), "iso-8859-1");  
			return ftpClient.deleteFile(fileName);
		} catch (IOException e) {
			return false;
		}
	}
	
	

	/**
	 * @param args
	 * @throws CommonException 
	 * @throws ParseException
	 */
	public static void main(String[] args) throws CommonException{
		
//		int checkTime = 0;
//		
//		boolean flag = true;
//		
//		while(checkTime<5&&flag){
//
//			try {
//				Thread.sleep(1000*3);
//			} catch (InterruptedException e) {
//			}
//			
//			System.out.println("执行"+checkTime);
//			checkTime++;
//			
//			if(checkTime == 3){
//				flag = false;
//			}
//		}
//		System.out.println("ssdfsdf");
		
		FtpUtils ftp = new FtpUtils("xuxiaojun-PC",21,"admin","admin");
		String fileName  = "我是剩女们.csv";
		try {
			fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ftp.uploadFile("C:/historyPm.csv","xxxxxx",fileName);
		
		
//		try{
//			FtpUtils ftp = new FtpUtils();
//			String corbaIp = "11.1.1.1";
//			String neDisplayName = "我是网元";
////			neDisplayName = new String(neDisplayName.getBytes("GBK"),"iso-8859-1"); 
//			String startFileFlag = corbaIp + "_" + neDisplayName+".start";
//			System.out.println(startFileFlag);
//			ftp.createFile(startFileFlag);
////			File file = new File("D:/FTP文件目录/我.txt");
////			file.createNewFile();
////			ftp.uploadFile(file.getPath(), "", "我是复制品.txt");
////			file.delete();
////			ftp.uploadFile("C:/historyPm.csv","1/2","historyPm.csv");
////			ftp.downloadFile("10.167.28.99_NE644csv","10.167.28.99_NE644csv");
////			ftp.moveFile("sdfsfd", "historyPm.txt", "", "historyPm.txt");
////			ftp.moveFile("10.167.28.99_NE644csv","234234234");
////			System.out.println(ftp.downloadFile("xxxx", "historyPm.csv"));
//	//		ftp.removeFile("historyPm.txt", "sdfsfd");
//			for(String s:ftp.getFileList()){
//	//			String sdfsdf = ftp.readFile(s);
//				s = new String(s.getBytes("ISO-8859-1"), "GBK");
//				System.out.println(s);
//	//			ftp.removeFile(s,"sdfsfd");
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		try {
//			ftp.ftpClient.makeDirectory("sdfsfd");
//			ftp.ftpClient.c
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ftp.getFileList();
		/*ftp.unloadFile("D:\\test_t_department.txt", "t_department_20110623.txt");
		List<String> files = ftp.getFileList(path);
		for(int i = 0 ; i < files.size() ; i++){
		        String fileName = files.get(i);
		        ftp.unloadFile("D:\\test", "t_department_20110623.txt");
		        System.out.println(fileName);
		        String result = ftp.readFile("t_department_20110623.txt");
		        System.out.println(result);
		        ftp.deleteFile(fileName);
		}*/

	}
}
