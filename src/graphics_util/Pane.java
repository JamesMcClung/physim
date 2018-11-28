package graphics_util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import graphics.STPanel;

public class Pane { 
	public static Font font = new Font("Verdana", Font.PLAIN, 12);
	/** 
	 * Color that text is drawn in. Can be changed during runtime.
	 */
	public static Color colorText = Color.WHITE,
			colorBackground = new Color(128, 128, 128, 128),
			colorOutline = new Color(255, 255, 255, 128);
	public final static int PADDING = 10, LINE_HEIGHT = 18, TICK_LENGTH = 3;
	/**
	 * The minimum number of pixels that a pane must show after being partially moved off-screen.
	 * This is to prevent moving a pane completely off-screen.
	 */
	public static final int MINIMUM_VISIBLE_PORTION = 16;
	
	public Pane(int x, int y, int width, int height) {
		this.xPane = x;
		this.yPane = y;
		this.widthPane = width;
		this.heightPane = height;
	}
	
	private int xPane, yPane;
	protected int widthPane, heightPane;
	protected boolean isHidden = false;
	public boolean isDraggable = true, isHoverable = true;;
	private boolean isHovered = false;
	protected int padding = PADDING;
	protected List<Column> columns = new ArrayList<>();
	
	/**
	 * Render the visible labels. Invisible labels are skipped, and their spot is taken.
	 * @param g
	 */
	public void render(Graphics g) {
		if (isHidden) return;
		
		g.setColor(colorBackground);
		g.fillRect(xPane, yPane, widthPane, heightPane);
		
		g.setColor(colorText);
		g.setFont(font);
		
		int x = xPane + padding;
		int y = yPane + padding;
		for (Column col : columns) {
			if (col.isVisible()) {
				col.render(g, x, y);
				x += col.getWidth() + padding;
			}
		}
		
		if (isHovered) {
			g.setColor(colorOutline);
			g.drawRect(xPane, yPane, widthPane, heightPane);
		}
	}

	/**
	 * Add labels for the pane to render. The pane itself does not change at all; to resize
	 * according the labels' dimensions, use {@link #pack()}.
	 * <p>
	 * Order does matter. The first label is drawn at the top of the column,
	 * and the last is at the bottom.
	 * <p>
	 * To add labels horizontally (one to the bottom of each column), let column = -1.
	 * @param columnIndex the column to which the labels are added 
	 * @param labels labels to add
	 */
	public void add(int columnIndex, Label...labels) {
		if (columnIndex == -1) {
			for (int i = 0; i < labels.length; i++) {
				getColumn(i).add(labels[i]);
			}
		} else {
			Column col = getColumn(columnIndex);
			for (Label l : labels)
				col.add(l);
		}
	}
	
	/**
	 * Adds labels in the zeroth column.
	 * @param labels labels to add
	 * @see #add(int, Label...)
	 */
	public void add(Label...labels) {
		add(0, labels);
	}
	
	/**
	 * Add the column to the end of the list of columns.
	 * @param column to be added
	 * @return the index of the column just added (i.e., size - 1)
	 */
	protected int add(Column column) {
		if (column != null)
			columns.add(column);
		return columns.size()-1;
	}
	
	/**
	 * Determines whether a given pixel coordinate is within the bounds of this pane
	 * (i.e., the pixel is one that the pane draws onto). One relevant use is to determine
	 * whether the pane was clicked on.
	 * @param xPixel x-coordinate of pixel in component
	 * @param yPixel y-coordinate of pixel in component
	 * @return whether the pixel is contained by this pane
	 */
	public boolean containsPixel(int xPixel, int yPixel) {
		return xPane <= xPixel && xPixel <= xPane+widthPane &&
				yPane <= yPixel && yPixel <= yPane+heightPane;
	}
	
	/**
	 * Resizes the pane to fit its labels exactly (plus buffer space on each side).
	 * Invisible labels (and columns) are ignored.
	 */
	public void pack() {
		int newWidth = padding;
		int newHeight = 0;
		for (Column c : columns) {
			if (c.isVisible()) {
				newWidth += c.getWidth() + padding;
				newHeight = Math.max(newHeight, c.getHeight());
			}
		}
		setWidth(newWidth);
		setHeight(newHeight + 2*padding);
	}
	
	public Label getLabelAt(int xPixel, int yPixel) {
		int x = xPane + padding, y = yPane + padding;
		for (Column col : columns) {
			if (col.isVisible()) { 
				int colWidth = col.getWidth();
				if (x <= xPixel && xPixel <= x + col.getWidth()) {
					for (Label label : col.labels) {
						if (label.isVisible()) {
							if (y <= yPixel && yPixel <= y + label.heightLabel) {
								return xPixel <= x + label.widthLabel ? label : null;
							}
							y += label.heightLabel + padding;
						}
					}
				}
				x += colWidth + padding;
			}
		}
		return null;
	}
	
	/**
	 * Returns the column at index, generating new columns if necessary.
	 * @param index index of column
	 * @return the column
	 */
	protected Column getColumn(int index) {
		// add intermediate columns, if necessary 
		for (int i = columns.size(); i <= index; i++) {
			columns.add(new Column());
		}
		return columns.get(index);
	}
	
	public int getWidth() {
		return widthPane;
	}
	
	public int getHeight() {
		return heightPane;
	}
	
	public int getX() {
		return xPane;
	}
	
	public int getY() {
		return yPane;
	}
	
