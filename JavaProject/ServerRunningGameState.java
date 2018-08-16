import java.io.IOException;


public class ServerRunningGameState extends ServerState
{
	ClientThread clientThread;
	ServerMainLobby mainLobby;
	ServerRunningGame runningGameThread;
	Player thisThreadsPlayer;//will be set to null when a player loses the game/times out or enter a running game as a spectator, otherwise has a reference to the threads player
							// object
	
	
	
	
	public ServerRunningGameState(ServerMainLobby mainLobby, ClientThread clientThread, ServerRunningGame runningGameThread, Player thisThreadsPlayer)
	{
		this.runningGameThread = runningGameThread;
		this.mainLobby = mainLobby;
		this.clientThread = clientThread; 
		this.thisThreadsPlayer = thisThreadsPlayer;
		
	}
	
	//takes a ServerHostedGame object containing the list of clients to send update messages to
	
	public void readMessage(ClientToServerMessage message) throws IOException, ClassNotFoundException
	{
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
				break;
			}
			case GAME_MOVE:
			{
				processGAME_MOVE(message);
				break;
			}
			
			default:
				break;
		}
	}
	
	public void processRETURN_TO_LOBBY(ClientToServerMessage message) throws IOException
	{
		
		
		
		//remove a spectator/player who was in game, lost/timed out, and is now a spectator
		if(thisThreadsPlayer == null)
		{
			runningGameThread.returnSpectatorToMainLobby(clientThread);
			runningGameThread.updateClientListOfSpectators();
		}
		else//we are removing a player who is in the game and wants to return to the main lobby
		{
			runningGameThread.returnPlayerToMainLobby(thisThreadsPlayer);
			runningGameThread.updateClientListofPlayers();
			//runningGameThread.getPokerGame().removePlayerFromRound(thisThreadsPlayer);
			runningGameThread.getPokerGame().playersInRound--;
			System.out.println("player returning to lobby. players in this game is now " + runningGameThread.getPokerGame().playersInRound);
			System.out.println("player returning to lobby. players in this game is now " + runningGameThread.numPlayers);
			
			
		}
		
		runningGameThread.writeToAllThreadsInThisGame(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
					  								clientThread.getClient().getClientName() + " just returned to the main lobby"));
		runningGameThread.destroyGameIfEmpty();
		updateClientsMainLobby();
	}
	
	public void processCHATMESSAGE(ClientToServerMessage message) throws IOException
	{
		runningGameThread.writeToAllThreadsInThisGame(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
				clientThread.getClient().getClientName() + ": " + message.getMessage()));
		
		System.out.println(clientThread.getClient().getClientName() + ": " + message.getMessage() + " from a running game");
	}
	
	//this is where we get a players game move and pass it to this threads player object. this code is triggered when a player presses any game input buttons on the GUI
	//in turn, the loop in the player object will successfully return with this gamemove message
	public void processGAME_MOVE(ClientToServerMessage message)
	{
		
		thisThreadsPlayer.setGameMove(message.getPlayersMoveInfo());
		
		//System.out.println("I should be setting the game move in here");
		
	
	}
	
	//update all main lobby user GUIs of the user returning from a game, and update this users hosted games list
	public void updateClientsMainLobby() throws IOException
	{
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_USERS, 
				  										mainLobby.getNamesOfMainLobbyClients()));
		
		
		clientThread.getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_GAMES, 
																		 mainLobby.getNamesOfHostedGames()));
		
		mainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, 
				 													mainLobby.getNamesOfRunningGames()));
	}
	
	
	
	public Player getThisThreadsPlayer()
	{
		return thisThreadsPlayer;
	}
	
	//when a player is removed from the game, this method gets run.
	public void removeThisThreadsPlayer()
	{
		this.thisThreadsPlayer = null; 
	}
	
	public ServerRunningGame getRunningGameThread()
	{
		return runningGameThread;
	}
	
	public ServerMainLobby getMainLobby()
	{
		return mainLobby;
	}
	
	
	
	
	

}
