package com.feytuo.chat.activity;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.adapter.ListViewAdapter;
import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.ImageLoader;

/**
 * 黑名单列表页面
 * 
 */
public class BlacklistActivity extends BaseActivity {
	private ListView listView;
	private BlacklistAdapater adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_list);

		listView = (ListView) findViewById(R.id.list);

		List<String> blacklist = null;
		try {
			// 获取黑名单
			blacklist = EMContactManager.getInstance().getBlackListUsernames();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 显示黑名单列表
		if (blacklist != null) {
			Collections.sort(blacklist);
			adapter = new BlacklistAdapater(this, 1, blacklist);
			listView.setAdapter(adapter);
		}

		// 注册上下文菜单
		registerForContextMenu(listView);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.remove_from_blacklist, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.remove) {
			final String tobeRemoveUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 把目标user移出黑名单
			removeOutBlacklist(tobeRemoveUser);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 移出黑民单
	 * 
	 * @param tobeRemoveUser
	 */
	void removeOutBlacklist(final String tobeRemoveUser) {
		try {
			// 移出黑民单
			EMContactManager.getInstance().deleteUserFromBlackList(tobeRemoveUser);
			adapter.remove(tobeRemoveUser);
		} catch (EaseMobException e) {
			e.printStackTrace();
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), "移出失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * adapter
	 * 
	 */
	private class BlacklistAdapater extends ArrayAdapter<String> {

		private LXHUserDao userDao;
		private ImageLoader mImageLoader;
		public BlacklistAdapater(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			userDao = new LXHUserDao(context);
			mImageLoader = new ImageLoader(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getContext(), R.layout.row_contact, null);
				holder.avatarImageView = (ImageView)convertView.findViewById(R.id.avatar);
				holder.nickTextView = (TextView)convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			// 设置昵称和头像
			setUserInfo(getItem(position).toString(),
					holder.nickTextView, holder.avatarImageView);
			return convertView;
		}
		
		class ViewHolder{
			private ImageView avatarImageView;
			private TextView nickTextView;
		}
		
		/**
		 * 设置item的用户昵称、头像
		 * @param userName
		 * @param nameTV
		 * @param personHeadImg
		 */
		public void setUserInfo(String uId, TextView nameTV,
				ImageView personHeadImg) {
			LXHUser user = null;
			user = userDao.getNickAndHeadByUid(uId);
			if (user != null) {// 如果本地数据库存在该用户
				nameTV.setText(user.getNickName());
				Log.i("ImageLoader", ""+user.getHeadUrl());
				mImageLoader.loadImage(user.getHeadUrl(), this,
						personHeadImg);
			} else {// 如果没有再从bmob上取
				setUserInfoFromBmob(uId, nameTV, personHeadImg);
			}
		}

		// 从网络获取帖子作者昵称和头像
		private void setUserInfoFromBmob(final String uId, final TextView nameTV,
				final ImageView personHeadImg) {
			// TODO Auto-generated method stub
			BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
			query.addWhereEqualTo("objectId", uId);
			query.findObjects(BlacklistActivity.this, new FindListener<LXHUser>() {

				@Override
				public void onSuccess(List<LXHUser> arg0) {
					// TODO Auto-generated method stub
					if (arg0.size() > 0) {
						nameTV.setText(arg0.get(0).getNickName());
						mImageLoader.loadImage(arg0.get(0)
								.getHeadUrl(), BlacklistAdapater.this, personHeadImg);
						userDao.insertUser(arg0.get(0));
					} else {
						// 没有改用户信息
					}
				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.i("blacklist", "黑名单查找用户失败：" + arg1);
				}
			});
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
}
