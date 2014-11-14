package com.feytuo.laoxianghao.view;

import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.feytuo.chat.activity.MainActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.domain.Invitation;

public class MyDialog extends Dialog {
	Context context;
	private final String targetUrl = "http://182.254.140.92:8080/xiangxiang/xiangxiang.apk";// app下载地址
	private final int imageResource = R.drawable.ic_launcher;
	private final String imageUrl = "http://182.254.140.92:8080/xiangxiang/share/ic_launcher.png";
	private final String voiceTitle = "乡乡,熟悉的才是好玩的";
	private final String voiceDes = "远在异乡的你，是否怀念家乡的声音？";
	private String words;// 分享的文字内容
	private String audioUrl;// 分享的声音

	private ImageView shareWeixinArea;
	private ImageView shareSina;
	private ImageView shareWeixinFriend;
	private ImageView shareQQFriend;
	private ImageView shareQzone;
	private ImageView shareSms;

	public MyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public MyDialog(Context context, Map<String, Object> map, int theme) {
		super(context, theme);
		this.context = context;
		this.words = map.get("words") + "";
		this.audioUrl = map.get("voice") + "";
	}

	public MyDialog(Context context, Invitation inv, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.context = context;
		this.words = inv.getWords();
		this.audioUrl = inv.getVoice();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);
		shareWeixinArea = (ImageView) findViewById(R.id.share_weixin_area);
		shareSina = (ImageView) findViewById(R.id.share_sina);
		shareWeixinFriend = (ImageView) findViewById(R.id.share_weixin_friend);
		shareQQFriend = (ImageView) findViewById(R.id.share_qq_friend);
		shareQzone = (ImageView) findViewById(R.id.share_qzone);
		shareSms = (ImageView) findViewById(R.id.share_sms);

		shareWeixinArea.setOnClickListener(loginListener);
		shareSina.setOnClickListener(loginListener);
		shareWeixinFriend.setOnClickListener(loginListener);
		shareQQFriend.setOnClickListener(loginListener);
		shareQzone.setOnClickListener(loginListener);
		shareSms.setOnClickListener(loginListener);

	}

	private View.OnClickListener loginListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.share_weixin_area:
				toFriendGroup();
				break;
			case R.id.share_sina:
				shareSina();
				break;
			case R.id.share_weixin_friend:
				toWeixinFriend();
				break;
			case R.id.share_qq_friend:
				shareQFriend();
				break;
			case R.id.share_qzone:
				shareQzone();
				break;
			case R.id.share_sms:
				shareSms();
				break;
			}
			if (isShowing()) {
				dismiss();
			}
		}

	};

	// 新浪分享
	private void shareSina() {
		// TODO Auto-generated method stub
		// String voiceUrl =
		// "http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
		if (MainActivity.shareWeibo.getmWeiboShareAPI().isWeiboAppInstalled()) {
			// 安装了客户端正常分享
			MainActivity.shareWeibo.sendMessage(words, targetUrl,
					imageResource, voiceTitle, voiceDes, audioUrl,
					imageResource);
		} else {
			// 未安装客户端调用openapi分享
			MainActivity.shareWeibo.sendMessage(words + targetUrl,
					imageResource);
		}
	}

	private void shareQzone() {
		// TODO Auto-generated method stub
		MainActivity.shareQQ.shareToQQOrQzone(words,voiceTitle, targetUrl,
				imageUrl, audioUrl, 1);
	}

	// 分享QQ好友
	private void shareQFriend() {
		// TODO Auto-generated method stub
		MainActivity.shareQQ.shareToQQOrQzone(words,voiceTitle, targetUrl,
				imageUrl, audioUrl, 2);
	}

	/**
	 * 分享给微信好友
	 * 
	 * @param v
	 */
	private void toWeixinFriend() {
		MainActivity.shareWeixin.wechatShare(0, words,voiceTitle, targetUrl,
				audioUrl, R.drawable.ic_launcher);
	}

	/**
	 * 分享到朋友圈
	 * 
	 * @param v
	 */
	private void toFriendGroup() {
		MainActivity.shareWeixin.wechatShare(1,words,voiceTitle, targetUrl,
				audioUrl, R.drawable.ic_launcher);
	}
	
	//短信分享
	private void shareSms() {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", words + "(分享自 *乡乡* ,身在异乡的你是否怀念家乡话的味道？)"+targetUrl);//短信内容
		context.startActivity(intent);
	}
}