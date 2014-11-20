package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.feytuo.chat.activity.MainActivity;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.FeedbackActivity;
import com.feytuo.laoxianghao.MessageCellectActivity;
import com.feytuo.laoxianghao.PublishActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.adapter.ListViewAdapter;
import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ScreenUtils;
import com.feytuo.listviewonload.SimpleFooter;
import com.feytuo.listviewonload.SimpleHeader;
import com.feytuo.listviewonload.ZrcListView;
import com.feytuo.listviewonload.ZrcListView.OnStartListener;
import com.umeng.analytics.MobclickAgent;

public class FindDetailsActivity extends Activity {

	private ListViewAdapter adapter;
	private ZrcListView findListView;
	private Handler handler;
	private List<Map<String, Object>> listItems;
	private List<Invitation> listData;
	private TextView findTypeText;
	private final int STATE_REFRESH = 0;// 下拉刷新
	private final int STATE_MORE = 1;// 加载更多
	private final int LIMIT = 10;// 每页的数据是10条
	private int curPage = 0;// 当前页的编号，从0开始

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_details_activity);

		initview();
		initlistview();
		getListDataFromLocal();
		findListView.refresh();
	}

	public void initview() {
		findTypeText = (TextView) findViewById(R.id.find_type_text);
		int type = getIntent().getIntExtra("type", 0);
		if (type == 1) {
			findTypeText.setText("热门话题");
		} else if (type == 2) {
			findTypeText.setText("方言段子");
		} else if (type == 3) {
			findTypeText.setText("方言KTV");
		} else if (type == 4) {
			findTypeText.setText("方言秀场");
		} else {
			findTypeText.setText("全部方言");
		}
	}

	public void findDetailsReturnBtn() {
		finish();
	}

	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		// TODO Auto-generated method stub
		listData = new InvitationDao(FindDetailsActivity.this)
				.getAllInfo(App.pre.getInt(Global.CURRENT_NATIVE, 1));
		for (Invitation inv : listData) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("inv_id", inv.getObjectId());
			map.put("position", inv.getPosition());
			map.put("time", inv.getTime());
			map.put("words", inv.getWords());
			map.put("voice_duration", inv.getVoiceDuration());
			map.put("praise_num", inv.getPraiseNum());
			map.put("comment_num", inv.getCommentNum());
			map.put("voice", inv.getVoice());
			map.put("ishot", inv.getIsHot());
			map.put("head_id", inv.getHeadId());
			map.put("invitation", inv);
			listItems.add(map);
		}
		adapter.notifyDataSetChanged();
	}

	public void initlistview() {
		findListView = (ZrcListView) findViewById(R.id.find_listview);
		handler = new Handler();

		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
		float density = getResources().getDisplayMetrics().density;
		findListView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(this);
		header.setTextColor(getResources().getColor(R.color.indexbg));
		header.setCircleColor(getResources().getColor(R.color.indexbg));
		findListView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(this);
		footer.setCircleColor(getResources().getColor(R.color.indexbg));
		findListView.setFootable(footer);

		// 设置列表项出现动画（可选）
		findListView.setItemAnimForTopIn(R.anim.topitem_in);
		findListView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// 下拉刷新事件回调（可选）
		findListView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				refresh();
			}
		});

		// 加载更多事件回调（可选）
		findListView.setOnLoadMoreStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				loadMore();
			}
		});

		listItems = new ArrayList<Map<String, Object>>();
		adapter = new ListViewAdapter(FindDetailsActivity.this, listItems,
				R.layout.index_listview, new String[] { "position", "words",
						"time", "praise_num", "comment_num" }, new int[] {
						R.id.index_locals_country, R.id.index_text_describe,
						R.id.index_locals_time, R.id.index_support_num,
						R.id.index_comment_num });
		findListView.setLayoutAnimation(getListAnim());
		findListView.setAdapter(adapter);
	}

	/**
	 * 
	 * 加载listview初始动画
	 * 
	 * @tangpeng
	 */
	private LayoutAnimationController getListAnim() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}

	// 下拉刷新事件回调
	private void refresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter.stopAudio();
				getListData(0, STATE_REFRESH);
			}
		}, 2 * 1000);
	}

	// 加载更多事件回调
	private void loadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				adapter.stopAudio();
				getListData(curPage, STATE_MORE);

			}
		}, 2 * 1000);
	}

	private void getListData(final int page, final int actionType) {
		// 获取当前城市
		int homeId = App.pre.getInt(Global.CURRENT_NATIVE, 1);
		BmobQuery<Invitation> query1 = new BmobQuery<Invitation>();
		query1.addWhereEqualTo("home", homeId);
		BmobQuery<Invitation> query2 = new BmobQuery<Invitation>();
		query2.addWhereEqualTo("isHot", 1);
		List<BmobQuery<Invitation>> queries = new ArrayList<BmobQuery<Invitation>>();
		queries.add(query1);
		queries.add(query2);
		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.or(queries);
		query.order("-createdAt");
		query.setLimit(LIMIT); // 设置每页多少条数据
		query.setSkip(page * LIMIT); // 从第几条数据开始
		query.findObjects(FindDetailsActivity.this,
				new FindListener<Invitation>() {
					@Override
					public void onSuccess(List<Invitation> arg0) {
						// TODO Auto-generated method stub
						// if (arg0.size() > 0) {// 有数据接收
						if (actionType == STATE_REFRESH) {
							curPage = 0;
							listItems.clear();
						}
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
							map.put("invitation", inv);
							listItems.add(map);
						}
						adapter.notifyDataSetChanged();
						// 存入本地数据库
						InvitationDao inv = new InvitationDao(
								FindDetailsActivity.this);
						if (actionType == STATE_REFRESH) {
							inv.insert2Invitation(arg0, false);
							findListView.setRefreshSuccess("加载成功");
							findListView.startLoadMore(); // 开启LoadingMore功能
						} else if (actionType == STATE_MORE) {
							inv.insert2Invitation(arg0, true);
							findListView.setLoadMoreSuccess();
						}
						if (arg0.size() == 0) {
							if (actionType == STATE_MORE) {
								// Toast.makeText(MainActivity.getActivity(),
								// "没有啦,要么...来几句？",
								// Toast.LENGTH_SHORT).show();
								findListView.stopLoadMore();// 关闭上拉加载的功能
							} else {
								// Toast.makeText(MainActivity.getActivity(),
								// "暂无更新",
								// Toast.LENGTH_SHORT).show();
								findListView.setRefreshSuccess("暂无更新");
								findListView.startLoadMore(); // 开启LoadingMore功能
							}
						}
						// 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
						curPage++;
					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						// Toast.makeText(MainActivity.getActivity(), "查询失败：" +
						// arg1,
						// Toast.LENGTH_SHORT).show();
						Log.i("MainActivity", "查询失败");
						findListView.setRefreshFail("暂无更新");
					}
				});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 是否需要更新列表
		if (App.pre.getBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false)) {
			if (findListView != null) {
				findListView.refresh();
			}
			App.pre.edit().putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false)
					.commit();
		}
		MobclickAgent.onPageStart("MainActivity"); // 友盟统计页面
		MobclickAgent.onResume(FindDetailsActivity.this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MainActivity");// 友盟保证 onPageEnd 在onPause
												// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(FindDetailsActivity.this);
	}

}
