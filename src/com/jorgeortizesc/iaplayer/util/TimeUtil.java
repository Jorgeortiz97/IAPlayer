package com.jorgeortizesc.iaplayer.util;

import javafx.util.Duration;

public class TimeUtil {


	public static String durationToString(Duration d) {
		int sec = (int) (d.toSeconds() % 3600);
		int hours = sec / 3600;
		sec -= hours * 3600;
		int min = sec / 60;
		sec -= min * 60;
        return String.format("%4d:%02d:%02d", hours, min, sec);
	}


	public static Duration stringToDuration(String s) {
		String[] tokens = s.split(":");
		if (tokens.length != 3) return null;
		int hours = Integer.parseInt(tokens[0]);
		int minutes = Integer.parseInt(tokens[1]);
		int seconds = Integer.parseInt(tokens[2]);
		return Duration.seconds(hours * 3600 + minutes * 60 + seconds);
	}

}
