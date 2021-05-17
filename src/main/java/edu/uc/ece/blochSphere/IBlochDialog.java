package edu.uc.ece.blochSphere;

/**
 * A generic dialog interface that allows the common functions of the Dialogs to
 * be used generically.
 * 
 * @author Stephen Shary
 * 
 */
public interface IBlochDialog {

    public static final int DIALOG_WIDTH = 800;
    public static final int DIALOG_HEIGHT = 600;
    public static final int PADDING = 10;

    /**
     * Builds the UI for the dialog. This MUST be done before the showDialog()
     * function is called.
     */
    public void buildDialog();

    /**
     * Displays the current dialog.
     */
    public void showDialog();
}
