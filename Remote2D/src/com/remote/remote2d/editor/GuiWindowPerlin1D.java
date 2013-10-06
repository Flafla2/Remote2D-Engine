package com.remote.remote2d.editor;

import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.GuiButton;
import com.remote.remote2d.engine.gui.GuiWindow;
import com.remote.remote2d.engine.gui.WindowHolder;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Noise1D;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiWindowPerlin1D extends GuiWindow {
		private float[] values;

	public GuiWindowPerlin1D(WindowHolder holder, Vector2 pos, ColliderBox allowedBounds) {
		super(holder, pos, new Vector2(300,300), allowedBounds, "1D Perlin Noise");
		generate();
	}
	
	public void generate()
	{
		values = Noise1D.GeneratePerlinNoise(Noise1D.GenerateWhiteNoise(300, (int) System.currentTimeMillis()), 6);
	}
	
	@Override
	public void renderContents(float interpolation) {
		Vector2[] vecValues = new Vector2[values.length];
		for(int x=0;x<values.length;x++)
		{
			vecValues[x] = new Vector2(x, 300-values[x]*300);
		}
		
		Renderer.drawLinePoly(vecValues, 1, 1, 1, 1);
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		buttonList.add(new GuiButton(0,new Vector2(30,10),new Vector2(240,40),"Regenerate"));
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id == 0)
			generate();
	}
	
	@Override
	public boolean canResize()
	{
		return false;
	}

}
