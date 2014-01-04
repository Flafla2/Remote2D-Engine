package com.remote.remote2d.engine.art;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.remote.remote2d.engine.DisplayHandler;

/**
 * Low-level texture holder.  Acts as a direct interface between Java, Remote2D,
 * and LWJGL to load and bind textures.
 * 
 * @author Flafla2
 */
public class Texture {
	
	/**
	 * If true, sets linear scaling to on by default.
	 * @see #linearScaling
	 */
	public static boolean DEFAULT_LINEAR_SCALE = false;
	/**
	 * If true, sets repeat textures on by default.
	 * @see #repeat
	 */
	public static boolean DEFAULT_REPEAT = false;
	
	private boolean linearScaling = false;
	private long lastReload;
	private boolean repeat = false;
	private String textureLocation;
	private BufferedImage image;
	private int glId;
	private long lastBindTime = -1;
	
	/**
	 * Loads a new texture from a file.
	 * @param loc Location to any PNG file, in relation to the jar path.
	 * @param linearScaling Whether or not to scale this Texture linearly (see {@link #linearScaling})
	 * @param repeat Whether or not to repeat this texture (see {@link #repeat})
	 */
	public Texture(String loc, boolean linearScaling, boolean repeat)
	{
		textureLocation = loc;
		this.linearScaling = linearScaling;
		this.repeat = repeat;
		image = TextureLoader.loadImage(textureLocation);
		glId = TextureLoader.loadTexture(image,linearScaling,repeat);
		lastReload = System.currentTimeMillis();
	}
	
	/**
	 * Loads a new texture from a BufferedImage.
	 * @param image BufferedImage to use for this texture.
	 * @param linearScaling Whether or not to scale this Texture linearly (see {@link #linearScaling})
	 * @param repeat Whether or not to repeat this texture (see {@link #repeat})
	 */
	public Texture(BufferedImage image, boolean linearScaling,boolean repeat) {
		textureLocation = "";
		this.image = image;
		this.linearScaling = linearScaling;
		this.repeat = repeat;
		glId = TextureLoader.loadTexture(image,linearScaling,repeat);
		lastReload = System.currentTimeMillis();
	}
	
	/**
	 * Loads a new texture from a file.
	 * @param loc Location to any PNG file, in relation to the jar path.
	 */
	public Texture(String loc)
	{
		this(loc,DEFAULT_LINEAR_SCALE,DEFAULT_REPEAT);
	}
	
	/**
	 * Loads a new texture from a BufferedImage.
	 * @param image BufferedImage to use for this texture.
	 */
	public Texture(BufferedImage image)
	{
		this(image,DEFAULT_LINEAR_SCALE,DEFAULT_REPEAT);
	}

	/**
	 * Binds this texture to OpenGL.  Generally this shouldn't need to be used;
	 * use {@link com.remote.remote2d.engine.art.Renderer} instead.
	 */
	public void bind()
	{
		if(lastReload < DisplayHandler.getLastTexReload())
			reload();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId);
		lastBindTime = System.currentTimeMillis();
	}
	
	/**
	 * Removes this texture from OpenGL memory.  This MUST be done before
	 * deleting this texture.  Not doing so causes a memory leak because OpenGL
	 * is not garbage collected.
	 */
	public void removeTexture()
	{
		GL11.glDeleteTextures(glId);
	}
	
	/**
	 * Destroys this texture's BufferedImage safely.
	 */
	public void removeImage()
	{
		image.flush();
		image = null;
	}
	
	/**
	 * Location of this texture in relation to the jar file, if this Texture was
	 * loaded from a file.
	 */
	public String getTextureLocation() {
		return textureLocation;
	}
	
	/**
	 * BufferedImage representation of this texture.
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * The OpenGL ID of this texture.
	 */
	public int getGlId() {
		return glId;
	}
	
	/**
	 * Reloads this texture in OpenGL - useful after reinitializing OpenGL.
	 */
	public void reload() {
		if(lastReload >= DisplayHandler.getLastTexReload())
			GL11.glDeleteTextures(glId);
		glId = TextureLoader.loadTexture(image,linearScaling,repeat);
		lastReload = System.currentTimeMillis();
	}
	
	/**
	 * The last time that this texture has been bound to OpenGL.
	 * @return The last time that this texture has been bound to OpenGL, or -1 if it hasn't been bound.
	 */
	public long getLastBindTime()
	{
		return lastBindTime;
	}
	
	public boolean isLinearScaling()
	{
		return linearScaling;
	}

	public void setLinearScaling(boolean linearScaling)
	{
		this.linearScaling = linearScaling;
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linearScaling ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linearScaling ? GL_LINEAR : GL_NEAREST);
	}

	public boolean isRepeat()
	{
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
		bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, repeat ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, repeat ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
	}
}
