<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>权限域新增及修改</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/expand.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
     <!-- 不显示节点图标 -->
    <style type="text/css">
	    .x-tree-node-icon{
			display:none;
		}
	</style>
    <script type="text/javascript">
		var saveType="<%=request.getParameter("saveType")%>";
		var id="<%=request.getParameter("id")%>";
    </script>
    <script type="text/javascript" src="editAuthRegionAsynchr.js"></script>
    
  </head>
<body>
    <div id="form"></div>
    <div id="tree-div" style="overflow:hidden;height:350px;width:250px"></div>
</body>
</html>