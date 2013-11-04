package timestamp;

import java.util.Random;

public class Experiment {
	public static void main(String[] args){
		Random rand = new Random(50);
		for(int i=0;i<100;i++) {
			System.out.println(rand.nextInt(100) +" " + rand.nextBoolean());
		}
	}
}
