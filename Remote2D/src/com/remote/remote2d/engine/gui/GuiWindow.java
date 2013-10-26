package com.remote.remote2d.engine.gui;

import java.util.ArrayList;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.ColliderBox;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;

public abstract class GuiWindow extends Gui {
	
	public Vector2 pos;
	public Vector2 dim;
	public ColliderBox allowedBounds;
	public String title;
	
	protected ArrayList<GuiButton> buttonList;
	protected WindowHolder holder;
	protected boolean isSelected;
	
	private Vector2 dragOffset = new Vector2(-1,-1);
	private Vector2 resizeOffset = new Vector2(-1,-1);
	private Vector2 oldPos;
	private boolean isDragging = false;
	private boolean isResizing = false;
	private boolean dontTick = false;
	private boolean hoverX = false;
	
	private final int windowTopColor = 0xff5555;
	private final int windowMainColor = 0xffbbbb;
	
	public GuiWindow(WindowHolder holder, Vector2 pos, Vector2 dim, ColliderBox allowedBounds, String title)
	{
		this.holder = holder;
		this.pos = pos;
		this.oldPos = pos.copy();
		this.dim = dim;
		this.title = title;
		this.allowedBounds = allowedBounds;
		this.isSelected = false;
		
		buttonList = new ArrayList<GuiButton>();
		initGui();
	}
	
	public void initGui()
	{
		
	}

	@Override
	public void tick(int i, int j, int k) {
		if(dontTick)
		{
			dontTick  = false;
			return;
		}
		
		oldPos = pos.copy();
		
		boolean buttonOverride = false;
		
		if(!pos.getColliderWithDim(dim.add(new Vector2(20,20))).isPointInside(new Vector2(i,j)) && Remote2D.hasMouseBeenPressed())
		{
			isSelected = false;
			buttonOverride = true;
		}
		if(!isSelected)
		{
			if(pos.getColliderWithDim(dim.add(new Vector2(0,20))).isPointInside(new Vector2(i,j)) && Remote2D.hasMouseBeenPressed())
			{
				holder.pushWindow(this);
				buttonOverride = true;
			}
			if(!isSelected)
			{
				for(int x=0;x<buttonList.size();x++)
				{
					if(!buttonList.get(x).getDisabled())
						buttonList.get(x).selectState = 1;
				}
				return;
			}
		}
		
		if(new Vector2(pos.x+dim.x-20,pos.y).getColliderWithDim(new Vector2(20)).isPointInside(new Vector2(i,j)) && !isDragging)
		{
			hoverX = true;
			if(k == 1)
				holder.closeWindow(this);
		} else
			hoverX = false;
		
		if(pos.getColliderWithDim(new Vector2(dim.x,20)).isPointInside(new Vector2(i,j)) && k == 1)
		{
			isDragging = true;
		} else if(k == 0)
			isDragging = false;
		
		if(pos.add(new Vector2(dim.x-20,dim.y)).getColliderWithDim(new Vector2(20)).isPointInside(new Vector2(i,j)) && k == 1)
		{
			isResizing = true;
		} else if(k == 0)
			isResizing = false;
		
		if(isDragging)
		{
			if(dragOffset.x == -1 || dragOffset.y == -1)
				dragOffset = new Vector2(i-pos.x,j-pos.y);
			else
			{
				float x = i-dragOffset.x;
				float y = j-dragOffset.y;
				if(y < allowedBounds.pos.y)
					y = allowedBounds.pos.y;
				
				pos = new Vector2(x,y);
			}
		} else
			dragOffset = new Vector2(-1,-1);
		
		if(canResize())
		{
			if(isResizing)
			{
				
				if(resizeOffset.x == -1 || resizeOffset.y == -1)
					resizeOffset = new Vector2(i-pos.x-dim.x+20,j-pos.y-dim.y+20);
				else
				{
					float x = i-pos.x+20-resizeOffset.x;
					float y = j-pos.y+20-resizeOffset.y;
					
					dim = new Vector2(x,y);
				}
			} else if(resizeOffset.x != -1 || resizeOffset.y != -1)
			{
				initGui();
				resizeOffset = new Vector2(-1,-1);
			}
		}
		
		for(int x=0;x<buttonList.size();x++)
		{
			buttonList.get(x).tick((int)getMouseInWindow(i,j).x, (int)getMouseInWindow(i,j).y, k);
			if(buttonList.get(x).selectState == 2 || buttonList.get(x).selectState == 3)
			{
				if(Remote2D.hasMouseBeenPressed() && !buttonOverride)
				{
					actionPerformed(buttonList.get(x));
				}
			}
		}
	}
	
	public abstract void renderContents(float interpolation);
	
	public Vector2 getMouseInWindow(int i, int j)
	{
		return new Vector2(i-pos.x,j-pos.y-20);
	}

	@Override
	public void render(float interpolation) {
		
		Vector2 pos = Interpolator.linearInterpolate(oldPos, this.pos, interpolation);
		
		Renderer.drawRect(pos, new Vector2(dim.x,20), isSelected ? windowTopColor : windowMainColor, 1.0f);
		Renderer.drawRect(pos.add(new Vector2(0,20)), dim, windowMainColor, 1.0f);
		if(hoverX)
			Renderer.drawRect(new Vector2(pos.x+dim.x-20,pos.y), new Vector2(20), 0x000000, 1);
		Renderer.drawLineRect(new Vector2(pos.x+dim.x-20,pos.y), new Vector2(20), 0x000000, 1);
		Renderer.drawLine(new Vector2(pos.x+dim.x-20,pos.y), new Vector2(pos.x+dim.x,pos.y+20), 0xffffff, 1);
		Renderer.drawLine(new Vector2(pos.x+dim.x,pos.y), new Vector2(pos.x+dim.x-20,pos.y+20), 0xffffff, 1);
		Fonts.get("Arial").drawString(title, pos.x+10, pos.y+1, 20, 0xffffff);
		Renderer.startScissor(new Vector2(pos.x,pos.y+20), dim);
		
		Renderer.pushMatrix();
			Renderer.translate(new Vector2(pos.x,pos.y+20));
			renderContents(interpolation);
			for(int x=0;x<buttonList.size();x++)
				buttonList.get(x).render(interpolation);
			Renderer.translate(new Vector2(-pos.x,-pos.y-20));
		Renderer.popMatrix();
		
		Renderer.endScissor();
		
		
		Renderer.drawLineRect(pos, dim.add(new Vector2(0,20)), 0x000000, 1.0f);
		Renderer.drawLine(new Vector2(pos.x, pos.y+20),new Vector2(pos.x+dim.x, pos.y+20),0.4f,0.4f,0.4f,1.0f);
		
		if(canResize())
		{
			Renderer.drawLine(new Vector2(pos.x+dim.x-15,pos.y+dim.y+20), new Vector2(pos.x+dim.x,pos.y+dim.y+5), 0x000000, 1);
			Renderer.drawLine(new Vector2(pos.x+dim.x-9,pos.y+dim.y+20), new Vector2(pos.x+dim.x,pos.y+dim.y+11), 0x000000, 1);
		}
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public GuiWindow setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		return this;
	}
	
	public void dontTick()
	{
		dontTick = true;
	}
	
	public void setPos(Vector2 pos)
	{
		this.pos = pos.copy();
		this.oldPos = pos.copy();
	}

	public void actionPerformed(GuiButton button)
	{
		
	}
	
	public boolean canClose()
	{
		return true;
	}
	
	public boolean canResize()
	{
		return true;
	}

}
