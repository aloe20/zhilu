package cn.aloe.boot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TranslateFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		int layoutId = arguments.getInt("layoutId");
		View view = View.inflate(getActivity(), layoutId, null);
		return view;
	}
}
