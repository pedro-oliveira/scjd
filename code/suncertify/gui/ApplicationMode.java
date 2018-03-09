/*
 * ApplicationMode.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;


/**
 * This <tt>enum</tt> specifies all possible applications modes.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 */
public enum ApplicationMode {

    /**
     * Direct connection for standalone application.
     */
    STANDALONE,

    /**
     * Connection using network to interact with database. 
     */
    NETWORK,

    /**
     * Server application. 
     */
    SERVER;	
}
