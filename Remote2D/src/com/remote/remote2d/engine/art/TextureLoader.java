package com.remote.remote2d.engine.art;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.io.R2DFileUtility;

public class TextureLoader {
	/**
	 * How many bytes ares stored in memory for each pixel. 3 allocates space for
	 * Red, Green, and Blue. 4 allocates space for Red, Green, Blue, and Alpha (opaqueness).
	 */
	private static final int BYTES_PER_PIXEL = 4;
	
	/**
	 * Converts a standard BufferedImage to an OpenGL texture.
	 * @param image Image to load
	 * @param linearScaling Whether or not to use linear interpolation when scaling a texture.
	 * @param repeat Whether or not to repeat a texture when scaling, instead of stretching to the bounds of the texture.
	 * @return An OpenGL Texture ID
	 */
	public static int loadTexture(BufferedImage image, boolean linearScaling, boolean repeat){
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
				buffer.put((byte) (pixel & 0xFF));               // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
			}
		}

		buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using 
		// whatever OpenGL method you want, for example:

		int textureID = glGenTextures(); //Generate texture ID
		glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

		//Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, repeat ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, repeat ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);

		//Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linearScaling ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linearScaling ? GL_LINEAR : GL_NEAREST);

		//Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	      
		//Return the texture ID so we can bind it later again
		buffer.clear();
		return textureID;
		}
	   
	   public static BufferedImage loadImage(String loc)
	   {
		   File f = R2DFileUtility.getResource(loc);
		   try{
			  return ImageIO.read(f);
		   } catch (IOException e) {
			   throw new Remote2DException(e,"Failed to load image: "+f.getPath()+" "+f.exists()+"!");
			}
	   }
}
