import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class MainLobbyGUI extends JFrame implements ActionListener 
{
	Socket socketClient;
	 private JLabel label;
	 MessageListenerThread chatBox;
	 ConnectedServer connectedServer;
	 JTextArea connectedUsers;
	 JTextArea runningGames;
	 HostedLobbyGUI hostedLobbyGUI;
	 RunningGameGUI runningGameGUI;
	 
	 JTextArea hostedGames;

	     // to hold the Username and later on the messages
	
	     private JTextField tf, hostedGameName;
	     
	     
	
	     // to hold the server address an the port number
	
	     private JTextField tfServer, tfPort;
	
	     // to Logout and get the list of the users
	 
	     private JButton login, logout, hostGame, joinHostedGame, spectateGame;
	
	     // for the chat room
	 
	     private JTextArea ta;
	 
	     // if it is for connection
	 
	     private boolean connected;
	 
	     // the Client object
	 
	    // private Client client;
	 
	     // the default port number
	 
	     private int defaultPort;
	 
	     private String defaultHost;
	     
	     PrintWriter out;
         
	     
	     MainLobbyGUI(String host, int port, Socket socketClient) 
	     {
	    	 super("User Client");
	    	 
	    	 this.socketClient = socketClient;
	    	 defaultPort = port;
	    	 
	    	
	    	
	    	 defaultHost = host;
	    	 
	    	 connected = false;
	    
	    	 // The NorthPanel with:
	    	 JPanel northPanel = new JPanel(new GridLayout(3,1));
	    	
	    	 // the server name anmd the port number
	    	 JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
	    	
	    	 // the two JTextField with default value for server address and port number
	    	
	    	 tfServer = new JTextField(host);
	    
	    	 tfPort = new JTextField("" + port);
	    	
	    	 tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
	    	 
	    	  
	    	 serverAndPort.add(new JLabel("Server Address:  "));
	    	
	    	 serverAndPort.add(tfServer);
	    	
	    	 serverAndPort.add(new JLabel("Port Number:  "));
	    	 
	    	 serverAndPort.add(tfPort);
	    
	    	 serverAndPort.add(new JLabel(""));
	    	
	    	// adds the Server an port field to the GUI
	    	 
	    	 northPanel.add(serverAndPort);
	    	
	    	  // the Label and the TextField
	    	
	    	 label = new JLabel("Enter text to transmit", SwingConstants.CENTER);
	    	
	    	 northPanel.add(label);
	    	 
	    	  tf = new JTextField("");
	    	  tf.setBackground(Color.WHITE);
	    	  tf.addActionListener(this);
	    	  tf.setEnabled(false);
	    	
	    	   northPanel.add(tf);
	    	
	    	   add(northPanel, BorderLayout.NORTH);
	    	   
	    	// the 3 buttons
	    	  
	    	           login = new JButton("Login");
	    	  
	    	           login.addActionListener(this);
	    	           
	    	           logout = new JButton("Logout");
	    	          
	    	           logout.addActionListener(this);
	    	           
	    	           logout.setEnabled(false);
	    	           
	    	           hostGame = new JButton("Host Game");
	    	           
	    	           hostGame.addActionListener(this);
	    	           
	    	           hostGame.setEnabled(false);
	    	           
	    	           joinHostedGame = new JButton("Join Hosted Game");
	    	           
	    	           joinHostedGame.addActionListener(this);
	    	           
	    	           joinHostedGame.setEnabled(false);
	    	           
	    	           spectateGame = new JButton("Spectate Game");
	    	           
	    	           spectateGame.addActionListener(this);
	    	           
	    	           spectateGame.setEnabled(false);
	    	           
	    	           //hostedGameName = new JTextField("");
	    	           //hostedGameName.setBackground(Color.WHITE);
	    	           //hostedGameName.addActionListener(this);
	    	          // hostedGameName.setEnabled(false);
	    	          // hostedGameName.setHorizontalAlignment(SwingConstants.RIGHT);
	    	           
	    	           
	    	       
	    	           

	    	
	    	  
	    	
	    	   // The CenterPanel which is the chat room
	    	
	    	   ta = new JTextArea("Welcome to the Chat room\n", 20, 20);
	    	   connectedUsers = new JTextArea(80, 80);
	    	   hostedGames = new JTextArea(80,80);
	    	   runningGames = new JTextArea(80,80);
	    	 
	    	   JPanel centerPanel = new JPanel(new GridLayout(1,1));
	    	
	    	   centerPanel.add(new JScrollPane(ta));
	    	   centerPanel.add(new JScrollPane(connectedUsers));
	    	   centerPanel.add(new JScrollPane(hostedGames));
	    	   centerPanel.add(new JScrollPane(runningGames));
	    	   
	    	   hostedGames.addMouseListener(new HostedLobbySelectionListener());
	    	   runningGames.addMouseListener(new RunningGameSelectionListener());
	    	
	    	   ta.setEditable(false);
	    	   connectedUsers.setEditable(false);
	    	   hostedGames.setEditable(false);
	    	   runningGames.setEditable(false);
	    	   
	    	   JPanel southPanel = new JPanel();
	    	  
	    	   southPanel.add(login);
	    	  southPanel.add(logout);
	    	  southPanel.add(hostGame);
	    	  southPanel.add(joinHostedGame);
	    	  southPanel.add(spectateGame);
	    	  
	    	  
	    	  //southPanel.add(hostedGameName);
	    	   
	    	   add(southPanel, BorderLayout.SOUTH);
	    	   
	    	  

	    	
	    	   add(centerPanel, BorderLayout.CENTER);

	    	   setDefaultCloseOperation(EXIT_ON_CLOSE);
	    	   setSize(600, 600);
	    	   setVisible(true);
	    	   tf.requestFocus();

	    	 
	    	   hostedLobbyGUI = null;
	     }


	public void actionPerformed(ActionEvent ae) 
	{
		
		
		
		 if(ae.getSource() == login)
		  {

		 
	      try 
	      {
	    	   int port = Integer.parseInt(tfPort.getText());
	    	   String host = tfServer.getText();
	    	   
	    	   
	    	    ta.append(("C: Attempting to connect to "+ host +":"+ port) + "\n");
	    	    
	    	    //tfServer, tfPort;
	    	    
	    	    System.out.println("the port from the box is: " + tfPort.getText() + " the server is: " + tfServer.getText());
	    	    
				socketClient = new Socket(host,port);
				
				ta.append("Client: Connection Established" + "\n");
				
				connectedServer = new ConnectedServer(socketClient, this);
				
				String username = JOptionPane.showInputDialog(null,"enter username","must not be blank, longer then 15 characters, and unique",JOptionPane.QUESTION_MESSAGE);
			     
			     chatBox = new MessageListenerThread(socketClient, this, connectedServer, username);
			     chatBox.setPriority(Thread.NORM_PRIORITY);
			     chatBox.start();
			     
			     this.writeToTextArea("waiting for server confirmation.....");
			     
		  }
	      catch (UnknownHostException e)
		  {
	    	  ta.append("Host unknown. Cannot establish connection" + "\n");
	      } 
		  catch (IOException e) 
		  {
			  ta.append("Cannot establish connection. Server may not be up."+e.getMessage() + "\n");
	      }
		  catch(Exception e)
	      {
			  this.writeToTextArea("exception found");
			  
		  }

	        return;
	     }
		 
		 if(ae.getSource() == logout)
		 {
			 writeToTextArea("shutting down connection");
			 
		 try 
			{
				
			    chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.LOGOFF, ""));
				chatBox.close();
				
				
				return;
				
			} catch (IOException e) 
	 {
				//ta.append("wtf while logging out");
			}
		 }
		 
		  
		 
		 if(ae.getSource() == tf)
		 {
			 String message = tf.getText();
			 
			 if(!message.isEmpty())
				try {
					chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.CHATMESSAGE, message));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				 writeToTextArea("No blank messages");
			 
			 tf.setText("");
			 return;
		 }
		 
		 if(ae.getSource() == hostGame)
		 {
			
			  CreateHostGUI myPanel = new CreateHostGUI();
			 
		      int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter game name and number of players", JOptionPane.OK_CANCEL_OPTION);
			 
		      	if (result == JOptionPane.OK_OPTION) 
		      	{
		    	 
				  try 
				  {
					chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.HOST_GAME_REQUEST, 
							  															  myPanel.getGameName(),
							  															  myPanel.getNumPlayers(),
							  															  myPanel.getStartingMoney()));
				  } 
				  catch (IOException e)
				  {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  }
				  catch (NumberFormatException e)
				  {
					  writeToTextArea("improper input into create host game GUI");
				  }
				  
		        }
		      	
		 }
		 
		 if(ae.getSource() == joinHostedGame)
		 {
			 String gameName = tf.getText();//using the text field where you type in messages to fetch the game name. will add a seperate text field later
			 try {
				chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.JOIN_HOSTED_GAME, gameName));
				//note that if the hosted game name does not exist, NOTHING WILL HAPPEN. the server will not return an error. may change this.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 return;
		 
		 }
		 
		 if(ae.getSource() == spectateGame)
		 {
			 String gameName = tf.getText();//using the text field where you type in messages to fetch the game name. will add a seperate text field later
			 try {
				chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.SPECTATE_GAME, gameName));
				//note that if the hosted game name does not exist, NOTHING WILL HAPPEN. the server will not return an error. may change this.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 return;
		 
		 }
		 
		
		 
		 
		 
	}
	
	public void writeToTextArea(String message)
	{
		ta.append(message + "\n");
	}
	
	
	public void setDisconnectedGUIState()
	{
		chatBox = null;
		connectedServer = null;
		//out.close();
		//ta.append("logging out");
		login.setEnabled(true);
		logout.setEnabled(false);
		tf.setEnabled(false);
		
		
		connectedUsers.setText("");
		hostedGames.setText("");
		runningGames.setText("");
		
		hostGame.setEnabled(false);
		joinHostedGame.setEnabled(false);
		//hostedGameName.setEnabled(true);
		spectateGame.setEnabled(false);
		
		if(hostedLobbyGUI != null)
		{
			writeToTextArea("i am disposing the hosted lobby");
			hostedLobbyGUI.dispose();
			setVisible(true);
		}
		else if(runningGameGUI != null)
		{
			writeToTextArea("i am disposing the running game");
			runningGameGUI.dispose();
			setVisible(true);
		}
		
	}
	
	public void writeToUpdateUsersTextArea(String message)
	{
		connectedUsers.setText(message);
	}
	
	public void setConnectedGUIState()
	{
		login.setEnabled(false);
		logout.setEnabled(true);
		tf.setEnabled(true);
		hostGame.setEnabled(true);
		joinHostedGame.setEnabled(true);
		spectateGame.setEnabled(true);
		//hostedGameName.setEnabled(true);
	}
	
	public void writeToHostedGamesTextArea(String message)
	{
		hostedGames.setText(message);
	}
	
	public void writeToRunningGamesTextArea(String message)
	{
		runningGames.setText(message);
	}
	
	public void setHostedLobbyGUI(HostedLobbyGUI hostedLobbyGUI)
	{
		this.hostedLobbyGUI = hostedLobbyGUI;
	}
	
	public void setRunningGameGUI(RunningGameGUI runningGameGUI)
	{
		this.runningGameGUI = runningGameGUI;
	}

	
	class HostedLobbySelectionListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				//writeToTextArea("hosted lobby was clicked" + hostedGames.getSelectedText());
				 String gameName = tf.getText();//using the text field where you type in messages to fetch the game name. will add a seperate text field later
				 try {
					chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.JOIN_HOSTED_GAME, hostedGames.getSelectedText()));
					//note that if the hosted game name does not exist, NOTHING WILL HAPPEN. the server will not return an error. may change this.
				} catch (IOException r) {
					// TODO Auto-generated catch block
					r.printStackTrace();
				}
			}
		}
	}
	
	class RunningGameSelectionListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				//writeToTextArea("running was clicked" + runningGames.getSelectedText());
				 String gameName = tf.getText();//using the text field where you type in messages to fetch the game name. will add a seperate text field later
				 try {
					chatBox.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.SPECTATE_GAME, runningGames.getSelectedText()));
					//note that if the hosted game name does not exist, NOTHING WILL HAPPEN. the server will not return an error. may change this.
				} catch (IOException r) {
					// TODO Auto-generated catch block
					r.printStackTrace();
				}
			}
		}
	}

	//
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
