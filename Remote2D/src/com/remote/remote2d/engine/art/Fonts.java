package com.remote.remote2d.engine.art;

import java.awt.Font;
import java.util.HashMap;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.gui.FontRenderer;

/**
 * A holder class which holds all {@link FontRenderer} instances.
 * 
 * @author Flafla2
 */
public class Fonts {
	
	private static HashMap<String, FontRenderer> fontTable;
	
	static
	{
		fontTable = new HashMap<String, FontRenderer>();
		
		try
		{
			Font arial = Font.createFont(Font.TRUETYPE_FONT, Remote2D.class.getResourceAsStream("/res/fonts/Arial.ttf"));
			fontTable.put("Arial", new FontRenderer(arial, true));
			Font pixelarial = Font.createFont(Font.TRUETYPE_FONT, Remote2D.class.getResourceAsStream("/res/fonts/pixel_arial.ttf"));
			fontTable.put("Pixel_Arial", new FontRenderer(pixelarial, false));
			Font tahoma = Font.createFont(Font.TRUETYPE_FONT, Remote2D.class.getResourceAsStream("/res/fonts/tahoma.ttf"));
			fontTable.put("Tahoma", new FontRenderer(tahoma, false));
			Font verdana = Font.createFont(Font.TRUETYPE_FONT, Remote2D.class.getResourceAsStream("/res/fonts/Verdana.ttf"));
			fontTable.put("Verdana", new FontRenderer(verdana, true));
			Font logo = Font.createFont(Font.TRUETYPE_FONT, Remote2D.class.getResourceAsStream("/res/fonts/logo.ttf"));
			fontTable.put("Logo", new FontRenderer(logo, false));
		} catch(Exception e)
		{
			throw new Remote2DException(e,"Error initializing fonts!");
		}
	}
	
	/**
	 * Returns a registered instance of {@link FontRenderer} with the specified name.
	 * @param fontname Name of the font.
	 */
	public static FontRenderer get(String fontname)
	{
		return fontTable.get(fontname);
	}
	
	/**
	 * Adds a font to the font list.  This only needs to be done once.
	 * @param fontName Name of the font to use in {@link #get(String)}
	 * @param path Path to the font in relation to the jar path
	 */
	public static void add(String fontName, String path)
	{
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Remote2D.class.getResourceAsStream(path));
			fontTable.put(fontName, new FontRenderer(font, true));
		} catch(Exception e)
		{
			throw new Remote2DException(e,"Failed adding font to Font list!");
		}
	}
	
}
