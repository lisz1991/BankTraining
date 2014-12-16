package com.lisz.banktraining;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lisz.banktraining.bean.ChoiceQuestion;
import com.lisz.banktraining.bean.JudgeQuestion;
import com.lisz.banktraining.db.DBUtil;
import com.lisz.banktraining.util.Constent;
import com.lisz.banktraining.util.L;
import com.lisz.banktraining.util.SharedPreferencesUtil;
import com.lisz.banktraining.view.JustifyTextView;

public class TrainingActivity extends Activity implements OnClickListener {

	private static final String TAG = "TrainingActivity";
	private ImageButton backButton, resetProcess;
	private TextView serial_num, trainingItem;
	private JustifyTextView questionContent, selection_a, selection_b,
			selection_c, selection_d, answer_desc;
	private CheckBox point_a, point_b, point_c, point_d;
	private Button submit;
	private RelativeLayout selection_layout_a, selection_layout_b,
			selection_layout_c, selection_layout_d;
	private String trainingName;
	private int quesNum = 1;
	private int trainingType;
	private DBUtil dbUtil;
	private String correctAnswer;
	private Set userAnswer;
	private Handler handler;
	private boolean isChecking;
	private ArrayList<String> wrongQuesIdList;
	private Long totalCount;
	private AlertDialog tipsDialog;

