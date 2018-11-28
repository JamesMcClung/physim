package vector;

/**
 * Cartesian vector. Stores data as (x, y, z). Preferred for most, if not all, cases, unless
 * I ever need to integrate.
 * @author james
 *
 */
public class CVector extends Vector {
	
//	public static CVector sum(Vector a, Vector b) {
//		return new CVector(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
//	}
//	
//	public static CVector difference(Vector a, Vector b) {
//		return new CVector(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
//	}
	
	/**
	 * Identical to <code>new CVector(x, y, z)</code>, but more convenient (if statically imported).
	 */
	public static CVector vec(double x, double y, double z) {
		return new CVector(x, y, z);
	}
	
	/**
	 * Creates a zero vector.
	 */
	public CVector() {
		x = y = z = 0;
	}
	
	public CVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a duplicate of a vector.
	 * @param v vector to be copied; if null, defaults to 0-vector
	 */
	public CVector(Vector v) {
		if (v == null) {
			x = y = z = 0;
		} else {
			x = v.x();
			y = v.y();
			z = v.z();
		}
	}

	private double x, y, z;

	@Override
	public double magnitude() {
		return magnitude(x, y, z);
	}
	
	@Override
	public double magnitudeSq() {
		return getMagnitudeSq(x, y, z);
	}

	@Override
	public double azumithal() {
		return Math.atan2(y, x);
	}

	@Override
	public double zenith() {
		return Math.atan2(magnitude(x, y), x);
	}

	@Override
	public double x() {
		return x;
	}

	@Override
	public double y() {
		return y;
	}

	@Override
	public double z() {
		return z;
	}

	@Override
	public Vector setMagnitude(double mag) {
		scale(mag / magnitude());
		return this;
	}

	@Override
	public double setAzumithal(double az) {
		return 0;
	}

	@Override
	public double setZenith(double zen) {
		return 0;
	}

	@Override
	public double setX(double x) {
		double temp = this.x;
		this.x = x;
		return temp;
	}

	@Override
	public double setY(double y) {
		double temp = this.y;
		this.y = y;
		return temp;
	}

	@Override
	public double setZ(double z) {
		double temp = this.z;
		this.z = z;
		return temp;
	}

	@Override
	public Vector scale(double factor) {
		x *= factor;
		y *= factor;
		z *= factor;
		return this;
	}
	
	@Override
	public Vector projUonV(Vector u, Vector v) {
		return become(u).scale(u.dot(v) / v.magnitudeSq());
	}
	
	@Override
	public Vector zero() {
		x = y = z = 0;
		return this;
	}
	
	@Override
	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	@Override
	public Vector rotate(double daz, double dzen) {
		return become(new PVector(this).rotate(daz, dzen));
	}
	
	@Override
	public Vector translate(double dx, double dy, double dz) {
		x += dx;
		y += dy;
		z += dz;
		return this;
	}

	@Override
	public Vector cross(Vector v) {
		return new CVector(y*v.z() - z*v.y(), z*v.x() - x*v.z(), x*v.y() - y*v.x());
	}

	@Override
	public double dot(Vector v) {
		return x*v.x() + y*v.y() + z*v.z();
	}
	
	@Override
	public Vector sum(Vector v) {
		return new CVector(x + v.x(), y + v.y(), z + v.z());
	}
	
	@Override
	public Vector difference(Vector v) {
		return new CVector(x-v.x(), y-v.y(), z-v.z());
	}
	
	@Override
	public Vector become(Vector v) {
		if (v == null)
			x = y = z = 0;
		else {
			x = v.x();
			y = v.y();
			z = v.z();
		}
		return this;
	}
	
	@Override
	public Vector duplicate() {
		return new CVector(this);
	}
	
	@Override
	public String toString() {
		return String.format("<%.2f, %.2f, %.2f>", x, y, z);
	}
	
}
