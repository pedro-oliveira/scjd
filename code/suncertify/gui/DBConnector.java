/*
 * DBConnector.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;

import java.io.*;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.db.InvalidSchemaException;
import suncertify.db.UnknownDBException;
import suncertify.network.DBRemote;
import suncertify.network.NetworkConnector;
import suncertify.network.RemoteAdapter;


/**
 * The {@code DBConnector} is used in cases where the GUI client wants to make a
 * connection to the data file. In this case, that connection is an direct
 * connection.
 *
 * @author Pedro Oliveira
 * @version 1.2
 * @see ConfigOptions
 * @see DB
 * @see UnknownDBException
 * @see InvalidSchemaException
 * @see IOException
 */
public class DBConnector {
	
    /**
     * Since this is a utility class (it only exists for other classes to call
     * it's static methods), lets stop users creating unneeded instances of
     * this class by creating a private constructor.
     */
    private DBConnector() { }

    /**
     * This method is the static factory which retrieves the database 
     * connection handler.
     *
     * @throws UnknownDBException If the database id does not match the one 
     * expected.
     * @throws InvalidSchemaException If the database schema is not consistent.
     * @throws IOException Thrown if there is any problem accessing the 
     * database information.
     */
    public static DB getConnection(ConfigOptions options) 
    		throws UnknownDBException, InvalidSchemaException, IOException {
    	DB connection = null;
    	ApplicationMode mode = options.getApplicationMode();
    	
    	// Get database connection depending on the application mode.
    	switch (mode) {
    	case STANDALONE : 
    		connection = new Data(options.getDbPath());
    		break;
    	case NETWORK :
    		DBRemote dbRemote = NetworkConnector.getRemote(options);   
    		connection = new RemoteAdapter(dbRemote);
    		break;
    	}
    	return connection;
    }
}
