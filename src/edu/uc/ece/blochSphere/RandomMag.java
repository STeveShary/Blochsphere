package edu.uc.ece.blochSphere;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.j3d.BranchGroup;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RandomMag implements Runnable, ActionListener, ChangeListener {

    protected JPanel RandomMagneticFieldOptions = null;
    protected JLabel instructionLabel = null;
    protected JSlider WRatioSlider = null;
    protected JLabel WRatioValueLabel = null;
    protected JLabel WRatioLabel = null;
    protected JSlider NSlider = null;
    protected JLabel NLabel = null;
    protected JLabel NValueLabel = null;
    protected JSlider SampleNumberSlider = null;
    protected JLabel SampleNumberLabel = null;
    protected JLabel SampleNumberValueLabel = null;
    protected JButton startButton = null;
    protected JButton resetButton = null;
    protected JButton trackButton = null;
    private Qubit sumQbit;
    private Vector<TrackDot> tdv = new Vector<TrackDot>();
    private boolean animationInProgress = false;
    private boolean trackOn = false;
    private static int T = 1;
    private static float dt = (float) T / 1000;
    private double W1OverW0;
    private double W1OverW0Max;
    private double W1OverW0Range[];
    private double N;
    private double SampleNumber;
    private int IndexNumber1;
    private int IndexNumber2 = 0;
    private double qmag = 1;
    private double qx = 0;
    private double qy = 0;
    private double qz = 0;
    private double qphi;
    private double qtheta;
    private double qxratio;
    private double sumX;
    private double sumY;
    private double sumZ;
    private double blochVectorX[];
    private double blochVectorY[];
    private double blochVectorZ[];
    private double vectorX;
    private double vectorY;
    private double vectorZ;
    private double t = 0;
    private Random rand = new Random();
    /*
     * The parent BranchGroup to contain the children objects.
     */
    BranchGroup parentBranchGroup = null;

    public RandomMag() {
	buildControlPanel();
    }

    public RandomMag(Qubit qbit, BranchGroup parentBG) {
	this();
	this.parentBranchGroup = parentBG;
	setSumQubit(qbit);
    }

    public void setSumQubit(Qubit qbit) {
	sumQbit = qbit;
    }

    protected void buildControlPanel() {
	RandomMagneticFieldOptions = new JPanel();
	Border tborder = new TitledBorder(BorderFactory.createEtchedBorder(),
		"Random Magnetic Field", TitledBorder.LEFT, TitledBorder.TOP);
	RandomMagneticFieldOptions.setBorder(tborder);
	SpringLayout layout = new SpringLayout();
	RandomMagneticFieldOptions.setLayout(layout);
	/*
	 * construct the instruction label
	 */
	instructionLabel = new JLabel("<html>TBD</html>");
	RandomMagneticFieldOptions.add(instructionLabel);
	layout.putConstraint(SpringLayout.NORTH, instructionLabel, 0,
		SpringLayout.NORTH, RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, instructionLabel,
		10, SpringLayout.HORIZONTAL_CENTER, RandomMagneticFieldOptions);
	/*
	 * construct the W1/W0_max slider
	 */
	WRatioSlider = new JSlider(JSlider.HORIZONTAL, 5, 200, 100);
	WRatioSlider.setMajorTickSpacing(10);
	Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
	labelTable.put(new Integer(5), new JLabel("<html>0.5</html>"));
	labelTable.put(new Integer(100), new JLabel(
		"<html><center>10</center></html>"));
	labelTable.put(new Integer(200), new JLabel(
		"<html><center>20</center></html>"));
	WRatioSlider.setLabelTable(labelTable);
	WRatioSlider.setPaintLabels(true);
	WRatioSlider.setPaintTicks(true);
	RandomMagneticFieldOptions.add(WRatioSlider);
	WRatioSlider.addChangeListener(this);
	layout.putConstraint(SpringLayout.NORTH, WRatioSlider, 10,
		SpringLayout.SOUTH, instructionLabel);
	layout.putConstraint(SpringLayout.WEST, WRatioSlider, 70,
		SpringLayout.WEST, RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, WRatioSlider, 10,
		SpringLayout.HORIZONTAL_CENTER, RandomMagneticFieldOptions);
	WRatioLabel = new JLabel(
		"<html>W<sub>1</sub>/W<sub>0</sub>_max</sub></html>");
	RandomMagneticFieldOptions.add(WRatioLabel);
	layout.putConstraint(SpringLayout.NORTH, WRatioLabel, 0,
		SpringLayout.NORTH, WRatioSlider);
	layout.putConstraint(SpringLayout.EAST, WRatioLabel, 0,
		SpringLayout.WEST, WRatioSlider);
	WRatioValueLabel = new JLabel("10");
	WRatioValueLabel.setBorder(BorderFactory.createEtchedBorder());
	RandomMagneticFieldOptions.add(WRatioValueLabel);
	WRatioValueLabel.setText(new Double(
		((double) WRatioSlider.getValue()) / 10).toString());
	layout.putConstraint(SpringLayout.NORTH, WRatioValueLabel, 0,
		SpringLayout.NORTH, WRatioSlider);
	layout.putConstraint(SpringLayout.WEST, WRatioValueLabel, 10,
		SpringLayout.EAST, WRatioSlider);
	/*
	 * construct the N=T/Delta_t slider
	 */
	NSlider = new JSlider(JSlider.HORIZONTAL, 10, 1000, 500);
	NSlider.setMajorTickSpacing(100);
	Hashtable<Integer, JLabel> NlabelTable = new Hashtable<Integer, JLabel>();
	NlabelTable.put(new Integer(10), new JLabel("<html>10</html>"));
	NlabelTable.put(new Integer(500), new JLabel(
		"<html><center>500</center></html>"));
	NlabelTable.put(new Integer(1000), new JLabel(
		"<html><center>1000</center></html>"));
	NSlider.setLabelTable(NlabelTable);
	NSlider.setPaintLabels(true);
	NSlider.setPaintTicks(true);
	RandomMagneticFieldOptions.add(NSlider);
	NSlider.addChangeListener(this);
	// put on the north side in the center
	layout.putConstraint(SpringLayout.NORTH, NSlider, 10,
		SpringLayout.SOUTH, WRatioSlider);
	layout.putConstraint(SpringLayout.WEST, NSlider, 70, SpringLayout.WEST,
		RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, NSlider, 10,
		SpringLayout.HORIZONTAL_CENTER, RandomMagneticFieldOptions);
	NLabel = new JLabel("<html>N(T/delta_t)</sub></html>");
	RandomMagneticFieldOptions.add(NLabel);
	layout.putConstraint(SpringLayout.NORTH, NLabel, 0, SpringLayout.NORTH,
		NSlider);
	layout.putConstraint(SpringLayout.EAST, NLabel, 0, SpringLayout.WEST,
		NSlider);
	NValueLabel = new JLabel("500");
	NValueLabel.setBorder(BorderFactory.createEtchedBorder());
	RandomMagneticFieldOptions.add(NValueLabel);
	NValueLabel.setText(new Double((double) NSlider.getValue()).toString());
	layout.putConstraint(SpringLayout.NORTH, NValueLabel, 0,
		SpringLayout.NORTH, NSlider);
	layout.putConstraint(SpringLayout.WEST, NValueLabel, 10,
		SpringLayout.EAST, NSlider);
	/*
	 * construct the Sample number slider
	 */
	SampleNumberSlider = new JSlider(JSlider.HORIZONTAL, 1, 1000, 1000);
	SampleNumberSlider.setMajorTickSpacing(100);
	Hashtable<Integer, JLabel> SampleNumberlabelTable = new Hashtable<Integer, JLabel>();
	SampleNumberlabelTable.put(new Integer(1),
		new JLabel("<html>10</html>"));
	SampleNumberlabelTable.put(new Integer(500), new JLabel(
		"<html><center>500</center></html>"));
	SampleNumberlabelTable.put(new Integer(1000), new JLabel(
		"<html><center>1000</center></html>"));
	SampleNumberSlider.setLabelTable(SampleNumberlabelTable);
	SampleNumberSlider.setPaintLabels(true);
	SampleNumberSlider.setPaintTicks(true);
	RandomMagneticFieldOptions.add(SampleNumberSlider);
	// SampleNumberSlider.setSize(300, SampleNumberSlider.getSize().height);
	SampleNumberSlider.addChangeListener(this);
	// put on the north side in the center
	layout.putConstraint(SpringLayout.NORTH, SampleNumberSlider, 10,
		SpringLayout.SOUTH, NSlider);
	layout.putConstraint(SpringLayout.WEST, SampleNumberSlider, 70,
		SpringLayout.WEST, RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
		SampleNumberSlider, 10, SpringLayout.HORIZONTAL_CENTER,
		RandomMagneticFieldOptions);
	SampleNumberLabel = new JLabel("<html>Sample No.</sub></html>");
	RandomMagneticFieldOptions.add(SampleNumberLabel);
	layout.putConstraint(SpringLayout.NORTH, SampleNumberLabel, 0,
		SpringLayout.NORTH, SampleNumberSlider);
	layout.putConstraint(SpringLayout.EAST, SampleNumberLabel, 0,
		SpringLayout.WEST, SampleNumberSlider);
	SampleNumberValueLabel = new JLabel("1000");
	SampleNumberValueLabel.setBorder(BorderFactory.createEtchedBorder());
	RandomMagneticFieldOptions.add(SampleNumberValueLabel);
	SampleNumberValueLabel.setText(new Double((double) SampleNumberSlider
		.getValue()).toString());
	layout.putConstraint(SpringLayout.NORTH, SampleNumberValueLabel, 0,
		SpringLayout.NORTH, SampleNumberSlider);
	layout.putConstraint(SpringLayout.WEST, SampleNumberValueLabel, 10,
		SpringLayout.EAST, SampleNumberSlider);
	/*
	 * construct the four buttons
	 */
	resetButton = new JButton("Reset");
	RandomMagneticFieldOptions.add(resetButton);
	resetButton.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, resetButton, -10,
		SpringLayout.SOUTH, RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.EAST, resetButton, -10,
		SpringLayout.EAST, RandomMagneticFieldOptions);
	trackButton = new JButton("Track on");
	RandomMagneticFieldOptions.add(trackButton);
	trackButton.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, trackButton, -10,
		SpringLayout.SOUTH, RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.EAST, trackButton, -10,
		SpringLayout.WEST, resetButton);
	startButton = new JButton("Start");
	RandomMagneticFieldOptions.add(startButton);
	startButton.addActionListener(this);
	layout.putConstraint(SpringLayout.SOUTH, startButton, -10,
		SpringLayout.SOUTH, RandomMagneticFieldOptions);
	layout.putConstraint(SpringLayout.EAST, startButton, -10,
		SpringLayout.WEST, trackButton);
	setAnimation(false);
    }

    protected synchronized boolean getAnimation() {
	return animationInProgress;
    }

    /**
     * Sets if the animation should be running.
     * 
     * @param animation
     */
    protected synchronized void setAnimation(boolean animation) {
	animationInProgress = animation;
    }

    protected synchronized boolean getTrackOn() {
	return trackOn;
    }

    /**
     * Sets whether the track could be seen.
     * 
     * @param TrackOn
     */
    protected synchronized void setTrackOn(boolean TrackOn) {
	trackOn = TrackOn;
    }

    public JPanel getControlPanel() {
	return RandomMagneticFieldOptions;
    }

    public void run() {
	while (getAnimation()) {
	    W1OverW0Max = Double.parseDouble(WRatioValueLabel.getText());
	    N = Double.parseDouble(NValueLabel.getText());
	    SampleNumber = Double.parseDouble(SampleNumberValueLabel.getText());
	    double DeltaT = T / N;
	    if (t == 0) {
		qphi = sumQbit.getPhi() / 180 * Math.PI;
		qtheta = sumQbit.getTh() / 180 * Math.PI;
		qx = qmag * Math.cos(qphi) * Math.sin(qtheta);
		qy = qmag * Math.sin(qphi) * Math.sin(qtheta);
		qz = qmag * Math.cos(qtheta);
		blochVectorX = new double[(int) SampleNumber];
		blochVectorY = new double[(int) SampleNumber];
		blochVectorZ = new double[(int) SampleNumber];
		W1OverW0Range = new double[(int) SampleNumber];
		for (int i = 0; i < SampleNumber; i++) {
		    W1OverW0Range[i] = 2 * (rand.nextDouble() - 0.5)
			    * W1OverW0Max;
		    blochVectorX[i] = qx;
		    blochVectorY[i] = qy;
		    blochVectorZ[i] = qz;
		}
	    }
	    for (int i = 0; i < SampleNumber; i++) {
		W1OverW0 = W1OverW0Range[i];
		double W0 = 2 * Math.PI / T;
		double W1 = W1OverW0 * W0;
		double Chi = Math.asin(-W1 / Math.sqrt(W0 * W0 + W1 * W1));
		if (W1OverW0 > 0)
		    Chi = -Math.PI - Chi;
		else
		    Chi = Math.PI - Chi;
		double Delta = Math.sqrt(W1OverW0 * W1OverW0 + 1) * 2 * Math.PI
			* dt / T;
		double f = 1 - Math.cos(Delta);
		double h = Math.cos(Delta) * Math.cos(Chi) * Math.cos(Chi)
			+ Math.sin(Chi) * Math.sin(Chi);
		double g = Math.sin(Delta) * Math.cos(Chi);
		double[][] rotate = new double[3][3];
		rotate[0][0] = h;
		rotate[0][1] = g;
		rotate[0][2] = f * Math.cos(Chi) * Math.sin(Chi);
		rotate[1][0] = -g;
		rotate[1][1] = Math.cos(Delta);
		rotate[1][2] = Math.sin(Delta) * Math.sin(Chi);
		rotate[2][0] = f * Math.cos(Chi) * Math.sin(Chi);
		rotate[2][1] = -Math.sin(Delta) * Math.sin(Chi);
		rotate[2][2] = Math.cos(Chi) * Math.cos(Chi) + Math.sin(Chi)
			* Math.sin(Chi) * Math.cos(Delta);
		vectorX = rotate[0][0] * blochVectorX[i] + rotate[0][1]
			* blochVectorY[i] + rotate[0][2] * blochVectorZ[i];
		vectorY = rotate[1][0] * blochVectorX[i] + rotate[1][1]
			* blochVectorY[i] + rotate[1][2] * blochVectorZ[i];
		vectorZ = rotate[2][0] * blochVectorX[i] + rotate[2][1]
			* blochVectorY[i] + rotate[2][2] * blochVectorZ[i];
		blochVectorX[i] = vectorX;
		blochVectorY[i] = vectorY;
		blochVectorZ[i] = vectorZ;
	    }
	    sumX = 0;
	    sumY = 0;
	    sumZ = 0;
	    for (int j = 0; j < SampleNumber; j++) {
		sumX += blochVectorX[j];
		sumY += blochVectorY[j];
		sumZ += blochVectorZ[j];
	    }
	    qx = sumX / SampleNumber;
	    qy = sumY / SampleNumber;
	    qz = sumZ / SampleNumber;
	    qmag = Math.sqrt(qx * qx + qy * qy + qz * qz);
	    qx = qx / qmag;
	    qy = qy / qmag;
	    qz = qz / qmag;
	    qtheta = Math.acos(qz);
	    if (qtheta == 0 || qtheta == Math.PI) {
		qphi = 0;
	    } else {
		qxratio = qx / Math.sin(qtheta);
		if (qxratio > 1)
		    qxratio = 1;
		if (qxratio < -1)
		    qxratio = -1;
		if (qy / Math.sin(qtheta) >= 0) {
		    qphi = Math.acos(qxratio);
		} else {
		    qphi = 2 * Math.PI - Math.acos(qxratio);
		}
	    }
	    sumQbit.setThetaPhi(qtheta / Math.PI * 180, qphi / Math.PI * 180);
	    sumQbit.changeMag((float) (qmag * IBlochConstants.QUBIT_MAG));
	    if (getTrackOn()) {
		tdv.add(new TrackDot(
			(float) (qmag * IBlochConstants.QUBIT_MAG),
			(float) (qtheta / Math.PI * 180), (float) (qphi
				/ Math.PI * 180), parentBranchGroup));
	    }
	    // tdv.lastElement().setVisible(false);
	    IndexNumber1 = (int) Math.floor(t / DeltaT);
	    if (!(IndexNumber1 == IndexNumber2)) {
		// W1OverW0Range = new double [(int)SampleNumber];
		for (int i = 0; i < SampleNumber; i++) {
		    W1OverW0Range[i] = 2 * (rand.nextDouble() - 0.5)
			    * W1OverW0Max;
		}
	    }
	    IndexNumber2 = IndexNumber1;
	    t += dt;
	}
    }

    public void actionPerformed(ActionEvent e) {
	Object component = e.getSource();
	// If we hit start, then only start when it isn't already running.
	if (component.equals(startButton)) {
	    if (!getAnimation()) {
		setAnimation(true);
		startButton.setText("Stop");
		new Thread(this).start();
	    } else {
		setAnimation(false);
		startButton.setText("Start");
	    }
	}
	// If we hit the reset button, then stop the animation, and set the
	// values to default values.
	if (component.equals(resetButton)) {
	    setAnimation(false);
	    sumQbit.setThetaPhi(45, 45);
	    sumQbit.changeMag(IBlochConstants.QUBIT_MAG);
	    WRatioSlider.setValue(100);
	    NSlider.setValue(500);
	    SampleNumberSlider.setValue(1000);
	    Iterator<TrackDot> it = tdv.iterator();
	    while (it.hasNext()) {
		it.next().setVisible(false);
	    }
	    tdv.clear();
	    t = 0;
	    qmag = 1;
	    IndexNumber2 = 0;
	}
	// If we hit track on button, a track of qbits would been seen instead
	// of a single qbit.
	if (component.equals(trackButton)) {
	    if (!getTrackOn()) {
		setTrackOn(true);
		trackButton.setText("Track Off");
	    } else {
		setTrackOn(false);
		trackButton.setText("Track On");
	    }
	}
	// If we hit clear button, the track of qbits would disappear.
	// if(component.equals(clearButton)){
	// }
    }

    public void stateChanged(ChangeEvent e) {
	Object component = e.getSource();
	// If the slider changed, then set it's value.
	if (component.equals(WRatioSlider))
	    WRatioValueLabel.setText(new Double(((double) WRatioSlider
		    .getValue()) / 10).toString());
	if (component.equals(NSlider))
	    NValueLabel.setText(new Double((double) NSlider.getValue())
		    .toString());
	if (component.equals(SampleNumberSlider))
	    SampleNumberValueLabel.setText(new Double(
		    ((double) SampleNumberSlider.getValue())).toString());
    }

    public String getTabName() {
	return "Random Mag Field";
    }
}
