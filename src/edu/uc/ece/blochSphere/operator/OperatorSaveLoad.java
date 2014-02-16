package edu.uc.ece.blochSphere.operator;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.uc.ece.blochSphere.ComplexNumber;
import edu.uc.ece.blochSphere.Qubit;

public class OperatorSaveLoad {

    protected Vector<Qubit> qubitVector = null;
    protected OperatorPlayback operatorPlayback = null;
    protected JFileChooser m_fileChooser = null;

    public OperatorSaveLoad(Vector<Qubit> qubitVector,
	    OperatorPlayback operatorPlayback) {
	m_fileChooser = new JFileChooser();
	BlochSphereOperatorFileFilter filter = new BlochSphereOperatorFileFilter();
	m_fileChooser.setFileFilter(filter);
	m_fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	this.qubitVector = qubitVector;
	this.operatorPlayback = operatorPlayback;
    }

    public boolean loadBloch(Component parentComponent) {
	int returnVal = m_fileChooser.showOpenDialog(parentComponent);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    if (loadFromFile(m_fileChooser.getSelectedFile())) {
		JOptionPane.showMessageDialog(parentComponent, "File Loaded");
		return true;
	    } else
		return false;
	} else
	    return false;
    }

    public final static String QUBIT_PREFIX = "qubit.";
    public final static String THETA_SUFFIX = ".theta";
    public final static String PHI_SUFFIX = ".phi";
    public final static String VISIBLE_SUFFIX = ".visible";
    public final static String OPERATOR_PREFIX = "operator.";
    public final static String OPERATOR_SUFFIX = "=";
    public final static String OPERATOR_VALUE_SEPARATOR = ",";
    public final static String FILE_EXTENSION = ".bso";

    private boolean loadFromFile(File file) {
	if (!file.exists())
	    return false;
	Properties blochProperties = new Properties();
	try {
	    blochProperties.load(new FileReader(file));
	    for (int i = 0; i < qubitVector.size(); i++) {
		String phi = blochProperties.getProperty(QUBIT_PREFIX + i
			+ PHI_SUFFIX);
		String theta = blochProperties.getProperty(QUBIT_PREFIX + i
			+ THETA_SUFFIX);
		String visible = blochProperties.getProperty(QUBIT_PREFIX + i
			+ VISIBLE_SUFFIX);
		if (visible != null)
		    qubitVector.get(i).setVisible(visible.equals("true"));
		if (phi != null)
		    qubitVector.get(i).setTh(Double.parseDouble(phi));
		if (theta != null)
		    qubitVector.get(i).setTh(Double.parseDouble(theta));
	    }
	    boolean hasOperator = true;
	    int counter = 0;
	    Vector<Operator> operators = new Vector<Operator>();
	    while (hasOperator) {
		if (blochProperties.getProperty(OPERATOR_PREFIX + counter) != null) {
		    StringTokenizer toker = new StringTokenizer(blochProperties
			    .getProperty(OPERATOR_PREFIX + counter), ",");
		    operators.add(new Operator(new ComplexNumber(toker
			    .nextToken()),
			    new ComplexNumber(toker.nextToken()),
			    new ComplexNumber(toker.nextToken()),
			    new ComplexNumber(toker.nextToken())));
		    counter++;
		} else {
		    hasOperator = false;
		}
	    }
	    operatorPlayback.loadData(operators);
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public boolean saveOperators(Component parentComponent,
	    Vector<Qubit> startingState, Vector<Operator> operators) {
	int returnVal = m_fileChooser.showSaveDialog(parentComponent);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    if (saveToFile(startingState, operators, m_fileChooser
		    .getSelectedFile())) {
		JOptionPane.showMessageDialog(parentComponent, "File Saved");
		return true;
	    } else
		return false;
	} else
	    return false;
    }

    private boolean saveToFile(Vector<Qubit> startingState,
	    Vector<Operator> operators, File file) {
	StringBuffer stringBuf = new StringBuffer();
	if (startingState != null && startingState.size() > 0) {
	    for (int i = 0; i < startingState.size(); i++) {
		stringBuf.append(QUBIT_PREFIX + i + THETA_SUFFIX + "="
			+ startingState.get(i).getTh() + "\n");
		stringBuf.append(QUBIT_PREFIX + i + PHI_SUFFIX + "="
			+ startingState.get(i).getPhi() + "\n");
		stringBuf.append(QUBIT_PREFIX
			+ i
			+ VISIBLE_SUFFIX
			+ "="
			+ new Boolean(startingState.get(i).isVisible())
				.toString() + "\n");
	    }
	}
	for (int i = 0; i < operators.size(); i++) {
	    stringBuf.append(OPERATOR_PREFIX + i + OPERATOR_SUFFIX);
	    stringBuf.append(operators.get(i).getTopLeftElement()
		    + OPERATOR_VALUE_SEPARATOR);
	    stringBuf.append(operators.get(i).getTopRightElement()
		    + OPERATOR_VALUE_SEPARATOR);
	    stringBuf.append(operators.get(i).getBottomLeftElement()
		    + OPERATOR_VALUE_SEPARATOR);
	    stringBuf.append(operators.get(i).getBottomRightElement() + "\n");
	}
	try {
	    if (!file.getAbsolutePath().toLowerCase().endsWith(FILE_EXTENSION))
		file = new File(file.getAbsoluteFile() + FILE_EXTENSION);
	    if (!file.exists())
		file.createNewFile();
	    BufferedWriter out = new BufferedWriter(new FileWriter(file));
	    out.write(stringBuf.toString());
	    out.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    class BlochSphereOperatorFileFilter extends FileFilter {

	/**
	 * Accepts only files with a name that ends with "bss" (case
	 * insensitive).
	 */
	public boolean accept(File f) {
	    if (f.isDirectory())
		return true;
	    else if (f.getName().toLowerCase().endsWith(FILE_EXTENSION))
		return true;
	    else
		return false;
	}

	public String getDescription() {
	    return new String("*" + FILE_EXTENSION
		    + " Bloch Sphere Operator Files");
	}
    }
}
