package com.ihappy.ttl_s;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ihappy.ttl_s.FolderFilePicker.PickPathEvent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	private static EditText outputTxt;
	private static EditText textContent;
	private static EditText timeInterval;
	private static EditText recordText;
	private static Button btPlay;
	private Button btStop;
	private static Button btBrowse;
	private Button btRandom;
	private static Button btRecord;
	private RadioGroup radioSpeed;
	private RadioGroup radioPitch;
	private RadioGroup radioEngine;
	private RadioGroup radioSex;
	private RadioButton radioMen;
	private RadioButton radioWomen;
	private RadioButton radioFast;
	private RadioButton radioSlow;
	private RadioButton radioNormal;
	private RadioButton radioLow;
	private RadioButton radioAlto;
	private RadioButton radioHigh;
	private String filePath;
	private static RadioButton radioGoogle;
	private static RadioButton radioIBM;
	private static RadioButton radioXunfei;
	static float ttsSpeed = 1.0f;
	static float ttsPithch = 1.0f;
	static String ttsSex = "women";
	static String ttsEngine = "google";
	ArrayList<String> result_value;
	ArrayList<String> recordList;
	ArrayList<String> recordListChoose;
	boolean randomFlag = false;
	boolean playFlag = false;
	static boolean threadEnd = true;
	static boolean isComplete;
	int playIndex = 0;
	int listSize = 0;
	int cycle;
	private int recordListSize = 0;
	private static String mPath;
	private static final int READ_REQUEST_CODE = 42;

	public static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				isComplete = true;
				long time = System.currentTimeMillis();
				Log.i("TTS_START_TIME", time + "");
				outputTxt.setText(time + "");
				String input_txt = (String) msg.obj;
				textContent.setText(input_txt + "");
				recordText.setText("");
			} else if (msg.what == 1) {
				btPlay.setText("播放");
				btBrowse.setEnabled(true);
				btRecord.setEnabled(true);
			}
			else if (msg.what == 2) {
				isComplete=true;
				long time = System.currentTimeMillis();
				Log.i("TTS_START_TIME", time + "");
				outputTxt.setText(time + "");
				textContent.setText("");
				recordText.setText((String)msg.obj);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		outputTxt = (EditText) findViewById(R.id.output_txt);
		btPlay = (Button) findViewById(R.id.play);
		btStop = (Button) findViewById(R.id.stop);
		btRandom = (Button) findViewById(R.id.random);
		btBrowse = (Button) findViewById(R.id.browse);
		btRecord = (Button) findViewById(R.id.recordchoose);
		textContent = (EditText) findViewById(R.id.content);
		timeInterval = (EditText) findViewById(R.id.intervalTime);
		recordText=(EditText) findViewById(R.id.recordText);
		btPlay.setOnClickListener(this);
		btStop.setOnClickListener(this);
		btBrowse.setOnClickListener(this);
		btRandom.setOnClickListener(this);
		radioSpeed = (RadioGroup) findViewById(R.id.radioSpeed);
		radioPitch = (RadioGroup) findViewById(R.id.radioPitch);
		radioEngine = (RadioGroup) findViewById(R.id.radioEngine);
		radioSex = (RadioGroup) findViewById(R.id.radioSex);
		radioMen = (RadioButton) findViewById(R.id.men);
		radioWomen = (RadioButton) findViewById(R.id.women);
		radioGoogle = (RadioButton) findViewById(R.id.google);
		radioIBM = (RadioButton) findViewById(R.id.ibm);
		radioXunfei = (RadioButton) findViewById(R.id.xunfei);
		radioFast = (RadioButton) findViewById(R.id.fast);
		radioNormal = (RadioButton) findViewById(R.id.normal);
		radioSlow = (RadioButton) findViewById(R.id.slow);
		radioLow = (RadioButton) findViewById(R.id.low);
		radioAlto = (RadioButton) findViewById(R.id.alto);
		radioHigh = (RadioButton) findViewById(R.id.high);
		final Intent intent = new Intent(MainActivity.this, TtsService.class);
		radioSpeed.setOnCheckedChangeListener(this);
		radioPitch.setOnCheckedChangeListener(this);
		radioEngine.setOnCheckedChangeListener(this);
		radioSex.setOnCheckedChangeListener(this);
		btRecord.setOnClickListener(this);
		setRadiosex(false);

		startService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(MainActivity.this, TtsService.class));
		isComplete=true;
		playFlag=false;
	}

	public static class HudBroadCast1 extends BroadcastReceiver {

		@Override
		public void onReceive(Context paramContext, Intent paramIntent) {
			Log.i("ttsTest", paramIntent.getAction());
			if (paramIntent.getAction().equals("HUD.TEST")) {
				String content = paramIntent.getStringExtra("content");
				String musicname = paramIntent.getStringExtra("musicname");
				String type = paramIntent.getStringExtra("type");
				if (type != null) {
					ttsEngine = type;
					if (type.equals("google")) {
						radioGoogle.setChecked(true);

					} else if (type.equals("ibm")) {
						radioIBM.setChecked(true);
					} else if (type.equals("xunfei")) {
						radioXunfei.setChecked(true);
					}
				}
				Log.i("ttsTest", "ttsEngine=" + ttsEngine);
				Intent intent = new Intent(paramContext, TtsService.class);
				intent.putExtra("ttsEngine", ttsEngine);
				intent.putExtra("CONTENT", content);
				intent.putExtra("ttsSpeed", ttsSpeed);
				intent.putExtra("ttsPithch", ttsPithch);
				intent.putExtra("ttsSex", ttsSex);
				if (musicname != null) {
					intent.putExtra("musicname", musicname);
				}

				paramContext.startService(intent);

			}

			try {
				if (paramIntent.getAction().equals("HUD.TEST.TIME.IBM")) {
					Log.i("YANG", "HUD.TEST.TIME.IBM");
					long ibmTime = paramIntent.getLongExtra("ibmTime", 0);
					if (ibmTime != 0) {
						mHandler.sendMessage(mHandler.obtainMessage(0, "null"));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			if(!threadEnd){
				Toast.makeText(this, "等待上次播放完成", Toast.LENGTH_SHORT).show();
				break;
			}
			if (listSize == 0 && recordListSize == 0) {
				Toast.makeText(this, "未选择播放内容或音频格式不正确", Toast.LENGTH_SHORT).show();
				break;
			}
			if(btPlay.getText().equals("播放")&&playIndex==0){
				try {
					cycle = Integer.parseInt(timeInterval.getText().toString());
				} catch (Exception e) {
					Toast.makeText(this, "时间间隔默认为3", Toast.LENGTH_SHORT).show();
					cycle = 3;
				}
			}
			btBrowse.setEnabled(false);
			btRecord.setEnabled(false);

			playFlag = !playFlag;
			if (playFlag) {
				btPlay.setText("暂停");
			} else {
				btPlay.setText("播放");
			}

			play();
			break;
		case R.id.browse:
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.setType("text/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(intent, READ_REQUEST_CODE);
			break;
		case R.id.random:
			if (!randomFlag) {
				btRandom.setText("关闭随机播放");
			} else {
				btRandom.setText("开启随机播放");
			}
			randomFlag = !randomFlag;
			break;
		case R.id.stop:
			btPlay.setText("播放");
			playIndex = 0;
			playFlag = false;
			btBrowse.setEnabled(true);
			btRecord.setEnabled(true);
			break;
		case R.id.recordchoose:
			Intent intent3 = new Intent(getBaseContext(), FileSelectionActivity.class);
	        startActivityForResult(intent3, 5000);
			break;

		default:
			break;
		}

	}

	private void play() {
		new Thread(new MyRunnable()).start();
	}

	public class MyRunnable implements Runnable {
		String text;
		Random random = new Random();
		Intent intent = new Intent(MainActivity.this, TtsService.class);
		private String musicname;

		@Override
		public void run() {
			threadEnd = false;
			if (listSize > 0) {
				while (playFlag && playIndex < listSize) {
					if (randomFlag) {
						text = result_value.get(random.nextInt(listSize));
					} else {
						text = result_value.get(playIndex);
						playIndex = playIndex + 1;
					}

					intent.putExtra("ttsEngine", ttsEngine);
					intent.putExtra("CONTENT", text);
					intent.putExtra("ttsSpeed", ttsSpeed);
					intent.putExtra("ttsPithch", ttsPithch);
					intent.putExtra("ttsSex", ttsSex);
					startService(intent);
					while (true) {
						if (isComplete) {
							sleep(cycle * 1000);
							break;
						} else {
							sleep(100);
						}

					}
					isComplete = false;
					threadEnd = true;
				}
			}
			if (recordListSize > 0) {
				while (playFlag && playIndex < recordListSize) {
					if (randomFlag) {
						musicname = recordListChoose.get(random.nextInt(recordListSize));
					} else {
						musicname = recordListChoose.get(playIndex);
						playIndex = playIndex + 1;
					}
					intent.putExtra("ttsEngine", "path");
					intent.putExtra("musicname", musicname);
					startService(intent);
					Log.i("ttsTest", "开启service");
					while (true) {
						if (isComplete) {
							sleep(cycle * 1000);
							break;
						} else {
							sleep(100);
						}

					}
					isComplete = false;
					threadEnd = true;
				}

			}

			if (playIndex == listSize || playIndex == recordListSize) {
				playIndex = 0;
				playFlag = false;
				mHandler.sendMessage(mHandler.obtainMessage(1, "null"));
			}
			
		}

		private void sleep(int i) {
			try {
				Thread.sleep(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public ArrayList<String> getTextList() throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			list.add(line);
		}
		br.close();
		return list;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.low:
			ttsPithch = 0.5f;
			break;
		case R.id.alto:
			ttsPithch = 1.0f;
			break;
		case R.id.high:
			ttsPithch = 2.0f;
			break;
		case R.id.fast:
			ttsSpeed = 2.0f;
			break;
		case R.id.normal:
			ttsSpeed = 1.0f;
			break;
		case R.id.slow:
			ttsSpeed = 0.5f;
			break;
		case R.id.women:
			ttsSex = "women";
			break;
		case R.id.men:
			ttsSex = "men";
			break;
		case R.id.ibm:
			ttsEngine = "ibm";
			setRadioPitch(false);
			setRadioSpeed(false);
			setRadiosex(true);
			break;
		case R.id.google:
			ttsEngine = "google";
			setRadiosex(false);
			setRadioPitch(true);
			setRadioSpeed(true);
			break;
		case R.id.xunfei:
			ttsEngine = "xunfei";
			setRadiosex(true);
			setRadioPitch(true);
			setRadioSpeed(true);
			break;
		default:
			break;
		}

	}

	private void setRadioSpeed(boolean b) {
		radioFast.setEnabled(b);
		// radioFast.setChecked(b);
		radioSlow.setEnabled(b);
		// radioSlow.setChecked(b);
		radioNormal.setEnabled(b);
		// radioNormal.setChecked(b);

	}

	private void setRadioPitch(boolean b) {

		radioLow.setEnabled(b);
		// radioLow.setChecked(b);
		radioHigh.setEnabled(b);
		// radioHigh.setChecked(b);
		radioAlto.setEnabled(b);
		// radioAlto.setChecked(b);

	}

	private void setRadiosex(boolean b) {
		radioMen.setEnabled(b);
		radioWomen.setEnabled(b);
		// radioMen.setChecked(b);
		// radioWomen.setChecked(b);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (resultData != null) {
				Uri uri = resultData.getData();
				String[] proj = { "_data" };
				Cursor cursor = getContentResolver().query(uri, proj, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
					Log.i("ttsTest", "path:" + filePath);
					Intent intent2 = new Intent(this, choose.class);
					try {
						intent2.putStringArrayListExtra("list", getTextList());
					} catch (IOException e) {
						e.printStackTrace();
					}
					startActivityForResult(intent2, 1000);
				}
			}
		}
		if (requestCode == 1000 && resultCode == 1001) {
			result_value = resultData.getStringArrayListExtra("listResult");
			listSize = result_value.size();
			if(listSize!=0&&recordListSize!=0){
				recordListChoose.clear();
				recordListSize=0;
			}
			if(listSize!=0){
				Log.i("ttsTest", "aaaaaaaaaaaaaaaa");
				setAllTtsEnabled(true);
			}
		}
//		if (requestCode == 2000 && resultCode == 1001) {
//			recordListChoose = resultData.getStringArrayListExtra("listResult");
//			recordListSize = recordListChoose.size();
//			if(listSize!=0&&recordListSize!=0){
//				result_value.clear();
//				listSize=0;
//			}
//
//		}
	     if(requestCode == 5000 && resultCode == RESULT_OK){
	    	 recordListChoose=new ArrayList<String>();
	            ArrayList<File> Files = (ArrayList<File>) resultData.getSerializableExtra("upload"); //file array list
	            for(File file : Files){
	                String uri = file.getAbsolutePath();
	                if(uri.endsWith(".wav")||uri.endsWith(".mp3")){
	                	recordListChoose.add(uri);
	                }
	    			}
                recordListSize=recordListChoose.size();
                Log.i("ttsTest", recordListChoose+"");
    			if(listSize!=0&&recordListSize!=0){
    				result_value.clear();
    				listSize=0;
	            }
    			if(recordListSize!=0){
    				setAllTtsEnabled(false);
    			}
	        }

	}

	private void setAllTtsEnabled(boolean b) {
		radioIBM.setEnabled(b);
		radioXunfei.setEnabled(b);
		radioGoogle.setEnabled(b);
		radioFast.setEnabled(b);
		radioSlow.setEnabled(b);
		radioNormal.setEnabled(b);
		radioAlto.setEnabled(b);
		radioHigh.setEnabled(b);
		radioLow.setEnabled(b);
		
	}
}