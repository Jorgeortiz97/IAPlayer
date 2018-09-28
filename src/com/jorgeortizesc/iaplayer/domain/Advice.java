package com.jorgeortizesc.iaplayer.domain;

import javafx.util.Duration;

public class Advice extends IAElement {

    private String content;

    public Advice(String title, Duration time, String content) {
        super(title, time, null);
        this.content = content;
    }

    public Advice(String title, Duration time, Duration explanation, String content) {
        super(title, time, explanation);
        this.content = content;
    }

    public String getContent() {
    	return content;
    }

}
