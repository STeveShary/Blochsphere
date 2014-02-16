package edu.uc.ece.blochSphere;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

/**
 * The 3-D model representation of the qubit.
 * 
 * @author Nick Vatamaniuc, Stephen Shary
 */
public class Qubit3DModel implements IQubit3DModel {

    protected BranchGroup rootBranchGroup = new BranchGroup();
    // The parent which used to remove itself upon deletion.
    protected BranchGroup parentBranchGroup = null;
    protected TransformGroup initGroup;
    protected TransformGroup rotationTransformGroup = new TransformGroup();
    protected Transform3D thetaTransform = new Transform3D();
    protected Transform3D phiTransform = new Transform3D();
    protected Transform3D qubitTransform = new Transform3D();
    protected Transform3D tmpTrans = new Transform3D();
    // Used to do quick rotations around the different axis.
    protected Transform3D xAxisTrans = new Transform3D();
    protected Transform3D yAxisTrans = new Transform3D();
    protected Transform3D zAxisTrans = new Transform3D();
    protected float initialXAngle = 0;
    protected float initialYAngle = 0;
    protected float initialZAngle = 0;
    protected Vector3f tmpVector = new Vector3f();
    protected Vector3f referenceVector = new Vector3f(xAxis);
    protected Vector3f currentVector = new Vector3f();
    protected Appearance ap = new Appearance();
    // Used to determine the "length" of the qubit visually.
    protected float magnitude = 0.8f;
    // The theta angle of the qubit.
    protected float theta = 0;
    // The phi angle of the qubit.
    protected float phi = 0;
    protected boolean showXYProjection = false;
    // These three determine the thickness of the
    // qubit and then the thickness of the arrow
    // and the length of the arrow.
    protected float qubitVectorRadius = 0;
    protected float arrowRadius = 0;
    protected float arrowHeight = 0;
    protected Appearance projAppearance = new Appearance();
    protected BranchGroup projectionBG;
    protected BranchGroup traceBG;
    protected BranchGroup qbitModel;

    /**
     * The constructor for the 3D Model of the qubit.
     * 
     * @param th
     *            the initial theta angle.
     * @param phi
     *            the initial phi angle.
     * @param color
     *            the color of the qubit.
     * @param qubitsGroup
     *            the parent branchGroup that all 3D object should be placed on.
     */
    public Qubit3DModel(float th, float phi, Color3f color,
	    BranchGroup qubitsGroup) {
	this.theta = th;
	this.phi = phi;
	makeAppearance(color);
	parentBranchGroup = qubitsGroup;
	qubitVectorRadius = magnitude / axisRadiusCoef;
	arrowRadius = magnitude / arrowRadiusCoef;
	arrowHeight = magnitude / arrowHeightCoef;
	rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	rootBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	rootBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
	parentBranchGroup.addChild(rootBranchGroup);
	rotationTransformGroup
		.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	rotationTransformGroup
		.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	projAppearance.setColoringAttributes(new ColoringAttributes(
		Bloch3D.GREEN, ColoringAttributes.NICEST));
	qbitModel = build3DQbit();
	rootBranchGroup.addChild(qbitModel);
	setThetaPhi(th, phi);
    }

    /**
     * Sets the magnitude of the qubit. The standard length is set in
     * <code>IBlochConstants.QUBIT_MAG</code>
     * 
     * @param mag
     */
    public void setMagnitude(float mag) {
	this.magnitude = mag;
	qubitVectorRadius = mag / axisRadiusCoef;
	arrowRadius = mag / arrowRadiusCoef;
	arrowHeight = mag / arrowHeightCoef;
	rootBranchGroup.removeAllChildren();
	qbitModel.removeAllChildren();
	qbitModel = build3DQbit();
	rootBranchGroup.addChild(qbitModel);
    }

