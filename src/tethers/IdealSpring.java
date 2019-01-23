package tethers;

import java.awt.Color;

import bodies.Body;
import graphics.GraphicsInterface;
import sim.Spacetime;
import vector.CVector;
import vector.Vector;

public class IdealSpring extends Tether {

	public IdealSpring(double stiffness, Body p1, Body p2, double length) {
		fasten(p1, p2);
		setLength(length);
		k = stiffness;
	}
	
	public IdealSpring(double stiffness, Body p1, Body p2) {
		this(stiffness, p1, p2, Vector.distance(p1.position(), p2.position()));
	}
	
	/**
	 * The stiffness (k = F/x)
	 */
	private double k; 

	private Vector force = new CVector();
	@Override
	public void applyTo(Spacetime s) {
		double x = Vector.distance(p1.position(), p2.position()) - length;
		
		force.difference(p1.position(), p2.position());
		force.setMagnitude(-k*x);
		
		p1.applyForce(force);
		p2.applyForce(force.scale(-1));
	}

	@Override
	public void renderForce(GraphicsInterface gu) {
		gu.setColor(Color.GRAY);
		gu.drawLine(p1.position(), p2.position());
	}

}
