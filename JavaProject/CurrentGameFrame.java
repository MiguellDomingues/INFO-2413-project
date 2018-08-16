import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//NOTE THAT PLAYERS IS A SHALLOW COPY AND MAY "NOT" WORK WHEN THE CLIENT AND SERVER ARE RUNNING ON DIFFERENT MACHINES
//NEED TO MANUALLY DEEP COPY NEW PLAYER OBJECTS. CREATE A COPY METHOD WHICH TAKES A LIST AND CREATES NEW() PLAYER OBJECTS WITH PLAYER STACK, NAME, AND LAST GAME MOVE
//NEED TO DEEP COPY THE CARDS AS WELL
public class CurrentGameFrame implements Serializable
{
	protected static final long serialVersionUID = 1112122200L;
	
	int currentPot;
	String currentGamePhase;
	ArrayList<PlayerStateInfo> currentPlayersInThisPokerHand;
	//Player[] currentPlayersInThisPokerHand;
	CommunityCards currentCommunityCards;
	String playerTurn;
	String otherInformation;
	

	

	

	public CurrentGameFrame(ArrayList<Player> currentPlayersInThisPokerHand, int currentPot, String currentGamePhase)
	{
		//DEEP COPY the current player list into the game frame
		//this.currentPlayersInThisPokerHand = new ArrayList<Player>();
		//this.currentPlayersInThisPokerHand = currentPlayersInThisPokerHand;
		//Collections.copy(this.currentPlayersInThisPokerHand, currentPlayersInThisPokerHand);
		//this.currentPlayersInThisPokerHand.addAll(currentPlayersInThisPokerHand);
		//this.currentPlayersInThisPokerHand = currentPlayersInThisPokerHand.toArray(new Player[currentPlayersInThisPokerHand.size()]);
		this.currentPot = currentPot;
		this.currentGamePhase = currentGamePhase;
	}
	
	public CurrentGameFrame()
	{
		
	}
	
	public String getPlayersTurn() 
	{
		return playerTurn;
	}

	public void setPlayersTurn(String playerTurn) 
	{
		this.playerTurn = playerTurn;
	}
	
	
	
	
	public ArrayList<PlayerStateInfo> getPlayerInfoList()
	{
		return currentPlayersInThisPokerHand;
		//return null;
	}
	
	public CommunityCards getCommunityCards()
	{
		return currentCommunityCards;
	}
	
	public void setCommunityCards(CommunityCards communityCards)
	{
		currentCommunityCards = communityCards;
	}
	
	public void setPlayerInfoList(ArrayList<PlayerStateInfo> currentPlayersInThisPokerHand)
	{
		this.currentPlayersInThisPokerHand = currentPlayersInThisPokerHand;
		//this.currentPlayersInThisPokerHand = currentPlayersInThisPokerHand.toArray(new Player[currentPlayersInThisPokerHand.size()]);
		//wipe the list and readd all elements
		//this.currentPlayersInThisPokerHand.clear();
		//Collections.copy(this.currentPlayersInThisPokerHand, currentPlayersInThisPokerHand);
		//this.currentPlayersInThisPokerHand.addAll(currentPlayersInThisPokerHand);
		//this.currentPlayersInThisPokerHand = new ArrayList<PlayerStateInfo>(currentPlayersInThisPokerHand);
	}
	
	public void setCurrentPot(int currentPot)
	{
		this.currentPot = currentPot;
	}
	
	public int getCurrentPot()
	{
		return currentPot;
	}
	
	public void setCurrentGamePhase(String currentGamePhase)
	{
		this.currentGamePhase = currentGamePhase;
	}
	
	

	
	/*
	public List<Integer> getCurrentCommunityCards() 
	{
		return currentCommunityCards;
	}

	public void setCurrentCommunityCards(List<Integer> currentCommunityCards) 
	{
		this.currentCommunityCards = currentCommunityCards;
	}
*/
	public String getCurrentGamePhase() 
	{
		return currentGamePhase;
	}
	
	public String getOtherInformation() 
	{
		return otherInformation;
	}

	public void setOtherInformation(String otherInformation) 
	{
		this.otherInformation = otherInformation;
	}
	
	
	
	
	

}
