package edu.uc.ece.blochSphere.operator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import edu.uc.ece.blochSphere.QubitOperator;

public class OperatorRecording implements ActionListener {

    private QubitOperator qubitOperator = null;
    protected JList operators = new JList();
    protected JScrollPane scrollPane = new JScrollPane(operators,
	    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JButton quitButton = new JButton("Quit");
    private JButton saveButton = new JButton("Save");
    private JLabel instructionsLabel = new JLabel(
	    "<html><center><b>Instructions:</b><br><br>"
		    + "Enter operators in the screen below.  As they are applied<br> "
		    + "to the qubits, they will be recorded.  You may save your set<br>"
		    + " of operators or quit at any time.</center></html>");
    private JPanel controlPanel = new JPanel();

    public OperatorRecording(QubitOperator qubitOperator) {
	this.qubitOperator = qubitOperator;
	qubitOperator.getApplyAllButton().addActionListener(this);
	qubitOperator.getApplyVisibleButton().addActionListener(this);
	buildPanel();
    }

    public JPanel getControlPanel() {
	return controlPanel;
    }

    public JButton getStopRecordingButton() {
	return quitButton;
    }

    private void buildPanel() {
	operators.setCellRenderer(new OperatorCellRenderer());
	operators.setVisibleRowCount(5);
	operators.setLayoutOrientation(JList.VERTICAL);
	operators.setFocusable(false);
	operators.setModel(new DefaultListModel());
	saveButton.addActionListener(this);

	controlPanel.add(instructionsLabel);
	controlPanel.add(scrollPane);
	controlPanel.add(quitButton);
	controlPanel.add(saveButton);

	SpringLayout layout = new SpringLayout();
	controlPanel.setLayout(layout);

	layout.putConstraint(SpringLayout.NORTH, instructionsLabel, 10,
		SpringLayout.NORTH, controlPanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructionsLabel,
		10, SpringLayout.HORIZONTAL_CENTER, controlPanel);

	layout.putConstraint(SpringLayout.NORTH, scrollPane, 10,
		SpringLayout.SOUTH, instructionsLabel);
	layout.putConstraint(SpringLayout.WEST, scrollPane, 10,
		SpringLayout.WEST, controlPanel);
	layout.putConstraint(SpringLayout.SOUTH, scrollPane, -10,
		SpringLayout.SOUTH, controlPanel);

	layout.putConstraint(SpringLayout.EAST, quitButton, -10,
		SpringLayout.EAST, controlPanel);
	layout.putConstraint(SpringLayout.SOUTH, quitButton, -10,
		SpringLayout.SOUTH, controlPanel);
	layout.putConstraint(SpringLayout.WEST, quitButton, 10,
		SpringLayout.EAST, scrollPane);

	layout.putConstraint(SpringLayout.EAST, saveButton, -10,
		SpringLayout.EAST, controlPanel);
	layout.putConstraint(SpringLayout.SOUTH, saveButton, -10,
		SpringLayout.NORTH, quitButton);
	layout.putConstraint(SpringLayout.WEST, saveButton, 10,
		SpringLayout.EAST, scrollPane);

    }

    public void resetRecording() {
	((DefaultListModel) operators.getModel()).removeAllElements();
    }

    public void actionPerformed(ActionEvent event) {
	if (event.getSource().equals(qubitOperator.getApplyAllButton())) {
	    ((DefaultListModel) operators.getModel()).addElement(qubitOperator
		    .getOperatorFromUI());
	    operators.ensureIndexIsVisible(operators.getModel().getSize() - 1);
	} else if (event.getSource().equals(
		qubitOperator.getApplyVisibleButton())) {
	    ((DefaultListModel) operators.getModel()).addElement(qubitOperator
		    .getOperatorFromUI());
	    operators.ensureIndexIsVisible(operators.getModel().getSize() - 1);
	} else if (event.getSource().equals(saveButton)) {
	    Vector<Operator> operaterDataSet = new Vector<Operator>();
	    DefaultListModel listModel = (DefaultListModel) operators
		    .getModel();
	    for (int i = 0; i < listModel.size(); i++) {
		operaterDataSet.add((Operator) listModel.get(i));
	    }
	    new OperatorSaveLoad(null, null).saveOperators(controlPanel, null,
		    operaterDataSet);

	}
    }

}
