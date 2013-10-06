package com.remote.remote2d.engine.world;

import java.util.ArrayList;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.logic.Vector2;

public class Message {
	
	private String prefix;
	private int minlogType;
	private String message;
	private int color;
	
	private int cachedRenderHeight = -1;
	private int cachedWidth = -1;
	private int cachedSize = -1;

	public String getPrefix() {
		return prefix;
	}

	public int getMinlogType() {
		return minlogType;
	}

	public String getMessage() {
		return message;
	}

	public int getColor() {
		return color;
	}

	public Message(String prefix, String message, int color, int minlogType) {
		this.prefix = prefix;
		this.message = message;
		this.color = color;
		this.minlogType = minlogType;
	}
	
	/**
	 * Renders the message using the given font.  It also clips the message to
	 * the given width if desired.
	 * @param pos Position to render
	 * @param size Size of the font
	 * @param width Width of the given area (to clip to)
	 */
	public void render(Vector2 pos, int size, int width)
	{
		ArrayList<String> set = new ArrayList<String>();
		if(width > 0)
			set = Fonts.get("Arial").getStringSet(toString(), size, width);
		else
		{
			set = new ArrayList<String>();
			set.add(toString());
		}
		int currentY = 0;
		for(String s : set)
		{
			Fonts.get("Arial").drawString(s, pos.x, pos.y+currentY, size, color);
			currentY += size;
		}
	}
	
	public String toString()
	{
		String str = "";
		switch(minlogType)
		{
		case Log.LEVEL_TRACE:
			str += "[TRACE]";
			break;
		case Log.LEVEL_DEBUG:
			str += "[DEBUG]";
			break;
		case Log.LEVEL_ERROR:
			str += "[ERROR]";
			break;
		case Log.LEVEL_INFO:
			str += "[INFO]";
			break;
		case Log.LEVEL_WARN:
			str += "[WARN]";
			break;
		}
		if(prefix != null)
			str += "["+prefix+"]";
		str += " "+message;
		return str;
	}

	public int getRenderHeight(int width, int size) {
		if(cachedRenderHeight == -1 || cachedWidth == -1 || cachedSize == -1 || width != cachedWidth || size != cachedSize)
		{
			cachedWidth = width;
			cachedSize = size;
			if(width > 0)
			{
				ArrayList<String> set = new ArrayList<String>();
				int h = 0;
				set = Fonts.get("Arial").getStringSet(toString(), size, width);
				for(String s : set)
					h += Fonts.get("Arial").getStringDim(s, size)[1];
				cachedRenderHeight = h;
			}
			else
				cachedRenderHeight = Fonts.get("Arial").getStringDim(toString(), size)[1];
		}
		return cachedRenderHeight;
	}

}
