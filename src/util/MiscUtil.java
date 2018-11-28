package util;

import java.awt.Color;
import java.util.Collection;

public class MiscUtil {
	
	/**
	 * @return a random color
	 */
	public static Color randomColor() {
		return new Color(randomInt(255), randomInt(255), randomInt(255));
	}
	
	/**
	 * @param range upper limit, exclusive
	 * @return a random integer between 0 and range
	 */
	public static int randomInt(int range) {
		return (int) (Math.random() * range);
	}
	
	public static double square(double a) {
		return a*a;
	}
	
	/**
	 * Concatenates the given arrays into one big array. Since generic arrays cannot be instantiated,
	 * the big array must be provided for this method to fill out.
	 * @param dest array to be filled with all the elements
	 * @param arrays arrays to be concatenated
	 * @return dest after it has been filled out
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] concatenate(T[]...arrays) {
		int size = 0;
		for (var arr : arrays)
			size += arr.length;
		T[] dest = (T[])java.lang.reflect.Array.newInstance(arrays[0].getClass().getComponentType(), size);
		
		int i = 0;
		for (var arr : arrays) {
			for (T element : arr) {
				dest[i++] = element;
			}
		}
		return dest;
	}
	
	@SafeVarargs
	public static <T> void addAll(Collection<T> c, T...items) {
		for (var item : items) 
			c.add(item);
	}

}
