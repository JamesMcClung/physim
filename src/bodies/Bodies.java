package bodies;

import static shapes.Frame.getVerticesOnRing;
import static shapes.Triangle.tri;
import static vector.CVector.vec;

import java.awt.Color;

import rocketry.InfiniteThruster;
import rocketry.Rocket;
import rocketry.Thruster;
import shapes.Polyhedron;
import shapes.Triangle;
import util.MiscUtil;
import vector.Quaternion;
import vector.Vector;

public class Bodies {
	
	/**
	 * Unit charge, in C.
	 */
	public static final double E = 1.6022E-19;
	// masses of particles, in kg
	public static final double MASS_ELECTRON = 9.10938E-31;
	public static final double MASS_PROTON = 1.6726219E-27;
	public static final double MASS_NEUTRON = 1.674929E-27;
	public static final double MASS_NEUTRINO = MASS_ELECTRON / 1E6;
	// radii of particles, in m
	public static final double RADIUS_PROTON = 8.55E-16;
	public static final double RADIUS_NEUTRON = 8E-16;

	public static Particle getElectron(Vector position, Vector velocity) {
		Particle electron = new Particle(MASS_ELECTRON, -E, position, velocity);
		electron.setName("Electron");
		return electron;
	}
	
	public static Body getProton(Vector position, Vector velocity) {
		SphereBody proton = new SphereBody(MASS_PROTON, RADIUS_PROTON, position, velocity);
		proton.setName("Proton");
		proton.setCharge(E);
		return proton;
	}
	
	public static Body getNeutron(Vector position, Vector velocity) {
		SphereBody neutron = new SphereBody(MASS_NEUTRON, RADIUS_NEUTRON, position, velocity);
		neutron.setName("Neutron");
		return neutron;
	}
	
	public static Body getNeutrino(Vector position, Vector velocity) {
		Particle neutrino = new Particle(MASS_NEUTRINO, 0, position, velocity);
		neutrino.setName("Neutrino");
		return neutrino;
	}
	
	public static Body getEarth(Vector position, Vector velocity) {
		SphereBody earth = new SphereBody(5.972e24, 6.371e6, position, velocity);
		earth.setName("Earth");
		earth.setColor(Color.GREEN);
		return earth;
	}
	
	public static Rocket getSpaceShuttle(Vector position, Vector velocity) {
		final double height = 56.14416, radius = 8.7/2;
		Vector apex = vec(0, height/2, 0);
		Vector[] base = getVerticesOnRing(3, -height/2, radius);
		
		Polyhedron ssBody = new Polyhedron(
				tri(apex, base[0], base[1]),
				tri(apex, base[1], base[2]),
				tri(apex, base[2], base[1]),
				tri(base[0], base[1], base[2]));
		Thruster ssThruster = new Thruster(997903.214, 3054.4, 7857.50, vec(0,1,0));
		
		Rocket spaceShuttle = new Rocket(242671.918, ssBody, position, velocity, ssThruster);
		spaceShuttle.setName("Space Shuttle");
		return spaceShuttle;
	}
	
	public static Rocket getUFO(Vector position, Vector velocity) {
		double radius = 30, height = 10, mass = 200000;
		Vector top = vec(0, height/2, 0), bottom = vec(0,-height/2,0);
		Vector[] sides = getVerticesOnRing(6, 0, radius);
		Vector.scaleAllAlong(vec(1, 1, 2), sides);
		
		Polyhedron ufoBody = new Polyhedron(MiscUtil.concatenate(Triangle.getOpenPyramid(top, sides), Triangle.getOpenPyramid(bottom, sides)));
//		Thruster advancedThruster = new InfiniteThruster(mass * 100, vec(0,1,0));
		Thruster advancedThruster = new InfiniteThruster(mass * 1e3, vec(0,0,-1));
		Thruster advancedThruster2 = new InfiniteThruster(mass * 1e6, vec(0,0,-1));
		
		Rocket ufo = new Rocket(mass, ufoBody, position, velocity, advancedThruster, advancedThruster2);
		ufo.setName("UFO");
		return ufo;
	}
	
	public static Rocket getISS(Vector position, Vector velocity) {
//		final double width = 73, height = 109;
//		double radius = width / Math.sqrt(2);
//		Vector[] top = getVerticesOnRing(4, height/2, radius), bottom = getVerticesOnRing(4, -height/2, radius);
		
		Polyhedron issBody = new Polyhedron();
		issBody.rotateVertices(Quaternion.getRotationQuaternion(Math.PI/4, vec(0,1,0)));
		
		Rocket iss = new Rocket(419725, issBody, position, velocity);
		iss.setName("ISS");
		return iss;
	}
	
	public static Body getMoon(Vector position, Vector velocity) {
		SphereBody moon = new SphereBody(7.34767309e22, 1737400, position, velocity);
		moon.setName("Moon");
		moon.setColor(Color.LIGHT_GRAY);
		return moon;
	}
}
