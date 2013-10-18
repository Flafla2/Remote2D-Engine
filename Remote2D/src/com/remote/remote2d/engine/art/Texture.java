package com.remote.remote2d.engine.art;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.Remote2D;

public class Texture {
	
	public static boolean DEFAULT_LINEAR_SCALE = false;
	public static boolean DEFAULT_REPEAT = false;
	
	public boolean linearScaling = false;
	private long lastReload;
	public boolean repeat = false;
	public String textureLocation;
	public BufferedImage image;
	public int glId;
	
	public Texture(String loc, boolean linearScaling, boolean repeat)
	{
		textureLocation = loc;
		this.linearScaling = linearScaling;
		this.repeat = repeat;
		image = TextureLoader.loadImage(textureLocation);
		glId = TextureLoader.loadTexture(image,linearScaling,repeat);
		lastReload = System.currentTimeMillis();
	}
	
	public Texture(BufferedImage image, boolean linearScaling,boolean repeat) {
		textureLocation = "";
		this.image = image;
		this.linearScaling = linearScaling;
		this.repeat = repeat;
		glId = TextureLoader.loadTexture(image,linearScaling,repeat);
		lastReload = System.currentTimeMillis();
	}
	
	public Texture(String loc)
	{
		this(loc,DEFAULT_LINEAR_SCALE,DEFAULT_REPEAT);
	}
	
	public Texture(BufferedImage image)
	{
		this(image,DEFAULT_LINEAR_SCALE,DEFAULT_REPEAT);
	}

	public void bind()
	{
		if(lastReload < Remote2D.displayHandler.getLastTexReload())
			reload();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId);
	}
	
	public void removeTexture()
	{
		GL11.glDeleteTextures(glId);
	}
	
	public String getTextureLocation() {
		return textureLocation;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getGlId() {
		return glId;
	}

	public void reload() {
		if(lastReload >= Remote2D.displayHandler.getLastTexReload())
			GL11.glDeleteTextures(glId);
		glId = TextureLoader.loadTexture(image,linearScaling,repeat);
		lastReload = System.currentTimeMillis();
	}
}
