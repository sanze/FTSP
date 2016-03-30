package com.fujitsu.common.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.fujitsu.common.ExportResult;

public class MultiHearderExcelUtil {
	private MultiHearderExcelUtil _instance;
	private SXSSFWorkbook book;
	private SXSSFSheet sheet;
	private FileOutputStream out = null;
	private List<Map> dat = null;
	private int row;
	private int col;
	private int success;
	private final int PAGE_LIMIT = 1000000;
	private static final String TITLE = "title";
	private static final String SUBTITLE = "subtitle";
	private Map<String, CellStyle> styleMap;
	protected File file;
	// 多层表头
	private List<List<MultiColumnMap>> header;
	// 单独传一下最后一层关联数据的表头，如果不传则使用多层表头最后一行
	private List<MultiColumnMap> dataColumn;
	// 在表格的最下面，可能会需要一些额外信息行，比如记录时间，记录人。
	// 这个List的内容将会在数据导出结束后，写在最下面
	private List<List<MultiColumnMap>> addBottomCols;
	// 行游标
	private int rowVernie = 0;
	// 列游标
	private int colVernie = 0;
	// 页游标
	private int pageVernie = 0;
	// 行计数
	private int count = 0;
	// 待合并区域List
	private List<Map<String, Integer>> mergeRegionList;
	// 用于替换0值的
//	private String replaceZero = null;
	// 用于替换空值的
	private String replaceEmpty = "";
	
