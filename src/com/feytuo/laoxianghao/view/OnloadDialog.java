package com.feytuo.laoxianghao.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.feytuo.laoxianghao.R;

public class OnloadDialog extends Dialog {
	Context context;

	public OnloadDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public OnloadDialog(Context context, int theme, boolean cancelable) {
		super(context, theme);
		this.context = context;
		setCancelable(cancelable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.onload_dialog);
	}

}