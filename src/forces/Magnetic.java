package forces;

import sim.Spacetime;
import vector.CVector;
import vector.Vector;
import vector.VectorField;

public class Magnetic implements Force {
	
	public Magnetic(double fieldStrength, VectorField backgroundField) {
		this.fieldStrength = fieldStrength;
		this.backgroundField = (backgroundField == null ? (pos) -> new CVector() : backgroundField);
	}
	
	public Magnetic(VectorField backgroundField) {
		this(0, backgroundField);
	}
	
	public Magnetic() {
		this(0, null); // what generates a magnetic field?
	}
	
	private double fieldStrength;
	private VectorField backgroundField;
	
	private boolean useCorrection1 = false; // TODO make the correction work
	private boolean useCorrection2 = false;

	private Vector f1 = new CVector(); // force on particle p (this gets recycled)
	private Vector v2 = new CVector(); // uncorrected velocity after f1 is applied
	private Vector fNet = new CVector(); // actual applied force once corrections are made
	@Override
	public void applyTo(Spacetime s) {
		// Apply force to each particle. See the math.
		for (var p : s.particles()) {
			Vector vi = p.velocity();
			Vector b = backgroundField.getVectorAt(p.position());
			f1.cross(vi, b).scale(p.getCharge());
			
			if (useCorrection1) {
				v2.become(vi).translateScaled(f1, s.dt()/p.getMass());
				fNet.become(v2).scale(vi.magnitude() / v2.magnitude())
						.translateScaled(vi, -1)
						.scale(p.getMass() / s.dt());
				
				// correction to ensure that v is constant:
				// vf = vi + F/m*dt
				//	  = vi + (f1 + f2)/m*dt
				//	  = vi + (f1 + f2)/m*dt
				
				// f1 = q * vi x B
				// v2 = vi + f1/m*dt
				// f2 = m/dt * (vf - vi) - f1
				// vf = v2 * |vi| / |v2|
				// f2 = m/dt * (v2 * |vi| / |v2| - vi) - f1
				// F = m/dt * (v2 * |vi| / |v2| - vi)
				
				p.applyForce(fNet); // this does not work as intended (particle loses speed in perp dir)
			} else if (useCorrection2) {
				
			} else 
				p.applyForce(f1); // this does not work as intended (particle gains speed)
		}
	}

}
