/*
 * NetworkConnector.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.network;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.gui.ConfigOptions;


/**
 * This class is static factory and is used by the client to get a connection
 * to the remote database. This connection is made using a RMI network 
 * connection.
 * 
 * @author Pedro Oliveira
 * @version 1.3
 * @see DBRemote
 */
public class NetworkConnector {

	/**
	 * This class is a static factory so no instances of the class should be
	 * generated using a default constructor. Creating this constructor with
	 * no functionallity and making it private solves the problem.
	 */
	private NetworkConnector() { };

	/**
	 * This is the static factory method through each one can get access
	 * to the remote database object. <p>
	 * 
	 * This method creates the RMI connection using the values specified
	 * in the {@code options} parameter.
	 * 
	 * @param options the configuration options necessary to set the RMI
	 * connection.
	 * @return an instance of {@code DBRemote}.
	 * @throws RemoteException if a connection to the remote database
	 * was not possible.
	 */
	public static DBRemote getRemote(ConfigOptions options)
			throws RemoteException {

		// Create url to the remote server using the specified options.
		NetworkClientFactory factory = null;
		String path = options.getDbPath(); 
		String port = options.getPort();
		String url = "rmi://" + path + ":" + port + "/Service";

		// All exceptions are wrapped arround a RemoteException
		try {
			factory = (NetworkClientFactory) Naming.lookup(url);
			return factory.getClient();
		} catch (NotBoundException e) {
			String msg = "Service not registered: " + e.getMessage();
			throw new RemoteException(msg, e);
		} catch (MalformedURLException e) {
			String msg = path + " not valid: " + e.getMessage();
			throw new RemoteException(msg, e);
		} catch(Exception e) {
			String msg = "Cannot connect to " + path + ". Reason: " 
					+ e.getMessage();
			throw new RemoteException(msg, e);
		}
	}
}
