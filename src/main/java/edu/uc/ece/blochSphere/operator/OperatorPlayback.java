package edu.uc.ece.blochSphere.operator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import edu.uc.ece.blochSphere.ComplexNumber;
import edu.uc.ece.blochSphere.Qubit;

/**
 * Controls the playback and recording of operators in a series of events.
 * 
 * @author Stephen Shary
 * 
 */
public class OperatorPlayback implements ActionListener {

    protected JPanel controlPanel = new JPanel();

    protected JPanel choicePanel = new JPanel();
    protected JLabel instructionsLabel = new JLabel(
	    "<html><center><b>Instruction:</b><br>Choose playback to play a previously recorded<br> set of operators, or press the record button<br> to record a series of operators.</center></html>");
    protected JButton recordButton = new JButton("Record");
    protected JButton playBackButton = new JButton("Play Back");

    protected JPanel playBackPanel = new JPanel();
    protected JLabel playBackInstructions = new JLabel(
	    "<html><center><b>Instruction:</b><br><br>"
		    + "First, set the desired delay period between operators.<br>"
		    + "Second, start the automatic play by hitting the \"play\" button<br>"
		    + "You may pause to stop the autmatic play.  Additionally you can use<br>"
		    + "the \"step\" button to apply the next operator.</center></html>");
    protected JLabel delayLabel = new JLabel("Delay:");
    protected JLabel secondsLabel = new JLabel("Seconds");
    protected JFormattedTextField secondsTextField = new JFormattedTextField(
	    NumberFormat.getNumberInstance());
    protected JList operators = new JList();
    protected JScrollPane scrollPane = new JScrollPane(operators,
	    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    protected JButton playButton = new JButton("Play");
    protected JButton pauseButton = new JButton("Pause");
    protected JButton stepButton = new JButton("Step");
    protected JButton restartButton = new JButton("Restart");
    protected JButton stopPlaybackButton = new JButton("End Playback");

    private OperatorSaveLoad saveLoad = null;

    private boolean isPlaying = false;

    private Vector<Qubit> qubitVector = null;

    public OperatorPlayback(Vector<Qubit> qubitVector) {
	buildPanel();
	this.qubitVector = qubitVector;
	saveLoad = new OperatorSaveLoad(qubitVector, this);
	controlPanel.revalidate();
	setPlayback(false);
    }

    public JPanel getControlPanel() {
	return controlPanel;
    }

    public JButton getRecordButton() {
	return recordButton;
    }

    public JButton getPlayBackButton() {
	return playBackButton;
    }

    public JButton getStopPlayBackButton() {
	return stopPlaybackButton;
    }

    private void buildPanel() {
	controlPanel.setLayout(new BorderLayout());

	SpringLayout layout = new SpringLayout();
	choicePanel.setLayout(layout);
	choicePanel.add(instructionsLabel);
	choicePanel.add(recordButton);
	choicePanel.add(playBackButton);
	instructionsLabel.setHorizontalTextPosition(SwingConstants.CENTER);

	layout.putConstraint(SpringLayout.NORTH, instructionsLabel, 25,
		SpringLayout.NORTH, choicePanel);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructionsLabel,
		5, SpringLayout.HORIZONTAL_CENTER, choicePanel);

	layout.putConstraint(SpringLayout.NORTH, recordButton, 20,
		SpringLayout.SOUTH, instructionsLabel);
	layout.putConstraint(SpringLayout.EAST, recordButton, -15,
		SpringLayout.HORIZONTAL_CENTER, choicePanel);

	layout.putConstraint(SpringLayout.NORTH, playBackButton, 20,
		SpringLayout.SOUTH, instructionsLabel);
	layout.putConstraint(SpringLayout.WEST, playBackButton, 15,
		SpringLayout.HORIZONTAL_CENTER, choicePanel);

	SpringLayout playBackLayout = new SpringLayout();
	playBackPanel.setLayout(playBackLayout);
	playBackPanel.add(playBackInstructions);
	playBackPanel.add(delayLabel);
	playBackPanel.add(secondsTextField);
	playBackPanel.add(secondsLabel);
	playBackPanel.add(scrollPane);
	playBackPanel.add(playButton);
	playBackPanel.add(pauseButton);
	playBackPanel.add(stepButton);
	playBackPanel.add(restartButton);
	playBackPanel.add(stopPlaybackButton);
	stopPlaybackButton.addActionListener(this);
	playBackButton.addActionListener(this);
	playButton.addActionListener(this);
	pauseButton.addActionListener(this);
	stepButton.addActionListener(this);
	restartButton.addActionListener(this);

	operators.setCellRenderer(new OperatorCellRenderer());
	operators.setVisibleRowCount(5);
	operators.setLayoutOrientation(JList.VERTICAL);
	for (MouseListener listener : operators.getMouseListeners()) {
	    operators.removeMouseListener(listener);
	}
	for (MouseMotionListener listener : operators.getMouseMotionListeners()) {
	    operators.removeMouseMotionListener(listener);
	}

	secondsTextField.setColumns(3);
	secondsTextField.setText("0.5");

	playBackLayout.putConstraint(SpringLayout.NORTH, playBackInstructions,
		0, SpringLayout.NORTH, playBackPanel);
	playBackLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
		playBackInstructions, 0, SpringLayout.HORIZONTAL_CENTER,
		playBackPanel);

