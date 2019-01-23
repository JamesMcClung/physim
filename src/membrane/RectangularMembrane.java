package membrane;

import static vector.CVector.vec;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import bodies.FixedPoint;
import forces.Force;
import sim.Spacetime;
import sim.PSystem;
import vector.CVector;
import vector.Vector;

/**
 * A membrane is a structure consisting of particles that are restricted to one
 * degree of freedom, and particles experience a correcting force that is a
 * function of average relative displacement.
 * <p>
 * A RectangularMembrane is a membrane that is rectangular.
 * 
 * @author James McClung
 *
 */
public class RectangularMembrane implements PSystem, Force {

//	public static final int DRAW_OPTIMIZATION_THRESHOLD = 25;

	/**
	 * Creates a new rectangular membrane.
	 * 
	 * @param w     the width, or number of columns
	 * @param h     the height, or number of rows
	 * @param pos   position of top left corner of the membrane
	 * @param delX  distance between columns
	 * @param delY  distance between rows
	 * @param pdf   Particle Distribution Function
	 * @param force the force function, which takes a displacement (m) and returns
	 *              the force (N)
	 * 
	 * @see #setFixedEdges(boolean)
	 * @see #generateGradient(ColorGradient)
	 */
	public RectangularMembrane(int w, int h, Vector pos, Vector delX, Vector delY, RestrainedParticleGenerator pdf,
			Function<Double, Double> force, boolean fixedEdges) {
		this.fixedEdges = fixedEdges;
		
		width = w;
		height = h;
		position = new CVector(pos);

		this.delX = new CVector(delX);
		this.delY = new CVector(delY);
		dof = this.delX.cross(this.delY).normalize();

		this.force = force;

		this.generator = pdf;
		particles = new RestrainedParticle[w][h];
		absoluteDisplacements = new double[w][h];
		meanRelativeDisplacements = new double[w][h];

		posDisplacement = position.dot(dof);

		makeParticles();
	}

	public RectangularMembrane(int w, int h, Vector pos, Vector delX, Vector delY, double particleMass, double strength,
			boolean fixedEdges) {
		this(w, h, pos, delX, delY,
				new RestrainedParticleGenerator((p) -> particleMass, (p) -> 0d, (p) -> 0d, (p) -> 0d),
				(d) -> -strength * d, fixedEdges);
	}

	public final int width, height;
	private final Function<Double, Double> force;

	private final Vector position;
	private final Vector delX, delY;
	private final Vector dof;

	protected final RestrainedParticle[][] particles;
	private final double[][] absoluteDisplacements;
	private final double[][] meanRelativeDisplacements;
	private boolean fixedEdges;
	private final double posDisplacement; // only used if the edges are fixed

	private final RestrainedParticleGenerator generator;

	private ColorGradient gradient;

	/**
	 * Instantiates a new set of particles for the mesh, overriding previous ones.
	 */
	protected void makeParticles() {
		Vector cursor1 = new CVector(position);
		Vector cursor2 = new CVector(position);

		generator.setPos(cursor2);
		generator.setDOF(dof);

		Point p = new Point();

		for (p.x = 0; p.x < width; p.x++) {
			cursor2.become(cursor1);

			for (p.y = 0; p.y < height; p.y++) {
				particles[p.x][p.y] = generator.apply(p);
				cursor2.translate(delY);
			}

			cursor1.translate(delX);
		}
	}

	@Override
	public void addTo(Spacetime s) {
		for (var line : particles)
			s.add(line);
		s.add(this);
	}

