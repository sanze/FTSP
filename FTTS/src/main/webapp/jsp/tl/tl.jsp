<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head><meta http-equiv="X-UA-Compatible">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>FTTS光纤连接图</title>
    <link rel="stylesheet" type="text/css" href="../../resource/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="../../resource/css/common.css"/>
    <link rel="stylesheet" media="screen, projection" href="../../resource/tl/css/ts_style.css" />
    
    <script type="text/javascript" src="../../resource/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../resource/ext/ext-all.js"></script>
    <script type="text/javascript" src="../../resource/expandExt/js/ext-lang-zh_CN.js"></script>
    
    <script type="text/javascript">
        var gStationId = <%=request.getParameter("stationId")%>;
    </script>
    
    <script src="../../resource/tl/TL.js"></script>
    <script src="main.js"></script>
    
</head>
<body>
    <div id="shell">
        <div id="header">
        <input type="checkbox" id="showHover" onchange="SHOW_HOVER = (this.checked)" checked=true style="display:none"/>
            <p id="title"></p>
        </div>
        
        <!-- scene用于显示 canvas 以及3个 ComboBox -->
        <div id="scene">
            <!-- <div id="lc"> </div> -->
            <!-- <div id="rc"> </div> -->
            <!-- <div id="mc"> </div> -->
            <canvas id="cvs_fg" style="display:block; position:absolute; top:34px"></canvas>
            <canvas id="cvs_bg" style="display:none;"></canvas>
        </div><!-- scene -->
        <!-- inspector用于显示一些debug信息 -->
        <div id="inspector" style="display:none">
        </div>
    </div><!-- shell -->
    
</body>
</html>