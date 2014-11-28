package com.feytuo.laoxianghao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.UpdateListener;

import com.feytuo.chat.widget.PasteEditText;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;

public class PersonUpdateInfoActivity extends Activity {
	private TextView titleTypeText;//显示昵称还是个性签名
	private TextView typeTint;//文字输入提示
	private PasteEditText mEditText;// 设置昵称的edit
	private RelativeLayout rela;// 设置昵称的底部横线
	private TextView wordnumText;//提示还可以输入多少字
	private String type;//判断是修改昵称，还是签名
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_person_info_activity);
		type=getIntent().getStringExtra("type");
		initview();

	}

	public void initview()
	{
		titleTypeText=(TextView)findViewById(R.id.title_type_text);
		typeTint=(TextView)findViewById(R.id.type_hint);
		// //昵称
		mEditText = (PasteEditText) findViewById(R.id.person_edit);
		if(type.equals("nick"))//如果是修改昵称
		{
			titleTypeText.setText("修改昵称");
			typeTint.setText("好名字可以让你的朋友更加容易记住你");
		}
		else
		{
			titleTypeText.setText("修改签名");
			typeTint.setText("签名即个性，秀出你的个性吧");
		}
		rela = (RelativeLayout) findViewById(R.id.edittext_rela);
		wordnumText=(TextView)findViewById(R.id.wordnumtext);
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rela.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					rela.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});
		
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				wordnumText.setText(20-s.length()+"/20");	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//点击修改完成按钮
	public void updateInfoSuccess(View v)
	{
		updateUserInfo(mEditText.getText().toString().trim());
	}
	

	/**
	 * 修改个性签名
	 * @param et
	 */
	private void updateUserInfo(final String et) {
		// TODO Auto-generated method stub
		final String userId = App.pre.getString(Global.USER_ID, "");
		LXHUser user = new LXHUser();
		if("nick".equals(type)){//昵称
			user.setNickName(et);
		}else{//个性签名
			user.setPersonSign(et);
		}
		user.update(this, userId, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if("nick".equals(type)){//昵称
					new LXHUserDao(PersonUpdateInfoActivity.this).updateUserNickName(userId, et);
				}else{//个性签名
					new LXHUserDao(PersonUpdateInfoActivity.this).updateUserPersonSign(userId, et);
				}
				Intent intent = new Intent();
				intent.putExtra("data", et);
				setResult(Global.RESULT_OK,intent);
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(PersonUpdateInfoActivity.this, "网络或服务器问题，请稍候再试...",Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void personDetailsRetImg(View v) {
		setResult(Global.RESULT_RETURN);
		finish();
	}
}
