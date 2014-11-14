package com.feytuo.laoxianghao.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.adapter.NoticeListViewAdapter;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ScreenUtils;
import com.umeng.analytics.MobclickAgent;

public class Fragment1 extends Fragment {

	private Button messageRedPointB;

	public Fragment1() {
		// TODO Auto-generated constructor stub
	}

	private ListView mymessageListview;
	private NoticeListViewAdapter adapter;
	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> tempListItems;
	private List<Invitation> listData;
	public View rootView;
	private SparseArray<Boolean> commentMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		commentMap = new SparseArray<>();
		rootView = inflater.inflate(R.layout.fragment1, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initView();
		getListDataFromLocal();
		super.onActivityCreated(savedInstanceState);
	}

	// 设置红点状态
	private void setRedPoint() {
		// TODO Auto-generated method stub
		if (getActivity() != null) {
			messageRedPointB = (Button) getActivity().findViewById(
					R.id.message_redpoint_btn);
			for (int i = 0, nsize = commentMap.size(); i < nsize; i++) {
				boolean value = commentMap.valueAt(i);
				if (value) {
					messageRedPointB.setVisibility(View.VISIBLE);
					break;
				}
				messageRedPointB.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void initView() {
		mymessageListview = (ListView) getActivity().findViewById(
				R.id.message_listview);

		listItems = new ArrayList<Map<String, Object>>();
		tempListItems = new ArrayList<Map<String, Object>>();
		adapter = new NoticeListViewAdapter(getActivity(), listItems,
				R.layout.index_listview_copy, new String[] { "position",
						"words", "time", "praise_num", "comment_num" },
				new int[] { R.id.index_locals_country,
						R.id.index_text_describe, R.id.index_locals_time,
						R.id.index_support_num, R.id.index_comment_num }, this);

		mymessageListview.setLayoutAnimation(ScreenUtils.getListAnim());// 一个简单的动画效果
		mymessageListview.setAdapter(adapter);
	}

	// 从本地数据库获取数据
	private void getListDataFromLocal() {
		Log.i("Fragment1", "load localdata");
		// TODO Auto-generated method stub
		listData = new InvitationDao(getActivity()).getAllInfoFromMy();
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
			tempListItems.add(map);
		}
		// 获取哪个变了
		getMyCommentNotice();

		listItems.addAll(tempListItems);
		adapter.notifyDataSetChanged();
		// 从网络获取更新
		getListDataFromNet();
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
		List<String> invIds = new ArrayList<String>();
		for (Map<String, Object> inv : tempListItems) {
			invIds.add(inv.get("inv_id").toString());
			localCommentNum.add((Integer) inv.get("comment_num"));
		}

		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereContainedIn("objectId", invIds);
		query.addQueryKeys("commentNum");
		query.findObjects(getActivity(), new FindListener<Invitation>() {
			@Override
			public void onSuccess(List<Invitation> arg0) {
				// TODO Auto-generated method stub
				for (Invitation inv : arg0) {
					netCommentNum.add(inv.getCommentNum());
				}
				// 对比本地和服务器评论数是否有不同
				for (int i = 0; i < netCommentNum.size(); i++) {
					int idIndex = 0;
					for (int j = 0; j < tempListItems.size(); j++) {
						if (arg0.get(i).getObjectId()
								.equals(tempListItems.get(j).get("inv_id"))) {
							idIndex = j;
							break;
						}
					}
					Log.i("Fragment1", arg0.get(i).getObjectId() + "=="
							+ tempListItems.get(idIndex).get("inv_id"));
					if (netCommentNum.get(i) != localCommentNum.get(idIndex)) {
						// 添加修改UI代码
						commentMap.put(idIndex, true);
						Log.i("Fragment1", "有更新:");
					} else {
						commentMap.put(idIndex, false);
						Log.i("Fragment1", "无");
					}
				}
				setRedPoint();
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("comment_notice", "查询我贴评论数失败:" + arg1);
			}
		});
	}

	private void getListDataFromNet() {
		// TODO Auto-generated method stub
		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
//		本来由于设置了外键需要用pointer来获取当前用户的帖子，但由于第一版本限制暂采用老方式
//		query.addWhereRelatedTo("myInvitation",
//				new BmobPointer(UserLogin.getCurrentUser()));
		/**********新版本过度获取，由于第一版只能通过id获取**********/
		query.addWhereEqualTo("uId", App.pre.getString(Global.USER_ID, ""));
		/**********新版本过度获取，由于第一版只能通过id获取**********/
		query.setLimit(1000);
		query.order("-createdAt");
		query.findObjects(getActivity(), new FindListener<Invitation>() {
			@Override
			public void onSuccess(List<Invitation> arg0) {
				// TODO Auto-generated method stub
				Log.i("Fragment1", "获取数据大小：" + arg0.size() + "---uid:"
						+ App.pre.getString(Global.USER_ID, ""));
				listItems.clear();
				for (Invitation inv : arg0) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("inv_id", inv.getObjectId());
					map.put("position", inv.getPosition());
					map.put("time", inv.getCreatedAt());
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
				// 存入本地数据库
				InvitationDao invDao = new InvitationDao(getActivity());
				invDao.deleteAllDataInMy();
				if (arg0.size() > 0) {// 有数据接收
					for (Invitation inv : arg0) {
						invDao.insert2Invitation(inv);
					}
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
	}

	public SparseArray<Boolean> getCommentMap() {
		return commentMap;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setRedPoint();
		adapter.notifyDataSetChanged();
		MobclickAgent.onPageStart("MyInvitationFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MyInvitationFragment");
	}

}
