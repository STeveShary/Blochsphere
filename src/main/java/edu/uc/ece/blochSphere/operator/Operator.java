package edu.uc.ece.blochSphere.operator;

import edu.uc.ece.blochSphere.ComplexNumber;

/**
 * The operator class that allows operators to be applied to each other and to
 * two dimensional complex Vectors (representing qubits).
 * 
 * @author Stephen Shary
 */
public class Operator {
    private ComplexNumber topLeft = null;
    private ComplexNumber topRight = null;
    private ComplexNumber bottomLeft = null;
    private ComplexNumber bottomRight = null;

    /**
     * The constructor that will populate the the four elements that are
     * used to describe the operator.
     * 
     * { topLeft 	topRight } 
     * { bottomLeft	bottomRight }
     * 
     * @param topLeft
     * 		The top-left element in the 2x2 matrix.
     * @param topRight
     * 		The top-right element in the 2x2 matrix.
     * @param bottomLeft
     * 		The bottom-left element in the 2x2 matrix.
     * @param bottomRight
     * 		The bottom-right element in the 2x2 matrix.
     */
    public Operator(ComplexNumber topLeft, ComplexNumber topRight,
	    ComplexNumber bottomLeft, ComplexNumber bottomRight) {
	this.topLeft = topLeft;
	this.topRight = topRight;
	this.bottomLeft = bottomLeft;
	this.bottomRight = bottomRight;
    }

    /**
     * Multiplies the current operator by the input operator.
     * 
     * @param operator
     *            The operator that this operator is multiplied by.
     * @return A new Operator that is the product of this operator and the input
     *         operator.
     */
    public Operator multiply(Operator operator) {
	return new Operator(topLeft.multiply(operator.getTopLeftElement()).add(
		topRight.multiply(operator.getBottomLeftElement())), topLeft
		.multiply(operator.getTopRightElement()).add(
			topRight.multiply(operator.getBottomRightElement())),
		bottomLeft.multiply(operator.getTopLeftElement()).add(
			bottomRight.multiply(operator.getBottomLeftElement())),
		bottomLeft.multiply(operator.getTopRightElement()).add(
			bottomRight.multiply(operator.getBottomRightElement())));
    }

    /**
     * Checks if the current operator is a unitary operator. It does this by
     * checking if the operator (A) is equal to I when A*A = I
     * 
     * @return If the operator multiplied by the complex conjugate is equivalent
     *         to I. It will check that each multiplied value is equivalent to
     *         expected value of I. This does rounding due to the rounding
     *         problems and precision of the ComplexNumber
     */
    public boolean isOperatorUnitary() {
	return (topLeft.getComplexConjugate().multiply(topLeft).add(bottomLeft.getComplexConjugate().multiply(bottomLeft)).equalsRounded(new ComplexNumber(1, 0), 2)
		&& topLeft.getComplexConjugate().multiply(topRight).add(bottomLeft.getComplexConjugate().multiply(bottomRight)).equalsRounded(new ComplexNumber(0, 0), 2)
		&& topRight.getComplexConjugate().multiply(topLeft).add(bottomRight.getComplexConjugate().multiply(bottomLeft)).equalsRounded(new ComplexNumber(0, 0), 2) 
		&& topRight.getComplexConjugate().multiply(topRight).add(bottomRight.getComplexConjugate().multiply(bottomRight)).equalsRounded(new ComplexNumber(1, 0), 2));
    }

    /**
     * Creates the adjoint of the current operator. The current value of the
     * operator are:
     * 
     * { topLeft 	topRight } 
     * { bottomLeft	bottomRight }
     * 
     * 
     * @return The adjoint of the operator where the values are defined from the
     *         operator values above:
     * 
     *         { topLeft.getComplexConjugate()  bottomLeft.getComplexConjugate()} 
     *         { topRight.getComplexConjugate() bottomRight.getComplexConjugate() }
     */
    public Operator getAdjoint() {
	return new Operator(topLeft.getComplexConjugate(), 
		           bottomLeft.getComplexConjugate(), 
		           topRight.getComplexConjugate(),
		           bottomRight.getComplexConjugate());
    }

    /**
     * Determines if the current operator is hermitian. If an operator is
     * hermitian, then it is both unitary and the operator is equal to it's
     * adjoint.
     * 
     * @return true if the current values of the operator make the operator both
     *         unitary and hermitian (the operator is equal to it's adjoint).
     */
    public boolean isOperatorHermitian() {
	return isOperatorUnitary() && equals(getAdjoint());
    }
    /**
     * This will return the modified value of a vector that consists of
     *         (Operator}          (Vector) 
     *   [topLeft        topRight] [alpha]
     *   [bottomLeft bottomrRight] [beta ]
     *   
     *  and is multiplied by the operator (on the left)
     *  
     * @param alphaValue
     * 		The top value of the alpha value of the vector that is being
     * 		multiplied by the Operator.
     * @param betaValue
     * 		The bottom value, or the beta value of the vector that si being
     * 		multiplied by the Operator.
     * @return
     * 		The new value of the top element of the Vector.  We refer to this as
     * 		the alpha value.  We look at the idea that the Vector is changing state when acted
     * 		upon by the Operator.
     */
    public ComplexNumber applyOperatorToAlpha(ComplexNumber alphaValue,
	    ComplexNumber betaValue) {
	return topLeft.multiply(alphaValue).add(topRight.multiply(betaValue));
    }
    /**
     * This will return the modified value of a vector that consists of
     *         (Operator}          (Vector) 
     *   [topLeft        topRight] [alpha]
     *   [bottomLeft bottomrRight] [beta ]
     *   
     *  and is multiplied by the operator (on the left)
     *  
     * @param alphaValue
     * 		The top value of the alpha value of the vector that is being
     * 		multiplied by the Operator.
     * @param betaValue
     * 		The bottom value, or the beta value of the vector that si being
     * 		multiplied by the Operator.
     * @return
     * 		The new value of the bottom element of the Vector.  We refer to this as
     * 		the beta value.  We look at the idea that the Vector is changing state when acted
     * 		upon by the Operator.
     */
    public ComplexNumber applyOperatorToBeta(ComplexNumber alphaValue,
	    ComplexNumber betaValue) {
	return bottomLeft.multiply(alphaValue).add(bottomRight.multiply(betaValue));
    }

    public ComplexNumber getTopLeftElement() {
	return topLeft;
    }

    public void setTopLeftElement(ComplexNumber topLeft) {
	this.topLeft = topLeft;
    }

    public ComplexNumber getTopRightElement() {
	return topRight;
    }

    public void setTopRightElement(ComplexNumber topRight) {
	this.topRight = topRight;
    }

    public ComplexNumber getBottomLeftElement() {
	return bottomLeft;
    }

    public void setBottomLeftElement(ComplexNumber bottomLeft) {
	this.bottomLeft = bottomLeft;
    }

    public ComplexNumber getBottomRightElement() {
	return bottomRight;
    }

    public void setBottomRightElement(ComplexNumber bottomRight) {
	this.bottomRight = bottomRight;
    }
}
