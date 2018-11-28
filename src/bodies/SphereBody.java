package bodies;

import graphics.GraphicsInterface;
import shapes.Orientation;
import util.MiscUtil;
import vector.Matrix3x3;
import vector.Quaternion;
import vector.Vector;

public class SphereBody extends RigidBody {

	public SphereBody(double mass, double radius, Vector position, Vector velocity) {
		super(mass, position, velocity);
		setRadius(radius);
	}
	
	private double crossSection;
	private Orientation orientation = new Orientation();
	
	public void render(GraphicsInterface g) {
		if (g.getRelativePosition(position).magnitudeSq() < MiscUtil.square(getRadius()))
			return;
		super.render(g);
	}
	
	public void setRadius(double r) {
		if (r < 0)
			throw new RuntimeException("Negative radius ("+r+") is not allowed");
		radius = r;
		crossSection = Math.PI * r*r;
		volume = crossSection * 4/3 * r;
		density = mass / volume;
		updateInertiaTensor();
	}
	
	@Override
	public void setDensityPreserveMass(double d) {
		density = d;
		volume = mass/density;
		radius = Math.cbrt(volume/(Math.PI * 4/3));
		crossSection = Math.PI * radius*radius;
		updateInertiaTensor();
	}

	@Override
	public void renderAt(GraphicsInterface g, Vector position) {
		g.fillCircle(position, radius);
	}

	@Override
	public void rotate(Quaternion q) {
		orientation.rotate(q);
	}

	@Override
	public double getCrossSection(Vector axis) {
		return crossSection;
	}

	@Override
	public double getVolume() {
		return volume;
	}

	@Override
	public Orientation orientation() {
		return orientation;
	}
	
	@Override
	public void updateInertiaTensor() {
		inertiaTensor = new Matrix3x3();
		double diagVal = mass * radius*radius * 2/5;
		for (int i = 0; i < 3; i++)
			inertiaTensor.elements[i][i] = diagVal;
		
		inertiaTensorInv = inertiaTensor.getInverse();
	}

}
