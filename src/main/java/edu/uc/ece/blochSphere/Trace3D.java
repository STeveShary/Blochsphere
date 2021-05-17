package edu.uc.ece.blochSphere;

////////////////////////////////////////////////////////
//Created by Nick Vatamaniuc
//Univ of Cincinnati, Quantum Computing, Spring 2004
/////////////////////////////////////////////////////////
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Link;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

//********************************************************************
// This is the trace for the Hadamard transform when phi=0, theta<90
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public class Trace3D {

    // The parent which used to remove itself upon deletion.
    protected BranchGroup parentBranchGroup = null;
    public static final float axisRadiusCoef = 100;
    public static final float arrowRadiusCoef = 30;
    public static final float arrowHeightCoef = 10;
    private BranchGroup transbg, blurbg;
    private Qubit3DModel oldQbit;
    Transform3D thetaTM, phiTM, qbitTM;
    Transform3D tmpTrans = new Transform3D();
    Vector3f tmpVector = new Vector3f();
    Appearance trans_ap, rot_ap;
    float mag, th, phi;
    Color3f color;

    public Trace3D(Color3f color, float transparency, BranchGroup parentBG) {
	this.color = color;
	parentBranchGroup = parentBG;
	rot_ap = makeAppearance(color, transparency);
	trans_ap = makeAppearance(Bloch3D.MAGENTA, 0.0f);
    }

    public void addTrace(Qubit qbit) {
	removeTrace();
	mag = (float) qbit.m_vectorMagnitude;
	th = (float) qbit.m_thetaAngle;
	phi = (float) qbit.m_phiAngle;
	transbg = createXYTranslateArrow();
	blurbg = createBlur(8);
	parentBranchGroup.addChild(transbg);
	parentBranchGroup.addChild(blurbg);
    }

    public void removeTrace() {
	if (transbg != null && parentBranchGroup != null)
	    parentBranchGroup.removeChild(transbg);
	if (blurbg != null && parentBranchGroup != null)
	    parentBranchGroup.removeChild(blurbg);
	if (oldQbit != null) {
	    oldQbit.removeComponents();
	    oldQbit = null;
	}
    }

    private Appearance makeAppearance(Color3f color, float transparency) {
	Appearance ap = new Appearance();
	ap.setTransparencyAttributes(new TransparencyAttributes(
		TransparencyAttributes.BLENDED, transparency));
	ap.setColoringAttributes(new ColoringAttributes(color,
		ColoringAttributes.FASTEST));
	return ap;
    }

    public Transform3D createRotTransform(float t, float p) {
	Transform3D tm = new Transform3D();
	Transform3D ry = new Transform3D();
	Transform3D rz = new Transform3D();
	ry.rotY(Math.toRadians(t - 90));
	rz.rotZ(Math.toRadians(p));
	tm.mul(rz, ry);
	return tm;
    }

    private BranchGroup createBlur(int n) {
	BranchGroup result = new BranchGroup();
	result.setCapability(BranchGroup.ALLOW_DETACH);
	SharedGroup blurredQbit = new SharedGroup();
	blurredQbit.addChild(makeOneBlurredQbit());
	float oldTheta = 180f - th;
	float sign;
	if (oldTheta >= 90) {
	    oldTheta -= 90;
	    sign = 1.0f;
	} else {
	    oldTheta += 90;
	    sign = -1.0f;
	}
	if (Math.abs(oldTheta - th) > 2) {
	    oldQbit = new Qubit3DModel(oldTheta, phi, Bloch3D.GREY,
		    parentBranchGroup);
	}
	int interval = (int) 90 / n;
	TransformGroup tmpTG;
	for (float i = 0; i < 90; i += interval) {
	    tmpTG = new TransformGroup(createRotTransform(sign * i + oldTheta,
		    this.phi));
	    tmpTG.addChild(new Link(blurredQbit));
	    result.addChild(tmpTG);
	}
	return result;
    }

    private BranchGroup makeOneBlurredQbit() {
	float axisRadius = mag / 100, arrowRadius = mag / 30, arrowHeight = mag / 10;
	// make the segment
	Cylinder cylinder = new Cylinder(axisRadius, mag - arrowHeight / 2,
		Cylinder.GENERATE_NORMALS, rot_ap);
	tmpVector.set(0f, (float) (mag / 2.0f), 0f);
	tmpTrans.set(tmpVector);
	// move the segment so its from 0 to +mag on the Y axis
	TransformGroup cyltg = new TransformGroup();
	cyltg.setTransform(tmpTrans);
	cyltg.addChild(cylinder);
	// Add the arrow ahead, a cone
	Cone arrowHead = new Cone(arrowRadius, arrowHeight,
		Cone.GENERATE_NORMALS, rot_ap); // was
	// hap
	tmpVector.set(0.0f, mag, 0.0f);
	tmpTrans.set(tmpVector);
	// move the arrow head to the end of the body of the arrow
	TransformGroup transHead = new TransformGroup(tmpTrans);
	transHead.addChild(arrowHead);
	// initialize this to the xAxis
	tmpTrans.rotZ(-Math.PI / 2.0);
	TransformGroup result = new TransformGroup(tmpTrans);
	result.addChild(cyltg);
	result.addChild(transHead);
	BranchGroup bg = new BranchGroup();
	bg.addChild(result);
	return bg;
    }

    public BranchGroup createXYTranslateArrow() {
	float axisRadius = mag / 250, arrowRadius = mag / 50, arrowHeight = mag / 6;
	float thr = (float) Math.toRadians(th);
	float phr = (float) Math.toRadians(phi);
	float len = (float) Math.abs(1.3 * Math.cos(thr));
	BranchGroup result = new BranchGroup();
	result.setCapability(BranchGroup.ALLOW_DETACH);
	if (th > 88 && th < 92)
	    return result;
	Cylinder xyCyl = new Cylinder(axisRadius, len - arrowHeight,
		Cylinder.GENERATE_NORMALS, trans_ap);
	// Add the arrow ahead, a cone
	Cone arrowHead = new Cone(arrowRadius, arrowHeight,
		Cone.GENERATE_NORMALS, trans_ap);
	TransformGroup arrow;
	if (th <= 90) {
	    tmpVector.set(0.0f, len / 2, 0.0f);
	    tmpTrans.set(tmpVector);
	    arrow = new TransformGroup(tmpTrans);
	    arrow.addChild(arrowHead);
	} else {
	    tmpTrans.rotX(Math.PI);
	    TransformGroup rotateArrowHeadTG = new TransformGroup(tmpTrans);
	    rotateArrowHeadTG.addChild(arrowHead);
	    tmpVector.set(0f, -len / 2, 0f);
	    tmpTrans.set(tmpVector);
	    arrow = new TransformGroup(tmpTrans);
	    arrow.addChild(rotateArrowHeadTG);
	}
	tmpTrans.rotX(Math.PI / 2.0);
	TransformGroup rotXTG = new TransformGroup(tmpTrans);
	rotXTG.addChild(arrow);
	rotXTG.addChild(xyCyl);
	float xyProjMag = (float) Math.abs(mag * Math.sin(Math.toRadians(th)));
	float xTrans = (float) (xyProjMag * Math.cos(phr));
	float yTrans = (float) (xyProjMag * Math.sin(phr));
	tmpVector.set(xTrans, yTrans, 0f);
	tmpTrans.set(tmpVector);
	TransformGroup zTG = new TransformGroup(tmpTrans);
	zTG.addChild(rotXTG);
	result.addChild(zTG);
	return result;
    }
}
