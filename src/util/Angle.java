package util;

public class Angle {
	
	public Angle(double radians) {
		double deg = radians * 180 / Math.PI;
		degrees = (int) deg;
		
		double amin = (deg - degrees) * 60;
		arcminutes = (int) amin;

		arcseconds = (amin - arcminutes) * 60;
		
		tostr = String.format("%d°\t%d′\t%2.3f″", degrees, arcminutes, arcseconds);
	}
	
	private final int degrees;
	private final int arcminutes;
	private final double arcseconds;
	
	private final String tostr;
	
	@Override
	public String toString() {
		return tostr;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		
		if (o instanceof Angle) {
			Angle a = (Angle) o;
			return degrees == a.degrees && arcminutes == a.arcminutes && arcseconds == a.arcseconds;
		}
		
		return false;
	}

}
