package org.laladev.gedcom.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gedcom4j.exception.GedcomParserException;
import org.laladev.gedcom.GedcomCompare;
import org.laladev.gedcom.Logger;

public class MainDialog extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	JButton openButton1, openButton2, startButton, leerButton;
	JTextArea log;
	JFileChooser fc;
	File file1;
	File file2;
	Logger logger;

	public MainDialog(final File file1, final File file2) {
		super(new BorderLayout());

		// Create the log first, because the action listeners
		// need to refer to it.
		this.log = new JTextArea(50, 150);
		this.log.setMargin(new Insets(5, 5, 5, 5));
		this.log.setEditable(false);
		this.log.setFont(new Font("monospaced", Font.PLAIN, 12));

		final JScrollPane logScrollPane = new JScrollPane(this.log);
		this.logger = new Logger(this.log);

		// Create a file chooser
		this.fc = new JFileChooser();
		final FileNameExtensionFilter filter = new FileNameExtensionFilter("GEDCOM", "ged");
		this.fc.setFileFilter(filter);

		// Create the open button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
		this.openButton1 = new JButton("\u00d6ffne Datei 1...");
		this.openButton1.addActionListener(this);
		this.openButton2 = new JButton("\u00d6ffne Datei 2...");
		this.openButton2.addActionListener(this);
		this.startButton = new JButton("Starte Vergleich");
		this.startButton.setEnabled(false);
		this.startButton.addActionListener(this);
		this.leerButton = new JButton("leeren");
		this.leerButton.addActionListener(this);

		// For layout purposes, put the buttons in a separate panel
		final JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(this.openButton1);
		buttonPanel.add(this.openButton2);
		buttonPanel.add(this.startButton);
		buttonPanel.add(this.leerButton);

		// Add the buttons and the log to this panel.
		this.add(buttonPanel, BorderLayout.PAGE_START);
		this.add(logScrollPane, BorderLayout.CENTER);

		if (file1 != null) {
			this.selectFile1(file1);
		}
		if (file2 != null) {
			this.selectFile2(file2);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == this.openButton1) {
			final int returnVal = this.fc.showOpenDialog(MainDialog.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.selectFile1(this.fc.getSelectedFile());
			}
			this.log.setCaretPosition(this.log.getDocument().getLength());

			// Handle save button action.
		} else if (e.getSource() == this.openButton2) {
			final int returnVal = this.fc.showOpenDialog(MainDialog.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.selectFile2(this.fc.getSelectedFile());
			}
			this.log.setCaretPosition(this.log.getDocument().getLength());
		} else if (e.getSource() == this.startButton) {
			final GedcomCompare compare = new GedcomCompare(this.file1, this.file2, this.logger);
			try {
				compare.compareFilesAndLogDifferences();
			} catch (final IOException e1) {
				e1.printStackTrace();
			} catch (final GedcomParserException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == this.leerButton) {
			this.log.setText("");
		}
	}

	private void selectFile1(final File file) {
		this.file1 = file;
		// This is where a real application would open the file.
		this.openButton1.setText("Datei 1: " + this.file1.getName());
		this.enableStartIfFilesSelected();
	}

	private void selectFile2(final File file) {
		this.file2 = file;
		// This is where a real application would open the file.
		this.openButton2.setText("Datei 2: " + this.file2.getName());
		this.enableStartIfFilesSelected();
	}

	private void enableStartIfFilesSelected() {
		if (this.file1 != null && this.file2 != null) {
			this.startButton.setEnabled(true);
		}
	}

}
