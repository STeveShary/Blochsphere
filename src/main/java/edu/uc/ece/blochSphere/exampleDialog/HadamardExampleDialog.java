package edu.uc.ece.blochSphere.exampleDialog;

import java.awt.Frame;

/**
 * An example dialog to show the action of the hadamard operator from several
 * different perspectives.
 * 
 * @author STeve Shary
 * 
 */
public class HadamardExampleDialog extends ExampleDialog {

    public HadamardExampleDialog(Frame parentFrame) {
	super(parentFrame);
    }

    public void run() {
	try {
	    // Wait for the dialog to be displayed initially.
	    while (!dialog.isVisible())
		Thread.sleep(100);
	    qubitModel.setVisible(true);
	    while (dialog.isVisible()) {
		// start from the |0> vector
		qubitModel.setInitalXAngle(0);
		qubitModel.setInitalYAngle(90);
		qubitModel.setInitalZAngle(0);
		qubitModel.resetAxisRotations();
		Thread.sleep(500);
		// rotate down 90 degress
		for (int i = 0; i <= 90; i++) {
		    qubitModel.doYAxisRotation(-i);
		    qubitModel.finishRotation();
		    Thread.sleep(10);
		}
		Thread.sleep(1000);
		qubitModel.setVisible(false);
		Thread.sleep(2000);
		qubitModel.setVisible(true);
		// start from the ( |0> + |1> ) / sqrt(2) vector
		qubitModel.setInitalXAngle(0);
		qubitModel.setInitalYAngle(0);
		qubitModel.setInitalZAngle(0);
		qubitModel.resetAxisRotations();
		Thread.sleep(500);
		// rotate down 90 degress
		for (int i = 0; i <= 90; i++) {
		    qubitModel.doYAxisRotation(-i);
		    qubitModel.finishRotation();
		    Thread.sleep(10);
		}
		qubitModel.doYAxisRotation(-90);
		qubitModel.doXAxisRotation(180);
		qubitModel.finishRotation();
		Thread.sleep(1000);
		qubitModel.setVisible(false);
		Thread.sleep(2000);
		qubitModel.setVisible(true);
		// start from the somwhere in the middle
		qubitModel.setInitalXAngle(0);
		qubitModel.setInitalYAngle(45);
		qubitModel.setInitalZAngle(45);
		qubitModel.resetAxisRotations();
		Thread.sleep(500);
		// rotate down 90 degress
		for (int i = 0; i <= 90; i++) {
		    qubitModel.doYAxisRotation(-i);
		    qubitModel.finishRotation();
		    Thread.sleep(10);
		}
		qubitModel.doYAxisRotation(-90);
		qubitModel.doXAxisRotation(180);
		qubitModel.finishRotation();
		Thread.sleep(1000);
		qubitModel.setVisible(false);
		Thread.sleep(2000);
		qubitModel.setVisible(true);
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public String getDialogTitle() {
	return "Hadamard Example";
    }

    @Override
    public String getHtmlHelpPage() {
	return "hadamard.html";
    }
}
