import java.io.IOException;

import javax.swing.JOptionPane;


public class ClientMainLobbyState extends ClientState
{
	//takes a GUI object to update and write to GUI accordingly
	 MainLobbyGUI clientGUI;
	 MessageListenerThread messageListenerThread;
	 HostedLobbyGUI hostedGameGUI;
     
	 
	 public ClientMainLobbyState(MainLobbyGUI clientGUI, MessageListenerThread messageListenerThread)
	 {
		 this.clientGUI = clientGUI;
		 this.messageListenerThread = messageListenerThread;
	 }
	
	public void readMessage(ServerToClientMessage message) throws IOException
	{
		//add the switch for reading server messages for the main lobby. have the code for writing to all clients and updating the current users list
		//throw new IOException("");
		switch(message.getMessageType())
		{
			case CHATMESSAGE:
			{
				processCHATMESSAGE(message);
				break;
			}
			case LOGON_SUCCESS:
			{
				processLOGON_SUCCESS(message);
				break;
			}
			case LOGON_DENIED:
			{
				processLOGON_DENIED(message);
				break;
			}
			case UPDATE_USERS:
			{
				processUPDATE_USERS(message);
				break;
			}
			case UPDATE_HOSTED_GAMES:
			{
				processUPDATE_HOSTED_GAMES(message);
				break;
			}
			case HOST_GAME_SUCCESS:
			{
				processHOST_GAME_SUCCESS(message);
				break;
			}
			case HOST_GAME_DENIED:
			{
				processHOST_GAME_DENIED(message);
				break;
			}
			case JOIN_HOSTED_GAME_DENIED:
			{
				processJOIN_HOSTED_GAME_DENIED(message);
				break;
			}
			case JOIN_HOSTED_GAME_SUCCESS:
			{
				processJOIN_HOSTED_GAME_SUCCESS(message);
				break;
			}
			case UPDATE_RUNNING_GAMES:
			{
				processUPDATE_RUNNING_GAMES(message);
				break;
			}
			case SPECTATE_GAME_DENIED:
			{
				processSPECTATE_GAME_DENIED(message);
				break;
			}
			case SPECTATE_GAME_SUCCESS:
			{
				processSPECTATE_GAME_SUCCESS(message);
				break;
			}
			default:
				break;
		}
	}
	
	   public void processCHATMESSAGE(ServerToClientMessage message)
	   {
		   clientGUI.writeToTextArea(message.getMessage());
	   }
	   
	   public void processLOGON_DENIED(ServerToClientMessage message)
	   {
		   //clientGUI.writeToTextArea("logon denied because: " + message.getMessage());
		   JOptionPane.showMessageDialog(clientGUI,
				    message.getMessage(),
				    "Logon Failed",
				    JOptionPane.WARNING_MESSAGE);
		   messageListenerThread.connected = false;
	   }
	   
	   public void processLOGON_SUCCESS(ServerToClientMessage message)
	   {
		   messageListenerThread.username = message.getMessage();
		   clientGUI.writeToTextArea("logon success. you are logged in as: " + messageListenerThread.username);
		   clientGUI.setConnectedGUIState();
		   clientGUI.setTitle(messageListenerThread.username);
	   }
	   
	   public void processUPDATE_USERS(ServerToClientMessage message)
	   {
		   clientGUI.writeToUpdateUsersTextArea(message.getMessage());
	   }
	   
	   public void processUPDATE_HOSTED_GAMES(ServerToClientMessage message)
	   {
		   clientGUI.writeToHostedGamesTextArea(message.getMessage());
	   }
	   
	   public void processHOST_GAME_SUCCESS(ServerToClientMessage message)
	   {
		   clientGUI.writeToTextArea("i just started a hosted game and changed my state");
		   /*
		   messageListenerThread.changeState(new ClientHostedGameState(clientGUI, new HostedLobbyGUI(clientGUI, messageListenerThread, message.getMessage(),  
				   							 message.getAnotherMessage()), messageListenerThread));
				   							 */
		   HostedLobbyGUI hostedLobbyGUI = new HostedLobbyGUI(clientGUI, messageListenerThread, message.getMessage(), 
                   message.getAnotherMessage());
		   clientGUI.setHostedLobbyGUI(hostedLobbyGUI);
		   messageListenerThread.changeState(new ClientHostedGameState(clientGUI,hostedLobbyGUI , messageListenerThread));
		   hostedLobbyGUI.enableStartGame();
	   }
	   
	   public void processJOIN_HOSTED_GAME_DENIED(ServerToClientMessage message)
	   {
		   JOptionPane.showMessageDialog(clientGUI,
				    message.getMessage(),
				    "Joining Hosted Game Failed",
				    JOptionPane.WARNING_MESSAGE);
	   }
	   
	   public void processJOIN_HOSTED_GAME_SUCCESS(ServerToClientMessage message)
	   {
		   clientGUI.writeToTextArea("i just joined a hosted game and changed my state");
		   HostedLobbyGUI hostedLobbyGUI = new HostedLobbyGUI(clientGUI, messageListenerThread, message.getMessage(), 
                   message.getAnotherMessage());
		   clientGUI.setHostedLobbyGUI(hostedLobbyGUI);
		   messageListenerThread.changeState(new ClientHostedGameState(clientGUI,hostedLobbyGUI , messageListenerThread));
	   }
	   
	   public void processHOST_GAME_DENIED(ServerToClientMessage message)
	   {
		   JOptionPane.showMessageDialog(clientGUI,
				    message.getMessage(),
				    "Hosted Game Failed",
				    JOptionPane.WARNING_MESSAGE);
	   }
	   
	   public void processUPDATE_RUNNING_GAMES(ServerToClientMessage message)
	   {
		   clientGUI.writeToRunningGamesTextArea(message.getMessage());
	   }
	   
	   public void processSPECTATE_GAME_DENIED(ServerToClientMessage message)
	   {
		   JOptionPane.showMessageDialog(clientGUI,
				    message.getMessage(),
				    "Joining Running Game Failed",
				    JOptionPane.WARNING_MESSAGE);
	   }
	   
	   public void processSPECTATE_GAME_SUCCESS(ServerToClientMessage message)
	   {
		   clientGUI.writeToTextArea("i just joined a running game and changed my state");
		   RunningGameGUI runningGameGUI = new RunningGameGUI(clientGUI, messageListenerThread, message.getMessage(), null);
           clientGUI.setVisible(false); 
		   clientGUI.setRunningGameGUI(runningGameGUI);
		   messageListenerThread.changeState(new ClientRunningGameState(runningGameGUI, messageListenerThread));
	   }


}
