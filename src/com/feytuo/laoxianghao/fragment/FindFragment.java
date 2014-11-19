package com.feytuo.laoxianghao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.feytuo.laoxianghao.MessageCellectActivity;
import com.feytuo.laoxianghao.R;

public class FindFragment extends Fragment {

	private RelativeLayout findTopicRelac;
	private RelativeLayout findDuanziRelac;
	private RelativeLayout findktvRelac;
	private RelativeLayout findtShowRelac;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.find_activity, container, false);

		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initview();
		super.onActivityCreated(savedInstanceState);
	}

	public void initview() {

		findTopicRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_topic_linear);
		findDuanziRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_duanzi_linear);
		findktvRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_ktv_linear);
		findtShowRelac = (RelativeLayout) getActivity().findViewById(
				R.id.find_show_linear);
		listener lister = new listener();
		findTopicRelac.setOnClickListener(lister);
		findDuanziRelac.setOnClickListener(lister);
		findktvRelac.setOnClickListener(lister);
		findtShowRelac.setOnClickListener(lister);
	}

	class listener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// case R.id.find_topic_linear:

			// break;
			// case R.id.find_duanzi_linear:
			//
			// break;
			// case R.id.find_ktv_linear:
			//
			// break;
			// case R.id.find_show_linear:
			//
			// break;
			default:
				Intent intent = new Intent();
				intent.setClass(getActivity(), MessageCellectActivity.class);
				getActivity().startActivity(intent);
				break;
			}
		}
	};
}
