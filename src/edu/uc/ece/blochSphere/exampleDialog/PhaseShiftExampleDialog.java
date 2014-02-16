package edu.uc.ece.blochSphere.exampleDialog;

import java.awt.Frame;

public class PhaseShiftExampleDialog extends ExampleDialog {
    
    public PhaseShiftExampleDialog(Frame parentFrame) {
        super(parentFrame);
    }
        
    @Override
    public String getDialogTitle() {
        return "Phase Shift Example";
    }

    @Override
    public String getHtmlHelpPage() {
        return "phaseshift.html";
    }

    @Override
    public void run() {
        try {
            // Wait for the dialog to be displayed initially.
            while (!dialog.isVisible())
            Thread.sleep(100);
            qubitModel.setVisible(true);
            while (dialog.isVisible()) {
            qubitModel.resetAxisRotations();
            // Go through 90 degrees.
            for (int i = 0; i < 90; i++) {
                qubitModel.doZAxisRotation(i);
                qubitModel.finishRotation();
                Thread.sleep(20);
            }
            Thread.sleep(1000);
            
            qubitModel.resetAxisRotations();
            // Go through 45 degrees.
            for (int i = 0; i < 45; i++) {
                qubitModel.doZAxisRotation(i);
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
