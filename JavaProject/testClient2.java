import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



public class testClient2
{

	public static void main(String[] args) 
	{
		String hostname = "localhost";
		  int port = 19999;
		  Socket socketClient = null;
		  
		  //PrintWriter out = null;
		  
		  MainLobbyGUI a = new MainLobbyGUI(hostname, port, socketClient);
		  
		 
		  
		 
		

	}

}
