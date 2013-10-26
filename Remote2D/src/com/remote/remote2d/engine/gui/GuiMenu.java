package com.remote.remote2d.engine.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.StretchType;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiMenu extends Gui{
	
	protected ArrayList<GuiButton> buttonList;
	public int backgroundColor = 0xffffff;
	
	public GuiMenu()
	{
		super();
		buttonList = new ArrayList<GuiButton>();
		initGui();
	}
	
	@Override
	public void render(float interpolation)
	{
		renderBackground(interpolation);
		for(int x=0;x<buttonList.size();x++)
			buttonList.get(x).render(interpolation);
	}
	
	public void initGui()
	{
		
	}
	
	public void renderBackground(float interpolation)
	{
		
	}
	
	public void drawBlueprintBackground()
	{
		final int bgcolor = 0x7f9ddf;
		final int medcolor = 0x6b8fdf;
		final int smallcolor = 0x5e87df;
		final int largecolor = 0x98aedf;
		
		backgroundColor = bgcolor;
		
		drawGrid(25,smallcolor);
		GL11.glLineWidth(3);
		drawGrid(100,medcolor);
		GL11.glLineWidth(5);
		drawGrid(200,largecolor);
		GL11.glLineWidth(1);
		
	}
	
	private void drawGrid(int distance, int color)
	{
		for(int x=0;x<=screenWidth()/distance;x++)
		{
			Renderer.drawLine(new Vector2(x*distance, 0),new Vector2(x*distance, screenHeight()),color,1.0f);
		}
		
		for(int x=0;x<=screenHeight()/distance;x++)
		{
			Renderer.drawLine(new Vector2(0, x*distance),new Vector2(screenWidth(), x*distance),color,1.0f);
		}
	}

	@Override
	public void tick(int i, int j, int k) {
		for(int x=0;x<buttonList.size();x++)
		{
			buttonList.get(x).tick(i, j, k);
			if(buttonList.get(x).selectState == 2 || buttonList.get(x).selectState == 3)
			{
				if(Remote2D.hasMouseBeenPressed())
				{
					//if(buttonList.get(x).sound != 0)
					//	DefenseStep.getInstance().soundManager.quickPlay((buttonList.get(x).sound==1 ? "gui.select" : "gui.back"), 0, 0, false, false, false);
					actionPerformed(buttonList.get(x));
				}
			}
		}
	}
	
	public void actionPerformed(GuiButton button)
	{
		
	}
	
	/**
	 * Overrides the default stretch type, if needed.
	 * @return Overridden stretch type, or null if there is no override.  Default is null.
	 */
	public StretchType getOverrideStretchType()
	{
		return null;
	}
	
}
