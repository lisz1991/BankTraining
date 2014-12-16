package com.lisz.banktraining.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lisz.banktraining.R;
import com.lisz.banktraining.db.DBUtil;
import com.lisz.banktraining.util.Constent;
import com.lisz.banktraining.util.SharedPreferencesUtil;

public class SettingPopup extends PopupWindow {
	private View contentView;
	private CheckBox saveSingleprocess, saveMutipleprocess, saveJudgeprocess,
			autoSaveErrorQues;
	private RelativeLayout clear_errorRecord;
	private Context context;

	public SettingPopup(Context context, Handler handler) {
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.setting_poplayout, null);
		initSettingView();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		// ����SelectPicPopupWindow��View
		this.setContentView(contentView);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0000000000);
		// ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener �����������ؼ��仯�Ȳ���
		this.setBackgroundDrawable(dw);

		this.setAnimationStyle(R.style.centerpopupanimation);
	}

	private void initSettingView() {
		saveSingleprocess = (CheckBox) contentView
				.findViewById(R.id.save_singleprocess);
		saveMutipleprocess = (CheckBox) contentView
				.findViewById(R.id.save_mutipleprocess);
		saveJudgeprocess = (CheckBox) contentView
				.findViewById(R.id.save_judgeprocess);
		autoSaveErrorQues = (CheckBox) contentView
				.findViewById(R.id.auto_saveWrongQues);
		clear_errorRecord = (RelativeLayout) contentView
				.findViewById(R.id.clear_errorquestions);

		if (SharedPreferencesUtil.getBooleanPreference(Constent.SINGLECHOICE)) {
			saveSingleprocess.setChecked(true);
		} else {
			saveSingleprocess.setChecked(false);
		}

		if (SharedPreferencesUtil.getBooleanPreference(Constent.MUTIPLECHOICE)) {
			saveMutipleprocess.setChecked(true);
		} else {
			saveMutipleprocess.setChecked(false);
		}

		if (SharedPreferencesUtil.getBooleanPreference(Constent.JUDGE)) {
			saveJudgeprocess.setChecked(true);
		} else {
			saveJudgeprocess.setChecked(false);
		}

		if (SharedPreferencesUtil
				.getBooleanPreference(Constent.AUTO_SAVEEROR_QUES)) {
			autoSaveErrorQues.setChecked(true);
		} else {
			autoSaveErrorQues.setChecked(false);
		}

		saveSingleprocess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SharedPreferencesUtil
						.getBooleanPreference(Constent.SINGLECHOICE)) {
					SharedPreferencesUtil.saveBooleanPreference(
							Constent.SINGLECHOICE, false);
					saveSingleprocess.setChecked(false);
				} else {
					SharedPreferencesUtil.saveBooleanPreference(
							Constent.SINGLECHOICE, true);
					saveSingleprocess.setChecked(true);
				}
			}
		});

		saveMutipleprocess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SharedPreferencesUtil
						.getBooleanPreference(Constent.MUTIPLECHOICE)) {
					SharedPreferencesUtil.saveBooleanPreference(
							Constent.MUTIPLECHOICE, false);
					saveMutipleprocess.setChecked(false);
				} else {
					SharedPreferencesUtil.saveBooleanPreference(
							Constent.MUTIPLECHOICE, true);
					saveMutipleprocess.setChecked(true);
				}
			}
		});

		saveJudgeprocess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SharedPreferencesUtil.getBooleanPreference(Constent.JUDGE)) {
					SharedPreferencesUtil.saveBooleanPreference(Constent.JUDGE,
							false);
					saveJudgeprocess.setChecked(false);
				} else {
					SharedPreferencesUtil.saveBooleanPreference(Constent.JUDGE,
							true);
					saveJudgeprocess.setChecked(true);
				}
			}
		});

		autoSaveErrorQues.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SharedPreferencesUtil
						.getBooleanPreference(Constent.AUTO_SAVEEROR_QUES)) {
					SharedPreferencesUtil.saveBooleanPreference(
							Constent.AUTO_SAVEEROR_QUES, false);
					autoSaveErrorQues.setChecked(false);
				} else {
					SharedPreferencesUtil.saveBooleanPreference(
							Constent.AUTO_SAVEEROR_QUES, true);
					autoSaveErrorQues.setChecked(true);
				}
			}
		});

		clear_errorRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmDialog();
			}
		});
	}

	private void showConfirmDialog() {
		new AlertDialog.Builder(context)
				.setMessage(R.string.confirm_dialog_message)
				.setPositiveButton(R.string.dialog_ok,
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								DBUtil.getInstance().deleteAllWrongQuesRecords();
							}
						}).setNegativeButton(R.string.dialog_cancle, null)
				.show();
	}

	/**
	 * ��ʾ��ָ���ؼ����Ϸ�
	 * 
	 * @param parent
	 */
	public void showPopupWindow(Activity activity) {
		if (!this.isShowing()) {
			this.showAtLocation(activity.getWindow().getDecorView(),
					Gravity.CENTER, 0, 0);
		} else {
			this.dismiss();
		}
	}

}
