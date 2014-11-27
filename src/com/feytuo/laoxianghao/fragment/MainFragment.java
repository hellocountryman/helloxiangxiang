package com.feytuo.laoxianghao.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.FeedbackActivity;
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

public class MainFragment extends Fragment {

	private final String TAG = "MainFragment";
	private TextView indexCitySelect;// 选择城市的按钮
	private ImageView publishImgview;// 发布
	private ImageView messageImgview;// 消息按钮
	private ImageView moreImgview;// 更多的按钮点击
	// private LinearLayout pullDownId;// actionbar
	private LinearLayout indexFeedbackLinerlayout;// 分享的
	private ListViewAdapter adapter;
	private ZrcListView indexListView;
	private Handler handler;

	private List<Map<String, Object>> listItems;
	private List<Invitation> listData;
	private Invitation topicInvitation;

	private final int STATE_REFRESH = 0;// 下拉刷新
	private final int STATE_MORE = 1;// 加载更多
	private final int LIMIT = 10;// 每页的数据是10条
	private int curPage = 0;// 当前页的编号，从0开始
	private PopupWindow popupWindow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		App.pre.edit().putBoolean(Global.IS_FIRST_USE, false).commit();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initview();
		initlistview();
		// 先从本地加载数据
		getListDataFromLocal();
		indexListView.refresh();
		super.onActivityCreated(savedInstanceState);
	}


	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		// TODO Auto-generated method stub
		InvitationDao invDao = new InvitationDao(getActivity());
		listData = invDao.getAllInfo(App.pre.getInt(Global.CURRENT_NATIVE, 0));
		invDao.setTypeInvitationFromClass(topicInvitation ,1);//获取本地保存的最新的一条话题
		Log.i(TAG, "本地的话题："+topicInvitation);
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
			map.put("uid", inv.getuId());
			map.put("home", inv.getHome());
			map.put("invitation", inv);
			listItems.add(map);
		}
		adapter.notifyDataSetChanged();
	}

	public void initview() {

		indexCitySelect = (TextView) getActivity().findViewById(R.id.index_city_select);
		publishImgview = (ImageView) getActivity().findViewById(R.id.publish_imgview);
		messageImgview = (ImageView) getActivity().findViewById(R.id.message_imgview);
		indexListView = (ZrcListView) getActivity().findViewById(R.id.index_listview);

		// 设置方言地选择textview
		setIndexCitySelect();

		listener listenerlist = new listener();
		indexCitySelect.setOnClickListener(listenerlist);
		moreImgview = (ImageView) getActivity().findViewById(R.id.more_imgview);
		publishImgview.setOnClickListener(listenerlist);
		messageImgview.setOnClickListener(listenerlist);
		moreImgview.setOnClickListener(listenerlist);
	}

	/**
	 * 
	 * 
	 * 设置方言地选择textview
	 */
	private void setIndexCitySelect() {
		int homeId = App.pre.getInt(Global.CURRENT_NATIVE, 1);
		String homeName = new CityDao(getActivity()).getCityNameById(homeId);
		indexCitySelect.setText(homeName.substring(0, homeName.length()) + "话");

	}

	public void initlistview() {
		handler = new Handler();

		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
		float density = getResources().getDisplayMetrics().density;
		indexListView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(getActivity());
		header.setTextColor(getResources().getColor(R.color.indexbg));
		header.setCircleColor(getResources().getColor(R.color.indexbg));
		indexListView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(getActivity());
		footer.setCircleColor(getResources().getColor(R.color.indexbg));
		indexListView.setFootable(footer);

		// 设置列表项出现动画（可选）
		indexListView.setItemAnimForTopIn(R.anim.topitem_in);
		indexListView.setItemAnimForBottomIn(R.anim.bottomitem_in);

		// 下拉刷新事件回调（可选）
		indexListView.setOnRefreshStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				refresh();
			}
		});

		// 加载更多事件回调（可选）
		indexListView.setOnLoadMoreStartListener(new OnStartListener() {
			@Override
			public void onStart() {
				loadMore();
			}
		});

		topicInvitation = new Invitation();
		listItems = new ArrayList<Map<String, Object>>();
		adapter = new ListViewAdapter(getActivity(), listItems,
				topicInvitation);
		indexListView.setLayoutAnimation(getListAnim());
		indexListView.setAdapter(adapter);
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
				getTopicInvitation();
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

	/**
	 * 
	 * 按钮监听
	 * 
	 * @author tangpeng
	 * 
	 */
	class listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.publish_imgview:// 点击发布的按钮
				if (App.isLogin()) {// 判断是否登录
					Intent intentpublish = new Intent();
					intentpublish.setClass(getActivity(),
							PublishActivity.class);
					startActivity(intentpublish);
				}
				break;
			case R.id.message_imgview:// 点击消息按钮
//				if (App.isLogin()) {// 判断是否登录
//					messageImgview
//							.setBackgroundResource(R.drawable.notice_normal_selector);
//					Intent intentmessage = new Intent();
//					intentmessage.setClass(getActivity(),
//							MessageCellectActivity.class);
//					startActivity(intentmessage);
//				}
//				break;
			case R.id.more_imgview:// 点击更多按钮
				showPopUp(moreImgview);
				break;
			case R.id.index_city_select:// 点击左上角的选择城市
