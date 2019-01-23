package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Objects;
import java.util.function.Function;

import shapes.Frame;
import shapes.Polyhedron;
import shapes.Triangle;
import util.Angle;
import util.DistanceUnit;
import vector.CVector;
import vector.HomogenousCoordinate;
import vector.MatrixSq;
import vector.Vector;

/**
 * An interface between spacetime coordinates and pixel coordinates. It has a
 * position and a dimension, which determine where the "window" into virtual
 * space is on the screen.
 * 
 * @author James McClung
 */
public class GraphicsInterface {

	/**
	 * The pixel-to-irl meter ratio. Set {@link #moveZ} to this for to-scale
	 * simulations.
	 */
	public static final double PIXELS_PER_METER = 4341.32;

	/**
	 * Creates a graphics interface.
	 * 
	 * @param width             width of the window into virtual space
	 * @param height            height of the window into virtual space
	 * @param x                 x-coordinate of leftmost pixel of window
	 * @param y                 y-coordinate of topmost pixel of window
	 * @param ppm               pixel-to-meter ratio (see {@link #PIXELS_PER_METER})
	 * @param origin            the origin in virtual meters that the observer
	 *                          stands
	 * @param backupOrientation the orientation in space the viewer is in
	 */
	public GraphicsInterface(int width, int height, int x, int y, Camera observer) {
		setDim(width, height);
		setPos(x, y); // sets offsets as well
		camera = Objects.requireNonNullElse(observer, new Camera(null));
		setPerspectiveParameters(Math.PI / 2, 0, Double.POSITIVE_INFINITY);
	}

	private Camera camera;

	// Different forms of projection
	public static final int ORTHOGRAPHIC = 0, PERSPECTIVE = 1, EXPONENTIAL = 2;
	private int projectionType = PERSPECTIVE;

	// Orthograpic projection, and other forms
	/**
	 * The pixel-to-sim meter ratio at the image plane.
	 * 
	 * @see #PIXELS_PER_METER
	 */
	@Deprecated
	private double ppm0 = PIXELS_PER_METER;
	private double orthoPPM = PIXELS_PER_METER; 

	// Perspective projection
	private double fov; // field of view, in radians
	private double near, far; // clipping planes

	// Exponential projection
	double flatness = 1e4; // too low makes it sharp, too high makes it flat.

	private int width, height; // in pixels
	private int x, y;
	// the pixel offsets of x and y (in other words, the on-screen location of the
	// origin)
	public int xPixelOffset, yPixelOffset;
	/**
	 * The graphics object to draw on. It must be reset each time!
	 */
	private Graphics2D g;

