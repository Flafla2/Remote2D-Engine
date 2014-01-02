package com.remote.remote2d.engine.gui;

import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.ResourceLoader;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiButton extends Gui {
	
	/**
	 * The ID number of the button.  This is used in GuiMenu's actionPerformed(GuiButton)
	 * to identify the button. 
	 */
	public int id;
	/**
	 * The state of the button.  0 = disabled, 1 = idle, and 2/3 = selected
	 */
	int selectState = 1;
	int selectColor = 2;
	Vector2 pos;
	Vector2 dim;
	public String text;
	Texture tex;
	
	/**
	 * A basic button class.  Click it, and something happens.  Buttons are built into
	 * the GUI class with a hook structure.
	 */
	public GuiButton(int id, Vector2 pos, Vector2 dim, String text)
	{
		this.id = id;
		this.pos = pos;
		this.dim = dim;
		this.text = text;
		
		tex = ResourceLoader.getTexture("res/gui/controls.png");
	}
	
	@Override
	public void render(float interpolation)
	{
		renderControlElement(tex,pos,dim,selectState,0);
		
		int[] texdim = Fonts.get("Arial").getStringDim(text, 20);
		Fonts.get("Arial").drawString(text, pos.x+dim.x/2-texdim[0]/2, pos.y+dim.y/2-texdim[1]/2, 20, 0x000000);
	}
	
	public GuiButton setDisabled(boolean disabled)
	{
		if(disabled == (selectState==0))
			return this;
		if(disabled)
			selectState = 0;
		else
			selectState = 1;
		
		return this;
	}
	
	public boolean getDisabled()
	{
		return selectState == 0;
	}
	
	public void renderControlElement(Texture tex, Vector2 pos, Vector2 dim, int color, int size)
	{
		Vector2[] coords = getControlImageCoords(color,size);
		
		Renderer.drawRect(
				pos,
				new Vector2(5,dim.y),
				coords[0],
				coords[1],
				tex,
				0xffffff,
				1.0f);
		Renderer.drawRect(
				new Vector2(pos.x+5,pos.y),
				new Vector2(dim.x-10,dim.y),
				coords[2],
				coords[3],
				tex,
				0xffffff,
				1.0f);
		Renderer.drawRect(
				new Vector2(pos.x+dim.x-5,pos.y),
				new Vector2(5,dim.y),
				coords[4],
				coords[5],
				tex,
				0xffffff,
				1.0f);
	}
	
	public Vector2[] getControlImageCoords(int color, int size)
	{
		Vector2[] coords = new Vector2[6];
		
		int posX = color*40;
		int posY = 0;
		
		if(size != 0)
		{
			posX += 20;
			if(size == 2)
				posY = 20;
		}
		
		int dimY = 40;
		if(size == 0)
			dimY = 40;
		else if(size == 1)
			dimY = 20;
		else if(size == 2)
			dimY = 10;
		
		coords[0] = new Vector2(posX,posY);
		coords[1] = new Vector2(5,dimY);
		
		coords[2] = new Vector2(posX+5,posY);
		coords[3] = new Vector2(10,dimY);
		
		coords[4] = new Vector2(posX+15,posY);
		coords[5] = new Vector2(5,dimY);
		
		for(Vector2 vec : coords)
		{
			vec.x /= tex.getImage().getWidth();
			vec.y /= tex.getImage().getHeight();
		}
		
		return coords;
	}
	
	@Override
	public void tick(int i, int j, int k)
	{
		if(i > pos.x && j > pos.y && i < pos.x+dim.x && j < pos.y+dim.y && selectState != 0)
		{
			selectState = selectColor;
		} else if(selectState != 0)
		{
			selectState = 1;
		}
	}

}
