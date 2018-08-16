import java.io.IOException;




public class ClientThread extends Thread
{
	
	ConnectedClient connectedClient;
	ConnectionListenerThread connectionManager;
	ServerState currentClientThreadState;
	//ServerMainLobby mainLobbyConnectedUsers;
	//ServerHostedGame thisThreadsHostedLobby;
	
	
	public ClientThread(ConnectedClient connectedClient)
	{
		this.connectedClient = connectedClient;
		
		//this.mainLobbyConnectedUsers = mainLobbyConnectedUsers;
		//currentClientThreadState = new ServerMainLobbyState(mainLobbyConnectedUsers, this);
		//thisThreadsHostedLobby = null;
	}
	
	public void run()
	{
		
		try
		{
			
			ClientToServerMessage message;
			
			do
			{
				message = connectedClient.ReadFromClient();
				
				currentClientThreadState.readMessage(message);
			}
			while(message != null);
			    		
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("client dissconnected suddenly without logging out");
		} 
		catch (ClassNotFoundException e) 
		{
			
			e.printStackTrace();
		}
		
		System.out.println("removing a logged out or dissconnected client from list and cleaning up resources");
		try 
		{
			connectedClient.closeClient();
		} 
		catch (IOException e) 
		{
			System.out.println("exception closing the disconnected clients resources");
			e.printStackTrace();
		}
		
		//need to add cleanup code for a user suddenly dissconnecting from a hosted game lobby. im thinking an if else ladder to see what state the thread is in, fetching the 
		//right data and removing the thread from the appropriate lists. if a host of a hosted lobby suddenly disconnected, need to remove the rest of the connected users and 
		//clean up/remove the hosted game. in fact, moving that cleanup code from the server hosted lobby LOG OUT message would be appropriate. just have that method throw
		//in IOexception and let the cleanup code at the end of the thread tie up lose ends.
		if(currentClientThreadState instanceof ServerMainLobbyState)
		{
			cleanupMainLobby();
			
		}
		else if(currentClientThreadState instanceof ServerHostedGameLobbyState)
		{
			cleanupHostedLobby();
			
		}
		else if(currentClientThreadState instanceof ServerRunningGameState)
		{
			cleanupRunningGame();
			
		}
		
		
		
		System.out.println(connectedClient.clientName + " just dissconnected from the server");
	}
	
	public ConnectedClient getClient()
	{
		return connectedClient;
	}
	
	public void changeState(ServerState newThreadState)
	  {
		currentClientThreadState = newThreadState;
	  }
	
	
	public ServerState getCurrentThreadState()
	{
		return currentClientThreadState;
	}
	
	private void cleanupMainLobby()
	{
		ServerMainLobby serverMainLobby = ((ServerMainLobbyState) currentClientThreadState).getMainLobby();
		System.out.println("the server main lobby state is being cleaned up");
		
		serverMainLobby.removeClientFromMainLobby(this);
		
		System.out.println("exiting thread" + " there are now " + serverMainLobby.getNumberOfMainLobbyClients() + " clients in the list of main lobby clients");
		
		serverMainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_USERS, serverMainLobby.getNamesOfMainLobbyClients()));
		
		serverMainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
												  connectedClient.getClientName() + " just dissconnected"));
	}
	
	private void cleanupHostedLobby()
	{
		//if the current lobby state is a hosted lobby state, CAST the current threadstate so we can fetch the clients hosted lobby reference
		ServerHostedGame thisThreadsHostedLobby = ((ServerHostedGameLobbyState) currentClientThreadState).getThisThreadsHostedLobby();
		
		if(thisThreadsHostedLobby.isHost(this))
		{
			System.out.println("the host client logged off/suddenly disconnected from a hosted lobby! ");
			System.out.println("removing all clients from the hosted game thread list and updating the hosted game list and main lobby user list");
			
				try {
					((ServerHostedGameLobbyState) currentClientThreadState).returnNonHostThreadsToMainLobby("The host disconnected!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				thisThreadsHostedLobby.removeClientFromHostedGame(this);
			
			((ServerHostedGameLobbyState) currentClientThreadState).updateMainLobbyHostedGames();
			((ServerHostedGameLobbyState) currentClientThreadState).updateMainLobbyUsers();
			
		}	
		else
		{
				
			System.out.println("a client who isnt the host logged off/suddenly disconnected from a hosted lobby! ");
			System.out.println("removing the client from the hosted game thread list and updating the hosted lobbby user list");
			thisThreadsHostedLobby.removeClientFromHostedGame(this);
			
			thisThreadsHostedLobby.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
					  connectedClient.getClientName() + " just dissconnected"));
		
			thisThreadsHostedLobby.writeToClientsInHostedGame(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_HOSTED_LOBBY_USERS, 
			                                          	  	  thisThreadsHostedLobby.getNamesOfHostedLobbyClients()));
			
			((ServerHostedGameLobbyState) currentClientThreadState).updateMainLobbyHostedGames();
		
			System.out.println("there are now " + thisThreadsHostedLobby.numOfClientsInThisHostedLobby() + " clients in the hosted lobby: "  + thisThreadsHostedLobby.getGameName());
		}
	}
	
	private void cleanupRunningGame()
	{
		ServerRunningGameState serverRunningGameState = ((ServerRunningGameState)currentClientThreadState);
		
		//if this thread was a spectator...
		if(serverRunningGameState.getThisThreadsPlayer() == null)
		{
			serverRunningGameState.getRunningGameThread().removeSpectator(this);//remove them from the spectator list
			serverRunningGameState.getRunningGameThread().updateClientListOfSpectators();//update the spectators in the client
		}
		else//we are removing a player who is in the game and logged off/suddenly dissconnected
		{
			Player player = serverRunningGameState.getThisThreadsPlayer();
			serverRunningGameState.getRunningGameThread().removePlayerFromGame(player);
			serverRunningGameState.getRunningGameThread().updateClientListofPlayers();//update the players in the client
			///serverRunningGameState.getRunningGameThread().getPokerGame().removePlayerFromRound(player);
			serverRunningGameState.getRunningGameThread().getPokerGame().playersInRound--;
			System.out.println("we are removing a player from the game because they disconnected/logged out!");
			serverRunningGameState.getRunningGameThread().getPokerGame().rebuildAndTransitGameFrame();
		}
		
		serverRunningGameState.getRunningGameThread().destroyGameIfEmpty();
		//update the running game on the main lobby 
		serverRunningGameState.getMainLobby().writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, 
																	  serverRunningGameState.getMainLobby().getNamesOfRunningGames()));
		
		serverRunningGameState.getRunningGameThread().writeToAllThreadsInThisGame(new ServerToClientMessage(ServerToClientMessage.Message.CHATMESSAGE , 
																				getClient().getClientName() + " just dissconnected from the server"));
		
		
		
		
	}
}
