package com.fujitsu;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;

import com.fujitsu.IService.IWSManagerService;

/**
 * Maven
 * @author xuxiaojun
 *
 */
public class TestWS {

	@Test
	public void testFtspWS(){
		
		 //调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(IWSManagerService.class);
        factory.setAddress("http://127.0.0.1:8080/FTSP_WS/webservice/ftspWS");
        
        IWSManagerService service = (IWSManagerService) factory.create();
        
        System.out.println("开始测试ws的getEmsList()方法");
        
        String dataList= service.getEmsList();
        
        System.out.println(dataList);

        System.out.println("开始测试ws的getNeList()方法");
        
        dataList= service.getNeList(4, null);
        
        System.out.println(dataList);
        
        System.out.println("开始测试ws的getUnitList()方法");
        
        dataList= service.getUnitList(203, null);
        
        System.out.println(dataList);
        
        System.out.println("开始测试ws的getPtpList()方法");
        
        dataList= service.getPtpList(6675);
        
        System.out.println(dataList);
	}
	
	
}
