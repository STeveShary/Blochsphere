package edu.uc.ece.blochSphere;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import edu.uc.ece.blochSphere.exampleDialog.HadamardExampleDialog;
import edu.uc.ece.blochSphere.exampleDialog.PhaseShiftExampleDialog;
import edu.uc.ece.blochSphere.exampleDialog.SigmaXExampleDialog;
import edu.uc.ece.blochSphere.exampleDialog.SigmaXYZExample;
import edu.uc.ece.blochSphere.exampleDialog.SigmaYExampleDialog;
import edu.uc.ece.blochSphere.exampleDialog.SigmaZExampleDialog;

/**
 * Provides the menu bar that is available in the application.
 * 
 * @author Stephen Shary
 * 
 */
public class BlochMenuBar implements ActionListener {

    protected JMenuBar m_menuBar = null;
    protected JMenuItem sigmaXMenuItem = new JMenuItem("Sigma X Operator",
	    KeyEvent.VK_X);
    protected JMenuItem sigmaYMenuItem = new JMenuItem("Sigma Y Operator",
	    KeyEvent.VK_Y);
    protected JMenuItem sigmaZMenuItem = new JMenuItem("Sigma Z Operator",
	    KeyEvent.VK_Z);
    protected JMenuItem sigmaXYZMenuItem = new JMenuItem("Sigma XYZ Operator",
	    KeyEvent.VK_S);
    protected JMenuItem hadamardMenuItem = new JMenuItem("Hadamard Operator",
	    KeyEvent.VK_H);
    protected JMenuItem phaseShiftMenuItem = new JMenuItem("Phase Shift Operator", 
        KeyEvent.VK_P);
    protected JMenuItem qbitMenuItem = new JMenuItem("Setting the Qubit States",
	    KeyEvent.VK_Q);
    protected JMenuItem customOperatorMenuItem = new JMenuItem(
	    "Using the Custom Operator", KeyEvent.VK_C);
    protected JMenuItem recordPlaybackMenuItem = new JMenuItem(
	    "Using the Operator Record/Playback", KeyEvent.VK_R);
    protected JMenuItem larmorMenuItem = new JMenuItem(
	    "Using the Larmor Precession", KeyEvent.VK_L);
    protected JMenuItem rabiFieldMenuItem = new JMenuItem(
	    "Using the Rabi Field", KeyEvent.VK_L);
    protected JMenuItem resetViewMenuItem = new JMenuItem("Reset View",
	    KeyEvent.VK_R);
    protected JMenuItem saveStateMenuItem = new JMenuItem("Save Current State",
	    KeyEvent.VK_S);
    protected JMenuItem loadStateMenuItem = new JMenuItem(
	    "Load State from File", KeyEvent.VK_L);
    protected Bloch3D m_bloch3D = null;

    /**
     * This will build the menu bar and attach it the current application.
     * 
     * @param bloch3D
     *            the current panel object that contains the simulator.
     */
    public BlochMenuBar(Bloch3D bloch3D) {
	m_bloch3D = bloch3D;
	// Used so the Lightweight menu comes in front of the 3D canvas.
	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	m_menuBar = new JMenuBar();
	buildMenuBar();
    }

