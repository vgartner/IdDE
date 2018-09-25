package org.idde.video;

//class to compare 2 images

import java.awt.image.*;
import java.awt.*;
import javax.swing.*;

public class Compare extends JPanel {
	
	private static final int K = 130000; //coeficient of change
	private int change;
	private int sensitivity;
	
	//Constructor
	public Compare( Image img, int sens )
	{
		setSensitivity( sens );
	
		MediaTracker tracker = new MediaTracker(this);
		
		tracker.addImage( img,1 );
		
		try
		{
			tracker.waitForID(1,100);
		}
		catch( InterruptedException ie ){}
				
		int width = img.getWidth(this);
		int height = img.getHeight(this);
					
		int pixels[] = new int[width*height];
    	PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
	    
	    try
	    {
	      pg.grabPixels();
	    }
	    catch (InterruptedException ie){}
		
		int num = 0; //sum of r g b values of each pixel
				
		for ( int i=0; i<pixels.length; i++ )
		{
			int  c = pixels[i];     // or  pixels[x * width + y]
		    int  red = (c & 0x00ff0000) >> 16;
		    int  green = (c & 0x0000ff00) >> 8;
		    int  blue = c & 0x000000ff;
		    num += red+green+blue;
		}
		
		setChange( num );
	
	}//end of Constructor	
	
	//this returns true if actual image is different of the last one
	public boolean compareWith( Image img )
	{
		boolean res = false;
		
		MediaTracker tracker = new MediaTracker(this);
		
		tracker.addImage( img,1 );
		
		try
		{
			tracker.waitForID(1,100);
		}
		catch( InterruptedException ie ){}
				
		int width = img.getWidth(this);
		int height = img.getHeight(this);
					
		int pixels[] = new int[width*height];
    	PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
	    
	    try
	    {
	      pg.grabPixels();
	    }
	    catch (InterruptedException ie){}
		
		int num = 0;
				
		for ( int i=0; i<pixels.length; i++ )
		{
			int  c = pixels[i];     // or  pixels[x * width + y]
		    int  red = (c & 0x00ff0000) >> 16;
		    int  green = (c & 0x0000ff00) >> 8;
		    int  blue = c & 0x000000ff;
		    num += red+green+blue;
		}
		
		if ( Math.abs(getChange() - num) > K * getSensitivity() )
			res = true;
		
		setChange( num );
		
		return res;
		
	}//fin CompararCon()
	
	
	//**Accesors / Mutators
	public int getChange()
	{
		return this.change;
	}
	
	public void setChange( int c )
	{
		this.change = c;
	}
	
	public int getSensitivity()
	{
		return this.sensitivity;
	}
	
	public void setSensitivity( int s )
	{
		this.sensitivity = s;
	}

	
}//end of class Compare