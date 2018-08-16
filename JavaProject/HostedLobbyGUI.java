import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class HostedLobbyGUI extends JFrame implements ActionListener 
{
	MainLobbyGUI clientGUI;
	MessageListenerThread messageListenerThread;
	JButton backToLobby, logout, startGame;
	JTextField tf;
	JTextArea chatBox, connectedUsers;
	String gameName;
	String hostName;

	public HostedLobbyGUI(MainLobbyGUI clientGUI, MessageListenerThread messageListenerThread, String gameName, String hostName)
	{
		this.clientGUI = clientGUI;
		this.messageListenerThread = messageListenerThread;
		this.gameName = gameName;
		this.hostName = hostName;
	   //setDefaultCloseOperation(EXIT_ON_CLOSE);
  	   setSize(600, 600);
  	   setVisible(true);
  	   setTitle("User name: " + messageListenerThread.getUserName() + " Lobby Name: " + gameName + " Host Name: " + hostName);
  	   
  	 addWindowListener(new WindowAdapter() 
  	 {
  	
  		public void windowClosing(WindowEvent e)
  		{
  		  returnToLobby();
  		}
  		
  	 });
  	 
  	JPanel northPanel = new JPanel(new GridLayout(3,1));
  	 JLabel label = new JLabel("Enter text to transmit", SwingConstants.CENTER);
 	
	 northPanel.add(label);
	 
	  tf = new JTextField("");
	  tf.setBackground(Color.WHITE);
	  tf.addActionListener(this);
	  tf.setEnabled(true);
	
	   northPanel.add(tf);
	   
	   add(northPanel, BorderLayout.NORTH);
	   
   	// the 3 buttons
   	  
	   backToLobby = new JButton("Back To Lobby");
   	  
   	   backToLobby.addActionListener(this);
   	           
   	   logout = new JButton("Logout");
   	          
   	   logout.addActionListener(this);
   	           
   	    logout.setEnabled(true);
   	           
   	    startGame = new JButton("Start Game");
   	           
   	    startGame.addActionListener(this);
   	           
   	    startGame.setEnabled(false);
   	    
   	    chatBox = new JTextArea("Welcome to the Chat room\n", 20, 20);
   	    connectedUsers = new JTextArea(80, 80);
   	    
   	 JPanel centerPanel = new JPanel(new GridLayout(1,1));
 	
	   centerPanel.add(new JScrollPane(chatBox));
	   centerPanel.add(new JScrollPane(connectedUsers));
	   
	   chatBox.setEditable(false);
	   connectedUsers.setEditable(false);
	   
	   JPanel southPanel = new JPanel();
 	  
	   southPanel.add(backToLobby);
	  southPanel.add(logout);
	  southPanel.add(startGame);
	    
	   
	   add(southPanel, BorderLayout.SOUTH);

	
	   add(centerPanel, BorderLayout.CENTER);
  	 
	}
	
	
	
	
	
	
	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource() == tf)
		 {
			 String message = tf.getText();
			 
			 if(!message.isEmpty())
			try {
					messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.CHATMESSAGE, message));
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				writeToChatbox("No blank messages");
			 
			tf.setText("");
			 return;
		 }
		
		if(ae.getSource() == backToLobby)
		{
			returnToLobby();
			
			return;
		}
		
		if(ae.getSource() == logout)
		{
			try {
				messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.LOGOFF, ""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientGUI.setVisible(true);
			dispose();
			return;
		}
		
		if(ae.getSource() == startGame)
		{
			try {
				messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.START_GAME, ""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		
	}
	
	public void writeToChatbox(String text)
	{
		chatBox.append(text + "\n");
	}
	
	public void writeToUpdateUsersTextArea(String message)
	{
		connectedUsers.setText(message);
	}
	
	public void returnToLobby()
	{
		clientGUI.writeToTextArea("hosted games lobby was closed");
		   try {
			   //on window close send message to server to send this client back to list of main lobby threads
			messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.RETURN_TO_LOBBY, ""));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   
		   //change state back to listen for main lobby messages and reveal lobby GUI
		 messageListenerThread.changeState(new ClientMainLobbyState(clientGUI, messageListenerThread));
		   clientGUI.setVisible(true);
		   dispose();
	}
	
	public void enableStartGame()
	{
		startGame.setEnabled(true);
	}

}
