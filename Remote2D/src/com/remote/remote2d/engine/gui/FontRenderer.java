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

import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.art.TextureLoader;
import com.remote.remote2d.engine.logic.Vector2;

public class FontRenderer {
	private Font font;
	private Map<RenderData,Texture> cache;
	public final boolean useAntiAliasing;
	
	public FontRenderer(Font f, boolean useAntiAliasing)
	{
		font = f;
		this.useAntiAliasing = useAntiAliasing;
		
		cache = new LinkedHashMap<RenderData,Texture>(16, 0.75f, true) {
			@Override
	    	protected boolean removeEldestEntry(Map.Entry<RenderData,Texture> eldest) {
				if(size() > 16)
				{
					eldest.getValue().removeTexture();
					eldest.getValue().image.flush();
					eldest.getValue().image = null;				}
				return size() > 16;
	    	}
	    };
	}
	
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
	
	public int[] getStringDim(String s, float size)
	{
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
	}
	
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
		Renderer.drawRect(new Vector2(x,y), new Vector2(tex.image.getWidth(),tex.image.getHeight()), tex, 0xffffff, 1);
		
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
//		int texID = TextureLoader.loadTexture(image,true,false);
//		
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
//		GL11.glBegin(GL11.GL_QUADS);
//		{
//			GL11.glTexCoord2f(0, 0);
//			GL11.glVertex2f(x, y);
//			GL11.glTexCoord2f(1, 0);
//			GL11.glVertex2f(x+image.getWidth(), y);
//			GL11.glTexCoord2f(1, 1);
//			GL11.glVertex2f(x+image.getWidth(), y+image.getHeight());
//			GL11.glTexCoord2f(0, 1);
//			GL11.glVertex2f(x, y+image.getHeight());
//		}
//		GL11.glEnd();
//		GL11.glDeleteTextures(texID);
//		image = null;
	}
	
	public void drawCenteredString(String s, int y, float size, int color)
	{
		int[] stringDim = getStringDim(s,size);
		drawString(s,Remote2D.getInstance().displayHandler.getDimensions().x/2-stringDim[0]/2,y,size,color);
	}
	
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
