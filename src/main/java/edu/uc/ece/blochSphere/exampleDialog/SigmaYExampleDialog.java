package edu.uc.ece.blochSphere.exampleDialog;

import java.awt.Frame;

/**
 * The help dialog that displays the Sigma Y operator. This will show the
 * information about the operator and will also demonstrate a rotation around
 * the Y axis by PI/2, or 180 degrees.
 * 
 * @author Stephen Shary
 * 
 */
public class SigmaYExampleDialog extends ExampleDialog {

    protected static boolean m_animationRun = false;

    public SigmaYExampleDialog(Frame parentFrame) {
	super(parentFrame);
    }

    @Override
    public String getDialogTitle() {
	return "Sigma Y Example";
    }

    @Override
    public String getHtmlHelpPage() {
	return "sigmay.html";
    }

    public void run() {
	try {
	    // Wait for the dialog to be displayed initially.
	    while (!dialog.isVisible()) {
		Thread.sleep(100);
	    }
	    qubitModel.setVisible(true);
	    while (dialog.isVisible()) {
		qubitModel.resetAxisRotations();
		Thread.sleep(1000);
		// Go through 180 degrees.
		for (int i = 0; i < 180; i++) {
		    qubitModel.doYAxisRotation(i);
		    qubitModel.finishRotation();
		    Thread.sleep(20);
		}
		Thread.sleep(1000);
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
}
