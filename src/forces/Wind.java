package forces;

import bodies.Body;
import sim.Spacetime;
import vector.CVector;
import vector.Vector;
import vector.VectorField;

public class Wind implements Force {

	/**
	 * Density of air at surface of Earth. Units: kg/m^3
	 */
	public static final double EARTH_ATMOSPHERIC_DENSITY = 1.225;

	/**
	 * Approximate drag coefficient of a (large, rough) sphere
	 */
	public static final double DRAG_COEFFICIENT_SPHERE = 0.47;

	/**
	 * Creates a new wind.
	 * 
	 * @param airDensity density of air [kg/m^3]
	 * @param dragCoef   drag coefficient
	 * @param windSpeed  wind speed [m/s]. Can be null.
	 */
	public Wind(double airDensity, double dragCoef, VectorField windSpeed) {
		setStrength(airDensity, dragCoef);
		this.windSpeed = windSpeed;
	}

	/**
	 * Creates a new wind. Air density and drag coefficients default to
	 * {@link #EARTH_ATMOSPHERIC_DENSITY} and {@link #DRAG_COEFFICIENT_SPHERE},
	 * respectively.
	 * 
	 * @param windSpeed wind speed [m/s]. Can be null.
	 */
	public Wind(VectorField windSpeed) {
		this(EARTH_ATMOSPHERIC_DENSITY, DRAG_COEFFICIENT_SPHERE, windSpeed);
	}

	/**
	 * Creates a new stationary wind with the specified strength. Air density is
	 * calculated; drag coefficients are kept normal.
	 * 
	 * @param strength the strength
	 */
	public Wind(double strength) {
		this();
		setStrength(strength);
	}

	/**
	 * Creates a new stationary wind. Air density and drag coefficients default to
	 * {@link #EARTH_ATMOSPHERIC_DENSITY} and {@link #DRAG_COEFFICIENT_SPHERE},
	 * respectively.
	 */
	public Wind() {
		this(null);
	}

	/**
	 * Creates a new wind.
	 * 
	 * @param strength  consolidated coefficient for calculating force.
	 * @param windSpeed wind speed [m/s]. Can be null.
	 */
	public Wind(double strength, VectorField windSpeed) {
		setStrength(strength);
		this.windSpeed = windSpeed;
	}

	private double airDensity, dragCoef;
	private double strength; // := -0.5 * airDensty * dragCoef
	protected VectorField windSpeed;

	public void setStrength(double airDensity, double dragCoef) {
		this.airDensity = airDensity;
		this.dragCoef = dragCoef;
		strength = -0.5 * airDensity * dragCoef;
	}

	/**
	 * Strength is the coefficient used in calculating force. Air density is recalculated, while drag coefficient is kept normal.
	 * 
	 * @param strength, a positive number
	 */
	public void setStrength(double strength) {
		this.strength = -strength;
		dragCoef = DRAG_COEFFICIENT_SPHERE;
		airDensity = 2 * strength / dragCoef;
	}

	public double getAirDensity() {
		return airDensity;
	}

	public double getDragCoef() {
		return dragCoef;
	}

	public void setAirDensity(double airDensity) {
		setStrength(airDensity, dragCoef);
	}

	public void setDragCoefficient(double dragCoef) {
		setStrength(airDensity, dragCoef);
	}

	protected Vector fp = new CVector(); // force on particle p (recycled)

	@Override
	public void applyTo(Spacetime s) {
		for (var p : s.particles()) {
			if (p instanceof Body) {
				if (windSpeed == null)
					fp.become(p.velocity());
				else
					fp.difference(p.velocity(), windSpeed.getVectorAt(p.position()));

				fp.scale(strength * p.getCrossSection(fp) * fp.magnitude());
				p.applyForce(fp);
			}
		}
	}

}
