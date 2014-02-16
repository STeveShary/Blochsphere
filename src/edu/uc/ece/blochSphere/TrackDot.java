package edu.uc.ece.blochSphere;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * This is the tracking that is used to show the different random magnetic
 * fields on the qubit.
 * 
 * Commented, reformatted by Stephen Shary
 * 
 */
public class TrackDot {

    // The parent which used to remove itself upon deletion.
    protected BranchGroup parentBranchGroup = null;
    private static final float ambientScaleFactor = 0.5f;
    public static final float arrowRadiusCoef = 30;
    public static final float arrowHeightCoef = 10;
    public static final float fontScale = 0.07f;
    public static final Vector3f xAxis = new Vector3f(1, 0, 0);
    public static final Vector3f yAxis = new Vector3f(0, 1, 0);
    public static final Vector3f zAxis = new Vector3f(0, 0, 1);
    private BranchGroup root;
    private TransformGroup initGroup;
    private TransformGroup rotationGroup;
    Transform3D thetaTM = null;
    Transform3D phiTM = null;
    Transform3D qbitTM = null;
    Transform3D tmpTrans = new Transform3D();
    // Used to do quick rotations around the different axis.
    Vector3f tmpVector = new Vector3f();
    Vector3f initVector = new Vector3f(xAxis);
    Vector3f currentVector = new Vector3f();
    Appearance dotAppearance = new Appearance();
    double magnitude = 1;
    double theta = 0;
    double phi = 0;
    Color3f ambientColor = new Color3f(Bloch3D.LIGHT_RED);
    boolean proj;
    double sphereRadius, arrowHeight;
    BranchGroup projectionBG;
    BranchGroup traceBG;
    BranchGroup trackDotGroup;

    /**
     * This will construct the dot to make it ready for transforms.
     * 
     * @param mag
     *            magnitude of the dot.
     * @param th
     *            the theta angle of the dot.
     * @param phi
     *            the phi angle of the dot.
     * @param parentBG
     *            the parent Branch group (used to remove itself from the
     *            parent).
     */
    public TrackDot(double mag, double th, double phi, BranchGroup parentBG) {
	this(mag, th, phi, parentBG, Bloch3D.RED, false);
    }

    /**
     * This will construct the dot to make it ready for transforms.
     * 
     * @param mag
     *            magnitude of the dot.
     * @param th
     *            the theta angle of the dot.
     * @param phi
     *            the phi angle of the dot.
     * @param parentBG
     *            the parent Branch group (used to remove itself from the
     *            parent).
     */
    public TrackDot(double mag, double th, double phi, BranchGroup parentBG,
	    Color3f color, boolean big) {
	this.magnitude = mag;
	this.theta = th;
	this.phi = phi;
	parentBranchGroup = parentBG;
	ambientColor.scale(ambientScaleFactor);
	Material mat = new Material(ambientColor, color, ambientColor,
		Bloch3D.WHITE, 30);
	mat.setLightingEnable(true);
	dotAppearance.setMaterial(mat);
	sphereRadius = (big) ? 0.05 : .005;
	arrowHeight = mag / arrowHeightCoef;
	root = new BranchGroup();
	root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	root.setCapability(BranchGroup.ALLOW_DETACH);
	parentBranchGroup.addChild(root);
	rotationGroup = new TransformGroup();
	rotationGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	rotationGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	initializePosition();
	root.addChild(trackDotGroup);
	setThetaPhi(th, phi);
    }

    /**
     * This will remove the trackDot from the parent container object.
     */
    public void kill() {
	root.detach();
	root = null;
    }

    /**
     * This will position the cone and the move the tracking dot to the
     * "resting" or starting position.
     * 
     * @return the branch group object representing the dot and its position.
     */
    public void initializePosition() {
	thetaTM = new Transform3D();
	phiTM = new Transform3D();
	qbitTM = new Transform3D();
	Sphere point = new Sphere(new Double(sphereRadius).floatValue(),
		Cone.GENERATE_NORMALS, dotAppearance);
	tmpVector.set(0.0f, new Double(magnitude).floatValue(), 0.0f);
	tmpTrans.set(tmpVector);
	TransformGroup transHead = new TransformGroup(tmpTrans);
	transHead.addChild(point);
	tmpTrans.rotZ(-Math.PI / 2.0);
	initGroup = new TransformGroup(tmpTrans);
	initGroup.addChild(transHead);
	rotationGroup.removeAllChildren();
	rotationGroup.addChild(initGroup);
	trackDotGroup = new BranchGroup();
	trackDotGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	trackDotGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	trackDotGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	trackDotGroup.setCapability(BranchGroup.ALLOW_DETACH);
	trackDotGroup.addChild(rotationGroup);
    }

    /**
     * This will rotate the track dot to the input theta and phi positions.
     * 
     * @param newTh
     *            the new theta position. This should be input in the form of
     *            degrees.
     * 
     * @param newPhi
     *            the new phi position. This should be in the form of the
     *            degrees.
     */
    public void setThetaPhi(double newTh, double newPhi) {
	thetaTM.rotY(Math.toRadians(newTh - 90));
	phiTM.rotZ(Math.toRadians(newPhi));
	qbitTM.mul(phiTM, thetaTM);
	transformTrackDot(qbitTM);
    }

    /**
     * This will transform the trackDot through the input rotation.
     * 
     * @param qubitRotation
     *            the rotation to apply to the track dot.
     */
    public void transformTrackDot(Transform3D qubitRotation) {
	if (root == null)
	    return;
	rotationGroup.setTransform(qubitRotation);
	qubitRotation.transform(initVector, currentVector);
    }

    /**
     * This will add or remove the trackdot from the parent based on the input.
     * 
     * @param visible
     *            if true and the trackdot is not already "visible" or added to
     *            the parent, then it will be added to the parent. Otherwise it
     *            will be removed.
     */
    public void setVisible(boolean visible) {
	if (visible && root.numChildren() == 0) {
	    root.addChild(trackDotGroup);
	} else if (!visible) {
	    root.removeChild(trackDotGroup);
	}
    }
}