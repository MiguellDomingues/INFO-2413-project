import java.io.IOException;


public abstract class ServerState 
{
	public abstract void readMessage(ClientToServerMessage message) throws IOException, ClassNotFoundException;
	
	public void processLOGOFF(ClientToServerMessage message) throws IOException
	{
		throw new IOException();
	}

}

