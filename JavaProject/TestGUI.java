import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class TestGUI extends JFrame implements ActionListener
{
	MainLobbyGUI clientGUI;
	MessageListenerThread messageListenerThread;
	
	ArrayList<PlayerInfoPanel> playerPanels;
	
	String gameName;
	String userName;
	
	int minimumBet;
	
	int playerStack;
	
	//private JFrame frame;
	private JTextField textField;//this is the textfield for typing into
	
	JPanel chatBoxPanel;
	JPanel messagePanel;
	JPanel panel;
	JPanel playerListPanel;
	
	JTextArea playingList;
	JTextArea spectatingList;
	JTextArea chatBox;
	
	
	
	JButton sendButton;
	JButton raiseButton;
	JButton betButton;
	JButton checkButton;
	JButton callButton;
	JButton foldButton;
	JButton returnToLobbyButton;
	JButton logOutButton;
	
	JLabel raiseAmountLabel;
	
	JScrollBar scrollBar;
	
	JLabel player1StackLabel;
	JLabel player2StackLabel;
	JLabel player3StackLabel;
	JLabel player4StackLabel;
	
	JLabel player1NameLabel;
	JLabel player2NameLabel;
	JLabel player3NameLabel;
	JLabel player4NameLabel;
	
	JLabel player1HoleCard1;
	JLabel player1HoleCard2;
	
	JLabel player2HoleCard1;
	JLabel player2HoleCard2;
	
	JLabel player3HoleCard1;
	JLabel player3HoleCard2;
	
	JLabel player4HoleCard1;
	JLabel player4HoleCard2;
	
	JLabel player1GameMove;
	JLabel player2GameMove;
	JLabel player3GameMove;
	JLabel player4GameMove;
	
	JLabel gamePotLabel;
	
	JLabel lastHandWinnerLabel;
	
	JLabel flopCard1;
	JLabel flopCard2;
	JLabel flopCard3;
	
	JLabel turnCard;
	
	JLabel riverCard;
	
	JLabel gamePhaseLabel;
	
	//note that hostName is passed but no longer used
	public TestGUI(MainLobbyGUI clientGUI, MessageListenerThread messageListenerThread, String gameName, String hostName) 
	{
		playerPanels = new ArrayList<PlayerInfoPanel>(); 
		
		this.clientGUI = clientGUI;
		this.messageListenerThread = messageListenerThread;
		this.gameName = gameName;
		//userName = messageListenerThread.getUserName();
		
		//this.hostName = hostName;
	   //setDefaultCloseOperation(EXIT_ON_CLOSE);
  	   
  	   
  	   setTitle("THIS IS THE GAME GUI " + "User name: " + userName + " Game Name: " + gameName);
		
		
		
		
		
		
		initialize();
		//testerMethod();
		setVisible(true);
	}

	
	private void initialize() 
	{

		
		//this is for setting up the frame
		getContentPane().setBackground(new Color(34, 139, 34));
		setSize(1400,750);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setTitle("Kwantlen Poker");
		
		//makes it so when the x button in the corner is pressed runs code to return the client back to lobby
		 addWindowListener(new WindowAdapter() 
	  	 {
	  	
	  		public void windowClosing(WindowEvent e)
	  		{
	  		  returnToLobby();
	  		}
	  		
	  	 });
		
		
		chatBoxPanel = new JPanel();
		chatBoxPanel.setBounds(0, 10, 165, 690);
		getContentPane().add(chatBoxPanel);
		chatBoxPanel.setLayout(null);
		
		messagePanel = new JPanel();
		messagePanel.setBounds(0, 610, 165, 80);
		chatBoxPanel.add(messagePanel);
		messagePanel.setLayout(null);
		
		//remove this button. chat box will enter messages when enter is pressed
		sendButton = new JButton("Send");
		sendButton.setBackground(Color.LIGHT_GRAY);
		sendButton.setBounds(97, 10, 68, 60);
		messagePanel.add(sendButton);
		sendButton.addActionListener(this);
		
		textField = new JTextField("messages to type appear here");
		textField.setBounds(10, 18, 86, 45);
		messagePanel.add(textField);
		textField.setColumns(10);
		
		panel = new JPanel();
		panel.setBounds(0, 0, 165, 611);
		chatBoxPanel.add(panel);
		panel.setLayout(new GridLayout(1, 1));
		
		
		
		playerListPanel = new JPanel();
		playerListPanel.setBounds(1188, 10, 186, 690);
		getContentPane().add(playerListPanel);
		playerListPanel.setLayout(new GridLayout(2, 1));
		
	   
		
		//user lists
		playingList = new JTextArea("This area is for players in the game\n",80, 80);
		playerListPanel.add(new JScrollPane(playingList));
		
		spectatingList = new JTextArea("This area is for spectators in the game\n",80, 80);
		playerListPanel.add(new JScrollPane(spectatingList));
		
		chatBox = new JTextArea("Welcome to the Chat room\n", 20, 20);
		panel.add(new JScrollPane(chatBox));
		
		 chatBox.setEditable(false);
		 playingList.setEditable(false);
		 spectatingList.setEditable(false);
		 
		//-------all the GUI buttons---------------------------------------------------------
		
		raiseButton = new JButton("Raise");
		raiseButton.setBackground(Color.LIGHT_GRAY);
		raiseButton.setBounds(925, 540, 72, 32);
		getContentPane().add(raiseButton);
		raiseButton.addActionListener(this);
		
		betButton = new JButton("Bet");
		betButton.setBackground(Color.LIGHT_GRAY);
		betButton.setBounds(925, 570, 72, 32);
		getContentPane().add(betButton);
		betButton.addActionListener(this);
		
		checkButton = new JButton("Check");
		checkButton.setBackground(Color.LIGHT_GRAY);
		checkButton.setBounds(925, 603, 72, 32);
		getContentPane().add(checkButton);
		checkButton.addActionListener(this);
		
		callButton = new JButton("Call");
		callButton.setBackground(Color.LIGHT_GRAY);
		callButton.setBounds(925, 635, 72, 32);
		getContentPane().add(callButton);
		callButton.addActionListener(this);
		
		
		foldButton = new JButton("Fold");
		foldButton.setBackground(Color.LIGHT_GRAY);
		foldButton.setBounds(925, 668, 72, 32);
		getContentPane().add(foldButton);
		foldButton.addActionListener(this);
		
		returnToLobbyButton = new JButton("Return to Lobby");
		returnToLobbyButton.setBackground(Color.LIGHT_GRAY);
		returnToLobbyButton.setFont(UIManager.getFont("Button.font"));
		returnToLobbyButton.setBounds(175, 668, 132, 32);
		getContentPane().add(returnToLobbyButton);
		returnToLobbyButton.addActionListener(this);
		
		logOutButton = new JButton("Log out");
		logOutButton.setBackground(Color.LIGHT_GRAY);
		logOutButton.setBounds(317, 668, 83, 32);
		getContentPane().add(logOutButton);
		logOutButton.addActionListener(this);
		
		//player2GameMove.setBounds(185, 402, 105, 23);
		
		JLabel gameInformationLabel = new JLabel("game info label" + "\n" + "allalaladfdfdfdffdfdfdfdfdfdfdfdfdfdfdfdfdlfgfgfgfgfgfgfa");
		gameInformationLabel.setBounds(200, 500, 325, 23);
		getContentPane().add(gameInformationLabel);
		
		//---------------------------scroll bar and the label with the scroll bar output-------------------------------------------
		
		scrollBar = new JScrollBar();
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.setBounds(1024, 572, 91, 32);
		getContentPane().add(scrollBar);
		
		scrollBar.setMinimum(2000);
		scrollBar.setMaximum(200);
		//scrollBar.setUnitIncrement(1);
		
		
		scrollBar.addAdjustmentListener(new AdjustmentListener()
		{
		      public void adjustmentValueChanged(AdjustmentEvent ae) 
		      {
		    	  raiseAmountLabel.setText(String.valueOf(scrollBar.getValue()));
		      }
		    }
		);
		
		
		
		
		
		
		
		
		raiseAmountLabel = new JLabel("$ 0");
		raiseAmountLabel.setLabelFor(scrollBar);
		raiseAmountLabel.setBounds(1065, 610, 41, 15);
		getContentPane().add(raiseAmountLabel);
		//raiseAmountLabel.setBounds(1137, 581, 41, 15);
		//raiseAmountLabel.setIcon(icon);
		//checkButton.setBounds(925, 603, 72, 32);
		
		
		
		
		//-------------------------------------------------------------
		//labels for unique data pretaining for each player--------------------------------------------------
		//THESE NEED TO BE PUT INTO AN OBJECT ALONG WITH PLAYERS LAST MOVE and name, then make a list of these objects
		
		//-------------player info labels for player 1------------------------
		
		player1StackLabel = new JLabel("Stack: $1");
		player1StackLabel.setBounds(597, 577, 83, 23);
		getContentPane().add(player1StackLabel);
		
		player1NameLabel = new JLabel("Player 1");
		player1NameLabel.setBackground(Color.LIGHT_GRAY);
		player1NameLabel.setBounds(597, 619, 83, 65);
		getContentPane().add(player1NameLabel);
		player1NameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		player1NameLabel.setBackground(new Color(66,168,66));
		
		player1HoleCard1 = new JLabel("Player 1 Hole Card1");
		player1HoleCard1.setBounds(568, 476, 72, 110);
		getContentPane().add(player1HoleCard1);
		

		
		player1HoleCard2 = new JLabel("Player 1 Hole Card2");
		player1HoleCard2.setBounds(640, 476, 72, 110);
		getContentPane().add(player1HoleCard2);
		
		player1GameMove = new JLabel("GAMEMOVE1");
		player1GameMove.setBounds(568, 688, 105, 23);
		getContentPane().add(player1GameMove);
		
		playerPanels.add(new PlayerInfoPanel(player1StackLabel,player1NameLabel,player1HoleCard1,player1HoleCard2,player1GameMove));
		
		
		
		
		//--------------------player info labels for player 2------------------------------------------
		
		
		player2StackLabel = new JLabel("Stack: $2");
		player2StackLabel.setBounds(175, 292, 83, 23);
		getContentPane().add(player2StackLabel);
		
		player2NameLabel = new JLabel("Player 2");
		player2NameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		player2NameLabel.setBackground(Color.LIGHT_GRAY);
		player2NameLabel.setBounds(175, 326, 83, 65);
		getContentPane().add(player2NameLabel);
		
		player2HoleCard1 = new JLabel("Player 2 Hole Card1");
		player2HoleCard1.setBackground(Color.WHITE);
		player2HoleCard1.setBounds(269, 298, 72, 121);
		getContentPane().add(player2HoleCard1);
		
		player2HoleCard2 = new JLabel("Player 2 Hole Card2");
		player2HoleCard2.setBounds(341, 298, 72, 121);
		getContentPane().add(player2HoleCard2);
		
		player2GameMove = new JLabel("GAMEMOVE2");
		player2GameMove.setBounds(185, 402, 105, 23);
		getContentPane().add(player2GameMove);
		
		playerPanels.add(new PlayerInfoPanel(player2StackLabel,player2NameLabel,player2HoleCard1,player2HoleCard2,player2GameMove));
		
		
		
		//------------------player info labels for player 3--------------------------
		
		
		
		player3StackLabel = new JLabel("Stack: $3");
		player3StackLabel.setBounds(608, 9, 83, 23);
		getContentPane().add(player3StackLabel);
		
		player3NameLabel = new JLabel("Player 3");
		player3NameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		player3NameLabel.setBackground(Color.LIGHT_GRAY);
		player3NameLabel.setBounds(597, 39, 83, 65);
		getContentPane().add(player3NameLabel);
		
		player3HoleCard1 = new JLabel("Player 3 Hole Card1");
		player3HoleCard1.setBackground(Color.WHITE);
		player3HoleCard1.setBounds(568, 126, 72, 121);
		getContentPane().add(player3HoleCard1);
				
		player3HoleCard2 = new JLabel("Player 3 Hole Card2");
		player3HoleCard2.setBounds(640, 126, 72, 121);
		getContentPane().add(player3HoleCard2);
		
		player3GameMove = new JLabel("GAMEMOVE3");
		player3GameMove.setBounds(586, 104, 105, 23);
		getContentPane().add(player3GameMove);
				
		
		playerPanels.add(new PlayerInfoPanel(player3StackLabel,player3NameLabel,player3HoleCard1,player3HoleCard2,player3GameMove));
		
		//---------------------player info labels for player 4------------------------------
		
		
		
		player4StackLabel = new JLabel("Stack: $4");
		player4StackLabel.setBounds(1095, 292, 83, 23);
		getContentPane().add(player4StackLabel);
		
		JLabel player4NameLabel =new JLabel("Player 4");
		player4NameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		player4NameLabel.setBackground(Color.LIGHT_GRAY);
		player4NameLabel.setBounds(1095, 326, 83, 65);
		getContentPane().add(player4NameLabel);
		
		player4HoleCard1 = new JLabel("Player 4 Hole Card1");
		player4HoleCard1.setBackground(Color.WHITE);
		player4HoleCard1.setBounds(904, 292, 72, 121);
		getContentPane().add(player4HoleCard1);
				
		player4HoleCard2 = new JLabel("Player 4 Hole Card2");
		player4HoleCard2.setBounds(976, 292, 72, 121);
		getContentPane().add(player4HoleCard2);
		
		player4GameMove = new JLabel("GAMEMOVE4");
		player4GameMove.setBounds(1047, 406, 105, 23);
		getContentPane().add(player4GameMove);
				
		playerPanels.add(new PlayerInfoPanel(player4StackLabel,player4NameLabel,player4HoleCard1,player4HoleCard2,player4GameMove));
		
		//--------pot label------------------------------
		
		gamePotLabel = new JLabel("Pot: ");
		gamePotLabel.setBounds(577, 247, 83, 23);
		getContentPane().add(gamePotLabel);
		
		//-------------winner of last hand label-------------------------------
		
		lastHandWinnerLabel = new JLabel("Winner of last hand:");
		lastHandWinnerLabel.setBounds(551, 442, 237, 23);
		getContentPane().add(lastHandWinnerLabel);
		
		//-----------------------------community cards group----------------------------
		
		
		flopCard1 = new JLabel("FlopCard1");//add community cards to evaluator
		flopCard1.setBounds(475, 298, 72, 121);
		getContentPane().add(flopCard1);
		flopCard1.setBackground(Color.WHITE);
		
		flopCard2 = new JLabel("FlopCard2");
		flopCard2.setBounds(547, 298, 72, 121);
		getContentPane().add(flopCard2);
		
		flopCard3 = new JLabel("FlopCard3");
		flopCard3.setBounds(619, 298, 72, 121);
		getContentPane().add(flopCard3);
		
		turnCard = new JLabel("Turn");
		turnCard.setBounds(691, 298, 72, 121);
		getContentPane().add(turnCard);
		
		riverCard = new JLabel("River");
		riverCard.setBounds(763, 298, 72, 121);
		getContentPane().add(riverCard);
		
		
		//showing the game phase
		gamePhaseLabel = new JLabel("Game Phase ");
		gamePhaseLabel.setBounds(521, 271, 195, 23);
		getContentPane().add(gamePhaseLabel);
		
		
		
	}
	
	//ImageIcon backCard = new ImageIcon("card/backCard.png");
	
	//player1HoleCard1.setIcon(backCard);
		
		
		
		
		
		
	


	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		new TestThread().start();
		//testerMethod();
		
		//send button listener
		if(ae.getSource() == sendButton)
		 {
			 String message = textField.getText();
			 
			 if(!message.isEmpty())
			try {
					messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.CHATMESSAGE, message));
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				writeToChatbox("No blank messages");
			 
			 textField.setText("");
			 return;
		 }
		
		//logout button listener
		if(ae.getSource() == returnToLobbyButton)
		{
			returnToLobby();
			return;
		}
		
		//pressing the "logout" button runs this code block
		if(ae.getSource() == logOutButton)
		{
			try 
			{
				messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.LOGOFF, ""));
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
			clientGUI.setVisible(true);
			dispose();
			return;
		}
		
		//pressing the check button will run this code block
		if(ae.getSource() == checkButton)
		 {
			transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.CHECK, 0));
			return;
		}
		
		//pressing the bet button will run this code block
		if(ae.getSource() == betButton)//put cry catches for numbeformat exception in here and raise
		{
			try
			{
				int playerBet = Integer.parseInt(textField.getText());
			
				if(validateRaiseOrBet(playerBet))
				{
				
					transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.BET, playerBet));
				}
			}
			catch(NumberFormatException e)
			{
				System.out.println("invalid input. game move canceled");
			}
			
			return;
		}
		
		//pressing the raise button will run this code block
		if(ae.getSource() == raiseButton)
		{
			try
			{
			
				int playerRaise = Integer.parseInt(textField.getText());
			
				if(validateRaiseOrBet(playerRaise))
				{
					transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.RAISE, playerRaise));
				}
			
			}
			catch(NumberFormatException e)
			{
				System.out.println("invalid input. game move canceled");
			}
			
			return;
		}
		
		//pressing the fold button will run this code block
		if(ae.getSource() == foldButton)
		{
			transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.FOLD, 0));
			return;
		}
		
		//pressing the call button will run this code block
		if(ae.getSource() == callButton)
		{
			transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.CALL, 0));
			return;
		}
		
	}
	
	public void transmitGameMoveToServer(PlayersMoveInfo playersMoveInfo)
	{
		try {
			messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.GAME_MOVE, playersMoveInfo));
			writeToChatbox("game move has been sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		disableGameMoves();
	}
	
	public boolean validateRaiseOrBet(int playerInput)
	{
		if(playerInput > playerStack)
		{
			JOptionPane.showMessageDialog(this,
 					 				"INPROPER INPUT",
 					  			    "you cannot bet more chips then you have!",
 					  			     JOptionPane.WARNING_MESSAGE);
			return false;//trying to bet more then you have, invalid input
		}
		if(playerInput < minimumBet)
		{
			JOptionPane.showMessageDialog(this,
		 				"INPROPER INPUT",
		  			    "you cannot bet less then the minimum bet!",
		  			     JOptionPane.WARNING_MESSAGE);
			return false;//try to bet less then the minimum required bet. invalid input
		}
		
		return true;
	}
	
	public void writeToChatbox(String text)
	{
		chatBox.append(text + "\n");
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
	
	public void writeToUpdatePlayers(String message)
	{
		playingList.setText(message);
	}
	
	public void writeToUpdateSpectators(String message)
	{
		spectatingList.setText(message);
	}
	
	public void enableGameMoves()
	{
		checkButton.setEnabled(true);
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
		raiseButton.setEnabled(true);
		betButton.setEnabled(true);
	}
	
	public void enablePRE_BETGameMoves()
	{
		checkButton.setEnabled(true);
		foldButton.setEnabled(true);
		betButton.setEnabled(true);
	}
	
	public void enableBETGameMoves()
	{
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
		raiseButton.setEnabled(true);
	}
	
	public void enableSUDDEN_DEATHGameMoves()
	{
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
	}
	
	public void disableGameMoves()
	{
		checkButton.setEnabled(false);
		foldButton.setEnabled(false);
		callButton.setEnabled(false);
		raiseButton.setEnabled(false);
		betButton.setEnabled(false);
	}
	
	public void setMinimumBet(int minimumBet)
	{
		this.minimumBet = minimumBet;
	}



	public void setPlayerStack(int playerStack)
	{
		this.playerStack = playerStack;
	}
	
	private class TestThread extends Thread
	{
		public void run()
		{
			
			ImageIcon aCard = new ImageIcon("card/backCard.png");
			
			//backCard.
			
			player1HoleCard1.setIcon(aCard);
			
			player1HoleCard1.setIcon(null);
			
			
			/*
			Deck deck = new Deck();
			
			while(true)
			{
				Card card = deck.drawCard();
				
				if(card != null)
					;
				else
					break;
			}
			
			ArrayList<Card> drawnCards = deck.getDrawnCards();
			
			for(int i = 0; i < drawnCards.size(); i++)
			{
				Card card = drawnCards.get(i);
				
				String filePath = "card/" + card.getSuite().toString() + card.getRank().toString() + ".png";
				
				System.out.println("attempting to draw" + card.toString());
				
				System.out.println(filePath);
				
				ImageIcon backCard = new ImageIcon(filePath);
				
				//backCard.
				
				player1HoleCard1.setIcon(backCard);
				//this.repaint();
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
			*/
			
		}
		
	}
	

}

