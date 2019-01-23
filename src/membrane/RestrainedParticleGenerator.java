package membrane;

import java.awt.Point;
import java.util.function.Function;

import vector.CVector;
import vector.Vector;

public class RestrainedParticleGenerator implements Function<Point, RestrainedParticle> {

	/**
	 * @param mdf  Mass Distribution Function
	 * @param cdf  Charge Distribution Function
	 * @param iddf Initial Displacement Distribution Function
	 * @param ivdf Initial Velocity Distribution Function
	 */
	public RestrainedParticleGenerator(Function<Point, Double> mdf, Function<Point, Double> cdf, Function<Point, Double> iddf,
			Function<Point, Double> ivdf) {
		this.mdf = mdf;
		this.cdf = cdf;
		this.iddf = iddf;
		this.ivdf = ivdf;
	}

	protected Function<Point, Double> mdf, cdf, iddf, ivdf;
	private Vector dof = null;
	private Vector pos = null;

	/**
	 * Sets the degree of freedom of generated particles. It should be a unit
	 * vector. It is neither mutated nor copied.
	 * 
	 * @param dof the specified degree of freedom
	 */
	public void setDOF(Vector dof) {
		this.dof = dof;
	}

	/**
	 * Sets the origin from which generated particles are displaced. The passed
	 * vector is not copied; changes to it are reflected in the positions of
	 * generated particles. In other words, it should be mutated between calls of
	 * {@link #apply(Point)}.
	 * 
	 * @param pos the specified origin
	 */
	public void setPos(Vector pos) {
		this.pos = pos;
	}

	@Override
	public RestrainedParticle apply(Point p) {
		return new RestrainedParticle(mdf.apply(p), cdf.apply(p), getParticlePosition(p), getParticleVelocity(p), dof);
	}

	/**
	 * @param p the indices of the particle
	 * @return the absolute position of the pth particle
	 */
	protected Vector getParticlePosition(Point p) {
		return new CVector(pos).translateScaled(dof, iddf.apply(p));
	}

	/**
	 * @param p the indices of the particle
	 * @return the absolute velocity of the pth particle
	 */
	protected Vector getParticleVelocity(Point p) {
		return new CVector(dof).scale(ivdf.apply(p));
	}
	
	/**
	 * @return a duplicate of the dof
	 */
	protected Vector getDOF() {
		return new CVector(dof);
	}

}
