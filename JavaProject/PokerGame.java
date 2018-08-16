import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;





/*
 TODO:

 
 - debug the crashes when a player logs off or leaves the game. i think i know what causing it DONE
 
 	
- debug the scroll bar. need to add some if/else checker code to make sure outgoing wagers are correct DONE? not rly tested
	
	- make a check to make the wager label say ALL IN when its scrolled over in the client DONE
- need to account for the 10% increase on the blind if player wants to raise/bet on the client side. so the minimum = minimum*.1 + minimum DONE


- need to add a client flag to reveal all the player cards in the showdown phase DONE

- need to add some client gui cleanup code at the end of the game. declare the winner. also need to add some code in case ALL PLAYERS leave the game. DONE

- really should have a way of informing players that they are out of the game. think i will reuse the old GAME_WINNER message to send to players
that have a stack of zero and send them to the spectator list. DONE

- send a chat message to all users in a game when someone logs off /disconnects DONE

- need to hack job the evluator method. think ill just make it so it adds the suites/ranks up or somthing. making a true evaluator will be a pain

- need to take a look at the pay blind method. make sure it shows who paid the blind and how much, make sure it removes players from the game and adds them
to spec list

- appears my deck method is giving dupicate cards somehow. ill look into it. who cares honestly though.
 
 

 */









public class PokerGame 
{
	int pot;
	List<Player> playersInThisPokerHand;
	ServerRunningGame serverRunningGame;
	
	//boolean clientShowCards;
	 int playersInRound;
	 int roundNumber;
	
	 int startingStacksSum;
	 List<Player> testList;
	 
	 CurrentGameFrame currentGameFrame;
	 
	 String gamePhase;
	 String playerTurn;
	 String gameInformation;
	
	 CommunityCards communityCards;
	
	public  final int MAX_COMMUNITY_CARDS = 5;
	public  final int STARTING_BLIND = 20;
	//public  final int STARTING_STACK = 1000;
	public  final double MIN_WAGER_INCREASE = .1;//a raise has to be at least 10% higher then the last wager
	
	public PokerGame(List<Player> playersInThisPokerHand, ServerRunningGame serverRunningGame)
	{
		this.playersInThisPokerHand = playersInThisPokerHand;
		playersInRound = playersInThisPokerHand.size();
		startingStacksSum = playersInThisPokerHand.get(0).getStack()*playersInThisPokerHand.size();
		pot = 0;
		this.serverRunningGame = serverRunningGame;
		gamePhase = "PRE-BET";
		roundNumber = 0;
		
	}
	
