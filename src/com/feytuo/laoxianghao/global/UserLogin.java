package com.feytuo.laoxianghao.global;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.feytuo.chat.Constant;
import com.feytuo.chat.db.UserDao;
import com.feytuo.chat.domain.User;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.util.GetSystemDateTime;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.util.StringTools;

public class UserLogin {

	public static LXHUser gUser = null;
	private ProgressDialog pd;
	private boolean progressShow;

	/**
	 * 获取当前登录的用户信息
	 * 
	 * @return
	 */
	public static LXHUser getCurrentUser() {
		if (gUser == null || TextUtils.isEmpty(gUser.getObjectId())) {
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
	public void Login(final Context context, final String uName,
			final String uKey, String nickName, Bitmap headBitmap) {
		// TODO Auto-generated method stub
		Log.i("UserLogin", "openId:" + uName);
		Log.i("UserLogin", "nickName:" + nickName);
		Log.i("UserLogin", "bitmap:" + headBitmap);
		//1、上传头像文件，获取头像文件地址
		uploadHeadFile(context,uName,uKey,nickName,headBitmap);
		//2、上传用户基本信息
		//3、注册环信服务器
		//4、登录环信服务器
	}

	//1、上传头像文件，获取头像文件地址
	private void uploadHeadFile(final Context context, final String uName,
			final String uKey, final String nickName, Bitmap headBitmap) {
		// TODO Auto-generated method stub
		((Activity)context).runOnUiThread(new Runnable() {
			public void run() {
				pd = new ProgressDialog(context);
				pd.setMessage("正在获取登录信息...");
				pd.setCanceledOnTouchOutside(false);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						progressShow = false;
					}
				});
				pd.show();
			}
		});
		
		File file = saveBitmap2File(context,headBitmap);
		if(file != null && file.exists()){
			final BmobFile bmobFile = new BmobFile(file);
			bmobFile.uploadblock(context, new UploadFileListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// 获取文件url后上传基本信息
					saveAndLogin(context,uName,uKey,nickName,bmobFile.getFileUrl());
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					pd.dismiss();
					Log.i("PublishActivity", "保存登录信息失败："+arg1);
				}
			});
		}
	}

	/**
	 * 2、上传用户基本信息
	 * @param context
	 * @param uName
	 * @param uKey
	 * @param nickName
	 * @param headUrl
	 */
	private void saveAndLogin(final Context context, final String uName,
			final String uKey, final String nickName,String headUrl) {
		// 如果没，则添加用户
		((Activity)context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在保存登录信息...");
			}
		});
		final LXHUser user = new LXHUser();
		user.setuName(uName);
		user.setuKey(uKey);
		user.setHeadUrl(headUrl);
		user.setNickName(nickName);
		user.save(context, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				//注册环信服务器
				registerHX(context,user.getObjectId(),uName);
//				App.pre.edit().putString(Global.USER_ID, user.getObjectId())
//						.commit();
				gUser = user;// 将获取的用户设为全局变量
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				pd.dismiss();
				Toast.makeText(context, uKey + "保存用户信息失败：", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	/**
	 * 3、注册环信服务器
	 * @param uId
	 * @param pwd
	 */
	protected void registerHX(final Context context,final String username, final String pwd) {
		// TODO Auto-generated method stub
		((Activity)context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在注册聊天服务器...");
			}
		});
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(username, pwd);
						// 保存用户名
						App.getInstance().setUserName(username);
						//登录服务器
						loginHX(context,username,pwd);
					} catch (final Exception e) {
						pd.dismiss();
						((Activity)context).runOnUiThread(new Runnable() {
							public void run() {
								if (e != null && e.getMessage() != null) {
									String errorMsg = e.getMessage();
									if (errorMsg.indexOf("EMNetworkUnconnectedException") != -1) {
										Toast.makeText(context, "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
									} else if (errorMsg.indexOf("conflict") != -1) {
										Toast.makeText(context, "用户已存在！",  Toast.LENGTH_SHORT).show();
									}/* else if (errorMsg.indexOf("not support the capital letters") != -1) {
										Toast.makeText(getApplicationContext(), "用户名不支持大写字母！", 0).show();
									} */else {
										Log.i("UserLogin", "注册失败: " + e.getMessage());
									}

								} else {
									Toast.makeText(context, "注册失败: 未知异常", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}).start();

		}
	}

	/**
	 * 4、登录环信服务器
	 * @param context
	 * @param username
	 * @param pwd
	 */
	private void loginHX(final Context context,final String username, final String pwd) {
		// TODO Auto-generated method stub
		((Activity)context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在登陆聊天服务器...");
			}
		});
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			progressShow = true;
			// 调用sdk登陆方法登陆聊天服务器
			EMChatManager.getInstance().login(username, pwd, new EMCallBack() {

				@Override
				public void onSuccess() {
					if (!progressShow) {
						return;
					}
					// 登陆成功，保存用户名密码
					App.getInstance().setUserName(username);
					App.getInstance().setPassword(pwd);
					((Activity)context).runOnUiThread(new Runnable() {
						public void run() {
							pd.setMessage("正在获取好友和群聊列表...");
						}
					});
					try {
						// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
						List<String> usernames = EMContactManager.getInstance().getContactUserNames();
						Map<String, User> userlist = new HashMap<String, User>();
						for (String username : usernames) {
							User user = new User();
							user.setUsername(username);
							setUserHearder(username, user);
							userlist.put(username, user);
						}
						// 添加user"申请与通知"
						User newFriends = new User();
						newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
						newFriends.setNick("申请与通知");
						newFriends.setHeader("");
						userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
						// 添加"群聊"
						User groupUser = new User();
						groupUser.setUsername(Constant.GROUP_USERNAME);
						groupUser.setNick("群聊");
						groupUser.setHeader("");
						userlist.put(Constant.GROUP_USERNAME, groupUser);

						// 存入内存
						App.getInstance().setContactList(userlist);
						// 存入db
						UserDao dao = new UserDao(context);
						List<User> users = new ArrayList<User>(userlist.values());
						dao.saveContactList(users);

						// 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
						EMGroupManager.getInstance().getGroupsFromServer();
					} catch (Exception e) {
						e.printStackTrace();
					}
					boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(App.currentUserNick);
					if (!updatenick) {
						EMLog.e("LoginActivity", "update current user nick fail");
					}

					pd.dismiss();
					App.pre.edit().putString(Global.USER_ID, username).commit();
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(int code, final String message) {
					if (!progressShow) {
						return;
					}
					((Activity)context).runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(context, "登录失败: " + message, Toast.LENGTH_SHORT).show();

						}
					});
				}
			});
		}
	}

	/*
	 * 
	 * 初始化数据
	 */
	private File saveBitmap2File(Context context, Bitmap headBitmap) {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(context, "请插入SD卡以便存储录音", Toast.LENGTH_LONG).show();
			return null;
		}

		// 要保存的文件的路径
		String filePath = SDcardTools.getSDPath() + "/" + "laoxianghaoImage";
		// 实例化文件夹
		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}

		// 保存文件名
		String fileName = "head" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".png";
		File headFile = new File(filePath + fileName);
		try {
			FileOutputStream fos = new FileOutputStream(headFile);
			headBitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return headFile;
	}
	
	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
