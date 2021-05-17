package edu.uc.ece.blochSphere;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class BrowseListener implements ActionListener {

    JTextField targetField;
    JFileChooser fileDialog;

    public BrowseListener(JTextField targetField) {
	this.targetField = targetField;
    }

    public void actionPerformed(ActionEvent ae) {
	fileDialog = new JFileChooser(".");
	fileDialog.showOpenDialog(null);
	File f = fileDialog.getSelectedFile();
	if (f != null)
	    targetField.setText(f.getAbsolutePath());
    }
}