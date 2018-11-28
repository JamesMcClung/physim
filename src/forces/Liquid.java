package forces;

import bodies.Body;
import bodies.SphereBody;
import graphics.GraphicsInterface;
import sim.Spacetime;
import vector.CVector;
import vector.Vector;

public class Liquid implements Force {
	
	public Liquid(double density, Vector flow) {
		this.density = density;
		flow = new CVector(flow);
	}
	
	private double density;
//	private double viscosity;
	private double seaLevel = 0;
	private Vector flow;

	@Override
	public void applyTo(Spacetime s) {
		for (Body b : s.particles()) {
			if (b instanceof SphereBody) {
				double buoyancy;
				
				var sb = (SphereBody) b;
				double r = sb.getRadius();
				double h = seaLevel - (sb.position().z() - r);
				if (h >= 2*r) { // completely submerged
					buoyancy = sb.getVolume() * density;
				} else if (h > 0) { // partially submerged
					buoyancy = Math.PI * h*h * (r - h/3) * density; 
				} else {
					buoyancy = 0;
				}
				
				b.applyForce(new CVector(0,0,buoyancy));
			}
		}
	}
	
//	@Override
//	public void renderForce(GraphicsInterface g) {
//		g.drawLine(r1, r2);
//	}

}
