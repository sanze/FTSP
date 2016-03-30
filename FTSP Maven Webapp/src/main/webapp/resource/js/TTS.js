var DEBUG_TTS = false;
/**
 * TTS引擎设计：
 * 1. 按网管分组推送告警信息，当前在播放的，则加入播放列表缓存延迟播放
 * 2. 播放列表按告警等级高低排序，同样等级的先插先放
 * 3. 当Beeper对象存在&&为IE时，覆盖Beeper对象的play方法
 */
var TTS = {
    isIE:undefined,
	player:undefined,
	ready : false,
    canPlay:true,//localStorage.getItem("playSound")==null?true:localStorage.getItem("playSound")=="true",
    playing:false,
    list:[],
    /**
     * 初始化函数，进行浏览器判断，播放器标签插入等工作~
     */
    init:function(){
        var ua = navigator.userAgent.toLowerCase();
        function test(r){
            return r.test(ua);
        };
        this.isIE = !test(/opera/) && test(/msie/);
        //仅在IE环境下进行TTS初始化
        if(this.isIE){
        	this.initTTS();
	        //如果有Beeper对象，则覆盖Beeper对象的play方法
	        if(this.Beeper){
	        	Beeper.play = this.play();
	        }
        }
    },
    /**
     * 播放函数，请输入合适的文件地址~  
     * @param	ems	网管名称
	 * @param	lvl	告警级别
     */
    play:function(ems, lvl){
    	lvl = lvl || 4;
    	var alarmLvls = ["紧急告警", "重要告警", "次要告警", "提示告警"];
    	var txt = ems + alarmLvls[lvl];
    	this.speak(txt);
    },
    /**
     * 播放函数，请输入合适的文件地址~  
     * @param   txt  需要播放的文本  
     */
    speak:function(txt){
    	if(!!txt && txt.length>0){
    		//内容有效长度>=1时，进行播放处理
	    	this.list.push({text:txt, level:5});
	    	//按告警等级排序，0最高，3最低
	    	this.list.sort(function(a, b){  
	              return a.level - b.level;  
	        });
	    	showPlayList();
	        if(this.canPlay && !this.playing){
	        	var obj = this.list.splice(0, 1)[0];
	        	this.playing = true;
	        	this.player.Speak(obj.text);
	        }
	        showPlayList();
    	}
    },
    /**
     * 当前播放开始之后执行
     */
    playStarted:function(){
    	//有特殊需要可在此进行修改
    },
    /**
     * 当前播放结束之后执行
     * 如果列表不为空，则继续播放
     */
    playFinished:function(){
    	if(this.list.length > 0){
    		//截取播放列表的第一个元素
    		var obj = this.list.splice(0, 1)[0];
        	this.playing = true;
        	//要播放的内容之前加入“，，，，，，，，，，，，，，，，”产生延迟效果
        	this.player.Speak("，，，，，，，，，" + obj.text);
    	}
    },
	//private
	//初始化TTS引擎，仅在IE下有效
	initTTS:function(){
        this.player = new ActiveXObject("Sapi.SpVoice");
        if(!!this.player){
    		//仅IE支持此语法,某些版本IE不支持
        	this.player::StartStream = this.playStarted;
        	this.player::EndStream = this.playFinished;
        }else{
        	
        }
	}
};
/**
 * 测试用，打印播放清单
 **/
function showPlayList(){
	for(var i=0; DEBUG_TTS && i<TTS.list.length; i++){
		console.log("#"+(i+1) + "\t@lvl " + TTS.list[i].level + "\t" + TTS.list[i].text);
	}
}
(function(){
	if(DEBUG_TTS){
		TTS.init();
		var strs = ["啊深V高污染", "和叫撒子班", "杨添茸那帮人", "在东北角你可别", "上日银行间嘚瑟", "不能用NES热啊", "FTSP", "hello", "黑腻害", "干宁真夏燥"];
		for(var i=0;i<10;i++){
			TTS.play(strs[i], Math.floor(Math.random()*6));
		}
	}
})();
//方便IE下的调试
top.tts = TTS;