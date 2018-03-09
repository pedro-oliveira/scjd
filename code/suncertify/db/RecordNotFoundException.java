/*
 * RecordNotFoundException.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;


/**
 * Thrown when a search is performed and the specified record does not exist
 * or was marked as deleted in the database. 
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see Exception
 */
public class RecordNotFoundException extends Exception {
	
	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code RecordNotFoundException} with no detail message.
	 */
	public RecordNotFoundException() {
		super();
	}
	
	/**
	 * Constructs a {@code RecordNotFoundException} with the specified
	 * detail message.
	 * 
	 * @param msg the detail message.
	 */
	public RecordNotFoundException(String msg) {
		super(msg);
	}
}
