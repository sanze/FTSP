Ext.Msg = top.Ext.Msg;
Ext.BLANK_IMAGE_URL = "../../resource/ext/resources/images/default/s.gif";
Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
var win = null;
var leftMenuArray = new Array();
// 图片panel
var imagePanel = new Ext.Panel({
			id : 'imagePanel',
			height : 80,
			html : '<img src="../../resource/images/otherImages/head.png" height=100% width=100%/>'
		});

// 工具栏panel
var toolbarPanel = new Ext.Toolbar({
	id : "toolbarPanel",
	bodyStyle : 'border-style:solid;border-color:#4F94CD'
});

// northPanel
var northPanel = new Ext.Panel({
	region : 'north',
	id : 'northPanel',
	height : 108,
	// margins: '0 3 0 0',
	// width:500,
	items : [imagePanel, toolbarPanel],
	split : false
});

// 页面导航条，暂不使用
var menuPathPanel = new Ext.Panel({
	id : 'menuPathPanel',
	frame : true,
	height : 27,
	margins : '0 0 0 0',
	style : 'padding:0 0 0 0',
	border : false,
	// collapsed:true,
	// margins: '0 0 0 5',
	split : false,
	bodyStyle : 'border-style:solid;border-color:#4F94CD'
});

// centerPanel
var centerPanel = new Ext.TabPanel({
	id : 'centerPanel',
	region : "center",
	// contentEl:'center',
	// frame:false,
	// collapsible: false,
	// border:false,
	// margins: '0 0 0 0',
	enableTabScroll : true,
	activeTab : 0,
	deferredRender : false,
	defaults : {
		autoScroll : true
	},
	plugins : new Ext.ux.TabCloseMenu(),
	bodyStyle : 'border-style:solid;border-color:#4F94CD',
	items : []
});

// eastPanel 暂不使用
var eastPanel = new Ext.Panel({
	id : 'eastPanel',
	region : "east",
	width : 230,
	collapsible : true,
	collapseMode : 'mini',
	contentEl : 'east',
	frame : false,
	animCollapse : true,
	collapsed : true,
	// margins: '0 0 0 5',
	split : false,
	bodyStyle : 'border-style:solid;border-color:#4F94CD'
});

// southPanel
var southPanel = new Ext.Panel({
	id : 'southPanel',
	region : "south",
	frame : true,
	split : false,
	// border:false,
	height : 25,
	margins : '0 0 0 0',
	bodyStyle : 'background-color:#eee;border-style:solid',
	html : '<p><center>版权所有：江苏富士通通信技术有限公司</center></p>',
	collapsible : false
});

// westPanel 暂不使用
var westPanel = new Ext.Panel({
	region : 'west',
	contentEl : 'west',
	collapsible : true,
	collapseMode : 'mini',
	width : 200,
	frame : false,
	animCollapse : true,
	// collapsed:true,
	// margins: '0 0 0 5',
	split : false,
	bodyStyle : 'border-style:solid;border-color:#4F94CD'
});

// 记录服务器与本机时间差
//var delta;

Ext.onReady(function() {
	Ext.Ajax.timeout=120000; 
	//登陆状态验
	initMenu();
	//loginStatusCheck();

	addTabPage("../main/centerPanel.jsp", "首页","",true);
		// Ext.getCmp("menuPathPanel").body.update("<font
		// size=2>您当前的位置：首页</font>");
	
	faultInfoControl();
});

//window.changeMenuPath = function(menuPath) {
//	Ext.getCmp("menuPathPanel").body.update("<font size=2>" + menuPath
//			+ "</font>");
//};

//检查当前设备面板图是否可以显示
function checkBayface(title){
	var len = centerPanel.items.length;
	var bayfaceCount = 0;

	for(var i=0;i<len;i++){
		if(centerPanel.get(i).id.search(/网元:/) != -1){
			bayfaceCount++;
		}
	}
	
	if(bayfaceCount >= 3){
		return false;
	}else{
		return true;
	}
}

