package mesh;

import bodies.FixedPoint;
import graphics.GraphicsInterface;
import vector.CVector;
import vector.Vector;

public class Pole extends FixedPoint {

	public Pole(double mass, double charge, Vector position, Vector velocity, Vector axis) {
		super(mass, charge, position, velocity);
		this.axis = new CVector(axis);
	}
	
	private final Vector axis;
	
	@Override
	public void renderAt(GraphicsInterface g, Vector pos) {
		g.drawLine(pos.sum(axis), pos.difference(axis));
	}

}
