package bodies;

import membrane.RestrainedParticle;
import vector.Vector;

public class FixedPoint extends RestrainedParticle {

	public FixedPoint(double mass, double charge, Vector position, Vector velocity) {
		super(mass, charge, position, velocity);
	}
	
	public FixedPoint(double mass, Vector position, Vector velocity) {
		this(mass, 0, position, velocity);
	}
	
	/**
	 * This does nothing.
	 * @param force the force being applied
	 */
	@Override
	public void applyForce(Vector force) {
		// do nothing
	}
	
	/**
	 * This does nothing.
	 */
	@Override
	public void updateAccelerations() {
		// do nothing
	}

}