	playBackLayout.putConstraint(SpringLayout.NORTH, delayLabel, 20,
		SpringLayout.NORTH, scrollPane);
	playBackLayout.putConstraint(SpringLayout.WEST, delayLabel, 25,
		SpringLayout.EAST, scrollPane);

	playBackLayout.putConstraint(SpringLayout.NORTH, secondsTextField, -3,
		SpringLayout.NORTH, delayLabel);
	playBackLayout.putConstraint(SpringLayout.WEST, secondsTextField, 5,
		SpringLayout.EAST, delayLabel);

	playBackLayout.putConstraint(SpringLayout.NORTH, secondsLabel, 2,
		SpringLayout.NORTH, secondsTextField);
	playBackLayout.putConstraint(SpringLayout.WEST, secondsLabel, 5,
		SpringLayout.EAST, secondsTextField);

	playBackLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10,
		SpringLayout.SOUTH, playBackInstructions);
	playBackLayout.putConstraint(SpringLayout.EAST, scrollPane, 5,
		SpringLayout.HORIZONTAL_CENTER, playBackPanel);
	playBackLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10,
		SpringLayout.SOUTH, playBackPanel);

	playBackLayout.putConstraint(SpringLayout.WEST, stopPlaybackButton, 5,
		SpringLayout.EAST, scrollPane);
	playBackLayout.putConstraint(SpringLayout.SOUTH, stopPlaybackButton,
		-10, SpringLayout.SOUTH, playBackPanel);

	playBackLayout.putConstraint(SpringLayout.WEST, restartButton, 5,
		SpringLayout.EAST, scrollPane);
	playBackLayout.putConstraint(SpringLayout.SOUTH, restartButton, -5,
		SpringLayout.NORTH, stopPlaybackButton);
	playBackLayout.putConstraint(SpringLayout.EAST, restartButton, 0,
		SpringLayout.EAST, stopPlaybackButton);

	playBackLayout.putConstraint(SpringLayout.WEST, stepButton, 5,
		SpringLayout.EAST, scrollPane);
	playBackLayout.putConstraint(SpringLayout.SOUTH, stepButton, -5,
		SpringLayout.NORTH, restartButton);
	playBackLayout.putConstraint(SpringLayout.EAST, stepButton, 0,
		SpringLayout.EAST, stopPlaybackButton);

	playBackLayout.putConstraint(SpringLayout.WEST, pauseButton, 5,
		SpringLayout.EAST, scrollPane);
	playBackLayout.putConstraint(SpringLayout.SOUTH, pauseButton, -5,
		SpringLayout.NORTH, stepButton);
	playBackLayout.putConstraint(SpringLayout.EAST, pauseButton, 0,
		SpringLayout.EAST, stopPlaybackButton);

	playBackLayout.putConstraint(SpringLayout.WEST, playButton, 5,
		SpringLayout.EAST, scrollPane);
	playBackLayout.putConstraint(SpringLayout.SOUTH, playButton, -5,
		SpringLayout.NORTH, pauseButton);
	playBackLayout.putConstraint(SpringLayout.EAST, playButton, 0,
		SpringLayout.EAST, stopPlaybackButton);

    }

    public void loadData(Vector<Operator> operators) {
	this.operators.setListData(operators);
    }

    public void setPlayback(boolean isPlayback) {
	playBackButton.setEnabled(!isPlayback);
	recordButton.setEnabled(!isPlayback);
	operators.setEnabled(isPlayback);
	restartButton.setEnabled(isPlayback);
	stepButton.setEnabled(isPlayback);
	pauseButton.setEnabled(isPlayback);
	playButton.setEnabled(isPlayback);
	stopPlaybackButton.setEnabled(isPlayback);
	setChoicePanel(!isPlayback);

    }

    public void setChoicePanel(boolean showChoicePanel) {
	if (showChoicePanel) {
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    controlPanel.removeAll();
		    controlPanel.add(choicePanel, BorderLayout.CENTER);
		    controlPanel.revalidate();
		}
	    });
	} else {
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    controlPanel.removeAll();
		    controlPanel.add(playBackPanel, BorderLayout.CENTER);
		    controlPanel.revalidate();
		}
	    });
	}
    }

    public void actionPerformed(ActionEvent event) {
	if (event.getSource().equals(playButton)) {
	    play();
	} else if (event.getSource().equals(pauseButton)) {
	    pausePlayback();
	} else if (event.getSource().equals(stepButton)) {
	    stepOperator();
	} else if (event.getSource().equals(restartButton)) {
	    restartPlayback();
	} else if (event.getSource().equals(playBackButton)) {
	    saveLoad.loadBloch(controlPanel);
	    setPlayback(true);
	} else if (event.getSource().equals(stopPlaybackButton)) {
	    setPlayback(false);
	    operators.setListData(new Vector());
	}
    }

    private void restartPlayback() {
	if (operators.getModel().getSize() > 0) {
	    operators.clearSelection();
	    operators.ensureIndexIsVisible(0);
	}
	setPlaying(false);
	pauseButton.setEnabled(false);
	playButton.setEnabled(true);
	stepButton.setEnabled(true);
    }

    private void play() {
	new Thread(new Runnable() {
	    public void run() {
		int waitingTime = getWaitInterval() - 100;
		playButton.setEnabled(false);
		pauseButton.setEnabled(true);
		stepButton.setEnabled(false);
		setPlaying(true);
		while (isPlaying()) {
		    try {
			Thread.sleep(100);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		    waitingTime = waitingTime + 100;
		    if (waitingTime > getWaitInterval()) {
			if (!stepOperator()) {
			    setPlaying(false);
			}
			waitingTime = 0;
		    }

		}
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stepButton.setEnabled(true);
	    }
	}).start();
    }

    private boolean stepOperator() {
	if (operators.getModel().getSize() <= 0) {
	    return false;
	} else if (operators.getSelectedIndex() < operators.getModel()
		.getSize() - 1) {
	    operators.setSelectedIndex(operators.getSelectedIndex() + 1);
	    Operator operatorData = (Operator) operators.getModel()
		    .getElementAt(operators.getSelectedIndex());
	    applyOperator(operatorData);
	    final int newIndex = operators.getSelectedIndex();
	    try {
		SwingUtilities.invokeAndWait(new Runnable() {
		    public void run() {
			operators.ensureIndexIsVisible(newIndex);
		    }
		});
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (InvocationTargetException e) {
		e.printStackTrace();
	    }

	    return true;
	} else {
	    return operators.getSelectedIndex() != operators.getModel()
		    .getSize() - 1;
	}
    }

    private void pausePlayback() {
	setPlaying(false);
	pauseButton.setEnabled(false);
	playButton.setEnabled(true);
	stepButton.setEnabled(true);
    }

    private void applyOperator(Operator operator) {
	ComplexNumber alphaNum = operator.getTopLeftElement();
	ComplexNumber betaNum = operator.getTopRightElement();
	ComplexNumber gammaNum = operator.getBottomLeftElement();
	ComplexNumber deltaNum = operator.getBottomRightElement();

	for (Qubit qubit : qubitVector) {
	    ComplexNumber qubitAlphaNum = qubit.getAlphaValue();
	    ComplexNumber qubitBetaNum = qubit.getBetaValue();

	    ComplexNumber productAlpha = alphaNum.multiply(qubitAlphaNum).add(
		    betaNum.multiply(qubitBetaNum));
	    ComplexNumber productBeta = gammaNum.multiply(qubitAlphaNum).add(
		    deltaNum.multiply(qubitBetaNum));

	    qubit.setAlphaBetaFullValues(productAlpha.getRealPart(),
		    productAlpha.getImaginaryPart(), productBeta.getRealPart(),
		    productBeta.getImaginaryPart());
	}
    }

    private synchronized boolean isPlaying() {
	return isPlaying;
    }

    private synchronized void setPlaying(boolean playing) {
	secondsTextField.setEnabled(!playing);
	this.isPlaying = playing;
    }

    public int getWaitInterval() {
	return new Float(Float.parseFloat(secondsTextField.getText()) * 1000)
		.intValue();
    }

    public String getTabName() {
	return "Record/Playback";
    }
}
