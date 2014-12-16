package com.lisz.banktraining.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lisz.banktraining.bean.ChoiceQuestion;
import com.lisz.banktraining.bean.JudgeQuestion;
import com.lisz.banktraining.util.BankUtility;
import com.lisz.banktraining.util.Constent;
import com.lisz.banktraining.util.L;

public class DBUtil {
	private static final String TAG = "DBUtil";
	private static DBUtil dbUtil;
	private SQLiteDatabase bankDB;
	private String databasePath;

	public static DBUtil getInstance() {
		if (dbUtil == null) {
			dbUtil = new DBUtil();
		}
		return dbUtil;
	}

	public DBUtil() {
		databasePath = BankUtility.configureGetDatabasePath();
		L.v(TAG, "databasePath", databasePath);
	}

	private void openDatabase() {
		L.v(TAG, "Open DataBase : " + databasePath);
		bankDB = SQLiteDatabase.openDatabase(databasePath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	private void closeDatabase() {
		SQLiteDatabase.releaseMemory();
		bankDB.close();
	}

	/**
	 * 通过题目序号获取判断题信息
	 * 
	 * @param serialNum
	 * @param fromType
	 * @return JudgeQuestion
	 */
	public JudgeQuestion getJudgeInfoByID(String serialNum, int selectType) {
		JudgeQuestion judgeInfo = new JudgeQuestion();
		String judgeSql = null;
		if (selectType == Constent.JUDGE_TRAINING) {
			judgeSql = "select * from " + Constent.JUDGE_TABLE
					+ " where serial_num = ? ";
		} else if (selectType == Constent.WRONG_JUDGE_TRAINING) {
			judgeSql = "select * from " + Constent.JUDGE_TABLE
					+ " where serial_num = ? ";
		}
		openDatabase();
		L.v(TAG, "getJudgeInfoByID_bankDB is null? ", bankDB == null);
		Cursor judgeCursor = bankDB.rawQuery(judgeSql,
				new String[] { serialNum });
		if (judgeCursor == null) {
			L.v(TAG, "selectJudgeInfoByID_failed_ID:", serialNum);
		} else if (!judgeCursor.moveToFirst()) {
			L.v(TAG, "selectJudgeInfoByID", "Cursor has no records");
		} else {
			judgeInfo.setSerial_num(judgeCursor.getString(judgeCursor
					.getColumnIndex("serial_num")));
			judgeInfo.setQuestionContent(judgeCursor.getString(judgeCursor
					.getColumnIndex("question_content")));
			judgeInfo.setAnswer(judgeCursor.getString(judgeCursor
					.getColumnIndex("answer")));
		}

		L.v(TAG, "selectJudgeInfoByID", judgeInfo.toString());
		judgeCursor.close();
		closeDatabase();
		return judgeInfo;
	}

	/**
	 * 通过题目序号获取选择题信息
	 * 
	 * @param serialNum
	 * @param fromType
	 * @return ChoiceQuestion
	 */
	public ChoiceQuestion getChoiceInfoByID(String serialNum, int selectType) {
		ChoiceQuestion choiceInfo = new ChoiceQuestion();
		String choiceSql = null;
		if (selectType == Constent.SINGLECHOICE_TRAINING) {
			choiceSql = "select * from " + Constent.SINGLE_TABLE
					+ " where serial_num = ? ";
		} else if (selectType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			choiceSql = "select * from " + Constent.SINGLE_TABLE
					+ " where serial_num = ? ";
		} else if (selectType == Constent.MUTIPLECHOICE_TRAINING) {
			choiceSql = "select * from " + Constent.MUTIPLE_TABLE
					+ " where serial_num = ? ";
		} else if (selectType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			choiceSql = "select * from " + Constent.MUTIPLE_TABLE
					+ " where serial_num = ? ";
		}
		openDatabase();
		L.v(TAG, "getJudgeInfoByID_bankDB is null? ", bankDB == null);
		Cursor choiceCursor = bankDB.rawQuery(choiceSql,
				new String[] { serialNum });
		if (choiceCursor == null) {
			L.v(TAG, "selectChoiceInfoByID_failed_ID:", serialNum);
		} else if (!choiceCursor.moveToFirst()) {
			L.v(TAG, "selectChoiceInfoByID", "Cursor has no records");
		} else {
			choiceInfo.setSerial_num(choiceCursor.getString(choiceCursor
					.getColumnIndex("serial_num")));
			choiceInfo.setQuestionContent(choiceCursor.getString(choiceCursor
					.getColumnIndex("question_content")));
			choiceInfo.setSelection_a(choiceCursor.getString(choiceCursor
					.getColumnIndex("selection_a")));
			choiceInfo.setSelection_b(choiceCursor.getString(choiceCursor
					.getColumnIndex("selection_b")));
			choiceInfo.setSelection_c(choiceCursor.getString(choiceCursor
					.getColumnIndex("selection_c")));
			choiceInfo.setSelection_d(choiceCursor.getString(choiceCursor
					.getColumnIndex("selection_d")));
			choiceInfo.setAnswer(choiceCursor.getString(choiceCursor
					.getColumnIndex("answer")));
		}
		L.v(TAG, "selectChoiceInfoByID", choiceInfo.toString());
		choiceCursor.close();
		closeDatabase();
		return choiceInfo;
	}

	/**
	 * 保存错误题目的序号到数据库中
	 * 
	 * @param serialNum
	 * @param saveType
	 */
	public void saveWrongQuestion(String serialNum, int saveType) {
		String tableName = null;
		if (saveType == Constent.SINGLECHOICE_TRAINING) {
			tableName = Constent.WRONG_SINGLE_TABLE;
		} else if (saveType == Constent.MUTIPLECHOICE_TRAINING) {
			tableName = Constent.WRONG_MUTIPLE_TABLE;
		} else if (saveType == Constent.JUDGE_TRAINING) {
			tableName = Constent.WRONG_JUDGE_TABLE;
		}
		openDatabase();
		ContentValues values = new ContentValues();
		values.put("serial_num", serialNum);
		bankDB.insert(tableName, null, values);
		closeDatabase();
	}

	public ArrayList<String> getWrongQuestionList(int selectType) {
		ArrayList<String> quesList = new ArrayList<String>();
		String tableName = null;
		if (selectType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			tableName = Constent.WRONG_SINGLE_TABLE;
		} else if (selectType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			tableName = Constent.WRONG_MUTIPLE_TABLE;
		} else if (selectType == Constent.WRONG_JUDGE_TRAINING) {
			tableName = Constent.WRONG_JUDGE_TABLE;
		}
		openDatabase();
		Cursor wrongCursor = bankDB.query(tableName, null, null, null, null,
				null, null);
		if (wrongCursor != null && wrongCursor.getCount() > 0) {
			while (wrongCursor.moveToNext()) {
				String serialNUm = wrongCursor.getString(wrongCursor
						.getColumnIndex("serial_num"));
				quesList.add(serialNUm);
			}
		}
		wrongCursor.close();
		closeDatabase();
		return quesList;
	}

	/**
	 * 删除某一个错误问题的记录
	 * 
	 * @param serialNum
	 * @param deleteType
	 */
	public void deleteWrongQuestionBySerialNum(String serialNum, int deleteType) {
		String tableName = null;
		if (deleteType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			tableName = Constent.WRONG_SINGLE_TABLE;
		} else if (deleteType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			tableName = Constent.WRONG_MUTIPLE_TABLE;
		} else if (deleteType == Constent.WRONG_JUDGE_TRAINING) {
			tableName = Constent.WRONG_JUDGE_TABLE;
		}
		openDatabase();
		bankDB.delete(tableName, "serial_num = ?", new String[] { serialNum });
		closeDatabase();
	}

	/**
	 * 清空所有错误问题记录
	 */
	public void deleteAllWrongQuesRecords() {
		openDatabase();
		bankDB.delete(Constent.WRONG_JUDGE_TABLE, null, null);
		bankDB.delete(Constent.WRONG_MUTIPLE_TABLE, null, null);
		bankDB.delete(Constent.WRONG_SINGLE_TABLE, null, null);
		closeDatabase();
	}

	/**
	 * 获取数据库中数据总数
	 * @param selectType
	 * @return count
	 */
	public Long getQuesCount(int selectType) {
		String tableName = null;
		if (selectType == Constent.WRONG_SINGLECHOICE_TRAINING) {
			tableName = Constent.WRONG_SINGLE_TABLE;
		} else if (selectType == Constent.WRONG_MUTIPLECHOICE_TRAINING) {
			tableName = Constent.WRONG_MUTIPLE_TABLE;
		} else if (selectType == Constent.WRONG_JUDGE_TRAINING) {
			tableName = Constent.WRONG_JUDGE_TABLE;
		}else if(selectType == Constent.JUDGE_TRAINING){
			tableName = Constent.JUDGE_TABLE;
		}else if(selectType == Constent.SINGLECHOICE_TRAINING){
			tableName = Constent.SINGLE_TABLE;
		}else if(selectType == Constent.MUTIPLECHOICE_TRAINING){
			tableName = Constent.MUTIPLE_TABLE;
		}
		openDatabase();
		// 调用查找书库代码并返回数据源
		Cursor cursor = bankDB.rawQuery("select count(*) from " + tableName,
				null);
		if (cursor == null) {
			return (long) 0;
		}
		// 游标移到第一条记录准备获取数据
		cursor.moveToFirst();
		// 获取数据中的LONG类型数据
		Long count = cursor.getLong(0);
		cursor.close();
		closeDatabase();
		return count;
	}
}
