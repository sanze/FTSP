package com.fujitsu.IService;

import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.demoForNew.model.DemoTest;

public interface IDemoService {
	/**
	 * Method name: getAllDemoData <BR>
	 * Description: 查询Demo)test表所有元素<BR>
	 * Remark: 2013-11-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAllDemoData(int startNumber, int pageSize) throws CommonException;
	/**
	 * Method name: saveDemoTest <BR>
	 * Description: 保存Demo_test表单元素<BR>
	 * Remark: 2013-11-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> saveDemoTest(DemoTest demoTest);
	/**
	 * Method name: deleteDemoTest <BR>
	 * Description: 删除demo_test一行记录<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	public Map<String, Object> deleteDemoTest(DemoTest demoTest);
	
	
}
