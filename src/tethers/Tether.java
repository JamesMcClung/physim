package tethers;

import bodies.Particle;
import forces.Force;
import graphics.GraphicsInterface;

public abstract class Tether implements Force {
	
	protected double length;
	protected Particle p1, p2;
	
	public void fasten(Particle p1, Particle p2) {
		if (p1 != null)
			this.p1 = p1;
		if (p2 != null)
			this.p2 = p2;
	}
	
	public Tether setLength(double length) {
		this.length = length;
		return this;
	}
	
	@Override
	public abstract void renderForce(GraphicsInterface gu);
	
	public double x1() {
		return p1.position().x();
	}
	
	public double y1() {
		return p1.position().y();
	}
	
	public double x2() {
		return p2.position().x();
	}
	
	public double y2() {
		return p2.position().y();
	}

}
