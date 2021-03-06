package com.feytuo.laoxianghao;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.feytuo.laoxianghao.domain.FeedBack;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.UserLogin;
import com.feytuo.laoxianghao.util.AppInfoUtil;
import com.umeng.analytics.MobclickAgent;

public class FeedbackActivity extends Activity {

	private Button  feedbackSetButton;
	private EditText publishText;
	
	// 用户信息
	private LXHUser mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		mUser = UserLogin.getCurrentUser();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		listener listenerlist = new listener();
		feedbackSetButton = (Button) findViewById(R.id.feedback_set_button);
		feedbackSetButton.setOnClickListener(listenerlist);
		publishText = (EditText)findViewById(R.id.feedback_publish_text);
	}

	public void feedbackReturnRelative(View v) {
		finish();
	}

	class listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.feedback_set_button:
				//发送反馈信息
				sendFeedBack();
				break;
			}
		}
	}
	
	/**
	 * 发送反馈数据
	 */
	public void sendFeedBack(){
		if("".equals(publishText.getText().toString())){
			Toast.makeText(this, "请尽情吐槽吧~", Toast.LENGTH_SHORT).show();
		}else{
			final FeedBack feedBack = new FeedBack();
			feedBack.setContent(publishText.getText().toString());
			feedBack.setVersion(AppInfoUtil.getAppVersionName(this));
			if(mUser != null){
				feedBack.setUser(mUser);
			}
			feedBack.save(this,new SaveListener(){

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(FeedbackActivity.this, "arg1", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					addFeedBackToUser(feedBack);
				}
				
			});
		}
	}
	
	private void addFeedBackToUser(final FeedBack feedBack) {
		// TODO Auto-generated method stub
		if (mUser == null || TextUtils.isEmpty(mUser.getObjectId())) {
			return;
		}
		BmobRelation feedBacks = new BmobRelation();
		feedBacks.add(feedBack);
		mUser.setMyFeedBack(feedBacks);
		mUser.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(FeedbackActivity.this, "谢谢您的关怀~", Toast.LENGTH_SHORT).show();
				finish();
				Log.i("PublishActivity", "上传反馈成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("PublishActivity", "上传反馈失败" + arg1);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("FeedbackActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("FeedbackActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
