package sim;

import static vector.CVector.vec;

import java.awt.Color;

import bodies.Body;
import bodies.SphereBody;
import forces.Gravity;

public interface System {
	
	void addTo(Spacetime s);
	
	public static System getSolarSystem() {
		return new System() {
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
