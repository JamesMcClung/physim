package graphics_util;

import java.awt.Color;
import java.awt.Graphics;

public class ProgressBar extends Label {
	public static Color colorFull = new Color(128, 255, 128, 228),
			colorEmpty = new Color(255, 128, 128, 228),
			colorTank = new Color(128, 128, 128, 64);
	private static final int CORNER_ARC_LENGTH = 3;

	/**
	 * Creates a new progress bar. It is a rounded rectangle with several colors.
	 * @param width width of the progress bar
	 * @param height height of the progress bar
	 * @param progressGetter returns the progress to be displayed, from 0 to 1
	 */
	public ProgressBar(int width, int height, ProgressGetter progressGetter) {
		super(width, height);
		this.progressGetter = progressGetter;
	}
	
	private ProgressGetter progressGetter;

	@Override
	public void render(Graphics g, int x, int y) {
		g.setColor(colorTank);
		g.fillRoundRect(x, y, widthLabel, heightLabel, CORNER_ARC_LENGTH, CORNER_ARC_LENGTH);
		
		double progress = progressGetter.getProgress();
		g.setColor(getProgressColor(progress));
		g.fillRoundRect(x, y, (int) (widthLabel * progress), heightLabel, CORNER_ARC_LENGTH, CORNER_ARC_LENGTH);
		
		g.setColor(Pane.colorOutline);
		g.drawRoundRect(x, y, widthLabel, heightLabel, CORNER_ARC_LENGTH, CORNER_ARC_LENGTH);
	}
	
	private Color progColor = null;
	private double lastProgress = -1;
	private Color getProgressColor(double progress) {
		if (progress != lastProgress) {
			progColor = new Color(
					colorEmpty.getRed() + (int) (progress * (colorFull.getRed() - colorEmpty.getRed())),
					colorEmpty.getGreen() + (int) (progress * (colorFull.getGreen() - colorEmpty.getGreen())),
					colorEmpty.getBlue() + (int) (progress * (colorFull.getBlue() - colorEmpty.getBlue())),
					colorEmpty.getAlpha() + (int) (progress * (colorFull.getAlpha() - colorEmpty.getAlpha())));
			lastProgress = progress;
		}
		return progColor;
	}
	
	/**
	 * Functional interface for getting progress.
	 */
	@FunctionalInterface
	public static interface ProgressGetter {
		double getProgress();
	}
	
}
