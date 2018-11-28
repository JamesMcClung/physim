package bodies;

import java.awt.Color;

import graphics.GraphicsInterface;
import shapes.Orientation;
import sim.Trace;
import util.MiscUtil;
import vector.CVector;
import vector.Quaternion;
import vector.Vector;
public abstract class Body {
	
	/**
	 * Minimum visual radius, for rendering and clicking purposes. In pixels!
	 */
	public static final int MINIMUM_APPARENT_RADIUS = 10;
	
	public Body(double mass, Vector position, Vector velocity) {
		this.mass = mass;

		this.position = new CVector(position);
		this.velocity = new CVector(velocity);
		acceleration = new CVector();
		netForce = new CVector();
	}
	
	// motion properies
	protected final Vector position, velocity, acceleration, netForce;
	
	// sim properties
	protected double mass, charge = 0;
	
	// UI properties
	private Color color = MiscUtil.randomColor();
	private String name = "unnamed";
	public final Trace trace = new Trace(this);
	
	public void render(GraphicsInterface g) {
		g.setColor(getColor());
		renderAt(g, position);
	}
	public abstract void renderAt(GraphicsInterface g, Vector position);
	
	public void renderName(GraphicsInterface g) {
		g.drawString(name, position);
	}

	

	public abstract void rotate(Quaternion q);
	
	
	public double getMass() {
		return mass;
	}
	public void setMass(double m) {
		mass = m;
	}

	public double getCharge() {
		return charge;
	}
	public void setCharge(double q) {
		charge = q;
	}
	
	
	public abstract double getRadius();
	public abstract double getCrossSection(Vector axis);
	public abstract double getVolume();
	
	
	public Color getColor() {
		return color;
	}
	public Body setColor(Color c) {
		color = c;
		return this;
	}
	
	
	public String getName() {
		return name;
	}
	public Body setName(String name) {
		this.name = name;
		return this;
	}
	
	
	public Vector position() {
		return position;
	}
	public Vector velocity() {
		return velocity;
	}
	public Vector acceleration() {
		return acceleration;
	}
	public Vector netForce() {
		return netForce;
	}
	
	public abstract Orientation orientation();
	
	
	
	
	/**
	 * Translate {@link #netForce} by force
	 * @param force force being applied, in N
	 */
	public void applyForce(Vector force) {
		netForce.translate(force);
	}
	
	public void zeroForce() {
		netForce.zero();
	}
	
	public void updateAccelerations() {
		acceleration.become(netForce).scale(1/getMass());
	}
	
	public void updatePosition(double dt) {
 		position.translateScaled(velocity, dt);
	}
	public void updateVelocity(double dt) {
		velocity.translateScaled(acceleration, dt);
	}
	
	public Vector getPositionOf(Vector pos) {
		return pos.difference(position).setBasis(orientation());
	}

}
