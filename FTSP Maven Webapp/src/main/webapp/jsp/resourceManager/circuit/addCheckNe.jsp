<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP3.0 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/expand.css" />
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/util.js"></script> 
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/sideText.js"></script>
    <script type="text/javascript">
   		
		saveType="<%=request.getParameter("saveType")%>";
		ne_id = "<%=request.getParameter("ne_id")%>";
		res_name = "<%=request.getParameter("res_name")%>";
		ftsp_name = "<%=request.getParameter("ftsp_name")%>";
    </script>
    <script type="text/javascript" src="addCheckNe.js"></script>
    
    <style type="text/css">
        .x-check-group-alt {
            background: #D1DDEF;
            border-top:1px dotted #B5B8C8;
            border-bottom:1px dotted #B5B8C8;
        }
    </style>
  </head>
<body>
    <div id="form-connectInfo"></div>
</body>
</html>