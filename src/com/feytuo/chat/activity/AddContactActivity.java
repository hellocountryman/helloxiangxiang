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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.easemob.chat.EMContactManager;
import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.User;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.LXHUser;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText;
	private Button searchBtn;
//	private ImageView avatar;
//	private InputMethodManager inputMethodManager;
	private String toAddUsername;//用户名id
	private String toAddUserNick;//用户昵称
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		
		editText = (EditText) findViewById(R.id.edit_note);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
//		avatar = (ImageView) findViewById(R.id.avatar);
//		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "请输入用户名"));
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
					toAddUserNick = arg0.get(0).getNickName();
					toAddUsername = arg0.get(0).getObjectId();
					//服务器存在此用户，显示此用户和添加按钮
					searchedUserLayout.setVisibility(View.VISIBLE);
					nameText.setText(toAddUserNick);
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


	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(View view){
		if(App.getInstance().getUserName().equals(toAddUsername)){
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "不能添加自己"));
			return;
		}
		
		if(App.getInstance().getContactList().containsKey(toAddUsername)){
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "此用户已是你的好友"));
			return;
		}
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在发送请求...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(toAddUsername, "加个好友呗");
					//将添加的好友持久到本地数据库
					addToLocalDB(toAddUsername,toAddUserNick);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "成功添加好友", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "添加好友失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
	
	
	private void addToLocalDB(String username,String userNick){
		// 保存增加的联系人
		Map<String, User> localUsers = App.getInstance()
				.getContactList();
		Map<String, User> toAddUsers = new HashMap<String, User>();
		User user = setUserHead(username);
		user.setNick(userNick);
		// 暂时有个bug，添加好友时可能会回调added方法两次
		UserDao userDao = new UserDao(this);
		if (!localUsers.containsKey(username)) {
			userDao.saveContact(user);
		}
		toAddUsers.put(username, user);
		localUsers.putAll(toAddUsers);
	}
	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}
	
	public void back(View v) {
		finish();
	}
}