	private static final int GET_NEXT_QUES = 0X0;
	private static final int OVER_TRAINING_TYPE = 0X1;
	private static final int NO_ERROR_RECORD_TYPE = 0X2;
	private static final int RESET_PROCESS_TYPE = 0X3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_layout);
		initIntent();
		initView();
		initBaseView();
		initData();
		initHandler();
		getQuestionInfoFromDB(quesNum);
	}

	private void initIntent() {
		Intent intent = getIntent();
		trainingType = intent.getIntExtra(Constent.TRAINING_TYPE,
				Constent.SINGLECHOICE_TRAINING);
		L.v(TAG, "initIntent_trainingType", trainingType);
		if (trainingType == Constent.SINGLECHOICE_TRAINING) {
			trainingName = "单选题练习";
		} else if (trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			trainingName = "错误单选题回顾";
		} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING) {
			trainingName = "多选题练习";
		} else if (trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			trainingName = "错误多选题回顾";
		} else if (trainingType == Constent.JUDGE_TRAINING) {
			trainingName = "判断题练习";
		} else if (trainingType == Constent.WRONG_JUDGE_TRAINING) {
			trainingName = "错误判断题回顾";
		}
	}

	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GET_NEXT_QUES:
					getNextQuestion();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void initView() {
		backButton = (ImageButton) findViewById(R.id.back_button);
		resetProcess = (ImageButton) findViewById(R.id.reset_process);
		serial_num = (TextView) findViewById(R.id.question_id);
		trainingItem = (TextView) findViewById(R.id.training_item);
		questionContent = (JustifyTextView) findViewById(R.id.question_content);
		selection_a = (JustifyTextView) findViewById(R.id.selection_a);
		selection_b = (JustifyTextView) findViewById(R.id.selection_b);
		selection_c = (JustifyTextView) findViewById(R.id.selection_c);
		selection_d = (JustifyTextView) findViewById(R.id.selection_d);
		answer_desc = (JustifyTextView) findViewById(R.id.answer_desc);
		point_a = (CheckBox) findViewById(R.id.point_a);
		point_b = (CheckBox) findViewById(R.id.point_b);
		point_c = (CheckBox) findViewById(R.id.point_c);
		point_d = (CheckBox) findViewById(R.id.point_d);
		answer_desc.setVisibility(View.GONE);
		submit = (Button) findViewById(R.id.submit);
		selection_layout_a = (RelativeLayout) findViewById(R.id.selection_layout_a);
		selection_layout_b = (RelativeLayout) findViewById(R.id.selection_layout_b);
		selection_layout_c = (RelativeLayout) findViewById(R.id.selection_layout_c);
		selection_layout_d = (RelativeLayout) findViewById(R.id.selection_layout_d);

		selection_layout_a.setOnClickListener(this);
		selection_layout_b.setOnClickListener(this);
		selection_layout_c.setOnClickListener(this);
		selection_layout_d.setOnClickListener(this);

		submit.setOnClickListener(this);

		backButton.setOnClickListener(this);
		resetProcess.setOnClickListener(this);
	}

	private void initBaseView() {
		trainingItem.setText(trainingName);
		if (trainingType == Constent.JUDGE_TRAINING
				| trainingType == Constent.WRONG_JUDGE_TRAINING) {
			selection_layout_c.setVisibility(View.GONE);
			selection_layout_d.setVisibility(View.GONE);
			submit.setVisibility(View.GONE);
		} else if (trainingType == Constent.SINGLECHOICE_TRAINING
				| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			submit.setVisibility(View.GONE);
		}
		if (trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING
				| trainingType == Constent.WRONG_JUDGE_TRAINING
				| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			resetProcess.setVisibility(View.GONE);
		}
	}

	private void initData() {
		dbUtil = DBUtil.getInstance();
		userAnswer = new TreeSet<String>();
		totalCount = dbUtil.getQuesCount(trainingType);
		L.v(TAG, "current_table_record_count", String.valueOf(totalCount));
		wrongQuesIdList = new ArrayList<String>();
		if (trainingType == Constent.WRONG_JUDGE_TRAINING
				| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING
				|| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			getWrongQuesListFromDB();
		}

		if (trainingType == Constent.SINGLECHOICE_TRAINING
				&& SharedPreferencesUtil
						.getBooleanPreference(Constent.SINGLECHOICE)) {
			quesNum = SharedPreferencesUtil
					.getIntPreference(Constent.SINGLECHOICE_PROCESS);
			L.v(TAG, "initData_getSingleNum:", quesNum);
			Toast.makeText(this, "上次练习到第" + quesNum + "题，本次将继续上次进度",
					Toast.LENGTH_SHORT).show();
		} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
				&& SharedPreferencesUtil
						.getBooleanPreference(Constent.MUTIPLECHOICE)) {
			quesNum = SharedPreferencesUtil
					.getIntPreference(Constent.MUTIPLECHOICE_PROCESS);
			L.v(TAG, "initData_getMutipleNum:", quesNum);
			Toast.makeText(this, "上次练习到第" + quesNum + "题，本次将继续上次进度",
					Toast.LENGTH_SHORT).show();
		} else if (trainingType == Constent.JUDGE_TRAINING
				&& SharedPreferencesUtil.getBooleanPreference(Constent.JUDGE)) {
			quesNum = SharedPreferencesUtil
					.getIntPreference(Constent.JUDGE_PROCESS);
			L.v(TAG, "initData_getJudgeNum:", quesNum);
			Toast.makeText(this, "上次练习到第" + quesNum + "题，本次将继续上次进度",
					Toast.LENGTH_SHORT).show();
		} else if (trainingType == Constent.WRONG_JUDGE_TRAINING) {
			quesNum = 0;
		} else if (trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			quesNum = 0;
		} else if (trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			quesNum = 0;
		}
	}

	private void initQuestionInfo(Object questionInfo) {
		isChecking = false;
		if (trainingType == Constent.JUDGE_TRAINING
				| trainingType == Constent.WRONG_JUDGE_TRAINING) {
			JudgeQuestion judgeInfo = (JudgeQuestion) questionInfo;
			L.v(TAG, "initQuestionInfo", judgeInfo.toString());
			serial_num.setText(judgeInfo.getSerial_num() + ".");
			questionContent.setText(judgeInfo.getQuestionContent());
			selection_a.setText("正确");
			selection_b.setText("错误");
			correctAnswer = judgeInfo.getAnswer();
		} else {
			ChoiceQuestion choiceInfo = (ChoiceQuestion) questionInfo;
			L.v(TAG, "initQuestionInfo", choiceInfo.toString());
			serial_num.setText(choiceInfo.getSerial_num() + ".");
			questionContent.setText(choiceInfo.getQuestionContent());

			selection_a.setText("A. " + choiceInfo.getSelection_a());
			selection_b.setText("B. " + choiceInfo.getSelection_b());
			selection_c.setText("C. " + choiceInfo.getSelection_c());
			selection_d.setText("D. " + choiceInfo.getSelection_d());
			correctAnswer = choiceInfo.getAnswer();
		}
	}

	private void clearQuestionView() {
		if (trainingType == Constent.JUDGE_TRAINING
				| trainingType == Constent.WRONG_JUDGE_TRAINING) {
			serial_num.setText("");
			questionContent.setText("");
			selection_a.setText("");
			selection_b.setText("");
		} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
				| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			point_a.setChecked(false);
			point_b.setChecked(false);
			point_c.setChecked(false);
			point_d.setChecked(false);
			serial_num.setText("");
			questionContent.setText("");
			selection_a.setText("");
			selection_b.setText("");
			selection_c.setText("");
			selection_d.setText("");
		} else {
			serial_num.setText("");
			questionContent.setText("");
			selection_a.setText("");
			selection_b.setText("");
			selection_c.setText("");
			selection_d.setText("");
		}
		answer_desc.setVisibility(View.GONE);
	}

	private void getWrongQuesListFromDB() {
		wrongQuesIdList = dbUtil.getWrongQuestionList(trainingType);
	}

	private void getQuestionInfoFromDB(int quesNumber) {
		Object questionInfo = null;
		if (trainingType == Constent.JUDGE_TRAINING) {
			questionInfo = dbUtil.getJudgeInfoByID(String.valueOf(quesNumber),
					Constent.JUDGE_TRAINING);
		} else if (trainingType == Constent.SINGLECHOICE_TRAINING) {
			L.v(TAG, "getQuestionInfoFromDB_quesNum:", quesNum);
			questionInfo = dbUtil.getChoiceInfoByID(String.valueOf(quesNumber),
					Constent.SINGLECHOICE_TRAINING);
		} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING) {
			questionInfo = dbUtil.getChoiceInfoByID(String.valueOf(quesNumber),
					Constent.MUTIPLECHOICE_TRAINING);
		} else if (trainingType == Constent.WRONG_JUDGE_TRAINING) {
			L.v(TAG, "wrongQuesIdList.size()", wrongQuesIdList.size());
			L.v(TAG, "quesNum", quesNumber);
			if (wrongQuesIdList.size() > quesNumber) {
				String quesId = wrongQuesIdList.get(quesNumber);
				questionInfo = dbUtil.getJudgeInfoByID(quesId,
						Constent.WRONG_JUDGE_TRAINING);
			} else {
				showDialog(R.string.no_errorQues_record, NO_ERROR_RECORD_TYPE);
				return;
			}
		} else if (trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			L.v(TAG, "wrongQuesIdList.size()", wrongQuesIdList.size());
			L.v(TAG, "quesNum", quesNumber);
			if (wrongQuesIdList.size() > quesNumber) {
				String quesId = wrongQuesIdList.get(quesNumber);
				questionInfo = dbUtil.getChoiceInfoByID(quesId,
						Constent.WRONG_SINGLECHOICE_TRAINING);
			} else {
				showDialog(R.string.no_errorQues_record, NO_ERROR_RECORD_TYPE);
				return;
			}
		} else if (trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			if (wrongQuesIdList.size() > quesNumber) {
				String quesId = wrongQuesIdList.get(quesNumber);
				questionInfo = dbUtil.getChoiceInfoByID(quesId,
						Constent.WRONG_MUTIPLECHOICE_TRAINING);
			} else {
				showDialog(R.string.no_errorQues_record, NO_ERROR_RECORD_TYPE);
				return;
			}
		}
		initQuestionInfo(questionInfo);
	}

	@Override
	protected void onStop() {
		saveProcess();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_button:
			saveProcess();
			finish();
			break;
		case R.id.reset_process:
			showDialog(R.string.reset_process, RESET_PROCESS_TYPE);
			break;
		case R.id.submit:
			checkUserAnswer();
			break;
		case R.id.selection_layout_a:
			if (trainingType == Constent.JUDGE_TRAINING
					| trainingType == Constent.WRONG_JUDGE_TRAINING) {
				userAnswer.add("√");
				checkUserAnswer();
			} else if (trainingType == Constent.SINGLECHOICE_TRAINING
					| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
				userAnswer.add("A");
				checkUserAnswer();
			} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
					| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
				if (point_a.isChecked()) {
					userAnswer.remove("A");
				} else {
					userAnswer.add("A");
				}
				point_a.setChecked(!point_a.isChecked());
			}
			break;
		case R.id.selection_layout_b:
			if (trainingType == Constent.JUDGE_TRAINING
					| trainingType == Constent.WRONG_JUDGE_TRAINING) {
				userAnswer.add("×");
				checkUserAnswer();
			} else if (trainingType == Constent.SINGLECHOICE_TRAINING
					| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
				userAnswer.add("B");
				checkUserAnswer();
			} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
					| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
				if (point_b.isChecked()) {
					userAnswer.remove("B");
				} else {
					userAnswer.add("B");
				}
				point_b.setChecked(!point_b.isChecked());
			}
			break;
		case R.id.selection_layout_c:
			if (trainingType == Constent.SINGLECHOICE_TRAINING
					| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
				userAnswer.add("C");
				checkUserAnswer();
			} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
					| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
				if (point_c.isChecked()) {
					userAnswer.remove("C");
				} else {
					userAnswer.add("C");
				}
				point_c.setChecked(!point_c.isChecked());
			}
			break;
		case R.id.selection_layout_d:
			if (trainingType == Constent.SINGLECHOICE_TRAINING
					| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
				userAnswer.add("D");
				checkUserAnswer();
			} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
					| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
				if (point_d.isChecked()) {
					userAnswer.remove("D");
				} else {
					userAnswer.add("D");
				}
				point_d.setChecked(!point_d.isChecked());
			}
			break;

		default:
			break;
		}
	}

	private void checkUserAnswer() {
		if (!isChecking) {
			isChecking = true;
			answer_desc.setVisibility(View.VISIBLE);
			StringBuilder finalAnswer = new StringBuilder();
			for (Iterator it = userAnswer.iterator(); it.hasNext();) {
				finalAnswer.append(it.next().toString());
			}
			Message getNext = handler.obtainMessage();
			getNext.what = GET_NEXT_QUES;
			if (finalAnswer.toString().equals(correctAnswer)) {
				if (trainingType == Constent.WRONG_JUDGE_TRAINING
						| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING
						| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
					dbUtil.deleteWrongQuestionBySerialNum(
							wrongQuesIdList.get(quesNum), trainingType);
					Toast.makeText(this, "该题目已在错误记录中删除", Toast.LENGTH_SHORT)
							.show();
				}
				answer_desc.setText("恭喜，答对了！");
				handler.sendMessageDelayed(getNext, 500);
			} else {
				if(SharedPreferencesUtil.getBooleanPreference(Constent.AUTO_SAVEEROR_QUES)){
					if (trainingType == Constent.JUDGE_TRAINING
							| trainingType == Constent.SINGLECHOICE_TRAINING
							| trainingType == Constent.MUTIPLECHOICE_TRAINING) {
						dbUtil.saveWrongQuestion(String.valueOf(quesNum),
								trainingType);
					}
				}
				answer_desc.setText("答错啦！" + "\n" + "正确答案：" + correctAnswer);
				handler.sendMessageDelayed(getNext, 1500);
			}
			userAnswer.clear();
		}
	}

	private void getNextQuestion() {
		quesNum++;
		L.v(TAG, "quesNum", quesNum);
		L.v(TAG, "totalCount", String.valueOf(totalCount));
		if (quesNum >= totalCount) {
			L.v(TAG, "quesNum >= totalCount?", quesNum > totalCount);
			showDialog(R.string.over_training_tips, OVER_TRAINING_TYPE);
		} else {
			clearQuestionView();
			getQuestionInfoFromDB(quesNum);
		}
	}

	private void showDialog(int msgId, final int type) {
		tipsDialog = new AlertDialog.Builder(this)
				.setMessage(msgId)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (type == OVER_TRAINING_TYPE) {
									if (trainingType == Constent.WRONG_JUDGE_TRAINING
											| trainingType == Constent.WRONG_MUTIPLECHOICE_TRAINING
											| trainingType == Constent.WRONG_SINGLECHOICE_TRAINING) {
										finish();
									} else {
										quesNum = 1;
										getQuestionInfoFromDB(quesNum);
									}
								} else if (type == RESET_PROCESS_TYPE) {
									quesNum = 1;
									getQuestionInfoFromDB(quesNum);
								} else {
									finish();
								}
							}
						}).create();
		if (type == OVER_TRAINING_TYPE | type == RESET_PROCESS_TYPE) {
			tipsDialog.setButton(Dialog.BUTTON_NEGATIVE, "取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (type == RESET_PROCESS_TYPE) {
								tipsDialog.dismiss();
							} else {
								finish();
							}
						}
					});
		}
		tipsDialog.setCancelable(false);
		tipsDialog.show();
	}

	@Override
	public void onBackPressed() {
		saveProcess();
		super.onBackPressed();
	}

	private void saveProcess() {
		if (trainingType == Constent.SINGLECHOICE_TRAINING
				&& SharedPreferencesUtil
						.getBooleanPreference(Constent.SINGLECHOICE)) {
			SharedPreferencesUtil.saveIntPreference(
					Constent.SINGLECHOICE_PROCESS, quesNum);
			L.v(TAG, "saveProcess_singleNum", quesNum);
		} else if (trainingType == Constent.MUTIPLECHOICE_TRAINING
				&& SharedPreferencesUtil
						.getBooleanPreference(Constent.MUTIPLECHOICE)) {
			SharedPreferencesUtil.saveIntPreference(
					Constent.MUTIPLECHOICE_PROCESS, quesNum);
			L.v(TAG, "saveProcess_mutipleNum", quesNum);
		} else if (trainingType == Constent.JUDGE_TRAINING
				&& SharedPreferencesUtil.getBooleanPreference(Constent.JUDGE)) {
			SharedPreferencesUtil.saveIntPreference(Constent.JUDGE_PROCESS,
					quesNum);
			L.v(TAG, "saveProcess_judgeNum", quesNum);
		}
	}

}
