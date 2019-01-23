package main;


import graphics.STPanel;
import graphics.STWindow;
import membrane.RectangularMembrane;
import sim.NSpace;
import sim.Spacetime;

public class Main {

	public static void main(String[] args) {
		Spacetime spacetime = new NSpace(.005);

		var mem = RectangularMembrane.Presets.getSlitExperiment(1, 1);
//		var mem = PSystem.getDampedBiSeriesPendulum();
		mem.addTo(spacetime);
		
//		var ufo = Bodies.getUFO(null, null);
//		spacetime.add((Body) ufo);
//		spacetime.add((Force) ufo);
		
		double view = 3;
		new STWindow(new STPanel(spacetime, view));
	}
	
	/*
	 * List of presets and locations:
	 * 
	 * PRESET									LOCATION
	 * ---------------------------------------	-----------------------------------------------
	 * Solar system								sim.System
	 * Single slit experiment					RectangularMembrane.Presets
	 * Double slit experiment					RectangularMembrane.Presets
	 */
}
