package bodies;

import graphics.GraphicsInterface;
import shapes.Orientation;
import shapes.Polyhedron;
import vector.Matrix3x3;
import vector.Quaternion;
import vector.Vector;

public class PolyBody extends RigidBody {

	public PolyBody(double mass, Polyhedron body, Vector position, Vector velocity) {
		super(mass, position, velocity);
		setBody(body);
	}
	
	protected Polyhedron body;
	
	@Override
	public void renderAt(GraphicsInterface g, Vector position) {
		g.drawPolyhedronOutline(body, position);
	}
	
	public void setBody(Polyhedron p) {
		if (body == p) return;
		
		body = p;
		volume = body.getVolume();
		radius = body.getRadiusOfBoundingSphere();
		density = mass / volume;
		
		updateInertiaTensor();
	}
	
	@Override
	public void rotate(Quaternion q) {
		body.rotate(q);
	}
	
	@Override
	public double getCrossSection(Vector axis) {
		return body.getCrossSection(axis);
	}
	@Override
	public double getVolume() {
		return body.getVolume();
	}
	
	@Override
	public Orientation orientation() {
		return body.orientation();
	}
	
	@Override
	public void setDensityPreserveMass(double d) {
		density = d;
		// TODO scale the poly accordingly
	}
	
	@Override
	public void updateInertiaTensor() {
		inertiaTensor = new Matrix3x3();
		for (var vertex : body.vertices()) {
			double x = vertex.x(), y = vertex.y(), z = vertex.z();
			inertiaTensor.elements[0][0] += y*y + z*z;
			inertiaTensor.elements[1][1] += x*x + z*z;
			inertiaTensor.elements[2][2] += x*x + y*y;
			inertiaTensor.elements[0][1] = inertiaTensor.elements[1][0] -= x*y;
			inertiaTensor.elements[0][2] = inertiaTensor.elements[2][0] -= x*z;
			inertiaTensor.elements[1][2] = inertiaTensor.elements[2][1] -= y*z;
		}
		inertiaTensor.scale(mass);
		
		inertiaTensorInv = inertiaTensor.getInverse();
	}

}
