package edu.uc.ece.blochSphere;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FileSaveLoad {

    protected Bloch3D m_blochInstance = null;
    protected JFileChooser m_fileChooser = null;

    public FileSaveLoad(Bloch3D bloch) {
	m_blochInstance = bloch;
	m_fileChooser = new JFileChooser();
	BlochSphereFileFilter filter = new BlochSphereFileFilter();
	m_fileChooser.setFileFilter(filter);
	m_fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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

    public boolean saveBloch(Component parentComponent) {
	int returnVal = m_fileChooser.showOpenDialog(parentComponent);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    if (saveToFile(m_fileChooser.getSelectedFile())) {
		JOptionPane.showMessageDialog(parentComponent, "File Saved");
		return true;
	    } else
		return false;
	} else
	    return false;
    }

    public boolean loadFromFile(File file) {
	if (!file.exists())
	    return false;
	Properties blochProperties = new Properties();
	try {
	    blochProperties.load(new FileReader(file));
	    for (int i = 0; i < m_blochInstance.getVisibleQubits().size(); i++) {
		String phi = blochProperties.getProperty("qbit." + i + ".phi");
		String theta = blochProperties.getProperty("qbit." + i
			+ ".theta");
		String visible = blochProperties.getProperty("qbit." + i
			+ ".visible");
		if (visible != null)
		    m_blochInstance.getVisibleQubits().get(i).setVisible(
			    visible.equals("true"));
		if (phi != null)
		    m_blochInstance.getVisibleQubits().get(i).setTh(
			    Double.parseDouble(phi));
		if (theta != null)
		    m_blochInstance.getVisibleQubits().get(i).setTh(
			    Double.parseDouble(theta));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public boolean saveToFile(File file) {
	Properties blochProperties = new Properties();
	for (int i = 0; i < m_blochInstance.getVisibleQubits().size(); i++) {
	    blochProperties.setProperty("qbit." + i + ".phi", new Double(
		    m_blochInstance.getVisibleQubits().get(i).getPhi())
		    .toString());
	    blochProperties.setProperty("qbit." + i + ".theta", new Double(
		    m_blochInstance.getVisibleQubits().get(i).getTh())
		    .toString());
	    blochProperties
		    .setProperty("qbit." + i + ".visible", m_blochInstance
			    .getVisibleQubits().get(i).isVisible() ? "true"
			    : "false");
	}
	try {
	    if (!file.getAbsolutePath().toLowerCase().endsWith(".bss"))
		file = new File(file.getAbsoluteFile() + ".bss");
	    if (!file.exists())
		file.createNewFile();
	    blochProperties.list(new PrintStream(file));
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    class BlochSphereFileFilter extends FileFilter {

	/**
	 * Accepts only files with a name that ends with "bss" (case
	 * insensitive).
	 */
	public boolean accept(File f) {
	    if (f.isDirectory())
		return true;
	    else if (f.getName().toLowerCase().endsWith("bss"))
		return true;
	    else
		return false;
	}

	public String getDescription() {
	    return new String("*.bss Bloch Sphere Simulation Files");
	}
    }
}
