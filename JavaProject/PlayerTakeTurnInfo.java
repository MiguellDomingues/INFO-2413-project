import java.io.Serializable;


public class PlayerTakeTurnInfo implements Serializable
{
	static public enum PlayerMoveType
	{
		PRE_BET, BET, SUDDEN_DEATH,
	}
	
	protected static final long serialVersionUID = 1112122200L;
	int minimumBet;
	int currentPlayerStack;
	PlayerMoveType playerMoveType;
	
	public PlayerTakeTurnInfo(int minimumBet, int currentPlayerStack, PlayerMoveType playerMoveType)
	{
		this.minimumBet = minimumBet;
		this.currentPlayerStack = currentPlayerStack;
		this.playerMoveType = playerMoveType;
	}

	public int getMinimumBet() 
	{
		return minimumBet;
	}

	public int getCurrentPlayerStack() 
	{
		return currentPlayerStack;
	}

	public PlayerMoveType getPlayerMoveType() 
	{
		return playerMoveType;
	}
	
}
