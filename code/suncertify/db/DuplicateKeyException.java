/*
 * DuplicateKeyException.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.db;


/**
 * Thrown when inserting a new element into the database and the input data
 * violates the database's primary key.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see Exception
 */
public class DuplicateKeyException extends Exception {
	
	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code DuplicateKeyException} with no detail message.
	 */
	public DuplicateKeyException() {
		super();
	}

	/**
	 * Constructs a {@code DuplicateKeyException} with the specified
	 * detail message.
	 * 
	 * @param msg the detail message.
	 */
	public DuplicateKeyException(String msg) {
		super(msg);
	}	
}
