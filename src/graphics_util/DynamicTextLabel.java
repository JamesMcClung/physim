package graphics_util;

import java.awt.Graphics;
import java.util.function.Supplier;

import util.ValAsStr;

/**
 * A label intended only to draw lines of text. The text is re-evaluated with
 * each rendering via a {@link java.util.function.Supplier}. Color defaults to {@link Pane#colorText}. 
 * <p>
 * {@link util.ValAsStr} is HIGHLY recommended when a value remains constant.
 */
public class DynamicTextLabel extends Label {
	/**
	 * Creates a DynamicTextLabel. {@link util.ValAsStr} is highly recommended over a simple
	 * String Supplier.
	 * @param width width of label, in pixels
	 * @param lineHeight height of each line, in pixels
	 * @param lines the lines of text to be displayed
	 */
	@SafeVarargs // no problems so far, anyways
	public DynamicTextLabel(int width, int lineHeight, Supplier<String>...lines) {
		super(width, lineHeight * lines.length);
		this.lineHeight = lineHeight;
		this.lines = lines;
	}
	
	public DynamicTextLabel(int width, int lineHeight, ValAsStr...lines) {
		super(width, lineHeight * lines.length);
		this.lineHeight = lineHeight;
		this.lines = lines;
	}
	
	private int lineHeight;
	private Supplier<String>[] lines;
	
	@Override
	public void render(Graphics g, int x, int y) {
		g.setColor(Pane.colorText);
		for (int i = 0; i < lines.length; i++) {
			y += lineHeight;
			g.drawString(lines[i].get(), x, y);
		}
	}
}