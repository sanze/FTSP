var cancelFlag= false;
var timer;
var vars = {
		percent:0,
		finished:false
	};
//进度条定义
var processMessageconfig = {
	title:'进度条',
	msg:'当前进度',
	modal:true,
	width:300,
	buttons: Ext.Msg.CANCEL,
	fn:cancelOperation,
	progress:true		//progress为true updateProgress才会执行
};

////测试方法
//function test(){
//	Ext.Msg.show(processMessageconfig);
//	timer = setInterval(getProcessPersent,1000);
//}

//function xxx(){
//	var url = 'common!xxxx.action';
//	Ext.Ajax.request({
//        url: url,
//        method : 'POST', 
//        //处理ajax的返回数据
//        success: function(response, options){  
//        },  
//        failure: function(){  
//            Ext.Msg.alert('错误', '发生错误了！');
//        }
//    });
//}

function getProcessPersent(){
	var url = 'common!getProcessPercent.action?processKey='+processMessageconfig.processKey;
	Ext.Ajax.request({
            url: url,
            method : "POST",
            //处理ajax的返回数据
            success: function(response, options){
               var obj = Ext.decode(response.responseText);
               var processPercent = obj.processPercent>1?1:obj.processPercent;
	           //判断是否可以更新消息框
	           if(!cancelFlag && !vars.finished || processPercent==1){
	           		Ext.Msg.updateText(obj.text);
	           		Ext.Msg.updateProgress(processPercent, Math.ceil(processPercent*100)+"%");
	           }
	           if(processPercent == 1){
            	   clearTimer();
            	   Ext.Msg.hide();
            	   cancelFlag = false;
               }
            },  
            error:function(response) {
		    	clearTimer();
	    		Ext.Msg.alert('错误', '获取进度发生错误了！');
		    },
            failure: function(){
            	clearTimer();
                Ext.Msg.alert('错误', '获取进度发生错误了！');
            }
        });
}

function cancelOperation(button){
	cancelFlag = true;
	if(button == "cancel"){
		Ext.Msg.confirm('提示','确认取消？',function(btn){
			if(btn=='yes'){
				processMessageconfig.title = "正在取消";
				processMessageconfig.buttons = "";
				Ext.Msg.show(processMessageconfig);
				
				var url = 'common!cancelOperation.action?processKey='+processMessageconfig.processKey+'&t='+new Date();
				//终止进程
				Ext.Ajax.request({
				    url: url, 
				    method : "POST",
				    success: function(response) {
				    },
				    error:function(response) {
				    	clearTimer();
			    		Ext.Msg.hide();
			    		Ext.Msg.alert('错误', '取消进度发生错误了！');
				    },
				    failure:function(response) {
				    	clearTimer();
			    		Ext.Msg.hide();
			    		Ext.Msg.alert('错误', '取消进度发生错误了！');
				    }
				});
			}else{
				Ext.Msg.show(processMessageconfig);
				cancelFlag = false;
			}
		});
	}else{
		
    }
}

function clearTimer(){
	vars.finished = true;
	clearInterval(timer);
}

function showProcessBar(key){
	vars.finished = false;
	processMessageconfig.processKey = key;
	processMessageconfig.title = "进度条";
	processMessageconfig.buttons = Ext.Msg.CANCEL;
	Ext.Msg.show(processMessageconfig);
	timer = setInterval(getProcessPersent,5000);
	getProcessPersent();//初始化一次进度信息,否则若调用程序为初始化进度则cancel不了.
}
//Ext.onReady(function(){
//
////    var win = new Ext.Viewport({
////        id:'win',
////		layout : 'border',
////		items : []
////	});
//	Ext.Ajax.timeout=90000000; 
//    test();
//    xxx();
// });
