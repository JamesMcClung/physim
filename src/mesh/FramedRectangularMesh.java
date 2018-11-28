package mesh;

import java.awt.Point;
import java.util.function.Function;

import vector.CVector;
import vector.Vector;

public class FramedRectangularMesh extends RectangularMesh {

	public FramedRectangularMesh(int w, int h, Vector pos, Vector delX, Vector delY,
			Function<Point, Double> massDistributionFunction,
			Function<Point, Double> horizontalStrengthDistributionFunction,
			Function<Point, Double> verticalStrengthDistributionFunction) {
		super(w, h, pos, delX, delY, massDistributionFunction, null, null);
		
		makeFrame(pos, delX, delY, massDistributionFunction);
		makeTethers(horizontalStrengthDistributionFunction, verticalStrengthDistributionFunction);
	}
	
	public FramedRectangularMesh(int w, int h, Vector pos, Vector delX, Vector delY, double particleMass, double tetherStrength) {
		this(w, h, pos, delX, delY, (p) -> particleMass, (p) -> tetherStrength, (p) -> tetherStrength);
	}
	
	protected void makeFrame(Vector pos, Vector delX, Vector delY, Function<Point, Double> massDistributionFunction) {
		Vector poleAxis = delX.cross(delY);
		poleAxis.scale(3); // this is entirely arbitrary and only makes it look better in my opinion
		
		Point p1 = new Point();
		Point p2 = new Point();
		Vector cursor1 = new CVector(pos);
		Vector cursor2 = new CVector(pos).translateScaled(delY, height-1);
		
		// instantiate top and bottom poles
		p1.y = 0;
		p2.y = height - 1;
		for (p1.x = p2.x = 0; p1.x < width; p1.x++, p2.x++) {
			particles[p1.x][p1.y] = new Pole(massDistributionFunction.apply(p1), 0, cursor1, null, poleAxis);
			cursor1.translate(delX);
			
			particles[p2.x][p2.y] = new Pole(massDistributionFunction.apply(p2), 0, cursor2, null, poleAxis);
			cursor2.translate(delX);
		}
		
		cursor1.become(pos);
		cursor2.become(pos).translateScaled(delX, width-1);
		
		// instantiate left and right poles
		p1.x = 0;
		p2.x = width - 1;
		for (p1.y = p2.y = 0; p1.y < height; p1.y++, p2.y++) {
			particles[p1.x][p1.y] = new Pole(massDistributionFunction.apply(p1), 0, cursor1, null, poleAxis);
			cursor1.translate(delY);
			
			particles[p2.x][p2.y] = new Pole(massDistributionFunction.apply(p2), 0, cursor2, null, poleAxis);
			cursor2.translate(delY);
		}
	}
	
	
}
