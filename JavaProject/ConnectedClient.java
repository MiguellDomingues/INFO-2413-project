import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ConnectedClient
{
	public Socket clientSocket;
	public String hostName;
	public String clientID;
	public String clientName;
	public int port;
	
	//BufferedReader readFromClient;
	//PrintWriter writeToClient;
	
	 ObjectInputStream readFromClient;     //reading  
	 ObjectOutputStream writeToClient;	//writing
	
	public ConnectedClient(Socket clientSocket, String clientID)
	{
		this.clientSocket = clientSocket;
		this.hostName = clientSocket.getLocalAddress().toString();
		this.clientID = clientID;
		this.port = clientSocket.getPort();
		clientName = "unnamed client";
		
		/**
		 * 
		 *  private ObjectInputStream sInput;     //reading  
		 *	private ObjectOutputStream sOutput;   //writing
	 	 * 
		 * sInput  = new ObjectInputStream(socket.getInputStream()); //reading. put in thread
           sOutput = new ObjectOutputStream(socket.getOutputStream()); //writing. put anywhere
		 * 
		 * sOutput.writeObject(msg); //message is the message object we are sending
		 * (need to cast message to appropriate type) sInput.readObject();

		 * 
		 */
		
		
		
		try
		{
			writeToClient = new ObjectOutputStream(clientSocket.getOutputStream());
			readFromClient = new ObjectInputStream(clientSocket.getInputStream());
			
		}
		catch(IOException e){System.out.println("error creating input/output streams for a connected client");
		e.printStackTrace();
		}
	}
	
	public void WriteToClient(ServerToClientMessage message) throws IOException
	{
		writeToClient.writeObject(message);
	}
	
	public ClientToServerMessage ReadFromClient() throws IOException, ClassNotFoundException
	{
		return (ClientToServerMessage)readFromClient.readObject();
		
	}
	
	public synchronized void closeClient() throws IOException
	{
		clientSocket.close();
		readFromClient.close();
		writeToClient.close();
	}
	
	public ObjectInputStream getReader()
	{
		return readFromClient;
	}
	
	public ObjectOutputStream getWriter()
	{
		return writeToClient;
	}

	public Socket getClient() {
		return clientSocket;
	}

	public void setClient(Socket client) {
		this.clientSocket = client;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
