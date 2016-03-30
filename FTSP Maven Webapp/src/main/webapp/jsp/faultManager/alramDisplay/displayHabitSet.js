
/**
 * 告警颜色
 */
var alarmColor= {
	id : 'alarmColor',
	xtype : 'fieldset',
	title : '告警颜色',
	style : 'margin-left:40px;',
	bodyStyle : 'padding:20px 20px 20px 20px',
	height : 300,
	width : 500,
	labelWidth : 20,
	border : true,
	layout : 'form',
	items : [{
		layout : 'column',
		border : false,
		width:450,
		style : 'margin-bottom:10px',
		items : [{
			border : false,
			width:100,
			style: 'margin-left:110px;text-align:center',
			html : '<span>背景</span>'
		},{
			border : false,
			width:100,
			style: 'text-align:center',
			html : '<span>文字</span>'
		},{
			border : false,
			width:100,
			style: 'text-align:center',
			html : '<span>效果</span>'
		}]
	},{//紧急告警
		layout : 'column',
		border : false,
		width:450,
		style : 'margin-bottom:10px',
		items : [{
			border : false,
			width:120,
			style: 'text-align:center;padding-top:6px',
			html : '<span>紧急告警</span>'
		},{
			  id : 'urgencyBackGround',
			  xtype:'panel',
	          border:true,
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("urgencyBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,0,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'urgencyBackGround','urgency',1);
             }
		},{
			  id : 'urgencyCharacters',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;',
	          //bodyStyle: "background-color:'(RGB:255,255,255)';",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("urgencyCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,255,255)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'urgencyCharacters','urgency',2);
             }
		},{
			  id : 'urgencyEffect',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;text-align:center;line-height: 25px;',
	          //bodyStyle: "background-color:(255,0,0);",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	var north = Ext.getCmp("urgencyEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('urgencyCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('urgencyBackGround').body.getStyle('background-color')));
          	 }}
		}]
	},{//重要告警
		layout : 'column',
		border : false,
		width:450,
		style : 'margin-bottom:10px',
		items : [{
			border : false,
			width:120,
			style: 'text-align:center;padding-top:6px',
			html : '<span>重要告警</span>'
		},{
			  id : 'importBackGround',
			  xtype:'panel',
	          border:true,
	          //bodyStyle: "background-color:(255,128,0);",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("importBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,128,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'importBackGround','import',1);
             }
		},{
			  id : 'importCharacters',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;',
	          //bodyStyle: "background-color:#008000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("importCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,0,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'importCharacters','import',2);
             }
		},{
			  id : 'importEffect',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;text-align:center;line-height: 25px;',
	          width:80,
	          height:25,
	          //html:'<span style="color:green;">告警<span>',
	          listeners: {
	            render: function(panel) {
	            	var north = Ext.getCmp("importEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('importCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('importBackGround').body.getStyle('background-color')));
          	 }}
		}]
	},{//次要告警
		layout : 'column',
		border : false,
		width:450,
		style : 'margin-bottom:10px',
		items : [{
			border : false,
			width:120,
			style: 'text-align:center;padding-top:6px',
			html : '<span>次要告警</span>'
		},{
			  id : 'minorBackGround',
			  xtype:'panel',
	          border:true,
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("minorBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,255,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'minorBackGround','minor',1);
             }
		},{
			  id : 'minorCharacters',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;',
	          //bodyStyle: "background-color:#008000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("minorCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,0,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'minorCharacters','minor',2);
             }
		},{
			  id : 'minorEffect',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;text-align:center;line-height: 25px;',
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	var north = Ext.getCmp("minorEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('minorCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('minorBackGround').body.getStyle('background-color')));
          	 }}
		}]
	},{//提示告警
		layout : 'column',
		border : false,
		width:450,
		style : 'margin-bottom:10px',
		items : [{
			border : false,
			width:120,
			style: 'text-align:center;padding-top:6px',
			html : '<span>提示告警</span>'
		},{
			  id : 'promptBackGround',
			  xtype:'panel',
	          border:true,
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("promptBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(128,0,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'promptBackGround','prompt',1);
             }
		},{
			  id : 'promptCharacters',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;',
	          //bodyStyle: "background-color:#008000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("promptCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,0,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'promptCharacters','prompt',2);
             }
		},{
			  id : 'promptEffect',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;text-align:center;line-height: 25px;',
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          //html:'<span style="color:green;">告警<span>',
	          listeners: {
	            render: function(panel) {
	            	var north = Ext.getCmp("promptEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('promptCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('promptBackGround').body.getStyle('background-color')));
          	 }}
		}]
	},{//正常状态
		layout : 'column',
		border : false,
		width:450,
		style : 'margin-bottom:10px',
		items : [{
			border : false,
			width:120,
			style: 'text-align:center;padding-top:6px',
			html : '<span>正常状态</span>'
		},{
			  id : 'normalBackGround',
			  xtype:'panel',
	          border:true,
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("normalBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,255,0)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'normalBackGround','normal',1);
             }
		},{
			  id : 'normalCharacters',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;',
	          //bodyStyle: "background-color:#008000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	panel.el.on('click', this.onClick, this);
	            	Ext.getCmp("normalCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,255,255)'));
            	}},
             onClick: function onClick(event, target, options) {
            	 colorChoosePanel(event.getPageX(),event.getPageY(),'normalCharacters','normal',2);
             }
		},{
			  id : 'normalEffect',
			  xtype:'panel',
	          border:true,
	          style : 'margin-left:20px;text-align:center;line-height: 25px;',
	          //bodyStyle: "background-color:#FF0000;",
	          width:80,
	          height:25,
	          listeners: {
	            render: function(panel) {
	            	var north = Ext.getCmp("normalEffect").body;
	            	var html='<span style="margin-top:6px;color:'+colorRGBToHex(Ext.getCmp('normalCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('normalBackGround').body.getStyle('background-color')));
          	 }}
		}]
	},{
	    height:30,
        width: 150,
        style : 'margin-left:315px;',
    	border:false,
    	items :[{
    		privilege:viewAuth,
    		xtype: 'button',
    		text: "恢复为默认值",
            listeners: { "click": function () {
 	        	recoverDefaultValue();
 	        }}
    	}]
    }]
};