    /**
     * Removes the current Qubit and any children from the current displaying
     * parent.
     * 
     */
    public void removeComponents() {
	parentBranchGroup.removeChild(rootBranchGroup);
	rootBranchGroup = null;
    }

    /**
     * Creates the look and lighting of the qubit.
     * 
     * @param color
     *            the color of the qubit arrow.
     */
    protected void makeAppearance(Color3f color) {
	Color3f ambientColor = new Color3f(color);
	ambientColor.scale(ambientScaleFactor);
	Material mat = new Material(ambientColor, Bloch3D.BLACK, color,
		Bloch3D.WHITE, 30);
	mat.setLightingEnable(true);
	ap.setMaterial(mat);
    }

    /**
     * This builds out the components that make the visual part of the qubit.
     * 
     * @return
     */
    protected BranchGroup build3DQbit() {
	Cylinder cylinder = new Cylinder(qubitVectorRadius, magnitude
		- arrowHeight / 2, Cylinder.GENERATE_NORMALS, ap);
	tmpVector.set(0f, (float) (magnitude / 2.0f), 0f);
	tmpTrans.set(tmpVector);
	// move the segment so it from 0 to +mag on the Y axis
	TransformGroup cyltg = new TransformGroup();
	cyltg.setTransform(tmpTrans);
	cyltg.addChild(cylinder);
	// Add the arrow ahead, a cone
	Cone arrowHead = new Cone(arrowRadius, arrowHeight,
		Cone.GENERATE_NORMALS, ap);
	tmpVector.set(0.0f, magnitude, 0.0f);
	tmpTrans.set(tmpVector);
	// move the arrow head to the end of the body of the arrow
	TransformGroup transHead = new TransformGroup(tmpTrans);
	transHead.addChild(arrowHead);
	// initialize this to the xAxis
	tmpTrans.rotZ(-Math.PI / 2.0);
	initGroup = new TransformGroup(tmpTrans);
	initGroup.addChild(cyltg);
	initGroup.addChild(transHead);
	rotationTransformGroup.removeAllChildren();
	rotationTransformGroup.addChild(initGroup);
	BranchGroup tmp = new BranchGroup();
	tmp.setCapability(BranchGroup.ALLOW_DETACH);
	tmp.addChild(rotationTransformGroup);
	return tmp;
    }

    /**
     * Sets the theta angle of the qubit.
     * 
     * @param new_th
     *            the new theta angle (in degrees).
     */
    public void setThetaAngle(float new_th) {
	theta = new_th;
	thetaTransform.rotY(Math.toRadians(new_th - 90));
	qubitTransform.mul(phiTransform, thetaTransform);
	transformQbit(qubitTransform);
    }

    /**
     * Sets the phi angle of the qubit.
     * 
     * @param new_phi
     *            the new phi angle (in degrees).
     */
    public void setPhiAngle(float new_phi) {
	phi = new_phi;
	phiTransform.rotZ(Math.toRadians(new_phi));
	qubitTransform.mul(phiTransform, thetaTransform);
	transformQbit(qubitTransform);
    }

    /**
     * Sets both the theta and the phi angle.
     * 
     * @param newTh
     *            the new theta angle (in degrees).
     * @param newPhi
     *            the new phi angle (in degrees).
     */
    public void setThetaPhi(float newTh, float newPhi) {
	theta = newTh;
	phi = newPhi;
	thetaTransform.rotY(Math.toRadians(newTh - 90));
	phiTransform.rotZ(Math.toRadians(newPhi));
	qubitTransform.mul(phiTransform, thetaTransform);
	transformQbit(qubitTransform);
    }

    /**
     * Sets the inital angle for the X axis. This is used for the
     * doXAxisRotation() function. All rotations will start from this angle.
     * 
     * @param angle
     *            an angle between 0 and 360 degrees.
     */
    public void setInitalXAngle(float angle) {
	initialXAngle = angle;
    }

