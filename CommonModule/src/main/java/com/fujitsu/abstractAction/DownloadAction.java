package com.fujitsu.abstractAction;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	@Result(name = "download", type="stream", params = {
		"contentType", "application/octet-stream" ,
		"inputName", "inputStream",
		"contentDisposition", "attachment;filename=\"${fileName}\"",
		"bufferSize", "4096"}),
	
	@Result(name = "preview", type="stream", params = {
			"contentType", "${contentType}" ,
			"inputName", "inputStream",
			"contentDisposition", "inline;filename=\"${fileName}\"",
			"bufferSize", "4096"})
})
public abstract class DownloadAction extends AbstractAction {
	
	protected final static String RESULT_DOWNLOAD = "download";
	protected final static String RESULT_PREVIEW = "preview";
	
	protected String filePath;//相对项目根路径或绝对路径
	private String contentType;
    
	public String getFileName(){
		int leftIndex = filePath.lastIndexOf("/");
		int rightIndex = filePath.lastIndexOf("\\");
		String fileName=filePath.substring((leftIndex>rightIndex?leftIndex:rightIndex)+1);
		try {
			//2014-9-16 解决火狐下载乱码问题
			if("FF".equals(getBrowser())){
				return new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
			}else{
			return URLEncoder.encode(fileName,"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			return fileName;
		}
	}
	private String getBrowser(){
		HttpServletRequest request = ServletActionContext.getRequest();
	    String UserAgent = request.getHeader("USER-AGENT").toLowerCase();
	    if(UserAgent!=null){
	        if (UserAgent.indexOf("msie") >=0 ) return "IE";
	        if (UserAgent.indexOf("firefox") >= 0) return "FF";
	        if (UserAgent.indexOf("safari") >= 0) return "SF";
	    }
	    return null;
	}
	
    public void setFilePath(String filePath) {
    	if(!filePath.contains(":")&&!filePath.startsWith("/")&&
    		!filePath.startsWith("\\"))//相对路径必须以"/"开头
    		filePath = "/"+filePath;
    	//filePath.replaceAll("\\\\", "/");
    	this.filePath = filePath;
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
    	if(filePath.contains(":"))//绝对路径
        	return new java.io.FileInputStream(filePath);
    	else
    		return org.apache.struts2.ServletActionContext.getServletContext().getResourceAsStream(filePath);
    	
    }
    
    public String execute(){
    	return RESULT_DOWNLOAD;
    }
    
    public String preview(){
    	this.contentType = "application/pdf";
    	return RESULT_PREVIEW;
    }
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
