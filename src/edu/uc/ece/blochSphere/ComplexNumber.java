package edu.uc.ece.blochSphere;

/**
 * The representation of complex numbers. This provides the ability to add,
 * subtract, multiply, and divide by other complex numbers. There is also some
 * robust parsing functionality to import a string and convert it to a Complex
 * Number.
 * 
 * @author Stephen Shary
 * 
 */
public class ComplexNumber implements IComplexNumber {
    private double realPart = 0.0f;
    private double imagPart = 0.0f;

    /**
     * A constructor that will build the Complex Number object with both the
     * real and imaginary parts defined.
     * 
     * @param real
     *            The real part of the complex number. If there is no real part,
     *            then supply 0.0f.
     * @param imaginary
     *            The imaginary part of the complex number. If there is no real
     *            part, then supply 0.0f.
     */
    public ComplexNumber(double real, double imaginary) {
    	realPart = roundNumber(real, 4);
    	imagPart = roundNumber(imaginary, 4);
    }

    /**
     * The constructor that will use the {@link #parseString(String)} function
     * to create a new ComplexNumber object.
     * 
     * @param complexNumber
     *            The string that contains either a real, imaginary or both
     *            part.
     * @throws NumberFormatException
     *             If the String supplied cannot be parsed to a complex String.
     */
    public ComplexNumber(String complexNumber) throws NumberFormatException {
    	ComplexNumber staticComp = parseString(complexNumber);
    	setImaginaryPart(roundNumber(staticComp.getImaginaryPart(), 4));
    	setRealPart(roundNumber(staticComp.getRealPart(), 4));
    }

    /**
     * Allows the real part of the Complex Number to be defined.
     * 
     * @param real
     *            The real part of the complex number. If there is no real part,
     *            then supply 0.0f.
     */
    public void setRealPart(double real) {
        this.realPart = real;
    }

    /**
     * Allows the imaginary part of the complex number to be defined.
     * 
     * @param imaginary
     *            The imaginary part of the complex number.
     */
    public void setImaginaryPart(double imaginary) {
        this.imagPart = imaginary;
    }

    /**
     * Gets the real part of the Complex Number.
     * 
     * @return The real part of the complex number. If there is not defined
     *         value, then the value will be 0.0f.
     */
    public double getRealPart() {
        return realPart;
    }

    /**
     * Gets the imaginary part of the Complex Number.
     * 
     * @return The imaginary part of the complex number. If there is not defined
     *         value, then the value will be 0.0f.
     */
    public double getImaginaryPart() {
        return imagPart;
    }

    /**
     * Returns a new ComplexNumber object where the real part of the number is
     * unaffected and the imaginary part is multiplied by "-1", effectively
     * changing the sign of the imaginary part of the complex number.
     * 
     * @return A new ComplexNumber object where the new object is the complex
     *         conjugate of the current value of this object.
     */
    public ComplexNumber getComplexConjugate() {
    	return new ComplexNumber(this.getRealPart(), 
    				             this.getImaginaryPart() * -1);
    }

    /**
     * Returns a new complex number where the value of this complex number is
     * added to the input parameter.
     * 
     * @param compNum
     *            The complex number that should be added to this number.
     * @return The sum of the two complex numbers. If the input number is null,
     *         then this will return the a new complex number object with the
     *         values of this.
     */
    public ComplexNumber add(ComplexNumber compNum) {
    	if (compNum == null) {
    	    return new ComplexNumber(this.getRealPart(), 
    		    		             this.getImaginaryPart());
    	}
    	return new ComplexNumber(this.getRealPart() + compNum.getRealPart(),
    				             this.getImaginaryPart() + compNum.getImaginaryPart());

    }

    /**
     * Returns a new complex number where the value of this complex number is
     * subtracted by the input parameter.
     * 
     * @param compNum
     *            The complex number that should be subtracted from this number.
     * @return The difference of the two complex numbers. If the input number is
     *         null, then this will return the a new complex number object with
     *         the values of this.
     */
    public ComplexNumber subtract(ComplexNumber compNum) {
    	if (compNum == null) {
    	    return new ComplexNumber(this.getRealPart(), 
    		    		             this.getImaginaryPart());
    	}
    	return new ComplexNumber(this.getRealPart() - compNum.getRealPart(),
    				             this.getImaginaryPart() - compNum.getImaginaryPart());
    }

