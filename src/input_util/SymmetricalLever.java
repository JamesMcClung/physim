package input_util;

public class SymmetricalLever extends Lever {
	
	public SymmetricalLever(double mag, Settable s) {
		magnitude = mag;
		value = s;
	}
	
	private double magnitude;
	private Settable value;
	
	@Override
	protected void flipPos() {
		value.set(magnitude);
	}
	
	@Override
	protected void flipNeu() {
		value.set(0);
	}
	
	@Override
	protected void flipNeg() {
		value.set(-magnitude);
	}
	
	public void setMagnitude(double mag) {
		magnitude = mag;
		// adjusts whatever this lever controls, as well
		pushUp.set(pushUp.is());
	}
	
	@FunctionalInterface
	public static interface Settable {
		void set(double val);
	}

}
