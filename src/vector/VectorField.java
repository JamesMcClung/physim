package vector;

@FunctionalInterface
public interface VectorField {
	
	/**
	 * A vector function that returns a vector. The return type is not specific- PVector, CVector, etc.
	 * could all be returned.  
	 * @param position the position vector
	 * @return the resulting vector
	 */
	Vector getVectorAt(Vector position);

}
