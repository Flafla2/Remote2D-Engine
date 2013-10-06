package com.remote.remote2d.editor.inspector;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.editor.DraggableObject;
import com.remote.remote2d.editor.DraggableObjectEntity;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.logic.Vector2;

public class GuiEditorInspectorSectionEntity extends GuiEditorInspectorSection {

	Entity entity = null;
	private boolean changed = false;
	
	public GuiEditorInspectorSectionEntity(String name, GuiEditor inspector, Vector2 pos, int width) {
		super(name, inspector, pos, width);
	}

	@Override
	public int sectionHeight() {
		return 20;
	}

	@Override
	public Object getData() {
		return entity;
	}

	@Override
	public void initSection() {
		
	}

	@Override
	public void tick(int i, int j, int k) {
		
	}

	@Override
	public void render(float interpolation) {
		Fonts.get("Arial").drawString(renderName, pos.x, pos.y, 20, isComplete() ? 0xffffff : 0xff7777);
		final int outerRad = 9;
		final Vector2 circleCenter = new Vector2(width-outerRad-10,sectionHeight()/2);
		if(isComplete())
			Renderer.drawCircleOpaque(pos.add(circleCenter), outerRad, 9, 0xffffff, 1);
		else
			Renderer.drawCircleHollow(pos.add(circleCenter), outerRad, 9, 0xffffff, 1);
	}
	
	@Override
	public void setData(Object o) {
		if(o instanceof Entity)
		{
			entity = (Entity)o;
		}
	}
	
	@Override
	public void deselect() {
		
	}
	
	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public boolean isComplete() {
		return entity != null;
	}
	
	@Override
	public boolean hasFieldBeenChanged() {
		boolean c = changed;
		changed = false;
		return c;
	}
	
	public boolean acceptsDraggableObject(DraggableObject object)
	{
		if(object instanceof DraggableObjectEntity)
		{
			DraggableObjectEntity fileobj = ((DraggableObjectEntity)object);
			if(fileobj.uuid != null)
			{
				if(!fileobj.uuid.trim().equals(""))
					return true;
			}
		}
		return false;
	}
	
	public void acceptDraggableObject(DraggableObject object)
	{
		if(object instanceof DraggableObjectEntity)
		{
			DraggableObjectEntity fileobj = ((DraggableObjectEntity)object);
			if(fileobj.uuid != null)
			{
				if(!fileobj.uuid.trim().equals(""))
				{
					setData(inspector.getMap().getEntityList().getEntityWithUUID(fileobj.uuid));
					changed = true;
				}
			}
		}
	}

}

