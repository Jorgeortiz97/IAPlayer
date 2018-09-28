package com.jorgeortizesc.iaplayer.domain;

import javafx.util.Duration;

public class IAElement {
	private String title;
	private Duration time, explanation;
	private boolean active = true;

	public IAElement(String title, Duration time, Duration explanation) {
		this.title = title;
		this.time = time;
		this.explanation = explanation;
	}

	public IAElement(String title, Duration time) {
		this(title, time, null);
	}

	public String getTitle() {
		return title;
	}


	public Duration getTime() {
		return time;
	}

	public Duration getExplanation() {
		return explanation;
	}

	public void setActive(boolean state) {
		active = state;
	}

	public boolean isActive() {
		return active;
	}
}
