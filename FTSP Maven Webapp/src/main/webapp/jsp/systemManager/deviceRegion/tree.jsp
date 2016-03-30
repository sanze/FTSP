<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.fujitsu.common.CommonDefine;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta http-equiv="X-UA-Compatible" content="IE=8">
<title></title>
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
	<script type="text/javascript">
			var NodeDefine=new Object();
			NodeDefine.EMSGROUP=<%=CommonDefine.TREE.NODE.EMSGROUP%>;
			NodeDefine.EMS=<%=CommonDefine.TREE.NODE.EMS%>;
			NodeDefine.NE=<%=CommonDefine.TREE.NODE.NE%>;
			<%!
			public String getStringParameter(String text){
				return text==null?null:"\""+text+"\"";
			}
			%>
			var nodeIconCls=["icon-root","icon-emsgroup","icon-ems","icon-subnet","icon-ne",
			                 "icon-shelf","icon-unit","icon-subunit","icon-ptp"]
			rootId=<%=request.getParameter("rootId")%>;
			rootType=<%=request.getParameter("rootType")%>;
			rootText=<%=getStringParameter(request.getParameter("rootText"))%>;
			rootVisible=<%=request.getParameter("rootVisible")%>;
			leafType=<%=request.getParameter("leafType")%>;
			/*checkModel类型string可选值["cascade","single","multiple"] 级联/单选/多选*/
			checkModel=<%=getStringParameter(request.getParameter("checkModel"))%>;
			/*onlyLeafCheckable类型boolean 仅叶子可勾选*/
			<%--暂不支持此参数
			onlyLeafCheckable=<%=request.getParameter("onlyLeafCheckable")%>;
			--%>
			
			if((rootId==null)||(rootType==null)){
				rootId=<%=CommonDefine.TREE.ROOT_ID%>;
				rootType=<%=CommonDefine.TREE.NODE.ROOT%>;
				rootText=<%=getStringParameter(CommonDefine.TREE.ROOT_TEXT)%>;
			}
			rootVisible=(true==rootVisible);
			if(leafType==null){
				leafType=<%=CommonDefine.TREE.NODE.LEAFMAX%>;
			}
			if(["cascade","single","multiple"].indexOf(checkModel)==-1){
				checkModel="cascade";
			}
			<%--暂不支持此参数
			if(!Ext.isBoolean(onlyLeafCheckable)){
				onlyLeafCheckable=false;
			}
			--%>
    </script>
    <script type="text/javascript" src="tree.js"></script>
</head>
	<body>
	</body>
</html>
