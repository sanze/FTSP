package com.fujitsu.IService;
import java.util.List;
import java.util.Map;
import com.fujitsu.common.CommonException;
public interface IColorManagement {
	/**
	 * Method name: setAlarmColorConfig <BR>
	 * Description: 设置各个告警等级的配置信息<BR>
	 * Remark: 2014-01-15<BR>
	 * @author wuchao
	 * @return boolean<BR>
	 */
	public boolean setAlarmColorConfig(List<Map> datas) throws CommonException;
	/**
	 * Method name: getAlarmColorConfig <BR>
	 * Description: 获取各个告警等级的配置信息<BR>
	 * Remark: 2014-01-15<BR>
	 * @author wuchao
	 * @return boolean<BR>
	 */
	public List<Map> getAlarmColorConfig(Map map) throws CommonException;
	
}