var changeColorCmpId;//需要改变颜色的组件id
var alarmSign;//表示哪个级别的告警
//弹出颜色选择面板
function colorChoosePanel(pageX,pageY,changeId,sign,flag){
	changeColorCmpId=changeId;
	alarmSign=sign;
	var params={};
	var uri;
	if(flag==1){
		uri="colorChoosePanel.jsp?"+Ext.urlEncode(params);
	}else{
		uri="colorChoosePanelAll.jsp?"+Ext.urlEncode(params);
	}
	var url ="<iframe  src =" + uri+ " height='100%' width='100%' frameBorder=0 border=0/>";
	var colorWindow=new Ext.Window({
        id:'colorWindow',
        title : '选择颜色',
		width : 200,
		height : 200,
		pageX :pageX,
		pageY :pageY+20,
        isTopContainer : true,
        modal : true,
        autoScroll:false,
		maximized:false,
        html:url
     });
	colorWindow.show();
}


function setCmpColorByChoose(choseColor){
	Ext.getCmp(changeColorCmpId).body.update('').setStyle('background-color', '#'+choseColor);
	var html='<span style="color:'+colorRGBToHex(Ext.getCmp(alarmSign+'Characters').body.getStyle('background-color'))+';">告警<span>';
	Ext.getCmp(alarmSign+'Effect').body.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp(alarmSign+'BackGround').body.getStyle('background-color')));
}

 

/**
 * RGB颜色转换为十六进制颜色
 * @param color 要转换的RGB颜色
 * @return 十六进制颜色
 */

function colorRGBToHex(color){
	var regexpRGB=/^(rgb|RGB)\([0-9]{1,3},\s?[0-9]{1,3},\s?[0-9]{1,3}\)$/;//RGB
	if(regexpRGB.test(color)){
		color=color.replace(/(\(|\)|rgb|RGB)*/g,"").split(",");
		var colorHex="#";
		for(var i=0;i<color.length;i++){
			var hex=Number(color[i]).toString(16);
			if(hex.length==1){
				hex="0"+hex;
			}
			colorHex+=hex;
		}
		return colorHex;
	}else{
		return color;
	}
}

/**
 * 告警灯显示
 */
