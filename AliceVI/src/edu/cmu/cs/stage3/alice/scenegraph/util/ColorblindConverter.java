package edu.cmu.cs.stage3.alice.scenegraph.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import Jama.Matrix;

/**
 * Converts images to color-blind friendly images based on theoretical values
 * found in the 1988 paper "Color Defective Vision and Computer Graphics Displays".
 * 
 * 
 * @author Alberto Pareja-Lecaros
 *
 */
public class ColorblindConverter {
	
	//theoretical values transformation matrix
	static double[][] vals = { { 0., 0., .5609 }, { -.4227, 1.1723, 0.0911 },
			{ 0.1150, 0.9364, -.0203 } };
	static Matrix m = new Matrix(vals);
	
	/**
	 * Takes a filepath for the image file to convert
	 * 
	 * @param filepath
	 */
	public void convertImage(String filepath) {
		BufferedImage image = readFile(filepath);
		if (image != null) {
			BufferedImage newImage = transformImageColor(image);
			if (newImage != null) {
				writeImage(newImage, filepath);
			}
		}
	}
	
	/**
	 * Helper method that reads an image file
	 * 
	 * @param filepath
	 * @return
	 */
	private BufferedImage readFile(String filepath) {
		BufferedImage image = null;
		try {
			// Read from a file
			File sourceimage = new File(filepath);
			image = ImageIO.read(sourceimage);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Invalid image type!");
		}

		return image; 
	}
	
	/**
	 * Goes one pixel at a time through the original image, performs a transformation
	 * on the RGB value of the pixel, and sets the new image pixel to that tranformed
	 * value.
	 * 
	 * @param image
	 * @return
	 */
	private BufferedImage transformImageColor(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		int pixel = 0;
		for (Integer i: image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth())) {
			int red = (i & 0xFF0000) >> 16;
			int green = (i & 0x00FF00) >> 8;
			int blue = (i & 0x0000FF);
			int x = pixel % image.getWidth();
			int y = (int) (pixel/image.getWidth());
			double[][] newVals = { {red}, {green}, {blue}};
			Matrix b = new Matrix(newVals);
			Matrix result = m.times(b);
			int newRed = (int) result.get(0,0);
			int newGreen = (int) result.get(1, 0);
			int newBlue = (int) result.get(2,0);
			if (newRed < 0) {
				newRed = 0;
			}
			else if (newRed > 255) {
				newRed = 255;
			}
			if (newGreen < 0) {
				newGreen = 0;
			}
			else if (newGreen > 255) {
				newGreen = 255;
			}
			if (newBlue < 0) {
				newBlue = 0;
			}
			else if (newBlue > 255) {
				newBlue = 255;
			}
			Color color = new Color(newRed, newGreen, newBlue);
			newImage.setRGB(x, y, color.getRGB());
			pixel++;
		}
		return newImage;
	}
	
	public static Color convertToColorblind(Color color){
		double[][] newVals = { {color.getRed()}, {color.getGreen()}, {color.getBlue()}};
		Matrix b = new Matrix(newVals);
		Matrix result = m.times(b);
		int newRed = (int) result.get(0,0);
		int newGreen = (int) result.get(1, 0);
		int newBlue = (int) result.get(2,0);
		if (newRed < 0) {
			newRed = 0;
		}
		else if (newRed > 255) {
			newRed = 255;
		}
		if (newGreen < 0) {
			newGreen = 0;
		}
		else if (newGreen > 255) {
			newGreen = 255;
		}
		if (newBlue < 0) {
			newBlue = 0;
		}
		else if (newBlue > 255) {
			newBlue = 255;
		}
		return new Color(newRed, newGreen, newBlue);
	}
	
	/**
	 * Helper method that writes the transformed image to file in the same location
	 * as the original image. It appends cb to the filename before the file suffix
	 * 
	 * @param image
	 * @param filepath
	 */
	private void writeImage(BufferedImage image, String filepath) {
		File destimage = new File(filepath.substring(0, filepath.length()-4).concat("cb" + filepath.substring(filepath.length()-4)));
		try {
			ImageIO.write(image, "JPG", destimage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String... args) {	
		JFileChooser chooseFile = new JFileChooser();
		int option = chooseFile.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			String filepath = chooseFile.getSelectedFile().getAbsolutePath(); 
			ColorblindConverter cb = new ColorblindConverter();
			cb.convertImage(filepath);
		}
	}
}
