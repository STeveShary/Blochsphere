package edu.uc.ece.blochSphere;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.uc.ece.blochSphere.operator.Operator;

public class QubitOperator implements ActionListener {

    protected JPanel mainControlPanel = new JPanel();
    protected JPanel controlPanel = new JPanel();
    protected JPanel buttonPanel = new JPanel();
    protected JPanel operatorPanel = new JPanel();
    protected JLabel instructions = new JLabel(
	    "<html><center><b>Instructions:</b></center><br>"
		    + "Enter the four (complex) values of the operator<br>"
		    + " that you want to act upon the Qubits.  You can also <br>"
		    + "click on a button on the left side to populate popular <br>"
		    + "operators.  The operator M (set below) acts upon the qubits<br>"
		    + " like M|&psi;></html>");

    protected JLabel operatorLabel = new JLabel("M = ");
    protected JLabel rightParenLabel = new JLabel("(");
    protected JLabel leftParenLabel = new JLabel(")");

    protected JTextField alphaTextField = new JTextField(10);
    protected JTextField betaTextField = new JTextField(10);
    protected JTextField gammaTextField = new JTextField(10);
    protected JTextField deltaTextField = new JTextField(10);

    protected JButton applyToVisible = new JButton("Apply To Visible Qubits");
    protected JButton applyToAll = new JButton("Apply To All Qubits");

    protected JButton xOperatorButton = new JButton("X");
    protected JButton yOperatorButton = new JButton("Y");
    protected JButton zOperatorButton = new JButton("Z");
    protected JButton hOperatorButton = new JButton("H");
    protected JButton sOperatorButton = new JButton("S");
    protected JButton tOperatorButton = new JButton("T");
    protected JButton rXOperatorButton = new JButton(
	    "<html>R<sub>x</sub>(&Theta;)</html>");
    protected JButton rYOperatorButton = new JButton(
	    "<html>R<sub>y</sub>(&Theta;)</html>");
    protected JButton rZOperatorButton = new JButton(
	    "<html>R<sub>z</sub>(&Theta;)</html>");

    protected SpringLayout layout = new SpringLayout();

    protected Vector<Qubit> qubitVector = null;

    public QubitOperator(Vector<Qubit> qubitVector) {
	buildPanel();
	this.qubitVector = qubitVector;
    }

    protected void buildPanel() {
	SpringLayout layout = new SpringLayout();
	controlPanel.setLayout(layout);
	controlPanel.add(instructions);
	controlPanel.add(operatorLabel);
	controlPanel.add(rightParenLabel);
	controlPanel.add(leftParenLabel);
	controlPanel.add(alphaTextField);
	controlPanel.add(betaTextField);
	controlPanel.add(gammaTextField);
	controlPanel.add(deltaTextField);

	alphaTextField.setHorizontalAlignment(JTextField.CENTER);
	betaTextField.setHorizontalAlignment(JTextField.CENTER);
	gammaTextField.setHorizontalAlignment(JTextField.CENTER);
	deltaTextField.setHorizontalAlignment(JTextField.CENTER);
	Font labelFont = rightParenLabel.getFont();
	Font bigFont = new Font(labelFont.getName(), labelFont.getStyle(), 39);
	rightParenLabel.setFont(bigFont);
	leftParenLabel.setFont(bigFont);

	layout.putConstraint(SpringLayout.WEST, operatorLabel, 0,
		SpringLayout.WEST, controlPanel);
	layout.putConstraint(SpringLayout.VERTICAL_CENTER, operatorLabel, 0,
		SpringLayout.VERTICAL_CENTER, controlPanel);

	layout.putConstraint(SpringLayout.NORTH, rightParenLabel, 0,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, rightParenLabel, 5,
		SpringLayout.EAST, operatorLabel);

	layout.putConstraint(SpringLayout.NORTH, alphaTextField, 5,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, alphaTextField, 5,
		SpringLayout.EAST, rightParenLabel);

	layout.putConstraint(SpringLayout.NORTH, betaTextField, 5,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, betaTextField, 5,
		SpringLayout.EAST, alphaTextField);

	layout.putConstraint(SpringLayout.NORTH, deltaTextField, 5,
		SpringLayout.SOUTH, alphaTextField);
	layout.putConstraint(SpringLayout.WEST, deltaTextField, 0,
		SpringLayout.WEST, alphaTextField);

	layout.putConstraint(SpringLayout.NORTH, gammaTextField, 5,
		SpringLayout.SOUTH, betaTextField);
	layout.putConstraint(SpringLayout.WEST, gammaTextField, 0,
		SpringLayout.WEST, betaTextField);

	layout.putConstraint(SpringLayout.EAST, controlPanel, 0,
		SpringLayout.EAST, leftParenLabel);
	layout.putConstraint(SpringLayout.SOUTH, controlPanel, 0,
		SpringLayout.SOUTH, deltaTextField);

	layout.putConstraint(SpringLayout.NORTH, leftParenLabel, 0,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, leftParenLabel, 5,
		SpringLayout.EAST, betaTextField);

	applyToVisible.addActionListener(this);
	applyToAll.addActionListener(this);
	buttonPanel.add(applyToVisible);
	buttonPanel.add(applyToAll);

	buildOperatorPanel();

	SpringLayout mainLayout = new SpringLayout();

	controlPanel.revalidate();
	mainControlPanel.setLayout(mainLayout);
	mainControlPanel.add(instructions);
	mainControlPanel.add(controlPanel);
	mainControlPanel.add(operatorPanel);
	mainControlPanel.add(buttonPanel);
	mainLayout.putConstraint(SpringLayout.NORTH, instructions, 10,
		SpringLayout.NORTH, mainControlPanel);
	mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructions,
		0, SpringLayout.HORIZONTAL_CENTER, mainControlPanel);

