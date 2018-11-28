package vector;

public class PVector extends Vector {
	
	public PVector(double mag, double az, double zen) {
		this.mag = mag;
		this.az = az;
		this.zen = zen;
	}
	
	public PVector(Vector v) {
		this.mag = v.magnitude();
		this.az = v.azumithal();
		this.zen = v.zenith();
	}
	
	private double mag, az, zen;

	@Override
	public double magnitude() {
		return mag;
	}

	@Override
	public double magnitudeSq() {
		return mag * mag;
	}

	@Override
	public double azumithal() {
		return az;
	}

	@Override
	public double zenith() {
		return zen;
	}

	@Override
	public double x() {
		return mag * Math.cos(az) * Math.sin(zen);
	}

	@Override
	public double y() {
		return mag * Math.sin(az) * Math.sin(zen);
	}

	@Override
	public double z() {
		return mag * Math.cos(zen);
	}

	@Override
	public Vector setMagnitude(double mag) {
		this.mag = mag;
		return this;
	}

	@Override
	public double setAzumithal(double az) {
		double temp = this.az;
		this.az = az;
		return temp;
	}

	@Override
	public double setZenith(double zen) {
		double temp = this.zen;
		this.zen = zen;
		return temp;
	}

	@Override
	@Deprecated
	public double setX(double x) {
		return 0;
	}

	@Override
	@Deprecated
	public double setY(double y) {
		return 0;
	}

	@Override
	@Deprecated
	public double setZ(double z) {
		return 0;
	}

	@Override
	public Vector scale(double factor) {
		mag *= factor;
		return this;
	}
	
	@Override
	public Vector zero() {
		mag = az = zen = 0;
		return this;
	}
	
	@Override
	public boolean isZero() {
		return mag == 0;
	}

	@Override
	public Vector rotate(double daz, double dzen) {
		az += daz;
		zen += dzen;
		return this;
	}

	@Override
	@Deprecated
	public Vector translate(double dx, double dy, double dz) {
		return null;
	}

	@Override
	public Vector cross(Vector v) {
		return new PVector(new CVector(this).cross(v));
	}

	@Override
	@Deprecated
	public double dot(Vector v) {
		return 0;
	}
	
	@Override
	@Deprecated
	public Vector projUonV(Vector u, Vector v) {
		return null;
	}

	@Override
	@Deprecated
	public Vector sum(Vector v) {
		return null;
	}
	
	@Override
	@Deprecated
	public Vector difference(Vector v) {
		return null;
	}
	
	@Override
	public Vector become(Vector v) {
		mag = v.magnitude();
		az = v.azumithal();
		zen = v.zenith();
		return this;
	}
	
	@Override
	public Vector duplicate() {
		return new PVector(this);
	}

	@Override
	public String toString() {
		return String.format("<%.2f, %.2fπ, %.2fπ>", mag, az / Math.PI, zen / Math.PI);
	}

}
