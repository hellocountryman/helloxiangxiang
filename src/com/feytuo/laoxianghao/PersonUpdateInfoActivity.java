package com.feytuo.laoxianghao;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feytuo.chat.widget.PasteEditText;

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
			typeTint.setText("签名即个性。秀出你的个性吧");
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
		mEditText.getText().toString();
	}
	

	public void personDetailsRetImg(View v) {
		finish();
	}
}
