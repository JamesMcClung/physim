package bodies;

import java.awt.Color;

public enum Material {
	ALUMINUM(2700, new Color(132, 135, 137)), CESIUM(1930), COPPER(8960), GALLIUM(5910), GOLD(19320, new Color(255,215,0)), IRIDIUM(22560), IRON(7860), LEAD(11340), LITHIUM(534), OSMIUM(22590), PLATINUM(21450), SILVER(10500), URANIUM(19100),
	ICE(934.0),
	HUMAN(1020, new Color(205,133,63)), STYROFOAM(45), WOOD(740),
	NEUTRONIUM(5.9E17);
	
	public final double density;
	public final Color color;
	
	Material(double density, Color color) {
		this.density = density;
		this.color = color;
	}
	
	Material(double density) {
		this(density, Color.WHITE);
	}
}

