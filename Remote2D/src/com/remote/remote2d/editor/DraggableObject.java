package com.remote.remote2d.editor;

import org.lwjgl.input.Mouse;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.gui.Gui;
import com.remote.remote2d.engine.logic.Interpolator;
import com.remote.remote2d.engine.logic.Vector2;

public class DraggableObject extends Gui {
	
	public String name;
	private Vector2 oldPos;
	public Vector2 pos;
	public Vector2 origPos;
	public Vector2 dim;
	private boolean hasShown = false; // If it has been dragged past a certian threshold, usually 20 pixels.
	private GuiEditor editor;
	private long dragTime = 0;
	private long letGoTime = 0;
	private Vector2 interpolatePos;
	private Vector2 mouseOffset = null;
	private boolean letgo = false;
	private boolean shouldDelete = false;

	public DraggableObject(GuiEditor editor, String name, Vector2 pos, Vector2 dim, Vector2 mouseOffset) {
		this.pos = pos.copy();
		this.editor = editor;
		this.origPos = pos.copy();
		this.oldPos = pos.copy();
		this.name = name;
		this.dim = dim;
		this.mouseOffset = mouseOffset;
	}

	@Override
	public void tick(int i, int j, int k) {
		oldPos = pos.copy();
		if(k != 1 && !letgo)
		{
			letGoTime = System.currentTimeMillis();
			interpolatePos = pos.copy();
			letgo = true;
			
			if(editor.getInspector().recieveDraggableObject(this))
			{
				shouldDelete = true;
				return;
			} else if(editor.getHeirarchy().recieveDraggableObject(this))
			{
				shouldDelete = true;
				return;
			}
		} else if(!letgo)
		{
			pos = new Vector2(i,j).subtract(mouseOffset);
			
			Vector2 vec = pos.subtract(origPos);//combined vector of old->new pos
			float length = (float) Math.sqrt(vec.x*vec.x+vec.y*vec.y);
			if(length > 20)
				hasShown = true;
		} else
		{
			long timesinceletgo = System.currentTimeMillis()-letGoTime;
			if(timesinceletgo > 200)
				shouldDelete = true;
			float time = (float)Math.min(200, timesinceletgo)/200f;
			pos = Interpolator.linearInterpolate(interpolatePos, origPos, time);
		}
	}
	
	public boolean shouldDelete()
	{
		return shouldDelete;
	}

	@Override
	public void render(float interpolation) {
		Vector2 pos = Interpolator.linearInterpolate(oldPos, this.pos, interpolation);
		
		if(hasShown)
		{
			//Renderer.drawRect(pos, dim, 0x000000, 0.4f);
			Fonts.get("Arial").drawString(name, pos.x+11, pos.y+1, 20, 0x444444);
			Fonts.get("Arial").drawString(name, pos.x+10, pos.y, 20, 0xffffff);
		}
	}
	
	public void setAbsolutePos(Vector2 pos)
	{
		this.pos = pos.copy();
		this.oldPos = pos.copy();
	}

}
