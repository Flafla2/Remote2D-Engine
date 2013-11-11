package com.remote.remote2d.engine.art;

import java.awt.Font;
import java.io.FileInputStream;
import java.util.HashMap;

import com.remote.remote2d.engine.Remote2DException;
import com.remote.remote2d.engine.gui.FontRenderer;
import com.remote.remote2d.engine.io.R2DFileUtility;

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
			String arial = "res/fonts/Arial.ttf";
			add("Arial", arial, true);
			String pixelarial = "res/fonts/pixel_arial.ttf";
			add("Pixel_Arial", pixelarial, false);
			String tahoma = "res/fonts/tahoma.ttf";
			add("Tahoma", tahoma, false);
			String verdana = "res/fonts/Verdana.ttf";
			add("Verdana", verdana, true);
			String logo = "res/fonts/logo.ttf";
			add("Logo", logo, false);
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
	 * @param useAntiAliasing Whether or not to use anti aliasing
	 */
	public static void add(String fontName, String path, boolean useAntiAliasing)
	{
		Font font;
		try {
			FileInputStream is = new FileInputStream(R2DFileUtility.getResource(path));
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			is.close();
			fontTable.put(fontName, new FontRenderer(font, useAntiAliasing));
		} catch(Exception e)
		{
			throw new Remote2DException(e,"Failed adding font to Font list!");
		}
	}
	
}
