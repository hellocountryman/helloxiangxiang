package com.feytuo.laoxianghao.global;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.domain.LXHUser;

public class UserLogin {

	public static LXHUser gUser = null;
	
	/**
	 * 获取当前登录的用户信息
	 * @return
	 */
	public static LXHUser getCurrentUser(){
		if(gUser == null || TextUtils.isEmpty(gUser.getObjectId())){
			gUser = new LXHUser();
			gUser.setObjectId(App.pre.getString(Global.USER_ID, ""));
		}
		return gUser;
	}
	/**
	 * 登录 先判断数据库是否有用户 没有则添加用户 有则直接取得用户id
	 * 
	 * @param uKey
	 */
	public static void Login(final Context context ,final String uName, final String uKey) {
		// TODO Auto-generated method stub
		// 判断是否在数据库中有该用户
		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
		query.addWhereEqualTo("uName", uName);
		query.findObjects(context, new FindListener<LXHUser>() {
			@Override
			public void onSuccess(List<LXHUser> arg0) {
				// TODO Auto-generated method stub
				if (arg0.size() > 0) {
					Toast.makeText(context, uKey + "登陆成功",
							Toast.LENGTH_SHORT).show();
					App.pre.edit()
							.putString(Global.USER_ID,
									arg0.get(0).getObjectId()).commit();
				} else {
					saveAndLogin(context,uName, uKey);
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, uKey + "登陆失败",
						Toast.LENGTH_SHORT).show();
				Log.i("loginproblem", arg1+"----"+arg0);
			}
		});
	}

	private static void saveAndLogin(final Context context, final String uName, final String uKey) {
		// 如果没，则添加用户
		final LXHUser user = new LXHUser();
		user.setuName(uName);
		user.setuKey(uKey);
		user.save(context, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(context, uKey + "登陆成功",
						Toast.LENGTH_SHORT).show();
				App.pre.edit().putString(Global.USER_ID, user.getObjectId())
						.commit();
				gUser = user;//将获取的用户设为全局变量
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, uKey + "登录失败",
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
