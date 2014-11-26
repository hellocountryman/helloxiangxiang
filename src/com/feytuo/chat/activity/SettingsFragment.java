/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feytuo.chat.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.PersonInvitationActivity;
import com.feytuo.laoxianghao.PersonUpdateInfoActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.SelsectedCountry;
import com.feytuo.laoxianghao.SetActivity;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.CommonUtils;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.SDcardTools;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingsFragment extends Fragment {
	private final String TAG = "SettingsFragment";
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	private static final int UPDATE_NICK_NAME = 4;
	private static final int UPDATE_PERSON_SIGN = 5;
	private static final int UPDATE_HOME = 6;
	private final String TEMP_HEAD_IMAGE = "temp_head_mage.png";
	
	private RelativeLayout personNickRela;// 修改昵称
	private TextView personNickText;// 用于显示昵称

	private RelativeLayout personSignRela;// 修改个性签名
	private TextView personSignText;// 用于显示个性签名
	
	private RelativeLayout personHomeRela;//修改家乡
	private TextView personHomeText;//显示家乡

	private RelativeLayout personTieziRela;
	private RelativeLayout personSetRela;

	private ImageView personHeadImg;// 个人中心的头像
	private TextView personHeadNick;// 个人中心头像下面的昵称
	
	private ImageView redPoint;//我的帖子红点
	
	private AlertDialog dialog;
	File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());
	private int crop = 180;
	
	private LXHUser user;
	private ImageLoader mImageLoader;
	private File uploadFile;
	private Bitmap photo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.person_activity, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageLoader = new ImageLoader(getActivity());
		initview();
		setViewContent();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		getMyCommentNotice();
