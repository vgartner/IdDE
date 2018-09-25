package org.idde.video;


import  sun.audio.*;
import  java.io.*;

public class PlaySound extends Thread {
	
	File f;
			
	//Constructor
	public PlaySound( File f )
	{
		this.f = f;
		
	}//end Constructor
	
	//run()
	public void run()
	{
		try
		{
			InputStream in = new FileInputStream( f );
			AudioStream as = new AudioStream(in);         
			AudioPlayer.player.start(as);            
			//AudioPlayer.player.stop(as); 
		}
		catch( Exception e ){e.printStackTrace();}
	
	}//end of run()
	
	
}//end of class