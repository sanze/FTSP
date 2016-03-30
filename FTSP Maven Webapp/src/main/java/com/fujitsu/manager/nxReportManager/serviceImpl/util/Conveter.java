package com.fujitsu.manager.nxReportManager.serviceImpl.util;
import com.fujitsu.common.CommonException;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


public class Conveter {
	private static String TOOL_SRC_XLS = "Excel";
	private static String TOOL_SRC_DOC = "Word";
	
    public static final int WORD_HTML = 8;
    public static final int WORD_TXT = 7;
    public static final int EXCEL_HTML = 44;  
    /**
     * 设置使用的转化工具
     * @param tool =wps时使用WPS进行文档转化
     * 			   =其他时使用Office套件转化
     */
    public static void setTool(String tool){
    	if(tool.toLowerCase().equals("wps_v9")){
    		TOOL_SRC_XLS = "Ket"; // wps的API升级从V8升级到V9，从et变成Ket
    		TOOL_SRC_DOC = "wps";
    	}else if(tool.toLowerCase().equals("wps_v8")){
    		TOOL_SRC_XLS = "et"; // wps的API升级从V8升级到V9，从et变成Ket
    		TOOL_SRC_DOC = "wps";
    	}else{
    		TOOL_SRC_XLS = "Excel";
    		TOOL_SRC_DOC = "Word";
    	}
    }
    /**  
     * WORD转HTML  
     * @param docfile WORD文件全路径  
     * @param htmlfile 转换后HTML存放路径  
     */  
    public static void w2h(String docfile, String htmlfile)   throws CommonException 
    {   
    	checkSoftware();
        ActiveXComponent app = new ActiveXComponent(TOOL_SRC_DOC + ".Application"); // 启动word   
        try  
        {   
            app.setProperty("Visible", new Variant(false));   
            Dispatch docs = app.getProperty("Documents").toDispatch();   
            Dispatch doc = Dispatch.invoke(   
                    docs,   
                    "Open",   
                    Dispatch.Method,   
                    new Object[] { docfile, new Variant(false),   
                            new Variant(true) }, new int[1]).toDispatch();   
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {   
                    htmlfile, new Variant(WORD_HTML) }, new int[1]);   
            Variant f = new Variant(false);   
            Dispatch.call(doc, "Close", f);   
        }   
        catch (Exception e)   
        {   
            e.printStackTrace();   
        }   
        finally  
        {   
            app.invoke("Quit", new Variant[] {});   
        }   
    }   
  
    /**  
     * EXCEL转HTML  
     * @param xlsfile EXCEL文件全路径  
     * @param htmlfile 转换后HTML存放路径  
     */  
    public static void e2h(String xlsfile, String htmlfile)    throws CommonException
    {   
    	ComThread.InitSTA(); 
//        ActiveXComponent app = new ActiveXComponent(TOOL_SRC_XLS + ".Application"); // 启动excel
    	ActiveXComponent app = getApplication();
        try  
        {   
            app.setProperty("Visible", new Variant(false));   
            Dispatch excels = app.getProperty("Workbooks").toDispatch();
            Dispatch excel = Dispatch.invoke(   
                    excels,   
                    "Open",   
                    Dispatch.Method,   
                    new Object[] { xlsfile, new Variant(false),   
                            new Variant(true) }, new int[1]).toDispatch();   
            Dispatch.invoke(excel, "SaveAs", Dispatch.Method, new Object[] {   
                    htmlfile, new Variant(EXCEL_HTML) }, new int[1]);   
            Variant f = new Variant(false);   
            Dispatch.call(excel, "Close", f);   
        }   
        catch (Exception e)   
        {   
            e.printStackTrace();   
        }   
        finally  
        {   
            app.invoke("Quit", new Variant[0]);   
            ComThread.Release();  
        }   
    }   
    @Deprecated
    private static void checkSoftware() throws CommonException{
    	try {
	    	ActiveXComponent app = new ActiveXComponent("Ket.Application");
	    	if(app != null){
	    		setTool("wps_v9");
	    	}
	    	app.invoke("Quit", new Variant[] {});   
            app = null;
		} catch (Exception e) {
			try{
				ActiveXComponent app = new ActiveXComponent("Excel.Application");
				if(app != null){
		    		setTool("office");
		    	}
				app.invoke("Quit", new Variant[] {});   
	            app = null;
			} catch (Exception e1){
				try{
					ActiveXComponent app = new ActiveXComponent("et.Application");
					if(app != null){
			    		setTool("wps_v8");
			    	}
					app.invoke("Quit", new Variant[] {});   
		            app = null;
					} catch (Exception e2){
		    		throw new CommonException(e1, -1,"请在服务器端安装Excel / WPS！");
					}
			}
		}
    }
    
    private static ActiveXComponent getApplication() throws CommonException{
    	try {
	    	ActiveXComponent app = new ActiveXComponent("Ket.Application");
	    	return app;
		} catch (Exception e) {
			try{
				ActiveXComponent app = new ActiveXComponent("Excel.Application");
				return app;
			} catch (Exception e1){
				try{
					ActiveXComponent app = new ActiveXComponent("et.Application");
					return app;
					} catch (Exception e2){
		    		throw new CommonException(e1, -1,"请在服务器端安装Excel / WPS！");
					}
			}
		}
    }
	public static void main(String argv[])
	{		
		try {
			setTool("wps");
			System.out.println("-------start------");
			Conveter.e2h("E:\\a.xlsx", "E:\\a.html");
//			Conveter.e2h("E:\\Workspace\\Work\\08.Others\\开发计划\\FTSP3.1开发计划V4.xlsx", "E:\\aaaasd.html");
			System.out.println("-------end------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
