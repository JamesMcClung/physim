package sim;

import forces.Force;
import graphics.GraphicsInterface;
import vector.Vector;

public class NSpace extends Spacetime {
	
	public NSpace(double dt) {
		super(dt);
	}
	
	private double globalTime = 0;

	/**
	 * Jumps to the next time increment, updating all particles and stuff.
	 * <p>
	 * Order: position -> forces -> acceleration -> velocity
	 */
	public void update() {
		globalTime += dt;
		updateForces();
		updateAccelerations();
		updateVelocities(dt);
		updatePositions(dt);
	}
	
	@Override
	public void render(GraphicsInterface gu) {
		for (Force f : forces) {
			f.renderForce(gu);
		}
		
		for (var p : particles) {
			p.render(gu);
		}
	}

	@Override
	protected void updatePositions(double dt) {
		for (var p : particles) {
			p.updatePosition(dt);
		}
	}

	@Override
	protected void updateVelocities(double dt) {
		for (var p : particles) {
			p.updateVelocity(dt);
		}
	}

	@Override
	protected void updateAccelerations() {
		for (var p : particles) {
			p.updateAccelerations();
		}
	}

	@Override
	public double getTimeAt(Vector position) {
		return globalTime;
	}
}
