package de.ollie.dbcomp.util;

public class Cutter {

	Cutter() {
		throw new UnsupportedOperationException("class should not instantiated.");
	}

	public static String cutQuotes(String s) {
		if (s == null) {
			return null;
		}
		if (s.startsWith("\"")) {
			s = s.substring(1);
		}
		if (s.endsWith("\"")) {
			if (s.length() > 1) {
				s = s.substring(0, s.lastIndexOf("\""));
			} else {
				s = "";
			}
		}
		return s;
	}

}