	@Override
	public void applyTo(Spacetime s) {
		updateAbsoluteDisplacements();
		updateMeanRelativeDisplacements();

//		Vector force = new CVector(dof);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
//				force.setMagnitude(getForceOnParticle(i, j));
				particles[i][j].applyForce(new CVector(dof).scale(getForceOnParticle(i, j)));
			}
		}
	}

	/**
	 * Calculates the force on the (i, j)th particle. The force is a function of
	 * mean relative displacement, and is typically of the opposite sign.
	 * 
	 * @param i horizontal index of particle
	 * @param j vertical index of particle
	 * @return the force
	 */
	private double getForceOnParticle(int i, int j) {
		return force.apply(meanRelativeDisplacements[i][j]);
	}

	/**
	 * Recalculates each particle's absolute displacement from the membrane's
	 * initial position.
	 */
	private void updateAbsoluteDisplacements() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				absoluteDisplacements[i][j] = particles[i][j].position().dot(dof);
			}
		}
	}

	/**
	 * Recalculates each particle's average relative displacement. If
	 * {@link #fixedEdges} is <code>true</code>, then the edge is considered a
	 * neighbor.
	 */
	private void updateMeanRelativeDisplacements() {
		// calculate the total relative displacements first
		double relD;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// reset the value
				meanRelativeDisplacements[i][j] = 0;

				if (fixedEdges) {
					
					// displacement relative to left or right edge
					if (i == 0 || i == width - 1)
						meanRelativeDisplacements[i][j] += absoluteDisplacements[i][j] - posDisplacement;
					
					// displacement relative to top or bottom
					if (j == 0 || j == height - 1)
						meanRelativeDisplacements[i][j] += absoluteDisplacements[i][j] - posDisplacement;
				}

				// displacement relative to left neighbor
				if (i > 0) {
					relD = absoluteDisplacements[i][j] - absoluteDisplacements[i - 1][j];
					meanRelativeDisplacements[i][j] += relD;
					meanRelativeDisplacements[i - 1][j] -= relD;
				}

				// displacement relative to upper neighbor
				if (j > 0) {
					relD = absoluteDisplacements[i][j] - absoluteDisplacements[i][j - 1];
					meanRelativeDisplacements[i][j] += relD;
					meanRelativeDisplacements[i][j - 1] -= relD;
				}
			}
		}

		// normalize relative displacements (set them to the average displacement
		// instead of total displacement)
		if (fixedEdges) {
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					meanRelativeDisplacements[i][j] /= 4;
		} else {
			int nNeighbors;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					nNeighbors = 4;
					if (i == 0 || i == width - 1)
						nNeighbors--;
					if (j == 0 || j == height - 1)
						nNeighbors--;
					meanRelativeDisplacements[i][j] /= nNeighbors;
				}
			}
		}
	}

	/**
	 * @return the number of particles in this membrane
	 */
	public int getNumParticles() {
		return width * height;
	}

	public void setFixedEdges(boolean b) {
		fixedEdges = b;
	}

	/**
	 * Generates a gradient for the membrane.
	 * 
	 * @param c1                   the color "beneath" the membrane
	 * @param c2                   the color "above" the membrane
	 * @param expectedDisplacement the minimum displacement needed to fully achieve
	 *                             either color
	 */
	public void generateGradient(Color c1, Color c2, double expectedDisplacement) {
		gradient = new ColorGradient(position, c1, c2, dof, expectedDisplacement);

		for (var line : particles)
			for (var p : line)
				p.applyGradient(gradient);
	}

	public static class Presets {

		public static RectangularMembrane getDoubleSlitExperiment() {
			return getSlitExperiment(2, 2);
		}

		/**
		 * Creates a membrane with a slitted wall, an initial ripple, and displacement
		 * detectors.
		 * 
		 * @param nSlits      the number of slits in the wall
		 * @param slitSpacing the number of wall particles between slits
		 * @return the membrane
		 */
		public static RectangularMembrane getSlitExperiment(int nSlits, int slitSpacing) {
			final int width = 200, length = 50;

			final int wallY = length * 3 / 4;
			final int holeX = width / 2;

			final int sourceY = 0;
			final double sourceMag = 2;

			final int measureY = length - 1;

			final List<Integer> slitXs = new ArrayList<>(nSlits);
			final boolean evenNSlits = nSlits % 2 == 0;
			int x1 = evenNSlits ? (slitSpacing + 1) / 2 : 0;
			int x2 = (slitSpacing % 2 == 0 ? -1 : 0) - x1;
			for (int i = 0; i < nSlits; i++, x1 += slitSpacing, x2 -= slitSpacing) {
				slitXs.add(holeX + x1);
				if (x1 != 0) {
					slitXs.add(holeX + x2);
					i++;
				}
			}

			RestrainedParticleGenerator generator = new RestrainedParticleGenerator((p) -> 1d, (p) -> 0d,
					(p) -> p.y == sourceY ? sourceMag : 0d, (p) -> 0d) {
				@Override
				public RestrainedParticle apply(Point p) {
					if (p.y == wallY && !slitXs.contains(p.x))
						return new FixedPoint(1, getParticlePosition(p), getParticleVelocity(p));
					else if (p.y == measureY)
						return new MeasuringParticle(mdf.apply(p), cdf.apply(p), getParticlePosition(p),
								getParticleVelocity(p), getDOF());
					return super.apply(p);
				}
			};

			final double forceStrength = 100;
			Function<Double, Double> force = (d) -> -forceStrength * d;

			final double spacing = 1d / width;

			var mem = new RectangularMembrane(width, length, null, vec(spacing, 0, 0), vec(0, 0, spacing), generator,
					force, false);
			mem.generateGradient(Color.RED, Color.GREEN, .1);

			return mem;
		}

	}
}
