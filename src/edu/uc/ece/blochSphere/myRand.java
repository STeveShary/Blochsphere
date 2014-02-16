package edu.uc.ece.blochSphere;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class myRand {

    public static void main(String[] args) {
	Random r = new Random();
	double x;
	double y;
	double z;
	// double m;
	BufferedWriter out = null;
	;
	try {
	    out = new BufferedWriter(new FileWriter("rand.txt"));
	} catch (IOException e) {
	    System.out.println("instantiation");
	}
	for (int i = 0; i < 2000; ++i) {
	    x = r.nextDouble();
	    y = r.nextDouble();
	    z = r.nextDouble();
	    // m = Math.sqrt(x * x + y * y + z * z);
	    // x /= m;
	    // y /= m;
	    // z /= m;
	    try {
		out.write(x + " " + y + " " + z);
		out.newLine();
	    } catch (IOException e) {
		System.out.println("writing");
	    }
	}
	try {
	    out.close();
	} catch (IOException e) {
	    System.out.println("closing");
	}
    }

    public myRand() {
    }
}