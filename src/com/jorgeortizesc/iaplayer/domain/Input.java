package com.jorgeortizesc.iaplayer.domain;

import javafx.util.Duration;

public class Input extends IAElement {

	private String[] answers;

	public Input(String title, Duration time, String[] answers) {
		super(title, time, null);
		this.answers = answers;
	}

	public Input(String title, Duration time, Duration expl, String[] answers) {
		super(title, time, expl);
		this.answers = answers;
	}

	public String[] getAnswers() {
		return answers;
	}
}
