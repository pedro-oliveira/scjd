/*
 * GuiException.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;


/**
 * 
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see Exception
 */
public class GuiException extends Exception {

	/**
	 * Default serial version UID. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The title used in the error message dialogs.
	 */
	private String messageTitle;

	/**
	 * The message text used in the error message dialogs.
	 */
	private String message;

	/**
	 * The message type used in the error message dialogs.
	 */
	private int messageType;

	/**
	 * Creates a {@code GuiException} with the information necessary to
	 * create an error message dialog.
	 * 
	 * @param messageTitle the title used in the error message dialogs.
	 * @param message the title used in the error message dialogs.
	 * @param messageType the message text used in the error message dialogs.
	 */
	public GuiException(String messageTitle, String message, int messageType) {
		super();
		this.messageTitle = messageTitle;
		this.message = message;
		this.messageType = messageType;
	}

	/**
	 * Returns the title message string of this exception.
	 * 
	 * @return the title message string of this exception.
	 */
	public String getMessageTitle() {
		return this.messageTitle;
	}

	/**
	 * Returns the message type of this exception.
	 * 
	 * @return the message type of this exception.
	 */
	public int getMessageType() {
		return this.messageType;
	}       
	
	@Override
	public String getMessage() {
		return this.message;
	}	
}
