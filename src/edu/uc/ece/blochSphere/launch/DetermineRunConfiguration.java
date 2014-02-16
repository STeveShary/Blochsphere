package edu.uc.ece.blochSphere.launch;

public class DetermineRunConfiguration {

    /**
     * @param args
     */
    public static void main(String[] args) {
	String operatingSystem = System.getProperties().getProperty("os.name");
	String architecture = System.getProperties().getProperty(
		"os.arch.data.model");
	System.out.println(operatingSystem + architecture);

    }
}
