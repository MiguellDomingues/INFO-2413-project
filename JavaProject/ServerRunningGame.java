import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * TODO list:
 * - update the players in a running game as they leave (done)
 * - start populating the spectator list. a player who loses goes out of the player list and into a spectator list(done)
 *  - a spectator list still receives gui updates every player move (done)
 *  - have a list of spectators that is updated as players lose/spectate the game (done)
 *  - need to maintain a list of running games in the main lobby so players can spectate games
 *   	*make sure that the last gui update is remembered by the game so spectators just joining can get it (done?)
 *   - add a timer to the game input loop to boot inactive players from the game (done?)
 *
 *	- i really should make seperate synchronized locks for dealing with the 2 seperate lists. spamming synchronized makes things inefficient
 *  - need to pass a turn timer to the client with the initial HOST_STARTED_GAME message and have that timer display on every clients GUI during there turm
 */


public class ServerRunningGame extends Thread
{
	
	
	//private Object lock = new Object();
	
	
	
	List<Player> playersInThisRunningGame;//list of players in the game
	List<ClientThread> spectaterClientsInThisRunningGame;//list of spectator clients
	
	//store the most recently built GAME_GUI message so spectators just joining can receive it. ISSUE: a player is just starting the first turn and a spectator joins, this will
	//be null. make sure this isnt null before sending it. OR construct a basic GUI_UPDATE in this constructor containing the all players in the game.
	ServerToClientMessage mostRecentGameState;
	
	ServerMainLobby serverMainLobby;
	int startingPlayerMoney;
	String gameName;
	int numPlayers;
	PokerGame pokerGame;
	
	public ServerRunningGame(ServerMainLobby serverMainLobby, int startingPlayerMoney, String gameName)
	{
		spectaterClientsInThisRunningGame = Collections.synchronizedList(new ArrayList<ClientThread>());
		playersInThisRunningGame = Collections.synchronizedList(new ArrayList<Player>());
		
		
		this.serverMainLobby = serverMainLobby;
		
		this.startingPlayerMoney = startingPlayerMoney;
		this.gameName = gameName;
		numPlayers = 0;
	}
	
	 
	 
	 //flag player for removal at the end of an iteration of players in the game loop and add there thread to the mainlobby
	 public void returnPlayerToMainLobby(Player player)
	 {
		 removePlayerFromGame(player);//flag player
		 serverMainLobby.addClientToMainLobby(player.getThisPlayersThread());//return player to main lobby
	 }
	 
	 //remove a spectator client from this game and add them back to the main lobby list
	 public void returnSpectatorToMainLobby(ClientThread clientThread)
	 {
		 removeSpectator(clientThread);//remove spectator from list
		 serverMainLobby.addClientToMainLobby(clientThread);//add them back to main lobby
	 }
	 
	 //set the flag on a player for removal and decrement the number of active players
	 public void removePlayerFromGame(Player player)
	 {
		 player.removeFromGame();
		 numPlayers--;
	 }
	 
	 //called before the game loop starts. adds a client wrapped in a player object to the list of active players and changes there thread state.
	 //increment number of players
	 public synchronized void addPlayerToRunningGame(Player player)
	 {
		 player.getThisPlayersThread().changeState(new ServerRunningGameState(serverMainLobby, player.getThisPlayersThread(), this, player));
		 playersInThisRunningGame.add(player);
		 numPlayers++;
	 }
	 
	 //unused method to be used for adding main lobby clients to the list of spectators in this running game
	 public synchronized void addSpectatorToRunningGame(ClientThread clientThread)
	 {
		 addSpectator(clientThread);
		 clientThread.changeState(new ServerRunningGameState(serverMainLobby, clientThread, this, null));
		 
	 }
	 
	 //unused method
	 public List<Player> getClientsInThisRunningGame()
	 {
		 return playersInThisRunningGame;
	 }
	 
