package com.feytuo.laoxianghao.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.feytuo.laoxianghao.MessageCellectActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.global.HeadImageChoose;
import com.feytuo.laoxianghao.util.NetUtil;

public class FindFragment extends Fragment {

	private RelativeLayout findTopicRelac;
	private RelativeLayout findDuanziRelac;
	private RelativeLayout findktvRelac;
	private RelativeLayout findtShowRelac;
	private ImageButton indexTopicImg;//
	private ImageButton indeDuanziImg;
	private ImageButton indexKtvImg;//
	private ImageButton indexShowImg;

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
		indexTopicImg = (ImageButton) getActivity().findViewById(
				R.id.find_topic_img);
		indeDuanziImg = (ImageButton) getActivity().findViewById(
				R.id.find_duanzi_img);
		indexKtvImg = (ImageButton) getActivity().findViewById(
				R.id.find_ktv_img);
		indexShowImg = (ImageButton) getActivity().findViewById(
				R.id.find_show_img);
		NetUtil.corner(getActivity(),R.drawable.findtopic, indexTopicImg);//设置圆角
		NetUtil.corner(getActivity(),R.drawable.findduanzi, indeDuanziImg);
		NetUtil.corner(getActivity(),R.drawable.findktv, indexKtvImg);
		NetUtil.corner(getActivity(),R.drawable.findshow, indexShowImg);

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
