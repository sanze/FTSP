package com.fujitsu.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

/**
 * @author xuxiaojun
 *
 */
public class XmlUtil {

	/**
	 * 生成xml文件
	 * @param data
	 * @param type 生成xml报文类型
	 * @return
	 * @throws CommonException
	 */
//	public static String generalXml(String fileName, Map data, List<Map> subDataList, int type){
//		String rootElementName = "";
//		String subRootElementName = "";
//
//		//生成文件
//		String xmlString = generalXmlImpl(data, subDataList, fileName, rootElementName, subRootElementName);
//		
//		return xmlString;
//	}
	
	
	public static String generalXmlImpl(Map data, int type,String subType) {

		Document doc = DocumentHelper.createDocument();
		try {
			// 添加根元素并设置属性
			Element rootElement = DocumentHelper.createElement("chart");
			
			Map attributeMap = readChartConfig(type*10+1,subType);
			
			Set set = attributeMap.keySet();
			
			for(Object obj:set){
				if(attributeMap.get(obj).toString().equals("random")){
					rootElement.addAttribute(obj.toString(),getRandomColor());
				}else{
					rootElement.addAttribute(obj.toString(),attributeMap.get(obj).toString());
				}
			}
			doc.setRootElement(rootElement);

			//填入数据
			set = data.keySet();
			for(Object obj:set){
				// 设置第一级元素及属性
				Element firstElement = rootElement.addElement("set");
				attributeMap = readChartConfig(type*10+2,subType);
				//添加数据属性
				Set attributeSet = attributeMap.keySet();
				for(Object attribute:attributeSet){
					if(attributeMap.get(attribute).toString().equals("random")){
						firstElement.addAttribute(attribute.toString(),getRandomColor());
					}else{
						firstElement.addAttribute(attribute.toString(),attributeMap.get(attribute).toString());
					}
					
				}
				
				firstElement.addAttribute("label",obj.toString());
				firstElement.addAttribute("value",data.get(obj).toString());
			}
			doc.setXMLEncoding("GBK");

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return doc.asXML();
	}
	
	public static String generalXmlImpl(List<Map> dataList, int type) {

		Document doc = DocumentHelper.createDocument();
		try {
			// 添加根元素并设置属性
			Element rootElement = DocumentHelper.createElement("chart");
			
			Map attributeMap = readChartConfig(type*10+1,null);
			
			Set set = attributeMap.keySet();
			
			for(Object obj:set){
				if(attributeMap.get(obj).toString().equals("random")){
					rootElement.addAttribute(obj.toString(),getRandomColor());
				}else{
					rootElement.addAttribute(obj.toString(),attributeMap.get(obj).toString());
				}
			}
			doc.setRootElement(rootElement);
			
			Element categories = rootElement.addElement("categories");
			
			attributeMap = readChartConfig(type*10+2,null);
			//添加数据属性
			Set attributeSet = attributeMap.keySet();
			for(Object attribute:attributeSet){
				if(attributeMap.get(attribute).toString().equals("random")){
					categories.addAttribute(attribute.toString(),getRandomColor());
				}else{
					categories.addAttribute(attribute.toString(),attributeMap.get(attribute).toString());
				}
			}
			Element datasetVC4 = rootElement.addElement("dataset");
			attributeMap = readChartConfig(type*10+3,"VC4");
			//添加数据属性
			attributeSet = attributeMap.keySet();
			for(Object attribute:attributeSet){
				if(attributeMap.get(attribute).toString().equals("random")){
					datasetVC4.addAttribute(attribute.toString(),getRandomColor());
				}else{
					datasetVC4.addAttribute(attribute.toString(),attributeMap.get(attribute).toString());
				}
			}
			Element datasetVC12 = rootElement.addElement("dataset");
			
			attributeMap = readChartConfig(type*10+3,"VC12");
			//添加数据属性
			attributeSet = attributeMap.keySet();
			for(Object attribute:attributeSet){
				if(attributeMap.get(attribute).toString().equals("random")){
					datasetVC12.addAttribute(attribute.toString(),getRandomColor());
				}else{
					datasetVC12.addAttribute(attribute.toString(),attributeMap.get(attribute).toString());
				}
			}

			for(Map data:dataList){
				Element category = categories.addElement("category");
				category.addAttribute("label", data.get("TYPE_NAME").toString());
				Element setVC4 = datasetVC4.addElement("set");
				setVC4.addAttribute("value", data.get("TYPE_VALUE_VC4").toString());
				Element setVC12 = datasetVC12.addElement("set");
				setVC12.addAttribute("value", data.get("TYPE_VALUE_VC12").toString());
			}
			
//			//填入数据
//			set = data.keySet();
//			for(Object obj:set){
//				// 设置第一级元素及属性
//				Element categories = rootElement.addElement("categories");
//				attributeMap = readChartConfig(type*10+2);
//				//添加数据属性
//				Set attributeSet = attributeMap.keySet();
//				for(Object attribute:attributeSet){
//					if(attributeMap.get(attribute).toString().equals("random")){
//						categories.addAttribute(attribute.toString(),getRandomColor());
//					}else{
//						categories.addAttribute(attribute.toString(),attributeMap.get(attribute).toString());
//					}
//				}
//				
//				firstElement.addAttribute("label",obj.toString());
//				firstElement.addAttribute("value",data.get(obj).toString());
//			}
			doc.setXMLEncoding("GBK");

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return doc.asXML();
	}
	
	
	/**
	 * @param configType
	 * 11： 槽道综合可用率配置 12：槽道综合可用率数据配置 13：各类型板卡槽道可用率配置 14：各类型板卡槽道可用率数据配置
	 * @return
	 */
	public static Map readChartConfig(int configType,String subType){
		String nodePath = "";
		
		String configFilePath = "resourceConfig/chartConfig/availabilityChartConfig.xml";
		
		subType = subType == null?"":subType;
		
		switch(configType){
		//槽道可用率
		case CommonDefine.NETWORK.NWA_SLOT_ZONGHE*10+1:
			nodePath = "//chartConfig/slotAvailability_zonghe/chart";
			break;
		case CommonDefine.NETWORK.NWA_SLOT_ZONGHE*10+2:
			nodePath = "//chartConfig/slotAvailability_zonghe/set";
			break;
		case CommonDefine.NETWORK.NWA_SLOT_SUB*10+1:
			nodePath = "//chartConfig/slotAvailability/chart";
			break;
		case CommonDefine.NETWORK.NWA_SLOT_SUB*10+2:
			nodePath = "//chartConfig/slotAvailability/set";
			break;
			
		//端口可用率
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE*10+1:
			nodePath = "//chartConfig/portAvailability_zonghe/chart";
			break;
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE*10+2:
			nodePath = "//chartConfig/portAvailability_zonghe/set";
			break;
		case CommonDefine.NETWORK.NWA_PORT_SUB*10+1:
			nodePath = "//chartConfig/portAvailability/chart";
			break;
		case CommonDefine.NETWORK.NWA_PORT_SUB*10+2:
			nodePath = "//chartConfig/portAvailability/set";
			break;
			
		//时隙可用率
		case CommonDefine.NETWORK.NWA_CTP_ZONGHE*10+1:
			nodePath = "//chartConfig/ctpAvailability_zonghe_"+subType+"/chart";
			break;
		case CommonDefine.NETWORK.NWA_CTP_ZONGHE*10+2:
			nodePath = "//chartConfig/ctpAvailability_zonghe_"+subType+"/set";
			break;
		case CommonDefine.NETWORK.NWA_CTP_SUB*10+1:
			nodePath = "//chartConfig/ctpAvailability/chart";
			break;
		case CommonDefine.NETWORK.NWA_CTP_SUB*10+2:
			nodePath = "//chartConfig/ctpAvailability/categories";
			break;		
		case CommonDefine.NETWORK.NWA_CTP_SUB*10+3:
			nodePath = "//chartConfig/ctpAvailability/dataset_"+subType;
			break;		

		//端口可用率--route
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE_ROUTE*10+1:
			nodePath = "//chartConfig/portAvailability_zonghe/chart";
			break;
		case CommonDefine.NETWORK.NWA_PORT_ZONGHE_ROUTE*10+2:
			nodePath = "//chartConfig/portAvailability_zonghe/set";
			break;
		case CommonDefine.NETWORK.NWA_PORT_SUB_ROUTE*10+1:
			nodePath = "//chartConfig/portAvailability/chart";
			break;
		case CommonDefine.NETWORK.NWA_PORT_SUB_ROUTE*10+2:
			nodePath = "//chartConfig/portAvailability/set";
			break;
		}
		
		Map config = new HashMap();
		
		Element element = getChartConfigElement(configFilePath , nodePath);
		
		if(element!=null){
			Iterator it = element.elementIterator();
			while(it.hasNext()){
				
				Element e = (Element) it.next();
				config.put(e.getName().trim(),e.getTextTrim());
			}
		}
		return config;
	}
	
	//讀取指定節點的xml片段
	private static Element getChartConfigElement(String configFilePath,String nodePath) {
		
		Element element = null;
		// 读取配置文件，取得初始值
		// 使用流形式否则中文路径出错
		
		URL filePath = Thread.currentThread().getContextClassLoader().getResource(configFilePath);

		InputStream in = null;

		SAXReader saxReader = new SAXReader();
		// 结果列表
//		List<ItemSelectInfo> messageList = new ArrayList<ItemSelectInfo>();

		try {
			File file = new File(filePath.toURI());  
			in = new BufferedInputStream(new FileInputStream(file));
			
			Document document = saxReader.read(in);
			
			element = (Element) document  
                    .selectSingleNode(nodePath);  

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				
			}
		}
		return element;
	}
	
	

	 /** 
     * 随即获得颜色代码 
     */  
	public static String getRandomColor() {
		// 颜色代码位数6
		int colorLength = 6;
		// 颜色代码组数
		char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', '0', '1', '2',
				'3', '4', '5', '6', '7', '8', '9' };
		StringBuffer str = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < colorLength; i++) {
			str.append(codeSequence[random.nextInt(16)]);
		}
		return str.toString();
	}
}
