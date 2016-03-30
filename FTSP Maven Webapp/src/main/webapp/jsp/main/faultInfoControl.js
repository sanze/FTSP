

function faultInfoControl(){
	var drag = document.getElementById('drag');
	
	if(document.attachEvent){
		drag.attachEvent('onmousedown',dragHandle);
	}else{
		drag.addEventListener('mousedown', dragHandle,false);
	}
	function dragHandle(event){
		var event = event||window.event;
		var startX = drag.offsetLeft;
		var startY = drag.offsetTop;
		var mouseX = event.clientX;
		var mouseY = event.clientY;
		var deltaX = mouseX - startX;
		var deltaY = mouseY - startY;
		if(document.attachEvent){
			drag.attachEvent('onmousemove',moveHandle);
			drag.attachEvent('onmouseup',upHandle);
			drag.attachEvent('onlosecapture',upHandle);
			drag.setCapture();
		}else{
			document.addEventListener('mousemove',moveHandle,true);
			document.addEventListener('mouseup',upHandle,true);
		}
		function moveHandle(event){
			var event = event||window.event;
			drag.style.left = (event.clientX - deltaX)+"px";
			drag.style.top = (event.clientY - deltaY)+"px";
		}
		function upHandle(){
			if(document.attachEvent){
				drag.detachEvent('onmousemove',moveHandle);
				drag.detachEvent('onmouseup',upHandle);
				drag.detachEvent('onlosecapture',upHandle);
				drag.releaseCapture();
			}else{
				document.removeEventListener('mousemove',moveHandle,true);
				document.removeEventListener('mouseup',upHandle,true);
			}
		}
	}
	
	if(VoiceObj != null) {
		Ext.Loader.load(['speak1.js']);
	}
	
	addFaultEventListener();
	getFaultInfoForFP();
}

function getFaultInfoForFP(){
	
	Ext.Ajax.request({
		url : 'fault-management!getFaultInfoForFP.action',
		method : 'POST',
		success : function(response) {
			Ext.getBody().unmask();
			var obj = Ext.decode(response.responseText);
			if (obj.returnResult == 0) {
				Ext.Msg.alert("提示", obj.returnMessage);
			}
			if (obj.returnResult == 1) {
				if(obj.returnMessage != ""){
					document.getElementById("drag").style.display = "inline";
					var original = document.getElementById("FaultMessageBox").innerHTML;
					if(original != obj.returnMessage){
						document.getElementById("FaultMessageBox").innerHTML = obj.returnMessage;
					}
				}else{
					document.getElementById("drag").style.display = "none";
				}
			};
		},
		failue : function(response) {
			Ext.getBody().unmask();
			Ext.Msg.alert('错误', '访问服务器失败！');
		}
	});
}

function addFaultEventListener() {
	var obj = document.getElementById("FaultMessageBox");
	if(window.addEventListener)
		obj.addEventListener("onpropertychange", speakControl, false);
	else if (window.attachEvent)
		obj.attachEvent("onpropertychange", speakControl);
	else
		obj.onpropertychange = speakControl;
}

function speakControl() {
	var obj = document.getElementById("FaultMessageBox");
	
	try {
		var msg = obj.innerText;
		if (msg.length > 0 && VoiceObj != null){
			VoiceObj.Speak(msg, 1);
		}
		
		if(msg != ""){
			document.getElementById("drag").style.display = "inline";
		}else{
			document.getElementById("drag").style.display = "none";
			if(VoiceObj != null){
				VoiceObj.Speak("", 2);
			}
		}
	}
	catch (exception) {
		alert("客户端未能正常发声，请检查声音输出设备！");
	}
}

function speakMsg() {
	var obj = document.getElementById("FaultMessageBox");
	try {
		var msg = obj.innerText;
		if (msg.length > 0 && VoiceObj != null){
			VoiceObj.Speak(msg, 1);
		}
	}
	catch (exception) {
		alert("客户端未能正常发声，请检查声音输出设备！");
	}
}
