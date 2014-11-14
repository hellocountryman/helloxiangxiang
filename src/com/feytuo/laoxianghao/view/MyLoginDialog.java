package com.feytuo.laoxianghao.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.feytuo.chat.activity.MainActivity;
import com.feytuo.laoxianghao.R;

public class MyLoginDialog extends Dialog {
	Context context;
	private ImageView qqImg;
	private ImageView sinaImg;

	public MyLoginDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public MyLoginDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_dialog);

		qqImg = (ImageView) findViewById(R.id.login_qq_img);
		sinaImg = (ImageView)findViewById(R.id.login_sina_img);
		qqImg.setOnClickListener(loginListener);
		sinaImg.setOnClickListener(loginListener);
	}
	
	private View.OnClickListener loginListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.login_qq_img:
				MainActivity.shareQQ.qqLogin();
				break;
			case R.id.login_sina_img:
				//第一个参数为true是登录，为false是授权
				MainActivity.shareWeibo.SSOAuthorize(true,"",0);
				break;
			}
			if (isShowing()) {
				dismiss();
			}
		}

	};
	
}