package edu.uc.ece.blochSphere.exampleDialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.uc.ece.blochSphere.Bloch3DCanvas;
import edu.uc.ece.blochSphere.DocumentationDialog;
import edu.uc.ece.blochSphere.IBlochDialog;
import edu.uc.ece.blochSphere.Qubit3DModel;

/**
 * A base class that was created that shows the Bloch Sphere on the left and has
 * a rich text window on the right. The Examples will have an animation in the
 * Bloch sphere and must implement the {@link #run()} method.
 * 
 * @author Stephen Shary
 * 
 */
public abstract class ExampleDialog implements ActionListener, Runnable,
	IBlochDialog {

    protected JDialog dialog = null;
    protected JLabel titleLabel = null;
    protected JButton closeButton = null;
    protected Bloch3DCanvas blochCanvas = null;
    protected JEditorPane exampleEditorPane = null;
    protected Qubit3DModel qubitModel = null;

    /**
     * This was made private so all other child classes must use the constructor
     * below that defines the parent frame.
     */
    private ExampleDialog() {
    }

    public ExampleDialog(Frame parentFrame) {
	dialog = new JDialog(parentFrame, true);
	dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
	dialog.setResizable(false);
    }

    /**
     * Initializes all of the components in the Dialog.
     * 
     */
    public final void buildDialog() {
	Container contentPane = dialog.getContentPane();
	JPanel basePanel = new JPanel();
	basePanel.setLayout(null);
	basePanel.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
	// Add title label at the top.
	titleLabel = new JLabel();
	titleLabel.setBounds(PADDING, PADDING, 200, 50);
	titleLabel.setText("Abstract Example");
	titleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
	titleLabel.setText(getDialogTitle());
	// Add close button at bottom right
	closeButton = new JButton();
	closeButton.setText("Close");
	int buttonWidth = 100;
	int buttonHeight = 40;
	closeButton.setBounds(DIALOG_WIDTH - (PADDING * 2) - buttonWidth,
		DIALOG_HEIGHT - (PADDING * 4) - buttonHeight, buttonWidth,
		buttonHeight);
	closeButton.addActionListener(this);
	// Add panel for Demonstration Text.
	exampleEditorPane = new JEditorPane();
	exampleEditorPane.setEditable(false);
	setPage(getHtmlHelpPage());
	JScrollPane scrollPane = new JScrollPane(exampleEditorPane);
	scrollPane.setBackground(Color.gray);
	int exampleWidth = 380;
	int exampleHeight = closeButton.getY() - (PADDING * 2);
	scrollPane.setBounds(400, closeButton.getY() - PADDING - exampleHeight,
		exampleWidth, exampleHeight);
	scrollPane
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scrollPane
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	// Add the BlochSphere
	blochCanvas = new Bloch3DCanvas();
	blochCanvas.buildCanvas();
	blochCanvas.getCanvas().setBounds(PADDING,
		(PADDING * 2) + titleLabel.getHeight(), 380, 440);
	// Add the QBit. We don't set it to visible, but rely on the animation
	// to do that when it is ready.
	qubitModel = new Qubit3DModel(45, 0, Bloch3DCanvas.RED, blochCanvas
		.getQubitsGroup());
	qubitModel.setInitalXAngle(0);
	qubitModel.setInitalYAngle(0);
	qubitModel.setInitalZAngle(45);
	basePanel.add(titleLabel);
	basePanel.add(closeButton);
	basePanel.add(scrollPane);
	basePanel.add(blochCanvas.getCanvas());
	contentPane.add(basePanel);
    }

    /**
     * Displays the dialog and starts the animation. The animation code should
     * be implmented in the public void run() function.
     */
    public final void showDialog() {
	// Start the animation. We do this first because the setVisible()
	// function is blocking.
	new Thread(this).start();
	dialog.setVisible(true);
    }

    /**
     * This function should be implemented to show the animation of the example.
     * It should continue and stop when m_dialog.isVisible() is false.
     */
    public abstract void run();

    /**
     * This function will return the title of the help dialog that should be
     * displayed
     * 
     * @return The title of the help dialog.
     */
    public abstract String getDialogTitle();

    /**
     * Defines the html page that should be used in the display of the help
     * dialog. All dialogs should have the html page displayed in the
     * edu/uc/ece/blochSphere/docs folder of the bloch3d.jar
     * 
     * @return The name of the HTML file (including the file extension). The
     *         folder is assumed.
     */
    public abstract String getHtmlHelpPage();

    /**
     * Handles action events from the close button on the dialog that will close
     * the dialog.
     */
    public void actionPerformed(ActionEvent e) {
	// This will end the animation as well.
	dialog.dispose();
    }

    /**
     * Sets the html documentation from in the edu/uc/ece/blochSphere/Docs in
     * the bloch.jar file.
     * 
     * All images that are referenced relatively should be placed in the
     * edu.uc.ece.blochSphere.docs.images package.
     * 
     * @param htmlPageName
     *            the name of the html file.
     * 
     */
    public void setPage(String htmlPageName) {
	try {
	    exampleEditorPane.setPage(DocumentationDialog.class
		    .getResource("docs/" + htmlPageName));
	} catch (IOException e) {
	    e.printStackTrace();
	    return;
	}
    }
}
