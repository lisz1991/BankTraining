package com.lisz.banktraining.bean;

public class ChoiceQuestion {

	private String serial_num;

	public String getSerial_num() {
		return serial_num;
	}

	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}

	private String questionContent;
	private String selection_a;
	private String selection_b;
	private String selection_c;
	private String selection_d;
	private String answer;

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public String getSelection_a() {
		return selection_a;
	}

	public void setSelection_a(String selection_a) {
		this.selection_a = selection_a;
	}

	public String getSelection_b() {
		return selection_b;
	}

	public void setSelection_b(String selection_b) {
		this.selection_b = selection_b;
	}

	public String getSelection_c() {
		return selection_c;
	}

	public void setSelection_c(String selection_c) {
		this.selection_c = selection_c;
	}

	public String getSelection_d() {
		return selection_d;
	}

	public void setSelection_d(String selection_d) {
		this.selection_d = selection_d;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String toString() {
		return "ChoiceQuestion_serial_num: " + getSerial_num()
				+ " questionContent: " + getQuestionContent()
				+ " selection_a: " + getSelection_a() + " selection_b: "
				+ getSelection_b() + " selection_c: " + getSelection_c()
				+ " selection_d: " + getSelection_d() + " answer: "
				+ getAnswer();
	}
}
