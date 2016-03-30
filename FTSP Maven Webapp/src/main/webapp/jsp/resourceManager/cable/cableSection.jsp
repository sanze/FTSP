<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <base/>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/expand.css" />
    
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all-debug.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/comboGrid.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    
     <link rel="stylesheet" type="text/css" href="../../../resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/TreeCheckNodeUI.css" />
    
    <script type="text/javascript" src="../../../resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="../../commonManager/areaTree.js"></script>
    <script type="text/javascript" src="../../commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script>
    <script type="text/javascript">      
        cablesId=<%=request.getParameter("cablesId")%>;  
        cableName="<%=request.getParameter("cableName")%>";  
        cableNo="<%=request.getParameter("cableNo")%>";  
    </script>
    <script type="text/javascript" src="cableSection.js"></script> 

  </head>
<body>
    <div id="form"></div>
</body>
</html>