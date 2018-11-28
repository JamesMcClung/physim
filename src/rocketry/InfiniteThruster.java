package rocketry;

import vector.Vector;

public class InfiniteThruster extends Thruster {

	public InfiniteThruster(double thrust, Vector thrustDir) {
		super(0, thrust, 1, thrustDir);
	}
	
	@Override
	public Vector burnFuelGetThrust(double dt) {
		return getThrustVec();
	}
	
	@Override
	public double getFuelPercentRemaining() {
		return 1;
	}

}
