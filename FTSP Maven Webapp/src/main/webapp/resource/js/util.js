var isTopWindow = false;
var curTimer = -1;

//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓事件压缩器↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
// 鼠标移动事件由于频繁触发（移动一下可能触发20-80次）
// 所以必须把事件进行压缩处理，比如每秒最多触发一次
// 原理就是 延时触发事件的建立与取消+时间控制
/**
 * 事件压缩器
 * @author TianHongjun
 * @param method 需要压缩事件的函数
 * @param delay 延迟的时间
 * @param duration 超过这个时间限制的话就出发一次
 */
function eventZipper(method, delay, duration){
    var timer=null, begin=new Date();
    return function(){
        var me = this, args = arguments, current = new Date();
        //清除原来的延时事件
        clearTimeout(timer);
        
        if(current - begin >= duration){
        	//大于最小触发间隔则进行触发
            method.apply(me, args);
            begin = current;
        }else{
        	//否则 建立延时事件
            timer = setTimeout(function(){
                method.apply(me, args);
            }, delay);
            curTimer = timer;
        }
    };
}
//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑事件节流器结束↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓鼠标移动事件处理↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
/**
 * 保存实际的事件处理函数句柄
 */
var realHandler = null;
// 
/**
 * 一般窗口使用的动作检测<br>
 * 顶层窗口请调用 <b><i>setTopHandler()</i></b> 函数定向为 <i>topMotionHandler</i>
 */
function childMotionHandler(){
	//调用顶层窗口的mouse move事件处理函数
	if(!isTopWindow){
		try{
			top.mouseMoveHandler();			
		}catch(ex){
			
		}
	}
}

/**
 * 标记是否第一次运行<br>
 * 如果是第一次运行，则记录起始时间<br>
 * 否则刷新延时事件
 */
var last = null;
/**
 * 超时退出控制句柄
 */
var lockTimer = -1;
/**
 * Session保持控制句柄
 */
//var heartBeatTimer = -1;
/**
 * 退出实现函数
 */
function fLock(){
	isLocked = true;
	//Ext.getCmp("uidField").setValue("");
	Ext.getCmp("uid").setValue(userName);
	Ext.getCmp("pwd").setValue("");
	//通知服务器锁定session
	Ext.Ajax.request({
	    url : 'login!lock.action'
	});
	lockWin.show();
};
/**
 * 心跳函数
 */
function fKeepAlive(){
	Ext.Ajax.request({
	    url : 'login!hello.action',
	    success : function(response) {
	    },
	    failure : function(response) {
	    }
	});
}
/**
 * 锁定功能函数<br>
 * 当没有鼠标动作的时间超过 用户设定时间之后<br>
 * 调用fLock，实现锁定
 */
function topMotionHandler(){
	if(lockTime < 0 || isLocked){
		return;
	}else{
		if(lockTimer<0){
			lockTimer = setTimeout(fLock, lockTime);
		}else{
			clearTimeout(lockTimer);
			lockTimer = setTimeout(fLock, lockTime);
		}
	}
}

/**
 * mouse move事件处理函数，调用相应的事件处理函数<br>
 * 会去调用实际的事件处理函数
 */
function mouseMoveHandler(){
	realHandler();
}
//实际的事件处理函数 默认设置为childMotionHandler
realHandler = childMotionHandler;


/**
 * 顶层窗口 <b><i>必须</i></b> 调用此函数，
 * 用于重新设定顶层窗口函数。
 */
function setTopHandler(){
	realHandler = topMotionHandler;
}

//↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑鼠标移动事件处理↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

var lockWin = new Ext.Window({
    title: '超时锁定',
    id : 'lockWin71696ND1495626547494',
    layout : 'form',
    labelWidth:60,
    labelAlign:"left",
    modal : true,
    closable:false,
    plain:true,
    closeAction:'hide',
    defaultType: 'textfield',
    width: 250,
    height: 115,
    items : [{
		    fieldLabel: '用户名',
		    id:"uid",
            value:"",
            readOnly:true,
		    name: 'first',
		    emptyText:"请输入当前登录名。",
		    allowBlank:false
		},{
		    fieldLabel: '密码',
		    id:"pwd",
		    name: 'last',
		    inputType: 'password',
		    allowBlank:false
		}],
    buttons: [{
        scope:this,
        text: '解锁',
        handler: function(){
        	unlock();
        }
    },{
    	text: '退出登录',
        handler: function(){
        	Ext.Ajax.request({
				url : 'login!logout.action',
				method : 'POST',
				success : function(response) {
					window.open('../login/login.jsp',
									"_parent");
				},
				failure : function(response) {
					window.open('../login/login.jsp',
									"_parent");
				}
			});
        }
    }],
    buttonAlign:"center"
});

