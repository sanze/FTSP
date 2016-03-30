var speakId = -1;
function VoiceObj::EndStream()
{
	Beeper.playing = false;
	//避免重复延时播放
	if (speakId > 0) {
		clearTimeout(speakId);
	}
    speakId = setTimeout(Beeper.play, 3000);
};