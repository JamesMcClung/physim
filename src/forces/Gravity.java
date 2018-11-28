package forces;

import java.util.List;

import bodies.Body;
import sim.Spacetime;
import vector.CVector;
import vector.Vector;
import vector.VectorField;

public class Gravity implements Force {
	
	/**
	 * Gravitational constant. In units of N*m^2/kg^2
	 */
	public static final double G = 6.674E-11;
	/**
	 * Acceleration due to gravity on Earth's surface. In units of m/s^2
	 */
	public static final VectorField EARTH_FIELD = (r) -> new CVector(0, -9.8, 0);
	
	/**
	 * Creates a new field of gravity. Default strength is {@link G} and default background field is 0.
	 */
	public Gravity() {
		this(G, null);
	}

	/**
	 * Creates a new field of gravity. Field strength can be anything, and backgroundField can 
	 * be null (in which case it is set to 0).
	 * @param fieldStrength gravitational constant [N*m^2/kg^2]
	 * @param backgroundField acceleration due to gravity [m/s^2]
	 * @see #G
	 * @see #EARTH_FIELD
	 */
	public Gravity(double fieldStrength, VectorField backgroundField) {
		this.fieldStrength = fieldStrength;
		this.backgroundField = backgroundField;
	}
	
	private VectorField backgroundField;
	private double fieldStrength;

	private Vector fa = new CVector(0,0,0); // force on particle a (this gets recycled)
	@Override
	public void applyTo(Spacetime s) {
		List<Body> particles = s.particles();
		Body a, b;
		for (int i = 0; i < particles.size(); i++) {
			a = particles.get(i);
			
			// apply force from backgound field (only once per particle)
			if (backgroundField != null)
				a.applyForce(backgroundField.getVectorAt(a.position()).scale(a.getMass()));
			
			// apply inter-particle forces
			for (int j = i+1; j < particles.size(); j++) {
				b = particles.get(j);
				fa.difference(b.position(), a.position())
					.scale(fieldStrength * a.getMass() * b.getMass() / 
							Math.pow(Vector.distanceSq(a.position(), b.position()), 1.5));
				a.applyForce(fa);
				b.applyForce(fa.scale(-1));
			}
		}
	}
	

}
