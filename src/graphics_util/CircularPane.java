package graphics_util;

import java.awt.Graphics;

/**
 * A circular pane. It isn't really meant for storing labels, but it certainly can.
 */
public class CircularPane extends Pane {

	public CircularPane(int x, int y, int radius) {
		super(x, y, 2*radius, 2*radius);
		this.radius = radius;
	}
	
	private int radius;
	
	@Override
	public void setWidth(int widthPane) {
		super.setWidth(widthPane);
		super.setHeight(widthPane);
		radius = widthPane/2;
	}
	
	@Override
	public void setHeight(int heightPane) {
		super.setWidth(heightPane);
		super.setHeight(heightPane);
		radius = heightPane/2;
	}
	
	public void setRadius(int radius) {
		super.setWidth(radius/2);
		super.setHeight(radius/2);
		this.radius = radius;
	}
	
	public int getXCenter() {
		return getX() + radius;
	}
	
	public int getYCenter() {
		return getY() + radius;
	}
	
	@Override
	public void render(Graphics g) {
		if (isHidden) return;
		
		g.setColor(colorBackground);
		g.fillOval(getX(), getY(), getWidth(), getHeight());
		
		g.setColor(colorText);
		g.setFont(font);
		
		int x = getX() + PADDING;
		int y = getY() + PADDING;
		for (Column col : columns) {
			if (col.isVisible()) {
				col.render(g, x, y);
				x += col.getWidth() + PADDING;
			}
		}
	}
	
	@Override
	public boolean containsPixel(int xPixel, int yPixel) {
		int delX = xPixel - (getX() + radius);
		int delY = yPixel - (getY() + radius);
		return delX*delX + delY*delY <= radius*radius;
	}

}
