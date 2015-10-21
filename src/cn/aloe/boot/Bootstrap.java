package cn.aloe.boot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import cn.aloe.map.MyMap;
import cn.aloe.zhilu.R;

public class Bootstrap extends FragmentActivity{
	private ViewPager vp;
	private int[] layoutId = {R.layout.guide1,R.layout.guide2};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bootstrap);
		vp = (ViewPager)findViewById(R.id.bootstrap);
		MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
		vp.setPageTransformer(true, new TranslatePageTransformer());
		vp.setAdapter(myAdapter);
	}
	
	class MyAdapter extends FragmentStatePagerAdapter{
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int position) {
			Fragment fragment = new TranslateFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("layoutId", layoutId[position]);
			fragment.setArguments(bundle);
			return fragment;
		}

		public int getCount() {
			return 2;
		}
	}
	
	public void goMapClick(View view){
		Intent intent = new Intent(Bootstrap.this, MyMap.class);
		startActivity(intent);
		Bootstrap.this.finish();
	}
}
