package edu.uc.ece.blochSphere;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class BlochApplication {

    private static final long serialVersionUID = 5384905380645652345L;

    /**
     * The constructor that will create the visual window (JFrame) object and
     * display it.
     * 
     */
    public BlochApplication() {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// run this code no matter what.
	finally {
	    JFrame blochFrame = new JFrame();
	    Bloch3D blochSim = new Bloch3D(blochFrame.getContentPane());
	    blochFrame.setTitle("Bloch Sphere Simulator");
	    blochFrame.setSize(1000, 800);
	    blochFrame.setLocation(0, 0);
	    blochFrame.setResizable(false);
	    blochFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    BlochMenuBar menuBar = new BlochMenuBar(blochSim);
	    blochFrame.setJMenuBar(menuBar.getMenuBar());
	    blochFrame.setVisible(true);
	}
    }

    /**
     * The standard main function used to launch a stand-alone application.
     * 
     * @param args
     *            The arguments passed into the application. There are no used
     *            arguments for this application.
     */
    public static void main(String[] args) {
	new BlochApplication();
    }
}
