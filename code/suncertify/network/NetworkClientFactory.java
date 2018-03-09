/*
 * NetworkClientFactory.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * This class represents the client network factory which has 
 * the methods that can be called remotely.
 * 
 * @author Pedro Oliveira
 * @version 1.0
 * @see Remote
 * @see DBRemote
 */
public interface NetworkClientFactory extends Remote {

    /**
     * Returns the reference to a remote instance of the remote database
     * connection class which has all the methods that can be called
     * remotely.
     * 
     * @return the remote database connection class.
     * @throws RemoteException for remote generated exceptions.
     */
    public DBRemote getClient() throws RemoteException;
    
}
