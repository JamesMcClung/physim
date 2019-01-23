package sim;

import static vector.CVector.vec;

import java.awt.Color;

import bodies.Bodies;
import bodies.Body;
import bodies.FixedPoint;
import bodies.Particle;
import bodies.SphereBody;
import forces.Electrostatic;
import forces.Force;
import forces.Gravity;
import forces.Magnetic;
import forces.Wind;
import tethers.IdealSpring;

/**
 * Physical system
 * 
 * @author james
 *
 */
public interface PSystem {
	
	void addTo(Spacetime s);
	
	public static PSystem getPendulum() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				var fulcrum = new FixedPoint(1, vec(0,1,0), null);
				var tip = new Particle(1, vec(.5,0,0), vec(0,0,.3));
				var string = new IdealSpring(900, fulcrum, tip);
				var gravity = new Gravity(0, Gravity.EARTH_FIELD);
				s.add(fulcrum, tip);
				s.add(string, gravity);
			}
		};
	}
	
	public static PSystem getDampedPendulum() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				var fulcrum = new FixedPoint(1, vec(0,1,0), null);
				var tip = new SphereBody(.1, .1, vec(.5,0,0), vec(-2,0,3));
				var string = new IdealSpring(900, fulcrum, tip);
				var gravity = new Gravity(0, Gravity.EARTH_FIELD);
				var air = new Wind(.1);
				s.add(fulcrum, tip);
				s.add(string, gravity, air);
			}
		};
	}
	
	public static PSystem getQuadParPendulum() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				double[] xpts = {0, 0, 1, 1};
				double[] zpts = {0, 1, 1, 0};
				double shelfXDisplacement = .5;
				double shelfVZ0 = .5;
				
				var fulcrums = new FixedPoint[4];
				var tips = new Particle[4];
				var strings = new IdealSpring[4];
				var edges = new IdealSpring[4];
				
				for (int i = 0; i < 4; i++) {
					fulcrums[i] = new FixedPoint(1, vec(xpts[i], 3, zpts[i]), null);
					tips[i] = new Particle(.1, vec(xpts[i] + shelfXDisplacement, 0, zpts[i]), vec(0, 0, shelfVZ0));
					strings[i] = new IdealSpring(900, fulcrums[i], tips[i]);
				}
				for (int i = 0; i < 4; i++) {
					edges[i] = new IdealSpring(100, tips[i], tips[(i+1) % 4]);
				}
				
				var gravity = new Gravity(0, Gravity.EARTH_FIELD);
				s.add(fulcrums);
				s.add(tips);
				s.add(strings);
				s.add(edges);
				s.add(gravity);
			}
		};
	}
	
	public static PSystem getBiSeriesPendulum() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				var fulcrum = new FixedPoint(1, vec(0,2,0), null);
				var mid = new Particle(1, vec(.5,0,0), vec(0,0,.3));
				var tip = new Particle(1, vec(.6,-.1,0), vec(0,0,.3));
				var string1 = new IdealSpring(900, fulcrum, mid);
				var string2 = new IdealSpring(900, mid, tip);
				var gravity = new Gravity(0, Gravity.EARTH_FIELD);
				s.add(fulcrum, mid, tip);
				s.add(string1, string2, gravity);
			}
		};
	}
	
	public static PSystem getDampedBiSeriesPendulum() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				var fulcrum = new FixedPoint(1, vec(0,2,0), null);
				var mid = new SphereBody(10, .1, vec(.5,0,0), vec(0,0,.3));
				var tip = new SphereBody(1, .1, vec(.6,-.1,0), vec(0,0,.3));
				var string1 = new IdealSpring(9000, fulcrum, mid);
				var string2 = new IdealSpring(900, mid, tip);
				var gravity = new Gravity(0, Gravity.EARTH_FIELD);
				var air = new Wind(1);
				s.add(fulcrum, mid, tip);
				s.add(string1, string2, gravity, air);
			}
		};
	}
	
	public static PSystem getEMCycloid() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				var particle = Bodies.getProton(null, null);
				double eFieldStr = 1e-9;
				double mFieldStr = eFieldStr * 8e0;
				Force electric = new Electrostatic((p) -> vec(0,eFieldStr,0));
				Force magnetic = new Magnetic((p) -> vec(0,0,mFieldStr));
				s.add(particle);
				s.add(electric, magnetic);
			}
		};
	}
	
	public static PSystem getSolarSystem() {
		return new PSystem() {
			@Override
			public void addTo(Spacetime s) {
				// enter the land of magic numbers
				Body sun = new SphereBody(1.989e30, 695.508e6, null, null)
					.setName("Sol")
					.setColor(Color.YELLOW);
				Body mercury = new SphereBody(0.33011e24, 2439700, vec(69.82e9,0,0), vec(0, 38.86e3, 0))
						.setName("Mercury")
						.setColor(Color.GRAY);
				Body venus = new SphereBody(4.8675e24, 6051.8e3, vec(108.94e9,0,0), vec(0,34.79e3,0))
						.setName("Venus")
						.setColor(Color.ORANGE);
				Body earth = new SphereBody(5.972e24, 6.371e6, vec(152e9,0,0), vec(0,29300,0))
						.setName("Earth")
						.setColor(Color.GREEN);
				Body moon = new SphereBody(7.34767309e22, 1737400, vec(152e9+385e6,0,0), vec(0,29300+1022,0))
						.setName("Moon")
						.setColor(Color.LIGHT_GRAY);
				Body mars = new SphereBody(0.64171e24, 3396.2e3, vec(249.23e9,0,0), vec(0,21.97e3,0))
						.setName("Mars")
						.setColor(Color.RED);
				Body jupiter = new SphereBody(1898.19e24, 71492e3, vec(816.62e9,0,0), vec(0,12.44e3,0))
						.setName("Jupiter")
						.setColor(new Color(201,144,57));
				Body saturn = new SphereBody(568.34e24, 60268e3, vec(1514.50e9,0,0), vec(0,9.09e3,0))
						.setName("Saturn")
						.setColor(new Color(234,214,184));
				Body uranus = new SphereBody(86.813e24, 25559e3, vec(3003.62e9,0,0), vec(0,6.49e3,0))
						.setName("Uranus")
						.setColor(new Color(225,238,238));
				Body neptune = new SphereBody(102.413e24, 24764e3, vec(4545.67e9,0,0), vec(0,5.37e3,0))
						.setName("Neptune")
						.setColor(new Color(63,84,186));
				Body pluto = new SphereBody(0.01303e24, 1187e3, vec(7375.93e9,0,0), vec(0,3.71e3,0))
						.setName("Pluto")
						.setColor(Color.GRAY);
				
//				Rocket ufo = Bodies.getUFO(vec(0,1.5e11, 0).rotate(-2, 0), null);
				
				s.add(new Gravity());
				s.add(sun, mercury, venus, earth, moon, mars, jupiter, saturn, uranus, neptune, pluto);
			}
		};
	}

}
