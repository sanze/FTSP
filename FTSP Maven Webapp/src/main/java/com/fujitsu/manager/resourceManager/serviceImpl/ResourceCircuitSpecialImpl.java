package com.fujitsu.manager.resourceManager.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICircuitManagerService;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IResourceCircuitSpecialService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.dao.mysql.ResourceCircuitManagerMapper;
import com.fujitsu.manager.resourceManager.model.RecordModel;

@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceCircuitSpecialImpl implements
		IResourceCircuitSpecialService {
	// 存放文件上传路径
	private String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.NC_CIRCUIT_RESOURCE;
	@Resource
	private ICommonManagerService commonManagerService;
	@Resource
	private ResourceCircuitManagerMapper resourceMapper;
	@Resource(name = "circuitManagerServiceImpl")
	private ICircuitManagerService circuitService;
	@Resource
	private CircuitManagerMapper circuitManagerMapper;

	@Override
	public boolean importResourceFile(String fileName, File tempFile, int type)
			throws CommonException {
		try {
			File file = saveTempFile(tempFile, fileName);
			Workbook workBook = this.getWorkbook(file);
			Sheet sheet = workBook.getSheetAt(0);
			// 如果表中有记录,开始循环读取记录
			if (sheet.getPhysicalNumberOfRows() > 2) {
				// 从第二行开始读
				for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
					// 为record对象赋值
					RecordModel record = new RecordModel(sheet.getRow(i));
					System.out.println(record.getCircuitName()+"第"+(i+1)+"条");
					// 根据网元名称查询网元id
					List<Integer> neId = resourceMapper.getNeIdByName(record
							.getLocation().getNeName(), record.getLocation()
							.getEmsIp());
					if (neId.size() == 1) {
						record.setNeId(neId.get(0));
						// 根据电路名称搜索文件
						String circuitName = record.getCircuitName();
						// TODO如果文件不存在
						if (circuitName != null) {
							File file2 = this.getExcelFile(
									file.getParentFile(), circuitName);
							if (file2 != null) {
								// 读入第二份文件
								Workbook workBook2 = this.getWorkbook(file2);
								Sheet sheet2 = workBook2.getSheetAt(0);
								// 如果第二份文件中有数据,读取数据
								if (sheet2.getPhysicalNumberOfRows() > 1) {
									for (int j = 1; j < sheet2
											.getPhysicalNumberOfRows(); j++) {
										// 给ctp属性赋值,type标识是否需要改变ctp值——>j/k/l/m值的转化策略：0：不需要,1：需要
										record.changeRecord(sheet2.getRow(j),
												type);
										System.out.println("第"+(j+1)+"条");
										// 根据网元Id，槽道号，端口号，j/k/l/m值，获取结果ctp信息
										List<Map> ctpInfo = resourceMapper
												.getCtpId(record
														.getCtpQueryCondition());
										// 获取ctpId
										if (ctpInfo.size() == 1) {
											//
											HashMap map = new HashMap();
											map.put("start", 0);
											map.put("limit", 2000);
											map.put("nodes", ctpInfo.get(0)
													.get("BASE_SDH_CTP_ID"));
											map.put("nodeLevel", 9);
											map.put("serviceType", 1);
											circuitService
													.getAboutCircuitCon(map);
											List<Map> circuits = circuitManagerMapper
													.selectCircuitAbout(map);
											Map info = record.getUpdateInfo();
											for (int k = 0; k < circuits.size(); k++) {
												info.put("cirInfoId", circuits
														.get(k).get("CIR_CIRCUIT_INFO_ID"));
												resourceMapper
														.updateCircuitResource(info);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_ERROR);
		} catch (InvalidFormatException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_BIFF);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_IO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(e, MessageCodeDefine.COM_EXCPT_DB_OP);
		}
		return true;
	}

	/**
	 * 根据文件创建workbook 注意:这里跟poi提供的根据文件生成workbook对象有区别，这里仍将file转化成了输入流读入
	 * 以后若有内存优化方面的需要,可做修改！
	 * 
	 * @param file
	 *            需要读取的excel文件
	 * @return Workbook对象
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private Workbook getWorkbook(File file) throws IOException,
			InvalidFormatException {
		FileInputStream fs = new FileInputStream(file);
		Workbook workBook = this.getWorkbook(fs);
		return workBook;
	}

	/**
	 * 根据输入流创建workbook
	 * 
	 * @param is
	 *            输入流
	 * @return workbook
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private Workbook getWorkbook(FileInputStream fs) throws IOException,
			InvalidFormatException {
		Workbook workBook = null;
		try {
			workBook = WorkbookFactory.create(fs);
		} finally {
			fs.close();
		}
		return workBook;
	}

	/**
	 * 将上传的文件保存到服务器指定位置
	 * 
	 * @param tempFile
	 *            临时文件
	 * @param desFile
	 *            目标文件名称
	 * @return
	 * @throws IOException
	 * @throws CommonException
	 * @throws FileNotFoundException
	 */
	public File saveTempFile(File tempFile, String desName)
			throws FileNotFoundException, CommonException, IOException {
		commonManagerService.uploadFile(tempFile, desName, path);
		return new File(this.path + "/" + desName);
	}

	/**
	 * 获取指定路径中，指定名称的excel文件
	 * 
	 * @param file
	 * @param name
	 * @return
	 */
	private File getExcelFile(File file, String name) {
		File returnFile = null;
		File[] fileList = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String fileName) {
				if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
					return true;
				}
				return false;
			}
		});

		for (File temp : fileList) {
			if (apply(temp.getName(), name)) {
				returnFile = temp;
				break;
			}
		}
		return returnFile;
	}

	/**
	 * 
	 * @param fileName
	 *            文件名称
	 * @param reg
	 *            excel表中"电路名称"字段
	 * @return
	 */
	private boolean apply(String fileName, String reg) {
		String[] str = fileName.split("南昌");
		String name = null;
		for (String temp : str) {
			if (name == null) {
				name = temp;
			} else {
				name = name + temp;
			}
		}
		if (name.contains(reg)) {
			return true;
		}
		return false;
	}
}
