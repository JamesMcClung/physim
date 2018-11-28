package graphics;

import javax.swing.JFrame;

public class STWindow {
	public static final String TITLE = "Space Simulator";
//	public static final int WIDTH_PANEL = 1000, HEIGHT_PANEL = 800;
	
	public STWindow(STPanel spacetime) {
		frame = new JFrame(TITLE);
//		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(keyHandler);

		spacetimePanel = spacetime;
		frame.add(spacetimePanel);
		frame.addMouseListener(spacetimePanel.mouseHandler);
		frame.addMouseMotionListener(spacetimePanel.mouseHandler);
		
		frame.pack(); // resize this window so that it is just big enough to fit spacetimePanel
		frame.setLocationRelativeTo(null); // move the window to the center of the screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private STPanel spacetimePanel;
	private JFrame frame;
	
}
