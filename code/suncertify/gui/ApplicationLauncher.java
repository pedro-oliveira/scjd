/*
 * ApplicationLauncher.java		
 * 
 * Copyright (c) 2011, Pedro Oliveira. All Rights Reserved.
 */

package suncertify.gui;


/**
 * This class is an implementation of the Façade pattern, hidding the various
 * applications modes behind this {@code ApplicationLauncher} class. <p>
 * 
 * This class will validate the command line arguments and invoke the class to
 * start the application depending on the mode selected in the command line.
 * 
 * @author Pedro Oliveira
 * @version 1.2
 * @see ClientGui
 * @see ServerGui
 * @see ApplicationMode
 */
public class ApplicationLauncher {

	/**
	 * This Constructor validates the input line arguments, and if they are in
	 * order launches the a GUI for the selected mode. <p>
	 * 
	 * The valid command line arguments are "alone", "server" or none. The
	 * case is ignored to facilitate life to users.
	 * 
	 * @param args the command line arguments with the application mode to run.
	 */
	public ApplicationLauncher(String[] args) {

		try {
			if (args.length == 0) {
				new ClientGui(ApplicationMode.NETWORK);
			} else if (args[0].equalsIgnoreCase("alone")) {    
				new ClientGui(ApplicationMode.STANDALONE);
			} else if (args[0].equalsIgnoreCase("server")) {
				new ServerGui();
			} else {
	
				/*
				 * Alert user to with error message, and show how to fill the 
				 * command line argument.
				 */
				System.err.println("Incorrect command line arguments. " 
						+ "Expecting:");
				System.err.println("\"server\" - starts server application");
				System.err.println("\"alone\"  - starts non-networked client");
				System.err.println("\"\"       - (none): networked client will" 
						+ " start");
			}	
		} catch (ExceptionInInitializerError e) {
			System.err.println("Installation was not terminated. Reason: " 
					+ e.getMessage());
		}

	}

	/**
	 * Method that launches the application.
	 * 
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		new ApplicationLauncher(args);	
	}
}