    /**
     * Sets the inital angle for the Y axis. This is used for the
     * doYAxisRotation() function. All rotations will start from this angle.
     * 
     * @param angle
     *            an angle between 0 and 360 degrees.
     */
    public void setInitalYAngle(float angle) {
	initialYAngle = angle;
    }

    /**
     * Sets the inital angle for the Z axis. This is used for the
     * doZAxisRotation() function. All rotations will start from this angle.
     * 
     * @param angle
     *            an angle between 0 and 360 degrees.
     */
    public void setInitalZAngle(float angle) {
	initialZAngle = angle;
    }

    /**
     * Sets the rotations back to their initial angled that are set through the
     * setIntial{X,Z,Y}Angle() functions
     * 
     */
    public void resetAxisRotations() {
	doXAxisRotation(0);
	doYAxisRotation(0);
	doZAxisRotation(0);
	finishRotation();
    }

    /**
     * Performs a rotaton of <code>degreeRot</code> around the x-axis.
     * 
     * This is nice for demonstrations and doing a short cut rather than
     * calculating the theta and phi for rotations
     * 
     * We use this to show some of the Qbit examples.
     * 
     * This is a partial rotation, meaning that this rotation will not be
     * displayed until the transformQBit() is called to actual perform the
     * rotation. This allows us to do a compound rotation someX, then someY and
     * maybe a little Z rotation, then view.
     * 
     * @param degreeRot
     *            degree of rotation [0 - 360] This is described in degrees
     *            rather than radians.
     */
    public void doXAxisRotation(float degreeRot) {
	xAxisTrans.rotX(Math.toRadians(initialXAngle + degreeRot));
    }

    /**
     * Performs a rotaton of <code>degreeRot</code> around the y-axis.
     * 
     * This is nice for demonstrations and doing a short cut rather than
     * calculating the theta and phi for rotations
     * 
     * We use this to show some of the Qbit examples.
     * 
     * This is a partial rotation, meaning that this rotation will not be
     * displayed until the transformQBit() is called to actual perform the
     * rotation. This allows us to do a compound rotation someX, then someY and
     * maybe a little Z rotation, then view.
     * 
     * @param degreeRot
     *            degree of rotation [0 - 360] This is described in degrees
     *            rather than radians.
     */
    public void doYAxisRotation(float degreeRot) {
	yAxisTrans.rotY(Math.toRadians(initialYAngle + degreeRot));
    }

    /**
     * Performs a rotaton of <code>degreeRot</code> around the z-axis.
     * 
     * This is nice for demonstrations and doing a short cut rather than
     * calculating the theta and phi for rotations
     * 
     * We use this to show some of the Qbit examples.
     * 
     * This is a partial rotation, meaning that this rotation will not be
     * displayed until the transformQBit() is called to actual perform the
     * rotation. This allows us to do a compound rotation someX, then someY and
     * maybe a little Z rotation, then view.
     * 
     * @param degreeRot
     *            degree of rotation [0 - 360] This is described in degrees
     *            rather than radians.
     */
    public void doZAxisRotation(float degreeRot) {
	zAxisTrans.rotZ(Math.toRadians(initialZAngle + degreeRot));
    }

    /**
     * This will apply the three Axis rotations and then transform the QBit to
     * that rotation.
     * 
     */
    public void finishRotation() {
	qubitTransform.setIdentity();
	qubitTransform.mul(xAxisTrans);
	qubitTransform.mul(yAxisTrans);
	qubitTransform.mul(zAxisTrans);
	transformQbit(qubitTransform);
    }

    /**
     * Sets if the projection of the qubit onto the X-Y plane should be visible
     * or not.
     * 
     * @param newproj
     *            if true, then the projection to the X-Y plane will be visible.
     *            If false then the projection vector will be hidden.
     */
    public void setProjection(boolean newproj) {
	this.showXYProjection = newproj;
	updateProjection();
    }

