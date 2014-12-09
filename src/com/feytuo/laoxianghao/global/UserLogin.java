package com.feytuo.laoxianghao.global;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
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
import com.feytuo.laoxianghao.SelsectedCountry;
import com.feytuo.laoxianghao.SimpleSelsectedCountry;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.util.GetSystemDateTime;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.util.StringTools;
import com.feytuo.laoxianghao.view.OnloadDialog;

public class UserLogin {

	private static LXHUser gUser = null;
	private OnloadDialog pd;
	private boolean progressShow;
	private LXHUserDao userDao;

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
		userDao = new LXHUserDao(context);
		// 0、上传头像文件，获取头像文件地址
		uploadHeadFile(context, uName, uKey, nickName, headBitmap);
		// 1、检查是否存在该用户，不存在则添加用户，反之更新用户
		// 2、上传用户基本信息
		// 3、注册环信服务器
		// 4、登录环信服务器
	}

	/**
	 * 0、上传头像文件，获取头像文件地址
	 * 
	 * @param context
	 * @param uName
	 * @param uKey
	 * @param nickName
	 * @param headBitmap
	 */
	private void uploadHeadFile(final Context context, final String uName,
			final String uKey, final String nickName, final Bitmap headBitmap) {
		// TODO Auto-generated method stub
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				pd = new OnloadDialog(context);
				pd.setCanceledOnTouchOutside(false);
				pd.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						progressShow = false;
					}
				});
				pd.show();
				pd.setMessage("正在获取登录信息...");
			}
		});

		File file = saveBitmap2File(context, headBitmap);
		if (file != null && file.exists()) {
			final BmobFile bmobFile = new BmobFile(file);
			progressShow = true;
			bmobFile.uploadblock(context, new UploadFileListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					if (!progressShow) {
						Log.i("UserLogin", "提示框不见了！");
						return;
					}
					// 判断是否存在该用户
					judgeUserExist(context, uName, uKey, nickName,
							bmobFile.getFileUrl());
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					pd.dismiss();
					Toast.makeText(context, "网络或服务器有问题，请稍候再试...",
							Toast.LENGTH_SHORT).show();
					Log.i("UserLogin", "保存登录信息失败：" + arg1);
				}
			});
		}
	}

	private void judgeUserExist(final Context context, final String uName,
			final String uKey, final String nickName, final String headUrl) {
		// TODO Auto-generated method stub
		progressShow = true;
		// 判断是否在数据库中有该用户
		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
		query.addWhereEqualTo("uName", uName);
		query.findObjects(context, new FindListener<LXHUser>() {
			@Override
			public void onSuccess(List<LXHUser> arg0) {
				// TODO Auto-generated method stub
				if (!progressShow) {
					Log.i("UserLogin", "提示框不见了！");
					return;
				}
				if (arg0.size() > 0 && arg0.get(0) != null) {
					// 如果存在该用户
					// 更新用户信息，然后直接登录环信服务器
					Log.i("UserLogin", "用户已存在");
					updataAndLogin(context, arg0.get(0), nickName, headUrl);
				} else {
					// 如果不存在
					// 获取文件url后上传基本信息，然后注册环信服务器
					Log.i("UserLogin", "用户不存在");
					saveAndLogin(context, uName, uKey, nickName, headUrl);
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (!progressShow) {
					Log.i("UserLogin", "提示框不见了！");
					return;
				}
				Toast.makeText(context, uKey + "登陆失败,请稍候再试...",
						Toast.LENGTH_SHORT).show();
				Log.i("UserLogin", arg1 + "----" + arg0);
			}
		});
	}

	private void updataAndLogin(final Context context, final LXHUser user,
			final String nickName, String headUrl) {
		progressShow = true;
		// TODO Auto-generated method stub
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在更新登录信息...");
			}
		});
		final LXHUser lxhUser = new LXHUser();
		lxhUser.setNickName(nickName);
		lxhUser.setHeadUrl(headUrl);
		lxhUser.setHome("");
		lxhUser.setPersonSign("好好学习,天天乡乡");
		lxhUser.update(context, user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				if (!progressShow) {
					Log.i("UserLogin", "提示框不见了！");
					return;
				}
				// TODO Auto-generated method stub
				Log.i("UserLogin", user.getObjectId() + "--" + user.getuName());
				//保存到本地当前用户表
				lxhUser.setObjectId(user.getObjectId());
				lxhUser.setuName(user.getuName());
				lxhUser.setuKey(user.getuKey());
				userDao.insertCurrentUser(lxhUser);
				loginHX(context, user.getObjectId(), user.getuName(), nickName);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (!progressShow) {
					Log.i("UserLogin", "提示框不见了！");
					return;
				}
				pd.dismiss();
				Toast.makeText(context, user.getuKey() + "更新用户信息失败,请稍后再试...",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 2、上传用户基本信息
	 * 
	 * @param context
	 * @param uName
	 * @param uKey
	 * @param nickName
	 * @param headUrl
	 */
	private void saveAndLogin(final Context context, final String uName,
			final String uKey, final String nickName, String headUrl) {
		progressShow = true;
		// 如果没，则添加用户
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在保存登录信息...");
			}
		});
		final LXHUser user = new LXHUser();
		user.setuName(uName);
		user.setuKey(uKey);
		user.setHeadUrl(headUrl);
		user.setNickName(nickName);
		user.setHome("");
		user.setPersonSign("好好学习,天天乡乡");
		user.save(context, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if (!progressShow) {
					Log.i("UserLogin", "提示框不见了！");
					return;
				}
				//保存到本地当前用户表
				userDao.insertCurrentUser(user);
				// 注册环信服务器
				registerHX(context, user.getObjectId(), uName, nickName);
				// App.pre.edit().putString(Global.USER_ID, user.getObjectId())
				// .commit();
				gUser = user;// 将获取的用户设为全局变量
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (!progressShow) {
					Log.i("UserLogin", "提示框不见了！");
					return;
				}
				pd.dismiss();
				Toast.makeText(context, uKey + "保存用户信息失败,请稍候再试...",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 3、注册环信服务器
	 * 
	 * @param uId
	 * @param pwd
	 */
	protected void registerHX(final Context context, final String username,
			final String pwd, final String nickName) {
		// TODO Auto-generated method stub
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				pd.setMessage("正在注册聊天服务器...");
			}
		});
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(
								username, pwd);
						// 保存用户名
						App.getInstance().setUserName(username);
						// 登录服务器
						loginHX(context, username, pwd, nickName);
					} catch (final Exception e) {
						pd.dismiss();
						((Activity) context).runOnUiThread(new Runnable() {
							public void run() {
								if (e != null && e.getMessage() != null) {
									String errorMsg = e.getMessage();
									if (errorMsg
											.indexOf("EMNetworkUnconnectedException") != -1) {
										Toast.makeText(context, "网络异常，请检查网络！",
												Toast.LENGTH_SHORT).show();
									} else if (errorMsg.indexOf("conflict") != -1) {
										Toast.makeText(context, "用户已存在！",
												Toast.LENGTH_SHORT).show();
									}/*
									 * else if (errorMsg.indexOf(
									 * "not support the capital letters") != -1)
									 * { Toast.makeText(getApplicationContext(),
									 * "用户名不支持大写字母！", 0).show(); }
									 */else {
										Log.i("UserLogin",
												"注册失败: " + e.getMessage());
									}

								} else {
									Toast.makeText(context, "注册失败: 未知异常",
											Toast.LENGTH_SHORT).show();
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
	 * 
	 * @param context
	 * @param username
	 * @param pwd
	 */
	private void loginHX(final Context context, final String username,
			final String pwd, final String nickName) {
		// TODO Auto-generated method stub
		((Activity) context).runOnUiThread(new Runnable() {
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
						Log.i("UserLogin", "提示框不见了！");
						return;
					}
					// 登陆成功，保存用户名密码
					App.getInstance().setUserName(username);
					App.getInstance().setPassword(pwd);
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							pd.setMessage("正在获取好友和群聊列表...");
						}
					});
					try {
						// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
						List<String> usernames = EMContactManager.getInstance()
								.getContactUserNames();
						// 获取所有人的昵称和头像url
						getUserInfoFromBmob(context, usernames, username,
								nickName);
					} catch (Exception e) {
						Log.i("UserLogin", "用户名或昵称获取失败");
						pd.dismiss();
						e.printStackTrace();
					}
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(final int code, final String message) {
					if (!progressShow) {
						return;
					}
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							Log.i("UserLogin", "登录失败: " + code +message);
							if(code == -1005 && "用户名或密码错误".equals(message)){
								registerHX(context, username, pwd, nickName);
							}else{
								pd.dismiss();
								Toast.makeText(context, "登录失败",Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		} else {
			// 用户名或者密码为空
		}
	}

	/**
	 * 获取用户的昵称和headUrl
	 * @param context
	 * @param usernames
	 * @param username
	 * @param nickName
	 */
	protected void getUserInfoFromBmob(final Context context,
			final List<String> usernames, final String username,
			final String nickName) {
		// TODO Auto-generated method stub
		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
		query.addWhereContainedIn("objectId", usernames);
		query.addQueryKeys("objectId,nickName,headUrl");
		query.findObjects(context, new FindListener<LXHUser>() {

			@Override
			public void onSuccess(List<LXHUser> arg0) {
				// TODO Auto-generated method stub
				try {
					Map<String, User> userlist = new HashMap<String, User>();
					// Log.i("UserLogin", "服务器好友有："+usernames.size());
					for (int i = 0; i < usernames.size(); i++) {
						User user = new User();
						String userName = arg0.get(i).getObjectId();
						user.setUsername(userName);
						user.setNickName(arg0.get(i).getNickName());
						user.setHeadUrl(arg0.get(i).getHeadUrl());
						setUserHearder(userName, user);
						userlist.put(userName, user);
						Log.i("UserLogin", "添加了服务器好友：" + userName
								+ "---" + arg0.get(i).getNickName()+"---"+arg0.get(i).getHeadUrl());
					}
//					// 添加user"申请与通知"
//					User newFriends = new User();
//					newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
//					newFriends.setNick("申请与通知");
//					newFriends.setHeader("");
//					userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
//					// 添加"群聊"
//					User groupUser = new User();
//					groupUser.setUsername(Constant.GROUP_USERNAME);
//					groupUser.setNick("群聊");
//					groupUser.setHeader("");
//					userlist.put(Constant.GROUP_USERNAME, groupUser);
//
//					// 存入内存
//					App.getInstance().setContactList(userlist);
					// 存入db
					UserDao dao = new UserDao(context);
					List<User> users = new ArrayList<User>(userlist.values());
					dao.saveContactList(users);

					// 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
					EMGroupManager.getInstance().getGroupsFromServer();
				} catch (Exception e) {
					pd.dismiss();
					e.printStackTrace();
				}
				boolean updatenick = EMChatManager.getInstance()
						.updateCurrentUserNick(nickName);
				if (!updatenick) {
					EMLog.e("UserLogin", "update current user nick fail");
				}

				// 登录成功，设置登录成功标示
				App.pre.edit().putString(Global.USER_ID, username).commit();
				// 注册聊天广播和监听,初始化联系人列表
				if (context instanceof SimpleSelsectedCountry) {
//					((MainActivity) context).registerHXListeners();
//					if (((MainActivity) context).cacFragment != null
//							&& ((MainActivity) context).cacFragment
//									.getContactListFragment() != null
//							&& ((MainActivity) context).cacFragment
//									.getContactListFragment()
//									.isActivityCreated()) {
//						((Activity) context).runOnUiThread(new Runnable() {
//							public void run() {
//								((MainActivity) context).cacFragment
//										.getContactListFragment().initView();
//							}
//						});
//					}
					pd.progressFinish("登录成功");
					
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pd.dismiss();
							Intent intent = new Intent();
							intent.putExtra("isfromtocity", 0);// 判断是从那里进入的城市选择
							intent.setClass(context, SelsectedCountry.class);
							context.startActivity(intent);
							((SimpleSelsectedCountry)context).finish();
						}
					}, 1000l);
					
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("UserLogin", "昵称获取失败");
				pd.dismiss();
			}
		});
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
		String filePath = SDcardTools.getSDPath() + "/" + "laoxianghaoAudio";
		// 实例化文件夹
		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdirs();
		}

		// 保存文件名
		String fileName = "/head" + GetSystemDateTime.now()
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
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
