package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.feytuo.chat.activity.ChatActivity;
import com.feytuo.laoxianghao.adapter.FindListViewAdapter;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ImageLoader;

public class UserToPersonActivity extends Activity {

	private FindListViewAdapter adapter;
	private ListView userToPersonListView;
	private List<Map<String, Object>> listItems;
	private TextView toPersonHome;// 家乡;
	private ImageView toPersonHeadImg;
	private TextView toPersonNick;
	private TextView toPersonSignText;
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

//	// 跳转到查看他人的信息
//	public void toPersondetails(View v) {
//		Intent intent = new Intent();
//		intent.setClass(UserToPersonActivity.this,
//				PersonUpdateInfoActivity.class);
//		startActivity(intent);
//	}

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

}
