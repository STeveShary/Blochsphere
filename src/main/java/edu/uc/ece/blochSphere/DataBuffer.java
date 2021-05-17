package edu.uc.ece.blochSphere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

@SuppressWarnings("unchecked")
public class DataBuffer implements Runnable {

    Semaphore buffering;
    LinkedList<Data>[] dataList = new LinkedList[11];
    String matrixFile;
    String[] qubitFile = new String[10];
    int qubitFiles;
    boolean finishedBuffering;

    public DataBuffer(String fileName) {
	finishedBuffering = false;
	for (int i = 0; i < 11; ++i) {
	    dataList[i] = new LinkedList<Data>();
	}
	BufferedReader in;
	qubitFiles = 0;
	buffering = new Semaphore(1, true);
	if (fileName.indexOf(".txt") == -1) {
	    // determine stem name and generate file names
	    File file = new File(fileName);
	    String stem;
	    stem = file.getName();
	    matrixFile = file.getAbsolutePath() + "\\" + stem + "M.txt";
	    File tmpFile = new File(matrixFile);
	    if (!tmpFile.exists()) {
		matrixFile = null;
	    }
	    tmpFile = new File(file.getAbsolutePath() + "\\" + stem + "Q"
		    + qubitFiles + ".txt");
	    while (qubitFiles < 10 && tmpFile.exists()) {
		qubitFile[qubitFiles] = tmpFile.getAbsolutePath();
		qubitFiles++;
		tmpFile = new File(file.getAbsolutePath() + "\\" + stem + "Q"
			+ qubitFiles + ".txt");
	    }
	} else {
	    // read file and parse out file names
	    try {
		in = new BufferedReader(new FileReader(fileName));
		String tmpStr;
		tmpStr = in.readLine();
		matrixFile = tmpStr;
		if (matrixFile.equals(""))
		    matrixFile = null;
		tmpStr = in.readLine();
		while (qubitFiles <= 10 && tmpStr != null) {
		    qubitFile[qubitFiles] = tmpStr;
		    qubitFiles++;
		    tmpStr = in.readLine();
		}
		in.close();
	    } catch (IOException e) {
		System.out.println("File " + fileName + " does not exist");
	    }
	}
	DisplayFileNames();
    }

    public void run() {
	finishedBuffering = false;
	String tmpStr;
	String tokens[];
	double x, y, z;
	// set up inputs
	try {
	    BufferedReader in[] = new BufferedReader[qubitFiles + 1];
	    for (int i = 0; i < qubitFiles; ++i) {
		in[i] = new BufferedReader(new FileReader(qubitFile[i]));
	    }
	    int numFiles = qubitFiles;
	    if (matrixFile != null) {
		in[qubitFiles] = new BufferedReader(new FileReader(matrixFile));
	    } else {
		numFiles -= 1;
	    }
	    boolean outOfData = false;
	    while (!outOfData) {
		buffering.acquireUninterruptibly();
		// read in data
		for (int i = 0; i <= numFiles; ++i) {
		    tmpStr = in[i].readLine();
		    if (tmpStr == null) {
			outOfData = true;
		    } else {
			tmpStr = tmpStr.replace(",", " ");
			tokens = tmpStr.split("\\s");
			if (tokens.length == 3) {
			    try {
				x = Double.parseDouble(tokens[0]);
				y = Double.parseDouble(tokens[1]);
				z = Double.parseDouble(tokens[2]);
				dataList[i].add(new Data(x, y, z));
			    } catch (NumberFormatException e) {
				System.out.println("data buffer");
			    }
			}
		    }
		}
		buffering.release();
	    }
	    for (int i = 0; i <= numFiles; ++i) {
		in[i].close();
	    }
	} catch (IOException e) {
	    System.out.println("Data Buffer");
	    buffering.release();
	}
	finishedBuffering = true;
    }

    public void startCriticalRegion() {
	buffering.acquireUninterruptibly();
    }

    public void endCriticalRegion() {
	buffering.release();
    }

    public void DisplayFileNames() {
	System.out.println(matrixFile);
	for (int i = 0; i < qubitFiles; ++i) {
	    System.out.println(qubitFile[i]);
	}
    }

    public Data getData(int qubit) {
	if (qubit <= qubitFiles) {
	    if (!(matrixFile == null && qubit == qubitFiles)
		    && !dataList[qubit].isEmpty()) {
		return (Data) dataList[qubit].remove(0);
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
    }

    public int getNumQubits() {
	return qubitFiles;
    }

    public boolean dataAvailable() {
	boolean dataAvailable = true;
	for (int i = 0; i < qubitFiles + 1; ++i) {
	    if (dataList[i].size() == 0) {
		dataAvailable = false;
	    }
	}
	return dataAvailable;
    }

    public boolean qubitDataAvailable() {
	boolean dataAvailable = true;
	for (int i = 0; i < qubitFiles; ++i) {
	    if (dataList[i].size() == 0) {
		dataAvailable = false;
	    }
	}
	return dataAvailable;
    }

    public String getFile(int i) {
	if (i < 10 && i >= 0) {
	    return qubitFile[i];
	} else if (i == 10) {
	    return matrixFile;
	} else {
	    return null;
	}
    }

    public int getNumFiles() {
	return qubitFiles;
    }

    public boolean isFinishedBuffering() {
	return finishedBuffering;
    }
}
