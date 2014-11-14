package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feytuo.laoxianghao.fragment.Fragment1;
import com.feytuo.laoxianghao.fragment.Fragment2;
import com.umeng.analytics.MobclickAgent;

public class MessageCellectActivity extends ActionBarActivity {
	private ViewPager viewPager;

	private RelativeLayout myMessageLinearlayout;
	private RelativeLayout myCollectLinearlayout;
	private TextView messageTitleText;
	private TextView collectTitleText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagecollect);
		
		initView();
		
		getSupportActionBar().hide();
		// 初始化viewpager
		initViewPager();
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		myMessageLinearlayout = (RelativeLayout) findViewById(R.id.my_message_linearlayout);
		myCollectLinearlayout = (RelativeLayout) findViewById(R.id.my_collect_linearlayout);
		messageTitleText=(TextView)findViewById(R.id.messagecollect_message_title_text);//消息的标题
		collectTitleText=(TextView)findViewById(R.id.messagecollect_collect_title_text);//收藏的标题
		myMessageLinearlayout.setOnClickListener(listener);
		myCollectLinearlayout.setOnClickListener(listener);
	}

	@SuppressLint("NewApi")
	private void initViewPager() {
		// TODO Auto-generated method stub
		List<Fragment> list = new ArrayList<>();
		Fragment fragment1 = new Fragment1();
		Fragment fragment2 = new Fragment2();
		list.add(fragment1);
		list.add(fragment2);

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), list));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
				//通过代码setTextColor时 如果color是一个资源文件 会set失败 没有效果
				ColorStateList indexbgcolors =getResources().getColorStateList(R.color.indexbg);
				ColorStateList whitecolors =getResources().getColorStateList(R.color.white);
				
				if (arg0 == 0) {
					myMessageLinearlayout.getBackground().setAlpha((int)(arg1*255));
					myCollectLinearlayout.getBackground().setAlpha((int)((1-arg1)*255));
					
					//设置标题栏中文字的字体颜色
					messageTitleText.setTextColor(indexbgcolors);
					collectTitleText.setTextColor(whitecolors);
					
				} else {
					myMessageLinearlayout.getBackground().setAlpha((int)((1-arg1)*255));
					myCollectLinearlayout.getBackground().setAlpha((int)(arg1*255));
					
					//设置标题栏中文字的字体颜色
					messageTitleText.setTextColor(whitecolors);
					collectTitleText.setTextColor(indexbgcolors);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			// TODO Auto-generated constructor stub
			this.list = list;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 点击时滑动块移动
			case R.id.my_message_linearlayout:
				viewPager.setCurrentItem(0, true);
				break;
			case R.id.my_collect_linearlayout:
				viewPager.setCurrentItem(1, true);
				break;
			}
		}
	};

	public void messagecollectReturnRelative(View v) {

		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
