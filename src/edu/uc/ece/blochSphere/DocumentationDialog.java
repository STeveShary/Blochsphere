package edu.uc.ece.blochSphere;

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
import javax.swing.SpringLayout;

/**
 * This is the generic Dialog that will allow you to create a blank dialog with
 * a scrollable window that you can point to your html documentation to.
 * 
 * @author Stephen Shary
 * 
 */
public class DocumentationDialog implements IBlochDialog {

    protected JDialog m_dialog = null;
    protected JLabel m_titleLabel = null;
    protected JButton m_closeButton = null;
    protected JEditorPane m_documentationPane = null;
    protected String m_titleString = null;
    protected String m_documentString = null;

    public DocumentationDialog(Frame parentFrame) {
	m_dialog = new JDialog(parentFrame, true);
	m_dialog.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
	m_dialog.setResizable(false);
    }

    public DocumentationDialog(Frame parentFrame, String titleString,
	    String documentString) {
	this(parentFrame);
	setTitle(titleString);
	setDocumentationPage(documentString);
    }

    /**
     * Sets the title of the documentation.
     * 
     */
    public void setTitle(String title) {
	m_titleString = title;
	if (m_titleLabel != null)
	    m_titleLabel.setText(title);
    }

    /**
     * Sets the page that the documentation should be loaded from. The
     * documentation will be at <code>DOCS_LOCATION</code> location
     * 
     * @param documentationPage
     *            the string of the documentation page.
     */
    public void setDocumentationPage(String documentationPage) {
	m_documentString = documentationPage;
	if (m_documentationPane != null) {
	    try {
		m_documentationPane.setPage(DocumentationDialog.class
			.getResource("docs/" + documentationPage));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Initializes all of the components in the Dialog.
     * 
     */
    public void buildDialog() {
	Container contentPane = m_dialog.getContentPane();
	JPanel basePanel = new JPanel();
	SpringLayout layout = new SpringLayout();
	basePanel.setLayout(layout);
	basePanel.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
	// Add title label at the top.
	m_titleLabel = new JLabel();
	m_titleLabel.setText("Abstract Example");
	m_titleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
	if (m_titleString != null)
	    m_titleLabel.setText(m_titleString);
	layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, m_titleLabel, 0,
		SpringLayout.HORIZONTAL_CENTER, basePanel);
	layout.putConstraint(SpringLayout.NORTH, m_titleLabel, 10,
		SpringLayout.NORTH, basePanel);
	// Add close button at bottom right
	m_closeButton = new JButton();
	m_closeButton.setText("Close");
	m_closeButton.setSize(100, 40);
	layout.putConstraint(SpringLayout.EAST, m_closeButton, -10,
		SpringLayout.EAST, basePanel);
	layout.putConstraint(SpringLayout.SOUTH, m_closeButton, -10,
		SpringLayout.SOUTH, basePanel);
	m_closeButton.addActionListener(new CloseButtonListener());
	// Add panel for Demonstration Text.
	m_documentationPane = new JEditorPane();
	m_documentationPane.setEditable(false);
	m_documentationPane.setDoubleBuffered(true);
	if (m_documentString != null)
	    setDocumentationPage(m_documentString);
	JScrollPane scrollPane = new JScrollPane(m_documentationPane);
	scrollPane.setBackground(Color.gray);
	scrollPane
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	scrollPane
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	layout.putConstraint(SpringLayout.NORTH, scrollPane, 10,
		SpringLayout.SOUTH, m_titleLabel);
	layout.putConstraint(SpringLayout.SOUTH, scrollPane, -10,
		SpringLayout.NORTH, m_closeButton);
	layout.putConstraint(SpringLayout.EAST, scrollPane, -10,
		SpringLayout.EAST, basePanel);
	layout.putConstraint(SpringLayout.WEST, scrollPane, 10,
		SpringLayout.WEST, basePanel);
	basePanel.add(m_titleLabel);
	basePanel.add(m_closeButton);
	basePanel.add(scrollPane);
	contentPane.add(basePanel);
    }

    /**
     * Displays the current dialog.
     */
    public void showDialog() {
	m_dialog.setVisible(true);
    }

    class CloseButtonListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    // This will end the animation as well.
	    m_dialog.dispose();
	}
    }
}
