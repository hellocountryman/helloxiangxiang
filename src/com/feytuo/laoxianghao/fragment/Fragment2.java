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


public class Fragment2 extends Fragment{
	
	private Button collectRedPoint;
	public Fragment2() {
		// TODO Auto-generated constructor stub
	}
	private ListView mycollectListview;
	private NoticeListViewAdapter adapter;
	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> tempListItems;
	private List<Invitation> listData;
	private SparseArray<Boolean> collectCommentMap;
	public View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		collectCommentMap = new SparseArray<>();
		View rootView = inflater.inflate(R.layout.fragment2, container,false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		getListDataFromLocal();
		super.onActivityCreated(savedInstanceState);
	}
	
	//设置红点状态
	private void setRedPoint() {
		// TODO Auto-generated method stub
		if(getActivity() != null){
			collectRedPoint = (Button)getActivity().findViewById(R.id.collect_redpoint_btn);
			for (int i = 0, nsize = collectCommentMap.size(); i < nsize; i++) {
				boolean value = collectCommentMap.valueAt(i);
				if(value){
					collectRedPoint.setVisibility(View.VISIBLE);
					break;
				}
				collectRedPoint.setVisibility(View.INVISIBLE);
			}
		}
	}
	public void init() {
		mycollectListview = (ListView)getActivity()
				.findViewById(R.id.collect_listview);

		listItems = new ArrayList<Map<String, Object>>();
		tempListItems = new ArrayList<Map<String, Object>>();
		adapter = new NoticeListViewAdapter(getActivity(), listItems,
				R.layout.index_listview_copy, new String[] { "position",
						"words", "time", "praise_num", "comment_num" },
				new int[] { R.id.index_locals_country,
						R.id.index_text_describe,
						R.id.index_locals_time,
						R.id.index_support_num, R.id.index_comment_num },this);

		mycollectListview.setLayoutAnimation(ScreenUtils.getListAnim());
		mycollectListview.setAdapter(adapter);
	}
	
	//从本地数据库获取数据
	private void getListDataFromLocal() {
		// TODO Auto-generated method stub
		listData = new InvitationDao(getActivity()).getAllInfoFromCollection();
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
		getMyCollectNotice();
		listItems.addAll(tempListItems);
		adapter.notifyDataSetChanged();
		getListDataFromNet();
	}
	
	/**
	 * "我的帖子"的评论数是否有更新
	 * 
	 * @return
	 */
	private void getMyCollectNotice() {
		// TODO Auto-generated method stub
		final List<Integer> localCommentNum = new ArrayList<Integer>();
		final List<Integer> netCommentNum = new ArrayList<Integer>();
		List<String> invIds = new ArrayList<String>();
		for(Map<String, Object> inv : tempListItems){
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
				for(int i = 0 ;i< netCommentNum.size();i++){
					int idIndex=0;
					for(int j =0;j < tempListItems.size();j++){
						if(arg0.get(i).getObjectId().equals(tempListItems.get(j).get("inv_id"))){
							idIndex = j;
							break;
						}
					}
					Log.i("Fragment1",arg0.get(i).getObjectId()+"=="+tempListItems.get(idIndex).get("inv_id"));
					if(netCommentNum.get(i) != localCommentNum.get(idIndex)){
						//添加修改UI代码
						collectCommentMap.put(idIndex, true);
						Log.i("Fragment2", "有更新:");
					}else{
						collectCommentMap.put(idIndex, false);
						Log.i("Fragment2", "无");
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
		List<String> invIds = new ArrayList<String>();
		new InvitationDao(getActivity()).getAllIndidFromCollection(invIds);
		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereContainedIn("objectId", invIds);
		query.setLimit(1000);
		query.order("-createdAt");
		query.findObjects(getActivity(), new FindListener<Invitation>() {
			@Override
			public void onSuccess(List<Invitation> arg0) {
				// TODO Auto-generated method stub
				Log.i("Fragment2", "获取数据大小：" + arg0.size() + "---uid:"
						+ App.pre.getString(Global.USER_ID, ""));
				if (arg0.size() > 0) {// 有数据接收
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
					invDao.deleteAllDataInCollection();
					for (Invitation inv : arg0) {
						invDao.insert2InvitationCollection(inv);
					}
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "查询失败：" + arg1,
//						Toast.LENGTH_SHORT).show();
			}
		});
	}
	

	public SparseArray<Boolean> getCollectCommentMap() {
		return collectCommentMap;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setRedPoint();
		adapter.notifyDataSetChanged();
		MobclickAgent.onPageStart("MyCollectFragment"); //统计页面
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		MobclickAgent.onPageEnd("MyCollectFragment"); 
	}
}