var alarmLightShow = {
	id : 'alarmLightShow',
	xtype : 'fieldset',
	title : '告警灯显示',
	style : 'margin-left:20px;',
	bodyStyle : 'padding:20px 20px 20px 20px',
	height : 300,
	width : 300,
	layout : 'form',
	labelWidth : 1,
	items : [{
			layout : 'column',
			border : false,
			style : 'margin-top:30px;',
			items : [{
				border : false,
				width:100,
				style: 'text-align:center',
				html : '<span>紧急告警</span>'
			},{
				xtype: 'checkbox',
				style : 'margin-left:50px;',
				id:'urgencyAlarmLight',
				name: 'urgencyAlarmLight',
				fieldLabel: '',
				boxLabel: '',
				checked:true
			}]
    },{
    	layout : 'column',
		border : false,
		style : 'margin-top:15px;',
		items : [{
			border : false,
			width:100,
			style: 'text-align:center',
			html : '<span>重要告警</span>'
		},{
			xtype: 'checkbox',
			style : 'margin-left:50px;',
			id:'majorAlarmLight',
			name: 'majorAlarmLight',
			fieldLabel: '',
			boxLabel: '',
			checked:true
		}]
    },{
    	layout : 'column',
		border : false,
		style : 'margin-top:15px;',
		items : [{
			border : false,
			width:100,
			style: 'text-align:center',
			html : '<span>次要告警</span>'
		},{
			xtype: 'checkbox',
			style : 'margin-left:50px;',
			id:'minorAlarmLight',
			name: 'minorAlarmLight',
			fieldLabel: '',
			boxLabel: '',
			checked:true
		}]
    },{
    	layout : 'column',
		border : false,
		style : 'margin-top:15px;',
		items : [{
			border : false,
			width:100,
			style: 'text-align:center',
			html : '<span>提示告警</span>'
		},{
			xtype: 'checkbox',
			style : 'margin-left:50px;',
			id:'promptAlarmLight',
			name: 'promptAlarmLight',
			fieldLabel: '',
			boxLabel: '',
			checked:true
		}]
    },{
    	layout : 'column',
		border : false,
		style : 'margin-top:15px;',
		items : [{
			border : false,
			width:100,
			style: 'text-align:center',
			html : '<span>正常状态</span>'
		},{
			xtype: 'checkbox',
			style : 'margin-left:50px;',
			id:'normalAlarmLight',
			name: 'normalAlarmLight',
			fieldLabel: '',
			boxLabel: '',
			checked:true
		}]
    }]
};











//恢复为默认值
function recoverDefaultValue(){
	Ext.getCmp("urgencyBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,0,0)'));
	Ext.getCmp("urgencyCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,255,255)'));
	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('urgencyCharacters').body.getStyle('background-color'))+';">告警<span>';
	Ext.getCmp("urgencyEffect").body.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('urgencyBackGround').body.getStyle('background-color')));
	
	Ext.getCmp("importBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,128,0)'));
	Ext.getCmp("importCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,0,0)'));
	html='<span style="color:'+colorRGBToHex(Ext.getCmp('importCharacters').body.getStyle('background-color'))+';">告警<span>';
	Ext.getCmp("importEffect").body.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('importBackGround').body.getStyle('background-color')));
	
	Ext.getCmp("minorBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,255,0)'));
	Ext.getCmp("minorCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,0,0)'));
	html='<span style="color:'+colorRGBToHex(Ext.getCmp('minorCharacters').body.getStyle('background-color'))+';">告警<span>';
	Ext.getCmp("minorEffect").body.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('minorBackGround').body.getStyle('background-color')));

	Ext.getCmp("promptBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(128,0,0)'));
	Ext.getCmp("promptCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,0,0)'));
	html='<span style="color:'+colorRGBToHex(Ext.getCmp('promptCharacters').body.getStyle('background-color'))+';">告警<span>';
	Ext.getCmp("promptEffect").body.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('promptBackGround').body.getStyle('background-color')));

	Ext.getCmp("normalBackGround").body.update('').setStyle('background-color',colorRGBToHex('rgb(0,255,0)'));
	Ext.getCmp("normalCharacters").body.update('').setStyle('background-color',colorRGBToHex('rgb(255,255,255)'));
	html='<span style="color:'+colorRGBToHex(Ext.getCmp('normalCharacters').body.getStyle('background-color'))+';">告警<span>';
	Ext.getCmp("normalEffect").body.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('normalBackGround').body.getStyle('background-color')));


