package com.jorgeortizesc.iaplayer.domain;

import javafx.util.Duration;

public class Question extends IAElement {

	private int right;
	private String[] answers;

	public Question(String title, int right, Duration time, Duration explanation, String[] answers) {
		super(title, time, explanation);
		this.right = right;
		this.answers = answers;
	}

	public Question(String title, int right, Duration time, String[] answers) {
		this(title, right, time, null, answers);
	}


	public int getRight() {
		return right;
	}

	public String[] getAnswers() {
		return answers;
	}

}
