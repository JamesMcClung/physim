package util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A String Supplier that formats another object into a String can be replaced with this. When the
 * object does not change between gets, the last string is used. In this way, String.format is not called
 * repeatedly to yield the exact same string. 
 * <p>
 * This is especially useful for HUDs where data is displayed as Strings, and the screen is refreshed each frame.  
 */
public class ValAsStr implements Supplier<String> {
	
	public ValAsStr(String formatString, Supplier<Object> val) {
		valSupply = val;
		this.formatString = formatString;
	}
	
	private Object lastVal = null;
	private String valAsStr = null;
	private final Supplier<Object> valSupply;
	private final String formatString;

	@Override
	public String get() {
		Object nextVal = valSupply.get();
		if (!Objects.equals(nextVal, lastVal)) {
			valAsStr = String.format(formatString, nextVal);
			lastVal = nextVal;
		}
		return valAsStr;
	}
	
}