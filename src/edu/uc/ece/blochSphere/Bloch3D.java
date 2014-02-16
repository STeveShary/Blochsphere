package edu.uc.ece.blochSphere;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Link;
import javax.media.j3d.Material;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import edu.uc.ece.blochSphere.operator.Operator;
import edu.uc.ece.blochSphere.operator.OperatorPlayback;
import edu.uc.ece.blochSphere.operator.OperatorRecording;

/**
 * The main class that will instantiate all of the UI and the qubits.
 * 
 * @author Nick Vatamaniuc, Stephen Shary
 * 
 */
public class Bloch3D implements IBlochConstants, ActionListener {

    protected BoundingSphere bounds = new BoundingSphere(
	    new Point3d(0d, 0d, 0d), 100d);
    protected BranchGroup qBitsGroup = new BranchGroup();
    protected TransformGroup viewingTransformGroup = new TransformGroup();
    protected JPanel qBitControlPanel = new JPanel();
    protected JPanel qBitPanelHolder = new JPanel();
    protected JTabbedPane qubitPane = new JTabbedPane();
    protected JPanel modeControlPanel = new JPanel();
    protected JPanel displayToggleControlPanel;
    protected JButton displayAllButton;
    protected JButton displayNoneButton;
    protected JButton showSumQubitButton;
    protected JTabbedPane transformTabbedPane = null;
    protected Container contentPane = null;
    protected LarmorPrecession m_precession = null;
    protected QubitOperator qubitOperator = null;
    protected OperatorPlayback operatorPlayback = null;
    protected RabiField rabiField = null;
    protected RandomMag randomMagneticFieldControl = null;
    protected OperatorRecording operatorRecording = null;
    protected Vector<Qubit> visibleQubits = new Vector<Qubit>(NUM_QBITS);
    protected Qubit sumQbit;
    protected int selectedQbit = 0;
    protected Qubit magFieldArrow;