    /**
     * Refreshes the projection of the vector onto the X-Y plane by removing the
     * old projection and then creating a new one based on the current theta and
     * phi angles.
     */
    public void updateProjection() {
	if (projectionBG != null)
	    rootBranchGroup.removeChild(projectionBG);
	if (showXYProjection) {
	    projectionBG = createProj(magnitude, theta, phi);
	    rootBranchGroup.addChild(projectionBG);
	}
    }

    public void transformQbit(Transform3D qubitRotation) {
	rotationTransformGroup.setTransform(qubitRotation);
	qubitRotation.transform(referenceVector, currentVector);
	if (rootBranchGroup == null)
	    return;
	updateProjection();
    }

    /**
     * Allows the users to set the coords of the Qubit3DModel.
     * 
     * @param x
     *            the x magitude of the qubit.
     * @param y
     *            the y magitude of the qubit.
     * @param z
     *            the z magitude of the qubit.
     */
    public void setQubitCoords(double x, double y, double z) {
	currentVector = new Vector3f(new Double(x).floatValue(), new Double(y)
		.floatValue(), new Double(z).floatValue());
	// Apply the identity matrix to update the values.
	transformQbit(new Transform3D());
    }

    public BranchGroup createProj(float mag, float th, float phi) {
	BranchGroup projBranch = new BranchGroup();
	projBranch.setCapability(BranchGroup.ALLOW_DETACH);
	float xyProjMag = (float) Math.abs(mag * Math.sin(Math.toRadians(th)));
	float zProjMag = (float) (mag * Math.cos(Math.toRadians(th)));
	float axisRadius = mag / 300;
	// make the xy proj
	Cylinder xyCyl = new Cylinder(axisRadius, xyProjMag, projAppearance);
	tmpVector.set(0f, (float) (xyProjMag / 2.0f), 0f);
	tmpTrans.set(tmpVector);
	TransformGroup xyCylTG = new TransformGroup(tmpTrans);
	xyCylTG.addChild(xyCyl);
	// the xy proj point in Y direction
	// -90 will take it back to X then add phi
	tmpTrans.rotZ(Math.toRadians(-90 + phi));
	TransformGroup xyProjTG = new TransformGroup(tmpTrans);
	xyProjTG.addChild(xyCylTG);
	// we now have the proj onto plane XY
	projBranch.addChild(xyProjTG);
	// *************************************
	// make the proj onto z axis
	Cylinder zCyl = new Cylinder(axisRadius, zProjMag, projAppearance);
	tmpVector.set(0f, (float) (zProjMag / 2f), 0f);
	tmpTrans.set(tmpVector);
	TransformGroup zCylTG = new TransformGroup(tmpTrans);
	zCylTG.addChild(zCyl);
	// now projz point into the direction of y. need to re-orient
	// rotate and set along z axis
	tmpTrans.rotX(Math.PI / 2.0);
	TransformGroup zRotXTG = new TransformGroup(tmpTrans);
	zRotXTG.addChild(zCylTG);
	float xProjxyProj = (float) (xyProjMag * Math.cos(Math.toRadians(phi)));
	float yProjxyProj = (float) (xyProjMag * Math.sin(Math.toRadians(phi)));
	tmpVector.set(xProjxyProj, yProjxyProj, 0f);
	tmpTrans.set(tmpVector);
	TransformGroup zProjTG = new TransformGroup(tmpTrans);
	zProjTG.addChild(zRotXTG);
	projBranch.addChild(zProjTG);
	return projBranch;
    }

    /**
     * Sets the qubit to visible or not visible.
     * 
     * @param visible
     *            if true, then this will add the qubit and set it visible. if
     *            false, then the qubit will remove all visible components.
     */
    public void setVisible(boolean visible) {
	if (visible && rootBranchGroup.numChildren() == 0)
	    rootBranchGroup.addChild(qbitModel);
	else if (!visible)
	    rootBranchGroup.removeAllChildren();
    }
}
