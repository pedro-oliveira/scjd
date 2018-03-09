/*
 * RegisterDatabase.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Creates an RMI registration on the client machine for the remote database
 * object using the RMI naming service.
 * 
 * @author Pedro Oliveira
 * @version 1.1
 * @see Registry
 */
public class RegisterDatabase {
	
    /**
     * This class is a utility class so no constructor should ever be
     * invoked. Creating the default constructor private solves this issue. 
     */
    private RegisterDatabase() { }

    /**
     * This method creates a RMI registry in the local host for connecting
     * with remote objects in the specified location and port number. 
     *
     * @param dbPath the location of the database file on disk.
     * @param port the port the RMI Registry will listen on.
     * @throws RemoteException if network errors occurr.
     */
    public static void register(String dbPath, int port)
            throws RemoteException {
    	
    	// Create a registry of the RMI connection in the specified port. 
        Registry r = LocateRegistry.createRegistry(port);

        // Set the service name in the RMI registry created.
        r.rebind("Service", new NetworkClient(dbPath));
    }
}
