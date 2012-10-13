import java.io.*;

import client.ChatClient;
import common.*;


/**
 * Created for Question E50b AP
 * This class constructs the UI for a server client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ClientConsole 
 *
 * @author Adnan Patel
 * @version Oct 2012
 */


public class ServerConsole implements ChatIF {

	//Class variables *************************************************

	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;

	//Instance variables **********************************************

	/**
	 * The instance of the server that created this ConsoleChat.
	 */
	EchoServer server;
	
	public ServerConsole(int port) 
	{
		server = new EchoServer(port, this);

	}
	
	/**
	 * Method that when overriden is used to display objects onto
	 * a UI.
	 */
	public void display(String message){
		System.out.println(message);
	}
	
	public void startServer(){
		try 
		{
			server.listen(); //Start listening for connections
			server.isClosed = false;
		} 
		catch (Exception ex) 
		{
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
	
	public void handleMessageFromConsole(){
		try
	    {
	      BufferedReader fromConsole = 
	        new BufferedReader(new InputStreamReader(System.in));
	      String message;

	      while (true)  // **** Changed for E49 RM
	      {
	        message = fromConsole.readLine();
	        server.handleMessageFromConsole(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	}

	/**
	 * This method is responsible for the creation of 
	 * the server instance (With UI).
	 *
	 * @param args[0] The port number to listen on.  Defaults to 5555 
	 *          if no argument is entered.
	 */
	public static void main(String[] args) 
	{
		int port = 0; //Port to listen on

		try
		{
			port = Integer.parseInt(args[0]); //Get port from command line
		}
		catch(Throwable t)
		{
			port = DEFAULT_PORT; //Set port to 5555
		}

		ServerConsole sc = new ServerConsole(port);

		sc.startServer(); //Start echo server
		sc.handleMessageFromConsole();

	}
}
