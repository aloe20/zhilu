package cn.aloe.zhilu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import cn.aloe.boot.Bootstrap;
import cn.aloe.map.MyMap;

public class Welcome extends Activity {
	private final int SHOW_TIME = 2000;
	private SharedPreferences preferences;
	private Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		preferences = getSharedPreferences("show", Context.MODE_PRIVATE);
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				welcomeView();
			}
		}, SHOW_TIME);
	}
	
	public void welcomeView(){
		Intent intent = new Intent();
		if(preferences.getBoolean("first_start", true)){
			editor = preferences.edit();
			editor.putBoolean("first_start", false);
			editor.commit();
			
			intent.setClass(Welcome.this, Bootstrap.class);
			startActivity(intent);
			Welcome.this.finish();
		} else{
			intent.setClass(Welcome.this, MyMap.class);
			startActivity(intent);
			Welcome.this.finish();
		}
	}
}
