package com.remote.remote2d.engine.art;

import java.awt.image.BufferedImage;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.logic.Vector2;

public class CursorLoader {
	private static Texture tex;
	private static Vector2 hotspot;
	
	/**
	 * Set the cursor to the specified image.
	 * @param tex Image to use as the cursor, or null to reset it to default
	 * @param hotspot The "hotspot" fot his cursor.  In other words, the position on this texture where the actual clicking occurs.
	 */
	public static void setCursor(String tex, Vector2 hotspot)
	{
		CursorLoader.tex = new Texture(tex);
		CursorLoader.hotspot = hotspot;
		
		if(tex != null)
		{
			Cursor emptyCursor;
			try {
				emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
				Mouse.setNativeCursor(emptyCursor);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		} else
		{
			try {
				Mouse.setNativeCursor(null);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Renders the cursor, if there is one registered with the system.
	 */
	public static void render(float interpolation)
	{
		if(tex != null)
		{
			BufferedImage image = tex.getImage();
			Vector2 renderpos = new Vector2(Remote2D.getMouseCoords()).subtract(hotspot);
			Vector2 renderDim = new Vector2(image.getWidth(),image.getHeight());
			image.flush();
			image = null;

			Renderer.drawRect(renderpos, renderDim, new Vector2(0,0), new Vector2(1,1), tex, 0xffffff, 1.0f);
			
		}
	}
	
	/**
	 * Sets the cursor texture
	 */
	public static void setMouseTexLoc(String tex)
	{
		CursorLoader.tex.removeTexture();
		CursorLoader.tex = new Texture(tex);
	}
	
}