    /**
     * Returns a new complex number where the value of this complex number
     * multiplied by the input parameter.
     * 
     * @param compNum
     *            The complex number that should be multiplied by this number.
     * @return The product of the two complex numbers. If the input number is
     *         null, then this will return the a new complex number with a value
     *         of zero (for real and imaginary parts).
     */
    public ComplexNumber multiply(ComplexNumber compNum) {
    	if (compNum == null) {
    	    return new ComplexNumber(0.0, 0.0);
    	}
    	return new ComplexNumber((this.getRealPart() * compNum.getRealPart()) - (this.getImaginaryPart() * compNum.getImaginaryPart()), 
    	                         (this.getRealPart() * compNum.getImaginaryPart()) + (this.getImaginaryPart() * compNum.getRealPart()));
    }

    /**
     * Returns a new complex number where the value of this complex number is
     * divided by the input parameter.
     * 
     * @param compNum
     *            The complex number that this number will be divided by.
     * @return The result of dividing this by the input complex number. If the
     *         input number is null, then this will return the a new complex
     *         number with a value of zero (for real and imaginary parts).
     */
    public ComplexNumber dividedBy(ComplexNumber compNum) {
    	if (compNum == null) {
    	    return new ComplexNumber(0.0, 0.0);
    	}
    	ComplexNumber numerator = this.multiply(compNum.getComplexConjugate());
    	ComplexNumber denominator = compNum.multiply(compNum.getComplexConjugate());
    	return new ComplexNumber(numerator.getRealPart() / denominator.getRealPart(), 
    	                         numerator.getImaginaryPart() / denominator.getRealPart());
    }

    /**
     * Determines if the input number is the complex conjugate of this.
     * 
     * @param conjugate
     *            The input number to check if it is the complex conjugate.
     * @return true if the input complex number is non-null and it is the
     *         complex conjugate of the current value. Otherwise, false.
     */
    public boolean isComplexConjugateOf(ComplexNumber conjugate) {
    	if (conjugate == null) {
    	    return false;
    	}
    	return equals(conjugate.getComplexConjugate());
    }

    /**
     * Determines if the input number is equal to the current values.
     * 
     * @return true if the input object is a complex number with the real and
     *         imaginary parts that are equal. Otherwise, false.
     */
    @Override
    public boolean equals(Object equalsComplex) {
    	if (equalsComplex == null) {
    	    return false;
    	}
    	if (!(equalsComplex instanceof ComplexNumber)) {
    	    return false;
    	}
    	ComplexNumber equals = (ComplexNumber) equalsComplex;
    	return equals.getRealPart() == getRealPart()
    		   && equals.getImaginaryPart() == getImaginaryPart();
    }

    /**
     * Determines if the number are equal to a certain degree of accuracy.
     * 
     * @param equalsComplex
     *            The complex number to compare.
     * @param decimalPlaces
     *            The number of decimal places to check to in the comparison of
     *            the imaginary and real values.
     * @return true if the values are non-null and they are equal to the number
     *         of decimal places as specified above.
     */
    public boolean equalsRounded(Object equalsComplex, int decimalPlaces) {
    	if (equalsComplex != null && equalsComplex instanceof ComplexNumber) {
    	    ComplexNumber equals = (ComplexNumber) equalsComplex;
    	    return roundNumber(equals.getRealPart(), decimalPlaces) == roundNumber(getRealPart(), decimalPlaces)
    		       && roundNumber(equals.getImaginaryPart(), decimalPlaces) == roundNumber(getImaginaryPart(), decimalPlaces);
    	} else {
    	    return false;
    	}
    }

