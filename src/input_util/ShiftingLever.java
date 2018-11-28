package input_util;


public class ShiftingLever extends Lever {

	public ShiftingLever(Shiftable s, double factor) {
		super();
		sfPos = factor;
		sfNeg = -sfPos;
		sfNeu = 0;
		currentSF = sfNeu;
		value = s;
	}
	
	private Shiftable value;
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
		value.shift(currentSF);
	}
	
	@FunctionalInterface
	public static interface Shiftable {
		void shift(double sf);
	}
	
	
}