//	Ext.getCmp('urgencyAlarmLight').setValue(true);
//	Ext.getCmp('majorAlarmLight').setValue(true);
//	Ext.getCmp('minorAlarmLight').setValue(true);
//	Ext.getCmp('promptAlarmLight').setValue(true);
//	Ext.getCmp('normalAlarmLight').setValue(true);
}


//保存修改的数据
function saveData(){
	
	var alarmShift = Ext.getCmp('liftCycleRadio2').checked;
	// 告警转移设置
	//var alarmShiftStatus = alarmShift.get(0).items.get(0).checked;
	var alarmShiftOne = Ext.getCmp('alarmShiftOne').getValue();
	var alarmShiftSecond = Ext.getCmp('alarmShiftSecond').getValue();
	var json6=Ext.encode({'alarmShiftStatus':alarmShift,'alarmShiftOne':alarmShiftOne,'alarmShiftSecond':alarmShiftSecond,'flag':'shift'});
	
	var showDigital=1;//获取告警灯显示数字值
	var urgencyALight=1;//紧急告警 告警灯是否显示
	var majorALight=1;//重要告警 告警灯是否显示
	var minorALight=1;//次要告警 告警灯是否显示
	var promptALight=1;//提示告警 告警灯是否显示
	var normalALight=1;//正常状态告警灯是否显示
	var urgencyBackGroundCol;//紧急告警 背景颜色
	var urgencyCharacterCol;//紧急告警 字体颜色
	var majorBackGroundCol;//重要告警  背景颜色
	var majorCharacterCol;//重要告警  字体颜色
	var minorBackGroundCol;//次要告警  背景颜色
	var minorCharacterCol;//次要告警  字体颜色
	var promptBackGroundCol;//提示告警  背景颜色
	var promptCharacterCol;//提示告警  字体颜色
	var normalBackGroundCol;//正常状态  背景颜色
	var normalCharacterCol;//正常状态 字体颜色
	if(Ext.getCmp('urgencyAlarmLight').checked){
		urgencyALight=1;
	}else{
		urgencyALight=0;
	}
	if(Ext.getCmp('majorAlarmLight').checked){
		majorALight=1;
	}else{
		majorALight=0;
	}
	if(Ext.getCmp('minorAlarmLight').checked){
		minorALight=1;
	}else{
		minorALight=0;
	}
	if(Ext.getCmp('promptAlarmLight').checked){
		promptALight=1;
	}else{
		promptALight=0;
	}
	if(Ext.getCmp('normalAlarmLight').checked){
		normalALight=1;
	}else{
		normalALight=0;
	}
	urgencyBackGroundCol=colorRGBToHex(Ext.getCmp('urgencyBackGround').body.getStyle('background-color'));
	urgencyCharacterCol=colorRGBToHex(Ext.getCmp('urgencyCharacters').body.getStyle('background-color'));
	majorBackGroundCol=colorRGBToHex(Ext.getCmp('importBackGround').body.getStyle('background-color'));
	majorCharacterCol=colorRGBToHex(Ext.getCmp('importCharacters').body.getStyle('background-color'));
	minorBackGroundCol=colorRGBToHex(Ext.getCmp('minorBackGround').body.getStyle('background-color'));
	minorCharacterCol=colorRGBToHex(Ext.getCmp('minorCharacters').body.getStyle('background-color'));
	promptBackGroundCol=colorRGBToHex(Ext.getCmp('promptBackGround').body.getStyle('background-color'));
	promptCharacterCol=colorRGBToHex(Ext.getCmp('promptCharacters').body.getStyle('background-color'));
	normalBackGroundCol=colorRGBToHex(Ext.getCmp('normalBackGround').body.getStyle('background-color'));
	normalCharacterCol=colorRGBToHex(Ext.getCmp('normalCharacters').body.getStyle('background-color'));
	
	var list=[];
	var json1={'alarmLevel':'1','backgroudColor':urgencyBackGroundCol,'characterColor':urgencyCharacterCol,'alramLightShow':urgencyALight,'showDigital':showDigital};
	list.push(Ext.encode(json1));
	var json2={'alarmLevel':'2','backgroudColor':majorBackGroundCol,'characterColor':majorCharacterCol,'alramLightShow':majorALight,'showDigital':showDigital};
	list.push(Ext.encode(json2));
	var json3={'alarmLevel':'3','backgroudColor':minorBackGroundCol,'characterColor':minorCharacterCol,'alramLightShow':minorALight,'showDigital':showDigital};
	list.push(Ext.encode(json3));
	var json4={'alarmLevel':'4','backgroudColor':promptBackGroundCol,'characterColor':promptCharacterCol,'alramLightShow':promptALight,'showDigital':showDigital};
	list.push(Ext.encode(json4));
	var json5={'alarmLevel':'5','backgroudColor':normalBackGroundCol,'characterColor':normalCharacterCol,'alramLightShow':normalALight,'showDigital':showDigital};
	list.push(Ext.encode(json5));
	
	list.push(json6);
	saveDataToDatabase(list);
}

