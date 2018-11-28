package graphics;

import static util.MiscUtil.square;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import bodies.Body;
import bodies.Controllable;
import bodies.Particle;
import graphics_util.Button;
import graphics_util.CircularPane;
import graphics_util.DynamicTextLabel;
import graphics_util.HUD;
import graphics_util.Label;
import graphics_util.Pane;
import graphics_util.ProgressBar;
import input_util.KeyBinder;
import input_util.ScalingLever;
import rocketry.Rocket;
import shapes.Orientation;
import sim.Spacetime;
import util.CircularLinkedList;
import util.DistanceUnit;
import util.MiscUtil;
import util.ValAsStr;
import vector.CVector;
import vector.Vector;;

public class STPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH_PANEL = 900, HEIGHT_PANEL = 900;
	
	private static final int PARTICLE_RENDER_THRESHOLD = 25;
	
	/**
	 * Time between frame updates. In irl-milliseconds.
	 */
	public static final double DELAY = 1d/60;
	
	public STPanel(Spacetime s) {
		spacetime = s;
		
		gps = new GPS(GPS.PADDING, GPS.PADDING + Toolbar.TOOLBAR_HEIGHT);
		pv = new ParticleViewer(Pane.PADDING, gps.getY() + gps.getHeight() + Pane.PADDING);
		tm = new TapeMeasure(300, 300, 400, 300);
		toolbar = new Toolbar();
		hud = new HUD(gps, pv, tm.label, tm.knob1, tm.knob2, toolbar);
		
		setSecondsPerMillisecond(1); // initial s/s
		
		addKeyBindings();
		setControllable(camera);
//		updateCameraSpeeds();
		
		timer = new Timer((int) (DELAY * 1000), (event) -> {
			// update the simulation
			if (isRunning)
				stepTime();
			
			// move camera
			camera.update(spacetime.dt() / secondsPerSecond);
			
			// scale ssps and zoom
			zoomScale.scaleVal();
			timeScale.scaleVal();
			
			// scale camera speed
//			updateCameraSpeeds();
			
			// rerender
			repaint();
		});
		
		timer.start();
	}
	
	public STPanel(Spacetime s, double view) {
		this(s);
		setView(view);
	}
	
	private Spacetime spacetime;
	
	// time stuff
	/**
	 * Sim seconds per real second.
	 */
	private double secondsPerSecond;
	private Timer timer;
	private boolean isRunning = false;
	
	// graphics stuff
	private Camera camera = new Camera(new CVector(0,0,100));
	private GraphicsInterface gi = new GraphicsInterface(WIDTH_PANEL, HEIGHT_PANEL, 0, 0, camera);
	private boolean doTraces = false;
	private boolean doNames = false;
	
	// GUI and input stuff
	private GPS gps;
	private ParticleViewer pv;
	private TapeMeasure tm;
	private Toolbar toolbar;
	private HUD hud;
	public final MouseHandler mouseHandler = new MouseHandler();
	
	
	private ScalingLever zoomScale = new ScalingLever(this::scaleZoom, 1 + 1/32d);
	private ScalingLever timeScale = new ScalingLever(this::scaleSimSecondsPerSecond, 1 + 1/16d);

	public void toggleTime() {
		isRunning = !isRunning;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		gi.setGraphics(g);
		gi.fill(Color.BLACK);
		
		// render traces of objects
		renderTraces(gi);
		
		
		// render objects and forces
		gi.setDrawParticlesAsDots(spacetime.particles().size() > PARTICLE_RENDER_THRESHOLD);
		spacetime.render(gi);
		
		if (doNames)
			renderNames(gi);
		
		// render HUD on top
		hud.render(g);
		
		// might be unnecessary
		gi.setGraphics(null);
	}
	
	private void scaleZoom(double sf) {
		if (sf == 1) return;
		gi.scaleView(sf);
		camera.scaleRollAndPitchSpeed(sf);
	}
	
	/**
	 * Set the spatial scale so that the width of the screen spans <code>meters</code>. Changes
	 * {@link #gi}.zoom and {@link #camera}.ds in doing so.
	 * @param meters distance in meters for the screen to span
	 */
	public void setView(double meters) {
		camera.position.setZ(meters / Math.tan(gi.getFOV()/2));
	}
	
	public void setSecondsPerMillisecond(double spms) {
		secondsPerSecond = spms;
		spacetime.setDT(DELAY * secondsPerSecond);
	}
	
	private void scaleSimSecondsPerSecond(double sf) {
		if (sf == 1) return;
		secondsPerSecond *= sf;
		spacetime.setDT(DELAY * secondsPerSecond);
	}
	
	/**
	 * Step forward one dt of time.
	 */
	public void stepTime() {
		spacetime.update();
		growTraces();
	}
	
	/**
	 * Finds the particle that is near (xPixel, yPixel). If multiple particles are at that
	 * point, the top one is returned.
	 * @param xPixel x-coordinate on screen
	 * @param yPixel y-coordinate on screen
	 * @return topmost particle at given coordinates
	 */
	private Body getParticleAt(int xPixel, int yPixel) {
		Body theParticle = null;
		Vector theRelPos = null;
		
		var contourLine = gi.getContourLine(xPixel, yPixel);
		for (var p : spacetime.particles()) {
			Vector relPos = gi.getRelativePosition(p.position());
			Vector selected = contourLine.apply(relPos.z());
			
			if (Vector.distanceSq(relPos, selected) < MiscUtil.square(Math.max(
					p.getRadius(), Body.MINIMUM_APPARENT_RADIUS / gi.getPPM2(relPos)))) {
				if (theParticle == null || relPos.z() > theRelPos.z()) {
					theParticle = p;
					theRelPos = relPos;
				}
			}
		}
		
		return theParticle;
	}
	
	public void toggleNames() {
		doNames = !doNames;
	}
	
	public void renderNames(GraphicsInterface g) {
		g.setColor(Color.WHITE);
		for (Body p : spacetime.particles())
			p.renderName(g);
	}
	
	public void toggleTraces() {
		doTraces = !doTraces;
		for (var p : spacetime.particles())
			p.trace.setIsActive(doTraces);
	}
	
	public void growTraces() {
		for (var p : spacetime.particles())
			p.trace.grow();
	}
	
	public void clearTraces() {
		for (var p : spacetime.particles())
			p.trace.clear();
	}
	
	private void renderTraces(GraphicsInterface gu) {
		for (var p : spacetime.particles())
			p.trace.render(gu);
	}

	private Controllable controlled = camera;
	
	private void setControlled(Controllable c) {
		if (controlled.equals(c))
			return;
		
		controlled.removeBindingsFrom(keyBindings);
		c.addBindingsTo(keyBindings);
		controlled = c;
	}
	
	/**
	 * Used for displaying information such as position, scale, and time.
	 */
	private class GPS extends Pane {
		public static final int HUD_WIDTH = 200;
		
		Camera gpsCam = new Camera(null);
		GraphicsInterface gpsGI;
		
		public GPS(int xHUD, int yHUD) {
			super(xHUD, yHUD, HUD_WIDTH, -1);
			
			int widthLabel = HUD_WIDTH - PADDING*2;
			
//			// label to show scale of space
//			Label scaleLabel= new Label(widthLabel, LINE_HEIGHT + PADDING + TICK_LENGTH) {
//				public void render(Graphics g, int x, int y) {
//					double maxUnitSize = widthLabel / gi.getPPM(0);
//					Tuple<DistanceUnit, Integer> unitAndCount = DistanceUnit.getUnitAndCount(maxUnitSize);
//					DistanceUnit unit = unitAndCount.x;
//					int nUnits = unitAndCount.y;
//					int barEndX = x + (int) (gi.getPPM(0) * unit.meterEquivalent * nUnits);
//					
//					g.setColor(colorText);
//					// draw text for unit
//					g.drawString(String.format("%d %s%s", nUnits, unit.name, (nUnits==1?"":"s")), x, y + LINE_HEIGHT);
//					
//					y += LINE_HEIGHT + PADDING;
//					
//					// draw the bar
//					g.drawLine(x, y, Math.min(barEndX, x+widthLabel), y); // it might be cut off
//					g.drawLine(x, y-TICK_LENGTH, x, y+TICK_LENGTH);
//					if (barEndX <= x+widthLabel) // if it IS cut off, don't draw the second tick
//						g.drawLine(barEndX, y-TICK_LENGTH, barEndX, y+TICK_LENGTH);
//				}
//			};
			
			// label to show coordinates in space
			Label positionLabel = new DynamicTextLabel(widthLabel, LINE_HEIGHT,
					new ValAsStr("x = %2.3e m", camera.position::x),
					new ValAsStr("y = %2.3e m", camera.position::y),
					new ValAsStr("z = %2.3e m", camera.position::z));
			
			// label to show speed
			Label speedLabel = new DynamicTextLabel(widthLabel, LINE_HEIGHT,
					new ValAsStr("v = %2.1e m/s", camera::moveSpeed),
					new ValAsStr("fov = %s", gi::getFOVAngle),
					new ValAsStr("(%1.3e rad)", gi::getFOV));
			
			// label to show current time
			Label timeLabel = new DynamicTextLabel(widthLabel, LINE_HEIGHT,
					new ValAsStr("t = %.2f s", () -> spacetime.getTimeAt(camera.position)),
					new ValAsStr("(%2.2e s/s)", () -> secondsPerSecond));

			// label to show orientation of camera
			gpsGI = new GraphicsInterface(widthLabel, widthLabel, xHUD, yHUD, gpsCam);
			gpsGI.setProjectionType(GraphicsInterface.ORTHOGRAPHIC);
			gpsGI.setOrthographicPPM(50);
			Label orientationLabel = new Label(widthLabel, widthLabel) {
				public void render(Graphics g, int x, int y) {
					gpsGI.setGraphics(g);
					gpsGI.setPos(x, y);
					gpsGI.setColor(Color.RED);
					renderOrientationAxes(gpsGI);
					gpsGI.setGraphics(null);
				}
			};

			// scaleLabel has been removed for now, since it only works for orthographic projection
			add(0, orientationLabel, positionLabel, speedLabel, timeLabel);
			pack();
		}
		
		private final Color[] axisColors = { Color.RED, Color.BLUE, Color.GREEN };
		private final Orientation initialOrientation = new Orientation();
		private final Vector zeroVector = new CVector();
		
		void renderOrientationAxes(GraphicsInterface gi) {
			gpsCam.orientation.become(camera.orientation);
			for (int i = 0; i < 3; i++) {
				gi.setColor(axisColors[i]);
				gi.drawLine(zeroVector, initialOrientation.axes[i]);
			}
		}
	}
	
	/**
	 * For viewing information on a particular particle.
	 */
	private class ParticleViewer extends Pane {
		public static final int PV_WIDTH = 150; 
		
		public ParticleViewer(int x, int y) {
			super(x, y, PV_WIDTH, -1);
			setViewedParticle(null);
			
			int labelWidth = widthPane - 2*PADDING;
			
			pvGI = new GraphicsInterface(labelWidth, labelWidth, x, y, pvCam);
			
			// draws the particle (not to scale)
			Label particleLabel = new Label(labelWidth, labelWidth) {
				public void render(Graphics g, int x, int y) {
					if (viewedParticle == null) return;
					pvGI.setGraphics(g);
					pvGI.setPos(x, y);
					pvGI.setColor(viewedParticle.getColor());
//					gi.setPPM0(labelWidth / (2 * viewedParticle.getRadius()));
//					gi.zoom = labelWidth / 2 / theParticle.getRadius(); // this will probably become infinite sometimes, but w/e
					viewedParticle.renderAt(pvGI, new CVector());
					pvGI.setGraphics(null);
				}
			};
			
			// draw the particle statistics (mass, charge, etc.)
			// note that if theParticle is null, this causes problems-- so hide the pane (or override render())
			Label statsLabel = new DynamicTextLabel(labelWidth, LINE_HEIGHT,
					() -> viewedParticle.getName(),
					new ValAsStr("m = %2.1e kg", () -> viewedParticle.getMass()),
					new ValAsStr("q = %2.1e C", () -> viewedParticle.getCharge()));
			
			// empty column showing no info (and not taking up screen space)
			expandedStats.add(new Column());
			
			// column showing absolute position, velocity, and acceleration
			expandedStats.add(new Column(
					new DynamicTextLabel(labelWidth, LINE_HEIGHT,
							() -> "Particle Position",
							new ValAsStr("x = %2.1e m", () -> viewedParticle.position().x()),
							new ValAsStr("y = %2.1e m", () -> viewedParticle.position().y()),
							new ValAsStr("z = %2.1e m", () -> viewedParticle.position().z())),
					new DynamicTextLabel(labelWidth, LINE_HEIGHT,
							() -> "Particle Velocity",
							new ValAsStr("x = %2.1e m/s", () -> viewedParticle.velocity().x()),
							new ValAsStr("y = %2.1e m/s", () -> viewedParticle.velocity().y()),
							new ValAsStr("z = %2.1e m/s", () -> viewedParticle.velocity().z())),
					new DynamicTextLabel(labelWidth, LINE_HEIGHT,
							() -> "Particle Acceleration",
							new ValAsStr("x = %2.1e m/s/s", () -> viewedParticle.acceleration().x()),
							new ValAsStr("y = %2.1e m/s/s", () -> viewedParticle.acceleration().y()),
							new ValAsStr("z = %2.1e m/s/s", () -> viewedParticle.acceleration().z()))
			));
			
			// column showing relative position and velocity magnitudes
			expandedStats.add(new Column(
					new DynamicTextLabel(labelWidth, LINE_HEIGHT,
							() -> "Distance from Origin",
							new ValAsStr("r = %2.1e m", () -> Vector.distance(viewedParticle.position(), camera.position))),
					new DynamicTextLabel(labelWidth, LINE_HEIGHT,
							() -> "Particle Velocity",
							new ValAsStr("v = %2.1e m/s", () -> viewedParticle.velocity().magnitude())),
					new DynamicTextLabel(labelWidth, LINE_HEIGHT,
							() -> "Particle Acceleration",
							new ValAsStr("a = %2.1e m/s/s", () -> viewedParticle.acceleration().magnitude()))
			));

			// column for other options
			expandedStats.add(new Column(
					new Button(labelWidth, LINE_HEIGHT, "Clear Trace", () -> viewedParticle.trace.clear()),
					new Button(labelWidth, LINE_HEIGHT, "Toggle Trace", () -> viewedParticle.trace.toggle()),
					new Button(labelWidth, LINE_HEIGHT, "Zoom Fit", () -> { 
						setView(viewedParticle.getRadius()*3);
						camera.position.become(viewedParticle.position());
					})));
			
			
			// button for going to particle
			gotoButton = new Button(labelWidth, LINE_HEIGHT, "Go To", () -> {
				camera.position.become(viewedParticle.position());
			});

			
			// buttons that causes the camera to track a particle, or not
			trackButton = new Button(labelWidth, LINE_HEIGHT, "Track", () -> {
				camera.setTrackedParticle(viewedParticle);
				stopTrackingButton.setVisibility(true);
				trackButton.setVisibility(false);
			});
			stopTrackingButton = new Button(labelWidth, LINE_HEIGHT, "Stop Tracking", () -> {
				camera.setTrackedParticle(null);
				trackButton.setVisibility(true);
				stopTrackingButton.setVisibility(false);
			}); 
			stopTrackingButton.setVisibility(false);
			
			
			// buttons that enable taking control of a particle, such as a rocket
			rideButton = new Button(labelWidth, LINE_HEIGHT, "Pilot", () -> { 
				setControlled((Controllable) viewedParticle);
				camera.setImitatedParticle(viewedParticle);
				gi.setNearClippingPlane(viewedParticle.getRadius() * 1.125);
				rideButton.setVisibility(false);
				stopRidingButton.setVisibility(true);
			});
			stopRidingButton = new Button(labelWidth, LINE_HEIGHT, "Exit", () -> {
				setControlled(camera);
				camera.setImitatedParticle(null);
				gi.setNearClippingPlane(0);
				stopRidingButton.setVisibility(false);
				rideButton.setVisibility(viewedParticle instanceof Controllable);
			});
			rideButton.setVisibility(false);
			stopRidingButton.setVisibility(false);
			
			// fuel bar for showing spaceship fuel, when it is a spaceship
			fuelBar = new ProgressBar(labelWidth, LINE_HEIGHT, () -> theRocket.getFuelPercentRemaining());
			fuelBar.setVisibility(false); // unnecessary but w/e
			
			add(particleLabel, statsLabel, gotoButton, trackButton, stopTrackingButton, rideButton, stopRidingButton, fuelBar);
			for (Column c : expandedStats) {
				c.setVisible(false);
				add(c);
			}
			
			pack();
		}
		
		Camera pvCam = new Camera(null);
		GraphicsInterface pvGI;
		
		/**
		 * Particle displayed in this viewer. DO NOT SET DIRECTLY-- use {@link #setViewedParticle(Particle)}
		 * instead, which also handles pane visibility.
		 */
		private Body viewedParticle;
		private Rocket theRocket = null;
		private CircularLinkedList<Column> expandedStats = new CircularLinkedList<>();
		private ProgressBar fuelBar;
		private Button gotoButton;
		private Button stopTrackingButton, trackButton;
		private Button rideButton, stopRidingButton;
		
		/**
		 * Sets the particle, and hides the pane if it isn't showing anything.
		 * @param p the particle
		 */
		public void setViewedParticle(Body p) {
			viewedParticle = p;
			if (p == null) {
				setIsHidden(true);
			} else {
				setIsHidden(false);
				pvCam.position.setZ(viewedParticle.getRadius()*2 / Math.tan(pvGI.getFOV() / 2));
				
				// deal with the control buttons
				boolean trackingP = camera.isTracking(p);
				trackButton.setVisibility(!trackingP);
				stopTrackingButton.setVisibility(trackingP);
				
				if (p == controlled) {
					stopRidingButton.setVisibility(true);
				} else {
					if (controlled != camera)
						stopRidingButton.setVisibility(false);
					rideButton.setVisibility(p instanceof Controllable);
				}
				
				// deal with showing the fuel bar
				if (p instanceof Rocket) {
					theRocket = (Rocket) p;
					fuelBar.setVisibility(true);
				} else {
					fuelBar.setVisibility(false);
					theRocket = null;
				}
				pack();
			}
		}
		
		@Override
		public boolean mouseClicked(int x, int y) {
			if (containsPixel(x, y)) {
				if (!clickLabels(x, y)) {
					expandedStats.get().setVisible(false);
					expandedStats.cycle().setVisible(true);
					pack();
				}
				return true;
			}
			return false;
		}
	}
	
	@Deprecated
	private class TapeMeasure {
		private static final int KNOB_RADIUS = 8;
		
		public TapeMeasure(int x1, int y1, int x2, int y2) {
			knob1 = new CircularPane(x1, y1, KNOB_RADIUS) {
				@Override
				public void setIsHidden(boolean b) {
					super.setIsHidden(b);
					knob2.setIsHidden(b);
					label.setIsHidden(b);
				}
			};
			
			knob2 = new CircularPane(x2, y2, KNOB_RADIUS);
			
			label = new Pane(-1, -1, 0, Pane.LINE_HEIGHT) {
				{padding = 2;}
				@Override
				public void render(Graphics g) {
					if (isHidden()) return;
					
					g.setColor(Pane.colorBackground);
					int x1 = knob1.getXCenter(),
							y1 = knob1.getYCenter(),
							x2 = knob2.getXCenter(),
							y2 = knob2.getYCenter();
					g.drawLine(x1, y1, x2, y2);
					
					double distance = Math.sqrt(square(x1-x2) + square(y1-y2)) / gi.getPPM(0);
					DistanceUnit unit = DistanceUnit.getUnit(distance);
					String text = String.format("%.1f %s", distance/unit.meterEquivalent, unit.abbreviation);
					
					FontMetrics f = g.getFontMetrics();
					setWidth(f.stringWidth(text) + 2*padding);
					if (heightPending)
						setHeight(f.getAscent() - f.getDescent() + 2*padding);
					
					int x = (x1+x2 - getWidth())/2, y = (y1+y2)/2 - getHeight();
					super.setX(x);
					lastX = x;
					super.setY(y);
					lastY = y;
					
					g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), 2*padding, 2*padding);
					g.setColor(Pane.colorText);
					g.drawString(text, x+padding, y+getHeight()-padding);
				}
				
				int lastX = getX(), lastY = getY();
				
				@Override
				public void setX(int x) {
					super.setX(x);
					knob1.setX(knob1.getX() + x - lastX);
					knob2.setX(knob2.getX() + x - lastX);
					lastX = x;
				}
				
				@Override
				public void setY(int y) {
					super.setY(y);
					knob1.setY(knob1.getY() + y - lastY);
					knob2.setY(knob2.getY() + y - lastY);
					lastY = y;
				}
			};
			
			knob1.setIsHidden(true);
		}
		
		private CircularPane knob1, knob2;
		private Pane label;
		boolean heightPending = true;
		
		public void toggleVisibility() {
			knob1.toggleVisibility();
		}
	}
	
	private class Toolbar extends Pane {
		public static final int BUTTON_WIDTH = 80, BUTTON_HEIGHT = 40;
		public static final int TOOLBAR_WIDTH = WIDTH_PANEL, TOOLBAR_HEIGHT = BUTTON_HEIGHT + 2*PADDING;
		public Toolbar() {
			super(0, 0, TOOLBAR_WIDTH, TOOLBAR_HEIGHT);
			isDraggable = false;
			isHoverable = false;
			
			add(-1,
//					new Button(BUTTON_WIDTH, BUTTON_HEIGHT, "Match Scale", () -> setZoom(GraphicsInterface.PIXELS_PER_METER)),
					new Button(BUTTON_WIDTH, BUTTON_HEIGHT, "Toggle GPS", gps::toggleVisibility),
					new Button(BUTTON_WIDTH, BUTTON_HEIGHT, "Toggle Ruler", tm::toggleVisibility),
					new Button(BUTTON_WIDTH, BUTTON_HEIGHT, "Toggle Trace", STPanel.this::toggleTraces),
					new Button(BUTTON_WIDTH, BUTTON_HEIGHT, "Clear Trace", STPanel.this::clearTraces),
					new Button(BUTTON_WIDTH, BUTTON_HEIGHT, "Toggle Names", STPanel.this::toggleNames));
		}
	}
	
	private class MouseHandler implements MouseMotionListener, MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			e = correct(e);
			hud.mouseClicked(e);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			e = correct(e);
			
			// deal with HUD first
			hud.mousePressed(e);
			if (e.isConsumed())
				return;
			
			// else, deal with particles
			pv.setViewedParticle(getParticleAt(e.getX(), e.getY()));
		}
	
		@Override
		public void mouseReleased(MouseEvent e) {
			hud.mouseReleased(e);
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
			e = correct(e);
			hud.mouseDragged(e);
		}
	
		@Override
		public void mouseMoved(MouseEvent e) {
			e = correct(e);
			hud.mouseMoved(e);
		}

		/**
		 * convert event coordinates to that of the jpanel (a minor but crucial correction)
		 * @param e mouse event to be corrected
		 * @return corrected mouse event
		 */
		private MouseEvent correct(MouseEvent e) {
			return e = SwingUtilities.convertMouseEvent(null, e, STPanel.this);
		}
	}
	
	private KeyBinder keyBindings = new KeyBinder(getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), getActionMap());
	
	/**
	 * Establish what keys correspond to what actions. Only call once.
	 */
	private void addKeyBindings() {
		keyBindings.bindKeyStrokeToCommand("pressed SPACE", this::toggleTime);
		keyBindings.bindKeyStrokeToCommand("pressed ENTER", () -> {if (!isRunning) stepTime();});
		keyBindings.bindKeyStrokeToCommand("pressed ESCAPE", () -> System.exit(0));
//		keyBindings.bindKeyStrokeToCommand("pressed X", () -> System.out.println("pressed debug"));
		keyBindings.bindKeyStrokeToCommand("pressed T", this::toggleTraces);
//		addKeyBinding("pressed R", "toggle ruler", tm::toggleVisibility);
		
		keyBindings.bindKeysToLever("UP", "DOWN", zoomScale);
		keyBindings.bindKeysToLever("LEFT", "RIGHT", timeScale);
	}
	
	private Controllable controllable;
	private void setControllable(Controllable c) {
		// if we already are controlling c, no need to change anything
		if (Objects.equals(controllable, c)) return;
		
		if (controllable != null)
			controllable.removeBindingsFrom(keyBindings);
		if (c != null)
			c.addBindingsTo(keyBindings);
		
		controllable = c;
	}
	
	/**
	 * Interface to provide no-argument, no-return lambda expression for key
	 * bindings. 
	 */
	@FunctionalInterface
	public static interface KeyCommand {
		void invoke();
	}
	
	private final Dimension preferredSize = new Dimension(WIDTH_PANEL, HEIGHT_PANEL);
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}
	
}