	mainLayout.putConstraint(SpringLayout.VERTICAL_CENTER, operatorPanel,
		0, SpringLayout.VERTICAL_CENTER, mainControlPanel);
	mainLayout.putConstraint(SpringLayout.WEST, operatorPanel, 5,
		SpringLayout.EAST, instructions);

	mainLayout.putConstraint(SpringLayout.VERTICAL_CENTER, controlPanel, 0,
		SpringLayout.VERTICAL_CENTER, operatorPanel);
	mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, controlPanel,
		0, SpringLayout.HORIZONTAL_CENTER, mainControlPanel);

	mainLayout.putConstraint(SpringLayout.NORTH, buttonPanel, 10,
		SpringLayout.SOUTH, operatorPanel);
	mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, buttonPanel,
		0, SpringLayout.HORIZONTAL_CENTER, mainControlPanel);
	mainControlPanel.revalidate();
    }

    private void buildOperatorPanel() {
	operatorPanel.setLayout(new BoxLayout(operatorPanel, BoxLayout.Y_AXIS));
	xOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	yOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	zOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	hOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	sOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	tOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	rXOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	rYOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	rZOperatorButton.setAlignmentX(Component.CENTER_ALIGNMENT);

	xOperatorButton.addActionListener(this);
	yOperatorButton.addActionListener(this);
	zOperatorButton.addActionListener(this);
	hOperatorButton.addActionListener(this);
	sOperatorButton.addActionListener(this);
	tOperatorButton.addActionListener(this);
	rXOperatorButton.addActionListener(this);
	rYOperatorButton.addActionListener(this);
	rZOperatorButton.addActionListener(this);

	operatorPanel.add(xOperatorButton);
	operatorPanel.add(yOperatorButton);
	operatorPanel.add(zOperatorButton);
	operatorPanel.add(hOperatorButton);
	operatorPanel.add(sOperatorButton);
	operatorPanel.add(tOperatorButton);
	operatorPanel.add(rXOperatorButton);
	operatorPanel.add(rYOperatorButton);
	operatorPanel.add(rZOperatorButton);
    }

    /**
     * Returns a reference to the control panel that can be used to display the
     * panel and apply the operators. This should always be built when the
     * object is constructed.
     */
    public JPanel getControlPanel() {
	return mainControlPanel;
    }

    /**
     * Gets the name associated with this tab.
     * 
     * @return The name that should be displayed on this tab.
     */
    public String getTabName() {
	return "Operator";
    }

    public JButton getApplyAllButton() {
	return applyToAll;
    }

    public JButton getApplyVisibleButton() {
	return applyToVisible;
    }

    /**
     * This is called when a click event is fired on one of the custom operator.
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource().equals(applyToVisible)) {
	    if (checkOperator()) {
		applyOperation(getApplicableQubits(true));
	    }
	} else if (e.getSource().equals(this.applyToAll)) {
	    if (checkOperator()) {
		applyOperation(getApplicableQubits(false));
	    }
	} else if (e.getSource().equals(this.xOperatorButton)) {
	    setOperatorvalues("0", "1", "1", "0");
	} else if (e.getSource().equals(this.yOperatorButton)) {
	    setOperatorvalues("0", "-i", "i", "0");
	} else if (e.getSource().equals(this.zOperatorButton)) {
	    setOperatorvalues("1", "0", "0", "-1");
	} else if (e.getSource().equals(this.hOperatorButton)) {
	    setOperatorvalues("0.7071", "0.7071", "0.7071", "-0.7071");
	} else if (e.getSource().equals(this.sOperatorButton)) {
	    setOperatorvalues("1", "0", "0", "i");
	} else if (e.getSource().equals(this.tOperatorButton)) {
	    setOperatorvalues("1", "0", "0", "0.707+0.707i");
	} else if (e.getSource().equals(this.rXOperatorButton)) {
	    Double angle = getRotationAngle();
	    if (angle == null) {
		return;
	    }
	    ComplexNumber topLeft = new ComplexNumber(Math.cos(angle / 2), 0);
	    ComplexNumber topRight = new ComplexNumber(0, -1
		    * Math.sin(angle / 2));
	    ComplexNumber bottomLeft = new ComplexNumber(0, -1
		    * Math.sin(angle / 2));
	    ComplexNumber bottomRight = new ComplexNumber(Math.cos(angle / 2),
		    0);
	    setOperatorvalues(topLeft.toString(3), topRight.toString(3),
		    bottomLeft.toString(3), bottomRight.toString(3));
	} else if (e.getSource().equals(this.rYOperatorButton)) {
	    Double angle = getRotationAngle();
	    if (angle == null) {
		return;
	    }
	    ComplexNumber topLeft = new ComplexNumber(Math.cos(angle / 2), 0);
	    ComplexNumber topRight = new ComplexNumber(
		    -1 * Math.sin(angle / 2), 0);
	    ComplexNumber bottomLeft = new ComplexNumber(Math.sin(angle / 2), 0);
	    ComplexNumber bottomRight = new ComplexNumber(Math.cos(angle / 2),
		    0);
	    setOperatorvalues(topLeft.toString(3), topRight.toString(3),
		    bottomLeft.toString(3), bottomRight.toString(3));
	} else if (e.getSource().equals(this.rZOperatorButton)) {
	    Double angle = getRotationAngle();
	    if (angle == null) {
		return;
	    }
	    ComplexNumber topLeft = new ComplexNumber(Math.cos(angle / 2), -1
		    * Math.sin(angle / 2));
	    ComplexNumber topRight = new ComplexNumber(0, 0);
	    ComplexNumber bottomLeft = new ComplexNumber(0, 0);
	    ComplexNumber bottomRight = new ComplexNumber(Math.cos(angle / 2),
		    Math.sin(angle / 2));
	    setOperatorvalues(topLeft.toString(3), topRight.toString(3),
		    bottomLeft.toString(3), bottomRight.toString(3));
	}
    }

    /**
     * Prompts the user to enter the rotation angle (in radians). This will then
     * parse and re-prompt the user to enter and angle if there are any
     * problems.
     * 
     * @return A double value of the rotation angle. If the user has canceled
     *         from the process, then this indicates that the user wants to
     *         cancel the operator action.
     */
    private Double getRotationAngle() {
	Double angle = null;
	while (angle == null) {
	    String angleString = (String) JOptionPane.showInputDialog(this
		    .getControlPanel(),
		    "Please enter the rotation angle (in radians)",
		    "Enter Rotation Angle", JOptionPane.OK_OPTION);
	    if (angleString == null) {
		return null;
	    }
	    try {
		angle = Double.parseDouble(angleString);
	    } catch (NumberFormatException nfe) {
		JOptionPane
			.showMessageDialog(this.getControlPanel(),
				"Please enter a number to represent the angle (in radians).");
	    }
	}
	return angle.doubleValue();
    }

    private void setOperatorvalues(String m11, String m12, String m21,
	    String m22) {
	final String finalM11 = new String(m11);
	final String finalM12 = new String(m12);
	final String finalM21 = new String(m21);
	final String finalM22 = new String(m22);

	Runnable updateOperator = new Runnable() {
	    public void run() {
		alphaTextField.setText(finalM11);
		betaTextField.setText(finalM12);
		deltaTextField.setText(finalM21);
		gammaTextField.setText(finalM22);
	    }
	};

	SwingUtilities.invokeLater(updateOperator);

    }

    /**
     * This will fetch all applicable qubits that should have the operator
     * applied to them.
     * 
     * @param onlyVisibleQubits
     *            if true, then only visible qubits should be returned in the
     *            list.
     * @return A non-null Vector of qubits that should have the operator applied
     *         to them.
     */
    private Vector<Qubit> getApplicableQubits(boolean onlyVisibleQubits) {
	if (onlyVisibleQubits) {
	    Vector<Qubit> visibleQubits = new Vector<Qubit>();
	    for (Qubit qubit : qubitVector) {
		if (qubit.isVisible()) {
		    visibleQubits.add(qubit);
		}
	    }
	    return visibleQubits;
	} else {
	    return qubitVector;
	}
    }

    /**
     * An internal check to verify that the operator is non-null, all entries
     * are well formed complex numbers and that the operator is unitary.
     * 
     * @return true if the operator is non-null, well-formed, and that the
     *         operator values make the operator unitary. otherwise false.
     */
    private boolean checkOperator() {
	Operator operator = getOperatorFromUI();
	if (operator == null) {
	    return false;
	} else if (!operator.isOperatorUnitary()) {
	    JOptionPane.showMessageDialog(this.mainControlPanel,
		    "The operator must be Unitary.", "Operator Value Error",
		    JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	return true;
    }

    /**
     * This will apply the operator on the each of the qubits that are supplied
     * in the Vector. If the Vector is empty, then the operation will not be
     * done.
     * 
     * @param qubits
     *            The qubits that should have the operator applied to them.
     */
    private void applyOperation(Vector<Qubit> qubits) {
	Operator operator = getOperatorFromUI();
	for (Qubit qubit : qubits) {
	    ComplexNumber qubitAlphaNum = new ComplexNumber(qubit.m_alphaValue,
		    0);
	    ComplexNumber qubitBetaNum = new ComplexNumber(
		    qubit.m_betaRealValue, qubit.m_betaImaginaryValue);
	    ComplexNumber productAlpha = operator.applyOperatorToAlpha(
		    qubitAlphaNum, qubitBetaNum);
	    ComplexNumber productBeta = operator.applyOperatorToBeta(
		    qubitAlphaNum, qubitBetaNum);
	    qubit.setAlphaBetaFullValues(productAlpha.getRealPart(),
		    productAlpha.getImaginaryPart(), productBeta.getRealPart(),
		    productBeta.getImaginaryPart());
	}
    }

    /**
     * Fetches the operator from the values that are set on the UI. If the
     * values are not well formed, then this will show a pop-up dialog with the
     * value that is malformed.
     * 
     * @return The operator represented by the values set on the UI. If any of
     *         the values are not valid complex numbers then this will return
     *         null.
     */
    public Operator getOperatorFromUI() {
	String errorValue = null;
	try {
	    errorValue = "Alpha";
	    ComplexNumber alphaNum = new ComplexNumber(alphaTextField.getText());
	    errorValue = "Beta";
	    ComplexNumber betaNum = new ComplexNumber(betaTextField.getText());
	    errorValue = "Gamma";
	    ComplexNumber gammaNum = new ComplexNumber(gammaTextField.getText());
	    errorValue = "Delta";
	    ComplexNumber deltaNum = new ComplexNumber(deltaTextField.getText());
	    return new Operator(alphaNum, betaNum, deltaNum, gammaNum);

	} catch (NumberFormatException ne) {
	    JOptionPane.showMessageDialog(this.mainControlPanel, errorValue
		    + " value must be in the form A+Bi or A-Bi",
		    "Alpha Number Error", JOptionPane.ERROR_MESSAGE);
	    return null;
	}
    }
}
