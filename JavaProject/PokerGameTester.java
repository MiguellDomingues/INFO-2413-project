import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class PokerGameTester 
{
	static int pot = 0;
	static int playersInRound;
	
	
	static int startingStacksSum;
	static List<TestPlayer> testList;
	
	static List<Integer> communityCards;
	
	public static final int MAX_COMMUNITY_CARDS = 5;
	public static final int STARTING_BLIND = 20;
	public static final int STARTING_STACK = 1000;
	public static final double MIN_WAGER_INCREASE = .1;//a raise has to be at least 10% higher then the last wager
	
	
	
	static public enum GameMove
	{
		BET, CALL, CHECK, RAISE, FOLD,
	}
	
	//-----------------community card methods----------------------------------
	
	public static void getTheFlop()
	{
		communityCards.add((int)(Math.random() * 10 + 1));
		communityCards.add((int)(Math.random() * 10 + 1));
		communityCards.add((int)(Math.random() * 10 + 1));
	}
	
	public static void getTheRiver()
	{
		communityCards.add((int)(Math.random() * 10 + 1));
	}
	
	public static void getTheTurn()
	{
		communityCards.add((int)(Math.random() * 10 + 1));
	}
	
	public static void clearCommunityCards()
	{
		communityCards.clear();
	}
	
	public static List<Integer> getCommunityCards()
	{
		return communityCards;
	}
	
	public static void getAllCommunityCards()
	{
		int currentCommunityCards = communityCards.size();
		
		for(int i = 0; i < MAX_COMMUNITY_CARDS-currentCommunityCards; i++)
			communityCards.add((int)(Math.random() * 10 + 1));
		
		//ASSERT
		if(communityCards.size() != 5)
			System.out.println("ERROR! COMMUNITY CARDS ARE NOT 5 AT SUDDEN DEATH ROUND, THEY ARE " + communityCards.size());
	}
	
	public static int getCommunityCardSum()
	{
		Iterator<Integer> communityCardIterator = communityCards.listIterator();
		
		int sum = 0;
		
		 while(communityCardIterator.hasNext())
			 sum =+ communityCardIterator.next();
			 
		return sum;
		 
	}
	
	//----------------------------------methods for manipulating the pot--------------------------------------------------
	
	public static int takeChipsFromPot(int amount)
	{
		pot = pot - amount;
		
		if(pot < 0)
			System.out.println("ERROR. POT IS NEGATIVE");
		
		return amount;
	}
	
	public static int emptyOutPot()
	{
		int total;
		total = pot;
		pot = 0;
		return total;
	}
	
	public static void addChipsToPot(int chipAmount)
	{
		pot = pot + chipAmount;
	}
	
	//------------------------------------------------------------------------------------------------
	
	public static void getpreBetInput(TestPlayer player, int minimumBet)
	{
	
		
		Scanner keyboard = new Scanner(System.in);
		String move;
		while(true)
		{
			System.out.println("enter an move for player " + player + ". you can c for check, f for fold and b for bet");
			move = keyboard.nextLine();
			
			if(move.equals("b") && player.getStack() < minimumBet)
				System.out.println("you cannot bet without more chips then the blind. you can only check or fold");
			else if(move.equals("b") && player.getStack() > minimumBet)
			{
				System.out.println("player is betting");
				player.setGameMove(GameMove.BET);
				break;
			}
			else if(move.equals("b") && player.getStack() == minimumBet)
			{
				System.out.println("player is going all in with the bet");
				player.setGameMove(GameMove.BET);
				break;
			}
			else if(move.equals("c"))
			{
				System.out.println("player is checking");
				player.setGameMove(GameMove.CHECK);
				break;
			}
			else if(move.equals("f"))
			{
				System.out.println("player folded");
				player.setGameMove(GameMove.FOLD);
				break;
			}
			else
				System.out.println("inproper input");
				
				
			
		}
		
		if(player.getLastGameMove() == null)
			System.out.println("ERROR IN GETTING GAME MOVE. IT SHOULD NOT BE NULL");
	}
	
	
	
	public static void printPlayersInGame(List<TestPlayer> playerList)
	{
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
		System.out.println("remaining players:");
		
		 while(playerListIterator.hasNext())
		 {
			 TestPlayer player = playerListIterator.next();
			 
			 if(player.isPlayerInRound())
			 {
				 System.out.println("Player: " + player.getName() + " chips: " + player.getStack());
				 player.showPlayerPocketCards();
			 }
		 }
	}
	
	public static void firstPlayerAddBlindToPot(List<TestPlayer> playerList, int blind)
	{
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
		 while(playerListIterator.hasNext())
		 {
			 TestPlayer player = playerListIterator.next();
			 
			 if(player.isPlayerInRound())
			 {
				 if(player.getStack() < blind)
				 {
						System.out.println(player.getName() + " cant meet the blind! hes out of the game!");
						removePlayerFromRound(player);
						player.removeFromGame();
						//need to add code to remove the player from the game so they can be culled at the end of the hand
				 }
				 else
				 {
					 player.takeChipsFromStack(blind);
					 
					 System.out.println(player.getName() + " just added a blind of " + blind);
					 addChipsToPot(blind);
					 return;
				 }
			 }
				 
	}
		
		
		
		
		
		
	}
	
	public static boolean isThereARoundWinner()
	{
		return playersInRound == 1;
	}
	
	static void initRound(List<TestPlayer> playerList)
	{
		
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
		 while(playerListIterator.hasNext())//for each player
		 {
			 TestPlayer player = playerListIterator.next();
			 player.clearPocketCards();//wipe the old pocket cards
			 player.resetGameMove();//reset the game move
			 player.initPlayerPocketCards();//and get 2 player pocket cards
			 player.addPlayerToRound();//add player back to round
		 }
		 
		 playersInRound = playerList.size();//resize the number of players to account for list removals at the end of the last round
		 clearCommunityCards();//wipe all 5 community cards
		 pot = 0;//reset the pot to 0 (although it should already be 0)
	}
	
	static void removePlayerFromRound(TestPlayer player)
	{
		playersInRound--;
		player.removePlayerFromRound();
	}
	
	public static int getMinimumWagerFromPlayer(TestPlayer player, int minimumWager)
	{
		if(minimumWager >= player.getStack())//this code needs to be in the betting round loop because the player cant meet the call. they have to all in or fold.
		{
			System.out.println("player cant meet the minimum bet. hes all in with " + player.getStack());
			minimumWager = player.getStack();
		}
		else
		{
			System.out.println("player " + player.getName() + " has called the minimum bet of " + minimumWager);
		}
		
		player.takeChipsFromStack(minimumWager);
		
		addChipsToPot(minimumWager);
		System.out.println("just took " + minimumWager + " from stack and added " + minimumWager + " to pot. now has " + player.getStack());
		System.out.println("the pot now has " + pot); 
		
		return minimumWager;
	}
	
	public static int getWagerFromPlayer(TestPlayer player, int minimumWager)
	{
		minimumWager = minimumWager + (int)(minimumWager*MIN_WAGER_INCREASE);
		
		Scanner keyboard = new Scanner(System.in);
		int wager;
		while(true)
		{
			System.out.println("enter a wager for player:" + player.getName() + ". the minimum bet is " + minimumWager);
			System.out.println("they have " + player.getStack() + " chips remaining");
			wager = keyboard.nextInt();
			
			if(wager < minimumWager)
				System.out.println("bet is below the raise or blind minimum. Please re-enter");
			else if(wager > player.getStack())
				System.out.println("you dont have that many chips! Please re-enter");
			else if(wager == player.getStack())
			{
				System.out.println("ALL IN BABY");
				break;
			}
			else
				break;
		}
		
		player.takeChipsFromStack(wager);
		addChipsToPot(wager);
		
		System.out.println("just took " + wager + " from stack and added " + wager + " to pot. player has " + player.getStack());
		System.out.println("the pot now has " + pot); 
		
		return wager;
	}
	
	public static void getBetInput(TestPlayer player, int minimumBet)
	{
		Scanner keyboard = new Scanner(System.in);
		String move;
		while(true)
		{
			System.out.println("enter an move for player " + player + ". you can c for call, f for fold and r for raise");
			move = keyboard.nextLine();
			
			if(move.equals("r") && player.getStack() <= minimumBet)
				System.out.println("you cannot raise without more chips then the minimum bet. you can only call");
			else if(move.equals("r") && player.getStack() > minimumBet)
			{
				System.out.println("player is raising");
				player.setGameMove(GameMove.RAISE);
				break;
			}
			
			else if(move.equals("c") && player.getStack() == minimumBet)
			{
				System.out.println("player is going all in with the call");
				player.setGameMove(GameMove.CALL);
				break;
			}
			else if(move.equals("c") && player.getStack() < minimumBet)
			{
				System.out.println("player cant cover the entire bet with the raise!");
				player.setGameMove(GameMove.CALL);
				break;
			}
			else if(move.equals("c") && player.getStack() > minimumBet)
			{
				System.out.println("player covered the bet or raise with a call");
				player.setGameMove(GameMove.CALL);
				break;
			}
			else if(move.equals("f"))
			{
				System.out.println("player folded");
				player.setGameMove(GameMove.FOLD);
				break;
			}
			else
				System.out.println("inproper input");
		}
			
		if(player.getLastGameMove() == null)
			System.out.println("ERROR IN GETTING GAME MOVE. IT SHOULD NOT BE NULL");
	    
	}
	
	public static void getSuddenDeathBetInput(TestPlayer player, int allInAmount)
	{
		Scanner keyboard = new Scanner(System.in);
		String move;
		while(true)
		{
			System.out.println("enter an move for player " + player.getName() + ". you can c for call, or f to fold");
			move = keyboard.nextLine();
			
			if(move.equals("c") && player.getStack() == allInAmount)
			{
				System.out.println("player is going all in with the call");
				player.setGameMove(GameMove.CALL);
				break;
			}
			else if(move.equals("c") && player.getStack() < allInAmount)
			{
				System.out.println("player cant cover the entire bet!");
				player.setGameMove(GameMove.CALL);
				break;
			}
			else if(move.equals("c") && player.getStack() > allInAmount)
			{
				System.out.println("player covered the entire bet!");
				player.setGameMove(GameMove.CALL);
				break;
			}
			else if(move.equals("f"))
			{
				System.out.println("player folded");
				player.setGameMove(GameMove.FOLD);
				break;
			}
			else
				System.out.println("bad input");
		}
		
		if(player.getLastGameMove() == null)
			System.out.println("ERROR IN GETTING GAME MOVE. IT SHOULD NOT BE NULL");
	}
	
	public static void resetPlayerMoves(List<TestPlayer> playerList)
	{
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
	    while(playerListIterator.hasNext())
		 {
			 TestPlayer player = playerListIterator.next();
			 
			 if(player.isPlayerInRound())
				 player.resetGameMove();
		 }
	}
	
	public static void refundHigherWagersAndCalls(List<TestPlayer> playerList, int oldBet, int newBet)
	{
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		System.out.println("refunding chips to previous betters callers and raisers.");
		
		 while(playerListIterator.hasNext())
		 {
			 TestPlayer player = playerListIterator.next();
			 
		
				 //System.out.println("attempting to refund player " + player.getName() + " his previous move was a " + player.getLastGameMove().toString());
			
				 
				 /*
				 System.out.println("player " + player.getName() + " last wager was" + player.getLastWager());
				 int refund = player.getLastWager() - newBet;
				 player.setLastWager(newBet);
				 System.out.println("his new lowest wager is" + player.getLastWager());
				 //System.out.println("the new bet was " + newBet);
				// System.out.println("the new bet was " + newBet);
				 */
				int refund = oldBet - newBet;
				//pot = pot - refund;
			
				player.addChipsToStack(takeChipsFromPot(refund));
				//player.addChipsToStack(refund);
				System.out.println("just refunded " + player.getName() + "refund:" + refund + " chips." + " stack: " + player.getStack());
				System.out.println("stack now has " + pot);
				
				//ASSERT
				if(refund < 0)
					System.out.println("ERROR. REFUND WAS NEGATIVE. BUG DETECTED");
				
				 
		 }
		 validatePotAndStacks();
	}
	
	public static void bettingRound(List<TestPlayer> playerList, int minimumBet, boolean suddenDeath) throws OtherPlayersFoldedException, SuddenDeathShowdownException
	{
		System.out.println("-------------STARTING A NEW BETTING ROUND---------------------------");
		
		List<TestPlayer> RaisersCallersBetterList = new ArrayList<TestPlayer>();
		
		boolean hadRaised = false;
	
		int lowestCall = minimumBet;
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
		RaisersCallersBetterList.add(playerListIterator.next());
		
		while(playerListIterator.hasNext())
		{
			 TestPlayer player = playerListIterator.next();
			 
			 if(!hadRaised && player.isPlayerInRound())
			 {
				 if(suddenDeath)
					 lowestCall = suddenDeathPlayerMove(player, lowestCall, RaisersCallersBetterList);
				 else
				 {
					 getBetInput(player, minimumBet);
					 if(player.getLastGameMove() == GameMove.RAISE)
					 {
						 //System.out.println("raise was selected by player " + player);
						 hadRaised = true;
						 int wager = getWagerFromPlayer(player, minimumBet);
					 
						 if(player.getStack() == 0)//make it so all in raise or bet commences sudden death right away in a new bettinground call. set a boolean flag
						 //to pass to bettinground.
						 {
							 System.out.println("player " + player.getName() + " went all in with that raise. commence sudden death round");
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
					 else if(player.getLastGameMove() == GameMove.CALL)
					 {
						 int playersWager = getMinimumWagerFromPlayer(player, lowestCall);
					 
						 if(player.getStack() == 0)
						 {
							 refundHigherWagersAndCalls(RaisersCallersBetterList, lowestCall, playersWager);
							 System.out.println("player " + player.getName() + " went all in to call a bet or raise without enough chips. commence sudden death round");
							 lowestCall = playersWager;
							 suddenDeath = true;
						 }
						
						 RaisersCallersBetterList.add(player);
					 }
					 else if(player.getLastGameMove() == GameMove.FOLD)
						 removePlayerFromRound(player);
				 }
			 
				 if(playersInRound == 1)
					 throw new OtherPlayersFoldedException();
			 }
		 }
		 
		 if(suddenDeath)
			 throw new SuddenDeathShowdownException();
	}
	
	public static void pokerhand(List<TestPlayer> playerList, int minimumBet) throws OtherPlayersFoldedException, SuddenDeathShowdownException
	{
		resetPlayerMoves(playerList);
		 
		 Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		 boolean hasBet = false;
		 
		//iterate through all the players..
		 while(playerListIterator.hasNext())
		 {
			
			 TestPlayer player = playerListIterator.next();
			 
			 	if(!hasBet && player.isPlayerInRound())
				 {
			 		getpreBetInput(player, minimumBet);
			 
					 if(player.getLastGameMove() == GameMove.BET)
					 {
						 hasBet = true;
						 int wager = getWagerFromPlayer(player, minimumBet);
						 
						 if(player.getStack() == 0)
						 {
							 System.out.println("player " + player.getName() + " went all in. commence sudden death round");
							 Collections.rotate(playerList, playerList.size()-playerList.indexOf(player));//change the player order so the player after the better goes first
							 bettingRound(playerList, wager, true);
						 }
						 else
						 {
							 Collections.rotate(playerList, playerList.size()-playerList.indexOf(player));//change the player order so the player after the better goes first
							 bettingRound(playerList, wager, false);
						 }
					 }
					 else if(player.getLastGameMove() == GameMove.CHECK)
					 {
						 continue;
					 }
					 else if(player.getLastGameMove() == GameMove.FOLD)
					 {
						 removePlayerFromRound(player);
					 }
				 }
				 
				 if(playersInRound == 1)
					 throw new OtherPlayersFoldedException();
		 	}
	}
	
	public static int suddenDeathPlayerMove(TestPlayer player,int lowestCall, List<TestPlayer> RaisersCallersBetterList)
	{
		 getSuddenDeathBetInput(player, lowestCall);
		 
		 if(player.getLastGameMove() == GameMove.CALL)
		 {
			 //System.out.println("call was selected by player " + player);
			 int playersWager = getMinimumWagerFromPlayer(player, lowestCall);
			 
			 if(player.getStack() == 0)
			 {
				 refundHigherWagersAndCalls(RaisersCallersBetterList, lowestCall, playersWager);
				 lowestCall = playersWager;
			 }
			
			 RaisersCallersBetterList.add(player);
		 }
		 else if(player.getLastGameMove() == GameMove.FOLD)
			 removePlayerFromRound(player);
		 
		 return lowestCall;
	 }
	
	
	
	public static TestPlayer declareHandWinner(List<TestPlayer> playerList)
	{
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
		TestPlayer winningPlayer = null;
		//ASSERT
		if(playersInRound != 1)
			System.out.println("ERROR! more then one winning player in declareHandWinner!");
		 
		 //iterate through all the players and find the winner..
		 while(playerListIterator.hasNext())
		 {
			
			 TestPlayer player = playerListIterator.next();
			 
			 if(player.isPlayerInRound())
				 winningPlayer = player;
			 
			 
		 }
		 
		 return winningPlayer;
	}
	
	
	
	public static void validatePotAndStacks()
	{
		Iterator<TestPlayer> playerListIterator = testList.listIterator();
		int sum = 0;
		
		 while(playerListIterator.hasNext())
		 {
			
			 TestPlayer player = playerListIterator.next();
			 
			sum = sum + player.getStack();
			 
		 }
		 
		 int a = sum + pot;
		 
		 if(a != startingStacksSum)
			 System.out.println("error! the added player stacks and the pot are not equal to the starting stack amount!");
		 else
			 System.out.println("pot and stack values add up!");
	}
	
	public static TestPlayer evaluatePlayerHands(List<TestPlayer> playerList, List<Integer> communityCards)
	{
		int maxPlayerCardSum = 0;
		int communityCardSum = getCommunityCardSum();
		
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		TestPlayer winningPlayer = null;
		
		//for each player
		 while(playerListIterator.hasNext())
		 {
			 TestPlayer player = playerListIterator.next();
			 
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
	}
	
	//all players who have the removedFromGame flag == true or players with getStack == 0 are removed() from the playerList
	public static void cullPlayerList(List<TestPlayer> playerList)
	{
		Iterator<TestPlayer> playerListIterator = playerList.listIterator();
		
		while(playerListIterator.hasNext())
		{
			TestPlayer player = playerListIterator.next();
			
			if(player.getStack() == 0 || !player.isPlayerInGame())
			{
				System.out.println(player.getName() + " has " + player.getStack() + " chips! hes out of the game for good! or he failed to pay the blind, or disconnected "
								   + " or returned to lobby!");
				startingStacksSum = startingStacksSum - player.getStack();//need to adjust the possible total stack amount for the chips the player is leaving the game with
				playerListIterator.remove();
			}
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		TestGUI a = new TestGUI(null,null,null,null);
		a.setVisible(true);
		
		//Deck deck = new Deck();
		
		//while(true)
		//{
		//	Card card = deck.drawCard();
			
			//if(card != null)
			//	System.out.println(card.toString());
			//else
				//break;
		//}
		
		//System.out.println(deck.a());
		
		//URL url = new URL("http://checkip.amazonaws.com/");
		//BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		//System.out.println(br.readLine());
		// System.out.println(InetAddress.getLocalHost()); 
		
		
		/*
		List<Player> playerList = new ArrayList<Player>();
		
		playerList.add(new Player(null, STARTING_STACK,1));
		playerList.add(new Player(null, STARTING_STACK,4));
		playerList.add(new Player(null, STARTING_STACK,3));
		playerList.add(new Player(null, STARTING_STACK,2));
		
		System.out.println("this is the inital player list:" + playerList);
		
		
		
		
		//CurrentGameFrame a = new CurrentGameFrame(playerList, 0, "");
		
		List<Player> playerList2 = new ArrayList<Player>();
		
		//playerList2 = a.getPlayerList();
		
		//Collections.sort(playerList);
		
		System.out.println("this is the inital player list:" + playerList);
		System.out.println("this is the DEEP COPY player list:" + playerList2);
		
		playerList2.get(0).addChipsToStack(500);
		
		System.out.println("this is the inital player list:" + playerList);
		System.out.println("this is the DEEP COPY player list:" + playerList2);
		
		
		
		int blind;
		
		int roundNumber = 0;
		
		
		List<TestPlayer> playerList = new ArrayList<TestPlayer>();
		communityCards = new ArrayList<Integer>();
		
		testList = playerList;
		PokerGameTester a = new PokerGameTester();
		
		playerList.add(a.new TestPlayer("1", STARTING_STACK));
		playerList.add(a.new TestPlayer("2", STARTING_STACK));
		playerList.add(a.new TestPlayer("3", STARTING_STACK));
		playerList.add(a.new TestPlayer("4", STARTING_STACK));
		
		startingStacksSum = STARTING_STACK*playerList.size();
		
		while(playerList.size() > 1)
		{
			roundNumber++;
			blind = STARTING_BLIND*roundNumber;//blinds go up every hand by round number x starting blind
			System.out.println("--------------------------------------starting a new hand-------------------------------------------");
			System.out.println("initing players. reseting pot. wiping community cards");
			System.out.println("the blind for round number " + roundNumber + " will be " + blind);
			try
			{
				initRound(playerList);
				printPlayersInGame(playerList);
				
			
				firstPlayerAddBlindToPot(playerList, blind);
			
				//preflop betting round
				pokerhand(playerList, blind);
				System.out.println("end of preflop betting round");
			
				printPlayersInGame(playerList);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				System.out.println("-------------------------the flop! start of flop betting round---------------------------------------");
				getTheFlop();
				pokerhand(playerList, blind);
				System.out.println("end of flop betting round");
			
				printPlayersInGame(playerList);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				System.out.println("-------------------------the river! start of turn betting round-------------------------");
				getTheRiver();
				pokerhand(playerList, blind);
				System.out.println("end of river betting round");
			
				printPlayersInGame(playerList);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				System.out.println("-------------------------the turn! start of river betting round-------------------------");
				getTheTurn();
				pokerhand(playerList, blind);
				System.out.println("end of turn betting round");
			
				printPlayersInGame(playerList);
				validatePotAndStacks();
				System.out.println("the pot is " + pot);
		
				System.out.println("the showdown! compare all player hands and declare a winner");
			
				printPlayersInGame(playerList);
				System.out.println("the pot is " + pot);
				validatePotAndStacks();
				
				throw new SuddenDeathShowdownException();
			
			}
			catch(OtherPlayersFoldedException e)
			{
				//run some code to reward the remaining player stillInGame the pot
				System.out.println("other players folded");
				System.out.println("reward the remaining player the pot");
				TestPlayer winner = declareHandWinner(playerList);
				
				System.out.println("The winner is " + winner.getName() + ". he won the pot of " + pot);
				winner.addChipsToStack(emptyOutPot());
				System.out.println("he now has " + winner.getStack());
				validatePotAndStacks();
			}
			catch(SuddenDeathShowdownException b)
			{
				System.out.println("making sure all community cards are in play");
				getAllCommunityCards();
				System.out.println("show all player hands and evaluate them. return a winning player");
				TestPlayer winner = evaluatePlayerHands(playerList, communityCards);
				//System.out.println("increments the winning player chips by the pot");
				System.out.println("The winner is " + winner.getName() + ". he won the pot of " + pot);
				winner.addChipsToStack(emptyOutPot());
				System.out.println("he now has " + winner.getStack());
				validatePotAndStacks();
				
			}
			
			//at the end of every hand, players who have 0 chips or left game/disconnected (isInGame == false)
			//System.out.println("remove all removedFromGame and 0 chip players from list, or do it outside the loop");
			cullPlayerList(playerList);
			
			
		}
		
		System.out.println("Game Over. all other players left the game or ran out of chips! The winner is " + playerList.get(0));
		validatePotAndStacks();
		*/
		
	}
	
		private class TestPlayer
		{
			String name;
			boolean isInRound;
			int stack;
			List<Integer> pocketCards;
			boolean isInGame;
			GameMove lastGameMove;
			
			public TestPlayer(String name, int startingStack)
			{
				this.name = name;
				isInRound = true;
				this.stack = startingStack;
				lastGameMove = null;
				pocketCards = new ArrayList<Integer>();
				isInGame = true;
				
			}
			
			public boolean isPlayerInGame()
			{
				return isInGame;
			}
			
			public void removeFromGame()
			{
				isInGame = false;
				//((ServerRunningGameState)clientThread.getCurrentThreadState()).removeThisThreadsPlayer();
			}
			
			public TestPlayer initPlayerPocketCards()
			{
				pocketCards.add((int)(Math.random() * 10 + 1));
				pocketCards.add((int)(Math.random() * 10 + 1));
				return this;
			}
			
			public void showPlayerPocketCards()
			{
				System.out.println(pocketCards);
			}
			
			public void clearPocketCards()
			{
				pocketCards.clear();
			}
			
			public List<Integer> getPocketCards()
			{
				return pocketCards;
			}
			
			public void setGameMove(GameMove gameMove)
			{
				lastGameMove = gameMove;
			}
			
			public GameMove getLastGameMove()
			{
				return lastGameMove;
			}
			
			public void resetGameMove()
			{
				lastGameMove = null;
			}
			
			public boolean isPlayerInRound()
			{
				return isInRound;
			}
			
			public void removePlayerFromRound()
			{
				isInRound = false;
			}
			
			public void addPlayerToRound()
			{
				isInRound = true;
			}
			
			public String getName()
			{
				return name;
			}
			
			public int getStack()
			{
				return stack;
			}
			
			public String toString()
			{
				return name + " stack: " + stack;
			}
			
			public void takeChipsFromStack(int amount)
			{
				stack = stack - amount;
				
				
			}
			
			public void addChipsToStack(int amount)
			{
				stack = stack + amount;
			}
			
		}
}
	

