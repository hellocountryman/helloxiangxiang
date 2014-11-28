package com.feytuo.laoxianghao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
	}

	public void me_about_us_ret(View v) {
		finish();
	}

	public void activity_about_us_feedback(View v) {
		Intent intent = new Intent(this, FeedbackActivity.class);
		startActivity(intent);
	}

	public void our_privacy_olicy(View v) {
		Intent intent = new Intent(this, MeAboutUsPrivacyPolicyActivity.class);
		startActivity(intent);
	}
}
