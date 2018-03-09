/*
 * ClientGui.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import suncertify.db.Record;
import suncertify.utils.PropertyHelper;
import suncertify.utils.Utilities;


/**
 * Class represents the window of the client application. <p>
 * 
 * The MVC pattern is implemented for the client gui, making
 * this class the View.
 * 
 * @author Pedro Oliveira
 * @version 1.9
 * @see CommonGui
 * @see ActionListener
 * @see ConfigPanel
 * @see ApplicationMode
 */
public class ClientGui extends CommonGui {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@code String} with the title of the client window.
	 */
	private static final String TITLE;

	/**
	 * String array with column names and which also are the filter names.
	 */
	private static final String[] COLUMN_NAMES;
	
	/**
	 * The properties helper which allows the mainpulation of configuration 
	 * properties.
	 */
	private static PropertyHelper propHelper;

	/**
	 * Reference to the Gui Controller.
	 */
	private Controller controller;

	/**
	 * Table which displays the database records to the user.
	 */
	private JTable table;

	/**
	 * Array with text fields to serve as user input data.
	 */
	private JTextField[] fields;

	/**
	 * This initializer block loads property values from the application
	 * properties file.
	 */
	static {	  
		Properties prop;
		try {
			prop = Utilities.loadProperties("suncertify.properties");
		} catch (IOException e) {			
			throw new ExceptionInInitializerError(e.getMessage());
		}		
		propHelper = new PropertyHelper(prop);	
		TITLE = propHelper.getPropertyAsString("swing.title");
		COLUMN_NAMES = propHelper.getPropertiesAsStringArray("swing.columns");
	}

	/**
	 * Creates the structure of the client gui window and displays it
	 * on the screen. <p>
	 * 
	 * The first step in this process is to display a connection dialog
	 * window to the user in which he can configure the connection options. <p>
	 * 
	 * @param mode the mode the application was invoked, for the 
	 * {@code ClientGui} the valid options are <tt>STANDALONE</tt> or
	 * <tt>NETWORK</tt>.
	 */
	public ClientGui(ApplicationMode mode) {
		super(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Input configuration options to the user and create the Controller.
		initializeController(mode);

		// Creates and adds the menu bar to the client window frame.
		buildMenuBar();

		// Creates the actual screen and adds it to the frame.
		buildScreen();	

		this.addWindowListener(new ExitGui(this, "Closing Application"));
		this.pack();
		setVisible(true);
	}

	/**
	 * This method initializes the controller of the MVC pattern. This is done
	 * using the options retrieve from the configuration dialog presented to 
	 * the user. 
	 */
	private void initializeController(ApplicationMode mode) {

		// Open configuration options dialog and prompt user.
		ConfigOptions options = openConfigOptionsDialog(mode);

		// Create controller using the configured options.
		try {
			this.controller = new Controller(options);
		} catch (GuiException e) {
			showMessageDialog(this, e);
			initializeController(mode);
		}		
	}

	/**
	 * Displays a dialog to the user, where he can select various configuration
	 * parameters. The configuration options will vary depending on the 
	 * application mode.
	 */
	private ConfigOptions openConfigOptionsDialog(ApplicationMode mode) {
		Object[] buttonTitles = {"Connect", "Exit"};
		ConfigPanel configPanel = new ConfigPanel(mode);

		// Display options dialog to the user.
		int answer = JOptionPane.showOptionDialog(this, 
				configPanel,
				"Choose configuration parameters",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				buttonTitles,
				buttonTitles[0]);

		// Exit application if the user has decided to quit.
		if (answer == JOptionPane.NO_OPTION || 
					answer == JOptionPane.CLOSED_OPTION) {    	 
			System.exit(0); 	    
		}
	
		return configPanel.getConfigOptions();
	}

	
	// Methods for building client gui interface. 
	
	
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
		
		// Create the Help menu.
		JMenu helpMenu = new JMenu("Help");
		JMenuItem helpContentsMenuItem = new JMenuItem("Help Contents");	
		helpContentsMenuItem.addActionListener(new ActionListener() {	    
			public void actionPerformed(ActionEvent e) {
				new HelpGui();
			}
		});
		helpMenu.add(helpContentsMenuItem);
		
		// Create the Configurations menu.
		JMenu configMenu = new JMenu("Default Configurations");
		Collection<JMenuItem> configMenus = new LinkedList<JMenuItem>();
		configMenus.add(buildConfigMenuItem("Local Path", 
				"user.client.locPath"));
		configMenus.add(buildConfigMenuItem("Network Address", 
				"user.client.netPath"));
		configMenus.add(buildConfigMenuItem("Port Number", "user.client.port"));		
		for (JMenuItem item : configMenus) {
			configMenu.add(item);
		}
		
		// Create the menu bar add the menus and add it to the frame.
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(configMenu);
		menuBar.add(helpMenu);		
		this.setJMenuBar(menuBar);		
	}		
	
	/**
	 * Creates the window main screen which is composed of the areas,
	 * enumerated from top to bottom: a table (with a scroll pane), a search
	 * area and a action area (booking). 
	 */
	private void buildScreen() {

		// Create a main panel in order to make the entire window scrollable.
		JPanel mainPanel = new JPanel();	
		JScrollPane scrollPane = new JScrollPane(mainPanel);

		// Create frame layout using the most flexible layout.
		GridBagLayout layout = new GridBagLayout();
		mainPanel.setLayout(layout);
		GridBagConstraints constraints = null;	

		// Create the table scroll and layout constraints and add it to frame.
		JScrollPane tableScroll = createTablePane();
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;	
		constraints.fill = GridBagConstraints.BOTH;	
		constraints.insets = new Insets(20, 20, 10, 20);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.weighty = 1;
		mainPanel.add(tableScroll, constraints);

		// Create the search panel and layout constraints and add it to frame.
		JPanel searchPanel = createSearchPanel();	
		constraints = new GridBagConstraints();
		constraints.gridx = 0;	
		constraints.gridy = GridBagConstraints.RELATIVE;
		constraints.fill = GridBagConstraints.BOTH;	
		constraints.insets = new Insets(10, 20, 10, 20);;	
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.weighty = 1;	
		mainPanel.add(searchPanel, constraints);

		// Create the book panel and layout constraints and add it to frame.
		JPanel bookingPanel = createBookPanel(); 
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = GridBagConstraints.RELATIVE;	
		constraints.insets = new Insets(10, 20, 20, 20);;	
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.weighty = 1;
		mainPanel.add(bookingPanel, constraints);
		
		this.add(scrollPane);
	}

	/**
	 * Creates a scroll pane with the application table inside. The table is 
	 * initialized and set with the respective table model (the Model in the
	 * MVC pattern).
	 */
	private JScrollPane createTablePane() {

		// Create and initialize the application table.
		this.table = new JTable();
		this.table.setModel(new RecordTableModel());
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.setToolTipText("Select a record to book or return.");		
		
		// Create a scroll pane for this table.
		JScrollPane tableScroll = new JScrollPane(this.table);	
		return tableScroll;
	}

	/**
	 * Creates the search panel which is composed by filters in the form of
	 * labels and text fields. The panel also as two buttons one for invoking
	 * the actual search and other to clear the search filters and set the
	 * table with the default search.    
	 */
	private JPanel createSearchPanel() {

		// Creates the search panel and adds a titled border to it.
		JPanel panel = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder("Searching");
		panel.setBorder(border);

		// Create panel layout using the most flexible layout.
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints constraints = null;

		// Name label and field.
		JLabel nameLabel = new JLabel(COLUMN_NAMES[0]);
		JTextField nameField = new JTextField(20);
		nameLabel.setLabelFor(nameField);
		nameLabel.setDisplayedMnemonic('N');	
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;		
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(10, 10, 5, 2);
		panel.add(nameLabel, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;		
		constraints.insets = new Insets(10, 1, 5, 2);
		panel.add(nameField, constraints);			

		// Location label and field.	
		JLabel locationLabel = new JLabel(COLUMN_NAMES[1]);
		JTextField locationField = new JTextField(20);
		locationLabel.setLabelFor(locationField);
		locationLabel.setDisplayedMnemonic('L');	
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 0;		
		constraints.anchor = GridBagConstraints.EAST;	
		constraints.insets = new Insets(10, 5, 5, 2);
		panel.add(locationLabel, constraints);	
		constraints = new GridBagConstraints();
		constraints.gridx = 3;
		constraints.gridy = 0;		
		constraints.insets = new Insets(10, 2, 5, 5);
		panel.add(locationField, constraints);

		// Services label and field.	
		JLabel servicesLabel = new JLabel(COLUMN_NAMES[2]);
		JTextField servicesField = new JTextField(20);
		servicesLabel.setLabelFor(servicesField);
		servicesLabel.setDisplayedMnemonic('S');
		constraints = new GridBagConstraints();
		constraints.gridx = 4;
		constraints.gridy = 0;		
		constraints.anchor = GridBagConstraints.EAST;	
		constraints.insets = new Insets(10, 5, 5, 2);
		panel.add(servicesLabel, constraints);		
		constraints = new GridBagConstraints();
		constraints.gridx = 5;
		constraints.gridy = 0;		
		constraints.insets = new Insets(10, 2, 5, 5);
		panel.add(servicesField, constraints);	

		// Number of Workers label and field.		
		JLabel nbrWorkersLabel = new JLabel(COLUMN_NAMES[3]);
		JTextField nbrWorkersField = new JTextField(20);
		nbrWorkersLabel.setLabelFor(nbrWorkersField);
		nbrWorkersLabel.setDisplayedMnemonic('W');	
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;		
		constraints.anchor = GridBagConstraints.EAST;	
		constraints.insets = new Insets(2, 10, 10, 1);
		panel.add(nbrWorkersLabel, constraints);
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;		
		constraints.insets = new Insets(5, 2, 10, 5);
		panel.add(nbrWorkersField, constraints);	

		// Hourly Charge label and field.		
		JLabel chargeLabel = new JLabel(COLUMN_NAMES[4]);
		JTextField chargeField = new JTextField(20);
		chargeLabel.setLabelFor(chargeField);
		chargeLabel.setDisplayedMnemonic('H');
		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 1;		
		constraints.anchor = GridBagConstraints.EAST;	
		constraints.insets = new Insets(5, 5, 10, 2);
		panel.add(chargeLabel, constraints);	
		constraints = new GridBagConstraints();
		constraints.gridx = 3;
		constraints.gridy = 1;	
		constraints.insets = new Insets(5, 2, 10, 5);
		panel.add(chargeField, constraints);	

		// Customer ID label and field.		
		JLabel customerIdLabel = new JLabel(COLUMN_NAMES[5]);
		JTextField customerIdField = new JTextField(20);		
		customerIdLabel.setLabelFor(customerIdField);
		customerIdLabel.setDisplayedMnemonic('C');
		constraints = new GridBagConstraints();
		constraints.gridx = 4;
		constraints.gridy = 1;		
		constraints.anchor = GridBagConstraints.EAST;	
		constraints.insets = new Insets(5, 5, 10, 2);
		panel.add(customerIdLabel, constraints);		
		constraints = new GridBagConstraints();
		constraints.gridx = 5;
		constraints.gridy = 1;		
		constraints.insets = new Insets(5, 2, 10, 5);
		panel.add(customerIdField, constraints);	

		/*
		 * Sets the fields array with created text fields, used by the
		 * searching mechanism. 
		 */		
		this.fields = new JTextField[] {nameField, locationField, 
				servicesField, nbrWorkersField, chargeField, customerIdField};	

		// Create the search button
		JButton searchButton = new JButton("Search");
		searchButton.setMnemonic(' ');
		searchButton.addActionListener(new SearchListener());
		constraints = new GridBagConstraints();
		constraints.gridx = 6;
		constraints.gridy = 1;		
		constraints.insets = new Insets(5, 5, 10, 2);
		panel.add(searchButton, constraints);	

		// Create the clear button
		JButton clearButton = new JButton("Clear");
		clearButton.setMnemonic(' ');
		clearButton.addActionListener(new ClearListener());
		constraints = new GridBagConstraints();
		constraints.gridx = 7;
		constraints.gridy = 1;		
		constraints.insets = new Insets(5, 2, 10, 10);
		panel.add(clearButton, constraints);			

		return panel;
	}

	/**
	 * Creates the booking panel which consists of two buttons, one for
	 * booking and the other for returning.
	 */
	private JPanel createBookPanel() {

		// Creates the booking panel and adds a titled border to it.
		JPanel panel = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder("Booking");
		panel.setBorder(border);

		// Create panel layout using the most flexible layout.
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints constraints = null;	

		// Create panel for booking buttons.	
		JButton bookButton = new JButton("Book");
		bookButton.addActionListener(new BookListener());
		bookButton.setMnemonic('B');
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;		
		constraints.insets = new Insets(10, 10, 10, 2);
		panel.add(bookButton, constraints);		

		JButton returnButton = new JButton("Return");
		returnButton.addActionListener(new ReturnListener());
		returnButton.setMnemonic('R');	
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;		
		constraints.insets = new Insets(10, 2, 10, 10);
		panel.add(returnButton, constraints);	

		return panel;
	}    


	// Private classes that implement the various ActionListeners.
	
	
	/**
	 * This class handles all the booking events. That is, the actions 
	 * to perform when the book button is pressed. 
	 */
	private class BookListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {					
			int row = table.getSelectedRow();
			
			// Evaluate if a row has been selected by the user.
			if (row >= 0) {	
				String customerID = null;
				
				// Create a dialog where the user must insert the customer ID.				
				String mask = "########";		// 8 digit number.				
				MaskFormatter formatter = ConfigPanel.createFormatter(mask);
				JFormattedTextField field = new JFormattedTextField(formatter);
				field.setColumns(mask.length());
				field.setFocusLostBehavior(JFormattedTextField.COMMIT);				
				JLabel fieldLabel = new JLabel("Fill in the Customer ID:");								
				Object[] objField = {fieldLabel, field};				
				Object[] buttonTitles = new String[] {"Book","Cancel"};
				int answer = JOptionPane.showOptionDialog(ClientGui.this, 
						objField, "Customer Booking", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, 
						buttonTitles,
						buttonTitles[0]);		

				// Evaluate the user response.
				if (answer == JOptionPane.NO_OPTION || 
						answer == JOptionPane.CLOSED_OPTION) {    	 
					return;	    
				} else {
					customerID = field.getText().toString();
				}				

				// Change the customer ID in the record of the selected row.
				RecordTableModel tableModel = 
						(RecordTableModel) table.getModel();
				Record record = tableModel.getRecord(row);				 
				record.changeAvailability(customerID);

				try {
					controller.bookRecord(record);
				} catch (GuiException e) {
					showMessageDialog(ClientGui.this, e);	    
				} 

				// Invoke the search listener to update the table immediately.
				new SearchListener().actionPerformed(ae);
			} else {
				String message = "No row selected. Please select a " +
						"row before booking.";
				JOptionPane.showMessageDialog(ClientGui.this, message);
			}
		}
	}

	/**
	 * This class handles all the returning events. That is, the actions 
	 * to perform when the return button is pressed. 
	 */
	private class ReturnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			int row = table.getSelectedRow();
			
			// Evaluate if a row has been selected by the user.
			if (row >= 0) {						
				RecordTableModel tableModel = 
						(RecordTableModel) table.getModel();
				Record record = tableModel.getRecord(row);

				// Check if the record is already available.
				if (record.isAvailable()) {
					String message = "Record is already available.";
					JOptionPane.showMessageDialog(ClientGui.this, message);
					return;
				}
				
				// Otherwise set the customer ID with a blank field. 
				record.changeAvailability(new String("        "));				

				try {					
					controller.returnRecord(record);								
				} catch (GuiException e) {
					showMessageDialog(ClientGui.this, e);
				}
				
				// Invoke the search listener to update the table immediately.
				new SearchListener().actionPerformed(ae);						
			} else {
				String message = "No row selected. Please select a " +
						"row before returning.";
				JOptionPane.showMessageDialog(ClientGui.this, message);
			}
		}
	}

	/**
	 * This class handles all the search events. That is, the actions 
	 * to perform when the search button is pressed. 
	 */
	private class SearchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			String[] criteria = new String[fields.length];
			
			// Fill the criteria array with text in the text fields.
			for (int i = 0; i < fields.length; i++) {
				JTextField field = fields[i];
				criteria[i] = field.getText();
				if(criteria[i].isEmpty()) {
					criteria[i] = null;
				}        			
			}

			try {
				RecordTableModel newTableModel = controller.search(criteria);
				table.setModel(newTableModel);			
			} catch (GuiException e) {
				showMessageDialog(ClientGui.this, e);
			}
		}      
	}

	/**
	 * This class handles all the clear events. That is, the actions 
	 * to perform when the clear button is pressed. 
	 */
	private class ClearListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			
			// Array with null values as necessary for the search method.
			String[] criteria = new String[fields.length];

			// Clear the text in the text fields.
			for (JTextField field : fields) {
				field.setText("");
			}

			try {
				RecordTableModel newTableModel = controller.search(criteria);
				table.setModel(newTableModel);
			} catch (GuiException e) {
				showMessageDialog(ClientGui.this, e);
			}
		}      
	}
}
