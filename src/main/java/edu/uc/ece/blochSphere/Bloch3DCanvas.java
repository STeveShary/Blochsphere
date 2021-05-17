package edu.uc.ece.blochSphere;

import java.awt.Font;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
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
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * This class will provide the
 * 
 */
public class Bloch3DCanvas implements IBlochConstants {

    protected Canvas3D canvas3D = null;
    protected BranchGroup qBitsGroup = null;

    /**
     * This will rebuild the sphere, planes, and axises.
     * 
     */
    public void rebuildCanvas() {
	canvas3D.setVisible(false);
	canvas3D = null;
	buildCanvas();
    }

    /**
     * Gets the current canvas object to display.
     * 
     * @return the canvas object to display.
     */
    public Canvas3D getCanvas() {
	return canvas3D;
    }

    /**
     * Creates the basic canvas. This will call the child functions to populate
     * with the axises...
     */
    public void buildCanvas() {
	GraphicsConfiguration config = SimpleUniverse
		.getPreferredConfiguration();
	canvas3D = new Canvas3D(config);
	SimpleUniverse su = new SimpleUniverse(canvas3D);
	BranchGroup objects = createObjectGraph();
	objects.compile();
	su.addBranchGraph(objects);
	su.getViewingPlatform().setNominalViewingTransform();
    }

    /**
     * Populates the BranchGroup with all fo the objects in the basic scene.
     * 
     * @return A BranchGroup of all fo the axises.
     */
    public BranchGroup createObjectGraph() {
	// Create the root of the branch graph
	BranchGroup broot = new BranchGroup();
	Transform3D rot = new Transform3D();
	Transform3D rot2 = new Transform3D();
	rot.rotX(-Math.PI / 2 + 0.5);
	rot2.rotZ(-Math.PI / 2 - 0.5);
	rot.mul(rot2);
	TransformGroup rottr = new TransformGroup();
	rottr.setTransform(rot);
	rottr = addMouseControl(rottr);
	rottr.addChild(createSphere(QUBIT_MAG, 0.7f, Bloch3D.sphere_color));
	rottr.addChild(createXYPlane(QUBIT_MAG, 0.6f, Bloch3D.xyplane_color));
	rottr.addChild(createAxes(QUBIT_MAG, Bloch3D.axes_color));
	rottr.addChild(createxzMeridian(QUBIT_MAG, 50));
	rottr.addChild(createyzMeridian(QUBIT_MAG, 50));
	rottr.addChild(createEquator(QUBIT_MAG, 50));
	qBitsGroup = new BranchGroup();
	qBitsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	qBitsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	qBitsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	qBitsGroup.setCapability(BranchGroup.ALLOW_DETACH);
	rottr.addChild(qBitsGroup);
	broot.addChild(rottr);
	// set background color
	Background bg = new Background(Bloch3D.background_color);
	bg.setApplicationBounds(bounds);
	broot.addChild(bg);
	broot = setLights(broot, WHITE, WHITE, RED);
	return broot;
    }

    public BranchGroup getQubitsGroup() {
	return qBitsGroup;
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
	MouseZoom mz = new MouseZoom();
	mz.setTransformGroup(tg);
	mz.setSchedulingBounds(bounds);
	mz.setFactor(0.007);
	tg.addChild(mz);
	MouseTranslate mt = new MouseTranslate();
	mt.setTransformGroup(tg);
	mt.setSchedulingBounds(bounds);
	mt.setFactor(0.007);
	tg.addChild(mt);
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
	ambientColor.scale(AMBIENT_SCALE_FACTOR);
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
	ambientColor.scale(AMBIENT_SCALE_FACTOR);
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
	float labelOffset = coordSysLength / 14, axisRadius = coordSysLength / 250, arrowRadius = coordSysLength / 100, arrowHeight = coordSysLength / 50, fontScale = 0.06f;
	// set up the material
	Color3f ambientColor = new Color3f(color);
	ambientColor.scale(AMBIENT_SCALE_FACTOR);
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
	Cone arrowHead = new Cone(arrowRadius, arrowHeight,
		Cone.GENERATE_NORMALS, ap);
	SharedGroup sharedArrow = new SharedGroup();
	sharedArrow.addChild(arrowHead);
	// Transofrm to move arrowHead to the top of axis
	Vector3f tmpVector = new Vector3f();
	tmpVector.set(0f, coordSysLength / 2 + arrowHeight / 2, 0f);
	Transform3D tmpTrans = new Transform3D();
	tmpTrans.set(tmpVector);
	TransformGroup topTG = new TransformGroup();
	topTG.setTransform(tmpTrans);
	topTG.addChild(new Link(sharedArrow));
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
}
