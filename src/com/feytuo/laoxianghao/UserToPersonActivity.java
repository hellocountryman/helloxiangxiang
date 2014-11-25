package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.feytuo.chat.activity.ChatActivity;
import com.feytuo.laoxianghao.adapter.FindListViewAdapter;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.listviewonload.SimpleFooter;
import com.feytuo.listviewonload.SimpleHeader;
import com.feytuo.listviewonload.ZrcListView;
import com.feytuo.listviewonload.ZrcListView.OnStartListener;
import com.umeng.analytics.MobclickAgent;

public class UserToPersonActivity extends Activity {

	private FindListViewAdapter adapter;
	private ListView userToPersonListView;
	private List<Map<String, Object>> listItems;
	private List<Invitation> listData;
	private TextView toPersonHome;// 家乡;
	private String userid;//用户id
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_to_person_activity);
		userid=getIntent().getStringExtra("userid");
		initlistview();
	}

	public void toPersonReturnBtn(View v) {
		finish();
	}

	// 跳转到查看他人的信息
	public void toPersondetails(View v) {
		Intent intent = new Intent();
		intent.setClass(UserToPersonActivity.this,
				PersonUpdateInfoActivity.class);
		startActivity(intent);
	}

	public void initlistview() {

		userToPersonListView = (ListView) findViewById(R.id.user_to_person_listview);

		listItems = new ArrayList<Map<String, Object>>();
		
		getListData(userid);//根据用户ID从网络上面获取到数据
		Log.i("tangpeng", listItems+"拉拉");
		adapter = new FindListViewAdapter(UserToPersonActivity.this, listItems,
				R.layout.index_listview, new String[] { "position", "words",
						"time", "praise_num", "comment_num" }, new int[] {
						R.id.index_locals_country, R.id.index_text_describe,
						R.id.index_locals_time, R.id.index_support_num,
						R.id.index_comment_num });
		userToPersonListView.setAdapter(adapter);
	}

	private void getListData(final String userId) {
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
						Log.i("MainActivity", "加载失败，请检查网络");
					}
				});
	}

}
