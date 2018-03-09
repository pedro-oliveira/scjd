/*
 * ConfigPanel.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.MaskFormatter;

import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * This class is a common panel between the various user interfaces. It is used
 * in the server and in the client with and without network connection. In this
 * panel the user is able to configure options regarding its working mode. <p>
 * 
 * Depending on the application working mode the panel will have some options
 * enabled or disabled and some common fields might have slightly different
 * meanings.
 * 
 * @author Pedro Oliveira
 * @version 1.4
 * @see JPanel
 * @see ConfigOptions
 * @see ApplicationMode
 * @see ActionListener
 */
public class ConfigPanel extends JPanel {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default path to the database in the client machine. Used in the 
	 * standalone mode.
	 */
	private static final String DFT_LOCAL_PATH;
	
	/**
	 * The default path to the database in the server machine. Used in the
	 * server mode.
	 */
	private static final String DFT_SERVER_PATH;
	
	/**
	 * The default ip address or url to the server machine. Used in the 
	 * network mode in the client interface.
	 */
	private static final String DFT_NET_PATH;	
	
	/**
	 * The default port number where the server listens showed in client gui.
	 */
	private static final String DFT_PORT;
	
	/**
	 * The default port number where the server listens showed in server gui.
	 */
	private static final String DFT_SERVER_PORT;	

	/**
	 * The port number field mask (5 digits). 
	 */
	private static final String PORT_FIELD_MASK = "#####";

	/**
	 * The file extension of the database files.
	 */
	private static final String FILE_EXTENSION;    

	/**
	 * This initializer block loads property values from the application
	 * properties file.
	 */
	static {	  
		Properties prop;
		try {
			prop = Utilities.loadProperties("suncertify.properties");
		} catch (IOException e) {
			prop = System.getProperties();
		}		
		PropertyHelper propHelper = new PropertyHelper(prop);
		DFT_LOCAL_PATH = propHelper.getPropertyAsString("user.client.locPath");
		DFT_SERVER_PATH = propHelper.getPropertyAsString("user.server.Path");
		DFT_NET_PATH = propHelper.getPropertyAsString("user.client.netPath");
		DFT_PORT = propHelper.getPropertyAsString("user.client.port");
		DFT_SERVER_PORT = propHelper.getPropertyAsString("user.server.port");
		FILE_EXTENSION = propHelper.getPropertyAsString("swing.fileExtension");
	}    

	/**
	 * The {@code JTextField} with the database path. Which is a file location
	 * or a machine location (url).
	 */
	private JTextField pathField;

	/**
	 * The change path button allows the user to choose a different database
	 * path file or a machine mapped in the client machine network.
	 */
	private JButton changePathButton;

	/**
	 * The port number text field which only allows numbers to be filled.
	 */    
	private JFormattedTextField portField;

	/**
	 * The reference to the object which will be updated with the user choices.
	 */
	private ConfigOptions options;

