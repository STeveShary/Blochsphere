package edu.uc.ece.blochSphere;

////////////////////////////////////////////////////////
//Created by Nick Vatamaniuc
//Univ of Cincinnati, Quantum Computing, Spring 2004
/////////////////////////////////////////////////////////
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.media.j3d.BranchGroup;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import javax.vecmath.Color3f;

public class Qubit {

    protected Qubit3DModel qubitModel3D;
    // gui components
    protected JSlider thetaAngleJSlider;
    protected JSlider phiAngleJSlider;
    protected JFormattedTextField thetaAngleTextField;
    protected JFormattedTextField phiAngleTextField;
    protected JTextField alphaTextField;
    protected JTextField betaTextField;
    protected JLabel normLabel;
    protected JCheckBox displayQbitCheckBox;
    protected JPanel qbitPanel = new JPanel();
    protected double m_alphaValue = 0.0;
    protected double m_betaRealValue = 0.0;
    protected double m_betaImaginaryValue = 0.0;
    protected double m_vectorMagnitude = 0.0;
    protected double m_thetaAngle = 0.0;
    protected double m_phiAngle = 0.0;
    // the qubit title that is displayed.
    protected String title;
    // The number of the qubit
    protected int qbitNumber = -1;
    // When set to true, this will show the projection on the X-Y plane.
    protected boolean showProjection;
    protected Color3f qubitColor;
    protected Trace3D hadamardTrace;
    protected boolean showHadamardTrace = false;

    /**
     * The default constructor.
     * 
     * @param id
     *            The number of the qubit.
     * @param th
     * @param phi
     * @param color
     */
    public Qubit(int id, float th, float phi, Color3f color,
	    BranchGroup qubitBranchGroup) {
	this.qubitModel3D = new Qubit3DModel(th, phi, color, qubitBranchGroup);
	this.m_thetaAngle = th;
	this.m_phiAngle = phi;
	this.title = "Qubit State " + id;
	this.qbitNumber = id;
	this.qubitColor = color;
	qubitModel3D.setThetaPhi(th, phi);
	makeQbitGUI();
	updateAlphaBeta(th, phi);
    }