	 //MAIN GAME LOOP
	 public void run()
	 {
		 //testForServerThreadState();
		 updateClientListofPlayers();//upon game start, update the player list for each player in the game
		 System.out.println("The game loop has started");
		 
		 pokerGame = new PokerGame(playersInThisRunningGame, this);
		 pokerGame.playPoker();
		 
		 /*
		 //while this game has at least 2 players...
		 while(playersInThisRunningGame.size() > 1)
		 {
			 //fetch iterator
			 Iterator<Player> playerList = playersInThisRunningGame.listIterator();
			 
			 //iterate through all the players..
			 while(playerList.hasNext())
			 {
				 Player player = playerList.next();//get player from list
				 
				 System.out.println("It is now " + player.getPlayerName() + " turn");
				 
				 //if the player is still in the game...
				 if(player.isPlayerInGame())
				 {
					 //take this players turn
					 System.out.println("This player is in the game");
					 signalPlayertoTakeTurn(player);//unlock the game moves for this players GUI
					 ClientToServerMessage gameMove = player.getPlayerMove();//loop intill we get a game move OR the player leaves game/dissconnects
					 player.eraseCurrentGameMove();//once we stored the player move, delete the last input move
					 
					 if(gameMove != null && player.isPlayerInGame())//if the player entered a game move and did not leave game/disconnect
					 {
						 mostRecentGameState = processGameMove(gameMove, player);//process the game move, return a GUI_UPDATE message with updates for the players/spectators
						 														 //SAVE that GUI_UPDATE message for spectators that randomly join the game
						 writeToAllThreadsInThisGame(mostRecentGameState);//send that GUI_UPDATE message to all players/spectators in the game
					 }
					 else if(player.isPlayerInGame())//the player timed out taking a move and is being removed from the game
					 {
						
						 System.out.println("a player took too long to act! move them to spectator list");
						 disablePlayerGUI(player);
						 movePlayerToSpectatorList(player);//add player to spectator list and remove them from the game
						 
					 }
				}
				else//the player dissconnected/left game in the middle of another players turn. print debug information
				{
					System.out.println("it appears " + player.getPlayerName() + " left the game suddenly. he will be removed once all players finished there moves");
				}
			}
			 
			updatePlayerList();//all players that might have left/lost game/disconnected are removed from the main game list
			
		}//loop again
		 //note that a winner can only be declared after all a round of all initial starting players has occured. this means:
		 // - if all the players except one leave in the middle of the loop, a winner will not be decided intill that player takes there turn
		 // - if ALL the players leave the below code will bug out and throw an exception because it assumes one player is left after the others have lost game/quit
		 // - better solution: another seperate thread that runs a loop which concurently checks the number of players. when players == 1, declare winner, inform spectators
		 // who the winner is, and interupt() this thread.
		 
		 System.out.println("the game has ended! breaking out of the game loop! declaring a winner!");
		 declareWinner();//if there is only one player left, signal that client that they are the winner
		 //add code to let all spectators know the game is over
		  * */
		  
		 
	 }
	 
