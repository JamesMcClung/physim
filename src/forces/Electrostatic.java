package forces;

import java.util.List;

import bodies.Body;
import sim.Spacetime;
import vector.CVector;
import vector.Vector;
import vector.VectorField;

public class Electrostatic implements Force {
	/**
	 * Commonly written as ε0. Units are C^2/N/m^2. 
	 */
	public static final double ELECTRIC_CONSTANT = 8.854187817E-12; 
	/**
	 * Field constant for electric field. Units are N*m^2/C^2.
	 */
	public static final double FIELD_CONSTANT = 1/(4*Math.PI*ELECTRIC_CONSTANT);
	
	/**
	 * Creates a new electric field. Default strength is {@link #FIELD_CONSTANT}
	 * and default background field is 0.
	 */
	public Electrostatic() {
		this(FIELD_CONSTANT, null);
	}
	
	/**
	 * Creates a new electric field. Field strength can be anything, and backgroundField can 
	 * be null (in which case it is set to 0).
	 * @param fieldStrength field strength [N*m^2/C^2]
	 * @param backgroundField background electric field [N/C]
	 */
	public Electrostatic(double fieldStrength, VectorField backgroundField) {
		this.fieldStrength = fieldStrength;
		this.backgroundField = backgroundField;
	}
	
	private double fieldStrength;
	private VectorField backgroundField;
	
	private Vector fa = new CVector(0,0,0); // force on particle a (this gets recycled)
	@Override
	public void applyTo(Spacetime s) {
		List<Body> particles = s.particles();
		Body a, b;
		for (int i = 0; i < particles.size(); i++) {
			a = particles.get(i);
			
			// apply force from backgound field (only once per particle)
			if (backgroundField != null)
				a.applyForce(backgroundField.getVectorAt(a.position()).scale(a.getCharge()));
			
			// apply inter-particle forces
			for (int j = i+1; j < particles.size(); j++) {
				b = particles.get(j);
				fa.difference(a.position(), b.position())
					.scale(fieldStrength * a.getCharge() * b.getCharge() / 
							Math.pow(Vector.distanceSq(a.position(), b.position()), 1.5));
				a.applyForce(fa);
				b.applyForce(fa.scale(-1));
			}
		}
	}

}
