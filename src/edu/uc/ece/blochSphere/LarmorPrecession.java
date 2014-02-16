package edu.uc.ece.blochSphere;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class was created to allow the a magnetic field to act on an array of
 * QBits. It will also supply the needed panel to modify, stop and start the
 * magnetic field affect.
 * 
 * @author Stephen Shary
 * 
 */
public class LarmorPrecession implements Runnable, ActionListener,
	ChangeListener {

    protected Vector qbitVector = null;
    protected JPanel m_controlPanel = null;
    protected JSlider m_teslaSlider = null;
    protected JLabel m_BzSliderValueLabel = null;
    protected JLabel m_instructionLabel = null;
    protected JButton m_startButton = null;
    protected JButton m_stopButton = null;
    protected JButton m_resetButton = null;
    protected JLabel m_scaleNoteLabel = null;
    protected String m_instructionString = "<html>Adjust the slider the change the B<sub>z</sub> strength<p> of the magnetic field on the qbit.</html";
    public static final double G_STAR = 2.002319;
    public static final double M_0 = 9.1 * Math.pow(10, -31);
    public static final double E = 1.6 * Math.pow(10, -19);
    public static final double H_BAR = 6.582 * Math.pow(10, -16);
    public static final double W_L = (E * G_STAR) / (2 * M_0);
    public static final double TIME_SCALE_FACTOR = Math.log10(W_L);
    protected Vector<Double> m_originalPhi = new Vector<Double>();
    private boolean m_isApplyingField = false;

    public LarmorPrecession() {
	buildControlPanel();
    }

    public LarmorPrecession(Vector<Qubit> qbitVector) {
	this();
	this.qbitVector = qbitVector;
    }

    /**
     * Sets the vector of qbits to apply the magnetic field to.
     */
    public void setQbitsToApplyPrecession(Vector qbitVector) {
	this.qbitVector = qbitVector;
    }

    /**
     * Returns the vector of qbits that this magnetic field will affect.
     */
    public Vector getQbitsToApplyPrecession() {
	return qbitVector;
    }

    /**
     * Returns a reference to the control panel that can be used to modify the
     * intensity of the field and also start and stop the magnetic field.
     * 
     * This should always be built when the object is constructed.
     */
    public JPanel getControlPanel() {
	return m_controlPanel;
    }

    /**
     * An internal command used to populate the control panel.
     * 
     */
    protected void buildControlPanel() {
	m_controlPanel = new JPanel();
	Border border = new TitledBorder(BorderFactory.createEtchedBorder(),
		"Magnetic Field Control", TitledBorder.LEFT, TitledBorder.TOP);
	m_controlPanel.setBorder(border);
	SpringLayout layout = new SpringLayout();
	m_controlPanel.setLayout(layout);
	m_instructionLabel = new JLabel(m_instructionString);
	m_controlPanel.add(m_instructionLabel);
	layout.putConstraint(SpringLayout.NORTH, m_instructionLabel, 20,
		SpringLayout.NORTH, m_controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
		m_instructionLabel, 10, SpringLayout.HORIZONTAL_CENTER,
		m_controlPanel);
	// Create the slider starting at 10 -> (1 tesla).
	m_teslaSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
	m_teslaSlider.setMajorTickSpacing(10);
	Hashtable<Integer, JComponent> labelTable = new Hashtable<Integer, JComponent>();
	labelTable.put(new Integer(1), new JLabel("<html>0.1</html>"));
	labelTable.put(new Integer(10), new JLabel(
		"<html><center>1.0<p>tesla</center</html>"));
	labelTable.put(new Integer(50), new JLabel(
		"<html><center>5.0<p>tesla</center></html>"));
	labelTable.put(new Integer(100), new JLabel(
		"<html><center>10<p>tesla</center></html>"));
	m_teslaSlider.setLabelTable(labelTable);
	m_teslaSlider.setPaintLabels(true);
	m_teslaSlider.setPaintTicks(true);
	m_controlPanel.add(m_teslaSlider);
	m_teslaSlider.addChangeListener(this);
	// put on the north side in the center
	layout.putConstraint(SpringLayout.NORTH, m_teslaSlider, 10,
		SpringLayout.SOUTH, m_instructionLabel);
	layout.putConstraint(SpringLayout.WEST, m_teslaSlider, 50,
		SpringLayout.WEST, m_controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, m_teslaSlider, 10,
		SpringLayout.HORIZONTAL_CENTER, m_controlPanel);
	// Create the label value of the slider
	m_BzSliderValueLabel = new JLabel();
	m_BzSliderValueLabel.setBorder(BorderFactory.createEtchedBorder());
	m_controlPanel.add(m_BzSliderValueLabel);
	m_BzSliderValueLabel.setText(new Double(((double) m_teslaSlider
		.getValue()) / 10).toString());
	layout.putConstraint(SpringLayout.WEST, m_BzSliderValueLabel, 5,
		SpringLayout.EAST, m_teslaSlider);
	layout.putConstraint(SpringLayout.NORTH, m_BzSliderValueLabel, 0,
		SpringLayout.NORTH, m_teslaSlider);
	// Create the buttons and place below the slider in the center
	m_stopButton = new JButton("Stop");
	m_controlPanel.add(m_stopButton);
	m_stopButton.addActionListener(this);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, m_stopButton, 10,
		SpringLayout.HORIZONTAL_CENTER, m_controlPanel);
	layout.putConstraint(SpringLayout.NORTH, m_stopButton, 10,
		SpringLayout.SOUTH, m_teslaSlider);
	m_startButton = new JButton("Start");
	m_controlPanel.add(m_startButton);
	m_startButton.addActionListener(this);
	layout.putConstraint(SpringLayout.EAST, m_startButton, -5,
		SpringLayout.WEST, m_stopButton);
	layout.putConstraint(SpringLayout.NORTH, m_startButton, 0,
		SpringLayout.NORTH, m_stopButton);
	m_resetButton = new JButton("Reset");
	m_controlPanel.add(m_resetButton);
	m_resetButton.addActionListener(this);
	layout.putConstraint(SpringLayout.WEST, m_resetButton, 5,
		SpringLayout.EAST, m_stopButton);
	layout.putConstraint(SpringLayout.NORTH, m_resetButton, 0,
		SpringLayout.NORTH, m_stopButton);
	DecimalFormat formatter = new DecimalFormat("00.0");
	String scalingFactorString = formatter.format(TIME_SCALE_FACTOR);
	m_scaleNoteLabel = new JLabel(
		"<html>NOTE: Precession is scaled by: 10<sup>-"
			+ scalingFactorString + "</sup> seconds.</html>");
	m_controlPanel.add(m_scaleNoteLabel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, m_scaleNoteLabel,
		5, SpringLayout.HORIZONTAL_CENTER, m_stopButton);
	layout.putConstraint(SpringLayout.NORTH, m_scaleNoteLabel, 15,
		SpringLayout.SOUTH, m_stopButton);
	// Set the field applying to false to initially set the enabled and
	// disabled buttons.
	setApplyField(false);
    }

    protected synchronized boolean getApplyField() {
	return m_isApplyingField;
    }

    protected synchronized void setApplyField(boolean applyField) {
	m_isApplyingField = applyField;
	m_stopButton.setEnabled(m_isApplyingField);
	m_startButton.setEnabled(!m_isApplyingField);
    }

    /**
     * The thread spawned function that will run to apply the magnetic field.
     */
    public void run() {
	long startTime = Calendar.getInstance().getTimeInMillis();
	m_originalPhi = new Vector<Double>();
	for (int i = 0; i < qbitVector.size(); i++)
	    m_originalPhi.add(new Double(Math.toRadians(((Qubit) qbitVector
		    .get(i)).getPhi())));
	while (getApplyField() && qbitVector != null && qbitVector.size() > 0) {
	    double deltaTime = new Long(Calendar.getInstance()
		    .getTimeInMillis()
		    - startTime).doubleValue() / 1000;
	    double deltaPhi = deltaTime * W_L
		    * Double.parseDouble(m_BzSliderValueLabel.getText())
		    * Math.pow(10, -TIME_SCALE_FACTOR);
	    for (int i = 0; i < qbitVector.size(); i++) {
		Qubit qbit = (Qubit) qbitVector.get(i);
		// Only apply to visible qbits.
		if (!qbit.isVisible())
		    continue;
		// Set the new value and remember to convert back to degrees.
		qbit.setPhi(Math.toDegrees(((Double) m_originalPhi.get(i))
			.doubleValue()
			+ deltaPhi));
		// We sleep for 1 millisecond to give the CPU a little break. It
		// also improves the UI responsiveness.
		try {
		    Thread.sleep(5);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    /**
     * Receives all events from the registered components on the panel and
     * processes their actions.
     * 
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
	Object component = e.getSource();
	// If we hit start, then only start when it isn't already running.
	if (component.equals(m_startButton)) {
	    if (!getApplyField()) {
		setApplyField(true);
		new Thread(this).start();
	    }
	}
	// If we hit stop, then set the ApplyField to false.
	if (component.equals(m_stopButton))
	    setApplyField(false);
	// If we hit the reset button, then stop the field, and set the value to
	// 1.0 tesla (10 on the JSlider).
	if (component.equals(m_resetButton)) {
	    setApplyField(false);
	    m_teslaSlider.setValue(10);
	    for (int i = 0; i < qbitVector.size(); i++) {
		Qubit qbit = (Qubit) qbitVector.get(i);
		// Set the qubit phi value back to the original.
		qbit.setPhi(Math.toDegrees(((Double) m_originalPhi.get(i))
			.doubleValue()));
	    }
	}
    }

    public void stateChanged(ChangeEvent e) {
	Object component = e.getSource();
	// If the slider changed, then set it's value.
	if (component.equals(m_teslaSlider))
	    m_BzSliderValueLabel.setText(new Double(((double) m_teslaSlider
		    .getValue()) / 10).toString());
    }

    public String getTabName() {
	return "Larmor Precession";
    }
}
