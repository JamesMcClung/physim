package bodies;

import java.awt.Color;

import vector.CVector;
import vector.Vector;
import vector.VectorField;

public class Tornado extends FixedPoint implements VectorField {

	/**
	 * Creates a new tornado. Mass and charge are 0.
	 * @param windSpeed wind speed (magnitude) [m/s]
	 * @param position position of center
	 * @param velocity velocity of center
	 */
	public Tornado(double windSpeed, Vector position, Vector velocity) {
		super(1, 0, position, velocity);
		setColor(Color.LIGHT_GRAY);
		this.windSpeed = windSpeed;
	}
	
	private double windSpeed;

	@Override
	public Vector getVectorAt(Vector position) {
		return new CVector(
				windSpeed * (this.position.y() - position.y()),
				windSpeed * (position.x() - this.position.x()),
				0)
				.scale(1/Vector.distance(position, this.position));
	}

}
