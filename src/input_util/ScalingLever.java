package input_util;

/**
 * 
 * A lever that scales a value up or down when {@link #scaleVal()} is called.
 * 
 * @author James McClung
 *
 */
public class ScalingLever extends Lever {

	public ScalingLever(Scalable s, double factor) {
		super();
		sfPos = factor;
		sfNeg = 1/sfPos;
		sfNeu = 1;
		currentSF = sfNeu;
		value = s;
	}
	
	private Scalable value;
	private double sfPos, sfNeg, sfNeu;
	private double currentSF;
	
	@Override
	protected void flipPos() {
		currentSF = sfPos;
	}
	
	@Override
	protected void flipNeu() {
		currentSF = sfNeu;
	}
	
	@Override
	protected void flipNeg() {
		currentSF = sfNeg;
	}
	
	public void scaleVal() {
		value.scale(currentSF);
	}

	@FunctionalInterface
	public static interface Scalable {
		void scale(double sf);
	}

}