    // >>>>>>>>>> USE THIS TO SET ALPHA BETA FROM ANYWHERE <<<<<<<<<<<<<<<<
    public void setAlphaBeta(double newa, double newb, double newbi) {
	double new_phi_deg = (float) (Math.toDegrees(Math.atan2(newbi, newb)));
	if (new_phi_deg < 0)
	    new_phi_deg += 360.0f;
	double new_th_deg = (float) (Math.toDegrees(2.0 * Math.acos(newa)));
	this.m_alphaValue = newa;
	this.m_betaRealValue = newb;
	this.m_betaImaginaryValue = newbi;
	writeAlphaBeta(newa, newb, newbi);
	this.m_thetaAngle = new_th_deg % 180;
	this.m_phiAngle = new_phi_deg % 360;
	m_thetaAngle = cleanNegativeZero(m_thetaAngle);
	m_phiAngle = cleanNegativeZero(m_phiAngle);
	this.thetaAngleJSlider.setValue((int) new_th_deg);
	this.phiAngleJSlider.setValue((int) new_phi_deg);
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void setTh(double new_th) {
	new_th = new_th % 360;
	m_thetaAngle = new_th;
	qubitModel3D.setThetaAngle((float) new_th);
	updateAlphaBeta(m_thetaAngle, m_phiAngle);
    }

    public void setPhi(double new_phi) {
	new_phi = new_phi % 360;
	m_phiAngle = new_phi;
	qubitModel3D.setPhiAngle((float) new_phi);
	updateAlphaBeta(m_thetaAngle, m_phiAngle);
	phiAngleJSlider.setValue(new Double(m_phiAngle).intValue());
	phiAngleTextField.setValue(new Double(m_phiAngle));
    }

    public double getTh() {
	return m_thetaAngle;
    }

    public double getPhi() {
	return m_phiAngle;
    }

    /**
     * Sets the coords of the qubit. We assume that they are normalized. This
     * will update the phi and theta values as well as the alpha and beta
     * values.
     * 
     * @param x
     *            the x magitude of the qubit.
     * @param y
     *            the y magitude of the qubit.
     * @param z
     *            the z magitude of the qubit.
     */
    public void setQubitCoords(double x, double y, double z) {
	qubitModel3D.setQubitCoords(x, y, z);
    }

    /**
     * Returns if the current QBit model is visisble.
     */
    public boolean isVisible() {
	return displayQbitCheckBox.isSelected();
    }

    /**
     * Updates the changes to the theta and the phi angles. THis will apply the
     * changes to the appropriate visual components and apply the changes the
     * Qbit in the 3d simulator.
     */
    public void updateModifiedThetaPhi() {
	qubitModel3D.setThetaPhi(new Double(m_phiAngle).floatValue(),
		new Double(m_phiAngle).floatValue());
	updateAlphaBeta(m_thetaAngle, m_phiAngle);
	this.phiAngleJSlider.setValue(new Double(m_phiAngle).intValue());
	this.thetaAngleJSlider.setValue(new Double(m_thetaAngle).intValue());
    }

    /**
     * Allows the theta and Phi angle to be set manually. All visual components
     * will be updated because of this.
     * 
     * @param newTheta
     *            a double of the new angle.
     * @param newPhi
     *            a double of the new angle.
     */
    public void setThetaPhi(double newTheta, double newPhi) {
	m_thetaAngle = newTheta;
	m_phiAngle = newPhi;
	updateModifiedThetaPhi();
    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void makeQbitGUI() {
	qbitPanel.setLayout(new GridBagLayout());
	GridBagConstraints con = new GridBagConstraints();
	JPanel p = createAngleControlPanel();
	con = new GridBagConstraints();
	con.gridx = 0;
	con.gridy = 0;
	con.gridwidth = 2;
	con.anchor = GridBagConstraints.WEST;
	con.gridwidth = GridBagConstraints.REMAINDER;
	con.fill = GridBagConstraints.HORIZONTAL;
	qbitPanel.add(p, con);
	con.gridx = 0;
	con.gridy = 1;
	con.gridwidth = 2;
	con.anchor = GridBagConstraints.WEST;
	JPanel abPanel = buildAlphaBetaPanel();
	Dimension size = p.getPreferredSize();
	// size.height=size.height-10;
	abPanel.setPreferredSize(size);
	abPanel.setMinimumSize(size);
	qbitPanel.add(abPanel, con);
	con.gridx = 0;
	con.gridy = 2;
	con.gridwidth = 2;
	JPanel optionsPanel = createOptionsPanel();
	qbitPanel.add(optionsPanel, con);
	qbitPanel.setBackground(new Color(qubitColor.x, qubitColor.y,
		qubitColor.z));
	qbitPanel.setMaximumSize(new Dimension(400, 200));
	qbitPanel.setMinimumSize(new Dimension(200, 200));
    }

    public JPanel createOptionsPanel() {
	JPanel p = new JPanel();
	p.setLayout(new GridBagLayout());
	p.setBorder(BorderFactory.createEtchedBorder());
	GridBagConstraints con = new GridBagConstraints();
	con.anchor = GridBagConstraints.WEST;
	con.gridx = 0;
	con.gridy = 0;
	displayQbitCheckBox = new JCheckBox("Display ");
	displayQbitCheckBox.setSelected(true);
	displayQbitCheckBox.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		qubitModel3D.setVisible(displayQbitCheckBox.isSelected());
	    }
	});
	p.add(displayQbitCheckBox, con);
	Dimension fixed = new Dimension(100, 50);
	p.setMinimumSize(fixed);
	p.setPreferredSize(fixed);
	return p;
    }

    public static float string2Float(String s) {
	float r;
	if (s.equals("1/sqrt(2)"))
	    return 0.0707f;
	if (s.equals("-1/sqrt(2)"))
	    return -0.0707f;
	if (s.equals(""))
	    return 0;
	else {
	    try {
		r = Float.parseFloat(s);
	    } catch (NumberFormatException nfe) {
		r = Float.NaN;
	    }
	}
	return r;
    }

    /**
     * Updates the Qbit by setting the alpha and beta values.
     * 
     * 
     * @param alpha
     *            a String value of the alpha value.
     * @param betaReal
     *            a String value of the beta Real value.
     * @param betaImaginary
     *            a String value of the beta Imaginary value.
     */
    public void updateThetaPhi(double alpha, double betaReal,
	    double betaImaginary) {
	double norm = getNorm(alpha, betaReal, betaImaginary);
	if (norm < 0.98 || norm > 1.03) {
	    alpha /= norm;
	    betaReal /= norm;
	    betaImaginary /= norm;
	}
	setAlphaBeta(alpha, betaReal, betaImaginary);
	normLabel.setText(" Norm: " + formatNumber(norm));
    }

    /**
     * Retuns the norm of the vector in the form
     * 
     * alphReal + (betaReal + i(betaImaginary))
     * 
     * @param alphaReal
     * @param betaReal
     * @param betaImaginary
     * 
     * @return the norm of the vector as a scalar.
     */
    public double getNorm(double alphaReal, double betaReal,
	    double betaImaginary) {
	return (float) (Math.sqrt(alphaReal * alphaReal + betaReal * betaReal
		+ betaImaginary * betaImaginary));
    }

    /**
     * Updates the alpha and beta values with the new theta and Phi values.
     * 
     * @param thetaAngle
     *            a float of the angle (0 through 360), not radians.
     * @param phiAngle
     *            a float of the angle (0 through 360), not radians.
     */
    protected void updateAlphaBeta(double thetaAngle, double phiAngle) {
	thetaAngle = Math.toRadians(thetaAngle);
	phiAngle = Math.toRadians(phiAngle);
	double newa = Math.cos(thetaAngle / 2);
	if (m_alphaValue < 0)
	    newa = 0f;
	double newb = (Math.cos(phiAngle) * Math.sin(thetaAngle / 2));
	double newbi = (Math.sin(phiAngle) * Math.sin(thetaAngle / 2));
	this.m_alphaValue = newa;
	this.m_betaRealValue = newb;
	this.m_betaImaginaryValue = newbi;
	writeAlphaBeta(m_alphaValue, m_betaRealValue, m_betaImaginaryValue);
    }

    public void writeAlphaBeta(double newa, double newb, double newbi) {
	alphaTextField.setText(new ComplexNumber(newa, 0).toString(3));
	betaTextField.setText(new ComplexNumber(newb, newbi).toString(3));
    }

    public static String formatNumber(float n) {
	float sqrtUpper = 0.710f;
	float sqrtLower = 0.700f;
	float zeroUpper = 0.015f;
	float oneLower = 0.9995f;
	NumberFormat nf = new DecimalFormat("0.000");
	String rs;
	if (n >= sqrtLower && n <= sqrtUpper)
	    rs = "1/sqrt(2)";
	else if (n >= -sqrtUpper && n <= -sqrtLower)
	    rs = "-1/sqrt(2)";
	else if (n >= 0f && n <= zeroUpper)
	    rs = "0";
	else if (n >= oneLower && n <= 1f)
	    rs = "1";
	else if (n >= -1f && n <= -oneLower)
	    rs = "-1";
	else
	    rs = nf.format(n);
	return rs;
    }

    public static String formatNumber(double n) {
	float sqrtUpper = 0.710f;
	float sqrtLower = 0.700f;
	float zeroUpper = 0.015f;
	float oneLower = 0.9995f;
	NumberFormat nf = new DecimalFormat("0.000");
	String rs;
	if (n >= sqrtLower && n <= sqrtUpper)
	    rs = "1/sqrt(2)";
	else if (n >= -sqrtUpper && n <= -sqrtLower)
	    rs = "-1/sqrt(2)";
	else if (n >= 0f && n <= zeroUpper)
	    rs = "0";
	else if (n >= oneLower && n <= 1f)
	    rs = "1";
	else if (n >= -1f && n <= -oneLower)
	    rs = "-1";
	else
	    rs = nf.format(n);
	return rs;
    }

    public static String beautifyDouble(double v) {
	NumberFormat nf = new DecimalFormat("0.00");
	return nf.format(v);
    }

    public void setAlphaBetaValues(ComplexNumber alpha, ComplexNumber beta) {
	if (alpha.equals(new ComplexNumber(0, 0))
		&& beta.equals(new ComplexNumber(0, 0))) {
	    return;
	}
	setAlphaBetaFullValues(alpha.getRealPart(), alpha.getImaginaryPart(),
		beta.getRealPart(), beta.getImaginaryPart());
    }

    /**
     * This will set the beta and alpha values in the application. Normally, we
     * are looking at the field of alpha and beta to be in C^2, but for
     * simplicity (and it was originally this way), we are using {R, C} for the
     * field for the qbit. Therefore this function will map from {C^2} to {R, C}
     * and then normalize the value and set it to the current alpha and beta.
     * 
     * @param alphaRealValue
     *            the alpha real value in C^2
     * @param alphaImaginaryValue
     *            the alpha imaginary value in C^2
     * @param betaRealValue
     *            the beta real value in C^2
     * @param betaImaginaryValue
     *            the beta imaginary value in C^2
     */
    public void setAlphaBetaFullValues(double alphaRealValue,
	    double alphaImaginaryValue, double betaRealValue,
	    double betaImaginaryValue) {
	double newAlphaReal = alphaRealValue * alphaRealValue
		+ alphaImaginaryValue * alphaImaginaryValue;
	double newBetaReal = alphaRealValue * betaRealValue
		+ alphaImaginaryValue * betaImaginaryValue;
	double newBetaImaginary = alphaRealValue * betaImaginaryValue
		- betaRealValue * alphaImaginaryValue;
	// If there is no imaginary part of alpha, then we don't actually do the
	// calculation. This fixes a
	// problem when alpha = 0 + 0i.
	if (alphaImaginaryValue == 0.0) {
	    newAlphaReal = alphaRealValue;
	    newBetaReal = betaRealValue;
	    newBetaImaginary = betaImaginaryValue;
	}
	newAlphaReal = cleanNegativeZero(newAlphaReal);
	newBetaReal = cleanNegativeZero(newBetaReal);
	newBetaImaginary = cleanNegativeZero(newBetaImaginary);
	double norm = Math.sqrt(newAlphaReal * newAlphaReal + newBetaReal
		* newBetaReal + newBetaImaginary * newBetaImaginary);
	// now a is real, need to normalize though
	setAlphaBeta((double) (newAlphaReal / norm),
		(double) (newBetaReal / norm),
		(double) (newBetaImaginary / norm));
    }

    /**
     * Fixes the negative zero problem. Java creates -0.0 for instances where
     * the double is sooo small that it cannot be expressed otherwise.
     * 
     * @param test
     *            a double to test clean if it is -0.0
     * 
     * @return if the double is 0.0 or -0.0, then 0,0, otherwise the value of
     *         the double.
     */
    private double cleanNegativeZero(double test) {
	if (test == 0.0)
	    return 0.0;
	else
	    return test;
    }

    public void setThetaPhiText() {
	thetaAngleTextField.setText(beautifyDouble(m_thetaAngle));
	phiAngleTextField.setText(beautifyDouble(m_phiAngle));
	thetaAngleJSlider.setValue((int) m_thetaAngle);
	phiAngleJSlider.setValue((int) m_phiAngle);
	return;
    }

    /**
     * Builds the panel that controls the current state of the qubit.
     * 
     * @return the panel that contols the current state of the qubit.
     */
    public JPanel buildAlphaBetaPanel() {
	JPanel alphaBetaPanel = new JPanel();
	SpringLayout layout = new SpringLayout();
	alphaBetaPanel.setLayout(layout);
	Border eborder = BorderFactory.createEtchedBorder();
	alphaBetaPanel.setBorder(eborder);
	JLabel alphaTextLabel = new JLabel("Alpha: ");
	JLabel betaTextLabel = new JLabel("Beta : ");
	normLabel = new JLabel("");
	alphaTextField = new JTextField(12);
	betaTextField = new JTextField(12);
	JButton updateQubitButton = new JButton("Update Value");
	updateQubitButton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent ae) {
		ComplexNumber alpha = new ComplexNumber(alphaTextField
			.getText());
		ComplexNumber beta = new ComplexNumber(betaTextField.getText());
		updateThetaPhi(alpha.getRealPart(), beta.getRealPart(), beta
			.getImaginaryPart());
	    }
	});
	alphaBetaPanel.add(alphaTextLabel);
	alphaBetaPanel.add(betaTextLabel);
	alphaBetaPanel.add(alphaTextField);
	alphaBetaPanel.add(betaTextField);
	alphaBetaPanel.add(updateQubitButton);
	layout.putConstraint(SpringLayout.NORTH, alphaTextLabel, 5,
		SpringLayout.NORTH, alphaBetaPanel);

	layout.putConstraint(SpringLayout.NORTH, alphaTextField, 0,
		SpringLayout.NORTH, alphaTextLabel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, alphaTextField, 5,
		SpringLayout.HORIZONTAL_CENTER, alphaBetaPanel);

	layout.putConstraint(SpringLayout.EAST, alphaTextLabel, -0,
		SpringLayout.WEST, alphaTextField);

	layout.putConstraint(SpringLayout.NORTH, betaTextLabel, 5,
		SpringLayout.SOUTH, alphaTextLabel);
	layout.putConstraint(SpringLayout.WEST, betaTextLabel, 0,
		SpringLayout.WEST, alphaTextLabel);

	layout.putConstraint(SpringLayout.NORTH, betaTextField, 0,
		SpringLayout.NORTH, betaTextLabel);
	layout.putConstraint(SpringLayout.WEST, betaTextField, 0,
		SpringLayout.WEST, alphaTextField);

	layout.putConstraint(SpringLayout.NORTH, updateQubitButton, 5,
		SpringLayout.SOUTH, betaTextField);
	layout.putConstraint(SpringLayout.WEST, updateQubitButton, 0,
		SpringLayout.WEST, betaTextField);

	return alphaBetaPanel;
    }

    public JPanel createAngleControlPanel() {
	JPanel p = new JPanel();
	p.setLayout(new GridBagLayout());
	Border eborder = BorderFactory.createEtchedBorder();
	p.setBorder(eborder);
	Font font = new Font("Dialog", Font.PLAIN, 9);
	Font fontb = new Font("Dialog", Font.BOLD, 9);
	thetaAngleJSlider = new JSlider(JSlider.HORIZONTAL, 0, 180,
		(int) m_thetaAngle);
	thetaAngleJSlider.addChangeListener(new ChangeListener() {

	    public void stateChanged(ChangeEvent e) {
		JSlider s = (JSlider) e.getSource();
		double value = (double) s.getValue();
		if (Math.abs(value - m_thetaAngle) <= 1) {
		    value = m_thetaAngle;
		}
		setTh(value);
		thetaAngleTextField.setText(beautifyDouble(value));
		normLabel.setText(" ");
	    }
	});
	thetaAngleJSlider.setMajorTickSpacing(45);
	thetaAngleJSlider.setMinorTickSpacing(5);
	thetaAngleJSlider.setPaintTicks(true);
	thetaAngleJSlider.setPaintLabels(true);
	JLabel l0 = new JLabel("0");
	l0.setFont(font);
	JLabel l45 = new JLabel("45");
	l45.setFont(font);
	JLabel l90 = new JLabel("90");
	l90.setFont(font);
	JLabel l135 = new JLabel("135");
	l135.setFont(font);
	JLabel l180 = new JLabel("180");
	l180.setFont(font);
	JLabel l270 = new JLabel("270");
	l270.setFont(font);
	JLabel l360 = new JLabel("360");
	l360.setFont(font);
	Hashtable<Integer, JLabel> th_labtab = new Hashtable<Integer, JLabel>();
	th_labtab.put(new Integer(0), l0);
	th_labtab.put(new Integer(45), l45);
	th_labtab.put(new Integer(90), l90);
	th_labtab.put(new Integer(135), l135);
	th_labtab.put(new Integer(180), l180);
	thetaAngleJSlider.setLabelTable(th_labtab);
	phiAngleJSlider = new JSlider(JSlider.HORIZONTAL, 0, 360,
		(int) m_phiAngle);
	phiAngleJSlider.addChangeListener(new ChangeListener() {

	    public void stateChanged(ChangeEvent e) {
		JSlider s = (JSlider) e.getSource();
		double value = (double) s.getValue();
		if (Math.abs(value - m_phiAngle) <= 1) {
		    value = m_phiAngle;
		}
		setPhi(value);
		phiAngleTextField.setText(beautifyDouble(value));
		normLabel.setText(" ");
	    }
	});
	Hashtable<Integer, JLabel> phi_labtab = new Hashtable<Integer, JLabel>();
	phi_labtab.put(new Integer(0), l0);
	phi_labtab.put(new Integer(90), l90);
	phi_labtab.put(new Integer(180), l180);
	phi_labtab.put(new Integer(270), l270);
	phi_labtab.put(new Integer(360), l360);
	phiAngleJSlider.setLabelTable(phi_labtab);
	phiAngleJSlider.setMajorTickSpacing(90);
	phiAngleJSlider.setMinorTickSpacing(10);
	phiAngleJSlider.setPaintTicks(true);
	phiAngleJSlider.setPaintLabels(true);
	NumberFormatter formatter_th = new NumberFormatter();
	formatter_th.setMinimum(new Float(0));
	formatter_th.setMaximum(new Float(180));
	NumberFormatter formatter_phi = new NumberFormatter();
	formatter_phi.setMinimum(new Float(0));
	formatter_phi.setMaximum(new Float(360));
	thetaAngleTextField = new JFormattedTextField(formatter_th);
	phiAngleTextField = new JFormattedTextField(formatter_phi);
	GridBagConstraints con = new GridBagConstraints();
	// add components to our gui
	// ===THETA=====
	JLabel lab_name_th = new JLabel("Theta");
	lab_name_th.setFont(fontb);
	con.gridx = 0;
	con.gridy = 0;
	con.gridwidth = 1;
	con.fill = GridBagConstraints.BOTH;
	p.add(lab_name_th, con);
	con.fill = GridBagConstraints.NONE;
	con.gridx = 1;
	con.gridy = 0;
	con.gridwidth = GridBagConstraints.REMAINDER;
	con.gridheight = 2;
	p.add(thetaAngleJSlider, con);
	con.gridx = 0;
	con.gridy = 1;
	con.gridwidth = 1;
	con.gridheight = 1;
	thetaAngleTextField.setFont(fontb);
	p.add(thetaAngleTextField, con);
	// =====PHI======
	JLabel lab_name_phi = new JLabel("Phi");
	lab_name_phi.setFont(fontb);
	con.gridx = 0;
	con.gridy = 2;
	con.gridwidth = 1;
	con.gridheight = 1;
	con.fill = GridBagConstraints.BOTH;
	p.add(lab_name_phi, con);
	con.fill = GridBagConstraints.NONE;
	con.gridx = 1;
	con.gridy = 2;
	con.gridwidth = GridBagConstraints.REMAINDER;
	con.gridheight = 2;
	p.add(phiAngleJSlider, con);
	con.gridx = 0;
	con.gridy = 3;
	con.gridwidth = 1;
	con.gridheight = 1;
	phiAngleTextField.setFont(fontb);
	p.add(phiAngleTextField, con);
	thetaAngleTextField.setValue(new Float(m_thetaAngle));
	phiAngleTextField.setValue(new Float(m_phiAngle));
	thetaAngleTextField.setColumns(5);
	phiAngleTextField.setColumns(5);
	thetaAngleTextField.getInputMap().put(
		KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
	thetaAngleTextField.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		if (!thetaAngleTextField.isEditValid()) {
		    Toolkit.getDefaultToolkit().beep();
		    thetaAngleTextField.selectAll();
		} else
		    try {
			thetaAngleTextField.commitEdit();
		    } catch (java.text.ParseException exc) {
		    }
	    }
	});
	thetaAngleTextField
		.addPropertyChangeListener(new PropertyChangeListener() {

		    public void propertyChange(PropertyChangeEvent evt) {
			if ((evt.getPropertyName()).equals("value")) {
			    Number value = (Number) evt.getNewValue();
			    // System.out.println("New Theta Text Entered:
			    // "+value+" oldtheta:
			    // "+th);
			    double new_th = value.doubleValue();
			    m_thetaAngle = new_th;
			    thetaAngleJSlider.setValue(value.intValue());
			    updateAlphaBeta(new_th, m_phiAngle);
			    normLabel.setText(" ");
			    // sl_th.setValue(value.intValue());
			}
		    }
		});
	phiAngleTextField.getInputMap().put(
		KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
	phiAngleTextField.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		if (!phiAngleTextField.isEditValid()) {
		    Toolkit.getDefaultToolkit().beep();
		    phiAngleTextField.selectAll();
		} else
		    try {
			phiAngleTextField.commitEdit();// will fire a property
			// changed
			// event
		    } catch (java.text.ParseException exc) {
		    }
	    }
	});
	phiAngleTextField
		.addPropertyChangeListener(new PropertyChangeListener() {

		    public void propertyChange(PropertyChangeEvent evt) {
			if ((evt.getPropertyName()).equals("value")) {
			    Number value = (Number) evt.getNewValue();
			    double new_phi = value.doubleValue();
			    phiAngleJSlider.setValue(value.intValue());
			    m_phiAngle = new_phi;
			    // if(new_phi==phi) return;
			    updateAlphaBeta(m_thetaAngle, new_phi);
			    normLabel.setText(" ");
			}
		    }
		});
	return p;
    }

    public JPanel getQbitPanel() {
	return this.qbitPanel;
    }

    public void setVisible(boolean visible) {
	qubitModel3D.setVisible(visible);
	displayQbitCheckBox.setSelected(visible);
    }

    public void changeMag(float mag) {
	qubitModel3D.setMagnitude(mag);
    }

    /**
     * Returns the current alpha value of the qubit by getting the value from
     * the text field.
     * 
     * @return a Complex number of the alpha value of the qubit.
     */
    public ComplexNumber getAlphaValue() {
	return new ComplexNumber(alphaTextField.getText());
    }

    /**
     * Returns the current beta value of the qubit by getting the value from the
     * text field.
     * 
     * @return a Complex number of the beta value of the qubit.
     */
    public ComplexNumber getBetaValue() {
	return new ComplexNumber(this.betaTextField.getText());
    }

    public Color3f getQubitColor() {
	return qubitColor;
    }
}
