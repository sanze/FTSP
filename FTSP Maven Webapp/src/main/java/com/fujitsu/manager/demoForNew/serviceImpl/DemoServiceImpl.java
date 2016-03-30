package com.fujitsu.manager.demoForNew.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.dao.mysql.DemoTestManagerMapper;
import com.fujitsu.manager.demoForNew.model.DemoTest;
import com.fujitsu.manager.demoForNew.service.DemoService;
import com.mongodb.Mongo;

@Service
@Transactional(rollbackFor = Exception.class)
public class DemoServiceImpl extends DemoService {
	
	@Resource
	private DemoTestManagerMapper demoTestManagerMapper;
//	
	@Autowired
	private Mongo mongo;
	public Mongo getMongo() {
		return mongo;
	}
	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}

	@Override
	public Map<String, Object> getAllDemoData(int startNumber, int pageSize) {		
		Map map=new HashedMap();
		Map returnMap=new HashedMap();
		List<Map> enigneerList = new ArrayList<Map>();
		
		int total=demoTestManagerMapper.countDemoDataList(map);
		
		map.put("startNumber", startNumber);
		map.put("pageSize", pageSize);
		
		enigneerList=demoTestManagerMapper.selectDemoDataList(map);
		returnMap.put("rows", enigneerList);
		returnMap.put("total", total);
		
				
		return returnMap;
	}
	/**
	 * Method name: saveDemoTest <BR>
	 * Description: 保存Demo_test表单元素<BR>
	 * Remark: 2013-11-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> saveDemoTest(DemoTest demoTest){
		Map m = new HashMap();
		try{
			if(demoTest.getId().equals("0")){
				demoTestManagerMapper.insert(demoTest);
			}else{
				demoTestManagerMapper.update(demoTest);
			}
			
			m.put("success", true);
			m.put("msg", "保存成功！");
		}catch(Exception e){
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "保存失败！");
		}
		return m;
	}
	/**
	 * Method name: deleteDemoTest <BR>
	 * Description: 删除demo_test一行记录<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	@Override
	public Map<String, Object> deleteDemoTest(DemoTest demoTest) {
		Map m = new HashMap();
		try{
			demoTestManagerMapper.delete(demoTest);
			m.put("success", true);
			m.put("msg", "保存成功！");
		}catch(Exception e){
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "保存失败！");
		}
		return null;
	}
}
