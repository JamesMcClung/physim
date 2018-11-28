package shapes;

import static vector.CVector.vec;

import vector.Matrix3x3;
import vector.Quaternion;
import vector.Vector;

public class Orientation {
	
	public static final int X_AXIS = 0, Y_AXIS = 1, Z_AXIS = 2;
	
	/**
	 * Creates a transformation matrix to take a vector from o1 to o2.
	 * @param o1 current basis of vector
	 * @param o2 desired basis of vector
	 */
	public static Matrix3x3 getTransformationMatrix(Orientation o1, Orientation o2) {
		var m = new Matrix3x3();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				m.elements[i][j] = o2.axes[i].dot(o1.axes[j]);
			}
		}
		return m;
	}
	
	/**
	 * Creates a new orientation. Defaults to x=(1,0,0), y=(0,1,0), z=(0,0,1).
	 * @see #Orientation(Vector, Vector, Vector)
	 */
	public Orientation() {
		axes = new Vector[] { vec(1,0,0), vec(0,1,0), vec(0,0,1) };
		totalRotation = new Quaternion(1,0,0,0);
	}
	
	public final Vector[] axes;
	public final Quaternion totalRotation;
	
	/**
	 * Rotates the axes by a rotation quaternion.
	 * @param rotation the rotation quaternion
	 */
	public void rotate(Quaternion rotation) {
		for (Vector axis : axes) {
			rotation.useToRotate2(axis);
		}
		totalRotation.multiplyByLeft(rotation); 
	}
	
	/**
	 * Rotates around the given axis.
	 * @param axis either {@link #X_AXIS}, {link #Y_AXIS}, or {link #Z_AXIS}
	 * @param angle angle to rotate by, in radians
	 */
	public void rotateAroundAxis(int axis, double angle) {
		rotate(Quaternion.getRotationQuaternion(angle, axes[axis]));
	}
	
	public Vector xAxis() {
		return axes[X_AXIS];
	}
	
	public Vector yAxis() {
		return axes[Y_AXIS];
	}
	
	public Vector zAxis() {
		return axes[Z_AXIS];
	}
	
	public void become(Orientation o) {
		for (int i = 0; i < axes.length; i++)
			axes[i].become(o.axes[i]);
		totalRotation.become(o.totalRotation);
	}
	
	@Override
	public String toString() {
		return String.format("x =  %s%ny = %s%nz = %s", axes[X_AXIS], axes[Y_AXIS], axes[Z_AXIS]);
	}

}
