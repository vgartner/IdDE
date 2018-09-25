package org.idde.video;



public class Delay extends Thread {
	
	VideoPanel vp;
	int delay;
	
	//Constructor
	public Delay( VideoPanel vp, int delay )
	{
		this.vp = vp;
		this.delay = delay;
		
	}//end of Constructor
	
	//run()
	public void run()
	{
		try
		{
			sleep( delay );
		}
		catch( InterruptedException ie ){}
			
		vp.setDelayExpired(false);
				
	}//end of run()
	
	
}//end of class