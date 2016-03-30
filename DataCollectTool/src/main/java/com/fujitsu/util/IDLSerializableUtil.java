package com.fujitsu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import com.fujitsu.handler.ExceptionHandler;

public class IDLSerializableUtil {

	/**
	 * 序列化对象保存至指定文件
	 * 
	 * @param object
	 *            目标对象
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名
	 */
	public static void writeObject(Object object, String filePath, String fileName) {
		FileOutputStream fs;
		ObjectOutputStream os;
		// int fileNo = 0;
		String targetFileName = fileName;
		File targetFile = new File(filePath + targetFileName);
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			while(targetFile.exists()){
				// 如果存在该文件,覆盖
				targetFile.delete();
				// fileNo++;
				// targetFileName = fileName+"_"+fileNo;
				targetFile = new File(filePath + targetFileName);
			}
			fs = new FileOutputStream(filePath + targetFileName);
			os = new ObjectOutputStream(fs);
			os.writeObject(object);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 读取指定文件的序列化对象
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名
	 */
	public static Object readObject(String filePath, String fileName) {
		FileInputStream fs;
		ObjectInputStream ois;
		Object object = null;
		try {
			fs = new FileInputStream(filePath + fileName);
			ois = new ObjectInputStream(fs);
			object = ois.readObject();
		} catch (FileNotFoundException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		} catch (ClassNotFoundException e) {
			ExceptionHandler.handleException(e);
		}
		return object;
	}


	/**
	 * 序列化对象保存至指定文件
	 * 
	 * @param object
	 *            目标对象
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名
	 */
	public static void writeObject(Object object, String filePath, String fileName, Class helper) {
		try {
			Properties props=new Properties();
			props.setProperty("org.omg.CORBA.ORBClass","com.sun.corba.se.impl.orb.ORBImpl");
			//创建、初始化CORBA OutputStream
			com.sun.corba.se.impl.encoding.CDROutputStream cos = 
				(com.sun.corba.se.impl.encoding.CDROutputStream)org.omg.CORBA.ORB.init(new String[0],props).create_output_stream();
			//调用CORBA对象相关Helper类将数据写入CORBA OutputStream
			
			helper.getMethod("write", org.omg.CORBA.portable.OutputStream.class,object.getClass()).invoke(helper, cos, object);

			// int fileNo = 0;
			String targetFileName = fileName;
			File targetFile = new File(filePath + targetFileName);
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			while(targetFile.exists()){
				// 如果存在该文件,覆盖
				targetFile.delete();
				// fileNo++;
				// targetFileName = fileName+"_"+fileNo;
				targetFile = new File(filePath + targetFileName);
			}
			//创建文件写入流、数据写入流
			java.io.OutputStream fos = new java.io.FileOutputStream(filePath + targetFileName);
			java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);
			//CORBA OutputStream格式化为byte []
			byte [] buffer=cos.toByteArray();
			cos.close();//关闭CORBA OutputStream
			dos.writeInt(buffer.length);//写入大小
			dos.write(buffer);//写入数据
			dos.flush();
			fos.close();//关闭文件写入流、数据写入流
			dos.close();
		} catch (FileNotFoundException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		} catch (IllegalArgumentException e) {
			ExceptionHandler.handleException(e);
		} catch (IllegalAccessException e) {
			ExceptionHandler.handleException(e);
		} catch (InvocationTargetException e) {
			ExceptionHandler.handleException(e);
		} catch (SecurityException e) {
			ExceptionHandler.handleException(e);
		} catch (NoSuchMethodException e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 读取指定文件的序列化对象
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名
	 */
	public static Object readObject(String filePath, String fileName, Class helper) {
		Object object = null;
		try {
			//创建文件读取流、数据读取流
			java.io.InputStream fis = new java.io.FileInputStream(filePath + fileName);
			java.io.DataInputStream dis = new java.io.DataInputStream(fis);
			int size = dis.readInt();//读取大小
			byte [] buf = new byte [size];//创建缓存
			dis.read(buf);//读取数据
			fis.close();//关闭读取流、数据读取流
			dis.close();
			
			Properties props=new Properties();
			props.setProperty("org.omg.CORBA.ORBClass","com.sun.corba.se.impl.orb.ORBImpl");
			//创建、初始化CORBA InputStream
			org.omg.CORBA.portable.InputStream cis = 
				new com.sun.corba.se.impl.encoding.EncapsInputStream(
						org.omg.CORBA.ORB.init(new String[0],props),buf,size);
			object = helper.getMethod("read", org.omg.CORBA.portable.InputStream.class).invoke(helper, cis);
		} catch (FileNotFoundException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		} catch (IllegalArgumentException e) {
			ExceptionHandler.handleException(e);
		} catch (IllegalAccessException e) {
			ExceptionHandler.handleException(e);
		} catch (InvocationTargetException e) {
			ExceptionHandler.handleException(e);
		} catch (SecurityException e) {
			ExceptionHandler.handleException(e);
		} catch (NoSuchMethodException e) {
			ExceptionHandler.handleException(e);
		}
		return object;
	}
	
	/**IDL数据类型转换<br>
	 * 如将各HW.CosNotification.StructuredEvent转换成org.omg.CosNotification.StructuredEvent
	 * @param objSrc
	 * @param helperSrc
	 * @param helperTar
	 * @return
	 */
	public static Object O2O(Object objSrc, Class helperSrc, Class helperTar) {
		try {
			Properties props=new Properties();
			props.setProperty("org.omg.CORBA.ORBClass","com.sun.corba.se.impl.orb.ORBImpl");
			//创建、初始化CORBA OutputStream
			com.sun.corba.se.impl.encoding.CDROutputStream cos = 
				(com.sun.corba.se.impl.encoding.CDROutputStream)org.omg.CORBA.ORB.init(new String[0],props).create_output_stream();
			//调用CORBA对象相关Helper类将数据写入CORBA OutputStream
			helperSrc.getMethod("write", org.omg.CORBA.portable.OutputStream.class,objSrc.getClass()).invoke(helperSrc, cos, objSrc);
			Object object=helperTar.getMethod("read", org.omg.CORBA.portable.InputStream.class).invoke(helperTar, cos.create_input_stream());
			
			cos.close();//关闭CORBA OutputStream
			return object;
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		} catch (IllegalAccessException e) {
			ExceptionHandler.handleException(e);
		} catch (InvocationTargetException e) {
			ExceptionHandler.handleException(e);
		} catch (NoSuchMethodException e) {
			ExceptionHandler.handleException(e);
		}
		return objSrc;
	}
}
