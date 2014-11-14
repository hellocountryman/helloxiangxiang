package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.feytuo.laoxianghao.adapter.ListViewAdapter;
import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.share_qq.Share_QQ;
import com.feytuo.laoxianghao.share_sina.Share_Weibo;
import com.feytuo.laoxianghao.util.ScreenUtils;
import com.feytuo.laoxianghao.view.MyLoginDialog;
import com.feytuo.laoxianghao.wxapi.Share_Weixin;
import com.feytuo.listviewonload.SimpleFooter;
import com.feytuo.listviewonload.SimpleHeader;
import com.feytuo.listviewonload.ZrcListView;
import com.feytuo.listviewonload.ZrcListView.OnStartListener;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity implements IWeiboHandler.Response {
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

	private final int STATE_REFRESH = 0;// 下拉刷新
	private final int STATE_MORE = 1;// 加载更多
	private final int LIMIT = 10;// 每页的数据是10条
	private int curPage = 0;// 当前页的编号，从0开始
	private PopupWindow popupWindow;

	public static Share_QQ shareQQ;// QQ登录和分享
	public static Share_Weibo shareWeibo;// 微博登录和分享
	public static Share_Weixin shareWeixin;// 微信分享

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		App.pre.edit().putBoolean(Global.IS_FIRST_USE, false).commit();
		initShare(savedInstanceState);
		initview();
		initlistview();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);////
		// 固定竖屏
		// 如果用户已经登录
		if (App.isLogin()) {
			// getPraiseStateFromeNet();
			getMyCommentNotice();
			getMyCollectionNotice();
		}
		// 先从本地加载数据
		getListDataFromLocal();
		indexListView.refresh();
	}

	private void initShare(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		shareQQ = new Share_QQ(this);// QQ登录和分享
		shareWeibo = new Share_Weibo(this);
		if (savedInstanceState != null) {
			shareWeibo.getmWeiboShareAPI().handleWeiboResponse(getIntent(),
					this);
		}
		shareWeixin = new Share_Weixin(this);
	}

	/**
	 * "我的帖子"的评论数是否有更新
	 * 
	 * @return
	 */
	private void getMyCommentNotice() {
		// TODO Auto-generated method stub
		final List<Integer> localCommentNum = new ArrayList<Integer>();
		final List<Integer> netCommentNum = new ArrayList<Integer>();
		final List<String> myInvIds = new ArrayList<String>();
		new InvitationDao(this).getAllCommentNum(localCommentNum, myInvIds);

		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereContainedIn("objectId", myInvIds);
		query.addQueryKeys("commentNum");
		query.findObjects(this, new FindListener<Invitation>() {
			@Override
			public void onSuccess(List<Invitation> arg0) {
				// TODO Auto-generated method stub
				for (Invitation inv : arg0) {
					netCommentNum.add(inv.getCommentNum());
				}
				// 对比本地和服务器评论数是否有不同
				for (int i = 0; i < netCommentNum.size(); i++) {
					int idIndex = 0;
					for (int j = 0; j < myInvIds.size(); j++) {
						if (arg0.get(i).getObjectId().equals(myInvIds.get(j))) {
							idIndex = j;
							break;
						}
					}
					Log.i("comment_notice", arg0.get(i).getObjectId() + "=="
							+ myInvIds.get(idIndex));
					if (netCommentNum.get(i) != localCommentNum.get(idIndex)) {
						// 添加修改UI代码
						messageImgview
								.setBackgroundResource(R.drawable.notice_selector);
						Log.i("comment_notice", "有更新:");
						break;
					} else {
						Log.i("comment_notice", "无");
					}
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("comment_notice", "查询我贴评论数失败:" + arg1);
			}
		});
	}

	/**
	 * "我的收藏"的评论数是否有更新
	 */
	private void getMyCollectionNotice() {
		// TODO Auto-generated method stub
		final List<Integer> localCollectionNum = new ArrayList<Integer>();
		final List<Integer> netCollectionNum = new ArrayList<Integer>();
		final List<String> myInvIds = new ArrayList<String>();
		new InvitationDao(this).getAllCommentNumFromCollection(
				localCollectionNum, myInvIds);

		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereContainedIn("objectId", myInvIds);
		query.addQueryKeys("commentNum");
		query.findObjects(this, new FindListener<Invitation>() {
			@Override
			public void onSuccess(List<Invitation> arg0) {
				// TODO Auto-generated method stub
				for (Invitation inv : arg0) {
					netCollectionNum.add(inv.getCommentNum());
				}
				// 对比本地和服务器评论数是否有不同
				for (int i = 0; i < netCollectionNum.size(); i++) {
					int idIndex = 0;
					for (int j = 0; j < myInvIds.size(); j++) {
						if (arg0.get(i).getObjectId().equals(myInvIds.get(j))) {
							idIndex = j;
							break;
						}
					}
					Log.i("collect_notice", arg0.get(i).getObjectId() + "=="
							+ myInvIds.get(idIndex));
					if (netCollectionNum.get(i) != localCollectionNum
							.get(idIndex)) {
						// 添加修改UI代码
						messageImgview
								.setBackgroundResource(R.drawable.notice_selector);
						Log.i("collect_notice", "有更新:");
						break;
					} else {
						Log.i("collect_notice", "无");
					}
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("collect_notice", "查询收藏贴评论数失败:" + arg1);
			}
		});
	}

	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		// TODO Auto-generated method stub
		listData = new InvitationDao(this).getAllInfo(App.pre.getInt(
				Global.CURRENT_NATIVE, 1));
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

	public void initview() {

		indexCitySelect = (TextView) findViewById(R.id.index_city_select);
		publishImgview = (ImageView) findViewById(R.id.publish_imgview);
		messageImgview = (ImageView) findViewById(R.id.message_imgview);
		indexListView = (ZrcListView) findViewById(R.id.index_listview);

		// 设置方言地选择textview
		setIndexCitySelect();

		listener listenerlist = new listener();
		indexCitySelect.setOnClickListener(listenerlist);
		moreImgview = (ImageView) findViewById(R.id.more_imgview);
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
		String homeName = new CityDao(this).getCityNameById(homeId);
		indexCitySelect.setText(homeName.substring(0, homeName.length()) + "话");

	}

	public void initlistview() {
		handler = new Handler();

		// 设置默认偏移量，主要用于实现透明标题栏功能。（可选）
		float density = getResources().getDisplayMetrics().density;
		indexListView.setFirstTopOffset((int) (50 * density));

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(this);
		header.setTextColor(getResources().getColor(R.color.indexbg));
		header.setCircleColor(getResources().getColor(R.color.indexbg));
		indexListView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(this);
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

		listItems = new ArrayList<Map<String, Object>>();
		adapter = new ListViewAdapter(MainActivity.this, listItems,
				R.layout.index_listview_copy, new String[] { "position",
						"words", "time", "praise_num", "comment_num" },
				new int[] { R.id.index_locals_country,
						R.id.index_text_describe, R.id.index_locals_time,
						R.id.index_support_num, R.id.index_comment_num });
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
				if (!App.isLogin()) {// 判断是否登录
					showLoginDialog();
				} else {
					Intent intentpublish = new Intent();
					intentpublish.setClass(MainActivity.this,
							PublishActivity.class);
					startActivity(intentpublish);
				}
				break;
			case R.id.message_imgview:// 点击消息按钮
				if (!App.isLogin()) {// 判断是否登录
					showLoginDialog();
				} else {
					messageImgview
							.setBackgroundResource(R.drawable.notice_normal_selector);
					Intent intentmessage = new Intent();
					intentmessage.setClass(MainActivity.this,
							MessageCellectActivity.class);
					startActivity(intentmessage);
				}
				break;
			case R.id.more_imgview:// 点击更多按钮
				showPopUp(moreImgview);
				break;
			case R.id.index_city_select:// 点击左上角的选择城市
				Intent intentselsectcity = new Intent();
				intentselsectcity.putExtra("isfromtocity", 1);// 判断是从那里进入的城市选择
				intentselsectcity.setClass(MainActivity.this,
						SelsectedCountry.class);
				startActivity(intentselsectcity);
				finish();
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void showPopUp(View v) {
		View root = this.getLayoutInflater().inflate(R.layout.pull_down, null);
		// 对于一个没有被载入或者想要动态载入的界面, 都需要使用inflate来载入.
		indexFeedbackLinerlayout = (LinearLayout) root
				.findViewById(R.id.index_feedback_linerlayout);

		popupWindow = new PopupWindow(root, ScreenUtils.dip2px(this, 100),
				ScreenUtils.dip2px(this, 46));
		// 把宽和高转化成dip
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
				location[1] + ScreenUtils.dip2px(this, 46));// //把宽和高转化成dip
		indexFeedbackLinerlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(MainActivity.this, "popwindow点击",
//						Toast.LENGTH_SHORT).show();
				// TODO Auto-generated method stub
				Intent intentfeedback = new Intent();
				intentfeedback.setClass(MainActivity.this,
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
		query.findObjects(this, new FindListener<Invitation>() {
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
				InvitationDao inv = new InvitationDao(MainActivity.this);
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
//						Toast.makeText(MainActivity.this, "没有啦,要么...来几句？",
//								Toast.LENGTH_SHORT).show();
						indexListView.stopLoadMore();// 关闭上拉加载的功能
					} else {
//						Toast.makeText(MainActivity.this, "暂无更新",
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
//				Toast.makeText(MainActivity.this, "查询失败：" + arg1,
//						Toast.LENGTH_SHORT).show();
				Log.i("MainActivity", "查询失败");
				indexListView.setRefreshFail("暂无更新");
			}
		});
	}

	private Dialog loginDialog;

	public void showLoginDialog() {
		// TODO Auto-generated method stub
		loginDialog = new MyLoginDialog(MainActivity.this, R.style.MyDialog);
		loginDialog.show();
	}

	/**
	 * 微博分享接口
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
//			Toast.makeText(this, R.string.weibosdk_demo_toast_share_success,
//					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
//			Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled,
//					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
//			Toast.makeText(
//					this,
//					getString(R.string.weibosdk_demo_toast_share_failed)
//							+ "Error Message: " + baseResp.errMsg,
//					Toast.LENGTH_LONG).show();
			break;
		}
	}

	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		// mWeiboShareAPI.handleWeiboResponse(intent, this);
		shareWeibo.getmWeiboShareAPI().handleWeiboResponse(intent, this);
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (shareWeibo.getmSsoHandler() != null) {
			shareWeibo.getmSsoHandler().authorizeCallBack(requestCode,
					resultCode, data);
		}
	}
	
	@Override
	protected void onResume() {
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
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MainActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
