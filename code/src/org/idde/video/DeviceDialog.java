package org.idde.video;


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class DeviceDialog extends JDialog implements ActionListener {
	
	
	private JTextField tfName, tfLocator;
	private JButton bOk, bCancel;
	private String name = "vfw:Microsoft WDM Image Capture (Win32):0";
	private String locator = "vfw://0";
	
	public DeviceDialog()
	{
		setModal(true);
		setTitle("Type your device properties");
		setLayout( new GridLayout(3,2,5,5) );
		
		add( new JLabel("Name:") );
		tfName = new JTextField( name );
		add( tfName );
		
		add( new JLabel("Locator:") );
		tfLocator = new JTextField( locator );
		add( tfLocator );
		
		bOk = new JButton("OK");
		bOk.addActionListener(this);
		add( bOk );
		
		bCancel = new JButton("Cancel");
		bCancel.addActionListener(this);
		add( bCancel );
		
		pack();
		show();
	}
	
	public void actionPerformed( ActionEvent evt )
	{
		if( evt.getSource() == bCancel )
		{
			System.exit(0);
		}
		else if( evt.getSource() == bOk )
		{
			name = tfName.getText();
			locator = tfLocator.getText();
			hide();
		}
		
		
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getLocator()
	{
		return locator;
	}
	
	
}