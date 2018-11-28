package vector;

import shapes.Orientation;

public abstract class Vector {
	
	public static Vector[] scaleAll(double factor, Vector...vecs) {
		for (var v : vecs)
			v.scale(factor);
		return vecs;
	}
	
	public static Vector[] scaleAllAlong(Vector dir, Vector...vecs) {
		for (var v : vecs)
			v.scaleAlong(dir);
		return vecs;
	}
	
	/**
	 * Calculates the square of the magnitude of an arbitrary-dimensional vector with Cartesian components e1, e2...
	 * @param e an array of the components
	 * @return the square of the magnitude, i.e. sum(e^2)
	 */
	static double getMagnitudeSq(double...e) {
		double mag = 0;
		for (int i = 0; i < e.length; i++)
			mag += e[i]*e[i];
		return mag;
	}
	
	/**
	 * Calculates the magnitude of an arbitrary-dimensional vector with Cartesian components e1, e2...
	 * @param e an array of the components
	 * @return the magnitude, i.e. sqrt(sum(e^2))
	 */
	static double magnitude(double...e) {
		return Math.sqrt(getMagnitudeSq(e));
	}
	
	/**
	 * @param u a position vector
	 * @param v another position vector
	 * @return the square of the distance between u and v
	 */
	public static double distanceSq(Vector u, Vector v) {
		return getMagnitudeSq(u.x() - v.x(), u.y() - v.y(), u.z() - v.z());
	}
	
	/**
	 * @param u a position vector
	 * @param v another position vector
	 * @return the distance between u and v
	 */
	public static double distance(Vector u, Vector v) {
		return Math.sqrt(distanceSq(u, v));
	}
	
	
	public abstract double magnitude();
	/**
	 * @return the square of the magnitude
	 */
	public abstract double magnitudeSq();
	/**
	 * The azumithal angle is in the x-y plane, denoting deviation from the x-axis.
	 * @return the azumithal angle, in radians: -pi < theta < pi
	 */
	public abstract double azumithal();
	/**
	 * The zenith angle denotes deviation from the z-axis.
	 * @return the zenith angle, in radians: 0 < phi < pi
	 */
	public abstract double zenith();
	public abstract double x();
	public abstract double y();
	public abstract double z();
	public double getCoord(int i) {
		switch(i) {
		case 0:
			return x();
		case 1:
			return y();
		case 2:
			return z();
		default:
			throw new RuntimeException("Invalid coordinate: " + i);
		}
	}
	
	public abstract Vector setMagnitude(double mag);
	public abstract double setAzumithal(double az);
	public abstract double setZenith(double zen);
	public abstract double setX(double x);
	public abstract double setY(double y);
	public abstract double setZ(double z);
	
	/**
	 * Zeroes the vector, setting all parameters to 0. Not necessarily equivalent to scale(0).
	 * @return this
	 */
	public abstract Vector zero();
	
	/**
	 * Checks if the magnitude is zero.
	 * @return whether the magnitude is zero.
	 */
	public abstract boolean isZero();
	
	/**
	 * Scales this vector by factor, preserving direction.
	 * @param factor the scale factor
	 * @return this
	 * @see #zero()
	 */
	public Vector scale(double factor) {
		setMagnitude(magnitude()*factor);
		return this;
	}
	
	/**
	 * Scales this vector along the direction of another vector, by that vector's magnitude.
	 * @param dir the scaling vector
	 * @return this
	 */
	public Vector scaleAlong(Vector dir) {
		setX(x() * dir.x());
		setY(y() * dir.y());
		setZ(z() * dir.z());
		return this;
	}
	
	/**
	 * Sets this vector to a unit vector. Direction is preserved; magnitude is obviously not.
	 * @return this
	 */
	public Vector normalize() {
		setMagnitude(1);
		return this;
	}
	
