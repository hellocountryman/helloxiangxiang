package com.feytuo.laoxianghao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.feytuo.laoxianghao.adapter.CommentListViewAdapter;
import com.feytuo.laoxianghao.dao.CommentDao;
import com.feytuo.laoxianghao.dao.InvitationDao;
import com.feytuo.laoxianghao.domain.Comment;
import com.feytuo.laoxianghao.domain.Invitation;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.util.GetSystemDateTime;
import com.feytuo.laoxianghao.util.Location_Baidu;
import com.feytuo.laoxianghao.util.SDcardTools;
import com.feytuo.laoxianghao.util.StringTools;
import com.feytuo.laoxianghao.view.OnloadDialog;
import com.feytuo.listviewonload.XListView;
import com.feytuo.listviewonload.XListView.IXListViewListener;
import com.umeng.analytics.MobclickAgent;

public class CommentActivity extends Activity implements IXListViewListener {
	private String invId;// 当前评论的帖子id
	private int enterFrom;// 从0主界面|1我的帖子|2主界面话题帖中进入，在获取帖子信息时有区别
	private final int STATE_REFRESH = 0;// 下拉刷新
	private final int STATE_MORE = 1;// 加载更多
	private final int LIMIT = 10;// 每页的数据是10条
	private int curPage = 0;// 当前页的编号，从0开始
	private XListView commentListview;
	private List<Map<String, Object>> listItems;
	private CommentListViewAdapter adapter;
	private EditText commentTextEdit;// 评论的输入框
	private LinearLayout commentRecordingLinear;
	private ImageView commentRecordingImg;// 按住录音的时候出现动画提示
	private TextView commentRecordHintText;// 录音评论的时候文字提示
	private TextView commentTextFocus;//textview、这里没有什么用，主要是用来失去edittext焦点
	private ImageView commentAddImg; // 添加额外的录音
	private Button commentCommentBtn;// 发送评论
	private LinearLayout commentRecordLinear;// 按住录音的布局
	private LinearLayout commentEditLinear;// 按住录音的布局
	private Button commentRecordBtn; // 按住录音
	private ImageView commentRerecordimg;// 录音之后可以取消录音按钮
	private ImageButton commentPlayRecordImgbutton;// 录音的时候播放的小按钮
	private MediaRecorder mediaRecorder; // 录音控制
	private String fileAudioName; // 保存的音频文件的名字
	private String filePath; // 音频保存的文件路径
	private File fileAudio; // 录音文件
	private MediaPlayer mp = new MediaPlayer();
	// private CountDownTimer mCountDownTimer;// /记录录音的时间
	// 动画效果
	private Animation animationOpen;
	private Animation animationClose;
	private Animation animationOut;
	private Animation animationIn;
	private boolean isReplay = false;
	private Handler mHandlerlist;// listview的处理
	private boolean isLuYin; // 是否在录音 true 是 false否

	private Invitation mInv;

