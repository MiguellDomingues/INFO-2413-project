import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;




public class MessageListenerThread extends Thread
{
	
   Socket socketClient;
   MainLobbyGUI clientGUI ;
   boolean connected;
   ConnectedServer connectedServer;
   String username;
   ClientState currentClientState;
   
   public MessageListenerThread(Socket socketClient,  MainLobbyGUI clientGUI, ConnectedServer connectedServer, String username)
   {
	   connected = true;
	   this.socketClient = socketClient;
	   this.clientGUI = clientGUI;
	   this.connectedServer = connectedServer;
	   this.username = username;
	   currentClientState = new ClientMainLobbyState(clientGUI, this);
	   
   }
   
   public void run()
   {
	   clientGUI.writeToTextArea("opening a new listener thread");
	   
	   try 
	   {	
		   connectedServer.WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.LOGON,username));
		   sleep(500);
			    
		   ServerToClientMessage message;
			    
		   do
		   {
			   message = connectedServer.ReadFromServer();
			   currentClientState.readMessage(message);
					
		   }
		   while(message != null && connected);
			    
	   } 
	   catch (IOException | InterruptedException | ClassNotFoundException e) 
	   {
		   clientGUI.writeToTextArea("error in listener theread: "+e.getMessage());
	   }
	   finally
	   {
		   try 
		   {
			   clientGUI.setDisconnectedGUIState();
			   close();
		   } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   }
		   
	
	   
	   clientGUI.writeToTextArea("closing thread");
	   clientGUI.setTitle("User Client");
	   
	   
	   
	   
	  
	  
   }
   
   public void close() throws IOException 
   {
	   connected = false;
	   connectedServer.closeServerConnection();
   }
   
   public ConnectedServer getServerConnection()
   {
	   return connectedServer;
   }
   
  
   
  public void changeState(ClientState newClientState)
  {
	  currentClientState = newClientState;
  }
  
  public String getUserName()
  {
	  return username;
  }
  
  public MainLobbyGUI getMainLobbyGUI()
  {
	  return clientGUI;
  }

   
}