	public void playPoker()
	{
		int blind;
		
		//new GameChecker(serverRunningGame).start();
		
		communityCards = new CommunityCards();
		
		testList = playersInThisPokerHand;
		//PokerGameTester a = new PokerGameTester();
		
		//currentGameFrame = new CurrentGameFrame((ArrayList<Player>) playersInThisPokerHand, pot, gamePhase);
		//currentGameFrame = new CurrentGameFrame();
		//rebuildAndTransitGameFrame();
		
		
		//MAIN GAME LOOP
		while(playersInThisPokerHand.size() > 1)
		{
			Deck deck = new Deck();
			roundNumber++;
			blind = STARTING_BLIND*roundNumber;//blinds go up every hand by round number x starting blind
			System.out.println("--------------------------------------starting a new hand-------------------------------------------");
			System.out.println("initing players. reseting pot. wiping community cards");
			System.out.println("the blind for round number " + roundNumber + " will be " + blind);
			
			try
			{
				initRound(playersInThisPokerHand, communityCards, deck);
				printPlayersInGame(playersInThisPokerHand);
				
				//make a seperate message for the blind to send to client
				firstPlayerAddBlindToPot(playersInThisPokerHand, blind);
				
				gamePhase = "PRE-BET" + " ROUND NUMBER " + roundNumber;
				rebuildAndTransitGameFrame();
			
				//preflop betting round
				pokerhand(playersInThisPokerHand, blind);
				System.out.println("end of preflop betting round");
			
				printPlayersInGame(playersInThisPokerHand);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				System.out.println("-------------------------the flop! start of flop betting round---------------------------------------");
				
				communityCards.getTheFlop(deck);
				gamePhase = "THE FLOP." + " ROUND NUMBER " + roundNumber;
				//rebuildAndTransitGameFrame();
				
				
				
				pokerhand(playersInThisPokerHand, blind);
				System.out.println("end of flop betting round");
			
				printPlayersInGame(playersInThisPokerHand);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				System.out.println("-------------------------the river! start of turn betting round-------------------------");
				
				communityCards.getTheRiver(deck);
				gamePhase = "THE RIVER." + " ROUND NUMBER " + roundNumber;
				//rebuildAndTransitGameFrame();
				
				
				pokerhand(playersInThisPokerHand, blind);
				System.out.println("end of river betting round");
			
				printPlayersInGame(playersInThisPokerHand);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				
				
				System.out.println("-------------------------the turn! start of river betting round-------------------------");
				
				communityCards.getTheTurn(deck);
				gamePhase = "THE TURN." + " ROUND NUMBER " + roundNumber;
				//rebuildAndTransitGameFrame();
				
				
				pokerhand(playersInThisPokerHand, blind);
				System.out.println("end of turn betting round");
			
				printPlayersInGame(playersInThisPokerHand);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				
				
				//gamePhase = "THE SHOWDOWN." + " ROUND NUMBER " + roundNumber;
				//rebuildAndTransitGameFrame();
			
				printPlayersInGame(playersInThisPokerHand);
				System.out.println("the pot is " + pot);
				validatePotAndStacks();
				
				throw new SuddenDeathShowdownException();
			
			}
			catch(OtherPlayersFoldedException e)
			{
				//this check to ensure that this code dosent get run if all players leave the game
				if(playersInRound > 1)
				{
					//run some code to reward the remaining player stillInGame the pot
					System.out.println("other players folded");
					System.out.println("reward the remaining player the pot");
					Player winner = declareHandWinner(playersInThisPokerHand);
				
					System.out.println("The winner is " + winner.getPlayerName() + ". he won the pot of " + pot);
					winner.addChipsToStack(emptyOutPot());
				
					gamePhase = "Everyone Folded! Hand winner: " + winner.getPlayerName();
					rebuildAndTransitGameFrame();
				
					System.out.println("he now has " + winner.getStack());
					validatePotAndStacks();
				}
				
			}
			catch(SuddenDeathShowdownException b)
			{
				//this check to ensure that this code dosent get run if all players/all players except 1 leave the game
				if(playersInRound > 1)
				{
					System.out.println("the showdown! compare all player hands and declare a winner");
					//trigger some kind of flag to make the client reveal all the cards
					System.out.println("making sure all community cards are in play");
					communityCards.revealAllCommunityCards(deck);
				
					playerTurn = "WHO WILL WIN";
					gamePhase = "THE SHOWDOWN";
					rebuildAndTransitGameFrame();
				
					try {Thread.sleep(3000);} catch (InterruptedException d) {d.printStackTrace();}
				
				
				
					System.out.println("show all player hands and evaluate them. return a winning player");
					Player winner = evaluatePlayerHands(playersInThisPokerHand, communityCards);//currently returns a random player
					//System.out.println("increments the winning player chips by the pot");
				
				
				
				
					System.out.println("The winner is " + winner.getPlayerName() + ". he won the pot of " + pot);
					winner.addChipsToStack(emptyOutPot());
				
					gameInformation = winner.getPlayerName() + " just won the last hand!";
					rebuildAndTransitGameFrame();
					System.out.println("he now has " + winner.getStack());
					try {Thread.sleep(5000);} catch (InterruptedException d) {d.printStackTrace();}
					validatePotAndStacks();
				}
				
			}
			
			//at the end of every hand, players who have 0 chips or left game/disconnected (isInGame == false)
			//System.out.println("remove all removedFromGame and 0 chip players from list, or do it outside the loop");
			//try {Thread.sleep(5000);} catch (InterruptedException d) {d.printStackTrace();}
			cullPlayerList(playersInThisPokerHand);
			
			
		}
		
		//add a check for number of players left in the game to make sure it has at least one player
		//or just catch the exception and say nobody won because everyone left the game. hack job it
		String winnerName = "";
		
		try
		{
			winnerName = playersInThisPokerHand.get(0).getPlayerName();
		}
		catch(Exception e){e.getStackTrace();}
		
		System.out.println("Game Over. all other players left the game or ran out of chips! The winner is " + winnerName);
		validatePotAndStacks();
		setEndOfGameState(playersInThisPokerHand);
	}
	
	public void setEndOfGameState(List<Player> playerList)
	{
		resetPlayerMoves(playerList);
		
		gamePhase = "GAME OVER";
		playerTurn = "";
		
		assert playerList.size() > 1;
		//gameInformation
		if(!playerList.isEmpty())
		{
			gameInformation = "THE WINNER IS " + playerList.get(0).getPlayerName();
			serverRunningGame.movePlayerToSpectatorList(playerList.get(0));
		}
		else
			gameInformation = "THERE IS NO WINNER";
		
		rebuildAndTransitGameFrame();
	}
	
	//----------------------------------methods for manipulating the pot--------------------------------------------------
	
