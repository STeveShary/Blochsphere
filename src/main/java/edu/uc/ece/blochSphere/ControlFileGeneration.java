package edu.uc.ece.blochSphere;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ControlFileGeneration {

    static JPanel controlFileOptions;
    static ControlFileGeneration instance;
    JTextField matrixFile;
    JTextField[] files;
    JTextField[] p;
    JTextField controlFile;
    JButton load;
    JButton generate;
    int numValidFiles;
    DataBuffer db;
    JFileChooser fileDialog;

    public ControlFileGeneration() {
	instance = this;
	Border eborder = BorderFactory.createEtchedBorder();
	Border tborder = new TitledBorder(eborder, "Control File Generation",
		TitledBorder.LEFT, TitledBorder.TOP);
	controlFileOptions = new JPanel();
	controlFileOptions.setLayout(new GridBagLayout());
	controlFileOptions.setBorder(tborder);
	controlFileOptions.setMinimumSize(new Dimension(267, 400));
	controlFileOptions.setPreferredSize(new Dimension(267, 400));
	controlFileOptions.setMaximumSize(new Dimension(400, 400));
	fileDialog = new JFileChooser(".");
	GridBagConstraints con = new GridBagConstraints();
	numValidFiles = 0;
	files = new JTextField[10];
	matrixFile = new JTextField(14);
	matrixFile.setEditable(false);
	controlFile = new JTextField(14);
	controlFile.setEditable(false);
	load = new JButton("Load...");
	generate = new JButton("Generate");
	con.gridx = 1;
	con.gridy = 0;
	controlFileOptions.add(new JLabel("Density Matrix File"), con);
	con.gridx = 1;
	con.gridy = 1;
	controlFileOptions.add(matrixFile, con);
	con.gridx = 0;
	con.gridy = 1;
	controlFileOptions.add(load, con);
	con.gridy = 2;
	con.gridx = 1;
	controlFileOptions.add(new JLabel("Qubit File"), con);
	JButton loadtmp;
	for (int i = 0; i < 10; ++i) {
	    files[i] = new JTextField(14);
	    con.gridx = 0;
	    con.gridy = i + 3;
	    loadtmp = new JButton("Load...");
	    loadtmp.addActionListener(new BrowseListener(files[i]));
	    controlFileOptions.add(loadtmp, con);
	    con.gridx = 1;
	    controlFileOptions.add(files[i], con);
	    files[i].setEditable(false);
	}
	con.gridx = 0;
	con.gridy = 13;
	controlFileOptions.add(new JLabel("Save As:"), con);
	con.gridx = 1;
	con.gridy = 14;
	controlFileOptions.add(controlFile, con);
	con.gridx = 0;
	controlFileOptions.add(generate, con);
	load.addActionListener(new BrowseListener(matrixFile));
	generate.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent ae) {
		ControlFileGeneration.getInstance().fileDialog
			.showSaveDialog(null);
		File f = ControlFileGeneration.getInstance().fileDialog
			.getSelectedFile();
		if (f != null) {
		    controlFile.setText(f.getAbsolutePath());
		    ControlFileGeneration.generateControlFile();
		}
	    }
	});
    }

    public JPanel getPanel() {
	return controlFileOptions;
    }

    public static ControlFileGeneration getInstance() {
	return instance;
    }

    public static void generateControlFile() {
	ControlFileGeneration cfg = ControlFileGeneration.getInstance();
	int overwriteChoice;
	boolean validControlFile = true;
	// check for control file existence
	if (!cfg.controlFile.getText().equals("")) {
	    File tmpFile = new File(cfg.controlFile.getText());
	    if (tmpFile.exists()) {
		// ask if they want to overwrite
		overwriteChoice = JOptionPane
			.showConfirmDialog(
				null,
				"The control file you have specified already exists.  Do you want to overwrite?",
				"File Already Exists.",
				JOptionPane.YES_NO_OPTION);
		if (overwriteChoice == JOptionPane.NO_OPTION) {
		    validControlFile = false;
		}
	    }
	    if (validControlFile) {
		try {
		    // set up output buffer
		    PrintWriter out = new PrintWriter(new BufferedWriter(
			    new FileWriter(cfg.controlFile.getText())));
		    out.println(cfg.matrixFile.getText());
		    int qubitNum = 0;
		    while (qubitNum < 10
			    && !cfg.files[qubitNum].getText().equals("")) {
			out.println(cfg.files[qubitNum].getText());
			qubitNum++;
		    }
		    // close output buffer
		    out.close();
		    JOptionPane.showMessageDialog(null,
			    "The control file has been successuflly written!",
			    "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
		    System.out.println("printing control file");
		    // indicate file could not be opened for writing
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "The specified control file could not be opened for writing.",
				    "File IO Error", JOptionPane.ERROR_MESSAGE);
		}
	    } else {
		System.out.println("file already exists");
		// indicate creation will not occur
		JOptionPane
			.showMessageDialog(
				null,
				"The file already exists and the control file will not be created",
				"File IO Error", JOptionPane.ERROR_MESSAGE);
	    }
	} else {
	    System.out.println("No control file specified");
	    // alert they must include a matrix file
	    JOptionPane.showMessageDialog(null,
		    "You must specify control file to create.", "File Error",
		    JOptionPane.ERROR_MESSAGE);
	}
    }
}