import java.io.IOException;


public class ServerMainLobbyState extends ServerState 
{
	//contains a ServerMainLobby object which contains the list of threads in this state. for users sitting in main lobby to chat/join/search games. used to run code that
	//acts to change the states of the clients sitting in the main lobby
	ServerMainLobby mainLobby;
	ClientThread clientThread;
	//ConnectedClient connectedClient;//this is redundent because i can get to the connected client writer through the client thread. will chnage later
	
	
	
	public ServerMainLobbyState(ServerMainLobby mainLobby, ClientThread clientThread)
	{
		this.mainLobby = mainLobby;
		this.clientThread = clientThread; 
		//this.connectedClient = connectedClient;
	}
	
	/*
	public ServerMainLobbyState(ConnectionManager connectionManager, ConnectedClient connectedClient)
	{
		
		this.connectionManager = connectionManager;
		this.connectedClient = connectedClient;
	}
	*/
	public void readMessage(ClientToServerMessage message) throws IOException, ClassNotFoundException
	{
		//add the switch for reading client messages from the main lobby. have the code for writing to thpse clients and updating the current users list
		//throw new IOException("");
		
		
		
		switch(message.getMessageType())
		{
			case CHATMESSAGE:
			{
				processCHATMESSAGE(message);
				break;
			}
			case LOGOFF:
			{
				processLOGOFF(message);
				break;
			}
			case LOGON:
			{
				processLOGON(message);
				break;
			}
			case HOST_GAME_REQUEST:
			{
				processHOST_GAME_REQUEST(message);
				break;
			}
			case JOIN_HOSTED_GAME:
			{
				processJOIN_HOSTED_GAME(message);
				break;
			}
			case SPECTATE_GAME:
			{
				processSPECTATE_GAME(message);
			}
			default:
				break;
		}
	}
	