//				Intent intentselsectcity = new Intent();
//				intentselsectcity.putExtra("isfromtocity", 1);// 判断是从那里进入的城市选择
//				intentselsectcity.setClass(getActivity(),
//						SelsectedCountry.class);
//				startActivity(intentselsectcity);
//				finish();
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void showPopUp(View v) {
		View root = getActivity().getLayoutInflater().inflate(R.layout.pull_down, null);
		// 对于一个没有被载入或者想要动态载入的界面, 都需要使用inflate来载入.
		indexFeedbackLinerlayout = (LinearLayout) root
				.findViewById(R.id.index_feedback_linerlayout);

		popupWindow = new PopupWindow(root, ScreenUtils.dip2px(getActivity(), 100),
				ScreenUtils.dip2px(getActivity(), 46));
		// 把宽和高转化成dip
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
				location[1] + ScreenUtils.dip2px(getActivity(), 46));// //把宽和高转化成dip
		indexFeedbackLinerlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(MainActivity.getActivity(), "popwindow点击",
//						Toast.LENGTH_SHORT).show();
				// TODO Auto-generated method stub
				Intent intentfeedback = new Intent();
				intentfeedback.setClass(getActivity(),
						FeedbackActivity.class);
				startActivity(intentfeedback);
				dismissPop();
			}
		});

	}

	// pop消失
	private void dismissPop() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}


	//获取最新话题帖子
	private void getTopicInvitation() {
		// TODO Auto-generated method stub
		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereEqualTo("isHot", 1);//不包括话题
		query.order("-createdAt");
		query.setLimit(LIMIT); // 设置每页多少条数据
		query.findObjects(getActivity(), new FindListener<Invitation>() {
			@Override
			public void onSuccess(List<Invitation> arg0) {
				if(arg0.size() > 0 ){
					// 存入本地数据库
					InvitationDao inv = new InvitationDao(getActivity());
					inv.insert2InvitationClass(arg0, 1, false);
					topicInvitation.setPosition(arg0.get(0).getPosition());
					topicInvitation.setWords(arg0.get(0).getWords());
					topicInvitation.setVoice(arg0.get(0).getVoice());
					topicInvitation.setVoiceDuration(arg0.get(0).getVoiceDuration());
					topicInvitation.setTime(arg0.get(0).getCreatedAt());
					topicInvitation.setPraiseNum(arg0.get(0).getPraiseNum());
					topicInvitation.setCommentNum(arg0.get(0).getCommentNum());
					topicInvitation.setObjectId(arg0.get(0).getObjectId());
					topicInvitation.setuId(arg0.get(0).getuId());
					topicInvitation.setHome(arg0.get(0).getHome());
					topicInvitation.setIsHot(arg0.get(0).getIsHot());
					topicInvitation.setShareNum(arg0.get(0).getShareNum());
					topicInvitation.setHeadId(arg0.get(0).getHeadId());
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				Log.i("MainActivity", "话题查询失败");
				indexListView.setRefreshFail("刷新失败，请检查网络...");
			}
		});
	}
	//获取最新帖子
	private void getListData(final int page, final int actionType) {
		// 获取当前城市
		int homeId = App.pre.getInt(Global.CURRENT_NATIVE, 0);
		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		if(homeId > 0){
			query.addWhereEqualTo("home", homeId);
		}
		query.addWhereNotEqualTo("isHot", 1);//不包括话题
		query.order("-createdAt");
		query.setLimit(LIMIT); // 设置每页多少条数据
		query.setSkip(page * LIMIT); // 从第几条数据开始
		query.findObjects(getActivity(), new FindListener<Invitation>() {
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
					map.put("uid", inv.getuId());
					map.put("home", inv.getHome());
					map.put("invitation", inv);
					listItems.add(map);
				}
				adapter.notifyDataSetChanged();
				// 存入本地数据库
				InvitationDao inv = new InvitationDao(getActivity());
				if (actionType == STATE_REFRESH) {
					inv.insert2Invitation(arg0, false);
					indexListView.setRefreshSuccess("加载成功");
					indexListView.startLoadMore(); // 开启LoadingMore功能
				} else if (actionType == STATE_MORE) {
					inv.insert2Invitation(arg0, true);
					indexListView.setLoadMoreSuccess();
				}
				if (arg0.size() == 0) {
					if (actionType == STATE_MORE) {
//						Toast.makeText(MainActivity.getActivity(), "没有啦,要么...来几句？",
//								Toast.LENGTH_SHORT).show();
						indexListView.stopLoadMore();// 关闭上拉加载的功能
					} else {
//						Toast.makeText(MainActivity.getActivity(), "暂无更新",
//								Toast.LENGTH_SHORT).show();
						indexListView.setRefreshSuccess("暂无更新");
						indexListView.startLoadMore(); // 开启LoadingMore功能
					}
				}
				// 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
				curPage++;
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
//				Toast.makeText(MainActivity.getActivity(), "查询失败：" + arg1,
//						Toast.LENGTH_SHORT).show();
				Log.i("MainActivity", "查询失败");
				indexListView.setRefreshFail("刷新失败，请检查网络...");
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//是否需要更新列表
		if(App.pre.getBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false)){
			if(indexListView != null){
				indexListView.refresh();
			}
			App.pre.edit().putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, false).commit();
		}
		MobclickAgent.onPageStart("MainActivity"); // 友盟统计页面
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MainActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(getActivity());
	}

}
