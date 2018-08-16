import java.io.Serializable;




public class ServerToClientMessage implements Serializable
{
	static public enum Message
	{
		//these are main lobby messages
	    LOGON_DENIED, LOGON_SUCCESS, UPDATE_USERS, CHATMESSAGE, HOST_GAME_SUCCESS, HOST_GAME_DENIED, UPDATE_HOSTED_GAMES, UPDATE_RUNNING_GAMES, SPECTATE_GAME_SUCCESS, 
	    SPECTATE_GAME_DENIED, JOIN_HOSTED_GAME_SUCCESS, JOIN_HOSTED_GAME_DENIED,
	    //------------------------------------------------------------------------------------------------------------------------------------
	    //these hosted lobby messages. includes CHATMESSAGE
	    UPDATE_HOSTED_LOBBY_USERS, ENABLE_GAME_START, HOST_LEFT_lOBBY, HOST_STARTED_GAME, 
	    //------------------------------------------------------------------------------------------------------------------------------------
	    //these are running games messages. includes CHATMESSAGE
	    UPDATE_GUI, TAKE_TURN, UPDATE_PLAYER_USERS, DECLARE_LOSER, LOSE_TURN, UPDATE_SPECTATOR_USERS,
	    //------------------------------------------------------------------------------------------------------------------------------------
	}
	
	
	protected static final long serialVersionUID = 1112122200L;
	Message messageType;
	String message;
	String anotherMessage;
	//need a GUI_UPDATE message which will rebuild the GUI's of all players and spectators in a running game every game move
	PlayerTakeTurnInfo playerTakeTurnInfo;
	CurrentGameFrame currentGameFrame;
	
	//constructor which takes a single message type and single string of data
	public ServerToClientMessage(Message messageType, String message)
	{
		this.messageType = messageType;
		this.message = message;
	}
	
	//overloaded constructor which takes a single message type and two strings of data
	public ServerToClientMessage(Message messageType, String message, String anotherMessage)
	{
		this.messageType = messageType;
		this.message = message;
		this.anotherMessage = anotherMessage;
	}
	
	public ServerToClientMessage(Message messageType, PlayerTakeTurnInfo playerTakeTurnInfo)
	{
		this.messageType = messageType;
		this.playerTakeTurnInfo = playerTakeTurnInfo;
	}
	
	public ServerToClientMessage(Message messageType, CurrentGameFrame currentGameFrame)
	{
		this.messageType = messageType;
		this.currentGameFrame = currentGameFrame;
	}
	
	public PlayerTakeTurnInfo getPlayerTakeTurnInfo()
	{
		return playerTakeTurnInfo;
	}
	
	public CurrentGameFrame getCurrentGameFrame()
	{
		return currentGameFrame;
	}
	
	

	Message getMessageType() 
	{
		return messageType;
	}
		
	String getMessage() 
	{
		return message;
	}
	
	String getAnotherMessage() 
	{
		return anotherMessage;
	}
}
