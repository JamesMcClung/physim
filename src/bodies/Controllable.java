package bodies;

import input_util.KeyBinder;

public interface Controllable {
	
	void addBindingsTo(KeyBinder binder);
	void removeBindingsFrom(KeyBinder binder);
	
}
