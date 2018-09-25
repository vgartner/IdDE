package org.idde.video;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class ActionsDialog extends JDialog implements ActionListener {
	
	private JButton buttonOk, buttonCancel;
	private JButton browseImage, browseSound;
	private JTextField tfImage, tfSound, tfDelay;
	private JCheckBox cbImage, cbSound;
	private JFileChooser imagePath, soundPath;
	private File img = new File("."), sound = new File("saco.wav");
	
	
	//Constructor
	public ActionsDialog()
	{
		
		setTitle( "Actions" );
		setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		setLocationRelativeTo( null );
		setLayout( new GridLayout(4,1) );
		
		//file choosers
		imagePath = new JFileChooser();
		imagePath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
		soundPath = new JFileChooser();
		soundPath.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
		//image panel
		JPanel panelImage = new JPanel();
		panelImage.setBorder(BorderFactory.createTitledBorder(
                                 "ImageCapture"));
		
		cbImage = new JCheckBox( "Save Image" );
		panelImage.add( cbImage );
		JPanel p1 = new JPanel();
		p1.setLayout( new GridLayout(1,2) );
		
		tfImage = new JTextField();
		tfImage.setEnabled(false);
		browseImage = new JButton( "Browse.." );
		browseImage.addActionListener(this);
		p1.add( tfImage );
		p1.add( browseImage );
		panelImage.add( p1 );
				
		add( panelImage );
		
		
		//sound panel
		JPanel panelSound = new JPanel();
		panelSound.setToolTipText("Use only .wav files.");
		panelSound.setBorder(BorderFactory.createTitledBorder(
                                 "Play Sound"));
		
		cbSound = new JCheckBox( "Play Sound" );
		panelSound.add( cbSound );
		JPanel p2 = new JPanel();
		p2.setLayout( new GridLayout(1,2) );
		
		tfSound = new JTextField();
		tfSound.setText( "saco.wav" );
		tfSound.setEnabled(false);
		browseSound = new JButton( "Browse.." );
		browseSound.addActionListener(this);
		p2.add( tfSound );
		p2.add( browseSound );
		panelSound.add( p2 );
				
		add( panelSound );
		
		//delay panel
		JPanel panelDelay = new JPanel();
		panelDelay.setBorder(BorderFactory.createTitledBorder(
                                 "Delay between detections"));
		
		JPanel p3 = new JPanel();
		p3.setLayout( new GridLayout(1,2) );
		
		tfDelay = new JTextField();
		tfDelay.setText( "5000" );
		p3.add( new JLabel("1sec = 1000ms  ms:") );
		p3.add( tfDelay );
		panelDelay.add( p3 );
		add( panelDelay );
		
		
		//buttons
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout( new GridLayout(1,2,5,5) );
		buttonOk = new JButton( "Ok" );
		buttonCancel = new JButton( "Cancel" );
		panelButtons.add( buttonOk );
		panelButtons.add( buttonCancel );
		
				
		add( panelButtons );
		
		pack();
		hide();
	}
	
	//actionPerformed
	public void actionPerformed( ActionEvent evt )
	{
		if( evt.getSource() == browseImage )
		{
			if (imagePath.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{ 
		      img = imagePath.getSelectedFile();
		      tfImage.setText( img.toString() );
		    }
		}
		else if( evt.getSource() == browseSound )
		{
			if (soundPath.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{ 
		      sound = soundPath.getSelectedFile();
		      tfSound.setText( sound.toString() );
		    }
		}
		
		
	}//end of actionPerformed
	
	
	//**Accesors
	public File getImagePath()
	{
		return img;
	}
	
	public File getSoundPath()
	{
		return sound;
	}
	
	public JCheckBox getCbImage()
	{
		return cbImage;
	}
	
	public JCheckBox getCbSound()
	{
		return cbSound;
	}
	
	public JButton getButtonOk()
	{
		return buttonOk;
	}
	
	public JButton getButtonCancel()
	{
		return buttonCancel;
	}
	
	public JTextField getTfDelay()
	{
		return tfDelay;
	}
	
	
}//end of class