<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><meta http-equiv="X-UA-Compatible" content="IE=8">
    <base/>
    <title>FTSP 传输网络维护支撑平台</title>  
    <link rel="stylesheet" type="text/css" href="../../../resource/ext/resources/css/ext-all.css" />
    
    <script type="text/javascript" src="../../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/combo.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/timeComponent/WdatePicker.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/comboGrid.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/sideText.js"></script>
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/LockingGridView.css" />
 
	<link rel="stylesheet" type="text/css" href="../../../resource/css/common.css" />
    <link rel="stylesheet" type="text/css" href="../../../resource/expandExt/css/TreeCheckNodeUI.css" />
    <script type="text/javascript" src="../../../resource/expandExt/js/TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="../../commonManager/areaTree.js"></script>
    <script type="text/javascript" src="../../../resource/expandExt/js/mySessionProvider.js"></script>  
    <script type="text/javascript" src="../../../resource/js/util.js"></script>   
	<script type="text/javascript" src="../../../resource/expandExt/ux/LockingGridView.js"></script>
	<script type="text/javascript" src="../../commonManager/commonAuthDomian.js"></script>
    <script type="text/javascript">
       userId="<%=session.getAttribute("SYS_USER_ID")%>";
    </script>
	<script type="text/javascript" src="ODFfunc.js"></script>
    <script type="text/javascript" src="ODFManager.js"></script>

   </head>
    <style type="text/css">
        .x-check-group-alt {
            background: #D1DDEF;
            border-top:1px dotted #B5B8C8;
            border-bottom:1px dotted #B5B8C8;
        }
    </style>
<body>
    <div id="form"></div>
</body>
</html>