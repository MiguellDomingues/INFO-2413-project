import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ConnectedServer 
{
	public Socket serverSocket;
	public String hostName;
	public String clientName;
	public int port;
	
	MainLobbyGUI gui;
	
	//BufferedReader serverReader;
	//PrintWriter serverWriter;
	
	 private ObjectInputStream serverReader;     //reading  
	 private ObjectOutputStream serverWriter;	//writing
	
	 
	 ObjectOutputStream outToClient;
	 ObjectInputStream inFromClient;
	 
	public ConnectedServer(Socket serverSocket, MainLobbyGUI gui)
	{
		this.serverSocket = serverSocket;
		this.hostName = serverSocket.getLocalAddress().toString();
		this.port = serverSocket.getPort();
		clientName = "unnamed client";
		this.gui = gui;
		
		/*
		
		try {
			 outToClient = new ObjectOutputStream(serverSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
        try {
			 inFromClient = new ObjectInputStream(serverSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		
			try {
				serverWriter = new ObjectOutputStream(serverSocket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				gui.writeToTextArea("exception creating input stream");
			}
			
			try {
				serverReader = new ObjectInputStream(serverSocket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				gui.writeToTextArea("exception creating output stream");
			}
		
		
	}
	
	public void closeServerConnection() throws IOException
	{
		//server.shutdownOutput();
		serverWriter.close();
		serverReader.close();
		serverSocket.close();
	}
	
	public void WriteToServer(ClientToServerMessage message) throws IOException
	{
		serverWriter.writeObject(message);
	}
	
	public ServerToClientMessage ReadFromServer() throws IOException, ClassNotFoundException
	{
		return (ServerToClientMessage)serverReader.readObject();
		
	}
		
		
}
