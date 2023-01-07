package org.laladev.gedcom;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.laladev.gedcom.gui.MainDialog;

public class GedcomDiff {

	private static File file1 = null;
	private static File file2 = null;

	private static void createAndShowGUI(final File file1, final File file2) {
		final JFrame frame = new JFrame("Gedcom Diff");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new MainDialog(file1, file2));
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(final String[] args) {
		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			final String arg = args[i++];

			if (arg.equals("-file1")) {
				file1 = new File(args[i++]);
			} else if (arg.equals("-file2")) {
				file2 = new File(args[i++]);
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI(file1, file2);
			}
		});
	}
}