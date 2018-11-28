package mesh;

import java.awt.Point;
import java.util.function.Function;

import vector.CVector;
import vector.Vector;

public class SupportedRectangularMesh extends RectangularMesh {

	public SupportedRectangularMesh(int w, int h, Vector pos, Vector delX, Vector delY,
			Function<Point, Double> massDistributionFunction,
			Function<Point, Double> horizontalStrengthDistributionFunction,
			Function<Point, Double> verticalStrengthDistributionFunction) {
		super(w, h, pos, delX, delY, massDistributionFunction, null, null);
		
		Vector poleAxis = delX.cross(delY);
		poleAxis.scale(3); // this is entirely arbitrary and only makes it look better in my opinion

		Point p = new  Point();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				p.x = i * (w-1);
				p.y = j * (h-1);
				particles[p.x][p.y] = new Pole(massDistributionFunction.apply(p), 0, new CVector(pos).translateScaled(delX, p.x).translateScaled(delY, p.y), null, poleAxis);
			}
		}
		
		makeTethers(horizontalStrengthDistributionFunction, verticalStrengthDistributionFunction);
	}
	
	public SupportedRectangularMesh(int w, int h, Vector pos, Vector delX, Vector delY, double particleMass, double tetherStrength) {
		this(w, h, pos, delX, delY, (p) -> particleMass, (p) -> tetherStrength, (p) -> tetherStrength);
	}

}
