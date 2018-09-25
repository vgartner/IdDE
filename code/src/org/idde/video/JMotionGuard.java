package org.idde.video;

//Enrique Garc�a Ceja
//www.tareitas.net.ms


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class JMotionGuard extends JFrame {
	
	private DeviceDialog deviceDialog;
	private String name, locator;
	
	private ActionsDialog actionsDialog;
	private JButton adOk, adCancel;
	
	private ToolBar toolBar;
	private JMenuItem miConfigure,miActions, miAbout;
	private JButton buttonStart;
	private JLabel labelStatus;
		
	private static VideoPanel videoPanel;
	
//	private BottomBar bottomBar;
	private JSlider sliderSensitivity;
		
	//**Constructor
	public JMotionGuard()
	{
		super("IdDE Video Capturing");
		
		//device dialog
		deviceDialog = new DeviceDialog();
		name = deviceDialog.getName();
		locator = deviceDialog.getLocator();
		deviceDialog = null;
				
		Container c = getContentPane();
		c.setLayout( new BorderLayout() );
				
		//initialize toolBar
//		MenuManager mm = new MenuManager();
//
//		toolBar = new ToolBar( mm );
//		setJMenuBar( toolBar );
//
//		miConfigure	= toolBar.getMiConfigure();
//		miActions	= toolBar.getMiActions();
//		miAbout		= toolBar.getMiAbout();
				
						
		//initialize videoPanel
		videoPanel = new VideoPanel( name,locator );
		c.add( videoPanel, BorderLayout.NORTH );
		videoPanel.setImagePath( new java.io.File(".") );
		videoPanel.setDelay( 5000 );
		
		
		//initialize south panel
//		bottomBar = new BottomBar();
//		c.add( bottomBar, BorderLayout.CENTER );
//		sliderSensitivity = bottomBar.getSliderSensitivity();
//		sliderSensitivity.addChangeListener( new SliderManager() );
//		buttonStart = bottomBar.getButtonStart();
////		buttonStart.addActionListener( mm );
//		labelStatus = bottomBar.getLabelStatus();
		
		//dialogs
//		actionsDialog = new ActionsDialog();
//		adOk = actionsDialog.getButtonOk();
//		adOk.addActionListener(mm);
//		adCancel = actionsDialog.getButtonCancel();
//		adCancel.addActionListener(mm);
				
										
		pack();
		setVisible( true );
		
//		videoPanel.setLabelAlert( bottomBar.getLabelWarning() );
		videoPanel.startSensing();
				
	}//**end of Constructor
	
		
	//internal class
	class MenuManager implements ActionListener {
		
		public void actionPerformed( ActionEvent evt )
		{
			//Start
			if( evt.getSource() == buttonStart )
			{
				if( buttonStart.getText() == "Start" )
				{
					miActions.setEnabled( false );
					miConfigure.setEnabled( false );
					labelStatus.setText( "Monitoring..." );
					buttonStart.setText( "Stop" );
					videoPanel.setRecord(true);
					videoPanel.setDelayExpired(false);					
				}
				else
				{
					miActions.setEnabled( true );
					miConfigure.setEnabled( true );
					labelStatus.setText( "Stopped" );
					buttonStart.setText( "Start" );
					videoPanel.setRecord(false);

				}
			}
			//actionDialog buttons
			else if( evt.getSource() == adOk )
			{
				if( actionsDialog.getCbImage().isSelected() )
					{
						videoPanel.setSaveImage(true);
					}	
					else
					{
						videoPanel.setSaveImage(false);
					}
					
				if( actionsDialog.getCbSound().isSelected() )
					{
						videoPanel.setPlaySound(true);
					}	
					else
					{
						videoPanel.setPlaySound(false);
					}
				
				videoPanel.setImagePath( actionsDialog.getImagePath() );
				int delay = Integer.parseInt( actionsDialog.getTfDelay().getText() );
				videoPanel.setDelay( delay );
				videoPanel.setSoundPath( actionsDialog.getSoundPath() );
				actionsDialog.hide();
			}
			else if( evt.getSource() == adCancel )
			{
				actionsDialog.hide();
			}
			
			else if( evt.getSource() == miAbout )
			{
				Icon icon = new ImageIcon("hechoMex.jpg");
					
				String str = "JMotionGuard\n\nEnrique Garc�a Ceja\nToluca,"+
				"M�xico\n\nwww.tareitas.net.ms";
				JOptionPane.showMessageDialog(null,str,"About..",
				JOptionPane.PLAIN_MESSAGE,icon);
			}
			else if( evt.getSource() == miActions )
			{
				actionsDialog.show();
			}
			
		}
		
	}//end of internal class MenuManager
	
	
	
	//internal listener class for the JSlider.
	class SliderManager implements ChangeListener
	{		
		public void stateChanged(ChangeEvent e)
		{	        
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting())
	        {
	            int sens = (int)source.getValue();
	            videoPanel.getCompare().setSensitivity( sens+1 );
	        }
    	}
	}//end of internal listener class	
	

//**main
public static void main( String args[] )	
{
	JMotionGuard jmg = new JMotionGuard();
	
	jmg.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
      videoPanel.closePlayer();
      System.exit(0);}});
	
}//**end of main
	
	
}//end of class JMotionGuard