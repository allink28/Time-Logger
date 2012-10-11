package allen.timelog;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

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
	
	private JPanel controls = new JPanel();
	private JButton save = new JButton("Save");
	Properties prop = new Properties();
	private double timeRemaining = 0; //Measured in minutes
	private byte items = 0;
	private JLabel timeText = new JLabel("Time:");
	private JLabel timeRemainingLabel;
	private JButton reset = new JButton("Reset");
	private JButton clear = new JButton("Clear Text");
	
	DecimalFormat df = new DecimalFormat("#.##");
	private JButton start = new JButton("Start");
	private ButtonListener listener = new ButtonListener();
	private long startTime = 0;
	private long endTime;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private JTextArea displayText = new JTextArea(20, 20);
	
	private JPanel bottomContainer = new JPanel();
	private JLabel tfEnterTimeLabel = new JLabel("Subtract time in minutes:");
	private JTextField textField = new JTextField("",7);	
	private JLabel itemText = new JLabel("  Items:");
	private JButton plusI = new JButton("+");
	private JLabel itemCount = new JLabel(""+items);
	private JButton minusI = new JButton("-");
	
    // ----------------------------------------------------------
    /**
     * Create a new MenuPanel object.
     */
    public MenuPanel(){
    	setLayout(new BorderLayout());
        setPreferredSize(new Dimension(451, 395));//500, 400
        setBackground(Color.DARK_GRAY);                
                        
        //-----------------North (Controls) ------------------------------------- 
        controls.setBackground(Color.GRAY);
        timeRemainingLabel = new JLabel();
        updateTime(0);
        controls.add(timeText);
        controls.add(timeRemainingLabel);
        controls.add(save);        
        save.addActionListener(new ActionListener(){ //Anonymous listener for Save button
        	public void actionPerformed(ActionEvent e) {
        		FileOutputStream fos = null;
        		if(start.getText().equals("Stop ")){
        			displayText.append("WARNING: Timer is still running! Stop timer before saving to keep track of accumulated time.\n");
        		}
        		try {
        			fos = new FileOutputStream("time.properties");
            		prop.setProperty("time", Double.toString(timeRemaining));
            		prop.setProperty("items", Byte.toString(items));
            		//save properties to project root folder
            		prop.store(fos, null);
            		displayText.append("Saved.\n");
            	} catch (IOException ex) {
            		displayText.append("Couldn't save.\n");
                }finally {
                	try {
        				fos.close();
        			} catch (IOException e1) {
        				displayText.append("Could'nt close output stream\n");
        			}
        		}
        	}
        });
        controls.add(reset);
        reset.addActionListener(new ActionListener(){ //Anonymous listener for Reset button
        	public void actionPerformed(ActionEvent e) {
        		timeRemaining = 480; //8 hours, in minutes
        		updateTime(0);
        		items = 0;
        		itemCount.setText(""+items);        		
        		displayText.append("\nTime reset to 8 hours and items reset to 0, at "+
        				dateFormat.format(Calendar.getInstance().getTime()) +"\n");
        	}
        });
        controls.add(clear);
        clear.addActionListener(new ActionListener(){ //Anonymous listener for Clear
        	public void actionPerformed(ActionEvent e) {
        		displayText.setText("");
        	}
        });        
        add(controls, BorderLayout.NORTH);
        
        
        //--------------------- West (Start/Stop button) ---------------------------------
        add(start, BorderLayout.WEST);
        start.addActionListener(listener);        
        
      //--------------------- Center (textfield) --------------------------------------
        displayText.setLineWrap(true);
        displayText.setName( "displayText" );
        displayText.setEditable( false );
        displayText.setText( "Welcome!\nAdd a negative number to increase the countdown timer.\n" );
        displayText.append("\nProgram opened at: "+
    			dateFormat.format(Calendar.getInstance().getTime()) +"\n");
        JScrollPane scrollingResult = new JScrollPane(displayText);
        scrollingResult.setAutoscrolls(true);
        add(scrollingResult, BorderLayout.CENTER);
        
      //--------------------- South (Bottom Container/Adjustments) ---------------------
        textField.addActionListener(new ActionListener() {//Anonymous listener for textfield that Adds/Substracts time
            public void actionPerformed(ActionEvent e) {
            	try{
            		int minutes = Integer.parseInt(textField.getText());
            		updateTime(minutes);
            		displayText.append("Time Remaining: "+timeRemaining+" minutes.\n");
            	}catch(NumberFormatException nfe){
					displayText.append("Only enter numbers in minutes that you wish to have subtracted from the remaining time."
									+"\nNo decimal minutes.\n");   
            	}            		            	
                textField.setText("");
            }
        });
        plusI.addActionListener(new ActionListener() {//Anonymous listener for textfield that Adds/Substracts time
            public void actionPerformed(ActionEvent e) {
            	itemCount.setText(""+ ++items);
            }
        });
        minusI.addActionListener(new ActionListener() {//Anonymous listener for textfield that Adds/Substracts time
            public void actionPerformed(ActionEvent e) {
            	itemCount.setText(""+ --items);
            }
        });
        add(bottomContainer, BorderLayout.SOUTH);
        bottomContainer.setBackground(Color.GRAY);
        bottomContainer.add(tfEnterTimeLabel);
        bottomContainer.add(textField);
        bottomContainer.add(itemText);
        bottomContainer.add(plusI);
        bottomContainer.add(itemCount);
        bottomContainer.add(minusI);
        
      //--------------------- Loading --------------------------------------
        FileInputStream fin = null;
    	try{		
    		fin = new FileInputStream("time.properties");
    		prop.load(fin);
    		timeRemaining = Double.parseDouble(prop.getProperty("time"));
    		updateTime(0);
    		items = Byte.parseByte(prop.getProperty("items"));
    		itemCount.setText(""+items);
    		
    		//displayText.append("Succesfully loaded savefile!\n");
    	}
    	catch(IOException e){
    		displayText.append("No time.properties file found.\n");    		
		} finally {
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e1) {
					displayText.append("Could'nt close input stream\n");
				}
			}
		}
    	
    }
    
    /**
     * Update the time remaining by subtracting the given time, then displaying the
     * the result on the timeRemainingLabel.
     * @param minutes Number of minutes to be subtracted from time remaining.
     */
    public void updateTime(double minutes){
    	timeRemaining-=minutes;
    	double m = Math.abs(timeRemaining%60);
    	StringBuilder sb = new StringBuilder();
    	if(m < 10){
    		sb.append('0');
    	}
    	sb.append(df.format(m));
    	if (timeRemaining<0){
    		timeRemainingLabel.setText("-"+(int)(timeRemaining/60)+":"+sb);
    	}
    	else{
    		timeRemainingLabel.setText((int)(timeRemaining/60)+":"+sb);
    	}
    }

    /**
     * @param g The graphic context.
     */
    public void paintComponent(Graphics g) {
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
            	start.setText("Stop ");
            	startTime = System.nanoTime();
            	displayText.append("Started at: "+
            			dateFormat.format(Calendar.getInstance().getTime()) +"\n");            	
            }
            else{            	
            	endTime = System.nanoTime();
            	double seconds = (double)(endTime-startTime) / 1000000000.0;            	
            	displayText.append("Elapsed time: "+ Math.round(seconds) + " seconds or "+df.format(seconds/60)+" minutes\n");
            	start.setText("Start");
            	startTime = 0;
            	
            	updateTime(seconds/60);
            }
        }
    }
}