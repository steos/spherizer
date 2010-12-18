package spherizer;

import java.awt.image.BufferedImage;

abstract public class Distortion {
	static final public double PI2 = Math.PI * 2; 

	static public int distortCoordinate(int x, int y, int width, int height) {
		assert x > -1 && width > 0 && x < width;
		assert y > -1 && height > 0 && y < height;
		
		double theta, phi, phi2;
		final int lastIndex = height - 1;
		
		theta = Math.PI * (y - lastIndex / 2.0) / lastIndex;
		phi = PI2 * (x - width / 2.0) / width;
		phi2 = phi * Math.cos(theta);
		int z = (int) (phi2 * width / PI2 + width / 2.0);
		
		assert z > -1 && z < width;
		
		return z;
	}
	
	abstract public BufferedImage distort(BufferedImage source);
}
