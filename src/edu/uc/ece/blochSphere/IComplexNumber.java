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
public interface IComplexNumber {

    /**
     * Allows the real part of the Complex Number to be defined.
     * 
     * @param real
     *            The real part of the complex number. If there is no real part,
     *            then supply 0.0f.
     */
    public void setRealPart(double real);

    /**
     * Allows the imaginary part of the complex number to be defined.
     * 
     * @param imaginary
     *            The imaginary part of the complex number.
     */
    public void setImaginaryPart(double imaginary);

    /**
     * Gets the real part of the Complex Number.
     * 
     * @return The real part of the complex number. If there is not defined
     *         value, then the value will be 0.0f.
     */
    public double getRealPart();

    /**
     * Gets the imaginary part of the Complex Number.
     * 
     * @return The imaginary part of the complex number. If there is not defined
     *         value, then the value will be 0.0f.
     */
    public double getImaginaryPart();

    /**
     * Returns a new ComplexNumber object where the real part of the number is
     * unaffected and the imaginary part is multiplied by "-1", effectively
     * changing the sign of the imaginary part of the complex number.
     * 
     * @return A new ComplexNumber object where the new object is the complex
     *         conjugate of the current value of this object.
     */
    public ComplexNumber getComplexConjugate();

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
    public ComplexNumber add(ComplexNumber compNum);

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
    public ComplexNumber subtract(ComplexNumber compNum);

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
    public ComplexNumber multiply(ComplexNumber compNum);

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
    public ComplexNumber dividedBy(ComplexNumber compNum);

    /**
     * Determines if the input number is the complex conjugate of this.
     * 
     * @param conjugate
     *            The input number to check if it is the complex conjugate.
     * @return true if the input complex number is non-null and it is the
     *         complex conjugate of the current value. Otherwise, false.
     */
    public boolean isComplexConjugateOf(ComplexNumber conjugate);

    /**
     * Determines if the input number is equal to the current values.
     * 
     * @return true if the input object is a complex number with the real and
     *         imaginary parts that are equal. Otherwise, false.
     */
    public boolean equals(Object equalsComplex);

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
    public boolean equalsRounded(Object equalsComplex, int decimalPlaces);
}
