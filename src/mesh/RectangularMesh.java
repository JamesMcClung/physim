package mesh;

import java.awt.Point;
import java.util.function.Function;

import bodies.Particle;
import sim.Spacetime;
import sim.System;
import tethers.IdealRubberBand;
import tethers.Tether;
import vector.CVector;
import vector.Vector;

public class RectangularMesh implements System {
	
	public RectangularMesh(int w, int h, Vector pos, Vector delX, Vector delY, Function<Point, Double> massDistributionFunction, Function<Point, Double> horizontalStrengthDistributionFunction, Function<Point, Double> verticalStrengthDistributionFunction) {
		particles = new Particle[w][h];
		horizontalTethers = new Tether[w-1][h];
		verticalTethers = new Tether[w][h-1];
		width = w;
		height = h;
		
		makeParticles(pos, delX, delY, massDistributionFunction);
		makeTethers(horizontalStrengthDistributionFunction, verticalStrengthDistributionFunction);
	}
	
	public RectangularMesh(int w, int h, Vector pos, Vector delX, Vector delY, double particleMass, double tetherStrength) {
		this(w, h, pos, delX, delY, (p) -> particleMass, (p) -> tetherStrength, (p) -> tetherStrength);
	}
	
	public final int width, height;
	protected final Particle[][] particles;
	protected final Tether[][] horizontalTethers, verticalTethers;
	
	/**
	 * Instantiates a new set of particles for the mesh, overriding previous ones.
	 * @param pos the lower-left coordinate of the mesh
	 * @param delX the relative position of the particle "to the right" of another
	 * @param delY the relative position of the particle "above" another 
	 * @param massDistributionFunction used to determine each particle's mass as a function of its position in the mesh
	 */
	protected void makeParticles(Vector pos, Vector delX, Vector delY, Function<Point, Double> massDistributionFunction) {
		Vector cursor1 = new CVector(pos);
		
		Point p = new Point();
		
		for (p.x = 0; p.x < width; p.x++) {
			Vector cursor2 = new CVector(cursor1);
			
			for (p.y = 0; p.y < height; p.y++) {
				particles[p.x][p.y] = new Particle(massDistributionFunction.apply(p), cursor2, null);
				cursor2.translate(delY);
			}
			
			cursor1.translate(delX);
		}
	}
	
	/**
	 * Instantiates a new set of tethers between adjacent particles, overriding previous ones.
	 * No tethers are instantiated if the corresponding function is null, <i>nor are previous ones overridden.</i>
	 * @param horizontalStrengthDistributionFunction used to determine each horizontal tether's strength as a function of its position in the mesh
	 * @param verticalStrengthDistributionFunction used to determine each vertical tether's strength as a function of its position in the mesh 
	 */
	protected void makeTethers(Function<Point, Double> horizontalStrengthDistributionFunction, Function<Point, Double> verticalStrengthDistributionFunction) {
		Point p = new Point();
		
		// instantiate the horizontal tethers
		if (horizontalStrengthDistributionFunction != null) {
			int almostWidth = width-1;
			for (p.x = 0; p.x < almostWidth; p.x++) {
				for (p.y = 0; p.y < height; p.y++)
					horizontalTethers[p.x][p.y] = new IdealRubberBand(horizontalStrengthDistributionFunction.apply(p),particles[p.x][p.y],particles[p.x+1][p.y]);
			}
		}
		
		// instantiate the vertical tethers
		if (verticalStrengthDistributionFunction != null) {
			int almostHeight = height-1;
			for (p.y = 0; p.y < almostHeight; p.y++) {
				for (p.x = 0; p.x < width; p.x++)
					verticalTethers[p.x][p.y] = new IdealRubberBand(verticalStrengthDistributionFunction.apply(p), particles[p.x][p.y], particles[p.x][p.y+1]);
			}
		}
	}
	
	/**
	 * Adds all the current particles and tethers to the spacetime
	 * @param s the spacetime
	 */
	@Override
	public void addTo(Spacetime s) {
		for (var row : particles)
			s.add(row);
		for (var row : horizontalTethers)
			s.add(row);
		for (var row : verticalTethers)
			s.add(row);
	}

}
