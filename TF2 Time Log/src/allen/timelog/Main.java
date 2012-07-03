package allen.timelog;

import javax.swing.JFrame;

// -------------------------------------------------------------------------
/**
 *  Main method for project
 *
 *  @author Allen
 *  @version Jun 26, 2012
 */

public class Main
{

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @param args
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Time Log");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MenuPanel());
        frame.pack();
        frame.setVisible(true);

    }

}