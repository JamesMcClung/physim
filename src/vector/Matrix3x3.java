package vector;

public class Matrix3x3 {
	
	public static Matrix3x3 getIdentity() {
		var m = new Matrix3x3();
		for (int i = 0; i < 3; i++)
			m.elements[i][i] = 1;
		return m;
	}
	
	
	public Matrix3x3() { 
		elements = new double[3][3];
	}
	
	public final double[][] elements;
	
	
	
	/**
	 * Creates a new vector equal to the product <b><u>A</u>v</b>, where <u><b>A</b></u> is
	 * this matrix and <b>v</b> is a vector.
	 * @param v the vector
	 * @return the product
	 */
	public Vector getProduct(Vector v) {
		return new CVector(getRow(0).dot(v), getRow(1).dot(v), getRow(2).dot(v));
	}
	
	public Vector getRow(int i) {
		return new CVector(elements[i][0], elements[i][1], elements[i][2]);
	}
	
	public Vector getCol(int i) {
		return new CVector(elements[0][i], elements[1][i], elements[2][i]);
	}
	
	public Matrix3x3 scale(double d) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				elements[i][j] *= d;
		return this;
	}
	
	public double getDeterminant() {
		double det = 0;
		for (int i = 0; i < 3; i++) {
			int j = (i+1)%3;
			int k = (j+1)%3;
			det += elements[1][j] * elements[2][k] - elements[1][k] * elements[2][j];
		}
		return det;
	}
	
	public Matrix3x3 getInverse() {
		var inv = new Matrix3x3();
		double invdet = 1/getDeterminant();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// it's stupid but it works and it took too long to figure these formula out to delete them
				int c1 = j == 0 ? 1 : 0;
				int c2 = (1 + i + j*(j+1)/2) % 3;
				int c3 = j == 2 ? 1 : 2;
				int c4 = (2 + i + j*(j+1)) % 3;
				inv.elements[i][j] = (elements[c1][c2]*elements[c3][c4] - elements[c1][c4]*elements[c3][c2]) * invdet;
			}
		}
		return inv;
	}
	
	public Matrix3x3 transpose() {
		for (int j = 1; j < 3; j++) {
			for (int i = 0; i < j; i++) {
				double temp = elements[i][j];
				elements[i][j] = elements[j][i];
				elements[j][i] = temp;
			}
		}
		return this;
	}
	
	public Matrix3x3 getProduct(Matrix3x3 m) {
		var prod = new Matrix3x3();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				prod.elements[i][j] = getRow(i).dot(m.getCol(j));
		return prod;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(9*5);
		for (var row : elements) {
			for (var e : row) {
				s.append(String.format("%.2f ", e));
			}
			s.append('\n');
		}
		return s.toString();
	}
	
}