// 添加tab页
window.addTabPage = function(url, title,authSequence,disableClose) {
	//判断是否含有disableClose这个参数，如果有不关闭，没有关闭
	var closable = true;
	if(disableClose){
		closable = false;
	}
	if(title.search(/网元:/) != -1){
		if(!checkBayface(title)) {
			Ext.Msg.alert("提示","只能同时打开3个网元面板图，请关闭其它网元面板图！");
			return;
		}
	}
	
	var iframeId = "f_" + title;
	centerPanel.remove(title);
	//加入权限字符串
	if(url){
		if(url.indexOf("?") != -1){
			url = url +"&authSequence="+authSequence;
		}else{
			url = url +"?authSequence="+authSequence;
		}
	}
	
	var tabPage = centerPanel.add({// 动态添加tab页
		id : title,
		visible : true,
		closable : closable,
		title : title,
		html : '<iframe id= \'' + iframeId + '\' src=\'' + url
		+ '\' frameborder="0" width="100%" height="100%"/>'
				
//		listeners:{
//			activate :function(tab){
//				 //刷新页面
//				 var iframe = window.frames[iframeId];
//				 if(iframe.initPage){
//					 iframe.initPage(authSequence);
//				 }
//			 }
//		}
	});
	centerPanel.setActiveTab(tabPage);// 设置当前tab页
//	centerPanel.doLayout();
	(function(){
		centerPanel.doLayout();
	}).defer(2000);
};
// 激活tab页
window.setActiveTab = function(tabId) {
	centerPanel.setActiveTab(tabId);
};
// 关闭tab页
window.closeTab = function(tabId) {
	centerPanel.remove(tabId);
};

// 取得当前活动页的tabId
window.getCurrentTabId = function() {
	return centerPanel.getActiveTab().id;
};

// 取得指定tab页
window.getTab = function(tabId) {
	return centerPanel.getItem(tabId);
};

function home() {
	addTabPage("../main/centerPanel.jsp", "首页");
};

function notSupport() {
	Ext.Msg.alert('错误', "此功能暂不支持！");
};

function logout() {
	Ext.Msg.confirm("确认", "确认要注销吗？", function(r) {
				if (r == "yes") {
					Ext.Ajax.request({
								url : 'login!logout.action',
								method : 'POST',
								success : function(response) {
									window
											.open('../login/login.jsp',
													"_parent");
								},
								failure : function(response) {
									window
											.open('../login/login.jsp',
													"_parent");
								}
							});
				};
			});
};
var hideBtn = new Ext.Button({
			text : '隐藏',
            enableToggle:true,
			icon : '../../resource/images/btnImages/up.png',
			handler : function() {
				if (!imagePanel.hidden) {
					imagePanel.hide();
					northPanel.setHeight(28);
                    southPanel.hide();
					win.doLayout();
                    hideBtn.setText("显示");
                    hideBtn.setIcon('../../resource/images/btnImages/down.png');
				} else {
					imagePanel.show();
                    southPanel.show();
					northPanel.setHeight(108);
					win.doLayout();
                    hideBtn.setText("隐藏");
                    hideBtn.setIcon('../../resource/images/btnImages/up.png');
				}
			}
		});
// 加载第一级目录
function initMenu() {
	// parentMenuId固定为0
	var path = 'menu!getSubMenuList.action?userId=' + userId + '&parentMenuId=0';
	Ext.Ajax.request({
				url : path,
				method : 'POST',
				success : function(response) {
					var responseArray = Ext.util.JSON
							.decode(response.responseText);
					for (var i = 0; i < responseArray.length; i++) {
						// 添加一级菜单
						addTopLevelMenu(responseArray[i].IS_LEAF == 0?false:true,
								responseArray[i].SYS_MENU_ID,
								responseArray[i].ICON_CLASS,
								responseArray[i].MENU_DISPLAY_NAME,
								i==responseArray.length-1);
						
					}
					// @Modified by thj
					// 添加登录，退出信息
					toolbarPanel.add('->', {
                                id:"uidField",
								xtype : 'displayfield',
								value : "当前登录：" + displayName
							}, {
								xtype : 'tbspacer',
								width : 10
							}, 
//							{
//								id : "currentTime",
//								xtype : 'displayfield'
//							}, 
							{
								xtype : 'tbspacer',
								width : 10
							}, {
								xtype : 'displayfield',
								html : "<a href='javascript:logout()'>" + "退出"
										+ "</a>"
							}, {
								xtype : 'tbspacer',
								width : 5
							},hideBtn);

					// 强制重新布局toolbar
					toolbarPanel.doLayout();
				},
				failure : function(response) {
//					Ext.Msg.alert("超时", response.responseText);
				}
			});
}

// 添加一级菜单目录
function addTopLevelMenu(isleaf, id, iconClass, text,end) {
	// 如果是叶子节点，不用添加menu属性
	if (isleaf) {
		toolbarPanel.add({
					id : id,
					iconCls : iconClass,
					text : text
				}, "-");
	}
	// 非叶子节点添加menu属性
	else {
		toolbarPanel.add({
					id : id,
					iconCls : iconClass,
					text : text,
					menu : {
						items : []
					}
				}, "-");
	}
	// 直接加载子菜单
	var menuButton = Ext.getCmp(id);
	if (menuButton.menu.items.length == 0) {
		getSubMenu(id,end);
	}
	menuButton.showMenu();
	// 添加单击监听，加载该节点下的菜单
	// menuButton.on('click', function(button,e){
	// // menuButton.on('mouseover', function(button,e){
	// if(button.menu.items.length == 0){
	// getSubMenu(id);
	// }
	// button.showMenu();
	// });

}

