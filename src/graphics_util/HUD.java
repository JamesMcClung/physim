package graphics_util;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * A HUD manages an assortment of Panes, which in turn manage their Labels.
 */
public class HUD {
	
	public HUD() { }
	
	public HUD(Pane...panes) {
		add(panes);
	}
	
	private LinkedList<Pane> panes = new LinkedList<>();
	
	public void add(Pane...panes) {
		for (Pane pane : panes)
			this.panes.add(pane);
	}
	
	/**
	 * Renders all the panes in reverse order, such that older panes are drawn on top of
	 * newer panes.
	 * @param g graphics to be drawn on
	 */
	public void render(Graphics g) {
		panes.descendingIterator().forEachRemaining((pane) -> pane.render(g));
	}
	
	/*
	 * The following methods should be called only after the MouseEvent positions
	 * have been corrected for. Otherwise, there is a significant offset in where
	 * the mouse actually is and where the HUD thinks it is. Therefore, do not add
	 * the HUD itself as a MouseListener or MouseMotionListener, since it cannot
	 * perform the correction necessary. (This is why HUD does not implement either
	 * of these interfaces, despite having all of their methods.)
	 */
	
	// used for dragging, and represent the last position of the mouse
	private int xMouse = 0, yMouse = 0;
	private Pane draggedPane = null;

	public void mouseClicked(MouseEvent e) {
		for (Pane pane : panes) {
			if (pane.mouseClicked(e.getX(), e.getY())) {
				e.consume();
				break; // only click one pane at a time
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		xMouse = e.getX();
		yMouse = e.getY();
		
		// drag panes
		for (Pane pane : panes) {
			if (!pane.isHidden() && pane.containsPixel(xMouse, yMouse)) {
				if (pane.isDraggable)
					draggedPane = pane; // drag the pane
				e.consume(); // whether the pane is dragged or not, the press has been intercepted
				break; // only drag one panel at a time 
			}
		}
		if (draggedPane != null) {
			panes.remove(draggedPane);
			panes.addFirst(draggedPane);
		}
	}

	public void mouseReleased(MouseEvent e) {
		// stop dragging panes
		draggedPane = null;
		
		for (Pane pane : panes) {
			if (pane.mouseReleased(e.getX(), e.getY())) {
				e.consume();
				break;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (draggedPane == null)
			return;
		
		// drag pane
		int x = e.getX(), y = e.getY();
		int xPane = draggedPane.getX() + x - xMouse;
		draggedPane.setX(xPane);
		if (xPane == draggedPane.getX())
			xMouse = x;
		
		int yPane = draggedPane.getY() + y - yMouse;
		draggedPane.setY(yPane);
		if (yPane == draggedPane.getY())
			yMouse = y;
		
		e.consume();
	}
	
	public void mouseMoved(MouseEvent e) {
		for (Pane pane : panes) {
			if (e.isConsumed())
				pane.setIsHovered(false);
			else if (pane.mouseMoved(e.getX(), e.getY()))
				e.consume();
		}
	}
}
