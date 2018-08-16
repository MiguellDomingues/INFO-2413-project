import java.io.IOException;

import javax.swing.JOptionPane;


public class ClientHostedGameState extends ClientState
{
	//takes a GUI object to update and write to GUI accordingly. The GUI object will be the hosted game lobby
	MainLobbyGUI clientGUI;
	 MessageListenerThread messageListenerThread;
	 HostedLobbyGUI hostedLobbyGUI;
	 
	 public ClientHostedGameState(MainLobbyGUI clientGUI, HostedLobbyGUI hostedLobbyGUI, MessageListenerThread messageListenerThread)
	 {
		 this.clientGUI = clientGUI;
		 this.hostedLobbyGUI = hostedLobbyGUI;
		 this.messageListenerThread = messageListenerThread;
		 hostedLobbyGUI.setVisible(true);
		 clientGUI.setVisible(false);
	 }
	
		public void readMessage(ServerToClientMessage message) throws IOException
		{
			//clientGUI.writeToTextArea("im currently processing messages in the hosted lobby");
			//messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.CHATMESSAGE, ""));
			//add the switch for reading server messages for the hosted game lobby. have the code for writing to all clients and updating the current users list
			switch(message.getMessageType())
			{
				
				case CHATMESSAGE:
				{
					processCHATMESSAGE(message);
					break;
				}
				case UPDATE_HOSTED_LOBBY_USERS:
				{
					processUPDATE_HOSTED_LOBBY_USERS(message);
					break;
				}
				case HOST_LEFT_lOBBY:
				{
					processHOST_LEFT_LOBBY(message);
					break;
				}
				case HOST_STARTED_GAME:
				{
					processHOST_STARTED_GAME(message);
					break;
				}
					
				default:
					break;
			}
		}
		
		public void processCHATMESSAGE(ServerToClientMessage message)
		{
			hostedLobbyGUI.writeToChatbox(message.getMessage());
		}
		
		public void processUPDATE_HOSTED_LOBBY_USERS(ServerToClientMessage message)
		{
			hostedLobbyGUI.writeToUpdateUsersTextArea(message.getMessage());
		}
		
		public void processHOST_LEFT_LOBBY(ServerToClientMessage message)
		{
			messageListenerThread.changeState(new ClientMainLobbyState(clientGUI, messageListenerThread));
			hostedLobbyGUI.dispose();
			clientGUI.setVisible(true);
			
			JOptionPane.showMessageDialog(clientGUI,
					   message.getMessage(),
					  "The host has left",
					   JOptionPane.WARNING_MESSAGE);
		}
		
		public void processHOST_STARTED_GAME(ServerToClientMessage message)
		{
			
			//hostedLobbyGUI.writeToChatbox(message.getMessage());
			 clientGUI.writeToTextArea("i just started a game and changed my state");
			 RunningGameGUI runningGameGUI = new RunningGameGUI(clientGUI, messageListenerThread, message.getMessage(), 
	                   message.getAnotherMessage());
			   clientGUI.setRunningGameGUI(runningGameGUI);
			   hostedLobbyGUI.dispose();
			   messageListenerThread.changeState(new ClientRunningGameState(runningGameGUI, messageListenerThread));
			   
		}


}
