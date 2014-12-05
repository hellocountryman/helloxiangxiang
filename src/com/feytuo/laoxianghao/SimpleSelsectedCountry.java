package com.feytuo.laoxianghao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.feytuo.laoxianghao.share_qq.Share_QQ;
import com.feytuo.laoxianghao.share_sina.Share_Weibo;
import com.umeng.analytics.MobclickAgent;

public class SimpleSelsectedCountry extends Activity {
	private Share_Weibo shareWeibo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_selected_country);
		shareWeibo = new Share_Weibo(this);
	}

	// public void selected_country_click(View v) {
	// Intent intent = new Intent();
	// intent.putExtra("isfromtocity", 0);// 判断是从那里进入的城市选择
	// intent.setClass(this, SelsectedCountry.class);
	// startActivity(intent);
	// finish();
	// }

	public void appLogin(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.login_qq_btn:
			new Share_QQ(this).qqLogin(this);
			break;
		case R.id.login_sina_btn:
			shareWeibo.SSOAuthorize(this, true, "", 0);
			break;
		default:
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (shareWeibo.getmSsoHandler() != null) {
			shareWeibo.getmSsoHandler().authorizeCallBack(requestCode,
					resultCode, data);
		}
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
		MobclickAgent.onPageEnd("SimpleSelsectedCountry");// 友盟保证 onPageEnd
															// 在onPause
		// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
