package shapes;

import java.util.Collection;
import java.util.LinkedList;

import util.CircularLinkedList;
import vector.CVector;
import vector.Vector;

public class Triangle {
	
	public static Triangle tri(Vector v1, Vector v2, Vector v3) {
		return new Triangle(v1, v2, v3);
	}
	
	public static Triangle[] getOpenPyramid(Vector apex, Vector...baseVertices) {
		Triangle[] sides = new Triangle[baseVertices.length];
		sides[0] = new Triangle(apex, baseVertices[0], baseVertices[sides.length-1]);
		for (int i = 1; i < sides.length; i++) {
			sides[i] = new Triangle(apex, baseVertices[i-1], baseVertices[i]);
		}
		return sides;
	}
	
	/**
	 * Creates a new triangle with the given vectors as vertices. Their order is such that the triangle
	 * faces away from the interior, meaning that the vertices are traversed counterclockwise from the
	 * point of view of someone standing on the outside face.
	 * <p>
	 * For a convex polygon, any point contained in the polygon can serve as the interior of all of its
	 * faces.
	 * @param v1 a vertex
	 * @param v2 another vertex
	 * @param v3 the last vertex
	 * @param interior any point on the side of the triangle's interior face
	 */
	public Triangle(Vector v1, Vector v2, Vector v3, Vector interior) {
//		vertices = new Vector[] { v1, v2, v3 };
		vert.add(v1);
		vert.add(v2);
		vert.add(v3);
		// super lazy test, but it works:
		// if the area vector faces inward, then flip v2 and v3
		if (getAreaVector().dot(v1.difference(interior)) < 0) {
			vert.set(1, v3);
			vert.set(2, v2);
//			vertices[1] = v3;
//			vertices[2] = v2;
		}
	}
	
	/**
	 * Creates a new triangle with the given vectors as vertices and the origin as the interior.
	 * @param v1 a vertex
	 * @param v2 another vertex
	 * @param v3 the last vertex
	 * @see #Triangle(Vector, Vector, Vector, Vector)
	 */
	public Triangle(Vector v1, Vector v2, Vector v3) {
		this(v1, v2, v3, new CVector());
	}
	
//	private final Vector[] vertices;
	private final CircularLinkedList<Vector> vert = new CircularLinkedList<>();
	
	/**
	 * A triangle with two sides u and v, where u and v are vectors, has an area vector equal to
	 * 1/2 u x v. An area vector is normal to the surface and its magnitude is equal to the area.
	 * There are two possible area vectors, most likely, only one is wanted. The two area vectors
	 * are negations of each other, so if this returns the wrong one, then scale it by -1.
	 * @return the area vector
	 */
	public Vector getAreaVector() {
		Vector u = vert.get(1).difference(vert.get(0)),
				v = vert.get(2).difference(vert.get(0));
		return u.cross(u, v).scale(.5);
	}
//	
//	public Vector vertex(int index) {
//		return vertices[index];
//	}
	
	public Vector vertex(int index) {
		return vert.get(index);
	}
	
	public Collection<Vector> getVertices() {
		return new LinkedList<Vector>(vert);
	}
	
	public Collection<Edge> getEdges() {
		LinkedList<Edge> edges = new LinkedList<>();
		for (int i = 0; i < 3; i++)
			edges.add(new Edge(vert.get(), vert.cycle()));
		return edges;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Triangle) {
			return vert.isEquivalent(((Triangle) o).vert);
		}
		return false;
	}

}
