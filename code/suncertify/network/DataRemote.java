/*
 * DataRemote.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import suncertify.db.DB;
import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;


/**
 * This class represents the RMI implementation of the {@code DBRemote} 
 * interface. An object of this class contains a reference to the database. <p>
 * 
 * This class acts as an adapter or wrapper of the remote database.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see DBRemote
 * @see UnicastRemoteObject
 */
public class DataRemote extends UnicastRemoteObject implements DBRemote {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The database reference variable.
	 */
	private DB db; 

	/**
	 * Creates an instance of this class using the database in the location
	 * specified by {@code dbPath}.
	 * 
	 * @param dbPath the path in the remote machine to database physical file.
	 * @throws RemoteException if an a remote access to database could not be
	 * obtained.
	 */
	public DataRemote(String dbPath) throws RemoteException {
		try {
			this.db = new Data(dbPath);
		} catch (Exception e) {
			
			// Wrapps all exceptions into a RemoteException
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public String[] read(int recNo) throws RemoteException, 
			RecordNotFoundException {	
		return this.db.read(recNo);
	}

	@Override
	public void update(int recNo, String[] data, long lockCookie) 
			throws RemoteException, RecordNotFoundException, 
			SecurityException {
		this.db.update(recNo, data, lockCookie);
	}

	@Override
	public void delete(int recNo, long lockCookie) throws RemoteException,
			RecordNotFoundException, SecurityException {
		this.db.delete(recNo, lockCookie);
	}

	@Override
	public int[] find(String[] criteria) throws RemoteException {
		return this.db.find(criteria);
	}

	@Override
	public int create(String[] data) throws RemoteException, 
			DuplicateKeyException {
		return this.db.create(data);
	}
	
	@Override
	public long lock(int recNo) throws RemoteException, 
			RecordNotFoundException {
		return this.db.lock(recNo);
	}

	@Override
	public void unlock(int recNo, long cookie) 
			throws RemoteException, RecordNotFoundException, 
			SecurityException {
		this.db.unlock(recNo, cookie);
	}    
}