	public void processCHATMESSAGE(ClientToServerMessage message)
	{
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
				clientThread.getClient().getClientName() + ": " + message.getMessage()));

		System.out.println(clientThread.getClient().getClientName() + ": " + message.getMessage());
		
	}
	
	public void processLOGON(ClientToServerMessage message) throws IOException, ClassNotFoundException 
	{
		String username = message.getMessage();
		String clientOutput = null;
		boolean validName = true;
		
		
			
			//if client presses the cancel button or closes the log on dialog box
			if(username == null || username.equals("null"))
			{
				validName = false;
				clientOutput = "username box was closed";
		    } 
			else if(username.isEmpty())
		    {
				validName = false;
				clientOutput = "username can not be empty";
		    }
			else if(username.length() > 15)
			{
				validName = false;
				clientOutput = "username can not be longer then 15 characters";
			}
			else if(mainLobby.isDuplicateUserName(username))
			{
				validName = false;
				clientOutput = "username taken";
			}
			
			if(!validName)
			{
				clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.LOGON_DENIED, clientOutput));
				throw new IOException(clientOutput);
			}
			
			clientThread.getClient().setClientName(username);
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.LOGON_SUCCESS, username));
			
			mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , clientThread.getClient().getClientName() + " just connected"));
			updateClientsMainLobby();
			System.out.println(clientThread.getClient().clientName + " just connected");
	}
	
	
	public void processHOST_GAME_REQUEST(ClientToServerMessage message) throws IOException
	{
		String gameName = message.getMessage();
		int numPlayers = message.getNumPlayers();
		int startingMoney = message.getStartingMoney();
		
		//add code to check for max players and name length. if any fails, send a HOST_GAME_DENIED message
		if(gameName.length() > mainLobby.MAX_GAME_NAME)
		{
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.HOST_GAME_DENIED, "game name is longer then 15 characters"));
			return;
		}else if(numPlayers > mainLobby.MAX_PLAYERS || numPlayers == 1)
		{
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.HOST_GAME_DENIED, "game must contain 2 to 4 players"));
			return;
		}else if(mainLobby.isDuplicateHostedGameName(gameName))
		{
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.HOST_GAME_DENIED, "game name already exists on server"));
			return;
		}
		
		
		
		
		
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.HOST_GAME_SUCCESS, gameName, clientThread.getClient().getClientName()));
		
		
		
		
		
		
		//ServerHostedGame newHostedGame = new ServerHostedGame(clientThread, mainLobbyConnectedUsers, gameName, numPlayers, startingMoney);
		ServerHostedGame newHostedGame = new ServerHostedGame(mainLobby, gameName, numPlayers, startingMoney);
		addThreadToHostedGame(clientThread, newHostedGame);//this sets this thread to position 0 in the hosted game, which is the host
		//mainLobbyConnectedUsers.removeClientFromMainLobby(clientThread);
		mainLobby.addHostedGame(newHostedGame);
		
		//clientThread.changeState(new ServerHostedGameLobbyState(mainLobbyConnectedUsers, newHostedGame, clientThread));
		
		//clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.HOST_GAME_SUCCESS, gameName));//this will prompt the client to open the new GUI and change its state
		
		System.out.println(clientThread.getClient().clientName + " is requesting to make a game called " + gameName + " with " + numPlayers + " players and " + startingMoney + " money");
		System.out.println("This theread has been removed from the list of main lobby threads and should not receive anymore messages from other users, not even himself");
		
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_LOBBY_USERS, newHostedGame.getNamesOfHostedLobbyClients()));
		
		
		updateClientsMainLobby();
		
	}
	
	public void processJOIN_HOSTED_GAME(ClientToServerMessage message) throws IOException
	{
		//iterate through the list of hosted games
		String gameName = message.getMessage();
		ServerHostedGame serverHostedGame = mainLobby.findHostedGameByName(gameName);
		
		if(serverHostedGame == null)
		{
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.JOIN_HOSTED_GAME_DENIED, gameName + " does not exist"));
			System.out.println("the hosted game " + gameName + " does not exist");
			return;
		}
		
		System.out.println("found the hosted game the clent requested and got a reference to it. now checking number of players");
		
		if(serverHostedGame.isFull())
		{
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.JOIN_HOSTED_GAME_DENIED, gameName + " is full"));
			System.out.println("the hosted game is full");
			return;
		}
		
		//mainLobbyConnectedUsers.removeClientFromMainLobby(clientThread);
		//serverHostedGame.addClientToHostedGame(clientThread);
		
		addThreadToHostedGame(clientThread, serverHostedGame);
		
		
		
		
		
		
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.JOIN_HOSTED_GAME_SUCCESS, gameName, serverHostedGame.getHostName()));
		
		
		
		serverHostedGame.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_LOBBY_USERS, serverHostedGame.getNamesOfHostedLobbyClients()));
		
		//mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_USERS, mainLobby.getNamesOfMainLobbyClients()));
		updateClientsMainLobby();
		
	}
	
	private void processSPECTATE_GAME(ClientToServerMessage message) throws IOException
	{
		String gameName = message.getMessage();
		ServerRunningGame serverRunningGame = mainLobby.findRunningGameByName(gameName);
		
		if(serverRunningGame == null)
		{
			clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.SPECTATE_GAME_DENIED, "game " + gameName + " does not exist"));
			System.out.println("the running game " + gameName + " does not exist");
			return;
		}
		
		addThreadToRunningGame(clientThread, serverRunningGame);
		
		//send client message to open the running game gui with the game name
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.SPECTATE_GAME_SUCCESS, gameName));
		
		//serverRunningGame.getPokerGame().rebuildGameFrame();
		
		
		
		//update the spectator list for all users in the game
		serverRunningGame.writeToAllThreadsInThisGame(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_SPECTATOR_USERS, serverRunningGame.getNamesofActiveSpectators()));
		
		//update the playerlist for the user just joining, they have the spectator list already from the last method call
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_PLAYER_USERS, serverRunningGame.getNamesofActivePlayers()));
		
		//if(serverRunningGame.getMostRecentgameState() != null)
			//clientThread.getClient().WriteToClient(serverRunningGame.getMostRecentgameState());
		
		//get a GUI_UPDATE message with the most recent game state so the spectator has the latest game status/state/frame
		
		
		
		
		//if the game just started and the game state hasent been set yet. this will be changed once the game is implemented
		//if(serverRunningGame.getMostRecentgameState() != null)
			//clientThread.getClient().WriteToClient(serverRunningGame.getMostRecentgameState());
		
		//update the main lobby users list
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_USERS, mainLobby.getNamesOfMainLobbyClients()));
		
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, mainLobby.getNamesOfRunningGames()));
		
		serverRunningGame.getPokerGame().rebuildGameFrame();
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_GUI, serverRunningGame.getPokerGame().getCurrentGameFrame()));
		//mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, serverRunningGame.g));
		//updateClientsMainLobby();
	}
	
	public void updateClientsMainLobby()
	{
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_USERS, mainLobby.getNamesOfMainLobbyClients()));
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_GAMES, mainLobby.getNamesOfHostedGames()));
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, mainLobby.getNamesOfRunningGames()));
	}
	
	public void addThreadToHostedGame(ClientThread clientThread, ServerHostedGame serverHostedGame)
	{
		mainLobby.removeClientFromMainLobby(clientThread);
		serverHostedGame.addClientToHostedGame(clientThread);
	}
	
	public void addThreadToRunningGame(ClientThread clientThread, ServerRunningGame serverRunningGame)
	{
		mainLobby.removeClientFromMainLobby(clientThread);
		serverRunningGame.addSpectatorToRunningGame(clientThread);
	}
	
	public ServerMainLobby getMainLobby()
	{
		return mainLobby;
	}
	
	
	
	
}