//		setViewContent();
		super.onResume();
	}

	public void initview() {

		redPoint = (ImageView) getView().findViewById(R.id.person_red_point);
		personHeadImg = (ImageView) getView()
				.findViewById(R.id.person_head_img);
		personHeadNick = (TextView) getView().findViewById(
				R.id.person_head_nick);
		personNickText = (TextView)getView().findViewById(R.id.person_nick_text);
		personSignText = (TextView)getView().findViewById(R.id.person_sign_text);
		personHomeText = (TextView)getView().findViewById(R.id.person_home_text);
		personNickRela = (RelativeLayout) getView().findViewById(
				R.id.person_nick_rela);
		personSignRela = (RelativeLayout) getView().findViewById(
				R.id.person_sign_rela);
		personTieziRela = (RelativeLayout) getView().findViewById(
				R.id.person_tiezi_rela);
		personSetRela = (RelativeLayout) getView().findViewById(
				R.id.person_set_rela);
		personHomeRela = (RelativeLayout) getView().findViewById(
				R.id.person_home_rela);

		Linstener linstener = new Linstener();
		personHeadImg.setOnClickListener(linstener);
		personNickRela.setOnClickListener(linstener);
		personSignRela.setOnClickListener(linstener);
		personTieziRela.setOnClickListener(linstener);
		personSetRela.setOnClickListener(linstener);
		personHomeRela.setOnClickListener(linstener);
	}

	private void setViewContent() {
		// TODO Auto-generated method stub
		user = new LXHUserDao(getActivity()).getCurrentUserInfo(App.pre.getString(Global.USER_ID, ""));
		CommonUtils.corner(getActivity(), R.drawable.default_avatar, personHeadImg);// 设置圆角
		if(user != null){
			personHeadNick.setText(user.getNickName());
			personNickText.setText(user.getNickName());
			personSignText.setText(user.getPersonSign());
			Log.i(TAG, "家乡是："+user.getHome());
			personHomeText.setText(user.getHome());
			setHead(user.getuName(), user.getHeadUrl(), personHeadImg);
		}else{
			personHeadNick.setText("");
			personNickText.setText("");
			personSignText.setText("");
			personHomeText.setText("");
		}
	}
	//设置头像
	private void setHead(String userName,String headUrl ,ImageView head_iv) {
		// TODO Auto-generated method stub
		if(headUrl != null && !TextUtils.isEmpty(headUrl)){//如果本地数据库存在该用户
			mImageLoader.loadCornerImage(headUrl, null, head_iv);
		}else{
			getHeadUrlFromBmob(userName,head_iv);
		}
	}

	private void getHeadUrlFromBmob(final String userName,final ImageView head_iv) {
		// TODO Auto-generated method stub
		BmobQuery<LXHUser> query = new BmobQuery<LXHUser>();
		query.addWhereEqualTo("objectId", userName);
		query.addQueryKeys("headUrl");
		query.findObjects(getActivity(), new FindListener<LXHUser>() {
			
			@Override
			public void onSuccess(List<LXHUser> arg0) {
				// TODO Auto-generated method stub
				if(arg0.size() > 0 && !TextUtils.isEmpty(arg0.get(0).getHeadUrl())){
					mImageLoader.loadImage(arg0.get(0).getHeadUrl(), null, head_iv);
					//更新本地数据库
					new LXHUserDao(getActivity()).updateUserHeadUrl(userName, arg0.get(0).getHeadUrl());
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i(TAG, "查找头像url失败："+arg1);
			}
		});
	}
	class Linstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.person_head_img:
				// 弹出对话框，选择照相还是相册
				if (dialog == null) {
					dialog = new AlertDialog.Builder(getActivity()).setItems(
							new String[] { "相机", "相册" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										Intent intent = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										intent.putExtra(
												MediaStore.EXTRA_OUTPUT,
												Uri.fromFile(tempFile));
										Log.e("file", tempFile.toString());
										startActivityForResult(intent,
												PHOTO_REQUEST_TAKEPHOTO);

									} else {
										Intent intent = new Intent(
												Intent.ACTION_PICK, null);
										intent.setDataAndType(
												MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
												"image/*");
										startActivityForResult(intent,
												PHOTO_REQUEST_GALLERY);
									}
								}
							}).create();
				}
				if (!dialog.isShowing()) {
					dialog.show();
				}
				break;
			case R.id.person_nick_rela:
				intent.setClass(getActivity(), PersonUpdateInfoActivity.class);
				intent.putExtra("type", "nick");//
				startActivityForResult(intent, UPDATE_NICK_NAME);
				break;
			case R.id.person_sign_rela:
				intent.setClass(getActivity(), PersonUpdateInfoActivity.class);
				intent.putExtra("type", "sign");
				startActivityForResult(intent, UPDATE_PERSON_SIGN);
				break;
			case R.id.person_tiezi_rela:
				redPoint.setVisibility(View.GONE);
				intent.setClass(getActivity(), PersonInvitationActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.person_set_rela:
				intent.setClass(getActivity(), SetActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.person_home_rela:
				intent.setClass(getActivity(), SelsectedCountry.class);
				intent.putExtra("isfromtocity", 1);
				startActivityForResult(intent, UPDATE_HOME);
				break;
			default:
				break;
			}
		}

	}

	// 接收data返回的值
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			Log.e("zoom", "begin2");
			if (data != null)
				setPicToView(data);
			break;
		}
		if(resultCode == Global.RESULT_OK){
			String resultData = data.getStringExtra("data").toString().trim();
			switch(requestCode){
			case UPDATE_NICK_NAME:
				personNickText.setText(resultData);
				personHeadNick.setText(resultData);
				break;
			case UPDATE_PERSON_SIGN:
				personSignText.setText(resultData);
				break;
			case UPDATE_HOME:
				personHomeText.setText(resultData);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	// 使用默认的裁切工具进行图的裁切
	private void startPhotoZoom(Uri uri, int size) {
		Log.e("zoom", "begin");
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", crop);// 输出图片大小
		intent.putExtra("outputY", crop);
		intent.putExtra("return-data", true);
		Log.e("zoom", "begin1");
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private ProgressDialog pd;
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			pd = new ProgressDialog(getActivity());
			pd.setMessage("正在上传头像...");
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			photo = bundle.getParcelable("data");
//			Drawable drawable = new BitmapDrawable(photo);
//			personHeadImg.setBackgroundDrawable(drawable);
			if (!SDcardTools.isHaveSDcard()) {
				Toast.makeText(getActivity(), "请插入SD卡以便存储头像",
						Toast.LENGTH_LONG).show();
				return;
			}
			File dir = new File(SDcardTools.getSDPath()+"/laoxianghaoAudio");
			if(!dir.exists()){
				dir.mkdir();
			}
			uploadFile = new File(SDcardTools.getSDPath()+"/laoxianghaoAudio/"+TEMP_HEAD_IMAGE);
			try {
				FileOutputStream ous = new FileOutputStream(uploadFile);
				photo.compress(CompressFormat.PNG, 100, ous);
				ous.flush();
				ous.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//上传到服务器
			upload2Bmob(uploadFile);
		}
	}

	private void upload2Bmob(File file) {
		// TODO Auto-generated method stub
		if (file != null && file.exists()) {
			final BmobFile bmobFile = new BmobFile(file);
			bmobFile.uploadblock(getActivity(), new UploadFileListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					//更新服务器当前用户头像url
					updateHeadUrl2Bmob(bmobFile.getFileUrl());
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "网络或服务器有问题，请稍候再试...",
							Toast.LENGTH_SHORT).show();
					Log.i("SettingsFragment", "上传头像失败：" + arg1);
					pd.dismiss();
				}
			});
		}
	}

	private void updateHeadUrl2Bmob(final String fileUrl) {
		// TODO Auto-generated method stub
		if (pd != null && pd.isShowing()) {
			pd.setMessage("正在更新服务器信息...");
		}
		final String userId = App.pre.getString(Global.USER_ID, "");
		LXHUser user = new LXHUser();
		user.setHeadUrl(fileUrl);
		user.update(getActivity(),userId, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(uploadFile.exists()){
					uploadFile.delete();
					uploadFile = null;
				}
				new LXHUserDao(getActivity()).updateUserHeadUrl(userId, fileUrl);
				//设置头像
				CommonUtils.corner(getActivity(), photo, personHeadImg);
				pd.dismiss();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "网络或服务器有问题，请稍候再试...",
						Toast.LENGTH_SHORT).show();
				Log.i("UserLogin", "上传头像失败：" + arg1);
				pd.dismiss();
			}
		});
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
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
		new InvitationDao(getActivity()).getAllCommentNum(localCommentNum, myInvIds);

		BmobQuery<Invitation> query = new BmobQuery<Invitation>();
		query.addWhereContainedIn("objectId", myInvIds);
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
						redPoint.setVisibility(View.VISIBLE);
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
}