		public int takeChipsFromPot(int amount)
		{
			pot = pot - amount;
			
			if(pot < 0)
				System.out.println("ERROR. POT IS NEGATIVE");
			
			return amount;
		}
		
		public int emptyOutPot()
		{
			int total;
			total = pot;
			pot = 0;
			return total;
		}
		
		public void addChipsToPot(int chipAmount)
		{
			pot = pot + chipAmount;
		}
		
		//---------------------------------player input methods--------------------------------------
		
		
		//these methods need to be changed so they send a TAKE_TURN message to the players GUI with one of three turns to take,
		//either a PREBET, BET and SUDDEN DEATH with a minimum bet/call amount
		//method will return a GameMove, and will add onto the GameFrame message that is sent to all players and spectators after every move
		//the input checking will be done on the client side to save fuss
		public PlayersMoveInfo getpreBetInput(Player player, int minimumBet)
		{
			//MAKE SURE TO ACCOUNT FOR THE 10% INCREASE ON THE BLIND IF PLAYER WANTS TO RAISE
			serverRunningGame.signalPlayertoTakeTurn(player, new PlayerTakeTurnInfo(minimumBet, player.getStack(), PlayerTakeTurnInfo.PlayerMoveType.PRE_BET));
			//ClientToServerMessage playersMoveServerMessage = player.getPlayerMove();
			
			player.eraseCurrentGameMove();
			PlayersMoveInfo playersMove = player.getPlayerMove();
			
			
			int playerWager = playersMove.getPlayerWager();
			Player.GameMove gameMove = playersMove.getGameMove();
			
			System.out.println("player " + player.getPlayerName() + " did a " + gameMove.toString() + " and bet " + playerWager + " in a prebet round");
			
			switch(gameMove)
			{
				case CHECK:
				{
					System.out.println(player.getPlayerName() + " is checking in a pre-bet round");
					break;
				}
				case BET:
				{
					System.out.println(player.getPlayerName() + " is betting in a pre-bet round");
					break;
				}
				case FOLD:
				{
					System.out.println(player.getPlayerName() + "player is folding in a pre-bet round");
					break;
				}
				default:
					System.out.println(player.getPlayerName() + "ERROR UNKNOWN GAME MOVE IN PRE-BET ROUND");
			}
			
			return playersMove;
			
		}
		
		
		
		public PlayersMoveInfo getBetInput(Player player, int minimumBet)
		{
			//MAKE SURE TO ACCOUNT FOR THE 10% INCREASE ON THE LAST BET OR RAISE IF PLAYER WANTS TO RAISE
			serverRunningGame.signalPlayertoTakeTurn(player, new PlayerTakeTurnInfo(minimumBet, player.getStack(), PlayerTakeTurnInfo.PlayerMoveType.BET));
			
			//ClientToServerMessage playersMoveServerMessage = player.getPlayerMove();
			player.eraseCurrentGameMove();
			PlayersMoveInfo playersMove = player.getPlayerMove();
			//PlayersMoveInfo playersMove = playersMoveServerMessage.getPlayersMoveInfo();
			//player.eraseCurrentGameMove();
			
			
			//debug info
			int playerWager = playersMove.getPlayerWager();
			Player.GameMove gameMove = playersMove.getGameMove();
			
			System.out.println("player " + player.getPlayerName() + " did a " + gameMove.toString() + " and bet " + playerWager + " in a betting round");
			
			switch(gameMove)
			{
				case CALL:
				{
					System.out.println(player.getPlayerName() + " is calling in a betting round");
					break;
				}
				case RAISE:
				{
					System.out.println(player.getPlayerName() + " is raising in a betting round");
					break;
				}
				case FOLD:
				{
					System.out.println(player.getPlayerName() + "player is folding in a betting round");
					break;
				}
				default:
					System.out.println(player.getPlayerName() + "ERROR UNKNOWN GAME MOVE IN BETTING ROUND");
			}
			
			return playersMove;
			
			
		}
		