// 加载子菜单方法
function getSubMenu(parentMenuId,end) {
	var path = 'menu!getSubMenuList.action?userId=' + userId + '&parentMenuId='
			+ parentMenuId;
	Ext.Ajax.request({
		url : path,
		method : 'POST',
		success : function(response) {
			var responseArray = Ext.util.JSON.decode(response.responseText);
			// 移除父节点组件
			Ext.getCmp(parentMenuId).menu.removeAll();
			for (var i = 0; i < responseArray.length; i++) {
				// 找到需要添加节点的父节点添加菜单组件
				if (Ext.getCmp(responseArray[i].MENU_PARENT_ID).menu) {
					// 如果是叶子节点，不用添加menu属性
                    if (responseArray[i].MENU_DISPLAY_NAME=="-"){
                    	Ext.getCmp(responseArray[i].MENU_PARENT_ID).menu.add("-");
                    }
					else if (responseArray[i].IS_LEAF == 0?false:true) {
						Ext.getCmp(responseArray[i].MENU_PARENT_ID).menu
								.addItem(new Ext.menu.Item({
									id : responseArray[i].SYS_MENU_ID,
									text : responseArray[i].MENU_DISPLAY_NAME,
									href : responseArray[i].MENU_HREF,
									iconCls : responseArray[i].ICON_CLASS,
									disabled : responseArray[i].DISABLED,
									privilege:responseArray[i].AUTH_SEQUENCE,
									listeners : {
										'click' : function(item, e) {
											e.stopEvent();
											var authSequence = item.privilege;
											//if (item.getEl()) {
												var href = item.initialConfig["href"];
												if(href){
													if(href.indexOf("f:")>=0){
														var fn = href.substring(2);
														invoker(fn);
													}
													if(href.indexOf("ftts:")>=0){
														var jspDir = href.substring(5);
														invokerFTTS(jspDir,
																item.text,
																authSequence);
													}
													/*else if(href.indexOf("..")>=0){
														addTabPage(
															href,
															item.text,
															authSequence);
													}*/else{
														addTabPage(
																href,
																item.text,
																authSequence);
													}
												}
											//}
										}
									}
								}));
					}
					// 非叶子节点添加menu属性
					else {
						Ext.getCmp(responseArray[i].MENU_PARENT_ID).menu
								.addItem(new Ext.menu.Item({
											id : responseArray[i].SYS_MENU_ID,
											text : responseArray[i].MENU_DISPLAY_NAME,
											menu : {
												items : []
											},
											listeners : {
												'click' : function(item, e) {
													// e.stopEvent();
												}
											}
										}));
					}
				}
				var id = responseArray[i].SYS_MENU_ID;
				if(id == 4010000 || id == 5010000 || id == 6010000 || id == 6040000 || id == 6130000 || id == 10050000){
				  leftMenuArray.push(responseArray[i].MENU_DISPLAY_NAME);
				}
			}
			if(end){
			//alert("yuanshi"+leftMenuArray);
				showMainWin();
			}
			
			// toolbarPanel.doLayout();
		},
		failure : function(response) {
//			Ext.Msg.alert("超时", response.responseText);
		}
	});
}

function showMainWin(){
	win = new Ext.Viewport({
		id : "viewport",
		loadMask : true,
		autoScroll: true,
		minWidth:1000,
		minHeight:600,
		items : [{layout : 'border',items:[northPanel, centerPanel]}]
	});
	//处理最小宽高限制
	win.on('resize',function(obj,adjWidth,adjHeight,rawWidth,rawHeight){
		var resizeHeight=adjHeight>=obj.minHeight?adjHeight:obj.minHeight;
		obj.items.itemAt(0).setHeight(resizeHeight);
		var resizeWidth=adjWidth>=obj.minWidth?adjWidth:obj.minWidth;
		obj.items.itemAt(0).setWidth(resizeWidth);
	});
	win.fireEvent('resize',win,win.getWidth(),win.getHeight(),win.getWidth(),win.getHeight());
}

function invoker(fnName){
	eval("(" + fnName + "())");
}

function invokerFTTS(jspDir,text,authSequence){
	var href = window.location.protocol+"//"+window.location.host+"/"+jspDir;
	addTabPage(
			href,
			text,
			authSequence);
}

function help(){
	window.open("../help/help.htm");
}

function keyCustomer(){
	window.open("../../win8/jsp/win8.jsp");
}
