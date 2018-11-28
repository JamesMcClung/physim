package bodies;

import vector.Quaternion;
import vector.Vector;

public class CentripetalForce {
	
	public CentripetalForce(Vector centerOfRotation, Vector axis, double angularSpeed) {
		this.centerOfRotation = centerOfRotation;
		this.axis = axis;
		this.angularSpeed = angularSpeed;
	}
	
	private Vector centerOfRotation;
	private Vector axis;
	private double angularSpeed;
	
	public void applyTo(Particle p, double dt) {
		Vector r = p.position().difference(centerOfRotation);
		Quaternion.rotateAroundAxis(r, dt * angularSpeed, axis);
		p.position().become(centerOfRotation.sum(r));
	}

	public Vector getForce(Particle p) {
		return null;
	}
	
}
