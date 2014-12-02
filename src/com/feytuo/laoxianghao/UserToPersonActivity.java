package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.easemob.chat.EMContactManager;
import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.activity.ChatActivity;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.User;
import com.feytuo.laoxianghao.adapter.FindListViewAdapter;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.view.OnloadDialog;
import com.umeng.analytics.MobclickAgent;

public class UserToPersonActivity extends Activity {

	private FindListViewAdapter adapter;
	private ListView userToPersonListView;
	private List<Map<String, Object>> listItems;
	private TextView toPersonHome;// 家乡;
	private ImageView toPersonHeadImg;
	private TextView toPersonNick;
	private TextView toPersonSignText;
	private Button addFriendBtn;
	private LXHUser mUser;
	
	private String userId;//用户id
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_to_person_activity);
		userId=getIntent().getStringExtra("userid");
		initView();
		initlistview();
		getUserInfo();//获取用户信息
		getListData();//根据用户ID从网络上面获取到数据
	}

	//初始化view
	private void initView() {
		// TODO Auto-generated method stub
		toPersonHeadImg = (ImageView)findViewById(R.id.to_person_head_img);
		toPersonNick = (TextView)findViewById(R.id.to_person_nick);
		toPersonHome = (TextView)findViewById(R.id.to_person_home);
		toPersonSignText = (TextView)findViewById(R.id.to_person_sign_text);	
		addFriendBtn = (Button)findViewById(R.id.user_info_add_friend_btn);
	}

	/**
	 * 点击返回按钮
	 * @param v
	 */
	public void toPersonReturnBtn(View v) {
		finish();
	}
	/**
	 * 点击聊天按钮
	 * @param v
	 */
	public void toPersonChat(View v){
		if(!App.pre.getString(Global.USER_ID, "").equals(userId)){
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
			intent.putExtra("userId", userId);
			startActivity(intent);
		}else{
			//不能和自己聊天
		}
	}


	//初始化listview
	private void initlistview() {

		userToPersonListView = (ListView) findViewById(R.id.user_to_person_listview);
		listItems = new ArrayList<Map<String, Object>>();
		adapter = new FindListViewAdapter(UserToPersonActivity.this, listItems,
				R.layout.index_listview, new String[] { "position", "words",
						"time", "praise_num", "comment_num" }, new int[] {
						R.id.index_locals_country, R.id.index_text_describe,
						R.id.index_locals_time, R.id.index_support_num,
						R.id.index_comment_num });
		userToPersonListView.setAdapter(adapter);
	}
	//获取用户信息
	private void getUserInfo(){
		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
		query.getObject(this, userId, new GetListener<LXHUser>() {
			
			@Override
			public void onSuccess(LXHUser arg0) {
				// TODO Auto-generated method stub
				mUser = arg0;
				setUserInfo(arg0);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(UserToPersonActivity.this, "用户信息加载失败，请检查网络", Toast.LENGTH_SHORT).show();
				Log.i("UserToPersonActivity", "用户信息加载失败，请检查网络");
			}
		});
	}
	//设置用户信息
	private void setUserInfo(LXHUser user) {
		// TODO Auto-generated method stub
		if(user != null){
			new ImageLoader(this).loadNoImage(user.getHeadUrl(), null, toPersonHeadImg);
			toPersonNick.setText(user.getNickName());
			toPersonHome.setText(user.getHome());
			toPersonSignText.setText(user.getPersonSign());
			//添加好友按钮初始化
			if(App.getInstance().getContactList().containsKey(user.getObjectId())){
				addFriendBtn.setVisibility(View.INVISIBLE);
			}else{
				addFriendBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	//获取用户帖子列表
	private void getListData() {
		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereEqualTo("uId", userId);
		query.order("-createdAt");
		query.findObjects(UserToPersonActivity.this,
				new FindListener<Invitation>() {
					@Override
					public void onSuccess(List<Invitation> arg0) {

						for (Invitation inv : arg0) {
							HashMap<String, Object> map = new HashMap<>();
							inv.setTime(inv.getCreatedAt());
							map.put("inv_id", inv.getObjectId());
							map.put("position", inv.getPosition());
							map.put("time", inv.getCreatedAt());
							map.put("words", inv.getWords());
							map.put("praise_num", inv.getPraiseNum());
							map.put("comment_num", inv.getCommentNum());
							map.put("voice", inv.getVoice());
							map.put("voice_duration", inv.getVoiceDuration());
							map.put("ishot", inv.getIsHot());
							map.put("head_id", inv.getHeadId());
							map.put("uid", inv.getuId());
							map.put("home", inv.getHome());
							map.put("invitation", inv);
							listItems.add(map);							
						}
						adapter.notifyDataSetChanged();//bmob从数据库中取出的数据都是异步的，所以要这个东东把，
					}

					@Override
					public void onError(int arg0, String arg1) {
						Log.i("UserToPersonActivity", "用户帖子加载失败，请检查网络");
					}
				});
	}
	
	/**
	 * 点击添加好友按钮
	 * @param v
	 */
	public void addFriend(View v){
		if(mUser != null){
			addContact();
		}
	}
	
	private OnloadDialog pd;
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(){
		pd = new OnloadDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		pd.setMessage("正在发送请求...");
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(mUser.getObjectId(), "加个好友呗");
					//将添加的好友持久到本地数据库
					addToLocalDB(mUser.getObjectId(),mUser.getNickName(),mUser.getHeadUrl());
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserToPersonActivity.this, "成功添加"+mUser.getNickName(), Toast.LENGTH_SHORT).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(UserToPersonActivity.this, "添加好友失败,请稍候再试...", Toast.LENGTH_SHORT).show();
							Log.i("UserToPersonActivity","添加好友失败：" + e.getMessage());
						}
					});
				}
			}
		}).start();
		
		
	}
	
	
	private void addToLocalDB(String username,String userNick,String headUrl){
		// 保存增加的联系人
		Map<String, User> localUsers = App.getInstance()
				.getContactList();
		Map<String, User> toAddUsers = new HashMap<String, User>();
		User user = new User();
		user.setUsername(username);
		user.setNickName(userNick);
		user.setHeadUrl(headUrl);
		setUserHead(user);
		// 暂时有个bug，添加好友时可能会回调added方法两次
		UserDao userDao = new UserDao(this);
		if (!localUsers.containsKey(username)) {
			userDao.saveContact(user);
		}
		toAddUsers.put(username, user);
		localUsers.putAll(toAddUsers);
		pd.dismiss();
	}
	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	void setUserHead(User user) {
		String headerName = null;
		String username = user.getUsername();
		if (!TextUtils.isEmpty(user.getNickName())) {
			headerName = user.getNickName();
		} else {
			headerName = username;
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
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("UserToPersonActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("UserToPersonActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
