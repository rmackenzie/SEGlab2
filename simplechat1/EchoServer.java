// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.regex.*;
import ocsf.server.*;
import common.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
  
  /**
   * Indicates whether the server is closed
   */
  boolean isClosed;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI)  //added for E50b AP
  {
    super(port);
    this.serverUI = serverUI;
  }
  
  public EchoServer(int port)  
  {
    super(port);
  }
  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient //changed for E50 RM
    (Object msg, ConnectionToClient client)
  {

    System.out.println("Message received: " + msg + " from " + client);
    try {
        if(msg.equals("#quit")||msg.equals("#logoff")){
        	//clientDisconnected(client);
        } else {
            this.sendToAllClients(msg);
        }    
    } catch (Exception e){
    	System.out.println("Failed to disconnect the client");
    }
  }
  
  /**
   * Added for Quesion E50 AP
   * This method handles any messages received from the Console.
   *
   * @param msg The message received from the console.
   */
  public void handleMessageFromConsole //changed for E50 RM
    (String message){

	//look for commands
		Pattern command = Pattern.compile("^#.*?$");
		Matcher matcher = command.matcher(message);
		
		//commands
		Pattern stop = Pattern.compile("^#stop$");
		Matcher matcherStop = stop.matcher(message);
		Pattern close = Pattern.compile("^#close$");
		Matcher matcherClose = close.matcher(message);
		Pattern quit = Pattern.compile("^#quit$");
		Matcher matcherQuit = quit.matcher(message);
		Pattern setPort = Pattern.compile("^#setport (\\d*?)$");
		Matcher matcherSetPort = setPort.matcher(message);
		Pattern start = Pattern.compile("^#start$");
		Matcher matcherStart = start.matcher(message);
		Pattern getPort = Pattern.compile("^#getport$");
		Matcher matcherGetPort = getPort.matcher(message);
		if(matcher.find()){ //is a command
			
			//look for which commands
			if(matcherStop.find()){  //set host
				if(this.isListening()){
					this.stopListening();
				} 
				else {
					this.serverUI.display("Server already stopped!");
				}
				
			} else if(matcherClose.find()){ //log off
				try{
					this.close();
				} catch (IOException e){} //ignore exception as per close() method description
			} else if(matcherQuit.find()){ //quit
				try{
					this.close();
				} 
				catch (IOException e){}
				serverUI.display("Program is now exiting...Goodbye!");
				System.exit(0);
			} else if(matcherSetPort.find()){ //set port
				if(this.isClosed){
					this.setPort(Integer.parseInt(matcherSetPort.group(1)));
					serverUI.display("Port set to " + getPort());
				} else {
					this.serverUI.display
					("Cannot change port while server is open! Please close server first");
				}
			} else if(matcherStart.find()){ //start server
				if(!this.isListening()){
					try {
						this.listen();
						this.isClosed = false;
					} catch (IOException e) {
						serverUI.display("ERROR - Could not listen for clients!");
					}
					
				} else {
					serverUI.display("Server already listening for clients!");
				}
			} else if(matcherGetPort.find()) {
				serverUI.display("Port: " + Integer.toString(this.getPort()));
			} else {
				serverUI.display("Incorrect Command");
			}
			
		}
		else { //not a command, therefore must echo input to all clients
			this.sendToAllClients("SERVER MSG> " + message);
			serverUI.display("SERVER MSG> " + message);	  
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server closes
   */
  protected void serverClosed()
  {
	  this.isClosed = true;
	  System.out.println
      ("Server has now closed");
  }
  
//**** Changed for E49 RM
  /**
   * Overrides the abstract method
   * Outputs a message when client disconnects
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) { 
	  
	  System.out.println("A client has disconnected.");
  }
  
//**** Changed for E49 RM
  /**
   * Overrides the abstract method
   * Outputs a message when client disconnects
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) { 
	  System.out.println("A client has disconnected unexpectedly.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
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
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
