package org.idde.video;


import javax.swing.*;
import java.awt.*;

public class BottomBar extends JPanel {
	
	
	private JPanel panelControl, panelStatus;
	private JButton buttonStart;
	
	//JSlider
	private JSlider sliderSensitivity;
	private static final int MIN = 0;
	private static final int MAX = 50;
	
	private JLabel labelStatus;
	private JLabel labelWarning;
	
	
	//**Constructor
	public BottomBar()
	{
		
		setLayout( new BorderLayout() );
						
		panelControl = new JPanel();
		buttonStart = new JButton("Start");
		panelControl.add( buttonStart );
		
		sliderSensitivity = new JSlider( JSlider.HORIZONTAL, MIN,MAX,MIN );
		sliderSensitivity.setMajorTickSpacing(10);
        sliderSensitivity.setMinorTickSpacing(1);
        sliderSensitivity.setPaintTicks(true);
        sliderSensitivity.setPaintLabels(true);
        sliderSensitivity.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
		sliderSensitivity.setToolTipText("Sensitivity 0=more 50=less");
		
		panelControl.add( sliderSensitivity );
		
		add( panelControl,BorderLayout.NORTH );
		
		panelStatus = new JPanel();
		labelStatus = new JLabel("Stopped");
		panelStatus.add( labelStatus );
		labelWarning = new JLabel( "##############" );
		labelWarning.setForeground( Color.BLACK );
		panelStatus.add( labelWarning );
		
		add( panelStatus,BorderLayout.CENTER );
		
	}//end of Constructor
	
	
	
	//**Accesors
	public JButton getButtonStart()
	{
		return this.buttonStart;
	}
	
	public JSlider getSliderSensitivity()
	{
		return this.sliderSensitivity;
	}
	
	public JLabel getLabelStatus()
	{
		return this.labelStatus;
	}
	
	public JLabel getLabelWarning()
	{
		return this.labelWarning;
	}
	
	
}//end of class BottomBar