	 //send a message to a player to unlock the game move portion of the GUI to accept game input
	 public void signalPlayertoTakeTurn(Player player, PlayerTakeTurnInfo playerTakeTurnInfo)
	 {
		 try 
		 {
			 System.out.println("This player is getting the message to unlock the game GUI");
			player.getThisPlayersThread().getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.TAKE_TURN, playerTakeTurnInfo));
		 } 
		 catch (IOException e) 
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error signaling player to take turn");
		}
	 }
	 
	 //this code block should contain code to process the players game move, update the current game state etc. it should construct a message
	 //to update the running game GUIs of all the current players/spectators in the game to the current game state and return it
	 //also needs to contain code to make a player a loser and add them to the spectator list
	 private ServerToClientMessage processGameMove(ClientToServerMessage gameMove, Player player)
	 {
		 String playerMove = gameMove.getMessage();
		 System.out.println("the game move was" + playerMove);
		 //ADD CODE TO PROCESS GAME MOVE, UPDATE GAME STATE, DELCARE A LOSER AND REMOVE FROM PLAYER LIST ETC
		 
		 //this is test code for lose conditions
		 if(playerMove.equals("a game move:LOSE"))
		 {
			 System.out.println(player.getPlayerName() + " has lost the game! moving them to spectator list");
			 movePlayerToSpectatorList(player);
		 }
		 //end test code
		 
		 return new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_GUI, playerMove);
	 }
	 
	 //remove all players not in the game from the playerlist
	 private synchronized void updatePlayerList()
	 {
			 System.out.println("now deleting all players not in the game from the game list");
			 Iterator<Player> playerList = playersInThisRunningGame.listIterator();
		 
			 while(playerList.hasNext())
			 {
				 Player player = playerList.next();//get player from list
			 
				 if(!player.isPlayerInGame())//check the flag 
				 {
					 System.out.println(player.getPlayerName() + "is out of the game! removing him");
					 playerList.remove();
				 }
			 }
	}
	 
	 //assuming there is one player left in the player list, get that player and send them a victory message
	 public void declareWinner()
	 {
		 
		 try 
		 {
			 Player winningPlayer = playersInThisRunningGame.listIterator().next();
			 System.out.println("The winner is " + winningPlayer.getPlayerName()); 
			 
			winningPlayer.getThisPlayersThread().getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.DECLARE_LOSER, 
																									 "A WINNER IS YOU" + winningPlayer.getPlayerName()));
			movePlayerToSpectatorList(winningPlayer);
			
		 } 
		 catch (Exception e) { e.printStackTrace(); System.out.println("exception caught. most likely it means all the players lost/left and were removed"
		 																+ " completely from the player list before a winner could be declared");}
		 
		 
	 }
	 
	 //when a player gets removed from the game for idling, disable there game input GUI
	 public void disablePlayerGUI(Player player)
	 {
		 try {
			player.getThisPlayersThread().getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.LOSE_TURN,
					 																		   "You are being removed from the game for inactivity"));
		} catch (IOException e) {
			System.out.println("error disabling players GUI");
			e.printStackTrace();
		}
	 }
	 
	 //update the GUI list of players
	 public void updateClientListofPlayers()
	 {
		 String playerList = getNamesofActivePlayers();
		 writeToAllThreadsInThisGame(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_PLAYER_USERS, playerList));
	 }
	 
	 //update the GUI list of spectators
	 public void updateClientListOfSpectators()
	 {
		 String spectatorList =  getNamesofActiveSpectators();
		 writeToAllThreadsInThisGame(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_SPECTATOR_USERS, spectatorList));
		 
	 }
	 
	 //go through the list of players and output the ones that are stil in the game
	 public synchronized String getNamesofActivePlayers()
	 {
		 String connectedPlayers = "";
		 
		for(int i = 0; i < playersInThisRunningGame.size(); i++)
		{
			 Player player = playersInThisRunningGame.get(i);
			 
			 if(player.isPlayerInGame())
				 connectedPlayers = connectedPlayers + player.getPlayerName() + "\n";
		
		}
		 
		 return connectedPlayers;
	 }
	 
	 //get a list of names of spectators
	 public synchronized String getNamesofActiveSpectators()
	 {
		 String connectedSpectators = "";
		 
		for(int i = 0; i < spectaterClientsInThisRunningGame.size(); i++)
			connectedSpectators = connectedSpectators + spectaterClientsInThisRunningGame.get(i).getClient().getClientName() + "\n";
		 
		 return connectedSpectators;
	 }
	 
	 //write a message only to players still in the game
	 public synchronized ServerToClientMessage writeToPlayersInRunningGame(ServerToClientMessage message)
	 {
		
			 for(int i = 0; i < playersInThisRunningGame.size(); i++)
			 {
				 try
				 {	 
					 Player player = playersInThisRunningGame.get(i);
				 
					 if(player.isPlayerInGame())
					 {
						 player.getThisPlayersThread().getClient().WriteToClient(message);
					 }
				 }
				 catch(IOException e){System.out.println("error writing to players in a game");}
			 }
		
		 return message;
	 }
	 
	 //write a message to all spectators
	 public synchronized ServerToClientMessage writeToSpectatorsInRunningGame(ServerToClientMessage message)
	 {
		 for(int i = 0; i < spectaterClientsInThisRunningGame.size(); i++)
		  {
			 try
			 {	 
				 ClientThread clientThread = spectaterClientsInThisRunningGame.get(i);
				 clientThread.getClient().WriteToClient(message);
				 
			 }
			 catch(IOException e){System.out.println("error writing to spectators in a game");}
		  }
		 
		 return message;
	 }
	 
	 //add a spectator to the game
	 public synchronized void addSpectator(ClientThread clientThread)
	 {
		 spectaterClientsInThisRunningGame.add(clientThread);
	 }
	 
	 //remove a spectators from the game
	 public synchronized void removeSpectator(ClientThread clientThread)
	 {
		 spectaterClientsInThisRunningGame.remove(clientThread);
	 }
	 
	 //flag a player for removal and add there thread to the spectator list, then update the list of players/clients for the GUIs. update the main lobby clients GUI as well
	 public void movePlayerToSpectatorList(Player player)
	 {	
		 
		 removePlayerFromGame(player);
		 addSpectator(player.getThisPlayersThread());
		 System.out.println(player.getPlayerName() + " has been removed from the game!" + "there are now " + getNumberOfSpectators() + " spectators and "
				 								   + numPlayers + " players in this game");
		 updateClientListofPlayers();
		 updateClientListOfSpectators();
		 
		 serverMainLobby.writeToConnectedClients(new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_RUNNING_GAMES, serverMainLobby.getNamesOfRunningGames()));
		 
	 }
	 
	
	 
	 
	 //write a message to all players and spectators
	 public void writeToAllThreadsInThisGame(ServerToClientMessage message)
	 {
		 writeToPlayersInRunningGame(message);
		 writeToSpectatorsInRunningGame(message);
	 }
	 
	
	 
	 //return the number of spectators in this game
	 public int getNumberOfSpectators()
	 {
		 return spectaterClientsInThisRunningGame.size();
	 }
	 
	 public int getNumberofPlayers()
	 {
		 return numPlayers;
	 }
	 
	 public String getGameName()
	 {
		 return gameName;
	 }
	 
	 public ServerToClientMessage getMostRecentgameState()
	 {
		 return mostRecentGameState;
	 }
	 
	 public boolean gameIsEmpty()
	 {
		 return (numPlayers == 0) && (getNumberOfSpectators() == 0);
	 }
	 
	 public synchronized void destroyGameIfEmpty()
	 {
		 if(gameIsEmpty())
			 serverMainLobby.removeRunningGame(this);
	 }
	 
	 public PokerGame getPokerGame()
	 {
		 return pokerGame;
	 }
}