		public PlayersMoveInfo getSuddenDeathBetInput(Player player, int allInAmount)
		{
			serverRunningGame.signalPlayertoTakeTurn(player, new PlayerTakeTurnInfo(allInAmount, player.getStack(), PlayerTakeTurnInfo.PlayerMoveType.SUDDEN_DEATH));
			
			//ClientToServerMessage playersMoveServerMessage = player.getPlayerMove();
			player.eraseCurrentGameMove();
			PlayersMoveInfo playersMove = player.getPlayerMove();
			//PlayersMoveInfo playersMove = playersMoveServerMessage.getPlayersMoveInfo();
			//player.eraseCurrentGameMove();
			
			int playerWager = playersMove.getPlayerWager();
			Player.GameMove gameMove = playersMove.getGameMove();
			
			System.out.println("player " + player.getPlayerName() + " did a " + gameMove.toString() + " and bet " + playerWager + " in a sudden death round");
			
			//debug info
			switch(gameMove)
			{
				case CALL:
				{
					System.out.println(player.getPlayerName() + " is calling in a sudden death round");
					break;
				}
				case FOLD:
				{
					System.out.println(player.getPlayerName() + "player is folding in a sudden death round");
					break;
				}
				default:
					System.out.println(player.getPlayerName() + "ERROR UNKNOWN GAME MOVE IN SUDDEN DEATH ROUND");
			}
			
			return playersMove;
			
			
		}
		
		//----------------------------------methods for getting chip values from player----------------------------------
		//note that these need to be changed. need to make a PlayerMove class that encapulates the game move AND the stack amount inside one small class
		//and is sent when a player makes a game move. these values will be fetched as input for the appropriate methods
		
