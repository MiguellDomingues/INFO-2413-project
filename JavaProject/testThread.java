
public class testThread implements Runnable
{

	@Override
	public void run() 
	{
		
		     System.out.println("another theread is running");
		     try
		     {
		     Thread.sleep(3000);
		     }
		     catch(Exception e){}
		
		
	}

}
