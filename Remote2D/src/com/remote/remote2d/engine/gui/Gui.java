package com.remote.remote2d.engine.gui;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.DisplayHandler;
import com.remote.remote2d.engine.logic.Vector2;

/**
 * Basic class for renderable elements in a Gui.
 * 
 * @author Flafla2
 *
 */
public abstract class Gui {
	
	public Gui()
	{
		
	}
	
	/**
	 * Ticks this element.  All game logic should go in tick() - in other words anything that depends on running the same on
	 * all systems, regardless of how fast the user's system is.
	 * @param i X-position of the mouse
	 * @param j Y-position of the mouse
	 * @param k Button state of the mouse (0 = no buttons pressed, 1 = left mouse button is down, 2 = right button is down)
	 */
	public abstract void tick(int i, int j, int k);
	/**
	 * All rendering should go here.  Since rendering happens much faster than ticking, an interpolation value must be used.
	 * This is how far in between the previous tick and the next tick this render was called.
	 * @param interpolation How far in between this tick and the next tick we are (0.0-1.0)
	 */
	public abstract void render(float interpolation);
	
	/**
	 * Converts a hex RGB value to an array of floats.
	 * @param rgb RGB value in hex (0x000000-0xffffff)
	 * @return A float array - {red, green, blue} (0.0-1.0)
	 */
	public static float[] getRGB(int rgb)
	{
		float r = ((rgb >> 16) & 0xff)/255f;
		float g = ((rgb >> 8) & 0xff)/255f;
		float b = (rgb & 0xff)/255f;
		float[] returnval = {r,g,b};
		return returnval;
	}
	
	/**
	 * Binds the given hex value to OpenGL
	 * @param rgb RGB value in hex (0x000000-0xffffff)
	 */
	public static void bindRGB(int rgb)
	{
		float[] color = getRGB(rgb);
		GL11.glColor3f(color[0],color[1],color[2]);
	}
	
	public static int screenWidth()
	{
		return (int)DisplayHandler.getDimensions().x;
	}
	
	public static int screenHeight()
	{
		return (int)DisplayHandler.getDimensions().y;
	}
	
	public static Vector2 screenDim()
	{
		return DisplayHandler.getDimensions();
	}
	
}
