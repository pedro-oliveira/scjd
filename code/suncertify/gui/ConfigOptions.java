/*
 * ConfigOptions.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;


/**
 * This class represents the user configuration options that are filled in
 * the {@code ConfigPanel} of the user interface. 
 * 
 * @author Pedro Oliveira
 * @version 1.1
 * @see ApplicationMode
 */
public class ConfigOptions {

	/**
	 * User definied application mode. 
	 */
	private ApplicationMode mode;

	/**
	 * The database location path.
	 */
	private String dbPath;

	/**
	 * The server port number.
	 */
	private String port;	

	/**
	 * Constructor for the standalone application, creates an options object
	 * only using the application mode and database location.
	 * 
	 * @param mode the mode the application was invoked, the valid options are
	 * <tt>STANDALONE</tt>, <tt>NETWORK</tt> or <tt>SERVER</tt>.  
	 * @param dbPath the path to the database file.
	 */
	public ConfigOptions(ApplicationMode mode, String dbPath) {
		this.mode = mode;
		this.dbPath = dbPath;
		this.port = "";
	}

	/**
	 * Constructor for the networking mode, where the options are built
	 * using the location of the database and server in the network and 
	 * the port number where the server is listening.
	 * 
	 * @param mode the mode the application was invoked, the valid options are
	 * <tt>STANDALONE</tt>, <tt>NETWORK</tt> or <tt>SERVER</tt>. 
	 * @param dbPath the <tt>URL</tt> or <tt>ip</tt> address to machine
	 * where the server is running..
	 * @param port the port number of where the server listens.
	 */
	public ConfigOptions(ApplicationMode mode, String dbPath, String port) {
		this.mode = mode;
		this.dbPath = dbPath;
		this.port = port;
	}

	/**
	 * Returns the mode the application was invoked.
	 * 
	 * @return the mode the application was invoked, the valid options are
	 * <tt>STANDALONE</tt>, <tt>NETWORK</tt> or <tt>SERVER</tt>.
	 */
	public ApplicationMode getApplicationMode() {
		return this.mode;
	}

	/**
	 * Returns the database path which may be a file location in the local
	 * machine or a url, ip address or hostname depending on the application
	 * mode.  
	 * 
	 * @return the dbPath to the database.
	 */
	public String getDbPath() {
		return this.dbPath;
	}

	/**
	 * Returns the port number the server is listening. A blank field
	 * means the application was invoked in a standalone mode.
	 * 
	 * @return the port number where the server listens.
	 */
	public String getPort() {
		return this.port;
	}

	/**
	 * Sets the location of the database to the given path.
	 * 
	 * @param dbPath the location of the database.
	 */
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	/**
	 * Sets the port number with the given port value.
	 * 
	 * @param port the port number where the server should listen.
	 */
	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return this.mode + "\t" + this.dbPath + "\t" + this.port;
	}

}
