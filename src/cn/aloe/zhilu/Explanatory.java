package cn.aloe.zhilu;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class Explanatory extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explanatory);
	}
	
	// 手机按键事件
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Explanatory.this.finish();
				break;
			default:
				break;
			}
			return false;
		}
}
