package shapes;

import java.util.HashSet;
import java.util.Set;

import vector.CVector;
import vector.Quaternion;
import vector.Vector;

public class Frame {
	
	public static Vector[] getVerticesOnRing(int numVertices, double y, double radius) {
		Vector[] vertices = new Vector[numVertices];
		
		double angleBetweenVertices = Math.PI * 2 / numVertices;
		for (int i = 0; i < numVertices; i++) {
			double theta = i * angleBetweenVertices;
			vertices[i] = new CVector(radius * Math.cos(theta), y, radius * Math.sin(theta));
		}
		
		return vertices;
	}
	
	public static Edge[] linkRing(Vector...vertices) {
		if (vertices.length == 2) {
			return new Edge[] { new Edge(vertices[0], vertices[1]) };
		}
		
		Edge[] edges = new Edge[vertices.length];
		edges[0] = new Edge(vertices[0], vertices[vertices.length - 1]);
		for (int i = 1; i < edges.length; i++) {
			edges[i] = new Edge(vertices[i], vertices[i-1]);
		}
		
		return edges;
	}
	
	public static Edge[] linkOneToMany(Vector one, Vector...many) {
		Edge[] edges = new Edge[many.length];
		for (int i = 0; i < edges.length; i++) {
			edges[i] = new Edge(one, many[i]);
		}
		return edges;
	}
	
	public static Edge[] linkBijectively(Vector[] a, Vector[] b) {
		if (a.length != b.length) throw new RuntimeException("not same number of vertices to link");
		Edge[] edges = new Edge[a.length];
		for (int i = 0; i < edges.length; i++) {
			edges[i] = new Edge(a[i], b[i]);
		}
		return edges;
	}
	
	public Frame(Orientation orientation, Edge...edges) {
		for (var e : edges) {
			this.edges.add(e);
			vertices.add(e.v1);
			vertices.add(e.v2);
		}
		this.orientation = orientation;
	}
	
	public Frame(Edge...edges ) {
		this(new Orientation(), edges);
	}
	
	public Frame(Orientation orientation, Edge[]...edgeses) {
		for (var es : edgeses) {
			for (var e : es) {
				this.edges.add(e);
				vertices.add(e.v1);
				vertices.add(e.v2);
			}
		}
		this.orientation = orientation;
	}
	
	public Frame(Edge[]...edgeses) {
		this(new Orientation(), edgeses);
	}
	
	public Frame(Orientation o, Triangle...faces) {
		this.orientation = o;
		for (var face  : faces) {
			edges.addAll(face.getEdges());
			vertices.addAll(face.getVertices());
		}
	}
	
	public Frame(Triangle...faces) {
		this(new Orientation(), faces);
	}
	
	private final Orientation orientation;
	private final Set<Edge> edges = new HashSet<>();
	private final Set<Vector> vertices = new HashSet<>();
	
	/**
	 * Rotates the polyhedron and its orientation by q.
	 * @param q the rotation quaternion
	 * @see #rotateVertices(Quaternion)
	 */
	public void rotate(Quaternion q) {
		rotateVertices(q);
		orientation.rotate(q);
	}
	
	/**
	 * Rotates just the polyhedron by q, without changing its orientation.
	 * @param q the rotation quaternion
	 * @see #rotate(Quaternion)
	 */
	public void rotateVertices(Quaternion q) {
		for (var v : vertices)
			q.useToRotate2(v);
	}
	
	public Set<Edge> edges() {
		return edges;
	}
	
	public Set<Vector> vertices() {
		return vertices;
	}

	/**
	 * Returns a duplicate of the orientation's specified axis.
	 * @param axis the specified axis
	 * @return the axis
	 */
	public Vector getOrientationAxis(int axis) {
		return orientation.axes[axis];
	}
	
	/**
	 * Returns this.orientation.
	 * @return a reference to this frame's actual orientation
	 */
	public Orientation orientation() {
		return orientation;
	}
	
	/**
	 * Centers the polyhedron so that the center of "mass" is 0. Each vertex is weighted equally.
	 */
	public void center() {
		double sumX = 0, sumY = 0, sumZ = 0;
		for (var vertex : vertices) {
			sumX += vertex.x();
			sumY += vertex.y();
			sumZ += vertex.z();
		}
		
		double dx = -sumX/vertices.size(), dy = -sumY/vertices.size(), dz = -sumZ/vertices.size();
		for (var vertex : vertices) {
			vertex.translate(dx, dy, dz);
		}
	}
	
	/**
	 * Calculates the radius of the smallest sphere that contains every vertex. This is equivalent to the
	 * greatest vertex magnitude. 
	 * @return the radius
	 */
	public double getRadiusOfBoundingSphere() {
		double radiusSq = 0;
		for (var vertex : vertices) {
			radiusSq = Math.max(radiusSq, vertex.magnitudeSq());
		}
		return Math.sqrt(radiusSq);
	}
}
