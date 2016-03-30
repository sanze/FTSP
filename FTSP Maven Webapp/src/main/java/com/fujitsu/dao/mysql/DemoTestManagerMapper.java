package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.manager.demoForNew.model.DemoTest;

public interface DemoTestManagerMapper {
	/**
	 * Method name: selectDemoDataList <BR>
	 * Description: 查询所有demoTest分页数据<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public List<Map> selectDemoDataList(@Param(value = "map")Map map);
	/**
	 * Method name: countDemoDataList <BR>
	 * Description: 查询所有demoTest总数<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public int countDemoDataList(@Param(value = "map")Map map);
	/**
	 * Method name: insert <BR>
	 * Description: 插入数据<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public void insert(@Param(value = "demoTest")DemoTest demoTest);
	/**
	 * Method name: update <BR>
	 * Description: 更新数据<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return Map<String, Object><BR>
	 */
	public void update(@Param(value = "demoTest")DemoTest demoTest);
	/**
	 * Method name: deleteDemoTest <BR>
	 * Description: 删除demo_test一行记录<BR>
	 * Remark: 2013-12-15<BR>
	 * @author hg
	 * @return String<BR>
	 */
	public void delete(@Param(value = "demoTest")DemoTest demoTest);
}
