package org.idde.video;



import javax.swing.*;
import java.awt.event.*;


public class ToolBar extends JMenuBar {
	
	private JMenuItem miConfigure,miActions,miAbout;
			
	//Constructor
	public ToolBar( ActionListener listener )
	{
		miConfigure = new JMenuItem("Configure");
		miConfigure.addActionListener( listener );
		add( miConfigure );
		
		
		miActions = new JMenuItem("Actions");
		miActions.addActionListener( listener );
		add( miActions );
				
		miAbout = new JMenuItem("About");
		miAbout.addActionListener( listener );
		add( miAbout );
		
		
				
	}//end of Constructor
	
	
	
	//***Accesors
	public JMenuItem getMiConfigure(){
		return miConfigure;
	}
	
	public JMenuItem getMiActions(){
		return miActions;
	}
	
	public JMenuItem getMiAbout(){
		return miAbout;
	}
		
	
}//end of class ToolBar.