package com.fujitsu.manager.commonManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.util.ExportExcelUtil;
import com.fujitsu.IService.IExportExcel;

public class ExportAction extends DownloadAction {
	public String execute(){
		/* service层部分 开始 */
		List list = new ArrayList();
		Map map = new HashMap();
		//for (int i = 0; i < 1000; i++) {
			map.put("COL1", 111);
			map.put("COL2", 111);
			list.add(map);
		//}
		IExportExcel ex = new ExportExcelUtil(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,"DEAULT");
		String destination=ex.writeExcel(list, CommonDefine.EXCEL.HEADER_DEFAULT,false);
		System.out.print(destination);
		/* service层部分 结束 */
		setFilePath(destination);
    	return RESULT_DOWNLOAD;
    }
}
