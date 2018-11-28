package membrane;

import java.awt.Color;
import java.util.function.Function;

import vector.Vector;

public class ColorGradient implements Function<Vector, Color> {

	public ColorGradient(Vector initialPos, Color color1, Color color2, Vector dof, double expectedDisplacement) {
		this.initialPos = initialPos;
		this.color1 = color1;
		this.color2 = color2;
		this.dof = dof;
		this.maxDistance = expectedDisplacement;
	}

	private static final int maxWeight = 2 << 16;

	private final Vector initialPos;
	private final Color color1, color2;
	private final Vector dof;
	private double maxDistance;

	@Override
	public Color apply(Vector t) {
		double dist = t.difference(initialPos).dot(dof);
		return weightRGB(color1, color2, (int) (maxWeight * (1 + dist / maxDistance) / 2));
	}

	/**
	 * Creates a new color somewhere in between two colors, based on the specified
	 * weight.
	 * 
	 * @param color1  some color
	 * @param color2  another color
	 * @param weight1 the weight of the first color, from which the weight of the
	 *                second color is determined
	 * @return the new color
	 */
	private Color weightRGB(Color color1, Color color2, int weight1) {
		int weight2 = maxWeight - weight1;
		int r = (color1.getRed() * weight1 + color2.getRed() * weight2) / maxWeight;
		int g = (color1.getGreen() * weight1 + color2.getGreen() * weight2) / maxWeight;
		int b = (color1.getBlue() * weight1 + color2.getBlue() * weight2) / maxWeight;
		return new Color(bound(r), bound(g), bound(b));
	}

	public void accommodateDistance(double dist) {
		maxDistance = Math.max(maxDistance, Math.abs(dist));
	}

	private int bound(int colorVal) {
		if (colorVal < 0)
			return 0;
		if (colorVal > 255)
			return 255;
		return colorVal;
	}

}
