package com.remote.remote2d.editor.inspector;

import java.awt.Color;

import com.remote.remote2d.editor.DraggableObject;
import com.remote.remote2d.editor.GuiEditor;
import com.remote.remote2d.engine.Remote2D;
import com.remote.remote2d.engine.art.Animation;
import com.remote.remote2d.engine.art.Fonts;
import com.remote.remote2d.engine.art.Renderer;
import com.remote.remote2d.engine.art.Texture;
import com.remote.remote2d.engine.entity.Entity;
import com.remote.remote2d.engine.logic.Vector2;

public abstract class GuiEditorInspectorSectionSet extends GuiEditorInspectorSection {
	
	protected GuiEditorInspectorSection[] set;
	private int height;
	private int dragObject = -1;
	
	public GuiEditorInspectorSectionSet(String name, GuiEditor inspector, Vector2 pos, int width, String[] names, Class<?>[] objects) {
		super(name, inspector, pos, width);
		
		set = new GuiEditorInspectorSection[objects.length];
		int currentY = 20;
		for(int x=0;x<set.length;x++)
		{
			if(objects[x] == Animation.class)
				set[x] = new GuiEditorInspectorSectionAnimation(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Boolean.class)
				set[x] = new GuiEditorInspectorSectionBoolean(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Color.class)
				set[x] = new GuiEditorInspectorSectionColor(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Entity.class)
				set[x] = new GuiEditorInspectorSectionEntity(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Float.class)
				set[x] = new GuiEditorInspectorSectionFloat(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Integer.class)
				set[x] = new GuiEditorInspectorSectionInt(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Texture.class)
				set[x] = new GuiEditorInspectorSectionTexture(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == String.class)
				set[x] = new GuiEditorInspectorSectionString(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			if(objects[x] == Vector2.class)
				set[x] = new GuiEditorInspectorSectionVec2D(names[x], inspector, new Vector2(pos.x,pos.y+currentY), width);
			currentY += set[x].sectionHeight();
		}
		height = currentY;
	}

	@Override
	public int sectionHeight() {
		return height;
	}

	@Override
	public void initSection() {
		for(GuiEditorInspectorSection sec : set)
			sec.initSection();
	}

	@Override
	public void deselect() {
		for(GuiEditorInspectorSection sec : set)
			sec.deselect();
	}

	@Override
	public boolean isSelected() {
		for(GuiEditorInspectorSection sec : set)
			if(sec.isSelected())
				return true;
		return false;
	}

	@Override
	public boolean isComplete() {
		for(GuiEditorInspectorSection sec : set)
			if(!sec.isComplete())
				return false;
		return true;
	}
	
	public boolean isComplete(String name)
	{
		for(GuiEditorInspectorSection sec : set)
			if(sec.name.equals(name))
				return sec.isComplete();
		return false;
	}

	@Override
	public boolean hasFieldBeenChanged() {
		for(GuiEditorInspectorSection sec : set)
			if(sec.hasFieldBeenChanged())
				return true;
		return false;
	}

	@Override
	public void tick(int i, int j, int k) {
		for(GuiEditorInspectorSection sec : set)
			sec.tick(i, j, k);
	}

	@Override
	public void render(float interpolation) {
		Fonts.get("Arial").drawString(renderName, pos.x, pos.y, 20, isComplete() ? 0xffffff : 0xff7777);
		for(GuiEditorInspectorSection sec : set)
			sec.render(interpolation);
		
		int[] mouse = Remote2D.getMouseCoords();
		Vector2 mouseVec = new Vector2(mouse[0],mouse[1]).add(new Vector2(0,editor.getInspector().offset));
		
		if(dragObject != -1)
		{
			Vector2 secDim = new Vector2(width,set[dragObject].sectionHeight());
			boolean inside = set[dragObject].pos.getColliderWithDim(secDim).isPointInside(mouseVec);
			if(inside)
				Renderer.drawRect(set[dragObject].pos, new Vector2(width,set[dragObject].sectionHeight()), 0xffffff, 0.5f);
		}
	}
	
	public Object getDataWithName(String name)
	{
		for(GuiEditorInspectorSection sec : set)
			if(sec.name.equals(name))
				return sec.getData();
		return null;
	}
	
	public void setDataWithName(String name,Object o)
	{
		for(GuiEditorInspectorSection sec : set)
			if(sec.name.equals(name))
				sec.setData(o);
	}
	
	@Override
	public boolean acceptsDraggableObject(DraggableObject object)
	{
		for(int x=0;x<set.length;x++)
		{
			if(set[x].acceptsDraggableObject(object))
			{
				dragObject = x;
				return true;
			}
		}
		dragObject = -1;
		return false;
	}
	
	@Override
	public void acceptDraggableObject(DraggableObject object)
	{
		for(int x=0;x<set.length;x++)
		{
			if(set[x].acceptsDraggableObject(object))
				set[x].acceptDraggableObject(object);
		}
	}

}
