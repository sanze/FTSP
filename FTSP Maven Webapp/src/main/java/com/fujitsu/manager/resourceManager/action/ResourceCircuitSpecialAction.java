package com.fujitsu.manager.resourceManager.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IResourceCircuitSpecialService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.handler.MessageHandler;

public class ResourceCircuitSpecialAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private File uploadFileNcResource;
	private String uploadFileNcResourceFileName;
	private int type;
	@Resource(name = "resourceCircuitSpecialImpl")
	IResourceCircuitSpecialService service;

	@IMethodLog(desc = "导入资源文件文件")
	public String importResourceFile() {
		try{
			boolean b = service.importResourceFile(uploadFileNcResourceFileName, uploadFileNcResource,type);
			Map map = new HashMap();
			map.put("success",b);
			resultObj = JSONObject.fromObject(map);
		}catch(CommonException e){
			result.setReturnResult(CommonDefine.FAILED);
			// result.setReturnMessage(e.getErrorMessage());
			result.setReturnMessage(MessageHandler
					.getErrorMessage(e.getErrorCode()));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_UPLOAD;
	}

	public File getUploadFileNcResource() {
		return uploadFileNcResource;
	}

	public void setUploadFileNcResource(File uploadFileNcResource) {
		this.uploadFileNcResource = uploadFileNcResource;
	}

	public String getUploadFileNcResourceFileName() {
		return uploadFileNcResourceFileName;
	}

	public void setUploadFileNcResourceFileName(String uploadFileNcResourceFileName) {
		this.uploadFileNcResourceFileName = uploadFileNcResourceFileName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
