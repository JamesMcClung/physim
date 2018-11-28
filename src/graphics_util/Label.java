package graphics_util;

import java.awt.Graphics;

/**
 * A label that can be added to an InfoPane. It has a width, height, visibility, and fully
 * customizable render method. 
 */
public abstract class Label {
	public Label(int width, int height) {
		this.widthLabel = width;
		this.heightLabel = height;
	}
	
	public final int widthLabel, heightLabel;
	/**
	 * Can be set to false to be skipped by the containing pane
	 */
	private boolean isVisible = true;
	
	/**
	 * Render this label onto g
	 * @param g on which to be drawn
	 * @param x leftmost x-coordinate
	 * @param y bottommost y-coordinate
	 */
	public abstract void render(Graphics g, int x, int y);
	
	/**
	 * This method is called if and only if the label is actually clicked. It does nothing
	 * by default, and is intended to be overridden.
	 * @return false if nothing happened (alternatively, return true if it was overridden)
	 */
	public boolean handleClick() {
		// does nothing by default
		return false;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisibility(boolean b) {
		isVisible = b;
	}
	
}