package sim;

import java.util.Iterator;
import java.util.LinkedList;

import bodies.Body;
import graphics.GraphicsInterface;
import vector.Vector;

public class Trace implements Iterable<Vector> {
	
	/**
	 * A {@link #coarseness} setting. In this mode, traces only grow when their {@link #target} has moved an
	 * amount exceeding its radius.
	 */
	public static final int COARSE = 0;
	/**
	 * A {@link #coarseness} setting. In this mode, traces grow every nth tick, where n is {@link #integrity}.
	 */
	public static final int STANDARD = 1;
	/**
	 * A coarseness setting. In this mode, traces grow every tick, ignoring {@link #integrity}.
	 */
	public static final int FINE = 2;
	/**
	 * The coarseness of the trace. There are several different modes: {@link #COARSE}, {@link #STANDARD}, and
	 * {@link #FINE}.
	 */
	public static int coarseness = STANDARD;
	
	/**
	 * The number of ticks per trace growth. Also see {@link #coarseness}.
	 */
	public static int integrity = 2;
	
	public Trace(Body target) {
		this.target = target;
	}
	
	private boolean isActive = false;
	private int integrityCounter = 0;
	private final Body target;
	private LinkedList<Vector> trace = new LinkedList<>();
	
	public void toggle() {
		setIsActive(!isActive);
	}
	
	public void setIsActive(boolean b) {
		// indicate a gap with null
		if (!b)
			trace.add(null);
		isActive = b;
	}
	
	public void grow() {
		if (isActive) {
			switch (coarseness) {
			case COARSE:
				if (trace.getLast().difference(target.position()).magnitude() < target.getRadius()) {
					break;
				} // else, fall through
			case STANDARD:
				if (integrityCounter++ == integrity) {
					integrityCounter = 0;
					// fall through
				} else
					break;
			case FINE:
				trace.add(target.position().duplicate());
			}
		}
	}
	
	public void clear() {
		trace.clear();
	}
	
	public void render(GraphicsInterface gu) {
		if (isActive) {
			gu.setColor(target.getColor());
			
			Vector lastPoint = null;
			boolean lastPointWasOnScreen = false;
			boolean pointIsOnScreen = false;
			for (Vector point : trace) {
				if (point == null) { // there was a gap in the trace
					lastPointWasOnScreen = false;
				} else {
					pointIsOnScreen = gu.isOnScreen(point);
					if (lastPoint != null && (lastPointWasOnScreen || pointIsOnScreen)) {
						gu.drawLine(lastPoint, point);
					}
					lastPointWasOnScreen = pointIsOnScreen;
				}
				lastPoint = point;
			}
		}
	}

	@Override
	public Iterator<Vector> iterator() {
		return trace.iterator();
	}
	
	
}
