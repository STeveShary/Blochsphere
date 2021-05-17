package edu.uc.ece.blochSphere.operator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

public class OperatorCellRenderer extends JPanel implements ListCellRenderer {

    private JLabel leftParen = new JLabel("(");
    private JLabel rightParen = new JLabel(")");

    private JPanel operatorGridPanel = new JPanel();

    private JLabel topLeftElement = new JLabel("  ");
    private JLabel topRightElement = new JLabel("  ");
    private JLabel bottomLeftElement = new JLabel("  ");
    private JLabel bottomRightElement = new JLabel("  ");

    public OperatorCellRenderer() {
	buildPanel();
    }

    public Component getListCellRendererComponent(JList list, Object value,
	    int index, boolean isSelected, boolean hasFocus) {

	if (isSelected) {
	    setBackground(list.getSelectionBackground());
	    setForeground(list.getSelectionForeground());
	} else {
	    setBackground(list.getBackground());
	    setForeground(list.getForeground());
	}

	if (value instanceof Operator) {

	    Operator operator = (Operator) value;
	    topLeftElement.setText(operator.getTopLeftElement().toString());
	    topRightElement.setText(operator.getTopRightElement().toString());
	    bottomLeftElement.setText(operator.getBottomLeftElement()
		    .toString());
	    bottomRightElement.setText(operator.getBottomRightElement()
		    .toString());
	}
	return this;
    }

    /**
     * Layouts out the panel by setting the different parts of the operator on
     * the JPanel.
     */
    private void buildPanel() {
	Font font = new Font("Courier", Font.BOLD, 30);
	leftParen.setFont(font);
	rightParen.setFont(font);
	topLeftElement.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	topLeftElement.setHorizontalAlignment(SwingConstants.CENTER);
	topRightElement.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	topRightElement.setHorizontalAlignment(SwingConstants.CENTER);
	bottomLeftElement
		.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	bottomLeftElement.setHorizontalAlignment(SwingConstants.CENTER);
	bottomRightElement.setBorder(BorderFactory
		.createEmptyBorder(2, 2, 2, 2));
	bottomRightElement.setHorizontalAlignment(SwingConstants.CENTER);
	add(leftParen, BorderLayout.WEST);

	operatorGridPanel.setOpaque(false);
	operatorGridPanel.setLayout(new GridLayout(2, 2));
	operatorGridPanel.add(topRightElement);
	operatorGridPanel.add(topLeftElement);
	operatorGridPanel.add(bottomRightElement);
	operatorGridPanel.add(bottomLeftElement);

	add(operatorGridPanel, BorderLayout.CENTER);

	add(rightParen, BorderLayout.EAST);
    }
}
