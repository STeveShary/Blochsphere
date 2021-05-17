package edu.uc.ece.blochSphere.exampleDialog;

import java.awt.Frame;

public class SigmaXExampleDialog extends ExampleDialog {

    public SigmaXExampleDialog(Frame parentFrame) {
	super(parentFrame);
    }

    @Override
    public String getDialogTitle() {
	return "Sigma X Example";
    }

    @Override
    public String getHtmlHelpPage() {
	return "sigmax.html";
    }

    public void run() {
	try {
	    // Wait for the dialog to be displayed initially.
	    while (!dialog.isVisible())
		Thread.sleep(100);
	    qubitModel.setVisible(true);
	    while (dialog.isVisible()) {
		qubitModel.resetAxisRotations();
		Thread.sleep(1000);
		// Go through 180 degrees.
		for (int i = 0; i < 180; i++) {
		    qubitModel.doXAxisRotation(i);
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
