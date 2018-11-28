package graphics_util;

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * A button is a clickable label. It has rounded edges and is filled in to distinguish it
 * from non-buttons. 
 */
public class Button extends Label {
	private static final int BUTTON_CURVE = 16;
	
	/**
	 * Creates a basic button.
	 * @param width width of button
	 * @param height height of button
	 * @param text text displayed on button; leave null if no text is desired
	 * @param action action taken when button is clicked
	 */
	public Button(int width, int height, String text, ButtonAction action) {
		super(width, height);
		this.action = action;
		setText(text);
	}
	
	private ButtonAction action;
	private String text;
	private int xTextOffset, yTextOffset;
	private boolean offsetsAreUndefined = true;

	@Override
	public void render(Graphics g, int x, int y) {
		g.setColor(Pane.colorBackground);
		g.fillRoundRect(x, y, widthLabel, heightLabel, BUTTON_CURVE, BUTTON_CURVE);
		
		if (text != null) {
			g.setColor(Pane.colorText);
			g.setFont(Pane.font);
			if (offsetsAreUndefined) {
				FontMetrics f = g.getFontMetrics();
				xTextOffset = (widthLabel - f.stringWidth(text)) / 2;
				yTextOffset = (heightLabel + f.getAscent() - f.getDescent()) / 2;
				offsetsAreUndefined = false;
			}
			g.drawString(text, x + xTextOffset, y + yTextOffset);
		}
	}
	
	@Override
	public boolean handleClick() {
		action.invoke();
		return true;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * A method that is called when the button is clicked.
	 */
	@FunctionalInterface
	public static interface ButtonAction {
		void invoke();
	}
}
