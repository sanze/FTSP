package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

public interface IExportExcel {
	/**
	 * 用户可以将数据导入到excel表中，用户可以指定文件存放路径，文件名，sheet名，表列宽度等参数（该方法暂不支持合并单元格功能）
	 * @param data 数据源 
	 * @param header 自定义表格头，headerCode为表头的编码 。表格的编码和格式在resourceConfig.excelHeader文件中配置。
	 * @param append 是否需要继续追加数据。当append为false时直接写入数据，操作完成后自动关闭文件。
	 * @return 返回为空则导出失败，否则为文件存放位置
	 * 当append为true时可以向同一个文件中追加数据，追加完数据后必须显示得用close()方法关闭文件。
	 * @author DaiHuijun
	 */
	public String writeExcel(List<Map> data, int headerCode, boolean append);
	public String writeExcel(List<Map> data,String headerStr, int headerCode, boolean append);
	// 关闭文件
	/**
	 * 
	 * @return true:关闭成功,false:文件已处于关闭状态，该操作无效
	 */
	public boolean close();
}
