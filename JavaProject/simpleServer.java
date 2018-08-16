
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/* IN ORDER FOR THE PROGRAM TO WORK ON DIFFERENT MACHINES:
 * - run the server on laptop
 * - run ipconfig in command prompt
 * - get the default gateway address
 * - the clients have to use that default gateway ip address as the HOST NAME.
 * - port number will always remain the same
 * 
 */


public class simpleServer 
{

	public static void main(String[] args) throws UnknownHostException
	{
		  System.out.println(InetAddress.getLocalHost()); 
		  ServerSocket serverSocket;
		  int port = 19999;
		  ConnectedClient connectedClient;
		  
		  ConnectionListenerThread a = new ConnectionListenerThread();
		  
		  //a.setDaemon(true);
		  //a.setPriority(Thread.MIN_PRIORITY);
		  
		  a.start();
		  
		  
		
	}

}
