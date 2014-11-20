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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.feytuo.laoxianghao.PersonDetailsActivity;
import com.feytuo.laoxianghao.R;
import com.feytuo.laoxianghao.SetActivity;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingsFragment extends Fragment{

	private RelativeLayout personInfoRela;
	private RelativeLayout personTalkRela;
	private RelativeLayout personSetRela;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.person_activity, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		personInfoRela = (RelativeLayout) getView().findViewById(
				R.id.person_info_rela);
		personTalkRela = (RelativeLayout) getView().findViewById(
				R.id.person_talk_rela);
		personSetRela = (RelativeLayout) getView().findViewById(
				R.id.person_set_rela);
		personInfoRela.setOnClickListener(new linstener());
		personTalkRela.setOnClickListener(new linstener());
		personSetRela.setOnClickListener(new linstener());

	}

	class linstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
			switch (v.getId()) {
			
			case R.id.person_info_rela:
				intent.setClass(getActivity(), PersonDetailsActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.person_talk_rela:

				break;
			case R.id.person_set_rela:
				intent.setClass(getActivity(), SetActivity.class);
				getActivity().startActivity(intent);
				break;

			default:
				break;
			}
		}

	}


}
