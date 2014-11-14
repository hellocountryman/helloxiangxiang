package com.feytuo.laoxianghao.adapter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.UpdateListener;

import com.feytuo.chat.activity.MainActivity;
import com.feytuo.laoxianghao.App;
import com.feytuo.laoxianghao.CommentActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.dao.PraiseDao;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.global.HeadImageChoose;
import com.feytuo.laoxianghao.util.NetUtil;
import com.feytuo.laoxianghao.util.StringTools;
import com.feytuo.laoxianghao.view.MyDialog;

/**
 * 
 * @author feytuo
 * 
 */
@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class ListViewAdapter extends SimpleAdapter {
	private Context context;
	private LayoutInflater m_Inflater;
	private int resource;
	private List<Map<String, Object>> list;// 声明List容器对象
	private SparseArray<Boolean> praiseMap; // 标记点赞对象
	private SparseArray<Boolean> collectionMap;// 标记收藏对象
	private SparseArray<Boolean> isAudioPlayArray;// 记录是否正在播放音乐
	private boolean isCurrentItemAudioPlay;

	public ListViewAdapter(Context context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.list = data;
		this.context = context;
		this.resource = resource;
		m_Inflater = LayoutInflater.from(context);
		praiseMap = new SparseArray<>();
		collectionMap = new SparseArray<>();
		isAudioPlayArray = new SparseArray<>();
	}

	@Override
	// 获取listitem下面的view的值
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// ViewHolder不是Android的开发API，而是一种设计方法，就是设计个静态类，缓存一下，省得Listview更新的时候，还要重新操作。
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = m_Inflater.inflate(resource, null);

			if (resource == R.layout.index_listview_copy) {

				holder.indexSupportLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_support_linerlayout);
				holder.indexCommentLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_comment_linerlayout);
				holder.indexCollectLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_collect_linerlayout);
				holder.indexShareLinerlayout = (LinearLayout) convertView
						.findViewById(R.id.index_share_linerlayout);
				holder.AudioLinearlayout = (LinearLayout) convertView
						.findViewById(R.id.audio_linearlayout);
				holder.audioSpaceTextView1 = (TextView) convertView
						.findViewById(R.id.audio_space_textview_1);
				holder.audioSpaceTextView2 = (TextView) convertView
						.findViewById(R.id.audio_space_textview_2);
				holder.indexProgressbarBtn = (ImageButton) convertView
						.findViewById(R.id.index_progressbar_btn);

				holder.titleImage = (ImageView) convertView
						.findViewById(R.id.title_img_id);
				holder.indexProgressbarId = (ProgressBar) convertView
						.findViewById(R.id.index_progressbar_id);
				holder.supportImg = (ImageView) convertView
						.findViewById(R.id.support_img);
				holder.commentImg = (ImageView) convertView
						.findViewById(R.id.comment_img);
				holder.personHeadImg = (ImageView) convertView
						.findViewById(R.id.person_head_img);
				holder.indexSupportNum = (TextView) convertView
						.findViewById(R.id.index_support_num);
				holder.indexCommentNum = (TextView) convertView
						.findViewById(R.id.index_comment_num);
				holder.collectImg = (ImageView) convertView
						.findViewById(R.id.collect_img);
				holder.indexProgressbarTime = (TextView) convertView
						.findViewById(R.id.index_progressbar_time);

				holder.indexTextDescribe = (TextView) convertView
						.findViewById(R.id.index_text_describe);
				holder.indexLocalsCountry = (TextView) convertView
						.findViewById(R.id.index_locals_country);
				holder.indexLocalsTime = (TextView) convertView
						.findViewById(R.id.index_locals_time);
				convertView.setTag(holder);
				// Tag从本质上来讲是就是相关联的view的额外的信息。它们经常用来存储(set）一些view的数据，用的时候（get）这样做非常方便而不用存入另外的单独结构。
				// <<<<<<< HEAD
				// =======
				//
				// viewMap.put(position, convertView);
				// >>>>>>> branch 'master' of
				// https://github.com/hellocountryman/helloman.git
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Listener listener = new Listener(holder, position);
		convertView.setClickable(true);
		convertView.setOnClickListener(listener);
		holder.indexSupportLinerlayout.setOnClickListener(listener);
		holder.indexCommentLinerlayout.setOnClickListener(listener);
		holder.indexCollectLinerlayout.setOnClickListener(listener);
		holder.indexShareLinerlayout.setOnClickListener(listener);
		// holder.indexProgressbarBtn.setOnClickListener(listener);
		holder.indexProgressbarId.setOnClickListener(listener);
		setSubBtn(holder, position);
		setcontent(holder, position);
		setAudioState(holder, position);
		return convertView;
	}

	// 根据音乐是否播放设置item
	private void setAudioState(final ViewHolder holder, final int position) {
		// TODO Auto-generated method stub
		if (!isAudioPlayArray.get(position, false)) {// 没有播放的
			if (mHolder != null && mHolder.equals(holder)) {
				isCurrentItemAudioPlay = false;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						holder.indexProgressbarId.setProgress(0);
						Log.i("progressbar", "主界面progress:"
								+ holder.indexProgressbarId.getProgress());
					}
				});
				holder.indexProgressbarTime.setText((Integer) list
						.get(position).get("voice_duration") + "s");
				holder.indexProgressbarBtn
						.setBackgroundResource(R.drawable.play_ico);

			}
		} else {// 正在播放的
			isCurrentItemAudioPlay = true;
			holder.indexProgressbarBtn
					.setBackgroundResource(R.drawable.pause_ico);
		}

		// 设置音频板块是否显示
		if ("".equals(list.get(position).get("voice").toString())) {
			holder.AudioLinearlayout.setVisibility(View.GONE);
			holder.audioSpaceTextView1.setVisibility(View.VISIBLE);
			holder.audioSpaceTextView2.setVisibility(View.VISIBLE);
		} else {
			holder.AudioLinearlayout.setVisibility(View.VISIBLE);
			holder.audioSpaceTextView1.setVisibility(View.GONE);
			holder.audioSpaceTextView2.setVisibility(View.GONE);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setcontent(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		// 文字
		holder.indexTextDescribe.setText(list.get(position).get("words") + "");
		// 地点
		holder.indexLocalsCountry.setText(list.get(position).get("position")
				.toString());
		// 设置头像
		holder.personHeadImg
				.setBackgroundResource(HeadImageChoose.HEAD_IDS[(int) list.get(
						position).get("head_id")]);
		// 确定热门还是地理位置图标
		if (0 == (int) list.get(position).get("ishot")) {
			holder.titleImage.setBackgroundResource(R.drawable.geographical);
			holder.indexLocalsCountry.setTextColor(context.getResources()
					.getColor(R.color.indexbg));
		} else {
			holder.titleImage
					.setBackgroundResource(R.drawable.geographical_hot);
			holder.indexLocalsCountry.setTextColor(context.getResources()
					.getColor(R.color.hot));
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 如果要奖Sring转为达特型需要用的到方法
		Date date = null;
		try {
			date = df.parse(list.get(position).get("time") + "");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.indexLocalsTime.setText(StringTools.getTimeFormatText(date));
		holder.indexProgressbarTime.setText(list.get(position).get(
				"voice_duration")
				+ "s");
		// 点赞数
		if ((Integer) (list.get(position).get("praise_num")) > 0) {
			holder.indexSupportNum.setText(list.get(position).get("praise_num")
					.toString());
		} else {
			holder.indexSupportNum.setText("赞");
		}
		// 评论数
		if ((Integer) (list.get(position).get("comment_num")) > 0) {
			holder.indexCommentNum.setText(list.get(position)
					.get("comment_num").toString());
			// holder.commentImg.setBackgroundResource(R.drawable.comment_press);
		} else {
			holder.indexCommentNum.setText("评论");
		}
		holder.commentImg.setBackgroundResource(R.drawable.comment_no);
	}

	// 设置点赞等图片按钮
	private void setSubBtn(ViewHolder holder, int position) {
		if (App.isLogin()) {
			String invId = (String) list.get(position).get("inv_id");
			String uId = App.pre.getString(Global.USER_ID, "");
			// 判断该帖子是否在点赞表里
			if (isPraised(invId, uId)) {
				holder.supportImg
						.setBackgroundResource(R.drawable.support_press);
				praiseMap.put(position, true);
			} else {
				holder.supportImg.setBackgroundResource(R.drawable.support_no);
				praiseMap.put(position, false);
			}
			// 判断该帖子是否被收藏
			if (isInCollection(invId)) {
				holder.collectImg
						.setBackgroundResource(R.drawable.collect_press);
				collectionMap.put(position, true);
			} else {
				holder.collectImg.setBackgroundResource(R.drawable.collect_no);
				collectionMap.put(position, false);
			}
		} else {
			holder.supportImg.setBackgroundResource(R.drawable.support_no);
			praiseMap.put(position, false);
			holder.collectImg.setBackgroundResource(R.drawable.collect_no);
			collectionMap.put(position, false);
		}
	}

	// 是否已经点赞
	private boolean isPraised(String invId, String uId) {
		return new PraiseDao(context).selectPraiseInvitation(invId, uId);
	}

	// 是否已经收藏
	private boolean isInCollection(String invId) {
		return new InvitationDao(context).selectCollection(invId);
	}

	class Listener implements OnClickListener {

		private int position;
		private ViewHolder holder;

		public Listener(ViewHolder holder, int position) {
			this.position = position;
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.index_share_linerlayout:
				Dialog dialog = new MyDialog(context, list.get(position),
						R.style.MyDialog);
				dialog.show();
				break;
			case R.id.index_comment_linerlayout:
				if (App.isLogin()) {
					turnToComment(position);
				} else {
					if(context instanceof MainActivity){
						((MainActivity)context).showLoginDialog();
					}
				}
				// Toast.makeText(context, "点击了所在" + position + "的评论",
				// Toast.LENGTH_SHORT).show();
				break;
			case R.id.index_collect_linerlayout:
				if (App.isLogin()) {
					dealCollectionBtn(v, position);
				} else {
					if(context instanceof MainActivity){
						((MainActivity)context).showLoginDialog();
					}
				}
				break;
			case R.id.index_support_linerlayout:
				if (App.isLogin()) {
					if (NetUtil.isNetConnect(context)) {// 检查是否联网
						dealSupportBtn(holder, position);
					}
				} else {
					if(context instanceof MainActivity){
						((MainActivity)context).showLoginDialog();
					}
				}
				break;
			case R.id.index_progressbar_id:
				if (NetUtil.isNetConnect(context)) {// 检查是否联网
					if (lastPosition == position) {
						if (!isPlay) {
							// Toast.makeText(context, "你点击了录音的播放按钮" + position,
							// Toast.LENGTH_SHORT).show();

							playAudio(holder, position);// 播放语音

						} else {
							stopAudio(holder, position);
						}
					} else {
						playAudio(holder, position);// 播放语音
					}
				}
				break;
			default:
				if (App.isLogin()) {
					turnToComment(position);
				} else {
					if(context instanceof MainActivity){
						((MainActivity)context).showLoginDialog();
					}
				}
				break;
			}
		}
	}

	// 跳转到评论页面
	private void turnToComment(int position) {
		// TODO Auto-generated method stub
		String invId = list.get(position).get("inv_id").toString();
		Intent intentComment = new Intent();
		intentComment.setClass(context, CommentActivity.class);
		intentComment.putExtra("invId", invId);
		intentComment.putExtra("enterFrom", 0);
		context.startActivity(intentComment);
	}

	// 点击收藏按钮处理
	public void dealCollectionBtn(View v, int position) {
		// 处理ui
		ImageButton collectImageButton = (ImageButton) ((LinearLayout) v)
				.findViewById(R.id.collect_img);
		if (!collectionMap.get(position)) {// 收藏
			collectImageButton.setBackgroundResource(R.drawable.collect_press);
			// 将数据添加到本地数据库
			new InvitationDao(context)
					.insert2InvitationCollection((Invitation) list
							.get(position).get("invitation"));
			collectionMap.put(position, true);
		} else {// 取消收藏
			collectImageButton.setBackgroundResource(R.drawable.collect_no);
			// 从本地删除数据
			new InvitationDao(context).deleteInInvitationCollection(list
					.get(position).get("inv_id").toString());
			collectionMap.put(position, false);
		}
	}

	public void dealSupportBtn(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		// 点赞+1的动态效果
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.nn);
		if (!praiseMap.get(position)) {// 点赞
			holder.supportImg.startAnimation(animation);
			holder.supportImg.setBackgroundResource(R.drawable.support_press);
			String number = holder.indexSupportNum.getText().toString();
			if ("赞".equals(number)) {
				holder.indexSupportNum.setText("1");
			} else {
				holder.indexSupportNum.setText((Integer.parseInt(number) + 1)
						+ "");
			}
			praiseMap.put(position, true);
			addPraise(position);
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
			praiseMap.put(position, false);
			deletePraise(position);
		}
	}

	// 向服务器和本地数据库添加点赞数据
	private void addPraise(final int position) {
		// TODO Auto-generated method stub
		String invId = list.get(position).get("inv_id").toString();
		// 该贴点赞数+1
		Invitation inv = new Invitation();
		inv.increment("praiseNum", 1);
		inv.update(context, invId, new UpdateListener() {
			@Override
			public void onSuccess() {
				int praiseNum = (Integer) (list.get(position).get("praise_num"));
				list.get(position).put("praise_num", praiseNum + 1);
				// Toast.makeText(context, "点赞数+1", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// Toast.makeText(context, "点赞数+1失败",
				// Toast.LENGTH_SHORT).show();
			}
		});

		new PraiseDao(context).insertPraise(
				App.pre.getString(Global.USER_ID, ""), invId);
	}

	private void deletePraise(final int position) {
		// TODO Auto-generated method stub
		String invId = list.get(position).get("inv_id").toString();
		// 该贴点赞数-1
		Invitation inv = new Invitation();
		inv.increment("praiseNum", -1);
		inv.update(context, invId, new UpdateListener() {
			@Override
			public void onSuccess() {
				int praiseNum = (Integer) (list.get(position).get("praise_num"));
				list.get(position).put("praise_num", praiseNum - 1);
				// Toast.makeText(context, "点赞数-1", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// Toast.makeText(context, "点赞数-1失败",
				// Toast.LENGTH_SHORT).show();
			}
		});

		PraiseDao praiseDao = new PraiseDao(context);
		praiseDao.deletePraise(invId, App.pre.getString(Global.USER_ID, ""));
	}

	class ViewHolder {
		private LinearLayout indexSupportLinerlayout;// 赞
		private LinearLayout indexCommentLinerlayout;// 评论
		private LinearLayout indexCollectLinerlayout;// 收藏
		private LinearLayout indexShareLinerlayout;// 分享
		private LinearLayout AudioLinearlayout;// 音频
		private TextView audioSpaceTextView1;// 空白
		private TextView audioSpaceTextView2;// 空白
		private ImageView titleImage;// 热门/地理位置图标
		private ImageView supportImg;// 点赞的图标
		private ImageView personHeadImg;// 头像
		private TextView indexSupportNum;// 点赞数
		private TextView indexCommentNum;// 评论数
		private ImageView collectImg;// 收藏的图标
		private ImageView commentImg;// 评论的图标
		private ImageButton indexProgressbarBtn;// 在进度条中的播放停止按钮
		private TextView indexProgressbarTime;
		private ProgressBar indexProgressbarId;
		private TextView indexTextDescribe;// 帖子内容文字
		private TextView indexLocalsCountry;// 帖子内容城市
		private TextView indexLocalsTime;// 帖子内容时间
	}

	private MyCount mCountDownTimer;// 当前录音倒计时
	private Timer mProgressTimer;// 当前进度条进度计时
	private int mCount = 0;// 进度条度数
	private MediaPlayer mp;
	private int voiceDuration;
	private boolean isPlay = false;
	private ViewHolder mHolder;// 记录前一个holder，在停止时调用
	private int lastPosition;// 记录上一个position，在点击播放按钮时判断是否有其它item在播放

	// 播放已经录好的音
	public void playAudio(ViewHolder holder, int position) {

		stopAudio(mHolder, lastPosition);
		// 设置
		mHolder = holder;
		mHolder.indexProgressbarBtn.setBackgroundResource(R.drawable.pause_ico);

		isPlay = true;
		isCurrentItemAudioPlay = true;
		Log.i("ListViewAdapter", "1:" + isCurrentItemAudioPlay);
		// 解决按钮状态也被重用的问题
		isAudioPlayArray.put(position, true);
		lastPosition = position;

		// 重新获得
		String audioUrl = list.get(position).get("voice").toString();
		Log.i("tangpeng", audioUrl + "音频文件");
		voiceDuration = (Integer) list.get(position).get("voice_duration");
		// 点击播放而已
		try {
			mp.reset();
			mp.setDataSource(audioUrl);
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener() {

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
			stopAudio();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			stopAudio();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();
			stopAudio();
			e.printStackTrace();
		}
	}

	public void stopAudio() {
		// TODO Auto-generated method stub
		stopAudio(mHolder, lastPosition);
	}

	public void stopAudio(final ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		// 进度条走完
		mCount = 0;
		isPlay = false;
		isAudioPlayArray.put(position, false);
		if (mProgressTimer != null) {
			mProgressTimer.cancel();
		}
		if (holder != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					holder.indexProgressbarId.setProgress(0);
				}
			});
			holder.indexProgressbarTime.setText(voiceDuration + "s");
			holder.indexProgressbarBtn
					.setBackgroundResource(R.drawable.play_ico);
		}
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		if (mp == null) {
			mp = new MediaPlayer();
		} else {
			if (mp.isPlaying()) {
				mp.stop();
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
			if (isCurrentItemAudioPlay) {
				mHolder.indexProgressbarTime.setText(0 + "s");
			}

		}

		@Override
		public void onTick(long millisUntilFinished) {
			// Log.i("countdown", millisUntilFinished + "");
			Log.i("ListViewAdapter", "isCurrentItemAudioPlay:"
					+ isCurrentItemAudioPlay);
			if (isCurrentItemAudioPlay) {
				mHolder.indexProgressbarTime.setText(millisUntilFinished / 1000
						+ "s");
			}
		}
	}

	// /进度条的处理
	private void showIndeterDialog(int processtime) {
		final long newprocesstime = processtime * 10;
		final int progrocessMax = 100;
		mHolder.indexProgressbarId.setMax(progrocessMax);
		mHolder.indexProgressbarId.setProgress(0);
		mHolder.indexProgressbarId.setIndeterminate(false);
		mProgressTimer = new Timer();
		mProgressTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mCount <= progrocessMax) {
					mCount++;
					if (isCurrentItemAudioPlay) {
						// TODO Auto-generated method stub
						mHolder.indexProgressbarId.setProgress(mCount);
						Log.i("progressbar", "子线程progress:"
								+ mHolder.indexProgressbarId.getProgress());
					}
				} else {
					mHandler.sendEmptyMessage(0);
				}
			}
		}, 0l, newprocesstime);
	}

	/**
	 * Handler消息处理
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// 进度条走完
				mCount = 0;
				mProgressTimer.cancel();
				mHolder.indexProgressbarId.setProgress(0);
				if (isCurrentItemAudioPlay) {
					mHolder.indexProgressbarTime.setText(voiceDuration + "s");
					mHolder.indexProgressbarBtn
							.setBackgroundResource(R.drawable.play_ico);
				}
				isAudioPlayArray.put(lastPosition, false);
				isPlay = false;
			}
			super.handleMessage(msg);
		}
	};

}
