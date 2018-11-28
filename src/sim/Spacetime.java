package sim;

import java.util.ArrayList;
import java.util.List;

import bodies.Body;
import forces.Force;
import graphics.GraphicsInterface;
import vector.Vector;

public abstract class Spacetime {
	
	public Spacetime(double dt) {
		this.dt = dt;
	}
	
	protected double dt;
	protected ArrayList<Body> particles = new ArrayList<>(50);
	protected ArrayList<Force> forces = new ArrayList<>();
	
	public abstract void update();
	protected abstract void updatePositions(double dt);
	protected abstract void updateVelocities(double dt);
	protected abstract void updateAccelerations();
	
	protected void updateForces() {
		for (var p : particles) {
			p.zeroForce();
		}
		
		for (Force f : forces)
			f.applyTo(this);
	}
	
	public double dt() {
		return dt;
	}
	
	public void setDT(double dt) {
		this.dt = dt;
	}
	
	public void add(Body...particles) {
		for (var p : particles)
			this.particles.add(p);
	}
	
	public void add(Force...forces) {
		for (Force f : forces)
			this.forces.add(f);
	}
	
	public List<Body> particles() {
		return particles;
	}
	
	public List<Force> forces() {
		return forces;
	}
	
	public abstract void render(GraphicsInterface gu);
	public abstract double getTimeAt(Vector position);

}
