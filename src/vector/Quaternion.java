package vector;


public class Quaternion {
	
	public static Quaternion sum(Quaternion...qs) {
		var sum = new Quaternion();
		for (var q : qs) {
			sum.re += q.re;
			sum.im.translate(q.im);
		}
		return sum;
	}
	
	public Quaternion(double re, double i, double j, double k) {
		this.re = re;
		im = new CVector(i, j, k);
	}
	
	public Quaternion(double re, Vector v) {
		this(re, v.x(), v.y(), v.z());
	}
	
	public Quaternion(Quaternion q) {
		this(q.re, q.im);
	}
	
	public Quaternion() {
		this(0,0,0,0);
	}
	
	private double re;
	private final Vector im;
	
	public double getReal() {
		return re;
	}
	
	public Vector getImaginary() {
		return im.duplicate();
	}
	
	/**
	 * Multiplies this (q1) by q2 using the formula:
	 * <p>
	 * (r1, v1)(r2, v2) = (r1r2 - v1•v2, r1v2 + r2v1 + v1xv2)
	 * @param q2 another quaternion
	 * @return this
	 */
	public Quaternion multiplyByRight(Quaternion q2) {
		Quaternion q1 = this;
		double r = q1.re*q2.re - q1.im.dot(q2.im);
		Vector i = q1.im.cross(q2.im).translateScaled(q1.im, q2.re).translateScaled(q2.im, q1.re);
		re = r;
		im.become(i);
		return this;
	}
	
	/**
	 * Multiplies this (q2) by q1 using the formula:
	 * <p>
	 * (r1, v1)(r2, v2) = (r1r2 - v1•v2, r1v2 + r2v1 + v1xv2)
	 * @param q1 another quaternion
	 * @return this
	 */
	public Quaternion multiplyByLeft(Quaternion q1) {
		Quaternion q2 = this;
		double r = q1.re*q2.re - q1.im.dot(q2.im);
		Vector i = q1.im.cross(q2.im).translateScaled(q1.im, q2.re).translateScaled(q2.im, q1.re);
		re = r;
		im.become(i);
		return this;
	}
	
	/**
	 * Multiplies this quaternion by d.
	 * @param d some number
	 * @return this
	 */
	public Quaternion scale(double d) {
		re *= d;
		im.scale(d);
		return this;
	}
	
	/**
	 * Adds a quaternion to this quaternion.
	 * @param q the quaternion to add
	 * @return this
	 */
	public Quaternion translate(Quaternion q) {
		re += q.re;
		im.translate(q.im);
		return this;
	}
	
	public Quaternion translate(double re) {
		this.re += re;
		return this;
	}
	
	public Quaternion translate(Vector im) {
		this.im.translate(im);
		return this;
	}
	
	/**
	 * Creates a new quaternion that is the conjugate of this quaternion.
	 * <p>
	 * If a quaternion is denoted as (r, v), then its conjugate is (r, -v).
	 * <p>
	 * The conjugate can be expressed as a pure function: q* = -1/2 (q + iqi + jqj + kqk).
	 * @return the conjugate quaternion, q*
	 */
	public Quaternion getConjugate() {
		return new Quaternion(re, -im.x(), -im.y(), -im.z());
	}
	
	/**
	 * Calculates the norm of this quaternion.
	 * <p>
	 * If a quaternion is denoted as (r, i, j, k), then its norm is sqrt(r^2 + i^2 + j^2 + k^2).
	 * @return the norm, ||q||
	 * @see #getNormSq()
	 */
	public double getNorm() {
		return Math.sqrt(getNormSq());
	}
	
	/**
	 * Calculates the square of the norm of this quaternion.
	 * @return the square of the norm.
	 * @see #getNorm()
	 */
	public double getNormSq() {
		return re*re + im.magnitudeSq();
	}
	
	/**
	 * Creates a new quaternion that is the versor of this quaternion.
	 * <p>
	 * A versor is a unit quaternion, such that norm(versor) = 1.
	 * @return the versor, Uq
	 */
	public Quaternion getVersor() {
		return new Quaternion(this).scale(getNorm());
	}
	
	/**
	 * Creates a new quaternion that is the reciprocal of this quaternion.
	 * <p>
	 * The reciprocal is defined to be q* / ||q||^2, which is the conjugate divided by the square of the norm.
	 * @return the reciprocal
	 */
	public Quaternion getReciprocal() {
		return getConjugate().scale(getNormSq());
	}
	
	/**
	 * Set the norm to 1. If the quaternion is 0, returns null.
	 * @return this, or null of it is 0.
	 */
	public Quaternion normalize() {
		double norm = getNorm();
		if (norm == 0)
			return null;
		return scale(1/norm);
	}
	
	/**
	 * Rotates the vector v by this quaternion. The operation is v' = qvq^-1.
	 * @param v the vector to be rotated
	 * @see #useToRotate2(Vector)
	 */
	@Deprecated
	public void useToRotate(Vector v) {
		v.become(new Quaternion(this).multiplyByRight(new Quaternion(0, v)).multiplyByRight(getReciprocal()).im);
	}
	
	/**
	 * Uses a more efficient, but less compact formula: v' = v + 2 * im x (im x v + re*v), where q = (re, im).
	 * <p>
	 * This quaternion is not mutated.
	 * @param v the vector to be rotated
	 * @return the rotated vector
	 */
	public Vector useToRotate2(Vector v) {
		v.translateScaled(im.cross(im.cross(v).translateScaled(v, re)), 2);
		return v;
	}
	
	/**
	 * Rotates v around an axis by some angle using quaternions.
	 * <p>
	 * Many virtual quaternions and vectors are used in doing so.
	 * @param v the vector to be rotated
	 * @param angle the angle, in radians
	 * @param axis the axis, a unit vector
	 */
	public static void rotateAroundAxis(Vector v, double angle, Vector axis) {
		new Quaternion(Math.cos(angle/2), new CVector(axis).scale(Math.sin(angle/2))).useToRotate2(v);
	}
	
	public Quaternion rotateBy(Quaternion q) {
		im.translateScaled(q.im.cross(q.im.cross(im).translateScaled(im, q.re)), 2);
		return this;
	}
	
	/**
	 * Creates a quaternion to represent a rotation around axis by angle.
	 * <p>
	 * q = cos(angle/2) + axis / ||axis|| * sin(angle/2)
	 * @param angle angle, in radians
	 * @param axis the axis of rotation; length does not matter
	 * @return q, the rotation quaternion
	 */
	public static Quaternion getRotationQuaternion(double angle, Vector axis) {
		return new Quaternion(Math.cos(angle/2), new CVector(axis).setMagnitude(Math.sin(angle/2)));
	}
	
	public void become(Quaternion q) {
		re = q.re;
		im.become(q.im);
	}
	
	@Override
	public String toString() {
		return String.format("<%.02f, %.02f, %.02f, %.02f>", re, im.x(), im.y(), im.z());
	}

}
