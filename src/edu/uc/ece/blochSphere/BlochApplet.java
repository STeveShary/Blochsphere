package edu.uc.ece.blochSphere;

////////////////////////////////////////////////////////
//Created by Nick Vatamaniuc
//Univ of Cincinnati, Quantum Computing, Spring 2004
/////////////////////////////////////////////////////////
import javax.swing.JApplet;

public class BlochApplet extends JApplet {

    private static final long serialVersionUID = -4523676628636348991L;
    Bloch3D bloch3d;

    /**
     * A one-time initialization that will create the bloch sphere simulator and
     * the menu bar associated with it.
     */
    public void init() {
	bloch3d = new Bloch3D(this.getContentPane());
	setJMenuBar(new BlochMenuBar(bloch3d).getMenuBar());
    }

    /**
     * This is run when the applet has finished initalizing and is read to
     * start.
     */
    public void start() {
	getContentPane().setVisible(true);
    }

    /**
     * Run when the applet is no longer to be run.
     */
    public void stop() {
	getContentPane().setVisible(false);
	bloch3d = null;
    }
}
