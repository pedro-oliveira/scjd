/*
 * ServerGui.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */


package suncertify.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import suncertify.network.ServerStarter;
import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * This class represents the Gui window in which the user interacts
 * when he launches the application in the server mode. <p>
 * 
 * This class defines the user interface for configuration the server 
 * connection options and provides the functionality for starting the
 * server.
 * 
 * @author Pedro Oliveira
 * @version 1.6
 * @see CommonGui
 * @see ActionListener
 * @see ConfigPanel
 */
public class ServerGui extends CommonGui {
	
	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;	

	/**
	 * The {@code String} with the title of the server window.
	 */
	private static final String SERVER_TITLE;

	/**
	 * The {@code String} with the text of initial status of the server. 
	 */
	private static final String INITIAL_STATUS = "Enter configuration " +
			"parameters and click in the Start button.";

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
			throw new ExceptionInInitializerError(e.getMessage());
		}		
		PropertyHelper propHelper = new PropertyHelper(prop);	
		SERVER_TITLE = propHelper.getPropertyAsString("swing.server.title");
	}    

	/**
	 * The configuration panel in which the user selects the server options.
	 */
	private ConfigPanel configPanel;

	/**
	 * The start button in the server window.
	 */
	private JButton startButton;

	/**
	 * The exit button in the server window.
	 */
	private JButton exitButton;

	/**
	 * A {@code JLabel} with the server status information.
	 */
	private JLabel statusLabel;   

	/**
	 * Creates the structure of the server gui window and displays it
	 * on the screen.
	 */
	public ServerGui() {
		super(SERVER_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and add the configuration panel in the server mode.
		this.configPanel = new ConfigPanel(ApplicationMode.SERVER);
		this.add(this.configPanel, BorderLayout.NORTH);        

		// Create and add the start and exit buttons panel.
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.startButton = new JButton("Start");        
		this.startButton.addActionListener(new StartServer());
		this.exitButton = new JButton("Exit");
		this.exitButton.addActionListener(new ExitGui(this, "Closing Server"));       
		buttonsPanel.add(this.startButton);
		buttonsPanel.add(this.exitButton);
		this.add(buttonsPanel, BorderLayout.CENTER);

		// Create and add status label in the bottom of the screen.
		this.statusLabel = new JLabel(INITIAL_STATUS);
		Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		this.statusLabel.setBorder(border);
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(this.statusLabel, BorderLayout.CENTER);
		this.add(statusPanel, BorderLayout.SOUTH);	        
		
		// Creates and adds the menu bar to the server window frame.
		buildMenuBar();

		this.addWindowListener(new ExitGui(this, "Closing Server"));
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}


	/**
	 * Creates and adds the menu bar and the respective menu items.
	 */
	private void buildMenuBar() {

		// Create the File menu.
		JMenu fileMenu = new JMenu("File");
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		String exitMessage = "Closing Application";
		quitMenuItem.addActionListener(new ExitGui(this, exitMessage));
		fileMenu.add(quitMenuItem);		
		
		// Create the Configurations menu.
		JMenu configMenu = new JMenu("Default Configurations");
		Collection<JMenuItem> configMenus = new LinkedList<JMenuItem>();
		configMenus.add(buildConfigMenuItem("Local Path", "user.server.Path"));
		configMenus.add(buildConfigMenuItem("Port Number", "user.server.port"));		
		for (JMenuItem item : configMenus) {
			configMenu.add(item);
		}
		
		// Create the menu bar add the menus and add it to the frame.
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(configMenu);
		this.setJMenuBar(menuBar);		
	}	
	
	/**
	 * This class will be called when the start button is pressed. This
	 * class will launch the actual server using the configuration options
	 * retrieved from the configuration panel. <p>
	 * 
	 * In this method the {@code statusLabel} variable gets its value 
	 * updated while the server is being started. <p>
	 * 
	 * In the end in case of success all the fields in the configuation panel
	 * and the start button will be deactivated. In case of error an error
	 * message dialog will be displayed with the respective reason for the 
	 * error.
	 */
	private class StartServer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			ConfigOptions options = configPanel.getConfigOptions();
			ServerStarter server = new ServerStarter(options);
			
			try {
				server.start(statusLabel);
			} catch (GuiException e) {
				showMessageDialog(ServerGui.this, e);
				return;
			}
			
			// If server started successfully disable gui fields.
			configPanel.disableFields();
			startButton.setEnabled(false);
		}	
	}
}

