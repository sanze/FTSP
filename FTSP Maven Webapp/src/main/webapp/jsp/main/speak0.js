
var VoiceObj = null;

// Create the Sapi SpVoice object
(function createVoiceObj(){
	
	try{
		VoiceObj = new ActiveXObject("Sapi.SpVoice");
	}catch(exception){
		alert("故障提示音被禁止，如需开启请调整浏览器的安全设置！");
	}
	
})();
