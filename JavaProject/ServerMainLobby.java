import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ServerMainLobby 
{
	
	public final int MAX_PLAYERS = 4;
	public final int MIN_PLAYERS = 2;
	public final int MAX_GAME_NAME = 15;
	
	 List<ClientThread> clientsInMainLobby ;
	 //ArrayList<ClientThread> clientsInMainLobby ;
	 
	 
	 
	 List<ServerHostedGame> hostedGames ;
	 //ArrayList<ServerHostedGame> hostedGames ;
	 
	 List<ServerRunningGame > runningGames ;
	 
	 public ServerMainLobby()
	 {
		 clientsInMainLobby = Collections.synchronizedList(new ArrayList<ClientThread>());
		 hostedGames = Collections.synchronizedList(new ArrayList<ServerHostedGame>());
		 runningGames = Collections.synchronizedList(new ArrayList<ServerRunningGame>());
		 
		 
		 
	 }
	 
	 
	 
	 public synchronized void addClientToMainLobby(ClientThread clientThread)
	 {
		 clientThread.changeState(new ServerMainLobbyState(this, clientThread));
		 clientsInMainLobby.add(clientThread);
	 }
	 
	 //this object should handall the writes on the main lobby state threads in the arraylist
	 public synchronized ServerToClientMessage writeToConnectedClients(ServerToClientMessage message)
	 {
		 for(int i = 0; i < clientsInMainLobby.size(); i++)
		  {
			 try
			 {
				 clientsInMainLobby.get(i).getClient().WriteToClient(message);
			 }
			 catch(IOException e){System.out.println("error writing to all clients");}
		  }
		 
		 return message;
	 }
	 
	 public synchronized void removeClientFromMainLobby(ClientThread clientThread)
	 {
		 clientsInMainLobby.remove(clientThread);
	 }
	 
		
		 
	 
	 public synchronized boolean isDuplicateUserName(String username)
	 {
		 for(int i = 0; i < clientsInMainLobby.size(); i++)
		 {
			if(username.equals(clientsInMainLobby.get(i).getClient().getClientName()))
				return true;
		 }
		 
		 return false;
	 }
	 
	 public synchronized String getNamesOfMainLobbyClients()
	 {
		 String connectedUsers = "";
		 
		 for(int i = 0; i < clientsInMainLobby.size(); i++)
			 connectedUsers = connectedUsers + clientsInMainLobby.get(i).getClient().getClientName() + "\n";
		 
		 return connectedUsers;
				
	  }
	 
	 public int getNumberOfMainLobbyClients()
	 {
		 return clientsInMainLobby.size();
	 }
	 
	 public synchronized void addHostedGame(ServerHostedGame serverHostedGame)
	 {
		 hostedGames.add(serverHostedGame);
	 }
	 
	 public synchronized void removeHostedGame(ServerHostedGame serverHostedGame)
	 {
		 hostedGames.remove(serverHostedGame);
	 }
	 
	 public synchronized void addRunningGame(ServerRunningGame serverRunningGame)
	 {
		 runningGames.add(serverRunningGame);
	 }
	 
	 public synchronized void removeRunningGame(ServerRunningGame serverRunningGame)
	 {
		 runningGames.remove(serverRunningGame);
	 }
	 
	 public int getNumberofRunningGames()
	 {
		 return runningGames.size();
	 }
	 
	 public synchronized String getNamesOfRunningGames()
	 {
		 String namesOfRunningGames = "";
		 
		 for(int i = 0; i < runningGames.size(); i++)
		 {
			 namesOfRunningGames = namesOfRunningGames + runningGames.get(i).getGameName() + " ";
			 namesOfRunningGames = namesOfRunningGames + "[" + runningGames.get(i).getNumberofPlayers() + "]";
			 namesOfRunningGames = namesOfRunningGames + "[" + runningGames.get(i).getNumberOfSpectators() + "]";
			 
			 if(runningGames.get(i).getNumberofPlayers() <= 1)
				 namesOfRunningGames = namesOfRunningGames + "[GAMEOVER]";
			 
			 namesOfRunningGames = namesOfRunningGames + "\n";
		 }
		 
		 return namesOfRunningGames;
		 
	 }
	 
	 
	 
	 public synchronized String getNamesOfHostedGames()
	 {
		 String namesOfHostedGames = "";
		 
		 for(int i = 0; i < hostedGames.size(); i++)
		 {
			 namesOfHostedGames = namesOfHostedGames + hostedGames.get(i).getGameName();
			 
			 if(hostedGames.get(i).isFull())
				 namesOfHostedGames = namesOfHostedGames + "[FULL]";
			 else
				 namesOfHostedGames = namesOfHostedGames + "[" + hostedGames.get(i).numOfClientsInThisHostedLobby() + "]";
				 
				 namesOfHostedGames = namesOfHostedGames + "\n";
		 }
		 
		 return namesOfHostedGames;
		 
	 }
	 
	 public synchronized ServerHostedGame findHostedGameByName(String gameName)
	 {
		 for(int i = 0; i < hostedGames.size(); i++)
		 {
			 if(hostedGames.get(i).getGameName().equals(gameName))
			 {
				 return hostedGames.get(i);
			 }
		 }
		 
		 return null;
	 }
	 
	
	 
	 public synchronized ServerRunningGame findRunningGameByName(String runningGameName)
	 {
		 for(int i = 0; i < runningGames.size(); i++)
		 {
			 if(runningGames.get(i).getGameName().equals(runningGameName))
			 {
				 return runningGames.get(i);
			 }
		 }
		 
		 return null;
	 }
	 
	 public synchronized boolean isDuplicateHostedGameName(String gameName)
	 {
		 for(int i = 0; i < hostedGames.size(); i++)
		 {
			if(gameName.equals(hostedGames.get(i).getGameName()))
				return true;
		 }
		 
		 return false;
	 }
	
}
