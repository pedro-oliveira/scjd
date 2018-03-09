/*
 * RemoteAdapter.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.rmi.RemoteException;

import suncertify.db.DB;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;


/**
 * This class is the implementation of the adapter pattern which is used
 * to provied the implementation of the two interfaces {@code DB} and 
 * {@code} that could not be implemented in the same class, due to 
 * the restrictions of subclassing with more checked exceptions of its
 * interfaces. 
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see DB
 * @see DBRemote
 */
public class RemoteAdapter implements DB {
	
	/**
	 * The reference to remote database object.
	 */
	private DBRemote dbRemote;
	
	/**
	 * Creates an instance of the remote database adapter, which
	 * wrapps the remote database object.
	 * 
	 * @param dbRemote
	 */
	public RemoteAdapter(DBRemote dbRemote) {
		this.dbRemote = dbRemote;
	}

	@Override
	public String[] read(int recNo) throws RecordNotFoundException {
		try {
			return this.dbRemote.read(recNo);
		} catch (RemoteException e) {
			throw new RecordNotFoundException();
		}
	}

	@Override
	public void update(int recNo, String[] data, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			this.dbRemote.update(recNo, data, lockCookie);
		} catch (RemoteException e) {
			throw new RecordNotFoundException();
		}
	}

	@Override
	public void delete(int recNo, long lockCookie)
			throws RecordNotFoundException, SecurityException {
		try {
			this.dbRemote.delete(recNo, lockCookie);
		} catch (RemoteException e) {
			throw new RecordNotFoundException();
		}
	}

	@Override
	public int[] find(String[] criteria) {
		try {
			return this.dbRemote.find(criteria);
		} catch (RemoteException e) {
			return new int[]{};
		}
	}

	@Override
	public int create(String[] data) throws DuplicateKeyException {
		try {
			return this.dbRemote.create(data);
		} catch (RemoteException e) {
			throw new DuplicateKeyException();
		}
	}

	@Override
	public long lock(int recNo) throws RecordNotFoundException {
		try {
			return this.dbRemote.lock(recNo);
		} catch (RemoteException e) {
			throw new RecordNotFoundException();
		}
	}

	@Override
	public void unlock(int recNo, long cookie) throws RecordNotFoundException,
			SecurityException {
		try {
			this.dbRemote.unlock(recNo, cookie);
		} catch (RemoteException e) {
			throw new RecordNotFoundException();
		}
	}
}
