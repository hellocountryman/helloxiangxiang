package com.feytuo.laoxianghao.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.feytuo.laoxianghao.PublishActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.global.HeadImageChoose;

public class HeadChooseDialog extends Dialog {
	Context context;

	private GridView mGridView;
	private SimpleAdapter adapter;
	private List<Map<String, Integer>> data;
	private int position;

	public HeadChooseDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public HeadChooseDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		position = 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.head_choose_dialog);

//		initData();
		initGridView();
	}
//
//	private void initData() {
//		// TODO Auto-generated method stub
//		data = new ArrayList<Map<String, Integer>>();
//		for (int head : HeadImageChoose.HEAD_IDS) {
//			HashMap<String, Integer> map = new HashMap<String, Integer>();
//			map.put("head", head);
//			data.add(map);
//		}
//	}

	private void initGridView() {
		// TODO Auto-generated method stub
		mGridView = (GridView) findViewById(R.id.head_img_gridview);
		adapter = new SimpleAdapter(context, data,
				R.layout.head_choose_grid_item, new String[] { "head" },
				new int[] { R.id.head_choose_img });
		mGridView.setAdapter(adapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Toast.makeText(context, "position:"+position, Toast.LENGTH_SHORT).show();
				setPosition(position);
				if(context instanceof PublishActivity){
//					((PublishActivity)context).setHeadImg(HeadImageChoose.HEAD_IDS[position]);
				}
				HeadChooseDialog.this.dismiss();
			}
			
		});
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	
}