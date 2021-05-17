package edu.uc.ece.blochSphere;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.media.j3d.BranchGroup;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;

import edu.uc.ece.blochSphere.operator.Operator;

/**
 * Created to allow the user to apply an oscillating magnetic field to the
 * visible qbits.
 * 
 * @author STeve Shary
 * 
 */
public class RabiField implements Runnable, ActionListener, ChangeListener {

    protected JPanel m_controlPanel = null;
    protected JLabel m_instructionLabel = null;
    protected JSlider m_magFieldSlider = null;
    protected JLabel m_magFieldValueLabel = null;
    protected JLabel m_magFieldLabel = null;
    protected JSlider m_magPerpFieldSlider = null;
    protected JLabel m_magPerpValueLabel = null;
    protected JLabel m_magPerpLabel = null;
    protected JSlider m_omegaSlider = null;
    protected JLabel m_omegaValueLabel = null;
    protected JLabel m_omegaLabel = null;
    protected JButton m_startButton = null;
    protected JButton m_stopButton = null;
    protected JButton m_resetButton = null;
    protected JCheckBox matchFrequencies = null;
    protected Vector<Qubit> m_qbits = null;
    private boolean m_isApplyingField = false;
    protected Qubit magneticQubit = null;
    private Vector<ComplexNumber> alphaValues = new Vector<ComplexNumber>();
    private Vector<ComplexNumber> betaValues = new Vector<ComplexNumber>();
    private HashMap<Qubit, Vector<TrackDot>> allTracks = new HashMap<Qubit, Vector<TrackDot>>();
    private boolean showPreviousStates = false;
    private JToggleButton showPreviousValues = new JToggleButton("Show Trail");
    public static final int MAX_NUM_PREV_STATES = 1600;
    private BranchGroup parentBG = null;

    public RabiField(BranchGroup parentBG) {
	this.parentBG = parentBG;
	buildControlPanel();
    }

    public RabiField(BranchGroup parentBG, Vector<Qubit> qbits) {
	this(parentBG);
	setQbits(qbits);
    }

    public void setQbits(Vector<Qubit> qbits) {
	if (qbits != null)
	    m_qbits = qbits;
    }

