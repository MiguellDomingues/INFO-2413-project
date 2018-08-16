import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class GameCountdownPanel extends JDialog
{
	
	
	int a = 5;
	String count;
	Timer timer;
	JLabel y;
	
	public GameCountdownPanel() throws InterruptedException
	{
		JPanel x = new JPanel();
		 y = new JLabel("aaa");
		
		x.add(y);
		
		setContentPane(x);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pack();
		
		 Timer timer = new Timer(1000, new MyTimerActionListener());

		    timer.start();
		
		 //start(y);
		
		 setVisible(true);
		
	}
	
	
	
	
	class MyTimerActionListener implements ActionListener 
	{
		  public void actionPerformed(ActionEvent e) 
		  {
			  if(a == 0)
			  {
				  dispose();
			  }
			  else
			  {
				  y.setText(Integer.toString(a--));
			  }
		  

		  }
	}
 


	
	
}