function unlock(){
        	var uid = Ext.getCmp("uid").getValue();
        	var pwd = Ext.getCmp("pwd").getValue();
        	if(uid == "" || pwd == ""){
        		Ext.Msg.alert("提示", "请输入用户名或密码！");
        		return;
        	}
        	Ext.Ajax.request({
        	    url : 'login!unlock.action',
        	    params:{
        	        userName:uid,
        	        password:pwd
        	    },
        	    success : function(response) {
        	        var obj = Ext.decode(response.responseText);
        	        if(obj.returnResult == 1 && Ext.getCmp("uid").getValue() == userName){
        	        	//如果解锁成功且是当前用户
        	        	isLocked = false;
        	        	Ext.getCmp("uidField").setValue("当前登录：" + displayName);
        	        	lockWin.hide();
        	        }else{
        	        	Ext.Msg.alert("提示", "用户名或者密码错误！");
        	        }
        	    },
        	    failure : function(response) {
        	        Ext.Msg.alert("提示", "用户名或者密码错误！");
        	    }
        	});
};

//处理键盘事件,当超时锁定的时候 禁止F5
function noF5(e) {
	var ev = e || window.event; //获取event对象 
    var obj = ev.target || ev.srcElement; //获取事件源 
    var type = obj.type || obj.getAttribute('type'); //获取事件源类型 
    var key = ev.keyCode || ev.which;
    //判断 事件目标是不是只读/被禁用
    var vReadOnly = !!obj.readOnly;
    var vDisabled = !!obj.disabled;
//	console.log(String.format("key<{0}> @ <{1}> -> ReadOnly【{2}】    Disabled【{3}】", key, type, vReadOnly, vDisabled));
	if (key == 116 && window.isLocked){
		return false;
	}else if (key == 8) {
		if (type != 'text' && type != 'textarea' && type != 'submit' && type != 'password'){
			return false;
		}else if(vReadOnly || vDisabled){
			return false;
		}
	}else if (key == 13) {
		if(isTopWindow && isLocked){
			unlock();
		}
	}
}
//鼠标移动/按键事件注册
(function(){
	//禁止F5 作用于Firefox、Opera
	//document.onkeypress = noF5;
	//禁止F5  作用于IE、Chrome
	document.onkeydown = noF5;
	
	// 鼠标移动时，如果触发间隔小于10ms，则延迟执行，但是至少每100ms执行一次。
	// 原本最小间隔是500ms，但是出现了stack overflow @ line0的bug，所以修改小时间间隔，谁要是能在10ms内移动并关闭页面那我也服了……
	//以防出现极端情况：某蛋疼人士登录之后就在看帮助...然后半小时过去了
	document.onmousemove = eventZipper(mouseMoveHandler, 10, 100);
	//修复隐藏bug:进入首页之后鼠标就不动，会导致超时锁定不出现，故而必须强制触发一次
	document.onmousemove();
})();

//心跳机制，保持session存活
(function dectectMain(){
	if(location.href.indexOf("main.jsp")>0 || (top == window) || (parent == window)){
		//10分钟一次心跳
		setInterval(fKeepAlive, 10*60*1000);
//		setInterval(fKeepAlive, 3*1000);
		isTopWindow = true;
		setTopHandler();
		//检测是否之前就锁定了
		Ext.onReady(function(){
//			console.log("dectectLock -> " + isLocked);
			//延时锁定，以防出现组件还没加载完的坑爹情况
			if(isLocked){
				fLock.defer(1000);
			}
		});
	}
})();
/////////////////////
function cunzaiganjun(){
//	var stt = location.href.indexOf("jsp", 1);
//	console.log("hey! look @ me~~~ " + location.href.substring(stt + 4));
	if(curTimer > 0){
		//试图解决 stack overflow @ line 0 的bug
		//console.log("存在感什么的最不好了~");
		clearTimeout(curTimer);
	}
/*	if(isTopWindow){
		//强插窗口时强制logout
		Ext.Ajax.request({
			url : 'login!logout.action',
			method : 'POST'
		});
	}*/
}
window.onunload = cunzaiganjun;

