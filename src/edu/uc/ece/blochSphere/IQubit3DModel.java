package edu.uc.ece.blochSphere;

import javax.vecmath.Vector3f;

public interface IQubit3DModel {

    public final float ambientScaleFactor = 0.5f;
    public final float axisRadiusCoef = 100;
    public final float arrowRadiusCoef = 30;
    public final float arrowHeightCoef = 10;
    public final float fontScale = 0.07f;
    public final Vector3f xAxis = new Vector3f(1, 0, 0);
    public final Vector3f yAxis = new Vector3f(0, 1, 0);
    public final Vector3f zAxis = new Vector3f(0, 0, 1);
}
