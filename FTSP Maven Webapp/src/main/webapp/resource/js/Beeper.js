//方法：打开IE,
//然后选"工具->Internet选项–安全->自定义级别",
//启用"对未标记为可安全执行脚本的的ActiveX控制初始化并执行脚本"
var Beeper = {
		version:"",
		isIE:false,
		author:"田洪俊@JFTT",
		help:function(){
			var msg="Beeper类用法：\n" + 
			"1. 初始化：Beeper.init()\n"+
			"2. 清除数据：Beeper.clear()\n" + 
			"3. 添加告警：Beeper.add()\n" + 
			"4. 播放告警：Beeper.play()\n" + 
			"----------------------------------";
			if(!!window.console && !!console.log){
				console.log(msg);
			}else{
				alert(msg);
				alert(window.navigator.userAgent);
			}
		}
};
var VoiceObj = null;
if(Ext.isIE){
	try{
		VoiceObj = new ActiveXObject("Sapi.SpVoice");
        var VoicesToken = VoiceObj.GetVoices();
        var AudioOutputsToken = VoiceObj.GetAudioOutputs();
	}catch(exception){
		if(!!window.console && !!console.log){
			console.error("请开启\n工具->Internet选项–安全->自定义级别->对未标记为可安全执行脚本的的ActiveX控制初始化并执行脚本");
		}else{
			
		}
	}
	if(!!VoiceObj){
        var isTTSSupported = true;
    }
}
(function(){
	var alarmStrs = ["", "紧急告警", "重要告警", "次要告警", "提示告警","无告警"];
	var data = {alarmLevel:5,detail:{}};
	var curIdx = 0;
	function getAlarmMsg(){
		var idx = 0;
		var rv = "";
		for(var ems in data.detail){
			if(idx == curIdx){
				curIdx++;
				rv = ems + "_" + alarmStrs[data.detail[ems]];
//				console.log("Play->" + rv);
				return rv;
			}
			idx++;
		}
		curIdx = 0;
		return getAlarmMsg();
	}
	var href = location.href;
	var index = href.indexOf("jsp");
	var preUrl = href.substr(0, index);
	//console.log(preUrl);
	var suffix = Beeper.isIE ? "wav" : "mp3";
	var CR = preUrl + "resource/audio/CR." + suffix;
	var MJ = preUrl + "resource/audio/MJ." + suffix;
	var MN = preUrl + "resource/audio/MN." + suffix;
	var WR = preUrl + "resource/audio/WR." + suffix;
	var beeperEventJs = preUrl + "resource/js/BeeperEvent.js";
	Ext.Loader.load([beeperEventJs]);
	var alarmAudios = ["", CR, MJ, MN, WR,""];
	var urlChrome = '<audio id="xiaoyejie" src="%URL%" type=audio/mpeg hidden="true" autoplay = "autoplay" loop = "true"></audio>'
	Ext.apply(Beeper, {
		hasTTS:!!isTTSSupported,
		player:VoiceObj,
		playing:false,
	    canPlay:localStorage.getItem("playSound")==null?true:localStorage.getItem("playSound")=="true",
		init:function(){
			if(!isTTSSupported){
		        var player = document.createElement("div");
		        player.id = "bgAlarmPlayer" + ((Math.random()*1000000)>>0);
		        Beeper.id = player.id;
				var body = document.getElementsByTagName("body")[0];
				if(!body){
					console.error("请在Ext.onReady里面调用Beeper.init()");
					return;
				}
		        body.appendChild(player);
			}else{
			}
		},
		/**
		 * 清空数据，停止播放
		 */
		clear:function(){
			data = {alarmLevel:5,detail:{}};
			//停止播放
			if(isTTSSupported){
				if(speakId>0){
					clearTimeout(speakId);
				}
			}
		},
		/**
		 * 添加告警数据
		 * @param emsName ems名称
		 * @param lvl     告警等级
		 */
		add:function(emsName, lvl){
//			console.log("Beeper.add(" + emsName + ", " + lvl + ")")
			emsName = emsName || "";
			//计算最高告警
			//当不支持TTS/不是IE的时候会用到
			if(lvl>0){
				data.alarmLevel = Math.min(data.alarmLevel, lvl);
			}
			//添加告警详情
			if(emsName.length>0){
				if(!data.detail[emsName]){
					data.detail[emsName] = 5;
				}
				data.detail[emsName] = Math.min(data.detail[emsName], lvl);
			}
		},
		play:function(){
//			console.log("Beeper.play()");
			if(!Beeper.canPlay){
				return;
			}
			Beeper.playing = true;
			if(isTTSSupported){
				try{
					Beeper.player.Speak(getAlarmMsg());
				}catch(e){
					//TTS对象冲突！
				}
			}else{
				for(var ems in data.detail){
//					console.log("[" + ems + "] - <" + alarmStrs[data.detail[ems]] + ">")
				}
//				console.log(data.alarmLevel);
				var url = alarmAudios[data.alarmLevel];
				Beeper.playByTag(url);
			}
		},
	    /**
	     * 播放函数，请输入合适的文件地址~
	     * @param url 声音文件的地址
	     *            注意不同浏览器支持的文件格式也不一样
	     *                  IE 8    Chrome
	     *            Ogg           √
	     *            MP3   √       √
	     *            Wav   √                 
	     */
	    playByTag:function(url){
	    	url = url || "";
//			console.log("Beeper.playByTag(" + url + ")");
            var player = document.getElementById(Beeper.id);
	        if(url.length>0){
	            //console.log("playing -> " + url);
	            if(Ext.isIE){
	            	var bg = document.createElement("bgsound");
	            	bg.id = "xiaoyejie";
	            	bg.src = url;
	            	bg.loop = -1;
	            	player.appendChild(bg);
	            }else{
		            player.innerHTML = urlChrome.replace("%URL%", url);
	            }
	        }else{
	            if(Beeper.isIE){
	            	var bg = document.getElementById("xiaoyejie");
	            	if(!!bg){
	            		bg.remove();
	            	}
	            }else{
		            player.innerHTML = "";
	            }
	        }
	    },
	    toggle:function(){
	    	if(isTTSSupported && Beeper.canPlay){
	    		Beeper.play();
	    	}
	    	if(!isTTSSupported){
	    		if(Beeper.canPlay){
	    			Beeper.play();
	    		}else{
	    			Beeper.playByTag("");
	    		}
	    	}
	    }
	});
})();