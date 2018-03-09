/*
 * HelpGui.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * This class is the representation of the help/user guide window. This 
 * window contains a user guide in ".html".
 * 
 * @author Pedro Oliveira
 * @version 1.1
 * @see CommonGui
 * @see GuiException
 */
class HelpGui extends CommonGui {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The path to the file with the user guide.
	 */
	private static final String FILE_PATH = "docs" + File.separator 
			+ "userguide.html";

	/**
	 * The {@code String} title in a message of an error loading the user guide
	 * file.
	 */
	private static final String ERR_TITLE = "Could not open Help menu";
	
	/**
	 * The {@code JOptionPane} error message type.
	 */
	private static final int ERR_MSG_TYPE = JOptionPane.ERROR_MESSAGE;

	/**
	 * The {@code JEditorPane} which contains the actual user guide.
	 */
	private JEditorPane editorPane;    

	/**
	 * Creates an help menu window where the user the end user documentation
	 * that shows how to work with the application.
	 */
	public HelpGui() {
		super("Help - B & S Application");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);			
		URL helpURL = null;
		
		try {
			File file = new File(FILE_PATH);
			URI uri = file.toURI();
			helpURL = uri.toURL();
		} catch (MalformedURLException e) {
			String msg = FILE_PATH + " not valid: " + e.getMessage();
			GuiException ge = new GuiException(ERR_TITLE, msg, ERR_MSG_TYPE);
			showMessageDialog(this, ge);
			return;
		}		

		if (helpURL != null) {
			try {
				this.editorPane = new JEditorPane(helpURL);
				this.editorPane.setEditable(false);
				this.editorPane.addHyperlinkListener(new LinkUpdate());
			} catch (IOException e) {
				String msg = "Attempted to read a bad URL: " + helpURL;
				GuiException ge = new GuiException(ERR_TITLE, msg, 
						ERR_MSG_TYPE);
				showMessageDialog(this, ge);
				return;
			}
		}

		// Put the editor pane in a scroll pane.
		JScrollPane editorScrollPane = new JScrollPane(this.editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));

		this.addWindowListener(new WindowClosing());
		this.add(editorScrollPane);	
		this.setSize(900, 500);
		this.setVisible(true);
	}

	/**
	 * This class handles the window closing event by poping up an exit
	 * confirmation dialog.
	 */
	private class WindowClosing extends WindowAdapter {
		
		@Override
		public void windowClosing(WindowEvent event) {
			int answer = JOptionPane.showConfirmDialog(HelpGui.this, 
					"Are you sure you want to quit?",
					"",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (answer == JOptionPane.NO_OPTION || 
					answer == JOptionPane.CLOSED_OPTION) {
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			} else {
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		}		
	}

	/**
	 * This class activates the hyperlinks in the user guide ".html" file. 
	 */
	private class LinkUpdate implements HyperlinkListener {

		@Override
		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					editorPane.setPage(event.getURL());
				} catch(IOException e) {
					String msg = e.getMessage();
					GuiException ge = new GuiException(ERR_TITLE, msg, 
							ERR_MSG_TYPE);
					showMessageDialog(HelpGui.this, ge);
				}
			}
		}
	}
}
