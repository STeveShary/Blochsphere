package edu.uc.ece.blochSphere;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * Created to define the constants used in the main class.
 * 
 * @author Stephen Shary
 * 
 */
public interface IBlochConstants {

    public static final String AUTHOR = "Quantum Bloch Sphere. Developed by Nick Vatamaniuc (University Of Cincinnati, 2004), David Kesler and Brian Jauch (University of Cincinnati, 2007)";

    public static final Color3f BLACK = new Color3f(0, 0, 0);
    public static final Color3f GREY = new Color3f(0.3f, 0.3f, 0.3f);
    public static final Color3f WHITE = new Color3f(1, 1, 1);
    public static final Color3f RED = new Color3f(1, 0, 0);
    public static final Color3f LIGHT_RED = new Color3f(.3f, 0, 0);
    public static final Color3f GREEN = new Color3f(0, 1, 0);
    public static final Color3f LIGHT_GREEN = new Color3f(.8f, .99f, .8f);
    public static final Color3f YELLOW = new Color3f(.8f, .8f, 0f);
    public static final Color3f ROSE = new Color3f(1f, .8f, .75f);
    public static final Color3f BLUE = new Color3f(0f, 0f, 1f);
    public static final Color3f LIGHT_BLUE = new Color3f(.5f, .5f, .8f);
    public static final Color3f MAGENTA = new Color3f(1f, 0f, 1f);
    public static final Color3f CYAN = new Color3f(0f, 1f, 1f);
    public static final Color3f ORANGE = new Color3f(1f, .6f, 0f);
    public static final BoundingSphere bounds = new BoundingSphere(new Point3d(
	    0d, 0d, 0d), 100d);
    public static final Color3f background_color = BLACK;
    public static final Color3f xyplane_color = WHITE;
    public static final Color3f sphere_color = WHITE, axes_color = BLACK;
    public static final Color3f label_color = WHITE;
    public static final Color3f meridian_color = WHITE;
    /*
     * The list of colors that are used for each of the qubits.
     */
    public static Color3f[] qbit_color_list = { RED, CYAN, MAGENTA, ORANGE,
	    LIGHT_BLUE, ROSE, GREEN, LIGHT_GREEN, YELLOW, LIGHT_RED };
    public final float AMBIENT_SCALE_FACTOR = 0.8f;
    /*
     * Defines the maximum number of Qubits that can be displayed.
     */
    public static final int NUM_QBITS = 10;
    /*
     * Defines the magnitude of the bloch sphere and the length of the qubits
     * that are contained in them.
     */
    public static final float QUBIT_MAG = 0.8f;
}
