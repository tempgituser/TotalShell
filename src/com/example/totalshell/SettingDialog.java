package com.example.totalshell;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingDialog extends Dialog {

	public SettingDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SettingDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public SettingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SettingDialog(Context context, final OnCancelListener cancelListener) {
		super(context, true, null);
		
		setContentView(R.layout.setting_dialog);
		
		final CheckBox includeSystemCB = (CheckBox)findViewById(R.id.includeSystem);
		boolean isIncludeSystem = SettingState.isIncludeSystem(context);
		includeSystemCB.setChecked(isIncludeSystem);
		
		final CheckBox includeServiceCB = (CheckBox)findViewById(R.id.includeService);
		boolean includeService = SettingState.isIncludeService(context);
		includeServiceCB.setChecked(includeService);
//		
//		final CheckBox useRootCB = (CheckBox)findViewById(R.id.useRoot);
//		boolean useRoot = SettingState.isUseRoot(context);
//		useRootCB.setChecked(useRoot);
		
		Button ok = (Button) findViewById(R.id.ok);
		Button cancel = (Button) findViewById(R.id.cancel);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SettingState.setIsIncludeSystem(getContext(), includeSystemCB.isChecked());
				SettingState.setIsIncludeService(getContext(), includeServiceCB.isChecked());
//				SettingState.setIsUseRoot(getContext(), useRootCB.isChecked());
				SettingDialog.this.dismiss();
				cancelListener.onCancel(SettingDialog.this);
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SettingDialog.this.dismiss();
			}
		});
		
		
	}
}
