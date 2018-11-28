package rocketry;

import vector.Vector;

public class SolarThruster extends Thruster {

	SolarThruster(double fuelMass, double exhaustVelocity, double fuelConsumptionRate, double fuelRechargeRate, Vector thrustDir) {
		super(fuelMass, exhaustVelocity, fuelConsumptionRate, thrustDir);
		this.fuelRechargeRate = fuelRechargeRate;
	}
	
	private double fuelRechargeRate;
	
	@Override
	public void tick(double dt) {
		addFuel(fuelRechargeRate * dt);
	}

}
