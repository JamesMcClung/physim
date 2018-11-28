package input_util;

public class Lever {
	
	public Lever(FlipAction flipPositive, FlipAction flipNeutral, FlipAction flipNegative) {
		this.flipPositive = flipPositive;
		this.flipNeutral = flipNeutral;
		this.flipNegative = flipNegative;
	}
	
	/**
	 * Instead of passing lambdas, just override {@link #flipPos()}, {@link #flipNeu()},
	 * and {@link #flipNeg()}.
	 */
	protected Lever() { }

	protected FlipAction flipPositive, flipNeutral, flipNegative;
	protected boolean isPos = false, isNeu = true, isNeg = false;
	
	public final BinaryState pushUp = new BinaryState() {
		@Override
		public void set(boolean b) {
			super.set(b);
			if (b && pushDown.isnt()) {
				flipPos();
				isPos = true;
				isNeu = isNeg = false;
			} else if (!b && pushDown.is()) {
				flipNeg();
				isNeg = true;
				isPos = isNeu = false;
			} else {
				flipNeu();
				isNeu = true;
				isPos = isNeg = false;
			}
		}
	};
	
	
	public final BinaryState pushDown = new BinaryState() {;
		@Override
		public void set(boolean b) {
			super.set(b);
			if (b && pushUp.isnt()) {
				flipNeg();
				isNeg = true;
				isPos = isNeu = false;
			} else if (!b && pushUp.is()) {
				flipPos();
				isPos = true;
				isNeu = isNeg = false;
			} else {
				flipNeu();
				isNeu = true;
				isPos = isNeg = false;
			}
		}
	};
	
	protected void flipPos() {
		flipPositive.flip();
	}
	
	protected void flipNeu() {
		flipNeutral.flip();
	}
	
	protected void flipNeg() {
		flipNegative.flip();
	}
	
	public boolean isUp() {
		return isPos;
	}
	
	public boolean isNeutral() {
		return isNeu;
	}
	
	public boolean isDown() {
		return isNeg;
	}
	
	@FunctionalInterface
	public interface FlipAction {
		void flip();
	}
	
	
}
