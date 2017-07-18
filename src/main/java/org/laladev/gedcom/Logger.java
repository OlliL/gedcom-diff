package org.laladev.gedcom;

import javax.swing.JTextArea;

public class Logger {
	static private final String newline = "\n";
	private JTextArea textArea;

	public Logger(JTextArea textArea) {
		super();
		this.textArea = textArea;
	}

	public void append(String log) {
		textArea.append(log + newline);
	}

}
