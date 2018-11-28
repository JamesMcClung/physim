package util;

/**
 * Various units of distance, in strictly decreasing size. 
 * 
 * @author james
 */
public enum DistanceUnit {
	
	GIGAPARSEC("gigaparsec", "Gpc", 3.0856776E25),
	MEGAPARSEC("megaparsec", "Mpc", 3.0856776E22),
	KILOPARSEC("kiloparsec", "kpc", 3.0856776E19),
	PARSEC("parsec", "pc", 3.0856776E16),
	LIGHT_YEAR("light year", "ly", 9.460730472580800E15),
	ASTRONOMICAL_UNIT("astronomical unit", "AU", 1.49597870700E11),
	GIGAMETER("gigameter", "Gm", 1E9),
	MEGAMETER("megameter", "Mm", 1E6),
	KILOMETER("kilometer", "km", 1E3),
	METER("meter", "m", 1),
	DECIMETER("decimeter", "dm", 1E-1),
	CENTIMETER("centimeter", "cm", 1E-2),
	MILLIMETER("millimeter", "mm", 1E-3),
	MICROMETER("micrometer", "um", 1E-6),
	NANOMETER("nanometer", "nm", 1E-9),
	PICOMETER("picometer", "pm", 1E-12),
	FEMTOMETER("femtometer", "fm", 1E-15),
	ATTOMETER("attometer", "am", 1E-18);
	
	DistanceUnit(String name, String abbreviation, double meterEquivalent) {
		this.name = name;
		this.abbreviation = abbreviation;
		this.meterEquivalent = meterEquivalent;
	}
	
	public final String name;
	public final String abbreviation;
	public final double meterEquivalent;
	

	/**
	 * Determine the largest unit of distance less or equal to than distanceInMeters.
	 * @param distanceInMeters upper bound of unit distance
	 * @return the largest smaller unit
	 */
	public static DistanceUnit getUnit(double distanceInMeters) {
		for (DistanceUnit u : DistanceUnit.values()) {
			if (distanceInMeters >= u.meterEquivalent)
				return u;
		}
		return DistanceUnit.values()[DistanceUnit.values().length-1];
	}
	
	/**
	 * Determine the largest unit of distance less than or equal to distanceInMeters, as well as
	 * how many can fit within (only powers of 10 are considered).
	 * @param distanceInMeters upper bound of unit distance
	 * @return the largest smaller unit, and the number of them that fit 
	 */
	public static Tuple<DistanceUnit, Integer> getUnitAndCount(double distanceInMeters) {
		DistanceUnit unit = DistanceUnit.getUnit(distanceInMeters);
		int nUnits = 1;
		for (; unit.meterEquivalent * nUnits < distanceInMeters/10; nUnits *= 10);
		return new Tuple<>(unit, nUnits);
	}
}