		public void firstPlayerAddBlindToPot(List<Player> playerList, int blind)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			 while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 if(player.isPlayerInRound())
				 {
					 if(player.getStack() <= blind)
					 {
							System.out.println(player.getPlayerName() + " cant meet the blind! hes out of the game!");
							removePlayerFromRound(player);
							player.removeFromGame();
							//need to add code to remove the player from the game so they can be culled at the end of the hand
					 }
					 else
					 {
						 player.takeChipsFromStack(blind);
						 
						 System.out.println(player.getPlayerName() + " just added a blind of " + blind);
						 playerTurn = player.getPlayerName() + " just added a blind of " + blind;
						 addChipsToPot(blind);
						 return;
					 }
				 }
					 
			 }
		}
		
		//for min wager and wager, just unwrap the encapulated playermove object sent from client and fetch the returned call/bet/raise and use it as a 
		//parameter for these method calls
		public int getMinimumWagerFromPlayer(Player player, int minimumWager)
		{
			if(minimumWager >= player.getStack())//this code needs to be in the betting round loop because the player cant meet the call. they have to all in or fold.
			{
				System.out.println("player cant meet the minimum bet. hes all in with " + player.getStack());
				minimumWager = player.getStack();
			}
			else
			{
				System.out.println("player " + player.getPlayerName() + " has called the minimum bet of " + minimumWager);
			}
			
			player.takeChipsFromStack(minimumWager);
			
			addChipsToPot(minimumWager);
			player.setLastWager(minimumWager);
			
			System.out.println("just took " + minimumWager + " from stack and added " + minimumWager + " to pot. now has " + player.getStack());
			System.out.println("the last move was a " + player.getLastGameMove().getGameMove().toString() + " " + player.getLastWager());
			System.out.println("the pot now has " + pot); 
			
			return minimumWager;
		}
		
		public int getWagerFromPlayer(Player player, int wager)
		{
			player.takeChipsFromStack(wager);
			addChipsToPot(wager);
			player.setLastWager(wager);
			
			System.out.println("just took " + wager + " from stack and added " + wager + " to pot. player has " + player.getStack());
			System.out.println("the last move was a " + player.getLastGameMove().getGameMove().toString());
			System.out.println("the pot now has " + pot); 
			
			return wager;
		}
		//--------------------------------------------------main betting methods------------------------------------
		
		
		public void bettingRound(List<Player> playerList, int minimumBet, boolean suddenDeath) throws OtherPlayersFoldedException, SuddenDeathShowdownException
		{
			System.out.println("players in round " + playersInRound + " players in game " + serverRunningGame.numPlayers);
			//gameInformation = "A NEW BETTING ROUND " + "THE NEW LOW BET IS " + minimumBet;
			System.out.println("-------------STARTING A NEW BETTING ROUND---------------------------");
			rebuildAndTransitGameFrame();
			
			List<Player> RaisersCallersBetterList = new ArrayList<Player>();
			
			boolean hadRaised = false;
		
			int lowestCall = minimumBet;
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			RaisersCallersBetterList.add(playerListIterator.next());
			
			while(playerListIterator.hasNext())
			{
				 Player player = playerListIterator.next();
				 
				 if(!hadRaised && player.isPlayerInRound())
				 {
					 if(suddenDeath)
						 lowestCall = suddenDeathPlayerMove(player, lowestCall, RaisersCallersBetterList);
					 else
					 {
						 playerTurn = "It is now player " + player.getPlayerName() + "'s turn";
					 	 rebuildAndTransitGameFrame();
					 	 
						 PlayersMoveInfo playersMove = getBetInput(player, minimumBet);
						 //rebuildAndTransitGameFrame();
						 
						 if(playersMove.getGameMove() == Player.GameMove.RAISE)
						 {
							 resetOtherPlayerMoves(playerList, player);
							 //resetPlayerMoves(playerList);
							 //System.out.println("raise was selected by player " + player);
							 hadRaised = true;
							 int wager = getWagerFromPlayer(player, playersMove.getPlayerWager());
							 rebuildAndTransitGameFrame();
						 
							 if(player.getStack() == 0)//make it so all in raise or bet commences sudden death right away in a new bettinground call. set a boolean flag
							 //to pass to bettinground.
							 {
								 System.out.println("player " + player.getPlayerName() + " went all in with that raise. commence sudden death round");
								 gameInformation = "player " + player.getPlayerName() + " is ALL IN on that RAISE!!";
								 gamePhase = "SUDDEN DEATH SHOWDOWN";
								 lowestCall = wager;
								 Collections.rotate(playerList, playerList.size()-playerList.indexOf(player));//change the player order so the player after the better goes first
								 bettingRound(playerList, wager, true);
							 }
							 else
							 {
								 Collections.rotate(playerList, playerList.size()-playerList.indexOf(player));//change the player order so the player after the better goes first
								 bettingRound(playerList, wager, false);
							 }
						 }
						 else if(playersMove.getGameMove() == Player.GameMove.CALL)
						 {
							 int playersWager = getMinimumWagerFromPlayer(player, lowestCall);
							 rebuildAndTransitGameFrame();
						 
							 if(player.getStack() == 0)
							 {
								 refundHigherWagersAndCalls(RaisersCallersBetterList, lowestCall, playersWager);
								 System.out.println("player " + player.getPlayerName() + " went all in to call a bet or raise without enough chips. commence sudden death round");
								 gameInformation = "player " + player.getPlayerName() + " is ALL IN on that CALL!";
								 gamePhase = "SUDDEN DEATH SHOWDOWN";
								 rebuildAndTransitGameFrame();
								 lowestCall = playersWager;
								 suddenDeath = true;
							 }
							
							 RaisersCallersBetterList.add(player);
						 }
						 else if(playersMove.getGameMove() == Player.GameMove.FOLD)
							 removePlayerFromRound(player);
					 }
				 
					 if(playersInRound <= 1)
						 throw new OtherPlayersFoldedException();
				 }
			 }
			 
			 if(suddenDeath)
				 throw new SuddenDeathShowdownException();
				 
		}
		
		
		
		public void pokerhand(List<Player> playerList, int minimumBet) throws OtherPlayersFoldedException, SuddenDeathShowdownException
		{
			System.out.println("players in round " + playersInRound + " players in game " + serverRunningGame.numPlayers);
			gameInformation = "ROUND " + roundNumber + ", FIGHT! " + "MINIMUM BET WILL START AT " + minimumBet + "$ ";
			resetPlayerMoves(playerList);
			rebuildAndTransitGameFrame();
			 
			 Iterator<Player> playerListIterator = playerList.listIterator();
			 boolean hasBet = false;
			 
			//iterate through all the players..
			 while(playerListIterator.hasNext())
			 {
				
				 Player player = playerListIterator.next();
				 
				 	if(!hasBet && player.isPlayerInRound())
					 {
				 		playerTurn = "It is now player " + player.getPlayerName() + "'s turn";
				 		rebuildAndTransitGameFrame();
				 		
				 		PlayersMoveInfo playersMove = getpreBetInput(player, minimumBet);
				 
						 if(playersMove.getGameMove() == Player.GameMove.BET)
						 {
							 resetOtherPlayerMoves(playerList, player);
							 //resetPlayerMoves(playerList);
							 hasBet = true;
							 int wager = getWagerFromPlayer(player, playersMove.getPlayerWager());
							 rebuildAndTransitGameFrame();
							 
							 if(player.getStack() == 0)
							 {
								 System.out.println("player " + player.getPlayerName() + " went all in. commence sudden death round");
								 gameInformation = "player " + player.getPlayerName() + " is ALL IN on that bet!";
								 gamePhase = "SUDDEN DEATH SHOWDOWN";
								 Collections.rotate(playerList, playerList.size()-playerList.indexOf(player));//change the player order so the player after the better goes first
								 bettingRound(playerList, wager, true);
							 }
							 else
							 {
								 Collections.rotate(playerList, playerList.size()-playerList.indexOf(player));//change the player order so the player after the better goes first
								 bettingRound(playerList, wager, false);
							 }
						 }
						 else if(playersMove.getGameMove() == Player.GameMove.CHECK)
						 {
							 continue;
						 }
						 else if(playersMove.getGameMove() == Player.GameMove.FOLD)
						 {
							 removePlayerFromRound(player);
						 }
					 }
					 
					 if(playersInRound <= 1)
						 throw new OtherPlayersFoldedException();
			 	}
			 	
		}
		
		public int suddenDeathPlayerMove(Player player,int lowestCall, List<Player> RaisersCallersBetterList)
		{
			playerTurn = "It is now player " + player.getPlayerName() + "'s turn";
	 		rebuildAndTransitGameFrame();
			
			PlayersMoveInfo playersMove = getSuddenDeathBetInput(player, lowestCall);
			 
			 if(playersMove.getGameMove() ==  Player.GameMove.CALL)
			 {
				 //System.out.println("call was selected by player " + player);
				 int playersWager = getMinimumWagerFromPlayer(player, lowestCall);
				 rebuildAndTransitGameFrame();
				 
				 if(player.getStack() == 0)
				 {
					 gameInformation = "player " + player.getPlayerName() + " is ALL IN on that CALL!";
					 refundHigherWagersAndCalls(RaisersCallersBetterList, lowestCall, playersWager);
					 rebuildAndTransitGameFrame();
					 lowestCall = playersWager;
				 }
				
				 RaisersCallersBetterList.add(player);
			 }
			 else if(playersMove.getGameMove() ==  Player.GameMove.FOLD)
				 removePlayerFromRound(player);
			 
			 return lowestCall;
		 }
		
		//-----------------------------------------utility methods-----------------------------------------------------------------
		
		//all players who have the removedFromGame flag == true or players with getStack == 0 are removed() from the playerList
		public void cullPlayerList(List<Player> playerList)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			while(playerListIterator.hasNext())
			{
				Player player = playerListIterator.next();
				
				if(!player.isPlayerInGame())
				{
					System.out.println(player.getPlayerName() + "has disconnected or returned to lobby!");
					startingStacksSum = startingStacksSum - player.getStack();//need to adjust the possible total stack amount for the chips the player is leaving the game with
					playerListIterator.remove();
				}
				else if(player.getStack() == 0)
				{
					System.out.println(player.getPlayerName() + " has " + player.getStack() + " chips! hes out of the game for good! he is now a spectator");
					serverRunningGame.movePlayerToSpectatorList(player);
					playerListIterator.remove();
					declarePlayerLoser(player);
				}
				
				
				
				
				
				
				/*
				if(player.getStack() == 0 || !player.isPlayerInGame())
				{
					System.out.println(player.getPlayerName() + " has " + player.getStack() + " chips! hes out of the game for good! or he failed to pay the blind, or disconnected "
									   + " or returned to lobby!");
					startingStacksSum = startingStacksSum - player.getStack();//need to adjust the possible total stack amount for the chips the player is leaving the game with
					playerListIterator.remove();
				}
				*/
			}
			 //serverRunningGame.movePlayerToSpectatorList(player);
		}
		
		public void declarePlayerLoser(Player player)
		{
			try
			{
				player.getThisPlayersThread().getClient().WriteToClient(new ServerToClientMessage(ServerToClientMessage.Message.DECLARE_LOSER, 
																								  "GG YOU RAN OUT OF CHIPS" + player.getPlayerName()));
			}
			catch(IOException e){e.getStackTrace();}
		}
		
		//this method is a place holder intill i can get mikes code
		public Player evaluatePlayerHands(List<Player> playerList, CommunityCards communityCards)
		{
			
			
			//RETURNS A RANDOM PLAYER IN THE LIST FOR NOW. NOTE THAT THE WINNING PLAYER *MAY NOT* BE IN THE GAME DESPITE STILL BEING IN THE PLAYER LIST
			/*
			int maxPlayerCardSum = 0;
			int communityCardSum = communityCards.getCommunityCardSum();
			
			Iterator<Player> playerListIterator = playerList.listIterator();
			Player winningPlayer = null;
			
			//for each player
			 while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 if(player.isPlayerInRound())
				 {
					 int playerTotalCardSum = communityCardSum;//set initial score to community card sum
					 Iterator<Integer> pocketCardIterator = player.getPocketCards().listIterator();
				 
					 //add each of the pocket cards to the score
					 while(pocketCardIterator.hasNext())
					 playerTotalCardSum = playerTotalCardSum + pocketCardIterator.next();
				 
					 //compare the score to the score of the last players. if its highter, make them the new highest scored thus winning player
					 if(playerTotalCardSum > maxPlayerCardSum)
					 {
						 maxPlayerCardSum = playerTotalCardSum;
						 winningPlayer = player;
					 }
				 
				 }
				 
			 }
			
			 //return winning player. note this algo does NOT split pot in case of ties
			  
			  
			return winningPlayer;
			*/
			
			return playerList.get((int)(Math.random() * playerList.size()));
			
		}
		
		public void validatePotAndStacks()
		{
			Iterator<Player> playerListIterator = testList.listIterator();
			int sum = 0;
			
			 while(playerListIterator.hasNext())
			 {
				
				 Player player = playerListIterator.next();
				 
				sum = sum + player.getStack();
				 
			 }
			 
			 int a = sum + pot;
			 
			 if(a != startingStacksSum)
			 {
				 System.out.println("error! the added player stacks and the pot are not equal to the starting stack amount!");
				 System.out.println("the initial stack sum was " + startingStacksSum);
				 System.out.println("the sum now is " + a);
			 }
			 else
				 System.out.println("pot and stack values add up!");
		}
		
		public Player declareHandWinner(List<Player> playerList)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			Player winningPlayer = null;
			//ASSERT
			if(playersInRound != 1)
				System.out.println("ERROR! more then one winning player in declareHandWinner!");
			 
			 //iterate through all the players and find the winner..
			 while(playerListIterator.hasNext())
			 {
				
				 Player player = playerListIterator.next();
				 
				 if(player.isPlayerInRound())
					 winningPlayer = player;
				 
				 
			 }
			 
			 return winningPlayer;
		}
		
		//make sure to update everyones gui's with the adjusted pot and stacks for everyone aqfter running this
		public void refundHigherWagersAndCalls(List<Player> playerList, int oldBet, int newBet)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			System.out.println("refunding chips to previous betters callers and raisers.");
			
			 while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 int refund = oldBet - newBet;
					player.addChipsToStack(takeChipsFromPot(refund));
					
					System.out.println("just refunded " + player.getPlayerName() + "refund:" + refund + " chips." + " stack: " + player.getStack());
					System.out.println("stack now has " + pot);
					
					//ASSERT
					if(refund < 0)
						System.out.println("ERROR. REFUND WAS NEGATIVE. BUG DETECTED");
					
					 
			 }
			 validatePotAndStacks();
		}
		
		//used at the begining of every gamephase to reset player moves on the GUI
		public void resetPlayerMoves(List<Player> playerList)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
		    while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 if(player.isPlayerInRound())
				 {
					 player.resetLastWager();
					 player.eraseCurrentGameMove();
				 }
			 }
		}
		
		//used to reset all other player moves except the player who just BET or RAISED
		public void resetOtherPlayerMoves(List<Player> playerList, Player otherPlayer)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
		    while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 if(player.isPlayerInRound() && player != otherPlayer)
				 {
					 player.resetLastWager();
					 player.eraseCurrentGameMove();
				 }
			 }
		}
		
		public void initRound(List<Player> playerList, CommunityCards communityCards, Deck deck)
		{
			
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			 while(playerListIterator.hasNext())//for each player
			 {
				 Player player = playerListIterator.next();
				 player.clearPocketCards();//wipe the old pocket cards
				 player.eraseCurrentGameMove();//reset the game move
				 player. getPocketCards(deck);//and get 2 player pocket cards
				 player.addPlayerToRound();//add player back to round
			 }
			 
			 playersInRound = playerList.size();//resize the number of players to account for list removals at the end of the last round
			 communityCards.clearCommunityCards();//wipe all 5 community cards
			 pot = 0;//reset the pot to 0 (although it should already be 0)
		}
		
		public boolean isThereARoundWinner()
		{
			return playersInRound == 1;
		}
		
		//testing methods
		public void printPlayersInGame(List<Player> playerList)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			System.out.println("remaining players:");
			
			 while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 if(player.isPlayerInRound())
				 {
					 System.out.println("Player: " + player.getPlayerName() + " chips: " + player.getStack());
					 player.showPlayerPocketCards();
				 }
			 }
		}
		
		public void removePlayerFromRound(Player player)
		{
			playersInRound--;
			player.removePlayerFromRound();
		}
		
		//need to synchronize this and the currentgameframe getter so the data dosent bug out in the middle of getting it
		public synchronized void rebuildAndTransitGameFrame()
		{
			System.out.println("REBUILDING GAME STATE AND SENDING TO ALL PLAYERS AND SPECTATOTS");
			//printPlayersInGame(playersInThisPokerHand);
			//System.out.println("The community Cards: " + communityCards.getCommunityCards());
			
			//currentGameFrame = new CurrentGameFrame();
			
			
			rebuildGameFrame();
			
			//i think i found the problem. for objects inside the current gameframe, they need to be DEEP COPIED before they can be sent to the client.
			//NEED TO DEEP COPY EVERYTHING AND CREATE A NEW GAMEFRAME WITH A NEW LIST OF PLAYERS WITH A NEW LIST OF CARDS
			ServerToClientMessage updatedGameState = new ServerToClientMessage(ServerToClientMessage.Message.UPDATE_GUI, currentGameFrame);
			
			serverRunningGame.writeToAllThreadsInThisGame(updatedGameState);
		}
		
		synchronized void rebuildGameFrame()
		{
			CurrentGameFrame gameFrameToBuild = new CurrentGameFrame();
			
			/*
			currentGameFrame.setCurrentPot(pot);//copy the pot
			currentGameFrame.setCurrentGamePhase(gamePhase);//copy the game phase
			currentGameFrame.setCommunityCards(new CommunityCards(communityCards));//copy the community card object via copy constructor
			currentGameFrame.setPlayersTurn(playerTurn);
			currentGameFrame.setOtherInformation(gameInformation);
			
			//System.out.println("updating pot and gamephase. they are " + pot + " " + gamePhase + " on the server");
			//System.out.println("they are " + currentGameFrame.getCurrentPot() + " and " + currentGameFrame.getCurrentGamePhase() + " on the server");
			
			ArrayList<PlayerStateInfo> playerStateInfoList =  initPlayerStateInfoList(playersInThisPokerHand);//copy the info for each player
			
			currentGameFrame.setPlayerInfoList(playerStateInfoList);
			*/
			
			gameFrameToBuild.setCurrentPot(pot);//copy the pot
			gameFrameToBuild.setCurrentGamePhase(gamePhase);//copy the game phase
			gameFrameToBuild.setCommunityCards(new CommunityCards(communityCards));//copy the community card object via copy constructor
			gameFrameToBuild.setPlayersTurn(playerTurn);
			gameFrameToBuild.setOtherInformation(gameInformation);
			
			//System.out.println("updating pot and gamephase. they are " + pot + " " + gamePhase + " on the server");
			//System.out.println("they are " + currentGameFrame.getCurrentPot() + " and " + currentGameFrame.getCurrentGamePhase() + " on the server");
			
			ArrayList<PlayerStateInfo> playerStateInfoList =  initPlayerStateInfoList(playersInThisPokerHand);//copy the info for each player
			
			gameFrameToBuild.setPlayerInfoList(playerStateInfoList);
			
			//the reason this wasent working was because List is not a serialized object. arraylist is but i cant cast from a list to an arraylist
			//the solution i think is just make a new wrapper class for all relevent information and getter methods the client needs.
			//run a method to iterate through the list and copy it into a new arraylist that is created everytime this method is called
			//currentGameFrame.setCurrentPlayersInThisPokerHand(playersInThisPokerHand);
			
			currentGameFrame = gameFrameToBuild;
			
		}
		
		public ArrayList<PlayerStateInfo> initPlayerStateInfoList(List<Player> playerList)
		{
			Iterator<Player> playerListIterator = playerList.listIterator();
			
			ArrayList<PlayerStateInfo> playerStateInfoList = new ArrayList<PlayerStateInfo>();
			
			 while(playerListIterator.hasNext())
			 {
				 Player player = playerListIterator.next();
				 
				 PlayerStateInfo playerStateInfo = new PlayerStateInfo(player.getStack(), 
						 											   player.isPlayerInGame(),
						 											   player.isPlayerInRound(),
						 											   player.getPlayerName(),
						 											   player.getPlayerID(),
						 											   player.getLastGameMove(),
						 											   player.getLastWager(),
						 											   player.getPocketCards());
				 
				 playerStateInfoList.add(playerStateInfo);
			 }
			 
			 return playerStateInfoList;
		}
		
		//can also try a manual lock if this bugs out
		public synchronized CurrentGameFrame getCurrentGameFrame()
		{
			return currentGameFrame;
		}
		
		private class GameChecker extends Thread
		{
			ServerRunningGame a;
			
			
			public GameChecker(ServerRunningGame a)
			{
				this.a = a;
			}
			
			public void run()
			{
				System.out.println("the check thread is now running");
				while(true)
				{
					if(serverRunningGame.numPlayers <= 1)
					{
						a.interrupt();
						System.out.println("interupting game thread");
						
						
						String winnerName = "";
						
						try
						{
							winnerName = playersInThisPokerHand.get(0).getPlayerName();
						}
						catch(Exception e){e.getStackTrace();}
						
						System.out.println("Game Over. all other players left the game or ran out of chips! The winner is " + winnerName);
						
						setEndOfGameState(playersInThisPokerHand);
						
						break;
					}
					try {Thread.sleep(2000);} catch (InterruptedException d) {d.printStackTrace();}
				}
			}
		}
		
		
		
}
