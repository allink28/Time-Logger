package allen.timelog;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// -------------------------------------------------------------------------
/**
 *  Panel for the program
 *
 *  @author Allen
 *  @version Jun 26, 2012
 */

public class MenuPanel extends JPanel
{
	private static final long serialVersionUID = -3175089802389935233L;
	private JPanel bottomContainer = new JPanel();
	private JLabel tfEnterTimeLabel = new JLabel("Subtract time in minutes:");
	private JTextField textField = new JTextField("",10);
	private JTextArea displayText = new JTextArea(20, 20);
	private JButton clear = new JButton("Clear Text");
	private ButtonListener listener = new ButtonListener();
	
	private JPanel controls = new JPanel();
	private double timeRemaining = 10;
	private JLabel timeRemainingLabel = new JLabel(timeRemaining+" Hours");
	private JButton reset = new JButton("Reset");	
	
	DecimalFormat df = new DecimalFormat("#.##");
	private JButton start = new JButton("Start");
	private long startTime = 0;
	private long endTime;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // ----------------------------------------------------------
    /**
     * Create a new MenuPanel object.
     */
    public MenuPanel(){
    	setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 400));
        setBackground(Color.DARK_GRAY);
                        
        controls.setBackground(Color.GRAY);
        controls.add(timeRemainingLabel);
        controls.add(reset);
        reset.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		timeRemaining = 10;
        		timeRemainingLabel.setText(df.format(timeRemaining)+" Hours");
        		displayText.append("Time reset to 10.\n");
        	}
        });
        add(controls, BorderLayout.NORTH);
                
        add(start, BorderLayout.WEST);
        start.addActionListener(listener);
        
        
        //Anonymous listener for the textfield that Adds/Substracts time
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try{
            		int minutes = Integer.parseInt(textField.getText());
            		timeRemaining-=(minutes/60.0);
            		displayText.append("Time Remaining: "+timeRemaining+" hours.\n");
                	timeRemainingLabel.setText(df.format(timeRemaining)+" Hours");
            	}catch(NumberFormatException nfe){
					displayText.append("Only enter numbers in minutes that you wish to have subtracted from the remaining time."
									+"\nNo decimal minutes.\n");   
            	}            		            	
                textField.setText("");
            }
        });
        
        clear.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		displayText.setText("");
        	}
        });
        add(bottomContainer, BorderLayout.SOUTH);
        bottomContainer.setBackground(Color.GRAY);
        bottomContainer.add(tfEnterTimeLabel);
        bottomContainer.add(textField);      
        bottomContainer.add(clear);
        
        displayText.setLineWrap(true);
        displayText.setName( "displayText" );
        displayText.setEditable( false );
        displayText.setText( "Welcome!\nAdd a negative number to increase the countdown timer.\n" );
        JScrollPane scrollingResult = new JScrollPane(displayText);
        scrollingResult.setAutoscrolls(true);
        add(scrollingResult, BorderLayout.CENTER);             
        
    }

    /**
     * Draws all the shapes displayed.
     * @param g The graphic context.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }
    
    /**
     * Inner listener class.
     */
    private class ButtonListener implements ActionListener
    {    	
        public void actionPerformed( ActionEvent arg0 )
        {
            if(startTime == 0){
            	start.setText("Stop");
            	startTime = System.nanoTime();
            	displayText.append("Started at: "+
            			dateFormat.format(Calendar.getInstance().getTime()) +"\n");            	
            }
            else{            	
            	endTime = System.nanoTime();
            	double seconds = (double)(endTime-startTime) / 1000000000.0;            	
            	displayText.append("Elapsed time: "+ seconds + " seconds or "+df.format(seconds/3600)+" hours\n");
            	start.setText("Start");
            	startTime = 0;
            	timeRemaining-=(seconds/3600);
            	timeRemainingLabel.setText(df.format(timeRemaining)+" Hours");
            }
        }
    }
}