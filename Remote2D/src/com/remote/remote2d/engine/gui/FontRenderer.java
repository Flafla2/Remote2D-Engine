package com.remote.remote2d.engine.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.remote.remote2d.engine.DisplayHandler;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.logic.Vector2;

/**
 * Relatively low-level Font rendering class.  This uses Java's existing font rendering system and acts as an interface between LWJGL
 * and Java AWT.
 * 
 * Because generating new images for strings to render is expensive, Remote2D caches renders of fonts for you.  Remote2D will automatically
 * delete prerendered text if it isn't used for more than 5 seconds (5000ms).  For this reason drawing text is actually pretty inexpensive,
 * however it does depend heavily on your system's VRAM, due to the fact that the FontRenderer creates a new OpenGL texture every render.
 * This can be contrasted with a bitmap system which only stores one texture in memory.
 * 
 * @author Flafla2
 */
public class FontRenderer {
	private Font font;
	private Map<RenderData,Texture> cache;
	
	public final boolean useAntiAliasing;
	
	/**
	 * Creates a new FontRenderer
	 * @param f Font to use in this renderer
	 * @param useAntiAliasing Whether or not to use anti aliasing.  Most fonts should have this at true, 
	 * except for some pixel fonts which would look better with this off.
	 */
	public FontRenderer(Font f, boolean useAntiAliasing)
	{
		font = f;
		this.useAntiAliasing = useAntiAliasing;
		
		cache = new LinkedHashMap<RenderData,Texture>(16, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
	    	protected boolean removeEldestEntry(Map.Entry<RenderData,Texture> eldest) {
				boolean delete = System.currentTimeMillis()-eldest.getValue().getLastBindTime() > 5000 && eldest.getValue().getLastBindTime() != -1;
				if(delete)
				{
					eldest.getValue().removeTexture();
					eldest.getValue().removeImage();
				}
				return delete;
			}
	    };
	}
	
	/**
	 * Generates a BufferedImage of this fontrenderer's font with the given size and color.
	 * @param s String to generate
	 * @param size Size of the text to generate
	 * @param color Color of the text to generate
	 */
	public BufferedImage createImageFromString(String s, float size, int color)
	{
		if(s.length() == 0)
			return null;
		Font sizedFont = font.deriveFont(size);
		FontRenderContext frc = new FontRenderContext(null,useAntiAliasing,false);
		
		String[] returnSplit = s.split("\n");
		int width = 0;
		int height = 0;
		for(String str : returnSplit)
		{
			
			int w = (int) sizedFont.getStringBounds(str, frc).getWidth();
			if(w > width)
				width = w;
			height += (int) sizedFont.getStringBounds(str, frc).getHeight();
		}
		
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, useAntiAliasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		graphics.setFont(sizedFont);
		graphics.setColor(new Color(color));
		
		int currentY = 0;
		for(String str : returnSplit)
		{
			int h = (int) sizedFont.getStringBounds(str, frc).getHeight();
			graphics.drawString(str, 0, h+currentY-graphics.getFontMetrics(sizedFont).getDescent());//Remember, the y var in drawString is the BASELINE!
			currentY += h;
		}
		graphics.dispose();
		
		return image;
	}
	
	/**
	 * The dimensions of the given String, if it were rendered
	 * @param s String to measure
	 * @param size Size of the string to measure
	 */
	public int[] getStringDim(String s, float size)
	{
		RenderData data = new RenderData(s,size,0xffffff);
		if(!cache.containsKey(data))
		{
			BufferedImage image = createImageFromString(s,size,0xffffff);
			if(image == null)
				return new int[]{0,0};
			Texture tex = new Texture(image,true,false);
			cache.put(data, tex);
		}
		
		BufferedImage image = cache.get(data).getImage();
		return new int[]{image.getWidth(),image.getHeight()};
		/*
		Font sizedFont = font.deriveFont(size);
		FontRenderContext frc = new FontRenderContext(null,useAntiAliasing,true);
		String[] returnSplit = s.split("\n");
		int width = 0;
		int height = 0;
		for(String str : returnSplit)
		{
			width += (int) sizedFont.getStringBounds(str, frc).getWidth();
			height += (int) sizedFont.getStringBounds(str, frc).getHeight();
		}
		
		int[] r = {width,height};
		return r;
		*/
	}
	
	/**
	 * Draws the given string at the given coordinates.
	 * @param s String to render
	 * @param x Left side of the rendered string
	 * @param y Top of the rendered string
	 * @param size Size of the text to render (line height)
	 * @param color Color of the text to render
	 */
	public void drawString(String s, float x, float y, float size, int color)
	{
		if(s.trim().equals(""))
			return;
		
		RenderData data = new RenderData(s,size,color);
		Texture tex;
		if(cache.containsKey(data))
			tex = cache.get(data);
		else
		{
			BufferedImage image = createImageFromString(s,size,color);
			if(image == null)
				return;
			tex = new Texture(image,true,false);
			cache.put(data, tex);
		}
		BufferedImage image = tex.getImage();
		Renderer.drawRect(new Vector2(x,y), new Vector2(image.getWidth(),image.getHeight()), tex, 0xffffff, 1);
		image.flush();
		image = null;
	}
	
	/**
	 * Draws a string centered in the middle of the screen.
	 * @param s String to render
	 * @param y Top of the rendered string
	 * @param size Size of the string to render (line height)
	 * @param color Color of the text to render
	 */
	public void drawCenteredString(String s, int y, float size, int color)
	{
		int[] stringDim = getStringDim(s,size);
		drawString(s,DisplayHandler.getDimensions().x/2-stringDim[0]/2,y,size,color);
	}
	
	/**
	 * Divides the given string into multiple lines using a given width to render in.
	 * @param s String to measure
	 * @param size Size of the text to measure (line height)
	 * @param width Maximum width that one line can be
	 */
	public ArrayList<String> getStringSet(String s, float size, float width)
	{
		ArrayList<String> trueContents = new ArrayList<String>();
		String current = "";
		String[] tokens = s.split(" ");
		
		for(int x=0;x<tokens.length;x++)
		{
			if(getStringDim(current+" "+tokens[x], 20)[0] > width-20)
			{
				trueContents.add(current);
				current = "";
			}
			if(!current.equals(""))
				current += " ";
			current += tokens[x];
		}
		if(!current.trim().equals(""))
			trueContents.add(current);
		return trueContents;
	}
	
	private class RenderData
	{
		public String s;
		public float size;
		public int color;
				
		public RenderData(String s, float size, int color)
		{
			this.s = s;
			this.size = size;
			this.color = color;
		}
		
		@Override
		public int hashCode() {
			return 0;//force call equals
		}

		@Override
		public boolean equals(Object obj) {
			//Generated by Eclipse
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RenderData other = (RenderData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (color != other.color)
				return false;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size))
				return false;
			return true;
		}

		private FontRenderer getOuterType() {
			//Generated by Eclipse
			return FontRenderer.this;
		}
	}
}
