import java.util.ArrayList;





public class Player
{
	//max time a player has for there turn, in seconds
	final static int MAX_TURNTIME = 6000;
	
	static public enum GameMove
	{
		BET, CALL, CHECK, RAISE, FOLD,
	}
	
	protected static final long serialVersionUID = 1112122200L;
	
	ArrayList<Card> pocketCards;
	
	ClientThread clientThread;
	int playerStack;
	boolean isInGame;//flag to check if the players is in the game
	boolean isInRound;
	PlayersMoveInfo gameMove; //a players game move. set from the player threads ServerRunningGameState object
	//GameMove gameMove;
	String playerName;
	int playerID;
	int lastWager;
	
	/*
	 * have variables for the players current card hand etc
	 */
	
	

	public Player(ClientThread clientThread, int startingStack, int playerID)
	{
		this.clientThread = clientThread;
		this.playerStack = startingStack;
		isInGame = true;
		isInRound = true;
		gameMove = null;
		pocketCards = new ArrayList<Card>();
		this.playerID = playerID;
		playerName = clientThread.getClient().getClientName();
		lastWager = -1;
		
	}
	
	public int getLastWager() 
	{
		return lastWager;
	}
	
	public void resetLastWager()
	{
		lastWager = -1;
	}

	public void setLastWager(int lastWager)
	{
		this.lastWager = lastWager;
	}
	
	public Player getPocketCards(Deck deck)
	{
		pocketCards.add(deck.drawCard());
		pocketCards.add(deck.drawCard());
		return this;
	}
	
	public void clearPocketCards()
	{
		pocketCards.clear();
	}
	
	
	public ArrayList<Card> getPocketCards()
	{
		return pocketCards;
	}
	
	
	public void showPlayerPocketCards()
	{
		System.out.println(pocketCards);
	}
	
	//check to see if this player is still in the game
	public boolean isPlayerInGame()
	{
		return isInGame;
	}
	
	//if a player loses, returns to lobby, dissconnects, or times out this method will be run
	public void removeFromGame()
	{
		isInGame = false;
		((ServerRunningGameState)clientThread.getCurrentThreadState()).removeThisThreadsPlayer();
		isInRound = false;
		
	}
	
	public ClientThread getThisPlayersThread()
	{
		return clientThread;
	}
	
	//------------------------------------------------------------------------------
	
	/*
	
	public void setGameMove(GameMove gameMove)
	{
		this.gameMove = gameMove;
	}
	
	
	public GameMove getCurrentGameMove()
	{
		//return gameMove;
		return null;
	}
	
	*/
	
	public PlayersMoveInfo getLastGameMove()
	{
		return gameMove;
	}
	
	
	
	public void setGameMove(PlayersMoveInfo gameMove)
	{
		this.gameMove = gameMove;
	}
	
	
	public void eraseCurrentGameMove()
	{
		gameMove = null;
	}
	
	//--------------------------------------------------
	
	//the loop waiting on a players move. will run intil either the player dissconnects/leaves game/MAX_TURNTIME is reached or they input a move
	//returns null when a player times out or leaves game/disconnects
	public PlayersMoveInfo getPlayerMove()
	{
		int currentTurnTime = 0;
		System.out.println("now waiting to get " + playerName + "'s move. checking for a move every 1 second");
		while(isInGame && currentTurnTime <  MAX_TURNTIME)
		{
			if(gameMove != null)
			{
				System.out.println("got a game move. returning out of the loop");
				return gameMove;
			}
			
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			currentTurnTime++;
			//System.out.println("still waiting on that game move");
		}
		
		//return null;
		//if a player times out while the server waits for a move the player automatically folds. note there is no code to disable to player GUI
		return new PlayersMoveInfo(Player.GameMove.FOLD, 0);
	}
	
	public String getPlayerName()
	{
		return playerName;
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
	
	public void takeChipsFromStack(int amount)
	{
		playerStack = playerStack - amount;
		
		
	}
	
	public void addChipsToStack(int amount)
	{
		playerStack = playerStack + amount;
	}
	
	public int getStack()
	{
		return playerStack;
	}
	
	public String toString()
	{
		return playerName + " stack: " + playerStack + " player ID: " + playerID;
	}
	
	public int getPlayerID()
	{
		return playerID;
	}

	
	
	
}
