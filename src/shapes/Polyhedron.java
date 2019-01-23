package shapes;

import java.util.HashSet;
import java.util.Set;

import vector.Vector;

public class Polyhedron extends Frame {
	
	public Polyhedron(Orientation o, Triangle...faces) {
		super(o, faces);
		for (var face : faces)
			this.faces.add(face);
	}
	
	public Polyhedron(Triangle...faces) {
		this(new Orientation(), faces);
	}
	
	private Set<Triangle> faces = new HashSet<>();
	
	/**
	 * Uses the formula <i>V = 1/3 sum(<b>x</b>•<b>a</b>)</i>, where <b>x</b>
	 * is an arbitrary point on the face (e.g., a vertex), <b>a</b> is the area vector pointing outwards
	 * of the face, and <i>sum</i> sums over each face.
	 * <p>
	 * This works for both convex and concave polyhedra.
	 * @return the volume
	 */
	public double getVolume() {
		double sum = 0;
		for (Triangle face : faces) {
			sum += face.vertex(0).dot(face.getAreaVector());
		}
		return sum/3;
	}
	
	/**
	 * Calculates the cross section of the polyhedron along a given axis. This is done by taking the dot
	 * product of each face with the axis and adding up these values, then dividing by 2.
	 * <p>
	 * Note: for concave polyhedra, this might return unexpected results. Indentations would be counted
	 * as part of the cross section.
	 * <p>
	 * Note 2: Axis is assumed to be a unit vector. If it is not, then the cross product will be off by
	 * a factor of the axis' magnitude.
	 * @param axis unit vector along which to get cross section
	 * @return the cross section
	 */
	public double getCrossSection(Vector axis) {
		double sum = 0;
		for (var face : faces) {
			sum += Math.abs(face.getAreaVector().dot(axis));
		}
		return sum/2;
	}
	
	public Set<Triangle> faces() {
		return faces;
	}
	
	// for testing purposes
//	public static void main(String[] args) {
//		Vector apex = new CVector(0,1,0);
//		Vector[] base = Frame.getVerticesOnRing(3, 0, 1);
//		Polyhedron q = new Polyhedron(new Triangle(apex, base[0], base[1]),
//				new Triangle(apex, base[1], base[2]),
//				new Triangle(apex, base[2], base[0]),
//				new Triangle(base[2], base[1], base[0]));
//		
////		Polyhedron p = new Polyhedron(new Triangle(vec(1,0,0), vec(1,1,0), vec(1,0,1)));
////		System.out.println(p.getVolume());
//		
//		System.out.println(q.getVolume());
//		System.out.println(q.getCrossSection(new CVector(0,1,0)));
//	}

}
