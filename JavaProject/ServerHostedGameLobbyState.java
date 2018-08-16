import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ServerHostedGameLobbyState extends ServerState 
{
	
	ServerMainLobby mainLobby;
	ClientThread clientThread;
	//ConnectedClient connectedClient;
	ServerHostedGame thisThreadsHostedLobby;
	
	
	// contains a list of client threads that can be updated as users join the game
	//is the default state when client first connects
	public ServerHostedGameLobbyState(ServerMainLobby mainLobby, ServerHostedGame thisThreadsHostedLobby, ClientThread clientThread)
	{
		this.thisThreadsHostedLobby = thisThreadsHostedLobby;
		this.mainLobby = mainLobby;
		this.clientThread = clientThread; 
		//this.connectedClient = connectedClient;
	}
	
	public void readMessage(ClientToServerMessage message) throws IOException, ClassNotFoundException
	{
		//System.out.println("state change success");
		//add the switch for reading client messages from the host lobby. have the code for writing to those clients, updating the current users list, starting game etc
		switch(message.getMessageType())
		{
			case CHATMESSAGE:
			{
				processCHATMESSAGE(message);
				break;
			}
			case RETURN_TO_LOBBY:
			{
				processRETURN_TO_LOBBY(message);
				break;
			}
			case LOGOFF:
			{
				processLOGOFF(message);
			}
			case START_GAME:
			{
				processSTART_GAME(message);
				break;
			}
			
			default:
				break;
		}
		
		//throw new IOException("server client thread state change sucessful");
	}
	
	public void processRETURN_TO_LOBBY(ClientToServerMessage message) throws IOException
	{
		
		if(thisThreadsHostedLobby.isHost(clientThread))//return all clients to main lobby list and change all states, sent client a HOST_LEFT message which closes all hosted lobby GUIs
		{	                                          // and changes states
			
			returnNonHostThreadsToMainLobby("The host returned back to the main lobby");
			thisThreadsHostedLobby.returnThreadToMainLobby(clientThread);
			//clientThread.changeState(new ServerMainLobbyState(mainLobby, clientThread));
			
			//updateClientsMainLobby();
			/**
			thisThreadsHostedLobby.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.HOST_LEFT_lOBBY, 
														     "The host left the hosted game and returned to the main lobby!"));
			thisThreadsHostedLobby.returnAllClientsToMainLobby();
			updateClientsMainLobby();
			
			//System.out.println("the host just left");
			 * **/
			 
		}
		else
		{
			//System.out.println("The host didnt leave");
		
		
			thisThreadsHostedLobby.returnThreadToMainLobby(clientThread);
			//clientThread.changeState(new ServerMainLobbyState(mainLobby, clientThread));
			//connectedClient.WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.RETURN_TO_MAIN_LOBBY, ""));
			System.out.println("returning this client back to the main lobby. he should be able to process main lobby messages now");
			
			thisThreadsHostedLobby.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
					  										 clientThread.getClient().getClientName() + " just returned to the main lobby"));
		
			thisThreadsHostedLobby.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_LOBBY_USERS, 
																					thisThreadsHostedLobby.getNamesOfHostedLobbyClients()));
			//updateClientsMainLobby();
		}
		
		updateMainLobbyUsers();
		updateMainLobbyHostedGames();
	}
	
	public void processCHATMESSAGE(ClientToServerMessage message) throws IOException
	{
		thisThreadsHostedLobby.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
				clientThread.getClient().getClientName() + ": " + message.getMessage()));
		
		System.out.println(clientThread.getClient().getClientName() + ": " + message.getMessage() + " from hosted game: " + thisThreadsHostedLobby.getGameName());
	}
	
	public void processSTART_GAME(ClientToServerMessage message) throws IOException
	{
		ServerRunningGame serverRunningGame = new ServerRunningGame(mainLobby, thisThreadsHostedLobby.getStartingMoney(), thisThreadsHostedLobby.getGameName());
		initGameForUsersInHostedLobby(serverRunningGame);
		mainLobby.removeHostedGame(thisThreadsHostedLobby);//remove the hosted game from the list of hosted games
		mainLobby.addRunningGame(serverRunningGame);
		System.out.println("added a running game to the main lobby. there are now" + mainLobby.getNumberofRunningGames());
		serverRunningGame.start();
		
		updateMainLobbyHostedGames();
		
	}
	
	public void updateMainLobbyUsers()
	{
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_USERS, 
				  													mainLobby.getNamesOfMainLobbyClients()));
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_GAMES, 
				  													mainLobby.getNamesOfHostedGames()));
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, 
																	mainLobby.getNamesOfRunningGames()));
	}
	
	public void updateMainLobbyHostedGames()
	{
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_GAMES, 
										  mainLobby.getNamesOfHostedGames()));
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, 
				mainLobby.getNamesOfRunningGames()));
	}
	
	public synchronized void returnNonHostThreadsToMainLobby(String message) throws IOException
	{
		System.out.println("the host left the game! attempting to return all clients to main lobby");
		List<ClientThread> nonHostClientsInHostedLobby = thisThreadsHostedLobby.getClientsInHostedGame();
		ClientThread otherClientThread;
		//int numberOfUsersToReturn = nonHostClientsInHostedLobby.size();
		
		System.out.println("number of users are are returning is " + (nonHostClientsInHostedLobby.size()-1));
		//start at 1 because the host will be removed after the clients are gone. host is ALWAYS position 0
		for(int i = nonHostClientsInHostedLobby.size()-1; i >= 1; i--)
		{
			System.out.println("returning a user");
			otherClientThread = nonHostClientsInHostedLobby.get(i);
			
			
			
			otherClientThread.getClient().WriteToClient((new ServerToClientMessage(ServerToClientMessage.Message.HOST_LEFT_lOBBY, message)));
			
			thisThreadsHostedLobby.returnThreadToMainLobby(otherClientThread);
			
			//otherClientThread.changeState(new ServerMainLobbyState(mainLobby, otherClientThread));
			
		}
	}
	
	//this code is very similar to the method above
	//tell all the users in ths running game to start the game GUI, change all the server/client thread states (inside the add method)
	//wrap all the clients inside of player objects and add them to the runing game
	
	
	public synchronized void initGameForUsersInHostedLobby(ServerRunningGame runningGameThread) throws IOException
	{
		System.out.println("the host started the game. changing states and messaging all hosted lobby clients");
		
		List<ClientThread> clientsGoingIntoThisRunningGame = thisThreadsHostedLobby.getClientsInHostedGame();
		//List<Player> playersInThisRunningGame = Collections.synchronizedList(new ArrayList<Player>());
		ClientThread otherClientThread;
		
		System.out.println("number of clients going into this game: " + clientsGoingIntoThisRunningGame.size());
		
		for(int i = 0; i < clientsGoingIntoThisRunningGame.size(); i++)
		{
			System.out.println("entering a user into a running game");
			otherClientThread = clientsGoingIntoThisRunningGame.get(i);
			
			
			
			otherClientThread.getClient().WriteToClient((new ServerToClientMessage(ServerToClientMessage.Message.HOST_STARTED_GAME, 
																				   thisThreadsHostedLobby.getGameName(), thisThreadsHostedLobby.getHostName())));
			
			//i is the playerID number used to reorder the list of players for the client to display
			runningGameThread.addPlayerToRunningGame(new Player(otherClientThread, thisThreadsHostedLobby.getStartingMoney(), i));
		}
		
		
		
		
	}
	
	public ServerHostedGame getThisThreadsHostedLobby()
	{
		return thisThreadsHostedLobby;
	}
	
	
	
	
	

}
