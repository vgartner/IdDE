package org.idde.video;


import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.util.*;
import javax.media.control.*;
import javax.media.protocol.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import com.sun.image.codec.jpeg.*;


public class VideoPanel extends JPanel implements Runnable {
	
	public static Player player = null;
	private CaptureDeviceInfo di = null;
	private MediaLocator ml = null;
	private Buffer buf = null;
	private VideoFormat vf = null;
	
	private BufferToImage btoi = null;
  	private Image img = null;
	private Thread thread = null;
	
	private Compare compare = null;
	JLabel labelAlert = null;
	
	//flags
	private boolean record = false;
	private boolean saveImage = false;
	private boolean playSound = false;
	private boolean delayExpired = false;
	
	//paths
	private File imagePath = null;
	private File soundPath = null;
	
	//delay
	private int delay;
		
	//**Constructor
	public VideoPanel( String name, String locator )
	{
		setSize( 320,240 );
		//String str1 = "vfw:Logitech USB Video Camera:0";
	    //String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";
	    
	    di = CaptureDeviceManager.getDevice( name );
		ml = new MediaLocator( locator );
		
		try
		{
			player = Manager.createRealizedPlayer(ml);
		    player.start();
		    Component comp;
		    
		    if ((comp = player.getVisualComponent()) != null)
      	      add( comp );
		}
		catch (Exception e)
		{
      		e.printStackTrace();
    	}
			
	}//**end of Constructor
	
	
	//closePlayer()
	public void closePlayer()
	{
		stopSensing();
		player.close();
    	player.deallocate();
	}
	
	//startSensing()
	public void startSensing()
	{
		try{Thread.sleep(2000);}
	    catch(Exception e){}
		thread = new Thread(this);
		thread.start();
	}
	
	//stopSensing()
	public void stopSensing()
	{
		thread = null;		
	}
	
	//run()
	public void run()
	{
		Image jpg;
		
		// Grab a frame
      	FrameGrabbingControl fgc = (FrameGrabbingControl)
      	player.getControl("javax.media.control.FrameGrabbingControl");
      	buf = fgc.grabFrame();
      
      	// Convert it to an image
      	btoi = new BufferToImage((VideoFormat)buf.getFormat());
      	img = btoi.createImage(buf);
                    
      	compare = new Compare( img, 5 );
				
		while ( true )
      	{
      
          try{Thread.sleep(500);}
	      catch(Exception e){}      
	      
	      //Grab a frame
	      fgc = (FrameGrabbingControl)
	      player.getControl("javax.media.control.FrameGrabbingControl");
	      buf = fgc.grabFrame();
	      
	      // Convert it to an image
	      btoi = new BufferToImage((VideoFormat)buf.getFormat());
	      img = btoi.createImage(buf);
	            
	      //see if image has changed
	      if ( compare.compareWith( img ) )
	      {
	      	setLabelColor( Color.RED );
	      	
	      	
	      	if( record && !delayExpired )
	      	{
	      		//save image
		      	if( saveImage )
		      	{
		      		jpg = btoi.createImage( buf );
		      		new SaveJPG( jpg, imagePath ).start();
		      	}
		      	
		      	//play sound
		      	if( playSound )
		      	{
		      		new PlaySound( soundPath ).start();
		      	}
		      	new Delay( this,delay ).start();
		      	setDelayExpired(true);
	      	}
	     	      	
	      }
	      else
	      {
	      	setLabelColor( Color.BLACK );
	      }
	      
	      
	      	
        }//end of while
		
	}//end of run()
	
	//setLabelColor()
	public void setLabelColor( final Color c )
	{
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					labelAlert.setForeground( c );
				}
			}
			);
	}
	
	//**Mutators / Accesors
	public void setLabelAlert( JLabel l )
	{
		this.labelAlert = l;	
	}
	
	public Compare getCompare()
	{
		return this.compare;
	}
	
	public void setRecord( boolean b )
	{
		this.record = b;
	}
	
	public void setSaveImage( boolean b )
	{
		this.saveImage = b;
	}
	
	public void setPlaySound( boolean b )
	{
		this.playSound = b;
	}
	
	public void setImagePath( File f )
	{
		imagePath = f;
	}
	
	public void setSoundPath( File f )
	{
		soundPath = f;
	}
	
	public void setDelayExpired( boolean b )
	{
		delayExpired = b;
	}
	
	public void setDelay( int n )
	{
		delay = n;
	}
	
}//end of class VideoPanel