package cn.aloe.zhilu;

import android.annotation.SuppressLint;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class MyOnTouch implements OnTouchListener {
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View view, MotionEvent event) {
		((TextView) view.findViewById(R.id.show_message)).setMovementMethod(new ScrollingMovementMethod());
		return false;
	}

}
