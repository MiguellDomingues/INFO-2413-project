import java.io.IOException;


public abstract class ClientState 
{

		public abstract void readMessage(ServerToClientMessage message) throws IOException;
		
}
		
		
