import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class ClientRunningGameState extends ClientState
{
	//takes a GUI object to update and write to GUI accordingly. The GUI object will be the running game
	
	//takes a GUI object to update and write to GUI accordingly. The GUI object will be the hosted game lobby
		//ClientGUI clientGUI;
		 MessageListenerThread messageListenerThread;
		 RunningGameGUI runningGameGUI;
		 
		 public ClientRunningGameState(RunningGameGUI runningGameGUI,  MessageListenerThread messageListenerThread)
		 {
			 
			 this.runningGameGUI = runningGameGUI;
			 this.messageListenerThread = messageListenerThread;
			 runningGameGUI.setVisible(true);
			// clientGUI.setVisible(false);
		 }
		
			public void readMessage(ServerToClientMessage message) throws IOException
			{
				
				switch(message.getMessageType())
				{
					
					case CHATMESSAGE:
					{
						processCHATMESSAGE(message);
						break;
					}
					case UPDATE_PLAYER_USERS:
					{
						processUPDATE_PLAYER_USERS(message);
						break;
					}
					case UPDATE_GUI:
					{
						processUPDATE_GUI(message);
						break;
					}
					case UPDATE_SPECTATOR_USERS:
					{
						processUPDATE_SPECTATOR_USERS(message);
						break;
					}
					case TAKE_TURN:
					{
						processTAKE_TURN(message);
						break;
					}
					case DECLARE_LOSER:
					{
						processDECLARE_WINNER(message);
						break;
					}
					case LOSE_TURN:
					{
						processLOSE_TURN(message);
						break;
					}
					default:
					{
						break;
					}
				}
			}
			
			public void processCHATMESSAGE(ServerToClientMessage message)
			{
				runningGameGUI.writeToChatbox(message.getMessage());
			}
			
			public void processUPDATE_PLAYER_USERS(ServerToClientMessage message)
			{
				runningGameGUI.writeToUpdatePlayers(message.getMessage());
			}
			
			public void processUPDATE_GUI(ServerToClientMessage message)
			{
				//runningGameGUI.writeToChatbox("getting the game GUI update");
				
				//runningGameGUI.writeToChatbox(message.getMessage());
				
				
				CurrentGameFrame currentGameFrame = message.getCurrentGameFrame();
				
				int currentPot = currentGameFrame.getCurrentPot();
				
				String playerTurn = currentGameFrame.getPlayersTurn();
				
				String gameInformation = currentGameFrame.getOtherInformation();
				
				String currentGamePhase = currentGameFrame.getCurrentGamePhase();
				
				ArrayList<PlayerStateInfo> currentPlayersInThisPokerHand = currentGameFrame.getPlayerInfoList();
				ArrayList<Card> communityCards = currentGameFrame.getCommunityCards().getCommunityCards();
				
				runningGameGUI.setGamePotLabel(currentPot);
				runningGameGUI.setGamePhaseLabel(currentGamePhase);
				runningGameGUI.setPlayerTurnLabel(playerTurn);
				runningGameGUI.setGameInformationLabel(gameInformation);
				//System.out.println(currentPot);
				//System.out.println(currentGamePhase);
				
				//System.out.println("the pot is " + currentPot);
				//System.out.println("the current game phase is " + currentGamePhase);
				
				
				
				//System.out.println("the current player info is:");
				
				System.out.println(communityCards);
				
				updateCommunityCardPanel(communityCards, runningGameGUI.getCommunityCardPanels());
				
				
				
				//Collections.sort(currentPlayersInThisPokerHand);
				
				//displayPlayerInfo(currentPlayersInThisPokerHand);
				
				updatePlayerPanels(currentPlayersInThisPokerHand, runningGameGUI.getPlayerPanels(), currentGamePhase);
				
				
				
				//get the game move from the player and update the gui accordingly
				//prob gonna need an emueration and a switch for the different game moves
			}
			
			public void processTAKE_TURN(ServerToClientMessage message)
			{	
				//runningGameGUI.writeToChatbox("it is now your turn");
				runningGameGUI.setPlayerTurnLabel("IT IS NOW YOUR TURN");
				
				PlayerTakeTurnInfo playerTakeTurnInfo = message.getPlayerTakeTurnInfo();//unwrap the data to determine what kind of kind of turn the player can take
				
				PlayerTakeTurnInfo.PlayerMoveType playerMoveType = playerTakeTurnInfo.getPlayerMoveType();
				int minimumBet = playerTakeTurnInfo.getMinimumBet();//MIN RAISE or bet
				int playerStack = playerTakeTurnInfo.getCurrentPlayerStack();//MAX RAISE or bet
				
				runningGameGUI.setMinimumBet(minimumBet);
				runningGameGUI.setPlayerStack(playerStack);
				
				switch(playerMoveType)
				{
				case BET:
				{
					runningGameGUI.enableBETGameMoves();
					break;
				}
				case PRE_BET:
				{
					runningGameGUI.enablePRE_BETGameMoves();
					break;
				}
				case SUDDEN_DEATH:
				{
					runningGameGUI.enableSUDDEN_DEATHGameMoves();
					break;
				}
				default:
					break;
				
				}
				
				runningGameGUI.validateScrollBar();
				
				//create a switch with the three bet types to decide which game moves to unlock
				//output bets to the server will use the text input box
				
			}
			
			public void processDECLARE_WINNER(ServerToClientMessage message)
			{
				JOptionPane.showMessageDialog(runningGameGUI,
						   					  message.getMessage(),
						   					  "YOU LOSE",
						   					  JOptionPane.WARNING_MESSAGE);
			}
			
			public void processLOSE_TURN(ServerToClientMessage message)
			{
				runningGameGUI.disableGameMoves();
				JOptionPane.showMessageDialog(runningGameGUI,
	   					  message.getMessage(),
	   					  "Error",
	   					  JOptionPane.WARNING_MESSAGE);
			}
			
			private void processUPDATE_SPECTATOR_USERS(ServerToClientMessage message)
			{
				runningGameGUI.writeToUpdateSpectators(message.getMessage());
			}
			
			private void displayPlayerInfo(ArrayList<PlayerStateInfo> currentPlayersInThisPokerHand)
			{
				Iterator<PlayerStateInfo> playerListIterator = currentPlayersInThisPokerHand.listIterator();
				
				//System.out.println("remaining players:");
				
				 while(playerListIterator.hasNext())
				 {
					 PlayerStateInfo player = playerListIterator.next();
					 
					 if(player.isInGame())
					 {
						 System.out.println("Player: " + player.getPlayerName() + " chips: " + player.getPlayerStack() + " playerID: " + player.getPlayerID());
						 System.out.println("Is player in game? " + player.isInGame() + " Is player in round? " + player.isInRound());
						 
						 if(player.getPlayersMoveInfo() != null)
							 System.out.println("Players last move: " + player.getPlayersMoveInfo().getGameMove());
						 
						 if(player.getLastWager() != 0)
							 System.out.println("Players last wager: " + player.getLastWager());
						 
						 //runningGameGUI.writeToChatbox((player.getPocketCards().size()));
						 System.out.println(player.getPocketCards());
						 //System.out.println(player.getPocketCards().size());
						 //player.showPlayerPocketCards();
					 }
				 }
			}
			
			public void updatePlayerPanels(ArrayList<PlayerStateInfo> players, ArrayList<PlayerInfoPanel> playerPanels, String currentGamePhase)
			{
				System.out.println("updating the player panels. we have " + players.size() + " players in the game");
				
				
				for(int i = 0; i < playerPanels.size(); i ++)
				{
					PlayerInfoPanel playerPanel = playerPanels.get(i);
					
					PlayerStateInfo player = getPlayerInfoForThisPanelPosition(players, i);
					
					if(player != null && player.isInGame())
					{
						playerPanel.setPlayerName(player.getPlayerName());
						playerPanel.setPlayerStack(String.valueOf(player.getPlayerStack()));
						
						updatePlayerCardPanel(playerPanel, player, currentGamePhase);
						//check to see if the cards that are being sent are the players own cards by matching the names.
						//if they match, show the face values, otherwise show the backs
						/*
						if(player.getPlayerName().equals(runningGameGUI.getUserName()))
						{
							generateCardImage(playerPanel.getPlayerHoleCard1Label(), player.getPocketCards().get(0));
							generateCardImage(playerPanel.getPlayerHoleCard2Label(), player.getPocketCards().get(1));
							
							
							//playerPanel.setPlayerHoleCard1(player.getPocketCards().get(0).toString());
							//playerPanel.setPlayerHoleCard2(player.getPocketCards().get(1).toString());
						}
						else
						{
							generateBackCardImage(playerPanel.getPlayerHoleCard1Label());
							generateBackCardImage(playerPanel.getPlayerHoleCard2Label());
							
							
							//playerPanel.setPlayerHoleCard1("blankcard1");
							//playerPanel.setPlayerHoleCard2("blankcard2");
						}
						*/
						updatePlayerMoveInfo(playerPanel, player);
						
						/*
						PlayersMoveInfo playersMoveInfo = player.getPlayersMoveInfo();
						
						
						
						if(playersMoveInfo != null)
						{
							//System.out.println("now updating " +player.getPlayerName()+ " " + "gamemove");
							
							//System.out.println("the game move was " + playersMoveInfo.getGameMove().toString() + " with a value of " + player.getLastWager());
							
							
							
							if(player.isInRound())
							{
								String gameMove = playersMoveInfo.getGameMove().toString();
								
								if(player.getLastWager() > 0)
								{
									gameMove = gameMove + " " + String.valueOf(player.getLastWager());
								}
								
								playerPanel.setPlayerGameMove(gameMove);
							}
							else
								playerPanel.setPlayerGameMove("FOLD");
								
						}
						else
							playerPanel.setPlayerGameMove("THINKING");
							*/
					}
					else
					{
						playerPanel.setPlayerName("not in game");
						playerPanel.setPlayerStack("");
						playerPanel.setPlayerHoleCard1("");
						playerPanel.setPlayerHoleCard2("");
						playerPanel.setPlayerGameMove("");
						
						playerPanel.getPlayerHoleCard1Label().setIcon(null);
						//(new ImageIcon(filePath); );
						playerPanel.getPlayerHoleCard2Label().setIcon(null);
					}
				}
			}
			
			public PlayerStateInfo getPlayerInfoForThisPanelPosition(ArrayList<PlayerStateInfo> players, int position)
			{
				Iterator<PlayerStateInfo> playerListIterator = players.listIterator();
				
				//System.out.println("remaining players:");
				
				 while(playerListIterator.hasNext())
				 {
					 PlayerStateInfo playerStateInfo = playerListIterator.next();
					 
					 if(playerStateInfo.getPlayerID() == position)
					 {
						// System.out.println("found player position for " + playerStateInfo.getPlayerID() + ". removing from list and returning the ref");
						 playerListIterator.remove();
						 return playerStateInfo;
					 }
				 }
				
				//System.out.println("the player position was not found. returning null");
				return null;
			}
			
			public void updateCommunityCardPanel(ArrayList<Card> communityCards, ArrayList<JLabel> communityCardPanels)
			{
				int i;
				//draw the community cards that are sent from server...
				for(i = 0; i < communityCards.size(); i++)
				{
					System.out.println("drawing community card from server");
					JLabel cardPanel = communityCardPanels.get(i);
					
					//try
					//{
						Card card = communityCards.get(i);
						cardPanel.setText(card.toString());
						
						generateCardImage(cardPanel, card);
					//}
					//catch(IndexOutOfBoundsException e)
					//{
					//	System.out.println("card is null");
						//cardPanel.setText("");
					//}
				}
				
				for(int j = i; j < communityCardPanels.size(); j++)
				{
					System.out.println("drawing blank cards from server");
					JLabel cardPanel = communityCardPanels.get(j);
					//System.out.println("card is null");
					//cardPanel.setText("blank");
					generateBackCardImage(cardPanel);
				}
				
				//and fill the remaining empty spaces with blank cards
				/*
				for(int j = i; j < communityCardPanels.size()-communityCards.size(); j++)
				{
					System.out.println("drawing blank cards from server");
					JLabel cardPanel = communityCardPanels.get(j);
					//System.out.println("card is null");
					//cardPanel.setText("blank");
					generateBackCardImage(cardPanel);
				}
				*/
			}
			
			public void generateBackCardImage(JLabel cardPanel)
			{
				ImageIcon cardImage = new ImageIcon("card/backCard.png");
				cardPanel.setIcon(cardImage);
			}
			
			public void generateCardImage(JLabel cardPanel, Card card)
			{
				String filePath = "card/" + card.getSuite().toString() + card.getRank().toString() + ".png";
				
				ImageIcon cardImage = new ImageIcon(filePath);
				cardPanel.setIcon(cardImage);
			}
			
			public void updatePlayerMoveInfo(PlayerInfoPanel playerPanel, PlayerStateInfo player)
			{
				PlayersMoveInfo playersMoveInfo = player.getPlayersMoveInfo();
				
				if(playersMoveInfo != null)
				{
					//System.out.println("now updating " +player.getPlayerName()+ " " + "gamemove");
					
					//System.out.println("the game move was " + playersMoveInfo.getGameMove().toString() + " with a value of " + player.getLastWager());
					
					
					
					if(player.isInRound())
					{
						String gameMove = playersMoveInfo.getGameMove().toString();
						
						if(player.getLastWager() > 0)
						{
							gameMove = gameMove + " " + String.valueOf(player.getLastWager());
						}
						
						playerPanel.setPlayerGameMove(gameMove);
					}
					else
						playerPanel.setPlayerGameMove("FOLD");
						
				}
				else
					playerPanel.setPlayerGameMove("THINKING");
			}
			
			public void updatePlayerCardPanel(PlayerInfoPanel playerPanel, PlayerStateInfo player, String currentGamePhase)
			{
				if(player.getPlayerName().equals(runningGameGUI.getUserName()) || currentGamePhase.equals("THE SHOWDOWN") || currentGamePhase.equals("GAME OVER"))
				{
					generateCardImage(playerPanel.getPlayerHoleCard1Label(), player.getPocketCards().get(0));
					generateCardImage(playerPanel.getPlayerHoleCard2Label(), player.getPocketCards().get(1));
					
					
					//playerPanel.setPlayerHoleCard1(player.getPocketCards().get(0).toString());
					//playerPanel.setPlayerHoleCard2(player.getPocketCards().get(1).toString());
				}
				else
				{
					generateBackCardImage(playerPanel.getPlayerHoleCard1Label());
					generateBackCardImage(playerPanel.getPlayerHoleCard2Label());
					
					
					//playerPanel.setPlayerHoleCard1("blankcard1");
					//playerPanel.setPlayerHoleCard2("blankcard2");
				}
			}

}