	/**
	 * Rotates this vector, preserving magnitude.
	 * @param daz change in azumithal angle
	 * @param dzen change in zenith angle
	 * @return this
	 */
	public Vector rotate(double daz, double dzen) {
		setAzumithal(azumithal() + daz);
		setZenith(zenith() + dzen);
		return this;
	}
	/**
	 * Translates this vector, adding dx, dy, and dz to x, y, and z
	 * @param dx change in x
	 * @param dy change in y
	 * @param dz change in z
	 * @return this
	 */
	public Vector translate(double dx, double dy, double dz) {
		setX(x() + dx);
		setY(y() + dy);
		setZ(z() + dz);
		return this;
	}
	/**
	 * Translates this vector, adding v to it 
	 * @param v changes to x, y, and z
	 * @return this
	 */
	public Vector translate(Vector v) {
		setX(x() + v.x());
		setY(y() + v.y());
		setZ(z() + v.z());
		return this;
	}
	
	/**
	 * Translates this vector by scale*v
	 * @param v changes to x, y, and z
	 * @param scale scale factor for v
	 * @return this
	 */
	public Vector translateScaled(Vector v, double scale) {
		setX(x() + scale*v.x());
		setY(y() + scale*v.y());
		setZ(z() + scale*v.z());
		return this;
	}
	
	/**
	 * Sets this vector to the projection of u on v
	 * @param u magnitude of interest
	 * @param v direction of interest
	 * @return this
	 */
	public abstract Vector projUonV(Vector u, Vector v);

	/**
	 * Takes the cross product of this and v, returning a new vector of the same type as this.
	 * Neither this nor v are mutated.
	 * @param v the multiplicand
	 * @return a new vector representing the cross product
	 */
	public abstract Vector cross(Vector v);
	/**
	 * Sets this vector to u x v.
	 * Example: u.cross(u, v) sets u to u x v, while a.cross(b, c) sets a to b x c and returns a
	 * @param u multiplier vector
	 * @param v multiplicand vector
	 * @return this
	 */
	public Vector cross(Vector u, Vector v) {
		double xTemp = (u.y()*v.z() - u.z()*v.y());
		double yTemp = (u.z()*v.x() - u.x()*v.z());
		double zTemp = (u.x()*v.y() - u.y()*v.x());
		setX(xTemp);
		setY(yTemp);
		setZ(zTemp);
		return this; 
	}
	
	/**
	 * Takes the dot product of this and v, returning a double of the result.
	 * Neither this nor v are mutated.
	 * @param v the other factor
	 * @return the dot product
	 */
	public double dot(Vector v) {
		return x()*v.x() + y()*v.y() + z()*v.z();
	}
	
	/**
	 * Adds this and v together, returning a new vector of the same type as this.
	 * Neither this nor v are mutated.
	 * @param v the addend.
	 * @return A vector representing this + v
	 */
	public abstract Vector sum(Vector v);
	
	/**
	 * Subtracts v from u, returning a new vector of the same type as this
	 * @param v the subtrahend
	 * @return this-v as a new vector
	 */
	public abstract Vector difference(Vector v);
	/**
	 * Sets this vector to u - v.
	 * @param u minuend
	 * @param v subtrahend
	 * @return this
	 */
	public Vector difference(Vector u, Vector v) {
		setX(u.x() - v.x());
		setY(u.y() - v.y());
		setZ(u.z() - v.z());
		return this;
	}
	
	/**
	 * Sets this vector to v, effectively duplicating it.
	 * @param v vector to be copied
	 * @return this
	 */
	public abstract Vector become(Vector v);
	
	/**
	 * Adjusts x, y, and z so that they correspond to the new basis. This only works once! To change
	 * the basis after this, use {@link #changeBasis(Orientation, Orientation)}.
	 * @param o the basis
	 * @return this
	 */
	public Vector setBasis(Orientation o) {
		double x = dot(o.xAxis());
		double y = dot(o.yAxis());
		double z = dot(o.zAxis());
		setX(x);
		setY(y);
		setZ(z);
		return this;
	}
	
	public Vector transform(Matrix3x3 m) {
		return become(m.getProduct(this));
	}
	
	/**
	 * @return an exact duplicate of the same type as this
	 */
	public abstract Vector duplicate();
	
	@Override
	public abstract String toString();
}
