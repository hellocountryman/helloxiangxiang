/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feytuo.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.feytuo.chat.adapter.AddContactAdapter;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.umeng.analytics.MobclickAgent;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private Button searchBtn;
	private ListView addContactListView;
	private AddContactAdapter adapter;
	private List<LXHUser> listData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		listData = new ArrayList<LXHUser>();
		
		addContactListView = (ListView)findViewById(R.id.add_contact_listview);
		adapter = new AddContactAdapter(this,listData);
		addContactListView.setAdapter(adapter);
		
		editText = (EditText) findViewById(R.id.edit_note);
		searchBtn = (Button) findViewById(R.id.search);
		
	}
	
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
//			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "请输入好友昵称"));
				return;
			}
			
			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
			//根据昵称或者id获取用户信息
			isUserExistInBmob(name);
		} 
	}	
	
	/**
	 * 判断bmob中是否存在该用户
	 * @param name 用户的昵称或者用户id
	 * @return 用户昵称
	 */
	private void isUserExistInBmob(String name) {
		// TODO Auto-generated method stub
		BmobQuery<LXHUser> query1 = new BmobQuery<LXHUser>();
		query1.addWhereEqualTo("objectId",name);
		BmobQuery<LXHUser> query2 = new BmobQuery<LXHUser>();
		query2.addWhereEqualTo("nickName",name);
		List<BmobQuery<LXHUser>> queries = new ArrayList<BmobQuery<LXHUser>>();
		queries.add(query1);
		queries.add(query2);
		BmobQuery<LXHUser> mainQuery = new BmobQuery<LXHUser>();
		mainQuery.or(queries);
		mainQuery.findObjects(this, new FindListener<LXHUser>() {
			
			@Override
			public void onSuccess(List<LXHUser> arg0) {
				// TODO Auto-generated method stub
				if(arg0.size() > 0){
					listData.clear();
					listData.addAll(arg0);
					adapter.notifyDataSetChanged();
				}else{
					Toast.makeText(AddContactActivity.this, "查无此人", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("AddContactActivity", "查询用户失败："+arg1);
			}
		});
	}

	public void back(View v) {
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("AddContactActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("AddContactActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
