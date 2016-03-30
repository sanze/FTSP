// Code in the BODY of the webpage is used to initialize controls and
// to handle SAPI events

/***** Initializer code *****/

function InitializeControls()
{
    // Initialize the Voices and AudioOutput Select boxes
    var VoicesToken = VoiceObj.GetVoices();
    var AudioOutputsToken = VoiceObj.GetAudioOutputs();
}

InitializeControls();

var speakId = null;

// Handle EndStream event   
function VoiceObj::EndStream()
{
	//避免重复延时播放
	if (speakId != null) {
		clearTimeout(speakId);
	}
    speakId = setTimeout(speakMsg, 15000);
};
/*
// Handle StartStream event 
function VoiceObj::StartStream()
{
    idbSpeakText.value = "停止";
}
*/