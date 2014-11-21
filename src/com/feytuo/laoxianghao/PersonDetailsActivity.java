package com.feytuo.laoxianghao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feytuo.chat.widget.PasteEditText;

public class PersonDetailsActivity extends Activity {
	private RelativeLayout personHeadRela;// 改变头像
	private ImageButton personDetailsHeadImg;
	private AlertDialog dialog;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	private PasteEditText nickmEditText;// 设置昵称的edit
	private RelativeLayout nickrela;// 设置昵称的底部横线
	private PasteEditText visamEditText;// 设置签名的edit
	private RelativeLayout visarela;// 设置签名的底部横线
	private TextView personDetailsHomeText;//修改家乡

	File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());
	private int crop = 180;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_details_activity);
		initview();

	}

	public void initview()
	{
		personDetailsHeadImg = (ImageButton) findViewById(R.id.person_details_head_img);
		personDetailsHomeText=(TextView)findViewById(R.id.person_details_home_text);
		// //昵称
		nickmEditText = (PasteEditText) findViewById(R.id.person_nick_edit);
		nickrela = (RelativeLayout) findViewById(R.id.edittext_nick_rela);
		nickmEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					nickrela.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					nickrela.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});

		// //个性签名
		visamEditText = (PasteEditText) findViewById(R.id.person_visa_edit);
		visarela = (RelativeLayout) findViewById(R.id.edittext_visa_rela);
		visamEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					visarela.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					visarela.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});
		personHeadRela = (RelativeLayout) findViewById(R.id.person_head_rela);
		personHeadRela.setOnClickListener(new onlistener());
	}
	
	//点击修改完成按钮
	public void updateInfoSuccess(View v)
	{
		
		nickmEditText.getText().toString();
		visamEditText.getText().toString();
		personDetailsHomeText.getText().toString();
	}
	
	
	class onlistener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.person_head_rela:
				if (dialog == null) {
					dialog = new AlertDialog.Builder(PersonDetailsActivity.this)
							.setItems(new String[] { "相机", "相册" },
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (which == 0) {
												Intent intent = new Intent(
														MediaStore.ACTION_IMAGE_CAPTURE);
												intent.putExtra(
														MediaStore.EXTRA_OUTPUT,
														Uri.fromFile(tempFile));
												Log.e("file",
														tempFile.toString());
												startActivityForResult(intent,
														PHOTO_REQUEST_TAKEPHOTO);

											} else {
												Intent intent = new Intent(
														Intent.ACTION_PICK,
														null);
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

			default:
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		super.onActivityResult(requestCode, resultCode, data);

	}

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

	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			personDetailsHeadImg.setBackgroundDrawable(drawable);
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public void personDetailsRetImg(View v) {
		finish();
	}
}
