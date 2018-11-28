package graphics;

import bodies.Controllable;
import bodies.Body;
import input_util.KeyBinder;
import input_util.ScalingLever;
import input_util.SymmetricalLever;
import shapes.Orientation;
import vector.CVector;
import vector.Quaternion;
import vector.Vector;

public class Camera implements Controllable {

	public static final double PIXELS_PER_SECOND = STPanel.WIDTH_PANEL / 3;
	public static final double RADIANS_PER_SECOND = Math.PI;

	public Camera(Vector position) {
		this.position = new CVector(position);
		orientation = new Orientation();
	}

	public final Vector position;
	public final Orientation orientation;

	private Body trackedParticle = null;
	private final Vector relativePositionOfTrackedParticle = new CVector();
	private boolean imitateTrackedParticle = false;

	private final double baseRotationSpeed = Math.PI / 2; // in rad/s
	/**
	 * The rotation speed of {@link #spinRoll} and {@link #spinPitch} in rad/s.
	 * Initially set to {@link #baseRotationSpeed}, but can be scaled (to
	 * accommodate zoom, for instance). It would not make sense to scale
	 * {@link #spinYaw} with zoom, so it stays at {@link #baseRotationSpeed}.
	 */
	private double rollPitchRotationSpeed = baseRotationSpeed;
	private double yawRotationSpeed = baseRotationSpeed;
	private final Vector angularVelocity = new CVector();
	private final ScalingLever rotationSpeedScale = new ScalingLever(this::scaleAllRotationSpeed, 1 + 1 / 32d);

	private final double baseMoveSpeed = 1;
	private double moveSpeed = baseMoveSpeed;
	private final ScalingLever speedScale = new ScalingLever(this::scaleMoveSpeed, 1 + 1 / 32d);
	private final Vector velocity = new CVector();

	public final SymmetricalLever moveX = new SymmetricalLever(1, velocity::setX);
	public final SymmetricalLever moveY = new SymmetricalLever(1, velocity::setY);
	public final SymmetricalLever moveZ = new SymmetricalLever(1, velocity::setZ);

	public final SymmetricalLever spinRoll = new SymmetricalLever(rollPitchRotationSpeed, angularVelocity::setX);
	public final SymmetricalLever spinPitch = new SymmetricalLever(rollPitchRotationSpeed, angularVelocity::setY);
	public final SymmetricalLever spinYaw = new SymmetricalLever(yawRotationSpeed, angularVelocity::setZ);

	public void update(double dt) {
		// position
		if (trackedParticle == null) {
			speedScale.scaleVal();
			rotationSpeedScale.scaleVal();
			if (!velocity.isZero()) {
				position.translateScaled(orientation.totalRotation.useToRotate2(velocity.duplicate()), dt * moveSpeed);
			}
		} else {
			if (imitateTrackedParticle) {
				orientation.become(trackedParticle.orientation());
				position.become(trackedParticle.position());

				// don't bother with orientation stuff below, either
				return;
			} // else

			position.difference(trackedParticle.position(), relativePositionOfTrackedParticle);
		}

		// orientation
		Quaternion rot = new Quaternion(1, 0, 0, 0);
		if (angularVelocity.x() != 0)
			rot.translate(Quaternion.getRotationQuaternion(angularVelocity.x() * dt, orientation.xAxis()));
		if (angularVelocity.y() != 0)
			rot.translate(Quaternion.getRotationQuaternion(angularVelocity.y() * dt, orientation.yAxis()));
		if (angularVelocity.z() != 0)
			rot.translate(Quaternion.getRotationQuaternion(angularVelocity.z() * dt, orientation.zAxis()));
		rot.normalize();
		orientation.rotate(rot);
		rot.useToRotate2(relativePositionOfTrackedParticle);
	}

	public void setRotationSpeed(double rotationSpeed) {
		this.rollPitchRotationSpeed = rotationSpeed;
		spinRoll.setMagnitude(rotationSpeed);
		spinPitch.setMagnitude(rotationSpeed);
		spinYaw.setMagnitude(rotationSpeed);
	}

	@Override
	public void addBindingsTo(KeyBinder binder) {
		binder.bindKeysToLever("A", "D", moveX);
		binder.bindKeysToLever("S", "W", moveY);
		binder.bindKeysToLever("E", "F", moveZ);

		binder.bindKeysToLever("K", "I", spinRoll);
		binder.bindKeysToLever("L", "J", spinPitch);
		binder.bindKeysToLever("O", "U", spinYaw);

		binder.bindKeysToLever("Z", "C", speedScale);
		binder.bindKeysToLever("N", "M", rotationSpeedScale);
	}

	@Override
	public void removeBindingsFrom(KeyBinder binder) {
		binder.clearLetterKeys("DAWSIKJLUOEFZCNM");
	}

	public void setTrackedParticle(Body p) {
		trackedParticle = p;
		if (p != null)
			relativePositionOfTrackedParticle.difference(p.position(), position);
		imitateTrackedParticle = false;
	}

	public void setImitatedParticle(Body p) {
		trackedParticle = p;
		imitateTrackedParticle = p != null;
	}

	public boolean isTracking(Body p) {
		return p == trackedParticle && !imitateTrackedParticle;
	}

	public boolean isImitating(Body p) {
		return p == trackedParticle && imitateTrackedParticle;
	}

	public double moveSpeed() {
		return moveSpeed;
	}

	public void scaleMoveSpeed(double sf) {
		if (sf == 1)
			return;
		moveSpeed *= sf;
	}

	public void scaleRollAndPitchSpeed(double sf) {
		if (sf == 1)
			return;
		rollPitchRotationSpeed *= sf;
		spinRoll.setMagnitude(rollPitchRotationSpeed);
		spinPitch.setMagnitude(rollPitchRotationSpeed);
	}
	
	public void scaleAllRotationSpeed(double sf) {
		if (sf == 1)
			return;
		
		rollPitchRotationSpeed *= sf;
		spinRoll.setMagnitude(rollPitchRotationSpeed);
		spinPitch.setMagnitude(rollPitchRotationSpeed);
		
		yawRotationSpeed *= sf;
		spinYaw.setMagnitude(yawRotationSpeed);
	}

}
