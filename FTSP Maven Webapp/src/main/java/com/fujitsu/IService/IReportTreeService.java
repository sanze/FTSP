package com.fujitsu.IService;
import java.util.List;
import java.util.Map;
import com.fujitsu.common.CommonException;
import com.fujitsu.manager.resourceManager.model.ReportTreeModel;
public interface IReportTreeService {
	public List<Map> treeGetChildNodes(ReportTreeModel reportTreeModel) throws CommonException;
}
	
	
