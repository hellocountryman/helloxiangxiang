package com.feytuo.laoxianghao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

public class SimpleSelsectedCountry extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_selected_country);

	}

	public void selected_country_click(View v) {
		Intent intent = new Intent();
		intent.putExtra("isfromtocity", 0);// 判断是从那里进入的城市选择
		intent.setClass(this, SelsectedCountry.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("SimpleSelsectedCountry"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("SimpleSelsectedCountry");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
