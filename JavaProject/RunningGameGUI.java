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




public class RunningGameGUI extends JFrame implements ActionListener
{
	MainLobbyGUI clientGUI;
	MessageListenerThread messageListenerThread;
	
	ArrayList<PlayerInfoPanel> playerPanels;
	ArrayList<JLabel> communityCardPanels;
	
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
	
	JLabel playerTurnLabel;
	JLabel gameInformationLabel;
	
	

	JLabel flopCard1;
	JLabel flopCard2;
	JLabel flopCard3;
	
	JLabel turnCard;
	
	JLabel riverCard;
	
	JLabel gamePhaseLabel;
	
	//note that hostName is passed but no longer used
	public RunningGameGUI(MainLobbyGUI clientGUI, MessageListenerThread messageListenerThread, String gameName, String hostName) 
	{
		playerPanels = new ArrayList<PlayerInfoPanel>(); 
		communityCardPanels = new ArrayList<JLabel>();
		
		this.clientGUI = clientGUI;
		this.messageListenerThread = messageListenerThread;
		this.gameName = gameName;
		userName = messageListenerThread.getUserName();
		
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
		//setSize(600,600);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		//setTitle("Kwantlen Poker");
		
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
		
		gameInformationLabel = new JLabel("game info label");
		gameInformationLabel.setBounds(200, 500, 325, 23);
		getContentPane().add(gameInformationLabel);
		
		/*
		 * player2GameMove = new JLabel("GAMEMOVE2");
		player2GameMove.setBounds(185, 402, 105, 23);
		getContentPane().add(player2GameMove);
		 */
		
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
		raiseButton.setEnabled(false);
		
		betButton = new JButton("Bet");
		betButton.setBackground(Color.LIGHT_GRAY);
		betButton.setBounds(925, 570, 72, 32);
		getContentPane().add(betButton);
		betButton.addActionListener(this);
		betButton.setEnabled(false);
		
		checkButton = new JButton("Check");
		checkButton.setBackground(Color.LIGHT_GRAY);
		checkButton.setBounds(925, 603, 72, 32);
		getContentPane().add(checkButton);
		checkButton.addActionListener(this);
		checkButton.setEnabled(false);
		
		callButton = new JButton("Call");
		callButton.setBackground(Color.LIGHT_GRAY);
		callButton.setBounds(925, 635, 72, 32);
		getContentPane().add(callButton);
		callButton.addActionListener(this);
		callButton.setEnabled(false);
		
		
		foldButton = new JButton("Fold");
		foldButton.setBackground(Color.LIGHT_GRAY);
		foldButton.setBounds(925, 668, 72, 32);
		getContentPane().add(foldButton);
		foldButton.addActionListener(this);
		foldButton.setEnabled(false);
		
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
		
		//---------------------------scroll bar and the label with the scroll bar output-------------------------------------------
		
		scrollBar = new JScrollBar();
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.setBounds(1024, 572, 91, 32);
		getContentPane().add(scrollBar);
		scrollBar.setEnabled(false);
		
		//methods to use: setmaximum and setminimum for the minimum bet + 1 and the players stack
		
		
		
		
		scrollBar.addAdjustmentListener(new AdjustmentListener()
		{
		      public void adjustmentValueChanged(AdjustmentEvent ae) 
		      {
		    	  if(ae.getValue()+10 == playerStack)
		    		  raiseAmountLabel.setText("ALL IN BABY");
		    	  else
		    		  raiseAmountLabel.setText(String.valueOf(ae.getValue()+10));
		      }
		    }
		);
		
		
		raiseAmountLabel = new JLabel("$ 0");
		raiseAmountLabel.setLabelFor(scrollBar);
		raiseAmountLabel.setBounds(1065, 610, 100, 15);
		getContentPane().add(raiseAmountLabel);
		//to shunt the scrollbars value to the raiseAmountLabel as the scroll bar is moved left and right
		
		
		
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
		gamePotLabel.setBounds(577, 247, 250, 23);
		getContentPane().add(gamePotLabel);
		
		//-------------winner of last hand label-------------------------------
		
		playerTurnLabel = new JLabel("");
		playerTurnLabel.setBounds(551, 442, 237, 23);
		getContentPane().add(playerTurnLabel);
		
		//-----------------------------community cards group----------------------------
		
		
		flopCard1 = new JLabel("FlopCard1");//add community cards to evaluator
		flopCard1.setBounds(475, 298, 72, 121);
		getContentPane().add(flopCard1);
		flopCard1.setBackground(Color.WHITE);
		
		 communityCardPanels.add(flopCard1);
		
		flopCard2 = new JLabel("FlopCard2");
		flopCard2.setBounds(547, 298, 72, 121);
		getContentPane().add(flopCard2);
		
		communityCardPanels.add(flopCard2);
		
		flopCard3 = new JLabel("FlopCard3");
		flopCard3.setBounds(619, 298, 72, 121);
		getContentPane().add(flopCard3);
		
		communityCardPanels.add(flopCard3);
		
		turnCard = new JLabel("Turn");
		turnCard.setBounds(691, 298, 72, 121);
		getContentPane().add(turnCard);
		
		communityCardPanels.add(turnCard);
		
		riverCard = new JLabel("River");
		riverCard.setBounds(763, 298, 72, 121);
		getContentPane().add(riverCard);
		
		communityCardPanels.add(riverCard);
		
		
		//showing the game phase
		gamePhaseLabel = new JLabel("Game Phase ");
		gamePhaseLabel.setBounds(521, 271, 400, 23);
		getContentPane().add(gamePhaseLabel);
		
	}
	
	