function saveDataToDatabase(list){
	Ext.Ajax.request({
	    url: 'alarm-habit-set-manage!setAlarmColorConfig.action', 
	    method : 'POST',
	    params: {// 请求参数
			"datas":list
			},
	    success: function(response) {
	    	top.Ext.getBody().unmask();
	    	var obj = Ext.decode(response.responseText);
			if(obj.success){//新增成功
				Ext.Msg.alert("提示",'设置成功');
			}else{
				Ext.Msg.alert("错误",obj.msg);
			}
	    },
	    error:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    },
	    failure:function(response) {
	    	top.Ext.getBody().unmask();
        	Ext.Msg.alert("错误",response.responseText);
	    }
	}); 	
}


//关闭
function close(){
	window.parent.centerPanel.remove(window.parent.centerPanel.getActiveTab().id);
}


//{
//	xtype: 'checkbox',
//	style : 'margin-left:50px;',
//	id:'majorAlarmLight',
//	name: 'majorAlarmLight',
//	fieldLabel: '',
//	boxLabel: '',
//	checked:true
//}

/**
 * 告警灯显示数字
 */
var alarmLightShowPanel = {
	id : 'alarmLightShowPanel',
	xtype : 'fieldset',
	title : '告警灯显示数字',
	style : 'margin-left:40px;',
	bodyStyle : 'padding:20px 20px 20px 20px',
	height : 100,
	width : 820,
	items : [{
		id:'alarmLightShowDigit',
		xtype: 'radiogroup',
		items:[
		       {
		           name: 'alarmLightShowDigit',
		           inputValue: '1',
		           boxLabel: '当前告警总数',
		           checked:true
		       }, {
		           name: 'alarmLightShowDigit',
		           inputValue: '2',
		           boxLabel: '未清除告警总数'
		       }
		       ]}
    ]
};


/**
 * 创建告警确认转移设置tab
 */
var alarmConfirmShift = {
	id : 'alarmShift',
	xtype : 'fieldset',
	title : '当前告警生命周期',
	style : 'margin-left:40px;',
	bodyStyle : 'padding:0 20px 20px 20px',
	height : 100,
	width : 820,
	items :[{
		layout : 'form',
		border:false,
		items:[{
			layout : 'column',
			border : false,
			items : [{
		     id:'liftCycleRadio1',
			 xtype : 'radio',
			 name:'liftCycle'
			},{
				border : false,
				style: 'text-align:center;margin-left:30px;margin-top:2px',
				html : '<span>无生命周期：已清除已确认告警会立即转移为历史告警。</span>'
			}]},{
			layout : 'column',
			border : false,
			style : 'margin-top:20px;',
			items : [
	        {
	        	id:'liftCycleRadio2',
				xtype : 'radio',
				name:'liftCycle',
				checked : false,
				fieldLabel:''
			},{
				border : false,
				//width:100,
				style: 'text-align:center;margin-left:30px;margin-top:2px',
				html : '<span>有生命周期：每</span>'
			},{
				id : 'alarmShiftOne',
				border : false,
				xtype : 'numberfield',
				width : 40,
				height:18,
				allowDecimals : false,//不允许输入小数 
				allowNegative : false,//不允许输入负数
				maxValue:48,//最大值                 
				maxText:'请输入1~48之间的整数',
				minValue:1,//最小值        
				minText:'请输入1~48之间的整数'
			},{
				border : false,
				//width:100,
				style: 'text-align:center;margin-top:2px',
				html : '<span>小时自动把已清除、已确认时间满</span>'
			},{
				id : 'alarmShiftSecond',
				border : false,
				xtype : 'numberfield',
				width : 40,
				height:18,
				allowDecimals : false,//不允许输入小数 
				allowNegative : false,//不允许输入负数
				maxValue:60,//最大值                 
				maxText:'请输入1~60之间的整数',
				minValue:1,//最小值        
				minText:'请输入1~60之间的整数'
			},{
				border : false,
				//width:100,
				style: 'text-align:center;margin-top:2px',
				html : '<span>分钟的告警转移到历史告警。</span>'
			}]}]
	}]
//	bbar : new Ext.Toolbar({
//		items : ['->',{
//			xtype : 'button',
//			text:'确定',
//			handler : function(){
//				saveAlarmShift();
//			}
//		}]
//	})
};

