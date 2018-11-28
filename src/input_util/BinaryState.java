package input_util;

import input_util.KeyBinder.State;

public class BinaryState implements State {
	
	public BinaryState() {
		this(false);
	}
	
	public BinaryState(boolean b) {
		this.b = b;
	}
	
	private boolean b;

	@Override
	public void set(boolean b) {
		this.b = b;
	}
	
	public boolean is() {
		return b;
	}
	
	public boolean isnt() {
		return !b;
	}
	
	public boolean xor(BinaryState state) {
		return b ^ state.b;
	}
	
	public boolean and(BinaryState state) {
		return b && state.b;
	}
	
	public boolean or(BinaryState state) {
		return b || state.b;
	}
	
	public boolean andAll(BinaryState...states) {
		if (!b) return false;
		for (var state : states)
			if (!state.b) return false;
		return true;
	}
	
	public boolean orAny(BinaryState...states) {
		if (b) return true;
		for (var state : states)
			if (state.b) return true;
		return false;
	}
	
}