	@Override
	public void actionPerformed(ActionEvent ae) 
	{
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
			
				int playerBet = scrollBar.getValue()+10;
			
				if(validateRaiseOrBet(playerBet))
				{
					System.out.println("sending a BET of " + playerBet + " to the server");
					transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.BET, playerBet));
				}
			
			return;
		}
		
		//pressing the raise button will run this code block
		if(ae.getSource() == raiseButton)
		{
			//try
			//{
			
				int playerRaise = scrollBar.getValue()+10;
			
				if(validateRaiseOrBet(playerRaise))
				{
					System.out.println("sending a RAISE of " + playerRaise + " to the server");
					transmitGameMoveToServer(new PlayersMoveInfo(Player.GameMove.RAISE, playerRaise));
				}
			
			//}
			//catch(NumberFormatException e)
			//{
			//	System.out.println("invalid input. game move canceled");
			//}
			
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
		setPlayerTurnLabel("");
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
	
	/*
	public void enableGameMoves()
	{
		checkButton.setEnabled(true);
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
		raiseButton.setEnabled(true);
		betButton.setEnabled(true);
	}
	*/
	
	public void enablePRE_BETGameMoves()
	{
		checkButton.setEnabled(true);
		foldButton.setEnabled(true);
		betButton.setEnabled(true);
		scrollBar.setEnabled(true);
	}
	
	public void enableBETGameMoves()
	{
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
		raiseButton.setEnabled(true);
		scrollBar.setEnabled(true);
	}
	
	public void enableSUDDEN_DEATHGameMoves()
	{
		foldButton.setEnabled(true);
		callButton.setEnabled(true);
		scrollBar.setEnabled(false);
	}
	
	public void disableGameMoves()
	{
		checkButton.setEnabled(false);
		foldButton.setEnabled(false);
		callButton.setEnabled(false);
		raiseButton.setEnabled(false);
		betButton.setEnabled(false);
		scrollBar.setEnabled(false);
	}
	
	public void setMinimumBet(int minimumBet)
	{
		this.minimumBet = minimumBet;
		scrollBar.setMinimum(minimumBet);
		
	}



	public void setPlayerStack(int playerStack)
	{
		this.playerStack = playerStack;
		scrollBar.setMaximum(playerStack);
	}
	
	public void validateScrollBar()
	{
		if(minimumBet >= playerStack)
		{
			scrollBar.setValue(playerStack);
			scrollBar.setEnabled(false);
			raiseButton.setEnabled(false);
			betButton.setEnabled(false);
		}
	}
	
	public ArrayList<PlayerInfoPanel> getPlayerPanels()
	{
		return playerPanels;
	}
	
	//ArrayList<JPanel> communitycardPanels;
	
	public  ArrayList<JLabel> getCommunityCardPanels()
	{
		return communityCardPanels;
	}
	
	public void setGamePotLabel(int pot)
	{
		gamePotLabel.setText("THE POT: " + String.valueOf(pot));
	}
	
	public void setGamePhaseLabel(String gamePhase)
	{
		gamePhaseLabel.setText(gamePhase);
	}
	
	public void setPlayerTurnLabel(String text)
	{
		playerTurnLabel.setText(text);
	}
	
	public void setGameInformationLabel(String gameInformation) 
	{
		gameInformationLabel.setText(gameInformation);
	}
	
	public String getUserName()
	{
		return userName;
	}
	

}