/**
 * 保存告警自动转移设置
 */
function saveAlarmShift(){
	var alarmShift = Ext.getCmp('alarmShift').items;

	// 告警转移设置
	var alarmShiftStatus = alarmShift.get(0).items.get(0).checked;
	var alarmShiftOne = Ext.getCmp('alarmShiftOne').getValue();
	var alarmShiftSecond = Ext.getCmp('alarmShiftSecond').getValue();
	if(!Ext.getCmp('alarmShift').getForm().isValid()){
		return false;
	}
	Ext.Ajax.request({
	    url: 'fault!modifyAlarmConfirmShift.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'alarmShiftStatus':alarmShiftStatus,'alarmShiftOne':alarmShiftOne,'alarmShiftSecond':alarmShiftSecond,'flag':'shift'})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	if (obj.returnResult == 0) {
				Ext.Msg.alert("信息", obj.returnMessage);
			}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	})
}

/**
 * 初始化告警自动转移时间设置
 */
function initAlarmConfirmShift(){
	Ext.Ajax.request({
	    url: 'fault!getAlarmConfirmShift.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'flag':'shift'})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	// 告警转移
	    	var alarmShift = obj.shift.PARAM_VALUE.split(',');
	    	if(alarmShift[0]=='true'){
	    		Ext.getCmp('liftCycleRadio2').setValue(true);
	    	}else{
	    		Ext.getCmp('liftCycleRadio1').setValue(true);
	    	}
	    	Ext.getCmp('alarmShiftOne').setValue(alarmShift[1]);
	    	Ext.getCmp('alarmShiftSecond').setValue(alarmShift[2]);
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	})
}

//var tabs = new Ext.TabPanel({      
//	region : 'center',
//    activeTab: 0,//初始显示第几个Tab页      
//    deferredRender: false,//是否在显示每个标签的时候再渲染标签中的内容.默认true      
//    tabPosition: 'top',//表示TabPanel头显示的位置,只有两个值top和bottom.默认是top.      
//    enableTabScroll: true,//当Tab标签过多时,出现滚动条      
//    items: [{     
//       layout : 'fit',
//       title: '告警显示习惯设置',      
//       items: centerPanel
//    },{     
//        layout : 'fit',
//        title: '选项',      
//        items: alarmConfirmShift,
//        listeners : {
//        	activate : function(tab){
//        		initAlarmConfirmShift();
//        	}
//        }
//     }]     
//});   


/**
 * 初始化告警自动转移时间设置
 */