    /**
     * Parses the input string to set the values to a complex number. All
     * whitespace will be removed before the number is parsed. The valid values
     * are set in this regular expression:
     * "[+-]?([\d]+[iI]?|[iI]?[\d]+)[+-]?([\d]+[iI]?|[iI]?[\d]+)"
     * 
     * @param complexNumber
     *            The string to parse. All whitespace will be ignored and the
     *            values of "i" will be case insensitive.
     * @return A complex number with the real and imaginary values set. If there
     *         are two imaginary, or two real parts to the String, then this
     *         will use the last value of the value and the other will be
     *         ignored.
     * @throws NumberFormatException
     *             If the string does not contain a valid format of the number.
     */
    public static ComplexNumber parseString(String complexNumber) throws NumberFormatException {
    	double real = 0.0f;
    	double imag = 0.0f;
    	complexNumber = complexNumber.toLowerCase();
    	complexNumber = complexNumber.replaceAll(" ", "");
    	while (complexNumber.length() > 0) {
    	    if (isNextNumberImag(complexNumber)) {
    	        imag = parseNextNumber(complexNumber);
    	    } else {
    	        real = parseNextNumber(complexNumber);
    	    }
    	    if (complexNumber.charAt(0) == '-'|| complexNumber.charAt(0) == '+') {
    	        complexNumber = complexNumber.substring(1);
    	    }
    	    // find the next occurance of '-' or '+'
    	    if (complexNumber.indexOf('+') != -1) {
    	        complexNumber = complexNumber.substring(complexNumber.indexOf('+'));
    	    } else if (complexNumber.indexOf('-') != -1) {
    	        complexNumber = complexNumber.substring(complexNumber.indexOf('-'));
    	    } else {
    	        complexNumber = "";
    	    }
    	}
    	return new ComplexNumber(real, imag);
    }
    /**
     * Rounds the number to the number of decimal places.
     * This uses the {@link Math.rint()} function to correctly round the
     * number
     * 
     * @param number
     *      The number that _may_ need to be rounded.
     * @param places
     *      The number of decimal places to round the number (inclusive).
     *      
     * @return
     *      The rounded number.
     */
    private double roundNumber(double number, int places) {
    	double shiftFactor = Math.pow(10, places);
    	number = number * shiftFactor;
    	double tmp = Math.rint(number);
    	return (double) tmp / shiftFactor;
    }
    /**
     * Determines if the next series of characters in the input string contains an
     * 'i' or 'I' before the end of the string or the '+' or '-' signs.
     * 
     * @param complexNumber
     *      The String that represents the complex number.
     * @return
     *      true if the String contains and 'i' before the next '-' or '+' and all of the
     *      numbers before it are digits or a '.'
     *      
     * @throws NumberFormatException
     *      If any characters that are in the string do not match the digit, '.', '+', or '-'.
     */
    private static boolean isNextNumberImag(String complexNumber) throws NumberFormatException {
    	int start = 0;
    	if (Character.isDigit(complexNumber.charAt(start))) {
    	    start--;
    	} else if (complexNumber.charAt(0) == 'i') {
    	    return true;
    	}
    	for (int i = start + 1; i < complexNumber.length(); i++) {
    	    if (Character.isDigit(complexNumber.charAt(i)) || complexNumber.charAt(i) == '.') {
    	        continue;
    	    } else if (complexNumber.charAt(i) == 'i') {
    	        return true;
    	    } else if (complexNumber.charAt(i) == '+'
    		    || complexNumber.charAt(i) == '-') {
    	        return false;
    	    }
    	    // Not a valid Number
    	    throw new NumberFormatException("String \"" + complexNumber
    		    + "\" is not a valid number  at position: " + i);
    	}
    	return false;
    }
    /**
     * Parses the string to give a double number that is found until there is a non-digit or
     * non '.' character.
     * 
     * @param complexNumber
     *      The string to parse.
     * @return
     *      The double value of the number that was found at the beginning of the String.
     */
    private static double parseNextNumber(String complexNumber) {
    	double sign = 1.0f;
    	int start = 0;
    	if (complexNumber.charAt(0) == '-') {
    	    sign = sign * -1f;
    	}
    	// If the first char is a digit, then we will start one earlier because
    	// we usually skip the first char because it is the sign.
    	else if (Character.isDigit(complexNumber.charAt(0)) || complexNumber.charAt(0) == '.') {
    	    start--;
    	}
    	int endPoint;
    	for (endPoint = start + 1; endPoint < complexNumber.length(); endPoint++) {
    	    if (Character.isDigit(complexNumber.charAt(endPoint)) || complexNumber.charAt(endPoint) == '.') {
    	        continue;
    	    } else {
    	        break;
    	    }
    	}
    	if (start == endPoint - 1) {
    	    return sign;
    	} else {
    	    return Double.parseDouble(complexNumber.substring(start + 1, endPoint)) * sign;
    	}
    }
    /**
     * An overridden toString() method to provide the String version of the 
     * Complex Number.  This will output the full complex number.
     * 
     * @return 
     *      The String version of the complex number.  The output will only
     *      include the real part if it is non-zero (unless the imaginary part is
     *      also zero) and will only include the imaginary part if it is non-zero.
     */
    public String toString() {
    	String real = "";
    	if (getRealPart() != 0) {
    	    real = getNiceDoubleValue(getRealPart());
    	}
    	String operator = (getImaginaryPart() > 0 && real.length() > 0) ? "+" : "";
    	String imaginary = "";
    	if (getImaginaryPart() == 1) {
    	    imaginary = "i";
    	} else if (getImaginaryPart() == -1) {
    	    imaginary = "-i";
    	} else if (getImaginaryPart() != 0) {
    	    imaginary = getNiceDoubleValue(getImaginaryPart()) + "i";
    	}
    	String returnValue = new String(real + operator + imaginary);
    	if (returnValue.equals(""))
    	    return new String("0");
    	else
    	    return returnValue;
    }

