package com.lisz.banktraining.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lisz.banktraining.R;
import com.lisz.banktraining.util.Constent;

public class StarttrainingPopup extends PopupWindow {
	private View contentView;
	private Button singleChoice, mutipleChoice, judge;

	public StarttrainingPopup(Context context, final Handler handler, final int type) {

		LayoutInflater inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.start_training_poplayout, null);

		singleChoice = (Button) contentView.findViewById(R.id.single_choice);
		mutipleChoice = (Button) contentView.findViewById(R.id.mutiple_choice);
		judge = (Button) contentView.findViewById(R.id.judge);
		singleChoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(type == Constent.NORMAL_TRAININGPOPUP){
					handler.sendEmptyMessage(Constent.SINGLECHOICE_TRAINING);
				}else if(type == Constent.WRONG_TRAININGPOPUP){
					handler.sendEmptyMessage(Constent.WRONG_SINGLECHOICE_TRAINING);
				}
			}
		});

		mutipleChoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(type == Constent.NORMAL_TRAININGPOPUP){
					handler.sendEmptyMessage(Constent.MUTIPLECHOICE_TRAINING);
				}else if(type == Constent.WRONG_TRAININGPOPUP){
					handler.sendEmptyMessage(Constent.WRONG_MUTIPLECHOICE_TRAINING);
				}
			}
		});

		judge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(type == Constent.NORMAL_TRAININGPOPUP){
					handler.sendEmptyMessage(Constent.JUDGE_TRAINING);
				}else if(type == Constent.WRONG_TRAININGPOPUP){
					handler.sendEmptyMessage(Constent.WRONG_JUDGE_TRAINING);
				}
			}
		});

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		// 设置SelectPicPopupWindow的View
		this.setContentView(contentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(height / 10);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		if(type == Constent.NORMAL_TRAININGPOPUP){
			this.setAnimationStyle(R.style.normalpopupanimation);
		}else if(type == Constent.WRONG_TRAININGPOPUP){
			this.setAnimationStyle(R.style.middlepopupanimation);
		}
	}

	/**
	 * 显示在指定控件的上方
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			int[] location = new int[2];
			parent.getLocationOnScreen(location);
			this.showAtLocation(parent, Gravity.NO_GRAVITY, location[0],
					location[1] - this.getHeight());
		} else {
			this.dismiss();
		}
	}

}