	/**
	 * Sets the width of the pane, changing its x-coordinate to avoid violating
	 * {@link #MINIMUM_VISIBLE_PORTION} if necessary.
	 * @param widthPane width of the pane
	 */
	public void setWidth(int widthPane) {
		this.widthPane = widthPane;
		xPane = Math.max(xPane, MINIMUM_VISIBLE_PORTION - widthPane);
	}
	
	/**
	 * Sets the height of the pane, changing its y-coordinate to avoid violating
	 * {@link #MINIMUM_VISIBLE_PORTION} if necessary.
	 * @param heightPane height of the pane
	 */
	public void setHeight(int heightPane) {
		this.heightPane = heightPane;
		yPane = Math.max(yPane, MINIMUM_VISIBLE_PORTION - heightPane);
	}
	
	/**
	 * Set the x-coordinate of the leftmost point on the pane, without violating
	 * {@link #MINIMUM_VISIBLE_PORTION}.
	 * @param x the x-coordinate
	 */
	public void setX(int x) {
		xPane = Math.max(x, MINIMUM_VISIBLE_PORTION - widthPane);
		xPane = Math.min(xPane, STPanel.WIDTH_PANEL - MINIMUM_VISIBLE_PORTION);
	}
	
	/**
	 * Set the y-coordinate of the topmost point on the pane, without violating
	 * {@link #MINIMUM_VISIBLE_PORTION}.
	 * @param y the y-coordinate
	 */
	public void setY(int y) {
		yPane = Math.max(y, MINIMUM_VISIBLE_PORTION - heightPane);
		yPane = Math.min(yPane, STPanel.HEIGHT_PANEL - MINIMUM_VISIBLE_PORTION);
	}
	
	/**
	 * Set whether the pane is hidden.
	 * @param b
	 * @see #toggleVisibility()
	 */
	public void setIsHidden(boolean b) {
		isHidden = b;
	}
	
	public boolean isHidden() {
		return isHidden;
	}
	
	/**
	 * Toggles whether the pane is visible.
	 * @return true if it is now hidden
	 */
	public boolean toggleVisibility() {
		setIsHidden(!isHidden);
		return isHidden;
	}
	
	public void setPadding(int padding) {
		this.padding = padding;
	}
	
	public void setIsHovered(boolean b) {
		if (isHoverable)	
			isHovered = b;
	}
	
	/**
	 * A column of labels. Handles rendering vertically. 
	 */
	protected class Column implements Iterable<Label> {
		
		public Column(Label...labels) {
			add(labels);
		}
		
		private List<Label> labels = new LinkedList<>();
		
		public void add(Label...labels) {
			for (Label label : labels)
				this.labels.add(label);
		}
		
		/**
		 * Calculates the width of the column, which is the greatest width of its labels.
		 * Padding is NOT included, and invisible labels are ignored.
		 * @return the width
		 */
		public int getWidth() {
			int width = 0;
			for (Label label : labels) {
				if (label.isVisible())
					width = Math.max(width, label.widthLabel);
			}
			return width;
		}
		
		/**
		 * Calculates the height of the column, which is the sum of the heights of its labels.
		 * Padding is ONLY included BETWEEN the labels, not at the top or bottom, an invisible
		 * labels are ignored.
		 * @return the height
		 */
		public int getHeight() {
			int height = 0;
			for (Label label : labels) {
				if (label.isVisible()) {
					height += label.heightLabel + padding;
				}
			}
			if (height > 0)
				height -= padding; // remove extra padding at the bottom
			return height;
		}
		
		/**
		 * The column considers itself "visible" if it has at least one visible label.
		 * @return whether the column is visible
		 */
		public boolean isVisible() {
			for (Label label : labels)
				if (label.isVisible())
					return true;
			return false;
		}

		/**
		 * Sets the visibility of all labels in the column to b.
		 * @param b visibility
		 */
		public void setVisible(boolean b) {
			for (Label label : labels)
				label.setVisibility(b);
		}
		
		public void render(Graphics g, int x, int y) {
			for (Label label : labels) {
				if (label.isVisible()) {
					label.render(g, x, y);
					y += label.heightLabel + padding;
				}
			}
		}

		@Override
		public Iterator<Label> iterator() {
			return labels.iterator();
		}
	}
	
	/*
	 * Mouse Handling
	 */

	/**
	 * Determines whether this pane was actually clicked. If it was, the click is handled by any labels that may
	 * intercept it.
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return true if the click happened on the pane
	 */
	public boolean mouseClicked(int x, int y) {
		if (containsPixel(x, y)) {
			clickLabels(x, y);
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to click a label at (x, y).
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return true iff a label intercepted the click
	 */
	protected boolean clickLabels(int x, int y) {
		Label labelAtClick = getLabelAt(x, y);
		if (labelAtClick != null)
			return labelAtClick.handleClick();
		return false;
	}
	
	/**
	 * Called if and only if the pane was pressed.
	 * <p>
	 * Does nothing and returns false unless overridden.
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return true if the press was consumed
	 */
	public boolean mousePressed(int x, int y) { return false; }

	/**
	 * Called if and only if the pane was pressed.
	 * <p>
	 * Does nothing and returns false unless overridden.
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return true if the release was consumed
	 */
	public boolean mouseReleased(int x, int y) { return false; }

	/**
	 * Called whenever the mouse moves, whether it is over the pane or not.
	 * <p>
	 * By default, this handles the outline for when the pane is hovered over, and returns true.
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 * @return true if the pane is hovered
	 */
	public boolean mouseMoved(int x, int y) {
		boolean tryHover = containsPixel(x, y);
		setIsHovered(tryHover);
		return tryHover;
	}
}
