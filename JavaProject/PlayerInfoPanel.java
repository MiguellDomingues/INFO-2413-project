import javax.swing.JLabel;


public class PlayerInfoPanel 
{
	JLabel playerStack;
	JLabel playerName;
	JLabel playerHoleCard1;
	JLabel playerHoleCard2;
	JLabel gameMove;
	
	
	public PlayerInfoPanel(JLabel playerStack, JLabel playerName,
						   JLabel playerHoleCard1, JLabel playerHoleCard2, 
						   JLabel gameMove) 
	{
		
		this.playerStack = playerStack;
		this.playerName = playerName;
		this.playerHoleCard1 = playerHoleCard1;
		this.playerHoleCard2 = playerHoleCard2;
		this.gameMove = gameMove;
	}
	
	public JLabel getPlayerStackLabel() 
	{
		return playerStack;
	}

	public JLabel getPlayerNameLabel() 
	{
		return playerName;
	}


	public JLabel getPlayerHoleCard1Label() 
	{
		return playerHoleCard1;
	}


	public JLabel getPlayerHoleCard2Label() 
	{
		return playerHoleCard2;
	}


	public JLabel getGameMoveLabel() 
	{
		return gameMove;
	}
	
	public void setPlayerStack(String value) 
	{
		playerStack.setText("Stack: " + value);
	}

	public void setPlayerName(String name) 
	{
		playerName.setText("Name: " + name);
	}
	
	public void setPlayerHoleCard1(String value) 
	{
		playerHoleCard1.setText(value);
	}
	
	public void setPlayerHoleCard2(String value) 
	{
		playerHoleCard2.setText(value);
	}
	
	public void setPlayerGameMove(String move) 
	{
		gameMove.setText(move);
	}
	
	
	
	
	
	
}
