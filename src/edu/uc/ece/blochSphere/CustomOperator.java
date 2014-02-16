package edu.uc.ece.blochSphere;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import edu.uc.ece.blochSphere.operator.Operator;

public class CustomOperator implements ActionListener {

    protected JPanel mainControlPanel = new JPanel();
    protected JPanel controlPanel = new JPanel();
    protected JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    protected JLabel instructions = new JLabel("Instructions");

    protected JLabel alphaLabel = new JLabel("<html>&Alpha;: </html>");
    protected JLabel betaLabel = new JLabel("<html>&Beta;: </html>");
    protected JLabel gammaLabel = new JLabel("<html>&Gamma;: </html>");
    protected JLabel deltaLabel = new JLabel("<html>&Delta;: </html>");

    protected JTextField alphaTextField = new JTextField(10);
    protected JTextField betaTextField = new JTextField(10);
    protected JTextField gammaTextField = new JTextField(10);
    protected JTextField deltaTextField = new JTextField(10);

    protected JButton applyToVisible = new JButton("Apply To Visible Qubits");
    protected JButton applyToAll = new JButton("Apply To All Qubits");

    protected SpringLayout layout = new SpringLayout();

    protected Vector<Qubit> qubitVector = null;

    public CustomOperator(Vector<Qubit> qubitVector) {
	buildPanel();
	this.qubitVector = qubitVector;
    }

    protected void buildPanel() {
	SpringLayout layout = new SpringLayout();
	controlPanel.setLayout(layout);
	controlPanel.add(instructions);
	controlPanel.add(alphaLabel);
	controlPanel.add(betaLabel);
	controlPanel.add(gammaLabel);
	controlPanel.add(deltaLabel);
	controlPanel.add(alphaTextField);
	controlPanel.add(betaTextField);
	controlPanel.add(gammaTextField);
	controlPanel.add(deltaTextField);

	layout.putConstraint(SpringLayout.NORTH, alphaLabel, 5,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, alphaLabel, 5,
		SpringLayout.WEST, controlPanel);

	layout.putConstraint(SpringLayout.NORTH, alphaTextField, 5,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, alphaTextField, 5,
		SpringLayout.EAST, alphaLabel);
	layout.putConstraint(SpringLayout.NORTH, betaLabel, 5,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, betaLabel, 10,
		SpringLayout.EAST, alphaTextField);
	layout.putConstraint(SpringLayout.NORTH, betaTextField, 5,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, betaTextField, 5,
		SpringLayout.EAST, betaLabel);

	layout.putConstraint(SpringLayout.NORTH, deltaLabel, 10,
		SpringLayout.SOUTH, alphaLabel);
	layout.putConstraint(SpringLayout.WEST, deltaLabel, 5,
		SpringLayout.WEST, controlPanel);
	layout.putConstraint(SpringLayout.NORTH, deltaTextField, 10,
		SpringLayout.SOUTH, alphaLabel);
	layout.putConstraint(SpringLayout.WEST, deltaTextField, 5,
		SpringLayout.EAST, deltaLabel);

	layout.putConstraint(SpringLayout.NORTH, gammaLabel, 10,
		SpringLayout.SOUTH, alphaLabel);
	layout.putConstraint(SpringLayout.WEST, gammaLabel, 10,
		SpringLayout.EAST, deltaTextField);
	layout.putConstraint(SpringLayout.NORTH, gammaTextField, 10,
		SpringLayout.SOUTH, alphaLabel);
	layout.putConstraint(SpringLayout.WEST, gammaTextField, 5,
		SpringLayout.EAST, gammaLabel);
	layout.putConstraint(SpringLayout.EAST, controlPanel, 0,
		SpringLayout.EAST, gammaTextField);
	layout.putConstraint(SpringLayout.SOUTH, controlPanel, 0,
		SpringLayout.SOUTH, gammaTextField);

	applyToVisible.addActionListener(this);
	applyToAll.addActionListener(this);
	buttonPanel.add(applyToVisible);
	buttonPanel.add(applyToAll);
	SpringLayout mainLayout = new SpringLayout();

	controlPanel.revalidate();
	mainControlPanel.setLayout(mainLayout);
	mainControlPanel.add(instructions);
	mainControlPanel.add(controlPanel);
	mainControlPanel.add(buttonPanel);
	mainLayout.putConstraint(SpringLayout.NORTH, instructions, 10,
		SpringLayout.NORTH, mainControlPanel);
	mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructions,
		0, SpringLayout.HORIZONTAL_CENTER, mainControlPanel);
	mainLayout.putConstraint(SpringLayout.NORTH, controlPanel, 10,
		SpringLayout.SOUTH, instructions);
	mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, controlPanel,
		0, SpringLayout.HORIZONTAL_CENTER, mainControlPanel);
	mainLayout.putConstraint(SpringLayout.NORTH, buttonPanel, 10,
		SpringLayout.SOUTH, controlPanel);
	mainLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, buttonPanel,
		0, SpringLayout.HORIZONTAL_CENTER, mainControlPanel);
	mainControlPanel.revalidate();
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
     * This is called when a click event is fired on one of the custom operator.
     */
    public void actionPerformed(ActionEvent e) {
	if (checkOperator()) {
	    applyOperation(getApplicableQubits(e.getSource().equals(
		    applyToVisible)));
	}
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
     * are well formed complex numbers and that the operator is both unitary and
     * hermitian.
     * 
     * @return true if the operator is non-null, well-formed, and that the
     *         operator values make the operator hermitian (which is unitary),
     *         otherwise false.
     */
    private boolean checkOperator() {

	Operator operator = getOperatorFromUI();
	if (operator == null) {
	    return false;
	} else if (!operator.isOperatorHermitian()) {
	    JOptionPane
		    .showMessageDialog(
			    this.mainControlPanel,
			    "The operator must be hermitian (Unitary and equal to it's adjoint.",
			    "Operator Value Error", JOptionPane.ERROR_MESSAGE);
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
    private Operator getOperatorFromUI() {
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
