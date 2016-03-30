<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>报表树</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
   
    <script type="text/javascript">
		<%!
		public String getStringParameter(String text){
			return text==null?null:"\""+text+"\"";
		}
		%>
		var saveType="<%=request.getParameter("saveType")%>";
		var id="<%=request.getParameter("id")%>";
		var leafType=<%=CommonDefine.TREE.NODE.EMS%>;
		var rootId=<%=request.getParameter("rootId")%>;
		var rootType=<%=request.getParameter("rootType")%>;
		var rootText=<%=getStringParameter(request.getParameter("rootText"))%>;
		
		
		treeParams={};
		treeParams.leafType=2;
		if(rootId==-1){
			treeParams.rootVisible=false;
			treeParams.rootId=0;
    		treeParams.rootType=rootType;
			treeurl="../common/tree.jsp?"+Ext.urlEncode(treeParams);
		}else{
			treeParams.rootVisible=true;
			treeParams.rootId=rootId;
    		treeParams.rootType=rootType;
    		treeurl="../../commonManager/tree.jsp?"+Ext.urlEncode(treeParams);
		}
		
    </script>
       <script type="text/javascript">
		
    </script>
    <script type="text/javascript" src="reportTreeCommon.js"></script>
    
  </head>
<body>
    <div id="tree-div" style="height:350px;width:250px;overflow: hidden;"></div>
</body>
</html>