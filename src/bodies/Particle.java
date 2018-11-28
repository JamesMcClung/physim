package bodies;

import graphics.GraphicsInterface;
import shapes.Orientation;
import vector.Quaternion;
import vector.Vector;

public class Particle extends Body {
	/**
	 * Radius of point particles, for rendering purposes. In pixels!
	 */
	public static final int POINT_PARTICLE_APPARENT_RADIUS = 10;
	
	/**
	 * Creates a particle with the following properties. Null vectors are treated as 0,0,0,
	 * defaulting to CVectors (Cartesian vectors). The vector types will not change; PVectors will
	 * remain PVectors and CVectors will remain CVectors. They are, however, duplicated.
	 * <p>
	 * The particle is initially a point particle, but this can be changed via
	 * {@link #setDensity(double)}, {@link #setRadius(double)}, etc.
	 * @param mass the mass (kg)
	 * @param charge the charge (C)
	 * @param position position vector (m)
	 * @param velocity velocity vector (m/s)
	 */
	public Particle(double mass, double charge, Vector position, Vector velocity) {
		super(mass, position, velocity);
		setCharge(charge);
	}
	
	/**
	 * Creates a particle with zero charge.
	 * @see #Particle(double, double, Vector, Vector)
	 */
	public Particle(double mass, Vector position, Vector velocity) {
		this(mass, 0, position, velocity);
	}
	
	private Orientation orientation = new Orientation();
	
	@Override
	public void renderAt(GraphicsInterface g, Vector position) {
		g.drawParticle(position, MINIMUM_APPARENT_RADIUS);
	}
	
	@Override
	public void updateAccelerations() {
		acceleration.become(netForce).scale(1/mass);
	}
	
	@Override
	public double getCrossSection(Vector axis) {
		return 0;
	}
	
	@Override
	public double getVolume() {
		return 0;
	}


	@Override
	public void rotate(Quaternion q) {
		orientation.rotate(q);
	}

	@Override
	public double getRadius() {
		return 0;
	}

	@Override
	public Orientation orientation() {
		return orientation;
	}

}
