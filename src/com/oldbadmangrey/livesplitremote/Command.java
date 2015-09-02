package com.oldbadmangrey.livesplitremote;

public enum Command {
	START_OR_SPLIT("startorsplit\r\n"), SPLIT("split\r\n"), UNSPLIT("unsplit\r\n"), SKIPSPLIT("skipsplit\r\n"), PAUSE(
			"pause\r\n"), RESUME("resume\r\n"), RESET("reset\r\n"), STARTTIMER("starttimer\r\n");

	Command(String s) {
		string = s;
	}

	private String string;

	@Override
	public String toString() {
		return string;
	}
}