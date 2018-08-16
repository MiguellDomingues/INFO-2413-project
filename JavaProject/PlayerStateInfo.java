import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


public class PlayerStateInfo implements Serializable, Comparable<PlayerStateInfo>
{
	//this class will contain all data for a player that a client needs to update it
	int playerStack;
	
	boolean isInGame;//flag to check if the players is in the game
	boolean isInRound;
	
	String playerName;
	int playerID;
	
	//need a feild for arraylist of cards;
	//need feild for last game move
	PlayersMoveInfo playerMoveInfo;
	//need feild for last amount of chips bid
	int lastWager;
	
	ArrayList<Card> pocketCards;
	
	
	
	


	public PlayerStateInfo(int playerStack, 
						   boolean isInGame,
						   boolean isInRound, 
						   String playerName, 
						   int playerID,
						   PlayersMoveInfo playerMoveInfo,
						   int lastWager,
						   ArrayList<Card> pocketCards)
	{
		
		this.playerStack = playerStack;
		this.isInGame = isInGame;
		this.isInRound = isInRound;
		this.playerName = playerName;
		this.playerID = playerID;
		this.playerMoveInfo = playerMoveInfo;
		this.lastWager = lastWager;
		this.pocketCards = deepCopyPocketCards(pocketCards);
		
	}
	public boolean isInGame()
	{
		return isInGame;
	}
	public boolean isInRound() 
	{
		return isInRound;
	}
	
	public PlayersMoveInfo getPlayersMoveInfo()
	{
		return playerMoveInfo;
	}
	
	public String getPlayerName() 
	{
		return playerName;
	}
	public int getPlayerID() 
	{
		return playerID;
	}
	public int getPlayerStack() 
	{
		return playerStack;
	}
	public int getLastWager() 
	{
		return lastWager;
	}
	
	public ArrayList<Card> getPocketCards() 
	{
		return pocketCards;
	}
	
	public String toString()
	{
		return pocketCards.toString();
	}
	
	public ArrayList<Card> deepCopyPocketCards(ArrayList<Card> sourceCopy)
	{
		pocketCards = new  ArrayList<Card>();
		
		Iterator<Card> pocketCardIterator = sourceCopy.listIterator();
		
		
		
		 while(pocketCardIterator.hasNext())
		 {
			 
			 pocketCards.add(new Card(pocketCardIterator.next()));
			 //pocketCardIterator.next().g;
		 }
		 
		 return pocketCards;
	}
	
	@Override
	public int compareTo(PlayerStateInfo otherPlayer) 
	{
		 int comparePlayerID = ((PlayerStateInfo)otherPlayer).getPlayerID();
	        /* For Ascending order*/
	        return this.getPlayerID()-comparePlayerID;
		
	}
}