    protected void buildControlPanel() {
	m_controlPanel = new JPanel();
	// m_controlPanel.setMinimumSize(new Dimension(200, 190));
	// m_controlPanel.setPreferredSize(new Dimension(200, 190));
	// m_controlPanel.setMaximumSize(new Dimension(400, 190));
	Border border = new TitledBorder(BorderFactory.createEtchedBorder(),
		"Magnetic Field Control", TitledBorder.LEFT, TitledBorder.TOP);
	m_controlPanel.setBorder(border);
	SpringLayout layout = new SpringLayout();
	m_controlPanel.setLayout(layout);
	m_instructionLabel = new JLabel(
		"<html>Adjust the B<sub>z</sub>, B<sub>perp</sub>, and W to adjust the Rabi Magnetic Field.</html>");
	m_controlPanel.add(m_instructionLabel);
	layout.putConstraint(SpringLayout.NORTH, m_instructionLabel, 20,
		SpringLayout.NORTH, m_controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
		m_instructionLabel, 10, SpringLayout.HORIZONTAL_CENTER,
		m_controlPanel);
	m_magFieldSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
	m_magFieldSlider.setMajorTickSpacing(10);
	Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
	labelTable.put(new Integer(1), new JLabel("<html>0.1</html>"));
	labelTable.put(new Integer(10), new JLabel(
		"<html><center>1.0<p>tesla</center></html>"));
	labelTable.put(new Integer(50), new JLabel(
		"<html><center>5.0<p>tesla</center></html>"));
	labelTable.put(new Integer(100), new JLabel(
		"<html><center>10<p>tesla</center></html>"));
	m_magFieldSlider.setLabelTable(labelTable);
	m_magFieldSlider.setPaintLabels(true);
	m_magFieldSlider.setPaintTicks(true);
	m_controlPanel.add(m_magFieldSlider);
	m_magFieldSlider.addChangeListener(this);
	// put on the north side in the center
	layout.putConstraint(SpringLayout.NORTH, m_magFieldSlider, 10,
		SpringLayout.SOUTH, m_instructionLabel);
	layout.putConstraint(SpringLayout.WEST, m_magFieldSlider, 70,
		SpringLayout.WEST, m_controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, m_magFieldSlider,
		10, SpringLayout.HORIZONTAL_CENTER, m_controlPanel);
	m_magFieldLabel = new JLabel("<html>B<sub>z</sub></html>");
	m_controlPanel.add(m_magFieldLabel);
	layout.putConstraint(SpringLayout.NORTH, m_magFieldLabel, 0,
		SpringLayout.NORTH, m_magFieldSlider);
	layout.putConstraint(SpringLayout.EAST, m_magFieldLabel, -10,
		SpringLayout.WEST, m_magFieldSlider);
	m_magFieldValueLabel = new JLabel("1.0");
	m_magFieldValueLabel.setBorder(BorderFactory.createEtchedBorder());
	m_controlPanel.add(m_magFieldValueLabel);
	m_magFieldValueLabel.setText(new Double(((double) m_magFieldSlider
		.getValue()) / 10).toString());
	layout.putConstraint(SpringLayout.NORTH, m_magFieldValueLabel, 0,
		SpringLayout.NORTH, m_magFieldSlider);
	layout.putConstraint(SpringLayout.WEST, m_magFieldValueLabel, 10,
		SpringLayout.EAST, m_magFieldSlider);
	m_magPerpFieldSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
	m_magPerpFieldSlider.setMajorTickSpacing(10);
	Hashtable<Integer, JLabel> perplabelTable = new Hashtable<Integer, JLabel>();
	perplabelTable.put(new Integer(1), new JLabel("<html>0.1</html>"));
	perplabelTable.put(new Integer(10), new JLabel(
		"<html><center>1.0<p>tesla</center></html>"));
	perplabelTable.put(new Integer(50), new JLabel(
		"<html><center>5.0<p>tesla</center></html>"));
	perplabelTable.put(new Integer(100), new JLabel(
		"<html><center>10<p>tesla</center></html>"));
	m_magPerpFieldSlider.setLabelTable(perplabelTable);
	m_magPerpFieldSlider.setPaintLabels(true);
	m_magPerpFieldSlider.setPaintTicks(true);
	m_controlPanel.add(m_magPerpFieldSlider);
	m_magPerpFieldSlider.addChangeListener(this);
	layout.putConstraint(SpringLayout.NORTH, m_magPerpFieldSlider, 10,
		SpringLayout.SOUTH, m_magFieldSlider);
	layout.putConstraint(SpringLayout.WEST, m_magPerpFieldSlider, 70,
		SpringLayout.WEST, m_controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
		m_magPerpFieldSlider, 10, SpringLayout.HORIZONTAL_CENTER,
		m_controlPanel);
	m_magPerpLabel = new JLabel("<html>B<sub>perp</sub></html>");
	m_controlPanel.add(m_magPerpLabel);
	layout.putConstraint(SpringLayout.NORTH, m_magPerpLabel, 0,
		SpringLayout.NORTH, m_magPerpFieldSlider);
	layout.putConstraint(SpringLayout.EAST, m_magPerpLabel, -10,
		SpringLayout.WEST, m_magPerpFieldSlider);
	m_magPerpValueLabel = new JLabel("1.0");
	m_magPerpValueLabel.setBorder(BorderFactory.createEtchedBorder());
	m_controlPanel.add(m_magPerpValueLabel);
	m_magPerpValueLabel.setText(new Double(((double) m_magPerpFieldSlider
		.getValue()) / 10).toString());
	layout.putConstraint(SpringLayout.NORTH, m_magPerpValueLabel, 0,
		SpringLayout.NORTH, m_magPerpFieldSlider);
	layout.putConstraint(SpringLayout.WEST, m_magPerpValueLabel, 10,
		SpringLayout.EAST, m_magPerpFieldSlider);
	m_omegaSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 314);
	m_omegaSlider.setMajorTickSpacing(157);
	Hashtable<Integer, JLabel> omegaLabelTable = new Hashtable<Integer, JLabel>();
	omegaLabelTable.put(new Integer(0), new JLabel(
		"<html><center>0</center></html>"));
	omegaLabelTable.put(new Integer(157), new JLabel(
		"<html><center>&Pi; / 2</center></html>"));
	omegaLabelTable.put(new Integer(314), new JLabel(
		"<html><center>&Pi;</center></html>"));
	omegaLabelTable.put(new Integer(628), new JLabel(
		"<html><center>2&Pi;</center></html>"));
	omegaLabelTable.put(new Integer(942), new JLabel(
		"<html><center>3&Pi;</center></html>"));
	m_omegaSlider.setLabelTable(omegaLabelTable);
	m_omegaSlider.setPaintLabels(true);
	m_omegaSlider.setPaintTicks(true);
	m_controlPanel.add(m_omegaSlider);
	m_omegaSlider.setSize(300, m_omegaSlider.getSize().height);
	m_omegaSlider.addChangeListener(this);
	layout.putConstraint(SpringLayout.NORTH, m_omegaSlider, 10,
		SpringLayout.SOUTH, m_magPerpFieldSlider);
	layout.putConstraint(SpringLayout.WEST, m_omegaSlider, 70,
		SpringLayout.WEST, m_controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, m_omegaSlider, 10,
		SpringLayout.HORIZONTAL_CENTER, m_controlPanel);
	m_omegaLabel = new JLabel("<html>W</html>");
	m_controlPanel.add(m_omegaLabel);
	layout.putConstraint(SpringLayout.NORTH, m_omegaLabel, 0,
		SpringLayout.NORTH, m_omegaSlider);
	layout.putConstraint(SpringLayout.EAST, m_omegaLabel, -10,
		SpringLayout.WEST, m_omegaSlider);
	m_omegaValueLabel = new JLabel("100");
	m_omegaValueLabel.setBorder(BorderFactory.createEtchedBorder());
	m_controlPanel.add(m_omegaValueLabel);
	m_omegaValueLabel.setText(new Double(
		((double) m_omegaSlider.getValue()) / 100).toString());
	layout.putConstraint(SpringLayout.NORTH, m_omegaValueLabel, 0,
		SpringLayout.NORTH, m_omegaSlider);
	layout.putConstraint(SpringLayout.WEST, m_omegaValueLabel, 10,
		SpringLayout.EAST, m_omegaSlider);
	matchFrequencies = new JCheckBox("Match W to Larmor Frequency");
	matchFrequencies.addActionListener(this);
	m_controlPanel.add(matchFrequencies);
	layout.putConstraint(SpringLayout.NORTH, matchFrequencies, 10,
		SpringLayout.SOUTH, m_omegaSlider);
	layout.putConstraint(SpringLayout.WEST, matchFrequencies, 0,
		SpringLayout.WEST, m_omegaSlider);
	m_resetButton = new JButton("Restart");
	m_controlPanel.add(m_resetButton);
	m_resetButton.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, m_resetButton, -10,
		SpringLayout.SOUTH, m_controlPanel);
	layout.putConstraint(SpringLayout.EAST, m_resetButton, -10,
		SpringLayout.EAST, m_controlPanel);
	m_stopButton = new JButton("Stop");
	m_controlPanel.add(m_stopButton);
	m_stopButton.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, m_stopButton, -10,
		SpringLayout.SOUTH, m_controlPanel);
	layout.putConstraint(SpringLayout.EAST, m_stopButton, -10,
		SpringLayout.WEST, m_resetButton);
	m_startButton = new JButton("Start");
	m_controlPanel.add(m_startButton);
	m_startButton.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, m_startButton, -10,
		SpringLayout.SOUTH, m_controlPanel);
	layout.putConstraint(SpringLayout.EAST, m_startButton, -10,
		SpringLayout.WEST, m_stopButton);
	m_controlPanel.add(showPreviousValues);
	showPreviousValues.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, showPreviousValues, -10,
		SpringLayout.SOUTH, m_controlPanel);
	layout.putConstraint(SpringLayout.EAST, showPreviousValues, -10,
		SpringLayout.WEST, m_startButton);
	// Set the field applying to false to initially set the enabled and
	// disabled buttons.
	setApplyField(false);
    }

    /**
     * Returns if the magnetic field is currently being applied to the qbits.
     */
    protected synchronized boolean getApplyField() {
	return m_isApplyingField;
    }

    /**
     * Sets if the magnetic field should be applied.
     * 
     * @param applyField
     */
    protected synchronized void setApplyField(boolean applyField) {
	m_isApplyingField = applyField;
	m_stopButton.setEnabled(m_isApplyingField);
	m_startButton.setEnabled(!m_isApplyingField);
    }

    public JPanel getControlPanel() {
	return m_controlPanel;
    }

    /*
     * Created to be run when the start button has been selected. This should
     * apply the animation to the visible qubits.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
	long startTime = Calendar.getInstance().getTimeInMillis();
	alphaValues = new Vector<ComplexNumber>();
	betaValues = new Vector<ComplexNumber>();
	for (Qubit qbit : m_qbits) {
	    alphaValues.add(qbit.getAlphaValue());
	    betaValues.add(qbit.getBetaValue());
	}
	double deltaTime = 0;
	while (getApplyField() && m_qbits != null && m_qbits.size() > 0) {
	    deltaTime = new Long(Calendar.getInstance().getTimeInMillis()
		    - startTime).doubleValue() / 1000;
	    double beta_z = Double.parseDouble(m_magFieldValueLabel.getText());
	    double beta_perp = Double
		    .parseDouble(m_magPerpValueLabel.getText());
	    double omega = Double.parseDouble(m_omegaValueLabel.getText());
	    Operator finalOperator = calculateOperator(omega, beta_perp,
		    beta_z, deltaTime);
	    int counter = 0;
	    for (Qubit qbit : m_qbits) {
		// Only apply to visible qbits.
		if (qbit.isVisible()) {
		    ComplexNumber betaValue = alphaValues.get(counter);
		    ComplexNumber alphaValue = betaValues.get(counter);
		    qbit.setAlphaBetaValues(finalOperator.applyOperatorToAlpha(
			    alphaValue, betaValue), finalOperator
			    .applyOperatorToBeta(alphaValue, betaValue));
		    if (isShowPreviousStates()) {
			addTrackDot(qbit);
		    }
		}
		counter++;
	    }
	    // This will keep the CPU from spiking.
	    try {
		Thread.sleep(10);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Adds a new "track dot" or small position dot representing the previous
     * value of the qubit.
     * 
     * @param qubit
     *            The current qubit to create a new position dot for.
     */
    private void addTrackDot(Qubit qubit) {
	Vector<TrackDot> trackDots = allTracks.get(qubit);
	if (trackDots == null) {
	    trackDots = new Vector<TrackDot>();
	    trackDots.add(new TrackDot(new Double(0.8f).doubleValue(), qubit
		    .getTh(), qubit.getPhi(), this.parentBG, qubit
		    .getQubitColor(), false));
	    allTracks.put(qubit, trackDots);
	} else {
	    if (trackDots.size() > MAX_NUM_PREV_STATES) {
		trackDots.get(0).kill();
		trackDots.remove(0);
	    }
	    Color3f color = qubit.getQubitColor();
	    double magnitude = 0.8f;
	    boolean big = false;
	    trackDots.add(new TrackDot(magnitude, qubit.getTh(),
		    qubit.getPhi(), this.parentBG, color, big));
	}
    }

    /**
     * Calculates the correct operator that will move the qubit from the
     * original state to the state at deltaTime based on the input parameters.
     * 
     * @param omega
     *            The rotational speed of the b_perp magnetic field along the
     *            XY-axis.
     * @param beta_perp
     *            The strength (in Telsas) of the magnetic field that is rotated
     *            around the XY-axis.
     * @param beta_z
     *            The strength (in Telsas) of the magnetic field that is run
     *            along the Z-axis.
     * @param deltaTime
     *            The change in time from the original state of the qubit(s).
     * 
     * @return The 2x2 Operator object that represents the rotation of the
     *         original qubit states to their correct placement.
     */
    private Operator calculateOperator(double omega, double beta_perp,
	    double beta_z, double deltaTime) {
	double W = omega;
	double W1 = beta_perp;
	double W0 = beta_z;
	if (matchFrequencies.isSelected()) {
	    W = Math.sqrt(W0);
	}
	double dW = (W * W) - W0;
	Operator rOperator = new Operator(new ComplexNumber(Math.cos(W
		* deltaTime / 2), -1 * Math.sin(W * deltaTime / 2)),
		new ComplexNumber(0, 0), new ComplexNumber(0, 0),
		new ComplexNumber(Math.cos(W * deltaTime / 2), Math.sin(W
			* deltaTime / 2)));
	double b = (-1 * W1 * deltaTime) / 2;
	double a = (dW * deltaTime) / 2;
	double q = Math.sqrt((a * a) + (b * b));
	double bOverAMinusQ = (a - q) == 0 ? 0 : b / (a - q);
	double bOverAPlusQ = (a + q) == 0 ? 0 : b / (a + q);
	Operator sOperator = new Operator(new ComplexNumber(-1 * bOverAMinusQ,
		0), new ComplexNumber(-1 * bOverAPlusQ, 0), new ComplexNumber(
		1, 0), new ComplexNumber(1, 0));
	Operator eLambdaOperator = new Operator(new ComplexNumber(Math.cos(q),
		Math.sin(q)), new ComplexNumber(0, 0), new ComplexNumber(0, 0),
		new ComplexNumber(Math.cos(q), -1 * Math.sin(q)));
	Operator sInverseOperator = new Operator(new ComplexNumber(1, 0),
		new ComplexNumber(bOverAPlusQ, 0), new ComplexNumber(-1, 0),
		new ComplexNumber(-1 * bOverAMinusQ, 0));
	Operator xOperator = new Operator(new ComplexNumber(0, 0),
		new ComplexNumber(1, 0), new ComplexNumber(1, 0),
		new ComplexNumber(0, 0));
	return xOperator.multiply(rOperator.multiply(sOperator
		.multiply(eLambdaOperator.multiply(sInverseOperator))));
    }

    /*
     * Used to listen to the start, stop and reset buttons.
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
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
	    int counter = 0;
	    if (alphaValues.size() > 0 && betaValues.size() > 0) {
		for (Qubit qbit : m_qbits) {
		    qbit.setAlphaBetaValues(alphaValues.get(counter),
			    betaValues.get(counter));
		    counter++;
		}
	    }
	    m_omegaSlider.setValue(314);
	    m_magPerpFieldSlider.setValue(10);
	    m_magFieldSlider.setValue(10);
	}
	if (component.equals(showPreviousValues)) {
	    if (showPreviousValues.isSelected()) {
		showPreviousValues.setText("Hide Trail");
		setShowPreviousStates(true);
	    } else {
		showPreviousValues.setText("Show Trail");
		setShowPreviousStates(false);
		clearPreviousValues();
	    }
	}
	if (component.equals(matchFrequencies)) {
	    if (matchFrequencies.isSelected()) {
		m_omegaSlider.setValue(new Double(Math.sqrt(m_magFieldSlider
			.getValue())).intValue() * 10);
	    }
	    m_omegaSlider.setEnabled(!matchFrequencies.isSelected());
	}
    }

    /**
     * Removes any track dots.
     */
    private void clearPreviousValues() {
	for (Vector<TrackDot> trackDots : allTracks.values()) {
	    for (TrackDot trackDot : trackDots) {
		trackDot.setVisible(false);
	    }
	    trackDots.removeAllElements();
	}
	allTracks.clear();
    }

    /*
     * Used to listen to the JSlider components.
     * 
     * @see
     * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
     * )
     */
    public void stateChanged(ChangeEvent e) {
	Object component = e.getSource();
	// If the slider changed, then set it's value.
	if (component.equals(m_magFieldSlider)) {
	    m_magFieldValueLabel.setText(new Double(((double) m_magFieldSlider
		    .getValue()) / 10).toString());
	    if (matchFrequencies.isSelected()) {
		m_omegaSlider.setValue(new Double(Math.sqrt(m_magFieldSlider
			.getValue() / 10) * 100).intValue());
	    }
	}
	if (component.equals(m_magPerpFieldSlider))
	    m_magPerpValueLabel
		    .setText(new Double(((double) m_magPerpFieldSlider
			    .getValue()) / 10).toString());
	if (component.equals(m_omegaSlider)) {
	    m_omegaValueLabel.setText(new Double(((double) m_omegaSlider
		    .getValue()) / 100).toString());
	}
    }

    public String getTabName() {
	return "Rabi Field";
    }

    public synchronized boolean isShowPreviousStates() {
	return showPreviousStates;
    }

    public synchronized void setShowPreviousStates(boolean showPreviousStates) {
	this.showPreviousStates = showPreviousStates;
    }
}
