package testExternalConnect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;

import com.fujitsu.IService.IExternalConnectManagerService;
import com.fujitsu.IService.IWSManagerService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.util.SpringContextUtil;

public class ExternalConnectTest {
	@Test
	public void testGetCableList(){
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IExternalConnectManagerService service = (IExternalConnectManagerService) SpringContextUtil.getBean("externalConnectManagerImpl");
		
		try {
			Map<String,Object> result = service.getCableList(2);
			int total = 0;
			List<Map<String,Object>> rows;
			if (result.get("total") != null) {
				total = Integer.valueOf(result.get("total").toString());
			}
			if (total > 0) {
				rows = (List<Map<String,Object>>) result.get("rows");
				System.out.println("CableId          : CableName          ");
				for (Map<String,Object> map : rows) {
					System.out.println(map.get("CABLE_ID").toString()+"     : "+map.get("CABLE_NAME_FTTS").toString());
				}
			}
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetFiberListByCableId() {
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IExternalConnectManagerService service = (IExternalConnectManagerService) SpringContextUtil.getBean("externalConnectManagerImpl");
		
		try {
			Map<String,Object> result = service.getFiberListByCableId(1);
			int total = 0;
			List<Map<String,Object>> rows;
			if (result.get("total") != null) {
				total = Integer.valueOf(result.get("total").toString());
			}
			if (total > 0) {
				rows = (List<Map<String,Object>>) result.get("rows");
				System.out.println("RESOURCE_FIBER_ID : FIBER_NO : FIBER_NAME  :  NOTE");
				for (Map<String,Object> map : rows) {
					System.out.println(map.get("RESOURCE_FIBER_ID").toString()+" : "+map.get("FIBER_NO").toString()+" : "+
					map.get("FIBER_NAME").toString()+" : "+map.get("NOTE").toString());
				}
			}
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
	}
	
	@Test
	public void testGetRcListByStationId() {
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IExternalConnectManagerService service = (IExternalConnectManagerService) SpringContextUtil.getBean("externalConnectManagerImpl");
		
		try {
			Map<String,Object> result = service.getRcListByStationId(1);
			int total = 0;
			List<Map<String,Object>> rows;
			if (result.get("total") != null) {
				total = Integer.valueOf(result.get("total").toString());
			}
			if (total > 0) {
				rows = (List<Map<String,Object>>) result.get("rows");
				System.out.println("RC_ID  :  NUMBER  :  NAME");
				for (Map<String,Object> map : rows) {
					System.out.println(map.get("RC_ID").toString()+" : "+map.get("NUMBER").toString()+" : "+map.get("NAME").toString());
				}
			}
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
	}
	
	@Test
	public void testGetUnitListByRcId() {
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IExternalConnectManagerService service = (IExternalConnectManagerService) SpringContextUtil.getBean("externalConnectManagerImpl");
		
		try {
			Map<String,Object> result = service.getUnitListByRcId(1);
			int total = 0;
			List<Map<String,Object>> rows;
			if (result.get("total") != null) {
				total = Integer.valueOf(result.get("total").toString());
			}
			if (total > 0) {
				rows = (List<Map<String,Object>>) result.get("rows");
				System.out.println("UNIT_ID  :  NAME  :  SLOT_NO  :  PORT_COUNT  :  PORT_IDS");
				for (Map<String,Object> map : rows) {
					System.out.println(map.get("UNIT_ID").toString()+" : "+map.get("NAME").toString()+" : "+map.get("SLOT_NO").toString()+
							"  :  "+map.get("PORT_COUNT").toString()+"  :  "+map.get("PORT_IDS").toString());
				}
			}
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
	}
	
	@Test
	public void testGetConnectInfoByStationId() {
		SpringContextUtil xxxxxx = new SpringContextUtil(true);
		
		IExternalConnectManagerService service = (IExternalConnectManagerService) SpringContextUtil.getBean("externalConnectManagerImpl");
		
		try {
			Map<String,Object> result = service.getConnectInfoByStationId(1);
			int total = 0;
			List<Map<String,Object>> rows;
			if (result.get("total") != null) {
				total = Integer.valueOf(result.get("total").toString());
			}
			if (total > 0) {
				rows = (List<Map<String,Object>>) result.get("rows");
				System.out.println("CONNECT_ID  :  STATION_ID  :  A_END_ID  :  Z_END_ID  :  CONN_TYPE  :  FIBERINFO");
				for (Map<String,Object> map : rows) {
					System.out.println(map.get("CONNECT_ID").toString()+" : "+map.get("STATION_ID").toString()+" : "+map.get("A_END_ID").toString()+
							"  :  "+map.get("Z_END_ID").toString()+"  :  "+map.get("CONN_TYPE").toString()+"  :  "+map.get("FIBERINFO").toString());
				}
			}
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void wsTest() {
		 //调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(IWSManagerService.class);
        factory.setAddress("http://127.0.0.1:8080/FTTS/webservice/fttsWS");
        
        IWSManagerService service = (IWSManagerService) factory.create();

        Map<String, Object> testParam = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		sb.append("<XML>");
		sb.append("<OTDR_WAVE_LENGTH>1310</OTDR_WAVE_LENGTH>");
		sb.append("<OTDR_RANGE>25</OTDR_RANGE>");
		sb.append("<OTDR_PLUSE_WIDTH>30</OTDR_PLUSE_WIDTH>");
		sb.append("<OTDR_TEST_DURATION>15</OTDR_TEST_DURATION>");
		sb.append("<OTDR_REFRACT_COEFFICIENT>1.4685</OTDR_REFRACT_COEFFICIENT>");
		sb.append("</XML>");
        
        try {
        	CommonResult result = service.runTest("10", sb.toString(), 3);
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
