/*
 * CommonGui.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * This abstract class provides the implementation of common functionality
 * between Gui classes.
 * 
 * @author Pedro Oliveira
 * @version 1.3
 * @see JFrame
 * @see ClientGui
 * @see ServerGui
 * @see GuiException
 */
public abstract class CommonGui extends JFrame {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;	
	
	/**
	 * The properties helper which allows the mainpulation of configuration 
	 * properties.
	 */
	private static PropertyHelper propHelper;
	
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
		propHelper = new PropertyHelper(prop);
	}	
	
	/**
	 * This constructor sets the frame title with the one given as argument
	 * and changes the swing look and feel to the system's look and feel. If
	 * that is not possible the the look and feel will remain with the default.
	 *  
	 * @param title the title to set the Gui frame with.
	 */
	public CommonGui(String title) {
		super(title);		

		// Set the swing look and feel with the system's own look and feel.
		try {
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			
			// If an exception is raised just print stack trace.
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates each menu item of the configuration menu. Each item
	 * is identified by a name and has an associated property of the properties
	 * configuration file.
	 */
	public JMenuItem buildConfigMenuItem(String itemName, String property) {
		JMenuItem menuItem = new JMenuItem(itemName);
		ActionListener listener = new ChangeConfigListener(itemName, property);
		menuItem.addActionListener(listener);		
		return menuItem;
	}	
	
	/**
	 * This method creates a message dialog to be displayed in the 
	 * {@code frame} passed as argument and with the title, message and 
	 * message type extracted from the {@code GuiException} object.
	 * 
	 * @param frame the {@code JFrame} in which the dialog is displayed.
	 * @param e the {@code GuiException} with the information to fill in 
	 * the error message dialog.
	 */
	public void showMessageDialog(JFrame frame, GuiException e) { 
		String messageTitle = e.getMessageTitle();
		String message = e.getMessage();
		int messageType = e.getMessageType();
		
		// Display the message dialog using the exception information.
		JOptionPane.showMessageDialog(frame, message, messageTitle, 
				messageType);
	}	

	/**
	 * This class handles exit events in menu items, exit buttons or window
	 * closing buttons.
	 * 
	 * @see WindowAdapter
	 * @see ActionListener
	 */
	protected class ExitGui extends WindowAdapter implements ActionListener {

		/**
		 * This frame holds the reference to the {@code JFrame} which the
		 * actions will be performed.
		 */
		private JFrame frame;

		/**
		 * The message title of the closing window of the {@code frame}.
		 */
		private String messageTitle;

		/**
		 * Creates a reference to this object using the specified arguments.
		 * 
		 * @param frame the frame to execute the exit actions.
		 * @param messageTitle the title message of the exit window.
		 */
		public ExitGui(JFrame frame, String messageTitle) {
			this.frame = frame;
			this.messageTitle = messageTitle;
		}

		/**
		 * This method will popup a window for the user to confirm if he
		 * really wants to quit the application. 
		 */
		private void exit() {
			int answer = JOptionPane.showConfirmDialog(this.frame, 
					"Are you sure you want to quit?",
					messageTitle,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {
				System.exit(0);
			}	
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			
			// Invoked by the action listener (menu items or buttons).
			exit();    	    
		}

		@Override
		public void windowClosing(WindowEvent event) {
			
			// Invoked when the mouse presses the closing "X" button.
			exit();		
			this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);   
		}
	}    
	
	/**
	 * This class handles any default configuration change, by showing a popup
	 * menu where the user can change the default value. 
	 */
	private class ChangeConfigListener implements ActionListener {	
		
		/**
		 * The name of the configuration to be updated.
		 */
		private String configName;
		
		/**
		 * The name of the property in the suncertify.properties to be changed.
		 */
		private String propertyName;
		
		/**
		 * Constructs a listener with the specified configuration name and 
		 * property name.
		 */
		public ChangeConfigListener(String configName, 
				String propertyName) {
			this.configName = configName;
			this.propertyName = propertyName;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			
			// Open popup menu and query user for the new configuration.
			JLabel fieldLabel = new JLabel(this.configName + ":");
			String val = propHelper.getPropertyAsString(propertyName);
			JTextField field = new JTextField(val);
			Object[] objField = {fieldLabel, field};				
			Object[] buttonTitles = new String[] {"Ok","Cancel"};
			int answer = JOptionPane.showOptionDialog(CommonGui.this, 
					objField, "Changing Default Configurations", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					buttonTitles,
					buttonTitles[0]);		

			// Evaluate the user response.
			String newValue = null;
			if (answer == JOptionPane.NO_OPTION || 
					answer == JOptionPane.CLOSED_OPTION) {    	 
				return;	    
			} else {
				newValue = field.getText().trim();
			}			
			
			// Update property helper and save properties file.
			propHelper.setProperty(propertyName, newValue);
			Properties p = propHelper.toProperties(); 
			try {
				Utilities.saveProperties(p, "suncertify.properties");
			} catch (IOException e) {
				GuiException ge = new GuiException("Default Configurations", 
						"Could not save configurations!",  
						JOptionPane.ERROR_MESSAGE);
				showMessageDialog(CommonGui.this, ge);
			}
			
		}		
	}	
}
