/*
 * ServerStarter.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import suncertify.gui.ConfigOptions;
import suncertify.gui.GuiException;


/**
 * The objects from this class start the server which accepts network 
 * connections via RMI.
 * 
 * @author Pedro Oliveira
 * @version 1.4
 * @see ConfigOptions
 * @see JLabel
 * @see GuiException
 */
public class ServerStarter {

	/**
	 * {@code String} to set the status label in the server Gui when the 
	 * connection is being started.
	 */
	private static final String STARTING_LABEL = "Starting ...";

	/**
	 * {@code String} to set the status label in the server Gui when the 
	 * connection as been achieved and the server is running.
	 */
	private static final String RUNNING_LABEL = "Server Running.";
	
	/**
	 * {@code String} to set the status label in the server Gui when the
	 * connection as failed and the user needs to re enter parameters.
	 */
	private static final String ERROR_LABEL = "Configuration error. Please " +
			"re enter the configuration parameters.";

	/**
	 * {@code String} with the title of the gui error message. 
	 */
	private static final String MSG_TITLE_ERR = "Could not start server";
	
	/**
	 * {@code String} with the message of invalid port.
	 */
	private static final String MSG_PORT_ERR = "Invalid port number! " +
			"Choose a value between 1025 and 65536.";
	
	/**
	 * {@code String} with the message of invalid RMI registration.
	 */
	private static final String MSG_REGISTER_ERR = "Invalid path and/or" +
			" port number.";	
	
	/**
	 * {@code String} with the message of invalid database location.
	 */
	private static final String MSG_PATH_ERR = "Invalid database path!";
	
	/**
	 * The object with the necessary configuration options to set start
	 * the server.
	 */
	private ConfigOptions options;

	/**
	 * Creates an instance of the {@code ServerStarter} class using the
	 * configuration options specified as input parameter.
	 * 
	 * @param options the configuration options to start the server.
	 */
	public ServerStarter(ConfigOptions options) {
		this.options = options;
	}

	/**
	 * This method starts the server that accepts RMI connections and
	 * updates the {@code statusLabel} during its process.
	 * 
	 * @param statusLabel this reference is changed during the method
	 * execution to give feedback to user about the server status.
	 * @throws GuiException thrown if any exception was raised trying to
	 * start the server.
	 */
	public void start(JLabel statusLabel) throws GuiException {
		
		statusLabel.setText(STARTING_LABEL);
		
		try {
			String dbPath = this.options.getDbPath();
			int port = Integer.parseInt(this.options.getPort());
			if (port < 1025 || port >  65536) {
				// NFE to avoid another catch clause.
				throw new NumberFormatException();	
			}
			
			/*
			 * In the client side it was already assumed the database
			 * is a file. So one can verify that the file exists before
			 * performing the database server registration.
			 */
			File file = new File(dbPath);
			if (!file.isFile()) {
				throw new IOException();
			}
			
			// If no exceptions were thrown. the database is registered.
			RegisterDatabase.register(dbPath, port);
		} catch (NumberFormatException e) {
			statusLabel.setText(ERROR_LABEL);
			throw new GuiException(MSG_TITLE_ERR, MSG_PORT_ERR, 
					JOptionPane.ERROR_MESSAGE);
		} catch (RemoteException e) {
			statusLabel.setText(ERROR_LABEL);
			throw new GuiException(MSG_TITLE_ERR, MSG_REGISTER_ERR, 
					JOptionPane.ERROR_MESSAGE);			
		} catch (IOException e) {
			statusLabel.setText(ERROR_LABEL);
			throw new GuiException(MSG_TITLE_ERR, MSG_PATH_ERR, 
					JOptionPane.ERROR_MESSAGE);				
		}
		
		// In this moment the server is running.
		statusLabel.setText(RUNNING_LABEL);
	}

}
