package com.remote.remote2d.gui.editor;

import org.lwjgl.input.Mouse;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.Remote2D;
import com.remote.remote2d.art.Fonts;
import com.remote.remote2d.art.Renderer;
import com.remote.remote2d.logic.Interpolator;
import com.remote.remote2d.logic.Vector2;

public class GuiEditorHeirarchySection {
	private Vector2 oldAnimPos;
	private Vector2 newAnimPos;
	private long animStartTime;
	private long animLength;
	private long lastClickEvent = -1;
	private Vector2 oldPos;
	public Vector2 pos;
	public Vector2 dim;
	
	public String content;
	public boolean selected = false;
	public GuiEditorHeirarchy heirarchy;
	
	
	public GuiEditorHeirarchySection(GuiEditorHeirarchy heirarchy, String content, Vector2 pos, Vector2 dim)
	{
		this.heirarchy = heirarchy;
		this.content = content;
		this.pos = pos;
		this.dim = dim;
	}
	

	public void tick(int i, int j, int k) {
		oldPos = pos.copy();
		if(oldAnimPos != null && newAnimPos != null && animStartTime != -1 && animLength != -1)
		{
			float currentTime = System.currentTimeMillis()-animStartTime;
			currentTime /= animLength;
			pos = Interpolator.linearInterpolate(oldAnimPos, newAnimPos, currentTime);
		}
		
		long time = System.currentTimeMillis();
		if(Remote2D.getInstance().hasMouseBeenPressed())
		{
			if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)))
			{
				heirarchy.setAllUnselected();
				selected = true;
				heirarchy.updateSelected();
				
				if(lastClickEvent != -1 && time-lastClickEvent <= 500)
				{
					//TODO: Enable entity focusing on double click
					lastClickEvent = -1;
				} else
				{
					lastClickEvent = time;
				}
			} else
			{
				lastClickEvent = -1;
			}
		} else if(Mouse.isButtonDown(0) && pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)) && heirarchy.getEditor().dragObject == null)
		{
			String uuid = heirarchy.getEntityForSec(this).uuid();
			heirarchy.getEditor().dragObject = new DraggableObjectEntity(heirarchy.getEditor(),content,uuid,pos,dim,new Vector2(i,j).subtract(pos));
		}
	}

	public void render(float interpolation) {
		Vector2 truePos = Interpolator.linearInterpolate(oldPos, pos, interpolation);
		if(selected)
			Renderer.drawRect(truePos, dim, 0xffffff, 0.5f);
		Fonts.get("Arial").drawString(content, truePos.x, truePos.y, 20, 0xffffff);
	}
	
	public boolean isAnimating()
	{
		return System.currentTimeMillis() - animStartTime <= animLength;
	}
	
	public void setKeyframe(Vector2 newPos, long length)
	{
		oldAnimPos = this.pos.copy();
		newAnimPos = newPos;
		animStartTime = System.currentTimeMillis();
		animLength = length;
	}

}
