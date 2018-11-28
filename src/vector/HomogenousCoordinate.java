package vector;

public class HomogenousCoordinate extends Matrix {
	
	public HomogenousCoordinate(Vector v) {
		super(1,4);
		elements[0][0] = v.x();
		elements[0][1] = v.y();
		elements[0][2] = v.z();
		elements[0][3] = 1;
	}
	
	public CVector getCartesian() {
		return new CVector(elements[0][0] / elements[0][3], 
				elements[0][1] / elements[0][3], 
				elements[0][2] / elements[0][3]);
	}
	
	// see https://www.scratchapixel.com/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix/building-basic-perspective-projection-matrix
	// for where I got this from
	public static MatrixSq getPerspectiveProjection(double fov, double near, double far) {
		var pp = new MatrixSq(4);
		
		double s = 1 / Math.tan(fov/2);
		pp.elements[0][0] = s;
		pp.elements[1][1] = s;
		pp.elements[2][3] = -1;
		pp.elements[2][2] = far == Double.POSITIVE_INFINITY ? -1 : -far / (far - near);
		pp.elements[3][2] = pp.elements[2][2] * near;
		
		return pp;
	}
	
	/**
	 * Transforms a vector to a point on the screen according to a perspective projection.
	 * X and Y are mapped from -1 to 1, while Z is mapped from 0 to 1 (representing the near
	 * and far clipping planes, respectively). Coordinates outside of this box are off-screen. 
	 * @param v the vector, in the reference frame of the camera/viewer
	 * @param perspectiveProjection the matrix representing the projection
	 * @return the vector representing a point on the screen (ignore z, except for checking
	 * if it is between 0 and 1).
	 */
	public static Vector getProjection(Vector v, MatrixSq perspectiveProjection) {
		var proj = new HomogenousCoordinate(v);
		perspectiveProjection.transformRight(proj);
		return proj.getCartesian();
	}

}
