package vector;

public class MatrixSq extends Matrix {
	
	public MatrixSq(int n) {
		super(n, n);
	}
	
	/**
	 * Sets a = Ma, where M is this matrix.
	 * @param a the transformed matrix
	 */
	public void transformLeft(Matrix a) {
		a.become(Matrix.product(this, a));
	}
	
	/**
	 * Sets a = aM, where M is this matrix.
	 * @param a the transformed matrix
	 */
	public void transformRight(Matrix a) {
		a.become(Matrix.product(a, this));
	}

}
