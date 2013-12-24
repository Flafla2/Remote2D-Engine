package com.remote.remote2d.editor;

import org.lwjgl.input.Mouse;

import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorHeirarchySection {
	private long lastClickEvent = -1;
	private Vector2 oldPos;
	public Vector2 pos;
	public Vector2 dim;
	
	public String content;
	public boolean selected = false;
	public GuiEditorHeirarchy heirarchy;
	public String uuid;
	
	
	public GuiEditorHeirarchySection(GuiEditorHeirarchy heirarchy, String content, String uuid, Vector2 pos, Vector2 dim)
	{
		this.heirarchy = heirarchy;
		this.content = content;
		this.pos = pos;
		this.dim = dim;
		this.uuid = uuid;
	}
	

	public void tick(int i, int j, int k) {
		oldPos = pos.copy();
		
		long time = System.currentTimeMillis();
		if(Remote2D.hasMouseBeenPressed())
		{
			if(heirarchy.pos.getColliderWithDim(heirarchy.dim).isPointInside(new Vector2(i,j)))
			{
				if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)))
				{	
					if(lastClickEvent != -1 && time-lastClickEvent <= 500)
					{
						//TODO: Enable entity focusing on double click
						lastClickEvent = -1;
					} else
					{
						lastClickEvent = time;
					}
				} else
					lastClickEvent = -1;
			}
		} else if(Mouse.isButtonDown(0) && pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)) && heirarchy.getEditor().dragObject == null)
		{
			Vector2 pos = this.pos.subtract(new Vector2(0,heirarchy.offset));
			String uuid = heirarchy.getEntityForSec(this).getUUID();
			heirarchy.getEditor().dragObject = new DraggableObjectEntity(heirarchy.getEditor(),content,uuid,pos,dim,new Vector2(i,j).subtract(this.pos));
		} else if(pos.getColliderWithDim(dim).isPointInside(new Vector2(i,j)) && Remote2D.hasMouseBeenReleased())
		{
			heirarchy.setAllUnselected();
			selected = true;
			heirarchy.updateSelected();
			
			heirarchy.getEditor().getBrowser().setAllUnselected();
		}
	}

	public void render(float interpolation) {
		Vector2 truePos = Interpolator.linearInterpolate(oldPos, pos, interpolation);
		if(selected)
			Renderer.drawRect(truePos, dim, 0xffffff, 0.5f);
		Fonts.get("Arial").drawString(content, truePos.x, truePos.y, 20, 0xffffff);
	}

}