	/**
	 * Creates an instance of the configuration panel object. This panel will
	 * vary depending on the mode the application is running.
	 * 
	 * @param mode the mode the application was invoked, the valid options are
	 * <tt>STANDALONE</tt>, <tt>NETWORK</tt> or <tt>SERVER</tt>.
	 */
	public ConfigPanel(ApplicationMode mode) {
		String portNumber = "";
		
		// Initialize the configuration options depending on the working mode.
		switch (mode) {
		case STANDALONE :
			this.options = new ConfigOptions(mode, DFT_LOCAL_PATH);
			portNumber = DFT_PORT;
			break;
		case SERVER :
			this.options = new ConfigOptions(mode, DFT_SERVER_PATH);
			portNumber = DFT_SERVER_PORT;
			break;
		case NETWORK :
			this.options = new ConfigOptions(mode, DFT_NET_PATH);
			portNumber = DFT_PORT;
			break;
		}

		// Create the panel layout using the most flexible layout.
		GridBagLayout gridbag = new GridBagLayout();		
		this.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();

		// Add padding between components.
		constraints.insets = new Insets(2, 2, 2, 2);

		/*
		 * Add components to layout starting from left to rigth and 
		 * from top to bottom. Add path label.
		 */
		JLabel dbPathLabel = new JLabel("Database path:");
		gridbag.setConstraints(dbPathLabel, constraints);
		this.add(dbPathLabel);        

		// Add path text field. 
		this.pathField = new JTextField(this.options.getDbPath());	
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(pathField, constraints);
		this.add(pathField);   

		// Add change path button.
		this.changePathButton = new JButton("Change");
		this.changePathButton.addActionListener(new ChangePath());
		constraints.gridwidth = GridBagConstraints.REMAINDER; 
		gridbag.setConstraints(this.changePathButton, constraints);
		this.add(this.changePathButton);

		// Add port label.
		JLabel portLabel = new JLabel("Server port:");
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(portLabel, constraints);
		this.add(portLabel);

		// Add port text field with a mask (maximum of 5 digits).     
		MaskFormatter formatter = createFormatter(PORT_FIELD_MASK);
		this.portField = new JFormattedTextField(formatter);
		this.portField.setColumns(PORT_FIELD_MASK.length());	
		this.portField.setText(portNumber);
		this.portField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		this.portField.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent e) { }
					
					@Override
					public void insertUpdate(DocumentEvent e) {
						updatePort();	
					}
					
					@Override
					public void removeUpdate(DocumentEvent e) { }
				});
		this.portField.setName("Server Port:");
		constraints.gridwidth = GridBagConstraints.REMAINDER; 
		constraints.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(this.portField, constraints);
		this.add(this.portField);

		// For standalone applications disable the port field.
		if (mode == ApplicationMode.STANDALONE) {
			this.portField.setEnabled(false);
		}
	}	

	/**
	 * Updates the {@code options} variable after the user changed the text
	 * field.
	 */
	private void updatePort() {
		String port = this.portField.getText().trim();
		this.options.setPort(port);
	}    

	/**
	 * Creates a {@code MaskFormatter} with the specified mask. A 
	 * <tt>null</tt> value is returned if a {@code ParseException} occurs.
	 * 
	 * @param mask the mask to use in formatting process.
	 * @return the {@code MaskFormatter} with the specified mask.
	 */
	public static MaskFormatter createFormatter(String mask) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(mask);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formatter;
	}

	/**
	 * Returns the configuration options filled by the user in the panel.
	 * 
	 * @return a {@code ConfigOptions} with the options inputed by the user.
	 */
	public ConfigOptions getConfigOptions() {
		updatePort();
		this.options.setDbPath(this.pathField.getText());
		return this.options;
	}

	/**
	 * This method disables all fields in the {@code ConfigPanel}.
	 */
	public void disableFields() {
		this.pathField.setEnabled(false);
		this.portField.setEnabled(false);
		this.changePathButton.setEnabled(false);
	}
	
	/**
	 * This class handles the change path event. This occur when the user 
	 * presses the change path button. 
	 */
	private class ChangePath implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {	    	    
			String startingDir = System.getProperty("user.dir");
			JFileChooser chooser = new JFileChooser(startingDir);
			
			if (options.getApplicationMode() == ApplicationMode.NETWORK) {				
				/* 
				 * Only allow user to select directories (case where the 
			 	 * remote host is mapped as network drive).
				 */					
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			} else {				
				/*
				 * For the server and standalone modes only files with the
				 * known database extension should be allowed.
				 */
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new DbFileChooser());
			}

			// 
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				pathField.setText(chooser.getSelectedFile().toString());
				options.setDbPath(pathField.getText());
			}
		}
	}
	
	/**
	 * This file chooser display files ending in ".ext" or any other object
	 * (directory or other selectable devices). <p>
	 * 
	 * The description is set with the desired extension. 
	 * 
	 */
	private class DbFileChooser extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isFile()) {
				return f.getName().endsWith(FILE_EXTENSION);
			} else {
				return true;
			}
		}

		@Override
		public String getDescription() {
			return FILE_EXTENSION;
		}	
	}
}

