package rocketry;

import bodies.Controllable;
import bodies.PolyBody;
import forces.Force;
import graphics.GraphicsInterface;
import input_util.BinaryState;
import input_util.KeyBinder;
import input_util.SymmetricalLever;
import shapes.Polyhedron;
import sim.Spacetime;
import util.CircularLinkedList;
import vector.CVector;
import vector.Vector;

public class Rocket extends PolyBody implements Controllable, Force {
	
	/**
	 * Creates a new rocket!
	 * @param shipMass mass of ship without fuel, in kg
	 * @param fuelMass mass of fuel, in kg
	 * @param exhaustVelocity velocity of exhaust, in m/s
	 * @param fuelConsumptionRate rate that fuel is consumed, in kg/s
	 * @param position initial position of ship, in m 
	 */
	public Rocket(double shipMass, Polyhedron body, Vector position, Vector velocity, Thruster...thrusters) {
		super(shipMass, body, position, velocity);
		for (var t : thrusters)
			this.thrusters.add(t);
	}
	
	@Override
	public void renderAt(GraphicsInterface g, Vector position) {
		super.renderAt(g, position);
//		g.drawLine(position, position.sum(thrusters.get().getThrustVec()).scale(1/200d));
	}
	
	@Override
	public void render(GraphicsInterface g) {
		super.render(g);
//		Vector end = position.sum(orientation().totalRotation.useToRotate2(thrusters.get().getThrustVec().setMagnitude(100 / g.getPPM(0))));
//		g.drawLine(position, end);
	}
	
	private CircularLinkedList<Thruster> thrusters = new CircularLinkedList<>();
	
	public double getFuelPercentRemaining() {
		if (thrusters.isEmpty()) return 0;
		return thrusters.get().getFuelPercentRemaining();
	}
	
	@Override
	public double getMass() {
		double totalMass = mass;
		for (var t : thrusters)
			totalMass += t.getFuelMass();
		return totalMass;
	}
	
	private final BinaryState isOn = new BinaryState();
	
	double torqueStrength = 1; // in N•m
	private Vector torqueVec = new CVector();
	private final SymmetricalLever spinRoll = new SymmetricalLever(torqueStrength, torqueVec::setX);
	private final SymmetricalLever spinPitch = new SymmetricalLever(torqueStrength, torqueVec::setY);
	private final SymmetricalLever spinYaw = new SymmetricalLever(torqueStrength, torqueVec::setZ);
	
	@Override
	public void applyTo(Spacetime s) {
		if (!thrusters.isEmpty()) {
			for (var t : thrusters)
				t.tick(s.dt());
			
			// determine thrust
			if (isOn.is()) {
				netForce.translate(orientation().totalRotation.useToRotate2(thrusters.get().burnFuelGetThrust(s.dt())));
			}
		}

		// determine rotation
		applyTorque(torqueVec);
	}
	
	@Override
	public void addBindingsTo(KeyBinder binder) {
		binder.bindKeysToLever("L", "J", spinPitch);
		binder.bindKeysToLever("O", "U", spinYaw);
		binder.bindKeysToLever("I", "K", spinRoll);
		
		binder.bindKeyToState("W", isOn);
		binder.bindKeyStrokeToCommand("pressed E", thrusters::cycle);
	}
	
	@Override
	public void removeBindingsFrom(KeyBinder binder) {
		binder.clearLetterKeys("JLUOIKWE");
	}
	
}
