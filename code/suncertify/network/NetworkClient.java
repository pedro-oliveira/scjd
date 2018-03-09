/*
 * NetworkClient.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * This class is the implementation of the client network factory which returns
 * clients with connectivity to the database.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see NetworkClientFactory
 * @see UnicastRemoteObject
 */
public class NetworkClient extends UnicastRemoteObject implements NetworkClientFactory {  

	/**
	 * Default serial version UID. 
	 */
    private static final long serialVersionUID = 1L;

	/**
	 * Location in the remote machine of the database file. 
	 */
	private String dbPath;


	/**
	 * Creates an instance of this factory with the specified database file
	 * path.
	 * 
	 * @param dbPath the path in the remote machine to database physical file. 
	 * @throws RemoteException if a remote instance of the database can not be
	 * created. 
	 */
	public NetworkClient(String dbPath) throws RemoteException {
		this.dbPath = dbPath;
	}

	@Override
	public DBRemote getClient() throws RemoteException {
		return new DataRemote(this.dbPath);
	}

}
