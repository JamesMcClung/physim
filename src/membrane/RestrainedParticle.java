package membrane;

import java.awt.Color;
import java.util.function.Function;

import bodies.Particle;
import vector.Vector;

/**
 * 
 * A RestrainedParticle acts like a normal particle, except its movement is limited to only certain degrees
 * of freedom. The particle can freely move in any direction listed as a degree of freedom.
 * <p>
 * Specifically, the velocity is restrained. Position is thus restrained via velocity.
 * <p>
 * The color can also change as a function of position. Obviously, this is purely aesthetic.
 * 
 * @author james
 *
 */
public class RestrainedParticle extends Particle {

	/**
	 * Creates a new RestrainedParticle from the given parameters.
	 * @param mass the mass, kg
	 * @param charge the charge, C
	 * @param position the initial position, m
	 * @param velocity the initial velocity, m/s
	 * @param dof degrees of freedom, which are used directly, will not be mutated, must not be mutated, and must be unit vectors 
	 */
	public RestrainedParticle(double mass, double charge, Vector position, Vector velocity, Vector...dof) {
		super(mass, charge, position, velocity);
		this.dof = dof;
		for (var v : dof)
			v.normalize();
	}
	
	/**
	 * Allowable degrees of freedom. All unit vectors.
	 */
	private final Vector[] dof;
	private Function<Vector, Color> gradient = null;
	
	@Override
	public void updatePosition(double dt) {
		super.updatePosition(dt);
		if (gradient != null)
			setColor(gradient.apply(position));
	}
	
	@Override
	public void updateVelocity(double dt) {
		for (Vector n : dof) {
			velocity.translateScaled(n, n.dot(acceleration) * dt);
		}
	}
	
	public void applyGradient(Function<Vector, Color> g) {
		gradient = g;
		setColor(g.apply(position));
	}

}