    // Constructor
    public Bloch3D(Container contentPane) {
	this.contentPane = contentPane;
	this.contentPane.setLayout(new BorderLayout());
	qBitControlPanel = new JPanel();
	Border eborder = BorderFactory.createEtchedBorder();
	qBitControlPanel.setBorder(eborder);
	qBitControlPanel.setLayout(new BoxLayout(qBitControlPanel,
		BoxLayout.Y_AXIS));
	this.contentPane.add(create3DWorld(), BorderLayout.CENTER);
	this.contentPane.add(qBitControlPanel, BorderLayout.EAST);
	qBitControlPanel.add(javax.swing.Box.createVerticalStrut(5));
	for (int i = 0; i < NUM_QBITS; ++i) {
	    visibleQubits.add(new Qubit(i + 1, i * 18, i * 36,
		    qbit_color_list[i], qBitsGroup));
	}
	sumQbit = new Qubit(NUM_QBITS + 4, 45, 45, LIGHT_RED, qBitsGroup);
	sumQbit.setVisible(false);
	displayToggleControlPanel = new JPanel();
	displayToggleControlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	displayAllButton = new JButton("Display All");
	displayAllButton.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < Bloch3D.NUM_QBITS; ++i)
		    visibleQubits.get(i).setVisible(true);
		sumQbit.setVisible(false);
	    }
	});
	displayToggleControlPanel.add(displayAllButton);
	displayNoneButton = new JButton("Hide All");
	displayNoneButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < Bloch3D.NUM_QBITS; ++i)
		    visibleQubits.get(i).setVisible(false);
		if (sumQbit != null)
		    sumQbit.setVisible(false);
	    }
	});
	displayToggleControlPanel.add(displayNoneButton);
	qBitControlPanel.add(displayToggleControlPanel);

	qubitPane.setTabPlacement(JTabbedPane.LEFT);
	for (int i = 0; i < visibleQubits.size(); i++) {
	    int counter = i + 1;
	    qubitPane.add("Qubit " + counter, visibleQubits.get(i)
		    .getQbitPanel());
	}/*
	  * qubitPane.add("Sum Qubit", sumQbit.getQbitPanel());
	  */

	JPanel tabbedPanel = buildTabbedPanePanel();
	operatorRecording = new OperatorRecording(qubitOperator);

	qBitControlPanel.add(qubitPane);
	qBitControlPanel.add(operatorRecording.getControlPanel());
	operatorRecording.getControlPanel().setVisible(false);
	operatorRecording.getStopRecordingButton().addActionListener(this);
	qBitControlPanel.add(javax.swing.Box.createVerticalGlue());
	qBitControlPanel.add(tabbedPanel);
    }

    /**
     * Builds the tabbed pane panel with the larmor precession and the random
     * magnetic field panes in it.
     * 
     * @return A JPanel with the tabbed pane in it.
     */
    public JPanel buildTabbedPanePanel() {
	JPanel tabbedPanel = new JPanel();
	tabbedPanel.setPreferredSize(new Dimension(400, 350));
	tabbedPanel.setMinimumSize(new Dimension(400, 350));
	tabbedPanel.setLayout(null);
	transformTabbedPane = new JTabbedPane();
	transformTabbedPane.setTabPlacement(JTabbedPane.TOP);
	transformTabbedPane.setBounds(0, 0, 400, 350);
	tabbedPanel.add(transformTabbedPane);
	qubitOperator = new QubitOperator(visibleQubits);

	operatorPlayback = new OperatorPlayback(visibleQubits);
	operatorPlayback.getPlayBackButton().addActionListener(this);
	operatorPlayback.getStopPlayBackButton().addActionListener(this);
	operatorPlayback.getRecordButton().addActionListener(this);

	m_precession = new LarmorPrecession(visibleQubits);

	rabiField = new RabiField(this.qBitsGroup, visibleQubits);

	randomMagneticFieldControl = new RandomMag(sumQbit, this.qBitsGroup);
	randomMagneticFieldControl.setSumQubit(sumQbit);

	transformTabbedPane.addTab(qubitOperator.getTabName(), qubitOperator
		.getControlPanel());
	transformTabbedPane.addTab(operatorPlayback.getTabName(),
		operatorPlayback.getControlPanel());
	transformTabbedPane.addTab(m_precession.getTabName(), m_precession
		.getControlPanel());
	transformTabbedPane.addTab(rabiField.getTabName(), rabiField
		.getControlPanel());
	// transformTabbedPane.addTab(randomMagneticFieldControl.getTabName(),
	// randomMagneticFieldControl.getControlPanel());
	return tabbedPanel;
    }

    // this creates the 3d world and returns a Canvas object
    private Canvas3D create3DWorld() {
	GraphicsConfiguration config = SimpleUniverse
		.getPreferredConfiguration();
	Canvas3D can3d = new Canvas3D(config);
	SimpleUniverse su = new SimpleUniverse(can3d);
	BranchGroup objects = createObjectGraph();
	objects.compile();
	su.addBranchGraph(objects);
	su.getViewingPlatform().setNominalViewingTransform();
	return can3d;
    }

    // Add object to our 3d scene. Called from create3dWorld--------
    public BranchGroup createObjectGraph() {
	// Create the root of the branch graph
	BranchGroup broot = new BranchGroup();
	viewingTransformGroup.setTransform(getResetView());
	viewingTransformGroup = addMouseControl(viewingTransformGroup);
	viewingTransformGroup.addChild(createSphere(QUBIT_MAG, 0.7f,
		Bloch3D.sphere_color));
	viewingTransformGroup.addChild(createXYPlane(QUBIT_MAG, 0.6f,
		Bloch3D.xyplane_color));
	viewingTransformGroup
		.addChild(createAxes(QUBIT_MAG, Bloch3D.axes_color));
	viewingTransformGroup.addChild(createxzMeridian(QUBIT_MAG, 50));
	viewingTransformGroup.addChild(createyzMeridian(QUBIT_MAG, 50));
	viewingTransformGroup.addChild(createEquator(QUBIT_MAG, 50));
	qBitsGroup = new BranchGroup();
	qBitsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	qBitsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	qBitsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	qBitsGroup.setCapability(BranchGroup.ALLOW_DETACH);
	viewingTransformGroup.addChild(qBitsGroup);
	broot.addChild(viewingTransformGroup);
	// set background color
	Background bg = new Background(Bloch3D.background_color);
	bg.setApplicationBounds(bounds);
	broot.addChild(bg);
	broot = setLights(broot, WHITE, WHITE, RED);
	return broot;
    }

    private Transform3D getResetView() {
	Transform3D rot = new Transform3D();
	Transform3D rot2 = new Transform3D();
	rot.rotX(-Math.PI / 2 + 0.5);
	rot2.rotZ(-Math.PI / 2 - 0.5);
	rot.mul(rot2);
	return rot;
    }

    public void resetView() {
	this.viewingTransformGroup.setTransform(getResetView());
    }

    public BranchGroup setLights(BranchGroup bg, Color3f ambcol,
	    Color3f dir1col, Color3f dir2col) {
	// ambient lights
	AmbientLight ambientLightNode = new AmbientLight(ambcol);
	ambientLightNode.setInfluencingBounds(bounds);
	bg.addChild(ambientLightNode);
	// directional
	DirectionalLight dlight1 = new DirectionalLight(dir1col, new Vector3f(
		0.0f, 0.3f, -1.0f));
	dlight1.setInfluencingBounds(bounds);
	bg.addChild(dlight1);
	return bg;
    }

    public TransformGroup addMouseControl(TransformGroup tg) {
	// Add mouse behavior to the scene
	tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	MouseRotate mr = new MouseRotate();
	mr.setTransformGroup(tg);
	mr.setSchedulingBounds(bounds);
	mr.setFactor(0.007);
	tg.addChild(mr);
	return tg;
    }

    // make SPHERE
    // ---------------------------------------------------------------------
    public BranchGroup createSphere(float rad, float transparency, Color3f color) {
	BranchGroup bsph = new BranchGroup();
	Sphere s = new Sphere(rad, Sphere.GENERATE_NORMALS, 30);
	// Fix the appearance of the sphere
	Appearance ap = new Appearance();
	Color3f ambientColor = new Color3f(color);
	ambientColor.scale(IBlochConstants.AMBIENT_SCALE_FACTOR);
	Material material = new Material(ambientColor, BLACK, color, WHITE, 120);
	material.setLightingEnable(true);
	ap.setMaterial(material);
	ap.setPolygonAttributes(new PolygonAttributes(
		PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_BACK,
		0.0f));
	ap.setTransparencyAttributes(new TransparencyAttributes(
		TransparencyAttributes.BLENDED, transparency));
	// ap.setColoringAttributes(new ColoringAttributes(color,
	// ColoringAttributes.NICEST));
	s.setAppearance(ap);
	bsph.addChild(s);
	return bsph;
    }

    // make the XY plane, the inersection of the sphere and the xy plane
    // actually
    public BranchGroup createXYPlane(float rad, float transparency,
	    Color3f color) {
	BranchGroup bsph = new BranchGroup();
	Appearance cap = new Appearance();
	Color3f ambientColor = new Color3f(color);
	ambientColor.scale(IBlochConstants.AMBIENT_SCALE_FACTOR);
	Material cmat = new Material(ambientColor, BLACK, color, WHITE, 10);
	cmat.setLightingEnable(true);
	cap.setMaterial(cmat);
	cap.setPolygonAttributes(new PolygonAttributes(
		PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_BACK,
		0.0f));
	cap.setTransparencyAttributes(new TransparencyAttributes(
		TransparencyAttributes.BLENDED, transparency));
	Cylinder xy = new Cylinder(rad, 0.0002f, Cylinder.GENERATE_NORMALS, 50,
		1, cap);
	Transform3D tmpTrans = new Transform3D();
	tmpTrans.rotX(Math.PI / 2f);
	TransformGroup cyltg = new TransformGroup(tmpTrans);
	cyltg.addChild(xy);
	bsph.addChild(cyltg);
	return bsph;
    }

    private Appearance getMeridianAppearance() {
	Appearance ap = new Appearance();
	ap.setTransparencyAttributes(new TransparencyAttributes(
		TransparencyAttributes.BLENDED, .3f));
	ap.setColoringAttributes(new ColoringAttributes(Bloch3D.meridian_color,
		ColoringAttributes.NICEST));
	return ap;
    }

    public BranchGroup createxzMeridian(float rad, int segcount) {
	BranchGroup b = new BranchGroup();
	double intlength = Math.PI * 2 / segcount;
	int intcount = (int) Math.floor((Math.PI * 2.0d) / intlength);
	Point3f[] points = new Point3f[intcount + 1];
	int intindex = 0;
	float x, z;
	for (float th = 0; th < Math.PI * 2; th += intlength) {
	    x = rad * (float) Math.cos(th);
	    z = rad * (float) Math.sin(th);
	    points[intindex++] = new Point3f(x, 0, z);
	}
	points[intcount] = new Point3f(rad, 0, 0);
	int[] stripLengths = { intcount + 1 };
	LineStripArray meridianShape = new LineStripArray(points.length,
		GeometryArray.COORDINATES, stripLengths);
	meridianShape.setCoordinates(0, points);
	Shape3D meridian = new Shape3D(meridianShape, getMeridianAppearance());
	b.addChild(meridian);
	return b;
    }

    public BranchGroup createyzMeridian(float rad, int segcount) {
	BranchGroup b = new BranchGroup();
	double intlength = Math.PI * 2 / segcount;
	int intcount = (int) Math.floor((Math.PI * 2.0d) / intlength);
	Point3f[] points = new Point3f[intcount + 1];
	int intindex = 0;
	float y, z;
	for (float th = 0; th < Math.PI * 2; th += intlength) {
	    y = rad * (float) Math.cos(th);
	    z = rad * (float) Math.sin(th);
	    points[intindex++] = new Point3f(0, y, z);
	}
	points[intcount] = new Point3f(0, rad, 0);
	int[] stripLengths = { intcount + 1 };
	LineStripArray meridianShape = new LineStripArray(points.length,
		GeometryArray.COORDINATES, stripLengths);
	meridianShape.setCoordinates(0, points);
	Shape3D meridian = new Shape3D(meridianShape, getMeridianAppearance());
	b.addChild(meridian);
	return b;
    }

    public BranchGroup createEquator(float rad, int segcount) {
	BranchGroup b = new BranchGroup();
	double intlength = Math.PI * 2 / segcount;
	int intcount = (int) Math.floor((Math.PI * 2.0d) / intlength);
	Point3f[] points = new Point3f[intcount + 1];
	int intindex = 0;
	float x, y;
	for (float th = 0; th < Math.PI * 2; th += intlength) {
	    x = rad * (float) Math.cos(th);
	    y = rad * (float) Math.sin(th);
	    points[intindex++] = new Point3f(x, y, 0);
	}
	points[intcount] = new Point3f(rad, 0, 0);
	int[] stripLengths = { intcount + 1 };
	LineStripArray meridianShape = new LineStripArray(points.length,
		GeometryArray.COORDINATES, stripLengths);
	meridianShape.setCoordinates(0, points);
	Shape3D meridian = new Shape3D(meridianShape, getMeridianAppearance());
	b.addChild(meridian);
	return b;
    }

    // make AXES
    // ---------------------------------------------------------------------
    public BranchGroup createAxes(float coordSysLength, Color3f color) {
	BranchGroup ba = new BranchGroup();
	// constraints
	Point3f O = new Point3f();
	coordSysLength *= 2f;
	float labelOffset = coordSysLength / 14;
	float axisRadius = coordSysLength / 300;
	float arrowHeight = coordSysLength / 50;
	float fontScale = 0.06f;
	// set up the material
	Color3f ambientColor = new Color3f(color);
	ambientColor.scale(IBlochConstants.AMBIENT_SCALE_FACTOR);
	Material material = new Material(ambientColor, BLACK, color, WHITE, 30);
	material.setLightingEnable(true);
	Appearance ap = new Appearance();
	ap.setMaterial(material);
	ap.setPolygonAttributes(new PolygonAttributes(
		PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE,
		0.0f));
	// ap.setColoringAttributes(new ColoringAttributes(color,
	// ColoringAttributes.NICEST));
	SharedGroup sharedAxis = new SharedGroup();
	// Main branch: from -coorSysLength/2 -> +coorSysLeng/2
	Cylinder cylinder = new Cylinder(axisRadius, coordSysLength,
		Cylinder.GENERATE_NORMALS, ap);
	sharedAxis.addChild(cylinder);
	// ArrowHead
	/*
	 * Cone arrowHead = new Cone(arrowRadius, arrowHeight,
	 * Cone.GENERATE_NORMALS, ap); SharedGroup sharedArrow = new
	 * SharedGroup(); sharedArrow.addChild(arrowHead);
	 */
	// Transofrm to move arrowHead to the top of axis
	Transform3D tmpTrans = new Transform3D();
	Vector3f tmpVector = new Vector3f();
	tmpVector.set(0f, coordSysLength / 2 + arrowHeight / 2, 0f);
	tmpTrans.set(tmpVector);
	TransformGroup topTG = new TransformGroup();
	topTG.setTransform(tmpTrans);
	// topTG.addChild(new Link(sharedArrow));
	sharedAxis.addChild(topTG);
	// Y axis
	ba.addChild(new Link(sharedAxis));
	// -----now make X,Z axes -----------
	// --1) Make X: <= rot pi/2 of y around z
	// -90' around Z
	tmpTrans.rotZ(-Math.PI / 2.0);
	TransformGroup xTransGroup = new TransformGroup(tmpTrans);
	xTransGroup.addChild(new Link(sharedAxis));
	// X axis
	ba.addChild(xTransGroup);
	// --2) Make Z: <= rot pi/2 of y around x
	// 90' around X
	tmpTrans.rotX(Math.PI / 2.0);
	TransformGroup zTransGroup = new TransformGroup(tmpTrans);
	zTransGroup.setTransform(tmpTrans);
	zTransGroup.addChild(new Link(sharedAxis));
	// Z axis
	ba.addChild(zTransGroup);
	// Labels
	Font3D f3d = new Font3D(new Font("Default", Font.PLAIN, 1), null);
	// Y label
	Text3D Xtxt = new Text3D(f3d, "+y", O, Text3D.ALIGN_CENTER,
		Text3D.PATH_RIGHT);
	// orient around local origin
	OrientedShape3D XtxtShape = new OrientedShape3D(Xtxt, ap,
		OrientedShape3D.ROTATE_ABOUT_POINT, O);
	Appearance apLab = new Appearance();
	apLab.setColoringAttributes(new ColoringAttributes(Bloch3D.label_color,
		ColoringAttributes.NICEST));
	XtxtShape.setAppearance(apLab);
	// transform to scale to .15 in height, put at end of axis
	TransformGroup Xtrans = new TransformGroup();
	tmpVector.set(0f, coordSysLength / 2 + labelOffset, 0f);
	tmpTrans.set(fontScale, tmpVector);
	Xtrans.setTransform(tmpTrans);
	Xtrans.addChild(XtxtShape);
	ba.addChild(Xtrans);
	// X
	Text3D Ytxt = new Text3D(f3d, "+x", O, Text3D.ALIGN_CENTER,
		Text3D.PATH_RIGHT);
	// orient
	OrientedShape3D YtxtShape = new OrientedShape3D(Ytxt, ap,
		OrientedShape3D.ROTATE_ABOUT_POINT, O);
	YtxtShape.setAppearance(apLab);
	// transform
	TransformGroup Ytrans = new TransformGroup();
	tmpVector.set(coordSysLength / 2 + labelOffset, 0f, 0f);
	tmpTrans.set(fontScale, tmpVector);
	Ytrans.setTransform(tmpTrans);
	Ytrans.addChild(YtxtShape);
	ba.addChild(Ytrans);
	// Z
	Text3D Ztxt = new Text3D(f3d, "+z = |0>", O, Text3D.ALIGN_CENTER,
		Text3D.PATH_RIGHT);
	// orient
	OrientedShape3D ZtxtShape = new OrientedShape3D(Ztxt, ap,
		OrientedShape3D.ROTATE_ABOUT_POINT, O);
	ZtxtShape.setAppearance(apLab);
	// transform
	TransformGroup Ztrans = new TransformGroup();
	tmpVector.set(0f, 0f, coordSysLength / 2 + labelOffset);
	tmpTrans.set(fontScale, tmpVector);
	Ztrans.setTransform(tmpTrans);
	Ztrans.addChild(ZtxtShape);
	ba.addChild(Ztrans);
	Text3D Ztxtm = new Text3D(f3d, "-z = |1>", O, Text3D.ALIGN_CENTER,
		Text3D.PATH_RIGHT);
	OrientedShape3D ZtxtmShape = new OrientedShape3D(Ztxtm, ap,
		OrientedShape3D.ROTATE_ABOUT_POINT, O);
	ZtxtmShape.setAppearance(apLab);
	tmpVector.set(0f, 0f, -coordSysLength / 2 - labelOffset);
	tmpTrans.set(fontScale, tmpVector);
	TransformGroup Ztransm = new TransformGroup(tmpTrans);
	Ztransm.addChild(ZtxtmShape);
	ba.addChild(Ztransm);
	return ba;
    }

    /**
     * Gets the Vector of visible qubits.
     * 
     * @return a Vector of Vector<Qubit>
     */
    public Vector<Qubit> getVisibleQubits() {
	return visibleQubits;
    }

    public void actionPerformed(ActionEvent event) {
	if (event.getSource().equals(operatorPlayback.getRecordButton())) {
	    setRecordingMode(true);
	}
	if (event.getSource().equals(operatorPlayback.getStopPlayBackButton())) {
	    setPlayBackMode(false);
	}
	if (event.getSource().equals(operatorPlayback.getPlayBackButton())) {
	    setPlayBackMode(true);
	}
	if (event.getSource()
		.equals(operatorRecording.getStopRecordingButton())) {
	    setRecordingMode(false);
	}

    }

    private void setRecordingMode(boolean isRecording) {
	qubitPane.setVisible(!isRecording);
	operatorRecording.getControlPanel().setVisible(isRecording);
	if (isRecording) {
	    operatorRecording.resetRecording();
	    transformTabbedPane.removeAll();
	    transformTabbedPane.addTab(qubitOperator.getTabName(),
		    qubitOperator.getControlPanel());
	} else {
	    transformTabbedPane.removeAll();
	    transformTabbedPane.addTab(qubitOperator.getTabName(),
		    qubitOperator.getControlPanel());
	    transformTabbedPane.addTab(operatorPlayback.getTabName(),
		    operatorPlayback.getControlPanel());
	    transformTabbedPane.addTab(m_precession.getTabName(), m_precession
		    .getControlPanel());
	    transformTabbedPane.addTab(rabiField.getTabName(), rabiField
		    .getControlPanel());
	}
    }

    private void setPlayBackMode(boolean playBack) {
	qubitPane.setEnabled(!playBack);
	if (playBack) {
	    transformTabbedPane.removeAll();
	    transformTabbedPane.addTab(operatorPlayback.getTabName(),
		    operatorPlayback.getControlPanel());
	} else {
	    transformTabbedPane.removeAll();
	    transformTabbedPane.addTab(qubitOperator.getTabName(),
		    qubitOperator.getControlPanel());
	    transformTabbedPane.addTab(operatorPlayback.getTabName(),
		    operatorPlayback.getControlPanel());
	    transformTabbedPane.addTab(m_precession.getTabName(), m_precession
		    .getControlPanel());
	    transformTabbedPane.addTab(rabiField.getTabName(), rabiField
		    .getControlPanel());
	}
    }

    public void setPlayBackOperators(Vector<Operator> operators) {
	operatorPlayback.loadData(operators);
    }
}
