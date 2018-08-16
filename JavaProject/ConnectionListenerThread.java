
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;


public class ConnectionListenerThread extends Thread
{
	ServerSocket serverSocket;
	int port = 19999;
	
	ServerMainLobby mainLobby;
	 //ArrayList<ClientThread> connectedClients ;
	
	 
	
	 
	int numberOfClients = 0;
			//new ArrayList<String>();
	
	public ConnectionListenerThread()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			
		}
		catch(Exception e){}
		
		//connectedClients = new ArrayList<ClientThread>();
		
		mainLobby = new ServerMainLobby();
		
		
	}
	
	public void run()
	 {
		 
		 while(true)
		 {
			 try
			 {
		 
		
		 ConnectedClient connectedClient;
		 
		
	     connectedClient = new ConnectedClient(serverSocket.accept(), "client" + ++numberOfClients);
	     ClientThread clientThread = new ClientThread(connectedClient);
	     clientThread.changeState(new ServerMainLobbyState(mainLobby, clientThread));
	     mainLobby.addClientToMainLobby(clientThread);
	     
	    
	     
	    clientThread.start();
		 
		 System.out.println("added " + connectedClient.clientID + " to list of clients" + " there are now " + mainLobby.getNumberOfMainLobbyClients());
		 
		 sleep(1000);
		 
		 
			 }
			 catch(Exception e){System.out.println("wtf");}
			 
			 
		 
		 }
	 }
	
	

}