/*********************************************THIS IS A BACKUP OF THE OLD GUI CODE****************************************************************
 * 
 * import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class RunningGameGUI extends JFrame implements ActionListener  
{

	MainLobbyGUI clientGUI;
	MessageListenerThread messageListenerThread;
	JButton backToLobby, logout, gameMove;
	JButton check, fold, call, raise, bet;
	JTextField tf;
	JTextArea chatBox, connectedPlayers, connectedSpectators;
	String gameName;
	String userName;
	
	int minimumBet;
	

	int playerStack;

	public RunningGameGUI(MainLobbyGUI clientGUI, MessageListenerThread messageListenerThread, String gameName, String hostName)
	{
		this.clientGUI = clientGUI;
		this.messageListenerThread = messageListenerThread;
		this.gameName = gameName;
		userName = messageListenerThread.getUserName();
		//this.hostName = hostName;
	   //setDefaultCloseOperation(EXIT_ON_CLOSE);
  	   setSize(600, 600);
  	   setVisible(true);
  	   setTitle("THIS IS THE GAME GUI " + "User name: " + userName + " Game Name: " + gameName);
  			  
  	   
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
   	    
   	 //check, fold, call, raise, bet
   	    
   	    check = new JButton("Check");
   	    check.addActionListener(this);
   	    check.setEnabled(false);
   	    
   	 fold = new JButton("Fold");
   	fold.addActionListener(this);
   	fold.setEnabled(false);
   	
   	call = new JButton("Call");
   	call.addActionListener(this);
   	call.setEnabled(false);
   	
   	raise = new JButton("Raise");
   	raise.addActionListener(this);
   	raise.setEnabled(false);
   	
   	bet = new JButton("Bet");
   	bet.addActionListener(this);
   	bet.setEnabled(false);
   	    
   	    
   	           
   	    // gameMove = new JButton("Game Move");
   	           
   	   // gameMove.addActionListener(this);
   	           
   	   // gameMove.setEnabled(false);
   	    
   	    
   	    
   	    chatBox = new JTextArea("Welcome to the Chat room\n", 20, 20);
   	    connectedPlayers = new JTextArea("This area is for players in the game\n",80, 80);
   	    connectedSpectators =  new JTextArea("This area is for spectators in the game\n",80, 80);
   	    
   	 JPanel centerPanel = new JPanel(new GridLayout(1,1));
 	
	   centerPanel.add(new JScrollPane(chatBox));
	   centerPanel.add(new JScrollPane(connectedPlayers));
	   centerPanel.add(new JScrollPane(connectedSpectators));
	   
	   chatBox.setEditable(false);
	   connectedPlayers.setEditable(false);
	   connectedSpectators.setEditable(false);
	   
	   JPanel southPanel = new JPanel();
 	  
	   southPanel.add(backToLobby);
	  southPanel.add(logout);
	  //southPanel.add(gameMove);
	  
	  southPanel.add(check);
	  southPanel.add(fold);
	  southPanel.add(raise);
	  southPanel.add(call);
	  southPanel.add(bet);
	    
	   
	   add(southPanel, BorderLayout.SOUTH);

	
	   add(centerPanel, BorderLayout.CENTER);

	}
	
	public void setMinimumBet(int minimumBet)
	{
		this.minimumBet = minimumBet;
	}



	public void setPlayerStack(int playerStack)
	{
		this.playerStack = playerStack;
	}
	
    
	
	public void actionPerformed(ActionEvent ae) 
	{
		//clicking enter button in chat input feild runs this code block
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
		
		//clicking "back to lobby" button runs this code block
		if(ae.getSource() == backToLobby)
		{
			returnToLobby();
			
			return;
		}
		
		//pressing the "logout" button runs this code block
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
		
		//pressing the "game move" button enables this code block. upon press, takes text from the text feild, sends it as a game move and disables the button 
		if(ae.getSource() == check)
		 {
			transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.CHECK, 0));
			return;
		}
		
		if(ae.getSource() == bet)//put cry catches for numbeformat exception in here and raise
		{
			try
			{
				int playerBet = Integer.parseInt(tf.getText());
			
				if(validateRaiseOrBet(playerBet))
				{
				
					transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.BET, playerBet));
				}
			}
			catch(NumberFormatException e)
			{
				System.out.println("invalid input. game move canceled");
			}
			
			
			
			
			return;
		}
		
		if(ae.getSource() == raise)
		{
			try
			{
			
				int playerRaise = Integer.parseInt(tf.getText());
			
				if(validateRaiseOrBet(playerRaise))
				{
					transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.RAISE, playerRaise));
				}
			
			}
			catch(NumberFormatException e)
			{
				System.out.println("invalid input. game move canceled");
			}
			
			return;
		}
		
		if(ae.getSource() == fold)
		{
			transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.FOLD, 0));
			return;
		}
		
		if(ae.getSource() == call)
		{
			transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.CALL, 0));
			return;
		}
		
		
	}
	
//int minimumBet;
	

	//int playerStack;
	
	public boolean validateRaiseOrBet(int playerInput)
	{
		if(playerInput > playerStack)
		{
			JOptionPane.showMessageDialog(this,
 					 				"INPROPER INPUT",
 					  			    "you cannot bet more chips then you have!",
 					  			     JOptionPane.WARNING_MESSAGE);
			return false;//trying to bet more then you have, invalid input
		}
		if(playerInput < minimumBet)
		{
			JOptionPane.showMessageDialog(this,
		 				"INPROPER INPUT",
		  			    "you cannot bet less then the minimum bet!",
		  			     JOptionPane.WARNING_MESSAGE);
			return false;//try to bet less then the minimum required bet. invalid input
		}
		
		return true;
	}
	
	public void writeToChatbox(String text)
	{
		chatBox.append(text + "\n");
	}
	
	public void writeToUpdatePlayers(String message)
	{
		connectedPlayers.setText(message);
	}
	
	public void writeToUpdateSpectators(String message)
	{
		connectedSpectators.setText(message);
	}
	
	public void enableGameMoves()
	{
		check.setEnabled(true);
		fold.setEnabled(true);
		call.setEnabled(true);
		raise.setEnabled(true);
		bet.setEnabled(true);
	}
	
	public void enablePRE_BETGameMoves()
	{
		check.setEnabled(true);
		fold.setEnabled(true);
		bet.setEnabled(true);
	}
	
	public void enableBETGameMoves()
	{
		fold.setEnabled(true);
		call.setEnabled(true);
		raise.setEnabled(true);
	}
	
	public void enableSUDDEN_DEATHGameMoves()
	{
		fold.setEnabled(true);
		call.setEnabled(true);
	}
	
	
	
	
	
	
	
	public void disableGameMoves()
	{
		check.setEnabled(false);
		fold.setEnabled(false);
		call.setEnabled(false);
		raise.setEnabled(false);
		bet.setEnabled(false);
	}
	
	public void transmitGameMoveToServer(PlayersMoveInfo playersMoveInfo)
	{
		try {
			messageListenerThread.getServerConnection().WriteToServer(new ClientToServerMessage(ClientToServerMessage.Message.GAME_MOVE, playersMoveInfo));
			writeToChatbox("game move has been sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		disableGameMoves();
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
	
}

 * 
 * 
 * 
 * 
 * 
 *
*/