function initAlarmColor(){
	Ext.Ajax.request({
	    url: 'alarm-habit-set-manage!getAlarmColorInit.action',
	    method: 'POST',
	    params: {'jsonString':Ext.encode({'flag':'color'})},
	    success : function(response) {
	    	var obj = Ext.decode(response.responseText);
	    	var arr=obj.result;
	    	if(arr==undefined || arr==null){
	    		return;
	    	}
	    	for(var i=0;i<arr.length;i++){
	    		if(arr[i].alarm_type==1){
	    			Ext.getCmp("urgencyBackGround").body.update('').setStyle('background-color',arr[i].color_code);
	    			Ext.getCmp("urgencyCharacters").body.update('').setStyle('background-color',arr[i].character_color_code);
	    			var north = Ext.getCmp("urgencyEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('urgencyCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('urgencyBackGround').body.getStyle('background-color')));
	    			if(arr[i].display_flag==1){
	    				Ext.getCmp('urgencyAlarmLight').setValue(true);
	    			}else{
	    				Ext.getCmp('urgencyAlarmLight').setValue(false);
	    			}
	    		}else if(arr[i].alarm_type==2){
	    		 	Ext.getCmp("importBackGround").body.update('').setStyle('background-color',arr[i].color_code);
	            	Ext.getCmp("importCharacters").body.update('').setStyle('background-color',arr[i].character_color_code);
	            	var north = Ext.getCmp("importEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('importCharacters').body.getStyle('background-color'))+';">告警<span>';
	            	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('importBackGround').body.getStyle('background-color')));
	            	if(arr[i].display_flag==1){
	    				Ext.getCmp('majorAlarmLight').setValue(true);
	    			}else{
	    				Ext.getCmp('majorAlarmLight').setValue(false);
	    			}
	    		}else if(arr[i].alarm_type==3){
	    		 	Ext.getCmp("minorBackGround").body.update('').setStyle('background-color',arr[i].color_code);
	            	Ext.getCmp("minorCharacters").body.update('').setStyle('background-color',arr[i].character_color_code);
	            	var north = Ext.getCmp("minorEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('minorCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('minorBackGround').body.getStyle('background-color')));
	            	if(arr[i].display_flag==1){
	    				Ext.getCmp('minorAlarmLight').setValue(true);
	    			}else{
	    				Ext.getCmp('minorAlarmLight').setValue(false);
	    			}
	    		}else if(arr[i].alarm_type==4){
	    	     	Ext.getCmp("promptBackGround").body.update('').setStyle('background-color',arr[i].color_code);
	            	Ext.getCmp("promptCharacters").body.update('').setStyle('background-color',arr[i].character_color_code);
	              	var north = Ext.getCmp("promptEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('promptCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('promptBackGround').body.getStyle('background-color')));
	            	if(arr[i].display_flag==1){
	    				Ext.getCmp('promptAlarmLight').setValue(true);
	    			}else{
	    				Ext.getCmp('promptAlarmLight').setValue(false);
	    			}
	    		}else if(arr[i].alarm_type==5){
	    		 	Ext.getCmp("normalBackGround").body.update('').setStyle('background-color',arr[i].color_code);
	            	Ext.getCmp("normalCharacters").body.update('').setStyle('background-color',arr[i].character_color_code);
	            	var north = Ext.getCmp("normalEffect").body;
	            	var html='<span style="color:'+colorRGBToHex(Ext.getCmp('normalCharacters').body.getStyle('background-color'))+';">告警<span>';
		          	north.update(html).setStyle('background-color',colorRGBToHex(Ext.getCmp('normalBackGround').body.getStyle('background-color')));
	            	if(arr[i].display_flag==1){
	    				Ext.getCmp('normalAlarmLight').setValue(true);
	    			}else{
	    				Ext.getCmp('normalAlarmLight').setValue(false);
	    			}
	    		}
	    	}
		},
		error : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		},
		failure : function(response) {
			top.Ext.getBody().unmask();
			Ext.Msg.alert("错误", response.responseText);
		}
	})
}

/**
 * 创建border布局的center部分
 */
var centerPanel = new Ext.FormPanel({
	region : 'center',
	autoScroll : true,
	items : [{
		layout : 'form',
		border : false,
		items : [{
			layout : 'column',
			border : false,
			style : 'margin-top:10px;',
			items : [{
				layout : 'form',
				border : false,
				items : alarmColor
			},{
				layout : 'form',
				border : false,
				items :  alarmLightShow
			}]
		},{
			layout : 'form',
			border : false,
			style : 'margin-top:10px',
			items : alarmConfirmShift
		},{
			layout : 'column',
			style : 'margin-left:600px;',
			border:false,
			width : 820,
			items:[{
				xtype: 'button',
				width:60,
				privilege:modAuth,
				text : '确定',
				handler : saveData
			},{
				xtype: 'button',
				width:60,
				style : 'margin-left:20px;',
				privilege:viewAuth,
				text : '取消',
				handler : close
			}]
		}]
	}]
});


Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	initAlarmConfirmShift();
 	
 	//初始化颜色
 	initAlarmColor();
  	new Ext.Viewport({
        layout : 'border',
        autoScroll : true,
        items : centerPanel
	});
});
