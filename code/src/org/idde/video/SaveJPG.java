package org.idde.video;


import java.awt.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;
import java.io.*;
import java.util.Date;

public class SaveJPG extends Thread {
	
	Image img;
	File f;
		
	//Constructor
	public SaveJPG( Image img, File f )
	{
		this.img = img;
		this.f = f;
		
	}//end Constructor
	
	
	public void run()
	{	
	
		Date date = new Date();
		String s = "\\"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds()+".jpg";
		
				
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = bi.createGraphics();
	    g2.drawImage(img, null, null);
	 
	    FileOutputStream out = null;
	    	    
	    System.out.println( s );
	    
	    try
	    { 
	      out = new FileOutputStream( f+s ); 
	    }
	    catch (java.io.FileNotFoundException io)
	    { 
	      System.out.println("File Not Found"); 
	    }
	    
	    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
	    param.setQuality(0.5f,false);
	    encoder.setJPEGEncodeParam(param);
	    
	    try 
	    { 
	      encoder.encode(bi); 
	      out.close(); 
	    }
	    catch (java.io.IOException io) 
	    {
	      System.out.println("IOException"); 
	    }
		
	}//end of run
	
}//end of class