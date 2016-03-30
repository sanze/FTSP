package com.fujitsu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class ConfigUtil {  
    /**
     * 读取配置文件指定属性值
     * @param key 键 
     * @param fileURL 配置文件名
     * @return 对应键值,null表示失败
     */
    public static String getProperty(String key,String fileURL){
    	Properties prop = new Properties(); 
    	InputStream fis = null;  
    	String result=null;
    	try {
            //java.net.URL  url = ConfigUtil.class.getClassLoader().getSystemResource(fileURL);  
        	
        	URL filePath = Thread.currentThread().getContextClassLoader().getResource(fileURL+".properties");
        	File file = new File(filePath.toURI());  
            if (!file.exists())  
                file.createNewFile();  
            fis = new FileInputStream(file);  
            prop.load(fis);  
            fis.close();//一定要在修改值之前关闭fis  
            result = prop.getProperty(key);  
        } catch (Exception e) {  
            System.err.println("Visit " + fileURL + " for getProperty "  
            + key + " error"); 
        }// catch (URISyntaxException e) {  
            // TODO Auto-generated catch block  
         //   e.printStackTrace();  
         //   result=false;
        //}  
		return result;
    }
    /**
     * 设置配置文件指定属性值
     * @param key 键
     * @param value 值
     * @param fileURL 配置文件名
     * @return 执行结果
     */
    public static boolean setProperty(String key, String value,String fileURL) {  
        Properties prop = new Properties();  
        InputStream fis = null;  
        OutputStream fos = null;
        boolean result=true;
        try {
            //java.net.URL  url = ConfigUtil.class.getClassLoader().getSystemResource(fileURL);  
        	
        	URL filePath = Thread.currentThread().getContextClassLoader().getResource(fileURL+".properties");
        	File file = new File(filePath.toURI());  
            if (!file.exists())  
                file.createNewFile();  
            fis = new FileInputStream(file);  
            prop.load(fis);  
            fis.close();//一定要在修改值之前关闭fis  
            fos = new FileOutputStream(file);
            prop.setProperty(key, value);  
            prop.store(fos, "Update '" + key + "' value");
            fos.close();
              
        } catch (Exception e) {  
            System.err.println("Visit " + fileURL + " for setProperty "  
            + key + " error"); 
            result=false;
        }// catch (URISyntaxException e) {  
            // TODO Auto-generated catch block  
         //   e.printStackTrace();  
         //   result=false;
        //}  
        finally{  
            try {  
                fos.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();
                result=false;
            }  
        }
        return result;
    }
    public static Properties getProperties(String fileURL){
    	Properties prop = new Properties(); 
    	InputStream fis = null;  
    	try {
            //java.net.URL  url = ConfigUtil.class.getClassLoader().getSystemResource(fileURL);  
        	
        	URL filePath = Thread.currentThread().getContextClassLoader().getResource(fileURL+".properties");
        	File file = new File(filePath.toURI());  
            if (!file.exists())  
                file.createNewFile();  
            fis = new FileInputStream(file);  
            prop.load(fis);  
            fis.close();//一定要在修改值之前关闭fis  
        } catch (Exception e) {  
            System.err.println("Visit " + fileURL + " for getProperties error."); 
        }// catch (URISyntaxException e) {  
            // TODO Auto-generated catch block  
         //   e.printStackTrace();  
         //   result=false;
        //}  
		return prop;
    }
    public static boolean setProperties(Properties properties,String fileURL){
    	Properties prop = new Properties();  
        InputStream fis = null;  
        OutputStream fos = null;
        boolean result=true;
        try {
            //java.net.URL  url = ConfigUtil.class.getClassLoader().getSystemResource(fileURL);  
        	
        	URL filePath = Thread.currentThread().getContextClassLoader().getResource(fileURL+".properties");
        	File file = new File(filePath.toURI());  
            if (!file.exists())  
                file.createNewFile();  
            fis = new FileInputStream(file);  
            prop.load(fis);  
            fis.close();//一定要在修改值之前关闭fis  
            fos = new FileOutputStream(file);
            prop.putAll(properties);
            prop.store(fos, "Update '" + properties.keySet().toString() + "' values");
            fos.close();
              
        } catch (Exception e) {  
            System.err.println("Visit " + fileURL + " for setProperties error."); 
            result=false;
        }// catch (URISyntaxException e) {  
            // TODO Auto-generated catch block  
         //   e.printStackTrace();  
         //   result=false;
        //}  
        finally{  
            try {  
                fos.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();
                result=false;
            }  
        }
        return result;
    }
}  
