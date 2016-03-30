package com.fujitsu.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.model.ItemSelectInfo;
import com.fujitsu.model.PtpDomainModel;

public class XmlUtil {
	
	/**
	 * 取得ptp type相关信息
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws CommonException 
	 */
	public static List<PtpDomainModel> parserXmlForPtpDomain(String fileName) throws CommonException {
		//读取配置文件，取得初始值
		InputStream in = XmlUtil.class.getResourceAsStream("/resourceConfig/ptpConfig/"+fileName);
		SAXReader saxReader = new SAXReader();
		// 结果列表
		List<PtpDomainModel> messageList = new ArrayList<PtpDomainModel>();

		try {
			Document document = saxReader.read(in);
			Element items = document.getRootElement();
			
			List<Short> layerList = null;
			for (Iterator i = items.elementIterator(); i.hasNext();) {
				Element item = (Element) i.next();
				PtpDomainModel message = new PtpDomainModel();
				// 每个对象
				for (Iterator j = item.elementIterator(); j.hasNext();) {
					Element node = (Element) j.next();
					if (node.getName().equals("DOMAIN")) {
						String domain = node.getTextTrim().toLowerCase();
						if (DataCollectDefine.COMMON.DOMAIN_SDH_.equals(domain)) {
							message.setDomainFlag(DataCollectDefine.COMMON.DOMAIN_SDH_FLAG);
						}else if (DataCollectDefine.COMMON.DOMAIN_WDM_.equals(domain)) {
							message.setDomainFlag(DataCollectDefine.COMMON.DOMAIN_WDM_FLAG);
						}else if (DataCollectDefine.COMMON.DOMAIN_ETH_.equals(domain)) {
							message.setDomainFlag(DataCollectDefine.COMMON.DOMAIN_ETH_FLAG);
						}else if (DataCollectDefine.COMMON.DOMAIN_ATM_.equals(domain)) {
							message.setDomainFlag(DataCollectDefine.COMMON.DOMAIN_ATM_FLAG);
						}else{
							//未知domain
							message.setDomainFlag(DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG);
						}
						message.setDomain(domain);
					} else if (node.getName().equals("LAYER")) {
						String layerString = node.getText().trim();
						if(layerString!=null&&!layerString.isEmpty()){
							layerList = new ArrayList<Short>();
							String[] layer = layerString.split(",");
							for(String temp:layer){
								layerList.add(Short.valueOf(temp));
							}
						}
						message.setLayerList(layerList);
					} else if (node.getName().equals("PTP_TYPE")) {
						message.setPtpType(node.getText().trim());
					} else if (node.getName().equals("RATE")) {
						message.setRate(node.getText().trim());
					} else if (node.getName().equals("PRIORITY")) {
						message.setPriority(Integer.valueOf(node.getText().trim()));
					}
				}
				messageList.add(message);
			}
			//排序
			Collections.sort(messageList);
			return messageList;
		} catch (DocumentException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION);
			}
		}
	}
	
	/**
	 * 解析层速率配置文件xml文件并返回ItemSelectInfo List数据
	 * 
	 * @param fileName
	 * @return 解析失败返回null
	 * @throws CommonException 
	 */
	public static List<ItemSelectInfo> parserXmlForLayRate(String fileName) throws CommonException {
		//读取配置文件，取得初始值
		//使用流形式否则中文路径出错
		InputStream in = XmlUtil.class.getResourceAsStream("/resourceConfig/layerRateConfig/"+fileName);

		SAXReader saxReader = new SAXReader();
		// 结果列表
		List<ItemSelectInfo> messageList = new ArrayList<ItemSelectInfo>();

		try {
			Document document = saxReader.read(in);
			Element items = document.getRootElement();
			for (Iterator i = items.elementIterator(); i.hasNext();) {
				Element item = (Element) i.next();
				ItemSelectInfo message = new ItemSelectInfo();
				// 每个对象
				for (Iterator j = item.elementIterator(); j.hasNext();) {
					Element node = (Element) j.next();
					if (node.getName().equals("key")) {
						message.setKey(node.getText());
					} else if (node.getName().equals("value1")) {
						message.setValue1(node.getText());
					}
				}
				messageList.add(message);
			}
			return messageList;
		} catch (DocumentException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION);
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION);
			}
		}
	}
	
	public static void main(String[] args){
		List<PtpDomainModel> xxx = null;
		
		try {
			xxx = parserXmlForPtpDomain("HW_PTP_DOMAIN.xml");
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(PtpDomainModel model:xxx){
			System.out.println(model.getPriority());
		}
	}
}
