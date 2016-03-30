package com.fujitsu;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;

import com.fujitsu.IService.IWSManagerService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

import com.fujitsu.util.SpringContextUtil;

/**
 * Maven
 * @author xuxiaojun
 *
 */
public class TestWS {

//	@Test
//	public void testFttsWS(){
//		
//		 //调用WebService
//        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//        factory.setServiceClass(IWSManagerService.class);
//        factory.setAddress("http://127.0.0.1:8080/FTTS/webservice/fttsWS");
//        
//        IWSManagerService service = (IWSManagerService) factory.create();
//        
//        System.out.println("开始测试ws的runTest()方法");
//        
//        try {
//			boolean result = service.runTest("34", 1);
//			
//			System.out.println(result);
//		} catch (CommonException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void testFttsWSUseSpring(){
		
		SpringContextUtil util = new SpringContextUtil(true);
		
		IWSManagerService service = (IWSManagerService) util.getBean("fttsWsClient");
        
        System.out.println("开始测试ws的runTest()方法");
        
        try {
			CommonResult result = service.runTest("34",null, 1);
			
			System.out.println(result.getReturnId() + ":" + result.getReturnMessage());
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}
	
	
}
