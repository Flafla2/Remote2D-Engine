package com.remote.remote2d.engine;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;

import javax.swing.JFrame;

import com.remote.remote2d.engine.logic.Vector2;

public abstract class Remote2DGame {
	
	/**
	 * This is called when the Remote2D instance starts up.  Put any Remote2D-based initialization code here.
	 */
	public abstract void initGame();
	
	/**
	 * Supplies the local path to any icons.  These are shown on the window and in the taskbar in windows/linux, and in the dock on mac.
	 * 
	 * For full support, you need at least:<ul><li>
	 * One 16x16 and one 32x32 icon for full windows support</li><li>
	 * One 32x32 icon for full Linux (and similar platforms) support</li><li>
	 * One 128x128 icon for full Mac support</li></ul>
	 * @return A string array with icons to use, or null for the default LWJGL icon.
	 */
	public String[] getIconPath()
	{
		return null;
	}
	
	/**
	 * This is called at the beginning of each render.  If it is different than the
	 * current stretch type, and no GuiMenus are overriding it, than the stretch type
	 * will change to this.
	 * 
	 * @return The default Stretch Type - {@link StretchType#MULTIPLES} by default.
	 * @see StretchType
	 */
	public StretchType getDefaultStretchType()
	{
		return StretchType.MULTIPLES;
	}
	
	public Vector2 getDefaultResolution()
	{
		return new Vector2(1024,576);
	}
	
	public Vector2 getDefaultScreenResolution()
	{
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Dimension screenDimension = env.getMaximumWindowBounds().getSize();
		
		JFrame frame = new JFrame("Top lel");
		frame.pack();
		Insets inset = frame.getInsets();
		frame.dispose();
		return new Vector2(screenDimension.width-inset.left-inset.right,screenDimension.height-inset.top-inset.bottom);
	}
	
}
