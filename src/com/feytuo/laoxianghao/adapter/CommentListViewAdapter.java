package com.feytuo.laoxianghao.adapter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.v3.listener.UpdateListener;

import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.dao.LXHUserDao;
import com.feytuo.laoxianghao.dao.PraiseDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.domain.LXHUser;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.CommonUtils;
import com.feytuo.laoxianghao.util.ImageLoader;
import com.feytuo.laoxianghao.util.NetUtil;
import com.feytuo.laoxianghao.view.MyDialog;

/**
 * 
 * @author feytuo
 * 
 * 
 */
public class CommentListViewAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater m_Inflater;
	private List<? extends Map<String, ?>> data;// 声明List容器对象
	private Invitation inv;// 帖子id
	final int VIEW_TYPE = 2;// 一共是使用2个布局
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	private MediaPlayer mp = new MediaPlayer();

	private SparseArray<Boolean> isAudioPlayArray;
	private boolean isPraised;
	
	private LXHUserDao userDao;
	private CityDao cityDao;
	private ImageLoader mImageLoader;

	public CommentListViewAdapter(Context context,
			List<? extends Map<String, ?>> data, Invitation inv) {
		this.inv = inv;
		this.data = data;
		this.context = context;
		m_Inflater = LayoutInflater.from(context);
		isPraised = false;
		isAudioPlayArray = new SparseArray<>();
		userDao = new LXHUserDao(context);
		cityDao = new CityDao(context);
		mImageLoader = new ImageLoader();
		// LayoutInflater作用是将layout的xml布局文件实例化为View类对象。
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size() + 1;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		Object o = null;
		if (arg0 == 0) {
			o = inv;
		} else {
			o = data.get(arg0 - 1);
		}
		return o;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;

	}

	// getItemViewType(int) – 根据position返回相应的Item
	public int getItemViewType(int position) {
		int p = position;
		if (p == 0) {
			return TYPE_1;
		} else {
			return TYPE_2;
		}
	}

	// getViewTypeCount() – 该方法返回多少个不同的布局
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return VIEW_TYPE;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder1 holder1 = null;
		viewHolder2 holder2 = null;
		int type = getItemViewType(position);// 判断使用的布局
		// 如果没有创建布局
		switch (type) {
		case TYPE_1:
			if (convertView == null) {
				convertView = m_Inflater.inflate(R.layout.index_listview,
						null);// 同样是将布局转化成view
				holder1 = new viewHolder1();
				holder1.indexSupportLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_support_linerlayout);

				holder1.indexShareLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_share_linerlayout);
				holder1.indexProgressbarBtn = (ImageButton) convertView
						.findViewById(R.id.index_progressbar_btn);

				holder1.titleImage = (ImageView) convertView
						.findViewById(R.id.title_img_id);
				holder1.supportImg = (ImageView) convertView
						.findViewById(R.id.support_img);
				holder1.commentImg = (ImageView) convertView
						.findViewById(R.id.comment_img);
				holder1.personHeadImg = (ImageButton) convertView
						.findViewById(R.id.index_user_head);
				holder1.personUserNick = (TextView) convertView
						.findViewById(R.id.index_user_nick);
				holder1.home = (TextView) convertView
						.findViewById(R.id.index_home_textview);
				holder1.indexSupportNum = (TextView) convertView
						.findViewById(R.id.index_support_num);
				holder1.indexCommentNum = (TextView) convertView
						.findViewById(R.id.index_comment_num);
				holder1.indexProgressbarTime = (TextView) convertView
						.findViewById(R.id.index_progressbar_time);

				holder1.indexTextDescribe = (TextView) convertView
						.findViewById(R.id.index_text_describe);
				holder1.indexLocalsCountry = (TextView) convertView
						.findViewById(R.id.index_locals_country);
				holder1.indexLocalsTime = (TextView) convertView
						.findViewById(R.id.index_locals_time);
				convertView.setTag(holder1);

				// View中的setTag(Onbect)表示给View添加一个格外的数据,以后可以用getTag()将这个数据取出来
			} else {
				holder1 = (viewHolder1) convertView.getTag();
			}
			break;
		case TYPE_2:
			// if (ViewArray.get(position) == null) {
			if (convertView == null) {
				convertView = m_Inflater.inflate(R.layout.comment_item, null);
				// 实例化holder以及控件
				holder2 = new viewHolder2();
				holder2.commentFloor = (TextView) convertView
						.findViewById(R.id.comment_floor);
				holder2.commentPosition = (TextView) convertView
						.findViewById(R.id.comment_position);
				holder2.commentTime = (TextView) convertView
						.findViewById(R.id.comment_time);
				holder2.commentTextContext = (TextView) convertView
						.findViewById(R.id.comment_text_context);
				holder2.commentPlayId = (Button) convertView
						.findViewById(R.id.comment_play_btn);
				convertView.setTag(holder2);
				// 用容器将已经实例化的convertView保存用于获取里面给的控件
			} else {
				holder2 = (viewHolder2) convertView.getTag();
			}
			break;

		}
		// 可以对控件进行操作
		switch (type) {
		case TYPE_1:
			setcontent(holder1, inv);
			setSubBtn(holder1, inv);
			Listener listener = new Listener(holder1);
			holder1.indexSupportLinerlayout.setOnClickListener(listener);
			holder1.indexShareLinerlayout.setOnClickListener(listener);
			// holder1.indexProgressbarBtn.setOnClickListener(listener);
			holder1.indexProgressbarBtn.setOnClickListener(listener);
			break;
		case TYPE_2:
			setAudioPlayBtn(holder2.commentPlayId, position);
			Map<String, ?> map = data.get(position - 1);
			holder2.commentFloor.setText(Integer.toString(position) + "楼");
			holder2.commentPosition.setText(map.get("com_position") + "");
			holder2.commentTime.setText(map.get("com_time") + "");
			holder2.commentTextContext.setText(map.get("com_words") + "");
			holder2.commentPlayId
					.setOnClickListener(new AudioListener(position));
			break;
		}
		return convertView;

	}

	private void setAudioPlayBtn(Button btn, int position) {
		// 播放时的修改
		if (isAudioPlayArray.get(position, false)) {
			btn.setBackgroundResource(R.drawable.comment_audio_play);
		} else {
			btn.setBackgroundResource(R.drawable.comment_audio_selector);
		}
		// 初始化时
		if ("".equals(data.get(position - 1).get("com_voice").toString())) {
			btn.setVisibility(View.INVISIBLE);
		} else {
			btn.setVisibility(View.VISIBLE);
		}
	}

	private void setcontent(viewHolder1 holder, Invitation inv) {
		// TODO Auto-generated method stub
		holder.indexTextDescribe.setText(inv.getWords());
		holder.indexLocalsCountry.setText(inv.getPosition());
		holder.indexLocalsTime.setText(inv.getTime());
		//地方话
		holder.home.setText(cityDao.getCityNameById(inv.getHome())+"话");
		holder.indexProgressbarTime.setText(inv.getVoiceDuration() + "s");
		if (1 == inv.getIsHot()) {
			holder.titleImage.setVisibility(View.GONE);
			holder.indexLocalsCountry.setVisibility(View.GONE);
			holder.home.setVisibility(View.GONE);
			// 设置头像、昵称
			holder.personUserNick.setText("乡乡话题");
			CommonUtils.corner(context, R.drawable.ic_launcher, holder.personHeadImg);
		} else {
			holder.titleImage.setVisibility(View.VISIBLE);
			holder.indexLocalsCountry.setVisibility(View.VISIBLE);
			holder.home.setVisibility(View.VISIBLE);
			holder.titleImage.setBackgroundResource(R.drawable.geographical);
			holder.indexLocalsCountry.setTextColor(context.getResources()
					.getColor(R.color.indexbg));
			// 设置头像、昵称
			setUserInfo(inv.getuId(),holder.personUserNick,holder.personHeadImg);
		}
		if (inv.getPraiseNum() > 0) {
			holder.indexSupportNum.setText(inv.getPraiseNum() + "");
		} else {
			holder.indexSupportNum.setText("赞");
		}
		if (inv.getCommentNum() > 0) {
			holder.indexCommentNum.setText(inv.getCommentNum() + "");
		} else {
			holder.indexCommentNum.setText("评论");
		}
		holder.commentImg.setBackgroundResource(R.drawable.comment_no);
	}

	/**
	 * 设置item的用户昵称
	 * @param userName
	 * @param nameTV
	 * @param personHeadImg 
	 */
	public void setUserInfo(String uId ,TextView nameTV, ImageButton personHeadImg){
		LXHUser user = userDao.getNickAndHeadByUid(uId);
		if(user != null){//如果本地数据库存在该用户
			nameTV.setText(user.getNickName());
			mImageLoader.loadCornerImage(context,user.getHeadUrl(), this, personHeadImg);
		}
	}
	// 设置点赞等图片按钮
	private void setSubBtn(viewHolder1 holder, Invitation inv) {
		String invId = inv.getObjectId();
		String uId = App.pre.getString(Global.USER_ID, "");
		// 判断该帖子是否在点赞表里
		if (isPraised(invId, uId)) {
			holder.supportImg.setBackgroundResource(R.drawable.support_press);
			isPraised = true;
		} else {
			holder.supportImg.setBackgroundResource(R.drawable.support_no);
			isPraised = false;
		}
	}

	// 是否已经点赞
	private boolean isPraised(String invId, String uId) {
		return new PraiseDao(context).selectPraiseInvitation(invId, uId);
	}


	class AudioListener implements OnClickListener {
		private int position;

		public AudioListener(int position) {
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.comment_play_btn:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					Log.i("playAudioComment", "点击了:" + position);
					if (lastPosition == position) {
						if (!isPlay) {
							playAudio(v, position);// 播放语音
						} else {
							stopAudio(v, position);
						}
					} else {
						playAudio(v, position);// 播放语音
					}
				}
				break;

			default:
				break;
			}

		}

	}

	private int lastPosition;
	private boolean isPlay = false;
	private View lastView;

	// 播放已经录好的音
	private void playAudio(final View v, final int position) {
		stopInvitationAudio();//当播放评论录音时，确保帖子录音时关闭的
		stopAudio(lastView, lastPosition);
		lastView = v;
		lastPosition = position;
		isPlay = true;
		// 解决按钮状态也被重用的问题
		isAudioPlayArray.put(position, true);
		v.setBackgroundResource(R.drawable.comment_audio_play);
		String fileUrl = data.get(position - 1).get("com_voice").toString();
		// 点击播放而已
		try {
			mp.reset();
			mp.setDataSource(fileUrl);
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
				}
			});
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					stopAudio(v, position);
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopAudio() {
		stopAudio(lastView, lastPosition);
	}

	private void stopAudio(View v, int position) {
		// TODO Auto-generated method stub
		isPlay = false;
		if (v != null) {
			v.setBackgroundResource(R.drawable.comment_audio_selector);
		}
		isAudioPlayArray.put(position, false);
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}
	}

	// viewHolder把你getView方法中每次返回的View存起来，可以下次再用。
	// 这样做的好处就是不必每次都到布局文件中去拿到你的View，提高了效率
	class viewHolder1 {
		private LinearLayout indexSupportLinerlayout;// 赞
		private LinearLayout indexShareLinerlayout;// 分享
		private ImageView titleImage;// 热门/地理位置图标
		private ImageView supportImg;// 点赞的图标
		private ImageButton personHeadImg;// 头像
		private TextView personUserNick;//昵称
		private TextView home;//地方话
		private TextView indexSupportNum;// 点赞数
		private TextView indexCommentNum;// 评论数
		private ImageView commentImg;// 评论的图标
		private ImageButton indexProgressbarBtn;// 在进度条中的播放停止按钮
		private TextView indexProgressbarTime;
		private TextView indexTextDescribe;// 帖子内容文字
		private TextView indexLocalsCountry;// 帖子内容城市
		private TextView indexLocalsTime;// 帖子内容时间
	}

	class viewHolder2 {
		TextView commentFloor;// 楼层
		TextView commentPosition; // 评论的用户所在的位置
		TextView commentTime; // 评论的时间
		TextView commentTextContext; // 评论的文字内容
		Button commentPlayId;// 语言的按钮
	}

	class Listener implements OnClickListener {

		private viewHolder1 holder;

		public Listener(viewHolder1 holder1) {
			this.holder = holder1;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.index_share_linerlayout:
				Dialog dialog = new MyDialog(context, inv, R.style.MyDialog);
				dialog.show();
				break;
			case R.id.index_support_linerlayout:
				// 主列表需要刷新
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					App.pre.edit()
							.putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, true)
							.commit();
					dealSupportBtn(holder);
				}
				break;
			case R.id.index_progressbar_btn:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					if (!isInvitationPlay) {
						playInvitationAudio(holder);// 播放语音
					} else {
						stopInvitationAudio(holder);
					}
				}
				break;
			default:
				break;
			}
		}

	}


	public void dealSupportBtn(viewHolder1 holder) {
		// TODO Auto-generated method stub
		// 点赞+1的动态效果
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.nn);
		if (!isPraised) {// 点赞
			holder.supportImg.startAnimation(animation);
			holder.supportImg.setBackgroundResource(R.drawable.support_press);
			String number = holder.indexSupportNum.getText().toString();
			if ("赞".equals(number)) {
				holder.indexSupportNum.setText("1");
			} else {
				holder.indexSupportNum.setText((Integer.parseInt(number) + 1)
						+ "");
			}
			isPraised = true;
			addPraise();
		} else {// 取消点赞
			holder.supportImg.setBackgroundResource(R.drawable.support_no);
			String number = holder.indexSupportNum.getText().toString();
			if ("1".equals(number)) {
				holder.indexSupportNum.setText("赞");
			} else if ("赞".equals(number)) {
				holder.indexSupportNum.setText("赞");
			} else {
				holder.indexSupportNum.setText((Integer.parseInt(number) - 1)
						+ "");
			}
			isPraised = false;
			deletePraise();
		}
	}

	// 向服务器和本地数据库添加点赞数据
	private void addPraise() {
		// TODO Auto-generated method stub
		// 该贴点赞数+1
		Invitation invitation = new Invitation();
		invitation.increment("praiseNum", 1);
		invitation.update(context, inv.getObjectId(), new UpdateListener() {
			@Override
			public void onSuccess() {
				int praiseNum = inv.getPraiseNum();
				inv.setPraiseNum(praiseNum + 1);
				// Toast.makeText(context, "点赞数+1", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// Toast.makeText(context, "点赞数+1失败",
				// Toast.LENGTH_SHORT).show();
			}
		});

		new PraiseDao(context).insertPraise(
				App.pre.getString(Global.USER_ID, ""), inv.getObjectId());
	}

	private void deletePraise() {
		// TODO Auto-generated method stub
		// 该贴点赞数-1
		Invitation invitation = new Invitation();
		invitation.increment("praiseNum", -1);
		invitation.update(context, inv.getObjectId(), new UpdateListener() {
			@Override
			public void onSuccess() {
				int praiseNum = inv.getPraiseNum();
				inv.setPraiseNum(praiseNum - 1);
				// Toast.makeText(context, "点赞数-1", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// Toast.makeText(context, "点赞数-1失败",
				// Toast.LENGTH_SHORT).show();
			}
		});

		PraiseDao praiseDao = new PraiseDao(context);
		praiseDao.deletePraise(inv.getObjectId(),
				App.pre.getString(Global.USER_ID, ""));
	}

	private boolean isInvitationPlay;
	private int voiceDuration;
	private MyCount mCountDownTimer;// 当前录音倒计时
	private Timer mProgressTimer;// 当前进度条进度计时
	private int mCount = 0;// 进度条度数
	private MediaPlayer mMeidaPlayer;
	private viewHolder1 mHolder1;// 记录前一个holder，在停止时调用

	private void playInvitationAudio(viewHolder1 holder) {
		// TODO Auto-generated method stub
		stopAudio();//播放帖子录音时确保评论录音已经关闭
		stopInvitationAudio(mHolder1);
		// 重新获得
		mHolder1 = holder;
		mHolder1.indexProgressbarBtn
				.setBackgroundResource(R.drawable.pause_ico);
		isInvitationPlay = true;
		String audioUrl = inv.getVoice();
		voiceDuration = inv.getVoiceDuration();
		// 点击播放而已
		try {
			mMeidaPlayer.reset();
			mMeidaPlayer.setDataSource(audioUrl);
			mMeidaPlayer.prepareAsync();
			mMeidaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					// 实现倒计时
					mCountDownTimer = new MyCount((voiceDuration) * 1000 + 50,
							1000);
					mCountDownTimer.start();
					// 显示进度条
					showIndeterDialog(voiceDuration);
				}
			});

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopInvitationAudio() {
		// TODO Auto-generated method stub
		stopInvitationAudio(mHolder1);
	}

	private void stopInvitationAudio(final viewHolder1 holder) {
		// TODO Auto-generated method stub
		// 进度条走完
		mCount = 0;
		isInvitationPlay = false;
		if (mProgressTimer != null) {
			mProgressTimer.cancel();
		}
		if (holder != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
//					// TODO Auto-generated method stub
//					holder.indexProgressbarId.setProgress(0);
				}
			});
			holder.indexProgressbarTime.setText(voiceDuration + "s");
			holder.indexProgressbarBtn
					.setBackgroundResource(R.drawable.play_ico);
		}
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		if (mMeidaPlayer == null) {
			mMeidaPlayer = new MediaPlayer();
		} else {
			if (mMeidaPlayer.isPlaying()) {
				mMeidaPlayer.stop();
			}
		}
	}

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// 完成的时候提示
			mHolder1.indexProgressbarTime.setText(0 + "s");

		}

		@Override
		public void onTick(long millisUntilFinished) {
			// Log.i("countdown", millisUntilFinished + "");
			mHolder1.indexProgressbarTime.setText(millisUntilFinished / 1000
					+ "s");
		}
	}

	// /进度条的处理
	private void showIndeterDialog(int processtime) {
		final int newprocesstime = processtime;
		final int progrocessMax = 1000;;
		mProgressTimer = new Timer();
		mProgressTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mCount <= progrocessMax) {
					mCount++;
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
						}
					});
				} else {
					mHandler.sendEmptyMessage(0);
				}
			}
		}, 0l, newprocesstime);
	}

	/**
	 * Handler消息处理
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// 进度条走完
				mCount = 0;
				mProgressTimer.cancel();
				mHolder1.indexProgressbarTime.setText(voiceDuration + "s");
				mHolder1.indexProgressbarBtn
						.setBackgroundResource(R.drawable.play_ico);
				isInvitationPlay = false;
			}
			super.handleMessage(msg);
		}
	};
}
