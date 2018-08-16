import java.io.Serializable;


public class ClientToServerMessage implements Serializable
{
	static public enum Message
	{
		//used in log in screen
	    LOGON, 
	    
	    //used in main lobby
	    LOGOFF, CHATMESSAGE, HOST_GAME_REQUEST, JOIN_HOSTED_GAME, SPECTATE_GAME,
	    
	    //used in a hosted game. includes CHATMESSAGE and LOGOFF
	    RETURN_TO_LOBBY, START_GAME, 
	    
	    //used in a running game. includes CHATMESSAGE, RETURN_TO_LOBBY and LOGOFF
	    GAME_MOVE
	}
	
	protected static final long serialVersionUID = 1112122200L;
	Message messageType;
	String message;
	int numPlayers;//used for host game requests
	int startingMoney;//used for host game requests
	PlayersMoveInfo playersMoveInfo;
	
	public ClientToServerMessage(Message messageType, String message)
	{
		this.messageType = messageType;
		this.message = message;
	}
	
	//used for host game requests
	public ClientToServerMessage(Message messageType, String message, int numPlayers, int startingMoney)
	{
		this.messageType = messageType;
		this.message = message;
		this.numPlayers = numPlayers;
		this.startingMoney = startingMoney;
	}
	
	public ClientToServerMessage(Message messageType, PlayersMoveInfo playersMoveInfo)
	{
		this.messageType = messageType;
		this.playersMoveInfo = playersMoveInfo;
		
	}
	
	public PlayersMoveInfo getPlayersMoveInfo()
	{
		return playersMoveInfo;
	}

	Message getMessageType() 
	{
		return messageType;
	}
		
	String getMessage() 
	{
		return message;
	}
	
	//used for host game requests
	int getNumPlayers()
	{
		return numPlayers;
	}
	
	//used for host game requests
	int getStartingMoney()
	{
		return startingMoney;
	}


}
