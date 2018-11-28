package vector;
import static java.lang.Math.*;

public class Test {
	
	public static void main(String[] args) {
		Vector v = new PVector(1, PI/2, 0);
		Vector u = new PVector(2, 0, PI/2);
		double angle = PI/2;
		Vector axis = new CVector(0,0,1);
		
		Vector vc = new CVector(v);
		Vector uc = new CVector(u);

		System.out.println("u = " + u + " = " + uc);
		System.out.println("v = " + v + " = " + vc);
		
		System.out.println("u x v = " + new CVector(u.cross(v)));
		System.out.println("u x v = " + uc.cross(vc));

		System.out.println("rotating " + vc + " around " + axis + " by angle " + angle);
		Quaternion.rotateAroundAxis(vc, angle, axis);
		System.out.println(vc);
		
		System.out.println(vc.rotate(PI/2, PI/2));
	}

}