	public MultiHearderExcelUtil(String fileName) {

		try {
			file =  new File(fileName);
			String parentPath=file.getParent();
			if(file.exists()){
    			file.delete();
    		}
    		File dirFile = new File(parentPath);
    		if (!(dirFile.exists()) || !(dirFile.isDirectory())) {
    			dirFile.mkdirs();
    			try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
			book = new SXSSFWorkbook(-1); 
			out = new FileOutputStream(fileName);
			mergeRegionList = new ArrayList<Map<String, Integer>>();
			styleMap = new HashMap<String, CellStyle>();
			intiColumn();
		} catch (FileNotFoundException e) {
			// T1ODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// T1ODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void intiColumn() {
		CellStyle commonStyle = book.createCellStyle();
		commonStyle
				.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		commonStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		commonStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		commonStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		commonStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		commonStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		commonStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		commonStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		styleMap.put("COMMON", commonStyle);

		CellStyle titleStyle = book.createCellStyle();
		Font fontBig = book.createFont();
		fontBig.setFontHeight((short) 400);
		fontBig.setColor(HSSFColor.BLACK.index);
		fontBig.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleStyle.setFont(fontBig);
		titleStyle
				.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		styleMap.put("TITLE", titleStyle);

		Font font = book.createFont();
		font.setColor(HSSFColor.BLACK.index);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		CellStyle subStyle = book.createCellStyle();
		subStyle.setFont(font);
		subStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		subStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		subStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
		subStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		styleMap.put("SUB", subStyle);

		CellStyle headerStyle = book.createCellStyle();
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW
				.getIndex());
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		styleMap.put("HEADER", headerStyle);
	}

	/**
	 * 导出数据到一页(一页最大 100万行),然后关闭文件
	 * 
	 * @param sheetName
	 *            表名
	 * @param dat
	 *            数据源
	 * @return
	 */
	public boolean writeSheet(String sheetName, List<Map> dat) {
		return writeSheet(sheetName, dat, PAGE_LIMIT, false);
	}

	/**
	 * 导出数据到一页(一页最大 100万行)
	 * 
	 * @param sheetName
	 *            表名
	 * @param dat
	 *            数据源
	 * @param pageSize
	 *            每页数据量
	 * @return
	 */

	public boolean writeSheet(String sheetName, List<Map> dat, int pageSize) {
		return writeSheet(sheetName, dat, pageSize, false);
	}

	/**
	 * 导出数据到一页(一页最大 100万行)
	 * 
	 * @param sheetName
	 *            表名
	 * @param dat
	 *            数据源
	 * @param append
	 *            是否追加数据 --true 后面还有数据，需要继续输入 --false 后面没有数据，直接结束
	 * @return
	 */
	public boolean writeSheet(String sheetName, List<Map> dat, boolean append) {
		return writeSheet(sheetName, dat, PAGE_LIMIT, append);
	}

	/**
	 * 导出数据到一页
	 * 
	 * @param sheetName
	 *            表名
	 * @param dat
	 *            数据源
	 * @param pageSize
	 *            每页最大数据量
	 * @param append
	 *            是否追加数据 --true 后面还有数据，需要继续输入 --false 后面没有数据，直接结束
	 * @return
	 */
	public boolean writeSheet(String sheetName, List<Map> dat, int pageSize,
			boolean append) {
		boolean result = true;
		try {
			this.setData(dat);
			int total = dat.size();
			this.count += total;
			if (dataColumn == null || dataColumn.size() == 0)
				this.setDataColumn(this.header.get(this.header.size() - 1));
			// 判断是否需要分页,为了简化，策略是这次追加只要超过就全部写在新建的一页上
			if (this.pageVernie == 0 || this.count > pageSize) {
				String sheetNameN = sheetName + "【" + (this.pageVernie + 1)
						+ "】";
				sheetNameN = WorkbookUtil.createSafeSheetName(sheetNameN);
				sheet = (SXSSFSheet)book.createSheet(sheetNameN);
				this.pageVernie++;
				resetVernie();
				// 输出标题
				writeHeader();
			}
			// 输出数据
			if (dat.size() > 0)
				writeData();
			if (!append) {
				close();
			}
			this.success *= 1;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			this.success = 0;
		}
		return result;
	}

	/**
	 * 写入Excel实现函数
	 * 
	 * @param start
	 *            起始数据index
	 * @param pageSize
	 *            数据量
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean writeData() {
		try {

			// cur当前单元格值
			String cur = null;
			// 用来合并同类项,存了当时的坐标
			Map<String, Integer[]> comboRec = new HashMap<String, Integer[]>();
			// 保存相同的数据的个数
			Map<String, Integer> comboRecCnt = new HashMap<String, Integer>();

			// 数据写入
			for (int i = 0; i < dat.size(); i++) {

				Row sheetRow = sheet.createRow(this.rowVernie);
				// 行游标前进
				this.rowVernie++;
				// 列游标归零
				this.colVernie = 0;
				Map datMap = dat.get(i);
				// 写入每一行的数据
				for (int j = 0; j < col; j++) {
					Cell cell = sheetRow.createCell(j);
					// 列游标前进
					this.colVernie++;
					// 取值
					String key = dataColumn.get(j).getKey();
					String basisKey = null;
					if (dataColumn.get(j).getComboBasis() && key.contains("%")) {
						String[] keys = key.split("%");
						key = keys[0];
						basisKey = keys[1];
					} else {
						basisKey = key;
					}
					Object v = datMap.get(key);
					if (v != null) {
						cur = v.toString();
					} else {
						cur = this.replaceEmpty;
					}
					cell.setCellValue(cur);
//					cell.setCellType(HSSFCell.ENCODING_UTF_16);
					cell.setCellStyle(styleMap.get("COMMON"));

					if (dataColumn.get(j).getComboBasis()) {
						if (!comboRec.containsKey(String.valueOf(datMap.get(basisKey)))) {
							// 坐标【行，列】
							Integer[] coordinate = new Integer[2];
							// 行
							coordinate[0] = Integer.valueOf(this.rowVernie - 1);
							// 列
							coordinate[1] = Integer.valueOf(this.colVernie - 1);

							comboRec.put(String.valueOf(datMap.get(basisKey)),
									coordinate);
							comboRecCnt.put(String.valueOf(datMap.get(basisKey)), 1);
						} else {
//							sheetRow.removeCell(cell);
							comboRecCnt.put(String.valueOf(datMap.get(basisKey)),
									comboRecCnt.get(String.valueOf(datMap.get(basisKey))) + 1);
						}
						// 这里是对随从列的处理。这些列是不管自身的值，直接根据主列合并的。
						if(dataColumn.get(j).getRetinue().size()>0){
							List<Integer> retinue = dataColumn.get(j).getRetinue();
							for (Integer index : retinue) {
								String complexKey = String.valueOf(datMap.get(basisKey))	
										+ dataColumn.get(index).getKey();
								if (!comboRec.containsKey(complexKey)) {
									// 坐标【行，列】
									Integer[] c = new Integer[2];
									c[0] = Integer.valueOf(this.rowVernie - 1);
									c[1] = index;
									comboRec.put(complexKey,c);
									comboRecCnt.put(complexKey, 1);
								}else{
									comboRecCnt.put(complexKey,comboRecCnt.get(complexKey) + 1);
								}
							}
						}
													
					}
				}
			}
			// 开始合并
			Iterator<String> it = comboRec.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				int count = comboRecCnt.get(key).intValue();
				if (count == 1)
					continue;
				Integer[] coord = comboRec.get(key);
				Integer[] region = new Integer[4];
				// firstRow
				region[0] = coord[0];
				// lastRow
				region[1] = coord[0] + count - 1;
				// firstCol
				region[2] = coord[1];
				// lastCol
				region[3] = coord[1];
				// go merge
				doMerge(region);
			}
			sheet.flushRows();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	


	public Workbook getBook() {
		return book;
	}

	public void setBook(SXSSFWorkbook book) {
		this.book = book;
	}

	public FileOutputStream getOut() {
		return out;
	}

	public void setOut(FileOutputStream out) {
		this.out = out;
	}

	   /**
     * 对于同一文件分批导出的，使用此方法获取返回结果
     * @return ExportResult
     */
    public ExportResult getResult(){
    	close();
    	ExportResult rv = new ExportResult();
		rv.setExportTime(new Date());
		rv.setFileName(file.getName()); 
		rv.setFilePath(file.getPath());
		rv.setReturnMessage("success");
		rv.setSize((int)(file.length()/1024));
		rv.setReturnResult(success);
		return rv;
    }
    
    public Map<String, Object> getResultMap(){
//    	close();
    	Map<String, Object> rv = new HashMap<String, Object>();
		rv.put("EXPORT_TIME", new Date());
		rv.put("REPORT_NAME", file.getName());
		rv.put("EXCEL_URL", file.getPath());
		rv.put("SIZE", (int)(file.length()/1024));
		return rv;
    }
    
	public boolean close() {
		System.out.println("-----------close----------");
		int lastrownum = sheet.getLastRowNum();
		System.out.println(sheet.toString());
		if (out != null) {
			try {
				book.write(out);
				out.close();
				book.dispose();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public MultiHearderExcelUtil getInstance() {
		if (_instance == null) {
			_instance = new MultiHearderExcelUtil("D:\\aaaa.xlsx");
		}
		return _instance;
	}

	// TODO
	/**
	 * 写多层表头
	 */
	private void writeHeader() {
		for (int i = 0; i < this.header.size(); i++) {
			Row headerRow = sheet.createRow(this.rowVernie);
			List<MultiColumnMap> header = this.header.get(i);
			// 行游标设置
			this.rowVernie++;
			// 列游标归零
			this.colVernie = 0;
			for (int k = 0; k < header.size(); k++) {
				Cell cell = headerRow.createCell(this.colVernie);
				if (!header.get(k).getColumnName().isEmpty())
					cell.setCellValue(header.get(k).getColumnName());
				this.colVernie++;
				if (header.get(k).getOccupyCellNumx() > 1
						|| header.get(k).getOccupyCellNumy() > 1) {
					if (header.get(k).getKey()
							.equals(MultiHearderExcelUtil.TITLE)
							|| header.get(k).getKey()
									.equals(MultiHearderExcelUtil.SUBTITLE))
						addMergeRegion(header.get(k).getOccupyCellNumx(),
								header.get(k).getOccupyCellNumy(), 0);
					else
						addMergeRegion(header.get(k).getOccupyCellNumx(),
								header.get(k).getOccupyCellNumy(), 1);
				}
				for (int j = 1; j < header.get(k).getOccupyCellNumx(); j++) {
					// Cell emptyCell = headerRow.createCell(this.colVernie);
					this.colVernie++;
				}

				// 大标题和副标题需要特殊的格式 TODO
				if (header.get(k).getKey().equals(MultiHearderExcelUtil.TITLE)) {
					cell.setCellStyle(styleMap.get("TITLE"));
				} else if (header.get(k).getKey()
						.equals(MultiHearderExcelUtil.SUBTITLE)) {
					cell.setCellStyle(styleMap.get("SUB"));
				} else {
					cell.setCellStyle(styleMap.get("HEADER"));
				}
				// 最后一行表头需要设置一下列宽
				if (i == this.header.size() - 1) {
					sheet.setColumnWidth((short) this.colVernie - 1, header
							.get(k).getWidth() * 256);
				}
			}
		}
		doMerge();
		clearMergeRegion();
	}

	public void writeFooter(){
		if(this.addBottomCols==null||this.addBottomCols.size()==0)
			return;
		//开始写
		for (int i = 0; i < this.addBottomCols.size(); i++) {
			Row headerRow = sheet.createRow(this.rowVernie);
			List<MultiColumnMap> header = this.addBottomCols.get(i);
			// 行游标设置
			this.rowVernie++;
			// 列游标归零
			this.colVernie = 0;
			for (int k = 0; k < header.size(); k++) {
				Cell cell = headerRow.createCell(this.colVernie);
				if (!header.get(k).getColumnName().isEmpty())
					cell.setCellValue(header.get(k).getColumnName());
				this.colVernie++;
				if (header.get(k).getOccupyCellNumx() > 1
						|| header.get(k).getOccupyCellNumy() > 1) {
					if (header.get(k).getKey()
							.equals(MultiHearderExcelUtil.TITLE)
							|| header.get(k).getKey()
									.equals(MultiHearderExcelUtil.SUBTITLE))
						addMergeRegion(header.get(k).getOccupyCellNumx(),
								header.get(k).getOccupyCellNumy(), 0);
					else
						addMergeRegion(header.get(k).getOccupyCellNumx(),
								header.get(k).getOccupyCellNumy(), 1);
				}
				for (int j = 1; j < header.get(k).getOccupyCellNumx(); j++) {
					// Cell emptyCell = headerRow.createCell(this.colVernie);
					this.colVernie++;
				}
				cell.setCellStyle(styleMap.get("HEADER"));
			}
		}
		doMerge();
		clearMergeRegion();
			
	}
	
	/**
	 * 加入到需要合并单元格的记录里
	 * 
	 * @param occupyCellNumx
	 *            需要合并的列数
	 * @param occupyCellNumy
	 *            需要合并的行数
	 */
	private void addMergeRegion(int occupyCellNumx, int occupyCellNumy,
			int border) {
		Map<String, Integer> mergeRegion = new HashMap<String, Integer>();
		mergeRegion.put("firstRow", this.rowVernie - 1);
		mergeRegion.put("lastRow", this.rowVernie + occupyCellNumy - 2);
		mergeRegion.put("firstCol", this.colVernie - 1);
		mergeRegion.put("lastCol", this.colVernie + occupyCellNumx - 2);
		mergeRegion.put("border", border);
		this.mergeRegionList.add(mergeRegion);
	}

	/**
	 * 将需要合并的单元格执行合并
	 */
	private void doMerge() {
		if (this.mergeRegionList.size() > 0) {
			for (Map<String, Integer> mergeRegion : this.mergeRegionList) {
				CellRangeAddress region = new CellRangeAddress(mergeRegion.get(
						"firstRow").intValue(), mergeRegion.get("lastRow")
						.intValue(), mergeRegion.get("firstCol").intValue(),
						mergeRegion.get("lastCol").intValue());
				sheet.addMergedRegion(region);
				if (mergeRegion.get("border") != 0)
					setRegionBorder(1, region, sheet, book);
			}
		}
	}

	/**
	 * 合并坐标，(int firstRow, int lastRow, int firstCol, int lastCol)
	 * 
	 * @param coordinate
	 *            坐标
	 */
	private void doMerge(Integer[] coordinate) {
		CellRangeAddress region = new CellRangeAddress(coordinate[0].intValue(),
				coordinate[1].intValue(), coordinate[2].intValue(), coordinate[3].intValue());
		sheet.addMergedRegion(region);
		setRegionBorder(1, region, sheet, book);
	}

	/**
	 * 清空存储的合并区域信息
	 */
	private void clearMergeRegion() {
		this.mergeRegionList = new ArrayList<Map<String, Integer>>();
	}

	/**
	 * 重置页游标
	 */
	private void resetVernie() {
		this.rowVernie = 0;
		this.colVernie = 0;
	}

	private static void setRegionBorder(int border, CellRangeAddress region,
			Sheet sheet, Workbook wb) {
		RegionUtil.setBorderBottom(border, region, sheet, wb);
		RegionUtil.setBorderLeft(border, region, sheet, wb);
		RegionUtil.setBorderRight(border, region, sheet, wb);
		RegionUtil.setBorderTop(border, region, sheet, wb);
		//test
//		writeFile(region);
	}

	/*
	 * for test
	 */
	/*private static File fileFT;
	private static BufferedWriter bw;

	public static void openFile(String filename) {
		try {
			fileFT = new File(filename);
			bw = new BufferedWriter(new FileWriter(fileFT, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/*public static void writeFile(List<Map> src) {
		try {
			bw.newLine();
			bw.write("==================next page======================");
			for (Map m : src) {
				bw.newLine();
				Iterator it = m.keySet().iterator();
				bw.write("{");
				// while (it.hasNext()) {
				// String key = it.next().toString();
				// bw.write(key+"="+m.get(key)+",");
				bw.write(m.get("neId").toString() + ","
						+ m.get("ptpId").toString());
				// }
				bw.write("};");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void writeFile(CellRangeAddress region) {
		try {
			bw.newLine();
			bw.write("(" + region.getFirstColumn() + "," + region.getFirstRow()
					+ ")--(" + region.getLastColumn() + "," + region.getLastRow()
					+ ")");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeFile() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/*
     * 
     * */
	public List<Map> getData() {
		return dat;
	}

	public void setData(List<Map> dat) {
		this.dat = dat;
	}

	public List<List<MultiColumnMap>> getHeader() {
		return header;
	}

	public void setHeader(List<List<MultiColumnMap>> header) {
		this.header = header;
		this.row = header.size();
	}

	public List<MultiColumnMap> getDataColumn() {
		return dataColumn;
	}

	public void setDataColumn(List<MultiColumnMap> dataColumn) {
		this.dataColumn = dataColumn;
		this.col = dataColumn.size();
	}

	public List<List<MultiColumnMap>> getAddBottomCols() {
		return addBottomCols;
	}

	public void setAddBottomCols(List<List<MultiColumnMap>> addBottomCols) {
		this.addBottomCols = addBottomCols;
	}

//	public String getReplaceZero() {
//		return replaceZero;
//	}
//
//	public void setReplaceZero(String replaceZero) {
//		this.replaceZero = replaceZero;
//	}

	public String getReplaceEmpty() {
		return replaceEmpty;
	}

	public void setReplaceEmpty(String replaceEmpty) {
		this.replaceEmpty = replaceEmpty;
	}

}