	void setGraphics(Graphics graphics) {
		g = (Graphics2D) graphics;
		if (g != null) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	/**
	 * Sets the color to c
	 * 
	 * @param c the color
	 */
	public void setColor(Color c) {
		g.setColor(c);
	}

	/**
	 * Fills the entire screen.
	 */
	public void fill() {
		g.fillRect(x, y, width, height);
	}

	/**
	 * Fills the entire screen with a specific color.
	 * 
	 * @param c the color
	 */
	public void fill(Color c) {
		g.setColor(c);
		fill();
	}

	/**
	 * Draws a line from r1 to r2, which are abosulte coordinates.
	 * 
	 * @param r1 position 1, in m
	 * @param r2 position 2, in m
	 */
	public void drawLine(Vector r1, Vector r2) {
		drawLineFromRel(getRelativePosition(r1), getRelativePosition(r2));
	}

	public void drawLineFromRel(Vector r1Rel, Vector r2Rel) {
		Point p1 = getPositionOnScreen(r1Rel);
		if (p1 == null)
			return;
		Point p2 = getPositionOnScreen(r2Rel);
		if (p2 == null)
			return;
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Draws the outline of a circle centered at position
	 * 
	 * @param position center of circle in m
	 * @param radius   radius of circle, in m
	 */
	public void drawCircle(Vector position, double radius) {
		var relPos = getRelativePosition(position);
		int r = (int) (radius * getPPM(relPos.z()));
		Point p = getPositionOnScreen(relPos);
		if (p == null)
			return;
		g.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);
	}

	/**
	 * Draws the outline of a circe centered the specified absolute position.
	 * 
	 * @param position       center of circle in m
	 * @param radiusInPixels radius of circle in pixels
	 */
	public void drawCircleAbsoluteRadius(Vector position, int radiusInPixels) {
		Point p = getPositionOnScreen(getRelativePosition(position));
		if (p == null)
			return;
		g.drawOval(p.x - radiusInPixels, p.y - radiusInPixels, 2 * radiusInPixels, 2 * radiusInPixels);
	}

	/**
	 * Draws a single point at the specified absolute position
	 * 
	 * @param position the position, in m
	 */
	public void drawPoint(Vector position) {
		Point p = getPositionOnScreen(getRelativePosition(position));
		if (p == null)
			return;
		g.drawLine(p.x, p.y, p.x, p.y);
	}

	/**
	 * Used by {@link #drawParticle(Vector, int)} and set by
	 * {@link #setDrawParticlesAsDots(boolean)}.
	 * <p>
	 * Defaults to <code>false</code>.
	 */
	private boolean drawParticlesAsDots = false;

	/**
	 * Draws either a single point (via {@link #drawPoint(Vector)} or an empty
	 * circle (via {@link #drawCircleAbsoluteRadius(Vector, int)} at the specified
	 * position. The sole factor in choosing which to draw is
	 * {@link #drawParticlesAsDots}, which defaults to <code>false</code> and is set
	 * exclusively by {@link #setDrawParticlesAsDots(boolean)}.
	 * 
	 * @param position       the absolute position of the particle (m)
	 * @param radiusInPixels the radius of the circle, which might not be used
	 */
	public void drawParticle(Vector position, int radiusInPixels) {
		if (drawParticlesAsDots)
			drawPoint(position);
		else
			drawCircleAbsoluteRadius(position, radiusInPixels);
	}

	/**
	 * Sets {@link #drawParticlesAsDots}, which defaults to <code>false</code>. The
	 * sole purpose of that field is its name. Circles are much more computationally
	 * expensive than dots, so when there are many particles, it is better to draw
	 * them as dots.
	 * 
	 * @param b true or false
	 */
	void setDrawParticlesAsDots(boolean b) {
		drawParticlesAsDots = b;
	}

	/**
	 * Fills in a circle centered at position
	 * 
	 * @param position center of circle in m
	 * @param radius   radius of circle, in m
	 */
	public void fillCircle(Vector position, double radius) {
		var relPos = getRelativePosition(position);
		int r = (int) (radius * getPPM(relPos.z()));
		Point p = getPositionOnScreen(relPos);
		if (p == null)
			return;
		g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
	}

	public void drawString(String s, Vector position) {
		var p = getPositionOnScreen(getRelativePosition(position));
		if (p == null)
			return;
		g.drawString(s, p.x, p.y);
	}

	/**
	 * Draws in the edges of a polyhedron, translated by some position.
	 * 
	 * @param p        the polyhedron
	 * @param position the origin
	 */
	public void drawPolyhedronOutline(Frame p, Vector position) {
		for (var edge : p.edges()) {
			drawLine(edge.v1.duplicate().translate(position), edge.v2.duplicate().translate(position));
		}
	}
	
	/**
	 * Fills in the polyhedron's faces. The color of each face is determined by the intensity of light at that angle.
	 * The color is based on the current color of the graphics object, and this method ends by setting the color back to normal.
	 * @param p the polyhedron
	 * @param position the center of the polyhedron, in absolute coords
	 * @param light the direction and magnitude of the light (let mag be 1 for natural brightness, 0 for darkness) 
	 */
	public void fillPolyhedron(Polyhedron p, Vector position, Vector light) {
		Color c = g.getColor();
		for (var face : p.faces()) {
			fillFace(face, position, light, c);
		}
		g.setColor(c);
	}
	
	/**
	 * Fills a single face of a polyhedron.
	 * @param t the face
	 * @param position teh center of the polyhedron
	 * @param light light direction and magnitude
	 * @param baseColor natural color of the face
	 */
	private void fillFace(Triangle t, Vector position, Vector light, Color baseColor) {
		Color c = getColorOfFace(t, light, baseColor);
		if (c == Color.BLACK)
			return;
		
		var polygon = getPolygon(t, position);
		if (polygon != null) {
			g.setColor(c);
			g.fillPolygon(polygon);
		}
	}
	
	/**
	 * Turns a polyhedron face into an actual Polygon object describing its on-screen appearance.
	 * @param t the face
	 * @param position the center of the polyhedron
	 * @return the polygon
	 */
	private Polygon getPolygon(Triangle t, Vector position) {
		final int npts = 3;
		int[] xpts = new int[npts];
		int[] ypts = new int[npts];
		int i = 0;
		for (var vertex : t.getVertices()) {
			Point p = getPositionOnScreen(getRelativePosition(new CVector(position).translate(vertex)));
			if (p == null)
				return null;
			xpts[i] = p.x;
			ypts[i] = p.y;
			i++;
		}
		return new Polygon(xpts, ypts, npts);
	}
	
	/**
	 * Calculates the color of a polyhedron face based on lighting and angle.
	 * @param face the face
	 * @param light the lighting direction and magnitude (mag 1 = natural brightness, mag 0 = dark)
	 * @param baseColor the natural color of the face
	 * @return the color; if the face is being lit from the back, this will always be black
	 */
	private Color getColorOfFace(Triangle face, Vector light, Color baseColor) {
		if (light == null)
			return baseColor;
		
		int r = baseColor.getRed();
		int g = baseColor.getGreen();
		int b = baseColor.getBlue();
		
		final int scale = 100;
		var areaVec = face.getAreaVector();
		int brightness = (int) (scale * areaVec.dot(light) / areaVec.magnitude());
		
		r = r * brightness / scale;
		g = g * brightness / scale;
		b = b * brightness / scale;
		
		if (r < 0 || g < 0 || b < 0)
			return Color.BLACK;
		
		return new Color(r, g, b);
	}

	public DistanceUnit getRecommendedUnit(int lengthInPixels) {
		return DistanceUnit.getUnit(lengthInPixels / getPPM(0));
	}

	/**
	 * Calculates the pixels per meter at a given z-coordinate of the viewer's
	 * reference frame.
	 * 
	 * @param z the relative z-coordinate, which ought to be negative
	 * @return pixels per meter at that layer
	 */
	@Deprecated
	public double getPPM(double z) {
		switch (projectionType) {
		case PERSPECTIVE:
			return -width / (z * fov * 2); // TODO this should depend on distance to camera, not just z
		case EXPONENTIAL:
			return ppm0 * Math.exp(z / flatness);
		case ORTHOGRAPHIC:
			return orthoPPM;
		default:
			return ppm0;
		}
	}

	/**
	 * Calculates the pixels per meter at a given z-coordinate of the viewer's
	 * reference frame.
	 * 
	 * @param rPos the relative position
	 * @return pixels per meter at that spot
	 */
	public double getPPM2(Vector rPos) {
		switch (projectionType) {
		case PERSPECTIVE:
			return width / (rPos.magnitude() * fov * 2); // is that supposed to be mag, not z?
		case EXPONENTIAL:
			return ppm0 * Math.exp(rPos.z() / flatness);
		case ORTHOGRAPHIC:
		default:
			return ppm0;
		}
	}

	private final double MIN_FOV = 0, MAX_FOV = Math.PI, MID_FOV = (MIN_FOV + MAX_FOV) / 2;

	public void scaleView(double sf) {
		switch (projectionType) {
		case PERSPECTIVE:
			if (fov < MID_FOV) {
				setFOV(MIN_FOV + (fov - MIN_FOV) * sf);
			} else {
				setFOV(MAX_FOV - (MAX_FOV - fov) / sf);
			}
			break;
		case ORTHOGRAPHIC:
		case EXPONENTIAL:
			ppm0 *= sf;
		}
	}

	public void setOrthographicPPM(double ppm) {
		orthoPPM = ppm;
	}
	
	/**
	 * Recalculates the projection transform matrix of the perspective projection.
	 * 
	 * @param fov  field of view, in radians (between 0 and pi, or else risk strange
	 *             behavior)
	 * @param near distance to near cutoff plane, in meters
	 * @param far  distance to far cutoff plane, in meters (infinity is recommended)
	 */
	public void setPerspectiveParameters(double fov, double near, double far) {
		this.fov = fov;
		this.near = near;
		this.far = far;
		perspectiveProjectionTransform = HomogenousCoordinate.getPerspectiveProjection(fov, near, far);
	}

	/**
	 * Sets the distance to the near clipping plane for the perspective projection
	 * style.
	 * 
	 * @param near distance to plane, in meters
	 */
	public void setNearClippingPlane(double near) {
		this.near = near;
		perspectiveProjectionTransform = HomogenousCoordinate.getPerspectiveProjection(fov, near, far);
	}

	/**
	 * Sets the distance to the far clipping plane for the perspective projection
	 * style.
	 * 
	 * @param far distance to plane, in meters
	 */
	public void setFarClippingPlane(double far) {
		this.far = far;
		perspectiveProjectionTransform = HomogenousCoordinate.getPerspectiveProjection(fov, near, far);
	}

	/**
	 * Sets the total viewing angle for the perspective projection style. For
	 * example, a human has a viewing angle of about 210 degrees.
	 * 
	 * @param fov the field of view, in radians (really should never be outside of
	 *            0-pi)
	 */
	public void setFOV(double fov) {
		this.fov = fov;
		perspectiveProjectionTransform = HomogenousCoordinate.getPerspectiveProjection(fov, near, far);
	}

	public double getNearClippingPlane() {
		return near;
	}

	public double getFarClippingPlane() {
		return far;
	}

	public double getFOV() {
		return fov;
	}

	public Angle getFOVAngle() {
		return new Angle(fov);
	}

	/**
	 * Determines if the position is on screen.
	 * 
	 * @param position the position
	 * @return the relative position if it is; null if not
	 */
	public boolean isOnScreen(Vector position) {
		Vector relPos = getRelativePosition(position);
		Vector projPos = HomogenousCoordinate.getProjection(relPos, perspectiveProjectionTransform);

		double projx = projPos.x(), projy = projPos.y(), projz = projPos.z();
		return -1 <= projx && projx <= 1 && -1 <= projy && projy <= 1 && 0 <= projz && projz <= 1;
	}

	/**
	 * Calculates the curve in space that maps to the point (x, y) on the screen.
	 * The curve is in the reference frame of the viewer, NOT the absolute origin.
	 * Use {@link #getRelativePosition(Vector)} to compare to the vectors on this
	 * curve.
	 * 
	 * @param x x-coordinate of pixel on screen
	 * @param y y-coordinate of pixel on screen
	 * @return the curve as a function of (relative) z
	 */
	public Function<Double, Vector> getContourLine(int x, int y) {
		return new Function<>() {
			double xCoef = (x - xPixelOffset);
			double yCoef = (yPixelOffset - y);
			int px = x;
			int py = y;

			@Override
			public Vector apply(Double z) {
				switch (projectionType) {
				case PERSPECTIVE:
					double s = perspectiveProjectionTransform.elements[0][0];
					double x = (1 - 2d * px / width) * z / s;
					double y = (2d * py / height - 1) * z / s;
					return new CVector(x, y, z);
				default:
					double ppm = getPPM(z);
					return new CVector(xCoef / ppm, yCoef / ppm, z);
				}
			}
		};
	}

	/**
	 * Gets the position of a point in space relative to the origin, using the
	 * viewer's orientation.
	 * 
	 * @param absolutePosition the absolute position in space
	 * @return the position of the point in the viewer's frame of reference
	 */
	public Vector getRelativePosition(Vector absolutePosition) {
		return absolutePosition.difference(camera.position).setBasis(camera.orientation);
	}

	public double getRelativeZ(Vector absolutePosition) {
		return absolutePosition.difference(camera.position).dot(camera.orientation.zAxis());
	}

	private MatrixSq perspectiveProjectionTransform;

	/**
	 * Gets the on-screen position, relative to the spacetime panel, of the
	 * position. The origin is assumed to be 0,0,0 with standard orientation.
	 * 
	 * @param relativePosition relative coordinates in space (meters)
	 * @return coordinates on screen (pixels), or null if it is behind the near
	 *         plane
	 */
	private Point getPositionOnScreen(Vector relativePosition) {
		if (projectionType == PERSPECTIVE) {
			var proj = HomogenousCoordinate.getProjection(relativePosition, perspectiveProjectionTransform);

//			if (proj.z() < 0 || 1 < proj.z()) // TODO this should work, but doesnt, because projz is always 1???
//				return null;
			if (relativePosition.z() > -near || relativePosition.z() < -far)// this is temporary fix
				return null;

			int x = (int) ((proj.x() + 1) * .5 * width) + this.x;
			int y = (int) ((1 - (proj.y() + 1) * .5) * height) + this.y;

			return new Point(x, y);

		} // else

		double ppm = getPPM(relativePosition.z());
		return new Point((int) (relativePosition.x() * ppm) + xPixelOffset,
				(int) (-relativePosition.y() * ppm) + yPixelOffset);
	}

	public void setProjectionType(int type) {
		projectionType = type;
	}

	/**
	 * Sets the position of where the graphics interface puts its "window" into the
	 * virtual space. X and Y offsets are also recalcuated.
	 * 
	 * @param x x-coordinate, in pixels, of left of the window
	 * @param y y-coordinate, in pixels, of top of the window
	 */
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
		xPixelOffset = x + width / 2;
		yPixelOffset = y + height / 2;
	}

	/**
	 * Sets the width and height of the "window" of the virtual space. X and Y
	 * offsets are also recalculated.
	 * 
	 * @param width  width, in pixels, of window
	 * @param height height, in pixels, of the window
	 */
	public void setDim(int width, int height) {
		this.width = width;
		this.height = height;
		xPixelOffset = x + width / 2;
		yPixelOffset = y + height / 2;
	}

}