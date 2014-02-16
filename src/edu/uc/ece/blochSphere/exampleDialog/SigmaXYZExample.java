package edu.uc.ece.blochSphere.exampleDialog;

import java.awt.Frame;

public class SigmaXYZExample extends ExampleDialog {

    public SigmaXYZExample(Frame parentFrame) {
	super(parentFrame);
    }

    @Override
    public String getDialogTitle() {
	return "Sigma XYZ Example";
    }

    @Override
    public String getHtmlHelpPage() {
	return "sigmaxyz.html";
    }

    public void run() {
	try {
	    // Wait for the dialog to be displayed initially.
	    while (!dialog.isVisible())
		Thread.sleep(100);
	    qubitModel.setVisible(true);
	    while (dialog.isVisible()) {
		// reset
		qubitModel.resetAxisRotations();
		Thread.sleep(1500);
		// Do sigmaZ
		for (int i = 0; i < 180; i++) {
		    qubitModel.doZAxisRotation(i);
		    qubitModel.finishRotation();
		    Thread.sleep(10);
		}
		// Do sigmaY
		for (int i = 0; i < 180; i++) {
		    qubitModel.doYAxisRotation(i);
		    qubitModel.finishRotation();
		    Thread.sleep(10);
		}
		// Do sigmaX
		for (int i = 0; i < 180; i++) {
		    qubitModel.doXAxisRotation(i);
		    qubitModel.finishRotation();
		    Thread.sleep(10);
		}
		Thread.sleep(1500);
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
}
