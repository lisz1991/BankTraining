package com.lisz.banktraining.bean;

public class JudgeQuestion {

	private String serial_num;

	public String getSerial_num() {
		return serial_num;
	}

	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}

	private String questionContent;
	private String answer;

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String toString(){
		return "JudgeQuestion_serialNum: "+getSerial_num()+" questionContent: "+getQuestionContent()
				+" answer: "+getAnswer();
	}
}
