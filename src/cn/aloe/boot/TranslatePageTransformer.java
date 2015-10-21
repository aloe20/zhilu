package cn.aloe.boot;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.ViewGroup;
import cn.aloe.zhilu.R;

public class TranslatePageTransformer implements PageTransformer {
	public void transformPage(View view, float position) {
		if(position<1 && position>-1){
			ViewGroup vg = (ViewGroup) view.findViewById(R.id.vg);
			vg.setScaleX(1f);
//			vg.setScaleY(1f);
			vg.setScaleX(Math.max(0.8f,1-Math.abs(position)));
			vg.setScaleY(Math.max(0.8f,1-Math.abs(position)));
			
			// 3D翻转动画 往外翻转
//			vg.setPivotX(position<0f?vg.getWidth():0f);
//			vg.setPivotY(vg.getHeight()*0.5f);
//			vg.setRotationY(position*90);
			
			// 3D翻转动画 往内翻转
//			vg.setPivotX(position<0f?vg.getWidth():0f);
//			vg.setPivotY(vg.getHeight()*0.5f);
//			vg.setRotationY(-position*90);
			
			vg.setPivotX(vg.getHeight());
			vg.setPivotY(vg.getWidth()*0.5f);
			vg.setRotationY(position*90);
		}
	}

}
