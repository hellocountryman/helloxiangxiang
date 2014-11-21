package com.feytuo.laoxianghao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.feytuo.laoxianghao.dao.CityDao;
import com.feytuo.laoxianghao.global.Global;
import com.feytuo.laoxianghao.sortlistview.CharacterParser;
import com.feytuo.laoxianghao.sortlistview.ClearEditText;
import com.feytuo.laoxianghao.sortlistview.PinyinComparator;
import com.feytuo.laoxianghao.sortlistview.SideBar;
import com.feytuo.laoxianghao.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.feytuo.laoxianghao.sortlistview.SortAdapter;
import com.feytuo.laoxianghao.sortlistview.SortModel;
import com.umeng.analytics.MobclickAgent;

public class SelsectedCountry extends Activity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private TextView titleTextSelect;// 选择城市的标题会更改，从不同的页面
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private Button selectCountryReturnBtn;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<String> cityNameList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	
	//跳转路径
	private int path;//0为从欢迎界面跳转，1为从主界面跳转

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_country);
		path = getIntent().getIntExtra("isfromtocity", -1);
		initCity();
		initViews();
	}

	private void initCity() {
		// TODO Auto-generated method stub
		cityNameList = new CityDao(this).getAllCityName();
	}

	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		selectCountryReturnBtn = (Button) findViewById(R.id.select_country_return_btn);
		titleTextSelect = (TextView) findViewById(R.id.title_text_select);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// //城市判断
		if (path == 0) {
			titleTextSelect.setText("请选择家乡");
		} else {
			selectCountryReturnBtn.setVisibility(View.VISIBLE);//
			titleTextSelect.setText("请更改城市");
		}

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
//				Toast.makeText(getApplication(),
//						((SortModel) adapter.getItem(position)).getName(),
//						Toast.LENGTH_SHORT).show();
				// 保存当前方言地
				String city = ((SortModel) adapter.getItem(position))
						.getName();
				saveCurrentHome(city);
				turnToMain(city);
			}
		});

		SourceDateList = filledData(cityNameList);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	//跳转
	private void turnToMain(String city){
		// //城市判断
		if (path == 0) {
			/*********统计添加点击************/
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("city", city);
			MobclickAgent.onEvent(this, "Home",map);//添加操作
		} else {
			/*********统计添加点击************/
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("city", city);
			MobclickAgent.onEvent(this, "CityChange",map);//添加操作
		}
		Intent intent = new Intent();
		intent.setClass(SelsectedCountry.this, com.feytuo.chat.activity.MainActivity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * 保留当前方言地id
	 * 
	 * @param home
	 */
	protected void saveCurrentHome(String home) {
		// TODO Auto-generated method stub
		int cityId = new CityDao(this).getCityIdByName(home);
		App.pre.edit().putInt(Global.USER_HOME, cityId).commit();
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<String> data) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < data.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(data.get(i));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(data.get(i));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	public void selectcityret(View v) {
		Intent intent = new Intent();
		intent.setClass(SelsectedCountry.this, com.feytuo.chat.activity.MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("SelectedActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("SelectedActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
