package membrane;

import graphics.GraphicsInterface;
import vector.CVector;
import vector.Vector;

public class MeasuringParticle extends RestrainedParticle {

	public MeasuringParticle(double mass, double charge, Vector position, Vector velocity, Vector dof) {
		super(mass, charge, position, velocity, dof);
		initialPosition = new CVector(position());
		maxDisplacement = new CVector(position());
		minDisplacement = new CVector(position());
	}
	
	private final Vector initialPosition;
	protected final Vector maxDisplacement, minDisplacement;
	private double maxDispMag = 0, minDispMag = 0;
	
	@Override
	public void updatePosition(double dt) {
		super.updatePosition(dt);
		double dispMag = position.difference(initialPosition).dot(dof[0]);
		if (dispMag > maxDispMag) {
			maxDispMag = dispMag;
			maxDisplacement.become(position);
		} else if (dispMag < minDispMag) {
			minDispMag = dispMag;
			minDisplacement.become(position);
		}
	}
	
	@Override
	public void renderAt(GraphicsInterface gi, Vector pos) {
		super.renderAt(gi, pos);
		gi.drawLine(minDisplacement, maxDisplacement);
	}

}
