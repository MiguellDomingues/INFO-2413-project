import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ServerHostedGame 
{
	String gameName;
	
	
	//will contain code for writing to all clients in this lobby
	//position 0 in the arraylist IS THE HOST
	private final int HOST = 0;
	//ArrayList<ClientThread> clientsInThisHostedGame ;
	List<ClientThread> clientsInThisHostedGame ;
	ServerMainLobby serverMainLobby;
	

	
	int numMaxPlayers;
	int startingMoney;
	
	public ServerHostedGame(ServerMainLobby serverMainLobby, String gameName, int numMaxPlayers, int startingMoney)
	{
		clientsInThisHostedGame = Collections.synchronizedList(new ArrayList<ClientThread>());
		
		this.serverMainLobby = serverMainLobby;
		this.gameName = gameName;
		this.numMaxPlayers = numMaxPlayers;
		this.startingMoney = startingMoney;
	}
	
	//this object should handall the writes on the hosted gane lobby threads in the arraylist
	public String getGameName()
	{
		return gameName;
	}
	
	//remove a client from the hosted game and send his thread back to the main lobby list
	public synchronized void returnThreadToMainLobby(ClientThread clientThread)
	{
		//clientsInThisHostedGame.remove(clientThread);
		removeClientFromHostedGame(clientThread);
		serverMainLobby.addClientToMainLobby(clientThread);
		
		
		//updateNonFullGameName();
		
		if(clientsInThisHostedGame.isEmpty())
			serverMainLobby.removeHostedGame(this);
	}
	
	 public synchronized ServerToClientMessage writeToClientsInHostedGame(ServerToClientMessage message)
	 {
		 for(int i = 0; i < clientsInThisHostedGame.size(); i++)
		  {
			 try
			 {
				 clientsInThisHostedGame.get(i).getClient().WriteToClient(message);
			 }
			 catch(IOException e){System.out.println("error writing to all clients in a hosted lobby");}
		  }
		 
		 return message;
	 }
	 
	
	 
	 public synchronized String getNamesOfHostedLobbyClients()
	 {
		 String connectedUsers = "";
		 
		 //first append the host clients name with [HOST]
		 connectedUsers = connectedUsers + clientsInThisHostedGame.get(HOST).getClient().getClientName() + "[HOST]" + "\n";
		 
		 //..then get the rest of the clients
		 for(int i = 1; i < clientsInThisHostedGame.size(); i++)
			 connectedUsers = connectedUsers + clientsInThisHostedGame.get(i).getClient().getClientName() + "\n";
		 
		 return connectedUsers;
				
	  }
	 
	 public synchronized void addClientToHostedGame(ClientThread clientThread)
	 {
		 clientsInThisHostedGame.add(clientThread);
		 clientThread.changeState(new ServerHostedGameLobbyState(serverMainLobby, this, clientThread));
			 //clientThread.setHostedLobby(this);
	 }
	 
	 public synchronized void removeClientFromHostedGame(ClientThread clientThread)
	 {
		 if(clientsInThisHostedGame.remove(clientThread));
		 	//clientThread.setHostedLobby(null);
		 
		 if(clientsInThisHostedGame.isEmpty())
		 {
				serverMainLobby.removeHostedGame(this);
		 }
	 }
	 
	 
	 public synchronized boolean isFull()
	 {
		 return clientsInThisHostedGame.size() == numMaxPlayers;
	 }
	 
	 public int numOfClientsInThisHostedLobby()
	 {
		 return clientsInThisHostedGame.size();
	 }
	 
	 public String getHostName()
	 {
		 return clientsInThisHostedGame.get(HOST).getClient().getClientName();
	 }
	 
	 public boolean isHost(ClientThread clientThread)
	 {
		 return clientThread.equals(clientsInThisHostedGame.get(HOST));
	 }
	 
	 public List<ClientThread> getClientsInHostedGame()
	 {
		 return clientsInThisHostedGame;
	 }
	 
	 public ClientThread getHostClient()
	 {
		 return clientsInThisHostedGame.get(HOST);
	 }
	 
	 public int getStartingMoney()
	 {
		 return startingMoney;
	 }
	 
	 
}
