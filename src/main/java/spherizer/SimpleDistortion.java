package spherizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

public class SimpleDistortion extends Distortion {
	private BufferedImage source;
	private int width, height;
	public BufferedImage distort(BufferedImage source) {
		assert source != null;
		this.source = source;
		width = source.getWidth();
		height = source.getHeight();
		
		
		int[] pixels = null;
		try {
			pixels = readPixels();
		}
		catch (InterruptedException e) {
			// this really shouldn't happen unless
			// we interrupt the thread ourselves
			throw new RuntimeException(e);
		}
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = result.createGraphics();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int xDistorted = distortCoordinate(x, y, width, height);
				int targetIndex = toIndex(xDistorted, y);
				g2d.setColor(new Color(pixels[targetIndex], true));
				g2d.drawRect(x, y, 1, 1);
			}
		}
		 
		return result;
	}

	private int[] readPixels() throws InterruptedException {
		int[] packed = new int[width * height];
		
		final PixelGrabber pg = new PixelGrabber(source, 0, 0, width, height, packed, 0, width);
		pg.grabPixels();
		
		return packed;
	}
	
	private int toIndex(int x, int y) {
		return y * width + x;
	}
}
