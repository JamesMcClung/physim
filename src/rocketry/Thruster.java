package rocketry;

import vector.CVector;
import vector.Vector;

public class Thruster {
	
	public Thruster(double fuelMass, double exhaustSpeed, double fuelConsumptionRate, Vector thrustDir) {
		fuelCapacity = this.fuelMass = fuelMass;
		this.exhaustSpeed = exhaustSpeed;
		this.fuelConsumptionRate = fuelConsumptionRate;
		thrust = exhaustSpeed * fuelConsumptionRate;
		thrustVec = new CVector(thrustDir).setMagnitude(thrust);
	}
	
	private final double fuelCapacity;
	private double fuelMass;
	private double fuelPercentRemaining = 1;
	private double exhaustSpeed, fuelConsumptionRate;
	private double thrust; // in N; thrust is the product of exhaust velocity and fuel consumption rate
	private Vector thrustVec; // the vector representing the thrust
	
	/**
	 * Consumes fuel in order to generate thrust.
	 * @param dt time elapsed, in seconds
	 * @return thrust generated, in newtons
	 */
	public Vector burnFuelGetThrust(double dt) {
		double fuelConsumed = fuelConsumptionRate * dt;
		
		
		if (fuelMass == 0) {
			return new CVector();
		} else if (fuelMass < fuelConsumed) {
			fuelMass = 0;
			fuelPercentRemaining = 0;
			return getThrustVec().scale(fuelConsumed / fuelMass);
		} else {
			removeFuel(fuelConsumed);
			return getThrustVec();
		}
	}
	
	public double getFuelPercentRemaining() {
		return fuelPercentRemaining;
	}
	
	public double getFuelMass() {
		return fuelMass;
	}
	
	public double getThrust() {
		return thrust;
	}
	
	public Vector getThrustVec() {
		return new CVector(thrustVec);
	}
	
	public double getExhaustSpeed() {
		return exhaustSpeed;
	}
	
	public void tick(double dt) {
		// does nothing by default
	}
	
	public void addFuel(double amount) {
		fuelMass = Math.min(fuelMass + amount, fuelCapacity);
		fuelPercentRemaining = fuelMass / fuelCapacity;
	}
	
	public void removeFuel(double amount) {
		fuelMass = Math.max(fuelMass - amount, 0);
		fuelPercentRemaining = fuelMass / fuelCapacity;
	}
	
}
