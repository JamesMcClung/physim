package vector;

public class Matrix {
	
	/**
	 * Creates a new matrix.
	 * @param m height of matrix (y)
	 * @param n width of matrix (x)
	 */
	public Matrix(int m, int n) {
		elements = new double[m][n];
		this.m = m;
		this.n = n;
	}
	
	public final double[][] elements;
	public final int m, n;
	
	public void become(Matrix a) {
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				elements[i][j] = a.elements[i][j];
			}
		}
	}

	public static Matrix product(Matrix a, Matrix b) {
		if (a.n != b.m) throw new InvalidMatrixProductException(a, b);
		var c = new Matrix(a.m, b.n);
		
		for (int i = 0; i < c.m; i++) {
			for (int j = 0; j < c.n; j++) {
				for (int k = 0; k < a.n; k++) {
					c.elements[i][j] += a.elements[i][k] * b.elements[k][j];  
				}
			}
		}
		
		return c;
	}
	
	private static class InvalidMatrixProductException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidMatrixProductException(Matrix a, Matrix b) {
			super(String.format("%dx%d matrix cannot be multiplied by %dx%d matrix.", a.m, a.n, b.m, b.n));
		}
	}
}
