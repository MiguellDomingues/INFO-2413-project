import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class CreateHostGUI extends JPanel 
{
	JTextField numPlayersTF;
    JTextField gameNameTF;
    JTextField startingMoneyTF;
    
    public CreateHostGUI()
    {
    	gameNameTF = new JTextField(15);
    	numPlayersTF = new JTextField(3);
    	startingMoneyTF = new JTextField(5);
    	
    	
    	add(new JLabel("game name:"));
	    add(gameNameTF);
	    add(Box.createHorizontalStrut(15)); // a spacer
	    add(new JLabel("number of players:"));
	    add(numPlayersTF);
	    add(Box.createHorizontalStrut(15)); 
	    add(new JLabel("starting money:"));
	    add(startingMoneyTF);
	    
    }
    
    public String getGameName()
    {
    	return gameNameTF.getText();
    }
    
    
    
    public int getNumPlayers() throws NumberFormatException
    {
    	return Integer.parseInt(numPlayersTF.getText());
    }
    
   
    
   
    public int getStartingMoney() throws NumberFormatException
    {
    	return Integer.parseInt(startingMoneyTF.getText());
    }
    
   

}
