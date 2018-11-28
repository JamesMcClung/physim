package forces;

import graphics.GraphicsInterface;
import sim.Spacetime;

public interface Force {
	
	/**
	 * Apply this force to every particle in the space
	 * @param s the space
	 */
	public abstract void applyTo(Spacetime s);
	
	public default void renderForce(GraphicsInterface gu) {
		// do nothing by default
	}

}
