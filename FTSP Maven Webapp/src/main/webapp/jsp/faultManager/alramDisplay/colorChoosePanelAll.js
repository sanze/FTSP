

var choseColor='';

//定义颜色板
var cp = new Ext.ColorPalette();

/**
 * 创建border布局的center部分
 */
var centerPanel = new Ext.FormPanel({
	width:180,
	height:150,
	items : cp,
	border:false,
	buttonAlign : 'center', 
	buttons : [{
		text : '确定',
		width:80,
		handler : sureChoseColor
	},{
		text : '取消',
		width:80,
		handler : close
	}]
});


function sureChoseColor(){
	if(choseColor==null || choseColor==''){
		Ext.Msg.alert("提示","请选择颜色!");
		return;
	}
	parent.setCmpColorByChoose(choseColor);
	close();
}

function close(){
	choseColor='';
	var win = parent.Ext.getCmp('colorWindow');
	if(win){
		win.close();
	}
}


Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'title';
 	Ext.BLANK_IMAGE_URL="../../../resource/ext/resources/images/default/s.gif";
 	document.onmousedown=function(){top.Ext.menu.MenuMgr.hideAll();};
 	Ext.Msg = top.Ext.Msg; 
 	cp.on('select', function(p,v){   
    	choseColor=v;
    });
  	new Ext.Viewport({
        layout : 'form',
        items : centerPanel
	});
 });
