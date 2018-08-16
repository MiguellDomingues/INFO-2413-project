import java.io.Serializable;


//pulled out of a clientToserver message when a player makes a playermove. may or may not have a wager. only contains data for playerwager when a player bets or raises.
//does not need it for calls because the server already knows how many chips a player is going to call so it is not needed.
public class PlayersMoveInfo implements Serializable
{
	protected static final long serialVersionUID = 1112122200L;
	Player.GameMove gameMove;
	int playerWager;
	
	public PlayersMoveInfo(Player.GameMove gameMove, int playerWager) 
	{
		this.gameMove = gameMove;
		this.playerWager = playerWager;
	}
	
	public Player.GameMove getGameMove()
	{
		return gameMove;
	}

	public int getPlayerWager() 
	{
		return playerWager;
	}

	
	
	
}
