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

public class DensityMatrix {

    static JPanel densityMatrixOptions;
    static DensityMatrix instance;
    JTextField location;
    JTextField[] files;
    JTextField[] p;
    JTextField matrixFile;
    JButton load;
    JButton calculate;
    int numValidFiles;
    DataBuffer db;
    JFileChooser fileDialog;

    public DensityMatrix() {
	instance = this;
	Border eborder = BorderFactory.createEtchedBorder();
	Border tborder = new TitledBorder(eborder,
		"Density Matrix File Generation", TitledBorder.LEFT,
		TitledBorder.TOP);
	densityMatrixOptions = new JPanel();
	densityMatrixOptions.setLayout(new GridBagLayout());
	densityMatrixOptions.setBorder(tborder);
	densityMatrixOptions.setMinimumSize(new Dimension(267, 400));
	densityMatrixOptions.setPreferredSize(new Dimension(267, 400));
	densityMatrixOptions.setMaximumSize(new Dimension(400, 400));
	fileDialog = new JFileChooser(".");
	GridBagConstraints con = new GridBagConstraints();
	numValidFiles = 0;
	files = new JTextField[10];
	p = new JTextField[10];
	location = new JTextField(14);
	location.setEditable(false);
	matrixFile = new JTextField(14);
	matrixFile.setEditable(false);
	load = new JButton("Load...");
	calculate = new JButton("Calculate");
	con.gridx = 0;
	con.gridy = 0;
	densityMatrixOptions.add(new JLabel("Control File"), con);
	con.gridx = 1;
	con.gridy = 1;
	densityMatrixOptions.add(location, con);
	con.gridx = 0;
	con.gridy = 1;
	densityMatrixOptions.add(load, con);
	con.gridx = 0;
	con.gridy = 2;
	densityMatrixOptions.add(new JLabel("p"), con);
	con.gridx = 1;
	densityMatrixOptions.add(new JLabel("Qubit File"), con);
	for (int i = 0; i < 10; ++i) {
	    files[i] = new JTextField(14);
	    p[i] = new JTextField(5);
	    con.gridx = 0;
	    con.gridy = i + 3;
	    densityMatrixOptions.add(p[i], con);
	    con.gridx = 1;
	    densityMatrixOptions.add(files[i], con);
	    files[i].setEditable(false);
	}
	con.gridx = 0;
	con.gridy = 13;
	densityMatrixOptions.add(new JLabel("Save As:"), con);
	con.gridx = 1;
	con.gridy = 14;
	densityMatrixOptions.add(matrixFile, con);
	con.gridx = 0;
	densityMatrixOptions.add(calculate, con);
	load.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent ae) {
		DensityMatrix.getInstance().fileDialog.showOpenDialog(null);
		try {
		    File f = DensityMatrix.getInstance().fileDialog
			    .getSelectedFile();
		    if (f != null)
			location.setText(f.getCanonicalPath());
		} catch (IOException e) {
		    System.out.println("Density Matrix file loading");
		}
		DensityMatrix.loadFiles();
	    }
	});
	calculate.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent ae) {
		DensityMatrix.getInstance().fileDialog.showSaveDialog(null);
		try {
		    matrixFile.setText(DensityMatrix.getInstance().fileDialog
			    .getSelectedFile().getCanonicalPath());
		} catch (IOException e) {
		    System.out.println("Density Matrix file loading");
		}
		boolean success = DensityMatrix.calculateMatrixFile();
		if (success) {
		    location.setText("");
		    for (int i = 0; i < 10; ++i) {
			files[i].setText("");
		    }
		}
	    }
	});
    }

    public JPanel getPanel() {
	return densityMatrixOptions;
    }

    public static DensityMatrix getInstance() {
	return instance;
    }

    public static void loadFiles() {
	DensityMatrix dm = DensityMatrix.getInstance();
	dm.db = new DataBuffer(dm.location.getText());
	dm.numValidFiles = dm.db.getNumFiles();
	for (int i = 0; i < dm.numValidFiles; ++i) {
	    dm.files[i].setText(dm.db.getFile(i));
	}
    }

    public static boolean calculateMatrixFile() {
	DensityMatrix dm = DensityMatrix.getInstance();
	boolean validMatrixFile = true;
	double totalP;
	double mx;
	double my;
	double mz;
	Data d;
	double[] p = new double[dm.numValidFiles];
	int overwriteChoice;
	boolean success = false;
	// check for matrix file existence
	if (!dm.matrixFile.getText().equals("")) {
	    File tmpFile = new File(dm.matrixFile.getText());
	    if (tmpFile.exists()) {
		// ask if they want to overwrite
		overwriteChoice = JOptionPane
			.showConfirmDialog(
				null,
				"The file you have specified already exists.  Do you want to overwrite?",
				"File Already Exists.",
				JOptionPane.YES_NO_OPTION);
		if (overwriteChoice == JOptionPane.NO_OPTION) {
		    validMatrixFile = false;
		}
	    }
	    if (validMatrixFile) {
		// check for proper p values
		totalP = 0;
		try {
		    for (int i = 0; i < dm.numValidFiles; ++i) {
			p[i] = Double.parseDouble(dm.p[i].getText());
			totalP += p[i];
		    }
		} catch (NumberFormatException e) {
		    System.out.println("p format");
		    // indicate that a p value is not valid
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "One of the values you have specified for p is not valid.",
				    "Invalid P Value",
				    JOptionPane.ERROR_MESSAGE);
		}
		try {
		    if (totalP == 1) {
			// set up output buffer
			PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(dm.matrixFile.getText())));
			// start data buffer
			new Thread(dm.db).start();
			try {
			    while (!dm.db.isFinishedBuffering()) {
				Thread.sleep(25);
			    }
			} catch (Exception e) {
			}
			// read off data
			while (dm.db.qubitDataAvailable()) {
			    // calculate density matrix
			    mx = 0;
			    my = 0;
			    mz = 0;
			    for (int i = 0; i < dm.numValidFiles; ++i) {
				d = dm.db.getData(i);
				mx += p[i] * d.getX();
				my += p[i] * d.getY();
				mz += p[i] * d.getZ();
			    }
			    // write density matrix values
			    out.print(mx);
			    out.print(",");
			    out.print(my);
			    out.print(",");
			    out.print(mz);
			    out.println();
			}
			// close output buffer
			out.close();
			success = true;
			JOptionPane
				.showMessageDialog(
					null,
					"The density matrix file has been successuflly written!",
					"Success",
					JOptionPane.INFORMATION_MESSAGE);
		    } else {
			System.out.println("p != 1");
			// indicate p values must add up to 1
			JOptionPane.showMessageDialog(null,
				"The values for P must add up to 1",
				"Invalid P Value", JOptionPane.ERROR_MESSAGE);
		    }
		} catch (IOException e) {
		    System.out.println("printing matrix file");
		    // indicate file could not be opened for writing
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "The specified density matrix file could not be opened for writing.",
				    "File IO Error", JOptionPane.ERROR_MESSAGE);
		}
	    } else {
		System.out.println("file already exists");
		// indicate calculation will not occur
		JOptionPane
			.showMessageDialog(
				null,
				"The file already exists and the density matrix values will not be calculated",
				"File IO Error", JOptionPane.ERROR_MESSAGE);
	    }
	} else {
	    System.out.println("No matrix file specified");
	    // alert they must include a matrix file
	    JOptionPane
		    .showMessageDialog(
			    null,
			    "You must specify a file to store the density matrix data in.",
			    "File Error", JOptionPane.ERROR_MESSAGE);
	}
	return success;
    }
}