    private String getNiceDoubleValue(double doubleValue) {
    	if (Math.rint(doubleValue) == doubleValue) {
    	    return new Integer(new Double(doubleValue).intValue()).toString();
    	} else {
    	    return new Double(doubleValue).toString();
    	}
    }

    /**
     * Prints the value of the complex number and rounds to the number of
     * decimal places specific by the input argument
     * 
     * @param decimalPlaces
     *            number of decimal places to round.
     * 
     * @return the string out of the Complex Number.
     */
    public String toString(int decimalPlaces) {
    	if (decimalPlaces < 0) {
    	    return toString();
    	} else {
    	    String real = (roundNumber(getRealPart(), decimalPlaces) != 0) ? getNiceDoubleValue(roundNumber(
    		    getRealPart(), decimalPlaces)) : "";
    	    String operator = (getImaginaryPart() > 0 && real.length() > 0) ? "+" : "";
    	    String imaginary = null;
    	    double imaginaryDouble = roundNumber(getImaginaryPart(),
    		    decimalPlaces);
    	    if (imaginaryDouble == 1) {
    	        imaginary = "i";
    	    } else if (imaginaryDouble == -1) {
    	        imaginary = "-i";
    	    } else {
        		imaginary = (getImaginaryPart() != 0) ? getNiceDoubleValue(
        			imaginaryDouble).toString() + "i" : "";
    	    }
    
    	    String returnValue = new String(real + operator + imaginary);
    	    if (returnValue.equals("")){
    	        return new String("0");
    	    }
    	    else{
    	        return returnValue;
    	    }
    	}
    }
    /**
     * A simple series of tests that looks to make sure that the
     * constructor, parsing, divide, and multiply functions are working.
     * 
     * @param args
     */
    public static void main(String[] args) {
    	System.out.println(new ComplexNumber(1, 1).toString());
    	System.out.println(new ComplexNumber(0, 0).toString());
    	System.out.println(new ComplexNumber(-5.44444, +1000393.32323).toString());
    	System.out.println(new ComplexNumber("3-4i").toString());
    	System.out.println(new ComplexNumber("3.054-4.2222i").toString());
    	System.out.println(new ComplexNumber("3.044+4i").toString());
    	System.out.println(new ComplexNumber(".055445I+4").toString());
    	System.out.println(new ComplexNumber("-.055445I+4").toString());
    	System.out.println(new ComplexNumber("- .0 55445I + 4").toString());
    	System.out.println(new ComplexNumber("- .055445 I +4 ").toString());
    	System.out.println(new ComplexNumber("3i").toString());
    	System.out.println(new ComplexNumber("-i").toString());
    	System.out.println(new ComplexNumber("+I").toString());
    	System.out.println(new ComplexNumber("+1").toString());
    	System.out.println(new ComplexNumber("-1").toString());
    	System.out.println(new ComplexNumber("7.0444").toString());
    	System.out.println(new ComplexNumber("-5.086+53434.333i").toString());
    	System.out.println(new ComplexNumber("3-4I").toString());
    	System.out.println(new ComplexNumber("3-4i").dividedBy( new ComplexNumber("3-4i")).toString());
    	System.out.println(new ComplexNumber("3-4i").dividedBy( new ComplexNumber("-4i")).toString());
    	System.out.println(new ComplexNumber("3-4i").multiply( new ComplexNumber("3-4i")).toString());
    }

}