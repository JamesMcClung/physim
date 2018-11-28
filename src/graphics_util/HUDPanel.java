package graphics_util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class HUDPanel extends JPanel {
	private static final long serialVersionUID = 3141908488923658395L;

	public HUDPanel(HUD hud, int width, int height) {
		this.hud = hud;
		this.width = width;
		this.height = height;
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
	}
	
	private MouseHandler mouseHandler = new MouseHandler();
	private HUD hud;
	private final int width, height;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		hud.render(g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	private class MouseHandler implements MouseMotionListener, MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			hud.mouseClicked(correct(e));
		}
	
		@Override
		public void mousePressed(MouseEvent e) {
			hud.mousePressed(correct(e));
		}
	
		@Override
		public void mouseReleased(MouseEvent e) {
			hud.mouseReleased(correct(e));
		}
	
		@Override
		public void mouseEntered(MouseEvent e) {
			// do nothing
		}
	
		@Override
		public void mouseExited(MouseEvent e) {
			// do nothing
		}
	
		@Override
		public void mouseDragged(MouseEvent e) {
			hud.mouseDragged(correct(e));
		}
	
		@Override
		public void mouseMoved(MouseEvent e) {
			hud.mouseMoved(correct(e));
		}

		/**
		 * convert event coordinates to that of the jpanel (a minor but crucial correction)
		 * @param e mouse event to be corrected
		 * @return corrected mouse event
		 */
		private MouseEvent correct(MouseEvent e) {
			return e = SwingUtilities.convertMouseEvent(null, e, HUDPanel.this);
		}
	}

}
