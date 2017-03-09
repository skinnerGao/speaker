package com.ihappy.ttl_s;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class TtsService extends Service {

	private android.speech.tts.TextToSpeech tts;
	private SpeechSynthesizer mTts;
	private String mEngineType;
	private MediaPlayer mMediaPlayer;
	private String name;

	@Override
	public IBinder onBind(Intent intent) {
		return new MsgBinder();
	}

	public class MsgBinder extends Binder {
		public TtsService getService() {
			return TtsService.this;
		}
	}

	public void playByGoogle(String content, float ttsPithch, float ttsSpeed) {
		tts.setSpeechRate(ttsSpeed);
		tts.setPitch(ttsPithch);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, content);
		tts.speak(content, android.speech.tts.TextToSpeech.QUEUE_FLUSH, params);
		Log.i("ttsTest", content);
	}

	public void playByIbm(String content, String sex) {
		try {
			String voiceName = "en-US_LisaVoice";
			if (sex.equals("men")) {
				voiceName = "en-US_MichaelVoice";
			}
			String serviceURL = "https://stream.watsonplatform.net/text-to-speech/api";
			com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance()
					.initWithContext(new URI(serviceURL));
			com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance()
					// .setCredentials("37828a97-31e6-44b5-9647-865682f54e67",
					// "ARRhe3hA74as");
					.setCredentials("3921f58c-e230-4da6-b61f-33b66a538410", "XXynLwdZ6aZ5");
			com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().setVoice(voiceName);
			// //Call the sdk function
			com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech.sharedInstance().synthesize(this,
					content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		tts = new TextToSpeech(this, new OnInitListener() {

			@Override
			public void onInit(int status) {
				tts.setLanguage(Locale.US);
				tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

					@Override
					public void onStart(String utteranceId) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDone(String utteranceId) {
						MainActivity.mHandler.sendMessage(MainActivity.mHandler.obtainMessage(0, utteranceId));

					}

					@Override
					public void onError(String utteranceId) {
						// TODO Auto-generated method stub

					}
				});
			}
		});

		mEngineType = SpeechConstant.TYPE_CLOUD;
		mTts = SpeechSynthesizer.createSynthesizer(this, new InitListener() {

			@Override
			public void onInit(int arg0) {
				// TODO Auto-generated method stub
			}
		});

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		String path = intent.getStringExtra("path");
		String ttsEngine = intent.getStringExtra("ttsEngine");
		String content = intent.getStringExtra("CONTENT");
		String ttsSex = intent.getStringExtra("ttsSex");
		float ttsPithch = intent.getFloatExtra("ttsPithch", 1.0f);
		float ttsSpeed = intent.getFloatExtra("ttsSpeed", 1.0f);
		if (ttsEngine != null) {
			if (ttsEngine.equals("google")) {
				playByGoogle(content, ttsPithch, ttsSpeed);
			}

			if (ttsEngine.equals("ibm")) {
				playByIbm(content, ttsSex);
			}

			if (ttsEngine.equals("xunfei")) {
				playByXunFei(content, ttsSex, ttsPithch, ttsSpeed);
			}

			if (ttsEngine.equals("path")) {
				String musicname = intent.getStringExtra("musicname");
				name=musicname;
				playByPath(musicname);
			}
		}
		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}

		if (mTts != null) {
			mTts.stopSpeaking();
			mTts.destroy();
		}

		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
	}

	private void playByXunFei(final String content, String ttsSex, float ttsPithch, float ttsSpeed) {

		String name = "vimary";
		String speed = "50";
		String pitch = "50";
		String volume = "100";
		if (ttsSex.equals("men")) {
			name = "henry";
		}
		if (ttsPithch == 0.5f) {
			pitch = "25";
		} else if (ttsPithch == 2.0f) {
			pitch = "100";
		}
		if (ttsSpeed == 0.5f) {
			speed = "25";
		} else if (ttsSpeed == 2.0f) {
			speed = "100";
		}

		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		mTts.setParameter(SpeechConstant.VOICE_NAME, name);
		mTts.setParameter(SpeechConstant.SPEED, speed);
		mTts.setParameter(SpeechConstant.PITCH, pitch);
		mTts.setParameter(SpeechConstant.VOLUME, volume);

		mTts.startSpeaking(content, new com.iflytek.cloud.SynthesizerListener() {

			@Override
			public void onSpeakResumed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSpeakProgress(int arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSpeakPaused() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSpeakBegin() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCompleted(SpeechError error) {
				if (error == null) {
					MainActivity.mHandler.sendMessage(MainActivity.mHandler.obtainMessage(0, content));
				}
			}

			@Override
			public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void playByPath(String musicname) {
		String audioPath;
		if(musicname.contains("/")){
			audioPath=musicname;
			
		}
		else{
			audioPath= "/sdcard/test_audio/" + musicname;
		}
		
		Log.i("ttsTest", audioPath);

		mMediaPlayer = new MediaPlayer();

		if (!mMediaPlayer.isPlaying()) {
			try {
				mMediaPlayer.setDataSource(audioPath);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			mMediaPlayer.pause();
		}

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mMediaPlayer.release();
				mMediaPlayer = null;
				MainActivity.mHandler.sendMessage(MainActivity.mHandler.obtainMessage(2, name));
			}
		});
	}

}