	// 百度定位
	private Location_Baidu locationBaidu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		Intent intent = getIntent();
		invId = intent.getStringExtra("invId");
		enterFrom = intent.getIntExtra("enterFrom", -1);
		initLocation();// 开启定位
		initview();// 初始化布局文件
		initData();
		initAnimation();
		initlistview();// listview初始化
		initListViewData();
	}

	/*
	 * 百度定位
	 */
	private void initLocation() {
		// TODO Auto-generated method stub
		locationBaidu = new Location_Baidu(this);
		locationBaidu.start();
	}

	private Invitation getInvitationInfo(String invId) {
		if (enterFrom == 0) {// 从主界面普通帖子进入
			return new InvitationDao(this).getInvitationById(invId);
		} else if (enterFrom == 1) {// 从我的帖子进入
			return new InvitationDao(this).getInvitationFromMyById(invId);
		} else if (enterFrom == 2) {// 从主界面话题帖中进入
			return new InvitationDao(this).getTypeInvitationFromClass(invId);
		} else {
			return null;
		}
	}

	private void initAnimation() {
		// TODO Auto-generated method stub
		animationOpen = AnimationUtils.loadAnimation(this,
				R.anim.comment_turn_open);
		animationClose = AnimationUtils.loadAnimation(this,
				R.anim.comment_turn_closed);
		animationOut = AnimationUtils.loadAnimation(this,
				R.anim.comment_record_out);
		animationIn = AnimationUtils.loadAnimation(this,
				R.anim.comment_record_in);
	}

	private void initview() {

		ClickListener listenerlist = new ClickListener();
		commentRecordingLinear=(LinearLayout)findViewById(R.id.comment_recording_linear);// 按住录音的时候出现动画提示,带背景
		commentRecordingImg = (ImageView) findViewById(R.id.comment_recording_img);// 按住录音的时候出现动画提示
		commentRecordHintText = (TextView) findViewById(R.id.comment_record_hint_text);
		commentTextEdit = (EditText) findViewById(R.id.comment_text_edit);
		commentAddImg = (ImageView) findViewById(R.id.comment_add_img);
		commentCommentBtn = (Button) findViewById(R.id.comment_comment_btn);// 发送评论按钮实例化
		commentCommentBtn.setClickable(false);
		commentEditLinear = (LinearLayout) findViewById(R.id.comment_edit_linearlayout);
		commentRecordLinear = (LinearLayout) findViewById(R.id.comment_record_linearlayout);
		commentRecordBtn = (Button) findViewById(R.id.comment_record_btn);// 录音按钮的实例化
		commentPlayRecordImgbutton = (ImageButton) findViewById(R.id.comment_play_record_imgbutton);// 录音的时候播放的小按钮
		commentRerecordimg = (ImageView) findViewById(R.id.comment_rerecord_btn);// 重录的按钮
		commentListview = (XListView) findViewById(R.id.comment_listview);// 评论的listview

		commentTextEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					//当聚焦的时候改变输入框的背景色和边框，把录音的布局给隐藏起来，发送按钮可以点击
					commentTextEdit.setBackgroundResource(R.drawable.corners_storke_edit_press);
					commentRecordLinear.setVisibility(View.GONE);
					commentCommentBtn.setBackgroundResource(R.drawable.corners_storke_edit_press);
					commentCommentBtn.setClickable(true);
				}
			}
		});
		commentTextEdit.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				commentRecordLinear.setVisibility(View.GONE);
				commentCommentBtn.setClickable(true);
				return false;
			}
		});
		commentRerecordimg.setVisibility(View.INVISIBLE);// 载入重录的按钮不可见
		commentPlayRecordImgbutton.setVisibility(View.GONE);// 载入的时候录音播放按钮不可见

		commentAddImg.setOnClickListener(listenerlist);// 增加录音的评论按钮
		commentCommentBtn.setOnClickListener(listenerlist);// 发送按钮
		commentRecordBtn.setOnTouchListener(new OnToucher());// 按住录音事件
		commentRerecordimg.setOnClickListener(listenerlist);// 点击重录事件
		commentPlayRecordImgbutton.setOnClickListener(listenerlist);// 录音播放试听
		
	}

	/**
	 * 
	 * 初始化数据
	 */
	private void initData() {
		if (!SDcardTools.isHaveSDcard()) {
			Toast.makeText(CommentActivity.this, "请插入SD卡以便存储录音",
					Toast.LENGTH_LONG).show();
			return;
		}

		// 要保存的文件的路径
		filePath = SDcardTools.getSDPath() + "/" + "laoxianghaoAudio";
		// 实例化文件夹
		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
	}

	/**
	 * 
	 * 初始化list数据
	 */
	private void initlistview() {
		mInv = getInvitationInfo(invId);
		if (mInv != null) {
			// 设置数据统计
			setDataStatistics(mInv.getIsHot(), mInv.getObjectId(),
					mInv.getWords());
			listItems = new ArrayList<Map<String, Object>>();
			commentListview.setPullLoadEnable(true);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
			adapter = new CommentListViewAdapter(CommentActivity.this,
					listItems, mInv);
			commentListview.setAdapter(adapter);
			commentListview.setXListViewListener(this);
			mHandlerlist = new Handler();
		}
	}

	// 设置数据统计普通和热门话题评论查看数
	private void setDataStatistics(int isHot, String invId, String words) {
		// TODO Auto-generated method stub
		if (isHot == 1) {
			/********* 统计添加点击 ************/
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("invId", invId);
			map.put("content", words);
			MobclickAgent.onEvent(this, "HotInvitation", map);// 添加操作
		} else {
			/********* 统计添加点击 ************/
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("invId", invId);
			map.put("content", words);
			MobclickAgent.onEvent(this, "CommonInvitation", map);// 添加操作
		}
	}

	private void initListViewData() {
		// TODO Auto-generated method stub
		// 获取本地数据
		getDataFromLocal();
		// 获取网络数据，删除本地，存入本地
		getDataFromNet(0, STATE_REFRESH);
	}

	// 获取本地数据，加载数据
	private void getDataFromLocal() {
		// TODO Auto-generated method stub
		List<Comment> listData = new ArrayList<Comment>();
		listData = new CommentDao(this).getAllComment(invId);
		for (Comment comment : listData) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("com_id", comment.getObjectId());
			map.put("uid", comment.getuId());
			map.put("inv_id", comment.getInvId());
			map.put("com_words", comment.getComWords());
			map.put("com_voice", comment.getComVoice());
			map.put("com_time", comment.getComTime());
			map.put("com_position", comment.getComPosition());
			listItems.add(map);
		}
		adapter.notifyDataSetChanged();
	}

	// 加载网络数据
	private void getDataFromNet(final int page, final int actionType) {
		// TODO Auto-generated method stub
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		/********** 新版本过度获取，由于第一版只能通过id获取 **********/
		query.addWhereEqualTo("invId", invId);
		/********** 新版本过度获取，由于第一版只能通过id获取 **********/
		// 本来由于设置了外键需要用pointer来获取当前用户的帖子，但由于第一版本限制暂采用老方式
		// query.addWhereRelatedTo("comment", new BmobPointer(mInv));
		Log.i("CommentActivity", "invId:" + invId);
		query.order("createdAt");
		query.setLimit(LIMIT); // 设置每页多少条数据
		query.setSkip(page * LIMIT); // 从第几条数据开始
		query.findObjects(this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> arg0) {
				// TODO Auto-generated method stub
				Log.i("CommentActivity", "list大小：" + arg0.size());
				if (actionType == STATE_REFRESH) {
					curPage = 0;
					listItems.clear();
				}
				for (Comment comment : arg0) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("com_id", comment.getObjectId());
					map.put("uid", comment.getuId());
					map.put("inv_id", comment.getInvId());
					map.put("com_words", comment.getComWords());
					map.put("com_voice", comment.getComVoice());
					map.put("com_time", comment.getCreatedAt());
					map.put("com_position", comment.getComPosition());
					listItems.add(map);
				}
				adapter.notifyDataSetChanged();
				// 存入本地数据库
				if (actionType == STATE_REFRESH) {
					CommentDao commentDao = new CommentDao(CommentActivity.this);
					commentDao.deleteAllComment(invId);
					commentDao.insert2Comment(arg0);
				}
				if (arg0.size() == 0) {
					if (actionType == STATE_MORE) {
						Toast.makeText(CommentActivity.this, "没有更多内容",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(CommentActivity.this, "暂无更新",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					// 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
					curPage++;
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				// Toast.makeText(CommentActivity.this, "查询失败：" + arg1,
				// Toast.LENGTH_SHORT).show();
				Log.i("CommentActivity", "查询失败:" + arg0 + "=" + arg1);
			}
		});
	}

	/**
	 * 
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandlerlist.postDelayed(new Runnable() {

			@Override
			public void run() {
				getDataFromNet(0, STATE_REFRESH);
				onLoad();
			}
		}, 2000);
	}

	/**
	 * 
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandlerlist.postDelayed(new Runnable() {
			@Override
			public void run() {
				getDataFromNet(curPage, STATE_MORE);
				onLoad();
			}
		}, 2000);
	}

	/** 停止刷新， */
	private void onLoad() {
		commentListview.stopRefresh();
		commentListview.stopLoadMore();
		commentListview.setRefreshTime("刚刚");
	}

	/**
	 * 
	 * 按住事件
	 */
	class OnToucher implements OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				commentCommentBtn.setBackgroundResource(R.drawable.corners_storke_edit_no);
				commentRecordBtn.setBackgroundResource(R.drawable.comment_record_press);
				commentRecordHintText.setText("正在录音");
				commentRecordingLinear.setVisibility(View.VISIBLE);
				startAudio();
				break;
			case MotionEvent.ACTION_UP:
				
				commentRecordHintText.setText("点击试听");
				commentRecordingLinear.setVisibility(View.GONE);
				//发送按钮可以点击
				commentCommentBtn.setBackgroundResource(R.drawable.corners_storke_edit_press);
				commentCommentBtn.setClickable(true);
				stopAudio();
				break;

			default:
				break;
			}
			return true;
		}
	}

	/**
	 * 
	 * 按钮监听
	 * 
	 * @author tangpeng
	 * 
	 */
	class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.comment_add_img:// 点击增加录音的按钮
				// 隐藏输入法
				HideKeyboard(v);			
				if (commentRecordLinear.getVisibility() == View.VISIBLE) {

					commentRecordLinear.setVisibility(View.GONE);
					
				} else {

					commentRecordLinear.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.comment_comment_btn:// 点击发送评论按钮
				publishComment();
				break;
			case R.id.comment_rerecord_btn:// 点击重录按钮
				recordAudio();
				break;
			case R.id.comment_play_record_imgbutton:// 录音后试听
				if (!isReplay) {
					
					playAudio();
				} else {
					pauseAudio();
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 发布评论
	 */
	public void publishComment() {
		// TODO Auto-generated method stub
		String comWords = commentTextEdit.getText().toString();
		if ("".equals(comWords)) {
			Toast.makeText(this, "来一句评论撒~", Toast.LENGTH_SHORT).show();
			return;
		}
		uploadAudioFile(comWords);
	}

	private Dialog dialog;

	// 上传音频文件
	private void uploadAudioFile(final String comWords) {
		dialog = new OnloadDialog(this, R.style.LoadDialog, true);
		dialog.show();
		if (fileAudio != null && fileAudio.exists()) {
			final BmobFile bmobFile = new BmobFile(fileAudio);
			bmobFile.uploadblock(this, new UploadFileListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					// 获取文件url后上传基本信息
					Log.i("CommentActivity", "上传成功");
					uploadBaseInfo(bmobFile.getFileUrl(), comWords);
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					closeDialog();
					Log.i("CommentActivity", "上传失败");
				}
			});
		} else {
			// 没有录音，也要上传基本信息
			Log.i("CommentActivity", "没有录音文件");
			uploadBaseInfo("", comWords);
		}
	}

	// 上传基本信息
	private void uploadBaseInfo(String fileUrl, String comWords) {
		// TODO Auto-generated method stub
		final Comment comment = new Comment();
		comment.setuId(App.pre.getString(Global.USER_ID, ""));
		comment.setInvId(invId);
		comment.setComWords(comWords);
		comment.setComVoice(fileUrl);
		comment.setInvitation(mInv);
		comment.setComPosition(locationBaidu.getmPosition());
		comment.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// 将评论添加到关联的帖子中
				addCommentToInvitation(comment);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(CommentActivity.this, "网络或服务器不给力啊！请稍候再试...",
						Toast.LENGTH_SHORT).show();
				Log.i("CommentActivity", "网络或服务器问题，请稍候再试..." + arg1);
				closeDialog();
			}
		});
	}

	protected void addCommentToInvitation(Comment comment) {
		// TODO Auto-generated method stub
		BmobRelation comments = new BmobRelation();
		comments.add(comment);
		mInv.setComment(comments);
		mInv.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i("PublishActivity", "上传评论基础信息成功");
				Toast.makeText(CommentActivity.this, "发表成功", Toast.LENGTH_SHORT)
						.show();
				// 服务器评论数加1
				addCommentNum();
				// 界面评论数+1
				addInCurrrentInvitation();
				// 获取网络数据，删除本地，存入本地
				getDataFromNet(0, STATE_REFRESH);
				// 关闭加载对话框
				closeDialog();
				// 回复发表评论框
				commentTextEdit.setText("");
				// 录音功能恢复
				recordAudio();
				// 返回主页需要刷新
				App.pre.edit()
						.putBoolean(Global.IS_MAIN_LIST_NEED_REFRESH, true)
						.commit();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(CommentActivity.this, "网络或服务器不给力啊！请稍候再试...",
						Toast.LENGTH_SHORT).show();
				Log.i("CommentActivity", "网络或服务器问题，请稍候再试..." + arg1);
				closeDialog();
			}
		});
	}

	protected void addInCurrrentInvitation() {
		// TODO Auto-generated method stub
		if (mInv != null) {
			mInv.setCommentNum(mInv.getCommentNum() + 1);
			adapter.notifyDataSetChanged();
		}
	}

	private void closeDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void addCommentNum() {
		// 该贴点赞数+1
		Invitation inv = new Invitation();
		inv.increment("commentNum", 1);
		inv.update(this, invId, new UpdateListener() {
			@Override
			public void onSuccess() {
				// Toast.makeText(CommentActivity.this, "评论数+1",
				// Toast.LENGTH_SHORT).show();
				Log.i("CommentActivity", "评论数+1");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// Toast.makeText(CommentActivity.this, "评论数+1失败",
				// Toast.LENGTH_SHORT).show();
				Log.i("CommentActivity", "评论数+1失败");
			}
		});
	}

	// 关闭其他录音
	private void stopAntherVoice() {
		adapter.stopAudio();
		adapter.stopInvitationAudio();
	}

	private int recorderTime;
	private Timer recorderTimer;
	private AnimationDrawable animationDrawable;

	/**
	 * 
	 * 开始录音
	 */
	private void startAudio() {

		commentRecordingImg.setBackgroundResource(R.anim.frame_comment_anim);// 正在录音的动画
		animationDrawable = (AnimationDrawable) commentRecordingImg.getBackground();
		animationDrawable.start();

		stopAntherVoice();
		recorderTime = 0;
		recorderTimer = new Timer();
		recorderTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				recorderTime++;
			}
		}, 1000l, 1000l);
		// 创建录音频文件
		// 这种创建方式生成的文件名是随机的
		fileAudioName = "audio" + GetSystemDateTime.now()
				+ StringTools.getRandomString(2) + ".mar";
		mediaRecorder = new MediaRecorder();
		// 设置录音的来源为麦克风
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(filePath + "/" + fileAudioName);
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
			fileAudio = new File(filePath + "/" + fileAudioName);
			isLuYin = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 停止录制
	 */
	private void stopAudio() {
		animationDrawable.stop();// 停止动画
		commentRerecordimg.setVisibility(View.VISIBLE);// 录完音之后录音出现
		
		commentPlayRecordImgbutton.setVisibility(View.VISIBLE);// 录音播放按钮可见
		commentPlayRecordImgbutton.setBackgroundResource(R.drawable.comment_record_play);//出现试听的按钮
		if (recorderTime <= 1) {
			Toast.makeText(this, "太短啦", Toast.LENGTH_SHORT).show();
			recordAudio();
		} else {
			if (null != mediaRecorder) {
				// 停止录音
				commentRecordBtn.setText("");
				mediaRecorder.stop();
			}
		}
		recorderTimer.cancel();
		mediaRecorder.reset();
		mediaRecorder.release();
		mediaRecorder = null;
	}

	// 停止当前播放的录音
	private void pauseAudio() {
		// TODO Auto-generated method stub
		if (mp.isPlaying()) {
			mp.stop();
		}
		commentRecordHintText.setText("点击播放");
		commentPlayRecordImgbutton.setBackgroundResource(R.drawable.comment_record_play);
		commentRerecordimg.setVisibility(View.VISIBLE);// 已经录好准备评论的声音后重放中暂停，重录按钮显示
		isReplay = false;
	}

	/**
	 * 
	 * 重新录音
	 */
	private void recordAudio() {
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}
		if (fileAudio != null) {
			fileAudio.delete();// 文件删除
			fileAudio = null;
		}
		//发送按钮不可以点击
		commentCommentBtn.setBackgroundResource(R.drawable.corners_storke_edit_no);
		commentCommentBtn.setClickable(false);
		commentPlayRecordImgbutton.setVisibility(View.GONE);// 录音播放按钮不可见
		commentRecordBtn.setBackgroundResource(R.drawable.comment_record_no);
		commentRecordHintText.setText("按住录音");
		commentPlayRecordImgbutton.setVisibility(View.GONE);
		commentRerecordimg.setVisibility(View.INVISIBLE);

	}

	// 播放已经录好的音
	private void playAudio() {
		stopAntherVoice();
		// 设置ui
		commentPlayRecordImgbutton.setBackgroundResource(R.drawable.comment_record_pause);
		commentRecordHintText.setText("正在播放");
		commentRerecordimg.setVisibility(View.INVISIBLE);
		isReplay = true;
		// 点击播放而已
		try {
			mp.reset();
			mp.setDataSource(filePath + "/" + fileAudioName);
			mp.prepare();
			mp.seekTo(0);
			mp.start();
			mp.setOnCompletionListener(onCompletionListener);
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

	private OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			commentRecordHintText.setText("点击试听");
			commentPlayRecordImgbutton
					.setBackgroundResource(R.drawable.comment_record_play);
			isReplay = false;
			commentRerecordimg.setVisibility(View.VISIBLE);
		}
	};

	public void commentReturnRelative(View v) {
		finish();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			mp.release();
		}
		if (null != mediaRecorder && isLuYin) {
			mediaRecorder.release();
		}
		if (fileAudio != null) {
			fileAudio.delete();
		}
		locationBaidu.stop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("CommentActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.stopAudio();
		adapter.stopInvitationAudio();
		MobclickAgent.onPageEnd("CommentActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
	
	//隐藏虚拟键盘
    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
      if ( imm.isActive( ) ) {     
          imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );   
          
      }    
    }
}
