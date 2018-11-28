package bodies;

import graphics.GraphicsInterface;
import vector.CVector;
import vector.Matrix3x3;
import vector.Quaternion;
import vector.Vector;

public abstract class RigidBody extends Body {

	public RigidBody(double mass, Vector position, Vector velocity) {
		super(mass, position, velocity);
		
		angularVelocity = new CVector();
		angularAcceleration = new CVector();
		netTorque = new CVector();
	}
	
	protected Matrix3x3 inertiaTensor, inertiaTensorInv;
	protected final Vector angularVelocity, angularAcceleration, netTorque;
	
	private Material material = null;
	protected double radius, volume, density;
	
	public void render(GraphicsInterface g) {
		g.setColor(getColor());
		if (radius * g.getPPM2(g.getRelativePosition(position)) < 1) {
			g.drawCircleAbsoluteRadius(position, MINIMUM_APPARENT_RADIUS);
		} else {
			renderAt(g, position);
		}
	}
	
	public double getDensity() {
		return density;
	}
	public void setDensityPreserveVolume(double d) {
		density = d;
		mass = volume * density;
		updateInertiaTensor();
	}
	public abstract void setDensityPreserveMass(double d);
	
	
	@Override
	public double getRadius() {
		return radius;
	}
	
	public Matrix3x3 inertiaTensor() {
		return inertiaTensor;
	}
	

	@Override
	public void setMass(double m) {
		mass = m;
		density = mass / volume;
		updateInertiaTensor();
	}
	
	
	public Vector angularVelocity() {
		return angularVelocity;
	}
	public Vector angularAcceleration() {
		return angularAcceleration;
	}
	public Vector netTorque() {
		return netTorque;
	}

	public void applyTorque(Vector torque) {
		netTorque.translate(torque);
	}
	
	@Override
	public void zeroForce() {
		super.zeroForce();
		netTorque.zero();
	}
	
	@Override
	public void updateAccelerations() {
		super.updateAccelerations();
		angularAcceleration.become(netTorque).transform(inertiaTensorInv);
	}
	
	@Override
	public void updatePosition(double dt) {
		super.updatePosition(dt);

		Quaternion rot = new Quaternion(1,0,0,0);
		
		if (angularVelocity.x() != 0)
			rot.translate(Quaternion.getRotationQuaternion(angularVelocity.x() * dt, orientation().xAxis()));
		if (angularVelocity.y() != 0)
			rot.translate(Quaternion.getRotationQuaternion(angularVelocity.y() * dt, orientation().yAxis()));
		if (angularVelocity.z() != 0)
			rot.translate(Quaternion.getRotationQuaternion(angularVelocity.z() * dt, orientation().zAxis()));

		rot.normalize();
		rotate(rot);
	}
	
	@Override
	public void updateVelocity(double dt) {
		super.updateVelocity(dt);
		angularVelocity.translateScaled(angularAcceleration, dt);
	}

	
	/**
	 * Sets the density and color of the particle to that of mat. Mass is conserved; radius changes.
	 * @param mat the material
	 * @return this
	 */
	public void setMaterial(Material mat) {
		material = mat;
		if (mat == null) return;
		setDensityPreserveMass(mat.density);
		setColor(mat.color);
	}
	
	public Material getMaterial() {
		return material;
	}
	
	protected abstract void updateInertiaTensor();
	
}