    /**
     * Builds the actual menu and populates it.
     */
    protected void buildMenuBar() {
	JMenu backgroundMenu = new JMenu("Simulator");
	m_menuBar.add(backgroundMenu);
	backgroundMenu.add(resetViewMenuItem);
	backgroundMenu.add(saveStateMenuItem);
	backgroundMenu.add(loadStateMenuItem);
	resetViewMenuItem.addActionListener(this);
	saveStateMenuItem.addActionListener(this);
	loadStateMenuItem.addActionListener(this);
	JMenu qbitOperatorMenu = new JMenu("QBit Operators");
	m_menuBar.add(qbitOperatorMenu);
	qbitOperatorMenu.add(sigmaXMenuItem);
	qbitOperatorMenu.add(sigmaYMenuItem);
	qbitOperatorMenu.add(sigmaZMenuItem);
	qbitOperatorMenu.add(sigmaXYZMenuItem);
	qbitOperatorMenu.add(hadamardMenuItem);
	qbitOperatorMenu.add(phaseShiftMenuItem);
	JMenu applicationHelp = new JMenu("Using the Application");
	applicationHelp.add(qbitMenuItem);
	applicationHelp.add(customOperatorMenuItem);
	applicationHelp.add(recordPlaybackMenuItem);
	applicationHelp.add(larmorMenuItem);
	applicationHelp.add(rabiFieldMenuItem);
	m_menuBar.add(applicationHelp);
	sigmaXMenuItem.addActionListener(this);
	sigmaYMenuItem.addActionListener(this);
	sigmaZMenuItem.addActionListener(this);
	sigmaXYZMenuItem.addActionListener(this);
	hadamardMenuItem.addActionListener(this);
	phaseShiftMenuItem.addActionListener(this);
	qbitMenuItem.addActionListener(this);
	larmorMenuItem.addActionListener(this);
	customOperatorMenuItem.addActionListener(this);
	recordPlaybackMenuItem.addActionListener(this);
	rabiFieldMenuItem.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource().equals(sigmaXMenuItem))
	    showExampleDialog(new SigmaXExampleDialog(JOptionPane
		    .getFrameForComponent(m_menuBar)));
	else if (e.getSource().equals(sigmaYMenuItem))
	    showExampleDialog(new SigmaYExampleDialog(JOptionPane
		    .getFrameForComponent(m_menuBar)));
	else if (e.getSource().equals(sigmaZMenuItem))
	    showExampleDialog(new SigmaZExampleDialog(JOptionPane
		    .getFrameForComponent(m_menuBar)));
	else if (e.getSource().equals(sigmaXYZMenuItem))
	    showExampleDialog(new SigmaXYZExample(JOptionPane
		    .getFrameForComponent(m_menuBar)));
	else if (e.getSource().equals(phaseShiftMenuItem))
        showExampleDialog(new PhaseShiftExampleDialog(JOptionPane
            .getFrameForComponent(m_menuBar)));	
	else if (e.getSource().equals(hadamardMenuItem))
	    showExampleDialog(new HadamardExampleDialog(JOptionPane
		    .getFrameForComponent(m_menuBar)));
	
	else if (e.getSource().equals(qbitMenuItem))
	    showExampleDialog(new DocumentationDialog(JOptionPane
		    .getFrameForComponent(m_menuBar),
		    "Setting the QBit States", "qbitpanel.html"));
	else if (e.getSource().equals(larmorMenuItem))
	    showExampleDialog(new DocumentationDialog(JOptionPane
		    .getFrameForComponent(m_menuBar), "Using the Larmor Precession Simulation",
		    "larmorprecession.html"));
	else if (e.getSource().equals(rabiFieldMenuItem))
        showExampleDialog(new DocumentationDialog(JOptionPane
            .getFrameForComponent(m_menuBar), "Using the Rabi Field Simulation",
            "rabiField.html"));
	else if (e.getSource().equals(customOperatorMenuItem))
        showExampleDialog(new DocumentationDialog(JOptionPane
            .getFrameForComponent(m_menuBar), "Using the Custom Operator",
            "customOperator.html"));
	else if (e.getSource().equals(recordPlaybackMenuItem))
        showExampleDialog(new DocumentationDialog(JOptionPane
            .getFrameForComponent(m_menuBar), "Using the Record/Playback Functions",
            "recordPlayback.html"));
		            
	else if (e.getSource().equals(saveStateMenuItem)) {
	    FileSaveLoad saveFile = new FileSaveLoad(m_bloch3D);
	    saveFile.saveBloch(m_menuBar);
	} else if (e.getSource().equals(loadStateMenuItem)) {
	    FileSaveLoad loadFile = new FileSaveLoad(m_bloch3D);
	    loadFile.loadBloch(m_menuBar);
	} else if (e.getSource().equals(resetViewMenuItem)) {
	    m_bloch3D.resetView();
	}
    }

    /**
     * Builds and displays the dialog created.
     * 
     * @param dialog
     */
    public void showExampleDialog(IBlochDialog dialog) {
	dialog.buildDialog();
	dialog.showDialog();
    }

    /**
     * Allows another object to obtain the MenuBar that was created by this
     * class. It will always provide the populated MenuBar.
     */
    public JMenuBar getMenuBar() {
	return m_menuBar;
    }
}
