package com.lisz.banktraining;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lisz.banktraining.util.Constent;
import com.lisz.banktraining.view.SettingPopup;
import com.lisz.banktraining.view.StarttrainingPopup;

public class MainActivity extends Activity implements OnClickListener {

	private Button startTraning, setting, error_record;
	private SettingPopup settingPopup;
	private StarttrainingPopup normal_TrainingPopup, wrong_TrainingPopup;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlayout);
		initView();
		initHandler();
		initData();
	}

	private void initView() {
		startTraning = (Button) findViewById(R.id.start_training);
		setting = (Button) findViewById(R.id.setting_button);
		error_record = (Button) findViewById(R.id.error_record);
		startTraning.setOnClickListener(this);
		setting.setOnClickListener(this);
		error_record.setOnClickListener(this);
	}

	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Intent intent = new Intent(MainActivity.this,
						TrainingActivity.class);
				switch (msg.what) {
				case Constent.SINGLECHOICE_TRAINING:
					intent.putExtra(Constent.TRAINING_TYPE,
							Constent.SINGLECHOICE_TRAINING);
					normal_TrainingPopup.dismiss();
					break;
				case Constent.WRONG_SINGLECHOICE_TRAINING:
					intent.putExtra(Constent.TRAINING_TYPE,
							Constent.WRONG_SINGLECHOICE_TRAINING);
					wrong_TrainingPopup.dismiss();
					break;
				case Constent.MUTIPLECHOICE_TRAINING:
					intent.putExtra(Constent.TRAINING_TYPE,
							Constent.MUTIPLECHOICE_TRAINING);
					normal_TrainingPopup.dismiss();
					break;
				case Constent.WRONG_MUTIPLECHOICE_TRAINING:
					intent.putExtra(Constent.TRAINING_TYPE,
							Constent.WRONG_MUTIPLECHOICE_TRAINING);
					wrong_TrainingPopup.dismiss();
					break;
				case Constent.JUDGE_TRAINING:
					intent.putExtra(Constent.TRAINING_TYPE,
							Constent.JUDGE_TRAINING);
					normal_TrainingPopup.dismiss();
					break;
				case Constent.WRONG_JUDGE_TRAINING:
					intent.putExtra(Constent.TRAINING_TYPE,
							Constent.WRONG_JUDGE_TRAINING);
					wrong_TrainingPopup.dismiss();
					break;
					
				default:
					break;
				}
				startActivity(intent);
				super.handleMessage(msg);
			}
		};
	}

	private void initData() {
		settingPopup = new SettingPopup(this, handler);
		normal_TrainingPopup = new StarttrainingPopup(this, handler,
				Constent.NORMAL_TRAININGPOPUP);
		wrong_TrainingPopup = new StarttrainingPopup(this, handler,
				Constent.WRONG_TRAININGPOPUP);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.start_training:
			wrong_TrainingPopup.dismiss();
			normal_TrainingPopup.showPopupWindow(startTraning);
			break;
		case R.id.setting_button:
			settingPopup.showPopupWindow(MainActivity.this);
			break;
		case R.id.error_record:
			normal_TrainingPopup.dismiss();
			wrong_TrainingPopup.showPopupWindow(error_record);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
